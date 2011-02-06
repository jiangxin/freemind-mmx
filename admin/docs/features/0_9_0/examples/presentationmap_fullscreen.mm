<map version="0.9.0">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1202925311258" ID="Freemind_Link_1880326040" LINK="tmp/pp.mm" MODIFIED="1215007980360" TEXT="Presentation">
<attribute NAME="script1" VALUE="import javax.swing.*;&#xa;import java.awt.event.ActionEvent;&#xa;import freemind.modes.ModeController;&#xa;&#xa;class MyNodeListener implements freemind.modes.ModeController.NodeSelectionListener {&#xa;&#x9;freemind.modes.mindmapmode.MindMapController c;&#xa;        MyNodeListener(freemind.modes.mindmapmode.MindMapController con) {&#xa;&#x9;&#x9;this.c = con;&#xa;&#x9;&#x9;}&#xa;&#xa;&#x9;&#x9;/** &#xa;         * Sent, if a node is changed&#xa;         * */&#xa;        void onUpdateNodeHook(freemind.modes.MindMapNode node){&#x9;&#x9;&#xa;&#x9;&#x9;};&#xa;&#xa;        /** Is sent when a node is selected.&#xa;         */&#xa;        void onSelectHook(freemind.view.mindmapview.NodeView nodeView){&#xa;&#x9;&#x9;&#x9;def node = nodeView.getModel();&#xa;&#x9;&#x9;&#x9;if(c.getSelecteds().size()&gt;1)&#xa;&#x9;&#x9;&#x9;&#x9;return;&#xa;&#x9;&#x9;&#x9;// unfold node:&#xa;&#x9;&#x9;&#x9;c.setFolded(node, false);&#xa;&#x9;&#x9;&#x9;// fold every child:&#xa;      def it2 = node.childrenUnfolded().iterator();&#xa;      while (it2.hasNext()) {&#xa;          def child = it2.next();&#xa;&#x9;&#x9;&#x9;&#x9;  if(child.hasChildren()) {&#xa;&#x9;&#x9;&#x9;&#x9;&#x9;  c.setFolded(child, false);&#xa;&#x9;&#x9;&#x9;&#x9;&#x9;  foldChildren(child,null);&#xa;&#x9;&#x9;&#x9;&#x9; }&#xa;&#x9;&#x9;&#x9;}&#xa;&#x9;&#x9;&#x9;// close everything else:&#xa;&#x9;&#x9;&#x9;if(!node.isRoot()) {&#xa;&#x9;&#x9;&#x9;&#x9;foldEverybody(node.getParent(),node);&#xa;&#x9;&#x9;&#x9;}&#xa;&#x9;&#x9;};&#xa;        /**&#xa;         * Is sent when a node is deselected.&#xa;         */&#xa;        void onDeselectHook(freemind.view.mindmapview.NodeView node){};&#xa;&#xa;&#x9;&#x9;/**&#xa;&#x9;&#x9; * Is issued before a node is saved (eg. to save its notes, too, even if the notes is currently edited).&#xa;&#x9;&#x9; */&#xa;&#x9;&#x9;void onSaveNode(freemind.modes.MindMapNode node){};&#xa;&#xa;&#x9;def foldEverybody(child, exception) {&#xa;&#x9;&#x9;if(child == null)&#xa;&#x9;&#x9;&#x9;return;&#xa;    def it = child.childrenUnfolded();&#xa;    while(it.hasNext()) {&#xa;      def child2 = it.next();&#xa;&#x9;&#x9;&#x9;if(child2 != exception) {&#xa;&#x9;&#x9;&#x9;&#x9;c.setFolded(child2, true);&#xa;&#x9;&#x9;&#x9;}&#xa;    }&#xa;&#x9;&#x9;if(child.isRoot()) {&#xa;&#x9;&#x9;&#x9;foldChildren(child, exception);&#xa;&#x9;&#x9;} else {&#xa;&#x9;&#x9;&#x9;if(!child.getParent().isRoot()) {&#xa;&#x9;&#x9;&#x9;&#x9;foldEverybody(child.getParent(), exception.getParent());&#xa;&#x9;&#x9;&#x9;} else {&#xa;&#x9;&#x9;&#x9;&#x9;foldChildren(child.getParent(), exception.getParent());&#x9;&#x9;&#xa;&#x9;&#x9;&#x9;}&#xa;&#x9;&#x9;}&#xa;&#x9;}&#xa;&#xa;&#x9;def foldChildren(child,exception) {&#xa;&#x9;&#x9;if(child == null)&#xa;&#x9;&#x9;&#x9;return;&#xa;    def it = child.childrenUnfolded();&#xa;    while(it.hasNext()) {&#xa;      def child2 = it.next();&#xa;&#x9;&#x9;  if(child2 != exception) {&#xa;&#x9;&#x9;&#x9;&#x9;c.setFolded(child2, true);&#xa;&#x9;&#x9;&#x9;}&#xa;    }&#xa;&#x9;}&#xa;}&#xa;&#xa;   class EscapeAction extends AbstractAction{&#xa;&#x9;&#x9;ModeController con;&#xa;&#x9;&#x9;MyNodeListener lis;&#xa;&#x9;&#x9;EscapeAction(ModeController c, MyNodeListener l) {&#xa;&#x9;&#x9;&#x9;this.con = c;&#xa;&#x9;&#x9;&#x9;this.lis = l;&#xa;&#x9;&#x9;}&#xa;&#x9;&#x9;public void actionPerformed(ActionEvent e) {&#xa;&#x9;&#x9;&#x9;con.getController().setMenubarVisible(true);&#xa;&#x9;&#x9;&#x9;con.getController().setToolbarVisible(true);&#xa;&#x9;&#x9;&#x9;con.getController().setLeftToolbarVisible(true);&#x9;&#xa;&#x9;&#x9;&#x9;con.getFrame().getJFrame().getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)&#xa;&#x9;&#x9;&#x9;      .put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, java.awt.event.InputEvent.ALT_DOWN_MASK),&#xa;&#x9;&#x9;              null);&#xa;&#x9;&#x9;&#x9;con.deregisterNodeSelectionListener(lis);&#xa;&#x9;&#x9;};&#x9;&#x9;   &#xa;   }&#xa;&#xa;&#xa;def cookieKey = &quot;work_update_listener&quot;;&#xa;if(cookies.get(cookieKey) != null) {&#xa;&#x9;c.deregisterNodeSelectionListener(cookies.get(cookieKey));&#xa;}&#xa;def newListener = new MyNodeListener(c);&#xa;cookies.put(cookieKey, newListener);&#xa;c.registerNodeSelectionListener(newListener);&#xa;c.getController().setMenubarVisible(false);&#xa;c.getController().setToolbarVisible(false);&#xa;c.getController().setLeftToolbarVisible(false);&#xa;def action = new EscapeAction(c, newListener);&#xa;action.putValue(Action.NAME, &quot;end_presentation&quot;);&#xa;// Register keystroke&#xa;c.getFrame().getJFrame().getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)&#xa;      .put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, java.awt.event.InputEvent.ALT_DOWN_MASK),&#xa;              action.getValue(Action.NAME));&#xa;&#xa;// Register action&#xa;c.getFrame().getJFrame().getRootPane().getActionMap().put(action.getValue(Action.NAME),&#xa;      action);"/>
<node CREATED="1202925316254" ID="Freemind_Link_882744846" MODIFIED="1215008017011" POSITION="right" TEXT="a">
<node CREATED="1202925317550" FOLDED="true" ID="Freemind_Link_805290174" MODIFIED="1215008014530" TEXT="b">
<node CREATED="1202925316254" FOLDED="true" ID="Freemind_Link_162466756" MODIFIED="1215007990885" TEXT="a">
<node CREATED="1202925317550" ID="Freemind_Link_91941942" MODIFIED="1202925317882" TEXT="b"/>
<node CREATED="1202925318260" ID="Freemind_Link_1371972988" MODIFIED="1202925318475" TEXT="b"/>
<node CREATED="1202925318909" ID="Freemind_Link_146448977" MODIFIED="1202925319460" TEXT="c"/>
</node>
<node CREATED="1202925316254" FOLDED="true" ID="Freemind_Link_416132069" MODIFIED="1215007990885" TEXT="a">
<node CREATED="1202925317550" ID="Freemind_Link_1206924312" MODIFIED="1202925317882" TEXT="b"/>
<node CREATED="1202925318260" ID="Freemind_Link_1961622678" MODIFIED="1202925318475" TEXT="b"/>
<node CREATED="1202925318909" ID="Freemind_Link_245015046" MODIFIED="1202925319460" TEXT="c"/>
</node>
<node CREATED="1202925316254" FOLDED="true" ID="Freemind_Link_907444440" MODIFIED="1215007990886" TEXT="a">
<node CREATED="1202925317550" ID="Freemind_Link_1585348774" MODIFIED="1202925317882" TEXT="b"/>
<node CREATED="1202925318260" ID="Freemind_Link_920140108" MODIFIED="1202925318475" TEXT="b"/>
<node CREATED="1202925318909" ID="Freemind_Link_539266803" MODIFIED="1202925319460" TEXT="c"/>
</node>
</node>
<node CREATED="1202925318260" FOLDED="true" ID="Freemind_Link_671539113" MODIFIED="1215008014531" TEXT="b">
<node CREATED="1202925316254" FOLDED="true" ID="Freemind_Link_314035780" MODIFIED="1215008001279" TEXT="a">
<node CREATED="1202925317550" ID="Freemind_Link_1489697493" MODIFIED="1202925317882" TEXT="b"/>
<node CREATED="1202925318260" ID="Freemind_Link_429738948" MODIFIED="1202925318475" TEXT="b"/>
<node CREATED="1202925318909" ID="Freemind_Link_716423237" MODIFIED="1202925319460" TEXT="c"/>
</node>
<node CREATED="1202925316254" FOLDED="true" ID="Freemind_Link_1694371544" MODIFIED="1215008001280" TEXT="a">
<node CREATED="1202925317550" ID="Freemind_Link_1049703117" MODIFIED="1202925317882" TEXT="b"/>
<node CREATED="1202925318260" ID="Freemind_Link_1038192991" MODIFIED="1202925318475" TEXT="b"/>
<node CREATED="1202925318909" ID="Freemind_Link_1320004318" MODIFIED="1202925319460" TEXT="c"/>
</node>
</node>
<node CREATED="1202925318909" FOLDED="true" ID="Freemind_Link_154391063" MODIFIED="1215008014531" TEXT="c">
<node CREATED="1202925316254" FOLDED="true" ID="Freemind_Link_1009507415" MODIFIED="1215007990903" TEXT="a">
<node CREATED="1202925317550" ID="Freemind_Link_733050354" MODIFIED="1202925317882" TEXT="b"/>
<node CREATED="1202925318260" ID="Freemind_Link_75837256" MODIFIED="1202925318475" TEXT="b"/>
<node CREATED="1202925318909" ID="Freemind_Link_593864584" MODIFIED="1202925319460" TEXT="c"/>
</node>
</node>
</node>
<node CREATED="1214885194932" ID="ID_530555549" MODIFIED="1214885196279" POSITION="left" TEXT="test"/>
<node CREATED="1214885197009" ID="ID_147475924" MODIFIED="1215008017012" POSITION="left" TEXT="test2">
<node CREATED="1214885200725" ID="ID_1972971616" MODIFIED="1214885201835" TEXT="asdfsadf"/>
<node CREATED="1214885202580" ID="ID_627780338" MODIFIED="1214885203572" TEXT="asdfasdf"/>
</node>
<node CREATED="1202925316254" ID="ID_569000102" MODIFIED="1215008017014" POSITION="right" TEXT="a">
<node CREATED="1202925317550" FOLDED="true" ID="ID_1673554726" MODIFIED="1215008014537" TEXT="b">
<node CREATED="1202925316254" FOLDED="true" ID="ID_1387099654" MODIFIED="1215007998736" TEXT="a">
<node CREATED="1202925317550" ID="ID_1676257381" MODIFIED="1202925317882" TEXT="b"/>
<node CREATED="1202925318260" ID="ID_1766936591" MODIFIED="1202925318475" TEXT="b"/>
<node CREATED="1202925318909" ID="ID_286434051" MODIFIED="1202925319460" TEXT="c"/>
</node>
<node CREATED="1202925316254" FOLDED="true" ID="ID_600328948" MODIFIED="1215007998736" TEXT="a">
<node CREATED="1202925317550" ID="ID_1694721176" MODIFIED="1202925317882" TEXT="b"/>
<node CREATED="1202925318260" ID="ID_661158166" MODIFIED="1202925318475" TEXT="b"/>
<node CREATED="1202925318909" ID="ID_324736120" MODIFIED="1202925319460" TEXT="c"/>
</node>
<node CREATED="1202925316254" FOLDED="true" ID="ID_1835509182" MODIFIED="1215007998737" TEXT="a">
<node CREATED="1202925317550" ID="ID_804007777" MODIFIED="1202925317882" TEXT="b"/>
<node CREATED="1202925318260" ID="ID_901330514" MODIFIED="1202925318475" TEXT="b"/>
<node CREATED="1202925318909" ID="ID_1766392312" MODIFIED="1202925319460" TEXT="c"/>
</node>
</node>
<node CREATED="1202925318260" FOLDED="true" ID="ID_1629839131" MODIFIED="1215008014537" TEXT="b">
<node CREATED="1202925316254" FOLDED="true" ID="ID_1416329448" MODIFIED="1215007998743" TEXT="a">
<node CREATED="1202925317550" ID="ID_429846882" MODIFIED="1202925317882" TEXT="b"/>
<node CREATED="1202925318260" ID="ID_1872881178" MODIFIED="1202925318475" TEXT="b"/>
<node CREATED="1202925318909" ID="ID_1968749583" MODIFIED="1202925319460" TEXT="c"/>
</node>
<node CREATED="1202925316254" FOLDED="true" ID="ID_1589561910" MODIFIED="1215007998744" TEXT="a">
<node CREATED="1202925317550" ID="ID_97235004" MODIFIED="1202925317882" TEXT="b"/>
<node CREATED="1202925318260" ID="ID_1385777887" MODIFIED="1202925318475" TEXT="b"/>
<node CREATED="1202925318909" ID="ID_1215891638" MODIFIED="1202925319460" TEXT="c"/>
</node>
</node>
<node CREATED="1202925318909" FOLDED="true" ID="ID_867539333" MODIFIED="1215008014537" TEXT="c">
<node CREATED="1202925316254" FOLDED="true" ID="ID_195659205" MODIFIED="1215007998748" TEXT="a">
<node CREATED="1202925317550" ID="ID_624900166" MODIFIED="1202925317882" TEXT="b"/>
<node CREATED="1202925318260" ID="ID_335442986" MODIFIED="1202925318475" TEXT="b"/>
<node CREATED="1202925318909" ID="ID_73486636" MODIFIED="1202925319460" TEXT="c"/>
</node>
</node>
</node>
</node>
</map>
