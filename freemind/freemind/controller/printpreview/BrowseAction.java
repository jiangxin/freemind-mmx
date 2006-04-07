/*
 *  Preview Dialog - A Preview Dialog for your Swing Applications
 *
 *  Copyright (C) 2003 Jens Kaiser.
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

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JLabel;

class BrowseAction extends AbstractAction {
    private JLabel pageNumber;
    public BrowseAction(Preview preview, JLabel pageNumber, int pageStep) {
        super();
        this.preview = preview;
        this.pageStep = pageStep;
        this.pageNumber = pageNumber;
        pageIndexPainter = new Runnable(){
                    public void run() {
                        BrowseAction.this.pageNumber.setText(String.valueOf(1 + BrowseAction.this.preview.getPageIndex()));
                    }            
                };
    }
    
    public void actionPerformed(ActionEvent e) {
        pageNumber.setText(String.valueOf(preview.getPageIndex()));
        preview.moveIndex(pageStep);
        preview.repaint();
        EventQueue.invokeLater(pageIndexPainter);
    }
    
    protected Preview preview;
    protected int pageStep;
    private final Runnable pageIndexPainter;
}
