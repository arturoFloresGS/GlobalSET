package com.webset.set.impresion.action;
/*
 * Autor: Luis Alfredo Serrato Montes de Oca
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.impresion.dto.ChequeContinuoDto;
import com.webset.set.impresion.dto.ConfiguracionChequeDto;
import com.webset.set.impresion.service.ConfiguracionContinuaService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;

public class ConfiguracionContinuaAction {
	Bitacora bitacora = new Bitacora();
	Contexto contexto = new  Contexto();
	ConfiguracionContinuaService configuracionContinua;
	
	@DirectMethod
	public List<ChequeContinuoDto> obtenerConfiguraciones(){
		List<ChequeContinuoDto> listaConf = new ArrayList<ChequeContinuoDto>();
		
		try {
			if(Utilerias.haveSession(WebContextManager.get())){
				configuracionContinua = (ConfiguracionContinuaService)contexto.obtenerBean("configuracionContinuaBusinessImpl");
				listaConf = configuracionContinua.obtenerConfiguraciones();
			}
			
		} catch (Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaAction, M:obtenerConfiguraciones");
		}
		
		return listaConf;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> obtenerBancos(){
		List<LlenaComboGralDto> listaBancos = new ArrayList<LlenaComboGralDto>();
		
		try {
			if(Utilerias.haveSession(WebContextManager.get())){
				configuracionContinua = (ConfiguracionContinuaService)contexto.obtenerBean("configuracionContinuaBusinessImpl");
				listaBancos = configuracionContinua.obtenerBancos();
			}
			
		} catch (Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaAction, M:obtenerConfiguraciones");
		}
		
		return listaBancos;
	}
	
	@DirectMethod
	public List<LlenaComboChequeraDto> obtenerChequeras(int idBanco, String idEmpresa){
		System.out.println("---> ENTRO");
		List<LlenaComboChequeraDto> listaChequeras = new ArrayList<LlenaComboChequeraDto>();
		
		try {
			if(Utilerias.haveSession(WebContextManager.get())){
				configuracionContinua = (ConfiguracionContinuaService)contexto.obtenerBean("configuracionContinuaBusinessImpl");
				listaChequeras = configuracionContinua.obtenerChequera(idBanco, idEmpresa);
			}
			
		} catch (Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaAction, M:obtenerChequerasq");
		}
		
		return listaChequeras;
	}
	
	@DirectMethod
	public String insertarConfiguracion(String json){
		String mensaje = "";
		
		try {
			if(Utilerias.haveSession(WebContextManager.get())){
				configuracionContinua = (ConfiguracionContinuaService)contexto.obtenerBean("configuracionContinuaBusinessImpl");
				mensaje = configuracionContinua.insertarConfiguracion(json);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaAction, M:insertarConfiguracion");
		}
		
		return mensaje;
	}
	
	@DirectMethod
	public String eliminarConfiguracion(int idConfiguracion){
		String mensaje = "";
		
		try {
			if(Utilerias.haveSession(WebContextManager.get())){
				configuracionContinua = (ConfiguracionContinuaService)contexto.obtenerBean("configuracionContinuaBusinessImpl");
				mensaje = configuracionContinua.eliminarConfiguracion(idConfiguracion);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaAction, M:eliminarConfiguracion");
		}
		
		return mensaje;
	}
	
	@DirectMethod
	public List<ConfiguracionChequeDto> obtenerCampos(int idConf){
		List<ConfiguracionChequeDto> listaCampos = new ArrayList<ConfiguracionChequeDto>();
		
			try {
				if(Utilerias.haveSession(WebContextManager.get())){
					configuracionContinua = (ConfiguracionContinuaService)contexto.obtenerBean("configuracionContinuaBusinessImpl");
					listaCampos = configuracionContinua.obtenerCampos(idConf);
				}
			} catch (Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Impresion, C:ConfiguracionContinuaAction, M:obtenerCampos");
			}
		
		return listaCampos;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> obtenerFuentes(){
		List<LlenaComboGralDto> listaFuentes = new ArrayList<LlenaComboGralDto>();
		
		try {
			if(Utilerias.haveSession(WebContextManager.get())){
				configuracionContinua = (ConfiguracionContinuaService)contexto.obtenerBean("configuracionContinuaBusinessImpl");
				listaFuentes = configuracionContinua.obtenerFuentes();
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaAction, M:obtenerFuentes");
		}
		
	
		return listaFuentes;
	}
	
	@DirectMethod
	public String insertarCampos(String json){
		String mensaje = "";
		
		try {
			if(Utilerias.haveSession(WebContextManager.get())){
				configuracionContinua = (ConfiguracionContinuaService)contexto.obtenerBean("configuracionContinuaBusinessImpl");
				mensaje = configuracionContinua.insertarCampos(json);
			}
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaAction, M:insertarCampos");
		}
		
		return mensaje;
	}
	
	@DirectMethod
	public String eliminarCampo(int idCampo){
		String mensaje = "";
		
		try {
			if(Utilerias.haveSession(WebContextManager.get())){
				configuracionContinua = (ConfiguracionContinuaService)contexto.obtenerBean("configuracionContinuaBusinessImpl");
				mensaje = configuracionContinua.eliminarCampo(idCampo);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaAction, M:eliminarCampo");
		}
		
		return mensaje;
	}
	
	
	
}
