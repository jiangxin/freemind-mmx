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
/*$Id: XMLElementAdapter.java,v 1.4.14.1 2004-10-17 23:00:08 dpolivaev Exp $*/

package freemind.modes;

import freemind.extensions.PermanentNodeHook;
import freemind.main.XMLElement;
import freemind.main.FreeMindMain;
import freemind.main.Tools;

import java.awt.Font;
import java.io.File;
import java.util.Vector;
import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;

public abstract class XMLElementAdapter extends XMLElement {

   private Object           userObject = null;
   private FreeMindMain     frame;
   private NodeAdapter      mapChild   = null;

   //   Font attributes

   private String fontName; 
   private int    fontStyle = 0;
   private int    fontSize = 0;

   //   Icon attributes

   private String iconName; 

    // arrow link attributes:
    protected Vector ArrowLinkAdapters;
    protected HashMap /* id -> target */  IDToTarget;

   //   Overhead methods

   public XMLElementAdapter(FreeMindMain frame) {
      this.frame = frame; 
      this.ArrowLinkAdapters = new Vector();
      this.IDToTarget = new HashMap();
   }

    protected XMLElementAdapter(FreeMindMain frame, Vector ArrowLinkAdapters, HashMap IDToTarget) {
        this.frame = frame; 
        this.ArrowLinkAdapters = ArrowLinkAdapters;
        this.IDToTarget = IDToTarget;
    }

    /** abstract method to create elements of my type (factory).*/
    abstract protected XMLElement  createAnotherElement();
    abstract protected NodeAdapter createNodeAdapter(FreeMindMain     frame);
    abstract protected EdgeAdapter createEdgeAdapter(NodeAdapter node, FreeMindMain frame);
    abstract protected CloudAdapter createCloudAdapter(NodeAdapter node, FreeMindMain frame);
    abstract protected ArrowLinkAdapter createArrowLinkAdapter(NodeAdapter source, NodeAdapter target, FreeMindMain frame);
    
    protected FreeMindMain getFrame() {
        return frame;
    }

   public Object getUserObject() {
      return userObject; }

   public NodeAdapter getMapChild() {
      return mapChild; }

   //   Real parsing methods

   public void setName(String name)  {
		super.setName(name);
		// Create user object based on name
		if (name.equals("node")) {
			userObject = createNodeAdapter(frame);
		} else if (name.equals("edge")) {
			userObject = createEdgeAdapter(null, frame);
		} else if (name.equals("cloud")) {
			userObject = createCloudAdapter(null, frame);
		} else if (name.equals("arrowlink")) {
			userObject = createArrowLinkAdapter(null, null, frame);
		} else if (name.equals("font")) {
			userObject = null;
		} else if (name.equals("map")) {
			userObject = null;
		} else if (name.equals("icon")) {
			userObject = null;
		} else if (name.equals("hook")) {
			// we gather the xml element and send it to the hook after completion.
			userObject = new XMLElement();
		} else {
			userObject = new XMLElement(); // for childs of hooks
		}
   }

