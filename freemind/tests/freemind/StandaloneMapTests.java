/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2014 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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

package tests.freemind;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Vector;

import freemind.controller.MindMapNodesSelection;
import freemind.main.FreeMind;
import freemind.main.Tools;
import freemind.modes.EdgeAdapter;
import freemind.modes.ExtendedMapFeedbackAdapter;
import freemind.modes.MapAdapter;
import freemind.modes.MindIcon;
import freemind.modes.MindMap;
import freemind.modes.MindMapArrowLink;
import freemind.modes.MindMapLink;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController.StringReaderCreator;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.MindMapNodeModel;
import freemind.modes.mindmapmode.actions.xml.DefaultActionHandler;
import freemind.modes.mindmapmode.actions.xml.actors.XmlActorFactory;

/**
 * @author foltin
 * @date 16.03.2014
 */
public class StandaloneMapTests extends FreeMindTestBase {
	/**
	 * 
	 */
	private static final String INITIAL_MAP = "<map>" + "<node TEXT='ROOT'>"
			+ "<node TEXT='FormatMe'>"
			+ "<node TEXT='Child1'/>"
			+ "<node TEXT='Child2'/>" 
			+ "<node TEXT='Child3'/>" 
			+ "</node>"
			+ "</node>" + "</map>";

	/**
	 * @author foltin
	 * @date 21.02.2014
	 */
	private final class DemoMapFeedback extends ExtendedMapFeedbackAdapter {
		MindMap mMap;

		@Override
		public MindMap getMap() {
			return mMap;
		}

		@Override
		public MindMapNode newNode(Object pUserObject, MindMap pMap) {
			return new MindMapNodeModel(pUserObject, pMap);
		}

		@Override
		public Font getDefaultFont() {
			int fontSize = 12;
			int fontStyle = 0;
			String fontFamily = "SansSerif";

			return getFontThroughMap(new Font(fontFamily, fontStyle, fontSize));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * freemind.modes.ExtendedMapFeedbackAdapter#copy(freemind.modes.MindMapNode
		 * , boolean)
		 */
		@Override
		public Transferable copy(MindMapNode pNode, boolean pSaveInvisible) {
			StringWriter stringWriter = new StringWriter();
			try {
				((MindMapNodeModel) pNode).save(stringWriter, getMap()
						.getLinkRegistry(), pSaveInvisible, true);
			} catch (IOException e) {
			}
			Vector nodeList = Tools
					.getVectorWithSingleElement(getNodeID(pNode));
			return new MindMapNodesSelection(stringWriter.toString(), null,
					null, null, null, null, null, nodeList);
		}
	}

	public void testStandaloneCreation() throws Exception {
		DemoMapFeedback mapFeedback = new DemoMapFeedback();
		final MindMapMapModel mMap = new MindMapMapModel(mapFeedback);
		mapFeedback.mMap = mMap;
		StringReaderCreator readerCreator = new StringReaderCreator(INITIAL_MAP);
		MindMapNode root = mMap.loadTree(readerCreator,
				MapAdapter.sDontAskInstance);
		mMap.setRoot(root);
		String xmlResult = getMapContents(mMap);
		xmlResult = xmlResult.replaceAll("CREATED=\"[0-9]*\"", "CREATED=\"\"");
		xmlResult = xmlResult
				.replaceAll("MODIFIED=\"[0-9]*\"", "MODIFIED=\"\"");
		String expected = "<map version=\""
				+ FreeMind.XML_VERSION
				+ "\">\n"
				+ "<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->\n"
				+ "<node CREATED=\"\" MODIFIED=\"\" TEXT=\"ROOT\">\n"
				+ "<node CREATED=\"\" MODIFIED=\"\" POSITION=\"right\" TEXT=\"FormatMe\">\n"
				+ "<node CREATED=\"\" MODIFIED=\"\" TEXT=\"Child1\"/>\n"
				+ "<node CREATED=\"\" MODIFIED=\"\" TEXT=\"Child2\"/>\n"
				+ "<node CREATED=\"\" MODIFIED=\"\" TEXT=\"Child3\"/>\n"
				+ "</node>\n" + "</node>\n" + "</map>\n";
		assertEquals(expected, xmlResult);
	}

	protected String getMapContents(final MindMapMapModel mMap)
			throws IOException {
		StringWriter stringWriter = new StringWriter();
		mMap.getFilteredXml(stringWriter);
		String xmlResult = stringWriter.getBuffer().toString();
		return xmlResult;
	}

