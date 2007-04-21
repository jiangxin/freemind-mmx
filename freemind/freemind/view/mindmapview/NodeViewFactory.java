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
/* $Id: NodeViewFactory.java,v 1.1.4.2 2007-04-21 15:11:23 dpolivaev Exp $ */
package freemind.view.mindmapview;

import java.awt.Container;

import javax.swing.Box;
import javax.swing.JComponent;

import freemind.modes.EdgeAdapter;
import freemind.modes.MindMapNode;

class NodeViewFactory {

    private static NodeViewFactory factory;
    private EdgeView sharpBezierEdgeView;
    private EdgeView sharpLinearEdgeView;
    private EdgeView bezierEdgeView;
    private EdgeView linearEdgeView;

    //Singleton
    private NodeViewFactory(){
        
    }
    
    
    static NodeViewFactory getInstance(){
        if(factory == null){
            factory = new NodeViewFactory();
        }
        return factory;
    }
    
    EdgeView getEdge(NodeView newView) {
        if (newView.getModel().getEdge().getStyle().equals(EdgeAdapter.EDGESTYLE_LINEAR)) {
            return getLinearEdgeView();
        } else if (newView.getModel().getEdge().getStyle().equals(EdgeAdapter.EDGESTYLE_BEZIER)) {
            return getBezierEdgeView();
        } else if (newView.getModel().getEdge().getStyle().equals(EdgeAdapter.EDGESTYLE_SHARP_LINEAR)) {
            return getSharpEdgeView();
        } else if (newView.getModel().getEdge().getStyle().equals(EdgeAdapter.EDGESTYLE_SHARP_BEZIER)) {
            return getSharpBezierEdgeView();
        } else {
            System.err.println("Unknown Edge Type.");
            return getLinearEdgeView();
        }
    }

    private EdgeView getSharpBezierEdgeView() {
        if(sharpBezierEdgeView == null){
            sharpBezierEdgeView = new SharpBezierEdgeView();
        }
        return sharpBezierEdgeView;
    }

    private EdgeView getSharpEdgeView() {
        if(sharpLinearEdgeView == null){
            sharpLinearEdgeView = new SharpLinearEdgeView();
        }
        return sharpLinearEdgeView;
    }

    private EdgeView getBezierEdgeView() {
        if(bezierEdgeView == null){
            bezierEdgeView = new BezierEdgeView();
        }
        return bezierEdgeView;
    }

    private EdgeView getLinearEdgeView() {
        if(linearEdgeView == null){
            linearEdgeView = new LinearEdgeView();
        }
        return linearEdgeView;
    }


    /**
     * Factory method which creates the right NodeView for the model.
     */
    NodeView newNodeView(MindMapNode model, int position, MapView map, Container parent) {
        NodeView newView = new NodeView( model, position, map, parent);
        final MainView mainView;
        if (model.isRoot()) {
            mainView = new RootMainView(); 
            newView.setMainView(mainView);
            newView.setLayout(VerticalRootNodeViewLayout.getInstance());
            
        } else { 
            if (model.getStyle().equals(MindMapNode.STYLE_FORK) ){
                mainView = new ForkMainView(); 
                newView.setMainView(mainView);
            }
            else if (model.getStyle().equals(MindMapNode.STYLE_BUBBLE)) {
                mainView = new BubbleMainView(); 
                newView.setMainView(mainView);
            }
            else {
                System.err.println("Tried to create a NodeView of unknown Style.");
                mainView = new ForkMainView(); 
                newView.setMainView(mainView);
            }
            if(newView.isLeft()){
                newView.setLayout(LeftNodeViewLayout.getInstance());
            }
            else{
                newView.setLayout(RightNodeViewLayout.getInstance());
            }
        }
        
        model.addViewer(newView);
        newView.update();
        return newView;
    }


    JComponent newContentPane(NodeView view) {
        return Box.createVerticalBox();
    }
}
