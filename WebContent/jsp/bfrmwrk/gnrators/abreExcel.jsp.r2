<%@page import="com.webset.set.inversiones.action.InversionesRepAction"%>
<%@page import="com.webset.set.consultas.action.PosicionBancariaAction"%>
<%@page import="com.webset.set.impresion.action.CartasPorEntregarAction"%>
<%@page import="com.webset.set.barridosfondeos.action.BarridosFondeosRepAction"%>
<%@page import="com.webset.set.egresos.action.RentasAction"%>
<%@page import="com.webset.set.egresos.action.AplicacionDescuentosPropuestasAction"%>
<%@page import="com.webset.set.utilerias.action.MantenimientoBancosAction"%>
<%@page import="com.webset.set.seguridad.action.SegEmpresaAction"%>
<%@page import="com.webset.set.seguridad.action.SeguridadUsuarioAction"%>
<%@page import="com.webset.set.egresos.action.PagoPropuestasAction"%>
<%@page import="com.webset.set.utilerias.action.MantenimientoCatalogosAction"%>
<%@page import="com.webset.set.utilerias.action.CodigosAction"%>
<%@page import="com.webset.set.utilerias.action.GrupoEmpresasAction"%>
<%@page import="com.webset.set.utilerias.action.ConfiguracionSolicitudPagoAction"%>
<%@page import="com.webset.set.utilerias.action.MantenimientoTiendasAction"%>
<%@page import="com.webset.set.consultas.action.ConsultasAction"%>
<%@page import="com.webset.set.utilerias.action.MantenimientoSolicitantesAction"%>
<%@page import="com.webset.set.impresion.action.MantenimientosAction"%>
<%@page import="com.webset.set.derivados.action.ForwardsAction"%>
<%@page import="com.webset.set.impresion.action.ChequesTransitoAction"%>
<%@page import="com.webset.set.impresion.action.CartasChequesAction"%>
<%@page import="com.webset.set.impresion.action.ChequesPorEntregarAction"%>
<%@page import="com.webset.set.utilerias.action.MantenimientoSolicitantesFirmantesAction"%>
<%@page import="com.webset.set.impresion.action.ControlChequesAction"%>
<%@page import="com.webset.set.utilerias.action.MapeoAction"%>

<%@page import="com.webset.set.utilerias.action.EquivalenciaBancosAction"%>
<%@page import="com.webset.set.bancaelectronica.action.MovimientosBEAction"%>
<%@page import="com.webset.set.bancaelectronica.action.MovimientosBancaEAction"%>
<%@page import="com.webset.set.interfaz.action.CargaPagosAction"%>
<%@page import="com.webset.set.utilerias.action.MantenimientoLeyendasAction"%>
<%@page import="com.webset.set.utilerias.action.EquivalenciaEmpresasAction"%>
<%@page import="com.webset.set.utilerias.action.MantenimientoChequerasAction"%>
<%@page import="com.webset.set.personas.action.ConsultaPersonasAction"%>
<%@page import="com.webset.set.egresos.action.MantenimientoBitacoraRentasAction"%>
<%@page import="com.webset.set.utilerias.action.ReglasNegocioAction"%>
<%@page import="com.softwarementors.extjs.djn.servlet.ssm.WebContextManager"%>
<%@page import="com.softwarementors.extjs.djn.servlet.ssm.WebContext"%>
<%@page import="com.webset.set.utilerias.Contexto"%>
<%@page import="com.webset.set.egresos.business.ConsultaPropuestasBusinessImpl"%>
<%@page import="com.webset.set.derivados.action.ForwardsAction"%>
<%@ page session="true" %>

<%@ page import  = "java.util.*,
                 com.webset.set.coinversion.action.CoinversionAction,
                 com.webset.set.egresos.action.ConsultaPropuestasAction,
                 com.webset.set.ingresos.action.IdentificacionIngresosAction,
				 org.apache.poi.hssf.usermodel.HSSFWorkbook"%>

