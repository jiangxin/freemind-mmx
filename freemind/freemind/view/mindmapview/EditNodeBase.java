/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2004  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 *
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
 *
 * Created on 02.05.2004
 */
/*$Id: EditNodeBase.java,v 1.1.2.2 2004-05-09 22:31:16 christianfoltin Exp $*/

package freemind.view.mindmapview;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

import freemind.controller.Controller;
import freemind.main.FreeMindMain;
import freemind.modes.ModeController;

/**
 * @author foltin
 *
 */
public class EditNodeBase {

    public interface EditControl {
    	void cancel();
    	void ok(String newText);
    	void split(String newText, int position);
    
    }


    protected static  final int BUTTON_OK = 0;
    protected static  final int BUTTON_CANCEL = 1;
    protected static  final int BUTTON_SPLIT = 2;
    private NodeView node;
    private EditControl editControl;
    private Clipboard clipboard;
    private ModeController controller;
	private String text;

	EditNodeBase(final NodeView node,
	final String text,
	ModeController controller,
	EditControl editControl) {
		this.controller = controller;
		this.editControl = editControl;
		this.node = node;
		this.text = text;
	}

    /**
    	 * 
    	 */
    protected MapView getView() {
    	return controller.getView();
    }

	protected ModeController getModeController() {
		return controller;
	}

    /**
    	 * 
    	 */
    protected Controller getController() {
    	return controller.getController();
    }

    /**
    	 * @param string
    	 */
    protected String getText(String string) {
    	return controller.getController().getResourceString(string);
    }

    /**
    	 * @return
    	 */
    protected FreeMindMain getFrame() {
    	return controller.getFrame();
    }

    protected boolean binOptionIsTrue(String option) {
    	return true;
    }

	// this enables from outside close the edit mode
	protected FocusListener textFieldListener = null;

	protected class EditCopyAction extends AbstractAction {
		private JTextComponent textComponent;
		public EditCopyAction(JTextComponent textComponent) {
			super(getText("copy"));
			this.textComponent = textComponent;
		}
		public void actionPerformed(ActionEvent e) {
			String selection = textComponent.getSelectedText();
			if (selection != null) {
				clipboard.setContents(new StringSelection(selection), null);
			}
		}
	}

	protected class EditPopupMenu extends JPopupMenu {
		//private JTextComponent textComponent;

		public EditPopupMenu(JTextComponent textComponent) {
			//this.textComponent = textComponent;        
			this.add(new EditCopyAction(textComponent));
		}
	}

    public void closeEdit() {
    	if (textFieldListener != null) {
    		textFieldListener.focusLost(null); // hack to close the edit
    	}
    }

	/**
	 * @return
	 */
	protected String getText() {
		return text;
	}


    /**
     * @return
     */
    public Clipboard getClipboard() {
        return clipboard;
    }

    /**
     * @return
     */
    public EditControl getEditControl() {
        return editControl;
    }

    /**
     * @return
     */
    public NodeView getNode() {
        return node;
    }

    /**
     * @return
     */
    public FocusListener getTextFieldListener() {
        return textFieldListener;
    }

    /**
     * @param string
     */
    public void setText(String string) {
        text = string;
    }

    /**
     * @param listener
     */
    public void setTextFieldListener(FocusListener listener) {
        textFieldListener = listener;
    }

}
