package com.webset.set.seguridad.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.seguridad.dao.SegComponenteDao;
import com.webset.set.seguridad.dto.SegBotonDto;
import com.webset.set.seguridad.dto.SegCatTipoComponenteDto;
import com.webset.set.seguridad.dto.SegComponenteDto;
import com.webset.set.seguridad.exceptions.BusinessException;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;

/**
 * Clase que contiene las reglas de negocio de la aplicacion
 * @author Sergio Vaca 
 *
*/
public class SegComponenteBusiness {
	private SegComponenteDao segComponenteDao;
	private Logger logger = Logger.getLogger(SegComponenteBusiness.class);
	private Bitacora bitacora = new Bitacora();
	
	//private static int FACULTAD_DESHABILITAR = 1;
	private static int FACULTAD_HABILITAR = 2;
	//private static int FACULTAD_OCULTAR = 3;
	//private static int FACULTAD_MOSTRAR = 4;
	
	private static int COMPONENTE_MODULO = 1;
	//private static int COMPONENTE_MENU = 2;
	//private static int COMPONENTE_FORMA = 3;
	
	/**
	 * Busca en la tabla seg_componente los datos segun los
	 *	parametros recibidos
	 */
	public List<SegComponenteDto> consultar(SegComponenteDto comp) throws Exception, BusinessException{
		return segComponenteDao.consultar(comp);
	}
	/**
	 * 
	 * @param tipoComp
	 * @return Lista tipo SegCatTipoComponenteDto 
	 * @throws Exception
	 * @throws BusinessException
	 */
	public List<SegCatTipoComponenteDto> consultarClaveTipo(SegCatTipoComponenteDto tipoComp) throws Exception, BusinessException{
		return segComponenteDao.consultarTipoComponente(tipoComp);
	}
	
	/**
	 * Modifica en la tabla seg_componente identificado 
	 * por el id_facultad 
	 * y actualiza la informacion en la BD 
	 */
	
	public int modificar(SegComponenteDto comp) throws Exception, BusinessException{
			return segComponenteDao.modificar(comp);
	}
	
	/**
	 * Elimina de la tabla seg_componente 
	 * el componente identificado por el id_componente 
	 */
	public int eliminar(int idComp) throws Exception, BusinessException{
		return segComponenteDao.eliminar(idComp);
	}
	/**
	 * Se inserta en la tabla seg_componente 
	 * un nuevo componente
	 */
	
	public int agregar(SegComponenteDto comp) throws Exception, BusinessException{
		return segComponenteDao.insertar(comp);
	}
	/**
	 * Llenar el combo de busqueda
	 * @param id
	 * @return
	 */
	public List<SegComponenteDto> llenarCombo(int id){
		return segComponenteDao.llenarCombo(id);
	}

	/**
	 * Obtener una lista de modulos para mostrar el menu
	 * @param usuarioLogin
	 * @return
	 */
	public List<SegComponenteDto> obtenerModulos(String usuarioLogin){
		return segComponenteDao.obtenerComponentesUsuario(usuarioLogin, COMPONENTE_MODULO, FACULTAD_HABILITAR);
	}
	
	/**
	 * Obtener un arbol de menus y submenus en un string Json
	 * @param usuarioLogin
	 * @return
	 */
	public String obtenerArbolSubMenus(String usuarioLogin, int nModulo){
		String sArbol = segComponenteDao.obtenerArbolSubMenus(usuarioLogin, nModulo, null, "").trim();
		
		if(sArbol != null && !sArbol.equals(""))
			sArbol = "{nModulo: "+nModulo+", treeMenu:"+sArbol+"}"; 
		return sArbol;
	}

	public String obtenerArbolComponentes(int noPerfil){
		int nPadre = 0;
		String sArbol = segComponenteDao.obtenerArbolComponentes(nPadre, null, "", noPerfil).trim();
		
		if(sArbol != null && !sArbol.equals(""))
			sArbol = "{nModulo: "+nPadre+", treeMenu:"+sArbol+"}";
		return sArbol;
	}
	
