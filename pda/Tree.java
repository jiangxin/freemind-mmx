/*******************************************************************************
 *  SuperWaba Virtual Machine, version 4                                       *
 *  Copyright (C) 2003-2004 Guilherme Campos Hazan <support@superwaba.com.br>  *
 *  Copyright (C) 2001 Daniel Tauchke                                          *
 *  All Rights Reserved                                                        *
 *                                                                             *
 *  This library and virtual machine is free software; you can redistribute    *
 *  it and/or modify it under the terms of the Amended GNU Lesser Genegral      *
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

// ListBox.java written 2001 by Daniel Tauschke and a little modified by guich
// http://www.tauschke.com
// E-mail: tauschke@ tauschke.com


import waba.ui.*;
import waba.fx.*;
import waba.util.*;
import waba.sys.*;


/*******************************************************************************
 *
 *    File:           Tree.java
 *    Date:           August 26,2004.
 *    Last Modified:  September 5, 2004.
 *    Author:         Tri (Trev) Quang Nguyen.
 *    Version:        0.9 
 *    Email:          tnguyen@ceb.nlm.nih.gov
 *    
 *    Description:    This class is a simple implementation of a tree widget.
 *                    Since it's natural to render the tree in rows, this class
 *                    borrows most of the code from Waba's ListBox.
 *                    
 *    Features:   
 *    
 *         - similiar to Microsoft Window Explorer tree
 *         - horizontal and vertical scrolling
 *         - allows setting of folder and leaf icons.               
 *         - angled line
 *         - expands and collapse of folder  
 *         - allowsChildren flag to determine if the node is a leaf or folder
 *         - delete, insert, and modify (user object) of a node
 *         - clicking on leaf node will swap leaf icon (like hyperlink)
 *         - allows creation of tree to show or hide root node.
 *
 *    Note:           You should use TreeModel class to mofidy the tree after
 *
 ********************************************************************************/
public class Tree extends Container{
    //private static final Color red     = new Color(255,0,0);
    //private static final Color green   = new Color(0, 255, 0);
    //private static final Color blue    = new Color(0,0,255);
    //private static final Color yellow  = new Color(255,255,200);
    //private static final Color aqua    = new Color(225,255,255); 
    
    protected Image        imgPlus;                    // the expand icon "-"
    protected Image        imgMinus;                   // the expand icon "+"
    protected Image        imgOpen;                    // the open folder icon
    protected Image        imgClose;                   // the close folder icon
    protected Image        imgVisit;                   // the visited file icon
    protected Image        imgFile;                    // the unvisited file icon
	
    protected ScrollBar    vbar;                       // vertical scrollbar
    protected ScrollBar    hbar;                       // horizontal scrollbar

    protected TreeModel    model   = null;             // holds the (original) node tree structure
    protected Vector       items   = new Vector();     // hold the nodes to be drawn
    protected IntVector    levels  = new IntVector();  // hold the level (for faster rendering) 
    protected IntVector    expands = new IntVector();
    
    protected boolean      simpleBorder;               // used by PopList
    protected int          offset;                     // the vertical offset 
    protected int          hsOffset;                   // the horizontal offset
    protected int          selectedIndex = -1;         // the selected index
    protected int          itemCount;                  // the vertical scrollbar maximum (size of items vector)
    protected int          hsCount;                    // the horizontal scrollbar maximum
    protected int          visibleItems;               // the first visible item display
    protected int          btnX;                       // the vertical scrollbar's x position

    private   int          maxLevel       = 0;         // the max level to expand when setting the root node.
    private   boolean      showRoot       = false;     // flag to show the root node or hide it. 
    private   boolean      allowsChildren = true;      // flag to use the node's allowsChildren to determine if the node is a leaf or not 
   
    private   Color        fColor;
    private   Color        bgColor0;
    private   Color        bgColor1;
    private   Color        cursorColor;
    private   Color        fourColors[] = new Color[4];
    private   Graphics     myg;

    // variable used for x position to draw the icons and line connectors. 
    private   int          w1     = 0;   // width of collapse[imgPlus] icon
    private   int          h1     = 0;   // height of collapse[imgPlus] icon
    private   int          w2     = 0;   // width of folder [imgOpen] icon
    private   int          h2     = 0;   // height of folder [imgOpen] icon
    private   int          hline  = 3;   // number of pixel used to draw horizontal line (from plus icon to gap betwwen plus icon and folder or leaf icon)
    private   int          gap    = 2;   // the number of space(in pixel) between the plus icon and the folder or leaf icon.


    // scrollbars policies (not implemented yet - but still some bugs)   
    //     0 = always
    //     1 = as needed
    //     2 = never
    private int hbarPolicy = 0;   
    private int vbarPolicy = 0;
    

   /*********************************************************************
    *********************************************************************/ 
    public Tree(){ this(new TreeModel());}


