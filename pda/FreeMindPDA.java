import waba.ui.*;
import waba.fx.*;
import waba.io.*;
import waba.sys.*;
import superwaba.ext.xplat.xml.*;
import superwaba.ext.xplat.io.*;

/**
 * PDA Version of FreeMind, based heavily on various SuperWaba example code and
 * released under the corresponding LGPL.
 * 
 * @author vik@diamondage.co.nz
 *
 * TODO:
 * 
 * With able assistance from tester kwoodham:
 * 
 * A POSITION ID in for the nodes attached to the root.
 *		POSITION="right|left".
 *
 *Looks like the shorthand method for closing off the node element:

<node TEXT="here's an element"/>

...creates problems when you go back in and assign an icon:

<node TEXT="here's an element"/>
<icon BUILTIN="messagebox_warning"/>

Since the "/" in the node line closes out the node object, the icon won't get
attached to it on when the map is rendered in FreeMind on the PC side.  Looks
to me like separate <node> </node> tags need to be generated when the node is
created - that way, if an icon is attached later, its tag will be correctly
inserted between the tags:

<node TEXT="here's an element">
<icon BUILTIN="messagebox_warning"/>
</node>

Would be nice to have a "Sibling" button alongside the child to generate a
new node at the same level.  The "Insert" puts the new node right under the
parent.  Best way I've found to generate a sibling node is to hit "Child" then
use the arrow to move the node left when I've entered it.

The "extended" entry needs to close when the node is closed out and a new
node entered.  Right now, when I edit an extended entry, close, then open a
different node, the extra lines - and the extended text from the previous node
- are still displayed.
 */
public class FreeMindPDA extends MainWindow {
	protected static String dbName=new String("FMindD.FmPD.DATA");

	private Tree tree;
	private TreeModel model;
	private TreeFile tf; /* The file operations module for a tree. */
	private boolean showLeaf = true;
	private Button btnEdit;
	private Button btnDelete;
	private Button btnInsertChild;
	private Button btnInsertSibling;
	private Button btnArrowUp;
	private Button btnArrowDown;
	private Button btnArrowLeft;
	private Button btnArrowRight;
	private Container main;
	private EditFmNode editContainer;
	private MenuBar menu;
	private Rect mainRect;
	private ScaledIcons icons;
	private FmParser fmParser;
	protected String lastFilename = null;
	protected int dbRecord=-1;		/* >=0 if using a catalog entry. */

	/** A simplified version of FreeMind for PDAs
	 */
	public FreeMindPDA() {
		setDoubleBuffer(true);
		setBorderStyle(TAB_ONLY_BORDER);
		setTitle("FreeMindPDA V0.02");
		Settings.setPalmOSStyle(true);
	}

	//##############################################################################
	public void onStart() {
		/* Create a container to operate from. */
		main = new Container();
		main.setRect(getClientRect());
		// make this the main container for swapping
		swap(main);

		String col0[] =
			{
				"File",
				"Save",
				"Save as File",
				"Save PDB",
				"Load File",
				"Load PDB",
				"New",
				"Exit" };
		menu = new MenuBar(new String[][] { col0 });
		setMenuBar(menu);

		btnEdit = new Button("Edit");
		btnDelete = new Button("Delete");
		btnInsertChild = new Button("Child");
		btnInsertSibling = new Button("Insert");
		main.add(btnEdit, LEFT + 1, BOTTOM);
		main.add(btnDelete, AFTER + 2, SAME);
		main.add(btnInsertChild, AFTER + 2, SAME);
		main.add(btnInsertSibling, AFTER + 2, SAME);

		/* Now some arrows for moving the current entry. */
		btnArrowUp = Button.createArrowButton(Graphics.ARROW_UP,fmH*3/11,Color.BLACK); // guich@240_18
		if (Settings.uiStyle == Settings.PalmOS) btnArrowUp.setBorder(Button.BORDER_SIMPLE);
		main.add(btnArrowUp);
		btnArrowUp.setRect(AFTER+2,SAME,PREFERRED+4,fmH);

		btnArrowDown = Button.createArrowButton(Graphics.ARROW_DOWN,fmH*3/11,Color.BLACK); // guich@240_18
		if (Settings.uiStyle == Settings.PalmOS) btnArrowDown.setBorder(Button.BORDER_SIMPLE);
		main.add(btnArrowDown);
		btnArrowDown.setRect(AFTER+1,SAME,PREFERRED+4,fmH);

		btnArrowLeft = Button.createArrowButton(Graphics.ARROW_LEFT,fmH*3/11,Color.BLACK); // guich@240_18
		if (Settings.uiStyle == Settings.PalmOS) btnArrowLeft.setBorder(Button.BORDER_SIMPLE);
		main.add(btnArrowLeft);
		btnArrowLeft.setRect(AFTER+1,SAME,PREFERRED+6,fmH);

		btnArrowRight = Button.createArrowButton(Graphics.ARROW_RIGHT,fmH*3/11,Color.BLACK); // guich@240_18
		if (Settings.uiStyle == Settings.PalmOS) btnArrowRight.setBorder(Button.BORDER_SIMPLE);
		main.add(btnArrowRight);
		btnArrowRight.setRect(AFTER+1,SAME,PREFERRED+6,fmH);
		/* Figure out where to put the tree */
		mainRect =
			new Rect(
				LEFT,
				TOP,
				main.getClientRect().width,
				btnEdit.getRect().y);

		/* Put up the logo in a new container while the most recent tree builds. */
		SplashScreen spl = new SplashScreen(this);
		spl.setRect(getClientRect());
		/* Swap in the Save module. */
		swap(spl);
	}

