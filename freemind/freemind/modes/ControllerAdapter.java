/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000  Joerg Mueller <joergmueller@bigfoot.com>
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

package freemind.modes;

import freemind.main.FreeMind;
import freemind.main.Tools;
import freemind.controller.Controller;
import freemind.view.MapModule;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.MindMapEdge;
import freemind.modes.Mode;
import freemind.modes.ModeController;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;
import java.awt.Color;
import javax.swing.JColorChooser;
import javax.swing.ImageIcon;
import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.Point;
import javax.swing.JViewport;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;

/**
 * Derive from this class to implement the Controller for your mode. Overload the methods
 * you need for your data model, or use the defaults. There are some default Actions you may want
 * to use for easy editing of your model. Take MindMapController as a sample.
 */
public abstract class ControllerAdapter implements ModeController {

    Mode mode;
    Action setLink = new SetLinkAction();
    Action followLink = new FollowLinkAction();

    public ControllerAdapter(Mode mode) {
	this.mode = mode;
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
     */
    protected FileFilter getFileFilter() {
	return null;
    }

    //
    // Map Management
    //

    public void newMap() {
	getController().newMapModule(newModel());
    }

    public void load(File file) {
 	MapAdapter model = newModel();
 	model.load(file);
	getController().newMapModule(model);
    }

    public void save() {
	if (getModel().isSaved()) return;
	if (getModel().getFile()==null) {
	    saveAs();
	} else {
	    save(getModel().getFile());
	}
    }

    public void save(File file) {
	getModel().save(file);
    }

    //
    // Dialogs with user
    //
    public void open() {
	JFileChooser chooser = new JFileChooser();
	//chooser.setLocale(currentLocale);
	if (getFileFilter() != null) {
	    chooser.addChoosableFileFilter(getFileFilter());
	}
	int returnVal = chooser.showOpenDialog(getView());
	if (returnVal==JFileChooser.APPROVE_OPTION) {
	    load(chooser.getSelectedFile());
	}
    }

    public void saveAs() {
	JFileChooser chooser = new JFileChooser();
	//chooser.setLocale(currentLocale);
	if (getFileFilter() != null) {
	    chooser.addChoosableFileFilter(getFileFilter());
	}
	int returnVal = chooser.showSaveDialog(getView());
	if (returnVal==JFileChooser.APPROVE_OPTION) {//ok pressed
	    File f = chooser.getSelectedFile();
	    //Force the extension to be .mm
	    String ext = Tools.getExtension(f);
	    if(!ext.equals("mm")) {
		f = new File(f.getParent(),f.getName()+".mm");
	    }
	    save(f);
	    //Update the name of the map
	    getController().updateMapModuleName();
	}
    }

    public void close() throws Exception {
	String[] options = {FreeMind.getResources().getString("yes"),FreeMind.getResources().getString("no"),FreeMind.getResources().getString("cancel")};
	if (!getModel().isSaved()) {
	    String text = FreeMind.getResources().getString("save_unsaved")+"\n"+getMapModule().toString();
	    String title = FreeMind.getResources().getString("save");
	    int returnVal = JOptionPane.showOptionDialog( getView(),text,title,JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
	    if (returnVal==JOptionPane.YES_OPTION) {
		save();
	    } else if (returnVal==JOptionPane.NO_OPTION) {
	    } else if (returnVal==JOptionPane.CANCEL_OPTION) {
		throw new Exception();
		//How should I do this? quitAction must be notified, but if I throw an Exception,
		// i can't catch it (its an action)
		//		System.err.println("I'm sorry, cancel is currently not supported.");
	    }
	}
    }


    //
    // Node editing
    //
    public void addNew(NodeView parent) {
	MindMapNode newNode = newNode();
	int place;
	if (FreeMind.userProps.getProperty("placenewbranches").equals("last")) {
	    place = parent.getModel().getChildCount();
	} else {
	    place = 0;
	}
	getModel().insertNodeInto(newNode,parent.getModel(), place);
	edit(newNode.getViewer(),parent);
    }

    public void edit(final NodeView node,final NodeView toBeSelected) {
	getView().scrollNodeToVisible(node);
	final JTextField input = new JTextField(node.getModel().toString());
	Point loc = node.getLocation();
	Point scroll = ((JViewport)getView().getParent()).getViewPosition();
	Point content = getFrame().getContentPane().getLocation();
	//Todo: Remove the constant(40) (the menubar) from this calc
	input.setBounds(loc.x-scroll.x+content.x,loc.y-scroll.y+content.y+40,input.getPreferredSize().width,15);
	input.selectAll();
	input.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    getView().select(toBeSelected);
		    //Focus is lost, so focusLost() is called, which does the work
		}
	    });
	input.addFocusListener(new FocusAdapter() {
		public void focusLost(FocusEvent e) {
  		    getModel().changeNode(node.getModel(),input.getText());
  		    getFrame().getLayeredPane().remove(input);
		}
	    });
		
