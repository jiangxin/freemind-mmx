/*******************************************************************************
 *  SuperWaba Virtual Machine, version 4                                       *
 *  Copyright (C) 2003-2004 Guilherme Campos Hazan <support@superwaba.com.br>  *
 *  Copyright (C) 2001 Daniel Tauchke                                          *
 *  All Rights Reserved                                                        *
 *                                                                             *
 *  This library and virtual machine is free software; you can redistribute    *
 *  it and/or modify it under the terms of the Amended GNU Lesser General      *
 *  Public License distributed with this software.                             *
 *                                                                             *
 *  This library and virtual machine is distributed in the hope that it will   *
 *  be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of  *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.                       *
 *                                                                             *
 *  For the purposes of the SuperWaba software we request that software using  *
 *  or linking to the SuperWaba virtual machine or its libraries display the   *
 *  following notice:                                                          *
 *                                                                             *
 *                   Created with SuperWaba                                    *
 *                  http://www.superwaba.org                                   *
 *                                                                             *
 *  Please see the software license located at SuperWabaSDK/license.txt        *
 *  for more details.                                                          *
 *                                                                             *
 *  You should have received a copy of the License along with this software;   *
 *  if not, write to                                                           *
 *                                                                             *
 *     Guilherme Campos Hazan                                                  *
 *     Av. Nossa Senhora de Copacabana 728 apto 605 - Copacabana               *
 *     Rio de Janeiro / RJ - Brazil                                            *
 *     Cep: 22050-000                                                          *
 *     E-mail: support@superwaba.com.br                                        *
 *                                                                             *
 *******************************************************************************/

import waba.util.Vector;

/*******************************************************************************
 *
 *    File:           Node.java
 *    Date:           August 26,2004.
 *    Last Modified:  September 5, 2004.
 *    Author:         Tri (Trev) Quang Nguyen.
 *    Version:        0.9 
 *    Email:          tnguyen@ceb.nlm.nih.gov
 *    
 *    Description:    This class defines the requirements for an object that can 
 *                    be used as a tree node in a Tree.  
 *
 *    Note:           Some methods are still unimplemented.                   
 *
 ********************************************************************************/
public class Node  {
	private static final int BEFORE  = 0;  // used in getChildBefore()
	private static final int AFTER   = 1;  // used in getChildAfter()
	
    protected Node    parent         = null;   // this node parent (root node is when the parent node = null)
    protected Vector  children       = null;   // this node children
    protected Object  userObject     = null;   // user object
    protected Vector	userIcons		=new Vector();	// Icons user associates with this node.
    protected int wicons=0;						// Width of combined icons (used by highlighting)
    
    public  boolean   allowsChildren = false;  // flag to determine if this node can have children
    public  boolean   visited        = true;   // flag to determine if the leaf node has been clicked before
    
    
    
   /***************************************************************************
    * Default constructor to creates a tree node that has no parent and no 
    * children, but which allows children.
    ***************************************************************************/
    public Node(){  this("");	}
    
    
   /***************************************************************************
    * Default constructor to creates a tree node with no parent, no children, 
    * but which allows children, and initializes it with the specified user object.
    * @param userObject the user object.
    ***************************************************************************/
    public Node(Object userObject){ 
        this.children   = new Vector();
        this.userObject = userObject;
        allowsChildren  = false;
        visited         = false;
    }
    
    
   /***************************************************************************
    * Method to remove newChild from its parent and makes it a child of this 
    * node by adding it to the end of this node's child vector.
    * @param newChild the new child node to add to the end of the child vector.
    ***************************************************************************/
    public void add(Node newChild){
    	remove(newChild);
    	newChild.setParent(this);
    	children.add(newChild);
    }


   /***************************************************************************
    * Method to create and return a vector that traverses the subtree rooted at
    * this node in breadth-first order.
    * @return the children vector of this node. 
    ***************************************************************************/
    public Vector breathFirstVector(){
    	Vector v = new Vector();
    	breathFirst(v, this);
        return v;
    }
    
    
   /***************************************************************************
    * Method used by breathFirstVector() to create the breath first vector.
    * @param v the vector to hold tree nodes.
    * @param node the node to traverse 
    ***************************************************************************/
    private void breathFirst(Vector v, Node node){
        v.add(node);	
        if (node.getChildCount() > 0){
            Node childs[] = node.childrenArray();
            for(int i = 0; i < childs.length; i++){
                breathFirst(v, childs[i]);	
            }
        }
    }
    
    
   /***************************************************************************
    * Method to return the children vector of this node.
    * @return the children vector of this node. 
    ***************************************************************************/
    public Vector children(){ return children; }


