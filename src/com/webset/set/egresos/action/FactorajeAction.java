package com.webset.set.egresos.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContext;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.egresos.dto.PagosPendientesDto;
import com.webset.set.egresos.service.FactorajeService;
import com.webset.set.impresion.service.ChequesPorEntregarService;

/*
 * Autor: Luis Alfredo Serrato Montes de Oca
 * 30/11/2015
 */

import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;

public class FactorajeAction {
	
	Bitacora bitacora = new Bitacora();
	Contexto contexto = new  Contexto();
	FactorajeService factorajeService;
	
	
	@DirectMethod
	public List<PagosPendientesDto> obtenerListaFactoraje(int empresa, int proveedor, String fechaIni, String fechaFin){
		List<PagosPendientesDto> listFactoraje = new ArrayList<PagosPendientesDto>();
		
		try {
			if(Utilerias.haveSession(WebContextManager.get())){
				factorajeService = (FactorajeService)contexto.obtenerBean("factorajeBusinessImpl");
				listFactoraje = factorajeService.obtenerListaFactoraje(empresa, proveedor, fechaIni, fechaFin);
			}	
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:FactorajeAction, M:obtenerListaFactoraje");
		}
		
		return listFactoraje;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboBeneficiario(String campoUno, String campoDos, String tabla, String condicion, String orden, boolean regUnico){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get()))
			return list;
		
		LlenaComboGralDto dto= new LlenaComboGralDto();

		try{
			factorajeService = (FactorajeService)contexto.obtenerBean("factorajeBusinessImpl");
			dto.setCampoUno(campoUno!=null && !campoUno.equals("")?campoUno:"");
			dto.setCampoDos(campoDos!=null && !campoDos.equals("")?campoDos:"");
			dto.setTabla(tabla!=null && !tabla.equals("")?tabla:"");
			dto.setCondicion(condicion!=null && !condicion.equals("")?condicion:"");
			dto.setOrden(orden!=null && !orden.equals("")?orden:"");
			dto.setRegistroUnico(regUnico);
			list = factorajeService.llenarComboBeneficiario(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:FactorajeAction, M:llenarComboBeneficiario");
		}
		return list;
	}
	
	
	@DirectMethod
	public List<LlenaComboGralDto> obtenerProveedores(String filtro){
		List<LlenaComboGralDto> listProveedores = new ArrayList<LlenaComboGralDto>();
		
		try {
			if(Utilerias.haveSession(WebContextManager.get())){
				factorajeService = (FactorajeService)contexto.obtenerBean("factorajeBusinessImpl");
				listProveedores = factorajeService.obtenerProveedores(filtro);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:FactorajeAction, M:obtenerProveedores");
		}
		
		return listProveedores;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> obtenerIntermediarios(){
		List<LlenaComboGralDto> listIntermediarios = new ArrayList<LlenaComboGralDto>();
		
		try {
			if(Utilerias.haveSession(WebContextManager.get())){
				factorajeService = (FactorajeService)contexto.obtenerBean("factorajeBusinessImpl");
				listIntermediarios = factorajeService.obtenerIntermediarios();
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:FactorajeAction, M:obtenerIntermediarios");
		}
		
		return listIntermediarios;
	}
	
	@DirectMethod
	public Map<String,Object> enviarDatos(String json, int noFactoraje, String fechaFactoraje){
		Map<String, Object>mapRetorno = new HashMap<String, Object>();
		mapRetorno.put("msgError", "Error desconocido");
		mapRetorno.put("estatus", false);
		try{
			if(Utilerias.haveSession(WebContextManager.get())){
				factorajeService = (FactorajeService)contexto.obtenerBean("factorajeBusinessImpl");
				mapRetorno =  factorajeService.enviarDatos(json, noFactoraje, fechaFactoraje);
			}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:FactorajeAction, M:enviarDatos");
			
		}
		return mapRetorno;
		
	}
}
