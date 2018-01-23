package com.webset.set.seguridad.business;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.webset.set.seguridad.dao.SegUsuarioDao;
import com.webset.set.seguridad.dto.ComboUsuario;
import com.webset.set.seguridad.dto.GridUsuario;
import com.webset.set.seguridad.dto.SegUsuarioDto;
import com.webset.set.seguridad.exceptions.BusinessException;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;

/**
 * Clase que contiene las reglas de negocio de la aplicacion
 * @author Sergio Vaca 
 *
*/

public class SegUsuarioBusiness {

	private Contexto contexto = new Contexto();
	private SegUsuarioDao segUsuarioDao = new SegUsuarioDao();
	private SegUsuarioPerfilBusiness segUsuarioPerfilBusiness;
	//private GlobalSingleton globalSingleton;
	Bitacora bitacora = new Bitacora();
	private Funciones funciones = new Funciones();
	private static Logger logger = Logger.getLogger(SegUsuarioBusiness.class);
	
	/**
	 * Verifica si la combinacion de usuario y password son validas.
	 * @param login usuario a verificar
	 * @param password contrase\u00f1a del usuario a verificar
	 * @throws BusinessException en caso de un error logico (Usuario o password no valido) 
	 * @throws Exception en caso de un error de programacion
	 */
	public Map<String, Object> validarContrasena(String login, String password) throws Exception, BusinessException{
		Map<String, Object> result = new HashMap<String, Object>();
		SegUsuarioDto usuario = new SegUsuarioDto();		
		
		usuario.setClaveUsuario(login);
		usuario.setContrasena(funciones.encriptador(password));
		
		if (segUsuarioDao.validarUsuario(usuario.getClaveUsuario())) {
			if (segUsuarioDao.validarVigencia(usuario.getClaveUsuario())) {
				if (segUsuarioDao.validarBloqueo(usuario.getClaveUsuario())) {
					if (segUsuarioDao.intentos(usuario.getClaveUsuario()) != 5) {
						result = segUsuarioDao.validarContrasena(usuario);
						if ((Integer)result.get("id_usuario") != 0) {
							segUsuarioDao.cambioAcceso((Integer)result.get("id_usuario"));
							segUsuarioDao.cambioIntento((Integer)result.get("id_usuario"), false);
						}
					} else {
						result.put("id_usuario", 0);
						result.put("msg", "El usuario esta bloqueado por intentos fallidos.");
					}	
				} else {
					result.put("id_usuario", 0);
					result.put("msg", "El usuario esta bloqueado.");
				}		
			} else {
				result.put("id_usuario", 0);
				result.put("msg", "El usuario ya no es vigente.");
			}
		} else {
			result.put("id_usuario", 0);
			result.put("msg", "El usuario no existe.");
		}
		
		
		
		return result;
	}
	
	
	/**
	 * Editado por luis serrato 04092015
	 * Busca en la tabla seg_usuario los datos segun los
	 *	parametros recibidos
	 * @param usuario
	 * @return
	 * @throws Exception
	 * @throws BusinessException
	 */
	public List<ComboUsuario> consultar() throws Exception, BusinessException{		
		List<ComboUsuario> comboUsuarios = segUsuarioDao.consultar();;
		if(comboUsuarios.size() == 0){
			logger.info("No existe el usuario. Verifique");
		}		
		return comboUsuarios;
	}
	
	/*
	 * Creado por Luis Serrato 04092015
	 */
	public List<GridUsuario> consultarGrid(int idUsuario, String estatus) throws Exception, BusinessException{
		SegUsuarioDto usuario = new SegUsuarioDto();
		usuario.setIdUsuario(idUsuario);
		usuario.setEstatus(estatus);
		
		List<GridUsuario> usuarios = segUsuarioDao.consultarGrid(usuario); 
		if(usuarios.size() == 0)
			logger.info("No existe el usuario. Verifique");
		return usuarios;
		
	}
	
