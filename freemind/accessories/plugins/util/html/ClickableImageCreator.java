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
/*$Id: ClickableImageCreator.java,v 1.1.2.1.8.1 2005-07-12 15:41:13 dpolivaev Exp $*/

package accessories.plugins.util.html;

import java.awt.Rectangle;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringEscapeUtils;

import freemind.modes.MindMapNode;
import freemind.modes.ModeController;

/** */
public class ClickableImageCreator {

    public static class AreaHolder {
        // <area shape="rect" href="#id47808" alt="Import/Export of parts of a
        // map" title="Import/Export of parts of a map" coords="" />
        // <area shape="rect" href="#id47708" alt="Screenshots cross-red coming
        // soon." title="Screenshots cross-red coming soon."
        // coords="699,143,835,168" />
        String shape = "rect";

        String href;

        String alt;

        String title;

        Rectangle coordinates = new Rectangle();

    }

    Vector area = new Vector();


    private final MindMapNode root;

    private final ModeController controller;


    private Rectangle innerBounds;


    private final String regExpLinkReplacement;

    /**
     * @param root
     * @param controller
     * @param regExpLinkReplacement if for example the link abc must be replaced with FMabcFM,
     * then this string has to be FM$1FM.
     */
    public ClickableImageCreator(MindMapNode root,
            ModeController controller, String regExpLinkReplacement) {
        super();
        this.root = root;
        this.regExpLinkReplacement = regExpLinkReplacement;
		innerBounds = controller.getView().getInnerBounds(root.getViewer());
        this.controller = controller;
        createArea();
    }

    public String generateHtml() {
        StringBuffer htmlArea = new StringBuffer();
        for (Iterator i = area.iterator(); i.hasNext();) {
            AreaHolder holder = (AreaHolder) i.next();
            htmlArea.append("<area shape=\"" + holder.shape + "\" href=\"#"
                    + holder.href.replaceFirst("^(.*)$", regExpLinkReplacement) + "\" alt=\""
                    + StringEscapeUtils.escapeHtml(holder.alt) + "\" title=\""
                    + StringEscapeUtils.escapeHtml(holder.title)
                    + "\" coords=\"" + holder.coordinates.x + ","
                    + holder.coordinates.y + ","
                    + (holder.coordinates.width + holder.coordinates.x) + ","
                    + +(holder.coordinates.height + holder.coordinates.y)
                    + "\" />");
        }
        return htmlArea.toString();
    }

    private void createArea() {
        createArea(root);
    }

    /**
     * @param root2
     */
    private void createArea(MindMapNode node) {
        if (node != null && node.getViewer() != null) {
            AreaHolder holder = new AreaHolder();
            holder.title = node.getShortText(controller);
            holder.alt = node.getShortText(controller);
            holder.href = node.getObjectId(controller);
            holder.coordinates.x = (int) (node.getViewer().getX()-innerBounds.getMinX());
            holder.coordinates.y = (int) (node.getViewer().getY()-innerBounds.getMinY());
            holder.coordinates.width = node.getViewer().getWidth();
            holder.coordinates.height = node.getViewer().getHeight();
            area.add(holder);
            for (Iterator i = node.childrenUnfolded(); i.hasNext();) {
                MindMapNode child = (MindMapNode) i.next();
                createArea(child);
            }
        }
    }

}

