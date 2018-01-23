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
import com.webset.set.utilerias.dao.MantenimientoSolicitantesFirmantesDao;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.service.MantenimientoSolicitantesFirmantesService;
import com.webset.set.utileriasmod.dto.MantenimientoSolicitantesFirmantesDto;
import com.webset.utils.tools.Utilerias;



public class MantenimientoSolicitantesFirmantesBusinessImpl implements MantenimientoSolicitantesFirmantesService{
	Bitacora bitacora;
	MantenimientoSolicitantesFirmantesDao objMantenimientoSolicitantesFirmantesDao;	
	
	public List<LlenaComboGralDto> llenarComboPersonas(){
		List<LlenaComboGralDto> combo=  new ArrayList<LlenaComboGralDto>();
		try {
			combo= objMantenimientoSolicitantesFirmantesDao.llenarComboPersonas();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesFirmantesBusinessImpl, M:llenarComboNoFirmantes");
		}return combo;
	}
	
	public List<MantenimientoSolicitantesFirmantesDto> llenaGridSolicitantesFirmantes(String tipoPersona){
		List<MantenimientoSolicitantesFirmantesDto> recibeDatos = new ArrayList<MantenimientoSolicitantesFirmantesDto>();
		try {
			recibeDatos = objMantenimientoSolicitantesFirmantesDao.llenaGridSolicitantesFirmantes(tipoPersona);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesFirmantesBusinessImpl, M:MantenimientoSolicitantesFirmantesService");	
		}return recibeDatos;
	
	}
	
	public String insertaMantenimientoSolicitantesFirmantes(MantenimientoSolicitantesFirmantesDto dto){
		String resultado="";
		try {
			resultado=validarDatos(dto);
			if(resultado.equals("")){
				resultado=objMantenimientoSolicitantesFirmantesDao.insertaMantenimientoSolicitantesFirmantes(dto);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesFirmantesBusinessImpl, M:insertaMantenimientoSolicitantesFirmantes");	
		}return resultado;
		
	}
	
	public String updateMantenimientoSolicitantesFirmantes(MantenimientoSolicitantesFirmantesDto dto){
		String resultado="";
		try {
			resultado=validarDatos(dto);
			if(resultado.equals("")){
				resultado=objMantenimientoSolicitantesFirmantesDao.updateMantenimientoSolicitantesFirmantes(dto);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesFirmantesBusinessImpl, M:updateMantenimientoSolicitantesFirmantes");	
		}return resultado;
	}
	
	public String deleteMantenimientoSolicitantesFirmantes(MantenimientoSolicitantesFirmantesDto dto){
		String resultado="Error";
		try {
			if(!dto.getIdPersona().equals("")){
				resultado=objMantenimientoSolicitantesFirmantesDao.deleteMantenimientoSolicitantesFirmantes(dto);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesFirmantesBusinessImpl, M:deleteMantenimientoSolicitantesFirmantes");	
		}return resultado;
	}
	
	public String validarDatos(MantenimientoSolicitantesFirmantesDto dto){
		try {
			if(dto.getIdPersona().equals("")&& dto.getNombre().equals("")&& dto.getPuesto().equals("") && dto.getTipoPersona().equals("")&& dto.getTipoPersona().equals(" ")&& (!dto.getTipoPersona().equals("S") || !dto.getTipoPersona().equals("F")|| !dto.getTipoPersona().equals("L"))){
				return "Error datos vacios";
			}else{
				if(dto.getTipoPersona().equals("S")||dto.getTipoPersona().equals("F")||dto.getTipoPersona().equals("L")){
					return "";
				}else{
					return "Error en los datos";
				}
				
			}
		} catch (Exception e) {
			return "Error datos corruptos";
		}
	}
	
	/*********Exporta excel************/
	public String exportaExcel(String datos) {
		String respuesta = "";
	    Gson gson = new Gson();
		List<Map<String, String>> parameters = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
	    try {	    	
			//Se crea el libro Excel
	    	
			HSSFWorkbook wb = Utilerias.generarExcel(new String[]{
													"ID",
													"Nombre",
										            "Puesto",
										            "Tipo"
												}, 
												parameters, 
												new String[]{
														"idPersona",
														"nombre",
														"puesto",
														"tipoPersona"
												},"Solicitantes Firmantes");			
            
            respuesta = ConstantesSet.RUTA_EXCEL + "propuestas " + Utilerias.indicadorFecha() +".xls";
            File outputFile = new File(respuesta);
          
			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
        } catch (IOException e) {
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Utilerias, C:MantenimientoSolicitantesFirmantesBusinessImpl, M:exportaExcel");
        	respuesta = "";
        } catch(Exception e){
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Utilerias, C:MantenimientoSolicitantesFirmantesBusinessImpl, M:exportaExcel");
            respuesta = "";
        }
        return respuesta;
	}
	
	//*********************************************************************
		public MantenimientoSolicitantesFirmantesDao getMantenimientoSolicitantesFirmantesDao() {
			return objMantenimientoSolicitantesFirmantesDao;
		}

		public void setObjMantenimientoSolicitantesFirmantesDao(
				MantenimientoSolicitantesFirmantesDao objMantenimientoSolicitantesFirmantesDao) {
			this.objMantenimientoSolicitantesFirmantesDao = objMantenimientoSolicitantesFirmantesDao;
		}
		
		

}
