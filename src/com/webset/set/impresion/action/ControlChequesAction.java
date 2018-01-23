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
import com.webset.set.impresion.dto.ControlPapelDto;
import com.webset.set.impresion.service.ControlChequesService;
import com.webset.set.impresion.service.ImpresionService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;

/**
 * @author Eric Medina Serrato
 * @since 21/12/2015 (Casi navidad :D)
 */

public class ControlChequesAction {

	Contexto contexto = new Contexto();
	private Bitacora bitacora = new Bitacora();
	//private Funciones funciones = new Funciones();
	private ControlChequesService controlChequesService;
	
	
	@DirectMethod
	public List<LlenaComboEmpresasDto>llenarComboEmpresa(){
		List<LlenaComboEmpresasDto> listEmp = new ArrayList<LlenaComboEmpresasDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get()) 
				|| !Utilerias.tienePermiso(WebContextManager.get(), 196) )
			return listEmp;
		
		try{
			controlChequesService = (ControlChequesService)contexto.obtenerBean("controlChequesBusinessImpl");
			listEmp = controlChequesService.llenarComboEmpresa();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ControlChequesAction, M:llenarComboEmpresa");
		}
		return listEmp;
	}
	 
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboBanco(){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 196))
			return list;
		
		try{
			controlChequesService = (ControlChequesService)contexto.obtenerBean("controlChequesBusinessImpl");
			list = controlChequesService.llenarComboBanco();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ControlChequesAction, M:llenarComboBancoPag");	
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboChequera(int idBanco, int noEmpresa) {
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 196))
			return list;
		
		try{
			controlChequesService = (ControlChequesService)contexto.obtenerBean("controlChequesBusinessImpl");
			list = controlChequesService.llenarComboChequera(idBanco, noEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ControlChequesAction, M:llenarComboChequera");	
		}
		return list;
	}
	
	@DirectMethod
	public Map<String, Object> guardarControlCheque(String jsonControlCheques){
		
		Map<String,Object> result= new HashMap<String,Object>();
		
		result.put("error","");
		result.put("mensaje","");
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 196))
			return result;
		
		if(jsonControlCheques.equals("")){
			result.put("error", "Error al guardar, datos vacíos.");
			return result;
		}

		Gson gson = new Gson();

		List<Map<String, String>> objParams = gson.fromJson(jsonControlCheques, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
		try {
			controlChequesService = (ControlChequesService)contexto.obtenerBean("controlChequesBusinessImpl");
			
			ControlPapelDto controlPapel = new ControlPapelDto();
			
			if(objParams.size() > 0){
				controlPapel.setCajaClave(1);
				controlPapel.setIdBanco((objParams.get(0).get("idBanco") != null && !objParams.get(0).get("idBanco").equals(""))?Integer.parseInt(objParams.get(0).get("idBanco")):0);
				controlPapel.setFolioInvIni((objParams.get(0).get("folioIni") != null && !objParams.get(0).get("folioIni").equals(""))?Integer.parseInt(objParams.get(0).get("folioIni")):0);
				controlPapel.setFolioInvFin((objParams.get(0).get("folioFin") != null && !objParams.get(0).get("folioFin").equals(""))?Integer.parseInt(objParams.get(0).get("folioFin")):0);
				controlPapel.setFolioUltImpreso(controlPapel.getFolioInvIni()-1); //CUANDO SE DÉ DE ALTA, SIEMPRE SERÁ -1 AL FOLIO INI
				controlPapel.setIdChequera(objParams.get(0).get("idChequera") != null?objParams.get(0).get("idChequera"):"");
				controlPapel.setNoEmpresa((objParams.get(0).get("noEmpresa") != null && !objParams.get(0).get("noEmpresa").equals(""))?Integer.parseInt(objParams.get(0).get("noEmpresa")):0);
				if(objParams.get(0).get("tipoFolio") != null){
					if(objParams.get(0).get("tipoFolio").equals("0")){
						controlPapel.setTipoFolio("P"); //Papel
					}else{
						controlPapel.setTipoFolio("C");	//Cheque
					}
				}
				controlPapel.setStock((objParams.get(0).get("stock") != null && !objParams.get(0).get("stock").equals(""))?Integer.parseInt(objParams.get(0).get("stock")):0);
			}
			
			result = controlChequesService.guardarControlCheque(controlPapel);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresión, C:ControlChequesAction, M:guardarControlCheque");
		}
		return result;
		
	}
	
	
	
	@DirectMethod
	public List<ControlPapelDto> consultarCheques(int noEmpresa,int idBanco, String idChequera, boolean folioPapel, boolean estatusChequeras, boolean estatusChequerasT){
		
		List<ControlPapelDto>list= new ArrayList<ControlPapelDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 196))
			return list;
		
		ControlPapelDto controlPapel = new ControlPapelDto();
		
		try {
			controlChequesService = (ControlChequesService)contexto.obtenerBean("controlChequesBusinessImpl");
			
			controlPapel.setNoEmpresa(noEmpresa);
			controlPapel.setIdBanco(idBanco);
			controlPapel.setIdChequera(idChequera);
			
			list = controlChequesService.consultarCheques(controlPapel, folioPapel, estatusChequeras, estatusChequerasT);
			 
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresión, C:ControlChequesAction, M:consultarCheques");	
		}
		
		return list;
	}
	
	@DirectMethod
	public Map<String, Object> eliminarControlCheque(String jsonControlCheques){
		
		Map<String,Object> result= new HashMap<String,Object>();
		
		result.put("error","");
		result.put("mensaje","");
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 196))
			return result;
		
		if(jsonControlCheques.equals("")){
			result.put("error", "Error al eliminar, datos vacíos.");
			return result;
		}

		Gson gson = new Gson();

		List<Map<String, String>> objParams = gson.fromJson(jsonControlCheques, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
		try {
			controlChequesService = (ControlChequesService)contexto.obtenerBean("controlChequesBusinessImpl");
			
			ControlPapelDto controlPapel = new ControlPapelDto();
			
			if(objParams.size() > 0){
				controlPapel.setIdControlCheque((objParams.get(0).get("idControlCheque") != null && !objParams.get(0).get("idControlCheque").equals(""))?Integer.parseInt(objParams.get(0).get("idControlCheque")):0);
				controlPapel.setIdBanco((objParams.get(0).get("idBanco") != null && !objParams.get(0).get("idBanco").equals(""))?Integer.parseInt(objParams.get(0).get("idBanco")):0);
				controlPapel.setIdChequera(objParams.get(0).get("idChequera") != null?objParams.get(0).get("idChequera"):"");
				controlPapel.setNoEmpresa((objParams.get(0).get("noEmpresa") != null && !objParams.get(0).get("noEmpresa").equals(""))?Integer.parseInt(objParams.get(0).get("noEmpresa")):0);
				if(objParams.get(0).get("tipoFolio") != null){
					if(objParams.get(0).get("tipoFolio").equals("0")){
						controlPapel.setTipoFolio("P"); //Papel
					}else{
						controlPapel.setTipoFolio("C");	//Cheque
					}
				}
			}
			
			result = controlChequesService.eliminarControlCheque(controlPapel);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresión, C:ControlChequesAction, M:eliminarControlCheque");
		}
		return result;
	}
	
	@DirectMethod
	public Map<String, Object> modificarControlCheque(String jsonOrig, String jsonControlCheques){
		
		Map<String,Object> result= new HashMap<String,Object>();
		
		result.put("error","");
		result.put("mensaje","");
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 196))
			return result;
		
		if(jsonOrig.equals("")){
			result.put("error", "Error al modificar, registro original con datos vacios.");
			return result;
		}
		
		if(jsonControlCheques.equals("")){
			result.put("error", "Error al modificar, datos vacíos.");
			return result;
		}

		Gson gson = new Gson();
		List<Map<String, String>> objOrig = gson.fromJson(jsonOrig, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<Map<String, String>> objParams = gson.fromJson(jsonControlCheques, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
		
		try {
			controlChequesService = (ControlChequesService)contexto.obtenerBean("controlChequesBusinessImpl");
			
			ControlPapelDto controlPapelOriginal = new ControlPapelDto();
			ControlPapelDto controlPapel = new ControlPapelDto();
			
			if(objOrig.size() > 0){
				controlPapelOriginal.setIdControlCheque((objOrig.get(0).get("idControlCheque") != null && !objOrig.get(0).get("idControlCheque").equals(""))?Integer.parseInt(objOrig.get(0).get("idControlCheque")):0);
				controlPapelOriginal.setIdBanco((objOrig.get(0).get("idBanco") != null && !objOrig.get(0).get("idBanco").equals(""))?Integer.parseInt(objOrig.get(0).get("idBanco")):0);
				controlPapelOriginal.setIdChequera(objOrig.get(0).get("idChequera") != null?objOrig.get(0).get("idChequera"):"");
				controlPapelOriginal.setNoEmpresa((objOrig.get(0).get("noEmpresa") != null && !objOrig.get(0).get("noEmpresa").equals(""))?Integer.parseInt(objOrig.get(0).get("noEmpresa")):0);
				controlPapelOriginal.setTipoFolio(objOrig.get(0).get("tipoFolio") != null ? objOrig.get(0).get("tipoFolio"):"");
			}
			
			if(objParams.size() > 0){
				controlPapel.setCajaClave(1);
				controlPapel.setIdBanco((objParams.get(0).get("idBanco") != null && !objParams.get(0).get("idBanco").equals(""))?Integer.parseInt(objParams.get(0).get("idBanco")):0);
				controlPapel.setFolioInvIni((objParams.get(0).get("folioIni") != null && !objParams.get(0).get("folioIni").equals(""))?Integer.parseInt(objParams.get(0).get("folioIni")):0);
				controlPapel.setFolioInvFin((objParams.get(0).get("folioFin") != null && !objParams.get(0).get("folioFin").equals(""))?Integer.parseInt(objParams.get(0).get("folioFin")):0);
				controlPapel.setFolioUltImpreso((objParams.get(0).get("folioUltImp") != null && !objParams.get(0).get("folioUltImp").equals(""))?Integer.parseInt(objParams.get(0).get("folioUltImp")):0); //Posiblemente sea 0, ó folioIni -1.
				controlPapel.setIdChequera(objParams.get(0).get("idChequera") != null?objParams.get(0).get("idChequera"):"");
				controlPapel.setNoEmpresa((objParams.get(0).get("noEmpresa") != null && !objParams.get(0).get("noEmpresa").equals(""))?Integer.parseInt(objParams.get(0).get("noEmpresa")):0);
				if(objParams.get(0).get("tipoFolio") != null){
					if(objParams.get(0).get("tipoFolio").equals("0")){
						controlPapel.setTipoFolio("P"); //Papel
					}else{
						controlPapel.setTipoFolio("C");	//Cheque
					}
				}
				controlPapel.setStock((objParams.get(0).get("stock") != null && !objParams.get(0).get("stock").equals(""))?Integer.parseInt(objParams.get(0).get("stock")):0);
			}
			
			result = controlChequesService.modificarControlCheque(controlPapelOriginal, controlPapel);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresión, C:ControlChequesAction, M:modificarControlCheque");
		}
		return result;
	}
	
	@DirectMethod
	public String exportaExcel(String datos, String titulo) {
		String resp = "";
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 196))
			return resp;
		
		try {
			controlChequesService = (ControlChequesService)contexto.obtenerBean("controlChequesBusinessImpl");
			resp = controlChequesService.exportaExcel(datos, titulo);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresión, C:ControlChequesAction, M:exportaExcel");
		}
		return resp;
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
			+"P:Impresión, C:ControlChequesAction, M:obtenerExcel");
		} catch (IOException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresión, C:ControlChequesAction, M:obtenerExcel");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresión, C:ControlChequesAction, M:obtenerExcel");
		}
		arch.delete();
		return workbook;
	}
	
	
}
