/*
 * Created on 05.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package freemind.modes.actions;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.xml.bind.JAXBException;

import freemind.controller.actions.ActionPair;
import freemind.controller.actions.ActorXml;
import freemind.controller.actions.generated.instance.EditNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ControllerAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.view.mindmapview.EditNodeBase;
import freemind.view.mindmapview.EditNodeDialog;
import freemind.view.mindmapview.EditNodeTextField;
import freemind.view.mindmapview.NodeView;


//
// Node editing
//

public class EditAction extends AbstractAction implements ActorXml {
	private final ControllerAdapter c;
    public EditAction(ControllerAdapter modeController) {
        super(modeController.getText("edit_node"));
		this.c = modeController;
		this.c.getActionFactory().registerActor(this, getDoActionClass());
    }
	public void actionPerformed(ActionEvent arg0) {
		MindMapNode selected = this.c.getSelected();
		this.c.edit(null, false, false);
	}
	/* (non-Javadoc)
	 * @see freemind.controller.actions.ActorXml#act(freemind.controller.actions.generated.instance.XmlAction)
	 */
	public void act(XmlAction action) {
		EditNodeAction editAction = (EditNodeAction) action;
		NodeAdapter node = this.c.getNodeFromID(editAction.getNode());
		if(!node.toString().equals(editAction.getText())) {
			node.setUserObject(editAction.getText());
			this.c.nodeChanged(node);
		}
	}
	/* (non-Javadoc)
	 * @see freemind.controller.actions.ActorXml#getDoActionClass()
	 */
	public Class getDoActionClass() {
		return EditNodeAction.class;
	}
	
	// edit begins with home/end or typing (PN 6.2)
	public void edit(KeyEvent e, boolean addNew, boolean editLong) {
	  if (c.getView().getSelected() != null) {
		if (e == null || !addNew) {
		  edit(c.getView().getSelected(),c.getView().getSelected(), e, false, false, editLong);
		}
		else if (!c.isBlocked()) {
			c.addNew(c.getSelected(), ControllerAdapter.NEW_SIBLING_BEHIND, e);
		}
		if (e != null) {
		  e.consume();
		}
	  }
	}


    /**
	 * @param node
	 * @param prevSelected when new->esc: node be selected
	 * @param firstEvent
	 * @param isNewNode when new->esc: cut the node
	 * @param parentFolded when new->esc: fold prevSelected
	 * @param editLong
	 */
	public void editLater(
		final NodeView node,
		final NodeView prevSelected,
		final KeyEvent firstEvent,
		final boolean isNewNode,
		final boolean parentFolded,
		final boolean editLong) {
		class DelayedEditor implements Runnable {

			/* (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			final NodeView node;
			final NodeView prevSelected;
			final KeyEvent firstEvent;
			final boolean isNewNode;
			final boolean parentFolded;
			final boolean editLong;
			DelayedEditor(
					final NodeView node,
					final NodeView prevSelected,
					final KeyEvent firstEvent,
					final boolean isNewNode,
					final boolean parentFolded,
					final boolean editLong){
				this.node = node;
				this.prevSelected = prevSelected;
				this.firstEvent = firstEvent;
				this.isNewNode = isNewNode;
				this.parentFolded = parentFolded;
				this.editLong = editLong;
			}
			public void run() {
				edit(node, prevSelected, firstEvent, isNewNode, parentFolded, editLong);				
			}
		};
		EventQueue.invokeLater(new DelayedEditor(node, prevSelected, firstEvent, isNewNode, parentFolded, editLong));
		}

		public void edit(
				final NodeView node,
				final NodeView prevSelected,
				final KeyEvent firstEvent,
				final boolean isNewNode,
				final boolean parentFolded,
				final boolean editLong) {
				if (node == null) {
					return;
				}

		//EditNodeBase.closeEdit();
		c.setBlocked(true); // locally "modal" stated

		String text = node.getModel().toString();
		if (node.getIsLong() || editLong) {
			EditNodeDialog nodeEditDialog =
				new EditNodeDialog(
					node,
					text,
					firstEvent,
					c,
					new EditNodeBase.EditControl() {

				public void cancel() {
				}

				public void ok(String newText) {
					setNodeText(node.getModel(), newText);
				}

				public void split(String newText, int position) {
					c.splitNode(node.getModel(), position, newText);
					c.getController().obtainFocusForSelected(); // focus fix
				}
			});
			;
			nodeEditDialog.show();
			c.setBlocked(false);
			return;
		}
		// inline editing:
		EditNodeTextField textfield =
			new EditNodeTextField(node, text, firstEvent, c, new EditNodeBase.EditControl(){

				public void cancel() {
					if (isNewNode) { // delete also the node and set focus to the parent
						c.getView().selectAsTheOnlyOneSelected(node);
						c.cut();
						c.select(prevSelected);
						// include max level for navigation
						if (parentFolded) {
							c.setFolded(prevSelected.getModel(), true);
						}
					}
					endEdit();
				}

				public void ok(String newText) {
					setNodeText(node.getModel(), newText);
					endEdit();
				}

				private void endEdit() {
					c.getController().obtainFocusForSelected();
					c.setBlocked(false);
				}

				public void split(String newText, int position) {
				}});
		  textfield.show();

	}

	public void setNodeText(MindMapNode selected, String newText){
		String oldText = selected.toString();

		try {
			c.getActionFactory().startTransaction(c.getText("edit_node"));
			EditNodeAction EditAction = c.getActionXmlFactory().createEditNodeAction();
			EditAction.setNode(c.getNodeID(selected));
			EditAction.setText(newText);
            
			EditNodeAction undoEditAction = c.getActionXmlFactory().createEditNodeAction();
			undoEditAction.setNode(c.getNodeID(selected));
			undoEditAction.setText(oldText);
            	
			c.getActionFactory().executeAction(new ActionPair(EditAction, undoEditAction));
			c.getActionFactory().endTransaction(c.getText("edit_node"));
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
	}

    protected ControllerAdapter getModeController() {
        return c;
    }


	
}