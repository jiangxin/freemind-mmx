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
/*$Id: NodeView.java,v 1.27.10.2 2004-03-29 18:08:10 christianfoltin Exp $*/

package freemind.view.mindmapview;

import freemind.main.FreeMind;
import freemind.main.Tools;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;//This should not be done.
import freemind.modes.MindIcon;

import java.util.LinkedList;
import java.util.ListIterator;
import java.awt.*;
import java.awt.dnd.*;
import java.net.MalformedURLException;

import javax.swing.JLabel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.util.Vector;


/**
 * This class represents a single Node of a MindMap
 * (in analogy to TreeCellRenderer).
 */
public abstract class NodeView extends JLabel {

    protected MindMapNode model;
    protected MapView map;
    protected EdgeView edge;
    /** the Color of the Rectangle of a selected Node */
    protected static Color selectedColor; 
    protected final static Color dragColor = Color.lightGray; //the Color of appearing GradientBox on drag over
    protected int treeHeight;
    private boolean left = true; //is the node left of root?
    int relYPos;//the relative Y Position to it's parent
    private boolean isLong = false;

    public final static int DRAGGED_OVER_NO = 0;
    public final static int DRAGGED_OVER_SON = 1;
    public final static int DRAGGED_OVER_SIBLING = 2;
    /** For RootNodeView.*/
    public final static int DRAGGED_OVER_SON_LEFT = 3;

    protected int isDraggedOver = DRAGGED_OVER_NO;
    public void setDraggedOver(int draggedOver) {
       isDraggedOver = draggedOver; }
    public void setDraggedOver(Point p) {
       setDraggedOver( (dropAsSibling(p.getX())) ? NodeView.DRAGGED_OVER_SIBLING : NodeView.DRAGGED_OVER_SON) ; }
    public int getDraggedOver() {
       return isDraggedOver; }

	final static int ALIGN_BOTTOM = -1;
	final static int ALIGN_CENTER = 0;
	final static int ALIGN_TOP = 1;

    public final int LEFT_WIDTH_OVERHEAD = 0;
    public final int LEFT_HEIGHT_OVERHEAD = 0;

    //
    // Constructors
    //
    
    protected NodeView(MindMapNode model, MapView map) {
	this.model = model;
	setMap(map);
	// initialize the selectedColor:
	if(selectedColor== null) {
		String stdcolor = map.getController().getFrame().getProperty("standardselectednodecolor");
		if (stdcolor.length() == 7) {
			selectedColor = Tools.xmlToColor(stdcolor);
		} else {
			selectedColor = new Color(210,210,210);
		}
	}

	//Root has no edge
	if (!isRoot()) {
	    if (getModel().getEdge().getStyle().equals("linear")) {
		edge = new LinearEdgeView(getParentView(),this);
	    } else if (getModel().getEdge().getStyle().equals("bezier")) {
		edge = new BezierEdgeView(getParentView(),this);
	    } else if (getModel().getEdge().getStyle().equals("sharp_linear")) {
		edge = new SharpLinearEdgeView(getParentView(),this);
	    } else if (getModel().getEdge().getStyle().equals("sharp_bezier")) {
		edge = new SharpBezierEdgeView(getParentView(),this);
	    } else {
		System.err.println("Unknown Edge Type.");
	    }
	}

	addMouseListener( map.getNodeMouseMotionListener() );
	addMouseMotionListener( map.getNodeMouseMotionListener() );
	addKeyListener( map.getNodeKeyListener() );

	addDragListener( map.getNodeDragListener() );
	addDropListener( map.getNodeDropListener() );
    }

    void addDragListener(DragGestureListener dgl) {
	DragSource dragSource = DragSource.getDefaultDragSource();
	dragSource.createDefaultDragGestureRecognizer 
           (this, DnDConstants.ACTION_COPY | DnDConstants.ACTION_MOVE | DnDConstants.ACTION_LINK ,dgl);
    }

    void addDropListener(DropTargetListener dtl) {
	DropTarget dropTarget = new DropTarget(this,dtl);
	dropTarget.setActive(true);
    }

