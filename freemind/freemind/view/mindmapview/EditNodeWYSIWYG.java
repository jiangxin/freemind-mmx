/*FreeMind - a program for creating and viewing mindmaps
 *Copyright (C) 2000-2006  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 *See COPYING for details
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
 */
/*$Id: EditNodeWYSIWYG.java,v 1.1.4.4 2006-04-09 18:03:16 dpolivaev Exp $*/

package freemind.view.mindmapview;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.text.html.HTMLDocument;

import de.xeinfach.kafenio.KafenioPanel;
import de.xeinfach.kafenio.KafenioPanelConfiguration;
import de.xeinfach.kafenio.interfaces.KafenioPanelConfigurationInterface;
import de.xeinfach.kafenio.interfaces.KafenioPanelInterface;
import freemind.main.FreeMindMain;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.modes.ModeController;

/**
 * @author Daniel Polansky
 *
 */
public class EditNodeWYSIWYG extends EditNodeBase {

   private KeyEvent firstEvent;

static KafenioPanelConfigurationInterface kafenioPanelConfiguration;

   private static JDialog htmlEditorWindow;
   private static KafenioPanelInterface htmlEditorPanel;
   
   public EditNodeWYSIWYG
      (final NodeView node,
       final String text,
       final KeyEvent firstEvent,
       ModeController controller,
       EditControl editControl) {
      super(node, text, controller, editControl);
      this.firstEvent = firstEvent; }
   public void show() {
      // Return true if successful.
      try {
         lastEditingWasSuccessful = false;
         MapView mapView = getView();
         String title = getText("edit_long_node");
         boolean position_window_below_node = binOptionIsTrue("el__position_window_below_node");
         FreeMindMain frame = getFrame();
         if (htmlEditorPanel == null) {
            createKafenioPanel();
            htmlEditorWindow = new JDialog((JFrame)frame, title, /*modal=*/true);
            htmlEditorWindow.getContentPane().setLayout(new BorderLayout());
            htmlEditorWindow.getContentPane().add((JPanel)htmlEditorPanel, BorderLayout.CENTER);
            htmlEditorWindow.setJMenuBar(htmlEditorPanel.getJMenuBar());
         }

         htmlEditorPanel.setKafenioParent(htmlEditorWindow);

         String rule = "BODY {";
         rule += "font-family: "+node.getFont().getFamily()+";";
         rule += "font-size: "+node.getFont().getSize()+"pt;";
         if (node.getModel().isItalic()) {
            rule+="font-style: italic; "; }
         if (node.getModel().isBold()) {
            rule+="font-weight: bold; "; }
         if (node.getModel().getColor() != null) {
            rule+="color: "+Tools.colorToXml(node.getModel().getColor())+";"; }
         rule += "}";
         ((HTMLDocument)htmlEditorPanel.getTextPane().getDocument()).getStyleSheet().addRule(rule);
         
         htmlEditorPanel.setDocumentText(node.getModel().toString());

         //this.addWindowListener(this);
         //htmlEditorWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
         //htmlEditorWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

         final Tools.BooleanHolder windowClosedByX = new Tools.BooleanHolder();
         windowClosedByX.setValue(false);
         htmlEditorWindow.addWindowListener( new WindowAdapter() {
               public void windowClosing(WindowEvent e) { windowClosedByX.setValue(true); }} );

         //{ -- Set size (can be refactored to share code with long node editor)
         int preferredHeight = (int)(node.getHeight() * 1.2);
         preferredHeight =
            Math.max (preferredHeight, Integer.parseInt(frame.getProperty("el__min_default_window_height")));
         preferredHeight =
            Math.min (preferredHeight, Integer.parseInt(frame.getProperty("el__max_default_window_height")));
         int preferredWidth = (int)(node.getWidth() * 1.2);
         preferredWidth =
            Math.max (preferredWidth, Integer.parseInt(frame.getProperty("el__min_default_window_width")));
         preferredWidth =
            Math.min (preferredWidth, Integer.parseInt(frame.getProperty("el__max_default_window_width")));
         //htmlEditorPanel.getTextPane().setPreferredSize(new Dimension(preferredWidth, preferredHeight));
         //htmlEditorPanel.getSourcePane().setPreferredSize(new Dimension(preferredWidth, preferredHeight));
         htmlEditorPanel.getHTMLScrollPane().setPreferredSize(new Dimension(preferredWidth, preferredHeight));
         htmlEditorPanel.getSrcScrollPane().setPreferredSize(new Dimension(preferredWidth, preferredHeight));
         //}

         htmlEditorWindow.pack();

         //{ -- Set location (can be refactored to share code with long node editor)
         mapView.scrollNodeToVisible(node, 0);
         Point frameScreenLocation = frame.getLayeredPane().getLocationOnScreen();
         double posX = node.getLocationOnScreen().getX() - frameScreenLocation.getX();
         double posY = node.getLocationOnScreen().getY() - frameScreenLocation.getY()
            + (position_window_below_node ? node.getHeight() : 0);
         if (posX + htmlEditorWindow.getWidth() > frame.getLayeredPane().getWidth()) {
            posX = frame.getLayeredPane().getWidth() - htmlEditorWindow.getWidth(); }
         if (posY + htmlEditorWindow.getHeight() > frame.getLayeredPane().getHeight()) {
            posY = frame.getLayeredPane().getHeight() - htmlEditorWindow.getHeight(); }
         posX = ((posX < 0) ? 0 : posX) + frameScreenLocation.getX();
         posY = ((posY < 0) ? 0 : posY) + frameScreenLocation.getY();
         htmlEditorWindow.setLocation(new Double(posX).intValue(), new Double(posY).intValue());
         //}

         htmlEditorPanel.setDocumentConfirmed(false);
         htmlEditorWindow.show();

         // Returned from editing

         ((HTMLDocument)htmlEditorPanel.getTextPane().getDocument()).getStyleSheet().removeStyle("body");


         
         if (htmlEditorPanel.getDocumentConfirmed() || windowClosedByX.getValue()) {
            getEditControl().ok(Tools.unescapeHTMLUnicodeEntity
                                (htmlEditorPanel.getDocumentText())); }
         lastEditingWasSuccessful = true;
         //mapView.getModel().changeNode(node.getModel(), htmlEditorPanel.getDocumentText()); }
         //htmlEditorWindow.dispose(); // Do not dispose; reuse. Free the memory and other resources
         //return true; }
         //setBlocked(false);
         //return; }
      }
      catch (Exception ex) { // Probably class not found exception
         ex.printStackTrace();
         System.err.println("Loading of WYSIWYG HTML editor Kafenio failed. Use the other editors instead."); 
      }}
   // return false; }}
   
