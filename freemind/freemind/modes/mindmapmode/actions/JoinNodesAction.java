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
package freemind.modes.mindmapmode.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import freemind.main.HtmlTools;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.view.mindmapview.MapView;

public class JoinNodesAction extends AbstractAction {
	private final MindMapController controller;

	public JoinNodesAction(MindMapController controller) {
		super(controller.getText("join_nodes"));
		this.controller = controller;
	}

	public void actionPerformed(ActionEvent e) {
		MindMapNode selectedNode = controller.getView().getSelected()
				.getModel();
		ArrayList selectedNodes = controller.getView()
				.getSelectedNodesSortedByY();
		joinNodes(selectedNode, selectedNodes);
	}

	public void joinNodes(MindMapNode selectedNode, List selectedNodes) {
		String newContent = "";
		// Make sure the selected node do not have children
		final MapView mapView = controller.getView();
		for (Iterator it = selectedNodes.iterator(); it.hasNext();) {
			MindMapNode node = (MindMapNode) it.next();
			if (node.hasChildren()) {
				JOptionPane.showMessageDialog(mapView,
						controller.getText("cannot_join_nodes_with_children"),
						"FreeMind", JOptionPane.WARNING_MESSAGE);
				return;
			}
		}

		// Join
		boolean isHtml = false;
		for (Iterator it = selectedNodes.iterator(); it.hasNext();) {
			final MindMapNode node = (MindMapNode) it.next();
			final String nodeContent = node.toString();
			final boolean isHtmlNode = HtmlTools.isHtmlNode(nodeContent);
			newContent = addContent(newContent, isHtml, nodeContent, isHtmlNode);
			if (node != selectedNode) {
				controller.deleteNode(node);
			}
			isHtml = isHtml || isHtmlNode;
		}

		mapView.selectAsTheOnlyOneSelected(mapView.getNodeView(selectedNode));
		controller.setNodeText(selectedNode, newContent);
	}

	final static Pattern BODY_START = Pattern.compile("<body>",
			Pattern.CASE_INSENSITIVE);
	final static Pattern BODY_END = Pattern.compile("</body>",
			Pattern.CASE_INSENSITIVE);

	private String addContent(String content, boolean isHtml,
			String nodeContent, boolean isHtmlNode) {
		if (isHtml) {
			final String start[] = BODY_END.split(content, -2);
			content = start[0];
			if (!isHtmlNode) {
				final String end[] = BODY_START.split(content, 2);
				nodeContent = end[0] + "<body><p>" + nodeContent + "</p>";
			}
		}
		if (isHtmlNode & !content.equals("")) {
			final String end[] = BODY_START.split(nodeContent, 2);
			nodeContent = end[1];
			if (!isHtml) {
				content = end[0] + "<body><p>" + content + "</p>";
			}
		}
		if (!(isHtml || isHtmlNode || content.equals(""))) {
			content += " ";
		}
		content += nodeContent;
		return content;
	}
}
