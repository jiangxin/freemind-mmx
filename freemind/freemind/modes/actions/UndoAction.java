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
 * Created on 20.09.2004
 */
/*$Id: UndoAction.java,v 1.1.4.1.10.2 2006-02-26 14:27:55 dpolivaev Exp $*/

package freemind.modes.actions;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.xml.bind.JAXBException;

import freemind.controller.actions.AbstractXmlAction;
import freemind.controller.actions.ActionPair;
import freemind.controller.actions.ActorXml;
import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.CompoundActionType;
import freemind.controller.actions.generated.instance.UndoXmlAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ControllerAdapter;
import freemind.modes.ModeController;


public class UndoAction extends AbstractXmlAction implements ActorXml {

    private ControllerAdapter controller;
    private boolean isUndoAction;
	protected Vector actionPairList=new Vector();
	private long timeOfLastAdd = 0;
    private boolean actionFrameStarted = false;
    private static final long TIME_TO_BEGIN_NEW_ACTION = 100;
    protected static Logger logger;

    public UndoAction(ControllerAdapter controller) {
        this(controller, controller.getText("undo"), new ImageIcon(controller.getResource("images/undo.png")), controller);
        this.controller = controller;
        if (logger==null) {
            logger = controller.getFrame()
                    .getLogger(this.getClass().toString());
        }
    }

	protected UndoAction(ControllerAdapter adapter, String text, Icon icon, ModeController mode) {
		super(text, icon, mode);
        this.controller = adapter;
		addActor(this);
		setEnabled(false);
		isUndoAction = false;
	}

    /**
     * @return
     */
    public boolean isUndoAction() {
        return isUndoAction;
    }

    /* (non-Javadoc)
     * @see freemind.controller.actions.AbstractXmlAction#xmlActionPerformed(java.awt.event.ActionEvent)
     */
    protected void xmlActionPerformed(ActionEvent arg0) throws JAXBException {
     	if(actionPairList.size() > 0) {
			ActionPair pair = (ActionPair) actionPairList.get(0);
			informUndoPartner(pair);
			actionPairList.remove(0);

            undoDoAction(pair);

			if(actionPairList.size() == 0) {
				// disable undo
				this.setEnabled(false);
			}
    	} else {
			setEnabled(false);
    	}
    }

    /**
     * @param pair
     */
    protected void informUndoPartner(ActionPair pair) {
        this.controller.redo.add(pair.reverse());
        this.controller.redo.setEnabled(true);
    }

    protected void undoDoAction(ActionPair pair) throws JAXBException {
        String doActionString = this.controller.marshall(pair.getDoAction());
        String redoActionString = this.controller.marshall(pair.getUndoAction());
		//logger.info("doActionString: "+ doActionString ); 
		//logger.info("\nredoActionString: "+ redoActionString);
        
        UndoXmlAction undoAction = this.controller.getActionXmlFactory().createUndoXmlAction();
        undoAction.setDescription(redoActionString);
        undoAction.setRemedia(doActionString);
        
        UndoXmlAction redoAction = this.controller.getActionXmlFactory().createUndoXmlAction();
        redoAction.setDescription(doActionString);
        undoAction.setRemedia(redoActionString);
        
        isUndoAction = true;
        this.controller.getActionFactory().executeAction(new ActionPair(undoAction, redoAction));
        isUndoAction = false;
    }

    /* (non-Javadoc)
     * @see freemind.controller.actions.ActorXml#act(freemind.controller.actions.generated.instance.XmlAction)
     */
    public void act(XmlAction action) {
       // unmarshall:
        UndoXmlAction undoAction = (UndoXmlAction) action;
		XmlAction doAction = this.controller.unMarshall( undoAction.getDescription() );
		XmlAction redoAction = this.controller.unMarshall( undoAction.getRemedia() );
		this.controller.getActionFactory().executeAction(new ActionPair(doAction, redoAction));
    }

    public Class getDoActionClass() {
        return UndoXmlAction.class;
    }

    /* (non-Javadoc)
     * @see javax.swing.Action#setEnabled(boolean)
     */
    public void setEnabled(boolean arg0) {
    	if(arg0)
        	super.setEnabled(actionPairList.size() != 0);
        else
        	super.setEnabled(false);
    }
    
    public void add(ActionPair pair) {
	    try {
	        long currentTime = System.currentTimeMillis();
	        if((actionPairList.size() > 0) && (actionFrameStarted || currentTime - timeOfLastAdd < TIME_TO_BEGIN_NEW_ACTION)) {
	            ActionPair firstPair = (ActionPair) actionPairList.get(0);
                CompoundAction action;
                CompoundAction remedia;
	            if ( ! (firstPair.getDoAction() instanceof CompoundActionType) || ! (firstPair.getUndoAction() instanceof CompoundActionType)) {
	                action = controller.getActionXmlFactory().createCompoundAction();
	                action.getCompoundActionOrSelectNodeActionOrCutNodeAction().add(firstPair.getDoAction());
	                remedia = controller.getActionXmlFactory().createCompoundAction();
	                remedia.getCompoundActionOrSelectNodeActionOrCutNodeAction().add(firstPair.getUndoAction());
	                actionPairList.remove(0);
	                actionPairList.add(0, new ActionPair(action, remedia));
		            firstPair = (ActionPair) actionPairList.get(0);
	            } else {
	                action = (CompoundAction) firstPair.getDoAction();
	                remedia = (CompoundAction) firstPair.getUndoAction();
	            }
	            action.getCompoundActionOrSelectNodeActionOrCutNodeAction().add(pair.getDoAction());
	            remedia.getCompoundActionOrSelectNodeActionOrCutNodeAction().add(0, pair.getUndoAction());
	        } else {
	            actionPairList.add(0, pair);
                // and cut vector, if bigger than given size:
	            int maxEntries = 100;
                try {
                    maxEntries = new Integer(controller.getFrame().getProperty(
                            "undo_levels")).intValue();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                while (actionPairList.size()>maxEntries) {
                    actionPairList.remove(actionPairList.size()-1); // remove
                                                                    // last elt
                }
	        }
            startActionFrame();
	        timeOfLastAdd = currentTime;
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void startActionFrame() {
        if(actionFrameStarted == false && EventQueue.isDispatchThread())
        {
            actionFrameStarted = true;
            EventQueue.invokeLater(new Runnable() {
                public void run(){
                    actionFrameStarted = false; 
                }
            });
        }
    }
    
    public void clear() {
        actionPairList.clear();
    }

    public void print() {
        logger.info("Undo list:");
        int j=0;
        for (Iterator i = actionPairList.iterator(); i.hasNext();) {
            ActionPair pair = (ActionPair) i.next();
            logger.info("line "+(j++)+" = " + controller.marshall(pair.getDoAction()));
        }
    }
}