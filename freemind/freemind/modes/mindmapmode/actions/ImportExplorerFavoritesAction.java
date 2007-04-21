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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import freemind.main.Tools;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;

public class ImportExplorerFavoritesAction extends AbstractAction {
    private final MindMapController controller;

    public ImportExplorerFavoritesAction(MindMapController controller) {
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
                        freemind.main.Resources.getInstance().logException(e);
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
     */
    private MindMapNode addNode(MindMapNode target, String nodeContent) {
        MindMapNode node = controller.addNewNode(target, target.getChildCount(), target.isNewChildLeft());
        controller.setNodeText(node, nodeContent);
        return node;
    }

}

