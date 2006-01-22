
/*FreeMindget - A Program for creating and viewing Mindmaps
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
/*$Id: MindMapMapModel.java,v 1.36.14.10.2.1.2.7 2006-01-22 12:24:39 dpolivaev Exp $*/

package freemind.modes.mindmapmode;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.datatransfer.Transferable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import freemind.controller.MindMapNodesSelection;
import freemind.main.FreeMind;
import freemind.main.FreeMindMain;
import freemind.main.Tools;
import freemind.main.XMLParseException;
import freemind.modes.LinkRegistryAdapter;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapLinkRegistry;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;


public class MindMapMapModel extends MapAdapter  {

    LockManager lockManager;
    private LinkRegistryAdapter linkRegistry;
    private Timer timerForAutomaticSaving;
    private static final String EXPECTED_START_STRINGS[] = {
            "<map version=\"" + FreeMind.XML_VERSION + "\"",
            "<map version=\"0.7.1\"",
            "<map version=\"0.8.0\""};

    //
    // Constructors
    //

    public MindMapMapModel( FreeMindMain frame , ModeController modeController) {
        super(frame, modeController);
        MindMapNodeModel root = new MindMapNodeModel( frame.getResourceString("new_mindmap"), frame, this);
        lockManager = frame.getProperty("experimental_file_locking_on").equals("true") ? 
           new LockManager() : new DummyLockManager();

        // register new LinkRegistryAdapter
        linkRegistry = new LinkRegistryAdapter();

        setRoot(root);
        readOnly = false; 
        
        // automatic save:
        timerForAutomaticSaving = new Timer();
        int delay = Integer.parseInt(getFrame().getProperty("time_for_automatic_save"));
        int numberOfTempFiles = Integer.parseInt(getFrame().getProperty("number_of_different_files_for_automatic_save"));
        boolean filesShouldBeDeletedAfterShutdown = Tools.safeEquals(getFrame().getProperty("delete_automatic_saves_at_exit"),"true");
        String path = getFrame().getProperty("path_to_automatic_saves");
        /* two standard values: */
        if(Tools.safeEquals(path, "default")) {
            path = null;
        }
        if(Tools.safeEquals(path, "freemind_home")) {
            path = getFrame().getFreemindDirectory();
        }
        File dirToStore = null;
        if(path!=null) {
            dirToStore = new File(path);
            /* existence? */
            if(! dirToStore.isDirectory()) {
                dirToStore = null;
                System.err.println("Temporary directory " + path + " not found. Disabling automatic store.");
                delay = Integer.MAX_VALUE;
            }
        }
        timerForAutomaticSaving.schedule(new doAutomaticSave(this, numberOfTempFiles, filesShouldBeDeletedAfterShutdown, dirToStore), delay, delay);
    }

    // 

    public MindMapLinkRegistry getLinkRegistry() {
        return linkRegistry;
    }

    public String getRestoreable() {
       return getFile()==null ? null : "MindMap:"+getFile().getAbsolutePath(); }

    //
    // Other methods
    //

    public String toString() {
       return getFile() == null ? null : getFile().getName(); }

    //
    // Export and saving
    //


   public boolean saveHTML(MindMapNodeModel rootNodeOfBranch, File file) { 
        // When isRoot is true, rootNodeOfBranch will be exported as folded
        // regardless his isFolded state in the mindmap.
        try {
            // We do all the HTML saving using just ordinary output.

            //Generating output Stream
            BufferedWriter fileout = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(file) ) );

