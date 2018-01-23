package com.webset.set.seguridad.business;

import java.util.List;

import com.webset.set.seguridad.dao.SegFacultadDao;
import com.webset.set.seguridad.dto.SegFacultadDto;
import com.webset.set.seguridad.exceptions.BusinessException;

/**
 * Clase que contiene las reglas de negocio de la aplicacion
 * @author Sergio Vaca 
 *
*/
public class SegFacultadBusiness {
	private SegFacultadDao segFacultadDao;
	
	/**
	 * Busca en la tabla seg_facultad los datos segun los
	 *	parametros recibidos
	 */
	public List<SegFacultadDto> consultar(SegFacultadDto facultad) throws Exception, BusinessException{
		return segFacultadDao.consultar(facultad);
	}
	
	/**
	 * Modifica en la tabla seg_facultad identificado 
	 * por el id_tipo_componente 
	 * y actualiza la informacion en la BD 
	 */
	
	public int modificar(SegFacultadDto comp) throws Exception, BusinessException{
		return segFacultadDao.modificar(comp);
	}
	
	/**
	 * Elimina de la tabla seg_facultad 
	 * el componente identificado por el id_componente 
	 */
	public int eliminar(int idFacul) throws Exception, BusinessException{
		return segFacultadDao.eliminar(idFacul);
	}
	/**
	 * Se inserta en la tabla seg_facultad 
	 * un nuevo componente
	 */
	
	public int agregar(SegFacultadDto comp) throws Exception, BusinessException{
		return segFacultadDao.insertar(comp);
	}
	
	public List<SegFacultadDto> llenarCombo(int id){
		return segFacultadDao.llenarCombo(id);
	}
			
	//getters && setters
	public SegFacultadDao getSegFacultadDao() {
		return segFacultadDao;
	}
		
	public void setSegFacultadDao(SegFacultadDao segFacultadDao) {
		this.segFacultadDao = segFacultadDao;
	}

}
