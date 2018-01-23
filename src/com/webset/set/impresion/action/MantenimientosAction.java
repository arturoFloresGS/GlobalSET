package com.webset.set.impresion.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.impresion.dto.MantenimientosDto;
import com.webset.set.impresion.service.MantenimientosService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;
public class MantenimientosAction {
	private Contexto contexto = new Contexto();
	private Bitacora bitacora= new Bitacora();
	private MantenimientosService mantenimientosService;
	
	//Patanlla de Mantenimiento de Firmantes
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboNoFirmantes( ){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		try{
			if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 110))
				return null;
			mantenimientosService = (MantenimientosService)contexto.obtenerBean("mantenimientosBusinessImpl");
			list=mantenimientosService.llenarComboNoFirmantes();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Impresion, C:MantenimientosAction, M:llenarComboNoFirmantes");
		}return list;
	}
	
	@DirectMethod
	public List<MantenimientosDto> buscaFirmantes() {
		List<MantenimientosDto> grid = new ArrayList<MantenimientosDto>();
		try {
			if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 110))
				return null;
			mantenimientosService = (MantenimientosService)contexto.obtenerBean("mantenimientosBusinessImpl");
			grid = mantenimientosService.buscaFirmantes();
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosAction, M:buscaFirmantes");
		}return grid;
	}
	
	@DirectMethod
	public Map<String, Object> eliminarFirmantes(String datos) {
		Map<String,Object> mapRetorno = new HashMap<String, Object>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 110))
			return mapRetorno;
		Gson gson = new Gson();
		List<Map<String, String>> objParams = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<MantenimientosDto> list = new ArrayList<MantenimientosDto>();
		try {
			
			mantenimientosService = (MantenimientosService)contexto.obtenerBean("mantenimientosBusinessImpl");
			for(int i=0; i<objParams.size(); i++) {
				MantenimientosDto dtoGrid= new MantenimientosDto();
				dtoGrid.setNoPersona(objParams.get(i).get("noPersona") != null ? Integer.parseInt(objParams.get(i).get("noPersona")) : 0);
				list.add(dtoGrid);
			}
			mapRetorno = mantenimientosService.eliminarFirmantes(list);
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosAction, M:elimarFirmantes");
		}
		return mapRetorno;
	}
	
	@DirectMethod
	public Map<String, Object> insertarFirmantes(String datos) {
		Map<String, Object> mapRetorno = new HashMap<String, Object>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 110))
			return mapRetorno;
		Gson gson = new Gson();
		List<Map<String, String>> objParams = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<MantenimientosDto> list = new ArrayList<MantenimientosDto>();
		try {
			mantenimientosService = (MantenimientosService)contexto.obtenerBean("mantenimientosBusinessImpl");
			for(int i=0; i<objParams.size(); i++) {
				MantenimientosDto dtoGrid = new MantenimientosDto();
				dtoGrid.setNoPersona(objParams.get(i).get("noPersona") != null ? Integer.parseInt(objParams.get(i).get("noPersona")) : 0);
				dtoGrid.setNombre(objParams.get(i).get("nombre"));
				dtoGrid.setPathFirma(objParams.get(i).get("pathFirma"));
				list.add(dtoGrid);
			}
			mapRetorno = mantenimientosService.insertarFirmantes(list);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosAction, M:insertarFirmantes");
		}
		return mapRetorno;
	}
	
	//Patanlla de Mantenimiento de Firmas
	
	@DirectMethod
	public List<LlenaComboGralDto> llenaComboBancos() {
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		try {
			if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 109))
				return list;
			mantenimientosService = (MantenimientosService)contexto.obtenerBean("mantenimientosBusinessImpl");
			list = mantenimientosService.llenaComboBancos();
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosAction, M:llenaComboBancos");
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> llenaComboChequeras(int idBanco) {
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		try {
			if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 109))
				return list;
			mantenimientosService = (MantenimientosService)contexto.obtenerBean("mantenimientosBusinessImpl");
			list = mantenimientosService.llenaComboChequeras(idBanco);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosAction, M:llenaComboChequeras");
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> llenaComboPersonas(String idBanco, String cuenta , boolean busqueda) {
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		try {
			if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 109))
				return list;
			mantenimientosService = (MantenimientosService)contexto.obtenerBean("mantenimientosBusinessImpl");
			list = mantenimientosService.llenaComboPersonas(idBanco,cuenta,busqueda);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosAction, M:llenaComboPersonas");
		}
		return list;
	}
	
	@DirectMethod
	public List<MantenimientosDto> buscaFirmas(String idBanco,String idChequera, String noFirma) {
		List<MantenimientosDto> list = new ArrayList<MantenimientosDto>();
		try {
			if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 109))
				return list;
			mantenimientosService = (MantenimientosService)contexto.obtenerBean("mantenimientosBusinessImpl");
			//System.out.println(idBanco+"--"+idChequera+"--"+noFirma);
			list = mantenimientosService.buscaFirmas(idBanco,idChequera,noFirma);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosAction, M:buscaFirmas");
		}
		return list;
	}
	
	@DirectMethod
	public String eliminarFirma(String sJson){
		String resultado="Error";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 109))
			return resultado;
		Gson gson = new Gson();
		MantenimientosDto dto= new MantenimientosDto();
		List<Map<String, String>> objDatos = gson.fromJson(sJson, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		int t;
		try {
			mantenimientosService = (MantenimientosService)contexto.obtenerBean("mantenimientosBusinessImpl");
			for(t=0; t<objDatos.size(); t++) {
				dto.setIdBanco(objDatos.get(t).get("idBanco")!=null ? Integer.parseInt(objDatos.get(t).get("idBanco")):0);
				dto.setNoPersona(objDatos.get(t).get("noPersona")!=null ? Integer.parseInt(objDatos.get(t).get("noPersona")):0);
				dto.setIdChequera(objDatos.get(t).get("cuenta")!=null ? objDatos.get(t).get("cuenta"):"");
				/*dto.setBDeter(objDatos.get(t).get("bDeter")!=null ? objDatos.get(t).get("bDeter"):"");
				dto.setTipoFirma(objDatos.get(t).get("tipoFirma")!=null ? objDatos.get(t).get("tipoFirma"):"");
				dto.setNombre(objDatos.get(t).get("nombre")!=null ? objDatos.get(t).get("nombre"):"");*/
			}
			resultado = mantenimientosService.eliminarFirma(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosAction, M: insertarFirma");
		} return resultado;	
		
	}
	
	@DirectMethod
	public String insetarFirma(String sJson){
		String resultado="Error";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 109))
			return resultado;
		Gson gson = new Gson();
		MantenimientosDto dto= new MantenimientosDto();
		List<Map<String, String>> objDatos = gson.fromJson(sJson, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		int t;
		try{
			mantenimientosService = (MantenimientosService)contexto.obtenerBean("mantenimientosBusinessImpl");
			for(t=0; t<objDatos.size(); t++) {
				dto.setIdBanco(objDatos.get(t).get("idBanco")!=null ? Integer.parseInt(objDatos.get(t).get("idBanco")):0);
				dto.setNoPersona(objDatos.get(t).get("noPersona")!=null ? Integer.parseInt(objDatos.get(t).get("noPersona")):0);
				dto.setIdChequera(objDatos.get(t).get("cuenta")!=null ? objDatos.get(t).get("cuenta"):"");
			}
			resultado = mantenimientosService.insetarFirma(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosAction, M: insertarFirma");
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
			+"P:Impresion, C:MantenimientosAction, M:obtenerExcel");
		} catch (IOException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:MantenimientosAction, M:obtenerExcel");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:MantenimientosAction, M:obtenerExcel");
		}
		arch.delete();
		return workbook;
	}
	
	@DirectMethod
	public String exportaExcel(String datos, String opcion) {
		String resultado = "";
			
		if(!Utilerias.haveSession(WebContextManager.get()) || !(Utilerias.tienePermiso(WebContextManager.get(), 109)||Utilerias.tienePermiso(WebContextManager.get(), 110)))
			return resultado;
		try {
			mantenimientosService  = (MantenimientosService )contexto.obtenerBean("mantenimientosBusinessImpl");
			resultado = mantenimientosService.exportaExcel(datos,opcion);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosAction, M: exportaExcel");
		}
		return resultado;
	} 
}
