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
/*$Id: BrowseXMLElement.java,v 1.3 2003-11-03 10:49:17 sviles Exp $*/

/*On doubling of code
 *
 *You've got basically doubled code in MindMapXMLElement.java and
 *BrowseXMLElement.java. This goes together with the split to
 *MindMapMode and BrowseMode with no reasonable common ground.
 *I am not going to fix it now, it does no great harm anyway.
 */

package freemind.modes.browsemode;

import freemind.main.XMLElement;
import freemind.main.FreeMindMain;
import freemind.main.Tools;

import java.awt.Font;

public class BrowseXMLElement extends XMLElement {

   private Object           userObject = null;
   private FreeMindMain     frame;
   private BrowseNodeModel mapChild   = null;

   //   Font attributes

   private String fontName; 
   private int    fontStyle = 0;
   private int    fontSize = 0;

   //   Overhead methods

   public BrowseXMLElement(FreeMindMain frame) {
      this.frame = frame; }

   protected XMLElement createAnotherElement() {
      // We do not need to initialize the things of XMLElement.
      return new BrowseXMLElement(frame); }

   public Object getUserObject() {
      return userObject; }

   public BrowseNodeModel getMapChild() {
      return mapChild; }

   //   Real parsing methods

   public void setName(String name)  {
      super.setName(name);
      // Create user object based on name
      if (name.equals("node")) {
         userObject = new BrowseNodeModel(frame); }
      if (name.equals("edge")) {
         userObject = new BrowseEdgeModel(null, frame); }}

   public void addChild(XMLElement child) {
      if (getName().equals("map")) {
         mapChild = (BrowseNodeModel)child.getUserObject();
         return; }
      if (userObject instanceof BrowseNodeModel) {
         BrowseNodeModel node = (BrowseNodeModel)userObject;
         if (child.getUserObject() instanceof BrowseNodeModel) {
            node.insert((BrowseNodeModel)child.getUserObject(),
                        node.getChildCount());}
         else if (child.getUserObject() instanceof BrowseEdgeModel) {
            BrowseEdgeModel edge = (BrowseEdgeModel)child.getUserObject();
            edge.setTarget(node);
            node.setEdge(edge); }
         else if (child.getName().equals("font")) {
            node.setFont((Font)child.getUserObject()); }}}

   public void setAttribute(String name, Object value) {
      // We take advantage of precondition that value != null.
      String sValue = value.toString();
      if (ignoreCase) {
         name = name.toUpperCase(); }

      if (userObject instanceof BrowseNodeModel) {
         // 
         BrowseNodeModel node = (BrowseNodeModel)userObject;
         if (name.equals("TEXT")) {
            node.setUserObject(sValue); }
         else if (name.equals("FOLDED")) {
            if (sValue.equals("true")) {
               node.setFolded(true); }}
         else if (name.equals("COLOR")) {
            if (sValue.length() == 7) {
               node.setColor(Tools.xmlToColor(sValue)); }}
         else if (name.equals("LINK")) {
            node.setLink(sValue); }
         else if (name.equals("STYLE")) {
            node.setStyle(sValue); }
         return; }

      if (userObject instanceof BrowseEdgeModel) {
         BrowseEdgeModel edge = (BrowseEdgeModel)userObject;
         if (name.equals("STYLE")) {
	    edge.setStyle(sValue); }
         else if (name.equals("COLOR")) {
	    edge.setColor(Tools.xmlToColor(sValue)); }
         else if (name.equals("WIDTH")) {
            if (sValue.equals("thin")) {
               edge.setWidth(BrowseEdgeModel.WIDTH_THIN); }
            else {
               edge.setWidth(Integer.parseInt(sValue)); }}
         return; }

      if (getName().equals("font")) {
         if (name.equals("SIZE")) {
            fontSize = Integer.parseInt(sValue); }
         else if (name.equals("NAME")) {
            fontName = sValue; }             

         // Styling
         else if (sValue.equals("true")) {
            if (name.equals("BOLD")) {
               fontStyle+=Font.BOLD; }
            else if (name.equals("ITALIC")) {
               fontStyle+=Font.ITALIC; }}}}

   protected void completeElement() {
      if (getName().equals("font")) {
         userObject =  frame.getController().getFontThroughMap
            (new Font(fontName, fontStyle, fontSize)); }}

}
