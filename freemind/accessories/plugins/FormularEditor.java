/*
 * Created on 30.03.2004
 *
 */
package accessories.plugins;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import freemind.extensions.PermanentNodeHookAdapter;
import freemind.main.FreeMindMain;
import freemind.main.XMLElement;

/**
 * @author foltin
 *
 */
public class FormularEditor extends PermanentNodeHookAdapter {



	private static final String XML_FORMULAR_TYPE = "TYPE";
	private abstract class FormularEntity {
		private String value;
		private String label;
		private String type;
		private String displayLabel;
		public String getValue() {
			return value;
		}

		public void setValue(String string) {
			value = string;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String string) {
			label = string;
		}

		public FormularEntity(String name, String value, String type, String display) {
			this.label=name;
			this.value=value;
			this.type=type;
			this.displayLabel = display;
		}

		public void addToPanel(JPanel panel, GridBagLayout l, int y) {
			GridBagConstraints c = new GridBagConstraints();
			c.gridx=0;
			c.gridy=y;
			c.weightx=50;
			c.weighty=100;
			c.fill=GridBagConstraints.VERTICAL;
			c.anchor=GridBagConstraints.EAST;
			JLabel jlabel = new JLabel(getLabel());
			if(getDisplayLabel()!=null)
				jlabel.setText(getDisplayLabel());
			l.setConstraints(jlabel, c);
			panel.add(jlabel);
		}

		/**
		 * @param child
		 */
		public void save(XMLElement child) {
			child.setAttribute("NAME", getLabel());
			child.setAttribute("VALUE", getValue()); 
			if(getDisplayLabel()!= null)
				child.setAttribute("DISPLAY", getDisplayLabel()); 
			child.setAttribute(XML_FORMULAR_TYPE, getType()); 
		}

		/**
		 * @return
		 */
		private String getDisplayLabel() {
			return displayLabel;
		}

		/**
		 * @param paramChild
		 */
		public FormularEntity(XMLElement paramChild) {
			this(paramChild.getStringAttribute("NAME"), 
			paramChild.getStringAttribute("VALUE"),
			paramChild.getStringAttribute(XML_FORMULAR_TYPE),
			paramChild.getStringAttribute("DISPLAY"));
		}
		/**
		 * @return
		 */
		public String getType() {
			return type;
		}

