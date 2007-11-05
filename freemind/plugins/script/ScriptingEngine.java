/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 *
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
 *
 * Created on 02.09.2006
 */
/* $Id: ScriptingEngine.java,v 1.1.2.7 2007-11-05 21:43:20 christianfoltin Exp $ */
package plugins.script;

import java.util.Iterator;

import javax.swing.JOptionPane;

import freemind.common.OptionalDontShowMeAgainDialog;
import freemind.extensions.HookRegistration;
import freemind.main.FreeMind;
import freemind.main.Tools.BooleanHolder;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.attributes.NodeAttributeTableModel;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.hooks.MindMapHookAdapter;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 * @author foltin
 * 
 */
public class ScriptingEngine extends MindMapHookAdapter {
    public static class Registration implements HookRegistration{

        private final MindMapController controller;
        private final MindMap mMap;
        private final java.util.logging.Logger logger;

        public Registration(ModeController controller, MindMap map) {
            this.controller = (MindMapController) controller;
            mMap = map;
            logger = controller.getFrame().getLogger(this.getClass().getName());
        }
        
        public void register() {
//            controller.patterns[0].registerExternalPatternAction(this);
        }

        public void deRegister() {
        }

    }
    

	public void startupMapHook() {
		super.startupMapHook();
		// start calculation:
		MindMapNode node = getMindMapController().getMap().getRootNode();
		performScriptOperation(node, new BooleanHolder(false));
	}

	private void performScriptOperation(MindMapNode node, BooleanHolder pAlreadyAScriptExecuted) {
		getController().getFrame().setWaitingCursor(true);
		try {
			// depth first:
			for (Iterator iter = node.childrenUnfolded(); iter.hasNext();) {
				MindMapNode element = (MindMapNode) iter.next();
				performScriptOperation(element, pAlreadyAScriptExecuted);
			}
			NodeAttributeTableModel attributes = node.getAttributes();
			if (attributes == null)
				return;
			for (int row = 0; row < attributes.getRowCount(); ++row) {
				String attrKey = (String) attributes.getName(row);
				logger.info("Found key = " + attrKey);
				if (attrKey.startsWith("script")) {
					// ask user if first script:
					if(!pAlreadyAScriptExecuted.getValue()){
						int showResult = new OptionalDontShowMeAgainDialog(getMindMapController()
								.getFrame().getJFrame(), getMindMapController().getSelectedView(),
								"really_execute_script", "confirmation", getMindMapController(),
								new OptionalDontShowMeAgainDialog.StandardPropertyHandler(
										getMindMapController().getController(),
										FreeMind.RESOURCES_EXECUTE_SCRIPTS_WITHOUT_ASKING),
								OptionalDontShowMeAgainDialog.ONLY_OK_SELECTION_IS_STORED)
								.show().getResult();
						if(showResult != JOptionPane.OK_OPTION) {
							return;
						}
					}
					pAlreadyAScriptExecuted.setValue(true);
					Binding binding = new Binding();
					binding.setVariable("c", getMindMapController());
					binding.setVariable("node", node);
					GroovyShell shell = new GroovyShell(binding);

					String script = (String) attributes.getValue(row);
					boolean assignResult = false;
					if (script.startsWith("=")) {
						script = script.substring(1);
						assignResult = true;
					}
					Object value = shell.evaluate(script);
					logger.info("Result of executing " + script + " is "
							+ value);
					if (assignResult && value != null) {
						getMindMapController().setNodeText(node,
								value.toString());
					}
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
			freemind.main.Resources.getInstance().logException(e);
			getController().getController().errorMessage(
					e.getClass().getName() + ": " + e.getMessage());
		}
		getController().getFrame().setWaitingCursor(false);
	}

}
