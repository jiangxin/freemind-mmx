/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2006  Christian Foltin <christianfoltin@users.sourceforge.net>
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
/*$Id: HtmlTools.java,v 1.1.2.2 2006-05-21 20:11:09 christianfoltin Exp $*/

package freemind.main;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.swing.text.BadLocationException;

/** */
public class HtmlTools {

    private static HtmlTools sInstance = new HtmlTools();

    /**
     * 
     */
    private HtmlTools() {
        super();

    }

    public static HtmlTools getInstance() {
        return sInstance;
    }

    public String toXhtml(String htmlText) {
        StringReader reader = new StringReader(htmlText);
        StringWriter writer = new StringWriter();
        try {
            XHTMLWriter.html2xhtml(reader, writer);
            return writer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        // fallback:
        htmlText = htmlText.replaceAll("<", "&gt;");
        htmlText = htmlText.replaceAll(">", "&lt;");
        return htmlText;
    }

    public String toHtml(String xhtmlText) {
        // Remove '/' from <.../> of elements that do not have '/' there in HTML
        return xhtmlText.replaceAll("<((" + "br|area|base|basefont|"
                + "bgsound|button|col|colgroup|embed|hr"
                + "|img|input|isindex|keygen|link|meta"
                + "|object|plaintext|spacer|wbr" + ")[^>]*)/>", "<$1>");
    }

}
