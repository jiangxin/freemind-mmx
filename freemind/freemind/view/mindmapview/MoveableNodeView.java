/*
 * Created on 07.04.2005
 */
package freemind.view.mindmapview;

import java.awt.Dimension;

import javax.swing.JLabel;

import freemind.modes.MindMapNode;
import freemind.view.mindmapview.attributeview.AttributeView;

/**
 * @author Dimitri
 */
public abstract class MoveableNodeView extends NodeView {

	private final int LISTENER_VIEW_WIDTH = 10; 
	private NodeMotionListenerView motionListenerView;
	
	protected MoveableNodeView(MindMapNode model, MapView map) {
		super(model, map);
		motionListenerView = new NodeMotionListenerView(this);
	}
	public NodeMotionListenerView getMotionListenerView() {
		return motionListenerView;
	}

    protected void addToMap(){
    	map.add(this);
    	map.add(getMotionListenerView());
    }
    
    protected void removeFromMap(){
    	map.remove(this);
    	map.remove(getMotionListenerView());
    }

	public void setBounds(int x,	int y){
	    super.setBounds(x, y);
		Dimension prefSize = getPreferredSize();
		int motionListenerViewX 
		  = isLeft() ? x + prefSize.width : x-LISTENER_VIEW_WIDTH;
		motionListenerView.setLocation(motionListenerViewX, y);
		motionListenerView.setSize(LISTENER_VIEW_WIDTH, prefSize.height);
	}

	    public void setVisible(boolean isVisible) {
        super.setVisible(isVisible);
        motionListenerView.setVisible(isVisible);
    }
}
