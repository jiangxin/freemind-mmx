
/*FreeMindget - A Program for creating and viewing Mindmaps
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
/*$Id: MindMapMapModel.java,v 1.25 2003-11-16 22:15:16 christianfoltin Exp $*/

package freemind.modes.mindmapmode;

import freemind.main.FreeMindMain;
import freemind.controller.MindMapNodesSelection;
import freemind.main.XMLParseException;
import freemind.main.Tools;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.MindIcon;
import freemind.modes.MindMapLink;
import freemind.modes.mindmapmode.MindMapCloudModel;
import freemind.modes.mindmapmode.MindMapArrowLinkModel;

import java.awt.Color;
import java.awt.Font;
import java.awt.datatransfer.*;

import java.io.*;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.lang.UnsatisfiedLinkError;
import java.lang.NoClassDefFoundError;

import java.net.URL;
//import java.net.URLConnection;
import java.net.MalformedURLException;

import javax.swing.JOptionPane;
import java.nio.channels.FileLock;

//temporary for unique labels:
import java.util.Random;
// link registry.
import freemind.modes.LinkRegistryAdapter;
import freemind.modes.MindMapLinkRegistry;


public class MindMapMapModel extends MapAdapter {

    LockManager lockManager;
    private LinkRegistryAdapter linkRegistry;

    //
    // Constructors
    //

    public MindMapMapModel(FreeMindMain frame) {
        this(new MindMapNodeModel( frame.getResources().getString("new_mindmap"), frame), frame);
    }
    
    public MindMapMapModel( MindMapNodeModel root, FreeMindMain frame ) {
        super(frame);
        lockManager = frame.getProperty("experimental_file_locking_on").equals("true") ? 
           new LockManager() : new DummyLockManager();

        // register new LinkRegistryAdapter
        linkRegistry = new LinkRegistryAdapter();

        setRoot(root);
        readOnly = false; }

    // 

    public MindMapLinkRegistry getLinkRegistry() {
        return linkRegistry;
    }

    public String getRestoreable() {
       return getFile()==null ? null : "MindMap:"+getFile().getAbsolutePath(); }

    //  All these methods do redisplay, because they are offered to controller for use.
    // __________________________________________________________________________

    public void setNodeColor(MindMapNodeModel node, Color color) {
        node.setColor(color);
        nodeChanged(node); }

    public void blendNodeColor(MindMapNodeModel node) {
        Color mapColor = getBackgroundColor();
        Color nodeColor = node.getColor();
        if (nodeColor == null) {
           nodeColor = Tools.xmlToColor(getFrame().getProperty("standardnodecolor")); }
        node.setColor( new Color ( (3*mapColor.getRed() + nodeColor.getRed()) / 4,
                                   (3*mapColor.getGreen() + nodeColor.getGreen()) / 4,
                                   (3*mapColor.getBlue() + nodeColor.getBlue()) / 4));
        nodeChanged(node); }

    public void setNodeFont(MindMapNodeModel node, Font font) {
        node.setFont(font);
        nodeChanged(node); }

    public void setEdgeColor(MindMapNodeModel node, Color color) {
        ((MindMapEdgeModel)node.getEdge()).setColor(color);
        nodeChanged(node); }

    public void setEdgeWidth(MindMapNodeModel node, int width) {
        ((MindMapEdgeModel)node.getEdge()).setWidth(width);
        nodeChanged(node); }

    public void setNodeStyle(MindMapNodeModel node, String style) {
        node.setStyle(style);
        nodeStructureChanged(node); }

    public void setEdgeStyle(MindMapNodeModel node, String style) {
        MindMapEdgeModel edge = (MindMapEdgeModel)node.getEdge();
        edge.setStyle(style);
        nodeStructureChanged(node); }

    public void setBold(MindMapNodeModel node) {
        node.setBold(!node.isBold());
        nodeChanged(node); }

    public void setCloud(MindMapNodeModel node) {
        if(node.getCloud() == null) {
            node.setCloud(new MindMapCloudModel(node, getFrame()));
        } else {
            node.setCloud(null);
        }
        nodeChanged(node); 
    }

