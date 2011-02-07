<map version="0.8.0_beta5">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node COLOR="#000000" ID="Freemind_Link_661254052" TEXT="Freemind 0.8.0 ToDos">
<font NAME="SansSerif" SIZE="20"/>
<hook NAME="accessories/plugins/AutomaticLayout.properties"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380732932" MODIFIED="1107901568379"/>
</hook>
<node COLOR="#0033ff" ID="Freemind_Link_1741074004" POSITION="left" TEXT="Undo">
<font NAME="SansSerif" SIZE="18"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380732957" MODIFIED="1107380732962"/>
</hook>
<node COLOR="#00b439" FOLDED="true" ID="Freemind_Link_241899915" TEXT="Transactions">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
<node COLOR="#990000" ID="Freemind_Link_559929439" TEXT="A possibility to avoid the transaction close problem is to stop the time between consecutive actions, and undo all that occur in less than 100msec. ">
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
<node COLOR="#00b439" FOLDED="true" ID="Freemind_Link_1531194022" TEXT="Paste without undo (PasteAction)">
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" ID="Freemind_Link_1915758209" TEXT="&#x9;/** URGENT: Change this method. */&#xa;&#x9;public void paste(MindMapNode node, MindMapNode parent) {&#xa;&#x9;&#x9;if (node != null) {&#xa;            insertNodeInto(node, parent);&#xa;&#x9;&#x9;&#x9;c.nodeStructureChanged(parent);&#xa;&#x9;&#x9;}&#xa;&#x9;}&#xa;">
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
</node>
<node COLOR="#0033ff" ID="_" POSITION="right" TEXT="Plugins">
<font NAME="SansSerif" SIZE="18"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380732968" MODIFIED="1107380732973"/>
</hook>
<node COLOR="#00b439" FOLDED="true" ID="Freemind_Link_1622666910" TEXT="Base plugin instance class">
<font NAME="SansSerif" SIZE="16"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733063" MODIFIED="1107380733069"/>
</hook>
<node COLOR="#990000" ID="Freemind_Link_1380475139" TEXT="Visible/Checked descision">
<font NAME="SansSerif" SIZE="14"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733073" MODIFIED="1107380733078"/>
</hook>
</node>
<node COLOR="#990000" ID="Freemind_Link_79747200" TEXT="Communication between various hooks">
<font NAME="SansSerif" SIZE="14"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733082" MODIFIED="1107380733089"/>
</hook>
</node>
</node>
<node COLOR="#00b439" ID="Freemind_Link_617003066" TEXT="Test plugin">
<font NAME="SansSerif" SIZE="16"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733094" MODIFIED="1107380733098"/>
</hook>
</node>
<node COLOR="#00b439" FOLDED="true" ID="Freemind_Link_1861328848" TEXT="Modifcation times">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_cancel"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733106" MODIFIED="1107380733110"/>
</hook>
<node COLOR="#990000" ID="Freemind_Link_409863622" TEXT="&gt;&#xa;&gt;&#xa;&gt;&#xa;&gt;3 Modification times&#xa;&gt;&#xa;&gt;  This is in my view such a standard and useful function that I would&#xa;&gt;  move it from the plugin into the core, being switched on by default.&#xa;&gt;&#xa;&gt;  Instead of storing the number of seconds (take 13 characters),&#xa;&gt;  I would store human readable yyyymmddhhmmss (take 14 characters).&#xa;&gt;&#xa;">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" ID="Freemind_Link_1989990814" TEXT="Modifcation times are not respected by drag&amp; drop">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="button_cancel"/>
<icon BUILTIN="button_cancel"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107900655647" MODIFIED="1107900782555"/>
</hook>
</node>
<node COLOR="#990000" ID="Freemind_Link_1035766747" TEXT="tooltips are not displayed upon node change.">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="button_cancel"/>
<icon BUILTIN="button_cancel"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107901012295" MODIFIED="1108188933446"/>
</hook>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107901021027" MODIFIED="1108188933446"/>
</hook>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107901620607" MODIFIED="1107901668274"/>
</hook>
</node>
</node>
</node>
<node COLOR="#0033ff" ID="Freemind_Link_313278498" POSITION="right" TEXT="Startup&#xa;&#xa;">
<font NAME="SansSerif" SIZE="18"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733117" MODIFIED="1109885285759"/>
</hook>
<node COLOR="#00b439" FOLDED="true" HGAP="58" ID="Freemind_Link_191520795" SHIFT_Y="-63" TEXT="BaseClass for FreemindMain and Applet ">
<font NAME="SansSerif" SIZE="16"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733128" MODIFIED="1114058009110"/>
</hook>
<node AA_NODE_CLASS="freemind.modes.mindmapmode.EncryptedMindMapNode" ADDITIONAL_INFO="0CqOp+MXKF4= S5vCVafj4a/QjmdiBcnbDj3iJ6cVI6kEaQLjEif2uJmhyQ58kXRo62NgWrH5hATFRoAMouOE1ixO&#xa;hDd2/eyVXfSt/djBTc+Qe17rjX1rY2DmYD6ywLnb7gbT9HG5rfWN5GQVnzNTEr9w42ORsJ0XKQ==" COLOR="#990000" FOLDED="true" ID="Freemind_Link_45751236" TEXT="password is aa">
<font NAME="SansSerif" SIZE="14"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733140" MODIFIED="1107380733144"/>
</hook>
</node>
</node>
<node COLOR="#00b439" HGAP="55" ID="Freemind_Link_963511801" SHIFT_Y="-68" TEXT="Windows.exe" VGAP="184">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733149" MODIFIED="1114058015506"/>
</hook>
</node>
</node>
<node COLOR="#0033ff" ID="Freemind_Link_1835883960" POSITION="left" TEXT="Menus">
<font NAME="SansSerif" SIZE="18"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733191" MODIFIED="1107380733196"/>
</hook>
<node COLOR="#00b439" ID="Freemind_Link_878523352" TEXT="Save into the right click menu">
<font NAME="SansSerif" SIZE="16"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733210" MODIFIED="1107380733215"/>
</hook>
</node>
</node>
<node COLOR="#0033ff" ID="Freemind_Link_874280203" POSITION="left" TEXT="Language">
<font NAME="SansSerif" SIZE="18"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733287" MODIFIED="1107380733294"/>
</hook>
</node>
<node COLOR="#0033ff" ID="Freemind_Link_926537596" POSITION="right" TEXT="Bugs">
<font NAME="SansSerif" SIZE="18"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733334" MODIFIED="1107380733340"/>
</hook>
<node COLOR="#00b439" ID="Freemind_Link_1923173530" TEXT="Links are not respected by Drag&apos;n&apos;Drop">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="help"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733346" MODIFIED="1107380733352"/>
</hook>
</node>
<node COLOR="#00b439" ID="Freemind_Link_1557925043" TEXT="Links double sometimes">
<font NAME="SansSerif" SIZE="16"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733357" MODIFIED="1107380733362"/>
</hook>
</node>
<node COLOR="#00b439" FOLDED="true" ID="Freemind_Link_468811635" TEXT="Cut/Paste Bug">
<font NAME="SansSerif" SIZE="16"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733366" MODIFIED="1107380733373"/>
</hook>
<node COLOR="#990000" ID="Freemind_Link_1297760803" TEXT="     [java] 25.11.2004 06:20:17 freemind.modes.ControllerAdapter registerMouseWheelEventHandler&#xa;     [java] INFO: Registered   MouseWheelEventHandler accessories.plugins.UnfoldAll$Registration@33c3e6&#xa;     [java] 25.11.2004 06:20:54 freemind.controller.actions.PrintActionHandler executeAction&#xa;     [java] INFO: &lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;yes&quot;?&gt;&#xa;     [java] &lt;compound_action&gt;&lt;cut_node_action isLeft=&quot;true&quot; asSibling=&quot;true&quot; node=&quot;Freemind_Link_1531194022&quot;&gt;&lt;transferable_content Transferable=&quot;&amp;lt;node ID=&amp;quot;Freemind_Link_241899915&amp;quot; COLOR=&amp;quot;#00b439&amp;quot; TEXT=&amp;quot;Transactions&amp;quot; FOLDED=&amp;quot;true&amp;quot;&amp;gt;&#xa;     [java] &amp;lt;font NAME=&amp;quot;SansSerif&amp;quot; SIZE=&amp;quot;16&amp;quot;/&amp;gt;&#xa;     [java] &amp;lt;icon BUILTIN=&amp;quot;button_ok&amp;quot;/&amp;gt;&#xa;     [java] &amp;lt;node ID=&amp;quot;Freemind_Link_559929439&amp;quot; COLOR=&amp;quot;#990000&amp;quot; TEXT=&amp;quot;A possibility to avoid the transaction close problem is to stop the time between consecutive actions, and undo all that occur in less than 100msec. &amp;quot;&amp;gt;&#xa;     [java] &amp;lt;font NAME=&amp;quot;SansSerif&amp;quot; SIZE=&amp;quot;14&amp;quot;/&amp;gt;&#xa;     [java] &amp;lt;/node&amp;gt;&#xa;     [java] &amp;lt;/node&amp;gt;&#xa;     [java] &quot;/&gt;&lt;/cut_node_action&gt;&lt;/compound_action&gt;&#xa;&#xa;&#xa;     [java] &lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;yes&quot;?&gt;&#xa;     [java] &lt;compound_action&gt;&lt;paste_node_action isLeft=&quot;true&quot; asSibling=&quot;true&quot; node=&quot;Freemind_Link_1531194022&quot;&gt;&lt;transferable_content Transferable=&quot;&amp;lt;node ID=&amp;quot;Freemind_Link_241899915&amp;quot; COLOR=&amp;quot;#00b439&amp;quot; TEXT=&amp;quot;Transactions&amp;quot; FOLDED=&amp;quot;true&amp;quot;&amp;gt;&#xa;     [java] &amp;lt;font NAME=&amp;quot;SansSerif&amp;quot; SIZE=&amp;quot;16&amp;quot;/&amp;gt;&#xa;     [java] &amp;lt;icon BUILTIN=&amp;quot;button_ok&amp;quot;/&amp;gt;&#xa;     [java] &amp;lt;node ID=&amp;quot;Freemind_Link_559929439&amp;quot; COLOR=&amp;quot;#990000&amp;quot; TEXT=&amp;quot;A possibility to avoid the transaction close problem is to stop the time between consecutive actions, and undo all that occur in less than 100msec. &amp;quot;&amp;gt;&#xa;     [java] &amp;lt;font NAME=&amp;quot;SansSerif&amp;quot; SIZE=&amp;quot;14&amp;quot;/&amp;gt;&#xa;     [java] &amp;lt;/node&amp;gt;&#xa;     [java] &amp;lt;/node&amp;gt;&#xa;     [java] &quot;/&gt;&lt;/paste_node_action&gt;&lt;/compound_action&gt;&#xa;&#xa;&#xa;     [java] java.lang.IndexOutOfBoundsException: Index: -1, Size: 1&#xa;     [java]     at java.util.LinkedList.entry(LinkedList.java:360)&#xa;     [java]     at java.util.LinkedList.get(LinkedList.java:303)&#xa;     [java]     at freemind.modes.NodeAdapter.getChildAt(NodeAdapter.java:483)&#xa;     [java]     at freemind.modes.actions.PasteAction$NodeCoordinate.getNode(PasteAction.java:170)&#xa;     [java]     at freemind.modes.actions.CutAction.act(CutAction.java:130)&#xa;     [java]     at freemind.modes.actions.CompoundActionHandler.act(CompoundActionHandler.java:60)&#xa;     [java]     at freemind.controller.actions.ModeControllerActionHandler.executeAction(ModeControllerActionHandler.java:43)&#xa;     [java]     at freemind.controller.actions.ActionFactory.executeAction(ActionFactory.java:115)&#xa;     [java]     at freemind.modes.actions.CutAction.cut(CutAction.java:114)&#xa;     [java]     at freemind.modes.ControllerAdapter.cut(ControllerAdapter.java:1031)&#xa;     [java]     at freemind.controller.NodeDropListener.drop(NodeDropListener.java:148)&#xa;     [java]     at java.awt.dnd.DropTarget.drop(DropTarget.java:398)&#xa;     [java]     at sun.awt.dnd.SunDropTargetContextPeer.processDropMessage(SunDropTargetContextPeer.java:542)&#xa;     [java]     at sun.awt.dnd.SunDropTargetContextPeer.access$800(SunDropTargetContextPeer.java:52)&#xa;     [java]     at sun.awt.dnd.SunDropTargetContextPeer$EventDispatcher.dispatchDropEvent(SunDropTargetContextPeer.java:805)&#xa;     [java]     at sun.awt.dnd.SunDropTargetContextPeer$EventDispatcher.dispatchEvent(SunDropTargetContextPeer.java:743)&#xa;     [java]     at sun.awt.dnd.SunDropTargetEvent.dispatch(SunDropTargetEvent.java:29)&#xa;     [java]     at java.awt.Component.dispatchEventImpl(Component.java:3494)&#xa;     [java]     at java.awt.Container.dispatchEventImpl(Container.java:1627)&#xa;     [java]     at java.awt.Component.dispatchEvent(Component.java:3477)&#xa;     [java]     at java.awt.LightweightDispatcher.retargetMouseEvent(Container.java:3483)&#xa;     [java]     at java.awt.LightweightDispatcher.processDropTargetEvent(Container.java:3269)&#xa;     [java]     at java.awt.LightweightDispatcher.dispatchEvent(Container.java:3123)&#xa;     [java]     at java.awt.Container.dispatchEventImpl(Container.java:1613)&#xa;     [java]     at java.awt.Window.dispatchEventImpl(Window.java:1606)&#xa;     [java]     at java.awt.Component.dispatchEvent(Component.java:3477)&#xa;     [java]     at java.awt.EventQueue.dispatchEvent(EventQueue.java:456)&#xa;     [java]     at java.awt.EventDispatchThread.pumpOneEventForHierarchy(EventDispatchThread.java:201)&#xa;     [java]     at java.awt.EventDispatchThread.pumpEventsForHierarchy(EventDispatchThread.java:151)&#xa;     [java]     at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:145)&#xa;     [java]     at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:137)&#xa;     [java]     at java.awt.EventDispatchThread.run(EventDispatchThread.java:100)&#xa;     [java] 25.11.2004 06:20:54 freemind.controller.actions.PrintActionHandler executeAction&#xa;     [java] INFO: &lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;yes&quot;?&gt;&#xa;     [java] &lt;paste_node_action isLeft=&quot;true&quot; asSibling=&quot;false&quot; node=&quot;Freemind_Link_1847988226&quot;&gt;&lt;transferable_content TransferableAsPlainText=&quot;Transactions&#xa;     [java]     A possibility to avoid the transaction close problem is to stop the time between consecutive actions, and undo all that occur in less than 100msec.&#xa;     [java] &quot; Transferable=&quot;&amp;lt;node ID=&amp;quot;Freemind_Link_241899915&amp;quot; COLOR=&amp;quot;#00b439&amp;quot; TEXT=&amp;quot;Transactions&amp;quot; FOLDED=&amp;quot;true&amp;quot;&amp;gt;&#xa;     [java] &amp;lt;font NAME=&amp;quot;SansSerif&amp;quot; SIZE=&amp;quot;16&amp;quot;/&amp;gt;&#xa;     [java] &amp;lt;icon BUILTIN=&amp;quot;button_ok&amp;quot;/&amp;gt;&#xa;     [java] &amp;lt;node ID=&amp;quot;Freemind_Link_559929439&amp;quot; COLOR=&amp;quot;#990000&amp;quot; TEXT=&amp;quot;A possibility to avoid the transaction close problem is to stop the time between consecutive actions, and undo all that occur in less than 100msec. &amp;quot;&amp;gt;&#xa;     [java] &amp;lt;font NAME=&amp;quot;SansSerif&amp;quot; SIZE=&amp;quot;14&amp;quot;/&amp;gt;&#xa;     [java] &amp;lt;/node&amp;gt;&#xa;     [java] &amp;lt;/node&amp;gt;&#xa;     [java] &quot;/&gt;&lt;/paste_node_action&gt;&#xa;&#xa;">
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
<node COLOR="#00b439" ID="Freemind_Link_719829807" TEXT="Fit to page does not work as expected">
<font NAME="SansSerif" SIZE="16"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107382036360" MODIFIED="1114057885325"/>
</hook>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733381" MODIFIED="1114057147044"/>
</hook>
</node>
<node COLOR="#00b439" ID="Freemind_Link_1903090486" TEXT="without a visible map, some actions are not disabled">
<font NAME="SansSerif" SIZE="16"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733404" MODIFIED="1107380733408"/>
</hook>
</node>
<node COLOR="#00b439" ID="Freemind_Link_1958316219" TEXT="When moving nodes up and down with keyboard, the zoom bar blinks">
<font NAME="SansSerif" SIZE="16"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733434" MODIFIED="1107380733439"/>
</hook>
</node>
<node COLOR="#00b439" ID="Freemind_Link_361806090" TEXT="strg+shift+w does not work">
<font NAME="SansSerif" SIZE="16"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733444" MODIFIED="1107380733448"/>
</hook>
</node>
<node COLOR="#00b439" ID="Freemind_Link_1716273918" TEXT="freemind properties directory must be changed">
<font NAME="SansSerif" SIZE="16"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733453" MODIFIED="1107380733459"/>
</hook>
</node>
<node COLOR="#00b439" ID="Freemind_Link_39683791" TEXT="Ctrl+Up wechselt nicht die Seiten, falls der Baum einseitig ist.">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="messagebox_warning"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733464" MODIFIED="1107380733469"/>
</hook>
</node>
<node COLOR="#00b439" FOLDED="true" ID="Freemind_Link_220399055" TEXT="POSTPONED: physical styles cause illeagel edit fields">
<font NAME="SansSerif" SIZE="16"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733473" MODIFIED="1107380733479"/>
</hook>
<node COLOR="#990000" ID="Freemind_Link_788111381" TEXT="Bugs item #1094623, was opened at 2005-01-02 13:37&#xa;Message generated for change (Tracker Item Submitted) made by Item Submitter&#xa;You can respond by visiting: &#xa;https://sourceforge.net/tracker/?func=detail&amp;atid=107118&amp;aid=1094623&amp;group_id=7118&#xa;&#xa;Category: None&#xa;Group: None&#xa;Status: Open&#xa;Resolution: None&#xa;Priority: 5&#xa;Submitted By: John Ralls (jralls)&#xa;Assigned to: Nobody/Anonymous (nobody)&#xa;Summary: 0.8.0B4: Applying Physical Style Breaks Node Editing&#xa;&#xa;Initial Comment:&#xa;After a &quot;physical style&quot; is applied to any node in a map, node editing in &#xa;that map is broken:&#xa;&#xa;Under OSX 3.7 (jre 1.4.2), the node editing itself works normally, but &#xa;the results of the edit are not visible until a redraw of the node is &#xa;forced (by collapsing and uncollapsing it or one of its parent nodes).&#xa;&#xa;Under Linux 2.6.8, (Mandrake 10.0, j2sdk 1.4.2), editing an empty &#xa;node (as with a new node) inline doesn&apos;t show the edit until one is &#xa;finished.  Editing a node which already has content works normally, as &#xa;does the long node edit window.&#xa;&#xa;&#xa;----------------------------------------------------------------------&#xa;&#xa;You can respond by visiting: &#xa;https://sourceforge.net/tracker/?func=detail&amp;atid=107118&amp;aid=1094623&amp;group_id=7118">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" ID="Freemind_Link_1259071995" TEXT="postponed">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" ID="Freemind_Link_365307963" TEXT="error in ApplyPatternAction&#x0;">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" ID="Freemind_Link_1024005742" TEXT="            if (pattern.getNodeStyle() != null) {&#xa;                getModeController().setNodeStyle(node, pattern.getNodeStyle());&#xa;            }&#xa;">
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
</node>
<node COLOR="#0033ff" ID="Freemind_Link_1209892924" POSITION="right" TEXT="Merge">
<font NAME="SansSerif" SIZE="18"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733496" MODIFIED="1107380733501"/>
</hook>
<node COLOR="#00b439" ID="Freemind_Link_1347278642" TEXT="Winlaf?">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="help"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733506" MODIFIED="1107380733511"/>
</hook>
</node>
</node>
<node COLOR="#0033ff" FOLDED="true" ID="Freemind_Link_1851230743" POSITION="left" TEXT="released">
<font NAME="SansSerif" SIZE="18"/>
<icon BUILTIN="button_ok"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107381897274" MODIFIED="1111348989494"/>
</hook>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733484" MODIFIED="1114057885396"/>
</hook>
<node COLOR="#00b439" ID="Freemind_Link_1111601817" TEXT="Alt+Down does not work">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
</node>
<node COLOR="#00b439" FOLDED="true" ID="Freemind_Link_1361039997" TEXT="Move nodes removes the focus">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
<node COLOR="#990000" ID="Freemind_Link_1584702524" TEXT="Bugs item #1064047, was opened at 2004-11-10 14:04&#xa;Message generated for change (Tracker Item Submitted) made by Item Submitter&#xa;You can respond by visiting: &#xa;https://sourceforge.net/tracker/?func=detail&amp;atid=107118&amp;aid=1064047&amp;group_id=7118&#xa;&#xa;Category: None&#xa;Group: None&#xa;Status: Open&#xa;Resolution: None&#xa;Priority: 5&#xa;Submitted By: sampath krishna (xnok)&#xa;Assigned to: Nobody/Anonymous (nobody)&#xa;Summary: CTRL+arrow repositions the map&#xa;&#xa;Initial Comment:&#xa;&#xa;CTRL+UP or CTRL+DOWN arrow keys on a map repositions&#xa;the entire map. This sometimes makes the highlighted&#xa;node go off the screen -- It did no happen with 0.7&#xa;verion, if I recall right.&#xa;&#xa;krishna&#xa;&#xa;&#xa;----------------------------------------------------------------------&#xa;&#xa;You can respond by visiting: &#xa;https://sourceforge.net/tracker/?func=detail&amp;atid=107118&amp;aid=1064047&amp;group_id=7118">
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
<node COLOR="#00b439" ID="Freemind_Link_1389121528" TEXT="ExportBranch is buggy">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
</node>
<node COLOR="#00b439" ID="Freemind_Link_850382226" TEXT="Preferences are gone">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
</node>
<node COLOR="#00b439" FOLDED="true" ID="Freemind_Link_1841441758" TEXT="Menus without functionality">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
<node COLOR="#990000" ID="Freemind_Link_380165435" TEXT="5. There are new menu items that seem nonfunctional at the moment.&#xa;   --Import Linked Branch&#xa;   --Import Without Root&#xa;   --Insert New Parent Node&#xa;   I&apos;m not sure what they are intended to do when they are made to work. Any&#xa;explanation would be helpful.">
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
<node COLOR="#00b439" ID="Freemind_Link_724183816" TEXT="load/save icons">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
</node>
<node COLOR="#00b439" ID="Freemind_Link_1200148592" TEXT="salt for encrypted nodes must be added to xml">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
</node>
<node COLOR="#00b439" ID="Freemind_Link_1252070115" TEXT="wrong password must be reported to the user.">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
</node>
<node COLOR="#00b439" FOLDED="true" ID="Freemind_Link_377306865" TEXT="Menus must be resorted">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
<node COLOR="#990000" ID="Freemind_Link_822620705" TEXT="Show modification times/revisions must be in tools">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" ID="Freemind_Link_1347422316" TEXT="online help must appear first">
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
<node COLOR="#00b439" ID="Freemind_Link_700023061" TEXT="fold children buggy.">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
</node>
<node COLOR="#00b439" ID="Freemind_Link_353038783" TEXT="Documentation under MacOSX">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
</node>
<node COLOR="#00b439" ID="Freemind_Link_1272242967" TEXT="*When I wanted to quit freemind asked me if i want to save --&gt; I pressed cancel and freemind crashed">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="messagebox_warning"/>
<icon BUILTIN="button_ok"/>
</node>
<node COLOR="#00b439" ID="Freemind_Link_1456913466" TEXT="new splash screen">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
</node>
<node COLOR="#00b439" ID="Freemind_Link_1803421450" LINK="http://marktree.sourceforge.net/mm/" TEXT="New html export variant from Miika Nurminen">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
</node>
<node COLOR="#00b439" ID="Freemind_Link_936966927" TEXT="Move nodes from dimitri">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
</node>
<node COLOR="#00b439" ID="Freemind_Link_423078407" TEXT="Packaging under Linux">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
</node>
<node COLOR="#00b439" ID="Freemind_Link_1668966628" TEXT="Encrypt/Decrypt visible">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
</node>
<node COLOR="#00b439" ID="Freemind_Link_1263835594" TEXT="Fold all bug: move some nodes into a folded one, and press &quot;fold all&quot; on them.">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
</node>
<node COLOR="#00b439" ID="Freemind_Link_65235877" TEXT=" Context menu for the map starts with separating line">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107381893114" MODIFIED="1107381893143"/>
</hook>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733413" MODIFIED="1108188934507"/>
</hook>
</node>
<node COLOR="#00b439" ID="Freemind_Link_1058088076" TEXT="Context menu&apos;s labels are not aligned due to icons">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107381893158" MODIFIED="1108188934514"/>
</hook>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733425" MODIFIED="1107381893179"/>
</hook>
</node>
<node COLOR="#00b439" ID="Freemind_Link_1416398819" TEXT="XML">
<font NAME="SansSerif" SIZE="16"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733263" MODIFIED="1107380733270"/>
</hook>
<node COLOR="#990000" ID="Freemind_Link_1785373014" TEXT="Save XML in lower case letters (problem with turkish language)">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="button_ok"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733276" MODIFIED="1109885144056"/>
</hook>
</node>
</node>
<node COLOR="#00b439" FOLDED="true" ID="Freemind_Link_1717087993" TEXT="Turkish">
<font NAME="SansSerif" SIZE="16"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733303" MODIFIED="1107380733308"/>
</hook>
<node COLOR="#990000" ID="Freemind_Link_919111112" TEXT="foltin@linuxii:~/java/freemind/contributors/0_7_1&gt; cvs diff -u -r FM-0-7-1 freemind&#xa;cvs diff: Diffing freemind&#xa;Index: freemind/Resources_it.properties&#xa;===================================================================&#xa;RCS file: /cvsroot/freemind/freemind/Resources_it.properties,v&#xa;retrieving revision 1.3&#xa;retrieving revision 1.4&#xa;diff -u -r1.3 -r1.4&#xa;--- freemind/Resources_it.properties    6 Feb 2004 06:04:24 -0000       1.3&#xa;+++ freemind/Resources_it.properties    29 Feb 2004 15:44:20 -0000      1.4&#xa;@@ -178,8 +178,8 @@&#xa; user_defined_zoom = Definito dall&apos;utente&#xa; user_defined_zoom_status_bar = Impostato il livello di zoom definito dall&apos;utente di {0}%.&#xa; # new from 7.12.2003, fc.&#xa;-selection_method_delayed = Delayed selection&#xa;-selection_method_direct = Direct selection&#xa;+selection_method_delayed = Selezione differita&#xa;+selection_method_direct = Selezione diretta&#xa; # new from 14.12.2003, fc&#xa; FAQ = FAQ&#xa; # new from 20.12.2003, fc&#xa;Index: freemind/build.xml&#xa;===================================================================&#xa;RCS file: /cvsroot/freemind/freemind/build.xml,v&#xa;retrieving revision 1.23&#xa;diff -u -r1.23 build.xml&#xa;--- freemind/build.xml  15 Feb 2004 11:52:43 -0000      1.23&#xa;+++ freemind/build.xml  4 Sep 2004 06:36:41 -0000&#xa;@@ -7,7 +7,7 @@&#xa;        &lt;property name=&quot;post&quot; value=&quot;../post&quot; /&gt;&#xa;        &lt;property name=&quot;debug&quot; value=&quot;on&quot; /&gt;&#xa;        &lt;property name=&quot;build.compiler&quot; value=&quot;modern&quot; /&gt;&#xa;-       &lt;property name=&quot;ver&quot; value=&quot;0_7_1&quot; /&gt;&#xa;+       &lt;property name=&quot;ver&quot; value=&quot;0_7_1_turkish&quot; /&gt;&#xa;&#xa;        &lt;target name=&quot;compile&quot;&gt;&#xa;                &lt;mkdir dir=&quot;${build}&quot;/&gt;&#xa;cvs diff: Diffing freemind/accessories&#xa;cvs diff: Diffing freemind/accessories/plugins&#xa;cvs diff: Diffing freemind/accessories/plugins/dialogs&#xa;cvs diff: Diffing freemind/accessories/plugins/icons&#xa;cvs diff: Diffing freemind/accessories/plugins/lib&#xa;cvs diff: Diffing freemind/accessories/plugins/util&#xa;cvs diff: Diffing freemind/accessories/plugins/util/window&#xa;cvs diff: Diffing freemind/accessories/plugins/util/xslt&#xa;cvs diff: Diffing freemind/admin&#xa;cvs diff: Diffing freemind/admin/docs&#xa;cvs diff: Diffing freemind/doc&#xa;cvs diff: Diffing freemind/doc/maps&#xa;cvs diff: Diffing freemind/doc/pics&#xa;cvs diff: Diffing freemind/freemind&#xa;cvs diff: Diffing freemind/freemind/controller&#xa;cvs diff: Diffing freemind/freemind/controller/actions&#xa;cvs diff: Diffing freemind/freemind/extensions&#xa;cvs diff: Diffing freemind/freemind/main&#xa;Index: freemind/freemind/main/FreeMind.java&#xa;===================================================================&#xa;RCS file: /cvsroot/freemind/freemind/freemind/main/FreeMind.java,v&#xa;retrieving revision 1.32&#xa;diff -u -r1.32 FreeMind.java&#xa;--- freemind/freemind/main/FreeMind.java        2 Feb 2004 21:25:24 -0000       1.32&#xa;+++ freemind/freemind/main/FreeMind.java        4 Sep 2004 06:36:42 -0000&#xa;@@ -60,7 +60,7 @@&#xa;&#xa; public class FreeMind extends JFrame implements FreeMindMain {&#xa;&#xa;-    public static final String version = &quot;0.7.1&quot;;&#xa;+    public static final String version = &quot;0.7.1_turkish&quot;;&#xa;     //    public static final String defaultPropsURL = &quot;freemind.properties&quot;;&#xa;     public URL defaultPropsURL;&#xa;     //    public static Properties defaultProps;&#xa;Index: freemind/freemind/main/XMLElement.java&#xa;===================================================================&#xa;RCS file: /cvsroot/freemind/freemind/freemind/main/XMLElement.java,v&#xa;retrieving revision 1.7&#xa;diff -u -r1.7 XMLElement.java&#xa;--- freemind/freemind/main/XMLElement.java      3 Nov 2003 11:00:10 -0000       1.7&#xa;+++ freemind/freemind/main/XMLElement.java      4 Sep 2004 06:36:48 -0000&#xa;@@ -473,7 +473,7 @@&#xa;                          boolean   ignoreCase)&#xa;     {&#xa;         this.ignoreWhitespace = skipLeadingWhitespace;&#xa;-        this.ignoreCase = ignoreCase;&#xa;+        this.ignoreCase = false /*ignoreCase, fc, bad hack, 20.6.2004*/;&#xa;         this.name = null;&#xa;         this.contents = &quot;&quot;;&#xa;         this.attributes = new Hashtable();&#xa;cvs diff: Diffing freemind/freemind/modes&#xa;Index: freemind/freemind/modes/XMLElementAdapter.java&#xa;===================================================================&#xa;RCS file: /cvsroot/freemind/freemind/freemind/modes/XMLElementAdapter.java,v&#xa;retrieving revision 1.4&#xa;diff -u -r1.4 XMLElementAdapter.java&#xa;--- freemind/freemind/modes/XMLElementAdapter.java      24 Jan 2004 22:36:48 -0000      1.4&#xa;+++ freemind/freemind/modes/XMLElementAdapter.java      4 Sep 2004 06:36:49 -0000&#xa;@@ -89,17 +89,17 @@&#xa;    public void setName(String name)  {&#xa;       super.setName(name);&#xa;       // Create user object based on name&#xa;-      if (name.equals(&quot;node&quot;)) {&#xa;+      if (name.equalsIgnoreCase(&quot;node&quot;)) {&#xa;          userObject = createNodeAdapter(frame); }&#xa;-      if (name.equals(&quot;edge&quot;)) {&#xa;+      if (name.equalsIgnoreCase(&quot;edge&quot;)) {&#xa;          userObject = createEdgeAdapter(null, frame); }&#xa;-      if (name.equals(&quot;cloud&quot;)) {&#xa;+      if (name.equalsIgnoreCase(&quot;cloud&quot;)) {&#xa;           userObject = createCloudAdapter(null, frame); }&#xa;-      if (name.equals(&quot;arrowlink&quot;)) {&#xa;+      if (name.equalsIgnoreCase(&quot;arrowlink&quot;)) {&#xa;           userObject = createArrowLinkAdapter(null, null, frame); }}&#xa;&#xa;    public void addChild(XMLElement child) {&#xa;-      if (getName().equals(&quot;map&quot;)) {&#xa;+      if (getName().equalsIgnoreCase(&quot;map&quot;)) {&#xa;          mapChild = (NodeAdapter)child.getUserObject();&#xa;          return; }&#xa;       if (userObject instanceof NodeAdapter) {&#xa;@@ -123,9 +123,9 @@&#xa;             //System.out.println(&quot;arrowLink=&quot;+arrowLink);&#xa;             ArrowLinkAdapters.add(arrowLink);&#xa;          }&#xa;-         else if (child.getName().equals(&quot;font&quot;)) {&#xa;+         else if (child.getName().equalsIgnoreCase(&quot;font&quot;)) {&#xa;             node.setFont((Font)child.getUserObject()); }&#xa;-         else if (child.getName().equals(&quot;icon&quot;)) {&#xa;+         else if (child.getName().equalsIgnoreCase(&quot;icon&quot;)) {&#xa;              node.addIcon((MindIcon)child.getUserObject()); }}}&#xa;&#xa;    public void setAttribute(String name, Object value) {&#xa;@@ -137,22 +137,22 @@&#xa;       if (userObject instanceof NodeAdapter) {&#xa;          //&#xa;          NodeAdapter node = (NodeAdapter)userObject;&#xa;-         if (name.equals(&quot;TEXT&quot;)) {&#xa;+         if (name.equalsIgnoreCase(&quot;TEXT&quot;)) {&#xa;             node.setUserObject(sValue); }&#xa;-         else if (name.equals(&quot;FOLDED&quot;)) {&#xa;-            if (sValue.equals(&quot;true&quot;)) {&#xa;+         else if (name.equalsIgnoreCase(&quot;FOLDED&quot;)) {&#xa;+            if (sValue.equalsIgnoreCase(&quot;true&quot;)) {&#xa;                node.setFolded(true); }}&#xa;-         else if (name.equals(&quot;POSITION&quot;)) {&#xa;+         else if (name.equalsIgnoreCase(&quot;POSITION&quot;)) {&#xa;              // fc, 17.12.2003: Remove the left/right bug.&#xa;-             node.setLeft(sValue.equals(&quot;left&quot;)); }&#xa;-         else if (name.equals(&quot;COLOR&quot;)) {&#xa;+             node.setLeft(sValue.equalsIgnoreCase(&quot;left&quot;)); }&#xa;+         else if (name.equalsIgnoreCase(&quot;COLOR&quot;)) {&#xa;             if (sValue.length() == 7) {&#xa;                node.setColor(Tools.xmlToColor(sValue)); }}&#xa;-         else if (name.equals(&quot;LINK&quot;)) {&#xa;+         else if (name.equalsIgnoreCase(&quot;LINK&quot;)) {&#xa;             node.setLink(sValue); }&#xa;-         else if (name.equals(&quot;STYLE&quot;)) {&#xa;+         else if (name.equalsIgnoreCase(&quot;STYLE&quot;)) {&#xa;             node.setStyle(sValue); }&#xa;-         else if (name.equals(&quot;ID&quot;)) {&#xa;+         else if (name.equalsIgnoreCase(&quot;ID&quot;)) {&#xa;              // do not set label but annotate in list:&#xa;              //System.out.println(&quot;(sValue, node) = &quot; + sValue + &quot;, &quot;+  node);&#xa;              IDToTarget.put(sValue, node);&#xa;@@ -161,12 +161,12 @@&#xa;&#xa;       if (userObject instanceof EdgeAdapter) {&#xa;          EdgeAdapter edge = (EdgeAdapter)userObject;&#xa;-         if (name.equals(&quot;STYLE&quot;)) {&#xa;+         if (name.equalsIgnoreCase(&quot;STYLE&quot;)) {&#xa;            edge.setStyle(sValue); }&#xa;-         else if (name.equals(&quot;COLOR&quot;)) {&#xa;+         else if (name.equalsIgnoreCase(&quot;COLOR&quot;)) {&#xa;            edge.setColor(Tools.xmlToColor(sValue)); }&#xa;-         else if (name.equals(&quot;WIDTH&quot;)) {&#xa;-            if (sValue.equals(&quot;thin&quot;)) {&#xa;+         else if (name.equalsIgnoreCase(&quot;WIDTH&quot;)) {&#xa;+            if (sValue.equalsIgnoreCase(&quot;thin&quot;)) {&#xa;                edge.setWidth(EdgeAdapter.WIDTH_THIN); }&#xa;             else {&#xa;                edge.setWidth(Integer.parseInt(sValue)); }}&#xa;@@ -174,63 +174,63 @@&#xa;&#xa;       if (userObject instanceof CloudAdapter) {&#xa;          CloudAdapter cloud = (CloudAdapter)userObject;&#xa;-         if (name.equals(&quot;STYLE&quot;)) {&#xa;+         if (name.equalsIgnoreCase(&quot;STYLE&quot;)) {&#xa;            cloud.setStyle(sValue); }&#xa;-         else if (name.equals(&quot;COLOR&quot;)) {&#xa;+         else if (name.equalsIgnoreCase(&quot;COLOR&quot;)) {&#xa;            cloud.setColor(Tools.xmlToColor(sValue)); }&#xa;-         else if (name.equals(&quot;WIDTH&quot;)) {&#xa;+         else if (name.equalsIgnoreCase(&quot;WIDTH&quot;)) {&#xa;                cloud.setWidth(Integer.parseInt(sValue));&#xa;          }&#xa;          return; }&#xa;&#xa;       if (userObject instanceof ArrowLinkAdapter) {&#xa;          ArrowLinkAdapter arrowLink = (ArrowLinkAdapter)userObject;&#xa;-         if (name.equals(&quot;STYLE&quot;)) {&#xa;+         if (name.equalsIgnoreCase(&quot;STYLE&quot;)) {&#xa;              arrowLink.setStyle(sValue); }&#xa;-         else if (name.equals(&quot;COLOR&quot;)) {&#xa;+         else if (name.equalsIgnoreCase(&quot;COLOR&quot;)) {&#xa;              arrowLink.setColor(Tools.xmlToColor(sValue)); }&#xa;-         else if (name.equals(&quot;DESTINATION&quot;)) {&#xa;+         else if (name.equalsIgnoreCase(&quot;DESTINATION&quot;)) {&#xa;              arrowLink.setDestinationLabel(sValue); }&#xa;-         else if (name.equals(&quot;REFERENCETEXT&quot;)) {&#xa;+         else if (name.equalsIgnoreCase(&quot;REFERENCETEXT&quot;)) {&#xa;              arrowLink.setReferenceText((sValue)); }&#xa;-         else if (name.equals(&quot;STARTINCLINATION&quot;)) {&#xa;+         else if (name.equalsIgnoreCase(&quot;STARTINCLINATION&quot;)) {&#xa;              arrowLink.setStartInclination(Tools.xmlToPoint(sValue)); }&#xa;-         else if (name.equals(&quot;ENDINCLINATION&quot;)) {&#xa;+         else if (name.equalsIgnoreCase(&quot;ENDINCLINATION&quot;)) {&#xa;              arrowLink.setEndInclination(Tools.xmlToPoint(sValue)); }&#xa;-         else if (name.equals(&quot;STARTARROW&quot;)) {&#xa;+         else if (name.equalsIgnoreCase(&quot;STARTARROW&quot;)) {&#xa;              arrowLink.setStartArrow(sValue); }&#xa;-         else if (name.equals(&quot;ENDARROW&quot;)) {&#xa;+         else if (name.equalsIgnoreCase(&quot;ENDARROW&quot;)) {&#xa;              arrowLink.setEndArrow(sValue); }&#xa;-         else if (name.equals(&quot;WIDTH&quot;)) {&#xa;+         else if (name.equalsIgnoreCase(&quot;WIDTH&quot;)) {&#xa;              arrowLink.setWidth(Integer.parseInt(sValue));&#xa;          }&#xa;          return; }&#xa;&#xa;-      if (getName().equals(&quot;font&quot;)) {&#xa;-         if (name.equals(&quot;SIZE&quot;)) {&#xa;+      if (getName().equalsIgnoreCase(&quot;font&quot;)) {&#xa;+         if (name.equalsIgnoreCase(&quot;SIZE&quot;)) {&#xa;             fontSize = Integer.parseInt(sValue); }&#xa;-         else if (name.equals(&quot;NAME&quot;)) {&#xa;+         else if (name.equalsIgnoreCase(&quot;NAME&quot;)) {&#xa;             fontName = sValue; }&#xa;&#xa;          // Styling&#xa;-         else if (sValue.equals(&quot;true&quot;)) {&#xa;-            if (name.equals(&quot;BOLD&quot;)) {&#xa;+         else if (sValue.equalsIgnoreCase(&quot;true&quot;)) {&#xa;+            if (name.equalsIgnoreCase(&quot;BOLD&quot;)) {&#xa;                fontStyle+=Font.BOLD; }&#xa;-            else if (name.equals(&quot;ITALIC&quot;)) {&#xa;+            else if (name.equalsIgnoreCase(&quot;ITALIC&quot;)) {&#xa;                fontStyle+=Font.ITALIC; }}}&#xa;       /* icons */&#xa;-      if (getName().equals(&quot;icon&quot;)) {&#xa;-         if (name.equals(&quot;BUILTIN&quot;)) {&#xa;+      if (getName().equalsIgnoreCase(&quot;icon&quot;)) {&#xa;+         if (name.equalsIgnoreCase(&quot;BUILTIN&quot;)) {&#xa;             iconName = sValue; }&#xa;       }&#xa;   }&#xa;&#xa;    protected void completeElement() {&#xa;-      if (getName().equals(&quot;font&quot;)) {&#xa;+      if (getName().equalsIgnoreCase(&quot;font&quot;)) {&#xa;          userObject =  frame.getController().getFontThroughMap&#xa;             (new Font(fontName, fontStyle, fontSize)); }&#xa;       /* icons */&#xa;-            if (getName().equals(&quot;icon&quot;)) {&#xa;+            if (getName().equalsIgnoreCase(&quot;icon&quot;)) {&#xa;          userObject =  new MindIcon(iconName); }&#xa;    }&#xa;&#xa;@@ -258,7 +258,7 @@&#xa;                     continue;&#xa;                 }&#xa;                 newID = registry.getLabel(target);&#xa;-                if( ! newID.equals(oldID) ) {&#xa;+                if( ! newID.equalsIgnoreCase(oldID) ) {&#xa;                     System.err.println(&quot;Servere internal error. Looked for id &quot; + oldID + &quot; but found &quot;+newID + &quot; in the node &quot; + target+&quot;.&quot;);&#xa;                     continue;&#xa;                 }&#xa;cvs diff: Diffing freemind/freemind/modes/actions&#xa;cvs diff: Diffing freemind/freemind/modes/browsemode&#xa;cvs diff: Diffing freemind/freemind/modes/filemode&#xa;cvs diff: Diffing freemind/freemind/modes/mindmapmode&#xa;cvs diff: Diffing freemind/freemind/modes/schememode&#xa;cvs diff: Diffing freemind/freemind/util&#xa;cvs diff: Diffing freemind/freemind/util/window&#xa;cvs diff: Diffing freemind/freemind/util/xslt&#xa;cvs diff: Diffing freemind/freemind/view&#xa;cvs diff: Diffing freemind/freemind/view/mindmapview&#xa;cvs diff: Diffing freemind/html&#xa;cvs diff: Diffing freemind/images&#xa;cvs diff: Diffing freemind/images/icons&#xa;cvs diff: Diffing freemind/lib&#xa;cvs diff: Diffing freemind/lib/ant&#xa;cvs diff: Diffing freemind/lib/ant/lib&#xa;cvs diff: Diffing freemind/windows-launcher&#xa;foltin@linuxii:~/java/freemind/contributors/0_7_1&gt;">
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
<node COLOR="#00b439" ID="Freemind_Link_1038651712" TEXT="Close should appear above">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733201" MODIFIED="1107380733206"/>
</hook>
</node>
<node COLOR="#00b439" ID="Freemind_Link_1132265304" TEXT="Notes must be removed">
<font NAME="SansSerif" SIZE="16"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733050" MODIFIED="1109885251213"/>
</hook>
</node>
<node COLOR="#00b439" ID="Freemind_Link_760733807" TEXT="Internationalization">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733041" MODIFIED="1107380733046"/>
</hook>
</node>
<node COLOR="#00b439" FOLDED="true" ID="Freemind_Link_632136551" TEXT="Export Import">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733017" MODIFIED="1107380733026"/>
</hook>
<node COLOR="#990000" ID="Freemind_Link_408989851" TEXT="SVG">
<font NAME="SansSerif" SIZE="14"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733030" MODIFIED="1107380733037"/>
</hook>
</node>
</node>
<node COLOR="#00b439" ID="Freemind_Link_1761458856" TEXT="Undo Hooks">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733002" MODIFIED="1107380733006"/>
</hook>
</node>
<node COLOR="#00b439" ID="Freemind_Link_677613542" TEXT="Registration Xml Tag needs modes">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380732993" MODIFIED="1107380732997"/>
</hook>
</node>
<node COLOR="#00b439" ID="Freemind_Link_1181720810" TEXT="External plugins -&gt; build file">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="messagebox_warning"/>
<icon BUILTIN="button_ok"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380732982" MODIFIED="1107380732986"/>
</hook>
</node>
<node COLOR="#00b439" ID="Freemind_Link_1037224611" TEXT="3. I think dropping formatting options from the right-click node menu is a&#xa;big, big mistake. To me, one of the best features of the program is the ease&#xa;with which I can point to a node and quickly reformat it. Is there an&#xa;alpha-test group that can be used to get others&apos; reaction to this before&#xa;posting a distribution version?">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733220" MODIFIED="1107380733243"/>
</hook>
</node>
<node COLOR="#00b439" FOLDED="true" ID="Freemind_Link_154963576" TEXT="Russian">
<font NAME="SansSerif" SIZE="16"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733312" MODIFIED="1107380733317"/>
</hook>
<node COLOR="#990000" ID="Freemind_Link_1076629566" TEXT="to Dimitri">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="messagebox_warning"/>
<icon BUILTIN="button_ok"/>
<hook NAME="accessories/plugins/CreationModificationPlugin.properties">
<Parameters CREATED="1107380733322" MODIFIED="1107380733327"/>
</hook>
</node>
</node>
</node>
</node>
</map>
