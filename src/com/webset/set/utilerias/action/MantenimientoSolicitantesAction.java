package com.webset.set.utilerias.action;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.service.MantenimientoSolicitantesService;
import com.webset.set.utileriasmod.dto.MantenimientoSolicitantesDto;
import com.webset.utils.tools.Utilerias;

public class MantenimientoSolicitantesAction {
	Contexto contexto = new Contexto();
	Bitacora bitacora = new Bitacora();
	MantenimientoSolicitantesService objMantenimientoSolicitantesService;
		 
	@DirectMethod
	public List<MantenimientoSolicitantesDto> llenaGridSolicitantes(String nombre){
		List<MantenimientoSolicitantesDto> listaResultado = new ArrayList<MantenimientoSolicitantesDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 199))
			return listaResultado;
		try{
			objMantenimientoSolicitantesService = (MantenimientoSolicitantesService)contexto.obtenerBean("objMantenimientoSolicitantesBusinessImpl");
			listaResultado = objMantenimientoSolicitantesService.llenaGridSolicitantes(nombre.toUpperCase());
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesAction, M: llenaGridSolicitantes");
		} return listaResultado;
	}
		
	@DirectMethod
	public String insertSolicitante(String sJson){
		String resultado="";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 199))
			return resultado;
		Gson gson = new Gson();
		MantenimientoSolicitantesDto dto=new MantenimientoSolicitantesDto();
		List<Map<String, String>> objDatos = gson.fromJson(sJson, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		int t;	
		try{
			objMantenimientoSolicitantesService = (MantenimientoSolicitantesService)contexto.obtenerBean("objMantenimientoSolicitantesBusinessImpl");
			for(t=0; t<objDatos.size(); t++) {
				dto.setIdentificacion1(objDatos.get(t).get("idPersona1")!=null ? objDatos.get(t).get("idPersona1"):"");
				dto.setNombre1(objDatos.get(t).get("nombre1")!=null ? objDatos.get(t).get("nombre1").toUpperCase():"" );
				dto.setPuesto1(objDatos.get(t).get("puesto1")!=null ? objDatos.get(t).get("puesto1"):"" );
                dto.setCorreo1(objDatos.get(t).get("correo1")!=null ? objDatos.get(t).get("correo1"):"" );
				dto.setTelefono1(objDatos.get(t).get("telefono1")!=null ? objDatos.get(t).get("telefono1"):"" );
				dto.setIdentificacion2(objDatos.get(t).get("idPersona2")!=null ? objDatos.get(t).get("idPersona2"):"");
				dto.setNombre2(objDatos.get(t).get("nombre2")!=null ? objDatos.get(t).get("nombre2").toUpperCase():"" );
				dto.setPuesto2(objDatos.get(t).get("puesto2")!=null ? objDatos.get(t).get("puesto2"):"" );
                dto.setCorreo2(objDatos.get(t).get("correo2")!=null ? objDatos.get(t).get("correo2"):"" );
				dto.setTelefono2(objDatos.get(t).get("telefono2")!=null ? objDatos.get(t).get("telefono2"):"" );
				dto.setFecha(objDatos.get(t).get("fecAlta")!=null ? objDatos.get(t).get("fecAlta"):"" );
				dto.setObservacion(objDatos.get(t).get("observacion")!=null ? objDatos.get(t).get("observacion").toUpperCase():"" );
			}
			resultado = objMantenimientoSolicitantesService.insertSolicitante(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesAction, M: insertSolicitante");
		} return resultado;	
	}
		
	@DirectMethod
	public String updateSolicitante(String sJson){
		String resultado="";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 199))
			return resultado;
		Gson gson = new Gson();
		MantenimientoSolicitantesDto dto=new MantenimientoSolicitantesDto();
		List<Map<String, String>> objDatos = gson.fromJson(sJson, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		int t;
		try{
			objMantenimientoSolicitantesService = (MantenimientoSolicitantesService)contexto.obtenerBean("objMantenimientoSolicitantesBusinessImpl");
			for(t=0; t<objDatos.size(); t++) {
				dto.setIdentificacion1(objDatos.get(t).get("idPersona1")!=null ? objDatos.get(t).get("idPersona1"):"");
				dto.setNombre1(objDatos.get(t).get("nombre1")!=null ? objDatos.get(t).get("nombre1").toUpperCase():"" );
				dto.setPuesto1(objDatos.get(t).get("puesto1")!=null ? objDatos.get(t).get("puesto1"):"" );
                dto.setCorreo1(objDatos.get(t).get("correo1")!=null ? objDatos.get(t).get("correo1"):"" );
				dto.setTelefono1(objDatos.get(t).get("telefono1")!=null ? objDatos.get(t).get("telefono1"):"" );
				dto.setIdentificacion2(objDatos.get(t).get("idPersona2")!=null ? objDatos.get(t).get("idPersona2"):"");
				dto.setNombre2(objDatos.get(t).get("nombre2")!=null ? objDatos.get(t).get("nombre2").toUpperCase():"" );
				dto.setPuesto2(objDatos.get(t).get("puesto2")!=null ? objDatos.get(t).get("puesto2"):"" );
                dto.setCorreo2(objDatos.get(t).get("correo2")!=null ? objDatos.get(t).get("correo2"):"" );
				dto.setTelefono2(objDatos.get(t).get("telefono2")!=null ? objDatos.get(t).get("telefono2"):"" );
				dto.setFecha(objDatos.get(t).get("fecAlta")!=null ? objDatos.get(t).get("fecAlta"):"" );
				dto.setObservacion(objDatos.get(t).get("observacion")!=null ? objDatos.get(t).get("observacion").toUpperCase():"" );
				dto.setIdSolicitante(objDatos.get(t).get("idSol")!=null ? objDatos.get(t).get("idSol"):"" );
			}
			resultado = objMantenimientoSolicitantesService.updateSolicitante(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesAction, M: updateSolicitante");
		} return resultado;	
	}
		
	@DirectMethod
	public String deleteSolicitante(String sJson){
		String resultado="";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 199))
			return resultado;
		Gson gson = new Gson();
		MantenimientoSolicitantesDto dto=new MantenimientoSolicitantesDto();
		List<Map<String, String>> objDatos = gson.fromJson(sJson, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		int t;
		try{
			objMantenimientoSolicitantesService = (MantenimientoSolicitantesService)contexto.obtenerBean("objMantenimientoSolicitantesBusinessImpl");
			for(t=0; t<objDatos.size(); t++) {
				dto.setIdSolicitante(objDatos.get(t).get("idSolicitante")!=null ? objDatos.get(t).get("idSolicitante"):"" );
			}
			resultado = objMantenimientoSolicitantesService.deleteSolicitante(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesAction, M: deleteSolicitante");
		} return resultado;	
	}
	
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
			+"P:Utilerias, C:MantenimientoSolicitantesAction, M:obtenerExcel");
		} catch (IOException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:MantenimientoSolicitantesAction, M:obtenerExcel");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:MantenimientoSolicitantesAction, M:obtenerExcel");
		}
		arch.delete();
		return workbook;
	}
	
	@DirectMethod
	public String exportaExcel(String datos) {
		String resultado = "";
		try {
			objMantenimientoSolicitantesService  = (MantenimientoSolicitantesService )contexto.obtenerBean("objMantenimientoSolicitantesBusinessImpl");
			resultado = objMantenimientoSolicitantesService .exportaExcel(datos);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C:MantenimientoBitacoraRentasAction, M: exportaExcel");
		}
		return resultado;
	}
		
}
