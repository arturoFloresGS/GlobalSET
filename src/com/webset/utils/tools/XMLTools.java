/*
 * XMLTools.java
 *
 * Created on 20 de agosto de 2008, 02:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.webset.utils.tools;

//import org.apache.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;



/**
 *
 * @author Administrador
 */
public class XMLTools {
   
	private static Logger logger = Logger.getLogger(XMLTools.class);
   /** Creates a new instance of XMLTools */
   public XMLTools() {
   }
   
   public String DOM2String(Document doc) 
   { 
       TransformerFactory transformerFactory =TransformerFactory.newInstance(); 
       Transformer transformer = null; 
       try{ 
    	   transformer = transformerFactory.newTransformer(); 
       }
       catch (javax.xml.transform.TransformerConfigurationException error){ 
    	   logger.error(error.getLocalizedMessage(),error);
    	   return null; 
       } 

       Source source = new DOMSource(doc); 
       StringWriter writer = new StringWriter(); 
       Result result = new StreamResult(writer); 
       try{ 
    	   transformer.transform(source,result); 
       }
       	catch (javax.xml.transform.TransformerException error){ 
       		logger.error(error.getLocalizedMessage(),error);
       		return null; 
       } 

       String s = writer.toString(); 
       return s; 
   } 

   public Document string2DOM(String s) 
   { 
       Document tmpX=null; 
       DocumentBuilder builder = null; 
       try{ 
    	   builder = DocumentBuilderFactory.newInstance().newDocumentBuilder(); 
       }
       catch(javax.xml.parsers.ParserConfigurationException error){ 
    	   logger.error(error.getLocalizedMessage(),error);
    	   return null; 
       } 

       try{ 
    	   tmpX=builder.parse(new ByteArrayInputStream(s.getBytes())); 
       }catch(org.xml.sax.SAXException error){ 
    	   logger.error(error.getLocalizedMessage(),error);
    	   return null; 
       }catch(IOException error){ 
    	   logger.error(error.getLocalizedMessage(),error);
    	   return null; 
       } 
       return tmpX; 
   }  
         
   
}
