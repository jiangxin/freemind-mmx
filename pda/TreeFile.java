import waba.io.*;
import waba.sys.*;
import waba.ui.*;
import superwaba.ext.xplat.xml.*;
import superwaba.ext.xplat.io.BufferStream;

public class TreeFile {
    
	private Stream file;
	private FmParser fmp;
	
    public TreeFile(String fileName,FmParser fmp) {
		file = new File(fileName, File.READ_ONLY);
		this.fmp=fmp;
    }

	public TreeFile(BufferStream buf,FmParser fmp) {
		file = buf;
		this.fmp=fmp;
	}

	public boolean isOpen() {
		return file.isOpen();
	}

	public void loadRoot(Node root,ScaledIcons icons) {
		/* Hack in a bunch of nodes based on the content of this XML data. */
		XmlReader rdr = new XmlReader();
		rdr.setContentHandler(fmp);
		try {
			//			rdr.parse(new XmlReadableString(data));
			rdr.parse(file);
		} catch (Exception ae) {
			waba.sys.Vm.debug("Parsing exception");
		}
		/* We're done with the file now. */
		file.close();
	}

	public void saveTree(Tree source,String filename,Window main) {
		/* Create a buffered stream. Eventually this may go to a catalog. */
		File target = new File(filename, File.READ_WRITE);
		/* If it exists, delete it. */
		if (target.isOpen())
			target.delete();
		target.close();
		/* Now open a new one for output. */
		target = new File(filename, File.CREATE);

		if (target.isOpen()) {
			/* Put up a popup to tell user what gives. */
			MessageBox mb = new MessageBox("Saving",
				"Saving file:|"+filename,null);
			main.popupModal(mb);
			/* Turn it into a buffered stream. */
			BufferStream outBuffer = new BufferStream(target);
			/* We only really want the [map] node. Look through the items off
			 * the root node, and hunt down the firs map item. */
			Node root=source.model.getRoot();
			for (int i = 0; i < root.children.size(); i++) {
				Node n = (Node)(root.children.items[i]);
				/* Find the map node and write the bugger. */
				if (n.getUserObject() instanceof FreeMindNode) {
					if (((FreeMindNode)(n.getUserObject())).nodeType==FmParser.mapTag) {
						saveNode(outBuffer, n);
						break;
					}
				}
			}
			outBuffer.flush();
			target.close();
			mb.unpop();
		} else {
			/* Tarnation. */
			Vm.debug("Failed to open target file.");
		}
	}

	/** Save the source tree in the specified catalog entry. If it doesn't exist, add
	 * one on the end.
	 * @param source		Tree to read data from.
	 * @param dBase		Database to write record to.
	 * @param rec
	 * @param mapName	Name for use confidence popup. 
	 * @param main
	 */
	public void saveTree(Tree source,Catalog dBase, int rec, String mapName,Window main) {
		/* Put up a popup to tell user what gives. */
		MessageBox mb = new MessageBox("Saving",
			"Saving map:|"+mapName,null);
		main.popupModal(mb);
		/* Turn it into a buffered stream. */
		BufferStream outBuffer = new BufferStream();

		/* write filename before saving data */
		DataStream ds = new DataStream(outBuffer);
		ds.writeString(mapName);

		/* We only really want the [map] node. Look through the items off
		 * the root node, and hunt down the firs map item. */
		Node root=source.model.getRoot();
		for (int i = 0; i < root.children.size(); i++) {
			Node n = (Node)(root.children.items[i]);
			/* Find the map node and write the bugger. */
			if (n.getUserObject() instanceof FreeMindNode) {
				if (((FreeMindNode)(n.getUserObject())).nodeType==FmParser.mapTag) {
					saveNode(outBuffer, n);
					break;
				}
			}
		}
		/* Transfer the output buffer into a record. Hope it bloody
		 * well fits, 'cos they're limited to 64K. */
		byte[] buf=outBuffer.getBuffer();
		/* If the record exists, resize it. Otherwise create a new one. */
		if (rec>=dBase.getRecordCount()) {
			if (dBase.addRecord(buf.length)<0)
				waba.sys.Vm.debug("Failed to add record");
		} else {
			if (dBase.setRecordPos(rec)==false)
				waba.sys.Vm.debug("Failed to set record");
			if (dBase.resizeRecord(buf.length)==false)
				waba.sys.Vm.debug("Failed to resize record");
		}
		/* Write it and unlock it. */
		if (dBase.writeBytes(buf,0,buf.length)!=buf.length)
			waba.sys.Vm.debug("Failed to write record");
		dBase.setRecordPos(-1);
		mb.unpop();
	}


	private void saveNode(BufferStream outBuffer, Node node) {
		FreeMindNode fmn = (FreeMindNode) node.getUserObject();

		int type = fmn.nodeType;
		String nodeTagName = null;
		/* Let's figure out what we are. */
		if (type == FmParser.cloudTag) {
			nodeTagName = "cloud";
		} else if (type == FmParser.fontTag) {
			nodeTagName = "font";
		} else if (type == FmParser.mapTag) {
			nodeTagName = "map";
		} else if (type == FmParser.nodeTag) {
			nodeTagName = "node";
		}

		/* See how much of a tag we get. */
		Node n = node.getFirstChild();

		writeToFile(outBuffer, "<" + nodeTagName);

		/* List any attributes */
		AttributeList aList = fmn.atts;
		if (aList != null) {
			AttributeList.Iterator a = aList.new Iterator();
			while (a.next()) {
				writeToFile(outBuffer, " " + a.getAttributeAsString());
			}
		}
		
		/* If there are no children or icons, close this node now. */
		if ((n == null)&&(node.userIcons.size()==0)) {
			writeToFile(outBuffer, "/>\n");
		} else {
			/* There are children. List them. */
			writeToFile(outBuffer, ">\n");
			while (n != null) {
				saveNode(outBuffer, n);
				n = n.getNextSibling();
			}
			/* If there are icon entries, list them too. */
			if (node.userIcons.size()>0) {
				for (int i=0; i<node.userIcons.size();i++) {
					/* Time to list the icon attributes. */
					writeToFile(outBuffer,"<icon");
					FmIcon fmi=((FmIcon)(node.userIcons.items[i]));
					aList = fmi.atts;
					if (aList != null) {
						AttributeList.Iterator a = aList.new Iterator();
						while (a.next()) {
							writeToFile(outBuffer, " " + a.getAttributeAsString());
						}
					}
					writeToFile(outBuffer, "/>\n");
				}
			}
			/* Close off this node. */
			writeToFile(outBuffer, "</" + nodeTagName + ">\n");
		}
	}

	private void writeToFile(BufferStream outBuffer, String s) {
		outBuffer.writeBytes(s.getBytes(), 0, s.length());
	}
}
