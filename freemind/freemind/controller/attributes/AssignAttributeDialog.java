/*
 * Created on 10.12.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.controller.attributes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.ListModel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import freemind.main.Resources;
import freemind.modes.ControllerAdapter;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.attributes.Attribute;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.attributes.AttributeRegistryElement;
import freemind.modes.attributes.AttributeTableLayoutModel;
import freemind.modes.attributes.NodeAttributeTableModel;
import freemind.modes.mindmapmode.MindMapController;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeView;
import freemind.view.mindmapview.attributeview.AttributeView;

public class AssignAttributeDialog extends JDialog implements ChangeListener{
    private static class ClonedComboBoxModel implements ComboBoxModel{
        private ListModel sharedListModel;
        private Object selectedItem;
        private ListDataListener l;
        private ListDataEvent ev = null; 
        public ClonedComboBoxModel(ListModel sharedListModel) {
            super();
            this.sharedListModel = sharedListModel;
        }
        
        public void addListDataListener(ListDataListener l) {
            sharedListModel.addListDataListener(l);
            this.l = l;
        }
        
        public Object getElementAt(int index) {
            return sharedListModel.getElementAt(index);
        }
        
        public int getSize() {
            return sharedListModel.getSize();
        }
        
        public void removeListDataListener(ListDataListener l) {
            sharedListModel.removeListDataListener(l);
            this.l = null;
        }
        
        public void setSelectedItem(Object anItem) {
            selectedItem = anItem;
            if(l != null){
                l.contentsChanged(getContentChangedEvent());                
            }
            
        }

        private ListDataEvent getContentChangedEvent() {
            if(ev == null){
                ev = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, -1, -1);                
            }
            return ev;
        }
        
        public Object getSelectedItem() {
            return selectedItem;
        }
    }
    
    private abstract class IteratingAction implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            try{
                if(selectedBtn.getModel().isSelected()){
                    LinkedList selecteds = mapView.getSelecteds();
                    ListIterator iterator = selecteds.listIterator();
                    while(iterator.hasNext()){
                        NodeView selectedNodeView = (NodeView) iterator.next();
                        performAction(selectedNodeView);
                    }
                    return;
                }
                int n = mapView.getComponentCount();
                for(int i = 0; i < n; i++){
                    Component component = mapView.getComponent(i);
                    if(component instanceof NodeView && component.isVisible()){
                        performAction((NodeView)component);
                    }
                }
            }
            catch(NullPointerException ex){
                
            }
        }
        private void performAction(NodeView selectedNodeView) {
            if(! selectedNodeView.isRoot() || ! skipRootBtn.isSelected())
            performAction(selectedNodeView.getModel());
        }
       abstract protected void performAction(MindMapNode model) ;
    }
    
    private  class ShowAction extends IteratingAction{
        protected void performAction(MindMapNode model) {
            NodeAttributeTableModel attributes = model.getAttributes();
            if(! attributes.getViewType().equals(AttributeTableLayoutModel.SHOW_EXTENDED)){
                attributes.setViewType(AttributeTableLayoutModel.SHOW_EXTENDED);
                model.getMap().nodeChanged(model);
            }
        }        
    }
    
    private  class HideAction extends IteratingAction{
        protected void performAction(MindMapNode model) {
            NodeAttributeTableModel attributes = model.getAttributes();
            if(! attributes.getViewType().equals(AttributeTableLayoutModel.SHOW_REDUCED)){
                attributes.setViewType(AttributeTableLayoutModel.SHOW_REDUCED);
                model.getMap().nodeChanged(model);
            }
        }        
    }
    
    private  class AddAction extends IteratingAction{
        private String name;
        private String value;

        protected void performAction(MindMapNode model) {
            NodeAttributeTableModel attributes = model.getAttributes();
            Attribute attribute = new Attribute(name, value);
            attributes.addRow(attribute);
       }

        public void actionPerformed(ActionEvent e) {
            name = attributeNames.getSelectedItem().toString();
            Object selectedItem = attributeValues.getSelectedItem();
            value =  selectedItem != null ? selectedItem.toString() : "";            
            super.actionPerformed(e);
            if(selectedItem == null){                
                selectedAttributeChanged(name, attributeValues);
            }
        }        
        
    }
    
    private  class DeleteAttributeAction extends IteratingAction{
        protected void performAction(MindMapNode model) {
            NodeAttributeTableModel attributes = model.getAttributes();
            String name = attributeNames.getSelectedItem().toString();
            for(int i = attributes.getRowCount()-1; i >= 0; i--){
                if(attributes.getAttribute(i).getName().equals(name)){
                    attributes.removeRow(i);
                }
            }
       }        
    }
    
    private  class DeleteValueAction extends IteratingAction{
        protected void performAction(MindMapNode model) {
            NodeAttributeTableModel attributes = model.getAttributes();
            String name = attributeNames.getSelectedItem().toString();
            String value = attributeValues.getSelectedItem().toString();
            for(int i = attributes.getRowCount()-1; i >= 0; i--){
                Attribute attribute = attributes.getAttribute(i);
                if(attribute.getName().equals(name)
                        && attribute.getValue().equals(value)){
                    attributes.removeRow(i);
                }
            }
       }        
    }

    private  class ReplaceValueAction extends IteratingAction{
        protected void performAction(MindMapNode model) {
            NodeAttributeTableModel attributes = model.getAttributes();
            String name = attributeNames.getSelectedItem().toString();
            String value = attributeValues.getSelectedItem().toString();
            String replacingName = replacingAttributeNames.getSelectedItem().toString();
            String replacingValue = replacingAttributeValues.getSelectedItem().toString();
            for(int i = attributes.getRowCount()-1; i >= 0; i--){
                Attribute attribute = attributes.getAttribute(i);
                if(attribute.getName().equals(name)
                        && attribute.getValue().equals(value)){
                    attributes.removeRow(i);
                    attributes.insertRow(i, new Attribute(replacingName, replacingValue));
                }
            }
       }        
    }
    
    private static final Dimension maxButtonDimension = new Dimension(1000, 1000);
    
    private MapView mapView;
    private JComboBox attributeNames;
    private JComboBox attributeValues;
    private JComboBox replacingAttributeNames;
    private JComboBox replacingAttributeValues;

    private JRadioButton selectedBtn;

    private JRadioButton visibleBtn;

    private JCheckBox skipRootBtn;
    public AssignAttributeDialog(MapView mapView){
        super(JOptionPane.getFrameForComponent(mapView), Resources.getInstance().getResourceString("attributes_assign_dialog"), false);
        
        Border actionBorder = new MatteBorder(2, 2, 2, 2, Color.BLACK);
        Border emptyBorder = new EmptyBorder(5, 5, 5, 5);
        Border btnBorder = new EmptyBorder(2, 2, 2, 2);
        
        selectedBtn = new JRadioButton(Resources.getInstance().getResourceString("attributes_for_selected"));
        selectedBtn.setSelected(true);
        visibleBtn = new JRadioButton(Resources.getInstance().getResourceString("attributes_for_visible"));
        ButtonGroup group = new ButtonGroup();
        group.add(selectedBtn);
        group.add(visibleBtn);
        
        skipRootBtn = new JCheckBox(Resources.getInstance().getResourceString("attributes_skip_root"));
        skipRootBtn.setSelected(true);
        JButton showBtn = new JButton(Resources.getInstance().getResourceString("attributes_show"));
        showBtn.addActionListener(new ShowAction());
        JButton hideBtn = new JButton(Resources.getInstance().getResourceString("attributes_hide"));
        hideBtn.addActionListener(new HideAction());
        
        Box selectionBox = Box.createHorizontalBox();
        selectionBox.setBorder(emptyBorder);
        selectionBox.add(Box.createHorizontalGlue());
        selectionBox.add(selectedBtn);
        selectionBox.add(Box.createHorizontalGlue());
        selectionBox.add(visibleBtn);
        selectionBox.add(Box.createHorizontalGlue());
        selectionBox.add(skipRootBtn);
        selectionBox.add(Box.createHorizontalGlue());
        selectionBox.add(showBtn);
        selectionBox.add(Box.createHorizontalGlue());
        selectionBox.add(hideBtn);
        selectionBox.add(Box.createHorizontalGlue());
        
        getContentPane().add(selectionBox, BorderLayout.NORTH);
        
        String pattern = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
        JLabel patternLabel = new JLabel(pattern);
        Dimension comboBoxMaximumSize = patternLabel.getPreferredSize();
        comboBoxMaximumSize.width += 4;
        comboBoxMaximumSize.height += 4;
        attributeNames = new JComboBox();        
        attributeNames.setMaximumSize(comboBoxMaximumSize);
        attributeNames.setPreferredSize(comboBoxMaximumSize);
        attributeNames.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
                selectedAttributeChanged(e.getItem(), attributeValues);
            }            
        });
        attributeValues = new JComboBox();
        attributeValues.setMaximumSize(comboBoxMaximumSize);
        attributeValues.setPreferredSize(comboBoxMaximumSize);
        replacingAttributeNames = new JComboBox();
        replacingAttributeNames.setMaximumSize(comboBoxMaximumSize);
        replacingAttributeNames.setPreferredSize(comboBoxMaximumSize);
        replacingAttributeNames.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
                selectedAttributeChanged(e.getItem(), replacingAttributeValues);
            }            
        });
        replacingAttributeValues = new JComboBox();
        replacingAttributeValues.setMaximumSize(comboBoxMaximumSize);
        replacingAttributeValues.setPreferredSize(comboBoxMaximumSize);
        
        JButton addBtn  = new JButton(Resources.getInstance().getResourceString("filter_add"));
        addBtn.addActionListener(new AddAction());
        addBtn.setMaximumSize(maxButtonDimension);
        JButton deleteAttributeBtn = new JButton(Resources.getInstance().getResourceString("attribute_delete"));
        deleteAttributeBtn.addActionListener(new DeleteAttributeAction());
        deleteAttributeBtn.setMaximumSize(maxButtonDimension);
        JButton deleteAttributeValueBtn = new JButton(Resources.getInstance().getResourceString("attribute_delete_value"));
        deleteAttributeValueBtn.addActionListener(new DeleteValueAction());
        deleteAttributeValueBtn.setMaximumSize(maxButtonDimension);
        JButton replaceBtn = new JButton(Resources.getInstance().getResourceString("attribute_replace"));
        replaceBtn.addActionListener(new ReplaceValueAction());
        replaceBtn.setMaximumSize(maxButtonDimension);
        
        
        Box addDeleteBtnBox = Box.createVerticalBox();
        addDeleteBtnBox.setBorder(btnBorder);
        addDeleteBtnBox.add(Box.createVerticalGlue());
        addDeleteBtnBox.add(addBtn);
        addDeleteBtnBox.add(deleteAttributeBtn);
        addDeleteBtnBox.add(deleteAttributeValueBtn);
        addDeleteBtnBox.add(Box.createVerticalGlue());
        
        Box addDeleteBox = Box.createHorizontalBox();
        addDeleteBox.setBorder(actionBorder);
        addDeleteBox.add(Box.createHorizontalGlue());
        addDeleteBox.add(addDeleteBtnBox);
        addDeleteBox.add(Box.createHorizontalStrut(5));
        addDeleteBox.add(attributeNames);
        addDeleteBox.add(Box.createHorizontalStrut(5));
        addDeleteBox.add(attributeValues);
        addDeleteBox.add(Box.createHorizontalStrut(5));
        
        Box outerReplaceBox = Box.createVerticalBox();
        outerReplaceBox.setBorder(actionBorder);
        
        Box replaceBox = Box.createHorizontalBox();
        replaceBox.setBorder(btnBorder);
        replaceBox.add(Box.createHorizontalGlue());
        replaceBox.add(replaceBtn);
        replaceBox.add(Box.createHorizontalStrut(5));
        replaceBox.add(replacingAttributeNames);
        replaceBox.add(Box.createHorizontalStrut(5));
        replaceBox.add(replacingAttributeValues);
        replaceBox.add(Box.createHorizontalStrut(5));
        
        outerReplaceBox.add(Box.createVerticalGlue());
        outerReplaceBox.add(replaceBox);
        outerReplaceBox.add(Box.createVerticalGlue());
        
        Box actionBox = Box.createVerticalBox();
        actionBox.add(Box.createVerticalGlue());
        actionBox.add(addDeleteBox);
        actionBox.add(Box.createVerticalStrut(5));
        actionBox.add(outerReplaceBox);
        actionBox.add(Box.createVerticalGlue());
        getContentPane().add(actionBox, BorderLayout.CENTER);
        
        JButton closeBtn = new JButton(Resources.getInstance().getResourceString("close"));
        closeBtn.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
               setVisible(false);
            }
            
        });
        
        Box bottomBox = Box.createHorizontalBox();
        bottomBox.setBorder(emptyBorder);
        bottomBox.add(Box.createHorizontalGlue());
        bottomBox.add(closeBtn);
        bottomBox.add(Box.createHorizontalGlue());
        
        getContentPane().add(bottomBox, BorderLayout.SOUTH);
        pack();
        comboBoxMaximumSize.width = 1000;
        attributeNames.setMaximumSize(comboBoxMaximumSize);
        attributeValues.setMaximumSize(comboBoxMaximumSize);
        replacingAttributeNames.setMaximumSize(comboBoxMaximumSize);
        replacingAttributeValues.setMaximumSize(comboBoxMaximumSize);
        mapChanged(mapView);
    }
    
    public void mapChanged(MapView currentMapView) {
        if(mapView != null){
            mapView.getModel().getRegistry().getAttributes().removeChangeListener(this);
        }
        mapView = currentMapView;        
        MindMap map = currentMapView.getModel();
        AttributeRegistry attributes = map.getRegistry().getAttributes();
        attributes.addChangeListener(this);
        stateChanged();
    }

    private void selectedAttributeChanged(Object selectedAttributeName, JComboBox values){
        AttributeRegistry attributes = mapView.getModel().getRegistry().getAttributes();
        AttributeRegistryElement element = attributes.getElement(selectedAttributeName.toString());
        ComboBoxModel selectedValues = element.getValues();
        Object firstValue = selectedValues.getElementAt(0);
        values.setModel(new ClonedComboBoxModel(selectedValues));
        values.setSelectedItem(firstValue);
        values.setEditable(! element.isRestricted());
    }

    public void stateChanged(ChangeEvent e) {  
        stateChanged();
    }
    
    private void stateChanged() {  
        AttributeRegistry attributes = mapView.getModel().getRegistry().getAttributes();
        ComboBoxModel names = attributes.getComboBoxModel();
        attributeNames.setModel(new ClonedComboBoxModel(names));
        attributeNames.setEditable(! attributes.isRestricted());
        replacingAttributeNames.setModel(new ClonedComboBoxModel(names));
        replacingAttributeNames.setEditable(! attributes.isRestricted());
        
        if(attributes.size()> 0){
            Object first =  names.getElementAt(0); 
            attributeNames.setSelectedItem(first);
            replacingAttributeNames.setSelectedItem(first);
            selectedAttributeChanged(attributeNames.getSelectedItem(), attributeValues);
            selectedAttributeChanged( replacingAttributeNames.getSelectedItem(), replacingAttributeValues);
        }
        else{
            attributeValues.setModel(new DefaultComboBoxModel());
            attributeValues.setEditable(false);
            replacingAttributeValues.setModel(new DefaultComboBoxModel());
            replacingAttributeValues.setEditable(false);
        }
    }
}