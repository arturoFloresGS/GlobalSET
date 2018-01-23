package com.webset.set.flujos.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.flujos.dto.MantenimientoDeRubrosDto;
import com.webset.set.flujos.dto.PosicionBancariaDto;
import com.webset.set.flujos.service.MantenimientoDeRubrosService;
import com.webset.set.inversiones.middleware.service.MantenimientoDePapelService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.utils.tools.Utilerias;


public class MantenimientoDeRubrosAction {
	private Bitacora bitacora = new Bitacora();
	private Contexto contexto = new Contexto();
	private static Logger logger = Logger.getLogger(MantenimientoDeRubrosAction.class);
	
	@DirectMethod
	public List<MantenimientoDeRubrosDto> llenarComboGrupo(){
		List<MantenimientoDeRubrosDto> listCr = new ArrayList<MantenimientoDeRubrosDto>();
		try{
		//	if (Utilerias.haveSession(WebContextManager.get())) {
			MantenimientoDeRubrosService mantenimientoDeRubrosService = (MantenimientoDeRubrosService) contexto.obtenerBean("mantenimientoDeRubrosBusinessImpl");
			listCr = mantenimientoDeRubrosService.llenarComboGrupo();
//			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Flujos, C:MantenimientoDeRubrosAction, M:llenarComboGrupo");
		}
		return listCr;
	}
	
	@DirectMethod
	public List<MantenimientoDeRubrosDto> consultarRubro(int idGrupo){
		List<MantenimientoDeRubrosDto> listConsRub = new ArrayList<MantenimientoDeRubrosDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			MantenimientoDeRubrosService mantenimientoDeRubrosService = (MantenimientoDeRubrosService) contexto.obtenerBean("mantenimientoDeRubrosBusinessImpl");
			listConsRub = mantenimientoDeRubrosService.consultarRubro(idGrupo);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Flujos, C:MantenimientoDeRubrosAction, M:consultarRubro");
		}
		return listConsRub;
	}
	
	@SuppressWarnings("unchecked")
	@DirectMethod
	public Map accionRubro(String datRubro) { 
		Map<String, Object> mapRet = new HashMap<String, Object>();
		Gson gson = new Gson();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			MantenimientoDeRubrosService mantenimientoDeRubrosService = (MantenimientoDeRubrosService) contexto.obtenerBean("mantenimientoDeRubrosBusinessImpl");
			List<Map<String, String>> gListRubro = gson.fromJson(datRubro, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			if(gListRubro.get(0).get("idRubro").equals("")){
				mapRet.put("msgUsuario","Falta ID de rubro");
				return mapRet;
			}else if(gListRubro.get(0).get("rubro").equals("")){
				mapRet.put("msgUsuario","Falta descripcion de rubro");
				return mapRet;
			}else if(gListRubro.get(0).get("ingesoEgreso").equals("")){
				mapRet.put("msgUsuario","Falta Ingreso o egreso");
				return mapRet;
			}
			mapRet = mantenimientoDeRubrosService.accionRubro(gListRubro);
			}
		}catch(Exception e) {bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:MantenimientoDeRubrosAction, M:accionRubro"); }
		return mapRet;
	}
	
	@SuppressWarnings("unchecked")
	@DirectMethod
	public Map eliminarRubro(int grupo, int rubro){ 
		Map<String, Object> mapRet = new HashMap<String, Object>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			MantenimientoDeRubrosService mantenimientoDeRubrosService = (MantenimientoDeRubrosService) contexto.obtenerBean("mantenimientoDeRubrosBusinessImpl");
		mapRet= mantenimientoDeRubrosService.eliminarRubro(grupo,rubro);
			}
		}catch(Exception e) {bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:MantenimientoDeRubrosAction, M:eliminarRubro"); }
		return mapRet;
	}

}
