package com.webset.set.inversiones.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import net.sf.jasperreports.engine.JRDataSource;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.inversiones.dto.CanastaInversionesDto;
import com.webset.set.inversiones.middleware.service.BarridoInversionService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.utils.tools.Utilerias;

public class BarridoInversionAction {
	private Contexto contexto = new Contexto();
	private Bitacora bitacora = new Bitacora();
	
	@DirectMethod
	public List<CanastaInversionesDto> empresasConcentradoras() {
		List<CanastaInversionesDto> listEmpresas = new ArrayList<CanastaInversionesDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listEmpresas;
		try {
			BarridoInversionService barridoInversionService = (BarridoInversionService) contexto.obtenerBean("barridoInversionBusinessImpl");
			listEmpresas = barridoInversionService.empresasConcentradoras();
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:BarridoInversionAction, M:empresasConcentradoras");
		}
		return listEmpresas;
	}
	
	@DirectMethod
	public List<CanastaInversionesDto> todosLosBancos(String idDivisa) {
		List<CanastaInversionesDto> listBancos = new ArrayList<CanastaInversionesDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listBancos;
		try {
			BarridoInversionService barridoInversionService = (BarridoInversionService) contexto.obtenerBean("barridoInversionBusinessImpl");
			listBancos = barridoInversionService.todosLosBancos(idDivisa);
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:BarridoInversionAction, M:todosLosBancos");
		}
		return listBancos;
	}
	
	@DirectMethod
	public List<CanastaInversionesDto> obtenerRegistros(String idDivisa, String noBanco) {
		List<CanastaInversionesDto> listBancos = new ArrayList<CanastaInversionesDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listBancos;
		try {
			BarridoInversionService barridoInversionService = (BarridoInversionService) contexto.obtenerBean("barridoInversionBusinessImpl");
			listBancos = barridoInversionService.obtenerRegistros(idDivisa, noBanco);
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:BarridoInversionAction, M:obtenerRegistros");
		}
		return listBancos;
	}

