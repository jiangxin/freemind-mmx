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
 * Created on 06.10.2004
 */
/*$Id: CloudAction.java,v 1.1.4.1 2004-10-17 23:00:08 dpolivaev Exp $*/

package freemind.modes.actions;

import javax.xml.bind.JAXBException;

import freemind.controller.actions.ActionPair;
import freemind.controller.actions.NodeActorXml;
import freemind.controller.actions.generated.instance.AddCloudXmlAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.mindmapmode.MindMapCloudModel;

/**
 * @author foltin
 *  
 */
public class CloudAction extends NodeGeneralAction implements NodeActorXml {

    public CloudAction(ModeController controller) {
        super(controller, "cloud", "images/Cloud24.gif");
        addActor(this);
    }

    public Class getDoActionClass() {
        return AddCloudXmlAction.class;
    }

    public ActionPair apply(MapAdapter model, MindMapNode selected)
            throws JAXBException {
        ActionPair pair = getActionPair(selected, selected.getCloud() == null);
        return pair;
    }

    public void setCloud(MindMapNode node, boolean enable) {
        try {
            modeController.getActionFactory().startTransaction(
                    (String) getValue(NAME));
            modeController.getActionFactory().executeAction(
                    getActionPair(node, enable));
            modeController.getActionFactory().endTransaction(
                    (String) getValue(NAME));
        } catch (JAXBException e) {
            e.printStackTrace();
        }

    }

    private ActionPair getActionPair(MindMapNode selected, boolean enable)
            throws JAXBException {
        AddCloudXmlAction cloudAction = createAddCloudXmlAction(
                selected, enable);
        AddCloudXmlAction undocloudAction = createAddCloudXmlAction(
                selected, selected.getCloud() != null);
        return new ActionPair(cloudAction, undocloudAction);
    }

    private AddCloudXmlAction createAddCloudXmlAction(
            MindMapNode selected, boolean enable) throws JAXBException {
        AddCloudXmlAction nodecloudAction = getActionXmlFactory()
                .createAddCloudXmlAction();
        nodecloudAction.setNode(getNodeID(selected));
        nodecloudAction.setEnabled(enable);
        return nodecloudAction;
    }

    public void act(XmlAction action) {
        if (action instanceof AddCloudXmlAction) {
            AddCloudXmlAction nodecloudAction = (AddCloudXmlAction) action;
            MindMapNode node = getNodeFromID(nodecloudAction.getNode());
            if((node.getCloud() == null) == nodecloudAction.isEnabled()) {
                if (nodecloudAction.isEnabled()) {
                    node.setCloud(new MindMapCloudModel(node, getModeController().getFrame()));
                } else {
                    node.setCloud(null);
                }
                modeController.nodeChanged(node);
            }
        }
    }

}