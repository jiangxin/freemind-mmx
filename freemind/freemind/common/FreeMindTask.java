/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2013 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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

package freemind.common;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.RootPaneContainer;

/**
 * Long running tasks inside FreeMind should derive from this class.
 * 
 * @author foltin
 * @date 08.04.2013
 */
public abstract class FreeMindTask extends Thread {

	private static final long TIME_TO_DISPLAY_PROGRESS_BAR_IN_MILLIS = 1000;
	private boolean mInterrupted = false;
	private boolean mFinished = false;
	private int mAmountOfSteps;
	private FreeMindProgressMonitor mProgressMonitor = null;
	private int mRounds;
	protected ProgressDescription mProgressDescription;
	private RootPaneContainer mFrame;
	private JPanel mGlass;
	private Component mOldGlassPane;

	protected class ProgressDescription {
		/**
		 * @param pProgressString
		 * @param pProgressParameters
		 */
		public ProgressDescription(String pProgressString,
				Object[] pProgressParameters) {
			super();
			mProgressString = pProgressString;
			mProgressParameters = pProgressParameters;
		}

		public String mProgressString;
		/**
		 * To be inserted into mProgressString;
		 */
		public Object[] mProgressParameters;
	}

	public FreeMindTask(RootPaneContainer pRootPaneContainer, int pAmountOfSteps, String pName) {
		super(pName);
		mFrame = pRootPaneContainer;
		mAmountOfSteps = pAmountOfSteps;
		mProgressMonitor = new FreeMindProgressMonitor(getName());
		mGlass = new JPanel(new GridLayout(0, 1));
		JLabel padding = new JLabel();
		mGlass.setOpaque(false);
		mGlass.add(padding);

		// trap both mouse and key events. Could provide a smarter
		// key handler if you wanted to allow things like a keystroke
		// that would cancel the long-running operation.
		mGlass.addMouseListener(new MouseAdapter() {
		});
		mGlass.addMouseMotionListener(new MouseMotionAdapter() {
		});
		mGlass.addKeyListener(new KeyAdapter() {
		});

		// make sure the focus won't leave the glass pane
		mGlass.setFocusCycleRoot(true); // 1.4
		mOldGlassPane = pRootPaneContainer.getGlassPane();
		pRootPaneContainer.setGlassPane(mGlass);
		mGlass.setVisible(true);
		padding.requestFocus();  // required to trap key events
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		long startTime = System.currentTimeMillis();
		mRounds = 0;
		boolean again = true;
		while (again) {
			try {
				again = processAction();
				mRounds++;
				if (!again) {
					// already ready!!
					mRounds = mAmountOfSteps;
				}
				if (mRounds == mAmountOfSteps) {
					again = false;
				}
			} catch (Exception e) {
				freemind.main.Resources.getInstance().logException(e);
				again = false;
			}
			if (isInterrupted()) {
				again = false;
			}
			if (System.currentTimeMillis() - startTime > TIME_TO_DISPLAY_PROGRESS_BAR_IN_MILLIS) {
				// mProgressMonitor.setModal(true);
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						mProgressMonitor.setVisible(true);
					}});
			}
			if (mProgressMonitor.isVisible()) {
				ProgressDescription progressDescription = mProgressDescription;
				if (mProgressDescription == null) {
					progressDescription = new ProgressDescription(
							"FreeMindTask.Default", new Object[] { new Integer(
									mRounds) });
				}
				boolean canceled = mProgressMonitor.showProgress(mRounds,
						mAmountOfSteps, progressDescription.mProgressString,
						progressDescription.mProgressParameters);
				if (canceled) {
					mInterrupted = true;
					again = false;
				}
			}
		}
		setFinished(true);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				mGlass.setVisible(false);
				mFrame.setGlassPane(mOldGlassPane);
				mProgressMonitor.dismiss();
			}
		});
	}

	/**
	 * Subclasses should process one single action out of the set of its actions
	 * and then return. The method is directly called again by the task
	 * controller until it returns false.
	 * 
	 * @return true, if further actions follow. False, if done.
	 */
	protected abstract boolean processAction() throws Exception;

	public boolean isInterrupted() {
		return mInterrupted;
	}

	public void setInterrupted(boolean pInterrupted) {
		mInterrupted = pInterrupted;
	}

	public boolean isFinished() {
		return mFinished;
	}

	public void setFinished(boolean pFinished) {
		mFinished = pFinished;
	}

	public int getAmountOfSteps() {
		return mAmountOfSteps;
	}

	public void setAmountOfSteps(int pAmountOfSteps) {
		mAmountOfSteps = pAmountOfSteps;
	}

	public int getRounds() {
		return mRounds;
	}

}
