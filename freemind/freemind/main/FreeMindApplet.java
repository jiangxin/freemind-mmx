/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000  Joerg Mueller <joergmueller@bigfoot.com>
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
/*$Id: FreeMindApplet.java,v 1.1 2001-03-13 16:01:42 ponder Exp $*/

package freemind.main;

import freemind.main.FreeMindApplet;
import freemind.view.mindmapview.MapView;
import freemind.controller.MenuBar;
import freemind.controller.Controller;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
import java.util.Enumeration;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.applet.Applet;
import java.applet.AppletContext;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.Button;
import javax.swing.*;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.UIManager;

public class FreeMindApplet extends JApplet implements FreeMindMain {

    public static final String version = "0.3";
    //    public static final String defaultPropsURL;
    public URL defaultPropsURL;
    public static Properties defaultProps;
    public static Properties userProps;
    private JScrollPane scrollPane = new JScrollPane();
    private MenuBar menuBar;
    private PropertyResourceBundle resources;
    private JLabel status = new JLabel();
    Controller c;//the one and only controller

    public FreeMindApplet() {
    }//Constructor

    public MapView getView() {
	return c.getView();
    }

    public void setView(MapView view) {
 	scrollPane.setViewportView(view);
    }

    public MenuBar getFreeMindMenuBar() {
	return menuBar;
    }

    /**
     * Returns the ResourceBundle with the current language
     */
    public ResourceBundle getResources() {
	if (resources==null) {
	    String lang = userProps.getProperty("language");
	    try {
		URL myurl = getResource("Resources_"+lang.trim()+".properties");
		InputStream in = myurl.openStream();
		resources = new PropertyResourceBundle(in);
		in.close();
	    } catch (Exception ex) {
		System.err.println("Error loading Resources");
		return null;
	    }
	}
	return resources;
    }

    public String getProperty(String key) {
	return userProps.getProperty(key);
    }

    public void setTitle(String title) {
    }

    public void out (String msg) {
	status.setText(msg);
    }

    public void err (String msg) {
	status.setText("ERROR: "+msg);
    }

    public void openDocument(URL doc) throws Exception {
	getAppletContext().showDocument(doc,"_blank");
    }

    /*
    public void start() {
	
    }

    public void stop() {
    }

    public void destroy() {
    }
    */

    public URL getResource(String name) {
	return this.getClass().getResource("/"+name);
    }

    public void init() {
	JRootPane rootPane = createRootPane();
 	//load properties
	defaultPropsURL = getResource("freemind.properties");
	try {
	    //load properties
	    defaultProps = new Properties();
	    InputStream in = defaultPropsURL.openStream();
	    defaultProps.load(in);
 	    in.close();
	    userProps = defaultProps;
 	} catch (Exception ex) {
 	    System.err.println("Panic! Error while loading properties");
 	}

	//try to overload some properties with given command-line (html tag) Arguments
	Enumeration allKeys = userProps.propertyNames();
	while (allKeys.hasMoreElements()) {
	    String key = (String)allKeys.nextElement();
	    String val = getParameter(key);
	    if (val != null  &&  val != "") {
		userProps.setProperty(key,val);
	    }
	}


	//set Look&Feel
	try {
	    String lookAndFeel = userProps.getProperty("lookandfeel");
	    if (lookAndFeel.equals("windows")) {
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
	    } else if (lookAndFeel.equals("motif")) {
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
	    } else if (lookAndFeel.equals("mac")) {
		//Only available on macOS
		UIManager.setLookAndFeel("javax.swing.plaf.mac.MacLookAndFeel");
	    } else {
		//Metal is default
		UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
	    }
	} catch (Exception ex) {
	    System.err.println("Panic! Error while setting Look&Feel");
	}


 	//Layout everything
 	getContentPane().setLayout( new BorderLayout() );

	c = new Controller(this);

 	//Create the MenuBar
	menuBar = new MenuBar(c); //new MenuBar(c);
 	setJMenuBar(menuBar);

	//Create the scroll pane.
		
	getContentPane().add( scrollPane, BorderLayout.CENTER );
	getContentPane().add( status, BorderLayout.SOUTH );

 	c.changeToMode("Browse");
    }
}
