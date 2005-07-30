/*
 * Created on 24.07.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.modes.attributes;

import java.util.EventObject;

/**
 * @author Dimitri Polivaev
 * 24.07.2005
 */
public class ColumnWidthChangeEvent extends EventObject {
    private final int col;
    /**
     * @param source
     * @param col
     */
    public ColumnWidthChangeEvent(Object source, int col) {
        super(source);
        this.col = col;
    }

    public int getColumnNumber() {
        return col;
    }
}
