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
 * Created on 22.01.2006
 * Created by Dimitri Polivaev
 */
package freemind.modes.mindmapmode.attributeactors;

import java.util.ListIterator;
import java.util.NoSuchElementException;

import freemind.controller.filter.util.SortedComboBoxModel;
import freemind.modes.MindMapNode;
import freemind.modes.attributes.Attribute;
import freemind.modes.attributes.AttributeController;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.attributes.AttributeRegistryElement;
import freemind.modes.attributes.NodeAttributeTableModel;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapNodeModel;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

public class MindMapModeAttributeController implements AttributeController {
	private static interface Visitor {
		void visit(NodeAttributeTableModel model);
	}

	private class AttributeRenamer implements Visitor {

		private Object oldName;
		private Object newName;

		public AttributeRenamer(Object oldName, Object newName) {
			super();
			this.newName = newName;
			this.oldName = oldName;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * freemind.modes.attributes.AttributeRegistry.Visitor#visit(freemind
		 * .modes.attributes.ConcreteAttributeTableModel)
		 */
		public void visit(NodeAttributeTableModel model) {
			for (int i = 0; i < model.getRowCount(); i++) {
				if (model.getName(i).equals(oldName)) {
					final ActionPair setAttributeNameActionPair = setAttributeNameActor
							.createActionPair(model, i, newName.toString());
					controller.doTransaction(
							"setAttributeNameActionPair",
							setAttributeNameActionPair);
				}
			}
		}

	}

	private class AttributeChanger implements Visitor {
		private Object name;
		private Object oldValue;
		private Object newValue;

		public AttributeChanger(Object name, Object oldValue, Object newValue) {
			super();
			this.name = name;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * freemind.modes.attributes.AttributeRegistry.Visitor#visit(freemind
		 * .modes.attributes.ConcreteAttributeTableModel)
		 */
		public void visit(NodeAttributeTableModel model) {
			for (int i = 0; i < model.getRowCount(); i++) {
				if (model.getName(i).equals(name)
						&& model.getValue(i).equals(oldValue)) {
					final ActionPair setAttributeValueActionPair = setAttributeValueActor
							.createActionPair(model, i, newValue.toString());
					controller.doTransaction(
							"setAttributeValueActionPair",
							setAttributeValueActionPair);
				}
			}
		}
	}

	private class AttributeRemover implements Visitor {
		private Object name;

		public AttributeRemover(Object name) {
			super();
			this.name = name;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * freemind.modes.attributes.AttributeRegistry.Visitor#visit(freemind
		 * .modes.attributes.ConcreteAttributeTableModel)
		 */
		public void visit(NodeAttributeTableModel model) {
			for (int i = 0; i < model.getRowCount(); i++) {
				if (model.getName(i).equals(name)) {
					final ActionPair removeAttributeActionPair = removeAttributeActor
							.createActionPair(model, i);
					controller.doTransaction(
							"removeAttributeActionPair",
							removeAttributeActionPair);
				}
			}
		}
	}

	private class AttributeValueRemover implements Visitor {

		private Object name;
		private Object value;

		public AttributeValueRemover(Object name, Object value) {
			super();
			this.name = name;
			this.value = value;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * freemind.modes.attributes.AttributeRegistry.Visitor#visit(freemind
		 * .modes.attributes.ConcreteAttributeTableModel)
		 */
		public void visit(NodeAttributeTableModel model) {
			for (int i = 0; i < model.getRowCount(); i++) {
				if (model.getName(i).equals(name)
						&& model.getValue(i).equals(value)) {
					final ActionPair removeAttributeActionPair = removeAttributeActor
							.createActionPair(model, i);
					controller.doTransaction(
							"removeAttributeActionPair",
							removeAttributeActionPair);
				}
			}
		}
	}

	private static class Iterator {
		private Visitor visitor;

		Iterator(Visitor v) {
			this.visitor = v;
		}

		/**
         */
		void iterate(MindMapNode node) {
			visitor.visit(node.getAttributes());
			ListIterator iterator = node.childrenUnfolded();
			while (iterator.hasNext()) {
				MindMapNode child = (MindMapNode) iterator.next();
				iterate(child);
			}
		}
	}

	SetAttributeNameActor setAttributeNameActor;
	SetAttributeValueActor setAttributeValueActor;
	RemoveAttributeActor removeAttributeActor;
	InsertAttributeActor insertAttributeActor;
	SetAttributeColumnWidthActor setAttributeColumnWidthActor;
	RegistryAttributeActor registryAttributeActor;
	UnregistryAttributeActor unregistryAttributeActor;
	RegistryAttributeValueActor registryAttributeValueActor;
	UnregistryAttributeValueActor unregistryAttributeValueActor;
	ReplaceAttributeValueActor replaceAttributeValueActor;
	SetAttributeFontSizeActor setAttributeFontSizeActor;
	SetAttributeVisibleActor setAttributeVisibleActor;
	SetAttributeRestrictedActor setAttributeRestrictedActor;

