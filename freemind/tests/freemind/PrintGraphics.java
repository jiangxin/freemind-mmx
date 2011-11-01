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

package tests.freemind;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

/**
 * @author foltin
 * @date 05.09.2011
 */
public class PrintGraphics extends Graphics2D {

	private final Graphics2D mG;

	/**
	 * @param pG
	 */
	public PrintGraphics(Graphics pG) {
		mG = (Graphics2D) pG;
		// TODO Auto-generated constructor stub
	}

	public Graphics create() {
		return new PrintGraphics(mG.create());
	}

	public Graphics create(int pX, int pY, int pWidth, int pHeight) {
		return new PrintGraphics(mG.create(pX, pY, pWidth, pHeight));
	}

	public void drawRoundRect(int pX, int pY, int pWidth, int pHeight,
			int pArcWidth, int pArcHeight) {
		logMe("drawRoundRect", pX, pY, pWidth, pHeight);
		mG.drawRoundRect(pX, pY, pWidth, pHeight, pArcWidth, pArcHeight);
	}

	protected void logMe(String title, int pX, int pY, int pWidth, int pHeight) {
		System.out.println(title + ", " + pX + ", " + pY + ", " + pWidth + ", "
				+ pHeight);
	}

	public void drawOval(int pX, int pY, int pWidth, int pHeight) {
		logMe("drawOval", pX, pY, pWidth, pHeight);

		mG.drawOval(pX, pY, pWidth, pHeight);
	}

	public int hashCode() {
		return mG.hashCode();
	}

	public boolean equals(Object pObj) {
		return mG.equals(pObj);
	}

	public Color getColor() {
		return mG.getColor();
	}

	public void setColor(Color pC) {
		System.out.println("Color: " + pC);
		mG.setColor(pC);
	}

	public void setPaintMode() {
		mG.setPaintMode();
	}

	public void setXORMode(Color pC1) {
		mG.setXORMode(pC1);
	}

	public Font getFont() {
		return mG.getFont();
	}

	public void setFont(Font pFont) {
		mG.setFont(pFont);
	}

	public FontMetrics getFontMetrics() {
		return mG.getFontMetrics();
	}

	public FontMetrics getFontMetrics(Font pF) {
		return mG.getFontMetrics(pF);
	}

	public Rectangle getClipBounds() {
		return mG.getClipBounds();
	}

	public void clipRect(int pX, int pY, int pWidth, int pHeight) {
		mG.clipRect(pX, pY, pWidth, pHeight);
	}

	public void setClip(int pX, int pY, int pWidth, int pHeight) {
		mG.setClip(pX, pY, pWidth, pHeight);
	}

	public Shape getClip() {
		return mG.getClip();
	}

	public void setClip(Shape pClip) {
		mG.setClip(pClip);
	}

	public void copyArea(int pX, int pY, int pWidth, int pHeight, int pDx,
			int pDy) {
		mG.copyArea(pX, pY, pWidth, pHeight, pDx, pDy);
	}

	public void drawLine(int pX1, int pY1, int pX2, int pY2) {
		mG.drawLine(pX1, pY1, pX2, pY2);
	}

	public void fillRect(int pX, int pY, int pWidth, int pHeight) {
		mG.fillRect(pX, pY, pWidth, pHeight);
	}

	public void drawRect(int pX, int pY, int pWidth, int pHeight) {
		mG.drawRect(pX, pY, pWidth, pHeight);
	}

	public void draw3DRect(int pX, int pY, int pWidth, int pHeight,
			boolean pRaised) {
		mG.draw3DRect(pX, pY, pWidth, pHeight, pRaised);
	}

	public void clearRect(int pX, int pY, int pWidth, int pHeight) {
		mG.clearRect(pX, pY, pWidth, pHeight);
	}

	public void fill3DRect(int pX, int pY, int pWidth, int pHeight,
			boolean pRaised) {
		mG.fill3DRect(pX, pY, pWidth, pHeight, pRaised);
	}

