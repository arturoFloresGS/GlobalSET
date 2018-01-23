package com.webset.set.reportes.business;

import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.webset.set.bancaelectronica.dao.ReportesBancaXMLDao;

public class ReportesBusiness {
	 private static Logger logger = Logger.getLogger(ReportesBusiness.class);
	ReportesBancaXMLDao reportesBancaXmlDao = new ReportesBancaXMLDao(); //objeto de la clase dao

	public JRDataSource obtenerDatosReporte(String nomReporte, Map<String, Object> parameters){
		JRMapArrayDataSource  jrDataSource = null; 
	    XStream xStream = new XStream(new DomDriver());
	    List<Map<String, Object>> resMap = null;
	    String resXml = "";
		try{

			if(nomReporte.equals("reportebanca2")){
				
				resMap = reportesBancaXmlDao.ejecutarReporteBE(parameters);
				logger.info("***resMap="+resMap);
				
			} else if(nomReporte.equals("reporteCon")){
				//resMap = reportesBancaXmlDao.ejecutarReporteConcepto(parameters);
				
			} else if(nomReporte.equals("report2")){
				resMap = reportesBancaXmlDao.ejecutarReporteTest(parameters);
				logger.info("***resMap="+resMap);
				
			} else if(nomReporte.equals("reporteconcepto11")){
				resMap = reportesBancaXmlDao.ejecutarReporteConcepto(parameters);
				logger.info("***resMap conc="+resMap);
				
			} else if(nomReporte.equals("reportebanca1")){
				resMap = reportesBancaXmlDao.ejecutarReporteBE(parameters);
				logger.info("***resMap be="+resMap);
				
			} 

			// convert to XML 
			//xStream.alias("map", java.util.Map.class);
			xStream.alias("map",  java.util.List.class);
			resXml = xStream.toXML(resMap.toArray());
			//logger.info("***xml="+resXml);
			
			String [] cadenas = new String[2];
            jrDataSource = new JRMapArrayDataSource(resMap.toArray());
			//xmlDataSource = new JRXmlDataSource(resXml, "/");

		}catch(Exception e){
			//bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:SET, C:ReportesAction, M:obtenerXMLReporte");
		}
		return jrDataSource;
	}

	
	//getters && setters
	public ReportesBancaXMLDao getReportesBancaXmlDao() {
		return reportesBancaXmlDao;
	}

	public void setReportesBancaXmlDao(ReportesBancaXMLDao reportesBancaXmlDao) {
		this.reportesBancaXmlDao = reportesBancaXmlDao;
	}

	
}
