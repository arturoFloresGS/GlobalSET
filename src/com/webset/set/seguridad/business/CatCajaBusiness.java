package com.webset.set.seguridad.business;

import java.util.List;

import com.webset.set.seguridad.dao.CatCajaDao;
import com.webset.set.seguridad.dto.CajaUsuarioDto;
import com.webset.set.seguridad.dto.CatCajaDto;
import com.webset.set.seguridad.exceptions.BusinessException;

/**
 * 
 * @author Cristian Garcia Garcia
 *
 */
public class CatCajaBusiness {
	private CatCajaDao catCajaDao;
	
	/**
	 * Busca en la BD un usuario cuyo id sea igual al parametro proporcionado
	 * @param id idetificador del usuario a buscar
	 * @return usuario cuyo id sea igual al id proporcionado como parametro
	 * @throws BusinessException en caso de un error logico (No encuentra al usuario, hay mas de 2 usuarios, etc) 
	 * @throws Exception en caso de un error de programacion 
	 */
	public List<CatCajaDto> obtenerListaCajas(int noUsuario, boolean existe) throws Exception, BusinessException{
		List<CatCajaDto> cajas = catCajaDao.consultar(noUsuario, existe);
		return cajas;
	}
	
	public int insertar(CajaUsuarioDto dto, boolean todos) {
		int res=0;
		try {
			res=catCajaDao.insertar(dto,todos);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	public int eliminar(CajaUsuarioDto dto, boolean todos) {
		int res=0;
		try {
			res=catCajaDao.eliminar(dto, todos);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	public List<CatCajaDto> obtenerTodasCajas() throws Exception, BusinessException{
		List<CatCajaDto> cajas = catCajaDao.obtenerTodasCajas();
		return cajas;
	}
	
		//getters && setters
	public CatCajaDao getCatCajaDao() {
		return catCajaDao;
	}
	
	public void setCatCajaDao(CatCajaDao catCajaDao) {
		this.catCajaDao = catCajaDao;
	}
	

}
