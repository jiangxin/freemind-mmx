/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
 *
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package freemind.modes.common.actions;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import freemind.main.FreeMind;
import freemind.main.HtmlTools;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.modes.ControllerAdapter;
import freemind.modes.MindMapNode;

public class FindAction extends AbstractAction {
	private final ControllerAdapter controller;

	private ArrayList findNodesUnfoldedByLastFind;

	private MindMapNode findFromNode;

	private String searchTerm;

	private Collection subterms;

	/**
	 * @return Returns the subterms.
	 */
	public Collection getSubterms() {
		return subterms;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public String getFindFromText() {
		String plainNodeText = HtmlTools.htmlToPlain(findFromNode.toString())
				.replaceAll("\n", " ");
		return plainNodeText.length() <= 30 ? plainNodeText : plainNodeText
				.substring(0, 30) + "...";
	}

	private boolean findCaseSensitive;

	private LinkedList findNodeQueue;

	private JDialog mDialog;

	private int mResult;

	private JCheckBox mFindInNotesTooBox;

	private JTextField mSearchField;

	/** This isn't stored and is per map. */
	private String mLastSearchString;

	public FindAction(ControllerAdapter controller) {
		super(controller.getText("find"), new ImageIcon(
				controller.getResource("images/filefind.png")));
		this.controller = controller;
	}

	public void actionPerformed(ActionEvent e) {
		displayDialog();
		if (mResult != JOptionPane.OK_OPTION) {
			return;
		}
		String what = mSearchField.getText();
		if (what == null || what.equals("")) {
			return;
		}
		Collection subterms = breakSearchTermIntoSubterms(what);
		this.searchTerm = what;
		// System.err.println(subterms);
		/* caseSensitive=false */
		boolean found = find(controller.getSelected(), subterms, false);
		controller.getView().repaint();
		if (!found) {
			String messageText = controller.getText("no_found_from");
			String searchTerm = getSearchTermAsEscapedString(messageText);
			controller.getController().informationMessage(
					messageText.replaceAll("\\$1", searchTerm).replaceAll(
							"\\$2", getFindFromText()),
					controller.getView().getSelected());
		}
	}

	private void close(int pResult) {
		mResult = pResult;
		mDialog.setVisible(false);
		mDialog.dispose();
		// Store "find in notes too" value to prop.
		if (pResult == JOptionPane.OK_OPTION) {
			Resources
					.getInstance()
					.getProperties()
					.setProperty(FreeMind.RESOURCES_SEARCH_IN_NOTES_TOO,
							mFindInNotesTooBox.isSelected() ? "true" : "false");
			mLastSearchString = mSearchField.getText();
		}
	}

	void displayDialog() {
		mDialog = null;
		mDialog = new JDialog(controller.getFrame().getJFrame(),
				controller.getText("find"));
		mDialog.setModal(true);
		mDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		AbstractAction cancelAction = new AbstractAction() {

			public void actionPerformed(ActionEvent pE) {
				close(JOptionPane.CANCEL_OPTION);
			}
		};
		AbstractAction okAction = new AbstractAction() {

			public void actionPerformed(ActionEvent pE) {
				close(JOptionPane.OK_OPTION);
			}
		};
		Tools.addEscapeActionToDialog(mDialog, cancelAction);
		mDialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent pE) {
				close(JOptionPane.CANCEL_OPTION);
			}
		});
		Container contentPane = mDialog.getContentPane();
		contentPane.setLayout(new GridBagLayout());
		contentPane.add(new JLabel(controller.getText("find_what")),
				new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
						GridBagConstraints.WEST, GridBagConstraints.BOTH,
						new Insets(5, 5, 0, 0), 0, 0));
		mSearchField = new JTextField(mLastSearchString);
		mSearchField.selectAll();
		mSearchField.setMinimumSize(new Dimension(500, 14));
		contentPane.add(mSearchField, new GridBagConstraints(2, 0, 10, 1, 1.0,
				1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
				new Insets(5, 5, 0, 0), 0, 0));
		ImageIcon findImage = new ImageIcon(Resources.getInstance()
				.getResource("images/filefind_big.png"));
		contentPane.add(new JLabel(findImage), new GridBagConstraints(0, 0, 1,
				2, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
				new Insets(5, 5, 0, 0), 0, 0));
		mFindInNotesTooBox = new JCheckBox(
				controller
						.getText("ExtendedFindDialog.find_search_in_notes_too"));
		mFindInNotesTooBox.setSelected(Resources.getInstance().getBoolProperty(
				FreeMind.RESOURCES_SEARCH_IN_NOTES_TOO));
		Tools.setLabelAndMnemonic(mFindInNotesTooBox, null);
		contentPane.add(mFindInNotesTooBox, new GridBagConstraints(0, 2, 3, 1,
				1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
				new Insets(5, 5, 0, 0), 0, 0));
		JButton okButton = new JButton(
				controller.getText("ExtendedFindDialog.ok"));
		Tools.setLabelAndMnemonic(okButton, null);
		okButton.addActionListener(okAction);
		contentPane.add(okButton, new GridBagConstraints(2, 3, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
						5, 0, 0), 0, 0));
		JButton cancelButton = new JButton(
				controller.getText("ExtendedFindDialog.cancel"));
		Tools.setLabelAndMnemonic(cancelButton, null);
		cancelButton.addActionListener(cancelAction);
		contentPane.add(cancelButton, new GridBagConstraints(3, 3, 1, 1, 1.0,
				1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
				new Insets(5, 5, 0, 0), 0, 0));
		mDialog.getRootPane().setDefaultButton(okButton);
		mDialog.pack();
		Tools.setDialogLocationRelativeTo(mDialog, controller.getSelectedView());
		mDialog.setVisible(true);
	}

	private String getSearchTermAsEscapedString(String messageText) {
		String searchTerm = messageText.startsWith("<html>") ? HtmlTools
				.toXMLEscapedText(getSearchTerm()) : getSearchTerm();
		// Fix for
		// https://sourceforge.net/tracker/?func=detail&aid=3200783&group_id=7118&atid=107118
		// Patch
		// https://sourceforge.net/tracker/?func=detail&aid=3276562&group_id=7118&atid=307118,
		// thanks to the author
		searchTerm = searchTerm.replace("$", "\\$");
		return searchTerm;
	}

	public static class FindNextAction extends AbstractAction {
		private final ControllerAdapter controller;

		private final FindAction find;

		public FindNextAction(ControllerAdapter controller, FindAction find) {
			super(controller.getText("find_next"));
			this.controller = controller;
			this.find = find;
		}

		public void actionPerformed(ActionEvent e) {
			Collection subterms = find.getSubterms();
			if (subterms == null) {
				controller.getController().informationMessage(
						controller.getText("no_previous_find"),
						controller.getView().getSelected());
				return;
			}
			boolean found = find.findNext();
			controller.getView().repaint();
			if (!found) {
				String messageText = controller.getText("no_more_found_from");
				String searchTerm = find
						.getSearchTermAsEscapedString(messageText);
				controller.getController().informationMessage(
						messageText.replaceAll("\\$1", searchTerm).replaceAll(
								"\\$2", find.getFindFromText()),
						controller.getView().getSelected());
			}
		}
	}

	public boolean find(MindMapNode node, Collection subterms,
			boolean caseSensitive) {
		findNodesUnfoldedByLastFind = new ArrayList();
		LinkedList nodes = new LinkedList();
		nodes.addFirst(node);
		findFromNode = node;
		Collection finalizedSubterms;
		if (!caseSensitive) {
			finalizedSubterms = new ArrayList();
			for (Iterator i = subterms.iterator(); i.hasNext();) {
				finalizedSubterms.add(((String) i.next()).toLowerCase());
			}
		} else {
			finalizedSubterms = subterms;
		}
		return find(nodes, finalizedSubterms, caseSensitive);
	}

	private boolean find(LinkedList /* queue of MindMapNode */nodes,
			Collection subterms, boolean caseSensitive) {
		// Precondition: if !caseSensitive then >>what<< is in lowercase.
		boolean searchInNotesToo = Resources.getInstance().getBoolProperty(
				FreeMind.RESOURCES_SEARCH_IN_NOTES_TOO);

		// Fold the path of previously found node
		boolean thereWereNodesToBeFolded = !findNodesUnfoldedByLastFind
				.isEmpty();
		if (!findNodesUnfoldedByLastFind.isEmpty()) {

			// if (false) {
			ListIterator i = findNodesUnfoldedByLastFind
					.listIterator(findNodesUnfoldedByLastFind.size());
			while (i.hasPrevious()) {
				MindMapNode node = (MindMapNode) i.previous();
				try {
					controller.setFolded(node, true);
				} catch (Exception e) {
				}
			}
			findNodesUnfoldedByLastFind = new ArrayList();
		}

		// We implement width-first search.
		while (!nodes.isEmpty()) {
			MindMapNode node = (MindMapNode) nodes.removeFirst();
			// Add children to the queue
			for (ListIterator i = node.childrenUnfolded(); i.hasNext();) {
				nodes.addLast(i.next());
			}

			if (!node.isVisible())
				continue;

			// Bug fix for
			// http://sourceforge.net/tracker/?func=detail&aid=3035387&group_id=7118&atid=107118
			String nodeText = node.toString();
			nodeText = prepareTextContent(caseSensitive, nodeText);
			// End bug fix.
			String noteText = node.getNoteText();
			noteText = prepareTextContent(caseSensitive, noteText);

			boolean found = true;
			boolean foundInNotes = false;
			for (Iterator i = subterms.iterator(); i.hasNext();) {
				if (nodeText.indexOf((String) i.next()) < 0) {
					// Subterm not found
					found = false;
					break;
				}
			}

			if ((!found) && searchInNotesToo) {
				/* now, search the notes. */
				found = true;
				for (Iterator i = subterms.iterator(); i.hasNext();) {
					if (noteText.indexOf((String) i.next()) < 0) {
						// Subterm not found
						found = false;
						break;
					}
				}
				foundInNotes = true;
			}
			if (found) { // Found
				displayNode(node, findNodesUnfoldedByLastFind);
				centerNode(node);
				if (foundInNotes) {
					// TODO: Select text in notes window.
				}
				// Save the state for find next
				this.subterms = subterms;
				findCaseSensitive = caseSensitive;
				findNodeQueue = nodes;
				return true;
			}
		}

		centerNode(findFromNode);
		return false;
	}

	public String prepareTextContent(boolean caseSensitive, String nodeText) {
		if (nodeText == null) {
			nodeText = "";
		}
		if (HtmlTools.isHtmlNode(nodeText)) {
			nodeText = HtmlTools.removeHtmlTagsFromString(nodeText);
			nodeText = HtmlTools.unescapeHTMLUnicodeEntity(nodeText);
		}
		if (!caseSensitive) {
			nodeText = nodeText.toLowerCase();
		}
		return nodeText;
	}

	private Collection breakSearchTermIntoSubterms(String searchTerm) {
		ArrayList subterms = new ArrayList();
		StringBuffer subterm = new StringBuffer();
		int len = searchTerm.length();
		char myChar;
		char previousChar = 'a';
		boolean withinQuotes = false;
		for (int i = 0; i < len; ++i) {
			myChar = searchTerm.charAt(i);
			if (myChar == ' ' && withinQuotes) {
				subterm.append(myChar);
			} else if ((myChar == ' ' && !withinQuotes)) {
				subterms.add(subterm.toString());
				subterm.setLength(0);
			} else if (myChar == '"' && i > 0 && i < len - 1
					&& searchTerm.charAt(i - 1) != ' '
					&& searchTerm.charAt(i + 1) != ' ') {
				// Character " surrounded by non-spaces
				subterm.append(myChar);
			} else if (myChar == '"' && withinQuotes) {
				withinQuotes = false;
			} else if (myChar == '"' && !withinQuotes) {
				withinQuotes = true;
			} else {
				subterm.append(myChar);
			}
			previousChar = myChar;
		}
		subterms.add(subterm.toString());
		return subterms;
	}

	/**
	 * Display a node in the display (used by find and the goto action by arrow
	 * link actions).
	 */
	public void displayNode(MindMapNode node, ArrayList nodesUnfoldedByDisplay) {
		// Unfold the path to the node
		Object[] path = controller.getMap().getPathToRoot(node);
		// Iterate the path with the exception of the last node
		for (int i = 0; i < path.length - 1; i++) {
			MindMapNode nodeOnPath = (MindMapNode) path[i];
			// System.out.println(nodeOnPath);
			if (nodeOnPath.isFolded()) {
				if (nodesUnfoldedByDisplay != null)
					nodesUnfoldedByDisplay.add(nodeOnPath);
				controller.setFolded(nodeOnPath, false);
			}
		}

	}

	public boolean findNext() {
		// Precodition: subterms != null. We check the precodition but give no
		// message.

		// The logic of find next is vulnerable. find next relies on the queue
		// of nodes from previous find / find next. However, between previous
		// find / find next and this find next, nodes could have been deleted
		// or moved. The logic expects that no changes happened, even that no
		// node has been folded / unfolded.

		// You may want to come with more correct solution, but this one
		// works for most uses, and does not cause any big trouble except
		// perhaps for some uncaught exceptions. As a result, it is not very
		// nice, but far from critical and working quite fine.

		if (subterms != null) {
			return find(findNodeQueue, subterms, findCaseSensitive);
		}
		return false;
	}

	/**
	 */
	private void centerNode(MindMapNode node) {
		// Select the node and scroll to it.
		controller.centerNode(node);
	}

}
