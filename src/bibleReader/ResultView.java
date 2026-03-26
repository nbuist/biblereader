package bibleReader;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import bibleReader.model.BibleReaderModel;
import bibleReader.model.Reference;
import bibleReader.model.Verse;
import bibleReader.model.VerseList;

/**
 * The display panel for the Bible Reader.
 * 
 * @author cusack
 * @author Aleks Rutins
 * @author noahbuist
 */
public class ResultView extends JPanel {

	private BibleReaderModel model;
	private JEditorPane output = new JEditorPane();
	private JLabel stats = new JLabel();

	/**
	 * Construct a new ResultView and set its model to myModel. It needs to model to
	 * look things up.
	 * 
	 * @param myModel The model this view will access to get information.
	 */
	public ResultView(BibleReaderModel myModel) {

		model = myModel;

		output.setContentType("text/html");
		output.setEditable(false);

		stats.setHorizontalAlignment(SwingConstants.CENTER);

		setLayout(new BorderLayout(1, 1));

		add(new JScrollPane(output));
		add(stats, BorderLayout.SOUTH);
	}

	public void updateSearchStats(String searchText, int nResults) {
		stats.setText(nResults + " verses found containing '" + searchText + "'");
	}

	/**
	 * Load and display a list of verses directly. You probably want
	 * {@link loadReferences}.
	 * 
	 * @param verses The verses to display.
	 */
	// public void loadVerses(VerseList verses) {
	// StringBuilder html = new
	// StringBuilder("<table><tr><th>Content</th><th>Location</th></tr>");
	// for (Verse v : verses) {
	// html.append("<tr><td>");
	// html.append(v.getText());
	// html.append("</td><td>");
	// html.append(v.getReference().toString());
	// html.append("</td></tr>");
	// }
	// html.append("</table>");
//
//		output.setText(html.toString());
//
//		output.setCaretPosition(0); // scroll to top
//	}

	/**
	 * Load and display a list of references.
	 * 
	 * @param version The version of the Bible to load from.
	 * @param refs    The references to display.
	 */
	//public void loadReferences(String version, ArrayList<Reference> refs) {
	//	loadReferencesMultiView(refs);
	//}

	public void loadReferencesMultiView(ArrayList<Reference> refs) {
		StringBuilder html = new StringBuilder("<table><tr><th>Verse</th><th>ASV</th><th>ESV</th><th>KJV</th></tr>");
		for (Reference r : refs) {
			/**
			 * refs asv esv kjv
			 * 
			 */
			html.append("<tr>");

			html.append("<td>");
			html.append(r.toString());
			html.append("</td>");

			html.append("<td>");
			html.append(model.getText("ASV", r));
			html.append("</td>");

			html.append("<td>");
			html.append(model.getText("ESV", r));
			html.append("</td>");

			html.append("<td>");
			html.append(model.getText("KJV", r));
			html.append("</td>");

			html.append("</tr>");
		}
		
		html.append("</table>");
		output.setText(html.toString());
	    output.setCaretPosition(0);

	}
}