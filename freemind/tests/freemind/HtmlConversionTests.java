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
/*$Id: HtmlConversionTests.java,v 1.1.2.6 2006-10-10 18:51:53 christianfoltin Exp $*/

package tests.freemind;

import java.io.StringReader;

import freemind.main.HtmlTools;
import freemind.main.XMLElement;
import freemind.modes.mindmapmode.MindMapNodeModel;

public class HtmlConversionTests extends FreeMindTestBase {

    
    public void testSetHtml() throws Exception {
        MindMapNodeModel node = new MindMapNodeModel(getFrame(), new MindMapMock("</map>"));
        node.setText("test");
        // wiped out: <?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"DTD/xhtml1-transitional.dtd\">\n
        assertEquals("proper conversion", "<html>\n  <head>\n    \n  </head>\n  <body>\n    test\n  </body>\n</html>\n", node.getXmlText());
        node.setXmlText("test");
        
        assertEquals("proper conversion", "test", node.getText());
        node.setText("<html><br>");
        assertEquals("proper html conversion", "<html>\n  <head>\n    \n  </head>\n  <body>\n    <br />\n  </body>\n</html>\n", node.getXmlText());
        // must remove the '/' in <br/>.
        node.setXmlText("<html><br/></html>");
        assertEquals("proper html conversion", "<html><br></html>", node.getText());
        node.setXmlText("<html><br /></html>");
        assertEquals("proper html conversion", "<html><br ></html>", node.getText());
        
    }
    
    public void testEndContentMatcher() throws Exception {
        matchingTest("</" + XMLElement.XML_NODE_XHTML_CONTENT_TAG + ">");
        matchingTest("</ " + XMLElement.XML_NODE_XHTML_CONTENT_TAG + ">");
        matchingTest("</ " + XMLElement.XML_NODE_XHTML_CONTENT_TAG + " >");
        matchingTest("< /\n" + XMLElement.XML_NODE_XHTML_CONTENT_TAG + " >");
    }

    /**
     */
    private void matchingTest(String string) {
        assertTrue(string.matches(XMLElement.XML_NODE_XHTML_CONTENT_END_TAG_REGEXP));
    }
    
    public void testNanoXmlContent() throws Exception {
        XMLElement element = new XMLElement();
        element.parseFromReader(new StringReader("<" + XMLElement.XML_NODE_XHTML_CONTENT_TAG + "><body>a<b>cd</b>e</body></" + XMLElement.XML_NODE_XHTML_CONTENT_TAG + ">"));
        assertEquals("end " + XMLElement.XML_NODE_XHTML_CONTENT_TAG + " tag removed", "<body>a<b>cd</b>e</body>", element.getContent());
    }
    
    public void testXHtmlToHtmlConversion() throws Exception {
        assertEquals("br removal", "<br >", HtmlTools.getInstance().toHtml("<br />"));
        assertEquals("br removal, not more.", "<brmore/>", HtmlTools.getInstance().toHtml("<brmore/>"));
    }
    
    public void testWellFormedXml() throws Exception {
        assertEquals(true, HtmlTools.getInstance().isWellformedXml("<a></a>"));
//        assertEquals(true, HtmlTools.getInstance().isWellformedXml("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"DTD/xhtml1-transitional.dtd\">\n<a></a>"));
        assertEquals(true, HtmlTools.getInstance().isWellformedXml("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<a></a>"));
        assertEquals(false, HtmlTools.getInstance().isWellformedXml("<a><a></a>"));
        
    }
}

