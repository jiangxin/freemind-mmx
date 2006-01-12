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
/*$Id: NodeNoteBase.java,v 1.1.2.1 2006-01-12 23:10:13 christianfoltin Exp $*/
package freemind.modes.common.plugins;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import freemind.extensions.PermanentNodeHookAdapter;
import freemind.main.FreeMindMain;
import freemind.main.XMLElement;
import freemind.modes.MindMapNode;

/**
 * @author foltin
 *
 */
public abstract class NodeNoteBase extends PermanentNodeHookAdapter {

	protected JTextArea text;
	private String myNodeText;
	private JScrollPane scroller;
	private static ImageIcon noteIcon;
	/**
	 * 
	 */
	public NodeNoteBase() {
		super();
		myNodeText = new String();
	}


	/* (non-Javadoc)
	 * @see freemind.extensions.NodeHook#invoke(freemind.modes.MindMapNode)
	 */
	public void invoke(MindMapNode node) {
		super.invoke(node);
		enableStateIcon(node);
	}

	/**
     * @param node
     */
    private void enableStateIcon(MindMapNode node) {
        // icon
		if (noteIcon == null) {
			noteIcon = new ImageIcon(getController().getFrame().getResource("images/knotes.png"));
		}
		node.setStateIcon(getName(), noteIcon);
		nodeRefresh(node);
    }


    /* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#onReceiveFocusHook()
	 */
	public void onReceiveFocusHook() {
		super.onReceiveFocusHook();
		if(text==null) {
			logger.fine("Text ctrl. set for node "+getNode()+" as "+getMyNodeText());
			// panel:
			FreeMindMain frame = getController().getFrame();
	
			text = new JTextArea(5,50);
			text.setText(getMyNodeText());
            // word wrap for notes.
            text.setWrapStyleWord(true);
			
			text.addKeyListener(new KeyListener(){

                public void keyPressed(KeyEvent e) {
                	switch ( e.getKeyCode() ) {
                    	// the space event must not reach the parent frames, as folding would result.
                        case KeyEvent.VK_SPACE:
                            e.consume();
                        	break;
                	}
                }

                public void keyReleased(KeyEvent e) {
                }

                public void keyTyped(KeyEvent e) {
                }});
	
			receiveFocusAddons();
			scroller = new JScrollPane(text);
			scroller.setPreferredSize( new Dimension( 600, 150 ) );
			frame.getSouthPanel().add(scroller, BorderLayout.CENTER);
			scroller.setVisible(true);
			frame.getSouthPanel().revalidate();
		}
	}



	/**
	 * @return
	 */
	public String getMyNodeText() {
		return new String(myNodeText);
	}

	/**
	 * @param string
	 */
	public void setMyNodeText(String string) {
		myNodeText = new String(string);
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#onLooseFocusHook()
	 */
	public void onLooseFocusHook() {
		super.onLooseFocusHook();
		if (text != null) {
			looseFocusAddons();
			// shut down the display:
			scroller.setVisible(false);
			JPanel southPanel = getController().getFrame().getSouthPanel();
			southPanel.remove(scroller);
			southPanel.revalidate();
			scroller = null;
			text = null;
		}
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#loadFrom(freemind.main.XMLElement)
	 */
	public void loadFrom(XMLElement child) {
		super.loadFrom(child);
		if(child.getChildren().size()>0) {
			XMLElement paramChild = (XMLElement) child.getChildren().get(0);
			if(paramChild != null) {
				setMyNodeText(paramChild.getContent());
			}
		}
	}

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


	/* (non-Javadoc)
	 * @see freemind.extensions.MindMapHook#shutdownMapHook()
	 */
	public void shutdownMapHook() {
		onLooseFocusHook();
		getNode().setStateIcon(getName(), null);
		nodeRefresh(getNode());
		super.shutdownMapHook();
	}


	protected abstract void nodeRefresh(MindMapNode node);
	protected abstract void receiveFocusAddons();
	protected abstract void looseFocusAddons();

}
