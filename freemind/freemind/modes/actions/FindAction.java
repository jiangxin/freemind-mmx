package freemind.modes.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import freemind.modes.MindMapNode;
import freemind.modes.ModeController;

public class FindAction extends AbstractAction {
    private final ModeController controller;

    private ArrayList findNodesUnfoldedByLastFind;

    private MindMapNode findFromNode;

    private String findWhat;

    /**
     * @return Returns the findWhat.
     */
    public String getFindWhat() {
        return findWhat;
    }

    public String getFindFromText() {
        return findFromNode.toString();
    }

    private boolean findCaseSensitive;

    private LinkedList findNodeQueue;

    public FindAction(ModeController controller) {
        super(controller.getText("find"), new ImageIcon(controller
                .getController().getResource("images/filefind.png")));
        this.controller = controller;
    }

    public void actionPerformed(ActionEvent e) {
        String what = JOptionPane.showInputDialog(controller.getView()
                .getSelected(), controller.getText("find_what"));
        if (what == null || what.equals("")) {
            return;
        }
        boolean found = find(controller.getSelected(), what, /* caseSensitive= */
        false);
        controller.getView().repaint();
        if (!found) {
            controller.getController().informationMessage(
                    controller.getText("no_found_from")
                            .replaceAll("\\$1", what).replaceAll("\\$2",
                                    getFindFromText()),
                    controller.getView().getSelected());
        }
    }

    public static class FindNextAction extends AbstractAction {
        private final ModeController controller;

        private final FindAction find;

        public FindNextAction(ModeController controller, FindAction find) {
            super(controller.getText("find_next"));
            this.controller = controller;
            this.find = find;
        }

        public void actionPerformed(ActionEvent e) {
            String what = find.getFindWhat();
            if (what == null) {
                controller.getController().informationMessage(
                        controller.getText("no_previous_find"),
                        controller.getView().getSelected());
                return;
            }
            boolean found = find.findNext();
            controller.getView().repaint();
            if (!found) {
                controller.getController().informationMessage(
                        controller.getText("no_more_found_from").replaceAll(
                                "\\$1", what).replaceAll("\\$2",
                                find.getFindFromText()),
                        controller.getView().getSelected());
            }
        }
    }

    public boolean find(MindMapNode node, String what, boolean caseSensitive) {
        findNodesUnfoldedByLastFind = new ArrayList();
        LinkedList nodes = new LinkedList();
        nodes.addFirst(node);
        findFromNode = node;
        if (!caseSensitive) {
            what = what.toLowerCase();
        }
        return find(nodes, what, caseSensitive);
    }

    private boolean find(LinkedList /* queue of MindMapNode */nodes,
            String what, boolean caseSensitive) {
        // Precondition: if !caseSensitive then >>what<< is in lowercase.

        // Fold the path of previously found node
        boolean thereWereNodesToBeFolded = !findNodesUnfoldedByLastFind
                .isEmpty();
        if (!findNodesUnfoldedByLastFind.isEmpty()) {

            //if (false) {
            ListIterator i = findNodesUnfoldedByLastFind
                    .listIterator(findNodesUnfoldedByLastFind.size());
            while (i.hasPrevious()) {
                MindMapNode node = (MindMapNode) i.previous();
                try {
                    controller.setFolded(node, true);
                } catch (Exception e) {
                }
            }
            findNodesUnfoldedByLastFind = new ArrayList();
        }

        // We implement width-first search.
        while (!nodes.isEmpty()) {
            MindMapNode node = (MindMapNode) nodes.removeFirst();
            // Add children to the queue
            for (ListIterator i = node.childrenUnfolded(); i.hasNext();) {
                nodes.addLast(i.next());
            }

            String nodeText = caseSensitive ? node.toString() : node.toString()
                    .toLowerCase();
            if (nodeText.indexOf(what) >= 0) { // Found
                displayNode(node, findNodesUnfoldedByLastFind);
                centerNode(node);

                // Save the state for find next
                findWhat = what;
                findCaseSensitive = caseSensitive;
                findNodeQueue = nodes;

                return true;
            }
        }

        centerNode(findFromNode);
        return false;
    }

    /**
     * Display a node in the display (used by find and the goto action by arrow
     * link actions).
     */
    public void displayNode(MindMapNode node, ArrayList nodesUnfoldedByDisplay) {
        // Unfold the path to the node
        Object[] path = controller.getMap().getPathToRoot(node);
        // Iterate the path with the exception of the last node
        for (int i = 0; i < path.length - 1; i++) {
            MindMapNode nodeOnPath = (MindMapNode) path[i];
            //System.out.println(nodeOnPath);
            if (nodeOnPath.isFolded()) {
                if (nodesUnfoldedByDisplay != null)
                    nodesUnfoldedByDisplay.add(nodeOnPath);
                controller.setFolded(nodeOnPath, false);
            }
            nodeOnPath.getFilterInfo().setAncestor();
        }
        if (node.getViewer() != null && ! node.getViewer().isVisible()){
        	node.getFilterInfo().reset();
        	controller.getMap().nodeChanged(node);
        }
    }

    public boolean findNext() {
        // Precodition: findWhat != null. We check the precodition but give no
        // message.

        // The logic of find next is vulnerable. find next relies on the queue
        // of nodes from previous find / find next. However, between previous
        // find / find next and this find next, nodes could have been deleted
        // or moved. The logic expects that no changes happened, even that no
        // node has been folded / unfolded.

        // You may want to come with more correct solution, but this one
        // works for most uses, and does not cause any big trouble except
        // perhaps for some uncaught exceptions. As a result, it is not very
        // nice, but far from critical and working quite fine.

        if (findWhat != null) {
            return find(findNodeQueue, findWhat, findCaseSensitive);
        }
        return false;
    }

    /**
     * @param node
     */
    private void centerNode(MindMapNode node) {
        // Select the node and scroll to it.
        controller.centerNode(node);
    }

}

