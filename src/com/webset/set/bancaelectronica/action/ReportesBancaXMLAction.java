package com.webset.set.bancaelectronica.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.bancaelectronica.business.ReportesBancaXMLBusiness;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.utils.tools.Utilerias;

/**
 * @author Jessica Arelly Cruz Cruz
 * @since 11/03/2011
 */
public class ReportesBancaXMLAction {
	Contexto contexto=new Contexto();
	Bitacora bitacora=new Bitacora();
	ReportesBancaXMLBusiness reportesBancaXmlBusiness = new ReportesBancaXMLBusiness();
	private static Logger logger = Logger.getLogger(ReportesBancaXMLAction.class);
	
	@SuppressWarnings("null")
	@DirectMethod
	public List<Map<String,Object>> ejecutarReporteConcepto(String datos){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		Gson gson = new Gson();
		List<Map<String, String>> objParams = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		Map<String, Object> params = null;
		List<Map<String,Object>> resultado = null;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			reportesBancaXmlBusiness = (ReportesBancaXMLBusiness) contexto.obtenerBean("reportesBancaXmlBusiness");
			logger.info(objParams.get(0).get("empresaInf"));
			logger.info(objParams.get(0).get("empresaSup"));
			logger.info(objParams.get(0).get("lBancoInf"));
			logger.info(objParams.get(0).get("lBancoSup"));
			logger.info(objParams.get(0).get("lValor"));
			logger.info(objParams.get(0).get("dateInf"));
			logger.info(objParams.get(0).get("dateSup"));
			logger.info(objParams.get(0).get("optDetalle"));
			
			params.put("piEmpresaInf", objParams.get(0).get("empresaInf"));
			params.put("piEmpresaSup", objParams.get(0).get("empresaSup"));
			params.put("piBancoInf", objParams.get(0).get("lBancoInf"));
			params.put("piBancoSup", objParams.get(0).get("lBancoSup"));
			params.put("psChequera", objParams.get(0).get("lValor"));
			params.put("pdFechaInf", objParams.get(0).get("dateInf"));
			params.put("pdFechaSup", objParams.get(0).get("dateSup"));
			params.put("pbDetalle", objParams.get(0).get("optDetalle"));
			resultado = reportesBancaXmlBusiness.ejecutarReporteConcepto(params);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:ReportesBancaXMLAction, M:ejecutarReporteConcepto");
		}
		return resultado;
	}
	
	@SuppressWarnings("null")
	@DirectMethod
	public List<Map<String, Object>> ejecutarReporteBE(String datos){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		Gson gson = new Gson();
		List<Map<String, String>> objParams = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		Map<String, Object> params = null;
		List<Map<String, Object>> resultado = null;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			reportesBancaXmlBusiness = (ReportesBancaXMLBusiness) contexto.obtenerBean("reportesBancaXmlBusiness");
			logger.info(objParams.get(0).get("empresaInf"));
			logger.info(objParams.get(0).get("empresaSup"));
			logger.info(objParams.get(0).get("lBancoInf"));
			logger.info(objParams.get(0).get("lBancoSup"));
			logger.info(objParams.get(0).get("lValor"));
			logger.info(objParams.get(0).get("dateInf"));
			logger.info(objParams.get(0).get("dateSup"));
			logger.info(objParams.get(0).get("optMovimiento"));
			logger.info(objParams.get(0).get("tipoMov"));
			logger.info(objParams.get(0).get("origenMov"));
			logger.info(objParams.get(0).get("concepto"));
			logger.info(objParams.get(0).get("opcion1"));
			logger.info(objParams.get(0).get("movtoTEF"));
			logger.info(objParams.get(0).get("movtoDia"));
			logger.info(objParams.get(0).get("movtoAutomata"));
			logger.info(objParams.get(0).get("exportaExcel"));
			
			params.put("piEmpresaInf", objParams.get(0).get("empresaInf"));
			params.put("piEmpresaSup", objParams.get(0).get("empresaSup"));
			params.put("piBancoInf", objParams.get(0).get("lBancoInf"));
			params.put("piBancoSup", objParams.get(0).get("lBancoSup"));
			params.put("psChequera", objParams.get(0).get("lValor"));
			params.put("pdFechaInf", objParams.get(0).get("dateInf"));
			params.put("pdFechaSup", objParams.get(0).get("dateSup"));
			params.put("pbMov", objParams.get(0).get("optMovimiento"));
			params.put("psTipoMov", objParams.get(0).get("tipoMov"));
			params.put("pvOrigenMov", objParams.get(0).get("origenMov"));
			params.put("psConcepto", objParams.get(0).get("concepto"));
			params.put("pbOpcion1", objParams.get(0).get("opcion1"));
			params.put("psMovtosTEF", objParams.get(0).get("movtoTEF"));
			params.put("psMovtosDia", objParams.get(0).get("movtoDia"));
			params.put("psMovtoAutomata", objParams.get(0).get("movtoAutomata"));
			params.put("exportaExcel", objParams.get(0).get("exportaExcel"));
			resultado = reportesBancaXmlBusiness.ejecutarReporteBE(params);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:ReportesBancaXMLAction, M:ejecutarReporteBE");
		}
		return resultado;
	}

}
