package com.webset.set.seguridad.business;
/**
 * Esta clase representa la tabla seg_tipofac 
 * @author Cristian Garcia Garcia
 *
 */
import java.util.List;

import com.webset.set.seguridad.dao.SegTipoFacDao;
import com.webset.set.seguridad.dto.SegTipoFacDto;
import com.webset.set.seguridad.exceptions.BusinessException;


public class SegTipoFacBusiness {
	
	private SegTipoFacDao tipoFacDao;
	/**
	 * Obtiene un listado de seg_tipofac con la clave de la facultad
	 * @param  tipoFacultad objeto que cotiene los parametros a consultar
	 * @return lista de objetos SegSegFacultadDao que satisfacen el criterio de busqueda 
	 * @throws BusinessException en caso de un error logico (Clave  no encontrada) 
	 * @throws Exception en caso de un error de programacion
	 */
	public List<SegTipoFacDto> obtenerListadoFacultad(SegTipoFacDto tipoFacultad) throws Exception,BusinessException{
		return tipoFacDao.consultar(tipoFacultad);
	}
	
	/**
	 * Busca en la BD un tipo de facultad donde la clave  sea igual al parametro proporcionado
	 * @param tfacultad idetificador del tipo de  facultad a buscar
	 * @return tipoFacultad cuya clave  sea igual ala clave proporcionada como parametro
	 * @throws BusinessException en caso de un error logico (No encuentra al usuario, 
	 * hay mas de 2 usuarios, etc) 
	 * @throws Exception en caso de un error de programacion 
	 */
	public SegTipoFacDto obtenerTipoFacClave(String tFacultad) throws Exception, BusinessException{
		SegTipoFacDto usr = new SegTipoFacDto();
		usr.setTTipoFacultad(tFacultad);
		List<SegTipoFacDto> tipoFacultad =tipoFacDao.consultar(usr);
		
		if(tipoFacultad.size()==0){
			throw new BusinessException("No existe tipo de facultad con la clave " + tFacultad + ". Verifique");
		}
		
		if(tipoFacultad.size() > 1){
			throw new BusinessException("Existe mas de un tipo de  facultad con la clave " + tFacultad + ". Verifique");
		}
		
		return tipoFacultad.get(0);
	}
	
	/**
	 * Modifica seg_tipofac identificado por  clave
	 * @param  tipoFacultad objeto el cual contiene  la informacion a actualizar
	 * @throws Exception En caso de un error de programacion
	 * @throws BusinessException en caso de un error logico (No existe la clave de empresa en la BD)
	 */
	public void modificarFacultad(SegTipoFacDto tipoFacultad) throws Exception, BusinessException{
		if(tipoFacultad.getTTipoFacultad()== null && tipoFacultad.getTTipoFacultad().trim().equals(""))
		{
			throw new BusinessException("La clave del tipo de facultad No Existe");
		}

		tipoFacDao.modificar(tipoFacultad);
	}
	
	/**
	 * Elimina el registro de seg_tipofac identificado por la clave en t_tipo_facultad 
	 * @param  t_facultad identificador del registro que se quiere eliminar
	 * @throws Exception En caso de un error de programacion
	 * @throws BusinessException en caso de un error logico (No existe la clave de la empresa en la BD)
	 */
	public void eliminarFacultadPorClave(String t_facultad) throws Exception, BusinessException{
		if(t_facultad==null || t_facultad.equals(""))
		{
			throw new BusinessException("La clave del tipo de facultad no Existe");
		}
		tipoFacDao.eliminar(t_facultad);
	}
	
	/**
	 * Inserta el registro a seg_tipofac.  
	 * @param tipoFacultad objeto de datos  que se quieren insertar
	 * @throws Exception En caso de un error de programacion
	 * @throws BusinessException en caso de un error logico
	 */
	public void agregar(SegTipoFacDto tipoFacultad) throws Exception, BusinessException{
		tipoFacDao.insertar(tipoFacultad);
	}

}
