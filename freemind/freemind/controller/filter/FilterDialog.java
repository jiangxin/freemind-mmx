/*
 * Created on 05.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package freemind.controller.filter;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import freemind.controller.Controller;
import freemind.controller.filter.condition.Condition;
import freemind.controller.filter.condition.ConditionFactory;
import freemind.controller.filter.condition.ConditionNotSatisfiedDecorator;
import freemind.controller.filter.condition.ConjunctConditions;
import freemind.controller.filter.condition.DisjunctConditions;
import freemind.controller.filter.condition.NodeCondition;
import freemind.controller.filter.util.ExtendedComboBoxModel;
import freemind.controller.filter.util.SortedListModel;
import freemind.controller.filter.util.TranslatedString;
import freemind.modes.MindIcon;
import freemind.modes.MindMap;

/**
 * @author dimitri
 *
 */
public class FilterDialog extends JDialog {
    /**
     * @author dimitri
     * 06.05.2005
     */
    private class AddConditionAction extends AbstractAction {
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        AddConditionAction(){
            super(Controller.getInstance().getResourceString("filter_add"));
        }
        public void actionPerformed(ActionEvent e) {
            TranslatedString attribute = (TranslatedString) attributes.getSelectedItem();
            String value = getAttributeValue();
            TranslatedString simpleCond = (TranslatedString) simpleCondition.getSelectedItem();
            boolean ignoreCase = caseInsensitive.isSelected();
            Condition newCond = fc.getConditionFactory().createCondition(
                    attribute, simpleCond, value, ignoreCase);
            DefaultComboBoxModel model = (DefaultComboBoxModel) conditionList.getModel();
            if (newCond != null) 
                model.addElement(newCond);
            if(values.isEditable()){
                Object item = values.getSelectedItem();
                if (item != null && !item.equals("")){
                    values.removeItem(item);
                    values.insertItemAt(item, 0);
                    values.setSelectedIndex(0);
                    if (values.getItemCount() >= 10) values.removeItemAt(9);
                }
            }
            validate();
        }
    }
    
    private class SelectConditionAction extends AbstractAction {
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        SelectConditionAction(){
            super(Controller.getInstance().getResourceString("filter_select"));
        }
        public void actionPerformed(ActionEvent e) {
            JList conditions = conditionList;
            int min = conditions.getMinSelectionIndex();
            if(min >= 0){
                int max = conditions.getMinSelectionIndex();
                if(min == max){
                    ft.getActiveFilterConditionComboBox().setSelectedIndex(min);
                }
            }
        }
    }
    
    private class DeleteConditionAction extends AbstractAction {
         /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        DeleteConditionAction(){
            super(Controller.getInstance().getResourceString("filter_delete"));
        }
        public void actionPerformed(ActionEvent e) {
            DefaultComboBoxModel model = (DefaultComboBoxModel)conditionList.getModel();
            int selectedIndex;
            while(0 <= (selectedIndex = conditionList.getSelectedIndex())){
                if(selectedIndex == ft.getActiveFilterConditionComboBox().getSelectedIndex()){
                    ft.getActiveFilterConditionComboBox().setSelectedIndex(-1);
                    if(ft.getBtnApply().getModel().isSelected())
                        ft.getBtnApply().doClick();
                }
                model.removeElementAt(selectedIndex);                 
            }           
            validate();
        }
    }
    
    private class CreateNotSatisfiedConditionAction extends AbstractAction {
            /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        CreateNotSatisfiedConditionAction(){
            super(Controller.getInstance().getResourceString("filter_not"));
        }
        public void actionPerformed(ActionEvent e) {            
            int min = conditionList.getMinSelectionIndex();
            if(min >= 0){
                int max = conditionList.getMinSelectionIndex();
                if(min == max){
                    Condition oldCond = (Condition)conditionList.getSelectedValue();   
                    Condition newCond = new ConditionNotSatisfiedDecorator(oldCond); 
                    DefaultComboBoxModel model = (DefaultComboBoxModel)conditionList.getModel();
                    model.addElement(newCond);
                    validate();
                    
                }
            }
        }
    }
    
