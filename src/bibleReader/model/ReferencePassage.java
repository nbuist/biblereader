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

	/** Get all of the references contained between {@link start} and {@link end} */
	public ArrayList<Reference> getReferences(Bible bible) {
		ArrayList<Reference> res = new ArrayList<>();

		for (Reference r = new Reference(start.getBookOfBible(), start.getChapter(), start.getVerse()); r
				.compareTo(end) <= 0; r = incrementReference(bible, r)) {
			res.add(r);
		}

		return res;
	}
}