   public void addChild(XMLElement child) {
      if (getName().equals("map")) {
         mapChild = (NodeAdapter)child.getUserObject();
         return; }
      if( userObject instanceof XMLElement ) {
      	 //((XMLElement) userObject).addChild(child);
      	 super.addChild(child);
      	 return;
      }
      if (userObject instanceof NodeAdapter) {
         NodeAdapter node = (NodeAdapter)userObject;
         if (child.getUserObject() instanceof NodeAdapter) {
            node.insert((NodeAdapter)child.getUserObject(),
                        -1); } // to the end without preferable... (PN)
                     // node.getRealChildCount()); }
         else if (child.getUserObject() instanceof EdgeAdapter) {
            EdgeAdapter edge = (EdgeAdapter)child.getUserObject();
            edge.setTarget(node);
            node.setEdge(edge); }
         else if (child.getUserObject() instanceof CloudAdapter) {
            CloudAdapter cloud = (CloudAdapter)child.getUserObject();
            cloud.setTarget(node);
            node.setCloud(cloud); }
         else if (child.getUserObject() instanceof ArrowLinkAdapter) {
            ArrowLinkAdapter arrowLink = (ArrowLinkAdapter)child.getUserObject();
            arrowLink.setSource(node);
            // annotate this link: (later processed by caller.).
            //System.out.println("arrowLink="+arrowLink);
            ArrowLinkAdapters.add(arrowLink);
         }
         else if (child.getName().equals("font")) {
            node.setFont((Font)child.getUserObject()); }
         else if (child.getName().equals("icon")) {
             node.addIcon((MindIcon)child.getUserObject()); }
         else if (child.getName().equals("hook")) {
         	 XMLElement xml = (XMLElement) child/*.getUserObject()*/;
             try {
             String loadName = (String)xml.getAttribute("NAME");
			 loadName=loadName.replace('/', File.separatorChar);
			 /* The next code snippet is an exception. Normally, hooks 
			  * have to be created via the ModeController. 
			  * DO NOT COPY. */
 			 PermanentNodeHook hook = (PermanentNodeHook) frame.getHookFactory().createNodeHook(loadName);
 			 hook.loadFrom(xml);
 			 node.addHook(hook);
             } catch(Exception e) {
                 e.printStackTrace();
             }
 		 }
      }
   }

public void setAttribute(String name, Object value) {
      // We take advantage of precondition that value != null.
      String sValue = value.toString();
      if (ignoreCase) {
         name = name.toUpperCase(); }
	  if(userObject instanceof XMLElement) {
		//((XMLElement) userObject).setAttribute(name, value);
		super.setAttribute(name, value); // and to myself, as I am also an xml element.
		return;
	  }

      if (userObject instanceof NodeAdapter) {
         // 
         NodeAdapter node = (NodeAdapter)userObject;
         if (name.equals("TEXT")) {
            node.setUserObject(sValue); }
         else if (name.equals("FOLDED")) {
            if (sValue.equals("true")) {
               node.setFolded(true); }}
         else if (name.equals("POSITION")) {
             // fc, 17.12.2003: Remove the left/right bug.
             node.setLeft(sValue.equals("left")); }
         else if (name.equals("COLOR")) {
            if (sValue.length() == 7) {
               node.setColor(Tools.xmlToColor(sValue)); }}
         else if (name.equals("BACKGROUND_COLOR")) {
            if (sValue.length() == 7) {
               node.setBackgroundColor(Tools.xmlToColor(sValue)); }}
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

      if (userObject instanceof EdgeAdapter) {
         EdgeAdapter edge = (EdgeAdapter)userObject;
         if (name.equals("STYLE")) {
	    edge.setStyle(sValue); }
         else if (name.equals("COLOR")) {
	    edge.setColor(Tools.xmlToColor(sValue)); }
         else if (name.equals("WIDTH")) {
            if (sValue.equals("thin")) {
               edge.setWidth(EdgeAdapter.WIDTH_THIN); }
            else {
               edge.setWidth(Integer.parseInt(sValue)); }}
         return; }

      if (userObject instanceof CloudAdapter) {
         CloudAdapter cloud = (CloudAdapter)userObject;
         if (name.equals("STYLE")) {
	    cloud.setStyle(sValue); }
         else if (name.equals("COLOR")) {
	    cloud.setColor(Tools.xmlToColor(sValue)); }
         else if (name.equals("WIDTH")) {
               cloud.setWidth(Integer.parseInt(sValue)); 
         }
         return; }

      if (userObject instanceof ArrowLinkAdapter) {
         ArrowLinkAdapter arrowLink = (ArrowLinkAdapter)userObject;
         if (name.equals("STYLE")) {
             arrowLink.setStyle(sValue); }
         else if (name.equals("ID")) {
             arrowLink.setUniqueID(sValue); }
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
         else if (name.equals("STARTARROW")) {
             arrowLink.setStartArrow(sValue); }
         else if (name.equals("ENDARROW")) {
             arrowLink.setEndArrow(sValue); }
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
        for(int i = 0; i < ArrowLinkAdapters.size(); ++i) {
            ArrowLinkAdapter arrowLink = (ArrowLinkAdapter) ArrowLinkAdapters.get(i);
            String oldID = arrowLink.getDestinationLabel();
            NodeAdapter target = null;
            String newID = null;
            // find oldID in target list:
            if(IDToTarget.containsKey(oldID)) {
                // link present in the xml text
                target = (NodeAdapter) IDToTarget.get(oldID);
                newID = registry.getLabel(target);
            } else if(registry.getTargetForID(oldID) != null) {
                // link is already present in the map (paste).
                target = (NodeAdapter) registry.getTargetForID(oldID);
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
            NodeAdapter target = (NodeAdapter) IDToTarget.get(key);
            MindMapLinkRegistry.ID_Registered newState = registry.registerLinkTarget(target, key /* Proposed name for the target, is changed by the registry, if already present.*/);
            String newId = newState.getID();
            // and in the cutted case: 
            // search for links to this ids that have been cutted earlier:
            Vector cuttedLinks = registry.getCuttedNode(key /* old target id*/);
            for(int j=0; j < cuttedLinks.size(); ++j) {
                ArrowLinkAdapter link = (ArrowLinkAdapter) cuttedLinks.get(j);
                // repair link
                link.setTarget(target);
                link.setDestinationLabel(newId);
                // and set it:
                registry.registerLink(link);
            }
        }
    }


}
