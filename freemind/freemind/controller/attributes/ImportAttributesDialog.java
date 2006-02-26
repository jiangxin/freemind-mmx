/*
 * Created on 11.12.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.controller.attributes;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

import freemind.controller.MapModuleManager;
import freemind.controller.filter.util.SortedComboBoxModel;
import freemind.main.Resources;
import freemind.modes.MindIcon;
import freemind.modes.MindMap;
import freemind.modes.attributes.Attribute;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.attributes.AttributeRegistryElement;
import freemind.view.MapModule;

class ImportAttributesDialog extends JDialog implements TreeSelectionListener {
    private JTree tree;
    private JScrollPane scrollPane;
    private DefaultMutableTreeNode topNode;
    private AttributeRegistry currentAttributes;
    static private class TreeNodeInfo{
        private String info;
        static private final int FULL_SELECTED = 0;
        static private final int PARTIAL_SELECTED = 1;
        static private final int NOT_SELECTED = 2;
        
        private int selected;
        
        public TreeNodeInfo(String info) {
            this.info = info;
            this.selected = NOT_SELECTED;
        }
        
        int getSelected() {
            return selected;
        }
        
        void setSelected(int selected) {
            this.selected = selected;
        }
        
        String getInfo() {
            return info;
        }
        
         public String toString(){
            return info;
        }
    }
    static private class AttributeTreeNodeInfo extends TreeNodeInfo{
        private boolean restricted;

        public AttributeTreeNodeInfo(String info, boolean restricted) {
            super(info);
            this.restricted = restricted;
        }
        boolean isRestricted() {
            return restricted;
        }

    }
    static private class MyRenderer extends DefaultTreeCellRenderer {
        static final Icon iconFull = MindIcon.factory("button_ok").getIcon(); 
        static final Icon iconPartial = MindIcon.factory("forward").getIcon(); 
        static final Icon iconNotSelected = MindIcon.factory("button_cancel").getIcon(); 
        public MyRenderer() {
        }
        
        public Component getTreeCellRendererComponent(
                JTree tree,
                Object value,
                boolean sel,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {
            
            super.getTreeCellRendererComponent(
                    tree, value, false,
                    expanded, leaf, row,
                    false);
            
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            TreeNodeInfo info = (TreeNodeInfo) node.getUserObject() ;
            switch(info.getSelected()){
            case TreeNodeInfo.FULL_SELECTED:
                setIcon(iconFull);
                break;
            case TreeNodeInfo.PARTIAL_SELECTED:
                setIcon(iconPartial);
                break;
            case TreeNodeInfo.NOT_SELECTED:
                setIcon(iconNotSelected);
                break;
            }
            return this;
        }
    }
    
    MyRenderer renderer = null;
    private DefaultTreeModel treeModel;
    private Component parentComponent;
    public ImportAttributesDialog(Component parentComponent){
        super(Resources.getInstance().getJFrame(), Resources.getInstance().getResourceString("attributes_import"), true);
        this.parentComponent = parentComponent;
        TreeNodeInfo nodeInfo = new TreeNodeInfo(Resources.getInstance().getResourceString("attribute_top"));
        topNode = new DefaultMutableTreeNode(nodeInfo);
        treeModel = new DefaultTreeModel(topNode);
        tree = new JTree(treeModel);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(this);
        
        scrollPane = new JScrollPane();        
        scrollPane.setViewportView(tree);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        
        Box buttons = Box.createHorizontalBox();
        buttons.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        JButton okBtn = new JButton(Resources.getInstance().getResourceString("ok"));
        okBtn.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                performImport(topNode);
                setVisible(false);                
            }
            
        });
        
        JButton cancelBtn = new JButton(Resources.getInstance().getResourceString("cancel"));
        cancelBtn.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                setVisible(false);                
            }
            
        });
        buttons.add(Box.createHorizontalGlue());
        buttons.add(okBtn);
        buttons.add(Box.createHorizontalGlue());
        buttons.add(cancelBtn);
        buttons.add(Box.createHorizontalGlue());
        
        getContentPane().add(buttons, BorderLayout.SOUTH);

    }
    
    private void performImport(DefaultMutableTreeNode node) {
        TreeNodeInfo info = (TreeNodeInfo) node.getUserObject();
        if(info.getSelected() == TreeNodeInfo.NOT_SELECTED){
            return;
        }
        String name = info.getInfo();
        boolean attributeNameRegistered = false;
        for(int i = 0; i < node.getChildCount(); i++){
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
            if(childNode.isLeaf()){
                if(attributeNameRegistered == false){
                    attributeNameRegistered = true;
                    if(-1 == currentAttributes.indexOf(name)){
                       currentAttributes.getAttributeController().performRegistryAttribute(name, null);
                       int index = currentAttributes.indexOf(name);
                       currentAttributes.getAttributeController().performSetRestriction(index, ((AttributeTreeNodeInfo)info).isRestricted());
                    }
                }
                TreeNodeInfo childInfo = (TreeNodeInfo) childNode.getUserObject();
                if(childInfo.getSelected() == TreeNodeInfo.FULL_SELECTED){
                    String value = childInfo.getInfo();
                    currentAttributes.getAttributeController().performRegistryAttribute(name, value);
                }                
            }
            else{
                performImport(childNode);
            }
        }
        
    }

    public void setVisible(boolean b) {
        if(b == isVisible()){
            return;
        }
        if(b){
            createMapSubTrees(topNode);
            if(topNode.getChildCount() == 0){
                JOptionPane.showMessageDialog(parentComponent, 
                        Resources.getInstance().getResourceString("atributes_no_import_candidates_found"),
                        getTitle(),
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            treeModel.reload();
            if(renderer == null){
                renderer = new MyRenderer();
            }
            tree.setCellRenderer(renderer);
            setLocationRelativeTo(parentComponent);
            pack();
        }
        super.setVisible(b);
    }
    
    private void createMapSubTrees(DefaultMutableTreeNode top) {
        top.removeAllChildren();
        TreeNodeInfo topInfo = (TreeNodeInfo) top.getUserObject();
        topInfo.setSelected(TreeNodeInfo.NOT_SELECTED);
        MapModuleManager mapModuleManager = Resources.getInstance().getFrame().getController().getMapModuleManager();
        MindMap currentMap = mapModuleManager.getMapModule().getModel();
        currentAttributes = currentMap.getRegistry().getAttributes();
        Iterator iterator = mapModuleManager.getMapModules().entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry)iterator.next();
            String nextmapName = entry.getKey().toString();
            MapModule nextMapModule = (MapModule) entry.getValue();
            MindMap nextMap = nextMapModule.getModel();
            if(nextMap == currentMap){
                continue;
            }
            TreeNodeInfo treeNodeInfo = new TreeNodeInfo(nextmapName);
            DefaultMutableTreeNode mapInfo = new DefaultMutableTreeNode(treeNodeInfo);
            createAttributeSubTrees(mapInfo, nextMap.getRegistry().getAttributes());
            if(mapInfo.getChildCount() != 0){
                top.add(mapInfo);
            }
        }
        
        
    }
    private void createAttributeSubTrees(DefaultMutableTreeNode mapInfo, AttributeRegistry attributes) {
        for(int i = 0; i < attributes.size();i++){
            AttributeRegistryElement element = attributes.getElement(i);
            TreeNodeInfo treeNodeInfo = new AttributeTreeNodeInfo(element.getKey().toString(), element.isRestricted());
            DefaultMutableTreeNode attributeInfo = new DefaultMutableTreeNode(treeNodeInfo);  
            createValueSubTrees(attributeInfo, element, currentAttributes);
            if(attributeInfo.getChildCount() != 0){
                mapInfo.add(attributeInfo);
            }
        }
    }
    private void createValueSubTrees(DefaultMutableTreeNode attributeInfo, AttributeRegistryElement element, AttributeRegistry currentAttributes) {
        String attributeName = element.getKey().toString();
        SortedComboBoxModel values = element.getValues();
        for(int i = 0; i < values.getSize(); i++){
            Object nextElement = values.getElementAt(i);
            if(! currentAttributes.exist(attributeName, nextElement)){
                TreeNodeInfo treeNodeInfo = new TreeNodeInfo(nextElement.toString());
                DefaultMutableTreeNode valueInfo = new DefaultMutableTreeNode(treeNodeInfo);  
                attributeInfo.add(valueInfo);
            }
        }
    }

    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
        if(selectedNode == null){
            return;
        }
        TreeNodeInfo info = (TreeNodeInfo) selectedNode.getUserObject();
        int newSelectionType;
        switch(info.getSelected()){
        case TreeNodeInfo.FULL_SELECTED:
            newSelectionType = TreeNodeInfo.NOT_SELECTED;
            break;
        default:
            newSelectionType = TreeNodeInfo.FULL_SELECTED;
            break;
        }
        setSelectionType(selectedNode, newSelectionType);
        setParentSelectionType(selectedNode, newSelectionType);
        tree.clearSelection() ;
    }

    private void setParentSelectionType(DefaultMutableTreeNode selectedNode, int newSelectionType) {
        TreeNode parentNode = selectedNode.getParent();
        if(parentNode == null){
            return;
        }
        DefaultMutableTreeNode defaultMutableParentNode = (DefaultMutableTreeNode)parentNode;
        TreeNodeInfo info = (TreeNodeInfo) (defaultMutableParentNode).getUserObject();
        if(newSelectionType == TreeNodeInfo.PARTIAL_SELECTED){
            if(info.getSelected() != TreeNodeInfo.PARTIAL_SELECTED){
                info.setSelected(TreeNodeInfo.PARTIAL_SELECTED);
                treeModel.nodeChanged(defaultMutableParentNode);
            }
            setParentSelectionType(defaultMutableParentNode, TreeNodeInfo.PARTIAL_SELECTED);
            return;
        }
       for(int i = 0; i < defaultMutableParentNode.getChildCount(); i++){
           TreeNodeInfo childInfo = (TreeNodeInfo) ((DefaultMutableTreeNode)defaultMutableParentNode.getChildAt(i)).getUserObject();
           if(childInfo.getSelected() != newSelectionType){
               if(info.getSelected() != TreeNodeInfo.PARTIAL_SELECTED){
                   info.setSelected(TreeNodeInfo.PARTIAL_SELECTED);
                   treeModel.nodeChanged(defaultMutableParentNode);
               }
               setParentSelectionType(defaultMutableParentNode, TreeNodeInfo.PARTIAL_SELECTED);
               return;
           }
       }
       if(info.getSelected() != newSelectionType){
           info.setSelected(newSelectionType);
           treeModel.nodeChanged(defaultMutableParentNode);
       }
       setParentSelectionType(defaultMutableParentNode, newSelectionType);
    }

    private void setSelectionType(TreeNode selectedNode, int newSelectionType) {
        TreeNodeInfo info = (TreeNodeInfo) ((DefaultMutableTreeNode)selectedNode).getUserObject();
        if(info.getSelected() != newSelectionType){
            info.setSelected(newSelectionType);
            treeModel.nodeChanged(selectedNode);
        }
        for(int i = 0; i < selectedNode.getChildCount(); i++){
            setSelectionType(selectedNode.getChildAt(i), newSelectionType);
        }
        
    }    
}
