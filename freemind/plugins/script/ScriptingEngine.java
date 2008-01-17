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
/* $Id: ScriptingEngine.java,v 1.1.2.10 2008-01-17 20:27:40 christianfoltin Exp $ */
package plugins.script;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.regex.Matcher;

import javax.swing.JOptionPane;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ModuleNode;

import freemind.common.OptionalDontShowMeAgainDialog;
import freemind.main.FreeMind;
import freemind.main.Tools.BooleanHolder;
import freemind.modes.MindMapNode;
import freemind.modes.attributes.NodeAttributeTableModel;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.hooks.MindMapHookAdapter;
import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;

/**
 * @author foltin
 * 
 */
public class ScriptingEngine extends MindMapHookAdapter {
	public static final String SCRIPT_PREFIX = "script";
	static java.util.logging.Logger logger;

	public interface ErrorHandler {
		void gotoLine(int pLineNumber);
	}

	public void startupMapHook() {
		super.startupMapHook();
		// start calculation:
		MindMapNode node = getMindMapController().getMap().getRootNode();
		performScriptOperation(node, new BooleanHolder(false));
	}

	private void performScriptOperation(MindMapNode node,
			BooleanHolder pAlreadyAScriptExecuted) {
		getController().getFrame().setWaitingCursor(true);
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
			String script = (String) attributes.getValue(row);
			logger.info("Found key = " + attrKey);
			if (attrKey.startsWith(SCRIPT_PREFIX)) {
				boolean result = executeScript(node, pAlreadyAScriptExecuted,
						script, getMindMapController(), new ErrorHandler(){
							public void gotoLine(int pLineNumber) {
							}}, System.out);
				if (!result) {
					break;
				}
			}
		}
		getController().getFrame().setWaitingCursor(false);
	}

	public static int findLineNumberInString(String resultString, int lineNumber) {
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
				".*@ line ([0-9]+).*", java.util.regex.Pattern.DOTALL);
		Matcher matcher = pattern.matcher(resultString);
		if (matcher.matches()) {
			lineNumber = Integer.parseInt(matcher.group(1));
		}
		return lineNumber;
	}

	/**
	 * @param node
	 * @param pAlreadyAScriptExecuted
	 * @param script
	 * @param pMindMapController
	 * @return true, if further scripts can be executed, false, if the user
	 *         canceled or an error occurred.
	 */
	static boolean executeScript(MindMapNode node,
			BooleanHolder pAlreadyAScriptExecuted, String script,
			MindMapController pMindMapController, ErrorHandler pErrorHandler,
			PrintStream pOutStream) {
		PrintStream oldOut = System.out;
		try {
			System.setOut(pOutStream);
			// ask user if first script:
			if (!pAlreadyAScriptExecuted.getValue()) {
				int showResult = new OptionalDontShowMeAgainDialog(
						pMindMapController.getFrame().getJFrame(),
						pMindMapController.getSelectedView(),
						"really_execute_script",
						"confirmation",
						pMindMapController,
						new OptionalDontShowMeAgainDialog.StandardPropertyHandler(
								pMindMapController.getController(),
								FreeMind.RESOURCES_EXECUTE_SCRIPTS_WITHOUT_ASKING),
						OptionalDontShowMeAgainDialog.ONLY_OK_SELECTION_IS_STORED)
						.show().getResult();
				if (showResult != JOptionPane.OK_OPTION) {
					return false;
				}
			}
			pAlreadyAScriptExecuted.setValue(true);
			Binding binding = new Binding();
			binding.setVariable("c", pMindMapController);
			binding.setVariable("node", node);
			GroovyShell shell = new GroovyShell(binding);

			boolean assignResult = false;
			String assignTo = null;
			if (script.startsWith("=")) {
				script = script.substring(1);
				assignResult = true;
			} else if(script.matches("[a-zA-Z0-9]*=.*")) {
				int indexOfEquals = script.indexOf('=');
				assignTo = script.substring(0,indexOfEquals);
				script = script.substring(indexOfEquals+1);
				assignResult = true;
			}
			Object value = shell.evaluate(script);
			pOutStream.print(pMindMapController.getFrame().getResourceString(
					"plugins/ScriptEditor/window.Result")
					+ value);
			if (assignResult && value != null) {
				if (assignTo==null) {
					pMindMapController.setNodeText(node, value.toString());
				} else {
					pMindMapController.editAttribute(node, assignTo, value.toString());
				}
			}
			return true;
		} catch (GroovyRuntimeException e) {
			String resultString = e.getMessage();
			pOutStream.print("message: " + resultString);
			ModuleNode module = e.getModule();
			ASTNode astNode = e.getNode();
			int lineNumber = -1;
			if (module != null) {
				lineNumber = module.getLineNumber();
			} else if (astNode != null) {
				lineNumber = astNode.getLineNumber();
			} else {
				lineNumber = findLineNumberInString(resultString, lineNumber);
			}
			pOutStream.print("Line number: " + lineNumber);
			pErrorHandler.gotoLine(lineNumber);
			return false;
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			pOutStream.print(e.getMessage());
			pMindMapController.getController().errorMessage(
					e.getClass().getName() + ": " + e.getMessage());
			return false;
		} finally {
			System.setOut(oldOut);
		}
	}

}
