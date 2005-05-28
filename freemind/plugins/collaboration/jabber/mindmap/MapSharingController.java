/*
 * Created on Mar 10, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package plugins.collaboration.jabber.mindmap;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.xml.bind.JAXBException;

import plugins.collaboration.jabber.view.MapSharingWizardView;
import freemind.controller.actions.ActionPair;
import freemind.controller.actions.generated.instance.RevertXmlAction;
import freemind.modes.mindmapmode.MindMapController;

/**
 * @author RReppel
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MapSharingController {

    //The wizard is implemented as a state machine.
    private final int STATE_NOT_CONNECTED = 1;

    private final int STATE_CONNECTED = 2;

    private final int STATE_SENT_SHARE_REQUEST_PENDING = 3;

    /** Waiting for a user to accept or decline the sharing of a map. */
    private final int STATE_RECEIVED_SHARE_REQUEST_PENDING = 4;

    private final int STATE_SHARING_MAP = 5;

    //TODO: Make Jabber server port configurable.
    private final int JABBER_SERVER_PORT = 5222;

    private int state;

    private MapSharingWizardView jabberConnectionWizardView;

    private MapSharingController mapSharingWizardController;

    private MindMapController controller;

    private JabberListener mapSharingListener;

    private JabberSender sender;

    private String mapSharingRequestingUser;

    private String frameTitle; //The title of the FreeMind window.

    //When sharing, the window is postfixed to indicate state, e.g.
    // "Connected".
    //frameTitle is used to re-set the title to its original value later.
    private boolean isSendingEnabled = true;

    /**
     * mapContent is the marshalled map of the others party. If we accept
     * sharing, this map is displayed.
     */
    private String mapContent;

    private String mapFileName;

    public MapSharingController(
            MapSharingWizardView jabberConnectionWizardView,
            MindMapController controller) {
        this.jabberConnectionWizardView = jabberConnectionWizardView;
        this.controller = controller;
        this.mapSharingWizardController = this; //Need an instance of this in
        // the listener classes,
        //to enable JabberListener to react to accepting/declining map share
        // invitations.
        setState(STATE_NOT_CONNECTED);
        addListeners();

        //TODO: Evil hack. Need to retrieve the title here for real
        // application.
        //Also, this can change when a new map is created...
        frameTitle = "Freemind";

    }

    private void addListeners() {
        jabberConnectionWizardView
                .addCloseButtonMouseListener(new CloseButtonClickListener());
        jabberConnectionWizardView
                .addNextButtonMouseListener(new NextButtonClickListener());
        jabberConnectionWizardView
                .addBackButtonMouseListener(new BackButtonClickListener());
        jabberConnectionWizardView
                .addAcceptButtonMouseListener(new AcceptButtonClickListener());
        jabberConnectionWizardView
                .addDeclineButtonMouseListener(new DeclineButtonClickListener());

    }

    private class CloseButtonClickListener implements MouseListener {

        public void mouseClicked(MouseEvent arg0) {
            //TODO: Need to set state to a previous state. Either connected or
            // not_connected.
            jabberConnectionWizardView.hide();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
         */
        public void mouseEntered(MouseEvent arg0) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
         */
        public void mouseExited(MouseEvent arg0) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
         */
        public void mousePressed(MouseEvent arg0) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
         */
        public void mouseReleased(MouseEvent arg0) {

        }
    }

    private class NextButtonClickListener implements MouseListener {

        public void mouseClicked(MouseEvent arg0) {
            if (state == STATE_CONNECTED) {
                sender = new JabberSender(mapSharingListener.getSession(),
                        MapSharingController.this);
                // register action listener:
                getController().getActionFactory().registerFilter(sender);
                if (jabberConnectionWizardView.isShareMapSelected()) {
                    setState(STATE_SENT_SHARE_REQUEST_PENDING);
                    jabberConnectionWizardView.showSelectUserDialog();
                } else {
                    //Close the wizard, wait until somebody shares a map:
                    //TODO: Information message needed.
                    jabberConnectionWizardView.hide();
                } //endif
            } else if (state == STATE_NOT_CONNECTED) {
                //TODO: Error handling - what about failed connections, etc.?
                // Should display error dialog.
                mapSharingListener = new JabberListener(controller,
                        mapSharingWizardController, jabberConnectionWizardView
                                .getServername(), JABBER_SERVER_PORT,
                        jabberConnectionWizardView.getUsername(),
                        jabberConnectionWizardView.getPassword());
                setState(STATE_CONNECTED);
                jabberConnectionWizardView.showMapSharingSelectionDialog();
            } else if (state == STATE_SENT_SHARE_REQUEST_PENDING) {
                //Ask the selected user if map sharing is acceptable.
                sender.setShareMapUser(jabberConnectionWizardView
                        .getMapShareUserName());
                sender.sendMapSharingRequest(jabberConnectionWizardView
                        .getUsername()
                        + "@" + jabberConnectionWizardView.getServername(),
                        jabberConnectionWizardView.getMapShareUserName());
                jabberConnectionWizardView.showAwaitMapSharingMessage();
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
         */
        public void mouseEntered(MouseEvent arg0) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
         */
        public void mouseExited(MouseEvent arg0) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
         */
        public void mousePressed(MouseEvent arg0) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
         */
        public void mouseReleased(MouseEvent arg0) {

        }
    }

    private class BackButtonClickListener implements MouseListener {

        public void mouseClicked(MouseEvent arg0) {
            if (state == STATE_CONNECTED) {
                setState(STATE_NOT_CONNECTED);
                jabberConnectionWizardView.showConnectToServerDialog();
            } else if (state == STATE_CONNECTED) {
                //TODO: Track previous state here (the user could have been
                // coming from the '"connect to server' dialog.)
                //For now, always return to the start state.
                setState(STATE_SENT_SHARE_REQUEST_PENDING);
                jabberConnectionWizardView.showMapSharingSelectionDialog();
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
         */
        public void mouseEntered(MouseEvent arg0) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
         */
        public void mouseExited(MouseEvent arg0) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
         */
        public void mousePressed(MouseEvent arg0) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
         */
        public void mouseReleased(MouseEvent arg0) {

        }
    }

    private class AcceptButtonClickListener implements MouseListener {

        public void mouseClicked(MouseEvent arg0) {
            if (state == STATE_RECEIVED_SHARE_REQUEST_PENDING) {
                //Indicate that the the requested map sharing has been
                // accepted:
                sender.sendMapSharingInvitationResponse(
                        jabberConnectionWizardView.getUsername() + "@"
                                + jabberConnectionWizardView.getServername(),
                        mapSharingRequestingUser, true);
                setState(STATE_SHARING_MAP);
                sender.setShareMapUser(mapSharingRequestingUser);
                sender.isMapShared(true);
                jabberConnectionWizardView.hide();
                try {
                    RevertXmlAction action = controller.revertAction
                            .createRevertXmlAction(mapContent, null,
                                    mapFileName);
                    // no undo possible.
                    ActionPair pair = new ActionPair(action, null);
                    controller.getActionFactory().executeAction(pair);
                } catch (JAXBException e) {
                    e.printStackTrace();
                }
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
         */
        public void mouseEntered(MouseEvent arg0) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
         */
        public void mouseExited(MouseEvent arg0) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
         */
        public void mousePressed(MouseEvent arg0) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
         */
        public void mouseReleased(MouseEvent arg0) {

        }
    }

    private class DeclineButtonClickListener implements MouseListener {

        public void mouseClicked(MouseEvent arg0) {
            if (state == STATE_RECEIVED_SHARE_REQUEST_PENDING) {
                setState(STATE_CONNECTED);
                sender.sendMapSharingInvitationResponse(
                        jabberConnectionWizardView.getUsername() + "@"
                                + jabberConnectionWizardView.getServername(),
                        mapSharingRequestingUser, false);
                sender.isMapShared(false);
                jabberConnectionWizardView.hide();
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
         */
        public void mouseEntered(MouseEvent arg0) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
         */
        public void mouseExited(MouseEvent arg0) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
         */
        public void mousePressed(MouseEvent arg0) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
         */
        public void mouseReleased(MouseEvent arg0) {

        }
    }

    /**
     * Called by the JabberListener to indicate that a user has accepted or
     * declined an invitation to share a map.
     * 
     * @param isAccepted
     */
    public void setMapShareRequestAccepted(String userName, boolean isAccepted) {
        if (state == STATE_SENT_SHARE_REQUEST_PENDING) {
            if (isAccepted) {
                setState(STATE_SHARING_MAP);
                sender.setShareMapUser(userName);
                sender.isMapShared(true);
                //TODO: Send current mind map to share user.
                /*
                 * MindMapXMLElement mapElement = new
                 * MindMapXMLElement(controller.getFrame()); try { StringWriter
                 * sw = new StringWriter();
                 * 
                 * sw.write(" <map version=\"" +
                 * controller.getFrame().getFreemindVersion()+"\">\n");
                 * MindMapMapModel mindMapNodeModel = (MindMapMapModel)
                 * controller.getView().getModel();
                 * mindMapNodeModel.saveInternal(sw, true); // save(sw,
                 * (MindMapMapModel) controller.getView().getModel().getRoot());
                 * sw.write(" </map>\n");
                 * 
                 * sender.sendMap(sw.getBuffer().toString()); } catch(Exception
                 * e) { e.printStackTrace(); }
                 */
                jabberConnectionWizardView.showSharingAcceptedMessage(userName);
            } else {
                setState(STATE_CONNECTED);
                sender.isMapShared(false);
                jabberConnectionWizardView.showSharingDeclinedMessage(userName);
            }
        } //endif
    }

    public void setMapSharingRequested(String username, String mapContent,
            String mapFileName) {
        setState(STATE_RECEIVED_SHARE_REQUEST_PENDING);
        mapSharingRequestingUser = username;
        this.mapContent = mapContent;
        this.mapFileName = mapFileName;
        jabberConnectionWizardView
                .showMapShareAcceptDeclineMessage(mapSharingRequestingUser);
    }

    public void showMapSharingDialogue() {
        if (state == STATE_SHARING_MAP || state == STATE_CONNECTED) {
            //TODO: Ask "Are you sure you want to stop sharing?"
            sender.sendMapSharingStopRequest();
            stopSharing();
        } else {
            jabberConnectionWizardView.showConnectToServerDialog();
        }
    }

    /**
     * Stops sharing the current map and disconnects from server.
     *  
     */
    private void stopSharing() {
        mapSharingListener.getSession().disconnect();
        sender.mapShared = false;
        setState(STATE_NOT_CONNECTED);

    }

    /**
     * Called by the JabberListener when the other user no longer shares his/her
     * map.
     * 
     * @param username
     */
    public void setSharingStopped(String username) {
        stopSharing();
        jabberConnectionWizardView.showSharingStoppedMessage(username);
    }

    /**
     * Sets the new state of the map sharing controller and updates the Freemind
     * window title to indicate the state.
     * 
     * @param newState
     */
    private void setState(int newState) {
        state = newState;

        //TODO: Hack alert. Window title setting belongs into a view...
        //Set the window title:
        switch (newState) {
        case STATE_CONNECTED:
            //TODO: Update resource files.
            controller.getFrame().setTitle(frameTitle + " - " + "Connected");
            //				controller.getFrame().getFreeMindMenuBar().getMenu(0).getItem(7).setText("Disconnect");
            break;
        case STATE_NOT_CONNECTED:
            controller.getFrame().setTitle(frameTitle);
            //TODO: Update resource files.
            //Change the "File->Share" menu item to be "File->Share"
            //				if(controller.getFrame().getFreeMindMenuBar() != null)
            //					controller.getFrame().getFreeMindMenuBar().getMenu(0).getItem(7).setText("Share");

            break;
        case STATE_SENT_SHARE_REQUEST_PENDING:
            break;
        case STATE_RECEIVED_SHARE_REQUEST_PENDING:
            break;
        case STATE_SHARING_MAP:
            //TODO: Update resource files.
            controller.getFrame().setTitle(frameTitle + " - " + "Sharing Map");
            //Change the "File->Share" menu item to be "File->Stop Sharing"
            //				if(controller.getFrame().getFreeMindMenuBar() != null)
            //					controller.getFrame().getFreeMindMenuBar().getMenu(0).getItem(7).setText("Stop
            // Sharing");
            break;
        default:

        } //endswitch
    }

    public MindMapController getController() {
        return controller;
    }

    /**
     * @param b
     */
    public void setSendingEnabled(boolean b) {
        this.isSendingEnabled = b;
    }

    public boolean isSendingEnabled() {
        return isSendingEnabled;
    }
}