	@DirectMethod
	public String insertarBarridos(String params) {
		String msg = "";
		if(!Utilerias.haveSession(WebContextManager.get()))
			return msg;
		try {
			msg = validaDatos(params);
			
			if(msg.equals("")) {
				BarridoInversionService barridoInversionService = (BarridoInversionService) contexto.obtenerBean("barridoInversionBusinessImpl");
				msg = barridoInversionService.insertarBarridos(params);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:BarridoInversionAction, M:insertarBarridos");
		}
		return msg;
	}

	public String validaDatos(String params) {
		String msg = "";
		Gson gson = new Gson();
		List<Map<String, String>> datosGrid = gson.fromJson(params, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		if(!Utilerias.haveSession(WebContextManager.get()))
			return msg;
		try {
			for(int i=0; i<datosGrid.size(); i++) {
				if(datosGrid.get(i).get("idChequera").equals(""))
					return "Error al obtener la cuenta bancaria del grid";
				if(datosGrid.get(i).get("noEmpresaCon").equals(""))
					return "Error al obtener la empresa concentradora";
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:BarridoInversionAction, M:validaDatos");
		}
		return msg;
	}
	
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//++++++++++++++++++++++++++ PROCESO DE EJECUCION DE BARRIDO INVERSION +++++++++++++++++++++++++++
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	
	@DirectMethod
	public List<CanastaInversionesDto> obtenerSolicitudesBarrido(String idDivisa, String noBanco) {
		List<CanastaInversionesDto> listDatos = new ArrayList<CanastaInversionesDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listDatos;
		try {
			BarridoInversionService barridoInversionService = (BarridoInversionService) contexto.obtenerBean("barridoInversionBusinessImpl");
			listDatos = barridoInversionService.obtenerSolicitudesBarrido(idDivisa, noBanco);
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:BarridoInversionAction, M:obtenerSolicitudesBarrido");
		}
		return listDatos;
	}

	@DirectMethod
	public String confirmaBarrido(String noSolicitudes, String opcion, String clave) {
		String result = "";
		if(!Utilerias.haveSession(WebContextManager.get()))
			return result;
		try {
			BarridoInversionService barridoInversionService = (BarridoInversionService) contexto.obtenerBean("barridoInversionBusinessImpl");
			result = barridoInversionService.confirmaBarrido(noSolicitudes, opcion, clave);
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:BarridoInversionAction, M:confirmaBarrido");
		}
		return result;
	}

	@DirectMethod
	public String regresarBarrido(String noSolicitudes) {
		String result = "";
		if(!Utilerias.haveSession(WebContextManager.get()))
			return result;
		try {
			BarridoInversionService barridoInversionService = (BarridoInversionService) contexto.obtenerBean("barridoInversionBusinessImpl");
			result = barridoInversionService.regresarBarrido(noSolicitudes);
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:BarridoInversionAction, M:confirmaBarrido");
		}
		return result;
	}
	
	@DirectMethod
	public String ejecutarBarridos(String noSolicitudes) {
		String result = "";
		if(!Utilerias.haveSession(WebContextManager.get()))
			return result;
		try {
			BarridoInversionService barridoInversionService = (BarridoInversionService) contexto.obtenerBean("barridoInversionBusinessImpl");
			result = barridoInversionService.ejecutarBarridos(noSolicitudes);
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:BarridoInversionAction, M:ejecutarBarridos");
		}
		return result;
	}
	
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//++++++++++++++++++++++++++++++        MONITOR DE INVERSION        ++++++++++++++++++++++++++++++
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	
	@DirectMethod
	public List<CanastaInversionesDto> obtenerCanastasInv(String noEmpresa, String idDivisa) {
		List<CanastaInversionesDto> listCanastas = new ArrayList<CanastaInversionesDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listCanastas;
		try {
			BarridoInversionService barridoInversionService = (BarridoInversionService) contexto.obtenerBean("barridoInversionBusinessImpl");
			listCanastas = barridoInversionService.obtenerCanastasInv(noEmpresa, idDivisa);
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:BarridoInversionAction, M:obtenerCanastasInv");
		}
		return listCanastas;
	}
	@DirectMethod
	public List<CanastaInversionesDto> obtenerCanastasVencidasHoy(String noEmpresa, String idDivisa) {
		List<CanastaInversionesDto> listCanastas = new ArrayList<CanastaInversionesDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listCanastas;
		try {
			BarridoInversionService barridoInversionService = (BarridoInversionService) contexto.obtenerBean("barridoInversionBusinessImpl");
			listCanastas = barridoInversionService.obtenerCanastasVencidasHoy(noEmpresa, idDivisa);
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:BarridoInversionAction, M:obtenerCanastasVencidasHoy");
		}
		return listCanastas;
	}
	@DirectMethod
	public String crearNodosArbol(String noEmpresa, String idDivisa) {
		String respNodos = "";
		if(!Utilerias.haveSession(WebContextManager.get()))
			return respNodos;
		try {
			BarridoInversionService barridoInversionService = (BarridoInversionService) contexto.obtenerBean("barridoInversionBusinessImpl");
			respNodos = barridoInversionService.crearNodosArbol(noEmpresa, idDivisa);
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:BarridoInversionAction, M:crearNodosArbol");
		}
		return respNodos;
	}
	
	@DirectMethod
	public String vencimientoCanastas(String param, int noEmpresa, String tasaReal) {
		String resp = "";
		if(!Utilerias.haveSession(WebContextManager.get()))
			return resp;
		try {
			BarridoInversionService barridoInversionService = (BarridoInversionService) contexto.obtenerBean("barridoInversionBusinessImpl");
			resp = barridoInversionService.vencimientoCanastas(param, noEmpresa, tasaReal);
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:BarridoInversionAction, M:vencimientoCanastas");
		}
		return resp;
	}

	@DirectMethod
	public String clasificacionCanastas(int canasta, int noEmpresa, String codigo) {
		String resp = "";
		if(!Utilerias.haveSession(WebContextManager.get()))
			return resp;
		try {
			BarridoInversionService barridoInversionService = (BarridoInversionService) contexto.obtenerBean("barridoInversionBusinessImpl");
			resp = barridoInversionService.clasificacionCanastas(canasta, noEmpresa, codigo);
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:BarridoInversionAction, M:clasificacionCanastas");
		}
		return resp;
	}
	
	@DirectMethod
	public List<CanastaInversionesDto> consultarDetalleCanasta(int canasta) {
		List<CanastaInversionesDto> resp = new ArrayList<CanastaInversionesDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return resp;
		try {
			BarridoInversionService barridoInversionService = (BarridoInversionService) contexto.obtenerBean("barridoInversionBusinessImpl");
			resp = barridoInversionService.consultarDetalleCanasta(canasta);
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:BarridoInversionAction, M:consultarDetalleCanasta");
		}
		return resp;
	}
	
	
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//++++++++++++++++++++++++++++++       REPORTES DE INVERSION        ++++++++++++++++++++++++++++++
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	
	@DirectMethod
	public JRDataSource reportesCanasta(Map<String,Object> datos, ServletContext context){
		JRDataSource jrDataSource = null;
		try {
			BarridoInversionService barridoInversionService = (BarridoInversionService) contexto.obtenerBean("barridoInversionBusinessImpl", context);
			jrDataSource = barridoInversionService.reportesCanasta(datos);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:reportesCanasta");
		}
		return jrDataSource;
	}
	
	
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//++++++++++++++++++++++++++       TASAS Y COMISION DE INVERSION        ++++++++++++++++++++++++++
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	
	
	@DirectMethod
	public List<CanastaInversionesDto> obtenerTasaComision() {
		List<CanastaInversionesDto> listTC  = new ArrayList<CanastaInversionesDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listTC;
		try {
			BarridoInversionService barridoInversionService = (BarridoInversionService) contexto.obtenerBean("barridoInversionBusinessImpl");
			listTC = barridoInversionService.obtenerTasaComision();
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:BarridoInversionAction, M:obtenerTasaComision");
		}
		return listTC;
	}
	
	@DirectMethod
	public String insertarTasaComision(String param, String comision) {
		String resp = "";
		if(!Utilerias.haveSession(WebContextManager.get()))
			return resp;
		try {
			if(comision.equals("")) return "El campo de comisión no puede estar vació";
			
			BarridoInversionService barridoInversionService = (BarridoInversionService) contexto.obtenerBean("barridoInversionBusinessImpl");
			resp = barridoInversionService.insertarTasaComision(param, comision);
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:BarridoInversionAction, M:insertarTasaComision");
		}
		return resp;
	}
}
