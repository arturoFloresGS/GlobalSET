package com.webset.set.seguridad.business;

import java.util.List;

import com.webset.set.seguridad.dao.SegFacultadComponenteDao;
import com.webset.set.seguridad.dto.SegFacultadComponenteDto;
import com.webset.set.seguridad.exceptions.BusinessException;

/**
 * @author Sergio Vaca
 * @since 19/Octubre/2010 
 * @see <p>Tabla seg_facultad_componente</p>
*/


/**
 * Clase que contiene los metodos "consultar", "insertar", "eliminar",
 * de todas los componentes asignadas o por asignar a una facultad..
 */

public class SegFacultadComponenteBusiness {
	private SegFacultadComponenteDao segFacultadComponenteDao;
	
	
	/**
	 * 
	 * @param comp un objeto de tipo SegFacultadComponenteDto 
	 * el cual sirve para consultar todos los componentes ya sean asignados 
	 * o sin asignar  
	 * @param ban false para los no asignados y true para los asignados
	 * @return una lista de objetos SegFacultadComponente
	 * @throws Exception 
	 * @throws BusinessException
	 */
	public List<SegFacultadComponenteDto> consultar(SegFacultadComponenteDto comp,boolean ban) throws Exception, BusinessException{
		List<SegFacultadComponenteDto> componentes = null;
		componentes =segFacultadComponenteDao.consultar(comp,ban);
		return componentes;
		
	}
	/**
	 * inserta todos los componente en estatus activo o solo un componente 
	 * @param facultad para saber a que facultad y que componente dependiendo si
	 * se le fijo dato al id componente
	 * @return un valor entero para 1 correcto 0 ocurrio un error
	 * @throws Exception
	 */
	public int insertar(SegFacultadComponenteDto facultad) throws Exception{
		return segFacultadComponenteDao.insertar(facultad);
		 
	}
	
	/**
	 * elimina todos los componente en estatus activo o solo un componente 
	 * @param facultad para saber a que facultad y que componente dependiendo si
	 * se le fijo dato al id componente
	 * @return un valor entero para 1 correcto -1 ocurrio un error
	 * @throws Exception
	 */
	public int eliminar(SegFacultadComponenteDto facultad) throws Exception{
		return segFacultadComponenteDao.eliminar(facultad);
		 
	}
			
	//getters && setters
	public SegFacultadComponenteDao getSegFacultadComponenteDao() {
		return segFacultadComponenteDao;
	}
		
	public void setSegFacultadComponenteDao(SegFacultadComponenteDao segFacultadComponenteDao) {
		this.segFacultadComponenteDao = segFacultadComponenteDao;
	}
}
