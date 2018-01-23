package com.webset.set.interfaz.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.interfaz.dto.CargaPagosDto;
import com.webset.set.interfaz.service.CargaPagosService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.utils.tools.Utilerias;

public class CargaPagosAction 
{
	Bitacora bitacora;
	Contexto contexto = new Contexto();
	CargaPagosService objCargaPagosService;
	
	@DirectMethod
	public List<CargaPagosDto> llenaComboEmpresas(int idUsuario)
	{
		//Se crea un objeto de tipo Lista de la clase DTO
		List<CargaPagosDto> objEmpresa = new ArrayList<CargaPagosDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return objEmpresa;	
		try{
			//Invoca al Bean q se encuentra en el aplication context
			// crea objeto de la clase SERVICE y se invoca con el objeto del Business
			//CargaPagosService objCargaPagosService = (CargaPagosService)contexto.obtenerBean("objCargaPagosBusinessImpl");
			objCargaPagosService = (CargaPagosService)contexto.obtenerBean("objCargaPagosBusinessImpl");
			objEmpresa = objCargaPagosService.llenaComboEmpresas(idUsuario);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Interfaz, C:CargaPagosAction, M:llenaComboEmpresas");	
		}
		return objEmpresa;
	}
		
	@DirectMethod
	public List<CargaPagosDto> llenaGrid(int iTipoPago, String fecHoy, int usuarioAlta, String noEmpresa, String estatus, String fecini, String fecFin)
	{
		List<CargaPagosDto> objTipoPago = new ArrayList<CargaPagosDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return objTipoPago;
		try{
			objCargaPagosService = (CargaPagosService)contexto.obtenerBean("objCargaPagosBusinessImpl");
			objTipoPago = objCargaPagosService.llenaGrid(iTipoPago, fecHoy, usuarioAlta, noEmpresa, estatus, fecini, fecFin);			
		}catch(Exception e){			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosAction, M:llenaGrid");
		}
		return objTipoPago;
	}
	
	@DirectMethod
	public HSSFWorkbook reporteInterfaces(String tipoValor, String fecHoy, String usuarioAlta, String idEmpresa, String estatus, String fecIni, String fecFin, ServletContext context){
		HSSFWorkbook wb=null;
		try {
			objCargaPagosService = (CargaPagosService)contexto.obtenerBean("objCargaPagosBusinessImpl", context);
			wb=objCargaPagosService.reporteInterfaces(tipoValor, fecHoy, usuarioAlta, idEmpresa, estatus, fecIni, fecFin);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: reportePersonas");
		}return wb;
	}
	
	@DirectMethod	
	public String insertaRegistros(String gridDatos, int iTipoPago, String empresa)
	{
		String cadenaRespuesta = "";
		Gson gson = new Gson();
		List<Map<String, String>> objCamposGrid = gson.fromJson(gridDatos, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		if(!Utilerias.haveSession(WebContextManager.get()))
			return cadenaRespuesta;
		try
		{
			objCargaPagosService = (CargaPagosService)contexto.obtenerBean("objCargaPagosBusinessImpl");			
			if(iTipoPago == 1) //Opcion para SAP cargando CXP desde archivo
				cadenaRespuesta = objCargaPagosService.insertaRegistros(objCamposGrid);
			else if(iTipoPago == 2) //Opcion para SAE importa todo lo que este en zimp_fact en estatus "P" y "R"
				cadenaRespuesta = objCargaPagosService.insertaRegistrosCXP(empresa);
				
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosAction, M:insertaRegistrosZimpFact");			
		}return cadenaRespuesta;		
	}
	
	@DirectMethod	
	public String armaPropuesta(String gridDatos) {
		String cadenaRespuesta = "";
		Gson gson = new Gson();
		List<Map<String, String>> objCamposGrid = gson.fromJson(gridDatos, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		if(!Utilerias.haveSession(WebContextManager.get()))
			return cadenaRespuesta;
		try {
			objCargaPagosService = (CargaPagosService)contexto.obtenerBean("objCargaPagosBusinessImpl");			
			cadenaRespuesta = objCargaPagosService.armaPropuesta(objCamposGrid);
				
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosAction, M:insertaRegistrosZimpFact");			
		}
		return cadenaRespuesta;
	}
	
	@DirectMethod
	public int validaFacultad(int facultad) {
		int resp = 0;
		if(!Utilerias.haveSession(WebContextManager.get()))
			return resp;
		try {
			objCargaPagosService = (CargaPagosService)contexto.obtenerBean("objCargaPagosBusinessImpl");			
			resp = objCargaPagosService.validaFacultad(facultad);
				
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosAction, M:validaFaculta");			
		}
		return resp;
	}
	
	//********************  Metodos para la carga de las cuentas por cobrar CXC  ********************************
	//***********************************************************************************************************
	
	@DirectMethod
	public List<CargaPagosDto> llenaGridCXC(int noEmpresa, String estatus){
		List<CargaPagosDto> objTipoPago = new ArrayList<CargaPagosDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return objTipoPago;
		try {
			objCargaPagosService = (CargaPagosService)contexto.obtenerBean("objCargaPagosBusinessImpl");
			objTipoPago = objCargaPagosService.llenaGridCXC(noEmpresa, estatus);			
		}
		catch(Exception e)
		{			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosAction, M:llenaGrid");
		}
		return objTipoPago;
	}
	
	@DirectMethod	
	public String insertaRegistrosCXC(String gridDatos)
	{
		String cadenaRespuesta = "";
		Gson gson = new Gson();
		List<Map<String, String>> objCamposGrid = gson.fromJson(gridDatos, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		if(!Utilerias.haveSession(WebContextManager.get()))
			return cadenaRespuesta;
		try {
			objCargaPagosService = (CargaPagosService)contexto.obtenerBean("objCargaPagosBusinessImpl");			
			cadenaRespuesta = objCargaPagosService.insertaRegistrosCXC(objCamposGrid);
		}
		catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosAction, M:insertaRegistrosZimpFact");			
		}return cadenaRespuesta;		
	}
}
