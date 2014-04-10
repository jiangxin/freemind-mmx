/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2014 Christian Foltin, Joerg Mueller, Daniel Polansky, Dimitri Polivaev and others.
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

package freemind.modes.mindmapmode.actions.xml.actors;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import freemind.controller.actions.generated.instance.RevertXmlAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Resources;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.MindMap;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * @author foltin
 * @date 10.04.2014
 */
public class RevertActor extends XmlActorAdapter {

	/**
	 * @param pMapFeedback
	 */
	public RevertActor(ExtendedMapFeedback pMapFeedback) {
		super(pMapFeedback);
	}

	public void revertMap(MindMap map, File file) throws IOException {
		RevertXmlAction doAction = createRevertXmlAction(file);
		RevertXmlAction undoAction = createRevertXmlAction(
				map, null, file.getName());
		execute(new ActionPair(doAction, undoAction));
	}

	public void openXmlInsteadOfMap(String xmlFileContent) {
		try {
			RevertXmlAction doAction = createRevertXmlAction(xmlFileContent,
					null, null);
			RevertXmlAction undoAction = createRevertXmlAction(
					getExMapFeedback().getMap(), null, null);
			execute(new ActionPair(doAction, undoAction));
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
				getExMapFeedback().close(true);
				if (revertAction.getLocalFileName() != null) {
					getExMapFeedback().load(new File(revertAction
							.getLocalFileName()));
				} else {
					// the map is given by xml. we store it and open it.
					String filePrefix = getExMapFeedback()
							.getResourceString("freemind_reverted");
					if (revertAction.getFilePrefix() != null) {
						filePrefix = revertAction.getFilePrefix();
					}
					String xmlMap = revertAction.getMap();
					File tempFile = File.createTempFile(filePrefix,
							freemind.main.FreeMindCommon.FREEMIND_FILE_EXTENSION,
							new File(Resources.getInstance().getFreemindDirectory()));
					FileWriter fw = new FileWriter(tempFile);
					fw.write(xmlMap);
					fw.close();
					getExMapFeedback().load(tempFile);
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
