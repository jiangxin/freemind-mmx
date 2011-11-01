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
/*$Id: IconProperty.java,v 1.1.2.1.2.4 2007/08/05 22:15:21 dpolivaev Exp $*/
package freemind.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;

import com.jgoodies.forms.builder.DefaultFormBuilder;

import freemind.main.FreeMindMain;
import freemind.modes.MindIcon;
import freemind.modes.common.dialogs.IconSelectionPopupDialog;

public class IconProperty extends PropertyBean implements PropertyControl,
		ActionListener {
	String description;

	String label;

	JButton mButton;

	private final FreeMindMain mFreeMindMain;

	/**
	 * Of IconInformation s.
	 */
	private final Vector mIcons;

	private MindIcon mActualIcon = null;

	public IconProperty(String description, String label, FreeMindMain frame,
			Vector icons) {
		super();
		this.description = description;
		this.label = label;
		this.mFreeMindMain = frame;
		this.mIcons = icons;
		mButton = new JButton();
		mButton.addActionListener(this);
	}

	public String getDescription() {
		return description;
	}

	public String getLabel() {
		return label;
	}

	public void setValue(String value) {
		for (Iterator iter = mIcons.iterator(); iter.hasNext();) {
			MindIcon icon = (MindIcon) iter.next();
			if (icon.getName().equals(value)) {
				mActualIcon = icon;
				setIcon(mActualIcon);
			}
		}
	}

	private void setIcon(MindIcon actualIcon) {
		mButton.setIcon(actualIcon.getIcon());
		mButton.setToolTipText(actualIcon.getDescription());
	}

	public String getValue() {
		return mActualIcon.getName();
	}

	public void layout(DefaultFormBuilder builder, TextTranslator pTranslator) {
		JLabel label = builder.append(pTranslator.getText(getLabel()), mButton);
		label.setToolTipText(pTranslator.getText(getDescription()));
	}

	public void actionPerformed(ActionEvent arg0) {
		Vector icons = new Vector();
		Vector descriptions = new Vector();
		for (Iterator iter = mIcons.iterator(); iter.hasNext();) {
			MindIcon icon = (MindIcon) iter.next();
			icons.add(icon);
			descriptions.add(icon.getDescription());
		}
		IconSelectionPopupDialog dialog = new IconSelectionPopupDialog(
				mFreeMindMain.getJFrame(), icons, mFreeMindMain);
		dialog.setLocationRelativeTo(mFreeMindMain.getJFrame());
		dialog.setModal(true);
		dialog.setVisible(true);
		int result = dialog.getResult();
		if (result >= 0) {
			MindIcon icon = (MindIcon) mIcons.get(result);
			setValue(icon.getName());
			firePropertyChangeEvent();
		}
	}

	public void setEnabled(boolean pEnabled) {
		mButton.setEnabled(pEnabled);
	}

}