   protected KeyEvent getFirstEvent() {
       return firstEvent; }
   
   static private KafenioPanelInterface createKafenioPanel() throws Exception {
       createCafenioConfiguration();
       htmlEditorPanel  = new KafenioPanel(kafenioPanelConfiguration);
       htmlEditorPanel.getJToolBar1().setRollover(true);
       //htmlEditorPanel.getJToolBar2().setRollover(true);
       return htmlEditorPanel;           
   }
private static void createCafenioConfiguration() {
    String language = Resources.getInstance().getProperty("language");
       HashMap countryMap = Resources.getInstance().getCountryMap();        
       kafenioPanelConfiguration = new KafenioPanelConfiguration();
       kafenioPanelConfiguration.setImageDir("file://");
       kafenioPanelConfiguration.setDebugMode(true); 
       //kafenioPanelConfiguration.setLanguage("sk");
       //kafenioPanelConfiguration.setCountry("SK");
       kafenioPanelConfiguration.setLanguage(language);
       kafenioPanelConfiguration.setCountry((String)countryMap.get(language));
       kafenioPanelConfiguration.setCustomMenuItems("edit view font format insert table forms search tools help");
       // In the following excluded: new, open, styleselect
       kafenioPanelConfiguration.setCustomToolBar1("cut copy paste ld bold italic underline strike color left center right justify viewsource confirmcontent");
       // All available tool bar items:
       // new open save cut copy paste bold italic underline left center right justify styleselect ulist olist deindent indent anchor
       // image clearformats viewsource strike superscript subscript insertcharacter find color table
       
       kafenioPanelConfiguration.setShowToolbar2(false);
       kafenioPanelConfiguration.setProperty("escapeCloses","true");
       kafenioPanelConfiguration.setProperty("confirmRatherThanPost","true");
       //kafenioPanelConfiguration.setProperty("alternativeLanguage","en");
       //kafenioPanelConfiguration.setProperty("alternativeCountry","US");
}
}
