/*
 * Created on 16.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package accessories.plugins;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import freemind.extensions.PermanentNodeHookAdapter;
import freemind.main.FreeMindMain;
import freemind.main.XMLElement;
import freemind.modes.MindMapNode;

/**
 * @author foltin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NodeNote extends PermanentNodeHookAdapter {

	static private JTextArea text;
	static private Integer instanceCounter;
	static private MindMapNode oldSelectedNode;
	private String myNodeText;
	static private JScrollPane scroller;
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
		if(instanceCounter == null ){
			instanceCounter = new Integer(0);
		}
		instanceCounter=new Integer(instanceCounter.intValue()+1);
		if(text == null) {
			// panel:
			FreeMindMain frame = getController().getFrame();
			text = new JTextArea(5,50);
			scroller = new JScrollPane(text);
			scroller.setPreferredSize( new Dimension( 600, 150 ) );
			frame.getSouthPanel().add(scroller, BorderLayout.CENTER);
			scroller.repaint();
			oldSelectedNode = node;
		}
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#onReceiveFocusHook()
	 */
	public void onReceiveFocusHook() {
		super.onReceiveFocusHook();
		logger.info("Text ctrl. set for node "+getNode()+" as "+getMyNodeText());
		text.setText(null);				
		text.setText(getMyNodeText());				
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
		logger.info("Text set for node "+getNode()+" as "+string);
		myNodeText = new String(string);
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#onLooseFocusHook()
	 */
	public void onLooseFocusHook() {
		super.onLooseFocusHook();
		setMyNodeText(text.getText());
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.MindMapHook#shutdownMapHook()
	 */
	public void shutdownMapHook() {
		instanceCounter=new Integer(instanceCounter.intValue()-1);
		if(instanceCounter.intValue()==0) {
			// shut down the display:
			FreeMindMain frame = getController().getFrame();
			frame.getSouthPanel().remove(scroller);
			frame.getSouthPanel().repaint();
			scroller=null;
			text    =null;
			instanceCounter=null;
		}
		super.shutdownMapHook();
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#loadFrom(freemind.main.XMLElement)
	 */
	public void loadFrom(XMLElement child) {
		super.loadFrom(child);
		if(child.getChildren().size()>0) {
			XMLElement paramChild = (XMLElement) child.getChildren().get(0);
			if(paramChild != null) {
				myNodeText = paramChild.getContent();
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
		child.setContent(myNodeText);
		xml.addChild(child);
	}

}
