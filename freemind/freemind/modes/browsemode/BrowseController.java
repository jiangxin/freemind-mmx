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


package freemind.modes.browsemode;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import freemind.controller.MenuBar;
import freemind.controller.MenuItemEnabledListener;
import freemind.controller.StructuredMenuHolder;
import freemind.extensions.HookFactory;
import freemind.main.Tools;
import freemind.main.XMLElement;
import freemind.main.XMLParseException;
import freemind.modes.MapAdapter;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.Mode;
import freemind.modes.ModeController;
import freemind.modes.NodeAdapter;
import freemind.modes.common.GotoLinkNodeAction;
import freemind.modes.common.plugins.MapNodePositionHolderBase;
import freemind.modes.common.plugins.NodeNoteBase;
import freemind.modes.viewmodes.ViewControllerAdapter;
import freemind.view.mindmapview.MainView;

public class BrowseController extends ViewControllerAdapter {

	private JPopupMenu popupmenu;
	private JToolBar toolbar;

	Action followLink;

	Action nodeUp;

	Action nodeDown;

	private HookFactory mBrowseHookFactory;
	private ImageIcon noteIcon;
	public FollowMapLink followMapLink;

	public static class FollowMapLink extends AbstractAction implements MenuItemEnabledListener {

		private ViewControllerAdapter modeController;

		private Logger logger;

		public FollowMapLink(ViewControllerAdapter controller) {
			super(controller.getText("follow_map_link"), MapNodePositionHolderBase.getMapLocationIcon());
			this.modeController = controller;
			logger = modeController.getFrame().getLogger(
					this.getClass().getName());
		}

		public void actionPerformed(ActionEvent e) {

			MapNodePositionHolderBase hook = getHook();
			if (hook != null) {
				String[] barePositions = hook.getBarePosition();
				try {
					// GRR, this is doubled code :-(
					HashMap tileSources = new HashMap();
					tileSources.put(
							MapNodePositionHolderBase.TILE_SOURCE_MAPNIK,
							MapNodePositionHolderBase.SHORT_MAPNIK);
					tileSources.put(
							MapNodePositionHolderBase.TILE_SOURCE_CYCLE_MAP,
							MapNodePositionHolderBase.SHORT_CYCLE_MAP);
					tileSources
							.put(MapNodePositionHolderBase.TILE_SOURCE_TRANSPORT_MAP,
									MapNodePositionHolderBase.SHORT_TRANSPORT_MAP);
					tileSources
							.put(MapNodePositionHolderBase.TILE_SOURCE_MAP_QUEST_OPEN_MAP,
									MapNodePositionHolderBase.SHORT_MAP_QUEST_OPEN_MAP);
					String link = "http://www.openstreetmap.org/?" + "mlat="
							+ barePositions[0] + "&mlon=" + barePositions[1]
							+ "&lat=" + barePositions[2] + "&lon="
							+ barePositions[3] + "&zoom=" + barePositions[4]
							+ "&layers=" + tileSources.get(barePositions[5]);
					logger.fine("Try to open link " + link);
					modeController.getFrame().openDocument(new URL(link));
				} catch (MalformedURLException e1) {
					freemind.main.Resources.getInstance().logException(e1);

				} catch (Exception e1) {
					freemind.main.Resources.getInstance().logException(e1);
				}
			}
		}

		protected MapNodePositionHolderBase getHook() {
			MindMapNode selected = modeController.getSelected();
			MapNodePositionHolderBase hook = MapNodePositionHolderBase
					.getBaseHook(selected);
			return hook;
		}

		/* (non-Javadoc)
		 * @see freemind.controller.MenuItemEnabledListener#isEnabled(javax.swing.JMenuItem, javax.swing.Action)
		 */
		public boolean isEnabled(JMenuItem pItem, Action pAction) {
			return getHook() != null;
		}

	}

	public BrowseController(Mode mode) {
		super(mode);
		mBrowseHookFactory = new BrowseHookFactory();
		// Daniel: Actions are initialized here and not above because of
		// some error it would produce. Not studied in more detail.
		followLink = new FollowLinkAction();

		followMapLink = new FollowMapLink(this);
		popupmenu = new BrowsePopupMenu(this);
		toolbar = new BrowseToolBar(this);
		setAllActions(false);
		// for displaying notes.
		registerNodeSelectionListener(new NodeNoteViewer(this), false);
	}

