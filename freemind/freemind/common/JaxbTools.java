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
 * Created on 23.06.2004
 */
/*$Id: JaxbTools.java,v 1.1.4.1 2004-10-17 23:00:06 dpolivaev Exp $*/

package freemind.common;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import freemind.controller.actions.generated.instance.ObjectFactory;

/**
 * @author foltin
 * Singleton
 */
public class JaxbTools {

	private static JaxbTools instance;
	private static JAXBContext context;

    private JaxbTools() {
		context = getJAXBContext();
	}

	public static JaxbTools getInstance() {
		if(instance == null) {
			instance = new JaxbTools();
		}
		return instance;
	}

	private static final String JAXB_CONTEXT =
		"freemind.controller.actions.generated.instance";


	public JAXBContext getJAXBContext() {
		try {
			return JAXBContext.newInstance(JAXB_CONTEXT);
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new RuntimeException("getJAXBContext failed.");
		}
	}
	
	public ObjectFactory getObjectFactory() {
		return new ObjectFactory();
	}
	
	public Marshaller createMarshaller() {
		try {
            return context.createMarshaller();
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException("createMarshaller failed.");
        }
	}
	
	public Unmarshaller createUnmarshaller() {
		try {
            return context.createUnmarshaller();
        } catch (JAXBException e) {
            e.printStackTrace();
			throw new RuntimeException("createUnmarshaller failed.");
        }
	}
}
