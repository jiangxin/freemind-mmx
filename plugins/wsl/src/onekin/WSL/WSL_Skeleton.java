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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import freemind.extensions.ModeControllerHookAdapter;
import freemind.main.XMLParseException;
/**
 * 
 * @author Gorka Puente
 *
 */
public class WSL_Skeleton extends ModeControllerHookAdapter {
	private final String curDir = System.getProperty("user.dir");
	private final String skeleton = curDir + File.separator + "plugins/WSL/resources/WSL_Skeleton.mm";
	public WSL_Skeleton() {
		super();
		}

	public void startupMapHook() {
		super.startupMapHook();
		
		File skeletonF = new File(skeleton);
		try {
			getController().load(skeletonF);
		} catch (XMLParseException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			freemind.main.Resources.getInstance().logException(e);
		} catch (IOException e) {
			freemind.main.Resources.getInstance().logException(e);
		}
	  }
	}