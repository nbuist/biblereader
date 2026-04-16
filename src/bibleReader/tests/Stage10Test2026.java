package bibleReader.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bibleReader.BibleReaderApp;

/**
 * JUnit 5 conversion of Stage10Test2020. Done by ChatGPT. Supervised by CAC,
 * 3/14/26
 */
public class Stage10Test2026 {

	private BibleReaderApp br;
	private JTextField input;
	private JButton search;
	private JButton passage;
	private JButton next;
	private JButton previous;
	private JEditorPane output;

	@BeforeEach
	public void setUp() throws Exception {
		runOnEdtAndWait(() -> {
			br = new BibleReaderApp();

			Container root = getRootContainer(br);

			input = findByName(root, JTextField.class, "InputTextField");
			search = findByName(root, JButton.class, "SearchButton");
			passage = findByName(root, JButton.class, "PassageButton");
			next = findByName(root, JButton.class, "NextButton");
			previous = findByName(root, JButton.class, "PreviousButton");
			output = findByName(root, JEditorPane.class, "OutputEditorPane");

			assertNotNull(input, "Could not find InputTextField");
			assertNotNull(search, "Could not find SearchButton");
			assertNotNull(passage, "Could not find PassageButton");
			assertNotNull(next, "Could not find NextButton");
			assertNotNull(previous, "Could not find PreviousButton");
			assertNotNull(output, "Could not find OutputEditorPane");
		});

		waitForEdt();
	}

	@AfterEach
	public void tearDown() throws Exception {
		runOnEdtAndWait(() -> {
			for (Window w : Window.getWindows()) {
				if (w != null) {
					w.dispose();
				}
			}
		});
	}

	@Test
	public void testSearchResultsPage1() throws Exception {
		setText(input, "eaten");
		click(search);
		assertEquals(57, countOccurrences(getText(output), "eaten"), "Wrong number of occurrences on page 1");
	}

	@Test
	public void testSearchResultsPage1Bolded() throws Exception {
		setText(input, "eaten");
		click(search);
		assertEquals(57, countOccurrences(getText(output), "<b>eaten</b>"), "Wrong number of occurrences on page 1");
	}

	@Test
	public void testSearchResultsPages2AndBeyond() throws Exception {
		int[] timesOnPage = { 57, 56, 54, 61, 49, 39, 49, 34, 28 };

		setText(input, "eaten");
		click(search);
		for (int i = 1; i < timesOnPage.length; i++) {
			int page = i + 1;
			click(next);
			assertEquals(timesOnPage[i], countOccurrences(getText(output), "eaten"),
					"Wrong number of occurrences on page " + page);
		}
	}

	@Test
	public void testSearchResultsPages2AndBeyondBolded() throws Exception {
		int[] timesOnPage = { 57, 56, 54, 61, 49, 39, 49, 34, 28 };

		setText(input, "eaten");
		click(search);
		for (int i = 1; i < timesOnPage.length; i++) {
			int page = i + 1;
			click(next);
			assertEquals(timesOnPage[i], countOccurrences(getText(output), "<b>eaten</b>"),
					"Wrong number of occurrences on page " + page + ".  I think you forgot to bold it.");
		}
	}

	@Test
	public void testPreviousNextEnabledAndDisabledCorrectly() throws Exception {
		setText(input, "eaten");
		click(search);

		assertFalse(isEnabled(previous), "Previous should be disabled");
		assertTrue(isEnabled(next), "Next should be enabled");

		for (int i = 1; i < 8; i++) {
			int page = i + 1;
			click(next);
			assertTrue(isEnabled(previous), "Previous should be enabled on page " + page);
			assertTrue(isEnabled(next), "Next should be enabled on page " + page);
		}

		click(next);
		assertTrue(isEnabled(previous), "Previous should be enabled");
		assertFalse(isEnabled(next), "Next should be disabled");
	}

	@Test
	public void testPreviousAndNextBackAndForthWorks() throws Exception {
		setText(input, "eaten");
		click(search);
		clickMultiple(next, 8);
		assertTrue(isEnabled(previous), "Previous should be enabled");
		assertFalse(isEnabled(next), "Next should be disabled");

		clickMultiple(previous, 3);

		clickMultiple(next, 3);
		assertFalse(isEnabled(next), "Next should be disabled");
		assertTrue(isEnabled(previous), "Previous should be enabled");

		clickMultiple(previous, 9);
		assertFalse(isEnabled(previous), "Previous should be disabled");
		assertTrue(isEnabled(next), "Next should be enabled");
	}

