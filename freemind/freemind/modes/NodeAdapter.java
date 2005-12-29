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
/*$Id: NodeAdapter.java,v 1.20.16.10.2.4.2.8 2005-12-29 20:21:30 dpolivaev Exp $*/

package freemind.modes;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.event.EventListenerList;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import freemind.controller.Controller;
import freemind.controller.filter.Filter;
import freemind.controller.filter.FilterInfo;
import freemind.extensions.NodeHook;
import freemind.extensions.PermanentNodeHook;
import freemind.main.FreeMind;
import freemind.main.FreeMindMain;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.main.XMLElement;
import freemind.modes.attributes.NodeAttributeTableModel;
import freemind.view.mindmapview.NodeView;

/**
 * This class represents a single Node of a Tree. It contains direct handles 
 * to its parent and children and to its view.
 */
public abstract class NodeAdapter implements MindMapNode {
	
    final static int SHIFT = -2;//height of the vertical shift between node and its closest child
    public final static int HGAP = 20;//width of the horizontal gap that contains the edges
    public final static int VGAP = 3;//height of the vertical gap between nodes

    private HashSet activatedHooks;
	private List hooks;
	protected Object userObject = "no text";
    private String link = null; //Change this to vector in future for full graph support
    private HashMap toolTip = null; // lazy, fc, 30.6.2005

    //these Attributes have default values, so it can be useful to directly access them in
    //the save() method instead of using getXXX(). This way the stored file is smaller and looks better.
    //(if the default is used, it is not stored) Look at mindmapmode for an example.
    protected String style;
    /**stores the icons associated with this node.*/
    protected Vector/*<MindIcon>*/ icons = null; // lazy, fc, 30.6.2005
    
    protected TreeMap /* of String to MindIcon s*/ stateIcons = null; // lazy, fc, 30.6.2005
//     /**stores the label associated with this node:*/
//     protected String mLabel;
    /** parameters of an eventually associated cloud*/
    protected MindMapCloud cloud;

    protected Color color;
    protected Color backgroundColor;
    protected boolean folded;
    private Tools.BooleanHolder left;

	private int vGap = AUTO;
	private int hGap = HGAP;
    private int shiftY = 0;

    protected List children;
    private MindMapNode preferredChild; 

    protected Font font;
    protected boolean underlined = false;

    private FilterInfo filterInfo = new FilterInfo();
    
    
    private MindMapNode parent;
    private MindMapEdge edge;//the edge which leads to this node, only root has none
    //In future it has to hold more than one view, maybe with a Vector in which the index specifies
    //the MapView which contains the NodeViews
    private NodeView viewer = null;
    private FreeMindMain frame;
    private static final boolean ALLOWSCHILDREN = true;
    private static final boolean ISLEAF = false; //all nodes may have children
    
    private HistoryInformation historyInformation = null;
	// Logging: 
    static protected java.util.logging.Logger logger;
    private MindMap map = null;
    private NodeAttributeTableModel attributes;


    //
    // Constructors
    //

    protected NodeAdapter(FreeMindMain frame, MindMap map) {
		this(null, frame, map);
    }

    protected NodeAdapter(Object userObject, FreeMindMain frame, MindMap map) {
		this.userObject = userObject;
		this.frame = frame;
		hooks = null; // lazy, fc, 30.6.2005.
		activatedHooks = null; //lazy, fc, 30.6.2005
		if(logger == null)
			logger = frame.getLogger(this.getClass().getName());
		// create creation time:
		setHistoryInformation(new HistoryInformation());
		MindMapNode parentNode = getParentNode();
		this.map = map; 
		this.attributes = new NodeAttributeTableModel(this);
    }

    /**
     * @param map
     */
    public void setMap(MindMap map) {
        this.map = map; 
        map.getRegistry().registrySubtree(this);
    }
    /**
     *
     */

    public String getText() {
        return toString();
    }
    /**
     *
     */

    public void setText(String text) {
        setUserObject(text);
    }

    public String getLink() {
 	return link;
    }
    
