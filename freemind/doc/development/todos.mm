<map version="0.9.0_Beta_6">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node COLOR="#000000" CREATED="1154896082504" ID="Freemind_Link_366022196" MODIFIED="1155500757582" TEXT="Todos for coming version 0.9.x">
<font NAME="SansSerif" SIZE="20"/>
<hook NAME="accessories/plugins/AutomaticLayout.properties"/>
<node COLOR="#0033ff" CREATED="1154896187932" ID="Freemind_Link_917676449" MODIFIED="1154896964685" POSITION="right" TEXT="HTML">
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1154896193111" ID="Freemind_Link_16073776" MODIFIED="1154896964695" TEXT="Use SimpleHTML and fix most annoying bugs">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="full-1"/>
</node>
<node COLOR="#00b439" CREATED="1154896202219" ID="Freemind_Link_1421475055" MODIFIED="1154896964705" TEXT="Export-Scripts must be fixed">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="full-2"/>
<node COLOR="#990000" CREATED="1154896215298" ID="Freemind_Link_347487038" MODIFIED="1154896964712" TEXT="Started with freemind2html.xsl">
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
<node COLOR="#00b439" CREATED="1154896365380" ID="Freemind_Link_1661079448" MODIFIED="1154896964716" TEXT="Need a script for XHTML to OOo transformation">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="full-2"/>
</node>
<node COLOR="#00b439" CREATED="1154897281267" ID="Freemind_Link_1534541891" MODIFIED="1154897295354" TEXT="Split node is missing in the editor">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="full-4"/>
</node>
<node COLOR="#00b439" CREATED="1154897482262" ID="Freemind_Link_703354147" MODIFIED="1156187889471">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    Menu points &quot;use rich edit&quot; and &quot;use plain edit&quot; should be moved to Format menu Dan: I would prefer to have the menu item for switching between rich text and plain text in the main context menu of a node (Subjective)
  </body>
</html>
</richcontent>
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="full-4"/>
<icon BUILTIN="button_ok"/>
</node>
</node>
<node COLOR="#0033ff" CREATED="1154896909470" ID="Freemind_Link_1346714871" MODIFIED="1155478510125" POSITION="right" TEXT="Java Check at startup">
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1155478510017" ID="Freemind_Link_1233066351" MODIFIED="1155500698643" TEXT="C++-Exe:">
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1154896915723" FOLDED="true" ID="Freemind_Link_1295491813" MODIFIED="1155500701499" TEXT="Use freemind.main.FreeMindStarter.main as new main class">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="button_ok"/>
<node COLOR="#111111" CREATED="1154896939930" ID="Freemind_Link_1729691966" MODIFIED="1155478510099" TEXT="Ensures that the java version is checked at first">
<richcontent TYPE="NOTE"><html>
  <head>

  </head>
  <body>
    <p align="left">
      The FreeMind class can't do this as it uses the logging API of Java1.4. 
      Java1.3 fails before any check in FreeMind can be performed. Thus, the 
      checks were moved to the new class FreeMindStarter.
    </p>
  </body>
