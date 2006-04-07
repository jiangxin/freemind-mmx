/*
 * Created on 26.05.2005
 *
 */
package freemind.modes;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import freemind.controller.filter.util.SortedMapListModel;
import freemind.main.XMLElement;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.attributes.NodeAttributeTableModel;

/**
 * @author dimitri
 * 26.05.2005
 */
public class MapRegistry {
    private SortedMapListModel mapIcons;
    private AttributeRegistry attributes;
    private MindMap map;
    private ModeController modeController;

    public MapRegistry(MindMap map, ModeController modeController) {
        super();
        this.map = map;
        this.modeController = modeController;
        mapIcons = new SortedMapListModel();
        attributes = new AttributeRegistry(this);
     }

    public void addIcon(MindIcon icon) {
        mapIcons.add(icon);
    }

    /**
     * @return
     */
    public SortedMapListModel getIcons() {
        return mapIcons;
    }

    public AttributeRegistry getAttributes() {
        return attributes;
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
        NodeAttributeTableModel model = node.getAttributes();
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

    public MindMap getMap() {
        return map;
    }
    
    public ModeController getModeController() {
        return modeController;
    }

    /**
     * @param fileout
     * @throws IOException
     */
    public void save(Writer fileout) throws IOException {
        getAttributes().save(fileout);        
    }
}
