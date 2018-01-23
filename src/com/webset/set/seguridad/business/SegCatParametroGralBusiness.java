package com.webset.set.seguridad.business;

import java.util.List;

import com.webset.set.seguridad.dao.SegCatParametroGralDao;
import com.webset.set.seguridad.dto.SegCatParametroGralDto;
import com.webset.set.seguridad.exceptions.BusinessException;

/**
 * Clase que contiene las reglas de negocio de la aplicacion
 * @author Sergio Vaca 
 *
*/
public class SegCatParametroGralBusiness {
	private SegCatParametroGralDao segCatParametroGralDao;
	/**
	 * Busca en la tabla seg_cat_param_gral los datos segun los
	 *	parametros recibidos
	 */
	public List<SegCatParametroGralDto> consultar(int idParam, String desc, String valor) throws Exception, BusinessException{
		SegCatParametroGralDto comp = new SegCatParametroGralDto();
		if(desc!=null && valor!=null && idParam>0 && !desc.equals("") && !valor.equals("")){ //Saber si tenemos algun dato para la consulta
			comp.setIdParametroGeneral(idParam);
			comp.setDescripcion(desc);
			comp.setValor(valor);
		}
		List<SegCatParametroGralDto> componentes = segCatParametroGralDao.consultar(comp);
		if(componentes.size() == 0){
			throw new BusinessException("No existen componentes con este id " + idParam + ". Verifique");
		}
		return componentes;
	}
	
	/**
	 * Modifica en la tabla seg_componente identificado 
	 * por el id_parametro_general 
	 * y actualiza la informacion en la BD 
	 */
	
	public void modificar(SegCatParametroGralDto comp) throws Exception, BusinessException{
		if(segCatParametroGralDao.noNulo(comp))
			segCatParametroGralDao.modificar(comp);
		else
			throw new BusinessException("Introduzca Todos los datos");
		
	}
	
	/**
	 * Elimina de la tabla seg_componente 
	 * el componente identificado por el id_parametro_general 
	 */
	public void eliminar(int idParam) throws Exception, BusinessException{
		segCatParametroGralDao.eliminar(idParam);
	}
	/**
	 * Se inserta en la tabla seg_cat_parametro_general 
	 * un nuevo componente
	 */
	
	public void agregar(SegCatParametroGralDto param) throws Exception, BusinessException{
		if(segCatParametroGralDao.noNulo(param))
			segCatParametroGralDao.insertar(param);
		else
			throw new BusinessException("Introduzca Todos los datos");
	}
			
	//getters && setters
	public SegCatParametroGralDao getCatTipoComponenteDao() {
		return segCatParametroGralDao;
	}
		
	public void setSegCatParametroGralDao(SegCatParametroGralDao segCatParametroGralDao) {
		this.segCatParametroGralDao = segCatParametroGralDao;
	}

	
}
