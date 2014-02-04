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

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;

import freemind.modes.ArrowLinkAdapter;
import freemind.modes.ArrowLinkTarget;
import freemind.modes.CloudAdapter;
import freemind.modes.EdgeAdapter;
import freemind.modes.MapAdapter;
import freemind.modes.MapFeedback;
import freemind.modes.MindMap;
import freemind.modes.MindMapLinkRegistry;
import freemind.modes.ModeController;
import freemind.modes.NodeAdapter;

public class BrowseMapModel extends MapAdapter {

	private static final String ENCRYPTED_BROWSE_NODE = EncryptedBrowseNode.class
			.getName();
	private URL url;
	private MindMapLinkRegistry linkRegistry;

	public BrowseMapModel(BrowseNodeModel root, ModeController modeController) {
		super(modeController);
		if (root != null)
			setRoot(root);
		else
			setRoot(new BrowseNodeModel(getMapFeedback().getResourceString(
					"new_mindmap"), modeController.getMap()));
		// register new LinkRegistryAdapter
		linkRegistry = new MindMapLinkRegistry();
	}

	//
	// Other methods
	//
	public MindMapLinkRegistry getLinkRegistry() {
		return linkRegistry;
	}

	public String toString() {
		if (getURL() == null) {
			return null;
		} else {
			return getURL().toString();
		}
	}

	public File getFile() {
		return null;
	}

	protected void setFile() {
	}

	/**
	 * Get the value of url.
	 * 
	 * @return Value of url.
	 */
	public URL getURL() {
		return url;
	}

	/**
	 * Set the value of url.
	 * 
	 * @param v
	 *            Value to assign to url.
	 */
	public void setURL(URL v) {
		this.url = v;
	}

	public boolean save(File file) {
		return true;
	}

	public boolean isSaved() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.MindMap#setLinkInclinationChanged()
	 */
	public void setLinkInclinationChanged() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.MindMap#getXml(java.io.Writer)
	 */
	public void getXml(Writer fileout) throws IOException {
		// nothing.
		// FIXME: Implement me if you need me.
		throw new RuntimeException("Unimplemented method called.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.MindMap#getFilteredXml(java.io.Writer)
	 */
	public void getFilteredXml(Writer fileout) throws IOException {
		// nothing.
		// FIXME: Implement me if you need me.
		throw new RuntimeException("Unimplemented method called.");
	}


	protected NodeAdapter createNodeAdapter(MapFeedback pMapFeedback, String nodeClass) {
		if (nodeClass == ENCRYPTED_BROWSE_NODE) {
			return new EncryptedBrowseNode(null, pMapFeedback);
		}
		return new BrowseNodeModel(null, pMapFeedback.getMap());
	}

	public EdgeAdapter createEdgeAdapter(NodeAdapter node) {
		return new BrowseEdgeModel(node, mMapFeedback);
	}

	public CloudAdapter createCloudAdapter(NodeAdapter node) {
		return new BrowseCloudModel(node, mMapFeedback);
	}

	public ArrowLinkAdapter createArrowLinkAdapter(NodeAdapter source,
			NodeAdapter target) {
		return new BrowseArrowLinkModel(source, target, mMapFeedback);
	}

	public ArrowLinkTarget createArrowLinkTarget(NodeAdapter source,
			NodeAdapter target) {
		// FIXME: Need an implementation here
		return null;
	}
	
	public NodeAdapter createEncryptedNode(String additionalInfo) {
		NodeAdapter node = createNodeAdapter(mMapFeedback, ENCRYPTED_BROWSE_NODE);
		node.setAdditionalInfo(additionalInfo);
		return node;
	}

	/* (non-Javadoc)
	 * @see freemind.modes.XMLElementAdapter#createNodeAdapter(freemind.modes.MindMap, java.lang.String)
	 */
	@Override
	public NodeAdapter createNodeAdapter(MindMap pMap, String pNodeClass) {
		return createNodeAdapter(mMapFeedback, null);
	}

}
