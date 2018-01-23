package com.webset.set.seguridad.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectFormPostMethod;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.seguridad.business.SegComponenteBusiness;
import com.webset.set.seguridad.business.SegPolizasBusiness;
import com.webset.set.seguridad.dto.ComboUsuario;
import com.webset.set.seguridad.dto.SegBotonDto;
import com.webset.set.seguridad.dto.SegCatTipoComponenteDto;
import com.webset.set.seguridad.dto.SegComponenteDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.utils.tools.Utilerias;

/**
 * 
 * @author Cristian García García
 * @since 02/12/2010
 *
 */
public class SegComponentesAction {
	Contexto contexto=new Contexto();
	Bitacora bitacora= new Bitacora();
	private SegComponenteBusiness segComponenteBusiness;
	boolean retorno=false;
	
	
	private static class SubmitResult {
		public boolean success = true;
		public Map<String, String> errors;
	}
	
	@DirectMethod
	public List<SegComponenteDto> obtenerComponentes()
	{
		List<SegComponenteDto> comp=null;
		SegComponenteBusiness componenteBusiness=(SegComponenteBusiness) contexto.obtenerBean("segComponenteBusiness");
		SegComponenteDto dtoComponentes=new SegComponenteDto();
		dtoComponentes=null;
		try{
			comp=componenteBusiness.consultar(dtoComponentes);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegcomponentesAction, M:obtenerComponentes");
		}
			
		return comp;
	}
	
