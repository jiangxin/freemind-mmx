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

package plugins.script;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;

import javax.swing.JOptionPane;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.runtime.InvokerHelper;

import freemind.common.OptionalDontShowMeAgainDialog;
import freemind.main.FreeMind;
import freemind.main.FreeMindMain;
import freemind.main.FreeMindSecurityManager;
import freemind.main.Tools;
import freemind.main.Tools.BooleanHolder;
import freemind.modes.MindMapNode;
import freemind.modes.attributes.NodeAttributeTableModel;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.hooks.MindMapHookAdapter;
import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

/**
 * @author foltin
 * 
 */
public class ScriptingEngine extends MindMapHookAdapter {
	public static final String SCRIPT_PREFIX = "script";
	private static final HashMap sScriptCookies = new HashMap();
	static java.util.logging.Logger logger;

	public interface ErrorHandler {
		void gotoLine(int pLineNumber);
	}

	public void startupMapHook() {
		super.startupMapHook();
		MindMapNode node = getMindMapController().getMap().getRootNode();
		BooleanHolder booleanHolder = new BooleanHolder(false);
		// check for installed script:
		String scriptLocation = getResourceString("ScriptLocation");
		if (scriptLocation != null && scriptLocation.length() != 0) {
			performExternalScript(scriptLocation, node, booleanHolder);
			return;
		}
		// start calculation:
		getController().getFrame().setWaitingCursor(true);
		try {
			performScriptOperation(node, booleanHolder);
		} finally {
			getController().getFrame().setWaitingCursor(false);
		}
	}

	private void performExternalScript(String pScriptLocation,
			MindMapNode pNode, BooleanHolder pBooleanHolder) {
		// get cookies from base plugin:
		ScriptingRegistration reg = (ScriptingRegistration) getPluginBaseClass();
		String scriptContent = Tools.getFile(new File(pScriptLocation));
		if (scriptContent == null) {
			return;
		}
		executeScript(pNode, pBooleanHolder, scriptContent,
				getMindMapController(), new ErrorHandler() {
					public void gotoLine(int pLineNumber) {
					}
				}, System.out, reg.getScriptCookies());
	}