   /*********************************************************************
    *********************************************************************/ 
    public Tree(TreeModel model){ this(model, true); }


   /*********************************************************************
    *********************************************************************/ 
    public Tree(TreeModel model, boolean showRoot){ 
        super.add(vbar = new ScrollBar(ScrollBar.VERTICAL));
        super.add(hbar = new ScrollBar(ScrollBar.HORIZONTAL));
        vbar.setLiveScrolling(true);
        hbar.setLiveScrolling(true);
        this.showRoot       = showRoot;
        this.allowsChildren = (model == null)? true: model.getAllowsChildren();
        setModel(model);
        
        if (Settings.isColor)
            setCursorColor(new Color(225,255,255));   // aqua (cursor color) highlight
    }
    

   /*********************************************************************
    * Method to set the tree model.
    * @param model the tree model.
    *********************************************************************/ 
    public void setModel(TreeModel model){
    	clear();
        this.model = (model != null)? model: new TreeModel();
        model.setTree(this);
        initTree(model.getRoot());
    }


   /*********************************************************************
    * Method to set the tree properties.  
    * Currently, only support setting the horizontal scrollbar policy
    * @param policyType = "Tree.Horizontal_ScrollBar"
    * @param policyValue = "ScrollBar_ALWAYS", "ScrollBar_AS_NEEDED", or "ScrollBar_NEVER"
    *********************************************************************/ 
    public void setPolicy(String policyType, String policyValue){
        if (policyType.equals("Tree.Horizontal_ScrollBar")){
        	if (policyValue.equals("ScrollBar_ALWAYS")){         
        	    hbarPolicy = 0;    
        	    if (!hbar.isVisible())
        	    	resetScrollBars();
        	}
            else if (policyValue.equals("ScrollBar_AS_NEEDED")){ 
                hbarPolicy = 1;
                resetScrollBars();
            }
            else if (policyValue.equals("ScrollBar_NEVER")){
            	hbarPolicy = 2;
            	hbar.setVisible(false);
            }
        } 	
    }

    
   /*********************************************************************
    * Method to set the tree root node with the new root node.  If the 
    * new root node is null, the tree is unchanged.
    * @param root the new tree root node.
    *********************************************************************/ 
    public void initTree(Node root){
    	if (root != null){
        	if (showRoot){
        	    items.add(root);
        	    levels.add(0);
        	    expands.add(0);
        	    expand(root);
        	}
        	else {    
        	    Node childs[] = root.childrenArray();
        	    for (int i = 0; i < childs.length; i++){
   	                items.add(childs[i]);
   	                levels.add(1);
   	                expands.add(0);
    	        }	
    	    }
    	}
    	resetScrollBars();
        initImage();
    }
    

   /*********************************************************************
    * Method to initialize the vertical and horizontal scrollbars maximum.
    *********************************************************************/ 
    protected void initScrollBars(){
    	// initialize the vertical scrollbar
        itemCount = items.size();
        vbar.setEnabled(enabled && visibleItems < itemCount);
        vbar.setMaximum(itemCount); // guich@210_12: forgot this line!

        // initialize the horizontal scrollbar
        int maxWidth = 0;
        for (int i = 0; i < this.items.size(); i++)
  	        maxWidth = Math.max(getItemWidth(i), maxWidth);
        maxWidth = maxWidth - (width - vbar.getPreferredWidth());
        hsCount = (maxWidth > 0)? maxWidth: 0;
        hbar.setEnabled(enabled && hsCount > width - vbar.getPreferredWidth());
        hbar.setMaximum(hsCount); 

        switch (hbarPolicy){ 
            case 0:   hbar.setVisible(true); break;
            case 1:
                if (hsCount > width - vbar.getPreferredWidth()) hbar.setVisible(true);
                else hbar.setVisible(false);
            case 2:   hbar.setVisible(false); break;
        } 
    }
    
    

   /*********************************************************************
    * Method to load icons use for the tree.  You can change the icon by
    * using the setIcon(int iconType, Filename imageFilename).
    *********************************************************************/ 
    protected void initImage(){
    	try {
    		if (Settings.screenWidth > 160){
                setIcon(0,"cePlus.bmp");
                setIcon(1,"ceMinus.bmp");
                setIcon(2,"ceClose.bmp");
                setIcon(3,"ceOpen.bmp");
                setIcon(4,"ceFile.bmp");
                setIcon(5,"ceFileOpen.bmp");
            }
            else {
                setIcon(0,"plus.bmp");
                setIcon(1,"minus.bmp");
                setIcon(2,"close.bmp");
                setIcon(3,"open.bmp");
                setIcon(4,"file.bmp");
                setIcon(5,"fileOpen.bmp");
            }
        }
        catch (Exception e){  /* TODO */  }
    }
    
        
        
