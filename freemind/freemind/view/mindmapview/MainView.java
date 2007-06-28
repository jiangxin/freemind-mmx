/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2007  Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
/* $Id: MainView.java,v 1.1.4.8 2007-06-28 21:53:05 dpolivaev Exp $ */
package freemind.view.mindmapview;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import freemind.controller.Controller;
import freemind.controller.MenuBar;
import freemind.main.HtmlTools;
import freemind.main.Resources;
import freemind.main.Tools;

public abstract class MainView extends JLabel{
    static Dimension minimumSize = new Dimension(0, 0);
    static Dimension maximumSize = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        private static boolean NEED_PREF_SIZE_BUG_FIX = Controller.JAVA_VERSION.compareTo("1.5.0") < 0;
        private static final int MIN_HOR_NODE_SIZE = 10;
        static private boolean isPrinting = false;
        
        int getZoomedFoldingSymbolHalfWidth(){
            return getNodeView().getZoomedFoldingSymbolHalfWidth();
        }
        
        MainView(){
            isPainting = false;
            setAlignmentX(NodeView.CENTER_ALIGNMENT);
            setHorizontalAlignment(CENTER);
            setVerticalAlignment(CENTER);
        }
        public Dimension getMinimumSize() {
            return minimumSize;
        }
        
        public Dimension getMaximumSize() {
            return maximumSize;
        }
        
        private boolean isPainting;
        
        public NodeView getNodeView(){
            return (NodeView)SwingUtilities.getAncestorOfClass(NodeView.class, this);
        }
        
        /* (non-Javadoc)
         * @see javax.swing.JComponent#getPreferredSize()
         */
        public Dimension getPreferredSize() {
            final String text = getText();
			boolean isEmpty = text.length() == 0 || HtmlTools.isHtmlNode(text) && HtmlTools.htmlToPlain(text).length() == 0;
            if(isEmpty){
                setText("!");
            }
            Dimension prefSize = super.getPreferredSize();
            final float zoom = getNodeView().getMap().getZoom();
            if(zoom != 1F){
                prefSize.width = (int)(0.99 + prefSize.width * zoom);
                prefSize.height= (int)(0.99 + prefSize.height *zoom);
            }
            
            if(getNodeView().getMap().isCurrentlyPrinting() && NEED_PREF_SIZE_BUG_FIX) {
                prefSize.width += getNodeView().getMap().getZoomed(10);
            }
            prefSize.width = Math.max(getNodeView().getMap().getZoomed(MIN_HOR_NODE_SIZE), prefSize.width);
            if (isEmpty){
                setText("");
            }
             prefSize.width += getNodeView().getMap().getZoomed(4);
             prefSize.height += getNodeView().getMap().getZoomed(4);
             return prefSize;
        }
        
        /* (non-Javadoc)
         * @see javax.swing.JComponent#paint(java.awt.Graphics)
         */
        public void paint(Graphics g) {
        	Color backupTextColor = null; 
        	if (NodeView.standardChangeColorForSelection && getNodeView().isSelected() && ! isPrinting){
        		backupTextColor = getForeground();
        		setForeground(NodeView.standardSelectTextColor);
        	}
            float zoom = getZoom();
            if(zoom != 1F){
                Graphics2D g2 = (Graphics2D)g;
                final AffineTransform transform = g2.getTransform();                
                g2.scale(zoom, zoom);
                isPainting = true;
                super.paint(g);
                isPainting = false;
                g2.setTransform(transform);
            }
            else{
                super.paint(g);
            }
            if(backupTextColor != null){
            	setForeground(backupTextColor);
            }
        }

        private float getZoom() {
            float zoom = getNodeView().getMap().getZoom();
            
            // Dimitry: workaround because of j2se can not calculate font size properly
            // in case of small zoom values.
            // This work around does not help in case of html nodes,
            // thats why text may be truncated there. 
            if(zoom < 1 && getClientProperty("html") == null){
                int w = super.getWidth();
                int charCount = getText().length();
                w *= zoom;
                if(charCount > 0 && charCount < w){
                    zoom *= w;
                    zoom /= (w + charCount);
                }
            }
            return zoom;
        }   
        protected void printComponent(Graphics g){
            super.paintComponent(g);
        }
        public void paintSelected(Graphics2D graphics) {
            if (NodeView.standardChangeColorForSelection && getNodeView().isSelected() && ! isPrinting) {
                paintBackground(graphics, getNodeView().getSelectedColor());
            } else if (getNodeView().getModel().getBackgroundColor() != null) {
                paintBackground(graphics, getNodeView().getModel().getBackgroundColor());
            }
        }

        protected void paintBackground(Graphics2D graphics, Color color) {
            graphics.setColor(color);
            graphics.fillRect(0, 0, getWidth()-1, getHeight()-1);
        }


