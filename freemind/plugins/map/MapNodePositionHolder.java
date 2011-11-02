/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2011 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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

package plugins.map;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.ImageIcon;

import org.openstreetmap.gui.jmapviewer.Coordinate;

import freemind.controller.actions.generated.instance.PlaceNodeXmlAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.HookRegistration;
import freemind.extensions.PermanentNodeHook;
import freemind.main.Resources;
import freemind.main.XMLElement;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionFactory;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;
import freemind.modes.mindmapmode.hooks.PermanentMindMapNodeHookAdapter;

/**
 * @author foltin
 * @date 27.10.2011
 */
public class MapNodePositionHolder extends PermanentMindMapNodeHookAdapter {
	public final static String NODE_MAP_HOOK_NAME = "plugins/map/MapNodePositionHolder.properties";
	public final static String NODE_MAP_LOCATION_ICON = "node_map_location_icon";

	private static final String XML_STORAGE_POS_LON = "XML_STORAGE_POS_LON";
	private static final String XML_STORAGE_POS_LAT = "XML_STORAGE_POS_LAT";
	private static final String XML_STORAGE_MAP_LON = "XML_STORAGE_MAP_LON";
	private static final String XML_STORAGE_MAP_LAT = "XML_STORAGE_MAP_LAT";
	private static final String XML_STORAGE_ZOOM = "XML_STORAGE_ZOOM";

