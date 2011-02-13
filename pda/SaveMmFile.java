import waba.ui.*;
import waba.io.*;
import waba.sys.*;
import waba.fx.*;

/* Pops up a screen that allows a user to select the file that they want to load. */
public class SaveMmFile extends Container {

	private FreeMindPDA main;
	private Button btnQuit;
	private Button btnOk;
	private Button btnArrow;
	private ListBox listFiles;			/* A drop-down list of files. */
	private Edit saveFile;
	private ListBox listDirs;			/* A drop-down list of directories. */
	private ComboBox cbxDirs;
	private String storeDir;

	protected String selectedFileName=null;

	public SaveMmFile(FreeMindPDA main) {
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
			/* Give the user a way to enter the filename. */
			add(new Label("Select filename to save as:"),LEFT,TOP+1);
			/* Our own popup trigger */
			btnArrow = Button.createArrowButton(Graphics.ARROW_DOWN,fmH*3/11,Color.BLACK); // guich@240_18
			if (Settings.uiStyle == Settings.PalmOS) btnArrow.setBorder(Button.BORDER_NONE);
			add(btnArrow);
			btnArrow.setRect(LEFT,AFTER+2,PREFERRED,fmH);
			/* Stick the editable filename to the right of the trigger. */
			add(saveFile=new Edit());
			saveFile.setRect(AFTER+1,SAME,FILL,PREFERRED);
			saveFile.setText(main.lastFilename); 
			
			/* House directory list in combobox */
			add(new Label("Change directory:"),LEFT,saveFile.getRect().y2()+10);
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
			} else if (event.target == btnArrow) {
				/* We fake an editable combo box popup. */
				PopList popFiles=new PopList(listFiles);
				popFiles.dontHideParent=true;
				popFiles.fullHeight=true;
				Rect r=saveFile.getAbsoluteRect();
				popFiles.setRect(r.x,r.y2(),FILL,PREFERRED);
				getParentWindow().popupBlockingModal(popFiles);
				/* If the user selected something, put it in the editable filename. */
				if (listFiles.getSelectedIndex()>=0) {
					saveFile.setText((String)listFiles.getSelectedItem());
				}
			} else if (event.target == btnOk) {
				/* Byebye */
				selectedFileName=saveFile.getText();
				main.saveFile(storeDir+selectedFileName);
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
