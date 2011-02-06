<map version="0.9.0_Beta_8">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1202925311258" ID="Freemind_Link_1880326040" LINK="tmp/pp.mm" MODIFIED="1209011648049" TEXT="g">
<attribute NAME="script1" VALUE="&#xa;class MyNodeListener implements freemind.modes.ModeController.NodeSelectionListener {&#xa;&#x9;freemind.modes.mindmapmode.MindMapController c;&#xa;        MyNodeListener(freemind.modes.mindmapmode.MindMapController con) {&#xa;&#x9;&#x9;this.c = con;&#xa;&#x9;&#x9;}&#xa;&#xa;&#x9;&#x9;/** &#xa;         * Sent, if a node is changed&#xa;         * */&#xa;        void onUpdateNodeHook(freemind.modes.MindMapNode node){&#x9;&#x9;&#xa;&#x9;&#x9;};&#xa;&#xa;        /** Is sent when a node is selected.&#xa;         */&#xa;        void onSelectHook(freemind.view.mindmapview.NodeView node){&#xa;&#x9;&#x9;&#x9;if(c.getSelecteds().size()&gt;1)&#xa;&#x9;&#x9;&#x9;&#x9;return;&#xa;&#x9;&#x9;&#x9;// unfold node:&#xa;&#x9;&#x9;&#x9;c.setFolded(node.getModel(), false);&#xa;&#x9;&#x9;&#x9;// fold every child:&#xa;                def it2 = node.getModel().childrenUnfolded().iterator();&#xa;                while (it2.hasNext()) {&#xa;                        def child = it2.next();&#xa;&#x9;&#x9;&#x9;&#x9;  c.setFolded(child, true);&#xa;&#x9;&#x9;&#x9;}&#xa;&#x9;&#x9;&#x9;// close everything else:&#xa;&#x9;&#x9;&#x9;foldEverybody(node.getModel().getParent(),node.getModel());&#xa;&#x9;&#x9;};&#xa;        /**&#xa;         * Is sent when a node is deselected.&#xa;         */&#xa;        void onDeselectHook(freemind.view.mindmapview.NodeView node){};&#xa;&#xa;&#x9;&#x9;/**&#xa;&#x9;&#x9; * Is issued before a node is saved (eg. to save its notes, too, even if the notes is currently edited).&#xa;&#x9;&#x9; */&#xa;&#x9;&#x9;void onSaveNode(freemind.modes.MindMapNode node){};&#xa;def foldEverybody(child, exception) {&#xa;&#x9;&#x9;if(child == null || child.isRoot())&#xa;&#x9;&#x9;&#x9;return;&#xa;        def it = child.childrenUnfolded();&#xa;        while(it.hasNext()) {&#xa;                def child2 = it.next();&#xa;&#x9;&#x9;&#x9;if(child2 != exception) {&#xa;&#x9;&#x9;&#x9;&#x9;c.setFolded(child2, true);&#xa;&#x9;&#x9;&#x9;}&#xa;        }&#xa;&#x9;if(!child.getParent().isRoot())&#xa;&#x9;&#x9;foldEverybody(child.getParent(), exception.getParent());&#xa;}&#xa;&#xa;&#xa;}&#xa;&#xa;def cookieKey = &quot;work_update_listener&quot;;&#xa;if(cookies.get(cookieKey) != null) {&#xa;&#x9;c.deregisterNodeSelectionListener(cookies.get(cookieKey));&#xa;}&#xa;def newListener = new MyNodeListener(c);&#xa;cookies.put(cookieKey, newListener);&#xa;c.registerNodeSelectionListener(newListener);&#xa;//SIGN:MCwCFHazSeZo6i54LU7TPPFUXcqcy3uVAhQqIuRtCXuv8vqHW4I5DK6aSIl3Ag==&#xa;"/>
<node CREATED="1202925316254" ID="Freemind_Link_882744846" MODIFIED="1202925316538" POSITION="right" TEXT="a">
<node CREATED="1202925317550" ID="Freemind_Link_805290174" MODIFIED="1202925317882" TEXT="b">
<node CREATED="1202925316254" FOLDED="true" ID="Freemind_Link_162466756" MODIFIED="1202925316538" TEXT="a">
<node CREATED="1202925317550" ID="Freemind_Link_91941942" MODIFIED="1202925317882" TEXT="b"/>
<node CREATED="1202925318260" ID="Freemind_Link_1371972988" MODIFIED="1202925318475" TEXT="b"/>
<node CREATED="1202925318909" ID="Freemind_Link_146448977" MODIFIED="1202925319460" TEXT="c"/>
</node>
<node CREATED="1202925316254" FOLDED="true" ID="Freemind_Link_416132069" MODIFIED="1202925316538" TEXT="a">
<node CREATED="1202925317550" ID="Freemind_Link_1206924312" MODIFIED="1202925317882" TEXT="b"/>
<node CREATED="1202925318260" ID="Freemind_Link_1961622678" MODIFIED="1202925318475" TEXT="b"/>
<node CREATED="1202925318909" ID="Freemind_Link_245015046" MODIFIED="1202925319460" TEXT="c"/>
</node>
<node CREATED="1202925316254" FOLDED="true" ID="Freemind_Link_907444440" MODIFIED="1202925316538" TEXT="a">
<node CREATED="1202925317550" ID="Freemind_Link_1585348774" MODIFIED="1202925317882" TEXT="b"/>
<node CREATED="1202925318260" ID="Freemind_Link_920140108" MODIFIED="1202925318475" TEXT="b"/>
<node CREATED="1202925318909" ID="Freemind_Link_539266803" MODIFIED="1202925319460" TEXT="c"/>
</node>
</node>
<node CREATED="1202925318260" ID="Freemind_Link_671539113" MODIFIED="1202925318475" TEXT="b">
<node CREATED="1202925316254" FOLDED="true" ID="Freemind_Link_314035780" MODIFIED="1202925316538" TEXT="a">
<node CREATED="1202925317550" ID="Freemind_Link_1489697493" MODIFIED="1202925317882" TEXT="b"/>
<node CREATED="1202925318260" ID="Freemind_Link_429738948" MODIFIED="1202925318475" TEXT="b"/>
<node CREATED="1202925318909" ID="Freemind_Link_716423237" MODIFIED="1202925319460" TEXT="c"/>
</node>
<node CREATED="1202925316254" FOLDED="true" ID="Freemind_Link_1694371544" MODIFIED="1202925316538" TEXT="a">
<node CREATED="1202925317550" ID="Freemind_Link_1049703117" MODIFIED="1202925317882" TEXT="b"/>
<node CREATED="1202925318260" ID="Freemind_Link_1038192991" MODIFIED="1202925318475" TEXT="b"/>
<node CREATED="1202925318909" ID="Freemind_Link_1320004318" MODIFIED="1202925319460" TEXT="c"/>
</node>
</node>
<node CREATED="1202925318909" ID="Freemind_Link_154391063" MODIFIED="1202925319460" TEXT="c">
<node CREATED="1202925316254" FOLDED="true" ID="Freemind_Link_1009507415" MODIFIED="1202925316538" TEXT="a">
<node CREATED="1202925317550" ID="Freemind_Link_733050354" MODIFIED="1202925317882" TEXT="b"/>
<node CREATED="1202925318260" ID="Freemind_Link_75837256" MODIFIED="1202925318475" TEXT="b"/>
<node CREATED="1202925318909" ID="Freemind_Link_593864584" MODIFIED="1202925319460" TEXT="c"/>
</node>
</node>
</node>
</node>
</map>
