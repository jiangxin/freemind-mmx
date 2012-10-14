/*
 * FreeMind - A Program for creating and viewing Mindmaps Copyright (C)
 * 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 * 
 * See COPYING for Details
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * Created on 25.02.2006
 */

package freemind.common;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import com.jgoodies.forms.builder.DefaultFormBuilder;

public class BooleanProperty extends PropertyBean implements PropertyControl {
	static public final String FALSE_VALUE = "false";

	static public final String TRUE_VALUE = "true";

	protected String mFalseValue = FALSE_VALUE;

	protected String mTrueValue = TRUE_VALUE;

	String description;

	String label;

	JCheckBox mCheckBox = new JCheckBox();

	/**
     */
	public BooleanProperty(String description, String label) {
		super();
		this.description = description;
		this.label = label;
		mCheckBox.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent pE) {
				firePropertyChangeEvent();
			}
		});
	}

	public String getDescription() {
		return description;
	}

	public String getLabel() {
		return label;
	}

	public void setValue(String value) {
		if (value == null
				|| !(value.toLowerCase().equals(mTrueValue) || value
						.toLowerCase().equals(mFalseValue))) {
			throw new IllegalArgumentException("Cannot set a boolean to '"
					+ value + "', allowed are " + mTrueValue + " and "
					+ mFalseValue + ".");
		}
		mCheckBox.setSelected(value.toLowerCase().equals(mTrueValue));
	}

	public String getValue() {
		return mCheckBox.isSelected() ? mTrueValue : mFalseValue;
	}

	public void layout(DefaultFormBuilder builder, TextTranslator pTranslator) {
		JLabel label = builder.append(pTranslator.getText(getLabel()),
				mCheckBox);
		label.setToolTipText(pTranslator.getText(getDescription()));
	}

	public void setEnabled(boolean pEnabled) {
		mCheckBox.setEnabled(pEnabled);
	}

}
