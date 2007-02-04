/*
 * Created on 04.02.2007
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.modes.mindmapmode;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import freemind.main.HtmlTools;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.modes.MindIcon;

class MindMapHTMLWriter {
    private Writer fileout;
    private static String el = System.getProperty("line.separator");
    private boolean writeFoldingCode;
    private boolean basedOnHeadings;

    MindMapHTMLWriter(Writer fileout) {
        this.fileout = fileout;
    }

     private static String convertSpecialChar(char c) {
        String cvt;

        // try {
        // // Create the encoder and decoder for ISO-8859-1
        // Charset ansi = Charset.forName("windows-1252");
        // CharsetDecoder decoder = ansi.newDecoder();
        //
        // Charset utf8 = Charset.forName("UTF-8");
        // CharsetEncoder encoder = utf8.newEncoder();
        //
        // // The new ByteBuffer is ready to be read.
        // ByteBuffer bb = ByteBuffer.allocate(2);
        // bb.putChar(c);
        // CharBuffer cb = decoder.decode(bb);
        //          
        // cvt = cvt + cb.toString();
        // } catch (Exception e) {
        // //cvt = "CHAR ENC FAILED " + e.getMessage();
        // cvt = cvt + "&#" + Character.toString(c) + ";";
        // }

        switch ((int) c) {
        case 0xe4:
            cvt = "&auml;";
            break;
        case 0xf6:
            cvt = "&ouml;";
            break;
        case 0xfc:
            cvt = "&uuml;";
            break;
        case 0xc4:
            cvt = "&Auml;";
            break;
        case 0xd6:
            cvt = "&Ouml;";
            break;
        case 0xdc:
            cvt = "&Uuml;";
            break;
        case 0xdf:
            cvt = "&szlig;";
            break;
        default:
            cvt = "&#" + Integer.toString((int) c) + ";";
            break;
        }

        return cvt;
    }

    private static String saveHTML_escapeUnicodeAndSpecialCharacters(
            String text) {
        int len = text.length();
        StringBuffer result = new StringBuffer(len);
        int intValue;
        char myChar;
        boolean previousSpace = false;
        boolean spaceOccured = false;
        for (int i = 0; i < len; ++i) {
            myChar = text.charAt(i);
            intValue = (int) text.charAt(i);
            if (intValue >= 128) {
                result.append(convertSpecialChar(myChar));
            }
            else {
                spaceOccured = false;
                switch (myChar) {
                case '&':
                    result.append("&amp;");
                    break;
                case '<':
                    result.append("&lt;");
                    break;
                case '>':
                    result.append("&gt;");
                    break;
                case ' ':
                    spaceOccured = true;
                    if (previousSpace) {
                        result.append("&nbsp;");
                    }
                    else {
                        result.append(" ");
                    }
                    break;
                case '\n':
                    result.append("\n<br />\n");
                    break;
                default:
                    result.append(myChar);
                }
                previousSpace = spaceOccured;
            }
        }
        return result.toString();
    }
    
    void saveHTML(List mindMapNodes) throws IOException {
        writeFoldingCode = false;
        basedOnHeadings = false;
        fileout.write("<html>" + el + "<head>" + el);
        writeStyle();
        fileout.write(el + "</head>" + el 
                + "<body>" + el);
        Iterator iterator = mindMapNodes.iterator();
        while(iterator.hasNext()){
            MindMapNodeModel node = (MindMapNodeModel)iterator.next();
            saveHTML(node, "1", 0, /* isRoot */true, /* treatAsParagraph */
                    true, /* depth */1);                
        }
        fileout.write("</body>" + el);
        fileout.write("</html>" + el);
        fileout.close();
    }

    void saveHTML(MindMapNodeModel rootNodeOfBranch)
            throws IOException {
        // When isRoot is true, rootNodeOfBranch will be exported as folded
        // regardless his isFolded state in the mindmap.
        // We do all the HTML saving using just ordinary output.

        String htmlExportFoldingOption = getProperty("html_export_folding");
        writeFoldingCode = (htmlExportFoldingOption
                .equals("html_export_fold_currently_folded") && rootNodeOfBranch
                .hasFoldedStrictDescendant())
                || htmlExportFoldingOption.equals("html_export_fold_all");
        basedOnHeadings = (getProperty("html_export_folding")
                .equals("html_export_based_on_headings"));
        
        fileout.write("<html>" + el + "<head>" + el);
        fileout
                .write("<title>"
                        + saveHTML_escapeUnicodeAndSpecialCharacters(rootNodeOfBranch
                                .getPlainTextContent()) + "</title>" + el);
        
        writeStyle();
        fileout.write(el + "</head>" + el 
                + "<body>" + el);
        
        if (writeFoldingCode) {
            writeBodyWithFolding(rootNodeOfBranch);
        }
        else{
            saveHTML(rootNodeOfBranch, "1", 0, /* isRoot */true, /* treatAsParagraph */
                    true, /* depth */1);                
        }
        fileout.write("</body>" + el);
        fileout.write("</html>" + el);
        fileout.close();
    }

    private void writeBodyWithFolding(MindMapNodeModel rootNodeOfBranch) throws IOException {
        fileout.write(""
                        + el
                        + "<script language=\"JavaScript\">"
                        + el
                        + "   // Here we implement folding. It works fine with MSIE5.5, MSIE6.0 and"
                        + el
                        + "   // Mozilla 0.9.6."
                        + el
                        + ""
                        + el
                        + "   if (document.layers) {"
                        + el
                        + "      //Netscape 4 specific code"
                        + el
                        + "      pre = 'document.';"
                        + el
                        + "      post = ''; }"
                        + el
                        + "   if (document.getElementById) {"
                        + el
                        + "      //Netscape 6 specific code"
                        + el
                        + "      pre = 'document.getElementById(\"';"
                        + el
                        + "      post = '\").style'; }"
                        + el
                        + "   if (document.all) {"
                        + el
                        + "      //IE4+ specific code"
                        + el
                        + "      pre = 'document.all.';"
                        + el
                        + "      post = '.style'; }"
                        + el
                        + ""
                        + el
                        + "function layer_exists(layer) {"
                        + el
                        + "   try {"
                        + el
                        + "      eval(pre + layer + post);"
                        + el
                        + "      return true; }"
                        + el
                        + "   catch (error) {"
                        + el
                        + "      return false; }}"
                        + el
                        + ""
                        + el
                        + "function show_layer(layer) {"
                        + el
                        + "   eval(pre + layer + post).position = 'relative'; "
                        + el
                        + "   eval(pre + layer + post).visibility = 'visible'; }"
                        + el
                        + ""
                        + el
                        + "function hide_layer(layer) {"
                        + el
                        + "   eval(pre + layer + post).visibility = 'hidden';"
                        + el
                        + "   eval(pre + layer + post).position = 'absolute'; }"
                        + el
                        + ""
                        + el
                        + "function hide_folder(folder) {"
                        + el
                        + "    hide_folding_layer(folder)"
                        + el
                        + "    show_layer('show'+folder);"
                        + el
                        + ""
                        + el
                        + "    scrollBy(0,0); // This is a work around to make it work in Browsers (Explorer, Mozilla)"
                        + el
                        + "}"
                        + el
                        + ""
                        + el
                        + "function show_folder(folder) {"
                        + el
                        + "    // Precondition: all subfolders are folded"
                        + el
                        + ""
                        + el
                        + "    show_layer('hide'+folder);"
                        + el
                        + "    hide_layer('show'+folder);"
                        + el
                        + "    show_layer('fold'+folder);"
                        + el
                        + ""
                        + el
                        + "    scrollBy(0,0); // This is a work around to make it work in Browsers (Explorer, Mozilla)"
                        + el
                        + ""
                        + el
                        + "    var i;"
                        + el
                        + "    for (i=1; layer_exists('fold'+folder+'_'+i); ++i) {"
                        + el
                        + "       show_layer('show'+folder+'_'+i); }"
                        + el
                        + "}"
                        + el
                        + ""
                        + "function show_folder_completely(folder) {"
                        + el
                        + "    // Precondition: all subfolders are folded"
                        + el
                        + ""
                        + el
                        + "    show_layer('hide'+folder);"
                        + el
                        + "    hide_layer('show'+folder);"
                        + el
                        + "    show_layer('fold'+folder);"
                        + el
                        + ""
                        + el
                        + "    scrollBy(0,0); // This is a work around to make it work in Browsers (Explorer, Mozilla)"
                        + el
                        + ""
                        + el
                        + "    var i;"
                        + el
                        + "    for (i=1; layer_exists('fold'+folder+'_'+i); ++i) {"
                        + el
                        + "       show_folder_completely(folder+'_'+i); }"
                        + el
                        + "}"
                        + el
                        + ""
                        + el
                        + ""
                        + el
                        + ""
                        + el
                        + "function hide_folding_layer(folder) {"
                        + el
                        + "   var i;"
                        + el
                        + "   for (i=1; layer_exists('fold'+folder+'_'+i); ++i) {"
                        + el
                        + "       hide_folding_layer(folder+'_'+i); }"
                        + el
                        + ""
                        + el
                        + "   hide_layer('hide'+folder);"
                        + el
                        + "   hide_layer('show'+folder);"
                        + el
                        + "   hide_layer('fold'+folder);"
                        + el
                        + ""
                        + el
                        + "   scrollBy(0,0); // This is a work around to make it work in Browsers (Explorer, Mozilla)"
                        + el
                        + "}"
                        + el
                        + ""
                        + el
                        + "function fold_document() {"
                        + el
                        + "   var i;"
                        + el
                        + "   var folder = '1';"
                        + el
                        + "   for (i=1; layer_exists('fold'+folder+'_'+i); ++i) {"
                        + el
                        + "       hide_folder(folder+'_'+i); }"
                        + el
                        + "}"
                        + el
                        + ""
                        + el
                        + "function unfold_document() {"
                        + el
                        + "   var i;"
                        + el
                        + "   var folder = '1';"
                        + el
                        + "   for (i=1; layer_exists('fold'+folder+'_'+i); ++i) {"
                        + el
                        + "       show_folder_completely(folder+'_'+i); }"
                        + el + "}" + el + "" + el + "</script>" + el);

        fileout
                .write("<SPAN class=\"foldspecial\" onclick=\"fold_document()\">All +</SPAN>"
                        + el);
        fileout
                .write("<SPAN class=\"foldspecial\" onclick=\"unfold_document()\">All -</SPAN>"
                        + el);

     // fileout.write("<ul>");

     saveHTML(rootNodeOfBranch, "1", 0, /* isRoot */true, /* treatAsParagraph */
            true, /* depth */1);

     // fileout.write("</ul>");

        fileout.write("<SCRIPT language=\"JavaScript\">" + el);
        fileout.write("fold_document();" + el);
        fileout.write("</SCRIPT>" + el);
    }

    private void writeStyle() throws IOException {
        fileout.write("<style type=\"text/css\">" + el);
        if(writeFoldingCode){
            fileout.write("    span.foldopened { color: white; font-size: xx-small;"
                        + el
                        + "    border-width: 1; font-family: monospace; padding: 0em 0.25em 0em 0.25em; background: #e0e0e0;"
                        + el
                        + "    VISIBILITY: visible;"
                        + el
                        + "    cursor:pointer; }"
                        + el
                        + ""
                        + el
                        + ""
                        + el
                        + "    span.foldclosed { color: #666666; font-size: xx-small;"
                        + el
                        + "    border-width: 1; font-family: monospace; padding: 0em 0.25em 0em 0.25em; background: #e0e0e0;"
                        + el
                        + "    VISIBILITY: hidden;"
                        + el
                        + "    cursor:pointer; }"
                        + el
                        + ""
                        + el
                        + "    span.foldspecial { color: #666666; font-size: xx-small; border-style: none solid solid none;"
                        + el
                        + "    border-color: #CCCCCC; border-width: 1; font-family: sans-serif; padding: 0em 0.1em 0em 0.1em; background: #e0e0e0;"
                        + el
                        + "    cursor:pointer; }"
                        + el);
        }
        fileout.write(el + "    li.mapnode { list-style: none; }"
                        + el
                        + ""
                        + el
                        + "    span.l { color: red; font-weight: bold; }"
                        + el
                        + ""
                        + el
                        + "    a.mapnode:link {text-decoration: none; color: black; }"
                        + el
                        + "    a.mapnode:visited {text-decoration: none; color: black; }"
                        + el
                        + "    a.mapnode:active {text-decoration: none; color: black; }"
                        + el
                        + "    a.mapnode:hover {text-decoration: none; color: black; background: #eeeee0; }"
                        + el
                        + ""
                        + el
                        + "</style>"
                        + el
                        + "<!-- ^ Position is not set to relative / absolute here because of Mozilla -->");                            
    }

    private int saveHTML(MindMapNodeModel model, String parentID,
            int lastChildNumber, boolean isRoot, boolean treatAsParagraph,
            int depth) throws IOException {
        // return lastChildNumber
        // Not very beautiful solution, but working at least and logical
        // too.

        boolean createFolding = model.isFolded();
        if(writeFoldingCode )
        {
            if (getProperty("html_export_folding").equals(
            "html_export_fold_all")) {
                createFolding = model.hasChildren();
            }
            if (getProperty("html_export_folding").equals(
            "html_export_no_folding")
            || basedOnHeadings || isRoot) {
                createFolding = false;
            }
        }
        else{
            createFolding = false;
        }

        fileout.write(treatAsParagraph || basedOnHeadings ? "<p>"
                : "<li class=\"mapnode\">");

        String localParentID = parentID;
        if (createFolding) {
            // lastChildNumber = new Integer lastChildNumber.intValue() + 1;
            // Change value of an integer
            lastChildNumber++;

            localParentID = parentID + "_" + lastChildNumber;
            fileout.write("<span id=\"show" + localParentID
                    + "\" class=\"foldclosed\" onClick=\"show_folder('"
                    + localParentID
                    + "')\" style=\"POSITION: absolute\">+</span> "
                    + "<span id=\"hide" + localParentID
                    + "\" class=\"foldopened\" onClick=\"hide_folder('"
                    + localParentID + "')\">-</span>");

            fileout.write("\n");
        }

        if (basedOnHeadings && model.hasChildren() && depth <= 5) {
            fileout.write("<h" + depth + ">");
        }

        if (model.getLink() != null) {
            String link = model.getLink();
            if (link
                    .endsWith(freemind.main.FreeMindCommon.FREEMIND_FILE_EXTENSION)) {
                link += ".html";
            }
            fileout.write("<a class=\"mapnode\" href=\"" + link
                    + "\" target=\"_blank\"><span class=\"l\">~</span>");
        }

        String fontStyle = "";

        if (model.getColor() != null) {
            fontStyle += "color: " + Tools.colorToXml(model.getColor())
                    + ";";
        }

        if (model.getFont() != null && model.getFont().getSize() != 0) {
            int defaultFontSize = Integer
                    .parseInt(getProperty("defaultfontsize"));
            int procentSize = (int) (model.getFont().getSize() * 100 / defaultFontSize);
            if (procentSize != 100) {
                fontStyle += "font-size: " + procentSize + "%;";
            }
        }

        if (model.getFont() != null) {
            String fontFamily = model.getFont().getFamily();
            fontStyle += "font-family: " + fontFamily + ", sans-serif; ";
        }

        if (model.isItalic()) {
            fontStyle += "font-style: italic; ";
        }

        if (model.isBold()) {
            fontStyle += "font-weight: bold; ";
        }

        // ------------------------

        if (!fontStyle.equals("")) {
            fileout.write("<span style=\"" + fontStyle + "\">");
        }

        if (getProperty("export_icons_in_html").equals("true")) {
            for (int i = 0; i < model.getIcons().size(); ++i) {
                fileout.write("<img src=\""
                        + ((MindIcon) model.getIcons().get(i))
                                .getIconFileName()
                        + "\" alt=\""
                        + ((MindIcon) model.getIcons().get(i))
                                .getDescription() + "\">");
            }
        }

        if (model.toString().matches(" *")) {
            fileout.write("&nbsp;");
        }
        else if (model.toString().startsWith("<html>")) {
            String output = model.toString().substring(6); // do not write
            // <html>
            if (output.endsWith("</html>")) {
                output = output.substring(0, output.length() - 7);
            }
            fileout.write(HtmlTools.unicodeToHTMLUnicodeEntity(output));
        }
        else {
            fileout
                    .write(saveHTML_escapeUnicodeAndSpecialCharacters(model.toString()));
        }

        if (fontStyle != "") {
            fileout.write("</span>");
        }

        fileout.write(el);

        if (model.getLink() != null) {
            fileout.write("</a>" + el);
        }

        if (basedOnHeadings && model.hasChildren() && depth <= 5) {
            fileout.write("</h" + depth + ">");
        }

        // Are the children to be treated as paragraphs?

        boolean treatChildrenAsParagraph = false;
        for (ListIterator e = model.childrenUnfolded(); e.hasNext();) {
            if (((MindMapNodeModel) e.next()).toString().length() > 100) { // TODO:
                // replace
                // heuristic
                // constant
                treatChildrenAsParagraph = true;
                break;
            }
        }

        // Write the children

        // Export based on headings

        if (basedOnHeadings) {
            lastChildNumber = saveChildrenHtml(model, fileout, parentID,
                    lastChildNumber, depth, treatChildrenAsParagraph);
            if (treatAsParagraph || basedOnHeadings)
                fileout.write("</p>");
            return lastChildNumber;
        }

        // Export not based on headings

        if (model.hasChildren()) {
            if (basedOnHeadings) {
                lastChildNumber = saveChildrenHtml(model, fileout,
                        parentID, lastChildNumber, depth,
                        treatChildrenAsParagraph);
            }
            else if (createFolding) {
                fileout
                        .write("<ul id=\"fold"
                                + localParentID
                                + "\" style=\"POSITION: relative; VISIBILITY: visible;\">");
                if (treatChildrenAsParagraph) {
                    fileout.write("<li>");
                }
                int localLastChildNumber = 0;
                saveChildrenHtml(model, fileout, localParentID,
                        localLastChildNumber, depth,
                        treatChildrenAsParagraph);
            }
            else {
                fileout.write("<ul>");
                if (treatChildrenAsParagraph) {
                    fileout.write("<li>");
                }
                lastChildNumber = saveChildrenHtml(model, fileout,
                        parentID, lastChildNumber, depth,
                        treatChildrenAsParagraph);
            }
            if (treatChildrenAsParagraph) {
                fileout.write("</li>");
            }
            fileout.write(el);
            fileout.write("</ul>");
        }

        // End up the node

        if (!treatAsParagraph) {
            fileout.write(el + "</li>" + el);
        }

        if (treatAsParagraph || basedOnHeadings)
            fileout.write("</p>");
        return lastChildNumber;
    }

    private String getProperty(String key) {
        return Resources.getInstance().getProperty(key);
    }

    private int saveChildrenHtml(MindMapNodeModel model, Writer fileout,
            String parentID, int lastChildNumber, int depth,
            boolean treatChildrenAsParagraph) throws IOException {
        for (ListIterator e = model.childrenUnfolded(); e.hasNext();) {
            MindMapNodeModel child = (MindMapNodeModel) e.next();
            if (child.isVisible()) {
                lastChildNumber = saveHTML(child, parentID,
                        lastChildNumber,/* isRoot= */false,
                        treatChildrenAsParagraph, depth + 1);
            }
            else {
                lastChildNumber = saveChildrenHtml(child, fileout,
                        parentID, lastChildNumber, depth,
                        treatChildrenAsParagraph);
            }
        }
        return lastChildNumber;
    }

}