package bibleReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import bibleReader.model.Bible;
import bibleReader.model.BookOfBible;
import bibleReader.model.Reference;
import bibleReader.model.Verse;
import bibleReader.model.VerseList;

/**
 * A utility class that has useful methods to read/write Bibles and Verses.
 * 
 * @version 1.2.0 | March 12th 2026
 * @author cusack
 * @author noahbuist, aleksrutins
 */
public class BibleIO {

	/**
	 * Read in a file and create a Bible object from it and return it.
	 * 
	 * @param bibleFile
	 * @return bible file based on its extension
	 */
	// T his method is complete, bu t it won't work until the methods it uses are
	// implemented.
	public static VerseList readBible(File bibleFile) { // Get the extension of
														// the file
		String name = bibleFile.getName();
		String extension = name.substring(name.lastIndexOf('.') + 1, name.length());

		// Call the read method based on the file type.
		if ("atv".equals(extension.toLowerCase())) {
			try {
				return readATV(bibleFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("xmv".equals(extension.toLowerCase())) {
			try {
				return readXMV(bibleFile);
			}catch(FileNotFoundException e) {
				System.out.println("FileNotFound exception at: "  + e.getLocalizedMessage());
				return null;
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("IOException exception at: " + e.getLocalizedMessage());
				System.out.println("Absolute path tried: " + bibleFile.getAbsolutePath());
			}
		} else {
			return null;
		}
		return null;

	}

	/**
	 * Read in a Bible that is saved in the "ATV" format. The format is described
	 * below.
	 * 
	 * @param bibleFile The file containing a Bible with .atv extension.
	 * @return A Bible object constructed from the file bibleFile, or null if there
	 *         was an error reading the file.
	 * @throws IOException
	 */
	private static VerseList readATV(File bibleFile) throws IOException {
		String version = "unknown";
		String description = "";

		try (BufferedReader reader = new BufferedReader(new FileReader(bibleFile))) {
			// Read the first line for version/description
			String firstLine = reader.readLine();
			if (firstLine != null) {
				firstLine = firstLine.trim();
				if (firstLine.isEmpty()) {
					version = "unknown";
					description = "";
				} else if (firstLine.contains(":")) {
					int colonIndex = firstLine.indexOf(":");
					version = firstLine.substring(0, colonIndex).trim();
					description = firstLine.substring(colonIndex + 1).trim();
				} else {
					version = firstLine;
					description = "";
				}
			}

			VerseList verses = new VerseList(version, description);

			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty()) {
					continue;
				}

				String[] parts = line.split("@", 3);
				if (parts.length < 3) {
					return null;
				}

				String bookAbbrev = parts[0].trim();
				String chapterVerse = parts[1].trim();
				String text = parts[2].trim();

				// chapter:verse must contain a colon and two numbers
				String[] chapterVerseParts = chapterVerse.split(":", 2);
				if (chapterVerseParts.length < 2) {
					return null;
				}

				int chapter, verseNum;
				try {
					chapter = Integer.parseInt(chapterVerseParts[0].trim());
					verseNum = Integer.parseInt(chapterVerseParts[1].trim());
				} catch (NumberFormatException e) {
					return null;
				}

				BookOfBible book = BookOfBible.getBookOfBible(bookAbbrev);
				if (book == null) {
					return null;
				}

				Reference ref = new Reference(book, chapter, verseNum);
				verses.add(new Verse(ref, text));
			}

			return verses;

		} catch (FileNotFoundException e) {
			System.out.println("FileNotFound exception at: " + e.getLocalizedMessage());
			return null;
		}
	}

	/**
	 * Read in the Bible that is stored in the XMV format.
	 * 
	 * @param bibleFile The file containing a Bible with .xmv extension.
	 * @return A Bible object constructed from the file bibleFile, or null if there
	 *         was an error reading the file.
	 */
	private static VerseList readXMV(File bibleFile) throws IOException {
		// TODO Implement me: Stage 8

		// The XMV is sort of XML-like, but it doesn't have end tags.
		// No description of the file format is given here.
		// You need to look at the file to determine how it should be parsed.

		// TODO Documentation: Stage 8 (Update the Javadoc comment to describe
		// the format of the file.)
		String version = "unknown";
		String description = "";
		

		BufferedReader reader = new BufferedReader(new FileReader(bibleFile));
		VerseList verses = new VerseList(version, description);
		String currentBook = "";
		int currentChapter = 0;
		String line;
		
		while ((line = reader.readLine()) != null){
			line = line.trim();
			if (line.startsWith("<Version")) {
				String content = line.substring("<Version ".length(), line.length() - 1);
				int colonIndex = content.indexOf(":");
				if (colonIndex != -1) {
					version = content.substring(0, colonIndex).trim();
					description = content.substring(colonIndex + 1).trim();
				} else {
					version = content.trim();
					description = "";
				}
				verses = new VerseList(version, description);

			} else if (line.startsWith("<Book ")) {
				String content = line.substring("<Book ".length(), line.length() - 1);
				int commaIndex = content.indexOf(",");
				if (commaIndex != -1) {
					currentBook = content.substring(0, commaIndex).trim();
				} else {
					currentBook = content.trim();
				}

			} else if (line.startsWith("<Chapter ")) {
				String content = line.substring("<Chapter ".length(), line.length() - 1);
				currentChapter = Integer.parseInt(content.trim());
			} else if (line.startsWith("<Verse ")) {
				String content = line.substring("<Verse ".length());
				int closeIndex = content.indexOf(">");
				if (closeIndex != -1) {
					int verseNum = Integer.parseInt(content.substring(0, closeIndex).trim());
					String verseText = content.substring(closeIndex + 1).trim();
					BookOfBible bible = BookOfBible.getBookOfBible(currentBook);
					Reference ref = new Reference(bible, currentChapter, verseNum);
					verses.add(new Verse(ref, verseText));
				}
			}
		}
		reader.close();
		return verses;

	}

	// Note: In the following methods, we should really ensure that the file
	// extension is correct
	// (i.e. it should be ".atv"). However for now we won't worry about it.
	// Hopefully the GUI code
	// will be written in such a way that it will require the extension to be
	// correct if we are
	// concerned about it.

	/**
	 * Write out the Bible in the ATV format.
	 * 
	 * @param file  The file that the Bible should be written to.
	 * @param bible The Bible that will be written to the file.
	 */
	public static void writeBibleATV(File file, Bible bible) {
		String description = bible.getVersion() + ": " + bible.getTitle();
		writeVersesATV(file, description, bible.getAllVerses());
	}

	/**
	 * Write out the given verses in the ATV format, using the description as the
	 * first line of the file.
	 * 
	 * @param file        The file that the Bible should be written to.
	 * @param description The contents that will be placed on the first line of the
	 *                    file, formatted appropriately.
	 * @param verses      The verses that will be written to the file.
	 */
	public static void writeVersesATV(File file, String description, VerseList verses) {
		// TODO Implement me: Stage 8
		try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
			writer.println(description);
			for (Verse verse : verses) {
				Reference ref = verse.getReference();
				writer.println(ref.getBook().toString() + "@" + ref.getChapter() + ":" + ref.getVerse() + "@"
						+ verse.getText());
			}
		} catch (IOException e) {
			System.out.println("FileNotFound exception at: " + e.getLocalizedMessage());
		}
	}

	/**
	 * Write the string out to the given file. It is presumed that the string is an
	 * HTML rendering of some verses, but really it can be anything.
	 * 
	 * @param file
	 * @param text
	 */
	public static void writeText(File file, String text) {
		// TODO Implement me: Stage 8
		// This one should be really simple.
		// My version is 4 lines of code (not counting the try/catch code).
		try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
			writer.print(text);
		} catch (IOException e) {
			System.out.println("IOException at: " + e.getLocalizedMessage());
		}
	}
}