    public void setCloudColor(MindMapNodeModel node, Color color) {
        if(node.getCloud() == null) {
            setCloud(node);
        }
        ((MindMapCloudModel)node.getCloud()).setColor(color);
        nodeChanged(node); }

    public void setCloudWidth(MindMapNodeModel node, int width) {
        if(node.getCloud() == null) {
            setCloud(node);
        }
        ((MindMapCloudModel)node.getCloud()).setWidth(width);
        nodeChanged(node); }

    public void setCloudStyle(MindMapNodeModel node, String style) {
        if(node.getCloud() == null) {
            setCloud(node);
        }
        MindMapCloudModel cloud = (MindMapCloudModel)node.getCloud();
        cloud.setStyle(style);
        nodeStructureChanged(node); }


    public void addIcon(MindMapNodeModel node, MindIcon icon) {
        node.addIcon(icon);
        nodeChanged(node); }

    public int removeLastIcon(MindMapNodeModel node) {
        int retval = node.removeLastIcon();
        nodeChanged(node); 
        return retval;
    }

    /** Source holds the MindMapArrowLinkModel and points to the id placed in target.*/
    public void addLink(MindMapNodeModel source, MindMapNodeModel target) {
        if(target.getLabel() == null) {
            // call registry to give new label
            getLinkRegistry().registerLinkTarget(target);
        }
        MindMapArrowLinkModel linkModel = new MindMapArrowLinkModel(source, target, getFrame());
        linkModel.setDestinationLabel(target.getLabel());
        // register link.
        getLinkRegistry().registerLink(source, target);
        source.addReference((MindMapLink) linkModel);
        nodeChanged(target); 
        nodeChanged(source); 
    }

    public void removeReference(MindMapNode source, MindMapArrowLinkModel arrowLink) {
        Vector mapLinks = source.getReferences();
        for(int i = 0; i < mapLinks.size(); ++i) {
            if(mapLinks.get(i) == arrowLink) {
                source.removeReferenceAt(i);
//                 System.out.println("Deregister?");
                // deregister link.
                getLinkRegistry().deregisterLink(source, arrowLink.getTarget());
                nodeChanged(source);
                nodeChanged(arrowLink.getTarget());
                return;
            }
        }
    }

    public void changeArrowsOfArrowLink(MindMapNode source, MindMapArrowLinkModel arrowLink, boolean hasStartArrow, boolean hasEndArrow) {
        arrowLink.setStartArrow(hasStartArrow);
        arrowLink.setEndArrow(hasEndArrow);
        nodeChanged(source);
    }

    public void setArrowLinkColor(MindMapNode source, MindMapArrowLinkModel arrowLink, Color color) {
        arrowLink.setColor(color);
        nodeChanged(source); 
    }

    public void setItalic(MindMapNodeModel node) {
        node.setItalic(!node.isItalic());
        nodeChanged(node); }

    public void setUnderlined(MindMapNodeModel node) {
        node.setUnderlined(!node.isUnderlined());
        nodeChanged(node); }

    public void setNormalFont(MindMapNodeModel node) {
        node.setItalic(false);
        node.setBold(false);
        node.setUnderlined(false);
        nodeChanged(node); }

    public void setFontFamily(MindMapNodeModel node, String fontFamily) {
        node.estabilishOwnFont();
        node.setFont(getFrame().getController().getFontThroughMap
                     (new Font(fontFamily,node.getFont().getStyle(),node.getFont().getSize())));
        nodeChanged(node); }

    public void setFontSize(MindMapNodeModel node, int fontSize) {
        node.estabilishOwnFont();
        node.setFont(node.getFont().deriveFont((float)fontSize));
        nodeChanged(node); }

    public void increaseFontSize(MindMapNodeModel node, int increment) {
        node.estabilishOwnFont();
        node.setFontSize(node.getFont().getSize() + increment);
        nodeChanged(node); }

    //
    // Other methods
    //

    public String toString() {
       return getFile() == null ? null : getFile().getName(); }

    //
    // Export and saving
    //


   public boolean saveHTML(MindMapNodeModel rootNodeOfBranch, File file) { 
        // When isRoot is true, rootNodeOfBranch will be exported as folded
        // regardless his isFolded state in the mindmap.
        try {
            // We do all the HTML saving using just ordinary output.

            //Generating output Stream
            BufferedWriter fileout = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(file) ) );