   /*********************************************************************
    * Method to set the icon of the tree based on the icon type. 
    * Note: You should not change the plus and minus icons.
    * @param iconType  0 - plus icon  "+"
    *                  1 - minus icon "-" 
    *                  2 - open folder icon
    *                  3 - close folder icon
    *                  4 - file icon
    *                  5 - file opened (visited) icon
    * @param filename the filename of the image to load.
    * @throws Exception
    *********************************************************************/ 
    public void setIcon(int iconType, String filename) throws Exception {
    	Image img = new Image(filename);
    	
        switch (iconType){
             case 0: 
                 imgPlus  = img;  
                 w1 = img.getWidth();
                 h1 = img.getHeight();
                 break; 
             case 1: imgMinus = img;  break;
             case 2: imgClose = img;  break;
             case 3: 
                 imgOpen  = img;  
                 w2 = img.getWidth();
                 h2 = img.getHeight();
                 break;
                 
             case 4: imgFile  = img;  break;
             case 5: imgVisit = img;  break;
        }	
    }
        

   /*********************************************************************
    * Method to return the width of the given item index with the current 
    * fontmetrics. 
    * Note: if you overide this class you must implement this method. 
    *********************************************************************/ 
    protected int getItemWidth(int index){
        return fm.getTextWidth(items.items[index].toString());
    }
    
    

   /*********************************************************************
    * Method to empties this Tree, setting all elements of the array to 
    * null, so they can be garbage collected.
    *********************************************************************/ 
    public void removeAll(){ // guich@210_13
        model         = new TreeModel();
        clear();
    }
    

   /*********************************************************************
    * Same as removeAll() method.  Just more clearer method name
    *********************************************************************/ 
    public void clear(){ 
        items.clear();
        levels.clear();
        expands.clear();
        vbar.setMaximum(0);
        hbar.setMaximum(0);
        itemCount     = 0;
        hsCount       = 0;
        offset        = 0;  // wolfgang@330_23
        hsOffset      = 0;
        selectedIndex = -1; // seanwalton@401_26
        repaint();
    }

    
    
   /*********************************************************************
    * Method to insert the items to the tree (For internal uses)
    * Note: this method does not reset the scroll bar..you need to call this
    * resetScrollBars() after you have performed an insert.
    *********************************************************************/ 
    private void insert(int index, Node node, int level, int expandValue){
       	items.insert(index, node);
       	levels.insert(index, level);
       	expands.insert(index, expandValue);
    }


   /*********************************************************************
    * Method to remove the given index from the Tree items vector.
    * This method will not remove the node from the original node. 
    * @param index the item index in the items vector.
    *********************************************************************/ 
    public void remove(int index){ // guich@200final_12: new method
        if (index < 0 || index > itemCount-1) return;
       
        int  level = levels.items[index];
        do {
            items.del(index);
            levels.del(index);
            expands.del(index);
            itemCount--;
        } while (level < levels.items[index] && index < itemCount);

        resetScrollBars(); 
        repaint();
    }




   /*********************************************************************
    * Method to remove an Object from the Tree's items vector. 
    * @paran the Node to delete from the tree's item vector.
    *********************************************************************/ 
    public void remove(Object item){
        int index = items.find(item);
        if (itemCount > 0 && index != -1)
            remove(index);
    }



   /*********************************************************************
    * Method to reset the horizontal scroll bar properties.
    *********************************************************************/ 
    private void resetHBar(){
    	if (hbarPolicy == 2)  return;
    	    
        // calculate the horizontalscrollbar maximum
        int max    = 0;
        int indent = 3 + (w1+hline+w2/2-w1/2);
        for (int i = 0; i < items.size(); i++){
			max = Math.max(max, fm.getTextWidth( ((Node) items.items[i]).getNodeName()) + indent*levels.items[i]);  // bug: calculation is off
        }
        max += vbar.getPreferredWidth();  // remember to take into account of the pixels used to draw the icons and scrollbar      

        hbar.setMaximum(max);   
        if (hbarPolicy == 0 || (width-vbar.getPreferredWidth()) < max) {
            hbar.setEnabled(enabled && (width-vbar.getPreferredWidth()) < max);
            hbar.setVisible(true);
        }
        else hbar.setVisible(false);
    }


   /*********************************************************************
    * Method to reset the horizontal scroll bar properties.
    *********************************************************************/ 
    private void resetVBar(){
    	itemCount = items.size();
        vbar.setMaximum(itemCount);
        vbar.setEnabled(enabled && visibleItems < itemCount);

        if (selectedIndex == itemCount) //last item was removed?
            select(selectedIndex-1);

        if ( itemCount == 0 )  // olie@200b4_196: if after removing the list has 0 items, select( -1 ) is called, which does nothing (see there), then selectedIndex keeps being 0 which is wrong, it has to be -1
            selectedIndex = -1;

        if (itemCount <= visibleItems && offset != 0) // guich@200final_13
            offset = 0;
    }


