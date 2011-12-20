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
/*$Id: HtmlConversionTests.java,v 1.1.2.16 2010/12/04 21:07:23 christianfoltin Exp $*/

package tests.freemind;

import java.io.StringReader;

import com.lightdev.app.shtm.SHTMLPanel;

import freemind.main.HtmlTools;
import freemind.main.Tools;
import freemind.main.XMLElement;
import freemind.modes.mindmapmode.MindMapNodeModel;

/**
 * @author foltin
 * 
 */
public class HtmlConversionTests extends FreeMindTestBase {

	public void testSetHtml() throws Exception {
		MindMapNodeModel node = new MindMapNodeModel(getFrame(),
				new MindMapMock("</map>"));
		node.setText("test");
		// wiped out: <?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html
		// PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"
		// \"DTD/xhtml1-transitional.dtd\">\n
		assertEquals("no conversion as test is not html.", null,
				node.getXmlText()); /*
									 * former :
									 * "<html>\n  <head>\n    \n  </head>\n  <body>\n    test\n  </body>\n</html>\n"
									 */
		node.setXmlText("test");

		assertEquals("proper conversion", "test", node.getText());
		node.setText("<html><br>");
		assertEquals(
				"proper html conversion",
				"<html>\n  <head>\n    \n  </head>\n  <body>\n    <br />\n  </body>\n</html>\n",
				node.getXmlText());
		// must remove the '/' in <br/>.
		node.setXmlText("<html><br/></html>");
		assertEquals("proper html conversion", "<html><br></html>",
				node.getText());
		node.setXmlText("<html><br /></html>");
		assertEquals("proper html conversion", "<html><br ></html>",
				node.getText());

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
		assertTrue(string
				.matches(XMLElement.XML_NODE_XHTML_CONTENT_END_TAG_REGEXP));
	}

	public void testNanoXmlContent() throws Exception {
		XMLElement element = new XMLElement();
		element.parseFromReader(new StringReader("<"
				+ XMLElement.XML_NODE_XHTML_CONTENT_TAG
				+ "><body>a<b>cd</b>e</body></"
				+ XMLElement.XML_NODE_XHTML_CONTENT_TAG + ">"));
		assertEquals("end " + XMLElement.XML_NODE_XHTML_CONTENT_TAG
				+ " tag removed", "<body>a<b>cd</b>e</body>",
				element.getContent());
	}

	public void testXHtmlToHtmlConversion() throws Exception {
		assertEquals("br removal", "<br >",
				HtmlTools.getInstance().toHtml("<br />"));
		assertEquals("br removal, not more.", "<brmore/>", HtmlTools
				.getInstance().toHtml("<brmore/>"));
	}

	public void testWellFormedXml() throws Exception {
		assertEquals(true, HtmlTools.getInstance().isWellformedXml("<a></a>"));
		// assertEquals(true,
		// HtmlTools.getInstance().isWellformedXml("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"DTD/xhtml1-transitional.dtd\">\n<a></a>"));
		assertEquals(
				true,
				HtmlTools.getInstance().isWellformedXml(
						"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<a></a>"));
		assertEquals(false,
				HtmlTools.getInstance().isWellformedXml("<a><a></a>"));

	}

	public void testBr() throws Exception {
		String input = "<html>\n" + "  <head>\n" + "    \n" + "  </head>\n"
				+ "  <body>\n" + "    <p>\n"
				+ "      asdfasdf<br />asdfasdfdasf\n" + "    </p>\n"
				+ "    <p>\n" + "      asdasdfas\n" + "    </p>\n"
				+ "  </body>\n" + "</html>";
		String result = HtmlTools.getInstance().toHtml(input);
		assertFalse(" no > occurs  in " + result, result.matches("^.*&gt;.*$"));
	}

	/**
	 * I suspected, that the toXhtml method inserts some spaces into the output,
	 * but it doesn't seem to be the case.
	 * 
	 * @throws Exception
	 */
	public void testSpaceHandling() throws Exception {
		String input = getInputStringWithManySpaces(HtmlTools.SP);
		assertEquals(input, HtmlTools.getInstance().toXhtml(input));
	}

