/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2001  Joerg Mueller <joergmueller@bigfoot.com>
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
/*$Id: MindMapMapModel.java,v 1.19 2003-11-03 10:15:46 sviles Exp $*/

package freemind.modes.mindmapmode;

import freemind.main.FreeMindMain;
import freemind.controller.MindMapNodesSelection;
import freemind.main.XMLParseException;
import freemind.main.XMLElement;
import freemind.main.Tools;
import freemind.modes.MapAdapter;
import freemind.modes.NodeAdapter;
import freemind.modes.MindMapNode;

import java.awt.Color;
import java.awt.Font;
import java.awt.Cursor;
import java.awt.datatransfer.*;

import java.io.*;

import java.util.*;
import java.util.regex.Pattern;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;

import javax.swing.JOptionPane;


public class MindMapMapModel extends MapAdapter {

    //
    // Constructors
    //

    public MindMapMapModel(FreeMindMain frame) {
        super(frame);
        setRoot(new MindMapNodeModel( getFrame().getResources().getString("new_mindmap"), getFrame()));
    }
    
    public MindMapMapModel( MindMapNodeModel root, FreeMindMain frame ) {
        super(frame);
        setRoot(root);
    }

    public String getRestoreable() {
        if (getFile()==null) {
            return null;
        } else {
            return "MindMap:"+getFile().getAbsolutePath();
        }
    }

    //
    // Methods for editing of the Nodes
    //
    
    public void setNodeColor(MindMapNodeModel node, Color color) {
        node.setColor(color);
        nodeChanged(node);
    }

    public void setBranchFont(MindMapNodeModel node, Font f) {
        setNodeFont(node,f);
        for(int i=0;i<node.getChildCount();i++) {
            setBranchFont((MindMapNodeModel)node.getChildAt(i),f);
        }
        nodeChanged(node);
    }


    public void setBranchColor(MindMapNodeModel node, Color color) {
        setNodeColor(node,color);
        for(int i=0;i<node.getChildCount();i++) {
            setBranchColor((MindMapNodeModel)node.getChildAt(i),color);
        }
        nodeChanged(node);
    }

    public void setBranchBold(MindMapNodeModel node) {
        Font f = node.getFont();
        if(!f.isBold()) {
            // make bold
            node.setFont(f.deriveFont(f.getStyle()+Font.BOLD));
        }

        for(int i=0;i<node.getChildCount();i++) {
            setBranchBold((MindMapNodeModel)node.getChildAt(i));
        }
        nodeChanged(node);
    }

    public void setBranchNonBold(MindMapNodeModel node) {
        Font f = node.getFont();
        if(f.isBold()) {
            // make normal
            node.setFont(f.deriveFont(f.getStyle()-Font.BOLD));
        }

        for(int i=0;i<node.getChildCount();i++) {
            setBranchNonBold((MindMapNodeModel)node.getChildAt(i));
        }
        nodeChanged(node);
    }

    public void setBranchToggleBold(MindMapNodeModel node) {
        Font f = node.getFont();
        if(f.isBold()) {
            // make normal
            node.setFont(f.deriveFont(f.getStyle()-Font.BOLD));
        } else {
            node.setFont(f.deriveFont(f.getStyle()+Font.BOLD));
        }

        for(int i=0;i<node.getChildCount();i++) {
            setBranchToggleBold((MindMapNodeModel)node.getChildAt(i));
        }
        nodeChanged(node);
    }


    public void setBranchItalic(MindMapNodeModel node) {
        Font f = node.getFont();
        if(!f.isItalic()) {
            // make italic
            node.setFont(f.deriveFont(f.getStyle()+Font.ITALIC));
        }

        for(int i=0;i<node.getChildCount();i++) {
            setBranchItalic((MindMapNodeModel)node.getChildAt(i));
        }
        nodeChanged(node);
    }

    public void setBranchNonItalic(MindMapNodeModel node) {
        Font f = node.getFont();
        if(f.isItalic()) {
            // make normal
            node.setFont(f.deriveFont(f.getStyle()-Font.ITALIC));
        }

        for(int i=0;i<node.getChildCount();i++) {
            setBranchNonItalic((MindMapNodeModel)node.getChildAt(i));
        }
        nodeChanged(node);
    }

