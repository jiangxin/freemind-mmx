/*
 * Created on Mar 4, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package plugins.collaboration.jabber.mindmap;

import javax.xml.bind.JAXBException;

import com.echomine.jabber.JID;
import com.echomine.jabber.JabberChatService;
import com.echomine.jabber.JabberSession;

import freemind.controller.actions.ActionFilter;
import freemind.controller.actions.ActionPair;
import freemind.controller.actions.generated.instance.CompoundAction;

/**
 * @author RReppel
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class JabberSender implements ActionFilter {

    public final static String KEYSTROKE_NEWMAP = "keystroke_newMap";

    public final static String REQUEST_MAP_SHARING = "request_map_sharing";

    public final static String ACCEPT_MAP_SHARING = "accept_map_sharing";

    public final static String DECLINE_MAP_SHARING = "decline_map_sharing";

    public final static String STOP_MAP_SHARING = "stop_map_sharing";

    public final static String LOAD_MAP = "load_map";

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
            e.printStackTrace();
        }
    }

    /**
     * Sends a FreeMind command consisting of a single key to the Jabber server.
     * Examples: INSERT, F2, ALT-H, .... The valid parameters are a subset of
     * the command names which are defined in the freemind.properties file, for
     * example "keystroke_add_child"
     * 
     * @param key
     */
    public void sendCommand(String key) {
        if (mapShared) {
            String message = "<fmcmd cmd=\"" + key + "\" user=\""
                    + session.getContext().getUsername() + "@"
                    + session.getContext().getServerName() + "\"/>";
            sendMessage(message);
        }
    }

    //	/**
    //	 * Updates the text of the currently selected node.
    //	 * @param text
    //	 */
    //    /**
    //     * @param text
    //     */
    //    public void setNodeText(String text) {
    //		if(mapShared) {
    //	  		// Example: <fmcmd cmd="set_node_text" param="This is the text for the
    // node."/>
    //			try {
    //				if(sendToUser == null)
    //					throw new Exception("sendToUser is null.");
    //				else
    //					chat.sendPrivateMessage(new JID(sendToUser), "<fmcmd cmd=\"" +
    // SET_NODE_TEXT + "\" param=\"" + text + "\"/>", false);
    //			}
    //			catch(Exception e) {
    //				e.printStackTrace();
    //			}
    //		} //endif
    //    	   
    //    }

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
            String message = "<fmcmd cmd=\"" + REQUEST_MAP_SHARING
                    + "\" user=\"" + requestingUser + "\"/>";
            chat.sendPrivateMessage(new JID(requestReceiverUser), message,
                    false); //Wait until there is a reply.
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            if (accepted) {
                message = "<fmcmd cmd=\"" + ACCEPT_MAP_SHARING + "\" user=\""
                        + sentFromUser + "\"/>";
                mapShared = true;
            } else {
                message = "<fmcmd cmd=\"" + DECLINE_MAP_SHARING + "\" user=\""
                        + sentFromUser + "\"/>";
                mapShared = false;
            }
            chat.sendPrivateMessage(new JID(sendToUser), message, false); //Wait
            // until
            // there
            // is a
            // reply.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //	/**
    //	 * Sends the location of a selected node.
    //	 * Example: <fmcmd cmd="select_single_node" x="260" y="1582" />
    //	 * @param x
    //	 * @param y
    //	 */
    //	public void sendSelectedNodeLocation(int x, int y) {
    //		if(mapShared) {
    //			try {
    //				String message = "<fmcmd cmd=\"" + SELECT_SINGLE_NODE + "\" x=\"" + x +
    // "\" y=\"" + y + "\"/>";
    //				chat.sendPrivateMessage(new JID(sendToUser), message , false);
    //			}
    //			catch(Exception e) {
    //				e.printStackTrace();
    //			}
    //		} //endif
    //	}

    public void sendMap(String mapXml) {
        if (mapShared) {
            try {
                String message = "<fmcmd cmd=\"" + LOAD_MAP + "\">" + mapXml
                        + "</fmcmd>";
                chat.sendPrivateMessage(new JID(sendToUser), message, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } //endif
    }

    private void sendMessage(String message) {
        try {
            if (!controller.isSendingEnabled()) {
                return;
            }
            if (sendToUser == null)
                throw new Exception(
                        "sendToUser is null. (Did you specify the user to share with by calling 'setMapShareUser'?");
            else {
                logger.info("Sending message:"
                        + ((message.length() < 50) ? message : (message
                                .substring(0, 25)
                                + "..." + message
                                .substring(message.length() - 25))));
                chat.sendPrivateMessage(new JID(sendToUser), message, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public ActionPair filterAction(ActionPair pair) {
        try {
            CompoundAction eAction = controller.getController()
                    .getActionXmlFactory().createCompoundAction();
            eAction.getCompoundActionOrSelectNodeActionOrCutNodeAction().add(
                    pair.getDoAction());
            eAction.getCompoundActionOrSelectNodeActionOrCutNodeAction().add(
                    pair.getUndoAction());
            String marshalledString = controller.getController().marshall(
                    eAction);
            sendMessage(marshalledString);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return pair;
    }

}