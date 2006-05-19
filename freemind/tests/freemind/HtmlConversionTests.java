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
/*$Id: HtmlConversionTests.java,v 1.1.2.1 2006-05-19 21:27:44 christianfoltin Exp $*/

package tests.freemind;

import java.io.StringReader;

import junit.framework.TestCase;
import freemind.main.XMLElement;
import freemind.modes.mindmapmode.MindMapNodeModel;

public class HtmlConversionTests extends TestCase {

    
    public void testSetHtml() throws Exception {
        MindMapNodeModel node = new MindMapNodeModel(new FreeMindMainMock(), new MindMapMock());
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
        assertEquals("proper html conversion", "<html><br></html>", node.getText());
        
    }
    
    public void testEndContentMatcher() throws Exception {
        assertTrue("</nodecontent>".matches(XMLElement.XML_NODE_XHTML_CONTENT_END_TAG_REGEXP));
        assertTrue("</ nodecontent>".matches(XMLElement.XML_NODE_XHTML_CONTENT_END_TAG_REGEXP));
        assertTrue("</ nodecontent >".matches(XMLElement.XML_NODE_XHTML_CONTENT_END_TAG_REGEXP));
        assertTrue("< /\nnodecontent >".matches(XMLElement.XML_NODE_XHTML_CONTENT_END_TAG_REGEXP));
    }
    
    public void testNanoXmlContent() throws Exception {
        XMLElement element = new XMLElement();
        element.parseFromReader(new StringReader("<nodecontent><body>a<b>cd</b>e</body></nodecontent>"));
        assertEquals("end nodecontent tag removed", "<body>a<b>cd</b>e</body>", element.getContent());
    }
}

