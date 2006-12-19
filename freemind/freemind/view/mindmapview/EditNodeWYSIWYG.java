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
/*$Id: EditNodeWYSIWYG.java,v 1.1.4.17 2006-12-19 22:22:37 dpolivaev Exp $*/

package freemind.view.mindmapview;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.text.html.HTMLDocument;

import com.lightdev.app.shtm.SHTMLPanel;

import freemind.main.FreeMindMain;
import freemind.main.HtmlTools;
import freemind.main.Tools;
import freemind.modes.ModeController;

/**
 * @author Daniel Polansky
 *
 */
public class EditNodeWYSIWYG extends EditNodeBase {
    
    private KeyEvent firstEvent;
    
    private static JDialog htmlEditorWindow;
    private class HTMLDialog extends Dialog{
        private SHTMLPanel htmlEditorPanel;
        HTMLDialog() throws Exception {
            super();
            createEditorPanel();
            getContentPane().add(htmlEditorPanel, BorderLayout.CENTER);
            adjustKeyBindings();
        }
        private SHTMLPanel createEditorPanel() throws Exception {
            if(htmlEditorPanel == null){
                htmlEditorPanel  = SHTMLPanel.createSHTMLPanel();
            }
            return htmlEditorPanel;     
        }
        /**
         * @return Returns the htmlEditorPanel.
         */
        public SHTMLPanel getHtmlEditorPanel() {
            return htmlEditorPanel;
        }
        /**
         * adjust the key bindings of the key map existing for this
         * editor pane to our needs (i.e. add actions to certain keys
         * such as tab/shift tab for caret movement inside tables, etc.)
         *
         * This method had to be redone for using InputMap / ActionMap
         * instead of Keymap.
         */
        private void adjustKeyBindings() {
            ActionMap myActionMap = htmlEditorPanel.getActionMap();
            InputMap myInputMap = htmlEditorPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
            
            final String submitActionKey = "OK";
            KeyStroke ok = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_MASK);
            myActionMap.put(submitActionKey, new SubmitAction());
            myInputMap.put(ok, submitActionKey);
            
            final String cancelActionKey = "cancel";
            KeyStroke cancel = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
            myActionMap.put(cancelActionKey, new CancelAction());
            myInputMap.put(cancel, cancelActionKey);
        }
        /* (non-Javadoc)
         * @see freemind.view.mindmapview.EditNodeBase.Dialog#close()
         */
        protected void submit() {
            super.submit();
            htmlEditorPanel.getDocument().getStyleSheet().removeStyle("body");
            if (htmlEditorPanel.needsSaving()) {
                getEditControl().ok(HtmlTools.unescapeHTMLUnicodeEntity
                        (htmlEditorPanel.getDocumentText())); }
        }
        /* (non-Javadoc)
         * @see freemind.view.mindmapview.EditNodeBase.Dialog#close()
         */
        protected void cancel() {
            super.cancel();
            htmlEditorPanel.getDocument().getStyleSheet().removeStyle("body");
        }
        
        protected boolean isChanged() {
            return htmlEditorPanel.needsSaving();
        }
    }
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
            final FreeMindMain frame = getFrame();
            if (htmlEditorWindow == null) {             
                htmlEditorWindow = new HTMLDialog();                          
            }
            final SHTMLPanel htmlEditorPanel = ((HTMLDialog)htmlEditorWindow).getHtmlEditorPanel();
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
            htmlEditorPanel.getDocument().getStyleSheet().addRule(rule);
            
            EventQueue.invokeLater(new Runnable(){
                
                public void run() {
                    String content = node.getModel().toString();
                    if (!HtmlTools.isHtmlNode(content)) {
                        content = HtmlTools.plainToHTML(content);
                    }
                    htmlEditorPanel.setCurrentDocumentContent(content);            }
                
            });
            
            
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
            htmlEditorPanel.setContentPanePreferredSize(new Dimension(preferredWidth, preferredHeight));
            //}
            
            htmlEditorWindow.pack();
            
            Tools.moveDialogToPosition(frame, htmlEditorWindow, node
                    .getLocationOnScreen());
            
            htmlEditorWindow.setVisible(true);
        }
        catch (Exception ex) { // Probably class not found exception
            freemind.main.Resources.getInstance().logException(ex);
            System.err.println("Loading of WYSIWYG HTML editor failed. Use the other editors instead."); 
        }}
    // return false; }}
    
    protected KeyEvent getFirstEvent() {
        return firstEvent; 
    }
    
}

