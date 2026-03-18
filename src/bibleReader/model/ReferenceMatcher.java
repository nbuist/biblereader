package bibleReader.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class to parse references from user input.
 * 
 * @author Aleks Rutins
 */
public class ReferenceMatcher {
	/**
	 * https://regex101.com/r/GUzwIx/3
	 */
	private static Pattern referencePat = Pattern.compile(
			"^[ \\t]*(?<book>(?:\\d+[\\t ]+)*[ ]*[a-zA-Z ]+?)(?:[ \\t]+(?<chapterStart>\\d+)(?:(?:[ \\t]*-[ \\t]*(?:(?<chapterEnd>\\d+)(?:[ \\t]*:[ \\t]*(?<verseEndShort>\\d+))?)|(?<chapterEndShort>\\d+))|(?:[ \\t]*(?:[ \\t]*:[ \\t]*(?:(?:(?<verseStart>\\d+)(?:[ \\t]*-[ \\t]*(?<verseEnd>\\d+))?)|(?:(?<verseStartLong>\\d+)(?:[ \\t]*-[ \\t]*(?<chapterEndLong>\\d+)[ \\t]*:[ \\t]*(?<verseEndLong>\\d+))))?))?)?)?[ \\t]*$");

	/**
	 * Represents text extracted from a regex match, along with the group that
	 * extracted it. The group is not currently used (as of Proj. 7) with some
	 * design decisions I made, but could be useful later on.
	 */
	private record ExtractedGroup(String group, String text) {
	}

	/**
	 * Extract a group of text from a matcher, falling back to other groups.
	 */
	private static ExtractedGroup extract(Matcher m, String[] groupFallbacks) {
		for (String g : groupFallbacks) {
			try {
				String res = m.group(g);
				if (res != null)
					return new ExtractedGroup(g, res);
			} catch (IllegalStateException | IllegalArgumentException e) {
				System.err.println(
						"Warning: error while extracting group " + g + ". Check your regex? Falling back for now.");
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Attempt to parse a reference from a user-inputted string. Return null if the
	 * input is invalid.
	 */
	public static ReferencePassage parse(Bible bible, CharSequence sRef) {
		Matcher m = referencePat.matcher(sRef);
		if (!m.matches())
			return null;
		try {
			String book = m.group("book");
			String chapterStart = m.group("chapterStart");
			ExtractedGroup verseStart = extract(m, new String[] { "verseStart", "verseStartLong" });
			ExtractedGroup chapterEnd = extract(m, new String[] { "chapterEndShort", "chapterEnd", "chapterEndLong" });
			ExtractedGroup verseEnd = extract(m, new String[] { "verseEndShort", "verseEnd", "verseEndLong" });

			BookOfBible bookOfBible = BookOfBible.getBookOfBible(book);
			// if just book
			if (chapterStart == null && verseStart == null && chapterEnd == null && verseEnd == null) {
				// return whole book
				int lastChapter = bible.getLastChapterNumber(bookOfBible);
				return new ReferencePassage(new Reference(bookOfBible, 1, 1),
						new Reference(bookOfBible, lastChapter, bible.getLastVerseNumber(bookOfBible, lastChapter)));
			}
			// else
			else {
				// if no verse start
				if (verseStart == null) {
					// if no chapter end
					if (chapterEnd == null && chapterStart != null) {
						// return whole chapter start
						int ch = Integer.parseInt(chapterStart);
						return new ReferencePassage(new Reference(bookOfBible, ch, 1),
								new Reference(bookOfBible, ch, bible.getLastVerseNumber(bookOfBible, ch)));
					}
					// else
					else {
						// return (chapter start, chapter end)
						int chs = Integer.parseInt(chapterStart), che = Integer.parseInt(chapterEnd.text());
						return new ReferencePassage(new Reference(bookOfBible, chs, 1),
								new Reference(bookOfBible, che, bible.getLastVerseNumber(bookOfBible, che)));
					}
				}
				// else
				else {
					// if verse end
					if (verseEnd != null) {
						// return (verse start, verse end)
						if (chapterEnd == null)
							chapterEnd = new ExtractedGroup("chapterEnd", chapterStart);
						int chs = Integer.parseInt(chapterStart), che = Integer.parseInt(chapterEnd.text()),
								vs = Integer.parseInt(verseStart.text()), ve = Integer.parseInt(verseEnd.text());

						return new ReferencePassage(new Reference(bookOfBible, chs, vs),
								new Reference(bookOfBible, che, bible.getLastVerseNumber(bookOfBible, ve)));
					}
					// else
					else {
						// return verse start
						int ch = Integer.parseInt(chapterStart), vs = Integer.parseInt(verseStart.text());
						Reference ref = new Reference(bookOfBible, ch, vs);
						return new ReferencePassage(ref, ref);
					}
				}
			}

		} catch (IllegalStateException | IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		}
	}
}
