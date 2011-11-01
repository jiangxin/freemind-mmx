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
/*$Id: PermanentNodeHookSubstituteUnknown.java,v 1.1.2.1 2005/02/13 22:39:56 christianfoltin Exp $*/

package freemind.extensions;

import java.util.Iterator;

import freemind.main.XMLElement;

/**
 * Class saves xml data in case that the corresponding node hook is not present.
 */
public class PermanentNodeHookSubstituteUnknown extends
		PermanentNodeHookAdapter {

	private final String hookName;

	public PermanentNodeHookSubstituteUnknown(String name) {
		super();
		hookName = name;
	}

	private XMLElement child;

	public void loadFrom(XMLElement child) {
		this.child = child;
		super.loadFrom(child);

	}

	public void save(XMLElement xml) {
		super.save(xml);
		for (Iterator i = child.getChildren().iterator(); i.hasNext();) {
			XMLElement childchild = (XMLElement) i.next();
			xml.addChild(childchild);
		}
	}

	public String getName() {
		return hookName;
	}

}