   /*********************************************************************
    * Method to rest the vertical and horizontal scrollbars properties.
    * Note: there's still a bug in resetting the horizontal scroll bar.
    *********************************************************************/ 
    private void resetScrollBars(){
        resetVBar();
        resetHBar();           
    }


   /*********************************************************************
    * Method to expand a collapsed node.
    * @param node the collapse node to expand.
    *********************************************************************/ 
    public void expand(Node node){
    	int index = indexOf(node);
    	if (index != -1 && expands.items[index] == 0 && !node.isLeaf(allowsChildren)){
    		expands.items[index] = 1;
    	    Node childs[] = node.childrenArray();
    	    for (int i = 0; i < childs.length; i++){
       	        index++;
       	        insert(index, childs[i], childs[i].getLevel(), 0);
    	    }	
            resetScrollBars();             
            repaint();    	
    	}
    }


   /*********************************************************************
    * Method to collapse an expanded node.
    * @param node the expanded node to collapse.
    *********************************************************************/ 
    public void collapse(Node node){
        int index = indexOf(node);
        
        if (index != -1 && expands.items[index] == 1 && !node.isLeaf(allowsChildren)){
            int level = levels.items[index];
            index++;
            while (index < itemCount && level < levels.items[index]){
                levels.del(index);
                items.del(index);
                expands.del(index);
                itemCount++;	
            }
            expands.items[index-1] = 0;
            resetScrollBars();
            repaint();    	            
        }
    }



   /*********************************************************************
    * Method to set the Object at the given Index, starting from 0.
    * @param i the index
    * @param s the object to set.
    *********************************************************************/ 
    public void setItemAt(int i, Object s){
        if (0 <= i && i < itemCount){
            items.items[i] = s;
            repaint();
        }
    }



   /*********************************************************************
    * Method to get the Object at the given Index. Returns an empty 
    * string in case of error. 
    * @param i the index.
    *********************************************************************/ 
    public Object getItemAt(int i){
        if (0 <= i && i < itemCount)
            return items.items[i];//get(i);
        return "";
    }


   /*********************************************************************
    * Method to return the selected node from the tree.  Return null if
    * no selection has been made.
    * @return the selected Node, or null is no selection has been made.
    *********************************************************************/ 
    public Node getSelectedNode(){
        return selectedIndex >= 0 ? (Node)items.items[selectedIndex] : null; // guich@200b4: handle no selected index yet.
    }

   /*********************************************************************
    * Method to return the selected item of the Tree or an empty String
    * if no selection has been made.
    * @return the selected object, or null is no selection has been made.
    *********************************************************************/ 
    public Object getSelectedItem(){
        return selectedIndex >= 0 ? items.items[selectedIndex] : ""; // guich@200b4: handle no selected index yet.
    }


   /*********************************************************************
    * Method to return the position of the selected item of the Tree or 
    * -1 if the Tree has no selected index yet. 
    * @return the selected index or -1 if no selection has been made.
    *********************************************************************/ 
    public int getSelectedIndex(){  return selectedIndex;  }
   

   /*********************************************************************
    * Method to return all items in the items vector as an array of object.
    * The objects are of the class Node.  
    * @return all items in items vector as an array of Objects.
    *********************************************************************/ 
    public Object []getItems(){  return items.toObjectArray();  }


   /*********************************************************************
    * Method to return the index of the item specified by the name, or -1 
    * if not found. 
    * @param name the object to find.
    * @return the index of the item specified by the name, or -1 if not found. 
    *********************************************************************/ 
    public int indexOf(Object name){  return items.find(name);  }


   /*********************************************************************
    * Method to select the given name. If the name is not found, the current 
    * selected item is not changed.
    * @since SuperWaba 4.01
    * @param name the object to select.
    *********************************************************************/ 
    public void select(Object name){ // guich@401_25
        int pos = indexOf(name);
        if (pos != -1)
           select(pos);
    }

   /*********************************************************************
    * Method to select the given index and scroll to it if necessary. 
    * Note: select must be called only after the control has been added 
    *       to the container and its rect has been set. 
    * @param i the index of the item.
    *********************************************************************/ 
    public void select(int i) {
        if (0 <= i && i < itemCount && i != selectedIndex && height != 0){
            offset=i;
            int vi = vbar.getVisibleItems();
            int ma = vbar.getMaximum();
            if (offset+vi > ma) // astein@200b4_195: fix list items from being lost when the comboBox.select() method is used
                offset=Math.max(ma-vi,0); // guich@220_4: fixed bug when the listbox is greater than the current item count

            selectedIndex = i;
            vbar.setValue(offset); // guich@210_9: fixed scrollbar update when selecting items
            repaint();
        }
        else if (i == -1){ // guich@200b4_191: unselect all items
            offset = 0;
            vbar.setValue(0);
            selectedIndex = -1;
            repaint();
        }
    }


