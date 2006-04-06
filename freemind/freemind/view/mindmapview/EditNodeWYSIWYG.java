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
/*$Id: EditNodeWYSIWYG.java,v 1.1.4.2 2006-04-06 21:15:07 dpolivaev Exp $*/

package freemind.view.mindmapview;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;


import java.lang.reflect.Constructor;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.text.Style;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.html.HTMLDocument;

import freemind.main.FreeMind;
import freemind.main.FreeMindMain;
import freemind.main.Tools;
import freemind.modes.ModeController;

import de.xeinfach.kafenio.interfaces.KafenioPanelInterface;
import de.xeinfach.kafenio.interfaces.KafenioPanelConfigurationInterface;

/**
 * @author Daniel Polansky
 *
 */
public class EditNodeWYSIWYG extends EditNodeBase {

   private KeyEvent firstEvent;

   private static JDialog htmlEditorWindow;
   private static KafenioPanelConfigurationInterface kafenioPanelConfiguration;
   private static Class kafenioPanelConfigurationClass;
   private static Class kafenioPanelClass;
   private static Constructor kafenioPanelConstructor;
   private static KafenioPanelInterface htmlEditorPanel;
   private static boolean initialized = false;       
   private static HashMap countryMap;

   
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
         FreeMindMain frame = getFrame();
         MapView mapView = getView();
         String title = getText("edit_long_node");
         boolean position_window_below_node = binOptionIsTrue("el__position_window_below_node");
         if (!initialized) {

            String[] countryMapArray = new String[]{ 
               "de", "DE", "en", "UK", "en", "US", "es", "ES", "es", "MX", "fi", "FI", "fr", "FR", "hu", "HU", "it", "CH",
               "it", "IT", "nl", "NL", "no", "NO", "pt", "PT", "ru", "RU", "sl", "SI", "uk", "UA", "zh", "CN" };

            countryMap = new HashMap();
            for (int i = 0; i < countryMapArray.length; i = i + 2) {
               countryMap.put(countryMapArray[i],countryMapArray[i+1]); } 
            
            //System.err.println(countryMap);
            //frame.setProperty("language","bf");
            
            kafenioPanelConfigurationClass = Class.forName("de.xeinfach.kafenio.KafenioPanelConfiguration");
            kafenioPanelClass = Class.forName("de.xeinfach.kafenio.KafenioPanel");
            kafenioPanelConstructor = kafenioPanelClass.getConstructor( new Class[]{ KafenioPanelConfigurationInterface.class } );

            kafenioPanelConfiguration = (KafenioPanelConfigurationInterface)kafenioPanelConfigurationClass.newInstance();
            kafenioPanelConfiguration.setImageDir("file://");
            kafenioPanelConfiguration.setDebugMode(true); 
            //kafenioPanelConfiguration.setLanguage("sk");
            //kafenioPanelConfiguration.setCountry("SK");
            kafenioPanelConfiguration.setLanguage(frame.getProperty("language"));
            kafenioPanelConfiguration.setCountry((String)countryMap.get(frame.getProperty("language")));
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

            htmlEditorPanel  = (KafenioPanelInterface) kafenioPanelConstructor.newInstance(new Object[]{ kafenioPanelConfiguration });
            htmlEditorPanel.getJToolBar1().setRollover(true);
            //htmlEditorPanel.getJToolBar2().setRollover(true);

            htmlEditorWindow = new JDialog((JFrame)frame, title, /*modal=*/true);
            htmlEditorWindow.getContentPane().setLayout(new BorderLayout());
            htmlEditorWindow.getContentPane().add((JPanel)htmlEditorPanel, BorderLayout.CENTER);
            htmlEditorWindow.setJMenuBar(htmlEditorPanel.getJMenuBar());

            initialized = true; }

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
       return firstEvent; }}
