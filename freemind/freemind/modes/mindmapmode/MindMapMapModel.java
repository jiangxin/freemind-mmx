/*FreeMindget - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 */


package freemind.modes.mindmapmode;

import java.awt.Color;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.JOptionPane;

import freemind.common.OptionalDontShowMeAgainDialog;
import freemind.common.UnicodeReader;
import freemind.main.FreeMind;
import freemind.main.FreeMindMain;
import freemind.main.HtmlTools;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.main.XMLParseException;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapLinkRegistry;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.NodeAdapter;

public class MindMapMapModel extends MapAdapter {

	public static final String MAP_INITIAL_START = "<map version=\"";
	public static final String RESTORE_MODE_MIND_MAP = "MindMap:";
	public static final String FREEMIND_VERSION_UPDATER_XSLT = "freemind/modes/mindmapmode/freemind_version_updater.xslt";
	LockManager lockManager;
	private MindMapLinkRegistry linkRegistry;
	private Timer timerForAutomaticSaving;
	/**
	 * The current version and all other version that don't need XML update for
	 * sure.
	 */
	public static final String EXPECTED_START_STRINGS[] = {
			MAP_INITIAL_START + FreeMind.XML_VERSION + "\"",
			MAP_INITIAL_START + "0.7.1\"" };

	//
	// Constructors
	//

	public MindMapMapModel(FreeMindMain frame, ModeController modeController) {
		this(null, frame, modeController);
	}

