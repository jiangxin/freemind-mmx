import waba.ui.*;
import waba.io.*;

/* Pops up a screen that allows a user to select the file that they want to load. */
public class LoadMmFile extends Container {

	private FreeMindPDA main;
	private Button btnQuit;
	private Button btnOk;
	private ListBox listFiles;			/* A drop-down list of files. */
	private ComboBox cbxFiles;
	private ListBox listDirs;			/* A drop-down list of directories. */
	private ComboBox cbxDirs;
	private String storeDir;
	protected String selectedFileName=null;

	public LoadMmFile(FreeMindPDA main) {
		super();
		this.main=main;
	}
	
	public void onStart() {
		btnQuit=new Button("Quit");
		add(btnQuit,LEFT,BOTTOM);

		/* Construct a list of files on the SD Card. */
		File file=main.getStorageDir();

		if ((file==null)||(!file.exists())) {
			/* The filesystem we have chosen does not exits. Put up a warning. */
			add(new Label("No file system found."),CENTER,CENTER);
			return;
		}

		/* Filesystem exists, so we can add the OK button etc. */
		storeDir=file.getPath();
		btnOk=new Button("  OK  ");
		add(btnOk,RIGHT,BOTTOM);

		String[] list = file.listFiles();

		/* Construct list from selected files & dirs. */
		listFiles = new ListBox();
		listDirs = new ListBox();
		listDirs.add("Not implemented yet.");

		if (list != null) {
			for (int i = 0; i < list.length; i++)
				if (list[i] != null) {
					if (isDir(list[i])) {
						listDirs.add(list[i]);
					} else {
						if (list[i].toLowerCase().endsWith(".mm")) {
							listFiles.add(list[i]);
						}
					}
				}
			/* House lists in comboboxes */
			add(new Label("Select file to load:"),LEFT,TOP+1);
			cbxFiles=new ComboBox(listFiles);
			add(cbxFiles);
			cbxFiles.setRect(LEFT,AFTER+2,FILL,PREFERRED); 
			cbxFiles.select(listFiles.getItemAt(0));

			add(new Label("Change directory:"),LEFT,AFTER+10);
			cbxDirs=new ComboBox(listDirs);
			add(cbxDirs);
			cbxDirs.setRect(LEFT,AFTER,FILL,PREFERRED); 
			cbxDirs.select(listDirs.getItemAt(0));

		}
	}
	
	/**
	 * Standard event handler.
	 */
	public void onEvent(Event event) {
		if (event.type == ControlEvent.PRESSED) {
			if (event.target == btnQuit) {
				/* Byebye */
				getParentWindow().swap(null);
			} else if (event.target == btnOk) {
				/* Byebye */
				if (cbxFiles.getSelectedIndex()>=0)
				selectedFileName=(String)cbxFiles.getSelectedItem();
				main.loadFile(storeDir+selectedFileName);
				getParentWindow().swap(null);
			}
		}
	}

	private boolean isDir(String string) {
		if (string.endsWith("/"))
			return true;

		return false;
	}


}