	getFrame().getLayeredPane().add(input,2000);
	input.requestFocus();
	getFrame().repaint();
    }

    protected void setLink() {
	URL link;
	File input;
	JFileChooser chooser = new JFileChooser();
	if (getFileFilter() != null) {
	    chooser.addChoosableFileFilter(getFileFilter());
	}
	int returnVal = chooser.showOpenDialog(getView());
	if (returnVal==JFileChooser.APPROVE_OPTION) {
	    input = chooser.getSelectedFile();
	    try {
		link = input.toURL();
	    } catch (MalformedURLException ex) {
		JOptionPane.showMessageDialog(getController().getFrame(),"couldn't create valid URL!");
		return;
	    }
	    getModel().setLink(getSelected(),link);
	}
    }

    private void loadURL(URL url) {
	String fileName = url.getFile();
	File file = new File(fileName);
	load(file);
    }

    protected void loadURL() {
	URL link = getSelected().getLink();
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

    protected MapModule getMapModule() {
	return getController().getMapModule();
    }

    protected Controller getController() {
	return getMode().getController();
    }

    protected FreeMind getFrame() {
	return getController().getFrame();
    }

    private MapAdapter getModel() {
	return (MapAdapter)getController().getModel();
    }

    protected MapView getView() {
	return getController().getView();
    }

    private NodeAdapter getSelected() {
	return (NodeAdapter)getView().getSelected().getModel();
    }

    ////////////
    //  Actions
    ///////////

    protected class NewMapAction extends AbstractAction {
	ControllerAdapter c;
	protected NewMapAction(ControllerAdapter controller) {
	    super(FreeMind.getResources().getString("new"), new ImageIcon(controller.getClass().getResource("/images/New24.gif")));
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
	ControllerAdapter c;
	protected OpenAction(ControllerAdapter controller) {
	    super(FreeMind.getResources().getString("open"), new ImageIcon(controller.getClass().getResource("/images/Open24.gif")));
	    c = controller;
	}
	public void actionPerformed(ActionEvent e) {
	    c.open();
	}
    }

    protected class SaveAction extends AbstractAction {
	ControllerAdapter c;
	protected SaveAction(ControllerAdapter controller) {
	    super(FreeMind.getResources().getString("save"), new ImageIcon(controller.getClass().getResource("/images/Save24.gif")));
	    c = controller;
	}
	public void actionPerformed(ActionEvent e) {
	    c.save();
	}
    }	    

    protected class SaveAsAction extends AbstractAction {
	ControllerAdapter c;
	protected SaveAsAction(ControllerAdapter controller) {
	    super(FreeMind.getResources().getString("save_as"), new ImageIcon(controller.getClass().getResource("/images/SaveAs24.gif")));
	    c = controller;
	}
	public void actionPerformed(ActionEvent e) {
	    c.saveAs();
	}
    }


    private class SetLinkAction extends AbstractAction {
	SetLinkAction() {
	    super(FreeMind.getResources().getString("set_link"));
	}
	public void actionPerformed(ActionEvent e) {
	    setLink();
	}
    }

    private class FollowLinkAction extends AbstractAction {
	FollowLinkAction() {
	    super(FreeMind.getResources().getString("follow_link"));
	}
	public void actionPerformed(ActionEvent e) {
	    loadURL();
	}
    }
}
