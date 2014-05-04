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
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Vector;

import freemind.controller.actions.generated.instance.Pattern;
import freemind.main.FreeMind;
import freemind.main.Tools;
import freemind.modes.EdgeAdapter;
import freemind.modes.ExtendedMapFeedbackImpl;
import freemind.modes.MapAdapter;
import freemind.modes.MindIcon;
import freemind.modes.MindMapArrowLink;
import freemind.modes.MindMapLink;
import freemind.modes.MindMapNode;
import freemind.modes.StylePatternFactory;
import freemind.modes.attributes.Attribute;
import freemind.modes.mindmapmode.MindMapController.StringReaderCreator;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.MindMapNodeModel;

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

	public void testStandaloneCreation() throws Exception {
		ExtendedMapFeedbackImpl mapFeedback = new ExtendedMapFeedbackImpl();
		final MindMapMapModel mMap = new MindMapMapModel(mapFeedback);
		mapFeedback.setMap(mMap);
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
		ExtendedMapFeedbackImpl mapFeedback = new ExtendedMapFeedbackImpl();
		final MindMapMapModel mMap = new MindMapMapModel(mapFeedback);
		mapFeedback.setMap(mMap);
		StringReaderCreator readerCreator = new StringReaderCreator(INITIAL_MAP);
		MindMapNode root = mMap.loadTree(readerCreator,
				MapAdapter.sDontAskInstance);
		mMap.setRoot(root);
		MindMapNode firstChild = (MindMapNode) root.getChildAt(0);
		MindMapNode subChild1 = (MindMapNode) firstChild.getChildAt(0);
		MindMapNode subChild2 = (MindMapNode) firstChild.getChildAt(1);
		MindMapNode subChild3 = (MindMapNode) firstChild.getChildAt(2);
		mapFeedback.setBold(root, true);
		assertEquals(true, root.isBold());
		mapFeedback.setItalic(root, true);
		assertEquals(true, root.isItalic());
		int amount = root.getChildCount();
		MindMapNode newNode = mapFeedback.addNewNode(root, 0,
				true);
		assertEquals(amount + 1, root.getChildCount());
		mapFeedback.deleteNode(newNode);
		newNode = null;
		assertEquals(amount, root.getChildCount());
		try {
			mapFeedback.deleteNode(root);
			assertTrue("Must throw.", false);
		} catch (IllegalArgumentException e) {
		}
		mapFeedback.paste(new StringSelection("bla"), root, false,
				true);
		assertEquals(amount + 1, root.getChildCount());
		assertEquals(0, root.getIcons().size());
		MindIcon icon = MindIcon.factory("attach");
		mapFeedback.addIcon(root, icon);
		assertEquals(1, root.getIcons().size());
		mapFeedback.removeLastIcon(root);
		assertEquals(0, root.getIcons().size());
		mapFeedback.removeLastIcon(root);
		mapFeedback.addIcon(root, icon);
		mapFeedback.addIcon(root, icon);
		mapFeedback.addIcon(root, icon);
		mapFeedback.addIcon(root, icon);
		mapFeedback.addIcon(root, icon);
		assertEquals(5, root.getIcons().size());
		mapFeedback.removeAllIcons(root);
		assertEquals(0, root.getIcons().size());
		// cloud
		mapFeedback.setCloud(firstChild, true);
		assertNotNull(firstChild.getCloud());
		mapFeedback.setCloudColor(firstChild, Color.CYAN);
		assertEquals(Color.CYAN, firstChild.getCloud().getColor());
		mapFeedback.setCloud(firstChild, false);
		assertNull(firstChild.getCloud());
		// edges
		try {
			mapFeedback.setEdgeStyle(firstChild, "bluber");
			assertTrue("Must throw.", false);
		} catch (Exception e) {
		}
		mapFeedback.setEdgeStyle(firstChild,
				EdgeAdapter.EDGESTYLE_SHARP_BEZIER);
		assertTrue(firstChild.getEdge().hasStyle());
		assertEquals(EdgeAdapter.EDGESTYLE_SHARP_BEZIER, firstChild.getEdge()
				.getStyle());
		mapFeedback.setEdgeStyle(firstChild, null);
		assertFalse(firstChild.getEdge().hasStyle());
		assertEquals(EdgeAdapter.EDGESTYLE_BEZIER, firstChild.getEdge()
				.getStyle());
		mapFeedback.setEdgeWidth(firstChild, 8);
		assertEquals(8, firstChild.getEdge().getWidth());
		mapFeedback.setEdgeWidth(firstChild,
				EdgeAdapter.WIDTH_THIN);
		assertEquals(EdgeAdapter.WIDTH_THIN, firstChild.getEdge().getWidth());
		mapFeedback.setEdgeColor(firstChild, Color.GREEN);
		assertEquals(Color.GREEN, firstChild.getEdge().getColor());
		GraphicsEnvironment ge = null;
		ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] fontNames = ge.getAvailableFontFamilyNames();
		if (fontNames.length > 0) {
			String fontFamilyValue = fontNames[0];
			mapFeedback.setFontFamily(firstChild,
					fontFamilyValue);
			assertEquals(fontFamilyValue, firstChild.getFontFamilyName());
		}
		String fontSizeValue = "32";
		mapFeedback.setFontSize(firstChild, fontSizeValue);
		assertEquals(fontSizeValue, firstChild.getFontSize());
		mapFeedback.moveNodePosition(firstChild, 20, 30, 17);
		assertEquals(20, root.getVGap());
		assertEquals(30, firstChild.getHGap());
		assertEquals(17, firstChild.getShiftY());
		mapFeedback.setNodeStyle(firstChild, MindMapNode.STYLE_FORK);
		assertEquals(MindMapNodeModel.STYLE_FORK, firstChild.getStyle());
		assertTrue(firstChild.hasStyle());
		mapFeedback.setNodeStyle(firstChild, null);
		assertFalse(firstChild.hasStyle());
		try {
			mapFeedback.setNodeStyle(firstChild, "bla");
			assertTrue("Must throw.", false);
		} catch (Exception e) {
		}
		// underline not implemented
