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
/*$Id: FontProperty.java,v 1.1.2.4.2.2 2007/06/27 07:03:57 dpolivaev Exp $*/
package freemind.common;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.jgoodies.forms.builder.DefaultFormBuilder;

public class FontProperty extends PropertyBean implements PropertyControl {
	String description;

	String label;

	Font font = null;

	private final TextTranslator mTranslator;

	JComboBox mFontComboBox = new JComboBox();

	private String[] mAvailableFontFamilyNames;

	/**
	 * TODO TODO
	 */
	public FontProperty(String description, String label,
			TextTranslator pTranslator) {
		super();
		this.description = description;
		this.label = label;
		mTranslator = pTranslator;
		mAvailableFontFamilyNames = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		mFontComboBox.setModel(new DefaultComboBoxModel(
				mAvailableFontFamilyNames));
		mFontComboBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent pE) {
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

	public void layout(DefaultFormBuilder builder, TextTranslator pTranslator) {
		JLabel label = builder.append(pTranslator.getText(getLabel()),
				mFontComboBox);
		label.setToolTipText(pTranslator.getText(getDescription()));

	}

	public void setValue(String pValue) {
		for (int i = 0; i < mAvailableFontFamilyNames.length; i++) {
			String fontName = mAvailableFontFamilyNames[i];
			if (fontName.equals(pValue)) {
				mFontComboBox.setSelectedIndex(i);
				return;
			}
		}
		System.err.println("Unknown value:" + pValue);
		if (mFontComboBox.getModel().getSize() > 0) {
			mFontComboBox.setSelectedIndex(0);
		}
	}

	public String getValue() {
		return mAvailableFontFamilyNames[mFontComboBox.getSelectedIndex()];
	}

	public void setEnabled(boolean pEnabled) {
		mFontComboBox.setEnabled(pEnabled);
	}

}
