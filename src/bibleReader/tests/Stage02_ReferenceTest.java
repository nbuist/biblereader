package bibleReader.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bibleReader.model.BookOfBible;
import bibleReader.model.Reference;
import bibleReader.model.Verse;

public class Stage02_ReferenceTest {

	private Reference ruth1_1;
	private Reference gen1_1;
	private Reference rev1_1;
	private Reference alsoRefersToruth1_1;
	private Reference ruth1_1AnotherOne;
	private Reference ruth1_2;
	private Reference ruth2_1;
	private Reference ruth2_2;
	private Reference ruth3_4;
	private Reference john1_1;
	private Reference eph3_4;
	private Reference eph3_5;
	private Reference eph3_6;
	private Reference kings2_3_4;

	@BeforeEach
	public void setUp() throws Exception {
		ruth1_1 = new Reference(BookOfBible.Ruth, 1, 1);
		gen1_1 = new Reference(BookOfBible.Genesis, 1, 1);
		rev1_1 = new Reference(BookOfBible.Revelation, 1, 1);

		// For tests needing references to the same object
		alsoRefersToruth1_1 = ruth1_1;

		// For tests needing objects that are equal and/or using different
		// constructors
		ruth1_1AnotherOne = new Reference(BookOfBible.Ruth, 1, 1);

		// For tests needing objects that only different in 1 place.
		ruth1_2 = new Reference(BookOfBible.Ruth, 1, 2);
		ruth2_1 = new Reference(BookOfBible.Ruth, 2, 1);
		ruth2_2 = new Reference(BookOfBible.Ruth, 2, 2);
		john1_1 = new Reference(BookOfBible.John, 1, 1);

		// For tests needing verses that are close to each other.
		eph3_4 = new Reference(BookOfBible.Ephesians, 3, 4);
		eph3_5 = new Reference(BookOfBible.Ephesians, 3, 5);
		eph3_6 = new Reference(BookOfBible.Ephesians, 3, 6);

		ruth3_4 = new Reference(BookOfBible.Ruth, 3, 4);
		// For tests needing space in book name
		kings2_3_4 = new Reference(BookOfBible.Kings2, 3, 4);
	}

	@Test
	public void testGetBook() {
		// getBook returns a String representation of the book.
		assertEquals("Ruth", ruth1_1.getBook());
		assertEquals("Ruth", ruth1_1AnotherOne.getBook());
		assertEquals("Ephesians", eph3_5.getBook());
		assertEquals("2 Kings", kings2_3_4.getBook());
	}

	@Test
	public void testGetBookOfBible() {
		// getBookOfBible returns a BookOfBible corresponding to the book,
		// not a String.
		assertEquals(BookOfBible.Ruth, ruth1_1.getBookOfBible());
		assertEquals(BookOfBible.Ruth, ruth1_1AnotherOne.getBookOfBible());
		assertEquals(BookOfBible.Ephesians, eph3_6.getBookOfBible());
	}

	@Test
	public void testGetChapter() {
		// Testing getChapter and getVerse.
		assertEquals(3, kings2_3_4.getChapter());
		assertEquals(1, ruth1_2.getChapter());
	}

	@Test
	public void testGetVerse() {
		assertEquals(4, kings2_3_4.getVerse());
		assertEquals(2, ruth1_2.getVerse());
	}

	@Test
	public void testToString() {
		// Notice that I am explicitly specifying what the string should be.
		assertEquals("Ruth 1:1", ruth1_1.toString());

		// Try one that has a space in the book name.
		assertEquals("2 Kings 3:4", kings2_3_4.toString());
	}

	@Test
	public void testEqualsEasyStuff() {
		// This is sort of testing the constructors as well
		// since we are constructing some of the Ruth verses
		// with different constructors.

		// These should all be equal since the fields should be the same.
		assertTrue(ruth1_1.equals(alsoRefersToruth1_1));
		assertTrue(ruth1_1.equals(ruth1_1));
		assertTrue(ruth1_1.equals(ruth1_1AnotherOne));

		// Here we are testing References that are the
		// same in 2 of the 3 places to make sure
		// equals is using all of the fields.
		assertFalse(ruth1_1.equals(ruth1_2));
		assertFalse(ruth1_1.equals(ruth2_1));
		assertFalse(ruth1_1.equals(john1_1));

		// Different in all fields.
		assertFalse(eph3_4.equals(john1_1));
	}