   /*********************************************************************
    * Returns the number of items (Nodes)
    *********************************************************************/ 
    public int size(){   return itemCount;  }

   /*********************************************************************
    * Do nothing. 
    *********************************************************************/ 
    public void add(Control control){ }

   /*********************************************************************
    * Do nothing. 
    *********************************************************************/ 
    public void remove(Control control){ }


   /*********************************************************************
    * Method to return the preferred width, ie, size of the largest item plus 20.
    * @return the preferred width of this control.
    *********************************************************************/ 
    public int getPreferredWidth(){
        int maxWidth = 0;
        int n = itemCount;
        for (int i = 0; i < n; i++)
            maxWidth = Math.max(getItemWidth(i), maxWidth);
        return maxWidth + (simpleBorder?4:6) + vbar.getPreferredWidth();
    }

   /*********************************************************************
    * Method to return the number of items multiplied by the font metrics 
    * height 
    * @return the preferred height of this control.
    *********************************************************************/ 
    public int getPreferredHeight() {
        int n = itemCount;
        int h = Math.max(fmH*n,vbar.getPreferredHeight())+(simpleBorder?4:6);
        return n==1 ? h-1 : h;
    }




   /*********************************************************************
    * Method to search this Tree for an item with the first letter matching 
    * the given char. The search is made case insensitive. Note: if you 
    * override this class you must implement this method. 
    *********************************************************************/ 
    protected void find(char c) {
       for (int i =0; i < itemCount; i++) {
           String s = items.items[i].toString(); // guich@220_37
         
           // first letter matches and not the already selected index?
           if (s.length() > 0 && Convert.toUpperCase(s.charAt(0)) == c && selectedIndex != i){
               select(i);
               repaint();
               break; // end the for loop
           }
       }
    }




   /*********************************************************************
    * Method to enable this control if the specified enabled flag is true.
    *********************************************************************/ 
    public void setEnabled(boolean enabled){
        if (enabled != this.enabled){
            this.enabled = enabled;
            onColorsChanged(false);
            vbar.setEnabled(enabled && visibleItems < itemCount);
            repaint(); // now the controls have different l&f for disabled states
        }
    }






   /*********************************************************************
    *********************************************************************/ 
    protected void onColorsChanged(boolean colorsChanged){
        if (colorsChanged)
            vbar.setBackForeColors(backColor,foreColor);
        fColor = getForeColor();
        bgColor0  = getBackColor().brighter();
        bgColor1  = cursorColor!=null?cursorColor:(bgColor0.equ != Color.WHITE.equ)?backColor:bgColor0.getCursorColor();//guich@300_20: use backColor instead of: bgColor0.getCursorColor(); // guich@210_19
      
        if (fColor.equ == bgColor1.equ) // guich@200b4_206: ops! same color?
            fColor = foreColor;
        Graphics.compute3dColors(enabled,backColor,foreColor,fourColors);
    }




   /*********************************************************************
    * Method to recalculate the box size for the selected item  if the
    * control is resized by the main application .
    *********************************************************************/ 
    protected void onBoundsChanged(){
        int btnW = vbar.getPreferredWidth();
        //int m = simpleBorder?1:2;
        visibleItems = ((height-2 - hbar.getPreferredHeight()) / fmH);
        vbar.setMaximum(itemCount);
        vbar.setVisibleItems(visibleItems);
        vbar.setEnabled(visibleItems < itemCount);
        btnX = width - btnW;

        //if (Settings.uiStyle == Settings.PalmOS) {btnX--; m++;}
        vbar.setRect(btnX,0,btnW,height);
        int hsH = hbar.getPreferredHeight();
        hbar.setRect(0, height - hsH, width - btnW, hsH);
        myg = createGraphics(); // guich@350_25: create a new myg
        
        resetScrollBars();
        repaint();
    }


   /*********************************************************************
    * Method to notify the tree that a node has been removed from the tree
    * model and to repaint the tree to reflect the changes..if necessary
    * @param node the node that has been removed from the tree model
    *********************************************************************/ 
    public void nodeRemoved(Node node){
    	if (indexOf(node) != -1)
            remove(node);
    }


