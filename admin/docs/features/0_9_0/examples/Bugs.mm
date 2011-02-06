<map version="0.9.0_Beta_8">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1210516503846" ID="ID_305875338" MODIFIED="1210593819111" TEXT="0.9.0 Bugs">
<attribute NAME="script1" VALUE="import java.io.*;&#xa;import java.net.*;&#xa;// deactivated.&#xa;return ;&#xa;&#xa;node = c.addNewNode(node, 0, false);&#xa;c.setNodeText(node, &quot;Bugs&quot;);&#xa;&#xa;try {&#xa; &#x9; def urlT = &quot;http://sourceforge.net/tracker/?group_id=7118&amp;atid=107118&quot;;&#xa;&#x9;//def urlT = &quot;http://sourceforge.net/search/index.php?group_id=7118&amp;words=group_artifact_id%3A%28107118%29+AND+status_id%3A%281%29+AND+artifact_group%3A%28%22+FreeMind+0.9.0%22+%22+FreeMind+0.9.0%22%29&amp;type_of_search=artifact&amp;pmode=0&amp;limit=200&quot;;&#xa;        URL url = new URL(urlT);&#xa;        def inp = new BufferedReader(new InputStreamReader(url.openStream()));&#xa;        String str;&#xa; &#x9; def pattern = ~/.*&lt;a href=&quot;(\/tracker\/.*)&quot;.*/;&#xa;       def bugNamePattern = ~ /.*--&gt;(.*)&lt;!--.*/;&#xa;        while ((str = inp.readLine()) != null) {&#xa;            // str is one line of text; readLine() strips the newline character(s)&#xa;&#x9;&#x9; def matcher = pattern.matcher(str);&#xa;&#x9;&#x9;  if(matcher.matches()) {&#xa;&#x9;&#x9;      def bugNameLine = inp.readLine();&#xa;//&#x9;&#x9;&#x9;println str + &quot;\n&quot; + bugNameLine;&#xa;&#x9;&#x9;&#x9;def bugNameMatcher = bugNamePattern.matcher(bugNameLine);&#xa;&#x9;&#x9;&#x9;if (! bugNameMatcher.matches() ) continue;&#xa;&#x9;&#x9;&#x9;def nn = c.addNewNode(node, 0, false);&#xa;&#x9;&#x9;&#x9;c.setNodeText(nn,bugNameMatcher[0][1]);&#xa;&#x9;&#x9;&#x9;c.setLink(nn, &quot;http://sf.net&quot; + matcher[0][1].replaceAll(&quot;&amp;amp;&quot;,&quot;&amp;&quot;));&#xa;&#x9;&#x9;&#x9;}&#xa;        }&#xa;        inp.close();&#xa;    } catch (MalformedURLException e) {&#xa;print &quot;error:&quot;+e;&#xa;    } catch (IOException e) {&#xa;print &quot;error:&quot;+e;&#xa;    }"/>
<attribute NAME="script2" VALUE="import java.io.*;&#xa;import java.net.*;&#xa;&#xa;node = c.addNewNode(node, 0, false);&#xa;c.setNodeText(node, &quot;Bugs&quot;);&#xa;&#xa;try {&#xa; &#x9; //def urlT = &quot;http://sourceforge.net/tracker/?group_id=7118&amp;atid=107118&quot;;&#xa;&#x9;//def urlT = &quot;http://sourceforge.net/search/index.php?group_id=7118&amp;words=group_artifact_id%3A%28107118%29+AND+status_id%3A%281%29+AND+artifact_group%3A%28%22+FreeMind+0.9.0%22+%22+FreeMind+0.9.0%22%29&amp;type_of_search=artifact&amp;pmode=0&amp;limit=200&quot;;&#xa;&#x9;def urlT=&quot;http://sourceforge.net/search/index.php?group_id=7118&amp;search_summary=1&amp;search_details=1&amp;type_of_search=artifact&amp;all_words=&amp;exact_phrase=&amp;some_word=&amp;group_artifact_id%5B%5D=107118&amp;artifact_id=&amp;status_id%5B%5D=1&amp;submitted_by=&amp;assigned_to=&amp;artifact_group%5B%5D=No+Group&amp;artifact_group%5B%5D=+FreeMind+0.8.0&amp;artifact_group%5B%5D=+FreeMind+0.9.0&amp;resolution%5B%5D=None&amp;resolution%5B%5D=Accepted&amp;open_date_start=&amp;open_date_end=&amp;last_update_date_start=&amp;last_update_date_end=&amp;form_submit=Search&amp;limit=400&quot;;&#xa;        URL url = new URL(urlT);&#xa;        def inp = new BufferedReader(new InputStreamReader(url.openStream()));&#xa;        String str;&#xa;&#x9;  def lastPrio =1;&#xa; &#x9; def pattern = ~/.*&lt;a href=&quot;(\/tracker\/index.*)&quot;&gt;(.*)&lt;\/a&gt;.*/;&#xa;      def prioPattern = ~/\s*([1-9])\s*/;&#xa;//       def bugNamePattern = ~ /.*--&gt;(.*)&lt;!--.*/;&#xa;        while ((str = inp.readLine()) != null) {&#xa;&#x9;&#x9;def prioMatcher = prioPattern.matcher(str);&#xa;&#x9;&#x9;if (prioMatcher.matches())&#xa;&#x9;&#x9;&#x9;lastPrio = prioMatcher[0][1];&#xa;            // str is one line of text; readLine() strips the newline character(s)&#xa;&#x9;&#x9; def matcher = pattern.matcher(str);&#xa;&#x9;&#x9;  if(matcher.matches()) {&#xa;&#x9;&#x9;&#x9;println str;&#xa;//&#x9;&#x9;      def bugNameLine = inp.readLine();&#xa;//&#x9;&#x9;&#x9;println str + &quot;\n&quot; + bugNameLine;&#xa;//&#x9;&#x9;&#x9;def bugNameMatcher = bugNamePattern.matcher(bugNameLine);&#xa;//&#x9;&#x9;&#x9;if (! bugNameMatcher.matches() ) continue;&#xa;&#x9;&#x9;&#x9;def nn = c.addNewNode(node, 0, false);&#xa;&#x9;&#x9;&#x9;c.setNodeText(nn,matcher[0][2]);&#xa;&#x9;&#x9;&#x9;c.setLink(nn, &quot;http://sf.net&quot; + matcher[0][1].replaceAll(&quot;&amp;amp;&quot;,&quot;&amp;&quot;));&#xa;&#x9;&#x9;&#x9;if (lastPrio &gt; 0) &#xa;&#x9;&#x9;&#x9;&#x9;c.addIcon(nn,freemind.modes.MindIcon.factory(&quot;full-&quot;+lastPrio));&#xa;&#x9;&#x9;&#x9;}&#xa;        }&#xa;        inp.close();&#xa;    } catch (MalformedURLException e) {&#xa;print &quot;error:&quot;+e;&#xa;    } catch (IOException e) {&#xa;print &quot;error:&quot;+e;&#xa;    }"/>
<node CREATED="1210593848141" ID="ID_802582656" MODIFIED="1210656849310" POSITION="right" TEXT="Bugs">
<node CREATED="1210593851205" ID="ID_97569069" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1716823&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593851209" TEXT="Freemind 0.9.0 Beta 9 printing">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593851193" ID="ID_1270238089" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1776195&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593851197" TEXT="[Hyperlink] [left side] do not respond">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593851181" ID="ID_1820838402" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1751705&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593851186" TEXT="Firefox/IE Tab plgns/Flash or Java exprt/ Can not follow lnk">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593851169" ID="ID_1534993234" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1699869&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593851173" TEXT="FreeMind beta9 doesn&amp;#039;t start">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593851157" ID="ID_63913256" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1690419&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593851161" TEXT="Language property files are suddenly uncomplete">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593851143" ID="ID_1694080286" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1746704&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593851147" TEXT="Note text corrupted with HTML">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593851131" ID="ID_906822876" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1787637&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593851135" TEXT="Exception with hyperlinks click when unsaved map open">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593851119" ID="ID_1852871744" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1765112&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593851123" TEXT="Help -&amp;gt; Documentation freemind.mm ignores properties">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593851107" ID="ID_891459758" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1763676&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593851111" TEXT="&amp;quot;Toggle Left Toolbar&amp;quot; can&amp;#039;t be remembered">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593851089" ID="ID_931506667" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1763076&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593851098" TEXT="Filtering &amp;amp; Node Folding">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593851078" ID="ID_1055000317" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1763073&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593851082" TEXT="Filter Button Opens Note Editor">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593851067" ID="ID_151094942" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1713921&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593851070" TEXT="F8 Key ">
<icon BUILTIN="full-3"/>
</node>
<node CREATED="1210593851056" ID="ID_391452257" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1750976&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593851060" TEXT="Text Color of Nodes">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593851045" ID="ID_1356563285" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1750537&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593851049" TEXT="Adding Notes to a node with children corrupts the map">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593851032" ID="ID_1494687629" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1736983&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593851036" TEXT="IE problems with exported XHTML">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593851018" ID="ID_228661096" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1712025&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593851022" TEXT="FreeMind does not load maps during startup">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593851006" ID="ID_1693764798" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1708982&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593851010" TEXT="attribute_name attributes require specific order">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850994" ID="ID_1878649627" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1705724&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850998" TEXT="F6 doesn&amp;#039;t work to apply &amp;quot;Detail&amp;quot; physical pattern">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850981" ID="ID_1315255577" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1701275&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850985" TEXT="Export of Java Applet doesn&amp;#039;t work">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850969" ID="ID_952035278" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1680130&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850973" TEXT="FreeMind 0.9.0 Beta9 doesn&amp;#039;t work in Windows 2003">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850953" ID="ID_268802590" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1658825&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850957" TEXT="Links to files with names containing 2  blanks fail">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850941" ID="ID_1670584984" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1626702&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850945" TEXT="blinking nodes on beta8">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850930" ID="ID_581063553" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1592280&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850934" TEXT="Encrypted nodes cannot be moved">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850919" ID="ID_153933397" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1947263&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850923" TEXT="Provide an easier way to change a node color">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850907" ID="ID_1330522327" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1945302&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850911" TEXT="encrypted node just looks to be locked">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850892" ID="ID_52189762" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1941615&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850896" TEXT="home directory incorrectly read">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850880" ID="ID_1674185888" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1939771&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850884" TEXT="Some controls are hidden in left toolbar and tool bar">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850868" ID="ID_701343189" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1935369&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850872" TEXT=" I start FreeMind but nothing happens on MacOS X Leopard">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850856" ID="ID_1458349176" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1370318&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850860" TEXT="Printing fails to obey fit to page.">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850844" ID="ID_1229852220" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1927371&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850848" TEXT="Update to new ODT file format">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850829" ID="ID_65531495" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1925297&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850833" TEXT="Exporting and Importing branches">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850817" ID="ID_36506886" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1903262&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850822" TEXT="links to files with names which contain multiple blanks fail">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850805" ID="ID_1511804921" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1883397&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850809" TEXT="Java version 1.6 &amp;amp; crypted branches ">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850781" ID="ID_488826346" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1829120&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850785" TEXT="Problems running on Leopard">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850767" ID="ID_1166364610" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1846761&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850771" TEXT="Slow start with long class path">
<icon BUILTIN="full-7"/>
</node>
<node CREATED="1210593850755" ID="ID_734296809" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1670436&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850759" TEXT="Preferences does not show up">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850744" ID="ID_1193344655" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1650197&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850748" TEXT="After removing reminder, flag and alarm icons remain">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850731" ID="ID_221772866" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1449405&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850735" TEXT="Minorbug while opening">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850720" ID="ID_663541587" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1655646&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850724" TEXT="Sometimes tools menu displays preferences twice">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850705" ID="ID_1611484317" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1655644&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850712" TEXT="Application sometimes freezes">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850694" ID="ID_105435619" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1548536&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850698" TEXT="Notes don&amp;#039;t do automatic line breaks">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850682" ID="ID_605941391" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1367687&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850686" TEXT="Local Links do no work under Mac OS X">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850670" ID="ID_1622401866" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1404733&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850674" TEXT="Apple - + doesn&amp;#039;t work for format --&amp;gt; larger font">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850658" ID="ID_315150758" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1536202&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850662" TEXT="Failure to print from toolbar">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850645" ID="ID_888944384" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1681305&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850650" TEXT="problems with saved files">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850631" ID="ID_47590376" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1655649&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850635" TEXT="Contextual menu should be improved">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850619" ID="ID_1857322690" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1426469&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850623" TEXT="&amp;lt;HTML&amp;gt; not equal &amp;lt;html&amp;gt;">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850607" ID="ID_883632613" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1439249&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850611" TEXT="Multiple Help Maps Opening">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850596" ID="ID_657962840" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1376163&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850600" TEXT="&amp;quot;Note&amp;quot; menuitem invoked with keyboard doesn&amp;#039;t work">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850582" ID="ID_88835042" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1432576&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850587" TEXT="&amp;quot;Add sibling node&amp;quot; does strange effect">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850567" ID="ID_1460942778" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1446670&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850571" TEXT="Freemind hangs at startup if last mindmap file has moved.">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850554" ID="ID_1252664017" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1469795&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850558" TEXT="Window Redraw Error On Scroll">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850543" ID="ID_1826462588" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1418181&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850547" TEXT="arrow keys fozen after paste">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850531" ID="ID_1650175786" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1735888&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850535" TEXT="Unable to remove local hyperlink from node">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850518" ID="ID_1546916037" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1743361&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850523" TEXT="Can&amp;#039;t load any resources on Windows XPPolish on jre 1.6.0_01">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850502" ID="ID_339766570" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1736476&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850506" TEXT="Documentation caused a crash">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850491" ID="ID_1495694822" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1514800&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850495" TEXT="App won&amp;#039;t start but Java process visible in Task Manager">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850477" ID="ID_392688597" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1695456&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850481" TEXT="Ctrl+ and Ctrl- don&amp;#039;t change font size">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850465" ID="ID_1230300176" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1720170&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850469" TEXT="Child windows open only on primary display, invisible">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850436" ID="ID_228067375" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1695723&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850440" TEXT="Locking failed error on save">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850425" ID="ID_112169670" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1671835&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850429" TEXT="Portrait / rotated monitor fast horizontal scroll">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850413" ID="ID_1470777824" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1670437&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850417" TEXT="Menu mnemonics missing">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850401" ID="ID_1386274485" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1663586&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850405" TEXT="Freemind opens last file when opening enbedded mm file">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850389" ID="ID_550307506" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1503472&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850393" TEXT="Error in opening exports as Open Writer Doc">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850373" ID="ID_367989220" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1093873&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850377" TEXT="Dialog shows 9999 pages to be printed under Windows">
<icon BUILTIN="full-3"/>
</node>
<node CREATED="1210593850361" ID="ID_900459029" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1561211&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850365" TEXT="menu display error">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850350" ID="ID_10984290" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1536197&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850354" TEXT="Fail to detect invalid preference setting">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850338" ID="ID_145361228" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1524520&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850342" TEXT="No Refresh when Scrolling on Secondary Display">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850325" ID="ID_464561507" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1520697&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850329" TEXT="Undo parent node insertion">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850311" ID="ID_1470165606" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1480770&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850315" TEXT="Ctrl + Plus does not work but Ctrl + Minus does">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850298" ID="ID_1589590953" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1498010&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850303" TEXT="many large text nodes slow down opening text editor">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850286" ID="ID_1463737102" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1498008&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850290" TEXT="Ctrl+Enter sometimes doesn&amp;#039;t work">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850274" ID="ID_1907230327" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1493619&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850278" TEXT="export to PNG with folded node having link gives error">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850261" ID="ID_819795505" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1446072&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850265" TEXT="Issue related with current work directory">
<icon BUILTIN="full-6"/>
</node>
<node CREATED="1210593850246" ID="ID_79395695" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1438750&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850250" TEXT="FreeMind 0.8.0 drive letter handling issue on OS/2">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850234" ID="ID_588962" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1439469&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850238" TEXT="note font">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850222" ID="ID_442278961" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1447949&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850226" TEXT="Partially translated inteface locale disables help">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850210" ID="ID_37656207" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1419504&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850214" TEXT="no lolly pop when label empty">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850198" ID="ID_192452912" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1395166&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850202" TEXT="Time Management Plugin - Reminder Removal not possible">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850183" ID="ID_600048157" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1431505&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850187" TEXT="Run Minimized property in Windows shortcut doesn&amp;#039;t work">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850170" ID="ID_1051889337" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1467893&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850174" TEXT="Window title">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850159" ID="ID_1923658333" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1387649&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850163" TEXT="Links from collapsed nodes look bad">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850144" ID="ID_1674234597" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1420151&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850151" TEXT="Menus open on false monitor">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850132" ID="ID_720137483" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1390814&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850137" TEXT="PDF Issue">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850115" ID="ID_743183873" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1392661&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850121" TEXT="Alt-tab sometimes leaves Alt &amp;#039;on&amp;#039; in Freemind">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850103" ID="ID_1007176036" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1368727&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850107" TEXT="Error in files exported Open Office Writer Document">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850090" ID="ID_1105586968" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1361879&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850094" TEXT="Links with # in filename (_not_ anchor) do not work">
<icon BUILTIN="full-3"/>
</node>
<node CREATED="1210593850077" ID="ID_1930831689" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1295150&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850082" TEXT="0.8.0 Insert to an encrypted node - strange behaviour">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850064" ID="ID_705343750" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1304793&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850069" TEXT="Problem with icon toolbar set at bottom.">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593850048" ID="ID_1603862247" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1256528&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850052" TEXT="Empty node doesn&amp;#039;t indicate child nodes">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593849989" ID="ID_1395367441" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1242133&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593849993" TEXT="Map history has Time Management title">
<icon BUILTIN="full-3"/>
</node>
<node CREATED="1210593849977" ID="ID_1880925542" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1211409&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593849981" TEXT="Delete an encrypted node, undo, and the password is lost">
<icon BUILTIN="full-4"/>
</node>
<node CREATED="1210593849964" ID="ID_761135262" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1306906&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593849968" TEXT="plug ins not loaded on opening .mm file through explorer ">
<icon BUILTIN="full-6"/>
</node>
<node CREATED="1210593849952" ID="ID_1755003416" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1405101&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593849956" TEXT="Node move looses timestamp">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593849937" ID="ID_54241086" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1248847&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593849941" TEXT="No notification when &amp;#039;saving&amp;#039; read-only files">
<icon BUILTIN="full-7"/>
</node>
<node CREATED="1210593849925" ID="ID_466430433" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1317315&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593849929" TEXT="Duplicated icon selection dialog">
<icon BUILTIN="full-2"/>
</node>
<node CREATED="1210593849913" ID="ID_1738010869" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1433449&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593849917" TEXT="XHTML Export doesn&amp;#039;t close the .html file">
<icon BUILTIN="full-5"/>
</node>
<node CREATED="1210593849894" ID="ID_1737694111" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1852538&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593849904" TEXT="Hyperlink (File Chooser) creates relative path">
<icon BUILTIN="full-4"/>
</node>
<node CREATED="1210593850793" ID="ID_1974956725" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1822099&amp;group_id=7118&amp;atid=107118" MODIFIED="1210694996254" TEXT="bubble / fork style not correctly cascading">
<icon BUILTIN="full-7"/>
</node>
<node CREATED="1210593850448" ID="ID_166644740" LINK="http://sf.net/tracker/index.php?func=detail&amp;aid=1626873&amp;group_id=7118&amp;atid=107118" MODIFIED="1210593850457" TEXT="Display error on multi-head system">
<icon BUILTIN="full-5"/>
</node>
</node>
</node>
</map>