	@Test
	public void testSinglePageSearchResults() throws Exception {
		setText(input, "wagon");
		click(search);
		assertEquals(36, countOccurrences(getText(output), "wagon"), "Wrong number of occurrences of wagon");
	}

	@Test
	public void testSinglePageSearchNavigation() throws Exception {
		setText(input, "wagon");
		click(search);
		assertFalse(isEnabled(previous), "Previous should be disabled");
		assertFalse(isEnabled(next), "Next should be disabled");
	}

	@Test
	public void testSinglePagePassageResults() throws Exception {
		setText(input, "3 John");
		click(passage);
		assertEquals(3, countOccurrences(getText(output), "Diotrephes"), "Wrong number of occurrences of wagon");
	}

	@Test
	public void testSinglePagePassageNavigation() throws Exception {
		setText(input, "3 John");
		click(passage);
		assertFalse(isEnabled(previous), "Previous should be disabled");
		assertFalse(isEnabled(next), "Next should be disabled");
	}

	@Test
	public void testSearchResultsPage11() throws Exception {
		setText(input, "fool");
		click(search);
		clickMultiple(next, 10);
		assertEquals(54, countOccurrences(getText(output), "fool"), "Wrong number of occurrences on page 10");
	}

	@Test
	public void testnoResultsForPassage() throws Exception {
		setText(input, "fool");
		click(search);

		setText(input, "fool");
		click(passage);

		assertFalse(isEnabled(previous), "Previous should be disabled");
		assertFalse(isEnabled(next), "Next should be disabled");
	}

	@Test
	public void testNoResultsForSearch() throws Exception {
		setText(input, "3 John");
		click(search);
		assertFalse(isEnabled(previous), "Previous should be disabled");
		assertFalse(isEnabled(next), "Next should be disabled");
	}

	@Test
	public void testPreviousNextEnabledAndDisabledWithMultipleof20() throws Exception {
		setText(input, "fool");
		click(search);
		assertFalse(isEnabled(previous), "Previous should be disabled");
		assertTrue(isEnabled(next), "Next should be enabled");

		clickMultiple(next, 5);
		assertTrue(isEnabled(previous), "Previous should be enabled");
		assertTrue(isEnabled(next), "Next should be enabled");

		clickMultiple(next, 5);
		assertTrue(isEnabled(previous), "Previous should be enabled");
		assertFalse(isEnabled(next), "Next should be disabled");
	}

	@Test
	public void testPassageDisplaysReferencePage1() throws Exception {
		setText(input, "3 John");
		click(passage);

		String text = getText(output);
		assertTrue(text.contains("3 John 1:1-15"), "The title of the third page should be '3 John 1:1-15'");
	}

	@Test
	public void testPassageDisplaysReferencePage3() throws Exception {
		setText(input, "Ephesians");
		click(passage);
		click(next);
		click(next);
		String page3 = getText(output);
		assertTrue(page3.contains("Ephesians 2:18-3:15"),
				"The title of the third page should be 'Ephesians 2:18-3:15'");
	}

	@Test
	public void testSuptagesWithMissingVerse() throws Exception {
		setText(input, "3 John");
		click(passage);
		String text = getText(output);

		int numberFifteens = countOccurrences(text, ">15<");
		assertEquals(1, numberFifteens,
				"Only ESV has verse 15, so the verse number should only appear once in your results.");
	}

	@Test
	public void testSupTagsPage1() throws Exception {
		setText(input, "3 John");
		click(passage);
		String text = getText(output);

		int numFours = countOccurrences(text, ">4<");
		assertEquals(3, numFours);

		int numFourteens = countOccurrences(text, ">14<");
		assertEquals(3, numFourteens);
	}

	@Test
	public void testSupTagsForChapter() throws Exception {
		setText(input, "James");
		click(passage);
		click(next);
		String page2 = getText(output);

		int number2s = countOccurrences(page2, ">2<");
		assertEquals(6, number2s, "2 should occur inside 6 tags on this page--3 times to indicate chapter 2, "
				+ "and 3 times to indicate verse 2.");
	}

