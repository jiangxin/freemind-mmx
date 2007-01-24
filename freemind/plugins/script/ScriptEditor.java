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
/*$Id: ScriptEditor.java,v 1.1.2.2 2007-01-24 22:26:01 christianfoltin Exp $*/
package plugins.script;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Vector;

import plugins.script.ScriptEditorPanel.ScriptHolder;
import plugins.script.ScriptEditorPanel.ScriptModel;
import freemind.controller.actions.generated.instance.ScriptEditorWindowConfigurationStorage;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.hooks.MindMapHookAdapter;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 * @author foltin
 * 
 */
public class ScriptEditor extends MindMapHookAdapter {
	private final class NodeScriptModel implements ScriptModel {
		private final Vector mScripts;

		private NodeScriptModel(Vector pScripts) {
			mScripts = pScripts;
		}

		public ScriptEditorWindowConfigurationStorage decorateDialog(
				ScriptEditorPanel pPanel,
				String pWindow_preference_storage_property) {
			return (ScriptEditorWindowConfigurationStorage) getMindMapController()
					.decorateDialog(pPanel, pWindow_preference_storage_property);
		}

		public String executeScript(int pIndex, PrintStream pOutStream) {
			Binding binding = new Binding();
			binding.setVariable("c", null);
			binding.setVariable("node", null);
			GroovyShell shell = new GroovyShell(binding);

			String script = getScript(pIndex).getScript();
			// redirect output:
			PrintStream oldOut = System.out;
			Object value;
			try {
				System.setOut(pOutStream);
				value = shell.evaluate(script);
			} finally {
				System.setOut(oldOut);
			}
			return (value != null) ? value.toString() : null;
		}

		public int getAmountOfScripts() {
			return mScripts.size();
		}

		public ScriptHolder getScript(int pIndex) {
			return (ScriptHolder) mScripts.get(pIndex);
		}

		public void setScript(int pIndex, ScriptHolder pScript) {
			mScripts.set(pIndex, pScript);
		}

		public void storeDialogPositions(ScriptEditorPanel pPanel,
				ScriptEditorWindowConfigurationStorage pStorage,
				String pWindow_preference_storage_property) {
			getMindMapController().storeDialogPositions(pPanel, pStorage,
					pWindow_preference_storage_property);
		}
	}

	public void startupMapHook() {
		super.startupMapHook();
		final MindMapNode node = getMindMapController().getSelected();
		final Vector scripts = new Vector();
		int position = 0;
		for (Iterator iter = node.getAttributeKeyList().iterator(); iter
				.hasNext();) {
			String element = (String) iter.next();
			if (element.startsWith("script")) {
				scripts.add(new ScriptEditorPanel.ScriptHolder(element, node
						.getAttributes().getAttribute(position).getValue()));
			}
			position++;
		}
		ScriptEditorPanel scriptEditorPanel = new ScriptEditorPanel(
				new NodeScriptModel(scripts), getController().getFrame());
		scriptEditorPanel.setVisible(true);
	}
}
