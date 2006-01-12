/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006  Christian Foltin and others
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
/*$Id: NodeNote.java,v 1.1.4.5 2006-01-12 23:10:12 christianfoltin Exp $*/
package accessories.plugins;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import freemind.main.XMLElement;
import freemind.modes.MindMapNode;
import freemind.modes.common.plugins.NodeNoteBase;
import freemind.modes.mindmapmode.MindMapController;

/**
 * @author foltin
 *
 */
public class NodeNote extends NodeNoteBase {


	private NodeTextListener listener;

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#save(freemind.main.XMLElement)
	 */
	public void save(XMLElement xml) {
		super.save(xml);
		XMLElement child = new XMLElement();
		child.setName("text");
		child.setContent(getMyNodeText());
		xml.addChild(child);
	}

	public class NodeTextListener implements DocumentListener {
		private NodeNote pNote;

		public NodeTextListener() {
			pNote=null;
		}
		/**
		 * @see javax.swing.event.DocumentListener#insertUpdate(DocumentEvent)
		 */
		public void insertUpdate(DocumentEvent e) {
			changedUpdate(e);
		}

		/**
		 * @see javax.swing.event.DocumentListener#removeUpdate(DocumentEvent)
		 */
		public void removeUpdate(DocumentEvent e) {
			changedUpdate(e);
		}

		/**
		 * @see javax.swing.event.DocumentListener#changedUpdate(DocumentEvent)
		 */
		public void changedUpdate(DocumentEvent e) {
			try {
				if(pNote!=null) {
					String text = e.getDocument().getText(0, e.getDocument().getLength());
					pNote.setMyNodeText(text);
					pNote.nodeChanged(pNote.getNode());
				}
			} catch (BadLocationException ex) {
				System.err.println("Could not fetch nodeText content"+ex.toString());
			}
		}

		/**
		 * @param note
		 */
		public void setNote(NodeNote note) {
			pNote = note;
		}

	}

	protected void nodeRefresh(MindMapNode node) {
		((MindMapController) getController()).nodeRefresh(node);		
	}

	protected void receiveFocusAddons() {
		listener = new NodeTextListener();
		listener.setNote(this);				
		text.getDocument().addDocumentListener(listener);
	}

	protected void looseFocusAddons() {
		listener.setNote(null);
		
	}


}
