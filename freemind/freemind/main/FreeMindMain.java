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
/*$Id: FreeMindMain.java,v 1.2 2001-03-24 22:45:45 ponder Exp $*/

package freemind.main;

import freemind.view.mindmapview.MapView;
import freemind.controller.MenuBar;
import java.util.ResourceBundle;
import java.awt.Container;
import java.net.URL;
import javax.swing.JLayeredPane;

public interface FreeMindMain {
    public MapView getView();

    public void setView(MapView view);

    public MenuBar getFreeMindMenuBar();

    /**Returns the ResourceBundle with the current language*/
    public ResourceBundle getResources();

    public Container getContentPane();
    
    public void out (String msg);

    public void err (String msg);

    /**Try to open the document in an external application*/
    public void openDocument(URL location) throws Exception;

    /**remove this!*/
    public void repaint();

    public URL getResource(String name);

    public String getProperty(String key);

    public JLayeredPane getLayeredPane();

    public void setTitle(String title);
}