   /***************************************************************************
    * Method to return the children vector as an array of Nodes.
    * @return the children array of Nodes.
    ***************************************************************************/
    public Node[] childrenArray(){ 
        Node[] childs = new Node[children.size()];
        for (int i = 0; i < children.size(); i++)
            childs[i] = (Node) children.items[i];
        return childs; 
    }




   /***************************************************************************
    * NOT IMPLEMENTED !!!!!!!!!!!!!!
    * Method to create and return a vector that traverses the subtree rooted at
    * this node in breadth-first order.
    * @return the children vector of this node. 
    ***************************************************************************/
    public Vector depthFirstVector(){
        return null;    
        // TODO
    }


   /***************************************************************************
    * Method to return true if this node is allowed to have children.
    * @return true if this node is allowed to have children.
    ***************************************************************************/
    public boolean getAllowsChildren(){ return allowsChildren; }



   /***************************************************************************
    * Method to returns the child in this node's child array that immediately 
    * follows aChild, which must be a child of this node; otherwise, retrun null.  
    * @return the child node that immediately follows aChild node.
    ***************************************************************************/
    public Node getChildAfter(Node aChild){ return getChild(aChild, AFTER); }
    
   /***************************************************************************
    * Method to returns the child in this node's child array that immediately 
    * precedes aChild, which must be a child of this node; otherwise, retrun null.  
    * @return the child node that immediately precede aChild node.
    ***************************************************************************/
    public Node getChildBefore(Node aChild){ return getChild(aChild, BEFORE); }

    
   /***************************************************************************
    * Method to returns the child in this node's child array that immediately 
    * precedes or follows aChild (based on the specified position.  aChild must
    * be a child of this node; otherwise, retrun null.  
    * @return the child node that immediately precede aChild node.
    ***************************************************************************/
    private Node getChild(Node aChild, int position){
    	int pos = children.find(aChild);
    	if (position == BEFORE && pos > 0) return (Node) children.items[pos-1];	
    	else if (position == AFTER && pos < children.size() - 1) return (Node) children.items[pos+1];	
    	return null;
    }
    
    
   /***************************************************************************
    * Method to return the child at the specified index in this node's children 
    * vector.  Returns null if index is out of bound.
    * @return the child at the specified index.  Returns null is index out of bound.
    ***************************************************************************/
    private Node getChildAt(int index){
        if (index > -1 && index < children.size()) 
            return (Node) children.items[index];	
        else return null;
    }
    
   /***************************************************************************
    * Method to return the number of children of this node. 
    * @return the number of children of this node. 
    ***************************************************************************/
    public int getChildCount(){ return children.size(); }

   /***************************************************************************
    * Method to return this node's first child. 
    * @return this node's first child.  
    ***************************************************************************/
    public Node getFirstChild(){ return getChildAt(0); }
    

   /***************************************************************************
    * Method to return this node's last child. 
    * @return this node's last child.  
    ***************************************************************************/
    public Node getLastChild(){ return getChildAt(children.size()-1); }


   /***************************************************************************
    * Method to return the number of levels above this node -- the distance 
    * from the root to this node.
    * @return the number of levels above this node -- the distance from the 
    *  root to this node
    ***************************************************************************/
    public int getLevel(){ 
        int lvl = 0;
        if (this.isRoot()) return lvl;
        
        Node node = getParent();
        lvl++;
        while (!node.isRoot()){
        	node = node.getParent();
            lvl++;
        }
        return lvl;
    }
          

   /***************************************************************************
    * Method to return the node name of this node.
    * @return the node name of this node.
    ***************************************************************************/
    public String getNodeName(){ return (userObject == null)? "": userObject.toString(); }
          

   /***************************************************************************
    * Method to return the next sibling of this node in the parent's children 
    * array. Returns null if this node has no parent or is the parent's last
    * child. This method performs a linear search that is O(n) where n is the
    * number of children
    * @return the next sibling of this node in the parent's children array. 
    *  Returns null if this node has no parent or is the parent's last child.
    ***************************************************************************/
    public Node getNextSibling(){ 
        return (parent != null)? parent.getChildAfter(this): null;
    }


   /***************************************************************************
    * Method to return the previous sibling of this node in the parent's 
    * children array. Returns null if this node has no parent or is the parent's 
    * first child. This method performs a linear search that is O(n) where n is 
    * the number of children
    * @return the previous sibling of this node in the parent's children array. 
    *  Returns null if this node has no parent or is the parent's in the tree.
    ***************************************************************************/
    public Node getPreviousSibling(){ 
        if (parent == null) return null;
        return parent.getChildBefore(this); 
    }
          

   /***************************************************************************
    * Method to return this node's parent or null if this node has no parent. 
    ***************************************************************************/
    public Node getParent(){ return parent; }
          

