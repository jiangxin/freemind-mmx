/*
 * Created on 10.07.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.controller.attributes;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;

import freemind.controller.Controller;
import freemind.modes.MapRegistry;
import freemind.modes.attributes.AttributeRegistryTableModel;

/**
 * @author Dimitri Polivaev
 * 10.07.2005
 */
public class AttributeDialog extends JDialog {
    private JTable view;
    private MapRegistry registry;
    private AttributeRegistryTableModel model;

    private class CloseAction extends AbstractAction{
        CloseAction(){
            super(Controller.getInstance().getResourceString("attributes_close"));
        }
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            setVisible(false);
        }
        
    }

    private class ApplyAction extends AbstractAction{
        ApplyAction(){
            super(Controller.getInstance().getResourceString("attributes_apply"));
        }
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            model.fireVisibilityChanged();
        }
        
    }

    private class OKAction extends AbstractAction{
        OKAction(){
            super(Controller.getInstance().getResourceString("attributes_okey"));
        }
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            model.fireVisibilityChanged();
            setVisible(false);
        }
        
    }

    private class RefreshAction extends AbstractAction{
        RefreshAction(){
            super(Controller.getInstance().getResourceString("attributes_refresh"));
        }
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            registry.refresh();
        }
    }            
    public AttributeDialog(){
        super(Controller.getInstance().getJFrame());
        
        view = new JTable();
        registry = Controller.getInstance().getMapModuleManager().getMapModule().getModel().getRegistry();
        model = registry.getAttributes();
        view.setModel(model);
        view.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        view.getTableHeader().getColumnModel().getColumn(0).setHeaderValue(Controller.getInstance().getResourceString("attributes_attribute"));
        view.getTableHeader().getColumnModel().getColumn(1).setHeaderValue(Controller.getInstance().getResourceString("attributes_visible"));
        JScrollPane scrollPane = new JScrollPane(view);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        
        final JToolBar buttons = new JToolBar();
        
        buttons.setOrientation(JToolBar.HORIZONTAL);
        buttons.setFloatable(false);
        getContentPane().add(buttons, BorderLayout.SOUTH);
        
        buttons.add(new OKAction());
        buttons.add(new ApplyAction());
        buttons.add(new CloseAction());
        buttons.add(new RefreshAction());
        
    }

}
