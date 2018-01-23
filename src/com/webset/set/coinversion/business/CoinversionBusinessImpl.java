package com.webset.set.coinversion.business;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.ListableBeanFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.webset.set.coinversion.dao.CoinversionDao;
import com.webset.set.coinversion.dto.ArbolEmpresaDto;
import com.webset.set.coinversion.dto.ArbolEmpresaFondeoDto;
import com.webset.set.coinversion.dto.BarridoChequerasDto;
import com.webset.set.coinversion.dto.ControlFondeoChequesDto;
import com.webset.set.coinversion.dto.DivisasEncontradasDto;
import com.webset.set.coinversion.dto.HistSaldoDto;
import com.webset.set.coinversion.dto.InteresesDto;
import com.webset.set.coinversion.dto.ParamBusquedaFondeoDto;
import com.webset.set.coinversion.dto.ParamRetornoFondeoAutDto;
import com.webset.set.coinversion.dto.ParamTraspasoCoinversionDto;
import com.webset.set.coinversion.dto.ParametroDto;
import com.webset.set.coinversion.dto.ParamsBusquedaBarridoDto;
import com.webset.set.coinversion.dto.SaldoDto;
import com.webset.set.coinversion.dto.TmpSdoPromDto;
import com.webset.set.coinversion.dto.TmpTraspasoFondeoDto;
import com.webset.set.coinversion.dto.VencimientoInversionDto;
import com.webset.set.coinversion.service.CoinversionService;
import com.webset.set.layout.business.ImportarSaldosChequerasBusiness;
import com.webset.set.seguridad.dto.EmpresaDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.CatCtaBancoDto;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.OrdenInversionDto;
import com.webset.set.utilerias.dto.Retorno;
import com.webset.set.utilerias.dto.StoreParamsComunDto;
import com.webset.utils.tools.Utilerias;

/**
 * Clase que representa el modulo de Coinversion
 * 
 * @author Cristian Garc�a Garc�a
 * @author Jessica Arelly Cruz Cruz
 * @version 1.0(Coinversion)
 * @see
 * @since 10/June/2011
 */
public class CoinversionBusinessImpl implements CoinversionService {
	private CoinversionDao coinversionDao;
	private ImportarSaldosChequerasBusiness importarSaldosChequerasBusiness;
	private Bitacora bitacora = new Bitacora();
	private GlobalSingleton globalSingleton;
	private Funciones funciones = new Funciones();
	private static Logger logger = Logger.getLogger(CoinversionBusinessImpl.class);
	private static final String sFolioCuenta = "no_cuenta";
	private static final String sIdTipoCuenta = "T";
	private static final String sNumProducto = "1";
	private static final String IS_ORIGEN_MOV = "INV";

	public List<LlenaComboGralDto> llenarComboEmpresaRaiz(boolean bExistentes) {
		List<LlenaComboGralDto> listDatos = new ArrayList<LlenaComboGralDto>();
		try {
			listDatos = coinversionDao.consultarDatosEmpresaRaiz(bExistentes);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:llenarComboEmpresaRaiz");
		}
		return listDatos;
	}

	public List<LlenaComboGralDto> llenarComboBancoConcentradora(boolean bConcentradora, int idEmpresa,
			String idDivisa) {
		List<LlenaComboGralDto> listDatos = new ArrayList<LlenaComboGralDto>();
		try {
			listDatos = coinversionDao.consultarDatosBancoConcentradora(bConcentradora, idEmpresa, idDivisa);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:llenarComboBancoConcentradora");
			return null;
		}
		return listDatos;
	}

	public List<LlenaComboGralDto> llenarComboChequeraFondeo(int iIdEmpresa, String sIdDivisa, int iIdBanco) {
		List<LlenaComboGralDto> listDatos = new ArrayList<LlenaComboGralDto>();
		try {
			listDatos = coinversionDao.consultarChequeraFondeo(iIdEmpresa, sIdDivisa, iIdBanco);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:consultarChequeraFondeo");
		}
		return listDatos;
	}

	public List<ParamRetornoFondeoAutDto> consultarFondeoAutomatico(ParamBusquedaFondeoDto dtoBus) {
		List<ParamRetornoFondeoAutDto> listConsFondAut = new ArrayList<ParamRetornoFondeoAutDto>();
		double valorDivisaDls = 0;
		double uSaldoCoinversora = 0;
		double uSaldoCoinversoraOtraDivisa = 0;
		int iEmpresaCon = 0;
		int iEmpresaF = 0;
		String sIdDivisa = "";
		String sIdDivisaCon = "";

		try {
			globalSingleton = GlobalSingleton.getInstancia();
			valorDivisaDls = coinversionDao.consultarTipoCambioHoy();

			dtoBus.setFechaHoy(globalSingleton.getFechaHoy());
			dtoBus.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
			dtoBus.setNoLinea(coinversionDao.consultarIdDivisaSoin(dtoBus.getIdDivisa()) != null
					? Integer.parseInt(coinversionDao.consultarIdDivisaSoin(dtoBus.getIdDivisa()).trim()) : 0);
			if (globalSingleton.obtenerValorConfiguraSet(260).equals("SI") && dtoBus.getIdEmpresaRaiz() <= 0) {
				dtoBus.setIdEmpresa(dtoBus.getIdEmpresa());
				dtoBus.setBTodaslasChequeras(globalSingleton.obtenerValorConfiguraSet(3002).equals("SI"));
				dtoBus.setBUbicacionCCM(globalSingleton.obtenerValorConfiguraSet(3001).equals("SI"));
				listConsFondAut = coinversionDao.consultarFondeoAutomatico(dtoBus);
			} else if (globalSingleton.obtenerValorConfiguraSet(350).equals("SI") && dtoBus.getIdEmpresaRaiz() > 0) {
				dtoBus.setIdEmpresa(dtoBus.getIdEmpresaRaiz());
				dtoBus.setBTodaslasChequeras(globalSingleton.obtenerValorConfiguraSet(3002).equals("SI"));
				dtoBus.setBUbicacionCCM(globalSingleton.obtenerValorConfiguraSet(3001).equals("SI"));
				listConsFondAut = coinversionDao.consultarFondeoArbol(dtoBus);
			} else {
				dtoBus.setIdEmpresa(dtoBus.getIdEmpresa());
				dtoBus.setBTodaslasChequeras(false);
				dtoBus.setBUbicacionCCM(globalSingleton.obtenerValorConfiguraSet(3001).equals("SI"));
				listConsFondAut = coinversionDao.consultarFondeoAutomatico(dtoBus);
			}

			if (listConsFondAut.size() > 0) {
				for (int i = 0; i < listConsFondAut.size(); i++) {
					if (listConsFondAut.get(i).getPm() > 0 || listConsFondAut.get(i).getPmCheques() > 0) {
						listConsFondAut.get(i).setConcepto("RETIRO DE APORTACION");
						listConsFondAut.get(i).setIdDivisa(dtoBus.getIdDivisa());
						if (listConsFondAut.get(i).getSaldoChequera() < 0)
							listConsFondAut.get(i)
									.setImporteTraspaso(listConsFondAut.get(i).getPm()
											+ listConsFondAut.get(i).getSaldoMinimoChequera()
											- listConsFondAut.get(i).getImporteF());
						else
							listConsFondAut.get(i).setImporteTraspaso(
									listConsFondAut.get(i).getPm() - listConsFondAut.get(i).getImporteF());

						if (listConsFondAut.get(i).getImporteTraspaso() < 0)
							listConsFondAut.get(i).setImporteTraspaso(0);

						if (dtoBus.getIdEmpresaRaiz() < 0)
							iEmpresaCon = dtoBus.getIdEmpresa();
						else
							iEmpresaCon = listConsFondAut.get(i).getNoEmpresaOrigen();
						iEmpresaF = listConsFondAut.get(i).getNoEmpresaDestino();
						sIdDivisa = dtoBus.getIdDivisa();

						uSaldoCoinversora = coinversionDao.consultarSaldoCoinversora(iEmpresaCon, iEmpresaF, sIdDivisa);

						if (uSaldoCoinversora > 0)
							listConsFondAut.get(i).setSaldoCoinversion(uSaldoCoinversora);

						if (uSaldoCoinversora <= 0) {
							if (sIdDivisa.equals("MN"))
								sIdDivisaCon = "DLS";
							else
								sIdDivisaCon = "MN";

							uSaldoCoinversoraOtraDivisa = coinversionDao.consultarSaldoCoinversora(iEmpresaCon,
									iEmpresaF, sIdDivisaCon);

							if (sIdDivisaCon.equals("DLS"))
								listConsFondAut.get(i)
										.setSaldoCoinversionOtraDivisa(uSaldoCoinversoraOtraDivisa * valorDivisaDls);
							else
								listConsFondAut.get(i)
										.setSaldoCoinversionOtraDivisa(uSaldoCoinversoraOtraDivisa / valorDivisaDls);

							// .TextMatrix(0, LI_C_SALDO_COINVERSION_OTRADIV) =
							// "Saldo Coinv. " & ps_DivisaCont
						}
					} else {
						listConsFondAut.remove(i);
						i--;
					}
				}
			}

		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:consultarFondeoAutomatico");
			e.printStackTrace();
		}
		return listConsFondAut;
	}

	public double obtenerTipoCambioHoy() {
		try {
			return coinversionDao.consultarTipoCambioHoy();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:obtenerTipoCambioHoy");
		}
		return 0;
	}

