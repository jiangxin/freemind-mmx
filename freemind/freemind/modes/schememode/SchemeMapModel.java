/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2001  Joerg Mueller <joergmueller@bigfoot.com>
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
/*$Id: SchemeMapModel.java,v 1.5 2001-03-28 19:17:37 ponder Exp $*/

package freemind.modes.schememode;

import freemind.main.FreeMindMain;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.MindMapEdge;
import freemind.modes.MapAdapter;
import java.io.File;

public class SchemeMapModel extends MapAdapter {
    
    //
    // Constructors
    //

    public SchemeMapModel(FreeMindMain frame) {
	super(frame);
	setRoot(new SchemeNodeModel(getFrame()));
    }
    
    //
    // Other methods
    //
    public void save(File file) {
    }
    
    public void load(File file) {
    }

    /**
     * This method returns the scheme code that is represented by
     * this map as a plain string.
     */
    public String getCode() {
	return ((SchemeNodeModel)getRoot()).getCodeMathStyle();
    }
    
    public boolean isSaved() {
	return true;
    }

    public String toString() {
	return "Scheme";
    }
}
