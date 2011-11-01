/*FreeMind - a program for creating and viewing mindmaps
 *Copyright (C) 2000-2005  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 *
 *See COPYING for details
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
 * Created on 02.05.2004
 */
/*$Id: EditNodeExternalApplication.java,v 1.1.4.3 2008/03/14 21:15:24 christianfoltin Exp $*/

package freemind.view.mindmapview;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.text.MessageFormat;

import javax.swing.JFrame;

import freemind.main.Tools;
import freemind.modes.ModeController;

/**
 * @author Daniel Polansky
 * 
 */
public class EditNodeExternalApplication extends EditNodeBase {

	private KeyEvent firstEvent;
	private boolean lastEditingWasSuccessful;

	public EditNodeExternalApplication(final NodeView node, final String text,
			final KeyEvent firstEvent, ModeController controller,
			EditControl editControl) {
		super(node, text, controller, editControl);
		this.firstEvent = firstEvent;
	}

	public void show() {
		final JFrame mainWindow = (JFrame) getFrame();
		lastEditingWasSuccessful = false;
		// final Controller controller = getController();
		// mainWindow.setEnabled(false);
		new Thread() {
			public void run() {
				FileWriter writer = null;
				try {

					File temporaryFile = File.createTempFile("tmm", ".html");

					// a. Write the text of the long node to the temporary file
					writer = new FileWriter(temporaryFile);
					writer.write(text);
					writer.close();

					// b. Call the editor
					String htmlEditingCommand = getFrame().getProperty(
							"html_editing_command");
					String expandedHtmlEditingCommand = new MessageFormat(
							htmlEditingCommand)
							.format(new String[] { temporaryFile.toString() });
					// System.out.println("External application:"+expandedHtmlEditingCommand);
					Process htmlEditorProcess = Runtime.getRuntime().exec(
							expandedHtmlEditingCommand);
					int result = htmlEditorProcess.waitFor(); // Here we wait
																// until the
																// editor ends
																// up itself
					// Waiting does not work if the process starts another
					// process,
					// like in case of Microsoft Word. It works with certain
					// versions of FrontPage,
					// and with Vim though.

					// c. Get the text from the temporary file
					String content = Tools.getFile(temporaryFile);
					if (content == null) {
						getEditControl().cancel();
					}
					getEditControl().ok(content);
					lastEditingWasSuccessful = true;
				} catch (Exception e) {
					freemind.main.Resources.getInstance().logException(e);
					try {
						if (writer != null) {
							writer.close();
						}
						// if (bufferedReader != null) {
						// bufferedReader.close();
						// }
					} catch (Exception e1) {
					}
				}
				// setBlocked(false);
				// mainWindow.setEnabled(true); // Not used as it loses focus on
				// the window
				// controller.obtainFocusForSelected(); }
			}
		}.start();
		return;
	}

	protected KeyEvent getFirstEvent() {
		return firstEvent;
	}
}
