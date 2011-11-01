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
 * Created on 05.05.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.controller.filter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import freemind.controller.Controller;
import freemind.controller.FreeMindToolBar;
import freemind.controller.filter.condition.Condition;
import freemind.controller.filter.condition.NoFilteringCondition;
import freemind.controller.filter.condition.SelectedViewCondition;
import freemind.main.Resources;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;

class FilterToolbar extends FreeMindToolBar {
	private FilterController fc;
	private FilterComposerDialog filterDialog = null;
	private JComboBox activeFilterConditionComboBox;
	private JCheckBox showAncestors;
	private JCheckBox showDescendants;
	private Filter activeFilter;
	private JButton btnEdit;
	private JButton btnUnfoldAncestors;
	private Controller c;
	private static Color filterInactiveColor = null;
	private String pathToFilterFile;
	private FilterChangeListener filterChangeListener;

	private class FilterChangeListener extends AbstractAction implements
			ItemListener, PropertyChangeListener {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		public FilterChangeListener() {
		}

		public void actionPerformed(ActionEvent arg0) {
			resetFilter();
			setMapFilter();
			refreshMap();
			DefaultFilter.selectVisibleNode(c.getView());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent
		 * )
		 */
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED)
				filterChanged();
		}

		private void filterChanged() {
			resetFilter();
			setMapFilter();
			final MindMap map = fc.getMap();
			if (map != null) {
				activeFilter.applyFilter(c);
				refreshMap();
				DefaultFilter.selectVisibleNode(c.getView());
			}
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals("model")) {
				addStandardConditions();
				filterChanged();
			}
		}

	}

	private class EditFilterAction extends AbstractAction {
		EditFilterAction() {
			super("", new ImageIcon(Resources.getInstance().getResource(
					"images/Btn_edit.gif")));
			putValue(SHORT_DESCRIPTION, Resources.getInstance()
					.getResourceString("filter_edit_description"));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		private FilterComposerDialog getFilterDialog() {
			if (filterDialog == null) {
				filterDialog = new FilterComposerDialog(c, FilterToolbar.this);
				getFilterDialog().setLocationRelativeTo(FilterToolbar.this);
			}
			return filterDialog;
		}

		public void actionPerformed(ActionEvent arg0) {
			Object selectedItem = getFilterConditionModel().getSelectedItem();
			if (selectedItem != null) {
				getFilterDialog().setSelectedItem(selectedItem);
			}
			getFilterDialog().show();
		}

	}

	private class UnfoldAncestorsAction extends AbstractAction {
		/**
         *
         */
		UnfoldAncestorsAction() {
			super("", new ImageIcon(Resources.getInstance().getResource(
					"images/unfold.png")));
		}

		private void unfoldAncestors(MindMapNode parent) {
			for (Iterator i = parent.childrenUnfolded(); i.hasNext();) {
				MindMapNode node = (MindMapNode) i.next();
				if (showDescendants.isSelected()
						|| node.getFilterInfo().isAncestor()) {
					setFolded(node, false);
					unfoldAncestors(node);
				}
			}
		}

		private void setFolded(MindMapNode node, boolean state) {
			if (node.hasChildren() && (node.isFolded() != state)) {
				c.getModeController().setFolded(node, state);
			}
		}

		public void actionPerformed(ActionEvent e) {
			if (getSelectedCondition() != null) {
				unfoldAncestors(c.getModel().getRootNode());
			}
		}
	}

	FilterToolbar(final Controller c) {
		super();
		this.fc = c.getFilterController();
		this.c = c;
		setVisible(false);
		setFocusable(false);
		setRollover(true);
		filterChangeListener = new FilterChangeListener();
		add(new JLabel(Resources.getInstance().getResourceString(
				"filter_toolbar")
				+ " "));

		activeFilter = null;
		activeFilterConditionComboBox = new JComboBox() {
			public Dimension getMaximumSize() {
				return getPreferredSize();
			}
		};
		activeFilterConditionComboBox.setFocusable(false);
		pathToFilterFile = c.getFrame().getFreemindDirectory() + File.separator
				+ "auto."
				+ FilterController.FREEMIND_FILTER_EXTENSION_WITHOUT_DOT;

		btnEdit = add(new EditFilterAction());
		add(btnEdit);

		btnUnfoldAncestors = add(new UnfoldAncestorsAction());
		btnUnfoldAncestors.setToolTipText(Resources.getInstance()
				.getResourceString("filter_unfold_ancestors"));
		add(btnUnfoldAncestors);

		showAncestors = new JCheckBox(Resources.getInstance()
				.getResourceString("filter_show_ancestors"), true);
		add(showAncestors);
		showAncestors.getModel().addActionListener(filterChangeListener);

		showDescendants = new JCheckBox(Resources.getInstance()
				.getResourceString("filter_show_descendants"), false);
		add(showDescendants);
		showDescendants.getModel().addActionListener(filterChangeListener);

	}

	void addStandardConditions() {
		DefaultComboBoxModel filterConditionModel = fc
				.getFilterConditionModel();
		final Condition noFiltering = NoFilteringCondition.createCondition();
		filterConditionModel.insertElementAt(noFiltering, 0);
		filterConditionModel.insertElementAt(
				SelectedViewCondition.CreateCondition(), 1);
		if (filterConditionModel.getSelectedItem() == null) {
			filterConditionModel.setSelectedItem(noFiltering);
		}
	}

	void initConditions() {
		try {
			fc.loadConditions(fc.getFilterConditionModel(), pathToFilterFile);

		} catch (Exception e) {
		}
		addStandardConditions();
		activeFilterConditionComboBox.setSelectedIndex(0);
		activeFilterConditionComboBox.setRenderer(fc.getConditionRenderer());

		add(activeFilterConditionComboBox);
		add(Box.createHorizontalGlue());

		activeFilterConditionComboBox.addItemListener(filterChangeListener);
		activeFilterConditionComboBox
				.addPropertyChangeListener(filterChangeListener);
	}

	/**
     *
     */
	public void resetFilter() {
		activeFilter = null;

	}

	private Condition getSelectedCondition() {
		return (Condition) activeFilterConditionComboBox.getSelectedItem();
	}

	void setMapFilter() {
		if (activeFilter == null)
			activeFilter = new DefaultFilter(getSelectedCondition(),
					showAncestors.getModel().isSelected(), showDescendants
							.getModel().isSelected());
		final MindMap map = fc.getMap();
		if (map != null) {
			map.setFilter(activeFilter);
		}
	}

	/**
     */
	FilterComposerDialog getFilterDialog() {
		return filterDialog;
	}

	/**
     */
	void mapChanged(MindMap newMap) {
		if (!isVisible())
			return;
		Filter filter;
		if (newMap != null) {
			filter = newMap.getFilter();
			if (filter != activeFilter) {
				activeFilter = filter;
				activeFilterConditionComboBox.setSelectedItem(filter
						.getCondition());
				showAncestors.setSelected(filter.areAncestorsShown());
				showDescendants.setSelected(filter.areDescendantsShown());
			}
		} else {
			filter = null;
			activeFilterConditionComboBox.setSelectedIndex(0);
		}
	}

	private void refreshMap() {
		fc.refreshMap();
	}

	void saveConditions() {
		try {
			fc.saveConditions(fc.getFilterConditionModel(), pathToFilterFile);
		} catch (Exception e) {
		}
	}

	ComboBoxModel getFilterConditionModel() {
		return activeFilterConditionComboBox.getModel();
	}

	void setFilterConditionModel(ComboBoxModel filterConditionModel) {
		activeFilterConditionComboBox.setModel(filterConditionModel);
	}
}
