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
 * Created on 25.02.2006
 */
/*$Id: ScriptEditorProperty.java,v 1.1.2.6 2008/07/04 20:44:02 christianfoltin Exp $*/
package freemind.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;

import com.jgoodies.forms.builder.DefaultFormBuilder;

import freemind.main.HtmlTools;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapController.MindMapControllerPlugin;

public class ScriptEditorProperty extends PropertyBean implements
		PropertyControl, ActionListener {

	public interface ScriptEditorStarter extends MindMapControllerPlugin {
		String startEditor(String scriptInput);
	}

	String description;

	String label;

	String script;

	JButton mButton;
	final JPopupMenu menu = new JPopupMenu();

	private final MindMapController mMindMapController;

	private static java.util.logging.Logger logger = null;

	/**
	 */
	public ScriptEditorProperty(String description, String label,
			MindMapController pMindMapController) {
		super();
		this.description = description;
		this.label = label;
		mMindMapController = pMindMapController;
		if (logger == null) {
			logger = mMindMapController.getFrame().getLogger(
					this.getClass().getName());
		}
		mButton = new JButton();
		mButton.addActionListener(this);
		script = "";
	}

	public String getDescription() {
		return description;
	}

	public String getLabel() {
		return label;
	}

	public void setValue(String value) {
		setScriptValue(value);
	}

	public String getValue() {
		return HtmlTools.unicodeToHTMLUnicodeEntity(HtmlTools
				.toXMLEscapedText(script), false);
	}

	public void layout(DefaultFormBuilder builder, TextTranslator pTranslator) {
		JLabel label = builder.append(pTranslator.getText(getLabel()), mButton);
		label.setToolTipText(pTranslator.getText(getDescription()));
	}

	public void actionPerformed(ActionEvent arg0) {
		// search for plugin that handles the script editor.
		for (Iterator iter = mMindMapController.getPlugins().iterator(); iter
				.hasNext();) {
			MindMapControllerPlugin plugin = (MindMapControllerPlugin) iter
					.next();
			if (plugin instanceof ScriptEditorStarter) {
				ScriptEditorStarter starter = (ScriptEditorStarter) plugin;
				String resultScript = starter.startEditor(script);
				if (resultScript != null) {
					script = resultScript;
					firePropertyChangeEvent();
				}
			}
		}
	}

	/**
	 */
	private void setScriptValue(String result) {
		if (result == null) {
			result = "";
		}
		script = HtmlTools.toXMLUnescapedText(HtmlTools
				.unescapeHTMLUnicodeEntity(result));
		logger.fine("Setting script to " + script);
		mButton.setText(script);
	}

	// /**
	// */
	// private Color getColorValue() {
	// return color;
	// }

	public void setEnabled(boolean pEnabled) {
		mButton.setEnabled(pEnabled);
	}

}
