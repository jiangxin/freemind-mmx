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
/*$Id: ControllerAdapter.java,v 1.40 2004-01-25 16:41:12 christianfoltin Exp $*/

package freemind.modes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;




import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.JTextComponent;

import freemind.main.FreeMind;
import freemind.controller.Controller;
import freemind.main.FreeMindMain;
import freemind.main.Tools;
import freemind.main.ExampleFileFilter;
import freemind.main.XMLParseException;
import freemind.view.MapModule;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeView;


/**
 * Derive from this class to implement the Controller for your mode. Overload the methods
 * you need for your data model, or use the defaults. There are some default Actions you may want
 * to use for easy editing of your model. Take MindMapController as a sample.
 */
public abstract class ControllerAdapter implements ModeController {

    Mode mode;
    private int noOfMaps = 0; //The number of currently open maps
    private Clipboard clipboard;
    private int status;

    public Action copy = null;
    public Action copySingle = null;
    public Action cut = null;
    public Action paste = null;

    static final Color selectionColor = new Color(200,220,200);

    public ControllerAdapter() {
    }

    public ControllerAdapter(Mode mode) {
        this.mode = mode;

        cut = new CutAction(this);
        paste = new PasteAction(this);
        copy = new CopyAction(this);
        copySingle = new CopySingleAction(this);

        DropTarget dropTarget = new DropTarget(getFrame().getViewport(),
                                               new FileOpener());

        clipboard = getFrame().getViewport().getToolkit().getSystemSelection();

        // SystemSelection is a strange clipboard used for instance on
        // Linux. To get data into this clipboard user just selects the area
        // without pressing Ctrl+C like on Windows.
        
        if (clipboard == null) {
           clipboard = getFrame().getViewport().getToolkit().getSystemClipboard(); }

    }

    //
    // Methods that should be overloaded
    //

    protected abstract MindMapNode newNode();

    /**
     * You _must_ implement this if you use one of the following actions:
     * OpenAction, NewMapAction.
     */
    public MapAdapter newModel() {
        throw new java.lang.UnsupportedOperationException();
    }

    /**
     * You may want to implement this...
     * It returns the FileFilter that is used by the open() and save()
     * JFileChoosers.
     */
    protected FileFilter getFileFilter() {
        return null;
    }

    public void nodeChanged(MindMapNode n) {
    }

    public void anotherNodeSelected(MindMapNode n) {
    }

    public void doubleClick(MouseEvent e) {
        /* perform action only if one selected node.*/
        if(getSelecteds().size() != 1)
            return;
        MindMapNode node = ((NodeView)(e.getComponent())).getModel();
        // edit the node only if the node is a leaf (fc 0.7.1)
        if (node.hasChildren()) {
            // the emulate the plain click. 
            plainClick(e);
            return;
        }
        if (!e.isAltDown() 
            && !e.isControlDown() 
            && !e.isShiftDown() 
            && !e.isPopupTrigger()
            && e.getButton() == MouseEvent.BUTTON1
            && (node.getLink() == null)) {
            edit(null, false, false);
        }
    }

    public void plainClick(MouseEvent e) {
        /* perform action only if one selected node.*/
        if(getSelecteds().size() != 1)
            return;
        MindMapNode node = ((NodeView)(e.getComponent())).getModel();
        if (getView().getSelected().followLink(e.getX())) {
            loadURL(); }
        else {
            if (!node.hasChildren()) {
                // the emulate the plain click. 
                doubleClick(e);
                return;
            }
            toggleFolded(); 
        }
    }

    //
    // Map Management
    //

    /**
     * Get text identification of the map
     */

    protected String getText(String textId) {
       return getController().getResourceString(textId); }

    protected boolean binOptionIsTrue(String option) {
       return getFrame().getProperty(option).equals("true");}

    public void newMap() {
        getController().getMapModuleManager().newMapModule(newModel());
        mapOpened(true);
    }

    protected void newMap(MindMap map) {
        getController().getMapModuleManager().newMapModule(map);
        mapOpened(true);
    }

    /**
     * You may decide to overload this or take the default
     * and implement the functionality in your MapModel (implements MindMap)
     */
    public void load (File file) throws FileNotFoundException, IOException, XMLParseException {
        MapAdapter model = newModel();
        model.load(file);
        getController().getMapModuleManager().newMapModule(model);
        mapOpened(true);
    }

    public boolean save() {
        if (getModel().isSaved()) return true;
        if (getModel().getFile() == null || getModel().isReadOnly()) {
           return saveAs(); }
        else {
           return save(getModel().getFile()); }}

    /** fc, 24.1.2004: having two methods getSelecteds with different return values (linkedlists of models resp. views) is asking for trouble. @see MapView */
    protected LinkedList getSelecteds() {
	LinkedList selecteds = new LinkedList();
	ListIterator it = getView().getSelecteds().listIterator();
	if (it != null) {
	    while(it.hasNext()) {
		NodeView selected = (NodeView)it.next();
		selecteds.add( selected.getModel() );
	    }
	}
	return selecteds;
    }

    

    /**
     * Return false is the action was cancelled, e.g. when
     * it has to lead to saving as.
     */
    public boolean save(File file) {
       return getModel().save(file); }      

