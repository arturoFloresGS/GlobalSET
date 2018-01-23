package com.webset.set.financiamiento.action;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.ServletContext;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.financiamiento.dto.AmortizacionCreditoDto;
import com.webset.set.financiamiento.dto.AvalGarantiaDto;
import com.webset.set.financiamiento.dto.BitacoraCreditoBanDto;
import com.webset.set.financiamiento.dto.ContratoCreditoDto;
import com.webset.set.financiamiento.dto.DisposicionCreditoDto;
import com.webset.set.financiamiento.dto.ObligacionCreditoDto;
import com.webset.set.financiamiento.dto.Parametro;
import com.webset.set.financiamiento.dto.ProvisionCreditoDTO;
import com.webset.set.financiamiento.service.AltaFinanciamientoService;
import com.webset.set.financiamiento.service.FinanciamientoModificacionCService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.Retorno;
import com.webset.utils.tools.Utilerias;

public class AltaFinanciamientoAction {

	Bitacora bitacora = new Bitacora();
	Contexto contexto = new Contexto();
	Funciones funciones = new Funciones();
	AltaFinanciamientoService altaFinanciamientoService;
	private GlobalSingleton globalSingleton;

	@DirectMethod
	public List<Retorno> consultarConfiguraSetTodos() {
		List<Retorno> retorno = null;
		try {
		//	if (Utilerias.haveSession(WebContextManager.get())) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				retorno = altaFinanciamientoService.consultarConfiguraSet();
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:consultarConfiguraSetTodos");
		}
		return retorno;
	}
	@DirectMethod
	public List<LlenaComboGralDto> obtenerEmpresas(int idUsuario, boolean bMantenimiento) {
		List<LlenaComboGralDto> listEmpresas = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listEmpresas;
		try {
			AltaFinanciamientoService altaFinanciamientoService = (AltaFinanciamientoService) contexto.obtenerBean("altaFinanciamientoBusinessImpl");
			listEmpresas = altaFinanciamientoService.obtenerEmpresas(idUsuario, bMantenimiento);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoAction, M:obtenerEmpresas");
		}
		return listEmpresas;
	}
	@DirectMethod
	public List<LlenaComboGralDto> obtenerContratos(int empresa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
		//	// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.obtenerContratos(empresa);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:obtenerContratos");
		}
		return list;
	}
	@DirectMethod
	public List<LlenaComboGralDto> obtenerPais() {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
		//	// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.obtenerPais();
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:");
		}
		return list;
	}

	@DirectMethod
	public List<LlenaComboGralDto> obtenerBancos(String psNacionalidad, String psDivisa, int noEmpresa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
		//	// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.obtenerBancos(psNacionalidad, psDivisa,noEmpresa);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:");
		}
		return list;
	}

	@DirectMethod
	public List<LlenaComboGralDto> obtenerTipoContratos(String psTipoFinan, boolean pbTipoContrato) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
		//	// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.obtenerTipoContratos(psTipoFinan, pbTipoContrato);

		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:obtenerTipoContratos");
		}
		return list;
	}
