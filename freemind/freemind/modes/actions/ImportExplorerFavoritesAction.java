package freemind.modes.actions;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import freemind.main.Tools;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;

public class ImportExplorerFavoritesAction extends AbstractAction {
    private final ModeController controller;

    public ImportExplorerFavoritesAction(ModeController controller) {
        super(controller.getText("import_explorer_favorites"));
        this.controller = controller;
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle(controller.getText("select_favorites_folder"));
        int returnVal = chooser.showOpenDialog(controller.getFrame()
                .getContentPane());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File folder = chooser.getSelectedFile();
            controller.getFrame().out("Importing Favorites ...");
            //getFrame().repaint(); // Refresh the frame, namely hide dialog
            // and show status
            //getView().updateUI();
            // Problem: the frame should be refreshed here, but I don't know how
            // to do it
            importExplorerFavorites(folder, controller.getSelected(),/* redisplay= */
                    true);
            controller.getFrame().out("Favorites imported.");
        }
    }

    public boolean importExplorerFavorites(File folder, MindMapNode target,
            boolean redisplay) {
        // Returns true iff any favorites found
        boolean favoritesFound = false;
        if (folder.isDirectory()) {
            File[] list = folder.listFiles();
            // Go recursively to subfolders
            for (int i = 0; i < list.length; i++) {
                if (list[i].isDirectory()) {
                    // Insert a new node
                    String nodeContent = list[i].getName();
                    MindMapNode node = addNode(target, nodeContent);
                    //
                    boolean favoritesFoundInSubfolder = importExplorerFavorites(
                            list[i], node, false);
                    if (favoritesFoundInSubfolder) {
                        favoritesFound = true;
                    } else {
                        controller.deleteNode(node);
                    }
                }
            }

            // For each .url file: add it
            for (int i = 0; i < list.length; i++) {
                if (!list[i].isDirectory()
                        && Tools.getExtension(list[i]).equals("url")) {
                    favoritesFound = true;
                    try {
                        MindMapNode node = addNode(target, Tools
                                .removeExtension(list[i].getName()));
                        // For each line: Is it URL? => Set it as link
                        BufferedReader in = new BufferedReader(new FileReader(
                                list[i]));
                        while (in.ready()) {
                            String line = in.readLine();
                            if (line.startsWith("URL=")) {
                                node.setLink(line.substring(4));
                                break;
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (redisplay) {
            controller.nodeChanged(target);
        }
        return favoritesFound;
    }

    /**
     * @param target
     * @param nodeContent
     * @return
     */
    private MindMapNode addNode(MindMapNode target, String nodeContent) {
        MindMapNode node = controller.addNewNode(target, target.getChildCount(), target.isLeft());
        controller.setNodeText(node, nodeContent);
        return node;
    }

}