	@DirectMethod
	public List<ComboUsuario> obtenerUsuarios() {
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 187))
			return null;
		
		List<ComboUsuario> usuarios = new ArrayList<ComboUsuario>();
		
		try{
			
			SegPolizasBusiness segPolizasBusiness = (SegPolizasBusiness)contexto.obtenerBean("segPolizasBusinnes");
			usuarios = segPolizasBusiness.obtenerUsuarios();
			
		}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Seguridad, C:SegPolizasAction, M:obtenerUsuarios");
		}
		return usuarios;
	}
	
	@DirectMethod
	public List<SegComponenteDto> llenaComponentes(int tipoComponente){
		if(!Utilerias.haveSession(WebContextManager.get())
				|| (!Utilerias.tienePermiso(WebContextManager.get(), 184)&&
				!Utilerias.tienePermiso(WebContextManager.get(), 183)&&
				!Utilerias.tienePermiso(WebContextManager.get(), 187)&&
				!Utilerias.tienePermiso(WebContextManager.get(), 186)))
			return null;
		
		List<SegComponenteDto> comp=new ArrayList<SegComponenteDto>();
		SegComponenteBusiness componenteBusiness=(SegComponenteBusiness) contexto.obtenerBean("segComponenteBusiness");
		try{
			comp=componenteBusiness.llenaComponentes(tipoComponente);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegcomponentesAction, M:llenaComponentes");
		}
			
		return comp;
	}
	
	@DirectMethod
	public List<SegBotonDto> llenaBotones(int idModulo){
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 186))
			return null;
		List<SegBotonDto> comp=new ArrayList<SegBotonDto>();
		SegComponenteBusiness componenteBusiness=(SegComponenteBusiness) contexto.obtenerBean("segComponenteBusiness");
		try{
			comp=componenteBusiness.llenaBotones(idModulo);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegcomponentesAction, M:llenaBotones");
		}
			
		return comp;
	}
	
	@DirectMethod
	public List<SegComponenteDto> obtenerComponentesPadre(int idComponente){
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 183))
			return null;
		
		List<SegComponenteDto> comp=new ArrayList<SegComponenteDto>();
		SegComponenteBusiness componenteBusiness=(SegComponenteBusiness) contexto.obtenerBean("segComponenteBusiness");
		try{
			comp=componenteBusiness.obtenerComponentesPadre(idComponente);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegcomponentesAction, M:obtenerComponentesPadre");
		}			
		return comp;
	}
	
	@DirectMethod
	public List<SegComponenteDto> obtenerPantallas(int idModulo){
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 186))
			return null;
		List<SegComponenteDto> comp=new ArrayList<SegComponenteDto>();
		SegComponenteBusiness componenteBusiness=(SegComponenteBusiness) contexto.obtenerBean("segComponenteBusiness");
		try{
			comp=componenteBusiness.obtenerPantallas(idModulo);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegcomponentesAction, M:obtenerPantallas");
		}			
		return comp;
	}
	
	@DirectMethod
	public Map<String, Object> eliminarModulo(String id) {
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 184))
			return null;
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try{
			SegComponenteBusiness componenteBusiness=(SegComponenteBusiness) contexto.obtenerBean("segComponenteBusiness");
			resultado = componenteBusiness.eliminarModulo(id);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegcomponentesAction, M:eliminarModulo");
		}
		return resultado;
	}
	
	@DirectMethod
	public Map<String, Object> eliminarBoton(String id) {
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 186))
			return null;
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try{
			SegComponenteBusiness componenteBusiness=(SegComponenteBusiness) contexto.obtenerBean("segComponenteBusiness");
			resultado = componenteBusiness.eliminarBoton(id);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegcomponentesAction, M:eliminarBoton");
		}
		return resultado;
	}
	
	@DirectMethod
	public Map<String, Object> eliminarComponente(String id) {

		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 183))
			return null;
		
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try{
			SegComponenteBusiness componenteBusiness=(SegComponenteBusiness) contexto.obtenerBean("segComponenteBusiness");
			resultado = componenteBusiness.eliminarModulo(id);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegcomponentesAction, M:eliminarComponente");
		}
		return resultado;
	}
	@DirectMethod
	public Map<String, Object> guardarModulo(String jsDatos) {
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 184))
			return null;
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try{
			SegComponenteBusiness componenteBusiness=(SegComponenteBusiness) contexto.obtenerBean("segComponenteBusiness");
			Gson gson = new Gson();
			List<Map<String, String>> datos = gson.fromJson(jsDatos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			resultado = componenteBusiness.guardarModulo(datos.get(0));
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegcomponentesAction, M:guardarModulo");
		}
		return resultado;
	}
	
	@DirectMethod
	public Map<String, Object> guardarBoton(String jsDatos) {
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 186))
			return null;
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try{
			SegComponenteBusiness componenteBusiness=(SegComponenteBusiness) contexto.obtenerBean("segComponenteBusiness");
			Gson gson = new Gson();
			List<Map<String, String>> datos = gson.fromJson(jsDatos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			resultado = componenteBusiness.guardarBoton(datos.get(0));
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegcomponentesAction, M:guardarBoton");
		}
		return resultado;
	}
	
	@DirectMethod
	public Map<String, Object> guardarComponente(String jsDatos) {
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 183))
			return null;
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try{
			SegComponenteBusiness componenteBusiness=(SegComponenteBusiness) contexto.obtenerBean("segComponenteBusiness");
			Gson gson = new Gson();
			List<Map<String, String>> datos = gson.fromJson(jsDatos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			resultado = componenteBusiness.guardarComponente(datos.get(0));
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegcomponentesAction, M:guardarComponente");
		}
		return resultado;
	}
	
	@DirectMethod
	public Map<String, Object> modificarComponente(String jsDatos) {
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 183))
			return null;
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try{
			SegComponenteBusiness componenteBusiness=(SegComponenteBusiness) contexto.obtenerBean("segComponenteBusiness");
			Gson gson = new Gson();
			List<Map<String, String>> datos = gson.fromJson(jsDatos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			resultado = componenteBusiness.modificarComponente(datos.get(0));
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegcomponentesAction, M:modificarComponente");
		}
		return resultado;
	}
	
	
	
	@DirectMethod
	public Map<String, Object> obtenerComponente(int idComponente) {
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 183))
			return null;
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try{
			SegComponenteBusiness componenteBusiness=(SegComponenteBusiness) contexto.obtenerBean("segComponenteBusiness");
		
			resultado = componenteBusiness.obtenerComponente(idComponente);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegcomponentesAction, M:obtenerComponente");
		}
		return resultado;
	}
	
	@DirectMethod
	public Map<String, Object> modificarModulo(String jsDatos) {
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 184))
			return null;
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try{
			SegComponenteBusiness componenteBusiness=(SegComponenteBusiness) contexto.obtenerBean("segComponenteBusiness");
			Gson gson = new Gson();
			List<Map<String, String>> datos = gson.fromJson(jsDatos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			resultado = componenteBusiness.modificarModulo(datos.get(0));
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegcomponentesAction, M:modificarModulo");
		}
		return resultado;
	}
	
	@DirectMethod
	public Map<String, Object> modificarBoton(String jsDatos) {
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 186))
			return null;
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try{
			SegComponenteBusiness componenteBusiness=(SegComponenteBusiness) contexto.obtenerBean("segComponenteBusiness");
			Gson gson = new Gson();
			List<Map<String, String>> datos = gson.fromJson(jsDatos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			resultado = componenteBusiness.modificarBoton(datos.get(0));
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegcomponentesAction, M:modificarBoton");
		}
		return resultado;
	}
	
	@DirectMethod
	public List<SegCatTipoComponenteDto> obtenerClaveTipoComponente()
	{
		List<SegCatTipoComponenteDto>claveTipoComponente=null;
		SegComponenteBusiness componenteBusiness=(SegComponenteBusiness) contexto.obtenerBean("segComponenteBusiness");
		SegCatTipoComponenteDto dtoSegTipo=new SegCatTipoComponenteDto();
		dtoSegTipo=null;
		try{
			claveTipoComponente=componenteBusiness.consultarClaveTipo(dtoSegTipo);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegcomponentesAction, M:obtenerClaveTipoComponente");
		}
		return claveTipoComponente;		
	}
	@DirectMethod
	public List<SegComponenteDto> consultarComponentes(String idBusqueda,String tipo)
	{
		 
		List<SegComponenteDto> componentesConsulta=null;
		SegComponenteBusiness componenteBusiness=(SegComponenteBusiness) contexto.obtenerBean("segComponenteBusiness");
		SegComponenteDto dtoComponentes=new SegComponenteDto();
		
		
		if(tipo.equals("claveComponente"))
		{
			dtoComponentes.setIdComponente(Integer.parseInt(idBusqueda));
			dtoComponentes.setIdTipoComponente(0);
		}
		else if(tipo.equals("tipoComponente"))
		{
			dtoComponentes.setIdTipoComponente(Integer.parseInt(idBusqueda));
			dtoComponentes.setIdComponente(0);
		}
		else if(tipo.equals("estatus"))
		{
			dtoComponentes.setEstatus(idBusqueda);
			dtoComponentes.setIdTipoComponente(0);	
			dtoComponentes.setIdComponente(0);
		}
		try{
			componentesConsulta=componenteBusiness.consultar(dtoComponentes);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegcomponentesAction, M:consultarComponentes");
		}
		
		tipo="";
		idBusqueda="";
		return componentesConsulta;
		
	}
	
	//Metodo para Modificar e Insertar
		@DirectFormPostMethod
	public SubmitResult modificar( Map<String, String> formParameters, Map<String, FileItem> fileFields ) {

		assert formParameters != null;
		assert fileFields != null;
		SubmitResult result =new SubmitResult();
		SegComponenteDto dtoComponente=new SegComponenteDto();
		String pf=formParameters.get("prefijo");
		
		int idComponente1=Integer.parseInt(formParameters.get("idComponente"));
		int idTipoComponente1=Integer.parseInt(formParameters.get("idTipoComponente"));
		int idComponentePadre1=Integer.parseInt(formParameters.get("idComponentePadre"));
		String descripcion = formParameters.get(pf+"descripcion");
		String est = formParameters.get(pf+"estatusDetalle");
		String estatusDetalle=est.substring(0,1);
		String claveComponente = formParameters.get(pf+"claveComponente");
		String rutaImagen = formParameters.get(pf+"rutaImagen");
		String claveTipoComponentePadre = formParameters.get(pf+"claveTipoComponentePadre");
		
		String accion=formParameters.get("accion");

		dtoComponente.setIdComponente(idComponente1);
		dtoComponente.setIdTipoComponente(idTipoComponente1);
		dtoComponente.setIdComponentePadre(idComponentePadre1);
		dtoComponente.setDescripcion(descripcion);
		dtoComponente.setEstatus(estatusDetalle);
		dtoComponente.setClaveComponente(claveComponente);
		dtoComponente.setRutaImagen(rutaImagen);
		dtoComponente.setClaveComponentePadre(claveTipoComponentePadre);
		SegComponenteBusiness segComponenteBusiness = (SegComponenteBusiness)contexto.obtenerBean("segComponenteBusiness");
		
		try{
			
			if(accion.equals("N"))
				result.success = segComponenteBusiness.agregar(dtoComponente)>0;
			else if(accion.equals("M"))
				result.success = segComponenteBusiness.modificar(dtoComponente)>0;
			else if(accion.equals("E"))
				result.success=segComponenteBusiness.eliminar(idComponente1)>0;
			
			if(!result.success) {
	
			result.	errors = new HashMap<String,String>();
	
			result.	errors.put( "Error", "El registro no se pudo modificar");
	
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}

		return result;

	}
		
		//Metodo para Eliminar
		@DirectMethod
		public boolean eliminar(int idComponente) {
			SegComponenteBusiness segComponenteBusiness = (SegComponenteBusiness)contexto.obtenerBean("segComponenteBusiness");
			try{
				int res=segComponenteBusiness.eliminar(idComponente);
				if(res>0)
					retorno=true;
				else
					retorno=false;
			}
			catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Seguridad, C:SegcomponentesAction, M:consultarComponentes");
			}
			
			
			return retorno;

		}
		
		@DirectMethod 
		public String obtenerArbolComponentes(int noPerfil) {
			if(!Utilerias.haveSession(WebContextManager.get())
					|| !Utilerias.tienePermiso(WebContextManager.get(), 27))
				return null;
			String arbolComponentes = null;
			
			try{
				segComponenteBusiness = (SegComponenteBusiness)contexto.obtenerBean("segComponenteBusiness");
				arbolComponentes = segComponenteBusiness.obtenerArbolComponentes(noPerfil);
			}catch(Exception e){ 
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +" - " + e.getStackTrace()
						+ "P:Seg, C:SegMenuAction, M:listaModulos");
			}
			return arbolComponentes; 
		}
		
		@DirectMethod 
		public String obtenerArbolModulo(int idModulo) {
			if(!Utilerias.haveSession(WebContextManager.get())
					|| !Utilerias.tienePermiso(WebContextManager.get(), 183))
				return null;
			String arbolComponentes = null;			
			try{
				segComponenteBusiness = (SegComponenteBusiness)contexto.obtenerBean("segComponenteBusiness");
				arbolComponentes = segComponenteBusiness.obtenerArbolModulo(idModulo);
			}catch(Exception e){ 
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +" - " + e.getStackTrace()
						+ "P:Seguridad, C:SegComponentesAction, M:obtenerArbolModulo");
			}
			return arbolComponentes; 
		}
		
		
		
	/**
	 * Metodo para actualizar los componentes asociados a un perfil.
	 * @param idPerfil
	 * @param sComponentes
	 * @return
	 */
	@DirectMethod
	public boolean actualizarComponentesPerfil(int idPerfil, String sComponentes)
	{
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 27))
			return false;
		SegComponenteBusiness segComponenteBusiness = (SegComponenteBusiness)contexto.obtenerBean("segComponenteBusiness");
		
		try
		{
			retorno = segComponenteBusiness.actualizarComponentesPerfil(idPerfil, sComponentes);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ComponentesPerfil, C:SegComponentesAction, M:actualizarComponentesPerfil");
		}
		return retorno;
	}
	
	@DirectMethod
	public List<SegComponenteDto> obtenerBotonesSinAsignar(int idUsuario, int idModulo){
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 187))
			return null;
		List<SegComponenteDto> listBotones = new ArrayList<SegComponenteDto>();
		try {
			SegComponenteBusiness segComponenteBusiness = (SegComponenteBusiness)contexto.obtenerBean("segComponenteBusiness");
			listBotones = segComponenteBusiness.obtenerBotonesSinAsignar(idUsuario, idModulo);
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:ComponentesPerfil, C:SegComponentesAction, M:obtenerBotonesSinAsignar");
		}
		
		return listBotones;
	}
	
	@DirectMethod
	public List<SegComponenteDto> obtenerBotonesAsignados(int idUsuario, int idModulo){
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 187))
			return null;
		List<SegComponenteDto> listBotones = new ArrayList<SegComponenteDto>();
		try {
			SegComponenteBusiness segComponenteBusiness = (SegComponenteBusiness)contexto.obtenerBean("segComponenteBusiness");
			listBotones = segComponenteBusiness.obtenerBotonesAsignados(idUsuario, idModulo);
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:ComponentesPerfil, C:SegComponentesAction, M:obtenerBotonesAsignados");
		}
		
		return listBotones;
	}
	
	
	@DirectMethod
	public String asignarBoton(String idBoton, int idUsuario){
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 187))
			return null;
		String mensaje = "";
		
		try {
			SegComponenteBusiness segComponenteBusiness = (SegComponenteBusiness)contexto.obtenerBean("segComponenteBusiness");
			mensaje = segComponenteBusiness.asignarBoton(idBoton, idUsuario);
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:ComponentesPerfil, C:SegComponentesAction, M:obtenerBotonesSinAsignar");
		}
		
		return mensaje;
	}
	
	@DirectMethod
	public String desAsignarBoton(String clave, String idUsuario){
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 187))
			return null;
		System.out.println("aqui");
		String mensaje = "";
		
		try {
			SegComponenteBusiness segComponenteBusiness = (SegComponenteBusiness)contexto.obtenerBean("segComponenteBusiness");
			mensaje = segComponenteBusiness.desAsignarBoton(clave, idUsuario);
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:ComponentesPerfil, C:SegComponentesAction, M:desAsignarBoton");
		}
		
		return mensaje;
	}
	
	@DirectMethod
	public String desAsignarTodos(int idUsuario, int idModulo){
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 187))
			return null;
		String mensaje = "";
		
		try {
			SegComponenteBusiness segComponenteBusiness = (SegComponenteBusiness)contexto.obtenerBean("segComponenteBusiness");
			mensaje = segComponenteBusiness.desAsignarTodos(idUsuario, idModulo);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:ComponentesPerfil, C:SegComponentesAction, M:desAsignarTodos");
		}
		
		return mensaje;
	}
	
	@DirectMethod
	public String asignarTodos(String datos){
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 187))
			return null;
		String mensaje = "";
		
		try {
			SegComponenteBusiness segComponenteBusiness = (SegComponenteBusiness)contexto.obtenerBean("segComponenteBusiness");
			mensaje = segComponenteBusiness.asignarTodos(datos);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:ComponentesPerfil, C:SegComponentesAction, M:asignarTodos");
		}
		
		return mensaje;
	}
	
}
