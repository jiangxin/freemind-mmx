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
/*$Id: LinkAdapter.java,v 1.3 2003-11-29 17:12:33 christianfoltin Exp $*/

package freemind.modes;
import freemind.modes.LineAdapter;
import freemind.main.FreeMindMain;

public abstract class LinkAdapter extends LineAdapter implements MindMapLink {

    String destinationLabel;
    String referenceText;
    MindMapNode source;

    public LinkAdapter(MindMapNode source,MindMapNode target,FreeMindMain frame)  {
        this(source,target, frame, "standardlinkcolor", "standardlinkstyle");
    }

    /** For derived classes.*/
    protected  LinkAdapter(MindMapNode source,MindMapNode target,FreeMindMain frame, String standardColorPropertyString, String standardStylePropertyString)  {
        super(target, frame, standardColorPropertyString, standardStylePropertyString);
        this.source=source;
        destinationLabel = null;
        referenceText = null;
    }

    public String getDestinationLabel() { return destinationLabel; }
    public String getReferenceText(){ return referenceText; }
    public MindMapNode getSource() { return source;}
    
    public void setSource(MindMapNode source) {this.source=source;}
    public void setDestinationLabel(String destinationLabel) { this.destinationLabel = destinationLabel; }
    public void setReferenceText(String referenceText) { this.referenceText = referenceText; }

//     public Object clone() {
//         try {
//             return super.clone();
//         } catch(java.lang.CloneNotSupportedException e) {
//             return null;
//         }
//     }


}