	@Test
	public void testPassageResultsPage1() throws Exception {
		setText(input, "Ephesians");
		click(passage);
		String page1 = getText(output);
		assertEquals(6, countOccurrences(page1, "heavenly"));
	}

	@Test
	public void testPassageResultsPage8() throws Exception {
		setText(input, "Ephesians");
		click(passage);
		clickMultiple(next, 7);
		assertEquals(2, countOccurrences(getText(output), "incorruptible"));
	}

	@Test
	public void testPassageResultsNavigation() throws Exception {
		setText(input, "Ephesians");
		click(passage);
		for (int i = 1; i < 7; i++) {
			int page = i + 1;
			click(next);
			assertTrue(isEnabled(previous), "Previous should be enabled on page " + page);
			assertTrue(isEnabled(next), "Next should be enabled on page " + page);
		}

		click(next);
		assertTrue(isEnabled(previous), "Previous should be enabled");
		assertFalse(isEnabled(next), "Next should be disabled");
	}

	@Test
	public void testPassageResultOrderOneVerse() throws Exception {
		setText(input, "Ephesians");
		click(passage);
		String page1 = getText(output);
		page1 = page1.replaceAll("\\s+", "");

		int asvIndex = page1.indexOf("tosumupallthingsinChrist,");
		int esvIndex1 = page1.indexOf("touniteallthingsinhim,");
		int kjvIndex = page1.indexOf("hemightgathertogetherinoneallthingsinChrist,");

		assertTrue(asvIndex >= 0);
		assertTrue(esvIndex1 >= 0);
		assertTrue(kjvIndex >= 0);
		assertTrue(asvIndex < esvIndex1, "The results for ASV should be before the results for ESV");
		assertTrue(esvIndex1 < kjvIndex, "The results for ESV should be before the results for KJV");
	}

	@Test
	public void testPassageResultOrderWholePassage() throws Exception {
		setText(input, "Habakkuk 3");
		click(passage);
		String outputText = getText(output);

		outputText = outputText.replaceAll("\\s+", "");
		int asvIndex = outputText.indexOf("Andwillmakemetowalkuponmyhighplaces.");
		int esvIndex1 = outputText.indexOf("Ihaveheardthereportofyou,");
		int esvIndex2 = outputText.indexOf("Tothechoirmaster:");
		int kjvIndex = outputText.indexOf("AprayerofHabakkuktheprophetuponShigionoth");

		assertTrue(asvIndex >= 0);
		assertTrue(esvIndex1 >= 0);
		assertTrue(esvIndex2 >= 0);
		assertTrue(kjvIndex >= 0);
		assertTrue(asvIndex < esvIndex1,
				"For passages, all of the results for ASV should be before all of the results for ESV");
		assertTrue(esvIndex2 < kjvIndex,
				"For passages, all of the results for ESV should be before all of the results for KJV");
	}

	@Test
	public void testMultiplePassagesResults() throws Exception {
		setText(input, "John 1-10");
		click(passage);

		assertTrue(getText(output).contains("John 1:1-20"), "The title of the results should contain John 1:1-20");

		clickMultiple(next, 23);

		assertFalse(isEnabled(next), "Next should be disabled");
		assertTrue(isEnabled(previous), "Previous should be enabled");

		click(passage);

		assertTrue(getText(output).contains("John 1:1-20"), "The title of the results should contain John 1:1-20");
		assertFalse(isEnabled(previous), "Previous should be disabled");
		assertTrue(isEnabled(next), "Next should be enabled");

		click(next);
		assertTrue(getText(output).contains("John 1:21-40"), "The title of the results should contain John 1:21-40");
	}

	@Test
	public void testMultipleSearchResults() throws Exception {
		setText(input, "goats");
		click(search);
		clickMultiple(next, 5);

		assertFalse(isEnabled(next), "Next should be disabled");
		assertTrue(isEnabled(previous), "Previous should be enabled");

		setText(input, "golden");
		click(search);

		assertFalse(isEnabled(previous), "Previous should be disabled");
		assertTrue(isEnabled(next), "Next should be enabled");

		assertEquals(46, countOccurrences(getText(output), "golden"),
				"Wrong number of occurrences of golden on page 1");
		clickMultiple(next, 3);
		assertEquals(66, countOccurrences(getText(output), "golden"),
				"Wrong number of occurrences of golden on page 4");
	}