    public String getShortText(ModeController controller) {
	    String adaptedText = toString();
        adaptedText = adaptedText.replaceAll("<html>", "");
        if (adaptedText.length() > 40)
            adaptedText = adaptedText.substring(0, 40) + " ...";
        return adaptedText;
    }
    
    public void setLink(String link) {
 	this.link = link;
    }

    public FilterInfo getFilterInfo(){
        return filterInfo;
    }

    public FreeMindMain getFrame() {
	return frame;
    }

    //
    // Interface MindMapNode
    //

    //
    // get/set methods
    //

    public NodeView getViewer() {
	return viewer;
    }
    
    public void setViewer( NodeView viewer ) {
        if(this.viewer != null)
            fireNodeViewRemoved();
        this.viewer = viewer;
        if(this.viewer != null)
            fireNodeViewCreated();
    }

    /** Creates the TreePath recursively */
    public TreePath getPath() {
	Vector pathVector = new Vector();
	TreePath treePath;
	this.addToPathVector( pathVector );
	treePath = new TreePath( pathVector.toArray() );
	return treePath;
    }

    public MindMapEdge getEdge() {
	return edge;
    }

    public void setEdge( MindMapEdge edge ) {
	this.edge = edge;
    }

    public MindMapCloud getCloud() {
        return cloud;
    }

    public void setCloud( MindMapCloud cloud ) {
    	// Take care to keep the calculated iterative levels consistent
		if (cloud != null && this.cloud == null) {
			changeChildCloudIterativeLevels(1);
		}
		else if (cloud == null && this.cloud != null) {
			changeChildCloudIterativeLevels(-1);
		}
        this.cloud = cloud;
    }

	/**
	*  Correct iterative level values of children
	*/
	private void changeChildCloudIterativeLevels(int deltaLevel) {
		for (ListIterator e = childrenUnfolded(); e.hasNext(); ) {
			NodeAdapter childNode = (NodeAdapter)e.next();
			MindMapCloud childCloud = childNode.getCloud();
			if (childCloud != null) {
				childCloud.changeIterativeLevel(deltaLevel);
			}
			childNode.changeChildCloudIterativeLevels(deltaLevel); 
		}
	}

    /**A Node-Style like MindMapNode.STYLE_FORK or MindMapNode.STYLE_BUBBLE*/
	public String getStyle() {
    	String returnedString = style; /* Style string returned */
    	if (style==null) {
    		if (this.isRoot()) {
    			returnedString=getFrame().getProperty(FreeMind.RESOURCES_ROOT_NODE_STYLE);
    		}
    		else{ 
    			String stdstyle = getFrame().getProperty(FreeMind.RESOURCES_NODE_STYLE);
    			if( stdstyle.equals(MindMapNode.STYLE_AS_PARENT)){
    				returnedString=getParentNode().getStyle();
    			}
    			else{
    				returnedString = stdstyle;
    			}
    		}
    	}
    	else if (this.isRoot() && style.equals(MindMapNode.STYLE_AS_PARENT)) {
    		returnedString =  getFrame().getProperty(FreeMind.RESOURCES_ROOT_NODE_STYLE);
     	}
     	else if( style.equals(MindMapNode.STYLE_AS_PARENT)){
    		returnedString = getParentNode().getStyle();
     	}
    	
    	// Handle the combined node style
     	if (returnedString.equals(MindMapNode.STYLE_COMBINED))
     	{
     		if (this.isFolded()){
     			return MindMapNode.STYLE_BUBBLE;
     		}
     		else{
    			return MindMapNode.STYLE_FORK;
     		}
     	}
    	return returnedString;
	}
	
	

    /**The Foreground/Font Color*/
    public Color getColor() {
       return color; }

	
    //////
    // The set methods. I'm not sure if they should be here or in the implementing class.
    /////

    public void setStyle(String style) {
	this.style = style;
    }

    public void setColor(Color color) {
	this.color = color;
    }

