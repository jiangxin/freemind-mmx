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
/*$Id: MindMapXMLElement.java,v 1.10 2003-11-24 08:09:04 christianfoltin Exp $*/

/*On doubling of code
 *
 *You've got basically doubled code in MindMapXMLElement.java and
 *BrowseXMLElement.java. This goes together with the split to
 *MindMapMode and BrowseMode with no reasonable common ground.
 *I am not going to fix it now, it does no great harm anyway.
 */

package freemind.modes.mindmapmode;

import freemind.main.XMLElement;
import freemind.main.FreeMindMain;
import freemind.main.Tools;
import freemind.modes.MindIcon;
import freemind.modes.MindMapLinkRegistry;

import java.awt.Font;
import java.util.Vector;
import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;

public class MindMapXMLElement extends XMLElement {

   private Object           userObject = null;
   private FreeMindMain     frame;
   private MindMapNodeModel mapChild   = null;

   //   Font attributes

   private String fontName; 
   private int    fontStyle = 0;
   private int    fontSize = 0;

   //   Icon attributes

   private String iconName; 

    // arrow link attributes:
    protected Vector MindMapArrowLinkModels;
    protected HashMap /* id -> target */  IDToTarget;

   //   Overhead methods

   public MindMapXMLElement(FreeMindMain frame) {
      this.frame = frame; 
      this.MindMapArrowLinkModels = new Vector();
      this.IDToTarget = new HashMap();
   }

    protected MindMapXMLElement(FreeMindMain frame, Vector MindMapArrowLinkModels, HashMap IDToTarget) {
        this.frame = frame; 
        this.MindMapArrowLinkModels = MindMapArrowLinkModels;
        this.IDToTarget = IDToTarget;
    }

   protected XMLElement createAnotherElement() {
      // We do not need to initialize the things of XMLElement.
      return new MindMapXMLElement(frame, MindMapArrowLinkModels, IDToTarget); }

   public Object getUserObject() {
      return userObject; }

   public MindMapNodeModel getMapChild() {
      return mapChild; }

   //   Real parsing methods

   public void setName(String name)  {
      super.setName(name);
      // Create user object based on name
      if (name.equals("node")) {
         userObject = new MindMapNodeModel(frame); }
      if (name.equals("edge")) {
         userObject = new MindMapEdgeModel(null, frame); }
      if (name.equals("cloud")) {
          userObject = new MindMapCloudModel(null, frame); }
      if (name.equals("arrowlink")) {
          userObject = new MindMapArrowLinkModel(null, null, frame); }}

   public void addChild(XMLElement child) {
      if (getName().equals("map")) {
         mapChild = (MindMapNodeModel)child.getUserObject();
         return; }
      if (userObject instanceof MindMapNodeModel) {
         MindMapNodeModel node = (MindMapNodeModel)userObject;
         if (child.getUserObject() instanceof MindMapNodeModel) {
            node.insert((MindMapNodeModel)child.getUserObject(),
                        -1); } // to the end without preferable... (PN)
                     // node.getRealChildCount()); }
         else if (child.getUserObject() instanceof MindMapEdgeModel) {
            MindMapEdgeModel edge = (MindMapEdgeModel)child.getUserObject();
            edge.setTarget(node);
            node.setEdge(edge); }
         else if (child.getUserObject() instanceof MindMapCloudModel) {
            MindMapCloudModel cloud = (MindMapCloudModel)child.getUserObject();
            cloud.setTarget(node);
            node.setCloud(cloud); }
         else if (child.getUserObject() instanceof MindMapArrowLinkModel) {
            MindMapArrowLinkModel arrowLink = (MindMapArrowLinkModel)child.getUserObject();
            arrowLink.setSource(node);
            // annotate this link: (later processed by caller.).
            //System.out.println("arrowLink="+arrowLink);
            MindMapArrowLinkModels.add(arrowLink);
         }
         else if (child.getName().equals("font")) {
            node.setFont((Font)child.getUserObject()); }
         else if (child.getName().equals("icon")) {
             node.addIcon((MindIcon)child.getUserObject()); }}}

