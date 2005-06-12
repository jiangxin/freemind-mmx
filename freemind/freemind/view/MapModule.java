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
/*$Id: MapModule.java,v 1.4.18.1.2.1.2.1 2005-06-12 12:59:55 dpolivaev Exp $*/

package freemind.view;

import freemind.view.mindmapview.MapView;
import freemind.modes.MindMap;
import freemind.modes.Mode;

/**
 * This class is the key to one Model/View bundle
 * which represents one map.
 */
public class MapModule {
    private String name;
    private MindMap model;
    private MapView view;
    private Mode mode;
    private static int unnamedMapsNumber = 1;//used to give unique names to maps

    public MapModule(MindMap model, MapView view, Mode mode) {
	this.model = model;
	this.view = view;
	this.mode = mode;
    }

    /**
     * Returns the String that is used to identify this map.
     * Important: If the String is changed, other component (ie Controller)
     * must be notified.
     */
    public String toString() {
	if (name == null) {
	    rename();
	}
	return name;
    }

    public void rename() {
	if (getModel().toString() != null) {
	    name = getModel().toString();
	} else {
	    name = mode.getController().getFrame().getResourceString("mindmap")
		       +unnamedMapsNumber++;
	}
    }

    public MindMap getModel() {
	return model;
    }

    public MapView getView() {
	return view;
    }

    public Mode getMode() {
	return mode;
    }

    public void setName(String name) {
	this.name = name;
    }

    public void setModel(MindMap model) {
	this.model = model;
    }

    public void setView(MapView view) {
	this.view = view;
    }
}
