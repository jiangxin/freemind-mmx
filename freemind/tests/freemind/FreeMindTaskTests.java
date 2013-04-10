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

package tests.freemind;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;

import freemind.common.FreeMindTask;

/**
 * @author foltin
 * @date 08.04.2013
 */
public class FreeMindTaskTests extends FreeMindTestBase {

	/* (non-Javadoc)
	 * @see tests.freemind.FreeMindTestBase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	private class TestTask extends FreeMindTask {

		/**
		 * 
		 */
		private static final int AMOUNT_OF_TIME = 10;

		/**
		 * @param pFrame 
		 * @param pAmountOfSteps
		 */
		public TestTask(JFrame pFrame) {
			super(pFrame, AMOUNT_OF_TIME, "TestTask");
		}

		private int i = AMOUNT_OF_TIME;
		
		/* (non-Javadoc)
		 * @see freemind.common.FreeMindTask#processAction()
		 */
		protected boolean processAction() throws Exception {
			Thread.sleep(200);
			i--;
			mProgressDescription = new ProgressDescription("Format {0}", new Object[] {new Integer(i)});
			return true;
		}
		
	}

	/**
	 * @throws InterruptedException 
	 * @throws InvocationTargetException 
	 * 
	 */
	public void testTestTask() throws InterruptedException, InvocationTargetException {
		JFrame frame = new JFrame("Hi");
		final TestTask task = new TestTask(frame);
		JButton button = new JButton("Hello from FreeMind");
		frame.add(button, BorderLayout.CENTER);
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent pE) {
				task.i = -100;
			}});
		frame.setSize(500, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		// the task is called by the event queue.
		EventQueue.invokeAndWait(new Runnable() {
			public void run() {
				task.start();
			}
		});
		while (!task.isFinished()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				freemind.main.Resources.getInstance().logException(e);
			}
		}
		assertEquals(0, task.i);
	}
	
}