@DirectMethod
	public List<LlenaComboGralDto> obtenerDestinoRecursos() {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
		//	// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.obtenerDestinoRecursos();

		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:obtenerTipoContratos");
		}
		return list;
	}

	@DirectMethod
	public List<LlenaComboGralDto> obtenerDivisas(boolean bRestringido) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			//// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.obtenerDivisas(bRestringido);
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:obtenerDivisas");
		}
		return list;
	}
	@DirectMethod
	public List<LlenaComboGralDto> obtenerTasa() {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			//// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.obtenerTasa();
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:obtenerTasa");
		}
		return list;
	}
	@DirectMethod
	public List<LlenaComboGralDto> obtenerArrendadoras() {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
		//	// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.obtenerArrendadoras();
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:obtenerArrendadoras");
		}
		return list;
	}
	@DirectMethod
	public List<ContratoCreditoDto> obtenerContratoCredito(String clave) {
		List<ContratoCreditoDto> list = new ArrayList<ContratoCreditoDto>();
		try {
			//// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.obtenerContratoCredito(clave);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:obtenerContratoCredito");
		}
		return list;
	}
	@DirectMethod
	public List<ContratoCreditoDto> obtenerNoDisp(String idFin) {
		List<ContratoCreditoDto> list = new ArrayList<ContratoCreditoDto>();
		try {
		//	// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.obtenerNoDisp(idFin);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:obtenerNoDisp");
		}
		return list;
	}
	@DirectMethod
	public List<ContratoCreditoDto> obtenerTipoCambio(String idDiv) {
		List<ContratoCreditoDto> list = new ArrayList<ContratoCreditoDto>();
		try {
		//	// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.obtenerTipoCambio(idDiv);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:obtenerTipoCambio");
		}
		return list;
	}
	@DirectMethod
	public List<LlenaComboGralDto> obtenerDisposiciones(String psIdContrato, boolean pbEstatus) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.obtenerDisposiciones(psIdContrato, pbEstatus);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:obtenerDisposiciones");
		}
		return list;
	}
	@DirectMethod
	public List<ContratoCreditoDto> selectPrefijo(int piBanco) {
		List<ContratoCreditoDto> list = new ArrayList<ContratoCreditoDto>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.selectPrefijo(piBanco);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:selectPrefijo");
		}
		return list;
	}
	@DirectMethod
	public List<ContratoCreditoDto> selectConsecutivoLinea() {
		List<ContratoCreditoDto> list = new ArrayList<ContratoCreditoDto>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.selectConsecutivoLinea();
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:selectConsecutivoLinea");
		}
		return list;
	}
	@DirectMethod
	public List<ContratoCreditoDto> selectInhabil(String pvFechaInhabil) {
		List<ContratoCreditoDto> list = new ArrayList<ContratoCreditoDto>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.selectInhabil(pvFechaInhabil);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:selectInhabil");
		}
		return list;
	}
	@DirectMethod
	public List<ContratoCreditoDto> selectContratoCredito(String psIdContrato) {
		List<ContratoCreditoDto> list = new ArrayList<ContratoCreditoDto>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.selectContratoCredito(psIdContrato);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:selectContratoCredito");
		}
		return list;
	}
	@DirectMethod
	public Map<String, Object> altaContrato(String psFinanciamiento, String psPais, int piBanco, String psClabe,
			int piTipoFinancia, int piComun, String psFecIni, String psFecVen, String psDivisa, double pdMontoAuto,
			double pdMontoDisp, String psTasa, String psTipoOper, String psObliga1, String psObliga2, double pdSpreed,
			String piRevolvencia, String psEstatus, int piBancoLin, String psRecorreFecha, String psLargoPlazo,
			String psRecSoloFecha, int piFideicomiso, int piAgente, String piReestructura,int noEmpresa) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			ContratoCreditoDto dto = new ContratoCreditoDto();
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				dto.setIdFinanciamiento(psFinanciamiento);
				dto.setIdPais(psPais);
				dto.setIdBanco(piBanco);
				dto.setNoCuenta(psClabe);
				dto.setIdtipoFinanciamiento(piTipoFinancia);
				dto.setIdComun(piComun);
				dto.setFecInicio(psFecIni);
				dto.setFecVencimiento(psFecVen);
				dto.setIdDivisa(psDivisa);
				dto.setMontoAutorizado(pdMontoAuto);
				dto.setMontoDisposicion(pdMontoDisp);
				dto.setTasaLinea(psTasa);
				dto.setTipoOperacion(psTipoOper);
				dto.setObliga1(psObliga1);
				dto.setObliga2(psObliga2);
				dto.setSpreed(pdSpreed);
				dto.setRevolvencia(piRevolvencia.charAt(0));
				dto.setEstatus(psEstatus);
				dto.setIdBancoPrestamo(piBancoLin);
				dto.setFechaAntpost(psRecorreFecha.charAt(0));
				dto.setLargoPlazo(psLargoPlazo.charAt(0));
				dto.setRecfechaAntpost(psRecSoloFecha.charAt(0));
				dto.setIdBancoFideicomiso(piFideicomiso);
				dto.setAgente(piAgente);
				dto.setReestructura(piReestructura.charAt(0));
				mapResult = altaFinanciamientoService.altaContrato(dto,noEmpresa);
		//	}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:altaContrato");
		}
		return mapResult;
	}
	@DirectMethod
	public List<ContratoCreditoDto> selectExisteDispAmort(String psIdContrato) {
		List<ContratoCreditoDto> list = new ArrayList<ContratoCreditoDto>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.selectExisteDispAmort(psIdContrato);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:selectExisteDispAmort");
		}
		return list;
	}
	@DirectMethod
	public Map<String, Object> deleteDispAmortizacion(String psFinanciamiento) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			ContratoCreditoDto dto = new ContratoCreditoDto();
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				mapResult = altaFinanciamientoService.deleteDispAmortizacion(psFinanciamiento);
		//	}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:deleteDispAmortizacion");
		}
		return mapResult;
	}
	@DirectMethod
	public List<DisposicionCreditoDto> selectDisposicionCred(String psIdContrato, int piDisposicion) {
		List<DisposicionCreditoDto> list = new ArrayList<DisposicionCreditoDto>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.selectDisposicionCred(psIdContrato, piDisposicion);
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:selectDisposicionCred");
		}
		return list;
	}
	@DirectMethod
	public List<ContratoCreditoDto> selectAltaAmortizaciones(String psIdContrato) {
		List<ContratoCreditoDto> list = new ArrayList<ContratoCreditoDto>();
		try {

			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.selectAltaAmortizaciones(psIdContrato);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:selectAltaAmortizaciones");
		}
		return list;
	}
	@DirectMethod
	public Map<String, Object> updateLinea(String psFinanciamiento, String psPais, int piBanco, String psClabe,
			int piTipoFinancia, int piComun, String psFecIni, String psFecVen, String psDivisa, double pdMontoAuto,
			double pdMontoDisp, String psTasa, String psTipoOper, double pdSpreed, String piRevolvencia, int piBancoLin,
			String psRecorreFecha, String psLargoPlazo, int piFideicomiso, int piAgente, String psRecSoloFecha,
			String piReestructura) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			ContratoCreditoDto dto = new ContratoCreditoDto();
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				dto.setIdFinanciamiento(psFinanciamiento);
				dto.setIdPais(psPais);
				dto.setIdBanco(piBanco);
				dto.setNoCuenta(psClabe);
				dto.setIdtipoFinanciamiento(piTipoFinancia);
				dto.setIdComun(piComun);
				dto.setFecInicio(psFecIni);
				dto.setFecVencimiento(psFecVen);
				dto.setIdDivisa(psDivisa);
				dto.setMontoAutorizado(pdMontoAuto);
				dto.setMontoDisposicion(pdMontoDisp);
				dto.setTasaLinea(psTasa);
				dto.setTipoOperacion(psTipoOper);
				dto.setSpreed(pdSpreed);
				dto.setRevolvencia(piRevolvencia.charAt(0));
				dto.setIdBancoPrestamo(piBancoLin);
				dto.setFechaAntpost(psRecorreFecha.charAt(0));
				dto.setLargoPlazo(psLargoPlazo.charAt(0));
				dto.setRecfechaAntpost(psRecSoloFecha.charAt(0));
				dto.setIdBancoFideicomiso(piFideicomiso);
				dto.setAgente(piAgente);
				dto.setReestructura(piReestructura.charAt(0));
				mapResult = altaFinanciamientoService.updateLinea(dto);
		//	}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:updateLinea");
		}
		return mapResult;
	}

	@DirectMethod
	public List<LlenaComboGralDto> obtenerEquivalencia(String psDesBanco, int piBanco) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.obtenerEquivalencia(psDesBanco, piBanco);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:obtenerEquivalencia");
		}
		return list;
	}
	@DirectMethod
	public List<ContratoCreditoDto> funSQLTasa(String psTasa) {
		List<ContratoCreditoDto> list = new ArrayList<ContratoCreditoDto>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.funSQLTasa(psTasa);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:funSQLTasa");
		}
		return list;
	}
	@DirectMethod
	public List<LlenaComboGralDto> funSQLComboClabe(int pvvValor2, String psDivisa,int noEmpresa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.funSQLComboClabe(pvvValor2, psDivisa,noEmpresa);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:funSQLComboClabe");
		}
		return list;
	}
	@DirectMethod
	public List<ContratoCreditoDto> selectBancoNacionalidad(int piBanco) {
		List<ContratoCreditoDto> list = new ArrayList<ContratoCreditoDto>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.selectBancoNacionalidad(piBanco);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:selectBancoNacionalidad");
		}
		return list;
	}
	@DirectMethod
	public List<ContratoCreditoDto> selectNoDisp(String finac) {
		List<ContratoCreditoDto> list = new ArrayList<ContratoCreditoDto>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.selectNoDisp(finac);
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:selectNoDisp");
		}
		return list;
	}
	@DirectMethod
	public Map<String, Object> updateLineaBancoCheq(String psLinea, int piBanco, String psChequera) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			ContratoCreditoDto dto = new ContratoCreditoDto();
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				dto.setIdFinanciamiento(psLinea);
				dto.setIdBanco(piBanco);
				dto.setIdClabe(psChequera);

				mapResult = altaFinanciamientoService.updateLineaBancoCheq(dto);
			//}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:updateLineaBancoCheq");
		}
		return mapResult;
	}
	@DirectMethod
	public Map<String, Object> altaDisposicion(String psFinanci, String psEmision, String psDivisa, double pdMontoDisp,
			String psFecDisp, String psFecVenDisp, double pdSobreTasa, double pdAforoPorci, double pdAforoImporte,
			String psLargoPlazo, String piSP, String piMoody, String piFitch, String psTipoTasa, String psTasaBaseDis,
			double pdValorTasa, double pdPuntos, double pdTasaPonderada, double pdTasaPostura, double pdSobreVenta,
			double pdSobreTasaCB, String psFormaPago, String psEstatus, int piDisp, int piBancoDis,
			String psChequeraDis, int piTipoFinan, String psEquivale, double pdRenta, String piDispRef, String psAnexo,
			String psComentarios, int piPeriodoRenta, double pdWhtax, double pdRentaDep, int piComision,
			double pdMontoMora, double pdIntMora, double pdValorFacturas) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			DisposicionCreditoDto dto = new DisposicionCreditoDto();
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				dto.setIdFinanciamiento(psFinanci);
				dto.setEmision(psEmision);
				dto.setIdDivisa(psDivisa);
				dto.setMontoDisposicion(pdMontoDisp);
				dto.setFecDisposicion(psFecDisp);
				dto.setFecVencimiento(psFecVenDisp);
				dto.setSobreTasa(pdSobreTasa);
				dto.setAforoPorciento(pdAforoPorci);
				dto.setAforoImporte(pdAforoImporte);
				dto.setLargoPlazo(psLargoPlazo.charAt(0));
				dto.setCalificadoraSp(piSP);
				dto.setCalificadoraMoody(piMoody);
				dto.setCalificadoraFitch(piFitch);
				dto.setTipoTasa(psTipoTasa.charAt(0));
				if (!psTasaBaseDis.equals(""))
					dto.setTasaBase(psTasaBaseDis);
				else
					dto.setTasaBase("0");
				dto.setValorTasa(pdValorTasa);
				dto.setPuntos(pdPuntos);
				dto.setTasaPonderada(pdTasaPonderada);
				dto.setMontoPostura(pdTasaPostura);
				dto.setSobreVenta(pdSobreVenta);
				dto.setSobreTasacb(pdSobreTasaCB);
				dto.setFormaPago(psFormaPago.charAt(0));
				dto.setEstatus(psEstatus.charAt(0));
				dto.setIdDisposicion(piDisp);
				dto.setIdBancoDisp(piBancoDis);
				dto.setIdChequeraDisp(psChequeraDis);
				dto.setIdTipoFinanciamiento(piTipoFinan);
				dto.setEquivalente(psEquivale);
				dto.setRenta(pdRenta);
				dto.setIdDisposicionRef(piDispRef);
				dto.setAnexo(psAnexo);
				dto.setComentarios(psComentarios);
				dto.setPeriodoRenta(piPeriodoRenta);
				dto.setWhTax(pdWhtax);
				dto.setRentaDep(pdRentaDep);
				dto.setComisionApertura(piComision);
				dto.setMontoMoratorio(pdMontoMora);
				dto.setTasaMoratoria(pdIntMora);
				dto.setValorFacturas(pdValorFacturas);
				mapResult = altaFinanciamientoService.altaDisposicion(dto);
		//	}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:altaDisposicion");
		}
		return mapResult;
	}
	@DirectMethod
	public Map<String, Object> updateAmortizacionReestructurada(String psLinea, int piDisposicion) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			DisposicionCreditoDto dto = new DisposicionCreditoDto();
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				dto.setIdFinanciamiento(psLinea);
				dto.setIdDisposicion(piDisposicion);
				mapResult = altaFinanciamientoService.updateAmortizacionReestructurada(psLinea, piDisposicion);
		//	}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:updateAmortizacionReestructurada");
		}
		return mapResult;
	}
	@DirectMethod
	public List<ContratoCreditoDto> selectDivision(int piBanco, String psChequera) {
		List<ContratoCreditoDto> list = new ArrayList<ContratoCreditoDto>();
		try {

			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.selectDivision(piBanco, psChequera);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:selectDivision");
		}
		return list;
	}
	@DirectMethod
	public int obtieneFolio(String tipoFolio) {
		int folio = 0;
		try {
			//if (Utilerias.haveSession(WebContextManager.get())
			//		&& Utilerias.tienePermiso(WebContextManager.get(), 132)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				folio = altaFinanciamientoService.obtieneFolioReal(tipoFolio);
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:obtieneFolio");
		}
		return folio;
	}
	@DirectMethod
	public Map<String, Object> inserta1(String pvvFolio, int pvvTipoDocto, int pvvFormaPago, int pvvTipoOperacion,
			int pvvNoCuenta, String pvvNoCliente, String pvvFecValor, String pvvFecOriginal, double pvvImporte,
			double pvvImporteOriginal, String pvvIdCaja, String pvvIdDivisa, String pvvIdDivisaOriginal,
			String pvvOrigenReg, String pvvReferencia, String pvvConcepto, int pvvAplica, String pvvIdEstatusMov,
			String pvvBSalvoBC, String pvvIdEstatusReg, String pvvIdBanco, String pvvIdChequera, String pvvFolioBanco,
			String pvvOrigenMov, String pvvObservacion, int pvvIdInvCBolsa, int pvvNoFolioMov, int pvvFolioRef,
			int pvvGrupo, double pvvImporteDesglosado, int pvvLote, char pvvValor1, char pvvValor2, String sHoraRecibo,
			String plIdRubro, String psIdDivision, int psNoRecibo, String psGrupo, String pvvNoDocto,
			String pvvBeneficiario, String psCliente, int piBancoBenef, String psChequeraBenef, String psFecPropuesta,
			String psLinea,int noEmpresa) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			Parametro dto = new Parametro();
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				dto.setNoFolioParam(pvvFolio);
				dto.setIdTipoDocto(pvvTipoDocto);
				dto.setIdFormaPago(pvvFormaPago);
				dto.setIdTipoOperacion(pvvTipoOperacion);
				dto.setNoCuenta(pvvNoCuenta);
				dto.setNoCliente(pvvNoCliente);
				dto.setFecValor(pvvFecValor);
				dto.setFecValorOriginal(pvvFecOriginal);
				dto.setImporte(pvvImporte);
				dto.setImporteOriginal(pvvImporteOriginal);
				dto.setIdCaja(pvvIdCaja);
				dto.setIdDivisa(pvvIdDivisa);
				dto.setIdDivisaOriginal(pvvIdDivisaOriginal);
				dto.setOrigenReg(pvvOrigenReg);
				dto.setReferencia(pvvReferencia);
				dto.setConcepto(pvvConcepto);
				dto.setAplica(pvvAplica);
				dto.setIdEstatusMov(pvvIdEstatusMov);
				dto.setbSalvoBuenCobro(pvvBSalvoBC);
				dto.setIdEstatusReg(pvvIdEstatusReg);
				dto.setbSalvoBuenCobro(pvvBSalvoBC);
				dto.setIdEstatusReg(pvvIdEstatusReg);
				dto.setIdBanco(pvvIdBanco);
				dto.setIdChequera(pvvIdChequera);
				dto.setFolioBanco(pvvFolioBanco);
				dto.setOrigenMov(pvvOrigenMov);
				dto.setObservacion(pvvObservacion);
				dto.setIdInvCbolsa(pvvIdInvCBolsa);
				dto.setNoFolioMov(pvvNoFolioMov);
				dto.setFolioRef(pvvFolioRef);
				dto.setGrupo(pvvGrupo);
				dto.setImporteDesglosado(pvvImporteDesglosado);
				dto.setLote(pvvLote);
				// char pvvValor1, char pvvValor2,
				dto.setHoraRecibo(sHoraRecibo);
				dto.setIdRubro(plIdRubro);
				dto.setDivision(psIdDivision);
				dto.setNoRecibo(psNoRecibo);
				dto.setIdGrupo(psGrupo);
				dto.setNoDocto(pvvNoDocto);
				dto.setBeneficiario(pvvBeneficiario);
				dto.setIdCliente(psCliente);
				dto.setIdBancoBenef(piBancoBenef);
				dto.setIdChequeraBenef(psChequeraBenef);
				dto.setFecPropuesta(psFecPropuesta);
				// String psLinea
				mapResult = altaFinanciamientoService.altaParametro(dto,noEmpresa);
		//	}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:inserta1");
		}
		return mapResult;
	}
	@DirectMethod
	public List<DisposicionCreditoDto> selectDisp(String psIdContrato, int psIdDisp) {
		List<DisposicionCreditoDto> list = new ArrayList<DisposicionCreditoDto>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.selectDisp(psIdContrato, psIdDisp);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:selectDisp");
		}
		return list;
	}
	@DirectMethod
	public List<DisposicionCreditoDto> buscaComisiones(String psLinea, int piDisp) {
		List<DisposicionCreditoDto> list = new ArrayList<DisposicionCreditoDto>();
		try {

			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.selectDisp(psLinea, piDisp);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:buscaComisiones");
		}
		return list;
	}
	@DirectMethod
	public List<AmortizacionCreditoDto> selectAmortizaciones(String psIdContrato, int piDisposicion,
			boolean pbCambioTasa, String psTipoMenu, String psProyecto, int piCapital) {
		List<AmortizacionCreditoDto> list = new ArrayList<AmortizacionCreditoDto>();
		try {

			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.selectAmortizaciones(psIdContrato, piDisposicion, pbCambioTasa,
						psTipoMenu, psProyecto, piCapital);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:selectAmortizaciones");
		}
		return list;
	}
	@DirectMethod
	public Map<String, Object> updateDisposicionCompleta(String psFinanci, int piDisposi, String piSP, String piMoody,
			String piFitch, String psEmision, String psDivisa, double pdMontoDisp, String psFecDisp,
			String psFecVenDisp, double pdSobreTasa, double pdAforoPorci, double pdAforoImporte, String psLargoPlazo,
			String psFormaPago, String psTipoTasa, String psTasaBaseDis, double pdValorTasa, double pdPuntos,
			double pdTasaPonderada, double pdTasaPostura, double pdSobreVenta, double pdSobreTasaCB, int piBancoDis,
			String psChequeraDis, int piTipoFinan, String psEquivale, double pdRenta, String psAnexo,
			String psComentarios, int piPeriodoRenta, double pdWhtax, double pdRentaDep, int piComision,
			double pdMontoMora, double pdIntMora, double pdValorFacturas) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			DisposicionCreditoDto dto = new DisposicionCreditoDto();
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				dto.setIdFinanciamiento(psFinanci);
				dto.setIdDisposicion(piDisposi);
				dto.setCalificadoraSp(piSP);
				dto.setCalificadoraMoody(piMoody);
				dto.setCalificadoraFitch(piFitch);
				dto.setEmision(psEmision);
				dto.setIdDivisa(psDivisa);
				dto.setMontoDisposicion(pdMontoDisp);
				dto.setFecDisposicion(psFecDisp);
				dto.setFecVencimiento(psFecVenDisp);
				dto.setSobreTasa(pdSobreTasa);
				dto.setAforoPorciento(pdAforoPorci);
				dto.setAforoImporte(pdAforoImporte);
				dto.setLargoPlazo(psLargoPlazo.charAt(0));
				dto.setFormaPago(psFormaPago.charAt(0));
				dto.setTipoTasa(psTipoTasa.charAt(0));
				if (!psTasaBaseDis.equals(""))
					dto.setTasaBase(psTasaBaseDis);
				else
					dto.setTasaBase("0");
				dto.setValorTasa(pdValorTasa);
				dto.setPuntos(pdPuntos);
				dto.setTasaPonderada(pdTasaPonderada);
				dto.setMontoPostura(pdTasaPostura);
				dto.setSobreVenta(pdSobreVenta);
				dto.setSobreTasacb(pdSobreTasaCB);
				dto.setIdBancoDisp(piBancoDis);
				dto.setIdChequeraDisp(psChequeraDis);
				dto.setIdTipoFinanciamiento(piTipoFinan);
				dto.setEquivalente(psEquivale);
				dto.setRenta(pdRenta);
				dto.setAnexo(psAnexo);
				dto.setComentarios(psComentarios);
				dto.setPeriodoRenta(piPeriodoRenta);
				dto.setWhTax(pdWhtax);
				dto.setRentaDep(pdRentaDep);
				dto.setComisionApertura(piComision);
				dto.setMontoMoratorio(pdMontoMora);
				dto.setTasaMoratoria(pdIntMora);
				dto.setValorFacturas(pdValorFacturas);
				mapResult = altaFinanciamientoService.updateDisposicion(dto);
		//	}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:updateDisposicionCompleta");
		}
		return mapResult;
	}
	@DirectMethod
	public List<AmortizacionCreditoDto> selectExisteAmort(String psLinea, String psCredito) {
		List<AmortizacionCreditoDto> list = new ArrayList<AmortizacionCreditoDto>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.selectExisteAmort(psLinea, psCredito);
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:selectExisteAmort");
		}
		return list;
	}
	@DirectMethod
	public Map<String, Object> deleteAmortizacion(String psFinanciamiento, int piDisp, boolean piInteres,
			boolean pbDisposicion) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				mapResult = altaFinanciamientoService.deleteAmortizacion(psFinanciamiento, piDisp, piInteres,
						pbDisposicion);
			//}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:deleteAmortizacion");
		}
		return mapResult;
	}
	@DirectMethod
	public Map<String, Object> deleteAGAsigLin(String psFinanciamiento, int piDisp) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				mapResult = altaFinanciamientoService.deleteAGAsigLin(psFinanciamiento, piDisp);
			//}

		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:deleteAGAsigLin");
		}
		return mapResult;
	}
	@DirectMethod
	public Map<String, Object> deleteFactFacturas(String psFinanciamiento, int piDisp) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				mapResult = altaFinanciamientoService.deleteFactFacturas(psFinanciamiento, piDisp);
		//	}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:deleteFactFacturas");
		}
		return mapResult;
	}
	@DirectMethod
	public Map<String, Object> cancelaMovimiento(String psFinanciamiento, int piDisp) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {

			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				mapResult = altaFinanciamientoService.cancelaMovimiento(psFinanciamiento, piDisp);
		//	}

		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:cancelaMovimiento");
		}
		return mapResult;
	}
	@DirectMethod
	public List<LlenaComboGralDto> funSQLComboPeriodo(boolean pdAmort) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.funSQLComboPeriodo(pdAmort);
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:funSQLComboPeriodo");
		}
		return list;
	}
	@DirectMethod
	public Map<String, Object> obtenDiaHabil(String fechas, String psRecorre) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			List<ContratoCreditoDto> fechaHabil = new ArrayList<ContratoCreditoDto>();
			List<String> fechasHabiles = new ArrayList<String>();

			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				String[] aFechas = fechas.split(" ");
				String pdFecha, diaHabil = "";
				int plDiaInhabil, plWeekend = 0;
				for (int i = 0; i <= aFechas.length - 1; i++) {
					plWeekend = 0;
					pdFecha = aFechas[i];
					DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(df.parse(pdFecha));
					if (!psRecorre.equals("E")) {
						plDiaInhabil = 0;
						do {
							fechaHabil = altaFinanciamientoService.selectInhabil(aFechas[i]);
							if (fechaHabil.isEmpty()) {
								int diaSemana = calendar.get(Calendar.DAY_OF_WEEK);
								if (diaSemana == 7) {
									if (psRecorre == "A")
										plWeekend = -1;
									else
										plWeekend = 2;
								}
								if (diaSemana == 1) {
									if (psRecorre == "A")
										plWeekend = -2;
									else
										plWeekend = 1;
								} else {
									diaHabil = pdFecha;
								}
							} else {
								if (psRecorre == "A")
									plWeekend = -1;
								else
									plWeekend = 1;
							}
						} while (plDiaInhabil == 1);
					} else {
						diaHabil = pdFecha;
					}
					if (plWeekend != 0) {
						calendar.add(Calendar.DAY_OF_YEAR, plWeekend);
						if ((calendar.get(Calendar.MONTH) + 1) < 10) {
							if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
								diaHabil = "0" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + 0
										+ (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);
							} else {
								diaHabil = calendar.get(Calendar.DAY_OF_MONTH) + "/" + 0
										+ (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);
							}
						} else {
							if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
								diaHabil = "0" + calendar.get(Calendar.DAY_OF_MONTH) + "/"
										+ (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);
							} else {
								diaHabil = calendar.get(Calendar.DAY_OF_MONTH) + "/"
										+ (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);
							}
						}
					}
					fechasHabiles.add(diaHabil);
				}
				mapResult.put("fechasHabiles", fechasHabiles);
		//	}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:obtenDiaHabil");
		}
		return mapResult;
	}
	@DirectMethod
	public Map<String, Object> funSQLDeleteProvisiones(String psFinanciamiento, int piDisp, boolean pbEstatus) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				mapResult = altaFinanciamientoService.funSQLDeleteProvisiones(psFinanciamiento, piDisp, pbEstatus);
		//	}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:funSQLDeleteProvisiones");
		}
		return mapResult;
	}
	@DirectMethod
	public  Map<String, Object> provisiones(String contrato, int disposicion, int empresa) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			altaFinanciamientoService = (AltaFinanciamientoService) contexto
					.obtenerBean("altaFinanciamientoBusinessImpl");
			mapResult = altaFinanciamientoService.provision(contrato,disposicion,empresa);
			//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:FinanciamientoModificacionCAction, M:modificar");
		}
		return mapResult;
	}
	@DirectMethod
	public int insertaProvision(int lFolio, String IdFinanciamiento, int iIdDisposicion, int noEmpresa) {
		System.out.println("eNTRAAAA");
		int resultado = 0, lAfectados = 0;
		int iIdBancoDispo, lFolioDet;
		String sIdDivisa;
		double r = 0;
		Date vsFechaIni, dFecVenInt, vsFechaFin=null;
		double dTotInteres=0;
		double dTasaVigente=0;
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Calendar calendar = Calendar.getInstance(), calendar2 = Calendar.getInstance(),
				calendar3 = Calendar.getInstance();
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		List<DisposicionCreditoDto> list = new ArrayList<DisposicionCreditoDto>();
		List<AmortizacionCreditoDto> list2 = new ArrayList<AmortizacionCreditoDto>();
		try {
			//if (Utilerias.haveSession(WebContextManager.get())
			//		&& Utilerias.tienePermiso(WebContextManager.get(), 132)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.selectDisp(IdFinanciamiento, iIdDisposicion);
				System.out.println("tama lista 1"+list.size());
				if (!list.isEmpty()) {
					iIdBancoDispo = list.get(0).getIdBancoDisp();
					sIdDivisa = list.get(0).getIdDivisa();
				} else {
					return 0;
				}
				list2 = altaFinanciamientoService.funSQLSelectAmortizacionesIntProv(IdFinanciamiento, iIdDisposicion);
				System.out.println("tama lista 2:"+list2.size());
				int i = 0;
				while (i < list2.size()){
					vsFechaIni =funciones.ponerFechaDate(funciones.cambiarFecha(list2.get(i).getFecInicio()));
					dFecVenInt = funciones.ponerFechaDate(funciones.cambiarFecha(list2.get(i).getFecVencimiento()));
					calendar.setTime(vsFechaIni);
					System.out.println("vsFechaIni " +vsFechaIni);
					int mes = calendar.get(Calendar.MONTH);
					System.out.println("vsFechaIni mes" +mes);
					int anio = calendar.get(Calendar.YEAR);
					String fecha = "01/" + (mes + 1) + "/" + anio;
					System.out.println("final fecha" +fecha);
					calendar.setTime(df.parse(fecha));
					calendar.add(Calendar.MONTH,1);
					System.out.println("sumandole el mes" +calendar.getTime());
					calendar.add(Calendar.DAY_OF_YEAR, -1);
					vsFechaFin = calendar.getTime();
					System.out.println(vsFechaFin+" ya despues de todo");
					while (vsFechaFin.compareTo(dFecVenInt) < 0) {
						System.out.println("fin "+vsFechaFin+" venc"+dFecVenInt);
						//Último cambio CIE
						if(list2.get(i).getTasaFija()==0)
					                dTasaVigente =list2.get(i).getTasaVigente();
						else
					                dTasaVigente = list2.get(i).getTasaFija();
						r = dTasaVigente / 100 / 360 * (list2.get(i).getSaldoInsoluto())
								* ((vsFechaIni.getTime() - vsFechaFin.getTime()) / 86400000);
						dTotInteres = Math.rint(r * 100) / 100;
						lFolioDet = altaFinanciamientoService.existeIntProv(IdFinanciamiento, iIdDisposicion, 0,
								vsFechaFin, noEmpresa);
						//System.out.println(lFolioDet+"lFolioDet");
						if (lFolioDet == 0) {
							System.out.println("entra");
							lFolioDet = altaFinanciamientoService.obtieneFolioReal("no_folio_det");
							ProvisionCreditoDTO provision = new ProvisionCreditoDTO();
							provision.setIdFinanciamiento(list2.get(i).getIdContrato());
							provision.setIdDisposicion(iIdDisposicion);
							provision.setNoEmpresa(noEmpresa);
							provision.setConsecutivo(0);
							provision.setIdBanco(iIdBancoDispo);
							provision.setFecIniProv(vsFechaIni);
							provision.setFecFinProv(vsFechaFin);
							provision.setiDivisa(sIdDivisa);
							provision.setMontoSaldo(list2.get(i).getSaldoInsoluto());
							provision.setMontoProvision(dTotInteres);
							provision.setTasa(dTasaVigente);
							provision.setPuntos(list2.get(i).getPuntos());//le falta la sobre tasa: puntos del dto
							provision.setNoFolioAmort(lFolio);
							provision.setNoFolioDet(lFolioDet);
							System.out.println("abajo");
							resultado = altaFinanciamientoService.insertProvisionInteres(provision);
							if (resultado <= 0)
								return 0;
						} else {
							ProvisionCreditoDTO provision = new ProvisionCreditoDTO();
							provision.setIdFinanciamiento(list2.get(i).getIdContrato());
							provision.setIdDisposicion(iIdDisposicion);
							provision.setMontoSaldo(list2.get(i).getSaldoInsoluto());
							provision.setMontoProvision(dTotInteres);
							provision.setTasa(dTasaVigente);
							provision.setNoFolioDet(lFolioDet);
							provision.setFecIniProv(vsFechaIni);
							lAfectados = altaFinanciamientoService.updateProvision(provision);
							if (lAfectados <= 0)
								return 0;
						}
						calendar2.setTime(vsFechaFin);
						calendar2.add(Calendar.DAY_OF_YEAR, 1);
						vsFechaIni = calendar2.getTime();
						calendar3.setTime(vsFechaIni);
						mes = calendar3.get(Calendar.MONTH);
						anio = calendar3.get(Calendar.YEAR);
						fecha = "01/" + (mes + 1) + "/" + anio;
						calendar3.setTime(df.parse(fecha));
						calendar3.add(Calendar.MONTH,1);
						calendar3.add(Calendar.DAY_OF_YEAR, -1);
						vsFechaFin = calendar3.getTime();
					}
					i++;
					System.out.println("i="+i);
				} 
			
				altaFinanciamientoService.deleteProvisionesFuturas(IdFinanciamiento, iIdDisposicion, vsFechaFin);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:insertaProvision");
		}
		return resultado;
	}
	@DirectMethod
	public Map<String, Object> insertBitacora(String psFinanciamiento, int piDisposicion, String psNota,
			String psFinanciamientoHijo,int noEmpresa) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				mapResult = altaFinanciamientoService.insertBitacora(psFinanciamiento, piDisposicion, psNota,
						psFinanciamientoHijo,noEmpresa);
			//}

		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:insertBitacora");
		}
		return mapResult;
	}

	@DirectMethod
	public List<BitacoraCreditoBanDto> selectBitacora(String vsContrato, int viDisp, int noEmpresa) {
		List<BitacoraCreditoBanDto> list = new ArrayList<BitacoraCreditoBanDto>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.selectBitacora(vsContrato, viDisp,noEmpresa);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:selectBitacora");
		}
		return list;
	}
	@DirectMethod
	public List<ObligacionCreditoDto> obtenerObligaciones(String psFinanciamiento, int noEmpresa) {
		List<ObligacionCreditoDto> list = new ArrayList<ObligacionCreditoDto>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.obtenerObligaciones(psFinanciamiento,noEmpresa);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:obtenerObligaciones");
		}
		return list;
	}
	@DirectMethod
	public int insertObligacion(String psFinanciamiento, int piClave, String descripcion,int noEmpresa) {
		int resultado = 0;
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				resultado = altaFinanciamientoService.insertObligacion(psFinanciamiento, piClave, descripcion,noEmpresa);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:insertObligacion");
		}
		return resultado;
	}
	@DirectMethod
	public int deleteObligacion(String obligaciones, String psFinanciamiento, int noEmpresa) {
		int resultado = 0;
	//	if (!Utilerias.haveSession(WebContextManager.get()))
		//	return 0;
		Gson gson = new Gson();
		List<Map<String, String>> paramObligaciones = gson.fromJson(obligaciones,
				new TypeToken<ArrayList<Map<String, String>>>() {
				}.getType());
		List<ObligacionCreditoDto> listObligaciones = new ArrayList<ObligacionCreditoDto>();
		try {
		//	if (Utilerias.haveSession(WebContextManager.get())) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				for (int i = 0; i < paramObligaciones.size(); i++) {
					ObligacionCreditoDto obligacionCreditoDto = new ObligacionCreditoDto();
					obligacionCreditoDto
							.setIdClave(funciones.convertirCadenaInteger(paramObligaciones.get(i).get("idClave")));
					obligacionCreditoDto.setIdFinanciamiento(psFinanciamiento);
					listObligaciones.add(obligacionCreditoDto);
				}
				resultado = altaFinanciamientoService.deleteObligacion(listObligaciones,noEmpresa);
			//}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:deleteObligacion");
		}
		return resultado;
	}
	@DirectMethod
	public List<ObligacionCreditoDto> obtenerObligacionesTotal(String psFinanciamiento,int noEmpresa) {
		List<ObligacionCreditoDto> list = new ArrayList<ObligacionCreditoDto>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.obtenerObligacionesTotal(psFinanciamiento,noEmpresa);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:obtenerObligacionesTotal");
		}
		return list;
	}
	public List<Map<String, Object>> obtenerReporteContratos(String idFinanciamiento, ServletContext context) {
		List<Map<String, Object>> lista = null;
		if (contexto == null) {
			bitacora.insertarRegistro(
					"P:BarridosFondeos, C:BarridosFondeosRepAction, M:obtenerReporteFiliales" + " contexto nulo");
			return null;
		}
		try {
			AltaFinanciamientoService altaFinanciamientoService = (AltaFinanciamientoService) contexto
					.obtenerBean("altaFinanciamientoBusinessImpl", context);
			lista = altaFinanciamientoService.obtenerReporteContratos(idFinanciamiento);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:obtenerReporteContratos");
		}
		return lista;
	}

	public int obtenerDifMeses(String idFinanciamiento, String idDisp, ServletContext context) {
		int folio = 0;
		if (contexto == null) {
			bitacora.insertarRegistro(
					"P:BarridosFondeos, C:BarridosFondeosRepAction, M:obtenerReporteFiliales" + " contexto nulo");
			return 0;
		}
		try {
			//if (Utilerias.haveSession(WebContextManager.get())
			//		&& Utilerias.tienePermiso(WebContextManager.get(), 132)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				folio = altaFinanciamientoService.obtenerDifMeses(idFinanciamiento, idDisp);
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:obtenerDifMeses");
		}
		return folio;
	}
	@DirectMethod
	public List<LlenaComboGralDto> selectComboEmpresaAval(int noEmpresa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.selectComboEmpresaAval(noEmpresa);
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:selectComboEmpresaAval");
		}
		return list;
	}
	@DirectMethod
	public List<AvalGarantiaDto> selectMontoDispuestoAvalada(String idFinanciamiento, int idDisp,int noEmpresa) {
		List<AvalGarantiaDto> list = new ArrayList<AvalGarantiaDto>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.obtenerMontoDispuestoAvalada(idFinanciamiento, idDisp, noEmpresa);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:selectMontoDispuestoAvalada");
		}
		return list;
	}
	@DirectMethod
	public List<LlenaComboGralDto> selectComboAvalGtia(String piEmpresaAvalista, String psDivisa, int empresa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.selectComboAvalGtia(piEmpresaAvalista, psDivisa, empresa);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:selectComboAvalGtia");
		}
		return list;
	}
	@DirectMethod
	public int selectAvaladaGtia(int piEmpresa, String psClave, int noEmpresa) {
		int resultado = 0;
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				resultado = altaFinanciamientoService.selectAvaladaGtia(piEmpresa, psClave, noEmpresa);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:selectAvaladaGtia");
		}
		return resultado;
	}
	@DirectMethod
	public int insertAvalGtiaLin(String psFinanciamiento, int piDisposicion, double pdMontoDispuesto,
			String piEmpresaAvalista, String psClave, int noEmpresa) {
		int resultado = 0;
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				resultado = altaFinanciamientoService.insertAvalGtiaLin(psFinanciamiento, piDisposicion,
						pdMontoDispuesto, piEmpresaAvalista, psClave, noEmpresa);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:insertAvalGtiaLin");
		}
		return resultado;
	}
	@DirectMethod
	public int existeAvalGtiaLinea(String avGarant, int noEmpresa) {
		int resultado = 0;
	//	if (!Utilerias.haveSession(WebContextManager.get()))
		//	return 0;
		Gson gson = new Gson();
		List<Map<String, String>> paramAG = gson.fromJson(avGarant, new TypeToken<ArrayList<Map<String, String>>>() {
		}.getType());

		List<AvalGarantiaDto> listAG = new ArrayList<AvalGarantiaDto>();
		try {
		//	if (Utilerias.haveSession(WebContextManager.get())) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				for (int i = 0; i < paramAG.size(); i++) {
					AvalGarantiaDto avalGtiaDto = new AvalGarantiaDto();
					avalGtiaDto.setClave(paramAG.get(i).get("idClave"));
					avalGtiaDto.setNoEmpresaAvalistaAux(paramAG.get(i).get("noEmpresaAvalistaAux"));
					listAG.add(avalGtiaDto);
				}
				resultado = altaFinanciamientoService.existeAvalGtiaLinea(listAG, noEmpresa);
			//}
		} catch (Exception e) {

			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:existeAvalGtiaLinea");
		}
		return resultado;
	}

	@DirectMethod
	public int deleteLineaAvalada(String avGarant, String linea, int credito, int noEmpresa) {
		int resultado = 0;
		if (!Utilerias.haveSession(WebContextManager.get()))
			return 0;
		Gson gson = new Gson();
		List<Map<String, String>> paramAG = gson.fromJson(avGarant, new TypeToken<ArrayList<Map<String, String>>>() {
		}.getType());
		List<AvalGarantiaDto> listAG = new ArrayList<AvalGarantiaDto>();
		try {
		//	if (Utilerias.haveSession(WebContextManager.get())) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				for (int i = 0; i < paramAG.size(); i++) {
					AvalGarantiaDto avalGtiaDto = new AvalGarantiaDto();
					avalGtiaDto.setClave(paramAG.get(i).get("idClave"));
					avalGtiaDto.setNoEmpresaAvalistaAux(paramAG.get(i).get("noEmpresaAvalistaAux"));
					listAG.add(avalGtiaDto);
				}
				resultado = altaFinanciamientoService.deleteLineaAvalada(listAG, linea, credito, noEmpresa);
		//	}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:deleteLineaAvalada");
		}
		return resultado;
	}

	@DirectMethod
	public Map<String, Object> altaAmortizacion(String capital, String interes, String psTasa, String vsBisiesto,
			String txtLinea, int cmbDisp, String txtTasaVigente, String txtTasaFija, String cmbPeriodo, int txtNoAmort,
			String pvTasaBase, String txtBase, String txtPuntos, String txtIva, int cmbDiaCorte, int txtDias,
			String txtComentario, boolean chkDiasExacInt, int txtNoAmortInt, String cmbPeriodoInt, int txtSobreTasaCB,
			boolean chkSobreTasa, String cmbDiaCorteInt, int txtDiasInt, int noEmpresa) {
		if (cmbDiaCorteInt.equals(""))
			cmbDiaCorteInt = "0";
		int lFolio, viSobreTasaBC = 0, resultado = 0;
		double vdTasaVar = 0, vdTasaFij = 0;
		boolean insertaIntereses;
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			System.out.println("Entra");
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				lFolio = altaFinanciamientoService.obtieneFolioReal("no_folio_amort");
			//	if (!Utilerias.haveSession(WebContextManager.get()))
			//		return null;
			//	if (Utilerias.haveSession(WebContextManager.get())) {
					altaFinanciamientoService = (AltaFinanciamientoService) contexto
							.obtenerBean("altaFinanciamientoBusinessImpl");
					Gson gson = new Gson();
					List<Map<String, String>> paramInteres = gson.fromJson(interes,
							new TypeToken<ArrayList<Map<String, String>>>() {
							}.getType());
					List<Map<String, String>> paramCapital = gson.fromJson(capital,
							new TypeToken<ArrayList<Map<String, String>>>() {
							}.getType());
					if (!paramInteres.isEmpty() && !paramCapital.isEmpty()) {
						System.out.println("XARENI");
						insertaIntereses = true;
						mapResult = altaFinanciamientoService.insertAmortCapitales(capital, psTasa.charAt(0),
								Double.parseDouble(txtTasaVigente), Double.parseDouble(txtTasaFija), vsBisiesto, lFolio,
								cmbPeriodo, txtNoAmort, cmbDisp, txtLinea, pvTasaBase, Double.parseDouble(txtBase),
								Double.parseDouble(txtPuntos), Double.parseDouble(txtIva), cmbDiaCorte, txtDias,
								txtComentario, cmbDiaCorteInt, insertaIntereses);
						for (Entry<String, Object> e : mapResult.entrySet()) {
							if (e.getKey() == "msgUsuario" && !e.getValue().equals("")) {
								break;
							}
						}
						mapResult = altaFinanciamientoService.insertAmortInteres(interes, vsBisiesto, lFolio, psTasa,
								vdTasaFij, vdTasaVar, viSobreTasaBC, cmbPeriodoInt, txtNoAmortInt, txtLinea, cmbDisp,
								chkSobreTasa, pvTasaBase, Double.parseDouble(txtBase), Double.parseDouble(txtPuntos),
								Double.parseDouble(txtIva), cmbDiaCorte, txtDias, txtComentario, cmbDiaCorteInt);
						for (Entry<String, Object> e : mapResult.entrySet()) {
							if (e.getKey() == "msgUsuario" && !e.getValue().equals("")) {
								break;
							}
						}
						if (!chkSobreTasa && Integer.parseInt(cmbDiaCorteInt) < 31 || txtDiasInt != 30) {
							resultado = insertaProvision(lFolio, txtLinea, cmbDisp, noEmpresa);
						}
					} else if (paramInteres.isEmpty() && !paramCapital.isEmpty()) {
						insertaIntereses = false;
						mapResult = altaFinanciamientoService.insertAmortCapitales(capital, psTasa.charAt(0),
								Double.parseDouble(txtTasaVigente), Double.parseDouble(txtTasaFija), vsBisiesto, lFolio,
								cmbPeriodo, txtNoAmort, cmbDisp, txtLinea, pvTasaBase, Double.parseDouble(txtBase),
								Double.parseDouble(txtPuntos), Double.parseDouble(txtIva), cmbDiaCorte, txtDias,
								txtComentario, cmbDiaCorteInt, insertaIntereses);
					} else if (!paramInteres.isEmpty() && paramCapital.isEmpty()) {
						mapResult = altaFinanciamientoService.insertAmortInteres(interes, vsBisiesto, lFolio, psTasa,
								vdTasaFij, vdTasaVar, viSobreTasaBC, cmbPeriodoInt, txtNoAmortInt, txtLinea, cmbDisp,
								chkSobreTasa, pvTasaBase, Double.parseDouble(txtBase), Double.parseDouble(txtPuntos),
								Double.parseDouble(txtIva), cmbDiaCorte, txtDias, txtComentario, cmbDiaCorteInt);
						for (Entry<String, Object> e : mapResult.entrySet()) {
							if (e.getKey() == "msgUsuario" && !e.getValue().equals("")) {
								break;
							}
						}
						if (!chkSobreTasa && Integer.parseInt(cmbDiaCorteInt) < 31 || txtDiasInt != 30) {
							resultado = insertaProvision(lFolio, txtLinea, cmbDisp, noEmpresa);
						}
					}
			//	}
			//}
		} catch (Exception e) {
			mapResult.put("msgUsuario", "Error al guardar el plan de amortizaciones.");
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:altaAmortizacion");
		}
		return mapResult;
	}

	@DirectMethod
	public int selectExisteAmortizacion(String contrato, int disposicion, int noAmortizacion) {
		int resultado = 0;
		try {
		//	if (Utilerias.haveSession(WebContextManager.get())) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				resultado = altaFinanciamientoService.selectExisteAmortizacion(contrato, disposicion, noAmortizacion);
		//	}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:selectExisteAmortizacion");
		}
		return resultado;
	}

	@DirectMethod
	public HSSFWorkbook reporteAmortizaciones(String contrato, String disposicion, String montoAutorizado, String banco,
			String montoDisposicion, String divisa, String fechaInicio, String fechaFin, String tasa,
			ServletContext context) {
		HSSFWorkbook wb = null;
		try {
			altaFinanciamientoService = (AltaFinanciamientoService) contexto
					.obtenerBean("altaFinanciamientoBusinessImpl", context);
			wb = altaFinanciamientoService.reporteAmortizaciones(contrato, disposicion, montoAutorizado, banco,
					montoDisposicion, divisa, fechaInicio, fechaFin, tasa);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:reporteAmortizaciones");
		}
		return wb;
	}

	@DirectMethod
	public List<AmortizacionCreditoDto> selectAmortizacionesCapital(String psIdContrato, int piDisposicion,
			boolean pbCambioTasa, String psTipoMenu, String psProyecto, int piCapital) {
		List<AmortizacionCreditoDto> list = new ArrayList<AmortizacionCreditoDto>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.selectAmortizacionesCapital(psIdContrato, piDisposicion, pbCambioTasa,
						psTipoMenu, psProyecto, piCapital);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:selectAmortizacionesCapital");
		}
		return list;
	}

	@DirectMethod
	public List<AmortizacionCreditoDto> selectAmortizacionesInteres(String psIdContrato, int piDisposicion,
			boolean pbCambioTasa, String psTipoMenu, String psProyecto, int piCapital, int dias) {
		List<AmortizacionCreditoDto> list = new ArrayList<AmortizacionCreditoDto>();
		try {

			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				altaFinanciamientoService = (AltaFinanciamientoService) contexto
						.obtenerBean("altaFinanciamientoBusinessImpl");
				list = altaFinanciamientoService.selectAmortizacionesInteres(psIdContrato, piDisposicion, pbCambioTasa,
						psTipoMenu, psProyecto, piCapital, dias);

			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:AltaFinanciamientoAction, M:selectAmortizacionesInteres");
		}
		return list;
	}
}