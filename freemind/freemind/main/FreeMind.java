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
/*$Id: FreeMind.java,v 1.8 2001-03-13 15:50:05 ponder Exp $*/

package freemind.main;

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
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;

public class FreeMind extends JFrame implements FreeMindMain {

    public static final String version = "0.2.5";
    //    public static final String defaultPropsURL = "freemind.properties";
    public URL defaultPropsURL;
    public static Properties defaultProps;
    public static Properties userProps;
    private JScrollPane scrollPane = new JScrollPane();
    private MenuBar menuBar;
    private JLabel status;
    private Map filetypes;//Maps endings of files ("html") to programs ("netscape")

    Controller c;//the one and only controller
    
    public FreeMind() {
        super("FreeMind");
	
	defaultPropsURL = ClassLoader.getSystemResource("freemind.properties");
	//load properties
	try {
	    defaultProps = new Properties();
	    //	    FileInputStream in = new FileInputStream(defaultPropsURL);
	    InputStream in = defaultPropsURL.openStream();
	    defaultProps.load(in);
	    in.close();
	    userProps = new Properties(defaultProps);
	    String userPropsURL = defaultProps.getProperty("userproperties");
	    //replace ~ with the users home dir
	    if (userPropsURL.startsWith("~")) {
		userPropsURL = System.getProperty("user.home") + userPropsURL.substring(1);
	    }
	    in = new FileInputStream(userPropsURL);
	    userProps.load(in);
	    in.close();
	} catch (Exception ex) {
	    System.err.println("Panic! Error while loading properties");
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
	menuBar = new MenuBar(c);
	setJMenuBar(menuBar);

        //Create the scroll pane.
	scrollPane.setPreferredSize( new Dimension( 600, 400 ) );
	
	getContentPane().add( scrollPane, BorderLayout.CENTER );

	status = new JLabel();
	getContentPane().add( status, BorderLayout.SOUTH );

	//Disable the default close button, instead use windowListener
	setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

	addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
		    c.quit.actionPerformed(new ActionEvent(this,0,"quit"));
		}		
		public void windowActivated(WindowEvent e) {
		    //This doesn't work the first time, it's called too early to get Focus
		    if ( (getView() != null) && (getView().getSelected() != null) ) {
			getView().getSelected().requestFocus();
		    }
		}
        });  

	c.changeToMode("Browse");

    }//Constructor

    public URL getResource(String name) {
	return ClassLoader.getSystemResource(name);
    }

    public String getProperty(String key) {
	return userProps.getProperty(key);
    }

    public MapView getView() {
	return c.getView();
    }

    public void setView(MapView view) {
	scrollPane.setViewportView(view);
    }

    public MenuBar getFreeMindMenuBar() {
	return menuBar;
    }

    public void out (String msg) {
	status.setText(msg);
    }

    public void err (String msg) {
	status.setText(msg);	
    }

    public void openDocument(URL url) throws Exception {
	String type = Tools.getExtension(url.getFile());
	out(type); //IMPLEMENT THIS
	String prog = getProgramForFile(type);

	//try to use default program (netscape etc)
	if (prog.equals("")) {
	    prog = getProgramForFile("default");
	}

	String document = null;

	//convert "file:" url's to local filenames
	if (url.getProtocol().equals("file")) {
	    document = url.getFile(); //check if this works for windows
	} else {
	    document = url.toString();
	}

	if (prog.equals("execute")) {
	    Runtime.getRuntime().exec( document );
	} else {
	    String[] call = { prog, document };
	    Runtime.getRuntime().exec( call );
	}
    }

    private String getProgramForFile(String type) {
	if (filetypes == null) {
	    filetypes = new HashMap();
	    String raw = getProperty("filetypes");
	    if (raw == null  || raw.equals("")) {
		return "";
	    }
	    StringTokenizer tokens = new StringTokenizer(raw, ",");
	    while (tokens.hasMoreTokens()) {
		StringTokenizer pair = new StringTokenizer(tokens.nextToken(),":");
		String key = pair.nextToken().trim().toLowerCase();
		String value = pair.nextToken().trim();
		filetypes.put(key,value);
	    }
	}
	return (String)filetypes.get(type.trim().toLowerCase());
    }

    /**Returns the ResourceBundle with the current language*/
    public ResourceBundle getResources() {
	String lang = getProperty("language");
	try {
	    InputStream in = ClassLoader.getSystemResource("Resources_"+lang+".properties").openStream();
	    PropertyResourceBundle resources = new PropertyResourceBundle(in);
	    in.close();
	    return resources;
	} catch (Exception ex) {
	    System.err.println("Error loading Resources");
	    return null;
	}
	//	return ResourceBundle.getBundle("Resources",locale);
    }

    public static void main(String[] args) {
        JFrame frame = new FreeMind();
 
        frame.pack();
        frame.setVisible(true);

    }//main()
}