            String el = System.getProperty("line.separator");
            fileout.write(
"<html>"+el+
"<head>"+el+
"<title>"+rootNodeOfBranch.saveHTML_escapeUnicodeAndSpecialCharacters(rootNodeOfBranch.toString())+
"</title>"+el+
"<style type=\"text/css\">"+el+
"    span.foldopened { color: white; font-size: xx-small;"+el+
"    border-width: 1; font-family: monospace; padding: 0em 0.25em 0em 0.25em; background: #e0e0e0;"+el+
"    VISIBILITY: visible;"+el+
"    cursor:hand; }"+el+
""+el+
""+el+
"    span.foldclosed { color: #666666; font-size: xx-small;"+el+
"    border-width: 1; font-family: monospace; padding: 0em 0.25em 0em 0.25em; background: #e0e0e0;"+el+
"    VISIBILITY: hidden;"+el+
"    cursor:hand; }"+el+
""+el+
"    span.foldspecial { color: #666666; font-size: xx-small; border-style: none solid solid none;"+el+
"    border-color: #CCCCCC; border-width: 1; font-family: sans-serif; padding: 0em 0.1em 0em 0.1em; background: #e0e0e0;"+el+
"    cursor:hand; }"+el+
""+el+
"    li { list-style: none; }"+el+
""+el+
"    span.l { color: red; font-weight: bold; }"+el+
""+el+
"    a:link {text-decoration: none; color: black; }"+el+
"    a:visited {text-decoration: none; color: black; }"+el+
"    a:active {text-decoration: none; color: black; }"+el+
"    a:hover {text-decoration: none; color: black; background: #eeeee0; }"+el+
""+el+
"</style>"+el+
"<!-- ^ Position is not set to relative / absolute here because of Mozilla -->"+el+
"</head>"+el+
"<body>"+el);

            String htmlExportFoldingOption = getFrame().getProperty("html_export_folding");
            boolean writeFoldingCode =
               ( htmlExportFoldingOption.equals("html_export_fold_currently_folded") &&
                 rootNodeOfBranch.hasFoldedStrictDescendant() ) ||
               htmlExportFoldingOption.equals("html_export_fold_all") ;

