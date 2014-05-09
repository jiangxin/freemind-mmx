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
/*$Id: MindMapMock.java,v 1.1.2.9 2007/07/08 16:27:06 dpolivaev Exp $*/

package tests.freemind;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import freemind.controller.filter.Filter;
import freemind.controller.filter.util.SortedListModel;
import freemind.controller.filter.util.SortedMapListModel;
import freemind.extensions.NodeHook;
import freemind.main.Tools;
import freemind.main.Tools.ReaderCreator;
import freemind.main.XMLParseException;
import freemind.modes.ArrowLinkAdapter;
import freemind.modes.ArrowLinkTarget;
import freemind.modes.CloudAdapter;
import freemind.modes.EdgeAdapter;
import freemind.modes.MapFeedback;
import freemind.modes.MapFeedbackAdapter;
import freemind.modes.MindMap;
import freemind.modes.MindMapLinkRegistry;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;

/** */
public class MindMapMock implements MindMap {

	private final String mapXmlString;

	/**
     * 
     */
	public MindMapMock(String mapXmlString) {
		super();
		this.mapXmlString = mapXmlString;

	}

	@Override
	public void nodeChanged(TreeNode node) {
	}

	@Override
	public void nodeRefresh(TreeNode node) {
	}

	@Override
	public String getAsPlainText(List mindMapNodes) {
		return null;
	}

	@Override
	public String getAsRTF(List mindMapNodes) {
		return null;
	}

	@Override
	public String getAsHTML(List mindMapNodes) {
		return null;
	}

	@Override
	public File getFile() {
		return null;
	}

	@Override
	public URL getURL() throws MalformedURLException {
		return null;
	}

	@Override
	public void getXml(Writer fileout) throws IOException {
	}

	@Override
	public void getFilteredXml(Writer fileout) throws IOException {
		fileout.write(mapXmlString);
		fileout.close();
	}

	@Override
	public String getRestorable() {
		return null;
	}

	@Override
	public TreeNode[] getPathToRoot(TreeNode node) {
		return null;
	}

	@Override
	public MindMapLinkRegistry getLinkRegistry() {
		return null;
	}

	@Override
	public void destroy() {
	}

	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Override
	public Filter getFilter() {
		return null;
	}

	@Override
	public void setFilter(Filter inactiveFilter) {
	}

	@Override
	public Object getRoot() {
		return null;
	}

	@Override
	public Object getChild(Object parent, int index) {
		return null;
	}

	@Override
	public int getChildCount(Object parent) {
		return 0;
	}

	@Override
	public boolean isLeaf(Object node) {
		return false;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		return 0;
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
	}

	@Override
	public MindMapNode getRootNode() {
		return null;
	}

	@Override
	public void nodeStructureChanged(TreeNode node) {
	}

	@Override
	public boolean setSaved(boolean isSaved) {
		return true;
	}

	@Override
	public boolean isSaved() {
		return false;
	}

	@Override
	public boolean save(File pFile) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void registerMapSourceChangedObserver(
			MapSourceChangedObserver pMapSourceChangedObserver,
			long pGetEventIfChangedAfterThisTimeInMillies) {
		// TODO Auto-generated method stub

	}

	@Override
	public long deregisterMapSourceChangedObserver(
			MapSourceChangedObserver pMapSourceChangedObserver) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.MindMap#changeRoot(freemind.modes.MindMapNode)
	 */
	@Override
	public void changeRoot(MindMapNode pNewRoot) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see freemind.modes.MindMap#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean pIsReadOnly) {
		
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MindMap#getMapFeedback()
	 */
	@Override
	public MapFeedback getMapFeedback() {
		return new MapFeedbackAdapter() {

			@Override
			public MindMap getMap() {
				return MindMapMock.this;
			}

		};
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MindMap#getIcons()
	 */
	@Override
	public SortedListModel getIcons() {
		return new SortedMapListModel();
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MindMap#loadTree(freemind.modes.ModeController.ReaderCreator, freemind.modes.MapAdapter.AskUserBeforeUpdateCallback)
	 */
	@Override
	public MindMapNode loadTree(Tools.ReaderCreator pReaderCreator, AskUserBeforeUpdateCallback pAskUserBeforeUpdateCallback) throws XMLParseException,
			IOException {
		return null;
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MindMap#createNodeTreeFromXml(java.io.Reader, java.util.HashMap)
	 */
	@Override
	public MindMapNode createNodeTreeFromXml(Reader pReader, HashMap pIDToTarget)
			throws XMLParseException, IOException {
		return null;
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MindMap#createNodeAdapter(freemind.modes.MindMap, java.lang.String)
	 */
	@Override
	public NodeAdapter createNodeAdapter(MindMap pMap, String pNodeClass) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MindMap#createEdgeAdapter(freemind.modes.NodeAdapter)
	 */
	@Override
	public EdgeAdapter createEdgeAdapter(NodeAdapter pNode) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MindMap#createCloudAdapter(freemind.modes.NodeAdapter)
	 */
	@Override
	public CloudAdapter createCloudAdapter(NodeAdapter pNode) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MindMap#createArrowLinkAdapter(freemind.modes.NodeAdapter, freemind.modes.NodeAdapter)
	 */
	@Override
	public ArrowLinkAdapter createArrowLinkAdapter(NodeAdapter pSource,
			NodeAdapter pTarget) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MindMap#createArrowLinkTarget(freemind.modes.NodeAdapter, freemind.modes.NodeAdapter)
	 */
	@Override
	public ArrowLinkTarget createArrowLinkTarget(NodeAdapter pSource,
			NodeAdapter pTarget) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MindMap#createEncryptedNode(java.lang.String)
	 */
	@Override
	public NodeAdapter createEncryptedNode(String pAdditionalInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MindMap#insertNodeInto(freemind.modes.MindMapNode, freemind.modes.MindMapNode, int)
	 */
	@Override
	public void insertNodeInto(MindMapNode pNode, MindMapNode pParentNode,
			int pIndex) {
		
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MindMap#removeNodeFromParent(freemind.modes.MindMapNode)
	 */
	@Override
	public void removeNodeFromParent(MindMapNode pNode) {
	}

}
