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
import com.webset.set.egresos.service.ConsultaPropuestasService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.service.EquivalenciaEmpresasService;
import com.webset.set.utileriasmod.dto.EquivalenciaEmpresasDto;
import com.webset.utils.tools.Utilerias;

public class EquivalenciaEmpresasAction {
	Contexto contexto = new Contexto();
	Bitacora bitacora = new Bitacora();
	EquivalenciaEmpresasService objEquivalenciaEmpresasService;
	
	@DirectMethod
	public List<EquivalenciaEmpresasDto> llenaComboEmpresas(){
		
		List<EquivalenciaEmpresasDto> listaResultado = new ArrayList<EquivalenciaEmpresasDto>();
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objEquivalenciaEmpresasService = (EquivalenciaEmpresasService)contexto.obtenerBean("objEquivalenciaEmpresasBusinessImpl");
			listaResultado = objEquivalenciaEmpresasService.llenaComboEmpresas();
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: EquivalenciaEmpresasAction, M: llenaComboEmpresas");
		}return listaResultado;
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
			+"P:Utilerias, C:EquivalenciaEmpresasAction, M:obtenerExcel");
		} catch (IOException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:EquivalenciaEmpresasAction, M:obtenerExcel");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:EquivalenciaEmpresasAction, M:obtenerExcel");
		}
		arch.delete();
		return workbook;
	}
	
	@DirectMethod
	public String exportaExcel(String datos) {
		String resp = "";
		try {
			objEquivalenciaEmpresasService = (EquivalenciaEmpresasService)contexto.obtenerBean("objEquivalenciaEmpresasBusinessImpl");
			resp = objEquivalenciaEmpresasService.exportaExcel(datos);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P: Utilerias, C: EquivalenciaEmpresasAction, M: exportaExcel");
		}
		return resp;
	}
	
	@DirectMethod
	public List<EquivalenciaEmpresasDto> llenaGrid (String nomEmpresa){
		List<EquivalenciaEmpresasDto> listaResultado = new ArrayList<EquivalenciaEmpresasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			System.out.println("llega al action");
			objEquivalenciaEmpresasService = (EquivalenciaEmpresasService)contexto.obtenerBean("objEquivalenciaEmpresasBusinessImpl");
			listaResultado = objEquivalenciaEmpresasService.llenaGrid(nomEmpresa);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: EquivalenciaEmpresasAction, M: llenaGrid");
		}return listaResultado;
	}
	
	@DirectMethod
	public String validaDatos(String registro){
		Gson gson = new Gson();
		String resultado = "";	
		List<Map<String, String>> registroEnvio = gson.fromJson(registro, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objEquivalenciaEmpresasService = (EquivalenciaEmpresasService)contexto.obtenerBean("objEquivalenciaEmpresasBusinessImpl");
			resultado = objEquivalenciaEmpresasService.validaDatos(registroEnvio);
			}
		}		
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: EquivalenciaEmpresasAction, M: validaDatos");
		}return resultado;
	}

	@DirectMethod
	public int eliminaRegistro(String codigo, String empresaSet, String empresaInterface){
		int resultado = 0;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objEquivalenciaEmpresasService = (EquivalenciaEmpresasService)contexto.obtenerBean("objEquivalenciaEmpresasBusinessImpl");
			resultado = objEquivalenciaEmpresasService.eliminaRegistro(codigo, empresaSet, empresaInterface);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: EquivalenciaEmpresasAction, M: eliminaRegistro");
		}return resultado;
	}
	
	@DirectMethod
	public String insertaActualizaEmpresa(String registro){
		String resultado = "";
		Gson gson = new Gson();
		List<Map<String, String>> objGrid = gson.fromJson(registro, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		try{	
			if (Utilerias.haveSession(WebContextManager.get())) {
			objEquivalenciaEmpresasService = (EquivalenciaEmpresasService)contexto.obtenerBean("objEquivalenciaEmpresasBusinessImpl");
			resultado = objEquivalenciaEmpresasService.insertaActualizaEmpresa(objGrid);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: EquivalenciaEmpresasAction, M: insertaActualizaEmpresa");
		}return resultado;
	}
}
