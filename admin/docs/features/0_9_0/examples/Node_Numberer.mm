<map version="0.9.0_Beta_8">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<attribute_registry SHOW_ATTRIBUTES="hide"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1203420272703" ID="Freemind_Link_677606044" MODIFIED="1209011595972" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      <img src="Images/freemind-help.png" />
    </p>
  </body>
</html></richcontent>
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="40"/>
<attribute NAME="script1" VALUE="/*Number*/&#xa;/**  -----------------------DESCRIPTION-----------------------&#xa; * Recursive Script to number all the nodes in a map&#xa; * Indexing is displayed in form &quot;2.1.3.4.&quot; by default&#xa; * Indexes both as attribute &amp; text (option in SETUP) &#xa; * Long (html) nodes are numbered by attribute only&#xa; * Author Bernard Lang, 27/01/08 + Dmitry Romanov&#xa; * Status: Version 2 Beta - tested as detailed below:-*/&#xa;&#xa;/**  ---------------------------TESTING--------------------------&#xa; * Tested: Win XP SP2, Java 1.6, Freemind 0.9.0b15&#xa;* Performance numbering map with 50 nodes&#xa; * --Initial numbering when first run = .66secs&#xa; * --Renumber after 50 nodes moved = .14secs&#xa; * --Renumber after 10 nodes added = .06secs&#xa; * Performance numbering map with 1293 nodes&#xa; * --Initial numbering when first run = 6.7 secs&#xa; * --Renumber after 1293 nodes moved = 1.25secs&#xa; * --Renumber after 10 nodes added = 0.24secs*/&#xa;&#xa;/* ------------------------------IMPORT------------------------ */&#xa;import freemind.modes.*; &#xa;import freemind.modes.attributes.*; &#xa;import freemind.view.mindmapview.attributeview.*; &#xa;import freemind.view.mindmapview.*;&#xa;import javax.swing.JOptionPane; // neede for dialog&#xa;&#xa;/+ ---------------First select a mode for the numbering ------*/ &#xa;//Options: SIMPLE, NESTED or REMOVE - add your own tables&#xa;&#xa;/* ----------------------------SETUP--------------------------- */&#xa;/* Set to &quot;0&quot; for numbering to start at &quot;0&quot; */&#xa;def START = 1;&#xa;/* set NAME = whatever if &quot;toc&quot; used elsewhere */&#xa;def NAME = &quot;toc&quot;;&#xa;/* set PREPEND = false to omit text numbering */&#xa;def PREPEND = true;&#xa;/* set TERMINATOR to &quot;) &quot;/whatever to display &quot;2.1.3) &quot; */&#xa;def TERMINATOR = &quot;: &quot;;&#xa;/* set SEPARATOR = &quot;, &quot;/whatever to display &quot;2,1,3: &quot; */&#xa;def SEPARATOR = &quot;.&quot;;&#xa;/* set NESTED = &quot;false&quot; for simple numbes (&quot;2 &quot;, not &quot;1.3.2. &quot; */&#xa;def NESTED = true;&#xa;/* set REMOVE = &quot;true&quot; to remove previous numbering*/&#xa;def REMOVE = false;&#xa;/* set INFO = &quot;true&quot; to add a &quot;timing&quot; atribute */&#xa;def INFO = true;&#xa;/* --------------------------END SETUP------------------------- */&#xa;&#xa;//START NEW STUFF---------------------------&#xa;&#xa;def nodeText = node.getShortText();&#xa;nodeText = nodeText.substring(0, Math.min (20, nodeText.length()));&#xa;&#xa;//Work around, because can&apos;t create array as expected using:&#xa;//Object[] options = new Object{&quot;a&quot; , &quot;b&quot; , &quot;c&quot;};&#xa;&#xa;Object[] options = new Object[3];&#xa;options[0] = &quot;SIMPLE&quot;;&#xa;options[1] = &quot;NESTED&quot;;&#xa;options[2] = &quot;REMOVE&quot;;&#xa;&#xa;def int option = JOptionPane.showOptionDialog(null,&#xa;&#x9;&quot;Select a numbering style for the children of the\nnode who&apos;s text begins: &quot; + nodeText, // the question being asked&#xa;&#x9;&quot;Node Numberer...&quot;, // the title&#xa;&#x9;JOptionPane.YES_NO_CANCEL_OPTION, // set dialog options&#xa;&#x9;JOptionPane.QUESTION_MESSAGE, // set dialog type&#xa;&#x9;null, // no icon&#xa;&#x9;options, //list for dropdown&#xa;&#x9;options[0] // 1st item is initial selection&#xa;);&#xa;&#xa;//set start time so we can measure time processing&#xa;&#x9;startTime = System.currentTimeMillis();&#xa;&#xa;if (option != -1) {&#xa;&#xa;&#x9;numberStyleSelection = options[option];&#xa;&#xa;&#x9;switch (numberStyleSelection) {   &#xa;&#x9;case &apos;SIMPLE&apos;:       &#xa;&#x9;&#x9;NESTED = false;&#xa;&#x9;&#x9;break;&#xa;&#x9;case &apos;NESTED&apos;:       &#xa;&#x9;&#x9;NESTED = true;&#xa;&#x9;&#x9;break   &#xa;&#x9;case &apos;REMOVE&apos;:       &#xa;&#x9;&#x9;REMOVE = true;&#xa;&#x9;&#x9;break  &#xa;&#x9;default: &#xa;&#x9;&#x9;break;&#xa;&#x9;}&#xa;&#xa;&#x9;nodeInfo = numberNodes(node, START, NAME, PREPEND, TERMINATOR, NESTED, SEPARATOR, REMOVE,&quot;&quot;);&#xa;&#xa;&#x9;if (INFO == true) {&#xa;&#x9;&#x9;scriptTime = (((System.currentTimeMillis() - startTime)).toString() + &quot; ms&quot;);&#xa;&#x9;&#x9;c.editAttribute(node, &quot;timing:&quot;, (String) scriptTime);&#xa;&#x9;};&#xa;&#xa;}&#xa;&#xa;def numberNodes(MindMapNode targetNode, int childNum, String name, boolean text, String terminator, boolean nested, String separator, boolean remove, String parentNum) { &#xa;&#x9;nodeInfo = 0;&#xa;&#x9;def startAt = childNum;&#xa;&#x9;def myChildren =  targetNode.childrenUnfolded(); &#xa;&#x9;while (myChildren.hasNext()) { &#xa;&#x9;&#x9;def myChild = myChildren .next(); &#xa;&#x9;&#x9;def myIndex = childNum.toString();&#xa;&#x9;&#x9;def myOldValue = myChild.getAttribute(name);&#xa;&#x9;&#x9;if (nested == true) {&#xa;&#x9;&#x9;&#x9;myIndex = parentNum + myIndex;&#xa;&#x9;&#x9;};&#xa;&#x9;&#x9;if (text)  {&#xa;&#x9;&#x9;&#x9;myText = myChild.getText();&#xa;&#x9;&#x9;&#x9;/** hack because getIsLong() just won&apos;t work for me!)*/&#xa;&#x9;&#x9;&#x9;if (!myText.startsWith(&quot;&lt;html&gt;&quot;) == true ) {&#xa;&#x9;&#x9;&#x9;&#x9;if (myOldValue != null &amp;&amp; myText.startsWith(myOldValue) == true) {&#xa;&#x9;&#x9;&#x9;&#x9;&#x9;myText = myText.substring(myOldValue.length() + terminator.length());&#xa;&#x9;&#x9;&#x9;&#x9;};&#xa;&#x9;&#x9;&#x9;&#x9;if (remove == true) {&#xa;&#x9;&#x9;&#x9;&#x9;&#x9;c.setNodeText(myChild, myText); &#xa;&#x9;&#x9;&#x9;&#x9;} else {&#xa;&#x9;&#x9;&#x9;&#x9;&#x9;c.setNodeText(myChild, myIndex + terminator + myText); &#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#xa;&#x9;&#x9;&#x9;&#x9;}&#xa;&#x9;&#x9;&#x9;};&#xa;&#x9;&#x9;};&#xa;&#x9;&#x9;if (remove == true) {&#xa;&#x9;&#x9;&#x9;&#x9;removeAttributeFromNode(myChild, name);&#xa;&#x9;&#x9;} else {&#xa;&#x9;&#x9;&#x9;c.editAttribute(myChild, name, myIndex);&#xa;&#x9;&#x9;};&#xa;&#x9;&#x9;childNum = childNum + 1; &#xa;&#x9;&#x9;def NodeAttributeTableModel attributesModel = myChild.getAttributes();&#xa;    attributesModel.setColumnWidth(0, 25);&#xa;&#x9;&#x9;nodeInfo++;&#xa;&#x9;&#x9;nodeInfo = nodeInfo + numberNodes(myChild, startAt, name, text, terminator, nested, separator, remove, myIndex + separator)&#xa;&#x9;}; &#xa;&#x9;return nodeInfo;&#xa;};&#xa;&#xa;/*----Need simpler attribute methods to replace these by Dmitry Romanov ----*/&#xa;&#xa;def void removeAttributeFromNode(MindMapNode startNode, String attrName) {&#xa;&#x9;def attrIndex = findAttributeIndex(startNode, attrName); &#xa;&#x9;if (attrIndex&gt;-1) { startNode.getAttributes().removeRow(attrIndex) };&#xa;};&#xa;&#xa;&#xa;def int findAttributeIndex(MindMapNode targetNode, String attributeName) {&#xa;&#x9;def attrIndex = -1;&#xa;&#x9;targetNode.getAttributes().getAttributes().eachWithIndex() { anAttribute, index -&gt;&#xa;&#x9;&#x9;if (anAttribute.name == attributeName) {&#xa;&#x9;&#x9;&#x9;attrIndex = index;&#xa;&#x9;&#x9;&#x9;return;&#xa;&#x9;&#x9;};&#xa;&#x9;};&#xa;&#x9;return attrIndex;&#xa;};&#xa;//SIGN:MCwCFAIxa9xaYyX7piqP+X5AvmEHTdi6AhQWU3nspqWi7rMC2Ci+F3RvAMwXvw==&#xa;"/>
<attribute NAME="timing:" VALUE="125 ms"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1124560950701" HGAP="48" ID="_Freemind_Link_1091417446" MODIFIED="1206628704078" POSITION="left" STYLE="bubble" TEXT="keyboard">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font BOLD="true" NAME="Verdana" SIZE="12"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297531" ID="Freemind_Link_574365829" MODIFIED="1206628704078" STYLE="bubble" TEXT="Edit:">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="12"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297531" ID="Freemind_Link_1788936140" MODIFIED="1206628704078" STYLE="bubble" TEXT="Find        - Ctrl+F">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297531" ID="Freemind_Link_1560577921" MODIFIED="1206628704078" STYLE="bubble" TEXT="Find next   - Ctrl+G">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297531" ID="Freemind_Link_1565111083" MODIFIED="1206628704078" STYLE="bubble" TEXT="Cut         - Ctrl+X">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297531" ID="Freemind_Link_965285662" MODIFIED="1206628704093" STYLE="bubble" TEXT="Copy        - Ctrl+C">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297531" ID="Freemind_Link_716950331" MODIFIED="1206628704093" STYLE="bubble" TEXT="Copy single - Ctrl+Y">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297531" ID="Freemind_Link_417297145" MODIFIED="1206628704093" STYLE="bubble" TEXT="Paste       - Ctrl+V">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1124560950701" FOLDED="true" ID="Freemind_Link_1489369309" MODIFIED="1206628704093" STYLE="bubble" TEXT="File:">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="12"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297562" ID="Freemind_Link_730910226" MODIFIED="1206628704093" STYLE="bubble" TEXT="New map      - Ctrl+N">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297562" ID="Freemind_Link_799403481" MODIFIED="1206628704093" STYLE="bubble" TEXT="Open map     - Ctrl+O">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297562" ID="Freemind_Link_1227985890" MODIFIED="1206628704093" STYLE="bubble" TEXT="Save map     - Ctrl+S">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297562" ID="Freemind_Link_1273612339" MODIFIED="1206628704093" STYLE="bubble" TEXT="Save as      - Ctrl+A">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297562" ID="Freemind_Link_259058331" MODIFIED="1206628704093" STYLE="bubble" TEXT="Print        - Ctrl+P">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297562" ID="Freemind_Link_395928724" MODIFIED="1206628704093" STYLE="bubble" TEXT="Close        - Ctrl+W">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297546" ID="Freemind_Link_1602476152" MODIFIED="1206628704093" STYLE="bubble" TEXT="Quit         - Ctrl+Q">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297546" ID="Freemind_Link_865988609" MODIFIED="1206628704093" STYLE="bubble" TEXT="Previous map - Ctrl+LEFT">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297546" ID="Freemind_Link_330634482" MODIFIED="1206628704093" STYLE="bubble" TEXT="Next Map     - Ctrl+RIGHT">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297546" ID="Freemind_Link_413409937" MODIFIED="1206628704093" STYLE="bubble" TEXT="Export file to HTML          - Ctrl+E">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297546" ID="Freemind_Link_332477916" MODIFIED="1206628704093" STYLE="bubble" TEXT="Export branch to HTML        - Ctrl+H">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297546" ID="Freemind_Link_1218684544" MODIFIED="1206628704093" STYLE="bubble" TEXT="Export branch to new MM file - Alt+A">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297531" ID="Freemind_Link_840301914" MODIFIED="1206628704093" STYLE="bubble" TEXT="Open first file in history   - Ctrl+Shift+W">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297515" FOLDED="true" ID="Freemind_Link_762805387" MODIFIED="1206628704093" STYLE="bubble" TEXT="Mode:">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="12"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297515" ID="Freemind_Link_1167591875" MODIFIED="1206628704093" STYLE="bubble" TEXT="MindMap mode - Alt+1">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297515" ID="Freemind_Link_1185613899" MODIFIED="1206628704093" STYLE="bubble" TEXT="Browse mode  - Alt+2 ">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297515" ID="Freemind_Link_1244030242" MODIFIED="1206628704093" STYLE="bubble" TEXT="File mode    - Alt+3">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297468" FOLDED="true" ID="Freemind_Link_1244713593" MODIFIED="1206628704093" STYLE="bubble" TEXT="New node:">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="12"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297453" ID="Freemind_Link_1466797149" MODIFIED="1206628704093" STYLE="bubble" TEXT="Add sibling node   - ENTER">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297453" ID="Freemind_Link_1129411391" MODIFIED="1206628704093" STYLE="bubble" TEXT="Add child node     - INSERT">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297453" ID="Freemind_Link_1682628214" MODIFIED="1206628704093" STYLE="bubble" TEXT="Add sibling before - Shift+ENTER">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297453" FOLDED="true" ID="Freemind_Link_882658793" MODIFIED="1206628704093" STYLE="bubble" TEXT="Node editing:">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="12"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297453" ID="Freemind_Link_514107810" MODIFIED="1206628704093" STYLE="bubble" TEXT="Edit selected node        - F2">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297453" ID="Freemind_Link_769290998" MODIFIED="1206628704109" STYLE="bubble" TEXT="Edit long node            - Alt+ENTER">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297453" ID="Freemind_Link_1904027683" MODIFIED="1206628704109" STYLE="bubble" TEXT="Join nodes                - Ctrl+J">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297453" ID="Freemind_Link_1609263816" MODIFIED="1206628704109" STYLE="bubble" TEXT="Toggle folded             - SPACE">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297437" ID="Freemind_Link_831034422" MODIFIED="1206628704109" STYLE="bubble" TEXT="Toggle children folded    - Ctrl+SPACE">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297437" ID="Freemind_Link_1583764839" MODIFIED="1206628704109" STYLE="bubble" TEXT="Set link by filechooser   - Ctrl+Shift+K">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297437" ID="Freemind_Link_1498049441" MODIFIED="1206628704109" STYLE="bubble" TEXT="Set link by text entry    - Ctrl+K">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297437" ID="Freemind_Link_158726492" MODIFIED="1206628704109" STYLE="bubble" TEXT="Set image by filechooser  - Alt+K">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297437" ID="Freemind_Link_365802930" MODIFIED="1206628704109" STYLE="bubble" TEXT="Move node up              - Ctrl+UP">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297437" ID="Freemind_Link_1013783664" MODIFIED="1206628704109" STYLE="bubble" TEXT="Move node down            - Ctrl+DOWN">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297515" FOLDED="true" ID="Freemind_Link_765254256" MODIFIED="1206628704109" STYLE="bubble" TEXT="Node formatting:">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="12"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297515" ID="Freemind_Link_549044400" MODIFIED="1206628704109" STYLE="bubble" TEXT="Italicize                 - Ctrl+I">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297515" ID="Freemind_Link_769928785" MODIFIED="1206628704109" STYLE="bubble" TEXT="Bold                      - Ctrl+B">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297500" ID="Freemind_Link_609320549" MODIFIED="1206628704109" STYLE="bubble" TEXT="Cloud                     - Ctrl+Shift+B">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297500" ID="Freemind_Link_803276386" MODIFIED="1206628704109" STYLE="bubble" TEXT="Change node color         - Alt+C">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297500" ID="Freemind_Link_1659337247" MODIFIED="1206628704109" STYLE="bubble" TEXT="Blend node color          - Alt+B">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297500" ID="Freemind_Link_1683757774" MODIFIED="1206628704109" STYLE="bubble" TEXT="Change node edge color    - Alt+E">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297500" ID="Freemind_Link_1936427938" MODIFIED="1206628704109" STYLE="bubble" TEXT="Increase node font size   - Ctrl+L">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297500" ID="Freemind_Link_534292943" MODIFIED="1206628704109" STYLE="bubble" TEXT="decrease node font size   - Ctrl+M">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297500" ID="Freemind_Link_1125207586" MODIFIED="1206628704109" STYLE="bubble" TEXT="Increase branch font size - Ctrl+Shift+L">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297500" ID="Freemind_Link_471162187" MODIFIED="1206628704109" STYLE="bubble" TEXT="Decrease branch font size - Ctrl+Shift+M">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297500" FOLDED="true" ID="Freemind_Link_342797156" MODIFIED="1206628704109" STYLE="bubble" TEXT="Node navigation:">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="12"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297484" ID="Freemind_Link_443779941" MODIFIED="1206628704109" STYLE="bubble" TEXT="Go to root  - ESCAPE">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297484" ID="Freemind_Link_558175665" MODIFIED="1206628704109" STYLE="bubble" TEXT="Move up     - UP">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297484" ID="Freemind_Link_593780641" MODIFIED="1206628704109" STYLE="bubble" TEXT="Move down   - DOWN">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297484" ID="Freemind_Link_1721831534" MODIFIED="1206628704109" STYLE="bubble" TEXT="Move left   - LEFT">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297468" ID="Freemind_Link_1087695377" MODIFIED="1206628704109" STYLE="bubble" TEXT="Move right  - RIGHT">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297468" ID="Freemind_Link_1752057634" MODIFIED="1206628704109" STYLE="bubble" TEXT="Follow link - Ctrl+ENTER">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297468" ID="Freemind_Link_1371406695" MODIFIED="1206628704109" STYLE="bubble" TEXT="Zoom out    - Alt+UP">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201666297468" ID="Freemind_Link_1007108560" MODIFIED="1206628704109" STYLE="bubble" TEXT="Zoom in     - Alt+DOWN">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
</node>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1202045007500" HGAP="31" ID="Freemind_Link_1632508790" MODIFIED="1206628704109" POSITION="right" STYLE="bubble" TEXT="key links">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font BOLD="true" NAME="Verdana" SIZE="12"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204126235812" FOLDED="true" HGAP="21" ID="ID_927490573" MODIFIED="1206628710171" STYLE="bubble" TEXT="Key URLs">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="12"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201915130453" ID="ID_895690298" LINK="http://freemind.sourceforge.net/wiki/index.php/FreeMind_0.9.0:_The_New_Features" MODIFIED="1206628704109" STYLE="bubble" TEXT="FreeMind - 0.9.0 The New Features">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201662997015" ID="ID_1974244378" LINK="http://freemind.sourceforge.net/wiki/index.php/Asked_Questions" MODIFIED="1206628704109" STYLE="bubble" TEXT="FreeMind Frequently Asked questions">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201662997328" ID="ID_1319247958" LINK="http://freemind.sourceforge.net/wiki/index.php/Requests_for_enhancements" MODIFIED="1206628704109" STYLE="bubble" TEXT="Freemind Requested enhancements">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1202355118406" ID="ID_706845707" LINK="http://sourceforge.net/tracker/?group_id=7118&amp;atid=1006953" MODIFIED="1206628704109" STYLE="bubble" TEXT="Freemind Issue Tracker">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1202355083140" ID="ID_706288072" LINK="http://sourceforge.net/tracker/?atid=107118&amp;group_id=7118&amp;func=browse" MODIFIED="1206628704109" STYLE="bubble" TEXT="Freemind Bug Tracker">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204369662562" FOLDED="true" ID="Freemind_Link_1628221829" LINK="http://sourceforge.net/project/showfiles.php?group_id=7118" MODIFIED="1206628704109" TEXT="Download release vs of Freemind">
<font NAME="SansSerif" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#111111" CREATED="1204369809093" ID="Freemind_Link_1456301686" LINK="http://downloads.sourceforge.net/freemind/FreeMind-Windows-Installer-0_9_0-Beta15.exe?modtime=1196467793&amp;big_mirror=0" MODIFIED="1206628704109" TEXT="Download 0.9.0 b15 - Windows">
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#111111" CREATED="1204369903625" ID="Freemind_Link_1440791999" LINK="http://sourceforge.net/project/shownotes.php?group_id=7118&amp;release_id=558285" MODIFIED="1206628704109" TEXT="Relase notes 0.9.0 b15">
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#111111" CREATED="1204370070296" ID="Freemind_Link_1635148248" LINK="http://downloads.sourceforge.net/freemind/FreeMind-Windows-Installer-0.8.1-max.exe?modtime=1204064720&amp;big_mirror=0" MODIFIED="1206628704109" TEXT="Download 0.8.1 - Windows">
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#111111" CREATED="1204369956046" ID="Freemind_Link_1316448413" LINK="http://sourceforge.net/project/shownotes.php?group_id=7118&amp;release_id=569976" MODIFIED="1206628704109" TEXT="Relase notes 0.8.1">
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#111111" CREATED="1204542790531" ID="Freemind_Link_479969895" LINK="http://sourceforge.net/project/showfiles.php?group_id=7118&amp;package_id=188772" MODIFIED="1206628704109" TEXT="Download 0.9.0 beta 9 --&gt; 15">
<attribute_layout NAME_WIDTH="25"/>
</node>
</node>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204380419250" FOLDED="true" HGAP="22" ID="Freemind_Link_1843512541" MODIFIED="1206628704109" TEXT="Mindmaps" VSHIFT="-2">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="12"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204380463937" ID="Freemind_Link_1912928213" LINK="freemind.mm" MODIFIED="1206628704109" TEXT="Freemind Help File">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204380463937" ID="Freemind_Link_1746967311" LINK="freeMindFlashBrowser.mm" MODIFIED="1206628704109" TEXT="freeMind Flash Browser">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204380463921" ID="Freemind_Link_1477498329" LINK="Features 0.9.0.mm" MODIFIED="1206628704109" TEXT="Freemind Features 0.9.0">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1203714218640" ID="Freemind_Link_1247261968" MODIFIED="1206628704109" STYLE="bubble" TEXT="Scripting">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="12"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204207736359" FOLDED="true" ID="Freemind_Link_1603364077" LINK="http://www.manning.com/koenig/" MODIFIED="1206628704109" TEXT="Groovy in Action - Book">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204207787281" ID="Freemind_Link_982819608" LINK="http://www.manning.com/koenig/sample-ch01.pdf" MODIFIED="1206628704109" TEXT="manning.com &gt; Koenig &gt; Sample-ch01">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204207794984" ID="Freemind_Link_196263765" LINK="http://www.manning.com/koenig/sample-ch02.pdf" MODIFIED="1206628704109" TEXT="manning.com &gt; Koenig &gt; Sample-ch02">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204207755703" ID="Freemind_Link_1499239663" LINK="http://www.manning.com/koenig/sample-APP-C.pdf" MODIFIED="1206628704109" TEXT="manning.com &gt; Koenig &gt; Sample-APP-C">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204207805312" ID="Freemind_Link_710987199" LINK="http://www.manning.com/koenig/Groovy-in-Action-source-code.zip" MODIFIED="1206628704109" TEXT="manning.com &gt; Koenig &gt; Groovy-in-Action-source-code">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204208347500" ID="Freemind_Link_1493902737" LINK="http://www.manning-sandbox.com/forum.jspa?forumID=247" MODIFIED="1206628704109" TEXT="manning-sandbox.com &gt; Forum ? ...">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
</node>
<node BACKGROUND_COLOR="#ffffff" CREATED="1205290160234" FOLDED="true" ID="ID_409823213" MODIFIED="1206628704109" TEXT="SourceForge">
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201662997234" ID="ID_1448154880" LINK="http://freemind.sourceforge.net/wiki/index.php/FreeMind_0.9.0:_The_New_Features#Scripting_via_Groovy" MODIFIED="1206628704109" STYLE="bubble" TEXT="FreeMind - 0.9.0 New Features - Groovy">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1203714190953" ID="ID_1282144127" LINK="http://freemind.sourceforge.net/wiki/index.php/Example_scripts" MODIFIED="1206628704109" STYLE="bubble" TEXT="freemind.sourceforge.net &gt; Wiki &gt; Example Scripts">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204023095609" ID="ID_276606830" LINK="http://freemind.cvs.sourceforge.net/freemind/admin/docs/features/0_9_0/Features%200.9.0.mm?revision=1.4&amp;view=markup&amp;pathrev=MAIN" MODIFIED="1206628704109" STYLE="bubble" TEXT="freemind.cvs.sourceforge Features 0.9.0 mindmap (cvs)">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1203763739546" ID="ID_111134827" LINK="http://freemind.sourceforge.net/wiki/index.php/Undoable_Actions" MODIFIED="1206628704109" STYLE="bubble" TEXT="freemind.sourceforge.net &gt; Wiki &gt; Undoable Actions">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1203764170484" ID="ID_1396881602" LINK="http://freemind.sourceforge.net/wiki/index.php/Plugins" MODIFIED="1206628704109" STYLE="bubble" TEXT="freemind.sourceforge.net &gt; Wiki &gt; Plugins">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1203764239531" ID="ID_371348468" LINK="http://freemind.sourceforge.net/javadoc/index.html" MODIFIED="1206628704109" STYLE="bubble" TEXT="freemind.sourceforge.net &gt; Javadoc &gt; Index">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1203783879515" ID="ID_744921253" LINK="http://freemind.sourceforge.net/javadoc/" MODIFIED="1206628704109" STYLE="bubble" TEXT="freemind.sourceforge.net &gt; Javadoc">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204630527656" ID="Freemind_Link_198147192" LINK="http://freemind.sourceforge.net/wiki/index.php/Development" MODIFIED="1206628704109" TEXT="freemind.sourceforge.net &gt; Wiki &gt; Index.php &gt; Development">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204630577546" ID="Freemind_Link_107033601" LINK="http://freemind.sourceforge.net/wiki/index.php/Category:Development" MODIFIED="1206628704109" TEXT="freemind.sourceforge.net &gt; Wiki &gt; Index.php &gt; Category:Development">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204631697484" ID="Freemind_Link_397746595" LINK="http://freemind.sourceforge.net/wiki/index.php/Talk:Program_design#Implementation_of_the_Model_View_Controller_pattern_can_be_revisited" MODIFIED="1206628704109" TEXT="freemind.sourceforge.net &gt; Wiki &gt; Talk:Program design Model View Controller">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204631702609" ID="Freemind_Link_1415646390" LINK="http://freemind.sourceforge.net/wiki/index.php/Program_design#NodeView" MODIFIED="1206628704109" TEXT="freemind.sourceforge.net &gt; Wiki &gt; Index.php &gt; Program design#NodeView">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204631707796" ID="Freemind_Link_1285903535" LINK="http://freemind.sourceforge.net/wiki/index.php/Program_design" MODIFIED="1206628704109" TEXT="freemind.sourceforge.net &gt; Wiki &gt; Index.php &gt; Program design">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204631936234" ID="Freemind_Link_23858898" LINK="http://freemind.sourceforge.net/wiki/index.php/Rich_text_%28development%29" MODIFIED="1206628704109" TEXT="freemind.sourceforge.net &gt; Wiki &gt; Index.php &gt; Rich text development">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204881339421" ID="Freemind_Link_811244039" LINK="http://freemind.sourceforge.net/wiki/index.php/Bitmap_images_%28development%29#Solution_2:_Storage_in_.28zip.29_archive_file" MODIFIED="1206628704109" TEXT="freemind.sourceforge.net &gt; Wiki &gt; Index.php &gt; Bitmap images (development)">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
</node>
<node BACKGROUND_COLOR="#ffffff" CREATED="1205290335687" FOLDED="true" ID="ID_1617464059" MODIFIED="1206628704109" TEXT="Groovy official">
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1203955098734" ID="ID_1434064069" LINK="http://groovy.codehaus.org/" MODIFIED="1206628704109" STYLE="bubble" TEXT="groovy.codehaus.org">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204238214968" FOLDED="true" ID="Freemind_Link_159032325" LINK="http://groovy.codehaus.org/User+Guide" MODIFIED="1206628704109" TEXT="groovy.codehaus.org &gt; User+Guide">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204238571812" ID="Freemind_Link_413862641" LINK="http://groovy.codehaus.org/JN0545-Dates" MODIFIED="1206628704109" TEXT="groovy.codehaus.org &gt; JN0545-Dates">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1203959385781" ID="ID_1029907639" LINK="http://groovy.codehaus.org/Strings" MODIFIED="1206628704109" STYLE="bubble" TEXT="groovy.codehaus.org &gt; Strings">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204122217609" ID="ID_355920361" LINK="http://docs.codehaus.org/display/GROOVY/Alphabetical+Widgets+List" MODIFIED="1206628704109" STYLE="bubble" TEXT="docs.codehaus.org &gt; Groovy &gt; Alphabetical Widgets List">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204188987765" ID="ID_1012274987" LINK="http://groovy.codehaus.org/Related+Projects" MODIFIED="1206628704109" STYLE="bubble" TEXT="groovy.codehaus.org &gt; Related+Projects">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
</node>
<node BACKGROUND_COLOR="#ffffff" CREATED="1205290293171" FOLDED="true" ID="ID_1815654115" MODIFIED="1206628704109" TEXT="Other Sources">
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204211721828" ID="Freemind_Link_1472228662" LINK="samplescripts_090_features_map.html" MODIFIED="1206628704109" TEXT="Code from Features 0.9.0 mindmap as html - needs beta 16">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201662997500" ID="ID_548895990" LINK="http://www.efectokiwano.net/mm/" MODIFIED="1206628704109" STYLE="bubble" TEXT="efectokiwano.net - Freemind devt map">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204210078843" ID="Freemind_Link_1966156467" LINK="http://dir.gmane.org/gmane.comp.java.freemind.devel" MODIFIED="1206628704125" TEXT="dir.gmane.org &gt; Gmane.comp.java.freemind">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204189081640" ID="ID_1804408240" LINK="http://www.dinkla.net/groovy/emf.html" MODIFIED="1206628704125" STYLE="bubble" TEXT="dinkla.net &gt; Groovy &gt; Eclipse Emf">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1203955416437" ID="ID_1036574750" LINK="http://www.ibm.com/developerworks/java/library/j-pg10255.html" MODIFIED="1206628704125" STYLE="bubble" TEXT="ibm.com Developer Java Groovy overloaded operators">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204237954484" ID="Freemind_Link_1464122405" LINK="http://java.sun.com/developer/technicalArticles/JavaLP/groovy/" MODIFIED="1206628704125" TEXT="java.sun.com &gt; Developer &gt; TechnicalArticles &gt; JavaLP &gt; Groovy">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204035058656" ID="ID_1212372870" LINK="http://jsourcery.com/api/sourceforge/freemind/0.8.0/freemind/view/mindmapview/NodeView.source.html#j1719044378" MODIFIED="1206628704125" STYLE="bubble" TEXT="jsourcery.com Freemind Mindmapview NodeView.source">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204107357937" ID="ID_541683032" LINK="http://www.java-samples.com/showtutorial.php?tutorialid=226" MODIFIED="1206628704125" STYLE="bubble" TEXT="java-samples.com &gt; Showtutorial ? ...">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201915923968" ID="ID_1258209204" LINK="http://www.onjava.com/pub/a/onjava/2004/09/29/groovy.html" MODIFIED="1206628704125" STYLE="bubble" TEXT="onjava.com - Article on Groovy">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1203952302421" ID="ID_349139448" LINK="http://www.koders.com/java/fidDF81330CA21629EA4273730AB34729BF6AD7772C.aspx" MODIFIED="1206628704125" STYLE="bubble" TEXT="koders.com &gt; Java">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201662997500" ID="Freemind_Link_738679566" LINK="http://www.efectokiwano.net/mm/" MODIFIED="1206628704125" STYLE="bubble" TEXT="efectokiwano.net - Freemind devt map">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204289159281" ID="Freemind_Link_1718553461" LINK="http://www.oopweb.com/Java/Documents/IntroToProgrammingUsingJava/Volume/index.html" MODIFIED="1206628704125" TEXT="oopweb.com &gt; Intro To Programming Using Java">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204642408375" ID="Freemind_Link_552416323" LINK="http://sourceforge.krugle.com/kse/projects/4EcoEnJ#2" MODIFIED="1206628704125" TEXT="sourceforge.krugle.com &gt; Freemind find code in source">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204715007140" ID="Freemind_Link_641211937" LINK="http://zenit.senecac.on.ca/wiki/index.php/Adapter" MODIFIED="1206628704125" TEXT="zenit.senecac.on.ca &gt; Wiki &gt; Arrow Link Adapter">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204812247703" ID="ID_307334693" LINK="http://www.apl.jhu.edu/~hall/java/Swing-Tutorial/Swing-Tutorial-JOptionPane.html" MODIFIED="1206628704125" TEXT="apl.jhu.edu &gt; Hall &gt; Java &gt; Swing-Tutorial &gt; Swing-Tutorial-JOptionPane">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204922694156" ID="Freemind_Link_247143480" LINK="http://www.infoq.com/articles/groovy-1.5-new" MODIFIED="1206628704125" TEXT="infoq.com &gt; Articles &gt; Groovy-1">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1205027576015" ID="ID_856393937" LINK="http://java.sun.com/docs/books/tutorial/uiswing/events/keylistener.html" MODIFIED="1206628704125" TEXT="java.sun.com &gt; Docs &gt; Books &gt; Tutorial &gt; Uiswing &gt; Events &gt; Keylistener">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1205037142671" ID="ID_98367296" LINK="http://math.hws.edu/eck/cs124/javanotes2/c7/s2.html" MODIFIED="1206628704125" TEXT="math.hws.edu &gt; Eck &gt; Cs124 &gt; Javanotes2 &gt; C7 &gt; S2">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1205039603500" ID="ID_26819600" LINK="http://www.ociweb.com/jnb/jnbFeb2004.html" MODIFIED="1206628704125" TEXT="ociweb.com &gt; Jnb &gt; JnbFeb2004">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1205156425015" ID="ID_1313666267" LINK="http://froth-and-java.blogspot.com/2008/01/scala-vs-groovy-lists-part-1.html" MODIFIED="1206628704125" TEXT="froth-and-java.blogspot.com &gt; 2008 &gt; 01 &gt; Scala vs Groovy: lists">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1205319127906" ID="ID_1005715857" LINK="http://www.cs.bris.ac.uk/Teaching/Resources/COMSM0103/handouts/extra/dialogs.pdf" MODIFIED="1206628704125" TEXT="cs.bris.ac.uk &gt; Teaching Resources: Java Dialogs">
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" CREATED="1205335853578" ID="ID_1131831531" LINK="http://www.ociweb.com/jnb/jnbFeb2004.html" MODIFIED="1206628704125" TEXT="ociweb.com &gt; Jnb : installing Groovy with Java">
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node CREATED="1205495379250" ID="ID_1269361259" LINK="http://freemind.sourceforge.net/wiki/index.php/Freemind_Win_Collab" MODIFIED="1206628704125" TEXT="freemind.sourceforge.net &gt; Wiki &gt; Index.php &gt; Freemind Win Collab">
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node CREATED="1205837056203" ID="ID_639246232" LINK="http://fisheye.codehaus.org/rdiff/groovy-contrib?csid=264&amp;u&amp;N" MODIFIED="1206628704125" TEXT="fisheye.codehaus.org &gt; Rdiff &gt; Groovy-contrib ? ...">
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node CREATED="1205841816734" ID="ID_359443814" LINK="http://java.sun.com/j2se/1.4.2/docs/api/javax/swing/filechooser/FileSystemView.html" MODIFIED="1206628704125" TEXT="java.sun.com &gt; J2se &gt; 1.4.2 &gt; Docs &gt; Api &gt; Javax &gt; Swing &gt; Filechooser &gt; FileSystemView">
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node CREATED="1205842099187" ID="ID_1086451418" LINK="http://java.sun.com/j2se/1.4.2/docs/api/java/io/File.html" MODIFIED="1206628704125" TEXT="java.sun.com &gt; J2se &gt; 1.4.2 &gt; Docs &gt; Api &gt; Java &gt; Io &gt; File">
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node CREATED="1206442147953" ID="ID_263842268" LINK="http://www.java2s.com/" MODIFIED="1206628704125" TEXT="java2s.com &gt; Example Java source code">
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node CREATED="1206526595609" ID="ID_1274691716" LINK="http://snipplr.com/all/language/groovy/page/2" MODIFIED="1206628704125" TEXT="snipplr.com &gt; All &gt; Language &gt; Groovy &gt; Page &gt; 2">
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node CREATED="1206534789546" ID="ID_1066194439" LINK="http://www.cs.ucl.ac.uk/staff/G.Roberts/1007/installinggroovy.html" MODIFIED="1206628704125" TEXT="cs.ucl.ac.uk &gt; Staff &gt; G.Roberts &gt; 1007 &gt; Installinggroovy">
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node CREATED="1206534983484" ID="ID_812732589" LINK="http://www.ociweb.com/jnb/jnbFeb2004.html" MODIFIED="1206628704125" TEXT="ociweb.com &gt; Groovy - Scripting for Java">
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node CREATED="1206535182281" ID="ID_462703690" LINK="http://groovy.codehaus.org/Download" MODIFIED="1206628704125" TEXT="groovy.codehaus.org &gt; Download">
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node CREATED="1206535424125" ID="ID_357987827" LINK="http://dist.codehaus.org/groovy/distributions/installers/windows/nsis/groovy-1.5.4-installer.exe" MODIFIED="1206628704125" TEXT="dist.codehaus.org &gt; Groovy &gt; Distributions &gt; Installers &gt; Windows &gt; Nsis &gt; Groovy-1.5.4-installer">
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node CREATED="1206536756437" ID="ID_924844545" LINK="https://sdlc3b.sun.com/ECom/EComActionServlet/DownloadPage:~:com.sun.sunit.sdlc.content.DownloadPageInfo" MODIFIED="1206628704125" TEXT="sdlc3b.sun.com &gt; Java SDK: j2sdk-1_4_2_17-windows-i586-p.exe">
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node CREATED="1206548369406" ID="ID_1862777809" LINK="https://sdlc1e.sun.com/ECom/EComActionServlet/DownloadPage:~:com.sun.sunit.sdlc.content.DownloadPageInfo" MODIFIED="1206628704125" TEXT="https://sdlc1e.sun.com &gt;&#xa0;&#xa0;jdk-6u5-windows-i586-p.exe">
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node CREATED="1206546146312" ID="ID_844500109" LINK="http://groovy.codehaus.org/Installing+Groovy" MODIFIED="1206628704125" TEXT="groovy.codehaus.org &gt; Installing+Groovy">
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node CREATED="1206546487812" ID="ID_1249610264" LINK="http://groovy.codehaus.org/Quick+Start" MODIFIED="1206628704125" TEXT="groovy.codehaus.org &gt; Quick+Start">
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node CREATED="1206546074187" ID="ID_53957615" LINK="http://www.oreillynet.com/onjava/blog/2004/09/gdgroovy_installing_groovy.html" MODIFIED="1206628704125" TEXT="oreillynet.com &gt; Onjava: Installing Groovy with a working ~/.groovy/lib">
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node CREATED="1206538534515" ID="ID_221096159" LINK="http://www.ontopia.net/download/oks-samplers-install.html#d0e209" MODIFIED="1206628704125" TEXT="ontopia.net &gt; Set JAVA_HOME under Windows">
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node CREATED="1206546769171" ID="ID_1104275983" LINK="http://groovy.codehaus.org/Documentation" MODIFIED="1206628704125" TEXT="groovy.codehaus.org &gt; Documentation">
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node CREATED="1206547998859" ID="ID_1634294372" LINK="http://groovy.codehaus.org/Tutorial+1+-+Getting+started" MODIFIED="1206628704125" TEXT="groovy.codehaus.org &gt; Tutorial+1+-+Getting+started">
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node CREATED="1206549591203" ID="ID_211178688" LINK="http://java.sun.com/javase/namechange.html" MODIFIED="1206628704125" TEXT="java.sun.com &gt; Java SE Naming and Versions">
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
</node>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204124690671" FOLDED="true" ID="Freemind_Link_871143705" MODIFIED="1206628704125" STYLE="bubble" TEXT="Discussion">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="12"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1202354745687" FOLDED="true" ID="Freemind_Link_447208971" LINK="http://www.mail-archive.com/freemind-developer@lists.sourceforge.net/index.html#00639" MODIFIED="1206628704125" STYLE="bubble" TEXT="mail-archive.com &gt; Freemind-developer lists">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1202908755468" ID="Freemind_Link_270936567" LINK="https://lists.sourceforge.net/lists/listinfo/freemind-developer" MODIFIED="1206628704125" STYLE="bubble" TEXT="https://lists.sourceforge.net/lists/listinfo/freemind-developer">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1203673370828" ID="Freemind_Link_575510775" LINK="http://sourceforge.net/mailarchive/forum.php?forum_name=freemind-developer" MODIFIED="1206628704125" STYLE="bubble" TEXT="sourceforge.net &gt; Mailarchive &gt; Forum ? ...">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201662997406" ID="Freemind_Link_1943290925" LINK="http://sourceforge.net/forum/forum.php?thread_id=1423817&amp;forum_id=22101" MODIFIED="1206628704125" STYLE="bubble" TEXT="sourceforge.net - Open Discussion">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204123209531" FOLDED="true" ID="Freemind_Link_1427866725" MODIFIED="1206628704125" STYLE="bubble" TEXT="Resources">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="12"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1203076053750" ID="Freemind_Link_818475948" LINK="http://freemind.sourceforge.net/wiki/index.php/Essays" MODIFIED="1206628704125" STYLE="bubble" TEXT="freemind.sourceforge.net - Essay Robert Pirsig/mindmap">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201662997281" ID="Freemind_Link_431487601" LINK="http://freemind.sourceforge.net/wiki/images/d/d1/Freemind-concept-do.png" MODIFIED="1206628704125" STYLE="bubble" TEXT="sourceforge.net - Freemind logos">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1202354597515" ID="Freemind_Link_1048876818" LINK="http://freemind.sourceforge.net/wiki/index.php/Encryption" MODIFIED="1206628704125" STYLE="bubble" TEXT="freemind.sourceforge.net - Encryption">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201662997359" ID="Freemind_Link_1765330134" LINK="http://freemind.sourceforge.net/wiki/index.php/Mind_Map_Gallery" MODIFIED="1206628704125" STYLE="bubble" TEXT="FreeMind - Mind Map Gallery maps to download">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201662997218" ID="Freemind_Link_638969271" LINK="http://freemind.sourceforge.net/wiki/index.php/Flash_browser" MODIFIED="1206628704125" STYLE="bubble" TEXT="Wiki &gt; FreeMind Flash browser">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201785789140" ID="Freemind_Link_1991586041" LINK="http://freemind.sourceforge.net/wiki/index.php/Tutorial_effort" MODIFIED="1206628704125" STYLE="bubble" TEXT="sourceforge.net - Tutorial effort">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node CREATED="1205496237187" ID="ID_1151474318" LINK="http://www.geocities.com/shaila_kishore/Freemind  " MODIFIED="1206628704125" TEXT="Freemind 080 user guide as pdf">
<attribute_layout NAME_WIDTH="25"/>
</node>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1202043807796" FOLDED="true" ID="Freemind_Link_866163083" MODIFIED="1206628704125" STYLE="bubble" TEXT="Import/Export">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="12"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201980502343" FOLDED="true" ID="Freemind_Link_314531272" MODIFIED="1206628704125" STYLE="bubble" TEXT="Exporting">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201980519593" ID="Freemind_Link_220451075" MODIFIED="1206628704125" STYLE="bubble" TEXT="Outline&gt;Word - Copy &amp; Paste">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201646613640" ID="Freemind_Link_1717240631" MODIFIED="1206628704125" STYLE="bubble" TEXT="Attributes may not Export to Word">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201646601671" ID="Freemind_Link_1995726937" MODIFIED="1206628704125" STYLE="bubble" TEXT="Notes don&apos;t export to Word">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201667520609" FOLDED="true" ID="Freemind_Link_619949247" MODIFIED="1206628704125" STYLE="bubble" TEXT="Flashbrowser">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201667540484" ID="Freemind_Link_264534378" LINK="http://freemind.sourceforge.net/wiki/extensions/freemind/flashBrowserDocu.html" MODIFIED="1206628704125" STYLE="bubble" TEXT="Sourceforge Freemind FlashBrowser">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201711625265" ID="Freemind_Link_750001113" MODIFIED="1206628704125" STYLE="bubble" TEXT="&gt; 1000 (1079) nodes won&apos;t draw">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
</node>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1202043782578" FOLDED="true" ID="Freemind_Link_1210352313" MODIFIED="1206628704125" STYLE="bubble" TEXT="Importing">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201875765203" FOLDED="true" ID="Freemind_Link_451135974" MODIFIED="1206628704125" STYLE="bubble" TEXT="Convert .3map -&gt; .mm">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201875814875" FOLDED="true" ID="Freemind_Link_1223274264" MODIFIED="1206628704125" STYLE="bubble" TEXT="Search / Replace tags / attribs">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201876103343" FOLDED="true" ID="Freemind_Link_1312694280" MODIFIED="1206628704125" STYLE="bubble" TEXT="TIP Click bg between Ctrl-F">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201876142406" ID="Freemind_Link_293858803" MODIFIED="1206628704125" STYLE="bubble" TEXT="Prevents overwriting word in Find field">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
</node>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201875795765" FOLDED="true" ID="Freemind_Link_1597268290" MODIFIED="1206628704125" STYLE="bubble" TEXT="Open... in Dreamweaver / similar">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201876259921" ID="Freemind_Link_272283927" MODIFIED="1206628704140" STYLE="bubble" TEXT="TIP Right Click file &amp; Select Open With...">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201875834671" ID="Freemind_Link_1689287919" MODIFIED="1206628704140" STYLE="bubble" TEXT="&lt;mindmap = &lt;map">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201875852687" ID="Freemind_Link_656216751" MODIFIED="1206628704140" STYLE="bubble" TEXT="&lt;/mindmap = &lt;/map">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201875883218" ID="Freemind_Link_1040837801" MODIFIED="1206628704140" STYLE="bubble" TEXT="&lt;branch = &lt;node">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201875896500" ID="Freemind_Link_435799833" MODIFIED="1206628704140" STYLE="bubble" TEXT="&lt;/branch = &lt;/node">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201875909984" ID="Freemind_Link_32310972" MODIFIED="1206628704140" STYLE="bubble" TEXT="TITLE= = TEXT=">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201875937125" ID="Freemind_Link_986748812" MODIFIED="1206628704140" STYLE="bubble" TEXT="Save file as .mm">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201875951218" ID="Freemind_Link_1832376375" MODIFIED="1206628704140" STYLE="bubble" TEXT="Open in Freemind">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201875960031" ID="Freemind_Link_1777054916" MODIFIED="1206628704140" STYLE="bubble" TEXT="Set node properties as req">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201875968515" ID="Freemind_Link_1697756591" MODIFIED="1206628704140" STYLE="bubble" TEXT="Save As... under same name">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201662997343" ID="Freemind_Link_1589464240" LINK="http://www.liberatedcomputing.net/mm2fm" MODIFIED="1206628704140" STYLE="bubble" TEXT="Liberated Comput - mmap&gt;mm online">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201662997343" ID="Freemind_Link_1486866223" LINK="http://www.liberatedcomputing.net/mm2fm" MODIFIED="1206628704140" STYLE="bubble" TEXT="Liberated Computing - mmap&gt;mm">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201662997296" ID="Freemind_Link_331266194" LINK="http://freemind.sourceforge.net/wiki/index.php/Import_and_export_to_other_applications#Export_and_import_in_general" MODIFIED="1206628704140" STYLE="bubble" TEXT="FreeMind - Import and export...">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201662996968" ID="Freemind_Link_1359798241" LINK="http://freemind.sourceforge.net/wiki/index.php/Accessories" MODIFIED="1206628704140" STYLE="bubble" TEXT="FreeMind Accessories - Conbvertors">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201662997375" ID="Freemind_Link_645257919" LINK="http://eric-blue.com/projects/mindmapviewer/" MODIFIED="1206628704140" STYLE="bubble" TEXT="MindMap Viewer - online mmap&gt;FM">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1202819262500" ID="Freemind_Link_493387535" LINK="http://www.mind-mapping.org/interoperability-of-mind-mapping-software/" MODIFIED="1206628704140" STYLE="bubble" TEXT="mind-mapping.org &gt; Interoperability of mindmap software">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204123435562" FOLDED="true" ID="Freemind_Link_1780006342" MODIFIED="1206628704140" STYLE="bubble" TEXT="sql/php/flash/">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="12"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201662997250" ID="Freemind_Link_1412157280" LINK="http://64.233.179.104/translate_c?hl=en&amp;langpair=de%7Cen&amp;u=http://www.goermezer.de/content/view/318/502/" MODIFIED="1206628704140" STYLE="bubble" TEXT="G&#xf6;rmezer - Mind Maps on the Web">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201662997421" ID="Freemind_Link_891705596" LINK="http://translate.google.com/translate?u=http%3A%2F%2Fwww.goermezer.de%2Findex.php&amp;langpair=de%7Cen&amp;hl=en&amp;ie=UTF8" MODIFIED="1206628704140" STYLE="bubble" TEXT="G&#xf6;rmezer -&#xa0;Translated from German">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201662997250" ID="Freemind_Link_1325044407" LINK="http://64.233.179.104/translate_c?hl=en&amp;langpair=de%7Cen&amp;u=http://www.goermezer.de/content/view/319/502/" MODIFIED="1206628704140" STYLE="bubble" TEXT="G&#xf6;rmezer - Dynamic Mindmap / PHP">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201662997406" ID="Freemind_Link_1253368123" LINK="http://freemind.sourceforge.net/wiki/index.php/Stuff" MODIFIED="1206628704140" STYLE="bubble" TEXT="FreeMind - &quot;Stuff on freemind with pda wiki&apos;s etc">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201662997093" ID="Freemind_Link_33837059" LINK="http://www.blainekendall.com/deliciousmind/" MODIFIED="1206628704140" STYLE="bubble" TEXT="blainekendall.com - del.icio.us / FM">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1203713015765" ID="Freemind_Link_741395867" LINK="http://www.oreilly.com/openbook/" MODIFIED="1206628704140" STYLE="bubble" TEXT="oreilly.com &gt; Openbook - free books incl SQL Manual">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204123810421" FOLDED="true" ID="Freemind_Link_1838761550" MODIFIED="1206628704140" STYLE="bubble" TEXT="javascript">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="12"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1202647176359" ID="Freemind_Link_845776676" LINK="http://yeonisalive.net/javascript/MindWeb002.php" MODIFIED="1206628704140" STYLE="bubble" TEXT="yeonisalive.net &gt; Javascript &gt; MindWeb002">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204124926109" FOLDED="true" ID="Freemind_Link_1194213414" MODIFIED="1206628704140" STYLE="bubble" TEXT="Competitors">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="12"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1202819299906" ID="Freemind_Link_429483975" LINK="http://www.mind-mapping.org/" MODIFIED="1206628704140" STYLE="bubble" TEXT="mind-mapping.org - mindmapping &amp;&#xa0;&#xa0;info organisation&#xa0;">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1202819742125" ID="Freemind_Link_419717141" LINK="http://www.webofweb.net/showcases.html#Sharing" MODIFIED="1206628704156" STYLE="bubble" TEXT="webofweb.net &gt; Showcases: collaborative mindmaps, free">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1202820021375" ID="Freemind_Link_1529776402" LINK="http://www.mind-mapping.org/blog/" MODIFIED="1206628704156" STYLE="bubble" TEXT="mind-mapping.org &gt; Blog">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1202820128640" ID="Freemind_Link_466879291" LINK="http://www.exploratree.org.uk/" MODIFIED="1206628704156" STYLE="bubble" TEXT="exploratree.org.uk">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201640348156" ID="Freemind_Link_1301521411" MODIFIED="1206628704156" STYLE="bubble" TEXT="Curretnly only MindMap42,com?">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204188153093" ID="Freemind_Link_1021461300" LINK="http://mind42.com/" MODIFIED="1206628704156" STYLE="bubble" TEXT="mind42.com">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1204123334968" FOLDED="true" ID="Freemind_Link_897919483" MODIFIED="1206628704156" STYLE="bubble" TEXT="Reviews">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="12"/>
<attribute_layout NAME_WIDTH="25"/>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201662997406" ID="Freemind_Link_1207592879" LINK="http://sourceforge.net/potm/potm-2006-02.php" MODIFIED="1206628704156" STYLE="bubble" TEXT="sourceforge.net Project of the Month">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1201785616031" ID="Freemind_Link_1271158913" LINK="http://www.linux-mag.com/id/2435" MODIFIED="1206628704156" STYLE="bubble" TEXT="linux-mag.com - Freemind Review">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
<node BACKGROUND_COLOR="#ffffff" COLOR="#333333" CREATED="1202354404937" ID="Freemind_Link_1016267544" LINK="http://www.download.com/8301-2007_4-9757132-12.html" MODIFIED="1206628704156" STYLE="bubble" TEXT="download.com - Review of Freemind">
<edge COLOR="#ff9900" STYLE="bezier" WIDTH="thin"/>
<font NAME="Verdana" SIZE="10"/>
<attribute_layout NAME_WIDTH="25"/>
</node>
</node>
</node>
</node>
</map>
