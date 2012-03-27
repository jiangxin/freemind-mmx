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
/**
 * @author Gorka Puente García
 * @version 1
 */
package onekin.WSL.config;
import java.awt.Container;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import freemind.extensions.ExportHook;
import freemind.extensions.ModeControllerHookAdapter;
import freemind.main.XMLParseException;

public class WSL_ExistMWConfig extends ModeControllerHookAdapter {
	private String question="WSL needs to know the location of the MediaWiki LocalSettings.php";
	private final String localSettings = System.getProperty("user.dir") + File.separator + "plugins/WSL/resources/localSettings.conf";
	
	public WSL_ExistMWConfig() {
		super();
		}

	public void startupMapHook() {
		super.startupMapHook();
		setLocalSettings();
	    }

	public void setLocalSettings() {
		try {
			String strLine="";
			File localSettingsConf = new File(localSettings);
			Container component = getController().getFrame().getContentPane();
			 // Open LocalSettings.conf and get the object of DataInputStream
		    DataInputStream in;
		    try{
				in = new DataInputStream(new FileInputStream(localSettingsConf));
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
			    //Read File Line By Line
				while ((strLine = br.readLine()) != null){
					if(strLine.contains("LocalSettings.php")){
						question="Do you want to change the location of the MediaWiki LocalSettings.php?";
				    	break;
				    }
				 }
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException eio){
				eio.printStackTrace();
			}
			JDialog.setDefaultLookAndFeelDecorated(false);
			int response = JOptionPane.showConfirmDialog(null, strLine + "\n" + question, "Confirm",
			    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (response == JOptionPane.NO_OPTION) {
			  System.out.println("No button clicked");
			} else if (response == JOptionPane.YES_OPTION) {
				JFileChooser chooser = new JFileChooser();
			    chooser.addChoosableFileFilter(new ExportHook.ImageFilter("php", null));
			    // LocalSettings.php
			    File phpFile = getController().getMap().getFile();
			    if (phpFile != null && phpFile.getParentFile() != null) {
			    	chooser.setSelectedFile(phpFile.getParentFile());
			    }
				int returnVal = chooser.showOpenDialog(component);
				if (returnVal == JFileChooser.APPROVE_OPTION) { // ok pressed
					File localSettings = chooser.getSelectedFile();
						try {
							BufferedWriter out = new BufferedWriter(new FileWriter(localSettingsConf));
					        out.write(localSettings.getAbsolutePath());
					        out.close();
					        JOptionPane.showMessageDialog (null, localSettings.getAbsolutePath(), "LocalSettings.php selected", JOptionPane.INFORMATION_MESSAGE);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
				    }
			} else if (response == JOptionPane.CLOSED_OPTION) {
				System.out.println("JOptionPane closed");
			}
		} catch (XMLParseException e) {
			e.printStackTrace();
			}
		}
	}