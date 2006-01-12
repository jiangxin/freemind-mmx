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
/*$Id: JaxbTools.java,v 1.1.4.2 2006-01-12 23:10:12 christianfoltin Exp $*/

package freemind.common;

import java.awt.Dimension;
import java.io.StringReader;
import java.io.StringWriter;

import javax.swing.JDialog;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import freemind.controller.Controller;
import freemind.controller.actions.generated.instance.ObjectFactory;
import freemind.controller.actions.generated.instance.WindowConfigurationStorage;
import freemind.controller.actions.generated.instance.XmlAction;

/**
 * @author foltin
 * Singleton
 */
public class JaxbTools {

	private static JaxbTools instance;
	private static JAXBContext context;
	private ObjectFactory actionXmlFactory;


    private JaxbTools() {
		context = getJAXBContext();
		// new object factory for xml actions:
		actionXmlFactory = new ObjectFactory();
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
	
    public ObjectFactory getActionXmlFactory() {
        return actionXmlFactory;
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
	
	public void storeDialogPositions(Controller controller, JDialog dialog, WindowConfigurationStorage storage, String window_preference_storage_property) {
		storage.setX((dialog.getX()));
		storage.setY((dialog.getY()));
		storage.setWidth((dialog.getWidth()));
		storage.setHeight((dialog.getHeight()));
		String marshalled = marshall(storage);
		String result = marshalled;
		controller.setProperty(window_preference_storage_property, result);
	}

	public WindowConfigurationStorage decorateDialog(Controller controller, JDialog dialog, String window_preference_storage_property) {
		String marshalled = controller.getProperty(window_preference_storage_property);
		WindowConfigurationStorage result = decorateDialog(marshalled, dialog);
		return result;
	}
	
    public WindowConfigurationStorage decorateDialog(String marshalled, JDialog dialog) {
//		String unmarshalled = controller.getProperty(
//		        propertyName);
		if (marshalled != null) {
			WindowConfigurationStorage storage = (WindowConfigurationStorage) unMarshall(marshalled);
			if (storage != null) {
				dialog.setLocation(storage.getX(), storage.getY());
				dialog.getRootPane().setPreferredSize(new Dimension(storage.getWidth(), storage.getHeight()));
			}
			return storage;
		}
		return null;
    }


	public String marshall(XmlAction action) {
        try {
            // marshall:
            //marshal to StringBuffer:
            StringWriter writer = new StringWriter();
            Marshaller m = JaxbTools.getInstance().createMarshaller();
            m.marshal(action, writer);
            String result = writer.toString();
            return result;
        } catch (JAXBException e) {
//			logger.severe(e.toString(),e);
            e.printStackTrace();
            return "";
        }

	}

	public XmlAction unMarshall(String inputString) {
		try {
			// unmarshall:
			Unmarshaller u = JaxbTools.getInstance().createUnmarshaller();
			StringBuffer xmlStr = new StringBuffer( inputString);
			XmlAction doAction = (XmlAction) u.unmarshal( new StreamSource( new StringReader( xmlStr.toString() ) ) );
			return doAction;
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}


}
