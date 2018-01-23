package com.webset.set.financiamiento.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.financiamiento.service.AltaFinanciamientoService;
import com.webset.set.financiamiento.service.AvalesGarantiasFCService;
import com.webset.set.financiamiento.service.FinanciamientoModificacionCService;
import com.webset.set.financiamiento.service.GastosFinanciamientoCService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;
import com.webset.set.financiamiento.dto.AmortizacionCreditoDto;
import com.webset.set.financiamiento.dto.Amortizaciones;
import com.webset.set.financiamiento.dto.ContratoCreditoDto;


public class FinanciamientoModificacionCAction {
	
	Bitacora bitacora = new Bitacora();
	Contexto contexto=new  Contexto();
	Funciones funciones = new Funciones();
	FinanciamientoModificacionCService financiamientoModificacionCService;
	
	@DirectMethod
	public List<LlenaComboGralDto> obtenerEmpresas(int idUsuario, boolean bMantenimiento) {
		List<LlenaComboGralDto> listEmpresas = new ArrayList<LlenaComboGralDto>();
		try {
			 financiamientoModificacionCService = (FinanciamientoModificacionCService) contexto.obtenerBean("financiamientoModificacionCBusinessImpl");
			listEmpresas = financiamientoModificacionCService.obtenerEmpresas(idUsuario, bMantenimiento);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:FinanciamientoModificacionCAction, M:obtenerEmpresas");
		}
		return listEmpresas;
	}
	@DirectMethod
	public List<LlenaComboGralDto> obtenerContratos(int empresa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
		financiamientoModificacionCService = (FinanciamientoModificacionCService) contexto
						.obtenerBean("financiamientoModificacionCBusinessImpl");
				list = financiamientoModificacionCService.obtenerContratos(empresa);
		
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:FinanciamientoModificacionCAction, M:obtenerContratos");
		}
		return list;
	}
	@DirectMethod
	public List<LlenaComboGralDto> obtenerDisposiciones(String linea, boolean estatus) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			financiamientoModificacionCService = (FinanciamientoModificacionCService) contexto
						.obtenerBean("financiamientoModificacionCBusinessImpl");
				list = financiamientoModificacionCService.obtenerDisposiciones(linea,estatus);
		
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:FinanciamientoModificacionCAction, M:obtenerDisposiciones");
		}
		return list;
	}
	@DirectMethod 
	public List<LlenaComboGralDto>llenarCmbTasa(){
		List<LlenaComboGralDto> listDis = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listDis;
		try{																										   
			 financiamientoModificacionCService= (FinanciamientoModificacionCService)
					 contexto.obtenerBean("financiamientoModificacionCBusinessImpl");
			listDis  = financiamientoModificacionCService.llenarCmbTasa();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: FinanciamientoModificacionC C: FinanciamientoModificacionCAction M: llenarCmbTasa");
		}
		return listDis;
	}
	@DirectMethod
	public List<AmortizacionCreditoDto> selectAmortizaciones(String psIdContrato, int piDisposicion,
			boolean pbCambioTasa, String psTipoMenu, String psProyecto, int piCapital) {
		List<AmortizacionCreditoDto> list = new ArrayList<AmortizacionCreditoDto>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			financiamientoModificacionCService = (FinanciamientoModificacionCService) contexto
						.obtenerBean("financiamientoModificacionCBusinessImpl");
				list = financiamientoModificacionCService.selectAmortizaciones(psIdContrato, piDisposicion, pbCambioTasa,
						psTipoMenu, psProyecto, piCapital);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:FinanciamientoModificacionCAction, M:selectAmortizaciones");
		}
		return list;
	}
	@DirectMethod
	public List<ContratoCreditoDto> funSQLTasa(String psTasa) {
		List<ContratoCreditoDto> list = new ArrayList<ContratoCreditoDto>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			financiamientoModificacionCService = (FinanciamientoModificacionCService) contexto
						.obtenerBean("financiamientoModificacionCBusinessImpl");
				list = financiamientoModificacionCService.funSQLTasa(psTasa);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:FinanciamientoModificacionCAction, M:funSQLTasa");
		}
		return list;
	}
	
	@DirectMethod
	public  Map<String, Object> modificar(String contrato, int disposicion,int optTasa, String cmbTasaBase,String txtValTasa,String txtPuntos,String txtTasaVig, String txtTasaFij,String txtFecCor, boolean chkCapital,
			String txtRenta, String txtIva, String jsonGrid) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			financiamientoModificacionCService = (FinanciamientoModificacionCService) contexto
					.obtenerBean("financiamientoModificacionCBusinessImpl");
			mapResult = financiamientoModificacionCService.modificar(contrato,disposicion,optTasa, cmbTasaBase, txtValTasa, txtPuntos, txtTasaVig,  txtTasaFij, txtFecCor,  chkCapital,
					 txtRenta,  txtIva,  jsonGrid);
			//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:FinanciamientoModificacionCAction, M:modificar");
		}
		return mapResult;
	}
	@DirectMethod
	public  Map<String, Object> modificaProvision(String contrato, int disposicion, int empresa) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			financiamientoModificacionCService = (FinanciamientoModificacionCService) contexto
					.obtenerBean("financiamientoModificacionCBusinessImpl");
			mapResult = financiamientoModificacionCService.modificaProvision(contrato,disposicion,empresa);
			//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:FinanciamientoModificacionCAction, M:modificar");
		}
		return mapResult;
	}
}