   /*********************************************************************
    * NOT IMPLEMENTED !!!!!!!!!!!!!!!!!!!!!!!!!!!
    * Method to notify the tree that a node has been added to the tree model
    * and to repaint the tree to reflect the changes..if necessary
    * @param parent the parent node of the new added node
    * @param child the new ly added node
    * @param index the index of the new node
    *********************************************************************/ 
    public boolean nodeInserted(Node parent, Node child, int index){
    	int pos = indexOf(parent);
    	if (pos < 0) return false;                    // didn't find parent node
    	if (expands.items[pos] == 0)  return false;   // node is not expanded..so we don't have to paint the enode
    	
    	int lvl   = parent.getLevel() + 1;
    	int count = 0;
    	for (int i = pos+1; i < items.size(); i++){
    	    if (lvl == levels.items[i]){
    	        if (count == index){
    	        	insert(i, child, lvl, 0);
    	            break;
    	        }	
    	        count++;
    	    }	
    	    else if (lvl > levels.items[i] || i == items.size()-1){
   	        	insert(i+1, child, lvl, 0);
    	        break;
    	    }
    	}
    	resetScrollBars();
    	repaint();
    	return true;
    }
    

   /*********************************************************************
    * Method to notify the tree that a node in the tree model has been
    * modified (currently - only changing the user object)
    * @param node the node that has been modified
    *********************************************************************/ 
    public void nodeModified(Node node){
    	if (indexOf(node) != -1){
        	resetScrollBars();
        	repaint();
        }
    }
    

   /*********************************************************************
    *********************************************************************/ 
    public void event_PEN_UP(PenEvent pe){
        // Post the event
        int sel = ((pe.y-(simpleBorder?3:4))/fmH) + offset; // guich@200b4_2: corrected line selection
        if (contains(x+pe.x,y+pe.y) && pe.x < btnX && sel < itemCount){
            postEvent(new ControlEvent(ControlEvent.PRESSED, this));
            
            Node node   = (Node) items.items[sel];
            int level   = levels.items[sel];
            int xstart  = 3 - hsOffset + (w1 + hline + gap + w2/2 - w1/2 ) * (level-1);
            int xend    = xstart + w1+hline+gap+w2;

            if (node.isLeaf(allowsChildren)){
                xstart += w1+hline+gap+1;
                if (pe.x >= xstart && pe.x <= xend){           
                    node.setVisited(true);
                    repaint();
                }
            }
            else if (pe.x >= xstart && pe.x <= xend){             
                // call expand and collapse or change the leaf icon on when clicked on the icon or plus sign
                if (expands.items[sel] == 0)  expand(node);
                else                          collapse(node);
            }
            else if (pe.x >= (xend+5) && pe.x < ((xend+5) + fm.getTextWidth(node.toString()))){
                if (expands.items[sel] == 0)  expand(node);
                else                          collapse(node);
            }
        }            	
    }
    
    
   /*********************************************************************
    *********************************************************************/ 
    public void event_PEN_DRAG(PenEvent pe){ event_PEN_DOWN(pe); }
    

   /*********************************************************************
    *********************************************************************/ 
    public void event_PEN_DOWN(PenEvent pe){
        if (pe.x < btnX && contains(this.x+pe.x,this.y+pe.y)){ // && ((pe.y < fmH*drawItems) && (pe.y<fmH*itemCount-1)))
            int sel = ((pe.y- (simpleBorder?3:4)) / fmH) + offset; // guich@200b4: corrected line selection
            if (sel != selectedIndex && sel < itemCount){
                if (selectedIndex >= 0) 
                    drawCursor(myg,selectedIndex,false);
                selectedIndex = sel;
                drawCursor(myg,selectedIndex,true);
            }
        }
    }
    
    
  /*********************************************************************
   *********************************************************************/ 
    public void event_PRESSED(Event event){
        if (event.target == vbar){
            int newOffset = vbar.getValue();
            if (newOffset != offset){ // guich@200final_3: avoid unneeded repaints
                offset = newOffset;
                repaint();
            }
        }
        else if (event.target == hbar){
            int hsValue = hbar.getValue();
            if (hsValue != hsOffset && hsValue > -1){
                hsOffset = hsValue;
                repaint();
            }    	
        }
    }


  /*********************************************************************
   *********************************************************************/ 
   public void event_KEP_PRESS(Event event){
       int key = ((KeyEvent)event).key;
       if (key == IKeys.PAGE_UP ||
           key == IKeys.PAGE_DOWN || 
           key == IKeys.UP || 
           key == IKeys.DOWN || 
           key == IKeys.JOG_UP || 
           key == IKeys.JOG_DOWN){ // guich@220_19 - guich@330_45
             vbar.onEvent(event);
        }
        else if (key == IKeys.LEFT || key == IKeys.RIGHT)
             hbar.onEvent(event);
        else 
             find(Convert.toUpperCase((char)key));
    }



