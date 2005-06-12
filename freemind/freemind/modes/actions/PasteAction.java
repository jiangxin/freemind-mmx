/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2004  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 *
 * Created on 09.05.2004
 */
/*$Id: PasteAction.java,v 1.1.4.4.6.1 2005-06-12 12:59:55 dpolivaev Exp $*/

package freemind.modes.actions;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;

import freemind.controller.MindMapNodesSelection;
import freemind.controller.actions.ActionPair;
import freemind.controller.actions.ActorXml;
import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.CutNodeAction;
import freemind.controller.actions.generated.instance.PasteNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;
import freemind.main.XMLParseException;
import freemind.modes.ControllerAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapNodeModel;
import freemind.modes.mindmapmode.MindMapXMLElement;

public class PasteAction extends AbstractAction implements ActorXml {
    public static final String NODESEPARATOR = "<nodeseparator>";
    private static java.util.logging.Logger logger;
    private String text;
    private final ControllerAdapter c;
    public PasteAction(ControllerAdapter adapter) {
        super(
            adapter.getText("paste"),
            new ImageIcon(adapter.getResource("images/editpaste.png")));
        this.c = adapter;
		if(logger == null)
			logger = c.getFrame().getLogger(this.getClass().getName());

        this.text = adapter.getText("paste");
        setEnabled(false);
		this.c.getActionFactory().registerActor(this, getDoActionClass());
		
    }
    public void actionPerformed(ActionEvent e) {
        if (this.c.getClipboard() != null) {
            this.c.paste(
                this.c.getClipboard().getContents(c),
                this.c.getView().getSelected().getModel());
        }
    }

    /* (non-Javadoc)
      * @see freemind.controller.actions.ActorXml#act(freemind.controller.actions.generated.instance.XmlAction)
      */
    public void act(XmlAction action) {
    	PasteNodeAction pasteAction = (PasteNodeAction) action;
        _paste(
            c.cut.getTransferable(pasteAction.getTransferableContent()),
            c.getNodeFromID(pasteAction.getNode()),
            pasteAction.isAsSibling(),
            pasteAction.isIsLeft());
    }
    /* (non-Javadoc)
     * @see freemind.controller.actions.ActorXml#getDoActionClass()
     */
    public Class getDoActionClass() {
        return PasteNodeAction.class;
    }


	public PasteNodeAction getPasteNodeAction(Transferable t, NodeCoordinate coord) throws JAXBException {
		PasteNodeAction pasteAction =
			c.getActionXmlFactory().createPasteNodeAction();
		pasteAction.setNode(c.getNodeID(coord.target));
		pasteAction.setTransferableContent(c.cut.getTransferableContent(t));
		pasteAction.setAsSibling(coord.asSibling);
		pasteAction.setIsLeft(coord.isLeft);
		return pasteAction;
	}
    

	/** URGENT: Change this method. */
	public void paste(MindMapNode node, MindMapNode parent) {
		if (node != null) {
            insertNodeInto(node, parent);
			c.nodeStructureChanged(parent);
		}
	}


