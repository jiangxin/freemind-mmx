package freemind.modes.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import freemind.modes.MindMapNode;
import freemind.modes.ModeController;

public class JoinNodesAction extends AbstractAction {
    private final ModeController controller;

    public JoinNodesAction(ModeController controller) {
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
        for (Iterator it = selectedNodes.iterator(); it.hasNext();) {
            MindMapNode node = (MindMapNode) it.next();
            if (node.hasChildren()) {
                JOptionPane.showMessageDialog(node.getViewer(), controller
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

        controller.getView().selectAsTheOnlyOneSelected(
                selectedNode.getViewer());
        controller.setNodeText(selectedNode, newContent);
    }
}

