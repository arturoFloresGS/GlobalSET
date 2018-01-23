<%@ page session="true" %>
<%@ page import  = "java.math.*, 
                 java.util.*,
                 java.io.*,
                 com.thoughtworks.xstream.XStream,
                 com.thoughtworks.xstream.io.xml.DomDriver, 
                 com.webset.set.utilerias.Contexto,
                 com.webset.utils.connection.ConnectionDBDatasource,
                 com.webset.set.reportes.action.ReportesAction,
                 com.webset.set.coinversion.action.CoinversionAction,
                 com.webset.set.caja.action.CajaAction,
                 com.webset.set.inversiones.action.InversionesAction,
                 com.webset.set.inversiones.action.BarridoInversionAction,
                 com.webset.set.consultas.action.ConsultasAction,
                 com.webset.set.prestamosinterempresas.action.PrestamosInterempresasAction,
                 com.webset.set.conciliacionbancoset.action.ConciliacionBancoSetAction,
                 com.webset.set.egresos.action.PagoPropuestasAction,
                 com.webset.set.egresos.action.ConsultaPropuestasAction,
                 com.webset.set.flujos.action.CashFlowNAction,
                 com.webset.set.bancaelectronica.action.EnvioTransferenciasAction,
                 com.webset.set.bancaelectronica.action.EnvioTransferenciasAAction,
                 com.webset.set.traspasos.action.TraspasosAction,
                 com.webset.set.egresos.action.ReporteDeCuposAction,
                 com.webset.set.impresion.action.ImpresionAction,
                 java.sql.Connection,
                 javax.sql.DataSource,
				 org.springframework.jdbc.core.JdbcTemplate,
				 org.springframework.jdbc.core.RowMapper,
				 org.springframework.jdbc.datasource.DriverManagerDataSource,
                 javax.naming.InitialContext,
                 net.sf.jasperreports.engine.*,
		 		 net.sf.jasperreports.engine.fill.*,
                 net.sf.jasperreports.engine.design.JasperDesign,
                 net.sf.jasperreports.engine.xml.JRXmlLoader,
                 net.sf.jasperreports.engine.data.JRXmlDataSource,
                 net.sf.jasperreports.engine.data.JRMapArrayDataSource,
                 net.sf.jasperreports.engine.export.*,
                 net.sf.jasperreports.engine.JasperFillManager,
                 net.sf.jasperreports.engine.JasperPrint,
				 net.sf.jasperreports.engine.JasperReport,
                 net.sf.jasperreports.view.JasperViewer,
                 net.sf.jasperreports.engine.JasperExportManager,
				 net.sf.jasperreports.engine.JasperCompileManager,
                 com.softwarementors.extjs.djn.servlet.ssm.WebContext,
				 com.softwarementors.extjs.djn.servlet.ssm.WebContextManager"%>

