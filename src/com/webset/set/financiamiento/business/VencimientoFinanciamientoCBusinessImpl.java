package com.webset.set.financiamiento.business;

import java.net.InetAddress;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.springframework.jdbc.core.JdbcTemplate;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.consultas.dao.ConsultasDao;
import com.webset.set.financiamiento.dao.VencimientoFinanciamientoCDao;
import com.webset.set.financiamiento.dao.impl.VencimientoFinanciamientoCDaoImpl;
import com.webset.set.financiamiento.dto.AmortizacionCreditoDto;
import com.webset.set.financiamiento.dto.BeneficiarioDto;
import com.webset.set.financiamiento.dto.ContratoCreditoDto;
import com.webset.set.financiamiento.dto.ControlPagosPasivos;
import com.webset.set.financiamiento.dto.CorreoVencimientoDto;
import com.webset.set.financiamiento.dto.Parametro;
import com.webset.set.financiamiento.service.VencimientoFinanciamientoCService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dao.CorreoDao;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.Retorno;

public class VencimientoFinanciamientoCBusinessImpl implements VencimientoFinanciamientoCService {
	private Bitacora bitacora = new Bitacora();
	private VencimientoFinanciamientoCDao vencimientoFinanciamientoCDao;
	private VencimientoFinanciamientoCDaoImpl vencImpl = new VencimientoFinanciamientoCDaoImpl();
	private CorreoDao objCorreoDao;	
	private static Logger logger = Logger.getLogger(VencimientoFinanciamientoCBusinessImpl.class);
	private JdbcTemplate jdbcTemplate;
	GlobalSingleton globalSingleton;
	String vsTipoMenu = "";
	boolean banIva=false;
	Funciones funciones = new Funciones();
	double pagoTotal=0,vdImporteAux=0;
	int grupo=0,noFactura=0;
	public VencimientoFinanciamientoCDao getVencimientoFinanciamientoCDao() {
		return vencimientoFinanciamientoCDao;
	}

	public void setVencimientoFinanciamientoCDao(VencimientoFinanciamientoCDao vencimientoFinanciamientoCDao) {
		this.vencimientoFinanciamientoCDao = vencimientoFinanciamientoCDao;
	}

	@Override
	public List<LlenaComboGralDto> obtenerPaisVenc() {
		return vencimientoFinanciamientoCDao.obtenerPaisVenc();
	}

	@Override
	public List<LlenaComboGralDto> obtenerEmpresas() {
		return vencimientoFinanciamientoCDao.obtenerEmpresas();
	}

	@Override
	public List<LlenaComboGralDto> obtenerContratos(int piEmpresa) {
		return vencimientoFinanciamientoCDao.obtenerContratos(piEmpresa);
	}

	@Override
	public List<LlenaComboGralDto> obtenerDivisas(int noEmpresa) {
		return vencimientoFinanciamientoCDao.obtenerDivisas(noEmpresa);
	}

	@Override
	public List<AmortizacionCreditoDto> selectMovimientoAzt(String psFecIni, String psPais, int plEmpresa, int piBanco,
			String psLinea, int piTipoFinan, String psDivisa, int plCredito) {
		return vencimientoFinanciamientoCDao.selectMovimientoAzt(psFecIni, psPais, plEmpresa, piBanco, psLinea,
				piTipoFinan, psDivisa, plCredito);
	}

	@Override
	public List<LlenaComboGralDto> obtenerBancoVenci(String psNac, String psTipoMenu) {
		return vencimientoFinanciamientoCDao.obtenerBancoVenci(psNac, psTipoMenu);
	}

	@Override
	public List<LlenaComboGralDto> obtenerBancoPago(int piEmpresa) {
		return vencimientoFinanciamientoCDao.obtenerBancoPago(piEmpresa);
	}

	@Override
	public List<AmortizacionCreditoDto> storeSelectCapital(String psLinea, int piDisposicion) {
		return vencimientoFinanciamientoCDao.storeSelectCapital(psLinea, piDisposicion);
	}

	@Override
	public List<AmortizacionCreditoDto> selectPrimerAmortAct(String psLinea, int piDisposicion) {
		return vencimientoFinanciamientoCDao.selectPrimerAmortAct(psLinea, piDisposicion);
	}

