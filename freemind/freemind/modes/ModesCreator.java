/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000  Joerg Mueller <joergmueller@bigfoot.com>
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

package freemind.modes;

import freemind.modes.mindmapmode.MindMapMode;
import freemind.modes.filemode.FileMode;
import freemind.controller.Controller;
import java.util.Map;
import java.util.TreeMap;


/**
 * This class creates all the modes that are available. To add your own mode,
 * simply import it, and create it in getAllModes() (just do the same whats done
 * with MindMapMode). Thats all!
 */
public class ModesCreator {
    private Controller c;
    private Map modes = new TreeMap();

    public ModesCreator(Controller c) {
	this.c = c;
    }

    public Map getAllModes() {

	//Copy these two lines for every new Mode,
	//and replace MindMapMode(c) with YourNewMode(c)

	Mode mode = new MindMapMode(c);
	modes.put(mode.toString(), mode);

	mode = new FileMode(c);
	modes.put(mode.toString(), mode);


	return modes;
    }
}
