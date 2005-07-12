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
/*$Id: RevertAction.java,v 1.1.2.2.6.1 2005-07-12 15:41:16 dpolivaev Exp $*/
package freemind.modes.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBException;

import freemind.controller.actions.ActionPair;
import freemind.controller.actions.ActorXml;
import freemind.controller.actions.FreemindAction;
import freemind.controller.actions.generated.instance.RevertXmlAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.MindMap;
import freemind.modes.ModeController;

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

	private final ModeController controller;

	/**
	 * @param modeController
	 */
	public RevertAction(ModeController modeController) {
		super("RevertAction", (String) null, modeController);
		controller = modeController;
		addActor(this);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		try {
			File file = controller.getMap().getFile();
			RevertXmlAction doAction = createRevertXmlAction(file);
			RevertXmlAction undoAction = createRevertXmlAction(controller
					.getMap(), null, file.getName());
			controller.getActionFactory().startTransaction(
					this.getClass().getName());
			controller.getActionFactory().executeAction(
					new ActionPair(doAction, undoAction));
			controller.getActionFactory().endTransaction(
					this.getClass().getName());
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

    public void openXmlInsteadOfMap(String xmlFileContent) {
        try {
            RevertXmlAction doAction = createRevertXmlAction(xmlFileContent, null, null);
            RevertXmlAction undoAction = createRevertXmlAction(controller
                    .getMap(), null, null);
            controller.getActionFactory().startTransaction(
                    this.getClass().getName());
            controller.getActionFactory().executeAction(
                    new ActionPair(doAction, undoAction));
            controller.getActionFactory().endTransaction(
                    this.getClass().getName());
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	public RevertXmlAction createRevertXmlAction(File file)
			throws JAXBException, IOException {
		String fileName = file.getAbsolutePath();
		FileReader f = new FileReader(file);
		StringBuffer buffer = new StringBuffer();
		for (int c; (c = f.read()) != -1;)
			buffer.append((char) c);
		f.close();
		return createRevertXmlAction(buffer.toString(), fileName, null);
	}

	public RevertXmlAction createRevertXmlAction(MindMap map, String fileName, String filePrefix)
			throws JAXBException, IOException {
		StringWriter writer = new StringWriter();
		map.getXml(writer);
		return createRevertXmlAction(writer.getBuffer().toString(), fileName,
				filePrefix);
	}

	/**
	 * @param filePrefix is used to generate the name of the reverted map in case that fileName is null.
	 * @return
	 * @throws JAXBException
	 */
	public RevertXmlAction createRevertXmlAction(String xmlPackedFile,
			String fileName, String filePrefix) throws JAXBException {
		RevertXmlAction revertXmlAction = controller.getActionXmlFactory()
				.createRevertXmlAction();
		revertXmlAction.setLocalFileName(fileName);
		revertXmlAction.setMap(xmlPackedFile);
		revertXmlAction.setFilePrefix(filePrefix);
		return revertXmlAction;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.controller.actions.ActorXml#act(freemind.controller.actions.generated.instance.XmlAction)
	 */
	public void act(XmlAction action) {
		if (action instanceof RevertXmlAction) {
			try {
				RevertXmlAction revertAction = (RevertXmlAction) action;

				// close the old map.
				controller.getController().close(true);
				if (revertAction.getLocalFileName() != null) {
					controller.load(new File(revertAction.getLocalFileName()));
				} else {
					// the map is given by xml. we store it and open it.
					String filePrefix = controller.getText("freemind_reverted");
					if (revertAction.getFilePrefix() != null) {
						filePrefix = revertAction.getFilePrefix();
					}
					File tempFile = File.createTempFile(filePrefix, ".mm",
							new File(controller.getFrame()
									.getFreemindDirectory()));
					FileWriter fw = new FileWriter(tempFile);
					fw.write(revertAction.getMap());
					fw.close();
					controller.load(tempFile);
				}
			} catch (Exception e) {
				e.printStackTrace();
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