    /** @return returns the new JMenuItem.*/
    protected JMenuItem add(JMenu menu, Action action, String keystroke) { 
       JMenuItem item = menu.add(action);
       item.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty(keystroke)));
       return item;
    }

    protected void add(JMenu menu, Action action) {
       menu.add(action); }

    //
    // Dialogs with user
    //

    public void open() {
        JFileChooser chooser = null;
        if ((getMap() != null) && (getMap().getFile() != null) && (getMap().getFile().getParentFile() != null)) {
            chooser = new JFileChooser(getMap().getFile().getParentFile());
        } else {
            chooser = new JFileChooser();
        }
        //chooser.setLocale(currentLocale);
        if (getFileFilter() != null) {
            chooser.addChoosableFileFilter(getFileFilter());
        }
        int returnVal = chooser.showOpenDialog(getView());
        if (returnVal==JFileChooser.APPROVE_OPTION) {
            try {
                load(chooser.getSelectedFile());
            } catch (Exception ex) {
               handleLoadingException (ex); } {
            }
        }
        getController().setTitle();
    }

    public void handleLoadingException (Exception ex) {
       String exceptionType = ex.getClass().getName();
       if (exceptionType.equals("freemind.main.XMLParseException")) {
          int showDetail = JOptionPane.showConfirmDialog
             (getView(), getText("map_corrupted"),"FreeMind",JOptionPane.YES_NO_OPTION,JOptionPane.ERROR_MESSAGE);
          if (showDetail==JOptionPane.YES_OPTION) {
             getController().errorMessage(ex); }}
       else if (exceptionType.equals("java.io.FileNotFoundException")) {
          getController().errorMessage(ex.getMessage()); }
       else {
          getController().errorMessage(ex); }
    }

    /**
     * Save as; return false is the action was cancelled
     */
    public boolean saveAs() {
        JFileChooser chooser = null;
        if ((getMap().getFile() != null) && (getMap().getFile().getParentFile() != null)) {
            chooser = new JFileChooser(getMap().getFile().getParentFile()); }
        else {
           chooser = new JFileChooser();
           chooser.setSelectedFile(new File(((MindMapNode)getMap().getRoot()).toString()+".mm"));
        }
        //chooser.setLocale(currentLocale);
        if (getFileFilter() != null) {
            chooser.addChoosableFileFilter(getFileFilter()); }
        
        chooser.setDialogTitle(getText("save_as"));
        int returnVal = chooser.showSaveDialog(getView());
        if (returnVal != JFileChooser.APPROVE_OPTION) {// not ok pressed
        	return false; }
        
        // |= Pressed O.K.    
        File f = chooser.getSelectedFile();
        //Force the extension to be .mm
        String ext = Tools.getExtension(f.getName());
        if(!ext.equals("mm")) {
           f = new File(f.getParent(),f.getName()+".mm"); }        
                
        if (f.exists()) { // If file exists, ask before overwriting.
			int overwriteMap = JOptionPane.showConfirmDialog
			   (getView(), getText("map_already_exists"), "FreeMind", JOptionPane.YES_NO_OPTION );
			if (overwriteMap != JOptionPane.YES_OPTION) {
			   return false; }}

		try { // We have to lock the file of the map even when it does not exist yet
		   String lockingUser = getModel().tryToLock(f);
		   if (lockingUser != null) {          
		      getFrame().getController().informationMessage(
			    Tools.expandPlaceholders(getText("map_locked_by_save_as"), f.getName(), lockingUser));
		      return false; }}
		catch (Exception e){ // Throwed by tryToLock
		  getFrame().getController().informationMessage(
		    Tools.expandPlaceholders(getText("locking_failed_by_save_as"), f.getName()));	 
		  return false; } 	        	                              
              
        save(f);
        //Update the name of the map
        getController().getMapModuleManager().updateMapModuleName();        
        return true;
    }
    /**
     * Return false if user has canceled. 
     */
    public boolean close() {
        String[] options = {getText("yes"),
                            getText("no"),
                            getText("cancel")};
        if (!getModel().isSaved()) {
            String text = getText("save_unsaved")+"\n"+getMapModule().toString();
            String title = getText("save");
            int returnVal = JOptionPane.showOptionDialog(getFrame().getContentPane(),text,title,JOptionPane.YES_NO_CANCEL_OPTION,
                                                         JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
            if (returnVal==JOptionPane.YES_OPTION) {
               boolean savingNotCancelled = save();
               if (!savingNotCancelled) {
               	  return false; }}
			else if (returnVal==JOptionPane.CANCEL_OPTION) {
				return false; }}
                
        getModel().destroy();
        mapOpened(false);
        return true; }
    

    /**
     * Call this method if you have opened a map for this mode with true,
     * and if you have closed a map of this mode with false. It updates the Actions
     * that are dependent on whether there is a map or not.
     * --> What to do if either newMap or load or close are overwritten by a concrete
     * implementation? uups.
     */
     public void mapOpened(boolean open) {
        if (open) {
           if (noOfMaps == 0) {
              //opened the first map
              setAllActions(true);
              if (cut!=null) cut.setEnabled(true);
              if (copy!=null) copy.setEnabled(true);
              if (copySingle!=null) copySingle.setEnabled(true);
              if (paste!=null) paste.setEnabled(true);
           }
           if (getFrame().getView()!=null) {
              DropTarget dropTarget = new DropTarget
                 (getFrame().getView(), new FileOpener() );
           }
           noOfMaps++;
        } else {
           noOfMaps--;
           if (noOfMaps == 0) {
              //closed the last map
              setAllActions(false);
              if (cut!=null) cut.setEnabled(false);
              if (copy!=null) copy.setEnabled(false);
              if (copySingle!=null) copySingle.setEnabled(true);
              if (paste!=null) paste.setEnabled(false);
           }
        }
    }

    /**
     * Overwrite this to set all of your actions which are
     * dependent on whether there is a map or not.
     */
    protected void setAllActions(boolean enabled) {
    }

    //
    // Node editing
    //

    private JPopupMenu popupmenu;

    // listener, that blocks the controler if the menu is active (PN)
    private class ControllerPopupMenuListener implements PopupMenuListener  {
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        setBlocked(true);         // block controller
      }
      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        setBlocked(false);        // unblock controller
      }
      public void popupMenuCanceled(PopupMenuEvent e) {
        setBlocked(false);        // unblock controller
      }

    }
    private final ControllerPopupMenuListener popupListenerSingleton
        = new ControllerPopupMenuListener();
    
    public void showPopupMenu(MouseEvent e) {
      if (e.isPopupTrigger()) {
        JPopupMenu popupmenu = getPopupMenu();
        if (popupmenu != null) {
          // adding listener could be optimized but without much profit...
          popupmenu.addPopupMenuListener( this.popupListenerSingleton );
          popupmenu.show(e.getComponent(),e.getX(),e.getY());
          e.consume();
        }
      }
    }

    /** Default implementation: no context menu.*/
    public JPopupMenu getPopupForModel(java.lang.Object obj) {
        return null;
    }
        


    private static final int SCROLL_SKIPS = 8;
    private static final int SCROLL_SKIP = 10;
    private static final int HORIZONTAL_SCROLL_MASK 
       = InputEvent.SHIFT_MASK | InputEvent.BUTTON1_MASK 
         | InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK;
    private static final int ZOOM_MASK 
       = InputEvent.CTRL_MASK;
      // |=   oldX >=0 iff we are in the drag

    public void mouseWheelMoved(MouseWheelEvent e) {
       if (isBlocked()) {
         return; // block the scroll during edit (PN)
       }
        
       if ((e.getModifiers() & ZOOM_MASK) != 0) {
           // fc, 18.11.2003: when control pressed, then the zoom is changed.
           float newZoomFactor = 1f + Math.abs((float) e.getWheelRotation())/10f;
           if(e.getWheelRotation() < 0)
               newZoomFactor = 1 / newZoomFactor;
           float newZoom = ((MapView)e.getComponent()).getZoom() * newZoomFactor;
           // round the value due to possible rounding problems.
           newZoom =  (float) Math.rint(newZoom*1000f)/1000f;
           getController().setZoom(newZoom);
           // end zoomchange
       } else if ((e.getModifiers() & HORIZONTAL_SCROLL_MASK) != 0) {
          for (int i=0; i < SCROLL_SKIPS; i++) {
             ((MapView)e.getComponent()).scrollBy(
                 SCROLL_SKIP * e.getWheelRotation(), 0); }}
       else {
          for (int i=0; i < SCROLL_SKIPS; i++) {
             ((MapView)e.getComponent()).scrollBy(0, 
                 SCROLL_SKIP * e.getWheelRotation()); }}
    }

    // edit begins with home/end or typing (PN 6.2)
    public void edit(KeyEvent e, boolean addNew, boolean editLong) {
      if (getView().getSelected() != null) {
        if (e == null || !addNew) {
          edit(getView().getSelected(),getView().getSelected(), e, false, false, editLong);
        }
        else if (!isBlocked()) {
          addNew(getView().getSelected(), NEW_SIBLING_BEHIND, e);
        }
        if (e != null) {
          e.consume();
        }
      }
    }

   private void changeComponentHeight(JComponent component, int difference, int minimum) {
      Dimension preferredSize = component.getPreferredSize();
      System.out.println("pf:"+preferredSize);
      if (preferredSize.getHeight() + difference >= minimum) {
         System.out.println("pf:"+preferredSize);
         component.setPreferredSize(new Dimension((int)preferredSize.getWidth(),
                                                  (int)preferredSize.getHeight() + difference)); }}

    /** Private variable to hold the last value of the "Enter confirms" state.*/
    private static Tools.BooleanHolder booleanHolderForConfirmState;

    private void editLong(final NodeView node,
                            final String text,
                            final KeyEvent firstEvent) {

        final int BUTTON_OK     = 0;
        final int BUTTON_CANCEL = 1;
        final int BUTTON_SPLIT  = 2;

        final JDialog dialog = new JDialog((JFrame)getFrame(), getText("edit_long_node"), /*modal=*/true);

        final JTextArea textArea = new JTextArea(text);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true); // wrap around words rather than characters
        if (firstEvent != null) {
          switch (firstEvent.getKeyCode()) {
            case KeyEvent.VK_HOME:
              textArea.setCaretPosition(0);
              break;

            default:
              textArea.setCaretPosition(text.length());
              break;
          }
        }
        else {
          textArea.setCaretPosition(text.length());
        }


        final JScrollPane editorScrollPane = new JScrollPane(textArea);
        editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        //int preferredHeight = new Integer(getFrame().getProperty("el__default_window_height")).intValue();
        int preferredHeight = node.getHeight();
        preferredHeight =
           Math.max (preferredHeight, Integer.parseInt(getFrame().getProperty("el__min_default_window_height")));
        preferredHeight =
           Math.min (preferredHeight, Integer.parseInt(getFrame().getProperty("el__max_default_window_height")));

        int preferredWidth = node.getWidth();
        preferredWidth =
           Math.max (preferredWidth, Integer.parseInt(getFrame().getProperty("el__min_default_window_width")));
        preferredWidth =
           Math.min (preferredWidth, Integer.parseInt(getFrame().getProperty("el__max_default_window_width")));
                           
        editorScrollPane.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
        //textArea.setPreferredSize(new Dimension(500, 160));

        final JPanel panel = new JPanel();

        //String performedAction;
        final Tools.IntHolder eventSource = new Tools.IntHolder();
        final JButton okButton = new JButton("OK");
        final JButton cancelButton = new JButton(getText("cancel"));
        final JButton splitButton = new JButton(getText("split"));
        final JCheckBox enterConfirms =
           new JCheckBox(getText("enter_confirms"), binOptionIsTrue("el__enter_confirms_by_default"));

        if(booleanHolderForConfirmState == null) {
            booleanHolderForConfirmState = new Tools.BooleanHolder();
            booleanHolderForConfirmState.setValue(enterConfirms.isSelected());
        } else {
            enterConfirms.setSelected(booleanHolderForConfirmState.getValue());
        }            

        okButton.setMnemonic(KeyEvent.VK_O);
        enterConfirms.setMnemonic(KeyEvent.VK_E);
        splitButton.setMnemonic(KeyEvent.VK_S);
        cancelButton.setMnemonic(KeyEvent.VK_C);

        okButton.addActionListener (new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                 eventSource.setValue(BUTTON_OK);
                 dialog.dispose(); }});

        cancelButton.addActionListener (new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                 eventSource.setValue(BUTTON_CANCEL);
                 dialog.dispose(); }});

        splitButton.addActionListener (new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                 eventSource.setValue(BUTTON_SPLIT);
                 dialog.dispose(); }});

        enterConfirms.addActionListener (new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                 textArea.requestFocus(); 
                 booleanHolderForConfirmState.setValue(enterConfirms.isSelected());
              }});

        // On Enter act as if OK button was pressed

        textArea.addKeyListener(new KeyListener() {
              public void keyPressed(KeyEvent e) {
                 // escape key in long text editor (PN)
                 if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                   e.consume();
                   eventSource.setValue(BUTTON_CANCEL);
                   dialog.dispose(); 
                 }
                 else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (enterConfirms.isSelected() == ((e.getModifiers() & KeyEvent.CTRL_MASK) == 0)) {
                       e.consume();
                       eventSource.setValue(BUTTON_OK);
                       dialog.dispose(); }
                    else if (enterConfirms.isSelected() && (e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
                       e.consume();
                       textArea.insert("\n",textArea.getCaretPosition()); }}}

                 /*
                 // Daniel: I tried to make editor resizable. It worked somehow, but not
                 // quite. When I increased the size and then decreased again to the original
                 // size, it stopped working. The main idea here is to change the preferred
                 // size. I also tried to change the size but it did not do anything sensible.
                 //
                 // One possibility would be to disable decreasing the size.
                 //
                 // Another thing which was far from nice was that it flickered.
                 //
                 // If someone wants to find a solution, let it be.

                 else if (e.getKeyCode() == KeyEvent.VK_DOWN &&
                          (e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {

                    changeComponentHeight(editorScrollPane, 60, 180);
                    dialog.doLayout();
                    dialog.pack();
                    e.consume();
                 }
                 else if (e.getKeyCode() == KeyEvent.VK_UP &&
                          (e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
                    changeComponentHeight(editorScrollPane, -60, 180);
                    dialog.doLayout();
                    dialog.pack();
                    e.consume(); }} */
              public void keyTyped(KeyEvent e) {}
              public void keyReleased(KeyEvent e) {}
           });

        textArea.addMouseListener(new MouseListener() {
              public void mouseClicked(MouseEvent e) {}
              public void mouseEntered( MouseEvent e ) {}
              public void mouseExited( MouseEvent e ) {}

              public void mousePressed( MouseEvent e ) {
                 conditionallyShowPopup(e); }
              
              public void mouseReleased( MouseEvent e ) {
                 conditionallyShowPopup(e); }

              private void conditionallyShowPopup(MouseEvent e) {
                 if (e.isPopupTrigger()) {
                    JPopupMenu popupMenu = new EditPopupMenu(textArea);
                    popupMenu.show(e.getComponent(),e.getX(),e.getY());
                    e.consume(); }}
           });


        textArea.setFont(node.getFont());
        textArea.setForeground(node.getForeground());

        //panel.setPreferredSize(new Dimension(500, 160));
        //editorScrollPane.setPreferredSize(new Dimension(500, 160));

        JPanel buttonPane = new JPanel();
        buttonPane.add(enterConfirms);
        buttonPane.add(okButton);
        buttonPane.add(cancelButton);
        buttonPane.add(splitButton);
        buttonPane.setMaximumSize(new Dimension(1000, 20));

        if (getFrame().getProperty("el__buttons_position").equals("above")) {
           panel.add(buttonPane);
           panel.add(editorScrollPane); }
        else {
           panel.add(editorScrollPane);
           panel.add(buttonPane); }

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(panel);
        dialog.pack();  // calculate the size
        
        // set position
        getView().scrollNodeToVisible(node, 0);
        Point frameScreenLocation = getFrame().getLayeredPane().getLocationOnScreen();
        double posX = node.getLocationOnScreen().getX() - frameScreenLocation.getX();
        double posY = node.getLocationOnScreen().getY() - frameScreenLocation.getY()
           + (binOptionIsTrue("el__position_window_below_node") ? node.getHeight() : 0);
        if (posX + dialog.getWidth() > getFrame().getLayeredPane().getWidth()) {
          posX = getFrame().getLayeredPane().getWidth() - dialog.getWidth();
        }
        if (posY + dialog.getHeight() > getFrame().getLayeredPane().getHeight()) {
          posY = getFrame().getLayeredPane().getHeight() - dialog.getHeight();
        }
        posX = ((posX < 0) ? 0 : posX) + frameScreenLocation.getX();
        posY = ((posY < 0) ? 0 : posY) + frameScreenLocation.getY();
        dialog.setLocation(new Double(posX).intValue(), new Double(posY).intValue());


        textArea.requestFocusInWindow();  // make the text area focused when the dialog comes
        dialog.show();
        //dialog.setVisible(true);

        if (eventSource.getValue() == BUTTON_OK) {
           getModel().changeNode(node.getModel(), textArea.getText()); }
        if (eventSource.getValue() == BUTTON_SPLIT) {
           //getModel().changeNode(node.getModel(), textArea.getText());
           getModel().splitNode(node.getModel(),
                                textArea.getCaretPosition(),
                                textArea.getText()); 
           getController().obtainFocusForSelected(); // focus fix
        }

    }

    // this enables from outside close the edit mode
    private FocusListener textFieldListener = null;
    
    private void closeEdit() {
      if (this.textFieldListener != null) {
        textFieldListener.focusLost(null); // hack to close the edit
      }
    }
    
    // status, currently: default, blocked  (PN)
    // (blocked to protect against particular events e.g. in edit mode)
    private boolean isBlocked = false;

    public boolean isBlocked() {
      return this.isBlocked;
    }
    private void setBlocked(boolean isBlocked) {
      this.isBlocked = isBlocked;
    }
    
    private void edit(final NodeView node, 
                        final NodeView prevSelected,   // when new->esc: node be selected
                        final KeyEvent firstEvent,
                        final boolean isNewNode,      // when new->esc: cut the node
                        final boolean parentFolded,   // when new->esc: fold prevSelected
                        final boolean editLong) {
		if (node == null){
			return;	}

        closeEdit();
        setBlocked(true); // locally "modal" stated

        String text = node.getModel().toString();
        if (node.getIsLong() || editLong) {
           editLong(node, text, firstEvent);
           setBlocked(false);
           return; 
        }

        //if (isNewNode) {
        //  if (firstEvent instanceof KeyEvent
        //      && ((KeyEvent)firstEvent).getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
        //  }
        //  //else if (text.length() == 0) {
        //    // new node text if the user did not press a key and the text would be empty
        //    // text = getText("new_node");
        //  //}
        //}
        
        final JTextField textField = (text.length() < 8)
           ? new JTextField(text,8)     //Make fields for short texts editable
           : new JTextField(text);

        // Set textFields's properties

        /* fc, 12.10.2003: the following method is not correct. Even more with the zoom factors!*/
        int linkIconWidth = 16;
        int textFieldBorderWidth = 2;
        int cursorWidth = 1;
        int xOffset = -1 * textFieldBorderWidth + node.getLeftWidthOverhead() - 1;
        int yOffset = -1; // Optimized for Windows style; basically ad hoc
        int widthAddition = 2 * textFieldBorderWidth + cursorWidth - 2 * node.getLeftWidthOverhead() + 2;
        int heightAddition = 2;
        if (node.getModel().getLink() != null) {
           xOffset += linkIconWidth;
           widthAddition -= linkIconWidth; }
        if (node.getModel().getIcons().size() != 0) { // fc, 24.9.2003 full ok for the moment, that an icon has the same size as the link icon.
           xOffset += linkIconWidth * node.getModel().getIcons().size();
           widthAddition -= linkIconWidth; }
        /* fc, 12.10.2003: end buggy method*/

        // minimal width for input field of leaf or folded node (PN)
        final int MINIMAL_LEAF_WIDTH = 150;
        final int MINIMAL_WIDTH = 50;

        int xSize = node.getWidth() + widthAddition;
        int xExtraWidth = 0;
        if (MINIMAL_LEAF_WIDTH > xSize 
            && (node.getModel().isFolded() || !node.getModel().hasChildren())) {
          // leaf or folded node with small size
          xExtraWidth = MINIMAL_LEAF_WIDTH - xSize;
          xSize = MINIMAL_LEAF_WIDTH; // increase minimum size
          if (node.isLeft()) { // left leaf
            xExtraWidth = - xExtraWidth;
            textField.setHorizontalAlignment(JTextField.RIGHT);
          }
        }
        else if (MINIMAL_WIDTH > xSize) {
          // opened node with small size
          xExtraWidth = MINIMAL_WIDTH - xSize;
          xSize = MINIMAL_WIDTH; // increase minimum size
          if (node.isLeft()) { // left node
            xExtraWidth = - xExtraWidth;
            textField.setHorizontalAlignment(JTextField.RIGHT);
          }
        }

        textField.setSize(xSize, node.getHeight() + heightAddition);
        textField.setFont(node.getFont());
        textField.setForeground(node.getForeground());
        textField.setSelectedTextColor(node.getForeground());
        textField.setSelectionColor(selectionColor);
        // textField.selectAll(); // no selection on edit (PN)
        
        final int INIT   = 0;
        final int EDIT   = 1;
        final int CANCEL = 2;
        final Tools.IntHolder eventSource = new Tools.IntHolder();
        eventSource.setValue(INIT);

        // listener class
        class TextFieldListener implements KeyListener, 
                                             FocusListener, MouseListener {

          public void focusGained(FocusEvent e) {
            // the first time the edit field gains a focus
            // process the predefined first key (if any)

            if (eventSource.getValue() == INIT) {
              eventSource.setValue(EDIT);
              if (firstEvent instanceof KeyEvent) {
                KeyEvent firstKeyEvent = (KeyEvent)firstEvent;
                if (firstKeyEvent.getKeyChar() == KeyEvent.CHAR_UNDEFINED) {
                  // for the char_undefined the scenario with dispatching 
                  // doesn't work => hard code dispatching :-(
                  // // dispatch action key events as it came
                  // textField.dispatchEvent(firstKeyEvent);
                  
                  // dispatch 2 known events (+ special for insert:) (hardcoded)
                  switch (firstKeyEvent.getKeyCode()) {
                    case KeyEvent.VK_HOME:
                      textField.setCaretPosition(0);
                      break;
                    case KeyEvent.VK_END:
                      textField.setCaretPosition(textField.getText().length());
                      break;
                  }
                }
                else {
                  // or create new "key type" event for printable key
                  KeyEvent keyEv;
                  keyEv = new KeyEvent(
                      firstKeyEvent.getComponent(),
                      KeyEvent.KEY_TYPED, 
                      firstKeyEvent.getWhen(), 
                      firstKeyEvent.getModifiers(), 
                      KeyEvent.VK_UNDEFINED,
                      firstKeyEvent.getKeyChar(), 
                      KeyEvent.KEY_LOCATION_UNKNOWN );
                  textField.selectAll(); // to enable overwrite
                  textField.dispatchEvent(keyEv);
                }
              } // 1st key event defined
            } // first focus
          } // focus gained


          public void focusLost(FocusEvent e) {
            
            // %%% open problems:
            // - adding of a child to the rightmost node
            // - scrolling while in editing mode (it can behave just like other viewers)
            // - block selected events while in editing mode
        
            if (e == null) { // can be when called explicitly
              getModel().changeNode(node.getModel(), textField.getText());
              getFrame().getLayeredPane().remove(textField);
              getFrame().repaint(); //  getLayeredPane().repaint();
              textFieldListener = null;
              eventSource.setValue(CANCEL); // disallow real focus lost
            }
            else if (eventSource.getValue() != CANCEL) {
              // always confirm the text if not yet
              getModel().changeNode(node.getModel(), textField.getText());
              getFrame().getLayeredPane().remove(textField);
              getFrame().repaint(); //  getLayeredPane().repaint();
              setBlocked(false);
              textFieldListener = null;
            }
          }

          public void keyPressed(KeyEvent e) {

            if (e.isAltDown() || e.isControlDown()) {
                return;
            }

            boolean commit = true;

            switch (e.getKeyCode()) {
              case KeyEvent.VK_ESCAPE:
                commit = false;
              case KeyEvent.VK_ENTER:
                e.consume();

                eventSource.setValue(CANCEL); // do not process loose of focus
                if (commit) {
                  getModel().changeNode(node.getModel(), textField.getText());
                }
                else if (isNewNode) { // delete also the node and set focus to the parent
                  getView().selectAsTheOnlyOneSelected(node);
                  getModel().cut();
                  select(prevSelected); // include max level for navigation
                  if (parentFolded) {
                    getModel().setFolded(prevSelected.getModel(), true);
                  }
                }
                getFrame().getLayeredPane().remove(textField);
                getFrame().repaint(); //  getLayeredPane().repaint();
                setBlocked(false);
                textFieldListener = null;
                getController().obtainFocusForSelected(); // hack: to keep the focus
                break;

             case KeyEvent.VK_SPACE: 
               e.consume();
             }
          }
          public void keyTyped(KeyEvent e) { }
          public void keyReleased(KeyEvent e) { }

           public void mouseClicked(MouseEvent e) {}
           public void mouseEntered( MouseEvent e ) {}
           public void mouseExited( MouseEvent e ) {}

           public void mousePressed( MouseEvent e ) {
              conditionallyShowPopup(e); }
              
           public void mouseReleased( MouseEvent e ) {
              conditionallyShowPopup(e); }

           private void conditionallyShowPopup(MouseEvent e) {
              if (e.isPopupTrigger()) {
                 JPopupMenu popupMenu = new EditPopupMenu(textField);
                 popupMenu.show(e.getComponent(),e.getX(),e.getY());
                 e.consume(); }}

        }

        // create the listener
        final TextFieldListener textFieldListener = new TextFieldListener();
        
        // Add listeners
        this.textFieldListener = textFieldListener;
        textField.addFocusListener( textFieldListener );
        textField.addKeyListener( textFieldListener );
        textField.addMouseListener( textFieldListener );

        // screen positionining ---------------------------------------------

        // SCROLL if necessary
        getView().scrollNodeToVisible(node, xExtraWidth);

        // NOTE: this must be calculated after scroll because the pane location changes
        Point frameScreenLocation = getFrame().getLayeredPane().getLocationOnScreen();
        Point nodeScreenLocation = node.getLocationOnScreen();

        int xLeft = (int)(nodeScreenLocation.getX() -  frameScreenLocation.getX() + xOffset);
        if (xExtraWidth < 0) {
          xLeft += xExtraWidth;
        }

        textField.setLocation(xLeft, (int)(nodeScreenLocation.getY()
                                          - frameScreenLocation.getY() + yOffset));
        
        getFrame().getLayeredPane().add(textField); // 2000);
        getFrame().repaint();
 
        SwingUtilities.invokeLater( new Runnable() { // PN 0.6.2
             public void run () { textField.requestFocus(); }});
    }

    public final int NEW_CHILD_WITHOUT_FOCUS = 1;  // old model of insertion
    public final int NEW_CHILD = 2;
    public final int NEW_SIBLING_BEHIND = 3;
    public final int NEW_SIBLING_BEFORE = 4;

    public void addNew(final NodeView target, final int newNodeMode, final KeyEvent e) {
       closeEdit();
       
       MindMapNode newNode = newNode();
       final MindMapNode targetNode = target.getModel();

       switch (newNodeMode) {
         case NEW_SIBLING_BEFORE:
         case NEW_SIBLING_BEHIND:
           if (targetNode.isRoot()) {
             getController().errorMessage(
                 getText("new_node_as_sibling_not_possible_for_the_root"));
             setBlocked(false);
             return; 
           }
           MindMapNode parent = targetNode.getParentNode();
           int childPosition = parent.getChildPosition(targetNode);
           if (newNodeMode == NEW_SIBLING_BEHIND) {
              childPosition++;
           }
           if(targetNode.isLeft()!= null) {
               newNode.setLeft(targetNode.isLeft().getValue());
           }
           getModel().insertNodeInto(newNode, parent, childPosition);
           select(newNode.getViewer());
                getFrame().repaint(); //  getLayeredPane().repaint();
           edit(newNode.getViewer(), target, e, true, false, false);
           break;
  
         case NEW_CHILD:
         case NEW_CHILD_WITHOUT_FOCUS:
           final boolean parentFolded = targetNode.isFolded();
           if (parentFolded) {
             getModel().setFolded(targetNode,false);
           }
           int position = getFrame().getProperty("placenewbranches").equals("last") ?
              targetNode.getChildCount() : 0;
           // Here the NodeView is created for the node. }
           getModel().insertNodeInto(newNode, targetNode, position);
                getFrame().repaint(); //  getLayeredPane().repaint();
           if (newNodeMode == NEW_CHILD) {
             select(newNode.getViewer());
           }
           final NodeView editView = newNode.getViewer();
           edit(editView, targetNode.getViewer(), e, true, parentFolded, false);
           break;
       }
    }

