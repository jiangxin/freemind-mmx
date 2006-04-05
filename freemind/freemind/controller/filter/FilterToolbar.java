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
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import freemind.controller.Controller;
import freemind.controller.filter.condition.Condition;
import freemind.controller.filter.condition.ConditionRenderer;
import freemind.main.Resources;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;

class FilterToolbar extends JToolBar {
    private FilterController fc;
    private FilterComposerDialog filterDialog = null;
    private JComboBox activeFilterCondition;
    private JToggleButton btnApply;
    private JCheckBox showAncestors;
    private JCheckBox showDescendants;
    private Filter activeFilter;
    private Filter inactiveFilter;
    private JButton btnEdit;
    private JButton btnUnfoldAncestors;
    private Controller c;
    private static Color filterInactiveColor = null;
    static private  final String FILTER_ON = Resources.getInstance().getResourceString("filter_on");
    static private  final String FILTER_OFF = Resources.getInstance().getResourceString("filter_off");
    
    private class ApplyFilterAction extends AbstractAction {
        
        /**
         *
         */
        ApplyFilterAction() {
            super(FILTER_ON);
        }
        
        public void actionPerformed(ActionEvent e) {
            resetFilter();
            getFilter().applyFilter(c);
            refreshMap();
            if(btnApply.isSelected()){
                if(filterInactiveColor == null)
                    filterInactiveColor = btnApply.getBackground();
                btnApply.setBackground(ConditionRenderer.SELECTED_BACKGROUND);
                btnApply.setText(FILTER_OFF);
            }
            else{
                btnApply.setBackground(filterInactiveColor);                
                btnApply.setText(FILTER_ON);
            }
            btnUnfoldAncestors.setEnabled(btnApply.getModel().isSelected());
        }
    }
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
            final boolean isFilterSet = activeFilterCondition.getSelectedItem() != null;
            if(btnApply.isSelected() && ! isFilterSet)
                btnApply.doClick();
            btnApply.setEnabled(isFilterSet);
            resetFilter();
            getFilter().applyFilter(c);
            refreshMap();
            DefaultFilter.selectVisibleNode(c.getView());
        }
        public void propertyChange(PropertyChangeEvent evt) {
            if(evt.getPropertyName().equals("model"))
                filterChanged();            
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
        
        activeFilterCondition = new JComboBox();
        activeFilterCondition.setFocusable(false);
        activeFilterCondition.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        activeFilterCondition.setRenderer(fc.getConditionRenderer());
        add(activeFilterCondition);
        activeFilterCondition.addItemListener(filterChangeListener);
        activeFilterCondition.addPropertyChangeListener(filterChangeListener);
        
        btnApply = new JToggleButton(new ApplyFilterAction());
        btnApply.setEnabled(false);
        add(btnApply);
        
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
        
        activeFilter = null;
        inactiveFilter = new DefaultFilter(null, false, false, false);
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
        if (isVisible())
        {
            if(activeFilter == null)
                activeFilter = new DefaultFilter(
                        getSelectedCondition(),
                        btnApply.getModel().isSelected(),
                        showAncestors.getModel().isSelected(),
                        showDescendants.getModel().isSelected()
                );
            fc.getMap().setFilter(activeFilter);
            return activeFilter;
        }
        fc.getMap().setFilter(inactiveFilter);
        return inactiveFilter;
    }
    
    /**
     * @return
     */
    public AbstractButton getBtnApply() {
        return btnApply;
    }
    
    FilterComposerDialog getFilterDialog() {
        return filterDialog;
    }

    /**
     * @param filter
     */
    void mapChanged(MindMap newMap) {
        Filter filter = newMap.getFilter();
        if((filter == null || filter == inactiveFilter) && btnApply.isSelected()){
            btnApply.doClick();            
        }
        else {            
            if(filter != activeFilter){
                activeFilter = filter;
                activeFilterCondition.setSelectedItem(filter);
                if(filter != null){
                    showAncestors.setSelected(filter.areAncestorsShown());
                    showDescendants.setSelected(filter.areDescendantsShown());
                }
                else{                    
                    showAncestors.setSelected(false);
                    showDescendants.setSelected(false);
                }
            }
            if(filter != null && ! btnApply.isSelected())
                btnApply.doClick();            
        }
    }

    private void refreshMap() {
        MindMap map = c.getModel();
        MindMapNode root = (MindMapNode)map.getRoot();
        root.getViewer().invalidateDescendantsTreeGeometries();         
        map.nodeRefresh(root);
    }
}
