/*
 * Created on 09.04.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.preferences.layout;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;

public class VariableSizeCardLayout extends CardLayout {

    public VariableSizeCardLayout() {
        super();
    }

    public VariableSizeCardLayout(int hgap, int vgap) {
        super(hgap, vgap);
    }

    /**
     * Determines the preferred size of the container argument using
     * this card layout.
     * @param   parent the parent container in which to do the layout
     * @return  the preferred dimensions to lay out the subcomponents
     *                of the specified container
     * @see     java.awt.Container#getPreferredSize
     * @see     java.awt.CardLayout#minimumLayoutSize
     */
    public Dimension preferredLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int ncomponents = parent.getComponentCount();
            int w = 0;
            int h = 0;

            for (int i = 0 ; i < ncomponents ; i++) {
                Component comp = parent.getComponent(i);
                if(comp.isVisible()){
                    Dimension d = comp.getPreferredSize();
                    if (d.width > w) {
                        w = d.width;
                    }
                    if (d.height > h) {
                        h = d.height;
                    }
                }
            }
            return new Dimension(insets.left + insets.right + w + getHgap()*2,
                                 insets.top + insets.bottom + h + getVgap()*2);
        }
    }
}