</html>
</richcontent>
<font NAME="SansSerif" SIZE="12"/>
</node>
</node>
<node COLOR="#990000" CREATED="1155500706972" ID="Freemind_Link_674545358" MODIFIED="1155500754159" TEXT="Check, whether or not java is recognized.">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="full-2"/>
</node>
</node>
<node COLOR="#00b439" CREATED="1155478623965" ID="Freemind_Link_1440615425" MODIFIED="1155909761244" TEXT="Consider how to fix the problem with searching the complete class path for plug-ins, leading to slow start-ups (Prio middle)">
<arrowlink DESTINATION="Freemind_Link_550816658" ENDARROW="Default" ENDINCLINATION="524;0;" ID="Freemind_Arrow_Link_1550539726" STARTARROW="None" STARTINCLINATION="524;0;"/>
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="full-3"/>
<icon BUILTIN="button_ok"/>
</node>
</node>
<node COLOR="#0033ff" CREATED="1154896599093" ID="Freemind_Link_1998038587" MODIFIED="1154896964726" POSITION="right" TEXT="Notes">
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1154896564531" ID="Freemind_Link_868777102" MODIFIED="1156187871126" TEXT="Need icon for nodes with notes">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="full-3"/>
<icon BUILTIN="button_ok"/>
</node>
<node COLOR="#00b439" CREATED="1154896584602" ID="Freemind_Link_32386694" MODIFIED="1156187876151" TEXT="This should be configurable (switch on/off) via Preferences">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="full-7"/>
</node>
<node COLOR="#00b439" CREATED="1154896108780" ID="_" MODIFIED="1154897550909" TEXT="Shortcut for switch between notes and nodes">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="full-3"/>
</node>
</node>
<node COLOR="#0033ff" CREATED="1154896180148" ID="Freemind_Link_1350749537" MODIFIED="1154896964659" POSITION="right" TEXT="New Search">
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1154896125530" ID="Freemind_Link_1068027871" MODIFIED="1154896964668" TEXT="Shortcut for new Search functionality like ctrl-shift-F">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="full-4"/>
</node>
<node COLOR="#00b439" CREATED="1154896149217" ID="Freemind_Link_728689599" MODIFIED="1154896964672" TEXT="Activate Replace in new Search functionality">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="idea"/>
</node>
<node COLOR="#00b439" CREATED="1154896159445" ID="Freemind_Link_738550042" MODIFIED="1154896964676" TEXT="Move time management plugin to integrated plugins">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="full-4"/>
</node>
</node>
<node COLOR="#0033ff" CREATED="1155478643777" ID="Freemind_Link_696990453" MODIFIED="1155478646348" POSITION="right" TEXT="Deleting">
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1155478647713" ID="Freemind_Link_1097171716" MODIFIED="1155478650516" TEXT="Consider adding an option to ask user before deleting a node or a branch (Prio middle)">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="full-3"/>
</node>
</node>
<node COLOR="#0033ff" CREATED="1155478659376" ID="Freemind_Link_1479474730" MODIFIED="1155478665325" POSITION="right" TEXT="Conversion/File Format">
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1155478667584" ID="Freemind_Link_1227008016" MODIFIED="1155478673248" TEXT="Consider letting user know by means of popup when converting from old map format into new map format (Prio middle)">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="full-3"/>
</node>
</node>
<node COLOR="#0033ff" CREATED="1154897384463" ID="Freemind_Link_1463824321" MODIFIED="1154897386408" POSITION="right" TEXT="build">
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1154897387078" ID="Freemind_Link_1295952002" MODIFIED="1154897405866" TEXT="Exclude this map from sources before packaging">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="full-7"/>
</node>
</node>
<node COLOR="#0033ff" CREATED="1154896413253" ID="Freemind_Link_1766300173" MODIFIED="1154896964771" POSITION="right" TEXT="Bugs">
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1154896415905" ID="Freemind_Link_1452279670" MODIFIED="1154898240798" TEXT="Change Cloud Color is not undoable">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
</node>
<node COLOR="#00b439" CREATED="1154898259668" ID="Freemind_Link_550816658" MODIFIED="1155909754394" TEXT="External Plugins are not loaded under Mac">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="full-2"/>
<icon BUILTIN="button_ok"/>
</node>
<node COLOR="#00b439" CREATED="1156102965918" ID="Freemind_Link_1537014547" MODIFIED="1156103004974" TEXT="The display should not change the scroll direction if not dragging a node.">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="full-3"/>
<icon BUILTIN="button_ok"/>
</node>
</node>
<node COLOR="#0033ff" CREATED="1154897688737" ID="Freemind_Link_475477860" MODIFIED="1154897691455" POSITION="right" TEXT="Documentation">
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1154897691881" ID="Freemind_Link_151544330" MODIFIED="1154897716495">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    Update documentation.mm and help plugin
  </body>