	@Test
	public void testEqualsMoreCompletely() {
		// Another test for objects that should be equal.
		// Here the verse and chapter are different.
		Reference r = new Reference(BookOfBible.Ephesians, 3, 4);
		assertTrue(r.equals(eph3_4));
		assertTrue(eph3_4.equals(r));

		// Now a bunch of cases of unequal References.
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
	public void testEqualsWithOtherObjects() {
		// Need to make sure equals doesn't crash if it is given something
		// that isn't a Reference.
		assertFalse(eph3_4.equals(5));
		assertFalse(eph3_4.equals(new Object()));
		assertFalse(eph3_4.equals(new Verse(BookOfBible.Ephesians, 3, 4,
				"I am Eph 3:4")));
	}

	@Test
	public void testEqualsNotUsingToString() {
		// Testing an incorrect way to implement equals.
		assertFalse(eph3_4.equals("Ephesians 3:4"));
		assertFalse(ruth1_1.equals("Ruth 1:1"));
	}

	@Test
	public void testHashCode() {
		// There are a few things we might test here. The obvious ones are:
		// 1. Does hashCode compute the correct value?
		// 2. Does hashCode obey the contract w/equals (if a.equals(b), then
		// a.hashCode()==b.hashCode())
		// Since we don't know exactly how it will be computed, we will just
		// test the second part.
		//
		// NOTE: We do NOT test that if !a.equals(b), then
		// a.hashCode()!=b.hashCode().
		// There is no expectation of what hashCode should do for different
		// objects other than it should attempt to create different values for
		// different objects as much as possible. Since there is a limit to the
		// number of possible hashCodes, it is generally impossible to
		// prevent two different objects form having the same hashCode.
		//
		// Note: For semantic reasons, I am not using assertEquals, since
		// neither of the values we are comparing are the "expected" value.
		assertTrue(ruth1_1.hashCode() == alsoRefersToruth1_1.hashCode());
		assertTrue(ruth1_1.hashCode() == ruth1_1.hashCode());
		assertTrue(ruth1_1.hashCode() == ruth1_1AnotherOne.hashCode());
		assertTrue(eph3_4.hashCode() == new Reference(BookOfBible.Ephesians, 3,
				4).hashCode());
		assertTrue(gen1_1.hashCode() == new Reference(BookOfBible.Genesis, 1, 1)
				.hashCode());
	}

	@Test
	public void testcompareTo() {
		// See Stage02VerseTest for important comments about testing compareTo.

		// Should all be the same.
		assertTrue(ruth1_1.compareTo(ruth1_1) == 0);
		assertTrue(ruth1_1.compareTo(ruth1_1AnotherOne) == 0);
		assertTrue(ruth1_1.compareTo(alsoRefersToruth1_1) == 0);

		// Same book and chapter, different verse.
		assertTrue(ruth1_1.compareTo(ruth1_2) < 0);
		assertTrue(ruth1_2.compareTo(ruth1_1) > 0);
		assertTrue(ruth2_1.compareTo(ruth2_2) < 0);
		assertTrue(ruth2_2.compareTo(ruth2_1) > 0);

		// Same book, different chapter
		assertTrue(ruth1_1.compareTo(ruth2_1) < 0);
		assertTrue(ruth2_1.compareTo(ruth1_1) > 0);

		// Different books.
		assertTrue(gen1_1.compareTo(rev1_1) < 0);
		assertTrue(rev1_1.compareTo(gen1_1) > 0);

	}

	@Test
	public void testcompareToMoreTests() {
		assertTrue(gen1_1.compareTo(eph3_5) < 0);
		assertTrue(eph3_5.compareTo(gen1_1) > 0);

		assertTrue(ruth1_1.compareTo(eph3_5) < 0);
		assertTrue(eph3_5.compareTo(ruth1_1) > 0);

		// Try a few more for kicks.
		assertTrue(eph3_4.compareTo(eph3_5) < 0);
		assertTrue(eph3_5.compareTo(eph3_6) < 0);
		assertTrue(eph3_6.compareTo(eph3_5) > 0);
		assertTrue(eph3_5.compareTo(eph3_4) > 0);

		assertTrue(eph3_5.compareTo(eph3_5) == 0);
		assertTrue(eph3_4.compareTo(eph3_4) == 0);
		
		// And a few more.
		assertTrue(kings2_3_4.compareTo(eph3_5)<0);
		assertTrue(eph3_6.compareTo(kings2_3_4)>0);
		assertTrue(ruth3_4.compareTo(john1_1)<0);
		assertTrue(john1_1.compareTo(ruth2_2)>0);
		
	}

	@Test
	public void compareToNotUsingToStringTest() {
		Reference d1_3 = new Reference(BookOfBible.Deuteronomy, 1, 3);
		Reference d10_1 = new Reference(BookOfBible.Deuteronomy, 10, 1);
		Reference d3_10 = new Reference(BookOfBible.Deuteronomy, 3,10);
		Reference d3_11 = new Reference(BookOfBible.Deuteronomy, 3,11);
		Reference d3_2 = new Reference(BookOfBible.Deuteronomy, 3,2);
		
		assertTrue(d1_3.compareTo(d10_1) < 0);
		assertTrue(d10_1.compareTo(d1_3) > 0);

		assertTrue(d3_10.compareTo(d3_11) < 0);
		assertTrue(d3_11.compareTo(d3_10) > 0);

		assertTrue(d3_2.compareTo(d3_10)<0);
		assertTrue(d3_10.compareTo(d3_2)>0);

		Reference king2_21_34 = new Reference(BookOfBible.Kings2, 21, 34);
		Reference king1_21_34 = new Reference(BookOfBible.Kings1, 21, 34);
		Reference king2_32_21 = new Reference(BookOfBible.Kings2, 34, 21);
		Reference king2_2_13 = new Reference(BookOfBible.Kings2, 2, 13);
		Reference king2_2_8 = new Reference(BookOfBible.Kings2, 2,8);
		
		assertTrue(king2_2_8.compareTo(king2_2_13)<0);
		assertTrue(king2_2_13.compareTo(king2_2_8)>0);
		
		assertTrue(king1_21_34.compareTo(king2_21_34)<0);
		assertTrue(king2_21_34.compareTo(king1_21_34)>0);
		
		assertTrue(king2_32_21.compareTo(king1_21_34)>0);
		
		assertTrue(kings2_3_4.compareTo(king2_32_21)<0);
		
	}

	// ----------------------------------------------------------------------------------------
	// Here are some examples of badly written tests. Read through the tests and
	// the comments.
	// Notice that this test is not annotated. This is so that the test won't
	// run.
	public void badTests() {
		// Here are the sorts of things that you should not do:

		// Don't use Integer.valueOf arbitrarily.
		// For instance, if you want to compare two integers, don't do this:
		assertTrue(Integer.valueOf(ruth1_1.getChapter()).equals(
				Integer.valueOf(ruth1_1AnotherOne.getChapter())));
		// The previous statement just create Integer objects based on the ints
		// and compares them using equals.
		// But == works perfectly fine with ints.
		// Instead, do this (this is a good test):
		assertTrue(ruth1_1.getChapter() == ruth1_1AnotherOne.getChapter());

		// There is a test for whether or not something is true (or false).
		// So, don't do:
		assertEquals(true, ruth1_1.equals(ruth1_1AnotherOne));
		assertEquals(false, ruth1_1.equals(ruth1_2));
		// Instead do:
		assertTrue(ruth1_1.equals(ruth1_1AnotherOne));
		assertFalse(ruth1_1.equals(ruth1_1AnotherOne));

		// When you use the version of the assertXYZ methods that takes a String
		// as the first argument,
		// that message is displayed when the test is failed, so it should be
		// used to indicate something
		// specific about the test and perhaps why it failed.
		// So don't do things like this:
		assertTrue(ruth1_1.equals(ruth1_1AnotherOne), "This is testing equals");

		// Make sure you are testing apples to apples.
		// For instance, don't do this:
		assertEquals(ruth1_1.getBook(), ruth1_1AnotherOne.getBookOfBible());
		// This will always return false since one is a String and one is a
		// BookOfBible.

		// The assertSame and assertNotSame are for use to determine whether or
		// not two variables
		// refer to the same object. It should not be used to compare primitive
		// types.
		// So don't do this:
		assertSame(1, ruth1_1.getChapter());
		// Never ever ever use assertSame or assertNotSame on primitive types!
		// Ever!
		// Because of Autoboxing, etc., it may or may not work as you expect.

		// Don't test for what something SHOULDN'T be.
		// Don't do this:
		assertFalse(1 == ruth2_1.getChapter());
		// Of course it shouldn't be 1. But it shouldn't be 3 or -23 either.
		// What is important is that it SHOULD be 2. So do this:
		assertEquals(2, ruth2_1.getChapter());

		// Don't use the same object twice if you don't mean to. For instance,
		// this is not a good test of getReference since the Reference luke is
		// used to create the verse and test against:
		Reference luke = new Reference(BookOfBible.Luke, 2, 3);
		Verse lukeVerse = new Verse(luke, "I am not really like 2:3.");
		assertEquals(luke, lukeVerse.getReference());
		// Instead do this:
		Verse lukeVerse2 = new Verse(new Reference(BookOfBible.Luke, 2, 3),
				"I am not really like 2:3.");
		Reference luke2 = new Reference(BookOfBible.Luke, 2, 3);
		assertEquals(luke2, lukeVerse2.getReference());

	}
}