    /**
     * Factory method which creates the right NodeView for the model.
     */ 
    protected static NodeView newNodeView(MindMapNode model, MapView map) {
	NodeView newView;
	if (model.isRoot()) {
	    newView = new RootNodeView( model, map );
	} else if (model.getStyle().equals(MindMapNode.STYLE_FORK) ) {
	    newView = new ForkNodeView( model, map );
	} else if (model.getStyle().equals(MindMapNode.STYLE_BUBBLE) ) {
	    newView = new BubbleNodeView( model, map );
	} else {
	    System.err.println("Tried to create a NodeView of unknown Style.");
	    newView = new ForkNodeView(model, map);
	}
	model.setViewer(newView);
	map.add(newView);
	newView.update();
	return newView;
    }

    //
    // public methods
    //

    public boolean dropAsSibling(double xCoord) {
       return isLeft() ?
          xCoord > getSize().width*2/3 :
          xCoord < getSize().width/3; 
    }

    /** @return true if should be on the left, false otherwise.*/
    public boolean dropPosition (double xCoord) {
        /* here it is the same as me. */
       return isLeft(); 
    }

    public boolean followLink(double xCoord) {
       return getModel().getLink() != null &&
          (getModel().isRoot() || !getModel().hasChildren() || xCoord < getSize().width/2); }

    public void updateCursor(double xCoord) {
      int requiredCursor = followLink(xCoord) ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR;
      if (getCursor().getType() != requiredCursor) {
        setCursor(new Cursor(requiredCursor));
      }
    }

    public boolean isRoot() {
	return model.isRoot();
    }

    public boolean getIsLong() {
        return isLong; }

    /* fc, 25.1.2004: Refactoring necessary: should call the model. */
    public boolean isSiblingOf(NodeView myNodeView) { 
       return getParentView() == myNodeView.getParentView(); }

    /* fc, 25.1.2004: Refactoring necessary: should call the model. */
    public boolean isChildOf(NodeView myNodeView) { 
       return getParentView() == myNodeView; }

    /* fc, 25.1.2004: Refactoring necessary: should call the model. */
    public boolean isParentOf(NodeView myNodeView) { 
       return (this == myNodeView.getParentView()); }

    public MindMapNode getModel() {
	return model;
    }

    /** Returns the coordinates occupied by the node and its children as a vector of four point per node.*/
    public void getCoordinates(LinkedList inList, int additionalDistanceForConvexHull) {
        inList.addLast(new Point( -additionalDistanceForConvexHull + getX()             ,  -additionalDistanceForConvexHull + getY()              ));
        inList.addLast(new Point( -additionalDistanceForConvexHull + getX()             ,   additionalDistanceForConvexHull + getY() + getHeight()));
        inList.addLast(new Point(  additionalDistanceForConvexHull + getX() + getWidth(),   additionalDistanceForConvexHull + getY() + getHeight()));
        inList.addLast(new Point(  additionalDistanceForConvexHull + getX() + getWidth(),  -additionalDistanceForConvexHull + getY()              ));
        LinkedList childrenViews = getChildrenViews();
        ListIterator children_it = childrenViews.listIterator();
        while(children_it.hasNext()) {
            NodeView child = (NodeView)children_it.next();
            child.getCoordinates(inList, additionalDistanceForConvexHull);
        }
    }   

    /** Changed to remove the printing bug of java.*/
    public Dimension getPreferredSize() {
        if(map.isPrinting()) {
            return new Dimension(super.getPreferredSize().width + (int)(10f*map.getZoom()),
                                 super.getPreferredSize().height);
        } else {
            return super.getPreferredSize();
        }
    }	


   public void requestFocus(){
      map.getController().getMode().getModeController().anotherNodeSelected(getModel());
      super. requestFocus();
   }

    public void paint(Graphics graphics) {
        // background color starts here, fc. 9.11.2003: todo
//           graphics.setColor(Color.yellow);
//           graphics.fillRect(0,0,getWidth(), getHeight());
          // background color ends here, fc. 9.11.2003: todo
	super.paint(graphics);
    }

