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
/*$Id: MainToolBar.java,v 1.16.14.2.4.8 2009/07/04 20:38:27 christianfoltin Exp $*/

package freemind.controller;

import java.awt.Insets;
import java.util.logging.Logger;

public class MainToolBar extends FreeMindToolBar {
	Controller controller;
	private static Logger logger = null;

	public MainToolBar(final Controller controller) {
		super();
		this.controller = controller;
		if (logger == null) {
			logger = controller.getFrame().getLogger(this.getClass().getName());
		}
		setRollover(true);
		setBorderPainted(false);
		setMargin(new Insets(0, 0, 0, 0));
	}

	public void activate(boolean visible) {
	}


	public void setAllActions(boolean enabled) {
	}

}