            if (writeFoldingCode) { 
               fileout.write(
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
               
               fileout.write("<SPAN class=foldspecial onclick=\"fold_document()\">All +</SPAN>"+el);
               fileout.write("<SPAN class=foldspecial onclick=\"unfold_document()\">All -</SPAN>"+el); }

            //fileout.write("<ul>");

            rootNodeOfBranch.saveHTML(fileout, "1", 0, /*isRoot*/true, /*treatAsParagraph*/true, /*depth*/1);

            //fileout.write("</ul>");

            if (writeFoldingCode) {
               fileout.write("<SCRIPT language=JavaScript>"+el);
               fileout.write("fold_document();"+el);
               fileout.write("</SCRIPT>"+el); }
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
           String colorTableString="{\\colortbl;\\red0\\green0\\blue255;";
           // 0 - Automatic, 1 - blue for links

           HashMap colorTable = new HashMap();
           int colorPosition = 2;
           for(Iterator it=colors.iterator();it.hasNext();++colorPosition) {
              Color color = (Color)it.next();
              colorTableString += "\\red"+color.getRed()+"\\green"+color.getGreen()+
                 "\\blue"+color.getBlue()+";";
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
    /**
     * Return the success of saving
     */
    public boolean save(File file) {
        if (readOnly) { // unexpected situation, yet it's better to back it up
            System.err.println("Attempt to save read-only map.");           
            return false; }
        try {            
            //Generating output Stream            
            BufferedWriter fileout = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(file) ) );
            
            fileout.write("<map>\n");
            ((MindMapNodeModel)getRoot()).save(fileout);
            fileout.write("</map>\n");
            fileout.close();

            setFile(file);            
            setSaved(true);
            return true;
        } catch (FileNotFoundException e ) {
            getFrame().getController().errorMessage(
                Tools.expandPlaceholders(getText("save_failed"),file.getName()));
            return false; 
        } catch(Exception e) {
            System.err.println("Error in MindMapMapModel.save(): ");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Attempts to lock the map using a semaphore file
     * @param file
     * @return If the map is locked, return the name of the locking user, otherwise return null.
     * @throws Exception, when the locking failed for other reasons than that the
     * file is being edited.
     */
    public String tryToLock(File file) throws Exception {       
        String lockingUser = lockManager.tryToLock(file);
        String lockingUserOfOldLock = lockManager.popLockingUserOfOldLock(); 
        if (lockingUserOfOldLock != null) {     
          getFrame().getController().informationMessage(
            Tools.expandPlaceholders(getText("locking_old_lock_removed"), file.getName(), lockingUserOfOldLock)); }
        if (lockingUser == null) {          
          readOnly = false; } // The map sure is not read only when the locking suceeded.                   
        return lockingUser; }
                
    public void load(File file) throws FileNotFoundException, IOException, XMLParseException {
       if (!file.exists()) {
          throw new FileNotFoundException(Tools.expandPlaceholders(getText("file_not_found"), file.getPath())); }
       if (!file.canWrite()) {
          readOnly = true; }
       else {
          // try to lock the map
          try {
             String lockingUser = tryToLock(file);
             if (lockingUser != null) {          
               getFrame().getController().informationMessage(
                 Tools.expandPlaceholders(getText("map_locked_by_open"), file.getName(), lockingUser));
               readOnly = true; }
             else {
               readOnly = false; }}            
          catch (Exception e){ // Throwed by tryToLock
             e.printStackTrace();
             getFrame().getController().informationMessage(
               Tools.expandPlaceholders(getText("locking_failed_by_open"), file.getName()));   
             readOnly = true; }}
       
       MindMapNodeModel root = loadTree(file);
       if (root != null) {
          setRoot(root); }
       setFile(file);
       setSaved(true); } 
    
    public void destroy() {
       lockManager.releaseLock();
       lockManager.releaseTimer(); }

    MindMapNodeModel loadTree(File file) throws XMLParseException, IOException {
        MindMapXMLElement mapElement = new MindMapXMLElement(getFrame());
        try {
           mapElement.parseFromReader(new BufferedReader(new FileReader(file))); }
        catch (Exception ex) {
           System.err.println("Error while parsing file:"+ex);
           ex.printStackTrace();
           return null; }
        // complete the arrow links:
        mapElement.processUnfinishedLinks(mapElement.getMapChild(), getLinkRegistry());
        return mapElement.getMapChild(); 
    }

    

    //
    // cut'n'paste
    //
// (PN) see: super.cut(node) ... it does exactly the same!!!
//    public Transferable cut(MindMapNode node) {
//       Transferable transfer = copy(node);
//       super.cut(node);
//       return transfer;
//    }

    public Transferable copy(MindMapNode node) {
       StringWriter stringWriter = new StringWriter();
       try {
          ((MindMapNodeModel)node).save(stringWriter); }
       catch (IOException e) {}
       return new StringSelection(stringWriter.toString()); }

   public void splitNode(MindMapNode node, int caretPosition, String newText) {
      //If there are children, they go to the node below
      String currentText = newText != null ? newText : node.toString();

      String newContent = currentText.substring(caretPosition, currentText.length());
      MindMapNodeModel upperNode =
         new MindMapNodeModel(currentText.substring(0,caretPosition), getFrame());

      upperNode.setColor(node.getColor());
      upperNode.setFont(node.getFont());

      node.setUserObject(newContent);
      MindMapNode parent = node.getParentNode();
      insertNodeInto(upperNode, parent, parent.getChildPosition(node));
      nodeStructureChanged(parent);
   }

   public void joinNodes() {
      MindMapNode selectedNode = getFrame().getView().getSelected().getModel();
      ArrayList selectedNodes = getFrame().getView().getSelectedNodesSortedByY();
      String newContent = "";
      boolean firstLoop = true;

      // Make sure the selected node do not have children
      for(Iterator it = selectedNodes.iterator();it.hasNext();) {
         MindMapNode node = (MindMapNode)it.next();
         if (node.hasChildren()) {
            JOptionPane.showMessageDialog
               (node.getViewer(), getText("cannot_join_nodes_with_children"),
                "FreeMind", JOptionPane.WARNING_MESSAGE);
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

      getFrame().getView().selectAsTheOnlyOneSelected(selectedNode.getViewer());
      changeNode(selectedNode, newContent);
   }

   public boolean importExplorerFavorites(File folder, MindMapNode target, boolean redisplay) {
      // Returns true iff any favorites found
      boolean favoritesFound = false;
      if (folder.isDirectory()) {
         File[] list = folder.listFiles();
         // Go recursively to subfolders
         for (int i = 0; i < list.length; i++) {
            if (list[i].isDirectory()) {
               // Insert a new node
               MindMapNodeModel node = new MindMapNodeModel(list[i].getName(), getFrame());
               insertNodeIntoNoEvent(node, target);
               //
               boolean favoritesFoundInSubfolder = importExplorerFavorites(list[i], node, false);
               if (favoritesFoundInSubfolder) {
                  favoritesFound = true; }
               else {
                  removeNodeFromParent(node, /*notify=*/false); }}}
         
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
          MindMapXMLElement element = new MindMapXMLElement(getFrame());
          element.parseFromReader(new StringReader(pasted));
          MindMapNodeModel node = (MindMapNodeModel)element.getUserObject();

          if (asSibling) {
             MindMapNode parent = target.getParentNode();
             insertNodeInto(node, parent, parent.getChildPosition(target)); }
          else {
             insertNodeIntoNoEvent(node, target); }
          return node; }
       catch (IOException ee) { ee.printStackTrace(); return null; }}

    static final Pattern nonLinkCharacter = Pattern.compile("[ \n()'\",;]");

    /**
     * Paste String (as opposed to other flavours)
     *
     * Split the text into lines; determine the new tree structure
     * by the number of leading spaces in lines.  In case that
     * trimmed line starts with protocol (http:, https:, ftp:),
     * create a link with the same content.
     *
     * If there was only one line to be pasted, return the pasted node, null otherwise.
     */

    private MindMapNode pasteStringWithoutRedisplay(String textFromClipboard, MindMapNode parent,
                                                    boolean asSibling) {

       Pattern mailPattern = Pattern.compile("([^@ <>\\*']+@[^@ <>\\*']+)");

       String[] textLines = textFromClipboard.split("\n");
              
       if (textLines.length > 1) {
          getFrame().setWaitingCursor(true); }

       MindMapNode realParent = null;
       if (asSibling) {
          // When pasting as sibling, we use virtual node as parent. When the pasting to
          // virtual node is completed, we insert the children of that virtual node to
          // the parrent of real parent.
          realParent = parent;
          parent = new MindMapNodeModel(getFrame()); }

       ArrayList parentNodes = new ArrayList();
       ArrayList parentNodesDepths = new ArrayList();

       parentNodes.add(parent);
       parentNodesDepths.add(new Integer(-1));

       String[] linkPrefixes = { "http://", "ftp://", "https://" };

       MindMapNodeModel pastedNode = null;

       for (int i = 0; i < textLines.length; ++i) {
          String text = textLines[i];
          text = text.replaceAll("\t","        ");
          if (text.matches(" *")) {
             continue; }
          
          int depth = 0;
          while (depth < text.length() && text.charAt(depth) == ' ') {
             ++depth; }
          String visibleText = text.trim();

          // If the text is a recognizable link (e.g. http://www.google.com/index.html),
          // make it more readable by look nicer by cutting off obvious prefix and other
          // transforamtions.

          if (visibleText.matches("^http://(www\\.)?[^ ]*$")) {
             visibleText = visibleText.replaceAll("^http://(www\\.)?","").
                replaceAll("(/|\\.[^\\./\\?]*)$","").replaceAll("((\\.[^\\./]*\\?)|\\?)[^/]*$"," ? ...").replaceAll("_|%20"," ");
             String[] textParts = visibleText.split("/");
             visibleText = "";
             for (int textPartIdx = 0; textPartIdx < textParts.length; textPartIdx++) {
                if (textPartIdx > 0 ) {
                   visibleText += " > "; }
                visibleText += textPartIdx == 0 ? textParts[textPartIdx] : 
                   Tools.firstLetterCapitalized(textParts[textPartIdx].replaceAll("^~*","")); }}

          MindMapNodeModel node = new MindMapNodeModel(visibleText, getFrame());
          if (textLines.length == 1) {
             pastedNode = node; }

          // Heuristically determine, if there is a mail.

          Matcher mailMatcher = mailPattern.matcher(visibleText);
          if (mailMatcher.find()) {
             node.setLink("mailto:"+mailMatcher.group()); }

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

       if (asSibling) {
          for (Iterator i=parent.childrenUnfolded(); /*children.iterator()*/ i.hasNext(); ) {
             insertNodeIntoNoEvent((MindMapNode)i.next(), realParent, asSibling); }
          nodeStructureChanged(realParent.getParentNode()); }
       else {
          nodeStructureChanged(parent); }
       // ^ Do not fire any event when inserting single lines. Fire the event
       // when all the lines are inserted.
       return pastedNode;
    }

    /*
     *
     */
    public void paste(Transferable t, MindMapNode target, boolean asSibling) {
       if (t == null) {
          return; }
       try {
           // Uncomment to print obtained data flavours

           /*
           DataFlavor[] fl = t.getTransferDataFlavors(); 
           for (int i = 0; i < fl.length; i++) {
              System.out.println(fl[i]); }
           */

          if (t.isDataFlavorSupported(MindMapNodesSelection.fileListFlavor)) {
             // TODO: Does not correctly interpret asSibling.
             System.err.println("flflpas");
             List fileList = (List)t.getTransferData(MindMapNodesSelection.fileListFlavor);
             for(ListIterator it=fileList.listIterator();it.hasNext();) {
                File file = (File)it.next();
                MindMapNodeModel node = new MindMapNodeModel(file.getName(), getFrame());
                node.setLink(file.getAbsolutePath());
                insertNodeIntoNoEvent(node, target, asSibling); }
             nodeStructureChanged(asSibling ? target.getParent() : target); }
          else if (t.isDataFlavorSupported(MindMapNodesSelection.mindMapNodesFlavor)) {
             String textFromClipboard =
                (String)t.getTransferData(MindMapNodesSelection.mindMapNodesFlavor);
             String[] textLines = textFromClipboard.split("<nodeseparator>");
             if (textLines.length > 1) {
                getFrame().setWaitingCursor(true); }
             for (int i = 0; i < textLines.length; ++i) {
                pasteXMLWithoutRedisplay(textLines[i], target, asSibling); }}
          else if (t.isDataFlavorSupported(MindMapNodesSelection.htmlFlavor)) {
             String textFromClipboard =
                (String)t.getTransferData(MindMapNodesSelection.htmlFlavor);
             // ^ This outputs transfer data to standard output. I don't know why.
             MindMapNode pastedNode = 
                pasteStringWithoutRedisplay
                ((String)t.getTransferData(DataFlavor.stringFlavor), target, asSibling);

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
                         if (referenceURLString == null) {
                            baseUrlCanceled = true; }
                         else {
                            referenceURL = new URL(referenceURLString); }}
                      linkURL = new URL(referenceURL, link); }
                   catch (MalformedURLException ex2) { } }
                if (linkURL != null) {
                   if (links.length == 2 & pastedNode != null) {
                      // pastedNode != null iff the number of pasted lines is one
                      // The firts element in links[] array is never a link, therefore
                      // the condition links.length == 2 actually says "there is one link".
                      // Set link directly into node
                      ((MindMapNodeModel)pastedNode).setLink(linkURL.toString());
                      break; }
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
             pasteStringWithoutRedisplay(textFromClipboard, target, asSibling); }          
          nodeStructureChanged(asSibling ? target.getParent() : target); }
       catch (Exception e) { e.printStackTrace(); }
       getFrame().setWaitingCursor(false);        
    }
    
    private class LockManager extends TimerTask {
        File lockedSemaphoreFile = null;        
        Timer lockTimer = null;
        final long lockUpdatePeriod = 4*60*1000; // four minutes
        final long lockSafetyPeriod = 5*60*1000; // five minutes
        String lockingUserOfOldLock = null;    
                
        private File getSemaphoreFile(File mapFile) {       
            return new File(mapFile.getParent()+System.getProperty("file.separator")+
                            "$~"+mapFile.getName()+"~"); }
                            
        public synchronized String popLockingUserOfOldLock() {
            String toReturn = lockingUserOfOldLock;
            lockingUserOfOldLock = null;
            return toReturn; }  

        private void writeSemaphoreFile(File inSemaphoreFile) throws Exception {
            FileOutputStream semaphoreOutputStream = new FileOutputStream(inSemaphoreFile);
            FileLock lock = null;
            try {
               lock = semaphoreOutputStream.getChannel().tryLock(); 
               if (lock == null) {
                  semaphoreOutputStream.close();
                  System.err.println("Locking failed.");                  
                  throw new Exception(); }} // locking failed
            catch (UnsatisfiedLinkError eUle) {}  // This may come with Windows95. We don't insist on detailed locking in that case.
            catch (NoClassDefFoundError eDcdf) {} // ^ just like above.
            // ^ On Windows95, the necessary libraries are missing.
            semaphoreOutputStream.write(System.getProperty("user.name").getBytes());
            semaphoreOutputStream.write('\n');
            semaphoreOutputStream.write(String.valueOf(System.currentTimeMillis()).getBytes());         
            semaphoreOutputStream.close();
            semaphoreOutputStream = null;
            Tools.setHidden(inSemaphoreFile, true, /*synchro=*/false); // Exception free  
            if (lock != null) lock.release(); }
                                    
        public synchronized String tryToLock(File file) throws Exception {
            // Locking should work for opening as well as for saving as.
            // We are especially carefull when it comes to exclusivity of writing.
                 
            File semaphoreFile = getSemaphoreFile(file);
            if (semaphoreFile == lockedSemaphoreFile) {
                return null ; }
            try {
               BufferedReader semaphoreReader = new BufferedReader(new FileReader(semaphoreFile));
               String lockingUser = semaphoreReader.readLine();

               long lockTime = new Long (semaphoreReader.readLine()).longValue();
               long timeDifference = System.currentTimeMillis() - lockTime;
               //catch (NumberFormatException enf) {} // This means that the time was not written at all - lock is corrupt
               if (timeDifference > lockSafetyPeriod) { // the lock is old
                  semaphoreReader.close();
                  lockingUserOfOldLock = lockingUser;
                  semaphoreFile.delete(); }
               else return lockingUser; }
            catch (FileNotFoundException e) {}

            writeSemaphoreFile(semaphoreFile);

            if (lockTimer == null) {
              lockTimer = new Timer();
              lockTimer.schedule(this, lockUpdatePeriod, lockUpdatePeriod); }                   
            releaseLock();
            lockedSemaphoreFile = semaphoreFile;
            return null; }
               
        public synchronized void releaseLock() {
           if (lockedSemaphoreFile != null) {
              lockedSemaphoreFile.delete();
              lockedSemaphoreFile = null; }} // this may fail, TODO: ensure real deletion

        public synchronized void releaseTimer() {
            if (lockTimer != null) {
              lockTimer.cancel();
              lockTimer = null; }}
               
        public synchronized void run() { // update semaphore file           
            if (lockedSemaphoreFile == null) {
                System.err.println("unexpected: lockedSemaphoreFile is null upon lock update");
                return; }           
            try {
               Tools.setHidden(lockedSemaphoreFile, false, /*synchro=*/true); // Exception free
               // ^ We unhide the file before overwriting because JavaRE1.4.2 does
               // not let us open hidden files for writing. This is a workaround for Java bug,
               // I guess.

               writeSemaphoreFile(lockedSemaphoreFile); }
            catch (Exception e) {e.printStackTrace();}}         
    }   
    private class DummyLockManager extends LockManager {
        public synchronized String popLockingUserOfOldLock() {
            return null; }  
                                    
        public synchronized String tryToLock(File file) throws Exception {
            return null; }
               
        public synchronized void releaseLock() {}
            
        public synchronized void releaseTimer() {}
               
        public synchronized void run() {}
    }   
    
}
