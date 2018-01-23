package com.webset.set.seguridad.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.seguridad.business.CatOrigenMovBusiness;
import com.webset.set.seguridad.business.SegUsuarioBusiness;
import com.webset.set.seguridad.dto.CatOrigenMovDto;
import com.webset.set.seguridad.dto.ComboUsuario;
import com.webset.set.seguridad.dto.OrigenUsuarioDto;
import com.webset.set.seguridad.exceptions.BusinessException;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.utils.tools.Utilerias;
 

public class SegOrigenAction {
	
	Contexto contexto=new Contexto();
	Bitacora bitacora=new Bitacora();
	
	/*
	 * Editado por Luis Serrato 04092015
	private static class ComboUsuario {
		public int idUsuario;
		public String nombre;
		public String claveUsuario;

		private ComboUsuario(int idUsuario,String claveUsuario, String nombre) {
			this.idUsuario = idUsuario;
			this.claveUsuario= claveUsuario;
			this.nombre = nombre;
		}
	}
	*/
	
	class OrigenD{
		public String origen;
		public String descOrigen;
		private OrigenD(String origen, String descOrigen)
		{
			this.origen= origen;
			this.descOrigen= descOrigen;
		}
	}
	/*
	 * Editado por luis Serrato
	 * Este metodo no se usa en las vista
	 */
	@DirectMethod
	public List<ComboUsuario> obtenerUsuarios() {
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 21))
			return null;
		List<ComboUsuario> usuarios = new ArrayList<ComboUsuario>();
		
		try{
			SegUsuarioBusiness segUsuarioBusiness = (SegUsuarioBusiness)contexto.obtenerBean("segUsuarioBusiness");
			usuarios = segUsuarioBusiness.consultar();
	
		}catch(Exception e){
				System.out.println("Cayo en excepcion");
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Seguridad, C:SegPersonasAction, M:obtenerUsuarios");
		}
		return usuarios;
	}
	
	@DirectMethod
	public List<OrigenD> obtenerOrigen(int intIdUsuario, boolean bExiste) {
		System.out.println("llego");
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 21))
			return null;
		System.out.println("paso");
		List<CatOrigenMovDto> listOrigen = new ArrayList<CatOrigenMovDto>();
		List<OrigenD> listOrigenD = new ArrayList<OrigenD>();
		try{
			CatOrigenMovBusiness catOrigenMovBusiness = (CatOrigenMovBusiness) contexto.obtenerBean("catOrigenMovBusiness");
			listOrigen = catOrigenMovBusiness.obtenerListaOrigenes(intIdUsuario, bExiste);
			ListIterator<CatOrigenMovDto> itr = listOrigen.listIterator();
			
			while(itr.hasNext()){
				CatOrigenMovDto origenTmp = itr.next();
			    listOrigenD.add(new OrigenD(origenTmp.getOrigenMov(),origenTmp.getDescOrigenMov()));
			}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegOrigenAction, M:obtenerOrigen");
		}
		return listOrigenD;
	}
	
	
	
	@DirectMethod
	public boolean asignarOrigenes(String claves)
	{
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 21))
			return false;
		int ind=0,idUsuario=0, res=0;
		String noOrigen="";
		boolean todos=false;
		OrigenUsuarioDto dtoOrigenUsuario= new OrigenUsuarioDto();
		ind=claves.indexOf(",");
		try{
			CatOrigenMovBusiness catOrigenMovBusiness = (CatOrigenMovBusiness) contexto.obtenerBean("catOrigenMovBusiness");
			if(ind>0){
				idUsuario=Integer.parseInt(claves.substring(0,ind));
				noOrigen=claves.substring(ind+1);
				todos=false;
				}
			else{
				idUsuario=Integer.parseInt(claves.substring(0));
				todos=true;
			}
			dtoOrigenUsuario.setNoUsuario(idUsuario);
			dtoOrigenUsuario.setOrigenMov(noOrigen);
			res=catOrigenMovBusiness.insertar(dtoOrigenUsuario, todos);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegOrigenAction, M:asignarOrigenes");
		}
		if(res>0)
			return true;
		else
			return false;
	}

	/**
	 * Metodo para eliminar origenes de origen_usuario
	 * @param claves
	 * @return true o false segun la respuesta de la consulta
	 * @throws BusinessException
	 * @throws Exception
	 */
	@DirectMethod
	public boolean eliminarOrigenes(String claves)  
	{
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 21))
			return false;
		int ind=0, idUsuario=0, res=0;
		String noOrigen="";
		boolean todos=false;
		OrigenUsuarioDto dtoOrigenUsuario = new OrigenUsuarioDto();
		ind=claves.indexOf(",");
		try{
			CatOrigenMovBusiness catOrigenMovBusiness = (CatOrigenMovBusiness) contexto.obtenerBean("catOrigenMovBusiness");	
			if(ind>0){
				idUsuario=Integer.parseInt(claves.substring(0,ind));
				noOrigen=claves.substring(ind+1);
				todos=false;
				}
			else{
				idUsuario=Integer.parseInt(claves.substring(0));
				todos=true;
			}
			
			dtoOrigenUsuario.setNoUsuario(idUsuario);
			dtoOrigenUsuario.setOrigenMov(noOrigen);
			res=catOrigenMovBusiness.eliminar(dtoOrigenUsuario,todos);
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegOrigenAction, M:eliminarOrigenes");
		}
		if(res>0)
			return true;
		else
			return false;
	}


}
