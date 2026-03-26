package bibleReader;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import bibleReader.model.ArrayListBible;
import bibleReader.model.Bible;
import bibleReader.model.BibleReaderModel;
import bibleReader.model.Reference;
import bibleReader.model.VerseList;

/**
 * The main class for the Bible Reader Application.
 * @version 1.2.0 | March 12th 2026
 * @author cusack
 * @author Noah Buist
 * @author noahbuist, aleksrutins
 */
public class BibleReaderApp extends JFrame implements ActionListener {
	// Change these to suit your needs.
	public static final int width = 600;
	public static final int height = 600;

	public static void main(String[] args) {
		new BibleReaderApp();
	}

	// Fields
	private BibleReaderModel model;
	private ResultView resultView;

	private JTextField searchField;
	private JComboBox<String> bibleSelect;

	// TODO add more fields as necessary

	/**
	 * Default constructor. We may want to replace this with a different one.
	 */
	public BibleReaderApp(){
		// There is no guarantee that this complete/correct, so take a close
		// look to make sure you understand what this code is doing in case
		// you need to modify or add to it.
		model = new BibleReaderModel(); // For now call the default constructor. This might change.
		File kjvFile = new File("files/kjv.atv");
		File asvFile = new File("files/asv.xmv");
		File esvFile = new File("files/esv.atv");
		
		
		VerseList verses = null;
		try {
			verses = BibleIO.readBible(kjvFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Bible kjv = new ArrayListBible(verses);
		try {
			verses = BibleIO.readBible(asvFile);
		}catch(Exception e) {
			e.printStackTrace();
		}
		Bible asv = new ArrayListBible(verses);
		try {
			verses = BibleIO.readBible(esvFile);
		}catch(Exception e) {
			e.printStackTrace();
		}
		Bible esv = new ArrayListBible(verses);

		
		
		
		model.addBible(esv);
		model.addBible(kjv);
		model.addBible(asv);
		for (String v : model.getVersions()) {
		    System.out.println("Loaded version: [" + v + "]");
		}
		resultView = new ResultView(model);

		setupGUI();
		pack();
		setSize(width, height);

		// So the application exits when you click the "x".
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	/**
	 * Set up the main GUI. Make sure you don't forget to put resultView somewhere!
	 */
	private void setupGUI() {
		setTitle("Bible Reader Stage 9");
		setLayout(new BorderLayout(5, 5));

		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		fileMenu.add(exitItem);
		menuBar.add(fileMenu);

		JMenu helpMenu = new JMenu("Help");
		JMenuItem aboutItem = new JMenuItem("About");
		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,
						"Bible Reader - Stage 5\n" + "Version 1.1" + "\nAuthor: Noah Buist + Aleks Rutins\n\n"
								+ "A simple Bible reading and search application.",
						"About Bible Reader", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		helpMenu.add(aboutItem);
		menuBar.add(helpMenu);
		setJMenuBar(menuBar);

		JPanel controls = new JPanel(new BorderLayout(5, 5));

		bibleSelect = new JComboBox<>();
		for (String v : model.getVersions())
			bibleSelect.addItem(v);
		controls.add(bibleSelect, BorderLayout.WEST);

		searchField = new JTextField();
		controls.add(searchField);

		JPanel buttonPanel = new JPanel();
		JButton searchBtn = new JButton("Search");
		searchBtn.addActionListener(this);
		buttonPanel.add(searchBtn);

		JButton passageBtn = new JButton("Passage");
		passageBtn.addActionListener(this);
		buttonPanel.add(passageBtn);

		controls.add(buttonPanel, BorderLayout.EAST);

		Container content = getContentPane();
		content.add(controls, BorderLayout.NORTH);
		content.add(resultView);

		// The stage numbers below may change, so make sure to pay attention to
		// what the assignment says.
		// TODO Add 2nd version on display: Stage ?
		// TODO Limit the displayed search results to 20 at a time: Stage ?
		// TODO Add 3rd versions on display: Stage ?
		// TODO Format results better: Stage ?
		// TODO Display cross references for third version: Stage ?
		// TODO Save/load search results: Stage ?

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Search for the text.
		if (e.getActionCommand().equals("Search")) {
			ArrayList<Reference> results = model.getReferencesContaining(searchField.getText());
			resultView.updateSearchStats(searchField.getText(), results.size());
			resultView.loadReferencesMultiView(results);
		} else if (e.getActionCommand().equals("Passage")) {
			ArrayList<Reference> results = model.getReferencesForPassage(searchField.getText());
			resultView.updateSearchStats(searchField.getText(), results.size());
			resultView.loadReferencesMultiView(results);
		}
	}

}
