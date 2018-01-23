package com.webset.set.seguridad.business;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.webset.set.seguridad.dao.SegConfiguracionesSETDao;
import com.webset.set.seguridad.dto.SegConfiguracionesSETDto;
import com.webset.set.seguridad.dto.SegMantenimientoCatalogosDto;
import com.webset.set.utilerias.Bitacora;

public class SegConfiguracionesSETBusiness {
	private SegConfiguracionesSETDao segConfiguracionesSETDao;
	private Logger logger = Logger.getLogger(SegMantenimientoCatalogosBusiness.class);
	private Bitacora bitacora = new Bitacora();

	public List<SegConfiguracionesSETDto> llenaConfiguraciones() {
		// TODO Auto-generated method stub
		return segConfiguracionesSETDao.llenaConfiguraciones();
	}


	public Map<String, Object> guardarConfiguracion(Map<String, String> datos) {
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try {
			System.out.println("Entro business");
			SegConfiguracionesSETDto segConfiguracionesSETDto = new SegConfiguracionesSETDto();
			segConfiguracionesSETDto.setIndice(datos.get("indice"));
			segConfiguracionesSETDto.setValor(datos.get("valor"));
			segConfiguracionesSETDto.setDescripcion(datos.get("descripcion"));
			int r = segConfiguracionesSETDao.guardarConfiguracion(segConfiguracionesSETDto);
			if (r > 0) {
				resultado.put("estatus", true);
				resultado.put("msg", "Configuraci�n guardada con exito.");
			} else if (r == 0) {
				resultado.put("error", "La configuraci�n no fue guardada.");
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegConfiguracionesSETBusiness, M:guardarConfiguracion");
		}
		return resultado;
	}
	

	public Map<String, Object> modificarConfiguracion(Map<String, String> datos) {
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try {
			SegConfiguracionesSETDto segConfiguracionesSETDto = new SegConfiguracionesSETDto();
			segConfiguracionesSETDto.setIndice(datos.get("indice"));
			segConfiguracionesSETDto.setValor(datos.get("valor"));
			segConfiguracionesSETDto.setDescripcion(datos.get("descripcion"));
			int r = segConfiguracionesSETDao.modificarConfiguracion(segConfiguracionesSETDto);
			if (r > 0) {
				resultado.put("estatus", true);
				resultado.put("msg", "Configuraci�n modificada con exito.");
			} else if (r == 0) {
				resultado.put("error", "La configuraci�n no fue modificada.");
			}
		} catch (NumberFormatException e) {
			resultado.put("error", "Error en el id del cat�logo.");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteBusiness, M:modificarModulo");
		}catch (Exception e) {		
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegConfiguracionesSETBusiness, M:modificarConfiguracion");
		}
		return resultado;
	}


	public Map<String, Object> eliminarConfiguracion(String id) {
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try {
			
				int r = segConfiguracionesSETDao.eliminarConfiguracion(id);
				if (r > 0) {
					resultado.put("estatus", true);
					resultado.put("msg", "Configuraci�n eliminada con exito.");
				} else if (r == 0) {
					resultado.put("error", "La configuraci�n no fue eliminada.");
				}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegConfiguracionesSETBusiness, M:eliminarConfiguracion");
		}
		return resultado;
	}
	
	public SegConfiguracionesSETDao getSegConfiguracionesSETDao() {
		return segConfiguracionesSETDao;
	}

	public void setSegConfiguracionesSETDao(SegConfiguracionesSETDao segConfiguracionesSETDao) {
		this.segConfiguracionesSETDao = segConfiguracionesSETDao;
	}
	
}
