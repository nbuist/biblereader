package bibleReader.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.File;
import java.util.concurrent.TimeUnit;

import bibleReader.BibleIO;
import bibleReader.model.Bible;
import bibleReader.model.BibleFactory;
import bibleReader.model.VerseList;

/**
 * Tests that the concordance can be created within a reasonable time frame.
 * 
 * @author Chuck Cusack, March, 2013
 */
@Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
public class Stage12_1ConcordanceCreationTest {

	private static Bible	bible;

	@BeforeAll
	public static void readFilesAndCreateConcordance() {
			File file = new File("files/esv.atv");
			VerseList verses = BibleIO.readBible(file);
			bible = BibleFactory.createBible(verses);
	}

	@BeforeEach
	public void setUp() throws Exception {}

	@Test
	public void testConcordanceCreationTime() {
			BibleFactory.createConcordance(bible);
	}
}
