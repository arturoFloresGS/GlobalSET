package com.webset.set.seguridad.business;

import java.util.List;

import com.webset.set.seguridad.dao.CatOrigenMovDao;
import com.webset.set.seguridad.dto.CatOrigenMovDto;
import com.webset.set.seguridad.dto.OrigenUsuarioDto;
import com.webset.set.seguridad.exceptions.BusinessException;

/**
 * 
 * @author Cristian Garcia Garcia
 *
 */
public class CatOrigenMovBusiness {
	private CatOrigenMovDao catOrigenMovDao;
	
	/**
	 * Busca en la BD un usuario cuyo id sea igual al parametro proporcionado
	 * @param id idetificador del usuario a buscar
	 * @return usuario cuyo id sea igual al id proporcionado como parametro
	 * @throws BusinessException en caso de un error logico (No encuentra al usuario, hay mas de 2 usuarios, etc) 
	 * @throws Exception en caso de un error de programacion 
	 */
	public List<CatOrigenMovDto> obtenerListaOrigenes(int noUsuario, boolean existe) throws Exception, BusinessException{
		List<CatOrigenMovDto> origenes = catOrigenMovDao.consultar(noUsuario, existe);
		return origenes;
	}
	
	public int insertar(OrigenUsuarioDto dto, boolean todos){
		int res=0;
		try {
			res=catOrigenMovDao.insertar(dto,todos);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	public int eliminar(OrigenUsuarioDto dto, boolean todos){
		int res=0;
		try {
			res=catOrigenMovDao.eliminar(dto,todos);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
		//getters && setters
	public CatOrigenMovDao getCatOrigenMovDao() {
		return catOrigenMovDao;
	}
	
	public void setCatOrigenMovDao(CatOrigenMovDao catOrigenMovDao) {
		this.catOrigenMovDao = catOrigenMovDao;
	}
	

}
