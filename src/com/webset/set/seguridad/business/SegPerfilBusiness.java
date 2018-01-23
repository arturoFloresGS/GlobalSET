package com.webset.set.seguridad.business;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.CannotGetJdbcConnectionException;

import com.webset.set.seguridad.dao.SegPerfilDao;
import com.webset.set.seguridad.dto.SegPerfilDto;
import com.webset.set.seguridad.exceptions.BusinessException;
import com.webset.set.utilerias.Bitacora;

/**
 * Clase que contiene las reglas de negocio de la aplicacion
 * @author Sergio Vaca 
 *
*/
public class SegPerfilBusiness {
	private SegPerfilDao segPerfilDao;
	private Bitacora bitacora = new Bitacora();
	
	/**
	 * Busca en la tabla seg_Perfil los datos segun los
	 *	parametros recibidos
	 */
	public List<SegPerfilDto> consultar(SegPerfilDto perfil) throws Exception, BusinessException{
		List<SegPerfilDto> perfiles = segPerfilDao.consultar(perfil);
		return perfiles;
	}
	
	/**
	 * Modifica en la tabla seg_Perfil identificado 
	 * por el id_tipo_componente 
	 * y actualiza la informacion en la BD 
	 */
	
	public int modificar(SegPerfilDto comp) throws Exception, BusinessException{
		return segPerfilDao.modificar(comp);
		
		
	}
	
	/**
	 * Elimina de la tabla seg_Perfil 
	 * el componente identificado por el id_perfil 
	 */
	public int eliminar(int idPerfil) throws Exception, BusinessException{
		return segPerfilDao.eliminar(idPerfil);
	}
	/**
	 * Se inserta en la tabla seg_Perfil 
	 * un nuevo componente
	 */
	
	public int agregar(SegPerfilDto perfil) throws Exception, BusinessException{
		return segPerfilDao.insertar(perfil);
	}
	
	public List<SegPerfilDto> llenarCombo(int id){
		return segPerfilDao.llenarCombo(id);
	}
	//getters && setters
	public SegPerfilDao getSegPerfilDao() {
		return segPerfilDao;
	}
		
	public void setSegPerfilDao(SegPerfilDao segPerfilDao) {
		this.segPerfilDao = segPerfilDao;
	}

	public Map<String, Object> verificarFacultad(String idComponente, int idUsuario) {
		//boolean r = false;	
		Map<String, Object> result = new HashMap<>(); 
		result.put("conn", "");
		result.put("excep", "");
		result.put("facultad", false);
		
		try{
			result = segPerfilDao.verificarFacultad(idComponente, idUsuario);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seguridad, C:SegPerfilBusiness, M:verificarFacultad");
			
			result.put("excep", "Error al verificar la facultad");
		}
		return result;
	}
}
