/*
 * Created on 28.03.2004
 *
 */
package freemind.controller;

import java.awt.Insets;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 * @author Stefan Zechmeister
 */
public class FreeMindToolBar extends JToolBar {
	private static Insets nullInsets = new Insets(0,0,0,0);

	/**
	 * 
	 */
	public FreeMindToolBar() {
		this("", JToolBar.HORIZONTAL);
	}

	/**
	 * @param arg0
	 */
	public FreeMindToolBar(int arg0) {
		this("", arg0);
	}

	/**
	 * @param arg0
	 */
	public FreeMindToolBar(String arg0) {
		this(arg0, JToolBar.HORIZONTAL);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public FreeMindToolBar(String arg0, int arg1) {
		super(arg0, arg1);
		this.setMargin(nullInsets);

	}

	/* (non-Javadoc)
	 * @see javax.swing.JToolBar#add(javax.swing.Action)
	 */
	public JButton add(Action arg0) {
		arg0.putValue(Action.SHORT_DESCRIPTION, arg0.getValue(Action.NAME));
		JButton returnValue = super.add(arg0);
		returnValue.setText("");
		returnValue.setMargin(nullInsets);
		returnValue.setFocusable(false);

		// fc, 20.6.2004: try to make the toolbar looking good under Mac OS X.
		if (System.getProperty("os.name").startsWith("Mac OS")) {
			returnValue.setBorderPainted     (false);
		}
		returnValue.setContentAreaFilled (false);
		
		return returnValue;
	}

}
