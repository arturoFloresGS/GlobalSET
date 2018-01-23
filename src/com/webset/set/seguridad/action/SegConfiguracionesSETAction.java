package com.webset.set.seguridad.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.seguridad.business.SegConfiguracionesSETBusiness;
import com.webset.set.seguridad.dto.SegConfiguracionesSETDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.utils.tools.Utilerias;

public class SegConfiguracionesSETAction {
	Contexto contexto=new Contexto();
	Bitacora bitacora= new Bitacora();
	//private SegMantenimientoCatalogosBusiness segMantenimientoCatalogosBusinees;
	boolean retorno=false;
	
	@DirectMethod
	public List<SegConfiguracionesSETDto> llenaConfiguraciones(){
		if(!Utilerias.haveSession(WebContextManager.get())
		|| (!Utilerias.tienePermiso(WebContextManager.get(), 218)))
			return null;
		
		List<SegConfiguracionesSETDto> conf=new ArrayList<SegConfiguracionesSETDto>();
		
		try{
			SegConfiguracionesSETBusiness confBusiness=(SegConfiguracionesSETBusiness) contexto.obtenerBean("segConfiguracionesSETBusiness");
			conf=confBusiness.llenaConfiguraciones();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegConfiguracionesSETAction, M:llenaConfiguraciones");
		}
			
		return conf;
	}
	
	@DirectMethod
	public Map<String, Object> guardarConfiguracion(String jsDatos) {
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 218))
			return null;
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try{
			SegConfiguracionesSETBusiness confBusiness=(SegConfiguracionesSETBusiness) contexto.obtenerBean("segConfiguracionesSETBusiness");
			Gson gson = new Gson();
			List<Map<String, String>> datos = gson.fromJson(jsDatos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			resultado = confBusiness.guardarConfiguracion(datos.get(0));
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegConfiguracionesSETAction, M:guardarConfiguracion");
		}
		return resultado;
	}
	
	@DirectMethod
	public Map<String, Object> modificarConfiguracion(String jsDatos) {
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 218))
			return null;
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try{
			SegConfiguracionesSETBusiness confBusiness=(SegConfiguracionesSETBusiness) contexto.obtenerBean("segConfiguracionesSETBusiness");
			Gson gson = new Gson();
			List<Map<String, String>> datos = gson.fromJson(jsDatos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			resultado = confBusiness.modificarConfiguracion(datos.get(0));
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegConfiguracionesSETAction, M:modificarConfiguracion");
		}
		return resultado;
	}
	
	@DirectMethod
	public Map<String, Object> eliminarConfiguracion(String id) {
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 218))
			return null;
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try{
			SegConfiguracionesSETBusiness confBusiness=(SegConfiguracionesSETBusiness) contexto.obtenerBean("segConfiguracionesSETBusiness");
			resultado = confBusiness.eliminarConfiguracion(id);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegConfiguracionesSETAction, M:eliminarConfiguracion");
		}
		return resultado;
	}
}