   public void paintSelected(Graphics2D graphics, Dimension size) {
       if( this.isSelected() ) {
          graphics.setColor(selectedColor);
          graphics.fillRect(0,0,size.width, size.height);
          //g.drawRect(0,0,size.width-1, size.height-2);
       } else if( getModel().getBackgroundColor() != null) {
          graphics.setColor(getModel().getBackgroundColor());
          graphics.fillRect(0,0,size.width, size.height);
       }
    }

   public void paintDragOver(Graphics2D graphics, Dimension size) {
        if (isDraggedOver == DRAGGED_OVER_SON) {
           if (isLeft()) {
              graphics.setPaint( new GradientPaint(size.width*3/4,0,map.getBackground(), size.width/4, 0, dragColor));
              graphics.fillRect(0, 0, size.width*3/4, size.height-1); }
           else {
              graphics.setPaint( new GradientPaint(size.width/4,0,map.getBackground(), size.width*3/4, 0, dragColor));
              graphics.fillRect(size.width/4, 0, size.width-1, size.height-1); }       
	}

        if (isDraggedOver == DRAGGED_OVER_SIBLING) {
            graphics.setPaint( new GradientPaint(0,size.height*3/5,map.getBackground(), 0, size.height/5, dragColor));
            graphics.fillRect(0, 0, size.width-1, size.height-1);
	}
    }

    public int getLeftWidthOverhead() {
       return LEFT_WIDTH_OVERHEAD; }

    //
    // get/set methods
    //

    int getTreeHeight() {
	return treeHeight;
    }

    void setTreeHeight(int treeHeight) {
	this.treeHeight = treeHeight;
    }

    protected boolean isSelected() {
	return (getMap().isSelected(this));
    }

    /**Is the node left of root?*/
    public boolean isLeft() {
        if(getModel().isLeft() == null)
            return true;
        return getModel().isLeft().getValue();
    }

    protected void setLeft(boolean left) {
        //this.left = left;
        getModel().setLeft(left);
    }
	
//     public boolean isLeftDefault() {
//         return getModel().isLeft() == null;
//     }
    
    protected void setModel( MindMapNode model ) {
	this.model = model;
    }

    MapView getMap() {
	return map;
    }

    protected void setMap( MapView map ) {
	this.map = map;
    }

    EdgeView getEdge() {
	return edge;
    }

    void setEdge(EdgeView edge) {
	this.edge = edge;
    }

    protected NodeView getParentView() {
	return getModel().getParentNode().getViewer();
    }

    /**
     * This method returns the NodeViews that are children of this node.
     */
    public LinkedList getChildrenViews() {
	LinkedList childrenViews = new LinkedList();
	ListIterator it = getModel().childrenUnfolded();
	if (it != null) {
           while(it.hasNext()) {
              NodeView view = ((MindMapNode)it.next()).getViewer();
              if (view != null) { // Visible view
                 childrenViews.add(view); // child.getViewer() );
              }
           }
        }
        return childrenViews;
    }
    
    protected LinkedList getSiblingViews() {
	return getParentView().getChildrenViews();
    }

    /**
     * Returns the Point where the OutEdge
     * should leave the Node.
     * THIS SHOULD BE DECLARED ABSTRACT AND BE DONE IN BUBBLENODEVIEW ETC.
     */
    Point getOutPoint() {
	Dimension size = getSize();
	if( isRoot() ) {
	    return new Point(getLocation().x + size.width, getLocation().y + size.height / 2);
	} else if( isLeft() ) {
	    return new Point(getLocation().x, getLocation().y + size.height - 2);
	} else {
	    return new Point(getLocation().x + size.width, getLocation().y + size.height - 2);
	} 
    }

    /**
     * Returns the Point where the InEdge
     * should arrive the Node.
     * THIS SHOULD BE DECLARED ABSTRACT AND BE DONE IN BUBBLENODEVIEW ETC.
     */
    Point getInPoint() {
	Dimension size = getSize();
	if( isRoot() ) {
	    return new Point(getLocation().x, getLocation().y + size.height / 2);
	} else if( isLeft() ) {
	    return new Point(getLocation().x + size.width, getLocation().y + size.height - 2);
	} else {
	    return new Point(getLocation().x, getLocation().y + size.height - 2);
	} 
    }


