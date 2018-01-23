package com.webset.set.conciliacionbancoset.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletContext;

import net.sf.jasperreports.engine.JRDataSource;





import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.conciliacionbancoset.dto.ConciliaBancoDto;
import com.webset.set.conciliacionbancoset.dto.CriteriosBusquedaDto;
import com.webset.set.conciliacionbancoset.dto.MonitorConciliacionDTO;
import com.webset.set.conciliacionbancoset.dto.ParamBusquedaDto;
import com.webset.set.conciliacionbancoset.service.ConciliacionBancoSetService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.CatCtaBancoDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.utils.tools.Utilerias;
import com.webset.set.graficas.CreacionGrafica;
import com.webset.set.ingresos.dto.CuentaContableDto;

public class ConciliacionBancoSetAction {

	private Bitacora bitacora = new Bitacora();
	private Funciones funciones = new Funciones();
	private Contexto contexto = new Contexto();
	private ConciliacionBancoSetService conciliacionService;
	String conceptoFacturas = "";
	
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboBancos(int iEmpresa){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<LlenaComboGralDto> listDatos = new ArrayList<LlenaComboGralDto>();
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl"); 
			listDatos = conciliacionService.llenarComboBancos(iEmpresa);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:llenarComboBancos");
		}
		return listDatos;
	}
	
	@DirectMethod
	public List<CatCtaBancoDto> llenarGridChequeras(int noEmpresa, int noBanco){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;

		List<CatCtaBancoDto> listDatos = new ArrayList<CatCtaBancoDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl"); 
			listDatos = conciliacionService.consultarChequerasConciliar(noEmpresa, noBanco);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:llenarGridChequeras");
		}
		return listDatos;
	}
	
	@DirectMethod
	public String ejecutarConciliacionAutomaticaBS(String sRegistros){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		String result = "";
		List<CatCtaBancoDto> listDatos = new ArrayList<CatCtaBancoDto>();
		Gson gson = new Gson();
		List<Map<String, String>> paramsGrid = gson.fromJson(sRegistros, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl"); 
			
			for(int i = 0; i < paramsGrid.size(); i++) {
				CatCtaBancoDto dto = new CatCtaBancoDto();
				dto.setNoEmpresa(funciones.convertirCadenaInteger(paramsGrid.get(i).get("noEmpresa")));
				dto.setIdChequera(funciones.validarCadena(paramsGrid.get(i).get("idChequera")));
				dto.setIdBanco(funciones.convertirCadenaInteger(paramsGrid.get(i).get("noBanco")));
				dto.setDescBanco(funciones.validarCadena(paramsGrid.get(i).get("descBanco")));
				listDatos.add(dto);
			}
			result = conciliacionService.ejecutarConciliacionAutomaticaBS(listDatos);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:ejecutarConciliacionAutomaticaBS");
			return "Error en el action!!";
		}
		return result;
	}
	
	@DirectMethod
	public String consultarFechaConciliacion(int iBanco, int tEmpresa){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		String sFecha = "";
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl"); 
			sFecha = conciliacionService.consultarFechaConciliacion(iBanco, tEmpresa);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:consultarFechaConciliacion");
		}
		return sFecha;
		
	}
	/*
	public String validaDatos() {
		String result = "";
		
		try {
			if(Ext.getCmp(PF+'txtBanco').getValue() === '' || Ext.getCmp(PF+'cmbBanco').getValue() === '')
			{
				BFwrk.Util.msgShow('Debe seleccionar un banco','WARNING');
				return;
			}	
			
			if(regSelec.length == 0)
			{
				BFwrk.Util.msgShow('Debe seleccionar al menos una chequera','WARNING');
				return;
			}	
	        
	        if(fecIni === '' || fecFin === '')
			{
				BFwrk.Util.msgShow('Debe seleccionar dos fechas','WARNING');
				return;
			}	
			
	        if(fecIni > fecFin )
			{
				BFwrk.Util.msgShow('La fecha inicial debe ser menor a la fecha final','WARNING');
				return;
			}
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:validaDatos");
			return "Erro en validaci�n de parametros";
		}
		return result;
	}
	*/
	@DirectMethod
	public List<LlenaComboChequeraDto> llenarCmbChequeras(int iBanco, int iEmpresa, int iOpc){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<LlenaComboChequeraDto> list = new ArrayList<LlenaComboChequeraDto> ();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl");
			list = conciliacionService.llenarCmbChequeras(iBanco, iEmpresa, iOpc);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:llenarCmbChequeras");
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboChequeraDto> consultarEstatus(boolean bCancelacion){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<LlenaComboChequeraDto> list = new ArrayList<LlenaComboChequeraDto> ();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl");
			list = conciliacionService.consultarEstatus(bCancelacion);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:consultarEstatus");
		}
		return list;
	}
	
	/**
	 * valida si la chequera seleccionada no esta siendo conciliada
	 * @param iBanco
	 * @param sChequera
	 * @return
	 */
	@DirectMethod
	public Map<String, Object> validarConciliacion(int iBanco, String sChequera, boolean bCancelacion){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		Map<String, Object> mapRet = new HashMap<String, Object>();
		CriteriosBusquedaDto dto = new CriteriosBusquedaDto();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl");
				dto.setIdBanco(iBanco);
				dto.setIdChequera(sChequera);
			mapRet = conciliacionService.validarConciliacion(dto, bCancelacion);
			}
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:validarConciliacion");
		}
		return mapRet;
	}
	
	/**
	 * carga el grid de movimientos del banco
	 */
	@DirectMethod
	public List<ConciliaBancoDto> llenarMovsBanco(int iIdBanco, String sIdChequera, String sFecIni, String sFecFin, String sEstatus,
													String uMontoIni, String uMontoFin, String sCargoAbono, int iFormaPago, int iNoEmpresa){
		List<ConciliaBancoDto> movsBanco = new ArrayList<ConciliaBancoDto>();
		CriteriosBusquedaDto dto = new CriteriosBusquedaDto();
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
				conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl");
				dto.setIdBanco(iIdBanco);
				dto.setIdChequera(funciones.validarCadena(sIdChequera));
				if(!sFecIni.equals(""))
					dto.setFechaIni(funciones.ponerFechaDate(sFecIni));
				if(!sFecFin.equals(""))
					dto.setFechaFin(funciones.ponerFechaDate(sFecFin));
				dto.setEstatus(funciones.validarCadena(sEstatus));
				dto.setMontoIni(funciones.convertirCadenaDouble(uMontoIni.trim()));
				dto.setMontoFin(funciones.convertirCadenaDouble(uMontoFin.trim()));
				dto.setCargoAbono(funciones.validarCadena(sCargoAbono));
				dto.setFormaPago(iFormaPago);
				dto.setNoEmpresa(iNoEmpresa);
				movsBanco = conciliacionService.llenarMovsBanco(dto);
			}
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:llenarMovsBanco");
		}
		return movsBanco;
	}
	
	@DirectMethod
	public List<MovimientoDto> llenarMovsEmpresa(int iIdBanco, String sIdChequera, String sFecIni, String sFecFin, String sEstatus,
													String uMontoIni, String uMontoFin, String sCargoAbono, int iFormaPago, int iNoEmpresa){
		List<MovimientoDto> movsEmpresa = new ArrayList<MovimientoDto>();
		CriteriosBusquedaDto dto = new CriteriosBusquedaDto();
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl");
				dto.setIdBanco(iIdBanco);
				dto.setIdChequera(funciones.validarCadena(sIdChequera));
				if(!sFecIni.equals(""))
					dto.setFechaIni(funciones.ponerFechaDate(sFecIni));
				if(!sFecFin.equals(""))
					dto.setFechaFin(funciones.ponerFechaDate(sFecFin));
				dto.setEstatus(funciones.validarCadena(sEstatus));
				dto.setMontoIni(funciones.convertirCadenaDouble(uMontoIni.trim()));
				dto.setMontoFin(funciones.convertirCadenaDouble(uMontoFin.trim()));
				dto.setCargoAbono(funciones.validarCadena(sCargoAbono));
				dto.setFormaPago(iFormaPago);
				dto.setNoEmpresa(iNoEmpresa);
				movsEmpresa = conciliacionService.llenarMovsEmpresa(dto);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:llenarMovsEmpresa");
		}
		return movsEmpresa;
	}
	
	@DirectMethod
	public Map<String, Object> consultarContabiliza(String sRubro){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		Map<String, Object> mapRet = new HashMap<String, Object>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl");
			mapRet = conciliacionService.consultarContabiliza(sRubro);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:consultarContabiliza");
		}
		return mapRet;
	}
	
	
	@DirectMethod
	public Map<String, Object> crearMovimientoSET(String sRegistros, String sCriterios){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<ConciliaBancoDto> listBanco = new ArrayList<ConciliaBancoDto>();
		Gson gson = new Gson();
		List<Map<String, String>> paramsGrid = gson.fromJson(sRegistros, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<Map<String, String>> paramsCriterio = gson.fromJson(sCriterios, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl"); 
			
			for(int i = 0; i < paramsGrid.size(); i++){
				ConciliaBancoDto dto = new ConciliaBancoDto();
				dto.setIdEstatuscb(funciones.validarCadena(paramsGrid.get(i).get("idEstatuscb")));
				
				dto.setFecOperacion(funciones.ponerFechaDate3(paramsGrid.get(i).get("fecOperacion")));
				dto.setCargoAbono(funciones.validarCadena(paramsGrid.get(i).get("cargoAbono")));
				dto.setImporte(funciones.convertirCadenaDouble(paramsGrid.get(i).get("importe")));
				dto.setReferencia(funciones.validarCadena(paramsGrid.get(i).get("referencia")));
				dto.setSecuencia(funciones.convertirCadenaInteger(paramsGrid.get(i).get("secuencia")));
				dto.setNoCheque(funciones.convertirCadenaInteger(paramsGrid.get(i).get("noCheque")));
				dto.setConcepto(funciones.validarCadena(paramsGrid.get(i).get("concepto")));
				dto.setNoEmpresa(funciones.convertirCadenaInteger(paramsGrid.get(i).get("noEmpresa")));
				dto.setIdBanco(funciones.convertirCadenaInteger(paramsGrid.get(i).get("idBanco")));
				dto.setIdChequera(funciones.validarCadena(paramsGrid.get(i).get("idChequera")));
				listBanco.add(dto);
			}
			
			CriteriosBusquedaDto dtoParams = new CriteriosBusquedaDto(); 
			dtoParams.setIdBanco(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("idBanco")));
			dtoParams.setIdChequera(funciones.validarCadena(paramsCriterio.get(0).get("idChequera")));
			dtoParams.setIdGrupo(funciones.validarCadena(paramsCriterio.get(0).get("idGrupo")));
			dtoParams.setIdRubro(funciones.validarCadena(paramsCriterio.get(0).get("idRubro")));
			dtoParams.setContabiliza(funciones.convertirCadenaBoolean(paramsCriterio.get(0).get("contabiliza")));
			dtoParams.setDescRubro(funciones.validarCadena(paramsCriterio.get(0).get("descRubro")));
			dtoParams.setNomComputadora(funciones.validarCadena(paramsCriterio.get(0).get("nomComputadora")));
			
			mapResult = conciliacionService.crearMovimientoSET(listBanco, dtoParams);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:crearMovimientoSET");
		}
		return mapResult;
	}
	
	@DirectMethod
	public Map<String, Object> ejecutarConciliacionManualBS(String sGridBanco, String sGridEmpresa, String sCriterios){
		Map<String, Object> mapResult = new HashMap<String, Object>();
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<ConciliaBancoDto> listBanco = new ArrayList<ConciliaBancoDto>();
		List<MovimientoDto> listEmpresa = new ArrayList<MovimientoDto>();
		Gson gson = new Gson();
		List<Map<String, String>> paramsBanco = gson.fromJson(sGridBanco, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<Map<String, String>> paramsEmpresa = gson.fromJson(sGridEmpresa, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<Map<String, String>> paramsCriterio = gson.fromJson(sCriterios, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl"); 
			
			for(int i = 0; i < paramsBanco.size(); i++){
				ConciliaBancoDto dtoCB = new ConciliaBancoDto();
				dtoCB.setIdEstatuscb(funciones.validarCadena(paramsBanco.get(i).get("idEstatuscb")));
				dtoCB.setFecOperacion(funciones.ponerFechaDate3(paramsBanco.get(i).get("fecOperacion")));
				dtoCB.setCargoAbono(funciones.validarCadena(paramsBanco.get(i).get("cargoAbono")));
				dtoCB.setImporte(funciones.convertirCadenaDouble(paramsBanco.get(i).get("importe")));
				dtoCB.setReferencia(funciones.validarCadena(paramsBanco.get(i).get("referencia")));
				dtoCB.setSecuencia(funciones.convertirCadenaInteger(paramsBanco.get(i).get("secuencia")));
				dtoCB.setNoCheque(funciones.convertirCadenaInteger(paramsBanco.get(i).get("noCheque")));
				dtoCB.setConcepto(funciones.validarCadena(paramsBanco.get(i).get("concepto")));
				dtoCB.setNoEmpresa(funciones.convertirCadenaInteger(paramsBanco.get(i).get("noEmpresa")));
				dtoCB.setIdBanco(funciones.convertirCadenaInteger(paramsBanco.get(i).get("idBanco")));
				dtoCB.setIdChequera(funciones.validarCadena(paramsBanco.get(i).get("idChequera")));
				listBanco.add(dtoCB);
			}
			
			for(int j = 0; j < paramsEmpresa.size(); j ++){
				MovimientoDto dtoM = new MovimientoDto();
				dtoM.setIdEstatusCb(funciones.validarCadena(paramsEmpresa.get(j).get("idEstatusCb")));
				dtoM.setFecValor(funciones.ponerFechaDate3(paramsEmpresa.get(j).get("fecValor")));
				dtoM.setIdTipoMovto(funciones.validarCadena(paramsEmpresa.get(j).get("idTipoMovto")));
				dtoM.setImporte(funciones.convertirCadenaDouble(paramsEmpresa.get(j).get("importe")));
				dtoM.setReferencia(funciones.validarCadena(paramsEmpresa.get(j).get("referencia")));
				dtoM.setNoFolioDet(funciones.convertirCadenaInteger(paramsEmpresa.get(j).get("noFolioDet")));
				dtoM.setNoCheque(funciones.convertirCadenaInteger(paramsEmpresa.get(j).get("noCheque")));
				dtoM.setNoCuenta(funciones.convertirCadenaInteger(paramsEmpresa.get(j).get("noCuenta")));
				dtoM.setIdDivisa(funciones.validarCadena(paramsEmpresa.get(j).get("idDivisa")));
				dtoM.setNoDocto(funciones.validarCadena(paramsEmpresa.get(j).get("noDocto")));
				dtoM.setConcepto(funciones.validarCadena(paramsEmpresa.get(j).get("concepto")));
				dtoM.setNoFactura(funciones.validarCadena(paramsEmpresa.get(j).get("noFactura")));
				dtoM.setBeneficiario(funciones.validarCadena(paramsEmpresa.get(j).get("beneficiario")));
				dtoM.setTablaOrigen(funciones.validarCadena(paramsEmpresa.get(j).get("tablaOrigen")));
				dtoM.setIdEstatusMov(funciones.validarCadena(paramsEmpresa.get(j).get("idEstatusMov")));
				listEmpresa.add(dtoM);
			}
			
			CriteriosBusquedaDto dtoParams = new CriteriosBusquedaDto(); 
			dtoParams.setIdBanco(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("idBanco")));
			dtoParams.setIdChequera(funciones.validarCadena(paramsCriterio.get(0).get("idChequera")));
			dtoParams.setAclaracion(funciones.validarCadena(paramsCriterio.get(0).get("aclaracion")));
			dtoParams.setAjuste(funciones.convertirCadenaBoolean(paramsCriterio.get(0).get("bAjuste")));
			dtoParams.setDiferencia(funciones.convertirCadenaDouble(paramsCriterio.get(0).get("diferencia")));
			dtoParams.setCargoAbono(funciones.validarCadena(paramsCriterio.get(0).get("sCargoAbono")));
			
			mapResult = conciliacionService.ejecutarConciliacionManualBS(listBanco, listEmpresa, dtoParams);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:ejecutarConciliacionManualBS");
		}
		return mapResult;
	}
	
	/**
	 * carga el grid de movimientos del banco para la cancelacion de conciliaciones
	 */
	@DirectMethod
	public List<ConciliaBancoDto> llenarMovsBancoCancelacion(int iIdBanco, String sIdChequera, String sFecIni, String sFecFin, String sEstatus,
													String uMontoIni, String uMontoFin, String sCargoAbono, int iFormaPago, int iNoEmpresa,
													int iGrupoIni, int iGrupoFin){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<ConciliaBancoDto> movsBanco = new ArrayList<ConciliaBancoDto>();
		CriteriosBusquedaDto dto = new CriteriosBusquedaDto();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl");
				dto.setIdBanco(iIdBanco);
				dto.setIdChequera(funciones.validarCadena(sIdChequera));
				if(!sFecIni.equals(""))
					dto.setFechaIni(funciones.ponerFechaDate(sFecIni));
				if(!sFecFin.equals(""))
					dto.setFechaFin(funciones.ponerFechaDate(sFecFin));
				dto.setEstatus(funciones.validarCadena(sEstatus));
				dto.setMontoIni(funciones.convertirCadenaDouble(uMontoIni.trim()));
				dto.setMontoFin(funciones.convertirCadenaDouble(uMontoFin.trim()));
				dto.setCargoAbono(funciones.validarCadena(sCargoAbono));
				dto.setFormaPago(iFormaPago);
				dto.setNoEmpresa(iNoEmpresa);
				dto.setGrupoIni(iGrupoIni);
				dto.setGrupoFin(iGrupoFin);
			movsBanco = conciliacionService.llenarGridMovsBanco(dto);
			}
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:llenarMovsBancoCancelacion");
		}
		return movsBanco;
	}
	
	@DirectMethod
	public List<MovimientoDto> llenarMovsEmpresaCancelacion(int iIdBanco, String sIdChequera, String sFecIni, String sFecFin, String sEstatus,
													String uMontoIni, String uMontoFin, String sCargoAbono, int iFormaPago, int iNoEmpresa,
													int iGrupoIni, int iGrupoFin){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<MovimientoDto> movsEmpresa = new ArrayList<MovimientoDto>();
		CriteriosBusquedaDto dto = new CriteriosBusquedaDto();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl");
				dto.setIdBanco(iIdBanco);
				dto.setIdChequera(funciones.validarCadena(sIdChequera));
				if(!sFecIni.equals(""))
					dto.setFechaIni(funciones.ponerFechaDate(sFecIni));
				if(!sFecFin.equals(""))
					dto.setFechaFin(funciones.ponerFechaDate(sFecFin));
				dto.setEstatus(funciones.validarCadena(sEstatus));
				dto.setMontoIni(funciones.convertirCadenaDouble(uMontoIni.trim()));
				dto.setMontoFin(funciones.convertirCadenaDouble(uMontoFin.trim()));
				dto.setCargoAbono(funciones.validarCadena(sCargoAbono));
				dto.setFormaPago(iFormaPago);
				dto.setNoEmpresa(iNoEmpresa);
				dto.setGrupoIni(iGrupoIni);
				dto.setGrupoFin(iGrupoFin);
				movsEmpresa = conciliacionService.llenarGridMovsEmpresa(dto);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:llenarMovsEmpresaCancelacion");
		}
		return movsEmpresa;
	}
	
	@DirectMethod
	public Map<String, Object> cancelarConciliaciones(String sGridBanco, String sGridEmpresa, String sCriterios){
		Map<String, Object> mapResult = new HashMap<String, Object>();
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<ConciliaBancoDto> listBanco = new ArrayList<ConciliaBancoDto>();
		List<MovimientoDto> listEmpresa = new ArrayList<MovimientoDto>();
		Gson gson = new Gson();
		List<Map<String, String>> paramsBanco = gson.fromJson(sGridBanco, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<Map<String, String>> paramsEmpresa = gson.fromJson(sGridEmpresa, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<Map<String, String>> paramsCriterio = gson.fromJson(sCriterios, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl");
			
			for(int i = 0; i < paramsBanco.size(); i++){
				ConciliaBancoDto dtoCB = new ConciliaBancoDto();
				dtoCB.setGrupo(funciones.convertirCadenaInteger(paramsBanco.get(i).get("grupo")));
				dtoCB.setIdEstatuscb(funciones.validarCadena(paramsBanco.get(i).get("idEstatuscb")));
				dtoCB.setFecOperacion(funciones.ponerFechaDate(paramsBanco.get(i).get("fecOperacion")));
				dtoCB.setCargoAbono(funciones.validarCadena(paramsBanco.get(i).get("cargoAbono")));
				dtoCB.setImporte(funciones.convertirCadenaDouble(paramsBanco.get(i).get("importe")));
				dtoCB.setReferencia(funciones.validarCadena(paramsBanco.get(i).get("referencia")));
				dtoCB.setSecuencia(funciones.convertirCadenaInteger(paramsBanco.get(i).get("secuencia")));
				dtoCB.setNoCheque(funciones.convertirCadenaInteger(paramsBanco.get(i).get("noCheque")));
				dtoCB.setConcepto(funciones.validarCadena(paramsBanco.get(i).get("concepto")));
				dtoCB.setNoEmpresa(funciones.convertirCadenaInteger(paramsBanco.get(i).get("noEmpresa")));
				dtoCB.setIdBanco(funciones.convertirCadenaInteger(paramsBanco.get(i).get("idBanco")));
				dtoCB.setIdChequera(funciones.validarCadena(paramsBanco.get(i).get("idChequera")));
				dtoCB.setExportado(funciones.validarCadena(paramsBanco.get(i).get("exportado")));
				listBanco.add(dtoCB);
			}
			
			for(int j = 0; j < paramsEmpresa.size(); j ++){
				MovimientoDto dtoM = new MovimientoDto();
				dtoM.setGrupoPago(funciones.convertirCadenaInteger(paramsEmpresa.get(j).get("grupo")));
				dtoM.setIdEstatusCb(funciones.validarCadena(paramsEmpresa.get(j).get("idEstatusCb")));
				dtoM.setFecValor(funciones.ponerFechaDate(paramsEmpresa.get(j).get("fecValor")));
				dtoM.setIdTipoMovto(funciones.validarCadena(paramsEmpresa.get(j).get("idTipoMovto")));
				dtoM.setImporte(funciones.convertirCadenaDouble(paramsEmpresa.get(j).get("importe")));
				dtoM.setReferencia(funciones.validarCadena(paramsEmpresa.get(j).get("referencia")));
				dtoM.setNoFolioDet(funciones.convertirCadenaInteger(paramsEmpresa.get(j).get("noFolioDet")));
				dtoM.setNoCheque(funciones.convertirCadenaInteger(paramsEmpresa.get(j).get("noCheque")));
				dtoM.setIdDivisa(funciones.validarCadena(paramsEmpresa.get(j).get("idDivisa")));
				dtoM.setBEntregado(funciones.validarCadena(paramsEmpresa.get(j).get("exportado")));
				listEmpresa.add(dtoM);
			}
			
			CriteriosBusquedaDto dtoParams = new CriteriosBusquedaDto(); 
			dtoParams.setAclaracion(funciones.validarCadena(paramsCriterio.get(0).get("observacion")));
			
			mapResult = conciliacionService.cancelarConciliaciones(listBanco, listEmpresa, dtoParams);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:cancelarConciliaciones");
		}
		return mapResult;
	}
	
	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteCancelados(Map parameters, ServletContext context){
		JRDataSource jrDataSource = null;
		try{
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl", context);
			jrDataSource = conciliacionService.obtenerReporteCancelados(parameters);
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:obtenerReporteCancelados");
		}
		return jrDataSource;
	}
	
	@DirectMethod
	public List<MovimientoDto> llenarMovsFicticios(int iIdBanco, String sIdChequera, String sFecIni, String sFecFin, String sEstatus,
													String uMontoIni, String uMontoFin, String sCargoAbono, int iFormaPago, int iNoEmpresa){
		List<MovimientoDto> movsEmpresa = new ArrayList<MovimientoDto>();
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		CriteriosBusquedaDto dto = new CriteriosBusquedaDto();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl");
				dto.setIdBanco(iIdBanco);
				dto.setIdChequera(funciones.validarCadena(sIdChequera));
				if(!sFecIni.equals(""))
					dto.setFechaIni(funciones.ponerFechaDate(sFecIni));
				if(!sFecFin.equals(""))
					dto.setFechaFin(funciones.ponerFechaDate(sFecFin));
				dto.setEstatus(funciones.validarCadena(sEstatus));
				dto.setMontoIni(funciones.convertirCadenaDouble(uMontoIni.trim()));
				dto.setMontoFin(funciones.convertirCadenaDouble(uMontoFin.trim()));
				dto.setCargoAbono(funciones.validarCadena(sCargoAbono));
				dto.setFormaPago(iFormaPago);
				dto.setNoEmpresa(iNoEmpresa);
				movsEmpresa = conciliacionService.consultarMovsFicticios(dto);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:llenarMovsFicticios");
		}
		return movsEmpresa;
	}
	
	@DirectMethod
	public Map<String, Object> ejecutarMovsFicticios(String sGridEmpresa, String sCriterios){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		Map<String, Object> mapRet = new HashMap<String, Object>();
		List<MovimientoDto> listMov = new ArrayList<MovimientoDto>();
		Gson gson = new Gson();
		List<Map<String, String>> paramsEmpresa = gson.fromJson(sGridEmpresa, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<Map<String, String>> paramsCriterio = gson.fromJson(sCriterios, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl");
			
			for(int j = 0; j < paramsEmpresa.size(); j ++){
				MovimientoDto dtoM = new MovimientoDto();
				dtoM.setIdEstatusCb(funciones.validarCadena(paramsEmpresa.get(j).get("idEstatusCb")));
				dtoM.setFecValor(funciones.ponerFechaDate(paramsEmpresa.get(j).get("fecValor")));
				dtoM.setIdTipoMovto(funciones.validarCadena(paramsEmpresa.get(j).get("idTipoMovto")));
				dtoM.setImporte(funciones.convertirCadenaDouble(paramsEmpresa.get(j).get("importe")));
				dtoM.setIdTipoOperacion(funciones.convertirCadenaInteger(paramsEmpresa.get(j).get("idTipoOperacion")));
				dtoM.setReferencia(funciones.validarCadena(paramsEmpresa.get(j).get("referencia")));
				dtoM.setNoFolioDet(funciones.convertirCadenaInteger(paramsEmpresa.get(j).get("noFolioDet")));
				dtoM.setNoCheque(funciones.convertirCadenaInteger(paramsEmpresa.get(j).get("noCheque")));
				dtoM.setIdDivisa(funciones.validarCadena(paramsEmpresa.get(j).get("idDivisa")));
				dtoM.setConcepto(funciones.validarCadena(paramsEmpresa.get(j).get("concepto")));
				listMov.add(dtoM);
			}
			
			CriteriosBusquedaDto dto = new CriteriosBusquedaDto(); 
			dto.setAclaracion(funciones.validarCadena(paramsCriterio.get(0).get("observacion")));
			dto.setNoEmpresa(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("noEmpresa")));
			
			mapRet = conciliacionService.ejecutarMovsFicticios(listMov, dto);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:ejecutarMovsFicticios");
		}
		return mapRet;
	}
	
	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteMovsFicticios(Map parameters, ServletContext context){
		JRDataSource jrDataSource = null;
		try{
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl", context);
			jrDataSource = conciliacionService.obtenerReporteMovsFicticios(parameters);
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:obtenerReporteMovsFicticios");
		}
		return jrDataSource;
	}
	
	@DirectMethod
	public List<ConciliaBancoDto> llenarMovsDuplicados(int iIdBanco, String sIdChequera, String sFecIni, String sFecFin, String sEstatus,
													String uMontoIni, String uMontoFin, String sCargoAbono, int iNoEmpresa){
		List<ConciliaBancoDto> movsBanco = new ArrayList<ConciliaBancoDto>();
		CriteriosBusquedaDto dto = new CriteriosBusquedaDto();
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl");
				dto.setIdBanco(iIdBanco);
				dto.setIdChequera(funciones.validarCadena(sIdChequera));
				if(!sFecIni.equals(""))
					dto.setFechaIni(funciones.ponerFechaDate(sFecIni));
				if(!sFecFin.equals(""))
					dto.setFechaFin(funciones.ponerFechaDate(sFecFin));
				dto.setEstatus(funciones.validarCadena(sEstatus));
				dto.setMontoIni(funciones.convertirCadenaDouble(uMontoIni.trim()));
				dto.setMontoFin(funciones.convertirCadenaDouble(uMontoFin.trim()));
				dto.setCargoAbono(funciones.validarCadena(sCargoAbono));
				dto.setNoEmpresa(iNoEmpresa);
				movsBanco = conciliacionService.consultarMovsDuplicados(dto);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:llenarMovsDuplicados");
		}
		return movsBanco;
	}
	
	@DirectMethod
	public Map<String, Object> ejecutarMovsDuplicados(String sGridBanco, String sCriterios){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		Map<String, Object> mapRet = new HashMap<String, Object>();
		List<ConciliaBancoDto> listMov = new ArrayList<ConciliaBancoDto>();
		Gson gson = new Gson();
		List<Map<String, String>> paramsBanco = gson.fromJson(sGridBanco, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<Map<String, String>> paramsCriterio = gson.fromJson(sCriterios, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl");
			
			for(int j = 0; j < paramsBanco.size(); j ++){
				ConciliaBancoDto dtoCB = new ConciliaBancoDto();
				dtoCB.setIdEstatuscb(funciones.validarCadena(paramsBanco.get(j).get("idEstatuscb")));
				dtoCB.setFecOperacion(funciones.ponerFechaDate(paramsBanco.get(j).get("fecOperacion")));
				dtoCB.setCargoAbono(funciones.validarCadena(paramsBanco.get(j).get("cargoAbono")));
				dtoCB.setImporte(funciones.convertirCadenaDouble(paramsBanco.get(j).get("importe")));
				dtoCB.setReferencia(funciones.validarCadena(paramsBanco.get(j).get("referencia")));
				dtoCB.setSecuencia(funciones.convertirCadenaInteger(paramsBanco.get(j).get("secuencia")));
				dtoCB.setNoCheque(funciones.convertirCadenaInteger(paramsBanco.get(j).get("noCheque")));
				dtoCB.setConcepto(funciones.validarCadena(paramsBanco.get(j).get("concepto")));
				listMov.add(dtoCB);
			}
			
			CriteriosBusquedaDto dto = new CriteriosBusquedaDto(); 
			dto.setAclaracion(funciones.validarCadena(paramsCriterio.get(0).get("observacion")));
			dto.setNoEmpresa(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("noEmpresa")));
			
			mapRet = conciliacionService.ejecutarMovsDuplicados(listMov, dto);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:ejecutarMovsDuplicados");
		}
		return mapRet;
	}
	
	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteMovsDuplicados(Map parameters, ServletContext context){
		JRDataSource jrDataSource = null;
		try{
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl", context);
			jrDataSource = conciliacionService.obtenerReporteMovsDuplicados(parameters);
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:obtenerReporteMovsDuplicados");
		}
		return jrDataSource;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboBancosEmpresas(String sEmpresas){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<LlenaComboGralDto> listDatos = new ArrayList<LlenaComboGralDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl"); 
			listDatos = conciliacionService.llenarComboBancosEmpresa(sEmpresas.trim());
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:llenarComboBancosEmpresas");
		}
		return listDatos;
	}
	
	@DirectMethod
	public List<CatCtaBancoDto> llenarGridChequerasBanco(String sEmpresas, int iBanco){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<CatCtaBancoDto> listCheque = new ArrayList<CatCtaBancoDto>();
		try{
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl");
			listCheque = conciliacionService.llenarGridChequerasBanco(sEmpresas, iBanco);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:llenarGridChequerasBanco");
		}
		return listCheque;
	}
	
	@SuppressWarnings("unchecked")
	public List<JRDataSource> obtenerReporteDetalle(Map parameters, ServletContext context){
		List<JRDataSource> listJR = new ArrayList<JRDataSource>();
		try{
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl", context);
			listJR = conciliacionService.obtenerReporteDetalle(parameters);
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:obtenerReporteDetalle");
		}
		return listJR;
	}
	
	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteGlobal(Map parameters, ServletContext context){
		ParamBusquedaDto dto = new ParamBusquedaDto();
		JRDataSource jrDataSource = null;
		try{
			System.out.println("JCRE 0");
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl", context);
			dto.setIdEmpresa(funciones.convertirCadenaInteger(parameters.get("noEmpresa").toString()));
			dto.setIdBanco(funciones.convertirCadenaInteger(parameters.get("bancoIni").toString()));
			dto.setIdBanco2(funciones.convertirCadenaInteger(parameters.get("bancoFin").toString()));
			dto.setIdChequera(funciones.validarCadena(parameters.get("chequera").toString()));
			dto.setFechaIni(funciones.ponerFechaDate(parameters.get("fechaIni").toString()));
			dto.setFechaFin(funciones.ponerFechaDate(parameters.get("fechaFin").toString()));
			dto.setIdUsuario(funciones.convertirCadenaInteger(parameters.get("idUsuario").toString()));
			dto.setEstatus(funciones.validarCadena(parameters.get("estatus").toString()));
			jrDataSource = conciliacionService.reporteGlobal(dto);
			System.out.println("JCRE 1");
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:obtenerReporteGlobal");
		}
		return jrDataSource;
	}
	
	@DirectMethod
	@SuppressWarnings("unchecked")
	public MonitorConciliacionDTO obtenerReporteGlobalMonitor(ParamBusquedaDto dto){
//		ParamBusquedaDto dto = new ParamBusquedaDto();
		JRDataSource jrDataSource = null;
		try{
			System.out.println("JCRE 0");
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl");

			dto.setFechaIni(funciones.ponerFechaDate(dto.getsFechaIni()));
			dto.setFechaFin(funciones.ponerFechaDate(dto.getsFechaFin()));
			
			
			MonitorConciliacionDTO  monitor = conciliacionService.consultarMonitorConciliacion(dto);
			
			System.out.println("JCRE 1");
			
			return monitor;
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:obtenerReporteGlobal");
		}
		return null;
	}

	@DirectMethod
	public List<MovimientoDto> llenarMovsEmpresaVIngresos(int iIdBanco, String sIdChequera, String sFecIni, String sFecFin, String sEstatus,
													String uMontoIni, String uMontoFin, String sCargoAbono, int iFormaPago, int iNoEmpresa,
													int iUserId, int identificados) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<MovimientoDto> movsEmpresa = new ArrayList<MovimientoDto>();
		CriteriosBusquedaDto dto = new CriteriosBusquedaDto();
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl");
			dto.setIdBanco(iIdBanco);
			dto.setIdChequera(funciones.validarCadena(sIdChequera));
			
			if(!sFecIni.equals(""))
				dto.setFechaIni(funciones.ponerFechaDate(sFecIni));
			if(!sFecFin.equals(""))
				dto.setFechaFin(funciones.ponerFechaDate(sFecFin));
			
			dto.setMontoIni(funciones.convertirCadenaDouble(uMontoIni.trim()));
			dto.setMontoFin(funciones.convertirCadenaDouble(uMontoFin.trim()));
			dto.setNoEmpresa(iNoEmpresa);
			dto.setIdentificados(identificados);
			
			movsEmpresa = conciliacionService.llenarMovsEmpresaVIngresos(dto);
			
			if(movsEmpresa!=null && movsEmpresa.size()>0)
			{
				//Nombre de la grafica.
				String sName=iNoEmpresa+""+iIdBanco+sIdChequera;
				sName += (iFormaPago>0) ? iFormaPago : "";
				sName += (sEstatus!=null && !sEstatus.equals("0")) ? sEstatus : "";
				sName += (uMontoIni!=null && !uMontoIni.equals("0") && !uMontoIni.equals("")) ? uMontoIni.substring(0, uMontoIni.indexOf(".")) : "";
				sName += (uMontoFin!=null && !uMontoFin.equals("0") && !uMontoFin.equals("")) ? uMontoFin.trim().substring(0, uMontoFin.indexOf(".")-1) : "";
				
				if(sFecIni!=null && !sFecIni.equals("")) {
					sFecIni = sFecIni.trim();
					sFecFin = sFecFin.trim();
					sName += (!sFecIni.equals("")) ? sFecIni.replaceAll("/", "") : "";
					sName += (!sFecFin.equals("")) ? sFecFin.replaceAll("/", "") : "";
				}
				
				//Grafica: Fecha - Importe.
				Map<String, Double> datos = new TreeMap<String, Double>();
				//Grafica: Forma de pago - Importe.
				Map<String, Double> datosPie = new HashMap<String, Double>();
				
				for(int i=0; i<movsEmpresa.size(); i++){
					MovimientoDto movtoDto = (MovimientoDto)movsEmpresa.get(i);
					
					if(datos.containsKey(movtoDto.getFecValor().toString())){
						double dImporte = datos.get(movtoDto.getFecValor().toString());
						datos.remove(movtoDto.getFecValor().toString());
						datos.put(movtoDto.getFecValor().toString(), (movtoDto.getImporte()+dImporte));
					}
					else {
						datos.put(movtoDto.getFecValor().toString(), movtoDto.getImporte());
					}
					
					if(datosPie.containsKey(movtoDto.getDescFormaPago())){
						double dImporte = datosPie.get(movtoDto.getDescFormaPago());
						datosPie.remove(movtoDto.getDescFormaPago());
						datosPie.put(movtoDto.getDescFormaPago()+"", (movtoDto.getImporte()+dImporte));
					}
					else {
						datosPie.put(movtoDto.getDescFormaPago()+"", movtoDto.getImporte());
					}
				}
				
				CreacionGrafica cg = new CreacionGrafica();
				
				cg.crearGraficaPie   (iUserId+"", "ConsultaDepositos"+sName, "Importe por Forma de Depósito", datosPie);
				cg.crearGraficaBarras(iUserId+"", "ConsultaDepositos"+sName, "Importe por Fecha de Depósito", "Importe", datos, true);
				cg.crearGraficaLineas(iUserId+"", "ConsultaDepositos"+sName, "Importe por Fecha de Depósito", "Importe", datos, true);
				//Fin graficas
			}
			}
		}
		catch(Exception e){
			System.err.println(e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:llenarMovsEmpresa");
		}
		return movsEmpresa;
	}
	
	@DirectMethod
	public Map<String, Object> updateMovimientoSETVIngresos(String sRegistros, String sCriterios){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<ConciliaBancoDto> listBanco = new ArrayList<ConciliaBancoDto>();
		
		String concepto;		
		concepto = "";
		
		Gson gson = new Gson();
		
		List<Map<String, String>> paramsGrid = gson.fromJson(sRegistros, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<Map<String, String>> paramsCriterio = gson.fromJson(sCriterios, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl"); 
			
			for(int i = 0; i < paramsGrid.size(); i++){
				
				ConciliaBancoDto dto = new ConciliaBancoDto();				
				dto.setFolioBanco( paramsGrid.get(i).get("idChequera")  );				
				listBanco.add(dto);
				
			}
			
			CriteriosBusquedaDto dtoParams = new CriteriosBusquedaDto(); 
			dtoParams.setIdGrupo(funciones.validarCadena(paramsCriterio.get(0).get("idGrupo")));
			dtoParams.setIdRubro(funciones.validarCadena(paramsCriterio.get(0).get("idRubro")));
			dtoParams.setIdSubGrupo(Integer.parseInt(funciones.validarCadena(paramsCriterio.get(0).get("idSubGrupo"))));
			dtoParams.setIdSubSubGrupo(Integer.parseInt(funciones.validarCadena(paramsCriterio.get(0).get("idSubSubGrupo"))));
			dtoParams.setIdRubroC(Integer.parseInt(funciones.validarCadena(paramsCriterio.get(0).get("idRubroC"))));
			dtoParams.setCuentaContable(funciones.validarCadena(paramsCriterio.get(0).get("ctaCargo")));
			
			concepto += paramsCriterio.get(0).get("cliente") + "/";
			concepto += paramsCriterio.get(0).get("factura") + "/";
			concepto += paramsCriterio.get(0).get("concepto");
			concepto = concepto.toUpperCase();
			
			dtoParams.setDescRubro(conceptoFacturas.equals("") ? concepto : conceptoFacturas);
			
			mapResult = conciliacionService.updateMovimientoSETVIngresos(listBanco, dtoParams);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:updateMovimientoSETVIngresos");
		}
		return mapResult;
	}
	
	@DirectMethod
	public List<CuentaContableDto> getCuentaContable(int noEmpresa, String idGrupo, String idRubro ){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		
		List<CuentaContableDto> listDatos = new ArrayList<CuentaContableDto>();
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl"); 
			listDatos = conciliacionService.getCuentaContable(noEmpresa, idGrupo,idRubro);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:llenarComboBancos");
		}
		return listDatos;
	}
	
	@DirectMethod
	public List<CuentaContableDto> consultarFacturasCXC(String noCliente, int noEmpresa){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		
		List<CuentaContableDto> listDatos = new ArrayList<CuentaContableDto>();
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl"); 
			listDatos = conciliacionService.consultarFacturasCXC(noCliente, noEmpresa);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:llenarComboBancos");
		}
		return listDatos;
	}
	
	@DirectMethod
	public String clasificaIngresos(String sRegistros, boolean ietu, boolean iva, String concepto, String sRegistrosMov, String sCriterios){
		Gson gson = new Gson();
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<Map<String, String>> datosGrid = gson.fromJson(sRegistros, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		String resul = "";
		Map<String, Object> mapRet = new HashMap<String, Object>();
		conceptoFacturas = concepto;
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			mapRet = updateMovimientoSETVIngresos(sRegistrosMov, sCriterios);
			
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl");
			
			if(mapRet.get("msgUsuario").toString().equals("Se ejecuto correctamente la operacion"))
				resul = conciliacionService.clasificaIngresos(datosGrid,ietu, iva, concepto);
			else
				resul = "El registro no fue procesado correctamente en movimiento!!";
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:clasificaIngresos");
		}
		return resul;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboDeptos(int noEmpresa){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		
		List<LlenaComboGralDto> listDatos = new ArrayList<LlenaComboGralDto>();
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl"); 
			listDatos = conciliacionService.llenarComboDeptos(noEmpresa);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:llenarComboBancos");
		}
		return listDatos;
	}
	

	@DirectMethod
	public List<LlenaComboGralDto> llenarComboEmpresa(int iUsuario){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<LlenaComboGralDto> listDatos = new ArrayList<LlenaComboGralDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl"); 
			listDatos = conciliacionService.llenarComboEmpresa(iUsuario);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:llenarComboEmpresa");
		}
		return listDatos;
	}
	
	@DirectMethod
	public String validaDatos(int regBanco, int regEmpresa, double dif, String aclaracion){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			if(regBanco == 0 || regEmpresa == 0)
				return "Debe seleccionar movimientos en ambos lados para conciliar";
			else if(dif != 0 && aclaracion.equals(""))
				return "Necesita aclaraci�n si hay diferencia";
			else if(dif == 0 && !aclaracion.equals(""))
				return "No necesita aclaraci�n, si no hay diferencia";
			else if(dif <= -20 || dif >= 20) {
				return "La diferencia no puede ser mayor a 20";
			}
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:validaDatos");
			return "Error en clase action";
		}
		return "";
	}
	
	@DirectMethod
	public String validaDatosBusqueda(String parametros){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		Gson gson = new Gson();
		List<Map<String, String>> datos = gson.fromJson(parametros, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		boolean banco = false;
		boolean cheque = false;
		boolean fecha = false;
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				for(int i=0; i<datos.size(); i++) {
					if(datos.get(i).get("criterio").equals("BANCO") && datos.get(i).get("id").equals(""))
						return "Debe seleccionar un valor para el criterio de banco.";
					else if(datos.get(i).get("criterio").equals("CHEQUERA") && datos.get(i).get("id").equals(""))
						return "Debe seleccionar un valor para el criterio de chequera.";
					else if(datos.get(i).get("criterio").equals("FECHA") && datos.get(i).get("id").equals(""))
						return "Debe seleccionar un valor para el criterio de fechas.";
					else if(datos.get(i).get("criterio").equals("ESTATUS") && datos.get(i).get("id").equals(""))
						return "Debe seleccionar un valor para el criterio de estatus.";
					else if(datos.get(i).get("criterio").equals("MONTOS") && datos.get(i).get("id").equals(""))
						return "Debe ingresas un valor para el criterio de montos.";
					else if(datos.get(i).get("criterio").equals("CARGO/ABONO") && datos.get(i).get("id").equals(""))
						return "Debe seleccionar un valor para el criterio de cargo/abono.";
					else if(datos.get(i).get("criterio").equals("FORMA DE PAGO") && datos.get(i).get("id").equals(""))
						return "Debe seleccionar un valor para el criterio de forma de pago.";
					else if(datos.get(i).get("criterio").equals("BANCO"))
						banco = true;
					else if(datos.get(i).get("criterio").equals("CHEQUERA"))
						cheque = true;
					else if(datos.get(i).get("criterio").equals("FECHA"))
						fecha = true;
				}
				if(!banco)
					return "Debe seleccionar el criterio de banco.";
				else if(!cheque)
					return "Debe seleccionar el criterio de chequera.";
				else if(!fecha)
					return "Debe seleccionar el criterio de fecha.";
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:validaDatos");
			return "Error en clase action";
		}
		return "";
	}

	@DirectMethod
	public Map<String, String> consultarHeadersMonitor(int idBanco, int noEmpresa, String chequera){
		Map<String, String> headers = new HashMap<String, String>();
		try{
			CriteriosBusquedaDto dto = new CriteriosBusquedaDto();
			dto.setIdBanco(idBanco);
			dto.setNoEmpresa(noEmpresa);;
			dto.setIdChequera(chequera);
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl"); 
			headers = conciliacionService.consultarHeadersMonitor(dto);
		}catch(Exception e){
			headers.put("Error", e.getMessage());
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:llenarComboBancos");
		}
		
		System.out.println("Al regreso en el action"+headers);
		return headers;
	}
	
	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteMonitor(Map datosReporte, ServletContext context){
		JRDataSource jrDataSource = null;
		try{
			conciliacionService = (ConciliacionBancoSetService) contexto.obtenerBean("conciliacionBancoSetBusinessImpl", context);
			jrDataSource = conciliacionService.obtenerReporteMonitor(datosReporte);
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:obtenerReporteCancelados");
		}
		return jrDataSource;
	}
	
}//End class
