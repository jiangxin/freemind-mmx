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
/*$Id: ComboProperty.java,v 1.1.2.5.2.2 2006/07/25 20:28:19 christianfoltin Exp $*/
package freemind.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.jgoodies.forms.builder.DefaultFormBuilder;

public class ComboProperty extends PropertyBean implements PropertyControl {
	String description;

	String label;

	protected JComboBox mComboBox = new JComboBox();

	protected Vector possibleValues;

	/**
	 * @param pTranslator
	 *            TODO
	 */
	public ComboProperty(String description, String label, String[] possibles,
			TextTranslator pTranslator) {
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
		mComboBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent pE) {
				firePropertyChangeEvent();
			}
		});
	}

	public ComboProperty(String description, String label, String[] possibles,
			List possibleTranslations) {
		this.description = description;
		this.label = label;
		fillPossibleValues(possibles);
		mComboBox.setModel(new DefaultComboBoxModel(new Vector(
				possibleTranslations)));
	}

	public ComboProperty(String description, String label, List possibles,
			List possibleTranslations) {
		this.description = description;
		this.label = label;
		fillPossibleValues(possibles);
		mComboBox.setModel(new DefaultComboBoxModel(new Vector(
				possibleTranslations)));
	}

	/**
	 */
	private void fillPossibleValues(String[] possibles) {
		fillPossibleValues(Arrays.asList(possibles));
	}

	/**
	 */
	private void fillPossibleValues(List possibles) {
		this.possibleValues = new Vector();
		possibleValues.addAll(possibles);
	}

	/**
	 * If your combo base changes, call this method to update the values. The
	 * old selected value is not selected, but the first in the list. Thus, you
	 * should call this method only shortly before setting the value with
	 * setValue.
	 */
	public void updateComboBoxEntries(List possibles, List possibleTranslations) {
		mComboBox.setModel(new DefaultComboBoxModel(new Vector(
				possibleTranslations)));
		fillPossibleValues(possibles);
		if (possibles.size() > 0) {
			mComboBox.setSelectedIndex(0);
		}
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
			System.err.println("Can't set the value:" + value
					+ " into the combo box " + getLabel() + "/"
					+ getDescription());
			if (mComboBox.getModel().getSize() > 0) {
				mComboBox.setSelectedIndex(0);
			}
		}
	}

	public String getValue() {
		return (String) possibleValues.get(mComboBox.getSelectedIndex());
	}

	public void layout(DefaultFormBuilder builder, TextTranslator pTranslator) {
		JLabel label = builder.append(pTranslator.getText(getLabel()),
				mComboBox);
		label.setToolTipText(pTranslator.getText(getDescription()));
	}

	public void setEnabled(boolean pEnabled) {
		mComboBox.setEnabled(pEnabled);
	}

}
