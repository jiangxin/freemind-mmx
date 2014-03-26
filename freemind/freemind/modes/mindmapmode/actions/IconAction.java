/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2004  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 *
 * Created on 29.09.2004
 */


package freemind.modes.mindmapmode.actions;

import java.awt.event.ActionEvent;
import java.util.ListIterator;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import freemind.controller.actions.generated.instance.AddIconAction;
import freemind.main.Tools;
import freemind.modes.IconInformation;
import freemind.modes.MindIcon;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapNodeModel;
import freemind.modes.mindmapmode.actions.xml.actors.AddIconActor;

public class IconAction extends MindmapAction implements IconInformation {
	public MindIcon icon;
	private final MindMapController modeController;
	private final RemoveIconAction removeLastIconAction;

	public IconAction(MindMapController controller, MindIcon _icon,
			RemoveIconAction removeLastIconAction) {
		super(_icon.getDescription(), _icon.getIcon(), controller);
		this.modeController = controller;
		this.removeLastIconAction = removeLastIconAction;
		putValue(Action.SHORT_DESCRIPTION, _icon.getDescription());
		this.icon = _icon;
		
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getID() == ActionEvent.ACTION_FIRST
				&& (e.getModifiers() & ActionEvent.SHIFT_MASK
						& ~ActionEvent.CTRL_MASK & ~ActionEvent.ALT_MASK) != 0) {
			removeAllIcons();
			addLastIcon();
			return;
		}
		if (e == null
				|| (e.getModifiers() & (ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK)) == 0) {
			addLastIcon();
			return;
		}
		// e != null
		if ((e.getModifiers() & ~ActionEvent.SHIFT_MASK
				& ~ActionEvent.CTRL_MASK & ActionEvent.ALT_MASK) != 0) {
			removeIcon(false);
			return;
		}
		if ((e.getModifiers() & ~ActionEvent.SHIFT_MASK & ActionEvent.CTRL_MASK & ~ActionEvent.ALT_MASK) != 0) {
			removeIcon(true);
			return;
		}
	}

	private void addLastIcon() {
		for (ListIterator it = modeController.getSelecteds().listIterator(); it
				.hasNext();) {
			MindMapNodeModel selected = (MindMapNodeModel) it.next();
			getAddIconActor().addIcon(selected, icon);
		}
	}

	private void removeIcon(boolean removeFirst) {
		for (ListIterator it = modeController.getSelecteds().listIterator(); it
				.hasNext();) {
			MindMapNodeModel selected = (MindMapNodeModel) it.next();
			getAddIconActor().removeIcon(selected, icon, removeFirst);
		}
	}

	private void toggleIcon() {
		for (ListIterator it = modeController.getSelecteds().listIterator(); it
				.hasNext();) {
			MindMapNodeModel selected = (MindMapNodeModel) it.next();
			getAddIconActor().toggleIcon(selected, icon);
		}
	}

	private void removeAllIcons() {
		for (ListIterator it = modeController.getSelecteds().listIterator(); it
				.hasNext();) {
			MindMapNodeModel selected = (MindMapNodeModel) it.next();
			if (selected.getIcons().size() > 0) {
				modeController.removeAllIcons(selected);
			}
		}
	}

	


	protected AddIconAction createAddIconAction(MindMapNode node,
			MindIcon icon, int iconIndex) {
		return getAddIconActor().createAddIconAction(node, icon, iconIndex);
	}

	protected AddIconActor getAddIconActor() {
		return getMindMapController().getActorFactory().getAddIconActor();
	}

	public Class getDoActionClass() {
		return AddIconAction.class;
	}
	

	public MindIcon getMindIcon() {
		return icon;
	}

	public KeyStroke getKeyStroke() {
		final String keystrokeResourceName = icon.getKeystrokeResourceName();
		final String keyStrokeDescription = getMindMapController().getFrame()
				.getAdjustableProperty(keystrokeResourceName);
		return Tools.getKeyStroke(keyStrokeDescription);
	}

	public String getDescription() {
		return icon.getDescription();
	}

	public ImageIcon getIcon() {
		return icon.getIcon();
	}

	public String getKeystrokeResourceName() {
		return icon.getKeystrokeResourceName();
	}

}
