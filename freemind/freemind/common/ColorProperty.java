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
/*$Id: ColorProperty.java,v 1.1.2.4.2.2 2008/07/24 03:10:36 christianfoltin Exp $*/
package freemind.common;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.jgoodies.forms.builder.DefaultFormBuilder;

import freemind.controller.Controller;
import freemind.main.Tools;

public class ColorProperty extends PropertyBean implements PropertyControl,
		ActionListener {
	String description;

	String label;

	Color color;

	JButton mButton;
	final JPopupMenu menu = new JPopupMenu();

	private final String defaultColor;

	private final TextTranslator mTranslator;

	/**
	 * @param defaultColor
	 *            TODO
	 * @param pTranslator
	 *            TODO
	 */
	public ColorProperty(String description, String label, String defaultColor,
			TextTranslator pTranslator) {
		super();
		this.description = description;
		this.label = label;
		this.defaultColor = defaultColor;
		mTranslator = pTranslator;
		mButton = new JButton();
		mButton.addActionListener(this);
		color = Color.BLACK;
	}

	public String getDescription() {
		return description;
	}

	public String getLabel() {
		return label;
	}

	public void setValue(String value) {
		setColorValue(Tools.xmlToColor(value));
	}

	public String getValue() {
		return Tools.colorToXml(getColorValue());
	}

	public void layout(DefaultFormBuilder builder, TextTranslator pTranslator) {
		JLabel label = builder.append(pTranslator.getText(getLabel()), mButton);
		label.setToolTipText(pTranslator.getText(getDescription()));
		// add "reset to standard" popup:

		// Create and add a menu item
		JMenuItem item = new JMenuItem(
				mTranslator.getText("ColorProperty.ResetColor"));
		item.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				setValue(defaultColor);
			}
		});
		menu.add(item);

		// Set the component to show the popup menu
		mButton.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				if (evt.isPopupTrigger()) {
					menu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}

			public void mouseReleased(MouseEvent evt) {
				if (evt.isPopupTrigger()) {
					menu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
		});
	}

	public void actionPerformed(ActionEvent arg0) {
		Color result = Controller.showCommonJColorChooserDialog(
				mButton.getRootPane(), mTranslator.getText(getLabel()),
				getColorValue());
		if (result != null) {
			setColorValue(result);
			firePropertyChangeEvent();
		}
	}

	/**
	 */
	private void setColorValue(Color result) {
		color = result;
		if (result == null) {
			result = Color.WHITE;
		}
		mButton.setBackground(result);
		mButton.setText(Tools.colorToXml(result));
	}

	/**
	 */
	private Color getColorValue() {
		return color;
	}

	public void setEnabled(boolean pEnabled) {
		mButton.setEnabled(pEnabled);
	}

}
