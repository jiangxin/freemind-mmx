/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2007  Joerg Mueller, Daniel Polansky, Dimitri Polivaev, Christian Foltin and others.
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
 * Created on 10.01.2007
 */
/*$Id: ScriptEditor.java,v 1.1.2.11 2008/05/21 19:15:23 christianfoltin Exp $*/
package plugins.script;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Vector;

import plugins.script.ScriptEditorPanel.ScriptHolder;
import plugins.script.ScriptEditorPanel.ScriptModel;
import plugins.script.ScriptingEngine.ErrorHandler;
import freemind.controller.actions.generated.instance.ScriptEditorWindowConfigurationStorage;
import freemind.main.Tools.BooleanHolder;
import freemind.modes.MindMapNode;
import freemind.modes.attributes.Attribute;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.hooks.MindMapHookAdapter;

/**
 * @author foltin
 * 
 */
public class ScriptEditor extends MindMapHookAdapter {
	private final class AttributeHolder {
		Attribute mAttribute;
		int mPosition;

		public AttributeHolder(Attribute pAttribute, int pPosition) {
			super();
			mAttribute = pAttribute;
			mPosition = pPosition;
		}
	}

	private final class NodeScriptModel implements ScriptModel {
		/**
		 * Of AttributeHolder
		 */
		private final Vector mScripts;
		private final MindMapNode mNode;
		private final MindMapController mMindMapController;
		private boolean isDirty = false;

		private NodeScriptModel(Vector pScripts, MindMapNode node,
				MindMapController pMindMapController) {
			mScripts = pScripts;
			mNode = node;
			mMindMapController = pMindMapController;
		}

		public ScriptEditorWindowConfigurationStorage decorateDialog(
				ScriptEditorPanel pPanel,
				String pWindow_preference_storage_property) {
			return (ScriptEditorWindowConfigurationStorage) getMindMapController()
					.decorateDialog(pPanel, pWindow_preference_storage_property);
		}

		public boolean executeScript(int pIndex, PrintStream pOutStream,
				ErrorHandler pErrorHandler) {
			String script = getScript(pIndex).getScript();
			// get cookies from base plugin:
			ScriptingRegistration reg = (ScriptingRegistration) getPluginBaseClass();
			return ScriptingEngine.executeScript(
					mMindMapController.getSelected(), new BooleanHolder(true),
					script, mMindMapController, pErrorHandler, pOutStream,
					reg.getScriptCookies());
		}

		public int getAmountOfScripts() {
			return mScripts.size();
		}

		public ScriptHolder getScript(int pIndex) {
			Attribute attribute = ((AttributeHolder) mScripts.get(pIndex)).mAttribute;
			return new ScriptHolder(attribute.getName(), attribute.getValue());
		}

		public void setScript(int pIndex, ScriptHolder pScript) {
			AttributeHolder oldHolder = (AttributeHolder) mScripts.get(pIndex);
			if (!pScript.mScriptName.equals(oldHolder.mAttribute.getName())) {
				isDirty = true;
			}
			if (!pScript.mScript.equals(oldHolder.mAttribute.getValue())) {
				isDirty = true;
			}
			oldHolder.mAttribute.setName(pScript.mScriptName);
			oldHolder.mAttribute.setValue(pScript.mScript);
		}

		public void storeDialogPositions(ScriptEditorPanel pPanel,
				ScriptEditorWindowConfigurationStorage pStorage,
				String pWindow_preference_storage_property) {
			getMindMapController().storeDialogPositions(pPanel, pStorage,
					pWindow_preference_storage_property);
		}

		public void endDialog(boolean pIsCanceled) {
			if (!pIsCanceled) {
				// read length only once, as new attributes get this number as
				// position.
				int attributeTableLength = mNode.getAttributeTableLength();
				// store node attributes back
				for (Iterator iter = mScripts.iterator(); iter.hasNext();) {
					AttributeHolder holder = (AttributeHolder) iter.next();
					Attribute attribute = holder.mAttribute;
					int position = holder.mPosition;
					if (attributeTableLength <= position) {
						// add new attribute
						mMindMapController.addAttribute(mNode, attribute);
					} else if (mNode.getAttribute(position).getValue() != attribute
							.getValue()) {
						// logger.info("Setting attribute " + position + " to "
						// + attribute);
						mMindMapController.setAttribute(mNode, position,
								attribute);
					}
				}
			}
		}

		public boolean isDirty() {
			return isDirty;
		}

		public int addNewScript() {
			int index = mScripts.size();
			/**
			 * is in general different from index, as not all attributes need to
			 * be scripts.
			 */
			int attributeIndex = mNode.getAttributeTableLength();
			String scriptName = ScriptingEngine.SCRIPT_PREFIX;
			int scriptNameSuffix = 1;
			boolean found;
			do {
				found = false;
				for (Iterator iterator = mScripts.iterator(); iterator
						.hasNext();) {
					AttributeHolder holder = (AttributeHolder) iterator.next();
					if ((scriptName + scriptNameSuffix)
							.equals(holder.mAttribute.getName())) {
						found = true;
						scriptNameSuffix++;
						break;
					}
				}
			} while (found);
			mScripts.add(new AttributeHolder(new Attribute(scriptName
					+ scriptNameSuffix, ""), attributeIndex));
			isDirty = true;
			return index;
		}
	}

	public void startupMapHook() {
		super.startupMapHook();
		final MindMapNode node = getMindMapController().getSelected();
		final Vector scripts = new Vector();
		for (int position = 0; position < node.getAttributeTableLength(); position++) {
			Attribute attribute = node.getAttribute(position);
			if (attribute.getName().startsWith(ScriptingEngine.SCRIPT_PREFIX)) {
				scripts.add(new AttributeHolder(attribute, position));
			}
		}
		NodeScriptModel nodeScriptModel = new NodeScriptModel(scripts, node,
				getMindMapController());
		ScriptEditorPanel scriptEditorPanel = new ScriptEditorPanel(
				nodeScriptModel, getController().getFrame(), true);
		scriptEditorPanel.setVisible(true);
	}
}
