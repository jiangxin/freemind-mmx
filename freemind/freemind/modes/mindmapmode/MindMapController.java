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

package freemind.modes.mindmapmode;

import freemind.main.FreeMind;
import freemind.main.Tools;
import freemind.controller.Controller;
import freemind.view.MapModule;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.MindMapEdge;
import freemind.modes.Mode;
import freemind.modes.ControllerAdapter;
import freemind.modes.MapAdapter;
import freemind.view.mindmapview.MapView;
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

public class MindMapController extends ControllerAdapter {

    Mode mode;
    Action newMap = new NewMapAction(this);
    Action open = new OpenAction(this);
    Action save = new SaveAction(this);
    Action saveAs = new SaveAsAction(this);

    Action fork = new ForkAction();
    Action bubble = new BubbleAction();
    Action nodeColor = new NodeColorAction();
    Action edgeColor = new EdgeColorAction();
    Action linear = new LinearAction();
    Action bezier = new BezierAction();
    Action italic = new ItalicAction(this);
    Action bold = new BoldAction(this);
    Action underline = new UnderlineAction(this);
    Action normalFont = new NormalFontAction(this);
    Action setLink = new SetLinkAction();
    Action followLink = new FollowLinkAction();

    FileFilter filefilter = new MindMapFilter();

    public MindMapController(Mode mode) {
	super(mode);
    }

    public MapAdapter newModel() {
	return new MindMapMapModel();
    }

    public void save(File file) {
	//	getController().getModel().save(file);
    }

    public FileFilter getFileFilter() {
	return filefilter;
    }

    //Node editing
    void setFontSize(int fontSize) {
	getModel().setFontSize(getSelected(),fontSize);
    }

    void setFont(String font) {
	getModel().setFont(getSelected(),font);
    }

    protected MindMapNode newNode() {
	return new MindMapNodeModel(FreeMind.getResources().getString("new_node"));
    }


    //private
    private MindMapMapModel getModel() {
	return (MindMapMapModel)getController().getModel();
    }

    private MindMapNodeModel getSelected() {
	return (MindMapNodeModel)getView().getSelected().getModel();
    }

    //////////
    // Actions
    /////////

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

    private class ForkAction extends AbstractAction {
	ForkAction() {
	    super(FreeMind.getResources().getString("fork"));
	}
	public void actionPerformed(ActionEvent e) {
	    getModel().setNodeStyle(getSelected(), "fork");
	}
    }

    private class BubbleAction extends AbstractAction {
	BubbleAction() {
	    super(FreeMind.getResources().getString("bubble"));
	}
	public void actionPerformed(ActionEvent e) {
	    getModel().setNodeStyle(getSelected(), "bubble");
	}
    }

    private class LinearAction extends AbstractAction {
	LinearAction() {
	    super(FreeMind.getResources().getString("linear"));
	}
	public void actionPerformed(ActionEvent e) {
	    getModel().setEdgeStyle(getSelected(), "linear");
	}
    }

    private class BezierAction extends AbstractAction {
	BezierAction() {
	    super(FreeMind.getResources().getString("bezier"));
	}
	public void actionPerformed(ActionEvent e) {
	    getModel().setEdgeStyle(getSelected(), "bezier");
	}
    }

    //
    // Fonts
    //
    private class ItalicAction extends AbstractAction {
	ItalicAction(Object controller) {
	    super(FreeMind.getResources().getString("italic"), new ImageIcon(controller.getClass().getResource("/images/Italic24.gif")));
	}
	public void actionPerformed(ActionEvent e) {
	    getModel().setItalic(getSelected());
	}
    }

    private class BoldAction extends AbstractAction {
	BoldAction(Object controller) {
	    super(FreeMind.getResources().getString("bold"), new ImageIcon(controller.getClass().getResource("/images/Bold24.gif")));
	}
	public void actionPerformed(ActionEvent e) {
	    getModel().setBold(getSelected());
	}
    }

    private class NormalFontAction extends AbstractAction {
	NormalFontAction(Object controller) {
	    super(FreeMind.getResources().getString("normal"), new ImageIcon(controller.getClass().getResource("/images/Normal24.gif")));	}
	public void actionPerformed(ActionEvent e) {
	    getModel().setNormalFont(getSelected());
	}
    }

    /**Not yet implemented*/
    private class UnderlineAction extends AbstractAction {
	UnderlineAction(Object controller) {
	    super(FreeMind.getResources().getString("underline"), new ImageIcon(controller.getClass().getResource("/images/Underline24.gif")));
	}
	public void actionPerformed(ActionEvent e) {
	    getModel().setUnderlined(getSelected());
	}
    }

    //
    // Color
    //

    private class NodeColorAction extends AbstractAction {
	NodeColorAction() {
	    super(FreeMind.getResources().getString("node_color"));
	}
	public void actionPerformed(ActionEvent e) {
	    Color color = JColorChooser.showDialog(getView(),"Choose Node Color:",getSelected().getColor() );
	    getModel().setNodeColor(getSelected(), color);
	}
    }

    private class EdgeColorAction extends AbstractAction {
	EdgeColorAction() {
	    super(FreeMind.getResources().getString("edge_color"));
	}
	public void actionPerformed(ActionEvent e) {
	    MindMapNodeModel node = getSelected();
	    Color color = JColorChooser.showDialog(getView(),"Choose Edge Color:",getSelected().getEdge().getColor());
	    getModel().setEdgeColor(node,color);
	}
    }

    //
    // Other classes
    //
    private class MindMapFilter extends FileFilter {
	public boolean accept(File f) {
	    if (f.isDirectory()) return true;
	    String extension = Tools.getExtension(f);
	    if (extension != null) {
		if (extension.equals("mm")) {
		    return true;
		} else {
		    return false;
		}
	    }
	    return false;
	}
	
	public String getDescription() {
	    return FreeMind.getResources().getString("mindmaps");
	}
    }
}
