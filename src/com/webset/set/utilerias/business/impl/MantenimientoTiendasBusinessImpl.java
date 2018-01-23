package com.webset.set.utilerias.business.impl;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.dao.MantenimientoTiendasDao;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.service.MantenimientoTiendasService;
import com.webset.set.utileriasmod.dto.MantenimientoTiendasDto;
import com.webset.utils.tools.Utilerias;

/**
* Modificado por YEC
* Se agregan funciones para generar excel
* 21 de diciembre del 2015
*/

public class MantenimientoTiendasBusinessImpl implements MantenimientoTiendasService{
	private Bitacora bitacora = new Bitacora();
	MantenimientoTiendasDao objMantenimientoTiendasDao;	
	
	public List<MantenimientoTiendasDto> llenaGridTiendas(String noAcredor){
		List<MantenimientoTiendasDto> recibeDatos = new ArrayList<MantenimientoTiendasDto>();
		try {
			recibeDatos = objMantenimientoTiendasDao.llenaGridTiendas(noAcredor);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoTiendasBusinessImpl, M: llenaGridTiendas");	
		}return recibeDatos;
	}
	
	public List<LlenaComboGralDto>llenarComboBeneficiario(String ePersona, String nombre){
		try {
			if(ePersona.equals("")&&nombre.equals(""))
				return null;
			else
				return objMantenimientoTiendasDao.llenarComboBeneficiario(ePersona,nombre);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoTiendasBusinessImpl, M:llenarComboBeneficiarios");
			return null;
		}
			
	}
	
	public String insertaMantenimientoTiendas(MantenimientoTiendasDto dto){
		String resultado="Error al registrar";
		try {
			resultado=validarDatos(dto);
			if(resultado.equals("")){
				resultado=objMantenimientoTiendasDao.insertaMantenimientoTiendas(dto);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoTiendasBusinessImpl, M: insertaMantenimientoTiendas");
		}
		return resultado;
	}
	
	public String updateMantenimientoTiendas(MantenimientoTiendasDto dto){
		String resultado="Error al realizar cambios";
		try {
			resultado=validarDatos(dto);
			if(resultado.equals("")){
				resultado=objMantenimientoTiendasDao.updateMantenimientoTiendas(dto);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoTiendasBusinessImpl, M: updateMantenimientoTiendas");
		}return resultado;
	}
	
	public String deleteMantenimientoTiendas(MantenimientoTiendasDto dto){
		String resultado="Error al eliminar";
		try {
			resultado=objMantenimientoTiendasDao.deleteMantenimientoTiendas(dto);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoTiendasBusinessImpl, M: deleteMantenimientoTiendas");
		}return resultado;
	}
	
	public String validarDatos(MantenimientoTiendasDto dto){
		String ccid=""+dto.getCcid();
		if(ccid.equals("0")&& dto.getDescSucursal().equals("")&& dto.getNoAcreedor().equals("")&& dto.getRazonSocial().equals("")){
			return "ERROR: Los datos no deben estar vacios"; //se encontraron campos vacios
		}else{
			if(ccid.length()<=6 && dto.getDescSucursal().length()<=60 && dto.getNoAcreedor().length()<=15 ){
				return "";	
			}else{
				return "ERROR: El texto en algun campo supera los caracteres permitidos"; //Sobrepasa la longitud aceptada
			}
		}
	}
	
	/**********Excel***************/			 
	public String exportaExcel(String datos) {
		String respuesta = "";
	    Gson gson = new Gson();
		List<Map<String, String>> parameters = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
	    try {	    	
			
			HSSFWorkbook wb = Utilerias.generarExcel(new String[]{
													"CCID",
													"Descripcion",
										            "No acreedor",
										            "No empresa",
										            "Empresa",
										            "Razon social"
												}, 
												parameters, 
												new String[]{
														"ccid",
														"descSucursal",
														"noAcreedor",
														"noEmpresa",
														"nomEmpresa",
														"razonSocial"
												},"Catalogo de tiendas");			
            
            respuesta = ConstantesSet.RUTA_EXCEL + "propuestas " + Utilerias.indicadorFecha() +".xls";
            File outputFile = new File(respuesta);
          
			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
        } catch (IOException e) {
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Utilerias, C:MantenimientoTiendasBusinessImpl, M:exportaExcel");
        	respuesta = "";
        } catch(Exception e){
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Utilerias, C:MantenimientTiendasBusinessImpl, M:exportaExcel");
            respuesta = "";
        }
        return respuesta;
	}
	
	//*********************************************************************
		public MantenimientoTiendasDao getMantenimientoTiendasDao() {
			return objMantenimientoTiendasDao;
		}

		public void setObjMantenimientoTiendasDao(MantenimientoTiendasDao objMantenimientoTiendasDao) {
			this.objMantenimientoTiendasDao = objMantenimientoTiendasDao;
		}

}
