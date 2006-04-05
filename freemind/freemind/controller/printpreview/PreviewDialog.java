/*
 *  Preview Dialog - A Preview Dialog for your Swing Applications
 *
 *  Copyright (C) 2003 Jens Kaiser.
 *  Copyright (C) 2006 Dimitri Polivaev.
 *
 *  Written by: 2003 Jens Kaiser <jens.kaiser@web.de>
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package freemind.controller.printpreview;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import freemind.view.mindmapview.MapView;

public class PreviewDialog extends JDialog implements ActionListener {
    private final static double DEFAULT_ZOOM_FACTOR_STEP = 0.1;
    private JLabel pageNumber;

    public PreviewDialog(String title, MapView view) {
        super(JOptionPane.getFrameForComponent(view), title, true);
        this.view = view;        
        Preview preview = new Preview(view, 1);
        JScrollPane scrollPane = new JScrollPane(preview);
        getContentPane().add(scrollPane, "Center");
        JToolBar toolbar = new JToolBar();
        //toolbar.setRollover(true);
        getContentPane().add(toolbar, "North");
        pageNumber = new JLabel("1");
        final JButton button = getButton("Back24.gif", new BrowseAction(preview, pageNumber, -1));
        toolbar.add(button);
        pageNumber.setPreferredSize(button.getPreferredSize());
        toolbar.add(pageNumber);
        toolbar.add(getButton("Forward24.gif", new BrowseAction(preview, pageNumber,1)));
        toolbar.add(new JToolBar.Separator());
        toolbar.add(getButton("ZoomIn24.gif", new ZoomAction(preview, DEFAULT_ZOOM_FACTOR_STEP))); 
        toolbar.add(getButton("ZoomOut24.gif", new ZoomAction(preview, -DEFAULT_ZOOM_FACTOR_STEP))); 
        toolbar.add(new JToolBar.Separator());
        JPanel dialog = new JPanel();
        dialog.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("OK");
        ok.addActionListener(this);
        dialog.add(ok);
        getContentPane().add(dialog, "South"); 
    }
    

    private JButton getButton(String iconName) {
        return getButton(null, iconName, null);
    }

    private JButton getButton(String iconName, AbstractAction action) {
        return getButton(null, iconName, action);
    }
    
    private JButton getButton(String name, String iconName, AbstractAction action) {
        JButton result = null;

        ImageIcon icon = null;        
        URL imageURL = getClass().getClassLoader().getResource("images/" + iconName);
        if (imageURL != null)
            icon = new ImageIcon(imageURL);

        if (action != null) {
            if (icon != null) action.putValue(Action.SMALL_ICON, new ImageIcon(imageURL));
            if (name != null) action.putValue(Action.NAME, name);    
            result = new JButton(action);
        } else 
            result = new JButton(name, icon);
        
        return result;
    }
    
    public void actionPerformed(ActionEvent e) {
        dispose();
    }
    
    protected MapView view;
}
