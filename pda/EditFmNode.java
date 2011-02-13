import waba.ui.*;
import waba.sys.*;
import waba.fx.*;
import waba.util.*;
import superwaba.ext.xplat.ui.MultiEdit;
import superwaba.ext.xplat.xml.*;

public class EditFmNode extends Container {

	private Tree tree;
	private TreeModel model;
	private Node node;
	private ScaledIcons icons;
	private FmParser fmp;
	private FreeMindNode fmNode;
	private Button btnQuit;
	private Button btnExtended;
	private Button btnOk;
	private Edit standardEdit;
	private MultiEdit editDescription;
	private ListBox listAtts; /* A drop-down list of attributes. */
	private ComboBox cbxAttrs;
	private int bigEditBoxHeight = Settings.screenHeight / 3;
	private boolean extendedEdit = false;
	private boolean newNode = false;
	private Vector buttonList = new Vector(); /* Buttons for the icons. */
	private boolean[] iconSelected;
	/* Tells us if the icon button is active or not. */

	/**
	 * Edit a node of a tree. If no node is specified, add a new one as a child
	 * of the currently selected node in the tree.
	 * @param fmp		Parser, needed for icon images and types.
	 * @param model	Model containing this tree's node.
	 * @param node		node to edit or null for new node.
	 */
	public EditFmNode(ScaledIcons icons, TreeModel model) {
		super();
		tree = model.getTree();
		this.model = model;
		this.icons = icons;
	}
	
	public void onStart() {
		btnQuit = new Button("Quit");
		add(btnQuit, LEFT, BOTTOM);
		btnExtended = new Button("Extended");
		add(btnExtended, AFTER + 2, BOTTOM);
		btnOk = new Button("  OK  ");
		add(btnOk, AFTER + 2, BOTTOM);

		/* Reserve space for a combobox */
		cbxAttrs = new ComboBox(new ListBox(new String[] { "A", "B", "C" }));
		add(cbxAttrs);
		cbxAttrs.setRect(LEFT, TOP, FILL, PREFERRED);

		/* Create a standard text box. */
		add(standardEdit = new Edit(), CENTER, AFTER + 1);
		standardEdit.setRect(LEFT, SAME, FILL, PREFERRED);

		/* Put up a selection of icons below where the large edit field goes. */
		int icony = standardEdit.getRect().y + bigEditBoxHeight;
		int iconx = 0;
		Button b;
		iconSelected = new boolean[icons.allIcons.size()];
		for (int i = 0; i < icons.allIcons.size(); i++) {
			Image bImg = (Image) (icons.allIcons.items[i]);
			b = new Button(bImg);
			add(b);
			b.setRect(iconx, icony, icons.iconSize + 8, icons.iconSize + 8);
			/* Mark icons we've got as selected. Assume they're not. */
			b.setBorder(BORDER_NONE);
			b.setGap(0);
			iconSelected[i] = false;
			/* Allow plenty for border and spacing. */
			iconx += icons.iconSize + 8;
			buttonList.add(b);
		}
	}

	/** Populate the container edit fields etc. with the contents of this node.
	 * 
	 * @param n	Node to take editable data from.
	 */
	public void populateContainer(Node n) {
		if (n == null) {
			newNode = true;
			this.node = prepareNewNode();
		} else {
			newNode = false;
			this.node = tree.getSelectedNode();
		}
		/* Stop if we couldn't make a node. */
		if (this.node == null) {
			waba.sys.Vm.debug("Unable to make or open edit node.");
			getParentWindow().swap(null);
		} else {
			fmNode = (FreeMindNode) this.node.getUserObject();
		}

		/* Trap dud nodes. */
		if (node == null) {
			add(new Label("Nothing to edit."), CENTER, CENTER);
			return;
		}

		/* List the attributes */
		AttributeList.Iterator a = fmNode.atts.new Iterator();
		int textItem = 0;
		int i = 0;
		String selectedText = "";
		listAtts = new ListBox();

		while (a.next()) {
			listAtts.add(a.getAttributeAsString());
			if (a.getAttributeName().equals("TEXT")) {
				textItem = i;
				selectedText = a.getAttributeValue();
			}
			i++;
		}

		/* House list in a combobox */
		if (cbxAttrs!=null) remove(cbxAttrs);
		cbxAttrs = new ComboBox(listAtts);
		add(cbxAttrs);
		cbxAttrs.setRect(LEFT, TOP, FILL, PREFERRED);
		cbxAttrs.select(textItem);

		/* Fill the standard text box. */
		standardEdit.setText(selectedText);
		/* If the extended edit is selected, remove any old extened edit box and
		 * create a new one. */
		if (extendedEdit == true) {
			if (editDescription != null) {
				remove(editDescription);
				editDescription = null;
			}
			setExtendedEdit();
		}
		extendedEdit = false;

		/* Loop round for all button icons, and set their borders if this entry uses them. */
		for (i = 0; i < buttonList.size(); i++) {
			/* Pick the button. */
			Button b = (Button) buttonList.items[i];
			Image bImg = b.getImage();
			/* Assume no border needed. */
			b.setBorder(BORDER_NONE);
			iconSelected[i] = false;
			/* If this is an icon we know about, add a visible border. */
			for (int j = 0; j < node.userIcons.size(); j++) {
				if (((NodeIcon) (node.userIcons.items[j])).getImage()
					== bImg) {
					b.setBorder(BORDER_SIMPLE);
					iconSelected[i] = true;
					break;
				}
			}
		}

		/* Position the cursor at the end of any text. */
		standardEdit.setCursorPos(
			standardEdit.getLength(),
			standardEdit.getLength());
		getParentWindow().repaintNow();
	}

