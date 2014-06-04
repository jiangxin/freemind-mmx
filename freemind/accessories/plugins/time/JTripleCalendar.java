/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2007  Christian Foltin <christianfoltin@users.sourceforge.net>
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
/*$Id: JTripleCalendar.java,v 1.1.2.2 2007/02/25 21:12:50 christianfoltin Exp $*/

package accessories.plugins.time;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

import tests.freemind.FreeMindMainMock;
import freemind.main.Resources;
import freemind.main.Tools;

/** 
 * Formerly, it has only three calendar widgets at once.
 * Now, it has 9 in total, but we keep the naming.
 * */
public class JTripleCalendar extends JPanel implements PropertyChangeListener {

	private static final int AMOUNT_OF_ROWS = 3;
	private static final int AMOUNT_OF_COLUMNS = 4;
	private JCalendar mCalendarWidget;
	/** Contains a mapping info panel -> month distance of the panel to upper left corner.*/
	private HashMap<JInfoPanel, Integer> mInfoPanels = new HashMap<JTripleCalendar.JInfoPanel, Integer>();
	private int mCurrentMonthPosition;
	private JInfoPanel mCurrentlyHiddenPanel;
	private GridBagLayout mGridLayout;

	public JTripleCalendar(int pCurrentMonthPosition) {
		mCurrentMonthPosition = pCurrentMonthPosition;
		this.setName("JTripleCalendar");
		mGridLayout = new GridBagLayout();
//		gridLayout.setHgap(50);
		setLayout(mGridLayout);
		mCalendarWidget = new JCalendar();
		mCalendarWidget.addPropertyChangeListener(this);
		int monthIndex = 0;
		for(int column=0; column < AMOUNT_OF_COLUMNS; ++column) {
			for(int row=0; row < AMOUNT_OF_ROWS; ++row) {
				JInfoPanel infoPanel = createInfoPanel();
				infoPanel.getCalendarWidget().addPropertyChangeListener(this);
				mInfoPanels.put(infoPanel, monthIndex);
				GridBagConstraints constraints = getConstraints(row, column);
				if (monthIndex == pCurrentMonthPosition) {
					add(mCalendarWidget, constraints);
					mCurrentlyHiddenPanel = infoPanel;
				} else {
					add(infoPanel, constraints);
				}
				monthIndex++;
			}
		}
	}

	protected GridBagConstraints getConstraints(int row, int column) {
		GridBagConstraints constraints = new GridBagConstraints(column, row, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 50, 10);
		return constraints;
	}

	private static class JInfoPanel extends JPanel {
		// private JLabel monthYearLabel;
		private JDayChooser dayChooser;
		private JMonthChooser monthChooser;
		private JYearChooser yearChooser;

		public JInfoPanel() {
			this.setLayout(new BorderLayout());

			JPanel monthYearPanel = new JPanel();
			monthYearPanel.setLayout(new BorderLayout());

			// monthYearLabel = new JLabel("MM.YYYY");
			// monthYearPanel.add(monthYearLabel, BorderLayout.CENTER);
			// monthYearPanel.setBorder(BorderFactory.createEmptyBorder());
			monthChooser = new JMonthChooser();
			monthChooser.setEnabled(false);
			yearChooser = new JYearChooser();
			yearChooser.setEnabled(false);
			monthYearPanel.add(monthChooser, BorderLayout.WEST);
			monthYearPanel.add(yearChooser, BorderLayout.CENTER);

			dayChooser = new JDayChooser(true) {
				protected void init() {
					super.init();
					// no color selection
					selectedColor = oldDayBackgroundColor;
				}

				public void addListeners(int index) {
					days[index].addActionListener(this);
					days[index].setFocusable(false);
				}
			};
			dayChooser.setEnabled(true);
			/**
			 * This is needed as sometimes the current selected date is equal to
			 * the one, the user presses. Thus, without this statement, no
			 * property change event is issued.
			 */
			dayChooser.setAlwaysFireDayProperty(true);
			this.add(monthYearPanel, BorderLayout.NORTH);
			this.add(dayChooser, BorderLayout.CENTER);
		}

