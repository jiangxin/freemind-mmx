/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2009 Christian Foltin and others.
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
 * Created on 16.06.2009
 */
/*$Id: CompileXsdStart.java,v 1.1.2.1 2009/07/17 19:17:41 christianfoltin Exp $*/

package de.foltin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author foltin
 * 
 */
public class CompileXsdStart extends DefaultHandler {
	public static final String FREEMIND_PACKAGE = "freemind.controller.actions.generated.instance";
	private static final String DESTINATION_DIR = "binding/src/"
			+ FREEMIND_PACKAGE.replace('.', File.separatorChar);
	private static final String FREEMIND_ACTIONS_XSD = "freemind_actions.xsd";
	private static final String KEY_PACKAGE = "000_KEY_PACKAGE";
	private static final String FILE_START = "010_start";
	private static final String KEY_IMPORT_ARRAY_LIST = "020_import_array_list";
	private static final String KEY_CLASS_START = "030_CLASS_START";
	private static final String KEY_CLASS_EXTENSION = "040_CLASS_EXTENSION";
	private static final String KEY_CLASS_START2 = "050_CLASS_START2";
	private static final String KEY_CLASS_MIXED = "055_CLASS_MIXED";
	private static final String KEY_CLASS_PRIVATE_MEMBERS = "060_PRIVATE_MEMBERS";
	private static final String KEY_CLASS_GETTERS = "070_Getters";
	private static final String KEY_CLASS_SETTERS = "080_setters";
	private static final String KEY_CLASS_SINGLE_CHOICE = "090_single_choice";
	private static final String KEY_CLASS_MULTIPLE_CHOICES_MEMBERS = "100_choice_members";
	private static final String KEY_CLASS_MULTIPLE_CHOICES_SETGET = "110_choice_setget";
	private static final String KEY_CLASS_SEQUENCE = "120_sequence";
	private static final String KEY_CLASS_END = "500_CLASS_END";

	private final InputStream mInputStream;
	private XsdHandler mCurrentHandler;
	private TreeSet/* <String> */mKeyOrder = new TreeSet/* <String> */();
	private HashMap/* <String, HashMap<String, String> > */mClassMap = new HashMap/*
																					 * <
																					 * String
																					 * ,
																					 * HashMap
																					 * <
																					 * String
																					 * ,
																					 * String
																					 * >
																					 * >
																					 */();
	private StringBuffer mBindingXml = new StringBuffer();

	private HashMap/* <String, ElementTypes> */mElementMap = new HashMap/*
																		 * <String
																		 * ,
																		 * ElementTypes
																		 * >
																		 */();
	private HashMap/* <String, String> */mTypeMap = new HashMap/*
																 * <String,
																 * String>
																 */();

	private class ElementTypes {

		private final int mEnumerationId;

		public ElementTypes(int pEnumerationId) {
			mEnumerationId = pEnumerationId;
		}

		public int getId() {
			return mEnumerationId;
		}
	};

	public final int Schema_Id = 0;
	public final int ComplexType_Id = 1;
	public final int Sequence_Id = 2;
	public final int Choice_Id = 3;
	public final int Attribute_Id = 4;
	public final int ComplexContent_Id = 5;
	public final int Element_Id = 6;
	public final int Extension_Id = 7;
	public final int SimpleType_Id = 8;
	public final int Restriction_Id = 9;
	public final int Enumeration_Id = 10;
	public final int Group_Id = 11;
	ElementTypes Schema = new ElementTypes(Schema_Id);
	ElementTypes ComplexType = new ElementTypes(ComplexType_Id);
	ElementTypes Sequence = new ElementTypes(Sequence_Id);
	ElementTypes Choice = new ElementTypes(Choice_Id);
	ElementTypes Attribute = new ElementTypes(Attribute_Id);
	ElementTypes ComplexContent = new ElementTypes(ComplexContent_Id);
	ElementTypes Element = new ElementTypes(Element_Id);
	ElementTypes Extension = new ElementTypes(Extension_Id);
	ElementTypes SimpleType = new ElementTypes(SimpleType_Id);
	ElementTypes Restriction = new ElementTypes(Restriction_Id);
	ElementTypes Enumeration = new ElementTypes(Enumeration_Id);
	ElementTypes Group = new ElementTypes(Group_Id);

