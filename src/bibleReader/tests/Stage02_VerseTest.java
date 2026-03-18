package bibleReader.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bibleReader.model.BookOfBible;
import bibleReader.model.Reference;
import bibleReader.model.Verse;
/**
 * Official tests for the Verse class for Stage 2.
 * 
 * @author Chuck Cusack, February 5, 2013.
 */
public class Stage02_VerseTest {
	private Verse ruth1_1;
	private Verse ruth1_1_DiffContents;
	private Verse gen1_1;
	private Verse rev1_1;
	private Verse anotherReferenceToRuth1_1;
	private Verse ruth1_1duplicate;
	private Verse ruth1_2;
	private Verse ruth2_1;
	private Verse ruth2_2;
	private Verse john1_1;
	private Verse eph3_4;
	private Verse eph3_5;
	private Verse eph3_5b;
	private Verse eph3_6;
	private Verse exo5_9;
	private Verse kings2_3_4;
	private Verse kings2_3_4b;
	private Verse ruth3_4;

    @BeforeEach
    public void setUp() throws Exception {
		ruth1_1 = new Verse(new Reference(BookOfBible.Ruth, 1, 1), "I am not ruth 1:1");
		ruth1_1_DiffContents = new Verse(new Reference(BookOfBible.Ruth, 1, 1), "I don't match the other ruth");
		gen1_1 = new Verse(new Reference(BookOfBible.Genesis, 1, 1), "Genny 1");
		rev1_1 = new Verse(new Reference(BookOfBible.Revelation, 1, 1), "Not rev");

		// For tests needing references to the same object
		anotherReferenceToRuth1_1 = ruth1_1;

		// For tests needing objects that are equal and/or using different
		// constructors
		ruth1_1duplicate = new Verse(BookOfBible.Ruth, 1, 1, "I am not ruth 1:1");

		// For tests needing objects that only differ in 1 place.
		ruth1_2 = new Verse(new Reference(BookOfBible.Ruth, 1, 2), "r12");
		ruth2_1 = new Verse(new Reference(BookOfBible.Ruth, 2, 1), "r21");
		ruth2_2 = new Verse(new Reference(BookOfBible.Ruth, 2, 2), "22");
		john1_1 = new Verse(new Reference(BookOfBible.John, 1, 1), "j11");

		// For tests needing verses that are close to each other.
		eph3_4 = new Verse(new Reference(BookOfBible.Ephesians, 3, 4), "I have same string as you do");
		eph3_5 = new Verse(new Reference(BookOfBible.Ephesians, 3, 5), "I have same string as you do");
		eph3_5b = new Verse(new Reference(BookOfBible.Ephesians, 3, 5), "I have same string as you do");
		eph3_6 = new Verse(new Reference(BookOfBible.Ephesians, 3, 6), "Don't worry.");
		exo5_9 = new Verse(BookOfBible.Exodus, 5, 9, "I have same string as you do");

		kings2_3_4 = new Verse(BookOfBible.Kings2,3,4,"I say stuff");
		kings2_3_4b = new Verse(BookOfBible.Kings2,3,4,"I say different stuff");
		ruth3_4 = new Verse(BookOfBible.Ruth,3,4,"I'm a verse in Ruth");
	}

	@Test
	public void testConstructorReferenceString() {
		assertEquals("I have same string as you do", eph3_4.getText());
		assertEquals(new Reference(BookOfBible.Ephesians, 3, 4), eph3_4.getReference());
	}

	@Test
	public void testConstructorBookOfBibleIntIntString() {
		Verse v = new Verse(BookOfBible.Colossians, 2, 4, "The cake is a lie.");
		assertEquals("The cake is a lie.", v.getText());
		assertEquals(new Reference(BookOfBible.Colossians, 2, 4), v.getReference());
	}

	@Test
	public void testGetReference() {
		Reference r11 = new Reference(BookOfBible.Ruth, 1, 1);
		assertEquals(r11, ruth1_1.getReference());
	}

	@Test
	public void testGetText() {
		// Notice I am explicitly specifying the strings that should be the
		// results.
		assertEquals("I am not ruth 1:1", ruth1_1.getText());
		assertEquals("I have same string as you do", eph3_4.getText());
	}