	private Coordinate mPosition = new Coordinate(0, 0);
	private Coordinate mMapCenter = new Coordinate(0, 0);
	private int mZoom = 1;
	private static ImageIcon sMapLocationIcon;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.extensions.NodeHookAdapter#invoke(freemind.modes.MindMapNode)
	 */
	public void invoke(MindMapNode pNode) {
		super.invoke(pNode);
		((Registration) getPluginBaseClass()).registerMapNode(this);
		setStateIcon(pNode, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.PermanentNodeHookAdapter#shutdownMapHook()
	 */
	public void shutdownMapHook() {
		setStateIcon(getNode(), false);
		((Registration) getPluginBaseClass()).deregisterMapNode(this);
		super.shutdownMapHook();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.extensions.PermanentNodeHookAdapter#save(freemind.main.XMLElement
	 * )
	 */
	public void save(XMLElement xml) {
		super.save(xml);
		HashMap values = new HashMap();
		values.put(XML_STORAGE_POS_LON, toString(mPosition.getLon()));
		values.put(XML_STORAGE_POS_LAT, toString(mPosition.getLat()));
		values.put(XML_STORAGE_MAP_LON, toString(mMapCenter.getLon()));
		values.put(XML_STORAGE_MAP_LAT, toString(mMapCenter.getLat()));
		values.put(XML_STORAGE_ZOOM, toString(mZoom));
		saveNameValuePairs(values, xml);
	}

	protected void setStateIcon(MindMapNode node, boolean enabled) {
		node.setStateIcon(NODE_MAP_LOCATION_ICON,
				(enabled) ? getMapLocationIcon() : null);
	}

	/**
	 * @param pDouble
	 * @return
	 */
	private String toString(double pDouble) {
		return "" + pDouble;
	}

	/**
	 * @param pInt
	 * @return
	 */
	private String toString(int pInt) {
		return "" + pInt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.PermanentNodeHookAdapter#loadFrom(freemind.main.
	 * XMLElement)
	 */
	public void loadFrom(XMLElement pChild) {
		super.loadFrom(pChild);
		HashMap values = loadNameValuePairs(pChild);
		mPosition.setLat(fromString(values.get(XML_STORAGE_POS_LAT)));
		mPosition.setLon(fromString(values.get(XML_STORAGE_POS_LON)));
		mMapCenter.setLat(fromString(values.get(XML_STORAGE_MAP_LAT)));
		mMapCenter.setLon(fromString(values.get(XML_STORAGE_MAP_LON)));
		mZoom = intFromString(values.get(XML_STORAGE_ZOOM));
	}

	/**
	 * @param pObject
	 * @return
	 */
	private double fromString(Object pObject) {
		try {
			return Double.parseDouble((String) pObject);
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			return 0.0;
		}
	}

	/**
	 * @param pObject
	 * @return
	 */
	private int intFromString(Object pObject) {
		try {
			return Integer.parseInt((String) pObject);
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			return 1;
		}
	}

	/**
	 * Set map position. Is undoable.
	 * 
	 */
	public void changePosition(MapNodePositionHolder pHolder,
			Coordinate pPosition, Coordinate pMapCenter, int pZoom) {
		((Registration) getPluginBaseClass()).changePosition(pHolder, pPosition, pMapCenter, pZoom);
	}
	
	public static interface MapNodePositionListener {
		void registerMapNode(MapNodePositionHolder pMapNodePositionHolder);

		void deregisterMapNode(MapNodePositionHolder pMapNodePositionHolder);
	}

	public static class Registration implements HookRegistration, ActorXml {

		private static final String PLUGINS_MAP_NODE_POSITION = MapNodePositionHolder.class
				.getName();

		/*
		 * Collects MapNodePositionHolder. This is necessary to be able to
		 * display them all efficiently.
		 */
		private HashSet/* MapNodePositionHolder s */mMapNodePositionHolders = new HashSet();

		private HashSet mMapNodePositionListeners = new HashSet();

		private final MindMapController controller;

		private final MindMap mMap;

		private final java.util.logging.Logger logger;

		public Registration(ModeController controller, MindMap map) {
			this.controller = (MindMapController) controller;
			mMap = map;
			logger = controller.getFrame().getLogger(this.getClass().getName());
		}

		public void deRegister() {
			controller.getActionFactory().deregisterActor(getDoActionClass());

		}

		public void register() {
			controller.getActionFactory().registerActor(this,
					getDoActionClass());
		}

		public void registerMapNode(MapNodePositionHolder pMapNodePositionHolder) {
			mMapNodePositionHolders.add(pMapNodePositionHolder);
			for (Iterator it = mMapNodePositionListeners.iterator(); it
					.hasNext();) {
				MapNodePositionListener listener = (MapNodePositionListener) it
						.next();
				try {
					listener.registerMapNode(pMapNodePositionHolder);
				} catch (Exception e) {
					freemind.main.Resources.getInstance().logException(e);
				}
			}
		}

		public HashSet getMapNodePositionHolders() {
			return mMapNodePositionHolders;
		}

		public void deregisterMapNode(
				MapNodePositionHolder pMapNodePositionHolder) {
			mMapNodePositionHolders.remove(pMapNodePositionHolder);
			for (Iterator it = mMapNodePositionListeners.iterator(); it
					.hasNext();) {
				MapNodePositionListener listener = (MapNodePositionListener) it
						.next();
				try {
					listener.deregisterMapNode(pMapNodePositionHolder);
				} catch (Exception e) {
					freemind.main.Resources.getInstance().logException(e);
				}
			}
		}

		public void registerMapNodePositionListener(
				MapNodePositionListener pMapNodePositionListener) {
			mMapNodePositionListeners.add(pMapNodePositionListener);
		}

		public void deregisterMapNodePositionListener(
				MapNodePositionListener pMapNodePositionListener) {
			mMapNodePositionListeners.remove(pMapNodePositionListener);
		}

		/**
		 * Set map position. Is undoable.
		 * 
		 */
		public void changePosition(MapNodePositionHolder pHolder,
				Coordinate pPosition, Coordinate pMapCenter, int pZoom) {
			MindMapNode node = pHolder.getNode();
			PlaceNodeXmlAction doAction = createPlaceNodeXmlActionAction(node,
					pPosition, pMapCenter, pZoom);
			PlaceNodeXmlAction undoAction = createPlaceNodeXmlActionAction(
					node, pHolder.getPosition(), pHolder.getMapCenter(),
					pHolder.getZoom());
			ActionFactory actionFactory = controller.getActionFactory();
			actionFactory.startTransaction(PLUGINS_MAP_NODE_POSITION);
			actionFactory.executeAction(new ActionPair(doAction, undoAction));
			actionFactory.endTransaction(PLUGINS_MAP_NODE_POSITION);
		}

		/**
		 * @param pNode
		 * @param pPosition
		 * @param pMapCenter
		 * @param pZoom
		 * @return
		 */
		private PlaceNodeXmlAction createPlaceNodeXmlActionAction(
				MindMapNode pNode, Coordinate pPosition, Coordinate pMapCenter,
				int pZoom) {
			logger.info("Setting position of node " + pNode);
			PlaceNodeXmlAction action = new PlaceNodeXmlAction();
			action.setNode(controller.getNodeID(pNode));
			action.setCursorLatitude(pPosition.getLat());
			action.setCursorLongitude(pPosition.getLon());
			action.setMapCenterLatitude(pMapCenter.getLat());
			action.setMapCenterLongitude(pMapCenter.getLon());
			action.setZoom(pZoom);
			return action;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * freemind.modes.mindmapmode.actions.xml.ActorXml#act(freemind.controller
		 * .actions.generated.instance.XmlAction)
		 */
		public void act(XmlAction pAction) {
			if (pAction instanceof PlaceNodeXmlAction) {
				PlaceNodeXmlAction placeAction = (PlaceNodeXmlAction) pAction;
				MindMapNode node = controller.getNodeFromID(placeAction
						.getNode());
				MapNodePositionHolder hook = getHook(node);
				if (hook != null) {
					hook.setMapCenter(new Coordinate(placeAction
							.getMapCenterLatitude(), placeAction
							.getMapCenterLongitude()));
					hook.setPosition(new Coordinate(placeAction
							.getCursorLatitude(), placeAction
							.getCursorLongitude()));
					hook.setZoom(placeAction.getZoom());
				} else {
					throw new IllegalArgumentException(
							"MapNodePositionHolder to node id "
									+ placeAction.getNode() + " not found.");
				}

			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * freemind.modes.mindmapmode.actions.xml.ActorXml#getDoActionClass()
		 */
		public Class getDoActionClass() {
			return PlaceNodeXmlAction.class;
		}

	}

	public Coordinate getPosition() {
		return mPosition;
	}

	public void setPosition(Coordinate pPosition) {
		mPosition = pPosition;
	}

	public Coordinate getMapCenter() {
		return mMapCenter;
	}

	public void setMapCenter(Coordinate pMapCenter) {
		mMapCenter = pMapCenter;
	}

	public int getZoom() {
		return mZoom;
	}

	public void setZoom(int pZoom) {
		mZoom = pZoom;
	}

	public MindMapNode getNode() {
		return super.getNode();
	}

	/**
	 */
	public static MapNodePositionHolder getHook(MindMapNode node) {
		for (Iterator j = node.getActivatedHooks().iterator(); j.hasNext();) {
			PermanentNodeHook element = (PermanentNodeHook) j.next();
			if (element instanceof MapNodePositionHolder) {
				return (MapNodePositionHolder) element;
			}
		}
		return null;
	}

	public static ImageIcon getMapLocationIcon() {
		// icon
		if (sMapLocationIcon == null) {
			sMapLocationIcon = new ImageIcon(Resources.getInstance()
					.getResource("images/map_location.png"));
		}
		return sMapLocationIcon;
	}

}
