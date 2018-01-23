package com.webset.set.inversiones.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.inversiones.dto.MantenimientoDePapelDto;
import com.webset.set.inversiones.middleware.service.MantenimientoDePapelService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.utils.tools.Utilerias;

public class MantenimientoDePapelAction {
	private Bitacora bitacora = new Bitacora();
	private Contexto contexto = new Contexto();
	//private static Logger logger = Logger.getLogger(MantenimientoDePapelAction.class);
	
	@DirectMethod
	public List<MantenimientoDePapelDto> consultarPapel(){
		List<MantenimientoDePapelDto> listConsPap = new ArrayList<MantenimientoDePapelDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listConsPap;
		try{
			MantenimientoDePapelService MantenimientoDePapelService = (MantenimientoDePapelService) contexto.obtenerBean("MantenimientoDePapelBusinessImpl");
			listConsPap = MantenimientoDePapelService.consultarPapel();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:MantenimientoDePapelAction, M:consultarPapel");
		}
		return listConsPap;
	}
	
	@DirectMethod
	public List<MantenimientoDePapelDto> llenarComboTipoValor(){
		List<MantenimientoDePapelDto> listCtv = new ArrayList<MantenimientoDePapelDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listCtv;
		try{
			MantenimientoDePapelService MantenimientoDePapelService = (MantenimientoDePapelService) contexto.obtenerBean("MantenimientoDePapelBusinessImpl");
			listCtv = MantenimientoDePapelService.llenarComboTipoValor();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:MantenimientoDePapelAction, M:llenarComboTipoValor");
		}
		return listCtv;
	}
	
	@DirectMethod
	public Map<String, Object> accionPapel(String datPapel, char bandera, String confir) { 
		Map<String, Object> mapRet = new HashMap<String, Object>();
		Gson gson = new Gson();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return mapRet;
		try {
			MantenimientoDePapelService MantenimientoDePapelService = (MantenimientoDePapelService) contexto.obtenerBean("MantenimientoDePapelBusinessImpl");
			List<Map<String, String>> gListPapel = gson.fromJson(datPapel, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			if(gListPapel.get(0).get("idPapel").equals("")){
				mapRet.put("msgUsuario","Falta tipo de papel");
				return mapRet;
			}else if(gListPapel.get(0).get("idTipoValor").equals("")){
				mapRet.put("msgUsuario","Falta id de tipo valor");
				return mapRet;
			}
			mapRet = MantenimientoDePapelService.accionPapel(gListPapel, bandera, confir);
		}catch(Exception e) {bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Inversiones, C:MantenimientoDePapelAction, M:accionPapel"); }
		return mapRet;
	}
	
	@DirectMethod
	public Map<String, Object> eliminarPapel(String papel){ 
		Map<String, Object> mapRet = new HashMap<String, Object>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return mapRet;
		try {
		MantenimientoDePapelService MantenimientoDePapelService = (MantenimientoDePapelService) contexto.obtenerBean("MantenimientoDePapelBusinessImpl");
		mapRet= MantenimientoDePapelService.eliminarPapel(papel);
		}catch(Exception e) {bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Inversiones, C:MantenimientoDePapelAction, M:eliminarPapel"); }
		return mapRet;
	}

}