	public void startupController() {
		super.startupController();
		invokeHooksRecursively((NodeAdapter) getRootNode(), getMap());
	}

	protected void restoreMapsLastState(ModeController pNewModeController,
			MapAdapter pModel) {
		// intentionally do nothing.
	}

	public MapAdapter newModel(ModeController modeController) {
		return new BrowseMapModel(getFrame(), modeController);
	}

	public void plainClick(MouseEvent e) {
		/* perform action only if one selected node. */
		if (getSelecteds().size() != 1)
			return;
		final MainView component = (MainView) e.getComponent();
		if (component.isInFollowLinkRegion(e.getX())) {
			loadURL();
		} else {
			MindMapNode node = (component).getNodeView().getModel();
			if (!node.hasChildren()) {
				// the emulate the plain click.
				doubleClick(e);
				return;
			}
			toggleFolded.toggleFolded(getSelecteds().listIterator());
		}

	}

	public void doubleClick() {
		/* If the link exists, follow the link; toggle folded otherwise */
		if (getSelected().getLink() == null) {
			toggleFolded.toggleFolded();
		} else {
			loadURL();
		}
	}

	// public void anotherNodeSelected(MindMapNode n) {
	// super.anotherNodeSelected(n);
	// if(n.isRoot()){
	// return;
	// }
	// //Presentation:
	// setFolded(n, false);
	// foldOthers(n);
	// }
	//
	//
	// private void foldOthers(MindMapNode n) {
	// if(n.isRoot()){
	// return;
	// }
	// MindMapNode parent = n.getParentNode();
	// for (Iterator iter = parent.childrenUnfolded(); iter.hasNext();) {
	// MindMapNode element = (MindMapNode) iter.next();
	// if(element != n){
	// setFolded(element, true);
	// }
	// }
	// foldOthers(parent);
	// }

	public MindMapNode newNode(Object userObject, MindMap map) {
		return new BrowseNodeModel(userObject, getFrame(), map);
	}

	public JPopupMenu getPopupMenu() {
		return popupmenu;
	}

	/**
	 * Link implementation: If this is a link, we want to make a popup with at
	 * least removelink available.
	 */
	public JPopupMenu getPopupForModel(java.lang.Object obj) {
		if (obj instanceof BrowseArrowLinkModel) {
			// yes, this is a link.
			BrowseArrowLinkModel link = (BrowseArrowLinkModel) obj;
			JPopupMenu arrowLinkPopup = new JPopupMenu();

			arrowLinkPopup.add(getGotoLinkNodeAction(link.getSource()));
			arrowLinkPopup.add(getGotoLinkNodeAction(link.getTarget()));

			arrowLinkPopup.addSeparator();
			// add all links from target and from source:
			HashSet nodeAlreadyVisited = new HashSet();
			nodeAlreadyVisited.add(link.getSource());
			nodeAlreadyVisited.add(link.getTarget());
			Vector links = getModel().getLinkRegistry().getAllLinks(
					link.getSource());
			links.addAll(getModel().getLinkRegistry().getAllLinks(
					link.getTarget()));
			for (int i = 0; i < links.size(); ++i) {
				BrowseArrowLinkModel foreign_link = (BrowseArrowLinkModel) links
						.get(i);
				if (nodeAlreadyVisited.add(foreign_link.getTarget())) {
					arrowLinkPopup.add(getGotoLinkNodeAction(foreign_link
							.getTarget()));
				}
				if (nodeAlreadyVisited.add(foreign_link.getSource())) {
					arrowLinkPopup.add(getGotoLinkNodeAction(foreign_link
							.getSource()));
				}
			}
			return arrowLinkPopup;
		}
		return null;
	}

	/**
	 */
	private GotoLinkNodeAction getGotoLinkNodeAction(MindMapNode destination) {
		return new GotoLinkNodeAction(this, destination);
	}

	public JToolBar getModeToolBar() {
		return getToolBar();
	}

	BrowseToolBar getToolBar() {
		return (BrowseToolBar) toolbar;
	}