	public CompileXsdStart(InputStream pInputStream) {
		mInputStream = pInputStream;
		mElementMap.put("xs:schema", /* ElementTypes. */Schema);
		mElementMap.put("xs:complexType", /* ElementTypes. */ComplexType);
		mElementMap.put("xs:complexContent", /* ElementTypes. */ComplexContent);
		mElementMap.put("xs:element", /* ElementTypes. */Element);
		mElementMap.put("xs:extension", /* ElementTypes. */Extension);
		mElementMap.put("xs:choice", /* ElementTypes. */Choice);
		mElementMap.put("xs:sequence", /* ElementTypes. */Sequence);
		mElementMap.put("xs:attribute", /* ElementTypes. */Attribute);
		mElementMap.put("xs:simpleType", /* ElementTypes. */SimpleType);
		mElementMap.put("xs:restriction", /* ElementTypes. */Restriction);
		mElementMap.put("xs:enumeration", /* ElementTypes. */Enumeration);
		mElementMap.put("xs:group", /* ElementTypes. */Group);

		mTypeMap.put("xs:long", "long");
		mTypeMap.put("xs:int", "int");
		mTypeMap.put("xs:string", "String");
		mTypeMap.put("xs:boolean", "boolean");
		mTypeMap.put("xs:float", "float");
		mTypeMap.put("xs:double", "double");
	}

	public static void main(String[] args) throws Exception {
		CompileXsdStart cXS = new CompileXsdStart(new BufferedInputStream(
				new FileInputStream(FREEMIND_ACTIONS_XSD)));
		cXS.generate();
		cXS.print();
	}

	private void print() throws Exception {
		File dir = new File(DESTINATION_DIR);
		dir.mkdirs();
		// for (String className : mClassMap.keySet()) {
		for (Iterator it = mClassMap.keySet().iterator(); it.hasNext();) {
			String className = (String) it.next();
			// special handling for strange group tag.
			if (className == null)
				continue;
			HashMap/* <String, String> */classMap = (HashMap) mClassMap
					.get(className);
			// System.out.println("\nClass:" + keys);
			FileOutputStream fs = new FileOutputStream(DESTINATION_DIR + "/"
					+ className + ".java");
			// for (String orderString : mKeyOrder) {
			for (Iterator it2 = mKeyOrder.iterator(); it2.hasNext();) {
				String orderString = (String) it2.next();
				if (classMap.containsKey(orderString)) {
					String string = (String) classMap.get(orderString);
					fs.write(string.getBytes());
					// System.out.print(string);
				}
			}
			fs.close();
		}
		// write binding to disk
		if (true) {
			FileOutputStream fs = new FileOutputStream(DESTINATION_DIR
					+ "/binding.xml");
			fs.write(mBindingXml.toString().getBytes());
			fs.close();
		}
	}

