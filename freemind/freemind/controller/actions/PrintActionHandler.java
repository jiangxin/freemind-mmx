/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2004  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 * Created on 24.04.2004
 */
/*$Id: PrintActionHandler.java,v 1.1.2.2 2004-05-06 05:24:11 christianfoltin Exp $*/

package freemind.controller.actions;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * @author foltin
 *
 */
public class PrintActionHandler implements ActionHandler {

	private static JAXBContext jaxContext;

    /**
	 * 
	 */
	public PrintActionHandler() {
		super();
		jaxContext = null;
	}

	/* (non-Javadoc)
	 * @see freemind.controller.actions.ActionHandler#startTransaction(java.lang.String)
	 */
	public void startTransaction(String name) {

	}

	/* (non-Javadoc)
	 * @see freemind.controller.actions.ActionHandler#endTransaction(java.lang.String)
	 */
	public void endTransaction(String name) {

	}

	/* (non-Javadoc)
	 * @see freemind.controller.actions.ActionHandler#executeAction(freemind.controller.actions.ActionPair)
	 */
	public void executeAction(ActionPair pair) {
		try {
			if(jaxContext == null)
				jaxContext = JAXBContext.newInstance(ActionFactory.JAXB_CONTEXT);
			//			marshal to System.out
			Marshaller m = jaxContext.createMarshaller();
			m.marshal(pair.getDoAction(), System.out);

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