	// public void loadURL(String relative) {
	// // copy from mind map controller:
	// if (relative.startsWith("#")) {
	// // inner map link, fc, 12.10.2004
	// String target = relative.substring(1);
	// try {
	// MindMapNode node = getNodeFromID(target);
	// centerNode(node);
	// return;
	// } catch (Exception e) {
	// // bad luck.
	// getFrame().out(Tools.expandPlaceholders(getText("link_not_found"),
	// target));
	// return;
	// }
	// }
	//
	// URL absolute = null;
	// try {
	// absolute = new URL(getMap().getURL(), relative);
	// getFrame().out(absolute.toString());
	// } catch (MalformedURLException ex) {
	// freemind.main.Resources.getInstance().logExecption(ex);
	// getController().errorMessage(
	// getText("url_error") + " " + ex.getMessage());
	// // getFrame().err(getText("url_error"));
	// return;
	// }
	//
	// String type = Tools.getExtension(absolute.getFile());
	// try {
	// if
	// (type.equals(freemind.main.FreeMindCommon.FREEMIND_FILE_EXTENSION_WITHOUT_DOT))
	// {
	// getFrame().setWaitingCursor(true);
	// load(absolute);
	// } else {
	// getFrame().openDocument(absolute);
	// }
	// } catch (Exception ex) {
	// getController().errorMessage(getText("url_load_error") + absolute);
	// freemind.main.Resources.getInstance().logExecption( ex);
	// // for some reason, this exception is thrown anytime...
	// } finally {
	// getFrame().setWaitingCursor(false);
	// }
	//
	// }

	public ModeController load(URL url) throws IOException, XMLParseException,
			URISyntaxException {
		ModeController newModeController = super.load(url);
		// decorator pattern.
		((BrowseToolBar) newModeController.getModeToolBar()).setURLField(url
				.toString());
		return newModeController;
	}

	public ModeController load(File url) throws IOException {
		ModeController newModeController = super.load(url);
		// decorator pattern.
		((BrowseToolBar) newModeController.getModeToolBar()).setURLField(Tools
				.fileToUrl(url).toString());
		return newModeController;
	}

	public void newMap(MindMap mapModel) {
		setNoteIcon(mapModel.getRootNode());
		super.newMap(mapModel);
	}

	private void setNoteIcon(MindMapNode node) {
		String noteText = node.getNoteText();
		if (noteText != null && !noteText.equals("")) {
			// icon
			if (noteIcon == null) {
				noteIcon = new ImageIcon(getController().getResource(
						"images/knotes.png"));
			}
			node.setStateIcon(NodeNoteBase.NODE_NOTE_ICON, noteIcon);
		}
		ListIterator children = node.childrenUnfolded();
		while (children.hasNext()) {
			setNoteIcon((MindMapNode) children.next());
		}

	}

	/**
	 * Enabled/Disabled all actions that are dependent on whether there is a map
	 * open or not.
	 */
	protected void setAllActions(boolean enabled) {
		super.setAllActions(enabled);
		toggleFolded.setEnabled(enabled);
		toggleChildrenFolded.setEnabled(enabled);
		followLink.setEnabled(enabled);
	}

	// ////////
	// Actions
	// ///////

	private class FollowLinkAction extends AbstractAction {
		FollowLinkAction() {
			super(getText("follow_link"));
		}

		public void actionPerformed(ActionEvent e) {
			loadURL();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#updateMenus(freemind.controller.
	 * StructuredMenuHolder)
	 */
	public void updateMenus(StructuredMenuHolder holder) {
		add(holder, MenuBar.EDIT_MENU + "/find/find", find, "keystroke_find");
		add(holder, MenuBar.EDIT_MENU + "/find/findNext", findNext,
				"keystroke_find_next");
		add(holder, MenuBar.EDIT_MENU + "/find/followLink", followLink,
				"keystroke_follow_link");
		holder.addSeparator(MenuBar.EDIT_MENU + "/find");
		add(holder, MenuBar.EDIT_MENU + "/find/toggleFolded", toggleFolded,
				"keystroke_toggle_folded");
		add(holder, MenuBar.EDIT_MENU + "/find/toggleChildrenFolded",
				toggleChildrenFolded, "keystroke_toggle_children_folded");
	}

	public HookFactory getHookFactory() {
		return mBrowseHookFactory;
	}

	public XMLElement createXMLElement() {
		return new BrowseXMLElement(this);
	}

}