	public void fillRoundRect(int pX, int pY, int pWidth, int pHeight,
			int pArcWidth, int pArcHeight) {
		mG.fillRoundRect(pX, pY, pWidth, pHeight, pArcWidth, pArcHeight);
	}

	public void draw(Shape pS) {
		mG.draw(pS);
	}

	public boolean drawImage(Image pImg, AffineTransform pXform,
			ImageObserver pObs) {
		return mG.drawImage(pImg, pXform, pObs);
	}

	public void drawImage(BufferedImage pImg, BufferedImageOp pOp, int pX,
			int pY) {
		mG.drawImage(pImg, pOp, pX, pY);
	}

	public void drawRenderedImage(RenderedImage pImg, AffineTransform pXform) {
		mG.drawRenderedImage(pImg, pXform);
	}

	public void fillOval(int pX, int pY, int pWidth, int pHeight) {
		mG.fillOval(pX, pY, pWidth, pHeight);
	}

	public void drawArc(int pX, int pY, int pWidth, int pHeight,
			int pStartAngle, int pArcAngle) {
		mG.drawArc(pX, pY, pWidth, pHeight, pStartAngle, pArcAngle);
	}

	public void drawRenderableImage(RenderableImage pImg, AffineTransform pXform) {
		mG.drawRenderableImage(pImg, pXform);
	}

	public void drawString(String pStr, int pX, int pY) {
		mG.drawString(pStr, pX, pY);
	}

	public void fillArc(int pX, int pY, int pWidth, int pHeight,
			int pStartAngle, int pArcAngle) {
		mG.fillArc(pX, pY, pWidth, pHeight, pStartAngle, pArcAngle);
	}

	public void drawString(String pStr, float pX, float pY) {
		mG.drawString(pStr, pX, pY);
	}

	public void drawPolyline(int[] pXPoints, int[] pYPoints, int pNPoints) {
		mG.drawPolyline(pXPoints, pYPoints, pNPoints);
	}

	public void drawString(AttributedCharacterIterator pIterator, int pX, int pY) {
		mG.drawString(pIterator, pX, pY);
	}

	public void drawPolygon(int[] pXPoints, int[] pYPoints, int pNPoints) {
		mG.drawPolygon(pXPoints, pYPoints, pNPoints);
	}

	public void drawString(AttributedCharacterIterator pIterator, float pX,
			float pY) {
		mG.drawString(pIterator, pX, pY);
	}

	public void drawPolygon(Polygon pP) {
		mG.drawPolygon(pP);
	}

	public void fillPolygon(int[] pXPoints, int[] pYPoints, int pNPoints) {
		mG.fillPolygon(pXPoints, pYPoints, pNPoints);
	}

	public void drawGlyphVector(GlyphVector pG, float pX, float pY) {
		mG.drawGlyphVector(pG, pX, pY);
	}

	public void fillPolygon(Polygon pP) {
		mG.fillPolygon(pP);
	}

	public void fill(Shape pS) {
		mG.fill(pS);
	}

	public boolean hit(Rectangle pRect, Shape pS, boolean pOnStroke) {
		return mG.hit(pRect, pS, pOnStroke);
	}

	public void drawChars(char[] pData, int pOffset, int pLength, int pX, int pY) {
		mG.drawChars(pData, pOffset, pLength, pX, pY);
	}

	public GraphicsConfiguration getDeviceConfiguration() {
		return mG.getDeviceConfiguration();
	}

	public void setComposite(Composite pComp) {
		mG.setComposite(pComp);
	}

	public void drawBytes(byte[] pData, int pOffset, int pLength, int pX, int pY) {
		mG.drawBytes(pData, pOffset, pLength, pX, pY);
	}

	public void setPaint(Paint pPaint) {
		mG.setPaint(pPaint);
	}

	public boolean drawImage(Image pImg, int pX, int pY, ImageObserver pObserver) {
		return mG.drawImage(pImg, pX, pY, pObserver);
	}