       public void paintDragOver(Graphics2D graphics) {
            if (isDraggedOver == NodeView.DRAGGED_OVER_SON) {
               if (getNodeView().isLeft()) {
                  graphics.setPaint(
                          new GradientPaint(
                                  getWidth()*3/4,
                                  0,
                                  getNodeView().getMap().getBackground(),
                                  getWidth()/4,
                                  0,
                                  NodeView.dragColor));
                  graphics.fillRect(
                          0,
                          0,
                          getWidth()*3/4,
                          getHeight()-1); }
               else {
                  graphics.setPaint(
                          new GradientPaint(
                                  getWidth()/4,
                                  0,
                                  getNodeView().getMap().getBackground(),
                                  getWidth()*3/4,
                                  0,
                                  NodeView.dragColor)
                                  );
                  graphics.fillRect(
                          getWidth()/4,
                          0, getWidth()-1,
                          getHeight()-1);
                  }
        }

            if (isDraggedOver == NodeView.DRAGGED_OVER_SIBLING) {
                graphics.setPaint(
                        new GradientPaint(
                                0,
                                getHeight()*3/5,
                                getNodeView().getMap().getBackground(),
                                0,
                                getHeight()/5,
                                NodeView.dragColor)
                                );
                graphics.fillRect(
                        0,
                        0,
                        getWidth()-1,
                        getHeight()-1);
        }
        }

       public void print(Graphics g) {
           isPrinting = true;
           super.print(g);
           isPrinting = false;
       }
        
        /* (non-Javadoc)
         * @see javax.swing.JComponent#getHeight()
         */
        public int getHeight() {
            if(isPainting){
                final float zoom = getZoom();
                if(zoom != 1F){
                    return (int)(super.getHeight()/zoom);
                }
            }
            return super.getHeight();
        }
        /* (non-Javadoc)
         * @see javax.swing.JComponent#getWidth()
         */
        public int getWidth() {
            if(isPainting){
                final float zoom = getZoom();
                if(zoom != 1F){
                    return (int)(0.99f+super.getWidth()/zoom);
                }
            }
            return super.getWidth();
        }
        /* (non-Javadoc)
         * @see javax.swing.JComponent#processKeyBinding(javax.swing.KeyStroke, java.awt.event.KeyEvent, int, boolean)
         */
        protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
            if (super.processKeyBinding(ks, e, condition, pressed))
                return true;
            // try key bindings of the menu bar even if the menu bar is not visible
            final MenuBar freeMindMenuBar = getNodeView().getMap().getController().getFrame().getFreeMindMenuBar();
            return ! freeMindMenuBar.isVisible() && freeMindMenuBar.processKeyBinding(ks, e, JComponent.WHEN_IN_FOCUSED_WINDOW, pressed); 
        }
        
        abstract Point getCenterPoint() ;
        
        /** get x coordinate including folding symbol */
        public int getDeltaX()
        {
            return 0;
        }

        /** get y coordinate including folding symbol */
        public int getDeltaY()
        {
            return 0;
        }

        /** get height including folding symbol */
        protected int getMainViewHeightWithFoldingMark()
        {
            return getHeight();
        }
        
        /** get width including folding symbol */
        protected int getMainViewWidthWithFoldingMark()
        {
            return getWidth();
        }

        protected void convertPointToMap(Point p){
            Tools.convertPointToAncestor(this, p, getNodeView().getMap());
        }
        
        protected void convertPointFromMap(Point p){
            Tools.convertPointFromAncestor(getNodeView().getMap(), p, this);            
        }
        
        protected int isDraggedOver = NodeView.DRAGGED_OVER_NO;
        public void setDraggedOver(int draggedOver) {
           isDraggedOver = draggedOver; }
        public void setDraggedOver(Point p) {
           setDraggedOver( (dropAsSibling(p.getX())) ? NodeView.DRAGGED_OVER_SIBLING : NodeView.DRAGGED_OVER_SON) ; }
        public int getDraggedOver() {
           return isDraggedOver; }

        public boolean dropAsSibling(double xCoord) {
            return isInVerticalRegion(xCoord, 1./3);
         }

        /** @return true if should be on the left, false otherwise. */
        public boolean dropPosition (double xCoord) {
            /* here it is the same as me. */
           return getNodeView().isLeft();
        }

        /** Determines whether or not the xCoord is in the part p of the node:
         *  if node is on the left: part [1-p,1]
         *  if node is on the right: part[  0,p] of the total width.
         */
        public boolean isInVerticalRegion(double xCoord, double p) {
            return getNodeView().isLeft() ?
               xCoord > getSize().width*(1.0-p) :
               xCoord < getSize().width*p;
         }
        abstract  String getStyle();
        abstract int getAlignment();

        public int getTextWidth() {
            return getWidth()-getIconWidth();
        }
        
        public int getTextX() {
            return getNodeView().isLeft() && ! getNodeView().isRoot() ? 0 :getIconWidth();
        }
        protected int getIconWidth() {
            final Icon icon = getIcon();
            if(icon == null){
                return 0;
            }
            return getNodeView().getMap().getZoomed(icon.getIconWidth());
        }
        
        void paintFoldingMark(Graphics2D g, Point p) {
            final int zoomedFoldingSymbolHalfWidth = getZoomedFoldingSymbolHalfWidth();
            p = SwingUtilities.convertPoint(this, p, getNodeView());
            p.translate(-zoomedFoldingSymbolHalfWidth, - zoomedFoldingSymbolHalfWidth);
            final Color color = g.getColor();
            g.setColor(Color.WHITE);
            g.fillOval(p.x , p.y , zoomedFoldingSymbolHalfWidth * 2, zoomedFoldingSymbolHalfWidth * 2);
            g.setColor(color);
            g.drawOval(p.x , p.y , zoomedFoldingSymbolHalfWidth * 2, zoomedFoldingSymbolHalfWidth * 2);
        }
}