    public void setBranchToggleItalic(MindMapNodeModel node) {
        Font f = node.getFont();
        if(f.isItalic()) {
            // make normal
            node.setFont(f.deriveFont(f.getStyle()-Font.ITALIC));
        } else {
            node.setFont(f.deriveFont(f.getStyle()+Font.ITALIC));
        }

        for(int i=0;i<node.getChildCount();i++) {
            setBranchToggleItalic((MindMapNodeModel)node.getChildAt(i));
        }
        nodeChanged(node);
    }




    public void setNodeFont(MindMapNodeModel node, Font f) {
        node.setFont(f);
//      node.setFontSize(f.getSize());
//      node.setFont(f.getFontName());
        // FIXME: the implementation should be changed to only use java.awt.Font
        // node.setStyle(f.getStyle());
        nodeChanged(node);
    }

    public void setEdgeColor(MindMapNodeModel node, Color color) {
        ((MindMapEdgeModel)node.getEdge()).setColor(color);
        nodeChanged(node);
    }

    public void setEdgeWidth(MindMapNodeModel node, int width) {
        ((MindMapEdgeModel)node.getEdge()).setWidth(width);
        nodeChanged(node);
    }

    public void setNodeStyle(MindMapNodeModel node, String style) {
        node.setStyle(style);
        nodeStructureChanged(node);
    }

    public void setEdgeStyle(MindMapNodeModel node, String style) {
        MindMapEdgeModel edge = (MindMapEdgeModel)node.getEdge();
        edge.setStyle(style);
        nodeStructureChanged(node);
    }

    public void setBold(MindMapNodeModel node) {
        if (node.isBold()) {
            node.setBold(false);
        } else {
            node.setBold(true);
        }
        nodeChanged(node);
    }

    public void setItalic(MindMapNodeModel node) {
        if (node.isItalic()) {
            node.setItalic(false);
        } else {
            node.setItalic(true);
        }
        nodeChanged(node);
    }

    public void setUnderlined(MindMapNodeModel node) {
        if (node.isUnderlined()) {
            node.setUnderlined(false);
        } else {
            node.setUnderlined(true);
        }
        nodeChanged(node);
    }

    public void setNormalFont(MindMapNodeModel node) {
        node.setItalic(false);
        node.setBold(false);
        node.setUnderlined(false);
        nodeChanged(node);
    }

    public void setFontSize(MindMapNodeModel node, int fontSize) {
        // ** change the font size
        node.setFont(node.getFont().deriveFont((float)fontSize));
        nodeStructureChanged(node);
    }

    public void increaseFontSize(MindMapNodeModel node, int increment) {
       // This is not called !!!!
        Font f = node.getFont();
        float newSize = f.getSize()+increment;
        node.setFont(f.deriveFont(newSize));
        nodeStructureChanged(node);
    }

    public void setFont(MindMapNodeModel node, String font) {
        node.setFont(new Font(font,node.getFont().getStyle(),node.getFont().getSize()));
        nodeStructureChanged(node);
    }

    public void setBranchFontSize(MindMapNodeModel node, int fontSize) {
        // ** change the font size
        node.setFont(node.getFont().deriveFont((float)fontSize));

        for(int i=0;i<node.getChildCount();i++) {
            setBranchFontSize((MindMapNodeModel)node.getChildAt(i),fontSize);
        }

        nodeStructureChanged(node);
    }

    public void increaseBranchFontSize(MindMapNodeModel node, int increment) {
        Font f = node.getFont();
        float newSize = f.getSize()+increment;

        node.setFont(f.deriveFont(newSize));

        for(int i=0;i<node.getChildCount();i++) {
            increaseBranchFontSize((MindMapNodeModel)node.getChildAt(i),increment);
        }

        nodeStructureChanged(node);
    }

    //
    // Other methods
    //
    public String toString() {
        if (getFile() == null) {
            return null;
        } else {
            return getFile().getName();
        }
    }

