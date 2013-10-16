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

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;


/**
 * File Chooser for OS windows and linux (without Mac)
 * @author foltin
 * @date 23.02.2012
 */
public class FreeMindJFileDialog extends JFileChooser implements FreeMindFileDialog  {

	private DirectoryResultListener mDirectoryResultListener = null;

	/* (non-Javadoc)
	 * @see freemind.modes.FreeMindFileDialog#registerDirectoryResultListener(freemind.modes.FreeMindFileDialog.DirectoryResultListener)
	 */
	public void registerDirectoryResultListener(
			DirectoryResultListener pDirectoryResultListener) {
				mDirectoryResultListener = pDirectoryResultListener;
		
	}
	
	protected void callDirectoryListener(final int result) {
		if(result == JFileChooser.APPROVE_OPTION && mDirectoryResultListener != null) {
			try {
				mDirectoryResultListener.setChosenDirectory(getCurrentDirectory());
			} catch (Exception e) {
				freemind.main.Resources.getInstance().logException(e);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JFileChooser#showOpenDialog(java.awt.Component)
	 */
	public int showOpenDialog(Component pParent) throws HeadlessException {
		// TODO Auto-generated method stub
		final int result = super.showOpenDialog(pParent);
		callDirectoryListener(result);
		return result;
	}


	/* (non-Javadoc)
	 * @see javax.swing.JFileChooser#showSaveDialog(java.awt.Component)
	 */
	public int showSaveDialog(Component pParent) throws HeadlessException {
		final int result = super.showSaveDialog(pParent);
		callDirectoryListener(result);
		return result;
	}

	/* (non-Javadoc)
	 * @see freemind.modes.FreeMindFileDialog#addChoosableFileFilterAsDefault(javax.swing.filechooser.FileFilter)
	 */
	public void addChoosableFileFilterAsDefault(FileFilter pFilter) {
		addChoosableFileFilter(pFilter);
		setFileFilter(pFilter);
	}
}
