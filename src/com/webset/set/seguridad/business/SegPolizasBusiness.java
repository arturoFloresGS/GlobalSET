package com.webset.set.seguridad.business;

import java.util.Date;
import java.util.List;

import com.webset.set.seguridad.dao.SegPolizasDao;
import com.webset.set.seguridad.dto.ComboUsuario;
import com.webset.set.seguridad.dto.PolizaDto;
import com.webset.set.seguridad.dto.UsuarioPolizaDto;
import com.webset.set.utilerias.Bitacora;

public class SegPolizasBusiness {
	private SegPolizasDao segPolizasDao;
	private Bitacora bitacora = new Bitacora();

	public SegPolizasDao getSegPolizasDao() {
		return segPolizasDao;
	}

	public void setSegPolizasDao(SegPolizasDao segPolizasDao) {
		this.segPolizasDao = segPolizasDao;
	}

	public List<ComboUsuario> obtenerUsuarios() {
		try {
			return segPolizasDao.obtenerUsuario();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegPolizasBusinnes, M:obtenerUsuarios");
			return null;
		}
	}

	public List<PolizaDto> obtenerPolizas(int idUsuario, boolean existe) {
		try {
			return segPolizasDao.obtenerPolizas(idUsuario, existe);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegPolizasBusinnes, M:obtenerPolizas");
			return null;
		}
	}

	public int asignarPoliza(int noUsuario, int idPoliza, boolean todos) {
		try {
			return segPolizasDao.asignarPoliza(noUsuario, idPoliza, todos);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegPolizasBusiness, M:asignarPoliza");
			return 0;
		}
	}

	public int eliminar(UsuarioPolizaDto dtoPolizaDto, boolean todos) {
		int res=0;
		try {
			res=segPolizasDao.eliminar(dtoPolizaDto, todos);
		} catch (Exception e) {
			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegPolizasBusiness, M:eliminar");
			return 0;
		}
		return res;
	}
	
}