    //fc, 24.2.2004: background color:
    public Color getBackgroundColor(           ) { return backgroundColor; };
    public void  setBackgroundColor(Color color) { this.backgroundColor = color; };

    //   
    //  font handling
    // 

    
    //   Remark to setBold and setItalic implemetation
    //
    // Using deriveFont() is a bad idea, because it does not really choose
    // the appropriate face. For example, instead of choosing face
    // "Arial Bold", it derives the bold face from "Arial".

    // Node holds font only in the case that the font is not default.

    public void estabilishOwnFont() {
       font = (font != null) ? font : getFrame().getController().getDefaultFont(); }

    public void setBold(boolean bold) {
	if (bold != isBold()) {
           toggleBold(); }}

    public void toggleBold() {
       estabilishOwnFont();
       setFont(getFrame().getController().getFontThroughMap
               (new Font(font.getFamily(), font.getStyle() ^ Font.BOLD, font.getSize()))); }

    public void setItalic(boolean italic) {
        if (italic != isItalic()) {
           toggleItalic(); }}

    public void toggleItalic() {
       estabilishOwnFont();
       setFont(getFrame().getController().getFontThroughMap
               (new Font(font.getFamily(), font.getStyle() ^ Font.ITALIC, font.getSize()))); }

    public void setUnderlined(boolean underlined) {
       this.underlined = underlined; }

    public void setFont(Font font) {
       this.font = font; }

    public MindMapNode getParentNode() {
       return parent; }

    public void setFontSize(int fontSize) {
       estabilishOwnFont();
       setFont(getFrame().getController().getFontThroughMap
               (new Font(font.getFamily(), font.getStyle(), fontSize))); }

    public Font getFont() {
       return font; }

    public String getFontSize(){
       if (getFont() != null){
          return new Integer (getFont().getSize()).toString();}
       else {
          return getFrame().getProperty("defaultfontsize");}
    }

    public String getFontFamilyName(){
       if (getFont() != null){
          return getFont().getFamily();}
       else {
          return getFrame().getProperty("defaultfont");}
    }

    public boolean isBold() {
       return font != null ? font.isBold() : false; }

    public boolean isItalic() {
       return font != null ? font.isItalic() : false; }

    public boolean isUnderlined() {        // not implemented
       return underlined; }

    public boolean isFolded() {
       return folded; }

    // fc, 24.9.2003:
    public List getIcons() {
    		if(icons==null)
    			return Collections.EMPTY_LIST;
    		return icons;
    	}

    public MindMap getMap() {
        return map;
    }
    public void   addIcon(MindIcon _icon) {
    		createIcons();
    		icons.add(_icon); 
            getMap().getRegistry().addIcon(_icon);
    	}

    /** @return returns the number of remaining icons. */
	public int removeLastIcon() {
		createIcons();
		if (icons.size() > 0)
			icons.setSize(icons.size() - 1);
		int returnSize = icons.size();
		if(returnSize==0) {
			icons = null;
		}
		return returnSize;
	};

    // end, fc, 24.9.2003

//     public     String getLabel() { return mLabel; }

//     public     void setLabel(String newLabel) { mLabel = newLabel; /* bad hack: registry fragen.*/ };

//     public Vector/* of NodeLinkStruct*/ getReferences() { return mNodeLinkVector; };

//     public void removeReferenceAt(int i) {
//         if(mNodeLinkVector.size() > i) {
//             mNodeLinkVector.removeElementAt(i);
//         } else {
//             /* exception. */
//         }
//     }
    
//     public     void addReference(MindMapLink mindMapLink) { mNodeLinkVector.add(mindMapLink); };


    /**
     *  True iff one of node's <i>strict</i> descendants is folded. A node N
     *  is not its strict descendant - the fact that node itself is folded
     *  is not sufficient to return true.
     */
    public boolean hasFoldedStrictDescendant() {
       
       for (ListIterator e = childrenUnfolded(); e.hasNext(); ) {
          NodeAdapter child = (NodeAdapter)e.next();
          if (child.isFolded() || child.hasFoldedStrictDescendant()) {
             return true; }}

	return false;
    }

