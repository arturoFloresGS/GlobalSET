package com.webset.set.flujos.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webset.set.flujos.dao.MantenimientoDeRubrosDao;
import com.webset.set.flujos.dto.MantenimientoDeRubrosDto;
import com.webset.set.flujos.service.MantenimientoDeRubrosService;
import com.webset.set.utilerias.Bitacora;


@SuppressWarnings("unchecked")
public class MantenimientoDeRubrosBusinessImpl implements MantenimientoDeRubrosService{
	private Bitacora bitacora = new Bitacora();
	private MantenimientoDeRubrosDao mantenimientoDeRubrosDao;
	
	public List<MantenimientoDeRubrosDto> llenarComboGrupo(){
		List<MantenimientoDeRubrosDto> listCr = new ArrayList<MantenimientoDeRubrosDto>();
		try{
			listCr = mantenimientoDeRubrosDao.llenarComboGrupo();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Flujos, C:MantenimientoDeRubrosBusiness, M:llenarComboGrupo");
		}
		return listCr;
	}
	
	public List<MantenimientoDeRubrosDto> consultarRubro(int idGrupo){
		List<MantenimientoDeRubrosDto> listConsRub = new ArrayList<MantenimientoDeRubrosDto>();
		try{
			listConsRub = mantenimientoDeRubrosDao.consultarRubro(idGrupo);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:MantenimientoDeRubrosAction, M:consultarRubro");
		}
		return listConsRub;
	}
	
	public Map accionRubro(List<Map<String, String>> gListRubro){
		Map<String, Object> mapRet = new HashMap<String, Object>();
		int iInsert = 0;
		try{
			iInsert = mantenimientoDeRubrosDao.accionRubro(gListRubro);
			if(iInsert > 0)
				mapRet.put("msgUsuario", "Datos Registrados");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Flujos, C:MantenimientoDeRubrosBusiness, M:accionRubro");
			mapRet.put("msgUsuario", "Ocurrio un error durante la operaci�n");
		}
		return mapRet;	
	}
	
	public Map eliminarRubro(int grupo, int rubro){
		Map<String, Object> mapRet = new HashMap<String, Object>();
		try {
			int resp = 0;
			resp = mantenimientoDeRubrosDao.eliminarRubro(grupo,rubro);
			if(resp > 0)
				mapRet.put("msgUsuario","Registro Eliminado");
			else 
				mapRet.put("msgUsuario", "Error en la eliminaci�n");
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:MantenimientoDeRubrosBusiness, M:eliminarRubro");	
			mapRet.put("msgUsuario", "Ocurrio una excepci�n al eliminar");
		}
		return mapRet;
	}

	public MantenimientoDeRubrosDao getMantenimientoDeRubrosDao() {
		return mantenimientoDeRubrosDao;
	}

	public void setMantenimientoDeRubrosDao(
			MantenimientoDeRubrosDao mantenimientoDeRubrosDao) {
		this.mantenimientoDeRubrosDao = mantenimientoDeRubrosDao;
	}
}
