package com.webset.set.coinversion.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.coinversion.dto.BarridoChequerasDto;
import com.webset.set.coinversion.dto.DivisasEncontradasDto;
import com.webset.set.coinversion.dto.InteresesDto;
import com.webset.set.coinversion.dto.ParamBusquedaFondeoDto;
import com.webset.set.coinversion.dto.ParamRetornoFondeoAutDto;
import com.webset.set.coinversion.dto.ParamTraspasoCoinversionDto;
import com.webset.set.coinversion.dto.ParamsBusquedaBarridoDto;
import com.webset.set.coinversion.service.CoinversionService;
import com.webset.set.seguridad.dto.EmpresaDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.EstructuraColumnasDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.OrdenInversionDto;
import com.webset.utils.tools.Utilerias;

import net.sf.jasperreports.engine.JRDataSource;

public class CoinversionAction {
	private Bitacora bitacora = new Bitacora();
	private Funciones funciones = new Funciones();
	private Contexto contexto = new Contexto();
	private GlobalSingleton globalSingleton;
	private CoinversionService coinversionService;
	private static Logger logger = Logger.getLogger(CoinversionAction.class);

	@DirectMethod
	public List<LlenaComboGralDto> llenarComboEmpresaRaiz(boolean bExistentes) {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		List<LlenaComboGralDto> listDatos = new ArrayList<LlenaComboGralDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				CoinversionService coinversionService = (CoinversionService) contexto
						.obtenerBean("coinversionBusinessImpl");
				listDatos = coinversionService.llenarComboEmpresaRaiz(bExistentes);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:llenarComboEmpresaRaiz");
			return null;
		}
		return listDatos;
	}

	@DirectMethod
	public List<LlenaComboGralDto> llenarComboBancoConcentradora(boolean bConcentradora, int idEmpresa,
			String idDivisa) {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		List<LlenaComboGralDto> listDatos = new ArrayList<LlenaComboGralDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				CoinversionService coinversionService = (CoinversionService) contexto
						.obtenerBean("coinversionBusinessImpl");
				listDatos = coinversionService.llenarComboBancoConcentradora(bConcentradora, idEmpresa, idDivisa);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:llenarComboEmpresaRaiz");
			return null;
		}
		return listDatos;
	}

	@DirectMethod
	public List<LlenaComboGralDto> llenarComboBancosRaiz(int iIdRaiz, String sIdDivisa) {
		System.out.println(iIdRaiz + sIdDivisa);
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		List<LlenaComboGralDto> listBancos = new ArrayList<LlenaComboGralDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				CoinversionService coinversionService = (CoinversionService) contexto
						.obtenerBean("coinversionBusinessImpl");
				listBancos = coinversionService.llenarBancosRaiz(iIdRaiz, sIdDivisa);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:llenarBancosRaiz");
			return null;
		}
		return listBancos;
	}

	@DirectMethod
	public List<LlenaComboGralDto> consultarChequeraFondeo(int iIdEmpresa, String sIdDivisa, int iIdBanco) {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		List<LlenaComboGralDto> listDatos = new ArrayList<LlenaComboGralDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				CoinversionService coinversionService = (CoinversionService) contexto
						.obtenerBean("coinversionBusinessImpl");
				listDatos = coinversionService.llenarComboChequeraFondeo(iIdEmpresa, sIdDivisa, iIdBanco);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:llenarComboEmpresaRaiz");
			return null;
		}
		return listDatos;
	}

	@DirectMethod
	public List<ParamRetornoFondeoAutDto> consultarFondeoAutomatico(int idEmpresa, int idEmpresaRaiz, int idBanco,
			String idDivisa, String idChequera, boolean chkMismoBanco, String sTipoBusqueda) {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		List<ParamRetornoFondeoAutDto> listConsFondeoAut = new ArrayList<ParamRetornoFondeoAutDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				CoinversionService coinversionService = (CoinversionService) contexto
						.obtenerBean("coinversionBusinessImpl");
				ParamBusquedaFondeoDto dtoBus = new ParamBusquedaFondeoDto();
				dtoBus.setIdEmpresa(idEmpresa > 0 ? idEmpresa : 0);
				dtoBus.setIdEmpresaRaiz(idEmpresaRaiz > 0 ? idEmpresaRaiz : 0);
				dtoBus.setIdBanco(idBanco > 0 ? idBanco : 0);
				dtoBus.setIdDivisa(idDivisa != null && !idDivisa.equals("") ? idDivisa : "");
				dtoBus.setIdChequera(idChequera != null && !idChequera.equals("") ? idChequera : "");
				dtoBus.setChkMismoBanco(chkMismoBanco);
				dtoBus.setSTipoBusqueda(sTipoBusqueda != null && !sTipoBusqueda.equals("") ? sTipoBusqueda : "");
				listConsFondeoAut = coinversionService.consultarFondeoAutomatico(dtoBus);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:consultarFondeoAutomatico");
		}
		return listConsFondeoAut;
	}

	@DirectMethod
	public Map<String, Object> ejecutarFondeoAutomatico(String datosGrid, int idEmpresa, int idEmpresaRaiz, int idBanco,
			String idDivisa, String idChequera, boolean chkMismoBanco, String sTipoBusqueda,
			boolean bVisibleMontoMinFondeo, String montoMinFondeo) {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		Gson gson = new Gson();
		Map<String, Object> mapRetFondeo = new HashMap<String, Object>();
		List<ParamRetornoFondeoAutDto> listGridFondeo = new ArrayList<ParamRetornoFondeoAutDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				CoinversionService coinversionService = (CoinversionService) contexto
						.obtenerBean("coinversionBusinessImpl");
				List<Map<String, String>> objParams = gson.fromJson(datosGrid,
						new TypeToken<ArrayList<Map<String, String>>>() {
						}.getType());

				for (int i = 0; i < objParams.size(); i++) {
					ParamRetornoFondeoAutDto dto = new ParamRetornoFondeoAutDto();
					dto.setOrden(funciones.convertirCadenaInteger(objParams.get(i).get("orden")));
					dto.setPrestamos(funciones.convertirCadenaDouble(objParams.get(i).get("prestamos")));
					dto.setNoEmpresaOrigen(funciones.convertirCadenaInteger(objParams.get(i).get("noEmpresaOrigen")));
					dto.setNomEmpresaOrigen(
							funciones.validarCadena(objParams.get(i).get("nomEmpresaOrigen").toString()));
					dto.setNoEmpresaDestino(funciones.convertirCadenaInteger(objParams.get(i).get("noEmpresaDestino")));
					dto.setNomEmpresaDestino(
							funciones.validarCadena(objParams.get(i).get("nomEmpresaDestino").toString()));
					dto.setDescBanco(funciones.validarCadena(objParams.get(i).get("descBanco").toString()));
					dto.setIdBanco(funciones.convertirCadenaInteger(objParams.get(i).get("idBanco")));
					dto.setIdChequera(funciones.validarCadena(objParams.get(i).get("idChequera").toString()));
					dto.setSaldoChequera(funciones.convertirCadenaDouble(objParams.get(i).get("saldoChequera")));
					dto.setSaldoMinimoChequera(
							funciones.convertirCadenaDouble(objParams.get(i).get("saldoMinimoChequera")));
					dto.setImporteF(funciones.convertirCadenaDouble(objParams.get(i).get("importeF")));
					dto.setTipoCambio(funciones.convertirCadenaDouble(objParams.get(i).get("tipoCambio")));
					dto.setIdDivisa(funciones.validarCadena(objParams.get(i).get("idDivisa")));
					dto.setPm(funciones.convertirCadenaDouble(objParams.get(i).get("pm")));
					dto.setPmCheques(funciones.convertirCadenaDouble(objParams.get(i).get("pmCheques")));
					dto.setConcepto(funciones.validarCadena(objParams.get(i).get("concepto")));
					dto.setImporteTraspaso(funciones.convertirCadenaDouble(objParams.get(i).get("importeTraspaso")));
					dto.setSaldoCoinversion(funciones.convertirCadenaDouble(objParams.get(i).get("saldoCoinversion")));
					dto.setTipoSaldo(funciones.convertirCadenaInteger(objParams.get(i).get("idSaldo")));
					
					listGridFondeo.add(dto);
				}

				ParamBusquedaFondeoDto dtoBus = new ParamBusquedaFondeoDto();
				dtoBus.setIdEmpresa(idEmpresa > 0 ? idEmpresa : 0);
				dtoBus.setIdEmpresaRaiz(idEmpresaRaiz > 0 ? idEmpresaRaiz : 0);
				dtoBus.setIdBanco(idBanco > 0 ? idBanco : 0);
				dtoBus.setIdDivisa(idDivisa != null && !idDivisa.equals("") ? idDivisa : "");
				dtoBus.setIdChequera(idChequera != null && !idChequera.equals("") ? idChequera : "");
				dtoBus.setChkMismoBanco(chkMismoBanco);
				dtoBus.setSTipoBusqueda(sTipoBusqueda != null && !sTipoBusqueda.equals("") ? sTipoBusqueda : "");
				dtoBus.setBMontoMinFondeo(bVisibleMontoMinFondeo);
				dtoBus.setMontoMinFondeo(funciones.convertirCadenaDouble(montoMinFondeo));

				mapRetFondeo = coinversionService.realizarFondeoAutomatico(listGridFondeo, dtoBus);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:ejecutarFondeoAutomatico");
		}
		return mapRetFondeo;
	}

	/**
	 * Este metodo consulta los pagos pendientes
	 * 
	 * @param iIdEmpresa
	 * @param iIdBanco
	 * @param sIdChequera
	 * @param iIdEmpRaiz
	 * @param sTipoBusqueda
	 * @param sIdDivisa
	 * @return
	 */
	@DirectMethod
	public List<MovimientoDto> obtenerPagos(int iIdEmpresa, int iIdEmpRaiz, int iIdBanco, String sIdDivisa,
			String sIdChequera, String sTipoBusqueda) {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		List<MovimientoDto> listConsPag = new ArrayList<MovimientoDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				globalSingleton = GlobalSingleton.getInstancia();
				ParamBusquedaFondeoDto dtoBus = new ParamBusquedaFondeoDto();
				CoinversionService coinversionService = (CoinversionService) contexto
						.obtenerBean("coinversionBusinessImpl");
				dtoBus.setIdEmpresa(funciones.validarEntero(iIdEmpresa));
				dtoBus.setIdBanco(funciones.validarEntero(iIdBanco));
				dtoBus.setIdChequera(funciones.validarCadena(sIdChequera));
				dtoBus.setIdEmpresaRaiz(funciones.validarEntero(iIdEmpRaiz));
				dtoBus.setSTipoBusqueda(funciones.validarCadena(sTipoBusqueda));
				dtoBus.setIdDivisa(funciones.validarCadena(sIdDivisa));
				globalSingleton = GlobalSingleton.getInstancia();
				dtoBus.setFechaHoy(globalSingleton.getFechaHoy());
				dtoBus.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());

				listConsPag = coinversionService.obtenerPagos(dtoBus);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:obtenerPagos");
		}
		return listConsPag;
	}

	@DirectMethod
	public List<ParamRetornoFondeoAutDto> obtenerDesglosePagos(int iIdEmpresa, int iIdEmpRaiz, int iIdBanco,
			String sIdDivisa, String sIdChequera, String sTipoBusqueda, String cveControl) {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		List<ParamRetornoFondeoAutDto> listConsPag = new ArrayList<ParamRetornoFondeoAutDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				globalSingleton = GlobalSingleton.getInstancia();
				ParamBusquedaFondeoDto dtoBus = new ParamBusquedaFondeoDto();
				CoinversionService coinversionService = (CoinversionService) contexto
						.obtenerBean("coinversionBusinessImpl");
				dtoBus.setIdEmpresa(funciones.validarEntero(iIdEmpresa));
				dtoBus.setIdBanco(funciones.validarEntero(iIdBanco));
				dtoBus.setIdChequera(funciones.validarCadena(sIdChequera));
				dtoBus.setIdEmpresaRaiz(funciones.validarEntero(iIdEmpRaiz));
				dtoBus.setSTipoBusqueda(funciones.validarCadena(sTipoBusqueda));
				dtoBus.setIdDivisa(funciones.validarCadena(sIdDivisa));
				dtoBus.setCveControl(funciones.validarCadena(cveControl));
				dtoBus.setFechaHoy(globalSingleton.getFechaHoy());
				dtoBus.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());

				listConsPag = coinversionService.obtenerDesglosePagos(dtoBus);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:obtenerDesglosePagos");
		}
		return listConsPag;
	}

	/**
	 * Metodo para obtener los cheques por pagar
	 * 
	 * @param sIdChequera
	 * @param iIdBanco
	 * @param sVencimiento
	 *            este parametro determina si se quieren el fondeo de hoy('H') o
	 *            dias anteriores('A')
	 * @return
	 */
	@DirectMethod
	public List<MovimientoDto> obtenerFondeoCheques(String sIdChequera, int iIdBanco, String sVencimiento) {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		List<MovimientoDto> listFonChe = new ArrayList<MovimientoDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				CoinversionService coinversionService = (CoinversionService) contexto
						.obtenerBean("coinversionBusinessImpl");
				listFonChe = coinversionService.obtenerFondeoCheques(funciones.validarCadena(sIdChequera),
						funciones.validarEntero(iIdBanco), funciones.validarCadena(sVencimiento));
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:obtenerFondeoCheques");
		}
		return listFonChe;
	}

	/**
	 * 
	 * @param datGridVencHoy
	 * @param datGridVencAnt
	 * @param iIdBanco
	 * @param sIdChequera
	 * @return
	 */
	@DirectMethod
	public Map<String, Object> ejecutarFondeoCheques(String datGridVencHoy, String datGridVencAnt, int iIdBanco,
			String sIdChequera) {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		Map<String, Object> mapRet = new HashMap<String, Object>();
		List<MovimientoDto> listVencHoy = new ArrayList<MovimientoDto>();
		List<MovimientoDto> listVencAnt = new ArrayList<MovimientoDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				Gson gson = new Gson();
				List<Map<String, String>> objParamsVencHoy = gson.fromJson(datGridVencHoy,
						new TypeToken<ArrayList<Map<String, String>>>() {
						}.getType());
				CoinversionService coinversionService = (CoinversionService) contexto
						.obtenerBean("coinversionBusinessImpl");
				for (int i = 0; i < objParamsVencHoy.size(); i++) {
					MovimientoDto dto = new MovimientoDto();
					dto.setSeleccionado(
							funciones.convertirCadenaBoolean(objParamsVencHoy.get(i).get("seleccionado").toString()));
					dto.setNoFolioDet(
							funciones.convertirCadenaInteger(objParamsVencHoy.get(i).get("noFolioDet").toString()));
					listVencHoy.add(dto);
				}
				// Lista para Vencimientos Anteriores
				List<Map<String, String>> objParamsVencAnt = gson.fromJson(datGridVencAnt,
						new TypeToken<ArrayList<Map<String, String>>>() {
						}.getType());
				for (int c = 0; c < objParamsVencAnt.size(); c++) {
					MovimientoDto dto = new MovimientoDto();
					dto.setSeleccionado(
							funciones.convertirCadenaBoolean(objParamsVencAnt.get(c).get("seleccionado").toString()));
					dto.setNoFolioDet(
							funciones.convertirCadenaInteger(objParamsVencAnt.get(c).get("noFolioDet").toString()));
					listVencAnt.add(dto);
				}
				mapRet = coinversionService.ejecutarFondeoCheques(listVencHoy, listVencAnt, iIdBanco, sIdChequera);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:ejecutarFondeoCheques");
		}
		return mapRet;
	}

	@DirectMethod
	public String obtenerEstructura() {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		Gson gson = new Gson();
		String json = "";
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				List<EstructuraColumnasDto> listEstruc = new ArrayList<EstructuraColumnasDto>();
				for (int i = 0; i < 3; i++) {
					EstructuraColumnasDto dto = new EstructuraColumnasDto();
					dto.setName("nombre");
					dto.setName("apellido");
					dto.setName("edad");
					listEstruc.add(dto);
				}
				json = gson.toJson(listEstruc);
				logger.info("JSON: " + json);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return json;
	}

	@DirectMethod
	public List<DivisasEncontradasDto> llenarGridDivisas(int iEmpresa, String sElemento) {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		List<DivisasEncontradasDto> list = new ArrayList<DivisasEncontradasDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl");
				if (sElemento.equals("grid"))
					list = coinversionService.consultarDivisasFilialesEncontradas(iEmpresa);
				else
					list = coinversionService.consultarDivisasFilialesNoEncontradas(iEmpresa);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:llenarGridDivisas");
		}
		return list;
	}

	@DirectMethod
	public Map<String, Object> operacionesFilial(String divisas, String empresas, int concentradora, String operacion) {

		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		Gson gson = new Gson();
		List<Map<String, String>> paramEmpresa = gson.fromJson(empresas,
				new TypeToken<ArrayList<Map<String, String>>>() {
				}.getType());
		List<Map<String, String>> paramDivisa = gson.fromJson(divisas, new TypeToken<ArrayList<Map<String, String>>>() {
		}.getType());
		Map<String, Object> mapResult = new HashMap<String, Object>();
		EmpresaDto dtoEmpresas = new EmpresaDto();
		List<DivisasEncontradasDto> listDivisas = new ArrayList<DivisasEncontradasDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl");
				dtoEmpresas.setNoEmpresa(funciones.convertirCadenaInteger(paramEmpresa.get(0).get("noEmpresa")));
				dtoEmpresas.setNomEmpresa(funciones.validarCadena(paramEmpresa.get(0).get("nomEmpresa")));
				dtoEmpresas.setNoCuentaEmp(funciones.convertirCadenaInteger(paramEmpresa.get(0).get("noCuentaEmp")));
				dtoEmpresas.setIdTipoPersona(funciones.validarCadena(paramEmpresa.get(0).get("idTipoPersona")));
				dtoEmpresas.setNoCuenta(funciones.convertirCadenaInteger(paramEmpresa.get(0).get("noCuenta")));
				dtoEmpresas
						.setNoControladora(funciones.convertirCadenaInteger(paramEmpresa.get(0).get("noControladora")));
				for (int i = 0; i < paramDivisa.size(); i++) {
					DivisasEncontradasDto dto = new DivisasEncontradasDto();
					dto.setIdDivisa(funciones.validarCadena(paramDivisa.get(i).get("idDivisa")));
					dto.setDescDivisa(funciones.validarCadena(paramDivisa.get(i).get("descDivisa")));
					listDivisas.add(dto);
				}
				if (operacion.equals("alta"))
					mapResult = coinversionService.ejecutarAltaFilial(dtoEmpresas, listDivisas, concentradora);
				else if (operacion.equals("baja"))
					mapResult = coinversionService.eliminarFilial(dtoEmpresas, listDivisas, concentradora);
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:operacionesFilial");
		}
		return mapResult;
	}

	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteCoinversion(String nomReporte, Map parameters, ServletContext context) {
		JRDataSource jrDataSource = null;
		try {

			coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl", context);
			jrDataSource = coinversionService.obtenerReporteCoinversion(parameters);

		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:obtenerReporteCoinversion(String nomReporte, Map parameters, ServletContext context)");
		}
		return jrDataSource;
	}

	public JRDataSource reportePrueba(Map<?, ?> parameters, ServletContext context) {
		JRDataSource jrDataSource = null;
		try {
			coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl", context);
			jrDataSource = coinversionService.reportePrueba(parameters);

		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:obtenerReporteCoinversion(String nomReporte, Map parameters, ServletContext context)");
		}
		return jrDataSource;
	}

	// Metodo de prueba
	@DirectMethod
	public HSSFWorkbook obtenerExcel(Map<String, String> map, String ruta, ServletContext context) {
		coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl", context);
		return coinversionService.obtenerExcel(map, ruta);
	}

	@SuppressWarnings("unchecked")
	@DirectMethod
	public List<Map<String, Object>> obtenerReporteCoinversion(int iEmpresa, int iUsuario) {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		List<Map<String, Object>> mapResult = null;
		try {
			coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl");
			mapResult = coinversionService.consultarReporteCoinversion(iEmpresa, iUsuario);

		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:obtenerReporteCoinversion(int iEmpresa, int iUsuario)");
		}
		return mapResult;
	}

	// barrido automatico por saldos

	@DirectMethod
	public List<LlenaComboGralDto> llenarComboBancosConcentradora(boolean bConcentradora, int idEmpresa,
			String idDivisa) {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		List<LlenaComboGralDto> listDatos = new ArrayList<LlenaComboGralDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl");
				listDatos = coinversionService.llenarComboBancosConcentradora(bConcentradora, idEmpresa, idDivisa);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:llenarComboBancosConcentradora");
			return null;
		}
		return listDatos;
	}

	@DirectMethod
	public List<LlenaComboChequeraDto> llenarComboChequerasBarrido(boolean bPagadora, int iBanco, int iNoEmpresa) {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		List<LlenaComboChequeraDto> list = new ArrayList<LlenaComboChequeraDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl");
				list = coinversionService.llenarComboChequerasBarrido(bPagadora, iBanco, iNoEmpresa);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:llenarComboChequerasBarrido");
		}
		return list;
	}

	@DirectMethod
	public List<BarridoChequerasDto> llenarGridBarridoChequeras(int iConcentradora, String sDivisa, String sBancos) {
		List<BarridoChequerasDto> listBarrido = new ArrayList<BarridoChequerasDto>();
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl");
				listBarrido = coinversionService.consultarBarridoChequerasPorSaldo(iConcentradora, sDivisa, sBancos);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:llenarGridBarridoChequeras");
		}
		return listBarrido;
	}

	@DirectMethod
	public Map<String, Object> importarBancos(String sBancos) {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl");
				mapResult = coinversionService.importarBancos(sBancos);
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:importarBancos");
		}
		return mapResult;
	}

	@DirectMethod
	public Map<String, Object> ejecutarBarrido(String datosGrid, String criterios) {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<BarridoChequerasDto> listBarrido = new ArrayList<BarridoChequerasDto>();
		Gson gson = new Gson();
		List<Map<String, String>> paramsGrid = gson.fromJson(datosGrid,
				new TypeToken<ArrayList<Map<String, String>>>() {
				}.getType());
		List<Map<String, String>> paramsCriterio = gson.fromJson(criterios,
				new TypeToken<ArrayList<Map<String, String>>>() {
				}.getType());
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl");
				for (int i = 0; i < paramsGrid.size(); i++) {
					BarridoChequerasDto dto = new BarridoChequerasDto();
					dto.setNoEmpresa(funciones.convertirCadenaInteger(paramsGrid.get(i).get("noEmpresa")));
					dto.setNomEmpresa(funciones.validarCadena(paramsGrid.get(i).get("nomEmpresa")));
					dto.setIdDivisa(funciones.validarCadena(paramsGrid.get(i).get("idDivisa")));
					dto.setIdBanco(funciones.convertirCadenaInteger(paramsGrid.get(i).get("idBanco")));
					dto.setDescBanco(funciones.validarCadena(paramsGrid.get(i).get("descBanco")));
					dto.setIdChequera(funciones.validarCadena(paramsGrid.get(i).get("idChequera")));
					dto.setSaldoChequera(funciones.convertirCadenaDouble(paramsGrid.get(i).get("saldoChequera")));
					dto.setSaldoFinal(funciones.convertirCadenaDouble(paramsGrid.get(i).get("saldoFinal")));
					dto.setSaldoMinimo(funciones.convertirCadenaDouble(paramsGrid.get(i).get("saldoMinimo")));
					dto.setDiferencia(funciones.convertirCadenaDouble(paramsGrid.get(i).get("diferencia")));
					dto.setSecuencia(funciones.convertirCadenaInteger(paramsGrid.get(i).get("secuencia")));
					dto.setFecValor(funciones.ponerFechaDate(paramsGrid.get(i).get("fecValor")));
					dto.setSobregiro(funciones.convertirCadenaDouble(paramsGrid.get(i).get("sobregiro")));
					dto.setMontoSobregiro(funciones.convertirCadenaDouble(paramsGrid.get(i).get("montoSobregiro")));
					dto.setNoLinea(funciones.convertirCadenaInteger(paramsGrid.get(i).get("noLinea")));
					dto.setSaldoCredito(funciones.convertirCadenaDouble(paramsGrid.get(i).get("saldoCredito")));
					dto.setTraspaso(funciones.convertirCadenaDouble(paramsGrid.get(i).get("traspaso")));
					dto.setTipoSaldo(funciones.convertirCadenaInteger( paramsGrid.get(i).get("idSaldo")));
		
					listBarrido.add(dto);
				}

				ParamsBusquedaBarridoDto dtoParams = new ParamsBusquedaBarridoDto();
				dtoParams.setNoEmpresa(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("noEmpresa")));
				dtoParams.setNomEmpresa(funciones.validarCadena(paramsCriterio.get(0).get("nomEmpresa")));
				dtoParams.setIdBanco(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("noBanco")));
				dtoParams.setOpcFondeo(funciones.convertirCadenaBoolean(paramsCriterio.get(0).get("opcFondeo")));
				dtoParams.setOpcSobregiro(funciones.convertirCadenaBoolean(paramsCriterio.get(0).get("opcSobregiro")));
				dtoParams.setIdDivisa(funciones.validarCadena(paramsCriterio.get(0).get("idDivisa")));
				dtoParams.setChequera(funciones.validarCadena(paramsCriterio.get(0).get("idChequera")));

				mapResult = coinversionService.ejecutarBarrido(listBarrido, dtoParams);
				System.out.print("ejecuto Barrido:");
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:ejecutarBarrido");
		}
		return mapResult;
	}

	// solicitud de barrido

	@DirectMethod
	public List<LlenaComboEmpresasDto> llenarComboEmpresasCoinv(int iEmpresa, String sDivisa) {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		List<LlenaComboEmpresasDto> list = new ArrayList<LlenaComboEmpresasDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl");
				list = coinversionService.llenarCmbEmpresasCoinversionistas(iEmpresa, sDivisa);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:llenarComboEmpresasCoinv");
			return null;
		}
		return list;
	}

	@DirectMethod
	public List<LlenaComboGralDto> llenarComboBancos(int iEmpresa, String sDivisa) {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl");
				list = coinversionService.llenarCmbBancos(iEmpresa, sDivisa);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:llenarComboBancos");
		}
		return list;
	}

	@DirectMethod
	public List<Double> consultarSaldoFinal(int iEmpresa, int iBanco, String sDivisa, String sChequera) {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		List<Double> dSaldoFin = new ArrayList<Double>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl");
				dSaldoFin = coinversionService.consultarSaldoFinal(iEmpresa, iBanco, sDivisa, sChequera);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:consultarSaldoFinal");
		}
		return dSaldoFin;
	}

	@DirectMethod
	public List<Map<String, String>> consultarSaldoCreditoCoinvPorChequera(int iCoinversora, int iEmpresa,
			String sDivisa) {
		List<Map<String, String>> saldos = new ArrayList<Map<String, String>>();
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl");
				saldos = coinversionService.consultarSaldoCreditoCoinvPorChequera(iCoinversora, iEmpresa, sDivisa);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:consultarSaldoCreditoCoinvPorChequera");
		}
		return saldos;
	}

	@DirectMethod
	public List<LlenaComboGralDto> llenarComboBancos2(int iEmpresa, int iBanco, String sChequera) {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		List<LlenaComboGralDto> listBanco = new ArrayList<LlenaComboGralDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl");
				listBanco = coinversionService.consultarBancos(iEmpresa, iBanco, sChequera);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:llenarComboBancos2");
		}
		return listBanco;
	}

	@DirectMethod
	public Map<String, Object> ejecutarSolicitudBarrido(String parametros) {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		Map<String, Object> mapResult = new HashMap<String, Object>();
		ParamTraspasoCoinversionDto dtoParam = new ParamTraspasoCoinversionDto();
		Gson gson = new Gson();
		List<Map<String, String>> param = gson.fromJson(parametros, new TypeToken<ArrayList<Map<String, String>>>() {
		}.getType());

		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl");

				dtoParam.setIdEmpresaOrigen(funciones.convertirCadenaInteger(param.get(0).get("empresaOrigen")));
				dtoParam.setDescEmpresaOrigen(funciones.validarCadena(param.get(0).get("nomEmpresaOrigen")));
				dtoParam.setIdBancoOrigen(funciones.convertirCadenaInteger(param.get(0).get("bancoOrigen")));
				dtoParam.setIdChequeraOrigen(funciones.validarCadena(param.get(0).get("chequeraOrigen")));
				dtoParam.setIdEmpresaDestino(funciones.convertirCadenaInteger(param.get(0).get("empresaDestino")));
				dtoParam.setDescEmpresaDestino(funciones.validarCadena(param.get(0).get("nomEmpresaDestino")));
				dtoParam.setIdBancoDestino(funciones.convertirCadenaInteger(param.get(0).get("bancoDestino")));
				dtoParam.setIdChequeraDestino(funciones.validarCadena(param.get(0).get("chequeraDestino")));
				dtoParam.setIdDivisa(funciones.validarCadena(param.get(0).get("divisa")));
				dtoParam.setMontoTraspaso(funciones.convertirCadenaDouble(param.get(0).get("monto")));
				dtoParam.setCredito(funciones.convertirCadenaDouble(param.get(0).get("credito")));
				dtoParam.setConcepto(funciones.validarCadena(param.get(0).get("concepto")));
				dtoParam.setTipoSaldo(funciones.convertirCadenaInteger( param.get(0).get("idSaldo")));
				mapResult = coinversionService.ejecutarSolicitudBarrido(dtoParam);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:ejecutarSolicitudBarrido");
		}
		return mapResult;
	}

	// solicitud fondeo

	@DirectMethod
	public List<LlenaComboGralDto> llenarCmbBancosSolFondeo2(int iEmpresa, String sDivisa) {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		List<LlenaComboGralDto> listBanco = new ArrayList<LlenaComboGralDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl");
				listBanco = coinversionService.llenarCmbBancosSolFondeo2(iEmpresa, sDivisa);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:llenarCmbBancosSolFondeo2");
		}
		return listBanco;
	}

	@DirectMethod
	public List<LlenaComboChequeraDto> llenarCmbChequeras(int iBanco, int iNoEmpresa) {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		List<LlenaComboChequeraDto> list = new ArrayList<LlenaComboChequeraDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl");
				list = coinversionService.llenarCmbChequeras(iBanco, iNoEmpresa);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:llenarCmbChequeras");
		}
		return list;
	}

	@DirectMethod
	public List<Double> consultarSaldoFinal2(int iBanco, String sChequera, int iEmpresa, int iEmpInv, String sDivisa) {
		List<Double> dSaldoFin = new ArrayList<Double>();
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl");
				dSaldoFin = coinversionService.consultarSaldoFinal2(iBanco, sChequera, iEmpresa, iEmpInv, sDivisa);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:consultarSaldoFinal2");
		}
		return dSaldoFin;
	}

	@DirectMethod
	public Map<String, Object> ejecutarSolicitudFondeo(String parametros) {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		Map<String, Object> mapResult = new HashMap<String, Object>();
		ParamTraspasoCoinversionDto dtoParam = new ParamTraspasoCoinversionDto();
		Gson gson = new Gson();
		List<Map<String, String>> param = gson.fromJson(parametros, new TypeToken<ArrayList<Map<String, String>>>() {
		}.getType());

		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl");

				dtoParam.setIdEmpresaOrigen(funciones.convertirCadenaInteger(param.get(0).get("empresaOrigen")));
				dtoParam.setDescEmpresaOrigen(funciones.validarCadena(param.get(0).get("nomEmpresaOrigen")));
				dtoParam.setIdBancoOrigen(funciones.convertirCadenaInteger(param.get(0).get("bancoOrigen")));
				dtoParam.setIdChequeraOrigen(funciones.validarCadena(param.get(0).get("chequeraOrigen")));
				dtoParam.setIdEmpresaDestino(funciones.convertirCadenaInteger(param.get(0).get("empresaDestino")));
				dtoParam.setDescEmpresaDestino(funciones.validarCadena(param.get(0).get("nomEmpresaDestino")));
				dtoParam.setIdBancoDestino(funciones.convertirCadenaInteger(param.get(0).get("bancoDestino")));
				dtoParam.setIdChequeraDestino(funciones.validarCadena(param.get(0).get("chequeraDestino")));
				dtoParam.setIdDivisa(funciones.validarCadena(param.get(0).get("divisa")));
				dtoParam.setMontoTraspaso(funciones.convertirCadenaDouble(param.get(0).get("monto")));
				dtoParam.setConcepto(funciones.validarCadena(param.get(0).get("concepto")));
				dtoParam.setTipoSaldo(funciones.convertirCadenaInteger( param.get(0).get("idSaldo")));
				System.out.println("saldo:"+param.get(0).get("idSaldo"));

				mapResult = coinversionService.ejecutarSolicitudFondeo(dtoParam);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:ejecutarSolicitudFondeo");
		}
		return mapResult;
	}

	// saldo promedio y capitalizacion
	@DirectMethod
	public List<InteresesDto> valoresSdoPromyCapitalizacion(int iEmpresa, String sDivisa) {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		List<InteresesDto> list = new ArrayList<InteresesDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl");
				list = coinversionService.consultarValoresSdoPromyCapitalizacion(iEmpresa, sDivisa);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:valoresSdoPromyCapitalizacion");
		}
		return list;
	}

	@DirectMethod
	public List<InteresesDto> buscarIntereses(int iEmpresa, double dIva, String sDivisa, String sFecha1,
			String sFecha2) {
		List<InteresesDto> list = new ArrayList<InteresesDto>();
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl");
				list = coinversionService.buscarIntereses(iEmpresa, dIva, sDivisa, sFecha1, sFecha2);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:buscarIntereses");
		}
		return list;
	}

	@DirectMethod
	public List<InteresesDto> calcularInteresesPorDevengar(String datos) {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		List<InteresesDto> list = new ArrayList<InteresesDto>();
		InteresesDto dto = new InteresesDto();
		Gson gson = new Gson();
		List<Map<String, String>> param = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {
		}.getType());

		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl");

				dto.setDivisa(funciones.validarCadena(param.get(0).get("divisa")));
				dto.setFechaIni(funciones.ponerFechaDate(param.get(0).get("fecha1")));
				dto.setFechaFin(funciones.ponerFechaDate(param.get(0).get("fecha2")));
				dto.setNoEmpresa(funciones.convertirCadenaInteger(param.get(0).get("empresa")));
				dto.setInteres(funciones.convertirCadenaDouble(param.get(0).get("interes")));
				dto.setIsr(funciones.convertirCadenaDouble(param.get(0).get("isr")));
				dto.setTasaDeterminada(funciones.convertirCadenaDouble(param.get(0).get("tasaDeterminada")));
				dto.setBTasaPromedio(funciones.convertirCadenaBoolean(param.get(0).get("optPromedio")));
				dto.setBTasaDeterminada(funciones.convertirCadenaBoolean(param.get(0).get("optDeterminada")));
				dto.setDIsr(funciones.convertirCadenaDouble(param.get(0).get("dIsr")));
				dto.setDIva(funciones.convertirCadenaDouble(param.get(0).get("dIva")));
				dto.setDComision(funciones.convertirCadenaDouble(param.get(0).get("dComision")));
				dto.setInteresT(funciones.convertirCadenaDouble(param.get(0).get("interesT")));
				dto.setIsrT(funciones.convertirCadenaDouble(param.get(0).get("isrT")));
				dto.setComisionT(funciones.convertirCadenaDouble(param.get(0).get("comisionT")));
				dto.setInteresDevT(funciones.convertirCadenaDouble(param.get(0).get("interesDevT")));
				dto.setIsrDevT(funciones.convertirCadenaDouble(param.get(0).get("isrDevT")));
				dto.setIvaT(funciones.convertirCadenaDouble(param.get(0).get("ivaT")));

				list = coinversionService.calcularInteresesPorDevengar(dto);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:calcularInteresesPorDevengar");
		}
		return list;
	}

	@DirectMethod
	public Map<String, Object> ejecutarCapitalizacion(String datosGrid, String criterios) {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<InteresesDto> listGrid = new ArrayList<InteresesDto>();
		Gson gson = new Gson();
		List<Map<String, String>> paramsGrid = gson.fromJson(datosGrid,
				new TypeToken<ArrayList<Map<String, String>>>() {
				}.getType());
		List<Map<String, String>> paramsCriterio = gson.fromJson(criterios,
				new TypeToken<ArrayList<Map<String, String>>>() {
				}.getType());
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl");
				for (int i = 0; i < paramsGrid.size(); i++) {
					InteresesDto dto = new InteresesDto();
					dto.setTasaDeterminada(funciones.convertirCadenaDouble(paramsGrid.get(i).get("tasaDeterminada")));
					dto.setFactor(funciones.convertirCadenaDouble(paramsGrid.get(i).get("factor")));
					dto.setNoEmpresa(funciones.convertirCadenaInteger(paramsGrid.get(i).get("noEmpresa")));
					dto.setNomEmpresa(funciones.validarCadena(paramsGrid.get(i).get("nomEmpresa")));
					dto.setSaldoPromedio(funciones.convertirCadenaDouble(paramsGrid.get(i).get("saldoPromedio")));
					dto.setInteres(funciones.convertirCadenaDouble(paramsGrid.get(i).get("interes")));
					dto.setIsr(funciones.convertirCadenaDouble(paramsGrid.get(i).get("isr")));
					dto.setInteresDev(funciones.convertirCadenaDouble(paramsGrid.get(i).get("interesDev")));
					dto.setIsrVencido(funciones.convertirCadenaDouble(paramsGrid.get(i).get("isrVencido")));
					dto.setComision(funciones.convertirCadenaDouble(paramsGrid.get(i).get("comision")));
					dto.setIva(funciones.convertirCadenaDouble(paramsGrid.get(i).get("iva")));
					dto.setTotal(funciones.convertirCadenaDouble(paramsGrid.get(i).get("total")));

					listGrid.add(dto);
				}

				InteresesDto dtoParam = new InteresesDto();
				dtoParam.setDivisa(funciones.validarCadena(paramsCriterio.get(0).get("divisa")));
				dtoParam.setFechaIni(funciones.ponerFechaDate(paramsCriterio.get(0).get("fecha1")));
				dtoParam.setFechaFin(funciones.ponerFechaDate(paramsCriterio.get(0).get("fecha2")));
				dtoParam.setNoEmpresa(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("empresa")));
				dtoParam.setInteres(funciones.convertirCadenaDouble(paramsCriterio.get(0).get("interes")));
				dtoParam.setIsr(funciones.convertirCadenaDouble(paramsCriterio.get(0).get("isr")));
				dtoParam.setTasaDeterminada(
						funciones.convertirCadenaDouble(paramsCriterio.get(0).get("tasaDeterminada")));
				dtoParam.setBTasaPromedio(funciones.convertirCadenaBoolean(paramsCriterio.get(0).get("optPromedio")));
				dtoParam.setBTasaDeterminada(
						funciones.convertirCadenaBoolean(paramsCriterio.get(0).get("optDeterminada")));
				dtoParam.setDIsr(funciones.convertirCadenaDouble(paramsCriterio.get(0).get("dIsr")));
				dtoParam.setDIva(funciones.convertirCadenaDouble(paramsCriterio.get(0).get("dIva")));
				dtoParam.setDComision(funciones.convertirCadenaDouble(paramsCriterio.get(0).get("dComision")));
				dtoParam.setInteresT(funciones.convertirCadenaDouble(paramsCriterio.get(0).get("interesT")));
				dtoParam.setIsrT(funciones.convertirCadenaDouble(paramsCriterio.get(0).get("isrT")));
				dtoParam.setComisionT(funciones.convertirCadenaDouble(paramsCriterio.get(0).get("comisionT")));
				dtoParam.setInteresDevT(funciones.convertirCadenaDouble(paramsCriterio.get(0).get("interesDevT")));
				dtoParam.setIsrDevT(funciones.convertirCadenaDouble(paramsCriterio.get(0).get("isrDevT")));
				dtoParam.setIvaT(funciones.convertirCadenaDouble(paramsCriterio.get(0).get("ivaT")));

				mapResult = coinversionService.ejecutarCapitalizacion(listGrid, dtoParam);
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:ejecutarCapitalizacion");
		}
		return mapResult;
	}

	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteSaldoPromedio(Map parameters, ServletContext context) {
		JRDataSource jrDataSource = null;
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl", context);
				jrDataSource = coinversionService.obtenerReporteSaldoPromedio(parameters);
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:obtenerReporteSaldoPromedio");
		}
		return jrDataSource;
	}

	@DirectMethod
	public List<OrdenInversionDto> llenarCmbAnios() {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		List<OrdenInversionDto> anios = new ArrayList<OrdenInversionDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl");
				anios = coinversionService.llenarCmbAnios();
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:llenarCmbAnios");
		}
		return anios;
	}

	@DirectMethod
	public List<OrdenInversionDto> llenarCmbMeses(int iAnio) {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		List<OrdenInversionDto> meses = new ArrayList<OrdenInversionDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl");
				meses = coinversionService.llenarCmbMeses(iAnio);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:llenarCmbMeses");
		}
		return meses;
	}

	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteTasasInversion(Map parameters, ServletContext context) {
		JRDataSource jrDataSource = null;
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl", context);
				jrDataSource = coinversionService.obtenerReporteTasasInversion(parameters);
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:obtenerReporteTasasInversion");
		}
		return jrDataSource;
	}

	@DirectMethod
	public List<EmpresaDto> llenarGridEmpresasCoinv(int iEmpresa, String sDivisa) {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		List<EmpresaDto> list = new ArrayList<EmpresaDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl");
				list = coinversionService.llenarGridEmpresasCoinv(iEmpresa, sDivisa);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:llenarGridEmpresasCoinv");
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteEstadoCuenta(Map parameters, ServletContext context) {
		JRDataSource jrDataSource = null;
		try {

			coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl", context);
			jrDataSource = coinversionService.obtenerReporteEstadodeCuenta(parameters);

		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:obtenerReporteEstadoCuenta");
		}
		return jrDataSource;
	}

	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteHistoricoSaldos(Map parameters, ServletContext context) {
		JRDataSource jrDataSource = null;
		try {

			coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl", context);
			jrDataSource = coinversionService.obtenerReporteHistoricoSaldos(parameters);

		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:obtenerReporteHistoricoSaldos");
		}
		return jrDataSource;
	}
	@DirectMethod
	public List<LlenaComboGralDto> llenarTipoSaldo() {
		if (!Utilerias.haveSession(WebContextManager.get()))
			return null;
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
							coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl");
				list = coinversionService.llenarTipoSaldo();
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionAction, M:llenarComboChequerasBarrido");
		}
		return list;
	}
}
