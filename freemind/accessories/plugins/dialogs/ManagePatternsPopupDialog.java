/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2006  Christian Foltin <christianfoltin@users.sourceforge.net>
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
/*$Id: ManagePatternsPopupDialog.java,v 1.1.2.4.2.7 2006-11-26 10:21:15 dpolivaev Exp $*/

package accessories.plugins.dialogs;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.forms.factories.ButtonBarFactory;

import freemind.common.XmlBindingTools;
import freemind.common.PropertyControl.TextTranslator;
import freemind.controller.actions.generated.instance.Pattern;
import freemind.main.Tools;
import freemind.modes.StylePatternFactory;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.dialogs.StylePatternFrame;
import freemind.modes.mindmapmode.dialogs.StylePatternFrame.StylePatternFrameType;

/** */
public class ManagePatternsPopupDialog extends JDialog implements
		TextTranslator, KeyListener {
	private static Integer sLastSelectedIndex = null;

	private static final String STACK_PATTERN_FRAME = "PATTERN";

	private static final String EMPTY_FRAME = "EMPTY_FRAME";

	private Integer mLastSelectedPatternIndex = null;

	private final class PatternListSelectionListener implements
			ListSelectionListener {

		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting())
				return;
			// save old list:
			writePatternBackToModel();
			JList theList = (JList) e.getSource();
			if (theList.isSelectionEmpty()) {
				mCardLayout.show(mRightStack, EMPTY_FRAME);
			} else {
				int index = theList.getSelectedIndex();
				setLastSelectedPatternIndex(new Integer(index));
				// write pattern:
				Pattern p = mPatternListModel.getPatternAt(index);
				mStylePatternFrame.setPatternList(mPatternListModel
						.getPatternList());
				mStylePatternFrame.setPattern(p);
				mCardLayout.show(mRightStack, STACK_PATTERN_FRAME);
			}
		}
	}

	protected final class PatternListModel implements ListModel {
		private final List mPatternList;

		private final List mListeners;

		public PatternListModel(List patternList) {
			// we take a copy of the list as it may came from the patterns xml
			// element and would be read-only
			this.mPatternList = new Vector(patternList);
			this.mListeners = new Vector();
		}

		public int getSize() {
			return mPatternList.size();
		}

		/**
		 * @return the name of the pattern belonging to index.
		 */
		public Object getElementAt(int index) {
			return getPatternAt(index).getName();
		}

		/**
		 * @return the pattern belonging to index.
		 */
		public Pattern getPatternAt(int index) {
			return ((Pattern) mPatternList.get(index));
		}

		public void addListDataListener(ListDataListener l) {
			mListeners.add(l);
		}

		public void removeListDataListener(ListDataListener l) {
			mListeners.remove(l);
		}

		public List getPatternList() {
			return mPatternList;
		}

		public void removePattern(int index) {
			if (index < 0 || index >= mPatternList.size()) {
				throw new IllegalArgumentException(
						"try to delete in pattern list with an index out of range: "
								+ index);
			}
			mPatternList.remove(index);
			for (Iterator iter = mListeners.iterator(); iter.hasNext();) {
				ListDataListener listener = (ListDataListener) iter.next();
				listener.intervalRemoved(new ListDataEvent(mList,
						ListDataEvent.INTERVAL_REMOVED, index, index));
			}
		}

		public void addPattern(Pattern newPattern, int selectedIndex) {
			mPatternList.add(selectedIndex, newPattern);
			for (Iterator iter = mListeners.iterator(); iter.hasNext();) {
				ListDataListener listener = (ListDataListener) iter.next();
				listener.intervalRemoved(new ListDataEvent(mList,
						ListDataEvent.INTERVAL_ADDED, selectedIndex,
						selectedIndex));
			}
		}

		public Pattern getPatternByName(String name) {
			for (Iterator iter = mPatternList.iterator(); iter.hasNext();) {
				Pattern pattern = (Pattern) iter.next();
				if (pattern.getName().equals(name)) {
					return pattern;
				}
			}
			return null;
		}

		public void add(int i, Object object) {
			if (object instanceof String) {
				String patternName = (String) object;
				Pattern correspondingPattern = getPatternByName(patternName);
				if (correspondingPattern != null) {
					addPattern(correspondingPattern, i);
				}
			}
		}

		public void remove(int i) {
			removePattern(i);
		}
	}

	public static final int CANCEL = -1;

	public static final int OK = 1;

	private int result = CANCEL;

	private javax.swing.JPanel jContentPane = null;

	private MindMapController mController;

	private JButton jCancelButton;

	private JButton jOKButton;

	private CardLayout mCardLayout;

	private JPanel mRightStack;

	private PatternListModel mPatternListModel;

	private JPopupMenu popupMenu;

	private StylePatternFrame mStylePatternFrame;

	private JList mList;

	private accessories.plugins.dialogs.ArrayListTransferHandler mArrayListHandler;

	/**
	 * This is the default constructor
	 */
	public ManagePatternsPopupDialog(JFrame caller, MindMapController controller) {
		super(caller);
		this.mController = controller;
		List patternList = new Vector();
		try {
			patternList = StylePatternFactory.loadPatterns(controller
					.getPatternReader());
		} catch (Exception e) {
freemind.main.Resources.getInstance().logException(			e);
			JOptionPane.showMessageDialog(this, getDialogTitle(), controller
					.getText("accessories/plugins/ManagePatterns.not_found"),
					JOptionPane.ERROR_MESSAGE);
		}
		initialize(patternList);
	}

	/**
	 * This method initializes this
	 * 
	 * 
	 * @return void
	 */
	private void initialize(List patternList) {
		this.setTitle(getDialogTitle());
		JPanel contentPane = getJContentPane(patternList);
		this.setContentPane(contentPane);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				cancelPressed();
			}
		});
		Action action = new AbstractAction() {

			public void actionPerformed(ActionEvent arg0) {
		        cancelPressed();
			}
		};
		Tools.addEscapeActionToDialog(this, action);
		// recover latest index:
		if (sLastSelectedIndex != null) {
			int lastIndex = sLastSelectedIndex.intValue();
			if (lastIndex < mPatternListModel.getSize()) {
				mList.setSelectedIndex(lastIndex);
			}
		}
	}

	/**
	 */
	private String getDialogTitle() {
		return mController
				.getText("accessories/plugins/ManagePatterns.dialog.title");
	}

	private void close() {
		this.dispose();

	}

	private void okPressed() {
		result = OK;
		writePatternBackToModel();
		close();
	}

	private void cancelPressed() {
		result = CANCEL;
		close();
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane(List patternList) {
		if (jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new GridBagLayout());
			mList = new JList();
			mArrayListHandler = new ArrayListTransferHandler();
			mList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			mPatternListModel = new PatternListModel(patternList);
			mList.setModel(mPatternListModel);
			mList.setTransferHandler(mArrayListHandler);
			mList.setDragEnabled(true);
			jContentPane.add(new JScrollPane(mList), new GridBagConstraints(0,
					0, 1, 1, 1.0, 8.0, GridBagConstraints.WEST,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			mList.addListSelectionListener(new PatternListSelectionListener());
			/* Popup menu */
			popupMenu = new JPopupMenu();
			// popupMenu.add(new JPopupMenu.Separator());
			JMenuItem menuItemAdd = new JMenuItem(mController
					.getText("ManagePatternsPopupDialog.add"));
			popupMenu.add(menuItemAdd);
			menuItemAdd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					addPattern(actionEvent);
				}
			});
			JMenuItem menuItemDuplicate = new JMenuItem(mController
					.getText("ManagePatternsPopupDialog.duplicate"));
			popupMenu.add(menuItemDuplicate);
			menuItemDuplicate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					duplicatePattern(actionEvent);
				}
			});
			JMenuItem menuItemFromNodes = new JMenuItem(mController
					.getText("ManagePatternsPopupDialog.from_nodes"));
			popupMenu.add(menuItemFromNodes);
			menuItemFromNodes.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					insertPatternFromNode(actionEvent);
				}
			});
			popupMenu.add(new JPopupMenu.Separator());
			JMenuItem menuItemRemove = new JMenuItem(mController
					.getText("ManagePatternsPopupDialog.remove"));
			popupMenu.add(menuItemRemove);
			menuItemRemove.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					removePattern(actionEvent);
				}
			});
			mList.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent me) {
					showPopup(mList, me);
				}

				/** For linux */
				public void mousePressed(MouseEvent me) {
					showPopup(mList, me);
				}

				private void showPopup(final JList mList, MouseEvent me) {
					// if right mouse button clicked (or me.isPopupTrigger())
					if (me.isPopupTrigger()
							&& !mList.isSelectionEmpty()
							&& mList.locationToIndex(me.getPoint()) == mList
									.getSelectedIndex()) {
						popupMenu.show(mList, me.getX(), me.getY());
					}
				}
			});

			mCardLayout = new CardLayout();
			mRightStack = new JPanel(mCardLayout);
			mRightStack.add(new JPanel(), EMPTY_FRAME);
			mStylePatternFrame = new StylePatternFrame(this, mController,
					StylePatternFrameType.WITH_NAME_AND_CHILDS);
			mStylePatternFrame.init();
			mStylePatternFrame.addListeners();
			mRightStack.add(new JScrollPane(mStylePatternFrame),
					STACK_PATTERN_FRAME);
			jContentPane.add(mRightStack, new GridBagConstraints(1, 0, 2, 1,
					8.0, 8.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			jContentPane.add(ButtonBarFactory.buildOKCancelBar(getJCancelButton(), getJOKButton()),
					new GridBagConstraints(1, 1, 1, 1,
					1.0, 1.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
					new Insets(0, 0, 0, 0), 0, 0));
//			jContentPane.add(getJOKButton(), new GridBagConstraints(1, 1, 1, 1,
//					1.0, 1.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
//					new Insets(0, 0, 0, 0), 0, 0));
//			jContentPane.add(getJCancelButton(), new GridBagConstraints(2, 1,
//					1, 1, 1.0, 1.0, GridBagConstraints.EAST,
//					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			getRootPane().setDefaultButton(getJOKButton());
		}
		return jContentPane;
	}

	private void addPattern(ActionEvent actionEvent) {
		writePatternBackToModel();
		setLastSelectedPatternIndex(null);
		Pattern newPattern = new Pattern();
		newPattern.setName(searchForNameForNewPattern());
		int selectedIndex = mList.getSelectedIndex();
		mPatternListModel.addPattern(newPattern, selectedIndex);
		mList.setSelectedIndex(selectedIndex);
	}

	private void duplicatePattern(ActionEvent actionEvent) {
		int selectedIndex = mList.getSelectedIndex();
		writePatternBackToModel();
		setLastSelectedPatternIndex(null);
		Pattern oldPattern = mPatternListModel.getPatternAt(selectedIndex);
		// deep copy via xml:
		XmlBindingTools instance = XmlBindingTools.getInstance();
		Pattern newPattern = (Pattern) instance.unMarshall(instance.marshall(oldPattern));
		newPattern.setName(searchForNameForNewPattern());
		mPatternListModel.addPattern(newPattern, selectedIndex);
		mList.setSelectedIndex(selectedIndex);
	}

	private void insertPatternFromNode(ActionEvent actionEvent) {
		int selectedIndex = mList.getSelectedIndex();
		writePatternBackToModel();
		setLastSelectedPatternIndex(null);
		Pattern newPattern = StylePatternFactory.createPatternFromSelected(mController.getSelected(), mController.getSelecteds());
		newPattern.setName(searchForNameForNewPattern());
		mPatternListModel.addPattern(newPattern, selectedIndex);
		mList.setSelectedIndex(selectedIndex);
	}
	
	private String searchForNameForNewPattern() {
		// give it a good name:
		String newName = mController.getText("PatternNewNameProperty");
		// collect names:
		Vector allNames = new Vector();
		for (Iterator iter = mPatternListModel.getPatternList().iterator(); iter
		.hasNext();) {
			Pattern p = (Pattern) iter.next();
			allNames.add(p.getName());
		}
		String toGiveName = newName;
		int i = 1;
		while (allNames.contains(toGiveName)) {
			toGiveName = newName + i;
			++i;
		}
		return toGiveName;
	}
	
	private void removePattern(ActionEvent actionEvent) {
		int selectedIndex = mList.getSelectedIndex();
		setLastSelectedPatternIndex(null);
		mPatternListModel.removePattern(selectedIndex);
		if (mPatternListModel.getSize() > selectedIndex) {
			mList.setSelectedIndex(selectedIndex);
		} else if (mPatternListModel.getSize() > 0 && selectedIndex >= 0) {
			mList.setSelectedIndex(selectedIndex - 1);
		} else {
			// empty
			mList.clearSelection();
		}
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJOKButton() {
		if (jOKButton == null) {
			jOKButton = new JButton();

			jOKButton.setAction(new AbstractAction() {

				public void actionPerformed(ActionEvent e) {
                    okPressed();
				}

			});

			jOKButton.setText(mController
					.getText("ManagePatternsPopupDialog.Save"));
		}
		return jOKButton;
	}

	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJCancelButton() {
		if (jCancelButton == null) {
			jCancelButton = new JButton();
			jCancelButton.setAction(new AbstractAction() {

				public void actionPerformed(ActionEvent e) {
					cancelPressed();
				}
			});
			jCancelButton.setText(mController.getText("cancel"));
		}
		return jCancelButton;
	}

	/**
	 * @return Returns the result.
	 */
	public int getResult() {
		return result;
	}

	public String getText(String pKey) {
		return mController.getText(pKey);
	}

	public List getPatternList() {
		return mPatternListModel.getPatternList();
	}

	private void writePatternBackToModel() {
		if (getLastSelectedPatternIndex() != null) {
			// save pattern:
			Pattern pattern = mPatternListModel
					.getPatternAt(getLastSelectedPatternIndex().intValue());
			Pattern resultPatternCopy = mStylePatternFrame.getResultPattern();
			// check for name change:
			String oldPatternName = pattern.getName();
			String newPatternName = resultPatternCopy.getName();
			if (!(oldPatternName.equals(newPatternName))) {
				// now, let's check, whether or not it is still unique:
				for (Iterator iter = mPatternListModel.getPatternList()
						.iterator(); iter.hasNext();) {
					Pattern otherPattern = (Pattern) iter.next();
					if (otherPattern == pattern) {
						// myself is not regarded:
						continue;
					}
					if (otherPattern.getName().equals(newPatternName)) {
						// duplicate found. What now?
						JOptionPane
								.showMessageDialog(
										this,
										mController
												.getText("ManagePatternsPopupDialog.DuplicateNameMessage"));
					}
				}
			}
			// no duplicates. We search for uses of the old name:
			for (Iterator iter = mPatternListModel.getPatternList().iterator(); iter
					.hasNext();) {
				Pattern otherPattern = (Pattern) iter.next();
				if (otherPattern.getPatternChild() != null
						&& oldPatternName.equals(otherPattern.getPatternChild()
								.getValue())) {
					// change to new name
					otherPattern.getPatternChild().setValue(newPatternName);
				}
			}
			mStylePatternFrame.getResultPattern(pattern);
			// Special case that a pattern that points to itself is renamed:
			if (pattern.getPatternChild() != null
					&& oldPatternName.equals(pattern.getPatternChild()
							.getValue())) {
				pattern.getPatternChild().setValue(newPatternName);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent keyEvent) {
		System.out.println("key pressed: " + keyEvent);
		switch (keyEvent.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			keyEvent.consume();
			cancelPressed();
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent keyEvent) {
		System.out.println("keyReleased: " + keyEvent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent keyEvent) {
		System.out.println("keyTyped: " + keyEvent);
	}

	private void setLastSelectedPatternIndex(Integer mLastSelectedPatternIndex) {
		this.mLastSelectedPatternIndex = mLastSelectedPatternIndex;
		sLastSelectedIndex = mLastSelectedPatternIndex;
	}

	private Integer getLastSelectedPatternIndex() {
		return mLastSelectedPatternIndex;
	}

}
