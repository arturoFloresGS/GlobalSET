package com.webset.set.bancaelectronica.business;

import java.util.List;
import java.util.Map;

import com.webset.set.bancaelectronica.dao.ReportesBancaXMLDao;

/**
 * clase que contiene las reglas de negocio para generar los reportes
 * @author Jessica Arelly Cruz Cruz
 * @since 11/03/2011
 *
 */
public class ReportesBancaXMLBusiness {
	ReportesBancaXMLDao reportesBancaXmlDao = new 	ReportesBancaXMLDao();
	
	public List<Map<String,Object>> ejecutarReporteConcepto(Map<String, Object> datos){
		System.out.println("business "+datos);
		return reportesBancaXmlDao.ejecutarReporteConcepto(datos);
	}
	
	public List<Map<String, Object>> ejecutarReporteBE(Map<String, Object> datos){
		System.out.println("business be "+ datos);
		return reportesBancaXmlDao.ejecutarReporteBE(datos);
	}

	public ReportesBancaXMLDao getReportesBancaXmlDao() {
		return reportesBancaXmlDao;
	}

	public void setReportesBancaXmlDao(ReportesBancaXMLDao reportesBancaXmlDao) {
		this.reportesBancaXmlDao = reportesBancaXmlDao;
	}

}