	/**
	 * Actualiza los Componentes de un Perfil.
	 * @param idPerfil
	 * @param sComponentes
	 * @return
	 */
	public boolean actualizarComponentesPerfil(int idPerfil, String sComponentes) {
		logger.debug("Entra: actualizarComponentesPerfil -> ");
		boolean result = true;
		
		result = segComponenteDao.actualizarComponentesPerfil(idPerfil, sComponentes);
		
		logger.debug("Sale: actualizarComponentesPerfil <- ");
		return result;
	}
	
	//getters && setters
	public SegComponenteDao getSegComponenteDao() {
		return segComponenteDao;
	}
	public void setSegComponenteDao(SegComponenteDao segComponenteDao) {
		this.segComponenteDao = segComponenteDao;
	}
	public List<SegComponenteDto> llenaComponentes(int tipoComponente) {
		return segComponenteDao.llenaComponentes(tipoComponente);
	}
	public Map<String, Object> guardarModulo(Map<String, String> datos) {
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try {
			SegComponenteDto segComponenteDto = new SegComponenteDto();
			segComponenteDto.setDescripcion(datos.get("descripcion"));
			segComponenteDto.setEstatus(datos.get("estatus"));
			segComponenteDto.setEtiqueta(datos.get("etiqueta"));
			segComponenteDto.setRutaImagen(datos.get("imagen"));
			int r = segComponenteDao.guardarModulo(segComponenteDto);
			if (r > 0) {
				resultado.put("estatus", true);
				resultado.put("msg", "M�dulo guardado con exito.");
			} else if (r == 0) {
				resultado.put("error", "El m�dulo no fue guardado.");
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteBusiness, M:guardarModulo");
		}
		return resultado;
	}
	public Map<String, Object> eliminarModulo(String id) {
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try {
			if (!segComponenteDao.tieneHijos(id)) {
				int r = segComponenteDao.eliminarModulo(id);
				if (r > 0) {
					resultado.put("estatus", true);
					resultado.put("msg", "M�dulo eliminado con exito.");
				} else if (r == 0) {
					resultado.put("error", "El m�dulo no fue eliminado.");
				}
			} else {
				resultado.put("error", "El m�dulo tiene sub-menus.");
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteBusiness, M:eliminarModulo");
		}
		return resultado;
	}
	public Map<String, Object> modificarModulo(Map<String, String> datos) {
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try {
			SegComponenteDto segComponenteDto = new SegComponenteDto();
			segComponenteDto.setDescripcion(datos.get("descripcion"));
			segComponenteDto.setEstatus(datos.get("estatus"));
			segComponenteDto.setEtiqueta(datos.get("etiqueta"));
			segComponenteDto.setRutaImagen(datos.get("imagen"));
			segComponenteDto.setIdComponente(Integer.parseInt(datos.get("idComponente")));
			int r = segComponenteDao.modificarModulo(segComponenteDto);
			if (r > 0) {
				resultado.put("estatus", true);
				resultado.put("msg", "M�dulo modificado con exito.");
			} else if (r == 0) {
				resultado.put("error", "El m�dulo no fue modificado.");
			}
		} catch (NumberFormatException e) {
			resultado.put("error", "Error en el id del m�dulo.");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteBusiness, M:modificarModulo");
		}catch (Exception e) {		
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteBusiness, M:modificarModulo");
		}
		return resultado;
	}
	public String obtenerArbolModulo(int idModulo) {
		String sArbol = segComponenteDao.obtenerArbolModulo(idModulo, null, "").trim();
		
		if(sArbol != null && !sArbol.equals(""))
			sArbol = "{nModulo: "+idModulo+", treeMenu:"+sArbol+"}";
		return sArbol;
	}
	public List<SegComponenteDto> obtenerComponentesPadre(int idComponente) {
		return segComponenteDao.obtenerComponentesPadre(idComponente);
	}
	public Map<String, Object> guardarComponente(Map<String, String> datos) {
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try {
			SegComponenteDto segComponenteDto = new SegComponenteDto();
			segComponenteDto.setDescripcion(datos.get("descripcion"));
			segComponenteDto.setEstatus(datos.get("estatus"));
			segComponenteDto.setEtiqueta(datos.get("etiqueta"));
			segComponenteDto.setURL(datos.get("url"));
			segComponenteDto.setIdComponentePadre(
					Integer.parseInt(datos.get("idComponentePadre")));
			int r = segComponenteDao.guardarComponente(segComponenteDto);
			if (r > 0) {
				resultado.put("estatus", true);
				resultado.put("msg", "Componente guardado con exito.");
			} else if (r == 0) {
				resultado.put("error", "El componente no fue guardado.");
			}
		} catch (NumberFormatException e) {
			resultado.put("error", "El componente padre no es valido.");
			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteBusiness, M:guardarComponente");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteBusiness, M:guardarComponente");
		}
		return resultado;
	}
	public Map<String, Object> obtenerComponente(int idComponente) {
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try {
			return segComponenteDao.obtenerComponente(idComponente);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteBusiness, M:obtenerComponente");
		}
		return resultado;
	}
	public Map<String, Object> modificarComponente(Map<String, String> datos) {
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try {
			SegComponenteDto segComponenteDto = new SegComponenteDto();
			segComponenteDto.setDescripcion(datos.get("descripcion"));
			segComponenteDto.setEstatus(datos.get("estatus"));
			segComponenteDto.setEtiqueta(datos.get("etiqueta"));
			segComponenteDto.setURL(datos.get("url"));
			segComponenteDto.setIdComponentePadre(
					Integer.parseInt(datos.get("idComponentePadre")));
			segComponenteDto.setIdComponente(
					Integer.parseInt(datos.get("idComponente")));
			int r = segComponenteDao.modificarComponente(segComponenteDto);
			if (r > 0) {
				resultado.put("estatus", true);
				resultado.put("msg", "Componente modificado con exito.");
			} else if (r == 0) {
				resultado.put("error", "El componente no fue modificado.");
			}
		} catch (NumberFormatException e) {
			resultado.put("error", "Error en el id del componente.");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteBusiness, M:modificarComponente");
		}catch (Exception e) {		
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteBusiness, M:modificarComponente");
		}
		return resultado;
	}
	public List<SegBotonDto> llenaBotones(int idModulo) {
		return segComponenteDao.llenaBotones(idModulo);
	}
	public List<SegComponenteDto> obtenerPantallas(int idModulo) {
		return segComponenteDao.obtenerPantallas(idModulo);
	}
	public Map<String, Object> guardarBoton(Map<String, String> datos) {
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try {
			SegBotonDto segComponenteDto = new SegBotonDto();
			segComponenteDto.setDescripcion(datos.get("descripcion"));
			segComponenteDto.setEstatus(datos.get("estatus"));
			segComponenteDto.setIdComponente(
					Integer.parseInt(
							datos.get("idPantalla")));
			segComponenteDto.setIdModulo(
					Integer.parseInt(
							datos.get("idModulo")));
			segComponenteDto.setIdBoton(datos.get("idBoton"));
			if (!segComponenteDao.existeBoton(segComponenteDto.getIdBoton())) {
				int r = segComponenteDao.guardarBoton(segComponenteDto);
				if (r > 0) {
					resultado.put("estatus", true);
					resultado.put("msg", "Bot�n guardado con exito.");
				} else if (r == 0) {
					resultado.put("error", "El bot�n no fue guardado.");
				}
			} else {
				resultado.put("error", "El bot�n ya existe.");
			}
		} catch (NumberFormatException e) {
			resultado.put("error", "Error en el id.");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteBusiness, M:guardarBoton");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteBusiness, M:guardarBoton");
		}
		return resultado;
	}
	public Map<String, Object> modificarBoton(Map<String, String> datos) {
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try {
			SegBotonDto segComponenteDto = new SegBotonDto();
			segComponenteDto.setDescripcion(datos.get("descripcion"));
			segComponenteDto.setEstatus(datos.get("estatus"));
			segComponenteDto.setIdComponente(
					Integer.parseInt(
							datos.get("idPantalla")));
			segComponenteDto.setIdModulo(
					Integer.parseInt(
							datos.get("idModulo")));
			segComponenteDto.setIdBoton(datos.get("idBoton"));
			int r = segComponenteDao.modificarBoton(segComponenteDto);
			if (r > 0) {
				resultado.put("estatus", true);
				resultado.put("msg", "Bot�n modificado con exito.");
			} else if (r == 0) {
				resultado.put("error", "El bot�n no fue modificado.");
			}
		} catch (NumberFormatException e) {
			resultado.put("error", "Error en el id.");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteBusiness, M:modificarBoton");
		}catch (Exception e) {		
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteBusiness, M:modificarBoton");
		}
		return resultado;
	}
	public Map<String, Object> eliminarBoton(String id) {
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try {
			if (!segComponenteDao.estaAsignadoElBoton(id)) {
				int r = segComponenteDao.eliminarBoton(id);
				if (r > 0) {
					resultado.put("estatus", true);
					resultado.put("msg", "Bot�n eliminado con exito.");
				} else if (r == 0) {
					resultado.put("error", "El bot�n no fue eliminado.");
				}
			} else {
				resultado.put("error", "El bot�n tiene sub-menus.");
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteBusiness, M:eliminarBoton");
		}
		return resultado;
	}
	
	public List<SegComponenteDto> obtenerBotonesSinAsignar(int idUsuario, int idModulo){
		List<SegComponenteDto> listBotones = new ArrayList<SegComponenteDto>();
		try {
			listBotones = segComponenteDao.obtenerBotonesSinAsignar(idUsuario, idModulo);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteBusiness, M:obtenerBotonesSinAsignar");
		}
		
		return listBotones;
	}
	
	public List<SegComponenteDto> obtenerBotonesAsignados(int idUsuario, int idModulo){
		List<SegComponenteDto> listBotones = new ArrayList<SegComponenteDto>();
		try {
			listBotones = segComponenteDao.obtenerBotonesAsignados(idUsuario, idModulo);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteBusiness, M:obtenerBotonesSinAsignar");
		}
		
		return listBotones;
	}
	
	public String asignarBoton(String idBoton, int idUsuario){
		String mensaje = "";
		int recibeEntero = 0;
		
		try {
			recibeEntero = segComponenteDao.asignarBoton(idBoton, idUsuario);
			if(recibeEntero > 0){
				mensaje = "Boton asignado con exito";
			}else{
				mensaje = "Error en la asignacion";
			}
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteBusiness, M:asignarBoton");
		}
		
		
		return mensaje;
	}
	
	public String desAsignarBoton(String clave, String idUsuario){
		System.out.println("aca");
		String mensaje = "";
		int recibeEntero = 0;
		
		try {
			recibeEntero = segComponenteDao.desAsignarBoton(clave, idUsuario);
			if(recibeEntero > 0){
				mensaje = "Boton desasignado con exito";
			}else{
				mensaje = "Error en la desasignacion";
			}
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteBusiness, M:asignarBoton");
		}
		
		
		return mensaje;
	}
	public String desAsignarTodos(int idUsuario, int idModulo){
		String mensaje = "";
		int recibeEntero = 0;
		
		try {
			recibeEntero = segComponenteDao.desAsignarTodos(idUsuario, idModulo);
			if(recibeEntero > 0){
				mensaje = "Boton asignado con exito";
			}else{
				mensaje = "Error en la asignacion";
			}
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteBusiness, M:desAsignarTodos");
		}
		
		
		return mensaje;
	}
	
	public String asignarTodos(String datos){
		
		String mensaje = "";
		Gson gson = new Gson();
		List<Map<String, String>> datosBoton = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
		try{
			for(int i = 0; i < datosBoton.size(); i++){
				segComponenteDao.asignarBoton(datosBoton.get(i).get("idBoton"),Integer.parseInt(datosBoton.get(i).get("idUsuario")));
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteBusiness, M:asignarTodos");
		}
		
		
		
		return mensaje;
	}
	public String obtenerPermiso(String usuarioLogin) {

        return  segComponenteDao.obtenerPermiso(usuarioLogin, COMPONENTE_MODULO, FACULTAD_HABILITAR);
	   

	}
}
