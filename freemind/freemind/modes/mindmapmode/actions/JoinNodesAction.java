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
package freemind.modes.mindmapmode.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.view.mindmapview.MapView;

public class JoinNodesAction extends AbstractAction {
    private final MindMapController controller;

    public JoinNodesAction(MindMapController controller) {
        super(controller.getText("join_nodes"));
        this.controller = controller;
    }

    public void actionPerformed(ActionEvent e) {
        MindMapNode selectedNode = controller.getView().getSelected()
                .getModel();
        ArrayList selectedNodes = controller.getView()
                .getSelectedNodesSortedByY();
        joinNodes(selectedNode, selectedNodes);
    }

    public void joinNodes(MindMapNode selectedNode, List selectedNodes) {
        String newContent = "";
        boolean firstLoop = true;

        // Make sure the selected node do not have children
        final MapView mapView = controller.getView();
        for (Iterator it = selectedNodes.iterator(); it.hasNext();) {
            MindMapNode node = (MindMapNode) it.next();
            if (node.hasChildren()) {
                JOptionPane.showMessageDialog(mapView, controller
                        .getText("cannot_join_nodes_with_children"),
                        "FreeMind", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // Join
        for (Iterator it = selectedNodes.iterator(); it.hasNext();) {
            if (firstLoop) {
                firstLoop = false;
            } else {
                newContent += " ";
            }
            MindMapNode node = (MindMapNode) it.next();
            newContent += node.toString();
            if (node != selectedNode) {
                controller.deleteNode(node);
            }
        }

        mapView.selectAsTheOnlyOneSelected(
                mapView.getNodeView(selectedNode));
        controller.setNodeText(selectedNode, newContent);
    }
}

