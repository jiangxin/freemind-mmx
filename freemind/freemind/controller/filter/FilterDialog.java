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

import freemind.controller.Controller;
import freemind.controller.filter.condition.Condition;
import freemind.controller.filter.condition.ConditionNotSatisfiedDecorator;
import freemind.controller.filter.condition.ConjunctConditions;
import freemind.controller.filter.condition.DisjunctConditions;
import freemind.controller.filter.util.ForeignString;
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
            ForeignString attType = getAttributeType();
            String att = getAttribute();
            ForeignString simpleCond = getSimpleCondition();
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
                    if(ft.getBtnApply().isSelected())
                        ft.getBtnApplyRef().doClick();
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
    
    
    private FilterController fc;
	private JList conditionList;
	private JTextField conditionValue;
	private JComboBox simpleCondition;
	private JComboBox attributes;
	private JComboBox attributeType;
	private FilterToolbar ft;
	public FilterDialog(final FilterToolbar ft) {
		super(Controller.getInstance().getFilterController().getFrame());
		this.fc = Controller.getInstance().getFilterController();
		this.ft = ft;
		getContentPane().setLayout(new BorderLayout());
		
		final JPanel simpleConditionPanel = new JPanel();
		getContentPane().add(simpleConditionPanel, BorderLayout.NORTH);

		attributeType = new JComboBox();
		DefaultComboBoxModel filteredAttributeComboBoxModel = new DefaultComboBoxModel(new ForeignString[] {
		        new ForeignString("filter_icon"), 
		        new ForeignString("filter_attribute")
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
		DefaultComboBoxModel simpleConditionSomboBoxModel = new DefaultComboBoxModel(new ForeignString[] {
		        new ForeignString("filter_exist"), 
		        new ForeignString("filter_does_not_exist"), 
		        new ForeignString("filter_contains"), 
		        new ForeignString("filter_is_equal_to"), 
		        new ForeignString("filter_is_not_equal_to"), 
		        ForeignString.literal(">"), 
		        ForeignString.literal(">="), 
		        ForeignString.literal("<="), 
		        ForeignString.literal("<"), 
		});
        simpleCondition.setModel(simpleConditionSomboBoxModel);
		simpleConditionPanel.add(simpleCondition);
		simpleCondition.setEnabled(false);

		conditionValue = new JTextField();
		conditionValue.setColumns(20);
		simpleConditionPanel.add(conditionValue);
		conditionValue.setText(Controller.getInstance().getResourceString("filter_enter_value"));
		conditionValue.setEnabled(false);

		final JButton btnAdd = new JButton(new AddConditionAction());
		btnAdd.setAlignmentX(Component.CENTER_ALIGNMENT);
		simpleConditionPanel.add(btnAdd);

		final JToolBar conditionButtonToolbar = new JToolBar();
		conditionButtonToolbar.setOrientation(JToolBar.VERTICAL);
		conditionButtonToolbar.setFloatable(false);
		getContentPane().add(conditionButtonToolbar, BorderLayout.EAST);

		final JButton btnSelect = new JButton(new SelectConditionAction());
		btnSelect.setAlignmentX(Component.CENTER_ALIGNMENT);
		conditionButtonToolbar.add(btnSelect);

		final JButton btnNot = new JButton(new CreateNotSatisfiedConditionAction());
		btnNot.setAlignmentX(Component.CENTER_ALIGNMENT);
		conditionButtonToolbar.add(btnNot);

		final JButton btnAnd = new JButton(new CreateConjunctConditionAction());
		btnAnd.setAlignmentX(Component.CENTER_ALIGNMENT);
		conditionButtonToolbar.add(btnAnd);

		final JButton btnOR = new JButton(new CreateDisjunctConditionAction());
		btnOR.setAlignmentX(Component.CENTER_ALIGNMENT);
		conditionButtonToolbar.add(btnOR);

		final JButton btnDelete = new JButton(new DeleteConditionAction());
		btnDelete.setAutoscrolls(true);
		btnDelete.setAlignmentX(Component.CENTER_ALIGNMENT);
		conditionButtonToolbar.add(btnDelete);

		conditionList = new JList(ft.getActiveFilterConditionRef().getModel());
		conditionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		conditionList.setCellRenderer(fc.getConditionRenderer());
		conditionList.setLayoutOrientation(JList.VERTICAL);
		conditionList.setAlignmentX(Component.LEFT_ALIGNMENT);
		final JScrollPane conditionScrollPane = new JScrollPane(conditionList);
		getContentPane().add(conditionScrollPane, BorderLayout.CENTER);
	}
    public ForeignString getAttributeType() {
        return (ForeignString) attributeType.getSelectedItem();
    }
    public String getAttribute() {
        if (getAttributeType().equals("filter_icon")){
            MindIcon mi = (MindIcon)attributes.getSelectedItem();
            return mi.getName();
        }                
        return attributes.getSelectedItem().toString();
    }
    public ForeignString getSimpleCondition() {
        return (ForeignString) simpleCondition.getSelectedItem();
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
        return ft.getActiveFilterConditionRef();
    }
}