	public void paste(Transferable t, MindMapNode target, boolean asSibling, boolean isLeft) {
		try {
			PasteNodeAction pasteAction = getPasteNodeAction(t,new NodeCoordinate(target,asSibling, isLeft));
			long amountOfCuts = 1;
    	   	DataFlavorHandler[] dataFlavorHandlerList = getFlavorHandlers();
    	   	for (int i = 0; i < dataFlavorHandlerList.length; i++) {
                DataFlavorHandler handler = dataFlavorHandlerList[i];
                DataFlavor 		  flavor  = handler.getDataFlavor();
                if(t.isDataFlavorSupported(flavor)) {
                    amountOfCuts = handler.getNumberOfObjects(t.getTransferData(flavor), t);
                    break;
                }
            }
            CompoundAction compound = c.getActionXmlFactory().createCompoundAction();
    	   	for(long i = 0; i < amountOfCuts; ++i) {
    			CutNodeAction cutNodeAction = c.cut.getCutNodeAction(t, new NodeCoordinate(target,asSibling, isLeft));
    			compound.getCompoundActionOrSelectNodeActionOrCutNodeAction().add(cutNodeAction);
    	   	}

				
			// Undo-action
			c.getActionFactory().startTransaction(text);
			c.getActionFactory().executeAction(new ActionPair(pasteAction, compound));
			c.getActionFactory().endTransaction(text);
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	public static class NodeCoordinate {
		public MindMapNode target;
		public boolean asSibling;
		public boolean isLeft;
		public NodeCoordinate(MindMapNode target, boolean asSibling, boolean isLeft) {
			this.target = target;
			this.asSibling = asSibling;
			this.isLeft = isLeft;
		}
        public MindMapNode getNode() {
            if (asSibling) {
                MindMapNode parentNode = target.getParentNode();
                return (MindMapNode) parentNode.getChildAt(parentNode.getChildPosition(target) - 1);
            } else {
                logger.finest("getChildCount = " + target.getChildCount() + ", target = "+ target);
                return (MindMapNode) target.getChildAt(
                    target.getChildCount() - 1);
            }
        }
        public NodeCoordinate(MindMapNode node, boolean isLeft) {
        	this.isLeft = isLeft;
        	MindMapNode parentNode = node.getParentNode();
            int childPosition = parentNode.getChildPosition(node);
            if(childPosition == parentNode.getChildCount() - 1) {
        		target = parentNode;
        		asSibling = false;
        	} else {
        		target = (MindMapNode) parentNode.getChildAt(
				childPosition + 1);
				asSibling = true;
        	}
        }
	}

	private interface DataFlavorHandler {
	    void paste(Object TransferData, MindMapNode target, boolean asSibling, boolean isLeft, Transferable t) throws UnsupportedFlavorException, IOException;
	    long getNumberOfObjects(Object TransferData, Transferable transfer) throws UnsupportedFlavorException, IOException;
	    DataFlavor getDataFlavor();
	}
	
	
	
	private class FileListFlavorHandler implements DataFlavorHandler {

        public void paste(Object TransferData, MindMapNode target,
                boolean asSibling, boolean isLeft, Transferable t) {
            // TODO: Does not correctly interpret asSibling.
            List fileList = (List) TransferData;
            for (ListIterator it = fileList.listIterator(); it.hasNext();) {
                File file = (File) it.next();
                MindMapNode node = c.newNode(file.getName(), target.getMap());
                node.setLink(file.getAbsolutePath());
                insertNodeIntoNoEvent(node, target, asSibling);
            }
            c.nodeStructureChanged((MindMapNode) (asSibling ? target
                    .getParent() : target));
        }
        

        public long getNumberOfObjects(Object TransferData, Transferable transfer) {
            return ((List) TransferData).size();
        }


        public DataFlavor getDataFlavor() {
            return MindMapNodesSelection.fileListFlavor;
        }
	    
	}
	
	private class MindMapNodesFlavorHandler implements DataFlavorHandler {

        public void paste(Object TransferData, MindMapNode target, boolean asSibling, boolean isLeft, Transferable t) {
			  //System.err.println("mindMapNodesFlavor");
			 String textFromClipboard =
				(String)TransferData;
			 if (textFromClipboard != null) {
               String[] textLines = textFromClipboard.split(NODESEPARATOR);
               if (textLines.length > 1) {
                   c.getFrame().setWaitingCursor(true);
               }
               for (int i = 0; i < textLines.length; ++i) {
                   //logger.info(textLines[i]+", "+ target+", "+ asSibling);
                   MindMapNodeModel newModel = pasteXMLWithoutRedisplay(
                           textLines[i], target, asSibling);
                   // additional code for left/right decision:
                   newModel.setLeft(isLeft);
               }
           }
        }

        public long getNumberOfObjects(Object TransferData, Transferable transfer) {
            String textFromClipboard = (String) TransferData;
            if (textFromClipboard != null) {
                String[] textLines = textFromClipboard.split(NODESEPARATOR);
                return textLines.length;
            }
            return 0;
        }

        public DataFlavor getDataFlavor() {
            return MindMapNodesSelection.mindMapNodesFlavor;
        }
    }
	private class HtmlFlavorHandler implements DataFlavorHandler {

        public void paste(Object TransferData, MindMapNode target,
                boolean asSibling, boolean isLeft, Transferable t)
                throws UnsupportedFlavorException, IOException {
            //System.err.println("htmlFlavor");
            String textFromClipboard = (String) TransferData;
            // ^ This outputs transfer data to standard output. I don't know
            // why.
            MindMapNode pastedNode = pasteStringWithoutRedisplay((String) t
                    .getTransferData(DataFlavor.stringFlavor), target,
                    asSibling);

            textFromClipboard = textFromClipboard.replaceAll("<!--.*?-->", ""); // remove
                                                                                // HTML
                                                                                // comment
            String[] links = textFromClipboard
                    .split("<[aA][^>]*[hH][rR][eE][fF]=\"");

            MindMapNode linkParentNode = null;
            URL referenceURL = null;
            boolean baseUrlCanceled = false;

            for (int i = 1; i < links.length; i++) {
                String link = links[i].substring(0, links[i].indexOf("\""));
                String textWithHtml = links[i].replaceAll("^[^>]*>", "")
                        .replaceAll("</[aA]>[\\s\\S]*", "");
                String text = Tools.toXMLUnescapedText(textWithHtml.replaceAll(
                        "\\n", "").replaceAll("<[^>]*>", "").trim());
                if (text.equals("")) {
                    text = link;
                }
                URL linkURL = null;
                try {
                    linkURL = new URL(link);
                } catch (MalformedURLException ex) {
                    try {
                        // Either invalid URL or relative URL
                        if (referenceURL == null && !baseUrlCanceled) {
                            String referenceURLString = JOptionPane
                                    .showInputDialog(c
                                            .getText("enter_base_url"));
                            if (referenceURLString == null) {
                                baseUrlCanceled = true;
                            } else {
                                referenceURL = new URL(referenceURLString);
                            }
                        }
                        linkURL = new URL(referenceURL, link);
                    } catch (MalformedURLException ex2) {
                    }
                }
                if (linkURL != null) {
                    if (links.length == 2 & pastedNode != null) {
                        // pastedNode != null iff the number of pasted lines is
                        // one
                        // The firts element in links[] array is never a link, therefore
                        // the condition links.length == 2 actually says "there is one link".
                        // Set link directly into node
                        ((MindMapNodeModel) pastedNode).setLink(linkURL
                                .toString());
                        break;
                    }
                    if (linkParentNode == null) {
                        linkParentNode = c.newNode("Links", target.getMap());
                        // Here we cannot set bold, because linkParentNode.font is null
                        insertNodeInto(linkParentNode, target);
                        ((NodeAdapter) linkParentNode).setBold(true);
                    }
                    MindMapNode linkNode = c.newNode(text, target.getMap());
                    linkNode.setLink(linkURL.toString());
                    insertNodeInto(linkNode, linkParentNode);
                }
            }
        }

        public long getNumberOfObjects(Object TransferData, Transferable transfer) throws UnsupportedFlavorException, IOException {
            return ((String) (String) transfer
                    .getTransferData(DataFlavor.stringFlavor)).split("\n").length;
        }

        public DataFlavor getDataFlavor() {
            return MindMapNodesSelection.htmlFlavor;
        }
	    
	}
	
	private class StringFlavorHandler implements DataFlavorHandler {

        public void paste(Object TransferData, MindMapNode target,
                boolean asSibling, boolean isLeft, Transferable t)
                throws UnsupportedFlavorException, IOException {
            //System.err.println("stringFlavor");
            String textFromClipboard = (String) t
                    .getTransferData(DataFlavor.stringFlavor);
            pasteStringWithoutRedisplay(textFromClipboard, target, asSibling);
        }

        public long getNumberOfObjects(Object TransferData, Transferable transfer) {
            return ((String) TransferData).split("\n").length;
        }

        public DataFlavor getDataFlavor() {
            return DataFlavor.stringFlavor;
        }
	    
	}
	/*
	 *
	 */
	private void _paste(Transferable t, MindMapNode target, boolean asSibling, boolean isLeft) {
	   if (t == null) {
		  return; }
	   try {
		   // Uncomment to print obtained data flavours

		   /*
		   DataFlavor[] fl = t.getTransferDataFlavors(); 
		   for (int i = 0; i < fl.length; i++) {
			  System.out.println(fl[i]); }
		   */

	   	DataFlavorHandler[] dataFlavorHandlerList = getFlavorHandlers();
	   	for (int i = 0; i < dataFlavorHandlerList.length; i++) {
            DataFlavorHandler handler = dataFlavorHandlerList[i];
            DataFlavor 		  flavor  = handler.getDataFlavor();
            if(t.isDataFlavorSupported(flavor)) {
                handler.paste(t.getTransferData(flavor), target, asSibling, isLeft, t);
                break;
            }
        }
  	   c.nodeStructureChanged((MindMapNode) (asSibling ? target.getParent() : target)); }
	   catch (Exception e) { e.printStackTrace(); }
	   c.getFrame().setWaitingCursor(false);        
	}

    /**
     * @return
     */
    private DataFlavorHandler[] getFlavorHandlers() {
        DataFlavorHandler[] dataFlavorHandlerList = new DataFlavorHandler[] {
                    new FileListFlavorHandler(),
                    new MindMapNodesFlavorHandler(), new HtmlFlavorHandler(),
                    new StringFlavorHandler() };
        return dataFlavorHandlerList;
    }
    private MindMapNodeModel pasteXMLWithoutRedisplay(String pasted, MindMapNode target)
	   throws XMLParseException  {
	   return pasteXMLWithoutRedisplay(pasted, target, /*asSibling=*/false); }

	public MindMapNodeModel pasteXMLWithoutRedisplay(String pasted, MindMapNode target, boolean asSibling)
	   throws XMLParseException {
	   // Call nodeStructureChanged(target) after this function.
	   try {
		  MindMapXMLElement element = new MindMapXMLElement(c.getFrame(), target.getMap());
		  element.parseFromReader(new StringReader(pasted));
		  MindMapNodeModel node = (MindMapNodeModel)element.getUserObject();
		  element.processUnfinishedLinks(c.getModel().getLinkRegistry());
		  // now, the import is finished. We can inform others about the new nodes:
		  if (asSibling) {
			 MindMapNode parent = target.getParentNode();
			 insertNodeInto(node, parent, parent.getChildPosition(target)); }
		  else {
			 insertNodeInto(node, target); }
		  c.invokeHooksRecursively(node, c.getModel());
		  return node; }
	   catch (IOException ee) { ee.printStackTrace(); return null; }}

    static final Pattern nonLinkCharacter = Pattern.compile("[ \n()'\",;]");

	/**
	 * Paste String (as opposed to other flavours)
	 *
	 * Split the text into lines; determine the new tree structure
	 * by the number of leading spaces in lines.  In case that
	 * trimmed line starts with protocol (http:, https:, ftp:),
	 * create a link with the same content.
	 *
	 * If there was only one line to be pasted, return the pasted node, null otherwise.
	 */

	private MindMapNode pasteStringWithoutRedisplay(String textFromClipboard, MindMapNode parent,
													boolean asSibling) {

	   Pattern mailPattern = Pattern.compile("([^@ <>\\*']+@[^@ <>\\*']+)");

	   String[] textLines = textFromClipboard.split("\n");
              
	   if (textLines.length > 1) {
		  c.getFrame().setWaitingCursor(true); }

	   MindMapNode realParent = null;
	   if (asSibling) {
		  // When pasting as sibling, we use virtual node as parent. When the pasting to
		  // virtual node is completed, we insert the children of that virtual node to
		  // the parrent of real parent.
		  realParent = parent;
		  parent = new MindMapNodeModel(c.getFrame(), parent.getMap()); }

	   ArrayList parentNodes = new ArrayList();
	   ArrayList parentNodesDepths = new ArrayList();

	   parentNodes.add(parent);
	   parentNodesDepths.add(new Integer(-1));

	   String[] linkPrefixes = { "http://", "ftp://", "https://" };

	   MindMapNode pastedNode = null;

	   for (int i = 0; i < textLines.length; ++i) {
		  String text = textLines[i];
		  text = text.replaceAll("\t","        ");
		  if (text.matches(" *")) {
			 continue; }
          
		  int depth = 0;
		  while (depth < text.length() && text.charAt(depth) == ' ') {
			 ++depth; }
		  String visibleText = text.trim();

		  // If the text is a recognizable link (e.g. http://www.google.com/index.html),
		  // make it more readable by look nicer by cutting off obvious prefix and other
		  // transforamtions.

		  if (visibleText.matches("^http://(www\\.)?[^ ]*$")) {
			 visibleText = visibleText.replaceAll("^http://(www\\.)?","").
				replaceAll("(/|\\.[^\\./\\?]*)$","").replaceAll("((\\.[^\\./]*\\?)|\\?)[^/]*$"," ? ...").replaceAll("_|%20"," ");
			 String[] textParts = visibleText.split("/");
			 visibleText = "";
			 for (int textPartIdx = 0; textPartIdx < textParts.length; textPartIdx++) {
				if (textPartIdx > 0 ) {
				   visibleText += " > "; }
				visibleText += textPartIdx == 0 ? textParts[textPartIdx] : 
				   Tools.firstLetterCapitalized(textParts[textPartIdx].replaceAll("^~*","")); }}

		  MindMapNode node = c.newNode(visibleText, parent.getMap());
		  if (textLines.length == 1) {
			 pastedNode = node; }

		  // Heuristically determine, if there is a mail.

		  Matcher mailMatcher = mailPattern.matcher(visibleText);
		  if (mailMatcher.find()) {
			 node.setLink("mailto:"+mailMatcher.group()); }

		  // Heuristically determine, if there is a link. Because this is
		  // heuristic, it is probable that it can be improved to include
		  // some matches or exclude some matches.

		  for (int j = 0; j < linkPrefixes.length; j++) {
			 int linkStart = text.indexOf(linkPrefixes[j]);
			 if (linkStart != -1) {
				int linkEnd = linkStart;
				while (linkEnd < text.length() &&
					   !nonLinkCharacter.matcher(text.substring(linkEnd,linkEnd+1)).matches()) {
				   linkEnd++; }
				node.setLink(text.substring(linkStart,linkEnd)); }}          

		  // Determine parent among candidate parents
		  // Change the array of candidate parents accordingly

		  for (int j = parentNodes.size()-1; j >= 0; --j) {
			 if (depth > ((Integer)parentNodesDepths.get(j)).intValue()) {
				for (int k = j+1; k < parentNodes.size(); ++k) {
				   parentNodes.remove(k);
				   parentNodesDepths.remove(k); }
				MindMapNode target = (MindMapNode)parentNodes.get(j);
				insertNodeIntoNoEvent(node, target);
				parentNodes.add(node);
				parentNodesDepths.add(new Integer(depth));
				break; }}}

	   if (asSibling) {
		  for (Iterator i=parent.childrenUnfolded(); /*children.iterator()*/ i.hasNext(); ) {
			 insertNodeIntoNoEvent((MindMapNode)i.next(), realParent, asSibling); }
		  c.nodeStructureChanged(realParent.getParentNode()); }
	   else {
		  c.nodeStructureChanged(parent); }
	   // ^ Do not fire any event when inserting single lines. Fire the event
	   // when all the lines are inserted.
	   return pastedNode;
	}
    /**
     * @param node
     * @param target
     */
    private void insertNodeIntoNoEvent(MindMapNode node, MindMapNode target) {
		c.getModel().insertNodeIntoNoEvent(node, target);
    }
    /**
     * @param node
     * @param realParent
     * @param asSibling
     */
    private void insertNodeIntoNoEvent(MindMapNode node, MindMapNode realParent, boolean asSibling) {
    	c.getModel().insertNodeIntoNoEvent(node, realParent, asSibling);
    }

	/**
	 * @param node
	 * @param parent
	 * @param i
	 */
	private void insertNodeInto(MindMapNodeModel node, MindMapNode parent, int i) {
		c.getModel().insertNodeInto(node, parent,i);
	}
	private void insertNodeInto(MindMapNode node, MindMapNode parent) {
		c.getModel().insertNodeInto(node, parent);
	}
 
    
    

}