/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2001  Joerg Mueller <joergmueller@bigfoot.com>
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
/*$Id: BrowseMapModel.java,v 1.9.18.4.6.2 2006-01-22 12:24:38 dpolivaev Exp $*/

package freemind.modes.browsemode;


import freemind.main.FreeMindMain;
import freemind.modes.MapAdapter;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessControlException;


// link registry.
import freemind.modes.LinkRegistryAdapter;
import freemind.modes.MindMapLinkRegistry;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;


public class BrowseMapModel extends MapAdapter {

    private URL url;
    private LinkRegistryAdapter linkRegistry;

    //
    // Constructors
    //
    public BrowseMapModel(FreeMindMain frame, ModeController modeController) {
        this(null, frame, modeController);
    }

    public BrowseMapModel( BrowseNodeModel root, FreeMindMain frame , ModeController modeController) {
        super(frame, modeController);
        if(root != null)
            setRoot(root);
        else
           setRoot(new BrowseNodeModel(getFrame().getResourceString("new_mindmap"), getFrame(), this)); 
        // register new LinkRegistryAdapter
        linkRegistry = new LinkRegistryAdapter();
    }

    //
    // Other methods
    //
    public MindMapLinkRegistry getLinkRegistry() {
        return linkRegistry;
    }

    public String toString() {
	if (getURL() == null) {
	    return null;
	} else {
	    return getURL().toString();
	}
    }

    public File getFile() {
	return null;
    }

    protected void setFile() {
    }

    
    /**
       * Get the value of url.
       * @return Value of url.
       */
    public URL getURL() {
       return url;}
    
    /**
       * Set the value of url.
       * @param v  Value to assign to url.
       */
    public void setURL(URL  v) {this.url = v;}
    

    public boolean save(File file) {
    	return true;
    }
    
    public boolean isSaved() {
	return true;
    }

    public void load(File file) throws FileNotFoundException {
	throw new FileNotFoundException();
    }

    public void load(URL url) throws Exception {
	setURL(url);
	BrowseNodeModel root = loadTree(url);
	if (root != null) {
	    setRoot(root);
	} else {
	    //	    System.err.println("Err:"+root.toString());
	    throw new Exception();
	}
    }

    BrowseNodeModel loadTree(URL url) {
	BrowseNodeModel root = null;

	//NanoXML Code
	//XMLElement parser = new XMLElement();
        BrowseXMLElement mapElement = new BrowseXMLElement(getFrame(), this);

        InputStreamReader urlStreamReader = null;
        URLConnection uc = null;

        try {
           urlStreamReader = new InputStreamReader( url.openStream() ); }
        catch (AccessControlException ex) {
           getFrame().getController().errorMessage("Could not open URL "+url.toString()+". Access Denied.");
           System.err.println(ex);
           return null; }
        catch (Exception ex) {
           getFrame().getController().errorMessage("Could not open URL "+url.toString()+".");
           System.err.println(ex);
           // ex.printStackTrace();
           return null;
        }

	try {
	    mapElement.parseFromReader(urlStreamReader);
	} catch (Exception ex) {
	    System.err.println(ex);
	    return null;
	}
    // complete the arrow links:
    mapElement.processUnfinishedLinks(getLinkRegistry());
    root = (BrowseNodeModel) mapElement.getMapChild();
	return root;
    }

    /* (non-Javadoc)
     * @see freemind.modes.MindMap#setLinkInclinationChanged()
     */
    public void setLinkInclinationChanged() {
    }

	/* (non-Javadoc)
	 * @see freemind.modes.MindMap#getXml(java.io.Writer)
	 */
	public void getXml(Writer fileout) throws IOException {
		// nothing. 
		//FIXME: Implement me if you need me.
		throw new RuntimeException("Unimplemented method called.");
	}
}
