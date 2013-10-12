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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.openstreetmap.gui.jmapviewer.Tile;
import org.openstreetmap.gui.jmapviewer.TileController;

import freemind.main.Tools;

public class TileImage implements ImageObserver {

	private Tile[][] mTiles = null;
	private boolean mTilesPresent = false;
	private boolean mImageCreated = false;
	private BufferedImage mImage;
	private int mWaitingForCallbacks = 0;
	private int mDx;
	private int mDy;

	public TileImage() {

	}

	public boolean isLoaded() {
		if (!mTilesPresent)
			return false;
		for (int i = 0; i < mTiles.length; i++) {
			Tile[] tiles = mTiles[i];
			for (int j = 0; j < tiles.length; j++) {
				Tile tile = tiles[j];
				if (!tile.isLoaded() && !tile.hasError()) {
					System.out.println("Tile " + tile + " is not loaded:"
							+ tile.getStatus());
					return false;
				}
			}
		}
		if (!mImageCreated) {
			createImage();
			mImageCreated = true;
		}
		return isDrawingDone();
	}

	/**
	 * Is called when all tiles are loaded and creates the common picture.
	 */
	private void createImage() {
		BufferedImage tileImage00 = mTiles[0][0].getImage();
		int height = tileImage00.getHeight();
		int width = tileImage00.getWidth();
		mImage = new BufferedImage(height * mTiles[0].length, width
				* mTiles.length, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = (Graphics2D) mImage.getGraphics();
		for (int i = 0; i < mTiles.length; i++) {
			Tile[] tiles = mTiles[i];
			for (int j = 0; j < tiles.length; j++) {
				Tile tile = tiles[j];
				boolean done = graphics.drawImage(tile.getImage(), i
						* height, j * width, this);
				if (!done) {
					mWaitingForCallbacks++;
				}
			}
		}
		if (isDrawingDone()) {
			drawCross();
		}
	}

	public boolean isDrawingDone() {
		return mWaitingForCallbacks <= 0;
	}

	public void drawCross() {
		Graphics2D graphics = (Graphics2D) mImage.getGraphics();
		graphics.setColor(Color.RED);
		graphics.setStroke(new BasicStroke(4));
		int size = 15;
		graphics.drawLine(mDx - size, mDy, mDx + size, mDy);
		graphics.drawLine(mDx, mDy - size, mDx, mDy + size);
	}

	public void load(String pCodedImage) {
		try {
			mImage = ImageIO.read(new ByteArrayInputStream(Tools
					.fromBase64(pCodedImage)));
			mTilesPresent = false;
			mImageCreated = true;
		} catch (IOException e) {
			freemind.main.Resources.getInstance().logException(e);
		}
	}

	public void load(File pFileName) {
		try {
			mImage = ImageIO.read(new FileInputStream(pFileName));
			mTilesPresent = false;
			mImageCreated = true;
		} catch (IOException e) {
			freemind.main.Resources.getInstance().logException(e);
		}
	}
	
	public String save() {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			ImageIO.write(mImage, "png", stream);
			stream.close();
			return Tools.toBase64(stream.toByteArray());
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
		}
		return null;

	}

	/**
	 * @return
	 */
	public RenderedImage getImage() {
		return mImage;
	}

	/**
	 * @param pDimension
	 * @param pX
	 * @param pY
	 * @param pZoom
	 * @param mTileController
	 * @param pLogger
	 * @param pDy
	 * @param pDx
	 */
	public void setTiles(int pDimension, int pX, int pY, int pZoom,
			TileController mTileController, Logger pLogger, int pDx, int pDy) {
		mDx = pDx;
		mDy = pDy;
		mTiles = new Tile[pDimension][pDimension];
		for (int i = 0; i < pDimension; ++i) {
			for (int j = 0; j < pDimension; ++j) {
				pLogger.fine("Trying to load tile to x=" + (pX + i)
						+ ", y=" + (pY + j) + ", zoom=" + pZoom);
				mTiles[i][j] = mTileController.getTile(pX + i, pY + j,
						pZoom);
			}
		}
		mTilesPresent = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int,
	 * int, int, int, int)
	 */
	public boolean imageUpdate(Image pImg, int pInfoflags, int pX, int pY,
			int pWidth, int pHeight) {
		mWaitingForCallbacks--;
		if (isDrawingDone()) {
			drawCross();
		}
		return isDrawingDone();
	}

	/**
	 * @return
	 */
	public boolean hasErrors() {
		if (!mTilesPresent)
			return false;
		for (int i = 0; i < mTiles.length; i++) {
			Tile[] tiles = mTiles[i];
			for (int j = 0; j < tiles.length; j++) {
				Tile tile = tiles[j];
				if (tile.hasError()) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isImageCreated() {
		return mImageCreated;
	}

}