/*
 * Created on 06.11.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.controller.filter.condition;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * @author Dimitri Polivaev
 * 06.11.2005
 */
public class JCondition extends JPanel {

    /**
     * @param axis
     */
    public JCondition() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setOpaque(false);
    }

}