   public void setAttribute(String name, Object value) {
      // We take advantage of precondition that value != null.
      String sValue = value.toString();
      if (ignoreCase) {
         name = name.toUpperCase(); }

      if (userObject instanceof MindMapNodeModel) {
         // 
         MindMapNodeModel node = (MindMapNodeModel)userObject;
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
         else if (name.equals("ID")) {
             // do not set label but annotate in list:
             //System.out.println("(sValue, node) = " + sValue + ", "+  node);
             IDToTarget.put(sValue, node);
         }
         return; }

      if (userObject instanceof MindMapEdgeModel) {
         MindMapEdgeModel edge = (MindMapEdgeModel)userObject;
         if (name.equals("STYLE")) {
	    edge.setStyle(sValue); }
         else if (name.equals("COLOR")) {
	    edge.setColor(Tools.xmlToColor(sValue)); }
         else if (name.equals("WIDTH")) {
            if (sValue.equals("thin")) {
               edge.setWidth(MindMapEdgeModel.WIDTH_THIN); }
            else {
               edge.setWidth(Integer.parseInt(sValue)); }}
         return; }

      if (userObject instanceof MindMapCloudModel) {
         MindMapCloudModel cloud = (MindMapCloudModel)userObject;
         if (name.equals("STYLE")) {
	    cloud.setStyle(sValue); }
         else if (name.equals("COLOR")) {
	    cloud.setColor(Tools.xmlToColor(sValue)); }
         else if (name.equals("WIDTH")) {
               cloud.setWidth(Integer.parseInt(sValue)); 
         }
         return; }

      if (userObject instanceof MindMapArrowLinkModel) {
         MindMapArrowLinkModel arrowLink = (MindMapArrowLinkModel)userObject;
         if (name.equals("STYLE")) {
             arrowLink.setStyle(sValue); }
         else if (name.equals("COLOR")) {
             arrowLink.setColor(Tools.xmlToColor(sValue)); }
         else if (name.equals("DESTINATION")) {
             arrowLink.setDestinationLabel(sValue); }
         else if (name.equals("REFERENCETEXT")) {
             arrowLink.setReferenceText((sValue)); }
         else if (name.equals("STARTINCLINATION")) {
             arrowLink.setStartInclination(Tools.xmlToPoint(sValue)); }
         else if (name.equals("ENDINCLINATION")) {
             arrowLink.setEndInclination(Tools.xmlToPoint(sValue)); }
         else if (name.equals("STARTHASARROW")) {
             arrowLink.setStartArrow(Tools.xmlToBoolean(sValue)); }
         else if (name.equals("ENDHASARROW")) {
             arrowLink.setEndArrow(Tools.xmlToBoolean(sValue)); }
         else if (name.equals("WIDTH")) {
             arrowLink.setWidth(Integer.parseInt(sValue)); 
         }
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
               fontStyle+=Font.ITALIC; }}}
      /* icons */
      if (getName().equals("icon")) {
         if (name.equals("BUILTIN")) {
            iconName = sValue; } 
      }
  }

   protected void completeElement() {
      if (getName().equals("font")) {
         userObject =  frame.getController().getFontThroughMap
            (new Font(fontName, fontStyle, fontSize)); }
      /* icons */
            if (getName().equals("icon")) {
         userObject =  new MindIcon(iconName); }
   }

    /** Completes the links within the map. They are registered in the registry.*/
    public void processUnfinishedLinks(MindMapLinkRegistry registry) {
        // add labels to the nodes:
        setIDs(IDToTarget, registry);
        // complete arrow links with right labels:
        for(int i = 0; i < MindMapArrowLinkModels.size(); ++i) {
            MindMapArrowLinkModel arrowLink = (MindMapArrowLinkModel) MindMapArrowLinkModels.get(i);
            String oldID = arrowLink.getDestinationLabel();
            MindMapNodeModel target = null;
            String newID = null;
            // find oldID in target list:
            if(IDToTarget.containsKey(oldID)) {
                // link present in the xml text
                target = (MindMapNodeModel) IDToTarget.get(oldID);
                newID = registry.getLabel(target);
            } else if(registry.getTargetForID(oldID) != null) {
                // link is already present in the map (paste).
                target = (MindMapNodeModel) registry.getTargetForID(oldID);
                if(target == null) {
                    // link target is in nowhere-land
                    System.err.println("Cannot find the label " + oldID + " in the map. The link "+arrowLink+" is not restored.");
                    continue;
                }
                newID = registry.getLabel(target);
                if( ! newID.equals(oldID) ) {
                    System.err.println("Servere internal error. Looked for id " + oldID + " but found "+newID + " in the node " + target+".");
                    continue;
                }
            } else {                
                // link target is in nowhere-land
                System.err.println("Cannot find the label " + oldID + " in the map. The link "+arrowLink+" is not restored.");
                continue;
            }
            // set the new ID:
            arrowLink.setDestinationLabel(newID);
            // set the target:
            arrowLink.setTarget(target);
            // add the arrowLink:
            //System.out.println("Node = " + target+ ", oldID="+oldID+", newID="+newID);
            registry.registerLink(arrowLink);

        }
    }


    /**Recursive method to set the ids of the nodes.*/
    private void setIDs(HashMap IDToTarget, MindMapLinkRegistry registry) {
        for(Iterator i = IDToTarget.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            MindMapNodeModel target = (MindMapNodeModel) IDToTarget.get(key);
            registry.registerLinkTarget(target, key /* Proposed name for the target, is changed by the registry, if already present.*/);
        }
    }


}
