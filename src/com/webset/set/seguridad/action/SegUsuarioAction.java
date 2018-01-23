/**
 * @author : Jessica Arelly Cruz Cruz
 */
package com.webset.set.seguridad.action;

// UTIL
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectFormPostMethod;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.seguridad.business.SegPerfilBusiness;
import com.webset.set.seguridad.business.SegUsuarioBusiness;
import com.webset.set.seguridad.dto.ComboUsuario;
import com.webset.set.seguridad.dto.GridUsuario;
import com.webset.set.seguridad.dto.SegPerfilDto;
import com.webset.set.seguridad.dto.SegUsuarioDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.dto.Retorno;
import com.webset.utils.tools.Utilerias;

public class SegUsuarioAction {

	private Contexto contexto = new Contexto();
	private Bitacora bitacora = new Bitacora();
	private SegPerfilBusiness segPerfilBusiness;
	
	private static class SubmitResult {
		public boolean success = true;
		public Map<String, String> errors;
		@SuppressWarnings("unused")
		public Map<String, String> debug_formPacket;
	}

	/*Movi esta clase a una publica ComboUsuario.java
	 * Editado por Luis Serrato 04092015
	private static class ComboUsuario {
		public String nombre;
		public String clave;
		public int id_usuario;

		private ComboUsuario(String nombre, String clave, int id_usuario) {
			this.nombre = nombre;
			this.clave = clave;
			this.id_usuario = id_usuario;
		}
	}
	*/
	
	class ComboPerfil {
		public String descripcionPerfil;
		public int idPerfil;

		private ComboPerfil(int idPerfil, String descripcionPerfil) {
			this.descripcionPerfil = descripcionPerfil;
			this.idPerfil = idPerfil;
		}
	}

	/* Movi esta clase a una publica GridUsuario.Java
	 * Editado por luis serrato 04092015
	private static class GridUsuario {
		public int idUsuario;
		public String cveUsuario;
		public String nombreU;
		public String paterno;
		public String materno;
		public String psw;
		public String estatus;
		public int intentos;
		public String mail;
		public String acceso;
		public String vencimiento;
		public String nomCaja;
		public String nomEmpresa;

		private GridUsuario(int idUsuario, String cveUsuario, String nombreU, String paterno,
			String materno, String psw, String estatus, int intentos,
			String mail, String acceso, String vencimiento, String nomCaja, String nomEmpresa) {
			this.idUsuario = idUsuario;
			this.cveUsuario = cveUsuario;
			this.nombreU = nombreU;
			this.paterno = paterno;
			this.materno = materno;
			this.psw = psw;
			this.estatus = estatus;
			this.intentos = intentos;
			this.mail = mail;
			this.acceso = acceso;
			this.vencimiento = vencimiento;
			this.nomCaja = nomCaja;
			this.nomEmpresa = nomEmpresa;
		}
	}
	*/

