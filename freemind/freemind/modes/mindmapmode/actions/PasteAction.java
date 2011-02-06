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
/* $Id: PasteAction.java,v 1.1.2.2.2.23 2009/01/16 23:10:45 dpolivaev Exp $ */

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import freemind.controller.MindMapNodesSelection;
import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.CutNodeAction;
import freemind.controller.actions.generated.instance.PasteNodeAction;
import freemind.controller.actions.generated.instance.TransferableContent;
import freemind.controller.actions.generated.instance.TransferableFile;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.HtmlTools;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.main.XMLParseException;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapNodeModel;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;

public class PasteAction extends AbstractAction implements ActorXml{
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
	        this.pMindMapController.paste(
	            this.pMindMapController.getClipboardContents(),
	            this.pMindMapController.getView().getSelected().getModel());
	}

    /* (non-Javadoc)
      * @see freemind.controller.actions.ActorXml#act(freemind.controller.actions.generated.instance.XmlAction)
      */
    public void act(XmlAction action) {
    	PasteNodeAction pasteAction = (PasteNodeAction) action;
        _paste(
            getTransferable(pasteAction.getTransferableContent()),
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
		pasteAction.setTransferableContent(getTransferableContent(t));
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


	/**
	 * @param t the content
	 * @param target where to add the content
	 * @param asSibling if true, the content is added beside the target, otherwise as new children
	 * @param isLeft if something is pasted as a sibling to root, it must be decided on which side of root
	 * @return true, if successfully executed.
	 */
	public boolean paste(Transferable t, MindMapNode target, boolean asSibling, boolean isLeft) {
		pasteAction = getPasteNodeAction(t,new NodeCoordinate(target,asSibling, isLeft));
		undoAction = new CompoundAction();
		// Undo-action
		pMindMapController.getActionFactory().startTransaction(text);
		boolean result = pMindMapController.getActionFactory().executeAction(new ActionPair(pasteAction, undoAction));
		pMindMapController.getActionFactory().endTransaction(text);
		return result;
	}

	private void addMindMapNodesFlavor() {
		if(pasteAction == null){
			return;
		}
		final TransferableContent transferableContent = pasteAction.getTransferableContent();		
		if(transferableContent.getTransferable() == null){
			final List nodes = new LinkedList();
			final ListIterator listIterator = undoAction.getListChoiceList().listIterator(undoAction.sizeChoiceList());
			while(listIterator.hasPrevious()){
				CutNodeAction cutAction = (CutNodeAction)listIterator.previous();
				NodeAdapter node = pMindMapController.getNodeFromID(cutAction.getNode());
				nodes.add(node);
			}
			try {
				String transferable = pMindMapController.createForNodesFlavor(nodes, true);
				transferableContent.setTransferable(transferable);
				transferableContent.setTransferableAsDrop(null);
				transferableContent.setTransferableAsHtml(null);
				transferableContent.setTransferableAsPlainText(null);
				transferableContent.setTransferableAsRTF(null);
			} catch (UnsupportedFlavorException e) {
				freemind.main.Resources.getInstance().logException(e);
			} catch (IOException e) {
				freemind.main.Resources.getInstance().logException(e);
			}
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
                insertNodeInto((MindMapNodeModel) node, target, asSibling, isLeft, false);
                addUndoAction(node);
            }
        }


        public DataFlavor getDataFlavor() {
            return MindMapNodesSelection.fileListFlavor;
        }

	}

	private class MindMapNodesFlavorHandler implements DataFlavorHandler {

        public void paste(Object TransferData, MindMapNode target, boolean asSibling, boolean isLeft, Transferable t) {
			  //System.err.println("mindMapNodesFlavor");
        	HashMap IDToTarget = new HashMap();
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
                           textLines[i], target, asSibling, true, isLeft, IDToTarget);
                   ListIterator childrenUnfolded = newModel.childrenUnfolded();
                   while(childrenUnfolded.hasNext()){
                	   pMindMapController.fireRecursiveNodeCreateEvent((MindMapNode) childrenUnfolded.next());
                   }
                   newModel.setLeft(isLeft);
                   addUndoAction(newModel);
               }
           }
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
  			// workaround for java decoding bug 
  			// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6740877
              if(textFromClipboard.charAt(0) == 65533)
              {
            	  throw new UnsupportedFlavorException(MindMapNodesSelection.htmlFlavor);
              }
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
             
             MindMapNode node = pMindMapController.newNode(textFromClipboard,  pMindMapController.getMap());
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
             addUndoAction(node);
             pMindMapController.getFrame().setWaitingCursor(false); 
             }

        public DataFlavor getDataFlavor() {
            return MindMapNodesSelection.htmlFlavor; }
        }
        
	private class HtmlFlavorHandler implements DataFlavorHandler {

        public void paste(Object TransferData, MindMapNode target,
                boolean asSibling, boolean isLeft, Transferable t)
                throws UnsupportedFlavorException, IOException {
            //System.err.println("htmlFlavor");
            String textFromClipboard = (String) TransferData;
            // ^ This outputs transfer data to standard output. I don't know
            // why.
            MindMapNode pastedNode = pasteStringWithoutRedisplay(t, target,  asSibling, isLeft);

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
                                    .showInputDialog(
                                    		pMindMapController.getView().getSelected(), 
                                    		pMindMapController.getText("enter_base_url"));
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
                        linkParentNode.setLeft(target.isNewChildLeft());
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

        public DataFlavor getDataFlavor() {
            return MindMapNodesSelection.htmlFlavor;
        }

	}

	private class StringFlavorHandler implements DataFlavorHandler {

        public void paste(Object TransferData, MindMapNode target,
                boolean asSibling, boolean isLeft, Transferable t)
                throws UnsupportedFlavorException, IOException {
            //System.err.println("stringFlavor");
            pasteStringWithoutRedisplay(t, target, asSibling, isLeft);
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
					try {
						handler.paste(t.getTransferData(flavor), target, asSibling, isLeft, t);
						break;
					} catch (UnsupportedFlavorException e) {
					}
				}
			}
			for (ListIterator e = newNodes.listIterator(); e.hasNext(); ) {
				final MindMapNodeModel child = (MindMapNodeModel)e.next();
				pMindMapController.getAttributeController().performRegistrySubtreeAttributes(child);
			}        
			//	   	pMindMapController.nodeStructureChanged((MindMapNode) (asSibling ? target.getParent() : target));

			// add information about the new nodes ID:
			addMindMapNodesFlavor();
		} catch (IOException e) {
			Resources.getInstance().logException(e);
		}
		finally{
			undoAction = null;
			pasteAction = null;
			pMindMapController.getFrame().setWaitingCursor(false);
		}
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

	public MindMapNodeModel pasteXMLWithoutRedisplay(String pasted,
			MindMapNode target, boolean asSibling, boolean changeSide,
			boolean isLeft, HashMap pIDToTarget) throws XMLParseException {
		// Call nodeStructureChanged(target) after this function.
		logger.fine("Pasting " + pasted + " to " + target);
		try {
			MindMapNodeModel node = (MindMapNodeModel) pMindMapController
					.createNodeTreeFromXml(new StringReader(pasted), pIDToTarget);
			insertNodeInto(node, target, asSibling, isLeft, changeSide);
			pMindMapController.invokeHooksRecursively(node, pMindMapController
					.getModel());
			return node;
		}
		catch (IOException ee) {
			freemind.main.Resources.getInstance().logException(ee);
			return null;
		}
	}

	private void insertNodeInto(MindMapNodeModel node, MindMapNode target,
			boolean asSibling, boolean isLeft, boolean changeSide) {
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
 	}

    static final Pattern nonLinkCharacter = Pattern.compile("[ \n()'\",;]");
	private CompoundAction undoAction;
	private PasteNodeAction pasteAction;

	/**
	 * Paste String (as opposed to other flavours)
	 *
	 * Split the text into lines; determine the new tree structure
	 * by the number of leading spaces in lines.  In case that
	 * trimmed line starts with protocol (http:, https:, ftp:),
	 * create a link with the same content.
	 *
	 * If there was only one line to be pasted, return the pasted node, null otherwise.
	 * @param isLeft TODO
	 */

	private MindMapNode pasteStringWithoutRedisplay(Transferable t, MindMapNode parent,
													boolean asSibling, boolean isLeft) 
	throws UnsupportedFlavorException, IOException {

       String textFromClipboard = (String) t.getTransferData(DataFlavor.stringFlavor);
	   Pattern mailPattern = Pattern.compile("([^@ <>\\*']+@[^@ <>\\*']+)");

	   String[] textLines = textFromClipboard.split("\n");

	   if (textLines.length > 1) {
		  pMindMapController.getFrame().setWaitingCursor(true); }

	   MindMapNode realParent = null;
	   if (asSibling) {
		  // When pasting as sibling, we use virtual node as parent. When the pasting to
		  // virtual node is completed, we insert the children of that virtual node to
		  // the parent of real parent.
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
					MindMapNode n = (MindMapNode)parentNodes.get(k);
					if(n.getParentNode() == parent){
						addUndoAction(n);
					}
				   parentNodes.remove(k);
				   parentNodesDepths.remove(k); }
				MindMapNode target = (MindMapNode)parentNodes.get(j);
		        node.setLeft(isLeft);
				insertNodeInto(node, target);
				parentNodes.add(node);
				parentNodesDepths.add(new Integer(depth));
				break; }}}

		for (int k = 0; k < parentNodes.size(); ++k) {
			MindMapNode n = (MindMapNode)parentNodes.get(k);
			if(n.getParentNode() == parent){
				addUndoAction(n);
			}
		}
	   return pastedNode;
	}
	/**
	 */
	private void insertNodeInto(MindMapNodeModel node, MindMapNode parent, int i) {
		pMindMapController.insertNodeInto(node, parent,i);
	}
	private void insertNodeInto(MindMapNode node, MindMapNode parent) {
		pMindMapController.insertNodeInto(node, parent);
	}

	private TransferableContent getTransferableContent(Transferable t)  {

		try {
			TransferableContent trans =
					new TransferableContent();
			if (t.isDataFlavorSupported(MindMapNodesSelection.mindMapNodesFlavor)) {
				String textFromClipboard;
				textFromClipboard =
					(String) t.getTransferData(MindMapNodesSelection.mindMapNodesFlavor);
				trans.setTransferable(textFromClipboard);
			}
			if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				String textFromClipboard;
				textFromClipboard =
					(String) t.getTransferData(DataFlavor.stringFlavor);
				trans.setTransferableAsPlainText(textFromClipboard);
			}
			if (t.isDataFlavorSupported(MindMapNodesSelection.rtfFlavor)) {
//				byte[] textFromClipboard = (byte[]) t.getTransferData(MindMapNodesSelection.rtfFlavor);
//				trans.setTransferableAsRTF(textFromClipboard.toString());
			}
			if(t.isDataFlavorSupported(MindMapNodesSelection.htmlFlavor)) {
				String textFromClipboard;
				textFromClipboard =
					(String) t.getTransferData(MindMapNodesSelection.htmlFlavor);
				trans.setTransferableAsHtml(textFromClipboard);
			}
			if(t.isDataFlavorSupported(MindMapNodesSelection.fileListFlavor)) {
				/* Since the JAXB-generated interface TransferableContent doesn't supply
				  a setTranserableAsFileList method, we have to get the fileList, clear it,
				  and then set it to the new value.
				*/
	            List fileList = (List)t.getTransferData(MindMapNodesSelection.fileListFlavor);
                for (Iterator iter = fileList.iterator(); iter.hasNext();) {
                    File fileName = (File) iter.next();
                    TransferableFile transferableFile = new TransferableFile();
                    transferableFile.setFileName(fileName.getAbsolutePath());
                    trans.addTransferableFile(transferableFile);
                }
			}
			return trans;
		} catch (UnsupportedFlavorException e) {
freemind.main.Resources.getInstance().logException(			e);
		} catch (IOException e) {
freemind.main.Resources.getInstance().logException(			e);
		}
		return null;
	}

    private Transferable getTransferable(TransferableContent trans) {
        // create Transferable:
        //Add file list to this selection.
        Vector fileList = new Vector();
        for (Iterator iter = trans.getListTransferableFileList().iterator(); iter.hasNext();)
        {
            TransferableFile tFile = (TransferableFile) iter.next();
            fileList.add(new File(tFile.getFileName()));
        }
        Transferable copy =
            new MindMapNodesSelection(
                trans.getTransferable(),
        		trans.getTransferableAsPlainText(),
                trans.getTransferableAsRTF(),
                trans.getTransferableAsHtml(),
                trans.getTransferableAsDrop(),
                fileList);
        return copy;
    }

    private void addUndoAction(MindMapNode node) {
		if(undoAction != null){
			CutNodeAction cutNodeAction = pMindMapController.cut.getCutNodeAction( node);
			undoAction.addAtChoice(0, cutNodeAction);
		}
	}
    
}