	private void performScriptOperation(MindMapNode node,
			BooleanHolder pAlreadyAScriptExecuted) {
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
				// get cookies from base plugin:
				ScriptingRegistration reg = (ScriptingRegistration) getPluginBaseClass();

				boolean result = executeScript(node, pAlreadyAScriptExecuted,
						script, getMindMapController(), new ErrorHandler() {
							public void gotoLine(int pLineNumber) {
							}
						}, System.out, reg.getScriptCookies());
				if (!result) {
					break;
				}
			}
		}
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
	 * @param pScriptCookies
	 *            TODO
	 * @return true, if further scripts can be executed, false, if the user
	 *         canceled or an error occurred.
	 */
	static boolean executeScript(MindMapNode node,
			BooleanHolder pAlreadyAScriptExecuted, String script,
			MindMapController pMindMapController, ErrorHandler pErrorHandler,
			PrintStream pOutStream, HashMap pScriptCookies) {
		// ask user if first script:
		FreeMindMain frame = pMindMapController.getFrame();
		if (!pAlreadyAScriptExecuted.getValue()) {
			int showResult = new OptionalDontShowMeAgainDialog(
					frame.getJFrame(), pMindMapController.getSelectedView(),
					"really_execute_script", "confirmation",
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
		binding.setVariable("cookies", sScriptCookies);

		boolean assignResult = false;
		String assignTo = null;
		if (script.startsWith("=")) {
			script = script.substring(1);
			assignResult = true;
		} else {
			int indexOfEquals = script.indexOf('=');
			if (indexOfEquals > 0) {
				String start = script.substring(0, indexOfEquals);
				if (start.matches("[a-zA-Z0-9_]+")) {
					assignTo = start;
					script = script.substring(indexOfEquals + 1);
					assignResult = true;
				}
			}
		}
		/*
		 * get preferences (and store them again after the script execution,
		 * such that the scripts are not able to change them).
		 */
		String executeWithoutAsking = frame
				.getProperty(FreeMind.RESOURCES_EXECUTE_SCRIPTS_WITHOUT_ASKING);
		String executeWithoutFileRestriction = frame
				.getProperty(FreeMind.RESOURCES_EXECUTE_SCRIPTS_WITHOUT_FILE_RESTRICTION);
		String executeWithoutNetworkRestriction = frame
				.getProperty(FreeMind.RESOURCES_EXECUTE_SCRIPTS_WITHOUT_NETWORK_RESTRICTION);
		String executeWithoutExecRestriction = frame
				.getProperty(FreeMind.RESOURCES_EXECUTE_SCRIPTS_WITHOUT_EXEC_RESTRICTION);
		String signedScriptsWithoutRestriction = frame
				.getProperty(FreeMind.RESOURCES_SIGNED_SCRIPT_ARE_TRUSTED);
		/* *************** */
		/* **Signature ** */
		/* *************** */
		PrintStream oldOut = System.out;
		Object value = null;
		GroovyRuntimeException e1 = null;
		Throwable e2 = null;
		boolean filePerm = Tools
				.isPreferenceTrue(executeWithoutFileRestriction);
		boolean networkPerm = Tools
				.isPreferenceTrue(executeWithoutNetworkRestriction);
		boolean execPerm = Tools
				.isPreferenceTrue(executeWithoutExecRestriction);
		if (Tools.isPreferenceTrue(signedScriptsWithoutRestriction)) {
			boolean isSigned = new SignedScriptHandler().isScriptSigned(script,
					pOutStream);
			if (isSigned) {
				filePerm = true;
				networkPerm = true;
				execPerm = true;
			}
		}
		final ScriptingSecurityManager scriptingSecurityManager = new ScriptingSecurityManager(
				filePerm, networkPerm, execPerm);
		final FreeMindSecurityManager securityManager = (FreeMindSecurityManager) System
				.getSecurityManager();
		try {
			System.setOut(pOutStream);
			// copied from freeplane from
			// http://freeplane.bzr.sourceforge.net/bzr/freeplane/freeplane_program/release_branches/1_0_x/annotate/head%3A/freeplane_plugin_script/src/org/freeplane/plugin/script/ScriptingEngine.java
			final GroovyShell shell = new GroovyShell(binding) {
				/**
				 * Evaluates some script against the current Binding and returns
				 * the result
				 * 
				 * @param in
				 *            the stream reading the script
				 * @param fileName
				 *            is the logical file name of the script (which is
				 *            used to create the class name of the script)
				 */
				public Object evaluate(final InputStream in,
						final String fileName)
						throws CompilationFailedException {
					Script script = null;
					try {
						script = parse(in, fileName);
						securityManager
								.setFinalSecurityManager(scriptingSecurityManager);
						return script.run();
					} finally {
						if (script != null) {
							InvokerHelper.removeClass(script.getClass());
							// setting the same security manager the second time
							// causes it to be
							// removed.
							securityManager
									.setFinalSecurityManager(scriptingSecurityManager);
						}
					}
				}
			};
			value = shell.evaluate(script);
		} catch (final GroovyRuntimeException e) {
			e1 = e;
		} catch (final Throwable e) {
			e2 = e;
		} finally {
			System.setOut(oldOut);
			/* restore preferences (and assure that the values are unchanged!). */
			frame.setProperty(
					FreeMind.RESOURCES_EXECUTE_SCRIPTS_WITHOUT_ASKING,
					executeWithoutAsking);
			frame.setProperty(
					FreeMind.RESOURCES_EXECUTE_SCRIPTS_WITHOUT_FILE_RESTRICTION,
					executeWithoutFileRestriction);
			frame.setProperty(
					FreeMind.RESOURCES_EXECUTE_SCRIPTS_WITHOUT_NETWORK_RESTRICTION,
					executeWithoutNetworkRestriction);
			frame.setProperty(
					FreeMind.RESOURCES_EXECUTE_SCRIPTS_WITHOUT_EXEC_RESTRICTION,
					executeWithoutExecRestriction);
			frame.setProperty(FreeMind.RESOURCES_SIGNED_SCRIPT_ARE_TRUSTED,
					signedScriptsWithoutRestriction);
		}
		/*
		 * Cover exceptions in normal security context (ie. no problem with
		 * (log) file writing etc.)
		 */
		if (e1 != null) {
			String resultString = e1.getMessage();
			pOutStream.print("message: " + resultString);
			ModuleNode module = e1.getModule();
			ASTNode astNode = e1.getNode();
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

		}
		if (e2 != null) {
			freemind.main.Resources.getInstance().logException(e2);
			pOutStream.print(e2.getMessage());
			String cause = ((e2.getCause() != null) ? e2.getCause()
					.getMessage() : "");
			String message = ((e2.getMessage() != null) ? e2.getMessage() : "");
			pMindMapController
					.getController()
					.errorMessage(
							e2.getClass().getName()
									+ ": "
									+ cause
									+ ((cause.length() != 0 && message.length() != 0) ? ", "
											: "") + message);
			return false;
		}
		pOutStream.print(frame
				.getResourceString("plugins/ScriptEditor/window.Result")
				+ value);
		if (assignResult && value != null) {
			if (assignTo == null) {
				pMindMapController.setNodeText(node, value.toString());
			} else {
				pMindMapController.editAttribute(node, assignTo,
						value.toString());
			}
		}
		return true;
	}

}
