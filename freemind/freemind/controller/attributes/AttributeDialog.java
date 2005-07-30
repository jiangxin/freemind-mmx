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

        JScrollPane scrollPane = new JScrollPane(view);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        final Box buttons = Box.createHorizontalBox();

        getContentPane().add(buttons, BorderLayout.SOUTH);
        buttons.add(Box.createHorizontalGlue());
        JButton ok = new JButton(new OKAction());
        buttons.add(ok);
        buttons.add(Box.createHorizontalGlue());
        JButton apply = new JButton(new ApplyAction());
        buttons.add(apply);
        buttons.add(Box.createHorizontalGlue());
        JButton close = new JButton(new CloseAction());
        buttons.add(close);
        buttons.add(Box.createHorizontalGlue());
        JButton refresh = new JButton(new RefreshAction());
        buttons.add(refresh);
        buttons.add(Box.createHorizontalGlue());

    }
    public void mapChanged(MindMap map){
        registry = map.getRegistry();
        view.setModel(registry.getAttributes());
    }
}
