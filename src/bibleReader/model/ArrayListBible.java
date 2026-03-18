package bibleReader.model;

import java.util.ArrayList;

/**
 * A class that stores a version of the Bible.
 * 
 * @author Chuck Cusack (Provided the interface). Modified February 9, 2015.
 * @author ? (provided the implementation)
 */
public class ArrayListBible implements Bible {

	// The Fields
	// Add necessary field(s) here.\
	private ArrayList<Verse> verses;
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
		this.verses = verses.copyVerses();

	}

	@Override
	public int getNumberOfVerses() {
		// TODO Implement me: Stage 2
		return verses.size();
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
		for (Verse v : verses) {
			if (v.getReference().equals(ref)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getVerseText(Reference r) {
		Verse v = getVerse(r);
		if (v != null) {
			return v.getText();
		} else {
			return null;
		}
	}

	@Override
	public Verse getVerse(Reference r) {
		for (Verse v : verses) {
			if (v.getReference().equals(r)) {
				return v;
			}
		}
		return null;
	}

	@Override
	public Verse getVerse(BookOfBible book, int chapter, int verse) {
		return getVerse(new Reference(book, chapter, verse));
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
		return new VerseList(version, title, verses);
	}

	@Override
	public VerseList getVersesContaining(String phrase) {
		VerseList result = new VerseList(version, title);
		if (phrase == null || phrase.isEmpty()) {
			return result;
		}
		for (Verse v : verses) {
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
		for (Verse v : verses) {
			if (v.getText().toLowerCase().contains(phrase.toLowerCase())) {
				result.add(v.getReference());
			}
		}
		return result;
	}

	@Override
	public VerseList getVerses(ArrayList<Reference> references) {
		VerseList result = new VerseList(version, title);
		for (Reference r : references) {
			Verse v = getVerse(r);
			result.add(v);
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
		int last = -1;
		for (Verse v : verses) {
			Reference r = v.getReference();
			if (r.getBook().equals(book) && r.getChapter() == chapter) {
				if (r.getVerse() > last) {
					last = r.getVerse();
				}
			}
		}
		return last;
	}

	@Override
	public int getLastChapterNumber(BookOfBible book) {
		int last = -1;
		for (Verse v : verses) {
			Reference r = v.getReference();
			if (r.getBook().equals(book)) {
				if (r.getChapter() > last) {
					last = r.getChapter();
				}
			}
		}
		return last;
	}

	@Override
	public ArrayList<Reference> getReferencesInclusive(Reference firstVerse, Reference lastVerse) {
		ArrayList<Reference> result = new ArrayList<>();
		boolean inRange = false;
		for (Verse v : verses) {
			Reference r = v.getReference();
			if (r.equals(firstVerse)) {
				inRange = true;
			}
			if (inRange) {
				result.add(r);
			}
			if (r.equals(lastVerse)) {
				break;
			}
		}
		return result;
	}

	@Override
	public ArrayList<Reference> getReferencesExclusive(Reference firstVerse, Reference lastVerse) {
		ArrayList<Reference> result = new ArrayList<>();
		boolean inRange = false;
		for (Verse v : verses) {
			Reference r = v.getReference();
			if (r.equals(lastVerse)) {
				break;
			}
			if (inRange) {
				result.add(r);
			}
			if (r.equals(firstVerse)) {
				inRange = true;
			}
		}
		return result;
	}

	@Override
	public ArrayList<Reference> getReferencesForBook(BookOfBible book) {
		Reference first = null;
		Reference last = null;
		for (Verse v : verses) {
			Reference r = v.getReference();
			if (r.getBook().equals(book)) {
				if (first == null) {
					first = r;
				}
				last = r;
			}
		}
		if (first == null) {
			return new ArrayList<>();
		}
		return getReferencesInclusive(first, last);
	}

	@Override
	public ArrayList<Reference> getReferencesForChapter(BookOfBible book, int chapter) {
		ArrayList<Reference> result = new ArrayList<>();
		for(Verse v : verses) {
			Reference r = v.getReference();
			if(r.getBook().equals(book) && r.getChapter() == chapter) {
				result.add(r);
			}
		}
		return result;
	}

	@Override
	public ArrayList<Reference> getReferencesForChapters(BookOfBible book, int chapter1, int chapter2) {
		ArrayList<Reference> result = new ArrayList<>();
		for(Verse v : verses) {
			Reference r = v.getReference();
			if(r.getBook().equals(book) && r.getChapter() >= chapter1 && r.getChapter() <= chapter2) {
				result.add(r);
			}
		}
		return result;
	}

	@Override
	public ArrayList<Reference> getReferencesForPassage(BookOfBible book, int chapter, int verse1, int verse2) {
		ArrayList<Reference> result = new ArrayList<>();
		for(Verse v : verses) {
			Reference r = v.getReference();
			if(r.getBook().equals(book) && r.getChapter() == chapter && r.getVerse() >= verse1 && r.getVerse() <= verse2) {
				result.add(r);
			}
		}
		return result;
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
		return getVerses(getReferencesForPassage(book, chapter,  verse1, verse2));
	}

	@Override
	public VerseList getPassage(BookOfBible book, int chapter1, int verse1, int chapter2, int verse2) {
		return getVerses(getReferencesForPassage(book, chapter1, verse1, chapter2, verse2));
	}
}
