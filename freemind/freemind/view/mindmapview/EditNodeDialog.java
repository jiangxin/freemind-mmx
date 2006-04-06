/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
/*$Id: EditNodeDialog.java,v 1.1.4.1.16.1 2006-04-06 21:15:07 dpolivaev Exp $*/

package freemind.view.mindmapview;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import freemind.main.Tools;
import freemind.modes.ModeController;

/**
 * @author foltin
 *
 */
public class EditNodeDialog extends EditNodeBase {


	private KeyEvent firstEvent;

	/** Private variable to hold the last value of the "Enter confirms" state.*/
	private static Tools.BooleanHolder booleanHolderForConfirmState;

	public EditNodeDialog(
		final NodeView node,
		final String text,
		final KeyEvent firstEvent,
		ModeController controller,
		EditControl editControl) {
			super(node, text, controller, editControl);
			this.firstEvent = firstEvent;

	}
	public void show() {
		final JDialog dialog =
			new JDialog(
				(JFrame) getFrame(),
				getText("edit_long_node"),
		/*modal=*/
		true);

		final JTextArea textArea = new JTextArea(getText());
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		// wrap around words rather than characters
		if (firstEvent != null) {
			switch (firstEvent.getKeyCode()) {
				case KeyEvent.VK_HOME :
					textArea.setCaretPosition(0);
					break;

				default :
					textArea.setCaretPosition(getText().length());
					break;
			}
		} else {
			textArea.setCaretPosition(getText().length());
		}

		final JScrollPane editorScrollPane = new JScrollPane(textArea);
		editorScrollPane.setVerticalScrollBarPolicy(
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		//int preferredHeight = new Integer(getFrame().getProperty("el__default_window_height")).intValue();
		int preferredHeight = getNode().getHeight();
		preferredHeight =
			Math.max(
				preferredHeight,
				Integer.parseInt(
					getFrame().getProperty("el__min_default_window_height")));
		preferredHeight =
			Math.min(
				preferredHeight,
				Integer.parseInt(
					getFrame().getProperty("el__max_default_window_height")));

		int preferredWidth = getNode().getWidth();
		preferredWidth =
			Math.max(
				preferredWidth,
				Integer.parseInt(
					getFrame().getProperty("el__min_default_window_width")));
		preferredWidth =
			Math.min(
				preferredWidth,
				Integer.parseInt(
					getFrame().getProperty("el__max_default_window_width")));

		editorScrollPane.setPreferredSize(
			new Dimension(preferredWidth, preferredHeight));
		//textArea.setPreferredSize(new Dimension(500, 160));

		final JPanel panel = new JPanel();

		//String performedAction;
		final Tools.IntHolder eventSource = new Tools.IntHolder();
		final JButton okButton = new JButton("OK");
		final JButton cancelButton = new JButton(getText("cancel"));
		final JButton splitButton = new JButton(getText("split"));
		final JCheckBox enterConfirms =
			new JCheckBox(
				getText("enter_confirms"),
				binOptionIsTrue("el__enter_confirms_by_default"));

		if (booleanHolderForConfirmState == null) {
			booleanHolderForConfirmState = new Tools.BooleanHolder();
			booleanHolderForConfirmState.setValue(enterConfirms.isSelected());
		} else {
			enterConfirms.setSelected(booleanHolderForConfirmState.getValue());
		}

		okButton.setMnemonic(KeyEvent.VK_O);
		enterConfirms.setMnemonic(KeyEvent.VK_E);
		splitButton.setMnemonic(KeyEvent.VK_S);
		cancelButton.setMnemonic(KeyEvent.VK_C);

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eventSource.setValue(BUTTON_OK);
				dialog.dispose();
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eventSource.setValue(BUTTON_CANCEL);
				dialog.dispose();
			}
		});

		splitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eventSource.setValue(BUTTON_SPLIT);
				dialog.dispose();
			}
		});

		enterConfirms.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.requestFocus();
				booleanHolderForConfirmState.setValue(
					enterConfirms.isSelected());
			}
		});

		// On Enter act as if OK button was pressed

		textArea.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				// escape key in long text editor (PN)
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					e.consume();
					eventSource.setValue(BUTTON_CANCEL);
					dialog.dispose();
				} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                                   if (enterConfirms.isSelected() && (e.getModifiers() & KeyEvent.SHIFT_MASK) != 0) {
                                      e.consume();
                                      textArea.insert("\n",textArea.getCaretPosition()); }
                                   else if (enterConfirms.isSelected() || ((e.getModifiers() & KeyEvent.ALT_MASK) != 0)) {
                                      e.consume();
                                      eventSource.setValue(BUTTON_OK);
                                      dialog.dispose(); }
                                   else {
                                      e.consume();
                                      textArea.insert("\n", textArea.getCaretPosition());
                                   }
				} 
			}

			/*
			// Daniel: I tried to make editor resizable. It worked somehow, but not
			// quite. When I increased the size and then decreased again to the original
			// size, it stopped working. The main idea here is to change the preferred
			// size. I also tried to change the size but it did not do anything sensible.
			//
			// One possibility would be to disable decreasing the size.
			//
			// Another thing which was far from nice was that it flickered.
			//
			// If someone wants to find a solution, let it be.
			
			else if (e.getKeyCode() == KeyEvent.VK_DOWN &&
					  (e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
			
				changeComponentHeight(editorScrollPane, 60, 180);
				dialog.doLayout();
				dialog.pack();
				e.consume();
			}
			else if (e.getKeyCode() == KeyEvent.VK_UP &&
					  (e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
				changeComponentHeight(editorScrollPane, -60, 180);
				dialog.doLayout();
				dialog.pack();
				e.consume(); }} */
			public void keyTyped(KeyEvent e) {
			}
			public void keyReleased(KeyEvent e) {
			}
		});

		textArea.addMouseListener(new MouseListener() {
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
					JPopupMenu popupMenu = new EditPopupMenu(textArea);
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
					e.consume();
				}
			}
		});

		textArea.setFont(getNode().getFont());
		textArea.setForeground(getNode().getForeground());

		//panel.setPreferredSize(new Dimension(500, 160));
		//editorScrollPane.setPreferredSize(new Dimension(500, 160));

		JPanel buttonPane = new JPanel();
		buttonPane.add(enterConfirms);
		buttonPane.add(okButton);
		buttonPane.add(cancelButton);
		buttonPane.add(splitButton);
		buttonPane.setMaximumSize(new Dimension(1000, 20));

		if (getFrame().getProperty("el__buttons_position").equals("above")) {
			panel.add(buttonPane);
			panel.add(editorScrollPane);
		} else {
			panel.add(editorScrollPane);
			panel.add(buttonPane);
		}

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setContentPane(panel);
		dialog.pack(); // calculate the size

		// set position
		getView().scrollNodeToVisible(getNode(), 0);
		Point frameScreenLocation =
			getFrame().getLayeredPane().getLocationOnScreen();
		double posX =
			getNode().getLocationOnScreen().getX() - frameScreenLocation.getX();
		double posY =
			getNode().getLocationOnScreen().getY()
				- frameScreenLocation.getY()
				+ (binOptionIsTrue("el__position_window_below_node")
					? getNode().getHeight()
					: 0);
		if (posX + dialog.getWidth()
			> getFrame().getLayeredPane().getWidth()) {
			posX = getFrame().getLayeredPane().getWidth() - dialog.getWidth();
		}
		if (posY + dialog.getHeight()
			> getFrame().getLayeredPane().getHeight()) {
			posY = getFrame().getLayeredPane().getHeight() - dialog.getHeight();
		}
		posX = ((posX < 0) ? 0 : posX) + frameScreenLocation.getX();
		posY = ((posY < 0) ? 0 : posY) + frameScreenLocation.getY();
		dialog.setLocation(
			new Double(posX).intValue(),
			new Double(posY).intValue());

		textArea.requestFocusInWindow();
		// make the text area focused when the dialog comes
		dialog.show();
		//dialog.setVisible(true);

		if (eventSource.getValue() == BUTTON_OK) {
			getEditControl().ok(textArea.getText());
		}
		if (eventSource.getValue() == BUTTON_SPLIT) {
			getEditControl().split(textArea.getText(), textArea.getCaretPosition());
		}
		if (eventSource.getValue() == BUTTON_CANCEL) {
			getEditControl().cancel();
		}

	}


    /**
     * @return
     */
    protected KeyEvent getFirstEvent() {
        return firstEvent;
    }


}
