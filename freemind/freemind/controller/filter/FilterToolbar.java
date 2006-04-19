/*
 * Created on 05.05.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.controller.filter;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JToolBar;

import freemind.controller.Controller;
import freemind.controller.filter.condition.Condition;
import freemind.controller.filter.condition.NoFilteringCondition;
import freemind.controller.filter.condition.SelectedViewCondition;
import freemind.main.Resources;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;

class FilterToolbar extends JToolBar {
    private FilterController fc;
    private FilterComposerDialog filterDialog = null;
    private JComboBox activeFilterCondition;
    private JCheckBox showAncestors;
    private JCheckBox showDescendants;
    private Filter activeFilter;
    private JButton btnEdit;
    private JButton btnUnfoldAncestors;
    private Controller c;
    private static Color filterInactiveColor = null;
    static private  final String FILTER_ON = Resources.getInstance().getResourceString("filter_on");
    static private  final String FILTER_OFF = Resources.getInstance().getResourceString("filter_off");
    
    private class FilterChangeListener extends AbstractAction implements ItemListener, PropertyChangeListener{
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public FilterChangeListener(){
        }
        public void actionPerformed(ActionEvent arg0) {
            resetFilter();
            getFilter();
            refreshMap();
            DefaultFilter.selectVisibleNode(c.getView());
        }
        /* (non-Javadoc)
         * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
         */
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED)
                filterChanged();
        }
        private void filterChanged() {
            resetFilter();
            getFilter().applyFilter(c);
            refreshMap();
            DefaultFilter.selectVisibleNode(c.getView());
        }
        public void propertyChange(PropertyChangeEvent evt) {
            if(evt.getPropertyName().equals("model")){
                addStandardConditions();
                filterChanged();                            
            }
        }
        
    }
    
    private class EditFilterAction extends AbstractAction {
        EditFilterAction() {
            super(Resources.getInstance().getResourceString("filter_edit"));
            putValue(SHORT_DESCRIPTION, Resources.getInstance().getResourceString("filter_edit_description"));
        }
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        private FilterComposerDialog getFilterDialog() {
            if (filterDialog == null){
                filterDialog = new FilterComposerDialog(c, FilterToolbar.this);
                getFilterDialog().setLocationRelativeTo(FilterToolbar.this);
            }
            return filterDialog;
        }
        public void actionPerformed(ActionEvent arg0) {
            Object selectedItem = getActiveFilterConditionComboBox().getSelectedItem();
            if(selectedItem != null){
                getFilterDialog().setSelectedItem(selectedItem);
            }
            if(getFilterDialog().isVisible() == false){
                getFilterDialog().setVisible(true);
            }
        }
        
    }
    
    private class UnfoldAncestorsAction extends AbstractAction {
        /**
         *
         */
        UnfoldAncestorsAction() {
            super("", new ImageIcon(Resources.getInstance().getResource("images/unfold.png")));
        }
        
        private void unfoldAncestors(MindMapNode parent) {
            for(Iterator i = parent.childrenUnfolded(); i.hasNext();) {
                MindMapNode node = (MindMapNode)i.next();
                if (showDescendants.isSelected()|| node.getFilterInfo().isAncestor() ){
                    setFolded(node, false);
                    unfoldAncestors(node) ;
                }
            }
        }
        
        private void setFolded(MindMapNode node, boolean state) {
            if(node.hasChildren() && (node.isFolded()!=state)) {
                c.getModeController().setFolded(node, state);
            }
        }
        public void actionPerformed(ActionEvent e) {
            if (getSelectedCondition() != null){
                unfoldAncestors((MindMapNode)c.getModel().getRoot());
            }
        }
    }
    
    FilterToolbar(final Controller c)
    {
        super();
        this.fc = c.getFilterController();
        this.c = c;
        setVisible(false);
        setFocusable(false);
        FilterChangeListener filterChangeListener = new FilterChangeListener();
        
        add(new JLabel(Resources.getInstance().getResourceString("filter_toolbar") + " "));
        
        
        activeFilter = null;
        activeFilterCondition = new JComboBox();
        activeFilterCondition.setFocusable(false);        
        activeFilterCondition.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        addStandardConditions();
        activeFilterCondition.setSelectedIndex(0);
        activeFilterCondition.setRenderer(fc.getConditionRenderer());
        add(activeFilterCondition);
        activeFilterCondition.addItemListener(filterChangeListener);
        activeFilterCondition.addPropertyChangeListener(filterChangeListener);
        
        btnEdit = new JButton(new EditFilterAction());
        add(btnEdit);
        
        btnUnfoldAncestors = new JButton(new UnfoldAncestorsAction());
        btnUnfoldAncestors.setToolTipText(Resources.getInstance().getResourceString("filter_unfold_ancestors"));
        btnUnfoldAncestors.setEnabled(false);
        add(btnUnfoldAncestors);
        
        showAncestors = new JCheckBox(Resources.getInstance().getResourceString("filter_show_ancestors"), true);
        add(showAncestors);
        showAncestors.getModel().addActionListener(filterChangeListener);
        
        showDescendants = new JCheckBox(Resources.getInstance().getResourceString("filter_show_descendants"), false);
        add(showDescendants);
        showDescendants.getModel().addActionListener(filterChangeListener);
    }
    
    /**
     *
     */
    public void resetFilter() {
        activeFilter = null;
        
    }
    
    private Condition getSelectedCondition() {
        return (Condition)activeFilterCondition.getSelectedItem();
    }
    
    JComboBox getActiveFilterConditionComboBox() {
        return activeFilterCondition;
    }
    
    Filter getFilter(){
        if(activeFilter == null)
            activeFilter = new DefaultFilter(
                    getSelectedCondition(),
                    showAncestors.getModel().isSelected(),
                    showDescendants.getModel().isSelected()
            );
        fc.getMap().setFilter(activeFilter);
        return activeFilter;
    }
    
    /**
     * @return
     */
    FilterComposerDialog getFilterDialog() {
        return filterDialog;
    }

    /**
     * @param filter
     */
    void mapChanged(MindMap newMap) {
        if(!isVisible())
            return;
        Filter filter = newMap.getFilter();
        if(filter != activeFilter){
            activeFilter = filter;
            activeFilterCondition.setSelectedItem(filter);
            showAncestors.setSelected(filter.areAncestorsShown());
            showDescendants.setSelected(filter.areDescendantsShown());
        }
    }

    private void refreshMap() {
        fc.refreshMap();
    }

    private void addStandardConditions() {
        final DefaultComboBoxModel model = (DefaultComboBoxModel)activeFilterCondition.getModel();
        model.insertElementAt(NoFilteringCondition.CreateCondition(), 0);
        model.insertElementAt(SelectedViewCondition.CreateCondition(), 1);
    }
}