	@Test
	public void testToString() {
		// We'll just test a few of these.
		assertEquals("Ruth 1:1 I am not ruth 1:1", ruth1_1.toString());
		assertEquals("Ruth 1:1 I don't match the other ruth", ruth1_1_DiffContents.toString());
		assertEquals("Ephesians 3:5 I have same string as you do", eph3_5.toString());
	}

	@Test
	public void testEqualsEasy() {
		// Does it work given the same variable?
		assertTrue(ruth1_1.equals(ruth1_1));

		// Does it work when given two variables that refer to the same object?
		assertTrue(ruth1_1.equals(anotherReferenceToRuth1_1));

		// Does it work given variables referring to different objects that have
		// the same contents?
		// Note: I created Ruth 1:1 using several different constructors, so
		// this also helps test that the constructors work properly.
		assertTrue(ruth1_1duplicate.equals(ruth1_1));
		assertTrue(ruth1_1.equals(ruth1_1duplicate));

		// IMPORTANT! Make sure to do these sort of tests.
		// Test a case where the text is different.
		assertFalse(ruth1_1duplicate.equals(ruth1_1_DiffContents));

		// The reference is different but the text is the same.
		assertFalse(eph3_4.equals(eph3_5));
		assertFalse(exo5_9.equals(eph3_4));

		// Almost the same, but different in one place (To make sure we are
		// using all of the fields in the equals method).
		assertFalse(ruth1_1.equals(ruth1_2));
		assertFalse(ruth1_1.equals(ruth2_1));
		assertFalse(ruth1_1.equals(john1_1));

		// totally different everything
		assertFalse(ruth1_1.equals(eph3_4));
		assertFalse(eph3_6.equals(gen1_1));
	}
	@Test
	public void testEqualsNotUsingDoubleEqualsOnTextField() {
		Verse ruth3_4b = new Verse(BookOfBible.Ruth,3,4,new String("I'm a verse in Ruth"));
		assertTrue(ruth3_4b.equals(ruth3_4));

		Verse kings2_3_4b = new Verse(BookOfBible.Kings2,3,4,"I say stuff");
		assertTrue(kings2_3_4.equals(kings2_3_4));
	}

	@Test
	public void testEqualsHarder() {
		// Need to make sure equals doesn't crash if it is given something
		// that isn't a Reference.
		// The first two examples demonstrates why they can't use
		// toString().equals(other.toString())
		assertFalse(eph3_4.equals("Ephesians 3:4 I have same string as you do"));
		assertFalse(ruth1_1.equals("Ruth 1:1 I am not ruth 1:1"));
		assertFalse(ruth1_1.equals("Cake"));

		// Other non-compatible objects.
		assertFalse(eph3_4.equals(5));
		assertFalse(eph3_4.equals(new Object()));
		assertFalse(eph3_4.equals(new Reference(BookOfBible.Ephesians, 3, 4)));
	}
	@Test
	public void testEqualsMoreCompletely() {
		// Another test for objects that should be equal.
		// Here the verse and chapter are different.
		Verse v = new Verse(BookOfBible.Ephesians, 3, 4, "I have same string as you do");
		assertTrue(v.equals(eph3_4));
		assertTrue(eph3_4.equals(v));

		// Now a bunch of cases of Verses with unequal References.
		// These just make sure Verse doesn't re-implement the Reference part of the
		// equals method and mess it up.
		// Different just in book.
		assertFalse(ruth3_4.equals(eph3_4));
		assertFalse(eph3_4.equals(ruth3_4));

		// Different just in Chapter
		assertFalse(ruth2_1.equals(ruth1_1));
		assertFalse(ruth1_1.equals(ruth2_1));

		// Swapped chapter and verse.
		assertFalse(ruth2_1.equals(ruth1_2));
		assertFalse(ruth1_2.equals(ruth2_1));

		// Different in verse.
		assertFalse(eph3_4.equals(eph3_5));
		assertFalse(eph3_5.equals(eph3_4));
	}
	
