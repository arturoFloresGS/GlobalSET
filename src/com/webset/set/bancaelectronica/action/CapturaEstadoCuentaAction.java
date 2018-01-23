package com.webset.set.bancaelectronica.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.bancaelectronica.business.AdministradorArchivosBusiness;
import com.webset.set.bancaelectronica.business.CapturaEstadoCuentaBusinessImpl;
import com.webset.set.bancaelectronica.business.ImportaBancaElectronicaBusiness;
import com.webset.set.bancaelectronica.dto.CapturaEstadoCuentaDto;
import com.webset.set.bancaelectronica.service.CapturaEstadoCuentaService;
import com.webset.set.personas.dto.ConsultaPersonasDto;
import com.webset.set.personas.service.ConsultaPersonasService;
import com.webset.set.seguridad.dto.EmpresaDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.utils.tools.Utilerias;

public class CapturaEstadoCuentaAction {
	private Contexto contexto = new Contexto();
	private Bitacora bitacora = new Bitacora();
	private Funciones funciones = new Funciones();
	
	//CapturaEstadoCuentaBusinessImpl objCapturaEstadoBusiness;
	CapturaEstadoCuentaService objCapturaEstadoCuentaService;
	GlobalSingleton globalSingleton;

	@DirectMethod
	public List<LlenaComboEmpresasDto> llenaComboEmpresas(int numUsuario){
		System.out.println("Entro a llena combo empresas");
		List<LlenaComboEmpresasDto> listaResultado = new ArrayList<LlenaComboEmpresasDto>();
		 if (!Utilerias.haveSession(WebContextManager.get()))	
			        return null;
		try{
//			if (Utilerias.haveSession(WebContextManager.get())
//					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			
			if (Utilerias.haveSession(WebContextManager.get())) {
			objCapturaEstadoCuentaService = (CapturaEstadoCuentaService)contexto.obtenerBean("objCapturaEstadoCuentaBusinessImpl");
			listaResultado = objCapturaEstadoCuentaService.llenaComboEmpresas(numUsuario);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			//bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: llenaComboTipoPersona");
		}return listaResultado;
	}
	@DirectMethod
	public List<CapturaEstadoCuentaDto> llenaComboTipoBanco(int numEmpresa){
		System.out.println("Entro al action"+numEmpresa);
		List<CapturaEstadoCuentaDto> listaResultado = new ArrayList<CapturaEstadoCuentaDto>();
		if (!Utilerias.haveSession(WebContextManager.get()))	
	        return null;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objCapturaEstadoCuentaService = (CapturaEstadoCuentaService)contexto.obtenerBean("objCapturaEstadoCuentaBusinessImpl");
			listaResultado = objCapturaEstadoCuentaService.llenaComboTipoBanco(numEmpresa);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			//bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: llenaComboTipoPersona");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<CapturaEstadoCuentaDto> llenaComboChequera(int numEmpresa,int numBanco){
		System.out.println("Entro al action llenacombochequera"+numBanco);
		List<CapturaEstadoCuentaDto> listaResultado = new ArrayList<CapturaEstadoCuentaDto>();
		if (!Utilerias.haveSession(WebContextManager.get()))	
	        return null;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objCapturaEstadoCuentaService = (CapturaEstadoCuentaService)contexto.obtenerBean("objCapturaEstadoCuentaBusinessImpl");
			listaResultado = objCapturaEstadoCuentaService.llenaComboChequera(numEmpresa,numBanco);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			//bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: llenaComboTipoPersona");
		}return listaResultado;
	}
	@DirectMethod
	public List<CapturaEstadoCuentaDto> llenaGrid(int numEmpresa,boolean banco,int numBanco,boolean chequera,String numChequera,boolean fecha,String fechaV){
		System.out.println("Entro al action llenaGrid"+fecha);
		List<CapturaEstadoCuentaDto> listaResultado = new ArrayList<CapturaEstadoCuentaDto>();
		if (!Utilerias.haveSession(WebContextManager.get()))	
	        return null;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objCapturaEstadoCuentaService = (CapturaEstadoCuentaService)contexto.obtenerBean("objCapturaEstadoCuentaBusinessImpl");
			listaResultado = objCapturaEstadoCuentaService.llenaGrid(numEmpresa,banco,numBanco,chequera,numChequera,fecha,fechaV);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			//bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: llenaComboTipoPersona");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<CapturaEstadoCuentaDto> llenaComboConcepto(int numBanco){
		System.out.println("Entro a llena combo concepto");
		List<CapturaEstadoCuentaDto> listaResultado = new ArrayList<CapturaEstadoCuentaDto>();
		if (!Utilerias.haveSession(WebContextManager.get()))	
	        return null;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objCapturaEstadoCuentaService = (CapturaEstadoCuentaService)contexto.obtenerBean("objCapturaEstadoCuentaBusinessImpl");
			listaResultado = objCapturaEstadoCuentaService.llenaComboConcepto(numBanco);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			//bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: llenaComboTipoPersona");
		}return listaResultado;
	}

	@DirectMethod
	public String obtenerCargo(String concepto,int numBanco){
		System.out.println("Entro a obtenerCargo");
		String cargo="";
		if (!Utilerias.haveSession(WebContextManager.get()))	
	        return null;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objCapturaEstadoCuentaService = (CapturaEstadoCuentaService)contexto.obtenerBean("objCapturaEstadoCuentaBusinessImpl");
			cargo = objCapturaEstadoCuentaService.obtenerCargo(concepto,numBanco);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			//bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: llenaComboTipoPersona");
		}return cargo;
	}
	
	@DirectMethod
	public String obtenerSalvoBuenCobro(int numBanco,String concepto){
		System.out.println("Entro a salvo");
		String cargo="";
		if (!Utilerias.haveSession(WebContextManager.get()))	
	        return null;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objCapturaEstadoCuentaService = (CapturaEstadoCuentaService)contexto.obtenerBean("objCapturaEstadoCuentaBusinessImpl");
			cargo = objCapturaEstadoCuentaService.obtenerSalvoBuenCobro(numBanco,concepto);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			//bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: llenaComboTipoPersona");
		}return cargo;
	}
	@DirectMethod
	public String obtenerSucursal(int numBanco,String chequera){
		System.out.println("Entro a suc");
		String cargo="";
		if (!Utilerias.haveSession(WebContextManager.get()))	
	        return null;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objCapturaEstadoCuentaService = (CapturaEstadoCuentaService)contexto.obtenerBean("objCapturaEstadoCuentaBusinessImpl");
			cargo = objCapturaEstadoCuentaService.obtenerSucursal(numBanco,chequera);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			//bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: llenaComboTipoPersona");
		}return cargo;
	}
	@DirectMethod
	public String validaCampos(String registro) {
		Gson gson = new Gson();
		List<Map<String, String>> registroGson = gson.fromJson(registro, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		int respuesta=0;
		String mensaje="";
		try{
//			if(funciones.isNumeric(registroGson.get(0).get("sucursal"))==false){
//				mensaje="La sucursal debe ser tipo númerico";
//			}else{
				if(funciones.isNumeric(registroGson.get(0).get("folio"))==false){
					mensaje="El folio debe ser tipo númerico";
				}else{
					if(funciones.isNumeric(registroGson.get(0).get("referencia"))==false){
						mensaje="La referencia debe ser tipo númerico";
					}
				}
//			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			//logger.error(e);
			System.out.println(e.toString());
//			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
//					"P: Personas, C: ConsultaPersonasBusinessImpl, M: llenaComboTipoPersona");
		}
		return mensaje;
	}
	
	@DirectMethod
	public String  guardarNuevoEstado(String registro,String fecha){
		String mensaje = "";
		Gson gson = new Gson();
		System.out.println("action guardar");
		
		List<Map<String, String>> registroGson = gson.fromJson(registro, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		if (!Utilerias.haveSession(WebContextManager.get()))	
	        return null;
		try{
			//System.out.println(registroGson);
			if (Utilerias.haveSession(WebContextManager.get())) {
			objCapturaEstadoCuentaService = (CapturaEstadoCuentaService)contexto.obtenerBean("objCapturaEstadoCuentaBusinessImpl");
			mensaje = objCapturaEstadoCuentaService.guardarNuevoEstado(registroGson,fecha);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			//bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: llenaComboTipoPersona");
		}return mensaje;
	}
	@DirectMethod
	public String  eliminarCapturaEstado(int empresa,int secuencia){
		String mensaje = "";
		Gson gson = new Gson();
		System.out.println("action eliminar");
		if (!Utilerias.haveSession(WebContextManager.get()))	
	        return null;
		try{
			//System.out.println(registroGson);
			if (Utilerias.haveSession(WebContextManager.get())) {
			objCapturaEstadoCuentaService = (CapturaEstadoCuentaService)contexto.obtenerBean("objCapturaEstadoCuentaBusinessImpl");
			mensaje = objCapturaEstadoCuentaService.eliminarEstadoCuenta(empresa,secuencia);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			//bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: llenaComboTipoPersona");
		}return mensaje;
	}
	
	public CapturaEstadoCuentaService getObjCapturaEstadoCuentaService() {
		return objCapturaEstadoCuentaService;
	}

	public void setObjCapturaEstadoCuentaService(CapturaEstadoCuentaService objCapturaEstadoCuentaService) {
		this.objCapturaEstadoCuentaService = objCapturaEstadoCuentaService;
	}

	public GlobalSingleton getGlobalSingleton() {
		return globalSingleton;
	}

	public void setGlobalSingleton(GlobalSingleton globalSingleton) {
		this.globalSingleton = globalSingleton;
	}

}
