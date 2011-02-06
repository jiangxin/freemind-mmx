/***************************************************************************
                           FreeMindSplash, taken from GanttSplash.java  -  description
                             -------------------
    begin                : dec 2002
    copyright            : (C) 2002 by Thomas Alexandre
    email                : alexthomas(at)ganttproject.org
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
import java.text.NumberFormat;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;



/**
 * Class to put a splash before lunch the soft
 */

public class FreeMindSplash extends JFrame implements IFreeMindSplash {

	private static final int SPLASH_FONT_SIZE = 16;


	private class FeedBackImpl implements FeedBack {

        private int mActualValue;
        private long mActualTimeStamp=System.currentTimeMillis();
        private long mTotalTime = 0;
        private String lastTaskId=null;
        private JLabel mImageJLabel=null;

        public void progress(final int act, String messageId) {
            final String progressString = frame.getResourceString(messageId);
            logger.info(progressString);
            this.mActualValue = act;
            long timeDifference = System.currentTimeMillis()-mActualTimeStamp;
            mActualTimeStamp = System.currentTimeMillis();
            mTotalTime += timeDifference;
            logger.info("Task: "+lastTaskId + " (" + act+") last " + (timeDifference)/1000.0 +
                        " seconds.\nTotal: "+mTotalTime/1000.0+"\n");
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    mProgressBar.setValue(act);
                    double percent = act*1.0/mProgressBar.getMaximum();
                    mProgressBar.setString(progressString);
                    if (mImageJLabel!=null) {
                       mImageJLabel.putClientProperty("progressString",progressString);
                       mImageJLabel.putClientProperty("progressPercent",new Double(percent));
                       mImageJLabel.repaint(); }
                }
            });
            logger.info("Beginnig task:" + messageId);
            lastTaskId = messageId;
        }

        public int getActualValue() {
            return mActualValue;
        }

        public void setMaximumValue(int max) {
            mProgressBar.setMaximum(max);
            mProgressBar.setIndeterminate(false);
        }

        public void increase(String messageId) {
            progress(getActualValue()+1, messageId);
        }

        public void setImageJLabel(JLabel imageJLabel) {
            mImageJLabel = imageJLabel;
        }
        
    }
	
    private final FreeMindMain frame;
	private final FeedBackImpl feedBack;
	private JProgressBar mProgressBar;
    private static Logger logger;
    private ImageIcon mIcon;

	public FeedBack getFeedBack() {
		return feedBack;
	}
	

    public FreeMindSplash(final FreeMindMain frame){
    	super("FreeMind");
        this.frame = frame;
        if(logger == null) {
            logger = frame.getLogger(this.getClass().getName());
        }

		this.feedBack = new FeedBackImpl();
    	
    	mIcon = new ImageIcon(frame.getResource(
        			"images/FreeMindWindowIcon.png"));
        setIconImage(mIcon.getImage());	//set the ganttproject icon
    	setDefaultLookAndFeelDecorated(false);
    	setUndecorated(true);
    	getRootPane().setWindowDecorationStyle(JRootPane.NONE); //set no border
        
    	ImageIcon splashImage = new ImageIcon(frame.getResource("images/splash.JPG"));
                
        JLabel splashImageLabel = new JLabel(splashImage) {
            private Integer mWidth = null;
            private final Font progressFont = new Font("SansSerif", Font.PLAIN, 10);
            private Font versionTextFont = null;
            {  Set availableFontFamilyNames = Tools.getAvailableFontFamilyNames();
               versionTextFont = availableFontFamilyNames.contains("Century Gothic")
                  ? new Font("Century Gothic", Font.BOLD, 14)
                  : new Font("Arial", Font.BOLD, 12); 
            }
            public void paint (Graphics g) {
                super.paint(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(versionTextFont);
                // Determine width of string to center it
                String freemindVersion = frame.getFreemindVersion().toString();
                if (mWidth == null) {
                    mWidth = new Integer(g2.getFontMetrics().stringWidth(freemindVersion));
                }
                int yCoordinate = (int)(getSize().getHeight())-14;
                int xCoordinate = (int)(getSize().getWidth()-mWidth.intValue())-45;
                g2.setColor(new Color(0x4d,0x63,0xb4));
                g2.drawString(freemindVersion, xCoordinate , yCoordinate);
                // Draw progress bar
                String progressString = (String)getClientProperty("progressString");
                if (progressString!=null) {
                   Double percent = (Double)getClientProperty("progressPercent");
                   int xBase = 21;
                   int yBase = yCoordinate+7;
                   int width = (int) getSize().getWidth() - 2*xBase;
                   g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
                   g2.setFont(progressFont);
                   g2.setColor(new Color(0x80,0x80,0x80));
                   g2.drawString(progressString, xBase+1, yBase-4);
                   g2.setColor(new Color(0xf0,0xf0,0xf0));
                   g2.draw(new Rectangle(xBase+2, yBase, width, 3));
                   g2.setColor(new Color(0xd0,0xd0,0xd0));
                   g2.draw(new Rectangle(xBase+1, yBase+1, width, 2));
                   g2.setColor(new Color(0xf4,0xf4,0xf4));
                   g2.fill(new Rectangle(xBase+1, yBase+1, width-1, 2));
                   g2.setColor(new Color(0x4d,0x63,0xb4));
                   g2.fill(new Rectangle(xBase+1, yBase+1, (int)(width*percent.doubleValue()), 2));
                }
            }
        };
        feedBack.setImageJLabel(splashImageLabel);
        getContentPane().add(splashImageLabel, BorderLayout.CENTER);
//        getContentPane().add(l, BorderLayout.CENTER);
        mProgressBar = new JProgressBar();
        mProgressBar.setIndeterminate(true);
        mProgressBar.setStringPainted(true);


//        getContentPane().add(mProgressBar, BorderLayout.SOUTH);
        pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = splashImageLabel.getPreferredSize();

        // Put image at the middle of the screen
        setLocation(screenSize.width/2 - (labelSize.width/2),
                    screenSize.height/2 - (labelSize.height/2));
		
    }
    

    public void close() {
          setVisible(false);
    	  dispose();
    }

    public ImageIcon getWindowIcon() {
        return mIcon;
    }


}

