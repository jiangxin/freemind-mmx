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
/* $Id: XMLElementAdapter.java,v 1.4.14.15.2.11 2007-03-20 22:01:41 christianfoltin Exp $ */

package freemind.modes;

import java.awt.Font;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import freemind.extensions.PermanentNodeHook;
import freemind.extensions.PermanentNodeHookSubstituteUnknown;
import freemind.main.FreeMindMain;
import freemind.main.Tools;
import freemind.main.XMLElement;
import freemind.modes.attributes.Attribute;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.attributes.AttributeTableLayoutModel;

public abstract class XMLElementAdapter extends XMLElement {


    // Logging:
	protected static java.util.logging.Logger logger;

   private Object           userObject = null;
   protected FreeMindMain     frame;
   private NodeAdapter      mapChild   = null;
   private HashMap 		  nodeAttributes = new HashMap();

   //   Font attributes

   private String fontName;
   private int    fontStyle = 0;
   private int    fontSize = 0;

   //   Icon attributes

   private String iconName;

    // arrow link attributes:
    protected Vector ArrowLinkAdapters;
    protected HashMap /* id -> target */  IDToTarget;
    public static final String XML_NODE_TEXT = "TEXT";
    public static final String XML_NODE = "node";
    public static final String XML_NODE_ATTRIBUTE = "attribute";
    public static final String XML_NODE_ATTRIBUTE_LAYOUT = "attribute_layout";
    public static final String XML_NODE_ATTRIBUTE_REGISTRY = "attribute_registry";
    public static final String XML_NODE_REGISTERED_ATTRIBUTE_NAME = "attribute_name";
    public static final String XML_NODE_REGISTERED_ATTRIBUTE_VALUE = "attribute_value";
    //public static final String XML_NODE_CLASS_PREFIX = XML_NODE+"_";
    public static final String XML_NODE_CLASS = "AA_NODE_CLASS";
    public static final String XML_NODE_ADDITIONAL_INFO = "ADDITIONAL_INFO";
    public static final String XML_NODE_ENCRYPTED_CONTENT = "ENCRYPTED_CONTENT";
    public static final String XML_NODE_HISTORY_CREATED_AT = "CREATED";
    public static final String XML_NODE_HISTORY_LAST_MODIFIED_AT = "MODIFIED";

	public static final String XML_NODE_XHTML_TYPE_TAG = "TYPE";
	public static final String XML_NODE_XHTML_TYPE_NODE = "NODE";
	public static final String XML_NODE_XHTML_TYPE_NOTE = "NOTE";

    private String attributeName;

    private String attributeValue;

    private int attributeNameWidth = AttributeTableLayoutModel.DEFAULT_COLUMN_WIDTH;

    private int attributeValueWidth = AttributeTableLayoutModel.DEFAULT_COLUMN_WIDTH;

	protected final ModeController mModeController;

   //   Overhead methods

   public XMLElementAdapter(ModeController modeController) {
	   this(modeController, new Vector(), new HashMap());
   }

    protected XMLElementAdapter(ModeController modeController, Vector ArrowLinkAdapters, HashMap IDToTarget) {
        this.mModeController = modeController;
        this.frame = modeController.getFrame();
        this.ArrowLinkAdapters = ArrowLinkAdapters;
        this.IDToTarget = IDToTarget;
        if(logger==null) {
        	logger = frame.getLogger(this.getClass().getName());
        }
    }

    /** abstract method to create elements of my type (factory).*/
    abstract protected XMLElement  createAnotherElement();
    abstract protected NodeAdapter createNodeAdapter(FreeMindMain     frame, String nodeClass);
    abstract protected EdgeAdapter createEdgeAdapter(NodeAdapter node, FreeMindMain frame);
    abstract protected CloudAdapter createCloudAdapter(NodeAdapter node, FreeMindMain frame);
    abstract protected ArrowLinkAdapter createArrowLinkAdapter(NodeAdapter source, NodeAdapter target, FreeMindMain frame);
    abstract protected NodeAdapter createEncryptedNode(String additionalInfo);



    protected FreeMindMain getFrame() {
        return frame;
    }

   public Object getUserObject() {
      return userObject; }

   protected void setUserObject(Object obj){
	   userObject = obj;
   }

   public NodeAdapter getMapChild() {
      return mapChild; }

   //   Real parsing methods

