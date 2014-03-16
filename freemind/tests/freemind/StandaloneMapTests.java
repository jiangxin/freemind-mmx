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

import java.awt.Font;
import java.io.StringWriter;

import freemind.main.FreeMind;
import freemind.modes.MapAdapter;
import freemind.modes.MapFeedbackAdapter;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController.StringReaderCreator;
import freemind.modes.mindmapmode.MindMapMapModel;


/**
 * @author foltin
 * @date 16.03.2014
 */
public class StandaloneMapTests extends FreeMindTestBase {
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
		
		@Override
		public Font getDefaultFont() {
			int fontSize = 12;
			int fontStyle = 0;
			String fontFamily = "SansSerif";

			return getFontThroughMap(new Font(fontFamily, fontStyle, fontSize));
		}
	}
	
	public void testStandaloneCreation() throws Exception {
		DemoMapFeedback mapFeedback = new DemoMapFeedback();
		final MindMapMapModel mMap = new MindMapMapModel(mapFeedback);
		mapFeedback.mMap = mMap;
		StringReaderCreator readerCreator = new StringReaderCreator(
				"<map>"
				+ "<node TEXT='ROOT'>"
				+ "<node TEXT='FormatMe'>"
				+ "<node TEXT='Child1'/><node TEXT='Child2'/>"
				+ "</node>"
				+ "</node>"
				+ "</map>");
		MindMapNode root = mMap.loadTree(readerCreator,
				MapAdapter.sDontAskInstance);
		mMap.setRoot(root);
		MindMapNode mDemoNode = (MindMapNode) root.getChildAt(0);
		StringWriter stringWriter = new StringWriter();
		mMap.getFilteredXml(stringWriter);
		String xmlResult = stringWriter.getBuffer().toString();
		xmlResult = xmlResult.replaceAll("CREATED=\"[0-9]*\"", "CREATED=\"\"");
		xmlResult = xmlResult.replaceAll("MODIFIED=\"[0-9]*\"", "MODIFIED=\"\"");
		String expected = "<map version=\"" + FreeMind.XML_VERSION +  "\">\n"
				+ "<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->\n"
				+ "<node CREATED=\"\" MODIFIED=\"\" TEXT=\"ROOT\">\n"
				+ "<node CREATED=\"\" MODIFIED=\"\" POSITION=\"right\" TEXT=\"FormatMe\">\n"
				+ "<node CREATED=\"\" MODIFIED=\"\" TEXT=\"Child1\"/>\n"
				+ "<node CREATED=\"\" MODIFIED=\"\" TEXT=\"Child2\"/>\n"
				+ "</node>\n" 
				+ "</node>\n" 
				+ "</map>\n";
		assertEquals(expected, xmlResult);
	}

}
