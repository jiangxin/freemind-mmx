/*
 * Created on 05.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package freemind.controller.actions;

import javax.xml.bind.JAXBException;

import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;

/**
 * @author foltin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface NodeActorXml extends ActorXml {
	/**
	 * @param model
	 * @param selected
	 * @return
	 */
	ActionPair apply(MapAdapter model, MindMapNode selected) throws JAXBException;

}