</html>
</richcontent>
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="full-4"/>
</node>
</node>
<node COLOR="#0033ff" CREATED="1154897203879" ID="Freemind_Link_39209766" MODIFIED="1154897254028" POSITION="right" TEXT="Missing merges">
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1154897219366" ID="Freemind_Link_935306843" MODIFIED="1155501094472" TEXT="juanpedro__rectangulars edges and rectangular clouds_freemind">
<font NAME="SansSerif" SIZE="16"/>
</node>
<node COLOR="#00b439" CREATED="1154897244833" ID="Freemind_Link_1890112641" MODIFIED="1155501097119" TEXT="1499796  XHTML export with notes attributes xhtml_xslt">
<font NAME="SansSerif" SIZE="16"/>
</node>
<node COLOR="#00b439" CREATED="1154897267841" ID="Freemind_Link_1262676997" MODIFIED="1156107772642">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    1532279 link between nodes on different freemind maps__freemind-0.8.0-gentoo-001-linkmaps
  </body>
</html>
</richcontent>
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
</node>
<node COLOR="#00b439" CREATED="1155478682863" ID="Freemind_Link_337228399" MODIFIED="1155478688431" TEXT="Consider merging the patch for the support of UTF-8 (Prio low)">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="full-6"/>
</node>
</node>
<node COLOR="#0033ff" CREATED="1154896282663" ID="Freemind_Link_962481393" MODIFIED="1154896964783" POSITION="left" TEXT="Legend">
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1154896288287" ID="Freemind_Link_1817730157" MODIFIED="1154896964788" TEXT="Must be fixed inmediately (even before an alpha version)">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="clanbomber"/>
</node>
<node COLOR="#00b439" CREATED="1154896308810" ID="Freemind_Link_2879835" MODIFIED="1154896964796" TEXT="Must be fixed before beta">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_cancel"/>
</node>
<node COLOR="#00b439" CREATED="1154896317602" ID="Freemind_Link_1503154831" MODIFIED="1154896964802" TEXT="Priorities">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="full-1"/>
<icon BUILTIN="full-2"/>
<icon BUILTIN="full-3"/>
<icon BUILTIN="full-4"/>
<icon BUILTIN="full-5"/>
<icon BUILTIN="full-6"/>
<icon BUILTIN="full-7"/>
</node>
<node COLOR="#00b439" CREATED="1154896329157" ID="Freemind_Link_496292861" MODIFIED="1154896964808" TEXT="Should be fixed">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="smily_bad"/>
</node>
<node COLOR="#00b439" CREATED="1154896335357" ID="Freemind_Link_529615894" MODIFIED="1154896964815" TEXT="Nice to have">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="idea"/>
</node>
<node COLOR="#00b439" CREATED="1154898245541" ID="Freemind_Link_392275545" MODIFIED="1154898251910" TEXT="Corrected/Done.">
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
</node>
</node>
<node COLOR="#0033ff" CREATED="1154896660715" ID="Freemind_Link_739426068" MODIFIED="1154897350272" POSITION="left" TEXT="Tests">
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1154897350221" ID="Freemind_Link_1646519917" MODIFIED="1154897352139" TEXT="App">
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1154896651467" ID="Freemind_Link_920412098" MODIFIED="1154897350251" TEXT="How about word wrap in the HTML editor?">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1154897020663" ID="Freemind_Link_220005720" MODIFIED="1154897350263" TEXT="Start with java1.3 must not succeed">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1154897587254" ID="Freemind_Link_439430897" MODIFIED="1154897599545" TEXT="What do the buttons &quot;bold&quot; and &quot;italic&quot; on HTML nodes?">
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
<node COLOR="#00b439" CREATED="1154897347213" ID="Freemind_Link_1327109603" MODIFIED="1154897348593" TEXT="Code">
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1154897321981" ID="Freemind_Link_644235764" MODIFIED="1154897347233" TEXT="Check, that every catch(Exception e) uses the new logging facility call">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="messagebox_warning"/>
</node>
</node>
</node>
</node>
</map>
