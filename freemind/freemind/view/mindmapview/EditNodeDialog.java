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
/*$Id: EditNodeDialog.java,v 1.1.4.1.16.20 2009/06/24 20:40:19 christianfoltin Exp $*/

package freemind.view.mindmapview;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.inet.jortho.SpellChecker;

import freemind.main.Tools;
import freemind.modes.ModeController;

/**
 * @author foltin
 * 
 */
public class EditNodeDialog extends EditNodeBase {
	private KeyEvent firstEvent;

	/** Private variable to hold the last value of the "Enter confirms" state. */
	private static Tools.BooleanHolder booleanHolderForConfirmState;

	public EditNodeDialog(final NodeView node, final String text,
			final KeyEvent firstEvent, ModeController controller,
			EditControl editControl) {
		super(node, text, controller, editControl);
		this.firstEvent = firstEvent;
	}

	class LongNodeDialog extends EditDialog {
		private static final long serialVersionUID = 6185443281994675732L;
		private JTextArea textArea;

		LongNodeDialog() {
			super(EditNodeDialog.this);
			textArea = new JTextArea(getText());
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
			// wish from
			// https://sourceforge.net/forum/message.php?msg_id=5923410
			// textArea.setTabSize(4);
			// wrap around words rather than characters

			final JScrollPane editorScrollPane = new JScrollPane(textArea);
			editorScrollPane
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

			// int preferredHeight = new
			// Integer(getFrame().getProperty("el__default_window_height")).intValue();
			int preferredHeight = getNode().getHeight();
			preferredHeight = Math.max(
					preferredHeight,
					Integer.parseInt(getFrame().getProperty(
							"el__min_default_window_height")));
			preferredHeight = Math.min(
					preferredHeight,
					Integer.parseInt(getFrame().getProperty(
							"el__max_default_window_height")));

			int preferredWidth = getNode().getWidth();
			preferredWidth = Math.max(
					preferredWidth,
					Integer.parseInt(getFrame().getProperty(
							"el__min_default_window_width")));
			preferredWidth = Math.min(
					preferredWidth,
					Integer.parseInt(getFrame().getProperty(
							"el__max_default_window_width")));

			editorScrollPane.setPreferredSize(new Dimension(preferredWidth,
					preferredHeight));
			// textArea.setPreferredSize(new Dimension(500, 160));

			final JPanel panel = new JPanel();

			// String performedAction;
			final JButton okButton = new JButton();
			final JButton cancelButton = new JButton();
			final JButton splitButton = new JButton();
			final JCheckBox enterConfirms = new JCheckBox("",
					binOptionIsTrue("el__enter_confirms_by_default"));

			Tools.setLabelAndMnemonic(okButton, getText("ok"));
			Tools.setLabelAndMnemonic(cancelButton, getText("cancel"));
			Tools.setLabelAndMnemonic(splitButton, getText("split"));
			Tools.setLabelAndMnemonic(enterConfirms, getText("enter_confirms"));

			if (booleanHolderForConfirmState == null) {
				booleanHolderForConfirmState = new Tools.BooleanHolder();
				booleanHolderForConfirmState.setValue(enterConfirms
						.isSelected());
			} else {
				enterConfirms.setSelected(booleanHolderForConfirmState
						.getValue());
			}

			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// next try to avoid bug 1159: focus jumps to file-menu after closing html-editing-window
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							submit();
						}
					});
				}
			});

			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cancel();
				}
			});

			splitButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					split();
				}
			});

			enterConfirms.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					textArea.requestFocus();
					booleanHolderForConfirmState.setValue(enterConfirms
							.isSelected());
				}
			});

			// On Enter act as if OK button was pressed

			textArea.addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent e) {
					// escape key in long text editor (PN)
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						e.consume();
						confirmedCancel();
					} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						if (enterConfirms.isSelected()
								&& (e.getModifiers() & KeyEvent.SHIFT_MASK) != 0) {
							e.consume();
							textArea.insert("\n", textArea.getCaretPosition());
						} else if (enterConfirms.isSelected()
								|| ((e.getModifiers() & KeyEvent.ALT_MASK) != 0)) {
							e.consume();
							submit();
						} else {
							e.consume();
							textArea.insert("\n", textArea.getCaretPosition());
						}
					}
				}

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
						if (checkSpelling) {
							popupMenu.add(SpellChecker.createCheckerMenu());
							popupMenu.add(SpellChecker.createLanguagesMenu());
						}
						popupMenu.show(e.getComponent(), e.getX(), e.getY());
						e.consume();
					}
				}
			});

			Font font = getNode().getTextFont();
			font = Tools.updateFontSize(font, getView().getZoom(),
					font.getSize());
			textArea.setFont(font);

			final Color nodeTextColor = getNode().getTextColor();
			textArea.setForeground(nodeTextColor);
			final Color nodeTextBackground = getNode().getTextBackground();
			textArea.setBackground(nodeTextBackground);
			textArea.setCaretColor(nodeTextColor);

			// panel.setPreferredSize(new Dimension(500, 160));
			// editorScrollPane.setPreferredSize(new Dimension(500, 160));

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
			setContentPane(panel);

			if (firstEvent instanceof KeyEvent) {
				redispatchKeyEvents(textArea, firstEvent);
			} // 1st key event defined
			else {
				textArea.setCaretPosition(getText().length());
			}

			if (checkSpelling) {
				SpellChecker.register(textArea, false, true, true, true);
			}
		}

		public void show() {
			textArea.requestFocus();
			super.show();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see freemind.view.mindmapview.EditNodeBase.Dialog#cancel()
		 */
		protected void cancel() {
			getEditControl().cancel();
			super.cancel();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see freemind.view.mindmapview.EditNodeBase.Dialog#split()
		 */
		protected void split() {
			getEditControl().split(textArea.getText(),
					textArea.getCaretPosition());
			super.split();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see freemind.view.mindmapview.EditNodeBase.Dialog#submit()
		 */
		protected void submit() {
			getEditControl().ok(textArea.getText());
			super.submit();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see freemind.view.mindmapview.EditNodeBase.Dialog#isChanged()
		 */
		protected boolean isChanged() {
			return !getText().equals(textArea.getText());
		}

		public Component getMostRecentFocusOwner() {
			if (isFocused()) {
				return getFocusOwner();
			} else {
				return textArea;
			}
		}
	}

	public void show() {
		final EditDialog dialog = new LongNodeDialog();

		dialog.pack(); // calculate the size

		// set position
		getView().scrollNodeToVisible(getNode(), 0);
		Tools.setDialogLocationRelativeTo(dialog, getNode());
		dialog.show();
	}
}
