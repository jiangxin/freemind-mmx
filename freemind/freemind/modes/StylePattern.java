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
/*$Id: StylePattern.java,v 1.2 2003-11-03 10:39:51 sviles Exp $*/

package freemind.modes;

import java.awt.Color;
import java.awt.Font;
import java.util.*;
import java.io.*;
import freemind.main.XMLElement;
import freemind.main.Tools;

// Daniel: this seems like a description of what pattern should do rather
// than of that what it actually does.

/**
 * This class represents a StylePattern than can be applied
 * to a node or a whole branch. The properties of the
 * nodes are replaced with the properties saved in the
 * pattern. If a property "text" is given, this pattern
 * is automatically applied to all nodes that contain the
 * String saved in "text".
 */
public class StylePattern {
    private String name;
    private boolean recursive;
    // ^ The idea of recursive is redundant. You have a possibility to
    // select all nodes in a branch easily.

    private String text;
    private boolean folded;   // Daniel: What is this good for?

    private boolean appliesToNode = false;
    private Color  nodeColor;
    private String nodeStyle;
    private Font   nodeFont;

    private boolean appliesToEdge = false;    
    private Color  edgeColor;
    private String edgeStyle;
    private int    edgeWidth;
    
    private boolean appliesToNodeFont = false;

    public StylePattern() {}

    public StylePattern(String name) {
       setName(name); }

    public StylePattern(File file) throws Exception {
       loadPatterns(file); }

    public StylePattern(XMLElement elm) {
       loadPattern(elm); }

    public String toString() {
        return "node: "+nodeColor+", "+nodeStyle+", "+nodeFont+", "+
           "\nedge: "+edgeColor+", "+edgeStyle+", "+edgeWidth; }

    public boolean getAppliesToEdge() {
       return appliesToEdge; }

    public boolean getAppliesToNode() {
       return appliesToNode; }

    public boolean getAppliesToNodeFont() {
       return appliesToNodeFont; }

    /**
       * Get the value of name.
       * @return Value of name.
       */
    public String getName() {
       return name; }
    
    /**
       * Set the value of name.
       * @param v  Value to assign to name.
       */
    public void setName(String  v) {
       this.name = v; }
    
    /**
     * Determine if the properies of this pattern, of course
     * except the "text" attribute, apply to all the child nodes
     * of this node.
     * @return Value of recursive.
     */
    public boolean getRecursive() {
       return recursive; }
    
    /**
       * Set the value of recursive.
       * @param v  Value to assign to recursive.
       */
    public void setRecursive(boolean  v) {
       this.recursive = v; }
    

    /**
       * Get the value of folded.
       * @return Value of folded.
       */
    public boolean getFolded() {
       return folded; }
    
    /**
       * Set the value of folded.
       * @param v  Value to assign to folded.
       */
    public void setFolded(boolean  v) {
       this.folded = v; }
    

    /**
       * Get the value of text.
       * @return Value of text.
       */
    public String getText() {
       return text; }
    
    /**
       * Set the value of text.
       * @param v  Value to assign to text.
       */
    public void setText(String  v) {
       this.text = v; }

    /**
       * Get the value of nodeColor.
       * @return Value of nodeColor.
       */
    public Color getNodeColor() {
       return nodeColor; }
    
    /**
       * Set the value of nodeColor.
       * @param v  Value to assign to nodeColor.
       */
    public void setNodeColor(Color  v) {
       this.nodeColor = v; }
    
    
    /**
       * Get the value of nodeStyle.
       * @return Value of nodeStyle.
       */
    public String getNodeStyle() {
       return nodeStyle; }
    
    /**
       * Set the value of nodeStyle.
       * @param v  Value to assign to nodeStyle.
       */
    public void setNodeStyle(String  nodeStyle) {
       this.nodeStyle = nodeStyle; }
    
    
    /**
       * Get the value of font.
       * @return Value of font.
       */
    public Font getNodeFont() {
       return nodeFont;}

    /**
       * Set the value of font.
       * @param v  Value to assign to font.
       */
    public void setNodeFont(Font  nodeFont) {
       this.nodeFont = nodeFont; }

    /**
       * Get the value of edgeColor.
       * @return Value of edgeColor.
       */
    public Color getEdgeColor() {
       return edgeColor; }
    
    /**
       * Set the value of edgeColor.
       * @param v  Value to assign to edgeColor.
       */
    public void setEdgeColor(Color edgeColor) {
       this.edgeColor = edgeColor; }

    /**
      * Get the value of edgeStyle.
      * @return Value of edgeStyle.
      */
    public String getEdgeStyle() {
       return edgeStyle; }
    
    /**
     * Set the value of edgeStyle.
       * @param v  Value to assign to edgeStyle.
       */
    public void setEdgeStyle(String  edgeStyle) {
       this.edgeStyle = edgeStyle; }


    /**
       * Get the value of edgeWidth.
       * @return Value of edgeWidth.
       */
    public int getEdgeWidth() {
       return edgeWidth; }
    
    /**
       * Set the value of edgeWidth.
       * @param v  Value to assign to edgeWidth.
       */
    public void setEdgeWidth(int edgeWidth) {
       this.edgeWidth = edgeWidth;}
    
