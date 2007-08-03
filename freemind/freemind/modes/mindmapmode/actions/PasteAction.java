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
/* $Id: PasteAction.java,v 1.1.2.2.2.11 2007-08-03 19:11:35 dpolivaev Exp $ */

package freemind.modes.mindmapmode.actions;

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
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import freemind.controller.MindMapNodesSelection;
import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.CutNodeAction;
import freemind.controller.actions.generated.instance.PasteNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.HtmlTools;
import freemind.main.Tools;
import freemind.main.XMLParseException;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapNodeModel;
import freemind.modes.mindmapmode.MindMapXMLElement;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;

public class PasteAction extends AbstractAction implements ActorXml {
    private static java.util.logging.Logger logger;
    private List newNodes; //only for Transferable with mindMapNodesFlavor 
    private String text;
    private final MindMapController pMindMapController;
    public PasteAction(MindMapController adapter) {
        super(
            adapter.getText("paste"),
            new ImageIcon(adapter.getResource("images/editpaste.png")));
        this.pMindMapController = adapter;
		if(logger == null)
			logger = pMindMapController.getFrame().getLogger(this.getClass().getName());

        this.text = adapter.getText("paste");
        setEnabled(false);
		this.pMindMapController.getActionFactory().registerActor(this, getDoActionClass());

    }
    public void actionPerformed(ActionEvent e) {
        if (this.pMindMapController.getClipboard() != null) {
            this.pMindMapController.paste(
                this.pMindMapController.getClipboard().getContents(pMindMapController),
                this.pMindMapController.getView().getSelected().getModel());
        }
    }

    /* (non-Javadoc)
      * @see freemind.controller.actions.ActorXml#act(freemind.controller.actions.generated.instance.XmlAction)
      */
    public void act(XmlAction action) {
    	PasteNodeAction pasteAction = (PasteNodeAction) action;
        Object transferable = pasteAction.getTransferableContent();
        _paste(
            pMindMapController.cut.getTransferable(pasteAction.getTransferableContent()),
            pMindMapController.getNodeFromID(pasteAction.getNode()),
            pasteAction.getAsSibling(),
            pasteAction.getIsLeft());
    }
    /* (non-Javadoc)
     * @see freemind.controller.actions.ActorXml#getDoActionClass()
     */
    public Class getDoActionClass() {
        return PasteNodeAction.class;
    }


	public PasteNodeAction getPasteNodeAction(Transferable t, NodeCoordinate coord)  {
		PasteNodeAction pasteAction =
			new PasteNodeAction();
		pasteAction.setNode(pMindMapController.getNodeID(coord.target));
		pasteAction.setTransferableContent(pMindMapController.cut.getTransferableContent(t));
		pasteAction.setAsSibling(coord.asSibling);
		pasteAction.setIsLeft(coord.isLeft);
		return pasteAction;
	}


