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

package plugins.collaboration.socket;


/**
 * Thread with termination methods.
 * @author foltin
 * @date 05.09.2012
 */
public abstract class TerminateableThread extends Thread {

	protected boolean mShouldTerminate = false;
	protected boolean mIsTerminated = false;
	protected static java.util.logging.Logger logger = null;
	protected int mSleepTime;

	/**
	 * 
	 */
	public TerminateableThread(String pName) {
		super(pName);
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
		mSleepTime = 1000;
	}

	public void run() {
		while (!mShouldTerminate) {
			boolean shouldBeCalledDirectlyAgain = false;
			try {
				shouldBeCalledDirectlyAgain = processAction();
			} catch (Exception e) {
				freemind.main.Resources.getInstance().logException(e);
			}
			if(!shouldBeCalledDirectlyAgain) {
				try {
					Thread.sleep(mSleepTime);
				} catch (InterruptedException e) {
					freemind.main.Resources.getInstance().logException(e);
					
				}
			}
		}
		mIsTerminated = true;
	}

	/**
	 * Method that does the work in this thread.
	 * Must return every second, to be able to terminate thread.
	 * @return true, if the method wants to be called directly again. Otherwise sleep is carried out.
	 * @throws Exception
	 */
	public abstract boolean processAction() throws Exception;

	public void commitSuicide() {
		mShouldTerminate = true;
		int timeout = 10;
		logger.info("Shutting down thread " + getName() + ".");
		while (!mIsTerminated && timeout-- > 0) {
			try {
				Thread.sleep(mSleepTime);
			} catch (InterruptedException e) {
				freemind.main.Resources.getInstance().logException(e);
			}
		}
		if (timeout == 0) {
			logger.warning("Can't stop thread " + getName() + "!");
		} else {
			logger.info("Shutting down thread " + getName() + " complete.");
		}
	}

}