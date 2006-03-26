/*
 * XmlExporter.java
 *
 * Created on 27 January 2004, 17:23
 */

package accessories.plugins.util.xslt;
import java.io.*;
import javax.swing.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.*;

/**
 *
 * @author  joerg
 */
public class XmlExporter {
     //Create a file chooser
    final JFileChooser fc = new JFileChooser();
    /** Creates a new instance of XmlExporter */
    public XmlExporter() {
    }
    
  
    
    
    public void transForm(File xmlFile, File xsltFile, File resultFile){
      //System.out.println("set source");
     Source xmlSource = new StreamSource(xmlFile);
      //System.out.println("set xsl");
     Source xsltSource =  new StreamSource(xsltFile);
      //System.out.println("set result");
     Result result = new StreamResult(resultFile);

     // create an instance of TransformerFactory
     try{
         //System.out.println("make transform instance");
     TransformerFactory transFact = TransformerFactory.newInstance(  );

     Transformer trans = transFact.newTransformer(xsltSource);
     
     trans.transform(xmlSource, result);
     }
     catch(Exception e){
     //System.err.println("error applying the xslt file "+e);
     e.printStackTrace();
     };
    return ;
    }
    
   
    
}