            String el = System.getProperty("line.separator");
            fileout.write(
"<html>"+el+
"<head>"+el+
"<title>"+rootNodeOfBranch.saveHTML_escapeUnicodeAndSpecialCharacters(rootNodeOfBranch.toString())+
"</title>"+el+
"<style type=\"text/css\">"+el+
"    span.foldopened { color: white; font-size: xx-small;"+el+
"    border-width: 1; font-family: monospace; padding: 0em 0.25em 0em 0.25em; background: #e0e0e0;"+el+
"    VISIBILITY: visible;"+el+
"    cursor:pointer; }"+el+
""+el+
""+el+
"    span.foldclosed { color: #666666; font-size: xx-small;"+el+
"    border-width: 1; font-family: monospace; padding: 0em 0.25em 0em 0.25em; background: #e0e0e0;"+el+
"    VISIBILITY: hidden;"+el+
"    cursor:pointer; }"+el+
""+el+
"    span.foldspecial { color: #666666; font-size: xx-small; border-style: none solid solid none;"+el+
"    border-color: #CCCCCC; border-width: 1; font-family: sans-serif; padding: 0em 0.1em 0em 0.1em; background: #e0e0e0;"+el+
"    cursor:pointer; }"+el+
""+el+
"    li { list-style: none; }"+el+
""+el+
"    span.l { color: red; font-weight: bold; }"+el+
""+el+
"    a:link {text-decoration: none; color: black; }"+el+
"    a:visited {text-decoration: none; color: black; }"+el+
"    a:active {text-decoration: none; color: black; }"+el+
"    a:hover {text-decoration: none; color: black; background: #eeeee0; }"+el+
""+el+
"</style>"+el+
"<!-- ^ Position is not set to relative / absolute here because of Mozilla -->"+el+
"</head>"+el+
"<body>"+el);

            String htmlExportFoldingOption = getFrame().getProperty("html_export_folding");
            boolean writeFoldingCode =
               ( htmlExportFoldingOption.equals("html_export_fold_currently_folded") &&
                 rootNodeOfBranch.hasFoldedStrictDescendant() ) ||
               htmlExportFoldingOption.equals("html_export_fold_all") ;

            if (writeFoldingCode) { 
               fileout.write(
""+el+
"<script language=\"JavaScript\">"+el+
"   // Here we implement folding. It works fine with MSIE5.5, MSIE6.0 and"+el+
"   // Mozilla 0.9.6."+el+
""+el+
"   if (document.layers) {"+el+
"      //Netscape 4 specific code"+el+
"      pre = 'document.';"+el+
"      post = ''; }"+el+
"   if (document.getElementById) {"+el+
"      //Netscape 6 specific code"+el+
"      pre = 'document.getElementById(\"';"+el+
"      post = '\").style'; }"+el+
"   if (document.all) {"+el+
"      //IE4+ specific code"+el+
"      pre = 'document.all.';"+el+
"      post = '.style'; }"+el+
""+el+
"function layer_exists(layer) {"+el+
"   try {"+el+
"      eval(pre + layer + post);"+el+
"      return true; }"+el+
"   catch (error) {"+el+
"      return false; }}"+el+
""+el+
"function show_layer(layer) {"+el+
"   eval(pre + layer + post).position = 'relative'; "+el+
"   eval(pre + layer + post).visibility = 'visible'; }"+el+
""+el+
"function hide_layer(layer) {"+el+
"   eval(pre + layer + post).visibility = 'hidden';"+el+
"   eval(pre + layer + post).position = 'absolute'; }"+el+
""+el+
"function hide_folder(folder) {"+el+
"    hide_folding_layer(folder)"+el+
"    show_layer('show'+folder);"+el+
""+el+
"    scrollBy(0,0); // This is a work around to make it work in Browsers (Explorer, Mozilla)"+el+
"}"+el+
""+el+
"function show_folder(folder) {"+el+
"    // Precondition: all subfolders are folded"+el+
""+el+
"    show_layer('hide'+folder);"+el+
"    hide_layer('show'+folder);"+el+
"    show_layer('fold'+folder);"+el+
""+el+
"    scrollBy(0,0); // This is a work around to make it work in Browsers (Explorer, Mozilla)"+el+
""+el+
"    var i;"+el+
"    for (i=1; layer_exists('fold'+folder+'_'+i); ++i) {"+el+
"       show_layer('show'+folder+'_'+i); }"+el+
"}"+el+
""+
"function show_folder_completely(folder) {"+el+
"    // Precondition: all subfolders are folded"+el+
""+el+
"    show_layer('hide'+folder);"+el+
"    hide_layer('show'+folder);"+el+
"    show_layer('fold'+folder);"+el+
""+el+
"    scrollBy(0,0); // This is a work around to make it work in Browsers (Explorer, Mozilla)"+el+
""+el+
"    var i;"+el+
"    for (i=1; layer_exists('fold'+folder+'_'+i); ++i) {"+el+
"       show_folder_completely(folder+'_'+i); }"+el+
"}"+el+
""+el+
""+el+
""+el+
"function hide_folding_layer(folder) {"+el+
"   var i;"+el+
"   for (i=1; layer_exists('fold'+folder+'_'+i); ++i) {"+el+
"       hide_folding_layer(folder+'_'+i); }"+el+
""+el+
"   hide_layer('hide'+folder);"+el+
"   hide_layer('show'+folder);"+el+
"   hide_layer('fold'+folder);"+el+
""+el+
"   scrollBy(0,0); // This is a work around to make it work in Browsers (Explorer, Mozilla)"+el+
"}"+el+
""+el+
"function fold_document() {"+el+
"   var i;"+el+
"   var folder = '1';"+el+
"   for (i=1; layer_exists('fold'+folder+'_'+i); ++i) {"+el+
"       hide_folder(folder+'_'+i); }"+el+
"}"+el+
""+el+
"function unfold_document() {"+el+
"   var i;"+el+
"   var folder = '1';"+el+
"   for (i=1; layer_exists('fold'+folder+'_'+i); ++i) {"+el+
"       show_folder_completely(folder+'_'+i); }"+el+
"}"+el+
""+el+
"</script>"+el);
               
               fileout.write("<SPAN class=foldspecial onclick=\"fold_document()\">All +</SPAN>"+el);
               fileout.write("<SPAN class=foldspecial onclick=\"unfold_document()\">All -</SPAN>"+el); }