   public boolean saveHTML(MindMapNodeModel rootNodeOfBranch, File file) { 
        // When isRoot is true, rootNodeOfBranch will be exported as folded regardless his isFolded state in the mindmap
        try {
            // We do all the HTML saving using just ordinary output.

            //Generating output Stream
            BufferedWriter fileout = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(file) ) );

            //            fileout.write( stringOut.toString() ); *///Spit out DOM as a String  */
            String el = System.getProperty("line.separator");
            fileout.write(
"<html>"+el+
"<head>"+el+
"<style type=\"text/css\">"+el+
"    span.foldopened { color: black; font-size: 12px; border-style: dotted;"+el+
"    border-width: 1; font-family: monospace; padding: 0em 0.25em 0em 0.25em; background: #e0e0e0;"+el+
"    VISIBILITY: visible;"+el+
"    cursor:hand; }"+el+
""+el+
""+el+
"    span.foldclosed { color: black; font-size: 12px; border-style: solid;"+el+
"    border-width: 1; font-family: monospace; padding: 0em 0.25em 0em 0.25em; background: #e0e0e0;"+el+
"    VISIBILITY: hidden;"+el+
"    cursor:hand; }"+el+
""+el+
"    span.foldspecial { color: black; font-size: 12px; border-style: none solid solid none;"+el+
"    border-width: 1; font-family: Arial, sans-serif; padding: 0em 0.1em 0em 0.1em; background: #e0e0e0;"+el+
"    cursor:hand; }"+el+
""+el+
" }"+el+
"</style>"+el+
"<!-- ^ Position is not set to relative / absolute here because of Mozilla -->"+el+
"</head>"+el+
"<body>"+el+
""+el+
"<script language=\"JavaScript\">"+el+
"   // Here we implement folding. It works fine with MSIE5.5, MSIE6.0 and"+el+
"   // Mozilla 0.9.6."+el+
""+el+
"   if (document.layers) {"+el+
"      //Netscape 4 specific code"+el+
"      pre = 'document.';"+el+
"      post = ''; }"+el+
"   if (document.getElementById) {"+el+
"      //Netscape 6 specific code"+el+
"      pre = 'document.getElementById(\"';"+el+
"      post = '\").style'; }"+el+
"   if (document.all) {"+el+
"      //IE4+ specific code"+el+
"      pre = 'document.all.';"+el+
"      post = '.style'; }"+el+
""+el+
"function layer_exists(layer) {"+el+
"   try {"+el+
"      eval(pre + layer + post);"+el+
"      return true; }"+el+
"   catch (error) {"+el+
"      return false; }}"+el+
""+el+
"function show_layer(layer) {"+el+
"   eval(pre + layer + post).position = 'relative'; "+el+
"   eval(pre + layer + post).visibility = 'visible'; }"+el+
""+el+
"function hide_layer(layer) {"+el+
"   eval(pre + layer + post).visibility = 'hidden';"+el+
"   eval(pre + layer + post).position = 'absolute'; }"+el+
""+el+
"function hide_folder(folder) {"+el+
"    hide_folding_layer(folder)"+el+
"    show_layer('show'+folder);"+el+
""+el+
"    scrollBy(0,0); // This is a work around to make it work in Browsers (Explorer, Mozilla)"+el+
"}"+el+
""+el+
"function show_folder(folder) {"+el+
"    // Precondition: all subfolders are folded"+el+
""+el+
"    show_layer('hide'+folder);"+el+
"    hide_layer('show'+folder);"+el+
"    show_layer('fold'+folder);"+el+
""+el+
"    scrollBy(0,0); // This is a work around to make it work in Browsers (Explorer, Mozilla)"+el+
""+el+
"    var i;"+el+
"    for (i=1; layer_exists('fold'+folder+'_'+i); ++i) {"+el+
"       show_layer('show'+folder+'_'+i); }"+el+
"}"+el+
""+
"function show_folder_completely(folder) {"+el+
"    // Precondition: all subfolders are folded"+el+
""+el+
"    show_layer('hide'+folder);"+el+
"    hide_layer('show'+folder);"+el+
"    show_layer('fold'+folder);"+el+
""+el+
"    scrollBy(0,0); // This is a work around to make it work in Browsers (Explorer, Mozilla)"+el+
""+el+
"    var i;"+el+
"    for (i=1; layer_exists('fold'+folder+'_'+i); ++i) {"+el+
"       show_folder_completely(folder+'_'+i); }"+el+
"}"+el+
""+el+
""+el+
""+el+
"function hide_folding_layer(folder) {"+el+
"   var i;"+el+
"   for (i=1; layer_exists('fold'+folder+'_'+i); ++i) {"+el+
"       hide_folding_layer(folder+'_'+i); }"+el+
""+el+
"   hide_layer('hide'+folder);"+el+
"   hide_layer('show'+folder);"+el+
"   hide_layer('fold'+folder);"+el+
""+el+
"   scrollBy(0,0); // This is a work around to make it work in Browsers (Explorer, Mozilla)"+el+
"}"+el+
""+el+
"function fold_document() {"+el+
"   var i;"+el+
"   var folder = '1';"+el+
"   for (i=1; layer_exists('fold'+folder+'_'+i); ++i) {"+el+
"       hide_folder(folder+'_'+i); }"+el+
"}"+el+
""+el+
"function unfold_document() {"+el+
"   var i;"+el+
"   var folder = '1';"+el+
"   for (i=1; layer_exists('fold'+folder+'_'+i); ++i) {"+el+
"       show_folder_completely(folder+'_'+i); }"+el+
"}"+el+
""+el+
"</script>"+el);

