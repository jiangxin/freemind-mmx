package freemind.main;

/*
 * XHTMLWriter -- A simple XHTML document writer
 * 
 * (C) 2004 Richard "Shred" Koerber
 *   http://www.shredzone.net/
 *
 * This is free software. You can modify and use it at will.
 */

import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.Option;

/**
 * Create a new XHTMLWriter which is able to save a HTMLDocument as XHTML.
 * <p>
 * The result will be a valid XML file, but it is not granted that the file will
 * really be XHTML 1.0 transitional conform. The basic purpose of this class
 * is to give an XSL processor access to plain HTML files.
 * 
 * @author Richard "Shred" K�rber
 */
public class XHTMLWriter extends FixedHTMLWriter {
	private boolean writeLineSeparatorEnabled = true;

	/**
	 * Create a new XHTMLWriter that will write the entire HTMLDocument.
	 * 
	 * @param writer
	 *            Writer to write to
	 * @param doc
	 *            Source document
	 */
	public XHTMLWriter(Writer writer, HTMLDocument doc) {
		this(writer, doc, 0, doc.getLength());
	}

	/**
	 * Create a new XHTMLWriter that will write a part of a HTMLDocument.
	 * 
	 * @param writer
	 *            Writer to write to
	 * @param doc
	 *            Source document
	 * @param pos
	 *            Starting position
	 * @param len
	 *            Length
	 */
	public XHTMLWriter(Writer writer, HTMLDocument doc, int pos, int len) {
		super(new XHTMLFilterWriter(writer), doc, pos, len);
		setLineLength(Integer.MAX_VALUE);
	}

	/**
	 * Start the writing process. An XML and DOCTYPE header will be written
	 * prior to the XHTML output.
	 */
	public void write() throws IOException, BadLocationException {
		// fc, 17.5.06: no special tags, as they are wrong inside XML tags like
		// <content>...<html>...
		// write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
		// writeLineSeparator();
		// write( "<!DOCTYPE html PUBLIC \"-//W3C//"
		// + "DTD XHTML 1.0 Transitional//EN\" "
		// + "\"DTD/xhtml1-transitional.dtd\">" );
		// writeLineSeparator();
		super.write();
	}

	protected void writeOption(Option option) throws IOException {
		writeLineSeparatorEnabled = false;
		super.writeOption(option);
		writeLineSeparatorEnabled = true;
		write("</option>");
		writeLineSeparator();
	}

	protected void writeLineSeparator() throws IOException {
		if (writeLineSeparatorEnabled)
			super.writeLineSeparator();
	}

	/**
	 * Read HTML from the Reader, and send XHTML to the writer. Common mistakes
	 * in the HTML code will also be corrected. The result is pretty-printed.
	 * 
	 * @param reader
	 *            HTML source
	 * @param writer
	 *            XHTML target
	 */
	public static void html2xhtml(Reader reader, Writer writer)
			throws IOException, BadLocationException {
		// --- Create a HTML document ---
		HTMLEditorKit kit = new HTMLEditorKit();
		Document doc = kit.createDefaultDocument();

		// --- Read the HTML source ---
		kit.read(reader, doc, doc.getLength());

		// --- Write the content ---
		XHTMLWriter xhw = new XHTMLWriter(writer, (HTMLDocument) doc);
		xhw.write();
	}

	/**
	 * External call to convert a source HTML file to a target XHTML file.
	 * <p>
	 * Usage: <tt>java XHTMLWriter &lt;source file&gt; &lt;target file&gt;</tt>
	 * 
	 * @param args
	 *            Shell arguments
	 */
	public static void main(String[] args) {
		try {
			FileReader reader = new FileReader(args[0]);
			FileWriter writer = new FileWriter(args[1]);
			html2xhtml(reader, writer);
			writer.close();
			reader.close();
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
		}
	}

	/**
	 * This FilterWriter will convert the output of Swing's HTMLWriter to XHTML
	 * format. This is done by converting tags like &lt;br&gt; to
	 * &lt;br&nbsp;/&gt;. Also, special characters in tag attributes are
	 * escaped.
	 * <p>
	 * This filter relies on known flaws of the HTMLWriter. It is known to work
	 * with Java 1.4, but might not work with future Java releases.
	 */
	public static class XHTMLFilterWriter extends FilterWriter {
		private boolean insideTag = false; // We're inside a tag
		private boolean insideValue = false; // We're inside an attribute value
		private boolean readTag = false; // We're reading the tag name
		private String tag = ""; // Collector for the tag name

		/**
		 * Create a new XHTMLFilterWriter.
		 * 
		 * @param writer
		 *            Writer to write to
		 */
		public XHTMLFilterWriter(Writer writer) {
			super(writer);
		}

		/**
		 * Write a single char to the Writer.
		 * 
		 * @param c
		 *            Char to be written
		 */
		public void write(int c) throws IOException {
			if (insideValue) {
				// We're currently within a tag attribute's value.
				// Take care for proper HTML escaping.
				if (c == '&') {
					super.write("&amp;", 0, 5);
					return;
				} else if (c == '<') {
					super.write("&lt;", 0, 4);
					return;
				} else if (c == '>') {
					super.write("&gt;", 0, 4);
					return;
				} else if (c == '"') { // leaving the value
					insideValue = false;
				}
			} else if (insideTag) {
				// We're inside a tag. Add a slash to the closing tag bracket
				// for
				// certain tags (like img, br, hr, input, ... ).
				if (readTag) {
					if (c == ' ' || c == '>') { // tag name ends
						readTag = false;
					} else {
						tag += (char) c; // collect tag name here
					}
				}
				if (c == '"') { // attribute value begins
					insideValue = true;
				} else if (c == '>') { // check if this is a "certain tag"
					if (tag.equals("img") || tag.equals("br")
							|| tag.equals("hr") || tag.equals("input")
							|| tag.equals("meta") || tag.equals("link")
							|| tag.equals("area") || tag.equals("base")
							|| tag.equals("basefont") || tag.equals("frame")
							|| tag.equals("iframe") || tag.equals("col")) {
						super.write(" /"); // add slash to the closing bracket
					}
					insideTag = false;
					readTag = false;
				}
			} else if (c == '<') {
				// We're just at the very beginning of a tag.
				tag = "";
				insideTag = true;
				readTag = true;
			}
			super.write(c);
		}

		/**
		 * Write a char array to the Writer.
		 * 
		 * @param cbuf
		 *            Char array to be written
		 * @param off
		 *            Start offset within the array
		 * @param len
		 *            Number of chars to be written
		 */
		public void write(char[] cbuf, int off, int len) throws IOException {
			while (len-- > 0) {
				write((int) cbuf[off++]);
			}
		}

		/**
		 * Write a String to the Writer.
		 * 
		 * @param str
		 *            String to be written
		 * @param off
		 *            Start offset within the String
		 * @param len
		 *            Number of chars to be written
		 */
		public void write(String str, int off, int len) throws IOException {
			write(str.toCharArray(), off, len);
		}

	}
}