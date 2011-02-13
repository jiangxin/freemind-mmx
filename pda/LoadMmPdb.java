import waba.ui.*;
import waba.io.*;

/* Pops up a screen that allows a user to select the PDB entry that they want to load. */
public class LoadMmPdb extends Container {

	private FreeMindPDA main;
	private Button btnQuit;
	private Button btnOk;
	private ListBox listMaps;			/* A drop-down list of files. */
	private ComboBox cbxMaps;
	private Catalog dBase;
	protected String selectedFileName=null;

	public LoadMmPdb(FreeMindPDA main) {
		super();
		this.main=main;
	}
	
	public void onStart() {
		btnQuit=new Button("Quit");
		add(btnQuit,LEFT,BOTTOM);

		/* Open the PDB and get a list of entries from it. */
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
			listMaps.add(getEntryMapName(i));
		}

		/* House list in combobox */
		add(new Label("Select map to load:"),LEFT,TOP+1);
		cbxMaps=new ComboBox(listMaps);
		add(cbxMaps);
		cbxMaps.setRect(LEFT,AFTER+2,FILL,PREFERRED); 
		cbxMaps.select(listMaps.getItemAt(0));
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
			} else if (event.target == btnOk) {
				/* Load the relevant entry. */
				int i=cbxMaps.getSelectedIndex();
				if (i>=0) {
					if (!dBase.isOpen()) {
						waba.sys.Vm.debug("Attempted load from closed file.");
					}
					main.loadPdb(dBase,i,(String)cbxMaps.getSelectedItem());
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
