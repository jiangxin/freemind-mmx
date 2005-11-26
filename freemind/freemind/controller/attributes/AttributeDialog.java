/*
 * Created on 10.07.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.controller.attributes;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import freemind.controller.filter.util.SortedListModel;
import freemind.main.Resources;
import freemind.modes.MapRegistry;
import freemind.modes.MindMap;
import freemind.modes.attributes.AttributeRegistry;

/**
 * @author Dimitri Polivaev
 * 10.07.2005
 */
public class AttributeDialog extends JDialog {
    private JTable view;
    private MapRegistry registry;
    private AttributeRegistry model;
    private static final String[] fontSizes = {"6","8","10","12","14","16","18","20","24"};
    private JComboBox size;
    static final Icon editButtonImage = new ImageIcon(Resources.getInstance().getResource("images/edit12.png"));
    
    private class ApplyAction extends AbstractAction{
        ApplyAction(){
            super(Resources.getInstance().getResourceString("attributes_apply"));
        }
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            applyChanges();
        }

    }
    private void applyChanges() {
        Object size = this.size.getSelectedItem();
        int iSize = Integer.parseInt(size.toString());
        model.setFontSize(iSize);
        model.applyChanges();
    }

    private void resetChanges() {        
        int iSize = model.getFontSize();
        size.setSelectedItem(Integer.toString(iSize));
        model.resetChanges();
    }

    private class OKAction extends AbstractAction{
        OKAction(){
            super(Resources.getInstance().getResourceString("ok"));
        }
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            applyChanges();
            setVisible(false);
        }
    }

    private class CancelAction extends AbstractAction{
        CancelAction(){
            super(Resources.getInstance().getResourceString("cancel"));
        }
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            resetChanges();
            setVisible(false);
        }
    }

//    private class RefreshAction extends AbstractAction{
//        RefreshAction(){
//            super(Resources.getInstance().getResourceString("attributes_refresh"));
//        }
//        /* (non-Javadoc)
//         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
//         */
//        public void actionPerformed(ActionEvent e) {
//            registry.refresh();
//        }
//    }
    
    class EditListAction extends AbstractAction{
        public EditListAction() {
            super("", editButtonImage);
        }
        private int row = 0;
        private SortedListModel listBoxModel;        
        public void actionPerformed(ActionEvent e) {
            ListDialog.showDialog(
                    (Component)e.getSource(),
                    AttributeDialog.this,
                    "labelText",
                    "Title",
                    listBoxModel,
                    "xxxxxxxxxxxxxxxxxxxxx"
                    );
        }
        
        public int getRow() {
            return row;
        }
        public void setRow(int row) {
            this.row = row;
        }
        public SortedListModel getListBoxModel() {
            return listBoxModel;
        }
        public void setListBoxModel(SortedListModel listBoxModel) {
            this.listBoxModel = listBoxModel;
        }
    }
    public AttributeDialog(MindMap map, String titel){
        super(Resources.getInstance().getJFrame(), titel, true);

        view = new AttributeRegistryTable(new EditListAction());
        registry = map.getRegistry();
        model = registry.getAttributes();
        view.setModel(model.getTableModel());
        view.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        view.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(view);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        final Box southButtons = Box.createHorizontalBox();
        southButtons.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        getContentPane().add(southButtons, BorderLayout.SOUTH);
        southButtons.add(Box.createHorizontalGlue());
        JButton ok = new JButton(new OKAction());
        southButtons.add(ok);
        southButtons.add(Box.createHorizontalGlue());
        JButton apply = new JButton(new ApplyAction());
        southButtons.add(apply);
        southButtons.add(Box.createHorizontalGlue());
        JButton cancel = new JButton(new CancelAction());
        southButtons.add(cancel);
        southButtons.add(Box.createHorizontalGlue());
//        JButton refresh = new JButton(new RefreshAction());
//        southButtons.add(refresh);
//        southButtons.add(Box.createHorizontalGlue());
    	size = new JComboBox(fontSizes);
    	size.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
                model.setVisibilityChanged();
            }
    	    
    	});
    	size.setToolTipText(Resources.getInstance().getResourceString("attribute_font_size_tooltip"));
    	southButtons.add(size);
        southButtons.add(Box.createHorizontalGlue());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); 
    	
    }
    public void mapChanged(MindMap map){
        registry = map.getRegistry();
        model = registry.getAttributes();
        view.setModel(registry.getAttributes().getTableModel());
    }
    public void setVisible(boolean b) {
        if(b){
            size.setSelectedItem(Integer.toString(model.getFontSize()));
        }
        super.setVisible(b);
    }
}