   /*********************************************************************
    *********************************************************************/ 
    public void onEvent(Event event){
        PenEvent pe;
        switch (event.type){
            case ControlEvent.WINDOW_MOVED:
                if (myg != null) 
                   myg.free();
                myg = createGraphics();
                break;
            
            case ControlEvent.PRESSED:
                event_PRESSED(event);
                break;
            
            case PenEvent.PEN_DRAG:
            case PenEvent.PEN_DOWN:
                if (event.target != this) break;
                event_PEN_DOWN((PenEvent)event);
                break;
            
            case KeyEvent.KEY_PRESS:
                event_KEP_PRESS(event);
                break;
            
            case PenEvent.PEN_UP:
                if (event.target != this)  break;
                event_PEN_UP((PenEvent)event);
                break;
        } // end switch
    }




   /*********************************************************************
    *********************************************************************/ 
    public void onPaint(Graphics g){
        if (myg == null) myg = createGraphics();
      
        // Draw background and borders
        g.setBackColor(bgColor0);
        g.fillRect(0,0,btnX,height);
        g.setForeColor(foreColor);

        if (simpleBorder && Settings.uiStyle == Settings.WinCE)
            g.drawRect(0,0,width,height);
        else
            g.draw3dRect(0,0,width,height,(Settings.uiStyle == Settings.PalmOS)?Graphics.R3D_SHADED:Graphics.R3D_CHECK,false,false,fourColors);

        
        // draw scrollbar border (why is it disappear in the first place? or is there a border for the scrollbar class??)
        g.drawRect(btnX-1, 0, vbar.getPreferredWidth()+1, height);
        if (hbar.isVisible())
            g.drawRect(0, height-hbar.getPreferredHeight()-1,  width-vbar.getPreferredWidth()+1, hbar.getPreferredHeight());


        int dx = 3;
        int dy = 3;
        if (Settings.uiStyle == Settings.PalmOS) dy--;
        if (simpleBorder) {dx--; dy--;}

        g.setForeColor(fColor);
        g.setClip(dx-1,dy-1,btnX-dx,fmH * visibleItems+1);
        int greatestVisibleItemIndex = Math.min(itemCount, visibleItems+offset); // code corrected by Bjoem Knafla
        for (int i = offset; i < greatestVisibleItemIndex; ++i, dy += fmH){
            drawNode(g,i,dx-hsOffset,dy); // guich@200b4: let the user extend ListBox and draw itself the items
        }
        if (selectedIndex >= 0) drawCursor(g,selectedIndex,true);
    }



   /*********************************************************************
    * Method to draw the icons and node text
    *********************************************************************/ 
    protected void drawNode(Graphics g, int index, int dx, int dy){
   	    Node   node     = (Node) items.items[index];
   	    int    level    = levels.items[index] - 1;
   	    
   	    dy += 2;
   	    drawConnector(g,index, dx,dy, node);  // draw the line that connect the nodes
   	    
        boolean expand = (expands.items[index] == 1);
   	    int     x = dx + (w1 + hline + gap + w2/2 - w1/2 ) * level;
        int     y = dy;   	    

        // draw plus minus icon
        y = dy + fmH/2;
        if (node.isLeaf(allowsChildren))    g.drawLine(x+w1/2, y, x + w1, y);
        else if (node.getChildCount() == 0) g.drawLine(x+w1/2, y, x + w1, y);
        else if (expand)                    g.drawImage(imgMinus, x, y - h1/2);   
        else                                g.drawImage(imgPlus,  x, y - h1/2);    
             
        // draw horizontal line
        x += w1;
        g.drawLine(x, y, x+hline, y);
        
        // draw folder icon (remember the gap needed)
        x += hline + gap;

        y = dy + fmH/2 - h2/2;  
        if (node.isLeaf(allowsChildren) && node.isVisited())  g.drawImage(imgVisit, x, y);
        else if (node.isLeaf(allowsChildren))                 g.drawImage(imgFile,  x, y);
        else if (expand)                        g.drawImage(imgOpen,  x, y); 
        else                                    g.drawImage(imgClose, x, y); 
   	   
        dy--;
   	    x += w2 + gap; 
   	    y = dy;
   	    /* Handle a potential list of user icons. */
		int iconw;
		if (Settings.screenWidth <= 160) {
			iconw=0;
		} else {
			iconw=gap;
		}

		/* Figure out how many user icons there are. */
		int n=0;
		if (node.userIcons!=null) {
			n=node.userIcons.size();
		}

		/* Loop through and display any icons found. */
		for (int i = 0; i <n; i++) {
			g.drawImage(((NodeIcon)(node.userIcons.items[i])).getImage(),  x+iconw, y); 
			iconw+=((NodeIcon)(node.userIcons.items[i])).getWidth();
		}
		if (Settings.screenWidth>160)
			iconw++;
		node.wicons=iconw;
		g.drawText(node.toString(), x+iconw,y); // guich@402_31: don't test for index out of bounds. this will be catched in the caller   	   
   } 


