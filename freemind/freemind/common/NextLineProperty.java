/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 *
 * Created on 25.02.2006
 */
/*$Id: NextLineProperty.java,v 1.1.2.2 2006/03/14 21:56:27 christianfoltin Exp $*/
package freemind.common;

import com.jgoodies.forms.builder.DefaultFormBuilder;

public class NextLineProperty implements PropertyControl {

	public NextLineProperty() {
		super();
	}

	public String getDescription() {
		return null;
	}

	public String getLabel() {
		return null;
	}

	public void layout(DefaultFormBuilder builder, TextTranslator pTranslator) {
		builder.nextLine();
	}

	public void setEnabled(boolean pEnabled) {

	}

}