   public void setName(String name)  {
		super.setName(name);
		// Create user object based on name
		if (name.equals(XML_NODE)) {
			userObject = createNodeAdapter(frame, null);
			nodeAttributes.clear();
		} else if (name.equals("edge")) {
			userObject = createEdgeAdapter(null, frame);
		} else if (name.equals("cloud")) {
			userObject = createCloudAdapter(null, frame);
		} else if (name.equals("arrowlink")) {
			userObject = createArrowLinkAdapter(null, null, frame);
		} else if (name.equals("font")) {
			userObject = null;
		}  else if (name.equals(XML_NODE_ATTRIBUTE)) {
			userObject = null;
		}  else if (name.equals(XML_NODE_ATTRIBUTE_LAYOUT)) {
			userObject = null;
		} else if (name.equals("map")) {
			userObject = null;
		} else if (name.equals(XML_NODE_ATTRIBUTE_REGISTRY)) {
			userObject = null;
		} else if (name.equals(XML_NODE_REGISTERED_ATTRIBUTE_NAME)) {
			userObject = null;
		} else if (name.equals(XML_NODE_REGISTERED_ATTRIBUTE_VALUE)) {
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
         else if (child.getName().equals(XML_NODE_ATTRIBUTE)) {
             node.getAttributes().addRowNoUndo((Attribute)child.getUserObject()); }
         else if (child.getName().equals(XML_NODE_ATTRIBUTE_LAYOUT)) {
             AttributeTableLayoutModel layout = node.getAttributes().getLayout();
             layout.setColumnWidth(0, ((XMLElementAdapter)child).attributeNameWidth);
             layout.setColumnWidth(1, ((XMLElementAdapter)child).attributeValueWidth);
             }
          else if (child.getName().equals("icon")) {
             node.addIcon((MindIcon)child.getUserObject()); }
          else if (child.getName().equals(XML_NODE_XHTML_CONTENT_TAG)) {
				String xmlText = ((XMLElement) child).getContent();
				Object typeAttribute = child
						.getAttribute(XML_NODE_XHTML_TYPE_TAG);
				if (typeAttribute == null
						|| XML_NODE_XHTML_TYPE_NODE.equals(typeAttribute)) {
					// output:
					logger.finest("Setting node html content to:" + xmlText);
					node.setXmlText(xmlText);
				} else {
					logger.finest("Setting note html content to:" + xmlText);
					node.setXmlNoteText(xmlText);
				}
			}
         else if (child.getName().equals("hook")) {
         	 XMLElement xml = (XMLElement) child/*.getUserObject()*/;
             String loadName = (String)xml.getAttribute("NAME");
 			 PermanentNodeHook hook = null;
             try {
				 //loadName=loadName.replace('/', File.separatorChar);
				 /* The next code snippet is an exception. Normally, hooks
				  * have to be created via the ModeController.
				  * DO NOT COPY. */
                hook = (PermanentNodeHook) mModeController.getHookFactory().createNodeHook(loadName);
                // this is a bad hack. Don't make use of this data unless
                // you know exactly what you are doing.
                hook.setNode(node);
             } catch(Exception e) {
                 freemind.main.Resources.getInstance().logException(e);
                 hook = new PermanentNodeHookSubstituteUnknown(loadName);
             }
 			 hook.loadFrom(xml);
 			 node.addHook(hook);
 		 }
         return;
      }
      if(child instanceof XMLElementAdapter
              && getName().equals(XML_NODE_REGISTERED_ATTRIBUTE_NAME)
              && child.getName().equals(XML_NODE_REGISTERED_ATTRIBUTE_VALUE)){
          Attribute attribute = new Attribute(attributeName, ((XMLElementAdapter)child).attributeValue);
        AttributeRegistry r = getMap().getRegistry().getAttributes();
          r.registry(attribute);
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
         userObject  = setNodeAttribute(name, sValue, node);
     	nodeAttributes.put(name, sValue);
        return; }

      if (userObject instanceof EdgeAdapter) {
         EdgeAdapter edge = (EdgeAdapter)userObject;
         if (name.equals("STYLE")) {
	    edge.setStyle(sValue); }
         else if (name.equals("COLOR")) {
	    edge.setColor(Tools.xmlToColor(sValue)); }
         else if (name.equals("WIDTH")) {
            if (sValue.equals(EdgeAdapter.EDGE_WIDTH_THIN_STRING)) {
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
      /* attributes */
      else if (getName().equals(XML_NODE_ATTRIBUTE)) {
          if (name.equals("NAME")) {
              attributeName = sValue; }
          else if (name.equals("VALUE")) {
              attributeValue = sValue; }
       }
      else if (getName().equals(XML_NODE_ATTRIBUTE_LAYOUT)) {
          if (name.equals("NAME_WIDTH")) {
              attributeNameWidth = Integer.parseInt(sValue); }
          else if (name.equals("VALUE_WIDTH")) {
              attributeValueWidth = Integer.parseInt(sValue); }
       }
      else if (getName().equals(XML_NODE_ATTRIBUTE_REGISTRY)) {
          if (name.equals("RESTRICTED")) {
              getMap().getRegistry().getAttributes().setRestricted(true);
          }
          if (name.equals("SHOW_ATTRIBUTES")) {
              mModeController.getController().setAttributeViewType(getMap(), sValue);
          }
          if (name.equals("FONT_SIZE")) {
              try {
                  int size = Integer.parseInt(sValue);
                  getMap().getRegistry().getAttributes().setFontSize(size);
              }
              catch (NumberFormatException ex){
              }
          }
      }
      else if (getName().equals(XML_NODE_REGISTERED_ATTRIBUTE_NAME)) {
          if (name.equals("NAME")) {
              attributeName = sValue;
              getMap().getRegistry().getAttributes().registry(attributeName);
          }
          if (name.equals("VISIBLE")) {
              getMap().getRegistry().getAttributes().getElement(attributeName).setVisibility(true);
          }
          if (name.equals("RESTRICTED")) {
              getMap().getRegistry().getAttributes().getElement(attributeName).setRestriction(true);
          }
      }
      else if (getName().equals(XML_NODE_REGISTERED_ATTRIBUTE_VALUE)) {
          if (name.equals("VALUE")) {
              attributeValue = sValue;
          }
      }
  }

   private NodeAdapter setNodeAttribute(String name, String sValue, NodeAdapter node) {
     if (name.equals(XML_NODE_TEXT)) {
			logger.finest("Setting node text content to:" + sValue);
	    node.setUserObject(sValue); }
	 else if (name.equals(XML_NODE_ENCRYPTED_CONTENT)) {
	     // we change the node implementation to EncryptedMindMapNode.
	     node = createEncryptedNode(sValue);
	 } else if (name.equals(XML_NODE_HISTORY_CREATED_AT)) {
	     if(node.getHistoryInformation()==null) {
	     	node.setHistoryInformation(new HistoryInformation());
	     }
	     node.getHistoryInformation().setCreatedAt(Tools.xmlToDate(sValue));
	 }
	 else if (name.equals(XML_NODE_HISTORY_LAST_MODIFIED_AT)) {
	     if(node.getHistoryInformation()==null) {
	     	node.setHistoryInformation(new HistoryInformation());
	     }
	     node.getHistoryInformation().setLastModifiedAt(Tools.xmlToDate(sValue));
	 }
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
	 else if (name.equals("VSHIFT")) {
	 	node.setShiftY(Integer.parseInt(sValue));
	 }
	 else if (name.equals("VGAP")) {
	   	node.setVGap(Integer.parseInt(sValue));
	 }
	 else if (name.equals("HGAP")) {
	   	node.setHGap(Integer.parseInt(sValue));
	 }
     return node;
}

	/** Sets all attributes that were formely applied to the current userObject
	 *  to a given (new) node. Thus, the instance of a node can be changed after
	 *  the creation. (At the moment, relevant for encrypted nodes).
	 */
	protected void copyAttributesToNode(NodeAdapter node) {
		// reactivate all settings from nodeAttributes:
        for (Iterator i = nodeAttributes.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            //to avoid self reference:
            setNodeAttribute(key, (String) nodeAttributes.get(key),
                    node);
        }
	}

    protected void completeElement() {
		if (getName().equals(XML_NODE)) {
			// unify map child behaviour:
			if(mapChild==null) {
				mapChild = (NodeAdapter) userObject;
			}
		}
    	if (getName().equals("font")) {
         userObject =  frame.getController().getFontThroughMap
            (new Font(fontName, fontStyle, fontSize)); }
      /* icons */
            if (getName().equals("icon")) {
         userObject =  MindIcon.factory(iconName); }
      /* attributes */
      if (getName().equals(XML_NODE_ATTRIBUTE)) {
          userObject = new Attribute(attributeName, attributeValue);}
   }

    /** Completes the links within the getMap(). They are registered in the registry.*/
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
                // link is already present in the getMap() (paste).
                target = (NodeAdapter) registry.getTargetForID(oldID);
                if(target == null) {
                    // link target is in nowhere-land
                    System.err.println("Cannot find the label " + oldID + " in the getMap(). The link "+arrowLink+" is not restored.");
                    continue;
                }
                newID = registry.getLabel(target);
                if( ! newID.equals(oldID) ) {
                    System.err.println("Servere internal error. Looked for id " + oldID + " but found "+newID + " in the node " + target+".");
                    continue;
                }
            } else {
                // link target is in nowhere-land
                System.err.println("Cannot find the label " + oldID + " in the getMap(). The link "+arrowLink+" is not restored.");
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


    protected MindMap getMap() {
        return mModeController.getMap();
    }
}
