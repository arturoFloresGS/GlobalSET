 package com.webset.utils.logging;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.xml.DOMConfigurator;

public class InitializeLogging extends HttpServlet{
     public void init(){
       String prefix = getServletContext().getRealPath("/");
       String filename= getInitParameter("log4j-config-file");
       if( filename != null ){
    	   DOMConfigurator.configure( prefix+filename );          
     }}
    public void doGet( HttpServletRequest req, HttpServletResponse res ){
    }
}
