package com.webset.set.seguridad.business;

import java.util.List;

import com.webset.set.seguridad.dao.SegBitacoraDao;
import com.webset.set.seguridad.dto.SegBitacoraDto;
import com.webset.set.seguridad.exceptions.BusinessException;
/**
 * Clase que contiene las reglas de negocio de la aplicacion
 * @author Jessica Arelly 
 *
*/
public class SegBitacoraBusiness {
	private SegBitacoraDao segBitacoraDao;
	
	/**
	 * Busca en la tabla seg_bitacora los datos segun los
	 *	parametros recibidos
	 * @param bitacora
	 * @return
	 * @throws Exception
	 * @throws BusinessException
	 */
	public List<SegBitacoraDto> consultar(SegBitacoraDto bitacora) throws Exception, BusinessException{
		List<SegBitacoraDto> bitacoras = segBitacoraDao.consultar(bitacora);
		return bitacoras;
	}
	
	/**
	 * Modifica en la tabla seg_bitacora identificado 
	 * por el id_bitacora 
	 * y actualiza la informacion en la BD
	 * @param bitacora
	 * @throws Exception
	 * @throws BusinessException
	 */
	public void modificar(SegBitacoraDto bitacora) throws Exception, BusinessException{
		if(segBitacoraDao.noNulo(bitacora))
			segBitacoraDao.modificar(bitacora);
		else
			throw new BusinessException("Introduzca Todos los datos");
		
	}
	
	/**
	 * Elimina de la tabla seg_bitacora
	 * el componente identificado por el id_bitacora
	 * @param idBitacora
	 * @throws Exception
	 * @throws BusinessException
	 */
	public void eliminar(int idBitacora) throws Exception, BusinessException{
		segBitacoraDao.eliminar(idBitacora);
	}
	
	/**
	 * Se inserta en la tabla seg_bitacora 
	 * un nuevo componente
	 * @param bitacora
	 * @throws Exception
	 * @throws BusinessException
	 */	
	public void agregar(SegBitacoraDto bitacora) throws Exception, BusinessException{
		if(segBitacoraDao.noNulo(bitacora))
			segBitacoraDao.insertar(bitacora);
		else
			throw new BusinessException("Introduzca Todos los datos");
	}
	
	//getters && setters
	public SegBitacoraDao getSegBitacoraDao() {
		return segBitacoraDao;
	}

	public void setSegBitacoraDao(SegBitacoraDao segBitacoraDao) {
		this.segBitacoraDao = segBitacoraDao;
	}
	

}
