/*
 * Created on 22.01.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.modes.attributes.mindmapmodeactors;

import java.util.ListIterator;
import java.util.NoSuchElementException;

import javax.xml.bind.JAXBException;

import freemind.controller.actions.ActionPair;
import freemind.controller.filter.util.SortedComboBoxModel;
import freemind.modes.ControllerAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.attributes.Attribute;
import freemind.modes.attributes.AttributeController;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.attributes.AttributeRegistryElement;
import freemind.modes.attributes.NodeAttributeTableModel;

public class MindMapModeAttributeController implements AttributeController{
    private static interface Visitor{
        void visit(NodeAttributeTableModel model) throws JAXBException;
    }
    
    private class AttributeRenamer implements Visitor{

        private Object oldName;
        private Object newName;

        public AttributeRenamer(Object oldName, Object newName) {
            super();
            this.newName = newName;
            this.oldName = oldName;
        }
        /* (non-Javadoc)
         * @see freemind.modes.attributes.AttributeRegistry.Visitor#visit(freemind.modes.attributes.ConcreteAttributeTableModel)
         */
        public void visit(NodeAttributeTableModel model) throws JAXBException {
            for(int i = 0; i < model.getRowCount(); i++){
                if(model.getName(i).equals(oldName)){
                    final ActionPair setAttributeNameActionPair = setAttributeNameActor.createActionPair(model, i, newName.toString());
                    controller.getActionFactory().executeAction(setAttributeNameActionPair);                }
            }            
        }
        
    }
    
    private class AttributeChanger implements Visitor{
        private Object name;
        private Object oldValue;
        private Object newValue;

        public AttributeChanger(Object name, Object oldValue, Object newValue) {
            super();
            this.name = name;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }
        /* (non-Javadoc)
         * @see freemind.modes.attributes.AttributeRegistry.Visitor#visit(freemind.modes.attributes.ConcreteAttributeTableModel)
         */
        public void visit(NodeAttributeTableModel model) throws JAXBException {
            for(int i = 0; i < model.getRowCount(); i++){
                if(model.getName(i).equals(name) && model.getValue(i).equals(oldValue)){
                    final ActionPair setAttributeValueActionPair = setAttributeValueActor.createActionPair(model, i, newValue.toString());
                    controller.getActionFactory().executeAction(setAttributeValueActionPair);                    
                }
            }            
        }
    }
    private class AttributeRemover implements Visitor{
        private Object name;

        public AttributeRemover(Object name) {
            super();
            this.name = name;
        }
        /* (non-Javadoc)
         * @see freemind.modes.attributes.AttributeRegistry.Visitor#visit(freemind.modes.attributes.ConcreteAttributeTableModel)
         */
        public void visit(NodeAttributeTableModel model) throws JAXBException {
            for(int i = 0; i < model.getRowCount(); i++){
                if(model.getName(i).equals(name)){
                    final ActionPair removeAttributeActionPair = removeAttributeActor.createActionPair(model, i);
                    controller.getActionFactory().executeAction(removeAttributeActionPair);                    
                }      
            }
         }
    }
    
    private class AttributeValueRemover implements Visitor{

        private Object name;
        private Object value;

        public AttributeValueRemover(Object name, Object value) {
            super();
            this.name = name;
            this.value = value;
        }
        /* (non-Javadoc)
         * @see freemind.modes.attributes.AttributeRegistry.Visitor#visit(freemind.modes.attributes.ConcreteAttributeTableModel)
         */
        public void visit(NodeAttributeTableModel model) throws JAXBException {
            for(int i = 0; i < model.getRowCount(); i++){
                if(model.getName(i).equals(name) && model.getValue(i).equals(value)){
                    final ActionPair removeAttributeActionPair = removeAttributeActor.createActionPair(model, i);
                    controller.getActionFactory().executeAction(removeAttributeActionPair);                   
                }          
            }            
        }
    }
    private static class Iterator{
        private Visitor visitor;
        Iterator(Visitor v){
            this.visitor = v;
        }
        /**
         * @param root
         * @throws JAXBException 
         */
        void iterate(MindMapNode node) throws JAXBException {
            visitor.visit(node.getAttributes());            
            ListIterator iterator = node.childrenUnfolded();
            while(iterator.hasNext()){
                MindMapNode child = (MindMapNode)iterator.next();
                iterate(child);           
            }            
        }
    }

    SetAttributeNameActor setAttributeNameActor;
    SetAttributeValueActor setAttributeValueActor;
    RemoveAttributeActor removeAttributeActor;
    InsertAttributeActor insertAttributeActor;
    SetAttributeColumnWidthActor setAttributeColumnWidthActor;
    RegistryAttributeActor registryAttributeActor;
    UnregistryAttributeActor unregistryAttributeActor;
    RegistryAttributeValueActor registryAttributeValueActor;
    UnregistryAttributeValueActor unregistryAttributeValueActor;
    ReplaceAttributeValueActor replaceAttributeValueActor;
    SetAttributeFontSizeActor setAttributeFontSizeActor;
    SetAttributeVisibleActor setAttributeVisibleActor;
    SetAttributeRestrictedActor setAttributeRestrictedActor;
    
    private ModeController controller;

    
    public MindMapModeAttributeController(ControllerAdapter controller) {
        this.controller = controller;
        setAttributeNameActor = new SetAttributeNameActor(controller);
        setAttributeValueActor = new SetAttributeValueActor(controller);
        removeAttributeActor = new RemoveAttributeActor(controller);
        insertAttributeActor = new InsertAttributeActor(controller);
        setAttributeColumnWidthActor = new SetAttributeColumnWidthActor(controller);
        registryAttributeActor = new RegistryAttributeActor(controller);
        unregistryAttributeActor = new UnregistryAttributeActor(controller);
        registryAttributeValueActor = new RegistryAttributeValueActor(controller);
        replaceAttributeValueActor = new ReplaceAttributeValueActor(controller);
        unregistryAttributeValueActor = new UnregistryAttributeValueActor(controller);
        setAttributeFontSizeActor = new SetAttributeFontSizeActor(controller);
        setAttributeVisibleActor = new SetAttributeVisibleActor(controller);
        setAttributeRestrictedActor = new SetAttributeRestrictedActor(controller);
    }

    public void performSetValueAt(NodeAttributeTableModel model, Object o, int row, int col) {
        try{
            startTransaction("performSetValueAt");
            Attribute attribute = model.getAttribute(row);

            AttributeRegistry attributes = getAttributeRegistry();
            switch(col){
            case 0: 
            {
                if(attribute.getName().equals(o))
                    return;
                String name = o.toString();
                final ActionPair setAttributeNameActionPair = setAttributeNameActor.createActionPair(model, row, name);
                controller.getActionFactory().executeAction(setAttributeNameActionPair);
                try{
                    AttributeRegistryElement element = attributes.getElement(name);
                    String value = model.getValueAt(row, 1).toString(); 
                    int index = element.getValues().getIndexOf(value);
                    if(index == -1){
                        final ActionPair setAttributeValueActionPair = setAttributeValueActor.createActionPair(model, row, element.getValues().firstElement().toString());
                        controller.getActionFactory().executeAction(setAttributeValueActionPair);
                    }
                }
                catch(NoSuchElementException ex)
                {
                    final ActionPair registryAttributeActionPair = registryAttributeActor.createActionPair(name, "");
                    controller.getActionFactory().executeAction(registryAttributeActionPair);
                }
                break;
            }
            case 1:
            {
                if(attribute.getValue().equals(o))
                    return;
                String value = o.toString();
                final ActionPair setValueActionPair = setAttributeValueActor.createActionPair(model, row, value);
                controller.getActionFactory().executeAction(setValueActionPair);
                String name = model.getValueAt(row, 0).toString(); 
                AttributeRegistryElement element = attributes.getElement(name);
                int index = element.getValues().getIndexOf(value);
                if(index == -1){
                    final ActionPair registryAttributeValueActionPair = registryAttributeValueActor.createActionPair(name, value);
                    controller.getActionFactory().executeAction(registryAttributeValueActionPair);
                }                
                break;
            }
            }      
            
            endTransaction("performSetValueAt");
        }
        catch(JAXBException e){
            e.printStackTrace();
        }
    }

    private void endTransaction(String name) {
        controller.getActionFactory().endTransaction(this.getClass().getName() + "." + name);
    }

    private void startTransaction(String name) {
        controller.getActionFactory().startTransaction(this.getClass().getName() + "." + name);
    }
    
    public void performInsertRow(NodeAttributeTableModel model, int row, String name, String value) {
        try{
            startTransaction("performInsertRow");
            AttributeRegistry attributes = getAttributeRegistry();
            if(name.equals(""))
                return;
            try{
                AttributeRegistryElement element = attributes.getElement(name);
                int index = element.getValues().getIndexOf(value);
                if(index == -1){
                    if(element.isRestricted()){
                        value = element.getValues().firstElement().toString();
                    }
                    else{
                        final ActionPair registryNewAttributeActionPair = registryAttributeValueActor.createActionPair(name, value);                
                        controller.getActionFactory().executeAction(registryNewAttributeActionPair);
                    }
                }
            }
            catch(NoSuchElementException ex)
            {
                final ActionPair registryAttributeActionPair = registryAttributeActor.createActionPair(name, value);
                controller.getActionFactory().executeAction(registryAttributeActionPair);
            }
            final ActionPair insertAttributeActionPair = insertAttributeActor.createActionPair(model, row, name, value);
            controller.getActionFactory().executeAction(insertAttributeActionPair);
            endTransaction("performInsertRow");
        }
        catch(JAXBException e){
            e.printStackTrace();
        }
    }

    public void performRemoveRow(NodeAttributeTableModel model, int row) {
        try{
            startTransaction("performRemoveRow");
            final ActionPair removeAttributeActionPair = removeAttributeActor.createActionPair(model, row);
            controller.getActionFactory().executeAction(removeAttributeActionPair);
            endTransaction("performRemoveRow");
        }
        catch(JAXBException e){
            e.printStackTrace();
        }
    }

    public void performSetColumnWidth(NodeAttributeTableModel model, int col, int width) {
        if(width == model.getLayout().getColumnWidth(col))
            return;
        try{
            startTransaction("performSetColumnWidth");
            final ActionPair setAttributeColumnWidthActionPair = setAttributeColumnWidthActor.createActionPair(model, col, width);
            controller.getActionFactory().executeAction(setAttributeColumnWidthActionPair);
            endTransaction("performSetColumnWidth");
        }
        catch(JAXBException e){
            e.printStackTrace();
        }
    }
    
    public void performRemoveAttributeValue(String name, String value) {
        try{
            startTransaction("performRemoveAttributeValue");
            final ActionPair removeAttributeActionPair = unregistryAttributeValueActor.createActionPair(name, value);
            controller.getActionFactory().executeAction(removeAttributeActionPair);
            Visitor remover = new AttributeValueRemover(name, value); 
            Iterator iterator = new Iterator(remover);
            MindMapNode root = controller.getRootNode();
            iterator.iterate(root);
            endTransaction("performRemoveAttributeValue");
        }
        catch(JAXBException e){
            e.printStackTrace();
        }
    }

    public void performReplaceAttributeValue(String name, String oldValue, String newValue) {
        try{
            startTransaction("performReplaceAttributeValue");
            final ActionPair replaceAttributeActionPair = replaceAttributeValueActor.createActionPair(name, oldValue, newValue);
            controller.getActionFactory().executeAction(replaceAttributeActionPair);
            Visitor replacer = new AttributeChanger(name, oldValue, newValue); 
            Iterator iterator = new Iterator(replacer);
            MindMapNode root = controller.getRootNode();
            iterator.iterate(root);
            endTransaction("performReplaceAttributeValue");
        }
        catch(JAXBException e){
            e.printStackTrace();
        }

    }

    public void performSetFontSize(AttributeRegistry registry, int size) {
        if(size == registry.getFontSize())
            return;
        try{
            startTransaction("performSetFontSize");
            final ActionPair setFontSizeActionPair = setAttributeFontSizeActor.createActionPair(size);
            controller.getActionFactory().executeAction(setFontSizeActionPair);
            endTransaction("performSetFontSize");
        }
        catch(JAXBException e){
            e.printStackTrace();
        }
    }

    public void performSetVisibility(int index, boolean isVisible) {
        if(getAttributeRegistry().getElement(index).isVisible() == isVisible)
            return;
        try{
            startTransaction("performSetVisibility");
            final ActionPair setVisibilityActionPair = setAttributeVisibleActor.createActionPair(index, isVisible);
            controller.getActionFactory().executeAction(setVisibilityActionPair);
            endTransaction("performSetVisibility");
        }
        catch(JAXBException e){
            e.printStackTrace();
        }
    }

    public void performSetRestriction(int index, boolean isRestricted) {
        boolean currentValue;
        if (index == AttributeRegistry.GLOBAL){
            currentValue= getAttributeRegistry().isRestricted();
        }else{
            currentValue= getAttributeRegistry().getElement(index).isRestricted();
        }
        
        if(currentValue == isRestricted)
            return;
        try{
            startTransaction("performSetRestriction");
            final ActionPair setRestrictionActionPair = setAttributeRestrictedActor.createActionPair(index, isRestricted);
            controller.getActionFactory().executeAction(setRestrictionActionPair);
            endTransaction("performSetRestriction");
        }
        catch(JAXBException e){
            e.printStackTrace();
        }     
    }

    public void performReplaceAtributeName(String oldName, String newName) {
        if(oldName.equals("") || newName.equals("") || oldName.equals(newName))
            return;                
        try{
            startTransaction("performReplaceAtributeName");
            final ActionPair unregistryOldAttributeActionPair = unregistryAttributeActor.createActionPair(oldName);
            controller.getActionFactory().executeAction(unregistryOldAttributeActionPair);
            AttributeRegistry registry = getAttributeRegistry();
            int iOld = registry.getElements().indexOf(oldName);
            AttributeRegistryElement oldElement = registry.getElement(iOld);
            final SortedComboBoxModel values = oldElement.getValues();
            for(int i = 0; i < values.getSize(); i++){
                final ActionPair registryNewAttributeActionPair = registryAttributeActor.createActionPair(newName, values.getElementAt(i).toString());                
                controller.getActionFactory().executeAction(registryNewAttributeActionPair);
            }
            Visitor replacer = new AttributeRenamer(oldName, newName); 
            Iterator iterator = new Iterator(replacer);
            MindMapNode root = controller.getRootNode();
            iterator.iterate(root);        
            endTransaction("performReplaceAtributeName");
        }
        catch(JAXBException e){
            e.printStackTrace();
        }
    }

    public void performRemoveAttribute(String name) {
        try{
            startTransaction("performReplaceAtributeName");
            final ActionPair unregistryOldAttributeActionPair = unregistryAttributeActor.createActionPair(name);
            controller.getActionFactory().executeAction(unregistryOldAttributeActionPair);
            Visitor remover = new AttributeRemover(name); 
            Iterator iterator = new Iterator(remover);
            MindMapNode root = controller.getRootNode();
            iterator.iterate(root);
            endTransaction("performReplaceAtributeName");
        }
        catch(JAXBException e){
            e.printStackTrace();
        }

        
    }

    public void performRegistryAttribute(String name, String value) {
        if(name.equals(""))
            return;       
        try{
        final AttributeRegistryElement element = getAttributeRegistry().getElement(name);
        if (element.getValues().contains(value))
            return;
        try{
            startTransaction("performRegistryAttributeValue");
            final ActionPair registryNewAttributeActionPair = registryAttributeValueActor.createActionPair(name, value);                
            controller.getActionFactory().executeAction(registryNewAttributeActionPair);
            endTransaction("performRegistryAttributeValue");
        }
        catch(JAXBException e){
            e.printStackTrace();
        }
        return;
        }
        catch(NoSuchElementException ex){
            try{
                startTransaction("performRegistryAttribute");
                final ActionPair registryNewAttributeActionPair = registryAttributeActor.createActionPair(name, value);                
                controller.getActionFactory().executeAction(registryNewAttributeActionPair);
                endTransaction("performRegistryAttribute");
            }
            catch(JAXBException e){
                e.printStackTrace();
            }
            return;            
        }
        
    }

    private AttributeRegistry getAttributeRegistry() {
        return controller.getMap().getRegistry().getAttributes();
    }
}