   /***************************************************************************
    * Method to return the path from the root, to get to this node. 
    * @return the path from the root, to get to this node. 
    ***************************************************************************/
    public Node[] getPath(){
        Vector v = new Vector();
        pathFromRootToNode(v, this);
        
        Node p[] = new Node[v.size()];
        for (int i = 0; i < p.length; i++)
            p[i] = (Node) v.items[i];
        return p;
    }

   /***************************************************************************
    * Method to builds the parents of node up to and including the root node, 
    * where the original node is the last element in the returned array. 
    * @return the path from this node to the root node, including the root node.
    ***************************************************************************/
    protected  Node[] getPathToRoot() {
        Vector v = new Vector();
        pathFromNodeToRoot(v, this);
        
        Node p[] = new Node[v.size()];
        for (int i = 0; i < p.length; i++)
            p[i] = (Node) v.items[i];
        return p;
    }    
    
        
   /***************************************************************************
    * Method to get the path from the root to the specified node.
    * @param v the vector to hold the path.
    * @param node the specified node.
    ***************************************************************************/
    private void pathFromRootToNode(Vector v, Node node){
        if (node == null) return;	
        pathFromRootToNode(v, node.getParent());
        v.add(node);
    }
    
    
   /***************************************************************************
    * Method to get the path from the specified node to the root node.
    * @param v the vector to hold the path.
    * @param node the specified node.
    ***************************************************************************/
    private void pathFromNodeToRoot(Vector v, Node node){
    	if (node == null) return;
    	v.add(node);
        pathFromNodeToRoot(v, node.getParent());
    }
    


    

   /***************************************************************************
    * Method to return the root of the tree that contains this node.
    * @return the root of the tree that contains this node.
    ***************************************************************************/
    public Node getRoot(){ 
        Node node = this;
        while (!node.isRoot())
            node = node.getParent();
        return node;
    }


   /***************************************************************************
    * Method to return this node's user object. 
    * @return this node's user object. 
    ***************************************************************************/
    public Object getUserObject(){ return userObject; }
          

   /***************************************************************************
    * Method to return the user object path, from the root, to get to this node. 
    * @return the user object path, from the root, to get to this node. 
    ***************************************************************************/
    public Object[] getUserObjectPath(){
    	Node nodes[] = getPath();
    	Object obj[] = new Object[nodes.length];
    	for (int i = 0; i < obj.length; i++)
    	    obj[i] = nodes[i].getUserObject();
    	return obj;
    } 
          

   /***************************************************************************
    * Method to removes newChild from its present parent (if it has a parent), 
    * sets the child's parent to this node, and then adds the child to this 
    * node's child array at index childIndex.  If childINdex is out of bound,
    * newChild will be inserted at the end of the children vector
    * @param newChild the new child to remove from this subtree and add to
    *  this node children vector at the specified index.
    * @param childIndex the position in the children vector to insert newChild.
    * @return the child index.
    ***************************************************************************/
    public int insert(Node newChild, int childIndex){
    	remove(newChild);
    	
    	try {  
    	    children.insert(childIndex, newChild); 
    	    newChild.setParent(this);
    	    return children.find(newChild); 
    	}
    	catch (Exception e){ 
			waba.sys.Vm.debug("insert: Exception "+e.getMessage());
    	    children.add(newChild); 
    	    newChild.setParent(this);
    	    return children.size() - 1; 
    	}
    } 
                   
          
   /***************************************************************************
    * Method to return true if this node has been visited
    * @return true if this node has been visited.
    ***************************************************************************/
    public boolean isVisited(){ return visited; }
    

   /***************************************************************************
    * Method to return true if this node has no children. 
    * @return true if this node has no children. 
    ***************************************************************************/
    public boolean isLeaf(){ return (children.size() == 0); }
          

   /***************************************************************************
    * Method to return true if this node has no children. 
    * @return true if this node has no children. 
    ***************************************************************************/
    public boolean isLeaf(boolean useAllowsChildren){ 
        if (useAllowsChildren)
            return (children.size() == 0 && !this.allowsChildren); 
        else return (children.size() == 0);
    }
                 

   /***************************************************************************
    * Method to return true if this node is a root.  Root node is node that
    * has a null parent node.
    ***************************************************************************/
    public boolean isRoot(){ return (parent == null); }


   /***************************************************************************
    * Method to return true if aNode is a child of this node. 
    * @param aNode the node to deterimine if it's a shild of this node.
    * @return true if aNode is a child of this node. 
    ***************************************************************************/
    public boolean isNodeChild(Node aNode){
        for (int i = 0; i < children.size(); i++){
            if (aNode == (Node) children.items[i])
                return true;
        }
        return false;
    }
          

