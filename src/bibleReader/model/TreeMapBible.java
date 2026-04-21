package bibleReader.model;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A class that stores a version of the Bible.
 * 
 * @author Chuck Cusack (Provided the interface)
 * @author ? (provided the implementation)
 */
public class TreeMapBible implements Bible {

	// The Fields
	private String						version;
	private TreeMap<Reference, String>	theVerses;
	private String						description;

	// Or replace the above with:
	// private TreeMap<Reference, Verse> theVerses;
	// Add more fields as necessary.

	/**
	 * Create a new Bible with the given verses.
	 * 
	 * @param version the version of the Bible (e.g. ESV, KJV, ASV, NIV).
	 * @param verses All of the verses of this version of the Bible.
	 */
	public TreeMapBible(VerseList verses) {
		this.version = verses.getVersion();
		this.description = verses.getDescription();
        this.theVerses = new TreeMap<>();
        for (Verse v : verses) {
            theVerses.put(v.getReference(), v.getText());
        }
	}

	@Override
	public int getNumberOfVerses() {
		// TODO Implement me: Stage 11
		return theVerses.size();
	}

	@Override
	public VerseList getAllVerses() {
		VerseList all = new VerseList(version, description);
        for (Map.Entry<Reference, String> e : theVerses.entrySet()) {
            all.add(new Verse(e.getKey(), e.getValue()));
        }
        return all;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public String getTitle() {
		return description;
	}

	@Override
	public boolean isValid(Reference ref) {
		return theVerses.containsKey(ref);
	}

	@Override
	public String getVerseText(Reference r) {
		return theVerses.get(r);
	}

	@Override
	public Verse getVerse(Reference r) {
		String text = theVerses.get(r);
        if (text != null) {
            return new Verse(r, text);
        }
        return null;
	}

	@Override
	public Verse getVerse(BookOfBible book, int chapter, int verse) {
		return getVerse(new Reference(book, chapter, verse));
	}

	// ---------------------------------------------------------------------------------------------
	// The following part of this class should be implemented for stage 4.
	//
	// For Stage 11 the first two methods below will be implemented as specified in the comments.
	// Do not over think these methods. All three should be pretty straightforward to implement.
	// For Stage 8 (give or take a 1 or 2) you will re-implement them so they work better.
	// At that stage you will create another class to facilitate searching and use it here.
	// (Essentially these two methods will be delegate methods.)
	// ---------------------------------------------------------------------------------------------

	@Override
	public VerseList getVersesContaining(String phrase) {
		// TODO Implement me: Stage 11
		VerseList results = new VerseList(version, description);
		if(phrase.isEmpty()) {
			return results;
		}
		String lowerPhrase = phrase.toLowerCase();
		for (Map.Entry<Reference, String> e : theVerses.entrySet()) {
            if (e.getValue().toLowerCase().contains(lowerPhrase)) {
                results.add(new Verse(e.getKey(), e.getValue()));
            }
        }
		return results;
	}

	@Override
	public ArrayList<Reference> getReferencesContaining(String phrase) {
		// TODO Implement me: Stage 11
		ArrayList<Reference> results = new ArrayList<>();
		if(phrase.isEmpty()) {
			return results;
		}
		String lowerPhrase = phrase.toLowerCase();
		for(Map.Entry<Reference, String> e : theVerses.entrySet()) {
			if(e.getValue().toLowerCase().contains(lowerPhrase)) {
				results.add(e.getKey());
			}
		}
		return results;
	}

	@Override
	public VerseList getVerses(ArrayList<Reference> references) {
		VerseList results = new VerseList(version, "Arbitrary list of Verses");
        for (Reference r : references) {
            results.add(getVerse(r));
        }
        return results;
	}

	// ---------------------------------------------------------------------------------------------
	// The following part of this class should be implemented for Stage 11.
	//
	// HINT: Do not reinvent the wheel. Some of these methods can be implemented by looking up
	// one or two things and calling another method to do the bulk of the work.
	// ---------------------------------------------------------------------------------------------

	@Override
	public int getLastVerseNumber(BookOfBible book, int chapter) {
		Reference start = new Reference(book, chapter, 1);
        Reference end   = new Reference(book, chapter + 1, 1);
        SortedMap<Reference, String> chapterMap = theVerses.subMap(start, end);
        if (chapterMap.isEmpty()) {
            return -1;
        }
        return chapterMap.lastKey().getVerse();
	}

	@Override
	public int getLastChapterNumber(BookOfBible book) {
		ArrayList<Reference> refs = getReferencesForBook(book);
		if(refs.isEmpty()){
			return -1;
		}
		return refs.get(refs.size() - 1).getChapter();
	}

	@Override
	public ArrayList<Reference> getReferencesInclusive(Reference firstVerse, Reference lastVerse) {
		if (firstVerse.compareTo(lastVerse) > 0) {
            return new ArrayList<>();
        }
        return new ArrayList<>(theVerses.subMap(firstVerse, true, lastVerse, true).keySet());
	}

	@Override
	public ArrayList<Reference> getReferencesExclusive(Reference firstVerse, Reference lastVerse) {
		if(firstVerse.compareTo(lastVerse) > 0) {
			return new ArrayList<>();
		}
		return new ArrayList<>(theVerses.subMap(firstVerse, lastVerse).keySet());
	}

	@Override
	public ArrayList<Reference> getReferencesForBook(BookOfBible book) {
		if(book == null) {
			return new ArrayList<>();
		}
		Reference start = new Reference(book, 1, 1);
		Reference end = getFirstReferenceOfNextBook(book);
		
		if (end != null) {
            return new ArrayList<>(theVerses.subMap(start, end).keySet());
        }
        return new ArrayList<>(theVerses.tailMap(start).keySet());
	}

	@Override
	public ArrayList<Reference> getReferencesForChapter(BookOfBible book, int chapter) {
		Reference start = new Reference(book, chapter, 1);
		Reference end = new Reference(book, chapter + 1, 1);
		
		return new ArrayList<Reference>(theVerses.subMap(start, end).keySet());
	}

	@Override
	public ArrayList<Reference> getReferencesForChapters(BookOfBible book, int chapter1, int chapter2) {
		if(chapter1 > chapter2) {
			return new ArrayList<>();
		}
		Reference start = new Reference(book, chapter1, 1);
		Reference end = new Reference(book, chapter2 + 1, 1);
		
		return new ArrayList<Reference>(theVerses.subMap(start, end).keySet());
	}

	@Override
	public ArrayList<Reference> getReferencesForPassage(BookOfBible book, int chapter, int verse1, int verse2) {
		if (verse1 > verse2) {
			return new ArrayList<>();
		}
		Reference start = new Reference(book, chapter, verse1);
		Reference end = new Reference(book, chapter, verse2);
		return getReferencesExclusive(start, end);
	}

	@Override
	public ArrayList<Reference> getReferencesForPassage(BookOfBible book, int chapter1, int verse1, int chapter2, int verse2) {
		Reference start = new Reference(book, chapter1, verse1);
		Reference end = new Reference(book, chapter2, verse2);
	
		return getReferencesInclusive(start, end);
	}

	@Override
	public VerseList getVersesInclusive(Reference firstVerse, Reference lastVerse) {
		VerseList result = new VerseList(getVersion(), firstVerse + "-" + lastVerse);
		if(firstVerse.compareTo(lastVerse) > 0) {
			return result;
		}
		SortedMap<Reference, String> sub = theVerses.subMap(firstVerse, true, lastVerse, true);
        for (Map.Entry<Reference, String> e : sub.entrySet()) {
            result.add(new Verse(e.getKey(), e.getValue()));
        }
        return result;
	}

	@Override
	public VerseList getVersesExclusive(Reference firstVerse, Reference lastVerse) {
		// Implementation of this method provided by Chuck Cusack.
		// This is provided so you have an example to help you get started
		// with the other methods.

		// We will store the resulting verses here. We copy the version from
		// this Bible and set the description to be the passage that was searched for.
		VerseList someVerses = new VerseList(getVersion(), firstVerse + "-" + lastVerse);

		// Make sure the references are in the correct order. If not, return an empty list.
		if (firstVerse.compareTo(lastVerse) > 0) {
			return someVerses;
		}
		// Return the portion of the TreeMap that contains the verses between
		// the first and the last, not including the last.
		SortedMap<Reference, String> s = theVerses.subMap(firstVerse, lastVerse);

		// Get the entries from the map so we can iterate through them.
		Set<Map.Entry<Reference, String>> mySet = s.entrySet();

		// Iterate through the set and put the verses in the VerseList.
		for (Map.Entry<Reference, String> element : mySet) {
			Verse aVerse = new Verse(element.getKey(), element.getValue());
			someVerses.add(aVerse);
		}
		return someVerses;
	}

	@Override
	public VerseList getBook(BookOfBible book) {
		if(book == null) {
			return new VerseList(version, "");
		}
		VerseList result = new VerseList(version, book.toString());
		for(Reference r : getReferencesForBook(book)) {
			result.add(new Verse(r, theVerses.get(r)));
		}
		return result;
	}

	@Override
	public VerseList getChapter(BookOfBible book, int chapter) {
		VerseList result = new VerseList(version, book + " " + chapter);
		for(Reference r : getReferencesForChapter(book, chapter)) {
			result.add(new Verse(r, theVerses.get(r)));
		}
		return result;
	}

	@Override
	public VerseList getChapters(BookOfBible book, int chapter1, int chapter2) {
		VerseList result = new VerseList(version, book + " " + chapter1 +"-" + chapter2);
		for(Reference r : getReferencesForChapters(book, chapter1, chapter2)) {
			result.add(new Verse(r, theVerses.get(r)));
		}
		
		return result;
	}

	@Override
	public VerseList getPassage(BookOfBible book, int chapter, int verse1, int verse2) {
		VerseList result = new VerseList(version, book + " " + chapter + ":" + verse1 + "-" + verse2);
		for(Reference r : getReferencesForPassage(book, chapter, verse1, verse2)){
			result.add(new Verse(r, theVerses.get(r)));
		}
		
		return result;
	}

	@Override
	public VerseList getPassage(BookOfBible book, int chapter1, int verse1, int chapter2, int verse2) {
		VerseList result = new VerseList(version, book + " " + chapter1 + ":" + verse1 + "-" + chapter2 + ":" + verse2);
		for(Reference r : getReferencesForPassage(book, chapter1, verse1, chapter2, verse2)) {
			result.add(new Verse(r, theVerses.get(r)));
		}
		return result;
	}
	//helper method
	private Reference getFirstReferenceOfNextBook(BookOfBible book) {
	    BookOfBible[] books = BookOfBible.values();
	    for (int i = 0; i < books.length - 1; i++) {
	        if (books[i] == book) {
	            return new Reference(books[i + 1], 1, 1);
	        }
	    }
	    return null;
	}

}
