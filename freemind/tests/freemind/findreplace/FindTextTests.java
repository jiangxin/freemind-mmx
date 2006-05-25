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
/*$Id: FindTextTests.java,v 1.1.2.2 2006-05-25 21:38:36 christianfoltin Exp $*/

package tests.freemind.findreplace;

import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.table.AbstractTableModel;

import junit.framework.TestCase;
import plugins.time.FlatNodeTableFilterModel;
import plugins.time.TimeList;
import plugins.time.TimeList.NodeHolder;
import freemind.main.HtmlTools;
import freemind.main.Tools;

public class FindTextTests extends TestCase {

    public void testTagRemoval() throws Exception {
        String input = "<html>test</html>";
        assertEquals("tags are removed:", "test", Tools
                .removeHtmlTagsFromString(input));
    }

    public void testTagRemovalWithNewlines() throws Exception {
        String input = "<html><strong\nref=\"test2\">test</strong></html\n>";
        assertEquals("tags are removed:", "test", Tools
                .removeHtmlTagsFromString(input));
    }

    public void testTagRemovalOnlyForHtmlText() throws Exception {
        String input = "test<test>";
        assertEquals("nothing is removed:", input, Tools
                .removeHtmlTagsFromString(input));
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
                testMindMapNode.setText("<html><strong>test</strong></html>");
                return new TimeList.NodeHolder(testMindMapNode);
            }
        };
        FlatNodeTableFilterModel dut = new FlatNodeTableFilterModel(tm, 0);
        assertEquals("One row:", 1, dut.getRowCount());
        dut.setFilter("strong");
        assertEquals("No row:", 0, dut.getRowCount());
        dut.setFilter("test");
        assertEquals("One row:", 1, dut.getRowCount());
    }
    
    public void testPositions() throws Exception {
        HtmlTools.IndexPair pair1 = new HtmlTools.IndexPair(0,6,0,0);
        HtmlTools.IndexPair pair2 = new HtmlTools.IndexPair(6,14,0,0);
        HtmlTools.IndexPair pair3 = new HtmlTools.IndexPair(14,27,0,4);
        HtmlTools.IndexPair pair4 = new HtmlTools.IndexPair(27,34,4,4);
        HtmlTools.IndexPair pair5 = new HtmlTools.IndexPair(34,34,4,4);
        ArrayList list = new ArrayList();
        list.add(pair1);
        list.add(pair2);
        list.add(pair3);
        list.add(pair4);
        list.add(pair5);
        assertEquals(0,HtmlTools.getInstance().getMinimalOriginalPosition(0, list));
        assertEquals(14,HtmlTools.getInstance().getMaximalOriginalPosition(0, list));
        assertEquals(27,HtmlTools.getInstance().getMinimalOriginalPosition(4, list));
        assertEquals(34,HtmlTools.getInstance().getMaximalOriginalPosition(4, list));
    }
    
    public void testDirectReplace() throws Exception {
        assertEquals("<html><strong>blabla</strong></html>", HtmlTools
                .getInstance().getReplaceResult(Pattern.compile("test"),
                        "blabla", "<html><strong>test</strong></html>"));

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
        // and replaces tex<tag>t</tag> by text<tag></tag>:
        info = new TestReplaceInputInfo("<html><strong>tes</strong>t</html>",
                "<html>blabla<strong></strong></html>");
        TimeList.replace(info, "text", "blabla");
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
