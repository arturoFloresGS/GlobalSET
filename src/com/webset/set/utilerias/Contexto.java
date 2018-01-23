package com.webset.set.utilerias;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;

import com.softwarementors.extjs.djn.servlet.ssm.WebContext;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
/**
 * 
 * @author Sergio Vaca
 *
 */
public class Contexto {
	private static Logger logger = Logger.getLogger(Contexto.class);
	/**
	 * 
	 * @param nombreBean
	 * @return Object
	 * 
	 * Retorna el bean para solo recibirlo y hacer un cast al objeto correspondiente
	 */
	public Object obtenerBean(String nombreBean){
		XmlBeanFactory beanFactory = null; 
		
		try{
			//logger.info("obtenerBean . Ruta actaul()="+ System.getProperty("user.dir"));
			// OBTENER CONTEXTO
			WebContext context = WebContextManager.get();
			ServletContext application = context.getServletContext();
			// OBTENER PATH
			logger.info("OBTENER PATH application="+application);
			//logger.info("OBTENER PATH getContextPath()="+application.getContextPath());
			
			//String sRealPath = application.getRealPath(application.getContextPath());
			
			// TOMCAT ONLY
			//System.getProperty("catalina.home"); // for $CATALINA_HOME
			//System.getProperty("catalina.base"); // for $CATALINA_BASE
			
	        String webAppPath = application.getRealPath("/");

			//logger.info("OBTENER PATH sRealPath="+sRealPath);
			logger.info("OBTENER PATH webAppPath="+webAppPath);
			
			//sRealPath = sRealPath.substring(0, sRealPath.length()-4);
			// GENERAR EL BUSSINESS DESDE BEAN FACTORY
			beanFactory = new XmlBeanFactory( new FileSystemResource(webAppPath+"/WEB-INF/applicationContext.xml"));
		} catch(Exception e){
			logger.error(e.toString());
			e.printStackTrace();
		}
		logger.info("*** Obteniendo bean:"+nombreBean+"="+beanFactory.getBean(nombreBean));
		return beanFactory.getBean(nombreBean);
	}

	public Object obtenerBean(String nombreBean, ServletContext context){
		XmlBeanFactory beanFactory = null; 
		
		try{
			//logger.info("obtenerBean . Ruta actaul()="+ System.getProperty("user.dir"));
			// OBTENER CONTEXTO
			ServletContext application = context;
			// OBTENER PATH
			logger.info("OBTENER PATH application="+application);
			//logger.info("OBTENER PATH getContextPath()="+application.getContextPath());
			
			//String sRealPath = application.getRealPath(application.getContextPath());
			
			// TOMCAT ONLY
			//System.getProperty("catalina.home"); // for $CATALINA_HOME
			//System.getProperty("catalina.base"); // for $CATALINA_BASE
			
	        String webAppPath = application.getRealPath("/");

			//logger.info("OBTENER PATH sRealPath="+sRealPath);
	        logger.info("OBTENER PATH webAppPath="+webAppPath);
			
			//sRealPath = sRealPath.substring(0, sRealPath.length()-4);
			// GENERAR EL BUSSINESS DESDE BEAN FACTORY
			beanFactory = new XmlBeanFactory( new FileSystemResource(webAppPath+"/WEB-INF/applicationContext.xml"));
			
		} catch(Exception e){
			logger.error(e.toString());
			e.printStackTrace();
		}
		logger.info("*** Obteniendo bean:"+nombreBean+"="+beanFactory.getBean(nombreBean));
		return beanFactory.getBean(nombreBean);
	}
	
	
}