	public List<LlenaComboGralDto> llenarBancosRaiz(int iIdRaiz, String sIdDivisa) {
		List<LlenaComboGralDto> listBancos = new ArrayList<LlenaComboGralDto>();
		try {
			listBancos = coinversionDao.consultarBancosRaiz(iIdRaiz, sIdDivisa);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:consultarBancosRaiz");
		}
		return listBancos;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> realizarFondeoAutomatico(List<ParamRetornoFondeoAutDto> listGridFon,
			ParamBusquedaFondeoDto dtoBus) {
		Map<String, Object> mapRetFon = new HashMap<String, Object>();
		int iIdBanco = 0;
		int iMismaEmpresa = 0;
		int iFolio = 0;
		int iAfectados = 0;
		int iFolMovi = 0;
		int iFolDeta = 0;
		int iFolioParametro = 0;
		int iFolioGrupo = 0;
		int iNoDocto = 0;
		int iEmpresaPad = 0;
		int iA2 = 0;
		int iIdBancoSalida = 0;
		int iTipoOperacion = 0;
		int iNoCuenta = 0;
		int iIdBancoEntrada = 0;
		double uSaldoChequera = 0;
		double uImporteTraspaso = 0;
		double uSaldoCoinversion = 0;
		double uTraspasosR = 0;
		double uImporte = 0;
		boolean bFondeo = false;
		boolean bRechazado = false;
		boolean bSolCredAutorizada;
		boolean bSinChequera = false;
		String sIdChequeraEntrada = "";
		String sIdChequeraSalida = "";
		String sDescTipoOper = "";
		List<String> mensajes = new ArrayList<String>();
		Map<String, Object> resGenerador = new HashMap<String, Object>();
		Map<String, Object> resPagador = new HashMap<String, Object>();
		List<Map<String, Object>> listRevFond = new ArrayList<Map<String, Object>>();
		List<CatCtaBancoDto> listRevisa = new ArrayList<CatCtaBancoDto>();
		List<Map<String, Object>> listArbolTrans = new ArrayList<Map<String, Object>>();
		List<ControlFondeoChequesDto> listRevisaPagos = new ArrayList<ControlFondeoChequesDto>();
		List<ArbolEmpresaFondeoDto> listTipoOper = new ArrayList<ArbolEmpresaFondeoDto>();
		List<CatCtaBancoDto> lisConsRevisaChequera2 = new ArrayList<CatCtaBancoDto>();

		String sFolio = "";
		ParametroDto dtoInsert = new ParametroDto();
		ParametroDto dtoInsParAut = new ParametroDto();
		GeneradorDto generadorDto = new GeneradorDto();
		StoreParamsComunDto dtoPagador = new StoreParamsComunDto();
		TmpTraspasoFondeoDto insTmp = new TmpTraspasoFondeoDto();
		try {
			globalSingleton = GlobalSingleton.getInstancia();

			coinversionDao.eliminarTmpTraspasoFondeo();// elimina los datos de
														// la tabala temporal
			// Este for obtiene las chequeras padre, ademas inserta los nuevos
			// parametros en
			// la tabla temporal
			for (int cont = 0; cont < listGridFon.size(); cont++) {
				lisConsRevisaChequera2 = new ArrayList<CatCtaBancoDto>();
				if (dtoBus.getIdEmpresaRaiz() > 0) {
					if (listGridFon.get(cont).getImporteTraspaso() <= 0) {
						mapRetFon.put("msgUsuario", " Existen registros con importe de traspaso cero");
						return mapRetFon;
					}

					lisConsRevisaChequera2 = coinversionDao.consultarRevisaChequera2(
							listGridFon.get(cont).getNoEmpresaOrigen(), dtoBus.getIdBanco(),
							listGridFon.get(cont).getIdDivisa());

					if (lisConsRevisaChequera2.size() <= 0) {
						mapRetFon.put("msgUsuario",
								"La empresa " + listGridFon.get(cont).getNoEmpresaOrigen()
										+ " No tiene chequera predeterminada de traspaso o del banco "
										+ "seleccionado o divisa seleccionada");
						return mapRetFon;
					}
					listGridFon.get(cont).setIdChequeraPadre(lisConsRevisaChequera2.get(0).getIdChequera());
				}

				insTmp = new TmpTraspasoFondeoDto();
				insTmp.setIdEmpresaOrigen(listGridFon.get(cont).getNoEmpresaOrigen());
				insTmp.setIdEmpresaDestino(listGridFon.get(cont).getNoEmpresaDestino());
				insTmp.setImporteTraspaso(listGridFon.get(cont).getImporteTraspaso());
				insTmp.setTipoArbol(dtoBus.getIdEmpresaRaiz());
				insTmp.setOrden(listGridFon.get(cont).getOrden());
				insTmp.setIdBanco(listGridFon.get(cont).getIdBanco());
				insTmp.setIdChequeraHijo(listGridFon.get(cont).getIdChequera());
				insTmp.setIdChequeraPadre(listGridFon.get(cont).getIdChequeraPadre());

				coinversionDao.insertarTmpTraspasoFondeo(insTmp);
			}
			if (dtoBus.getIdEmpresaRaiz() <= 0) {

				uSaldoChequera = this.sacarSaldoChequera(dtoBus.getIdEmpresa(), dtoBus.getIdChequera(),
						dtoBus.getIdBanco(), globalSingleton.obtenerValorConfiguraSet(252).trim().equals("SI"));
				for (int i = 0; i < listGridFon.size(); i++) {
					iIdBanco = dtoBus.getIdBanco();
					uImporteTraspaso = listGridFon.get(i).getImporteTraspaso();

					if (globalSingleton.obtenerValorConfiguraSet(264).trim().equals("SI")) {
						if (iMismaEmpresa == 0) {
							iMismaEmpresa = listGridFon.get(i).getNoEmpresaOrigen();
							uSaldoCoinversion = listGridFon.get(i).getSaldoCoinversion();
							uTraspasosR = uImporteTraspaso;
						} else if (iMismaEmpresa == listGridFon.get(i).getNoEmpresaOrigen()) {
							uTraspasosR = uTraspasosR + uImporteTraspaso;
						} else if (iMismaEmpresa != listGridFon.get(i).getNoEmpresaOrigen()) {
							iMismaEmpresa = listGridFon.get(i).getNoEmpresaOrigen();
							uSaldoCoinversion = listGridFon.get(i).getSaldoCoinversion();
							uTraspasosR = uImporteTraspaso;
						}

						bFondeo = true;

						if (verificarMovimientosNeg(dtoBus.getIdEmpresa(), iMismaEmpresa, uTraspasosR,
								dtoBus.getIdDivisa())) {
							if (dtoBus.isBMontoMinFondeo()) {
								if ((uSaldoChequera >= dtoBus.getMontoMinFondeo())
										&& (dtoBus.getMontoMinFondeo() > 0)) {
									if ((uSaldoChequera >= uImporteTraspaso)
											&& ((uSaldoCoinversion
													- (uTraspasosR - uImporteTraspaso)) >= uImporteTraspaso)
											&& (uImporteTraspaso >= dtoBus.getMontoMinFondeo())) {
										iFolio = obtenerFolioReal("no_folio_param");

										dtoInsert = new ParametroDto();

										dtoInsert.setNoEmpresa(dtoBus.getIdEmpresa());
										dtoInsert.setNoFolioParam(iFolio);
										dtoInsert.setAplica(1);
										dtoInsert.setSecuencia(1);
										dtoInsert.setIdTipoOperacion(3806);
										dtoInsert.setCuenta(listGridFon.get(i).getNoEmpresaDestino());
										dtoInsert.setIdEstatusMov("L");
										dtoInsert.setIdChequera(dtoBus.getIdChequera());
										dtoInsert.setIdBanco(dtoBus.getIdBanco());
										dtoInsert.setImporte(uImporteTraspaso);
										dtoInsert.setBSBC("S");
										dtoInsert.setFecValor(globalSingleton.getFechaHoy());
										dtoInsert.setIdEstatusReg("P");
										dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
										dtoInsert.setIdDivisa(listGridFon.get(i).getIdDivisa());
										dtoInsert.setIdFormaPago(3);
										dtoInsert.setIdBancoBenef(listGridFon.get(i).getIdBanco());
										dtoInsert.setIdChequeraBenef(listGridFon.get(i).getIdChequera());// Verificar
										dtoInsert.setOrigenMov("SET");
										dtoInsert.setConcepto(listGridFon.get(i).getConcepto());
										dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
										dtoInsert.setNoFolioMov(0);
										dtoInsert.setFolioRef(0);
										dtoInsert.setIdGrupo(iFolio);
										dtoInsert.setNoCliente(listGridFon.get(i).getNoEmpresaDestino());
										dtoInsert.setBeneficiario(listGridFon.get(i).getNomEmpresaDestino());

										iAfectados = 0;
										iAfectados = coinversionDao.insertarParametro(dtoInsert);

										dtoInsert = new ParametroDto();
										dtoInsert.setNoEmpresa(listGridFon.get(i).getNoEmpresaDestino());
										dtoInsert.setNoFolioParam(obtenerFolioReal("no_folio_param"));
										dtoInsert.setAplica(1);
										dtoInsert.setSecuencia(2);
										dtoInsert.setIdTipoOperacion(3806);
										dtoInsert.setCuenta(coinversionDao
												.consultarCuentaEmpresa(listGridFon.get(i).getNoEmpresaDestino()));
										dtoInsert.setIdEstatusMov("L");
										dtoInsert.setIdChequera(listGridFon.get(i).getIdChequera());// aki
																									// es
																									// chequeraBenef
																									// �?
										dtoInsert.setIdBanco(listGridFon.get(i).getIdBanco());
										dtoInsert.setImporte(uImporteTraspaso);
										dtoInsert.setBSBC("S");
										dtoInsert.setFecValor(globalSingleton.getFechaHoy());
										dtoInsert.setIdEstatusReg("P");
										dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
										dtoInsert.setIdDivisa(listGridFon.get(i).getIdDivisa());
										dtoInsert.setIdFormaPago(3);
										dtoInsert.setIdBancoBenef(dtoBus.getIdBanco());
										dtoInsert.setIdChequeraBenef(dtoBus.getIdChequera());// Verificar
										dtoInsert.setOrigenMov("SET");
										dtoInsert.setConcepto(listGridFon.get(i).getConcepto());
										dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
										dtoInsert.setNoFolioMov(1);
										dtoInsert.setFolioRef(1);
										dtoInsert.setIdGrupo(iFolio);
										dtoInsert.setNoCliente(dtoBus.getIdEmpresa());
										dtoInsert.setBeneficiario(listGridFon.get(i).getNomEmpresaDestino());

										iAfectados = 0;
										iAfectados = coinversionDao.insertarParametro(dtoInsert);

										generadorDto = new GeneradorDto();
										generadorDto.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
										generadorDto.setNomForma("FondeoAutomatico");
										generadorDto.setEmpresa(dtoBus.getIdEmpresa());
										generadorDto.setFolParam(iFolio);
										generadorDto.setFolMovi(iFolMovi);
										generadorDto.setFolDeta(iFolDeta);
										generadorDto.setResult(0);
										generadorDto.setMensajeResp("inicia generador");

										resGenerador = coinversionDao.ejecutarGenerador(generadorDto);

										if (funciones
												.convertirCadenaInteger(resGenerador.get("result").toString()) != 0) {
											mapRetFon.put("msgUsuario",
													"Error en Generador en btnEjecutar " + resGenerador.get("result"));
											break;
										} else {
											uSaldoChequera = uSaldoChequera - uImporteTraspaso;
											if (uImporteTraspaso > 0)
												uImporte = uImporte + uImporteTraspaso;
											else
												uImporte = uImporte + 0;
											sFolio += resGenerador.get("folDeta").toString() + ",";
										}
									} else if (uSaldoChequera < uImporteTraspaso
											&& (uSaldoCoinversion - (uTraspasosR - uImporteTraspaso)) >= uSaldoChequera
											&& (uSaldoChequera >= dtoBus.getMontoMinFondeo())) {
										iFolio = obtenerFolioReal("no_folio_param");

										iAfectados = 0;
										dtoInsert = new ParametroDto();

										dtoInsert.setNoEmpresa(dtoBus.getIdEmpresa());
										dtoInsert.setNoFolioParam(iFolio);
										dtoInsert.setAplica(1);
										dtoInsert.setSecuencia(1);
										dtoInsert.setIdTipoOperacion(3806);
										dtoInsert.setCuenta(listGridFon.get(i).getNoEmpresaDestino());
										dtoInsert.setIdEstatusMov("L");
										dtoInsert.setIdChequera(dtoBus.getIdChequera());
										dtoInsert.setIdBanco(dtoBus.getIdBanco());
										dtoInsert.setImporte(uSaldoChequera);
										dtoInsert.setBSBC("S");
										dtoInsert.setFecValor(globalSingleton.getFechaHoy());
										dtoInsert.setIdEstatusReg("P");
										dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
										dtoInsert.setIdDivisa(listGridFon.get(i).getIdDivisa());
										dtoInsert.setIdFormaPago(3);
										dtoInsert.setIdBancoBenef(listGridFon.get(i).getIdBanco());
										dtoInsert.setIdChequeraBenef(listGridFon.get(i).getIdChequera());// Verificar
																											// por
																											// ue
																											// en
																											// el
																											// grid
																											// tiene
																											// benef
																											// pero
																											// no
																											// se
																											// asigna
																											// valor
																											// a
																											// el
										dtoInsert.setOrigenMov("SET");
										dtoInsert.setConcepto(listGridFon.get(i).getConcepto());
										dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
										dtoInsert.setNoFolioMov(0);
										dtoInsert.setFolioRef(0);
										dtoInsert.setIdGrupo(iFolio);
										dtoInsert.setNoCliente(listGridFon.get(i).getNoEmpresaDestino());
										dtoInsert.setBeneficiario(listGridFon.get(i).getNomEmpresaDestino());

										iAfectados = 0;
										iAfectados = coinversionDao.insertarParametro(dtoInsert);

										dtoInsert = new ParametroDto();
										dtoInsert.setNoEmpresa(listGridFon.get(i).getNoEmpresaDestino());
										dtoInsert.setNoFolioParam(obtenerFolioReal("no_folio_param"));
										dtoInsert.setAplica(1);
										dtoInsert.setSecuencia(2);
										dtoInsert.setIdTipoOperacion(3806);
										dtoInsert.setCuenta(coinversionDao
												.consultarCuentaEmpresa(listGridFon.get(i).getNoEmpresaDestino()));
										dtoInsert.setIdEstatusMov("L");
										dtoInsert.setIdChequera(listGridFon.get(i).getIdChequera());// aki
																									// es
																									// chequeraBenef
																									// �?
										dtoInsert.setIdBanco(listGridFon.get(i).getIdBanco());
										dtoInsert.setImporte(uSaldoChequera);
										dtoInsert.setBSBC("S");
										dtoInsert.setFecValor(globalSingleton.getFechaHoy());
										dtoInsert.setIdEstatusReg("P");
										dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
										dtoInsert.setIdDivisa(listGridFon.get(i).getIdDivisa());
										dtoInsert.setIdFormaPago(3);
										dtoInsert.setIdBancoBenef(dtoBus.getIdBanco());
										dtoInsert.setIdChequeraBenef(dtoBus.getIdChequera());// Verificar
										dtoInsert.setOrigenMov("SET");
										dtoInsert.setConcepto(listGridFon.get(i).getConcepto());
										dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
										dtoInsert.setNoFolioMov(1);
										dtoInsert.setFolioRef(1);
										dtoInsert.setIdGrupo(iFolio);
										dtoInsert.setNoCliente(dtoBus.getIdEmpresa());
										dtoInsert.setBeneficiario(listGridFon.get(i).getNomEmpresaDestino());

										iAfectados = 0;
										iAfectados = coinversionDao.insertarParametro(dtoInsert);

										generadorDto = new GeneradorDto();
										generadorDto.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
										generadorDto.setNomForma("FondeoAutomatico");
										generadorDto.setEmpresa(dtoBus.getIdEmpresa());
										generadorDto.setFolParam(iFolio);
										generadorDto.setFolMovi(iFolMovi);
										generadorDto.setFolDeta(iFolDeta);
										generadorDto.setResult(0);
										generadorDto.setMensajeResp("inicia generador");

										resGenerador = coinversionDao.ejecutarGenerador(generadorDto);
										if (funciones
												.convertirCadenaInteger(resGenerador.get("result").toString()) != 0) {
											mapRetFon.put("msgUsuario",
													"Error en Generador en btnEjecutar " + resGenerador.get("result"));
											break;
										} else {
											uSaldoChequera = 0;// asi se
																// inicializa en
																// Vb
											if (uSaldoChequera > 0)
												uImporte = uImporte + uSaldoChequera;
											else
												uImporte = uImporte + 0;
											sFolio += resGenerador.get("folDeta").toString() + ",";
										}
									} else if (uSaldoChequera > uImporteTraspaso
											&& (uSaldoCoinversion - (uTraspasosR - uImporteTraspaso)) < uSaldoChequera
											&& (uSaldoCoinversion - (uTraspasosR - uImporteTraspaso) >= dtoBus
													.getMontoMinFondeo())) {
										iFolio = obtenerFolioReal("no_folio_param");

										iAfectados = 0;
										dtoInsert = new ParametroDto();

										dtoInsert.setNoEmpresa(dtoBus.getIdEmpresa());
										dtoInsert.setNoFolioParam(iFolio);
										dtoInsert.setAplica(1);
										dtoInsert.setSecuencia(1);
										dtoInsert.setIdTipoOperacion(3806);
										dtoInsert.setCuenta(listGridFon.get(i).getNoEmpresaDestino());
										dtoInsert.setIdEstatusMov("L");
										dtoInsert.setIdChequera(dtoBus.getIdChequera());
										dtoInsert.setIdBanco(dtoBus.getIdBanco());
										dtoInsert.setImporte(uSaldoCoinversion - (uTraspasosR - uImporteTraspaso));
										dtoInsert.setBSBC("S");
										dtoInsert.setFecValor(globalSingleton.getFechaHoy());
										dtoInsert.setIdEstatusReg("P");
										dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
										dtoInsert.setIdDivisa(listGridFon.get(i).getIdDivisa());
										dtoInsert.setIdFormaPago(3);
										dtoInsert.setIdBancoBenef(listGridFon.get(i).getIdBanco());
										dtoInsert.setIdChequeraBenef(listGridFon.get(i).getIdChequera());// Verificar
																											// por
																											// ue
																											// en
																											// el
																											// grid
																											// tiene
																											// benef
																											// pero
																											// no
																											// se
																											// asigna
																											// valor
																											// a
																											// el
										dtoInsert.setOrigenMov("SET");
										dtoInsert.setConcepto(listGridFon.get(i).getConcepto());
										dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
										dtoInsert.setNoFolioMov(0);
										dtoInsert.setFolioRef(0);
										dtoInsert.setIdGrupo(iFolio);
										dtoInsert.setNoCliente(listGridFon.get(i).getNoEmpresaDestino());
										dtoInsert.setBeneficiario(listGridFon.get(i).getNomEmpresaDestino());

										iAfectados = 0;
										iAfectados = coinversionDao.insertarParametro(dtoInsert);

										dtoInsert = new ParametroDto();
										dtoInsert.setNoEmpresa(listGridFon.get(i).getNoEmpresaDestino());
										dtoInsert.setNoFolioParam(obtenerFolioReal("no_folio_param"));
										dtoInsert.setAplica(1);
										dtoInsert.setSecuencia(2);
										dtoInsert.setIdTipoOperacion(3806);
										dtoInsert.setCuenta(coinversionDao
												.consultarCuentaEmpresa(listGridFon.get(i).getNoEmpresaDestino()));
										dtoInsert.setIdEstatusMov("L");
										dtoInsert.setIdChequera(listGridFon.get(i).getIdChequera());// aki
																									// es
																									// chequeraBenef
																									// �?
										dtoInsert.setIdBanco(listGridFon.get(i).getIdBanco());
										dtoInsert.setImporte(uSaldoCoinversion - (uTraspasosR - uImporteTraspaso));
										dtoInsert.setBSBC("S");
										dtoInsert.setFecValor(globalSingleton.getFechaHoy());
										dtoInsert.setIdEstatusReg("P");
										dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
										dtoInsert.setIdDivisa(listGridFon.get(i).getIdDivisa());
										dtoInsert.setIdFormaPago(3);
										dtoInsert.setIdBancoBenef(dtoBus.getIdBanco());
										dtoInsert.setIdChequeraBenef(dtoBus.getIdChequera());// Verificar
										dtoInsert.setOrigenMov("SET");
										dtoInsert.setConcepto(listGridFon.get(i).getConcepto());
										dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
										dtoInsert.setNoFolioMov(1);
										dtoInsert.setFolioRef(1);
										dtoInsert.setIdGrupo(iFolio);
										dtoInsert.setNoCliente(dtoBus.getIdEmpresa());
										dtoInsert.setBeneficiario(listGridFon.get(i).getNomEmpresaDestino());

										iAfectados = 0;
										iAfectados = coinversionDao.insertarParametro(dtoInsert);

										generadorDto = new GeneradorDto();
										generadorDto.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
										generadorDto.setNomForma("FondeoAutomatico");
										generadorDto.setEmpresa(dtoBus.getIdEmpresa());
										generadorDto.setFolParam(iFolio);
										generadorDto.setFolMovi(iFolMovi);
										generadorDto.setFolDeta(iFolDeta);
										generadorDto.setResult(0);
										generadorDto.setMensajeResp("inicia generador");

										resGenerador = coinversionDao.ejecutarGenerador(generadorDto);
										if (funciones
												.convertirCadenaInteger(resGenerador.get("result").toString()) != 0) {
											mapRetFon.put("msgUsuario",
													"Error en Generador en btnEjecutar " + resGenerador.get("result"));
											break;
										} else {
											uSaldoChequera = uSaldoChequera
													- (uSaldoCoinversion - (uTraspasosR - uImporteTraspaso));
											if ((uSaldoCoinversion - (uTraspasosR - uImporteTraspaso)) > 0)
												uImporte = uImporte
														+ (uSaldoCoinversion - (uTraspasosR - uImporteTraspaso));
											else
												uImporte = uImporte + 0;

											sFolio += resGenerador.get("folDeta").toString() + ",";
										}
									} else {
										listGridFon.get(i).setRechazado("S");
										bRechazado = true;
									}
								} else {
									listGridFon.get(i).setRechazado("S");
									bRechazado = true;
								}
							} else// Proceso para el fondeo automatico sin
									// montos minimos
							{
								Map<String, Object> mapParen = new HashMap<String, Object>();

								mapParen = sacarParentesco(listGridFon.get(i).getNoEmpresaDestino());
								if (mapParen.get("msgUsuario").toString() != null
										|| !mapParen.get("msgUsuario").toString().equals(""))
									mensajes.add(mapParen.get("msgUsuario").toString());

								iEmpresaPad = funciones.convertirCadenaInteger(mapParen.get("empresa").toString());

								if (iEmpresaPad != 0 && !listGridFon.get(i).getIdChequera().equals("")
										&& listGridFon.get(i).getIdBanco() != 0) {
									iFolio = obtenerFolioReal("no_folio_param");
									iFolioParametro = obtenerFolioReal("no_folio_param");
									iFolioGrupo = iFolioParametro;
									iNoDocto = obtenerFolioReal("docto_cred");

									if (globalSingleton.obtenerValorConfiguraSet(264) != null
											&& globalSingleton.obtenerValorConfiguraSet(264).trim().equals("NO"))
										bSolCredAutorizada = true;
									else
										bSolCredAutorizada = false;

									if (listGridFon.get(i).getSaldoCoinversion() > 0
											&& ((listGridFon.get(i).getImporteTraspaso()
													- listGridFon.get(i).getSaldoCoinversion())) > 0) {

										dtoInsert = new ParametroDto();
										dtoInsert.setNoEmpresa(dtoBus.getIdEmpresa());
										dtoInsert.setNoFolioParam(iFolio);
										dtoInsert.setAplica(1);
										dtoInsert.setSecuencia(1);
										dtoInsert.setIdTipoOperacion(3806);
										dtoInsert.setCuenta(listGridFon.get(i).getNoEmpresaDestino());
										dtoInsert.setIdEstatusMov("L");
										dtoInsert.setIdChequera(dtoBus.getIdChequera());
										dtoInsert.setIdBanco(dtoBus.getIdBanco());
										dtoInsert.setImporte(listGridFon.get(i).getSaldoCoinversion());
										dtoInsert.setBSBC("S");
										dtoInsert.setFecValor(globalSingleton.getFechaHoy());
										dtoInsert.setIdEstatusReg("P");
										dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
										dtoInsert.setIdDivisa(listGridFon.get(i).getIdDivisa());
										dtoInsert.setIdFormaPago(3);
										dtoInsert.setIdBancoBenef(listGridFon.get(i).getIdBanco());
										dtoInsert.setIdChequeraBenef(listGridFon.get(i).getIdChequera());// Verificar
																											// por
																											// ue
																											// en
																											// el
																											// grid
																											// tiene
																											// benef
																											// pero
																											// no
																											// se
																											// asigna
																											// valor
																											// a
																											// el
										dtoInsert.setOrigenMov("SET");
										dtoInsert.setConcepto(listGridFon.get(i).getConcepto());
										dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
										dtoInsert.setNoFolioMov(0);
										dtoInsert.setFolioRef(0);
										dtoInsert.setIdGrupo(iFolio);
										dtoInsert.setNoCliente(listGridFon.get(i).getNoEmpresaDestino());
										dtoInsert.setBeneficiario(listGridFon.get(i).getNomEmpresaDestino());

										iAfectados = 0;
										iAfectados = coinversionDao.insertarParametro(dtoInsert);

										dtoInsert = new ParametroDto();
										dtoInsert.setNoEmpresa(listGridFon.get(i).getNoEmpresaDestino());
										dtoInsert.setNoFolioParam(obtenerFolioReal("no_folio_param"));
										dtoInsert.setAplica(1);
										dtoInsert.setSecuencia(2);
										dtoInsert.setIdTipoOperacion(3806);
										dtoInsert.setCuenta(coinversionDao
												.consultarCuentaEmpresa(listGridFon.get(i).getNoEmpresaDestino()));
										dtoInsert.setIdEstatusMov("L");
										dtoInsert.setIdChequera(listGridFon.get(i).getIdChequera());// aki
																									// es
																									// chequeraBenef
																									// �?
										dtoInsert.setIdBanco(listGridFon.get(i).getIdBanco());
										dtoInsert.setImporte(listGridFon.get(i).getSaldoCoinversion());
										dtoInsert.setBSBC("S");
										dtoInsert.setFecValor(globalSingleton.getFechaHoy());
										dtoInsert.setIdEstatusReg("P");
										dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
										dtoInsert.setIdDivisa(listGridFon.get(i).getIdDivisa());
										dtoInsert.setIdFormaPago(3);
										dtoInsert.setIdBancoBenef(dtoBus.getIdBanco());
										dtoInsert.setIdChequeraBenef(dtoBus.getIdChequera());// Verificar
										dtoInsert.setOrigenMov("SET");
										dtoInsert.setConcepto(listGridFon.get(i).getConcepto());
										dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
										dtoInsert.setNoFolioMov(1);
										dtoInsert.setFolioRef(1);
										dtoInsert.setIdGrupo(iFolio);
										dtoInsert.setNoCliente(dtoBus.getIdEmpresa());
										dtoInsert.setBeneficiario(listGridFon.get(i).getNomEmpresaDestino());

										iAfectados = 0;
										iAfectados = coinversionDao.insertarParametro(dtoInsert);

										generadorDto = new GeneradorDto();
										generadorDto.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
										generadorDto.setNomForma("FondeoAutomatico");
										generadorDto.setEmpresa(dtoBus.getIdEmpresa());
										generadorDto.setFolParam(iFolio);
										generadorDto.setFolMovi(iFolMovi);
										generadorDto.setFolDeta(iFolDeta);
										generadorDto.setResult(0);
										generadorDto.setMensajeResp("inicia generador");

										resGenerador = coinversionDao.ejecutarGenerador(generadorDto);
										if (funciones
												.convertirCadenaInteger(resGenerador.get("result").toString()) != 0) {
											mapRetFon.put("msgUsuario",
													"Error en Generador en btnEjecutar " + resGenerador.get("result"));
											break;
										} else {
											if (listGridFon.get(i).getSaldoCoinversion() > 0)
												uImporte = uImporte + uSaldoCoinversion;
											else
												uImporte = uImporte + 0;

											sFolio += resGenerador.get("folDeta").toString() + ",";
										}

										if (!bSolCredAutorizada) {
											dtoInsert = new ParametroDto();
											dtoInsert.setNoEmpresa(listGridFon.get(i).getNoEmpresaDestino());
											dtoInsert.setNoFolioParam(obtenerFolioReal("no_folio_param"));
											dtoInsert.setAplica(1);
											dtoInsert.setSecuencia(1);
											dtoInsert.setIdTipoOperacion(3818);
											dtoInsert.setCuenta(coinversionDao
													.consultarCuentaEmpresa(listGridFon.get(i).getNoEmpresaDestino()));
											dtoInsert.setIdEstatusMov("L");
											dtoInsert.setIdChequera(listGridFon.get(i).getIdChequera());// aki
																										// es
																										// chequeraBenef
																										// �?
											dtoInsert.setIdBanco(listGridFon.get(i).getIdBanco());
											dtoInsert.setImporte(listGridFon.get(i).getImporteTraspaso()
													- listGridFon.get(i).getSaldoCoinversion());
											dtoInsert.setBSBC("S");
											dtoInsert.setFecValor(globalSingleton.getFechaHoy());
											dtoInsert.setIdEstatusReg("P");
											dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
											dtoInsert.setIdDivisa(listGridFon.get(i).getIdDivisa());
											dtoInsert.setIdFormaPago(3);
											dtoInsert.setIdBancoBenef(dtoBus.getIdBanco());
											dtoInsert.setIdChequeraBenef(dtoBus.getIdChequera());// Verificar
											dtoInsert.setOrigenMov("SET");
											dtoInsert.setConcepto(
													"PRESTAMO AUTOMATICO POR FALTA DE SALDO EN COINVERSION");
											dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
											dtoInsert.setNoFolioMov(1);
											dtoInsert.setFolioRef(1);
											dtoInsert.setIdGrupo(iFolio);
											dtoInsert.setNoCliente(iEmpresaPad);
											dtoInsert.setBeneficiario(listGridFon.get(i).getNomEmpresaDestino());

											iAfectados = 0;
											iAfectados = coinversionDao.insertarParametro(dtoInsert);

											generadorDto = new GeneradorDto();
											generadorDto
													.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
											generadorDto.setNomForma("FondeoAutomatico");
											generadorDto.setEmpresa(dtoBus.getIdEmpresa());
											generadorDto.setFolParam(iFolio);
											generadorDto.setFolMovi(iFolMovi);
											generadorDto.setFolDeta(iFolDeta);
											generadorDto.setResult(0);
											generadorDto.setMensajeResp("inicia generador");

											resGenerador = coinversionDao.ejecutarGenerador(generadorDto);

											if (funciones.convertirCadenaInteger(
													resGenerador.get("result").toString()) != 0) {
												mapRetFon.put("msgUsuario", "Error en Generador en btnEjecutar "
														+ resGenerador.get("result"));
												break;
											} else {
												if (listGridFon.get(i).getSaldoCoinversion() > 0)
													uImporte = uImporte + uSaldoCoinversion;
												else
													uImporte = uImporte + 0;

												sFolio += resGenerador.get("folDeta").toString() + ",";
											}
										} else {
											dtoInsParAut = new ParametroDto();
											dtoInsParAut.setNoEmpresa(dtoBus.getIdEmpresa());
											dtoInsParAut.setNoFolioParam(iFolioParametro);
											dtoInsParAut.setAplica(1);
											dtoInsParAut.setSecuencia(1);
											dtoInsParAut.setIdTipoOperacion(3808);
											dtoInsParAut.setCuenta(
													coinversionDao.consultarCuentaEmpresa(dtoBus.getIdEmpresa()));
											dtoInsParAut.setIdEstatusMov("L");
											dtoInsParAut.setIdChequera(dtoBus.getIdChequera());
											dtoInsParAut.setIdBanco(dtoBus.getIdBanco());
											dtoInsParAut.setImporte(listGridFon.get(i).getImporteTraspaso()
													- listGridFon.get(i).getSaldoCoinversion());
											dtoInsParAut.setBSBC("S");
											dtoInsParAut.setFecValor(globalSingleton.getFechaHoy());
											dtoInsParAut.setFecValorOriginal(globalSingleton.getFechaHoy());
											dtoInsParAut.setIdEstatusReg("P");
											dtoInsParAut
													.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
											dtoInsParAut.setFecValorAlta(globalSingleton.getFechaHoy());
											dtoInsParAut.setIdDivisa(listGridFon.get(i).getIdDivisa());
											dtoInsParAut.setIdFormaPago(3);
											dtoInsParAut.setImporteOriginal(listGridFon.get(i).getImporteTraspaso()
													- listGridFon.get(i).getSaldoCoinversion());
											dtoInsParAut.setFecOperacion(globalSingleton.getFechaHoy());
											dtoInsParAut.setIdBancoBenef(listGridFon.get(i).getIdBanco());
											dtoInsParAut.setIdChequeraBenef(listGridFon.get(i).getIdChequera());// Verificar
																												// por
																												// ue
																												// en
																												// el
																												// grid
																												// tiene
																												// benef
																												// pero
																												// no
																												// se
																												// asigna
																												// valor
																												// a
																												// el
											dtoInsParAut.setOrigenMov("SET");
											dtoInsParAut.setConcepto("TRASPASO CREDITO POR PRESTAMO");
											dtoInsParAut.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
											dtoInsParAut.setNoFolioMov(0);
											dtoInsParAut.setFolioRef(0);
											dtoInsParAut.setIdGrupo(iFolioParametro);
											dtoInsParAut.setNoDocto(iNoDocto);
											dtoInsParAut.setNoCliente(listGridFon.get(i).getNoEmpresaDestino());
											dtoInsParAut.setBeneficiario(listGridFon.get(i).getNomEmpresaDestino());
											dtoInsParAut.setValorTasa(0);
											dtoInsParAut.setDiasInv(0);

											iAfectados = 0;
											iAfectados = coinversionDao.insertarParAutPres(dtoInsParAut);

											dtoInsParAut = new ParametroDto();
											dtoInsParAut.setNoEmpresa(listGridFon.get(i).getNoEmpresaDestino());
											dtoInsParAut.setNoFolioParam(obtenerFolioReal("no_folio_param"));
											dtoInsParAut.setAplica(1);
											dtoInsParAut.setSecuencia(2);
											dtoInsParAut.setIdTipoOperacion(3808);
											dtoInsParAut.setCuenta(coinversionDao
													.consultarCuentaEmpresa(listGridFon.get(i).getNoEmpresaDestino()));
											dtoInsParAut.setIdEstatusMov("L");
											dtoInsParAut.setIdChequera(listGridFon.get(i).getIdChequera());// aki
																											// es
																											// chequeraBenef
																											// �?
											dtoInsParAut.setIdBanco(listGridFon.get(i).getIdBanco());
											dtoInsParAut.setImporte(listGridFon.get(i).getImporteTraspaso()
													- listGridFon.get(i).getSaldoCoinversion());
											dtoInsParAut.setBSBC("S");
											dtoInsParAut.setFecValor(globalSingleton.getFechaHoy());
											dtoInsParAut.setFecValorOriginal(globalSingleton.getFechaHoy());
											dtoInsParAut.setIdEstatusReg("P");
											dtoInsParAut
													.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
											dtoInsParAut.setFecValorAlta(globalSingleton.getFechaHoy());
											dtoInsParAut.setIdDivisa(listGridFon.get(i).getIdDivisa());
											dtoInsParAut.setIdFormaPago(3);
											dtoInsParAut.setImporteOriginal(listGridFon.get(i).getImporteTraspaso()
													- listGridFon.get(i).getSaldoCoinversion());
											dtoInsParAut.setFecOperacion(globalSingleton.getFechaHoy());
											dtoInsParAut.setIdBancoBenef(listGridFon.get(i).getIdBanco());
											dtoInsParAut.setIdChequeraBenef(listGridFon.get(i).getIdChequera());// Verificar
																												// por
																												// ue
																												// en
																												// el
																												// grid
																												// tiene
																												// benef
																												// pero
																												// no
																												// se
																												// asigna
																												// valor
																												// a
																												// el
											dtoInsParAut.setOrigenMov("SET");
											dtoInsParAut.setConcepto("TRASPASO CREDITO POR PRESTAMO");
											dtoInsParAut.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
											dtoInsParAut.setNoFolioMov(1);
											dtoInsParAut.setFolioRef(1);
											dtoInsParAut.setIdGrupo(iFolioParametro);
											dtoInsParAut.setNoDocto(iNoDocto);
											dtoInsParAut.setNoCliente(listGridFon.get(i).getNoEmpresaDestino());
											dtoInsParAut.setBeneficiario(listGridFon.get(i).getNomEmpresaDestino());
											dtoInsParAut.setValorTasa(0);
											dtoInsParAut.setDiasInv(0);

											iAfectados = 0;
											iAfectados = coinversionDao.insertarParAutPres(dtoInsParAut);

											generadorDto = new GeneradorDto();
											generadorDto
													.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
											generadorDto.setNomForma("FondeoAutomatico");
											generadorDto.setEmpresa(dtoBus.getIdEmpresa());
											generadorDto.setFolParam(iFolioParametro);
											generadorDto.setFolMovi(iFolMovi);
											generadorDto.setFolDeta(iFolDeta);
											generadorDto.setResult(0);
											generadorDto.setMensajeResp("inicia generador");

											resGenerador = coinversionDao.ejecutarGenerador(generadorDto);

											if (funciones.convertirCadenaInteger(
													resGenerador.get("result").toString()) != 0) {
												mapRetFon.put("msgUsuario", "Error en Generador en btnEjecutar "
														+ resGenerador.get("result"));
												break;
											} else {
												if (listGridFon.get(i).getSaldoCoinversion() > 0)
													uImporte = uImporte + listGridFon.get(i).getSaldoCoinversion();
												else
													uImporte = uImporte + 0;
												/*
												 * If pb_SolCredAutorizada =
												 * False Then Revisar aqui que
												 * folio se debe guardar
												 * ps_folio = ps_folio +
												 * Str(pd_FolioDet) + "," Else
												 * ps_folio = ps_folio + Str(a2)
												 * + "," End If
												 */
												sFolio += resGenerador.get("folDeta").toString() + ",";
											}
										}
									} else {
										if (!bSolCredAutorizada) {
											dtoInsert = new ParametroDto();
											dtoInsert.setNoEmpresa(listGridFon.get(i).getNoEmpresaDestino());
											dtoInsert.setNoFolioParam(iFolio);
											dtoInsert.setAplica(1);
											dtoInsert.setSecuencia(1);
											dtoInsert.setIdTipoOperacion(3818);
											dtoInsert.setCuenta(coinversionDao
													.consultarCuentaEmpresa(listGridFon.get(i).getNoEmpresaDestino()));
											dtoInsert.setIdEstatusMov("L");
											dtoInsert.setIdChequera(listGridFon.get(i).getIdChequera());// aki
																										// es
																										// chequeraBenef
																										// �?
											dtoInsert.setIdBanco(listGridFon.get(i).getIdBanco());
											dtoInsert.setImporte(listGridFon.get(i).getImporteTraspaso());
											dtoInsert.setBSBC("S");
											dtoInsert.setFecValor(globalSingleton.getFechaHoy());
											dtoInsert.setIdEstatusReg("P");
											dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
											dtoInsert.setIdDivisa(listGridFon.get(i).getIdDivisa());
											dtoInsert.setIdFormaPago(3);
											dtoInsert.setIdBancoBenef(dtoBus.getIdBanco());
											dtoInsert.setIdChequeraBenef(listGridFon.get(i).getIdChequera());
											dtoInsert.setOrigenMov("SET");
											dtoInsert.setConcepto(
													"PRESTAMO AUTOMATICO POR FALTA DE SALDO EN COINVERSION");
											dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
											dtoInsert.setNoFolioMov(1);
											dtoInsert.setFolioRef(1);
											dtoInsert.setIdGrupo(0);
											dtoInsert.setNoCliente(iEmpresaPad);
											dtoInsert.setBeneficiario(listGridFon.get(i).getNomEmpresaDestino());

											iAfectados = 0;
											iAfectados = coinversionDao.insertarParametro(dtoInsert);

											generadorDto = new GeneradorDto();
											generadorDto
													.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
											generadorDto.setNomForma("FondeoAutomatico");
											generadorDto.setEmpresa(listGridFon.get(i).getNoEmpresaDestino());
											generadorDto.setFolParam(iFolio);
											generadorDto.setFolMovi(iFolMovi);
											generadorDto.setFolDeta(iFolDeta);
											generadorDto.setResult(0);
											generadorDto.setMensajeResp("inicia generador");

											resGenerador = coinversionDao.ejecutarGenerador(generadorDto);

											if (funciones.convertirCadenaInteger(
													resGenerador.get("result").toString()) != 0) {
												mapRetFon.put("msgUsuario", "Error en Generador en btnEjecutar "
														+ resGenerador.get("result"));
												break;
											} else {
												if (listGridFon.get(i).getSaldoCoinversion() > 0)
													uImporte = uImporte + uSaldoCoinversion;
												else
													uImporte = uImporte + 0;

												sFolio += resGenerador.get("folDeta").toString() + ",";
											}
										} else {
											dtoInsParAut = new ParametroDto();
											dtoInsParAut.setNoEmpresa(dtoBus.getIdEmpresa());
											dtoInsParAut.setNoFolioParam(iFolioParametro);
											dtoInsParAut.setAplica(1);
											dtoInsParAut.setSecuencia(1);
											dtoInsParAut.setIdTipoOperacion(3808);
											dtoInsParAut.setCuenta(
													coinversionDao.consultarCuentaEmpresa(dtoBus.getIdEmpresa()));
											dtoInsParAut.setIdEstatusMov("L");
											dtoInsParAut.setIdChequera(dtoBus.getIdChequera());
											dtoInsParAut.setIdBanco(dtoBus.getIdBanco());
											dtoInsParAut.setImporte(listGridFon.get(i).getImporteTraspaso());
											dtoInsParAut.setBSBC("S");
											dtoInsParAut.setFecValor(globalSingleton.getFechaHoy());
											dtoInsParAut.setFecValorOriginal(globalSingleton.getFechaHoy());
											dtoInsParAut.setIdEstatusReg("P");
											dtoInsParAut
													.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
											dtoInsParAut.setFecValorAlta(globalSingleton.getFechaHoy());
											dtoInsParAut.setIdDivisa(listGridFon.get(i).getIdDivisa());
											dtoInsParAut.setIdFormaPago(3);
											dtoInsParAut.setImporteOriginal(listGridFon.get(i).getImporteTraspaso());
											dtoInsParAut.setFecOperacion(globalSingleton.getFechaHoy());
											dtoInsParAut.setIdBancoBenef(listGridFon.get(i).getIdBanco());
											dtoInsParAut.setIdChequeraBenef(listGridFon.get(i).getIdChequera());// Verificar
																												// por
																												// ue
																												// en
																												// el
																												// grid
																												// tiene
																												// benef
																												// pero
																												// no
																												// se
																												// asigna
																												// valor
																												// a
																												// el
											dtoInsParAut.setOrigenMov("SET");
											dtoInsParAut.setConcepto("TRASPASO CREDITO POR PRESTAMO");
											dtoInsParAut.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
											dtoInsParAut.setNoFolioMov(0);
											dtoInsParAut.setFolioRef(0);
											dtoInsParAut.setIdGrupo(iFolioGrupo);
											dtoInsParAut.setNoDocto(iNoDocto);
											dtoInsParAut.setNoCliente(listGridFon.get(i).getNoEmpresaDestino());
											dtoInsParAut.setBeneficiario(listGridFon.get(i).getNomEmpresaDestino());
											dtoInsParAut.setValorTasa(0);
											dtoInsParAut.setDiasInv(0);

											iAfectados = 0;
											iAfectados = coinversionDao.insertarParAutPres(dtoInsParAut);

											dtoInsParAut = new ParametroDto();
											dtoInsParAut.setNoEmpresa(listGridFon.get(i).getNoEmpresaDestino());
											dtoInsParAut.setNoFolioParam(obtenerFolioReal("no_folio_param"));
											dtoInsParAut.setAplica(1);
											dtoInsParAut.setSecuencia(2);
											dtoInsParAut.setIdTipoOperacion(3808);
											dtoInsParAut.setCuenta(coinversionDao
													.consultarCuentaEmpresa(listGridFon.get(i).getNoEmpresaDestino()));
											dtoInsParAut.setIdEstatusMov("L");
											dtoInsParAut.setIdChequera(listGridFon.get(i).getIdChequera());// aki
																											// es
																											// chequeraBenef
																											// �?
											dtoInsParAut.setIdBanco(listGridFon.get(i).getIdBanco());
											dtoInsParAut.setImporte(listGridFon.get(i).getImporteTraspaso());
											dtoInsParAut.setBSBC("S");
											dtoInsParAut.setFecValor(globalSingleton.getFechaHoy());
											dtoInsParAut.setFecValorOriginal(globalSingleton.getFechaHoy());
											dtoInsParAut.setIdEstatusReg("P");
											dtoInsParAut
													.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
											dtoInsParAut.setFecValorAlta(globalSingleton.getFechaHoy());
											dtoInsParAut.setIdDivisa(listGridFon.get(i).getIdDivisa());
											dtoInsParAut.setIdFormaPago(3);
											dtoInsParAut.setImporteOriginal(listGridFon.get(i).getImporteTraspaso());
											dtoInsParAut.setFecOperacion(globalSingleton.getFechaHoy());
											dtoInsParAut.setIdBancoBenef(listGridFon.get(i).getIdBanco());
											dtoInsParAut.setIdChequeraBenef(listGridFon.get(i).getIdChequera());// Verificar
																												// por
																												// ue
																												// en
																												// el
																												// grid
																												// tiene
																												// benef
																												// pero
																												// no
																												// se
																												// asigna
																												// valor
																												// a
																												// el
											dtoInsParAut.setOrigenMov("SET");
											dtoInsParAut.setConcepto("TRASPASO CREDITO POR PRESTAMO");
											dtoInsParAut.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
											dtoInsParAut.setNoFolioMov(1);
											dtoInsParAut.setFolioRef(1);
											dtoInsParAut.setIdGrupo(iFolioGrupo);
											dtoInsParAut.setNoDocto(iNoDocto);
											dtoInsParAut.setNoCliente(listGridFon.get(i).getNoEmpresaDestino());
											dtoInsParAut.setBeneficiario(listGridFon.get(i).getNomEmpresaDestino());
											dtoInsParAut.setValorTasa(0);
											dtoInsParAut.setDiasInv(0);

											iAfectados = 0;
											iAfectados = coinversionDao.insertarParAutPres(dtoInsParAut);

											generadorDto = new GeneradorDto();
											generadorDto
													.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
											generadorDto.setNomForma("FondeoAutomatico");
											generadorDto.setEmpresa(dtoBus.getIdEmpresa());
											generadorDto.setFolParam(iFolioParametro);
											generadorDto.setFolMovi(iFolMovi);
											generadorDto.setFolDeta(iFolDeta);
											generadorDto.setResult(0);
											generadorDto.setMensajeResp("inicia generador");

											resGenerador = coinversionDao.ejecutarGenerador(generadorDto);

											if (funciones.convertirCadenaInteger(
													resGenerador.get("result").toString()) != 0) {
												mapRetFon.put("msgUsuario", "Error en Generador en btnEjecutar "
														+ resGenerador.get("result"));
												break;
											} else {
												if (listGridFon.get(i).getSaldoCoinversion() > 0)
													uImporte = uImporte + listGridFon.get(i).getSaldoCoinversion();
												else
													uImporte = uImporte + 0;
												/*
												 * If pb_SolCredAutorizada =
												 * False Then Revisar aqui que
												 * folio se debe guardar
												 * ps_folio = ps_folio +
												 * Str(pd_FolioDet) + "," Else
												 * ps_folio = ps_folio + Str(a2)
												 * + "," End If
												 */
												sFolio += resGenerador.get("folDeta").toString() + ",";
											}
										}
									}
								} else {
									listGridFon.get(i).setRechazado("S");
									bRechazado = true;
								}
							} // end if montoMinimoVisible
						} // verificar movimientos
						else {
							if (dtoBus.isBMontoMinFondeo()) {
								if (uSaldoChequera >= dtoBus.getMontoMinFondeo() && dtoBus.getMontoMinFondeo() > 0) {
									if (uSaldoChequera >= uImporteTraspaso
											&& (uSaldoCoinversion
													- (uTraspasosR - uImporteTraspaso)) >= uImporteTraspaso
											&& uImporteTraspaso >= dtoBus.getMontoMinFondeo()) {
										iFolio = obtenerFolioReal("no_folio_param");

										dtoInsert = new ParametroDto();

										dtoInsert.setNoEmpresa(dtoBus.getIdEmpresa());
										dtoInsert.setNoFolioParam(iFolio);
										dtoInsert.setAplica(1);
										dtoInsert.setSecuencia(1);
										dtoInsert.setIdTipoOperacion(3806);
										dtoInsert.setCuenta(listGridFon.get(i).getNoEmpresaDestino());
										dtoInsert.setIdEstatusMov("L");
										dtoInsert.setIdChequera(dtoBus.getIdChequera());
										dtoInsert.setIdBanco(dtoBus.getIdBanco());
										dtoInsert.setImporte(uImporteTraspaso);
										dtoInsert.setBSBC("S");
										dtoInsert.setFecValor(globalSingleton.getFechaHoy());
										dtoInsert.setIdEstatusReg("P");
										dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
										dtoInsert.setIdDivisa(listGridFon.get(i).getIdDivisa());
										dtoInsert.setIdFormaPago(3);
										dtoInsert.setIdBancoBenef(listGridFon.get(i).getIdBanco());
										dtoInsert.setIdChequeraBenef(listGridFon.get(i).getIdChequera());// Verificar
										dtoInsert.setOrigenMov("SET");
										dtoInsert.setConcepto(listGridFon.get(i).getConcepto());
										dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
										dtoInsert.setNoFolioMov(0);
										dtoInsert.setFolioRef(0);
										dtoInsert.setIdGrupo(iFolio);
										dtoInsert.setNoCliente(listGridFon.get(i).getNoEmpresaDestino());
										dtoInsert.setBeneficiario(listGridFon.get(i).getNomEmpresaDestino());

										iAfectados = 0;
										iAfectados = coinversionDao.insertarParametro(dtoInsert);

										dtoInsert = new ParametroDto();
										dtoInsert.setNoEmpresa(listGridFon.get(i).getNoEmpresaDestino());
										dtoInsert.setNoFolioParam(obtenerFolioReal("no_folio_param"));
										dtoInsert.setAplica(1);
										dtoInsert.setSecuencia(2);
										dtoInsert.setIdTipoOperacion(3806);
										dtoInsert.setCuenta(coinversionDao
												.consultarCuentaEmpresa(listGridFon.get(i).getNoEmpresaDestino()));
										dtoInsert.setIdEstatusMov("L");
										dtoInsert.setIdChequera(listGridFon.get(i).getIdChequera());// aki
																									// es
																									// chequeraBenef
																									// �?
										dtoInsert.setIdBanco(listGridFon.get(i).getIdBanco());
										dtoInsert.setImporte(uImporteTraspaso);
										dtoInsert.setBSBC("S");
										dtoInsert.setFecValor(globalSingleton.getFechaHoy());
										dtoInsert.setIdEstatusReg("P");
										dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
										dtoInsert.setIdDivisa(listGridFon.get(i).getIdDivisa());
										dtoInsert.setIdFormaPago(3);
										dtoInsert.setIdBancoBenef(dtoBus.getIdBanco());
										dtoInsert.setIdChequeraBenef(dtoBus.getIdChequera());// Verificar
										dtoInsert.setOrigenMov("SET");
										dtoInsert.setConcepto(listGridFon.get(i).getConcepto());
										dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
										dtoInsert.setNoFolioMov(1);
										dtoInsert.setFolioRef(1);
										dtoInsert.setIdGrupo(iFolio);
										dtoInsert.setNoCliente(dtoBus.getIdEmpresa());
										dtoInsert.setBeneficiario(listGridFon.get(i).getNomEmpresaDestino());

										iAfectados = coinversionDao.insertarParametro(dtoInsert);

										generadorDto = new GeneradorDto();
										generadorDto.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
										generadorDto.setNomForma("FondeoAutomatico");
										generadorDto.setEmpresa(dtoBus.getIdEmpresa());
										generadorDto.setFolParam(iFolio);
										generadorDto.setFolMovi(iFolMovi);
										generadorDto.setFolDeta(iFolDeta);
										generadorDto.setResult(0);
										generadorDto.setMensajeResp("inicia generador");

										resGenerador = coinversionDao.ejecutarGenerador(generadorDto);
										if (funciones
												.convertirCadenaInteger(resGenerador.get("result").toString()) != 0) {
											mapRetFon.put("msgUsuario",
													"Error en Generador en btnEjecutar " + resGenerador.get("result"));
											break;
										} else {
											uSaldoChequera = uSaldoChequera - uImporteTraspaso;
											if (uImporteTraspaso > 0)
												uImporte = uImporte + listGridFon.get(i).getImporteTraspaso();
											else
												uImporte = uImporte + 0;

											sFolio += resGenerador.get("folDeta").toString() + ",";
										}
									} else if (uSaldoChequera < uImporteTraspaso
											&& (uSaldoCoinversion - (uTraspasosR - uImporteTraspaso)) >= uSaldoChequera
											&& uSaldoChequera >= dtoBus.getMontoMinFondeo()) {
										iFolio = obtenerFolioReal("no_folio_param");

										dtoInsert = new ParametroDto();

										dtoInsert.setNoEmpresa(dtoBus.getIdEmpresa());
										dtoInsert.setNoFolioParam(iFolio);
										dtoInsert.setAplica(1);
										dtoInsert.setSecuencia(1);
										dtoInsert.setIdTipoOperacion(3806);
										dtoInsert.setCuenta(listGridFon.get(i).getNoEmpresaDestino());
										dtoInsert.setIdEstatusMov("L");
										dtoInsert.setIdChequera(dtoBus.getIdChequera());
										dtoInsert.setIdBanco(dtoBus.getIdBanco());
										dtoInsert.setImporte(uSaldoChequera);
										dtoInsert.setBSBC("S");
										dtoInsert.setFecValor(globalSingleton.getFechaHoy());
										dtoInsert.setIdEstatusReg("P");
										dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
										dtoInsert.setIdDivisa(listGridFon.get(i).getIdDivisa());
										dtoInsert.setIdFormaPago(3);
										dtoInsert.setIdBancoBenef(listGridFon.get(i).getIdBanco());
										dtoInsert.setIdChequeraBenef(listGridFon.get(i).getIdChequera());// Verificar
										dtoInsert.setOrigenMov("SET");
										dtoInsert.setConcepto(listGridFon.get(i).getConcepto());
										dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
										dtoInsert.setNoFolioMov(0);
										dtoInsert.setFolioRef(0);
										dtoInsert.setIdGrupo(iFolio);
										dtoInsert.setNoCliente(listGridFon.get(i).getNoEmpresaDestino());
										dtoInsert.setBeneficiario(listGridFon.get(i).getNomEmpresaDestino());

										iAfectados = 0;
										iAfectados = coinversionDao.insertarParametro(dtoInsert);

										dtoInsert = new ParametroDto();
										dtoInsert.setNoEmpresa(listGridFon.get(i).getNoEmpresaDestino());
										dtoInsert.setNoFolioParam(obtenerFolioReal("no_folio_param"));
										dtoInsert.setAplica(1);
										dtoInsert.setSecuencia(2);
										dtoInsert.setIdTipoOperacion(3806);
										dtoInsert.setCuenta(coinversionDao
												.consultarCuentaEmpresa(listGridFon.get(i).getNoEmpresaDestino()));
										dtoInsert.setIdEstatusMov("L");
										dtoInsert.setIdChequera(listGridFon.get(i).getIdChequera());// aki
																									// es
																									// chequeraBenef
																									// �?
										dtoInsert.setIdBanco(listGridFon.get(i).getIdBanco());
										dtoInsert.setImporte(uSaldoChequera);
										dtoInsert.setBSBC("S");
										dtoInsert.setFecValor(globalSingleton.getFechaHoy());
										dtoInsert.setIdEstatusReg("P");
										dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
										dtoInsert.setIdDivisa(listGridFon.get(i).getIdDivisa());
										dtoInsert.setIdFormaPago(3);
										dtoInsert.setIdBancoBenef(dtoBus.getIdBanco());
										dtoInsert.setIdChequeraBenef(dtoBus.getIdChequera());// Verificar
										dtoInsert.setOrigenMov("SET");
										dtoInsert.setConcepto(listGridFon.get(i).getConcepto());
										dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
										dtoInsert.setNoFolioMov(1);
										dtoInsert.setFolioRef(1);
										dtoInsert.setIdGrupo(iFolio);
										dtoInsert.setNoCliente(dtoBus.getIdEmpresa());
										dtoInsert.setBeneficiario(listGridFon.get(i).getNomEmpresaDestino());

										iAfectados = coinversionDao.insertarParametro(dtoInsert);

										generadorDto = new GeneradorDto();
										generadorDto.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
										generadorDto.setNomForma("FondeoAutomatico");
										generadorDto.setEmpresa(dtoBus.getIdEmpresa());
										generadorDto.setFolParam(iFolio);
										generadorDto.setFolMovi(iFolMovi);
										generadorDto.setFolDeta(iFolDeta);
										generadorDto.setResult(0);
										generadorDto.setMensajeResp("inicia generador");

										resGenerador = coinversionDao.ejecutarGenerador(generadorDto);
										if (funciones
												.convertirCadenaInteger(resGenerador.get("result").toString()) != 0) {
											mapRetFon.put("msgUsuario",
													"Error en Generador en btnEjecutar " + resGenerador.get("result"));
											break;
										} else {
											uSaldoChequera = uSaldoChequera - uImporteTraspaso;
											if (uImporteTraspaso > 0)
												uImporte = uImporte + listGridFon.get(i).getImporteTraspaso();
											else
												uImporte = uImporte + 0;

											sFolio += resGenerador.get("folDeta").toString() + ",";
										}
									} else if (uSaldoChequera > uImporteTraspaso
											&& (uSaldoCoinversion - (uTraspasosR - uImporteTraspaso)) < uSaldoChequera
											&& (uSaldoCoinversion - (uTraspasosR - uImporteTraspaso)) >= dtoBus
													.getMontoMinFondeo()) {
										iFolio = obtenerFolioReal("no_folio_param");

										dtoInsert = new ParametroDto();

										dtoInsert.setNoEmpresa(dtoBus.getIdEmpresa());
										dtoInsert.setNoFolioParam(iFolio);
										dtoInsert.setAplica(1);
										dtoInsert.setSecuencia(1);
										dtoInsert.setIdTipoOperacion(3806);
										dtoInsert.setCuenta(listGridFon.get(i).getNoEmpresaDestino());
										dtoInsert.setIdEstatusMov("L");
										dtoInsert.setIdChequera(dtoBus.getIdChequera());
										dtoInsert.setIdBanco(dtoBus.getIdBanco());
										dtoInsert.setImporte(uSaldoCoinversion - (uTraspasosR - uImporteTraspaso));
										dtoInsert.setBSBC("S");
										dtoInsert.setFecValor(globalSingleton.getFechaHoy());
										dtoInsert.setIdEstatusReg("P");
										dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
										dtoInsert.setIdDivisa(listGridFon.get(i).getIdDivisa());
										dtoInsert.setIdFormaPago(3);
										dtoInsert.setIdBancoBenef(listGridFon.get(i).getIdBanco());
										dtoInsert.setIdChequeraBenef(listGridFon.get(i).getIdChequera());// Verificar
										dtoInsert.setOrigenMov("SET");
										dtoInsert.setConcepto(listGridFon.get(i).getConcepto());
										dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
										dtoInsert.setNoFolioMov(0);
										dtoInsert.setFolioRef(0);
										dtoInsert.setIdGrupo(iFolio);
										dtoInsert.setNoCliente(listGridFon.get(i).getNoEmpresaDestino());
										dtoInsert.setBeneficiario(listGridFon.get(i).getNomEmpresaDestino());

										iAfectados = 0;
										iAfectados = coinversionDao.insertarParametro(dtoInsert);

										dtoInsert = new ParametroDto();
										dtoInsert.setNoEmpresa(listGridFon.get(i).getNoEmpresaDestino());
										dtoInsert.setNoFolioParam(obtenerFolioReal("no_folio_param"));
										dtoInsert.setAplica(1);
										dtoInsert.setSecuencia(2);
										dtoInsert.setIdTipoOperacion(3806);
										dtoInsert.setCuenta(coinversionDao
												.consultarCuentaEmpresa(listGridFon.get(i).getNoEmpresaDestino()));
										dtoInsert.setIdEstatusMov("L");
										dtoInsert.setIdChequera(listGridFon.get(i).getIdChequera());// aki
																									// es
																									// chequeraBenef
																									// �?
										dtoInsert.setIdBanco(listGridFon.get(i).getIdBanco());
										dtoInsert.setImporte(uSaldoCoinversion - (uTraspasosR - uImporteTraspaso));
										dtoInsert.setBSBC("S");
										dtoInsert.setFecValor(globalSingleton.getFechaHoy());
										dtoInsert.setIdEstatusReg("P");
										dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
										dtoInsert.setIdDivisa(listGridFon.get(i).getIdDivisa());
										dtoInsert.setIdFormaPago(3);
										dtoInsert.setIdBancoBenef(dtoBus.getIdBanco());
										dtoInsert.setIdChequeraBenef(dtoBus.getIdChequera());// Verificar
										dtoInsert.setOrigenMov("SET");
										dtoInsert.setConcepto(listGridFon.get(i).getConcepto());
										dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
										dtoInsert.setNoFolioMov(1);
										dtoInsert.setFolioRef(1);
										dtoInsert.setIdGrupo(iFolio);
										dtoInsert.setNoCliente(dtoBus.getIdEmpresa());
										dtoInsert.setBeneficiario(listGridFon.get(i).getNomEmpresaDestino());

										iAfectados = coinversionDao.insertarParametro(dtoInsert);

										generadorDto = new GeneradorDto();
										generadorDto.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
										generadorDto.setNomForma("FondeoAutomatico");
										generadorDto.setEmpresa(dtoBus.getIdEmpresa());
										generadorDto.setFolParam(iFolio);
										generadorDto.setFolMovi(iFolMovi);
										generadorDto.setFolDeta(iFolDeta);
										generadorDto.setResult(0);
										generadorDto.setMensajeResp("inicia generador");

										resGenerador = coinversionDao.ejecutarGenerador(generadorDto);
										if (funciones
												.convertirCadenaInteger(resGenerador.get("result").toString()) != 0) {
											mapRetFon.put("msgUsuario",
													"Error en Generador en btnEjecutar " + resGenerador.get("result"));
											break;
										} else {
											uSaldoChequera = uSaldoChequera - uImporteTraspaso;
											if (uImporteTraspaso > 0)
												uImporte = uImporte + listGridFon.get(i).getImporteTraspaso();
											else
												uImporte = uImporte + 0;

											sFolio += resGenerador.get("folDeta").toString() + ",";
										}
									} else {
										listGridFon.get(i).setRechazado("S");
										bRechazado = true;
									}
								} else {
									listGridFon.get(i).setRechazado("S");
									bRechazado = true;
								}
							} else {
								iFolio = obtenerFolioReal("no_folio_param");

								dtoInsert = new ParametroDto();

								dtoInsert.setNoEmpresa(dtoBus.getIdEmpresa());
								dtoInsert.setNoFolioParam(iFolio);
								dtoInsert.setAplica(1);
								dtoInsert.setSecuencia(1);
								dtoInsert.setIdTipoOperacion(3806);
								dtoInsert.setCuenta(listGridFon.get(i).getNoEmpresaDestino());
								dtoInsert.setIdEstatusMov("L");
								dtoInsert.setIdChequera(dtoBus.getIdChequera());
								dtoInsert.setIdBanco(dtoBus.getIdBanco());
								dtoInsert.setImporte(listGridFon.get(i).getImporteTraspaso());
								dtoInsert.setBSBC("S");
								dtoInsert.setFecValor(globalSingleton.getFechaHoy());
								dtoInsert.setIdEstatusReg("P");
								dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
								dtoInsert.setIdDivisa(listGridFon.get(i).getIdDivisa());
								dtoInsert.setIdFormaPago(3);
								dtoInsert.setIdBancoBenef(listGridFon.get(i).getIdBanco());
								dtoInsert.setIdChequeraBenef(listGridFon.get(i).getIdChequera());// Verificar
								dtoInsert.setOrigenMov("SET");
								dtoInsert.setConcepto(listGridFon.get(i).getConcepto());
								dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
								dtoInsert.setNoFolioMov(0);
								dtoInsert.setFolioRef(0);
								dtoInsert.setIdGrupo(iFolio);
								dtoInsert.setNoCliente(listGridFon.get(i).getNoEmpresaDestino());
								dtoInsert.setBeneficiario(listGridFon.get(i).getNomEmpresaDestino());

								iAfectados = 0;
								iAfectados = coinversionDao.insertarParametro(dtoInsert);

								dtoInsert = new ParametroDto();
								dtoInsert.setNoEmpresa(listGridFon.get(i).getNoEmpresaDestino());
								dtoInsert.setNoFolioParam(obtenerFolioReal("no_folio_param"));
								dtoInsert.setAplica(1);
								dtoInsert.setSecuencia(2);
								dtoInsert.setIdTipoOperacion(3806);
								dtoInsert.setCuenta(coinversionDao
										.consultarCuentaEmpresa(listGridFon.get(i).getNoEmpresaDestino()));
								dtoInsert.setIdEstatusMov("L");
								dtoInsert.setIdChequera(listGridFon.get(i).getIdChequera());// aki
																							// es
																							// chequeraBenef
																							// �?
								dtoInsert.setIdBanco(listGridFon.get(i).getIdBanco());
								dtoInsert.setImporte(listGridFon.get(i).getImporteTraspaso());
								dtoInsert.setBSBC("S");
								dtoInsert.setFecValor(globalSingleton.getFechaHoy());
								dtoInsert.setIdEstatusReg("P");
								dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
								dtoInsert.setIdDivisa(listGridFon.get(i).getIdDivisa());
								dtoInsert.setIdFormaPago(3);
								dtoInsert.setIdBancoBenef(dtoBus.getIdBanco());
								dtoInsert.setIdChequeraBenef(dtoBus.getIdChequera());// Verificar
								dtoInsert.setOrigenMov("SET");
								dtoInsert.setConcepto(listGridFon.get(i).getConcepto());
								dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
								dtoInsert.setNoFolioMov(1);
								dtoInsert.setFolioRef(1);
								dtoInsert.setIdGrupo(iFolio);
								dtoInsert.setNoCliente(dtoBus.getIdEmpresa());
								dtoInsert.setBeneficiario(listGridFon.get(i).getNomEmpresaDestino());

								iAfectados = coinversionDao.insertarParametro(dtoInsert);

								generadorDto = new GeneradorDto();
								generadorDto.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
								generadorDto.setNomForma("FondeoAutomatico");
								generadorDto.setEmpresa(dtoBus.getIdEmpresa());
								generadorDto.setFolParam(iFolio);
								generadorDto.setFolMovi(iFolMovi);
								generadorDto.setFolDeta(iFolDeta);
								generadorDto.setResult(0);
								generadorDto.setMensajeResp("inicia generador");

								resGenerador = coinversionDao.ejecutarGenerador(generadorDto);
								if (funciones.convertirCadenaInteger(resGenerador.get("result").toString()) != 0) {
									mapRetFon.put("msgUsuario",
											"Error en Generador en btnEjecutar " + resGenerador.get("result"));
									break;
								} else {
									uImporte = uImporte + listGridFon.get(i).getImporteTraspaso();
									sFolio += resGenerador.get("folDeta").toString() + ",";
								}
							}
						} // end checa mov
					} else// configura_set(264)' Valida sin tomar en cuenta el
							// saldo en coinversion
					{
						if (globalSingleton.obtenerValorConfiguraSet(252).equals("SI")) {
							if ((uImporteTraspaso >= dtoBus.getMontoMinFondeo() && dtoBus.getMontoMinFondeo() > 0
									&& dtoBus.isBMontoMinFondeo())
									|| (uImporteTraspaso > 0 && !dtoBus.isBMontoMinFondeo())
											&& uSaldoChequera >= uImporteTraspaso) {
								iFolio = obtenerFolioReal("no_folio_param");

								dtoInsert = new ParametroDto();

								dtoInsert.setNoEmpresa(dtoBus.getIdEmpresa());
								dtoInsert.setNoFolioParam(iFolio);
								dtoInsert.setAplica(1);
								dtoInsert.setSecuencia(1);
								dtoInsert.setIdTipoOperacion(3806);
								dtoInsert.setCuenta(listGridFon.get(i).getNoEmpresaDestino());
								dtoInsert.setIdEstatusMov("L");
								dtoInsert.setIdChequera(dtoBus.getIdChequera());
								dtoInsert.setIdBanco(dtoBus.getIdBanco());
								dtoInsert.setImporte(uImporteTraspaso);
								dtoInsert.setBSBC("S");
								dtoInsert.setFecValor(globalSingleton.getFechaHoy());
								dtoInsert.setIdEstatusReg("P");
								dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
								dtoInsert.setIdDivisa(listGridFon.get(i).getIdDivisa());
								dtoInsert.setIdFormaPago(3);
								dtoInsert.setIdBancoBenef(listGridFon.get(i).getIdBanco());
								dtoInsert.setIdChequeraBenef(listGridFon.get(i).getIdChequera());// Verificar
								dtoInsert.setOrigenMov("SET");
								dtoInsert.setConcepto(listGridFon.get(i).getConcepto());
								dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
								dtoInsert.setNoFolioMov(0);
								dtoInsert.setFolioRef(0);
								dtoInsert.setIdGrupo(iFolio);
								dtoInsert.setNoCliente(listGridFon.get(i).getNoEmpresaDestino());
								dtoInsert.setBeneficiario(listGridFon.get(i).getNomEmpresaDestino());

								iAfectados = 0;
								iAfectados = coinversionDao.insertarParametro(dtoInsert);

								dtoInsert = new ParametroDto();
								dtoInsert.setNoEmpresa(listGridFon.get(i).getNoEmpresaDestino());
								dtoInsert.setNoFolioParam(obtenerFolioReal("no_folio_param"));
								dtoInsert.setAplica(1);
								dtoInsert.setSecuencia(2);
								dtoInsert.setIdTipoOperacion(3806);
								dtoInsert.setCuenta(coinversionDao
										.consultarCuentaEmpresa(listGridFon.get(i).getNoEmpresaDestino()));
								dtoInsert.setIdEstatusMov("L");
								dtoInsert.setIdChequera(listGridFon.get(i).getIdChequera());// aki
																							// es
																							// chequeraBenef
																							// �?
								dtoInsert.setIdBanco(listGridFon.get(i).getIdBanco());
								dtoInsert.setImporte(uImporteTraspaso);
								dtoInsert.setBSBC("S");
								dtoInsert.setFecValor(globalSingleton.getFechaHoy());
								dtoInsert.setIdEstatusReg("P");
								dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
								dtoInsert.setIdDivisa(listGridFon.get(i).getIdDivisa());
								dtoInsert.setIdFormaPago(3);
								dtoInsert.setIdBancoBenef(dtoBus.getIdBanco());
								dtoInsert.setIdChequeraBenef(dtoBus.getIdChequera());// Verificar
								dtoInsert.setOrigenMov("SET");
								dtoInsert.setConcepto(listGridFon.get(i).getConcepto());
								dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
								dtoInsert.setNoFolioMov(1);
								dtoInsert.setFolioRef(1);
								dtoInsert.setIdGrupo(iFolio);
								dtoInsert.setNoCliente(dtoBus.getIdEmpresa());
								dtoInsert.setBeneficiario(listGridFon.get(i).getNomEmpresaDestino());

								iAfectados = coinversionDao.insertarParametro(dtoInsert);

								generadorDto = new GeneradorDto();
								generadorDto.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
								generadorDto.setNomForma("FondeoAutomatico");
								generadorDto.setEmpresa(dtoBus.getIdEmpresa());
								generadorDto.setFolParam(iFolio);
								generadorDto.setFolMovi(iFolMovi);
								generadorDto.setFolDeta(iFolDeta);
								generadorDto.setResult(0);
								generadorDto.setMensajeResp("inicia generador");

								resGenerador = coinversionDao.ejecutarGenerador(generadorDto);
								if (funciones.convertirCadenaInteger(resGenerador.get("result").toString()) != 0) {
									mapRetFon.put("msgUsuario",
											"Error en Generador en btnEjecutar " + resGenerador.get("result"));
									break;
								} else {
									uSaldoChequera = uSaldoChequera - uImporteTraspaso;
									if (uImporteTraspaso > 0)
										uImporte = uImporte + uImporteTraspaso;
									else
										uImporte = uImporte + 0;

									sFolio += resGenerador.get("folDeta").toString() + ",";
								}
							} else if (((uImporteTraspaso >= dtoBus.getMontoMinFondeo()
									&& dtoBus.getMontoMinFondeo() > 0 && dtoBus.isBMontoMinFondeo()
									&& uSaldoChequera > dtoBus.getMontoMinFondeo())
									|| (uImporteTraspaso > 0 && !dtoBus.isBMontoMinFondeo()))
									&& uSaldoChequera < uImporteTraspaso) {
								iFolio = obtenerFolioReal("no_folio_param");

								dtoInsert = new ParametroDto();

								dtoInsert.setNoEmpresa(dtoBus.getIdEmpresa());
								dtoInsert.setNoFolioParam(iFolio);
								dtoInsert.setAplica(1);
								dtoInsert.setSecuencia(1);
								dtoInsert.setIdTipoOperacion(3806);
								dtoInsert.setCuenta(listGridFon.get(i).getNoEmpresaDestino());
								dtoInsert.setIdEstatusMov("L");
								dtoInsert.setIdChequera(dtoBus.getIdChequera());
								dtoInsert.setIdBanco(dtoBus.getIdBanco());
								dtoInsert.setImporte(uSaldoChequera);
								dtoInsert.setBSBC("S");
								dtoInsert.setFecValor(globalSingleton.getFechaHoy());
								dtoInsert.setIdEstatusReg("P");
								dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
								dtoInsert.setIdDivisa(listGridFon.get(i).getIdDivisa());
								dtoInsert.setIdFormaPago(3);
								dtoInsert.setIdBancoBenef(listGridFon.get(i).getIdBanco());
								dtoInsert.setIdChequeraBenef(listGridFon.get(i).getIdChequera());// Verificar
								dtoInsert.setOrigenMov("SET");
								dtoInsert.setConcepto(listGridFon.get(i).getConcepto());
								dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
								dtoInsert.setNoFolioMov(0);
								dtoInsert.setFolioRef(0);
								dtoInsert.setIdGrupo(iFolio);
								dtoInsert.setNoCliente(listGridFon.get(i).getNoEmpresaDestino());
								dtoInsert.setBeneficiario(listGridFon.get(i).getNomEmpresaDestino());

								iAfectados = 0;
								iAfectados = coinversionDao.insertarParametro(dtoInsert);

								dtoInsert = new ParametroDto();
								dtoInsert.setNoEmpresa(listGridFon.get(i).getNoEmpresaDestino());
								dtoInsert.setNoFolioParam(obtenerFolioReal("no_folio_param"));
								dtoInsert.setAplica(1);
								dtoInsert.setSecuencia(2);
								dtoInsert.setIdTipoOperacion(3806);
								dtoInsert.setCuenta(coinversionDao
										.consultarCuentaEmpresa(listGridFon.get(i).getNoEmpresaDestino()));
								dtoInsert.setIdEstatusMov("L");
								dtoInsert.setIdChequera(listGridFon.get(i).getIdChequera());// aki
																							// es
																							// chequeraBenef
																							// �?
								dtoInsert.setIdBanco(listGridFon.get(i).getIdBanco());
								dtoInsert.setImporte(uSaldoChequera);
								dtoInsert.setBSBC("S");
								dtoInsert.setFecValor(globalSingleton.getFechaHoy());
								dtoInsert.setIdEstatusReg("P");
								dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
								dtoInsert.setIdDivisa(listGridFon.get(i).getIdDivisa());
								dtoInsert.setIdFormaPago(3);
								dtoInsert.setIdBancoBenef(dtoBus.getIdBanco());
								dtoInsert.setIdChequeraBenef(dtoBus.getIdChequera());// Verificar
								dtoInsert.setOrigenMov("SET");
								dtoInsert.setConcepto(listGridFon.get(i).getConcepto());
								dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
								dtoInsert.setNoFolioMov(1);
								dtoInsert.setFolioRef(1);
								dtoInsert.setIdGrupo(iFolio);
								dtoInsert.setNoCliente(dtoBus.getIdEmpresa());
								dtoInsert.setBeneficiario(listGridFon.get(i).getNomEmpresaDestino());

								iAfectados = coinversionDao.insertarParametro(dtoInsert);

								generadorDto = new GeneradorDto();
								generadorDto.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
								generadorDto.setNomForma("FondeoAutomatico");
								generadorDto.setEmpresa(dtoBus.getIdEmpresa());
								generadorDto.setFolParam(iFolio);
								generadorDto.setFolMovi(iFolMovi);
								generadorDto.setFolDeta(iFolDeta);
								generadorDto.setResult(0);
								generadorDto.setMensajeResp("inicia generador");

								resGenerador = coinversionDao.ejecutarGenerador(generadorDto);
								if (funciones.convertirCadenaInteger(resGenerador.get("result").toString()) != 0) {
									mapRetFon.put("msgUsuario",
											"Error en Generador en btnEjecutar " + resGenerador.get("result"));
									break;
								} else {
									if (uSaldoChequera > 0)
										uImporte = uImporte + uSaldoChequera;
									else
										uImporte = uImporte + 0;

									sFolio += resGenerador.get("folDeta").toString() + ",";
								}
							} else {
								listGridFon.get(i).setRechazado("S");
								bRechazado = true;
							}
						} else {
							if ((uImporteTraspaso >= dtoBus.getMontoMinFondeo() && dtoBus.getMontoMinFondeo() > 0
									&& dtoBus.isBMontoMinFondeo())
									|| (uImporteTraspaso > 0 && !dtoBus.isBMontoMinFondeo())) {
								iFolio = obtenerFolioReal("no_folio_param");

								dtoInsert = new ParametroDto();

								dtoInsert.setNoEmpresa(dtoBus.getIdEmpresa());
								dtoInsert.setNoFolioParam(iFolio);
								dtoInsert.setAplica(1);
								dtoInsert.setSecuencia(1);
								dtoInsert.setIdTipoOperacion(3806);
								dtoInsert.setCuenta(listGridFon.get(i).getNoEmpresaDestino());
								dtoInsert.setIdEstatusMov("L");
								dtoInsert.setIdChequera(dtoBus.getIdChequera());
								dtoInsert.setIdBanco(dtoBus.getIdBanco());
								dtoInsert.setImporte(uImporteTraspaso);
								dtoInsert.setBSBC("S");
								dtoInsert.setFecValor(globalSingleton.getFechaHoy());
								dtoInsert.setIdEstatusReg("P");
								dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
								dtoInsert.setIdDivisa(listGridFon.get(i).getIdDivisa());
								dtoInsert.setIdFormaPago(3);
								dtoInsert.setIdBancoBenef(listGridFon.get(i).getIdBanco());
								dtoInsert.setIdChequeraBenef(listGridFon.get(i).getIdChequera());// Verificar
								dtoInsert.setOrigenMov("SET");
								dtoInsert.setConcepto(listGridFon.get(i).getConcepto());
								dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
								dtoInsert.setNoFolioMov(0);
								dtoInsert.setFolioRef(0);
								dtoInsert.setIdGrupo(iFolio);
								dtoInsert.setNoCliente(listGridFon.get(i).getNoEmpresaDestino());
								dtoInsert.setBeneficiario(listGridFon.get(i).getNomEmpresaDestino());

								iAfectados = 0;
								iAfectados = coinversionDao.insertarParametro(dtoInsert);

								dtoInsert = new ParametroDto();
								dtoInsert.setNoEmpresa(listGridFon.get(i).getNoEmpresaDestino());
								dtoInsert.setNoFolioParam(obtenerFolioReal("no_folio_param"));
								dtoInsert.setAplica(1);
								dtoInsert.setSecuencia(2);
								dtoInsert.setIdTipoOperacion(3806);
								dtoInsert.setCuenta(coinversionDao
										.consultarCuentaEmpresa(listGridFon.get(i).getNoEmpresaDestino()));
								dtoInsert.setIdEstatusMov("L");
								dtoInsert.setIdChequera(listGridFon.get(i).getIdChequera());// aki
																							// es
																							// chequeraBenef
																							// �?
								dtoInsert.setIdBanco(listGridFon.get(i).getIdBanco());
								dtoInsert.setImporte(uSaldoChequera);
								dtoInsert.setBSBC("S");
								dtoInsert.setFecValor(globalSingleton.getFechaHoy());
								dtoInsert.setIdEstatusReg("P");
								dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
								dtoInsert.setIdDivisa(listGridFon.get(i).getIdDivisa());
								dtoInsert.setIdFormaPago(3);
								dtoInsert.setIdBancoBenef(dtoBus.getIdBanco());
								dtoInsert.setIdChequeraBenef(dtoBus.getIdChequera());// Verificar
								dtoInsert.setOrigenMov("SET");
								dtoInsert.setConcepto(listGridFon.get(i).getConcepto());
								dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
								dtoInsert.setNoFolioMov(1);
								dtoInsert.setFolioRef(1);
								dtoInsert.setIdGrupo(iFolio);
								dtoInsert.setNoCliente(dtoBus.getIdEmpresa());
								dtoInsert.setBeneficiario(listGridFon.get(i).getNomEmpresaDestino());

								iAfectados = coinversionDao.insertarParametro(dtoInsert);

								generadorDto = new GeneradorDto();
								generadorDto.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
								generadorDto.setNomForma("FondeoAutomatico");
								generadorDto.setEmpresa(dtoBus.getIdEmpresa());
								generadorDto.setFolParam(iFolio);
								generadorDto.setFolMovi(iFolMovi);
								generadorDto.setFolDeta(iFolDeta);
								generadorDto.setResult(0);
								generadorDto.setMensajeResp("inicia generador");

								resGenerador = coinversionDao.ejecutarGenerador(generadorDto);
								if (funciones.convertirCadenaInteger(resGenerador.get("result").toString()) != 0) {
									mapRetFon.put("msgUsuario",
											"Error en Generador en btnEjecutar " + resGenerador.get("result"));
									break;
								} else {
									uSaldoChequera = uSaldoChequera - uImporteTraspaso;
									if (uImporteTraspaso > 0)
										uImporte = uImporte + uImporteTraspaso;
									else
										uImporte = uImporte + 0;

									sFolio += resGenerador.get("folDeta").toString() + ",";
								}
							} else {
								listGridFon.get(i).setRechazado("S");
								bRechazado = true;
							}
						} // fin configura 252
					} // fin configura 264
				} // fin ciclo
			} else// else idEmpresaRaiz 'Hace fondeos para arboles
			{

				logger.info("Entra else hace fondeo arboles");
				listRevFond = new ArrayList<Map<String, Object>>();
				listRevFond = coinversionDao.consultarRevisionFondeo();
				if (listRevFond.size() > 0) {
					for (int i = 0; i < listRevFond.size(); i++) {
						listRevisa = new ArrayList<CatCtaBancoDto>();
						// Revisa chequera padre
						listRevisa = coinversionDao.consultarRevisaChequera2(
								funciones.convertirCadenaInteger(listRevFond.get(0).get("no_padre").toString()),
								iIdBanco, dtoBus.getIdDivisa());
						if (listRevisa.size() <= 0) {
							bSinChequera = false;
							mapRetFon.put("msgUsuario", "La empresa " + listRevFond.get(0).get("no_padre").toString()
									+ " No tiene chequera predeterminada de traspaso o del banco seleccionado o divisa seleccionada");
							return mapRetFon;

						}
						// Revisa chequera hijo
						listRevisa = new ArrayList<CatCtaBancoDto>();
						listRevisa = coinversionDao.consultarRevisaChequera2(
								funciones.convertirCadenaInteger(listRevFond.get(0).get("no_hijo").toString()),
								iIdBanco, dtoBus.getIdDivisa());

						if (listRevisa.size() <= 0) {
							bSinChequera = false;
							mapRetFon.put("msgUsuario", "La empresa " + listRevFond.get(0).get("no_hijo").toString()
									+ " No tiene chequera predeterminada de traspaso o del banco seleccionado o divisa seleccionada");
							return mapRetFon;
						}
					}
				} else {
					mapRetFon.put("msgUsuario",
							"La empresa " + dtoBus.getIdEmpresa() + " No tiene chequera predeterminada de traspasos");
					return mapRetFon;
				}

				listArbolTrans = new ArrayList<Map<String, Object>>();
				listArbolTrans = coinversionDao.consutarTmpArbolTrasp();

				String sNoFolioDet = "";
				String sBandera = "";
				if (listArbolTrans.size() > 0) {
					for (int c = 0; c < listGridFon.size(); c++) {
						listRevisaPagos = new ArrayList<ControlFondeoChequesDto>();
						listRevisaPagos = coinversionDao.consultarRevisionPagos(listGridFon.get(c).getIdBanco(),
								listGridFon.get(c).getIdChequera());
						if (listRevisaPagos.size() > 0) {
							for (int d = 0; d < listRevisaPagos.size(); d++) {
								resPagador = new HashMap<String, Object>();
								dtoPagador = new StoreParamsComunDto();
								sNoFolioDet += listRevisaPagos.get(d).getNoFolioDet() + ",#";
								sBandera = "A";// Este valor esta asi por
												// default Checar
								dtoPagador.setParametro(
										sBandera + "," + 0 + "," + globalSingleton.getUsuarioLoginDto().getIdUsuario()
												+ "," + listGridFon.get(d).getIdBanco() + ","
												+ listGridFon.get(d).getIdChequera() + "," + sNoFolioDet);
								dtoPagador.setMensaje("iniciar pagador");
								dtoPagador.setResult(0);
								// ejecuta pagador
								resPagador = coinversionDao.ejecutarPagador(dtoPagador);
								if (funciones.convertirCadenaInteger(resGenerador.get("result").toString()) != 0)
									mapRetFon.put("mensajes", "Error en Pagador" + resPagador.get("result"));
								else
									coinversionDao.eliminarPagosCheques(listRevisaPagos.get(d).getNoFolioDet(),
											listGridFon.get(d).getIdBanco(), listGridFon.get(d).getIdChequera());
							}
						}
					}
					for (int e = 0; e < listArbolTrans.size(); e++) {
						listRevisa = new ArrayList<CatCtaBancoDto>();
						iFolio = obtenerFolioReal("no_folio_param");
						listRevisa = coinversionDao.consultarRevisaChequera2(
								funciones.convertirCadenaInteger(listArbolTrans.get(e).get("empOrigen").toString()),
								iIdBanco, dtoBus.getIdDivisa());
						logger.info("Hola 1");
						if (listRevisa.size() > 0) {
							iIdBancoSalida = listRevisa.get(0).getIdBanco();
							sIdChequeraSalida = listRevisa.get(0).getIdChequera();
						} else {
							bSinChequera = false;
							mapRetFon.put("msgUsuario", "La empresa " + listArbolTrans.get(e).get("empOrigen")
									+ " No tiene chequera predeterminada de traspaso o del banco seleccionado o divisa seleccionada");
							return mapRetFon;
						}
						logger.info("Hola 2");
						if (funciones.convertirCadenaInteger(listArbolTrans.get(e).get("idBanco").toString()) == 0) {
							listRevisa = new ArrayList<CatCtaBancoDto>();
							listRevisa = coinversionDao.consultarRevisaChequera2(
									funciones
											.convertirCadenaInteger(listArbolTrans.get(e).get("empDestino").toString()),
									iIdBanco, dtoBus.getIdDivisa());
							if (listRevisa.size() > 0) {
								iIdBancoEntrada = listRevisa.get(0).getIdBanco();
								sIdChequeraEntrada = listRevisa.get(0).getIdChequera();
							} else {
								bSinChequera = false;
								mapRetFon.put("msgUsuario", "La empresa " + listArbolTrans.get(e).get("empDestino")
										+ " No tiene chequera predeterminada de traspaso");
								return mapRetFon;
							}
						} else {
							iIdBancoEntrada = funciones
									.convertirCadenaInteger(listArbolTrans.get(e).get("idBanco").toString());
							sIdChequeraEntrada = listArbolTrans.get(e).get("idChequera").toString();
						}
						// saca tipo operacion chequera
						listTipoOper = new ArrayList<ArbolEmpresaFondeoDto>();
						listTipoOper = coinversionDao.consultarTipoOperacion(
								funciones.convertirCadenaInteger(listArbolTrans.get(e).get("empOrigen").toString()),
								funciones.convertirCadenaInteger(listArbolTrans.get(e).get("empDestino").toString()),
								funciones.convertirCadenaInteger(listArbolTrans.get(e).get("tipoArbol").toString()),
								funciones.convertirCadenaInteger(listArbolTrans.get(e).get("orden").toString()));
						if (listTipoOper.size() > 0) {
							iTipoOperacion = listTipoOper.get(0).getTipoOperacion();
							sDescTipoOper = listTipoOper.get(0).getDescTipoOperacion();
						} else {
							bSinChequera = false;
							mapRetFon.put("msgUsuario", "La empresa " + listArbolTrans.get(e).get("empDestino")
									+ " No tiene chequera predeterminada de traspaso");
							return mapRetFon;
						}
						if (iTipoOperacion == 3806)
							iNoCuenta = funciones
									.convertirCadenaInteger(listArbolTrans.get(e).get("empOrigen").toString());
						else
							iNoCuenta = coinversionDao.consultarCuentaEmpresa(funciones
									.convertirCadenaInteger(listArbolTrans.get(e).get("empOrigen").toString()));

						dtoInsert = new ParametroDto();
						dtoInsert.setNoEmpresa(
								funciones.convertirCadenaInteger(listArbolTrans.get(e).get("empOrigen").toString()));
						dtoInsert.setNoFolioParam(iFolio);
						dtoInsert.setAplica(1);
						dtoInsert.setSecuencia(1);
						dtoInsert.setIdTipoOperacion(iTipoOperacion);
						dtoInsert.setCuenta(iNoCuenta);
						dtoInsert.setIdEstatusMov("L");
						dtoInsert.setIdChequera(sIdChequeraSalida);
						dtoInsert.setIdBanco(iIdBancoSalida);
						dtoInsert.setImporte(funciones
								.convertirCadenaDouble(listArbolTrans.get(e).get("importeTraspaso").toString()));
						dtoInsert.setBSBC("S");
						dtoInsert.setFecValor(globalSingleton.getFechaHoy());
						dtoInsert.setIdEstatusReg("P");
						dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
						dtoInsert.setIdDivisa(dtoBus.getIdDivisa());
						dtoInsert.setNoFactura(dtoBus.getIdEmpresaRaiz());
						dtoInsert.setIdFormaPago(3);
						dtoInsert.setIdBancoBenef(iIdBancoEntrada);
						dtoInsert.setIdChequeraBenef(sIdChequeraEntrada);
						dtoInsert.setOrigenMov("SET");
						dtoInsert.setConcepto(listArbolTrans.get(e).get("orden").toString() + ":" + sDescTipoOper);
						dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
						dtoInsert.setNoFolioMov(0);
						dtoInsert.setFolioRef(0);
						dtoInsert.setIdGrupo(iFolio);
						dtoInsert.setNoCliente(
								funciones.convertirCadenaInteger(listArbolTrans.get(e).get("empDestino").toString()));
						dtoInsert.setBeneficiario(listArbolTrans.get(e).get("nomEmpresa").toString());

						iAfectados = 0;
						iAfectados = coinversionDao.insertarParametro(dtoInsert);

						dtoInsert = new ParametroDto();
						dtoInsert.setNoEmpresa(
								funciones.convertirCadenaInteger(listArbolTrans.get(e).get("empDestino").toString()));
						dtoInsert.setNoFolioParam(obtenerFolioReal("no_folio_param"));
						dtoInsert.setAplica(1);
						dtoInsert.setSecuencia(2);
						dtoInsert.setIdTipoOperacion(iTipoOperacion);
						dtoInsert.setCuenta(coinversionDao.consultarCuentaEmpresa(
								funciones.convertirCadenaInteger(listArbolTrans.get(e).get("empDestino").toString())));
						dtoInsert.setIdEstatusMov("L");
						dtoInsert.setIdChequera(sIdChequeraEntrada);
						dtoInsert.setIdBanco(iIdBancoEntrada);
						dtoInsert.setImporte(funciones
								.convertirCadenaDouble(listArbolTrans.get(e).get("importeTraspaso").toString()));
						dtoInsert.setBSBC("S");
						dtoInsert.setFecValor(globalSingleton.getFechaHoy());
						dtoInsert.setIdEstatusReg("P");
						dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
						dtoInsert.setIdDivisa(dtoBus.getIdDivisa());
						dtoInsert.setNoFactura(dtoBus.getIdEmpresaRaiz());
						dtoInsert.setIdFormaPago(3);
						dtoInsert.setIdBancoBenef(iIdBancoSalida);
						dtoInsert.setIdChequeraBenef(sIdChequeraSalida);
						dtoInsert.setOrigenMov("SET");
						dtoInsert.setConcepto(listArbolTrans.get(e).get("orden").toString() + ":" + sDescTipoOper);
						dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
						dtoInsert.setNoFolioMov(1);
						dtoInsert.setFolioRef(1);
						dtoInsert.setIdGrupo(iFolio);
						dtoInsert.setNoCliente(
								funciones.convertirCadenaInteger(listArbolTrans.get(e).get("empDestino").toString()));
						dtoInsert.setBeneficiario(listArbolTrans.get(e).get("nomEmpresa").toString());

						iAfectados = coinversionDao.insertarParametro(dtoInsert);

						generadorDto = new GeneradorDto();
						generadorDto.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
						generadorDto.setNomForma("FondeoAutomatico");
						generadorDto.setEmpresa(
								funciones.convertirCadenaInteger(listArbolTrans.get(e).get("empOrigen").toString()));
						generadorDto.setFolParam(iFolio);
						generadorDto.setFolMovi(iFolMovi);
						generadorDto.setFolDeta(iFolDeta);
						generadorDto.setResult(0);
						generadorDto.setMensajeResp("inicia generador");
						resGenerador = new HashMap<String, Object>();
						resGenerador = coinversionDao.ejecutarGenerador(generadorDto);

						if (funciones.convertirCadenaInteger(resGenerador.get("result").toString()) != 0) {
							mapRetFon.put("msgUsuario",
									"Error en Generador en btnEjecutar " + resGenerador.get("result"));
							return mapRetFon;
						}
					}
				}
			}

			if (!sFolio.equals("")) {
				mapRetFon.put("msgUsuario", "Datos registrados correctamente");
				sFolio = sFolio.substring(0, sFolio.length() - 1);
			}

			if (dtoBus.getIdEmpresaRaiz() <= 0) {
				if (bRechazado)
					mensajes.add("No se realiz� ninguna solicitud de fondeo");
				else if (bFondeo)
					mensajes.add("No se realiz� ninguna solicitud de fondeo");
			}
			mapRetFon.put("mensajes", mensajes);

		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:realizarFondeoAutomatico");
			e.printStackTrace();
		}
		return mapRetFon;
	}

	/**
	 * Function saca_saldo_chequera(pl_empresa As Long, ByRef ps_chequera As
	 * String, pl_Banco As Long, pbSobregiro As Boolean) As Double
	 * 
	 * @param iIdEmpresa
	 * @param sIdChequera
	 * @param iIdBanco
	 * @param bsobregiro
	 * @return
	 */
	public double sacarSaldoChequera(int iIdEmpresa, String sIdChequera, int iIdBanco, boolean bSobregiro) {
		double uSaldoChequera = 0;
		List<CatCtaBancoDto> listCons = new ArrayList<CatCtaBancoDto>();

		try {
			listCons = coinversionDao.consultarSaldoChequera(iIdEmpresa, sIdChequera, iIdBanco);
			if (listCons.size() > 0) {
				if (bSobregiro)
					uSaldoChequera = listCons.get(0).getSaldoFinal() + listCons.get(0).getSobregiro();
				else
					uSaldoChequera = listCons.get(0).getSaldoFinal();
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:sacarSaldoChequera");
		}
		return uSaldoChequera;
	}

	public boolean verificarMovimientosNeg(int iIdEmpresaCon, int iIdEmpresaF, double uImporte, String sIdDivisa) {
		double uImporteMov = 0;
		double uSuma = 0;
		boolean verMovNeg = false;
		try {
			if (globalSingleton.obtenerValorConfiguraSet(264).equals("SI")) {
				uImporteMov = coinversionDao.consultarImporteMovimientos(iIdEmpresaCon, iIdEmpresaF, sIdDivisa);
				if (uImporteMov > 0) {
					uSuma = uImporteMov - uImporte;
					if (uSuma >= 0)
						verMovNeg = false;
					else
						verMovNeg = true;
				}
			} else
				verMovNeg = true;

		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:verificarMovimientosNeg");
		}
		return verMovNeg;
	}

	public int obtenerFolioReal(String tipoFolio) {
		int folio = 0;
		try {
			coinversionDao.actualizarFolioReal(tipoFolio);
			folio = coinversionDao.seleccionarFolioReal(tipoFolio);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:obtenerFolioReal");
			return 0;
		}
		return folio;
	}

	public Map<String, Object> sacarParentesco(int iIdEmpresa) {
		List<ArbolEmpresaDto> listParen = new ArrayList<ArbolEmpresaDto>();
		Map<String, Object> mapRet = new HashMap<String, Object>();
		try {
			listParen = coinversionDao.consultarParentesco(iIdEmpresa);
			if (listParen.size() > 0) {
				if (listParen.get(0).getChosno4() > 0)
					if (iIdEmpresa == listParen.get(0).getChosno4())
						iIdEmpresa = listParen.get(0).getChosno3();
					else if (listParen.get(0).getChosno3() > 0)
						if (iIdEmpresa == listParen.get(0).getChosno3())
							iIdEmpresa = listParen.get(0).getChosno2();
						else if (listParen.get(0).getChosno2() > 0)
							if (iIdEmpresa == listParen.get(0).getChosno2())
								iIdEmpresa = listParen.get(0).getChosno();
							else if (listParen.get(0).getChosno() > 0)
								if (iIdEmpresa == listParen.get(0).getChosno())
									iIdEmpresa = listParen.get(0).getTataranieto();
								else if (listParen.get(0).getTataranieto() > 0)
									if (iIdEmpresa == listParen.get(0).getTataranieto())
										iIdEmpresa = listParen.get(0).getBisnieto();
									else if (listParen.get(0).getBisnieto() > 0)
										if (iIdEmpresa == listParen.get(0).getBisnieto())
											iIdEmpresa = listParen.get(0).getNieto();
										else if (listParen.get(0).getNieto() > 0)
											if (iIdEmpresa == listParen.get(0).getNieto())
												iIdEmpresa = listParen.get(0).getHijo();
											else if (listParen.get(0).getHijo() > 0)
												if (iIdEmpresa == listParen.get(0).getHijo())
													iIdEmpresa = listParen.get(0).getPadre();
												else if (listParen.get(0).getPadre() > 0)
													if (iIdEmpresa == listParen.get(0).getPadre())
														iIdEmpresa = listParen.get(0).getEmpresaRaiz();
													else
														iIdEmpresa = 0;
			} else {
				mapRet.put("msgUsuario", "La empresa " + iIdEmpresa + " no esta dentro de la jerarqu�a de empresas");
			}
			mapRet.put("empresa", iIdEmpresa);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:sacarParentesco");
		}
		return mapRet;
	}

	public List<MovimientoDto> obtenerPagos(ParamBusquedaFondeoDto dtoBus) {
		List<MovimientoDto> listCons = new ArrayList<MovimientoDto>();
		try {
			listCons = coinversionDao.consultarPagos(dtoBus);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:consultarPagos");
		}
		return listCons;
	}

	public List<ParamRetornoFondeoAutDto> obtenerDesglosePagos(ParamBusquedaFondeoDto dtoBus) {
		List<ParamRetornoFondeoAutDto> listCons = new ArrayList<ParamRetornoFondeoAutDto>();
		try {
			listCons = coinversionDao.consultarDesglosePagos(dtoBus);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:obtenerDesglosePagos");
		}
		return listCons;
	}

	public List<MovimientoDto> obtenerFondeoCheques(String sIdChequera, int iIdBanco, String sVencimiento) {
		List<MovimientoDto> listCons = new ArrayList<MovimientoDto>();
		List<MovimientoDto> listVencHoy = new ArrayList<MovimientoDto>();
		List<MovimientoDto> listVencAnt = new ArrayList<MovimientoDto>();
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			listCons = coinversionDao.consultarFondeoCheques(sIdChequera, iIdBanco);

			if (listCons.size() > 0) {
				for (int i = 0; i < listCons.size(); i++) {
					if ((listCons.get(i).getFecValor().compareTo(globalSingleton.getFechaHoy())) >= 0) {
						if (listCons.get(i).getFolioFondeo() <= 0)// Condici�n
																	// para
																	// agregar
																	// si aun no
																	// han sido
																	// fondeados
							listVencHoy.add(listCons.get(i));
					} else
						listVencAnt.add(listCons.get(i));
				}
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:obtenerFondeoCheques");
		}

		if (sVencimiento != null && sVencimiento.equals("H"))
			return listVencHoy;
		else
			return listVencAnt;

	}

	public Map<String, Object> ejecutarFondeoCheques(List<MovimientoDto> listVencHoy, List<MovimientoDto> listVencAnt,
			int iIdBanco, String sIdChequera) {
		Map<String, Object> mapRet = new HashMap<String, Object>();
		List<ControlFondeoChequesDto> listRevCheques = new ArrayList<ControlFondeoChequesDto>();
		try {
			if (listVencHoy.size() > 0) {
				for (int i = 0; i < listVencHoy.size(); i++) {
					if (listVencHoy.get(i).isSeleccionado()) {
						listRevCheques = coinversionDao.consultarRevisionCheques(listVencHoy.get(i).getNoFolioDet(),
								iIdBanco, sIdChequera);
						if (listRevCheques.size() > 0) {
							// 'Call gobjSQL.DELETEFondeoCheques(ls_folios,
							// Val(txtBanco.Text), Trim(tctCuenta.Text))
						} else {
							if (listVencHoy.get(i).getNoFolioDet() > 0)
								coinversionDao.insertarControlFondeoCheques(listVencHoy.get(i).getNoFolioDet(),
										iIdBanco, sIdChequera);
						}
					} else {
						listRevCheques = coinversionDao.consultarRevisionCheques(listVencHoy.get(i).getNoFolioDet(),
								iIdBanco, sIdChequera);
						if (listRevCheques.size() > 0)
							coinversionDao.eliminarControlFondeoCheques(listVencHoy.get(i).getNoFolioDet(), iIdBanco,
									sIdChequera);
					}
				}
			}

			if (listVencAnt.size() > 0) {
				for (int c = 0; c < listVencAnt.size(); c++) {
					if (listVencAnt.get(c).isSeleccionado()) {
						listRevCheques = coinversionDao.consultarRevisionCheques(listVencAnt.get(c).getNoFolioDet(),
								iIdBanco, sIdChequera);
						if (listRevCheques.size() > 0) {
							// 'Call gobjSQL.DELETEFondeoCheques(ls_folios,
							// Val(txtBanco.Text), Trim(tctCuenta.Text))
						} else {
							if (listVencAnt.get(c).getNoFolioDet() > 0)
								coinversionDao.insertarControlFondeoCheques(listVencAnt.get(c).getNoFolioDet(),
										iIdBanco, sIdChequera);
						}
					} else {
						listRevCheques = coinversionDao.consultarRevisionCheques(listVencAnt.get(c).getNoFolioDet(),
								iIdBanco, sIdChequera);
						if (listRevCheques.size() > 0)
							coinversionDao.eliminarControlFondeoCheques(listVencAnt.get(c).getNoFolioDet(), iIdBanco,
									sIdChequera);
					}
				}
			}
			mapRet.put("msgUsuario", "Datos registrados correctamente");
		} catch (Exception e) {
			mapRet.put("msgUsuario", "Ocurrio un error ");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:ejecutarFondeoCheques");
		}
		return mapRet;
	}

	/** Mantenimiento de coinversionistas **/
	/**
	 * divisas asignadas a la empresa seleccionada
	 */
	public List<DivisasEncontradasDto> consultarDivisasFilialesEncontradas(int iEmpresa) {
		List<DivisasEncontradasDto> list = new ArrayList<DivisasEncontradasDto>();
		try {
			list = coinversionDao.consultarDivisasFilialesEncontradas(iEmpresa);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:consultarDivisasFilialesEncontradas");
		}
		return list;
	}

	/**
	 * divisas que se pueden agregar a la empresa seleccionada
	 */
	public List<DivisasEncontradasDto> consultarDivisasFilialesNoEncontradas(int iEmpresa) {
		List<DivisasEncontradasDto> list = new ArrayList<DivisasEncontradasDto>();
		try {
			list = coinversionDao.consultarDivisasFilialesNoEncontradas(iEmpresa);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:consultarDivisasFilialesNoEncontradas");
		}
		return list;
	}

	public Map<String, Object> ejecutarAltaFilial(EmpresaDto dtoEmpresas, List<DivisasEncontradasDto> listDivisas,
			int concentradora) {
		Map<String, Object> mapRetorno = new HashMap<String, Object>();

		try {
			int tCuentaEmp = 0;
			int tLineaEmp = 0;
			int tLineaEmp2 = 0;
			int iFolio = 0;
			int iError = 0;
			int iAfectados = 0;
			int iRegAfectados = 0;
			String tDescCta = "";
			globalSingleton = GlobalSingleton.getInstancia();
			List<String> mensajes = new ArrayList<String>();

			for (int iContador = 0; iContador < listDivisas.size(); iContador++) {
				/*
				 * 'Validar que la empresa sea diferente a la coinversora
				 * seleccionada 'ya que ahora las coinversoras tambien pueden
				 * tener controladora
				 */

				// iRegMarcados = iRegMarcados + 1 = iCOntador
				tCuentaEmp = dtoEmpresas.getNoEmpresa();
				tLineaEmp = this.obtenerLinea(listDivisas.get(iContador).getIdDivisa());
				if (tLineaEmp < 0)
					mensajes.add("No se pudo obtener la linea de la empresa!");
				// mapRetorno.put("msgUsuario", "No se pudo obtener la linea de
				// la empresa!");
				tDescCta = dtoEmpresas.getNomEmpresa();

				if (concentradora != tCuentaEmp) {
					iFolio = coinversionDao.seleccionarFolioReal(sFolioCuenta);
					if (iFolio == 0) {
						iError = -200;
						return mapRetorno;
					}

					iAfectados = 0;
					iAfectados = coinversionDao.actualizarEmpresa(concentradora, tCuentaEmp);

					// inserta saldos
					tLineaEmp2 = this.obtenerLinea(listDivisas.get(iContador).getIdDivisa());
					if (tLineaEmp2 < 0)
						mensajes.add("No se pudo obtener la linea de la empresa!");
					// mapRetorno.put("msgUsuario", "No se pudo obtener la linea
					// de la empresa!");
					coinversionDao.insertaSaldos(concentradora, tLineaEmp2, dtoEmpresas.getNoEmpresa(), 5);
					coinversionDao.insertaSaldos(concentradora, tLineaEmp2, dtoEmpresas.getNoEmpresa(), 7);
					coinversionDao.insertaSaldos(concentradora, tLineaEmp2, dtoEmpresas.getNoEmpresa(), 90);
					coinversionDao.insertaSaldos(concentradora, tLineaEmp2, dtoEmpresas.getNoEmpresa(), 91);

					iAfectados = 0;
					System.out.println("Global singleton" + globalSingleton.getUsuarioLoginDto().getIdUsuario());
					System.out.println("Concentradora" + concentradora);
					iAfectados = coinversionDao.insertaFilial(concentradora, sNumProducto, concentradora,
							globalSingleton.getUsuarioLoginDto().getIdUsuario(), tLineaEmp, tCuentaEmp,
							globalSingleton.getFechaHoy(), sIdTipoCuenta, tDescCta,
							listDivisas.get(iContador).getIdDivisa());
					if (iAfectados > 0) {
						iRegAfectados = iRegAfectados + 1;
					}
				} else {
					mensajes.add("La empresa " + concentradora + " no puede tenerse a si misma como controladora");
					// mapRetorno.put("msgUsuario", "La empresa " +
					// concentradora + " no puede tenerse a si misma como
					// controladora");
				}
			} // end for
			if (listDivisas.size() > iRegAfectados)
				mensajes.add(
						"Se afectaron " + iRegAfectados + " de " + listDivisas.size() + " Registro(s) Seleccionado(s)");
			// mapRetorno.put("msgUsuario", "Se afectaron " + iRegAfectados + "
			// de " + listDivisas.size() + " Registro(s) Seleccionado(s)");
			else
				mensajes.add(iRegAfectados + " Registro(s) Afectado(s)");
			// mapRetorno.put("msgUsuario", iRegAfectados + " Registro(s)
			// Afectado(s)");
			mapRetorno.put("msgUsuario", mensajes);
			return mapRetorno;
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:ejecutarAltaFilial");
		}
		return mapRetorno;
	}

	public Map<String, Object> eliminarFilial(EmpresaDto dtoEmpresas, List<DivisasEncontradasDto> listDivisas,
			int concentradora) {
		Map<String, Object> mapRetorno = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();

		try {
			int iNumFilas = 0;
			int tLineaEmp = 0;
			int iRegMarcados = 0;
			int tCuentaEmp = 0;
			int iAfectados = 0;
			int iRegAfectados = 0;

			iNumFilas = listDivisas.size();

			for (int iContador = 0; iContador < iNumFilas; iContador++) {
				if (iContador > iNumFilas)
					break;

				tLineaEmp = this.obtenerLinea(listDivisas.get(iContador).getIdDivisa());
				if (tLineaEmp < 0)
					mensajes.add("No se pudo obtener la linea de la empresa!");

				if (!this.checaMovs(concentradora, dtoEmpresas.getNoEmpresa(), tLineaEmp)) {
					mensajes.add("La empresa " + dtoEmpresas.getNoEmpresa()
							+ " no puede ser eliminada porque tiene movimientos pendientes");
					break;
				}

				List<SaldoDto> saldoFiliales = new ArrayList<SaldoDto>();
				saldoFiliales = coinversionDao.consultarSaldoFiliales(concentradora, dtoEmpresas.getNoEmpresa(),
						tLineaEmp);
				if (saldoFiliales.size() <= 0) {
					if (saldoFiliales.get(0).getImporte() != 0) {
						mensajes.add("La empresa " + dtoEmpresas.getNoEmpresa()
								+ " no puede ser eliminada porque tiene saldo final = "
								+ funciones.obtenerFormatoPesos(saldoFiliales.get(0).getImporte()));
						saldoFiliales.clear();
						break;
					}
				}

				saldoFiliales.clear();

				iRegMarcados = iRegMarcados + 1;

				tCuentaEmp = dtoEmpresas.getNoEmpresa();

				iAfectados = 0;
				iAfectados = coinversionDao.actualizarControladora(tCuentaEmp);

				iAfectados = 0;
				iAfectados = coinversionDao.eliminarSaldo(concentradora, tCuentaEmp, tLineaEmp);

				iAfectados = 0;
				iAfectados = coinversionDao.eliminarCuenta(concentradora, tCuentaEmp, tLineaEmp);

				if (iAfectados > 0) {
					iRegAfectados = iRegAfectados + 1;
				}
			}

			if (iRegMarcados > iRegAfectados)
				mensajes.add("Se eliminaron " + iRegAfectados + " de " + iRegMarcados + " Registro(s) Seleccionado(s)");
			else
				mensajes.add(iRegAfectados + " Registro(s) Eliminado(s)");

			mapRetorno.put("msgUsuario", mensajes);
			return mapRetorno;

		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:eliminarFilial");
		}
		return mapRetorno;
	}

	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteCoinversion(Map parameters) {
		JRMapArrayDataSource jrDataSource = null;
		XStream xStream = new XStream(new DomDriver());
		List<Map<String, Object>> resMap = null;
		int iUsuario = 0;
		int iEmpresa = 0;
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			iUsuario = Integer.parseInt(parameters.get("usuario").toString());
			iEmpresa = Integer.parseInt(parameters.get("empresa").toString());
			resMap = coinversionDao.consultarReporteCoinversoras(iEmpresa, iUsuario);

			xStream.alias("mapa cheque", java.util.List.class);
			jrDataSource = new JRMapArrayDataSource(resMap.toArray());

		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:obtenerReporteCoinversion");
		}
		return jrDataSource;
	}

	public List<Map<String, Object>> consultarReporteCoinversion(int plEmpresa, int plUsuario) {
		List<Map<String, Object>> mapRet = null;
		try {
			mapRet = coinversionDao.consultarReporteCoinversoras(plEmpresa, plUsuario);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:consultarReporteCoinversion");
		}
		return mapRet;
	}

	public int obtenerLinea(String sIdDivisa) {
		int linea = 0;
		String sDivisaSoin = "";
		try {
			sDivisaSoin = coinversionDao.consultarIdDivisaSoin(sIdDivisa);
			if (sDivisaSoin != null && !sDivisaSoin.equals(""))
				linea = Integer.parseInt(sDivisaSoin.trim());
			else
				linea = -1;
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:obtenerLinea");
		}
		return linea;
	}

	public boolean checaMovs(int iNoEmpresa, int iNoCuenta, int iNoLinea) {
		int iMovs = -1;
		try {

			iMovs = coinversionDao.consultaMovimientosInversoras(iNoEmpresa, iNoCuenta, iNoLinea);
			if (iMovs <= 0)
				return true;
			else
				return false;
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:checaMovs");
			return false;
		}
	}

	// barrido automatico por saldos
	public List<LlenaComboGralDto> llenarComboBancosConcentradora(boolean bConcentradora, int iIdEmpresa,
			String idDivisa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			list = coinversionDao.consultarBancosConcentradora(bConcentradora, iIdEmpresa, idDivisa);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:llenarComboBancosConcentradora");
		}
		return list;
	}

