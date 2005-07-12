/*
 * Created on 05.06.2005
 *
 */
package freemind.view.mindmapview;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

/**
 * @author dimitri
 * 05.06.2005
 */
public class NodeViewLayoutManager implements LayoutManager {
    private NodeView nodeView(Container c)
    {
        return (NodeView)c;
    };
    
    protected NodeViewLayoutManager(){
        
    }
    
    
    static private NodeViewLayoutManager instance = null;
    private Dimension minDimension ;
    
    public void addLayoutComponent(String arg0, Component arg1) {
    }

    /* (non-Javadoc)
     * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
     */
    public void removeLayoutComponent(Component arg0) {
    }

     private Dimension getMainViewPreferredSize(Container c) {
        return nodeView(c).getMainViewPreferredSize();
    }

    private Dimension getAttributeViewPreferredSize(Container c) {        
        JScrollPane attributeView = nodeView(c).syncronizeAttributeView();
        Dimension attributeViewPreferredSize = attributeView.getPreferredSize();
        return attributeViewPreferredSize;
    }

    /* (non-Javadoc)
     * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
     */
    public Dimension minimumLayoutSize(Container arg0) {
        if (minDimension == null) 
            minDimension= new Dimension(0,0);
        return minDimension;
    }

    /* (non-Javadoc)
     * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
     */
    /* (non-Javadoc)
     * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
     */
    public Dimension preferredLayoutSize(Container c) {
        Dimension mainViewPreferredSize = getMainViewPreferredSize(c);
        final NodeView nodeView = nodeView(c);
        nodeView.syncronizeAttributeView();
        if (nodeView.areAttributesVisible()){
            Dimension attributeViewPreferredSize = getAttributeViewPreferredSize(c);
            return new Dimension(Math.max(mainViewPreferredSize.width, attributeViewPreferredSize.width),
                    mainViewPreferredSize.height + attributeViewPreferredSize.height);
        }
        else
            return mainViewPreferredSize;
    }

   public void layoutContainer(Container c) {        
        final NodeView nodeView = nodeView(c);
        int deltaX = nodeView.getDeltaX();
        int deltaY = nodeView.getDeltaY();
        Dimension mainViewPreferredSize = getMainViewPreferredSize(c);
        int w = mainViewPreferredSize.width;
        JScrollPane attributeView = nodeView.syncronizeAttributeView();
        if (nodeView(c).areAttributesVisible()){
            Dimension attributesViewPreferredSize = getAttributeViewPreferredSize(c);
            w = Math.max(mainViewPreferredSize.width, attributesViewPreferredSize.width);
            attributeView.setVisible(true);
            attributeView.setBounds(deltaX + (w - attributesViewPreferredSize.width)/2, deltaY + mainViewPreferredSize.height , attributesViewPreferredSize.width, attributesViewPreferredSize.height) ;
        }
        else{
            if(attributeView != null)
                attributeView.setVisible(false);
        }
        if (nodeView.isLeft()){
            nodeView.getMainView().setBounds(deltaX, deltaY , w, mainViewPreferredSize.height) ;
        }
        else{
            nodeView.getMainView().setBounds(deltaX, deltaY , w, mainViewPreferredSize.height) ;
        }
    }

    static NodeViewLayoutManager getInstance() {
        if (instance == null) instance = new NodeViewLayoutManager();
        return instance;
    }
}
