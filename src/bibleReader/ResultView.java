package bibleReader;

import java.awt.BorderLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import bibleReader.model.BibleReaderModel;
import bibleReader.model.Reference;

/**
 * The display panel for the Bible Reader.
 * 
 * @author cusack
 * @author Aleks Rutins
 * @author noahbuist
 */
public class ResultView extends JPanel {

	public static final int PAGE_SIZE = 20;
	
	private BibleReaderModel model;
    public JEditorPane output = new JEditorPane();
    private JLabel stats = new JLabel();
    private JLabel pageLabel = new JLabel();

    public JButton nextButton = new JButton("Next");
    public JButton previousButton = new JButton("Previous");

    private ArrayList<Reference> currentRefs = new ArrayList<>();
    private int currentPage = 0;
    private boolean isPassage = false;
    private String lastSearchText = "";
	

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
        output.setName("OutputEditorPane");

        nextButton.setName("NextButton");
        previousButton.setName("PreviousButton");

		stats.setHorizontalAlignment(SwingConstants.CENTER);
		pageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		nextButton.setEnabled(false);
        previousButton.setEnabled(false);
        
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage++;
                renderPage();
            }
        });

        previousButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage--;
                renderPage();
            }
        });
        
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.add(previousButton, BorderLayout.WEST);
        navPanel.add(pageLabel, BorderLayout.CENTER);
        navPanel.add(nextButton, BorderLayout.EAST);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(stats, BorderLayout.NORTH);
        bottomPanel.add(navPanel, BorderLayout.SOUTH);

		setLayout(new BorderLayout(1, 1));

		add(new JScrollPane(output));
		add(bottomPanel, BorderLayout.SOUTH);
	}

	public void updateSearchStats(String searchText, int nResults) {
		lastSearchText = searchText;
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
		isPassage = false;
        currentRefs = refs;
        currentPage = 0;
        renderPage();

	}
	
	public void loadPassageMultiView(ArrayList<Reference> refs) {
        isPassage = true;
        currentRefs = refs;
        currentPage = 0;
        renderPage();
    }
	
	private void renderPage() {
		int totalPages = (int) Math.ceil((double) currentRefs.size() / PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;
        
        
        int from = currentPage * PAGE_SIZE;
        int to = Math.min(from + PAGE_SIZE, currentRefs.size());
        ArrayList<Reference> pageRefs = new ArrayList<>(currentRefs.subList(from, to));
        
        // controls updating
        previousButton.setEnabled(currentPage > 0);
        nextButton.setEnabled(currentPage < totalPages - 1);
        pageLabel.setText("Page " + (currentPage + 1) + " of " + totalPages);
        
        StringBuilder html = new StringBuilder();
        
        if (isPassage && !pageRefs.isEmpty()) {
            // Passage header: "John 3:3-23" style
            Reference first = pageRefs.get(0);
            Reference last = pageRefs.get(pageRefs.size() - 1);
            String header;
            if (first.getBookOfBible().equals(last.getBookOfBible())
                    && first.getChapter() == last.getChapter()) {
                header = first.toString() + "-" + last.getVerse();
            } else if (first.getBookOfBible().equals(last.getBookOfBible())) {
                header = first.toString() + "-" + last.getChapter() + ":" + last.getVerse();
            } else {
                header = first.toString() + "-" + last.toString();
            }
            html.append("<center><b>").append(header).append("</b></center>");

            // Paragraph form grouped by chapter
            renderPassageParagraphs(html, pageRefs);
        }else {
        	html.append("<table><tr><th>Verse</th><th>ASV</th><th>ESV</th><th>KJV</th></tr>");
            for (Reference r : pageRefs) {
                html.append("<tr><td>").append(r.toString()).append("</td>");
                for (String version : new String[]{"ASV", "ESV", "KJV"}) {
                    String text = model.getText(version, r);
                    if (!lastSearchText.isBlank()) {
                        text = text.replaceAll("(?i)" + java.util.regex.Pattern.quote(lastSearchText),
                                "<b>$0</b>");
                    }
                    html.append("<td>").append(text).append("</td>");
                }
                html.append("</tr>");
            }
            html.append("</table>");
        }
        output.setText(html.toString());
        output.setCaretPosition(0);
	}
	private void renderPassageParagraphs(StringBuilder html, ArrayList<Reference> refs) {
        int lastChapter = -1;
        StringBuilder paragraph = new StringBuilder();
        for (Reference r : refs) {
            if (r.getChapter() != lastChapter) {
                if (paragraph.length() > 0) {
                    html.append("<p>").append(paragraph).append("</p><p></p>");
                    paragraph = new StringBuilder();
                }
                lastChapter = r.getChapter();
                // First verse of chapter: show chapter number instead of verse
                paragraph.append("<sup>").append(r.getChapter()).append("</sup> ");
            } else {
                paragraph.append("<sup>").append(r.getVerse()).append("</sup> ");
            }
            // Use KJV as primary, fall back to ASV, then ESV
            String text = model.getText("KJV", r);
            if (text.isBlank()) text = model.getText("ASV", r);
            if (text.isBlank()) text = model.getText("ESV", r);
            paragraph.append(text).append(" ");
        }
        if (paragraph.length() > 0) {
            html.append("<p>").append(paragraph).append("</p>");
        }
    }
}