<%
	Contexto contexto = new Contexto();
	ServletContext context = request.getSession().getServletContext();
	JasperPrint jasperPrint = null;
	WebContext contextoWeb = WebContextManager.get();
	
	//JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
	

	//DriverManagerDataSource dataSource = new DriverManagerDataSource();
	//dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	//dataSource.setUrl("jdbc:sqlserver://localhost:1433;database=WEBSET");
	//dataSource.setUsername("webset");
	//dataSource.setPassword("webset");
	ReportesAction reportesAction = new ReportesAction();
	CoinversionAction coinversionAction = new CoinversionAction();
	CajaAction cajaAction = new CajaAction();
	InversionesAction inversionesAction = new InversionesAction();
	BarridoInversionAction barridoInversionAction = new BarridoInversionAction();
	ConsultasAction consultasAction = new ConsultasAction();
	PrestamosInterempresasAction prestamosInterempresasAction = new PrestamosInterempresasAction();
	ConciliacionBancoSetAction conciliacionAction = new ConciliacionBancoSetAction();
	PagoPropuestasAction pagoPropuestasAction = new PagoPropuestasAction();
	ConsultaPropuestasAction consultaPropuestasAction = new ConsultaPropuestasAction();
	CashFlowNAction cashFlowNAction = new CashFlowNAction();
	EnvioTransferenciasAction envioTransferenciasAction = new EnvioTransferenciasAction();
	EnvioTransferenciasAAction envioTransferenciasAAction = new EnvioTransferenciasAAction();
	TraspasosAction traspasosAction = new TraspasosAction();
	ReporteDeCuposAction reporteDeCuposAction = new ReporteDeCuposAction();
	ImpresionAction impresionAction = new ImpresionAction();

	String sNombreRep = request.getParameter("nomReporte")==null?"":request.getParameter("nomReporte").toString();

	System.out.println("archivo: " + sNombreRep);
    if(sNombreRep.equals("")){
      out.print("Nombre de reporte no proporcionado.");
      //return;
    }

    String rutaServidor  = application.getRealPath("/WEB-INF/reportes/"); 
    String reportFile = rutaServidor+File.separator+sNombreRep+".jrxml";
    
    System.out.println("Imprimendo reporte: "+reportFile);
    
    Map<String, Object> params = new HashMap<String, Object>();
    Map<String, String> titu = new HashMap<String, String>();
    Map<String, String> datos = new HashMap<String, String>();
    List<JRDataSource> listJR = new ArrayList<JRDataSource>();    
    
    try {       
		System.out.println("JCarlitos 0 ");
		JRDataSource jrDataSource = conciliacionAction.obtenerReporteMonitor(datos, context);

		System.out.println("JCarlitos 1 ");
		Map<String, String[]> parameters = request.getParameterMap();
		System.out.println("Los parámetros son "+ parameters);
		for(String parameter : parameters.keySet()) {
		    //if(parameter.toLowerCase().startsWith("question")) {
		        String[] values = parameters.get(parameter);
		        System.out.println(values[0] + " " +parameter.substring(parameter.lastIndexOf(".")+1));
		        datos.put(parameter.substring(parameter.lastIndexOf(".")+1), values[0]);
		    //}
		}

		params.put("REPORT_DATA_SOURCE", jrDataSource);
		params.put("TITULO", "REPORTE");
		params.put("nombreEmpresa", request.getParameter("nomEmpresa").toString());
		System.out.println(params);
		
		Locale locale = new Locale("es", "MX");
		params.put(JRParameter.REPORT_LOCALE, locale);
		
		JasperReport jasperReport = JasperCompileManager.compileReport(reportFile);
		jasperPrint = JasperFillManager.fillReport(jasperReport, params);

		// REUSAR params
		//params.clear();
		
		// PARA MOSTRAR EN VISOR (NO ES CORRECTO USARLO EN UNA APP WEB)
		/*
		jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource.getConnection());
		JasperViewer visor = new JasperViewer(jasperPrint, true);
		visor.setVisible(true);
		*/
		
		//byte[] bytes = JasperRunManager.runReportToPdf(jasperReport, params, dataSource.getConnection());
		byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
		//JasperExportManager.exportReportToPdfFile(jasperPrint, "C://prueba2.pdf");
		
		response.setContentType("application/pdf");
		response.setContentLength(bytes.length);
		ServletOutputStream ouputStream = response.getOutputStream();
		ouputStream.write(bytes, 0, bytes.length);
		ouputStream.flush();
		ouputStream.close();
			
		// PARA GUARDAR EL ARCHIVO PDF
		//JasperExportManager.exportReportToPdfFile(jasperPrint, "reporte1.pdf");
		//out.print("Reporte generado correctamente");
}
    catch (Exception e){
      System.out.println("Error al generar el PDF: "+e.getMessage());
      out.print("Error al generar el PDF: "+e.getMessage()+"");
      e.printStackTrace();
   }
%>
