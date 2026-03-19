package bibleReader.model;

import java.util.ArrayList;

public record ReferencePassage(Reference start, Reference end) {

	/** Helper method: increment a reference within a bible */
	private Reference incrementReference(Bible bible, Reference ref) {
		BookOfBible book = ref.getBookOfBible();
		int chapter = ref.getChapter();
		int verse = ref.getVerse() + 1;

		if (verse > bible.getLastVerseNumber(book, chapter)) {
			chapter++;
			verse = 1;
		}

		if (chapter > bible.getLastChapterNumber(book)) {
			book = BookOfBible.nextBook(book);
			chapter = 1;
		}

		return new Reference(book, chapter, verse);

	}

	/**
	 * Get all of the references contained between {@link start} and {@link end},
	 * inclusive
	 */
	public ArrayList<Reference> getReferences(Bible bible) {
		ArrayList<Reference> res = new ArrayList<>();
		if (!isValid(bible))
			return res;

		for (Reference r = new Reference(start.getBookOfBible(), start.getChapter(), start.getVerse()); r
				.compareTo(end) <= 0; r = incrementReference(bible, r)) {
			res.add(r);
		}

		return res;
	}

	/**
	 * Get all of the references contained between {@link start} and {@link end},
	 * exclusive
	 */
	public ArrayList<Reference> getReferencesExclusive(Bible bible) {
		ArrayList<Reference> res = new ArrayList<>();
		if (!isValidExclusive(bible))
			return res;

		for (Reference r = new Reference(start.getBookOfBible(), start.getChapter(), start.getVerse()); r
				.compareTo(end) < 0 && bible.isValid(r); r = incrementReference(bible, r)) {
			res.add(r);
		}

		return res;
	}

	/** Returns if the passage is valid in the given Bible. */
	public boolean isValid(Bible bible) {
		return bible.isValid(start) && bible.isValid(end) && end.compareTo(start) >= 0;
	}

	/**
	 * Returns if the passage is valid, not excluding the end, in the given Bible.
	 */
	public boolean isValidExclusive(Bible bible) {
		return bible.isValid(start) && end.compareTo(start) >= 0;
	}
}