    public void setFolded(boolean folded) {
	this.folded = folded;
    }

    protected MindMapNode basicCopy(MindMap map) {
       return null; }
	
    public MindMapNode shallowCopy() {
       MindMapNode copy = basicCopy(getMap());
       copy.setColor(getColor());
       copy.setFont(getFont());
       copy.setLink(getLink());
       if(isLeft() != null)
           copy.setLeft(isLeft().getValue());
       List icons = getIcons();
       for(int i = 0; i < icons.size(); ++i) {
           copy.addIcon((MindIcon) icons.get(i));
       }
       return copy; }

    //
    // other
    //

    public String toString() {
	String string="";
    if (userObject!=null) {
        string = userObject.toString();
    }
// (PN) %%%
// Why? If because of presentation, this level shall be self responsible... 
// Model shall NOT change the real data!!!
//
//	if (string.equals("")) {
//	    string = "   ";
//	}
	return string;
    }

    /**
     * Returns whether the argument is parent
     * or parent of one of the grandpa's of this node.
     * (transitive)
     */
    public boolean isDescendantOf(MindMapNode node) {
	if(this.isRoot())
	    return false;
	else if (node == getParentNode())
	    return true;
	else
	    return getParentNode().isDescendantOf(node);
    }	    
	 
    public boolean isRoot() {
	return (parent==null);
    }

    public boolean hasChildren() {
        return children != null && !children.isEmpty(); }

    public int getChildPosition(MindMapNode childNode) {
       int position = 0;
       for (ListIterator i=children.listIterator(); i.hasNext(); ++position) {
          if (((MindMapNode)i.next()) == childNode) {
             return position; }}
       return -1;
    }

    public ListIterator childrenUnfolded() {
        return children != null ? children.listIterator() :
           Collections.EMPTY_LIST.listIterator(); }

    public ListIterator childrenFolded() {
       if (isFolded()) {
          return Collections.EMPTY_LIST.listIterator();
       }
       return childrenUnfolded();
    }

    //
    //  Interface TreeNode
    //

    /**
     * AFAIK there is no way to get an enumeration out of a linked list. So this exception must be
     * thrown, or we can't implement TreeNode anymore (maybe we shouldn't?)
     */
    public Enumeration children() {
	throw new UnsupportedOperationException("Use childrenFolded or childrenUnfolded instead");
    }
	
    public boolean getAllowsChildren() {
	return ALLOWSCHILDREN;
    }
	
    public TreeNode getChildAt(int childIndex) {
        // fc, 11.12.2004: This is not understandable, that a child does not
        // exist if the parent is folded.
        //	if (isFolded()) {
        //	    return null;
        //	}
        return (TreeNode) children.get(childIndex);
    }

    public int getChildCount() {
       return children == null ? 0 : children.size();
    }

// (PN)
//    public int getChildCount() {
//	if (isFolded()) {
//	    return 0;
//	}
//	return children.size();
//    }
//    // Daniel: ^ The name of this method is confusing. It does nto convey
//    // the meaning, at least not to me.

    public int getIndex( TreeNode node ) {
	return children.indexOf( (MindMapNode)node ); //uses equals()
    } 

    public TreeNode getParent() {
	return parent;
    }

    public boolean isLeaf() {
	return getChildCount() == 0;
    }

    // fc, 16.12.2003 left-right bug:
    public Tools.BooleanHolder isLeft() {
        return left;
    }
    
    
    
	/* (non-Javadoc)
	 * @see freemind.modes.MindMapNode#isOneLeftSideOfRoot()
	 */
	public boolean isOneLeftSideOfRoot() {
		if(isRoot()) 
			return false;
		// now, the node has a parent:
		if(getParentNode().isRoot()) {
			return (isLeft() == null)?false:isLeft().getValue();
		}
		return getParentNode().isOneLeftSideOfRoot();
	}

	public void setLeft(boolean isLeft){
        if(left == null)
            left = new Tools.BooleanHolder();
        left.setValue(isLeft);
    }

