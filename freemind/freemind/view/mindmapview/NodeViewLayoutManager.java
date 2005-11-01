/*
 * Created on 05.06.2005
 *
 */
package freemind.view.mindmapview;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

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
        Dimension attributeViewPreferredSize = c.getComponent(1).getPreferredSize();
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
        if (nodeView.getAttributeView().areAttributesVisible()){
            Dimension attributeViewPreferredSize = getAttributeViewPreferredSize(c);
            return new Dimension(Math.max(mainViewPreferredSize.width, attributeViewPreferredSize.width),
                    mainViewPreferredSize.height + attributeViewPreferredSize.height);
        }
        else
            return mainViewPreferredSize;
    }

      public void layoutContainer(Container c) {        
        final NodeView nodeView = nodeView(c);
        Dimension mainViewPreferredSize = getMainViewPreferredSize(c);
        int w = mainViewPreferredSize.width;
        if (nodeView(c).getAttributeView().areAttributesVisible()){
            Dimension attributesViewPreferredSize = getAttributeViewPreferredSize(c);
            w = Math.max(mainViewPreferredSize.width, attributesViewPreferredSize.width);
            Component attributeView = c.getComponent(1);
            c.getComponent(1).setVisible(true);
            attributeView.setBounds((w - attributesViewPreferredSize.width)/2, mainViewPreferredSize.height , attributesViewPreferredSize.width, attributesViewPreferredSize.height) ;
        }
        else{
            if(c.getComponentCount() >= 2){
                c.getComponent(1).setVisible(false);
            }
        }
        nodeView.getMainView().setBounds(0, 0 , w, mainViewPreferredSize.height) ;
    }

    static NodeViewLayoutManager getInstance() {
        if (instance == null) instance = new NodeViewLayoutManager();
        return instance;
    }
}