    private class CreateConjunctConditionAction extends AbstractAction {

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        CreateConjunctConditionAction(){
            super(Controller.getInstance().getResourceString("filter_and"));
        }
        public void actionPerformed(ActionEvent e) {
            Object[] selectedValues = conditionList.getSelectedValues() ;
            if (selectedValues.length < 2) return;
            Condition newCond = new ConjunctConditions(selectedValues); 
            DefaultComboBoxModel model = (DefaultComboBoxModel )conditionList.getModel();
            model.addElement(newCond);
            validate();
            
        }
    }
    
    private class CreateDisjunctConditionAction extends AbstractAction {

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        CreateDisjunctConditionAction(){
            super(Controller.getInstance().getResourceString("filter_or"));
        }
        public void actionPerformed(ActionEvent e) {
            Object[] selectedValues = conditionList.getSelectedValues() ;
            if (selectedValues.length < 2) return;
            Condition newCond = new DisjunctConditions(selectedValues); 
            DefaultComboBoxModel model = (DefaultComboBoxModel )conditionList.getModel();
            model.addElement(newCond);
            validate();
           
        }
    }
    
    private class ConditionListSelectionListener extends AbstractAction implements ListSelectionListener {

        /* (non-Javadoc)
         * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
         */
        public void valueChanged(ListSelectionEvent e) {
            
            if (conditionList.getMinSelectionIndex() == -1){
                btnSelect.setEnabled(false);
                btnNot.setEnabled(false);
                btnAnd.setEnabled(false);
                btnOr.setEnabled(false);
                btnDelete.setEnabled(false);
                return;
            }
            else if(conditionList.getMinSelectionIndex() == conditionList.getMaxSelectionIndex()){
                btnSelect.setEnabled(true);
                btnNot.setEnabled(true);
                btnAnd.setEnabled(false);
                btnOr.setEnabled(false);
                btnDelete.setEnabled(true);
                return;
            }
            else {
	            btnSelect.setEnabled(false);
	            btnNot.setEnabled(false);
	            btnAnd.setEnabled(true);
	            btnOr.setEnabled(true);
	            btnDelete.setEnabled(false);
            }
        }

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
        }
        
    }
    
    private static final int NODE_POSITION = 0;
    private static final int ICON_POSITION = 1;
    private static final int CONTAINS_POSITION = 0;
    
    private class SelectedAttributeChangeListener extends AbstractAction implements ItemListener {
        /* (non-Javadoc)
         * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
         */
        public void itemStateChanged(ItemEvent e) {
            if(e.getStateChange() == ItemEvent.SELECTED)
            {
                if (attributes.getSelectedIndex() == NODE_POSITION){
                    simpleCondition.setEnabled(true);
                    values.setEditable(true);
                    values.setModel(nodes);
                    caseInsensitive.setEnabled(true);
                    return;
                }
                if (attributes.getSelectedIndex() == ICON_POSITION){
                    simpleCondition.setSelectedIndex(CONTAINS_POSITION);
                    simpleCondition.setEnabled(false);
                    values.setEditable(false);
                    values.setModel(icons);
                    if(icons.getSize() >= 1){
                        values.setSelectedIndex(0);
                    }
                    caseInsensitive.setEnabled(false);
                    return;
                }
            }
        }

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
        }
    }
    
    private FilterController fc;
	private JList conditionList;
	private JComboBox simpleCondition;
	private JComboBox values;
	private JComboBox attributes;
	private FilterToolbar ft;
    private JButton btnAdd;
    private JButton btnSelect;
    private JButton btnNot;
    private JButton btnAnd;
    private JButton btnOr;
    private JButton btnDelete;
    private JCheckBox caseInsensitive;
    private ExtendedComboBoxModel icons;
    private DefaultComboBoxModel nodes;
    public FilterDialog(final FilterToolbar ft) {
        super(Controller.getInstance().getFilterController().getFrame());
        this.fc = Controller.getInstance().getFilterController();
        this.ft = ft;
        getContentPane().setLayout(new BorderLayout());
        
        final JToolBar simpleConditionToolbar = new JToolBar();
        simpleConditionToolbar.setOrientation(JToolBar.HORIZONTAL);
        simpleConditionToolbar.setFloatable(false);
        getContentPane().add(simpleConditionToolbar, BorderLayout.NORTH);
        
        attributes = new JComboBox();
        DefaultComboBoxModel filteredAttributeComboBoxModel = new DefaultComboBoxModel(new TranslatedString[] {
                new TranslatedString("filter_node"), 
                new TranslatedString("filter_icon")
        });
        attributes.setModel(filteredAttributeComboBoxModel);
        attributes.addItemListener(new SelectedAttributeChangeListener());
        simpleConditionToolbar.add(attributes);
        
        simpleCondition = new JComboBox();
        DefaultComboBoxModel simpleConditionComboBoxModel = new DefaultComboBoxModel(fc.getConditionFactory().getNodeConditionNames());
        simpleCondition.setModel(simpleConditionComboBoxModel);
        simpleConditionToolbar.add(simpleCondition);
        
        values = new JComboBox();
        icons = new ExtendedComboBoxModel();
        nodes = new DefaultComboBoxModel();
        values.setModel(nodes);
        simpleConditionToolbar.add(values);
        values.setRenderer(fc.getMindIconRenderer());
        
        icons.setExtensionList(Controller.getInstance().getModel().getStatistics().getIcons());
        values.setEditable(true);
        
        caseInsensitive = new JCheckBox();
        simpleConditionToolbar.add(caseInsensitive);
        caseInsensitive.setText(Controller.getInstance().getResourceString("filter_ignore_case"));
        
        btnAdd = new JButton(new AddConditionAction());
        btnAdd.setAlignmentX(Component.CENTER_ALIGNMENT);
        simpleConditionToolbar.add(btnAdd);
        
        final JToolBar conditionButtonToolbar = new JToolBar();
        conditionButtonToolbar.setOrientation(JToolBar.VERTICAL);
        conditionButtonToolbar.setFloatable(false);
        getContentPane().add(conditionButtonToolbar, BorderLayout.EAST);
        
        btnSelect = new JButton(new SelectConditionAction());
        btnSelect.setAlignmentX(Component.CENTER_ALIGNMENT);
        conditionButtonToolbar.add(btnSelect);
        btnSelect.setEnabled(false);
        
        btnNot = new JButton(new CreateNotSatisfiedConditionAction());
        btnNot.setAlignmentX(Component.CENTER_ALIGNMENT);
        conditionButtonToolbar.add(btnNot);
        btnNot.setEnabled(false);
        
        btnAnd = new JButton(new CreateConjunctConditionAction());
        btnAnd.setAlignmentX(Component.CENTER_ALIGNMENT);
        conditionButtonToolbar.add(btnAnd);
        btnAnd.setEnabled(false);
        
        btnOr = new JButton(new CreateDisjunctConditionAction());
        btnOr.setAlignmentX(Component.CENTER_ALIGNMENT);
        conditionButtonToolbar.add(btnOr);
        btnOr.setEnabled(false);
        
        btnDelete = new JButton(new DeleteConditionAction());
        btnDelete.setAutoscrolls(true);
        btnDelete.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDelete.setEnabled(false);
        conditionButtonToolbar.add(btnDelete);
        
        conditionList = new JList(ft.getActiveFilterConditionComboBox().getModel());
        conditionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        conditionList.setCellRenderer(fc.getConditionRenderer());
        conditionList.setLayoutOrientation(JList.VERTICAL);
        conditionList.setAlignmentX(Component.LEFT_ALIGNMENT);
        conditionList.addListSelectionListener(new ConditionListSelectionListener());
        
        final JScrollPane conditionScrollPane = new JScrollPane(conditionList);
        conditionScrollPane.setPreferredSize(new Dimension(500, 200));
        getContentPane().add(conditionScrollPane, BorderLayout.CENTER);
    }
    
    private String getAttributeValue() {
        if (((TranslatedString) attributes.getSelectedItem()).equals("filter_icon")){
            MindIcon mi = (MindIcon)values.getSelectedItem();
            return mi.getName();
        }  
        Object item = values.getSelectedItem();
        return item != null ? item.toString() : "";
    }

    /**
     * @param newMap
     */
    void mapChanged(MindMap newMap) {
        icons.setExtensionList(newMap.getStatistics().getIcons());        
        if(icons.getSize() >= 1){
            values.setSelectedIndex(0);
        }
        else{
            values.setSelectedIndex(-1);
        }
    }
}