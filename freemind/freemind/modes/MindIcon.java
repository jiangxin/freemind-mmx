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
/*$Id: MindIcon.java,v 1.1.18.5.2.1.2.2 2005-12-06 19:47:30 dpolivaev Exp $*/

package freemind.modes;

import java.awt.Component;
import java.net.URL;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import freemind.controller.Controller;
import freemind.main.FreeMindMain;
import freemind.main.Resources;

/**
 * This class represents a MindIcon than can be applied
 * to a node or a whole branch.
 */
public class MindIcon implements Comparable{
    private String name;
    private String description;
    private int number = UNKNOWN;
    /**
     * Stores the once created ImageIcon.
     */
    private ImageIcon   associatedIcon;
    private static Vector mAllIconNames;
    private static ImageIcon iconNotFound;
    /**
     * Set of all created icons. Name -> MindIcon
     */
    private static HashMap createdIcons = new HashMap();
    private static final int UNKNOWN = -2;
    private JComponent component = null;
    

    private MindIcon(String name) {
       setName(name); 
       associatedIcon=null;
    }

    /**
     * @param iconName
     * @param icon
     */
    private MindIcon(String name, ImageIcon icon) {
        setName(name); 
        associatedIcon=icon;
    }

    public String toString() {
        return "Icon_name: "+name; 
    }

    /**
       * Get the value of name.
       * @return Value of name.
       */
    public String getName() {
       // DanPolansky: it's essential that we do not return null
       // for saving of the map.
       return name == null ? "notfound" : name; }
    
    /**
       * Set the value of name.
       * @param v  Value to assign to name.
       */
    public void setName(String name) {
        
        this.name = name; 
        return;

        /* here, we must check, whether the name is allowed.*/

        // DanPolansky: I suggest to avoid any checking. If the icon with the name
        // does not exist, let's keep the name and save it again anyway. Let us
        // imagine the set of icons expanding and changing in the future.

        //Vector allIconNames = getAllIconNames();
        //for(int i = 0; i < allIconNames.size(); ++i) {
        //    if(((String) allIconNames.get(i)).equals(v)) {
        //        //System.out.println("Icon name: " + v);
        //        this.name = v; 
        //        return;
        //    }
        //}
        // throw new IllegalArgumentException("'"+v+"' is not a known icon.");
        // DanPolansky: we want to parse the file though. Not existent icon is not
        // that a big tragedy.
    }
    
    
    /**
       * Get the value of description (in local language).
       * @return Value of description.
       */
    public String getDescription(FreeMindMain frame) {
        String resource = new String("icon_"+getName());
        return frame.getResourceString(resource); 
    }
    
    public String getIconFileName() {
        return getIconsPath()+getIconBaseFileName();
    }

    public String getIconBaseFileName() {
        return getName()+".png";
    }

    public static String getIconsPath() {
        return "images/icons/";
    }

    public ImageIcon getIcon() {
        // We need the frame to be able to obtain the resource URL of the icon.
        if (iconNotFound == null) {
           iconNotFound = new ImageIcon(Resources.getInstance().getResource("images/IconNotFound.png")); }

        if(associatedIcon != null)
            return associatedIcon;
        if ( name != null ) {
           URL imageURL = Resources.getInstance().getResource(getIconFileName());
           ImageIcon icon = imageURL == null ? iconNotFound : new ImageIcon(imageURL);
           setIcon(icon);
           return icon; }
        else {
           setIcon(iconNotFound);
           return iconNotFound; }}      
            
    /**
       * Set the value of icon.
       * @param v  Value to assign to icon.
       */
    protected void setIcon(ImageIcon  _associatedIcon) {
       this.associatedIcon = _associatedIcon; }

    public static Vector getAllIconNames () {
        if(mAllIconNames != null)
            return mAllIconNames;
        Vector mAllIconNames = new Vector();
        mAllIconNames.add("help");
        mAllIconNames.add("messagebox_warning");
        mAllIconNames.add("idea");
        mAllIconNames.add("button_ok");
        mAllIconNames.add("button_cancel");
        mAllIconNames.add("full-1");
        mAllIconNames.add("full-2");
        mAllIconNames.add("full-3");
        mAllIconNames.add("full-4");
        mAllIconNames.add("full-5");
        mAllIconNames.add("full-6");
        mAllIconNames.add("full-7");
        mAllIconNames.add("back");
        mAllIconNames.add("forward");
        mAllIconNames.add("attach");
        mAllIconNames.add("ksmiletris");
        mAllIconNames.add("clanbomber");
        mAllIconNames.add("desktop_new");
        mAllIconNames.add("flag");
        mAllIconNames.add("gohome");
        mAllIconNames.add("kaddressbook");
        mAllIconNames.add("knotify");
        mAllIconNames.add("korn");
        mAllIconNames.add("Mail");
        mAllIconNames.add("password");
        mAllIconNames.add("pencil");
        mAllIconNames.add("stop");
        mAllIconNames.add("wizard");
        mAllIconNames.add("xmag");
        mAllIconNames.add("bell");
        mAllIconNames.add("bookmark");
        mAllIconNames.add("penguin");
        mAllIconNames.add("licq");
        return mAllIconNames;
    }

    public static MindIcon factory(String iconName){
    		if(createdIcons.containsKey(iconName)){
    			return (MindIcon) createdIcons.get(iconName);
    		}
    		MindIcon icon = new MindIcon(iconName);
    		createdIcons.put(iconName, icon);
    		return icon;
    }

    /**
     * @param key
     * @param icon
     * @return
     */
    public static MindIcon factory(String iconName, ImageIcon icon) {
		if(createdIcons.containsKey(iconName)){
			return (MindIcon) createdIcons.get(iconName);
		}
		MindIcon mindIcon = new MindIcon(iconName, icon);
		getAllIconNames ().add(iconName);
		createdIcons.put(iconName, mindIcon);
		return mindIcon;
    }
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        if (o instanceof MindIcon){
            MindIcon icon = (MindIcon)o;
            int i1 = getNumber();
            int i2 = icon.getNumber();
            return i1 < i2 ? -1 : i1 == i2 ? 0 : +1;
        }
        throw new ClassCastException() ;
    }
    
    
    private int getNumber() {
        if(number == UNKNOWN){
            number = getAllIconNames ().indexOf(name);
        }
        return number;
    }

    /**
     * @return
     */
    public JComponent getRendererComponent() {
        if(component == null){
            component = new JLabel(getIcon());
        }
        return component;
    }

}
