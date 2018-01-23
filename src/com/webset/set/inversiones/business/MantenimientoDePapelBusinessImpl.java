package com.webset.set.inversiones.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webset.set.inversiones.dao.MantenimientoDePapelDao;
import com.webset.set.inversiones.dto.MantenimientoDePapelDto;
import com.webset.set.inversiones.middleware.service.MantenimientoDePapelService;
import com.webset.set.utilerias.Bitacora;

public class MantenimientoDePapelBusinessImpl implements MantenimientoDePapelService{
	//private InversionesDao inversionesDao;
	private MantenimientoDePapelDao mantenimientoDePapelDao;
	private Bitacora bitacora = new Bitacora();
	//private Funciones funciones = new Funciones();
	
	public List<MantenimientoDePapelDto> consultarPapel(){
		List<MantenimientoDePapelDto> listConsPap = new ArrayList<MantenimientoDePapelDto>();
		try{
			listConsPap = mantenimientoDePapelDao.consultarPapel();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:MantenimientoDePapelBusiness, M:consultarPapel");
		}
		return listConsPap;
	}
	
	public List<MantenimientoDePapelDto> llenarComboTipoValor(){
		List<MantenimientoDePapelDto> listCtv = new ArrayList<MantenimientoDePapelDto>();
		try{
			listCtv = mantenimientoDePapelDao.llenarComboTipoValor();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:MantenimientoDePapelBusiness, M:llenarComboTipoValor");
		}
		return listCtv;
	}

	public Map<String, Object> accionPapel(List<Map<String, String>> gListPapel, char bandera, String confir){
		
		Map<String, Object> mapRet = new HashMap<String, Object>();
		int iInsert = 0;
		int iModifi = 0;
		
		try{
			
			if(bandera =='N')
			{
				if( mantenimientoDePapelDao.validaNuevoPapel(gListPapel) > 0 ){
					mapRet.put("msgUsuario", "Ya existe ese Id Papel registrado.");
				}else{
				
					iInsert = mantenimientoDePapelDao.nuevoPapel(gListPapel);
				
					if(iInsert > 0){
						mapRet.put("msgUsuario", "Datos Registrados");
					}
				
				}
			}
			
			else if(bandera =='M')
			{
				iModifi = mantenimientoDePapelDao.modificarPapel(gListPapel, confir);
				if(iModifi > 0)
					mapRet.put("msgUsuario", "Datos modificados correctamente");
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:MantenimientoDePapelBusiness, M:accionPapel");
			mapRet.put("msgUsuario", "Ocurrio un error durante la operación");
		}
		return mapRet;
	}

	public Map<String, Object> eliminarPapel(String papel){
		Map<String, Object> mapRet = new HashMap<String, Object>();
	
		try {
			int resp = 0;
			resp = mantenimientoDePapelDao.eliminarPapel(papel);
			if(resp > 0)
				mapRet.put("msgUsuario","Registro Eliminado");
			else 
				mapRet.put("msgUsuario", "Error en la eliminación");
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientoDePapelBusiness, M:eliminarImpre");	
			mapRet.put("msgUsuario", "Ocurrio una excepción al eliminar");
		}
		return mapRet;
	}

	public MantenimientoDePapelDao getMantenimientoDePapelDao() {
		return mantenimientoDePapelDao;
	}

	public void setMantenimientoDePapelDao(
			MantenimientoDePapelDao mantenimientoDePapelDao) {
		this.mantenimientoDePapelDao = mantenimientoDePapelDao;
	}
	
}
