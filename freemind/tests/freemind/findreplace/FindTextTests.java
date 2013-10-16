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
/*$Id: FindTextTests.java,v 1.1.2.8 2008/12/09 21:09:43 christianfoltin Exp $*/

package tests.freemind.findreplace;

import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.table.AbstractTableModel;

import tests.freemind.FreeMindTestBase;
import accessories.plugins.time.FlatNodeTableFilterModel;
import accessories.plugins.time.TimeList;
import accessories.plugins.time.TimeList.NodeHolder;
import freemind.main.HtmlTools;

public class FindTextTests extends FreeMindTestBase {

	public void testTagRemoval() throws Exception {
		String input = "<html>test</html>";
		assertEquals("tags are removed:", "test",
				HtmlTools.removeHtmlTagsFromString(input));
	}

	public void testTagRemovalWithNewlines() throws Exception {
		String input = "<html><strong\nref=\"test2\">test</strong></html\n>";
		assertEquals("tags are removed:", "test",
				HtmlTools.removeHtmlTagsFromString(input));
	}

	public void testTagRemovalOnlyForHtmlText() throws Exception {
		String input = "test<test>";
		assertEquals("nothing is removed:", input,
				HtmlTools.removeHtmlTagsFromString(input));
	}

	/**
	 * Tests that html tags are not matched by the filter:
	 * 
	 * @throws Exception
	 */
	public void testFlatNodeTableFilter() throws Exception {
		AbstractTableModel tm = new AbstractTableModel() {

			public int getRowCount() {
				return 1;
			}

			public int getColumnCount() {
				return 1;
			}

			public Object getValueAt(int rowIndex, int columnIndex) {
				TestMindMapNode testMindMapNode = new TestMindMapNode();
				testMindMapNode
				.setText("<html><strong>test</strong></html>");
				if (columnIndex == 0) {
					return new TimeList.NodeHolder(testMindMapNode);
				} else {
					return new TimeList.NotesHolder(testMindMapNode);
				}
			}
		};
		FlatNodeTableFilterModel dut = new FlatNodeTableFilterModel(tm, 0, 1);
		assertEquals("One row:", 1, dut.getRowCount());
		dut.setFilter("strong");
		assertEquals("No row:", 0, dut.getRowCount());
		dut.setFilter("test");
		assertEquals("One row:", 1, dut.getRowCount());
	}

	public void testPositions() throws Exception {
		HtmlTools.IndexPair pair1 = new HtmlTools.IndexPair(0, 6, 0, 0, false);
		HtmlTools.IndexPair pair2 = new HtmlTools.IndexPair(6, 14, 0, 0, false);
		HtmlTools.IndexPair pair3 = new HtmlTools.IndexPair(14, 27, 0, 4, false);
		HtmlTools.IndexPair pair4 = new HtmlTools.IndexPair(27, 34, 4, 4, false);
		HtmlTools.IndexPair pair5 = new HtmlTools.IndexPair(34, 34, 4, 4, false);
		ArrayList list = new ArrayList();
		list.add(pair1);
		list.add(pair2);
		list.add(pair3);
		list.add(pair4);
		list.add(pair5);
		assertEquals(0,
				HtmlTools.getInstance().getMinimalOriginalPosition(0, list));
		assertEquals(14,
				HtmlTools.getInstance().getMaximalOriginalPosition(0, list));
		assertEquals(18,
				HtmlTools.getInstance().getMinimalOriginalPosition(4, list));
		assertEquals(34,
				HtmlTools.getInstance().getMaximalOriginalPosition(4, list));
	}

	public void testDirectReplace() throws Exception {
		assertEquals(
				"<html><strong>blabla</strong></html>",
				HtmlTools.getInstance().getReplaceResult(
						Pattern.compile("test"), "blabla",
						"<html><strong>test</strong></html>"));
		assertEquals(
				"<html><strong>blabla und so weiter</strong></html>",
				HtmlTools.getInstance().getReplaceResult(
						Pattern.compile("test"), "blabla",
						"<html><strong>test und so weiter</strong></html>"));
		assertEquals(
				"<html><strong>blabla</strong></html>",
				HtmlTools.getInstance().getReplaceResult(
						Pattern.compile("strong"), "strang",
						"<html><strong>blabla</strong></html>"));

	}

	public void testGetPureRegularExpression() {
		executeRegExpRemovalAndTest("input", "input");
		executeRegExpRemovalAndTest("aaa.*bb", "aaa\\..*bb");
		executeRegExpRemovalAndTest("aaa(.*)bb", "aaa\\(\\..*\\)bb");
		executeRegExpRemovalAndTest("$aaa*bb^", "\\$aaa.*bb\\^");
	}

	private void executeRegExpRemovalAndTest(String input, String outputExpected) {
		String result = TimeList.getPureRegularExpression(input);
		assertEquals("remove regexp from '" + input + "' has lead to '"
				+ result + "' but expected was '" + outputExpected + "'",
				outputExpected, result);
	}

	public void testReplaceNodeText() throws Exception {
		// normal text is replaced,
		TimeList.IReplaceInputInformation info = new TestReplaceInputInfo(
				"<html><strong>test</strong></html>",
				"<html><strong>blabla</strong></html>");
		TimeList.replace(info, "test", "blabla");
		// but tags not:
		info = new TestReplaceInputInfo("<html><strong>test</strong></html>",
				"<html><strong>test</strong></html>");
		TimeList.replace(info, "strong", "strang");
		// and replaces <tag>tes</tag>t by <tag>text</tag>:
		info = new TestReplaceInputInfo("<html><strong>tes</strong>t</html>",
				"<html><strong>blabla</strong></html>");
		TimeList.replace(info, "test", "blabla");
		// and replaces tex<tag>t</tag> by text<tag></tag>:
		info = new TestReplaceInputInfo("<html>tes<strong>t</strong></html>",
				"<html>blabla<strong></strong></html>");
		TimeList.replace(info, "test", "blabla");
		// replace with environment
		info = new TestReplaceInputInfo(
				"<html>before<strong>tes</strong>tafter</html>",
				"<html>before<strong>blabla</strong>after</html>");
		TimeList.replace(info, "test", "blabla");
		// no replace at all
		info = new TestReplaceInputInfo("<html><strong>tes</strong>t</html>",
				"<html><strong>tes</strong>t</html>" /*
													 * No change as search text
													 * does not occur
													 */);
		TimeList.replace(info, "text", "blabla");
		// no html at all
		info = new TestReplaceInputInfo("beforetestafter", "beforeblablaafter");
		TimeList.replace(info, "test", "blabla");
	}

	private final class TestReplaceInputInfo implements
			TimeList.IReplaceInputInformation {
		private final String input;

		private final String output;

		private TestReplaceInputInfo(String input, String output) {
			super();
			this.input = input;
			this.output = output;
		}

		public int getLength() {
			return 1;
		}

		public NodeHolder getNodeHolderAt(int i) {
			TestMindMapNode testMindMapNode = new TestMindMapNode();
			testMindMapNode.setText(input);
			return new TimeList.NodeHolder(testMindMapNode);
		}

		public void changeString(NodeHolder holder, String newText) {
			assertEquals("correct replacement", output, newText);
		}
	}

}
