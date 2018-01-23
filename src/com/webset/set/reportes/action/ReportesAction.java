package com.webset.set.reportes.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContext;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.reportes.business.ReportesBusiness;
import com.webset.set.reportes.service.ReportesService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;

import net.sf.jasperreports.engine.JRDataSource;

/**
 * @author Armando Rodriguez Meneses
 * @since 11/02/2011
 */
public class ReportesAction {
	private Contexto contexto = new Contexto(); //objeto de la clase Contexto para hacer la conexion con los xml 
	private Bitacora bitacora = new Bitacora();	//objeto para insertar en la bitacora
	private ReportesBusiness reportesBusiness = new ReportesBusiness();
	private ReportesService reportesService;
	
	//private ReportesBusiness reportesBusiness = new ReportesBusiness();
	/**
	 * metodo que obtiene el xml para los reportes de la pantalla Movimientos de Banca Electronica
	 * @param id
	 * @return
	 */
	
	public JRDataSource obtenerDatosReporte(String nomReporte, Map<String, Object> parameters, ServletContext context){
		reportesBusiness = (ReportesBusiness)contexto.obtenerBean("reportesBusiness", context);
		JRDataSource jrDataSource = null;
		
		try{
			jrDataSource = reportesBusiness.obtenerDatosReporte(nomReporte, parameters);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:SET, C:ReportesAction, M:obtenerXMLReporte");
		}
		return jrDataSource;
	}
	
	
	/**Comienza codigo exclusivo para modulo Reportes**/
	
