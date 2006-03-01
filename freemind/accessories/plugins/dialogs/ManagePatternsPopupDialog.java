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
/*$Id: ManagePatternsPopupDialog.java,v 1.1.2.1 2006-03-01 21:13:28 christianfoltin Exp $*/

package accessories.plugins.dialogs;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
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
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import freemind.common.PropertyControl.TextTranslator;
import freemind.modes.StylePattern;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.dialogs.StylePatternFrame;

/** */
public class ManagePatternsPopupDialog extends JDialog implements
        TextTranslator {

    private final class PatternListSelectionListener implements
            ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting())
                return;

            JList theList = (JList) e.getSource();
            if (theList.isSelectionEmpty()) {
                mCardLayout.show(mRightStack, "");
            } else {
                int index = theList.getSelectedIndex();
                mCardLayout.show(mRightStack, (String) mPatternListModel
                        .getElementAt(index));
            }
        }
    }

    private final class PatternListModel implements ListModel {
        private final List mPatternList;

        private final List mListeners;

        public PatternListModel(List patternList) {
            this.mPatternList = patternList;
            this.mListeners = new Vector();
        }

        public int getSize() {
            return mPatternList.size();
        }

        public Object getElementAt(int index) {
            return ((StylePattern) mPatternList.get(index)).getName();
        }

        public void addListDataListener(ListDataListener l) {
            mListeners.add(l);
        }

        public void removeListDataListener(ListDataListener l) {
            mListeners.remove(l);
        }
    }

    public static final int CANCEL = -1;

    public static final int OK = 1;

    private int result = CANCEL;

    private javax.swing.JPanel jContentPane = null;

    private MindMapController controller;

    private JButton jCancelButton;

    private JButton jOKButton;

    /**
     * The model.
     */
    private List mPatternList;

    private CardLayout mCardLayout;

    private JPanel mRightStack;

    private PatternListModel mPatternListModel;

    private JPopupMenu popupMenu;

    /**
     * This is the default constructor
     */
    public ManagePatternsPopupDialog(JFrame caller, MindMapController controller) {
        super(caller);
        this.controller = controller;
        try {
            mPatternList = StylePattern.loadPatterns(controller
                    .getPatternReader());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, getDialogTitle(), controller
                    .getText("accessories/plugins/ManagePatterns.not_found"),
                    JOptionPane.ERROR_MESSAGE);
        }
        initialize(mPatternList);
    }

    /**
     * This method initializes this
     * 
     * @param patternList
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
    }

    /**
     * @return
     */
    private String getDialogTitle() {
        return controller
                .getText("accessories/plugins/ManagePatterns.dialog.title");
    }

    private void close() {
        this.dispose();

    }

    private void okPressed() {
        result = OK;
        close();
    }

    private void cancelPressed() {
        result = CANCEL;
        close();
    }

    /**
     * This method initializes jContentPane
     * 
     * @param patternList
     * 
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getJContentPane(List patternList) {
        if (jContentPane == null) {
            jContentPane = new javax.swing.JPanel();
            jContentPane.setLayout(new GridBagLayout());
            // add list box:
            final JList mList = new JList();
            mPatternListModel = new PatternListModel(patternList);
            mList.setModel(mPatternListModel);
            jContentPane.add(new JScrollPane(mList), new GridBagConstraints(0,
                    0, 1, 1, 2.0, 8.0, GridBagConstraints.WEST,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            mList.addListSelectionListener(new PatternListSelectionListener());
            popupMenu = new JPopupMenu();
            JMenuItem jmi1;
            popupMenu.add(jmi1 = new JMenuItem("Add"));
            popupMenu.add(new JPopupMenu.Separator());
            JMenuItem jmi2;
            popupMenu.add(jmi2 = new JMenuItem("Clear"));

            mList.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent me) {
                    // if right mouse button clicked (or me.isPopupTrigger())
                    if (SwingUtilities.isRightMouseButton(me)
                            && !mList.isSelectionEmpty()
                            && mList.locationToIndex(me.getPoint()) == mList
                                    .getSelectedIndex()) {
                        popupMenu.show(mList, me.getX(), me.getY());
                    }
                }
            });

//            jmi1.addActionListener(this);
//            jmi2.addActionListener(this);

            mCardLayout = new CardLayout();
            mRightStack = new JPanel(mCardLayout);
            mRightStack.add(new JPanel(), "");
            for (Iterator iter = patternList.iterator(); iter.hasNext();) {
                StylePattern pattern = (StylePattern) iter.next();
                StylePatternFrame stylePatternFrame = new StylePatternFrame(
                        this);
                stylePatternFrame.init();
                stylePatternFrame.setPattern(pattern);
                mRightStack.add(stylePatternFrame, pattern.getName());
            }
            jContentPane.add(mRightStack, new GridBagConstraints(1, 0, 2, 1,
                    2.0, 8.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            jContentPane.add(getJOKButton(), new GridBagConstraints(0, 1, 1, 1,
                    1.0, 1.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                    new Insets(0, 0, 0, 0), 0, 0));
            jContentPane.add(getJCancelButton(), new GridBagConstraints(1, 1,
                    1, 1, 1.0, 1.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            getRootPane().setDefaultButton(getJOKButton());
        }
        return jContentPane;
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

            jOKButton.setText(controller.getText("ok"));
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
            jCancelButton.setText(controller.getText("cancel"));
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
        return controller.getText(pKey);
    }

}
