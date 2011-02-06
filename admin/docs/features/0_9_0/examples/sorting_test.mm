<map version="0.9.0_Beta_8">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1202364738019" ID="Freemind_Link_1150612791" MODIFIED="1202364741991" TEXT="sorting_test">
<node CREATED="1202364743010" ID="Freemind_Link_312051561" MODIFIED="1202364743462" POSITION="right" TEXT="a"/>
<node CREATED="1202364743840" ID="Freemind_Link_482760885" MODIFIED="1202364744319" POSITION="right" TEXT="b"/>
<node CREATED="1202364744795" ID="Freemind_Link_797200690" MODIFIED="1209011676844" POSITION="right" TEXT="c">
<attribute NAME="script1" VALUE="import java.awt.datatransfer.Transferable;&#xa;import java.util.Comparator;&#xa;import java.util.Iterator;&#xa;import java.util.TreeSet;&#xa;import java.util.Vector;&#xa;import freemind.modes.MindMapNode;&#xa;&#xa;&#x9;class IconComparator implements java.util.Comparator {&#xa;&#x9;&#x9;&#x9;int compare(java.lang.Object pArg0, java.lang.Object pArg1) {&#xa;&#x9;&#x9;&#x9;&#x9;if (pArg0 instanceof MindMapNode) {&#xa;&#x9;&#x9;&#x9;&#x9;&#x9;MindMapNode node1 = (MindMapNode) pArg0;&#xa;&#x9;&#x9;&#x9;&#x9;&#x9;if (pArg1 instanceof MindMapNode) {&#xa;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;MindMapNode node2 = (MindMapNode) pArg1;&#xa;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;String iconText1 = getIconText(node1);&#xa;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;String iconText2 = getIconText(node2);&#xa;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;//print &quot;comparing&quot; + iconText1 + &quot; with &quot; + iconText2 + &quot;\n&quot;;&#xa;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;return iconText1.compareToIgnoreCase(iconText2);&#xa;&#x9;&#x9;&#x9;&#x9;&#x9;}&#xa;&#x9;&#x9;&#x9;&#x9;}&#xa;&#x9;&#x9;&#x9;&#x9;return 0;&#xa;&#x9;&#x9;&#x9;}&#xa;&#x9;&#x9;def getIconText(MindMapNode n) {&#xa;&#x9;&#x9;&#x9;if(n.getIcons() == null || n.getIcons().size()==0) &#xa;&#x9;&#x9;&#x9;&#x9;return &quot;&quot;;&#xa;&#x9;&#x9;&#x9;def retString = &quot;&quot;;&#xa;&#x9;&#x9;&#x9;def it = n.getIcons().iterator();&#xa;&#x9;&#x9;&#x9;while(it.hasNext()) {&#xa;&#x9;&#x9;&#x9;&#x9;retString +=it.next().getName()+&quot;, &quot;;&#xa;&#x9;&#x9;&#x9;}&#xa;&#x9;&#x9;&#x9;return retString;&#xa;&#x9;&#x9;}&#xa;&#x9;}&#xa;&#xa;&#xa;&#x9;&#x9;// we want to sort the children of the node:&#xa;&#x9; &#x9;Vector children = new Vector();&#xa;&#x9;&#x9;// put in all children of the node&#xa;&#x9;&#x9;children.addAll(node.getChildren());&#xa;&#x9;&#x9;// sort them&#xa;&#x9;&#x9;java.util.Collections.sort(children, new IconComparator());&#xa;&#x9;&#x9;//print &quot;The set has &quot; + children.size() + &quot; entries\n&quot;;&#xa;&#x9;&#x9;// now, as it is sorted. we cut the children&#xa;&#x9;&#x9;def it2 = children.iterator();&#xa;&#x9;&#x9;while (it2.hasNext()) {&#xa;&#x9;&#x9;&#x9;MindMapNode child = (MindMapNode) it2.next();&#xa;&#x9;&#x9;&#x9;Vector childList = new Vector();&#xa;&#x9;&#x9;&#x9;childList.add(child);&#xa;&#x9;&#x9;&#x9;Transferable cut = c.cut(childList);&#xa;&#x9;&#x9;&#x9;// paste directly again causes that the node is added as the last one.&#xa;&#x9;&#x9;&#x9;c.paste(cut, node);&#xa;&#x9;&#x9;}&#xa;&#x9;&#x9;c.select(c.getNodeView(node));&#xa;//SIGN:MCwCFAYmPOZvFJPl2FMo9UAhLpeElx9aAhQKldu6e8nMxdvZGV47XBUwJ4uBtA==&#xa;"/>
<node CREATED="1202364756003" ID="Freemind_Link_103214872" MODIFIED="1202364762999" TEXT="1">
<icon BUILTIN="full-1"/>
</node>
<node CREATED="1202364756716" ID="Freemind_Link_780056662" MODIFIED="1202364761496" TEXT="2">
<icon BUILTIN="full-2"/>
</node>
<node CREATED="1202364746442" ID="Freemind_Link_666280381" MODIFIED="1202364771028" TEXT="3">
<icon BUILTIN="full-3"/>
</node>
<node CREATED="1202364748534" ID="Freemind_Link_389808003" MODIFIED="1202364769832" TEXT="4">
<icon BUILTIN="full-4"/>
</node>
<node CREATED="1202364750213" ID="Freemind_Link_1378958926" MODIFIED="1202364768854" TEXT="5">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1202364750974" ID="Freemind_Link_1550921232" MODIFIED="1202364767750" TEXT="6">
<icon BUILTIN="full-6"/>
</node>
<node CREATED="1202364751671" ID="Freemind_Link_1050852268" MODIFIED="1202364766632" TEXT="7">
<icon BUILTIN="full-7"/>
</node>
<node CREATED="1202364752439" ID="Freemind_Link_1453054552" MODIFIED="1202364765465" TEXT="8">
<icon BUILTIN="full-7"/>
<icon BUILTIN="full-7"/>
</node>
</node>
</node>
</map>