	@DirectMethod
	public List<LlenaComboEmpresasDto>llenarComboEmpresa(int usuario){
		List<LlenaComboEmpresasDto> listEmp = new ArrayList<LlenaComboEmpresasDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listEmp;
		try{
			reportesService = (ReportesService)contexto.obtenerBean("reportesBusinessImpl");
			listEmp = reportesService.consultarEmpresas(usuario);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Reportes, C:ReportesAction, M:llenarComboEmpresa");
		}
		return listEmp;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboCajas(int usuario){
		List<LlenaComboGralDto> listCaja = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listCaja;
		try{
			reportesService = (ReportesService)contexto.obtenerBean("reportesBusinessImpl");
			listCaja = reportesService.consultarCajas(usuario);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Reportes, C:ReportesAction, M:llenarComboCajas");
		}
		return listCaja;
	}
	
	@DirectMethod
	public List<LlenaComboChequeraDto> llenarComboOrigen(String tipoMovto){
		List<LlenaComboChequeraDto> listOrigen = new ArrayList<LlenaComboChequeraDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listOrigen;
		try{
			reportesService = (ReportesService)contexto.obtenerBean("reportesBusinessImpl");
			listOrigen = reportesService.consultarOrigen(tipoMovto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Reportes, C:ReportesAction, M:llenarComboOrigen");
		}
		return listOrigen;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboBancos(int iEmpresa, String sDivisa){
		List<LlenaComboGralDto> listBanco = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listBanco;
		try{
			reportesService = (ReportesService)contexto.obtenerBean("reportesBusinessImpl");
			listBanco = reportesService.consultarBancos(iEmpresa, sDivisa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Reportes, C:ReportesAction, M:llenarComboBancos");
		}
		return listBanco;
	}
	
	@DirectMethod
	public List<LlenaComboChequeraDto> llenarComboChequeras(int iBanco, int iEmpresa){
		List<LlenaComboChequeraDto> listChe = new ArrayList<LlenaComboChequeraDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listChe;
		try{
			reportesService = (ReportesService)contexto.obtenerBean("reportesBusinessImpl");
			listChe = reportesService.consultarChequeras(iBanco, iEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Reportes, C:ReportesAction, M:llenarComboChequeras");
		}
		return listChe;
	}
	
	@DirectMethod
	public JRDataSource obtenerDatosReporteCheque(String nomReporte, Map<String, Object> parameters, ServletContext context){
		System.out.println("IMPRIMIR 1 obtenerDatosReporteCheque"+ nomReporte);
		JRDataSource jrDataSource = null;
		try{
			reportesService = (ReportesService)contexto.obtenerBean("reportesBusinessImpl", context);
			jrDataSource = reportesService.obtenerDatosReporteCheques(nomReporte, parameters);
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Reportes, C:ReportesAction, M:obtenerDatosReporteCheque");
		}
		return jrDataSource;
	}
	@DirectMethod
	public boolean obtenerDatosReporteChequeVerifica(String nomReporte,String subtitulo,String fechaIni,String agrupado,
			String divisa,String estatusMov,
			String origen,int bancoInf,String fechaFin,String estatusCb, String ordenado,String cheF,
			String chequera,String nomEmpresa,int empresas,
			int bancoSup,int user,String descEstatus,String cheI,int cajas,String estatusEntregado){
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("subtitulo", subtitulo);
		parameters.put("fechaIni", fechaIni);
		parameters.put("agrupado",agrupado );
		parameters.put("divisa", divisa);
		parameters.put("estatusMov", estatusMov);
		parameters.put("origen", origen);
		parameters.put("bancoInf", bancoInf);
		parameters.put("fechaFin",fechaFin);
		parameters.put("estatusCb", estatusCb);
		parameters.put("ordenado", ordenado);
		parameters.put("cheFin", cheF);
		parameters.put("chequera", chequera);
		parameters.put("nomEmpresa", nomEmpresa);
		parameters.put("empresas", empresas);
		parameters.put("bancoSup", bancoSup);
		parameters.put("usuario", user);
		parameters.put("descEstatus", descEstatus);
		parameters.put("cheIni", cheI);
		parameters.put("cajas", cajas);
		parameters.put("estatusEntregado", estatusEntregado);
		System.out.println("IMPRIMIR 1 obtenerDatosReporteCheque"+ nomReporte);
		boolean b=false;
		JRDataSource jrDataSource = null;
		try{
			reportesService = (ReportesService)contexto.obtenerBean("reportesBusinessImpl");
			jrDataSource = reportesService.obtenerDatosReporteCheques(nomReporte, parameters);
			if(jrDataSource==null){
				b=false;
			}else{
				b=jrDataSource.next();
			}
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Reportes, C:ReportesAction, M:obtenerDatosReporteCheque");
		}
		return b;
	}
	
	@DirectMethod
	public int buscarDatosReporte(String params) {
		int res = 0;
		Gson gson = new Gson();
		List<Map<String, String>> objParams = gson.fromJson(params, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		if(!Utilerias.haveSession(WebContextManager.get()))
			return res;
		try {
			reportesService = (ReportesService)contexto.obtenerBean("reportesBusinessImpl");
			res = reportesService.buscarDatosReportes(objParams);
			System.out.println("termino buscar:  " + res);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Reportes, C:ReportesAction, M:buscarDatosReporte");
		}
		return res;
	}
	
	@DirectMethod
	public JRDataSource reporteTransConfirmadas(Map<String, Object> parameters, ServletContext context) {
		JRDataSource jrDataSource = null;
		try {
			reportesService = (ReportesService)contexto.obtenerBean("reportesBusinessImpl", context);
			jrDataSource = reportesService.reporteTransConfirmadas(parameters);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Reportes, C:ReportesAction, M:reporteTransConfirmadas");
		}
		return jrDataSource;
	}
	@DirectMethod
	public boolean reporteTransConfirmadasVerificar(String idDivisa,String fechIni, boolean chkSmart, int estatusReporte,String tipoReporte,int user, String noBanco, String fechFin, String noEmpresa) {
		WebContext request=null;
		//ServletContext context = request.getSession().getServletContext();
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("idDivisa",idDivisa);
		resultado.put("FECHA_INI",fechIni);
		resultado.put("FECHA_FIN",fechFin);
		resultado.put("usuario",user);
		resultado.put("noEmpresa",noEmpresa);
		resultado.put("noBanco",noBanco);
		resultado.put("chkSmart",chkSmart);
		resultado.put("estatusReporte",estatusReporte);
		resultado.put("tipoReporte",tipoReporte);
		JRDataSource jrDataSource = null;
		boolean b=false;
		
		try {
			reportesService = (ReportesService)contexto.obtenerBean("reportesBusinessImpl");
			jrDataSource = reportesService.reporteTransConfirmadas(resultado);
			b=jrDataSource.next();
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Reportes, C:ReportesAction, M:reporteTransConfirmadas");
		}
		
		return b;
	}
	@DirectMethod
	public boolean reporteTransConfirmadasVerificarTranspasos(String idDivisa,String fechIni, int tipoTrasp, int estatusReporte,String tipoReporte,int user, String noBanco, String fechFin, String noEmpresa,String nomRep,String nomEmpresa,String estRep) {
		WebContext request=null;
		//ServletContext context = request.getSession().getServletContext();
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("idDivisa",idDivisa);
		resultado.put("FECHA_INI",fechIni);
		resultado.put("NOM_REP",nomRep);
		resultado.put("estatusReporte",estatusReporte);
		resultado.put("tipoReporte",tipoReporte);
		resultado.put("NOM_EMPRESA",nomEmpresa);
		resultado.put("EST_REP",estRep);
		resultado.put("usuario",user);
		resultado.put("tipoTrasp",tipoTrasp);
		resultado.put("noBanco",noBanco);
		resultado.put("FECHA_FIN",fechFin);
		resultado.put("noEmpresa",noEmpresa);
		JRDataSource jrDataSource = null;
		boolean b=false;
		
		try {
			reportesService = (ReportesService)contexto.obtenerBean("reportesBusinessImpl");
			jrDataSource = reportesService.reporteTransConfirmadas(resultado);
			b=jrDataSource.next();
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Reportes, C:ReportesAction, M:reporteTransConfirmadas");
		}
		
		return b;
	}
	
	@DirectMethod
	public String exportaExcel(String datos) {
		String resp = "Error";
		if(!Utilerias.haveSession(WebContextManager.get()))
			return resp;
		try {
			reportesService = (ReportesService)contexto.obtenerBean("reportesBusinessImpl");
			resp = reportesService.exportaExcel(datos);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Reportes, C:ReportesAction, M:reporteTransConfirmadas");
		}
		return resp;
	}
	
	@DirectMethod
	public String exportaExcelTransfer(String datos) {
		String resp = "";
		if(!Utilerias.haveSession(WebContextManager.get()))
			return resp;
		try {
			reportesService = (ReportesService)contexto.obtenerBean("reportesBusinessImpl");
			resp = reportesService.exportaExcelTransfer(datos);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Reportes, C:ReportesAction, M:exportaExcelTransfer");
		}
		return resp;
	}
	
}