//		mapFeedback.setUnderlined(firstChild, true);
//		assertTrue(firstChild.isUnderlined());
		// arrow links
		mapFeedback.addLink(subChild1, subChild2);
		Vector<MindMapLink> mapLinks = mapFeedback.getMap().getLinkRegistry().getAllLinksFromMe(subChild1);
		assertEquals(1, mapLinks.size());
		MindMapArrowLink mapLink = (MindMapArrowLink) mapLinks.firstElement();
		assertEquals(subChild2, mapLink.getTarget());
		Point startPoint = new Point(40,50);
		Point endPoint = new Point(-10,-20);
		mapFeedback.setArrowLinkEndPoints( mapLink, startPoint, endPoint);
		assertEquals(startPoint, mapLink.getStartInclination());
		assertEquals(endPoint, mapLink.getEndInclination());
		mapFeedback.changeArrowsOfArrowLink(mapLink, true, false);
		assertEquals(MindMapArrowLink.ARROW_DEFAULT, mapLink.getStartArrow());
		assertEquals(MindMapArrowLink.ARROW_NONE, mapLink.getEndArrow());
		mapFeedback.setArrowLinkColor(mapLink, Color.RED);
		assertEquals(Color.RED, mapLink.getColor());
		mapFeedback.removeReference(mapLink);
		mapLinks = mapFeedback.getMap().getLinkRegistry().getAllLinksFromMe(subChild1);
		assertEquals(0, mapLinks.size());
		String newText = "blabla";
		mapFeedback.setNodeText(firstChild, newText);
		assertEquals(newText, firstChild.getText());
		Color darkGray = Color.DARK_GRAY;
		mapFeedback.setNodeBackgroundColor(firstChild, darkGray);
		assertEquals(darkGray, firstChild.getBackgroundColor());
		mapFeedback.setNodeColor(firstChild, darkGray);
		assertEquals(darkGray, firstChild.getColor());
//		// hooks: currently disabled, as too dependent from MindMapController
//		mapFeedback.addHook(firstChild,
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
//		mapFeedback.addHook(firstChild,
//				Tools.getVectorWithSingleElement(firstChild),
//				"accessories/plugins/BlinkingNodeHook.properties", null);
		assertEquals(0, firstChild.getIndex(subChild1));
		mapFeedback.moveNodes(subChild1, Tools.getVectorWithSingleElement(subChild1), 1);
		assertEquals(1, firstChild.getIndex(subChild1));
		mapFeedback.moveNodes(subChild1, Tools.getVectorWithSingleElement(subChild1), -1);
		assertEquals(0, firstChild.getIndex(subChild1));
		mapFeedback.moveNodes(subChild1, Tools.getVectorWithSingleElement(subChild1), -1);
		assertEquals(2, firstChild.getIndex(subChild1));
		mapFeedback.setFolded(firstChild, true);
		assertTrue(firstChild.isFolded());
		String link = "http://freemind.sf.net/";
		mapFeedback.setLink(subChild3, link);
		assertEquals(link, subChild3.getLink());
		// attributes
		mapFeedback.addAttribute(root, new Attribute("name", "value"));
		assertEquals(1, root.getAttributeTableLength());
		assertEquals("value", root.getAttribute("name"));
		mapFeedback.setAttribute(root, 0, new Attribute("oname", "ovalue"));
		assertEquals(1, root.getAttributeTableLength());
		assertEquals("ovalue", root.getAttribute("oname"));
		assertEquals(null, root.getAttribute("name"));
		mapFeedback.insertAttribute(root, 0, new Attribute("0name", "0"));
		assertEquals(2, root.getAttributeTableLength());
		assertEquals("0", root.getAttribute("0name"));
		mapFeedback.removeAttribute(root, 1);
		assertEquals(1, root.getAttributeTableLength());
		assertEquals("0", root.getAttribute("0name"));
		assertEquals(null, root.getAttribute("oname"));
		// cut
		assertEquals(3, firstChild.getChildCount());
		mapFeedback.cut(Tools.getVectorWithSingleElement(subChild3));
		assertEquals(2, firstChild.getChildCount());
		subChild3=null;
		// note
		String htmlText = "<html><body>blaNOTE</body></html>";
		mapFeedback.setNoteText(subChild2, htmlText);
		assertEquals(htmlText, subChild2.getNoteText());
		// patterns
		mapFeedback.setNodeColor(subChild1, Color.MAGENTA);
		Pattern p = StylePatternFactory.createPatternFromNode(subChild1);
		assertNotNull(p.getPatternNodeColor());
		assertEquals(Tools.colorToXml(Color.MAGENTA), p.getPatternNodeColor().getValue());
		mapFeedback.applyPattern(subChild2, p);
		assertEquals(Color.MAGENTA, subChild2.getColor());
		
		String xmlResult = getMapContents(mMap);
		System.out.println(xmlResult);

	}
}
