/*
 * Created on 12.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package accessories.plugins;

import java.awt.Rectangle;

import freemind.extensions.ModeControllerHookAdapter;
import freemind.modes.NodeAdapter;
import freemind.view.mindmapview.MapView;

/**
 * @author foltin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FitToPage extends ModeControllerHookAdapter {

	/**
	 * 
	 */
	public FitToPage() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.MindMapHook#startupMapHook()
	 */
	public void startupMapHook() {
		super.startupMapHook();
		MapView view = getController().getView();
		 if(view == null)
			 return;
		NodeAdapter root = (NodeAdapter) getController().getMap().getRoot();
		Rectangle rect = view.getInnerBounds(root.getViewer());
		// calculate the zoom:
		double oldZoom = getController().getView().getZoom();
		double newZoom = 1;
		Rectangle viewer = view.getVisibleRect();
		Rectangle viewerTotal = view.getBounds();
		logger.info("Found viewer rect="+viewer+" (Total="+viewerTotal+") and inner bounds="+rect);
		double widthZoom = viewer.width*oldZoom / (rect.width+0.0);
		if( widthZoom < newZoom    ) {
			newZoom = widthZoom;
		}
		double heightZoom = viewer.height*oldZoom /(rect.height +0.0);
		if( heightZoom < newZoom ) {
			newZoom = heightZoom;
		}
		logger.info("Calculated new zoom from min("+widthZoom+","+heightZoom+")="+newZoom+", oldZoom="+oldZoom+", result ="+(newZoom*oldZoom));
		getController().getController().setZoom((float) (newZoom));
	}

}
