/*
 * Created on 08.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package accessories.plugins;

import java.io.File;

import freemind.extensions.ModeControllerHookAdapter;
import freemind.modes.MindMap;
import freemind.modes.ModeController;
import freemind.modes.mindmapmode.MindMapController;
import accessories.plugins.util.xslt.ExportDialog;
/**
 * @author foltin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ExportWithXSLT extends ModeControllerHookAdapter {

	/**
	 * 
	 */
	public ExportWithXSLT() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.MindMapHook#startupMapHook()
	 */
	public void startupMapHook() {
		super.startupMapHook();
		ModeController mc = getController();
		MindMap model = getController().getMap();
		 if(model == null) 
			 return; // there may be no map open
		 if((model.getFile() == null) || model.isReadOnly()) {
			if(mc.save()) {
				export(model.getFile());
				return;
			}
			else
				return;
		 }
		 else
			 export(model.getFile());
	}
	private void export(File file) {
		 ExportDialog exp = new ExportDialog(file);
		 exp.setVisible(true);

	}

}
