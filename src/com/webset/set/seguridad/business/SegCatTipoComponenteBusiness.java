package com.webset.set.seguridad.business;

import java.util.List;

import com.webset.set.seguridad.dao.SegCatTipoComponenteDao;
import com.webset.set.seguridad.dto.SegCatTipoComponenteDto;
import com.webset.set.seguridad.exceptions.BusinessException;

/**
 * Clase que contiene las reglas de negocio de la aplicacion
 * @author Sergio Vaca 
 *
*/
public class SegCatTipoComponenteBusiness {
	private SegCatTipoComponenteDao segCatTipoComponenteDao;
	/**
	 * Busca en la tabla seg_cat_tipo_componente los datos segun los
	 *	parametros recibidos
	 */
	public List<SegCatTipoComponenteDto> consultar(SegCatTipoComponenteDto comp) throws Exception, BusinessException{
		return segCatTipoComponenteDao.consultar(comp);
	}
	
	/**
	 * Modifica en la tabla seg_componente identificado 
	 * por el id_componente 
	 * y actualiza la informacion en la BD 
	 */
	public int modificar(SegCatTipoComponenteDto comp) throws Exception, BusinessException{
		return segCatTipoComponenteDao.modificar(comp);
	}
	
	/**
	 * Elimina de la tabla seg_componente 
	 * el componente identificado por el id_componente 
	 */
	public int eliminar(int idComp) throws Exception, BusinessException{
		return segCatTipoComponenteDao.eliminar(idComp);
	}
	/**
	 * Se inserta en la tabla seg_componente 
	 * un nuevo componente
	 */
	
	public int agregar(SegCatTipoComponenteDto comp) throws Exception, BusinessException{
		return segCatTipoComponenteDao.insertar(comp);
	}
	/**
	 * Llenar el combo de busqueda
	 * @param id
	 * @return
	 */	
	public List<SegCatTipoComponenteDto> llenarCombo(int id){
		return segCatTipoComponenteDao.llenarCombo(id);
	}
	//getters && setters
	public SegCatTipoComponenteDao getCatTipoComponenteDao() {
		return segCatTipoComponenteDao;
	}
		
	public void setSegCatTipoComponenteDao(SegCatTipoComponenteDao segCatTipoComponenteDao) {
		this.segCatTipoComponenteDao = segCatTipoComponenteDao;
	}
}
