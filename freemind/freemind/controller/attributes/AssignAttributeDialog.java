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
import java.util.NoSuchElementException;

import javax.swing.AbstractListModel;
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
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import freemind.main.Resources;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.attributes.Attribute;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.attributes.AttributeRegistryElement;
import freemind.modes.attributes.AttributeTableLayoutModel;
import freemind.modes.attributes.AttributesListener;
import freemind.modes.attributes.NodeAttributeTableModel;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeView;

public class AssignAttributeDialog extends JDialog implements AttributesListener{
    private static class ClonedComboBoxModel extends AbstractListModel implements ComboBoxModel{
        private AbstractListModel sharedListModel;
        private Object selectedItem;
        private ListDataEvent ev = null; 
        public ClonedComboBoxModel(ComboBoxModel sharedListModel) {
            super();
            this.sharedListModel = (AbstractListModel)sharedListModel;
        }
        
        public void addListDataListener(ListDataListener l) {
            super.addListDataListener(l);
            sharedListModel.addListDataListener(l);
        }
        
        public Object getElementAt(int index) {
            return sharedListModel.getElementAt(index);
        }
        
        public int getSize() {
            return sharedListModel.getSize();
        }
        
        public void removeListDataListener(ListDataListener l) {
            super.removeListDataListener(l);
            sharedListModel.removeListDataListener(l);
        }
        
        public void setSelectedItem(Object anItem) {
            selectedItem = anItem;
            fireContentsChanged(anItem, -1, -1);
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
       
       protected void showEmptyStringErrorMessage() {
           JOptionPane.showMessageDialog(AssignAttributeDialog.this, 
                   Resources.getInstance().getResourceString("attributes_adding_empty_attribute_error"), 
                   Resources.getInstance().getResourceString("error"), JOptionPane.ERROR_MESSAGE);
       }        
    }
    
    private  class AddAction extends IteratingAction{
        private String name;
        private String value;

        protected void performAction(MindMapNode model) {
            NodeAttributeTableModel attributes = model.getAttributes();                         
            attributes.getAttributeController().performInsertRow(attributes, attributes.getRowCount(), name, value);
       }

        public void actionPerformed(ActionEvent e) {
            if(attributeNames.getSelectedItem() == null){
                showEmptyStringErrorMessage();
                return;
            }
            
            name = attributeNames.getSelectedItem().toString();
            if(name.equals("")){
                showEmptyStringErrorMessage();
                return;
            }
            Object valueSelectedItem = attributeValues.getSelectedItem();
            value =  valueSelectedItem != null ? valueSelectedItem.toString() : "";
            
            super.actionPerformed(e);
            if(valueSelectedItem == null){                
                selectedAttributeChanged(name, attributeValues);
            }
        }

        
    }
    
    private  class DeleteAttributeAction extends IteratingAction{
        private String name;
        public void actionPerformed(ActionEvent e) {
            final Object selectedItem = attributeNames.getSelectedItem();
            if(selectedItem == null){
                showEmptyStringErrorMessage();
                return;
            }
            name = selectedItem.toString();
            if(name.equals("")){
                showEmptyStringErrorMessage();
                return;
            }
            super.actionPerformed(e);
        }        
        protected void performAction(MindMapNode model) {
            NodeAttributeTableModel attributes = model.getAttributes();
            for(int i = attributes.getRowCount()-1; i >= 0; i--){
                if(attributes.getAttribute(i).getName().equals(name)){
                    attributes.getAttributeController().performRemoveRow(attributes, i);
                }
            }
       }        
    }
    
    private  class DeleteValueAction extends IteratingAction{
        private String name;
        private String value;
        public void actionPerformed(ActionEvent e) {
            if(attributeNames.getSelectedItem() == null){
                showEmptyStringErrorMessage();
                return;
            }
            
            name = attributeNames.getSelectedItem().toString();
            if(name.equals("")){
                showEmptyStringErrorMessage();
                return;
            }
            Object valueSelectedItem = attributeValues.getSelectedItem();
            value =  valueSelectedItem != null ? valueSelectedItem.toString() : "";
            
            super.actionPerformed(e);
        }        
        protected void performAction(MindMapNode model) {
            NodeAttributeTableModel attributes = model.getAttributes();
            for(int i = attributes.getRowCount()-1; i >= 0; i--){
                Attribute attribute = attributes.getAttribute(i);
                if(attribute.getName().equals(name)
                        && attribute.getValue().equals(value)){
                    attributes.getAttributeController().performRemoveRow(attributes, i);
                }
            }
       }        
    }

