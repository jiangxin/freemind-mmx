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
 * Created on 06.10.2004
 */
/* $Id: FormatPaste.java,v 1.1.4.4.2.1 2006-04-05 21:26:24 dpolivaev Exp $ */

package accessories.plugins;

import javax.swing.JOptionPane;

import freemind.controller.actions.generated.instance.Pattern;
import freemind.modes.MindMapNode;
import freemind.modes.StylePatternFactory;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;

/**
 * @author foltin
 *
 */
public class FormatPaste extends MindMapNodeHookAdapter {
    private static Pattern pattern=null;

    public FormatPaste() {
        super();
    }

    public void invoke(MindMapNode node) {
		super.invoke(node);
		String actionType = getResourceString("actionType");
		if(actionType.equals("copy_format")) {
		    copyFormat(node);
		} else {
		    pasteFormat(node);
		}
	}

    /**
     * @param node
     */
    private void pasteFormat(MindMapNode node) {
        if(pattern==null) {
            JOptionPane.showMessageDialog(getMindMapController().getFrame().getContentPane(),
                    getResourceString("no_format_copy_before_format_paste"),"" /*=Title*/,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        getMindMapController().applyPattern(node, pattern);
    }

    /**
     * @param node
     */
    private void copyFormat(MindMapNode node) {
        pattern = StylePatternFactory.createPatternFromNode(node);
    }



}