	// public void testSpaceHandlingInShtml() throws Exception {
	// String input = getInputStringWithManySpaces(" ");
	// SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
	// panel.setCurrentDocumentContent(input);
	// assertEquals(input, panel.getDocumentText());
	// panel.setVisible(false);
	// }
	/**
	 * Set the panel to a text, read this text from the panel and set it again.
	 * Then, setting and getting this text to the panel must give the same.
	 */
	public void testSpaceHandlingInShtmlIdempotency() throws Exception {
		String input = getInputStringWithManySpaces(" ");
		SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
		panel.setCurrentDocumentContent(input);
		// set the value of the panel itself again.
		input = panel.getDocumentText();
		panel.setCurrentDocumentContent(input);
		assertEquals(
				"Setting the input to its output should cause the same output.",
				input, panel.getDocumentText());
		panel.setVisible(false);
	}

	public void testSpaceRemovalInShtml() throws Exception {
		String input = getInputStringWithManySpaces(HtmlTools.SP);
		SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
		panel.setCurrentDocumentContent(input);
		// set the value of the panel itself again (twice)
		input = panel.getDocumentText();
		panel.setCurrentDocumentContent(input);
		input = panel.getDocumentText();
		panel.setCurrentDocumentContent(input);
		assertEquals(
				"Setting the input to its output should cause the same output.",
				input, panel.getDocumentText());
		panel.setVisible(false);
	}

	private String getInputStringWithManySpaces(String pSpaceString) {
		String body = getHtmlBody(pSpaceString);
		String input = "<html>\n" + "  <head>\n" + "    \n" + "  </head>\n"
				+ "  <body>" + body + "</body>\n" + "</html>\n";
		return input;
	}

	private String getHtmlBody(String pSpaceString) {
		String body = "\n    <p>\n" + "      Using" + pSpaceString + "Filters"
				+ pSpaceString + "the" + pSpaceString + "current"
				+ pSpaceString + "mindmap" + pSpaceString + "can"
				+ pSpaceString + "be" + pSpaceString + "reduced" + pSpaceString
				+ "to" + pSpaceString + "nodes" + pSpaceString + "satisfying"
				+ pSpaceString + "certain" + pSpaceString + "criteria."
				+ pSpaceString + "For" + pSpaceString + "example,"
				+ pSpaceString + "if" + pSpaceString + "you" + pSpaceString
				+ "only" + pSpaceString + "want" + pSpaceString + "to"
				+ pSpaceString + "see" + pSpaceString + "every" + pSpaceString
				+ "node" + pSpaceString + "containing" + pSpaceString
				+ "&quot;TODO&quot;," + pSpaceString + "then" + pSpaceString
				+ "you" + pSpaceString + "have" + pSpaceString + "to"
				+ pSpaceString + "press" + pSpaceString + "on" + pSpaceString
				+ "the" + pSpaceString + "filter" + pSpaceString + "symbol"
				+ pSpaceString + "(the" + pSpaceString + "funnel"
				+ pSpaceString + "beside" + pSpaceString + "the" + pSpaceString
				+ "zoom" + pSpaceString + "box)," + pSpaceString + "the"
				+ pSpaceString + "filter" + pSpaceString + "toolbar"
				+ pSpaceString + "appears," + pSpaceString + "choose"
				+ pSpaceString + "&quot;edit&quot;" + pSpaceString + "and"
				+ pSpaceString + "add" + pSpaceString + "the" + pSpaceString
				+ "condition" + pSpaceString + "that" + pSpaceString + "the"
				+ pSpaceString + "node" + pSpaceString + "content"
				+ pSpaceString + "contains" + pSpaceString
				+ "&quot;TODO&quot;." + pSpaceString + "Then" + pSpaceString
				+ "select" + pSpaceString + "the" + pSpaceString + "filter"
				+ pSpaceString + "in" + pSpaceString + "the" + pSpaceString
				+ "filter" + pSpaceString + "toolbar." + pSpaceString + "Now,"
				+ pSpaceString + "only" + pSpaceString + "the" + pSpaceString
				+ "filtered" + pSpaceString + "nodes" + pSpaceString + "and"
				+ pSpaceString + "its" + pSpaceString + "ancestors"
				+ pSpaceString + "are" + pSpaceString + "displayed"
				+ pSpaceString + "unless" + pSpaceString + "you" + pSpaceString
				+ "choose" + pSpaceString + "&quot;No" + pSpaceString
				+ "filtering&quot;" + pSpaceString + "in" + pSpaceString
				+ "the" + pSpaceString + "toolbar." + pSpaceString + "\n"
				+ "    </p>\n" + "    <p>\n" + "      Using" + pSpaceString
				+ "the" + pSpaceString + "settings" + pSpaceString
				+ "&quot;Show" + pSpaceString + "ancestors&quot;"
				+ pSpaceString + "and" + pSpaceString + "&quot;Show"
				+ pSpaceString + "descendants&quot;" + pSpaceString + "you"
				+ pSpaceString + "can" + pSpaceString + "influence"
				+ pSpaceString + "the" + pSpaceString + "apperance"
				+ pSpaceString + "of" + pSpaceString + "the" + pSpaceString
				+ "parent" + pSpaceString + "and" + pSpaceString + "child"
				+ pSpaceString + "nodes" + pSpaceString + "that" + pSpaceString
				+ "are" + pSpaceString + "connected" + pSpaceString + "with"
				+ pSpaceString + "the" + pSpaceString + "nodes" + pSpaceString
				+ "being" + pSpaceString + "filtered.\n" + "    </p>\n"
				+ "    <p>\n" + "      There" + pSpaceString + "are"
				+ pSpaceString + "many" + pSpaceString + "different"
				+ pSpaceString + "criteria" + pSpaceString + "filters"
				+ pSpaceString + "can" + pSpaceString + "be" + pSpaceString
				+ "based" + pSpaceString + "on" + pSpaceString + "such"
				+ pSpaceString + "as" + pSpaceString + "a" + pSpaceString
				+ "set" + pSpaceString + "of" + pSpaceString + "selected"
				+ pSpaceString + "nodes," + pSpaceString + "a" + pSpaceString
				+ "specific" + pSpaceString + "icon" + pSpaceString + "and"
				+ pSpaceString + "some" + pSpaceString + "attributes.\n"
				+ "    </p>\n" + "    <p>\n" + "      " + pSpaceString + "\n"
				+ "    </p>\n  ";
		return body;
	}