		public void setDate(Calendar calendar) {
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			monthChooser.setMonth(month);
			yearChooser.setYear(year);
			// monthYearLabel.setText(toMonthYearLabelString(calendar));
			dayChooser.setYear(year);
			dayChooser.setMonth(month);
			// this is not necessary, I think.
			dayChooser.setEnabled(true);
		}

		/**
		 * Returns the calendar property.
		 * 
		 * @return the value of the calendar property.
		 */
		public Calendar getCalendar() {
			return dayChooser.calendar;
		}

		public JDayChooser getCalendarWidget() {
			return dayChooser;
		}
	}

	private JInfoPanel createInfoPanel() {
		JInfoPanel panel = new JInfoPanel();
		return panel;
	}

	public static void main(String[] args) {
		Resources.createInstance(new FreeMindMainMock());
		final JFrame frame = new JFrame("JTripleCalendar");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		final JTripleCalendar jcalendar = new JTripleCalendar(4);
		frame.getContentPane().add(jcalendar);
		frame.pack();
		// focus fix after startup.
		frame.addWindowFocusListener(new WindowAdapter() {

			public void windowGainedFocus(WindowEvent e) {
				jcalendar.getDayChooser().getSelectedDay().requestFocus();
				frame.removeWindowFocusListener(this);
			}
		});

		frame.setVisible(true);

	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == mCalendarWidget) {
			// on the calendar itself, there was only a different day clicked
			Calendar gregorianCalendar = (Calendar) mCalendarWidget
					.getCalendar().clone();
			propagateDate(gregorianCalendar);
		} else {
			if (Tools.safeEquals(evt.getPropertyName(),
					JDayChooser.DAY_PROPERTY)) {
				for(JInfoPanel infoPanel : mInfoPanels.keySet()) {
					if (evt.getSource() == infoPanel.getCalendarWidget()) {
						Calendar gregorianCalendar = (Calendar) infoPanel.getCalendar()
								.clone();
						gregorianCalendar.set(Calendar.DAY_OF_MONTH,
								((Integer) evt.getNewValue()).intValue());
						// exchange infoPanel and calendarWidget:
						GridBagConstraints currentConstraints = mGridLayout.getConstraints(mCalendarWidget);
						remove(mCalendarWidget);
						add(mCurrentlyHiddenPanel, currentConstraints);
						currentConstraints = mGridLayout.getConstraints(infoPanel);
						remove(infoPanel);
						mCurrentlyHiddenPanel = infoPanel;
						mCurrentMonthPosition = mInfoPanels.get(infoPanel);
						add(mCalendarWidget, currentConstraints);
						mCalendarWidget.setCalendar(gregorianCalendar);
						propagateDate(gregorianCalendar);
						validate();
						break;
					}
				}
			}
		}
	}

	public void propagateDate(Calendar gregorianCalendar) {
		for(JInfoPanel infoPanel : mInfoPanels.keySet()) {
			Integer monthDistance = mInfoPanels.get(infoPanel);
			gregorianCalendar.add(Calendar.MONTH, -mCurrentMonthPosition + monthDistance);
			infoPanel.setDate(gregorianCalendar);
			gregorianCalendar.add(Calendar.MONTH, mCurrentMonthPosition -monthDistance);
		}
	}

	public Calendar getCalendar() {
		return mCalendarWidget.getCalendar();
	}

	public Date getDate() {
		return mCalendarWidget.getDate();
	}

	public JDayChooser getDayChooser() {
		return mCalendarWidget.getDayChooser();
	}

	public void setDate(Date date) {
		mCalendarWidget.setDate(date);
	}

	public void setCalendar(Calendar c) {
		mCalendarWidget.setCalendar(c);
	}

	public JYearChooser getYearChooser() {
		return mCalendarWidget.getYearChooser();
	}
}

// private static java.util.logging.Logger logger =
// freemind.main.Resources.getInstance().getLogger(JTripleCalendar.class.getName());
