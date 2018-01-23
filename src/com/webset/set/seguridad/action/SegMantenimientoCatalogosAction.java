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
import com.webset.set.seguridad.business.SegComponenteBusiness;
import com.webset.set.seguridad.business.SegMantenimientoCatalogosBusiness;
import com.webset.set.seguridad.dto.SegComponenteDto;
import com.webset.set.seguridad.dto.SegMantenimientoCatalogosDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.utils.tools.Utilerias;

public class SegMantenimientoCatalogosAction {
	Contexto contexto=new Contexto();
	Bitacora bitacora= new Bitacora();
	//private SegMantenimientoCatalogosBusiness segMantenimientoCatalogosBusinees;
	boolean retorno=false;
	
	@DirectMethod
	public List<SegMantenimientoCatalogosDto> llenaCatalogos(){
		if(!Utilerias.haveSession(WebContextManager.get())
		|| (!Utilerias.tienePermiso(WebContextManager.get(), 217)))
			return null;
		
		List<SegMantenimientoCatalogosDto> cat=new ArrayList<SegMantenimientoCatalogosDto>();
		SegMantenimientoCatalogosBusiness catBusiness=(SegMantenimientoCatalogosBusiness) contexto.obtenerBean("segMantenimientoCatalogosBusiness");
		try{
			cat=catBusiness.llenaCatalogos();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegMantenimientoCatalogosAction, M:llenaCatalogos");
		}
			
		return cat;
	}
	
	@DirectMethod
	public Map<String, Object> guardarCatalogo(String jsDatos) {
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 217))
			return null;
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try{
			System.out.println("Entro action");
			SegMantenimientoCatalogosBusiness catBusiness=(SegMantenimientoCatalogosBusiness) contexto.obtenerBean("segMantenimientoCatalogosBusiness");
			Gson gson = new Gson();
			List<Map<String, String>> datos = gson.fromJson(jsDatos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			resultado = catBusiness.guardarCatalogo(datos.get(0));
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegMantenimientoCatalogosAction, M:guardarCatalogo");
		}
		return resultado;
	}

	@DirectMethod
	public Map<String, Object> modificarCatalogo(String jsDatos) {
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 217))
			return null;
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try{
			SegMantenimientoCatalogosBusiness catBusiness=(SegMantenimientoCatalogosBusiness) contexto.obtenerBean("segMantenimientoCatalogosBusiness");
			Gson gson = new Gson();
			List<Map<String, String>> datos = gson.fromJson(jsDatos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			resultado = catBusiness.modificarCatalogo(datos.get(0));
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegMantenimientoCatalogosAction, M:modificarCatalogo");
		}
		return resultado;
	}
	
	@DirectMethod
	public Map<String, Object> eliminarCatalogo(String id) {
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 217))
			return null;
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try{
			SegMantenimientoCatalogosBusiness catBusiness=(SegMantenimientoCatalogosBusiness) contexto.obtenerBean("segMantenimientoCatalogosBusiness");
			resultado = catBusiness.eliminarCatalogo(id);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegMantenimientoCatalogosAction, M:eliminarCatalogo");
		}
		return resultado;
	}
}
