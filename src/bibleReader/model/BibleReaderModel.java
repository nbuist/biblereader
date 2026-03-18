package bibleReader.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.regex.Pattern;
/**
 * The model of the Bible Reader. It stores the Bibles and has methods for searching for verses based on words or
 * references.
 * 
 * @author cusack
 * @author noahbuist, aleksrutins
 */
public class BibleReaderModel implements MultiBibleModel {

	// ---------------------------------------------------------------------------
	// TODO Add more fields here: Stage 5
	// You need to store several Bible objects.
	// You may need to store other data as well.
	private LinkedHashMap<String, Bible> bibles;
	
	/**
	 * Default constructor. You probably need to instantiate objects and do other assorted things to set up the model.
	 */
	public BibleReaderModel() {
		bibles = new LinkedHashMap<>();
	}

	@Override
	public String[] getVersions() {
		return bibles.keySet().toArray(new String[0]);
	}

	@Override
	public int getNumberOfVersions() {
		// TODO Implement me: Stage 5
		return bibles.size();
	}

	@Override
	public void addBible(Bible bible) {
		if(bible != null) {
			bibles.put(bible.getVersion(), bible);
		}
	}

	@Override
	public Bible getBible(String version) {
		return bibles.get(version);
	}

	@Override
	public ArrayList<Reference> getReferencesContaining(String words) {
		if(bibles.isEmpty()) {
			return new ArrayList<>();
		}
		
		ArrayList<Reference> result = null;
		for(Bible bible : bibles.values()){
			ArrayList<Reference> refs = bible.getReferencesContaining(words);
			if(result == null) {
				result = new ArrayList<>(refs);
			}
			else {
				result.retainAll(refs);
			}
		}
		if(result != null) {
			return result;
		}
		else {
			return new ArrayList<>();
		}
	}
	
	@Override
	public VerseList getVerses(String version, ArrayList<Reference> references) {
		Bible bible = bibles.get(version);
		if(bible == null || references == null) {
			return new VerseList(version, "");
		}
		return bible.getVerses(references);
	}
	//---------------------------------------------------------------------

	@Override
	public String getText(String version, Reference reference) {
		Bible bible = bibles.get(version);
		if(bible == null) {
			return null;
		}
		return bible.getVerseText(reference);
	}

	@Override
	public ArrayList<Reference> getReferencesForPassage(String reference) {
		if(bibles.isEmpty()) {
			return new ArrayList<>();
		}
		
		ArrayList<Reference> result = null;
		for(Bible bible : bibles.values()){
			ArrayList<Reference> refs = ReferenceMatcher.parse(bible, reference).getReferences(bible);
			if(result == null) {
				result = new ArrayList<>(refs);
			}
			else {
				result.retainAll(refs);
			}
		}
		if(result != null) {
			return result;
		}
		else {
			return new ArrayList<>();
		}
	}

	// -----------------------------------------------------------------------------
	// The next set of methods are for use by the getReferencesForPassage method above. 
	// After it parses the input string it will call one of these.
	//
	// These methods should be somewhat easy to implement. They are kind of delegate
	// methods in that they call a method on the Bible class to do most of the work.
	// However, they need to do so for every version of the Bible stored in the model.
	// and combine the results.
	//
	// Once you implement one of these, the rest of them should be fairly straightforward.
	// Think before you code, get one to work, and then implement the rest based on
	// that one.
	// -----------------------------------------------------------------------------

	@Override
	public ArrayList<Reference> getVerseReferences(BookOfBible book, int chapter, int verse) {
		LinkedHashSet<Reference> result = new LinkedHashSet<>();
		for(Bible bible : bibles.values()) {
			Reference ref = new Reference(book, chapter, verse);
			if(bible.isValid(ref)) {
				result.add(ref);
			}
		}
		return new ArrayList<>(result);
	}

	@Override
	public ArrayList<Reference> getPassageReferences(Reference startVerse, Reference endVerse) {
		LinkedHashSet<Reference> result = new LinkedHashSet<>();
		for(Bible bible : bibles.values()) {
			result.addAll(bible.getReferencesInclusive(startVerse, startVerse));
		}
		return new ArrayList<>(result);
	}

	@Override
	public ArrayList<Reference> getBookReferences(BookOfBible book) {
		LinkedHashSet<Reference> result = new LinkedHashSet<>();
		for(Bible bible : bibles.values()) {
			result.addAll(bible.getReferencesForBook(book));
		}
		return new ArrayList<>(result);
	}

	@Override
	public ArrayList<Reference> getChapterReferences(BookOfBible book, int chapter) {
		LinkedHashSet<Reference> result = new LinkedHashSet<>();
		for(Bible bible : bibles.values()) {
			result.addAll(bible.getReferencesForChapter(book, chapter));
		}
		return new ArrayList<>(result);
	}

	@Override
	public ArrayList<Reference> getChapterReferences(BookOfBible book, int chapter1, int chapter2) {
		LinkedHashSet<Reference> result = new LinkedHashSet<>();
		for(Bible bible : bibles.values()) {
			result.addAll(bible.getReferencesForChapters(book, chapter1, chapter2));
		}
		return new ArrayList<>(result);
	}

	@Override
	public ArrayList<Reference> getPassageReferences(BookOfBible book, int chapter, int verse1, int verse2) {
		LinkedHashSet<Reference> result = new LinkedHashSet<>();
		for(Bible bible : bibles.values()) {
			result.addAll(bible.getReferencesForPassage(book, chapter, verse1, verse2));
		}
		return new ArrayList<>(result);
	}

	@Override
	public ArrayList<Reference> getPassageReferences(BookOfBible book, int chapter1, int verse1, int chapter2, int verse2) {
		LinkedHashSet<Reference> result = new LinkedHashSet<>();
		for(Bible bible : bibles.values()) {
			result.addAll(bible.getReferencesForPassage(book, chapter1, verse1, chapter2, verse2));
		}
		return new ArrayList<>(result);
	}

	//------------------------------------------------------------------
	// These are the better searching methods.
	// 
	@Override
	public ArrayList<Reference> getReferencesContainingWord(String word) {
		// TODO Implement me: Stage 12
		return null;
	}

	@Override
	public ArrayList<Reference> getReferencesContainingAllWords(String words) {
		// TODO Implement me: Stage 12
		return null;
	}

	@Override
	public ArrayList<Reference> getReferencesContainingAllWordsAndPhrases(String words) {
		// TODO Implement me: Stage 12
		return null;
	}
}
