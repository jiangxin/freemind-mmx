/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
/*
 * Created on 15.11.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.modes.mindmapmode;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JScrollPane;

/**
 * @author Dimitri Polivaev 15.11.2005
 */
public class JAutoScrollBarPane extends JScrollPane {

	/**
     */
	public JAutoScrollBarPane(Component view) {
		super(view, VERTICAL_SCROLLBAR_NEVER, HORIZONTAL_SCROLLBAR_NEVER);
	}

	public void doLayout() {
		super.doLayout();
		Insets insets = getInsets();
		int insetHeight = insets.top + insets.bottom;
		Dimension prefSize = getViewport().getPreferredSize();
		int height = getHeight() - insetHeight;
		if (getHorizontalScrollBar().isVisible()) {
			height -= getHorizontalScrollBar().getHeight();
		}
		boolean isVsbNeeded = height < prefSize.height;
		boolean layoutAgain = false;

		if (isVsbNeeded
				&& getVerticalScrollBarPolicy() == VERTICAL_SCROLLBAR_NEVER) {
			setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
			layoutAgain = true;
		} else if (!isVsbNeeded
				&& getVerticalScrollBarPolicy() == VERTICAL_SCROLLBAR_ALWAYS) {
			setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_NEVER);
			layoutAgain = true;
		}

		if (layoutAgain) {
			super.doLayout();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
		if (!isValid()) {
			doLayout();
		}
		return super.getPreferredSize();
	}
}
