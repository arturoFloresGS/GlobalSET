package com.webset.set.egresos.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/*
 * Autor: Luis Alfredo Serrato Montes de Oca
 * 22102015
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.egresos.dto.DatosExcelDto;
import com.webset.set.egresos.service.RentasService;

/*
 * Autor: Luis Alfredo Serrato Montes de Oca
 * 05102015
 */

import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.service.MantenimientoLeyendasService;
import com.webset.utils.tools.Utilerias;

public class RentasAction {
	Bitacora bitacora = new Bitacora();
	Contexto contexto = new  Contexto();
	RentasService rentasService;
	
	
	
	@DirectMethod
	public List<DatosExcelDto> obtenerDatosExcel(String ruta){
		List<DatosExcelDto> listDatosExcel = new ArrayList<DatosExcelDto>();
		System.out.println(ruta);
		if(ruta != "" && ruta != null){
			
			try {
				if(Utilerias.haveSession(WebContextManager.get())  && Utilerias.tienePermiso(WebContextManager.get(), 182)){
					rentasService = (RentasService)contexto.obtenerBean("rentasBusiness");
				//	listDatosExcel = rentasService.obtenerDatosExcel(ruta);
				}
				
			} catch (Exception e) {
				
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Egresos, C:RentasAction, M:obtenerDatosExcel");
			}
			
		}else{
			bitacora.insertarRegistro(new Date().toString() + " " + "La ruta no fue especificada"
			+"P:Egresos, C:RentasAction, M:obtenerDatosExcel");
		}
		
		return listDatosExcel;
	}
	
	
	
	
	@DirectMethod
	public List<DatosExcelDto> validarDatosExcel(String datos){
		List<DatosExcelDto> datosExcelDto = new ArrayList<DatosExcelDto>();
		
		try {
			if(Utilerias.haveSession(WebContextManager.get())  && Utilerias.tienePermiso(WebContextManager.get(), 182)){
				rentasService = (RentasService)contexto.obtenerBean("rentasBusiness");
				datosExcelDto = rentasService.validarDatosExcel(datos);
			}
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:RentasAction, M:validarDatosExcel");
		}
		
		
		return datosExcelDto;
	}
	
	@DirectMethod
	public String crearPropuesta(String matrizDatos , double totalAprovado, double totalAprovadoDls, int sociedad){
		String mensaje = "";
		try {
			if(Utilerias.haveSession(WebContextManager.get())  && Utilerias.tienePermiso(WebContextManager.get(), 182)){
				rentasService = (RentasService)contexto.obtenerBean("rentasBusiness");
				mensaje = rentasService.crearPropuesta(matrizDatos, totalAprovado, totalAprovadoDls, sociedad);
			}
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:RentasAction, M:crearPropuesta");
		}
		
		return mensaje;
	}
	
	//Generar excel
	
	@DirectMethod
	public HSSFWorkbook obtenerExcel(String nombre) {
		FileInputStream file = null;
		HSSFWorkbook workbook = null;
		File arch = new File(nombre);
		try {
			file = new FileInputStream(arch);
			workbook = new HSSFWorkbook(file);
			file.close();
		} catch (FileNotFoundException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:RentasAction, M:obtenerExcel");
		} catch (IOException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:RentasAction, M:obtenerExcel");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:RentasAction, M:obtenerExcel");
		}
		arch.delete();
		return workbook;
	}
	
	@DirectMethod
	public String exportaExcel(String datos) {
		String resultado = "";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 182))
			return resultado;
		try {
			rentasService = (RentasService)contexto.obtenerBean("rentasBusiness");
			resultado = rentasService.exportaExcel(datos);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:RentasAction, M: exportaExcel");
		}
		return resultado;
	}

	
	
}
