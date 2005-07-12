/*
 * Created on 18.06.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.modes.attributes;
import freemind.main.XMLElement;
import freemind.modes.MindMapNode;

/**
 * @author Dimitri Polivaev
 * 18.06.2005
 */
public interface PersistentAttributeTableModel extends AttributeTableModel{
    Attribute getAttribute(int row);
    void save(XMLElement node);
}