package com.webset.set.seguridad.action;

//UTIL
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.seguridad.business.SegUsuarioPerfilBusiness;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.utils.tools.Utilerias;

/**
* 
* @author Armando Rodriguez
*
*/
public class SegUsuarioPerfilAction {   //SegUsuarioPerfilAction
		private Contexto contexto = new Contexto();
		private Bitacora bitacoraDao = new Bitacora();
		private SegUsuarioPerfilBusiness segUsuarioPerfilBusiness;
		
		
		@DirectMethod
		public List<Map<String, Object>> obtenerPerfilesUsuario(int idUsuario){
			if(!Utilerias.haveSession(WebContextManager.get())
					|| !Utilerias.tienePermiso(WebContextManager.get(), 29))
				return null;
			List<Map<String, Object>> listaUsuarioPerfil = null;
			segUsuarioPerfilBusiness = (SegUsuarioPerfilBusiness)contexto.obtenerBean("segUsuarioPerfilBusiness");
			
			
			try{
				

				listaUsuarioPerfil = segUsuarioPerfilBusiness.obtenerPerfilesUsuario(idUsuario);
			}catch(Exception e){
				bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
						+ "P:Seg, C:SegPerfilFacultadAction, M:seleccionarComponentes");
			}
			return listaUsuarioPerfil;
		}
		
		/*
		@DirectMethod
		public boolean asignarPerfil(String jsonParams){
			segUsuarioPerfilBusiness = (SegUsuarioPerfilBusiness)contexto.obtenerBean("segUsuarioPerfilBusiness");
			int ret = 0;
			
			Gson gson = new Gson();
			// Generar un mapa del objecto json
			Map<String, String> paramsMap = gson.fromJson(jsonParams, new TypeToken<Map<String, String>>(){}.getType());
			int usuario = Integer.parseInt(paramsMap.get("usuario"));
			int perfil = Integer.parseInt(paramsMap.get("perfil"));

			logger.info(paramsMap);
			
			//convert JSON into java objec
			//TestJsonFromObject obj = gson.fromJson(json, TestJsonFromObject.class);
			

			try{
				ret = segUsuarioPerfilBusiness.insertar(usuario, perfil);
			}catch(Exception e){
				bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
						+ "P:Seg, C:SegPerfilFacultadAction, M:seleccionarComponentes");
			}
			return (ret>0);
		}*/

		@DirectMethod
		public boolean asignarPerfiles(int usuario, List<Map<String, String>> perfilesMapLst){
			segUsuarioPerfilBusiness = (SegUsuarioPerfilBusiness)contexto.obtenerBean("segUsuarioPerfilBusiness");
			int ret = 0;
			

			try{
				ret = segUsuarioPerfilBusiness.insertar(usuario, perfilesMapLst);
			}catch(Exception e){
				bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
						+ "P:Seg, C:SegPerfilFacultadAction, M:seleccionarComponentes");
			}
			return (ret>0);
		}
		
		/*
		@DirectMethod
		public boolean asignarTodosLosPerfiles(int idUsuario){
			segUsuarioPerfilBusiness = (SegUsuarioPerfilBusiness)contexto.obtenerBean("segUsuarioPerfilBusiness");
			int perfil = 0;
			int ret = 0;

			try{
				ret = segUsuarioPerfilBusiness.insertar(idUsuario, perfil);
			}catch(Exception e){
				bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
						+ "P:Seg, C:SegPerfilFacultadAction, M:seleccionarComponentes");
			}
			return (ret>0);
		}*/
		
		@DirectMethod
		public boolean eliminarPerfil(String jsonParams){
			segUsuarioPerfilBusiness = (SegUsuarioPerfilBusiness)contexto.obtenerBean("segUsuarioPerfilBusiness");
			int ret = 0;

			Gson gson = new Gson();
			// Generar un mapa del objecto json
			Map<String, String> paramsMap = gson.fromJson(jsonParams, new TypeToken<Map<String, String>>(){}.getType());
			int usuario = Integer.parseInt(paramsMap.get("usuario"));
			int perfil = Integer.parseInt(paramsMap.get("perfil"));
			
			try{
				ret = segUsuarioPerfilBusiness.eliminar(usuario, perfil);
			}catch(Exception e){
				bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
						+ "P:Seg, C:SegPerfilFacultadAction, M:seleccionarComponentes");
			}
			return (ret>0);
		}
		
		@DirectMethod
		public boolean eliminarTodosLosPerfiles(int idUsuario){
			segUsuarioPerfilBusiness = (SegUsuarioPerfilBusiness)contexto.obtenerBean("segUsuarioPerfilBusiness");
			int perfil = 0;
			int ret = 0;
			
			try{
				ret = segUsuarioPerfilBusiness.eliminar(idUsuario, perfil);
			}catch(Exception e){
				bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
						+ "P:Seg, C:SegPerfilFacultadAction, M:seleccionarComponentes");
			}
			return (ret>0);
		}
}