	@Test
	public void testSearchThenPassage() throws Exception {
		setText(input, "granted");
		click(search);

		clickMultiple(next, 2);
		assertFalse(isEnabled(next), "Next should be disabled");
		assertTrue(isEnabled(previous), "Previous should be enabled");

		setText(input, "Psalm 27-28");
		click(passage);

		assertFalse(isEnabled(previous), "Previous should be disabled");
		assertTrue(isEnabled(next), "Next should be enabled");
		assertTrue(getText(output).contains("Psalms 27:1-28:6"),
				"The title of the first page should contain Psalms 27:1-28:6");

		click(next);
		assertTrue(getText(output).contains("Psalms 28:7-9"),
				"The title of the second page should contain Psalms 28:7-9");
	}

	@Test
	public void testPassageThenSearch() throws Exception {
		setText(input, "Ruth");
		click(passage);
		clickMultiple(next, 4);

		assertFalse(isEnabled(next), "Next should be disabled");
		assertTrue(isEnabled(previous), "Previous should be enabled");

		setText(input, "goats");
		click(search);

		assertFalse(isEnabled(previous), "Previous should be disabled");
		assertTrue(isEnabled(next), "Next should be enabled");

		// Now saying it should be 59? (2026)
		assertEquals(53, countOccurrences(getText(output), "goats"), "Wrong number of occurrences of goats on page 1.");
		click(next);
		assertEquals(34, countOccurrences(getText(output), "goats"), "Wrong number of occurrences of goats on page 2.");
	}

	@Test
	public void testInvalidSearchClearsResults() throws Exception {
		setText(input, "eaten");
		click(search);
		clickMultiple(next, 4);

		setText(input, "foofy");
		click(search);

		assertFalse(getText(output).contains("eaten"),
				"It appears you didn't clear the results from the previous search.");
		assertFalse(isEnabled(next), "Next should be disabled");
		assertFalse(isEnabled(previous), "Previous should be disabled");
	}

	@Test
	public void testInvalidPassageSearchClearsResults() throws Exception {
		setText(input, "Ruth");
		click(passage);
		click(next);

		setText(input, "Hessitations 3");
		click(passage);

		assertFalse(getText(output).toLowerCase().contains("ruth"),
				"It appears you didn't clear the results from the previous search.");
		assertFalse(isEnabled(next), "Next should be disabled");
		assertFalse(isEnabled(previous), "Previous should be disabled");
	}

	// -------------------------------------------------------------------
	// New ChatGPT tests
	@Test
	public void testSearchIsCaseInsensitive() throws Exception {
		setText(input, "eaten");
		click(search);
		String lower = getText(output);

		setText(input, "EATEN");
		click(search);
		String upper = getText(output);

		assertEquals(normalize(lower), normalize(upper),
				"Search results should be the same regardless of capitalization.");
		assertFalse(isEnabled(previous), "Previous should be disabled on page 1.");
		assertTrue(isEnabled(next), "Next should be enabled on page 1.");
	}

	@Test
	public void testPassageIgnoresExtraWhitespace() throws Exception {
		setText(input, "3 John");
		click(passage);
		String normal = getText(output);

		setText(input, "   3   John   ");
		click(passage);
		String spaced = getText(output);

		assertEquals(normalize(normal), normalize(spaced),
				"Passage lookup should ignore leading/trailing and repeated internal spaces.");
		assertFalse(isEnabled(previous), "Previous should be disabled.");
		assertFalse(isEnabled(next), "Next should be disabled.");
	}

	@Test
	public void testSearchBackThenForwardRestoresExactSamePage() throws Exception {
		setText(input, "eaten");
		click(search);

		String page1 = getText(output);
		click(next);
		String page2 = getText(output);

		assertNotEquals(normalize(page1), normalize(page2), "Page 2 should not be identical to page 1.");

		click(previous);
		String page1Again = getText(output);
		assertEquals(normalize(page1), normalize(page1Again),
				"Going back should restore exactly the original page 1 results.");

		click(next);
		String page2Again = getText(output);
		assertEquals(normalize(page2), normalize(page2Again),
				"Going forward again should restore exactly the original page 2 results.");
	}