	public void testXmlChangeWithoutModeController() throws Exception {
		DemoMapFeedback mapFeedback = new DemoMapFeedback();
		mapFeedback.getActionRegistry().registerHandler(
				new DefaultActionHandler(mapFeedback.getActionRegistry()));

		final MindMapMapModel mMap = new MindMapMapModel(mapFeedback);
		mapFeedback.mMap = mMap;
		StringReaderCreator readerCreator = new StringReaderCreator(INITIAL_MAP);
		MindMapNode root = mMap.loadTree(readerCreator,
				MapAdapter.sDontAskInstance);
		mMap.setRoot(root);
		MindMapNode firstChild = (MindMapNode) root.getChildAt(0);
		MindMapNode subChild1 = (MindMapNode) firstChild.getChildAt(0);
		MindMapNode subChild2 = (MindMapNode) firstChild.getChildAt(1);
		MindMapNode subChild3 = (MindMapNode) firstChild.getChildAt(2);
		XmlActorFactory factory = mapFeedback.getActorFactory();
		factory.getBoldActor().setBold(root, true);
		assertEquals(true, root.isBold());
		factory.getItalicActor().setItalic(root, true);
		assertEquals(true, root.isItalic());
		int amount = root.getChildCount();
		MindMapNode newNode = factory.getNewChildActor().addNewNode(root, 0,
				true);
		assertEquals(amount + 1, root.getChildCount());
		factory.getDeleteChildActor().deleteWithoutUndo(newNode);
		assertEquals(amount, root.getChildCount());
		try {
			factory.getDeleteChildActor().deleteWithoutUndo(root);
			assertTrue("Must throw.", false);
		} catch (IllegalArgumentException e) {
		}
		factory.getPasteActor().paste(new StringSelection("bla"), root, false,
				true);
		assertEquals(amount + 1, root.getChildCount());
		assertEquals(0, root.getIcons().size());
		MindIcon icon = MindIcon.factory("attach");
		factory.getAddIconActor().addIcon(root, icon);
		assertEquals(1, root.getIcons().size());
		factory.getRemoveIconActor().removeLastIcon(root);
		assertEquals(0, root.getIcons().size());
		factory.getRemoveIconActor().removeLastIcon(root);
		factory.getAddIconActor().addIcon(root, icon);
		factory.getAddIconActor().addIcon(root, icon);
		factory.getAddIconActor().addIcon(root, icon);
		factory.getAddIconActor().addIcon(root, icon);
		factory.getAddIconActor().addIcon(root, icon);
		assertEquals(5, root.getIcons().size());
		factory.getRemoveAllIconsActor().removeAllIcons(root);
		assertEquals(0, root.getIcons().size());
		// cloud
		factory.getCloudActor().setCloud(firstChild, true);
		assertNotNull(firstChild.getCloud());
		factory.getCloudColorActor().setCloudColor(firstChild, Color.CYAN);
		assertEquals(Color.CYAN, firstChild.getCloud().getColor());
		factory.getCloudActor().setCloud(firstChild, false);
		assertNull(firstChild.getCloud());
		// edges
		try {
			factory.getEdgeStyleActor().setEdgeStyle(firstChild, "bluber");
			assertTrue("Must throw.", false);
		} catch (Exception e) {
		}
		factory.getEdgeStyleActor().setEdgeStyle(firstChild,
				EdgeAdapter.EDGESTYLE_SHARP_BEZIER);
		assertTrue(firstChild.getEdge().hasStyle());
		assertEquals(EdgeAdapter.EDGESTYLE_SHARP_BEZIER, firstChild.getEdge()
				.getStyle());
		factory.getEdgeStyleActor().setEdgeStyle(firstChild, null);
		assertFalse(firstChild.getEdge().hasStyle());
		assertEquals(EdgeAdapter.EDGESTYLE_BEZIER, firstChild.getEdge()
				.getStyle());
		factory.getEdgeWidthActor().setEdgeWidth(firstChild, 8);
		assertEquals(8, firstChild.getEdge().getWidth());
		factory.getEdgeWidthActor().setEdgeWidth(firstChild,
				EdgeAdapter.WIDTH_THIN);
		assertEquals(EdgeAdapter.WIDTH_THIN, firstChild.getEdge().getWidth());
		factory.getEdgeColorActor().setEdgeColor(firstChild, Color.GREEN);
		assertEquals(Color.GREEN, firstChild.getEdge().getColor());
		GraphicsEnvironment ge = null;
		ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] fontNames = ge.getAvailableFontFamilyNames();
		if (fontNames.length > 0) {
			String fontFamilyValue = fontNames[0];
			factory.getFontFamilyActor().setFontFamily(firstChild,
					fontFamilyValue);
			assertEquals(fontFamilyValue, firstChild.getFontFamilyName());
		}
		String fontSizeValue = "32";
		factory.getFontSizeActor().setFontSize(firstChild, fontSizeValue);
		assertEquals(fontSizeValue, firstChild.getFontSize());
		factory.getMoveNodeActor().moveNodeTo(firstChild, 20, 30, 17);
		assertEquals(20, root.getVGap());
		assertEquals(30, firstChild.getHGap());
		assertEquals(17, firstChild.getShiftY());
		factory.getNodeStyleActor()
				.setStyle(firstChild, MindMapNode.STYLE_FORK);
		assertEquals(MindMapNodeModel.STYLE_FORK, firstChild.getStyle());
		try {
			factory.getNodeStyleActor().setStyle(firstChild, "bla");
			assertTrue("Must throw.", false);
		} catch (Exception e) {
		}
		// underline not implemented
		factory.getUnderlineActor().setUnderlined(firstChild, true);
		assertTrue(firstChild.isUnderlined());
		// arrow links
		factory.getAddArrowLinkActor().addLink(subChild1, subChild2);
		Vector<MindMapLink> mapLinks = mapFeedback.getMap().getLinkRegistry().getAllLinksFromMe(subChild1);
		assertEquals(1, mapLinks.size());
		MindMapArrowLink mapLink = (MindMapArrowLink) mapLinks.firstElement();
		assertEquals(subChild2, mapLink.getTarget());
		Point startPoint = new Point(40,50);
		Point endPoint = new Point(-10,-20);
		factory.getChangeArrowLinkEndPointsActor().setArrowLinkEndPoints( mapLink, startPoint, endPoint);
		assertEquals(startPoint, mapLink.getStartInclination());
		assertEquals(endPoint, mapLink.getEndInclination());
		factory.getChangeArrowsInArrowLinkActor().changeArrowsOfArrowLink(mapLink, true, false);
		assertEquals(MindMapArrowLink.ARROW_DEFAULT, mapLink.getStartArrow());
		assertEquals(MindMapArrowLink.ARROW_NONE, mapLink.getEndArrow());
		factory.getColorArrowLinkActor().setArrowLinkColor(mapLink, Color.RED);
		assertEquals(Color.RED, mapLink.getColor());
		factory.getRemoveArrowLinkActor().removeReference(mapLink);
		mapLinks = mapFeedback.getMap().getLinkRegistry().getAllLinksFromMe(subChild1);
		assertEquals(0, mapLinks.size());
		String newText = "blabla";
		factory.getEditActor().setNodeText(firstChild, newText);
		assertEquals(newText, firstChild.getText());
		Color darkGray = Color.DARK_GRAY;
		factory.getNodeBackgroundColorActor().setNodeBackgroundColor(firstChild, darkGray);
		assertEquals(darkGray, firstChild.getBackgroundColor());
		factory.getNodeColorActor().setNodeColor(firstChild, darkGray);
		assertEquals(darkGray, firstChild.getColor());
//		// hooks: currently disabled, as too dependent from MindMapController
//		factory.getAddHookActor().addHook(firstChild,
//				Tools.getVectorWithSingleElement(firstChild),
//				"accessories/plugins/BlinkingNodeHook.properties", null);
//		int timeout = 10;
//		Color nodeColor = firstChild.getColor();
//		boolean found = false;
//		while(timeout -- > 0) {
//			if(firstChild.getColor() != nodeColor) {
//				found = true;
//				break;
//			}
//			Thread.sleep(1000);
//		}
//		assertTrue(found);
//		factory.getAddHookActor().addHook(firstChild,
//				Tools.getVectorWithSingleElement(firstChild),
//				"accessories/plugins/BlinkingNodeHook.properties", null);
		assertEquals(0, firstChild.getIndex(subChild1));
		factory.getNodeUpActor().moveNodes(subChild1, Tools.getVectorWithSingleElement(subChild1), 1);
		assertEquals(1, firstChild.getIndex(subChild1));
		factory.getNodeUpActor().moveNodes(subChild1, Tools.getVectorWithSingleElement(subChild1), -1);
		assertEquals(0, firstChild.getIndex(subChild1));
		factory.getNodeUpActor().moveNodes(subChild1, Tools.getVectorWithSingleElement(subChild1), -1);
		assertEquals(2, firstChild.getIndex(subChild1));
		factory.getToggleFoldedActor().setFolded(firstChild, true);
		assertTrue(firstChild.isFolded());
		String xmlResult = getMapContents(mMap);
		System.out.println(xmlResult);

	}
}
