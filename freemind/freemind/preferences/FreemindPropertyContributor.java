/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006  Christian Foltin.
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
 * Created on 26.03.2006
 */
/*$Id: FreemindPropertyContributor.java,v 1.1.2.1.2.1 2007/08/05 20:33:17 christianfoltin Exp $*/
package freemind.preferences;

import java.util.List;

import freemind.common.TextTranslator;

/**
 * Implement this interface to take part in the property dialog.
 * 
 * @author foltin
 * 
 */
public interface FreemindPropertyContributor {

	public List getControls(TextTranslator pTextTranslator);

}
