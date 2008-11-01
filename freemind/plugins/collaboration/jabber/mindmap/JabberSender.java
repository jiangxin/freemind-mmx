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
 * Created on Mar 4, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package plugins.collaboration.jabber.mindmap;

import java.io.StringWriter;

import com.echomine.common.ParseException;
import com.echomine.common.SendMessageFailedException;
import com.echomine.jabber.JID;
import com.echomine.jabber.JabberChatService;
import com.echomine.jabber.JabberSession;

import freemind.controller.actions.generated.instance.CollaborationAction;
import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;
import freemind.modes.mindmapmode.actions.xml.ActionFilter;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * @author RReppel
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class JabberSender implements ActionFilter {

    public final static String REQUEST_MAP_SHARING = "request_map_sharing";

    public final static String ACCEPT_MAP_SHARING = "accept_map_sharing";

    public final static String DECLINE_MAP_SHARING = "decline_map_sharing";

    public final static String STOP_MAP_SHARING = "stop_map_sharing";

    // Logging:
    private static java.util.logging.Logger logger;

    JabberChatService chat;

    JabberSession session;

    String sendToUser;

    boolean mapShared; //True = send FreeMind commands. False = do not send

    // Freemind commands.

    private final MapSharingController controller;

    public JabberSender(JabberSession session, MapSharingController controller) {
        this.controller = controller;
        if (logger == null) {
            logger = controller.getController().getFrame().getLogger(
                    this.getClass().getName());
        }
        try {
            this.session = session;
            chat = this.session.getChatService();
            mapShared = false;
        } catch (Exception e) {
            freemind.main.Resources.getInstance().logException(e);
        }
    }

    /**
     * Sends a request to share a map. The receiving user can either accept or
     * decline the request.
     * 
     * @param requestingUser
     *            The user who requests the map to be shared.
     * @param requestReceiverUser
     *            The user who is to receive the request to share a map.
     */
    public void sendMapSharingRequest(String requestingUser,
            String requestReceiverUser) {
        try {
            CollaborationAction action = createCollaborationAction(
                    requestingUser, REQUEST_MAP_SHARING);
            // populate action with filename and map content
            String mapName = controller.getController().getMap().getFile().getName();
            action.setFilename(mapName);
            StringWriter stringWriter = new StringWriter();
            controller.getController().getMap().getXml(stringWriter);
            action.setMap(stringWriter.getBuffer().toString());
            sendMessage(requestReceiverUser, action);
        } catch (Exception e) {
            freemind.main.Resources.getInstance().logException(e);
        }
    }

    /**
     * Sends a request to stop sharing a map.
     *  
     */
    public void sendMapSharingStopRequest() {
        try {
            CollaborationAction action = createCollaborationAction(sendToUser,
                    STOP_MAP_SHARING);
            String message = marshal(action);
            sendMessage(sendToUser, action);
        } catch (Exception e) {
            freemind.main.Resources.getInstance().logException(e);
        }
    }

    /**
     * @param requestingUser
     * @param command
     * @throws JAXBException
     */
    private CollaborationAction createCollaborationAction(
            String requestingUser, String command) {
    
        CollaborationAction collaboration = new CollaborationAction();
        collaboration.setCmd(command);
        collaboration.setUser(requestingUser);
        collaboration.setTimestamp(String.valueOf(System
                .currentTimeMillis()));
        return collaboration;
         
    }

    /**
     * Sends whether a map sharing invitation was accepted or declined.
     * 
     * @param sentFromUser
     *            The name of the user accepting or declining the invitation.
     * @param sendToUser
     *            The user who had requested that his/her map be shared.
     * @param accepted
     *            true = accept, false = decline.
     *  
     */
    public void sendMapSharingInvitationResponse(String sentFromUser,
            String sendToUser, boolean accepted) {
        try {
            this.sendToUser = sendToUser;
            String message;
            CollaborationAction action;
            if (accepted) {
                action = createCollaborationAction(sentFromUser,
                        ACCEPT_MAP_SHARING);
                //                message = "<fmcmd cmd=\"" + ACCEPT_MAP_SHARING + "\" user=\""
                //                        + sentFromUser + "\"/>";
                mapShared = true;
            } else {
                action = createCollaborationAction(sentFromUser,
                        DECLINE_MAP_SHARING);
                //                message = "<fmcmd cmd=\"" + DECLINE_MAP_SHARING + "\"
                // user=\""
                //                        + sentFromUser + "\"/>";
                mapShared = false;
            }
            sendMessage(sendToUser, action);
        } catch (Exception e) {
            freemind.main.Resources.getInstance().logException(e);
        }
    }

    /**
     * @param action
     * @return
     */
    private String marshal(XmlAction action) {
        return controller.getController().marshall(action);
    }

    /** Sends commands to the other user(s?).
     * @param requestReceiverUser
     * @param action
     * @throws SendMessageFailedException
     * @throws ParseException
     */
    private void sendMessage(String requestReceiverUser, XmlAction action)
            throws SendMessageFailedException, ParseException {
        String message = marshal(action);
        if (!controller.isSendingEnabled()) {
            logger
                    .warning("JabberSender should not send messages. In particular the following messages is not sent:"
                            + message);
            return;
        }
        if (requestReceiverUser == null)
            throw new IllegalArgumentException(
                    "sendToUser is null. (Did you specify the user to share with by calling 'setMapShareUser'?)");
        logger.info("Sending message:"
                + ((message.length() < 100) ? message : (message
                        .substring(0, 50)
                        + "..." + message.substring(message.length() - 50))));
        /*
         * Wait until there is a reply.
         */
        chat.sendPrivateMessage(new JID(requestReceiverUser), Tools.compress(message), false);

    }

    /**
     * True if there is a shared map at present, false otherwise. If this value
     * is false, the sender will ignore requests to send Freemind commands.
     * 
     * @param shared
     */
    public void isMapShared(boolean shared) {
        mapShared = shared;
    }

    /**
     * Sets name of the user with whom the map is shared.
     * 
     * @param username
     */
    public void setShareMapUser(String username) {
        this.sendToUser = username;
    }

    /**
     * The overloaded filter action. Each action comes here along and is sent to the other 
     * participants.
     */
    public ActionPair filterAction(ActionPair pair) {
    	    try {
            CompoundAction eAction = new CompoundAction();
            eAction.getListChoiceList().add(
                    pair.getDoAction());
            eAction.getListChoiceList().add(
                    pair.getUndoAction());
            sendMessage(sendToUser, eAction);
        } catch (SendMessageFailedException e) {
            freemind.main.Resources.getInstance().logException(e);
        } catch (ParseException e) {
            freemind.main.Resources.getInstance().logException(e);
        }
        return pair;
    }

}