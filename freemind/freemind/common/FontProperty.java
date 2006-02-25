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
/*$Id: FontProperty.java,v 1.1.2.1 2006-02-25 23:10:58 christianfoltin Exp $*/
package freemind.common;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;

import com.jgoodies.forms.builder.DefaultFormBuilder;

public class FontProperty extends JButton implements
		PropertyControl, PropertyBean, ActionListener {
	String description;

	String label;

	Font font;
	
	final JPopupMenu menu = new JPopupMenu();

    private final String defaultFont;

	private final TextTranslator mTranslator;


	/**
	 * @param description
	 * @param label
	 * @param pTranslator TODO
	 * @param defaultColor TODO
	 */
	public FontProperty(String description, String label, String defaultFont, TextTranslator pTranslator) {
		super();
		this.description = description;
		this.label = label;
        this.defaultFont = defaultFont;
		mTranslator = pTranslator;
		addActionListener(this);
	}

	public String getDescription() {
		return description;
	}

	public String getLabel() {
		return label;
	}

	public void setValue(String value) {
		setFontValue(new Font(value, 0, 12));
	}

	public String getValue() {
		return getFontValue().getFontName();
	}

	public void layout(DefaultFormBuilder builder, TextTranslator pTranslator) {
		JLabel label = builder
				.append(pTranslator.getText(getLabel()), this);
		label.setToolTipText(pTranslator.getText(getDescription()));
	    
	}

	public void actionPerformed(ActionEvent arg0) {
		JFontChooser dialog = new JFontChooser(null);
		dialog.setFont(getFontValue());
		dialog.showDialog();
		Font result = dialog.getFont();
		if (result != null) {
			setFontValue(result);
		}
	}

	/**
	 * @param result
	 */
	private void setFontValue(Font result) {
		font = result;
		setFont(result);
		setText(result.getFontName());
	}

	/**
	 * @return
	 */
	private Font getFontValue() {
		return font;
	}

}