	@Test
	public void testPassageBackThenForwardRestoresExactSamePage() throws Exception {
		setText(input, "Ephesians");
		click(passage);

		String page1 = getText(output);
		click(next);
		String page2 = getText(output);

		assertNotEquals(normalize(page1), normalize(page2), "Page 2 should not be identical to page 1.");

		click(previous);
		String page1Again = getText(output);
		assertEquals(normalize(page1), normalize(page1Again),
				"Going back should restore exactly the original page 1 passage results.");

		click(next);
		String page2Again = getText(output);
		assertEquals(normalize(page2), normalize(page2Again),
				"Going forward again should restore exactly the original page 2 passage results.");
	}

	@Test
	public void testInvalidPassageThenValidPassageDoesNotLeaveStaleState() throws Exception {
		setText(input, "Hessitations 3");
		click(passage);

		assertFalse(isEnabled(previous), "Previous should be disabled after invalid passage.");
		assertFalse(isEnabled(next), "Next should be disabled after invalid passage.");

		setText(input, "3 John");
		click(passage);

		String text = getText(output);
		assertTrue(text.contains("3 John 1:1-15"),
				"A valid passage after an invalid one should display fresh results.");
		assertFalse(isEnabled(previous), "Previous should be disabled.");
		assertFalse(isEnabled(next), "Next should be disabled.");
	}

	@Test
	public void testInvalidSearchThenValidSearchDoesNotLeaveStaleState() throws Exception {
		setText(input, "foofy");
		click(search);

		assertFalse(isEnabled(previous), "Previous should be disabled after invalid search.");
		assertFalse(isEnabled(next), "Next should be disabled after invalid search.");

		setText(input, "wagon");
		click(search);

		String text = getText(output);
		assertTrue(text.toLowerCase().contains("wagon"),
				"A valid search after an invalid one should display fresh results.");
		assertFalse(isEnabled(previous), "Previous should be disabled.");
		assertFalse(isEnabled(next), "Next should be disabled for wagon.");
	}

	@Test
	public void testNoResultsMessageAppearsForInvalidPassage() throws Exception {
		setText(input, "fool");
		click(passage);

		String text = getText(output).toLowerCase();
		assertTrue(getText(output).trim().length() == 0 
		        || getText(output).toLowerCase().contains("no"),
		        "Invalid passage should produce empty or 'no results' output.");
		assertFalse(isEnabled(previous), "Previous should be disabled.");
		assertFalse(isEnabled(next), "Next should be disabled.");
	}

	// A few more from ChatGPT
	@Test
	public void testSearchPreviousReturnsExactOriginalFirstPage() throws Exception {
	    setText(input, "eaten");
	    click(search);

	    String firstPage = normalize(getText(output));
	    assertFalse(isEnabled(previous), "Previous should be disabled on page 1.");
	    assertTrue(isEnabled(next), "Next should be enabled on page 1.");

	    click(next);
	    String secondPage = normalize(getText(output));
	    assertNotEquals(firstPage, secondPage,
	            "Page 2 should not be identical to page 1.");

	    click(previous);
	    String firstPageAgain = normalize(getText(output));
	    assertEquals(firstPage, firstPageAgain,
	            "After going to page 2 and back, page 1 should be exactly restored.");
	    assertFalse(isEnabled(previous), "Previous should be disabled again on page 1.");
	    assertTrue(isEnabled(next), "Next should be enabled again on page 1.");
	}

	@Test
	public void testPassagePreviousReturnsExactOriginalFirstPage() throws Exception {
	    setText(input, "Ephesians");
	    click(passage);

	    String firstPage = normalize(getText(output));
	    assertFalse(isEnabled(previous), "Previous should be disabled on page 1.");
	    assertTrue(isEnabled(next), "Next should be enabled on page 1.");

	    click(next);
	    String secondPage = normalize(getText(output));
	    assertNotEquals(firstPage, secondPage,
	            "Page 2 should not be identical to page 1.");

	    click(previous);
	    String firstPageAgain = normalize(getText(output));
	    assertEquals(firstPage, firstPageAgain,
	            "After going to page 2 and back, page 1 should be exactly restored.");
	    assertFalse(isEnabled(previous), "Previous should be disabled again on page 1.");
	    assertTrue(isEnabled(next), "Next should be enabled again on page 1.");
	}

