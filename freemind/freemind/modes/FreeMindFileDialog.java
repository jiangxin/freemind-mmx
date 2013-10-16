/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2012 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
*/

package freemind.modes;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * @author foltin
 * @date 23.02.2012
 */
public interface FreeMindFileDialog  {

	public interface DirectoryResultListener {
		void setChosenDirectory(File pDir);
	}
	
	public int showOpenDialog(Component pParent) throws HeadlessException;

	public int showSaveDialog(Component pParent) throws HeadlessException;

	public void setDialogTitle(String pDialogTitle);

	/**
	 * Sets the default file filter (that one that is activated at showup). 
	 * @see #addChoosableFileFilter(FileFilter)
	 */
	public void addChoosableFileFilterAsDefault(FileFilter pFilter);
	/**
	 * Adds a further file filter for optional use. It is not selected by default, but this is UI dependent.
	 * @see #addChoosableFileFilterAsDefault(FileFilter)
	 */
	public void addChoosableFileFilter(FileFilter pFilter);

	/**
	 * @param pMode JFileChooser.DIRECTORIES_ONLY, JFileChooser.FILES_ONLY, JFileChooser.FILES_AND_DIRECTORIES
	 */
	public void setFileSelectionMode(int pMode);

	public void setMultiSelectionEnabled(boolean pB);

	public boolean isMultiSelectionEnabled();

	/**
	 * @return
	 */
	public File[] getSelectedFiles();

	/**
	 * @return
	 */
	public File getSelectedFile();

	/**
	 * @param pLastCurrentDir
	 */
	public void setCurrentDirectory(File pLastCurrentDir);

	/**
	 * @param pFile
	 */
	public void setSelectedFile(File pFile);

	public void registerDirectoryResultListener(DirectoryResultListener pDirectoryResultListener);
	
	
}
