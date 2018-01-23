package com.webset.set.seguridad.business;

import java.util.List;

import com.webset.set.seguridad.dao.PersonaDao;
import com.webset.set.seguridad.dto.PersonaDto;
import com.webset.set.seguridad.dto.UsuarioProveedorDto;
import com.webset.set.seguridad.exceptions.BusinessException;

/**
 * 
 * @author vtello
 *
 */
public class PersonaBusiness {
	private PersonaDao personaDao;
	
	/**
	 * Busca en la BD un usuario cuyo id sea igual al parametro proporcionado
	 * @param id idetificador del usuario a buscar
	 * @return usuario cuyo id sea igual al id proporcionado como parametro
	 * @throws BusinessException en caso de un error logico (No encuentra al usuario, hay mas de 2 usuarios, etc) 
	 * @throws Exception en caso de un error de programacion 
	 */
	public List<PersonaDto> obtenerListaPersonas(int noUsuario, boolean existe) throws Exception, BusinessException{
		List<PersonaDto> personas = personaDao.consultar(noUsuario, existe);
		return personas;
	}
	
	 
	/**
	 * Inserta en la tabla usuario_proveedor  
	 * @param dto que tiene la clave del usuario y la clave de la persona
	 * @throws Exception
	 * @throws BusinessException
	 */
	public int insertar(UsuarioProveedorDto dto, boolean todos)
	{
		int res=0;
	 try {
		 res= personaDao.insertar(dto, todos);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return res;
	}
	/**
	 * Elimina en la tabla usuario_proveedor
	 * @param dto
	 * @throws Exception
	 * @throws BusinessException
	 */
	public int eliminar(UsuarioProveedorDto dto,boolean todos)  
	{
		int res=0;
		try {
			res=personaDao.eliminar(dto,todos);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
			
	}
	
	
		//getters && setters
	public PersonaDao getPersonaDao() {
		return personaDao;
	}
	
	public void setPersonaDao(PersonaDao personaDao) {
		this.personaDao = personaDao;
	}
	

}
