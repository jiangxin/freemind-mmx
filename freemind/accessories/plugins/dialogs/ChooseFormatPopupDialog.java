/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2001  Joerg Mueller <joergmueller@bigfoot.com>
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

package accessories.plugins.dialogs;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import freemind.common.TextTranslator;
import freemind.controller.actions.generated.instance.Pattern;
import freemind.controller.actions.generated.instance.PatternEdgeWidth;
import freemind.controller.actions.generated.instance.WindowConfigurationStorage;
import freemind.main.Tools;
import freemind.main.XMLParseException;
import freemind.modes.EdgeAdapter;
import freemind.modes.MapAdapter;
import freemind.modes.MapFeedbackAdapter;
import freemind.modes.MindIcon;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapController.MindMapControllerPlugin;
import freemind.modes.mindmapmode.MindMapController.StringReaderCreator;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.actions.ApplyPatternAction;
import freemind.modes.mindmapmode.actions.ApplyPatternAction.ExternalPatternAction;
import freemind.modes.mindmapmode.dialogs.StylePatternFrame;
import freemind.modes.mindmapmode.dialogs.StylePatternFrame.StylePatternFrameType;
import freemind.view.mindmapview.MapView;

/** */
public class ChooseFormatPopupDialog extends JDialog implements TextTranslator,
		KeyListener {

	/**
	 * @author foltin
	 * @date 21.02.2014
	 */
	private final class DemoMapFeedback extends MapFeedbackAdapter {
		MindMap mMap;

		@Override
		public MindMap getMap() {
			return mMap;
		}
		
		/* (non-Javadoc)
		 * @see freemind.modes.MapFeedbackAdapter#getDefaultFont()
		 */
		@Override
		public Font getDefaultFont() {
			return mController.getController().getDefaultFont();
		}
		
		/* (non-Javadoc)
		 * @see freemind.modes.MapFeedbackAdapter#getFontThroughMap(java.awt.Font)
		 */
		@Override
		public Font getFontThroughMap(Font pFont) {
			return mController.getController().getFontThroughMap(pFont);
		}
	}

	public static final int CANCEL = -1;

	public static final int OK = 1;

	private static final String WINDOW_PREFERENCE_STORAGE_PROPERTY = "accessories.plugins.dialogs.ChooseFormatPopupDialog.window_storage";

	private int result = CANCEL;

	private javax.swing.JPanel jContentPane = null;

	private MindMapController mController;

	private JButton jCancelButton;

	private JButton jOKButton;

	private StylePatternFrame mStylePatternFrame;

	private MapView mDemoFrame;

	private MindMapNode mDemoNode;

	protected static java.util.logging.Logger logger = null;

	/**
	 * This constructor is used, if you need the user to enter a pattern
	 * generally.
	 * 
	 */
	public ChooseFormatPopupDialog(JFrame caller, MindMapController controller,
			String dialogTitle, Pattern pattern) {
		super(caller);
		this.mController = controller;
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
		initialize(dialogTitle);
		mStylePatternFrame.setPattern(pattern);
		mStylePatternFrame.addListeners();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize(String dialogTitle) {

		this.setTitle(mController.getText(dialogTitle));
		JPanel contentPane = getJContentPane();
		this.setContentPane(contentPane);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				cancelPressed();
			}
		});
		addKeyListener(this);
		Action action = new AbstractAction() {

			public void actionPerformed(ActionEvent arg0) {
				cancelPressed();
			}
		};
		Tools.addEscapeActionToDialog(this, action);
		pack();
		mController.decorateDialog(this, WINDOW_PREFERENCE_STORAGE_PROPERTY);

	}

	private void close() {
		WindowConfigurationStorage storage = new WindowConfigurationStorage();
		mController.storeDialogPositions(this, storage,
				WINDOW_PREFERENCE_STORAGE_PROPERTY);
		setVisible(false);
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
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new GridBagLayout());
			/*
			 * public GridBagConstraints(int gridx, int gridy, int gridwidth,
			 * int gridheight, double weightx, double weighty, int anchor, int
			 * fill, Insets insets, int ipadx, int ipady)
			 */
			jContentPane.add(new JScrollPane(getStylePatternFrame()),
					new GridBagConstraints(0, 0, 2, 1, 2.0, 8.0,
							GridBagConstraints.WEST, GridBagConstraints.BOTH,
							new Insets(0, 0, 0, 0), 0, 0));
			jContentPane.add(new JScrollPane(getDemoFrame()),
					new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0,
							GridBagConstraints.WEST, GridBagConstraints.BOTH,
							new Insets(0, 0, 0, 0), 0, 0));
			jContentPane.add(getJOKButton(), new GridBagConstraints(0, 2, 1, 1,
					1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
					new Insets(0, 0, 0, 0), 0, 0));
			jContentPane.add(getJCancelButton(), new GridBagConstraints(1, 2,
					1, 1, 1.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			getRootPane().setDefaultButton(getJOKButton());
		}
		return jContentPane;
	}

	/**
	 * @return
	 */
	private Component getDemoFrame() {
		if (mDemoFrame == null) {
			DemoMapFeedback mapFeedback = new DemoMapFeedback();
			final MindMapMapModel mMap = new MindMapMapModel(mapFeedback);
			mapFeedback.mMap = mMap;
			StringReaderCreator readerCreator = new StringReaderCreator(
					"<map><node TEXT='ROOT'><node TEXT='FormatMe'><node TEXT='Child1'/><node TEXT='Child2'/></node></node></map>");
			try {
				MindMapNode root = mMap.loadTree(readerCreator,
						MapAdapter.sDontAskInstance);
				mMap.setRoot(root);
				mDemoNode = (MindMapNode) root.getChildAt(0);
			} catch (XMLParseException e) {
				freemind.main.Resources.getInstance().logException(e);
			} catch (IOException e) {
				freemind.main.Resources.getInstance().logException(e);
			}
			mDemoFrame = new MapView(mMap, mapFeedback);
			mDemoFrame.centerNode(mDemoFrame.getNodeView(mDemoNode));
		}
		return mDemoFrame;
	}

	private Component getStylePatternFrame() {
		if (mStylePatternFrame == null) {
			mStylePatternFrame = new StylePatternFrame(this, mController,
					StylePatternFrameType.WITHOUT_NAME_AND_CHILDS) {
				/*
				 * (non-Javadoc)
				 * 
				 * @see freemind.modes.mindmapmode.dialogs.StylePatternFrame#
				 * propertyChange(java.beans.PropertyChangeEvent)
				 */
				@Override
				public void propertyChange(PropertyChangeEvent pEvt) {
					super.propertyChange(pEvt);
					Pattern pattern = mStylePatternFrame.getResultPattern();
					logger.info("Pattern " + Tools.marshall(pattern)
							+ " and event  " + pEvt);
					if (pattern.getPatternNodeColor() != null) {
						mDemoNode.setColor(Tools.xmlToColor(pattern
								.getPatternNodeColor().getValue()));
					}
					if (pattern.getPatternNodeBackgroundColor() != null) {
						mDemoNode.setBackgroundColor(Tools
								.xmlToColor(pattern
										.getPatternNodeBackgroundColor()
										.getValue()));
					}
					if (pattern.getPatternNodeStyle() != null) {
						mDemoNode.setStyle(pattern.getPatternNodeStyle()
								.getValue());
					}
					if (pattern.getPatternEdgeColor() != null) {
						((EdgeAdapter) mDemoNode.getEdge()).setColor(
								Tools.xmlToColor(pattern.getPatternEdgeColor()
										.getValue()));
					}
					if (pattern.getPatternNodeText() != null) {
						if (pattern.getPatternNodeText().getValue() != null) {
							mDemoNode.setText(pattern.getPatternNodeText().getValue());
						} else {
							// clear text:
							mDemoNode.setText("");
						}
					}
					if (pattern.getPatternIcon() != null) {
						String iconName = pattern.getPatternIcon().getValue();
						if (iconName == null) {
							while (mDemoNode.removeIcon(0) > 0) {
							}
						} else {
							// check if icon is already present:
							List icons = mDemoNode.getIcons();
							boolean found = false;
							for (Iterator iterator = icons.iterator(); iterator.hasNext();) {
								MindIcon icon = (MindIcon) iterator.next();
								if (icon.getName() != null
										&& icon.getName().equals(iconName)) {
									found = true;
									break;
								}
							}
							if (!found) {
								mDemoNode.addIcon(MindIcon.factory(iconName), mDemoNode.getIcons().size());
							}
						}
					} 
					if (pattern.getPatternNodeFontName() != null) {
						String nodeFontFamily = pattern.getPatternNodeFontName().getValue();
						if (nodeFontFamily == null) {
							mDemoNode.setFont(mController.getController()
									.getDefaultFont());
						} else {
							((NodeAdapter) mDemoNode).establishOwnFont();
							mDemoNode.setFont(mController.getController().getFontThroughMap(
									new Font(nodeFontFamily, mDemoNode.getFont().getStyle(), mDemoNode
											.getFont().getSize())));
						}
					}
					if (pattern.getPatternNodeFontSize() != null) {
						String nodeFontSize = pattern.getPatternNodeFontSize().getValue();
						if (nodeFontSize == null) {
							mDemoNode.setFontSize(mController.getController()
									.getDefaultFontSize());
						} else {
							try {
								mDemoNode.setFontSize(Integer
										.parseInt(nodeFontSize));
							} catch (Exception e) {
								freemind.main.Resources.getInstance()
										.logException(e);
							}
						}
					}
					if (pattern.getPatternNodeFontItalic() != null) {
						((NodeAdapter) mDemoNode)
								.setItalic(
										"true".equals(pattern.getPatternNodeFontItalic()
												.getValue()));
					}
					if (pattern.getPatternNodeFontBold() != null) {
						((NodeAdapter) mDemoNode).setBold("true".equals(pattern.getPatternNodeFontBold().getValue()));
					}

					if (pattern.getPatternEdgeStyle() != null) {
						((EdgeAdapter) mDemoNode.getEdge()).setStyle(pattern.getPatternEdgeStyle().getValue());
					}
					PatternEdgeWidth patternEdgeWidth = pattern.getPatternEdgeWidth();
					if (patternEdgeWidth != null) {
						if (patternEdgeWidth.getValue() != null) {
							((EdgeAdapter) mDemoNode.getEdge()).setWidth(Tools.edgeWidthStringToInt(patternEdgeWidth.getValue()));
						} else {
							((EdgeAdapter) mDemoNode.getEdge()).setWidth(EdgeAdapter.DEFAULT_WIDTH);
						}
					}

					mDemoFrame.getNodeView(mDemoNode).updateAll();
					mDemoFrame.doLayout();
				}
			};
			mStylePatternFrame.init();

		}
		return mStylePatternFrame;
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

			Tools.setLabelAndMnemonic(jOKButton, mController.getText("ok"));
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
			Tools.setLabelAndMnemonic(jCancelButton,
					mController.getText(("cancel")));
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

	public Pattern getPattern() {
		return mStylePatternFrame.getResultPattern();
	}

	public Pattern getPattern(Pattern copyIntoPattern) {
		return mStylePatternFrame.getResultPattern(copyIntoPattern);
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
			cancelPressed();
			keyEvent.consume();
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

}
