package freemind.controller.attributes;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import freemind.controller.filter.util.SortedListModel;

import java.awt.*;
import java.awt.event.*;
import java.util.EventObject;

public class ListDialog extends JDialog
{
    private final class TextChangeListener implements DocumentListener {
        private void update() {
            updateButtons();            
        }
        
        public void insertUpdate(DocumentEvent e) {
            update();
        }
        
        public void removeUpdate(DocumentEvent e) {
            update();
        }
        
        public void changedUpdate(DocumentEvent e) {
            update();
        }
    }
    
    private final class ListSelectionChangeListener implements ListSelectionListener{
        
        /* (non-Javadoc)
         * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
         */
        public void valueChanged(ListSelectionEvent e) {
            int minIndex = list.getMinSelectionIndex();
            int maxIndex = list.getMaxSelectionIndex();
            if(minIndex == maxIndex && minIndex != -1){
                textField.setText(data.getElementAt(minIndex).toString());
                selectText();
            }
            updateButtons();
        }
        
    }
    
    private class AddAction  implements ActionListener{
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            data.add(getCurrentText());
            addButton.setEnabled(false);
            selectText();
        }
    }
    
    private class RenameAction  implements ActionListener{
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            Object[] selectedValues = list.getSelectedValues();
            for(int i = 0; i < selectedValues.length; i++){
                if(! selectedValues[i].equals(getCurrentText())){
                    data.replace(selectedValues[i], getCurrentText());
                }
            }
            renameButton.setEnabled(false);            
            list.clearSelection();
            selectText();
        }
    }
    
    private class DeleteAction  implements ActionListener{
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            Object[] selectedValues = list.getSelectedValues();
            for(int i = 0; i < selectedValues.length; i++){
                data.remove(selectedValues[i]);
            }
            if(data.getSize() == 0){
                data.add("");
            }
            list.clearSelection();
        }
    }
    
    private class CloseAction  implements ActionListener{
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            ListDialog.dialog.setVisible(false);           
        }
    }
    
    private static ListDialog dialog;
    private JList list;
    private JButton addButton;
    private JButton renameButton;
    private JButton deleteButton;
    private JTextField textField;
    private SortedListModel data = null;
    public static void showDialog(Component frameComp,
            Component locationComp,
            String labelText,
            String title,
            SortedListModel possibleValues,
            String longValue) {
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        dialog = new ListDialog(frame,
                locationComp,
                labelText,
                title,
                possibleValues,
                longValue);
        dialog.setVisible(true);
    }
    
    /**
     * 
     */
    private void updateButtons() {
        String text = getCurrentText();
        boolean isNewText = -1 == getIndexOf(text);
        addButton.setEnabled(isNewText);
        int minSelectionIndex = list.getMinSelectionIndex();
        renameButton.setEnabled(minSelectionIndex != -1);
        deleteButton.setEnabled(minSelectionIndex != -1);
    }

    private String getCurrentText(){
        Document document = textField.getDocument();
        try {
            String text = document.getText(0, document.getLength());
            return text;
        } catch (BadLocationException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void selectText(){
        textField.requestFocus();
        textField.select(0, textField.getDocument().getLength());
    }

    private ListDialog(Frame frame,
            Component locationComp,
            String labelText,
            String title,
            final SortedListModel data,
            String longValue) {
        super(frame, title, true);
        this.data = data;
        //Create and initialize the buttons.
        final JButton closeButton = new JButton("Close");
        closeButton.setActionCommand("Close");
        closeButton.addActionListener(new CloseAction());
        getRootPane().setDefaultButton(closeButton);
        
        addButton = new JButton("Add");
        AddAction addAction = new AddAction();
        addButton.addActionListener(addAction);
        
        renameButton = new JButton("Rename");
        renameButton.addActionListener(new RenameAction());
        
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new DeleteAction());
        textField = new JTextField(20);
        textField.getDocument().addDocumentListener(new TextChangeListener());
        //main part of the dialog
        list = new JList(data) {
            //Subclass JList to workaround bug 4832765, which can cause the
            //scroll pane to not let the user easily scroll up to the beginning
            //of the list.  An alternative would be to set the unitIncrement
            //of the JScrollBar to a fixed value. You wouldn't get the nice
            //aligned scrolling, but it should work.
            public int getScrollableUnitIncrement(Rectangle visibleRect,
                    int orientation,
                    int direction) {
                int row;
                if (orientation == SwingConstants.VERTICAL &&
                        direction < 0 && (row = getFirstVisibleIndex()) != -1) {
                    Rectangle r = getCellBounds(row, row);
                    if ((r.y == visibleRect.y) && (row != 0))  {
                        Point loc = r.getLocation();
                        loc.y--;
                        int prevIndex = locationToIndex(loc);
                        Rectangle prevR = getCellBounds(prevIndex, prevIndex);
                        
                        if (prevR == null || prevR.y >= r.y) {
                            return 0;
                        }
                        return prevR.height;
                    }
                }
                return super.getScrollableUnitIncrement(
                        visibleRect, orientation, direction);
            }
        };
        
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        if (longValue != null) {
            list.setPrototypeCellValue(longValue); //get extra space
        }
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.setVisibleRowCount(-1);
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addButton.doClick(); //emulate button click
                }
            }
        });
        list.setModel(data);
        list.addListSelectionListener(new ListSelectionChangeListener());
        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setPreferredSize(new Dimension(250, 80));
        listScroller.setAlignmentX(LEFT_ALIGNMENT);
        
        //Create a container so that we can add a title around
        //the scroll pane.  Can't add a title directly to the
        //scroll pane because its background would be white.
        //Lay out the label and scroll pane from top to bottom.
        JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
        JLabel label = new JLabel(labelText);
        label.setLabelFor(list);
        listPane.add(label);
        listPane.add(Box.createRigidArea(new Dimension(0,5)));
        listPane.add(listScroller);
        listPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        //Lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(closeButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(addButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(renameButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(deleteButton);
        
        JPanel textPane = new JPanel();
        textPane.setLayout(new BoxLayout(textPane, BoxLayout.LINE_AXIS));
        textPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        textPane.add(textField);
        //Put everything together, using the content pane's BorderLayout.
        Container contentPane = getContentPane();
        
        contentPane.add(textPane, BorderLayout.PAGE_START);
        contentPane.add(listPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.PAGE_END);
        
        //Initialize values.
        updateButtons();
        pack();
        setLocationRelativeTo(locationComp);
    }
    
    private int getIndexOf(String text)
    {
        for(int i = 0; i < data.getSize(); i++){
            if(data.getElementAt(i).toString().equals(text))
                return i;
        }
        return -1;
    }
}
