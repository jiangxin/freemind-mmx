/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2001  Joerg Mueller <joergmueller@bigfoot.com>
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
 */
/*$Id: MindMapNodeModel.java,v 1.21.14.4.4.11 2008/05/26 19:25:09 christianfoltin Exp $*/

package freemind.modes.mindmapmode;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;

import freemind.main.FreeMindMain;
import freemind.main.HtmlTools;
import freemind.modes.MindMap;
import freemind.modes.NodeAdapter;

/**
 * This class represents a single Node of a Tree. It contains direct handles to
 * its parent and children and to its view.
 */
public class MindMapNodeModel extends NodeAdapter {

	//
	// Constructors
	//

	public MindMapNodeModel(FreeMindMain frame, MindMap map) {
		this(null, frame, map);
	}

	public MindMapNodeModel(Object userObject, FreeMindMain frame, MindMap map) {
		super(userObject, frame, map);
		children = new LinkedList();
		setEdge(new MindMapEdgeModel(this, getFrame()));
	}

	//
	// The mandatory load and save methods
	//

	public String getPlainTextContent() {
		return HtmlTools.htmlToPlain(toString());
	}

	public void saveTXT(Writer fileout, int depth) throws IOException {
		String plainTextContent = getPlainTextContent();
		for (int i = 0; i < depth; ++i) {
			fileout.write("    ");
		}
		if (plainTextContent.matches(" *")) {
			fileout.write("o");
		} else {
			if (getLink() != null) {
				String link = getLink();
				if (!link.equals(plainTextContent)) {
					fileout.write(plainTextContent + " ");
				}
				fileout.write("<" + link + ">");
			} else {
				fileout.write(plainTextContent);
			}
		}

		fileout.write("\n");
		// fileout.write(System.getProperty("line.separator"));
		// fileout.newLine();

		// ^ One would rather expect here one of the above commands
		// commented out. However, it does not work as expected on
		// Windows. My unchecked hypothesis is, that the String Java stores
		// in Clipboard carries information that it actually is \n
		// separated string. The current coding works fine with pasting on
		// Windows (and I expect, that on Unix too, because \n is a Unix
		// separator). This method is actually used only for pasting
		// purposes, it is never used for writing to file. As a result, the
		// writing to file is not tested.

		// Another hypothesis is, that something goes astray when creating
		// StringWriter.

		saveChildrenText(fileout, depth);
	}

	private void saveChildrenText(Writer fileout, int depth) throws IOException {
		for (ListIterator e = sortedChildrenUnfolded(); e.hasNext();) {
			final MindMapNodeModel child = (MindMapNodeModel) e.next();
			if (child.isVisible()) {
				child.saveTXT(fileout, depth + 1);
			} else {
				child.saveChildrenText(fileout, depth);
			}
		}
	}

	public void collectColors(HashSet colors) {
		if (color != null) {
			colors.add(getColor());
		}
		for (ListIterator e = childrenUnfolded(); e.hasNext();) {
			((MindMapNodeModel) e.next()).collectColors(colors);
		}
	}

	private String saveRFT_escapeUnicodeAndSpecialCharacters(String text) {
		int len = text.length();
		StringBuffer result = new StringBuffer(len);
		int intValue;
		char myChar;
		for (int i = 0; i < len; ++i) {
			myChar = text.charAt(i);
			intValue = (int) text.charAt(i);
			if (intValue > 128) {
				result.append("\\u").append(intValue).append("?");
			} else {
				switch (myChar) {
				case '\\':
					result.append("\\\\");
					break;
				case '{':
					result.append("\\{");
					break;
				case '}':
					result.append("\\}");
					break;
				case '\n':
					result.append(" \\line ");
					break;
				default:
					result.append(myChar);
				}
			}
		}
		return result.toString();
	}

	public void saveRTF(Writer fileout, int depth, HashMap colorTable)
			throws IOException {
		String pre = "{" + "\\li" + depth * 350;
		String level;
		if (depth <= 8) {
			level = "\\outlinelevel" + depth;
		} else {
			level = "";
		}
		String fontsize = "";
		if (color != null) {
			pre += "\\cf" + ((Integer) colorTable.get(getColor())).intValue();
		}

		if (isItalic()) {
			pre += "\\i ";
		}
		if (isBold()) {
			pre += "\\b ";
		}
		if (font != null && font.getSize() != 0) {
			fontsize = "\\fs" + Math.round(1.5 * getFont().getSize());
			pre += fontsize;
		}

		pre += "{}"; // make sure setting of properties is separated from the
						// text itself

		fileout.write("\\li" + depth * 350 + level + "{}");
		if (this.toString().matches(" *")) {
			fileout.write("o");
		} else {
			String text = saveRFT_escapeUnicodeAndSpecialCharacters(this
					.getPlainTextContent());
			if (getLink() != null) {
				String link = saveRFT_escapeUnicodeAndSpecialCharacters(getLink());
				if (link.equals(this.toString())) {
					fileout.write(pre + "<{\\ul\\cf1 " + link + "}>" + "}");
				} else {
					fileout.write("{" + fontsize + pre + text + "} ");
					fileout.write("<{\\ul\\cf1 " + link + "}}>");
				}
			} else {
				fileout.write(pre + text + "}");
			}
		}

		fileout.write("\\par");
		fileout.write("\n");

		saveChildrenRTF(fileout, depth, colorTable);
	}

	private void saveChildrenRTF(Writer fileout, int depth, HashMap colorTable)
			throws IOException {
		for (ListIterator e = sortedChildrenUnfolded(); e.hasNext();) {
			final MindMapNodeModel child = (MindMapNodeModel) e.next();
			if (child.isVisible()) {
				child.saveRTF(fileout, depth + 1, colorTable);
			} else {
				child.saveChildrenRTF(fileout, depth, colorTable);
			}
		}
	}

	public boolean isWriteable() {
		return true;
	}

}