	/**
	 * Standard event handler.
	 */
	public void onEvent(Event event) {
		int n; /* Number of button found. */
		switch (event.type) {
			case ControlEvent.PRESSED :
				if (event.target == btnQuit) {
					/* Byebye */
					getParentWindow().swap(null);
				} else if (event.target == btnOk) {
					/* If we have been editing a new node, add it in. */
					if (newNode) {
						Node oldNode = tree.getSelectedNode();
						tree.collapse(oldNode);
						/* Tack the new child onto the end of the node's list. */
						model.insertNode(oldNode, node, -1);
						tree.expand(oldNode);
						tree.select(node);
					}

					/* Tack selected icons onto node. */
					node.deleteIcons();
					for (int i = 0; i < iconSelected.length; i++) {
						if (iconSelected[i]) {
							/* Add this icon into the tree's node with a BUILTIN attribute. */
							AttributeList a = new AttributeList();
							a.addAttribute(
								"BUILTIN",
								(String) (icons.iconNames.items[i]),
								(byte) '"');
							node.addIcon(
								new FmIcon((Image) icons.allIcons.items[i], a));
						}
					}

					/* Set the required attribute. Find out what one we've picked. */
					String key[] =
						splitTo(((String) listAtts.getSelectedItem()), "=");
					/* Trash the old object */
					fmNode.atts.remove(key[0]);
					/* Add in the new value. */
					String s;
					if (extendedEdit) {
						s = editDescription.getText();
					} else {
						s = standardEdit.getText();
					}
					fmNode.atts.addAttribute(key[0], s, (byte) ('\"'));
					/* Byebye */
					getParentWindow().swap(null);

				} else if (event.target == btnExtended) {
					/* User is trying to switch to extended edit mode. */
					if (extendedEdit) {
						/* We're already in extended edit mode. Go back to one-line mode. */
						extendedEdit = false;
						standardEdit.setText(editDescription.getText());
						/* When we remove a Mulit-edit box, the area is not correctly redrawn.
						 * so we blank it manually. */
						remove(editDescription);
						editDescription = null;
						standardEdit.requestFocus();
						standardEdit.setCursorPos(
							standardEdit.getLength(),
							standardEdit.getLength());
						getParentWindow().repaintNow();
					} else {
						/* We're not in extended edit mode. Go there. */
						setExtendedEdit();
					}
				} else if (event.target == cbxAttrs) {
					/* Get the data for the selected attribute and pop it in the edit control. */
					String key[] =
						splitTo(((String) listAtts.getSelectedItem()), "=");
					if (extendedEdit) {
						editDescription.setText(
							fmNode.atts.getAttributeValue(key[0]));
						editDescription.repaintNow();
					} else {
						standardEdit.setText(
							fmNode.atts.getAttributeValue(key[0]));
					}
				} else if ((n = buttonList.find(event.target)) >= 0) {
					/* Toggle the selected state for the icon. */
					Button b = ((Button) (buttonList.items[n]));
					if (iconSelected[n]) {
						b.setBorder(BORDER_NONE);
						iconSelected[n] = false;
					} else {
						b.setBorder(BORDER_RAISED);
						iconSelected[n] = true;
					}
				}
				break;
			case ControlEvent.FOCUS_IN :
				if ((event.target == this) && (standardEdit != null)) {
					standardEdit.requestFocus();
				}
				break;
		}
	}

	/**
	 * Create a new node with TEXT attribute.
	 * @param tf
	 * @return
	 */
	private Node prepareNewNode() {
		/* Create a new child node with a TEXT attribute. Collapse the node first
		 * to get around an unexplored redraw bug. */
		AttributeList newAtts = new AttributeList();
		newAtts.addAttribute("TEXT", "", (byte) 0);
		Node newNode =
			new Node(new FreeMindNode(null, newAtts, FmParser.nodeTag));
		/* Text nodes may have children. */
		newNode.setAllowsChildren(true);
		return newNode;
	}

	private String[] splitTo(String source, String splitter) {
		int x = source.indexOf(splitter);
		if (x < 0) {
			/* String not there. return nothings. */
			return null;
		} else {
			String[] s = new String[2];
			s[0] = source.substring(0, x);
			s[1] = source.substring(x + splitter.length());
			return s;
		}
	}

	/* Take a standard edit field and switch it into extended edit. */
	private void setExtendedEdit() {
		extendedEdit = true;
		String s = standardEdit.getText();
		Rect r = standardEdit.getRect();
		add(editDescription = new MultiEdit(2, 1), CENTER, r.y);
		editDescription.setText(s);
		editDescription.setRect(LEFT, SAME, FILL, bigEditBoxHeight);
		editDescription.requestFocus();
	}
}
