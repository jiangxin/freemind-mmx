/*
 * Created on 29.02.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package freemind.extensions;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.swing.JMenu;

import freemind.modes.MindMap;
import freemind.modes.ControllerAdapter;
import freemind.modes.ModeController;

/**
 * @author foltin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ModeControllerHookAdapter extends HookAdapter implements ModeControllerHook {


	// Logging: 
	private static java.util.logging.Logger logger;

	/**
	 * @param map
	 * @param controller
	 */
	public ModeControllerHookAdapter(ModeController controller) {
		super(controller);
		if(logger == null)
			logger = ((ControllerAdapter)getController()).getFrame().getLogger(this.getClass().getName());
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.ModeControllerHook#enableActions(boolean)
	 */
	public void enableActions(boolean enable) {
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.ModeControllerHook#fileMenuHook(javax.swing.JMenu)
	 */
	public void fileMenuHook(JMenu fileMenu) {
        logger.info("fileMenuHook");
	}

}