	public void setStroke(Stroke pS) {
		mG.setStroke(pS);
	}

	public void setRenderingHint(Key pHintKey, Object pHintValue) {
		mG.setRenderingHint(pHintKey, pHintValue);
	}

	public Object getRenderingHint(Key pHintKey) {
		return mG.getRenderingHint(pHintKey);
	}

	public boolean drawImage(Image pImg, int pX, int pY, int pWidth,
			int pHeight, ImageObserver pObserver) {
		return mG.drawImage(pImg, pX, pY, pWidth, pHeight, pObserver);
	}

	public void setRenderingHints(Map pHints) {
		mG.setRenderingHints(pHints);
	}

	public void addRenderingHints(Map pHints) {
		mG.addRenderingHints(pHints);
	}

	public RenderingHints getRenderingHints() {
		return mG.getRenderingHints();
	}

	public boolean drawImage(Image pImg, int pX, int pY, Color pBgcolor,
			ImageObserver pObserver) {
		return mG.drawImage(pImg, pX, pY, pBgcolor, pObserver);
	}

	public void translate(int pX, int pY) {
		mG.translate(pX, pY);
	}

	public void translate(double pTx, double pTy) {
		mG.translate(pTx, pTy);
	}

	public void rotate(double pTheta) {
		mG.rotate(pTheta);
	}

	public boolean drawImage(Image pImg, int pX, int pY, int pWidth,
			int pHeight, Color pBgcolor, ImageObserver pObserver) {
		return mG.drawImage(pImg, pX, pY, pWidth, pHeight, pBgcolor, pObserver);
	}

	public void rotate(double pTheta, double pX, double pY) {
		mG.rotate(pTheta, pX, pY);
	}

	public void scale(double pSx, double pSy) {
		mG.scale(pSx, pSy);
	}

	public void shear(double pShx, double pShy) {
		mG.shear(pShx, pShy);
	}

	public boolean drawImage(Image pImg, int pDx1, int pDy1, int pDx2,
			int pDy2, int pSx1, int pSy1, int pSx2, int pSy2,
			ImageObserver pObserver) {
		return mG.drawImage(pImg, pDx1, pDy1, pDx2, pDy2, pSx1, pSy1, pSx2,
				pSy2, pObserver);
	}

	public void transform(AffineTransform Tx) {
		mG.transform(Tx);
	}

	public void setTransform(AffineTransform Tx) {
		mG.setTransform(Tx);
	}

	public AffineTransform getTransform() {
		return mG.getTransform();
	}

	public boolean drawImage(Image pImg, int pDx1, int pDy1, int pDx2,
			int pDy2, int pSx1, int pSy1, int pSx2, int pSy2, Color pBgcolor,
			ImageObserver pObserver) {
		return mG.drawImage(pImg, pDx1, pDy1, pDx2, pDy2, pSx1, pSy1, pSx2,
				pSy2, pBgcolor, pObserver);
	}

	public Paint getPaint() {
		return mG.getPaint();
	}

	public Composite getComposite() {
		return mG.getComposite();
	}

	public void setBackground(Color pColor) {
		mG.setBackground(pColor);
	}

	public Color getBackground() {
		return mG.getBackground();
	}

	public Stroke getStroke() {
		return mG.getStroke();
	}

	public void clip(Shape pS) {
		mG.clip(pS);
	}

	public FontRenderContext getFontRenderContext() {
		return mG.getFontRenderContext();
	}

	public void dispose() {
		mG.dispose();
	}

	public void finalize() {
		mG.finalize();
	}

	public String toString() {
		return mG.toString();
	}

	public Rectangle getClipRect() {
		return mG.getClipRect();
	}

	public boolean hitClip(int pX, int pY, int pWidth, int pHeight) {
		return mG.hitClip(pX, pY, pWidth, pHeight);
	}

	public Rectangle getClipBounds(Rectangle pR) {
		return mG.getClipBounds(pR);
	}

}
