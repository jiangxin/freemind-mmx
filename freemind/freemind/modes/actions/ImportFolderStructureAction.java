package freemind.modes.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.mindmapmode.MindMapNodeModel;

public class ImportFolderStructureAction extends AbstractAction {
    private final ModeController controller;

    public ImportFolderStructureAction(ModeController controller) {
        super(controller.getText("import_folder_structure"));
        this.controller = controller;
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle(controller
                .getText("select_folder_for_importing"));
        int returnVal = chooser.showOpenDialog(controller.getFrame()
                .getContentPane());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File folder = chooser.getSelectedFile();
            controller.getFrame().out("Importing folder structure ...");
            //getFrame().repaint(); // Refresh the frame, namely hide dialog
            // and show status
            //getView().updateUI();
            // Problem: the frame should be refreshed here, but I don't know how
            // to do it
            importFolderStructure(folder, controller.getSelected(),/* redisplay= */
                    true);
            controller.getFrame().out("Folder structure imported.");
        }
    }

    public void importFolderStructure(File folder, MindMapNode target,
            boolean redisplay) {

        if (folder.isDirectory()) {
            File[] list = folder.listFiles();
            // Go recursively to subfolders
            for (int i = 0; i < list.length; i++) {
                if (list[i].isDirectory()) {
                    // Insert a new node

                    MindMapNode node = addNode(target, list[i].getName());
                    try {
                        node.setLink(list[i].toURL().toString());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    importFolderStructure(list[i], node, false);
                }
            }

            // For each file: add it
            for (int i = 0; i < list.length; i++) {
                if (!list[i].isDirectory()) {
                    MindMapNode node = addNode(target, list[i].getName());
                    try {
                        node.setLink(list[i].toURL().toString());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (redisplay) {
            controller.nodeChanged(target);
        }
    }

    /**
     * @param target
     * @param nodeContent
     * @return
     */
    private MindMapNode addNode(MindMapNode target, String nodeContent) {
        MindMapNode node = controller.addNewNode(target,
                target.getChildCount(), target.isLeft());
        controller.setNodeText(node, nodeContent);
        return node;
    }

}

