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

package freemind.main;

import freemind.view.mindmapview.MapView;
import freemind.controller.MenuBar;
import freemind.controller.Controller;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.UIManager;

public class FreeMind extends JFrame {

    public static final String version = "0.0.3 build 1";
    public static final String defaultPropsURL = "freemind.properties";
    public static Properties defaultProps;
    public static Properties userProps;
    private JScrollPane scrollPane = new JScrollPane();
    private MenuBar menuBar;
    Controller c;//the one and only controller
    
    public FreeMind() {
        super("FreeMind");

	//load properties
	try {
	    defaultProps = new Properties();
	    FileInputStream in = new FileInputStream(defaultPropsURL);
	    defaultProps.load(in);
	    in.close();
	    userProps = new Properties(defaultProps);
	    String userPropsURL = defaultProps.getProperty("userproperties");
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
	
        //Create the scroll pane and add the tree to it. 
	scrollPane.setPreferredSize( new Dimension( 600, 400 ) );
	
	getContentPane().add( scrollPane, BorderLayout.CENTER );


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
 

    }//Constructor

    public MapView getView() {
	return c.getView();
    }

    public void setView(MapView view) {
	scrollPane.setViewportView(view);
    }

    public void updateMenuBar() {
	menuBar.update();
    }

    /**Returns the ResourceBundle with the current language*/
    public static ResourceBundle getResources() {
	String lang = userProps.getProperty("language");
	try {
	    return new PropertyResourceBundle(new FileInputStream("Resources_"+lang+".properties"));
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
