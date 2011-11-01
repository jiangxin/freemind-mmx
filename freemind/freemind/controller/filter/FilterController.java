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

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;

import freemind.controller.Controller;
import freemind.controller.MapModuleManager.MapModuleChangeObserver;
import freemind.controller.filter.condition.Condition;
import freemind.controller.filter.condition.ConditionFactory;
import freemind.controller.filter.condition.ConditionRenderer;
import freemind.controller.filter.condition.NoFilteringCondition;
import freemind.main.Resources;
import freemind.main.XMLElement;
import freemind.modes.MindIcon;
import freemind.modes.MindMap;
import freemind.modes.Mode;
import freemind.modes.common.plugins.NodeNoteBase;
import freemind.view.MapModule;

/**
 * @author dimitri
 * 
 */
public class FilterController implements MapModuleChangeObserver {
	private Controller c;
	private FilterToolbar filterToolbar;
	private DefaultComboBoxModel filterConditionModel;
	static private ConditionRenderer conditionRenderer = null;
	static private ConditionFactory conditionFactory;
	private MindMap map;
	static final String FREEMIND_FILTER_EXTENSION_WITHOUT_DOT = "mmfilter";
	private static Filter inactiveFilter;

	public FilterController(Controller c) {
		this.c = c;
		c.getMapModuleManager().addListener(this);
	}

	ConditionRenderer getConditionRenderer() {
		if (conditionRenderer == null)
			conditionRenderer = new ConditionRenderer();
		return conditionRenderer;
	}

	/**
     */
	public FilterToolbar getFilterToolbar() {
		if (filterToolbar == null) {
			filterToolbar = new FilterToolbar(c);
			filterConditionModel = (DefaultComboBoxModel) filterToolbar
					.getFilterConditionModel();

			// FIXME state icons should be created on order to make possible
			// their use in the filter component.
			// It should not happen here.
			MindIcon.factory("AttributeExist", new ImageIcon(Resources
					.getInstance().getResource("images/showAttributes.gif")));
			MindIcon.factory(NodeNoteBase.NODE_NOTE_ICON, new ImageIcon(
					Resources.getInstance().getResource("images/knotes.png")));
			MindIcon.factory("encrypted");
			MindIcon.factory("decrypted");

			filterToolbar.initConditions();
		}
		return filterToolbar;
	}

	/**
     */
	public void showFilterToolbar(boolean show) {
		if (show == isVisible())
			return;
		getFilterToolbar().setVisible(show);
		final Filter filter = getMap().getFilter();
		if (show) {
			filter.applyFilter(c);
		} else {
			createTransparentFilter().applyFilter(c);
		}
		refreshMap();
	}

	public boolean isVisible() {
		return getFilterToolbar().isVisible();
	}

	void refreshMap() {
		c.getModeController().refreshMap();
	}

	static public ConditionFactory getConditionFactory() {
		if (conditionFactory == null)
			conditionFactory = new ConditionFactory();
		return conditionFactory;
	}

	/**
     */
	public MindMap getMap() {
		return map;
	}

	/**
	 * @param filterToolbar
	 *            The filterToolbar to set.
	 */
	private void setFilterToolbar(FilterToolbar filterToolbar) {
		this.filterToolbar = filterToolbar;
	}

	public boolean isMapModuleChangeAllowed(MapModule oldMapModule,
			Mode oldMode, MapModule newMapModule, Mode newMode) {
		return true;
	}

	public void beforeMapModuleChange(MapModule oldMapModule, Mode oldMode,
			MapModule newMapModule, Mode newMode) {
	}

	public void afterMapClose(MapModule pOldMapModule, Mode pOldMode) {
	}

	public void afterMapModuleChange(MapModule oldMapModule, Mode oldMode,
			MapModule newMapModule, Mode newMode) {
		MindMap newMap = newMapModule != null ? newMapModule.getModel() : null;
		FilterComposerDialog fd = getFilterToolbar().getFilterDialog();
		if (fd != null) {
			fd.mapChanged(newMap);
		}
		map = newMap;
		getFilterToolbar().mapChanged(newMap);
	}

	public void numberOfOpenMapInformation(int number, int pIndex) {
	}

	private static Filter createTransparentFilter() {
		if (inactiveFilter == null)
			inactiveFilter = new DefaultFilter(
					NoFilteringCondition.createCondition(), true, false);
		return inactiveFilter;

	}

	public void saveConditions() {
		if (filterToolbar != null) {
			filterToolbar.saveConditions();
		}
	}

	public DefaultComboBoxModel getFilterConditionModel() {
		return filterConditionModel;
	}

	public void setFilterConditionModel(
			DefaultComboBoxModel filterConditionModel) {
		this.filterConditionModel = filterConditionModel;
		filterToolbar.setFilterConditionModel(filterConditionModel);
	}

	void saveConditions(DefaultComboBoxModel filterConditionModel,
			String pathToFilterFile) throws IOException {
		XMLElement saver = new XMLElement();
		saver.setName("filter_conditions");
		Writer writer = new FileWriter(pathToFilterFile);
		for (int i = 0; i < filterConditionModel.getSize(); i++) {
			Condition cond = (Condition) filterConditionModel.getElementAt(i);
			cond.save(saver);
		}
		saver.write(writer);
		writer.close();
	}

	void loadConditions(DefaultComboBoxModel filterConditionModel,
			String pathToFilterFile) throws IOException {
		filterConditionModel.removeAllElements();
		XMLElement loader = new XMLElement();
		Reader reader = new FileReader(pathToFilterFile);
		loader.parseFromReader(reader);
		reader.close();
		final Vector conditions = loader.getChildren();
		for (int i = 0; i < conditions.size(); i++) {
			filterConditionModel.addElement(FilterController
					.getConditionFactory().loadCondition(
							(XMLElement) conditions.get(i)));
		}
	}
}
