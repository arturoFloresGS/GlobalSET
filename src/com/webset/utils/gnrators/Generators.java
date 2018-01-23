/*
 * Generators.java
 *
 * Created on 6 de octubre de 2008, 04:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.webset.utils.gnrators;

import javax.servlet.http.*;

import org.apache.log4j.Logger;

import java.util.*;

/**
 *
 * @author ARM
 */
public class Generators {
	private static Logger logger = Logger.getLogger(Generators.class);
   /** Creates a new instance of Generators */
   public Generators() {
   }
   
   public ArrayList<String> getListColumnNames(HttpServletRequest req, String sParmDataCol, String sParmHeaderCol){
      // GENERATE A LIST WITH COLUMN NAMES
      //String sColumns = "";
      String sSqlColFinal = null;
      String sSqlColDataName = null;
      String sSqlColDataExp = null;
   
      ArrayList<String> listCols = new ArrayList<String>(0);  
      int j=1;
      String sNomParm = sParmDataCol+j;
      for(; req.getParameter(sNomParm) != null; j++, sNomParm=sParmDataCol+j){
	 sSqlColDataExp = req.getParameter(sNomParm).toString(); // expression of the sql column
	 sSqlColDataName = req.getParameter(sParmDataCol+"Name"+j)==null ? "":req.getParameter(sParmDataCol+"Name"+j).toString();
	 sSqlColFinal = sSqlColDataName.equals("") ? sSqlColDataExp : sSqlColDataExp+" "+sSqlColDataName;

	 listCols.add(sSqlColFinal);
      }
      return listCols;
   }

public static Logger getLogger() {
	return logger;
}

public static void setLogger(Logger logger) {
	Generators.logger = logger;
}
   
}
