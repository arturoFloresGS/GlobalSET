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
import com.webset.set.bancaelectronica.dto.ConsultaOperacionesBEDto;
import com.webset.set.bancaelectronica.service.ConsultaOperacionesBEService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.ComunDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;

/**
* YEC
* 22 de diciembre del 2015
*/
public class ConsultaOperacionesBEAction {
	Contexto contexto=new Contexto();
	Bitacora bitacora=new Bitacora();
	Funciones funciones = new Funciones();
	ConsultaOperacionesBEService objConsultaOperacionesBEService;
	
	@DirectMethod
	public List<ConsultaOperacionesBEDto> llenaGrid(String folioBanco,String idChequera){
		System.out.println("llego al action");
		List<ConsultaOperacionesBEDto> listaResultado = new ArrayList<ConsultaOperacionesBEDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listaResultado;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objConsultaOperacionesBEService = (ConsultaOperacionesBEService)contexto.obtenerBean("objConsultaOperacionesBEBusiness");
			listaResultado = objConsultaOperacionesBEService.llenaGrid(folioBanco, idChequera);
			System.out.println(listaResultado.size());
			}
		}
		catch(Exception e){
			System.out.println("error en llena grid action");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosAction, M: llenaGrid");
		} return listaResultado;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboBancos(int noEmpresa){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return list;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objConsultaOperacionesBEService = (ConsultaOperacionesBEService)contexto.obtenerBean("objConsultaOperacionesBEBusiness");
			list=objConsultaOperacionesBEService.llenarComboBancos(noEmpresa);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Bancaelectronica, C:ConsultaOperacionesBEAction, M:llenarComboBancos");
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
			objConsultaOperacionesBEService = (ConsultaOperacionesBEService)contexto.obtenerBean("objConsultaOperacionesBEBusiness");
			list=objConsultaOperacionesBEService.obtenerConceptos(lbGenerico, noBanco);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Bancaelectronica, C:ConsultaOperacionesBEAction, M:obtenerConceptos");	
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
			objConsultaOperacionesBEService = (ConsultaOperacionesBEService)contexto.obtenerBean("objConsultaOperacionesBEBusiness");
			list=objConsultaOperacionesBEService.llenarComboChequeras(noBanco, noEmpresa);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Bancaelectronica, C:ConsultaOperacionesBEAction, M:llenarComboChequeras");	
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto>llenaComboGrupo(){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return list;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objConsultaOperacionesBEService = (ConsultaOperacionesBEService)contexto.obtenerBean("objConsultaOperacionesBEBusiness");
			list=objConsultaOperacionesBEService.llenaComboGrupo();
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Bancaelectronica, C:ConsultaOperacionesBEAction, M:llenarComboGrupo");	
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto>llenaComboRubro(String idGrupo){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return list;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objConsultaOperacionesBEService = (ConsultaOperacionesBEService)contexto.obtenerBean("objConsultaOperacionesBEBusiness");
			list=objConsultaOperacionesBEService.llenaComboRubro(idGrupo);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Bancaelectronica, C:ConsultaOperacionesBEAction, M:llenaComboRubro");	
		}
		return list;
	}
	
	@DirectMethod
	public List<ConsultaOperacionesBEDto> contabiliza(String idRubro){
		System.out.println("contabiliza en el action"+ idRubro);
		List<ConsultaOperacionesBEDto> list= new ArrayList<ConsultaOperacionesBEDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return list;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objConsultaOperacionesBEService = (ConsultaOperacionesBEService)contexto.obtenerBean("objConsultaOperacionesBEBusiness");
			list=objConsultaOperacionesBEService.contabiliza(idRubro);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Bancaelectronica, C:ConsultaOperacionesBEAction, M:contabiliza");	
		}
		return list;
	}
	
	
	@DirectMethod
	public List<ConsultaOperacionesBEDto> consultaExcel(String sJson){
		List<ConsultaOperacionesBEDto> objResultado= new ArrayList<ConsultaOperacionesBEDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return objResultado;
		Gson gson = new Gson();
		List<Map<String, String>> objDatos = gson.fromJson(sJson, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		try{
			
			objConsultaOperacionesBEService = (ConsultaOperacionesBEService)contexto.obtenerBean("objConsultaOperacionesBEBusiness");
			String noEmpresa=(objDatos.get(0).get("noEmpresa")!=null ? objDatos.get(0).get("noEmpresa"):"0");
			String idBanco=(objDatos.get(0).get("noBanco")!=null ? objDatos.get(0).get("noBanco"):"");
			String chequera=(objDatos.get(0).get("chequera")!=null && !objDatos.get(0).get("chequera").equals("") ? objDatos.get(0).get("chequera"):"%");
			String fechaIni=(objDatos.get(0).get("fechaIni")!=null ? objDatos.get(0).get("fechaIni"):"");
			String fechaSup=(objDatos.get(0).get("fechaFin")!=null  ? objDatos.get(0).get("fechaFin"):"");
			String tipoMov=(objDatos.get(0).get("tipoMov")!=null && !objDatos.get(0).get("tipoMov").equals("") ? objDatos.get(0).get("tipoMov"):"%");
			String concepto=(objDatos.get(0).get("concepto")!=null  ? objDatos.get(0).get("concepto"):"%");
			String detalle=(objDatos.get(0).get("detalle")!=null  ? objDatos.get(0).get("detalle"):"false");
			String origenMovimiento=(objDatos.get(0).get("origenMovimiento")!=null  ? objDatos.get(0).get("origenMovimiento"):"0");
			objResultado=objConsultaOperacionesBEService.consultaExcel(noEmpresa,idBanco,chequera,fechaIni,fechaSup,tipoMov,concepto,detalle,origenMovimiento);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Bancaelectronica, C:ConsultaOperacionesBEAction, M:consultaExcel");	
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
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Bancaelectronica, C:ConsultaOperacionesBEAction, M:obtenerExcel");	
		} catch (IOException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Bancaelectronica, C:ConsultaOperacionesBEAction, M:obtenerExcel");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Bancaelectronica, C:ConsultaOperacionesBEAction, M:obtenerExcel");
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
			objConsultaOperacionesBEService = (ConsultaOperacionesBEService)contexto.obtenerBean("objConsultaOperacionesBEBusiness");
			resultado = objConsultaOperacionesBEService.exportaExcel(sJson);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C:ConsultaOperacionesBEAction , M: exportaExcel");
		}
		return resultado;
	}
	
}