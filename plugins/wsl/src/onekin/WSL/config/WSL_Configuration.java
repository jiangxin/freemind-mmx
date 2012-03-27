/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package onekin.WSL.config;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;

import freemind.extensions.ModeControllerHookAdapter;

public class WSL_Configuration extends ModeControllerHookAdapter {
	private final String existingRB = "Existing MediaWiki installation (LocalSettings file)";
	private final String newRB = "New MediaWiki installation (Download and install)";
	private final String simpleDialogDesc = "Choose an existing or new MediaWiki installation";
    JPanel jPMain;
    static JFrame frame = new JFrame("WSL Configuration");
    private static JComponent newContentPane;    
	private WSL_ExistMWConfig existMW;
    
	public void startupMapHook() {
		super.startupMapHook();
		launch();
    	
    	existMW = new WSL_ExistMWConfig();
    	existMW.setController(getController());
	 }
	
    /** Creates the GUI shown inside the frame's content pane. */
    public WSL_Configuration() {
        super();
    }

    private void initialize() {
    	jPMain = new JPanel(new BorderLayout());
        //Create the components.
        JPanel frequentPanel = createSimpleDialogBox();

        //Lay them out.
        Border padding = BorderFactory.createEmptyBorder(20,20,5,20);
        frequentPanel.setBorder(padding);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("WSL Configuration", null,
                          frequentPanel,
                          simpleDialogDesc); //tooltip text
     
        jPMain.add(tabbedPane, BorderLayout.CENTER);
        newContentPane = jPMain;		
	}

	/** Creates the panel shown by the first tab. */
    private JPanel createSimpleDialogBox() {
        final int numButtons = 2;
        JRadioButton[] radioButtons = new JRadioButton[numButtons];
        final ButtonGroup group = new ButtonGroup();

        JButton showItButton = null;

        final String existingMW = "existingMW";
        final String newMW = "newMW";

        radioButtons[0] = new JRadioButton(existingRB);
        radioButtons[0].setActionCommand(existingMW);

        radioButtons[1] = new JRadioButton(newRB);
        radioButtons[1].setActionCommand(newMW);

        for (int i = 0; i < numButtons; i++) {
            group.add(radioButtons[i]);
        }
        radioButtons[0].setSelected(true);

        showItButton = new JButton("Configure it!");
        showItButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String command = group.getSelection().getActionCommand();

                //Show WSL_LocalSettings
                if (command == existingMW) {
                	existMW.setLocalSettings();
                	destroy();

                //Show WSL_MediaWikiConfig
                } else if (command == newMW) {
                	WSL_MediaWikiConfig.launch(existMW);
//                    setLabel("Download, configure and install MediaWiki!");
                }
                return;
            }
        });

        return createPane(simpleDialogDesc + ":",
                          radioButtons,
                          showItButton);
    }
    
	public void destroy() {
		if(frame != null){
			frame.dispose();	
		}
	}
    
    /**
     * Used by createSimpleDialogBox to create a pane 
     * containing a description, a single column
     * of radio buttons, and the Show it! button.
     */
    private JPanel createPane(String description,
                              JRadioButton[] radioButtons,
                              JButton showButton) {

        int numChoices = radioButtons.length;
        JPanel box = new JPanel();
        JLabel label = new JLabel(description);

        box.setLayout(new BoxLayout(box, BoxLayout.PAGE_AXIS));
        box.add(label);

        for (int i = 0; i < numChoices; i++) {
            box.add(radioButtons[i]);
        }

        JPanel pane = new JPanel(new BorderLayout());
        pane.add(box, BorderLayout.PAGE_START);
        pane.add(showButton, BorderLayout.PAGE_END);
        return pane;
    }
    
    /**
     * Create the GUI and show it. For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Set up the window.
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //EXIT_ON_CLOSE
        //Content pane opaque
        newContentPane.setOpaque(true);
        //Set up the content pane.
        frame.setContentPane(newContentPane);
        // Frame half (height and width) to center
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        frame.setSize(width/2, height/2);
        // Center the frame
        frame.setLocationRelativeTo(null);
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
    	WSL_Configuration conf = new WSL_Configuration();
    	conf.launch();
    }

	public void launch() {
        initialize();
		//Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}
}
