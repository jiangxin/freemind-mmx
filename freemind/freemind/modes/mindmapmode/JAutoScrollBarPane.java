/*
 * Created on 15.11.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.modes.mindmapmode;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

/**
 * @author Dimitri Polivaev
 * 15.11.2005
 */
public class JAutoScrollBarPane extends JScrollPane{

    /**
     * @param view
     */
    public JAutoScrollBarPane(Component view) {
        super(view, VERTICAL_SCROLLBAR_NEVER , HORIZONTAL_SCROLLBAR_NEVER);
    }
    
    public JAutoScrollBarPane() {
        super(VERTICAL_SCROLLBAR_NEVER , HORIZONTAL_SCROLLBAR_NEVER);
    }


    public void doLayout() {
        super.doLayout();
    	Insets insets = getInsets();
    	int insetWidth = insets.left + insets.right;
    	int insetHeight = insets.top + insets.bottom;
        Dimension prefSize = getViewport().getPreferredSize();
        int width = getWidth() - insetWidth;
        if(getVerticalScrollBar().isVisible()){
            width -= getVerticalScrollBar().getWidth();
        }
        int height = getHeight() - insetHeight;
        if(getHorizontalScrollBar().isVisible()){
            height -= getHorizontalScrollBar().getHeight();
        }
        boolean isVsbNeeded = height < prefSize.height;
        boolean isHsbNeeded = width < prefSize.width;
        boolean layoutAgain = false;
        
        if(isVsbNeeded && getVerticalScrollBarPolicy() == VERTICAL_SCROLLBAR_NEVER){
            setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
            layoutAgain = true;
        }
        else if(! isVsbNeeded && getVerticalScrollBarPolicy() == VERTICAL_SCROLLBAR_ALWAYS){
            setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_NEVER);
            layoutAgain = true;
        }
        
        if(isHsbNeeded && getHorizontalScrollBarPolicy() == HORIZONTAL_SCROLLBAR_NEVER){
            setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_ALWAYS);
            layoutAgain = true;
        }
        else if(! isHsbNeeded && getHorizontalScrollBarPolicy() == HORIZONTAL_SCROLLBAR_ALWAYS){
            setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
            layoutAgain = true;
        }
        if(layoutAgain){
            getParent().doLayout();
        }            
    }
}
