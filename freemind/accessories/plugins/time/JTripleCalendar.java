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
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** */
public class JTripleCalendar extends JPanel implements PropertyChangeListener {
    
    private JCalendar calendarWidget;
    private JLabel leftLabel;
    private JLabel rightLabel;
    private JInfoPanel leftPanel;
    private JInfoPanel rightPanel;

    public JTripleCalendar(){
        this.setName("JTripleCalendar");
        GridLayout gridLayout = new GridLayout(3,1);
        gridLayout.setHgap(50);
        setLayout(gridLayout);
        leftPanel = createInfoPanel();
        rightPanel = createInfoPanel();
        add(leftPanel);
        calendarWidget = new JCalendar();
        calendarWidget.addPropertyChangeListener(this);
        add(calendarWidget);
        add(rightPanel);
        
    }

    private static class JInfoPanel extends JPanel {
//        private JLabel monthYearLabel;
        private JDayChooser dayChooser;
        private JMonthChooser monthChooser;
        private JYearChooser yearChooser;

        public JInfoPanel() {
            this.setLayout(new BorderLayout());
            
            JPanel monthYearPanel = new JPanel();
            monthYearPanel.setLayout(new BorderLayout());
            
//            monthYearLabel = new JLabel("MM.YYYY");
//            monthYearPanel.add(monthYearLabel, BorderLayout.CENTER);
//            monthYearPanel.setBorder(BorderFactory.createEmptyBorder());
            monthChooser = new JMonthChooser();
            monthChooser.setEnabled(false);
            yearChooser = new JYearChooser();
            yearChooser.setEnabled(false);
            monthYearPanel.add(monthChooser, BorderLayout.WEST);
            monthYearPanel.add(yearChooser, BorderLayout.CENTER);
            
            dayChooser = new JDayChooser(true){
                            protected void init() {
                                super.init();
                                // no color selection
                                selectedColor = oldDayBackgroundColor;
                            }
                        };
            dayChooser.setEnabled(false);
            this.add(monthYearPanel, BorderLayout.NORTH);
            this.add(dayChooser, BorderLayout.CENTER);
        }
        
        public void setDate(Calendar calendar) {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            monthChooser.setMonth(month);
            yearChooser.setYear(year);
            //            monthYearLabel.setText(toMonthYearLabelString(calendar));
            dayChooser.setYear(year);
            dayChooser.setMonth(month);
            // it is enabled after setting some time.
            dayChooser.setEnabled(false);
        }
//        private String toMonthYearLabelString(Calendar calendar) {
//            return (calendar.get(Calendar.MONTH)+1)+"." + calendar.get(Calendar.YEAR);
//        }
    }
    
    private JInfoPanel createInfoPanel() {
        JInfoPanel panel = new JInfoPanel();
        return panel;
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("JTripleCalendar");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JTripleCalendar jcalendar = new JTripleCalendar();
        frame.getContentPane().add(jcalendar);
        frame.pack();
        frame.setVisible(true);

    }

    public void propertyChange(PropertyChangeEvent evt) {
            Calendar gregorianCalendar = (Calendar) calendarWidget.getCalendar().clone();
            gregorianCalendar.add(Calendar.MONTH, -1);
            leftPanel.setDate(gregorianCalendar);
            gregorianCalendar.add(Calendar.MONTH, 2);
            rightPanel.setDate(gregorianCalendar);

    }


   public Calendar getCalendar() {
        return calendarWidget.getCalendar();
    }

    public Date getDate() {
        return calendarWidget.getDate();
    }

    public JDayChooser getDayChooser() {
        return calendarWidget.getDayChooser();
    }

    public void setDate(Date date) {
        calendarWidget.setDate(date);
    }

    public void setCalendar(Calendar c) {
        calendarWidget.setCalendar(c);
    }

	public JYearChooser getYearChooser() {
		return calendarWidget.getYearChooser();
	}
}

// private static java.util.logging.Logger logger =
// freemind.main.Resources.getInstance().getLogger(JTripleCalendar.class.getName());