	@Override
	public Map<String, Object> pagoAnticipadoParcial(AmortizacionCreditoDto dto,int noEmpresa) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		List<AmortizacionCreditoDto> list = new ArrayList<AmortizacionCreditoDto>();
		int resultado = 0, lFolio, vlResAmort = 0, vlNoAmort = 0, valActualiza=0;
		Date fecInicio, fecVencimiento;
		String impCap, psSalIns;
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		double psImportePago = 0, psSaldoInsoluto = 0, pdImporteCapital = 0, psSaldoInsolutoInst = 0, dTasa = 0,
				vdInteres = 0;
		boolean pbPagado = false;
		try {
			vencimientoFinanciamientoCDao.iniciaTransaccion();
			resultado = vencimientoFinanciamientoCDao.insertPagoTotal(dto, vlNoAmort);
			if (resultado > 0) {
				resultado = 0;
				resultado = vencimientoFinanciamientoCDao.insertPagoInteresParcial(dto, vlNoAmort);
				if (resultado > 0) {
					psImportePago = dto.getImporte();
					pbPagado = false;
					psSaldoInsoluto = 0;
					list = vencimientoFinanciamientoCDao.selectCapitales(dto);
					if (list.size() > 0) {
						for (int i = 0; i < list.size(); i++) {
							lFolio = list.get(i).getNoFolioAmort();
							if (list.get(i).getCapital() > 0) {
								if (psImportePago > 0) {
									if (psImportePago >= list.get(i).getCapital()) {
										if (dto.isPrimeras()) {
											pdImporteCapital = 0;
											if (psSaldoInsoluto == 0 && !pbPagado) {
												if (list.get(i).getSaldoInsoluto() == psImportePago) {
													psSaldoInsoluto = 0;
													psImportePago = 0;
													pbPagado = true;
												} else {
													psSaldoInsoluto = list.get(i).getSaldoInsoluto() - psImportePago;
												}
											} else {
												psSaldoInsolutoInst = psSaldoInsoluto;
											}
											psImportePago = psImportePago - list.get(i).getCapital();
										} else {
											if (!pbPagado) {
												if (list.get(i).getSaldoInsoluto() >= psImportePago) {
													psSaldoInsolutoInst = psSaldoInsoluto;
													psSaldoInsoluto = list.get(i).getSaldoInsoluto() - psImportePago;
													psImportePago = 0;
													pbPagado = true;
												} else {
													psSaldoInsoluto = 0;
													psSaldoInsolutoInst = psSaldoInsoluto;
												}
											} else {
												psSaldoInsolutoInst = psSaldoInsoluto;
												psSaldoInsoluto = psSaldoInsoluto - list.get(i).getCapital();
											}
										}
									} else {
										pdImporteCapital = list.get(i).getCapital() - psImportePago;
										if (dto.isPrimeras()) {
											if (psSaldoInsoluto == 0) {
												psSaldoInsolutoInst = list.get(i).getSaldoInsoluto();
												psSaldoInsoluto = list.get(i).getSaldoInsoluto() - pdImporteCapital;
											} else {
												psSaldoInsolutoInst = psSaldoInsoluto;
												psSaldoInsoluto = psSaldoInsoluto - pdImporteCapital;
											}
										} else {
											if (psSaldoInsoluto == 0) {
												psSaldoInsoluto = list.get(i).getSaldoInsoluto() - psImportePago;
												psSaldoInsolutoInst = psSaldoInsoluto;

											} else {
												psSaldoInsolutoInst = psSaldoInsoluto;
												psSaldoInsoluto = psSaldoInsoluto - pdImporteCapital;
											}
										}
										psImportePago = 0;
										pbPagado = true;
									}
								} else {
									pdImporteCapital = list.get(i).getCapital();
									if (dto.isPrimeras()) {
										psSaldoInsolutoInst = psSaldoInsoluto;
										psSaldoInsoluto = psSaldoInsoluto - list.get(i).getCapital();
									} else {
										psSaldoInsoluto = psSaldoInsoluto + list.get(i).getCapital();
										psSaldoInsolutoInst = psSaldoInsoluto;
									}
								}
								if (pdImporteCapital == 0)
									impCap = "X";
								else
									impCap = "";

								vlResAmort = vencimientoFinanciamientoCDao.actPagoAnt(dto.getIdFinanciamiento(),
										dto.getIdDisposicion(), list.get(i).getFecVencimiento(),
										list.get(i).getIdAmortizacion(), list.get(i).getNoFolioAmort(), impCap,
										pdImporteCapital, "", 0, 0, psSaldoInsolutoInst);
								if (vlResAmort < 1) {
									mensajes.add("Error al actualizar el capital");
									vencimientoFinanciamientoCDao.cancelaTransaccion();
									mapResult.put("msgUsuario", mensajes);
									return mapResult;
								}
							} else if (list.get(i).getInteres() > 0) {
								if (list.get(i).getTasa() == 'V')
									dTasa = list.get(i).getTasaVigente();
								if (list.get(i).getTasa() == 'F')
									dTasa = list.get(i).getTasaFija();
								if (!pbPagado) {
									if (dto.isPrimeras()) {
										psSaldoInsoluto = list.get(i).getSaldoInsoluto() - psImportePago;
										if (psSaldoInsoluto == 0)
											pbPagado = true;
									}
								}
								fecInicio = df.parse(vencImpl.cambiarOrdenFecha(list.get(i).getFecInicio()));
								fecVencimiento = df.parse(vencImpl.cambiarOrdenFecha(list.get(i).getFecVencimiento()));
								vdInteres = (dTasa / 100 / list.get(i).getDias())
										* ((fecInicio.getTime() - fecVencimiento.getTime()) / 86400000)
										* psSaldoInsoluto;
								if (psSaldoInsoluto == 0)
									psSalIns = "X";
								else
									psSalIns = "";
								vlResAmort = vencimientoFinanciamientoCDao.actPagoAnt(dto.getIdFinanciamiento(),
										dto.getIdDisposicion(), list.get(i).getFecVencimiento(),
										list.get(i).getIdAmortizacion(), list.get(i).getNoFolioAmort(), psSalIns, 0, "",
										0, vdInteres, psSaldoInsoluto);
								if (vlResAmort < 1) {
									mensajes.add("Error al actualizar el capital");
									vencimientoFinanciamientoCDao.cancelaTransaccion();
									mapResult.put("msgUsuario", mensajes);
									return mapResult;
								}
							}
						}
					}
				}
			} else {
				mensajes.add("No se registro correctamente el pago parcial.");
				vencimientoFinanciamientoCDao.cancelaTransaccion();
				mapResult.put("msgUsuario", mensajes);
				return mapResult;
			}
			vencimientoFinanciamientoCDao.terminaTransaccion();
			mensajes.add("Pago realizado correctamente.");
			mapResult.put("msgUsuario", mensajes);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCAction, M:pagoAnticipadoParcial");
		}
		return mapResult;
	}

	//Paga amortizaciones diferentes, creando una 3000 y una 3001 respectivamente
	@Override
	public Map<String, Object> pagoAmortizaciones(String separadas, String txtDivisaPag,int txtBancoPag,String cmbChequeraPag,double txtTipoCambio) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		Gson gson = new Gson();
		List<ContratoCreditoDto> listC = new ArrayList<ContratoCreditoDto>();
		List<Retorno> list = null;
		boolean vbRegistra = false, bAgrupado = false, vbFolio = false;
		String vsClave = "", configuraSet, vsDivisaPag, clave = "", vsDivisa="", vChequera = "",vsConcepto,valorMensaje="",sChequera="", vsProveedor = "";
		int plFolioAnt = 0, consecutivo, vsAcreedor=0, vBanco = 0,x=0,vsNoDocto,sBanco=0, vlNoPersona=0,i=0,idGrupoFlujo=0  ;
		double vdCapital = 0,vdInteres = 0,vdIva = 0,vdImporte;
		Date fechaI;
		SimpleDateFormat formato = new SimpleDateFormat("ddMMyyyy");
		globalSingleton = GlobalSingleton.getInstancia();

		try {
			Date fechaHoy = vencimientoFinanciamientoCDao.obtenerFechaHoy();
			list = vencimientoFinanciamientoCDao.consultarConfiguraSet();
			configuraSet = list.get(0).getValorConfiguraSet();
			if (vsTipoMenu.equals(""))
				vsClave = "MC";
			if (vsTipoMenu.equals("B"))
				vsClave = "MB";
			if (vsTipoMenu.equals("F"))
				vsClave = "MD";
			if (vsTipoMenu.equals("A"))
				vsClave = "MR";
			if(!separadas.equals("[]")){
				List<Map<String, String>> paramAmort = gson.fromJson(separadas,
						new TypeToken<ArrayList<Map<String, String>>>() {
				}.getType());


				List<BeneficiarioDto> listaB = new ArrayList<BeneficiarioDto>();
				for (i = 0; i < paramAmort.size(); i++) {
					if (paramAmort.get(i).get("checked").equals("true")
							&& !paramAmort.get(i).get("valorPago").equals("-")) {
						// if(configuraSet.equals("CIE")&&vsTipoMenu=="A")
						/// Call OrdenServicio(i, vbUnaVez)
						// else{
						sBanco = 0;
						sChequera = "";
						if (configuraSet.equals("CIE")) {
							if (!txtDivisaPag.equals(""))
								vsDivisaPag = txtDivisaPag;
							else
								vsDivisaPag = paramAmort.get(i).get("divisa");

							listaB=buscaBeneficiario(vsDivisaPag,paramAmort.get(i).get("equivalente"));
							if(listaB.isEmpty()){
								mensajes.add("El banco beneficiario no tiene cuenta de cheques");
								mapResult.put("msgUsuario", mensajes);
								return mapResult;
							}else{
								sBanco = Integer.parseInt(listaB.get(0).getBanco());
								sChequera = listaB.get(0).getChequera();
								vsProveedor = listaB.get(0).getProveedor();
								vlNoPersona = listaB.get(0).getNoPersona();
							}
						} else if (paramAmort.get(i).get("formaPago").equals("3")) {

							listaB=buscaBeneficiario("", paramAmort.get(i).get("equivalente"));
							if(listaB.isEmpty()){
								mensajes.add("El banco beneficiario no tiene cuenta de cheques");
								mapResult.put("msgUsuario", mensajes);
								return mapResult;

							}else{
								sBanco = Integer.parseInt(listaB.get(0).getBanco());
								sChequera = listaB.get(0).getChequera();
								vsProveedor = listaB.get(0).getProveedor();
								vlNoPersona = listaB.get(0).getNoPersona();
							}
						}
						if (!vbFolio) {
							consecutivo = folioCupos(globalSingleton.getUsuarioLoginDto().getIdUsuario());
							if (consecutivo < 0) {
								mensajes.add(" En cat_usuario el folio para la Clave Control es NULO para el usuario Num. "
										+ globalSingleton.getUsuarioLoginDto().getIdUsuario());
							}
							String fecha[] = paramAmort.get(i).get("fecVen").split("/");
							clave = vsClave + consecutivo + fecha[0] + fecha[1] + fecha[2]
									+ globalSingleton.getUsuarioLoginDto().getIdUsuario();
							vbFolio = true;
						}
						listC = vencimientoFinanciamientoCDao.selectContratoCredito(paramAmort.get(i).get("idContrato"));
						if (listC.size() > 0) {
							vsDivisa = listC.get(0).getIdDivisa();
							vsAcreedor = listC.get(0).getIdBancoPrestamo();
							vBanco = listC.get(0).getIdBanco();
							vChequera = listC.get(0).getIdClabe();
						}

						if (!txtDivisaPag.equals("")&&txtBancoPag>0&&!cmbChequeraPag.equals("")){
							vsDivisa =txtDivisaPag.trim();
							vBanco =txtBancoPag;
							vChequera =cmbChequeraPag.trim();

							if (txtTipoCambio!= 0 &&txtDivisaPag.equals("MN")){
								/* Este se utiliza para arrendamientos
							  if (Integer.parseInt(paramAmort.get(i).get("soloRenta"))==1){
								vdCapital = Double.parseDouble(paramAmort.get(i).get("renta")) * txtTipoCambio;
								vdInteres = 0;
								vdIva =Double.parseDouble(paramAmort.get(i).get("iva")) * txtTipoCambio;
							}
							else{*/
								vdCapital =Double.parseDouble(paramAmort.get(i).get("capital")) * txtTipoCambio;
								vdInteres = Double.parseDouble(paramAmort.get(i).get("interes"))*txtTipoCambio;
								vdIva = Double.parseDouble(paramAmort.get(i).get("iva"))* txtTipoCambio;
								//}
							}
							else if (txtTipoCambio!=0&&txtDivisaPag.equals("DLS")) {
								/*Este se utiliza para arrendamientos
							 if (Integer.parseInt(paramAmort.get(i).get("soloRenta"))==1){
								vdCapital = Double.parseDouble(paramAmort.get(i).get("renta")) / txtTipoCambio;
								vdInteres = 0;
								vdIva = Double.parseDouble(paramAmort.get(i).get("iva"))  /txtTipoCambio;
							}
							else{*/
								vdCapital = Double.parseDouble(paramAmort.get(i).get("capital")) /txtTipoCambio;
								vdInteres =  Double.parseDouble(paramAmort.get(i).get("interes")) / txtTipoCambio;
								vdIva =  Double.parseDouble(paramAmort.get(i).get("iva")) /txtTipoCambio;
								//}
							}
						}
						else{
							/*Este se utiliza para arrendamientos
							 if (Integer.parseInt(paramAmort.get(i).get("soloRenta"))==1){
                                    vdCapital = CDbl(Format(.TextMatrix(i, LI_C_RENTA), "#####0.00"))
                                    vdInteres = 0
                                    vdIva = CDbl(Format(.TextMatrix(i, LI_C_IVA), "#####0.00"))
                              } else*/
							vdCapital = Double.parseDouble(paramAmort.get(i).get("capital"));
							vdInteres = Double.parseDouble(paramAmort.get(i).get("interes"));
							vdIva =  Double.parseDouble(paramAmort.get(i).get("iva"));
							//}
						}

						valorMensaje=interes(i,vdCapital,vdInteres,vdIva, paramAmort, vsDivisa,vBanco,vChequera,vsAcreedor,plFolioAnt,clave,bAgrupado,
								sBanco,  sChequera, vsProveedor, vlNoPersona);

						if(valorMensaje.equals("Error1")){
							mensajes.add("Error al guardar datos para el generador (3001)");
							mapResult.put("msgUsuario", mensajes);
							return mapResult;
						}
						else if(valorMensaje.equals("Error2")){
							mensajes.add("Error en el generador (C)");
							mapResult.put("msgUsuario", mensajes);
							return mapResult;
						}
						else if(valorMensaje.equals("Error3")){
							mensajes.add("Error al Actualizar el Movimiento (3000, 3001) Campo: id_contable ");
							mapResult.put("msgUsuario", mensajes);
							return mapResult;
						}
					}
				}
				if(configuraSet.equals("CIE")&&vsTipoMenu=="A"){
					mensajes.add("Se genero la orden de servicio correctamente ");
					mapResult.put("msgUsuario", mensajes);
				}else{
					String vsFechaPropuesta="",fechaVenAux="",fechaVenHoy="";
					i=i-1;
					while(i>=0){
						int plresp = 0;
						if(paramAmort.get(i).get("checked").equals("true")){
							Date fechaVen = funciones.ponerFechaDate(paramAmort.get(i).get("fecVen").toString());
							fechaVenAux=funciones.ponerFechaSola(paramAmort.get(i).get("fecVen"));
							fechaVenHoy=funciones.ponerFechaSola(fechaHoy);
							//fechaVen menor a fechaHoy 
							pagoTotal=Double.parseDouble(valorMensaje.trim());
							if(fechaVen.compareTo(fechaHoy)<0){
								plresp = vencimientoFinanciamientoCDao.insertaEncabezado(0, paramAmort.get(i).get("gpoEmpresa"), clave, 
										fechaVenHoy, pagoTotal, fechaVenHoy,0);
								vsFechaPropuesta = fechaVenHoy;
							}
							else{
								plresp = vencimientoFinanciamientoCDao.insertaEncabezado(0,paramAmort.get(i).get("gpoEmpresa"), clave, 
										fechaVenAux, pagoTotal,fechaVenAux ,0);
								vsFechaPropuesta = fechaVenAux ;
							}
							idGrupoFlujo= vencimientoFinanciamientoCDao.obtenerGrupoFlujo(Integer.parseInt(paramAmort.get(i).get("empresa")));
							String concepto = "M" + "-" + "MANUAL" + "-" + globalSingleton.getUsuarioLoginDto().getClaveUsuario() + "-" + idGrupoFlujo + "-" + formato.format(fechaHoy) + "-" +  paramAmort.get(i).get("divisa");

							vencimientoFinanciamientoCDao.updateMovtosFecPropuesta(vsFechaPropuesta, clave,concepto);
							if(plresp<=0){
								mensajes.add("Error al Crear Registro del Encabezado de la Propuesta.");
								mapResult.put("msgUsuario", mensajes);
								return mapResult;
							}
							else{
								break;
							}
						}
						i = i - 1;
					}
					mensajes.add("Se efectuaron los pagos correctamente con la propuesta " + clave +" ");
					mapResult.put("msgUsuario", mensajes);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCAction, M:pagoAnticipadoParcial");
		}
		return mapResult;
	}

	public String interes(int i,double vdCapital,double vdInteres, double vdIva, 
			List<Map<String, String>> paramAmort,String vsDivisa,int vBanco, 
			String vChequera,int vsAcreedor,int plFolioAnt,String clave, boolean bAgrupado,
			int sBanco, String sChequera,String vsProveedor,int vlNoPersona){
		int x = 0,vsNoDocto;
		String valorMensaje="";
		double vdImporte = 0;
		String vsConcepto = "";
		if(vdCapital!=0) {
			vdImporte = vdCapital;
			if (!paramAmort.get(i).get("valorPago").equals("")){
				vsConcepto = "Pago Capital";
				x = 2;
			}
			else{
				vsConcepto = "Pago Capital";
				x = 1;
				;
			}
			vdCapital = 0;
		} 
		else if (vdInteres != 0 ){
			vdImporte = vdInteres;
			vsConcepto = "Pago Interes";
			vdInteres = 0;
			x = 2;
		}
		else if (vdIva !=0){
			vdImporte = vdIva;
			if (!paramAmort.get(i).get("valorPago").equals(""))
				vsConcepto = "Pago Iva";
			else
				vsConcepto = "Pago Iva";
			vdIva = 0;
			x = 3;

		}
		pagoTotal = pagoTotal + vdImporte;
		vsNoDocto =Integer.parseInt(paramAmort.get(i).get("idDisposicion"));
		if (x!=0){
			String resultado= insertaParametro(i, vdImporte, vsDivisa, vsConcepto, "N", vBanco, 
					vChequera, vdIva, vsNoDocto, vsAcreedor, sBanco, 
					sChequera, clave, vsProveedor, vlNoPersona, bAgrupado, 
					plFolioAnt,paramAmort);
			if(!resultado.equals(""))
				return resultado;
			int plresp = vencimientoFinanciamientoCDao.updateAmortizacion1(paramAmort.get(i).get("idContrato"),
					paramAmort.get(i).get("idDisposicion"),
					paramAmort.get(i).get("idAmortizacion"),
					paramAmort.get(i).get("fecVen"), "P", clave, vsTipoMenu);

			confirmaLiqCred(i,paramAmort);
		}
		/*If vsTipoMenu = "A" Then
                If x = 1 Then
                    .TextMatrix(i, LI_C_AMORTIZACION) = 0
                    GoTo INTERES
                ElseIf x = 2 Then
                    GoTo INTERES
                ElseIf x = 3 Then
                    GoTo INTERES
                End If
            Else*/
		if(x == 2){
			interes(i,vdCapital,vdInteres,vdIva, paramAmort, vsDivisa,vBanco,vChequera,vsAcreedor,plFolioAnt,clave,bAgrupado,
					sBanco,  sChequera, vsProveedor, vlNoPersona);
			System.out.println("Entra 1.");}
		else if (x == 3){
			interes(i,vdCapital,vdInteres,vdIva, paramAmort, vsDivisa,vBanco,vChequera,vsAcreedor,plFolioAnt,clave,bAgrupado,
					sBanco,  sChequera, vsProveedor, vlNoPersona);
			System.out.println("Entra 2.");	
		}
		//End If
		valorMensaje=pagoTotal+"";
		return valorMensaje;
	}

	private String insertaParametro(int i, double vdImporte, String vsDivisa, String vsConcepto, String vsEstatus,
			int vBanco, String vChequera, double vdIva, int vsNoDocto, int vsAcreedor, int sBanco, String sChequera,
			String clave, String vsProveedor, int vlNoPersona, boolean bAgrupado, int plFolioAnt,List<Map<String, String>> paramAmort) {
		int plFolio,lAfectados=0,pdFolioMov,pdFolioDet;
		Date date = new Date();
		Map<String, Object> mapGenerador = new HashMap<String, Object>();
		GeneradorDto generador = new GeneradorDto();
		DateFormat formatoHora = new SimpleDateFormat("HH:mm");
		String configuraSet,vsDivision = "",vsRubro,resultado="";
		List<Retorno> list = null;
		Date fechaHoy = vencimientoFinanciamientoCDao.obtenerFechaHoy();
		globalSingleton = GlobalSingleton.getInstancia();
		try {
			list = vencimientoFinanciamientoCDao.consultarConfiguraSet();
			configuraSet = list.get(0).getValorConfiguraSet();
			if(configuraSet.equals("CIE")&&!vsTipoMenu.equals("A")){
				List<AmortizacionCreditoDto> lista = new ArrayList<AmortizacionCreditoDto>();
				lista= vencimientoFinanciamientoCDao.selectDivision(Integer.parseInt(paramAmort.get(i).get("bancoBenef")), paramAmort.get(i).get("chequeraBenef"));
				vBanco =Integer.parseInt(paramAmort.get(i).get("bancoBenef"));
				vChequera = paramAmort.get(i).get("chequeraBenef");
				if(!lista.isEmpty())
					vsDivision = lista.get(0).getDivision();
			}
			vsRubro = "41201";
			if(configuraSet.equals("CIE"))
				vsRubro = "2400500";


			plFolio = vencimientoFinanciamientoCDao.seleccionarFolioReal("no_folio_param");
			plFolioAnt = plFolio;
			Parametro dto=new Parametro();
			lAfectados = vencimientoFinanciamientoCDao.inserta1(paramAmort.get(i).get("empresa"), plFolio, 0, paramAmort.get(i).get("formaPago"), 
					"3000", globalSingleton.getUsuarioLoginDto().getIdUsuario(), paramAmort.get(i).get("idDisposicion"), vlNoPersona, 
					paramAmort.get(i).get("fecVen"), paramAmort.get(i).get("fecVen"), funciones.ponerFecha(fechaHoy),funciones.ponerFecha(fechaHoy), 
					vdImporte, vdImporte,globalSingleton.getUsuarioLoginDto().getIdCaja(), vsDivisa, vsDivisa, "CRD", "", 
					vsConcepto + paramAmort.get(i).get("idDisposicion"), 1, vsEstatus, "S", "P", vBanco, vChequera, "", 
					"CRD", "Egreso por Credito Bancario" +paramAmort.get(i).get("idDisposicion"), 1, 0, 
					0, plFolioAnt, vdIva, 0, "A", "0", formatoHora.format(date), vsRubro, vsDivision, 0, plFolio, vsNoDocto, 
					vsProveedor, vsAcreedor, sBanco, sChequera, "", paramAmort.get(i).get("idContrato"));

			if(lAfectados<=0) {
				//MsgBox "ï¿½Error al Guardar Datos Para el Generador! (3000)", vbExclamation, "SET"
				return "Error1";
			}
			plFolio = vencimientoFinanciamientoCDao.seleccionarFolioReal("no_folio_param");
			lAfectados = vencimientoFinanciamientoCDao.inserta1(paramAmort.get(i).get("empresa"), plFolio, 0, paramAmort.get(i).get("formaPago"), 
					"3001",globalSingleton.getUsuarioLoginDto().getIdUsuario(),paramAmort.get(i).get("idDisposicion"), vlNoPersona, 
					paramAmort.get(i).get("fecVen"), paramAmort.get(i).get("fecVen"), funciones.ponerFecha(fechaHoy), 
					funciones.ponerFecha(fechaHoy), vdImporte, vdImporte, globalSingleton.getUsuarioLoginDto().getIdCaja(), vsDivisa, vsDivisa, "CRD", 
					"", vsConcepto + paramAmort.get(i).get("idDisposicion"), 1, "H", "S", "P", vBanco, vChequera, "", 
					"CRD", "Egreso por Credito Bancario" + paramAmort.get(i).get("idDisposicion"), 1, 1, 
					1, plFolioAnt, vdIva, 0, "A", "0",formatoHora.format(date), vsRubro, vsDivision, 0, plFolioAnt, vsNoDocto, 
					vsProveedor, vsAcreedor, sBanco, sChequera, "", paramAmort.get(i).get("idContrato"));
			if(lAfectados<=0) {
				//MsgBox "ï¿½Error al Guardar Datos Para el Generador! (3000)", vbExclamation, "SET"
				return "Error1";
			}
			pdFolioMov = 0;
			pdFolioDet = 0;
			lAfectados = 0;
			mapGenerador = new HashMap<String, Object>();
			generador.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
			generador.setEmpresa(Integer.parseInt(paramAmort.get(i).get("empresa")));
			generador.setFolParam(plFolioAnt);
			generador.setFolMovi(pdFolioMov);
			generador.setFolDeta(pdFolioDet);
			System.out.println(generador.toString());
			mapGenerador = vencimientoFinanciamientoCDao.generador(generador);
			lAfectados=Integer.parseInt(mapGenerador.get("result").toString());
			if (Integer.parseInt(mapGenerador.get("result").toString()) > 0) {
				return "Error2";
				// MsgBox "Error en el generador (C)", vbExclamation, "SET"
			}else{
				pdFolioMov=Integer.parseInt(mapGenerador.get("movi").toString());
				pdFolioDet=Integer.parseInt(mapGenerador.get("deta").toString());
				lAfectados=vencimientoFinanciamientoCDao.updateMovCre(pdFolioMov, pdFolioDet, clave,funciones.ponerFecha(fechaHoy), paramAmort.get(i).get("idContrato"));
				if(lAfectados<=0){
					return "Error3";
					//  MsgBox "Error al Actualizar el Movimiento (3000, 3001) Campo: id_contable ", vbExclamation, "SET"
				} 


			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCAction, M:pagoAnticipadoParcial");
		}
		return resultado;
	}

	private List<BeneficiarioDto> buscaBeneficiario(String psDivisaPag,String equivalente) {
		boolean buscaBeneficiario = false;
		List<BeneficiarioDto> list = new ArrayList<BeneficiarioDto>();
		try {
			list = vencimientoFinanciamientoCDao.selectBeneficiario(equivalente, psDivisaPag);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCAction, M:pagoAnticipadoParcial");
		}
		return list;
	}

	public int folioCupos(int usuario) {
		int numFolio = 0, piNoIntentos, lclRtabla;
		try {
			System.out.println("entro a folioCupos");
			piNoIntentos = 0;
			numFolio = 0;
			vencimientoFinanciamientoCDao.iniciaTransaccion();
			vencimientoFinanciamientoCDao.updateFolioCupos(usuario);
			lclRtabla = vencimientoFinanciamientoCDao.selectFolioCupos(usuario);
			if (lclRtabla >= 0) {
				numFolio = lclRtabla;
			} else {
				numFolio = -1;
			}
			vencimientoFinanciamientoCDao.terminaTransaccion();
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:selectBeneficiario");
		}
		return numFolio;
	}
	public void confirmaLiqCred(int i,List<Map<String, String>> paramAmort){
		List<AmortizacionCreditoDto> lista = new ArrayList<AmortizacionCreditoDto>();
		lista= vencimientoFinanciamientoCDao.selectPagosVenc(paramAmort.get(i).get("idContrato"),paramAmort.get(i).get("idDisposicion"));
		if(!lista.isEmpty()){
			if(lista.get(0).getMontoDisposicion()==lista.get(0).getCapital()){
				vencimientoFinanciamientoCDao.actualizaDisp(paramAmort.get(i).get("idContrato"),paramAmort.get(i).get("idDisposicion"),"P");
			}
		}
	}

	@Override
	public Map<String, Object> pagoAnticipadoTotal(AmortizacionCreditoDto dto, int noEmpresa) {

		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		int resultado = 0, vlNoAmort = 0;
		try {

			resultado = vencimientoFinanciamientoCDao.insertPagoTotal(dto, vlNoAmort);
			if (resultado > 0) {
				resultado = vencimientoFinanciamientoCDao.updateAmortizaCapital(dto);

				if (resultado <0) {
					mensajes.add("Los registros no se cancelaron correctamente.");
					vencimientoFinanciamientoCDao.cancelaTransaccion();
					mapResult.put("msgUsuario", mensajes);
					return mapResult;
				}
			} else {
				mensajes.add("Error al registrar el pago anticipado.");
				vencimientoFinanciamientoCDao.cancelaTransaccion();
				mapResult.put("msgUsuario", mensajes);
				return mapResult;
			}

			mensajes.add("Pago realizado correctamente.");
			mapResult.put("msgUsuario", mensajes);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCAction, M:pagoAnticipadoParcial");
		}
		return mapResult;

	}
	
public void consultarConfiguraSetenviarCorreoVencimientos() {
	
		
		String retorno = null;
		
		
		try {
			retorno= vencimientoFinanciamientoCDao.consultarConfiguraSet(1);
			
			if(retorno.equals("DALTON") ){
				enviarCorreoVencimientos();
				
			}
			else{
				System.out.println("No es dalton");
				return;
			}
		
			
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
			
			
		}
}
	
	@Override
	public void enviarCorreoVencimientos() {
		List<CorreoVencimientoDto> list = new ArrayList<CorreoVencimientoDto>();
		String html="",asunto="";
		int periodo=0;
		try{
			bitacora.registrar("Inicia el proceso de envio de correos electronicos");
			bitacora.registrar("Se obtienen configuraciones");
			InetAddress localHost = InetAddress.getLocalHost();			
			String miIP = localHost.getHostAddress();
			//Asunto
			String ipConf = vencimientoFinanciamientoCDao.consultarConfiguraSet(4848);
			String ejecutar = vencimientoFinanciamientoCDao.consultarConfiguraSet(5003);
			if (miIP.equals(ipConf) && ejecutar.toUpperCase().equals("SI")) {		
				String correoSalida = this.vencimientoFinanciamientoCDao.consultarConfiguraSet(4950);
				String psw = this.vencimientoFinanciamientoCDao.consultarConfiguraSet(4951);
				String puerto = this.vencimientoFinanciamientoCDao.consultarConfiguraSet(4952);
				String host = this.vencimientoFinanciamientoCDao.consultarConfiguraSet(4953);
				String correoSalidaDefecto = this.vencimientoFinanciamientoCDao.consultarConfiguraSet(4954);
				bitacora.insertarRegistro("Inicia proceso de envío de correos 1");				
				Properties properties = new Properties();
				properties.put("mail.smtp.host", host);
				properties.setProperty("mail.smtp.port",puerto );
				properties.setProperty("mail.smtp.user", correoSalida);
				properties.setProperty("mail.smtp.auth", "true");
				properties.put("mail.smtp.ssl.trust",host);
				properties.setProperty("mail.smtp.starttls.enable", "false");
				properties.put("mail.smtp.socketFactory.fallback", "false");
				properties.setProperty("mail.smtp.quitwait", "false");
				properties.put("mail.smtp.socketFactory.port",puerto); 
				properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
				Enumeration<?> en = properties.propertyNames();
				while(en.hasMoreElements()){
					String s = en.nextElement().toString();
					bitacora.insertarRegistro("["+ s +"] : ["+ properties.getProperty(s) +"]");
				}
				list = vencimientoFinanciamientoCDao.selectDestinatarios();
				bitacora.insertarRegistro("Registros para envío " + list.size());
				if(list.size()>0){

					for (int i = 0; i < list.size(); i++) {
						asunto="Alerta de financiamientos próximos a vencer en 5 y 10 días";
						html= armarHTML(list.get(i).getUsuario(),asunto);
						if(html.equals(""))
							return;
						else if(html.equals("1"))
							bitacora.insertarRegistro("El usuario" +list.get(i).getUsuario()+" no tiene empresas asignadas" );
						else if(html.equals("2"))
							bitacora.insertarRegistro("Las empresas asignadas al usuario " +list.get(i).getUsuario()+" no tienen vencimientos" );
						else{
							Session session = Session.getDefaultInstance(properties, null);
							MimeMessage message = new MimeMessage(session);
							message.setFrom(new InternetAddress(correoSalida));
							if(list.get(i).getEmail().equals(""))
								message.addRecipient(
										Message.RecipientType.TO,
										new InternetAddress(correoSalidaDefecto));
							else
								message.addRecipient(
										Message.RecipientType.TO,
										new InternetAddress(list.get(i).getEmail()));
							message.setSubject(asunto);
							message.setText("pruebas");
							message.setContent(html, "text/html");
							//Envío
							Transport t = session.getTransport("smtp");
							t.connect(correoSalida,psw);
							t.sendMessage(message, message.getAllRecipients());
							t.close();
							vencimientoFinanciamientoCDao.updateControlCorreo(asunto,list.get(i).getUsuario(),list.get(i).getEmail());
						}
					}
				}
				else{
					bitacora.registrar("No hay destinatarios registrados.");
					return;
				}
			} else {
				bitacora.registrar("El nodo no está configurado para este proceso.");
			}
		}
		catch (Exception e)
		{
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:enviarCorreoVencimientos");

		}
	}

	public String armarHTML(int usuario,String titulo) {
		DecimalFormat formato = new DecimalFormat("###,###,###,##0.00");
		DecimalFormat formato2 = new DecimalFormat("###,###,###,##0.00000");
		List<Map<String, String>> listDatosCorreo;
		List<ControlPagosPasivos> listaVencimientos= new ArrayList<ControlPagosPasivos>();
		List<CorreoVencimientoDto> listEmpresasUsuario= new ArrayList<CorreoVencimientoDto>();
		Map<String, String> rstDatosCorreo;
		StringBuffer sb= new StringBuffer();
		List<Map<String, String>> rstDatosPago;
		Map<String,String> resultado = new HashMap<String,String>();
		Date fechaHoy=new Date();
		int cont=0;
		String[] dias={"Domingo","Lunes","Martes", "Miércoles","Jueves","Viernes","Sábado"};
		String[] meses = {"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
		int numeroDia=0, numeroMes=0;
		double totalESaldoInsoluto=0,totalEPagoCap=0,totalEInteres=0,totalEIva=0,totalEPagoTotal=0;
		double totalLSaldoInsoluto=0,totalLPagoCap=0,totalLInteres=0,totalLIva=0,totalLPagoTotal=0;
		double totalSaldoInsoluto=0,totalPagoCap=0,totalInteres=0,totalIva=0,totalPagoTotal=0;
		Calendar cal= Calendar.getInstance();
		cal.setTime(fechaHoy);
		String color="",idFinanc="";
		numeroDia=cal.get(Calendar.DAY_OF_WEEK);
		numeroMes=cal.get(Calendar.MONTH);
		String fecha = dias[numeroDia - 1]+" "+cal.get(Calendar.DAY_OF_MONTH)+" de "+meses[numeroMes]+" de "+cal.get(Calendar.YEAR);
		try{
			sb.delete(0, sb.length());
			listDatosCorreo = vencimientoFinanciamientoCDao.funSQLDatosCorreo(2);
			rstDatosCorreo = listDatosCorreo.get(0);
			listEmpresasUsuario=vencimientoFinanciamientoCDao.obtenerEmpresasUsuario(usuario);
			if(listEmpresasUsuario.size()<=0) 
				return "1";
			//HTML
			sb.append("<!DOCTYPE html>");
			sb.append("<HTML>");
			//Encabezado pagina
			sb.append("<HEAD>");
			sb.append("<TITLE>"+titulo+"</TITLE>");
			sb.append("<style>");
			sb.append("table{border-collapse: collapse;width: 100%;}");
			sb.append("th,td{text-align: left;padding: 2.5px;}");
			sb.append("tr:nth-child(even){background-color: #f2f2f2}");
			sb.append("th{background-color: #4CAF50;color: white;}");
			sb.append(".titulo{background-color: #D3FFC0;color:black;}");
			sb.append("</style>");
			sb.append("</HEAD>");
			//Cuerpo pagina
			//Encabezado
			sb.append("<BODY LINK='BLUE' VLINK='BLUE'>");

			sb.append("<H4 ALIGN='LEFT'>FECHA DE EMISIÓN: "+fecha+"<BR>");
			sb.append("<H3 ALIGN='CENTER'>");
			sb.append("DALTON ORGULLO MOTRIZ" + "<BR>");

			sb.append("<BR>" + rstDatosCorreo.get("parrafo_1"));
			sb.append("<BR><BR><H4 ALIGN='CENTER'>" + rstDatosCorreo.get("parrafo_2"));

			sb.append("<H5 ALIGN='Left'><H5><TABLE>");
			//Encabezado Tabla
			sb.append("<tr><th width='100px'><STRONG>" +rstDatosCorreo.get("parrafo_3")  + "</STRONG></th>");
			sb.append("<th width='75px'><STRONG>" +rstDatosCorreo.get("parrafo_17")  + "</STRONG></th>");
			sb.append("<th width='120px'><STRONG>" +rstDatosCorreo.get("parrafo_4")  + "</STRONG></th>");
			sb.append("<th width='100px'><STRONG>" +rstDatosCorreo.get("parrafo_5")  + "</STRONG></th>");
			sb.append("<th width='75px'><STRONG>" +rstDatosCorreo.get("parrafo_6")  + "</STRONG></th>");
			sb.append("<th width='100px'><STRONG>" +rstDatosCorreo.get("parrafo_7")  + "</STRONG></th>");
			sb.append("<th width='75px'><STRONG>" +rstDatosCorreo.get("parrafo_8")  + "</STRONG></th>");
			sb.append("<th width='100px'><STRONG>" +rstDatosCorreo.get("parrafo_9")  + "</STRONG></th>");
			sb.append("<th width='100px'><STRONG>" +rstDatosCorreo.get("parrafo_10")  + "</STRONG></th>");
			sb.append("<th width='100px'><STRONG>" +rstDatosCorreo.get("parrafo_11")  + "</STRONG></th>");
			sb.append("<th width='100px'><STRONG>" +rstDatosCorreo.get("parrafo_12")  + "</STRONG></th>");
			sb.append("<th width='100px'><STRONG>" +rstDatosCorreo.get("parrafo_13")  + "</STRONG></th>");
			sb.append("</tr>");
			//Cuerpo tabla
			int noVenc=0;
			for (int j = 0; j < listEmpresasUsuario.size(); j++) {
				listaVencimientos = vencimientoFinanciamientoCDao.obtenerVencimientos(listEmpresasUsuario.get(j).getNoEmpresa(),0,"","","");
				
				if(listaVencimientos.size()<=0){
					bitacora.insertarRegistro("La empresa " +listEmpresasUsuario.get(j).getNoEmpresa()+" no tiene vencimientos." );
					noVenc++;
				}
				else{
					sb.append("<tr><th width='100px' class='titulo' colspan='12'><STRONG><BR>" +listEmpresasUsuario.get(j).getNomEmpresa()+ "<BR><BR></STRONG></th>");
					sb.append("</tr>");
					idFinanc="";
					totalLSaldoInsoluto=0;
					totalLPagoCap=0;
					totalLInteres=0;
					totalLIva=0;
					totalLPagoTotal=0;
					String lineaSig="";
					System.out.println("//////////");
					for (int i = 0; i < listaVencimientos.size(); i++) {
						sb.append("<tr>");
						if(listaVencimientos.get(i).getColor()=='S')
							color="<font color='#DF0101'>";
						else 
							color="";
						sb.append("<td width='100px'>"+color +listaVencimientos.get(i).getFinanciamiento() + "</td>");
						sb.append("<td width='75px'>"+color +listaVencimientos.get(i).getDisposicion()+ "</td>");
						sb.append("<td width='120px'>"+color +listaVencimientos.get(i).getFechaInicio() + "</td>");
						sb.append("<td width='100px'>"+color +listaVencimientos.get(i).getFecha()+ "</td>");
						sb.append("<td width='75px'>"+color +listaVencimientos.get(i).getPlazo()+ "</td>");
						sb.append("<td width='100px'>"+color +formato2.format(listaVencimientos.get(i).getValorTasa()) + "</td>");
						sb.append("<td width='75px'>"+color+listaVencimientos.get(i).getDivisa() + "</td>");
						sb.append("<td align='right'  width='100px'>"+color +formato.format(listaVencimientos.get(i).getSaldoInsoluto()) + "</td>");
						sb.append("<td align='right'width='100px'>" +color+formato.format(Double.parseDouble(listaVencimientos.get(i).getPagoCap())) + "</td>");
						sb.append("<td align='right'width='100px'>"+color +formato.format(Double.parseDouble(listaVencimientos.get(i).getInteres())) + "</td>");
						sb.append("<td align='right'width='100px'>"+color +formato.format(Double.parseDouble(listaVencimientos.get(i).getIva())) + "</td>");
						sb.append("<td align='right'width='100px'>"+color +formato.format(Double.parseDouble(listaVencimientos.get(i).getPagoTotal())) + "</td>");
						sb.append("</tr>");
						totalESaldoInsoluto+=listaVencimientos.get(i).getSaldoInsoluto();
						totalEPagoCap+=Double.parseDouble(listaVencimientos.get(i).getPagoCap());
						totalEInteres+=Double.parseDouble(listaVencimientos.get(i).getInteres());
						totalEIva+=Double.parseDouble(listaVencimientos.get(i).getIva());
						totalEPagoTotal+=Double.parseDouble(listaVencimientos.get(i).getPagoTotal());
						if(listaVencimientos.size()-1==i)
							lineaSig="";
							else
								lineaSig=listaVencimientos.get(i+1).getFinanciamiento();
						if(listaVencimientos.get(i).getFinanciamiento().equals(lineaSig)){
								totalLSaldoInsoluto+=listaVencimientos.get(i).getSaldoInsoluto();
								totalLPagoCap+=Double.parseDouble(listaVencimientos.get(i).getPagoCap());
								totalLInteres+=Double.parseDouble(listaVencimientos.get(i).getInteres());
								totalLIva+=Double.parseDouble(listaVencimientos.get(i).getIva());
								totalLPagoTotal+=Double.parseDouble(listaVencimientos.get(i).getPagoTotal());
							}else{
								totalLSaldoInsoluto+=listaVencimientos.get(i).getSaldoInsoluto();
								totalLPagoCap+=Double.parseDouble(listaVencimientos.get(i).getPagoCap());
								totalLInteres+=Double.parseDouble(listaVencimientos.get(i).getInteres());
								totalLIva+=Double.parseDouble(listaVencimientos.get(i).getIva());
								totalLPagoTotal+=Double.parseDouble(listaVencimientos.get(i).getPagoTotal());
								sb.append("<tr><th width='100px' colspan='7'><STRONG>Total por Línea</STRONG></th>");
								sb.append("<th>"+formato.format(totalLSaldoInsoluto)+"</th>");
								sb.append("<th>"+formato.format(totalLPagoCap)+"</th>");
								sb.append("<th>"+formato.format(totalLInteres)+"</th>");
								sb.append("<th>"+formato.format(totalLIva)+"</th>");
								sb.append("<th>"+formato.format(totalLPagoTotal)+"</th>");
								sb.append("</tr>");
								totalLSaldoInsoluto=0;
								totalLPagoCap=0;
								totalLInteres=0;
								totalLIva=0;
								totalLPagoTotal=0;
							}
							
					}
					sb.append("<tr><th width='100px' colspan='7'><STRONG>Total por Empresa</STRONG></th>");
					sb.append("<th>"+formato.format(totalESaldoInsoluto)+"</th>");
					sb.append("<th>"+formato.format(totalEPagoCap)+"</th>");
					sb.append("<th>"+formato.format(totalEInteres)+"</th>");
					sb.append("<th>"+formato.format(totalEIva)+"</th>");
					sb.append("<th>"+formato.format(totalEPagoTotal)+"</th>");
					sb.append("</tr>");
					totalSaldoInsoluto+=totalESaldoInsoluto;
					totalPagoCap+=totalEPagoCap;
					totalInteres+=totalEInteres;
					totalIva+=totalEIva;
					totalPagoTotal+=totalEPagoTotal;
					totalESaldoInsoluto=0;
					totalEPagoCap=0;
					totalEInteres=0;
					totalEIva=0;
					totalEPagoTotal=0;
				}
			}
			sb.append("<tr><th width='100px' colspan='7'><STRONG>Total de Vencimientos</STRONG></th>");
			sb.append("<th>"+formato.format(totalSaldoInsoluto)+"</th>");
			sb.append("<th>"+formato.format(totalPagoCap)+"</th>");
			sb.append("<th>"+formato.format(totalInteres)+"</th>");
			sb.append("<th>"+formato.format(totalIva)+"</th>");
			sb.append("<th>"+formato.format(totalPagoTotal)+"</th>");
			sb.append("</tr>");
			//Fin tabla
			sb.append("</TABLE ><BR><BR><BR>");
			sb.append("</H3>");
			sb.append("<BR><H5 ALIGN='CENTER'>" + rstDatosCorreo.get("parrafo_14"));
			sb.append("<BR><H3 ALIGN='RIGHT'> <a href='http://www.webset.com.mx'> http://www.webset.com.mx </A> </H3></BODY></HTML>");
			if(noVenc==listEmpresasUsuario.size()){
				return "2";
			}

		}
		catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M: armarHTML");
		}

		return sb.toString();
	}
	//Se crea para generar una 3000 con dos 3001
	@Override
	public Map<String, Object> pagoAmortizacionesAgrupadas(String agrupadas, String txtDivisaPag,int txtBancoPag,String cmbChequeraPag,double txtTipoCambio) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		Gson gson = new Gson();
		List<ContratoCreditoDto> listC = new ArrayList<ContratoCreditoDto>();
		List<Retorno> list = null;
		boolean vbRegistra = false, bAgrupado = false, vbFolio = false;
		String vsClave = "", configuraSet, vsDivisaPag, clave = "", vsDivisa="", vChequera = "",vsConcepto,valorMensaje="",sChequera="", vsProveedor = "";
		int plFolioAnt = 0,xAux=0, consecutivo, vsAcreedor=0, vBanco = 0,x=0,vsNoDocto,sBanco=0, vlNoPersona=0,i=0,idGrupoFlujo=0, par=0;
		double vdCapital = 0,vdInteres = 0,vdIva = 0,vdImporte;
		Date fechaI;
		System.out.println("vdImporteAux 1: "+vdImporteAux);
		vdImporteAux=0;
		pagoTotal=0;
		SimpleDateFormat formato = new SimpleDateFormat("ddMMyyyy");
		globalSingleton = GlobalSingleton.getInstancia();
		List<Map<String, String>> paramAmort = gson.fromJson(agrupadas,
				new TypeToken<ArrayList<Map<String, String>>>() {
		}.getType());
		List<BeneficiarioDto> listaB = new ArrayList<BeneficiarioDto>();
		try {
			Date fechaHoy = vencimientoFinanciamientoCDao.obtenerFechaHoy();
			list = vencimientoFinanciamientoCDao.consultarConfiguraSet();
			configuraSet = list.get(0).getValorConfiguraSet();
			if (vsTipoMenu.equals(""))
				vsClave = "MC";
			if (vsTipoMenu.equals("B"))
				vsClave = "MB";
			if (vsTipoMenu.equals("F"))
				vsClave = "MD";
			if (vsTipoMenu.equals("A"))
				vsClave = "MR";
			for (i = 0; i < paramAmort.size(); i++) {

				if (paramAmort.get(i).get("checked").equals("true")
						&& !paramAmort.get(i).get("valorPago").equals("-")) {
					if (paramAmort.get(i).get("idAmortizacion").equals("0")) 
						par=0;
					else
						par=1;
					sBanco = 0;
					sChequera = "";
					if (configuraSet.equals("CIE")) {
						if (!txtDivisaPag.equals(""))
							vsDivisaPag = txtDivisaPag;
						else
							vsDivisaPag = paramAmort.get(i).get("divisa");

						listaB=buscaBeneficiario(vsDivisaPag,paramAmort.get(i).get("equivalente"));
						if(listaB.isEmpty()){
							mensajes.add("El banco beneficiario no tiene cuenta de cheques");
							mapResult.put("msgUsuario", mensajes);
							return mapResult;
						}else{
							sBanco = Integer.parseInt(listaB.get(0).getBanco());
							sChequera = listaB.get(0).getChequera();
							vsProveedor = listaB.get(0).getProveedor();
							vlNoPersona = listaB.get(0).getNoPersona();
						}
					} else if (paramAmort.get(i).get("formaPago").equals("3")) {

						listaB=buscaBeneficiario("", paramAmort.get(i).get("equivalente"));
						if(listaB.isEmpty()){
							mensajes.add("El banco beneficiario no tiene cuenta de cheques");
							mapResult.put("msgUsuario", mensajes);
							return mapResult;

						}else{
							sBanco = Integer.parseInt(listaB.get(0).getBanco());
							sChequera = listaB.get(0).getChequera();
							vsProveedor = listaB.get(0).getProveedor();
							vlNoPersona = listaB.get(0).getNoPersona();
						}
					}
					if (!vbFolio) {
						consecutivo = folioCupos(globalSingleton.getUsuarioLoginDto().getIdUsuario());
						if (consecutivo < 0) {
							mensajes.add(" En cat_usuario el folio para la Clave Control es NULO para el usuario Num. "
									+ globalSingleton.getUsuarioLoginDto().getIdUsuario());
						}
						String fecha[] = paramAmort.get(i).get("fecVen").split("/");
						clave = vsClave + consecutivo + fecha[0] + fecha[1] + fecha[2]
								+ globalSingleton.getUsuarioLoginDto().getIdUsuario();
						vbFolio = true;
					}
					listC = vencimientoFinanciamientoCDao.selectContratoCredito(paramAmort.get(i).get("idContrato"));
					if (listC.size() > 0) {
						vsDivisa = listC.get(0).getIdDivisa();
						vsAcreedor = listC.get(0).getIdBancoPrestamo();
						vBanco = listC.get(0).getIdBanco();
						vChequera = listC.get(0).getIdClabe();
					}

					if (!txtDivisaPag.equals("")&&txtBancoPag>0&&!cmbChequeraPag.equals("")){
						vsDivisa =txtDivisaPag.trim();
						vBanco =txtBancoPag;
						vChequera =cmbChequeraPag.trim();

						if (txtTipoCambio!= 0 &&txtDivisaPag.equals("MN")){

							vdCapital =Double.parseDouble(paramAmort.get(i).get("capital")) * txtTipoCambio;
							vdInteres = Double.parseDouble(paramAmort.get(i).get("interes"))*txtTipoCambio;
							vdIva = Double.parseDouble(paramAmort.get(i).get("iva"))* txtTipoCambio;

						}
						else if (txtTipoCambio!=0&&txtDivisaPag.equals("DLS")) {

							vdCapital = Double.parseDouble(paramAmort.get(i).get("capital")) /txtTipoCambio;
							vdInteres =  Double.parseDouble(paramAmort.get(i).get("interes")) / txtTipoCambio;
							vdIva =  Double.parseDouble(paramAmort.get(i).get("iva")) /txtTipoCambio;
							//}
						}
					}
					else{
						vdCapital = Double.parseDouble(paramAmort.get(i).get("capital"));
						vdInteres = Double.parseDouble(paramAmort.get(i).get("interes"));
						vdIva =  Double.parseDouble(paramAmort.get(i).get("iva"));
						//}
					}

					valorMensaje=interesAgrupadas(i,vdCapital,vdInteres,vdIva, paramAmort, vsDivisa,vBanco,vChequera,vsAcreedor,plFolioAnt,clave,bAgrupado,
							sBanco,  sChequera, vsProveedor, vlNoPersona, par,txtTipoCambio,0);

					if(valorMensaje.equals("Error1")){
						mensajes.add("Error al guardar datos para el generador (3001)");
						mapResult.put("msgUsuario", mensajes);
						return mapResult;
					}
					else if(valorMensaje.equals("Error2")){
						mensajes.add("Error en el generador (C)");
						mapResult.put("msgUsuario", mensajes);
						return mapResult;
					}
					else if(valorMensaje.equals("Error3")){
						mensajes.add("Error al Actualizar el Movimiento (3000, 3001) Campo: id_contable ");
						mapResult.put("msgUsuario", mensajes);
						return mapResult;
					}
					else if(valorMensaje.equals("Error5")){
						mensajes.add("Error al actualizar el importe. ");
						mapResult.put("msgUsuario", mensajes);
						return mapResult;
					}
				}
			}
			if(configuraSet.equals("CIE")&&vsTipoMenu=="A"){
				mensajes.add("Se genero la orden de servicio correctamente ");
				mapResult.put("msgUsuario", mensajes);
			}else{
				String vsFechaPropuesta="",fechaVenAux="",fechaVenHoy="";
				i=i-1;
				while(i>=0){
					int plresp = 0;
					if(paramAmort.get(i).get("checked").equals("true")){
						Date fechaVen = funciones.ponerFechaDate(paramAmort.get(i).get("fecVen").toString());
						fechaVenAux=funciones.ponerFechaSola(paramAmort.get(i).get("fecVen"));
						fechaVenHoy=funciones.ponerFechaSola(fechaHoy);
						//fechaVen menor a fechaHoy 
						pagoTotal=Double.parseDouble(valorMensaje.trim());
						if(fechaVen.compareTo(fechaHoy)<0){
							plresp = vencimientoFinanciamientoCDao.insertaEncabezado(0, paramAmort.get(i).get("gpoEmpresa"), clave, 
									fechaVenHoy, pagoTotal, fechaVenHoy,0);
							vsFechaPropuesta = fechaVenHoy;
						}
						else{
							plresp = vencimientoFinanciamientoCDao.insertaEncabezado(0,paramAmort.get(i).get("gpoEmpresa"), clave, 
									fechaVenAux, pagoTotal,fechaVenAux ,0);
							vsFechaPropuesta = fechaVenAux ;
						}
						idGrupoFlujo= vencimientoFinanciamientoCDao.obtenerGrupoFlujo(Integer.parseInt(paramAmort.get(i).get("empresa")));
						String concepto = "M" + "-" + "MANUAL" + "-" + globalSingleton.getUsuarioLoginDto().getClaveUsuario() + "-" + idGrupoFlujo + "-" + formato.format(fechaHoy) + "-" +  paramAmort.get(i).get("divisa");

						vencimientoFinanciamientoCDao.updateMovtosFecPropuesta(vsFechaPropuesta, clave,concepto);
						if(plresp<=0){
							mensajes.add("Error al Crear Registro del Encabezado de la Propuesta.");
							mapResult.put("msgUsuario", mensajes);
							return mapResult;
						}
						else{
							break;
						}
					}
					i = i - 1;
				}
				mensajes.add("Se efectuaron los pagos correctamente con la propuesta " + clave +" ");
				mapResult.put("msgUsuario", mensajes);
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCAction, M:pagoAnticipadoParcial");
		}
		return mapResult;
	}

	public Map<String, Object> pagoAmortizacionesMezcla(String amortizaciones,String agrupadas,String separadas, String txtDivisaPag,int txtBancoPag,String cmbChequeraPag,double txtTipoCambio) {

		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		Gson gson = new Gson();
		List<ContratoCreditoDto> listC = new ArrayList<ContratoCreditoDto>();
		List<Retorno> list = null;
		boolean vbRegistra = false, bAgrupado = false, vbFolio = false;
		String vsClave = "", configuraSet, vsDivisaPag, clave = "", vsDivisa="", vChequera = "",vsConcepto,valorMensaje="",sChequera="", vsProveedor = "";
		int plFolioAnt = 0, consecutivo, vsAcreedor=0, vBanco = 0,x=0,vsNoDocto,sBanco=0, vlNoPersona=0,i=0,idGrupoFlujo=0, par=0;
		double vdCapital = 0,vdInteres = 0,vdIva = 0,vdImporte;
		Date fechaI;
		SimpleDateFormat formato = new SimpleDateFormat("ddMMyyyy");
		globalSingleton = GlobalSingleton.getInstancia();
		List<Map<String, String>> paramAmort = gson.fromJson(amortizaciones,
				new TypeToken<ArrayList<Map<String, String>>>() {
		}.getType());

		List<Map<String, String>>  paramAmortSeparadas= gson.fromJson(separadas,
				new TypeToken<ArrayList<Map<String, String>>>() {
		}.getType());
		List<Map<String, String>>  paramAmortAgrupadas= gson.fromJson(agrupadas,
				new TypeToken<ArrayList<Map<String, String>>>() {
		}.getType());

		List<BeneficiarioDto> listaB = new ArrayList<BeneficiarioDto>();
		try {
			Date fechaHoy = vencimientoFinanciamientoCDao.obtenerFechaHoy();
			list = vencimientoFinanciamientoCDao.consultarConfiguraSet();
			configuraSet = list.get(0).getValorConfiguraSet();
			if (vsTipoMenu.equals(""))
				vsClave = "MC";
			if (vsTipoMenu.equals("B"))
				vsClave = "MB";
			if (vsTipoMenu.equals("F"))
				vsClave = "MD";
			if (vsTipoMenu.equals("A"))
				vsClave = "MR";
			for (i = 0; i < paramAmortSeparadas.size(); i++) {
				if (paramAmortSeparadas.get(i).get("checked").equals("true")
						&& !paramAmortSeparadas.get(i).get("valorPago").equals("-")) {

					sBanco = 0;
					sChequera = "";
					if (configuraSet.equals("CIE")) {
						if (!txtDivisaPag.equals(""))
							vsDivisaPag = txtDivisaPag;
						else
							vsDivisaPag = paramAmortSeparadas.get(i).get("divisa");

						listaB=buscaBeneficiario(vsDivisaPag,paramAmortSeparadas.get(i).get("equivalente"));
						if(listaB.isEmpty()){
							mensajes.add("El banco beneficiario no tiene cuenta de cheques");
							mapResult.put("msgUsuario", mensajes);
							return mapResult;
						}else{
							sBanco = Integer.parseInt(listaB.get(0).getBanco());
							sChequera = listaB.get(0).getChequera();
							vsProveedor = listaB.get(0).getProveedor();
							vlNoPersona = listaB.get(0).getNoPersona();
						}
					} else if (paramAmortSeparadas.get(i).get("formaPago").equals("3")) {

						listaB=buscaBeneficiario("", paramAmortSeparadas.get(i).get("equivalente"));
						if(listaB.isEmpty()){
							mensajes.add("El banco beneficiario no tiene cuenta de cheques");
							mapResult.put("msgUsuario", mensajes);
							return mapResult;

						}else{
							sBanco = Integer.parseInt(listaB.get(0).getBanco());
							sChequera = listaB.get(0).getChequera();
							vsProveedor = listaB.get(0).getProveedor();
							vlNoPersona = listaB.get(0).getNoPersona();
						}
					}
					if (!vbFolio) {
						consecutivo = folioCupos(globalSingleton.getUsuarioLoginDto().getIdUsuario());
						if (consecutivo < 0) {
							mensajes.add(" En cat_usuario el folio para la Clave Control es NULO para el usuario Num. "
									+ globalSingleton.getUsuarioLoginDto().getIdUsuario());
						}
						String fecha[] = paramAmortSeparadas.get(i).get("fecVen").split("/");
						clave = vsClave + consecutivo + fecha[0] + fecha[1] + fecha[2]
								+ globalSingleton.getUsuarioLoginDto().getIdUsuario();
						vbFolio = true;
					}
					listC = vencimientoFinanciamientoCDao.selectContratoCredito(paramAmortSeparadas.get(i).get("idContrato"));
					if (listC.size() > 0) {
						vsDivisa = listC.get(0).getIdDivisa();
						vsAcreedor = listC.get(0).getIdBancoPrestamo();
						vBanco = listC.get(0).getIdBanco();
						vChequera = listC.get(0).getIdClabe();
					}

					if (!txtDivisaPag.equals("")&&txtBancoPag>0&&!cmbChequeraPag.equals("")){
						vsDivisa =txtDivisaPag.trim();
						vBanco =txtBancoPag;
						vChequera =cmbChequeraPag.trim();

						if (txtTipoCambio!= 0 &&txtDivisaPag.equals("MN")){

							vdCapital =Double.parseDouble(paramAmortSeparadas.get(i).get("capital")) * txtTipoCambio;
							vdInteres = Double.parseDouble(paramAmortSeparadas.get(i).get("interes"))*txtTipoCambio;
							vdIva = Double.parseDouble(paramAmortSeparadas.get(i).get("iva"))* txtTipoCambio;

						}
						else if (txtTipoCambio!=0&&txtDivisaPag.equals("DLS")) {

							vdCapital = Double.parseDouble(paramAmortSeparadas.get(i).get("capital")) /txtTipoCambio;
							vdInteres =  Double.parseDouble(paramAmortSeparadas.get(i).get("interes")) / txtTipoCambio;
							vdIva =  Double.parseDouble(paramAmortSeparadas.get(i).get("iva")) /txtTipoCambio;
							//}
						}
					}
					else{
						vdCapital = Double.parseDouble(paramAmortSeparadas.get(i).get("capital"));
						vdInteres = Double.parseDouble(paramAmortSeparadas.get(i).get("interes"));
						vdIva =  Double.parseDouble(paramAmortSeparadas.get(i).get("iva"));
						//}
					}

					valorMensaje=interes(i,vdCapital,vdInteres,vdIva, paramAmortSeparadas, vsDivisa,vBanco,vChequera,vsAcreedor,plFolioAnt,clave,bAgrupado,
							sBanco,  sChequera, vsProveedor, vlNoPersona);

					if(valorMensaje.equals("Error1")){
						mensajes.add("Error al guardar datos para el generador (3001)");
						mapResult.put("msgUsuario", mensajes);
						return mapResult;
					}
					else if(valorMensaje.equals("Error2")){
						mensajes.add("Error en el generador (C)");
						mapResult.put("msgUsuario", mensajes);
						return mapResult;
					}
					else if(valorMensaje.equals("Error3")){
						mensajes.add("Error al Actualizar el Movimiento (3000, 3001) Campo: id_contable ");
						mapResult.put("msgUsuario", mensajes);
						return mapResult;
					}
					else if(valorMensaje.equals("Error5")){
						mensajes.add("Error al actualizar el importe. ");
						mapResult.put("msgUsuario", mensajes);
						return mapResult;
					}
				}
			}
			vdImporteAux=0;
			grupo=0;noFactura=0;
			for (i = 0; i < paramAmortAgrupadas.size(); i++) {
				if (paramAmortAgrupadas.get(i).get("checked").equals("true")
						&& !paramAmortAgrupadas.get(i).get("valorPago").equals("-")) {
					if (paramAmortAgrupadas.get(i).get("idAmortizacion").equals("0")) {
						par=0;
						vdImporteAux=0;
					}
					else
						par=1;
					sBanco = 0;
					sChequera = "";
					if (configuraSet.equals("CIE")) {
						if (!txtDivisaPag.equals(""))
							vsDivisaPag = txtDivisaPag;
						else
							vsDivisaPag = paramAmortAgrupadas.get(i).get("divisa");

						listaB=buscaBeneficiario(vsDivisaPag,paramAmortAgrupadas.get(i).get("equivalente"));
						if(listaB.isEmpty()){
							mensajes.add("El banco beneficiario no tiene cuenta de cheques");
							mapResult.put("msgUsuario", mensajes);
							return mapResult;
						}else{
							sBanco = Integer.parseInt(listaB.get(0).getBanco());
							sChequera = listaB.get(0).getChequera();
							vsProveedor = listaB.get(0).getProveedor();
							vlNoPersona = listaB.get(0).getNoPersona();
						}
					} else if (paramAmortAgrupadas.get(i).get("formaPago").equals("3")) {

						listaB=buscaBeneficiario("", paramAmortAgrupadas.get(i).get("equivalente"));
						if(listaB.isEmpty()){
							mensajes.add("El banco beneficiario no tiene cuenta de cheques");
							mapResult.put("msgUsuario", mensajes);
							return mapResult;

						}else{
							sBanco = Integer.parseInt(listaB.get(0).getBanco());
							sChequera = listaB.get(0).getChequera();
							vsProveedor = listaB.get(0).getProveedor();
							vlNoPersona = listaB.get(0).getNoPersona();
						}
					}
					if (!vbFolio) {
						consecutivo = folioCupos(globalSingleton.getUsuarioLoginDto().getIdUsuario());
						if (consecutivo < 0) {
							mensajes.add(" En cat_usuario el folio para la Clave Control es NULO para el usuario Num. "
									+ globalSingleton.getUsuarioLoginDto().getIdUsuario());
						}
						String fecha[] = paramAmortAgrupadas.get(i).get("fecVen").split("/");
						clave = vsClave + consecutivo + fecha[0] + fecha[1] + fecha[2]
								+ globalSingleton.getUsuarioLoginDto().getIdUsuario();
						vbFolio = true;
					}
					listC = vencimientoFinanciamientoCDao.selectContratoCredito(paramAmortAgrupadas.get(i).get("idContrato"));
					if (listC.size() > 0) {
						vsDivisa = listC.get(0).getIdDivisa();
						vsAcreedor = listC.get(0).getIdBancoPrestamo();
						vBanco = listC.get(0).getIdBanco();
						vChequera = listC.get(0).getIdClabe();
					}

					if (!txtDivisaPag.equals("")&&txtBancoPag>0&&!cmbChequeraPag.equals("")){
						vsDivisa =txtDivisaPag.trim();
						vBanco =txtBancoPag;
						vChequera =cmbChequeraPag.trim();

						if (txtTipoCambio!= 0 &&txtDivisaPag.equals("MN")){

							vdCapital =Double.parseDouble(paramAmortAgrupadas.get(i).get("capital")) * txtTipoCambio;
							vdInteres = Double.parseDouble(paramAmortAgrupadas.get(i).get("interes"))*txtTipoCambio;
							vdIva = Double.parseDouble(paramAmortAgrupadas.get(i).get("iva"))* txtTipoCambio;

						}
						else if (txtTipoCambio!=0&&txtDivisaPag.equals("DLS")) {

							vdCapital = Double.parseDouble(paramAmortAgrupadas.get(i).get("capital")) /txtTipoCambio;
							vdInteres =  Double.parseDouble(paramAmortAgrupadas.get(i).get("interes")) / txtTipoCambio;
							vdIva =  Double.parseDouble(paramAmortAgrupadas.get(i).get("iva")) /txtTipoCambio;
							//}
						}
					}
					else{
						vdCapital = Double.parseDouble(paramAmortAgrupadas.get(i).get("capital"));
						vdInteres = Double.parseDouble(paramAmortAgrupadas.get(i).get("interes"));
						vdIva =  Double.parseDouble(paramAmortAgrupadas.get(i).get("iva"));
						//}
					}

					valorMensaje=interesAgrupadas(i,vdCapital,vdInteres,vdIva, paramAmortAgrupadas, vsDivisa,vBanco,vChequera,vsAcreedor,plFolioAnt,clave,bAgrupado,
							sBanco,  sChequera, vsProveedor, vlNoPersona, par,txtTipoCambio,0);

					if(valorMensaje.equals("Error1")){
						mensajes.add("Error al guardar datos para el generador (3001)");
						mapResult.put("msgUsuario", mensajes);
						return mapResult;
					}
					else if(valorMensaje.equals("Error2")){
						mensajes.add("Error en el generador (C)");
						mapResult.put("msgUsuario", mensajes);
						return mapResult;
					}
					else if(valorMensaje.equals("Error3")){
						mensajes.add("Error al Actualizar el Movimiento (3000, 3001) Campo: id_contable ");
						mapResult.put("msgUsuario", mensajes);
						return mapResult;
					}
					else if(valorMensaje.equals("Error5")){
						mensajes.add("Error al actualizar el importe. ");
						mapResult.put("msgUsuario", mensajes);
						return mapResult;
					}
				}
			}

			if(configuraSet.equals("CIE")&&vsTipoMenu=="A"){
				mensajes.add("Se genero la orden de servicio correctamente ");
				mapResult.put("msgUsuario", mensajes);
			}else{
				String vsFechaPropuesta="",fechaVenAux="",fechaVenHoy="";
				i=i-1;
				while(i>=0){
					int plresp = 0;
					if(paramAmort.get(i).get("checked").equals("true")){
						Date fechaVen = funciones.ponerFechaDate(paramAmort.get(i).get("fecVen").toString());
						fechaVenAux=funciones.ponerFechaSola(paramAmort.get(i).get("fecVen"));
						fechaVenHoy=funciones.ponerFechaSola(fechaHoy);
						//fechaVen menor a fechaHoy 
						pagoTotal=Double.parseDouble(valorMensaje.trim());
						if(fechaVen.compareTo(fechaHoy)<0){
							plresp = vencimientoFinanciamientoCDao.insertaEncabezado(0, paramAmort.get(i).get("gpoEmpresa"), clave, 
									fechaVenHoy, pagoTotal, fechaVenHoy,0);
							vsFechaPropuesta = fechaVenHoy;
						}
						else{
							plresp = vencimientoFinanciamientoCDao.insertaEncabezado(0,paramAmort.get(i).get("gpoEmpresa"), clave, 
									fechaVenAux, pagoTotal,fechaVenAux ,0);
							vsFechaPropuesta = fechaVenAux ;
						}
						idGrupoFlujo= vencimientoFinanciamientoCDao.obtenerGrupoFlujo(Integer.parseInt(paramAmort.get(i).get("empresa")));
						String concepto = "M" + "-" + "MANUAL" + "-" + globalSingleton.getUsuarioLoginDto().getClaveUsuario() + "-" + idGrupoFlujo + "-" + formato.format(fechaHoy) + "-" +  paramAmort.get(i).get("divisa");

						vencimientoFinanciamientoCDao.updateMovtosFecPropuesta(vsFechaPropuesta, clave,concepto);

						if(plresp<=0){
							mensajes.add("Error al Crear Registro del Encabezado de la Propuesta.");
							mapResult.put("msgUsuario", mensajes);
							return mapResult;
						}
						else{
							break;
						}
					}
					i = i - 1;
				}
				mensajes.add("Se efectuaron los pagos correctamente con la propuesta " + clave +" ");
				mapResult.put("msgUsuario", mensajes);
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCAction, M:pagoAnticipadoParcial");
		}
		return mapResult;

	}
	public String interesAgrupadas(int i,double vdCapital,double vdInteres, double vdIva, 
			List<Map<String, String>> paramAmort,String vsDivisa,int vBanco, 
			String vChequera,int vsAcreedor,int plFolioAnt,String clave, boolean bAgrupado,
			int sBanco, String sChequera,String vsProveedor,int vlNoPersona, int par,double txtTipoCambio,int xAux){
		System.out.println("vdImporteAux 2: "+vdImporteAux);
		int x = 0,vsNoDocto;
		String valorMensaje="";
		double vdImporte = 0;
		String vsConcepto = "";
		if(vdCapital!=0) {
			vdImporte = vdCapital;
			if (!paramAmort.get(i).get("valorPago").equals("")){
				vsConcepto = "Pago Capital";
				x = 2;
			}
			else{
				vsConcepto = "Pago Capital";
				x = 1;
				;
			}
			vdCapital = 0;
		} 
		else if (vdInteres != 0 ){
			vdImporte = vdInteres;
			System.out.println("En interes");
			vsConcepto = "Pago Interes";
			vdInteres = 0;
			x = 2;
		}
		else if (vdIva !=0){
			vdImporte = vdIva;
			if (!paramAmort.get(i).get("valorPago").equals(""))
				vsConcepto = "Pago Iva";
			else
				vsConcepto = "Pago Iva";
			vdIva = 0;
			x = 3;
			System.out.println("En iva");
			banIva=true;
		}
		pagoTotal = pagoTotal + vdImporte;
		vsNoDocto =Integer.parseInt(paramAmort.get(i).get("idDisposicion"));
		if (x!=0){
			String resultado= insertaParametroAgrupada(i, vdImporte, vsDivisa, vsConcepto, "N", vBanco, 
					vChequera, vdIva, vsNoDocto, vsAcreedor, sBanco, 
					sChequera, clave, vsProveedor, vlNoPersona, bAgrupado, 
					plFolioAnt,paramAmort, par, txtTipoCambio,0);
			if(!resultado.equals(""))
				return resultado;
			int plresp = vencimientoFinanciamientoCDao.updateAmortizacion1(paramAmort.get(i).get("idContrato"),
					paramAmort.get(i).get("idDisposicion"),
					paramAmort.get(i).get("idAmortizacion"),
					paramAmort.get(i).get("fecVen"), "P", clave, vsTipoMenu);

			confirmaLiqCred(i,paramAmort);
		}
		if(x == 2){
			interesAgrupadas(i,vdCapital,vdInteres,vdIva, paramAmort, vsDivisa,vBanco,vChequera,vsAcreedor,plFolioAnt,clave,bAgrupado,
					sBanco,  sChequera, vsProveedor, vlNoPersona,par,txtTipoCambio,x);

		}
		else if (x == 3){
			interesAgrupadas(i,vdCapital,vdInteres,vdIva, paramAmort, vsDivisa,vBanco,vChequera,vsAcreedor,plFolioAnt,clave,bAgrupado,
					sBanco,  sChequera, vsProveedor, vlNoPersona,par,txtTipoCambio,x);

		}
		//End If
		valorMensaje=pagoTotal+"";
		return valorMensaje;
	}
	private String insertaParametroAgrupada(int i, double vdImporte, String vsDivisa, String vsConcepto, String vsEstatus,
			int vBanco, String vChequera, double vdIva, int vsNoDocto, int vsAcreedor, int sBanco, String sChequera,
			String clave, String vsProveedor, int vlNoPersona, boolean bAgrupado, int plFolioAnt,List<Map<String, String>> paramAmort, int par,double txtTipoCambio, int x) {
		int plFolio,lAfectados=0,pdFolioMov,pdFolioDet;
		Date date = new Date();
		Map<String, Object> mapGenerador = new HashMap<String, Object>();
		GeneradorDto generador = new GeneradorDto();
		DateFormat formatoHora = new SimpleDateFormat("HH:mm");
		String configuraSet,vsDivision = "",vsRubro,resultado="";
		List<Retorno> list = null;
		Date fechaHoy = vencimientoFinanciamientoCDao.obtenerFechaHoy();
		int invoiceType=0, idGrupo=0,area=0;
		String noRecibo="";
		globalSingleton = GlobalSingleton.getInstancia();
		try {
			list = vencimientoFinanciamientoCDao.consultarConfiguraSet();
			configuraSet = list.get(0).getValorConfiguraSet();
			if(configuraSet.equals("CIE")&&!vsTipoMenu.equals("A")){
				List<AmortizacionCreditoDto> lista = new ArrayList<AmortizacionCreditoDto>();
				lista= vencimientoFinanciamientoCDao.selectDivision(Integer.parseInt(paramAmort.get(i).get("bancoBenef")), paramAmort.get(i).get("chequeraBenef"));
				vBanco =Integer.parseInt(paramAmort.get(i).get("bancoBenef"));
				vChequera = paramAmort.get(i).get("chequeraBenef");
				if(!lista.isEmpty())
					vsDivision = lista.get(0).getDivision();
			}
			vsRubro = "41201";
			if(configuraSet.equals("CIE"))
				vsRubro = "2400500";
			System.out.println("X="+x);
			if(par==0&&!banIva){
				plFolio = vencimientoFinanciamientoCDao.seleccionarFolioReal("no_folio_param");
				grupo=plFolio;
				plFolioAnt = grupo;
				Parametro dto=new Parametro();
				noFactura=vsNoDocto;
				noRecibo=null;
				area=1;
				//Inserta importe en cero
				lAfectados = vencimientoFinanciamientoCDao.inserta1Agrupado(paramAmort.get(i).get("empresa"), plFolio, 41, paramAmort.get(i).get("formaPago"), 
						"3000", globalSingleton.getUsuarioLoginDto().getIdUsuario(), paramAmort.get(i).get("idDisposicion"), vlNoPersona, 
						paramAmort.get(i).get("fecVen"), paramAmort.get(i).get("fecVen"), funciones.ponerFecha(fechaHoy),funciones.ponerFecha(fechaHoy), 
						0, 0,globalSingleton.getUsuarioLoginDto().getIdCaja(), vsDivisa, vsDivisa, "CRD", "", 
						"Egreso por Credito Bancario " + paramAmort.get(i).get("idContrato"), 1, "C", "S", "P", vBanco, vChequera, "", 
						"CRD", "Egreso por Credito Bancario" +paramAmort.get(i).get("idDisposicion"), 1, 0, 
						null, plFolioAnt, null, 2, "P"
								+ "A", "2", formatoHora.format(date), "30301001", vsDivision, noRecibo, 0, vsNoDocto, 
								vsProveedor, vsAcreedor, sBanco, sChequera, "", paramAmort.get(i).get("idContrato"),noFactura,txtTipoCambio,"1",grupo,invoiceType,idGrupo, area,null,0,0);
				if(lAfectados<=0) {
					//MsgBox "ï¿½Error al Guardar Datos Para el Generador! (3000)", vbExclamation, "SET"
					return "Error1";
				}
				//interes
				area=1;
				invoiceType=1;
				idGrupo=7000;
				plFolio = vencimientoFinanciamientoCDao.seleccionarFolioReal("no_folio_param");
				lAfectados = vencimientoFinanciamientoCDao.inserta1Agrupado(paramAmort.get(i).get("empresa"), plFolio, 41, paramAmort.get(i).get("formaPago"), 
						"3001", globalSingleton.getUsuarioLoginDto().getIdUsuario(), paramAmort.get(i).get("idDisposicion"), vlNoPersona, 
						paramAmort.get(i).get("fecVen"), paramAmort.get(i).get("fecVen"), funciones.ponerFecha(fechaHoy),funciones.ponerFecha(fechaHoy), 
						vdImporte, vdImporte,globalSingleton.getUsuarioLoginDto().getIdCaja(), vsDivisa, vsDivisa, "CRD", "", 
						vsConcepto  + paramAmort.get(i).get("idDisposicion"), 1, "H", "S", "P", vBanco, vChequera, "", 
						"CRD", "Egreso por Credito Bancario" +paramAmort.get(i).get("idDisposicion"), 1, 0, 
						"1", plFolioAnt, null, 2, "A", "2", formatoHora.format(date), "70000005", vsDivision, null, 0, vsNoDocto, 
						vsProveedor, vsAcreedor, sBanco, sChequera, "", paramAmort.get(i).get("idContrato"),noFactura,txtTipoCambio,"1",grupo,invoiceType,idGrupo,area,"1",0,0);


				if(lAfectados<=0) {
					//MsgBox "ï¿½Error al Guardar Datos Para el Generador! (3000)", vbExclamation, "SET"
					return "Error1";
				}
				System.out.println("vdImporteAux 5: "+vdImporteAux);
				vdImporteAux=vdImporteAux+vdImporte;
				System.out.println("ImporteAux:"+vdImporteAux);


			}
			else if(par==0&&banIva){
				//IVA
				area=1;
				invoiceType=2;
				idGrupo=2020;	
				plFolio = vencimientoFinanciamientoCDao.seleccionarFolioReal("no_folio_param");
				lAfectados = vencimientoFinanciamientoCDao.inserta1Agrupado(paramAmort.get(i).get("empresa"), plFolio, 41, paramAmort.get(i).get("formaPago"), 
						"3001", globalSingleton.getUsuarioLoginDto().getIdUsuario(), paramAmort.get(i).get("idDisposicion"), vlNoPersona, 
						paramAmort.get(i).get("fecVen"), paramAmort.get(i).get("fecVen"), funciones.ponerFecha(fechaHoy),funciones.ponerFecha(fechaHoy), 
						vdImporte, vdImporte,globalSingleton.getUsuarioLoginDto().getIdCaja(), vsDivisa, vsDivisa, "CRD", "", 
						vsConcepto  + paramAmort.get(i).get("idDisposicion"), 1, "H", "S", "P", vBanco, vChequera, "", 
						"CRD", "Egreso por Credito Bancario" +paramAmort.get(i).get("idDisposicion"), 1, 0, 
						"2", plFolioAnt, null, 2, "A", "1", formatoHora.format(date), "20205100", vsDivision, null, 0, vsNoDocto, 
						vsProveedor, vsAcreedor, sBanco, sChequera, "", paramAmort.get(i).get("idContrato"),noFactura,txtTipoCambio,"1",grupo,invoiceType,idGrupo,area,"1",0,0);
				vdImporteAux=vdImporteAux+vdImporte;
				banIva=false;
				if(lAfectados<=0) {
					//MsgBox "ï¿½Error al Guardar Datos Para el Generador! (3000)", vbExclamation, "SET"
					return "Error1";
				}
			}
			else{
				invoiceType=3;
				area=1;
				//Capital
				idGrupo=3030;
				plFolio = vencimientoFinanciamientoCDao.seleccionarFolioReal("no_folio_param");
				lAfectados = vencimientoFinanciamientoCDao.inserta1Agrupado(paramAmort.get(i).get("empresa"), plFolio, 41, paramAmort.get(i).get("formaPago"), 
						"3001", globalSingleton.getUsuarioLoginDto().getIdUsuario(), paramAmort.get(i).get("idDisposicion"), vlNoPersona, 
						paramAmort.get(i).get("fecVen"), paramAmort.get(i).get("fecVen"), funciones.ponerFecha(fechaHoy),funciones.ponerFecha(fechaHoy), 
						vdImporte, vdImporte,globalSingleton.getUsuarioLoginDto().getIdCaja(), vsDivisa, vsDivisa, "CRD", "", 
						vsConcepto  + paramAmort.get(i).get("idDisposicion"), 1, "H", "S", "P", vBanco, vChequera, "", 
						"CRD", "Egreso por Credito Bancario" +paramAmort.get(i).get("idDisposicion"), 1, 0, 
						"2", plFolioAnt, null, 2, "A", "1", formatoHora.format(date), "30301001", vsDivision, null, 0, vsNoDocto, 
						vsProveedor, vsAcreedor, sBanco, sChequera, "", paramAmort.get(i).get("idContrato"),noFactura,txtTipoCambio,"1",grupo,invoiceType,idGrupo,area,"1",0,0);


				if(lAfectados<=0) {
					//MsgBox "ï¿½Error al Guardar Datos Para el Generador! (3000)", vbExclamation, "SET"
					return "Error1";
				}
				vdImporteAux=vdImporteAux+vdImporte;
				//Actualización del importe (3000)
				lAfectados = vencimientoFinanciamientoCDao.actualizaImporteParametro(vdImporteAux,grupo);
				if(lAfectados<=0) {
					//MsgBox "ï¿½Error al Guardar Datos Para el Generador! (3000)", vbExclamation, "SET"
					return "Error5";
				}
				pdFolioMov = 0;
				pdFolioDet = 0;
				lAfectados = 0;
				//Después de insertar las 3001, actualiza el importe de la 3000 y llama al generador
				mapGenerador = new HashMap<String, Object>();
				generador.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
				generador.setEmpresa(Integer.parseInt(paramAmort.get(i).get("empresa")));
				generador.setFolParam(grupo);
				generador.setFolMovi(pdFolioMov);
				generador.setFolDeta(pdFolioDet);
				System.out.println(generador.toString());
				mapGenerador = vencimientoFinanciamientoCDao.generador(generador);
				lAfectados=Integer.parseInt(mapGenerador.get("result").toString());
				if (Integer.parseInt(mapGenerador.get("result").toString()) > 0) {
					return "Error2";
					// MsgBox "Error en el generador (C)", vbExclamation, "SET"
				}else{
					pdFolioMov=Integer.parseInt(mapGenerador.get("movi").toString());
					pdFolioDet=Integer.parseInt(mapGenerador.get("deta").toString());
					lAfectados=vencimientoFinanciamientoCDao.updateMovCre(pdFolioMov, pdFolioDet, clave,funciones.ponerFecha(fechaHoy), paramAmort.get(i).get("idContrato"));
					if(lAfectados<=0){
						return "Error3";
						//  MsgBox "Error al Actualizar el Movimiento (3000, 3001) Campo: id_contable ", vbExclamation, "SET"
					} 

				}
				grupo=0;
				invoiceType=0;
				idGrupo=0;
				par=0;
				noFactura=0;
				vdImporteAux=0;
			}	
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCAction, M:pagoAnticipadoParcial");
		}
		return resultado;
	}
}
