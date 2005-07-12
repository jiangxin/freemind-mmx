/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2004  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 *
 * Created on 02.05.2004
 */
/*$Id: EditNodeTextField.java,v 1.1.4.3.6.1 2005-07-12 15:41:18 dpolivaev Exp $*/

package freemind.view.mindmapview;

import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import freemind.main.Tools;
import freemind.modes.ModeController;

/**
 * @author foltin
 *
 */
public class EditNodeTextField extends EditNodeBase {

    private KeyEvent firstEvent;

    private JTextField textfield;

    public EditNodeTextField(
        final NodeView node,
        final String text,
        final KeyEvent firstEvent,
        ModeController controller,
        EditControl editControl) {
        super(node, text, controller, editControl);
        this.firstEvent = firstEvent;
    }

    public void show() {
            textfield =
                (getText().length() < 8)
                    ? new JTextField(
                        getText(),
                        8) //Make fields for short texts editable
    : new JTextField(getText());

        // Set textFields's properties

        /* fc, 12.10.2003: the following method is not correct. Even more with the zoom factors!*/
        int linkIconWidth = 16;
        int textFieldBorderWidth = 2;
        int cursorWidth = 1;
        int xOffset =
            -1 * textFieldBorderWidth  - 1;
        int yOffset = -1; // Optimized for Windows style; basically ad hoc
        int widthAddition =
            2 * textFieldBorderWidth
                + cursorWidth
                + 2;
        int heightAddition = 2;
        if (getNode().getModel().getLink() != null) {
            xOffset += linkIconWidth;
            widthAddition -= linkIconWidth;
        }
        if (getNode().getModel().getIcons().size() != 0) {
            // fc, 24.9.2003 full ok for the moment, that an icon has the same size as the link icon.
            xOffset += linkIconWidth * getNode().getModel().getIcons().size();
            widthAddition -= linkIconWidth;
        }
        /* fc, 12.10.2003: end buggy method*/

        // minimal width for input field of leaf or folded node (PN)
        final int MINIMAL_LEAF_WIDTH = 150;
        final int MINIMAL_WIDTH = 50;

        int xSize = getNode().getWidth() + widthAddition;
        int xExtraWidth = 0;
        if (MINIMAL_LEAF_WIDTH > xSize
            && (getNode().getModel().isFolded()
                || !getNode().getModel().hasChildren())) {
            // leaf or folded node with small size
            xExtraWidth = MINIMAL_LEAF_WIDTH - xSize;
            xSize = MINIMAL_LEAF_WIDTH; // increase minimum size
            if (getNode().isLeft()) { // left leaf
                xExtraWidth = -xExtraWidth;
                textfield.setHorizontalAlignment(JTextField.RIGHT);
            }
        } else if (MINIMAL_WIDTH > xSize) {
            // opened node with small size
            xExtraWidth = MINIMAL_WIDTH - xSize;
            xSize = MINIMAL_WIDTH; // increase minimum size
            if (getNode().isLeft()) { // left node
                xExtraWidth = -xExtraWidth;
                textfield.setHorizontalAlignment(JTextField.RIGHT);
            }
        }

        textfield.setSize(xSize, getNode().getMainView().getHeight() + heightAddition);
        textfield.setFont(getNode().getFont());
        textfield.setForeground(getNode().getForeground());
        textfield.setSelectedTextColor(getNode().getForeground());
        textfield.setSelectionColor(getModeController().getSelectionColor());
        // textField.selectAll(); // no selection on edit (PN)

        final int INIT = 0;
        final int EDIT = 1;
        final int CANCEL = 2;
        final Tools.IntHolder eventSource = new Tools.IntHolder();
        eventSource.setValue(INIT);

        // listener class
        class TextFieldListener
            implements KeyListener, FocusListener, MouseListener {

            public void focusGained(FocusEvent e) {
                // the first time the edit field gains a focus
                // process the predefined first key (if any)

                if (eventSource.getValue() == INIT) {
                    eventSource.setValue(EDIT);
                    if (firstEvent instanceof KeyEvent) {
                        KeyEvent firstKeyEvent = (KeyEvent) firstEvent;
                        if (firstKeyEvent.getKeyChar()
                            == KeyEvent.CHAR_UNDEFINED) {
                            // for the char_undefined the scenario with dispatching 
                            // doesn't work => hard code dispatching :-(
                            // // dispatch action key events as it came
                            // textField.dispatchEvent(firstKeyEvent);

                            // dispatch 2 known events (+ special for insert:) (hardcoded)
                            switch (firstKeyEvent.getKeyCode()) {
                                case KeyEvent.VK_HOME :
                                    textfield.setCaretPosition(0);
                                    break;
                                case KeyEvent.VK_END :
                                    textfield.setCaretPosition(
                                        textfield.getText().length());
                                    break;
                            }
                        } else {
                            // or create new "key type" event for printable key
                            KeyEvent keyEv;
                            keyEv =
                                new KeyEvent(
                                    textfield,	//firstKeyEvent.getComponent(),
                                    KeyEvent.KEY_TYPED,
                                    firstKeyEvent.getWhen(),
                                    firstKeyEvent.getModifiers(),
                                    KeyEvent.VK_UNDEFINED,
                                    firstKeyEvent.getKeyChar(),
                                    KeyEvent.KEY_LOCATION_UNKNOWN);
                            textfield.selectAll(); // to enable overwrite
                            textfield.dispatchEvent(keyEv);
                        }
                    } // 1st key event defined
                } // first focus
            } // focus gained

            public void focusLost(FocusEvent e) {

                // %%% open problems:
                // - adding of a child to the rightmost node
                // - scrolling while in editing mode (it can behave just like other viewers)
                // - block selected events while in editing mode

                if (e == null) { // can be when called explicitly
                    getEditControl().ok(textfield.getText());
                    hideMe();
                    eventSource.setValue(CANCEL); // disallow real focus lost
                } else if (eventSource.getValue() != CANCEL) {
                    // always confirm the text if not yet
                    getEditControl().ok(textfield.getText());
                    hideMe();
                }
            }

            public void keyPressed(KeyEvent e) {

                // add to check meta keydown by koh 2004.04.16
                if (e.isAltDown() || e.isControlDown() || e.isMetaDown()) {
                    return;
                }

                boolean commit = true;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE :
                        commit = false;
                    case KeyEvent.VK_ENTER :
                        e.consume();

                        eventSource.setValue(CANCEL);
                        // do not process loose of focus
                        if (commit) {
                            getEditControl().ok(textfield.getText());
                        } else {
                            getEditControl().cancel();
                        }
                        hideMe();
                        // hack: to keep the focus
                        break;

                    case KeyEvent.VK_SPACE :
                        e.consume();
                }
            }
            public void keyTyped(KeyEvent e) {
            }
            public void keyReleased(KeyEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
            }
            public void mouseEntered(MouseEvent e) {
            }
            public void mouseExited(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
                conditionallyShowPopup(e);
            }

            public void mouseReleased(MouseEvent e) {
                conditionallyShowPopup(e);
            }

            private void conditionallyShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    JPopupMenu popupMenu = new EditPopupMenu(textfield);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    e.consume();
                }
            }

        }

        // create the listener
        final TextFieldListener textFieldListener = new TextFieldListener();

        // Add listeners
        this.textFieldListener = textFieldListener;
        textfield.addFocusListener(textFieldListener);
        textfield.addKeyListener(textFieldListener);
        textfield.addMouseListener(textFieldListener);

        // screen positionining ---------------------------------------------

        // SCROLL if necessary
        getView().scrollNodeToVisible(getNode(), xExtraWidth);

        // NOTE: this must be calculated after scroll because the pane location changes
        Point frameScreenLocation =
            getFrame().getLayeredPane().getLocationOnScreen();
        Point nodeScreenLocation = getNode().getLocationOnScreen();

        int xLeft =
            (int) (nodeScreenLocation.getX()
                - frameScreenLocation.getX()
                + xOffset);
        if (xExtraWidth < 0) {
            xLeft += xExtraWidth;
        }

        textfield.setLocation(
            xLeft,
            (int) (nodeScreenLocation.getY()
                - frameScreenLocation.getY()
                + yOffset));

        getFrame().getLayeredPane().add(textfield); // 2000);
        getFrame().repaint();

        SwingUtilities.invokeLater(new Runnable() { // PN 0.6.2
            public void run() {
                textfield.requestFocus();
            }
        });
    }

    private void hideMe() {
        getFrame().getLayeredPane().remove(textfield);
        getFrame().repaint(); //  getLayeredPane().repaint();
        textFieldListener = null;
    }
    


}
