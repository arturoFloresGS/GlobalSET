package com.webset.set.utilerias.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.personas.service.ConsultaPersonasService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.service.MantenimientoChequerasService;
import com.webset.set.utileriasmod.dto.MantenimientoChequerasDto;
import com.webset.utils.tools.Utilerias;

public class MantenimientoChequerasAction 
{
	Contexto contexto = new Contexto();
	Bitacora bitacora = new Bitacora();
	MantenimientoChequerasService objMantenimientoChequerasService;
		
	@DirectMethod
	public List<LlenaComboEmpresasDto> obtenerEmpresas(int idUsuario)
	{
		List<LlenaComboEmpresasDto> listaResultado = new ArrayList<LlenaComboEmpresasDto>();
				
		try
		{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMantenimientoChequerasService = (MantenimientoChequerasService)contexto.obtenerBean("objMantenimientoChequerasBusinessImpl");
			listaResultado = objMantenimientoChequerasService.obtenerEmpresas(idUsuario);
			}
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoChequerasAction, M:obtenerEmpresas");
		}
		return listaResultado;
	}
	
	
	@DirectMethod
	public List<MantenimientoChequerasDto> obtenerBancos(int noEmpresa)
	{
		List <MantenimientoChequerasDto> listaResultado = new ArrayList<MantenimientoChequerasDto>();
		try
		{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMantenimientoChequerasService = (MantenimientoChequerasService)contexto.obtenerBean("objMantenimientoChequerasBusinessImpl");
			listaResultado = objMantenimientoChequerasService.obtenerBancos(noEmpresa);
			}
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoChequerasAction, M:obtenerBancos");
		}
		return listaResultado;
	}
	
	
	@DirectMethod
	public List<MantenimientoChequerasDto> llenaGrid(int noEmpresa, int idBanco)
	{
		List<MantenimientoChequerasDto> listaResultado = new ArrayList<MantenimientoChequerasDto>();
		
		try
		{
			if (Utilerias.haveSession(WebContextManager.get())) {
			MantenimientoChequerasService objMantenimientoChequerasService = (MantenimientoChequerasService)contexto.obtenerBean("objMantenimientoChequerasBusinessImpl");
			listaResultado = objMantenimientoChequerasService.llenaGrid(noEmpresa, idBanco);
			System.out.println(listaResultado.size() + "tama�o del grid en action");
			}
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoChequerasAction, M:llenaGrid");
		}return listaResultado;
	}
	
	@DirectMethod
	public HSSFWorkbook reporteChequeras(String tipoChequera, String empresa, ServletContext context){
		HSSFWorkbook wb=null;
		System.out.println(tipoChequera);
		try {
			objMantenimientoChequerasService = (MantenimientoChequerasService)contexto.obtenerBean("objMantenimientoChequerasBusinessImpl",context);
			wb=objMantenimientoChequerasService.reporteChequeras(tipoChequera, empresa);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: reportePersonas");
		}return wb;
	}
	
	@DirectMethod
	public List<MantenimientoChequerasDto> obtieneTipoChequeras()
	{
		List<MantenimientoChequerasDto> listaResultado = new ArrayList<MantenimientoChequerasDto>();
		
		try
		{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMantenimientoChequerasService = (MantenimientoChequerasService)contexto.obtenerBean("objMantenimientoChequerasBusinessImpl");
			listaResultado = objMantenimientoChequerasService.obtieneTipoChequeras();	
			}
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + "" + e.toString() + "P:Utilerias, C:MantenimientoChequerasAction, M:obtieneTipoChequeras");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<MantenimientoChequerasDto> llenaComboGrupo()
	{
		List<MantenimientoChequerasDto> listaResultado = new ArrayList<MantenimientoChequerasDto>();
		
		try
		{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMantenimientoChequerasService = (MantenimientoChequerasService)contexto.obtenerBean("objMantenimientoChequerasBusinessImpl");
			listaResultado = objMantenimientoChequerasService.llenaComboGrupo();
			}
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + "" + e.toString() + "P:Utilerias, C:MantenimientoChequerasAction, M:llenaComboGrupo");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<MantenimientoChequerasDto> obtenerBancosTodos(int bancoNacional)
	{
		List <MantenimientoChequerasDto> listaResultado = new ArrayList<MantenimientoChequerasDto>();
		try
		{			
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMantenimientoChequerasService = (MantenimientoChequerasService)contexto.obtenerBean("objMantenimientoChequerasBusinessImpl");
			listaResultado = objMantenimientoChequerasService.obtenerBancosTodos(bancoNacional);
			}
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoChequerasAction, M:obtenerBancosTodos");
		}
		return listaResultado;
	}
	
	@DirectMethod
	public List<MantenimientoChequerasDto> llenaComboDivision (int noEmpresa)
	{
		List<MantenimientoChequerasDto> listaResultado = new ArrayList<MantenimientoChequerasDto>();
		try
		{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMantenimientoChequerasService = (MantenimientoChequerasService)contexto.obtenerBean("objMantenimientoChequerasBusinessImpl");
			listaResultado = objMantenimientoChequerasService.llenaComboDivision(noEmpresa);
			}
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + "" + e.toString() + "P:Utilerias, C:MantenimientoChequerasAction, M:llenaComboDivision");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<MantenimientoChequerasDto> llenaComboDivisa()
	{
		List<MantenimientoChequerasDto> listaResultado = new ArrayList<MantenimientoChequerasDto>();
		
		try{			
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMantenimientoChequerasService = (MantenimientoChequerasService)contexto.obtenerBean("objMantenimientoChequerasBusinessImpl");
			listaResultado = objMantenimientoChequerasService.llenaComboDivisa();
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoChequerasAction, M:llenaComboDivisa");
		}return listaResultado;
		
	}
	
	@DirectMethod
	public String configuraSet(int indice)
	{
		String resultado = "";
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMantenimientoChequerasService = (MantenimientoChequerasService)contexto.obtenerBean("objMantenimientoChequerasBusinessImpl");
			resultado = objMantenimientoChequerasService.configuraSet(indice);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + "" + e.toString() + "P:Utilerias, C:MantenimientoChequerasAction, M:configuraSet");
		}return resultado;
	}
	
	@DirectMethod
	public int eliminaChequeras(int noEmpresa, int idBanco, String idChequera, int noUsuario)
	{
		int resultado = 0;
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMantenimientoChequerasService = (MantenimientoChequerasService)contexto.obtenerBean("objMantenimientoChequerasBusinessImpl");
			resultado = objMantenimientoChequerasService.eliminaChequeras(noEmpresa, idBanco, idChequera, noUsuario);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + "" + e.toString() + "P:Utilerias, C:MantenimientoChequerasAction, M:eliminaChequeras");
		}return resultado;
	}
	
	@DirectMethod
	public String aceptar(String registro)
	{
		System.out.println("Llega Aceptar");
		/*
		 * 		Gson gson = new Gson();
		List<Map<String, String>> objCamposGrid = gson.fromJson(gridDatos, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		
		 */
		Gson gson = new Gson();
		List<Map<String, String>> matRegistro = gson.fromJson(registro, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		
		String resultado = "";
		try{			
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMantenimientoChequerasService = (MantenimientoChequerasService)contexto.obtenerBean("objMantenimientoChequerasBusinessImpl");
			resultado = objMantenimientoChequerasService.aceptar(matRegistro);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + "" + e.toString() + "P:Utilerias, C:MantenimientoChequerasAction, M:aceptar"); 
		}return resultado;
	}
	
	@DirectMethod
	public Map<String, Object> guardarChequera(String jString){
		Map<String, Object> resultado= new HashMap<String, Object>();
		resultado.put("Status", false);
		resultado.put("msg", "error desconocido");
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMantenimientoChequerasService = (MantenimientoChequerasService)contexto.obtenerBean("objMantenimientoChequerasBusinessImpl");
			resultado = objMantenimientoChequerasService.guardarChequera(jString);
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoChequerasAction, M: guardarChequera");
		}
		return resultado;
		
		
	}
	
	@DirectMethod
	public List<MantenimientoChequerasDto> obtieneCajas(){
		List<MantenimientoChequerasDto> listaResultado = new ArrayList<MantenimientoChequerasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			System.out.println("Entra al action de cajas");
			objMantenimientoChequerasService = (MantenimientoChequerasService)contexto.obtenerBean("objMantenimientoChequerasBusinessImpl");
			listaResultado = objMantenimientoChequerasService.obtieneCajas();
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, MantenimientoChequerasAction, obtieneCajas");
		}return listaResultado;		
	}
		
	@DirectMethod
	public String facultadDeModificarChequera(int noUsuario, String indice){
		String mensaje = "";
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMantenimientoChequerasService = (MantenimientoChequerasService)contexto.obtenerBean("objMantenimientoChequerasBusinessImpl");
			mensaje = objMantenimientoChequerasService.facultadDeModificarChequera(noUsuario, indice);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoChequerasAction, M: facultadDeModificarChequera");
		}return mensaje;
	}
}