	/**
	 * metodo para llenar los combos de busqueda
	 * Editado por Luis Serrato 04092015
	 * */
	@DirectMethod
	public List<ComboUsuario> obtenerUsuarios() {
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 29))
			return null;
		List<ComboUsuario> usuarios = new ArrayList<ComboUsuario>();
		
		try{
			SegUsuarioBusiness segUsuarioBusiness = (SegUsuarioBusiness)contexto.obtenerBean("segUsuarioBusiness");
			usuarios = segUsuarioBusiness.consultar();
	
		}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Seguridad, C:SegPersonasAction, M:obtenerUsuarios");
		}
		return usuarios;
		

	}

	/**
	 * metodo para llenar el grid de usuarios
	 * Editado por Luis Serrato 04092015
	 * */
	@DirectMethod
	public List<GridUsuario> obtenerUsuariosGrid(int idUsuario, String estatus) {
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 29))
			return null;
		SegUsuarioBusiness segUsuarioBusiness = (SegUsuarioBusiness)contexto.obtenerBean("segUsuarioBusiness");
		
		List<GridUsuario> gridUsuarios = new ArrayList<GridUsuario>();
		
		
		try {
				gridUsuarios = segUsuarioBusiness.consultarGrid(idUsuario, estatus);
		} 
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seguridad, C:SegUsuarioAction, M:obtenerUsuariosGrid");
		}

		return gridUsuarios;
		

	}
	
	/**
	 * metodo para llenar los combos de busqueda
	 * 
	 * */
	@DirectMethod
	public List<ComboPerfil> obtenerPerfil() {
		segPerfilBusiness = (SegPerfilBusiness) contexto.obtenerBean("segPerfilBusiness");
		
		List<SegPerfilDto> perfiles = new ArrayList<SegPerfilDto>();
		SegPerfilDto perfil = new SegPerfilDto();
		List<ComboPerfil> comboPerfiles = new ArrayList<ComboPerfil>();

		try {
			perfiles = segPerfilBusiness.consultar(perfil);
			ListIterator<SegPerfilDto> itr = perfiles.listIterator();

			while (itr.hasNext()) {
				SegPerfilDto perfilTmp = itr.next();
				// FALTA REDISE�AR PERFILES
				comboPerfiles.add(new ComboPerfil(perfilTmp.getIdPerfil(),perfilTmp.getDescripcion()));
			}

		} 

		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegUsuarioAction, M:obtenerPerfil");
			}
		return comboPerfiles;

		

	}

	

	/**
	 * metodo para insertar un usuario
	 * */

	/**
	 * metodo para insertar un usuario
	 * */
	@DirectFormPostMethod
	public SubmitResult insertarModificar(Map<String, String> formParameters,	Map<String, FileItem> fileFields) {
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 29))
			return null;
		assert formParameters != null;
		assert fileFields != null;

		SubmitResult result = new SubmitResult();
		SegUsuarioDto usuario = new SegUsuarioDto();
		try {
			SegUsuarioBusiness segUsuarioBusiness = (SegUsuarioBusiness)contexto.obtenerBean("segUsuarioBusiness");
			
			boolean bandera = formParameters.get("banderaF").equals("true") ? true : false;
			String pf = formParameters.get("prefijo");
			usuario.setIdUsuario(Integer.parseInt(formParameters.get("idUsuarioF")));
			usuario.setClaveUsuario(formParameters.get(pf+"clave"));
			usuario.setNombre(formParameters.get(pf+"nombre"));
			usuario.setApellidoPaterno(formParameters.get(pf+"apPaterno"));
			usuario.setApellidoMaterno(formParameters.get(pf+"apMaterno"));
			usuario.setContrasena(formParameters.get(pf+"contrasena"));
			usuario.setEstatus(formParameters.get(pf+"cmbEstatusNuevo"));
			usuario.setCorreoElectronico(formParameters.get(pf+"correoElectronico"));
			usuario.setFechaVencimiento(formParameters.get(pf+"fecVencimiento"));
			usuario.setNoEmpresa(segUsuarioBusiness.buscaEmpresa(formParameters.get(pf+"cmbEmpresa")));
			usuario.setIdCaja(segUsuarioBusiness.buscaCaja(formParameters.get(pf+"cmbCaja")));
			String intentos=formParameters.get(pf+"chkBloqueo")+"";
			if(intentos.equals("on")){
				usuario.setIntentos(3);
			}else{
				usuario.setIntentos(0);
			}
			
			// OJO: FALTA REDISE�AR LA PARTE DE MULTIPLES PERFILES DE UN USUARIO DE ESTA PANTALLA.
			//usuario.setIdPerfil(Integer.parseInt(formParameters.get(pf+"idPerfil")));
			//usuario.setClavePerfil(formParameters.get(pf+"cmbClavePerfil"));
			Gson gson = new Gson();
			
	
			// Generar un mapa del objecto json
			List<Map<String, String>> perfilesMapLst = gson.fromJson(formParameters.get("listaPerfiles"), new TypeToken<ArrayList<Map<String, String>>>() {}.getType());  // para una lista de hashmaps
	
			if(!bandera) {
				result.success = segUsuarioBusiness.agregar(usuario, perfilesMapLst) > 0;
			}else{
				result.success = segUsuarioBusiness.modificar(usuario, perfilesMapLst)>0;
			}

			if (!result.success) {
				result.errors = new HashMap<String, String>();
				result.errors.put("Error",	"No se pudieron insertar los datos");
			}

			result.debug_formPacket = formParameters;
		} catch(Exception e){
			System.out.println("action 1 intentos");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seguridad, C:SegUsuarioAction, M:insertarModificar");
		}  

		return result;
	}
	
	/**
	 * metodo para eliminar un usuario
	 * */
	@DirectMethod
	public Retorno eliminar(int idU) {
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 29))
			return null;
	 Retorno retorno = new Retorno();
		SegUsuarioBusiness segUsuarioBusiness = (SegUsuarioBusiness) contexto.obtenerBean("segUsuarioBusiness");
		try {
			retorno.setResultado(segUsuarioBusiness.eliminar(idU)>0);
			retorno.setId(idU);

		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seguridad, C:SegUsuarioAction, M:eliminar");
					}

		return retorno;
	}


}