
/*FreeMindget - A Program for creating and viewing Mindmaps
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
/*$Id: MultipleImage.java,v 1.1 2003-11-03 11:02:44 sviles Exp $*/

package freemind.view.mindmapview;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.ImageIcon; 
import java.util.Vector;
import java.net.URL;

public class MultipleImage extends ImageIcon {
    private Vector mImages = new Vector();
    private double zoomFactor=1;
    MultipleImage(double zoom) 
    { 
        zoomFactor = zoom;
    };
//     MultipleImage(URL location) 
//     { 
//         ImageIcon firstIcon = new ImageIcon(location); 
//         mImages.add(firstIcon ); 
//     };
//     MultipleImage(String filename) 
//     {
//         ImageIcon firstIcon = new ImageIcon(filename); 
//         mImages.add(firstIcon );  
//     };
    public int getImageCount() {
        return mImages.size();
    };

    public void addImage(ImageIcon image) 
    { 
        mImages.add(image); 
    };

    public void paintIcon(Component c,
                          Graphics g,
                          int x,
                          int y) 
    { 
        int myX = x;
        int myHeight = getIconHeight();
        for(int i = 0 ; i < mImages.size(); i++) {
            ImageIcon currentIcon = ((ImageIcon) mImages.get(i));
            int px,py,pwidth, pheight;
            px = myX;
            py = y /* center: */ + ( myHeight - (int)(currentIcon.getIconHeight()* zoomFactor)) /2;
            pwidth = (int) (currentIcon.getIconWidth() * zoomFactor);
            pheight = (int) (currentIcon.getIconHeight() * zoomFactor);
            /* code from ImageIcon.*/
            if(currentIcon.getImageObserver() == null) {
                g.drawImage(currentIcon.getImage(), px, py, pwidth, pheight, c);
            } else {
                g.drawImage(currentIcon.getImage(), px, py, pwidth, pheight, currentIcon.getImageObserver());
            }
            /* end code*/
            myX += pwidth;
        }
    };

    public int getIconWidth() 
    {
        int myX = 0;
        for(int i = 0 ; i < mImages.size(); i++) {
            myX += ((ImageIcon) mImages.get(i)).getIconWidth();
        }
//         System.out.println("width: "+myX);
        return (int)(myX * zoomFactor);
    }

    public int getIconHeight()
    {
        int myY = 0;
        for(int i = 0 ; i < mImages.size(); i++) {
            int otherHeight = ((ImageIcon) mImages.get(i)).getIconHeight();
            if(otherHeight > myY) 
                myY = otherHeight;
        }
//         System.out.println("height: "+myY);
        return (int) (myY * zoomFactor);
    }
        

};
