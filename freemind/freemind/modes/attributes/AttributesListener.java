/*
 * Created on 26.02.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.modes.attributes;

import java.util.EventListener;

import javax.swing.event.ChangeEvent;

public interface AttributesListener extends EventListener{

    void attributesChanged(ChangeEvent changeEvent);

}
