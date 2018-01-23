package com.webset.set.seguridad.action;
/**
 * @author Cristian Garcia Garcia
 */
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.seguridad.business.CatCajaBusiness;
import com.webset.set.seguridad.business.SegUsuarioBusiness;
import com.webset.set.seguridad.dto.CajaUsuarioDto;
import com.webset.set.seguridad.dto.CatCajaDto;
import com.webset.set.seguridad.dto.ComboUsuario;
import com.webset.set.seguridad.exceptions.BusinessException;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.utils.tools.Utilerias;

public class SegCajaAction {
	Contexto contexto = new Contexto();
	Bitacora bitacora=new Bitacora();
	private static Logger logger = Logger.getLogger(SegCajaAction.class);
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
	/*
	 * Editado por Luis Serrato 04092015
	 * Este metodo no se usa en las vistas
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
				System.out.println("Cayo en excepcion");
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Seguridad, C:SegPersonasAction, M:obtenerUsuarios");
		}
		return usuarios;
	}
	
	@DirectMethod
	public List<CatCajaDto> obtenerCajas(int intIdUsuario, boolean bExiste) {
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 19))
			return null;
		
		List<CatCajaDto> areas = null;
		try{
			CatCajaBusiness catCajaBusiness = (CatCajaBusiness) contexto.obtenerBean("catCajaBusiness");
			areas = catCajaBusiness.obtenerListaCajas(intIdUsuario, bExiste);
			logger.info("areas="+areas.get(0).getIdCaja());
			logger.info("areas="+areas.get(0).getDescCaja());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegCajaAction, M:obtenerUsuarios");
		}
		
		return areas;
	}
	
	
	@DirectMethod
	public boolean asignarCajas(String claves)
	{
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 19))
			return false;
		
		int ind=0,noUsuario=0, noCaja=0, res=0;
		boolean todos=false;
		CajaUsuarioDto dtoCajaUsuario= new CajaUsuarioDto();
		ind=claves.indexOf(",");
		try {
			CatCajaBusiness catCajaBusiness = (CatCajaBusiness) contexto.obtenerBean("catCajaBusiness");	
			if(ind>0){
				noUsuario=Integer.parseInt(claves.substring(0,ind));
				noCaja=Integer.parseInt(claves.substring(ind+1));
				todos=false;
				}
			else{
				noUsuario=Integer.parseInt(claves.substring(0));
				todos=true;
				}
			dtoCajaUsuario.setNoUsuario(noUsuario);
			dtoCajaUsuario.setIdCaja(noCaja);
			res=catCajaBusiness.insertar(dtoCajaUsuario, todos);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegCajaAction, M:asignarCajas");
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
	public boolean eliminarCajas(String claves)  
	{
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 19))
			return false;
		int ind=0, noUsuario=0, noCaja=0,res=0;
		boolean todos=false;
		
		CajaUsuarioDto dtoCajaUsuario= new CajaUsuarioDto();
		ind=claves.indexOf(",");
		try {
			CatCajaBusiness catCajaBusiness = (CatCajaBusiness) contexto.obtenerBean("catCajaBusiness");
			if(ind>0){
				noUsuario=Integer.parseInt(claves.substring(0,ind));
				noCaja=Integer.parseInt(claves.substring(ind+1));
				todos=false;
				}
			else{
				noUsuario=Integer.parseInt(claves.substring(0));
				todos=true;
			}
			dtoCajaUsuario.setNoUsuario(noUsuario);
			dtoCajaUsuario.setIdCaja(noCaja);
			res=catCajaBusiness.eliminar(dtoCajaUsuario ,todos);
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegCajaAction, M:asignarCajas");
		}
		if(res>0)
			return true;
		else
			return false;
	}
	
	@DirectMethod
	public List<CatCajaDto> obtenerTodasCajas() {
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 29))
			return null;
		List<CatCajaDto> areas = null;
		
		try{
			CatCajaBusiness catCajaBusiness = (CatCajaBusiness) contexto.obtenerBean("catCajaBusiness");
			
			areas = catCajaBusiness.obtenerTodasCajas();
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegCajaAction, M:obtenerTodasCajas");
		}
		return areas;
	}


}
