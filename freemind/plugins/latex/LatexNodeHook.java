/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
 */
/** this is only a test class */
package plugins.latex;

import java.awt.Component;
import java.awt.Container;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import freemind.main.XMLElement;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.hooks.PermanentMindMapNodeHookAdapter;
import freemind.view.mindmapview.NodeView;

/**
 * @author Dimitry Polivaev
 * 
 * @file LatexNodeHook.java
 * @package freemind.modes.mindmapmode
 * */
public class LatexNodeHook extends PermanentMindMapNodeHookAdapter {
	private String equation;
	private Set viewers;

	/**
	 */
	public LatexNodeHook() {
		super();
		equation = "\\mbox{I}^\\fgcolor{ff0000}{\\heartsuit}\\mbox{HotEqn}";
		viewers = new LinkedHashSet();
	}

	public void onViewCreatedHook(NodeView nodeView) {
		createViewer(nodeView);
		super.onViewCreatedHook(nodeView);
	}

	public void onViewRemovedHook(NodeView nodeView) {
		deleteViewer(nodeView);
		super.onViewRemovedHook(nodeView);
	}

	private void deleteViewer(NodeView nodeView) {
		if (viewers.isEmpty()) {
			return;
		}
		final Container contentPane = nodeView.getContentPane();
		final int componentCount = contentPane.getComponentCount();
		for (int i = 0; i < componentCount; i++) {
			Component component = contentPane.getComponent(i);
			if (viewers.contains(component)) {
				viewers.remove(component);
				contentPane.remove(i);
				return;
			}
		}

	}

	public void invoke(MindMapNode node) {
		Iterator iterator = node.getViewers().iterator();
		while (iterator.hasNext()) {
			NodeView view = (NodeView) iterator.next();
			createViewer(view);
		}
		super.invoke(node);
	}

	private void createViewer(NodeView view) {
		JZoomedHotEqn comp = new JZoomedHotEqn(this);
		viewers.add(comp);
		view.getContentPane().add(comp);
	}

	public String getContent(String key) {
		return equation;
	}

	public void setContent(String key, String content) {
		equation = content;
		Iterator iterator = viewers.iterator();
		while (iterator.hasNext()) {
			JZoomedHotEqn comp = (JZoomedHotEqn) iterator.next();
			comp.setModel(this);
		}
		getController().nodeChanged(getNode());
	}

	public void loadFrom(XMLElement child) {
		equation = child.getAttribute("EQUATION", equation).toString();
		super.loadFrom(child);
	}

	public void save(XMLElement xml) {
		super.save(xml);
		xml.setAttribute("EQUATION", equation);
	}

	public void shutdownMapHook() {
		Iterator iterator = viewers.iterator();
		while (iterator.hasNext()) {
			JZoomedHotEqn comp = (JZoomedHotEqn) iterator.next();
			comp.getParent().remove(comp);
		}
		super.shutdownMapHook();
	}

}