		/**
		 * @param string
		 */
		public void setDisplayLabel(String string) {
			displayLabel = string;
		}

	}
	private class StringEntity extends FormularEntity {
		public void addToPanel(JPanel panel, GridBagLayout l, int y) {
			super.addToPanel(panel,l,y);
			GridBagConstraints c = new GridBagConstraints();
			c.gridx=1;
			c.gridy=y;
			c.weightx=100;
			c.weighty=100;
			c.fill=GridBagConstraints.BOTH;
			final JTextField text = new JTextField(getValue());
			l.setConstraints(text, c);
			panel.add(text);
			text.getDocument().addDocumentListener(new DocumentListener() {
				public void insertUpdate(DocumentEvent arg0) {
					changedUpdate(arg0);					
				}

				public void removeUpdate(DocumentEvent arg0) {
					changedUpdate(arg0);					
				}

				public void changedUpdate(DocumentEvent e) {
					try {
						String text =
							e.getDocument().getText(0, e.getDocument().getLength());
						setValue(text);
						nodeChanged(getNode());
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
				}});
		}

		/**
		 * @param paramChild
		 */
		public StringEntity(XMLElement paramChild) {
			super(paramChild);
		}
	}
	private class CheckBoxEntity extends FormularEntity {
		public CheckBoxEntity(XMLElement paramChild) {
			super(paramChild);
		}
		public void addToPanel(JPanel panel, GridBagLayout l, int y) {
			super.addToPanel(panel,l,y);
			GridBagConstraints c = new GridBagConstraints();
			c.gridx=1;
			c.gridy=y;
			c.weightx=100;
			c.weighty=100;
			c.fill=GridBagConstraints.BOTH;
			final JCheckBox text = new JCheckBox();
			text.setSelected(getValue().equals("true")?true:false);
			l.setConstraints(text, c);
			panel.add(text);
			text.addItemListener(new ItemListener(){

				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.DESELECTED) {
						setValue("false");
					} else {
						setValue("true");
					}
				}});
		}

	}
	private class ChoiceEntity extends FormularEntity {
		private class ChoiceElement {
			public String enumStr;
			public String displayValue;
			public ChoiceElement(XMLElement child) {
				enumStr = child.getStringAttribute("ENUM");
				displayValue = child.getStringAttribute("DISPLAY");
			}
			/**
			 * @param enumChild
			 */
			public void save(XMLElement enumChild) {
				enumChild.setAttribute("ENUM", enumStr);
				enumChild.setAttribute("DISPLAY", displayValue);
			}
		}
		ChoiceElement[] possibles;
		public void addToPanel(JPanel panel, GridBagLayout l, int y) {
			super.addToPanel(panel,l,y);
			GridBagConstraints c = new GridBagConstraints();
			c.gridx=1;
			c.gridy=y;
			c.weightx=100;
			c.weighty=100;
			c.fill=GridBagConstraints.BOTH;
			final JComboBox choice = new JComboBox();
			int found = -1;
			for(int i = 0; i < possibles.length; ++i) {
				String itemName = possibles[i].enumStr;
				if(possibles[i].displayValue!=null)
					itemName =  possibles[i].displayValue;
				choice.addItem(itemName);
				if(possibles[i].enumStr.equals(getValue())) {
					found = i;
				}
			}
			if(found < 0) {
				throw new IllegalArgumentException("Enum "+getValue()+" not found in possibles "+possibles);
			}
			choice.setSelectedIndex(found);
			l.setConstraints(choice, c);
			panel.add(choice);
			choice.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					setValue(possibles[choice.getSelectedIndex()].enumStr);
					nodeChanged(getNode());
				}});
		}

		/**
		 * @param paramChild
		 */
		public ChoiceEntity(XMLElement paramChild) {
			super(paramChild);
			possibles = new ChoiceElement[paramChild.getChildren().size()];
			for(int i=0; i < paramChild.getChildren().size(); ++i) {
				XMLElement child = (XMLElement) paramChild.getChildren().get(i);
				if(child != null) {
					possibles[i]=new ChoiceElement(child);								
				}
			}
		}
		/* (non-Javadoc)
		 * @see accessories.plugins.FormularEditor.FormularEntity#save(freemind.main.XMLElement)
		 */
		public void save(XMLElement child) {
			super.save(child);
			for(int i = 0; i < possibles.length; ++i) {
				XMLElement enumChild = new XMLElement();
				enumChild.setName("value");
				possibles[i].save(enumChild);
				child.addChild(enumChild);
			}			
		}

	}
	private JPanel panel;
	private List   entities;
	/**
	 * 
	 */
	public FormularEditor() {
		super();
		entities = new Vector();
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#loadFrom(freemind.main.XMLElement)
	 */
	public void loadFrom(XMLElement child) {
		super.loadFrom(child);
		entities.clear();
		for(int i=0; i < child.getChildren().size(); ++i) {
			XMLElement paramChild = (XMLElement) child.getChildren().get(i);
			if(paramChild != null) {
				String type = paramChild.getStringAttribute(XML_FORMULAR_TYPE);
				if(type == null)
					continue;
				if(type.equals("string")) {
					entities.add(new StringEntity(paramChild));
				} else if(type.equals("enumerator")) {
					entities.add(new ChoiceEntity(paramChild));					
				} else if(type.equals("checkbox")) {
					entities.add(new CheckBoxEntity(paramChild));
				}
			}
		}
	}
	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#save(freemind.main.XMLElement)
	 */
	public void save(XMLElement xml) {
		super.save(xml);
		for(Iterator i=entities.iterator(); i.hasNext(); ) {
			FormularEntity entity = (FormularEntity) i.next();
			XMLElement child = new XMLElement();
			child.setName("formular");
			entity.save(child);
			xml.addChild(child);
		}
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#onLooseFocusHook()
	 */
	public void onLooseFocusHook() {
		super.onLooseFocusHook();
		if (panel != null) {
//			int y =0;
//			// remove all display components
//			for(Iterator i=entities.iterator(); i.hasNext();) {
//				FormularEntity entity = (FormularEntity) i.next();
//				entity.removeFromPanel(panel,(GridBagLayout) panel.getLayout(), y);
//				y++;
//			}
			// shut down the display:
			panel.setVisible(false);
			FreeMindMain frame = getController().getFrame();
			frame.getSouthPanel().remove(panel);
			frame.getSouthPanel().validate();
			panel = null;
		}
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#onReceiveFocusHook()
	 */
	public void onReceiveFocusHook() {
		super.onReceiveFocusHook();
		if(panel==null) {
			// panel:
			panel = new JPanel(null);
			GridBagLayout gridbag = new GridBagLayout();
			int y = 0;
			for(Iterator i=entities.iterator(); i.hasNext();) {
				FormularEntity entity = (FormularEntity) i.next();
				entity.addToPanel(panel, gridbag, y);
				y++;
			}
			panel.setLayout(gridbag);
			FreeMindMain frame = getController().getFrame();
			frame.getSouthPanel().add(panel, BorderLayout.CENTER);
			frame.getSouthPanel().validate();
		}
	}


	/* (non-Javadoc)
	 * @see freemind.extensions.MindMapHook#shutdownMapHook()
	 */
	public void shutdownMapHook() {
		onLooseFocusHook();
		super.shutdownMapHook();
	}

}
