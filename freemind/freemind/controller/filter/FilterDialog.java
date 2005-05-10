/*
 * Created on 05.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package freemind.controller.filter;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
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
import freemind.controller.filter.condition.ConditionNotSatisfiedDecorator;
import freemind.controller.filter.condition.ConjunctConditions;
import freemind.controller.filter.condition.DisjunctConditions;
import freemind.controller.filter.util.TranslatedString;
import freemind.modes.MindIcon;

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
            TranslatedString attType = getAttributeType();
            String att = getAttribute();
            TranslatedString simpleCond = getSimpleCondition();
            String condValue = getConditionValue();
            Condition newCond = getFilterController().createCondition(
                    attType, att, simpleCond, condValue);
            DefaultComboBoxModel model = (DefaultComboBoxModel) getConditionList().getModel();
            if (newCond != null) 
                model.addElement(newCond);
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
            JList conditions = getConditionList();
            int min = conditions.getMinSelectionIndex();
            if(min >= 0){
                int max = conditions.getMinSelectionIndex();
                if(min == max){
                    getSelectedConditionReference().setSelectedIndex(min);
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
            DefaultComboBoxModel model = (DefaultComboBoxModel)getConditionList().getModel();
            int selectedIndex;
            while(0 <= (selectedIndex = getConditionList().getSelectedIndex())){
                if(selectedIndex == getSelectedConditionReference().getSelectedIndex()){
                    getSelectedConditionReference().setSelectedIndex(-1);
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
    
    private FilterController fc;
	private JList conditionList;
	private JTextField conditionValue;
	private JComboBox simpleCondition;
	private JComboBox attributes;
	private JComboBox attributeType;
	private FilterToolbar ft;
    private JButton btnAdd;
    private JButton btnSelect;
    private JButton btnNot;
    private JButton btnAnd;
    private JButton btnOr;
    private JButton btnDelete;
	public FilterDialog(final FilterToolbar ft) {
		super(Controller.getInstance().getFilterController().getFrame());
		this.fc = Controller.getInstance().getFilterController();
		this.ft = ft;
		getContentPane().setLayout(new BorderLayout());
		
		final JPanel simpleConditionPanel = new JPanel();
		getContentPane().add(simpleConditionPanel, BorderLayout.NORTH);

		attributeType = new JComboBox();
		DefaultComboBoxModel filteredAttributeComboBoxModel = new DefaultComboBoxModel(new TranslatedString[] {
		        new TranslatedString("filter_icon"), 
		        new TranslatedString("filter_attribute")
		        });
        attributeType.setModel(filteredAttributeComboBoxModel);
		simpleConditionPanel.add(attributeType);
		attributeType.setEnabled(false);

		attributes = new JComboBox();
		attributes.setRenderer(fc.getMindIconRenderer());
	    Vector iconNames = MindIcon.getAllIconNames();
	    for(int i=0; i< iconNames.size(); i++){
	        String iconName = (String)iconNames.get(i);
            attributes.addItem(fc.getIcon(iconName));
	    }
		
		attributes.setSelectedIndex(0);
		simpleConditionPanel.add(attributes);

		simpleCondition = new JComboBox();
		DefaultComboBoxModel simpleConditionSomboBoxModel = new DefaultComboBoxModel(new TranslatedString[] {
		        new TranslatedString("filter_exist"), 
		        new TranslatedString("filter_does_not_exist"), 
		        new TranslatedString("filter_contains"), 
		        new TranslatedString("filter_is_equal_to"), 
		        new TranslatedString("filter_is_not_equal_to"), 
		        TranslatedString.literal(">"), 
		        TranslatedString.literal(">="), 
		        TranslatedString.literal("<="), 
		        TranslatedString.literal("<"), 
		});
        simpleCondition.setModel(simpleConditionSomboBoxModel);
		simpleConditionPanel.add(simpleCondition);
		simpleCondition.setEnabled(false);

		conditionValue = new JTextField();
		conditionValue.setColumns(20);
		simpleConditionPanel.add(conditionValue);
		conditionValue.setText(Controller.getInstance().getResourceString("filter_enter_value"));
		conditionValue.setEnabled(false);

		btnAdd = new JButton(new AddConditionAction());
        btnAdd.setAlignmentX(Component.CENTER_ALIGNMENT);
		simpleConditionPanel.add(btnAdd);

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
		getContentPane().add(conditionScrollPane, BorderLayout.CENTER);
	}
    public TranslatedString getAttributeType() {
        return (TranslatedString) attributeType.getSelectedItem();
    }
    public String getAttribute() {
        if (getAttributeType().equals("filter_icon")){
            MindIcon mi = (MindIcon)attributes.getSelectedItem();
            return mi.getName();
        }                
        return attributes.getSelectedItem().toString();
    }
    public TranslatedString getSimpleCondition() {
        return (TranslatedString) simpleCondition.getSelectedItem();
    }
    public String getConditionValue() {
        return conditionValue.getText();
    }
    public JList getConditionList() {
        return conditionList;
    }

    FilterController getFilterController() {
        return fc;
    }
    JComboBox getSelectedConditionReference() {
        return ft.getActiveFilterConditionComboBox();
    }
}
