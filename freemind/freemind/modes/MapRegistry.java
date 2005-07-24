/*
 * Created on 26.05.2005
 *
 */
package freemind.modes;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import freemind.controller.Controller;
import freemind.controller.filter.util.SortedMapListModel;
import freemind.modes.attributes.Attribute;
import freemind.modes.attributes.AttributeRegistryElement;
import freemind.modes.attributes.AttributeRegistryTableModel;
import freemind.modes.attributes.ConcreteAttributeTableModel;

/**
 * @author dimitri
 * 26.05.2005
 */
public class MapRegistry {
    private SortedMapListModel mapIcons;
    private AttributeRegistryTableModel attributes;
    private MindMap map;

    public MapRegistry(MindMap map) {
        super();
        this.map = map;
        mapIcons = new SortedMapListModel();
        attributes = new AttributeRegistryTableModel(this);
     }

    public void addIcon(MindIcon icon) {
        mapIcons.add(icon);
    }

    public void addAttribute(Attribute newAttribute) {
        attributes.registry(newAttribute);
    }
    /**
     * @return
     */
    public SortedMapListModel getIcons() {
        return mapIcons;
    }

    public AttributeRegistryTableModel getAttributes() {
        return attributes;
    }
    
    public void refresh(){
        attributes.clear();
        mapIcons.clear();
        MindMapNode root = (MindMapNode)map.getRoot();
        registrySubtree(root);
    }
    public void registrySubtree(MindMapNode root){
        registryNodeIcons(root);
        registryAttributes(root);
        ListIterator iterator = root.childrenUnfolded();
        while(iterator.hasNext()){
            MindMapNode node = (MindMapNode) iterator.next();
            registrySubtree(node);
        }
    }

    private void registryAttributes(MindMapNode node) {
        ConcreteAttributeTableModel model = node.getAttributes();
        for (int i = 0; i < model.getRowCount(); i++){
            attributes.registry(model.getAttribute(i));
        }
    }

    private void registryNodeIcons(MindMapNode node) {
        List icons = node.getIcons();
        Iterator i = icons.iterator();
        while(i.hasNext()){
            MindIcon icon = (MindIcon) i.next();
            addIcon(icon);
        }
    }

    /**
     * 
     */
    public void repaintMap() {
        MindMapNode root = (MindMapNode)map.getRoot();
        map.nodeChanged(root);
    }
}
