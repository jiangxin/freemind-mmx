/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000  Joerg Mueller <joergmueller@bigfoot.com>
 *See COPYING f or Details
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

package freemind.modes.mindmapmode;

import freemind.modes.MindMapNode;
import java.util.*;
import javax.swing.tree.*;

/**A type-secure MindMapNode-Vector*/
//Changed this from TreeNodeVector to NodeModelVector to reduce downcasting in MapModel
//Changed this from NodeModelVector to MindMapNodeVector
public class MindMapNodeVector {
    
    private Vector vector;

    //basic operations needed by NodeModel
    public Enumeration elements() {
	return vector.elements();
    }

    public NodeModel elementAt( int index ) {
	return (NodeModel)vector.elementAt( index );
    }

    public int size() {
	return vector.size();
    }

    public int indexOf( MindMapNode node ) {
	return vector.indexOf( node );
    }

    public void add( int index, MindMapNode element ) {
	vector.add( index, element );
    }

    public boolean add( MindMapNode element ) {
	return vector.add( element );
    }

    public void addElement( MindMapNode element ) {
	vector.addElement( element );
    }

    public MindMapNode remove( int index ) {
	return (MindMapNode)vector.remove( index );
    }

    public boolean remove( MindMapNode node ) {
	return vector.remove( node );
    }

    //constructor
    public MindMapNodeVector() {
	vector = new Vector();
    }
}
    




