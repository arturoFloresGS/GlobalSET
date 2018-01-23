package com.webset.set.seguridad.business;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.webset.set.seguridad.dao.SegMantenimientoCatalogosDao;
import com.webset.set.seguridad.dto.SegComponenteDto;
import com.webset.set.seguridad.dto.SegMantenimientoCatalogosDto;
import com.webset.set.utilerias.Bitacora;

public class SegMantenimientoCatalogosBusiness {
	private SegMantenimientoCatalogosDao segMantenimientoCatalogosDao;
	private Logger logger = Logger.getLogger(SegMantenimientoCatalogosBusiness.class);
	private Bitacora bitacora = new Bitacora();
	

	public List<SegMantenimientoCatalogosDto> llenaCatalogos() {
		// TODO Auto-generated method stub
		return segMantenimientoCatalogosDao.llenaCatalogos();
 	}

	public Map<String, Object> guardarCatalogo(Map<String, String> datos) {
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try {
			System.out.println("Entro business");
			SegMantenimientoCatalogosDto segMantenimientoCatalogosDto = new SegMantenimientoCatalogosDto();
			segMantenimientoCatalogosDto.setNombreCatalogo(datos.get("nombreCatalogo"));
			segMantenimientoCatalogosDto.setDescCatalogo(datos.get("descCatalogo"));
			segMantenimientoCatalogosDto.setTituloColumnas(datos.get("tituloColumnas"));
			segMantenimientoCatalogosDto.setCampos(datos.get("campos"));
			segMantenimientoCatalogosDto.setBotones(datos.get("botones"));
			int r = segMantenimientoCatalogosDao.guardarCatalogo(segMantenimientoCatalogosDto);
			if (r > 0) {
				resultado.put("estatus", true);
				resultado.put("msg", "Cat�logo guardado con exito.");
			} else if (r == 0) {
				resultado.put("error", "El cat�logo no fue guardado.");
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegMantenimientoCatalogosBusiness, M:guardarModulo");
		}
		return resultado;
	}

	public Map<String, Object> modificarCatalogo(Map<String, String> datos) {
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try {
			SegMantenimientoCatalogosDto segMantenimientoCatalogosDto = new SegMantenimientoCatalogosDto();
			segMantenimientoCatalogosDto.setNombreCatalogo(datos.get("nombreCatalogo"));
			segMantenimientoCatalogosDto.setDescCatalogo(datos.get("descCatalogo"));
			segMantenimientoCatalogosDto.setTituloColumnas(datos.get("tituloColumnas"));
			segMantenimientoCatalogosDto.setCampos(datos.get("campos"));
			segMantenimientoCatalogosDto.setBotones(datos.get("botones"));
			int r = segMantenimientoCatalogosDao.modificarCatalogo(segMantenimientoCatalogosDto);
			if (r > 0) {
				resultado.put("estatus", true);
				resultado.put("msg", "Cat�logo modificado con exito.");
			} else if (r == 0) {
				resultado.put("error", "El cat�logo no fue modificado.");
			}
		} catch (NumberFormatException e) {
			resultado.put("error", "Error en el id del cat�logo.");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteBusiness, M:modificarModulo");
		}catch (Exception e) {		
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegMantenimientoCatalogosBusiness, M:modificarCatalogo");
		}
		return resultado;
	}

	public Map<String, Object> eliminarCatalogo(String id) {
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try {
			
				int r = segMantenimientoCatalogosDao.eliminarCatalogo(id);
				if (r > 0) {
					resultado.put("estatus", true);
					resultado.put("msg", "Cat�logo eliminado con exito.");
				} else if (r == 0) {
					resultado.put("error", "El cat�logo no fue eliminado.");
				}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegMantenimientoComponentesBusiness, M:eliminarCatalogo");
		}
		return resultado;
	}

	/*Getters and Setters*/
	public SegMantenimientoCatalogosDao getSegMantenimientoCatalogosDao() {
		return segMantenimientoCatalogosDao;
	}

	public void setSegMantenimientoCatalogosDao(SegMantenimientoCatalogosDao segMantenimientoCatalogosDao) {
		this.segMantenimientoCatalogosDao = segMantenimientoCatalogosDao;
	}
}
