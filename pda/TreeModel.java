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
 *    File:         TreeModel.java
 *    Date:         Sept 5,2004.
 *    Author:       Tri (Trev) Quang Nguyen.
 *    Version:      0.9 
 *    Email:        tnguyen@ceb.nlm.nih.gov
 *    
 *    Description:  This class holds the tree structure that the Tree class used
 *                  to render the tree widget.
 *                  
 *    Note:         you should use this class to mofidy the tree after the root 
 *                  node is setted to this model; otherwise, the tree will not 
 *                  update the node correctly.
 *
 *	Modifications:
 *		Added getTree() method. - vik@diamondage.co.nz 19-Nov-2004
 *
 ********************************************************************************/
public class TreeModel  {
	private Tree tree;
	private Node root;
    private boolean allowsChildren = true;
    
   /*********************************************************************
    * Constructor to create a tree model with the specified root node and 
    * with the specified allowsChildren flag.
    *********************************************************************/ 
	public TreeModel(Node root, boolean allowsChildren){
	    this.root = (root != null)? root: new Node("");	
	    this.allowsChildren = allowsChildren;
	}
	
   /*********************************************************************
    * Constructor to create a tree model with the specified root node and 
    * with allowsChildren is true.
    *********************************************************************/ 
	public TreeModel(Node root){ this(root, true); }
	
   /*********************************************************************
    * Constructor to create an empty tree model with allowsChildren is true.
    *********************************************************************/ 
	public TreeModel(){ this(true);	}
	
   /*********************************************************************
    * Constructor to create an empty tree model that use allowsChildren to
    * determine the leaf node, if and only if allowsChildren is true.
    * @param allowsChildren true to use allowwsChildren to determine a leaf node.
    *********************************************************************/ 
    public TreeModel(boolean allowsChildren){
    	this(null, allowsChildren);
    }	
	

   /*********************************************************************
    * Method to set the tree (For internal use only)
    * This method register the tree to this model, so when the user add,
    * delete, or modify a node, the tree view will be notify and updated.
    * @param tree the tree (view) that is associated with this model.
    *********************************************************************/ 
	public void setTree(Tree tree){
	    this.tree = tree;	
	}
	
	/*********************************************************************
	 * Method to get the tree
	 * This method gets the tree registered to this model, so when the user 
	 * wants to adds delete, or modify a node, it knows what tree to use if only
	 * the model is passed.
	 * @param tree the tree (view) that is associated with this model.
	 *********************************************************************/ 
	 public Tree getTree(){
		 return tree;	
	 }
	
   /*********************************************************************
    * Method to return true if use allowsChildren to determine a node is
    * a leaf or a folder.
    * @return true if a node's allowsChildren flag should be used to 
    * determine if the node is a leaf; otherwise, return false.
    *********************************************************************/ 
    public boolean getAllowsChildren(){ return allowsChildren; }
    	
	
   /*********************************************************************
    * Method to clear this model.
    *********************************************************************/ 
	public void clear(){
	    root = new Node("");	
	    tree.setModel(this);
	}

    
	
   /*********************************************************************
    * Method to notify the tree to reload.
    *********************************************************************/ 
	public void reload(){
	    if (tree != null) tree.reload();
	}
	
   /*********************************************************************
    * Method to return the root node of this tree model.
    * @return the root node of this tree model.
    *********************************************************************/ 
	public Node getRoot(){ return root; }


   /*********************************************************************
    * Method to set the root node of this tree model and notify the tree
    * to reload the tree.
    * @param root the new root node of this tree model.
    *********************************************************************/ 
	public void setRoot(Node root){ 
	    this.root = root; 
	    reload();
	}
	
	
	
   /*********************************************************************
    * Method to insert a node to the parent node at the specified position.
    * This method will notify the associated tree to display the node,
    * if the parent node is expanded.  If the index out of range, the new
    * node will be inserted at the end of the parent node children vector.
    * @param parent the parent node of the node to insert.
    * @param newNode the new node to insert into this tree model.
    * @param index the index to insert the node into
    *********************************************************************/ 
	public void insertNode(Node parent, Node newNode, int index){
		if (parent == null || newNode == null) return;
	
		// cannot insert into a leaf node
	    if (parent.isLeaf(allowsChildren)) {
	    	waba.sys.Vm.debug("insertNode:Parent does not allow children.");
	    	return;
	    }
	
	
	    Vector v = root.breathFirstVector();
	    int pos = v.find(parent);
	    if (pos != -1){
	    	index = parent.insert(newNode, index);
    		if (tree != null) {
				if (tree.nodeInserted(parent, newNode, index)==false) {
					waba.sys.Vm.debug("insertNode:Tree not modified.");
				}
    		} else{
				waba.sys.Vm.debug("insertNode:Tree was null.");
			} 
	    } else {
			waba.sys.Vm.debug("insertNode:New node has no parent.");
	    }
	}
	
	
	
   /*********************************************************************
    * Method to remove a node from the tree.  This method will notify
    * the tree to collapse the tree.
    * @param parent the parent node of the node to remove.
    * @param node the node to remove from this tree model.
    *********************************************************************/ 
	public void removeNode(Node parent, Node node){
		// cannot delete root node
		if (parent == null || node == null) return;

        parent.remove(node);
		if (tree != null)
            tree.nodeRemoved(node);		
	}
	
	
   /*********************************************************************
    * Method to modify a node userObject and notify the tree of the changes.
    * @param node the node to modify.
    * @param userObject the new user object.
    *********************************************************************/ 
	public void modifyNode(Node node, Object userObject){
	    if (node == null)return;
	    
	    node.setUserObject(userObject);
	    tree.nodeModified(node);
	}
	
  
}
