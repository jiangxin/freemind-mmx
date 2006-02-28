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
/*$Id: FontProperty.java,v 1.1.2.3 2006-02-28 18:56:50 christianfoltin Exp $*/
package freemind.common;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;

import com.jgoodies.forms.builder.DefaultFormBuilder;

public class FontProperty extends PropertyBean implements PropertyControl, ActionListener {
	String description;

	String label;

	Font font = null;

	private final TextTranslator mTranslator;

	JButton mButton = new JButton();

	/**
	 * @param description
	 * @param label
	 * @param pTranslator
	 *            TODO
	 * @param defaultColor
	 *            TODO
	 */
	public FontProperty(String description, String label,
			TextTranslator pTranslator) {
		super();
		this.description = description;
		this.label = label;
		mTranslator = pTranslator;
		mButton.addActionListener(this);
	}

	public String getDescription() {
		return description;
	}

	public String getLabel() {
		return label;
	}

	public void layout(DefaultFormBuilder builder, TextTranslator pTranslator) {
		JLabel label = builder.append(pTranslator.getText(getLabel()), mButton);
		label.setToolTipText(pTranslator.getText(getDescription()));

	}

	public void actionPerformed(ActionEvent arg0) {
		JFontChooser dialog = new JFontChooser(null);
		if (getFontValue() != null) {
			dialog.setFont(getFontValue());
		}
		dialog.showDialog();
		Font result = dialog.getFont();
		if (result != null) {
			setFontValue(result);
			firePropertyChangeEvent();
		}
	}

	/**
	 * @param result
	 */
	public void setFontValue(Font result) {
		font = result;
		if (font != null) {
			mButton.setFont(result);
			mButton.setText(result.getFontName());
		} else {
			mButton.setText(mTranslator.getText("undefined_font"));
		}
	}

	/**
	 * @return
	 */
	public Font getFontValue() {
		return font;
	}

    public void setValue(String pValue)
    {
        throw new IllegalArgumentException("Not implemented due to purpose.");
    }

    public String getValue()
    {
        return (font!= null)?font.getFamily():null;
    }

}