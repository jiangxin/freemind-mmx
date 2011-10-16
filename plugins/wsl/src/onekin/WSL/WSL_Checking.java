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
package onekin.WSL;
import javax.swing.JOptionPane;

import freemind.extensions.ModeControllerHookAdapter;

public class WSL_Checking extends ModeControllerHookAdapter {
	private WSL_Util wsl_util;
	public WSL_Checking() {
		super();
		}

	public void startupMapHook() {
		super.startupMapHook();
		WSL_Main w= new WSL_Main();
		wsl_util = new WSL_Util();
		w.setController(getController());
		Object[] list = wsl_util.checkModel(w.getCurrentFile());
		if(list.length!=0){
			JOptionPane.showMessageDialog (null,  list, "WSL checking report", JOptionPane.WARNING_MESSAGE);
	    }
	    else{
	    	JOptionPane.showMessageDialog (null, "The model is correct", "WSL checking", JOptionPane.INFORMATION_MESSAGE);
	    }
	    getController().getFrame().setWaitingCursor(false);
	}
}