	@Test
	public void testHashCode() {
		// See the comments in testHashCode in Reference.
		assertTrue(ruth1_1.hashCode() == anotherReferenceToRuth1_1.hashCode());
		assertTrue(ruth1_1.hashCode() == ruth1_1.hashCode());
		assertTrue(ruth1_1.hashCode() == ruth1_1duplicate.hashCode());
		
		Verse eph3_4b = new Verse(BookOfBible.Ephesians,3,4,"I have same string as you do");
		assertTrue(eph3_4.hashCode()== eph3_4b.hashCode());
	}

	@Test
	public void testCompareTo() {
		// We should only test that the result is ==0, <0, or >0 unless we know
		// the exact implementation of the method. We should not assume it will
		// return exactly 1 or -1.

		// All of these have identical references and text, so they should be
		// the same.
		assertTrue(ruth1_1.compareTo(ruth1_1) == 0);
		assertTrue(ruth1_1.compareTo(ruth1_1duplicate) == 0);
		assertTrue(ruth1_1.compareTo(anotherReferenceToRuth1_1) == 0);
		assertTrue(eph3_5.compareTo(eph3_5b) == 0);
		assertTrue(eph3_4.compareTo(eph3_4) == 0);

		// These have the same reference, but different text, so they should
		// not be the same. Since the references are the same, the order will
		// depend on the text, which is a String. Lexicographically,
		// "I am not.." comes before "I don't...", so here is what we expect:
		assertTrue(ruth1_1.compareTo(ruth1_1_DiffContents) < 0);
		assertTrue(ruth1_1_DiffContents.compareTo(ruth1_1) > 0);

		// Now we test verses that are the same in all but one field.
		// This helps us ensure that all of the fields are being properly
		// used to compare them.

		// Same book and chapter, different verse.
		assertTrue(ruth1_1.compareTo(ruth1_2) < 0);
		assertTrue(ruth1_2.compareTo(ruth1_1) > 0);
		assertTrue(ruth2_1.compareTo(ruth2_2) < 0);
		assertTrue(ruth2_2.compareTo(ruth2_1) > 0);

		// Same book, different chapter, same verse
		assertTrue(ruth1_1.compareTo(ruth2_1) < 0);
		assertTrue(ruth2_1.compareTo(ruth1_1) > 0);

		// Different books, same chapter and verse.
		assertTrue(gen1_1.compareTo(rev1_1) < 0);
		assertTrue(rev1_1.compareTo(gen1_1) > 0);

		// Try a few more for kicks.
		assertTrue(eph3_4.compareTo(eph3_5) < 0);
		assertTrue(eph3_5.compareTo(eph3_6) < 0);
		assertTrue(eph3_6.compareTo(eph3_5) > 0);
		assertTrue(eph3_5.compareTo(eph3_4) > 0);
		
		assertTrue(gen1_1.compareTo(eph3_5) < 0);
		assertTrue(eph3_5.compareTo(gen1_1) > 0);

		assertTrue(ruth1_1.compareTo(eph3_5) < 0);
		assertTrue(eph3_5.compareTo(ruth1_1) > 0);

		assertTrue(eph3_5.compareTo(eph3_5) == 0);
		assertTrue(eph3_4.compareTo(eph3_4) == 0);
		
		// And a few more.
		assertTrue(kings2_3_4.compareTo(eph3_5)<0);
		assertTrue(eph3_6.compareTo(kings2_3_4)>0);
		assertTrue(ruth3_4.compareTo(john1_1)<0);
		assertTrue(john1_1.compareTo(ruth2_2)>0);
		assertTrue(kings2_3_4.compareTo(kings2_3_4b)>0);
		
	}

	@Test
	public void testSameReferencePositive() {
		// These verses are the same everywhere.
		assertTrue(ruth1_1.sameReference(ruth1_1duplicate));

		// These have the same reference, but different text.
		assertTrue(ruth1_1.sameReference(ruth1_1_DiffContents));

	}

	@Test
	public void testSameReferenceNegative() {
		// It should fail verses with different references, even if the text is
		// the same.
		assertFalse(eph3_4.sameReference(eph3_5));
		assertFalse(eph3_5.sameReference(eph3_4));
		// This could have more tests, but for now this will do.
	}

}
