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
/*$Id: ComboProperty.java,v 1.1.2.4 2006-03-14 21:56:27 christianfoltin Exp $*/
package freemind.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.jgoodies.forms.builder.DefaultFormBuilder;


public class ComboProperty extends PropertyBean implements
		PropertyControl {
	String description;

	String label;
	
	JComboBox mComboBox = new JComboBox();

	private Vector possibleValues;

	/**
	 * @param description
	 * @param label
	 * @param pTranslator TODO
	 */
	public ComboProperty(String description, String label,
			String[] possibles, TextTranslator pTranslator) {
		super();
		this.description = description;
		this.label = label;
		fillPossibleValues(possibles);
		Vector possibleTranslations = new Vector();
		for (Iterator i = possibleValues.iterator(); i.hasNext();) {
			String key = (String) i.next();
			possibleTranslations.add(pTranslator.getText(key));
		}
		mComboBox.setModel(new DefaultComboBoxModel(possibleTranslations));
		mComboBox.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent pE)
            {
                firePropertyChangeEvent();
            }});
	}

	public ComboProperty(String description, String label,
			String[] possibles, Vector possibleTranslations) {
		this.description = description;
		this.label = label;
		fillPossibleValues(possibles);
		mComboBox.setModel(new DefaultComboBoxModel(possibleTranslations));
	}

	/**
	 * @param possibles
	 */
	private void fillPossibleValues(String[] possibles) {
		this.possibleValues = new Vector();
		possibleValues.addAll(Arrays.asList(possibles));
	}

	public String getDescription() {
		return description;
	}

	public String getLabel() {
		return label;
	}

	public void setValue(String value) {
		if (possibleValues.contains(value)) {
			mComboBox.setSelectedIndex(possibleValues.indexOf(value));
		} else {
			System.err.println("Unknown value:" + value);
            if(mComboBox.getModel().getSize()>0) {
                mComboBox.setSelectedIndex(0);
            }
		}
	}

	public String getValue() {
		return (String) possibleValues.get(mComboBox.getSelectedIndex());
	}

	public void layout(DefaultFormBuilder builder, TextTranslator pTranslator) {
		JLabel label = builder
				.append(pTranslator.getText(getLabel()), mComboBox);
		label.setToolTipText(pTranslator.getText(getDescription()));
	}

    public void setEnabled(boolean pEnabled) {
        mComboBox.setEnabled(pEnabled);
    }

    
}