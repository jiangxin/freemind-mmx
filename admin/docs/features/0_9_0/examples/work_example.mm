<map version="0.9.0_Beta_8">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1202190823702" ID="Freemind_Link_1475975383" MODIFIED="1209011695768" TEXT="New Mindmap">
<attribute NAME="script1" VALUE="class MyNodeListener implements freemind.modes.ModeController.NodeSelectionListener {&#xa;&#x9;freemind.modes.mindmapmode.MindMapController c;&#xa;        MyNodeListener(freemind.modes.mindmapmode.MindMapController con) {&#xa;&#x9;&#x9;this.c = con;&#xa;&#x9;&#x9;}&#xa;&#xa;&#x9;&#x9;/** &#xa;         * Sent, if a node is changed&#xa;         * */&#xa;        void onUpdateNodeHook(freemind.modes.MindMapNode node){&#x9;&#x9;&#xa;&#x9;&#x9;&#x9;calcWork(c.getRootNode());&#xa;&#x9;&#x9;};&#xa;&#xa;        /** Is sent when a node is selected.&#xa;         */&#xa;        void onSelectHook(freemind.view.mindmapview.NodeView node){};&#xa;        /**&#xa;         * Is sent when a node is deselected.&#xa;         */&#xa;        void onDeselectHook(freemind.view.mindmapview.NodeView node){};&#xa;&#xa;&#x9;&#x9;/**&#xa;&#x9;&#x9; * Is issued before a node is saved (eg. to save its notes, too, even if the notes is currently edited).&#xa;&#x9;&#x9; */&#xa;&#x9;&#x9;void onSaveNode(freemind.modes.MindMapNode node){};&#xa;&#xa;def calcWork(child) {&#xa;&#x9;def sum = 0;&#xa;&#x9;def it = child.childrenUnfolded(); &#xa;&#x9;while(it.hasNext()) { &#xa;&#x9;&#x9;def child2 = it.next(); &#xa;&#x9;&#x9;sum += calcWork(child2);&#xa;&#x9;&#x9;def w = child2.getAttribute(&quot;work&quot;);&#xa;&#x9;&#x9;if(w != null)&#xa;&#x9;&#x9;&#x9;sum += Integer.parseInt( w);&#xa;&#x9;}&#xa;&#x9;if(sum&gt;0)&#xa;&#x9;&#x9;c.editAttribute(child, &quot;sum&quot;, (String) sum);&#xa;&#x9;return sum;&#xa;}&#xa;&#xa;}&#xa;&#xa;def cookieKey = &quot;work_update_listener&quot;;&#xa;if(cookies.get(cookieKey) != null) {&#xa;&#x9;c.deregisterNodeSelectionListener(cookies.get(cookieKey));&#xa;}&#xa;def newListener = new MyNodeListener(c);&#xa;cookies.put(cookieKey, newListener);&#xa;c.registerNodeSelectionListener(newListener);&#xa;//SIGN:MC0CFQCDdH8/a9Ym7XEB8ebYuyA9NWHH4wIULOXDt5q2pZkfT2+6oR2ZyWV1h3c=&#xa;"/>
<attribute NAME="sum" VALUE="106"/>
<node CREATED="1202223325608" ID="Freemind_Link_459585472" MODIFIED="1202750545273" POSITION="right" TEXT="a0">
<attribute NAME="work" VALUE="17"/>
<attribute NAME="sum" VALUE="66"/>
<node CREATED="1202223161308" ID="Freemind_Link_1252106239" MODIFIED="1202241339440" TEXT="a">
<attribute NAME="work" VALUE="16"/>
<attribute NAME="sum" VALUE="50"/>
<node CREATED="1202223171192" ID="Freemind_Link_724411849" MODIFIED="1202223554842" TEXT="b">
<attribute NAME="work" VALUE="30"/>
</node>
<node CREATED="1202223172740" ID="Freemind_Link_944132774" MODIFIED="1202223554842" TEXT="c">
<attribute NAME="work" VALUE="20"/>
</node>
</node>
</node>
<node CREATED="1202223343960" ID="Freemind_Link_1266374392" MODIFIED="1202241338902" POSITION="right" TEXT="a1">
<attribute NAME="work" VALUE="0"/>
<attribute NAME="sum" VALUE="23"/>
<node CREATED="1202223356953" ID="Freemind_Link_1353605196" MODIFIED="1202224937753" TEXT="a3">
<font NAME="SansSerif" SIZE="12"/>
<attribute NAME="work" VALUE="19"/>
</node>
<node CREATED="1202224938280" ID="Freemind_Link_90865123" MODIFIED="1202224941004" TEXT="asdf"/>
<node CREATED="1202223373895" ID="Freemind_Link_1875670462" MODIFIED="1202223554844" TEXT="a4">
<attribute NAME="work" VALUE="4"/>
<node CREATED="1202223467200" ID="Freemind_Link_1973790271" MODIFIED="1202223554845" TEXT="a5"/>
<node CREATED="1202223469810" ID="Freemind_Link_822734801" MODIFIED="1202223554845" TEXT="a6"/>
</node>
</node>
</node>
</map>
