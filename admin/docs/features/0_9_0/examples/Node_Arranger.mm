<map version="0.9.0_Beta_8">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1205164043140" ID="ID_411342563" MODIFIED="1205767505828" TEXT="New Mindmap">
<icon BUILTIN="ksmiletris"/>
<node CREATED="1205152799484" HGAP="46" ID="ID_494639852" MODIFIED="1209011622761" POSITION="right" TEXT="EDIT MASTER" VGAP="0" VSHIFT="5">
<icon BUILTIN="ksmiletris"/>
<attribute_layout NAME_WIDTH="45"/>
<attribute NAME="script3" VALUE="/* Proof of concept script to layout the children of a node programatically    */&#xa;/* Idea is to generate a table of [x,y] coordinates to fit a desired layout    */&#xa;/* Just add your own routines to create shapes for other layout format         */&#xa;/* Needs input dialogue to select the preferred shape, default sin wave shape  */&#xa;/* is best with 20+ odd nodes. Some ropey hard coded vals are usedto compensate*/&#xa;/* for various offsets in use that I&apos;m still trying to quantify/compensate for */&#xa;&#xa;//Count number of children&#xa;def int numChildren = countChildren(node);&#xa;def nodeText = node.getShortText();&#xa;nodeText = nodeText.substring(0, Math.min (20, nodeText.length()));&#xa;&#xa;/+ ---------------First select a shape for plotting children of this node------*/ &#xa;//Options: WAVE, ALIGN or SLOPE - add your own tables&#xa;&#xa;import javax.swing.JOptionPane;&#xa;&#xa;//Work around, because can&apos;t create array as expected using:&#xa;//Object[] options = new Object{&quot;a&quot; , &quot;b&quot; , &quot;c&quot;};&#xa;&#xa;Object[] options = new Object[3];&#xa;options[0] = &quot;WAVE&quot;;&#xa;options[1] = &quot;ALIGN&quot;;&#xa;options[2] = &quot;SLOPE&quot;;&#xa;&#xa;def int option = JOptionPane.showOptionDialog(null,&#xa;&#x9;&quot;Select an arrangement for the children of the\nnode who&apos;s text begins: &quot; + nodeText, // the question being asked&#xa;&#x9;&quot;Node Arranger...&quot;, // the title&#xa;&#x9;JOptionPane.YES_NO_CANCEL_OPTION, // set dialog options&#xa;&#x9;JOptionPane.QUESTION_MESSAGE, // set dialog type&#xa;&#x9;null, // no icon&#xa;&#x9;options, //list for dropdown&#xa;&#x9;options[0] // 1st item is initial selection&#xa;);&#xa;&#xa;if (option != -1) {&#xa;&#x9;print &quot;SELECT&quot;;&#xa;&#x9;shapeSelection = options[option];&#xa;} else {&#xa;&#x9;print &quot;NO SELECT&quot;;&#xa;&#x9;shapeSelection = options[0];&#xa;};&#xa;&#x9;&#x9;&#xa;//select which table to use&#xa;&#xa;switch (shapeSelection) {   &#xa;case &apos;WAVE&apos;:       &#xa;&#x9;shape = makeSinTable(200,0,numChildren,100);&#xa;&#x9;break   &#xa;case &apos;ALIGN&apos;:       &#xa;&#x9;shape = makeAlignVertical(numChildren,100,0);&#xa;&#x9;break   &#xa;case &apos;SLOPE&apos;:       &#xa;&#x9;shape = makesSlopeHorizontal(numChildren,100,0)&#xa;&#x9;break  &#xa;default:       &#xa;&#x9;shape = makeSinTable(200,0,numChildren,140);&#xa;}&#xa;&#xa;//move the nodes children&#xa;&#xa;plotChildren(node, shape);&#xa;&#xa;//Create a table of [x,y] coordinates (from params) to plot a Sin Wave&#xa;//Really, the values for a circle, but displayed as wave by Freemind (why?)&#xa;&#xa;def makeSinTable(int xOffset, int yOffset, float numNodes, float radius){&#xa;&#xa; &#x9;&#x9;def table = [];&#xa;    angInc = (float) (2.0 * Math.PI / numNodes);&#xa;&#x9;&#x9;&#x9;&#x9;&#xa;    for (angle = 0.0f; angle &lt; 2.0f * Math.PI; angle += angInc){&#xa;            def int x = xOffset + (int) (radius * Math.cos(angle));&#xa;            def int y = yOffset + (int) (radius * Math.sin(angle));&#xa;        &#x9;&#x9;table.add([x,y])}&#x9;&#x9;&#xa;&#x9;&#x9;return table;&#xa;}&#xa;&#xa;//Create a table of [x,y] coordinates (from params) to plot Vertical Align&#xa;def makeAlignVertical(int numPoints, int xOffset, int yOffset){&#xa; &#x9;&#x9;def table = [];&#xa;    for (inc = 0; inc &lt;= numPoints; inc += 1){&#xa;    //*10 hack - to be fixed&#xa;&#x9;&#x9;table.add([xOffset, yOffset *10]);&#xa;&#x9;&#x9;&#x9;yOffset++;&#xa;&#x9;&#x9;}&#xa;&#x9;&#x9;return table;&#xa;}&#xa;&#xa;//Create a table of [x,y] coordinates (from params) to plot Slope Align&#xa;&#xa;def makesSlopeHorizontal(int numPoints, int xOffset, int yOffset){&#xa; &#x9;&#x9;def table = [];&#xa;    for (inc = 0; inc &lt;= numPoints; inc += 1){&#xa;    &#x9;table.add([xOffset,yOffset]);&#xa;&#x9;&#x9;&#x9;yOffset = -(numPoints * 10);&#xa;&#x9;&#x9;&#x9;xOffset = xOffset + 30;&#xa;&#x9;&#x9;}&#xa;&#x9;&#x9;return table;&#xa;}&#xa;&#xa;//plot location of a node&apos;s children based on the shape array passed&#xa;&#xa;def plotChildren(aNode, array) {&#xa;&#x9;def x = 0; &#xa;&#x9;def children = aNode.childrenUnfolded();&#xa;&#x9;while  (children.hasNext()) {&#xa;&#x9;&#x9;&#x9;child = children.next();&#xa;&#x9;&#x9;&#x9;xOffset = array[x][0];&#xa;&#x9;&#x9;&#x9;//hacky /1.5 tweak to compensate for offsets in use&#xa;&#x9;&#x9;&#x9;xOffset = (xOffset/1.5).toInteger();&#xa;&#x9;&#x9;&#x9;yOffset = array[x][1];&#xa;&#x9;&#x9;&#x9;//hacky /50 tweak to compensate for offsets in use&#xa;&#x9;&#x9;&#x9;yOffset = (yOffset/50).toInteger();&#xa;&#x9;&#x9;&#x9;c.moveNodePosition(child, yOffset, xOffset, 0);&#xa;&#x9;&#x9;&#x9;x++;&#xa;&#x9;&#x9;}&#xa;}&#xa;&#xa;//count number of nodes children&#xa;&#xa;def countChildren(aNode){&#xa;&#x9;return aNode.getChildCount();&#xa;};&#xa;//SIGN:MCwCFC/Xr1o1gzpt9rNWktWm9qpGiXugAhQ29MD99Ejxydt6zRTj4Pf0g1qKTQ==&#xa;"/>
<node CREATED="1205767487640" HGAP="200" ID="ID_1938091572" MODIFIED="1207839653977" TEXT="eryery">
<icon BUILTIN="ksmiletris"/>
</node>
<node CREATED="1205767488375" HGAP="198" ID="ID_1604019682" MODIFIED="1207839653978" TEXT="wrywry">
<icon BUILTIN="ksmiletris"/>
</node>
<node CREATED="1205767489109" HGAP="192" ID="ID_428794586" MODIFIED="1207839653979" TEXT="ryewry">
<icon BUILTIN="ksmiletris"/>
</node>
<node CREATED="1205767489812" HGAP="182" ID="ID_647097227" MODIFIED="1207839653979" TEXT="wryryw">
<icon BUILTIN="ksmiletris"/>
</node>
<node CREATED="1205767490546" HGAP="170" ID="ID_1461350484" MODIFIED="1207839653980" TEXT="wrywry">
<icon BUILTIN="ksmiletris"/>
</node>
<node CREATED="1205767491234" HGAP="156" ID="ID_748502529" MODIFIED="1207839653981" TEXT="rwyryw">
<icon BUILTIN="ksmiletris"/>
</node>
<node CREATED="1205767491968" HGAP="141" ID="ID_264169564" MODIFIED="1207839653981" TEXT="wryryw">
<icon BUILTIN="ksmiletris"/>
</node>
<node CREATED="1205767492703" HGAP="125" ID="ID_1666132865" MODIFIED="1207839653982" TEXT="rywrwy">
<icon BUILTIN="ksmiletris"/>
</node>
<node CREATED="1205767493437" HGAP="110" ID="ID_886813500" MODIFIED="1207839653982" TEXT="rywwry">
<icon BUILTIN="ksmiletris"/>
</node>
<node CREATED="1205767494171" HGAP="96" ID="ID_1470980969" MODIFIED="1207839653983" TEXT="wrywry">
<icon BUILTIN="ksmiletris"/>
</node>
<node CREATED="1205767494921" HGAP="84" ID="ID_36891299" MODIFIED="1207839653984" TEXT="wrywry">
<icon BUILTIN="ksmiletris"/>
</node>
<node CREATED="1205767495687" HGAP="74" ID="ID_1272090537" MODIFIED="1207839653984" TEXT="wryryw">
<icon BUILTIN="ksmiletris"/>
</node>
<node CREATED="1205767496484" HGAP="68" ID="ID_1603480583" MODIFIED="1207839653985" TEXT="wrywery">
<icon BUILTIN="ksmiletris"/>
</node>
<node CREATED="1205767497156" HGAP="67" ID="ID_1784931250" MODIFIED="1207839653986" TEXT="wwrywryywr">
<icon BUILTIN="ksmiletris"/>
</node>
<node CREATED="1205767499421" HGAP="68" ID="ID_86860638" MODIFIED="1207839653986" TEXT="wwry">
<icon BUILTIN="ksmiletris"/>
</node>
<node CREATED="1206628383031" HGAP="74" ID="ID_463846949" MODIFIED="1207839653987" TEXT="jjgjkgkjg">
<icon BUILTIN="ksmiletris"/>
</node>
<node CREATED="1206628392531" HGAP="84" ID="ID_724384965" MODIFIED="1207839653987" TEXT="jgkjgjgk">
<icon BUILTIN="ksmiletris"/>
</node>
<node CREATED="1206628408203" HGAP="96" ID="ID_1850484226" MODIFIED="1207839653988" TEXT="jgkjgjgkjgj">
<icon BUILTIN="ksmiletris"/>
</node>
<node CREATED="1206628486250" HGAP="110" ID="ID_130778627" MODIFIED="1207839653989" TEXT="kjgjkgkjgjg">
<icon BUILTIN="ksmiletris"/>
</node>
<node CREATED="1206628488968" HGAP="125" ID="ID_879801258" MODIFIED="1207839653989" TEXT="jhfjhfjhfhf">
<icon BUILTIN="ksmiletris"/>
</node>
<node CREATED="1206628490703" HGAP="141" ID="ID_482594983" MODIFIED="1207839653990" TEXT="hgjhgkjgkjg">
<icon BUILTIN="ksmiletris"/>
</node>
<node CREATED="1206628492234" HGAP="156" ID="ID_512324854" MODIFIED="1207839653990" TEXT="ghjgfjhfjhfjf">
<icon BUILTIN="ksmiletris"/>
</node>
<node CREATED="1206628493828" HGAP="170" ID="ID_1886984045" MODIFIED="1207839653991" TEXT="fjhfjhfjf">
<icon BUILTIN="ksmiletris"/>
</node>
<node CREATED="1206628495281" HGAP="182" ID="ID_1311576265" MODIFIED="1207839653992" TEXT="jhhgkjgkjg">
<icon BUILTIN="ksmiletris"/>
</node>
<node CREATED="1206628512359" HGAP="192" ID="ID_1668458319" MODIFIED="1207839653992" TEXT="kgjkjgkj">
<icon BUILTIN="ksmiletris"/>
</node>
<node CREATED="1206628517390" HGAP="198" ID="ID_925634406" MODIFIED="1207839653993" TEXT="fkjkjgjkgk">
<icon BUILTIN="ksmiletris"/>
</node>
</node>
</node>
</map>