//     public void toggleFolded() {
//         MindMapNode node = getSelected();
//         // fold the node only if the node is not a leaf (PN 0.6.2)
//         if (node.hasChildren()
//             || node.isFolded()
//             || Tools.safeEquals(getFrame()
//                 .getProperty("enable_leaves_folding"),"true")) {
//           getModel().setFolded(node, !node.isFolded());
//         }
//         getView().selectAsTheOnlyOneSelected(node.getViewer());
//     }

    public void toggleFolded() {
        /* Retrieve the information whether or not all nodes have the same folding state. */
        Tools.BooleanHolder state = null; 
        boolean allNodeHaveSameFoldedStatus = true;
        for (ListIterator it = getSelecteds().listIterator();it.hasNext();) {
            MindMapNode node = (MindMapNode)it.next();
            if(state == null) {
                state = new Tools.BooleanHolder();
                state.setValue(node.isFolded());
            } else {
                if(node.isFolded() != state.getValue()) {
                    allNodeHaveSameFoldedStatus = false;
                    break;
                }
            }
        }
        /* if the folding state is ambiguous, the nodes are folded. */
        boolean fold = true;
        if(allNodeHaveSameFoldedStatus && state != null) {
            fold = !state.getValue();
        }
        MindMapNode lastNode = null;
        for (ListIterator it = getView().getSelectedsByDepth().listIterator();it.hasNext();) {
            MindMapNode node = ((NodeView)it.next()).getModel();
            // fold the node only if the node is not a leaf (PN 0.6.2)
            if (node.hasChildren() || node.isFolded() || Tools.safeEquals(getFrame().getProperty("enable_leaves_folding"),"true"))   {
                getModel().setFolded(node, fold);
            }
            lastNode = node;
        }
        if(lastNode != null)
            getView().selectAsTheOnlyOneSelected(lastNode.getViewer());
    }


    /**
     * If any children are folded, unfold all folded children.
     * Otherwise, fold all children.
     */
    protected void toggleChildrenFolded() {
        // have NodeAdapter; need NodeView
        MindMapNode parent = getSelected();
        ListIterator children_it = parent.getViewer().getChildrenViews().listIterator();
        boolean areAnyFolded = false;
        while(children_it.hasNext() && !areAnyFolded) {
            NodeView child = (NodeView)children_it.next();
            if(child.getModel().isFolded()) {
                areAnyFolded = true;
            }
        }

        // fold the node only if the node is not a leaf (PN 0.6.2)
        boolean enableLeavesFolding = 
            Tools.safeEquals(getFrame().
               getProperty("enable_leaves_folding"),"true");

        children_it = parent.getViewer().getChildrenViews().listIterator();
        while(children_it.hasNext()) {
            MindMapNode child = ((NodeView)children_it.next()).getModel();
            if (child.hasChildren() 
                || enableLeavesFolding
                || child.isFolded()) {
              getModel().setFolded(child, !areAnyFolded); // (PN 0.6.2)
            }
        }
        getView().selectAsTheOnlyOneSelected(parent.getViewer());

        getController().obtainFocusForSelected(); // focus fix
    }

    protected void setLinkByTextField() {
        // Requires J2SDK1.4.0!
        String inputValue = JOptionPane.showInputDialog
           (getText("edit_link_manually"), getModel().getLink(getSelected()));
        if (inputValue != null) {
           if (inputValue.equals("")) {
              inputValue = null;        // In case of no entry unset link
           }
           getModel().setLink(getSelected(),inputValue);
        }
    }

    protected void setLinkByFileChooser() {
		String relative = getLinkByFileChooser(getFileFilter());
		if (relative != null) getModel().setLink(getSelected(),relative);
	}
	
	protected void setImageByFileChooser() {
		ExampleFileFilter filter = new ExampleFileFilter();
		filter.addExtension("jpg");
		filter.addExtension("jpeg");
		filter.addExtension("png");
		filter.addExtension("gif");
		filter.setDescription("JPG, PNG and GIF Images");

                // Are there any selected nodes with pictures?                
                boolean picturesAmongSelecteds = false;
                for (ListIterator e = getSelecteds().listIterator();e.hasNext();) {
                   String link = ((MindMapNode)e.next()).getLink();
                   if (link != null) {
                      if (filter.accept(new File(link))) {
                         picturesAmongSelecteds = true;
                         break;
                      }
                   }
                }

                try {
                   if (picturesAmongSelecteds) {
                      for (ListIterator e = getSelecteds().listIterator();e.hasNext();) {
                         MindMapNode node = (MindMapNode)e.next();
                         if (node.getLink() != null) {
                            String possiblyRelative = node.getLink();
                            String relative = Tools.isAbsolutePath(possiblyRelative) ?
                               new File(possiblyRelative).toURL().toString() : possiblyRelative;
                            if (relative != null) {
                               String strText = "<html><img src=\"" + relative + "\">"; 
                               node.setLink(null);
                               getModel().changeNode(node,strText);
                            }
                         }
                      }
                   }
                   else {
                      String relative = getLinkByFileChooser(filter);
                      if (relative != null) {
                         String strText = "<html><img src=\"" + relative + "\">"; 
                         getModel().changeNode((MindMapNode)getSelected(),strText);
                      } 
                   }
                }
                catch (MalformedURLException e) {e.printStackTrace(); }
	}
   
	protected String getLinkByFileChooser(FileFilter fileFilter) {
        URL link;
		String relative = null;
        File input;
        JFileChooser chooser = null;
        if (getMap().getFile() == null) {
            JOptionPane.showMessageDialog(getFrame().getContentPane(), getText("not_saved_for_link_error"), 
                                          "FreeMind", JOptionPane.WARNING_MESSAGE);
			return null;
            // In the previous version Freemind automatically displayed save
            // dialog. It happened very often, that user took this save
            // dialog to be an open link dialog; as a result, the new map
            // overwrote the linked map.

        }
        if ((getMap().getFile() != null) && (getMap().getFile().getParentFile() != null)) {
            chooser = new JFileChooser(getMap().getFile().getParentFile());
        } else {
            chooser = new JFileChooser();
        }

		if (fileFilter != null) {
           // Set filters, make sure AcceptAll filter comes first
		   chooser.setFileFilter(fileFilter);
		} else {
			chooser.setFileFilter(chooser.getAcceptAllFileFilter());
		}
 
        int returnVal = chooser.showOpenDialog(getFrame().getContentPane());
        if (returnVal==JFileChooser.APPROVE_OPTION) {
            input = chooser.getSelectedFile();
            try {
                link = input.toURL();
                relative = link.toString();
            } catch (MalformedURLException ex) {
                getController().errorMessage(getText("url_error"));
                return null;
            }
            if (getFrame().getProperty("links").equals("relative")) {
                //Create relative URL
                try {
                    relative = Tools.toRelativeURL(getMap().getFile().toURL(), link);
                } catch (MalformedURLException ex) {
                    getController().errorMessage(getText("url_error"));
                    return null;
                }
            }
        }
		return relative;
    }

    public void loadURL(String relative) {
        URL absolute = null;
        if (getMap().getFile() == null) {
            getFrame().out("You must save the current map first!");
            save(); }
        try {
           if (Tools.isAbsolutePath(relative)) {
              // Protocol can be identified by rexep pattern "[a-zA-Z]://.*".
              // This should distinguish a protocol path from a file path on most platforms.
              // 1)  UNIX / Linux - obviously
              // 2)  Windows - relative path does not contain :, in absolute path is : followed by \.
              // 3)  Mac - cannot remember

              // If relative is an absolute path, then it cannot be a protocol.
              // At least on Unix and Windows. But this is not true for Mac!!

              // Here is hidden an assumption that the existence of protocol implies !Tools.isAbsolutePath(relative).
              // The code should probably be rewritten to convey more logical meaning, on the other hand
              // it works on Windows and Linux.

              //absolute = new URL("file://"+relative); }
              absolute = new File(relative).toURL(); }
            else {
              absolute = new URL(getMap().getFile().toURL(), relative);
              // Remark: getMap().getFile().toURL() returns URLs like file:/C:/...
              // It seems, that it does not cause any problems.
            }

           String extension = Tools.getExtension(absolute.toString());
           if ((extension != null) && extension.equals("mm")) {   // ---- Open Mind Map
              String fileName = absolute.getFile();
              File file = new File(fileName);
              if(!getController().getMapModuleManager().tryToChangeToMapModule(file.getName())) {
                 //this can lead to confusion if the user handles multiple maps with the same name.
                 getFrame().setWaitingCursor(true);
                 load(file); }}
           else {                                                 // ---- Open URL in browser
               // fc, 14.12.2003: The following code seems not very good. Imagine file names with spaces. Then they occur as %20, now the OS does not find the file, 
               // etc. If this is necessary, this should be done in the openDocument command.
//               if (absolute.getProtocol().equals("file")) {                 
//                  File file = new File (Tools.urlGetFile(absolute));
//                  // If file does not exist, try http protocol (but only if it is reasonable)
//                  if (!file.exists()) {
//                     if (relative.matches("^[-\\.a-z0-9]*/?$")) {
//                        absolute = new URL("http://"+relative); }
//                     else {
//                        // This cannot be a base, to which http:// may be added.
//                        getController().errorMessage("File \""+file+"\" does not exist.");
//                        return; }}}
              getFrame().openDocument(absolute); }}
        catch (MalformedURLException ex) {
            getController().errorMessage(getText("url_error")+"\n"+ex);
            return; }
        catch (FileNotFoundException e) {
            int returnVal = JOptionPane.showConfirmDialog
               (getView(),
                getText("repair_link_question"),
                getText("repair_link"),
                JOptionPane.YES_NO_OPTION);
            if (returnVal==JOptionPane.YES_OPTION) {
               setLinkByTextField(); }}
        catch (Exception e) { e.printStackTrace(); }
        getFrame().setWaitingCursor(false);
    }

    public void loadURL() {
        String link = getSelected().getLink();
        if (link != null) {
            loadURL(link);
        }
    }

    //
    // Convenience methods
    //

    protected Mode getMode() {
        return mode;
    }

    protected void setMode(Mode mode) {
        this.mode = mode;
    }

    protected MapModule getMapModule() {
        return getController().getMapModuleManager().getMapModule();
    }

    public MapAdapter getMap() {
        if (getMapModule() != null) {
            return (MapAdapter)getMapModule().getModel();
        } else {
            return null;
        }
    }

    public URL getResource (String name) {
        return getFrame().getResource(name);
    }

    public Controller getController() {
        return getMode().getController();
    }

    public FreeMindMain getFrame() {
        return getController().getFrame();
    }

    private MapAdapter getModel() {
        return (MapAdapter)getController().getModel();
    }

    public MapView getView() {
        return getController().getView();
    }

    protected void updateMapModuleName() {
        getController().getMapModuleManager().updateMapModuleName();
    }

    private NodeAdapter getSelected() {
        return (NodeAdapter)getView().getSelected().getModel();
    }

    public boolean extendSelection(MouseEvent e) {
        NodeView newlySelectedNodeView = (NodeView)e.getSource();
        //MindMapNode newlySelectedNode = newlySelectedNodeView.getModel();
        boolean extend = e.isControlDown(); 
        boolean range = e.isShiftDown(); 
        boolean branch = e.isAltGraphDown() || e.isAltDown(); /* windows alt, linux altgraph .... */ 
        boolean retValue = false;

        if (extend || range || branch || !getView().isSelected(newlySelectedNodeView)) {
            if (!range) {
                if (extend)
                    getView().toggleSelected(newlySelectedNodeView);
                else
                    select(newlySelectedNodeView);
                retValue = true;
            }
            else {
                retValue = getView().selectContinuous(newlySelectedNodeView); 
//                 /* fc, 25.1.2004: replace getView by controller methods.*/
//                 if (newlySelectedNodeView != getView().getSelected() &&
//                     newlySelectedNodeView.isSiblingOf(getView().getSelected())) {
//                     getView().selectContinuous(newlySelectedNodeView); 
//                     retValue = true;
//                 } else {
//                     /* if shift was down, but no range can be selected, then the new node is simply selected: */
//                     if(!getView().isSelected(newlySelectedNodeView)) {
//                         getView().toggleSelected(newlySelectedNodeView);
//                         retValue = true;
//                     }
            }
            if(branch) {
                getView().selectBranch(newlySelectedNodeView, extend); 
                retValue = true;
            }    
        }

        if(retValue) {
            e.consume();
        
            // Display link in status line
            String link = newlySelectedNodeView.getModel().getLink();
            link = (link != null ? link : " ");
            getController().getFrame().out(link); 
        }
        return retValue;
    }

    private void select( NodeView node) {
        getView().selectAsTheOnlyOneSelected(node);
        getView().setSiblingMaxLevel(node.getModel().getNodeLevel()); // this level is default
    }

    ////////////
    //  Actions
    ///////////

    protected class NewMapAction extends AbstractAction {
        ControllerAdapter c;
        public NewMapAction(ControllerAdapter controller) {
            super(getText("new"), new ImageIcon(getResource("images/New24.gif")));
            c = controller;
            //Workaround to get the images loaded in jar file.
            //they have to be added to jar manually with full path from root
            //I really don't like this, but it's a bug of java
        }
        public void actionPerformed(ActionEvent e) {
            c.newMap();
        }
    }

    protected class OpenAction extends AbstractAction {
        ControllerAdapter mc;
        public OpenAction(ControllerAdapter modeController) {
            super(getText("open"), new ImageIcon(getResource("images/Open24.gif")));
            mc = modeController;
        }
        public void actionPerformed(ActionEvent e) {
            mc.open();
			getController().setTitle(); // Possible update of read-only
        }
    }

    protected class SaveAction extends AbstractAction {
        ControllerAdapter mc;
        public SaveAction(ControllerAdapter modeController) {
            super(getText("save"), new ImageIcon(getResource("images/Save24.gif")));
            mc = modeController;
        }
        public void actionPerformed(ActionEvent e) {
            mc.save();
            getFrame().out(getText("saved")); // perhaps... (PN)
			getController().setTitle(); // Possible update of read-only
        }
    }

    protected class SaveAsAction extends AbstractAction {
        ControllerAdapter mc;
        public SaveAsAction(ControllerAdapter modeController) {
            super(getText("save_as"), new ImageIcon(getResource("images/SaveAs24.gif")));
            mc = modeController;
        }
        public void actionPerformed(ActionEvent e) {
            mc.saveAs();
			getController().setTitle(); // Possible update of read-only
        }
    }

    protected class FindAction extends AbstractAction {
        public FindAction() {
           super(getText("find")); }
        public void actionPerformed(ActionEvent e) {
           String what = JOptionPane.showInputDialog(getView().getSelected(),
                                                     getText("find_what"));
           if (what == null || what.equals("")) {
              return; }
           boolean found = getView().getModel().find
              (getView().getSelected().getModel(), what, /*caseSensitive=*/ false);
           getView().repaint();
           if (!found) {
              getController().informationMessage
                 (getText("no_found_from").replaceAll("\\$1",what).
                  replaceAll("\\$2", getView().getModel().getFindFromText()),
                  getView().getSelected()); }}}

    protected class FindNextAction extends AbstractAction {
        public FindNextAction() {
           super(getText("find_next")); }
        public void actionPerformed(ActionEvent e) {
           String what = getView().getModel().getFindWhat();
           if (what == null) {
              getController().informationMessage(getText("no_previous_find"), getView().getSelected());
              return; }
           boolean found = getView().getModel().findNext();
           getView().repaint();
           if (!found) {
              getController().informationMessage
                 (getText("no_more_found_from").replaceAll("\\$1",what).
                  replaceAll("\\$2", getView().getModel().getFindFromText()),
                  getView().getSelected()); }}}

    protected class GotoLinkNodeAction extends AbstractAction {
        MindMapNode source;
        public GotoLinkNodeAction(String text, MindMapNode source) {
            super("", new ImageIcon(getResource("images/Link.png")));
            // only display a reasonable part of the string. the rest is available via the short description (tooltip).
            String adaptedText = new String(text);
            adaptedText = adaptedText.replaceAll("<html>", "");
            if(adaptedText.length() > 40)
                adaptedText = adaptedText.substring(0,40) + " ...";
            putValue(Action.NAME, getText("follow_link") + adaptedText );
            putValue(Action.SHORT_DESCRIPTION, text);
            this.source = source;
        }

        public void actionPerformed(ActionEvent e) {
            getMap().displayNode(source, null);
        }
    }


    //
    // Node editing
    //

    protected class EditAction extends AbstractAction {
        public EditAction() {
            super(getText("edit"));
        }
        public void actionPerformed(ActionEvent e) {
            edit(null, false, false);
        }
    }

    protected class EditLongAction extends AbstractAction {
        public EditLongAction() {
            super(getText("edit_long_node"));
        }
        public void actionPerformed(ActionEvent e) {
            edit(null, false, true);
        }
    }

    // old model of inserting node
    protected class NewChildWithoutFocusAction extends AbstractAction {
        public NewChildWithoutFocusAction() {
            super(getText("new_node"));
        }
        public void actionPerformed(ActionEvent e) {
            addNew(getView().getSelected(), NEW_CHILD_WITHOUT_FOCUS, null);
        }
    }

    // new model of inserting node
    protected class NewSiblingAction extends AbstractAction {
        public NewSiblingAction() {
            super(getText("new_sibling_behind"));
        }
        public void actionPerformed(ActionEvent e) {
            addNew(getView().getSelected(), NEW_SIBLING_BEHIND, null);
        }
    }

    protected class NewChildAction extends AbstractAction {
        public NewChildAction() {
            super(getText("new_child"));
        }
        public void actionPerformed(ActionEvent e) {
           addNew(getView().getSelected(), NEW_CHILD, null);
        }
    }

    protected class NewPreviousSiblingAction extends AbstractAction {
        public NewPreviousSiblingAction() {
            super(getText("new_sibling_before"));
        }
        public void actionPerformed(ActionEvent e) {
            addNew(getView().getSelected(), NEW_SIBLING_BEFORE, null);
        }
    }


    protected class RemoveAction extends AbstractAction {
        public RemoveAction() {
            super(getText("remove_node"));
        }
        public void actionPerformed(ActionEvent e) {
           if (getMapModule() != null) {
              getView().getModel().cut();
              getController().obtainFocusForSelected(); }}}

    protected class NodeUpAction extends AbstractAction {
        public NodeUpAction() {
            super(getText("node_up"));
        }
        public void actionPerformed(ActionEvent e) {
            MindMapNode selected = getView().getSelected().getModel();
            if(!selected.isRoot()) {
                MindMapNode parent = selected.getParentNode();
                int index = getModel().getIndexOfChild(parent, selected);
                int newIndex = getModel().moveNodeTo(selected,parent,index, -1);
                getModel().removeNodeFromParent(selected);
                getModel().insertNodeInto(selected,parent,newIndex);
//                 int maxindex = parent.getChildCount(); // (PN)
//                 if(index - 1 <0) {
//                     getModel().insertNodeInto(selected,parent,maxindex, -1);
//                 } else {
//                     getModel().insertNodeInto(selected,parent,index - 1, -1);
//                 }
                getModel().nodeStructureChanged(parent);
                getView().selectAsTheOnlyOneSelected(selected.getViewer());

                getController().obtainFocusForSelected(); // focus fix
            }
        }
    }

    protected class NodeDownAction extends AbstractAction {
        public NodeDownAction() {
            super(getText("node_down"));
        }
        public void actionPerformed(ActionEvent e) {
            MindMapNode selected = getView().getSelected().getModel();
            if(!selected.isRoot()) {
                MindMapNode parent = selected.getParentNode();
                int index = getModel().getIndexOfChild(parent, selected);
                int newIndex = getModel().moveNodeTo(selected,parent,index, 1);
                getModel().removeNodeFromParent(selected);
                getModel().insertNodeInto(selected,parent,newIndex);
//                 int maxindex = parent.getChildCount(); // (PN)
//                 if(index + 1 > maxindex) {
//                     getModel().insertNodeInto(selected,parent,0, 1);
//                 } else {
//                     getModel().insertNodeInto(selected,parent,index + 1, 1);
//                 }
                getModel().nodeStructureChanged(parent);
                getView().selectAsTheOnlyOneSelected(selected.getViewer());
                
                getController().obtainFocusForSelected(); // focus fix
            }
        }
    }

    protected class ToggleFoldedAction extends AbstractAction {
        public ToggleFoldedAction() {
            super(getText("toggle_folded"));
        }
        public void actionPerformed(ActionEvent e) {
            toggleFolded();
        }
    }

    protected class ToggleChildrenFoldedAction extends AbstractAction {
        public ToggleChildrenFoldedAction() {
            super(getText("toggle_children_folded"));
        }
        public void actionPerformed(ActionEvent e) {
            toggleChildrenFolded();
        }
    }

    protected class SetLinkByFileChooserAction extends AbstractAction {
        public SetLinkByFileChooserAction() {
            super(getText("set_link_by_filechooser"));
        }
        public void actionPerformed(ActionEvent e) {
            setLinkByFileChooser();
        }
    }

	protected class SetImageByFileChooserAction extends AbstractAction {
		public SetImageByFileChooserAction() {
			super(getText("set_image_by_filechooser"));
		}
		public void actionPerformed(ActionEvent e) {
                       setImageByFileChooser();
                        getController().obtainFocusForSelected();
		}
	}

    protected class SetLinkByTextFieldAction extends AbstractAction {
        public SetLinkByTextFieldAction() {
            super(getText("set_link_by_textfield"));
        }
        public void actionPerformed(ActionEvent e) {
            setLinkByTextField();
        }
    }


    protected class FollowLinkAction extends AbstractAction {
        public FollowLinkAction() {
            super(getText("follow_link"));
        }
        public void actionPerformed(ActionEvent e) {
            loadURL();
        }
    }

    protected class CopyAction extends AbstractAction {
        public CopyAction(Object controller) {
            super(getText("copy"), new ImageIcon(getResource("images/Copy24.gif")));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
           if(getMapModule() != null) {
              Transferable copy = getView().getModel().copy();
              if (copy != null) {
                 clipboard.setContents(copy,null); }}}}

    protected class CopySingleAction extends AbstractAction {
        public CopySingleAction(Object controller) {
           super(getText("copy_single"));
           setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
           if(getMapModule() != null) {
              Transferable copy = getView().getModel().copySingle();
              if (copy != null) {
                 clipboard.setContents(copy,null); }}}}

    protected class CutAction extends AbstractAction {
        public CutAction(Object controller) {
            super(getText("cut"), new ImageIcon(getResource("images/Cut24.gif")));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
           if (getMapModule() != null) {
              Transferable copy = getView().getModel().cut();
              if (copy != null) {
                 clipboard.setContents(copy,null); 
                 getController().obtainFocusForSelected(); }}}}

    protected class PasteAction extends AbstractAction {
        public PasteAction(Object controller) {
            super(getText("paste"),new ImageIcon(getResource("images/Paste24.gif")));
            setEnabled(false); }
        public void actionPerformed(ActionEvent e) {
            if(clipboard != null) {
               getModel().paste(clipboard.getContents(this), getView().getSelected().getModel()); }}}

    protected class FileOpener implements DropTargetListener {
        private boolean isDragAcceptable(DropTargetDragEvent event) {
            // check if there is at least one File Type in the list
            DataFlavor[] flavors = event.getCurrentDataFlavors();
            for (int i = 0; i < flavors.length; i++) {
                if (flavors[i].isFlavorJavaFileListType()) {
                    //              event.acceptDrag(DnDConstants.ACTION_COPY);
                    return true;
                }
            }
            //      event.rejectDrag();
            return false;
        }

        private boolean isDropAcceptable(DropTargetDropEvent event) {
            // check if there is at least one File Type in the list
            DataFlavor[] flavors = event.getCurrentDataFlavors();
            for (int i = 0; i < flavors.length; i++) {
                if (flavors[i].isFlavorJavaFileListType()) {
                    return true;
                }
            }
            return false;
        }

        public void drop (DropTargetDropEvent dtde) {
            if(!isDropAcceptable(dtde)) {
                dtde.rejectDrop();
                return;
            }
            dtde.acceptDrop(DnDConstants.ACTION_COPY);
            try {
                Object data =
                    dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                if (data == null) {
                    // Shouldn't happen because dragEnter() rejects drags w/out at least
                    // one javaFileListFlavor. But just in case it does ...
                    dtde.dropComplete(false);
                    return;
                }
                Iterator iterator = ((List)data).iterator();
                while (iterator.hasNext()) {
                    File file = (File)iterator.next();
                    load(file);
                }
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(getView(),
                                              "Couldn't open dropped file(s). Reason: " + e.getMessage()
                                              //getText("file_not_found")
                                              );
                dtde.dropComplete(false);
                return;
            }
            dtde.dropComplete(true);
        }

        public void dragEnter (DropTargetDragEvent dtde) {
            if(!isDragAcceptable(dtde)) {
                dtde.rejectDrag();
                return;
            }
        }

        public void dragOver (DropTargetDragEvent e) {}
        public void dragExit (DropTargetEvent e) {}
        public void dragScroll (DropTargetDragEvent e) {}
        public void dropActionChanged (DropTargetDragEvent e) {}
    }

   protected class EditCopyAction extends AbstractAction {
       private JTextComponent textComponent;
       public EditCopyAction(JTextComponent textComponent) {
          super(getText("copy")); 
          this.textComponent = textComponent; }
        public void actionPerformed(ActionEvent e) {
           String selection = textComponent.getSelectedText();
           if (selection != null) {
              clipboard.setContents(new StringSelection(selection),null); }}}

   private class EditPopupMenu extends JPopupMenu {
      //private JTextComponent textComponent;

    public EditPopupMenu(JTextComponent textComponent) {
       //this.textComponent = textComponent;        
       	this.add(new EditCopyAction(textComponent));
    }
   }




}