            //fileout.write("<ul>");

            rootNodeOfBranch.saveHTML(fileout, "1", 0, /*isRoot*/true, /*treatAsParagraph*/true, /*depth*/1);

            //fileout.write("</ul>");

            if (writeFoldingCode) {
               fileout.write("<SCRIPT language=JavaScript>"+el);
               fileout.write("fold_document();"+el);
               fileout.write("</SCRIPT>"+el); }
            fileout.write("</body>"+el);
            fileout.write("</html>"+el);
            fileout.close();
            return true;

        } catch(Exception e) {
            System.err.println("Error in MindMapMapModel.saveHTML(): ");
            e.printStackTrace();
            return false;
        }
    }

    public String getAsPlainText(List mindMapNodes) {
        // Returns success of the operation.
        try {
            StringWriter stringWriter = new StringWriter();
            BufferedWriter fileout = new BufferedWriter(stringWriter);

            for(ListIterator it=mindMapNodes.listIterator();it.hasNext();) {
               ((MindMapNodeModel)it.next()).saveTXT(fileout,/*depth=*/0); }

            fileout.close();
            return stringWriter.toString();

        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

   public boolean saveTXT(MindMapNodeModel rootNodeOfBranch, File file) { 
        // Returns success of the operation.
        try {
            BufferedWriter fileout = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(file) ) );
            rootNodeOfBranch.saveTXT(fileout,/*depth=*/0);
            fileout.close();
            return true;

        } catch(Exception e) {
            System.err.println("Error in MindMapMapModel.saveTXT(): ");
            e.printStackTrace();
            return false;
        }
    }

    public String getAsRTF(List mindMapNodes) {
        // Returns success of the operation.
        try {
            StringWriter stringWriter = new StringWriter();
            BufferedWriter fileout = new BufferedWriter(stringWriter);
            saveRTF(mindMapNodes, fileout);
            fileout.close();

            return stringWriter.toString();
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

   public boolean saveRTF(List mindMapNodes, BufferedWriter fileout) { 
        // Returns success of the operation.
        try {

           // First collect all used colors
           HashSet colors = new HashSet();
           for(ListIterator it=mindMapNodes.listIterator();it.hasNext();) {
              ((MindMapNodeModel)it.next()).collectColors(colors); }

           // Prepare table of colors containing indices to color table
           String colorTableString="{\\colortbl;\\red0\\green0\\blue255;";
           // 0 - Automatic, 1 - blue for links

           HashMap colorTable = new HashMap();
           int colorPosition = 2;
           for(Iterator it=colors.iterator();it.hasNext();++colorPosition) {
              Color color = (Color)it.next();
              colorTableString += "\\red"+color.getRed()+"\\green"+color.getGreen()+
                 "\\blue"+color.getBlue()+";";
              colorTable.put(color,new Integer(colorPosition)); }
           colorTableString += "}";

            fileout.write
               ("{\\rtf1\\ansi\\ansicpg1252\\deff0\\deflang1033{\\fonttbl{\\f0\\fswiss\\fcharset0 Arial;}"+
                colorTableString+
                "}"+
                "\\viewkind4\\uc1\\pard\\f0\\fs20{}");
            // ^ If \\ud is appended here, Unicode does not work in MS Word.

            for(ListIterator it=mindMapNodes.listIterator();it.hasNext();) {
               ((MindMapNodeModel)it.next()).saveRTF(fileout,/*depth=*/0,colorTable); }

            fileout.write("}");
            return true; }
        catch(Exception e) {
            e.printStackTrace();
            return false; }}
    /**
     * Return the success of saving
     */
    public boolean save(File file) {
        return saveInternal(file, false);
    }
    
    /** This method is intended to provide both normal save routines and saving of temporary (internal) files.*/
    private boolean saveInternal(File file, boolean isInternal) {
        if (!isInternal && readOnly) { // unexpected situation, yet it's better to back it up
            System.err.println("Attempt to save read-only map.");           
            return false; }
        try {            
            //Generating output Stream            
            BufferedWriter fileout = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(file) ) );
            getXml(fileout);

            if(!isInternal) {
                setFile(file);            
                setSaved(true);
            }
            return true;
        } catch (FileNotFoundException e ) {
            String message = Tools.expandPlaceholders(getText("save_failed"),file.getName());
            if(!isInternal)
                getFrame().getController().errorMessage(message);
            else
                getFrame().out(message);
            return false; 
        } catch(Exception e) {
            System.err.println("Error in MindMapMapModel.save(): ");
            e.printStackTrace();
            return false;
        }
    }


    /** writes the content of the map to a writer.
	 * @param fileout
	 * @throws IOException
	 */
	public void getXml(Writer fileout) throws IOException {
		fileout.write("<map version=\""+FreeMind.XML_VERSION+"\">\n");
		fileout.write("<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->\n");
		getRegistry().save(fileout);
		((MindMapNodeModel)getRoot()).save(fileout, this.getLinkRegistry());
		fileout.write("</map>\n");
		fileout.close();
	}

	/**
     * Attempts to lock the map using a semaphore file
     * @param file
     * @return If the map is locked, return the name of the locking user, otherwise return null.
     * @throws Exception, when the locking failed for other reasons than that the
     * file is being edited.
     */
    public String tryToLock(File file) throws Exception {       
        String lockingUser = lockManager.tryToLock(file);
        String lockingUserOfOldLock = lockManager.popLockingUserOfOldLock(); 
        if (lockingUserOfOldLock != null) {     
          getFrame().getController().informationMessage(
            Tools.expandPlaceholders(getText("locking_old_lock_removed"), file.getName(), lockingUserOfOldLock)); }
        if (lockingUser == null) {          
          readOnly = false; } // The map sure is not read only when the locking suceeded.                   
        return lockingUser; }
                
    public void load(File file) throws FileNotFoundException, IOException, XMLParseException {
       if (!file.exists()) {
          throw new FileNotFoundException(Tools.expandPlaceholders(getText("file_not_found"), file.getPath())); }
       if (!file.canWrite()) {
          readOnly = true; }
       else {
          // try to lock the map
          try {
             String lockingUser = tryToLock(file);
             if (lockingUser != null) {          
               getFrame().getController().informationMessage(
                 Tools.expandPlaceholders(getText("map_locked_by_open"), file.getName(), lockingUser));
               readOnly = true; }
             else {
               readOnly = false; }}            
          catch (Exception e){ // Throwed by tryToLock
             e.printStackTrace();
             getFrame().getController().informationMessage(
               Tools.expandPlaceholders(getText("locking_failed_by_open"), file.getName()));   
             readOnly = true; }}
       
       MindMapNodeModel root = loadTree(file);
       if (root != null) {
          setRoot(root); }
       setFile(file);
       setSaved(true); } 
    
    /** When a map is closed, this method is called. */
    public void destroy() {
       super.destroy();
       lockManager.releaseLock();
       lockManager.releaseTimer(); 
       /* cancel the timer, if map is closed. */
       timerForAutomaticSaving.cancel(); 
    }

    MindMapNodeModel loadTree(File file) throws XMLParseException, IOException {
        MindMapXMLElement mapElement = new MindMapXMLElement(getFrame(), this);
        int versionInfoLength = EXPECTED_START_STRINGS[0].length();
        // reading the start of the file:
        StringBuffer buffer = readFileStart(file, versionInfoLength);
        String mapStart = "";
        if(buffer.length() >= versionInfoLength){
        		mapStart = buffer.substring(0, versionInfoLength);
        }
        // the resulting file is accessed by the reader:
        Reader reader = null;
        for(int i = 0; i < EXPECTED_START_STRINGS.length; i++){
            if (mapStart.equals(EXPECTED_START_STRINGS[i])) {
                // actual version:
                reader = getActualReader(file);
                break;
            } 
        }
        if (reader == null) {
            // other version:
            reader = getUpdateReader(file);
        }
        try {
            mapElement.parseFromReader(reader);
        } catch (Exception ex) {
            System.err.println("Error while parsing file:" + ex);
            ex.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        // complete the arrow links:
        mapElement.processUnfinishedLinks(getLinkRegistry());
        // we wait with "invokeHooksRecursively" until the map is fully
        // registered.
        return (MindMapNodeModel) mapElement.getMapChild();
    }

    /** Returns pMinimumLength bytes of the files content.
     * @param file
     * @param pMinimumLength
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private StringBuffer readFileStart(File file, int pMinimumLength) {
    	BufferedReader in=null;
    	StringBuffer buffer = new StringBuffer();
        try {
			// get the file start into the memory:
			in = new BufferedReader(new FileReader(file));
			String str;
			while ((str = in.readLine()) != null) {
				buffer.append(str);
				if (buffer.length() >= pMinimumLength)
					break;
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return new StringBuffer();
		}
		return buffer;
    }

    

    /** Creates a reader that pipes the input file through a XSLT-Script that
     *  updates the version to the current.
     * @param file
     * @return 
     * @throws IOException
     */
    private Reader getUpdateReader(File file) throws IOException {
        StringWriter writer = null;
        InputStream inputStream = null;
        logger.info("Updating the file "+file.getName()+" to the current version.");
        try{
            // try to convert map with xslt:
            URL updaterUrl=null;
            updaterUrl = getFrame().getResource("freemind/modes/mindmapmode/freemind_version_updater.xslt");
            if(updaterUrl == null) {
                throw new IllegalArgumentException("freemind_version_updater.xslt not found.");
            }
            Source xsltSource=null;
            inputStream = updaterUrl.openStream();
            xsltSource = new StreamSource(inputStream);
            // get output:
            writer = new StringWriter();
            Result result = new StreamResult(writer);
            // create an instance of TransformerFactory
            TransformerFactory transFact = TransformerFactory.newInstance();
            Transformer trans = transFact.newTransformer(xsltSource);
            trans.transform(new StreamSource(file), result);
            logger.info("Updating the file "+file.getName()+" to the current version. Done.");
        } catch(Exception ex) {
            ex.printStackTrace();
            // exception: we take the file itself:
            return getActualReader(file);
        } finally {
            if(inputStream!= null) {
                inputStream.close();
            }
            if(writer != null) {
                writer.close();
            }
        }
        return new StringReader(writer.getBuffer().toString());
    }

    /** Creates a default reader that just reads the given file.
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    private Reader getActualReader(File file) throws FileNotFoundException {
        return new BufferedReader(new FileReader(file));
    }

    //
    // cut'n'paste
    //
// (PN) see: super.cut(node) ... it does exactly the same!!!
//    public Transferable cut(MindMapNode node) {
//       Transferable transfer = copy(node);
//       super.cut(node);
//       return transfer;
//    }

    public Transferable copy(MindMapNode node) {
       StringWriter stringWriter = new StringWriter();
       try {
          ((MindMapNodeModel)node).save(stringWriter, this.getLinkRegistry()); }
       catch (IOException e) {}
       return new MindMapNodesSelection(stringWriter.toString(), null, null, null, null, null); }



    private class LockManager extends TimerTask {
        File lockedSemaphoreFile = null;        
        Timer lockTimer = null;
        final long lockUpdatePeriod = 4*60*1000; // four minutes
        final long lockSafetyPeriod = 5*60*1000; // five minutes
        String lockingUserOfOldLock = null;    
                
        private File getSemaphoreFile(File mapFile) {       
            return new File(mapFile.getParent()+System.getProperty("file.separator")+
                            "$~"+mapFile.getName()+"~"); }
                            
        public synchronized String popLockingUserOfOldLock() {
            String toReturn = lockingUserOfOldLock;
            lockingUserOfOldLock = null;
            return toReturn; }  

        private void writeSemaphoreFile(File inSemaphoreFile) throws Exception {
            FileOutputStream semaphoreOutputStream = new FileOutputStream(inSemaphoreFile);
            FileLock lock = null;
            try {
               lock = semaphoreOutputStream.getChannel().tryLock(); 
               if (lock == null) {
                  semaphoreOutputStream.close();
                  System.err.println("Locking failed.");                  
                  throw new Exception(); }} // locking failed
            catch (UnsatisfiedLinkError eUle) {}  // This may come with Windows95. We don't insist on detailed locking in that case.
            catch (NoClassDefFoundError eDcdf) {} // ^ just like above.
            // ^ On Windows95, the necessary libraries are missing.
            semaphoreOutputStream.write(System.getProperty("user.name").getBytes());
            semaphoreOutputStream.write('\n');
            semaphoreOutputStream.write(String.valueOf(System.currentTimeMillis()).getBytes());         
            semaphoreOutputStream.close();
            semaphoreOutputStream = null;
            Tools.setHidden(inSemaphoreFile, true, /*synchro=*/false); // Exception free  
            if (lock != null) lock.release(); }
                                    
        public synchronized String tryToLock(File file) throws Exception {
            // Locking should work for opening as well as for saving as.
            // We are especially carefull when it comes to exclusivity of writing.
                 
            File semaphoreFile = getSemaphoreFile(file);
            if (semaphoreFile == lockedSemaphoreFile) {
                return null ; }
            try {
               BufferedReader semaphoreReader = new BufferedReader(new FileReader(semaphoreFile));
               String lockingUser = semaphoreReader.readLine();

               long lockTime = new Long (semaphoreReader.readLine()).longValue();
               long timeDifference = System.currentTimeMillis() - lockTime;
               //catch (NumberFormatException enf) {} // This means that the time was not written at all - lock is corrupt
               if (timeDifference > lockSafetyPeriod) { // the lock is old
                  semaphoreReader.close();
                  lockingUserOfOldLock = lockingUser;
                  semaphoreFile.delete(); }
               else return lockingUser; }
            catch (FileNotFoundException e) {}

            writeSemaphoreFile(semaphoreFile);

            if (lockTimer == null) {
              lockTimer = new Timer();
              lockTimer.schedule(this, lockUpdatePeriod, lockUpdatePeriod); }                   
            releaseLock();
            lockedSemaphoreFile = semaphoreFile;
            return null; }
               
        public synchronized void releaseLock() {
           if (lockedSemaphoreFile != null) {
              lockedSemaphoreFile.delete();
              lockedSemaphoreFile = null; }} // this may fail, TODO: ensure real deletion

        public synchronized void releaseTimer() {
            if (lockTimer != null) {
              lockTimer.cancel();
              lockTimer = null; }}
               
        public synchronized void run() { // update semaphore file           
            if (lockedSemaphoreFile == null) {
                System.err.println("unexpected: lockedSemaphoreFile is null upon lock update");
                return; }           
            try {
               Tools.setHidden(lockedSemaphoreFile, false, /*synchro=*/true); // Exception free
               // ^ We unhide the file before overwriting because JavaRE1.4.2 does
               // not let us open hidden files for writing. This is a workaround for Java bug,
               // I guess.

               writeSemaphoreFile(lockedSemaphoreFile); }
            catch (Exception e) {e.printStackTrace();}}         
    }   
    private class DummyLockManager extends LockManager {
        public synchronized String popLockingUserOfOldLock() {
            return null; }  
                                    
        public synchronized String tryToLock(File file) throws Exception {
            return null; }
               
        public synchronized void releaseLock() {}
            
        public synchronized void releaseTimer() {}
               
        public synchronized void run() {}
    }   

    private class doAutomaticSave  extends TimerTask {
        private MindMapMapModel model;
        private Vector tempFileStack;
        private int numberOfFiles;
        private boolean filesShouldBeDeletedAfterShutdown;
        private File pathToStore;
        /** This value is compared with the result of getNumberOfChangesSinceLastSave(). If the values coincide, no further automatic
            saving is performed until the value changes again.*/
        private int changeState;
        doAutomaticSave(MindMapMapModel model, int numberOfTempFiles, boolean filesShouldBeDeletedAfterShutdown, File pathToStore) {
            this.model = model;
            tempFileStack = new Vector();
            numberOfFiles = ((numberOfTempFiles > 0)? numberOfTempFiles: 1);
            this.filesShouldBeDeletedAfterShutdown = filesShouldBeDeletedAfterShutdown;
            this.pathToStore = pathToStore;
            changeState = 0;
        }
        public void run() {
            /* Map is dirty enough? */
            if(model.getNumberOfChangesSinceLastSave() == changeState)
                return;
            changeState = model.getNumberOfChangesSinceLastSave();
            if(model.getNumberOfChangesSinceLastSave() == 0) {
                /* map was recently saved.*/
                return;
            }
            try {
                EventQueue.invokeAndWait(new Runnable(){
                    public void run() {
                        /* Now, it is dirty, we save it.*/
                        File tempFile;
                        if(tempFileStack.size() >= numberOfFiles)
                            tempFile = (File) tempFileStack.remove(0); // pop
                        else {
                            try {
                                tempFile = File.createTempFile("FM_"+((model.toString()==null)?"unnamed":model.toString()), ".mm", pathToStore);
                                if(filesShouldBeDeletedAfterShutdown)
                                    tempFile.deleteOnExit();
                            } catch (Exception e) {
                                System.err.println("Error in automatic MindMapMapModel.save(): "+e.getMessage());
                                e.printStackTrace();
                                return;
                            }
                        }
                        try {
                            model.saveInternal(tempFile, true /*=internal call*/);
                            model.getFrame().out("Map was automatically saved (using the file name "+tempFile+") ...");
                        } catch (Exception e) {
                            System.err.println("Error in automatic MindMapMapModel.save(): "+e.getMessage());
                            e.printStackTrace();
                        }
                        tempFileStack.add(tempFile); // add at the back.
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
     }
}