	public List<LlenaComboChequeraDto> llenarComboChequerasBarrido(boolean bPagadora, int iBanco, int iNoEmpresa) {
		List<LlenaComboChequeraDto> list = new ArrayList<LlenaComboChequeraDto>();
		try {
			list = coinversionDao.consultarChequerasBarrido(bPagadora, iBanco, iNoEmpresa);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:llenarComboChequerasBarrido");
		}
		return list;
	}

	public List<BarridoChequerasDto> consultarBarridoChequerasPorSaldo(int iConcentradora, String sDivisa,
			String sBancos) {
		List<BarridoChequerasDto> listBarrido = new ArrayList<BarridoChequerasDto>();
		String strBancos = "";
		try {
			String[] bancos = sBancos.split(",");
			for (int i = 0; i < bancos.length; i++) {
				if (bancos[i].equals("BANAMEX"))
					strBancos = strBancos + ConstantesSet.BANAMEX + ",";

				if (bancos[i].equals("BANCOMER"))
					strBancos = strBancos + ConstantesSet.BANCOMER + ",";

				if (bancos[i].equals("CITIBANK"))
					strBancos = strBancos + ConstantesSet.CITIBANK_DLS + ",";

				if (bancos[i].equals("COMERICA"))
					strBancos = strBancos + ConstantesSet.COMERICA + ",";

				if (bancos[i].equals("SANTANDER"))
					strBancos = strBancos + ConstantesSet.SANTANDER + ",";

				if (bancos[i].equals("AZTECA"))
					strBancos = strBancos + ConstantesSet.AZTECA + ",";
			}

			sBancos = strBancos.substring(0, strBancos.length() - 1);
			listBarrido = coinversionDao.consultarBarridoChequerasPorSaldo(iConcentradora, sDivisa, sBancos);

			for (int i = 0; i < listBarrido.size(); i++) {
				listBarrido.get(i).setTraspaso(listBarrido.get(i).getDiferencia() - listBarrido.get(i).getSaldoMinimo());
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:consultarBarridoChequerasPorSaldo");
		}
		return listBarrido;
	}

	public Map<String, Object> importarBancos(String sBancos) {
		Map<String, Object> mapRetorno = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		String sRutaRegreso = "";
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			// Emiminar los registros de fechas anteriores a hoy
			coinversionDao.eliminarSaldosChequeras(globalSingleton.getFechaHoy());
			sRutaRegreso = globalSingleton.obtenerValorConfiguraSet(201);
			String[] bancos = sBancos.split(",");

			for (int i = 0; i < bancos.length; i++) {
				if (bancos[i].equals("BANAMEX")) {
					mensajes = importarSaldosChequerasBusiness.importarSaldosBanamex(sRutaRegreso);
				}

				if (bancos[i].equals("BANCOMER")) {
					mensajes = importarSaldosChequerasBusiness.importarSaldosBancomer(sRutaRegreso);
				}

				if (bancos[i].equals("AZTECA")) {
					mensajes = importarSaldosChequerasBusiness.importarSaldosAzteca(sRutaRegreso);
				}

				if (bancos[i].equals("SANTANDER")) {
					mensajes = importarSaldosChequerasBusiness.importarSaldosSantander(sRutaRegreso);
				}
			}
			/*
			 * If chkCitibank.Value = 1 Then If importar_saldos_CITIBANK = False
			 * Then Exit Sub End If End If If chkComerica.Value = 1 Then If
			 * Importar_saldos_COMERICA = False Then Exit Sub End If End If If
			 * chkSantander.Value = 1 Then If importar_saldos_SANTANDER = False
			 * Then Exit Sub End If End If
			 */
			mapRetorno.put("msgUsuario", mensajes);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:importarBancos");
		}
		return mapRetorno;
	}

