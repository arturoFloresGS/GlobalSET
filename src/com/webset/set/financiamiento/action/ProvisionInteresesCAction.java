package com.webset.set.financiamiento.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.webset.set.financiamiento.dto.ProvisionCreditoDTO;
import com.webset.set.financiamiento.service.ProvisionInteresesCService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public class ProvisionInteresesCAction {

	Bitacora bitacora = new Bitacora();
	Contexto contexto = new Contexto();
	Funciones funciones = new Funciones();
	ProvisionInteresesCService provisionInteresesCService;
	private GlobalSingleton globalSingleton;

	@DirectMethod
	public List<LlenaComboGralDto> llenarCmbEmpresa(int piNoUsuario,boolean pbMismaEmpresa, int plEmpresa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			//if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			provisionInteresesCService = (ProvisionInteresesCService) contexto
					.obtenerBean("provisionInteresesCBusinessImpl");
			list = provisionInteresesCService.llenarCmbEmpresa(piNoUsuario,pbMismaEmpresa,plEmpresa);
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:ProvisionInteresesCAction, M:llenarCmbEmpresa");
		}
		return list;
	}
	
	@DirectMethod
	public List<ProvisionCreditoDTO> llenarGridProvisiones(String psFecha,int plEmpresa,String psFechaIni,String psTipoFuncion,String psDivisa,int tipo) {
		List<ProvisionCreditoDTO> list = new ArrayList<ProvisionCreditoDTO>();
		try {
			//if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			provisionInteresesCService = (ProvisionInteresesCService) contexto
					.obtenerBean("provisionInteresesCBusinessImpl");
			
			list = provisionInteresesCService.llenarGridProvisiones(psFecha,plEmpresa,psFechaIni,psTipoFuncion,psDivisa,tipo);
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:ProvisionInteresesCAction, M:llenarGridProvisiones");
		}
		return list;
	}
	@DirectMethod
	public Map<String, Object> diaHabilReg(String fecha) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			provisionInteresesCService = (ProvisionInteresesCService) contexto
						.obtenerBean("provisionInteresesCBusinessImpl");
				mapResult = provisionInteresesCService.diaHabilReg(fecha);
			//}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:ProvisionInteresesCAction, M:selectInhabil");
		}
		return mapResult;
	}

	@DirectMethod
	public Map<String, Object> updateProvisionEstatus(String provisiones) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			provisionInteresesCService = (ProvisionInteresesCService) contexto
						.obtenerBean("provisionInteresesCBusinessImpl");
				mapResult = provisionInteresesCService.updateProvisionEstatus(provisiones);
			//}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:ProvisionInteresesCAction, M:updateProvisionEstatus");
		}
		return mapResult;
	}
	@DirectMethod
	public Map<String, Object> updateProvisionX(String provisiones,String pdFechaVal) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			provisionInteresesCService = (ProvisionInteresesCService) contexto
						.obtenerBean("provisionInteresesCBusinessImpl");
				mapResult = provisionInteresesCService.updateProvisionX(provisiones,pdFechaVal);
			//}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:ProvisionInteresesCAction, M:updateProvisionX");
		}
		return mapResult;
	}

}