	private MindMapController controller;

	public MindMapModeAttributeController(MindMapController controller) {
		this.controller = controller;
		setAttributeNameActor = new SetAttributeNameActor(controller);
		setAttributeValueActor = new SetAttributeValueActor(controller);
		removeAttributeActor = new RemoveAttributeActor(controller);
		insertAttributeActor = new InsertAttributeActor(controller);
		setAttributeColumnWidthActor = new SetAttributeColumnWidthActor(
				controller);
		registryAttributeActor = new RegistryAttributeActor(controller);
		unregistryAttributeActor = new UnregistryAttributeActor(controller);
		registryAttributeValueActor = new RegistryAttributeValueActor(
				controller);
		replaceAttributeValueActor = new ReplaceAttributeValueActor(controller);
		unregistryAttributeValueActor = new UnregistryAttributeValueActor(
				controller);
		setAttributeFontSizeActor = new SetAttributeFontSizeActor(controller);
		setAttributeVisibleActor = new SetAttributeVisibleActor(controller);
		setAttributeRestrictedActor = new SetAttributeRestrictedActor(
				controller);
	}

	public void performSetValueAt(NodeAttributeTableModel model, Object o,
			int row, int col) {
		Attribute attribute = model.getAttribute(row);

		AttributeRegistry attributes = getAttributeRegistry();
		switch (col) {
		case 0: {
			String name = o.toString().trim();
			if (attribute.getName().equals(name))
				return;
			controller.doTransaction(
					"setAttributeNameActionPair",
					setAttributeNameActor.createActionPair(model, row, name));
			try {
				AttributeRegistryElement element = attributes.getElement(name);
				String value = model.getValueAt(row, 1).toString();
				int index = element.getValues().getIndexOf(value);
				if (index == -1) {
					final ActionPair setAttributeValueActionPair = setAttributeValueActor
							.createActionPair(model, row, element.getValues()
									.firstElement().toString());
					controller.doTransaction(
							"setAttributeValueActionPair",
							setAttributeValueActionPair);
				}
			} catch (NoSuchElementException ex) {
				controller.doTransaction(
						"registryAttributeActionPair",
						registryAttributeActor.createActionPair(name));
				controller.getActionFactory()
						.doTransaction(
								"setAttributeValueActionPair",
								setAttributeValueActor.createActionPair(model,
										row, ""));
			}
			break;
		}
		case 1: {
			String value = o.toString().trim();
			if (attribute.getValue().equals(value))
				return;
			final ActionPair setValueActionPair = setAttributeValueActor
					.createActionPair(model, row, value);
			controller.doTransaction("setValueActionPair",
					setValueActionPair);
			String name = model.getValueAt(row, 0).toString();
			AttributeRegistryElement element = attributes.getElement(name);
			int index = element.getValues().getIndexOf(value);
			if (index == -1) {
				final ActionPair registryAttributeValueActionPair = registryAttributeValueActor
						.createActionPair(name, value);
				controller.doTransaction(
						"registryAttributeValueActionPair",
						registryAttributeValueActionPair);
			}
			break;
		}
		}

	}

	public void performInsertRow(NodeAttributeTableModel model, int row,
			String name, String value) {
		AttributeRegistry attributes = getAttributeRegistry();
		if (name.equals(""))
			return;
		try {
			AttributeRegistryElement element = attributes.getElement(name);
			int index = element.getValues().getIndexOf(value);
			if (index == -1) {
				if (element.isRestricted()) {
					value = element.getValues().firstElement().toString();
				} else {
					final ActionPair registryNewAttributeActionPair = registryAttributeValueActor
							.createActionPair(name, value);
					controller.doTransaction(
							"performInsertRow", registryNewAttributeActionPair);
				}
			}
		} catch (NoSuchElementException ex) {
			controller.doTransaction("performInsertRow",
					registryAttributeActor.createActionPair(name));
			controller.doTransaction(
					"registryAttributeValueActor",
					registryAttributeValueActor.createActionPair(name, value));
		}
		final ActionPair insertAttributeActionPair = insertAttributeActor
				.createActionPair(model, row, name, value);
		controller.doTransaction(
				"insertAttributeActionPair", insertAttributeActionPair);
	}

	public void performRemoveRow(NodeAttributeTableModel model, int row) {
		controller.doTransaction("performRemoveRow",
				removeAttributeActor.createActionPair(model, row));
	}

	public void performSetColumnWidth(NodeAttributeTableModel model, int col,
			int width) {
		if (width == model.getLayout().getColumnWidth(col))
			return;
		controller.doTransaction(
				"performSetColumnWidth",
				setAttributeColumnWidthActor
						.createActionPair(model, col, width));
	}

