package com.webset.set.seguridad.business;

import java.util.List;

import com.webset.set.seguridad.dao.CatAreaDao;
import com.webset.set.seguridad.dto.CatAreaDto;
import com.webset.set.seguridad.dto.UsuarioAreaDto;
import com.webset.set.seguridad.exceptions.BusinessException;

/**
 * 
 * @author vtello
 *
 */
public class CatAreaBusiness {
	private CatAreaDao catAreaDao;
	
	/**
	 * Busca en la BD un usuario cuyo id sea igual al parametro proporcionado
	 * @param id idetificador del usuario a buscar
	 * @return usuario cuyo id sea igual al id proporcionado como parametro
	 * @throws BusinessException en caso de un error logico (No encuentra al usuario, hay mas de 2 usuarios, etc) 
	 * @throws Exception en caso de un error de programacion 
	 */
	public List<CatAreaDto> obtenerListaAreas(int noUsuario, boolean existe) throws Exception, BusinessException{
		return catAreaDao.consultar(noUsuario, existe);
		
	}
	public int insertar(UsuarioAreaDto dto, boolean todos){
		return catAreaDao.insertar(dto, todos);
	}
	
	public int eliminar(UsuarioAreaDto dto, boolean todos){
		
		return catAreaDao.eliminar(dto, todos);
	}
	
	//getters && setters
	public CatAreaDao getCatAreaDao() {
		return catAreaDao;
	}
	
	public void setCatAreaDao(CatAreaDao catAreaDao) {
		this.catAreaDao = catAreaDao;
	}
	

}
