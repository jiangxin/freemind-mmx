/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
*/
/*
 * Created on 18.04.2006
 * Created by Dimitri Polivaev
 */
package freemind.controller.filter.condition;

import javax.swing.JComponent;
import javax.swing.JLabel;

import freemind.controller.Controller;
import freemind.main.Resources;
import freemind.modes.MindMapNode;

public class NoFilteringCondition implements Condition {

    private static String description;
    private static JComponent renderer;
    private static NoFilteringCondition condition;
    private NoFilteringCondition() {
        super();
        // TODO Auto-generated constructor stub
    }
    public String toString(){
        if(description == null){
            description = Resources.getInstance().getResourceString("filter_no_filtering");
        }
        return  description;
    }
    public boolean checkNode(Controller c, MindMapNode node) {
        return true;
    }
    public JComponent getListCellRendererComponent() {
        if(renderer == null){
            renderer = new JLabel(description);
        }
        return renderer;
    }

    public static Condition createCondition(){
        if(condition == null){
            condition = new NoFilteringCondition();
        }
        return condition;
    }

}
