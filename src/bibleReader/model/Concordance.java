package bibleReader.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Concordance is a class which implements a concordance for a Bible. In other
 * words, it allows the easy lookup of all references which contain a given
 * word.
 * 
 * @author Chuck Cusack, March 2013 (Provided the interface)
 * @author noahbuist, April 2026 (Provided the implementation details)
 */
public class Concordance {
	// Add fields here. (I actually only needed one field.)
	private TreeMap<String, ArrayList<Reference>> index;
	private VerseList verses;

	/**
	 * Construct a concordance for the given Bible.
	 */
	public Concordance(Bible bible) {
		index = new TreeMap<>();
		verses = bible.getAllVerses(); // adjust to your Bible's actual method
		for (Verse verse : verses) {
	        if (verse == null) continue;

	        ArrayList<String> words = extractWords(verse.getText());

	        for (String word : words) {
	            addToIndex(word, verse.getReference());
	        }
	    }
	}
	
	
	/**
	 * Return the list of references to verses that contain the word 'word'
	 * (ignoring case) in the version of the Bible that this concordance was created
	 * with.
	 * 
	 * @param word a single word (no spaces, etc.)
	 * @return the list of References of verses from this version that contain the
	 *         word, or an empty list if no verses contain the word.
	 */

	public ArrayList<Reference> getReferencesContaining(String word) {
		if (word == null || word.trim().isEmpty() || word.contains(" ")) {
	        return new ArrayList<>();
	    }
	    ArrayList<Reference> result = index.get(word.toLowerCase());
	    if (result != null) {
	        return new ArrayList<>(result); // return a copy!
	    } else {
	        return new ArrayList<>();
	    }
	}


	/**
	 * Given an array of Strings, where each element of the array is expected to be
	 * a single word (with no spaces, etc., but ignoring case), return a
	 * ArrayList<Reference> containing all of the verses that contain <i>all of the
	 * words</i>.
	 * 
	 * @param words A list of words.
	 * @return An ArrayList<Reference> containing references to all of the verses
	 *         that contain all of the given words, or an empty list if
	 */
	public ArrayList<Reference> getReferencesContainingAll(ArrayList<String> words) {
		if(words.isEmpty() || words == null) {
			return new ArrayList<>();
		}
		// Puts word into first index of result
		// boolean used as dummy value, not needed when returning 
		TreeMap<Reference, Boolean> result = new TreeMap<>();
		
		
		for (Reference ref : getReferencesContaining(words.get(0))) {
	        result.put(ref, true);
	    }
		
		for(int i = 1; i < words.size(); i++) {
			// initialized here to prevent other from holding old refs
			TreeMap<Reference, Boolean> other = new TreeMap<>();
			for(Reference ref : getReferencesContaining(words.get(i))) {
				other.put(ref, true);
			}
			// keeping only refs in both
			for(Reference ref : new ArrayList<>(result.keySet())) {
				if(!other.containsKey(ref)) {
					result.remove(ref);
				}
			}
			
		}
		
		
		return new ArrayList<>(result.keySet());
	}
	
	public static ArrayList<String> extractWords(String text) {
		text = text.toLowerCase();
		// Removes a few HTML tags (relevant to ESV) and 's at end of words.
	     // Replaces them with space so words around them don’t get squished
	     // together.  Notice the two types of apostrophe—each is used in a
	     // different version.
		text = text.replaceAll("(<sup>[,\\w]*?</sup>|'s|’s|&#\\w*;)", " ");
		// Remove commas. This should help us match numbers better.
		text = text.replaceAll(",", "");
		String[] words = text.split("\\W+");
		ArrayList<String> toRet = new ArrayList<String>(Arrays.asList(words));
		toRet.remove("");
		return toRet;
	}
	
	/**
	 * Helper method that adds a reference to the index for the given word.
	 * If the word is not yet in the index, a new list is created for it.
	 * Duplicate references for the same verse are not added.
	 * 
	 * @param word the word to index
	 * @param ref the reference to add
	 */
	private void addToIndex(String word, Reference ref) {
	    if (!index.containsKey(word)) {
	        index.put(word, new ArrayList<>());
	    }
	    ArrayList<Reference> list = index.get(word);
	    if (list.isEmpty() || !list.get(list.size() - 1).equals(ref)) {
	        list.add(ref);
	    }
	}
	

	

}