	public void onExit() {
		/* If we've got a tree file, save using that. Otherwise, it's too late. */
		if (tf != null) {
			/* Save the tree using the last known filename or DB record. */
			if (dbRecord>=0) {
				waba.sys.Vm.debug("Catalog save not done yet.");
			} else {
				tf.saveTree(tree, lastFilename, getMainWindow());
			}
		}
	}

	/**
	 * Standard event handler.
	 */
	public void onEvent(Event event) {
		if (event.type == ControlEvent.PRESSED) {
			/* Don't do anything if we're not set up yet. */
			if (tree == null)
				return;
			Node node = tree.getSelectedNode();
			/* Don't do anything if we're not set up yet. */
			if (node == null)
				return;
			/* Do not dick with the root node. */
			if (node.parent==null)
				return;

			/* Various editing buttons on the main screen. */
			if (event.target == btnEdit) {
				editContainer=new EditFmNode(icons,model);
				/* Swap in the edit container before populating it, or the
				 * fields won't have been added and retrect'd. */
				swap(editContainer);
				editContainer.populateContainer(tree.getSelectedNode());
			} else if (event.target == btnDelete) {
				if (node != null)
					model.removeNode(node.getParent(), node);
			} else if (event.target == btnInsertChild) {
				/* Create a new node as a child of the current node. */
				if (node != null) {
					swap(editContainer);
					editContainer.populateContainer(null);
				}
			} else if (event.target == btnInsertSibling) {
				/* If we've got a node, find its parent and create a child off that. */
				if (node != null) {
					tree.select(node.getParent());
					swap(editContainer);
					editContainer.populateContainer(null);
				}
			} else if (event.target==btnArrowUp) {
				/* User is trying to move the item up the list. If we're not
				 * already there, try to oblige.  */
				node.moveUp();
				tree.collapse(node.parent);
				tree.expand(node.parent);
				tree.select(node);
			} else if (event.target==btnArrowDown) {
				node.moveDown();
				tree.collapse(node.parent);
				tree.expand(node.parent);
				tree.select(node);
			} else if (event.target==btnArrowRight) {
				/* User is trying to demote the current item. If it is not the only
				 * node in the list, make it a child of the one above. If no above,
				 * go below. If impractical, do diddly. */
				if (node.parent.children.size()>1) {
					Node newParent=node.getPreviousSibling();
					if (newParent==null)
						newParent=node.getNextSibling();
					if (newParent!=null) {
						/* Remove node from existing parent. */
						node.removeFromParent();
						/* Stick it back in and jiggle things to cause a redraw. */
						newParent.add(node);
						tree.collapse(newParent.parent);
						tree.expand(newParent.parent);
						tree.expand(newParent);
						tree.select(node);
					}
				}
			} else if (event.target==btnArrowLeft) {
				/* User is trying to promote the item up the heirachy. If we're not
				 * already at the top, try to oblige.  */
				if (node.getLevel()>2) {
					Node newParent=node.parent;
					node.removeFromParent();
					newParent.parent.add(node);
					tree.collapse(newParent.parent);
					tree.expand(newParent.parent);
					tree.select(node);
				}
			}

		} else if (event.type == ControlEvent.WINDOW_CLOSED) {
			/* Menu handling events */
			if (event.target == menu) {
				switch (menu.getSelectedMenuItem()) {
					case 1 :
						/* Save the tree using the last known filename or db record. */
						if (dbRecord>=0) {
							waba.sys.Vm.debug("Save PDB not done yet.");
						} else {
							tf.saveTree(tree, lastFilename, getMainWindow());
						}
						break;

					case 2 :
						/* Save the tree as a file */
						SaveMmFile smf = new SaveMmFile(this);
						smf.setRect(getClientRect());
						/* Swap in the Save module. */
						swap(smf);
						break;

					case 3 :
						/* Save the tree as an entry in the catalog.  */
						SaveMmPdb smp = new SaveMmPdb(this);
						smp.setRect(getClientRect());
						/* Swap in the Save module. */
						swap(smp);
						break;

					case 4 :
						/* Load a file. */
						waba.sys.Vm.debug("Auto save before load not done yet.");
						LoadMmFile lmf = new LoadMmFile(this);
						lmf.setRect(getClientRect());
						/* Swap in the Load module. */
						swap(lmf);
						break;

					case 5 :
						/* Load a PDB. */
						waba.sys.Vm.debug("Auto save before load not done yet.");
						LoadMmPdb lmp = new LoadMmPdb(this);
						lmp.setRect(getClientRect());
						/* Swap in the Load module. */
						swap(lmp);
						break;

					case 6 :
						/* New map */
						newMap();
						break;

					case 7 :
						/* Killstopdie */
						exit(0);
						break;

					default :
						break;
				}
			}
		}
	}

