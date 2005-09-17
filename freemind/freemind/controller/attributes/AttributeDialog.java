/*
 * Created on 10.07.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.controller.attributes;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import freemind.main.Resources;
import freemind.modes.MapRegistry;
import freemind.modes.MindMap;
import freemind.modes.attributes.AttributeRegistryTableModel;

/**
 * @author Dimitri Polivaev
 * 10.07.2005
 */
public class AttributeDialog extends JDialog {
    private JTable view;
    private MapRegistry registry;
    private AttributeRegistryTableModel model;

    private class SelectAllAction extends AbstractAction{
        SelectAllAction(){
            super(Resources.getInstance().getResourceString("attributes_select_all"));
        }
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            for(int i = 0; i < model.getRowCount(); i++){
                model.setValueAt(Boolean.TRUE, i, 1);
            }
        }

    }

    private class DeselectAllAction extends AbstractAction{
        DeselectAllAction(){
            super(Resources.getInstance().getResourceString("attributes_deselect_all"));
        }
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {            
            for(int i = 0; i < model.getRowCount(); i++){
                model.setValueAt(Boolean.FALSE, i, 1);
            }
        }

    }

    private class CloseAction extends AbstractAction{
        CloseAction(){
            super(Resources.getInstance().getResourceString("attributes_close"));
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
            super(Resources.getInstance().getResourceString("attributes_apply"));
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
            super(Resources.getInstance().getResourceString("attributes_okey"));
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
            super(Resources.getInstance().getResourceString("attributes_refresh"));
        }
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            registry.refresh();
        }
    }
    public AttributeDialog(MindMap map){
        super(Resources.getInstance().getJFrame());

        view = new JTable();
        registry = map.getRegistry();
        model = registry.getAttributes();
        view.setModel(model);
        view.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        view.getTableHeader().setReorderingAllowed(false);

        final Box northButtons = Box.createHorizontalBox();

        getContentPane().add(northButtons, BorderLayout.NORTH);
        northButtons.add(Box.createHorizontalGlue());
        JButton selectAll = new JButton(new SelectAllAction());
        northButtons.add(selectAll);
        northButtons.add(Box.createHorizontalGlue());
        JButton deselectAll = new JButton(new DeselectAllAction());
        northButtons.add(deselectAll);
        northButtons.add(Box.createHorizontalGlue());

        JScrollPane scrollPane = new JScrollPane(view);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        final Box southButtons = Box.createHorizontalBox();

        getContentPane().add(southButtons, BorderLayout.SOUTH);
        southButtons.add(Box.createHorizontalGlue());
        JButton ok = new JButton(new OKAction());
        southButtons.add(ok);
        southButtons.add(Box.createHorizontalGlue());
        JButton apply = new JButton(new ApplyAction());
        southButtons.add(apply);
        southButtons.add(Box.createHorizontalGlue());
        JButton close = new JButton(new CloseAction());
        southButtons.add(close);
        southButtons.add(Box.createHorizontalGlue());
        JButton refresh = new JButton(new RefreshAction());
        southButtons.add(refresh);
        southButtons.add(Box.createHorizontalGlue());

    }
    public void mapChanged(MindMap map){
        registry = map.getRegistry();
        view.setModel(registry.getAttributes());
    }
}
