package freemind.controller;
import java.awt.datatransfer.*;
import java.io.*;

public class MindMapNodesSelection implements Transferable, ClipboardOwner {

   private String nodesContent;
   private String stringContent;
   private String rtfContent;
   private String dropActionContent;

   public static DataFlavor mindMapNodesFlavor = null;
   public static DataFlavor rtfFlavor = null;
   public static DataFlavor htmlFlavor = null;
   public static DataFlavor fileListFlavor = null;
   public static DataFlavor dropActionFlavor = null;
   static {
      try {
         mindMapNodesFlavor = new DataFlavor("text/freemind-nodes; class=java.lang.String");
         rtfFlavor = new DataFlavor("text/rtf; class=java.io.InputStream"); 
         htmlFlavor = new DataFlavor("text/html; class=java.lang.String");
         fileListFlavor = new DataFlavor("application/x-java-file-list; class=java.util.List");
         dropActionFlavor = new DataFlavor("text/drop-action; class=java.lang.String"); 
      }

      catch(Exception e) {
         System.err.println(e); }}

   //

   public MindMapNodesSelection(String nodesContent, String stringContent, String rtfContent, String dropActionContent) {
      this.nodesContent = nodesContent;
      this.rtfContent = rtfContent;
      this.stringContent = stringContent;
      this.dropActionContent = dropActionContent; }

   public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
      if (flavor.equals(DataFlavor.stringFlavor)) {
         return stringContent; }
      if (flavor.equals(mindMapNodesFlavor)) {
         return nodesContent; }
      if (flavor.equals(dropActionFlavor)) {
         return dropActionContent; }
      if (flavor.equals(rtfFlavor)) {
         byte[] byteArray = rtfContent.getBytes();
         // for (int i = 0; i < byteArray.length; ++i) {
         //   System.out.println(byteArray[i]); }

         return new ByteArrayInputStream(byteArray); }
      throw new UnsupportedFlavorException(flavor); }

   public DataFlavor[] getTransferDataFlavors() {
      return new DataFlavor[] { DataFlavor.stringFlavor, mindMapNodesFlavor, rtfFlavor, dropActionFlavor}; }

   public boolean isDataFlavorSupported(DataFlavor flavor) {
      return flavor.equals(DataFlavor.stringFlavor) || flavor.equals(mindMapNodesFlavor)
         || flavor.equals(rtfFlavor) || flavor.equals(dropActionFlavor); }

   public void lostOwnership(Clipboard clipboard, Transferable contents) {}

   public void setDropAction(String dropActionContent) {
      this.dropActionContent = dropActionContent; }
}
