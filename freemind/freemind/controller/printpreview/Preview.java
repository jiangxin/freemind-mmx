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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;

import javax.swing.JComponent;

import freemind.view.mindmapview.MapView;

class Preview extends JComponent {
    private final static int DEFAULT_PREVIEW_SIZE = 300;
    private final static double MINIMUM_ZOOM_FACTOR = 0.1;
    private BufferedImage previewPageImage = null;
    private Graphics2D imageGraphics; 
    
    public Preview(MapView view, double zoom) {
        this.view = view;
        PageFormat format = getPageFormat();
        if (zoom == 0.0) {
            if (format.getOrientation() == PageFormat.PORTRAIT)
                this.zoom = DEFAULT_PREVIEW_SIZE / format.getHeight();
            else 
                this.zoom = DEFAULT_PREVIEW_SIZE / format.getWidth();
        } else
            this.zoom = zoom;
        resize();
    }
    
    protected void paintPaper(Graphics g, PageFormat format) {
        g.setColor(Color.white);
        g.fillRect(0, 0, getPageWidth(format), getPageHeight(format));        
        g.setColor(Color.black);
        g.drawRect(0, 0, getPageWidth(format) - 1, getPageHeight(format) - 1);
    }
    
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        PageFormat format = getPageFormat();   
        paintPaper(g, format);
        if(previewPageImage == null){
            previewPageImage = (BufferedImage)createImage(getPageWidth(format) - 1, getPageHeight(format) - 1);
            imageGraphics = previewPageImage.createGraphics();
            imageGraphics.scale(zoom, zoom);
            view.preparePrinting();
            while(Printable.NO_SUCH_PAGE == view.print(imageGraphics, format, index) && index > 0){
                index -= 1;
            }
            view.endPrinting();
        }
        g2d.drawImage(previewPageImage, 0, 0, this);
    }

    private int getPageHeight(PageFormat format) {
        return (int)(format.getHeight()*zoom);
    }

    private int getPageWidth(PageFormat format) {
        return (int)(format.getWidth()*zoom);
    }
    
    public void moveIndex(int indexStep) {
        int newIndex = index + indexStep;
        if(newIndex >= 0){
            index = newIndex;
            previewPageImage = null;
        }
    }
    
    public void changeZoom(double zoom) {
        this.zoom = Math.max(MINIMUM_ZOOM_FACTOR, this.zoom + zoom);
        resize();
    }
    
    public void resize() {
        int size = (int)Math.max(getPageFormat().getWidth() * zoom, getPageFormat().getHeight() * zoom);
        setPreferredSize(new Dimension(size, size));
        previewPageImage = null;
        revalidate();
    }
    
    private PageFormat getPageFormat() {        
        return view.getController().getPageFormat();
    }

    public Dimension getMinimumSize() {
        return getPreferredSize();
    }
    
    protected MapView view;
    protected int index = 0;
    protected double zoom = 0.0;

    public int getPageIndex() {
        return index;        
    }
}
