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
/*$Id: LinkAdapter.java,v 1.1 2003-11-09 22:09:26 christianfoltin Exp $*/

package freemind.modes;
import freemind.modes.LineAdapter;
import freemind.main.FreeMindMain;

public abstract class LinkAdapter extends LineAdapter implements MindMapLink {

    String destinationLabel;
    String referenceText;

    public LinkAdapter(MindMapNode target,FreeMindMain frame)  {
        this(target, frame, "standardlinkcolor", "standardlinkstyle");
    }

    /** For derived classes.*/
    protected  LinkAdapter(MindMapNode target,FreeMindMain frame, String standardColorPropertyString, String standardStylePropertyString)  {
        super(target, frame, standardColorPropertyString, standardStylePropertyString);
        destinationLabel = null;
        referenceText = null;
    }

    public String getDestinationLabel() { return destinationLabel; }
    public String getReferenceText(){ return referenceText; }

    public void setDestinationLabel(String destinationLabel) { this.destinationLabel = destinationLabel; }
    public void setReferenceText(String referenceText) { this.referenceText = referenceText; }


}
