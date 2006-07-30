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
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.text.NumberFormat;
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

public class FreeMindSplash extends JFrame {

	private static final int SPLASH_FONT_SIZE = 16;


    private class FeedBackImpl implements FeedBack {

		private int mActualValue;
		private long mActualTimeStamp=System.currentTimeMillis();
        private long mTotalTime = 0;
        private String lastTaskId=null;

		public void progress(final int act, String messageId) {
		    final String progressString = frame.getResourceString(messageId);
            logger.info(progressString);
			this.mActualValue = act;
			long timeDifference = System.currentTimeMillis()-mActualTimeStamp;
			mActualTimeStamp = System.currentTimeMillis();
			mTotalTime += timeDifference;
            logger.info("Task: "+lastTaskId + " (" + act+") last " + (timeDifference)/1000.0 + " seconds.\nTotal: "+mTotalTime/1000.0+"\n");
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					mProgressBar.setValue(act);
					double percent = act*1.0/mProgressBar.getMaximum();
					mProgressBar.setString(progressString);
//					mProgressBar.setString(NumberFormat.getPercentInstance().format(percent));
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
		
	}
	
    private final FreeMindMain frame;
	private final FeedBack feedBack;
	private JProgressBar mProgressBar;
    private static Logger logger;

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
    	
    	ImageIcon icon = new ImageIcon(frame.getResource(
			"images/FreeMindWindowIcon.png"));
    	setIconImage(icon.getImage());	//set the ganttproject icon
    	setDefaultLookAndFeelDecorated(false);
    	setUndecorated(true);
    	getRootPane().setWindowDecorationStyle(JRootPane.NONE); //set no border
        
    	ImageIcon splashImage = new ImageIcon(frame.getResource("images/splash.JPG"));
        JLabel l = new JLabel(splashImage) {
        	private Integer mWidth = null;

            public void paint (Graphics g) {
        		super.paint(g);
        		Graphics2D g2 = (Graphics2D) g;
        		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        		Font font = new Font("Arial", Font.BOLD, SPLASH_FONT_SIZE);
        		g2.setFont(font);
                // determine width of string to center it.
                String freemindVersion = frame.getFreemindVersion();
                if (mWidth == null) {
                    mWidth = new Integer(g2.getFontMetrics().stringWidth(
                            freemindVersion));
                }                
                int yCoordinate = (int)(getSize().getHeight())-14;
                int xCoordinate = (int)(getSize().getWidth()/2-mWidth.intValue()/2);
        		g2.setColor(Color.YELLOW);
                g2.drawString(freemindVersion, xCoordinate , yCoordinate);
        		g2.setColor(Color.WHITE);
        		g2.drawString(freemindVersion, xCoordinate+1 , yCoordinate+1);
        	}
        };
        
        
        getContentPane().add(l, BorderLayout.CENTER);
        mProgressBar = new JProgressBar();
        mProgressBar.setIndeterminate(true);
        mProgressBar.setStringPainted(true);


        getContentPane().add(mProgressBar, BorderLayout.SOUTH);
        pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = l.getPreferredSize();

        // Put image at the middle of the screen
        setLocation(screenSize.width/2 - (labelSize.width/2),
                    screenSize.height/2 - (labelSize.height/2));
		
    }
    

    public void close() {
          setVisible(false);
    	  dispose();
    }

}