	public void generate() throws ParserConfigurationException, SAXException,
			IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		mCurrentHandler = new XsdHandler(null);
		mBindingXml.setLength(0);
		mBindingXml
				.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><binding>\n");
		// introduce correct marshaling for newlines in strings:
		mBindingXml
				.append("<format type=\"java.lang.String\" serializer=\"de.foltin.StringEncoder.encode\" deserializer=\"de.foltin.StringEncoder.decode\"/>\n");
		saxParser.parse(mInputStream, this);
		mBindingXml.append("</binding>\n");
		// System.out.println(mBindingXml.toString());
	}

	private class XsdHandler extends DefaultHandler {
		XsdHandler mParent;
		String mClassName = null;
		String mExtendsClassName = null;

		public XsdHandler(XsdHandler pParent) {
			mParent = pParent;
		}

		String getClassName() {
			if (mClassName != null) {
				return mClassName;
			}
			if (mParent != null)
				return mParent.getClassName();
			else
				return null;
		}

		HashMap/* <String, String> */getClassMap() {
			String className = getClassName();
			return createClass(className);
		}

		protected void appendToClassMap(String key, String value) {
			mKeyOrder.add(key);
			HashMap/* <String, String> */classMap = getClassMap();

			if (classMap.containsKey(key)) {
				classMap.put(key, classMap.get(key) + value);
			} else {
				classMap.put(key, value);
			}
		}

		protected void addArrayListImport() {
			appendToClassMap(KEY_IMPORT_ARRAY_LIST,
					"import java.util.ArrayList;\n");
		}

		String getExtendsClassName() {
			if (mExtendsClassName != null) {
				return mExtendsClassName;
			}
			if (mParent == null) {
				return null;
			}
			return mParent.getExtendsClassName();
		}

		public void startElement(String pName, Attributes pAttributes) {

		}

		public void startElement(String pUri, String pLocalName, String pName,
				Attributes pAttributes) throws SAXException {
			super.startElement(pUri, pLocalName, pName, pAttributes);
			// System.out.print("[ " + pName + ", ");
			// for (int i = 0; i < pAttributes.getLength(); ++i) {
			// System.out.print(pAttributes.getLocalName(i) + "="
			// + pAttributes.getValue(i));
			// }
			// System.out.println("]");
			ElementTypes defaultHandlerType;
			if (mElementMap.containsKey(pName)) {
				defaultHandlerType = (ElementTypes) mElementMap.get(pName);
			} else {
				throw new IllegalArgumentException("Element " + pName
						+ " is not matched.");
			}
			XsdHandler nextHandler = null;
			switch (defaultHandlerType.getId()) {
			case Element_Id:
				nextHandler = createElementHandler();
				break;
			case ComplexType_Id:
				nextHandler = new ComplexTypeHandler(this);
				break;
			case ComplexContent_Id:
				nextHandler = new ComplexContentHandler(this);
				break;
			case Schema_Id:
				nextHandler = new SchemaHandler(this);
				break;

			case Sequence_Id:
				nextHandler = new SequenceHandler(this);
				break;
			case Choice_Id:
				nextHandler = new ChoiceHandler(this);
				break;
			case Extension_Id:
				nextHandler = new ExtensionHandler(this);
				break;
			case Attribute_Id:
				nextHandler = new AttributeHandler(this);
				break;
			case Group_Id:
				nextHandler = new GroupHandler(this);
				break;
			default:
				nextHandler = new XsdHandler(this);
				// throw new IllegalArgumentException("Wrong type " + pName);
			}
			mCurrentHandler = nextHandler;
			nextHandler.startElement(pName, pAttributes);
		}

		protected XsdHandler createElementHandler() {
			return new ComplexTypeHandler(this);
		}

		public void endElement(String pUri, String pLocalName, String pName)
				throws SAXException {
			super.endElement(pUri, pLocalName, pName);
			mCurrentHandler = mParent;
		}

	}

	private class ExtensionHandler extends XsdHandler {

		public ExtensionHandler(XsdHandler pParent) {
			super(pParent);
		}

		public void startElement(String arg0, Attributes arg1) {
			super.startElement(arg0, arg1);
			String base = arg1.getValue("base");
			mExtendsClassName = getNameFromXml(base);
			mKeyOrder.add(KEY_CLASS_EXTENSION);
			getClassMap().put(KEY_CLASS_EXTENSION,
					" extends " + mExtendsClassName);
			mBindingXml.append("    <structure map-as=\"" + base
					+ "_type\"/>\n");
			// inform parents:
			XsdHandler xsdHandlerHierarchy = this;
			do {
				if (xsdHandlerHierarchy instanceof ComplexTypeHandler) {
					ComplexTypeHandler complexHandler = (ComplexTypeHandler) xsdHandlerHierarchy;
					complexHandler.mExtendsClassName = mExtendsClassName;
				}
				xsdHandlerHierarchy = xsdHandlerHierarchy.mParent;
			} while (xsdHandlerHierarchy != null);

		}
	}

	private class SchemaHandler extends XsdHandler {

		public SchemaHandler(XsdHandler pParent) {
			super(pParent);
		}

	}

	private class ChoiceHandler extends XsdHandler {
		private boolean isSingleChoice = false;

		public ChoiceHandler(XsdHandler pParent) {
			super(pParent);
			// TODO Auto-generated constructor stub
		}

		protected XsdHandler createElementHandler() {
			// TODO Auto-generated method stub
			return new ChoiceElementHandler(this);
		}

		protected boolean isSingleChoice() {
			return isSingleChoice;
		}

		public void startElement(String arg0, Attributes arg1) {
			// TODO Auto-generated method stub
			super.startElement(arg0, arg1);
			if (arg1.getValue("maxOccurs") != null) {
				// single array list:
				isSingleChoice = true;
				appendToClassMap(
						KEY_CLASS_SINGLE_CHOICE,
						"  public void addChoice(Object choice) {\n"
								+ "    choiceList.add(choice);\n"
								+ "  }\n"
								+ "\n"
								+ "  public void addAtChoice(int position, Object choice) {\n"
								+ "    choiceList.add(position, choice);\n"
								+ "  }\n"
								+ "\n"
								+ "  public void setAtChoice(int position, Object choice) {\n"
								+ "    choiceList.set(position, choice);\n"
								+ "  }\n"
								+ "  public Object getChoice(int index) {\n"
								+ "    return (Object)choiceList.get( index );\n"
								+ "  }\n"
								+ "\n"
								+ "  public int sizeChoiceList() {\n"
								+ "    return choiceList.size();\n"
								+ "  }\n"
								+ "\n"
								+ "  public void clearChoiceList() {\n"
								+ "    choiceList.clear();\n"
								+ "  }\n"
								+ "\n"
								+ "  public java.util.List getListChoiceList() {\n"
								+ "    return java.util.Collections.unmodifiableList(choiceList);\n"
								+ "  }\n"
								+ "\n"
								+ "  protected ArrayList choiceList = new ArrayList();\n"
								+ "\n" + "");
				addArrayListImport();
				mBindingXml
						.append("    <collection field='choiceList' ordered='false'>\n");
			}
		}

		public void endElement(String arg0, String arg1, String arg2)
				throws SAXException {
			if (isSingleChoice) {
				mBindingXml.append("    </collection>\n");
			}
			super.endElement(arg0, arg1, arg2);
		}
	}

	private class ChoiceElementHandler extends XsdHandler {
		private boolean mIsSingle;

		public ChoiceElementHandler(XsdHandler pParent) {
			super(pParent);
			// TODO Auto-generated constructor stub
			if (pParent instanceof ChoiceHandler) {
				ChoiceHandler choiceParent = (ChoiceHandler) pParent;
				mIsSingle = choiceParent.isSingleChoice();

			} else {
				throw new IllegalArgumentException(
						"Hmm, parent is not a choice.");
			}
		}

		public void startElement(String arg0, Attributes arg1) {
			// TODO Auto-generated method stub
			super.startElement(arg0, arg1);
			String rawName = arg1.getValue("ref");
			String name = getNameFromXml(rawName);
			String memberName = name.substring(0, 1).toLowerCase()
					+ name.substring(1);
			if (mIsSingle) {
				mBindingXml
						.append("      <structure usage=\"optional\" map-as=\""
								+ FREEMIND_PACKAGE + "." + name + "\"/>\n");
				return;
			}
			// do multiple choices.
			appendToClassMap(KEY_CLASS_MULTIPLE_CHOICES_MEMBERS, "  protected "
					+ name + " " + memberName + ";\n\n");
			appendToClassMap(KEY_CLASS_MULTIPLE_CHOICES_SETGET, "  public "
					+ name + " get" + name + "() {\n    return this."
					+ memberName + ";\n" + "  }\n\n");
			appendToClassMap(KEY_CLASS_MULTIPLE_CHOICES_SETGET,
					"  public void set" + name + "(" + name + " value){\n"
							+ "    this." + memberName + " = value;\n"
							+ "  }\n\n");
			mBindingXml.append("    <structure field=\"" + memberName
					+ "\" usage=\"" + "optional" + "\" map-as=\""
					+ FREEMIND_PACKAGE + "." + name + "\"/>\n");
		}

	}

	private class GroupHandler extends XsdHandler {

		public GroupHandler(XsdHandler pParent) {
			super(pParent);
		}

		public void startElement(String arg0, String arg1, String arg2,
				Attributes arg3) throws SAXException {
			// super.startElement(arg0, arg1, arg2, arg3);
			// omit the output.
			mCurrentHandler = new GroupHandler(this);
		}
	}

	private class SequenceHandler extends XsdHandler {

		public SequenceHandler(XsdHandler pParent) {
			super(pParent);
			// TODO Auto-generated constructor stub
		}

		protected XsdHandler createElementHandler() {
			// TODO Auto-generated method stub
			return new SequenceElementHandler(this);
		}

	}

	private class SequenceElementHandler extends XsdHandler {

		public SequenceElementHandler(XsdHandler pParent) {
			super(pParent);
			// TODO Auto-generated constructor stub
		}

		public void startElement(String arg0, Attributes arg1) {
			// TODO Auto-generated method stub
			super.startElement(arg0, arg1);
			String rawName = arg1.getValue("name");
			String type = arg1.getValue("type");
			boolean isRef = false;
			if (rawName == null) {
				rawName = arg1.getValue("ref");
				isRef = true;
			}
			String name = getNameFromXml(rawName);
			String memberName = name.substring(0, 1).toLowerCase()
					+ name.substring(1);
			if (isRef) {
				type = name;
			} else {
				type = getType(type);
			}
			String maxOccurs = arg1.getValue("maxOccurs");
			String minOccurs = arg1.getValue("minOccurs");
			if (maxOccurs != null && maxOccurs.trim().equals("1")) {
				// single ref:
				appendToClassMap(KEY_CLASS_MULTIPLE_CHOICES_MEMBERS,
						"  protected " + type + " " + memberName + ";\n\n");
				appendToClassMap(KEY_CLASS_MULTIPLE_CHOICES_SETGET, "  public "
						+ type + " get" + name + "() {\n    return this."
						+ memberName + ";\n" + "  }\n\n");
				appendToClassMap(KEY_CLASS_MULTIPLE_CHOICES_SETGET,
						"  public void set" + name + "(" + type + " value){\n"
								+ "    this." + memberName + " = value;\n"
								+ "  }\n\n");
				String optReq = "optional";
				if (minOccurs != null && minOccurs.trim().equals("1")) {
					optReq = "required";
				}
				if (isRef) {
					mBindingXml.append("      <structure field=\"" + memberName
							+ "\" usage=\"" + optReq + "\" map-as=\""
							+ FREEMIND_PACKAGE + "." + type + "\"/>\n");
				} else {
					mBindingXml.append("      <value name=\"" + rawName
							+ "\" field=\"" + memberName + "\" usage=\""
							+ optReq + "\"/>\n");
					// whitespace='preserve' doesn't work
				}
			} else {
				// list ref:
				appendToClassMap(KEY_CLASS_SEQUENCE, "  public void add" + name
						+ "(" + name + " " + memberName + ") {\n" + "    "
						+ memberName + "List.add(" + memberName + ");\n"
						+ "  }\n" + "\n" + "  public void addAt" + name
						+ "(int position, " + name + " " + memberName + ") {\n"
						+ "    " + memberName + "List.add(position, "
						+ memberName + ");\n" + "  }\n" + "\n" + "  public "
						+ name + " get" + name + "(int index) {\n"
						+ "    return (" + name + ")" + memberName
						+ "List.get( index );\n" + "  }\n" + "\n"
						+ "  public void removeFrom" + name
						+ "ElementAt(int index) {\n" + "    " + memberName
						+ "List.remove( index );\n" + "  }\n" + "\n"
						+ "  public int size" + name + "List() {\n"
						+ "    return " + memberName + "List.size();\n"
						+ "  }\n" + "\n" + "  public void clear" + name
						+ "List() {\n" + "    " + memberName
						+ "List.clear();\n" + "  }\n" + "\n"
						+ "  public java.util.List getList" + name
						+ "List() {\n"
						+ "    return java.util.Collections.unmodifiableList("
						+ memberName + "List);\n" + "  }\n"
						+ "    protected ArrayList " + memberName
						+ "List = new ArrayList();\n\n");
				addArrayListImport();
				mBindingXml.append("    <collection field=\"" + memberName
						+ "List\">\n" + "      <structure map-as=\""
						+ FREEMIND_PACKAGE + "." + name + "\"/>\n"
						+ "    </collection>\n");
			}
		}

	}

	private class ComplexTypeHandler extends XsdHandler {

		private boolean mIsClassDefinedHere = false;
		private String mRawName;
		private boolean mMixed = false;

		public ComplexTypeHandler(XsdHandler pParent) {
			super(pParent);
		}

		public void startElement(String arg0, Attributes arg1) {
			super.startElement(arg0, arg1);

			String mixed = arg1.getValue("mixed");
			if ("true".equals(mixed)) {
				// in case of mixed content (those with additional cdata
				// content), we add a "content" field to the class
				mMixed = true;
			}
			if (getClassName() == null) {
				mRawName = startClass(arg1);
				// make binding:
				mBindingXml.append("  <mapping class='" + FREEMIND_PACKAGE
						+ "." + mClassName + "' type-name='" + mRawName
						+ "_type' abstract='true'>\n");
				mIsClassDefinedHere = true;
			}
		}

		/**
		 * @param arg1
		 * @return the class name
		 */
		protected String startClass(Attributes arg1) {
			mKeyOrder.add(FILE_START);
			mKeyOrder.add(KEY_PACKAGE);
			mKeyOrder.add(KEY_CLASS_START);
			mKeyOrder.add(KEY_CLASS_END);
			String rawName = arg1.getValue("name");
			String name = getNameFromXml(rawName);
			HashMap/* <String, String> */class1 = createClass(name);
			mClassName = name;
			class1.put(FILE_START, "/* " + name + "...*/\n");
			class1.put(KEY_PACKAGE, "package " + FREEMIND_PACKAGE + ";\n");
			class1.put(KEY_CLASS_START, "public class " + name);
			mKeyOrder.add(KEY_CLASS_START2);
			class1.put(KEY_CLASS_START2, " {\n");
			if (mMixed) {
				mKeyOrder.add(KEY_CLASS_MIXED);
				class1.put(
						KEY_CLASS_MIXED,
						" public String content; public String getContent(){return content;} public void setContent(String content){this.content = content;}\n");
			}
			class1.put(KEY_CLASS_END, "} /* " + name + "*/\n");
			return rawName;
		}

		public void endElement(String arg0, String arg1, String arg2)
				throws SAXException {
			if (mIsClassDefinedHere) {
				String extendString = "";
				if (getExtendsClassName() != null) {
					extendString = " extends=\"" + FREEMIND_PACKAGE + "."
							+ getExtendsClassName() + "\"";
				}
				if (mMixed) {
					mBindingXml
							.append("     <value field='content' style='text'/>\n");
				}
				mBindingXml.append("  </mapping>\n" + "  <mapping name=\""
						+ mRawName + "\"" + extendString + " class=\""
						+ FREEMIND_PACKAGE + "." + mClassName
						+ "\"><structure map-as=\"" + mRawName
						+ "_type\"/></mapping>\n" + "\n");
			}
			super.endElement(arg0, arg1, arg2);
		}
	}

	private class ComplexContentHandler extends XsdHandler {

		public ComplexContentHandler(XsdHandler pParent) {
			super(pParent);
		}

	}

	private class AttributeHandler extends XsdHandler {

		public AttributeHandler(XsdHandler pParent) {
			super(pParent);
		}

		public void startElement(String arg0, Attributes arg1) {
			super.startElement(arg0, arg1);
			String type = arg1.getValue("type");
			type = getType(type);
			String rawName = arg1.getValue("name");
			String usage = arg1.getValue("use");
			String minOccurs = arg1.getValue("minOccurs");
			String name = arg1.getValue("id");
			if (name == null) {
				name = getNameFromXml(rawName);
			}
			String memberName = decapitalizeFirstLetter(name);
			appendToClassMap(KEY_CLASS_PRIVATE_MEMBERS, "  protected " + type
					+ " " + memberName + ";\n");
			appendToClassMap(KEY_CLASS_GETTERS, "  public " + type + " get"
					+ name + "(){\n" + "    return " + memberName + ";\n"
					+ "  }\n");
			appendToClassMap(KEY_CLASS_SETTERS, "  public void set" + name
					+ "(" + type + " value){\n" + "    this." + memberName
					+ " = value;\n" + "  }\n");
			mBindingXml.append("    <value name='" + rawName + "' field='"
					+ memberName + "' " + "usage='"
					+ (("required".equals(usage)) ? "required" : "optional")
					+ "' "
					+ (("0".equals(minOccurs)) ? "" : "style='attribute'")
					+ "/>\n");
			// whitespace='preserve' doesn't work
		}

		public String decapitalizeFirstLetter(String name) {
			return name.substring(0, 1).toLowerCase() + name.substring(1);
		}

	}

	public void endElement(String pUri, String pLocalName, String pName)
			throws SAXException {
		mCurrentHandler.endElement(pUri, pLocalName, pName);
	}

	public HashMap/* <String, String> */createClass(String pName) {
		if (mClassMap.containsKey(pName)) {
			return (HashMap) mClassMap.get(pName);
		}
		HashMap/* <String, String> */newValue = new HashMap/* <String, String> */();
		mClassMap.put(pName, newValue);
		return newValue;
	}

	public void startElement(String pUri, String pLocalName, String pName,
			Attributes pAttributes) throws SAXException {
		mCurrentHandler.startElement(pUri, pLocalName, pName, pAttributes);
	}

	public String firstLetterCapitalized(String text) {
		if (text == null || text.length() == 0) {
			return text;
		}
		return text.substring(0, 1).toUpperCase()
				+ text.substring(1, text.length());
	}

	private String getNameFromXml(String pXmlString) {
		StringTokenizer st = new StringTokenizer(pXmlString, "_");
		String result = "";
		while (st.hasMoreTokens()) {
			result += firstLetterCapitalized(st.nextToken());
		}
		return result;
	}

	private String getType(String type) {
		if (mTypeMap.containsKey(type)) {
			type = (String) mTypeMap.get(type);
		} else {
			// FIXME: Bad hack for tokens:
			type = "String";
			// throw new IllegalArgumentException("Unknown type " + type);
		}
		return type;
	}

}
