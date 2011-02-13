import waba.ui.*;
import waba.io.*;
import waba.sys.*;
import waba.fx.*;

/* Pops up a screen that allows a user to select the PDB entry that they want to load. */
public class SaveMmPdb extends Container {

	private FreeMindPDA main;
	private Button btnQuit;
	private Button btnOk;
	private Button btnArrow;
	private ListBox listMaps;			/* A drop-down list of files. */
	private Edit saveMap;
	private Catalog dBase;
	protected String selectedFileName=null;

	public SaveMmPdb(FreeMindPDA main) {
		super();
		this.main=main;
	}
	
	public void onStart() {
		btnQuit=new Button("Quit");
		add(btnQuit,LEFT,BOTTOM);

		/* Open the PDB and get a list of entries from it. Just like the load routine. */
		dBase=new Catalog(FreeMindPDA.dbName,Catalog.CREATE);

		/* If it's not open, don't give them anything to do but quit. */
		if (!dBase.isOpen()) {
			add(new Label("Unable to open database"),CENTER,CENTER);
			return;
		}

		btnOk=new Button("  OK  ");
		add(btnOk,RIGHT,BOTTOM);

		/* Loop round for all entries, and extract the map name from each one
		 * into the listbox. */
		listMaps = new ListBox();

		for (int i=0; i<dBase.getRecordCount();i++) {
			String s=getEntryMapName(i);
			listMaps.add(s);
		}

		/* Give the user a way to enter the filename. */
		add(new Label("Select map name to save as:"),LEFT,TOP+1);
		/* Our own popup trigger */
		btnArrow = Button.createArrowButton(Graphics.ARROW_DOWN,fmH*3/11,Color.BLACK); // guich@240_18
		if (Settings.uiStyle == Settings.PalmOS) btnArrow.setBorder(Button.BORDER_NONE);
		add(btnArrow);
		btnArrow.setRect(LEFT,AFTER+2,PREFERRED,fmH);
		/* Stick the editable filename to the right of the trigger. */
		add(saveMap=new Edit());
		saveMap.setRect(AFTER+1,SAME,FILL,PREFERRED);
		saveMap.setText(main.lastFilename); 

	}
	
	/**
	 * Standard event handler.
	 */
	public void onEvent(Event event) {
		if (event.type == ControlEvent.PRESSED) {
			if (event.target == btnQuit) {
				/* Byebye */
				dBase.close();
				getParentWindow().swap(null);
			} else if (event.target == btnArrow) {
				/* We fake an editable combo box popup. */
				PopList popFiles=new PopList(listMaps);
				popFiles.dontHideParent=true;
				popFiles.fullHeight=true;
				Rect r=saveMap.getAbsoluteRect();
				popFiles.setRect(r.x,r.y2(),FILL,PREFERRED);
				getParentWindow().popupBlockingModal(popFiles);
				/* If the user selected something, put it in the editable filename. */
				if (listMaps.getSelectedIndex()>=0) {
					saveMap.setText((String)listMaps.getSelectedItem());
				}
			} else if (event.target == btnOk) {
				/* Find the relevant entry, or create a new one as appropriate. */
				Object[] items = listMaps.getItems();
				int i, n = (items != null) ? items.length : 0;
				String name = saveMap.getText();
				for(i=0; i<n; i++) {
					String item = (String)items[i];
					if(item.equals(name)) {
						break;
					}
				}
				if(i < 0) {
					i = dBase.getRecordCount();
				}
//				waba.sys.Vm.debug("We're only saving one damn entry right now.");
				if (i>=0) {
					main.savePdb(dBase,i,saveMap.getText());
					dBase.close();
					getParentWindow().swap(null);
				}
			}
		}
	}

	/**
	 * Read in the entry until we get a  "TEXT=\"" entry, and return
	 * the resulting string.
	 * @param rec		Record number to examine
	 * @return			name of map.
	 */
	private String getEntryMapName(int rec) {
		dBase.setRecordPos(rec);
		DataStream ds = new DataStream(dBase);
		String fileName = ds.readString();
		return fileName;
	}
}