	@Test
	public void testNewSearchResetsToFirstPageEvenAfterBackingUp() throws Exception {
	    setText(input, "eaten");
	    click(search);
	    String eatenPage1 = normalize(getText(output));

	    clickMultiple(next, 4);
	    assertTrue(isEnabled(previous), "Previous should be enabled after advancing several pages.");

	    clickMultiple(previous, 2);
	    assertTrue(isEnabled(previous), "Previous should still be enabled after backing up from deep in the results.");

	    setText(input, "golden");
	    click(search);

	    assertFalse(isEnabled(previous), "A brand new search should always start at page 1.");
	    assertTrue(isEnabled(next), "Golden should have multiple pages.");

	    String goldenPage1 = normalize(getText(output));
	    assertNotEquals(eatenPage1, goldenPage1,
	            "A new search should replace the old results instead of reusing stale page content.");

	    setText(input, "eaten");
	    click(search);
	    String eatenPage1Again = normalize(getText(output));

	    assertEquals(eatenPage1, eatenPage1Again,
	            "Repeating the same search later should return to the original page 1 results.");
	    assertFalse(isEnabled(previous), "Repeated search should also restart on page 1.");
	}

	private String normalize(String s) {
	    return s.replaceAll("\\s+", "")
	            .replace(" ", "")
	            .trim();
	}
	// ------------------------------------------------------------------
	// Helper methods.

	private void clickMultiple(JButton button, int times) throws Exception {
		for (int i = 0; i < times; i++) {
			click(button);
		}
	}

	private int countOccurrences(String text, String wordToCount) {
		int occurrences = 0;
		int index = 0;
		while (index < text.length() && (index = text.indexOf(wordToCount, index)) >= 0) {
			occurrences++;
			index += wordToCount.length();
		}
		return occurrences;
	}

	private static Container getRootContainer(BibleReaderApp app) {
		if (app instanceof Container) {
			return (Container) app;
		}
		throw new IllegalStateException("BibleReaderApp is not a Container; adjust getRootContainer().");
	}

	private static <T extends Component> T findByName(Container root, Class<T> type, String name) {
		if (root == null) {
			return null;
		}

		for (Component c : root.getComponents()) {
			if (type.isInstance(c) && name.equals(c.getName())) {
				return type.cast(c);
			}
			if (c instanceof Container) {
				T found = findByName((Container) c, type, name);
				if (found != null) {
					return found;
				}
			}
		}
		return null;
	}

	private static void setText(JTextField field, String text) throws Exception {
		runOnEdtAndWait(() -> field.setText(text));
		waitForEdt();
	}

	private static void click(JButton button) throws Exception {
		runOnEdtAndWait(button::doClick);
		waitForEdt();
	}

	private static String getText(JEditorPane pane) throws Exception {
		return callOnEdtAndWait(pane::getText);
	}

	private static boolean isEnabled(JButton button) throws Exception {
		return callOnEdtAndWait(button::isEnabled);
	}

	private static void waitForEdt() throws Exception {
		runOnEdtAndWait(() -> {
			// intentionally empty; flush pending EDT work
		});
	}

	private static void runOnEdtAndWait(ThrowingRunnable r) throws Exception {
		if (SwingUtilities.isEventDispatchThread()) {
			r.run();
			return;
		}
		FutureTask<Void> task = new FutureTask<>(() -> {
			r.run();
			return null;
		});
		SwingUtilities.invokeAndWait(task);
		task.get();
	}

	private static <T> T callOnEdtAndWait(Callable<T> c) throws Exception {
		if (SwingUtilities.isEventDispatchThread()) {
			return c.call();
		}
		FutureTask<T> task = new FutureTask<>(c);
		SwingUtilities.invokeAndWait(task);
		return task.get();
	}

	@FunctionalInterface
	private interface ThrowingRunnable {
		void run() throws Exception;
	}
}