<%	

	ServletContext context = request.getSession().getServletContext();
	String sNombreRep = request.getParameter("nomReporte")==null?"":request.getParameter("nomReporte").toString();

    if (sNombreRep.equals("")) {
      return;
    }

    String reportFile = sNombreRep+".xls";
    
    HSSFWorkbook wb = null;
    
    Map<String, String> params = new HashMap<String, String>();
    try {
    	System.out.println(sNombreRep.equals("ResumenPropDet"));
    }catch(Exception e){
        e.printStackTrace();
    }
    try {
    	int i = 1;
    	String sNomVarParm = "nomParam" + i;
        String sNomValParm = "valParam" + i;
        
        for (; request.getParameter(sNomVarParm) != null 
        		&& !request.getParameter(sNomVarParm).toString().equals(""); 
        		i++, sNomVarParm = "nomParam" + i, sNomValParm = "valParam" + i)
        	params.put(request.getParameter(sNomVarParm).toString(), 
        			request.getParameter(sNomValParm).toString());
     
        
        
    	
        if (sNombreRep.equals("prueba")) {
			CoinversionAction coinversionAction = new CoinversionAction();
			wb = coinversionAction.obtenerExcel(params, reportFile, context);
        } else if (sNombreRep.equals("ResumenPropuestas")) { 
				ConsultaPropuestasAction consultaPropuestasAction = new ConsultaPropuestasAction();
				wb = consultaPropuestasAction.obtenerExcel(params.get("nomArchivo"));
		} else if (sNombreRep.equals("ResumenPropDet")) { 
			ConsultaPropuestasAction consultaPropuestasAction = new ConsultaPropuestasAction();
			wb = consultaPropuestasAction.obtenerExcel(params.get("nomArchivo"));
		} else if(sNombreRep.equals("ResumenPropuestasDetalles")){
			ConsultaPropuestasAction consultaPropuestasAction = new ConsultaPropuestasAction();
			wb = consultaPropuestasAction.obtenerExcel(params.get("nomArchivo"));
		} else if(sNombreRep.equals("bloqueados")) {
			//ConsultaPropuestasDaoImpl consultaPropuestasDao = (ConsultaPropuestasDaoImpl) contexto.obtenerBean("consultaPropuestasDaoImpl");
			ConsultaPropuestasAction consultaPropuestasAction = new ConsultaPropuestasAction();
			wb = consultaPropuestasAction.datosExcelBloqueados("Bloqueados", params, context );
		} else if(sNombreRep.equals("ReporteReglasNegocio")) {
			ReglasNegocioAction reglasNegocioAction = new ReglasNegocioAction();
			wb = reglasNegocioAction.obtenerExcel(params.get("nomArchivo"));
		} else if (sNombreRep.equals("mantenimientoBitacoraRentas")) {
			MantenimientoBitacoraRentasAction mantenimientoBitacoraRentasAction = new MantenimientoBitacoraRentasAction();
			wb = mantenimientoBitacoraRentasAction.obtenerExcel(params.get("nomArchivo"));
		} else if(sNombreRep.equals("excelPersonas")){
			ConsultaPersonasAction consultaPersonasAction = new ConsultaPersonasAction();
			wb=consultaPersonasAction.reportePersonas(params.get("tipoPersona").toString(), context);
		}else if(sNombreRep.equals("excelFws")){ 
				ForwardsAction consulta =new ForwardsAction();
				//ConsultaPersonasAction consultaPersonasAction = new ConsultaPersonasAction();
			     
				wb=consulta.reportePersonas2(params.get("tipoPersona").toString(),
						params.get("tipoPersona2").toString(),
						params.get("tipoPersona3").toString(),
						params.get("tipoPersona4").toString(),
						params.get("tipoPersona5").toString(),
						context);
				
				if (wb == null){
					System.out.println("es nulo el resultado!!!!");
				}
				
				

		} else if(sNombreRep.equals("consultaSolicitudPagoSinPoliza")) {
			MantenimientoBitacoraRentasAction mantenimientoBitacoraRentasAction = new MantenimientoBitacoraRentasAction();
			wb = mantenimientoBitacoraRentasAction.obtenerExcel(params.get("nomArchivo"));
		} else if (sNombreRep.equals("ConsultaDeMovimientos")) {
			ConsultaPropuestasAction consultaPropuestasAction = new ConsultaPropuestasAction();
			wb = consultaPropuestasAction.obtenerExcel(params.get("nomArchivo"));
		} else if (sNombreRep.equals("excelChequeras")) {
			MantenimientoChequerasAction mantenimientoChequerasAction = new MantenimientoChequerasAction();
			wb = mantenimientoChequerasAction.reporteChequeras(
					params.get("tipoChequera"), 
					params.get("empresa"), 
					context);
		} else if (sNombreRep.equals("equivalePersonas")) {
			EquivalenciaEmpresasAction equivalenciaEmpresasAction = new EquivalenciaEmpresasAction();
			wb = equivalenciaEmpresasAction.obtenerExcel(params.get("nomArchivo"));
		} else if (sNombreRep.equals("excelLeyendas")) {
			MantenimientoLeyendasAction mantenimientoLeyendasAction = new MantenimientoLeyendasAction();
			wb = mantenimientoLeyendasAction.obtenerExcel(params.get("nomArchivo"));
		} else if (sNombreRep.equals("interfaces")) {
			CargaPagosAction cargaPagosAction = new CargaPagosAction();
			wb = cargaPagosAction.reporteInterfaces(
					params.get("tipoValor"), 
					params.get("fecHoy"),
					params.get("usuarioAlta"), 
					params.get("idEmpresa"), 
					params.get("estatus"), 
					params.get("fecIni"),
					params.get("fecFin"),
					context);
		} else if (sNombreRep.equals("ConsultaMovsBancaE")) {
			MovimientosBEAction movimientosBEAction = new MovimientosBEAction();
			wb = movimientosBEAction.obtenerExcel(params.get("nomArchivo"));
		} else if(sNombreRep.equals("excelMapeo")){
			MapeoAction mapeoAction = new MapeoAction();
			wb=mapeoAction.reporteMapeo(context);
		} else if (sNombreRep.equals("controlCheques")) {
			ControlChequesAction controlCheques = new ControlChequesAction();
			wb = controlCheques.obtenerExcel(params.get("nomArchivo"));
		} else if (sNombreRep.equals("chequesPorEntregar")){
			ChequesPorEntregarAction chequesPorEntregar = new ChequesPorEntregarAction();
			wb = chequesPorEntregar.obtenerExcel(params.get("nomArchivo"));
		} else if (sNombreRep.equals("chequesTransito")) {
			ChequesTransitoAction chequesTransitoAction = new ChequesTransitoAction();
			wb = chequesTransitoAction.obtenerExcel(params.get("nomArchivo"));
		} else if (sNombreRep.equals("mantenimientoFirmantes") || sNombreRep.equals("mantenimientoFirmas")) {
			MantenimientosAction mantenimientosAction = new MantenimientosAction();
			wb = mantenimientosAction.obtenerExcel(params.get("nomArchivo"));
		} else if (sNombreRep.equals("forwardsImportados")) {
			System.out.println("Obtener forwards excel");
			ForwardsAction forwardsAction = new ForwardsAction();
			wb = forwardsAction.obtenerExcel(params.get("nomArchivo"));
		} else if (sNombreRep.equals("ExcelDetalles")) { 
			ConsultaPropuestasAction consultaPropuestasAction = new ConsultaPropuestasAction();
			wb = consultaPropuestasAction.obtenerExcelDetalles(params.get("idUsu").toString(), context);
		} else if (sNombreRep.equals("solicitantesFirmantes")) {
			MantenimientoSolicitantesFirmantesAction mSolFir = new MantenimientoSolicitantesFirmantesAction();
			wb = mSolFir.obtenerExcel(params.get("nomArchivo"));
		} else if (sNombreRep.equals("solicitantes")) {
			MantenimientoSolicitantesAction mSolFir = new MantenimientoSolicitantesAction();
			wb = mSolFir.obtenerExcel(params.get("nomArchivo"));
		} else if(sNombreRep.equals("CMreporteFondeo")){ 
			ConsultasAction consultasAction = new ConsultasAction();
			wb=consultasAction.reporteFondeo(params.get("fInicio").toString(),params.get("fFin").toString(), params.get("bandera").toString(), params.get("usuario").toString() , context);			
		} else if(sNombreRep.equals("excelImportaciones")){
			ConsultasAction consultasAction = new ConsultasAction();
			wb = consultasAction.reporteCXP(params.get("noEmpresa").toString(), params.get("fecIni").toString(), params.get("fecFin").toString(), params.get("estatus").toString(),params.get("tipoReporte").toString(), context);
		} else if (sNombreRep.equals("excelTiendas")) {
			MantenimientoTiendasAction tiendas = new MantenimientoTiendasAction();
			wb = tiendas.obtenerExcel(params.get("nomArchivo"));
		}else if (sNombreRep.equals("parametrosSolicitudPago")) {
			ConfiguracionSolicitudPagoAction confSol = new ConfiguracionSolicitudPagoAction();
			wb = confSol.obtenerExcel(params.get("nomArchivo"));
		} else if(sNombreRep.equals("excelGrupoEmpresas")){
			GrupoEmpresasAction grupoEmpresasAction = new GrupoEmpresasAction();
			wb=grupoEmpresasAction.reporteGrupoEmpresas(context);
		} else if(sNombreRep.equals("excelGrupos")){
			CodigosAction codigosAction = new CodigosAction();
			wb=codigosAction.reporteGrupos(context);
		} else if(sNombreRep.equals("excelGrupoPolizas")){
			CodigosAction codigosAction = new CodigosAction();
			wb=codigosAction.reporteGrupoPolizas(context);
		} else if(sNombreRep.equals("excelGrupoRubros")){
			CodigosAction codigosAction = new CodigosAction();
			wb=codigosAction.reporteGrupoRubros(context);
		} else if(sNombreRep.equals("excelCatalogo")){
			MantenimientoCatalogosAction mantenimientoCatalogosAction = new MantenimientoCatalogosAction();
			wb=mantenimientoCatalogosAction.reporteCatalogo(params.get("catalogo"), context);
		} else if (sNombreRep.equals("excelPagoPropuestas")) {
			PagoPropuestasAction pagoPropuestasAction = new PagoPropuestasAction();
			wb = pagoPropuestasAction.consultaPagosExcel(
					params.get("clave"), 
					context);
		}else if (sNombreRep.equals("excelMensajePagoPropuesta")){
			ConsultaPropuestasAction consultaPropuestasAction = new ConsultaPropuestasAction();
			wb = consultaPropuestasAction.obtenerExcel(params.get("nombre"));
		}else if (sNombreRep.equals("excelConciliaciones")){
			IdentificacionIngresosAction identificacionIngresos = new IdentificacionIngresosAction();
			wb = identificacionIngresos.obtenerExcel(params.get("nombre"));
		} else if (sNombreRep.equals("excelUsuariosS")){
			SeguridadUsuarioAction seguridadUsuarioAction = new SeguridadUsuarioAction();
			wb = seguridadUsuarioAction.obtenerExcel(params.get("nomArchivo"));
		} else if (sNombreRep.equals("usuarioEmpresa")){
			SegEmpresaAction segEmpresaAction = new SegEmpresaAction();
			wb = segEmpresaAction.obtenerExcel(params.get("nomArchivo"));
		} else if(sNombreRep.equals("excelBancos")){
			MantenimientoBancosAction mantenimientoBancosAction = new MantenimientoBancosAction();
			wb=mantenimientoBancosAction.reporteBancos(params.get("tipoBanco").toString(), context);			
		} else if(sNombreRep.equals("excelBancosExt")){
			EquivalenciaBancosAction equivalenciaBancosAction = new EquivalenciaBancosAction();
			wb=equivalenciaBancosAction.reporteBancosExt(context);
		}else if (sNombreRep.equals("excelRentas")) {
			RentasAction rentasAction = new RentasAction();
			wb = rentasAction.obtenerExcel(params.get("nomArchivo"));
		}else if(sNombreRep.equals("excelHeader")){
			AplicacionDescuentosPropuestasAction descuentosAction = new AplicacionDescuentosPropuestasAction();
			wb = descuentosAction.consultaHeader(params.get("claveP"), params.get("claveD"), context);
		} else if (sNombreRep.equals("excelDetalle")) {
			AplicacionDescuentosPropuestasAction descuentosAction = new AplicacionDescuentosPropuestasAction();
			wb = descuentosAction.consultaPropuestasDescuentos(params.get("claveP"),params.get("claveD"),context);
		}else if(sNombreRep.equals("excelCartaCheques")){
			CartasChequesAction cartasChequesAction = new CartasChequesAction();
			wb=cartasChequesAction.obtenerExcel(params.get("nomArchivo"));
		} else if (sNombreRep.equals("excelBarridosFondeos")) {
			BarridosFondeosRepAction barridosFondeosRepAction = new BarridosFondeosRepAction();
			wb = barridosFondeosRepAction.obtenerExcelBarridosFondeos(Integer.parseInt(params.get("noEmpresa")), Integer.parseInt(params.get("idUsuario")), params.get("fecha"), context);
			if (wb == null){
				System.out.println("es nulo el resultado!!!!");
			}
		} else if (sNombreRep.equals("excelCuadreFondeo")) {
			BarridosFondeosRepAction barridosFondeosRepAction = new BarridosFondeosRepAction();
			wb = barridosFondeosRepAction.obtenerExcelCuadreFeondeo(Integer.parseInt(params.get("noEmpresa")), Integer.parseInt(params.get("idUsuario")), params.get("fecha"), context);
			if (wb == null){
				System.out.println("es nulo el resultado!!!!");
			}
		} else if(sNombreRep.equals("excelCartasEmitidas")){
			CartasPorEntregarAction cartasPorEntregarAction = new CartasPorEntregarAction();
			wb=cartasPorEntregarAction.reporteCartasEmitidas(
					params.get("folio"), 
					params.get("idBanco"), 
					params.get("tipo"), 
					params.get("estatus"), 
					params.get("fechaIni"),
					params.get("fechaFin"),
					context);	
		}else if (sNombreRep.equals("MovimientoPosicionBancaria") 
				|| sNombreRep.equals("MovimientoPosicionBancaria2") 
				|| sNombreRep.equals("excelMovtosTesoreria")
				|| sNombreRep.equals("excelDiarioTesoreria")
				|| sNombreRep.equals("excelFecValorXEmp") 
				|| sNombreRep.equals("excelFecValorXCta")
				|| sNombreRep.equals("excelReporteSaldos")) { 
			PosicionBancariaAction posicionBancariaAction = new PosicionBancariaAction();
			wb = posicionBancariaAction.obtenerExcel(params.get("nomArchivo"));
		} else if (sNombreRep.equals("ExcelInversiones")) {
			InversionesRepAction inversionesRepAction = new InversionesRepAction();
			wb = inversionesRepAction.obtenerExcelInvEstablecidas(Integer.parseInt(params.get("noEmpresa")), 
					params.get("idDivisa"), 
					params.get("fecha"), 
					params.get("fechaFin"), 
					Integer.parseInt(params.get("idUsuario")), context);
			if (wb == null){
				System.out.println("es nulo el resultado!!!!");
			}
		} else if (sNombreRep.equals("ExcelVencimiento")) {
			InversionesRepAction inversionesRepAction = new InversionesRepAction();
			wb = inversionesRepAction.obtenerExcelVencimientos(Integer.parseInt(params.get("noEmpresa")), 
					params.get("idDivisa"), 
					params.get("fecha"), 
					params.get("fechaFin"), 
					Integer.parseInt(params.get("idUsuario")), context);
			if (wb == null){
				System.out.println("es nulo el resultado!!!!");
			}
		} else if (sNombreRep.equals("ExcelPosicionInv")) {
			InversionesRepAction inversionesRepAction = new InversionesRepAction();
			wb = inversionesRepAction.obtenerExcelPosicionInv(Integer.parseInt(params.get("noEmpresa")), 
					params.get("idDivisa"), 
					params.get("fecha"), 
					params.get("fechaFin"), 
					Integer.parseInt(params.get("idUsuario")), context);
			if (wb == null){
				System.out.println("es nulo el resultado!!!!");
			}
		} else if (sNombreRep.equals("ExcelSaldosInv")) {
			InversionesRepAction inversionesRepAction = new InversionesRepAction();
			wb = inversionesRepAction.obtenerExcelSaldosInv(Integer.parseInt(params.get("noEmpresa")), 
					params.get("idDivisa"), 
					params.get("fecha"), 
					params.get("fechaFin"), 
					Integer.parseInt(params.get("idUsuario")), context);
			if (wb == null){
				System.out.println("es nulo el resultado!!!!");
			}
		} else if(sNombreRep.equals("ExcelSaldosDeChequerasS")) {
			ConsultasAction consultasAction = new ConsultasAction();
			wb=consultasAction.obtenerDatosExcelReporteSaldosS(params.get("idEmpresa"),
					params.get("nomEmpresa"),
					params.get("idBancoInf"),
					params.get("idBancoSup"),
					params.get("idChequera"),
					params.get("tipoChequera"),
					params.get("estatusTipoEmpresa"),
					params.get("usuario"),
					params.get("empresas"), context);
			if (wb == null){
				System.out.println("es nulo el resultado!!!!");
			}
			
			
  		}
        
        response.setContentType("application/vnd.ms-excel; charset=ISO-8859-1");
		response.setHeader("Content-Disposition", "attachment; filename=" + reportFile);
		
		ServletOutputStream ouputStream = response.getOutputStream();
		wb.write(ouputStream);
		ouputStream.flush();
		ouputStream.close();
    } catch (Exception e) {
    	e.printStackTrace();
    	System.out.println("Error al generar el Excel: "+e.getMessage());
    }
%>
