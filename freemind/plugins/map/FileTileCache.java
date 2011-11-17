/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2011 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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

package plugins.map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.openstreetmap.gui.jmapviewer.MemoryTileCache;
import org.openstreetmap.gui.jmapviewer.Tile;
import org.openstreetmap.gui.jmapviewer.interfaces.TileCache;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;

import freemind.main.Resources;

/**
 * @author foltin
 * @date 25.10.2011
 * TODO: Specify limit and delete longest unused tiles to keep limit.
 */
public class FileTileCache implements TileCache {

	String mDirectory = null;

	MemoryTileCache mMemoryCache = new MemoryTileCache();

	Vector mTileList = new Vector();

	/**
	 * 
	 */
	public FileTileCache() {
		setDirectory("%/osm");
	}

	public File getDirectory() {
		return new File(mDirectory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openstreetmap.gui.jmapviewer.interfaces.TileCache#getTile(org.
	 * openstreetmap.gui.jmapviewer.interfaces.TileSource, int, int, int)
	 */
	public Tile getTile(TileSource pSource, int pX, int pY, int pZ) {
		Tile tile = mMemoryCache.getTile(pSource, pX, pY, pZ);
		if (tile != null) {
			return tile;
		}
		String tileKey = getTileKey(pSource, pX, pY, pZ);
		File file = getFile(tileKey);
//		System.out.println("Searching for tile " + tileKey);
		if (file.exists()) {
			try {
				BufferedImage bufferedImage = ImageIO.read(file);
				Tile loadedTile = new Tile(pSource, pX, pY, pZ, bufferedImage);
				loadedTile.setLoaded(true);
				return loadedTile;
			} catch (IOException e) {
				freemind.main.Resources.getInstance().logException(e);
			}
		}
		return null;
	}

	public File getFile(String tileKey) {
		File file = new File(mDirectory + File.separator + tileKey);
		return file;
	}

	public String getTileKey(TileSource pSource, int pX, int pY, int pZ) {
		return Tile.getTileKey(pSource, pX, pY, pZ).replace("/", "_");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openstreetmap.gui.jmapviewer.interfaces.TileCache#addTile(org.
	 * openstreetmap.gui.jmapviewer.Tile)
	 */
	public void addTile(Tile pTile) {
		mMemoryCache.addTile(pTile);
		// Add to a queue for later saving.
		mTileList.add(pTile);
		for (Iterator it = mTileList.iterator(); it.hasNext();) {
			Tile tile = (Tile) it.next();
			if (tile.isLoaded() && !tile.hasError()) {
				saveTile(tile);
				it.remove();
			}

		}
	}

	public void saveTile(Tile pTile) {
		String tileKey = getTileKey(pTile.getSource(), pTile.getXtile(),
				pTile.getYtile(), pTile.getZoom());
		File file = getFile(tileKey);
		if (!file.exists()) {
			try {
				// System.out.println("Saving tile " + tileKey);
				ImageIO.write(pTile.getImage(), "png", file);
			} catch (IOException e) {
				freemind.main.Resources.getInstance().logException(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openstreetmap.gui.jmapviewer.interfaces.TileCache#getTileCount()
	 */
	public int getTileCount() {
		return getDirectory().list().length;
	}

	public void setDirectory(String pDirectory) {
		if (pDirectory.startsWith("%/")) {
			pDirectory = Resources.getInstance().getFreemindDirectory()
					+ File.separator + pDirectory.substring(2);
		}
		mDirectory = pDirectory;
		File dir = getDirectory();
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

}
