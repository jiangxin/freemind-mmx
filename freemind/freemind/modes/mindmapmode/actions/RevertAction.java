/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2005  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 * Created on 11.03.2005
 */

package freemind.modes.mindmapmode.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;

import javax.swing.JOptionPane;

import freemind.controller.actions.generated.instance.RevertXmlAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.MindMap;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;

/**
 * Reverts the map to the saved version. In Xml, the old map is stored as xml
 * and as an undo action, the new map is stored, too.
 * 
 * Moreover, the filename of the doAction is set to the appropriate map file's
 * name. The undo action has no file name associated.
 * 
 * The action goes like this: close the actual map and open the given Xml/File.
 * If only a Xml string is given, a temporary file name is created, the xml
 * stored into and this map is opened instead of the actual.
 * 
 * @author foltin
 * 
 */
public class RevertAction extends FreemindAction implements ActorXml {

	private final MindMapController mindMapController;

	/**
	 */
	public RevertAction(MindMapController modeController) {
		super("RevertAction", (String) null, modeController);
		mindMapController = modeController;
		addActor(this);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		try {
			File file = mindMapController.getMap().getFile();
			if (file == null) {
				JOptionPane.showMessageDialog(mindMapController.getView(),
						mindMapController.getText("map_not_saved"), "FreeMind",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			RevertXmlAction doAction = createRevertXmlAction(file);
			RevertXmlAction undoAction = createRevertXmlAction(
					mindMapController.getMap(), null, file.getName());
			mindMapController.doTransaction(
					this.getClass().getName(),
					new ActionPair(doAction, undoAction));
		} catch (IOException e) {
			freemind.main.Resources.getInstance().logException(e);
		}

	}

	public void openXmlInsteadOfMap(String xmlFileContent) {
		try {
			RevertXmlAction doAction = createRevertXmlAction(xmlFileContent,
					null, null);
			RevertXmlAction undoAction = createRevertXmlAction(
					mindMapController.getMap(), null, null);
			mindMapController.doTransaction(
					this.getClass().getName(),
					new ActionPair(doAction, undoAction));
		} catch (IOException e) {
			freemind.main.Resources.getInstance().logException(e);
		}
	}

	public RevertXmlAction createRevertXmlAction(File file) throws IOException {
		String fileName = file.getAbsolutePath();
		FileReader f = new FileReader(file);
		StringBuffer buffer = new StringBuffer();
		for (int c; (c = f.read()) != -1;)
			buffer.append((char) c);
		f.close();
		return createRevertXmlAction(buffer.toString(), fileName, null);
	}

	public RevertXmlAction createRevertXmlAction(MindMap map, String fileName,
			String filePrefix) throws IOException {
		StringWriter writer = new StringWriter();
		map.getXml(writer);
		return createRevertXmlAction(writer.getBuffer().toString(), fileName,
				filePrefix);
	}

	/**
	 * @param filePrefix
	 *            is used to generate the name of the reverted map in case that
	 *            fileName is null.
	 */
	public RevertXmlAction createRevertXmlAction(String xmlPackedFile,
			String fileName, String filePrefix) {
		RevertXmlAction revertXmlAction = new RevertXmlAction();
		revertXmlAction.setLocalFileName(fileName);
		revertXmlAction.setMap(xmlPackedFile);
		revertXmlAction.setFilePrefix(filePrefix);
		return revertXmlAction;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.controller.actions.ActorXml#act(freemind.controller.actions.
	 * generated.instance.XmlAction)
	 */
	public void act(XmlAction action) {
		if (action instanceof RevertXmlAction) {
			try {
				RevertXmlAction revertAction = (RevertXmlAction) action;

				// close the old map.
				mindMapController.getController().close(true);
				if (revertAction.getLocalFileName() != null) {
					mindMapController.load(new File(revertAction
							.getLocalFileName()));
				} else {
					// the map is given by xml. we store it and open it.
					String filePrefix = mindMapController
							.getText("freemind_reverted");
					if (revertAction.getFilePrefix() != null) {
						filePrefix = revertAction.getFilePrefix();
					}
					String xmlMap = revertAction.getMap();
					mindMapController.load(xmlMap, filePrefix);
				}
			} catch (Exception e) {
				freemind.main.Resources.getInstance().logException(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.controller.actions.ActorXml#getDoActionClass()
	 */
	public Class getDoActionClass() {
		return RevertXmlAction.class;
	}

}
