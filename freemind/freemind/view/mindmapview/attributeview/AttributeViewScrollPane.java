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
    }
    public Dimension getPreferredSize() {
        Component table = getViewport();
        if(isValid() == false)
            validate();
        return super.getPreferredSize();
    }
}