   /***************************************************************************
    * Method to return true if anotherNode is a sibling of (has the same parent
    * as) this node.
    * @return true if anotherNode is a sibling of (has the same parent as) this node.
    ***************************************************************************/
    public boolean isNodeSibling(Node anotherNode){
    	if (parent == null) return false;
    	return (parent == anotherNode.parent);
    }
          
   /***************************************************************************
    * Method to remove the child at the specified index from this node's 
    * children and sets that node's parent to null. 
    * @param childIndex the index of the this node's children to be removed
    ***************************************************************************/
    public void remove(int childIndex){
    	if (childIndex > -1 && childIndex < children.size()){
    		Node child = (Node) children.items[childIndex];
    		child.setParent(null);
    	    children.del(childIndex);
    	}
    }
          
	/***************************************************************************
	 * Method to move this node up the list. Do nothing if this is the root node,
	 * already at the top, or the only node in the list. 
	 ***************************************************************************/
	 public void moveUp(){
	 	if (parent==null) return;
	 	if (parent.children.size()<2) return;
	 	int me=parent.children.find(this);
	 	if (me<1) return;
		/* OK, it looks safe to swap this entry and the one above it. */
		Object tmp=parent.children.items[me];
		parent.children.items[me]=parent.children.items[me-1];
		parent.children.items[me-1]=tmp;
	 }
          
	/***************************************************************************
	 * Method to move this node down the list. Do nothing if this is the root node,
	 * already at the botton, or the only node in the list. 
	 ***************************************************************************/
	 public void moveDown(){
		if (parent==null) return;
		if (parent.children.size()<2) return;
		if (this==parent.getLastChild()) return;
		/* OK, it looks safe to swap this entry and the one below it. */
		int me=parent.children.find(this);
		Object tmp=parent.children.items[me];
		parent.children.items[me]=parent.children.items[me+1];
		parent.children.items[me+1]=tmp;
	 }
          
   /***************************************************************************
    * Method to remove aChild from this node's child array, giving it a null parent. 
    * @param aChild the child node to remove.
    ***************************************************************************/
    public void remove(Node aChild){
    	for (int i = 0; i < children.size(); i++){
    	    if (aChild == (Node) children.items[i]){
    	        aChild.setParent(null);
    	        children.del(i);
    	        return;
       	    }
    	}
    }
    
          

   /***************************************************************************
    * Method to remove all of this node's children, setting their parents to null. 
    ***************************************************************************/
    public void removeAllChildren(){
    	for (int i = 0; i < children.size(); i++){
    	    Node node = (Node) children.items[i];
    	    node.setParent(null);
    	}
    	children.clear();
    }
          

   /***************************************************************************
    * Method to remove the subtree rooted at this node from the tree, giving 
    * this node a null parent. 
    ***************************************************************************/
    public void removeFromParent(){	parent.remove(this);  }
          

          
          
   /***************************************************************************
    * Method to determine whether or not this node is allowed to have children. 
    * @param allows determine whether or not this node is allowed to have children. 
    ***************************************************************************/
    public void setAllowsChildren(boolean allows){ allowsChildren = allows; }

   /***************************************************************************
    * Method to sets this node's parent to newParent but does not change the 
    * parent's child array. 
    * @param parent the newParent node
    ***************************************************************************/
    public void setParent(Node parent){ this.parent = parent; }
          

   /***************************************************************************
    * Method to set the user object for this node to userObject. 
    * @param userObject the user object of this node.
    ***************************************************************************/
    public void setUserObject(Object userObject){ this.userObject = userObject; } 


          
   /***************************************************************************
    * Method to set the node visited property.
    * @param visited the new visited property.
    ***************************************************************************/
    public void setVisited(boolean visited){ this.visited = visited; }      


          
          
   /***************************************************************************
    * Method to return the result of sending toString() to this node's user 
    * object, or null if this node has no user object. 
    * @return the result of sending toString() to this node's user object, 
    *  or null if this node has no user object. 
    ***************************************************************************/
    public String toString(){ return getNodeName(); }

	/***************************************************************************
	 * Add this image to the list of user icons.
	 * @param img
	 */          
   public void addIcon(NodeIcon ni) {
   		userIcons.add(ni);
   }

   /***************************************************************************
		* Eliminate tthe existing list of user icons.
		* @param img
		*/          
	  public void deleteIcons() {
		   userIcons=new Vector();
	  }

   /***************************************************************************
    * Method to display the root structor to the console. 
    * For debugging - Check what the node structure looks like
    ***************************************************************************/
    public void display(Node node, String indent){
    	//Vm.debug(indent + node.toString() + " [" + node.getLevel() + "] - " + node.isLeaf());
    	
    	Node childs[] = node.childrenArray();
    	for (int i = 0; i < childs.length; i++)
    	    display(childs[i], indent + "     ");
    }       
          
}



