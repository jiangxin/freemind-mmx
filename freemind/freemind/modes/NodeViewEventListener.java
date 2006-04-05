/*
 * Created on 31.10.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.modes;

import java.util.EventListener;

/**
 * @author Dimitri Polivaev
 * 31.10.2005
 */
public interface NodeViewEventListener extends EventListener{

    /**
     * @param nodeViewEvent
     */
    public void nodeViewCreated(NodeViewEvent nodeViewEvent);
    /**
     * @param nodeViewEvent
     */
    public void nodeViewRemoved(NodeViewEvent nodeViewEvent);
    
}