	/** URGENT: Change this method. */
	public void paste(MindMapNode node, MindMapNode parent) {
		if (node != null) {
            insertNodeInto(node, parent);
			pMindMapController.nodeStructureChanged(parent);
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
            CompoundAction compound = new CompoundAction();
            for(long i = 0; i < amountOfCuts; ++i) {
                CutNodeAction cutNodeAction = pMindMapController.cut.getCutNodeAction(t, new NodeCoordinate(target,asSibling, isLeft));
                compound.addChoice(cutNodeAction);
            }

			// Undo-action
			pMindMapController.getActionFactory().startTransaction(text);
			pMindMapController.getActionFactory().executeAction(new ActionPair(pasteAction, compound));
			pMindMapController.getActionFactory().endTransaction(text);
		} catch (UnsupportedFlavorException e) {
            freemind.main.Resources.getInstance().logException(e);
        } catch (IOException e) {
            freemind.main.Resources.getInstance().logException(e);
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
                MindMapNode node = pMindMapController.newNode(file.getName(), target.getMap());
                node.setLeft(isLeft);
                node.setLink(file.getAbsolutePath());
                insertNodeInto(node, target, asSibling);
            }
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
               String[] textLines = textFromClipboard.split(ModeController.NODESEPARATOR);
               if (textLines.length > 1) {
                   pMindMapController.getFrame().setWaitingCursor(true);
               }
               for (int i = 0; i < textLines.length; ++i) {
                   //logger.info(textLines[i]+", "+ target+", "+ asSibling);
                   MindMapNodeModel newModel = pasteXMLWithoutRedisplay(
                           textLines[i], target, asSibling, true, isLeft);
                   newModel.setLeft(isLeft);
               }
           }
        }

        public long getNumberOfObjects(Object TransferData, Transferable transfer) {
            String textFromClipboard = (String) TransferData;
            if (textFromClipboard != null) {
                String[] textLines = textFromClipboard.split(ModeController.NODESEPARATOR);
                return textLines.length;
            }
            return 0;
        }

        public DataFlavor getDataFlavor() {
            return MindMapNodesSelection.mindMapNodesFlavor;
        }
    }

    private static final Pattern HREF_PATTERN = Pattern.compile(
   		 "<html>\\s*<body>\\s*<a\\s+href=\"([^>]+)\">(.*)</a>\\s*</body>\\s*</html>"
   		 );
    
        private class DirectHtmlFlavorHandler implements DataFlavorHandler {
		public void paste(Object transferData, MindMapNode target,
                             boolean asSibling, boolean isLeft, Transferable t)
              throws UnsupportedFlavorException, IOException {
              String textFromClipboard = (String) transferData;              
              // ^ This outputs transfer data to standard output. I don't know
              // why.
             //{ Alternative pasting of HTML
              pMindMapController.getFrame().setWaitingCursor(true);
             textFromClipboard = textFromClipboard.
                replaceFirst("(?i)(?s)<head>.*</head>","").
                replaceFirst("(?i)(?s)^.*<html[^>]*>","<html>").
                replaceFirst("(?i)(?s)<body [^>]*>","<body>").
                replaceAll("(?i)(?s)<script.*?>.*?</script>","").
                replaceAll("(?i)(?s)</?tbody.*?>",""). // Java HTML Editor does not like the tag.
                replaceAll("(?i)(?s)<!--.*?-->","").   // Java HTML Editor shows comments in not very nice manner.
                replaceAll("(?i)(?s)</?o[^>]*>","");   // Java HTML Editor does not like Microsoft Word's <o> tag.

             if (Tools.safeEquals(pMindMapController.getFrame().getProperty("cut_out_pictures_when_pasting_html"),"true")) {
                textFromClipboard = textFromClipboard.replaceAll("(?i)(?s)<img[^>]*>",""); } // Cut out images.

             textFromClipboard = HtmlTools.unescapeHTMLUnicodeEntity(textFromClipboard);
             
             MindMapNodeModel node = new MindMapNodeModel(textFromClipboard, pMindMapController.getFrame(), pMindMapController.getMap());
             // if only one <a>...</a> element found, set link
             Matcher m = HREF_PATTERN.matcher(textFromClipboard);
             if(m.matches()){
            	 final String body = m.group(2);
            	 if (! body.matches(".*<\\s*a.*")){
                	 final String href = m.group(1);
                	 node.setLink(href);
            	 }
             }
             
             insertNodeInto(node, target);
             //nodeStructureChanged(target);
             pMindMapController.getFrame().setWaitingCursor(false); }

        public DataFlavor getDataFlavor() {
            return MindMapNodesSelection.htmlFlavor; }
        public long getNumberOfObjects(Object transferData, Transferable transfer) {
           return transferData != null ? 1: 0; }}

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
                String text = HtmlTools.toXMLUnescapedText(textWithHtml.replaceAll(
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
                                    .showInputDialog(pMindMapController
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
                        linkParentNode = pMindMapController.newNode("Links", target.getMap());
                        linkParentNode.setLeft(isLeft);
                        // Here we cannot set bold, because linkParentNode.font is null
                        insertNodeInto(linkParentNode, target);
                        ((NodeAdapter) linkParentNode).setBold(true);
                    }
                    MindMapNode linkNode = pMindMapController.newNode(text, target.getMap());
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
        if(newNodes == null){
            newNodes = new LinkedList();
        }
	    newNodes.clear();   
	   	DataFlavorHandler[] dataFlavorHandlerList = getFlavorHandlers();
	   	for (int i = 0; i < dataFlavorHandlerList.length; i++) {
            DataFlavorHandler handler = dataFlavorHandlerList[i];
            DataFlavor 		  flavor  = handler.getDataFlavor();
            if(t.isDataFlavorSupported(flavor)) {
                handler.paste(t.getTransferData(flavor), target, asSibling, isLeft, t);
                break;
            }
        }
        for (ListIterator e = newNodes.listIterator(); e.hasNext(); ) {
            final MindMapNodeModel child = (MindMapNodeModel)e.next();
            pMindMapController.getAttributeController().performRegistrySubtreeAttributes(child);
        }        
//  	   pMindMapController.nodeStructureChanged((MindMapNode) (asSibling ? target.getParent() : target));
        }
	   catch (Exception e) { freemind.main.Resources.getInstance().logException(e); }
	   pMindMapController.getFrame().setWaitingCursor(false);
	}

    /**
     */
    private DataFlavorHandler[] getFlavorHandlers() {
        DataFlavorHandler[] dataFlavorHandlerList = new DataFlavorHandler[] {
                    new FileListFlavorHandler(),
                    new MindMapNodesFlavorHandler(),
                    new DirectHtmlFlavorHandler(), // %%% Make dependent on an option?
                    //new HtmlFlavorHandler(),
                    new StringFlavorHandler() };
        return dataFlavorHandlerList;
    }
 	public MindMapNodeModel pasteXMLWithoutRedisplay(String pasted, MindMapNode target, boolean asSibling, boolean changeSide, boolean isLeft)
	   throws XMLParseException {
	   // Call nodeStructureChanged(target) after this function.
	   try {
		   MindMapNodeModel node = (MindMapNodeModel) pMindMapController.createNodeTreeFromXml(new StringReader(pasted));
		   MindMapNode parent;
		   if (asSibling) {
			   parent = target.getParentNode();
		   }
		   else {
			   parent = target;
		   }
           if(changeSide) {
        	   node.setParent(parent);
               node.setLeft(isLeft);
           }
		  // now, the import is finished. We can inform others about the new nodes:
		  if (asSibling) {
			 insertNodeInto(node, parent, parent.getChildPosition(target)); }
		  else {
			 insertNodeInto(node, target); }
		  pMindMapController.invokeHooksRecursively(node, pMindMapController.getModel());
		  return node; }
	   catch (IOException ee) { freemind.main.Resources.getInstance().logException(ee); return null; }}

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
		  pMindMapController.getFrame().setWaitingCursor(true); }

	   MindMapNode realParent = null;
	   if (asSibling) {
		  // When pasting as sibling, we use virtual node as parent. When the pasting to
		  // virtual node is completed, we insert the children of that virtual node to
		  // the parrent of real parent.
		  realParent = parent;
		  parent = new MindMapNodeModel(pMindMapController.getFrame(), pMindMapController.getMap()); }

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

		  MindMapNode node = pMindMapController.newNode(visibleText, parent.getMap());
          node.setLeft(parent.isNewChildLeft());
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
				insertNodeInto(node, target);
				parentNodes.add(node);
				parentNodesDepths.add(new Integer(depth));
				break; }}}

	   return pastedNode;
	}
     /**
     */
    private void insertNodeInto(MindMapNode node, MindMapNode realParent, boolean asSibling) {
    	pMindMapController.insertNodeInto(node, realParent, asSibling);
    }

	/**
	 */
	private void insertNodeInto(MindMapNodeModel node, MindMapNode parent, int i) {
		pMindMapController.insertNodeInto(node, parent,i);
	}
	private void insertNodeInto(MindMapNode node, MindMapNode parent) {
		pMindMapController.insertNodeInto(node, parent);
	}




}