	/**
	 * Metodo que ejecuta el barrido por saldos de las chequeras seleccionadas
	 * 
	 * @param listBarrido
	 * @param dtoParams
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> ejecutarBarrido(List<BarridoChequerasDto> listBarrido,
			ParamsBusquedaBarridoDto dtoParams) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		Map<String, Object> resGenerador = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		globalSingleton = GlobalSingleton.getInstancia();
		ParametroDto dtoInsert;
		GeneradorDto generadorDto;

		int iRenglon;
		int iFolio = 0;
		int iFolio2;
		int iBanco = 0;
		int iConcentradora = 0;
		int iAfectados;
		int iEmpresaGen = 0;
		int iFolioMov = 0;
		int iFolioDet = 0;
		int iFolioParametro = 0;
		double dSumaNo;
		double dSuma;
		double dDeudaEmpresa;
		double dDiferencia = 0;
		double dMontoASobregirar = 0;
		double dTotalNeteo = 0;
		boolean bDeudaTerminada;
		boolean bEjMovimiento;
		boolean bEjecutoNeteo = false;
		String sChequera = "";
		String sChequeraEmp;
		String sChequeraNo = "";
		String sChequeraNoEmp;
		String sSecuencias;
		String sFecha = "";
		try {

			sChequeraEmp = "";
			sChequeraNoEmp = "";
			dSumaNo = 0;
			dSuma = 0;
			

			for (iRenglon = 0; iRenglon < listBarrido.size(); iRenglon++) {
				iFolio2 = 0;

				bEjMovimiento = false;
				sSecuencias = "";
				dDeudaEmpresa = 0;
				bDeudaTerminada = false;

				if (dDeudaEmpresa == 0 && !bDeudaTerminada) {
					if (listBarrido.get(iRenglon).getSaldoCredito() == 0){
						dDeudaEmpresa = 0;
					}else{
						dDeudaEmpresa = listBarrido.get(iRenglon).getSaldoCredito();
				
					}
				}
				iBanco = listBarrido.get(iRenglon).getIdBanco();
				dDiferencia = 0;

				if (listBarrido.get(iRenglon).getTraspaso() > 0){
					dDiferencia = listBarrido.get(iRenglon).getTraspaso();
				}else{
				dDiferencia = 0;

				}
  				bEjMovimiento = false;

				if ((((iBanco != dtoParams.getIdBanco() && dDiferencia >= 50000 && dtoParams.getIdDivisa().equals("MN"))
						|| iBanco == dtoParams.getIdBanco()) && dDiferencia >= 0) || dtoParams.isOpcFondeo()
						|| dtoParams.isOpcSobregiro()) {

					iConcentradora = dtoParams.getNoEmpresa();
					//sFecha = "25/09/2017"; 
					sFecha =funciones.ponerFormatoDate(globalSingleton.getFechaHoy());
                    System.out.print("Fecha:"+sFecha);

					if (/*dtoParams.isOpcFondeo() && */((listBarrido.get(iRenglon).getSaldoChequera() < 0 || listBarrido.get(iRenglon).getTraspaso() < 0))) {

						/*
						 * ' Aqui se fondea el saldo de la chequera mas el saldo
						 * minimo ' que en teoria ya esta en el grid en el
						 * importe del traspaso ' lo traemos en diferencia
						 */
						iFolio = this.obtenerFolioReal("no_folio_param");
						if (iFolio <= 0) {
							mensajes.add("No se encontro el folio: no_folio_param");
							mapResult.put("msgUsuario", mensajes);
							return mapResult;
						}

						if (iFolio2 == 0)
							iFolio2 = iFolio;

						if (!globalSingleton.obtenerValorConfiguraSet(1).equals("CCM")){
							dDiferencia = Math.abs(dDiferencia);
						}else{
							dDiferencia = Math.abs(listBarrido.get(iRenglon).getTraspaso());
						}
						dtoInsert = new ParametroDto();
 
						dtoInsert.setNoEmpresa(iConcentradora);
						dtoInsert.setNoFolioParam(iFolio);
						dtoInsert.setAplica(1);
						dtoInsert.setSecuencia(1);
						dtoInsert.setIdTipoOperacion(3806);
						dtoInsert.setCuenta(listBarrido.get(iRenglon).getNoEmpresa());
						dtoInsert.setIdEstatusMov("L");
						dtoInsert.setIdChequera(dtoParams.getChequera());
						dtoInsert.setIdBanco(dtoParams.getIdBanco());
						dtoInsert.setImporte(dDiferencia);
						dtoInsert.setBSBC("S");
						dtoInsert.setFecValor(globalSingleton.getFechaHoy());
						dtoInsert.setIdEstatusReg("P");
						dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
						dtoInsert.setIdDivisa(dtoParams.getIdDivisa());
						dtoInsert.setIdFormaPago(3);
						dtoInsert.setIdBancoBenef(iBanco);
						dtoInsert.setIdChequeraBenef(listBarrido.get(iRenglon).getIdChequera());
						dtoInsert.setOrigenMov("SET");
						dtoInsert.setConcepto("FONDEO POR BARRIDO DE SALDOS");
						dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
						dtoInsert.setNoFolioMov(0);
						dtoInsert.setFolioRef(0);
						dtoInsert.setIdGrupo(iFolio2);
						dtoInsert.setNoCliente(listBarrido.get(iRenglon).getNoEmpresa());
						dtoInsert.setBeneficiario(listBarrido.get(iRenglon).getNomEmpresa());

						iAfectados = 0;
						iAfectados = coinversionDao.insertarParametro(dtoInsert);

						dtoInsert = new ParametroDto();

						dtoInsert.setNoEmpresa(listBarrido.get(iRenglon).getNoEmpresa());
						dtoInsert.setNoFolioParam(this.obtenerFolioReal("no_folio_param"));
						dtoInsert.setAplica(1);
						dtoInsert.setSecuencia(2);
						dtoInsert.setIdTipoOperacion(3806);
						dtoInsert.setCuenta(
								coinversionDao.consultarCuentaEmpresa(listBarrido.get(iRenglon).getNoEmpresa()));
						dtoInsert.setIdEstatusMov("L");
						dtoInsert.setIdChequera(listBarrido.get(iRenglon).getIdChequera());
						dtoInsert.setIdBanco(iBanco);
						dtoInsert.setImporte(dDiferencia);
						dtoInsert.setBSBC("S");
						dtoInsert.setFecValor(globalSingleton.getFechaHoy());
						dtoInsert.setIdEstatusReg("P");
						dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
						dtoInsert.setIdDivisa(dtoParams.getIdDivisa());
						dtoInsert.setIdFormaPago(3);
						dtoInsert.setIdBancoBenef(dtoParams.getIdBanco());
						dtoInsert.setIdChequeraBenef(dtoParams.getChequera());
						dtoInsert.setOrigenMov("SET");
						dtoInsert.setConcepto("FONDEO POR BARRIDO DE SALDOS");
						dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
						dtoInsert.setNoFolioMov(1);
						dtoInsert.setFolioRef(1);
						dtoInsert.setIdGrupo(iFolio2);
						dtoInsert.setNoCliente(iConcentradora);
						dtoInsert.setBeneficiario(dtoParams.getNomEmpresa());

						iAfectados = 0;
						iAfectados = coinversionDao.insertarParametro(dtoInsert);

						iEmpresaGen = iConcentradora;
						bEjMovimiento = true;

					} else{ // si no se indica fondear 
					
						if (listBarrido.get(iRenglon).getSaldoChequera() > 0 && dDiferencia > 0 && dtoParams.isOpcSobregiro() == false) {

							if (listBarrido.get(iRenglon).getMontoSobregiro() > 0){
								dMontoASobregirar = listBarrido.get(iRenglon).getMontoSobregiro();
							}else{
								dMontoASobregirar = -1;
							}
							dTotalNeteo = 0;
							bEjecutoNeteo = false;
							String valida = globalSingleton.obtenerValorConfiguraSet(262);
							if (valida.equals("SI") && dDeudaEmpresa >= 0) {
								// si se tiene credito, se pagan
								dTotalNeteo = dDiferencia - listBarrido.get(iRenglon).getSaldoCredito();
								iFolioParametro = this.obtenerFolioReal("no_folio_param");
								iAfectados = 0;

								if (dTotalNeteo > 0){
									dTotalNeteo = listBarrido.get(iRenglon).getSaldoCredito(); // se
																								// cubre
																								// todo
																								// el
																								// credito
								}else{
									dTotalNeteo = dDiferencia; // se cubre solo
																// una parte
								}
								// si se tiene algo que pagar se genera si no,
								// no
								if (dTotalNeteo > 0) {
									iFolio = this.obtenerFolioReal("no_folio_param");
									if (iFolio <= 0) {
										mensajes.add("No se encontro el folio: nofolio_param");
										mapResult.put("msgUsuario", mensajes);
										return mapResult;
									}

									if (iFolio2 == 0)
										iFolio2 = iFolio;

									dtoInsert = new ParametroDto();

									dtoInsert.setNoEmpresa(listBarrido.get(iRenglon).getNoEmpresa());
									dtoInsert.setNoFolioParam(iFolio);
									dtoInsert.setAplica(1);
									dtoInsert.setSecuencia(1);
									dtoInsert.setIdTipoOperacion(3809);
									dtoInsert.setCuenta(coinversionDao.consultarCuentaEmpresa(listBarrido.get(iRenglon).getNoEmpresa()));
									dtoInsert.setIdEstatusMov("L");
									dtoInsert.setIdChequera(listBarrido.get(iRenglon).getIdChequera());
									dtoInsert.setIdBanco(iBanco);
									dtoInsert.setImporte(dTotalNeteo);
									dtoInsert.setBSBC("S");
									dtoInsert.setFecValor(globalSingleton.getFechaHoy());
									dtoInsert.setIdEstatusReg("P");
									dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
									dtoInsert.setIdDivisa(dtoParams.getIdDivisa());
									dtoInsert.setIdFormaPago(3);
									dtoInsert.setIdBancoBenef(dtoParams.getIdBanco());
									dtoInsert.setIdChequeraBenef(dtoParams.getChequera());
									dtoInsert.setOrigenMov("SET");
									dtoInsert.setConcepto("PAGO POR BARRIDO DE SALDOS");
									dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
									dtoInsert.setNoFolioMov(0);
									dtoInsert.setFolioRef(0);
									dtoInsert.setIdGrupo(iFolio);
									dtoInsert.setNoCliente(iConcentradora);
									dtoInsert.setBeneficiario(dtoParams.getNomEmpresa());

									iAfectados = 0;
									iAfectados = coinversionDao.insertarParametro(dtoInsert);

									dtoInsert = new ParametroDto();

									dtoInsert.setNoEmpresa(iConcentradora);
									dtoInsert.setNoFolioParam(this.obtenerFolioReal("no_folio_param"));
									dtoInsert.setAplica(1);
									dtoInsert.setSecuencia(2);
									dtoInsert.setIdTipoOperacion(3809);
									dtoInsert.setCuenta(listBarrido.get(iRenglon).getNoEmpresa());
									dtoInsert.setIdEstatusMov("L");
									dtoInsert.setIdChequera(dtoParams.getChequera());
									dtoInsert.setIdBanco(dtoParams.getIdBanco());
									dtoInsert.setImporte(dTotalNeteo);
									dtoInsert.setBSBC("S");
									dtoInsert.setFecValor(globalSingleton.getFechaHoy());
									dtoInsert.setIdEstatusReg("P");
									dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
									dtoInsert.setIdDivisa(dtoParams.getIdDivisa());
									dtoInsert.setIdFormaPago(3);
									dtoInsert.setIdBancoBenef(iBanco);
									dtoInsert.setIdChequeraBenef(listBarrido.get(iRenglon).getIdChequera());
									dtoInsert.setOrigenMov("SET");
									dtoInsert.setConcepto("PAGO POR BARRIDO DE SALDOS");
									dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
									dtoInsert.setNoFolioMov(1);
									dtoInsert.setFolioRef(1);
									dtoInsert.setIdGrupo(iFolio);
									dtoInsert.setNoCliente(listBarrido.get(iRenglon).getNoEmpresa());
									dtoInsert.setBeneficiario(listBarrido.get(iRenglon).getNomEmpresa());

									iAfectados = 0;
									iAfectados = coinversionDao.insertarParametro(dtoInsert);

									iEmpresaGen = listBarrido.get(iRenglon).getNoEmpresa();

									generadorDto = new GeneradorDto();
									generadorDto.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
									generadorDto.setNomForma("BarridoAutomaticoPorSaldos");
									generadorDto.setEmpresa(iEmpresaGen);
									generadorDto.setFolParam(iFolio2);
									generadorDto.setFolMovi(iFolioMov);
									generadorDto.setFolDeta(iFolioDet);
									generadorDto.setResult(0); // 1
									generadorDto.setMensajeResp("inicia generador");

									resGenerador = coinversionDao.ejecutarGenerador(generadorDto);
									
									if (funciones.convertirCadenaInteger(resGenerador.get("result").toString()) != 0) {
										mensajes.add("Error en Generador en btnEjecutar " + resGenerador.get("result")
												+ " Empresa:" + iEmpresaGen + " Divisa: " + dtoParams.getIdDivisa());
										break;
									} else {
										dDeudaEmpresa = dDeudaEmpresa - dTotalNeteo;
										if (dDeudaEmpresa <= 0)
											bDeudaTerminada = true;

										if ((dDiferencia - dTotalNeteo) <= 0) {
											coinversionDao.actualizaEstatusSaldoChequera("" + listBarrido.get(iRenglon).getSecuencia());
										}

										dSuma = dSuma + dDiferencia;
										sChequera = sChequera + "'" + listBarrido.get(iRenglon).getIdChequera() + "'";

										if (iFolio == 0)
											iFolio = iFolioDet;
									}
									iFolio2 = 0;
								}
							}
							
							globalSingleton = GlobalSingleton.getInstancia();
							String empresa = globalSingleton.obtenerValorConfiguraSet(1);
							
							if ((dDiferencia - dTotalNeteo) > 0 || empresa.equals("DALTON")) {
								iFolio = this.obtenerFolioReal("no_folio_param");
								if (iFolio <= 0) {
									mensajes.add("No se encontro el folio: no_folio_param");
									mapResult.put("msgUsuario", mensajes);
									return mapResult;
								}

								if (iFolio2 == 0)
									iFolio2 = iFolio;
								int tipo_sal=0;

								dtoInsert = new ParametroDto();

								dtoInsert.setNoEmpresa(listBarrido.get(iRenglon).getNoEmpresa());
								dtoInsert.setNoFolioParam(iFolio);
								dtoInsert.setSecuencia(1);
								dtoInsert.setCuenta(coinversionDao.consultarCuentaEmpresa(listBarrido.get(iRenglon).getNoEmpresa()));
								dtoInsert.setIdChequera(listBarrido.get(iRenglon).getIdChequera());
								dtoInsert.setIdBanco(iBanco);
								dtoInsert.setImporte(dDiferencia - dTotalNeteo);
								dtoInsert.setFecValor(globalSingleton.getFechaHoy()); // sFecha
								dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
								dtoInsert.setIdDivisa(dtoParams.getIdDivisa());
								dtoInsert.setIdBancoBenef(dtoParams.getIdBanco());
								dtoInsert.setIdChequeraBenef(dtoParams.getChequera());
								dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
								dtoInsert.setNoFolioMov(0);
								dtoInsert.setFolioRef(0);
								dtoInsert.setIdGrupo(iFolio2);
								dtoInsert.setBeneficiario(dtoParams.getNomEmpresa());
								dtoInsert.setNoCliente(iConcentradora);
								dtoInsert.setMontoSobregiro(dMontoASobregirar);
								dtoInsert.setTipo_saldo(tipo_sal);

								iAfectados = 0;
								iAfectados = coinversionDao.insertarParametro1(dtoInsert);
								
								iFolio = this.obtenerFolioReal("no_folio_param");
								if (iFolio <= 0) {
									mensajes.add("No se encontro el folio: no_folio_param");
									mapResult.put("msgUsuario", mensajes);
									return mapResult;
								}

								dtoInsert = new ParametroDto();
								int tipo_saldo =0;
								if (listBarrido.get(iRenglon).getTipoSaldo() == 0)
								{
									tipo_saldo = listBarrido.get(iRenglon).getTipoSaldo();
								}
								else{
								 tipo_saldo = coinversionDao.buscaAbono(listBarrido.get(iRenglon).getTipoSaldo());
								}
								
								dtoInsert.setNoEmpresa(iConcentradora);
								dtoInsert.setNoFolioParam(iFolio);
								dtoInsert.setSecuencia(2);
								dtoInsert.setCuenta(listBarrido.get(iRenglon).getNoEmpresa());
								dtoInsert.setIdChequera(dtoParams.getChequera());
								dtoInsert.setIdBanco(dtoParams.getIdBanco());
								dtoInsert.setImporte(dDiferencia - dTotalNeteo);
								dtoInsert.setFecValor(globalSingleton.getFechaHoy()); // sFecha
								dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
								dtoInsert.setIdDivisa(dtoParams.getIdDivisa());
								dtoInsert.setIdBancoBenef(iBanco);
								dtoInsert.setIdChequeraBenef(listBarrido.get(iRenglon).getIdChequera());
								dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
								dtoInsert.setNoFolioMov(1);
								dtoInsert.setFolioRef(1);
								dtoInsert.setIdGrupo(iFolio2);
								dtoInsert.setBeneficiario(listBarrido.get(iRenglon).getNomEmpresa());
								dtoInsert.setNoCliente(listBarrido.get(iRenglon).getNoEmpresa());
								dtoInsert.setMontoSobregiro(dMontoASobregirar);
								dtoInsert.setTipo_saldo(tipo_saldo);
								
								iAfectados = 0;
								iAfectados = coinversionDao.insertarParametro1(dtoInsert);
								iEmpresaGen = listBarrido.get(iRenglon).getNoEmpresa();
								bEjMovimiento = true;
							}
						} else {
							if (dDiferencia > 0/* && dtoParams.isOpcSobregiro() == true*/) {
								// barre sobregiro
								/*
								 * if(listBarrido.get(iRenglon).getSaldoChequera
								 * () > 0) { if(listBarrido.get(iRenglon).
								 * getMontoSobregiro() +
								 * listBarrido.get(iRenglon).getSaldoChequera()
								 * >= listBarrido.get(iRenglon).getSobregiro())
								 * dDiferencia =
								 * listBarrido.get(iRenglon).getSobregiro();
								 * else dDiferencia =
								 * listBarrido.get(iRenglon).getMontoSobregiro()
								 * +
								 * listBarrido.get(iRenglon).getSaldoChequera();
								 * } else dDiferencia =
								 * listBarrido.get(iRenglon).getMontoSobregiro()
								 * ;
								 */
								if (dDiferencia > 0) {
									iFolio = this.obtenerFolioReal("no_folio_param");
									if (iFolio <= 0) {
										mensajes.add("No se encontro el folio: no_folio_param");
										mapResult.put("msgUsuario", mensajes);
										return mapResult;
									}

									if (iFolio2 == 0){
										iFolio2 = iFolio;
									}
									dtoInsert = new ParametroDto();

									dtoInsert.setNoEmpresa(listBarrido.get(iRenglon).getNoEmpresa());
									dtoInsert.setNoFolioParam(iFolio);
									dtoInsert.setSecuencia(1);
									dtoInsert.setCuenta(coinversionDao.consultarCuentaEmpresa(listBarrido.get(iRenglon).getNoEmpresa()));
									dtoInsert.setIdChequera(listBarrido.get(iRenglon).getIdChequera());
									dtoInsert.setIdBanco(iBanco);
									dtoInsert.setImporte(dDiferencia);
									dtoInsert.setFecValor(globalSingleton.getFechaHoy());
									dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
									dtoInsert.setIdDivisa(dtoParams.getIdDivisa());
									dtoInsert.setIdBancoBenef(dtoParams.getIdBanco());
									dtoInsert.setIdChequeraBenef(dtoParams.getChequera());
									dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
									dtoInsert.setNoFolioMov(0);
									dtoInsert.setFolioRef(0);
									dtoInsert.setIdGrupo(iFolio2);
									dtoInsert.setBeneficiario(dtoParams.getNomEmpresa());
									dtoInsert.setNoCliente(iConcentradora);
									dtoInsert.setMontoSobregiro(dDiferencia);

									iAfectados = 0;
									iAfectados = coinversionDao.insertarParametro1(dtoInsert);
								//	bEjMovimiento=true;
									
									iFolio = this.obtenerFolioReal("no_folio_param");
									if (iFolio <= 0) {
										mensajes.add("No se encontro el folio: no_folio_param");
										mapResult.put("msgUsuario", mensajes);
										return mapResult;
									}

									dtoInsert = new ParametroDto();

									dtoInsert.setNoEmpresa(iConcentradora);
									dtoInsert.setNoFolioParam(iFolio);
									dtoInsert.setSecuencia(2);
									dtoInsert.setCuenta(listBarrido.get(iRenglon).getNoEmpresa());
									dtoInsert.setIdChequera(dtoParams.getChequera());
									dtoInsert.setIdBanco(dtoParams.getIdBanco());
									dtoInsert.setImporte(dDiferencia);
									dtoInsert.setFecValor(globalSingleton.getFechaHoy());
									dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
									dtoInsert.setIdDivisa(dtoParams.getIdDivisa());
									dtoInsert.setIdBancoBenef(iBanco);
									dtoInsert.setIdChequeraBenef(listBarrido.get(iRenglon).getIdChequera());
									dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
									dtoInsert.setNoFolioMov(1);
									dtoInsert.setFolioRef(1);
									dtoInsert.setIdGrupo(iFolio2);
									dtoInsert.setBeneficiario(listBarrido.get(iRenglon).getNomEmpresa());
									dtoInsert.setNoCliente(listBarrido.get(iRenglon).getNoEmpresa());
									dtoInsert.setMontoSobregiro(dDiferencia);

									iAfectados = 0;
									iAfectados = coinversionDao.insertarParametro1(dtoInsert);

									iEmpresaGen = listBarrido.get(iRenglon).getNoEmpresa();
									bEjMovimiento = true;
								}
							} else if (dtoParams.isOpcFondeo() == true && dDiferencia < 0) {
								iFolio = this.obtenerFolioReal("no_folio_param");
								if (iFolio <= 0) {
									mensajes.add("No se encontro el folio: no_folio_param");
									mapResult.put("msgUsuario", mensajes);
									return mapResult;
								}
								if (iFolio2 == 0){
									iFolio2 = iFolio;
								}
								
								if (!globalSingleton.obtenerValorConfiguraSet(1).equals("CCM")){
									dDiferencia = Math.abs(dDiferencia);
								}else{
									dDiferencia = Math.abs(listBarrido.get(iRenglon).getTraspaso());
								}
								dtoInsert = new ParametroDto();

								dtoInsert.setNoEmpresa(iConcentradora);
								dtoInsert.setNoFolioParam(iFolio);
								dtoInsert.setAplica(1);
								dtoInsert.setSecuencia(1);
								dtoInsert.setIdTipoOperacion(3806);
								dtoInsert.setCuenta(listBarrido.get(iRenglon).getNoEmpresa());
								dtoInsert.setIdEstatusMov("L");
								dtoInsert.setIdChequera(dtoParams.getChequera());
								dtoInsert.setIdBanco(dtoParams.getIdBanco());
								dtoInsert.setImporte(dDiferencia);
								dtoInsert.setBSBC("S");
								dtoInsert.setFecValor(globalSingleton.getFechaHoy());
								dtoInsert.setIdEstatusReg("P");
								dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
								dtoInsert.setIdDivisa(dtoParams.getIdDivisa());
								dtoInsert.setIdFormaPago(3);
								dtoInsert.setIdBancoBenef(iBanco);
								dtoInsert.setIdChequeraBenef(listBarrido.get(iRenglon).getIdChequera());
								dtoInsert.setOrigenMov("SET");
								dtoInsert.setConcepto("FONDEO POR BARRIDO DE SALDOS");
								dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
								dtoInsert.setNoFolioMov(0);
								dtoInsert.setFolioRef(0);
								dtoInsert.setIdGrupo(iFolio2);
								dtoInsert.setNoCliente(listBarrido.get(iRenglon).getNoEmpresa());
								dtoInsert.setBeneficiario(listBarrido.get(iRenglon).getNomEmpresa());

								iAfectados = 0;
								iAfectados = coinversionDao.insertarParametro(dtoInsert);

								dtoInsert = new ParametroDto();

								dtoInsert.setNoEmpresa(listBarrido.get(iRenglon).getNoEmpresa());
								dtoInsert.setNoFolioParam(this.obtenerFolioReal("no_folio_param"));
								dtoInsert.setAplica(1);
								dtoInsert.setSecuencia(2);
								dtoInsert.setIdTipoOperacion(3806);
								dtoInsert.setCuenta(coinversionDao.consultarCuentaEmpresa(listBarrido.get(iRenglon).getNoEmpresa()));
								dtoInsert.setIdEstatusMov("L");
								dtoInsert.setIdChequera(listBarrido.get(iRenglon).getIdChequera());
								dtoInsert.setIdBanco(iBanco);
								dtoInsert.setImporte(dDiferencia);
								dtoInsert.setBSBC("S");
								dtoInsert.setFecValor(globalSingleton.getFechaHoy());
								dtoInsert.setIdEstatusReg("P");
								dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
								dtoInsert.setIdDivisa(dtoParams.getIdDivisa());
								dtoInsert.setIdFormaPago(3);
								dtoInsert.setIdBancoBenef(dtoParams.getIdBanco());
								dtoInsert.setIdChequeraBenef(dtoParams.getChequera());
								dtoInsert.setOrigenMov("SET");
								dtoInsert.setConcepto("FONDEO POR BARRIDO DE SALDOS");
								dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
								dtoInsert.setNoFolioMov(1);
								dtoInsert.setFolioRef(1);
								dtoInsert.setIdGrupo(iFolio2);
								dtoInsert.setNoCliente(iConcentradora);
								dtoInsert.setBeneficiario(dtoParams.getNomEmpresa());

								iAfectados = 0;
								iAfectados = coinversionDao.insertarParametro(dtoInsert);

								iEmpresaGen = iConcentradora;
								bEjMovimiento = true;
							} // else if(dtoParams.isOpcFondeo() == true &&
								// dDiferencia < 0)...
						} // else del
							// if(listBarrido.get(iRenglon).getSaldoChequera() >
							// 0 ...

					} // else del if(dtoParams.isOpcFondeo()...
					if (bEjMovimiento) {
						dSuma = dSuma + dDiferencia;
						sChequeraEmp = "'" + listBarrido.get(iRenglon).getIdChequera() + "',";
					}

				} /* if(iBanco != dtoParams.getIdBanco() && dDiferencia >= 50000){}*/
				else {
					if (dDiferencia > 0) {
						dSumaNo = dSumaNo + dDiferencia;
						sChequeraNoEmp = "'" + listBarrido. get(iRenglon).getIdChequera() + "',";
					}
				}

				if (bEjMovimiento){
					sSecuencias = sSecuencias + listBarrido.get(iRenglon).getSecuencia() + ",";
				}
				
				if (bEjMovimiento) { 
					if (!sSecuencias.trim().equals("")){
						sSecuencias = sSecuencias.substring(0, sSecuencias.length() - 1);
					}
					if (!sChequeraEmp.trim().equals("")) {
						generadorDto = new GeneradorDto();
						generadorDto.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
						generadorDto.setNomForma("BarridoAutomaticoPorSaldos");
						generadorDto.setEmpresa(iEmpresaGen);
						generadorDto.setFolParam(iFolio2);
						generadorDto.setFolMovi(iFolioMov);
						generadorDto.setFolDeta(iFolioDet);
						generadorDto.setResult(0);
						generadorDto.setMensajeResp("inicia generador");
						resGenerador = coinversionDao.ejecutarGenerador(generadorDto);
						if (funciones.convertirCadenaInteger(resGenerador.get("result").toString()) != 0) {
							mensajes.add("Error en Generador en btnEjecutar " + resGenerador.get("result"));
							break;
						} else {
							// cambiar el estatus de las chequeras procesadas a
							// X para no volverlas a considerar
							if (!sSecuencias.equals("")){
								coinversionDao.actualizaEstatusSaldoChequera(sSecuencias);
							}
							
							if (iFolio == 0){
								iFolio = iFolioDet;
							}
							
							sChequera = sChequera + sChequeraEmp;

							if (iRenglon > listBarrido.size() - 1){
								break;
							}
						}
						iFolio2 = 0;
					}
				}

				if (!sChequeraNoEmp.trim().equals(""))
					sChequeraNo = sChequeraNo + sChequeraNoEmp;

			} // for(;iRenglon < listBarrido.size(); iRenglon++)...

			mensajes.add("Datos Registrados");

			if (!sChequera.equals("")) {
				sChequera = sChequera.substring(0, sChequera.length() - 1);
				// Call imprime_reporte(ps_chequera, True, pd_suma, pd_folio)
			}

			if (sChequeraNo.equals("") && !sChequeraNoEmp.equals(""))
				sChequeraNo = sChequeraNoEmp;

			if (!sChequeraNo.equals("")) {
				sChequeraNo = sChequeraNo.substring(0, sChequeraNo.length() - 1);
				// Call imprime_reporte(ps_chequera_no, False, pd_suma_no, 0)
			}

			mapResult.put("msgUsuario", mensajes);

		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:ejecutarBarrido");
		}
		return mapResult;
	}

	// solicitud de barrido

	public List<LlenaComboEmpresasDto> llenarCmbEmpresasCoinversionistas(int iEmpresa, String sDivisa) {
		globalSingleton = GlobalSingleton.getInstancia();
		List<LlenaComboEmpresasDto> list = new ArrayList<LlenaComboEmpresasDto>();
		int iUsuario = 0;
		int iLinea = 0;
		try {
			iLinea = this.obtenerLinea(sDivisa);
			iUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			list = coinversionDao.consultarEmpresasCoinversionistas(iEmpresa, iLinea, iUsuario);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:llenarCmbEmpresasCoinversionistas");
		}
		return list;
	}

	public List<LlenaComboGralDto> llenarCmbBancos(int iEmpresa, String sDivisa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			list = coinversionDao.consultarBancos(iEmpresa, sDivisa);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:llenarCmbEmpresasCoinversionistas");
		}
		return list;
	}

	public List<Double> consultarSaldoFinal(int iEmpresa, int iBanco, String sDivisa, String sChequera) {
		List<Double> dSaldoFin = new ArrayList<Double>();
		List<CatCtaBancoDto> listDivisa = new ArrayList<CatCtaBancoDto>();
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			dSaldoFin.add(coinversionDao.consultarSaldoFinal(iEmpresa, iBanco, sChequera));
			if (sDivisa.equals("")) {
				listDivisa = coinversionDao.consultaDivisa(iBanco, sChequera);
				sDivisa = listDivisa.get(0).getIdDivisa();
			}
			iEmpresa = globalSingleton.getUsuarioLoginDto().getNumeroEmpresa();
			dSaldoFin.add(coinversionDao.consultarChequesPorEntregar(iEmpresa, iBanco, sDivisa, sChequera));
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:consultarSaldoFinal");
		}
		return dSaldoFin;
	}

	public List<Map<String, String>> consultarSaldoCreditoCoinvPorChequera(int iCoinversora, int iEmpresa,
			String sDivisa) {
		List<Map<String, String>> saldos = new ArrayList<Map<String, String>>();
		try {
			saldos = coinversionDao.consultarSaldoCreditoCoinvPorChequera(iCoinversora, iEmpresa, sDivisa);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:consultarSaldoCreditoCoinvPorChequera");
		}
		return saldos;
	}

	public List<LlenaComboGralDto> consultarBancos(int iEmpresa, int iBanco, String sChequera) {
		List<LlenaComboGralDto> listBanco = new ArrayList<LlenaComboGralDto>();
		List<CatCtaBancoDto> listDivisa = new ArrayList<CatCtaBancoDto>();
		String sDivisa = "";
		try {
			listDivisa = coinversionDao.consultaDivisa(iBanco, sChequera);
			sDivisa = listDivisa.get(0).getIdDivisa();
			listBanco = coinversionDao.consultarBancos2(iEmpresa, sDivisa);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:consultarBancos");
		}
		return listBanco;
	}

	/**
	 * Metodo que inserta en la tabla parametro y ejecuta el generador para
	 * registrar la solicitud de barrido (traspaso) en la tabla movimiento
	 * 
	 * @param dtoParam
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> ejecutarSolicitudBarrido(ParamTraspasoCoinversionDto dtoParam) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		Map<String, Object> resGenerador = new HashMap<String, Object>();
		List<CatCtaBancoDto> listDivisa = new ArrayList<CatCtaBancoDto>();
		globalSingleton = GlobalSingleton.getInstancia();
		ParametroDto dtoInsert;
		GeneradorDto generadorDto;
		int iTipoOperacion = 0;
		int iFolioParametro = 0;
		int iFolioParametroA = 0;
		int iFolio = 0;
		int iFolio2 = 0;
		int iCuenta = 0;
		int iAfectados = 0;
		int iFolioMov = 0;
		int iFolioDet = 0;
		int iBanco = 0;
		int iBancoBenef = 0;
		int a1 = 0;
		int a2 = 0;
		double dImporte = 0;
		double dPago = 0;
		double dMonto = 0;
		String sChequera = "";
		String sChequeraBenef = "";
		String sDivisaDe = "";
		String sDivisaA = "";

		try {
			// verificar divisas
			dImporte = dtoParam.getMontoTraspaso();
			listDivisa = coinversionDao.consultaDivisa(dtoParam.getIdBancoOrigen(), dtoParam.getIdChequeraOrigen());
			sDivisaDe = listDivisa.get(0).getIdDivisa();
			listDivisa = coinversionDao.consultaDivisa(dtoParam.getIdBancoDestino(), dtoParam.getIdChequeraDestino());
			sDivisaA = listDivisa.get(0).getIdDivisa();

			if (!sDivisaDe.trim().equals(sDivisaA.trim())) {
				mapResult.put("msgUsuario",
						"Error, el traspaso no se puede realizar porque las divisas son diferentes");
				return mapResult;
			}

			// verificar que las empresas sean iguales o diferentes
			// empresas iguales
			iTipoOperacion = 3805;
			iFolioParametro = this.obtenerFolioReal("no_folio_param");
			if (iFolioParametro <= 0) {
				return mapResult;
			}

			iCuenta = coinversionDao.consultarCuentaEmpresa(dtoParam.getIdEmpresaOrigen());
			if (iCuenta == 0) {
				mapResult.put("msgUsuario", "Error en la cuenta");
				return mapResult;
			}

			// 262 PAGO DE CREDITOS AL BARRER?
			if (globalSingleton.obtenerValorConfiguraSet(262).equals("SI") && dtoParam.getCredito() > 0) {
				// cuando se tiene adeudo se paga
				if (dtoParam.getCredito() >= dImporte) {
					dPago = dImporte;

					iFolio = this.obtenerFolioReal("no_folio_param");
					if (iFolio <= 0) {
						mapResult.put("msgUsuario", "No se encontro el folio: no_folio_param");
						return mapResult;
					}

					if (iFolio2 == 0)
						iFolio2 = iFolio;

					dtoInsert = new ParametroDto();
					dtoInsert.setNoEmpresa(dtoParam.getIdEmpresaOrigen());
					dtoInsert.setNoFolioParam(iFolio);
					dtoInsert.setAplica(1);
					dtoInsert.setSecuencia(1);
					dtoInsert.setIdTipoOperacion(3809);
					dtoInsert.setCuenta(coinversionDao.consultarCuentaEmpresa(dtoParam.getIdEmpresaOrigen()));
					dtoInsert.setIdEstatusMov("L");
					dtoInsert.setIdChequera(dtoParam.getIdChequeraOrigen());
					dtoInsert.setIdBanco(dtoParam.getIdBancoOrigen());
					dtoInsert.setImporte(dPago);
					dtoInsert.setBSBC("S");
					dtoInsert.setFecValor(globalSingleton.getFechaHoy());
					dtoInsert.setIdEstatusReg("P");
					dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
					dtoInsert.setIdDivisa(dtoParam.getIdDivisa());
					dtoInsert.setIdFormaPago(3);
					dtoInsert.setIdBancoBenef(dtoParam.getIdBancoDestino());
					dtoInsert.setIdChequeraBenef(dtoParam.getIdChequeraDestino());
					dtoInsert.setOrigenMov("SET");
					dtoInsert.setConcepto("PAGO POR BARRIDO DE SALDOS");
					dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
					dtoInsert.setNoFolioMov(0);
					dtoInsert.setFolioRef(0);
					dtoInsert.setIdGrupo(iFolio);
					dtoInsert.setNoCliente(dtoParam.getIdEmpresaDestino());
					dtoInsert.setBeneficiario(dtoParam.getDescEmpresaDestino());

					iAfectados = 0;
					iAfectados = coinversionDao.insertarParametro(dtoInsert);

					dtoInsert = new ParametroDto();
					dtoInsert.setNoEmpresa(dtoParam.getIdEmpresaDestino());
					dtoInsert.setNoFolioParam(this.obtenerFolioReal("no_folio_param"));
					dtoInsert.setAplica(1);
					dtoInsert.setSecuencia(2);
					dtoInsert.setIdTipoOperacion(3809);
					dtoInsert.setCuenta(dtoParam.getIdEmpresaOrigen());
					dtoInsert.setIdEstatusMov("L");
					dtoInsert.setIdChequera(dtoParam.getIdChequeraDestino());
					dtoInsert.setIdBanco(dtoParam.getIdBancoDestino());
					dtoInsert.setImporte(dPago);
					dtoInsert.setBSBC("S");
					dtoInsert.setFecValor(globalSingleton.getFechaHoy());
					dtoInsert.setIdEstatusReg("P");
					dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
					dtoInsert.setIdDivisa(dtoParam.getIdDivisa());
					dtoInsert.setIdFormaPago(3);
					dtoInsert.setIdBancoBenef(dtoParam.getIdBancoOrigen());
					dtoInsert.setIdChequeraBenef(dtoParam.getIdChequeraOrigen());
					dtoInsert.setOrigenMov("SET");
					dtoInsert.setConcepto("PAGO POR BARRIDO DE SALDOS");
					dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
					dtoInsert.setNoFolioMov(1);
					dtoInsert.setFolioRef(1);
					dtoInsert.setIdGrupo(iFolio);
					dtoInsert.setNoCliente(dtoParam.getIdEmpresaOrigen());
					dtoInsert.setBeneficiario(dtoParam.getDescEmpresaOrigen());

					iAfectados = 0;
					iAfectados = coinversionDao.insertarParametro(dtoInsert);

					generadorDto = new GeneradorDto();
					generadorDto.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
					generadorDto.setNomForma("SolicitudBarrido");
					generadorDto.setEmpresa(dtoParam.getIdEmpresaOrigen());
					generadorDto.setFolParam(iFolio2);
					generadorDto.setFolMovi(iFolioMov);
					generadorDto.setFolDeta(iFolioDet);
					generadorDto.setResult(0);
					generadorDto.setMensajeResp("inicia generador");

					resGenerador = coinversionDao.ejecutarGenerador(generadorDto);
					if (funciones.convertirCadenaInteger(resGenerador.get("result").toString()) != 0) {
						mapResult.put("msgUsuario", "Error en Generador en btnEjecutar " + resGenerador.get("result")
								+ " Empresa " + dtoParam.getIdEmpresaOrigen() + " Divisa " + dtoParam.getIdDivisa());
						return mapResult;
					} else {
						mapResult.put("msgUsuario", "Datos Registrados");
						return mapResult;
					}
				} else {
					// si el importe > credito
					dPago = dtoParam.getCredito();
					dMonto = dtoParam.getMontoTraspaso() - dPago;
					sChequera = dtoParam.getIdChequeraOrigen();
					iBanco = dtoParam.getIdBancoOrigen();
					sChequeraBenef = dtoParam.getIdChequeraDestino();
					iBancoBenef = dtoParam.getIdBancoDestino();

					dtoInsert = new ParametroDto();
					dtoInsert.setNoEmpresa(dtoParam.getIdEmpresaOrigen());
					dtoInsert.setNoFolioParam(iFolioParametro);
					dtoInsert.setAplica(1);
					dtoInsert.setSecuencia(1);
					dtoInsert.setIdTipoOperacion(iTipoOperacion);
					dtoInsert.setCuenta(iCuenta);
					dtoInsert.setIdEstatusMov("L");
					dtoInsert.setIdChequera(sChequera);
					dtoInsert.setIdBanco(iBanco);
					dtoInsert.setImporte(dMonto);
					dtoInsert.setBSBC("S");
					dtoInsert.setFecValor(globalSingleton.getFechaHoy());
					dtoInsert.setIdEstatusReg("P");
					dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
					dtoInsert.setIdDivisa(sDivisaDe);
					dtoInsert.setIdFormaPago(3);
					dtoInsert.setIdBancoBenef(iBancoBenef);
					dtoInsert.setIdChequeraBenef(sChequeraBenef);
					dtoInsert.setOrigenMov("SET");
					dtoInsert.setConcepto(dtoParam.getConcepto());
					dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
					dtoInsert.setNoFolioMov(0);
					dtoInsert.setFolioRef(0);
					dtoInsert.setIdGrupo(iFolioParametro);
					dtoInsert.setNoCliente(dtoParam.getIdEmpresaDestino());
					dtoInsert.setBeneficiario(dtoParam.getDescEmpresaDestino());

					iAfectados = 0;
					iAfectados = coinversionDao.insertarParametro(dtoInsert);

					iFolioParametroA = this.obtenerFolioReal("no_folio_param");
					if (iFolioParametro <= 0) {
						return mapResult;
					}

					sChequera = dtoParam.getIdChequeraDestino();
					iBanco = dtoParam.getIdBancoDestino();
					sChequeraBenef = dtoParam.getIdChequeraOrigen();
					iBancoBenef = dtoParam.getIdBancoOrigen();
					dImporte = dtoParam.getMontoTraspaso();

					dtoInsert = new ParametroDto();
					dtoInsert.setNoEmpresa(dtoParam.getIdEmpresaDestino());
					dtoInsert.setNoFolioParam(iFolioParametroA);
					dtoInsert.setAplica(1);
					dtoInsert.setSecuencia(2);
					dtoInsert.setIdTipoOperacion(iTipoOperacion);
					dtoInsert.setCuenta(dtoParam.getIdEmpresaOrigen());
					dtoInsert.setIdEstatusMov("L");
					dtoInsert.setIdChequera(sChequera);
					dtoInsert.setIdBanco(iBanco);
					dtoInsert.setImporte(dMonto);
					dtoInsert.setBSBC("S");
					dtoInsert.setFecValor(globalSingleton.getFechaHoy());
					dtoInsert.setIdEstatusReg("P");
					dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
					dtoInsert.setIdDivisa(dtoParam.getIdDivisa());
					dtoInsert.setIdFormaPago(3);
					dtoInsert.setIdBancoBenef(iBancoBenef);
					dtoInsert.setIdChequeraBenef(sChequeraBenef);
					dtoInsert.setOrigenMov("SET");
					dtoInsert.setConcepto(dtoParam.getConcepto());
					dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
					dtoInsert.setNoFolioMov(1);
					dtoInsert.setFolioRef(1);
					dtoInsert.setIdGrupo(iFolioParametro);
					dtoInsert.setNoCliente(dtoParam.getIdEmpresaOrigen());
					dtoInsert.setBeneficiario(dtoParam.getDescEmpresaOrigen());

					iAfectados = 0;
					iAfectados = coinversionDao.insertarParametro(dtoInsert);

					generadorDto = new GeneradorDto();
					generadorDto.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
					generadorDto.setNomForma("SolicitudBarrido");
					generadorDto.setEmpresa(dtoParam.getIdEmpresaOrigen());
					generadorDto.setFolParam(iFolioParametro);
					generadorDto.setFolMovi(a1);
					generadorDto.setFolDeta(a2);
					generadorDto.setResult(0);
					generadorDto.setMensajeResp("inicia generador");

					resGenerador = coinversionDao.ejecutarGenerador(generadorDto);
					if (funciones.convertirCadenaInteger(resGenerador.get("result").toString()) != 0) {
						mapResult.put("msgUsuario", "Error en Generador en btnEjecutar " + resGenerador.get("result"));
						return mapResult;
					}

					// pagamos
					iFolio = this.obtenerFolioReal("no_folio_param");
					if (iFolio <= 0) {
						mapResult.put("msgUsuario", "No se encontro el folio: no_folio_param");
						return mapResult;
					}

					if (iFolio2 == 0)
						iFolio2 = iFolio;

					dtoInsert = new ParametroDto();
					dtoInsert.setNoEmpresa(dtoParam.getIdEmpresaOrigen());
					dtoInsert.setNoFolioParam(iFolio);
					dtoInsert.setAplica(1);
					dtoInsert.setSecuencia(1);
					dtoInsert.setIdTipoOperacion(3809);
					dtoInsert.setCuenta(coinversionDao.consultarCuentaEmpresa(dtoParam.getIdEmpresaOrigen()));
					dtoInsert.setIdEstatusMov("L");
					dtoInsert.setIdChequera(dtoParam.getIdChequeraOrigen());
					dtoInsert.setIdBanco(dtoParam.getIdBancoOrigen());
					dtoInsert.setImporte(dPago);
					dtoInsert.setBSBC("S");
					dtoInsert.setFecValor(globalSingleton.getFechaHoy());
					dtoInsert.setIdEstatusReg("P");
					dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
					dtoInsert.setIdDivisa(dtoParam.getIdDivisa());
					dtoInsert.setIdFormaPago(3);
					dtoInsert.setIdBancoBenef(dtoParam.getIdBancoDestino());
					dtoInsert.setIdChequeraBenef(dtoParam.getIdChequeraDestino());
					dtoInsert.setOrigenMov("SET");
					dtoInsert.setConcepto("PAGO POR BARRIDO DE SALDOS");
					dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
					dtoInsert.setNoFolioMov(0);
					dtoInsert.setFolioRef(0);
					dtoInsert.setIdGrupo(iFolio);
					dtoInsert.setNoCliente(dtoParam.getIdEmpresaDestino());
					dtoInsert.setBeneficiario(dtoParam.getDescEmpresaDestino());

					iAfectados = 0;
					iAfectados = coinversionDao.insertarParametro(dtoInsert);

					dtoInsert = new ParametroDto();
					dtoInsert.setNoEmpresa(dtoParam.getIdEmpresaDestino());
					dtoInsert.setNoFolioParam(this.obtenerFolioReal("no_folio_param"));
					dtoInsert.setAplica(1);
					dtoInsert.setSecuencia(2);
					dtoInsert.setIdTipoOperacion(3809);
					dtoInsert.setCuenta(dtoParam.getIdEmpresaOrigen());
					dtoInsert.setIdEstatusMov("L");
					dtoInsert.setIdChequera(dtoParam.getIdChequeraDestino());
					dtoInsert.setIdBanco(dtoParam.getIdBancoDestino());
					dtoInsert.setImporte(dPago);
					dtoInsert.setBSBC("S");
					dtoInsert.setFecValor(globalSingleton.getFechaHoy());
					dtoInsert.setIdEstatusReg("P");
					dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
					dtoInsert.setIdDivisa(dtoParam.getIdDivisa());
					dtoInsert.setIdFormaPago(3);
					dtoInsert.setIdBancoBenef(dtoParam.getIdBancoOrigen());
					dtoInsert.setIdChequeraBenef(dtoParam.getIdChequeraOrigen());
					dtoInsert.setOrigenMov("SET");
					dtoInsert.setConcepto("PAGO POR BARRIDO DE SALDOS");
					dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
					dtoInsert.setNoFolioMov(1);
					dtoInsert.setFolioRef(1);
					dtoInsert.setIdGrupo(iFolio);
					dtoInsert.setNoCliente(dtoParam.getIdEmpresaOrigen());
					dtoInsert.setBeneficiario(dtoParam.getDescEmpresaOrigen());

					iAfectados = 0;
					iAfectados = coinversionDao.insertarParametro(dtoInsert);

					generadorDto = new GeneradorDto();
					generadorDto.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
					generadorDto.setNomForma("SolicitudBarrido");
					generadorDto.setEmpresa(dtoParam.getIdEmpresaOrigen());
					generadorDto.setFolParam(iFolio2);
					generadorDto.setFolMovi(iFolioMov);
					generadorDto.setFolDeta(iFolioDet);
					generadorDto.setResult(0);
					generadorDto.setMensajeResp("inicia generador");

					resGenerador = coinversionDao.ejecutarGenerador(generadorDto);
					if (funciones.convertirCadenaInteger(resGenerador.get("result").toString()) != 0) {
						mapResult.put("msgUsuario", "Error en Generador en btnEjecutar " + resGenerador.get("result")
								+ " Empresa: " + dtoParam.getIdEmpresaOrigen() + " Divisa; " + dtoParam.getIdDivisa());
						return mapResult;
					}

					mapResult.put("msgUsuario", "Datos Registrados");
				}
			} else {
				// si no hay saldo en credito
				logger.info("si no hay saldo en credito");
				sChequera = dtoParam.getIdChequeraOrigen();
				iBanco = dtoParam.getIdBancoOrigen();
				sChequeraBenef = dtoParam.getIdChequeraDestino();
				iBancoBenef = dtoParam.getIdBancoDestino();
				int tipo_saldo = 0;

				dtoInsert = new ParametroDto();
				dtoInsert.setNoEmpresa(dtoParam.getIdEmpresaOrigen());
				dtoInsert.setNoFolioParam(iFolioParametro);
				dtoInsert.setAplica(1);
				dtoInsert.setSecuencia(1);
				dtoInsert.setIdTipoOperacion(iTipoOperacion);
				dtoInsert.setCuenta(iCuenta);
				dtoInsert.setIdEstatusMov("L");
				dtoInsert.setIdChequera(sChequera);
				dtoInsert.setIdBanco(iBanco);
				dtoInsert.setImporte(dImporte);
				dtoInsert.setBSBC("S");
				dtoInsert.setFecValor(globalSingleton.getFechaHoy());
				dtoInsert.setIdEstatusReg("P");
				dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
				dtoInsert.setIdDivisa(sDivisaDe);
				dtoInsert.setIdFormaPago(3);
				dtoInsert.setIdBancoBenef(iBancoBenef);
				dtoInsert.setIdChequeraBenef(sChequeraBenef);
				dtoInsert.setOrigenMov("SET");
				dtoInsert.setConcepto(dtoParam.getConcepto());
				dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
				dtoInsert.setNoFolioMov(0);
				dtoInsert.setFolioRef(0);
				dtoInsert.setIdGrupo(iFolioParametro);
				dtoInsert.setNoCliente(dtoParam.getIdEmpresaDestino());
				dtoInsert.setBeneficiario(dtoParam.getDescEmpresaDestino());
				dtoInsert.setTipo_saldo(tipo_saldo);

				iAfectados = 0;
				iAfectados = coinversionDao.insertarParametro(dtoInsert);

				iFolioParametroA = this.obtenerFolioReal("no_folio_param");
				if (iFolioParametro <= 0)
					return mapResult;

				sChequera = dtoParam.getIdChequeraDestino();
				iBanco = dtoParam.getIdBancoDestino();
				sChequeraBenef = dtoParam.getIdChequeraOrigen();
				iBancoBenef = dtoParam.getIdBancoOrigen();
				dImporte = dtoParam.getMontoTraspaso();
				
				if(dtoParam.getTipoSaldo() == 0)
				{
					tipo_saldo = dtoParam.getTipoSaldo();
				}
				else
				{
				
					tipo_saldo=coinversionDao.buscaAbono(dtoParam.getTipoSaldo());
				}

				dtoInsert = new ParametroDto();
				dtoInsert.setNoEmpresa(dtoParam.getIdEmpresaDestino());
				dtoInsert.setNoFolioParam(iFolioParametroA);
				dtoInsert.setAplica(1);
				dtoInsert.setSecuencia(2);
				dtoInsert.setIdTipoOperacion(iTipoOperacion);
				dtoInsert.setCuenta(dtoParam.getIdEmpresaOrigen());
				dtoInsert.setIdEstatusMov("L");
				dtoInsert.setIdChequera(sChequera);
				dtoInsert.setIdBanco(iBanco);
				dtoInsert.setImporte(dImporte);
				dtoInsert.setBSBC("S");
				dtoInsert.setFecValor(globalSingleton.getFechaHoy());
				dtoInsert.setIdEstatusReg("P");
				dtoInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
				dtoInsert.setIdDivisa(dtoParam.getIdDivisa());
				dtoInsert.setIdFormaPago(3);
				dtoInsert.setIdBancoBenef(iBancoBenef);
				dtoInsert.setIdChequeraBenef(sChequeraBenef);
				dtoInsert.setOrigenMov("SET");
				dtoInsert.setConcepto(dtoParam.getConcepto());
				dtoInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
				dtoInsert.setNoFolioMov(1);
				dtoInsert.setFolioRef(1);
				dtoInsert.setIdGrupo(iFolioParametro);
				dtoInsert.setNoCliente(dtoParam.getIdEmpresaOrigen());
				dtoInsert.setBeneficiario(dtoParam.getDescEmpresaOrigen());
				dtoInsert.setTipo_saldo(tipo_saldo);

				iAfectados = 0;
				iAfectados = coinversionDao.insertarParametro(dtoInsert);

				generadorDto = new GeneradorDto();
				generadorDto.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
				generadorDto.setNomForma("SolicitudBarrido");
				generadorDto.setEmpresa(dtoParam.getIdEmpresaOrigen());
				generadorDto.setFolParam(iFolioParametro);
				generadorDto.setFolMovi(a1);
				generadorDto.setFolDeta(a2);
				generadorDto.setResult(0);
				generadorDto.setMensajeResp("inicia generador");

				resGenerador = coinversionDao.ejecutarGenerador(generadorDto);
				if (funciones.convertirCadenaInteger(resGenerador.get("result").toString()) != 0) {
					mapResult.put("msgUsuario", "Error en Generador en btnEjecutar " + resGenerador.get("result"));
					return mapResult;
				}

				mapResult.put("msgUsuario", "Datos Registrados");
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:ejecutarSolicitudBarrido");
		}
		return mapResult;
	}

	// solicitud fondeo

	public List<LlenaComboGralDto> llenarCmbBancosSolFondeo2(int iEmpresa, String sDivisa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			list = coinversionDao.consultarBancosSolFondeo2(iEmpresa, sDivisa);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:llenarCmbBancosSolFondeo2");
		}
		return list;
	}

	public List<LlenaComboChequeraDto> llenarCmbChequeras(int iBanco, int iNoEmpresa) {
		List<LlenaComboChequeraDto> list = new ArrayList<LlenaComboChequeraDto>();
		try {
			list = coinversionDao.consultarChequeras2(iBanco, iNoEmpresa);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:llenarCmbChequeras");
		}
		return list;
	}

	public List<Double> consultarSaldoFinal2(int iBanco, String sChequera, int iEmpresa, int iEmpInv, String sDivisa) {
		List<Double> dSaldoFin = new ArrayList<Double>();
		List<CatCtaBancoDto> listDivisa = new ArrayList<CatCtaBancoDto>();
		List<Map<String, String>> saldos = new ArrayList<Map<String, String>>();
		try {
			saldos = coinversionDao.consultarSaldoFinal2(iBanco, sChequera, iEmpresa, iEmpInv, sDivisa);
			dSaldoFin.add(Double.parseDouble(saldos.get(0).get("saldoFinal")));
			dSaldoFin.add(Double.parseDouble(saldos.get(0).get("saldoCoinversion")));
			if (sDivisa.equals("")) {
				listDivisa = coinversionDao.consultaDivisa(iBanco, sChequera);
				sDivisa = listDivisa.get(0).getIdDivisa();
			}
			dSaldoFin.add(coinversionDao.consultarChequesPorEntregar(iEmpresa, iBanco, sDivisa, sChequera));
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:consultarSaldoFinal2");
		}
		return dSaldoFin;
	}

	/**
	 * Metodo que inserta en la tabla parametro y ejecuta el generador para
	 * registrar la solicitud de fondeo (traspaso) en la tabla movimiento
	 * 
	 * @param dtoParam
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> ejecutarSolicitudFondeo(ParamTraspasoCoinversionDto dtoParam) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		Map<String, Object> resGenerador = new HashMap<String, Object>();
		List<CatCtaBancoDto> listDivisa = new ArrayList<CatCtaBancoDto>();
		globalSingleton = GlobalSingleton.getInstancia();
		GeneradorDto generadorDto;
		ParametroDto paramInsert;

		int iTipoOperacion = 0;
		int iFolioParametro = 0;
		int iFolioParametroA = 0;
		int iCuenta = 0;
		int iBanco = 0;
		int iBancoBenef = 0;
		int iAfectados = 0;
		int a1 = 0;
		int a2 = 0;
		double dImporte = 0;
		String sDivisaDe = "";
		String sDivisaA = "";
		String sChequera = "";
		String sChequeraBenef = "";
		try {
			dImporte = dtoParam.getMontoTraspaso();
			listDivisa = coinversionDao.consultaDivisa(dtoParam.getIdBancoOrigen(), dtoParam.getIdChequeraOrigen());
			sDivisaDe = listDivisa.get(0).getIdDivisa();
			listDivisa = coinversionDao.consultaDivisa(dtoParam.getIdBancoDestino(), dtoParam.getIdChequeraDestino());
			sDivisaA = listDivisa.get(0).getIdDivisa();

			if (!sDivisaDe.trim().equals(sDivisaA.trim())) {
				mapResult.put("msgUsuario", "El traspaso no se puede realizar porque las divisas son diferentes");
				return mapResult;
			}

			iTipoOperacion = 3806;
			iFolioParametro = this.obtenerFolioReal("no_folio_param");
			if (iFolioParametro <= 0)
				return mapResult;

			iCuenta = coinversionDao.consultarCuentaEmpresa(dtoParam.getIdEmpresaDestino());
			if (iCuenta == 0) {
				mapResult.put("msgUsuario", "Esta filial no tiene la cuenta correcta");
				return mapResult;
			}

			sChequera = dtoParam.getIdChequeraOrigen();
			iBanco = dtoParam.getIdBancoOrigen();
			sChequeraBenef = dtoParam.getIdChequeraDestino();
			iBancoBenef = dtoParam.getIdBancoDestino();
			int tipo_sal = 0;
			
			if(dtoParam.getTipoSaldo() == 0)
			{
				tipo_sal = dtoParam.getTipoSaldo();
			}
			else
			{
			
				tipo_sal=coinversionDao.buscaCargo(dtoParam.getTipoSaldo());
			}
			
			
			paramInsert = new ParametroDto();
			paramInsert.setNoEmpresa(dtoParam.getIdEmpresaOrigen());
			paramInsert.setNoFolioParam(iFolioParametro);
			paramInsert.setAplica(1);
			paramInsert.setSecuencia(1);
			paramInsert.setIdTipoOperacion(iTipoOperacion);
			paramInsert.setCuenta(dtoParam.getIdEmpresaDestino());
			paramInsert.setIdEstatusMov("L");
			paramInsert.setIdChequera(sChequera);
			paramInsert.setIdBanco(iBanco);
			paramInsert.setImporte(dImporte);
			paramInsert.setBSBC("S");
			paramInsert.setFecValor(globalSingleton.getFechaHoy());
			paramInsert.setFecValorOriginal(globalSingleton.getFechaHoy());
			paramInsert.setIdEstatusReg("P");
			paramInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
			paramInsert.setFecValorAlta(globalSingleton.getFechaHoy());
			paramInsert.setIdDivisa(sDivisaA);
			paramInsert.setIdFormaPago(3);
			paramInsert.setImporteOriginal(dImporte);
			paramInsert.setFecOperacion(globalSingleton.getFechaHoy());
			paramInsert.setIdBancoBenef(iBancoBenef);
			paramInsert.setIdChequeraBenef(sChequeraBenef);
			paramInsert.setOrigenMov("SET");
			paramInsert.setConcepto(dtoParam.getConcepto());
			paramInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
			paramInsert.setNoFolioMov(0);
			paramInsert.setFolioRef(0);
			paramInsert.setIdGrupo(iFolioParametro);
			paramInsert.setNoCliente(dtoParam.getIdEmpresaDestino());
			paramInsert.setBeneficiario(dtoParam.getDescEmpresaDestino());
			paramInsert.setTipo_saldo(tipo_sal);
			
			iAfectados = 0;
			iAfectados = coinversionDao.insertarTraspaso(paramInsert);

			iFolioParametroA = this.obtenerFolioReal("no_folio_param");
			if (iFolioParametro <= 0)
				return mapResult;

			sChequera = dtoParam.getIdChequeraDestino();
			iBanco = dtoParam.getIdBancoDestino();
			sChequeraBenef = dtoParam.getIdChequeraOrigen();
			iBancoBenef = dtoParam.getIdBancoOrigen();
			dImporte = dtoParam.getMontoTraspaso();
			tipo_sal=0;
			
			paramInsert = new ParametroDto();
			paramInsert.setNoEmpresa(dtoParam.getIdEmpresaDestino());
			paramInsert.setNoFolioParam(iFolioParametroA);
			paramInsert.setAplica(1);
			paramInsert.setSecuencia(2);
			paramInsert.setIdTipoOperacion(iTipoOperacion);
			paramInsert.setCuenta(iCuenta);
			paramInsert.setIdEstatusMov("L");
			paramInsert.setIdChequera(sChequera);
			paramInsert.setIdBanco(iBanco);
			paramInsert.setImporte(dImporte);
			paramInsert.setBSBC("S");
			paramInsert.setFecValor(globalSingleton.getFechaHoy());
			paramInsert.setFecValorOriginal(globalSingleton.getFechaHoy());
			paramInsert.setIdEstatusReg("P");
			paramInsert.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
			paramInsert.setFecValorAlta(globalSingleton.getFechaHoy());
			paramInsert.setIdDivisa(sDivisaDe);
			paramInsert.setIdFormaPago(3);
			paramInsert.setImporteOriginal(dImporte);
			paramInsert.setFecOperacion(globalSingleton.getFechaHoy());
			paramInsert.setIdBancoBenef(iBancoBenef);
			paramInsert.setIdChequeraBenef(sChequeraBenef);
			paramInsert.setOrigenMov("SET");
			paramInsert.setConcepto(dtoParam.getConcepto());
			paramInsert.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
			paramInsert.setNoFolioMov(1);
			paramInsert.setFolioRef(1);
			paramInsert.setIdGrupo(iFolioParametro);
			paramInsert.setNoCliente(dtoParam.getIdEmpresaOrigen());
			paramInsert.setBeneficiario(dtoParam.getDescEmpresaOrigen());
			paramInsert.setTipo_saldo(tipo_sal);
			
			iAfectados = 0;
			iAfectados = coinversionDao.insertarTraspaso(paramInsert);

			generadorDto = new GeneradorDto();
			generadorDto.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
			generadorDto.setNomForma("SolicitudFondeo");
			generadorDto.setEmpresa(dtoParam.getIdEmpresaOrigen());
			generadorDto.setFolParam(iFolioParametro);
			generadorDto.setFolMovi(a1);
			generadorDto.setFolDeta(a2);
			generadorDto.setResult(0);
			generadorDto.setMensajeResp("inicia generador");

			resGenerador = coinversionDao.ejecutarGenerador(generadorDto);
			if (funciones.convertirCadenaInteger(resGenerador.get("result").toString()) != 0) {
				mapResult.put("msgUsuario", "Error en Generador en btnEjecutar " + resGenerador.get("result"));
				return mapResult;
			}

			mapResult.put("msgUsuario", "Datos Registrados");

		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:ejecutarSolicitudFondeo");
		}
		return mapResult;
	}

	/**
	 * consulta los intereses con los que se van hacer los calculos en pantalla
	 * 
	 * @param iEmpresa
	 * @param dIva
	 * @param sDivisa
	 * @param sFecha1
	 * @param sFecha2
	 * @return
	 */
	public List<InteresesDto> buscarIntereses(int iEmpresa, double dIva, String sDivisa, String sFecha1,
			String sFecha2) {
		List<InteresesDto> listInteres = new ArrayList<InteresesDto>();
		InteresesDto dtoInteres = new InteresesDto();
		List<Map<String, Object>> listInt;
		double dInteres = 0;
		double dInteresPar = 0;
		double dTasaRend = 0;
		double dIsr = 0;
		double dISRPar = 0;
		double dIntDevengado = 0;
		double dIsrDevengado = 0;
		int iLineaEmp = 0;
		Date dFechaIni = new Date();
		Date dFechaFin = new Date();
		try {
			iLineaEmp = this.obtenerLinea(sDivisa);
			dFechaIni = funciones.ponerFechaDate(sFecha1);
			dFechaFin = funciones.ponerFechaDate(sFecha2);

			if (iEmpresa > 0) {
				dInteres = coinversionDao.consultarInteres(iEmpresa, sDivisa, dFechaIni, dFechaFin);

				if (dInteres > 0)
					dInteresPar = dInteres;

				dInteres = 0;
				dInteres = coinversionDao.consultarInteres2(iEmpresa, sDivisa, dFechaIni, dFechaFin);

				listInt = new ArrayList<Map<String, Object>>();
				listInt = coinversionDao.consultarIntereses(iEmpresa, sDivisa, dFechaIni, dFechaFin);
				for (int i = 0; i < listInt.size(); i++) {
					dInteresPar = dInteresPar
							+ funciones.convertirCadenaDouble(listInt.get(i).get("intDev").toString());
				}

				listInt.clear();
				listInt = null;

				if (dInteres > 0) {
					dInteresPar = dInteresPar + dInteres;
					dtoInteres.setInteres(dInteresPar);
					// listInteres.get(0).setInteres(dInteresPar);
					dInteres = 0;
					dTasaRend = dInteresPar;
				} else {
					dInteres = 0;
					dtoInteres.setInteres(dInteresPar);
					dTasaRend = dInteresPar;
				}

				dtoInteres.setIva(dtoInteres.getInteres() * dIva / 100);

				dIsr = coinversionDao.consultarIsr(iEmpresa, sDivisa, dFechaIni, dFechaFin);
				if (dIsr > 0) {
					dISRPar = dIsr;
					dIsr = 0;
				}

				dIsr = 0;
				dIsr = coinversionDao.consultarIsr2(iEmpresa, sDivisa, dFechaIni, dFechaFin);
				if (dIsr > 0) {
					dISRPar = dISRPar + dIsr;
					dtoInteres.setIsr(dISRPar);
					dIsr = 0;
				} else {
					dIsr = 0;
					dtoInteres.setIsr(dISRPar);
				}

				// inicia calculo de interes devengado
				listInt = new ArrayList<Map<String, Object>>();
				listInt = coinversionDao.consultarInteresDevengado(iEmpresa, sDivisa, dFechaIni, dFechaFin);
				if (listInt.size() > 0) {
					for (int j = 0; j < listInt.size(); j++) {
						dIntDevengado = dIntDevengado
								+ Math.abs(funciones.convertirCadenaDouble(listInt.get(0).get("intDev").toString()));
						dIsrDevengado = dIsrDevengado
								+ Math.abs(funciones.convertirCadenaDouble(listInt.get(0).get("isrDev").toString()));
					}
					if (dIntDevengado < 0)
						dIntDevengado = Math.abs(dIntDevengado);
					if (dIsrDevengado < 0)
						dIsrDevengado = Math.abs(dIsrDevengado);

					dtoInteres.setInteresDev(dIntDevengado);
					dtoInteres.setIsrDev(dIsrDevengado);
					dtoInteres.setBCalculo(false);
					listInt.clear();
					listInt = null;
					dTasaRend = dTasaRend + dIntDevengado;
				} else {
					listInt.clear();
					listInt = null;
					dIntDevengado = 0;
					dIsrDevengado = 0;
					dtoInteres.setInteresDev(dIntDevengado);
					dtoInteres.setIsrDev(dIsrDevengado);
					dtoInteres.setBCalculo(true);
				}

				listInteres.add(dtoInteres);
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:buscarIntereses");
		}
		return listInteres;
	}

	/**
	 * consulta los porcentajes de iva, isr y comision
	 * 
	 * @param iEmpresa
	 * @param sDivisa
	 * @return
	 */
	public List<InteresesDto> consultarValoresSdoPromyCapitalizacion(int iEmpresa, String sDivisa) {
		List<InteresesDto> list = new ArrayList<InteresesDto>();
		try {
			list = coinversionDao.consultarValoresSdoPromyCapitalazacion(iEmpresa, sDivisa);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:consultarValoresSdoPromyCapitalizacion");
		}
		return list;
	}

	/**
	 * ConsultaFiliales llena grid y cajas de texto
	 * 
	 * @param dtoParams
	 * @return
	 */
	public List<InteresesDto> calcularInteresesPorDevengar(InteresesDto dtoParams) {
		List<InteresesDto> listRet = new ArrayList<InteresesDto>();
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		List<TmpSdoPromDto> listSaldo;

		Calendar calendarFecha1 = Calendar.getInstance();
		Calendar calendarFecha2 = Calendar.getInstance();
		DecimalFormat formateador;
		double uTasaIsr = 0;
		double uSumaSaldosProm = 0;
		double uFactorInt = 0;
		double uFactorImp = 0;
		double uTasaRend = 0;
		double uTasaDeterminada = 0;
		int iAnio = 0;
		int iDias = 0;
		int iLineaEmp = 0;
		int iAfectados = 0;
		int iUsuario = 0;
		int iContador;
		String sMes = "";
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			iUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			calendarFecha1.setTime(dtoParams.getFechaIni());
			calendarFecha2.setTime(dtoParams.getFechaFin());

			if (dtoParams.isBTasaPromedio()) {
				if (dtoParams.getDivisa().equals("MN"))
					uTasaIsr = coinversionDao.consultarTasaIsr();
				else
					uTasaIsr = 0;
				// Se obtiene la Tasa promedio
				iAnio = calendarFecha1.get(Calendar.YEAR);
				sMes = funciones.nombreMes(calendarFecha1.get(Calendar.MONTH));
				listMap = coinversionDao.consultarTasasInversion(iAnio, sMes, dtoParams.getDivisa());
				formateador = new DecimalFormat("####.###");
				uTasaDeterminada = Double.parseDouble(formateador.format(this.obtenerTasaBruta(listMap) - uTasaIsr));
				listMap = null;
			}

			iDias = funciones.diasEntreFechas(dtoParams.getFechaIni(), dtoParams.getFechaFin()) + 1;
			iLineaEmp = this.obtenerLinea(dtoParams.getDivisa());

			// borra la tabla temporal tmp_saldo
			iAfectados = 0;
			iAfectados = coinversionDao.eliminarTmpSdoProm(iUsuario);

			// inserta en la tabla temporal tmp_saldo
			iAfectados = 0;
			iAfectados = coinversionDao.insertarTmpSdoProm(iUsuario, dtoParams.getNoEmpresa(), iDias, iLineaEmp,
					dtoParams.getFechaIni(), dtoParams.getFechaFin());

			uSumaSaldosProm = 0;
			listSaldo = new ArrayList<TmpSdoPromDto>();
			listSaldo = coinversionDao.consultarTmpSdoProm(iUsuario);

			if (listSaldo.size() > 0)
				for (int i = 0; i < listSaldo.size(); i++) {
					uSumaSaldosProm = uSumaSaldosProm + listSaldo.get(i).getSaldoProm();
				}

			if (uSumaSaldosProm > 0) {
				formateador = new DecimalFormat("####.##########");
				uFactorInt = Double.parseDouble(formateador.format(dtoParams.getInteres() / uSumaSaldosProm));
				// dFactorInt = Math.round(dtoParams.getInteres() /
				// uSumaSaldosProm);
				uFactorImp = Double.parseDouble(formateador.format(dtoParams.getIsr() / uSumaSaldosProm));
				uTasaRend = ((uTasaRend / uSumaSaldosProm) / (iDias)) * 360;
			}

			listSaldo = null;

			uFactorImp = dtoParams.getDIsr() / 100; // este dIsr viene del boton
													// buscar NS.dIsr
			listSaldo = new ArrayList<TmpSdoPromDto>();
			listSaldo = coinversionDao.consultarTmpSdoProm(iUsuario);

			if (listSaldo.size() > 0) {
				iContador = 0;
				if (uSumaSaldosProm == 0)
					uSumaSaldosProm = 1;
				formateador = new DecimalFormat("####.##");

				for (; iContador < listSaldo.size(); iContador++) {
					// valores del grid
					InteresesDto dtoRet = new InteresesDto();
					dtoRet.setTasaDeterminada(uTasaDeterminada);

					dtoRet.setNoEmpresa(listSaldo.get(iContador).getNoEmpresa());
					dtoRet.setNomEmpresa(listSaldo.get(iContador).getDescEmpresa());
					dtoRet.setSaldoPromedio(listSaldo.get(iContador).getSaldoProm());
					dtoRet.setInteres(Double.parseDouble(formateador.format(listSaldo.get(iContador).getSaldoProm()
							* dtoRet.getTasaDeterminada() / 100 / 360 * iDias)));
					dtoRet.setIsr(Math
							.round(listSaldo.get(iContador).getSaldoProm() * dtoParams.getDIsr() / 100 / 360 * iDias));
					dtoRet.setInteresDev(0.00);
					dtoRet.setIsrVencido(0.00);
					dtoRet.setComision(dtoRet.getInteres() * (dtoParams.getDComision() / 100)); // este
																								// dIsr
																								// viene
																								// del
																								// boton
																								// buscar
																								// NS.dComision
					dtoRet.setIva(dtoRet.getInteres() * dtoParams.getDIva() / 100); // este
																					// dIsr
																					// viene
																					// del
																					// boton
																					// buscar
																					// NS.dIva
					dtoRet.setTotal(dtoRet.getInteres() - dtoRet.getIsr() - dtoRet.getComision()
							+ dtoRet.getInteresDev() + dtoRet.getIva());

					// valores de las cajas de texto

					dtoRet.setFactor(uFactorInt);
					dtoRet.setInteresT(dtoParams.getInteresT() + dtoRet.getInteres());
					dtoRet.setIsrT(dtoParams.getIsrT() + dtoRet.getIsr());
					dtoRet.setComisionT(dtoParams.getComisionT() + dtoRet.getComision());
					dtoRet.setInteresDevT(dtoParams.getInteresDevT() + dtoRet.getInteresDev());
					dtoRet.setIsrDevT(dtoParams.getIsrDevT() + dtoRet.getIsrDev());
					dtoRet.setIvaT(dtoParams.getIvaT() + dtoRet.getIva());

					listRet.add(dtoRet);

					iAfectados = 0;

					InteresesDto dto = new InteresesDto();
					dto.setInteresDev(listRet.get(iContador).getInteresDev());
					dto.setIsrDev(listRet.get(iContador).getIsrVencido());
					dto.setComision(listRet.get(iContador).getComision());
					dto.setIva(listRet.get(iContador).getIva());
					dto.setInteres(listRet.get(iContador).getInteres());
					dto.setIsr(listRet.get(iContador).getIsr());
					dto.setNoEmpresa(listRet.get(iContador).getNoEmpresa());

					iAfectados = coinversionDao.actualizarTmpSdoProm(dto, iUsuario);
				}
			}

			listSaldo.clear();
			listSaldo = null;

		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:calcularInteresesPorDevengar");
		}
		return listRet;
	}

	// FunSQLTasaBruta
	private double obtenerTasaBruta(List<Map<String, Object>> listMap) {
		double tasaBruta = 0;
		double tasa = 0;
		int plazo = 0;
		int iSumaD = 0;
		try {
			for (int i = 0; i < listMap.size(); i++) {
				iSumaD = iSumaD + funciones.convertirCadenaInteger(listMap.get(i).get("plazo2").toString());
			}
			for (int j = 0; j < listMap.size(); j++) {
				tasa = funciones.convertirCadenaDouble(listMap.get(j).get("tasa").toString());
				plazo = funciones.convertirCadenaInteger(listMap.get(j).get("plazo2").toString());
				tasaBruta = tasaBruta + (tasa / iSumaD * plazo);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:obtenerTasaBruta");
		}
		return tasaBruta;
	}

	/**
	 * proceso que realiza la capitalizacion de los saldos calculados
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> ejecutarCapitalizacion(List<InteresesDto> listGrid, InteresesDto dtoParam) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		List<CatCtaBancoDto> lCatBco;
		List<VencimientoInversionDto> lPeriodo = new ArrayList<VencimientoInversionDto>();
		Map<String, Object> resGenerador = new HashMap<String, Object>();
		ParametroDto dtoParametro;
		int iLineaEmp = 0;
		int iGrupo = 0;
		int iBancoCoinv = 0;
		int iBancoCoinvActual = 0;
		int iFolio = 0;
		int iFolio1 = 0;
		int iFolio2 = 0;
		int iFolio3 = 0;
		int iAfectados = 0;
		int iBancoEmp = 0;
		int iFolioMov = 0;
		int iFolioDet = 0;
		int iDias = 0;
		double dTasa = 0;
		double dSdoCredito;
		double dInteresTotal = 0;
		boolean bPrimero;
		boolean bHayParametros;
		boolean bCheqEmpCoi;
		String sChequeraCoinv = "";
		String sChequeraCoinvActual = "";
		String sChequeraEmp = "";
		try {
			globalSingleton = GlobalSingleton.getInstancia();

			lPeriodo = coinversionDao.consultarVencInversion(dtoParam.getNoEmpresa(), dtoParam.getDivisa(),
					dtoParam.getFechaIni(), dtoParam.getFechaFin());

			if (lPeriodo.size() > 0) {
				mensajes.add("Los intereses al periodo indicado ya estan calculados");
				mapResult.put("msgUsuario", mensajes);
				return mapResult;
			}

			iLineaEmp = this.obtenerLinea(dtoParam.getDivisa());
			bPrimero = false;
			iGrupo = this.obtenerFolioReal("no_folio_param");
			dTasa = dtoParam.getTasaDeterminada() / 100;

			if (globalSingleton.obtenerValorConfiguraSet(267).equals("SI")) {
				lCatBco = new ArrayList<CatCtaBancoDto>();
				lCatBco = coinversionDao.consultarChequerasTraspCoinv(dtoParam.getNoEmpresa(), dtoParam.getDivisa());
				if (lCatBco.size() > 0) {
					sChequeraCoinv = lCatBco.get(0).getIdChequera();
					iBancoCoinv = lCatBco.get(0).getIdBanco();
				} else {
					sChequeraCoinv = "";
					iBancoCoinv = 0;
				}
				lCatBco = null;

				if (sChequeraCoinv.equals("") || iBancoCoinv == 0) {
					mensajes.add("No existe una chequera predeterminada para Traspasos para esta empresa Coinversora");
					mapResult.put("msgUsuario", mensajes);
					return mapResult;
				}
			}

			bHayParametros = false;
			iBancoCoinvActual = iBancoCoinv;
			sChequeraCoinvActual = sChequeraCoinv;

			for (int i = 0; i < listGrid.size(); i++) {
				bCheqEmpCoi = false;
				if (!sChequeraCoinvActual.equals(sChequeraCoinv)) {
					iBancoCoinv = iBancoCoinvActual;
					sChequeraCoinv = sChequeraCoinvActual;
				}

				if (listGrid.get(i).getInteres() > 0) {
					if (globalSingleton.obtenerValorConfiguraSet(267).equals("SI")) {
						dtoParametro = new ParametroDto();
						if (bPrimero == true) {
							iFolio = this.obtenerFolioReal("no_folio_param");
							dtoParametro.setNoFolioParam(iFolio);
						} else
							dtoParametro.setNoFolioParam(iGrupo);

						dtoParametro.setNoEmpresa(dtoParam.getNoEmpresa());
						dtoParametro.setIdChequera("");
						dtoParametro.setIdTipoOperacion(7000);
						dtoParametro.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
						dtoParametro.setNoDocto(iGrupo);
						dtoParametro.setIdGrupo(iGrupo);
						dtoParametro.setNoFolioMov(0);// ""
						dtoParametro.setFolioRef(0);
						dtoParametro.setFecValor(globalSingleton.getFechaHoy());
						dtoParametro.setFecValorOriginal(globalSingleton.getFechaHoy());
						dtoParametro.setCuenta(listGrid.get(i).getNoEmpresa());
						dtoParametro.setFecOperacion(globalSingleton.getFechaHoy());
						dtoParametro.setFecValorAlta(globalSingleton.getFechaHoy());
						dtoParametro.setImporte(listGrid.get(i).getInteres() + listGrid.get(i).getIva());
						dtoParametro.setValorTasa(dTasa);
						dtoParametro.setImporteOriginal(listGrid.get(i).getInteres() + listGrid.get(i).getIva());
						dtoParametro.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
						dtoParametro.setIdDivisa(dtoParam.getDivisa());
						dtoParametro.setAplica(1);
						dtoParametro.setIdEstatusMov("L");
						dtoParametro.setIdChequeraBenef("");
						dtoParametro.setSecuencia(0);// ""
						dtoParametro.setConcepto(
								"INTERES CAPITALIZADO " + dtoParam.getFechaIni() + "-" + dtoParam.getFechaFin());
						dtoParametro.setOrigenMov("SET");

						if (dtoParametro.getOrigenMov().equals(""))
							dtoParametro.setOrigenMov(IS_ORIGEN_MOV);

						iAfectados = 0;
						iAfectados = coinversionDao.insertaCapitalizacion(dtoParametro);

						bPrimero = true;
						iFolio1 = this.obtenerFolioReal("no_foio_param");

						dtoParametro = new ParametroDto();

						dtoParametro.setNoFolioParam(iFolio1);
						dtoParametro.setNoEmpresa(dtoParam.getNoEmpresa());
						dtoParametro.setIdChequera("");
						dtoParametro.setIdTipoOperacion(7001);
						dtoParametro.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
						dtoParametro.setNoDocto(iGrupo);
						dtoParametro.setIdGrupo(iGrupo);
						dtoParametro.setNoFolioMov(0);// ""
						dtoParametro.setFolioRef(0);
						dtoParametro.setFecValor(globalSingleton.getFechaHoy());
						dtoParametro.setFecValorOriginal(globalSingleton.getFechaHoy());
						dtoParametro.setCuenta(listGrid.get(i).getNoEmpresa());
						dtoParametro.setFecOperacion(globalSingleton.getFechaHoy());
						dtoParametro.setFecValorAlta(globalSingleton.getFechaHoy());
						dtoParametro.setImporte(listGrid.get(i).getIsr());
						dtoParametro.setValorTasa(dTasa);
						dtoParametro.setImporteOriginal(listGrid.get(i).getIsr());
						dtoParametro.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
						dtoParametro.setIdDivisa(dtoParam.getDivisa());
						dtoParametro.setAplica(1);
						dtoParametro.setIdEstatusMov("L");
						dtoParametro.setIdChequeraBenef("");
						dtoParametro.setSecuencia(0);// ""
						dtoParametro.setConcepto(
								"ISR CAPITALIZADO " + dtoParam.getFechaIni() + "-" + dtoParam.getFechaFin());
						dtoParametro.setOrigenMov("SET");

						iAfectados = 0;
						iAfectados = coinversionDao.insertaCapitalizacion(dtoParametro);

						iFolio2 = this.obtenerFolioReal("no_foio_param");

						dtoParametro = new ParametroDto();

						dtoParametro.setNoFolioParam(iFolio2);
						dtoParametro.setNoEmpresa(dtoParam.getNoEmpresa());
						dtoParametro.setIdChequera("");
						dtoParametro.setIdTipoOperacion(7002);
						dtoParametro.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
						dtoParametro.setNoDocto(iGrupo);
						dtoParametro.setIdGrupo(iGrupo);
						dtoParametro.setNoFolioMov(0);// ""
						dtoParametro.setFolioRef(0);
						dtoParametro.setFecValor(globalSingleton.getFechaHoy());
						dtoParametro.setFecValorOriginal(globalSingleton.getFechaHoy());
						dtoParametro.setCuenta(listGrid.get(i).getNoEmpresa());
						dtoParametro.setFecOperacion(globalSingleton.getFechaHoy());
						dtoParametro.setFecValorAlta(globalSingleton.getFechaHoy());
						dtoParametro.setImporte(listGrid.get(i).getComision());
						dtoParametro.setValorTasa(dTasa);
						dtoParametro.setImporteOriginal(listGrid.get(i).getComision());
						dtoParametro.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
						dtoParametro.setIdDivisa(dtoParam.getDivisa());
						dtoParametro.setAplica(1);
						dtoParametro.setIdEstatusMov("L");
						dtoParametro.setIdChequeraBenef("");
						dtoParametro.setSecuencia(0);// ""
						dtoParametro.setConcepto(
								"COMISION POR COINV. " + dtoParam.getFechaIni() + "-" + dtoParam.getFechaFin());
						dtoParametro.setOrigenMov("SET");

						iAfectados = 0;
						iAfectados = coinversionDao.insertaCapitalizacion(dtoParametro);

						iFolio3 = this.obtenerFolioReal("no_foio_param");

						dtoParametro = new ParametroDto();

						dtoParametro.setNoFolioParam(iFolio3);
						dtoParametro.setNoEmpresa(dtoParam.getNoEmpresa());
						dtoParametro.setIdChequera("");
						dtoParametro.setIdTipoOperacion(7004);
						dtoParametro.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
						dtoParametro.setNoDocto(iGrupo);
						dtoParametro.setIdGrupo(iGrupo);
						dtoParametro.setNoFolioMov(0);// ""
						dtoParametro.setFolioRef(0);
						dtoParametro.setFecValor(globalSingleton.getFechaHoy());
						dtoParametro.setFecValorOriginal(globalSingleton.getFechaHoy());
						dtoParametro.setCuenta(listGrid.get(i).getNoEmpresa());
						dtoParametro.setFecOperacion(globalSingleton.getFechaHoy());
						dtoParametro.setFecValorAlta(globalSingleton.getFechaHoy());
						dtoParametro.setImporte(listGrid.get(i).getInteresDev());
						dtoParametro.setValorTasa(dTasa);
						dtoParametro.setImporteOriginal(listGrid.get(i).getInteresDev());
						dtoParametro.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
						dtoParametro.setIdDivisa(dtoParam.getDivisa());
						dtoParametro.setAplica(1);
						dtoParametro.setIdEstatusMov("L");
						dtoParametro.setIdChequeraBenef("");
						dtoParametro.setSecuencia(0);// ""
						dtoParametro.setConcepto(
								"INTERES DEVENGADO " + dtoParam.getFechaIni() + "-" + dtoParam.getFechaFin());
						dtoParametro.setOrigenMov("SET");

						iAfectados = 0;
						iAfectados = coinversionDao.insertaCapitalizacion(dtoParametro);

						bHayParametros = true;
					} else {
						/**
						 * si no capitaliza debe generar los traspasos para
						 * pasar los intereses por bancos 1. debe generar una
						 * 3801 que es el pago de los intereses de la mama a la
						 * hija. 2. si saldo de prestamos > 0 entonces si su
						 * saldo de prestamos es >= pago entonces el pago de
						 * credito (3809) = pago si el saldo de prestamo < pago
						 * enteonces el pago de credito (3809) = saldo de
						 * prestamo y un barrido (3805) = pago - saldo de
						 * prestamo si el saldo de prestamos <= 0 entonces un
						 * barrido (3805) = pago
						 **/

						lCatBco = new ArrayList<CatCtaBancoDto>();
						lCatBco = coinversionDao.consultarChequerasConcEmp(listGrid.get(i).getNoEmpresa(),
								dtoParam.getDivisa());

						if (lCatBco.size() > 0) {
							sChequeraEmp = lCatBco.get(0).getIdChequera();
							iBancoEmp = lCatBco.get(0).getIdBanco();
						} else {
							sChequeraEmp = "";
							iBancoEmp = 0;
						}

						lCatBco = null;
						// Valida si el mismo banco, de lo contrario intenta una
						// chequera mismo banco
						if (iBancoEmp != iBancoCoinv) {
							if (!sChequeraEmp.equals("") && iBancoEmp != 0) {
								lCatBco = new ArrayList<CatCtaBancoDto>();
								lCatBco = coinversionDao.consultarCheqCoinvMismoBanco(dtoParam.getNoEmpresa(),
										dtoParam.getDivisa(), iBancoEmp);

								if (lCatBco.size() > 0) {
									sChequeraCoinv = lCatBco.get(0).getIdChequera();
									iBancoCoinv = lCatBco.get(0).getIdBanco();
								}

								lCatBco = null;
							}
						}

						if (sChequeraEmp.equals("") || iBancoEmp == 0) {
							mensajes.add("No existe una chequera concentradora del banco " + iBancoCoinv
									+ " para la empresa " + listGrid.get(i).getNoEmpresa()
									+ " en la divisa seleccionada");
							mapResult.put("msgUsuario", mensajes);
						} else {
							dtoParametro = new ParametroDto();
							if (bPrimero == true) {
								iFolio = this.obtenerFolioReal("no_folio_param");
								dtoParametro.setNoFolioParam(iFolio);
							} else {
								dtoParametro.setNoFolioParam(iGrupo);
								bPrimero = true;
							}

							dtoParametro.setNoEmpresa(dtoParam.getNoEmpresa());
							dtoParametro.setAplica(1);
							dtoParametro.setSecuencia(1);
							dtoParametro.setIdTipoOperacion(3801);
							dtoParametro.setCuenta(coinversionDao.consultarCuentaEmpresa(dtoParam.getNoEmpresa()));
							dtoParametro.setIdEstatusMov("L");
							dtoParametro.setIdChequera(sChequeraCoinv);
							dtoParametro.setIdBanco(iBancoCoinv);
							dtoParametro.setImporte(listGrid.get(i).getInteres() + listGrid.get(i).getIva());
							dtoParametro.setBSBC("S");
							dtoParametro.setFecValor(globalSingleton.getFechaHoy());
							dtoParametro.setIdEstatusReg("P");
							dtoParametro.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
							dtoParametro.setIdDivisa(dtoParam.getDivisa());
							dtoParametro.setIdFormaPago(3);
							dtoParametro.setIdBancoBenef(iBancoEmp);
							dtoParametro.setIdChequeraBenef(sChequeraEmp);
							dtoParametro.setOrigenMov("SET");
							dtoParametro.setConcepto("1. INTERES COINVERSION, PAGO DE INTERESES");
							dtoParametro.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
							dtoParametro.setNoFolioMov(0);
							dtoParametro.setFolioRef(0);
							dtoParametro.setIdGrupo(iGrupo);
							dtoParametro.setNoCliente(listGrid.get(i).getNoEmpresa());
							dtoParametro.setBeneficiario(listGrid.get(i).getNomEmpresa());

							iAfectados = 0;
							iAfectados = coinversionDao.insertarParametro(dtoParametro);

							dtoParametro = new ParametroDto();
							iFolio = this.obtenerFolioReal("no_folio_param");

							dtoParametro.setNoFolioParam(iFolio);
							dtoParametro.setNoEmpresa(listGrid.get(i).getNoEmpresa());
							dtoParametro.setAplica(1);
							dtoParametro.setSecuencia(2);
							dtoParametro.setIdTipoOperacion(3801);
							dtoParametro
									.setCuenta(coinversionDao.consultarCuentaEmpresa(listGrid.get(i).getNoEmpresa()));
							dtoParametro.setIdEstatusMov("L");
							dtoParametro.setIdChequera(sChequeraEmp);
							dtoParametro.setIdBanco(iBancoEmp);
							dtoParametro.setImporte(listGrid.get(i).getInteres() + listGrid.get(i).getIva());
							dtoParametro.setBSBC("S");
							dtoParametro.setFecValor(globalSingleton.getFechaHoy());
							dtoParametro.setIdEstatusReg("P");
							dtoParametro.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
							dtoParametro.setIdDivisa(dtoParam.getDivisa());
							dtoParametro.setIdFormaPago(3);
							dtoParametro.setIdBancoBenef(iBancoCoinv);
							dtoParametro.setIdChequeraBenef(sChequeraCoinv);
							dtoParametro.setOrigenMov("SET");
							dtoParametro.setConcepto("1. INTERES COINVERSION, PAGO DE INTERESES");
							dtoParametro.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
							dtoParametro.setNoFolioMov(1);
							dtoParametro.setFolioRef(1);
							dtoParametro.setIdGrupo(iGrupo);
							dtoParametro.setNoCliente(dtoParam.getNoEmpresa());
							dtoParametro.setBeneficiario(dtoParam.getNomEmpresa());

							iAfectados = 0;
							iAfectados = coinversionDao.insertarParametro(dtoParametro);
							dSdoCredito = 0;

							dSdoCredito = coinversionDao.consultarSaldoCredito(listGrid.get(i).getNoEmpresa(),
									dtoParam.getDivisa());

							if (dSdoCredito > 0) {
								if (dSdoCredito >= (listGrid.get(i).getInteres() + listGrid.get(i).getIva())) {
									dtoParametro = new ParametroDto();
									iFolio = this.obtenerFolioReal("no_folio_param");

									dtoParametro.setNoFolioParam(iFolio);
									dtoParametro.setNoEmpresa(listGrid.get(i).getNoEmpresa());
									dtoParametro.setAplica(1);
									dtoParametro.setSecuencia(1);
									dtoParametro.setIdTipoOperacion(3809);
									dtoParametro.setCuenta(
											coinversionDao.consultarCuentaEmpresa(listGrid.get(i).getNoEmpresa()));
									dtoParametro.setIdEstatusMov("L");
									dtoParametro.setIdChequera(sChequeraEmp);
									dtoParametro.setIdBanco(iBancoEmp);
									dtoParametro.setImporte(listGrid.get(i).getInteres() + listGrid.get(i).getIva());
									dtoParametro.setBSBC("S");
									dtoParametro.setFecValor(globalSingleton.getFechaHoy());
									dtoParametro.setIdEstatusReg("P");
									dtoParametro.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
									dtoParametro.setIdDivisa(dtoParam.getDivisa());
									dtoParametro.setIdFormaPago(3);
									dtoParametro.setIdBancoBenef(iBancoCoinv);
									dtoParametro.setIdChequeraBenef(sChequeraCoinv);
									dtoParametro.setOrigenMov("SET");
									dtoParametro.setConcepto("2. INTERES COINVERSION, PAGO DE CREDITO");
									dtoParametro.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
									dtoParametro.setNoFolioMov(0);
									dtoParametro.setFolioRef(0);
									dtoParametro.setIdGrupo(iGrupo);
									dtoParametro.setNoCliente(dtoParam.getNoEmpresa());
									dtoParametro.setBeneficiario(dtoParam.getNomEmpresa());

									iAfectados = 0;
									iAfectados = coinversionDao.insertarParametro(dtoParametro);

									dtoParametro = new ParametroDto();
									iFolio = this.obtenerFolioReal("no_folio_param");

									dtoParametro.setNoFolioParam(iFolio);
									dtoParametro.setNoEmpresa(dtoParam.getNoEmpresa());
									dtoParametro.setAplica(1);
									dtoParametro.setSecuencia(2);
									dtoParametro.setIdTipoOperacion(3809);
									dtoParametro
											.setCuenta(coinversionDao.consultarCuentaEmpresa(dtoParam.getNoEmpresa()));
									dtoParametro.setIdEstatusMov("L");
									dtoParametro.setIdChequera(sChequeraCoinv);
									dtoParametro.setIdBanco(iBancoCoinv);
									dtoParametro.setImporte(listGrid.get(i).getInteres() + listGrid.get(i).getIva());
									dtoParametro.setBSBC("S");
									dtoParametro.setFecValor(globalSingleton.getFechaHoy());
									dtoParametro.setIdEstatusReg("P");
									dtoParametro.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
									dtoParametro.setIdDivisa(dtoParam.getDivisa());
									dtoParametro.setIdFormaPago(3);
									dtoParametro.setIdBancoBenef(iBancoEmp);
									dtoParametro.setIdChequeraBenef(sChequeraEmp);
									dtoParametro.setOrigenMov("SET");
									dtoParametro.setConcepto("2. INTERES COINVERSION, PAGO DE CREDITO");
									dtoParametro.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
									dtoParametro.setNoFolioMov(1);
									dtoParametro.setFolioRef(1);
									dtoParametro.setIdGrupo(iGrupo);
									dtoParametro.setNoCliente(listGrid.get(i).getNoEmpresa());
									dtoParametro.setBeneficiario(listGrid.get(i).getNomEmpresa());

									iAfectados = 0;
									iAfectados = coinversionDao.insertarParametro(dtoParametro);
								} else {
									dtoParametro = new ParametroDto();
									iFolio = this.obtenerFolioReal("no_folio_param");

									dtoParametro.setNoFolioParam(iFolio);
									dtoParametro.setNoEmpresa(listGrid.get(i).getNoEmpresa());
									dtoParametro.setAplica(1);
									dtoParametro.setSecuencia(1);
									dtoParametro.setIdTipoOperacion(3809);
									dtoParametro.setCuenta(
											coinversionDao.consultarCuentaEmpresa(listGrid.get(i).getNoEmpresa()));
									dtoParametro.setIdEstatusMov("L");
									dtoParametro.setIdChequera(sChequeraEmp);
									dtoParametro.setIdBanco(iBancoEmp);
									dtoParametro.setImporte(dSdoCredito);
									dtoParametro.setBSBC("S");
									dtoParametro.setFecValor(globalSingleton.getFechaHoy());
									dtoParametro.setIdEstatusReg("P");
									dtoParametro.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
									dtoParametro.setIdDivisa(dtoParam.getDivisa());
									dtoParametro.setIdFormaPago(3);
									dtoParametro.setIdBancoBenef(iBancoCoinv);
									dtoParametro.setIdChequeraBenef(sChequeraCoinv);
									dtoParametro.setOrigenMov("SET");
									dtoParametro.setConcepto("2. INTERES COINVERSION, PAGO DE CREDITO");
									dtoParametro.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
									dtoParametro.setNoFolioMov(0);
									dtoParametro.setFolioRef(0);
									dtoParametro.setIdGrupo(iGrupo);
									dtoParametro.setNoCliente(dtoParam.getNoEmpresa());
									dtoParametro.setBeneficiario(dtoParam.getNomEmpresa());

									iAfectados = 0;
									iAfectados = coinversionDao.insertarParametro(dtoParametro);

									dtoParametro = new ParametroDto();
									iFolio = this.obtenerFolioReal("no_folio_param");

									dtoParametro.setNoFolioParam(iFolio);
									dtoParametro.setNoEmpresa(dtoParam.getNoEmpresa());
									dtoParametro.setAplica(1);
									dtoParametro.setSecuencia(2);
									dtoParametro.setIdTipoOperacion(3809);
									dtoParametro
											.setCuenta(coinversionDao.consultarCuentaEmpresa(dtoParam.getNoEmpresa()));
									dtoParametro.setIdEstatusMov("L");
									dtoParametro.setIdChequera(sChequeraCoinv);
									dtoParametro.setIdBanco(iBancoCoinv);
									dtoParametro.setImporte(dSdoCredito);
									dtoParametro.setBSBC("S");
									dtoParametro.setFecValor(globalSingleton.getFechaHoy());
									dtoParametro.setIdEstatusReg("P");
									dtoParametro.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
									dtoParametro.setIdDivisa(dtoParam.getDivisa());
									dtoParametro.setIdFormaPago(3);
									dtoParametro.setIdBancoBenef(iBancoEmp);
									dtoParametro.setIdChequeraBenef(sChequeraEmp);
									dtoParametro.setOrigenMov("SET");
									dtoParametro.setConcepto("2. INTERES COINVERSION, PAGO DE CREDITO");
									dtoParametro.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
									dtoParametro.setNoFolioMov(1);
									dtoParametro.setFolioRef(1);
									dtoParametro.setIdGrupo(iGrupo);
									dtoParametro.setNoCliente(listGrid.get(i).getNoEmpresa());
									dtoParametro.setBeneficiario(listGrid.get(i).getNomEmpresa());

									iAfectados = 0;
									iAfectados = coinversionDao.insertarParametro(dtoParametro);

									dtoParametro = new ParametroDto();
									iFolio = this.obtenerFolioReal("no_folio_param");

									dtoParametro.setNoFolioParam(iFolio);
									dtoParametro.setNoEmpresa(listGrid.get(i).getNoEmpresa());
									dtoParametro.setAplica(1);
									dtoParametro.setSecuencia(1);
									dtoParametro.setIdTipoOperacion(3805);
									dtoParametro.setCuenta(
											coinversionDao.consultarCuentaEmpresa(listGrid.get(i).getNoEmpresa()));
									dtoParametro.setIdEstatusMov("L");
									dtoParametro.setIdChequera(sChequeraEmp);
									dtoParametro.setIdBanco(iBancoEmp);
									dtoParametro.setImporte(
											listGrid.get(i).getInteres() + listGrid.get(i).getIva() - dSdoCredito);
									dtoParametro.setBSBC("S");
									dtoParametro.setFecValor(globalSingleton.getFechaHoy());
									dtoParametro.setIdEstatusReg("P");
									dtoParametro.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
									dtoParametro.setIdDivisa(dtoParam.getDivisa());
									dtoParametro.setIdFormaPago(3);
									dtoParametro.setIdBancoBenef(iBancoCoinv);
									dtoParametro.setIdChequeraBenef(sChequeraCoinv);
									dtoParametro.setOrigenMov("SET");
									dtoParametro.setConcepto("3. INTERES COINVERSION, BARRIDO");
									dtoParametro.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
									dtoParametro.setNoFolioMov(0);
									dtoParametro.setFolioRef(0);
									dtoParametro.setIdGrupo(iGrupo);
									dtoParametro.setNoCliente(dtoParam.getNoEmpresa());
									dtoParametro.setBeneficiario(dtoParam.getNomEmpresa());

									iAfectados = 0;
									iAfectados = coinversionDao.insertarParametro(dtoParametro);

									dtoParametro = new ParametroDto();
									iFolio = this.obtenerFolioReal("no_folio_param");

									dtoParametro.setNoFolioParam(iFolio);
									dtoParametro.setNoEmpresa(dtoParam.getNoEmpresa());
									dtoParametro.setAplica(1);
									dtoParametro.setSecuencia(2);
									dtoParametro.setIdTipoOperacion(3805);
									dtoParametro
											.setCuenta(coinversionDao.consultarCuentaEmpresa(dtoParam.getNoEmpresa()));
									dtoParametro.setIdEstatusMov("L");
									dtoParametro.setIdChequera(sChequeraCoinv);
									dtoParametro.setIdBanco(iBancoCoinv);
									dtoParametro.setImporte(
											listGrid.get(i).getInteres() + listGrid.get(i).getIva() - dSdoCredito);
									dtoParametro.setBSBC("S");
									dtoParametro.setFecValor(globalSingleton.getFechaHoy());
									dtoParametro.setIdEstatusReg("P");
									dtoParametro.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
									dtoParametro.setIdDivisa(dtoParam.getDivisa());
									dtoParametro.setIdFormaPago(3);
									dtoParametro.setIdBancoBenef(iBancoEmp);
									dtoParametro.setIdChequeraBenef(sChequeraEmp);
									dtoParametro.setOrigenMov("SET");
									dtoParametro.setConcepto("3. INTERES COINVERSION, BARRIDO");
									dtoParametro.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
									dtoParametro.setNoFolioMov(1);
									dtoParametro.setFolioRef(1);
									dtoParametro.setIdGrupo(iGrupo);
									dtoParametro.setNoCliente(listGrid.get(i).getNoEmpresa());
									dtoParametro.setBeneficiario(listGrid.get(i).getNomEmpresa());

									iAfectados = 0;
									iAfectados = coinversionDao.insertarParametro(dtoParametro);
								}
							} else {
								dtoParametro = new ParametroDto();
								iFolio = this.obtenerFolioReal("no_folio_param");

								dtoParametro.setNoFolioParam(iFolio);
								dtoParametro.setNoEmpresa(listGrid.get(i).getNoEmpresa());
								dtoParametro.setAplica(1);
								dtoParametro.setSecuencia(1);
								dtoParametro.setIdTipoOperacion(3805);
								dtoParametro.setCuenta(
										coinversionDao.consultarCuentaEmpresa(listGrid.get(i).getNoEmpresa()));
								dtoParametro.setIdEstatusMov("L");
								dtoParametro.setIdChequera(sChequeraEmp);
								dtoParametro.setIdBanco(iBancoEmp);
								dtoParametro.setImporte(listGrid.get(i).getInteres() + listGrid.get(i).getIva());
								dtoParametro.setBSBC("S");
								dtoParametro.setFecValor(globalSingleton.getFechaHoy());
								dtoParametro.setIdEstatusReg("P");
								dtoParametro.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
								dtoParametro.setIdDivisa(dtoParam.getDivisa());
								dtoParametro.setIdFormaPago(3);
								dtoParametro.setIdBancoBenef(iBancoCoinv);
								dtoParametro.setIdChequeraBenef(sChequeraCoinv);
								dtoParametro.setOrigenMov("SET");
								dtoParametro.setConcepto("3. INTERES COINVERSION, BARRIDO");
								dtoParametro.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
								dtoParametro.setNoFolioMov(0);
								dtoParametro.setFolioRef(0);
								dtoParametro.setIdGrupo(iGrupo);
								dtoParametro.setNoCliente(dtoParam.getNoEmpresa());
								dtoParametro.setBeneficiario(dtoParam.getNomEmpresa());

								iAfectados = 0;
								iAfectados = coinversionDao.insertarParametro(dtoParametro);

								dtoParametro = new ParametroDto();
								iFolio = this.obtenerFolioReal("no_folio_param");

								dtoParametro.setNoFolioParam(iFolio);
								dtoParametro.setNoEmpresa(dtoParam.getNoEmpresa());
								dtoParametro.setAplica(1);
								dtoParametro.setSecuencia(2);
								dtoParametro.setIdTipoOperacion(3805);
								dtoParametro.setCuenta(listGrid.get(i).getNoEmpresa());
								dtoParametro.setIdEstatusMov("L");
								dtoParametro.setIdChequera(sChequeraCoinv);
								dtoParametro.setIdBanco(iBancoCoinv);
								dtoParametro.setImporte(listGrid.get(i).getInteres() + listGrid.get(i).getIva());
								dtoParametro.setBSBC("S");
								dtoParametro.setFecValor(globalSingleton.getFechaHoy());
								dtoParametro.setIdEstatusReg("P");
								dtoParametro.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
								dtoParametro.setIdDivisa(dtoParam.getDivisa());
								dtoParametro.setIdFormaPago(3);
								dtoParametro.setIdBancoBenef(iBancoEmp);
								dtoParametro.setIdChequeraBenef(sChequeraEmp);
								dtoParametro.setOrigenMov("SET");
								dtoParametro.setConcepto("3. INTERES COINVERSION, BARRIDO");
								dtoParametro.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
								dtoParametro.setNoFolioMov(1);
								dtoParametro.setFolioRef(1);
								dtoParametro.setIdGrupo(iGrupo);
								dtoParametro.setNoCliente(listGrid.get(i).getNoEmpresa());
								dtoParametro.setBeneficiario(listGrid.get(i).getNomEmpresa());

								iAfectados = 0;
								iAfectados = coinversionDao.insertarParametro(dtoParametro);
							}
							bHayParametros = true;
						}
					}

					iDias = funciones.diasEntreFechas(dtoParam.getFechaIni(), dtoParam.getFechaFin()) + 1;
					dInteresTotal = listGrid.get(i).getInteres() + listGrid.get(i).getInteresDev();

					VencimientoInversionDto dto = new VencimientoInversionDto();

					dto.setNoControladora(dtoParam.getNoEmpresa());
					dto.setNoEmpresa(listGrid.get(i).getNoEmpresa());
					dto.setFecDesde(dtoParam.getFechaIni());
					dto.setFecHasta(dtoParam.getFechaFin());
					dto.setInteres(listGrid.get(i).getInteres());
					dto.setInteresDev(listGrid.get(i).getInteresDev());
					dto.setIsr(listGrid.get(i).getIsr());
					dto.setComision(listGrid.get(i).getComision());
					dto.setTasaRend(dtoParam.getTasaDeterminada());
					dto.setIdDivisa(dtoParam.getDivisa());
					dto.setSaldoPromedio(listGrid.get(i).getSaldoPromedio());
					dto.setIva(listGrid.get(i).getIva());
					iAfectados = coinversionDao.insertarVencimientoInversion(dto);
				}
			}

			if (bHayParametros) {
				GeneradorDto generadorDto = new GeneradorDto();
				generadorDto.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
				generadorDto.setNomForma("SolicitudFondeo");
				generadorDto.setEmpresa(dtoParam.getNoEmpresa());
				generadorDto.setFolParam(iGrupo);
				generadorDto.setFolMovi(iFolioMov);
				generadorDto.setFolDeta(iFolioDet);
				generadorDto.setResult(0);
				generadorDto.setMensajeResp("inicia generador");

				resGenerador = coinversionDao.ejecutarGenerador(generadorDto);
				if (funciones.convertirCadenaInteger(resGenerador.get("result").toString()) != 0) {
					mensajes.add("Error en Generador en btnEjecutar " + resGenerador.get("result"));
					mapResult.put("msgUsuario", mensajes);
					return mapResult;
				} else {
					mensajes.add("Datos Registrados");
					/**
					 * If (gobjVarGlobal.valor_Configura_set(267) <> "SI") Then
					 * ' Imprimir el reporte de traspasos solicitados
					 * imprime_reporte CmbDivisa.Text Else imprimirtras End If
					 */
					mapResult.put("msgUsuario", mensajes);
					return mapResult;
				}
			} else {
				mensajes.add("No se registraron movimientos");
				mapResult.put("msgUsuario", mensajes);
				return mapResult;
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:ejecutarCapitalizacion");
		}
		return mapResult;
	}

	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteSaldoPromedio(Map parameters) {
		JRMapArrayDataSource jrDataSource = null;
		XStream xStream = new XStream(new DomDriver());
		List<Map<String, Object>> resMap = null;
		int iUsuario = 0;
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			iUsuario = Integer.parseInt(parameters.get("usuario").toString());
			resMap = coinversionDao.consultarReporteSaldoPromedio(iUsuario);

			xStream.alias("mapa cheque", java.util.List.class);
			jrDataSource = new JRMapArrayDataSource(resMap.toArray());

		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:obtenerReporteSaldoPromedio");
		}
		return jrDataSource;
	}

	public List<OrdenInversionDto> llenarCmbAnios() {
		List<OrdenInversionDto> anios = new ArrayList<OrdenInversionDto>();
		try {
			anios = coinversionDao.consultarAnio();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:llenarCmbAnios");
		}
		return anios;
	}

	public List<OrdenInversionDto> llenarCmbMeses(int iAnio) {
		List<OrdenInversionDto> meses = new ArrayList<OrdenInversionDto>();
		try {
			meses = coinversionDao.consultarMes(iAnio);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:llenarCmbMeses");
		}
		return meses;
	}

	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteTasasInversion(Map parameters) {
		JRMapArrayDataSource jrDataSource = null;
		XStream xStream = new XStream(new DomDriver());
		List<Map<String, Object>> tasaInversion = null;
		Map<String, Object> tasas = new HashMap();
		double dTasaIsr = 0;
		double dTasaBruta = 0;
		double dTasaNeta = 0;
		int iAnio = 0;
		String sMes = "";
		String sDivisa = "";
		try {
			iAnio = Integer.parseInt(parameters.get("anio").toString());
			sMes = parameters.get("nomMes").toString();
			sDivisa = parameters.get("divisa").toString();

			if (sDivisa.equals("MN"))
				dTasaIsr = coinversionDao.consultarTasaIsr();
			else
				dTasaIsr = 0;

			tasaInversion = coinversionDao.consultarTasasInversion(iAnio, sMes, sDivisa);
			if (tasaInversion.size() > 0) {
				dTasaBruta = this.obtenerTasaBruta(tasaInversion);
				dTasaNeta = dTasaBruta - dTasaIsr;
			}

			tasas.put("dTasaBruta", dTasaBruta);
			tasas.put("dTasaNeta", dTasaNeta);
			tasas.put("dTasaIsr", dTasaIsr);
			tasaInversion.add(tasas);

			xStream.alias("mapa cheque", java.util.List.class);
			jrDataSource = new JRMapArrayDataSource(tasaInversion.toArray());

		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:obtenerReporteTasasInversion");
		}
		return jrDataSource;
	}

	public List<EmpresaDto> llenarGridEmpresasCoinv(int iEmpresa, String sDivisa) {
		List<EmpresaDto> list = new ArrayList<EmpresaDto>();
		int iUsuario = 0;
		int iLinea = 0;
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			iUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			iLinea = this.obtenerLinea(sDivisa);
			list = coinversionDao.consultarEmpresasCoinv(iUsuario, iEmpresa, iLinea);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:llenarGridEmpresasCoinv");
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteEstadodeCuenta(Map parameters) {
		Gson gson = new Gson();
		List<Map<String, String>> paramsGrid = null;
		JRMapArrayDataSource jrDataSource = null;
		XStream xStream = new XStream(new DomDriver());
		List<Map<String, Object>> listMapReporte = null;
		List<Map<String, Object>> datosEmpresa = null;
		List<HistSaldoDto> listSaldo = new ArrayList<HistSaldoDto>();
		Map<String, Object> mapDatos = new HashMap();
		String sDivisa = "";
		String nomEmpresaFilial = "";
		Date dFechaIni = new Date();
		Date dFechaFin = new Date();
		int iLinea = 0;
		int iUsuario = 0;
		int iEmpresaConcen = 0;
		int iEmpresa = 0;
		int iCuenta = 0;
		int iAfectados = 0;
		double dSdoFin;
		double dSdoInicial;
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			// iUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			iUsuario = funciones.convertirCadenaInteger(parameters.get("usuario").toString());
			String datosGrid = parameters.get("datosGrid").toString();
			paramsGrid = gson.fromJson(datosGrid, new TypeToken<ArrayList<Map<String, String>>>() {
			}.getType());
			for (int iContador = 0; iContador < paramsGrid.size(); iContador++) {
				sDivisa = funciones.validarCadena(parameters.get("divisa").toString());
				iEmpresaConcen = funciones.convertirCadenaInteger(parameters.get("noEmpresaConcentradora").toString());
				iEmpresa = funciones.convertirCadenaInteger(paramsGrid.get(iContador).get("noEmpresa"));
				nomEmpresaFilial = funciones.validarCadena(paramsGrid.get(iContador).get("nomEmpresa"));
				dFechaIni = funciones.ponerFechaDate(parameters.get("fechaIni").toString());
				dFechaFin = funciones.ponerFechaDate(parameters.get("fechaFin").toString());
				iCuenta = funciones.convertirCadenaInteger(paramsGrid.get(iContador).get("noEmpresa"));
				iLinea = this.obtenerLinea(sDivisa);
				Date dFechaIni1 = this.obtenerFecha(iEmpresaConcen, iEmpresa, "MIN", sDivisa, dFechaIni, dFechaFin);

				if (dFechaIni1 != null)
					dFechaIni = dFechaIni1;

				if (!dFechaFin.equals(globalSingleton.getFechaHoy()))
					dFechaFin = this.obtenerFecha(iEmpresaConcen, iEmpresa, "MAX", sDivisa, dFechaIni, dFechaFin);

				// If fechaIni = "12:00:00 a.m." Then
				// MsgBox "No existe la fecha inicial", vbInformation, "SET"
				// fechaIni = txtfechini
				// Exit Sub
				// ElseIf fechaFin = "12:00:00 a.m." Then
				// MsgBox "No existe la fecha final", vbInformation, "SET"
				// fechaFin = Txtfechfin
				// Exit Sub
				// End If

				iAfectados = coinversionDao.eliminarTmpEstadoCuenta(iUsuario);

				dSdoFin = this.preparaReporte(iUsuario, iEmpresaConcen, iCuenta, dFechaIni, iLinea, sDivisa, dFechaIni,
						dFechaFin);
				listSaldo = coinversionDao.consultarSaldoInicial(iEmpresaConcen, iEmpresaConcen,
						this.obtenerLinea(sDivisa),
						this.obtenerFecha(iEmpresaConcen, iEmpresa, "MIN", sDivisa, dFechaIni, dFechaFin));
				if (listSaldo.size() > 0)
					dSdoInicial = listSaldo.get(0).getImporte();
				else
					dSdoInicial = 0;

				datosEmpresa = coinversionDao.consultarDatosEmpresa(iEmpresa);

				if (datosEmpresa.size() > 0) {
					mapDatos.put("rfc", datosEmpresa.get(0).get("rfc"));
					mapDatos.put("direccion", datosEmpresa.get(0).get("direccion"));
				} else {
					logger.info("no existen datos de la empresa");
					mapDatos.put("rfc", "");
					mapDatos.put("direccion", "");
				}

				mapDatos.put("fechaLetraIni", funciones.ponerFechaLetra(dFechaIni, false).toUpperCase());
				mapDatos.put("fechaLetraFin", funciones.ponerFechaLetra(dFechaFin, false).toUpperCase());
				mapDatos.put("noEmpresaFilial", iEmpresa);
				mapDatos.put("nombreEmpresaFilial", nomEmpresaFilial);

				/** datos del reporte **/
				listMapReporte = new ArrayList<Map<String, Object>>();
				listMapReporte = coinversionDao.consultarReporteEstadoCuenta(iUsuario);

				/** datos de la empresa **/
				Map<String, Object> mapData = this.datosTotales(dSdoInicial, dSdoFin, dFechaIni, dFechaFin,
						iEmpresaConcen, iEmpresa, sDivisa);
				List<Map<String, Object>> listMapData = new ArrayList<Map<String, Object>>();
				for (int i = 0; i < listMapReporte.size(); i++) {
					Map<String, Object> mapInfo = listMapReporte.get(i);
					mapInfo.putAll(mapData);
					mapInfo.putAll(mapDatos);
					listMapData.add(i, mapInfo);

				}
				xStream.alias("mapa cheque", java.util.List.class);
				jrDataSource = new JRMapArrayDataSource(listMapData.toArray());

				coinversionDao.eliminarTmpEstadoCuenta(iUsuario);
				listMapReporte.clear();
			}

		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:obtenerReporteEstadodeCuenta");
		}
		return jrDataSource;
	}

	public Date obtenerFecha(int iEmpresaConcen, int iEmpresa, String tipoFecha, String sDivisa, Date dFechaIni,
			Date dFechaFin) {
		boolean bTipo = false;
		Date dFecha = new Date();
		List<HistSaldoDto> lFecha = new ArrayList<HistSaldoDto>();
		try {
			bTipo = tipoFecha.equals("MIN") ? true : false;

			lFecha = coinversionDao.consultarMinMaxFecha(bTipo, iEmpresaConcen, iEmpresa, this.obtenerLinea(sDivisa),
					dFechaIni, dFechaFin);
			if (lFecha.size() > 0) {
				for (int i = 0; i < lFecha.size(); i++) {
					dFecha = lFecha.get(i).getFecValor();
				}
			} else
				logger.info("No existe Fecha");

			lFecha.clear();

		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:obtenerFecha");
		}
		return dFecha;
	}

	public double preparaReporte(int iUsuario, int iEmpresa, int iCuenta, Date dFecha, int iLinea, String sDivisa,
			Date dFechaIni, Date dFechaFin) {
		double dSdoFin = 0;
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			List<HistSaldoDto> listSaldo;
			HistSaldoDto dtoSaldos;
			Date dFechaHoy = globalSingleton.getFechaHoy();
			int iSecuencia = 0;
			int iAfectados = 0;
			double dSaldoFin = 0;
			double dSaldoIni = 0;

			listSaldo = new ArrayList<HistSaldoDto>();
			listSaldo = coinversionDao.consultarSaldoHistorico(iEmpresa, iCuenta, iLinea, dFecha);

			dtoSaldos = new HistSaldoDto();

			if (listSaldo.size() > 0) {
				iSecuencia = 1;
				dtoSaldos.setSecuencia(iSecuencia);
				dtoSaldos.setFecValor(listSaldo.get(0).getFecValor());
				dtoSaldos.setConcepto(listSaldo.get(0).getConcepto());
				dtoSaldos.setSaldoIni(listSaldo.get(0).getSaldoIni());
				dtoSaldos.setDepositos(listSaldo.get(0).getDepositos());
				dtoSaldos.setRetiros(listSaldo.get(0).getRetiros());
				dtoSaldos.setSaldoFin(listSaldo.get(0).getSaldoIni());
				dtoSaldos.setEtiqueta(listSaldo.get(0).getEtiqueta());
				dSaldoFin = dtoSaldos.getSaldoFin();
				dSaldoIni = dtoSaldos.getSaldoIni();
			} else {
				iSecuencia = 1;
				dtoSaldos.setSecuencia(iSecuencia);
				dtoSaldos.setFecValor(dFecha);
				dtoSaldos.setConcepto("SALDO INICIAL");
				dtoSaldos.setSaldoIni(0);
				dtoSaldos.setDepositos(0);
				dtoSaldos.setRetiros(0);
				dtoSaldos.setSaldoFin(0);
				dtoSaldos.setEtiqueta(1);
				dSaldoFin = dtoSaldos.getSaldoFin();
				dSaldoIni = dtoSaldos.getSaldoIni();
			}

			listSaldo.clear();
			listSaldo = null;
			iAfectados = coinversionDao.insertarTmpEstadoCuenta(iUsuario, dtoSaldos);
			dtoSaldos = null;

			listSaldo = new ArrayList<HistSaldoDto>();
			listSaldo = coinversionDao.consultarSaldoDepRetDetalle(iEmpresa, iCuenta, sDivisa, dFechaIni, dFechaFin);

			if (listSaldo.size() > 0) {
				for (int i = 0; i < listSaldo.size(); i++) {
					dtoSaldos = new HistSaldoDto();
					iSecuencia = iSecuencia + 1;
					dtoSaldos.setSecuencia(iSecuencia);
					dtoSaldos.setFecValor(listSaldo.get(i).getFecValor());
					dtoSaldos.setConcepto(listSaldo.get(i).getConcepto());
					dtoSaldos.setSaldoIni(dSaldoFin);
					dtoSaldos.setDepositos(listSaldo.get(i).getDepositos());
					dtoSaldos.setRetiros(listSaldo.get(i).getRetiros());
					dtoSaldos.setSaldoFin(
							dtoSaldos.getSaldoIni() + listSaldo.get(i).getDepositos() - listSaldo.get(i).getRetiros());
					dtoSaldos.setEtiqueta(listSaldo.get(i).getEtiqueta());
					dSaldoFin = dtoSaldos.getSaldoFin();
					dSaldoIni = dtoSaldos.getSaldoIni();

					iAfectados = coinversionDao.insertarTmpEstadoCuenta(iUsuario, dtoSaldos);
				}
				dtoSaldos = null;
			} else {
				dtoSaldos = new HistSaldoDto();
				iSecuencia = iSecuencia + 1;
				dtoSaldos.setSecuencia(iSecuencia);
				dtoSaldos.setFecValor(dFecha);
				dtoSaldos.setConcepto("");
				dtoSaldos.setSaldoIni(dSaldoIni);
				dtoSaldos.setDepositos(0);
				dtoSaldos.setRetiros(0);
				dtoSaldos.setSaldoFin(dSaldoFin);
				dtoSaldos.setEtiqueta(2);
				dSaldoFin = dtoSaldos.getSaldoFin();
				dSaldoIni = dtoSaldos.getSaldoIni();

				iAfectados = coinversionDao.insertarTmpEstadoCuenta(iUsuario, dtoSaldos);
				dtoSaldos = null;
			}

			listSaldo.clear();
			listSaldo = null;

			listSaldo = new ArrayList<HistSaldoDto>();
			listSaldo = coinversionDao.consultarSaldoFin((dFechaFin.equals(dFechaHoy)), iEmpresa, iLinea, iCuenta,
					dFechaHoy, dFechaFin);

			dtoSaldos = new HistSaldoDto();

			if (listSaldo.size() > 0) {
				iSecuencia = iSecuencia + 1;
				dtoSaldos.setSecuencia(iSecuencia);
				dtoSaldos.setFecValor(listSaldo.get(0).getFecValor());
				dtoSaldos.setConcepto(listSaldo.get(0).getConcepto());
				dtoSaldos.setSaldoIni(listSaldo.get(0).getSaldoFin());
				dtoSaldos.setDepositos(listSaldo.get(0).getDepositos());
				dtoSaldos.setRetiros(listSaldo.get(0).getRetiros());
				dtoSaldos.setSaldoFin(listSaldo.get(0).getSaldoFin());
				dtoSaldos.setEtiqueta(listSaldo.get(0).getEtiqueta());
				dSaldoFin = dtoSaldos.getSaldoFin();
				dSaldoIni = dtoSaldos.getSaldoIni();
			} else {
				iSecuencia = iSecuencia + 1;
				dtoSaldos.setSecuencia(iSecuencia);
				dtoSaldos.setFecValor(dFechaFin);
				dtoSaldos.setConcepto("SALDO FINAL ANTES DE INTERESES");
				dtoSaldos.setSaldoIni(dSaldoFin);
				dtoSaldos.setDepositos(0);
				dtoSaldos.setRetiros(0);
				dtoSaldos.setSaldoFin(dSaldoFin);
				dtoSaldos.setEtiqueta(3);
				dSaldoFin = dtoSaldos.getSaldoFin();
				dSaldoIni = dtoSaldos.getSaldoIni();
			}

			listSaldo.clear();
			listSaldo = null;

			iAfectados = coinversionDao.insertarTmpEstadoCuenta(iUsuario, dtoSaldos);
			dtoSaldos = null;

			dSdoFin = dSaldoFin;

		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:preparaReporte");
		}
		return dSdoFin;
	}

	public Map<String, Object> datosTotales(double dSdoInicial, double dSdoFin, Date dFechaIni, Date dFechaFin,
			int iEmpresaConcen, int iEmpresa, String sDivisa) {
		Map<String, Object> mapTotales = new HashMap<String, Object>();
		try {
			double pdSdoini = 0;
			double pdInt = 0;
			double pdIntdev = 0;
			double pdantInt = 0;
			double pdSdofin = 0;
			double pdSdoprom = 0;
			double pdfac = 0;
			double pdimpret = 0;
			double pdTotiva = 0;
			double pdTotisr = 0;
			double pdCompag = 0;
			double pdIva = 0;
			double pdTasrenmes = 0;
			double pdTasrenAcum = 0;
			double psTasrenano = 0;
			double pdInteres = 0;
			List<VencimientoInversionDto> listIntereses = new ArrayList<VencimientoInversionDto>();
			listIntereses = coinversionDao.consultarInteresCapitalizado(funciones.obtenerMes(dFechaIni),
					funciones.obtenerAnio(dFechaIni), iEmpresaConcen, iEmpresa, sDivisa);

			if (listIntereses.size() > 0) {
				pdInt = listIntereses.get(0).getInteres();
				pdIntdev = listIntereses.get(0).getInteresDev();
				pdTasrenmes = listIntereses.get(0).getTasaRend();
				pdTotisr = listIntereses.get(0).getIsr();
				pdCompag = listIntereses.get(0).getComision();
				pdIva = listIntereses.get(0).getIva();
			} else {
				pdInt = 0;
				pdIntdev = 0;
				pdTasrenmes = 0;
				pdTotisr = 0;
				pdCompag = 0;
				pdIva = 0;
			}
			listIntereses.clear();
			listIntereses = null;

			// pdSdoini = sdoinicial(no_empresa)
			pdSdofin = dSdoFin;
			pdSdoprom = coinversionDao.consultarSaldoPromedio2(iEmpresaConcen, iEmpresa, dFechaIni, dFechaFin, sDivisa);
			pdTasrenAcum = this.acumuladoAnual(0, 0, dFechaFin, iEmpresaConcen, iEmpresa, sDivisa);
			pdantInt = pdSdofin;
			pdInteres = new Double(pdInt + pdIntdev);

			if (pdSdoprom != 0)
				pdfac = pdInteres / pdSdoprom;
			else
				pdfac = 0;

			mapTotales.put("saldoIni", dSdoInicial);// aun pendiente definir si
													// es necesario el otro
													// sdoIni q viene de
													// prepararReporte
			// de donde se obtien el pdSdoFin, se podria hacer q ese metodo
			// retorne un mapa con los dos saldos....
			mapTotales.put("saldoFin", pdSdofin);
			mapTotales.put("antInt", pdantInt);
			mapTotales.put("interes", pdInteres);
			mapTotales.put("pdSdoprom", pdSdoprom);
			mapTotales.put("pdfac", pdfac);
			mapTotales.put("pdTotisr", pdTotisr);
			mapTotales.put("pdCompag", pdCompag);
			mapTotales.put("pdTasrenmes", pdTasrenmes);
			mapTotales.put("pdTasrenAcum", pdTasrenAcum);
			mapTotales.put("pdIva", pdIva);
			mapTotales.put("saldoFinalmasInt", new Double(pdSdofin + pdInteres + pdIva - pdTotisr - pdCompag));
			mapTotales.put("interesIva", new Double(pdInteres + pdIva));

		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:datosTotales");
		}
		return mapTotales;
	}

	@SuppressWarnings("deprecation")
	public double acumuladoAnual(double dSumInteres, double dSdoPromedio, Date dFechaFin, int iEmpresaConcen,
			int iEmpresa, String sDivisa) {
		double acumuladoAnual = 0;
		int iDias = 0;
		Date dFecha;
		Date dFecha2;
		List<Map<String, Object>> saldoImp = null;
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			acumuladoAnual = 0;
			dFecha = new Date("01/01/" + funciones.obtenerAnio(globalSingleton.getFechaHoy()));
			dFecha2 = dFechaFin;
			iDias = funciones.diasEntreFechas(dFecha, dFecha2) + 1;

			dSdoPromedio = coinversionDao.consultarSaldoPromedio(iEmpresaConcen, iEmpresa, dFecha, dFecha2);

			saldoImp = coinversionDao.consultarSaldoImporte("hist_movimiento", iEmpresaConcen, iEmpresa,
					this.obtenerLinea(sDivisa), dFecha, dFecha2);

			if (saldoImp.size() > 0) {
				for (int i = 0; i < saldoImp.size(); i++) {
					dSumInteres = dSumInteres
							+ funciones.convertirCadenaDouble(saldoImp.get(i).get("sumInteres").toString());
				}
			} else
				dSumInteres = 0;
			saldoImp.clear();
			saldoImp = null;

			saldoImp = coinversionDao.consultarSaldoImporte("movimiento", iEmpresaConcen, iEmpresa,
					this.obtenerLinea(sDivisa), dFecha, dFecha2);

			if (saldoImp.size() > 0) {
				for (int i = 0; i < saldoImp.size(); i++) {
					dSumInteres = dSumInteres
							+ funciones.convertirCadenaDouble(saldoImp.get(i).get("sumInteres").toString());
				}
			} else
				dSumInteres = 0;
			saldoImp.clear();
			saldoImp = null;

			if (dSumInteres > 0 && dSdoPromedio > 0)
				acumuladoAnual = (((dSumInteres / dSdoPromedio) / iDias) * 360) * 100;

		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:acumuladoAnual");
		}
		return acumuladoAnual;
	}

	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteHistoricoSaldos(Map parameters) {
		JRMapArrayDataSource jrDataSource = null;
		XStream xStream = new XStream(new DomDriver());
		List<Map<String, Object>> repHistorico = null;
		String sDivisaSoin = "";
		int iEmpresa = 0;
		int iUsuario = 0;
		Date dFechaIni = new Date();
		Date dFechaFin = new Date();

		try {
			sDivisaSoin = coinversionDao.consultarIdDivisaSoin(parameters.get("idDivisa").toString());
			iEmpresa = funciones.convertirCadenaInteger(parameters.get("noEmpresa").toString());
			iUsuario = funciones.convertirCadenaInteger(parameters.get("usuario").toString());
			dFechaIni = funciones.ponerFechaDate(parameters.get("fechaIni").toString());
			dFechaFin = funciones.ponerFechaDate(parameters.get("fechaFin").toString());
			repHistorico = coinversionDao.consultarReporteHistoricoSaldos(iEmpresa,
					Integer.parseInt(sDivisaSoin.trim()), dFechaIni, dFechaFin, iUsuario);

			xStream.alias("mapa cheque", java.util.List.class);
			jrDataSource = new JRMapArrayDataSource(repHistorico.toArray());

		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:obtenerReporteHistoricoSaldos");
		}
		return jrDataSource;
	}

	public CoinversionDao getCoinversionDao() {
		return coinversionDao;
	}

	public void setCoinversionDao(CoinversionDao coinversionDao) {
		this.coinversionDao = coinversionDao;
	}

	public ImportarSaldosChequerasBusiness getImportarSaldosChequerasBusiness() {
		return importarSaldosChequerasBusiness;
	}

	public void setImportarSaldosChequerasBusiness(ImportarSaldosChequerasBusiness importarSaldosChequerasBusiness) {
		this.importarSaldosChequerasBusiness = importarSaldosChequerasBusiness;
	}

	@Override
	public JRDataSource reportePrueba(Map<?, ?> parameters) {
		JRMapArrayDataSource jrDataSource = null;
		List<Map<String, Object>> resMap = null;
		try {
			String parametroPrueba = parameters.get("parametroPrueba").toString();
			resMap = coinversionDao.reportePrueba(parametroPrueba);

			jrDataSource = new JRMapArrayDataSource(resMap.toArray());

		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:obtenerReporteCoinversion");
		}
		return jrDataSource;
	}

	@Override
	public HSSFWorkbook obtenerExcel(Map<String, String> map, String ruta) {
		return Utilerias.generarExcel("hoja1", new String[] { "Fecha" }, coinversionDao.obtenerExcel());
	}
	public List<LlenaComboGralDto> llenarTipoSaldo() {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			list = coinversionDao.llenarTipoSaldo();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionBusinessImpl, M:llenarComboChequerasBarrido");
		}
		return list;
	}

	@Override
	public List<Retorno> consultarConfiguraSet() {
		System.out.println("ALE2");
		return coinversionDao.consultarConfiguraSet();
	}
	
	
}
