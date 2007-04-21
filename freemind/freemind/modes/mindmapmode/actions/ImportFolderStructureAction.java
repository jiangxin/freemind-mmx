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
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;

public class ImportFolderStructureAction extends AbstractAction {
    //  Logging: 
    private static java.util.logging.Logger logger;
    private final MindMapController controller;

    public ImportFolderStructureAction(MindMapController controller) {
        super(controller.getText("import_folder_structure"));
        this.controller = controller;
        if(logger == null)
            logger = controller.getFrame().getLogger(this.getClass().getName());
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
            try {
                importFolderStructure(folder, controller.getSelected(),/* redisplay= */
                true);
            } catch (Exception ex) {
                freemind.main.Resources.getInstance().logException(ex);
            }
            controller.getFrame().out("Folder structure imported.");
        }
    }

    public void importFolderStructure(File folder, MindMapNode target,
            boolean redisplay) throws MalformedURLException {
        logger.warning("Entering folder: "+folder);

        if (folder.isDirectory()) {
            File[] list = folder.listFiles();
            // Go recursively to subfolders
            for (int i = 0; i < list.length; i++) {
                if (list[i].isDirectory()) {
                    // Insert a new node

                    MindMapNode node = addNode(target, list[i].getName(), list[i].toURL().toString());
                    importFolderStructure(list[i], node, false);
                }
            }

            // For each file: add it
            for (int i = 0; i < list.length; i++) {
                if (!list[i].isDirectory()) {
                    addNode(target, list[i].getName(), list[i].toURL().toString());
                }
            }
        }
        controller.setFolded(target, true);
        
    }

    /**
     */
    private MindMapNode addNode(MindMapNode target, String nodeContent, String link) {
        MindMapNode node = controller.addNewNode(target,
                target.getChildCount(), target.isNewChildLeft());
        controller.setNodeText(node, nodeContent);
        controller.setLink(node, link);
        return node;
    }

}