    public static List loadPatterns(File file) throws Exception {
       return loadPatterns(new BufferedReader(new FileReader(file))); }

    public static List loadPatterns(Reader reader) throws Exception {
        List list = new LinkedList();
        XMLElement parser = new XMLElement();
        parser.parseFromReader(reader);
        for (Enumeration e = parser.enumerateChildren();e.hasMoreElements();){
           list.add(new StylePattern((XMLElement)e.nextElement())); }
        return list; }

    public void loadPattern(XMLElement pattern) {
        //PATTERN
        if (pattern.getProperty("name")!=null) {
           setName(pattern.getProperty("name")); }
        if (Tools.safeEquals(pattern.getProperty("recursive"),"true")) {
           setRecursive(true); }

        for (Iterator i=pattern.getChildren().iterator(); i.hasNext(); ) {
            //this has to be improved!
            //NODE
           XMLElement child = (XMLElement)i.next();
           if (child.getTagName().equals("node")) {
              appliesToNode = true;
              if (child.getProperty("color")!=null && 
                  child.getProperty("color").length() == 7) {
                 setNodeColor(Tools.xmlToColor(child.getProperty("color") ) ); }
              if (child.getProperty("style")!=null) {
                 setNodeStyle(child.getProperty("style")); }
              setText(child.getProperty("text"));

              for (Iterator j=child.getChildren().iterator(); j.hasNext();) {
                 XMLElement nodeChild = (XMLElement)j.next();
                 //FONT
                 if (nodeChild.getTagName().equals("font")) {
                    appliesToNodeFont = true;

                    String name = nodeChild.getProperty("name"); 
                    int style=0;
                    int size=0;
                                            
                    if (Tools.safeEquals(nodeChild.getProperty("bold"),"true")) {
                       style+=Font.BOLD; }
                    if (Tools.safeEquals(nodeChild.getProperty("italic"),"true")) {
                       style+=Font.ITALIC; }
                    // if (font.getProperty("underline")!=null && 
                    // nodeChild.getProperty("underline").equals("true")) setUnderlined(true);                    
                    if (nodeChild.getProperty("size")!=null) {
                       size = Integer.parseInt(nodeChild.getProperty("size")); }
                    
                    setNodeFont(new Font(name, style, size));

                    if (size == 0) {
                       setNodeFont(null); }

                 }}}
           
           //EDGE
           if (child.getTagName().equals("edge")) {
              appliesToEdge = true;
              if (child.getProperty("style")!=null) {
                 setEdgeStyle(child.getProperty("style")); }
              if (child.getProperty("color")!=null) {
                 setEdgeColor(Tools.xmlToColor(child.getProperty("color") ) ); }
              if (child.getProperty("width")!=null) {
                 if (child.getProperty("width").equals("thin")) {
                    setEdgeWidth(freemind.modes.EdgeAdapter.WIDTH_THIN); }
                 else {
                    setEdgeWidth(Integer.parseInt(child.getProperty("width"))); }}}}}}




    /*
      Is saving necessary?
    public void savePattern(File file) {
        try {
            //CODE FOR NANOXML
            XMLElement pattern = new XMLElement();
            pattern.setTagName("pattern");
            XMLElement node = new XMLElement();
            node.setTagName("node");
            pattern.addChild(node);
            XMLElement edge = new XMLElement();
            edge.setTagName("edge");
            pattern.addChild(edge);


            pattern.addChild(((MindMapNodeModel)getRoot()).save());

              XMLElement node = new XMLElement();
              node.setTagName("node");
              
              node.addProperty("text",this.toString());
              
              //        ((MindMapEdgeModel)getEdge()).save(doc,node);

              XMLElement edge = ((MindMapEdgeModel)getEdge()).save();
              if (edge != null) {
              node.addChild(edge);
              }
              
              if (isFolded()) {
              node.addProperty("folded","true");
              }
              
              if (color != null) {
              node.addProperty("color", Tools.colorToXml(getColor()));
              }
              
              if (style != null) {
              node.addProperty("style", getStyle());
              }
              
              //link
              if (getLink() != null) {
              node.addProperty("link", getLink());
              }

              //font
              if (font!=null || font.getSize()!=0 || isBold() || isItalic() || isUnderlined() ) {
              XMLElement fontElement = new XMLElement();
              fontElement.setTagName("font");
              
              if (font != null) {
                fontElement.addProperty("name",getFont().getFontName());
                }
                if (font.getSize() != 0) {
                fontElement.addProperty("size",Integer.toString(getFont().getSize()));
                }
                if (isBold()) {
                fontElement.addProperty("bold","true");
                }
                if (isItalic()) {
                fontElement.addProperty("italic","true");
                }
                if (isUnderlined()) {
                fontElement.addProperty("underline","true");
                }
                node.addChild(fontElement);
                }
            
            
            
            //Generating output Stream
            BufferedWriter fileout = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(file) ) );
            pattern.write(fileout);
            
            fileout.close();
            
        } catch(Exception e) {
            System.err.println("Error in MindMapMapModel.saveXML(): ");
            e.printStackTrace();
        }
    }
    */

