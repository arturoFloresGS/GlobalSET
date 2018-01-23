package com.webset.set.seguridad.business;

import java.util.List;
import java.util.Map;

import com.webset.set.seguridad.dao.SegUsuarioPerfilDao;
import com.webset.set.seguridad.exceptions.BusinessException;

/**
 * Clase que contiene las reglas de negocio de la aplicacion
 * @author Armando Rodriguez 
 *
*/
public class SegUsuarioPerfilBusiness {
	private SegUsuarioPerfilDao segUsuarioPerfilDao;
	
	/**
	 * Busca en la tabla los datos segun los
	 *	parametros recibidos
	 */
	public List<Map<String, Object>> obtenerPerfilesUsuario(int idUsuario) throws Exception, BusinessException{
		return segUsuarioPerfilDao.obtenerPerfilesUsuario(idUsuario);
	}
	
	/**
	 * Elimina de la tabla  
	 * el componente identificado por los paramatros 
	 */
	public int eliminar(int usuario, int perfil) throws Exception, BusinessException{
		return segUsuarioPerfilDao.eliminar(usuario, perfil);
	}
	/**
	 * Se inserta en la tabla  
	 * un nuevo componente
	 */
	
	public int insertar(int usuario, List<Map<String, String>> perfilesMapLst) throws Exception, BusinessException{
		
		return segUsuarioPerfilDao.insertar(usuario, perfilesMapLst);
	}
	/**
	 * Valida si el perfil esta asignado a un usuario 
	 * Esto se deriva del objSeguridad de VB como gobjSeguridad.ValidaFacultad
	 * @param idUsuario
	 * @param idPerfil
	 * @return
	 */
	public boolean validarPerfilUsuario(int idUsuario, int idPerfil){
		return segUsuarioPerfilDao.validarPerfilUsuario(idUsuario, idPerfil);
	}
	
	//getters && setters
	public SegUsuarioPerfilDao getSegUsuarioPerfilDao() {
		return segUsuarioPerfilDao;
	}

	public void setSegUsuarioPerfilDao(SegUsuarioPerfilDao segUsuarioPerfilDao) {
		this.segUsuarioPerfilDao = segUsuarioPerfilDao;
	}
	
}
