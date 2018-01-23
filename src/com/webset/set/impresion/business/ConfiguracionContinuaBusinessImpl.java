package com.webset.set.impresion.business;

import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.impresion.dao.ConfiguracionContinuaDao;
import com.webset.set.impresion.dto.ChequeContinuoDto;
import com.webset.set.impresion.dto.ConfiguracionChequeDto;
import com.webset.set.impresion.service.ConfiguracionContinuaService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

/*
 * Luis Alfredo Serrato Montes de Oca
 */

public class ConfiguracionContinuaBusinessImpl implements ConfiguracionContinuaService {
	private ConfiguracionContinuaDao configuracionContinuaDao;
	Bitacora bitacora = new Bitacora();
	
	
	@Override
	public List<ChequeContinuoDto> obtenerConfiguraciones(){
		List<ChequeContinuoDto> listaConf = new ArrayList<ChequeContinuoDto>();
		
		try {
			listaConf = configuracionContinuaDao.obtenerConfiguraciones();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaBusinessImpl, M:obtenerConfiguraciones");
		}
		
		return listaConf;
	}
	
	@Override
	public List<LlenaComboGralDto> obtenerBancos(){
		List<LlenaComboGralDto> listBancos = new ArrayList<LlenaComboGralDto>();
		try {
			listBancos = configuracionContinuaDao.obtenerBancos();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaBusinessImpl, M:obtenerBancos");
		}
		
		return listBancos;
	}
	
	@Override
	public List<LlenaComboChequeraDto> obtenerChequera(int idBanco, String idEmpresa){
		List<LlenaComboChequeraDto> listaBancos = new ArrayList<LlenaComboChequeraDto>();
		
		try {
			listaBancos = configuracionContinuaDao.obtenerChequera(idBanco, idEmpresa);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaBusinessImpl, M:obtenerChequera");
		}
		
		return listaBancos;
	}
	
	
	
	@Override
	public String insertarConfiguracion(String json){
		String mensaje = "";
		Gson gson = new Gson();
		List<Map<String, String>> datos = gson.fromJson(json, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());

		try {
			mensaje = configuracionContinuaDao.insertarConfiguracion(datos);
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaBusinessImpl, M:insertarConfiguracion");
		}
		
		return mensaje;
	}
	
	@Override
	public String eliminarConfiguracion(int idConfiguracion){
		String mensaje = "";
		
		try {
			mensaje = configuracionContinuaDao.eliminarConfiguracion(idConfiguracion);
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaBusinessImpl, M:eliminarConfiguracion");
		}
		
		return mensaje;
	}
	
	@Override
	public List<ConfiguracionChequeDto> obtenerCampos(int idConf){
		List<ConfiguracionChequeDto> listaCampos = new ArrayList<ConfiguracionChequeDto>();
		
		try {
			listaCampos = configuracionContinuaDao.obtenerCampos(idConf);
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaBusinessImpl, M:obtenerCampos");
		}
		
		return listaCampos;
	}
	
	
	
	@Override
	public List<LlenaComboGralDto> obtenerFuentes(){
		List<LlenaComboGralDto> listaFuentes = new ArrayList<LlenaComboGralDto>();
		String fuentes[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		try {
			for (String string : fuentes) {
				LlenaComboGralDto fuente = new LlenaComboGralDto();
				fuente.setDescripcion(string);
				listaFuentes.add(fuente);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaBusinessImpl, M:obtenerFuentes");
		}
		
		
		return listaFuentes;
	}
	
	@Override
	public String insertarCampos(String json){
		String mensaje = "";
		Gson gson = new Gson();
		List<Map<String, String>> datos = gson.fromJson(json, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());

		try {
			mensaje = configuracionContinuaDao.insertarCampos(datos);
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaBusinessImpl, M:insertarCampos");
		}
		
		return mensaje;
	}
	
	@Override
	public String eliminarCampo(int idCampo){
		String mensaje = "";
		
		try {
			mensaje = configuracionContinuaDao.eliminarCampo(idCampo);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaBusinessImpl, M:eliminarCampo");
		}
		return mensaje;
	}
	
	/******************************************/
	/********GETTERS AND SETTERS***************/
	/******************************************/
	
	public ConfiguracionContinuaDao getConfiguracionContinuaDao() {
		return configuracionContinuaDao;
	}
	public void setConfiguracionContinuaDao(ConfiguracionContinuaDao configuracionDao) {
		this.configuracionContinuaDao = configuracionDao;
	}
	
	
	
	
	
}
