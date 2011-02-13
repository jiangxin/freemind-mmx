import superwaba.ext.xplat.xml.*;
import waba.fx.*;

public class FmParser implements ContentHandler {

	private Node root;
	private XmlReader rdr;
	private static final String[] knownNodes = { "map", "node", "cloud", "font","icon" };
	private static final int[] nodeTags = { 76092, 2401794, 64218645, 2163791,2241657 };
	protected static int mapTag;
	protected static int nodeTag;
	protected static int cloudTag;
	protected static int fontTag;
	protected static int iconTag;
	private ScaledIcons icons;

	public FmParser(Node r,ScaledIcons icons) {
		root = r;
		this.icons=icons;
		mapTag = nodeTags[0];
		nodeTag = nodeTags[1];
		cloudTag = nodeTags[2];
		fontTag = nodeTags[3];
		iconTag = nodeTags[4];
	}

	public void characters(String s) {
//		waba.sys.Vm.debug("Found chars: " + s);
	}

	public void comment(java.lang.String s) {
		waba.sys.Vm.debug("Found comment: " + s);
	}

	public void endElement(int tag) {
		/* Move back up the tree, eliminating icon nodes as we go. */
		if (((FreeMindNode)(root.getUserObject())).nodeType==iconTag) {
			Node iconNode=root;
			root = root.parent;
			root.remove(iconNode);
		} else {
			root = root.parent;
		}
	}

	/** Receive notification of the beginning of an element. */
	public void startElement(int tag, AttributeList atts) {
		/* Create a new node to contain these attributes. */
		Node newNode = new Node(new FreeMindNode("" + tag));
		root.add(newNode);

		if (tag == mapTag) {
			newNode.setUserObject(new FreeMindNode("[Map]", atts,mapTag));
			/* Map nodes may have children. */
			newNode.setAllowsChildren(true);
		} else if (tag == cloudTag) {
			newNode.setUserObject(new FreeMindNode("[Cloud]", atts,cloudTag));
		} else if (tag == fontTag) {
			newNode.setUserObject(new FreeMindNode("[Font]", atts,fontTag));
		} else if (tag == iconTag) {
			newNode.setUserObject(new FreeMindNode("[Icon]", atts,iconTag));
			/* Figure out which icon we want in there. */
			AttributeList.Iterator a = atts.new Iterator();
			Image newIcon=icons.imgUnknown;	/* Default icon */
			while (a.next()) {
				/* See if we can find any BUILTIN icons. */
				if (a.getAttributeName().equals("BUILTIN")) {
					/* Locate this icon in out own built-in list. */
					Image foundHim=icons.findIcon(a.getAttributeValue());
					if (foundHim!=null) {
						newIcon=foundHim;
					}
				}
			}
			newNode.parent.addIcon(new FmIcon(newIcon,new AttributeList(atts)));
		} else if (tag == nodeTag) {
			/* List the attributes */
			AttributeList.Iterator a = atts.new Iterator();
			while (a.next()) {
				if (a.getAttributeName().equals("TEXT")) {
					/* This is a Text attribute. Name this node and set its attributes. */
					newNode.setUserObject(
						new FreeMindNode(null, atts,nodeTag));
					/* Text nodes may have children. */
					newNode.setAllowsChildren(true);
				} else if (a.getAttributeName().equals("LINK")) {
					newNode.addIcon(new FmIcon(icons.imgLink));
				}
			}
		} else {
			waba.sys.Vm.debug("Unknown node type");
			/* We don't recognise this node. Drop it. */
		}
		root = newNode;
	}
}
