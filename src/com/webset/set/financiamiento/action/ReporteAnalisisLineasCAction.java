package com.webset.set.financiamiento.action;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.webset.set.financiamiento.dto.AnalisisLineasCreditoDto;
import com.webset.set.financiamiento.dto.ReportePasivosFDto;
import com.webset.set.financiamiento.service.AvalesGarantiasFCService;
import com.webset.set.financiamiento.service.ReporteAnalisisLineasCService;
import com.webset.set.financiamiento.service.ReportePasivosFService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public class ReporteAnalisisLineasCAction {

	Bitacora bitacora = new Bitacora();
	Contexto contexto = new Contexto();
	Funciones funciones = new Funciones();
	ReporteAnalisisLineasCService reporteAnalisisLineasCService;
	private GlobalSingleton globalSingleton;


	@DirectMethod
	public List<LlenaComboGralDto> obtenerEmpresas(int piNoUsuario, boolean grupo,int noGrupo) {
		System.out.println("action");
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			//if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			reporteAnalisisLineasCService = (ReporteAnalisisLineasCService) contexto
					.obtenerBean("reporteAnalisisLineasCBusinessImpl");
			list = reporteAnalisisLineasCService.llenarCmbEmpresa(piNoUsuario,grupo,noGrupo);
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: Financiamiento, C: ReporteAnalisisLineasCAction, M: llenarCmbEmpresa");
		}
		return list;
	}
	@DirectMethod
	public List<LlenaComboGralDto> obtenerGruposEmpresa() {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			//if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			reporteAnalisisLineasCService = (ReporteAnalisisLineasCService) contexto
					.obtenerBean("reporteAnalisisLineasCBusinessImpl");
			list = reporteAnalisisLineasCService.obtenerGruposEmpresa();
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: Financiamiento, C: ReporteAnalisisLineasCAction, M: obtenerGruposEmpresa");
		}
		return list;
	}
	@DirectMethod
	public List<LlenaComboGralDto> obtenerTipoFinanciamiento(String vsTipoMenu) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			//if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			reporteAnalisisLineasCService = (ReporteAnalisisLineasCService) contexto
					.obtenerBean("reporteAnalisisLineasCBusinessImpl");
			list = reporteAnalisisLineasCService.obtenerTipoFinanciamiento(vsTipoMenu);
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: Financiamiento, C: ReporteAnalisisLineasCAction, M: obtenerTipoFinanciamiento");
		}
		return list;
	}
	@DirectMethod
	public List<AnalisisLineasCreditoDto> obtenerAnalisisLineas(int empresa, int tipoFinanciamiento, boolean vbTipoCambio,String vsMenu, String fechaInicio, String fechaFin) {
		List<AnalisisLineasCreditoDto> list = new ArrayList<AnalisisLineasCreditoDto>();
		try {
			//if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			reporteAnalisisLineasCService = (ReporteAnalisisLineasCService) contexto
					.obtenerBean("reporteAnalisisLineasCBusinessImpl");
			list = reporteAnalisisLineasCService.obtenerAnalisisLineas(empresa,tipoFinanciamiento,vbTipoCambio,vsMenu,fechaInicio,fechaFin);
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: Financiamiento, C: ReporteAnalisisLineasCAction, M: obtenerTipoFinanciamiento");
		}
		return list;
	}
	@DirectMethod
	public int obtenerValoresDivisa() {
		int valor=0;
		try {
			//if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			reporteAnalisisLineasCService = (ReporteAnalisisLineasCService) contexto
					.obtenerBean("reporteAnalisisLineasCBusinessImpl");
			valor = reporteAnalisisLineasCService.obtenerValoresDivisa();
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: Financiamiento, C: ReporteAnalisisLineasCAction, M: obtenerValoresDivisa");
		}
		return valor;
	}
	@DirectMethod
	public List<AnalisisLineasCreditoDto> obtenerResumen(int empresa, int tipoFinanciamiento,String vsMenu) {
		System.out.println("action");
		List<AnalisisLineasCreditoDto> list = new ArrayList<AnalisisLineasCreditoDto>();
		try {
			//if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			reporteAnalisisLineasCService = (ReporteAnalisisLineasCService) contexto
					.obtenerBean("reporteAnalisisLineasCBusinessImpl");
			list = reporteAnalisisLineasCService.obtenerResumen(empresa,tipoFinanciamiento,vsMenu);
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: Financiamiento, C: ReporteAnalisisLineasCAction, M: obtenerResumen");
		}
		return list;
	}
	@DirectMethod
	public List<Map<String, Object>> obtenerReporteAnalisisResumen(String analisis, ServletContext context) {
		List<Map<String, Object>> lista =new ArrayList<Map<String, Object>>();
		Gson gson = new Gson();
		DecimalFormat formato= new DecimalFormat("###,###.##");
		List<Map<String, String>> paramAnalisis= gson.fromJson(analisis,
				new TypeToken<ArrayList<Map<String, String>>>() {
		}.getType());
		if (contexto == null) {
			bitacora.insertarRegistro(
					"P:Financiamiento, C: ReporteAnalisisLineasCAction, M: obtenerReporteAnalisisResumen" + " contexto nulo");
			return null;
		}
		try {
			for (int i = 0; i < paramAnalisis.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("empresa",paramAnalisis.get(i).get("empresa"));
				map.put("descripcion",paramAnalisis.get(i).get("descripcion"));
				map.put("lineasAut",paramAnalisis.get(i).get("lineasAut"));
				map.put("dispuestas",paramAnalisis.get(i).get("dispuestas"));
				map.put("descripcion",paramAnalisis.get(i).get("descripcion"));
				map.put("disponibles",paramAnalisis.get(i).get("disponibles"));
				map.put("color",paramAnalisis.get(i).get("color"));
				lista.add(map);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C: ReporteAnalisisLineasCAction, M: obtenerReporteAnalisisResumen");
		}
		return lista;
	}
	@DirectMethod
	public List<Map<String, Object>> obtenerReporteAnalisis(String analisis, ServletContext context) {
		List<Map<String, Object>> lista =new ArrayList<Map<String, Object>>();
		Gson gson = new Gson();
		DecimalFormat formato= new DecimalFormat("###,###.##");
		List<Map<String, String>> paramAnalisis= gson.fromJson(analisis,
				new TypeToken<ArrayList<Map<String, String>>>() {
		}.getType());
		if (contexto == null) {
			bitacora.insertarRegistro(
					"P:Financiamiento, C: ReporteAnalisisLineasCAction, M: obtenerReporteAnalisis" + " contexto nulo");
			return null;
		}
		try {
			for (int i = 0; i < paramAnalisis.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("empresa",paramAnalisis.get(i).get("empresa"));
				map.put("nomEmpresa",paramAnalisis.get(i).get("nomEmpresa"));
				map.put("tipoCredito",paramAnalisis.get(i).get("tipoCredito"));
				map.put("descBanco",paramAnalisis.get(i).get("descBanco"));
				map.put("linea",paramAnalisis.get(i).get("linea"));
				map.put("pasivo",paramAnalisis.get(i).get("pasivo"));
				map.put("tasa",paramAnalisis.get(i).get("tasa"));
				map.put("fecVencimiento",paramAnalisis.get(i).get("fecVencimiento"));
				map.put("factoraje",paramAnalisis.get(i).get("factoraje"));
				map.put("anticipagos",paramAnalisis.get(i).get("anticipagos"));
				map.put("totalLinea",paramAnalisis.get(i).get("totalLinea"));
				map.put("totalLineaDisp",paramAnalisis.get(i).get("totalLineaDisp"));
				lista.add(map);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C: ReporteAnalisisLineasCAction, M: obtenerReporteAnalisis");
		}
		return lista;
	}
	@DirectMethod
	public HSSFWorkbook excelAnalisisLineasCredito(String analisis,
			ServletContext context) {
		HSSFWorkbook wb = null;
		try {
			reporteAnalisisLineasCService = (ReporteAnalisisLineasCService) contexto
					.obtenerBean("reporteAnalisisLineasCBusinessImpl", context);
			wb = reporteAnalisisLineasCService.excelAnalisisLineasCredito(analisis);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C: ReporteAnalisisLineasCAction, M: excelAnalisisLineasCredito");
		}
		return wb;
	}
	@DirectMethod
	public HSSFWorkbook excelAnalisisLineasCreditoResumen(String analisis,
			ServletContext context) {
		HSSFWorkbook wb = null;
		try {
			reporteAnalisisLineasCService = (ReporteAnalisisLineasCService) contexto
					.obtenerBean("reporteAnalisisLineasCBusinessImpl", context);
			wb = reporteAnalisisLineasCService.excelAnalisisLineasCreditoResumen(analisis);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C: ReporteAnalisisLineasCAction, M: excelAnalisisLineasCreditoResumen");
		}
		return wb;
	}
}