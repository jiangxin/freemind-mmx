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
/*$Id: HtmlConversionTests.java,v 1.1.2.11 2008-12-10 21:29:20 christianfoltin Exp $*/

package tests.freemind;

import java.io.StringReader;

import com.lightdev.app.shtm.SHTMLPanel;

import freemind.main.HtmlTools;
import freemind.main.XMLElement;
import freemind.modes.mindmapmode.MindMapNodeModel;

/**
 * @author foltin
 *
 */
public class HtmlConversionTests extends FreeMindTestBase {

    
    public void testSetHtml() throws Exception {
        MindMapNodeModel node = new MindMapNodeModel(getFrame(), new MindMapMock("</map>"));
        node.setText("test");
        // wiped out: <?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"DTD/xhtml1-transitional.dtd\">\n
        assertEquals("no conversion as test is not html.",null , node.getXmlText()); /* former : "<html>\n  <head>\n    \n  </head>\n  <body>\n    test\n  </body>\n</html>\n" */
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
    
    public void testBr() throws Exception {
        String input = "<html>\n" + 
        "  <head>\n" + 
        "    \n" + 
        "  </head>\n" + 
        "  <body>\n" + 
        "    <p>\n" + 
        "      asdfasdf<br />asdfasdfdasf\n" + 
        "    </p>\n" + 
        "    <p>\n" + 
        "      asdasdfas\n" + 
        "    </p>\n" + 
        "  </body>\n" + 
        "</html>";
        String result = HtmlTools.getInstance().toHtml(input);
        assertFalse ( " no > occurs  in " + result, result.matches("^.*&gt;.*$"));
    }
    
    /**
     * I suspected, that the toXhtml method inserts some spaces
     * into the output, but it doesn't seem to be the case.
     * @throws Exception
     */
    public void testSpaceHandling() throws Exception {
		String input = getInputStringWithManySpaces();
		assertEquals(input, HtmlTools.getInstance().toXhtml(input));
	}
    public void testSpaceHandlingInShtml() throws Exception {
    	String input = getInputStringWithManySpaces();
    	SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
    	panel.setCurrentDocumentContent(input);
    	assertEquals(input, panel.getDocumentText());
    	panel.setVisible(false);
    }
    /**
     * Set the panel to a text, read this text from the panel
     * and set it again. Then, setting and getting this text to the panel
     * must give the same. 
     */
    public void testSpaceHandlingInShtmlIdempotency() throws Exception {
    	String input = getInputStringWithManySpaces();
    	SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
    	panel.setCurrentDocumentContent(input);
    	// set the value of the panel itself again.
    	input = panel.getDocumentText();
    	panel.setCurrentDocumentContent(input);
    	assertEquals("Setting the input to its output should cause the same output.", input, panel.getDocumentText());
    	panel.setVisible(false);
    }

	private String getInputStringWithManySpaces() {
		String input = "<html>\n"
+"  <head>\n"
+"    \n"
+"  </head>\n"
+"  <body>\n"
+"    <p>\n"
+"      Using&#160;Filters&#160;the&#160;current&#160;mindmap&#160;can&#160;be&#160;reduced&#160;to&#160;nodes&#160;satisfying&#160;certain&#160;criteria.&#160;For&#160;example,&#160;if&#160;you&#160;only&#160;want&#160;to&#160;see&#160;every&#160;node&#160;containing&#160;&quot;TODO&quot;,&#160;then&#160;you&#160;have&#160;to&#160;press&#160;on&#160;the&#160;filter&#160;symbol&#160;(the&#160;funnel&#160;beside&#160;the&#160;zoom&#160;box),&#160;the&#160;filter&#160;toolbar&#160;appears,&#160;choose&#160;&quot;edit&quot;&#160;and&#160;add&#160;the&#160;condition&#160;that&#160;the&#160;node&#160;content&#160;contains&#160;&quot;TODO&quot;.&#160;Then&#160;select&#160;the&#160;filter&#160;in&#160;the&#160;filter&#160;toolbar.&#160;Now,&#160;only&#160;the&#160;filtered&#160;nodes&#160;and&#160;its&#160;ancestors&#160;are&#160;displayed&#160;unless&#160;you&#160;choose&#160;&quot;No&#160;filtering&quot;&#160;in&#160;the&#160;toolbar.&#160;\n"
+"    </p>\n"
+"    <p>\n"
+"      Using&#160;the&#160;settings&#160;&quot;Show&#160;ancestors&quot;&#160;and&#160;&quot;Show&#160;descendants&quot;&#160;you&#160;can&#160;influence&#160;the&#160;apperance&#160;of&#160;the&#160;parent&#160;and&#160;child&#160;nodes&#160;that&#160;are&#160;connected&#160;with&#160;the&#160;nodes&#160;being&#160;filtered.\n"
+"    </p>\n"
+"    <p>\n"
+"      There&#160;are&#160;many&#160;different&#160;criteria&#160;filters&#160;can&#160;be&#160;based&#160;on&#160;such&#160;as&#160;a&#160;set&#160;of&#160;selected&#160;nodes,&#160;a&#160;specific&#160;icon&#160;and&#160;some&#160;attributes.\n"
+"    </p>\n"
+"    <p>\n"
+"      &#160;\n"
+"    </p>\n"
+"  </body>\n"
+"</html>\n";
		return input;
	}

    public void testUnicodeHandling() {
    	String input = "if (myOldValue != null && myText.startsWith(myOldValue) == true) { \nmyText = myText.substring(myOldValue.length() + terminator.length());\n};\n";
    	String escapedText = HtmlTools.toXMLEscapedText(input);
		String unicodeToHTMLUnicodeEntity = HtmlTools.unicodeToHTMLUnicodeEntity(escapedText);
		System.out.println(unicodeToHTMLUnicodeEntity);
    	String unescapeHTMLUnicodeEntity = HtmlTools.unescapeHTMLUnicodeEntity(unicodeToHTMLUnicodeEntity);
		String back = HtmlTools.toXMLUnescapedText(unescapeHTMLUnicodeEntity);
    	System.out.println(back);
    	assertEquals("unicode conversion", input, back);
    }
    
}

