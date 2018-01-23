package com.webset.set.seguridad.business;

import java.util.List;

import com.webset.set.seguridad.dao.SegPerfilFacultadDao;
import com.webset.set.seguridad.dto.SegPerfilFacultadDto;
import com.webset.set.seguridad.exceptions.BusinessException;

/**
 * Clase que contiene las reglas de negocio de la aplicacion
 * @author Sergio Vaca 
 *
*/

public class SegPerfilFacultadBusiness {
	private SegPerfilFacultadDao segPerfilFacultadDao;
	/**
	 * Metodo que consulta la tabla de las facultades asignadas o no asignadas a perfil
	 * facultad de acuerdo a los parametros
	 * de id_perfil y un valor boleano 
	 * true -> para los asignados  
	 * false -> para los no asignados
	 *  
	 * Retorna una lista de objetos SegPerfilFacultadDto con valores solamente de id_componente y clave_componente 
	 */
	public List<SegPerfilFacultadDto> consultar(SegPerfilFacultadDto perfil,boolean ban) throws Exception, BusinessException{
		List<SegPerfilFacultadDto> perfiles = null;
		perfiles =segPerfilFacultadDao.consultar(perfil,ban);
		return perfiles;
		
	}
	/**
	 * inserta todos los facultades en estatus activo
	 * pero en caso de que contenga el id_facultad solo inserta el correspondiente
	 * @param perfil para saber a que perfil 
	 * @return
	 * @throws Exception
	 */
	public int insertar(SegPerfilFacultadDto perfil) throws Exception{
		return segPerfilFacultadDao.insertar(perfil);
		 
	}
	
	/**
	 * elimina todos las facultades correspondientes al perfil pero
	 * si tambien contiene valor el id_facultad solo elimina el componente
	 * y perfil correspondiente 
	 * @param perfil
	 * @return
	 * @throws Exception
	 */
	public int eliminar(SegPerfilFacultadDto perfil) throws Exception{
		return segPerfilFacultadDao.eliminar(perfil);
		 
	}
			
	//getters && setters
	public SegPerfilFacultadDao getSegPerfilFacultadDao() {
		return segPerfilFacultadDao;
	}
		
	public void setSegPerfilFacultadDao(SegPerfilFacultadDao segPerfilFacultadDao) {
		this.segPerfilFacultadDao = segPerfilFacultadDao;
	}
}
