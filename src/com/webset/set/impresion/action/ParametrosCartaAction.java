package com.webset.set.impresion.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.impresion.dto.ParametrosCartaDto;
import com.webset.set.impresion.service.ParametrosCartaService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.utils.tools.Utilerias;

public class ParametrosCartaAction {

	Contexto contexto = new Contexto();
	Bitacora bitacora = new Bitacora();
	ParametrosCartaService objParametrosCartaService;
	
	@DirectMethod
	public List<ParametrosCartaDto> llenaBanco(){
		List<ParametrosCartaDto> listaResultado = new ArrayList<ParametrosCartaDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),203)) {
			objParametrosCartaService = (ParametrosCartaService)contexto.obtenerBean("objParametrosCartaBusinessImpl");
			listaResultado = objParametrosCartaService.llenaBanco();
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: ParametrosCartaAction, M: llenaBanco");
		}return listaResultado;
	}
	
	@DirectMethod
	public String insertaCarta (String registro){
		System.out.println("Entra a insertaCarta action");
		String mensaje = "";
		Gson gson = new Gson();
		List<Map<String, String>> registroGson = gson.fromJson(registro, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());

		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),203)) {
			objParametrosCartaService = (ParametrosCartaService)contexto.obtenerBean("objParametrosCartaBusinessImpl");
			mensaje = objParametrosCartaService.insertaCarta(registroGson);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: ParametrosCartaAction, M: insertaCarta");
		}
		return mensaje;
	}
	
	@DirectMethod
	public List<ParametrosCartaDto> verificaRegistro(int idBanco, String tipo){
		System.out.println("verifica");
		List<ParametrosCartaDto> listaResultado = new ArrayList<ParametrosCartaDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),203)) {
			objParametrosCartaService = (ParametrosCartaService)contexto.obtenerBean("objParametrosCartaBusinessImpl");
			listaResultado = objParametrosCartaService.verificaRegistro(idBanco, tipo);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoAction, M: verificaRegistro");
		}return listaResultado;
	}
	
	@DirectMethod
	public String eliminaCarta(int idBanco, String tipo){
		String mensaje = "";
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),203)) {
			objParametrosCartaService = (ParametrosCartaService)contexto.obtenerBean("objParametrosCartaBusinessImpl");
			mensaje = objParametrosCartaService.eliminaCarta(idBanco, tipo);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: ParametrosCartaAction,  M: eliminaCarta");
		}return mensaje;
	}
	
	@DirectMethod
	public String actualizaCarta (String registro, String idBanco, String tipo){
		System.out.println("Entra a actualiza Carta");
		String mensaje = "";
		Gson gson = new Gson();
		List<Map<String, String>> registroGson = gson.fromJson(registro, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),203)) {
			objParametrosCartaService = (ParametrosCartaService)contexto.obtenerBean("objParametrosCartaBusinessImpl");
			mensaje = objParametrosCartaService.actualizaCarta(registroGson, idBanco, tipo);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: ParametrosCartaAction, M: actualizaCarta");
		}
		return mensaje;
	}
	
	@DirectMethod
	public List<ParametrosCartaDto> obtieneCarta (int idBanco, String tipo){
		System.out.println(idBanco);
		System.out.println(tipo);
		List<ParametrosCartaDto> listaResultado = new ArrayList<ParametrosCartaDto>();		
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),203)) {
			System.out.println("Llega al action de obtiene");
			objParametrosCartaService = (ParametrosCartaService)contexto.obtenerBean("objParametrosCartaBusinessImpl");
			listaResultado = objParametrosCartaService.obtieneCarta(idBanco, tipo);
			System.out.println(listaResultado.size() + " tama\u00f1o obtiene");
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: ParametrosCartaAction, M: obtieneCarta");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ParametrosCartaDto> llenaGrid(){
		System.out.println("entra grid");
		List<ParametrosCartaDto> listaResultado = new ArrayList<ParametrosCartaDto>();
		try{		
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),203)) {
			objParametrosCartaService = (ParametrosCartaService)contexto.obtenerBean("objParametrosCartaBusinessImpl");
			listaResultado = objParametrosCartaService.llenaGrid( );
			}
		} 
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: ParametrosCartaAction, M: llenaGrid");
		}return listaResultado;
	}
	
}