	public MindMapMapModel(MindMapNodeModel root, FreeMindMain frame,
			ModeController modeController) {
		super(frame, modeController);
		lockManager = Resources.getInstance().getBoolProperty(
				"experimental_file_locking_on") ? new LockManager()
				: new DummyLockManager();

		// register new LinkRegistryAdapter
		linkRegistry = new MindMapLinkRegistry();

		if (root == null)
			root = new MindMapNodeModel(frame.getResourceString("new_mindmap"),
					frame, this);
		setRoot(root);
		readOnly = false;
		// automatic save: start timer after the map is completely loaded
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				scheduleTimerForAutomaticSaving();
			}
		});
	}

	//

	public MindMapLinkRegistry getLinkRegistry() {
		return linkRegistry;
	}

	public String getRestorable() {
		return getFile() == null ? null : RESTORE_MODE_MIND_MAP
				+ getFile().getAbsolutePath();
	}

	public void changeNode(MindMapNode node, String newText) {
		if (node.toString().startsWith("<html>")) {
			node.setUserObject(HtmlTools.unescapeHTMLUnicodeEntity(newText));
		} else {
			node.setUserObject(newText);
		}
		nodeChanged(node);
	}

	//
	// Other methods
	//

	public String toString() {
		return getFile() == null ? null : getFile().getName();
	}

	//
	// Export and saving
	//

	public String getAsHTML(List mindMapNodes) {
		// Returns success of the operation.
		try {
			StringWriter stringWriter = new StringWriter();
			BufferedWriter fileout = new BufferedWriter(stringWriter);
			MindMapController.saveHTML(mindMapNodes, fileout);
			fileout.close();

			return stringWriter.toString();
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			return null;
		}
	}

	public String getAsPlainText(List mindMapNodes) {
		// Returns success of the operation.
		try {
			StringWriter stringWriter = new StringWriter();
			BufferedWriter fileout = new BufferedWriter(stringWriter);

			for (ListIterator it = mindMapNodes.listIterator(); it.hasNext();) {
				((MindMapNodeModel) it.next()).saveTXT(fileout,/* depth= */0);
			}

			fileout.close();
			return stringWriter.toString();

		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			return null;
		}
	}

	public boolean saveTXT(MindMapNodeModel rootNodeOfBranch, File file) {
		// Returns success of the operation.
		try {
			BufferedWriter fileout = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file)));
			rootNodeOfBranch.saveTXT(fileout,/* depth= */0);
			fileout.close();
			return true;

		} catch (Exception e) {
			System.err.println("Error in MindMapMapModel.saveTXT(): ");
			freemind.main.Resources.getInstance().logException(e);
			return false;
		}
	}

	public String getAsRTF(List mindMapNodes) {
		// Returns success of the operation.
		try {
			StringWriter stringWriter = new StringWriter();
			BufferedWriter fileout = new BufferedWriter(stringWriter);
			saveRTF(mindMapNodes, fileout);
			fileout.close();

			return stringWriter.toString();
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			return null;
		}
	}

	public boolean saveRTF(List mindMapNodes, BufferedWriter fileout) {
		// Returns success of the operation.
		try {

			// First collect all used colors
			HashSet colors = new HashSet();
			for (ListIterator it = mindMapNodes.listIterator(); it.hasNext();) {
				((MindMapNodeModel) it.next()).collectColors(colors);
			}

			// Prepare table of colors containing indices to color table
			String colorTableString = "{\\colortbl;\\red0\\green0\\blue255;";
			// 0 - Automatic, 1 - blue for links

			HashMap colorTable = new HashMap();
			int colorPosition = 2;
			for (Iterator it = colors.iterator(); it.hasNext(); ++colorPosition) {
				Color color = (Color) it.next();
				colorTableString += "\\red" + color.getRed() + "\\green"
						+ color.getGreen() + "\\blue" + color.getBlue() + ";";
				colorTable.put(color, new Integer(colorPosition));
			}
			colorTableString += "}";

			fileout.write("{\\rtf1\\ansi\\ansicpg1252\\deff0\\deflang1033{\\fonttbl{\\f0\\fswiss\\fcharset0 Arial;}"
					+ colorTableString
					+ "}"
					+ "\\viewkind4\\uc1\\pard\\f0\\fs20{}");
			// ^ If \\ud is appended here, Unicode does not work in MS Word.

			for (ListIterator it = mindMapNodes.listIterator(); it.hasNext();) {
				((MindMapNodeModel) it.next()).saveRTF(fileout,/* depth= */0,
						colorTable);
			}

			fileout.write("}");
			return true;
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			return false;
		}
	}

	/**
	 * Return the success of saving
	 */
	public boolean save(File file) {
		boolean result;
		synchronized (this) {
			result = saveInternal(file, false);
			// TODO: Set only, when ok?
			if (result) {
				setFileTime();
			}
		}
		return result;
	}

	/**
	 * This method is intended to provide both normal save routines and saving
	 * of temporary (internal) files.
	 */
	private boolean saveInternal(File file, boolean isInternal) {
		if (!isInternal && readOnly) { // unexpected situation, yet it's better
										// to back it up
			System.err.println("Attempt to save read-only map.");
			return false;
		}
		try {
			// Generating output Stream
			if (timerForAutomaticSaving != null) {
				timerForAutomaticSaving.cancel();
			}
			BufferedWriter fileout = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file)));
			getXml(fileout);

			if (!isInternal) {
				setFile(file);
				setSaved(true);
			}
			scheduleTimerForAutomaticSaving();
			return true;
		} catch (FileNotFoundException e) {
			String message = Tools.expandPlaceholders(getText("save_failed"),
					file.getName());
			if (!isInternal)
				getFrame().getController().errorMessage(message);
			else
				getFrame().out(message);
		} catch (Exception e) {
			logger.severe("Error in MindMapMapModel.save(): ");
			freemind.main.Resources.getInstance().logException(e);
		}
		scheduleTimerForAutomaticSaving();
		return false;
	}

	/**
	 * writes the content of the map to a writer.
	 * 
	 * @throws IOException
	 */
	public void getXml(Writer fileout, boolean saveInvisible)
			throws IOException {
		getXml(fileout, saveInvisible, getRootNode());
	}

	/**
	 * writes the content of the map to a writer.
	 * 
	 * @throws IOException
	 */
	public void getXml(Writer fileout, boolean saveInvisible,
			MindMapNode pRootNode) throws IOException {
		fileout.write("<map ");
		fileout.write("version=\"" + FreeMind.XML_VERSION + "\"");
		fileout.write(">\n");
		fileout.write("<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->\n");
		getRegistry().save(fileout);
		pRootNode.save(fileout, this.getLinkRegistry(), saveInvisible, true);
		fileout.write("</map>\n");
		fileout.close();
	}

	public void getXml(Writer fileout) throws IOException {
		getXml(fileout, true);
	}

	public void getFilteredXml(Writer fileout) throws IOException {
		getXml(fileout, false);
	}

	/**
	 * Attempts to lock the map using a semaphore file
	 * 
	 * @return If the map is locked, return the name of the locking user,
	 *         otherwise return null.
	 * @throws Exception
	 *             , when the locking failed for other reasons than that the
	 *             file is being edited.
	 */
	public String tryToLock(File file) throws Exception {
		String lockingUser = lockManager.tryToLock(file);
		String lockingUserOfOldLock = lockManager.popLockingUserOfOldLock();
		if (lockingUserOfOldLock != null) {
			getFrame().getController().informationMessage(
					Tools.expandPlaceholders(
							getText("locking_old_lock_removed"),
							file.getName(), lockingUserOfOldLock));
		}
		if (lockingUser == null) {
			readOnly = false;
		} // The map sure is not read only when the locking suceeded.
		return lockingUser;
	}

	public void load(URL url) throws FileNotFoundException, IOException,
			XMLParseException, URISyntaxException {
		logger.info("Loading file: " + url.toString());
		File file = Tools.urlToFile(url);
		load(file);
	}

	public void load(File file) throws FileNotFoundException, IOException {
		if (!file.exists()) {
			throw new FileNotFoundException(Tools.expandPlaceholders(
					getText("file_not_found"), file.getPath()));
		}
		if (!file.canWrite()) {
			readOnly = true;
		} else {
			// try to lock the map
			try {
				String lockingUser = tryToLock(file);
				if (lockingUser != null) {
					getFrame().getController().informationMessage(
							Tools.expandPlaceholders(
									getText("map_locked_by_open"),
									file.getName(), lockingUser));
					readOnly = true;
				} else {
					readOnly = false;
				}
			} catch (Exception e) { // Thrown by tryToLock
				freemind.main.Resources.getInstance().logException(e);
				getFrame().getController().informationMessage(
						Tools.expandPlaceholders(
								getText("locking_failed_by_open"),
								file.getName()));
				readOnly = true;
			}
		}

		synchronized (this) {
			MindMapNodeModel root = loadTree(file);
			if (root != null) {
				setRoot(root);
			}
			setFile(file);
			setFileTime();
		}
	}

	/** When a map is closed, this method is called. */
	public void destroy() {
		super.destroy();
		lockManager.releaseLock();
		lockManager.releaseTimer();
		if (timerForAutomaticSaving != null) {
			/* cancel the timer, if map is closed. */
			timerForAutomaticSaving.cancel();
		}
	}

	MindMapNodeModel loadTree(final File pFile) throws XMLParseException,
			IOException {
		return loadTree(new FileReaderCreator(pFile));
	}

	public static class StringReaderCreator implements ReaderCreator {

		private final String mString;

		public StringReaderCreator(String pString) {
			mString = pString;
		}

		public Reader createReader() throws FileNotFoundException {
			return new StringReader(mString);
		}

		public String toString() {
			return mString;
		}
	}

	private static class FileReaderCreator implements ReaderCreator {
		private final File mFile;

		public FileReaderCreator(File pFile) {
			mFile = pFile;
		}

		public Reader createReader() throws FileNotFoundException {
			return new UnicodeReader(new FileInputStream(mFile), "UTF-8");
		}

		public String toString() {
			return mFile.getName();
		}
	}

	public interface ReaderCreator {
		Reader createReader() throws FileNotFoundException;
	}

	MindMapNodeModel loadTree(ReaderCreator pReaderCreator)
			throws XMLParseException, IOException {
		return loadTree(pReaderCreator, true);
	}

	public MindMapNodeModel loadTree(ReaderCreator pReaderCreator,
			boolean pAskUserBeforeUpdate) throws XMLParseException, IOException {
		int versionInfoLength;
		versionInfoLength = EXPECTED_START_STRINGS[0].length();
		// reading the start of the file:
		StringBuffer buffer = readFileStart(pReaderCreator.createReader(),
				versionInfoLength);
		// the resulting file is accessed by the reader:
		Reader reader = null;
		for (int i = 0; i < EXPECTED_START_STRINGS.length; i++) {
			versionInfoLength = EXPECTED_START_STRINGS[i].length();
			String mapStart = "";
			if (buffer.length() >= versionInfoLength) {
				mapStart = buffer.substring(0, versionInfoLength);
			}
			if (mapStart.startsWith(EXPECTED_START_STRINGS[i])) {
				// actual version:
				reader = Tools.getActualReader(pReaderCreator.createReader());
				break;
			}
		}
		if (reader == null) {
			if (pAskUserBeforeUpdate && !Tools.isHeadless()) {
				int showResult = new OptionalDontShowMeAgainDialog(
						mModeController.getFrame().getJFrame(),
						mModeController.getSelectedView(),
						"really_convert_to_current_version2",
						"confirmation",
						mModeController,
						new OptionalDontShowMeAgainDialog.StandardPropertyHandler(
								mModeController.getController(),
								FreeMind.RESOURCES_CONVERT_TO_CURRENT_VERSION),
						OptionalDontShowMeAgainDialog.ONLY_OK_SELECTION_IS_STORED)
						.show().getResult();
				if (showResult != JOptionPane.OK_OPTION) {
					throw new IllegalArgumentException(
							"We should not open the reader " + pReaderCreator);
				}
			}
			reader = Tools.getUpdateReader(pReaderCreator.createReader(),
					FREEMIND_VERSION_UPDATER_XSLT, getFrame());
			if (reader == null) {
				// something went wrong on update:
				// FIXME: Translate me.
				this.getModeController()
						.getFrame()
						.out("Error on conversion. Continue without conversion. Some elements may be lost!");
				reader = Tools.getActualReader(pReaderCreator.createReader());
			}
		}
		try {
			HashMap IDToTarget = new HashMap();
			return (MindMapNodeModel) mModeController.createNodeTreeFromXml(
					reader, IDToTarget);
			// MindMapXMLElement mapElement = new
			// MindMapXMLElement(mModeController);
			// mapElement.parseFromReader(reader);
			// // complete the arrow links:
			// mapElement.processUnfinishedLinks(getLinkRegistry());
			// // we wait with "invokeHooksRecursively" until the map is fully
			// // registered.
			// return (MindMapNodeModel) mapElement.getMapChild();
		} catch (Exception ex) {
			String errorMessage = "Error while parsing file:" + ex;
			System.err.println(errorMessage);
			freemind.main.Resources.getInstance().logException(ex);
			MindMapXMLElement mapElement = new MindMapXMLElement(
					mModeController);
			NodeAdapter result = mapElement.createNodeAdapter(getFrame(), null);
			result.setText(errorMessage);
			return (MindMapNodeModel) result;
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Returns pMinimumLength bytes of the files content.
	 * 
	 * @return an empty string buffer, if something fails.
	 */
	private StringBuffer readFileStart(Reader pReader, int pMinimumLength) {
		BufferedReader in = null;
		StringBuffer buffer = new StringBuffer();
		try {
			// get the file start into the memory:
			in = new BufferedReader(pReader);
			String str;
			while ((str = in.readLine()) != null) {
				buffer.append(str);
				if (buffer.length() >= pMinimumLength)
					break;
			}
			in.close();
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			return new StringBuffer();
		}
		return buffer;
	}

	private void scheduleTimerForAutomaticSaving() {
		int numberOfTempFiles = Integer.parseInt(getFrame().getProperty(
				"number_of_different_files_for_automatic_save"));
		boolean filesShouldBeDeletedAfterShutdown = Resources.getInstance()
				.getBoolProperty("delete_automatic_saves_at_exit");
		String path = getFrame().getProperty("path_to_automatic_saves");
		/* two standard values: */
		if (Tools.safeEquals(path, "default")) {
			path = null;
		}
		if (Tools.safeEquals(path, "freemind_home")) {
			path = getFrame().getFreemindDirectory();
		}
		int delay = Integer.parseInt(getFrame().getProperty(
				"time_for_automatic_save"));
		File dirToStore = null;
		if (path != null) {
			dirToStore = new File(path);
			/* existence? */
			if (!dirToStore.isDirectory()) {
				dirToStore = null;
				System.err.println("Temporary directory " + path
						+ " not found. Disabling automatic store.");
				delay = Integer.MAX_VALUE;
				return;
			}
		}
		timerForAutomaticSaving = new Timer();
		timerForAutomaticSaving.schedule(new DoAutomaticSave(
				MindMapMapModel.this, numberOfTempFiles,
				filesShouldBeDeletedAfterShutdown, dirToStore), delay, delay);
	}

	private class LockManager extends TimerTask {
		File lockedSemaphoreFile = null;
		Timer lockTimer = null;
		final long lockUpdatePeriod = 4 * 60 * 1000; // four minutes
		final long lockSafetyPeriod = 5 * 60 * 1000; // five minutes
		String lockingUserOfOldLock = null;

		private File getSemaphoreFile(File mapFile) {
			return new File(mapFile.getParent()
					+ System.getProperty("file.separator") + "$~"
					+ mapFile.getName() + "~");
		}

		public synchronized String popLockingUserOfOldLock() {
			String toReturn = lockingUserOfOldLock;
			lockingUserOfOldLock = null;
			return toReturn;
		}

		private void writeSemaphoreFile(File inSemaphoreFile) throws Exception {
			FileOutputStream semaphoreOutputStream = new FileOutputStream(
					inSemaphoreFile);
			FileLock lock = null;
			try {
				lock = semaphoreOutputStream.getChannel().tryLock();
				if (lock == null) {
					semaphoreOutputStream.close();
					System.err.println("Locking failed.");
					throw new Exception();
				}
			} // locking failed
			catch (UnsatisfiedLinkError eUle) {
			} // This may come with Windows95. We don't insist on detailed
				// locking in that case.
			catch (NoClassDefFoundError eDcdf) {
			} // ^ just like above.
			// ^ On Windows95, the necessary libraries are missing.
			semaphoreOutputStream.write(System.getProperty("user.name")
					.getBytes());
			semaphoreOutputStream.write('\n');
			semaphoreOutputStream.write(String.valueOf(
					System.currentTimeMillis()).getBytes());
			semaphoreOutputStream.close();
			semaphoreOutputStream = null;
			Tools.setHidden(inSemaphoreFile, true, /* synchro= */false); // Exception
																			// free
			if (lock != null)
				lock.release();
		}

		public synchronized String tryToLock(File file) throws Exception {
			// Locking should work for opening as well as for saving as.
			// We are especially carefull when it comes to exclusivity of
			// writing.

			File semaphoreFile = getSemaphoreFile(file);
			if (semaphoreFile == lockedSemaphoreFile) {
				return null;
			}
			try {
				BufferedReader semaphoreReader = new BufferedReader(
						new FileReader(semaphoreFile));
				String lockingUser = semaphoreReader.readLine();

				long lockTime = new Long(semaphoreReader.readLine())
						.longValue();
				long timeDifference = System.currentTimeMillis() - lockTime;
				// catch (NumberFormatException enf) {} // This means that the
				// time was not written at all - lock is corrupt
				if (timeDifference > lockSafetyPeriod) { // the lock is old
					semaphoreReader.close();
					lockingUserOfOldLock = lockingUser;
					semaphoreFile.delete();
				} else
					return lockingUser;
			} catch (FileNotFoundException e) {
			}

			writeSemaphoreFile(semaphoreFile);

			if (lockTimer == null) {
				lockTimer = new Timer();
				lockTimer.schedule(this, lockUpdatePeriod, lockUpdatePeriod);
			}
			releaseLock();
			lockedSemaphoreFile = semaphoreFile;
			return null;
		}

		public synchronized void releaseLock() {
			if (lockedSemaphoreFile != null) {
				lockedSemaphoreFile.delete();
				lockedSemaphoreFile = null;
			}
		} // this may fail, TODO: ensure real deletion

		public synchronized void releaseTimer() {
			if (lockTimer != null) {
				lockTimer.cancel();
				lockTimer = null;
			}
		}

		public synchronized void run() { // update semaphore file
			if (lockedSemaphoreFile == null) {
				System.err
						.println("unexpected: lockedSemaphoreFile is null upon lock update");
				return;
			}
			try {
				Tools.setHidden(lockedSemaphoreFile, false, /* synchro= */true); // Exception
																					// free
				// ^ We unhide the file before overwriting because JavaRE1.4.2
				// does
				// not let us open hidden files for writing. This is a
				// workaround for Java bug,
				// I guess.

				writeSemaphoreFile(lockedSemaphoreFile);
			} catch (Exception e) {
				freemind.main.Resources.getInstance().logException(e);
			}
		}
	}

	private class DummyLockManager extends LockManager {
		public synchronized String popLockingUserOfOldLock() {
			return null;
		}

		public synchronized String tryToLock(File file) throws Exception {
			return null;
		}

		public synchronized void releaseLock() {
		}

		public synchronized void releaseTimer() {
		}

		public synchronized void run() {
		}
	}

	static private class DoAutomaticSave extends TimerTask {
		private MindMapMapModel model;
		private Vector tempFileStack;
		private int numberOfFiles;
		private boolean filesShouldBeDeletedAfterShutdown;
		private File pathToStore;
		/**
		 * This value is compared with the result of
		 * getNumberOfChangesSinceLastSave(). If the values coincide, no further
		 * automatic saving is performed until the value changes again.
		 */
		private int changeState;

		DoAutomaticSave(MindMapMapModel model, int numberOfTempFiles,
				boolean filesShouldBeDeletedAfterShutdown, File pathToStore) {
			this.model = model;
			tempFileStack = new Vector();
			numberOfFiles = ((numberOfTempFiles > 0) ? numberOfTempFiles : 1);
			this.filesShouldBeDeletedAfterShutdown = filesShouldBeDeletedAfterShutdown;
			this.pathToStore = pathToStore;
			changeState = model.getNumberOfChangesSinceLastSave();
		}

		public void run() {
			/* Map is dirty enough? */
			if (model.getNumberOfChangesSinceLastSave() == changeState)
				return;
			changeState = model.getNumberOfChangesSinceLastSave();
			if (changeState == 0) {
				/* map was recently saved. */
				return;
			}
			try {
				cancel();
				EventQueue.invokeAndWait(new Runnable() {
					public void run() {
						/* Now, it is dirty, we save it. */
						File tempFile;
						if (tempFileStack.size() >= numberOfFiles)
							tempFile = (File) tempFileStack.remove(0); // pop
						else {
							try {
								tempFile = File.createTempFile(
										"FM_"
												+ ((model.toString() == null) ? "unnamed"
														: model.toString()),
										freemind.main.FreeMindCommon.FREEMIND_FILE_EXTENSION,
										pathToStore);
								if (filesShouldBeDeletedAfterShutdown)
									tempFile.deleteOnExit();
							} catch (Exception e) {
								System.err
										.println("Error in automatic MindMapMapModel.save(): "
												+ e.getMessage());
								freemind.main.Resources.getInstance()
										.logException(e);
								return;
							}
						}
						try {
							model.saveInternal(tempFile, true /* =internal call */);
							model.getFrame()
									.out(Resources
											.getInstance()
											.format("automatically_save_message",
													new Object[] { tempFile
															.toString() }));
						} catch (Exception e) {
							System.err
									.println("Error in automatic MindMapMapModel.save(): "
											+ e.getMessage());
							freemind.main.Resources.getInstance().logException(
									e);
						}
						tempFileStack.add(tempFile); // add at the back.
					}
				});
			} catch (InterruptedException e) {
				freemind.main.Resources.getInstance().logException(e);
			} catch (InvocationTargetException e) {
				freemind.main.Resources.getInstance().logException(e);
			}
		}
	}
}
