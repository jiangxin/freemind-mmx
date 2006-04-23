/*
 * Created on 17.09.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.view.mindmapview.attributeview;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JScrollPane;


class AttributeViewScrollPane extends JScrollPane{
    
     
    /**
     * @param attributeTable
     */
    AttributeViewScrollPane(AttributeTable attributeTable) {
        super(attributeTable);
        setAlignmentX(CENTER_ALIGNMENT);
    }
    public Dimension getPreferredSize() {
        validate();
        return super.getPreferredSize();
    }
    public Dimension getMaximumSize() {
        validate();
        return super.getPreferredSize();
    }
    public boolean isVisible(){
        return getViewport().getView().isVisible();
    }
}