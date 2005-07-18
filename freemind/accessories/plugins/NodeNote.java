/*
 * Created on 16.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package accessories.plugins;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import freemind.extensions.PermanentNodeHookAdapter;
import freemind.main.FreeMindMain;
import freemind.main.XMLElement;
import freemind.modes.MindIcon;
import freemind.modes.MindMapNode;

/**
 * @author foltin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NodeNote extends PermanentNodeHookAdapter {

	private NodeTextListener listener;
	private JTextArea text;
	private String myNodeText;
	private JScrollPane scroller;
	private static ImageIcon noteIcon;
	/**
	 * 
	 */
	public NodeNote() {
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
			noteIcon = new ImageIcon(getController().getFrame().getResource("accessories/plugins/icons/knotes.png"));
		}
		node.setStateIcon(getName(), noteIcon);
		getController().nodeRefresh(node);
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
	
			listener = new NodeTextListener();
			listener.setNote(this);				
			text.getDocument().addDocumentListener(listener);
			
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
			listener.setNote(null);
			// shut down the display:
			scroller.setVisible(false);
			FreeMindMain frame = getController().getFrame();
			frame.getSouthPanel().remove(scroller);
			frame.getSouthPanel().validate();
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

	/* (non-Javadoc)
	 * @see freemind.extensions.MindMapHook#shutdownMapHook()
	 */
	public void shutdownMapHook() {
		onLooseFocusHook();
		getNode().setStateIcon(getName(), null);
		getController().nodeRefresh(getNode());
		super.shutdownMapHook();
	}

}