	public void testUnicodeHandling() {
		String input = "if (myOldValue != null && myText.startsWith(myOldValue) == true) { \nmyText = myText.substring(myOldValue.length() + terminator.length());\n};\n";
		String escapedText = HtmlTools.toXMLEscapedText(input);
		String unicodeToHTMLUnicodeEntity = HtmlTools
				.unicodeToHTMLUnicodeEntity(escapedText, false);
		System.out.println(unicodeToHTMLUnicodeEntity);
		String unescapeHTMLUnicodeEntity = HtmlTools
				.unescapeHTMLUnicodeEntity(unicodeToHTMLUnicodeEntity);
		String back = HtmlTools.toXMLUnescapedText(unescapeHTMLUnicodeEntity);
		System.out.println(back);
		assertEquals("unicode conversion", input, back);
	}

	public void testHtmlBodyExtraction() {
		String input = getInputStringWithManySpaces(" ");
		String expectedOutput = getHtmlBody(" ");
		assertTrue(HtmlTools.isHtmlNode(input));
		assertEquals(expectedOutput, HtmlTools.extractHtmlBody(input));
	}

	public void testIllegalXmlChars() throws Exception {
		assertEquals(
				"Wrong chars are gone",
				"AB&#32;&#x20;",
				Tools.replaceUtf8AndIllegalXmlChars("&#x1f;A&#0;&#31;&#x0001B;B&#x1;&#32;&#1;&#x20;"));
	}

	public void testSpaceReplacements() throws Exception {
		assertEquals("Space conversion", " " + HtmlTools.NBSP,
				HtmlTools.replaceSpacesToNonbreakableSpaces("  "));
		assertEquals("Multiple space conversion", " " + HtmlTools.NBSP
				+ HtmlTools.NBSP + HtmlTools.NBSP,
				HtmlTools.replaceSpacesToNonbreakableSpaces("    "));
		assertEquals("Double space conversion", " " + HtmlTools.NBSP + "xy "
				+ HtmlTools.NBSP + HtmlTools.NBSP,
				HtmlTools.replaceSpacesToNonbreakableSpaces("  xy   "));
	}

}
