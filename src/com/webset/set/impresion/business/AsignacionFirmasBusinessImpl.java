package com.webset.set.impresion.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.webset.set.impresion.dao.AsignacionFirmasDao;
import com.webset.set.impresion.service.AsignacionFirmasService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

/*****
 * 
 * @author YEC
 * 04 DE ENERO DEL 2015
 */


public class AsignacionFirmasBusinessImpl implements AsignacionFirmasService {
	Bitacora bitacora;
	AsignacionFirmasDao objAsignacionFirmasDao;
	
	public List<LlenaComboGralDto> llenarComboBancos(){
		List<LlenaComboGralDto> combo=  new ArrayList<LlenaComboGralDto>();
		try {
			combo= objAsignacionFirmasDao.llenarComboBancos();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:AsignacionFirmasBusinessImpl, M:llenarComboBancos");
		}return combo;
	}
	
	public List<LlenaComboGralDto> llenarComboCuentas(String idBanco){
		List<LlenaComboGralDto> combo=  new ArrayList<LlenaComboGralDto>();
		try {
			if(!idBanco.equals(""))
				combo= objAsignacionFirmasDao.llenarComboCuentas(idBanco);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:AsignacionFirmasBusinessImpl, M:llenarComboBancos");
		}return combo;
	}
	
	public List<LlenaComboGralDto> llenarComboFirmantes(String tipo,String idBanco, String cuenta ){
		List<LlenaComboGralDto> combo=  new ArrayList<LlenaComboGralDto>();
		try {
			if(!idBanco.equals("") && !tipo.equals("") && !cuenta.equals(""))
				combo= objAsignacionFirmasDao.llenarComboFirmantes(tipo,idBanco,cuenta);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:AsignacionFirmasBusinessImpl, M:llenarComboBancos");
		}return combo; 
	}
	
	public String updateFirmanteDeterminado(String idBanco, String cuenta,String idPersonaA, String idPersonaB){
		System.out.println("En el businessImpl update");
		String resultado="";
		try {
			if(!idBanco.equals("") && !cuenta.equals("")){
				if(!idPersonaA.equals(""))
					resultado = objAsignacionFirmasDao.updateFirmanteDeterminado("A",idBanco,cuenta,idPersonaA) + " al actualizar el firmante A";
				
				if(!idPersonaB.equals(""))
					resultado += "<BR/>" +objAsignacionFirmasDao.updateFirmanteDeterminado("B",idBanco,cuenta,idPersonaB)  + " al actualizar el firmante B ";
			}
				
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:AsignacionFirmasBusinessImpl, M:llenarComboBancos");
		}return resultado; 
		
	}

	//*********************************************************************
			public AsignacionFirmasDao getAsignacionFirmasDao() {
				return objAsignacionFirmasDao;
			}

			public void setObjAsignacionFirmasDao(AsignacionFirmasDao objAsignacionFirmasDao) {
				this.objAsignacionFirmasDao = objAsignacionFirmasDao;
			}
		
}