    /**
     * Returns the Point where the Links 
     * should arrive the Node.
     * THIS SHOULD BE DECLARED ABSTRACT AND BE DONE IN BUBBLENODEVIEW ETC.
     */
    Point getLinkPoint() {
	Dimension size = getSize();
	if( isRoot() ) {
	    return new Point(getLocation().x, getLocation().y + size.height / 2);
	} else if( isLeft() ) {
	    return new Point(getLocation().x, getLocation().y + size.height / 2);
	} else {
	    return new Point(getLocation().x + size.width, getLocation().y + size.height / 2);
	} 
    }

    /**
     * Returns the relative position of the Edge.
	 * This is used by bold edge to know how to shift the line.
     */
    int getAlignment() {
	if( isRoot() )
	    return ALIGN_CENTER;
	return ALIGN_BOTTOM;
    }
	
    //
    // Navigation
    //
    protected NodeView getNextPage() {
      if (isRoot()) {
        return this; // I'm root
      }
      NodeView sibling = getNextSibling();
      if (sibling == this) {
        return this; // at the end
      }
//      if (sibling.getParentView() != this.getParentView()) {
//        return sibling; // sibling on another page (has different parent)
//      }
      NodeView nextSibling = sibling.getNextSibling();
      while (nextSibling != sibling 
              && sibling.getParentView() == nextSibling.getParentView()) {
        sibling = nextSibling;
        nextSibling = nextSibling.getNextSibling();
      }
      return sibling; // last on the page
    }
      
    protected NodeView getPreviousPage() {
      if (isRoot()) {
        return this; // I'm root
      }
      NodeView sibling = getPreviousSibling();
      if (sibling == this) {
        return this; // at the end
      }
//      if (sibling.getParentView() != this.getParentView()) {
//        return sibling; // sibling on another page (has different parent)
//      }
      NodeView previousSibling = sibling.getPreviousSibling();
      while (previousSibling != sibling 
              && sibling.getParentView() == previousSibling.getParentView()) {
        sibling = previousSibling;
        previousSibling = previousSibling.getPreviousSibling();
      }
      return sibling; // last on the page
    }
    
    protected NodeView getNextSibling() {
      NodeView sibling;
      NodeView nextSibling = this;
      
      // get next sibling even in higher levels
      for (sibling = this; !sibling.isRoot(); sibling = sibling.getParentView()) { 
        nextSibling = sibling.getNextSiblingSingle();
        if (sibling != nextSibling) {
          break; // found sibling
        }
      }
      
      if (sibling.isRoot()) {
        return this;  // didn't find (we are at the end)
      }
      
      // we have the nextSibling, search in childs
      // untill: leaf, closed node, max level
      sibling = nextSibling;
      while (sibling.getModel().getNodeLevel() < getMap().getSiblingMaxLevel()) {
        // can we drill down?
        if (sibling.getChildrenViews().size() <= 0) {
          break; // no
        }
        sibling = (NodeView)(sibling.getChildrenViews().getFirst());
      }
      return sibling;
    }

    protected NodeView getPreviousSibling() {
      NodeView sibling;
      NodeView previousSibling = this;
      
      // get Previous sibling even in higher levels
      for (sibling = this; !sibling.isRoot(); sibling = sibling.getParentView()) { 
        previousSibling = sibling.getPreviousSiblingSingle();
        if (sibling != previousSibling) {
          break; // found sibling
        }
      }
      
      if (sibling.isRoot()) {
        return this;  // didn't find (we are at the end)
      }
      
      // we have the PreviousSibling, search in childs
      // untill: leaf, closed node, max level
      sibling = previousSibling;
      while (sibling.getModel().getNodeLevel() < getMap().getSiblingMaxLevel()) {
        // can we drill down?
        if (sibling.getChildrenViews().size() <= 0) {
          break; // no
        }
        sibling = (NodeView)(sibling.getChildrenViews().getLast());
      }
      return sibling;
    }

