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
 * Created on 19.04.2004
 *
 */
package accessories.plugins;

import java.awt.EventQueue;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.ListIterator;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import freemind.main.FixedHTMLWriter;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;
import freemind.view.mindmapview.MapView;

/**
 * @author Dimitri Polivaev
 *
 */
public class SplitNode extends MindMapNodeHookAdapter {

	/**
	 * 
	 */
	public SplitNode() {
		super();
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.NodeHook#invoke(freemind.modes.MindMapNode, java.util.List)
	 */
	public void invoke(MindMapNode node) {
        super.invoke(node);
        final List list = getMindMapController().getSelecteds();
        final ListIterator listIterator = list.listIterator();
        while(listIterator.hasNext()){
            MindMapNode next = (MindMapNode)listIterator.next();
            splitNode(next);
        }
	}

    private void splitNode(MindMapNode node) {
        if(node.isRoot()){
            return;
        }
        String text = node.toString();
        String[] parts = splitNode(text);
        if(parts == null || parts.length == 1){
            return;
        }
        final MindMapController c = getMindMapController();
        int firstPartNumber = 0;
        while(parts[firstPartNumber]==null) {
            firstPartNumber++;
        }
        c.setNodeText(node, parts[firstPartNumber]);
        MindMapNode parent = node.getParentNode();
        final int nodePosition = parent.getChildPosition(node) + 1;
        for(int i = parts.length-1; i>firstPartNumber; i--){
            final MindMapNode lowerNode = c.addNewNode(
                    parent, nodePosition, node.isLeft()
                    );
            final String part = parts[i];
            if (part == null){
                continue;
            }
            lowerNode.setColor(node.getColor());
            lowerNode.setFont(node.getFont());
            c.setNodeText(lowerNode, part);
            EventQueue.invokeLater(new Runnable(){
                public void run() {
                    final MapView mapView = c.getView();
                    mapView.toggleSelected(mapView.getNodeView(lowerNode));
                }                
            });
        }
    }

    private String[] splitNode(String text) {
        if(text.startsWith("<html>")){
            String[] parts = null;
            HTMLEditorKit kit = new HTMLEditorKit();
            HTMLDocument doc = new HTMLDocument();
            StringReader buf = new StringReader(text);
            try {
                kit.read(buf, doc, 0);
                Element parent = getParentElement(doc);
                final int elementCount = parent.getElementCount();
                parts = new String[elementCount];
                for(int i = 0; i < elementCount; i++){
                    Element current = parent.getElement(i);
                    final int start = current.getStartOffset();
                    final int end = current.getEndOffset();
                    final String paragraphText = doc.getText(start, end - start).trim();
                    if(paragraphText.length() > 0){
                        StringWriter out = new StringWriter();
                        new FixedHTMLWriter(out, doc, start, end - start).write();                
                        parts[i] = out.toString();
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (BadLocationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return parts;
        }
        return text.split("\n");
    }

    private Element getParentElement(HTMLDocument doc) {
        final Element htmlRoot = doc.getDefaultRootElement();
        Element parentCandidate = htmlRoot.getElement(htmlRoot.getElementCount()-1);
        do{
            if(parentCandidate.getElementCount() > 1){
                return parentCandidate;
            }
            parentCandidate = parentCandidate.getElement(0);
        } while (! (parentCandidate.isLeaf() || parentCandidate.getName().equalsIgnoreCase("p-implied")));
        return null;
    }

}
