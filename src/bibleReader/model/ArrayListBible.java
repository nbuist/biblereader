package bibleReader.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * A class that stores a version of the Bible.
 * 
 * @author Chuck Cusack (Provided the interface). Modified February 9, 2015.
 * @author noahbuist
 */
public class ArrayListBible implements Bible {

	// The Fields
	// Add necessary field(s) here.
	private LinkedHashMap<Reference, Verse> verseMap; // O(1) lookup
	private HashMap<BookOfBible, Integer> lastChapterCache;
	private HashMap<String, Integer> lastVerseCache; // key: "BOOK-chapter"
	private String version;
	private String title;

	/**
	 * Create a new Bible with the given verses.
	 * 
	 * @param verses All of the verses of this version of the Bible.
	 */
	public ArrayListBible(VerseList verses) {
		this.version = verses.getVersion();
        this.title = verses.getDescription();
        this.verseMap = new LinkedHashMap<>();
        this.lastChapterCache = new HashMap<>();
        this.lastVerseCache = new HashMap<>();

        for (Verse v : verses) {
            Reference r = v.getReference();
            verseMap.put(r, v);

            BookOfBible book = r.getBookOfBible();
            lastChapterCache.merge(book, r.getChapter(), Math::max);
            lastVerseCache.merge(book + "-" + r.getChapter(), r.getVerse(), Math::max);
        }
	}

