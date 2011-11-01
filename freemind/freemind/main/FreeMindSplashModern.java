/***************************************************************************
 *
 *   FreeMindSplash, taken from GanttSplash.java.
 *
 *   Copyright (C) 2002 by Thomas Alexandre (alexthomas(at)ganttproject.org)
 *   Copyright (C) 2005-2008 by Christian Foltin and Daniel Polansky
 *    
 ***************************************************************************/

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

package freemind.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

/**
 * Class to put a splash during launching the application.
 */

public class FreeMindSplashModern extends JFrame implements IFreeMindSplash {

	private static final int SPLASH_FONT_SIZE = 16;

	private class FeedBackImpl implements FeedBack {

		private int mActualValue;
		private long mActualTimeStamp = System.currentTimeMillis();
		private long mTotalTime = 0;
		private String lastTaskId = null;
		private JLabel mImageJLabel = null;

		public void progress(final int act, String messageId) {
			final String progressString = frame.getResourceString(messageId);
			logger.info(progressString);
			this.mActualValue = act;
			long timeDifference = System.currentTimeMillis() - mActualTimeStamp;
			mActualTimeStamp = System.currentTimeMillis();
			mTotalTime += timeDifference;
			logger.info("Task: " + lastTaskId + " (" + act + ") last "
					+ (timeDifference) / 1000.0 + " seconds.\nTotal: "
					+ mTotalTime / 1000.0 + "\n");
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					mProgressBar.setValue(act);
					double percent = act * 1.0 / mProgressBar.getMaximum();
					mProgressBar.setString(progressString);
					if (mImageJLabel != null) {
						mImageJLabel.putClientProperty("progressString",
								progressString);
						mImageJLabel.putClientProperty("progressPercent",
								new Double(percent));
						mImageJLabel.repaint();
					}
				}
			});
			logger.info("Beginnig task:" + messageId);
			lastTaskId = messageId;
			// make it the top most window.
			FreeMindSplashModern.this.toFront();
		}

		public int getActualValue() {
			return mActualValue;
		}

		public void setMaximumValue(int max) {
			mProgressBar.setMaximum(max);
			mProgressBar.setIndeterminate(false);
		}

		public void increase(String messageId) {
			progress(getActualValue() + 1, messageId);
		}

		public void setImageJLabel(JLabel imageJLabel) {
			mImageJLabel = imageJLabel;
		}
	}

	private final FreeMindMain frame;
	private final FeedBackImpl feedBack; // !
	private JProgressBar mProgressBar;
	private static Logger logger;
	private ImageIcon mIcon;

	public FeedBack getFeedBack() {
		return feedBack;
	}

	public FreeMindSplashModern(final FreeMindMain frame) {
		super("FreeMind");
		this.frame = frame;
		if (logger == null) {
			logger = frame.getLogger(this.getClass().getName());
		}

		this.feedBack = new FeedBackImpl();

		// http://www.kde-look.org/content/show.php?content=76812
		// License GPLV2+
		mIcon = new ImageIcon(
				frame.getResource("images/76812-freemind_v0.4.png"));
		setIconImage(mIcon.getImage()); // Set the icon
		setDefaultLookAndFeelDecorated(false);
		setUndecorated(true);
		getRootPane().setWindowDecorationStyle(JRootPane.NONE); // Set no border
		// lamentablemente since 1.5: setAlwaysOnTop(true);

		ImageIcon splashImage = new ImageIcon(
				frame.getResource("images/Freemind_Splash_Butterfly_Modern.png"));
		JLabel splashImageLabel = new JLabel(splashImage) {
			private Integer mWidth = null;
			private final Font progressFont = new Font("SansSerif", Font.PLAIN,
					10);
			private Font versionTextFont = null;
			{
				Set availableFontFamilyNames = Tools
						.getAvailableFontFamilyNames();
				versionTextFont = availableFontFamilyNames
						.contains("Century Gothic") ? new Font(
						"Century Gothic", Font.BOLD, 14) : new Font("Arial",
						Font.BOLD, 12);
			}

			public void paint(Graphics g) {
				super.paint(g);
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2.setFont(versionTextFont);
				// Determine width of string to center it
				String freemindVersion = frame.getFreemindVersion().toString();
				if (mWidth == null) {
					mWidth = new Integer(g2.getFontMetrics().stringWidth(
							freemindVersion));
				}
				int yCoordinate = 58;
				int xCoordinate = (int) (getSize().getWidth() / 2 - mWidth
						.intValue() / 2);
				g2.setColor(new Color(0x4d, 0x63, 0xb4));
				g2.drawString(freemindVersion, xCoordinate, yCoordinate);
				// Draw progress bar
				String progressString = (String) getClientProperty("progressString");
				if (progressString != null) {
					Double percent = (Double) getClientProperty("progressPercent");
					int xBase = 7;
					int yBase = 185;
					int width = 281;
					g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
							RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
					g2.setFont(progressFont);
					// g2.setColor(new Color(0x80,0x80,0x80));
					g2.setColor(new Color(0xff, 0xff, 0xff));
					g2.drawString(progressString, xBase + 1, yBase - 4);
					g2.setColor(new Color(0xc8, 0xdf, 0x8b));
					g2.draw(new Rectangle(xBase + 2, yBase, width, 3));
					// g2.setColor(new Color(0xd0,0xd0,0xd0));
					// g2.draw(new Rectangle(xBase+1, yBase+1, width, 2));
					// g2.setColor(new Color(0xf4,0xf4,0xf4));
					// g2.fill(new Rectangle(xBase+1, yBase+1, width-1, 2));
					// g2.setColor(new Color(0x4d,0x63,0xb4));
					g2.setColor(new Color(0xff, 0xff, 0xff));
					g2.fill(new Rectangle(xBase + 1, yBase + 1,
							(int) (width * percent.doubleValue()), 2));
				}
			}
		};

		feedBack.setImageJLabel(splashImageLabel);

		getContentPane().add(splashImageLabel, BorderLayout.CENTER);

		mProgressBar = new JProgressBar();
		mProgressBar.setIndeterminate(true);
		mProgressBar.setStringPainted(true);

		pack();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension labelSize = splashImageLabel.getPreferredSize();

		// Put image at the middle of the screen
		setLocation(screenSize.width / 2 - (labelSize.width / 2),
				screenSize.height / 2 - (labelSize.height / 2));

	}

	public void close() {
		setVisible(false);
		dispose();
	}

	public ImageIcon getWindowIcon() {
		return mIcon;
	}

}