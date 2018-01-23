package com.webset.set.bancaelectronica.action;

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
import com.webset.set.bancaelectronica.dto.MovimientosBEDto;
import com.webset.set.bancaelectronica.service.MovimientosBEService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.ComunDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
/**
 * @author Jessica Arelly Cruz Cruz
 * @since 01/03/2011
 */
import com.webset.utils.tools.Utilerias;

/**
* Modificado por YEC
* Se agregan funciones para generar excel
* 21 de diciembre del 2015
*/
public class MovimientosBEAction {
	Contexto contexto=new Contexto();
	Bitacora bitacora=new Bitacora();
	Funciones funciones = new Funciones();
	MovimientosBEService objMovimientosBEService;
	
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboBancos(int noEmpresa){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return list;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMovimientosBEService = (MovimientosBEService)contexto.obtenerBean("objMovimientosBEBusiness");
			list=objMovimientosBEService.llenarComboBancos(noEmpresa);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Bancaelectronica, C:MovimientosBEAction, M:llenarComboBancos");
		}return list;
	}
	
	@DirectMethod
	public List<ComunDto>obtenerConceptos(int noBanco){
		List<ComunDto> list= new ArrayList<ComunDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return list;
		boolean lbGenerico = false;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMovimientosBEService = (MovimientosBEService)contexto.obtenerBean("objMovimientosBEBusiness");
			list=objMovimientosBEService.obtenerConceptos(lbGenerico, noBanco);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Bancaelectronica, C:MovimientosBEAction, M:obtenerConceptos");	
		}
		return list;
	}

	@DirectMethod
	public List<LlenaComboGralDto>llenarComboChequeras(int noBanco, int noEmpresa){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return list;

		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMovimientosBEService = (MovimientosBEService)contexto.obtenerBean("objMovimientosBEBusiness");
			list=objMovimientosBEService.llenarComboChequeras(noBanco, noEmpresa);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Bancaelectronica, C:MovimientosBEAction, M:llenarComboChequeras");	
		}
		return list;
	}
	
	
	@DirectMethod
	public List<MovimientosBEDto> consultaExcel(String sJson){
		List<MovimientosBEDto> objResultado= new ArrayList<MovimientosBEDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return objResultado;
		Gson gson = new Gson();
		List<Map<String, String>> objDatos = gson.fromJson(sJson, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		try{
			objMovimientosBEService = (MovimientosBEService)contexto.obtenerBean("objMovimientosBEBusiness");
			String noEmpresa=(objDatos.get(0).get("noEmpresa")!=null ? objDatos.get(0).get("noEmpresa"):"0");
			String idBanco=(objDatos.get(0).get("noBanco")!=null ? objDatos.get(0).get("noBanco"):"");
			String chequera=(objDatos.get(0).get("chequera")!=null && !objDatos.get(0).get("chequera").equals("") ? objDatos.get(0).get("chequera"):"%");
			String fechaIni=(objDatos.get(0).get("fechaIni")!=null ? objDatos.get(0).get("fechaIni"):"");
			String fechaSup=(objDatos.get(0).get("fechaFin")!=null  ? objDatos.get(0).get("fechaFin"):"");
			String tipoMov=(objDatos.get(0).get("tipoMov")!=null && !objDatos.get(0).get("tipoMov").equals("") ? objDatos.get(0).get("tipoMov"):"%");
			String concepto=(objDatos.get(0).get("concepto")!=null  ? objDatos.get(0).get("concepto"):"%");
			String detalle=(objDatos.get(0).get("detalle")!=null  ? objDatos.get(0).get("detalle"):"false");
			String origenMovimiento=(objDatos.get(0).get("origenMovimiento")!=null  ? objDatos.get(0).get("origenMovimiento"):"0");
			objResultado=objMovimientosBEService.consultaExcel(noEmpresa,idBanco,chequera,fechaIni,fechaSup,tipoMov,concepto,detalle,origenMovimiento);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Bancaelectronica, C:MovimientosBEAction, M:consultaExcel");	
		}
		return objResultado;
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
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Bancaelectronica, C:MovimientosBEAction, M:obtenerExcel");	
		} catch (IOException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Bancaelectronica, C:MovimientosBEAction, M:obtenerExcel");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Bancaelectronica, C:MovimientosBEAction, M:obtenerExcel");
		}
		arch.delete();
		return workbook;
	}
	
	@DirectMethod
	public String exportaExcel(String sJson) {
		String resultado = "";
		if(!Utilerias.haveSession(WebContextManager.get()))
			return resultado;
		try {
			objMovimientosBEService = (MovimientosBEService)contexto.obtenerBean("objMovimientosBEBusiness");
			resultado = objMovimientosBEService.exportaExcel(sJson);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C:MovimientosBEAction , M: exportaExcel");
		}
		return resultado;
	}
	
}