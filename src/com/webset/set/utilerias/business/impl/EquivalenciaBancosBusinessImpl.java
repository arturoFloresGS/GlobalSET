package com.webset.set.utilerias.business.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dao.EquivalenciaBancosDao;
import com.webset.set.utilerias.dto.EquivalenciaBancosDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.service.EquivalenciaBancosService;
import com.webset.utils.tools.Utilerias;


public class EquivalenciaBancosBusinessImpl implements EquivalenciaBancosService {
	private Bitacora bitacora = new Bitacora();
	private EquivalenciaBancosDao objEquivalenciaBancosDao;
	
	
	public List<EquivalenciaBancosDto> llenaGridBancos(String bankl, String banka,String idBanco,String descBanco) {
		List<EquivalenciaBancosDto> recibeDatos = new ArrayList<EquivalenciaBancosDto>();
		try{
			recibeDatos = objEquivalenciaBancosDao.llenaGridBancos(bankl,banka,idBanco,descBanco);
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:EquivalenciaBancosBusinessImpl, M:llenaGridBancos");
		}return recibeDatos;
	}
	
	public List<EquivalenciaBancosDto>consultarBancos(String texto){
		return objEquivalenciaBancosDao.consultarBancos(texto);
	}
	
	public List<EquivalenciaBancosDto>llenarComboBanco(EquivalenciaBancosDto dto){
		return objEquivalenciaBancosDao.llenarComboBanco(dto);	
	}
	
	public String actualizaEquivaleBanco(String bankl, String idBancoGrid, String idBancoText){
		StringBuffer mensaje = new StringBuffer();
		
		try {
			mensaje.append(objEquivalenciaBancosDao.actualizaEquivaleBanco(bankl, idBancoGrid, idBancoText));
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:llenarComboGrpEmpresas, M:aplicarDescuentoSimple");
		}
		
		return mensaje.toString();
	}
	
	@Override
	public HSSFWorkbook reporteBancosExt() {
		HSSFWorkbook hb=null;
		try {
			hb=Utilerias.generarExcel(new String[]{
					"ID Banco SAP",
					"Nombre Banco SAP",
					"ID Banco SET",
					"Nombre Banco SET"	
					
						
			}, 
					objEquivalenciaBancosDao.reporteBancosExt(), 
					new String[]{
						"bankl",
						"banka",
						"idBanco",
						"descBanco"
						
							
					});
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: EquivalenciaBancosImpl, M: reporteBancosExt");
		}return hb;
	}

	
	
	//Setters y Getters
	public EquivalenciaBancosDao getObjEquivalenciaBancosDao() {
		return objEquivalenciaBancosDao;
	}
	public void setObjEquivalenciaBancosDao(EquivalenciaBancosDao objEquivalenciaBancosDao) {
		this.objEquivalenciaBancosDao = objEquivalenciaBancosDao;
	}


}