	@Override
	public int getNumberOfVerses() {
		// TODO Implement me: Stage 2
		return verseMap.size();
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public boolean isValid(Reference ref) {
		return verseMap.containsKey(ref);
	}

	@Override
	public String getVerseText(Reference r) {
		Verse v = verseMap.get(r);
	    if (v != null) {
	        return v.getText();
	    } else {
	        return null;
	    }
	}

	@Override
	public Verse getVerse(Reference r) {
		return verseMap.get(r);
	}

	@Override
	public Verse getVerse(BookOfBible book, int chapter, int verse) {
		return verseMap.get(new Reference(book, chapter, verse));
	}

	// ---------------------------------------------------------------------------------------------
	// The following part of this class should be implemented for stage 4.
	// See the Bible interface for the documentation of these methods.
	// Do not over think these methods. All three should be pretty
	// straightforward to implement.
	// For Stage 8 (give or take a 1 or 2) you will re-implement them so they
	// work better.
	// At that stage you will create another class to facilitate searching and
	// use it here.
	// (Essentially these two methods will be delegate methods.)
	// ---------------------------------------------------------------------------------------------

	@Override
	public VerseList getAllVerses() {
		return new VerseList(version, title, new ArrayList<>(verseMap.values()));
	}

	@Override
	public VerseList getVersesContaining(String phrase) {
		VerseList result = new VerseList(version, title);
		if (phrase == null || phrase.isEmpty()) {
			return result;
		}
		for (Verse v : verseMap.values()) {
			if (v.getText().toLowerCase().contains(phrase.toLowerCase())) {
				result.add(v);
			}
		}
		return result;
	}

	@Override
	public ArrayList<Reference> getReferencesContaining(String phrase) {
		ArrayList<Reference> result = new ArrayList<>();
	    if (phrase == null || phrase.isEmpty()) {
	        return result;
	    }
	    String lowerPhrase = phrase.toLowerCase(); // compute once
	    for (Verse v : verseMap.values()) {
	        if (v.getText().toLowerCase().contains(lowerPhrase)) {
	            result.add(v.getReference());
	        }
	    }
	    return result;
	}

	@Override
	public VerseList getVerses(ArrayList<Reference> references) {
		VerseList result = new VerseList(version, "Arbitrary list of Verses");
		for (Reference r : references) {
			result.add(verseMap.get(r));
		}
		return result;
	}
	// ---------------------------------------------------------------------------------------------
	// The following part of this class should be implemented for Stage 7.
	//
	// HINT: Do not reinvent the wheel. Some of these methods can be implemented
	// by looking up
	// one or two things and calling another method to do the bulk of the work.
	// ---------------------------------------------------------------------------------------------

	@Override
	public int getLastVerseNumber(BookOfBible book, int chapter) {
		return lastVerseCache.getOrDefault(book + "-" + chapter, -1);
	}

	@Override
	public int getLastChapterNumber(BookOfBible book) {
		return lastVerseCache.getOrDefault(book, -1);
	}

	@Override
	public ArrayList<Reference> getReferencesInclusive(Reference firstVerse, Reference lastVerse) {
		ArrayList<Reference> result = new ArrayList<>();
	    if (firstVerse == null || lastVerse == null || firstVerse.compareTo(lastVerse) > 0) {
	        return result;
	    }
	    for (Reference r : verseMap.keySet()) {
	        if (r.compareTo(firstVerse) >= 0 && r.compareTo(lastVerse) <= 0) {
	            result.add(r);
	        }
	    }
	    return result;
	}

	@Override
	public ArrayList<Reference> getReferencesExclusive(Reference firstVerse, Reference lastVerse) {
		ArrayList<Reference> result = new ArrayList<>();
	    if (firstVerse == null || lastVerse == null || firstVerse.compareTo(lastVerse) > 0) {
	        return result;
	    }
	    for (Reference r : verseMap.keySet()) {
	        if (r.compareTo(firstVerse) >= 0 && r.compareTo(lastVerse) < 0) {
	            result.add(r);
	        }
	    }
	    return result;
	}

	@Override
	public ArrayList<Reference> getReferencesForBook(BookOfBible book) {
		int lastChapter = getLastChapterNumber(book);
		int lastVerse = getLastVerseNumber(book, lastChapter);
		System.out.println("book=" + book + " lastChapter=" + lastChapter + " lastVerse=" + lastVerse);
		return getReferencesInclusive(new Reference(book, 1, 1), new Reference(book, lastChapter, lastVerse));
	}

	@Override
	public ArrayList<Reference> getReferencesForChapter(BookOfBible book, int chapter) {
		return getReferencesInclusive(new Reference(book, chapter, 1),
				new Reference(book, chapter, getLastVerseNumber(book, chapter)));
	}

	@Override
	public ArrayList<Reference> getReferencesForChapters(BookOfBible book, int chapter1, int chapter2) {
		return getReferencesInclusive(new Reference(book, chapter1, 1),
				new Reference(book, chapter2, getLastVerseNumber(book, chapter2)));
	}

	@Override
	public ArrayList<Reference> getReferencesForPassage(BookOfBible book, int chapter, int verse1, int verse2) {
		return getReferencesInclusive(new Reference(book, chapter, verse1), new Reference(book, chapter, verse2));

	}

	@Override
	public ArrayList<Reference> getReferencesForPassage(BookOfBible book, int chapter1, int verse1, int chapter2,
			int verse2) {
		Reference first = new Reference(book, chapter1, verse1);
		Reference second = new Reference(book, chapter2, verse2);
		return getReferencesInclusive(first, second);
	}

	@Override
	public VerseList getVersesInclusive(Reference firstVerse, Reference lastVerse) {
		return getVerses(getReferencesInclusive(firstVerse, lastVerse));
	}

	@Override
	public VerseList getVersesExclusive(Reference firstVerse, Reference lastVerse) {
		return getVerses(getReferencesExclusive(firstVerse, lastVerse));
	}

	@Override
	public VerseList getBook(BookOfBible book) {
		return getVerses(getReferencesForBook(book));
	}

	@Override
	public VerseList getChapter(BookOfBible book, int chapter) {
		return getVerses(getReferencesForChapter(book, chapter));
	}

	@Override
	public VerseList getChapters(BookOfBible book, int chapter1, int chapter2) {
		return getVerses(getReferencesForChapters(book, chapter1, chapter2));
	}

	@Override
	public VerseList getPassage(BookOfBible book, int chapter, int verse1, int verse2) {
		return getVerses(getReferencesForPassage(book, chapter, verse1, verse2));
	}

	@Override
	public VerseList getPassage(BookOfBible book, int chapter1, int verse1, int chapter2, int verse2) {
		return getVerses(getReferencesForPassage(book, chapter1, verse1, chapter2, verse2));
	}
}