package com.webset.set.caja.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import net.sf.jasperreports.engine.JRDataSource;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.caja.service.CajaService;
import com.webset.set.impresion.dto.ConsultaChequesDto;
import com.webset.set.traspasos.dto.MovimientoDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;

public class CajaAction {

	private Bitacora bitacora = new Bitacora();
	private Contexto contexto = new Contexto();
	private Funciones funciones = new Funciones();
	private CajaService cajaService;
	private static Logger logger = Logger.getLogger(CajaAction.class);
	@DirectMethod
	public List<LlenaComboEmpresasDto> llenarComboEmpresas(int idUsuario){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<LlenaComboEmpresasDto> list = new ArrayList<LlenaComboEmpresasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			cajaService = (CajaService) contexto.obtenerBean("cajaBusinessImpl");
			list = cajaService.llenarComboEmpresas(idUsuario);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Caja, C:CajaAction, M:llenarComboEmpresas");
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboBancos(int iEmpresa){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			cajaService = (CajaService) contexto.obtenerBean("cajaBusinessImpl");
			list = cajaService.llenarComboBancos(iEmpresa);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Caja, C:CajaAction, M:llenarComboBancos");
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboBancosVentana(int iEmpresa){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			cajaService = (CajaService) contexto.obtenerBean("cajaBusinessImpl");
			list = cajaService.llenarComboBancosVentana(iEmpresa);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Caja, C:CajaAction, M:llenarComboBancosVentana");
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboDivisaDto> llenarComboDivisas(int cnt){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<LlenaComboDivisaDto> list = new ArrayList<LlenaComboDivisaDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			cajaService = (CajaService) contexto.obtenerBean("cajaBusinessImpl");
			list = cajaService.llenarComboDivisas(cnt);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Caja, C:CajaAction, M:llenarComboDivisas");
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboChequeraDto> llenarComboChequeras(int iBanco, int iEmpresa){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<LlenaComboChequeraDto> list = new ArrayList<LlenaComboChequeraDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			cajaService = (CajaService) contexto.obtenerBean("cajaBusinessImpl");
			list = cajaService.llenarComboChequeras(iBanco, iEmpresa);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Caja, C:CajaAction, M:llenarComboChequeras");
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboChequeraDto> llenarChequerasVentana(int iBanco, int iEmpresa){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<LlenaComboChequeraDto> list = new ArrayList<LlenaComboChequeraDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			cajaService = (CajaService) contexto.obtenerBean("cajaBusinessImpl");
			list = cajaService.llenarChequerasVentana(iBanco, iEmpresa);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Caja, C:CajaAction, M:llenarChequerasVentana");
		}
		return list;
	}
	
	@DirectMethod
	public List<MovimientoDto> llenarGridConsultaCheques(String sEmpresa, String sCaja, String sBanco, String sDivisa, String sChequera,
														String sConcepto, String sFechaIni, String sFechaFin, String sMontoIni, String sMontoFin,
														String sBeneficiario, String sNoChequeIni, String sNoChequeFin){
		List<MovimientoDto> list = new ArrayList<MovimientoDto> ();
		ConsultaChequesDto dtoCheques = new ConsultaChequesDto(); 
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			cajaService = (CajaService) contexto.obtenerBean("cajaBusinessImpl");
			dtoCheques.setNoEmpresa(funciones.convertirCadenaInteger(sEmpresa));
			dtoCheques.setIdCaja(funciones.convertirCadenaInteger(sCaja));
			dtoCheques.setCriterioBanco(funciones.convertirCadenaInteger(sBanco));
			dtoCheques.setCriterioDivisa(funciones.validarCadena(sDivisa));
			dtoCheques.setCriterioChequera(funciones.validarCadena(sChequera));
			dtoCheques.setCriterioConcepto(funciones.validarCadena(sConcepto));
			if(!sFechaIni.equals(""))
				dtoCheques.setCriterioFechaIni(funciones.ponerFechaDate(sFechaIni));
			else
				dtoCheques.setCriterioFechaIni(null);
			if(!sFechaFin.equals(""))
				dtoCheques.setCriterioFechaFin(funciones.ponerFechaDate(sFechaFin));
			else 
				dtoCheques.setCriterioFechaFin(null);
			dtoCheques.setCriterioImporteIni(funciones.convertirCadenaDouble(sMontoIni));
			dtoCheques.setCriterioImporteFin(funciones.convertirCadenaDouble(sMontoFin));
			dtoCheques.setCriterioBeneficiario(funciones.validarCadena(sBeneficiario));
			dtoCheques.setCriterioSolicitudIni(funciones.convertirCadenaInteger(sNoChequeIni));
			dtoCheques.setCriterioSolicitudFin(funciones.convertirCadenaInteger(sNoChequeFin));
			
			list = cajaService.llenarGridChequesPorEntregar(dtoCheques);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Caja, C:CajaAction, M:llenarGridConsultaCheques");
		}
		return list;
	}
	
	@DirectMethod
	public Map<String, Object> ejecutaEntregaCheques(String datos, String fecHoy, int usuario){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		Map<String, Object> mapRetorno = new HashMap<String, Object>();
		List<MovimientoDto> list = new ArrayList<MovimientoDto>();
		Gson gson = new Gson();
		List<Map<String, String>> params = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			for(int i = 0; i < params.size(); i++) {
				if(!funciones.validarCadena(params.get(i).get("idEstatusMov")).equals("I")) {
					mapRetorno.put("msgUsuario", "El movimiento con No. folio " + params.get(i).get("noFolioDet").toString() + " no se a impreso");
					return mapRetorno;
				}
			}
			
			cajaService = (CajaService) contexto.obtenerBean("cajaBusinessImpl");
			for(int i = 0; i < params.size(); i++){
				MovimientoDto dto = new MovimientoDto();
				dto.setNoEmpresa(funciones.convertirCadenaInteger(params.get(i).get("noEmpresa")));
				dto.setNombreProvDivisas(funciones.validarCadena(params.get(i).get("nomEmpresa")));
				dto.setDescBanco(funciones.validarCadena(params.get(i).get("descBanco")));
				dto.setIdChequera(funciones.validarCadena(params.get(i).get("idChequera")));
				dto.setImporte(funciones.convertirCadenaDouble(params.get(i).get("importe")));
				dto.setIdDivisa(funciones.validarCadena(params.get(i).get("idDivisa")));
				dto.setNoCheque(funciones.convertirCadenaInteger(params.get(i).get("noCheque")));
				dto.setBeneficiario(funciones.validarCadena(params.get(i).get("beneficiario")));
				dto.setFecValor(funciones.ponerFechaDate(params.get(i).get("fecValor")));
				dto.setConcepto(funciones.validarCadena(params.get(i).get("concepto")));
				dto.setNoFolioDet(funciones.convertirCadenaInteger(params.get(i).get("noFolioDet")));
				dto.setIdTipoOperacion(funciones.convertirCadenaInteger(params.get(i).get("idTipoOperacion")));
				dto.setIdBanco(funciones.convertirCadenaInteger(params.get(i).get("idBanco")));
				dto.setCveControl(funciones.validarCadena(params.get(i).get("cveControl")));
				list.add(dto);
			}
			
			mapRetorno = cajaService.ejecutarEntregaCheques(list, fecHoy, usuario);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Caja, C:CajaAction, M:ejecutaEntregaCheques");
		}
		return mapRetorno;
	}
	
	@SuppressWarnings("unchecked")
	@DirectMethod
	public JRDataSource obtenerDatosReporteCheque(String nomReporte, Map parameters, ServletContext context){
		JRDataSource jrDataSource = null;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			cajaService = (CajaService) contexto.obtenerBean("cajaBusinessImpl",context);
			jrDataSource = cajaService.llenarReporteCheques(nomReporte, parameters);
			}
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Caja, C:CajaAction, M:ejecutaEntregaCheques");
		}
		return jrDataSource;
	}
	
	@SuppressWarnings("unchecked")
	@DirectMethod
	public List<Map<String, Object>> obtenerReporteChequesPorEntregar(String datos){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		logger.info("action " + datos.toString());
		List<Map<String, Object>> mapResult = null;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
		Gson gson = new Gson();
		List<Map<String, String>> params = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
			cajaService = (CajaService) contexto.obtenerBean("cajaBusinessImpl");
			logger.info("params = "+params.toString());
			mapResult = cajaService.llenarReporteChequesPorEntregar((Map<String, Object>) params);
			}
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Caja, C:CajaAction, M:obtenerReporteChequesPorEntregar");
		}
		return mapResult;
	}
}