    //
    //  Interface MutableTreeNode
    //
    
    //do all remove methods have to work recursively to make the 
    //Garbage Collection work (Nodes in removed Sub-Trees reference each other)?
    
    public void insert( MutableTreeNode child, int index) {
        logger.finest("Insert at " + index + " the node "+child);
        if (index < 0) { // add to the end (used in xml load) (PN) 
          index = getChildCount();
          children.add( index, child );
        }
        else {           // mind preferred child :-)
          children.add( index, child );
          preferredChild = (MindMapNode)child;
        }
    	child.setParent( this );
    }
    


    
    public void remove( int index ) {
        MutableTreeNode node = (MutableTreeNode)children.get(index);
		remove(node);
    }
    
    public void remove( MutableTreeNode node ) {
        if (node == this.preferredChild) { // mind preferred child :-) (PN) 
          int index = children.indexOf(node);
          if (children.size() > index + 1) {
            this.preferredChild = (MindMapNode)(children.get(index + 1));
          }
          else {
            this.preferredChild = (index > 0) ? 
                (MindMapNode)(children.get(index - 1)) : null;
          }
        }
        node.setParent(null);
    	children.remove( node );
    	// call remove child hook after removal.
    	recursiveCallRemoveChildren(this, (MindMapNode) node, this);
    }

	/**
	 * @param node
	 * @param oldDad the last dad node had.
	 */
	private void recursiveCallRemoveChildren(MindMapNode node, MindMapNode removedChild, MindMapNode oldDad) {
		for(Iterator i=  node.getActivatedHooks().iterator(); i.hasNext();) {
        	PermanentNodeHook hook = (PermanentNodeHook) i.next();
            if (removedChild.getParentNode() == node) {
                hook.onRemoveChild(removedChild);
            }
            hook.onRemoveChildren(removedChild, oldDad);
        }
		if(!node.isRoot() && node.getParentNode()!= null)
		    recursiveCallRemoveChildren(node.getParentNode(), removedChild, oldDad);
	}


    
    public MindMapNode getPreferredChild() { // mind preferred child :-) (PN) 
      if (this.children.contains(this.preferredChild)) {
        return this.preferredChild;
      }
      else if (!isLeaf()) {
        return (MindMapNode)(this.children.get((getChildCount() + 1) / 2 - 1));
      }
      else {
        return null;
      }
    }
    public void setPreferredChild(MindMapNode node) {
      this.preferredChild = node;
      if (node == null) {
        return;
      }
      else if (this.parent != null) {
        // set also preffered child of parents...
        this.parent.setPreferredChild(this);
      }
    }

    public void removeFromParent() {
    	parent.remove( this );
    }

    public void setParent( MutableTreeNode newParent ) {
        parent = (MindMapNode) newParent;
    }

    public void setParent( MindMapNode newParent ) {
    	parent = newParent;
    }

    public void setUserObject( Object object ) {
	userObject = object;
    }

    ////////////////
    // Private methods. Internal Implementation
    //////////////

    /** Recursive Method for getPath() */
    private void addToPathVector( Vector pathVector ) {
	pathVector.add( 0, this ); //Add myself to beginning of Vector
	if ( parent != null ) {
	    ( (NodeAdapter)parent ).addToPathVector( pathVector );
	}
    }

    public int getNodeLevel() {  //for cursor navigation within a level (PN)  
      int level = 0;
      MindMapNode parent;
      for (parent = this; !parent.isRoot(); parent = parent.getParentNode()) {
        level++;
      }
      return level;
    }
	/* (non-Javadoc)
	 * @see freemind.modes.MindMapNode#addHook(freemind.modes.NodeHook)
	 */
	public PermanentNodeHook addHook(PermanentNodeHook hook) {
		// add then
		if(hook == null) 
			throw new IllegalArgumentException("Added null hook.");
		createHooks();
		hooks.add(hook);
		return hook;
	}

