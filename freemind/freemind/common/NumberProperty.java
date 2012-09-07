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
/*$Id: NumberProperty.java,v 1.1.2.3.2.5 2009/01/14 21:18:36 christianfoltin Exp $*/
package freemind.common;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jgoodies.forms.builder.DefaultFormBuilder;

public class NumberProperty extends PropertyBean implements PropertyControl {
	String description;
	// JSlider slider;
	String label;
	private JSpinner spinner;
	private final int min;
	private final int max;
	private final int step;

	/**
	     */
	public NumberProperty(String description, String label, int min, int max,
			int step) {
		this.min = min;
		this.max = max;
		this.step = step;
		// slider = new JSlider(JSlider.HORIZONTAL, 5, 1000, 100);
		spinner = new JSpinner(new SpinnerNumberModel(min, min, max, step));

		this.description = description;
		this.label = label;
		spinner.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent pE) {
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
		int intValue = min;
		try {
			int parsedIntValue = Integer.parseInt(value);
			intValue = parsedIntValue;
			int stepModul = (intValue - min) % step;
			if (intValue < min || intValue > max || (stepModul != 0)) {
				System.err.println("Actual value of property " + getLabel()
						+ " is not in the allowed range: " + value);
				intValue = min;
			}
		} catch (NumberFormatException e) {
			freemind.main.Resources.getInstance().logException(e);
		}
		spinner.setValue(new Integer(intValue));
	}

	public String getValue() {
		return spinner.getValue().toString();
	}
	
	public int getIntValue() {
		return ((Integer)(spinner.getValue())).intValue();
	}

	public void layout(DefaultFormBuilder builder, TextTranslator pTranslator) {
		// JLabel label = builder
		// .append(pTranslator.getText(getLabel()), slider);
		JLabel label = builder.append(pTranslator.getText(getLabel()), spinner);
		label.setToolTipText(pTranslator.getText(getDescription()));
	}

	public void setEnabled(boolean pEnabled) {
		spinner.setEnabled(pEnabled);
	}

}
