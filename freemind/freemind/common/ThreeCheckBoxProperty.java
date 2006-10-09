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
/* $Id: ThreeCheckBoxProperty.java,v 1.1.2.1.2.2 2006-10-09 05:44:33 christianfoltin Exp $ */
package freemind.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import com.jgoodies.forms.builder.DefaultFormBuilder;

import freemind.controller.BlindIcon;

public class ThreeCheckBoxProperty extends PropertyBean implements
        PropertyControl {
    static public final String FALSE_VALUE = "false";

    static public final String TRUE_VALUE = "true";

    public static final String DON_T_TOUCH_VALUE = "don_t_touch";

    private static final int DON_T_TOUCH_VALUE_INT = 2;

    private static final int TRUE_VALUE_INT = 0;

    private static final int FALSE_VALUE_INT = 1;

    //FIXME: Choose different icon.
    private static final ImageIcon PLUS_IMAGE = new ImageIcon(ClassLoader.getSystemResource("accessories/plugins/icons/edit_add.png"));

    //FIXME: Choose different icon.
    private static final ImageIcon MINUS_IMAGE = new ImageIcon(ClassLoader.getSystemResource("accessories/plugins/icons/edit_remove.png"));

    private static final Icon NO_IMAGE = new BlindIcon(15);

    String description;

    String label;

    int state = 0;

    JButton mButton = new JButton();

    /**
     */
    public ThreeCheckBoxProperty(String description, String label) {
        super();
        this.description = description;
        this.label = label;
        setState(0);
        mButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setState((getState() + 1) % 3);
                firePropertyChangeEvent();
            }

        });
    }

    private int getState() {
        return state;
    }

    public String getDescription() {
        return description;
    }

    public String getLabel() {
        return label;
    }

    public void setValue(String value) {
        if (value == null
                || !(value.toLowerCase().equals(TRUE_VALUE)
                        || value.toLowerCase().equals(FALSE_VALUE) || value
                        .toLowerCase().equals(DON_T_TOUCH_VALUE))) {
            throw new IllegalArgumentException("Cannot set a boolean to "
                    + value);
        }
        setState(transformString(value));
    }

    private int transformString(String string) {
        if (string == null) {
            return DON_T_TOUCH_VALUE_INT;
        }
        if (string.toLowerCase().equals(TRUE_VALUE)) {
            return TRUE_VALUE_INT;
        }
        if (string.toLowerCase().equals(FALSE_VALUE)) {
            return FALSE_VALUE_INT;
        }
        return DON_T_TOUCH_VALUE_INT;
    }

    public String getValue() {
        switch (state) {
        case TRUE_VALUE_INT:
            return TRUE_VALUE;
        case FALSE_VALUE_INT:
            return FALSE_VALUE;
        case DON_T_TOUCH_VALUE_INT:
            return DON_T_TOUCH_VALUE;
        }
        return null;
    }

    public void layout(DefaultFormBuilder builder, TextTranslator pTranslator) {
        JLabel label = builder.append(pTranslator.getText(getLabel()), mButton);
        label.setToolTipText(pTranslator.getText(getDescription()));
    }

    public void setEnabled(boolean pEnabled) {
        mButton.setEnabled(pEnabled);
    }

    /**
     * 
     */
    private void setState(int newState) {
        state = newState;
        Icon[] icons;
        icons = new Icon[3]; //{MINUS_IMAGE, PLUS_IMAGE, NO_IMAGE};
        icons[TRUE_VALUE_INT] = PLUS_IMAGE;
        icons[FALSE_VALUE_INT] = MINUS_IMAGE;
        icons[DON_T_TOUCH_VALUE_INT] = NO_IMAGE;
		//        mButton.setText(DISPLAY_VALUES[state]);
        mButton.setIcon(icons[state]);
    }

}
