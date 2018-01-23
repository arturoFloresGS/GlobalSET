package com.webset.set.seguridad.action;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.seguridad.business.CatAreaBusiness;
import com.webset.set.seguridad.business.SegUsuarioBusiness;
import com.webset.set.seguridad.dto.CatAreaDto;
import com.webset.set.seguridad.dto.ComboUsuario;
import com.webset.set.seguridad.dto.UsuarioAreaDto;
import com.webset.set.seguridad.exceptions.BusinessException;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.utils.tools.Utilerias;
/**
 * 
 * @author Cristian Garcia Garcia
 * @since 30/11/2010
 */

public class SegAreaAction {

	Contexto contexto= new Contexto();
	Bitacora bitacora=new Bitacora();
	/* 
	 * Editado por Luis Serrato 04092015
	 * Se cambio esta clase interna a una publica ComboUsuario.java
	private static class ComboUsuario {
		public int idUsuario;
		public String nombre;
		public String claveUsuario;

		private ComboUsuario(int idUsuario, String claveUsuario, String nombre) {
			this.idUsuario = idUsuario;
			this.claveUsuario= claveUsuario;
			this.nombre = nombre;
		}
	}
	*/
	
	/*
	 * Modificado por luis Serrato 04092015
	 */
	@DirectMethod
	public List<ComboUsuario> obtenerUsuarios() {
		List<ComboUsuario> usuarios = new ArrayList<ComboUsuario>();
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		try{
			SegUsuarioBusiness segUsuarioBusiness = (SegUsuarioBusiness)contexto.obtenerBean("segUsuarioBusiness");
			usuarios = segUsuarioBusiness.consultar();	
		}catch(Exception e){				
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Seguridad, C:SegPersonasAction, M:obtenerUsuarios");
		}
		return usuarios;
	}

	@DirectMethod
	public List<CatAreaDto> obtenerAreas(int intIdUsuario, boolean bExiste) {
		List<CatAreaDto> areas = null;
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		try{
			CatAreaBusiness catAreaBusiness = (CatAreaBusiness) contexto.obtenerBean("catAreaBusiness");
			areas = catAreaBusiness.obtenerListaAreas(intIdUsuario, bExiste);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegAreaAction, M:obtenerAreas");
		}
		
		return areas;
	}
	
	
	@DirectMethod
	public boolean asignarAreas(String claves)
	{
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return false;
		int ind=0,noUsuario=0, noArea=0,res=0;
		boolean todos=false;
		UsuarioAreaDto dtoAreaUsuario= new UsuarioAreaDto();
		ind=claves.indexOf(",");
		try {
			CatAreaBusiness catAreaBusiness = (CatAreaBusiness) contexto.obtenerBean("catAreaBusiness");
			if(ind>0){
				noUsuario=Integer.parseInt(claves.substring(0,ind));
				noArea=Integer.parseInt(claves.substring(ind+1));
				todos=false;
				}
			else{
				noUsuario=Integer.parseInt(claves.substring(0));
				todos=true;
			}
			
			dtoAreaUsuario.setNoUsuario(noUsuario);
			dtoAreaUsuario.setIdArea(noArea);
			res=catAreaBusiness.insertar(dtoAreaUsuario, todos);
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seguridad, C:SegAreaAction, M:asignarAreas");
		}
		if(res>0)
			return true;
		else
			return false;
	}
	
	/**
	 * Metodo para eliminar cajas de usuario_cajas
	 * @param claves
	 * @return true o false segun la respuesta de la consulta
	 * @throws BusinessException
	 * @throws Exception
	 */
	@DirectMethod
	public boolean eliminarAreas(String claves)  
	{
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return false;
		int ind=0, noUsuario=0, noArea=0,res=0;
		boolean todos=false;
		UsuarioAreaDto dtoAreaUsuario= new UsuarioAreaDto();
		ind=claves.indexOf(",");
		try {
			CatAreaBusiness catAreaBusiness = (CatAreaBusiness) contexto.obtenerBean("catAreaBusiness");
			if(ind>0){
				noUsuario=Integer.parseInt(claves.substring(0,ind));
				noArea=Integer.parseInt(claves.substring(ind+1));
				todos=false;
				}
			else{
				noUsuario=Integer.parseInt(claves.substring(0));
				todos=true;
			}
			
			dtoAreaUsuario.setNoUsuario(noUsuario);
			dtoAreaUsuario.setIdArea(noArea);
			res=catAreaBusiness.eliminar(dtoAreaUsuario ,todos);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegAreaAction, M:eliminarAreas");
		}
		if(res>0)
			return true;
		else
			return false;
	}
	
}
