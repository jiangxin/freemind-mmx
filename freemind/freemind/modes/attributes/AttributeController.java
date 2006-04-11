/*
 * Created on 22.01.2006
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.modes.attributes;


public interface AttributeController {

    void performSetValueAt(NodeAttributeTableModel model, Object o, int row, int col);

    void performInsertRow(NodeAttributeTableModel model, int index, String name, String value);

    void performRemoveRow(NodeAttributeTableModel model, int index);

    void performSetColumnWidth(NodeAttributeTableModel model, int col, int width);

    void performRemoveAttributeValue(String name, String value);

    void performReplaceAttributeValue(String name, String oldValue, String newValue);

    void performSetFontSize(AttributeRegistry registry, int size);

    void performSetVisibility(int i, boolean b);

    void performSetRestriction(int i, boolean b);

    void performReplaceAtributeName(String oldName, String newName);

    void performRemoveAttribute(String name);

    void performRegistryAttribute(String name);

    void performRegistryAttributeValue(String name, String value);

}
