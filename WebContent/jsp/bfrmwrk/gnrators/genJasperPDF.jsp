
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
                 com.webset.set.utilerias.action.MantenimientoConceptosAction,
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
				 com.softwarementors.extjs.djn.servlet.ssm.WebContextManager,
				 com.webset.set.egresos.dto.ConfirmacionCargoCtaDto,
				 com.webset.set.egresos.action.ConfirmacionCargoCtaAction,
                 com.webset.set.egresos.business.GestionDeOperacionesDivisasBo"%>


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
	ConfirmacionCargoCtaAction confirmacionCargoCtaAction = new ConfirmacionCargoCtaAction();
	MantenimientoConceptosAction mantenimientoConceptosAction = new MantenimientoConceptosAction();  

	String sNombreRep = request.getParameter("nomReporte")==null?"":request.getParameter("nomReporte").toString();
	String folioRepCVD = request.getParameter("folioReporte")==null?"":request.getParameter("folioReporte").toString();
	String foliosRepAutCVD = request.getParameter("foliosReporte")==null?"":request.getParameter("foliosReporte").toString();

	System.out.println("archivo: " + sNombreRep);
    if(sNombreRep.equals("")){
      out.print("Nombre de reporte no proporcionado.");
      return;
    }

    String rutaServidor  = application.getRealPath("/WEB-INF/reportes/"); 
    String reportFile = rutaServidor+File.separator+sNombreRep+".jrxml";
    
    System.out.println("Imprimendo reporte: "+reportFile);
    
    Map params = new HashMap();
    Map titu = new HashMap();
    List<JRDataSource> listJR = new ArrayList<JRDataSource>();  
    Map<String, Object> dtoCvd = null;
    
    try {            
      //ConnectionDBDatasource connection = new ConnectionDBDatasource();

      // *** OJO: ESTOS SON LOS EJEMPLOS DE PARAMETROS
      /*
      params.put( "mes", new Long("1"));
      params.put("banco", "B044");
      params.put("concepto", "ENERO 2007");
      */
      int j=1;
      String sNomVarParm = "nomParam"+j;
      String sNomValParm = "valParam"+j;
      for(; request.getParameter(sNomVarParm) != null && !request.getParameter(sNomVarParm).toString().equals(""); j++, sNomVarParm="nomParam"+j, sNomValParm = "valParam"+j){
      	System.out.print(sNomVarParm+"=["+request.getParameter(sNomVarParm)+"]");
      	System.out.println(", "+sNomValParm+"=["+request.getParameter(sNomValParm)+"]");
		params.put(request.getParameter(sNomVarParm).toString(), request.getParameter(sNomValParm).toString());
	  }

      //JasperDesign jasperDesign = JRXmlLoader.load(reportFile);
      //JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
      JRDataSource jrDataSource = null;
      boolean bListJRDS = false;
      
		if(sNombreRep.equals("ReporteChequesXChequera1") || sNombreRep.equals("ReporteChequesXCaja1")) {
			jrDataSource = reportesAction.obtenerDatosReporteCheque(sNombreRep, params, context);
		}else if(sNombreRep.equals("reportebanca1") || sNombreRep.equals("reporteconcepto11")){
	  		jrDataSource = reportesAction.obtenerDatosReporte(sNombreRep, params, context);
  		}else if(sNombreRep.equals("ReporteMttoConiv"))
  		{
	  		jrDataSource = coinversionAction.obtenerReporteCoinversion(sNombreRep, params, context);
  		}
  		else if (sNombreRep.equals("reporteChequesPorEntregar") || sNombreRep.equals("reporteChequesEntregados")
  			|| sNombreRep.equals("reporteHistoricoChequesEntregados") || sNombreRep.equals("reporteChequesEntregadosPorCaja")
  			|| sNombreRep.equals("reporteHistoricoChequesEntregadosPorCaja"))
  		{
  			jrDataSource = cajaAction.obtenerDatosReporteCheque(sNombreRep, params, context);
  		}
  		else if(sNombreRep.equals("ReporteLiquidacionDeInversiones"))
  		{
  			jrDataSource = inversionesAction.obtenerReporteLiquidacionInversion(params, context);
  			System.out.println("jrDataSource "+ jrDataSource);
  		}else if(sNombreRep.equals("ComprobanteVencimiento")) {
			jrDataSource = inversionesAction.obtenerReporteVencimientoInversion(params, context);
  			System.out.println("jrDataSource "+ jrDataSource);
		}
  		else if(sNombreRep.equals("ReporteConsultaMovimientos"))
  		{
  			jrDataSource = consultasAction.obtenerReporteMovimientos(params, context, contextoWeb);
  		}
  		else if(sNombreRep.equals("ReporteSaldoPromedioGrid"))
  		{
  			jrDataSource = coinversionAction.obtenerReporteSaldoPromedio(params,context);
  		}
  		else if(sNombreRep.equals("ReporteTasasInversion"))
  		{
  			jrDataSource = coinversionAction.obtenerReporteTasasInversion(params,context);
  		}
  		else if(sNombreRep.equals("ReporteEstadoCuenta"))
  		{
  			jrDataSource = coinversionAction.obtenerReporteEstadoCuenta(params,context);
  		}
  		else if(sNombreRep.equals("ReporteHistoricoSaldos"))
  		{
  			jrDataSource = coinversionAction.obtenerReporteHistoricoSaldos(params,context);
  		}
  		else if(sNombreRep.equals("InvEstablecidasHoy") || sNombreRep.equals("VencimientoDeInversiones")
  				|| sNombreRep.equals("VencimientoDeInversionesSal2Prom") || sNombreRep.equals("InversionesDiarias")) {
  			jrDataSource = inversionesAction.obtenerReportesInversion(params, context);
  		}
  		else if(sNombreRep.equals("ReportePrestamosNoDocumentados"))
  		{
  			jrDataSource = prestamosInterempresasAction.obtenerReportePrestamosNoDoc(params, context);
  		}
  		else if(sNombreRep.equals("ReporteInteresNeto"))
  		{
  			jrDataSource = prestamosInterempresasAction.obtenerRepInteresNeto(params, context);
  		}
  		else if(sNombreRep.equals("ReporteEstadoDeCuentaDeCredito") || sNombreRep.equals("ReporteEstadoDeCuentaDeCreditoDet"))
  		{
  			jrDataSource = prestamosInterempresasAction.obtenerRepEstadoDeCuentaDeCredito(params, context);
  		}
  		else if(sNombreRep.equals("ReporteCancelaConciliados"))
  		{
  			jrDataSource = conciliacionAction.obtenerReporteCancelados(params,context);
  		}
  		else if(sNombreRep.equals("ReporteMovsFicticios"))
  		{
  			jrDataSource = conciliacionAction.obtenerReporteMovsFicticios(params,context);
  		}
  		else if(sNombreRep.equals("ReporteMovsDuplicados"))
  		{
  			jrDataSource = conciliacionAction.obtenerReporteMovsDuplicados(params, context);
  		}
  		else if(sNombreRep.equals("ReporteMovimientosAclarados") || sNombreRep.equals("ReporteConciliadosAutomaticamente")
  				|| sNombreRep.equals("ReporteMovimientosDetectadosyPendientes") || sNombreRep.equals("ReporteMovimientosAjustados")
  				|| sNombreRep.equals("ReporteCargaInicial"))
		{
			bListJRDS = true;
			System.out.println("Creando el reporte "+ sNombreRep);
			listJR = conciliacionAction.obtenerReporteDetalle(params, context);
		}
		else if(sNombreRep.equals("ReporteGlobalDetectadosyPendientes") || sNombreRep.equals("ReporteGlobalPorConciliar")
				|| sNombreRep.equals("ReporteGlobalMonitor"))
		{
			jrDataSource = conciliacionAction.obtenerReporteGlobal(params,context);
		}
  		else if(sNombreRep.equals("ReporteSolicitudesDeCredito"))
  		{
  			jrDataSource = prestamosInterempresasAction.obtenerReporteDeSolicitudes(params, context);
  		}
  		else if(sNombreRep.equals("ReporteFlujoNeto") || sNombreRep.equals("ReporteFlujoNetoSectores"))
  		{
  			jrDataSource = prestamosInterempresasAction.obtenerReporteFlujoNeto(params, context);
  		}
		else if(sNombreRep.equals("ReporteCompraVentaDeDivisas")){
  			
  			dtoCvd = confirmacionCargoCtaAction.contratoCompraVentaDeDivisas( params, context, folioRepCVD );
		
		}else if(sNombreRep.equals("ReporteCompraTransfer")){
	  			
	  			dtoCvd = confirmacionCargoCtaAction.contratoCompraTransfer( params, context, folioRepCVD );
			
  		}else if(sNombreRep.equals("ReporteAutCompraVentaDeDivisas")){
  			
  			GestionDeOperacionesDivisasBo gestionDeOperacionesDivisasBo = (GestionDeOperacionesDivisasBo)contexto.obtenerBean("gestionDeOperacionesDivisasBo",context);  			
  			jrDataSource  = gestionDeOperacionesDivisasBo.operacionesVentaDeDivisasParaImprimir(foliosRepAutCVD);
  		
  		}else if(sNombreRep.equals("ReporteAutCompraVentaDeTransfer")){
			
			GestionDeOperacionesDivisasBo gestionDeOperacionesDivisasBo = (GestionDeOperacionesDivisasBo)contexto.obtenerBean("gestionDeOperacionesDivisasBo",context);  			
			jrDataSource  = gestionDeOperacionesDivisasBo.operacionesVentaDeTransferParaImprimir(foliosRepAutCVD);
		
		}
  		else if(sNombreRep.equals("ReporteArbolEmpPresInter")){
  			jrDataSource = prestamosInterempresasAction.obtenerReporteArbolEmpresa(context);
  		}else if(sNombreRep.equals("PagoPropuestas") || sNombreRep.equals("PagoPropuestasDetalle")) {
  			jrDataSource = pagoPropuestasAction.obtenerReportePagoPropuestas(params, context);
  		}else if(sNombreRep.equals("PosicionChequeras")) {
  			jrDataSource = consultasAction.reportePosicionChequeras(params, context);
  		}else if(sNombreRep.equals("CuentasPersonales")) {
  			jrDataSource = consultasAction.reporteCuentasPersonales(params, context);
  		}else if(sNombreRep.equals("ContratosProveedores")) {
  			jrDataSource = consultasAction.reporteContratosProveedores(params, context);
  		}else if(sNombreRep.equals("reporteDetalleCupos")) {
  			jrDataSource = consultaPropuestasAction.reporteDetalleCupos(params, context);
  		}else if(sNombreRep.equals("reporteDetalleFlujo")) {
  			jrDataSource = cashFlowNAction.reporteDetalleFlujo(params, context);
  		}else if(sNombreRep.equals("detArchivoTransf")) {
			jrDataSource = envioTransferenciasAction.reporteDetArchivoTransf(params, context);
  		}else if(sNombreRep.equals("detArchivoTransf2")) {
			jrDataSource = envioTransferenciasAAction.reporteDetArchivoTransf(params, context);
		}else if(sNombreRep.equals("detArchTraspInv")) {
			jrDataSource = traspasosAction.reporteDetArchTraspInv(params, context);
		}else if(sNombreRep.equals("propuestas")) {
			jrDataSource = consultaPropuestasAction.reporteConSelecAut(params, context);
		}else if(sNombreRep.equals("TransferenciasConfirmadas") || sNombreRep.equals("TransferenciasTransferidas") || sNombreRep.equals("TraspasosCuentas")) {
			jrDataSource = reportesAction.reporteTransConfirmadas(params, context);
		}else if(sNombreRep.equals("reporteCupos")){
  			jrDataSource = reporteDeCuposAction.obtenerReporte(params,context);
  		}else if(sNombreRep.equals("reporteCuposProvr")){
  			jrDataSource = reporteDeCuposAction.obtenerReporte(params,context);
  		}else if(sNombreRep.equals("chequeContinua"))
  		{
  			try {
			impresionAction.obtenerReporteCheque(params,context);
			
  			}catch(Exception e){ e.printStackTrace(); }
  			/*
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			//response.setContentType("application/vnd.ms-excel");
			System.out.println("Proceso lectura-envio Excel: Start");
			//FileInputStream in = new FileInputStream("C:\\set\\invoiceTemplate.xls");
			
			FileInputStream in = new FileInputStream("C:\\Users\\vtello\\Downloads\\invoiceTemplate.xls");
			response.setContentType("application/excel");
			response.setHeader("Content-Disposition", "attachment; filename=invoiceTemplate.xls");
			response.setHeader("Pragma", "no-cache");
			
			int i;
			PrintWriter pw = response.getWriter();
			while ((i=in.read()) != -1) {
			   pw.write(i);
			}
			in.close();
			pw.close();
			System.out.println("Proceso lectura-envio Excel: OK");
			
			*/
  		}else if(sNombreRep.equals("Barridos") || sNombreRep.equals("CanastasPendientes") || sNombreRep.equals("CanastasVencimientos") || 
  			     sNombreRep.equals("InversionesDetalladas") || sNombreRep.equals("CanastasVencenHoy")) {
  			jrDataSource = barridoInversionAction.reportesCanasta(params, context);
  		} else if (sNombreRep.equals("prueba")) {
  			jrDataSource = coinversionAction.reportePrueba(params, context);
  		} else if(sNombreRep.equals("ReporteSaldosDeChequerasS")) {
			jrDataSource = consultasAction.obtenerDatosReporteSaldosS(params, context);
  		}else if (sNombreRep.equals("ReporteSaldoHistoricoDeChequerasF")){
  			ConsultasAction accion = new ConsultasAction();
  			List<Map<String, Object>> lista = accion.reporteChequeraDetalleMov(params, context);
  			
  			if(lista.size() > 0){
	  			params.put("saldoInicial", lista.get(0).get("saldoI"));
	  			params.put("saldoFinal", lista.get(lista.size()-1).get("SALDO"));
  			}else{
	  			params.put("saldoInicial", 0D);
	  			params.put("saldoFinal", 0D);
	  			
	  			lista = new ArrayList<Map<String, Object>>();
	  			
	  			Map<String, Object> mapa = new HashMap<String, Object>();
	  			
	  			mapa.put("TIPO_MOVTO", "CARGO");
	  			lista.add(mapa);
	  			
	  			mapa = new HashMap<String, Object>();
	  			mapa.put("TIPO_MOVTO", "ABONO");
	  			
	  			lista.add(mapa);
  			}
  			
  			jrDataSource = new JRMapArrayDataSource(lista.toArray());
  			params.put("chequera", params.get("idChequera"));
  			//condicion agregada//
  		}else if(sNombreRep.equals("ReporteConceptos")){
  			jrDataSource = mantenimientoConceptosAction.obtenerDatosReporteConcepto(sNombreRep, params,context);
  		//fin de condicion
  		}else if (sNombreRep.equals("ReporteSaldoHistoricoDeChequerasA")){
  			ConsultasAction accion = new ConsultasAction();
  			List<Map<String, Object>> lista = accion.reporteChequeraMovimientos(params, context);
  			
  			if(lista.size() > 0){
	  			params.put("saldoInicial", lista.get(0).get("SALDO"));
	  			params.put("saldoFinal", lista.get(lista.size()-1).get("SALDO"));
  			}else{
	  			params.put("saldoInicial", 0D);
	  			params.put("saldoFinal", 0D);
	  			
	  			lista = new ArrayList<Map<String, Object>>();
	  			
	  			Map<String, Object> mapa = new HashMap<String, Object>();
	  			
	  			mapa.put("TIPO_MOVTO", "CARGO");
	  			lista.add(mapa);
	  			
	  			mapa = new HashMap<String, Object>();
	  			mapa.put("TIPO_MOVTO", "ABONO");
	  			
	  			lista.add(mapa);
  			}

  			Object[] objeto = lista.toArray();
  			
  			jrDataSource = new JRMapArrayDataSource(objeto);
  			params.put("chequera", params.get("idChequera"));
  			
  		}else if(sNombreRep.equals("ReporteDetallePagoPropuestas")) {
  			jrDataSource = consultaPropuestasAction.obtenerReportePropuestasAutorizadas(params, context);
  		}
		
		if(!sNombreRep.equals("chequeContinua"))
		{
			//jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap(), xmlDataSource);
			System.out.println("nombre del reporte: " + reportFile);
			JasperReport jasperReport = JasperCompileManager.compileReport(reportFile);
			//System.out.println("pasa jasperReport");
			//ds = new JRMapArrayDataSource(valores.toArray());
	      
			if(bListJRDS)
			{
				for(int i = 0; i < listJR.size(); i ++)
				{
					params.put("REPORT_DATA_SOURCE", listJR.get(i));
					params.put("TITULO", "REPORTE");
					
					Locale locale = new Locale("es", "MX");
					params.put(JRParameter.REPORT_LOCALE, locale);
					
					jasperPrint = JasperFillManager.fillReport(jasperReport, params);
				}
			}
			else
			{
				
				if(sNombreRep.equals("ReporteCompraVentaDeDivisas") || 
						   sNombreRep.equals("ReporteCompraTransfer")
				){
							 
							 jasperPrint = JasperFillManager.fillReport(jasperReport, dtoCvd);
							
				}else{
					params.put("REPORT_DATA_SOURCE", jrDataSource);
					params.put("TITULO", "REPORTE");
					Locale locale = new Locale("es", "MX");
					params.put(JRParameter.REPORT_LOCALE, locale);
					jasperPrint = JasperFillManager.fillReport(jasperReport, params);
					
				}
				
			}
			
			
			
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
			response.addHeader("Content-Disposition", "filename=\"reporte.pdf\"");
			ServletOutputStream ouputStream = response.getOutputStream();
			ouputStream.write(bytes, 0, bytes.length);
			ouputStream.flush();
			ouputStream.close();
			
			// PARA GUARDAR EL ARCHIVO PDF
			//JasperExportManager.exportReportToPdfFile(jasperPrint, "reporte1.pdf");
			//out.print("Reporte generado correctamente");
		}
}
    catch (Exception e){
      System.out.println("Error al generar el PDF: "+e.getMessage());
      out.print("Error al generar el PDF: "+e.getMessage()+"");
      e.printStackTrace();
   }
%>
