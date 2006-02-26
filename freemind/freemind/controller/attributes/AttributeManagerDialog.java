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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
public class AttributeManagerDialog extends JDialog {
    private JTable view;
    private MapRegistry registry;
    private AttributeRegistry model;
    private static final String[] fontSizes = {"6","8","10","12","14","16","18","20","24"};
    private JComboBox size;
    private ImportAttributesDialog importDialog = null;
    static final Icon editButtonImage = new ImageIcon(Resources.getInstance().getResource("images/edit12.png"));
    
    private class ApplyAction extends AbstractAction{
        ApplyAction(){
            super(Resources.getInstance().getResourceString("apply"));
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
        model.getAttributeController().performSetFontSize(model, iSize);
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
    
    private class ImportAction extends AbstractAction{
        ImportAction(){
            super(Resources.getInstance().getResourceString("attributes_import"));
        }
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            if(importDialog == null){
                importDialog = new ImportAttributesDialog(AttributeManagerDialog.this);
            }
            importDialog.setVisible(true);
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
        private String title;
        private String labelText;       
        public void actionPerformed(ActionEvent e) {
            ListDialog.showDialog(
                    (Component)e.getSource(),
                    AttributeManagerDialog.this,
                    labelText,
                    title,
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
        public void setListBoxModel(String title, String labelText, SortedListModel listBoxModel) {
            this.title = title;
            this.labelText = labelText;
            this.listBoxModel = listBoxModel;
        }
    }
    
    private class ClosingListener extends WindowAdapter{

        public void windowClosing(WindowEvent e) {
            resetChanges();
            super.windowClosing(e);
            setVisible(false);
        }
        
    }

    public AttributeManagerDialog(MindMap map){
        super(Resources.getInstance().getJFrame(), Resources.getInstance().getResourceString("attributes_dialog"), true);

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
                model.setAttributeLayoutChanged();
            }
    	    
    	});
    	size.setToolTipText(Resources.getInstance().getResourceString("attribute_font_size_tooltip"));
    	southButtons.add(size);
        southButtons.add(Box.createHorizontalGlue());
        JButton importBtn = new JButton(new ImportAction());
        importBtn.setToolTipText(Resources.getInstance().getResourceString("attributes_import_tooltip"));
        southButtons.add(importBtn);
        southButtons.add(Box.createHorizontalGlue());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); 
        addWindowListener(new ClosingListener());
    	
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
