/*
 * Created on 18.06.2005
 * Copyright (C) 2005 Dimitri Polivaev
 *
 */
package freemind.controller.filter.util;

import javax.swing.ListModel;

/**
 * @author Dimitri Polivaev
 * 18.06.2005
 */
public interface SortedListModel extends ListModel{

    /**
     * 
     */
    void clear();

    /**
     * @param o
     * @return
     */
    boolean contains(Object o);

    /**
     * @param o
     */
    void add(Object o);
    
    void replace(Object oldO, Object newO);

    void remove(Object o);
    
    /**
     * @param o
     * @return
     */
    int getIndexOf(Object o);
}