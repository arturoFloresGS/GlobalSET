package com.webset.set.egresos.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.egresos.dto.ConfirmacionCargoCtaDto;
import com.webset.set.egresos.dto.ResultadoDto;
import com.webset.set.egresos.service.ConfirmacionCargoCtaService;
import com.webset.set.egresos.service.ConfirmacionTransferenciasService;
import com.webset.set.financiamiento.dto.AmortizacionCreditoDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.utils.tools.Utilerias;

public class ConfirmacionCargoCtaAction {
	Bitacora bitacora = new Bitacora();
	Contexto contexto = new  Contexto();
	Funciones funciones = new Funciones();
	ConsultasGenerales consultasGenerales;
	
	ConfirmacionCargoCtaService confirmacionCargoCtaService;
	
	@DirectMethod
	public List<LlenaComboGralDto> obtenerBancos(int noEmpresa) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.obtenerBancos(noEmpresa);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerBancos");
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboChequeraDto> llenaComboChequera(int idBanco, int noEmpresa, String idDivisa) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<LlenaComboChequeraDto> list = new ArrayList<LlenaComboChequeraDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.llenaComboChequera(idBanco, noEmpresa, idDivisa);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionTransferenciasAction, M:llenaComboChequera");
		}
		return list;
	}
	
	@DirectMethod
	public List<MovimientoDto> llenarMovtosPagos(String fechaIni, String fechaFin, int noEmpresa, int idBanco, String idChequera) {
		List<MovimientoDto> list = new ArrayList<MovimientoDto>();
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			
			ConfirmacionCargoCtaDto dtoGrid = new ConfirmacionCargoCtaDto();
			dtoGrid.setFechaIni(fechaIni);
			dtoGrid.setFechaFin(fechaFin);
			dtoGrid.setNoEmpresa(noEmpresa);
			dtoGrid.setIdBanco(idBanco);
			dtoGrid.setIdChequera(idChequera);
			
			list =  confirmacionCargoCtaService.llenarMovtosPagos(dtoGrid);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:llenarMovtosPagos");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> llenarMovtosCargos(String fechaIni, String fechaFin, int noEmpresa, int idBanco, String idChequera, Double importe, String secuencia, boolean todos, boolean rechNom) {
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			
			ConfirmacionCargoCtaDto dtoGrid = new ConfirmacionCargoCtaDto();
			dtoGrid.setFechaIni(fechaIni);
			dtoGrid.setFechaFin(fechaFin);
			dtoGrid.setNoEmpresa(noEmpresa);
			dtoGrid.setIdBanco(idBanco);
			dtoGrid.setIdChequera(idChequera);
			dtoGrid.setImporte(importe);
			dtoGrid.setSecuencia(secuencia);
			dtoGrid.setTodos(todos);
			dtoGrid.setChkRechNom(rechNom);
			
			list =  confirmacionCargoCtaService.llenarMovtosCargos(dtoGrid);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:llenarMovtosCargos");
		}
		return list;
	}
	
	@DirectMethod
	public Map<String, Object> ejecutarConfirmacionCargoCta(String pagos, String cargos, String diferencia, String permisible) {
		Gson gson = new Gson();
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		Gson gson1 = new Gson();
		List<Map<String, String>> paramsPagos = gson.fromJson(pagos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<Map<String, String>> paramsCargos = gson1.fromJson(cargos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		Map<String,Object> mapRetorno = new HashMap<String,Object>();
		List<MovimientoDto> listPagos = new ArrayList<MovimientoDto>();
		List<ConfirmacionCargoCtaDto> listCargos = new ArrayList<ConfirmacionCargoCtaDto>();
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			
			for(int i=0; i<paramsPagos.size(); i++) {
				MovimientoDto dtoGrid = new MovimientoDto();
				dtoGrid.setNoFolioDet(paramsPagos.get(i).get("noFolioDet") != null ? Integer.parseInt(paramsPagos.get(i).get("noFolioDet")) : 0);
				dtoGrid.setImporte(paramsPagos.get(i).get("importe") != null ? Double.parseDouble(paramsPagos.get(i).get("importe")) : 0);
				dtoGrid.setFecValorOriginal(funciones.ponerFechaDate(paramsPagos.get(i).get("fecValorOriginal")));
				
				listPagos.add(dtoGrid);
			}
			for(int x=0; x<paramsCargos.size(); x++) {
				ConfirmacionCargoCtaDto dtoGrid = new ConfirmacionCargoCtaDto();
				dtoGrid.setNoFolioDet(paramsCargos.get(x).get("noFolioDet") != null ? Integer.parseInt(paramsCargos.get(x).get("noFolioDet")) : 0);
				dtoGrid.setImporte(paramsCargos.get(x).get("importe") != null ? Double.parseDouble(paramsCargos.get(x).get("importe")) : 0);
				dtoGrid.setSecuencia(paramsCargos.get(x).get("secuencia"));
				
				listCargos.add(dtoGrid);
			}
			mapRetorno = confirmacionCargoCtaService.ejecutarConfirmacionCargoCta(listPagos, listCargos, Double.parseDouble(diferencia), Double.parseDouble(permisible));
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:ejecutarConfirmacionCargoCta");	
		}
		return mapRetorno;
	}
	
	//Para la pantalla de Consulta de mantenimiento de cupos
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> llenaComboGpoEmpresas() {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.llenaComboGpoEmpresas();
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:LlenaComboGpoEmpresas");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> llenaComboDivision() {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.llenaComboDivision();
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:llenaComboDivision");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> llenaComboGpoCupo() {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.llenaComboGpoCupo();
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:llenaComboGpoCupo");
		}
		return list;
	}
	
	@DirectMethod
	public String validarParametros(int optCupos, String grupoEmpresa, String fecIni, String fecFin, String idDivision) {
		String mensajes = "";
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		int iGpoEmpresas = 0;
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			if(grupoEmpresa.equals(""))
				iGpoEmpresas = 0;
			else
				iGpoEmpresas = Integer.parseInt(grupoEmpresa);
			
			if(iGpoEmpresas == 0 && idDivision.equals("")) {
				mensajes = "Tiene que indicar un grupo de empresas o bien una divisi�n";
				return mensajes; 
			}else if(optCupos == 2) {
				if(fecIni.equals("") && fecFin.equals("")) {
					mensajes = "Debe capturar al menos, una fecha para la busqueda";
					return mensajes;
				}
				if(!fecIni.equals("")) {
					if(!funciones.isDate(fecIni, false)) {
						mensajes = "Debe capturar una fecha valida para la busqueda";
						return mensajes;
					}
				}
				if(!fecFin.equals("")) {
					if(!funciones.isDate(fecFin, false)) {
						mensajes = "Debe capturar una fecha valida para la busqueda";
						return mensajes;
					}
				}
			}
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:validarParametros");
		}
		return mensajes;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> buscarCupos(int optCupos, String grupoEmpresa, String fecIni, String fecFin, String idDivision) {
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		int iGpoEmpresas = 0;
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			iGpoEmpresas = grupoEmpresa.equals("") ? 0 : Integer.parseInt(grupoEmpresa);
			
			list = confirmacionCargoCtaService.buscarCupos(optCupos, iGpoEmpresas, fecIni, fecFin, idDivision);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:buscarCupos");
		}
		return list;
	}
	
	@DirectMethod
	public String validarParametrosInsert(String datos, boolean insertar) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		Gson gson = new Gson();
		List<Map<String, String>> paramsInsert = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		String mensajes = "";
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			if(paramsInsert.get(0).get("idGrupoFlujo").equals("")) {
				mensajes = "Debe seleccionar el grupo empresa o la divisi�n correspondiente";
				return mensajes;
			}else if(paramsInsert.get(0).get("fechaPropuesta").equals("") && !funciones.isDate(paramsInsert.get(0).get("fechaPropuesta"), false)) {
				mensajes = "Debe capturar una fecha de pago de la propuesta valida";
				return mensajes;
			}else if(paramsInsert.get(0).get("fecLimiteSelecc").equals("") && !funciones.isDate(paramsInsert.get(0).get("fecLimiteSelecc"), false)) {
				mensajes = "Debe capturar una fecha l�mite de selecci�n valida";
				return mensajes;
			}else if(paramsInsert.get(0).get("cupoManual").equals("")) {
				mensajes = "Debe capturar el cupo para pagos manuales";
				return mensajes;
			}else if(paramsInsert.get(0).get("cupoAutomatico").equals("")) {
				mensajes = "Debe capturar el cupo para pagos de seleccion autom�tica";
				return mensajes;
			}else if(paramsInsert.get(0).get("montoMaximo").equals("")) {
				mensajes = "Debe capturar un monto m�ximo para las facturas";
				return mensajes;
			}else if(paramsInsert.get(0).get("idGrupo").equals("")) {
				mensajes = "Debe seleccionar el grupo cupo de rubro";
				return mensajes;
			}
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:validarParametrosInsert");
		}
		return mensajes;
	}
	
	@DirectMethod
	public Map<String, Object> ejecutarAltaRegistro(String datos, boolean insertar, boolean esDivision) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		Gson gson = new Gson();
		List<Map<String, String>> paramsInsert = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		Map<String,Object> mapRetorno = new HashMap<String,Object>();
		List<ConfirmacionCargoCtaDto> listDatos = new ArrayList<ConfirmacionCargoCtaDto>();
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			
			for(int i=0; i<paramsInsert.size(); i++) {
				ConfirmacionCargoCtaDto datosGrid = new ConfirmacionCargoCtaDto();
				
				datosGrid.setDescGrupoFlujo(paramsInsert.get(i).get("descGrupoFlujo"));
				datosGrid.setMontoMaximo(paramsInsert.get(i).get("montoMaximo") != null ? Double.parseDouble(paramsInsert.get(i).get("montoMaximo")) : 0);
				datosGrid.setCupoManual(paramsInsert.get(i).get("cupoManual") != null ? Double.parseDouble(paramsInsert.get(i).get("cupoManual")) : 0);
				datosGrid.setCupoAutomatico(paramsInsert.get(i).get("cupoAutomatico") != null ? Double.parseDouble(paramsInsert.get(i).get("cupoAutomatico")) : 0);
				datosGrid.setDescGrupoCupo(paramsInsert.get(i).get("descGrupoCupo"));
				datosGrid.setFechaPropuesta(paramsInsert.get(i).get("fechaPropuesta"));
				datosGrid.setIdGrupo(paramsInsert.get(i).get("idGrupo") != null ? Integer.parseInt(paramsInsert.get(i).get("idGrupo")) : 0);
				datosGrid.setFecLimiteSelecc(paramsInsert.get(i).get("fecLimiteSelecc"));
				datosGrid.setCveControl(paramsInsert.get(i).get("cveControl").equals("") ? "" : paramsInsert.get(i).get("cveControl"));
				datosGrid.setIdGrupoFlujo(paramsInsert.get(i).get("idGrupoFlujo") != null ? Integer.parseInt(paramsInsert.get(i).get("idGrupoFlujo")) : 0);
				datosGrid.setConcepto(paramsInsert.get(i).get("concepto").equals("") ? "" : paramsInsert.get(i).get("concepto"));
				
				listDatos.add(datosGrid);
			}
			mapRetorno = confirmacionCargoCtaService.ejecutarAltaRegistro(listDatos, insertar, esDivision);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:ejecutarAltaRegistro");	
		}
		return mapRetorno;
	}
	
	@DirectMethod
	public Map<String, Object> eliminarRegistro(String datos) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		Gson gson = new Gson();
		List<Map<String, String>> paramsDelete = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		Map<String,Object> mapRetorno = new HashMap<String,Object>();
		List<ConfirmacionCargoCtaDto> listDatos = new ArrayList<ConfirmacionCargoCtaDto>();
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			
			for(int i=0; i<paramsDelete.size(); i++) {
				ConfirmacionCargoCtaDto datosGrid = new ConfirmacionCargoCtaDto();
				
				datosGrid.setCveControl(paramsDelete.get(i).get("cveControl").equals("") ? "" : paramsDelete.get(i).get("cveControl"));
				datosGrid.setIdGrupoFlujo(paramsDelete.get(i).get("idGrupoFlujo") != null ? Integer.parseInt(paramsDelete.get(i).get("idGrupoFlujo")) : 0);
				
				listDatos.add(datosGrid);
			}
			mapRetorno = confirmacionCargoCtaService.eliminarRegistro(listDatos);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:eliminarRegistro");	
		}
		return mapRetorno;
	}
	
	//Para la pantalla de pago de propuestas automatico
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> llenaComboCveControl(int groEmpresa, int grupo, int idDivision) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.llenaComboCveControl(groEmpresa, grupo, idDivision);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:llenaComboCveControl");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> llenaComboValor(int tipoCombo, int gpoEmpresa, int idBcoPag) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.llenaComboValor(tipoCombo, gpoEmpresa, idBcoPag);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:llenaComboValor");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> selectCupos(int gpoEmpresa, String idDivisa, int grupo, String cveControl, boolean bCambioMMF, int idDivision) {
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.selectCupos(gpoEmpresa, idDivisa, grupo, cveControl, bCambioMMF, idDivision);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:selectCupos");
		}
		return list;
	}
	
	@DirectMethod
	public String validaParametros(int gpoEmpresa, String idDivisa, int grupo, String cveControl, int idDivision, String sFecha) {
		String sMensa = "";
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			if(gpoEmpresa == 0 && idDivision == 0)
				sMensa = "Seleccione un Grupo de Empresas o una Division";
			else if(cveControl.equals(""))
				sMensa = "Seleccione un Clave Control";
			else if(grupo == 0)
				sMensa = "Seleccione un Grupo de Rubros";
			else if(sFecha.equals(""))
				sMensa = "No hay fecha propuesta seleccionada";
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:validaParametros");
		}
		return sMensa;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> selectPagosAutomaticos(int gpoEmpresa, String idDivisa, int grupo, String cveControl, int idDivision, 
			String fechaPropuesta, boolean chkServicio, boolean chkPropuestos, boolean bChequera, String datosGrid, String totPropuesto, String tipoGrid) {
		Gson gson = new Gson();
		List<Map<String, String>> paramsGrid = gson.fromJson(datosGrid, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		//String respuesta = "";
		ConfirmacionCargoCtaDto dto = new ConfirmacionCargoCtaDto();
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			
			//dto.setIdGrupoFlujo(gpoEmpresa);
			dto.setIdGrupoFlujo(10);
			dto.setIdDivisa(idDivisa.equals("") ? "MN" : idDivisa);
			//dto.setIdGrupo(grupo);
			dto.setIdGrupo(1);
			dto.setCveControl(cveControl);
			dto.setIdDivision("" + idDivision);
			//dto.setFechaPropuesta(fechaPropuesta);
			dto.setFechaPropuesta("16/03/2012");
			dto.setChkProvServ(chkServicio);
			dto.setChkPropuestos(chkPropuestos);
			dto.setBChequera(bChequera);
			dto.setTotalPropuesto(Double.parseDouble(totPropuesto));
			dto.setTipoGrid(tipoGrid);
			
			list = confirmacionCargoCtaService.selectPagosAutomaticos(dto, paramsGrid);
			
			//respuesta = confirmacionCargoCtaService.selectPagosAutomaticos(dto, paramsGrid);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:SelectPagosAutomaticos");
		}
		return list;
		//return respuesta;
	}
	
	@DirectMethod
	public String validaDatos(double cupoTotal, double totPropuesto) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		String sMensa = "";
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			if(totPropuesto == 0)
				return sMensa = "Seleccione los pagos propuestos";
			else if(totPropuesto > cupoTotal) 
				return sMensa = "El Total Propuesto supera al Cupo Total";
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:validaDatos");
		}
		return sMensa;
	}
	
	@DirectMethod
	public Map<String, Object> ejecutarDatos(String pametros, String fechaPago, String cveControl, double cupoAuto, double totPropuesto, String idDivisa, int idGrupo) {
		Gson gson = new Gson();
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<Map<String, String>> paramsDatos= gson.fromJson(pametros, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		Map<String,Object> mapRetorno = new HashMap<String,Object>();
		//List<ConfirmacionCargoCtaDto> listCargos = new ArrayList<ConfirmacionCargoCtaDto>();
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			
			mapRetorno = confirmacionCargoCtaService.ejecutarDatos(paramsDatos, fechaPago, cveControl, cupoAuto, totPropuesto, idDivisa, idGrupo);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:ejecutarConfirmacionCargoCta");	
		}
		return mapRetorno;
	}
	
	@DirectMethod
	public String validaAgregarQuitar(int gpoEmpresa, String idDivisa, int grupo, String cveControl, int idDivision, String sFecha, 
			boolean agregar, boolean chkPropuestos, boolean chkServicio, boolean bChequera) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		
		String sMensa = "";
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			if(agregar) {
				if(chkPropuestos)
					return "";
			}
			sMensa = validaParametros(gpoEmpresa, idDivisa, grupo, cveControl, idDivision, sFecha);
			
			if(!sMensa.equals(""))
				return sMensa;
			else {
				confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
				
				ConfirmacionCargoCtaDto dto = new ConfirmacionCargoCtaDto();
				
				dto.setIdGrupoFlujo(gpoEmpresa);
				dto.setIdDivisa(idDivisa.equals("") ? "MN" : idDivisa);
				dto.setIdGrupo(grupo);
				dto.setCveControl(cveControl);
				dto.setIdDivision("" + idDivision);
				dto.setFechaPropuesta(sFecha);
				dto.setChkPropuestos(chkPropuestos);
				dto.setChkProvServ(chkServicio);
				dto.setBChequera(bChequera);
				
				sMensa = confirmacionCargoCtaService.validaAgregarQuitar(dto);
			}
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:validaAgregarQuitar");
		}
		return sMensa;
	}
	
	//Para la pantalla de compra venta de divisas
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerDivisaVta (int idUsuario) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.obtenerDivisaVta(idUsuario);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerDivisaVta");
		}
		return list;
	}
	

	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerDivisaVnta (int idUsuario, int noEmpresa) {
		
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		
		try {
			
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.obtenerDivisaVta(idUsuario, noEmpresa);
			
		}catch(Exception e){
			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerDivisaVta");
			
		}
		
		return list;
		
	}//END METHOD: obtenerDivisaVta
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerBancoVta (int noEmpresa ,String idDivisa) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.obtenerBancoVta(noEmpresa,idDivisa);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerBancoVta");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerChequeraVta ( int noEmpresa, String idDivisa , int idBanco) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.obtenerChequeraVta(noEmpresa,idDivisa,idBanco);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerDivisaVta");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerBancoAbo (int noEmpresa, String idDivisa, int radio, int custodia ) {
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.obtenerBancoAbo(noEmpresa,idDivisa, radio, custodia);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerBancoAbo");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerBancoAbono (int noEmpresa, String idDivisa ) {
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		try {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.obtenerBancoAbo(noEmpresa,idDivisa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerBancoAbo");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerChequeraAbo (int custodia,int radio,int noEmpresa, String idDivisa , int idBanco) {
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.obtenerChequeraAbo(custodia,radio,noEmpresa,idDivisa,idBanco);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerChequeraAbo");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerChequeraAbono (int noEmpresa,String idDivisa,int idBanco) {
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		try {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.obtenerChequeraAbo(noEmpresa,idDivisa,idBanco);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerChequeraAbo");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerChequeraVnta ( int noEmpresa, String idDivisa , int idBanco) {
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		try {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.obtenerChequeraVta(noEmpresa,idDivisa,idBanco);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerDivisaVta");
		}
		return list;
	}
	
	
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerCasaCambioVta (int idUsuario) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.obtenerCasaCambioVta(idUsuario);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerCasaCambioVta");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerOperadorVta (int noCliente) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.obtenerOperadorVta(noCliente);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerOperadorVta");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerGrupoVta(int idUsuario) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();   
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.obtenerGrupoVta(idUsuario);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerGrupoVta");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerBanco (int noCliente ,String idDivisa) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.obtenerBanco(noCliente,idDivisa);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerBanco");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerRubroVta(int idGrupo) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();   
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.obtenerRubroVta(idGrupo);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerRubroVta");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerChequeraTotal (int noCliente, String idDivisa , int idBanco) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.obtenerChequeraTotal(noCliente,idDivisa,idBanco);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerChequeraAbo");
		}
		return list;
	}
	
	@DirectMethod
	public String validaCampos(int Cliente, int usuario, String folio, int noEmpresa ,String txtDivisaVenta,String txtBancoCargo,
			String cmbChequeraCargo,String txtDivisaCompra,String txtBancoAbono, String cmbChequeraAbono,String txtTipoCambio, 
			String txtCasaCambio, String cmbCasaCambio, String txtOperador,String cmbOperador,String txtGrupo, String txtBanco,
			String txtFechaValor,String txtRubro,String txtImporteCompra,String txtImporteVenta, String cmbChequera,
			String txtFechaLiquidacion, int custodia, int formaPago) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		String msg = "";
		String fecHoy = "";
		String datosBoton = "";
		ConfirmacionCargoCtaDto dto = new ConfirmacionCargoCtaDto();
		txtFechaLiquidacion = txtFechaLiquidacion.substring(0,4) + "/" + txtFechaLiquidacion.substring(5,7) + "/"+ txtFechaLiquidacion.substring(8,10);
		txtFechaValor = txtFechaValor.substring(0,4)+ "/" + txtFechaValor.substring(5,7) +"/" + txtFechaValor.substring(8,10);
		ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
		fecHoy = confirmacionCargoCtaService.fecHoy();
		
		fecHoy = fecHoy.substring(0,4) +"/"+fecHoy.substring(5,7)+ "/"+ fecHoy.substring(8,10); 
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			if (txtFechaValor.equals("")){  
				msg = "Debe proporcionar una fecha valor adecuada para el movimiento";
			}
			if (txtFechaValor.equals("")){
				msg = "Debe proporcionar una fecha valor adecuada para el movimiento";
			}
			if (txtFechaLiquidacion.equals("")){ 
				msg = "Debe proporcionar una fecha de liquidaci�n adecuada para el movimiento";
	        }        
			if (txtOperador.equals("") || cmbOperador.equals("")){
				msg = "Debe seleccionar alg�n Operador de la Casa de Cambio";
		    }
			if (txtRubro.equals("")){  
				msg = "Debe seleccionar alg�n Rubro";
			}
			if (txtGrupo.equals("")){  
				msg = "Debe seleccionar alg�n grupo de Rubros";
			}
				
			if (txtBanco.equals("")){
				msg = "Debe seleccionar el banco de la casa de cambio";    
		    } else if (custodia == 1 && cmbChequera.equals("")){ // evalua el radio tranferencia con valor 1  
		    	msg = "Debe seleccionar la chequera de la casa de cambio";
		    }
			
			if(txtFechaLiquidacion.compareTo(fecHoy) < 0){
				msg ="La fecha de liquidaci�n debe ser el dia de hoy, ma�ana o bien pasado ma�ana";
			}
			
			if(txtFechaValor.compareTo(fecHoy) < 0){
				msg ="La fecha valor debe ser el dia de hoy, ma�ana o bien pasado ma�ana";
			}
			if (txtFechaLiquidacion.compareTo(txtFechaValor) < 0){     
				msg = "La fecha de liquidaci�n debe ser mayor o igual a la fecha valor del movimiento";
			}
			
			if (txtCasaCambio.equals("")){  
				msg = "Debe seleccionar la casa de cambio";
		    }
			if (txtImporteCompra.equals("")){  
				msg = "Debe escirbir el importe de compra";
			}
			if (cmbChequeraAbono.equals("")){  
				msg = "Debe seleccionar una chequera de abono";
		    }		
			if (txtBancoAbono.equals("")){  
				msg = "Debe seleccionar un banco de abono";
	        }
			if (txtDivisaVenta == txtDivisaCompra){  
				msg = "La divisa de compra y la divisa de venta es la misma";
		   	}
			if (txtDivisaCompra.equals("")){  
				msg = "Debe seleccionar una divisa de compra";
            }
			if (cmbChequeraCargo.equals("")){  
				msg = "Debe seleccionar una chequera de cargo";
		    }
			if (txtBancoCargo.equals("")){ 
				msg = "Debe seleccionar un banco de cargo";
		    }  
			if (txtDivisaVenta.equals("")){  
			    msg = "Debe seleccionar una divisa de venta";
		   	}
			
			if(msg.equals("")) {
				dto.setUsuario(usuario);
		        dto.setSFolio(folio.toString());
		    	dto.setNoEmpresa(noEmpresa);
		    	dto.setFechaValor(txtFechaValor.toString());
		    	dto.setFechaLiquidacion(txtFechaLiquidacion.toString());
		    	dto.setNomOperador(cmbOperador.toString());
		    	dto.setDivisaVenta(txtDivisaVenta.toString());
		    	dto.setBancoCargo(funciones.convertirCadenaInteger(txtBancoCargo));
		    	dto.setChequeraCargo(cmbChequeraCargo.toString());  
		    	dto.setDivisaCompra(txtDivisaCompra.toString());
		    	dto.setBancoAbono(funciones.convertirCadenaInteger(txtBancoAbono));
		    	dto.setChequeraAbono(cmbChequeraAbono.toString());  
		    	dto.setTipoCambio(funciones.convertirCadenaDouble(txtTipoCambio));
		    	dto.setIdCasaCambio(funciones.convertirCadenaInteger(txtCasaCambio));
		    	dto.setDescCasaCambio(cmbCasaCambio);
		    	dto.setIdOperador(Integer.parseInt(txtOperador));
		    	dto.setGrupo(funciones.convertirCadenaInteger(txtGrupo));
		    	dto.setIdBanco(funciones.convertirCadenaInteger(txtBanco));
		    	dto.setIdRubro(funciones.convertirCadenaInteger(txtRubro));
		    	dto.setImporteCompra(funciones.convertirCadenaDouble(txtImporteCompra.replace(",", "")));
		    	dto.setImporteVenta(funciones.convertirCadenaDouble(txtImporteVenta.replace(",", "")));
		    	dto.setImporteOriginal(funciones.convertirCadenaDouble(txtImporteCompra.replace(",", "")));
		    	dto.setIdChequera(cmbChequera.toString());
		    	dto.setCustodia(custodia);
		    	dto.setCliente(Cliente);
		    	dto.setIdFormaPago(formaPago);
		    	
		    	datosBoton = confirmacionCargoCtaService.ejecutar(dto);
		    	
		    	if(!datosBoton.equals("")){
		    		msg = datosBoton;			    		
		    	}
		    	
		    }
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:validaCampos");
			e.printStackTrace();
		}
		return msg;
	}
	
	//Para la pantalla de compra venta de Transferencias
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> claveControl(int idUsuario) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.claveControl(idUsuario);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:claveControl");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> DivisaLlenado (String noClaveControl) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.DivisaLlenado(noClaveControl);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:claveControl");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> llenaGridMovimientos(int noEmpresa, int grupo, String noClaveControl, String idDivisa) {
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			ConfirmacionCargoCtaDto dtoGrid = new ConfirmacionCargoCtaDto();
			dtoGrid.setNoEmpresa(noEmpresa);
			dtoGrid.setGrupo(grupo);
			dtoGrid.setNoClaveControl(noClaveControl);		
			dtoGrid.setIdDivisa(idDivisa);
			
			list = confirmacionCargoCtaService.llenaGridMovimientos(dtoGrid);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:llenarMovtosPagos");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerDivisaEmpresa (int idUsuario) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.obtenerDivisaEmpresa(idUsuario); 
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerDivisaEmpresa");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerBancoEmpresa (String nomEmpresa, String idDivisa) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.obtenerBancoEmpresa(nomEmpresa, idDivisa); 
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerBancoEmpresa");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerChequeraEmp (int idBancoCasa, String noEmpresa, String idDivisa) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			ConfirmacionCargoCtaDto dtoGrid = new ConfirmacionCargoCtaDto();
			dtoGrid.setIdBancoCasa(idBancoCasa);			
			dtoGrid.setNoClaveControl(noEmpresa);
			dtoGrid.setIdDivisa(idDivisa);	
			list = confirmacionCargoCtaService.obtenerChequeraEmp(dtoGrid);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerChequeraEmp");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerDivisaPago (int noEmpresa) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.obtenerDivisaPago(noEmpresa);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerDivisaPago");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerEmpresa (int idUsuario) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.obtenerEmpresa(idUsuario);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerEmpresa");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> BancoLlenado (int noEmpresa, String idDivisa) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.BancoLlenado(noEmpresa, idDivisa);
			
			System.out.println(list.size());
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerBancoPago");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> ObtenerChequera (int noEmpresa, String idDivisa, int idBanco) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.obtenerChequera(noEmpresa, idDivisa, idBanco);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerDivisaPago");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> CasaCambio (int idUsuario) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.CasaCambio(idUsuario);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerDivisaPago");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> BancoCasaCambio (String idDivisa, int idCasaCambio) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		
		System.out.print(idDivisa);
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.BancoCasaCambio(idDivisa, idCasaCambio); 
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerBancoEmpresa");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerChequeraCasa (int idCasaCambio, String idDivisa, int idBancoCasa) {
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.obtenerChequeraCasa(idCasaCambio, idDivisa, idBancoCasa);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerDivisaPago");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> obtenerOperador (int idCasaCambio) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			list = confirmacionCargoCtaService.obtenerOperador(idCasaCambio);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerDivisaPago");
		}
		return list;
	}
	
	@DirectMethod
	public String Update(int psFoliosCli, String idCasaCambio, String idBancoCasa, String idChequera, String idDivisa, double tipoCambio, double importe ) {
		String Msj = "";
		int iRes = 0;
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			iRes = confirmacionCargoCtaService.Update(psFoliosCli, idCasaCambio, idBancoCasa, idChequera, idDivisa, tipoCambio, importe );
			
			if(iRes > 0)
				Msj = "actualizo";
			else
				Msj = "no actualizo nada ";
			}	
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:Update");
		}
		return Msj;
	}
	
	@DirectMethod
	public Map<String, Object> ejecutarPagador(String datosGrid, String sBandera, String usuario, String pi_Banco, String chequera, 
											   int pri, String ter, int seg, String ps_folios, String pi_folios_rech, String ps_folios_rech,
											   int cua){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		String Bandera = sBandera;
		String user = usuario;
		String pBanco = pi_Banco;
		String cheq = chequera;
		int prim = pri;
		String terc = ter;
		int segu = seg;
		String pfolios = ps_folios;
		String piFoliosRe = pi_folios_rech;
		String psFoliosRe = ps_folios_rech;
		
		
		Gson gson = new Gson();
		Map<String, Object> mapRetFondeo = new HashMap<String, Object>();
		List<ConfirmacionCargoCtaDto> listGridFondeo = new ArrayList<ConfirmacionCargoCtaDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService) contexto.obtenerBean("coinversionBusinessImpl");
			List<Map<String, String>> objParams = gson.fromJson(datosGrid, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			
			for(int i = 0; i < objParams.size(); i++)
			{
				ConfirmacionCargoCtaDto dto = new ConfirmacionCargoCtaDto();
					dto.setNoEmpresa(funciones.convertirCadenaInteger(objParams.get(i).get("noEmpresa")));
					dto.setNomEmpresa(funciones.validarCadena(objParams.get(i).get("nomEmpresa").toString()));
			        dto.setNoProveedor(funciones.validarCadena(objParams.get(i).get("noProveedor").toString()));
			     	dto.setNomProveedor(funciones.validarCadena(objParams.get(i).get("nomProveedor").toString()));
			     	dto.setNoDocto(funciones.validarCadena(objParams.get(i).get("noDocto").toString()));
			     	dto.setImporte(funciones.convertirCadenaDouble(objParams.get(i).get("importe")));
			     	dto.setImporteMn(funciones.convertirCadenaDouble(objParams.get(i).get("importeMn")));
			     	dto.setIdDivisa(funciones.validarCadena(objParams.get(i).get("idDivisa").toString()));
			     	dto.setNumFormaPago(funciones.validarCadena( objParams.get(i).get("numFormaPago").toString()));
			     	dto.setFechaHoy(funciones.validarCadena(objParams.get(i).get("FechaHoy").toString()));
			     	dto.setDescBanco(funciones.validarCadena(objParams.get(i).get("descBanco").toString()));
			     	dto.setNoBancoPago(funciones.convertirCadenaInteger(objParams.get(i).get("noBancoPago")));
			     	dto.setBancoPago(funciones.validarCadena(objParams.get(i).get("bancoPago").toString()));
			     	dto.setIdChequera(funciones.validarCadena(objParams.get(i).get("idChequera").toString()));
			     	dto.setBeneficiario(funciones.validarCadena(objParams.get(i).get("beneficiario").toString()));
			     	dto.setNoFolioDet(funciones.convertirCadenaInteger(objParams.get(i).get("noFolioDet")));
			     	dto.setConcepto(funciones.validarCadena(objParams.get(i).get("concepto")));
			     	dto.setObservaciones(funciones.validarCadena(objParams.get(i).get("observaciones").toString()));
			     	dto.setIdBancoBenef(funciones.convertirCadenaInteger(objParams.get(i).get("idBancoBenef")));
			     	dto.setDivisaOriginal(funciones.validarCadena(objParams.get(i).get("divisaOriginal").toString()));
			     	dto.setDias(funciones.convertirCadenaInteger(objParams.get(i).get("dias")));
			     	dto.setImporteOriginal(funciones.convertirCadenaDouble(objParams.get(i).get("importeOriginal")));
			     	dto.setChequeraBenef(funciones.validarCadena(objParams.get(i).get("chequeraBenef").toString()));
			     	dto.setIdRubro(funciones.convertirCadenaInteger(objParams.get(i).get("idRubro")));
			     	dto.setBancoCte(funciones.validarCadena(objParams.get(i).get("bancoCte").toString()));
			     	dto.setBcoPagoCruzado(funciones.validarCadena(objParams.get(i).get("bcoPagoCruzado").toString()));
			     	dto.setCheqPagoCruzado(funciones.validarCadena(objParams.get(i).get("cheqPagoCruzado").toString()));
			     	dto.setDivPagoCruzado(funciones.validarCadena(objParams.get(i).get("divPagoCruzado").toString()));
			     	dto.setChequeraCte(funciones.validarCadena(objParams.get(i).get("chequeraCte").toString()));
			     	dto.setIdDivisaPago(funciones.validarCadena(objParams.get(i).get("idDivisaPago").toString()));
			     	dto.setIdDivisaCte(funciones.validarCadena(objParams.get(i).get("idDivisaCte").toString()));
			     	dto.setIdContable(funciones.validarCadena(objParams.get(i).get("idContable").toString()));
		     	listGridFondeo.add(dto);
			}
			/*
			ParamBusquedaFondeoDto dtoBus = new ParamBusquedaFondeoDto();
				dtoBus.setIdEmpresa(idEmpresa > 0 ? idEmpresa : 0);
				dtoBus.setIdEmpresaRaiz(idEmpresaRaiz >0 ? idEmpresaRaiz : 0);
				dtoBus.setIdBanco(idBanco > 0 ? idBanco : 0);
				dtoBus.setIdDivisa(idDivisa != null && !idDivisa.equals("") ? idDivisa : "");
				dtoBus.setIdChequera(idChequera != null && !idChequera.equals("") ? idChequera : "");
				dtoBus.setChkMismoBanco(chkMismoBanco);
				dtoBus.setSTipoBusqueda(sTipoBusqueda != null && !sTipoBusqueda.equals("") ? sTipoBusqueda : "");
				dtoBus.setBMontoMinFondeo(bVisibleMontoMinFondeo);
				dtoBus.setMontoMinFondeo(funciones.convertirCadenaDouble(montoMinFondeo));
			*/
			mapRetFondeo = confirmacionCargoCtaService.ejecutaPagador(listGridFondeo, Bandera, user, pBanco, 
																		  cheq, prim,  terc,  segu,  pfolios,  
																		  piFoliosRe, psFoliosRe, cua);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Coinversion, C:CoinversionAction, M:ejecutarFondeoAutomatico");
		}
		return mapRetFondeo;
	}
	
	//JRBM
		@DirectMethod
		public List<ConfirmacionCargoCtaDto> obtenerGruposPorTipoMovto( String  idTipoMovto ) {
					
			try {
				
				return ( (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl") ).obtenerGruposPorTipoMovto( idTipoMovto ); 
				
				
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerGrupoVta");
				return null; 
			}
			
		}//END METHOD:obtenerGruposPorTipoMovto
		
		@DirectMethod
		public List<ConfirmacionCargoCtaDto> llenaComboFirmas ( ) {
			try {
				
				return ( (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl") ).llenaComboFirmas();
				
			}catch(Exception e){
				
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:llenaComboFirmas");
				return null;
				
			}
			
		}//END METHOD:llenaComboFirmas 
		
		@DirectMethod
		public ResultadoDto crearOperaciones(int Cliente			, int usuario			 	, String folio, 
										 int noEmpresa				, String idDivisaVenta	 	, String idBancoCargo,
									 	 String chequeraCargo		, String idDivisaCompra 	, String idBancoAbono, 
									 	 String chequeraAbono		, String tipoDeCambio	 	, String idCasaDeCambio, 
									 	 String nomCasaDeCambio		, String idOperador	 		, String nomOperador,
									 	 String idGrupoEgreso		, String idBancoCasaCambio	, String fechaValor,
									 	 String idRubroEgreso		, String importeCompra		, String importeVenta, 
									 	 String chequeraCasaCambio	, String fechaLiquidacion   , int custodia, 
									 	 int idFormaPago			, String concepto			, String referencia, 
									 	 String idGrupoIngreso		, String idRubroIngreso		, String firma1, 
									 	 String firma2,
									 	 String descDivisaVenta		,String descDivisaCompra	, String descBancoCargo,	
									 	 String descBancoAbono		,String descBancoCasa		, String descGrupoEgreso,	
									 	 String descRubroEgreso){ 
		
			
			String msg 			= null;
			String fecHoy 		= null;
			String datosBoton 	= null;
			
			ConfirmacionCargoCtaDto dto = new ConfirmacionCargoCtaDto();
			
			fechaLiquidacion = fechaLiquidacion.substring(0,4) + "/" + fechaLiquidacion.substring(5,7) + "/" + fechaLiquidacion.substring(8,10);
			fechaValor       = fechaValor.substring(0,4)       + "/" + fechaValor.substring(5,7)       + "/" + fechaValor.substring(8,10);
			
			ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			fecHoy = confirmacionCargoCtaService.fecHoy();
			
			fecHoy = fecHoy.substring(0,4) +"/"+fecHoy.substring(5,7)+ "/"+ fecHoy.substring(8,10); 
			
			try {
				
				if( noEmpresa == 0 ){
					return  new ResultadoDto(false, "No ha seleccionado una Empresa", null);
				}
				
				if (idDivisaVenta.equals("")){  
					return  new ResultadoDto(false, "No ha seleccionado una Divisa de Venta", null);
			   	}
				
				if (idBancoCargo.equals("")){ 
					return  new ResultadoDto(false, "No ha seleccionado un Banco de Cargo", null);
			    }  
				
				if (chequeraCargo.equals("")){  
					return  new ResultadoDto(false, "No ha seleccionado una chequera de Cargo", null);
			    }
				
				if (idDivisaCompra.equals("")){  
					return  new ResultadoDto(false, "No ha seleccionado una Divisa de Compra", null);
	            }
				
				if (idDivisaVenta == idDivisaCompra){  
					return  new ResultadoDto(false, "Divisa de Compra y Divisa de Venta son iguales", null);
			   	}
				
				if (idBancoAbono.equals("")){  
					return  new ResultadoDto(false, "No ha seleccionado un Banco de Abono", null);
		        }
				
				if (chequeraAbono.equals("")){  
					return  new ResultadoDto(false,  "No ha seleccionado una Chequera de Abono", null);
			    }		
				
				if (tipoDeCambio.equals( "" ) ){
					return  new ResultadoDto(false,  "No ha digitado el Tipo de Cambio", null);
				}
				
				if (importeCompra.equals("")){  
					return  new ResultadoDto(false,  "No ha digitado el Importe de Compra", null);
				}
				
				if ( importeVenta.equals("") ){
					return  new ResultadoDto(false,  "No ha digitado el Importe de Venta", null);
				}
				
				if (idCasaDeCambio.equals("")){  
					return  new ResultadoDto(false,  "No ha seleccionado la Casa de Cambio", null);
			    }
				
				if( idFormaPago == 3 ){
				
					if (idBancoCasaCambio.equals("")){
						return  new ResultadoDto(false,   "No ha seleccionado el Banco de la Casa de Cambio", null);    
				    } 
					
					if (chequeraCasaCambio.equals("")){  
						return  new ResultadoDto(false,  "No ha seleccionado la Chequera de la Casa de Cambio", null);
				    }
					
				}
				
				if (fechaValor.equals("")){  
					return  new ResultadoDto(false, "No ha seleccionado la Fecha Valor", null);
				}
				
				if(fechaValor.compareTo(fecHoy) < 0){
					return  new ResultadoDto(false,  "Fecha Valor no valida, es anterior al dia de hoy", null);
				}

				if (fechaLiquidacion.equals("")){ 
					return  new ResultadoDto(false,   "No ha seleccionado la fecha de liquidaci�n", null);
		        }
				
				if(fechaLiquidacion.compareTo(fecHoy) < 0){
					return  new ResultadoDto(false,  "Fecha Liquidaci�n no valida, es anterior al dia de hoy", null);
				}
				
				if (fechaLiquidacion.compareTo(fechaValor) < 0){     
					return  new ResultadoDto(false,   "La Fecha de Liquidaci�n debe ser mayor o igual a la Fecha Valor", null);
				}
				
				if (idGrupoEgreso.equals("")){  
					return  new ResultadoDto(false,   "No ha seleccionado un Grupo de Egreso.", null);
				}
				
				if (idRubroEgreso.equals("")){  
					return  new ResultadoDto(false,   "No ha seleccionado un Rubro de Egreso.", null);
				}
					
				if (idGrupoIngreso.equals("")){  
					return  new ResultadoDto(false,   "No ha seleccionado un Grupo de Ingreso.", null);
				}
				
				if (idRubroIngreso.equals("")){  
					return  new ResultadoDto(false,   "No ha seleccionado un Rubro de Ingreso.", null);
				}
				
				if( referencia.length() > 30 ){
					return new ResultadoDto(false, "La referencia es mayor a 30 caracteres", null);
				}
				
				if( referencia.contains("_") ){
					return new ResultadoDto(false, "La referencia no debe contener caracteres especiales.", null);
				}
				
				
				
				dto.setUsuario(usuario);
		        dto.setSFolio(folio.toString());
		    	dto.setNoEmpresa(noEmpresa);
		    		    	
		    	
		    	dto.setDivisaVenta(idDivisaVenta.toString());	    	
		    	dto.setBancoAbono(funciones.convertirCadenaInteger(idBancoAbono));
		    	dto.setChequeraAbono(chequeraAbono.toString());
		    	
		    	dto.setDivisaCompra(idDivisaCompra.toString());
		    	dto.setBancoCargo(funciones.convertirCadenaInteger(idBancoCargo));
		    	dto.setChequeraCargo(chequeraCargo.toString());
		    	
		    	dto.setFechaValor(fechaValor.toString());
		    	dto.setFechaLiquidacion(fechaLiquidacion.toString());
		    	
		    	  
		    	dto.setTipoCambio(funciones.convertirCadenaDouble(tipoDeCambio));
		    	dto.setImporteCompra(funciones.convertirCadenaDouble(importeCompra.replace(",", "")));
		    	dto.setImporteOriginal(funciones.convertirCadenaDouble(importeCompra.replace(",", "")));
		    	dto.setImporteVenta(funciones.convertirCadenaDouble(importeVenta.replace(",", "")));
		    	
		    	dto.setCustodia(custodia);
		    	dto.setIdFormaPago(idFormaPago);
		    	
		    	if( idFormaPago == 3 ){		    	
			    	dto.setIdBanco(funciones.convertirCadenaInteger(idBancoCasaCambio));		    	
			    	dto.setIdChequera(chequeraCasaCambio.toString());
			    	dto.setReferencia( referencia );			    	
		    	}else{
		    		dto.setIdBanco(0);		    	
			    	dto.setIdChequera(null);
			    	dto.setReferencia( null );			    
		    	}
		    	
		    	dto.setIdBancoBenef(funciones.convertirCadenaInteger(idBancoCasaCambio));
		    	dto.setIdChequeraBenef(chequeraCasaCambio.toString());	    	
		    	
		    	dto.setCliente(Cliente);		    	 
		    	dto.setIdCasaCambio(confirmacionCargoCtaService.traerNoPersona(idCasaDeCambio));
		    	dto.setDescCasaCambio(nomCasaDeCambio);
		    	
		    			    	
		    	
		    	
		    	dto.setIdOperador(0);
		    	dto.setNomOperador(nomOperador.toString());

		    	
		    	dto.setIdGrupo(funciones.convertirCadenaInteger(idGrupoEgreso));	    	
		    	dto.setIdRubro(funciones.convertirCadenaInteger(idRubroEgreso));
		    	
		    	dto.setGrupoIngreso( funciones.convertirCadenaInteger( idGrupoIngreso) );
		    	dto.setRubroIngreso( funciones.convertirCadenaInteger( idRubroIngreso ) );
		    	
		    	
		    	dto.setConcepto(concepto);	    	
		    	dto.setSolicita( firma1 ); 
		    	dto.setAutoriza( firma2 ); 	 
		    	
		    	dto.setDescDivisaVenta(descDivisaVenta);		
		    	dto.setDescDivisaCompra(descDivisaCompra);	
		    	dto.setDescBancoCargo(descBancoCargo);
			 	dto.setDescBancoAbono(descBancoAbono); 
			 	dto.setDescBancoCasa(descBancoCasa); 
			 	dto.setDescGrupoEgreso(descGrupoEgreso); 
			 	dto.setDescRubroEgreso(descRubroEgreso); 		
		    	
		    	return confirmacionCargoCtaService.ejecutar2(dto);
			    	
			    
			}catch(Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:validaCampos");
				e.printStackTrace();
				return  new ResultadoDto(false,    "Ocurrio un error en el Sistema. Favor de Reportarlo", null);
			}
			
		}//END METHOD: validaCampos
		
		@DirectMethod
		public Map<String, Object> contratoCompraVentaDeDivisas(Map datos, ServletContext context, String folioRepCVD) {
			
			try {
				
				ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl", context);
				return confirmacionCargoCtaService.reporteContratoCompraVentaDeDivisas(folioRepCVD);
							
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:TraspasosAction, M:reporteDetArchTraspInv");
				return null; 
			}
			
		}
		
		@DirectMethod
		public ResultadoDto ejecutarCompraDeTransfer( String folioPadre 		  , String foliosHijo 		 	 , String numRegistros 		 	, 	    
				  									  String noProveedor 		  , String nomProveedor 		 , String idDivisaTransfer     	,
				  									  String idBancoProveedor     , String descBancoProveedor    , String idChequeraProveedor   ,
				  									  String descDivisaTransfer   , String totalPagoTrans 	 	 , String idCasaDeCambio 		,
													  String nomCasaDeCambio      , String idBancoCasaDeCambio   , String nomBancoCasaDeCambio  , 				
													  String chequeraCadaDeCambio , String tipoDeCambio          , String idBancoPagador 		,
													  String nomBancoPagador      , String idChequeraPagadora    , String idDivisaDePago        ,
													  String descDivisaPago       , int noUsuario ){
			
			try {
				
				ConfirmacionCargoCtaService confirmacionCargoCtaService  = ( ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl" );
				return confirmacionCargoCtaService.ejecutarCompraDeTransfer(   folioPadre 		    ,  foliosHijo 		 	 ,  numRegistros 		 	, 	    
																			   noProveedor 		    ,  nomProveedor 		 ,  idDivisaTransfer     	,
																			   idBancoProveedor     ,  descBancoProveedor    ,  idChequeraProveedor     ,
																			   descDivisaTransfer   ,  totalPagoTrans 	 	 ,  idCasaDeCambio 		    ,
																			   nomCasaDeCambio      ,  idBancoCasaDeCambio   ,  nomBancoCasaDeCambio    , 				
																			   chequeraCadaDeCambio ,  tipoDeCambio          ,  idBancoPagador 		    ,
																			   nomBancoPagador      ,  idChequeraPagadora    ,  idDivisaDePago          ,
																			   descDivisaPago       ,  noUsuario );
				
				
							
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:ejecutarCompraDeTransfer");
				return null; 
			}
			
		}//END METHOD: ejecutarCompraDeTransfer
		
		@DirectMethod
		public Map<String, Object> contratoCompraTransfer(Map datos, ServletContext context, String folioRepCVD) {
			
			try {
				
				ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl", context);
				return confirmacionCargoCtaService.reporteContratoCompraTransfer(folioRepCVD);
							
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:TraspasosAction, M:reporteDetArchTraspInv");
				return null; 
			}
			
		}
		@DirectMethod
		public Map<String, Object> pagoParcial(double importeTotal,int folio, int piFolioRech , int psFolioRech, double saldoPendiente, String origenMov,
				 double interes, double iva) {
			Gson gson = new Gson();
			//if (!Utilerias.haveSession(WebContextManager.get())) 
			//	return null;
			Map<String,Object> mapRetorno = new HashMap<String,Object>();
			List<MovimientoDto> list = new ArrayList<MovimientoDto>();
			System.out.println("Action");
			try {
			//	if (Utilerias.haveSession(WebContextManager.get())) {
				ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
				mapRetorno = confirmacionCargoCtaService.pagoParcial(importeTotal,folio,piFolioRech,psFolioRech,saldoPendiente,origenMov,interes,iva);
			//	}
			}catch(Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C: ConfirmacionCargoCtaAction, M: pagoParcial");	
			}
			return mapRetorno;
		}
		@DirectMethod
		public List<AmortizacionCreditoDto> obtenerImporte(int folio) {
			if (!Utilerias.haveSession(WebContextManager.get())) 
				return null;
			List<AmortizacionCreditoDto> list = new ArrayList<AmortizacionCreditoDto>();
			try {
				if (Utilerias.haveSession(WebContextManager.get())) {
				confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
				list = confirmacionCargoCtaService.obtenerImportes(folio);
				}
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerImportes");
			}
			return list;
		}
		
		@DirectMethod
		public Map<String, Object> crearInteres(int folio,double interes,int secuencia,String cveControl) {
			Gson gson = new Gson();
			Map<String,Object> mapRetorno = new HashMap<String,Object>();
			List<MovimientoDto> list = new ArrayList<MovimientoDto>();
			System.out.println("Action crearInteres");
			try {
				ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
				mapRetorno = confirmacionCargoCtaService.crearInteres(folio,interes,secuencia,cveControl);
			}catch(Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C: ConfirmacionCargoCtaAction, M: pagoParcial");	
			}
			return mapRetorno;
		}
}