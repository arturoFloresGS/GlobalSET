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
import com.webset.set.utilerias.dao.MantenimientoLeyendasDao;
import com.webset.set.utilerias.service.MantenimientoLeyendasService;
import com.webset.set.utileriasmod.dto.MantenimientoLeyendasDto;
import com.webset.utils.tools.Utilerias;



public class MantenimientoLeyendasBusinessImpl implements MantenimientoLeyendasService{
	private Bitacora bitacora = new Bitacora();
	MantenimientoLeyendasDao objMantenimientoLeyendasDao;	
	
	public List<MantenimientoLeyendasDto> llenaGridLeyendas(String descLeyenda){
		List<MantenimientoLeyendasDto> recibeDatos = new ArrayList<MantenimientoLeyendasDto>();
		try{
			recibeDatos = objMantenimientoLeyendasDao.llenaGridLeyendas(descLeyenda);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoLeyendasBusinessImpl, M:llenaGridLeyendas");
		}return recibeDatos;
	}
	
	public String insertaMantenimientoLeyendas(MantenimientoLeyendasDto dto){
		String resultado="";
		try {
			if(!dto.getDescLeyenda().equals("")){
				resultado=objMantenimientoLeyendasDao.insertaMantenimientoLeyendas(dto);
			}else{
				resultado="La leyenda no debe estar vacia";
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoLeyendasBusinessImpl, M:insertaMantenimientoLeyendas");
		}return resultado;
	}
	
	public String updateMantenimientoLeyendas(MantenimientoLeyendasDto dto){
		String resultado="";
		try {
			if(!dto.getDescLeyenda().equals("")){
				resultado=objMantenimientoLeyendasDao.updateMantenimientoLeyendas(dto);
			}else{
				resultado="La leyenda no debe estar vacia";
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoLeyendasBusinessImpl, M:updateMantenimientoLeyendas");
		}return resultado;
	}
	
	public String deleteMantenimientoLeyendas(MantenimientoLeyendasDto dto){
		String resultado="";
		try {
			resultado=objMantenimientoLeyendasDao.deleteMantenimientoLeyendas(dto);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoLeyendasBusinessImpl, M:deleteMantenimientoLeyendas");
		}return resultado;
	}
	
	/**********Excel***************/			 
	public String exportaExcel(String datos) {
		String respuesta = "";
	    Gson gson = new Gson();
		List<Map<String, String>> parameters = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
	    try {	    	
			
			HSSFWorkbook wb = Utilerias.generarExcel(new String[]{
													"ID",
													"Leyenda",
										            "Fecha"
												}, 
												parameters, 
												new String[]{
														"idLeyenda",
														"descLeyenda",
														"fecAlta"
														
												},"Leyendas");			
            
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
		public MantenimientoLeyendasDao getMantenimientoLeyendasDao() {
			return objMantenimientoLeyendasDao;
		}

		public void setObjMantenimientoLeyendasDao(
				MantenimientoLeyendasDao objMantenimientoLeyendasDao) {
			this.objMantenimientoLeyendasDao = objMantenimientoLeyendasDao;
		}
	
		 	
}