	/**
	 * Load in a file with the given name.
	 * @param fileName file to find and load off filesystem or SD card etc.
	 */
	public void loadFile(String fileName) {
		Node root = new Node("Root");
		fmParser = new FmParser(root, icons);
		tf = new TreeFile(fileName, fmParser);

		if (!tf.isOpen()) {
			Vm.debug("Error opening source file.");
			return;
		}

		MessageBox mb =
			new MessageBox("Loading", "Loading file:|" + fileName, null);
		popupModal(mb);

		/* Save a copy of the filename as we'll use it to save with. */
		lastFilename = new String(fileName);
		dbRecord=-1;

		/* Get rid of the old, in with the new. No useful comments as
		 * I cribbed this from the example.	 */
		if (tree != null) {
			tree.clear();
			model.clear();
		}

		tf.loadRoot(root, icons);
		root.display(root, "     ");
		model = new TreeModel(root, true);
		tree = new Tree(model, true);
		tree.setPolicy("Tree.Horizontal_ScrollBar", "ScrollBar_AS_NEEDED");
		main.add(tree, LEFT, TOP);
		tree.setRect(mainRect);
		mb.unpop();

		/* Create a new edit control to edit this tree with. */
		editContainer =new EditFmNode(icons, model);
		return;
	}

	/** Save tree using given filename.
	 * 
	 * @param fname
	 */
	public void saveFile(String fname) {
		tf.saveTree(tree, fname, getMainWindow());
	}