            if (rootNodeOfBranch.hasFoldedStrictDescendant()) {
               fileout.write("<SPAN class=foldspecial onclick=\"fold_document()\">Hide all</SPAN>"+el);
               fileout.write("<SPAN class=foldspecial onclick=\"unfold_document()\">Show all</SPAN>"+el); }

            fileout.write("<ul>");

            rootNodeOfBranch.saveHTML(fileout,"1",0,/*isRoot=*/true);

            fileout.write("</ul>");

            fileout.write("<SCRIPT language=JavaScript>"+el);
            fileout.write("fold_document();"+el);
            fileout.write("</SCRIPT>"+el);
            fileout.write("</body>"+el);
            fileout.write("</html>"+el);
            fileout.close();
            return true;

        } catch(Exception e) {
            System.err.println("Error in MindMapMapModel.saveHTML(): ");
            e.printStackTrace();
            return false;
        }
    }

    public String getAsPlainText(List mindMapNodes) {
        // Returns success of the operation.
        try {
            StringWriter stringWriter = new StringWriter();
            BufferedWriter fileout = new BufferedWriter(stringWriter);

            for(ListIterator it=mindMapNodes.listIterator();it.hasNext();) {
               ((MindMapNodeModel)it.next()).saveTXT(fileout,/*depth=*/0); }

            fileout.close();
            return stringWriter.toString();

        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

   public boolean saveTXT(MindMapNodeModel rootNodeOfBranch, File file) { 
        // Returns success of the operation.
        try {
            BufferedWriter fileout = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(file) ) );
            rootNodeOfBranch.saveTXT(fileout,/*depth=*/0);
            fileout.close();
            return true;

        } catch(Exception e) {
            System.err.println("Error in MindMapMapModel.saveTXT(): ");
            e.printStackTrace();
            return false;
        }
    }

    public String getAsRTF(List mindMapNodes) {
        // Returns success of the operation.
        try {
            StringWriter stringWriter = new StringWriter();
            BufferedWriter fileout = new BufferedWriter(stringWriter);
            saveRTF(mindMapNodes, fileout);
            fileout.close();
            //System.out.println(stringWriter.toString());

            /*
return "{\\rtf1\\ansi\\ansicpg1252\\deff0\\deflang1033{\\fonttbl{\\f0\\fswiss\\fcharset0 Arial;}}"+
"\\viewkind4\\uc1\\pard\\b\\f0\\fs18 Test\\b0\\fs20\\par"+
"\\pard\\li400\\fs18\\u283?\\'9a\\u269?\\u345?\\'9e\\'fd\\'e1\\'ed\\'e9\\fs20\\par"+
"\\fs18\\'f6\\'fc\\'e4\\'d6\\'dc\\'c4\\'df\\fs20\\par"+
"\\pard\\par"+
   "}";

return "{\\rtf1\\ansi\\ansicpg1252\\deff0\\deflang1033{\\fonttbl{\\f0\\fswiss\\fcharset0 Arial;}{"+
"\\colortbl;\\red0\\green0\\blue255;}}\\viewkind4\\uc1\\pard\\f0\\fs20\\ud{}\\li0{}{\\li0\\b \\fs18{}Test}\\par"+
"\\li400{}{\\li400\\fs18{}\\u283?\\u353?\\u269?\\u345?\\u382?\\u253?\\u225?\\u237?\\u233?}\\par"+
"\\li400{}{\\li400\\fs18{}\\u246?\\u252?\\u228?\\u214?\\u220?\\u196?\\u223?}\\par"+
   "}";

            */


            //            return JOptionPane.showInputDialog ("RTF", "");      

            return stringWriter.toString();
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

   public boolean saveRTF(List mindMapNodes, BufferedWriter fileout) { 
        // Returns success of the operation.
        try {

           // First collect all used colors
           HashSet colors = new HashSet();
           for(ListIterator it=mindMapNodes.listIterator();it.hasNext();) {
              ((MindMapNodeModel)it.next()).collectColors(colors); }

           // Prepare table of colors containing indices to color table
           String colorTableString="{\\colortbl;\\red0\\green0\\blue255;";   // 0 - Automatic, 1 - blue for links
           HashMap colorTable = new HashMap();
           int colorPosition = 2;
           for(Iterator it=colors.iterator();it.hasNext();++colorPosition) {
              Color color = (Color)it.next();
              colorTableString += "\\red"+color.getRed()+"\\green"+color.getGreen()+"\\blue"+color.getBlue()+";";
              colorTable.put(color,new Integer(colorPosition)); }
           colorTableString += "}";

            fileout.write
               ("{\\rtf1\\ansi\\ansicpg1252\\deff0\\deflang1033{\\fonttbl{\\f0\\fswiss\\fcharset0 Arial;}"+
                colorTableString+
                "}"+
                "\\viewkind4\\uc1\\pard\\f0\\fs20{}");
            // ^ If \\ud is appended here, Unicode does not work in MS Word.

            for(ListIterator it=mindMapNodes.listIterator();it.hasNext();) {
               ((MindMapNodeModel)it.next()).saveRTF(fileout,/*depth=*/0,colorTable); }

            fileout.write("}");
            return true; }
        catch(Exception e) {
            e.printStackTrace();
            return false; }}

    public void save(File file) {
        try {
            setFile(file);
            setSaved(true);


            /*   CODE FOR XERCES (DOM)
                 Document doc = new DocumentImpl();
            Element map = doc.createElement("map");
            doc.appendChild(map);
            ( (MindMapNodeModel)getRoot() ).save(doc,map);
            String encoding = FreeMind.userProps.getProperty("mindmap_encoding");
            
            OutputFormat format = new OutputFormat(doc, encoding, false);//Serialize Document
            StringWriter  stringOut = new StringWriter();        //Writer will be a String
            XMLSerializer    serial = new XMLSerializer( stringOut, format );
            serial.asDOMSerializer();                            // As a DOM Serializer

            serial.serialize( doc.getDocumentElement() );
            */

            //CODE FOR NANOXML
            XMLElement map = new XMLElement();
            map.setTagName("map");
            map.addChild(((MindMapNodeModel)getRoot()).save());



            //Generating output Stream
            BufferedWriter fileout = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(file) ) );

            //            fileout.write( stringOut.toString() ); *///Spit out DOM as a String  */
            
            map.write(fileout);

            fileout.close();

        } catch(Exception e) {
            System.err.println("Error in MindMapMapModel.save(): ");
            e.printStackTrace();
        }
    }
    
    public void load(File file) throws FileNotFoundException, IOException, XMLParseException {
       MindMapNodeModel root = loadTree(file);
       if (root != null) {
          setRoot(root); }
       setFile(file);
       setSaved(true); 
    }

    MindMapNodeModel loadTree(File file) throws XMLParseException, IOException {
        MindMapNodeModel root = null;

	XMLElement parser = new XMLElement();
	try {
            parser.parseFromReader(new BufferedReader(new FileReader(file)));
	} catch (Exception ex) {
	    System.err.println("Error while parsing file:"+ex);
	    return null;
	}

	XMLElement rootElement = (XMLElement)parser.getChildren().firstElement();
	root = new MindMapNodeModel(getFrame());
	root.load(rootElement);

	return root;
    }

    //
    // cut'n'paste
    //
    public Transferable cut(MindMapNode node) {
       Transferable transfer = copy(node);
       super.cut(node);
       return transfer;
    }

    public Transferable copy(MindMapNode node) {
                XMLElement element = ((MindMapNodeModel)node).save();
                StringSelection text = new StringSelection(element.toString());
                return text;
    }

   //public void find() {
   //   Tools.errorMessage("find");
   //   MindMapNode selectedNode = getFrame().getView().getSelected().getModel();
   //   
   //}

   public void joinNodes() {
      MindMapNode selectedNode = getFrame().getView().getSelected().getModel();
      ArrayList selectedNodes = getFrame().getView().getSelectedNodesSortedByY();
      String newContent = "";
      boolean firstLoop = true;

      // Make sure the selected node do not have children
      for(Iterator it = selectedNodes.iterator();it.hasNext();) {
         if (((MindMapNode)it.next()).hasChildren()) {
            Tools.errorMessage(getText("cannot_join_nodes_with_children"));
            return; }}

      // Join
      for(Iterator it = selectedNodes.iterator();it.hasNext();) {
         if (firstLoop) {
            firstLoop = false; }
         else {
            newContent += " "; }
         MindMapNode node = (MindMapNode)it.next();
         newContent += node.toString();
         if (node != selectedNode) {
            removeNodeFromParent(node); }}

      getFrame().getView().select(selectedNode.getViewer());
      changeNode(selectedNode, newContent);
   }

   public boolean importExplorerFavorites(File folder, MindMapNode target, boolean redisplay) {
      // Returns true iff any favorites found
      boolean favoritesFound = false;
      if ( folder.isDirectory() ) {
         File[] list = folder.listFiles();
         // Go recursively to subfolders
         for (int i = 0; i < list.length; i++){
            if (list[i].isDirectory()) {
               // Insert a new node
               MindMapNodeModel node = new MindMapNodeModel(list[i].getName(), getFrame());
               insertNodeIntoNoEvent(node, target);
               //
               boolean favoritesFoundInSubfolder = importExplorerFavorites(list[i], node, false);
               if (favoritesFoundInSubfolder) {
                  favoritesFound = true; }
               else {
                  removeNodeFromParent(node); }}}
         
         // For each .url file: add it
         for (int i = 0; i < list.length; i++) {
            if (!list[i].isDirectory() && Tools.getExtension(list[i]).equals("url")) {
               favoritesFound = true;
               try {
                  MindMapNodeModel node = new MindMapNodeModel(Tools.removeExtension(list[i].getName()),
                                                               getFrame());
                  // For each line:  Is it URL? => Set it as link
                  BufferedReader in = new BufferedReader(new FileReader(list[i]));
                  while (in.ready()) {
                     String line = in.readLine();
                     if (line.startsWith("URL=")) {
                        node.setLink(line.substring(4));
                        break; }}

                  insertNodeIntoNoEvent(node, target); }
               catch (Exception e) {
                  e.printStackTrace(); }}}}
      if (redisplay) {
         nodeStructureChanged(target); }
      return favoritesFound;
   }

   public void importFolderStructure(File folder, MindMapNode target, boolean redisplay) {

      if ( folder.isDirectory() ) {
         File[] list = folder.listFiles();
         // Go recursively to subfolders
         for (int i = 0; i < list.length; i++){
            if (list[i].isDirectory()) {
               // Insert a new node
               MindMapNodeModel node = new MindMapNodeModel(list[i].getName(),getFrame());
               try {
                  node.setLink(list[i].toURL().toString()); }
               catch (MalformedURLException e) { e.printStackTrace(); }
               insertNodeIntoNoEvent(node, target); 
               importFolderStructure(list[i], node, false); }}

         // For each file: add it
         for (int i = 0; i < list.length; i++){
            if (!list[i].isDirectory()) {
               MindMapNodeModel node = new MindMapNodeModel(list[i].getName(),getFrame());
               try {
                  node.setLink(list[i].toURL().toString()); }
               catch (MalformedURLException e) { e.printStackTrace(); }
               insertNodeIntoNoEvent(node, target); }}}

      if (redisplay) {
         nodeStructureChanged(target); }
   }


    private MindMapNodeModel pasteXMLWithoutRedisplay(String pasted, MindMapNode target)
       throws XMLParseException  {
       return pasteXMLWithoutRedisplay(pasted, target, /*asSibling=*/false); }

    private MindMapNodeModel pasteXMLWithoutRedisplay(String pasted, MindMapNode target, boolean asSibling) 
       throws XMLParseException {
       // Call nodeStructureChanged(parent) after this function.
       try {
          XMLElement element = new XMLElement();
          element.parseFromReader(new StringReader(pasted));
          MindMapNodeModel node = new MindMapNodeModel(getFrame());
          node.load(element);
          if (asSibling) {
             MindMapNode parent = target.getParentNode();
             insertNodeInto(node, parent, parent.getChildPosition(target)); }
          else {
             insertNodeIntoNoEvent(node, target); }
          return node; }
       catch (IOException ee) { ee.printStackTrace(); return null; }}


    private void pasteStringWithoutRedisplay(String textFromClipboard, MindMapNode parent) {
       // Paste String content

       // Split the text into lines; determine the new tree structure
       // by the number of leading spaces in lines.  In case that
       // trimed line starts with protocol (http:, https:, ftp:),
       // create a link with the same content.
       String[] textLines = textFromClipboard.split("\n");
              
       if (textLines.length > 1) {
          getFrame().setWaitingCursor(true); }

       ArrayList parentNodes = new ArrayList();
       ArrayList parentNodesDepths = new ArrayList();

       parentNodes.add(parent);
       parentNodesDepths.add(new Integer(-1));

       String[] linkPrefixes = { "http://", "ftp://", "https://" };
       Pattern nonLinkCharacter = Pattern.compile("[ \n()'\",;]");

       for (int i = 0; i < textLines.length; ++i) {
          String text = textLines[i];
          text = text.replaceAll("\t","        ");
          if (text.matches(" *")) {
             continue; }
          
          int depth = 0;
          while (depth < text.length() && text.charAt(depth) == ' ') {
             ++depth; }
          String visibleText = text.trim();

          // If the text is a plain link (e.g. http://www.google.com/), make
          // it nicer by cutting off obvious prefix and suffix.

          if (visibleText.matches("^http://(www\\.)?[^/]*/?$")) {
             visibleText = visibleText.replaceAll("^http://(www\\.)?","").replaceAll("/$",""); }

          MindMapNodeModel node = new MindMapNodeModel(visibleText, getFrame());

          // Heuristically determine, if there is a link. Because this is
          // heuristic, it is probable that it can be improved to include
          // some matches or exclude some matches.

          for (int j = 0; j < linkPrefixes.length; j++) {
             int linkStart = text.indexOf(linkPrefixes[j]);
             if (linkStart != -1) {
                int linkEnd = linkStart;
                while (linkEnd < text.length() &&
                       !nonLinkCharacter.matcher(text.substring(linkEnd,linkEnd+1)).matches()) {
                   linkEnd++; }
                node.setLink(text.substring(linkStart,linkEnd)); }}          

          // Determine parent among candidate parents
          // Change the array of candidate parents accordingly

          for (int j = parentNodes.size()-1; j >= 0; --j) {
             if (depth > ((Integer)parentNodesDepths.get(j)).intValue()) {
                for (int k = j+1; k < parentNodes.size(); ++k) {
                   parentNodes.remove(k);
                   parentNodesDepths.remove(k); }
                MindMapNode target = (MindMapNode)parentNodes.get(j);
                insertNodeIntoNoEvent(node, target);

                parentNodes.add(node);
                parentNodesDepths.add(new Integer(depth));
                break; }}}
      
       nodeStructureChanged(parent);
       // ^ Do not fire any event when inserting single lines. Fire the event
       // when all the lines are inserted.
    }

    public void paste(Transferable t, MindMapNode target, boolean asSibling) {
       if (t == null) {
          return; }
       try {
           // Uncomment to print obtained data flavours
           //DataFlavor[] fl = t.getTransferDataFlavors(); 
           //  for (int i = 0; i < fl.length; i++) {
           //     System.out.println(fl[i]); }
          if (t.isDataFlavorSupported(MindMapNodesSelection.mindMapNodesFlavor)) {
             String textFromClipboard = (String)t.getTransferData(MindMapNodesSelection.mindMapNodesFlavor);
             String[] textLines = textFromClipboard.split("<nodeseparator>");
             if (textLines.length > 1) {
                getFrame().setWaitingCursor(true); }
             for (int i = 0; i < textLines.length; ++i) {
                pasteXMLWithoutRedisplay(textLines[i], target, asSibling); }}
          else if (t.isDataFlavorSupported(MindMapNodesSelection.htmlFlavor)) {
             String textFromClipboard = (String)t.getTransferData(MindMapNodesSelection.htmlFlavor);
             // ^ This outputs transfer data to standard output. I don't know why.
             pasteStringWithoutRedisplay((String)t.getTransferData(DataFlavor.stringFlavor), target);

             textFromClipboard = textFromClipboard.replaceAll("<!--.*?-->",""); // remove HTML comment
             String[] links = textFromClipboard.split("<[aA][^>]*[hH][rR][eE][fF]=\"");

             MindMapNodeModel linkParentNode = null;
             URL referenceURL = null;
             boolean baseUrlCanceled = false;
             for (int i = 1; i < links.length; i++) {
                String link =  links[i].substring(0, links[i].indexOf("\""));
                String textWithHtml = links[i].replaceAll("^[^>]*>","").replaceAll("</[aA]>[\\s\\S]*","");
                String text = Tools.toXMLUnescapedText
                   (textWithHtml.replaceAll("\\n","").replaceAll("<[^>]*>","").trim());
                if (text.equals("")) {
                   text = link; }
                URL linkURL = null;
                try {
                   linkURL = new URL(link); }
                catch (MalformedURLException ex) {
                   try {
                      // Either invalid URL or relative URL
                      if (referenceURL == null && !baseUrlCanceled) {
                         String referenceURLString = JOptionPane.showInputDialog(getText("enter_base_url"));
                         //("I want to paste relative links. Enter please base URL.");
                         //""getModel().getLink(getSelected()));
                         if (referenceURLString == null) {
                            baseUrlCanceled = true; }
                         else {
                            referenceURL = new URL(referenceURLString); }}
                      linkURL = new URL(referenceURL, link); }
                   catch (MalformedURLException ex2) { } }
                if (linkURL != null) {
                   if (linkParentNode == null) {
                      linkParentNode = new MindMapNodeModel("Links", getFrame());
                      // Here we cannot set bold, because linkParentNode.font is null
                      insertNodeInto(linkParentNode, target);
                      linkParentNode.setBold(true);
                   }
                   MindMapNodeModel linkNode = new MindMapNodeModel(text, getFrame());
                   linkNode.setLink(linkURL.toString());
                   insertNodeInto(linkNode, linkParentNode); }}}
          else if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
             String textFromClipboard = (String)t.getTransferData(DataFlavor.stringFlavor);
             pasteStringWithoutRedisplay(textFromClipboard, target); }
          nodeStructureChanged(target); }
       catch (Exception e) { e.printStackTrace(); }
       getFrame().setWaitingCursor(false);        
    }
}