	/**
	 * Modifica en la tabla seg_usuario identificado 
	 * por el id_tipo_componente 
	 * y actualiza la informacion en la BD
	 * @param usuario
	 * @throws Exception
	 * @throws BusinessException
	 */
	public int modificar(SegUsuarioDto usuario, List<Map<String, String>> perfilesMapLst) throws Exception, BusinessException{
		segUsuarioPerfilBusiness = (SegUsuarioPerfilBusiness)contexto.obtenerBean("segUsuarioPerfilBusiness");
		
		int ret = segUsuarioPerfilBusiness.insertar(usuario.getIdUsuario(), perfilesMapLst);
		
		logger.info("insertarPerfiles ret="+ret);
		
		if(ret>0) {
			ret = segUsuarioDao.modificar(usuario);
			segUsuarioDao.modificarCatUsuarios(usuario);
		}
		logger.info("modificar ret="+ret);

		return ret;
	}
	
	/**
	 * Elimina de la tabla seg_usuario 
	 * el componente identificado por el id_usuario
	 * @param idUsuario
	 * @throws Exception
	 * @throws BusinessException
	 */
	public int eliminar(int idUsuario) throws Exception, BusinessException{
		int res = 0;
		
		res = segUsuarioDao.eliminar(idUsuario);
		segUsuarioDao.eliminarCatUsuario(idUsuario);
		
		return res;
	}
	/**
	 * Se inserta en la tabla seg_usuario 
	 * un nuevo componente
	 * @param usuario
	 * @throws Exception
	 * @throws BusinessException
	 */	
	public int agregar(SegUsuarioDto usuario, List<Map<String, String>> perfilesMapLst) throws Exception, BusinessException{
		segUsuarioPerfilBusiness = (SegUsuarioPerfilBusiness)contexto.obtenerBean("segUsuarioPerfilBusiness");
		int ret = segUsuarioDao.insertar(usuario);
		
		logger.info("segUsuarioDao.insertar ret="+ret);
		
		if(ret>0) {
			ret = segUsuarioDao.insertarCatUsuario(usuario);
			ret = segUsuarioPerfilBusiness.insertar(usuario.getIdUsuario(), perfilesMapLst);
		}
		logger.info("insertarPerfiles ret="+ret);
		
		return ret;
	}
	
	public List<SegUsuarioDto>llenarComboNombreBusqueda(int id){
		return segUsuarioDao.llenarComboNombreBusqueda(id);
	}
	
	
	public List<SegUsuarioDto>llenarComboClaveBusqueda(int id){
		return segUsuarioDao.llenarComboClaveBusqueda(id);
	}
	/**
	 * 
	 * @param idUsuario
	 * @param edo false para regresar a cero intentos y true se le suma 1 al numero de intentos
	 */
	public int actualizaIntentos(int idUsuario, boolean edo){
		return segUsuarioDao.cambioIntento(idUsuario, edo);
	}
	
	/**
	 * 
	 * @param idUsuario
	 * @return numero de intentos del usuario
	 */
	public int intentos(int idUsuario){
		return segUsuarioDao.intentos(idUsuario);
	}
	
	/**
	 * actualiza la fecha de acceso cuando el usuario entra al sistema
	 * @param idUsuario
	 */
	public int acceso(int idUsuario){
		return segUsuarioDao.cambioAcceso(idUsuario);
	}
	
	/**
	 * Actuliza la fecha de vencimiento
	 * @param idUsuario
	 * @param fecVen
	 */
	public int vencimineto(int idUsuario, Date fecVen) {
		return segUsuarioDao.cambioVencimiento(idUsuario, fecVen);
	}
	
	public int cambiaContrasena(String cveUsr, String sNewPass) {
		return segUsuarioDao.cambiaContrasena(cveUsr, funciones.encriptador(sNewPass));
	}
	
	public int buscaEmpresa(String nomEmpresa) {
		return segUsuarioDao.buscaEmpresa(nomEmpresa);
	}
	
	public int buscaCaja(String nomCaja) {
		return segUsuarioDao.buscaCaja(nomCaja);
	}
	
	//getters && setters
	public SegUsuarioDao getSegUsuarioDao() {
		return segUsuarioDao;
	}

	public void setSegUsuarioDao(SegUsuarioDao segUsuarioDao) {
		this.segUsuarioDao = segUsuarioDao;
	}
}
