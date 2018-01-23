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
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.service.MantenimientoSolicitantesFirmantesService;
import com.webset.set.utileriasmod.dto.MantenimientoSolicitantesFirmantesDto;
import com.webset.utils.tools.Utilerias;

public class MantenimientoSolicitantesFirmantesAction {
	Contexto contexto = new Contexto();
	Bitacora bitacora = new Bitacora();
	MantenimientoSolicitantesFirmantesService objMantenimientoSolicitantesFirmantesService;
		 
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboPersonas(){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMantenimientoSolicitantesFirmantesService = (MantenimientoSolicitantesFirmantesService)contexto.obtenerBean("objMantenimientoSolicitantesFirmantesBusinessImpl");
			list=objMantenimientoSolicitantesFirmantesService.llenarComboPersonas();
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Utilerias, C:MantenimientoSolicitantesFirmantesAction, M:llenarComboNoFirmantes");
		}return list;
	}
	
	@DirectMethod
	public List<MantenimientoSolicitantesFirmantesDto> llenaGridSolicitantesFirmantes(String tipoPersona){
		List<MantenimientoSolicitantesFirmantesDto> listaResultado = new ArrayList<MantenimientoSolicitantesFirmantesDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMantenimientoSolicitantesFirmantesService = (MantenimientoSolicitantesFirmantesService)contexto.obtenerBean("objMantenimientoSolicitantesFirmantesBusinessImpl");
			listaResultado = objMantenimientoSolicitantesFirmantesService.llenaGridSolicitantesFirmantes(tipoPersona);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesFirmantesAction, M: llenaGridSolicitantesFirmantes");
		} return listaResultado;
	}
		
	@DirectMethod
	public String insertaMantenimientoSolicitantesFirmantes(String sJson){
		String resultado="";
		Gson gson = new Gson();
		MantenimientoSolicitantesFirmantesDto dto=new MantenimientoSolicitantesFirmantesDto();
		List<Map<String, String>> objDatos = gson.fromJson(sJson, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		int t;	
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMantenimientoSolicitantesFirmantesService = (MantenimientoSolicitantesFirmantesService)contexto.obtenerBean("objMantenimientoSolicitantesFirmantesBusinessImpl");
			for(t=0; t<objDatos.size(); t++) {
				dto.setIdPersona(objDatos.get(t).get("idPersona")!=null ? objDatos.get(t).get("idPersona"):"");
				dto.setNombre(objDatos.get(t).get("nombre")!=null ? objDatos.get(t).get("nombre"):"" );
				dto.setPuesto(objDatos.get(t).get("puesto")!=null ? objDatos.get(t).get("puesto"):"" );

dto.setTipoPersona(objDatos.get(t).get("tipo")!=null ? objDatos.get(t).get("tipo"):"" );
				dto.setFecAlta(objDatos.get(t).get("fecAlta")!=null ? objDatos.get(t).get("fecAlta"):"" );
				dto.setUsuarioAlta(objDatos.get(t).get("usuarioAlta")!=null ? objDatos.get(t).get("usuarioAlta"):"" );



			}
			resultado = objMantenimientoSolicitantesFirmantesService.insertaMantenimientoSolicitantesFirmantes(dto);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesFirmantesAction, M: llenaGridSolicitantesFirmantes");
		} return resultado;	
	}
		
	@DirectMethod
	public String updateMantenimientoSolicitantesFirmantes(String sJson){
		String resultado="";
		Gson gson = new Gson();
		MantenimientoSolicitantesFirmantesDto dto=new MantenimientoSolicitantesFirmantesDto();
		List<Map<String, String>> objDatos = gson.fromJson(sJson, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		int t;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMantenimientoSolicitantesFirmantesService = (MantenimientoSolicitantesFirmantesService)contexto.obtenerBean("objMantenimientoSolicitantesFirmantesBusinessImpl");
			for(t=0; t<objDatos.size(); t++) {
				dto.setIdPersona(objDatos.get(t).get("idPersona")!=null ? objDatos.get(t).get("idPersona"):"");
				dto.setNombre(objDatos.get(t).get("nombre")!=null ? objDatos.get(t).get("nombre"):"" );
				dto.setPuesto(objDatos.get(t).get("puesto")!=null ? objDatos.get(t).get("puesto"):"" );
				dto.setTipoPersona(objDatos.get(t).get("tipo")!=null ? objDatos.get(t).get("tipo"):"" );
							}
			resultado = objMantenimientoSolicitantesFirmantesService.updateMantenimientoSolicitantesFirmantes(dto);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesFirmantesAction, M: updateMantenimientoSolicitantesFirmantes");
			
		} return resultado;	
	}
		
	@DirectMethod
	public String deleteMantenimientoSolicitantesFirmantes(String sJson){
		String resultado="";
		Gson gson = new Gson();
		MantenimientoSolicitantesFirmantesDto dto=new MantenimientoSolicitantesFirmantesDto();
		List<Map<String, String>> objDatos = gson.fromJson(sJson, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		int t;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMantenimientoSolicitantesFirmantesService = (MantenimientoSolicitantesFirmantesService)contexto.obtenerBean("objMantenimientoSolicitantesFirmantesBusinessImpl");
			for(t=0; t<objDatos.size(); t++) {
				dto.setIdPersona(objDatos.get(t).get("idPersona")!=null ? objDatos.get(t).get("idPersona"):"");
				
			}
			resultado = objMantenimientoSolicitantesFirmantesService.deleteMantenimientoSolicitantesFirmantes(dto);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesFirmantesAction, M: deleteMantenimientoSolicitantesFirmantes");
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
			+"P:Utilerias, C:MantenimientoSolicitantesFirmantesAction, M:obtenerExcel");
		} catch (IOException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:MantenimientoSolicitantesFirmantesAction, M:obtenerExcel");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:MantenimientoSolicitantesFirmantesAction, M:obtenerExcel");
		}
		arch.delete();
		return workbook;
	}
	
	@DirectMethod
	public String exportaExcel(String datos) {
		String resultado = "";
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMantenimientoSolicitantesFirmantesService  = (MantenimientoSolicitantesFirmantesService )contexto.obtenerBean("objMantenimientoSolicitantesFirmantesBusinessImpl");
			resultado = objMantenimientoSolicitantesFirmantesService .exportaExcel(datos);
			}
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C:MantenimientoBitacoraRentasAction, M: exportaExcel");
		}
		return resultado;
	}
		
}