	public void performRemoveAttributeValue(String name, String value) {
		controller.doTransaction(
				"removeAttributeActionPair",
				unregistryAttributeValueActor.createActionPair(name, value));
		Visitor remover = new AttributeValueRemover(name, value);
		Iterator iterator = new Iterator(remover);
		MindMapNode root = controller.getRootNode();
		iterator.iterate(root);
	}

	public void performReplaceAttributeValue(String name, String oldValue,
			String newValue) {
		controller.doTransaction(
				"replaceAttributeActionPair",
				replaceAttributeValueActor.createActionPair(name, oldValue,
						newValue));
		Visitor replacer = new AttributeChanger(name, oldValue, newValue);
		Iterator iterator = new Iterator(replacer);
		MindMapNode root = controller.getRootNode();
		iterator.iterate(root);
	}

	public void performSetFontSize(AttributeRegistry registry, int size) {
		if (size == registry.getFontSize())
			return;
		controller.doTransaction("setFontSizeActionPair",
				setAttributeFontSizeActor.createActionPair(size));
	}

	public void performSetVisibility(int index, boolean isVisible) {
		if (getAttributeRegistry().getElement(index).isVisible() == isVisible)
			return;
		controller.doTransaction("setVisibilityActionPair",
				setAttributeVisibleActor.createActionPair(index, isVisible));
	}

	public void performSetRestriction(int index, boolean isRestricted) {
		boolean currentValue;
		if (index == AttributeRegistry.GLOBAL) {
			currentValue = getAttributeRegistry().isRestricted();
		} else {
			currentValue = getAttributeRegistry().getElement(index)
					.isRestricted();
		}

		if (currentValue == isRestricted)
			return;
		controller.doTransaction(
				"setRestrictionActionPair",
				setAttributeRestrictedActor.createActionPair(index,
						isRestricted));
	}

	public void performReplaceAtributeName(String oldName, String newName) {
		if (oldName.equals("") || newName.equals("") || oldName.equals(newName))
			return;
		AttributeRegistry registry = getAttributeRegistry();
		int iOld = registry.getElements().indexOf(oldName);
		AttributeRegistryElement oldElement = registry.getElement(iOld);
		final SortedComboBoxModel values = oldElement.getValues();
		controller.doTransaction(
				"registryNewAttributeActionPair",
				registryAttributeActor.createActionPair(newName));
		for (int i = 0; i < values.getSize(); i++) {
			controller.doTransaction(
					"registryNewAttributeValueActionPair",
					registryAttributeValueActor.createActionPair(newName,
							values.getElementAt(i).toString()));
		}
		Visitor replacer = new AttributeRenamer(oldName, newName);
		Iterator iterator = new Iterator(replacer);
		MindMapNode root = controller.getRootNode();
		iterator.iterate(root);
		controller.doTransaction(
				"unregistryOldAttributeActionPair",
				unregistryAttributeActor.createActionPair(oldName));
	}

	public void performRemoveAttribute(String name) {
		controller.doTransaction(
				"unregistryOldAttributeActionPair",
				unregistryAttributeActor.createActionPair(name));
		Visitor remover = new AttributeRemover(name);
		Iterator iterator = new Iterator(remover);
		MindMapNode root = controller.getRootNode();
		iterator.iterate(root);
	}

	public void performRegistryAttribute(String name) {
		if (name.equals(""))
			return;
		try {
			final AttributeRegistryElement element = getAttributeRegistry()
					.getElement(name);
		} catch (NoSuchElementException ex) {
			controller.doTransaction(
					"registryNewAttributeActionPair",
					registryAttributeActor.createActionPair(name));
			return;
		}

	}

	public void performRegistryAttributeValue(String name, String value) {
		if (name.equals(""))
			return;
		try {
			final AttributeRegistryElement element = getAttributeRegistry()
					.getElement(name);
			if (element.getValues().contains(value)) {
				return;
			}
			controller.doTransaction(
					"registryNewAttributeActionPair",
					registryAttributeValueActor.createActionPair(name, value));
			return;
		} catch (NoSuchElementException ex) {
			controller.doTransaction(
					"registryAttributeActionPair",
					registryAttributeActor.createActionPair(name));
			controller.doTransaction(
					"registryAttributeValueActionPair",
					registryAttributeValueActor.createActionPair(name, value));
			return;
		}

	}

	private AttributeRegistry getAttributeRegistry() {
		return controller.getMap().getRegistry().getAttributes();
	}

	public void performRegistrySubtreeAttributes(MindMapNodeModel node) {
		for (int i = 0; i < node.getAttributes().getRowCount(); i++) {
			String name = node.getAttributes().getValueAt(i, 0).toString();
			String value = node.getAttributes().getValueAt(i, 1).toString();
			performRegistryAttributeValue(name, value);
		}
		for (ListIterator e = node.childrenUnfolded(); e.hasNext();) {
			final MindMapNodeModel child = (MindMapNodeModel) e.next();
			performRegistrySubtreeAttributes(child);
		}
	}
}