	public void invokeHook(NodeHook hook) {
		// initialize:
		hook.startupMapHook();
		// the main invocation:
		hook.setNode(this);
		try {
			hook.invoke(this);
		} catch (Exception e) {
			//FIXME: Do something special here, but in any case, do not add the hook
			// to the activatedHooks:
			e.printStackTrace();
			return;
		}
	    if (hook instanceof PermanentNodeHook) {
	    		createActivatedHooks();
			activatedHooks.add(hook);
		} else {
		    // end of its short life:
		    hook.shutdownMapHook();
		}
	}

	private void createActivatedHooks() {
		if(activatedHooks == null) {
			activatedHooks = new HashSet();
		}
	}
	private void createToolTip() {
		if(toolTip == null) {
			toolTip = new HashMap();
		}
	}
	private void createHooks() {
		if(hooks == null) {
			hooks = new Vector();
		}
	}
	private void createStateIcons() {
		if(stateIcons == null) {
			stateIcons = new TreeMap();
		}
	}
	private void createIcons() {
		if(icons == null) {
			icons = new Vector();
		}
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MindMapNode#getHooks()
	 */
	public List getHooks() {
		if(hooks==null)
			return Collections.EMPTY_LIST;
		return Collections.unmodifiableList(hooks);
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MindMapNode#getActivatedHooks()
	 */
	public Collection getActivatedHooks() {
		if(activatedHooks==null) {
			return Collections.EMPTY_LIST;
		}
		return Collections.unmodifiableCollection(activatedHooks);
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MindMapNode#removeHook(freemind.modes.NodeHook)
	 */
	public void removeHook(PermanentNodeHook hook) {
	    // the order is crucial here: the shutdown method should be able to perform "nodeChanged" 
	    // calls without having its own updateNodeHook method to be called again.
		createActivatedHooks();
		activatedHooks.remove(hook);
		if(activatedHooks.size()==0) {
			activatedHooks=null;
		}
		hook.shutdownMapHook();
		createHooks();
		hooks.remove(hook);
		if(hooks.size()==0)
			hooks=null;
	}

	/**
	 * @return
	 */
	public java.util.Map getToolTip() {
		if(toolTip==null)
			return Collections.EMPTY_MAP;
		return Collections.unmodifiableMap(toolTip);
	}

	/**
	 * @param string
	 */
	public void setToolTip(String key, String string) {
		createToolTip();
		if (string == null) {
		    if (toolTip.containsKey(key)) {
                toolTip.remove(key);
            }
            if(toolTip.size()==0)
            	toolTip=null;
        } else {
            toolTip.put(key, string);
        }
	}


	/* (non-Javadoc)
	 * @see freemind.modes.MindMapNode#getNodeId()
	 */
	public String getObjectId(ModeController controller) {
	    return controller.getNodeID(this);
	}

    public XMLElement save(Writer writer, MindMapLinkRegistry registry) throws IOException {
    	XMLElement node = new XMLElement();
    	
//    	if (!isNodeClassToBeSaved()) {
    	node.setName(XMLElementAdapter.XML_NODE);
//        } else {
//            node.setName(XMLElementAdapter.XML_NODE_CLASS_PREFIX
//                    + this.getClass().getName());
//        }
    
        /** fc, 12.6.2005: XML must not contain any zero characters. */
        String text = this.toString().replace('\0', ' ');
        node.setAttribute(XMLElementAdapter.XML_NODE_TEXT,text);
    	// save additional info:
    	if (getAdditionalInfo() != null) {
            node.setAttribute(XMLElementAdapter.XML_NODE_ENCRYPTED_CONTENT,
                    getAdditionalInfo());
        }
    	//	((MindMapEdgeModel)getEdge()).save(doc,node);
    
    	XMLElement edge = (getEdge()).save();
    	if (edge != null) {
               node.addChild(edge); }
    
        if(getCloud() != null) {
            XMLElement cloud = (getCloud()).save();
            node.addChild(cloud); 
        }
    
        Vector linkVector = registry.getAllLinksFromMe(this); /* Puh... */
        for(int i = 0; i < linkVector.size(); ++i) {
            if(linkVector.get(i) instanceof ArrowLinkAdapter) {
                XMLElement arrowLinkElement = ((ArrowLinkAdapter) linkVector.get(i)).save();
                node.addChild(arrowLinkElement);
            }
        }
            
    	if (isFolded()) {
               node.setAttribute("FOLDED","true"); }
    	
        // fc, 17.12.2003: Remove the left/right bug.
        //                       VVV  save if and only if parent is root.
    	if ((isLeft()!= null) && !(isRoot()) && (getParentNode().isRoot())) {
            node.setAttribute("POSITION",(isLeft().getValue())?"left":"right"); 
        }
    	
        String label = registry.getLabel(this); /* Puh... */
    	if (label!=null) {
               node.setAttribute("ID",label); }
    	
    	if (color != null) {
               node.setAttribute("COLOR", Tools.colorToXml(getColor())); }
    
    	// new background color.
    	if (getBackgroundColor() != null) {
    		   node.setAttribute("BACKGROUND_COLOR", Tools.colorToXml(getBackgroundColor())); }
    
    
    	if (style != null) {
               node.setAttribute("STYLE", this.getStyle()); }
    	    //  ^ Here cannot be just getStyle() without super. This is because
    	    //  getStyle's style depends on folded / unfolded. For example, when
    	    //  real style is fork and node is folded, getStyle returns
    	    //  MindMapNode.STYLE_BUBBLE, which is not what we want to save.
    
    	//layout
        if(vGap != AUTO) {
        	node.setAttribute("VGAP",Integer.toString(vGap));
        }
        if(hGap != HGAP) {
        	node.setAttribute("HGAP",Integer.toString(hGap));
        }
        if(shiftY != 0) {
        	node.setAttribute("VSHIFT",Integer.toString(shiftY));
        }
    	//link
    	if (getLink() != null) {
               node.setAttribute("LINK", getLink()); }


    	//history information, fc, 11.4.2005
    	if (historyInformation != null) {
			node.setAttribute(XMLElementAdapter.XML_NODE_HISTORY_CREATED_AT,
					Tools.dateToString(getHistoryInformation().getCreatedAt()));
			node.setAttribute(
					XMLElementAdapter.XML_NODE_HISTORY_LAST_MODIFIED_AT, Tools
							.dateToString(getHistoryInformation()
									.getLastModifiedAt()));
	}
    	//font
    	if (font!=null) {
    	    XMLElement fontElement = new XMLElement();
    	    fontElement.setName("font");
    
    	    if (font != null) {
                   fontElement.setAttribute("NAME",font.getFamily()); }
    	    if (font.getSize() != 0) {
                   fontElement.setAttribute("SIZE",Integer.toString(font.getSize())); }
    	    if (isBold()) {
                   fontElement.setAttribute("BOLD","true"); }
    	    if (isItalic()) {
                   fontElement.setAttribute("ITALIC","true"); }
    	    if (isUnderlined()) {
                   fontElement.setAttribute("UNDERLINE","true"); }
    	    node.addChild(fontElement); }
        for(int i = 0; i < getIcons().size(); ++i) {
    	    XMLElement iconElement = new XMLElement();
    	    iconElement.setName("icon");
            iconElement.setAttribute("BUILTIN", ((MindIcon) getIcons().get(i)).getName());
            node.addChild(iconElement);
        }
    
    	for (Iterator i = getActivatedHooks().iterator(); i.hasNext();) {
            XMLElement hookElement = new XMLElement();
            hookElement.setName("hook");
            ((PermanentNodeHook) i.next()).save(hookElement);
            node.addChild(hookElement);
        }

        attributes.save(node);
        if (childrenUnfolded().hasNext()) {
            node.writeWithoutClosingTag(writer);
            //recursive
            for (ListIterator e = childrenUnfolded(); e.hasNext();) {
                NodeAdapter child = (NodeAdapter) e.next();
                child.save(writer, registry);
            }
            node.writeClosingTag(writer);
        } else {
            node.write(writer);
        }
        return node;
    }
    
	public int getShiftY() {
			return shiftY ;
	}
	
	
	
    public boolean hasOneVisibleChild() {
        int count = 0;
        for (ListIterator i = childrenUnfolded() ; i.hasNext() ;) {
            if (((MindMapNode)i.next()).isVisible()) count++;
            if (count == 2) return false;
        }
        return count == 1;
    }
    
	public int calcShiftY() {
		try{
			return shiftY + (parent.hasOneVisibleChild() ? SHIFT:0);
		}
		catch(NullPointerException e){
			return 0;			
		}
		
	}
	/**
	 * @param shiftY The shiftY to set.
	 */
	public void setShiftY(int shiftY) {
		this.shiftY = shiftY;
	}
    /**
     *
     */

    public void setAdditionalInfo(String info) {
    }
    
    public String getAdditionalInfo(){
        return null;
    }
    
   
    /** This method must be synchronized as the TreeMap isn't. */
    public synchronized void setStateIcon(String key, ImageIcon icon) {
//    		logger.warning("Set state of key:"+key+", icon "+icon);
    		createStateIcons();
        if (icon != null) {
			stateIcons.put(key, icon);			
            getMap().getRegistry().addIcon(MindIcon.factory(key, icon));
		} else if(stateIcons.containsKey(key)) {
            stateIcons.remove(key);
        }
		if(stateIcons.size()==0)
			stateIcons = null;
    }
    public Map getStateIcons() {
    		if(stateIcons==null)
    			return Collections.EMPTY_MAP;
        return Collections.unmodifiableSortedMap(stateIcons);
    }
	public HistoryInformation getHistoryInformation() {
		return historyInformation;
	}
	public void setHistoryInformation(HistoryInformation historyInformation) {
		this.historyInformation = historyInformation;
	}

	public int getHGap() {
		return hGap;
	}
	public void setHGap(int gap) {
		hGap = Math.max(HGAP, gap);
	}

	public int getVGap() {
		return vGap;
}
	public int calcVGap() {
		if (vGap != AUTO)
			return vGap;
/*		
//		double delta = 8.0 / Math.pow(1.5, 1 + getNodeLevel()); // to expensive... 
		double delta = 8.0 / Math.pow(1 + getNodeLevel(), 1.5); 
        return (int ) ((1 + delta) * VGAP );			 
*/
		return VGAP;		    
	}
	
	public void setVGap(int gap) {
		if (gap == AUTO) vGap = AUTO;
		else vGap = Math.max(gap, 0);
	}	   
    
    public boolean isVisible() {
        Filter filter = getMap().getFilter();
        return filter == null || filter.isVisible(this);
    }
    
    public NodeAttributeTableModel getAttributes(){
        return attributes;
    }
    EventListenerList listenerList = new EventListenerList();
    NodeViewEvent nodeViewEvent = null;

    public void addNodeViewEventListener(NodeViewEventListener l) {
        listenerList.add(NodeViewEventListener.class, l);
    }

    public void removeNodeViewEventListener(NodeViewEventListener l) {
        listenerList.remove(NodeViewEventListener.class, l);
    }


    // Notify all listeners that have registered interest for
    // notification on this event type.  The event instance 
    // is lazily created using the parameters passed into 
    // the fire method.

    protected void fireNodeViewCreated() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==NodeViewEventListener.class) {
                // Lazily create the event:
                if (nodeViewEvent == null)
                    nodeViewEvent = new NodeViewEvent(this);
                ((NodeViewEventListener)listeners[i+1]).nodeViewCreated(nodeViewEvent);
            }
        }
    }
    protected void fireNodeViewRemoved() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==NodeViewEventListener.class) {
                // Lazily create the event:
                if (nodeViewEvent == null)
                    nodeViewEvent = new NodeViewEvent(this);
                ((NodeViewEventListener)listeners[i+1]).nodeViewRemoved(nodeViewEvent);
            }
        }
    }
}