    protected NodeView getNextSiblingSingle() {
	LinkedList v = null;
	if (getParentView().isRoot()) {
	    if (this.isLeft()) {
		v = ((RootNodeView)getParentView()).getLeft();
	    } else {
		v = ((RootNodeView)getParentView()).getRight();
	    }
	} else {
	    v = getParentView().getChildrenViews();
	}	
	NodeView sibling;
	if (v.size()-1 == v.indexOf(this)) { //this is last, return first
//	    sibling = (NodeView)v.getFirst(); // loop
            sibling = this;
	} else {
	    sibling = (NodeView)v.get(v.indexOf(this)+1);
	}
	return sibling;
    }

    protected NodeView getPreviousSiblingSingle() {
	LinkedList v = null;
	if (getParentView().isRoot()) {
	    if (this.isLeft()) {
		v = ((RootNodeView)getParentView()).getLeft();
	    } else {
		v = ((RootNodeView)getParentView()).getRight();
	    }
	} else {
	    v = getParentView().getChildrenViews();
	}
	NodeView sibling;
	if (v.indexOf(this) <= 0) {//this is first, return last
//	    sibling = (NodeView)v.getLast(); // loop
          sibling = this;
	} else {
	    sibling = (NodeView)v.get(v.indexOf(this)-1);
	}
	return sibling;
    }

    //
    // Update from Model
    //

    void insert() {
       ListIterator it = getModel().childrenFolded();
       while(it.hasNext()) {               
          insert((MindMapNode)it.next());
       }
    }

    /**
     * Create views for the newNode and all his descendants, set their
     * isLeft attribute according to this view. Observe that views know
     * about their parents only through their models.
     */

    void insert(MindMapNode newNode) {
       NodeView newView = NodeView.newNodeView(newNode,getMap());
       newView.setLeft(this.isLeft());

       ListIterator it = newNode.childrenFolded();
       while (it.hasNext()) {
          MindMapNode child = (MindMapNode)it.next();
          newView.insert(child);
       }
    }
    
    /**
     * This is a bit problematic, because getChildrenViews() only works if 
     * model is not yet removed. (So do not _really_ delete the model before the view 
     * removed (it needs to stay in memory)
     */
    void remove() {
	getMap().remove(this);
	if (getEdge()!=null) {
           getEdge().remove(); }
        getModel().setViewer(null); // Let the model know he is invisible
	for(ListIterator e = getChildrenViews().listIterator();e.hasNext();) {
           ((NodeView)e.next()).remove(); }}