    private  class ReplaceValueAction extends IteratingAction{
        private String name;
        private String value;
        private String replacingName;
        private String replacingValue;
        public void actionPerformed(ActionEvent e) {
            if(attributeNames.getSelectedItem() == null){
                showEmptyStringErrorMessage();
                return;
            }
            if(replacingAttributeNames.getSelectedItem() == null){
                showEmptyStringErrorMessage();
                return;
            }
            name = attributeNames.getSelectedItem().toString();
            if(name.equals("")){
                showEmptyStringErrorMessage();
                return;
            }
            replacingName = replacingAttributeNames.getSelectedItem().toString();
            if(replacingName.equals("")){
                showEmptyStringErrorMessage();
                return;
            }
            Object valueSelectedItem = attributeValues.getSelectedItem();
            value =  valueSelectedItem != null ? valueSelectedItem.toString() : "";            
            Object replacingValueSelectedItem = replacingAttributeValues.getSelectedItem();
            replacingValue = replacingValueSelectedItem != null ? replacingValueSelectedItem.toString() :"";
            super.actionPerformed(e);
        }        
        protected void performAction(MindMapNode model) {
            NodeAttributeTableModel attributes = model.getAttributes();
            for(int i = attributes.getRowCount()-1; i >= 0; i--){
                Attribute attribute = attributes.getAttribute(i);
                if(attribute.getName().equals(name)
                        && attribute.getValue().equals(value)){
                    attributes.getAttributeController().performRemoveRow(attributes, i);
                    attributes.insertRow(i, replacingName, replacingValue);
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
        
        final Border actionBorder = new MatteBorder(2, 2, 2, 2, Color.BLACK);
        final Border emptyBorder = new EmptyBorder(5, 5, 5, 5);
        final Border btnBorder = new EmptyBorder(2, 2, 2, 2);
        
        selectedBtn = new JRadioButton(Resources.getInstance().getResourceString("attributes_for_selected"));
        selectedBtn.setSelected(true);
        visibleBtn = new JRadioButton(Resources.getInstance().getResourceString("attributes_for_visible"));
        final ButtonGroup group = new ButtonGroup();
        group.add(selectedBtn);
        group.add(visibleBtn);
        
        skipRootBtn = new JCheckBox(Resources.getInstance().getResourceString("attributes_skip_root"));
        skipRootBtn.setSelected(true);
        
        final Box selectionBox = Box.createHorizontalBox();
        selectionBox.setBorder(emptyBorder);
        selectionBox.add(Box.createHorizontalGlue());
        selectionBox.add(selectedBtn);
        selectionBox.add(Box.createHorizontalGlue());
        selectionBox.add(visibleBtn);
        selectionBox.add(Box.createHorizontalGlue());
        selectionBox.add(skipRootBtn);
        selectionBox.add(Box.createHorizontalGlue());
        
        getContentPane().add(selectionBox, BorderLayout.NORTH);
        
        final JButton addBtn = new JButton(Resources.getInstance().getResourceString("filter_add"));
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
        

        final String pattern = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
        final JLabel patternLabel = new JLabel(pattern);
        final Dimension comboBoxMaximumSize = patternLabel.getPreferredSize();
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
        
        
        final Box addDeleteBtnBox = Box.createVerticalBox();
        addDeleteBtnBox.setBorder(btnBorder);
        addDeleteBtnBox.add(Box.createVerticalGlue());
        addDeleteBtnBox.add(addBtn);
        addDeleteBtnBox.add(deleteAttributeBtn);
        addDeleteBtnBox.add(deleteAttributeValueBtn);
        addDeleteBtnBox.add(Box.createVerticalGlue());
        
        final Box addDeleteBox = Box.createHorizontalBox();
        addDeleteBox.setBorder(actionBorder);
        addDeleteBox.add(Box.createHorizontalGlue());
        addDeleteBox.add(addDeleteBtnBox);
        addDeleteBox.add(Box.createHorizontalStrut(5));
        addDeleteBox.add(attributeNames);
        addDeleteBox.add(Box.createHorizontalStrut(5));
        addDeleteBox.add(attributeValues);
        addDeleteBox.add(Box.createHorizontalStrut(5));
        
        final Box outerReplaceBox = Box.createVerticalBox();
        outerReplaceBox.setBorder(actionBorder);
        
        final Box replaceBox = Box.createHorizontalBox();
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
        
        final Box actionBox = Box.createVerticalBox();
        actionBox.add(Box.createVerticalGlue());
        actionBox.add(addDeleteBox);
        actionBox.add(Box.createVerticalStrut(5));
        actionBox.add(outerReplaceBox);
        actionBox.add(Box.createVerticalGlue());
        getContentPane().add(actionBox, BorderLayout.CENTER);
        
        final JButton closeBtn = new JButton(Resources.getInstance().getResourceString("close"));
        closeBtn.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
               setVisible(false);
            }
            
        });
        
        final Box bottomBox = Box.createHorizontalBox();
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
            mapView.getModel().getRegistry().getAttributes().removeAttributesListener(this);
        }
        mapView = currentMapView;        
        MindMap map = currentMapView.getModel();
        AttributeRegistry attributes = map.getRegistry().getAttributes();
        attributes.addAttributesListener(this);
        attributesChanged();
    }

    private void selectedAttributeChanged(Object selectedAttributeName, JComboBox values){
        AttributeRegistry attributes = mapView.getModel().getRegistry().getAttributes();
        try{
        AttributeRegistryElement element = attributes.getElement(selectedAttributeName.toString());
        ComboBoxModel selectedValues = element.getValues();
        Object firstValue = selectedValues.getElementAt(0);
        values.setModel(new ClonedComboBoxModel(selectedValues));
        values.setSelectedItem(firstValue);
        values.setEditable(! element.isRestricted());
        }
        catch(NoSuchElementException ex){
            values.setEditable(! selectedAttributeName.toString().equals(""));  
        }
    }

    public void attributesChanged(ChangeEvent e) {  
        attributesChanged();
    }
    
    private void attributesChanged() {  
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