   /*********************************************************************
    * Method to draw the (line connector) angled line.
    *********************************************************************/ 
    protected void drawConnector(Graphics g, int index, int dx, int dy, Node node){
        if (node == null ) return;

   	    int    level    = node.getLevel() - 1; //levels.items[index] - 1;
        Node   prev     = node.getPreviousSibling();
        Node   next     = node.getNextSibling();

        // calculate the x-start position
        int    x = dx;
        if (level == 0)      x += w1/2;
        else if (level == 1) x += w1 + hline + gap + w2/2;
        else                 x += w1 + hline + gap + w2/2 + (w1/2 + hline + gap + w2/2 + 1) * (level-1);

        // calculate the y-start and y-end position         
        int ystart;
        int yend;
                
        // handles the last level 1 node
        if (level == 0 && next == null){
        	if (prev != null && items.items[index] == node){
        	    ystart = dy - (fmH-h1)/2;
        	    yend   = dy + (fmH-h1)/2;
        	    g.drawLine(x, ystart, x, yend);
        	}
        }

        // draw vertical connector lines for leaf node
        if (node.isLeaf(allowsChildren) || node.getChildCount() == 0){
        	ystart = dy - (fmH-h2)/2;
        	yend   = dy + (fmH/2);
        	g.drawLine(x, ystart, x, yend);
        	
        	if (next != null){
        	    ystart = yend;
        	    yend   += (fmH/2);	
            	g.drawLine(x, ystart, x, yend); 
        	}
        }
        // draw vertical connector lines for folder node
        else {
        	if (next == null && node == items.items[index]){
        	        ystart = dy - (fmH-h1)/2;
        	        yend   = dy + (fmH-h1)/2;
            	    g.drawLine(x, ystart, x, yend); // draw from "+" to end of line
            }
            
            if (next != null){
                ystart = dy - (fmH-h1)/2;
                yend   = dy + fmH; 
                g.drawLine(x, ystart , x, yend);
            }
        }
        drawConnector(g,index,dx,dy, node.getParent());
    }


   /*********************************************************************
    * Method to draw the highlight box when user select a listbox's item.
    *********************************************************************/ 
    protected void drawCursor(Graphics g, int sel, boolean on){
        if (offset <= sel && sel < visibleItems+offset && sel < itemCount){
            int level = levels.items[sel];
      	    int pw = imgPlus.getWidth();
      	 
      	    int dx = 3 - hsOffset;
      	    if (level == 1)  dx += (w1+hline+gap+w2) * level;
            else             dx += w1+hline+gap+w2 + (w1+hline+gap+w2/2-w1/2) * (level-1);

            if (Settings.screenWidth > 160) dx += 3;
         
            int dy = 4;
            if (Settings.uiStyle == Settings.PalmOS) dy--;
            if (simpleBorder) {dx--; dy--;}
            
            dy += (sel-offset) * fmH;
            g.setClip(dx-1,dy-1,btnX-dx,Math.min(fmH * visibleItems, this.height-dy)); // guich@200b4_83: fixed selection overflowing paint area
            g.setForeColor(on?bgColor0:bgColor1);
            g.setBackColor(on?bgColor1:bgColor0);
            g.eraseRect(dx+1,dy,fm.getTextWidth(((Node) items.items[sel]).toString()) +
            	+((Node) items.items[sel]).wicons +2,fmH+fm.descent-1); // only select the Object - guich@200b4_130
        }
    }




   /*********************************************************************
    * Method to set the cursor color for this Tree. The default is 
    * equal to the background slightly darker. Make sure you tested it 
    * in 2,4 and 8bpp devices. 
    *********************************************************************/ 
    public void setCursorColor(Color color){
        this.cursorColor = color;
        onColorsChanged(true);
    }




   /*********************************************************************
    * Method to set the listbox's border to be not 3d if flag is true. 
    *********************************************************************/ 
    public void setSimpleBorder(boolean simpleBorder){ // guich@200b4_93
        this.simpleBorder = simpleBorder;
    }


   /*********************************************************************
    * Method to reload the tree.
    * Use this method when the tree model has made a drastic change.
    *********************************************************************/ 
    public void reload(){
    	clear();
        initTree(model.getRoot());    	
        repaint();
    }


   /*********************************************************************
    * Method to clear the tree and release the tree model references.
    *********************************************************************/ 
    public void unload(){
        clear();
        model = null;	
    }


   /*********************************************************************
    * Method to display a message to the system console.
    *********************************************************************/ 
    public void echo(Object msg){ Vm.debug(msg.toString()); }
  
    
   /*********************************************************************
    * Debug: convient method to diplay the contents of the items vector.
    *********************************************************************/ 
    public void display(){
    	for (int i = 0; i < items.size(); i++){
    		Node node = (Node) items.items[i];
    	    //Vm.debug(node.toString() + " [" + levels.items[i] + "," + expands.items[i] + "]");
    	}
    }
  
}
