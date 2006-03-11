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
/*$Id: StringProperty.java,v 1.1.4.2 2006-03-11 16:42:36 dpolivaev Exp $*/
package freemind.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.DefaultFormBuilder;

public class StringProperty extends PropertyBean implements
		PropertyControl {
	String description;

	String label;

	JTextField mTextField = new JTextField();
	/**
	 * @param description
	 * @param label
	 */
	public StringProperty(String description, String label) {
		super();
		this.description = description;
		this.label = label;
//        mTextField.addPropertyChangeListener(new PropertyChangeListener() {
//            public void propertyChange(PropertyChangeEvent pEvt)
//            {
//                firePropertyChangeEvent();
//            }
//        });
        mTextField.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent pE)
            {
                firePropertyChangeEvent();
            }});

	}

	public String getDescription() {
		return description;
	}

	public String getLabel() {
		return label;
	}

	public void setValue(String value) {
		mTextField.setText(value);
	}

	public String getValue() {
		return mTextField.getText();
	}

	public void layout(DefaultFormBuilder builder, TextTranslator pTranslator) {
		JLabel label = builder
				.append(pTranslator.getText(getLabel()), mTextField);
		label.setToolTipText(pTranslator.getText(getDescription()));
	}

}