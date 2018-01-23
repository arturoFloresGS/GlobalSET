package com.webset.set.seguridad.action;
/**
 * @author Cristian Garcia Garcia
 * Clase para obtener los valores del Personas.js
 */
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.seguridad.business.PersonaBusiness;
import com.webset.set.seguridad.business.SegUsuarioBusiness;
import com.webset.set.seguridad.dto.ComboUsuario;
import com.webset.set.seguridad.dto.PersonaDto;
import com.webset.set.seguridad.dto.UsuarioProveedorDto;
import com.webset.set.seguridad.exceptions.BusinessException;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.utils.tools.Utilerias;
public class SegPersonasAction {

	Contexto contexto= new Contexto();
	Bitacora bitacora=new Bitacora();

	/*
	 * El codigo de esta clase se utiliza en algunas otras clases, por lo que se creo una clase publica
	 * ComboUsuario
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
	}*/
	
	class PersonaD{
		public int noPersona;
		public String razonSocial;
		private PersonaD(int noPersona, String razonSocial)
		{
			this.noPersona= noPersona;
			this.razonSocial= razonSocial;
		}
	}
	/**
	 * Metodo que obtiene Los usuarios para los combos
	 * @return comboUsuarios que tiene id,nombre y clave del usuario
	 * Editado por Luis Serrato 04092015
	 */
	@DirectMethod
	public List<ComboUsuario> obtenerUsuarios() {
		List<ComboUsuario> usuarios = new ArrayList<ComboUsuario>();
		if(!Utilerias.haveSession(WebContextManager.get())
				|| (!Utilerias.tienePermiso(WebContextManager.get(), 19)&&
				!Utilerias.tienePermiso(WebContextManager.get(), 20)&&
				!Utilerias.tienePermiso(WebContextManager.get(), 21)))
			return usuarios;
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
	/**
	 * Metodo que obtiene las personas para llenar los grids
	 * @param intIdUsuario trae el identificador del usuario
	 * @param bExiste
	 * @return lista que tiene noPersona y razonSocial
	 */
	@DirectMethod
	public List<PersonaD> obtenerPersonas(int intIdUsuario, boolean bExiste) {
		List<PersonaDto> listPersona = new ArrayList<PersonaDto>();
		List<PersonaD> listPersonaD = new ArrayList<PersonaD>();
		try{
			PersonaBusiness personaBusiness = (PersonaBusiness) contexto.obtenerBean("personaBusiness");
			listPersona = personaBusiness.obtenerListaPersonas(intIdUsuario, bExiste);
			ListIterator<PersonaDto> itr = listPersona.listIterator();
			
			while(itr.hasNext()){
				PersonaDto personaTmp = itr.next();
			    listPersonaD.add(new PersonaD(personaTmp.getNoPersona(),personaTmp.getRazonSocial()));
			}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegPersonasAction, M:obtenerPersonas");
		}
		
		return listPersonaD;
	}
	/**
	 * Metodo para Asignar personas a los usuarios
	 * @param claves tiene el idUsuario y idPersona
	 */
	@DirectMethod
	public boolean asignarPersonas(String claves)
	{
		int ind=0,idUsuario=0, idPersona=0, res=0;;
		boolean todos=false;
		try{
		PersonaBusiness personaBusiness = (PersonaBusiness) contexto.obtenerBean("personaBusiness");
		UsuarioProveedorDto dtoUsuarioProveedor= new UsuarioProveedorDto();
		ind=claves.indexOf(",");
		if(ind>0){
			idUsuario=Integer.parseInt(claves.substring(0,ind));
			idPersona=Integer.parseInt(claves.substring(ind+1));
			todos=false;
			}
		else{
			idUsuario=Integer.parseInt(claves.substring(0));
			todos=true;
		}
		dtoUsuarioProveedor.setNoUsuario(idUsuario);
		dtoUsuarioProveedor.setNoPersona(idPersona);
		res=personaBusiness.insertar(dtoUsuarioProveedor, todos);
		
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegPersonasAction, M:asignarUsuarios");
		}
		if(res>0)
			return true;
		else 
			return false;
		
	}
	
	/**
	 * Metodo para eliminar personas de usuario_proveedor
	 * @param claves
	 * @return true o false segun la respuesta de la consulta
	 * @throws BusinessException
	 * @throws Exception
	 */
	@DirectMethod
	public boolean eliminarPersonas(String claves) 
	{
		int ind=0, idUsuario=0, idPersona=0, res=0;
		boolean todos=false;
		try{
			PersonaBusiness personaBusiness = (PersonaBusiness) contexto.obtenerBean("personaBusiness");
			UsuarioProveedorDto dtoUsuarioProveedor= new UsuarioProveedorDto();
			ind=claves.indexOf(",");
			if(ind>0){
				idUsuario=Integer.parseInt(claves.substring(0,ind));
				idPersona=Integer.parseInt(claves.substring(ind+1));
				todos=false;
				}
			else{
				idUsuario=Integer.parseInt(claves.substring(0));
				todos=true;
			}
			dtoUsuarioProveedor.setNoUsuario(idUsuario);
			dtoUsuarioProveedor.setNoPersona(idPersona);
			res=personaBusiness.eliminar(dtoUsuarioProveedor,todos);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegPersonasAction, M:obtenerUsuarios");
		}
		if(res>0)
			return true;
		else
			return false;
	}
	
	
	
	
	
}
