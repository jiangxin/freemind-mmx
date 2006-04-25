/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
*
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
/*
 * Created on 08.05.2005
 *
 */
package freemind.controller.filter.util;

import freemind.main.Resources;

/**
 * @author dimitri
 * 08.05.2005
 */
public class TranslatedString{
    private String foreignString;
    private String key;
    private TranslatedString(){
    }
    public TranslatedString(String key) {
        this.key = key;
        foreignString =Resources.getInstance().getResourceString(key);
    }
    static public TranslatedString literal(String literal){
        TranslatedString result = new TranslatedString();
        result.key = literal;
        result.foreignString = literal;
        return result;
    }
    public boolean equals(Object o){
        if (o instanceof TranslatedString){
            TranslatedString ts =  (TranslatedString)o;
            return key.equals(ts.key);
        }
        return key.equals(o);
    }

    public String toString(){
        return foreignString;
    }
}
