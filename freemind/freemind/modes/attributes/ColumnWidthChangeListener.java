/*
 * Created on 24.07.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.modes.attributes;

import java.util.EventListener;

import javax.swing.event.ChangeEvent;

/**
 * @author Dimitri Polivaev
 * 24.07.2005
 */
public interface ColumnWidthChangeListener extends EventListener {

    /**
     * @param changeEvent
     */
    void columnWidthChanged(ColumnWidthChangeEvent event);

}
