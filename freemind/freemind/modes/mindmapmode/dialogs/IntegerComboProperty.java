/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2013 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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

package freemind.modes.mindmapmode.dialogs;

import java.util.Iterator;
import java.util.Vector;

import freemind.common.ComboProperty;

/**
 * Adjusts string values of integers to the nearest integer as string.
 * 
 * @author foltin
 * @date 26.09.2013
 */
public class IntegerComboProperty extends ComboProperty {

	/**
	 * @param pDescription
	 * @param pLabel
	 * @param pPossibles
	 * @param pSizesVector
	 */
	public IntegerComboProperty(String pDescription, String pLabel,
			String[] pPossibles, Vector pSizesVector) {
		super(pDescription, pLabel, pPossibles, pSizesVector);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.common.ComboProperty#setValue(java.lang.String)
	 */
	public void setValue(String pValue) {
		String lastMatchedValue = null;
		if (possibleValues.contains(pValue)) {
			super.setValue(pValue);
			return;
		} else {
			int givenVal;
			try {
				givenVal = Integer.parseInt(pValue);
				for (Iterator it = possibleValues.iterator(); it.hasNext();) {
					String stringValue = (String) it.next();
					int val = Integer.parseInt(stringValue);
					if(val > givenVal && lastMatchedValue != null) {
						super.setValue(lastMatchedValue);
						return;
					}
					lastMatchedValue = stringValue;
				}
			} catch (NumberFormatException e) {
				freemind.main.Resources.getInstance().logException(e);
			}
		}
		super.setValue(pValue);
	}

}