     void update() {
        //System.err.println("update");
        // 1) Set color
        Color color = getModel().getColor();
        if (color==null) {
           String stdcolor = map.getController().getProperty("standardnodecolor");
           if (stdcolor.length() == 7) {
              color = Tools.xmlToColor(stdcolor); }
           else {
              color = Color.black; }}
        setForeground(color);
//         // 1b) set background color:
//         Color bgcolor = getModel().getBackgroundColor();
//         if (bgcolor!=null) {
//             setOpaque(true);
//             setBackground(bgcolor);
//         }

        // 2) Create the icons:
        MultipleImage iconImages = new MultipleImage(map.getZoom());
        boolean iconPresent = false;
        /* fc, 06.10.2003: images?*/
        Vector icons = ((NodeAdapter)getModel()).getIcons();
        for(int i = 0; i < icons.size(); ++i) {
            iconPresent = true;
            MindIcon myicon = (MindIcon) icons.get(i);
            //System.out.println("print the icon " + myicon.toString());
            iconImages.addImage((ImageIcon) myicon.getIcon(map.getController().getFrame()));  
        }
        String link = ((NodeAdapter)getModel()).getLink();
        if ( link != null ) 
            {
                iconPresent = true;
                ImageIcon icon = new ImageIcon(((NodeAdapter)getModel()).getFrame().getResource
                                          (link.startsWith("mailto:") ? "images/Mail.png" :
                                           (Tools.executableByExtension(link) ? "images/Executable.png" :
                                            "images/Link.png")));
                iconImages.addImage(icon); 
            }
//         /* Folded icon by Matthias Schade (mascha2), fc, 20.12.2003*/
//         if (((NodeAdapter)getModel()).isFolded()) {
//             iconPresent = true;
//             ImageIcon icon = new ImageIcon(((NodeAdapter)getModel()).getFrame().getResource("images/Folded.png"));
//             iconImages.addImage(icon); 
//         }
        // DanielPolansky: set icon only if icon is present, because
        // we don't want to insert any additional white space.        
        setIcon(iconPresent?iconImages:null);

        // 3) Determine font
        Font font = getModel().getFont();
        font = font == null ? map.getController().getDefaultFont() : font;
        if (font != null) {
           if (map.getZoom() != 1F) {
              font = font.deriveFont(font.getSize()*map.getZoom()); }
           setFont(font); }
        else {
           // We can survive this trouble.
           System.err.println("NodeView.update(): default font is null."); }

        // 4) Set the text
        // Right now, this implementation is quite logical, although it allows
        // for nonconvex feature of nodes starting with <html>.

        String nodeText = getModel().toString();
        if (nodeText.length() < 4) { // (PN)
          nodeText = nodeText + new String("   ").substring(nodeText.length());
        }

        // Tell if node is long and its width has to be restricted
        // boolean isMultiline = nodeText.indexOf("\n") >= 0;
        String[] lines = nodeText.split("\n");
        boolean widthMustBeRestricted = false;

        lines = nodeText.split("\n");           
        for (int line = 0; line < lines.length; line++) {
           // Compute the width the node would spontaneously take,
           // by preliminarily setting the text.
           setText(lines[line]);
           if (widthMustBeRestricted = getPreferredSize().width > 
               map.getZoomed(map.getMaxNodeWidth())) {
              break; }}

        isLong = widthMustBeRestricted || lines.length > 1;
   
        if (nodeText.startsWith("<html>")) {
           // Make it possible to use relative img references in HTML using tag <base>.
           if (nodeText.indexOf("<img")>=0 && nodeText.indexOf("<base ") < 0 ) {
              try {
                 nodeText = "<html><base href=\""+
                    map.getModel().getURL()+"\">"+nodeText.substring(6); }
              catch (MalformedURLException e) {} }
           setText(nodeText); }
        else if (nodeText.startsWith("<table>")) {           	             	  
           lines[0] = lines[0].substring(7); // remove <table> tag
           int startingLine = lines[0].matches("\\s*") ? 1 : 0;
           // ^ If the remaining first line is empty, do not draw it
           
           String text = "<html><table border=1 style=\"border-color: white\">";
           //String[] lines = nodeText.split("\n");
           for (int line = startingLine; line < lines.length; line++) {
              text += "<tr><td style=\"border-color: white;\">"+
                 Tools.toXMLEscapedText(lines[line]).replaceAll("\t","<td style=\"border-color: white\">"); }
           setText(text); }
        else if (isLong) {
           String text = "<tr><td>";              
           int maximumLineLength = 0;
           for (int line = 0; line < lines.length; line++) {
              text += Tools.toXMLEscapedTextWithNBSPizedSpaces(lines[line]) + "<p>";
              if (lines[line].length() > maximumLineLength) {
                 maximumLineLength = lines[line].length(); }}
           
			text += "</td></tr>";
           setText("<html><table"+
                   (!widthMustBeRestricted?">":" width=\""+map.getZoomed(map.getMaxNodeWidth())+"\">")+
                   text+"</table></html>"); }
   		// 5) ToolTip:
   		String tip = getModel().getToolTip();
   		if(tip != null)
   			setToolTipText(tip);
        // 6) Complete
        repaint(); // Because of zoom?
    }

    void updateAll() {
	update();
	for(ListIterator e = getChildrenViews().listIterator();e.hasNext();) {
	    NodeView child = (NodeView)e.next();
	    child.updateAll();
	}
    }

   protected void setRendering(Graphics2D g) {
      if (map.getController().getAntialiasAll()) {
         g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); }
//       else
//          g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF); 

   }

}
