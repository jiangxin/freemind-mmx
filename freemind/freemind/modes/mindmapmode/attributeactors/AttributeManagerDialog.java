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
 * Created on 10.07.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.modes.mindmapmode.attributeactors;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import freemind.controller.Controller;
import freemind.controller.MapModuleManager.MapModuleChangeObserver;
import freemind.controller.filter.util.SortedListModel;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.modes.MapRegistry;
import freemind.modes.Mode;
import freemind.modes.attributes.AttributeRegistry;
import freemind.view.MapModule;

/**
 * @author Dimitri Polivaev 10.07.2005
 */
public class AttributeManagerDialog extends JDialog implements
		MapModuleChangeObserver {
	private JTable view;
	private MapRegistry registry;
	private AttributeRegistry model;
	private static final String[] fontSizes = { "6", "8", "10", "12", "14",
			"16", "18", "20", "24" };
	private JComboBox size;
	private ImportAttributesDialog importDialog = null;
	private Controller c;
	static final Icon editButtonImage = new ImageIcon(Resources.getInstance()
			.getResource("images/edit12.png"));

	private class ApplyAction extends AbstractAction {
		ApplyAction() {
			super();
			Tools.setLabelAndMnemonic(this, Resources.getInstance()
					.getResourceString("apply"));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		public void actionPerformed(ActionEvent e) {
			applyChanges();
		}

	}

	private void applyChanges() {
		Object size = this.size.getSelectedItem();
		int iSize = Integer.parseInt(size.toString());
		model.getAttributeController().performSetFontSize(model, iSize);
		model.applyChanges();
	}

	private void resetChanges() {
		int iSize = model.getFontSize();
		size.setSelectedItem(Integer.toString(iSize));
		model.resetChanges();
	}

	private class OKAction extends AbstractAction {
		OKAction() {
			super();
			Tools.setLabelAndMnemonic(this, Resources.getInstance()
					.getResourceString("ok"));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		public void actionPerformed(ActionEvent e) {
			applyChanges();
			setVisible(false);
		}
	}

	private class CancelAction extends AbstractAction {
		CancelAction() {
			super();
			Tools.setLabelAndMnemonic(this, Resources.getInstance()
					.getResourceString("cancel"));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		public void actionPerformed(ActionEvent e) {
			resetChanges();
			setVisible(false);
		}
	}

	private class ImportAction extends AbstractAction {
		ImportAction() {
			Tools.setLabelAndMnemonic(this, Resources.getInstance()
					.getResourceString("attributes_import"));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		public void actionPerformed(ActionEvent e) {
			if (importDialog == null) {
				importDialog = new ImportAttributesDialog(c,
						AttributeManagerDialog.this);
			}
			importDialog.show();
		}
	}

	// private class RefreshAction extends AbstractAction{
	// RefreshAction(){
	// super(Resources.getInstance().getResourceString("attributes_refresh"));
	// }
	// /* (non-Javadoc)
	// * @see
	// java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	// */
	// public void actionPerformed(ActionEvent e) {
	// registry.refresh();
	// }
	// }

	class EditListAction extends AbstractAction {
		public EditListAction() {
			super("", editButtonImage);
		}

		private int row = 0;
		private SortedListModel listBoxModel;
		private String title;
		private String labelText;

		public void actionPerformed(ActionEvent e) {
			ListDialog.showDialog((Component) e.getSource(),
					AttributeManagerDialog.this, labelText, title,
					listBoxModel, "xxxxxxxxxxxxxxxxxxxxx");
		}

		public int getRow() {
			return row;
		}

		public void setRow(int row) {
			this.row = row;
		}

		public void setListBoxModel(String title, String labelText,
				SortedListModel listBoxModel) {
			this.title = title;
			this.labelText = labelText;
			this.listBoxModel = listBoxModel;
		}
	}

	private class ClosingListener extends WindowAdapter {

		public void windowClosing(WindowEvent e) {
			resetChanges();
			super.windowClosing(e);
			setVisible(false);
		}

	}

	public AttributeManagerDialog(Controller c) {
		super(c.getJFrame(), Resources.getInstance().getResourceString(
				"attributes_dialog_title"), true);
		this.c = c;
		view = new AttributeRegistryTable(new EditListAction());
		registry = c.getMap().getRegistry();
		model = registry.getAttributes();
		view.setModel(model.getTableModel());
		view.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
		view.getTableHeader().setReorderingAllowed(false);

		JScrollPane scrollPane = new JScrollPane(view);
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		final Box southButtons = Box.createHorizontalBox();
		southButtons.setBorder(new EmptyBorder(5, 5, 5, 5));

		getContentPane().add(southButtons, BorderLayout.SOUTH);
		southButtons.add(Box.createHorizontalGlue());
		JButton ok = new JButton(new OKAction());
		southButtons.add(ok);
		southButtons.add(Box.createHorizontalGlue());
		JButton apply = new JButton(new ApplyAction());
		southButtons.add(apply);
		southButtons.add(Box.createHorizontalGlue());
		JButton cancel = new JButton(new CancelAction());
		southButtons.add(cancel);
		southButtons.add(Box.createHorizontalGlue());
		// JButton refresh = new JButton(new RefreshAction());
		// southButtons.add(refresh);
		// southButtons.add(Box.createHorizontalGlue());
		size = new JComboBox(fontSizes);
		size.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				model.setAttributeLayoutChanged();
			}

		});
		size.setToolTipText(Resources.getInstance().getResourceString(
				"attribute_font_size_tooltip"));
		southButtons.add(size);
		southButtons.add(Box.createHorizontalGlue());
		JButton importBtn = new JButton(new ImportAction());
		importBtn.setToolTipText(Resources.getInstance().getResourceString(
				"attributes_import_tooltip"));
		southButtons.add(importBtn);
		southButtons.add(Box.createHorizontalGlue());

		Tools.addEscapeActionToDialog(this);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new ClosingListener());
		c.getMapModuleManager().addListener(this);

		addComponentListener(new ComponentAdapter() {

			public void componentShown(ComponentEvent e) {
				size.setSelectedItem(Integer.toString(model.getFontSize()));
			}
		});
	}

	public boolean isMapModuleChangeAllowed(MapModule oldMapModule,
			Mode oldMode, MapModule newMapModule, Mode newMode) {
		return !isVisible();
	}

	public void beforeMapModuleChange(MapModule oldMapModule, Mode oldMode,
			MapModule newMapModule, Mode newMode) {
	}

	public void afterMapModuleChange(MapModule oldMapModule, Mode oldMode,
			MapModule newMapModule, Mode newMode) {
		if (newMapModule != null) {
			registry = newMapModule.getModel().getRegistry();
			model = registry.getAttributes();
			view.setModel(registry.getAttributes().getTableModel());
		}
	}

	public void numberOfOpenMapInformation(int number, int pIndex) {
	}

	public void afterMapClose(MapModule pOldMapModule, Mode pOldMode) {
	}
}
