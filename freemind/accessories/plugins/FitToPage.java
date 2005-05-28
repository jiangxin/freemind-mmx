/*
 * Created on 12.03.2004
 *
 */
package accessories.plugins;

import java.awt.Rectangle;

import freemind.extensions.ModeControllerHookAdapter;
import freemind.modes.NodeAdapter;
import freemind.view.mindmapview.MapView;

/**
 * @author foltin
 * @author dimitri: Bug fixes.
 *
 */
public class FitToPage extends ModeControllerHookAdapter {

	private MapView view;

	/**
	 * 
	 */
	public FitToPage() {
		super();
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.MindMapHook#startupMapHook()
	 */
	public void startupMapHook() {
		super.startupMapHook();
		view = getController().getView();
		if (view == null)
			return;
		zoom();
		scroll();
	}

	private void scroll() {
		NodeAdapter root = (NodeAdapter) getController().getMap().getRoot();
		Rectangle rect = view.getInnerBounds(root.getViewer());
		Rectangle viewer = view.getVisibleRect();
		view.scrollBy(rect.x - viewer.x, rect.y - viewer.y, false);
	}

	private void zoom() {
		NodeAdapter root = (NodeAdapter) getController().getMap().getRoot();
		Rectangle rect = view.getInnerBounds(root.getViewer());
		// calculate the zoom:
		double oldZoom = getController().getView().getZoom();
		Rectangle viewer = view.getVisibleRect();
		logger.info(
			"Found viewer rect="
				+ viewer.height
				+ "/"
				+ rect.height
				+ ", "
				+ viewer.width
				+ "/"
				+ rect.width);
		double newZoom = viewer.width * oldZoom / (rect.width + 0.0);
		double heightZoom = viewer.height * oldZoom / (rect.height + 0.0);
		if (heightZoom < newZoom) {
			newZoom = heightZoom;
		}
		logger.info("Calculated new zoom " + (newZoom));
		getController().getController().setZoom((float) (newZoom));
	}

}