	/**
	 * Load in a Catalog entry as a map.
	 * @param dBase	Catalog to use
	 * @param rec			record to read in.
	 * @param name		Name of map for user confidence.
	 */
	public void loadPdb(Catalog dBase, int rec, String name) {
		Node root = new Node("Root");
		fmParser = new FmParser(root, icons);

		if (dBase.setRecordPos(rec)==false)
			waba.sys.Vm.debug("Failed to set record "+rec);

		int size=dBase.getRecordSize();
//		dBase.setRecordPos(-1);	/* Unlock the record. */

		byte[] b=new byte[size];
		int readed=dBase.readBytes(b,0,size);
		if (b.length!=size) {
			waba.sys.Vm.debug("Read of "+size+" bytes only got "+readed);
		}
		BufferStream bs=new BufferStream(b);
		tf = new TreeFile(bs, fmParser);

		if (!tf.isOpen()) {
			Vm.debug("Error opening source file.");
			return;
		}

		MessageBox mb =
			new MessageBox("Loading", "Loading map:|" + name, null);
		popupModal(mb);

		/* Save a copy of the record name and record number we saved with. */
		lastFilename = new String(name);
		dbRecord=rec;

		/* Get rid of the old, in with the new. No useful comments as
		 * I cribbed this from the example.	 */
		if (tree != null) {
			tree.clear();
			model.clear();
		}

		tf.loadRoot(root, icons);
		root.display(root, "     ");
		model = new TreeModel(root, true);
		tree = new Tree(model, true);
		tree.setPolicy("Tree.Horizontal_ScrollBar", "ScrollBar_AS_NEEDED");
		main.add(tree, LEFT, TOP);
		tree.setRect(mainRect);
		mb.unpop();

		/* Create a new edit control to edit this tree with. */
		editContainer =new EditFmNode(icons, model);

		/* Unlock the record. */
		dBase.setRecordPos(-1);	

		return;
	}


	/** Invokes the tree's method to write the appropriate record.
	 * 
	 * @param dBase	Catalog to use (opened please)
	 * @param rec			Record to create/overwrite.
	 * @param mapName	Name to put up for user confidence.
	 */
	public void savePdb(Catalog dBase,int rec,String mapName) {
		tf.saveTree(tree, dBase,rec,mapName, getMainWindow());
	}

	public void newMap() {
		Node root = new Node("Root");

		/* Save a copy of the filename as we'll use it to save with. */
		lastFilename = "unnamed.mm";
		dbRecord=-1;

		/* Get rid of the old, in with the new. No useful comments as
		 * I cribbed this from the example.	 */
		if (tree != null) {
			tree.clear();
			model.clear();
		}

		/* Set up an extensible root node and associated tree */
		root.display(root, "     ");
		root.setAllowsChildren(true);
		model = new TreeModel(root, true);
		tree = new Tree(model, true);
		tree.setPolicy("Tree.Horizontal_ScrollBar", "ScrollBar_AS_NEEDED");
		main.add(tree, LEFT, TOP);
		tree.setRect(mainRect);
		fmParser = new FmParser(root, icons);

		/* Create a map node and version. */
		AttributeList newAtts = new AttributeList();
		newAtts.addAttribute("\"version\"", "0.7.1", (byte) 0);
		Node newNode =
			new Node(new FreeMindNode("[Map]", newAtts, FmParser.mapTag));
		/* Map nodes may have children. */
		newNode.setAllowsChildren(true);
		model.insertNode(root, newNode, 0);
		/* Collapse & expand tree to avoid refresh problems. */
		tree.collapse(root);
		tree.expand(root);

		/* Create a new edit control to edit this tree with. */
		editContainer =new EditFmNode(icons, model);
		return;
	}

	/** This is so the splash screen can build scled icons while displaying the splash image. */
	public void buildScaledIcons() {
		/* Use text height for icons, but allow one pixel separation. */
		icons = new ScaledIcons(this.fm.ascent);
	}

	/** Used to find an SD card */
	private File getCardVolume() {
		if (waba.sys.Settings.platform.equals("WindowsCE")
			|| waba.sys.Settings.platform.equals("PocketPC")) {
			File f;
			String[] winceVols =
				{
					"\\Storage Card\\",
					"\\SD Card\\",
					"\\Storage Card1\\",
					"\\Storage Card2\\" };
			for (int i = winceVols.length - 1; i >= 0; i--)
				if ((f = new File(winceVols[i])).isDir()) {
					return f;
				}
		}
		return null;
	}

	/** Returns the directory to store thigns in - or has a bloody good go.
	 * 
	 * @return	File of storage dir, or a null if we can't cope. */
	public File getStorageDir() {
		File file;
		if (Settings.platform.startsWith("Palm")) {
			file=new File("/");
		} else if (Settings.platform.startsWith("Java")) {
			file=new File("./");
		} else
			file=getCardVolume();

		/* Fallback for emergencies */
		if (file == null)
			file=new File("./");
		waba.sys.Vm.debug("Using storage dir "+file.getPath());
		return file;
	}

}
