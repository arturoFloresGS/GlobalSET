<%@page import="com.webset.set.inversiones.action.InversionesRepAction"%>
<%@page import="net.sf.jasperreports.engine.JasperCompileManager"%>
<%@page import="net.sf.jasperreports.engine.JasperReport"%>
<%@page import="net.sf.jasperreports.engine.JRParameter"%>
<%@page import="java.util.Locale"%>
<%@page import="net.sf.jasperreports.engine.data.JRXmlDataSource"%>
<%@page import="java.io.FileWriter"%>
<%@page import="java.io.BufferedWriter"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="java.io.File"%>
<%@page import="java.io.FileNotFoundException"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.io.InputStream"%>
<%@page import="net.sf.jasperreports.engine.JRDataSource"%>
<%@page import="net.sf.jasperreports.engine.JasperExportManager"%>
<%@page import="net.sf.jasperreports.engine.JasperFillManager"%>
<%@page import="net.sf.jasperreports.engine.JasperPrint"%>
<%@page import="net.sf.jasperreports.engine.data.JRMapArrayDataSource"%>
<%@page import="javax.xml.parsers.DocumentBuilder"%>
<%@page import="javax.xml.parsers.DocumentBuilderFactory"%>
<%@page import="javax.xml.parsers.ParserConfigurationException"%>
<%@page import="org.w3c.dom.Document"%>
<%@page import="org.xml.sax.SAXException"%>
<%@page import="net.sf.jasperreports.engine.data.JRXmlDataSource"%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>REPORTE</title>
</head>
<body>
<%
try{
	String hdJason = null;
	
	String noEmpresa = request.getParameter("noEmpresa");
	String idUsuario = request.getParameter("idUsuario");
	String sNombreRep = request.getParameter("nomReporte");
	String sNomEmpresa = request.getParameter("EMPRESA");
	String sFecha = null;
	String sFechaFin = null;
	String idDivisa = null;
	String descDivisa = null;
	String tipoInversion = null;
	
	if (sNombreRep.equals("ReporteCotizaciones")){
		sFecha = request.getParameter("fecha");
	}else if (sNombreRep.equals("ReporteInversiones") || sNombreRep.equals("ReporteVencimiento") 
			|| sNombreRep.equals("ReportePosicionInv") || sNombreRep.equals("ReporteSaldosInv")){
		sFecha = request.getParameter("fecha");
		sFechaFin = request.getParameter("fechaFin");
		idDivisa = request.getParameter("idDivisa");
		descDivisa = request.getParameter("divisa");
	}
	else if (sNombreRep.equals("ReporteLiquidacionInv"))
		tipoInversion = request.getParameter("tipoInversion");
	
	System.out.println("sNombreRep "+sNombreRep);
	
	JRMapArrayDataSource jrDataSource = null;
	System.out.println("JCarlitos 0");
	JasperPrint jasperPrint = null;
	System.out.println("JCarlitos 1");
	InversionesRepAction accion = new InversionesRepAction();
	System.out.println("JCarlitos 2");
	ServletContext context = request.getSession().getServletContext();
	
	System.out.println("JCarlitos 3");
	List<Map<String, Object>> lista = null;
	System.out.println("JCarlitos 4");
	
	if (sNombreRep.equals("ReporteCotizaciones"))
		lista = accion.obtenerReporteCotizacion(sFecha, Integer.parseInt(noEmpresa), context);
	else if (sNombreRep.equals("ReporteInversiones"))
		lista = accion.obtenerReporteInvEstablecidas(Integer.parseInt(noEmpresa), idDivisa, sFecha, sFechaFin, Integer.parseInt(idUsuario), context);
	else if (sNombreRep.equals("ReporteVencimiento"))
		lista = accion.obtenerReporteVencimientos(Integer.parseInt(noEmpresa), idDivisa, sFecha, sFechaFin, Integer.parseInt(idUsuario), context);
	else if (sNombreRep.equals("ReportePosicionInv"))
		lista = accion.obtenerReportePosicionInv(Integer.parseInt(noEmpresa), idDivisa, sFecha, sFechaFin, Integer.parseInt(idUsuario), context);
	else if (sNombreRep.equals("ReporteSaldosInv")){
		System.out.println("Antes del servicio");
		lista = accion.obtenerReporteSaldosInv(Integer.parseInt(noEmpresa), idDivisa, sFecha, sFechaFin, Integer.parseInt(idUsuario), context);
		System.out.println("Despues del servicio");
	}else if (sNombreRep.equals("ReporteLiquidacionInv")){
		System.out.println("Antes del servicio");
		lista = accion.obtenerReporteLiquidacion(Integer.parseInt(noEmpresa), tipoInversion, context);
		System.out.println("Despues del servicio");
	}
	
	
	String rutaServidor  = application.getRealPath("/WEB-INF/reportes/"); 
    String reportFile = rutaServidor + File.separator + sNombreRep+".jrxml";

    System.out.println("Reporte: <" + reportFile + "> <" + sNomEmpresa + ">");
    
    jrDataSource = new JRMapArrayDataSource(lista.toArray());
    Map<String, Object> parametros = new HashMap<String, Object>();
    List<Map<String, Object>> valores = new ArrayList<Map<String,Object>>();
    
    JasperReport jasperReport = JasperCompileManager.compileReport(reportFile);
    System.out.println("Despues de compile");
    parametros.put("REPORT_DATA_SOURCE", jrDataSource);
    parametros.put("EMPRESA", sNomEmpresa);
	
	if (sNombreRep.equals("ReporteCotizaciones"))
		parametros.put("FECHA", sFecha);
	else if (sNombreRep.equals("ReporteInversiones") || sNombreRep.equals("ReporteVencimiento") 
			|| sNombreRep.equals("ReportePosicionInv") || sNombreRep.equals("ReporteSaldosInv")){
		parametros.put("FECHA", sFecha);
		parametros.put("FECHA_FIN", sFechaFin);
		parametros.put("DIVISA", descDivisa);
	}
	
	Locale locale = new Locale("es", "MX");
	parametros.put(JRParameter.REPORT_LOCALE, locale);
	System.out.println("Antes de fill");
	jasperPrint = JasperFillManager.fillReport(jasperReport, parametros);
	System.out.println("Despues de fill");
	byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
	response.setContentType("application/pdf");
	
	response.setHeader("Content-Disposition", "inline; filename=\"" + sNombreRep + ".pdf\";");
	response.setHeader("Cache-Control", "no-cache");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
	
	response.setContentLength(bytes.length);
	ServletOutputStream ouputStream = response.getOutputStream();
	ouputStream.write(bytes, 0, bytes.length);
	ouputStream.flush();
	ouputStream.close();

}catch (Exception e){
	e.printStackTrace();
	System.out.println("Error en el reporte: " + e);
}

%>
</body>
</html>
