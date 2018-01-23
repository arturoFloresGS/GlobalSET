package com.webset.set.bancaelectronica.business;

//Util
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.object.StoredProcedure;

//Apps

import com.webset.set.bancaelectronica.dao.AdministradorArchivosDao;
import com.webset.set.bancaelectronica.dto.CatCtaBancoDto;
import com.webset.set.bancaelectronica.dto.MovtoBancaEDto;
import com.webset.set.bancaelectronica.dto.ParametroBancomerDto;
import com.webset.set.bancaelectronica.dto.ParametroBuscarEmpresaDto;
import com.webset.set.bancaelectronica.dto.ParametroLayoutDto;
import com.webset.set.financiamiento.dao.VencimientoFinanciamientoCDao;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.BusquedaArchivos;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.ComunDto;
import com.webset.set.utilerias.dto.Retorno;

/**
 * @author Sergio Vaca
 * @since 10/Novimebre/2010
 */
public class AdministradorArchivosBusiness {
	public final int BANAMEX = 2;
	public final int BANCOMER = 12;
	public final int SANTANDER = 14;
	public final int HSBC = 21;
	// public final int INBURSA = 36;
	public final int INVERLAT = 44;
	public final int BANORTE = 72;
	// public final int AMROBANK = 102;
	// public final int AZTECA = 127;
	public final int CITIBANK = 1026;
	// public final int HSBCDLS = 1229;

	private static Logger logger = Logger.getLogger(AdministradorArchivosBusiness.class);
	
	private Bitacora bitacora = new Bitacora();
	
	public boolean lbReferenciaPorChequera;
	public boolean lbReferenciaNumerica;
	public int liLongitudReferencia;
	public int idUsuario;
	private JdbcTemplate jdbcTemplate;
	private AdministradorArchivosDao administradorArchivosDao;
	private BusquedaArchivos busquedaArchivos;
	private ParametroBuscarEmpresaDto bEmpresa;
	private ParametroLayoutDto parametro;
	private ParametroLayoutDto insercion;
	private Funciones funciones;
	
	
	public ArrayList<String> mensajes = new ArrayList<String>();
	public ArrayList<String> mensajesUsuario = new ArrayList<String>();
	
	private boolean control;
	private boolean bExisteChequera;
	private boolean bActualizarSaldo;
	private boolean pbHSBCMismoDia;
	private boolean bEmpresaUsuario;
	private boolean archivoOk;

	private int i;
	private int z;
	private int renglon;
	private int piNoEmpresa;
	private int plFolio;
	private int iContador;
	private int piEncontrados;
	private int piEmpresaAnt;
	private int plRegLeidos;
	private int plRegSinChequera;
	private int piPosicion;

	private long plFolioBanco;

	private double dSaldoBanco;
	private double pdImporte;

	private String psArchivo;
	private String psArchivos;
	private String psFecha;
	private String psSBCobro;
	private String psRegistro;
	private String psChequera;
	private String psConcepto;
	private String psHora;
	private String psReferencia;
	private String sCargoAbono;
	private String sDescripcion;
	private String sSucursal;
	private String psHSBC;
	private String psCveConcepto;
	private String psMensaje;
	private String rutaActual;
	private String psChequerasInexistentes;
	private String psChequeraNueva;
	private String psSucursal;
	private String psCargoAbono;
	private String psSantander;

	private File[] nombres;
	private Scanner archivo = null;
	
	GlobalSingleton globalSingleton;
	
	/**
	 * 
	 * @param activar
	 * 
	 * inicializar variables
	 */
	public void inicializar() {
		busquedaArchivos = null;
		funciones = null;
		bEmpresa = null;
		parametro = null;
		insercion = null;

		control = false;
		bExisteChequera = false;
		bActualizarSaldo = false;
		pbHSBCMismoDia = false;
		bEmpresaUsuario = false;
		archivoOk = false;

		i = 0;
		z = 0;
		renglon = 0;
		piNoEmpresa = 0;
		plFolio = 0;
		iContador = 0;
		piEncontrados = 0;
		piEmpresaAnt = 0;
		plRegLeidos = 0;
		plRegSinChequera = 0;
		plFolioBanco = 0;
		piPosicion = 0;

		dSaldoBanco = 0;
		pdImporte = 0;

		psArchivo = null;
		psArchivos = null;
		psFecha = null;
		psSBCobro = null;
		psRegistro = null;
		psChequera = null;
		psConcepto = null;
		psHora = null;
		psReferencia = null;
		sCargoAbono = null;
		sDescripcion = null;
		sSucursal = null;
		psHSBC = null;
		psCveConcepto = null;
		psMensaje = null;
		rutaActual = null;
		psChequerasInexistentes = null;
		psChequeraNueva = null;
		psSucursal = null;
		psCargoAbono = null;
		psSantander = null;

		archivo = null;
		
		psChequera = "";
		psArchivos = "";
		psChequeraNueva = "";
		busquedaArchivos = new BusquedaArchivos();
		funciones = new Funciones();
		bEmpresaUsuario = true;
		rutaActual = System.getProperty("user.dir");
	}

	/**
	 * 
	 * @param dto
	 * @return
	 * 
	 * lee el layout de bital de tamano de renglon de 101
	 * 
	 * Private Function lee_bital_version_oficial(ByRef psArchivos As String, _
	 * ByRef ps_chequeras_inexistentes As String, _ ByRef plReg_leidos As Long,
	 * ByRef plReg_sin_chequera As Long, _ ByRef plReg_importados As Long) As
	 * Boolean
	 */
	public boolean leerHSBCVersionOficial(ParametroLayoutDto dto) {
		psChequera = "";
		psChequerasInexistentes = "";
		psArchivos = "";
		dto.setPsArchivos("");
		inicializar();
		mensajes.add("\nHSBC");
		psHSBC = administradorArchivosDao.consultarConfiguraSet(201)+ "\\HSBC\\";
		if (!psHSBC.equals(rutaActual))
			System.setProperty("user.dir", psHSBC);
		nombres = busquedaArchivos.obtenerNombreArchivos(".txt");
		if (administradorArchivosDao.seleccionarSaldoBanco(21).get(0).getDescripcion().equals("C"))
			bActualizarSaldo = true;
		else
			bActualizarSaldo = false;
		try {
			nombres = busquedaArchivos.cambiarExtension(nombres, ".txt", ".tmp");
			while (z < nombres.length) {
				archivo = new Scanner(nombres[z]);
				psArchivo = nombres[z].getName().replace(".tmp", ".txt");
				if (psArchivos.equals(""))
					psArchivos = psArchivo;
				else
					psArchivos += "', '" + psArchivo;
				renglon = 0;
				i = 0;
				iContador = 0;
				renglon = 0;
				archivoOk = true;
				do {
					if (control)
						control = false;
					while (archivo.hasNext()) {
						i++;
						iContador++;
						pbHSBCMismoDia = false;
						psRegistro = archivo.nextLine();
						renglon++;
						if (psRegistro.length() == 101)
							pbHSBCMismoDia = true;
						else if (psRegistro.length() != 125) {
							if (!psRegistro.equals("")) {
								psMensaje = "HSBC: La longitud del registro no corresponde al estado de cuenta, archivo: " + psArchivo;
								mensajesUsuario.add(psMensaje);
								archivoOk = false;
								break;
							}
						}
						psRegistro = psRegistro.trim().replace("'", " ");
						psHora = psRegistro.substring(18, 24);
						psFecha = psRegistro.substring(16, 18) + "/" + psRegistro.substring(14, 16) + "/" + psRegistro.substring(10, 14) + " " + psHora.substring(0, 2) + ":" + psHora.substring(2, 4) + ":" + psHora.substring(4, 6);
						if (!funciones.isDate(psFecha, true)) {
							break;
						} else {
							// solo los abonos y la referencia en la primera
							// posicion tenga un 9
							if (psRegistro.trim().substring(42, 44).equals("CR"))
								psSBCobro = "S";
							else
								psSBCobro = "";
							i = 0;
							// asigna chequera
							if (!psChequera.equals(funciones.ponerCeros(psRegistro.substring(0, 10), 11))){
								psChequera = funciones.ponerCeros(psRegistro.substring(0, 10), 11);
								bEmpresa = new ParametroBuscarEmpresaDto();
								bEmpresa.setPiBanco(HSBC);
								bEmpresa.setPsChequera(psChequera);
								List<CatCtaBancoDto> empresa = administradorArchivosDao.buscarEmpresa(bEmpresa);
								if (empresa.size() == 0) {
									bExisteChequera = false;
									if (psChequerasInexistentes.equals(""))
										psChequerasInexistentes = psChequerasInexistentes + psChequera;
									else
										psChequerasInexistentes = psChequerasInexistentes + "\n" + psChequera;
									control = true;
									break;
								} 
								else {
									bExisteChequera = true;
									piNoEmpresa = empresa.get(0).getNoEmpresa();
									// aqui metemos la validacion de la empresa
									// por usuario
									// solo para CIE
									if (administradorArchivosDao.consultarConfiguraSet(1).equals("CIE")) {
										bEmpresa = null;
										bEmpresa = new ParametroBuscarEmpresaDto();
										bEmpresa.setIdUsuario(idUsuario);
										bEmpresa.setNoEmpresa(piNoEmpresa);
										if (administradorArchivosDao.buscarEmpresaUsu(bEmpresa) <= 0) {
											if (piNoEmpresa != piEmpresaAnt) {
												piEmpresaAnt = piNoEmpresa;
												psMensaje = "HSBC todas las columnas todas las chequeras; La empresa "
														+ piNoEmpresa
														+ " no esta asignada al usuario, archivo: "
														+ psArchivo;
												mensajesUsuario.add(psMensaje);
											}
											bEmpresaUsuario = false;
											control = true;
											break;
										}
										bEmpresaUsuario = true;
									}
								}
							} else if (!bExisteChequera) {
								plRegSinChequera++;
								control = true;
								break;
							} else if (!bEmpresaUsuario) {
								control = true;
								break;
							}
							if (Integer.parseInt(psRegistro.substring(45, 49)) == 5503) { // And (Mid(ps_registro,
								psConcepto = "550301";
							} else {
								psConcepto = psRegistro.substring(45, 49).trim();
							}
							if (pbHSBCMismoDia)
								plFolioBanco = Long.parseLong(psRegistro.substring(49, 59).equals("") ? "0" : psRegistro.substring(49, 59));
							else
								plFolioBanco = Long.parseLong(psRegistro.substring(49, 60).equals("") ? "0" : psRegistro.substring(49, 60));
							psReferencia = "";
							pdImporte = Double.parseDouble(psRegistro.substring(29, 42)) / 100;
							sCargoAbono = psRegistro.substring(42, 44).equals("CR") ? "I" : "E";
							if (pbHSBCMismoDia) {
								sDescripcion = psRegistro.substring(70, 86).trim();
								sSucursal = "";
								if (bActualizarSaldo)
									dSaldoBanco = Double.parseDouble(psRegistro.substring(85, 99)) / 100;
								else
									dSaldoBanco = 0;
							} else {
								sDescripcion = psRegistro.substring(70, 110).trim();
								// sSucursal = "";
								if (bActualizarSaldo)
									dSaldoBanco = Double.parseDouble(psRegistro.substring(110, 123)) / 100;
								else
									dSaldoBanco = 0;
							}
							parametro = new ParametroLayoutDto();
							parametro.setPlNoEmpresa(piNoEmpresa);
							parametro.setPsFolioBanco("" + plFolioBanco);
							parametro.setPsConcepto(psConcepto);
							parametro.setPsReferencia(psReferencia);
							parametro.setPsIdChequera(psChequera);
							parametro.setPsFecValor(psFecha);
							parametro.setPdImporte(pdImporte);
							parametro.setPdSaldo(dSaldoBanco);
							List<MovtoBancaEDto> datos = administradorArchivosDao.selecionarMovtoBancaE(parametro);
							if (datos.size() <= 0) {
								// 'sacamos el siguiente folio de la base de
								// datos
								plFolio = seleccionarFolioReal("secuencia");
								if (plFolio <= 0) {
									for (int h = z; h < nombres.length; h++)
										busquedaArchivos.cambiarExtensionSoloUno(nombres[h], ".tmp",	".txt");
									return false;
								}
								i = 0;
								psCveConcepto = psConcepto;
								insercion = new ParametroLayoutDto();
								insercion.setPlNoEmpresa(piNoEmpresa);
								insercion.setPsIdChequera(psChequera);
								insercion.setPsSecuencia(plFolio);
								insercion.setPsFecValor(psFecha);
								insercion.setPsFolioBanco("" + plFolioBanco);
								insercion.setPsReferencia(psReferencia);
								insercion.setPsBSalvoBuenCobro(psSBCobro);
								insercion.setPsConcepto(psConcepto);
								insercion.setPsFecAlta(funciones.ponerFecha(new Date()));
								insercion.setPsCargoAbono(sCargoAbono);
								insercion.setPsUsuarioAlta(idUsuario); 
								insercion.setPsDescripcion(sDescripcion);
								insercion.setPsArchivo(psArchivo);
								insercion.setPsSucursal(sSucursal);
								insercion.setPdImporte(pdImporte);
								insercion.setPdSaldoBancario(dSaldoBanco);
								insercion.setPsCveConcepto(psCveConcepto);
								insercion.setContadorRenglon(renglon);
								if(administradorArchivosDao.insertarMovtoBancaEHSBC(insercion)>0)
									dto.setPlRegImportados((dto.getPlRegImportados() + 1));
								insercion = null;
							} else
								piEncontrados++;
						}
					}
				} while (control);
				archivo.close();
				archivo = null;
				if (archivoOk) {
					plRegLeidos += renglon;
					busquedaArchivos.cambiarExtensionSoloUno(nombres[z], ".tmp", ".old");
				} else
					busquedaArchivos.cambiarExtensionSoloUno(nombres[z], ".tmp", ".err");
				z++;
			}
		} catch (FileNotFoundException e) {
			bitacora.insertarRegistro(new Date().toString()	+ " "
							+ e.toString()
							+ "Archivo: "
							+ psArchivo
							+ " P:BE, C:AdministradorArchivosBussines, M:leerHSBCVersionOficial");
		}
		dto.setPsArchivos(psArchivos);
		mensajes.add("Registros Leidos: " + plRegLeidos);
		mensajes.add("Registros Sin Chequera: " + plRegSinChequera);
		mensajes.add(psChequerasInexistentes);
		mensajes.add("Importados: " + dto.getPlRegImportados()+"\n");
		System.setProperty("user.dir", rutaActual);
		return true;
	}

	/**
	 * 
	 * @param dto
	 * @return boolean
	 * 
	 * Lee el layout de bital de todas la columnas
	 * 
	 * Private Function lee_bital_todas_columnas(ByRef psArchivos As String, _
	 * ByRef ps_chequeras_inexistentes As String, _ ByRef plReg_leidos As Long,
	 * ByRef plReg_sin_chequera As Long, _ ByRef plReg_importados As Long) As
	 * Boolean
	 */
	public boolean leerHSBCTodasColumnas(ParametroLayoutDto dto) {
		// boolean pbMismoBanco = false;
		psChequera = "";
		psArchivos = "";
		psChequerasInexistentes = "";
		dto.setPsArchivos("");
		inicializar();
		mensajes.add("\nHSBC Todas las Columnas");
		psHSBC = administradorArchivosDao.consultarConfiguraSet(201) + "\\HSBC\\todas_cols\\";
		if (!psHSBC.equals(rutaActual))
			System.setProperty("user.dir", psHSBC);
		bExisteChequera = false;
		nombres = busquedaArchivos.obtenerNombreArchivos(".txt");
		if (administradorArchivosDao.seleccionarSaldoBanco(21).get(0).getDescripcion().equals("C"))
			bActualizarSaldo = true;
		else
			bActualizarSaldo = false;
		try {
			nombres = busquedaArchivos.cambiarExtension(nombres, ".txt", ".tmp");
			while (z < nombres.length) {
				archivo = new Scanner(nombres[z]);
				psArchivo = nombres[z].getName().replace(".tmp", ".txt");
				if (psArchivos.equals(""))
					psArchivos = psArchivo;
				else
					psArchivos += "', '" + psArchivo;
				i = 0;
				iContador = 0;
				renglon = 0;
				archivoOk = true;
				do {
					if (control)
						control = false;
					while (archivo.hasNext()) {
						i++;
						iContador++;
						psRegistro = archivo.nextLine();
						renglon++;
						if (psRegistro.length() != 195) {
							if (!psRegistro.equals("")) {
								psMensaje = "HSBC Todas Columnas: La longitud del registro no corresponde al estado de cuenta, archivo: "
										+ psArchivo;
								mensajesUsuario.add(psMensaje);
								archivoOk = false;
								break;
							}
						}
						psRegistro = psRegistro.replace("'", " ");
						psFecha = psRegistro.substring(16, 18) + "/" + psRegistro.substring(14, 16) + "/" + psRegistro.substring(10, 14);
						if (!funciones.isDate(psFecha, false)) {
							break;
						} 
						else {
							psSBCobro = "";
							i = 0;
							// 'asigna chequera
							if (!psChequera.equals(funciones.ponerCeros(psRegistro.substring(0, 10), 11))) {
								psChequera = funciones.ponerCeros(psRegistro.substring(0, 10), 11);
								bEmpresa = new ParametroBuscarEmpresaDto();
								bEmpresa.setPsChequera(psChequera);
								bEmpresa.setPiBanco(HSBC);
								List<CatCtaBancoDto> empresa = administradorArchivosDao.buscarEmpresa(bEmpresa);
								if (empresa.size() == 0) {
									bExisteChequera = false;
									plRegSinChequera++;
									if (psChequerasInexistentes.equals(""))
										psChequerasInexistentes = psChequera;
									else
										psChequerasInexistentes = psChequerasInexistentes + "\n" + psChequera;
									control = true;
									break;
								} 
								else {
									bExisteChequera = true;
									piNoEmpresa = empresa.get(0).getNoEmpresa();
									// 'aqui metemos la validacion de la empresa
									// por usuario solo para CIE
									if (administradorArchivosDao.consultarConfiguraSet(1).equals("CIE")) {
										bEmpresa = null;
										bEmpresa = new ParametroBuscarEmpresaDto();
										bEmpresa.setIdUsuario(idUsuario);
										bEmpresa.setNoEmpresa(piNoEmpresa);
										if (administradorArchivosDao.buscarEmpresaUsu(bEmpresa) <= 0) {
											if (piNoEmpresa != piEmpresaAnt) {
												piEmpresaAnt = piNoEmpresa;
												psMensaje = "HSBC todas las columnas todas las chequeras; La empresa "
														+ piNoEmpresa
														+ " no esta asignada al usuario, archivo: "
														+ psArchivo
														+ "Renglon: "
														+ (renglon + 1);
												mensajesUsuario.add(psMensaje);
											}
											bEmpresaUsuario = false;
											control = true;
											break;
										}
										bEmpresaUsuario = true;
									}
								}
							} else if (!bExisteChequera) {
								plRegSinChequera++;
								control = true;
								break;
							} else if (!bEmpresaUsuario) {
								control = true;
								break;
							}
						}
						if (Integer.parseInt(psRegistro.substring(99, 104)) == 5503)
							psConcepto = "550301";
						else
							psConcepto = psRegistro.substring(100, 104).trim();
						// checa si ya existe el movimiento
						plFolioBanco = Long.parseLong(psRegistro.substring(18,29).equals("") ? "0" : psRegistro.substring(18,29));
						pdImporte = Double.parseDouble(psRegistro.substring(69, 82)) / 100;
						sCargoAbono = psRegistro.substring(82, 84).equals("CR") ? "I" : "E";
						sDescripcion = psRegistro.substring(29, 69).trim();
						/*
						 * if(sDescripcion.indexOf("DESDE") > 0) pbMismoBanco =
						 * true; else pbMismoBanco = false;
						 */
						if (sCargoAbono.equals("E")) {
							psReferencia = psRegistro.substring(125, 165);
							piPosicion = psReferencia.indexOf("FOLIO  NO.");
							if (piPosicion > -1)
								psReferencia = psReferencia.substring(piPosicion + 11);
						} 
						else {
							psReferencia = psRegistro.substring(125, 165);
							if (lbReferenciaPorChequera)
								psReferencia = psReferencia.trim();
							else {
								piPosicion = psReferencia.indexOf("FOLIO  NO.");
								if (piPosicion > -1)
									psReferencia = psReferencia.substring(piPosicion + 11);
								psReferencia = psReferencia.substring(0, liLongitudReferencia); // 'Trim(Mid(ps_registro, 126, liLongitud_referencia))
							}
						}
						sSucursal = psRegistro.substring(165, 170).trim();
						if (bActualizarSaldo)
							dSaldoBanco = Double.parseDouble(psRegistro.substring(84, 97)) / 100;
						else
							dSaldoBanco = 0;
						parametro = new ParametroLayoutDto();
						parametro.setPsFolioBanco("" + plFolioBanco);
						parametro.setPsConcepto(psConcepto);
						parametro.setPsReferencia(psReferencia);
						parametro.setPlNoEmpresa(piNoEmpresa);
						parametro.setPsIdChequera(psChequera);
						parametro.setPsFecValor(psFecha);
						parametro.setPdImporte(pdImporte);
						parametro.setPdSaldo(dSaldoBanco);
						parametro.setContadorRenglon(renglon);
						List<MovtoBancaEDto> movto = administradorArchivosDao.selecionarMovtoBancaE(parametro);
						if (movto.size() <= 0) {
							// sacamos el siguiente folio de la base de datos
							plFolio = seleccionarFolioReal("secuencia");
							if (plFolio <= 0)  {
								for (int h = z; h < nombres.length; h++)
									busquedaArchivos.cambiarExtensionSoloUno(nombres[h], ".tmp",	".txt");
								return false;
							}
							i = 0;
							psCveConcepto = psConcepto;
							insercion = new ParametroLayoutDto();
							insercion.setPlNoEmpresa(piNoEmpresa);
							insercion.setPsIdChequera(psChequera);
							insercion.setPsSecuencia(plFolio);
							insercion.setPsFecValor(psFecha);
							insercion.setPsFolioBanco("" + plFolioBanco);
							insercion.setPsReferencia(psReferencia);
							insercion.setPsBSalvoBuenCobro(psSBCobro);
							insercion.setPsConcepto(psConcepto);
							insercion.setPsFecAlta(funciones.ponerFechaSola(obtenerFechaHoy()));
							insercion.setPsCargoAbono(sCargoAbono);
							insercion.setPsUsuarioAlta(idUsuario);
							insercion.setPsDescripcion(sDescripcion);
							insercion.setPsArchivo(psArchivo);
							insercion.setPsSucursal(sSucursal);
							insercion.setPdImporte(pdImporte);
							insercion.setPdSaldoBancario(dSaldoBanco);
							insercion.setPsCveConcepto(psCveConcepto);
							insercion.setContadorRenglon(renglon);
							if(administradorArchivosDao.insertarMovtoBancaEHSBC(insercion)>0)
								dto.setPlRegImportados((dto.getPlRegImportados() + 1));
							insercion = null;
						} else
							piEncontrados++;
					}
				} while (control);
				archivo.close();
				archivo = null;
				if (archivoOk) {
					dto.setPlRegLeidos(dto.getPlRegLeidos() + renglon);
					busquedaArchivos.cambiarExtensionSoloUno(nombres[z], ".tmp", ".old");
				} else
					busquedaArchivos.cambiarExtensionSoloUno(nombres[z], ".tmp", ".err");
				z++;
			}
		} catch (FileNotFoundException e) {
			bitacora.insertarRegistro(new Date().toString()
							+ " "
							+ e.toString()
							+ "Archivo: "
							+ psArchivo
							+ " P:BE, C:AdministradorArchivosBussines, M:leerHSBCTodasColumnas");
		}
		dto.setPsArchivos(psArchivos);
		mensajes.add("Registros Leidos: " + dto.getPlRegLeidos());
		mensajes.add("Registros Sin Chequera: " + plRegSinChequera);
		mensajes.add(psChequerasInexistentes);
		mensajes.add("Importados: " + dto.getPlRegImportados()+"\n");
		System.setProperty("user.dir", rutaActual);
		return true;
	}

	/*
	 * Private Function lee_bital_todas_columnas_TODAS_CHEQUERAS(ByRef
	 * psArchivos As String, _ ByRef ps_chequeras_inexistentes As String, _
	 * ByRef plReg_leidos As Long, ByRef plReg_sin_chequera As Long, _ ByRef
	 * plReg_importados As Long) As Boolean
	 * 
	 */
	public boolean leerHSBCTodasColumnasTodasChequeras(ParametroLayoutDto dto) {
		// boolean pbMismoBanco=false;
		// Layout de HSBC version 4. de Todas las columnas con todas las
		// chequeras con referencia
		piEmpresaAnt = 0;
		dto.setPsArchivos("");
		dto.setPlRegSinChequera(0);
		dto.setPsChequerasInexistentes("");
		inicializar();
		rutaActual = System.getProperty("user.dir");
		psHSBC = administradorArchivosDao.consultarConfiguraSet(201) + "\\HSBC\\";
		if (!psHSBC.equals(rutaActual))
			System.setProperty("user.dir", psHSBC);
		// NOTA: ARCHIVO DE CONSULTA DE MOVIMIENTOS (Estado de cuenta con TODAS las columnas)
		nombres = busquedaArchivos.obtenerNombreArchivos(".txt");
		if (administradorArchivosDao.seleccionarSaldoBanco(21).get(0).getDescripcion().equals("C"))
			bActualizarSaldo = true;
		else
			bActualizarSaldo = false;
		try {
			mensajes.add("\nHSBC Todas Chequeras");
			if (nombres.length > 0) {
				nombres = busquedaArchivos.cambiarExtension(nombres, ".txt", ".tmp");
				while (z < nombres.length) {
					archivo = new Scanner(nombres[z]);
					psArchivo = nombres[z].getName().replace(".tmp", ".txt");
					if (psArchivos.equals(""))
						psArchivos = psArchivo;
					else
						psArchivos += "', '" + psArchivo;
					i = 0;
					renglon = 0;
					archivoOk = true;
					do {
						if (control)
							control = false;
						while (archivo.hasNext()) {
							psRegistro = archivo.nextLine();
							renglon++;
							i++;
							iContador++;
							if (psRegistro.length() != 170) {
								if (!psRegistro.equals("")) {
									psMensaje = "HSBC Todas Chequeras Todas Columnas: La longitud del registro no corresponde al estado de cuenta, archivo: "
											+ psArchivo;
									mensajesUsuario.add(psMensaje);
									archivoOk = false;
									break;
								}
							}
							psRegistro = psRegistro.replace("'", " ");
							psHora = psRegistro.substring(18, 20) + ":"	+ psRegistro.substring(20, 22) + ":" + psRegistro.substring(22, 24);
							psFecha = psRegistro.substring(16, 18) + "/" + psRegistro.substring(14, 16) + "/" + psRegistro.substring(10, 14);
							if (!funciones.isDate(psFecha, false)) {
								break;
							}
							else {
								psFecha = psFecha + " " + psHora;
								psSBCobro = "";
								i = 0;
								// asigna chequera
								psChequeraNueva = funciones.ponerCeros(psRegistro.substring(0, 10), 11);
								if (!psChequera.equals(psChequeraNueva)) {
									psChequera = psChequeraNueva;
									bEmpresa = new ParametroBuscarEmpresaDto();
									bEmpresa.setPsChequera(psChequera);
									bEmpresa.setPiBanco(HSBC);
									List<CatCtaBancoDto> empresa = administradorArchivosDao.buscarEmpresa(bEmpresa);
									if (empresa.size() == 0) {
										bExisteChequera = false;
										dto.setPlRegSinChequera(dto.getPlRegSinChequera() + 1);
										if (dto.getPsChequerasInexistentes().equals(""))
											dto.setPsChequerasInexistentes(psChequera);
										else
											dto.setPsChequerasInexistentes(dto.getPsChequerasInexistentes() + "\n" + psChequera);
										control = true;
										break;
									} 
									else {
										bExisteChequera = true;
										piNoEmpresa = empresa.get(0).getNoEmpresa();
										// aqui metemos la validacion de la empresa por usuario solo para CIE
										if (administradorArchivosDao.consultarConfiguraSet(1).equals("CIE")) {
											bEmpresa = null;
											bEmpresa = new ParametroBuscarEmpresaDto();
											bEmpresa.setIdUsuario(idUsuario);
											bEmpresa.setNoEmpresa(piNoEmpresa);
											if (administradorArchivosDao.buscarEmpresaUsu(bEmpresa) <= 0) {
												if (piNoEmpresa != piEmpresaAnt) {
													piEmpresaAnt = piNoEmpresa;
													psMensaje = "La empresa "
															+ piNoEmpresa
															+ " no esta asignada al usuario, archivo: "
															+ psArchivo;
													mensajesUsuario.add(psMensaje);
												}
												bEmpresaUsuario = false;
												control = true;
												break;
											}
											bEmpresaUsuario = true;
										}
									}
								} else if (!bExisteChequera) {
									dto.setPlRegSinChequera(dto.getPlRegSinChequera() + 1);
									control = true;
									break;
								} else if (!bEmpresaUsuario) {
									control = true;
									break;
								}
								psConcepto = psRegistro.substring(45, 49).trim();
								// checa si ya existe el movimiento
								plFolioBanco = Long.parseLong(psRegistro.substring(60, 65));
								pdImporte = Double.parseDouble(psRegistro.substring(29, 42)) / 100;
								sCargoAbono = psRegistro.substring(42, 44).equals("CR") ? "I" : "E";
								sDescripcion = psRegistro.substring(70, 110).trim();
								if (sCargoAbono.equals("E")) {
									psReferencia = psRegistro.substring(125,165);
									piPosicion = psReferencia.indexOf("FOLIO  NO.");
									if (piPosicion > -1)
										psReferencia = psReferencia.substring(piPosicion + 11);
								} 
								else {
									psReferencia = psRegistro.substring(125,165);
									if (lbReferenciaPorChequera)
										psReferencia = psReferencia.trim();
									else {
										piPosicion = psReferencia.indexOf("FOLIO  NO.");
										if (piPosicion > -1)
											psReferencia = psReferencia.substring(piPosicion + 11);
										psReferencia = psReferencia.substring(0, liLongitudReferencia).trim(); // 'Trim(Mid(ps_registro,126,iLongitud_referencia))
									}
								}
								if (psReferencia.length() > 30)
									psReferencia = psReferencia.substring(0, 30);
								sSucursal = psRegistro.substring(24, 29);
								if (bActualizarSaldo) {
									if (psRegistro.substring(110, 123).indexOf("-") != -1)
										dSaldoBanco = Double.parseDouble(psRegistro.substring(110 + (psRegistro.substring(110, 123).indexOf("-") + 1), 120 + (psRegistro.substring(110, 123).indexOf("-") + 1))) / 100; // 'cambio
									else
										dSaldoBanco = Double.parseDouble(psRegistro.substring(110, 123)) / 100;
									if (psRegistro.substring(123, 125).equals("DR")) // 'DR En Contra, CR A favor
										dSaldoBanco = dSaldoBanco * -1;
								} else
									dSaldoBanco = 0;
								parametro = new ParametroLayoutDto();
								parametro.setPsFolioBanco("" + plFolioBanco);
								parametro.setPsConcepto(psConcepto);
								parametro.setPsReferencia(psReferencia);
								parametro.setPlNoEmpresa(piNoEmpresa);
								parametro.setPsIdChequera(psChequera);
								parametro.setPsFecValor(psFecha);
								parametro.setPdImporte(pdImporte);
								parametro.setPdSaldo(dSaldoBanco);
								parametro.setContadorRenglon(renglon);
								List<MovtoBancaEDto> movto = administradorArchivosDao
										.selecionarMovtoBancaE(parametro);
								if (movto.size() <= 0) {
									// 'sacamos el siguiente folio de la base de
									// datos
									plFolio = seleccionarFolioReal("secuencia");
									if (plFolio <= 0) {
										for (int h = z; h < nombres.length; h++)
											busquedaArchivos.cambiarExtensionSoloUno(nombres[h], ".tmp",	".txt");
										return false;
									}
									i = 0;
									psCveConcepto = psConcepto;
									insercion = new ParametroLayoutDto();
									insercion.setPlNoEmpresa(piNoEmpresa);
									insercion.setPsIdChequera(psChequera);
									insercion.setPsSecuencia(plFolio);
									insercion.setPsFecValor(psFecha);
									insercion.setPsFolioBanco("" + plFolioBanco);
									insercion.setPsReferencia(psReferencia);
									insercion.setPsBSalvoBuenCobro(psSBCobro);
									insercion.setPsConcepto(psConcepto);
									insercion.setPsFecAlta(funciones.ponerFechaSola(new Date()));
									insercion.setPsCargoAbono(sCargoAbono);
									insercion.setPsUsuarioAlta(idUsuario);
									insercion.setPsDescripcion(sDescripcion);
									insercion.setPsArchivo(psArchivo);
									insercion.setPsSucursal(sSucursal);
									insercion.setPdImporte(pdImporte);
									insercion.setPdSaldoBancario(dSaldoBanco);
									insercion.setPsCveConcepto(psCveConcepto);
									insercion.setContadorRenglon(renglon);
									if(administradorArchivosDao.insertarMovtoBancaEHSBC(insercion)>0)
										dto.setPlRegImportados((dto.getPlRegImportados() + 1));
									insercion = null;
								} 
								else
									piEncontrados++;
							}
						}
					} while (control);
					archivo.close();
					archivo = null;
					if (archivoOk) {
						dto.setPlRegLeidos(dto.getPlRegLeidos() + renglon);
						busquedaArchivos.cambiarExtensionSoloUno(nombres[z], ".tmp", ".old");
					} 
					else
						busquedaArchivos.cambiarExtensionSoloUno(nombres[z], ".tmp", ".err");
					z++;
				}
			}
		} catch (FileNotFoundException e) {
			bitacora.insertarRegistro(new Date().toString() 
							+ " "
							+ e.toString()
							+ "Archivo: "
							+ nombres[z].getAbsolutePath()
							+ " P:BE, C:AdministradorArchivosBussines, M:leerHSBCTodasColumnasTodasChequeras");
		}
		dto.setPsArchivos(psArchivos);
		mensajes.add("Registros Leidos: " + dto.getPlRegLeidos());
		mensajes.add("Registros Sin Chequera: " + dto.getPlRegSinChequera());
		mensajes.add(dto.getPsChequerasInexistentes());
		mensajes.add("Importados: " + dto.getPlRegImportados()+"\n");
		System.setProperty("user.dir", rutaActual);
		return true;
	}
	
	public boolean leerHSBCGNP(ParametroLayoutDto dto){
		long plNoCheque;
		int piRegImportados = 0;
		StringBuffer psInserts;
		inicializar();
		psHSBC = administradorArchivosDao.consultarConfiguraSet(201) + "\\bital\\";
		
		if (!psHSBC.equals(rutaActual))
			System.setProperty("user.dir", psHSBC);
		nombres = busquedaArchivos.obtenerNombreArchivos(".txt");
		
		try {
			mensajes.add("HSBC");
			
			if(nombres == null) return false;
			nombres = busquedaArchivos.cambiarExtension(nombres, ".txt", ".tmp");
			
			while(z < nombres.length) {
				psInserts = new StringBuffer();
				psArchivo = nombres[z].getName().replace(".tmp", ".txt");
				
				if (psArchivos.equals(""))
					psArchivos = psArchivo;
				else
					psArchivos += "', '" + psArchivo;
				archivo = new Scanner(nombres[z]);
				renglon = 0;
				archivoOk = true;
				psChequerasInexistentes = "";
				
				while(archivo.hasNext()) {
					psRegistro = archivo.nextLine();
					
					if(psRegistro.length() != 210) {
						archivoOk = false;
						break;
					}
					psRegistro = psRegistro.replace("'", " ");
					
	                while(true) {
	                	psChequeraNueva = psRegistro.substring(7, 18);									//Hay que leer la nueva chequera
	                	renglon++;
	                	plRegLeidos++;
	                	
        				if(!psChequera.equals(psChequeraNueva)) {
                        	psChequera = psChequeraNueva;
                        	//numChequeras++;
                        	ParametroBancomerDto bEmpresa = new ParametroBancomerDto();
                    		bEmpresa.setIdBanco(HSBC);
                    		bEmpresa.setIdChequera(psChequera);
                    		List<ComunDto> empresas = administradorArchivosDao.buscarEmpresa(bEmpresa);
                    		
                    		if(empresas.size() > 0) {
                    			piNoEmpresa = empresas.get(0).getIdEmpresa();
                    			bExisteChequera = true;
	                    		// Para el procesos de GNP se coloca la condicion de que si la variable bExporta == "N" o bExporta == "", 
                    			// no se carguen los registros de esa chequera
                    			if(empresas.get(0).getBExporta().trim().equals("N") || empresas.get(0).getBExporta().trim().equals(""))
                    				bExisteChequera = false;
                    			//else
                    				//numCheqProce++;
                    		}else {
                    			if (psChequerasInexistentes.equals(""))
                    				psChequerasInexistentes = psChequera;
                    			else
                    				psChequerasInexistentes += ", " + psChequeraNueva;
                    			bExisteChequera = false;
                            }
                		}
                    	if(!bExisteChequera) {
            				plRegSinChequera++;
                			
                            if(archivo.hasNext()) {
                            	psRegistro = archivo.nextLine();
                            	psRegistro = psRegistro.replace("'", " ");
                                continue;
                            }
                            archivoOk = true;
            			}
                    	break;
                	}
	                psFecha = psRegistro.substring(18, 28);
	                
	                if(!funciones.isDate(psFecha, false)) {
	                    archivoOk = false;
	                    break;
	                }
	                psSBCobro = psRegistro.substring(118, 120).equals("CR") ? "S" : "";
	                plNoCheque = Long.parseLong(psRegistro.substring(28, 39));
	                sDescripcion = psRegistro.substring(39, 79).trim();
	                psConcepto = sDescripcion;
	                psCargoAbono = psRegistro.substring(118, 120).equals("CR") ? "I" : "E";
	                
	                if(psCargoAbono.equals("I"))
	                	pdImporte = (Double.parseDouble(psRegistro.substring(92, 105)) / 100);
	                else
	                	pdImporte = (Double.parseDouble(psRegistro.substring(79, 92)) / 100);
	                dSaldoBanco = (Double.parseDouble(psRegistro.substring(105, 118)) / 100);
	                psCveConcepto = psRegistro.substring(120, 125);
	                plFolioBanco = Long.parseLong(psRegistro.substring(125, 135));
	                psReferencia = plNoCheque > 0 ? "" + plNoCheque : psRegistro.substring(140, 180);
	                sSucursal = psRegistro.substring(180, 185);
	                
	                /*
	                	chequera, fecha, cheque, descripcion 40, cargo 13, abono 13, saldo 13, sirno 2, calve 5, folio 10, operador 5,
	                	referencia 40, sucursal 5, banco origen 3, causa devolucion 2, operacion 20
	                	*/
	                
                    parametro = new ParametroLayoutDto();
					parametro.setPsFolioBanco("" + plFolioBanco);
					parametro.setPsConcepto(psConcepto);
					parametro.setPsReferencia(psReferencia);
					parametro.setPlNoEmpresa(piNoEmpresa);
					parametro.setPsIdChequera(psChequera);
					parametro.setPsFecValor(psFecha);
					parametro.setPdImporte(pdImporte);
					parametro.setPdSaldo(dSaldoBanco);
					parametro.setContadorRenglon(renglon);
					List<MovtoBancaEDto> movto = administradorArchivosDao.selecionarMovtoBancaE(parametro);
					
					if(movto.size() <= 0) {
                        plFolio = seleccionarFolioReal("secuencia");
                        
                        if(plFolio <= 0) {
                        	archivoOk = false;
                        	return false;
						}
                        insercion = new ParametroLayoutDto();
						insercion.setPlNoEmpresa(piNoEmpresa);
						insercion.setPiBanco(HSBC);
						insercion.setPsIdChequera(psChequera);
						insercion.setPsSecuencia(plFolio);
						insercion.setPsFecValor(psFecha);
						insercion.setPsSucursal(sSucursal);
						insercion.setPsFolioBanco("" + plFolioBanco);
						insercion.setPsReferencia(psReferencia);
						insercion.setPsCargoAbono(psCargoAbono);
						insercion.setPdImporte(pdImporte);
						insercion.setPsBSalvoBuenCobro(psSBCobro);
						insercion.setPsConcepto(psConcepto);
						insercion.setPsUsuarioAlta(idUsuario);
						insercion.setPsDescripcion(sDescripcion);
						insercion.setPsArchivo(psArchivo);
						insercion.setPsFecAlta(funciones.ponerFechaSola(obtenerFechaHoy()));
						insercion.setPsObservacion("");
						insercion.setPsCveConcepto(psCveConcepto);
						insercion.setPdSaldoBancario(dSaldoBanco);
						insercion.setPlNoLineaArchivo(renglon);
						psInserts.append(administradorArchivosDao.insertarMovtoBancaE(insercion)); 
		                piRegImportados++;
					}else piEncontrados++;
				}
				archivo.close();
				archivo = null;
				
				if(archivoOk) {
					if(!psInserts.toString().equals("")) administradorArchivosDao.ejecutaSentencia(psInserts.toString());
	                dto.setPsArchivos(psArchivos);
					dto.setPlRegLeidos(plRegLeidos);
					dto.setPlRegSinChequera(plRegSinChequera);
					dto.setPsChequerasInexistentes(psChequerasInexistentes);
					dto.setPlRegImportados(piRegImportados);
					busquedaArchivos.cambiarExtensionSoloUno(nombres[z], ".tmp", ".old");
				}else {
					busquedaArchivos.cambiarExtensionSoloUno(nombres[z], ".tmp", ".err");
				}
				z++;
			}
		}catch(FileNotFoundException e){
	    	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "Archivo: " + nombres[z].getAbsolutePath()
					+ " P:BE, C:AdministradorArchivosBussines, M:leerHSBCGNP");
	    	return false;
	    }
		mensajes.add("Registros Leidos: " + dto.getPlRegLeidos());
		mensajes.add("Registros Sin Chequera: " + dto.getPlRegSinChequera());
		mensajes.add(dto.getPsChequerasInexistentes());
		mensajes.add("Importados: " + dto.getPlRegImportados());
		System.setProperty("user.dir", rutaActual);
		return true;
	}
	
	//Es la misma lectura para el H2H de Santander
	public boolean leerSantanderTodasChequeras(ParametroLayoutDto dto) {
		inicializar();
		psSantander = administradorArchivosDao.consultarConfiguraSet(201) + "santander\\todas_chequeras\\";
		
		if (!psSantander.equals(rutaActual))
			System.setProperty("user.dir", rutaActual + psSantander);
			
		
		nombres = busquedaArchivos.obtenerNombreArchivos(".edocta");
		mensajes.add("\nSantader Todas Chequeras");
		
		try {
			if (nombres != null) {
				while (z < nombres.length) {
					nombres = busquedaArchivos.cambiarExtension(nombres, ".edocta", ".tmp");
					psArchivo = nombres[z].getName().replace(".tmp", ".edocta");
					
					if (psArchivos.equals(""))
						psArchivos = psArchivo;
					else
						psArchivos += "', '" + psArchivo;
					archivo = new Scanner(nombres[z]);
					renglon = 0;
					archivoOk = true;
					
					do {
						if (control) control = false;
						
						while (archivo.hasNext()) {
							i = 0;
							iContador ++;
							renglon++;
							psRegistro = archivo.nextLine();
							psRegistro = psRegistro.replace("'", " ");
							// 'LOngitud 8 posiciones de la 17 a la 24 esta en formato MMDDAAAA
							psFecha = psRegistro.substring(18, 20) + "/" + psRegistro.substring(16, 18) + "/" + psRegistro.substring(20, 24);
							
							if (!funciones.isDate(psFecha, false))
								break;
							else {
								// checa si es necesario ir por la empresa Chequera longitud:16 posiciones de la 1 a la 16
								psChequeraNueva = psRegistro.substring(0, 11);
								if (!psChequera.equals(psChequeraNueva)) {
									psChequera = psChequeraNueva;
									bEmpresa = new ParametroBuscarEmpresaDto();
									bEmpresa.setPsChequera(psChequera);
									bEmpresa.setPiBanco(SANTANDER);
									List<CatCtaBancoDto> empresa = administradorArchivosDao.buscarEmpresa(bEmpresa);
									
									if (empresa.size() > 0) {
										piNoEmpresa = empresa.get(0).getNoEmpresa();
										bExisteChequera = true;
									} else {
										bExisteChequera = false;
										dto.setPlRegSinChequera(dto.getPlRegSinChequera() + 1);
										if (dto.getPsChequerasInexistentes().equals(""))
											dto.setPsChequerasInexistentes(psChequera);
										else
											dto.setPsChequerasInexistentes(dto.getPsChequerasInexistentes()	+ ", " + psChequera);
										control = true;
										break;
									}
								} 
								else {
									if (!bExisteChequera) {
										dto.setPlRegSinChequera(dto.getPlRegSinChequera() + 1);
										control = true;
										break;
									}
								}
								// 'SIGNO: longitud 1 posicion 77
								if (psRegistro.substring(76, 77).equals("+")) {
									if (psRegistro.substring(39, 72).trim().equals("DEP S B COBRO"))
										psSBCobro = "N";
									else
										psSBCobro = "S";
								} else
									psSBCobro = "";
								// 'checa si ya existe el movimiento 'SUCURSAL: NUmerico 4,posiciones 29 a 32
								psSucursal = psRegistro.substring(28, 32);
								// 'REFERENCIA NUM. 8 posiciones de 106 a 113
								plFolioBanco = Long.parseLong(psRegistro.substring(105, 113));
								// ' signo en la posicion 77 para determinar importe
								psCargoAbono = psRegistro.substring(76, 77).equals("+") ? "I" : "E";
								if (psCargoAbono.equals("E")) {
									// 'CONCEPTO ALFANUMERICO 40, posiciones 114 a 153
									psReferencia = psRegistro.substring(105, 113).trim();
								} 
								else {
									//if (lbReferenciaPorChequera)
										psReferencia = psRegistro.substring(105, 133).trim();
									//else
									//	psReferencia = psRegistro.substring(105, (113 + liLongitudReferencia));
								}
								// IMPORTE: numerico 14 posiciones 78 a 91
								pdImporte = Double.parseDouble(psRegistro.substring(77, 91)) / 100;
								// descripcion alfa 40 , posiciones 37 a 76
								sDescripcion = psRegistro.substring(36, 76).trim();
								// concepto alfa 40 ,posiciones 114 a 153
								psConcepto = psRegistro.substring(113, 153).trim();
								// SALDO: NUM 14, posiciones 92 a 105
								dSaldoBanco = Double.parseDouble(psRegistro.substring(91, 105)) / 100;
								parametro = new ParametroLayoutDto();
								parametro.setPsFolioBanco("" + plFolioBanco);
								parametro.setPsConcepto(psConcepto);
								parametro.setPsReferencia(psReferencia);
								parametro.setPlNoEmpresa(piNoEmpresa);
								parametro.setPsIdChequera(psChequera);
								parametro.setPsFecValor(psFecha);
								parametro.setPdImporte(pdImporte);
								parametro.setPdSaldoBancario(dSaldoBanco);
								parametro.setContadorRenglon(renglon);
								List<MovtoBancaEDto> movto = administradorArchivosDao.selecionarMovtoBancaE(parametro);
														
								if (movto.size() >= 0) {
									// obtiene la siguiente secuencia de
									// movto_banca_e
									plFolio = seleccionarFolioReal("secuencia");
									if (plFolio <= 0) {
										for (int h = z; h < nombres.length; h++)
											busquedaArchivos.cambiarExtensionSoloUno(nombres[h], "tmp",	"txt");
										return false;
									}
									insercion = new ParametroLayoutDto();
									insercion.setPlNoEmpresa(piNoEmpresa);
									insercion.setPiBanco(SANTANDER);
									insercion.setPsIdChequera(psChequera);
									insercion.setPsSecuencia(plFolio);
									insercion.setPsFecValor(psFecha);
									insercion.setPsSucursal(psSucursal);
									insercion.setPsFolioBanco("" + plFolioBanco);
									insercion.setPsReferencia(psReferencia);
									insercion.setPsCargoAbono(psCargoAbono);
									insercion.setPdImporte(pdImporte);
									insercion.setPsBSalvoBuenCobro(psSBCobro);
									insercion.setPsConcepto(sDescripcion);
									insercion.setPsUsuarioAlta(idUsuario);
									insercion.setPsDescripcion(psConcepto);
									insercion.setPsArchivo(psArchivo);
									insercion.setPsFecAlta(funciones.ponerFechaSola(obtenerFechaHoy()));
									insercion.setPlNoLineaArchivo(renglon);
									insercion.setPdSaldoBancario(dSaldoBanco);
									if(administradorArchivosDao.insertarMovtoBancaE(insercion)>0)
										dto.setPlRegImportados((dto.getPlRegImportados() + 1));
									insercion = null;
								} else
									piEncontrados++;
							}
						}
					} while (control);
					archivo.close();
					archivo = null;
					
					if (archivoOk) {
						dto.setPlRegLeidos(dto.getPlRegLeidos() + renglon);
						busquedaArchivos.cambiarExtensionSoloUno(nombres[z], "tmp", "old");
					} 
					else
						busquedaArchivos.cambiarExtensionSoloUno(nombres[z], "tmp", "err");
					z++;
				}
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "Archivo: " + nombres[z].getAbsolutePath() + " P:BE, C:AdministradorArchivosBussines, M:leerSantanderTodasChequeras");
		}
		dto.setPsArchivos(psArchivos);
		mensajes.add("Registros Leidos: " + dto.getPlRegLeidos());
		mensajes.add("Registros Sin Chequera: " + dto.getPlRegSinChequera());
		mensajes.add(dto.getPsChequerasInexistentes());
		mensajes.add("Importados: " + dto.getPlRegImportados()+"\n");
		System.setProperty("user.dir", rutaActual);
		return true;
	}

	/*
	 * Private Function lee_santander_buzon(ByRef psArchivos As String, _ ByRef
	 * ps_chequeras_inexistentes As String, _ ByRef plReg_leidos As Long, ByRef
	 * plReg_sin_chequera As Long, _ ByRef plReg_importados As Long) As Boolean
	 */
	public boolean leerSantanderBuzon(ParametroLayoutDto dto) {
		i = 0;
		inicializar();
		psSantander = administradorArchivosDao.consultarConfiguraSet(201) + "santander\\buzon\\";
		if (!psSantander.equals(rutaActual))
			System.setProperty("user.dir", rutaActual + psSantander);
		nombres = busquedaArchivos.obtenerNombreArchivos(".edocta");
		try {
			mensajes.add("\nSantander Buzon");
			if (nombres != null) {
				while (z < nombres.length) {
					nombres = busquedaArchivos.cambiarExtension(nombres, ".edocta", ".tmp");
					psArchivo = nombres[z].getName().replace(".tmp", ".edocta");
					if (psArchivos.equals(""))
						psArchivos = psArchivo;
					else
						psArchivos += "', '" + psArchivo;
					archivo = new Scanner(nombres[z]);
					iContador = 0;
					renglon = 0;
					i = 0;
					archivoOk = true;
					do {
						if (control)
							control = false;
						while (archivo.hasNext()) {
							i = 0;
							iContador ++;
							renglon++;
							psRegistro = archivo.nextLine();
							psRegistro = psRegistro.replace("'", " ");
							psFecha = psRegistro.substring(18, 20) + "/" + psRegistro.substring(16, 18) + "/" + psRegistro.substring(20, 24);
							if(!funciones.isDate(psFecha,false)){
								archivoOk = false;
								break;
							}
							else{
								//checa si es necesario ir por la empresa
								psChequeraNueva = psRegistro.substring(0, 11);
								if(!psChequera.equals(psChequeraNueva)){
			                        psChequera = psChequeraNueva;
			                        bEmpresa = new ParametroBuscarEmpresaDto();
									bEmpresa.setPsChequera(psChequera);
									bEmpresa.setPiBanco(SANTANDER);
									List<CatCtaBancoDto> empresa = administradorArchivosDao.buscarEmpresa(bEmpresa);
			                        if(empresa.size()>0){
			                            piNoEmpresa = empresa.get(0).getNoEmpresa();
			                            bExisteChequera = true;
			                        }
			                        else{
			                            bExisteChequera = false;
			                            dto.setPlRegSinChequera(dto.getPlRegSinChequera() + 1);
										if (dto.getPsChequerasInexistentes().equals(""))
											dto.setPsChequerasInexistentes(psChequera);
										else
											dto.setPsChequerasInexistentes(dto.getPsChequerasInexistentes()	+ ", " + psChequera);
										control = true;
										break;
			                        }
								}
								else{
			                        if(!bExisteChequera){
			                        	dto.setPlRegSinChequera(dto.getPlRegSinChequera() + 1);
			                        	control = true;
										break;
			                        }
								}
								if(psRegistro.charAt(76)=='+'){
			                        if(psRegistro.substring(39, 75).trim().equals("DEP S B COBRO"))
			                            psSBCobro = "N";
			                        else
			                            psSBCobro = "S";
								}
								else
		                        psSBCobro = "";
		                        i = 0;
		                        //'checa si ya existe el movimiento
		                        psSucursal = psRegistro.substring(28, 32);
		                        //El envno de archivo de Santander es programado
		                        plFolioBanco = Long.parseLong(psRegistro.substring(105, 113));
		                        psCargoAbono = psRegistro.substring(76, 77).equals("+") ? "I" : "E";
		                        if(psCargoAbono.equals("E"))
		                        	psReferencia = psRegistro.substring(113, 153);
		                        else{
			                        if(lbReferenciaPorChequera)
			                            psReferencia = psRegistro.substring(113, 153).trim();
			                        else
			                            psReferencia = psRegistro.substring(113, (113 + liLongitudReferencia)).trim();
		                        }
			                    pdImporte = Double.parseDouble(psRegistro.substring(77, 91)) / 100;
			                    psConcepto = psRegistro.substring(36, 76).trim();
			                    sDescripcion = psConcepto;
			                    dSaldoBanco = Double.parseDouble(psRegistro.substring(91, 105)) / 100;
			                    parametro = new ParametroLayoutDto();
								parametro.setPsFolioBanco("" + plFolioBanco);
								parametro.setPsConcepto(psConcepto);
								parametro.setPsReferencia(psReferencia);
								parametro.setPlNoEmpresa(piNoEmpresa);
								parametro.setPsIdChequera(psChequera);
								parametro.setPsFecValor(psFecha);
								parametro.setPdImporte(pdImporte);
								parametro.setPdSaldo(dSaldoBanco);
								parametro.setContadorRenglon(renglon);
								List<MovtoBancaEDto> movto = administradorArchivosDao.selecionarMovtoBancaE(parametro);    
								if(movto.size()<=0){
			                        //'sacamos el siguiente folio de la base de datos
			                        plFolio = seleccionarFolioReal("secuencia");
			                        if(plFolio <= 0) {
										for (int h = z; h < nombres.length; h++)
											busquedaArchivos.cambiarExtensionSoloUno(nombres[h], ".tmp",	".txt");
										return false;
									}
			                        insercion = new ParametroLayoutDto();
									insercion.setPlNoEmpresa(piNoEmpresa);
									insercion.setPiBanco(SANTANDER);
									insercion.setPsIdChequera(psChequera);
									insercion.setPsSecuencia(plFolio);
									insercion.setPsFecValor(psFecha);
									insercion.setPsFolioBanco("" + plFolioBanco);
									insercion.setPsReferencia(psReferencia);
									insercion.setPsCargoAbono(psCargoAbono);
									insercion.setPdImporte(pdImporte);
									insercion.setPsBSalvoBuenCobro(psSBCobro);
									insercion.setPsConcepto(psConcepto);
									insercion.setPsUsuarioAlta(idUsuario);
									insercion.setPsDescripcion(sDescripcion);
									insercion.setPsArchivo(psArchivo);
									insercion.setPsFecAlta(funciones.ponerFechaSola(obtenerFechaHoy()));
									insercion.setPsSucursal(psSucursal);
									insercion.setPlNoLineaArchivo(renglon);
									if(administradorArchivosDao.insertarMovtoBancaE(insercion)>0){
										dto.setPlRegImportados((dto.getPlRegImportados() + 1));
										insercion = null;
										insercion = new ParametroLayoutDto();
										insercion.setPsIdChequera(psChequera);
										insercion.setPsFolioBanco("" + plFolioBanco);
										insercion.setPdImporte(pdImporte);
										insercion.setPsConcepto(psConcepto);
										administradorArchivosDao.insertarTmpMovtoEdoCta(insercion);
										insercion = null;
									}
								} 
								else
									piEncontrados++;
							}
						}
					}while(control);
					archivo.close();
					archivo = null;
					if (archivoOk) {
						dto.setPlRegLeidos(dto.getPlRegLeidos() + renglon);
						busquedaArchivos.cambiarExtensionSoloUno(nombres[z], ".tmp", ".old");
					} 
					else
						busquedaArchivos.cambiarExtensionSoloUno(nombres[z], ".tmp", ".err");
					z++;
				}
			}
	    }catch(FileNotFoundException e){
	    	bitacora.insertarRegistro(new Date().toString()
					+ " "
					+ e.toString()
					+ "Archivo: "
					+ nombres[z].getAbsolutePath()
					+ " P:BE, C:AdministradorArchivosBussines, M:leerSantanderBuzon");
	    	return false;
	    }
	    dto.setPsArchivos(psArchivos);
	    dto.setPsArchivos(psArchivos);
		mensajes.add("Registros Leidos: " + dto.getPlRegLeidos());
		mensajes.add("Registros Sin Chequera: " + dto.getPlRegSinChequera());
		mensajes.add(dto.getPsChequerasInexistentes());
		mensajes.add("Importados: " + dto.getPlRegImportados());
		System.setProperty("user.dir", rutaActual);
		return true;
	}
	/*Private Function lee_santander(ByRef psArchivos As String, _
                ByRef ps_chequeras_inexistentes As String, _
                ByRef plReg_leidos As Long, ByRef plReg_sin_chequera As Long, _
                ByRef plReg_importados As Long) As Boolean*/
	public boolean leerSantander(ParametroLayoutDto dto){
		i = 0;
		inicializar();
		psChequera = "";
		psSantander = administradorArchivosDao.consultarConfiguraSet(201) + "\\santander\\";
		if (!psSantander.equals(rutaActual))
			System.setProperty("user.dir", psSantander);
		
		nombres = busquedaArchivos.obtenerNombreArchivos(".edocta");
		try{
			mensajes.add("Santander");
			if(nombres!=null){
				
				nombres = busquedaArchivos.cambiarExtension(nombres,  ".edocta", ".tmp");
				while(z<nombres.length){
					psArchivo = nombres[z].getName().replace(  ".tmp", ".edocta");
					if (psArchivos.equals(""))
						psArchivos = psArchivo;
					else
						psArchivos += "', '" + psArchivo;
					archivo = new Scanner(nombres[z]);
					iContador = 0;
					renglon = 0;
					i = 0;
					archivoOk = true;
					do {
						if (control)
							control = false;
						while (archivo.hasNext()) {
							psRegistro = archivo.nextLine();
							//psRegistro = psRegistro.substring(5, psRegistro.length());
							iContador++;
							renglon++;
							psRegistro = psRegistro.replace("'", " ");
			                psFecha = psRegistro.substring(16, 18) + "/" + psRegistro.substring(18, 20) + "/" + psRegistro.substring(20, 24);
			                if(!funciones.isDate(psFecha,false)) {
			                    archivoOk = false;
			                    break;
			                } else {
			                	psChequeraNueva = psRegistro.substring(0, 11);
			                    if (!psChequera.equals(psChequeraNueva)) {
			                        psChequera = psChequeraNueva;
			                        bEmpresa = new ParametroBuscarEmpresaDto();
									bEmpresa.setPsChequera(psChequera);
									bEmpresa.setPiBanco(SANTANDER);
									List<CatCtaBancoDto> empresa = administradorArchivosDao.buscarEmpresa(bEmpresa);
			                        if (empresa.size()>0) {
			                        	piNoEmpresa = empresa.get(0).getNoEmpresa();
			                            bExisteChequera = true;
			                        } else {
			                        	bExisteChequera = false;
			                        	dto.setPlRegSinChequera(dto.getPlRegSinChequera() + 1);
										if (dto.getPsChequerasInexistentes().equals(""))
											dto.setPsChequerasInexistentes(psChequera);
										else
											dto.setPsChequerasInexistentes(dto.getPsChequerasInexistentes()	+ ", " + psChequera);
										control = true;
										break;
			                        }
			                    } else {
			                        if (!bExisteChequera) {
			                        	dto.setPlRegSinChequera(dto.getPlRegSinChequera() + 1);
			                            control = true;
										break;
			                        }
			                	}
			                    if(psRegistro.charAt(72) == '+') {
			                        if(psRegistro.substring(39, 72).trim().equals("DEP S B COBRO"))
			                            psSBCobro = "N";
			                        else
			                            psSBCobro = "S";
			                    } else
		                        	psSBCobro = "";
			                    i = 0;
			                    //checa si ya existe el movimiento
			                    psSucursal = psRegistro.substring(28, 32);
			                    //El envno de archivo de Santander es vna enlace
			                    plFolioBanco = Long.parseLong(psRegistro.substring(101, 109));
			                    psCargoAbono = psRegistro.charAt(72) == '+' ? "I" : "E";
			                    if (psCargoAbono.equals("E")) {
			                    	//psReferencia = Format$(Mid$(ps_registro, 110, 20), String(20, "#"))
			                    	psReferencia = psRegistro.substring(109, 129);
			                    } else {
			                        if (lbReferenciaPorChequera)
			                            psReferencia = psRegistro.substring(109, 129);
			                        else {
			                            //psReferencia = Trim(Mid$(ps_registro, 110, liLongitud_referencia))
			                        	try {
			                        		psReferencia = psRegistro.substring(109, 129).trim().substring(psRegistro.substring(109, 129).trim().length()-10);
			                    		} catch(StringIndexOutOfBoundsException e) {
			                    			psReferencia = psRegistro.substring(109, 129).trim();
			                    		}
			                        }
			                    }
			                    pdImporte = Double.parseDouble(psRegistro.substring(77, 91)) / 100;
			                    //pdImporte = Double.parseDouble(psRegistro.substring(73, 87)) / 100;
			                    psConcepto = psRegistro.substring(32, 72).trim();
			                    sDescripcion = psConcepto;
			                    dSaldoBanco = Double.parseDouble(psRegistro.substring(87, 101)) / 100;
			                    parametro = new ParametroLayoutDto();
								parametro.setPsFolioBanco("" + plFolioBanco);
								parametro.setPsConcepto(psConcepto);
								parametro.setPsReferencia(psReferencia);
								parametro.setPlNoEmpresa(piNoEmpresa);
								parametro.setPsIdChequera(psChequera);
								parametro.setPsFecValor(psFecha);
								parametro.setPdImporte(pdImporte);
								parametro.setPdSaldo(dSaldoBanco);
								parametro.setContadorRenglon(renglon);
								List<MovtoBancaEDto> movto = administradorArchivosDao.selecionarMovtoBancaE(parametro);  
								if(movto.size()<=0) {
			                        //'sacamos el siguiente folio de la base de datos
			                        plFolio = seleccionarFolioReal("secuencia");
			                        if (plFolio <= 0) {
										for (int h = z; h < nombres.length; h++)
											busquedaArchivos.cambiarExtensionSoloUno(nombres[h], ".tmp",	".txt");
										return false;
									}
			                        insercion = new ParametroLayoutDto();
									insercion.setPlNoEmpresa(piNoEmpresa);
									insercion.setPiBanco(SANTANDER);
									insercion.setPsIdChequera(psChequera);
									insercion.setPsSecuencia(plFolio);
									insercion.setPsFecValor(psFecha);
									insercion.setPsSucursal(psSucursal);
									insercion.setPsFolioBanco("" + plFolioBanco);
									insercion.setPsReferencia(psReferencia);
									insercion.setPsCargoAbono(psCargoAbono);
									insercion.setPdImporte(pdImporte);
									insercion.setPsBSalvoBuenCobro(psSBCobro);
									insercion.setPsConcepto(psConcepto);
									insercion.setPsUsuarioAlta(idUsuario);
									insercion.setPsDescripcion(sDescripcion);
									insercion.setPsArchivo(psArchivo);
									insercion.setPsFecAlta(funciones.ponerFechaSola(obtenerFechaHoy()));
									insercion.setPsObservacion("");
									insercion.setPsCveConcepto("");
									insercion.setPdSaldoBancario(dSaldoBanco);
									insercion.setPlNoLineaArchivo(renglon);
									if (administradorArchivosDao.insertarMovtoBancaE(insercion) > 0) {
										insercion = null;
										dto.setPlRegImportados((dto.getPlRegImportados() + 1));
										insercion = new ParametroLayoutDto();
										insercion.setPsIdChequera(psChequera);
										insercion.setPsFolioBanco("" + plFolioBanco);
										insercion.setPdImporte(pdImporte);
										insercion.setPsConcepto(psConcepto);
										administradorArchivosDao.insertarTmpMovtoEdoCta(insercion);
										insercion = null;
									}
								} else
									piEncontrados++;
								//ACM 22/01/07 - Se insertan todos los movtos. del archivo para luego seleccionarlos desde la tabla
			                }
						}
					} while (control);
					archivo.close();
					archivo = null;
					if (archivoOk) {
						dto.setPlRegLeidos(dto.getPlRegLeidos() + renglon);
						busquedaArchivos.cambiarExtensionSoloUno(nombres[z], ".tmp", ".old");
					} else {
						dto.setPlRegLeidos(dto.getPlRegLeidos() + renglon);
						busquedaArchivos.cambiarExtensionSoloUno(nombres[z], ".tmp", ".err");
					}
					z++;
				}
			}
		} catch(FileNotFoundException e) {
	    	bitacora.insertarRegistro(new Date().toString()
					+ " "
					+ e.toString()
					+ "Archivo: "
					+ nombres[z].getAbsolutePath()
					+ " P:BE, C:AdministradorArchivosBussines, M:leerSantander");
	    	return false;
	    }
		dto.setPsArchivos(psArchivos);
		mensajes.add("Registros Leidos: " + dto.getPlRegLeidos());
		mensajes.add("Registros Sin Chequera: " + dto.getPlRegSinChequera());
		mensajes.add(dto.getPsChequerasInexistentes());
		mensajes.add("Importados: " + dto.getPlRegImportados());
		System.setProperty("user.dir", rutaActual);
		return true;
	}
	
	/**
	 * 
	 * @param param
	 * @return int Retorna el folio real de la BD
	 */
	public int seleccionarFolioReal(String param) {
		administradorArchivosDao.actualizarFolioReal(param);
		return administradorArchivosDao.seleccionarFolioReal(param);
	}

	/**
	 * 
	 * @param psArchivos
	 * @param piBanco
	 * @return int
	 * 
	 * Actualiza el campo fecha_banca de cat_cta_banco
	 */
	public int actualizarFechaBanca(String psArchivos, int piBanco) {
		return administradorArchivosDao.actualizarFechaBanca(psArchivos, piBanco);
	}

	/**
	 * 
	 * @return Date
	 * 
	 * Obtine la fecha de hoy segun la tabla fechas de la BD
	 */
	public Date obtenerFechaHoy() {
		return administradorArchivosDao.obtenerFechaHoy();
	}

	/**
	 * 
	 * @param piBanco
	 * @param piArchivos
	 * @return int
	 * 
	 * Actualizar saldo de banco en chequeras con el maximo saldo del registro
	 * cuya fecha sea la mayor agrupando por banco, chequera y empresa
	 * 
	 * Sub actualizar_saldo_chequera_version2(pi_banco As Integer, Optional
	 * pi_archivos As String) se cambia la forma de actualizar los saldos en
	 * cat_cta_banco se agrego la funcion
	 * FunSQLUpdateSaldos_CatCtaBanco_version3 para que la actualizacion de
	 * saldos sea mas sencilla
	 */
	public int actualizarSaldoChequeraV2(int piBanco, String piArchivos) {
		return administradorArchivosDao.actualizarSaldoChequeraV2(piBanco, piArchivos);
	}

	/**
	 * @author Cristian Garcia Garcia
	 * @since 26/Octubre/2010
	 * @see <p>Aqui se realizaran las consultas necesarias en la lectura del layout</p>
	 */
	
	public boolean leerBanamex(ParametroLayoutDto dto){
		int renglon = 0;
		int contInexistentes = 0;
		int noEmpresa = 0;
		int liLongitudReferencia = 16;// se obtiene de la tabla referencia_enc
		int registrosImportados = 0;
		int secuencia = 0;
		int resInsert = 0;
		int registrosLeidos = 0;
		int i = 0;
		int iContador=0;

		double pdImporte = 0;

		//boolean lee_banamex = false;
		boolean verificarChequera;
		boolean bMassPayment = false;
		boolean bImportar;
		boolean verificarDatos;

		String cadena = "";
		String fecValor = "";
		String valorChequera = "";
		String cheqInexistentes = "";
		String psSbCobro = "";
		String psCargoAbono = "";
		String psReferencia = "";
		String psConcepto = "";
		String psDescripcion = "";
		String psSucursal = "";
		String psObservacion = "";
		String psFolioBanco = "";
		String psBanamex = "";
		inicializar();
		mensajes.add("\nBANAMEX");
		try {
			psBanamex = administradorArchivosDao.consultarConfiguraSet(201) + "\\banamex\\";
			
			if (!psBanamex.equals(rutaActual))
				System.setProperty("user.dir", psBanamex);
			File[] nombres = busquedaArchivos.obtenerNombreArchivos(".txt");
			
			if(nombres!=null){
				nombres = busquedaArchivos.cambiarExtension(nombres, ".txt", ".tmp");
				CatCtaBancoDto catCtaBancoDto = new CatCtaBancoDto();
				MovtoBancaEDto movtoBancaEDto = new MovtoBancaEDto();
				
				// Obtener fecha valor
				for (i = 0; i < nombres.length; i++) {
					FileReader arch = new FileReader(nombres[i]);
					BufferedReader buf = new BufferedReader(arch);
					psArchivo=nombres[i].getName().replace(".tmp", ".txt");
					
					if (psArchivos.equals(""))
						psArchivos = psArchivo;
					else
						psArchivos += "', '" + psArchivo;
					if((cadena = buf.readLine())==null)
						break;
					cadena = cadena.replace("'", " ");
					
					if (cadena.length() == 6)
						fecValor = "" + cadena.substring(0, 2) + "/" + cadena.substring(2, 4) + "/20" + cadena.substring(4, 6);
					else
						fecValor = "" + cadena.substring(0, 2) + "/" + cadena.substring(2, 4) + cadena.substring(4, 8);
					
					if(funciones.isDate(fecValor, false)){
						if((cadena = buf.readLine())==null)
							break;
						cadena = cadena.replace("'", " ");
						renglon=2;
						
						if(cadena.length()!=24){
							movtoBancaEDto.setFecValor(administradorArchivosDao.cambiarFecha(fecValor));
							catCtaBancoDto.setIdBanco(BANAMEX);
							movtoBancaEDto.setFecAlta(administradorArchivosDao.obtenerFechaHoyBase());
							movtoBancaEDto.setArchivo(psArchivo);
							
							do{
								if(archivoOk)
									archivoOk=false;
								
								while ((cadena = buf.readLine()) != null) {
									cadena = cadena.replace("'", " ");
									iContador++;
									registrosLeidos=iContador;
									verificarDatos = false;
									renglon++;
									
									if (cadena.length() == 20)
										verificarChequera = true;
									else
										verificarChequera = false;
									
									if (cadena.charAt(0) == 'N' || verificarChequera) {
										if (verificarChequera)
											valorChequera = cadena.substring(0, 4)+ cadena.substring(13);
										else
											valorChequera = cadena.substring(41, 45)+ cadena.substring(54, 61);
										
										
										catCtaBancoDto.setIdChequera(valorChequera);
										/*
										 * Si la chequera tiene habilitada la bandera de
										 * MassPayment no subirla a movto_banca_e, ya que se
										 * importo en citibank_MN
										 */
										List<CatCtaBancoDto> datosConsulta = administradorArchivosDao.consultarEmpresa(catCtaBancoDto);
										
										if (datosConsulta.size() > 0) {
											if (datosConsulta.get(0).getPagoMass().equals("S"))
												bMassPayment = true;
											else
												bMassPayment = false;
											noEmpresa = datosConsulta.get(0).getNoEmpresa();
										} else {
											contInexistentes++;
											cheqInexistentes += valorChequera + ",";
											if(contInexistentes%3==0)
												cheqInexistentes+="\n";
											archivoOk=true;
											break;
										}
									}
									
									if ((cadena.substring(0, 1).equals("C")
											|| cadena.substring(0, 1).equals("A") || verificarChequera)
											&& noEmpresa > 0) {
										if (verificarChequera) {
											if ((cadena = buf.readLine()) != null)
												renglon++;
											psFolioBanco = "";
											psSbCobro = "";
											psCargoAbono = "S";
											psReferencia = "";
											psConcepto = "SALDO ULTIMA TRANS";
											pdImporte = Double.parseDouble(cadena.substring(63)) / 100;
											if (cadena.substring(62, 63).equals("-")) {
												pdImporte = pdImporte * -1;
											}
											verificarDatos = true;
										} else {
											psFolioBanco = cadena.substring(7, 17);
											if (cadena.substring(0, 1).equals("A")) {
												// 1009 n 1300 es deposito SBC
												if ((cadena.substring(1, 3) + cadena.substring(54, 56)).equals("1009")
														|| (cadena.substring(1, 3) + cadena.substring(54, 56)).equals("1300"))
													psSbCobro = "N";
												else
													psSbCobro = "S";
											} else
												psSbCobro = "";
											if (cadena.substring(0, 1).equals("A"))
												psCargoAbono = "I";
											else
												psCargoAbono = "E";
											if (lbReferenciaPorChequera) {
												psReferencia = (cadena.substring(7, 17)); // Referencia Numerica
											} else {
												if (psCargoAbono.equals("E"))
													psReferencia = cadena.substring(17,	17 + liLongitudReferencia).trim();
												else {
													psReferencia = cadena.substring(17, 17 + liLongitudReferencia).trim();
													if (lbReferenciaNumerica && psReferencia.indexOf("AUT.") == -1)
														psReferencia = cadena.substring(16 - liLongitudReferencia + 1, liLongitudReferencia);
												}
											}
											psConcepto = cadena.substring(1, 3)	+ cadena.subSequence(54, 56);
											if ((Double.parseDouble(psConcepto)) == 0){
												archivoOk = true;
												break;
											}
											pdImporte = (Double.parseDouble(cadena.substring(37, 54))) / 100;
											psDescripcion = cadena.substring(17, 27); // Referencia alfanumerica
											verificarDatos = true;
										}
									}
									
									// Solo subir los saldos de las chequeras de Mass Payment
									if (bMassPayment && !verificarChequera)
										if (Double.parseDouble(psConcepto) != 5500
												|| Double.parseDouble(psConcepto) != 7500){
											archivoOk=true;
											break;
										}
									if (verificarDatos) {
										// Verifica si ya existe el registro en movto_banca_e
										bImportar = false;
										movtoBancaEDto.setFolioBanco(psFolioBanco);
										movtoBancaEDto.setConcepto(psConcepto);
										movtoBancaEDto.setReferencia(psReferencia);
										movtoBancaEDto.setNoEmpresa(noEmpresa);
										movtoBancaEDto.setIdChequera(valorChequera);
										movtoBancaEDto.setImporte(pdImporte);
										movtoBancaEDto.setDescripcion(psDescripcion);
										movtoBancaEDto.setNoLineaArch(renglon);
										int movi = administradorArchivosDao.consultarMovimiento(movtoBancaEDto);
										if (movi == 0)
											bImportar = true;
										/*
										 * verificar el la base si esta el registro Set
										 * rst_movto = gobjSQL.FunSQLSelect6(Val(ps_foliobanco),
										 * psConcepto, _ psReferencia, pi_noempresa, ps_Chequera _ ,
										 * ps_Fecha, pdImporte, psDescripcion,
										 * plContadorRenglon)
										 */
										if (bImportar) {
											/*
											 * sacamos el siguiente folio de la base de datos
											 * pl_folio = gobjVarGlobal.Folio_Real("secuencia")
											 * If pl_folio <= 0 Then Exit Function End If
											 */
										//	lee_banamex = true;
											if (psCargoAbono == "S")
												psSucursal = valorChequera.substring(0, 4);
											else
												psSucursal = cadena.substring(3, 7);
				
											movtoBancaEDto.setBSalvoBuenCobro(psSbCobro);
											movtoBancaEDto.setIdBanco(BANAMEX);
											movtoBancaEDto.setCargoAbono(psCargoAbono);
											psObservacion = "";
											movtoBancaEDto.setObservacion(psObservacion);
											movtoBancaEDto.setSucursal(psSucursal);
											secuencia = seleccionarFolioReal("secuencia");
											if (secuencia <= 0){
												for (int h = i; h < nombres.length; h++)
													busquedaArchivos.cambiarExtensionSoloUno(nombres[h], ".tmp",	".txt");
												return false;
											}
											movtoBancaEDto.setSecuencia(secuencia);
											movtoBancaEDto.setUsuarioAlta(idUsuario);
											/*
											 * Call
											 * gobjSQL.FunSQLInserta_movto_banca_e(pi_noempresa,
											 * BANAMEX, _ ps_Chequera, pl_folio, ps_Fecha, _
											 * psSucursal, ps_foliobanco, psReferencia, _
											 * psCargoAbono, pdImporte, ps_SBCobro, _
											 * psConcepto, GI_USUARIO, psDescripcion, _
											 * ps_Archivo, Format(GT_FECHA_HOY, "dd/mm/yyyy"), _
											 * psObservacion, "", 0, "", plContadorRenglon)
											 */
											
											System.out.println("llega aqui sin broncas " + secuencia);
											resInsert = administradorArchivosDao.insertaMovimientoBanca(movtoBancaEDto);
											if (resInsert >0)
												registrosImportados++;
										}
									}
								}
							}while(archivoOk);
							dto.setPlRegLeidos(registrosLeidos);
							dto.setPlRegImportados(registrosImportados);
						}
					}
					else{
						mensajesUsuario.add("Banamex; El archivo: " + psArchivo + " no tiene el formato vnlido de Banamex");
					}
					buf.close();
					arch.close();
					busquedaArchivos.cambiarExtensionSoloUno(nombres[i], ".tmp",".old");
				}
				dto.setPsArchivos(psArchivos);
				mensajes.add("Registros Leidos: " + dto.getPlRegLeidos());
				mensajes.add("Inexistentes: " + contInexistentes);
				mensajes.add(cheqInexistentes);
				mensajes.add("Registros Importados: " + dto.getPlRegImportados());
			}
			else{
				mensajes.add("Error de Lectura");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.setProperty("user.dir", rutaActual);
		return true;
	}
	
	public boolean leerBanamexMismoDia(ParametroLayoutDto dto){
		int piNoEmpresa = 0;
		int iContador = 0;
		int renglon = 0;
		int plRegSinChequera = 0;
		int plRegLeidos = 0;
		int registrosImportados = 0;
		int secuencia = 0;
		int res = 0;
		int i = 0;

		double pdImporte = 0;

		boolean pbExisteChequera = false;
		boolean sigueLeyendo1 = false;
		boolean leeBanamexMismoDia = true;

		String psFolioBanco = "";
		String psArchivo = "";
		String psRegistro = "";
		String psFecha = "";
		String psChequera = "";
		String psSBCobro = "";
		String psBanamex = "";
		String psReferencia = "";
		String psCargoAbono = "";
		String psSucursal = "";
		String psConcepto = "";
		String psCodigo = "";
		String psDescripcion = "";
		String psConceptosNoEncontrados = "";
		String psChequerasInexistentes = "";
		
		// rst_Empresa As ADODB.Recordset
		// rst_movto As ADODB.Recordset
		inicializar();
		psBanamex=administradorArchivosDao.consultarConfiguraSet(201)+ "\\banamex\\mismo_dia\\";
		if(!rutaActual.equals(psBanamex))
			System.setProperty("user.dir", psBanamex);
		try{
			mensajes.add("\nBanamex Mismo Dna");
			File[] nombres = busquedaArchivos.obtenerNombreArchivos(".txt");
			nombres = busquedaArchivos.cambiarExtension(nombres, ".txt", ".tmp");
			CatCtaBancoDto catCtaBancoDto = new CatCtaBancoDto();
			MovtoBancaEDto movtoBancaEDto = new MovtoBancaEDto();
			movtoBancaEDto.setUsuarioAlta(idUsuario);
			renglon = 1;
			for (i = 0; i < nombres.length; i++) {
				psArchivo=nombres[i].getName().replace(".tmp", ".txt");
				if (psArchivos.equals(""))
					psArchivos = psArchivo;
				else
					psArchivos += "', '" + psArchivo;
				FileReader arch = new FileReader(nombres[i]);
				BufferedReader buf = new BufferedReader(arch);
				psRegistro = buf.readLine();
				if (psRegistro.length() == 6)
					psFecha = psRegistro.substring(0, 2) + "/"	+ psRegistro.substring(2, 4) + "/20" + psRegistro.substring(4, 6);
				else
					psFecha = psRegistro.substring(0, 2) + "/"	+ psRegistro.substring(2, 4)+ psRegistro.substring(4, 8);
				movtoBancaEDto.setFecValor(administradorArchivosDao.cambiarFecha(psFecha));
				catCtaBancoDto.setIdBanco(BANAMEX);
				movtoBancaEDto.setFecAlta(administradorArchivosDao.obtenerFechaHoyBase());
				movtoBancaEDto.setArchivo(psArchivo);
				psRegistro = buf.readLine();
				if (psRegistro.length() == 29) {
					psSucursal = psRegistro.substring(0, 4); // sucursal 1 - 4 (N4)
					psChequera = psSucursal + psRegistro.substring(17, 24); // cuenta de 5 - 24 (N20)
					catCtaBancoDto.setIdChequera(psChequera);
					List<CatCtaBancoDto> consultarEmpresa = administradorArchivosDao.consultarEmpresa(catCtaBancoDto);
					if (consultarEmpresa.size() > 0) {
						piNoEmpresa = consultarEmpresa.get(0).getNoEmpresa();
						pbExisteChequera = true;
					} else {
						psChequerasInexistentes += psChequera;
						pbExisteChequera = false;
						plRegSinChequera++;
					}
					if (pbExisteChequera){
						do {
							while ((psRegistro = buf.readLine()) != null){ // repite hasta el fin de archivo
								sigueLeyendo1 = false;
								iContador++;
								plRegLeidos = iContador;
								psRegistro.replace("'", " ");
								if (psRegistro.length() != 58){
									sigueLeyendo1 = true;
									break;
								}
								// psDescripcion = psReferencia;
								psFolioBanco = psRegistro.substring(11, 21); // Referencia 12-21 (N10)
								psCodigo = psRegistro.substring(9, 11);
								if (psCodigo.equals("00")) {
									sigueLeyendo1 = true;
									break;
								}
								psConcepto = psCodigo + psRegistro.substring(56, 58);
								pdImporte = (Double.parseDouble(psRegistro.substring(41, 56))) / 100;
								List<String> analizarConcepto = administradorArchivosDao.analizarConceptoBanamex(psConcepto,BANAMEX);
								if (analizarConcepto.size() > 0)
									psCargoAbono = analizarConcepto.get(0);
								if (psCargoAbono.equals("")) {
									if (psConceptosNoEncontrados.indexOf(psConcepto) == -1)
										psConceptosNoEncontrados += psConcepto + ",";
									sigueLeyendo1 = true;
									break;
								}
								if (psCargoAbono.equals("E"))
									psReferencia = psRegistro.substring(21,	21 + liLongitudReferencia).trim(); // 20 Referencia 22-41 (X20)
								else
									psReferencia = psRegistro.substring(21,	21 + liLongitudReferencia).trim(); // Referencia 22-41 (X20)
								if (psConcepto.equals("1009") || psConcepto.equals("1300"))
									psSBCobro = "N";
								else if (psCargoAbono.equals("I"))
									psSBCobro = "S";
								else
									psSBCobro = "";
								// checa si ya existe el movimiento
								psReferencia = psReferencia.trim();
								movtoBancaEDto.setFolioBanco(psFolioBanco);
								movtoBancaEDto.setConcepto(psConcepto);
								movtoBancaEDto.setReferencia(psReferencia);
								movtoBancaEDto.setNoEmpresa(piNoEmpresa);
								movtoBancaEDto.setIdChequera(psChequera);
								movtoBancaEDto.setImporte(pdImporte);
								movtoBancaEDto.setDescripcion(psDescripcion);
								movtoBancaEDto.setNoLineaArch(renglon);
								movtoBancaEDto.setEstatusExp("N");
								int consultarMovimiento = administradorArchivosDao.consultarMovimiento(movtoBancaEDto);
								/*
								 * Set rst_movto =
								 * gobjSQL.FunSQLSelect6(Val(ps_foliobanco),
								 * psConcepto, _ psReferencia, pi_noempresa,
								 * ps_Chequera _ , ps_Fecha, pdImporte, "N")
								 */
	
								if (consultarMovimiento <= 0) {
									// sacamos el siguiente folio de la base de datos
									secuencia = seleccionarFolioReal("secuencia");
									if (secuencia <= 0){
										for (int h = i; h < nombres.length; h++)
											busquedaArchivos.cambiarExtensionSoloUno(nombres[h], ".tmp",	".txt");
										return false;
									}
									movtoBancaEDto.setBSalvoBuenCobro(psSBCobro);
									movtoBancaEDto.setIdBanco(BANAMEX);
									movtoBancaEDto.setCargoAbono(psCargoAbono);
									movtoBancaEDto.setSucursal(psSucursal);
									movtoBancaEDto.setSecuencia(secuencia);
									res = administradorArchivosDao.insertaMovimientoBanca(movtoBancaEDto);
									if (res > 0) 
										registrosImportados++;
									leeBanamexMismoDia = true;
									/*
									 * Call
									 * gobjSQL.FunSQLInserta_movto_banca_e(pi_noempresa,
									 * 2, _ ps_Chequera, pl_folio, ps_Fecha, _
									 * psSucursal, ps_foliobanco, psReferencia, _
									 * psCargoAbono, pdImporte, ps_SBCobro, _
									 * psConcepto, GI_USUARIO, ps_descripcion, _
									 * ps_Archivo, Format(GT_FECHA_HOY,
									 * "dd/MM/yyyy"), _ "MISMO DIA")
									 */
								}
							}
						} while (sigueLeyendo1);
					}
					dto.setPlRegLeidos(plRegLeidos);
					dto.setPlRegSinChequera(plRegSinChequera);
					dto.setPsChequerasInexistentes(psChequerasInexistentes);
					dto.setPlRegImportados(registrosImportados);
				}
				buf.close();
				arch.close();
				busquedaArchivos.cambiarExtensionSoloUno(nombres[i], ".tmp", ".old");
			}
		}catch(Exception e){
			e.printStackTrace();
			leeBanamexMismoDia = false;
		}
		mensajes.add("Registros Leidos: " + dto.getPlRegLeidos());
		mensajes.add("Inexistentes: " + dto.getPlRegSinChequera());
		mensajes.add(dto.getPsChequerasInexistentes());
		mensajes.add("Registros Importados: " + dto.getPlRegImportados());
		return leeBanamexMismoDia;
	}

	public boolean leerBanamexTEF(ParametroLayoutDto dto){
		int piNoEmpresa = 0;
		int iContador = 0;
		int piEstatus = 0;
		int piTipoRegistro = 0;
		int plRegLeidos = 0;
		int plRegSinChequera = 0;
		int plRegImportados = 0;
		int secuencia = 0;
		int i = 0;

		boolean pbExisteChequera = false;
		boolean pbInterbancario = false;
		boolean pbAutomatico = false;
		boolean pbEstatusErr = false;
		boolean fechaDate = false;
		boolean leeBanamexTEF = true;

		double pdImporte = 0;

		String psFolioBanco = "";
		String psArchivo = "";
		String psRegistro = "";
		String psFecha = "";
		String psChequera = "";
		String psSBCobro = "";
		String psBanamex = "";
		String psReferencia = "";
		String psCargoAbono = "";
		String psSucursal = "";
		String psConcepto = "";
		String psDescripcion = "";
		String psChequerasInexistentes = "";
		inicializar();
		psBanamex=administradorArchivosDao.consultarConfiguraSet(201)+ "\\banamex_tef\\";
		if(!rutaActual.equals(psBanamex))
			System.setProperty("user.dir", psBanamex);
		File[] nombres = busquedaArchivos.obtenerNombreArchivos(".txt");
		nombres = busquedaArchivos.cambiarExtension(nombres, ".txt", ".tmp");
		CatCtaBancoDto catCtaBancoDto = new CatCtaBancoDto();
		MovtoBancaEDto movtoBancaEDto = new MovtoBancaEDto();
		renglon = 1;
		movtoBancaEDto.setUsuarioAlta(idUsuario);
		mensajes.add("\nBanamex TEF");
		if(nombres==null){
			mensajes.add("Registros Leidos: " + dto.getPlRegLeidos());
			mensajes.add("Inexistentes: " + dto.getPlRegSinChequera());
			mensajes.add(dto.getPsChequerasInexistentes());
			mensajes.add("Registros Importados: " + dto.getPlRegImportados());
			return false;
		}
		for (i = 0; i < nombres.length; i++) {
			FileReader arch;
			try {
				psArchivo=nombres[i].getName().replace(".tmp", ".txt");
				if (psArchivos.equals(""))
					psArchivos = psArchivo;
				else
					psArchivos += "', '" + psArchivo;
				arch = new FileReader(nombres[i]);
				BufferedReader buf = new BufferedReader(arch);
				psRegistro = buf.readLine(); 
				if (psRegistro.substring(121,122).equals("C"))
					pbInterbancario = true;
				else
					pbInterbancario = false;
				while ((psRegistro = buf.readLine()) != null && !pbEstatusErr){// repite hasta el fin de archivo
					//Registro de cargos o abonos individuales
					psRegistro = psRegistro.replace("'", " ");
					if (psRegistro.length() >= 229) {
						if (pbInterbancario)
							piEstatus = (Integer.parseInt(psRegistro.substring(229,	230)));
						else
							piEstatus = (Integer.parseInt(psRegistro.substring(230,	231)));
					}
					if ((piEstatus == 1) || (piEstatus == 2)){// 'Se rechaza el archivo
						if (plRegLeidos == 0)
							plRegLeidos++;
						pbEstatusErr = true;
						if (!pbAutomatico)
							mensajesUsuario.add("Banamex TEF: El archivo: "+ psArchivo +" tiene estatus pendiente de aplicar Consnltelo nuevamente en Banamex");
					}
				}
				arch.close();
				buf.close();
				if (pbEstatusErr)
					busquedaArchivos.cambiarExtensionSoloUno(nombres[i], ".tmp",".err"); // Cambia extensinn de archivo a .err
				if (!pbEstatusErr) {
					arch = new FileReader(nombres[i]);
					buf = new BufferedReader(arch);
					psRegistro = buf.readLine();
					// checa la fecha en la primer linea
					psRegistro = psRegistro.replace("'", " ");
					psFecha = "" + psRegistro.substring(13, 15) + "/" + psRegistro.substring(15, 17) + "/20" + psRegistro.substring(17, 19); // fecha 1-6 ddmmaa (N6)
					fechaDate = funciones.isDate(psFecha, false);
					if (fechaDate) {
						if (psRegistro.substring(121, 122).equals("C"))
							pbInterbancario = true;
						else
							pbInterbancario = false;
						// leer la sucursa y cuenta de la segunda linea
						psRegistro = buf.readLine();
						psRegistro = psRegistro.replace("'", " ");
						if (psRegistro.length() == 87 || psRegistro.length() == 69)	{
							psSucursal = psRegistro.substring(25, 29); // sucursal 26 - 29 (N4)
							psChequera = psSucursal + psRegistro.substring(42, 49); // cuenta de 30-49 (N20)
							catCtaBancoDto.setIdBanco(BANAMEX);
							catCtaBancoDto.setIdChequera(psChequera);
							List<CatCtaBancoDto> consultarEmpresa = administradorArchivosDao.consultarEmpresa(catCtaBancoDto);
							if (consultarEmpresa.size() > 0) {
								piNoEmpresa = consultarEmpresa.get(0).getNoEmpresa();
								pbExisteChequera = true;
							} else {
								psChequerasInexistentes += psChequera;
								pbExisteChequera = false;
								plRegSinChequera++;
							}
							if (pbExisteChequera) {
								while ((psRegistro = buf.readLine()) != null){ // repite hasta el fin de archivo
									psRegistro = psRegistro.replace("'", " ");
									piTipoRegistro = Integer.parseInt(psRegistro.substring(0, 1));
									if (piTipoRegistro == 3){ // Registro de Detalle
										iContador++;
										plRegLeidos = iContador;
										if (pbInterbancario) {
											psDescripcion = psRegistro.substring(180, 204);// 24 DESC
											psFolioBanco = psRegistro.substring(217, 228);// 12
											pdImporte = Double.parseDouble(psRegistro.substring(5, 23)) / 100;// 18
										} else {
											psDescripcion = psRegistro.substring(184, 208);// 24
											psFolioBanco = psRegistro.substring(218, 230);// 12
											pdImporte = Double.parseDouble(psRegistro.substring(5, 23)) / 100;// 18
										}
										// Logica inversa porque la chequera de
										// cargo esta en el registro global
										// Verificar el estatus para determinar si
										// es un Cargo o abono(Devolucion)
										if (pbInterbancario)
											piEstatus = Integer.parseInt(psRegistro.substring(229, 230));
										else
											piEstatus = Integer.parseInt(psRegistro.substring(230, 231));
										if (piEstatus == 3 || piEstatus == 0) {
											psCargoAbono = "E";
											psConcepto = "CARGO EN CUENTA";
											if (pbInterbancario)
												psReferencia = psRegistro.substring(45, 85); // Referencia 50-89 (X40)
											else
												psReferencia = psRegistro.substring(88 - liLongitudReferencia + 1,89); // Referencia 50-89 (X40)
										} else {
											psCargoAbono = "I";
											// 1014 Depnsito por Devol. Mcna. Darlo de alta en equivale_concepto
											psConcepto = "1014"; // -----> Notificar a MS
											if (pbInterbancario)
												psReferencia = psRegistro.substring(45, 85).trim(); // Referencia 50-89 (X40)
											else
												psReferencia = psRegistro.substring(88 - liLongitudReferencia + 1,	89); // Referencia 50-89 (X40)
										}
										psSBCobro = "";
										// checa si ya existe el movimiento
										psReferencia = psReferencia.trim();
										/*
										 * Set rst_movto =
										 * gobjSQL.FunSQLSelect6(Val(ps_foliobanco),
										 * psConcepto, _ psReferencia, pi_noempresa,
										 * ps_Chequera _ , ps_Fecha, pdImporte, "N")
										 */
										movtoBancaEDto.setFolioBanco(psFolioBanco);
										movtoBancaEDto.setConcepto(psConcepto);
										movtoBancaEDto.setReferencia(psReferencia);
										movtoBancaEDto.setNoEmpresa(piNoEmpresa);
										movtoBancaEDto.setIdBanco(BANAMEX);
										movtoBancaEDto.setIdChequera(psChequera);
										movtoBancaEDto.setFecValor(administradorArchivosDao.cambiarFecha(psFecha));
										movtoBancaEDto.setImporte(pdImporte);
										movtoBancaEDto.setEstatusExp("N");
										movtoBancaEDto.setBSalvoBuenCobro(psSBCobro);
										movtoBancaEDto.setDescripcion(psDescripcion);
										movtoBancaEDto.setArchivo(nombres[i].getName());
										movtoBancaEDto.setFecAlta(administradorArchivosDao.obtenerFechaHoyBase());
										movtoBancaEDto.setNoLineaArch(iContador);
										movtoBancaEDto.setSucursal(psSucursal);
										movtoBancaEDto.setCargoAbono(psCargoAbono);
										int consultarRegistro = administradorArchivosDao.consultarMovimiento(movtoBancaEDto);
										if (consultarRegistro <= 0) {
											// sacamos el siguiente folio de la base de datos
											secuencia = administradorArchivosDao.seleccionarFolioReal("secuencia");
											if (secuencia <= 0){
												for (int h = i; h < nombres.length; h++)
													busquedaArchivos.cambiarExtensionSoloUno(nombres[h], ".tmp",".txt");
												return false;
											}
											movtoBancaEDto.setSecuencia(secuencia);
											leeBanamexTEF = true;
											/*
											 * Call
											 * gobjSQL.FunSQLInserta_movto_banca_e(pi_noempresa,
											 * 2, _ ps_Chequera, pl_folio, ps_Fecha, _
											 * psSucursal, ps_foliobanco,
											 * psReferencia, _ psCargoAbono,
											 * pdImporte, ps_SBCobro, _ psConcepto,
											 * GI_USUARIO, ps_descripcion, _
											 * ps_Archivo, Format(GT_FECHA_HOY,
											 * "dd/MM/yyyy"), _ "BANAMEX TEF")
											 */
											int res = administradorArchivosDao.insertaMovimientoBanca(movtoBancaEDto);
											if (res>0) {
												plRegImportados++;
												administradorArchivosDao.actualizarFolioReal("secuencia");
											}
										}
									}
								}
							}
							arch.close();
							buf.close();
							busquedaArchivos.cambiarExtensionSoloUno(nombres[i],".tmp", ".old");
						}
					}
				}
				dto.setPlRegLeidos(plRegLeidos);
				dto.setPlRegSinChequera(plRegSinChequera);
				dto.setPsChequerasInexistentes(psChequerasInexistentes);
				dto.setPlRegImportados(plRegImportados);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}catch(IOException e1){
				e1.printStackTrace();
			}
		}
		mensajes.add("Registros Leidos: " + dto.getPlRegLeidos());
		mensajes.add("Inexistentes: " + dto.getPlRegSinChequera());
		mensajes.add(dto.getPsChequerasInexistentes());
		mensajes.add("Registros Importados: " + dto.getPlRegImportados());
		return leeBanamexTEF;
	}
	
	public boolean leerBanamexVersionInternet(ParametroLayoutDto dto){
		int renglon = 0;
		int contInexistentes = 0;
		int noEmpresa = 0;
		int liLongitudReferencia = 16;// se obtiene de la tabla referencia_enc
		int registrosImportados = 0;
		int secuencia = 0;
		int resInsert = 0;
		int registrosLeidos = 0;
		int i = 0;
		int iContador=0;

		double pdImporte = 0;

		//boolean lee_banamex = false;
		boolean verificarChequera;
		boolean bMassPayment = false;
		boolean bImportar;
		boolean verificarDatos;

		String cadena = "";
		String fecValor = "";
		String valorChequera = "";
		String cheqInexistentes = "";
		String psSbCobro = "";
		String psCargoAbono = "";
		String psReferencia = "";
		String psConcepto = "";
		String psDescripcion = "";
		String psSucursal = "";
		String psObservacion = "";
		String psFolioBanco = "";
		String psBanamex = "";
		inicializar();
		mensajes.add("\nBanamex Version Internet");
		try {
			psBanamex = administradorArchivosDao.consultarConfiguraSet(201) + "\\banamex_internet\\";
			if (!psBanamex.equals(rutaActual))
				System.setProperty("user.dir", psBanamex);
			File[] nombres = busquedaArchivos.obtenerNombreArchivos(".txt");
			if(nombres!=null){
				nombres = busquedaArchivos.cambiarExtension(nombres, ".txt", ".tmp");
				CatCtaBancoDto catCtaBancoDto = new CatCtaBancoDto();
				MovtoBancaEDto movtoBancaEDto = new MovtoBancaEDto();
				// Obtener fecha valor
				for (i = 0; i < nombres.length; i++) {
					FileReader arch = new FileReader(nombres[i]);
					BufferedReader buf = new BufferedReader(arch);
					psArchivo=nombres[i].getName().replace(".tmp", ".txt");
					if (psArchivos.equals(""))
						psArchivos = psArchivo;
					else
						psArchivos += "', '" + psArchivo;
					if((cadena = buf.readLine())==null)
						break;
					cadena = cadena.replace("'", " ");
					if (cadena.length() == 6)
						fecValor = "" + cadena.substring(0, 2) + "/" + cadena.substring(2, 4) + "/20" + cadena.substring(4, 6);
					else
						fecValor = "" + cadena.substring(0, 2) + "/" + cadena.substring(2, 4) + cadena.substring(4, 8);
					if(funciones.isDate(fecValor, false)){
						if((cadena = buf.readLine())==null)
							break;
						cadena = cadena.replace("'", " ");
						renglon=2;
						if(cadena.length()!=24){
							movtoBancaEDto.setFecValor(administradorArchivosDao.cambiarFecha(fecValor));
							catCtaBancoDto.setIdBanco(BANAMEX);
							movtoBancaEDto.setFecAlta(administradorArchivosDao.obtenerFechaHoyBase());
							movtoBancaEDto.setArchivo(psArchivo);
							do{
								if(archivoOk)
									archivoOk=false;
								while ((cadena = buf.readLine()) != null) {
									cadena = cadena.replace("'", " ");
									iContador++;
									registrosLeidos=iContador;
									verificarDatos = false;
									renglon++;
									if (cadena.length() == 20)
										verificarChequera = true;
									else
										verificarChequera = false;
									if (cadena.charAt(0) == 'N' || verificarChequera) {
										if (verificarChequera)
											valorChequera = cadena.substring(0, 4)+ cadena.substring(13);
										else
											valorChequera = cadena.substring(41, 45)+ cadena.substring(54, 61);
										catCtaBancoDto.setIdChequera(valorChequera);
										/*
										 * Si la chequera tiene habilitada la bandera de
										 * MassPayment no subirla a movto_banca_e, ya que se
										 * importo en citibank_MN
										 */
										List<CatCtaBancoDto> datosConsulta = administradorArchivosDao.consultarEmpresa(catCtaBancoDto);
										if (datosConsulta.size() > 0) {
											if (datosConsulta.get(0).getPagoMass().equals("S"))
												bMassPayment = true;
											else
												bMassPayment = false;
											noEmpresa = datosConsulta.get(0).getNoEmpresa();
										} else {
											contInexistentes++;
											cheqInexistentes += valorChequera + ",";
											if(contInexistentes%3==0)
												cheqInexistentes+="\n";
											archivoOk=true;
											break;
										}
									}
									if ((cadena.substring(0, 1).equals("C")
											|| cadena.substring(0, 1).equals("A") || verificarChequera)
											&& noEmpresa > 0) {
										if (verificarChequera) {
											if ((cadena = buf.readLine()) != null)
												renglon++;
											psFolioBanco = "";
											psSbCobro = "";
											psCargoAbono = "S";
											psReferencia = "";
											psConcepto = "SALDO ULTIMA TRANS";
											pdImporte = Double.parseDouble(cadena.substring(63)) / 100;
											if (cadena.substring(62, 63).equals("-")) {
												pdImporte = pdImporte * -1;
											}
											verificarDatos = true;
										} else {
											psFolioBanco = cadena.substring(7, 17);
											if (cadena.substring(0, 1).equals("A")) {
												// 1009 n 1300 es deposito SBC
												if ((cadena.substring(1, 3) + cadena.substring(54, 56)).equals("1009")
														|| (cadena.substring(1, 3) + cadena.substring(54, 56)).equals("1300"))
													psSbCobro = "N";
												else
													psSbCobro = "S";
											} else
												psSbCobro = "";
											if (cadena.substring(0, 1).equals("A"))
												psCargoAbono = "I";
											else
												psCargoAbono = "E";
											if (lbReferenciaPorChequera) {
												psReferencia = (cadena.substring(7, 17)); // Referencia Numerica
											} else {
												if (psCargoAbono.equals("E"))
													psReferencia = cadena.substring(17,	17 + liLongitudReferencia).trim();
												else {
													psReferencia = cadena.substring(17, 17 + liLongitudReferencia).trim();
													if (lbReferenciaNumerica && psReferencia.indexOf("AUT.") == -1)
														psReferencia = cadena.substring(16 - liLongitudReferencia + 1, liLongitudReferencia);
												}
											}
											psConcepto = cadena.substring(1, 3)	+ cadena.subSequence(54, 56);
											if ((Double.parseDouble(psConcepto)) == 0){
												archivoOk = true;
												break;
											}
											pdImporte = (Double.parseDouble(cadena.substring(37, 54))) / 100;
											psDescripcion = cadena.substring(17, 27); // Referencia alfanumerica
											verificarDatos = true;
										}
									}
									// Solo subir los saldos de las chequeras de Mass Payment
									if (bMassPayment && !verificarChequera)
										if (Double.parseDouble(psConcepto) != 5500
												|| Double.parseDouble(psConcepto) != 7500){
											archivoOk=true;
											break;
										}
									if (verificarDatos) {
										// Verifica si ya existe el registro en movto_banca_e
										bImportar = false;
										movtoBancaEDto.setFolioBanco(psFolioBanco);
										movtoBancaEDto.setConcepto(psConcepto);
										movtoBancaEDto.setReferencia(psReferencia);
										movtoBancaEDto.setNoEmpresa(noEmpresa);
										movtoBancaEDto.setIdChequera(valorChequera);
										movtoBancaEDto.setImporte(pdImporte);
										movtoBancaEDto.setDescripcion(psDescripcion);
										movtoBancaEDto.setNoLineaArch(renglon);
										int movi = administradorArchivosDao.consultarMovimiento(movtoBancaEDto);
										if (movi == 0)
											bImportar = true;
										/*
										 * verificar el la base si esta el registro Set
										 * rst_movto = gobjSQL.FunSQLSelect6(Val(ps_foliobanco),
										 * psConcepto, _ psReferencia, pi_noempresa, ps_Chequera _ ,
										 * ps_Fecha, pdImporte, psDescripcion,
										 * plContadorRenglon)
										 */
										if (bImportar) {
											/*
											 * sacamos el siguiente folio de la base de datos
											 * pl_folio = gobjVarGlobal.Folio_Real("secuencia")
											 * If pl_folio <= 0 Then Exit Function End If
											 */
										//	lee_banamex = true;
											if (psCargoAbono == "S")
												psSucursal = valorChequera.substring(0, 4);
											else
												psSucursal = cadena.substring(3, 7);
				
											movtoBancaEDto.setBSalvoBuenCobro(psSbCobro);
											movtoBancaEDto.setIdBanco(BANAMEX);
											movtoBancaEDto.setCargoAbono(psCargoAbono);
											psObservacion = "";
											movtoBancaEDto.setObservacion(psObservacion);
											movtoBancaEDto.setSucursal(psSucursal);
											secuencia = seleccionarFolioReal("secuencia");
											if (secuencia <= 0){
												for (int h = i; h < nombres.length; h++)
													busquedaArchivos.cambiarExtensionSoloUno(nombres[h], ".tmp",	".txt");
												return false;
											}
											movtoBancaEDto.setSecuencia(secuencia);
											movtoBancaEDto.setUsuarioAlta(idUsuario);
											/*
											 * Call
											 * gobjSQL.FunSQLInserta_movto_banca_e(pi_noempresa,
											 * BANAMEX, _ ps_Chequera, pl_folio, ps_Fecha, _
											 * psSucursal, ps_foliobanco, psReferencia, _
											 * psCargoAbono, pdImporte, ps_SBCobro, _
											 * psConcepto, GI_USUARIO, psDescripcion, _
											 * ps_Archivo, Format(GT_FECHA_HOY, "dd/mm/yyyy"), _
											 * psObservacion, "", 0, "", plContadorRenglon)
											 */
											resInsert = administradorArchivosDao.insertaMovimientoBanca(movtoBancaEDto);
											if (resInsert >0)
												registrosImportados++;
										}
									}
								}
							}while(archivoOk);
							dto.setPlRegLeidos(registrosLeidos);
							dto.setPlRegImportados(registrosImportados);
						}
					}
					else{
						mensajesUsuario.add("Banamex; El archivo: " + psArchivo + " no tiene el formato vnlido de Banamex");
					}
					buf.close();
					arch.close();
					busquedaArchivos.cambiarExtensionSoloUno(nombres[i], ".tmp",".old");
				}
				dto.setPsArchivos(psArchivos);
				mensajes.add("Registros Leidos: " + dto.getPlRegLeidos());
				mensajes.add("Inexistentes: " + contInexistentes);
				mensajes.add(cheqInexistentes);
				mensajes.add("Registros Importados: " + dto.getPlRegImportados());
			}
			else{
				mensajes.add("Error de Lectura");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.setProperty("user.dir", rutaActual);
		return true;
	}

	public boolean leerCitibankDls(ParametroLayoutDto dto){
		boolean sigueLeyendo = false;
		boolean siguienteChequera=false;
		int piBanco;
		int secuencia = 0;
		int plRegImportados = 0;
		String psCitibank = "";
		String psFolioBanco = "";
		String psIdentificador = "";
		String psAuxiliar = "";
		String psDato = "";
		String psDatoFecha = "";
		String psSigno = "";
		inicializar();
		SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yy");

		
		psCitibank = administradorArchivosDao.consultarConfiguraSet(201)+"\\citibank_mn\\";
		if(!psCitibank.equals(rutaActual))
			System.setProperty("user.dir", psCitibank);
		File[] nombres = busquedaArchivos.obtenerNombreArchivos(".txt");
		if(nombres!=null){
			mensajes.add("\n CITIBANK DLS");		
			CatCtaBancoDto catCtaBancoDto = new CatCtaBancoDto();
			MovtoBancaEDto movtoBancaEDto = new MovtoBancaEDto();
			catCtaBancoDto.setIdBanco(2);
			movtoBancaEDto.setFecAlta(administradorArchivosDao.obtenerFechaHoyBase());
			movtoBancaEDto.setUsuarioAlta(idUsuario);
			while(z<nombres.length){
				nombres = busquedaArchivos.cambiarExtension(nombres, ".txt", ".tmp");
				try {
					archivo = new Scanner(nombres[z]);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				psArchivo=nombres[z].getName().replace(".tmp", ".txt");
				if (psArchivos.equals(""))
					psArchivos = psArchivo;
				else
					psArchivos += "', '" + psArchivo;
				movtoBancaEDto.setArchivo(psArchivo);
				piEncontrados = 0;
				do{
					if(sigueLeyendo)
						sigueLeyendo=false;
					while(archivo.hasNext()){
						while(archivo.hasNext()){
	                        psRegistro=archivo.nextLine();
	                        if(psRegistro.indexOf(":",1) > 0){
	                            psIdentificador = psRegistro.substring(1, psRegistro.indexOf(":",1));
	                            psAuxiliar = psRegistro.substring(psRegistro.indexOf(":",1) + 1);
	                            if(psIdentificador.equals("25")){
	                                psChequera = psAuxiliar.trim();
	                                break;
	                            }
	                        }
						}
						if(!archivo.hasNext())
							break;
						do{
							if(siguienteChequera)
								siguienteChequera=false;
							catCtaBancoDto.setIdChequera(psChequera);
							List<CatCtaBancoDto> empresa = administradorArchivosDao.consultarEmpresa(catCtaBancoDto);
							if(empresa.size()>0){
								piNoEmpresa = empresa.get(0).getNoEmpresa();
		                        piBanco = empresa.get(0).getIdBanco();
		                        bExisteChequera = true;
							}
		                    else{
		                        plRegSinChequera++;
		                        if (psChequerasInexistentes==null||psChequerasInexistentes.equals(""))
		                        	psChequerasInexistentes=psChequera;
								else
									psChequerasInexistentes += ", " + psChequera;
		                        bExisteChequera = false;
		                        sigueLeyendo=true;
		                        break;
							}
							while(archivo.hasNext()){
								psRegistro=archivo.nextLine();
		                        if(psRegistro.length() > 2 && psRegistro.indexOf(":",1)> 0)
		                            psIdentificador = psRegistro.substring(1, psRegistro.indexOf(":",1));
		                        else
		                            psIdentificador = "";
		                        psAuxiliar = psRegistro.substring(psRegistro.indexOf(":",1) + 1);
		                        if(psIdentificador.equals("25")){
		                            psChequera = psAuxiliar.trim();
		                            siguienteChequera=true;
		                            break;
		                        }
		                        if(psIdentificador.equals("61")){
		                        	iContador++;
									plRegLeidos++;
									psDato = psAuxiliar.substring(0, 6); // 6 caracteres son de la fecha
									psFecha = psDato.substring(4, 6) + "/"	+ psDato.substring(2, 4) + "/20" + psDato.substring(0, 2);
									movtoBancaEDto.setFecValor(administradorArchivosDao.cambiarFecha(psFecha));
									psAuxiliar = psAuxiliar.substring(10); // truncar la cadena para ir analizando el resto
									psDato = psAuxiliar.substring(0, 1);
									if (psDato.equals("R")) {
										psDato = psAuxiliar.substring(0, 2);
										psAuxiliar = psAuxiliar.substring(3); // hasta donde empieza el importe
										if (psDato.equals("RC")) // Cancelacion de Cargo
											psCargoAbono = "I";
										else if (psDato.equals("RD")) // Devolucion de deposito
											psCargoAbono = "E";
									} 
									else {
										psAuxiliar = psAuxiliar.substring(2); // hasta donde empieza el importe
										if (psDato.equals("C")) // C--> Crndito
											psCargoAbono = "I";
										else// D--> Debito								
											psCargoAbono = "E";
									}
	                             // Buscar el importe
	                             // Mid(psAuxiliar,1, InStr(1, psAuxiliar, ",")+ 2)
									//Checolast
	    							psDato = psAuxiliar.substring(0, psAuxiliar.indexOf(",") + 3);
	    							psDato = psDato.replace(",", ".");
	    							pdImporte = Double.parseDouble(psDato);
	    							// Buscar la referencia
	    							// Mid(psAuxiliar, InStr(1, psAuxiliar, ",") + 7)
	    							psAuxiliar = psAuxiliar.substring(psAuxiliar.indexOf(",", 0) + 7);
	    							// Mid(psAuxiliar, 1, InStr(1, psAuxiliar, "//") - 1))
	    							psDato = psAuxiliar.substring(0, psAuxiliar.indexOf("//", 0));
	    							psReferencia = psDato;
	    							// Buscar el Folio Banco
	    							// Mid(psAuxiliar,InStr(1,psAuxiliar, "//")+ 2)
	    							psAuxiliar = psAuxiliar.substring(psAuxiliar.indexOf("//", 0) + 2); 
	    							psFolioBanco = psAuxiliar.replace(" ", "");
	    							if (psFolioBanco.length() > 15)
	    								psFolioBanco = psFolioBanco.substring(0, 15);
	                                // 'Buscar el concepto (siguente lines
	                                if(!archivo.hasNext())
	                                   	break;
	                                psRegistro =archivo.nextLine();
	                                psAuxiliar = psRegistro;
	                                //psAuxiliar = psAuxiliar.substring(psAuxiliar.indexOf("/",1) + 1);
	                                //psAuxiliar = psAuxiliar.substring(psAuxiliar.indexOf("/") + 1);
	                                //psConcepto = psAuxiliar.trim();
	                                psConcepto = psAuxiliar.substring(4, 7);
	                                sDescripcion = psConcepto;
	                                psSBCobro="";
	                                //'checa si ya existe el movimiento
	                                /*
	    							 * Set rst_movto =
	    							 * gobjSQL.FunSQLSelectMovto_banca_e(ps_folio_banco,
	    							 * psConcepto, _ psReferencia, pi_noempresa, _
	    							 * ps_Chequera, ps_Fecha, _ pdImporte)
	    							 */
	                                movtoBancaEDto.setFolioBanco(psFolioBanco);
	                                movtoBancaEDto.setConcepto(psConcepto);
	                                movtoBancaEDto.setReferencia(psReferencia);
	                                movtoBancaEDto.setNoEmpresa(piNoEmpresa);
	                                movtoBancaEDto.setIdChequera(psChequera);
	                                movtoBancaEDto.setIdBanco(piBanco);
	    							movtoBancaEDto.setImporte(pdImporte);
	    							List<MovtoBancaEDto> consultarMov = administradorArchivosDao.selecionarMovtoBancaE(movtoBancaEDto);
	    							if (consultarMov.size() == 0) {
	    								// sacamos el siguiente folio de la base de
	    								// datos
	    								secuencia = seleccionarFolioReal("secuencia");
	    								if (secuencia <= 0)
	    									break;
	    								movtoBancaEDto.setSecuencia(secuencia);
	    								// LEE_CITIBANK_DLS_SWIFT_MT940 = True
	    								sSucursal = "";
	    								movtoBancaEDto.setSucursal(sSucursal);
	    								if (sDescripcion.length() > 60)
	    									sDescripcion = sDescripcion.substring(0, 60).trim();
	    								movtoBancaEDto.setDescripcion(sDescripcion);
	    								//int res = administradorArchivosDao.insertaMovimientoBanca(movtoBancaEDto);
	    								insercion = new ParametroLayoutDto();
										insercion.setPlNoEmpresa(piNoEmpresa);
										insercion.setPiBanco(piBanco);
										insercion.setPsIdChequera(psChequera);
										insercion.setPsSecuencia(secuencia);
										insercion.setPsFecValor(psFecha);
										insercion.setPsSucursal(sSucursal);
										insercion.setPsFolioBanco("" + psFolioBanco);
										insercion.setPsReferencia(psReferencia);
										insercion.setPsCargoAbono(psCargoAbono);
										insercion.setPdImporte(pdImporte);
										insercion.setPsBSalvoBuenCobro(psSBCobro);
										insercion.setPsConcepto(psConcepto);
										insercion.setPsUsuarioAlta(idUsuario);
										insercion.setPsDescripcion(sDescripcion==null?"":sDescripcion);
										insercion.setPsArchivo(psArchivo);
										insercion.setPsFecAlta(funciones.ponerFechaSola(obtenerFechaHoy()));
										insercion.setPsObservacion("");
										insercion.setPsCveConcepto("");
										insercion.setPdSaldoBancario(dSaldoBanco);
										insercion.setPlNoLineaArchivo(iContador);	
	    								int res = administradorArchivosDao.insertarMovtoBancaE(insercion);
	    								insercion=null;
	    								if (res > 0)
	    									plRegImportados++;
	    								/*
	    								 * Call
	    								 * gobjSQL.FunSQLInserta_movto_banca_e(pi_noempresa,
	    								 * pi_banco, _ ps_Chequera, pl_folio, ps_Fecha, _
	    								 * "", ps_folio_banco, psReferencia, _
	    								 * psCargo_Abono, pdImporte, ps_SBCobro, _
	    								 * psConcepto, GI_USUARIO, sDescripcion, _
	    								 * ps_Archivo, Format(GT_FECHA_HOY,
	    								 * "dd/mm/yyyy"))
	    								 * 
	    								 * 'Actualizar el saldo en chequeras
	    								 *  ' pdSaldo_banco =
	    								 * sacar_dato_comerica_history(ps_registro, 4) '
	    								 * Call actualizar_saldo_chequera(pdSaldo_banco, _ '
	    								 * pi_noempresa, pi_banco, ps_chequera)
	    								 */
	    							} else 
	    								piEncontrados++;
	    						}
		                        if (psIdentificador.equals("62F")) {
									iContador++;
									plRegLeidos++;
									psDato = psAuxiliar.substring(0, 1).trim();
									psAuxiliar = psAuxiliar.substring(1); // hasta donde empieza el importe
									if (psDato.equals("C")){ // C--> Crndito
										psCargoAbono = "S";
										psSigno = "+";
									} 
									else if (psDato.equals("D")){ // D--> Debito
										psCargoAbono = "S";
										psSigno = "-";
									}
									psDatoFecha = psAuxiliar.substring(0, 6); // '6 caracteres son de la fecha
									psFecha = psDatoFecha.substring(4, 6) + "/"
											+ psDatoFecha.subSequence(2, 4) + "/"
											+ psDatoFecha.substring(0, 2);
									psAuxiliar = psAuxiliar.substring(9); // truncar la cadena para ir analizando el resto
									// Buscar el importe
									// 1, InStr(1, psAuxiliar, ",") + 2)
									psDato = psAuxiliar.substring(0, psAuxiliar.indexOf(",", 0) + 3);
									if (psSigno.equals("-"))
										psDato = psSigno + psDato.replace(",", ".");
									else
										psDato = psDato.replace(",", ".");
									pdImporte = Double.parseDouble(psDato);
									psAuxiliar = psRegistro;
									psAuxiliar = psAuxiliar.substring(psAuxiliar.indexOf("/", 1) + 1);
									// InStr(2, psAuxiliar, "/") + 1)
									psAuxiliar = psAuxiliar.substring(psAuxiliar.indexOf("/", 0) + 1); 
									// InStr(1, psAuxiliar, "/")+ 1)
									psConcepto = "SALDO FINAL";
									sDescripcion = psConcepto;
									psFolioBanco = "";
									psReferencia = "";
									// checa si ya existe el movimiento
									/*
									 * Set rst_movto = New ADODB.Recordset Set rst_movto =
									 * gobjSQL.FunSQLSelectMovto_banca_e(ps_folio_banco,
									 * psConcepto, _ psReferencia, pi_noempresa, _
									 * ps_Chequera, ps_Fecha, _ pdImporte)
									 */
									movtoBancaEDto.setFolioBanco(psFolioBanco);
	                                movtoBancaEDto.setConcepto(psConcepto);
	                                movtoBancaEDto.setReferencia(psReferencia);
	                                movtoBancaEDto.setNoEmpresa(piNoEmpresa);
	                                movtoBancaEDto.setIdChequera(psChequera);
	                                movtoBancaEDto.setIdBanco(piBanco);
	    							movtoBancaEDto.setImporte(pdImporte);
	    							try{
	    								if(psFecha!=null && !psFecha.equals(""))
	    									movtoBancaEDto.setFecValor(formatoFecha.parse(psFecha));
	    								else
	    									movtoBancaEDto.setFecValor(Calendar.getInstance().getTime());
	    							}
	    							catch(Exception e){
	    								e.printStackTrace();
	    							}
									List<MovtoBancaEDto> consultarMovto = administradorArchivosDao.selecionarMovtoBancaE(movtoBancaEDto);
									if (consultarMovto.size() == 0) {
										// sacamos el siguiente folio de la base de
										// datos
										secuencia = seleccionarFolioReal("secuencia");
										if (secuencia <= 0)
											break;
										movtoBancaEDto.setSecuencia(secuencia);
										// LEE_CITIBANK_DLS_SWIFT_MT940 = True
										sSucursal = "";
										movtoBancaEDto.setSucursal(sSucursal);
										if (sDescripcion.length() > 60)
											sDescripcion = sDescripcion.substring(0, 60);
										movtoBancaEDto.setDescripcion(sDescripcion);
										insercion = new ParametroLayoutDto();
										insercion.setPlNoEmpresa(piNoEmpresa);
										insercion.setPiBanco(piBanco);
										insercion.setPsIdChequera(psChequera);
										insercion.setPsSecuencia(secuencia);
										insercion.setPsFecValor(psFecha);
										insercion.setPsSucursal(sSucursal);
										insercion.setPsFolioBanco("" + psFolioBanco);
										insercion.setPsReferencia(psReferencia);
										insercion.setPsCargoAbono(psCargoAbono);
										insercion.setPdImporte(pdImporte);
										insercion.setPsBSalvoBuenCobro("N"); //psSBCobro
										insercion.setPsConcepto(psConcepto);
										insercion.setPsUsuarioAlta(idUsuario);
										insercion.setPsDescripcion(sDescripcion==null?"":sDescripcion);
										insercion.setPsArchivo(psArchivo);
										insercion.setPsFecAlta(funciones.ponerFechaSola(obtenerFechaHoy()));
										insercion.setPsObservacion("");
										insercion.setPsCveConcepto("");
										insercion.setPdSaldoBancario(dSaldoBanco);
										insercion.setPlNoLineaArchivo(iContador);	
	    								int res = administradorArchivosDao.insertarMovtoBancaE(insercion);
	    								insercion=null;
										//int res = administradorArchivosDao.insertaMovimientoBanca(movtoBancaEDto);
										if (res>0) 
											plRegImportados++;
										/*
										 * Call
										 * gobjSQL.FunSQLInserta_movto_banca_e(pi_noempresa,
										 * pi_banco, _ ps_Chequera, pl_folio, ps_Fecha, _
										 * "", ps_folio_banco, psReferencia, _
										 * psCargo_Abono, pdImporte, ps_SBCobro, _
										 * psConcepto, GI_USUARIO, sDescripcion, _
										 * ps_Archivo, Format(GT_FECHA_HOY,
										 * "dd/mm/yyyy")) plReg_importados =
										 * plReg_importados + 1 'Actualizar el saldo en
										 * chequeras
										 *  ' pdSaldo_banco =
										 * sacar_dato_comerica_history(ps_registro, 4) '
										 * Call actualizar_saldo_chequera(pdSaldo_banco, _ '
										 * pi_noempresa, pi_banco, ps_chequera)
										 */
									} else 
										piEncontrados++;
		                        }
							}
						}while(siguienteChequera);
					}
				}while(sigueLeyendo);
				dto.setPsArchivos(psArchivos);
				dto.setPlRegLeidos(plRegLeidos);
				dto.setPlRegSinChequera(plRegSinChequera);
				dto.setPsChequerasInexistentes(psChequerasInexistentes);
				dto.setPlRegImportados(plRegImportados);
				archivo.close();
				archivo=null;
				busquedaArchivos.cambiarExtensionSoloUno(nombres[z], ".tmp",".old");
				z++;
			}
		}
		mensajes.add("Registros Leidos:  " + dto.getPlRegLeidos());
		mensajes.add("Registros sin chequera: " + dto.getPlRegSinChequera());
		mensajes.add(dto.getPsChequerasInexistentes());
		mensajes.add("Registros Importados:  " + dto.getPlRegImportados());
		return true;
	}
	
	@SuppressWarnings("unused")
	public boolean leerBancomer(ParametroLayoutDto dto){
		//boolean siguiente =false;
		boolean pbLayoutMismoDia=false;
        boolean pbLayoutCIEDiario=false;
        //boolean sigueLeyendo1=false;
        boolean leeBancomer=true;
        boolean bTerminoLeerArchivo=false;
		int plRegImportados=0;
		int linea = 0;
		//long psFolioBanco=0;
		double pdSaldo=0;
		double pdSaldoChequera=0;
		String psFolioBanco = "";
		String psInserts="";
		String psTipoMovto="";
		String psManejaMismoBanco="";
		String psFecha1="";
		String psDescripcion="";
		String psBSaldoBanco="R";
		String psBancomer="";
		inicializar();
		psChequerasInexistentes = "";
		
		int numChequeras = 0;
		int numCheqProce = 0;
		
		double pdCargo = 0;
		double pdAbono = 0;
		
		//logger.info(funciones.ponerFechaSola(administradorArchivosDao.obtenerFechaHoy()));
		psBancomer = administradorArchivosDao.consultarConfiguraSet(201)+"\\bancomer\\";
		if(!psBancomer.equals(rutaActual))
			System.setProperty("user.dir", psBancomer);
		File[] nombres = busquedaArchivos.obtenerNombreArchivos(".txt");
		logger.info("archivos:" + nombres.length);
		mensajes.add("\nBancomer");
		
		if(nombres!=null){
			while(z<nombres.length){  // hasta que no encuentres archivos
				bTerminoLeerArchivo = false;
				bExisteChequera = true;
				nombres = busquedaArchivos.cambiarExtension(nombres, ".txt", ".tmp");
				psManejaMismoBanco = administradorArchivosDao.consultarConfiguraSet(352);
				try {
					archivo = new Scanner(nombres[z]);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				psArchivo=nombres[z].getName().replace(".tmp", ".txt");
				if (psArchivos.equals(""))
					psArchivos = psArchivo;
				else
					psArchivos += "', '" + psArchivo;
				psChequera = "";
	            iContador = 0;
	            archivoOk = true;
	            i = 0;
                piEncontrados = 0;
                psInserts = "";

                // BB: continuar:
				while(archivo.hasNext()) {//hasNext // hasta que no haya lineas que leer
					logger.info(z+".- Archivo: "+psArchivo);
					logger.info(" Chequera: "+(psChequera==null?"No hay Chequera":psChequera));
					logger.info(" Chequera Nueva: "+(psChequeraNueva==null?"":psChequeraNueva));
					logger.info(" No Empresa: "+piNoEmpresa);
					i++;
                    psRegistro = archivo.nextLine();
                    linea ++;
                    psRegistro = psRegistro.replace("'", " ");
                    logger.info("Registro: "+psRegistro+"\n*********************");
                    //'Por default el lay out es Estado de cuenta cash
                    pbLayoutMismoDia = false;
                    pbLayoutCIEDiario = false; //'Siempre permanece en falso
                    psTipoMovto = "";
                    
                    if(psRegistro.length() == 105)	//'Para archivos MD
                    {
                    	if(Long.parseLong(psRegistro.substring(73, 89)) > 0)
	                        psTipoMovto = "1";
	                    else
	                    	psTipoMovto = "0";
	                    //'IALA 03/08/2007
	                    //'Se agrego la variable ps_hora para en ella guardar la hora del archivo
	                    //'y que esta hora se valla al campo fec_valor de la tabla movto_banca_e
	                    //'para poder identificar aprox. a que hora llegaron para el fideicomiso
	                    psHora = psArchivo.substring(2, 4) + ":" + psArchivo.substring(4, 6) + ":" + psArchivo.substring(6, 8);
	                    psFecha = psRegistro.substring(26, 28) + "/" + psRegistro.substring(23, 25) + "/" + psRegistro.substring(18, 22);
	                    psChequeraNueva = psRegistro.substring(5, 18);
	                    if(psChequeraNueva.substring(0, 3).equals("910"))  // 'Cuentas azules
	                        psChequeraNueva = funciones.ponerCeros(psRegistro.substring(5, 18).trim(),13);
	                    else
	                        psChequeraNueva = funciones.ponerCeros(psRegistro.substring(10, 18),11);   //'sin considerar moneda
	                    pdSaldo = 0;
                    }
                    if(psArchivo.substring(0, 2).trim().equals("MM") && psManejaMismoBanco.equals("SI"))
                    {
                    	int op=-1;
                    	if(funciones.isNumeric(psRegistro.substring(0, 2)))
                    		op=Integer.parseInt(psRegistro.substring(0, 2));
                    	switch(op){	                   
	                    	case 11:
	                    		ParametroBancomerDto bEmpresa = new ParametroBancomerDto();
	                    		psChequeraNueva = psRegistro.substring(2, 20);
	                    		bEmpresa.setIdBanco(BANCOMER); 
	                    		bEmpresa.setIdChequera(psChequeraNueva);
	                    		bEmpresa.setPbSwiftMT940MN(true);
	                    		bEmpresa.setMovDiarios(true);
	                    		List<ComunDto> empresas = administradorArchivosDao.buscarEmpresa(bEmpresa);
	                    		
		                        if(empresas!=null && empresas.size()>0)
		                        {
		                            piNoEmpresa = empresas.get(0).getIdEmpresa();
		                            psChequera = empresas.get(0).getIdChequera();
		                            // BB: GOTO bandera
		                            continue;
		                        }
		                        else{
		                            plRegSinChequera++;
		                            if (psChequerasInexistentes==null)
			                        	psChequerasInexistentes="";

									psChequerasInexistentes += (!psChequerasInexistentes.equals("") ? ", " : "") + psChequeraNueva;
		                            while(archivo.hasNext()){ //hasta la siguiente chequera
		                                psRegistro=archivo.nextLine();
		                                psRegistro = psRegistro.replace("'", " ");
		                                if(psRegistro.substring(0, 2).equals("33"))
		                                    break;
		                            }
		                            
		                            // ya no leer mas lineas
		                            bTerminoLeerArchivo = true;
		                        }
		                        
		                        if(!bTerminoLeerArchivo)
		                        	psFecha = funciones.ponerFechaSola(administradorArchivosDao.obtenerFechaHoy());
		                        
		                    break;

	                    	case 22:
	                        //'******************** ARCHIVO MM DONDE SE MANEJAN LOS CONCEPTOS **************
		                        psHora = psArchivo.substring(2, 4) + ":" + psArchivo.substring(4, 6) + ":" + psArchivo.substring(6, 8);
		                        psTipoMovto = psRegistro.substring(88, 89);
		                        //' se cambia la fecha ya que en el MM vienen movimentos con fechas posteriores a hoy
		                        //' estos movimientos vienen despues en el SH pero en el dia que indica esa fecha y trae la fecha
		                        //' en que se recibio.
		                        psFecha1 = psRegistro.substring(128, 130) + "/" + psRegistro.substring(131, 133) + "/20" + psRegistro.substring(134, 136);
		                        psFecha = psRegistro.substring(144, 146) + "/" + psRegistro.substring(147, 149) + "/20" + psRegistro.substring(150, 152);
		                        if(!funciones.isDate(psFecha,false) || !funciones.isDate(psFecha1,false)){
		                            // BB: GOTO bandera
		                            continue; // continuar con la sig linea
		                        }
		                        if(!psFecha1.equals(funciones.ponerFechaSola(administradorArchivosDao.obtenerFechaHoy()))){
		                            //MsgBox "La fecha del Archivo: " & ps_Archivo & " es diferente a hoy", vbInformation, "SET"
		                        	bTerminoLeerArchivo = true;
		                            break;
		                        }
		                        if(psTipoMovto.equals("1")){
		                            psCargoAbono = "E";
		                            psSBCobro = "";
		                        }
		                        else if(psTipoMovto.equals("0")){
		                            psCargoAbono = "I";
		                            if(psRegistro.substring(26, 51).trim().equals("DEP S B COBRO"))
		                                psSBCobro = "N";
		                            else
		                                psSBCobro = "S";
		                        }
		                        //validar los demas campos es necesario tener el layout correcto se tiene una version que no cuadra
		                        psConcepto = psRegistro.substring(26, 51).trim(); //Concepto
		                        pdImporte = Math.abs(Double.parseDouble(psRegistro.substring(89, 105)));
		                        psSucursal = psRegistro.substring(2, 6); //'sucursal
		                        psCveConcepto = psRegistro.substring(121, 124).trim();
		                        psFolioBanco = (psRegistro.substring(14, 21));
		                        psDescripcion = (psRegistro.substring(51, 88));//funciones.elmiminarEspaciosAlaDerecha
		                        //se van a cambiar las lecturas por el concepto es mas seguro
		                        //MS 04/06/2009
		                        if(psCveConcepto.equals("C72")){ //Mid(psDescripcion, 1, 8) = "DEM REF:"
		                            psReferencia = psRegistro.substring(59, 79).trim();
		                            psFolioBanco = (psDescripcion.substring(psDescripcion.length()- 5));
		                        }
		                        else if(psConcepto.equals("DEPOSITO DE TERCERO")){
		                            psFolioBanco = (psRegistro.substring(12,18));
		                            if(psDescripcion.substring(0, 7).equals("0070005"))
		                                psReferencia = psRegistro.substring(51, 58).trim();
		                            else
		                                psReferencia = psRegistro.substring(51, 81).trim();
		                        }
		                        else if(psConcepto.substring(0, 19).equals("TRASPASO A TERCEROS")){
		                            psReferencia = psRegistro.substring(51, 81).trim();
		                            psFolioBanco = (psRegistro.substring(12, 18));
		                        }
		                        else if(psDescripcion.substring(0, 7).equals("0070005")){
		                            psReferencia = psRegistro.substring(51, 58).trim();
		                            psFolioBanco = (psRegistro.substring(15, 21));
		                        }
		                        else if(psDescripcion.substring(7, 17).equals("TEF AMEXCO")){
		                            psReferencia = psRegistro.substring(58, 88).trim();
		                            psFolioBanco = (psRegistro.substring(15, 21));
		                        }
		                        else if(psConcepto.substring(0, 13).equals("CHEQUE PAGADO")){
		                            psFolioBanco = (psRegistro.substring(11, 20));
		                            psReferencia = ""+psFolioBanco;
		                        }
		                        else if(psConcepto.substring(0, 12).equals("SPEI ENVIADO")){
		                            psReferencia = psDescripcion.substring(1, 31).trim();
		                            if((psReferencia.length() % 2) == 0)
		                                psReferencia = psReferencia.substring(0, psReferencia.length() / 2);
		                        }
		                        else if(psConcepto.substring(0, 2).equals("CB") || psConcepto.substring(0, 2).equals("CC") || psConcepto.substring(0, 2).equals("CE")
		                        		|| psConcepto.substring(0, 2).equals("CV") || psCveConcepto.equals("Y07")){
		                            psReferencia = psConcepto.substring(3).trim();
		                            psFolioBanco = (psRegistro.substring(11, 17));
		                        }
		                        else if(psConcepto.substring(0, 12).equals("TEF RECIBIDO")){
		                            psReferencia = psRegistro.substring(51, 81).trim();
		                            psFolioBanco = (psRegistro.substring(15, 22));
		                        }
		                        else if(psConcepto.substring(0, 13).equals("SPEI RECIBIDO")){
		                            psReferencia = psRegistro.substring(51, 81).trim();
		                            psFolioBanco = (psRegistro.substring(15, 21));
		                        }
		                        else if(psConcepto.trim().equals("PAGO DE SERVICIOS")){
		                            psReferencia = psRegistro.substring(51, 81).trim();
		                            psFolioBanco = "0";
		                        }
		                        else if(psConcepto.trim().equals("IVA COMISION TARJETAS"))
		                            psReferencia = psRegistro.substring(51, 81).trim();
		                        else if(psConcepto.trim().equals("COMISION TARJETAS"))
		                            psReferencia = psRegistro.substring(51, 81).trim();
		                        else if(psConcepto.trim().equals("FALTANTE DE EFECTIVO")){
		                            psReferencia = psRegistro.substring(51, 81).trim();
		                            psFolioBanco = "0";
		                        }
		                        else if(psConcepto.trim().equals("DEPOSITO CON DOCUMENTOS"))
		                            psReferencia = psRegistro.substring(51, 81).trim();
		                        else if(psConcepto.trim().equals("PAGO CUENTA DE TERCERO")){
		                            psReferencia = psRegistro.substring(51, 81).trim();
		                            psFolioBanco = "0";
		                        }
		                        else if(psCveConcepto.equals("C13")){ //"DEVOLUCION DE CHEQUE"
		                            psConcepto = "DEVOL.CHQ";
		                            psFolioBanco = (psRegistro.substring(12, 18));
		                        }
		                        else if(psCveConcepto.equals("C14")) //' = "COM. DEVOLUCION CHEQUE"
		                            psFolioBanco = (psRegistro.substring(12, 18));
		                        else if(psCveConcepto.equals("C15")) //' Trim(psConcepto) = "IVA COMISION DEVOLUCION"
		                            psFolioBanco = (psRegistro.substring(12, 18));
		                        else if(psConcepto.trim().equals("PAGO DE NOMINA"))
		                            psFolioBanco = "0";
		                        else if(psConcepto.trim().equals("CHEQUE DEVUELTO BANCOMER") || psCveConcepto.equals("B01")) //' MS 08/06/2009
		                            psFolioBanco = (psRegistro.substring(11, 17));
		                        else if(psCveConcepto.equals("C02") && psConcepto.trim().equals("DEPOSITO EN EFECTIVO")){
		                            psConcepto = "DEPOSITO EFECTIVO/CHEQUE";
		                            psFolioBanco = (psRegistro.substring(11, 17));
		                            if(psFolioBanco.equals("0")){
		                                psConcepto = "DEPOSITO EFECTIVO/CHEQUE";
		                                psFolioBanco = (psRegistro.substring(4, 10));
		                            }
		                        }
		                        else if(psCveConcepto.equals("C02")){
		                            psConcepto = "DEPOSITO EFECTIVO/CHEQUE";
		                            psFolioBanco = (psRegistro.substring(4, 10));
		                        }
		                        else if(psConcepto.trim().equals("ORDEN DE PAGO EXTRANJERO") || psCveConcepto.equals("E01")) //' MS 08/06/2009
		                            psFolioBanco = (psRegistro.substring(11, 17));
		                        else if(psConcepto.trim().equals("PAGO TARJETA DE CREDITO"))
		                            psFolioBanco = (psRegistro.substring(12, 18));
		                        else if(psConcepto.trim().equals("CERTIFICACION DE CHEQUE") || psConcepto.trim().equals("IVA COM. CERTIFICACION")
		                                || psConcepto.trim().equals("COMISION CERTIFICACION")){
		                            psFolioBanco = (psRegistro.substring(12, 19));
		                            psReferencia = ""+psFolioBanco;
		                        }
		                        else if(psCveConcepto.equals("C14")) //'COM DEVOLUCION CHEQUE ' MS 08/06/2009
		                            psFolioBanco = (psRegistro.substring(14, 18));
		                        else if(psCveConcepto.equals("C74") || psCveConcepto.equals("C73"))//'DEP CHEQUES OTROS BANCOS
		                            psFolioBanco = (psRegistro.substring(81, 87));
		                        else if(psCveConcepto.equals("E27")){ //' REMESAS EXTRANJERO
		                            psConcepto = "REMESAS EXTRANJ";
		                            psFolioBanco = (psRegistro.substring(11, 17));
		                        }
		                        else if(psCveConcepto.equals("X02")) //' IMSS INFONAVIT AFORE
		                            psFolioBanco = "0";
		                        else if(psCveConcepto.equals("B06")) //' CORRECCION CHEQUE
		                            psFolioBanco = (psRegistro.substring(11, 17));
		                        //***********************************************************
		                        else if(psCveConcepto.equals("W47")){ //'COMISION BBVA CASH
		                            psFolioBanco = "0";
		                            psConcepto = "COMISION BBVA CASH";
		                        }
		                        else if(psCveConcepto.equals("W48")){ //'ECOC 09/07/09 ----> 15% IVA COM.NET CASH
		                            psFolioBanco = "0";
		                            psConcepto = "15% IVA COM.NET CASH";
		                        }
		                        else if(psCveConcepto.equals("W59")){ // 'ECOC 09/07/09 ----> COMISION BBVA CASH SERV
		                            psFolioBanco = "0";
		                            psConcepto = "COMISION BBVA CASH SERV";
		                        }
		                        else if(psCveConcepto.equals("W60")){ //'ECOC 09/07/09 ----> 15% IVA COM.BBVA CASH
		                            psFolioBanco = "0";
		                            psConcepto = "15% IVA COM.BBVA CASH";
		                        }
		                        else if(psCveConcepto.equals("W47")){ //'ECOC 10/09/09 ----> COMISION BBVA CASH TRANS
		                            psFolioBanco = "0";
		                            psConcepto = "COMISION BBVA CASH TRANS";
		                        }
		                        else if(psCveConcepto.equals("W66")){ //'ECOC 15/09/09 ----> COM. SERVICIOS T.I.B.
		                            psFolioBanco = "0";
		                            psConcepto = "COM. SERVICIOS T.I.B.";
		                        }
		                        else if(psCveConcepto.equals("W67")){ //'ECOC 15/09/09 ----> 15% IVA COM.SERV.T.I.B.
		                            psFolioBanco = "0";
		                            psConcepto = "15% IVA COM.SERV.T.I.B.";
		                        }
		                        else
		                             psReferencia = psRegistro.substring(51, 81).trim();
		                        pdSaldo = Double.parseDouble(psRegistro.substring(105, 121));
	                        break;
                    	}
                    	if(bTerminoLeerArchivo) break; // salte de leer archivo
                    	//if(bSwitch)
                    	//	break;
                    } else {    //'Para Archivos SH  0 - Abono, 1 - Cargo, 2 n Saldo, 3 n Saldo Negativo
                        psTipoMovto = psRegistro.substring(64, 65);
                        //'IALA 03/08/2007
                        psHora = "";
                        psFecha = psRegistro.substring(26, 28) + "/" + psRegistro.substring(23, 25) + "/" + psRegistro.substring(18, 22);
                        pdSaldo = 0;
                    }
                    if(psRegistro.length() == 105 && psManejaMismoBanco.equals("SI"))   //'Lay out del mismo dna
                        pbLayoutMismoDia = true;   //'inhabilitado Hasta nuevo aviso, ya le toco 05 sep 2006
                    else if(psRegistro.length() == 105)//'Lay out del mismo dna
                        pbLayoutCIEDiario = false;
                    
                    if(!funciones.isDate(psFecha, false)){
                        archivoOk = false;
                        break; // salte de leer archivo
                    }
                    //' si se trata de un movimiento diario solo puede subir si la fecha del movimiento es igual al dia de hoy y si no, no subir ese archivo
                    if(pbLayoutMismoDia && !psFecha.equals(funciones.ponerFechaSola(administradorArchivosDao.obtenerFechaHoy()))) break;
                    iContador++;
                    plRegLeidos++;
                    
                    if(!psArchivo.substring(0, 2).equals("MM")) {
                    	while(true) {
	                    	psChequeraNueva = psRegistro.substring(5, 18);									//Hay que leer la nueva chequera
                    		
	        				if(psChequeraNueva.substring(0, 3).equals("910"))   							//'Cuentas azules
	        					psChequeraNueva = funciones.ponerCeros(psRegistro.substring(5, 18), 13);
	        				else
	        					psChequeraNueva = funciones.ponerCeros(psRegistro.substring(10, 18), 11);   //'sin considerar moneda
	        				
	                    	if(!psChequera.equals(psChequeraNueva)) {
	                        	psChequera = psChequeraNueva;
	                        	numChequeras++;
	                        	ParametroBancomerDto bEmpresa = new ParametroBancomerDto();
	                    		bEmpresa.setIdBanco(BANCOMER);
	                    		bEmpresa.setIdChequera(psChequeraNueva);
	                    		List<ComunDto> empresas = administradorArchivosDao.buscarEmpresa(bEmpresa);
	                    		
	                    		if(empresas != null && empresas.size() > 0) {
	                    			piNoEmpresa = empresas.get(0).getIdEmpresa();
	                    			bExisteChequera = true;
		                    		// Para el procesos de GNP se coloca la condicion de que si la variable bExporta == "N" o bExporta == "", 
	                    			// no se carguen los registros de esa chequera
	                    			if(empresas.get(0).getBExporta().trim().equals("N") || empresas.get(0).getBExporta().trim().equals(""))
	                    				bExisteChequera = false;
	                    			else
	                    				numCheqProce++;
	                    		}else {
	                    			if (psChequerasInexistentes == null || psChequerasInexistentes.equals(""))
	                    				psChequerasInexistentes = psChequera;
	                    			else
	                    				psChequerasInexistentes += ", " + psChequeraNueva;
	                    			bExisteChequera = false;
	                            }
                    		}
	                    	if(!bExisteChequera) {
                				plRegSinChequera++;
                    			
                                if(archivo.hasNext()) {
                                	psRegistro = archivo.nextLine();
                                	psRegistro = psRegistro.replace("'", " ");
                                    iContador++;
                                    plRegLeidos++;
                                    i++;
                                    continue;
                                }
                                bTerminoLeerArchivo = true;
                			}
	                    	break;
                    	}
                    }
                    if(bTerminoLeerArchivo) break;
                    
                    if((psTipoMovto.equals("2") || psTipoMovto.equals("3")) && !pbLayoutMismoDia) { //'And pb_layout_CIE_diario = False Then
                    	//'Verificar que maneje actualizacinn de saldos
                        if(psBSaldoBanco != null && psBSaldoBanco.equals("R")) {
                            //'actualizar saldo de los movimientos insertados
                            if(psInserts != null && !psInserts.equals(""))
                            	administradorArchivosDao.ejecutaSentencia(psInserts);
                            psInserts = "";
                            pdSaldoChequera = Double.parseDouble(psRegistro.substring(65, 81));
                            
                            if(psTipoMovto.equals("3")) 
                            	pdSaldoChequera = pdSaldoChequera * -1;
                            //Call actualizar_saldo_chequera_bancomer(pd_saldo_chequera, _
                            //	ps_Archivo, 12, ps_Chequera)   'Falta probar
                            //actualizar saldo en cat_cta_banco
                            //Call actualizar_saldo_chequera(pd_saldo_chequera, _
                            	//pi_noempresa, 12, ps_Chequera)
                            leeBancomer = true;
                        }else {
                        	psMensaje = "Bancomer; n Bancomer no maneja actualizacinn de saldo en chequera !";
                            mensajesUsuario.add(psMensaje);
                            archivo.close();
                            busquedaArchivos.cambiarExtensionSoloUno(nombres[z], ".tmp",	".err");
                            for (int h = z+1; h < nombres.length; h++)
								busquedaArchivos.cambiarExtensionSoloUno(nombres[h], ".tmp",	".txt");
                            return false;
                        }
                    } // if((psTipoMovto.equals("2") || psTipoMovto.equals("3")) && !pbLayoutMismoDia)
                    
                    if(piNoEmpresa == 0) break; // salte de leer archivo
                    
                    if(psTipoMovto.equals("0") && !pbLayoutMismoDia) {// 'pendiente sbc
	                    if(psRegistro.substring(34, 74).trim().equals("DEP S B COBRO"))
	                        psSBCobro = "N";
	                    else
	                        psSBCobro = "S";
                    }else
                    	psSBCobro = "";
                        
                    if(!psArchivo.substring(0, 2).equals("MM")){
                        if(pbLayoutMismoDia){
                            psDescripcion = psRegistro.substring(43, 73).trim();
                            psReferencia = psRegistro.substring(28, 43).trim();
                            if(psTipoMovto.equals("1")){
                                pdImporte = Double.parseDouble(psRegistro.substring(73, 89));
                                pdCargo += pdImporte;
                                psCargoAbono = "E";
                                if(funciones.isNumeric(psReferencia))
                                    psReferencia = "" + psReferencia;
                            }
                            else{
                                pdImporte = Double.parseDouble(psRegistro.substring(89, 105));
                                pdAbono += pdImporte;
                                psCargoAbono = "I";
                            }
                            //'CHEQUE PAGADO N 0000153
                            //'CHEQUE PAGADO N CH-0000079
                            psSucursal = "";
                            if(psDescripcion.substring(0, 13).equals("CHEQUE PAGADO")){
                                //'IALA 02/08/2007
                                //'Se convierte a valor numerico la variable psFolio_banco
                                psFolioBanco = (funciones.cadenaRight(psDescripcion, 6));
                                if(psDescripcion.substring(0, 18).equals("CHEQUE PAGADO N CH"))
                                    psConcepto = "CHEQUE PAGADO N CH";
                                else if(psDescripcion.substring(0, 15).equals("CHEQUE PAGADO N"))
                                    psConcepto = "CHEQUE PAGADO N";
                                else
                                    psConcepto = "CHEQUE PAGADO";
                            }
                            else{
                                psFolioBanco = (psRegistro.substring(37, 43));
                                psConcepto = psDescripcion;
                                ParametroLayoutDto busquedaX = new ParametroLayoutDto();
                                busquedaX.setPiBanco(BANCOMER);
                                busquedaX.setPsDescripcion(psDescripcion);
                                busquedaX.setPsRegistro(psRegistro);
                                busquedaX.setPsCargoAbono(psCargoAbono);
                                busquedaX.setPsConcepto(psConcepto);
                                busquedaX.setPsFolioBanco(""+psFolioBanco);
                                //'Obtener el concepto ARMAR UN CATALOGO Y VER COMO QUITAR DE CODIGO
                                if(administradorArchivosDao.obtenerDescripcion(busquedaX)){
                                    //' La funcion anterior nos regresa el concepto y el folio banco
                                	psConcepto=busquedaX.getPsConcepto();
                                	psFolioBanco = (busquedaX.getPsFolioBanco());
                                }
                                else if(psDescripcion.substring(0, 12).equals("SPEI ENVIADO")){
                                    psConcepto = "SPEI ENVIADO";
                                    psFolioBanco = (psRegistro.substring(63, 69));
                                }
                                else if(psDescripcion.substring(0, 13).equals("SPEI RECIBIDO")){
                                    psConcepto = "SPEI RECIBIDO";
                                    psFolioBanco = (psRegistro.substring(63, 69));
                                }
                                else if(psDescripcion.substring(0, 13).equals("SPEI DEVUELTO")){
                                    psConcepto = "SPEI DEVUELTO";
                                    psFolioBanco = (psRegistro.substring(63, 69));
                                }
                                else if(psDescripcion.substring(0, 13).equals("COMISION SPEI")){
                                    psConcepto = "COMISION SPEI DEV";
                                    psFolioBanco = (psRegistro.substring(63, 69));
                                }
                                else if(psDescripcion.substring(0, 12).equals("IVA SPEI DEV")){
                                    psConcepto = "IVA SPEI DEV";
                                    psFolioBanco = (psRegistro.substring(63, 69));
                                }
                                else if(psDescripcion.substring(0, 11).equals("TEF ENVIADO")){
                                    psConcepto = "TEF ENVIADO";
                                    psFolioBanco = (psRegistro.substring(63, 69));
                                }
                                else if(psDescripcion.substring(0, 12).equals("TEF RECIBIDO")){
                                    psConcepto = "TEF RECIBIDO";
                                    psFolioBanco = (psRegistro.substring(63, 69));
                                }
                                else if(psDescripcion.substring(0, 12).equals("TEF DEVUELTO")){
                                    psConcepto = "TEF DEVUELTO";
                                    psFolioBanco = (psRegistro.substring(63,69));
                                }
                                else if(psDescripcion.substring(0, 19).equals("PAGARE TEF DEVUELTO"))
                                    psConcepto = "TEF DEVUELTO";
                                else if(psDescripcion.substring(0, 14).equals("VENTAS CREDITO"))
                                    psFolioBanco = (psRegistro.substring(62, 68));
                                else if(psDescripcion.substring(0, 15).equals("COMISION VENTAS"))
                                    psFolioBanco = (psRegistro.substring(62, 68));
                                else if(psDescripcion.substring(0, 15).equals("IVA COM. VENTAS"))
                                    psFolioBanco = (psRegistro.substring(62, 68));
                                else if(psDescripcion.substring(0, 2).equals("CE") || psDescripcion.substring(0, 2).equals("CC"))
                                    psConcepto = psDescripcion.substring(0, 8);
                                else if(psDescripcion.substring(0, 15).equals("REGRESO DE VENT")){
                                    psConcepto = "REGRESO DE VENT";
                                    psFolioBanco = (psRegistro.substring(59, 65));
                                }
                                else if(psDescripcion.substring(0, 15).equals("DEPOSITO EN EFE")){
                                    psConcepto = "DEPOSITO EN EFE";
                                    psFolioBanco = (psRegistro.substring(59, 65));
                                }
                            }
                        }
                        else{
                            psFolioBanco = (psRegistro.substring(28, 34)); //si no maneja mismo banco esta es la referencia
                            psConcepto = psRegistro.substring(34, 64); //Transaccion MD = Descripcion
                            psDescripcion = psRegistro.substring(93, 123).trim(); //'Referencia
                            pdImporte = Double.parseDouble(psRegistro.substring(65, 81));
                            psSucursal = psRegistro.substring(158, 162);
                            
                            
                            if(psTipoMovto.equals("2")) 
                                psCargoAbono = "S";
                            if(psTipoMovto.equals("1")) {
                            	pdCargo += pdImporte;
                            	psCargoAbono = "E";
                            }
                            if(psTipoMovto.equals("0")) {
                            	pdAbono += pdImporte;
                            	psCargoAbono = "I";
                            }
                            if(psConcepto.indexOf("CARGO CHEQUE") > -1)
                                psReferencia = psRegistro.substring(93, 115).trim();
                            else if(psConcepto.indexOf("SPEUA") > -1 ||
                                   psConcepto.indexOf("SPEI ENVIADO") > -1 ||
                                   psConcepto.indexOf("INTERBANCARIO ENVIADO") > -1)
                                psReferencia = psRegistro.substring(99, 123).trim();
                            else
                                psReferencia = psRegistro.substring(93, 123).trim();
                            if(psCargoAbono.equals("E"))
                                if(funciones.isNumeric(psReferencia))
                                    psReferencia = ""+psReferencia;
                            //' se comenta eso ya que antes ya se tenia comentado la asignacion del concepto, y el otro parametro de el folio
                            //' del banco no se pasa por lo que no regresa nada esta funcion
                            ParametroLayoutDto busquedaX = new ParametroLayoutDto();
                            busquedaX.setPiBanco(BANCOMER);
                            busquedaX.setPsDescripcion(psRegistro.substring(34, 64).trim());
                            busquedaX.setPsRegistro(psRegistro);
                            busquedaX.setPsCargoAbono(psCargoAbono);
                            busquedaX.setPsConcepto(psConcepto);
                            busquedaX.setPsFolioBanco("");
                            if(administradorArchivosDao.obtenerDescripcion(busquedaX)){
                            	psConcepto=busquedaX.getPsConcepto();
                            	psFolioBanco = (busquedaX.getPsFolioBanco());	
                            }
                            //'psDescripcion = psConcepto
                            psCveConcepto = psRegistro.substring(152, 155); // 'Codigo Transaccion
                        }
                    } // if(!psArchivo.substring(0, 2).equals("MM"))...
                        
                        List<MovtoBancaEDto> movto = null;
                        if(psManejaMismoBanco.equals("SI") && pbLayoutMismoDia){
                        	parametro = new ParametroLayoutDto();
							parametro.setPsFolioBanco("" + plFolioBanco);
							parametro.setPsConcepto("MISMO BANCO BANCOMER2");
							parametro.setPsReferencia(psReferencia);
							parametro.setPlNoEmpresa(piNoEmpresa);
							parametro.setPsIdChequera(psChequera);
							parametro.setPsFecValor(psFecha);
							parametro.setPdImporte(pdImporte);
							parametro.setPlNoLineaArchivo(iContador);
							parametro.setCargoAbono(psCargoAbono);
							parametro.setPiBanco(BANCOMER);
							movto = administradorArchivosDao.selecionarMovtoBancaE(parametro);
                        }else if(psManejaMismoBanco.equals("SI") && !psArchivo.substring(0, 2).equals("MM")){
                            // estamos leyendo el layout del estado de cuenta
                            // PARA BANCOMER CON EL AUTOMATA CON MOVIMIENTOS AVANZADOS DEL DIA
                            // HAY QUE CAMBIAR EL CONCEPTO POR LA CLAVE DEL CONCEPTO YA QUE LO CAMBIAN DE UNO AL OTRO
                        	parametro = new ParametroLayoutDto();
							parametro.setPsFolioBanco("" + plFolioBanco);
							parametro.setPsConcepto("MISMO BANCO BANCOMER");
							parametro.setPsReferencia("");
							parametro.setPlNoEmpresa(piNoEmpresa);
							parametro.setPsIdChequera(psChequera);
							parametro.setPsFecValor(psFecha);
							parametro.setPdImporte(pdImporte);
							parametro.setCargoAbono(psCargoAbono);
							parametro.setPiBanco(BANCOMER);
							movto = administradorArchivosDao.selecionarMovtoBancaE(parametro);
                        }else{
                        	parametro = new ParametroLayoutDto();
                            parametro.setPsFolioBanco("" + plFolioBanco);
							parametro.setPsConcepto(psConcepto);
							parametro.setPsReferencia(psReferencia);
							parametro.setPlNoEmpresa(piNoEmpresa);
							parametro.setPsIdChequera(psChequera);
							parametro.setPsFecValor(psFecha);
							parametro.setPdImporte(pdImporte);
							parametro.setCargoAbono(psCargoAbono);
							parametro.setPiBanco(BANCOMER);
							movto = administradorArchivosDao.selecionarMovtoBancaE(parametro);
                        }
                        if(movto.size()<= 0){
                            plFolio = seleccionarFolioReal("secuencia");
                            if(plFolio <= 0){
                            	archivo.close();
                                for (int h = z; h < nombres.length; h++)
									busquedaArchivos.cambiarExtensionSoloUno(nombres[h], ".tmp",".txt");
                                return false;
                            }
                            leeBancomer = true;
                            //'MLI5 psObservacion
                            //'IALA 02/08/2007
                            //'Se le agrego una variable mas a la funcion FunSQLInserta_movto_banca_e_str
                            //'para pasarle la hora del archivo, con motivos del fideicomiso
	                        insercion = new ParametroLayoutDto();
							insercion.setPlNoEmpresa(piNoEmpresa);
							insercion.setPiBanco(BANCOMER);
							insercion.setPsIdChequera(psChequera);
							insercion.setPsSecuencia(plFolio);
							insercion.setPsFecValor(psFecha);
							insercion.setPsSucursal(psSucursal);
							insercion.setPsFolioBanco("" + plFolioBanco);
							insercion.setPsReferencia(psReferencia);
							insercion.setPsCargoAbono(psCargoAbono);
							insercion.setPdImporte(pdImporte);
							insercion.setPsBSalvoBuenCobro(psSBCobro);
							insercion.setPsConcepto(psConcepto);
							insercion.setPsUsuarioAlta(idUsuario);
							insercion.setPsDescripcion(psDescripcion);
							insercion.setPsArchivo(psArchivo);
							insercion.setPsFecAlta(funciones.ponerFechaSola(obtenerFechaHoy()));
							insercion.setPsObservacion("");
							insercion.setPsCveConcepto(psCveConcepto);
							insercion.setPdSaldoBancario(pdSaldo);
							insercion.setPlNoLineaArchivo(iContador);
							insercion.setPsHora(psHora);
							insercion.setPsFechaHoy(funciones.cambiarFecha(obtenerFechaHoy().toString(), true));
							psInserts = psInserts + administradorArchivosDao.insertarMovtoBancaEStr(insercion); 
                            plRegImportados++;
                            piEncontrados++;
                        }
                        else if(movto.size() >= 1 && psManejaMismoBanco.equals("SI") && !pbLayoutMismoDia 
                        		&& !psArchivo.substring(0, 2).equals("MM")){
                            //' significa que estamos subiendo el estado de cuenta
                            //' y se tiene el proceso de lectura del automata
                            //' asi que hay que poner los datos extras
                            //' solo se actualiza si es un archivo MD o MM
                            if(movto.get(0).getArchivo().substring(0, 2).equals("MD") 
                            		|| movto.get(0).getArchivo().substring(0, 2).equals("MM")){
                            	ParametroLayoutDto actu = new ParametroLayoutDto();
                            	actu.setPlNoEmpresa(piNoEmpresa);
                            	actu.setPiBanco(BANCOMER);
                            	actu.setPsIdChequera(psChequera);
                            	actu.setPsSecuencia(movto.get(0).getSecuencia());
                            	actu.setPsSucursal(psSucursal);
                            	actu.setPsReferencia(psReferencia);
                            	actu.setPsBSalvoBuenCobro(psSBCobro);
                            	actu.setPsConcepto(movto.get(0).getArchivo().substring(0,2));
                            	actu.setPsCveConcepto(psCveConcepto);
                            	actu.setPsArchivo(psArchivo);
                            	actu.setPsDescripcion(psDescripcion);
                            	actu.setPsCargoAbono(psCargoAbono);
                            	administradorArchivosDao.actualizarDatosEDOCtaExtras(actu);
                            }
                        }
                    
				} // while(archivo.hasNext())...
				if(!psInserts.equals("")) administradorArchivosDao.ejecutaSentencia(psInserts);
                psInserts = "";
				dto.setPsArchivos(psArchivos);
				dto.setPlRegLeidos(plRegLeidos);
				dto.setPlRegSinChequera(plRegSinChequera);
				dto.setPsChequerasInexistentes(psChequerasInexistentes);
				dto.setPlRegImportados(plRegImportados);
				archivo.close();
				archivo=null;
				busquedaArchivos.cambiarExtensionSoloUno(nombres[z], ".tmp",".old");
				z++;
			}//while(z<nombres.length)...
		}//if(nombres!=null)...
		mensajes.add("Archivos Leidos:  " + dto.getPsArchivos());
		mensajes.add("Chequeras Contenidas:  " + numChequeras);
		mensajes.add("Registros Contenidos:  " + dto.getPlRegLeidos());
		mensajes.add("Chequeras Procesadas:  " + numCheqProce);
		mensajes.add("Registros Procesados:  " + dto.getPlRegImportados());
		mensajes.add("Chequeras No Existentes:  " + dto.getPsChequerasInexistentes());
		mensajes.add("Registros Sin Chequera:  " + dto.getPlRegSinChequera());
		mensajes.add("Total Cargos:  " + pdCargo);
		mensajes.add("Total Abonos:  " + pdAbono);
		mensajes.add("Total Saldo Bancario:  " + (pdCargo + pdAbono));
		
		
		
		logger.info("leeB: " + leeBancomer);
		
		return leeBancomer;
	}
	
	@SuppressWarnings("unused")
	public boolean leeNetCashC43(ParametroLayoutDto dto) {
		String sRuta = "";
		String psInserts = "";
		String psFechaSaldo = "";
		String psTipoMovto = "";
		String psCargoAbono = "";
		String psSBCobro = "";
		String psConcepto = "";
        String psSucursal = "";
        String psCveConcepto = "";
		String psFolioBanco = "";
		String psDescripcion = "";
		String psReferencia = "";
		String psBSaldoBanco = "";
		
		Double pdImporte;
        Double pdSaldoChequera;
        
		boolean leeNetCashC43 = false;
		
		int op = -1;
		int iRegSinChequera = 0;
		int plRegImportados = 0;
		
		List<CatCtaBancoDto> empresas = new ArrayList<CatCtaBancoDto>();
		List<MovtoBancaEDto> movtoBancaE = new ArrayList<MovtoBancaEDto>();
		List<ComunDto> saldoChequera = new ArrayList<ComunDto>();  
		bEmpresa = new ParametroBuscarEmpresaDto();
		inicializar();
		psChequerasInexistentes = "";
		
		sRuta = administradorArchivosDao.consultarConfiguraSet(201)+"\\bancomer\\netCashC43\\";
		
		if(!sRuta.equals(rutaActual))
			System.setProperty("user.dir", sRuta);
		
		File[] nombresArch = busquedaArchivos.obtenerNombreArchivos(".exp");
		//logger.info("archivos:" + nombresArch.length);
		mensajes.add("\nBancomer netCashC43");
		saldoChequera = administradorArchivosDao.seleccionarSaldoBanco(BANCOMER);
		psBSaldoBanco = saldoChequera.get(0).getDescripcion();  
		
		if(nombresArch != null) {
			while(z < nombresArch.length) {  // hasta que no encuentres archivos
				nombresArch = busquedaArchivos.cambiarExtension(nombresArch, ".exp", ".old");
				
				try {
					archivo = new Scanner(nombresArch[z]);
				}catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				psArchivo = nombresArch[z].getName().replace(".old", ".exp");
				
				if(psArchivos.equals(""))
					psArchivos = psArchivo;
				else
					psArchivos += "', '" + psArchivo;
				
				i = 0;
				iContador = 0;
				piEncontrados = 0;
				piNoEmpresa = 0;
				pdImporte = 0.0;
				
				psChequera = "";
				psInserts = "";
				
				
	            archivoOk = true;
                
                while(archivo.hasNext()) {//hasNext // hasta que no haya lineas que leer
                	i++;
//sigue_leyendo1:
					logger.info( z + ".- Archivo: " + psArchivo);
					logger.info(" Chequera: " + (psChequera == null ? "No hay Chequera" : psChequera));
					logger.info(" Chequera Nueva: " + (psChequeraNueva == null ? "" : psChequeraNueva));
					logger.info(" No Empresa: " + piNoEmpresa);
                    
					psRegistro = archivo.nextLine();
                    psRegistro = psRegistro.replace("'", " ");
                    logger.info("Registro: " + psRegistro + "\n*********************");
                    
                	if(funciones.isNumeric(psRegistro.substring(0, 2)))
                		op = Integer.parseInt(psRegistro.substring(0, 2));
                	switch(op) {	                   
                    	case 0: /* Indica el inicio de Archivo
                    		La primer linea es cabecera y ningun valor nos interesa REGISTRO, CLAVE PAIS, FECHA DE PROCESO, LIBRE*/
                    		
                    		break;
                    	case 11: /* Registro de cabecera de cuenta
                    		De la cabecera de la cuenta se toma el NO_CUENTA, REGISTRO, CLAVE PAIS, SUCURSAL, CUENTA, FECHA INICIAL, FECHA FINAL,
                    		SALDO 2[+] / 1[-], SALDO INICIAL, MONEDA ALFABETICA, DIGITO CUENTA CLABE, TITULAR DE LA CUENTA, PLAZA CUENTA CLABE*/
                    		
                    		psChequeraNueva = psRegistro.substring(10, 20);
                    		
                    		bEmpresa = new ParametroBuscarEmpresaDto();
                    		bEmpresa.setPsChequera(psChequeraNueva);
                    		bEmpresa.setPiBanco(BANCOMER);
                    		bEmpresa.setPbSwiftMT940MN(true);
                    		empresas = administradorArchivosDao.buscaEmpresaNetCash(bEmpresa);
                    		
                    		if(empresas.size() > 0) {
                    			piNoEmpresa = empresas.get(0).getNoEmpresa();
                    			psChequera = empresas.get(0).getIdChequera();
                    		}else {
                    			iRegSinChequera++;
                    			psChequerasInexistentes += (!psChequerasInexistentes.equals("") ? ", " : "") + psChequeraNueva;
                    			
	                            while(archivo.hasNext()) { //hasNext // hasta que no haya lineas que leer
	                            	psRegistro = archivo.nextLine();
	                                psRegistro = psRegistro.replace("'", " ");
	                                
	                                if(psRegistro.substring(0, 2).equals("33"))
	                                	break;
	                            }
	                            i--;
	                            continue;
	                            //GoTo sigue_leyendo1
                    		}
                    		psFechaSaldo = psRegistro.substring(30, 32) + "/" + psRegistro.substring(28, 30) + "/20" + psRegistro.substring(26, 28);
                    		break;
                    	case 22: /* Registro principal de movimiento 
                    		REGISTRO, CLAVE PAIS, SUCURSAL, FECHA OPERACInN, FECHA VALOR, CVE CONCILIACION AGREGADA, _
                            CLAVE LEYENDA, CARGO (1) / ABONO (2), IMPORTE, DATO, CONCEPTO
                            Para Archivos SH  0 - Abono, 1 - Cargo, 2 n Saldo, 3 n Saldo Negativo */
                    		
                    		if(psRegistro.trim().substring(27, 28).equals("1")) {
                    			psTipoMovto = "1";
                    			psCargoAbono = "E";
                    			psSBCobro = "";
                    		}else if(psRegistro.trim().substring(27, 28).equals("2")) {
                    			psTipoMovto = "0";
                    			psCargoAbono = "I";
                    			
                    			if(psRegistro.substring(52, 80).trim().equals("DEP S B COBRO"))
                    				psSBCobro = "N";
                    			else
                    				psSBCobro = "S";
                    		}
                    		psFecha = psRegistro.substring(20, 22) + "/" + psRegistro.substring(18, 20) + "/20" + psRegistro.substring(16, 18);
                    		
                    		if(!funciones.isDate(psFecha, false)) {
                    			//se salta el complementario del movimiento
                    			
                    			while(archivo.hasNext()) {//hasNext // hasta que no haya lineas que leer
                    				psRegistro = archivo.nextLine();
                    				psRegistro = psRegistro.replace("'", " ");
                    				break;
	                            }
                    			i--;
                    			continue;
                    			//GoTo sigue_leyendo1
                    		}
                    		psConcepto = psRegistro.substring(52, 80);
                            pdImporte = Double.parseDouble(psRegistro.substring(28, 40) + "." + psRegistro.substring(40, 42));
                            psSucursal = psRegistro.substring(6, 10);
                            psCveConcepto = psRegistro.substring(24, 27).trim();
                            break;
                    	case 23: /* Registro complementario de movimiento
                    	 	REGISTRO, CODIGO DATO, REFERENCIA AMPLIADA, REFERENCIA */
                    		
                    		iContador++;
                    		plRegLeidos++;
                    		//El ejemplo lo encuentra en esta posicion, el folio varia de posicion en funcion del concepto (Se pueden basar en la lectura del MD)
                    		psFolioBanco = "";
                    		
                    		if(psConcepto.substring(0, 19).equals("DEPOSITO DE TERCERO"))
                    			psFolioBanco = psRegistro.substring(42, 49);
                    		else if(psConcepto.substring(0, 22).equals("TRASPASO ENTRE CUENTAS"))
                    			psFolioBanco = psRegistro.substring(42, 49);
                    		else if(psConcepto.substring(0, 12).equals("SPEI ENVIADO")) {
                    			psFolioBanco = psRegistro.substring(45, 52);
                    			psConcepto = "SPEI ENVIADO";
                    		}else if(psConcepto.substring(0, 20).equals("DEPOSITO EN EFECTIVO"))
                    			psFolioBanco = psRegistro.substring(42, 49);
                    		else if(psConcepto.substring(0, 19).equals("TRASPASO A TERCEROS"))
                    			psFolioBanco = psRegistro.substring(42, 49);
                    		else if(psConcepto.substring(0, 13).equals("SPEI RECIBIDO")) {
                    			psFolioBanco = psRegistro.substring(45, 52);
                    			psConcepto = "SPEI RECIBIDO";
                    		}else if(psConcepto.substring(0, 12).equals("TEF RECIBIDO")) {
                    			psFolioBanco = psRegistro.substring(45, 52);
                    			psConcepto = "TEF RECIBIDO";
                    		}
                    		psDescripcion = psRegistro.substring(4, 42); //'referencia ampliada
                    		/*validar para los otros casos la posicion donde se encuentra la referencia del SH
                            El ejemplo lo encuentra en esta posicion, la referencia varia de posicion en funcion del concepto
                            'If InStr(1, psConcepto, "CARGO CHEQUE") > 0 Then
                            '    psReferencia = Trim(Mid(ps_registro, 94, 22))
                            'ElseIf InStr(1, psConcepto, "SPEUA") > 0 Or _
                            '       InStr(1, psConcepto, "SPEI ENVIADO") > 0 Or _
                            '       InStr(1, psConcepto, "INTERBANCARIO ENVIADO") > 0 Then
                            '    psReferencia = Trim(Mid(ps_registro, 100, 24))
                            'Else*/
                            psReferencia = psRegistro.substring(42, 72).trim(); //referencia ampliada
                            //End If
                            //estamos leyendo el layout del estado de cuenta NetCashC43
                            parametro = new ParametroLayoutDto();
                            parametro.setPsFolioBanco(psFolioBanco);
                            //parametro.setPsConcepto("MISMO BANCO BANCOMER");
                            parametro.setPsConcepto(psConcepto);
		                    parametro.setPsReferencia(psReferencia);
		                    parametro.setPlNoEmpresa(piNoEmpresa);
		                    parametro.setPsIdChequera(psChequera);
		                    parametro.setPsFecValor(psFecha);
		                    parametro.setPdImporte(pdImporte);
		                    parametro.setPdSaldo(0);
		                    parametro.setPlNoLineaArchivo(0);
		                    parametro.setPsCargoAbono(psCargoAbono);
		                    parametro.setPiBanco(BANCOMER);
		                    movtoBancaE = administradorArchivosDao.seleccionarMovtoBancaE(parametro);
		                    
		                    if(movtoBancaE.size() <= 0) {
		                    	plFolio = seleccionarFolioReal("secuencia");
		                    	
		                    	if (plFolio <= 0) {
		                    		archivo.close();
		                    		
		                    		for (int h = z; h < nombresArch.length; h++)
		                    			busquedaArchivos.cambiarExtensionSoloUno(nombresArch[h], ".tmp",".txt");
		                    		return false;
		                    	}
		                    	leeNetCashC43 = true;
		                    	
		                    	insercion = new ParametroLayoutDto();
								insercion.setPlNoEmpresa(piNoEmpresa);
								insercion.setPiBanco(BANCOMER);
								insercion.setPsIdChequera(psChequera);
								insercion.setPsSecuencia(plFolio);
								insercion.setPsFecValor(psFecha);
								insercion.setPsSucursal(psSucursal);
								insercion.setPsFolioBanco(psFolioBanco);
								insercion.setPsReferencia(psReferencia);
								insercion.setPsCargoAbono(psCargoAbono);
								insercion.setPdImporte(pdImporte);
								insercion.setPsBSalvoBuenCobro(psSBCobro);
								insercion.setPsConcepto(psConcepto);
								insercion.setPsEstatus("");
								insercion.setPsFechaHoy(funciones.ponerFechaSola(obtenerFechaHoy()));
								insercion.setPsUsuarioAlta(idUsuario);
								insercion.setPsDescripcion(psDescripcion);
								insercion.setPsArchivos(psArchivo);
								insercion.setPsObservacion("");
								insercion.setPsCveConcepto(psCveConcepto);
								insercion.setPdSaldoBancario(0);
								insercion.setPlNoLineaArchivo(iContador);
								psInserts = psInserts + administradorArchivosDao.insertarMovtoBancaEStrNet(insercion); 
                                plRegImportados++;
                                piEncontrados++;
		                    }
		                    break;
                    	case 32: /* Registro complementario de fin de cuenta
                    		REGISTRO, CLAVE PAIS, SUBCODIGO DE REGISTRO, INFORMACION 1*, INFORMACION 2* */
                    		
                    		break;
                    	case 33: /* Registro de fin de cuenta
                    		REGISTRO, CLAVE PAIS, SUCURSAL, CUENTA, No. DE CARGOS, IMPORTE TOTAL DE CARGOS, No. DE ABONOS,
                    		IMPORTE TOTAL DE ABONOS, SALDO 2[+] / 1[-], SALDO FINAL, MONEDA ALFABETICA LIBRE */
                    		
                    		psCargoAbono = "S";
                    		
                    		if(psRegistro.substring(58, 59) == "1")
                    			psTipoMovto = "3";
                    		else
                    			psTipoMovto = "2";
                    		
                    		psSBCobro = "";
                    		psFecha = psFechaSaldo;
                    		
                    		if(!funciones.isDate(psFecha, false)) {
                    			i--;
                    			continue;
                    			//GoTo sigue_leyendo1
                    		}
                    		psConcepto = "SALDO ULTIMA TRANS";
                    		pdImporte = Double.parseDouble(psRegistro.substring(59, 71) + "." + psRegistro.substring(71, 73));
                    		psSucursal = psRegistro.substring(6, 10);
                            psCveConcepto = ""; //Clave leyenda
                            iContador++;
                            plRegLeidos++;
                            psFolioBanco = "0";
                            psDescripcion = "";
                            psReferencia = "";
                            
                            //Verificar que maneje actualizacinn de saldos
                    		if(psBSaldoBanco.equals("R")) {
                    			//actualizar saldo de los movimientos insertados
                    			if(!psInserts.equals(""))
                	                administradorArchivosDao.ejecutaSentencia(psInserts);
                    			
                    			psInserts = "";
                    			pdSaldoChequera = Double.parseDouble(psRegistro.substring(59, 71) + "." + psRegistro.substring(71, 73));
                    			
                    			if(psRegistro.substring(58, 59).equals("1"))
                    				pdSaldoChequera = pdSaldoChequera * -1;
                                /*
                    			Call actualizar_saldo_chequera_bancomer(pd_saldo_chequera, ps_Archivo, 12, ps_Chequera) 'Falta probar
                    			'actualizar saldo en cat_cta_banco
                    			Call actualizar_saldo_chequera(pd_saldo_chequera, pi_NoEmpresa, 12, ps_Chequera)
								*/
                    			leeNetCashC43 = true;
                    		}else {
                    			mensajesUsuario.add("Bancomer NetCashC43; n no maneja actualizacinn de saldo en chequera !");
                                archivo.close();
                                busquedaArchivos.cambiarExtensionSoloUno(nombresArch[z], ".tmp", ".err");
                                
                                for (int h = z+1; h < nombresArch.length; h++)
									busquedaArchivos.cambiarExtensionSoloUno(nombresArch[h], ".tmp", ".txt");
                                return false;
                    		}
                    		parametro = new ParametroLayoutDto();
                    		parametro.setPsFolioBanco(psFolioBanco);
                            //parametro.setPsConcepto("MISMO BANCO BANCOMER");
		                    //parametro.setPsReferencia("");
                    		parametro.setPsConcepto(psConcepto);
		                    parametro.setPsReferencia(psReferencia);
                    		parametro.setPlNoEmpresa(piNoEmpresa);
		                    parametro.setPsIdChequera(psChequera);
		                    parametro.setPsFecValor(psFecha);
		                    parametro.setPdImporte(pdImporte);
		                    parametro.setPdSaldo(0);
		                    parametro.setPlNoLineaArchivo(0);
		                    parametro.setPsCargoAbono(psCargoAbono);
		                    parametro.setPiBanco(BANCOMER);
		                    movtoBancaE = administradorArchivosDao.seleccionarMovtoBancaE(parametro);
		                    
		                    if(movtoBancaE.size() <= 0) {
		                    	plFolio = seleccionarFolioReal("secuencia");
		                    	
		                    	if (plFolio <= 0) {
		                    		archivo.close();
		                    		
		                    		for (int h = z; h < nombresArch.length; h++)
		                    			busquedaArchivos.cambiarExtensionSoloUno(nombresArch[h], ".tmp",".txt");
		                    		return false;
		                    	}
		                    	leeNetCashC43 = true;
		                    	
		                    	insercion = new ParametroLayoutDto();
								insercion.setPlNoEmpresa(piNoEmpresa);
								insercion.setPiBanco(BANCOMER);
								insercion.setPsIdChequera(psChequera);
								insercion.setPsSecuencia(plFolio);
								insercion.setPsFecValor(psFecha);
								insercion.setPsSucursal(psSucursal);
								insercion.setPsFolioBanco(psFolioBanco);
								insercion.setPsReferencia(psReferencia);
								insercion.setPsCargoAbono(psCargoAbono);
								insercion.setPdImporte(pdImporte);
								insercion.setPsBSalvoBuenCobro(psSBCobro);
								insercion.setPsConcepto(psConcepto);
								insercion.setPsEstatus("");
								insercion.setPsFechaHoy(funciones.ponerFechaSola(obtenerFechaHoy()));
								insercion.setPsUsuarioAlta(idUsuario);
								insercion.setPsDescripcion(psDescripcion);
								insercion.setPsArchivos(psArchivo);
								insercion.setPsObservacion("");
								insercion.setPsCveConcepto(psCveConcepto);
								insercion.setPdSaldoBancario(0);
								insercion.setPlNoLineaArchivo(iContador);
								psInserts = psInserts + administradorArchivosDao.insertarMovtoBancaEStrNet(insercion); 
                                plRegImportados++;
                                piEncontrados++;
		                    }
		                	break;
                    	case 88: /* Solo final de archivo
                    		La ultima linea es pie y ningun valor nos interesa para el proceso
                    		REGISTRO, CAMPO FIJO No. 9 (18), No. DE REGISTROS (contando cabecera pero no el pie), LIBRE
                    		Se puede hacer las validaciones para checar que el archivo no este alterado */
                    		
                    		break;
                            //GoTo siguiente
                	}
                }
//siguiente:
                if(!psInserts.equals(""))
	                administradorArchivosDao.ejecutaSentencia(psInserts);
                
                psInserts = "";
				dto.setPsArchivos(psArchivos);
				dto.setPlRegLeidos(plRegLeidos);
				dto.setPlRegSinChequera(iRegSinChequera);
				dto.setPsChequerasInexistentes(psChequerasInexistentes);
				dto.setPlRegImportados(plRegImportados);
				archivo.close();
				archivo = null;
				
				if(piEncontrados == i)
					busquedaArchivos.cambiarExtensionSoloUno(nombresArch[z], ".tmp", ".old");
				z++;
			} //while(z < nombresArch.length)
		}// if(nombres != null)
		mensajes.add("\nRegistros Leidos:  " + dto.getPlRegLeidos());
		mensajes.add("\nRegistros sin chequera: " + dto.getPlRegSinChequera());
		mensajes.add(dto.getPsChequerasInexistentes());
		mensajes.add("\nRegistros Importados:  " + dto.getPlRegImportados());
		logger.info("leeB: " + leeNetCashC43);
		
		return leeNetCashC43;
        
		/* aqui despues de leer hay que detectar y marcar los rechazados para que no se ocupen al confirmar en semiautomatico
	       esto en egresos confirmacion de transferencias 
		Call gobjSQL.FunSQLUpdateRechazados(BANCOMER)
		ProcesaRechazados BANCOMER */
	}
	
	//Para la lectura del MT940
	@SuppressWarnings({ "unused", "deprecation" })
	public String lecturaMT940(String fechaHoy)
	{
		//rcaballero
		String psRuta = "";
		String respuesta = "";
		String psArchivo = "";
		String psRegistro = "";		
		String psIdentificador = "";		
		String psReferencia = "";
		String psDescripcion = "";
		String psFolioBanco = "";
		String psDescBanco = "";		
		String fechaAno = "";
		String fechaMes = "";
		String fechaDia = "";
		String psChequera = "";
		String psChequeraAnt = "";
		String psCampo = "";
		String psTipoMonto = "";
		String psFechaOperacion = "";
		String psDivisa = "";
		String psCargoAbono = "";
		String psClaveBancaria = "";
		String lblRegistro = "0";
		String psAuxiliar = "";
		String psTipoReferencia = "";
		String psObservacion = "";
		String var = "";
		String psChequera2 = "";
		String psChequera12 = "";
		String psChequera14 = "";
		String psChequera1026 = "";
		String psDescBanco2 = "";
		String psDescBanco12 = "";
		String psDescBanco14 = "";
		String psDescBanco1026 = "";
		int posicion = 0;
		int posicionBlanco = 0;
		int piBanco = 0;
		int piEmpresa = 0;
		int iContador = 0;
		int buscaChequera = 0;
		int piRegLeidos2 = 0;
		int piRegLeidos12 = 0;
		int piRegLeidos14 = 0;
		int piRegLeidos1026 = 0;
		int cuentaCaracteres = 0;
		int longitud = 0;
		int piFolio = 0;
		int cuentaLinea = 0;
		int importados2 = 0;
		int importados12 = 0;
		int importados14 = 0;
		int importados1026 = 0;
		boolean validoSaldo = false;
		boolean pbInserta = false;
		boolean pbFinal  = false;
		boolean pbFinalSolo = false;
		boolean bExisteChequera = false;
		boolean pbSaldoInicial = false;
		boolean pbArchivoOk = false;
		double pdMonto = 0;
		double pdRubro = 0;
		double pdSaldoInicial = 0;
		double totalIngresos = 0;
		double totalEgresos = 0;
		
		SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
		
		List<MovtoBancaEDto> recibeDatos = new ArrayList<MovtoBancaEDto>();
		
		MovtoBancaEDto objInsertaDto = new MovtoBancaEDto();
		
		funciones = new Funciones();
		busquedaArchivos = new BusquedaArchivos();
		rutaActual = null;
		i = 0;
		rutaActual = System.getProperty("user.dir");
		
		try
		{
			//Ruta de los edos de cta MT940
			psChequeraAnt = "";
			psRuta = administradorArchivosDao.consultarConfiguraSet(2405);
			
			if (psRuta.equals(""))
				respuesta = "No existe Ruta para el MT940";
			else
			{							
				fechaAno = fechaHoy.substring(0, 4);
				fechaMes = fechaHoy.substring(5, 7);
				fechaDia = fechaHoy.substring(8, 10);
												
				psRuta = psRuta + "scheduler\\estados\\recepcion\\" + fechaAno + fechaMes + fechaDia + "\\"; 
											
				if (!psRuta.equals(rutaActual))				
					System.setProperty("user.dir", psRuta);
								
				File [] nombres = busquedaArchivos.obtenerNombreArchivos(".txt");
								
				if (nombres != null)
				{										
					cambiaArchivo:					
					while (i < nombres.length)
					{
						cuentaLinea = 0;
						try
						{								
							nombres = busquedaArchivos.cambiarExtension(nombres, ".txt", ".tmp");
							archivo = new Scanner(nombres[i]);
						}						
						catch(FileNotFoundException e)
						{						
							e.printStackTrace();
						}
						
						psArchivo = nombres[i].getName();
						
						if (!psArchivo.equals("")) 
						{
							siguienteLinea:
							while (archivo.hasNext()) //Se usa la variable de tipo Scanner y Lee el archivo hasta la ultima linea
							{
								
								cuentaLinea = cuentaLinea + 1;
								psRegistro = archivo.nextLine();								
								posicion = psRegistro.indexOf(":");
																							
								if (psRegistro.length() > 0 && !psRegistro.equals(""))
								{									
									if (posicion != -1)
									{										
										psIdentificador = psRegistro.substring(posicion + 1, posicion + 3);									
									}
									else
									{	
										if (posicion == -1)
										{
											posicion = 0;
										}
										
										if (psReferencia.equals("") && piBanco != BANCOMER)
										{
											psReferencia = psRegistro.substring(0, psRegistro.length());
										}
										
										if (piBanco == SANTANDER)
										{
											psDescripcion = psRegistro.substring(0, psRegistro.length());
										}
										else
										{
											if (psFolioBanco.equals(""))
											{
												psFolioBanco = psRegistro.substring(3, 19);
											}
										}
										
										psIdentificador = "";
										pbInserta = true;
										
										continue siguienteLinea;										
									}
									
								}
								else
								{
									continue siguienteLinea;
								}
																
								if (psIdentificador.equals("25"))
								{										
									pbFinalSolo = false;
									psChequera = psRegistro.substring(posicion + 4, psRegistro.length());									
									psChequera = funciones.elmiminarEspaciosAlaDerecha(psChequera);
																
									if (!psChequeraAnt.equals(""))
									{
										if (psChequeraAnt.equals(psChequera) && iContador > 0)
										{												
											continue siguienteLinea;
										}										
									}
									
									/*Se coloca esta validacion porque Citibank trae la chequea incorrecta, si se corrige layout
									 * este codigo se elimina 
									 */
									//////////////////////////
									buscaChequera = psChequera.indexOf("0326");
									
									if (buscaChequera > 0)
									{
										buscaChequera = 0;
										//psChequera = funciones.elmiminarEspaciosAlaDerecha(psChequera);
										buscaChequera = psChequera.indexOf("/");
										if (buscaChequera != 0)
										{
											psChequera = psChequera.substring(1, buscaChequera - 1);
										}
									}
									//////////////////////////
									
									recibeDatos = administradorArchivosDao.buscaBancoChequera(psChequera);									
									if (recibeDatos.size() != 0)
									{										
										piEmpresa = recibeDatos.get(0).getNoEmpresa();
										piBanco = recibeDatos.get(0).getIdBanco();
																												
										if (!psChequera.equals(psChequeraAnt))
										{											
											switch (piBanco)
											{
											case 2:
												psDescBanco2 = "BANAMEX:";
												if (psChequera2.equals(""))
													psChequera2 = psChequera;
												else if(!psChequera2.equals(psChequera))
													psChequera2 += "\n " + psChequera;
												break;
											case 12:
												psDescBanco12 = "BANCOMER:";
												if (psChequera12.equals(""))
													psChequera12 = psChequera;
												else if(!psChequera12.equals(psChequera))
													psChequera12 += "\n " + psChequera;
												break;
											case 14:
												psDescBanco14 = "SANTANDER:";
												if (psChequera14.equals(""))
													psChequera14 = psChequera;
												else if(!psChequera14.equals(psChequera))
													psChequera14 += "\n " + psChequera;
												break;
											case 1026:
												psDescBanco1026 = "CITIBANK:";
												if (psChequera1026.equals(""))
													psChequera1026 = psChequera;
												else if(!psChequera14.equals(psChequera))
													psChequera1026 += "\n " + psChequera;
												break;
											}
										}									
										
										/*
										 * Se coloca esta validacion para cuando sea Citibank se mande llamar la funcion actual
										 * ya que para el MT940 aun no mandan desde el banco el edo de cta correcto
										 */
										
										if (piBanco == CITIBANK)
										{
											nombres = busquedaArchivos.cambiarExtension(nombres, ".tmp", ".old");
											
											continue cambiaArchivo;
											//Esta pendiente la lectura de Citibank											
										}
										
										psChequera = recibeDatos.get(0).getIdChequera();
										psDescBanco = recibeDatos.get(0).getDescBanco();
										
	                                    bExisteChequera = true;
									}
									else
									{
										//Cierra y renombra archivo
										nombres = busquedaArchivos.cambiarExtension(nombres, ".tmp", ".err");
										archivo.close();
										
										i++;
										continue cambiaArchivo;
									}
								}//Termina identificador 25
								
								
								pbInserta = false;
																
								
								if ((!psChequeraAnt.equals(psChequera) && iContador > 0) || pbSaldoInicial == false)
								//if(psIdentificador.equals("62"))								
								{										
									//if ((!psChequeraAnt.equals(psChequera) && iContador > 0) || pbSaldoInicial == false)
									if(psIdentificador.equals("62"))
									{
										pbSaldoInicial = true;										
										psCampo = psRegistro.substring(posicion + psIdentificador.length(), posicion + psIdentificador.length() + 1);
										posicion = psRegistro.indexOf(":");
										psReferencia = "SALDO ULTIMA TRANS";
										psCargoAbono = "S";
																				
										if (psCampo.equals("F"))
										{
											psTipoMonto = psRegistro.substring(posicion + 1, 6);
											posicion = posicion + 2;
											psFechaOperacion = psRegistro.substring(posicion + 4, 13);
											psFechaOperacion = psFechaOperacion + "/" + psRegistro.substring(posicion + 2, 11);
											psFechaOperacion = psFechaOperacion + "/" + psRegistro.substring(posicion, 9);
											
											posicion = posicion + 6;
											psDivisa = psRegistro.substring(posicion, 15);
											posicion = posicion + 3;
											
											
											recibeDatos = administradorArchivosDao.selectEquivaleDivisa(psDivisa, "BE", false);
											
											if (recibeDatos.size() != 0)
											{
												psDivisa = recibeDatos.get(0).getIdDivisa();
											}
											else
												psDivisa = "";
											
											if (psTipoMonto.equals("C"))
											{
												pdMonto = Double.parseDouble(psRegistro.substring(posicion, psRegistro.length()).replace(",", "."));
											}
											else if (psTipoMonto.equals("D"))
											{
												pdMonto = Double.parseDouble(psRegistro.substring(posicion + 3, psRegistro.length()).replace(",", "."));
												pdMonto = pdSaldoInicial * -1;
												psCargoAbono = "S";
											}
										}
										else if (psCampo.equals("M"))
										{
											pbSaldoInicial = false;
											continue siguienteLinea;											
										}
										
										psFolioBanco = "";
										psClaveBancaria = "";										
										pbFinalSolo = true;
										pbInserta = true;
									}								
								}//Termina el identificador 62				
															
								if (psIdentificador.equals("61"))
								{										
									switch (piBanco)
									{
										case(2):
											piRegLeidos2 = piRegLeidos2 + 1;
											break;
										case(12):
											piRegLeidos12 = piRegLeidos12 + 1;
											break;
										case(14):
											piRegLeidos14 = piRegLeidos14 + 1;
											break;
										case(1026):								
											piRegLeidos1026 = piRegLeidos1026 + 1;
											break;
									}
									
									if (piBanco == SANTANDER)
									{
										
										iContador = iContador + 1;
										lblRegistro = psDescBanco + " " + iContador;
										
										posicion = posicion + 1;
										psFechaOperacion = psRegistro.substring(posicion + 5, 8);
										psFechaOperacion = psFechaOperacion + "/" + psRegistro.substring(posicion + 7, 10);														
										psFechaOperacion = psFechaOperacion + "/" + psRegistro.substring(posicion + 3, 6);										
										psFechaOperacion = formatoFecha.format(Date.parse(psFechaOperacion));										
																													
										posicion = posicion + 9;
										
										psTipoMonto = psRegistro.substring(posicion, 11);										
										if (psTipoMonto.equals("C") || psTipoMonto.equals("D"))
										{
											if (psTipoMonto.equals("C"))
												psCargoAbono = "I";
											else
												psCargoAbono = "E";
													
										}
										else
										{
											posicion = posicion + 4;
											psTipoMonto = psRegistro.substring(posicion, 11);
											if (psTipoMonto.equals("C"))
												psCargoAbono = "I";
											else
												psCargoAbono = "E";
										}									
																				
										posicion = posicion + 1;
										psDivisa = psRegistro.substring(posicion, 12);
										
										recibeDatos = administradorArchivosDao.selectEquivaleDivisa(psDivisa, "BE", true);
										if (recibeDatos.size() != 0)
										{
											psDivisa = recibeDatos.get(0).getCodDivisaSET();
										}
										else
										{
											psDivisa = "";
										}
										
										posicion = posicion + 1;
										psAuxiliar = psRegistro.substring(posicion, psRegistro.length());
										posicion = 0;											
										
										var = psAuxiliar.substring(posicion, psAuxiliar.indexOf(",")+ 3).replace(",", ".");
										
		/*								pdMonto = Double.parseDouble(var);										
										pdMonto = Float.parseFloat(psAuxiliar.substring(posicion, psAuxiliar.indexOf(",")+ 3).replace(",", "."));
										*/
										if (psCargoAbono.equals("I"))
										{
											totalIngresos = totalIngresos + pdMonto;
										}
										else if (psCargoAbono.equals("E"))
										{
											totalEgresos = totalEgresos + pdMonto;
										}
										
										posicion = psRegistro.indexOf(",") + 3;
										psAuxiliar = Integer.toString(psRegistro.indexOf(",") + 7);
										posicion = psRegistro.indexOf("//", posicion);
																				
										if (posicion != 0)
										{
											psReferencia = psRegistro.substring(Integer.parseInt(psAuxiliar), psRegistro.length());
										}
										else
										{
											posicion = psRegistro.indexOf("//", posicion);
											cuentaCaracteres = posicion - Integer.parseInt(psAuxiliar);
											psReferencia = psRegistro.substring(Integer.parseInt(psAuxiliar), cuentaCaracteres);
										}
																				
										/*
										posicion = psRegistro.indexOf("//");
										if (posicion > 0)
										{
											cuentaCaracteres = posicion - Integer.parseInt(psAuxiliar);
										}
										
										if (piBanco == CITIBANK)
										{
											psReferencia = psRegistro.substring(Integer.parseInt(psAuxiliar), cuentaCaracteres);
											if (!psReferencia.equals("NONREF"))
											{
												psFolioBanco = psReferencia;
												psReferencia = psRegistro.substring(Integer.parseInt(psAuxiliar), psRegistro.length());
												
												if(psReferencia.length() > 15)
												{
													psReferencia = psReferencia.substring(0, 14);
												}
											}
											psClaveBancaria = "";
											psConcepto = psClaveBancaria = "";
										}
										else if(piBanco == BANCOMER)
										{
											psReferencia = "";
											psClaveBancaria = psRegistro.substring(posicion + 2, psRegistro.length());
											psClaveBancaria = funciones.elmiminarEspaciosAlaDerecha(psClaveBancaria);
											psConcepto = psClaveBancaria;
										}
										 */
										
									}
									else
									{										
										iContador = iContador + 1;										
										lblRegistro = psDescBanco + " " + iContador;
										
										posicion = posicion + 1;
										psFechaOperacion = psRegistro.substring(posicion + 5, 8);
										psFechaOperacion = psFechaOperacion + "/" + psRegistro.substring(posicion + 7, 10);														
										psFechaOperacion = psFechaOperacion + "/" + psRegistro.substring(posicion + 3, 6);										
										psFechaOperacion = formatoFecha.format(Date.parse(psFechaOperacion));										
																				
										posicion = posicion + 13;
										psTipoMonto = psRegistro.substring(posicion, 15);
										
										if (funciones.isNumeric(psTipoMonto))
										{
											posicion = posicion + 4;
											psTipoMonto = psRegistro.substring(posicion, 19);
											
										}
										else
										{
											//psTipoMonto = psTipoMonto;
										}
										
										if (psTipoMonto.equals("C"))
											psCargoAbono = "I";
										else if(psTipoMonto.equals("D"))
											psCargoAbono = "E";
										
										posicion = posicion + 1;
										psDivisa = psRegistro.substring(posicion, 16);
										
										recibeDatos = administradorArchivosDao.selectEquivaleDivisa(psDivisa, "BE", true);
										if (recibeDatos.size() != 0)
											psDivisa = recibeDatos.get(0).getCodDivisaSET();
										else
											psDivisa = "";
																														
										if (piBanco == BANCOMER || piBanco == CITIBANK)
										{
											posicion = posicion + 1;
										}
										
										psAuxiliar = psRegistro.substring(posicion, psRegistro.length());
										posicion = 0;
										
										var = psAuxiliar.substring(posicion, psAuxiliar.indexOf(",", posicion) + 3).replace(",", ".");
										
										//pdMonto = Double.parseDouble(psAuxiliar.substring(posicion, psAuxiliar.indexOf(",", posicion)+ 3).replace(",", "."));
																				
										posicion = psRegistro.indexOf(",") + 3;
										
										//Falta validar si es por swift 
										if (piBanco != BANCOMER)
										{
											posicion = psRegistro.indexOf(",") + 7;
											psFolioBanco = psRegistro.substring(posicion, psRegistro.length());
										}
										else
										{
											psAuxiliar = Integer.toString(psRegistro.indexOf(",") + 7);
										}
										
										posicion = psRegistro.indexOf("//");
										if (posicion > 0)
										{
											cuentaCaracteres = posicion - Integer.parseInt(psAuxiliar);
										}
										
										if (piBanco == CITIBANK)
										{
											psReferencia = psRegistro.substring(Integer.parseInt(psAuxiliar), cuentaCaracteres);
											if (!psReferencia.equals("NONREF"))
											{
												psFolioBanco = psReferencia;
												psReferencia = psRegistro.substring(Integer.parseInt(psAuxiliar), psRegistro.length());
												
												if(psReferencia.length() > 15)
												{
													psReferencia = psReferencia.substring(0, 14);
												}
											}
											psClaveBancaria = "";
											psConcepto = psClaveBancaria = "";
										}
										else if(piBanco == BANCOMER)
										{
											psReferencia = "";
											psClaveBancaria = psRegistro.substring(posicion + 2, psRegistro.length());
											psClaveBancaria = funciones.elmiminarEspaciosAlaDerecha(psClaveBancaria);
											psConcepto = psClaveBancaria;
										}										
									}
								}//Termina el identificador 61
								
								if (psIdentificador.equals("86"))
								{									
									if (piBanco == CITIBANK)
									{
										psClaveBancaria = psRegistro.substring(posicion + 1, 8);
										psConcepto = psClaveBancaria;
									}
									else if (piBanco == SANTANDER || piBanco == BANAMEX)
									{										
										psClaveBancaria = psRegistro.substring(posicion + 4, 9);
										psConcepto = psClaveBancaria;
																	
										posicion = psRegistro.indexOf("//");
										if (posicion > 0)
										{
											psDescripcion = psRegistro.substring(posicion + 2, psRegistro.length());
											psDescripcion = funciones.elmiminarEspaciosAlaDerecha(psDescripcion);
										}										
									}
									else if(piBanco == BANCOMER)
									{
										if (psReferencia.equals(""))
										{
											psReferencia = psRegistro.substring(posicion + 4, psRegistro.length());
											psReferencia = funciones.elmiminarEspaciosAlaDerecha(psReferencia);
											psTipoReferencia = psReferencia.substring(0, 4);
																						
											if (psTipoReferencia.equals("CC00") || psTipoReferencia.equals("CB00") || psTipoReferencia.equals("CE00"))
											{
												psReferencia = psReferencia.substring(0, 22);
											}
										}
										
										psObservacion = psRegistro.substring(posicion + 4, psRegistro.length());
										psObservacion = funciones.elmiminarEspaciosAlaDerecha(psObservacion);
																				
										if (psReferencia.equals(""))
										{
											psReferencia = psFolioBanco;
										}
										
										if (psClaveBancaria.indexOf("CA9") > 0)
										{
											psFolioBanco = psRegistro.substring(29, 40);
											longitud = psFolioBanco.length();
											
											for (int i=0; i <= longitud; i++)
											{
												if (funciones.isNumeric(psFolioBanco.substring(0, 1)))
												{
													//psFolioBanco = psFolioBanco;
												}
												else
												{
													psFolioBanco = psFolioBanco.substring(1, psFolioBanco.length());
												}
											}
										}
									}
									else
									{
										psObservacion = psRegistro.substring(posicion + 4, psRegistro.length());
										if (psReferencia.equals(""))
										{
											psReferencia = psFolioBanco;
										}
										

										if (psClaveBancaria.indexOf("CA9") > 0)
										{
											psFolioBanco = psRegistro.substring(29, 40);
											longitud = psFolioBanco.length();
											
											for (int i=0; i <= longitud; i++)
											{
												if (funciones.isNumeric(psFolioBanco.substring(0, 1)))
												{
													//psFolioBanco = psFolioBanco;
												}
												else
												{
													psFolioBanco = psFolioBanco.substring(1, psFolioBanco.length());
												}
											}
										}
									}
									
									psChequeraAnt = psChequera;
									pbInserta = true;						
								}
								//Termina el identificador 86
								
								if (pbInserta == true && pbFinalSolo == false)
								{																		
									if (psFolioBanco.length() > 0)
									{
										for (int i=0; i<= psFolioBanco.length(); i++)
										{										
											if (psFolioBanco.substring(0, 1).equals("0"))
												psFolioBanco = psFolioBanco.substring(1, psFolioBanco.length());
										}
										
										posicionBlanco = psFolioBanco.indexOf(" ");
										if (posicionBlanco > 0)
											psFolioBanco = psFolioBanco.substring(0, posicionBlanco);
									}
																		
									
									String var2 = "";
									if (piBanco == 14)
									{
										psConcepto = psConcepto.replace("-", "");
										var2 = var;
									}
									else
									{										
									//	var2 = Double.toString(pdMonto);
										var2 = var;
									}								
									
									recibeDatos = administradorArchivosDao.selectMovtoBancaE(psFolioBanco, psConcepto,
																							psReferencia, piNoEmpresa, 
																							psChequera, psFechaOperacion, var2);
														
									if (recibeDatos.size() <= 0)
									{
										pbArchivoOk = true;
										piFolio = seleccionarFolioReal("secuencia");
										
										if (psDescripcion.length() > 60)
										{
											psDescripcion = psDescripcion.substring(0, 60);
										}
																				
										pdRubro = 0;
										if (!psConcepto.equals(""))
										{
											recibeDatos = administradorArchivosDao.selectRubro(psConcepto);
											
											if (recibeDatos.size() != 0)
												pdRubro = recibeDatos.get(0).getIdRubro();
											else
												pdRubro = 0;
										}
										
										if (psConcepto.length() > 60)
										{
											psConcepto = psConcepto.substring(0, 59);											
										}
										else
											psConcepto = funciones.elmiminarEspaciosAlaDerecha(psConcepto);
										
										if (psObservacion.length() > 230)
										{
											psObservacion = psObservacion.substring(0, 229);
										}
										else
											psObservacion = funciones.elmiminarEspaciosAlaDerecha(psObservacion);
										
										
										if (psReferencia.length() > 40)
											psReferencia = psReferencia.substring(0, 39);
										else
											psReferencia = funciones.elmiminarEspaciosAlaDerecha(psReferencia);
																									
										//inserta en movto_banca_e
										objInsertaDto.setNoEmpresa(piEmpresa);
										objInsertaDto.setIdBanco(piBanco);
										objInsertaDto.setIdChequera(psChequera);
										objInsertaDto.setSecuencia(piFolio);
										objInsertaDto.setFechaOperacion(psFechaOperacion);
										objInsertaDto.setSucursal("");
										objInsertaDto.setFolioBanco(psFolioBanco);
										objInsertaDto.setReferencia(psReferencia);
										objInsertaDto.setCargoAbono(psCargoAbono);
										objInsertaDto.setImporte(Double.parseDouble(var2));
										objInsertaDto.setBSalvoBuenCobro("");
										objInsertaDto.setConcepto(psConcepto);
										objInsertaDto.setIdEstatusTrasp("");
										objInsertaDto.setFecAlta(administradorArchivosDao.obtenerFechaHoyBase());
										objInsertaDto.setUsuarioAlta(idUsuario);
										objInsertaDto.setDescripcion(psDescripcion);
										objInsertaDto.setArchivo(psArchivo);										
										objInsertaDto.setObservacion(psObservacion);
										objInsertaDto.setSaldoBancario(0);
										objInsertaDto.setIdRubro(pdRubro);
										objInsertaDto.setNoLineaArch(cuentaLinea);
																				
										int resultado = 0;
										
										resultado = administradorArchivosDao.insertarMovtoBancaE(objInsertaDto);
										
										switch (piBanco)
										{
										case 2:
											importados2 = importados2 + 1;
											break;
										case 12:
											importados12 = importados12 + 1;
											break;
										case 14:
											importados14 = importados14 + 1;
											break;
										case 1026:
											importados1026 = importados1026 + 1;
											break;
										}
										
										psReferencia = "";
										psConcepto = "";
										psDescripcion = "";
										psObservacion = "";
										psFolioBanco = "";
										psClaveBancaria = "";
										psCargoAbono = "";
																																					
									}
								}//Termina el if de la insercion en MovtoBancaE
																
								pbInserta = false;								
							}
						
						}
						administradorArchivosDao.actualizarFechaBanca(psArchivo, piBanco);								
						
						archivo.close();
						archivo = null;	
						i++;
					}
/*					archivo.close();
					archivo = null;*/		
				}
				
				busquedaArchivos.cambiarExtension(nombres, ".tmp",".old");
				
				respuesta = "";
				if (!psChequera2.equals(""))
				{					
					respuesta += "\n\n" + psDescBanco2;
					respuesta += "\n" + psChequera2;
					respuesta += "\n" + "Registros Leidos: " + piRegLeidos2;
					respuesta += "\n" + "Registros Importados: " + importados2;
				}
				if (!psChequera12.equals(""))
				{
					respuesta += "\n\n" + psDescBanco12;
					respuesta += "\n" + psChequera12;
					respuesta += "\n" + "Registros Leidos: " + piRegLeidos12;
					respuesta += "\n" + "Registros Importados: " + importados12;
				}
				if (!psChequera14.equals(""))
				{
					respuesta += "\n\n" + psDescBanco14;
					respuesta += "\n" + psChequera14;
					respuesta += "\n" + "Registros Leidos: " + piRegLeidos14;
					respuesta += "\n" + "Registros Importados: " + importados14;
				}
				if (!psChequera1026.equals(""))
				{
					respuesta += "\n\n" + psDescBanco1026;
					respuesta += "\n" + psChequera1026;
					respuesta += "\n" + "Registros Leidos: " + piRegLeidos1026;
					respuesta += "\n" + "Registros Importados: " + importados1026;
				}
				
				//Confirmacion de movimientos enviados por H2H
				recibeDatos = administradorArchivosDao.selectMovtoBanca(administradorArchivosDao.obtenerFechaHoyBase());
				
				if (recibeDatos.size() != 0)
				{
					System.out.println("si encuentra info");
					for (int i = 0; i <= recibeDatos.size(); i++)
					{
						administradorArchivosDao.confirmaMovimientos(recibeDatos, i);											
					}
				}
			}							
		}		
		catch(Exception e)
		{			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectronica, M:BancaElectronicaBusiness, M:lecturaMT940");
			respuesta = "";
		}return respuesta;
	}
	
	@SuppressWarnings("unused")
	public boolean leeBanorte(ParametroLayoutDto dto) {
		String sRuta = "";
		String lsChequera = "";
		String psFecEdoCta = "";
		String lsFolio = "";
		String llImporte = "";
		String lsReferencia = "";
		String lsSucursal = "";
		String lsEstatus = "";
		String lsFechaPago = "";
		String lsConcepto = "";
		String psDescripcion = "";
		String psSBCobro = "";
		String psCargoAbono = "";
		String psInserts = "";
		
		boolean lbExisteChequera = false;
		boolean leeBanorte = false;
		
		double dSaldoBancario = 0;
		
		List<CatCtaBancoDto> empresas = new ArrayList<CatCtaBancoDto>();
		
		int op = -1;
		int iRegSinChequera = 0;
		int plRegImportados = 0;
		
		List<MovtoBancaEDto> movtoBancaE = new ArrayList<MovtoBancaEDto>();
		bEmpresa = new ParametroBuscarEmpresaDto();
		inicializar();
		psChequerasInexistentes = "";
		
		sRuta = administradorArchivosDao.consultarConfiguraSet(201) + "\\banorte\\";
		
		if(!sRuta.equals(rutaActual))
			System.setProperty("user.dir", sRuta);
		
		File[] nombresArch = busquedaArchivos.obtenerNombreArchivos(".txt");
		logger.info("archivos:" + nombresArch.length);
		mensajes.add("\nBanorte");
		
		/*liContador = liContador + 1
            plRegLeidos = liContador
            lblregistro = "BANORTE " & liContador
            
            lsTipoReg = Mid(lsRegistro, 1, 1)
*/
		if(nombresArch != null) {
			while(z < nombresArch.length) {  // hasta que no encuentres archivos
				nombresArch = busquedaArchivos.cambiarExtension(nombresArch, ".txt", ".tmp");
				
				try {
					archivo = new Scanner(nombresArch[z]);
				}catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				psArchivo = nombresArch[z].getName().replace(".tmp", ".txt");
				
				if(psArchivos.equals(""))
					psArchivos = psArchivo;
				else
					psArchivos += "', '" + psArchivo;
				
				psInserts = "";
				piEncontrados = 0;
				
				while(archivo.hasNext()) {//hasNext // hasta que no haya lineas que leer
					logger.info( z + ".- Archivo: " + psArchivo);
					logger.info(" Chequera: " + (lsChequera == null ? "No hay Chequera" : lsChequera));
					logger.info(" No Empresa: " + piNoEmpresa);
					
					psRegistro = archivo.nextLine();
					
					if(!psRegistro.trim().equals("")) {
						plRegLeidos++;
						psRegistro = psRegistro.replace("'", " ");
	                    logger.info("Registro: " + psRegistro + "\n*********************");
	                    
	                    if(funciones.isNumeric(psRegistro.substring(0, 1)))
	                		op = Integer.parseInt(psRegistro.substring(0, 1));
	                	
	                	switch(op) {
	                    	case 0:
	                    		lsChequera = psRegistro.substring(19, 30);
	                    		psFecEdoCta = psRegistro.substring(12, 14) + "/" + psRegistro.substring(10, 12) + "/" + psRegistro.substring(6, 10);
	                    		
	                    		bEmpresa = new ParametroBuscarEmpresaDto();
	                    		bEmpresa.setPsChequera(lsChequera);
	                    		bEmpresa.setPiBanco(BANORTE);
	                    		bEmpresa.setPbSwiftMT940MN(false);
	                    		empresas = administradorArchivosDao.buscaEmpresaNetCash(bEmpresa);
	                    		
	                    		if(empresas.size() > 0) {
	                    			piNoEmpresa = empresas.get(0).getNoEmpresa();
	                    			lbExisteChequera = true;
	                    		}else {
	                    			lbExisteChequera = false;
	                    			iRegSinChequera++;
	                    			psChequerasInexistentes += (!psChequerasInexistentes.equals("") ? ", " : "") + lsChequera;
	                    		}
	                    	break;
	                    	case 2:
	                    		if(!lbExisteChequera) {
	                    			iRegSinChequera++;
	                    			continue;
	                    		}else {
	                    			lsFolio = psRegistro.substring(1, 16);
			                        llImporte = "" + Double.parseDouble(psRegistro.substring(49, 63)) / 100; //Val(Mid$(lsRegistro, 50, 14)) / 100
			                        lsReferencia = psRegistro.substring(63, 83);
			                        lsSucursal = psRegistro.substring(151, 155);
			                        lsEstatus = psRegistro.substring(161, 162);
			                        lsFechaPago = psRegistro.substring(171, 173) + "/" + psRegistro.substring(169, 171) + "/" + psRegistro.substring(165, 169);
			                        lsConcepto = psRegistro.substring(83, 103);
			                        psDescripcion = psRegistro.substring(83, 103);
			                        psSBCobro = "N";
			                        psCargoAbono = psRegistro.substring(164, 165);
			                        psCargoAbono = psCargoAbono.equals("0") ? "I" : "E";
			                        
			                        parametro = new ParametroLayoutDto();
		                            parametro.setPsFolioBanco(lsFolio);
		                            parametro.setPsConcepto(lsConcepto);
				                    parametro.setPsReferencia(lsReferencia);
				                    parametro.setPlNoEmpresa(piNoEmpresa);
				                    parametro.setPsIdChequera(lsChequera);
				                    parametro.setPsFecValor(lsFechaPago);
				                    parametro.setPdImporte(Double.parseDouble(llImporte));
				                    parametro.setPdSaldo(0);
				                    parametro.setPlNoLineaArchivo(0);
				                    parametro.setPsCargoAbono(psCargoAbono);
				                    parametro.setPiBanco(BANORTE);
				                    movtoBancaE = administradorArchivosDao.seleccionarMovtoBancaE(parametro);
				                    
				                    if(movtoBancaE.size() <= 0) {
				                    	plFolio = seleccionarFolioReal("secuencia");
				                    	
				                    	if (plFolio <= 0) {
				                    		archivo.close();
				                    		
				                    		for (int h = z; h < nombresArch.length; h++)
				                    			busquedaArchivos.cambiarExtensionSoloUno(nombresArch[h], ".tmp",".txt");
				                    		return false;
				                    	}
				                    	leeBanorte = true;
				                    	
				                    	insercion = new ParametroLayoutDto();
										insercion.setPlNoEmpresa(piNoEmpresa);
										insercion.setPiBanco(BANORTE);
										insercion.setPsIdChequera(lsChequera);
										insercion.setPsSecuencia(plFolio);
										insercion.setPsFecValor(lsFechaPago);
										insercion.setPsSucursal(lsSucursal);
										insercion.setPsFolioBanco(lsFolio);
										insercion.setPsReferencia(lsReferencia);
										insercion.setPsCargoAbono(psCargoAbono);
										insercion.setPdImporte(Double.parseDouble(llImporte));
										insercion.setPsBSalvoBuenCobro(psSBCobro);
										insercion.setPsConcepto(lsConcepto);
										insercion.setPsUsuarioAlta(idUsuario);
										insercion.setPsDescripcion(psDescripcion);
										insercion.setPsArchivos(psArchivo);
										insercion.setPsFechaHoy(funciones.ponerFechaSola(obtenerFechaHoy()));
										insercion.setPsObservacion("");
										insercion.setPsCveConcepto("");
										insercion.setPdSaldoBancario(0);
										insercion.setPsEstatus("");
										insercion.setPlNoLineaArchivo(0);
										psInserts = psInserts + administradorArchivosDao.insertarMovtoBancaEStrNet(insercion); 
		                                plRegImportados++;
		                                piEncontrados++;
				                    }
	                    		}
	                    	break;
	                    	case 4:
	                    		if(!lbExisteChequera) {
	                    			plRegSinChequera++;
	                    			continue;
	                    		}else {
	                    			dSaldoBancario = Double.parseDouble(psRegistro.substring(67, 81)) / 100;
	                    			
	                    			parametro = new ParametroLayoutDto();
		                            parametro.setPsFolioBanco("");
		                            parametro.setPsConcepto("SALDO ULTIMA TRANS");
				                    parametro.setPsReferencia("");
				                    parametro.setPlNoEmpresa(piNoEmpresa);
				                    parametro.setPsIdChequera(lsChequera);
				                    parametro.setPsFecValor(psFecEdoCta);
				                    parametro.setPdImporte(0);
				                    parametro.setPdSaldo(dSaldoBancario);
				                    parametro.setPlNoLineaArchivo(0);
				                    parametro.setPsCargoAbono("");
				                    parametro.setPiBanco(BANORTE);
				                    movtoBancaE = administradorArchivosDao.seleccionarMovtoBancaE(parametro);
				                    
				                    if(movtoBancaE.size() <= 0) {
				                    	plFolio = seleccionarFolioReal("secuencia");
				                    	
				                    	if (plFolio <= 0) {
				                    		archivo.close();
				                    		
				                    		for (int h = z; h < nombresArch.length; h++)
				                    			busquedaArchivos.cambiarExtensionSoloUno(nombresArch[h], ".tmp",".txt");
				                    		return false;
				                    	}
				                    	leeBanorte = true;
				                    	
				                    	insercion = new ParametroLayoutDto();
										insercion.setPlNoEmpresa(piNoEmpresa);
										insercion.setPiBanco(BANORTE);
										insercion.setPsIdChequera(lsChequera);
										insercion.setPsSecuencia(plFolio);
										insercion.setPsFecValor(psFecEdoCta);
										insercion.setPsSucursal("");
										insercion.setPsFolioBanco("");
										insercion.setPsReferencia("");
										insercion.setPsCargoAbono("S");
										insercion.setPdImporte(0);
										insercion.setPsBSalvoBuenCobro("");
										insercion.setPsConcepto("SALDO ULTIMA TRANS");
										insercion.setPsUsuarioAlta(idUsuario);
										insercion.setPsDescripcion("");
										insercion.setPsArchivos(psArchivo);
										insercion.setPsFechaHoy(funciones.ponerFechaSola(obtenerFechaHoy()));
										insercion.setPsObservacion("");
										insercion.setPsCveConcepto("");
										insercion.setPdSaldoBancario(dSaldoBancario);
										insercion.setPsEstatus("");
										insercion.setPlNoLineaArchivo(0);
										psInserts = psInserts + administradorArchivosDao.insertarMovtoBancaEStrNet(insercion); 
		                                plRegImportados++;
		                                piEncontrados++;
				                    }
	
	                    		}
	                    	break;
	                    }
					}
                }
                if(!psInserts.equals(""))
	                administradorArchivosDao.ejecutaSentencia(psInserts);
                
                psInserts = "";
				dto.setPsArchivos(psArchivos);
				dto.setPlRegLeidos(plRegLeidos);
				dto.setPlRegSinChequera(iRegSinChequera);
				dto.setPsChequerasInexistentes(psChequerasInexistentes);
				dto.setPlRegImportados(plRegImportados);
				archivo.close();
				archivo = null;
				
				if(piEncontrados == plRegLeidos - 1)
					busquedaArchivos.cambiarExtensionSoloUno(nombresArch[z], ".tmp", ".old");
				z++;
			} //while(z < nombresArch.length)
		}// if(nombres != null)
		mensajes.add("\nRegistros Leidos:  " + dto.getPlRegLeidos());
		mensajes.add("\nRegistros sin chequera: " + dto.getPlRegSinChequera());
		mensajes.add(dto.getPsChequerasInexistentes());
		mensajes.add("\nRegistros Importados:  " + dto.getPlRegImportados());
		logger.info("leeB: " + leeBanorte);
		
		return leeBanorte;
        
		/* aqui despues de leer hay que detectar y marcar los rechazados para que no se ocupen al confirmar en semiautomatico
	       esto en egresos confirmacion de transferencias 
		Call gobjSQL.FunSQLUpdateRechazados(BANORTE)
		ProcesaRechazados BANORTE */
	}
	
	@SuppressWarnings("unused")
	public boolean leeBanortePipe(ParametroLayoutDto dto) {
		String sRuta = "";
		String psChequera = "";
		String psFechaValor = "";
		String psReferencia = "";
		String psDescripcion = "";
		String psCodigoTrans = "";
		String psSucursal = "";
		String psDepositos = "";
		String psRetiros = "";
		String psSaldo = "";
		String psMovimiento = "";
		String psDescripcionLarga = "";
		String psCargoAbono = "";
		String psInserts = "";
		
		boolean lbExisteChequera = false;
		boolean leeBanorte = false;
		boolean bHeader = true;
		
		List<CatCtaBancoDto> empresas = new ArrayList<CatCtaBancoDto>();
		
		int iRegSinChequera = 0;
		int plRegImportados = 0;
		
		List<MovtoBancaEDto> movtoBancaE = new ArrayList<MovtoBancaEDto>();
		bEmpresa = new ParametroBuscarEmpresaDto();
		inicializar();
		psChequerasInexistentes = "";
		
		sRuta = administradorArchivosDao.consultarConfiguraSet(201) + "\\banorte\\";
		
		if(!sRuta.equals(rutaActual))
			System.setProperty("user.dir", sRuta);
		
		File[] nombresArch = busquedaArchivos.obtenerNombreArchivos(".txt");
		mensajes.add("\nBanorte");
		
		if(nombresArch == null) {
			mensajes.add("\nRegistros Leidos: 0 ");
			mensajes.add("\nRegistros sin chequera: 0");
			mensajes.add("\nRegistros Importados: 0 ");
			return leeBanorte;
		}
		
		while(z < nombresArch.length) {  // hasta que no encuentres archivos
			nombresArch = busquedaArchivos.cambiarExtension(nombresArch, ".txt", ".tmp");
			
			try {
				archivo = new Scanner(nombresArch[z]);
			}catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			psArchivo = nombresArch[z].getName().replace(".tmp", ".txt");
			
			if(psArchivos.equals(""))
				psArchivos = psArchivo;
			else
				psArchivos += "', '" + psArchivo;
			
			psInserts = "";
			piEncontrados = 0;
			
			otroReg:
			while(archivo.hasNext()) {//hasNext // hasta que no haya lineas que leer
				
				psRegistro = archivo.nextLine();
				
				if(!psRegistro.trim().equals("") && !bHeader) {
					plRegLeidos++;
					psRegistro = psRegistro.replace("'", " ").replace("$", "");
					psChequera = funciones.ajustarLongitudCampo(psRegistro.substring(0, psRegistro.indexOf("|")), 11, "D", "", "0");
					psRegistro = psRegistro.substring(psRegistro.indexOf("|") + 1);
					psFecha = psRegistro.substring(0, psRegistro.indexOf("|"));
					psRegistro = psRegistro.substring(psRegistro.indexOf("|") + 1);
					psFechaValor = psRegistro.substring(0, psRegistro.indexOf("|"));
					psRegistro = psRegistro.substring(psRegistro.indexOf("|") + 1);
					
					if(!funciones.isDate(psFechaValor, false)) { 
						leeBanorte = false;
						break;
					}
					psReferencia = psRegistro.substring(0, psRegistro.indexOf("|"));
					psRegistro = psRegistro.substring(psRegistro.indexOf("|") + 1);
					psDescripcion = psRegistro.substring(0, psRegistro.indexOf("|"));
					psRegistro = psRegistro.substring(psRegistro.indexOf("|") + 1);
					psCodigoTrans = psRegistro.substring(0, psRegistro.indexOf("|"));
					psRegistro = psRegistro.substring(psRegistro.indexOf("|") + 1);
					psSucursal = psRegistro.substring(0, psRegistro.indexOf("|"));
					psRegistro = psRegistro.substring(psRegistro.indexOf("|") + 1);
					psDepositos = psRegistro.substring(0, psRegistro.indexOf("|")).replace(",", "");
					psRegistro = psRegistro.substring(psRegistro.indexOf("|") + 1);
					psRetiros = psRegistro.substring(0, psRegistro.indexOf("|")).replace(",", "");
					psRegistro = psRegistro.substring(psRegistro.indexOf("|") + 1);
					psSaldo = psRegistro.substring(0, psRegistro.indexOf("|")).replace(",", "");
					psRegistro = psRegistro.substring(psRegistro.indexOf("|") + 1);
					psMovimiento = psRegistro.substring(0, psRegistro.indexOf("|"));
					psRegistro = psRegistro.substring(psRegistro.indexOf("|") + 1);
					psDescripcionLarga = psRegistro;
					
					bEmpresa = new ParametroBuscarEmpresaDto();
            		bEmpresa.setPsChequera(psChequera);
            		bEmpresa.setPiBanco(BANORTE);
            		bEmpresa.setPbSwiftMT940MN(false);
            		empresas = administradorArchivosDao.buscaEmpresaNetCash(bEmpresa);
            		
            		if(empresas.size() > 0) {
            			piNoEmpresa = empresas.get(0).getNoEmpresa();
            			lbExisteChequera = true;
            		}else {
            			lbExisteChequera = false;
            			iRegSinChequera++;
            			psChequerasInexistentes += (!psChequerasInexistentes.equals("") ? ", " : "") + psChequera;
            			continue otroReg;
            		}
            		psCargoAbono = "S";
            		
            		if(Double.parseDouble(psDepositos) != 0) {
            			psCargoAbono = "I";
            			pdImporte = Double.parseDouble(psDepositos);
            		}else if(Double.parseDouble(psRetiros) != 0) {
            			psCargoAbono = "E";
            			pdImporte = Double.parseDouble(psRetiros);
            		}
            		parametro = new ParametroLayoutDto();
                    parametro.setPsFolioBanco(psMovimiento);
                    parametro.setPsConcepto(psDescripcion);
                    parametro.setPsReferencia(psReferencia);
                    parametro.setPlNoEmpresa(piNoEmpresa);
                    parametro.setPsIdChequera(psChequera);
                    parametro.setPsFecValor(psFechaValor);
                    parametro.setPdImporte(pdImporte);
                    parametro.setPdSaldo(Double.parseDouble(psSaldo.equals("") ? "0" : psSaldo));
                    parametro.setPlNoLineaArchivo(0);
                    parametro.setPsCargoAbono(psCargoAbono);
                    parametro.setPiBanco(BANORTE);
                    movtoBancaE = administradorArchivosDao.seleccionarMovtoBancaE(parametro);
                    
                    if(movtoBancaE.size() <= 0) {
                    	plFolio = seleccionarFolioReal("secuencia");
                    	
                    	if (plFolio <= 0) {
                    		archivo.close();
                    		
                    		for (int h = z; h < nombresArch.length; h++)
                    			busquedaArchivos.cambiarExtensionSoloUno(nombresArch[h], ".tmp",".txt");
                    		return false;
                    	}
                    	leeBanorte = true;
                    	//Inicia para no cargar dias anteriores a ayer
                    	/*bImporta = 0;
                    	
                    	if(administradorArchivosDao.consultarConfiguraSet(1014).equals("SI"))
                    		bImporta = 1;
                    	else if(administradorArchivosDao.consultarConfiguraSet(1014).equals("NO"))
                    		bImporta = 2;
                    	*/
                    	if(pdImporte != 0 && !psCargoAbono.equals("S")) {
                    		//If CDate(psFecAyer) = CDate(ps_Fecha_valor) And (b_importa = 1 Or b_importa = 2) Then
                    			
                    		//end
	                    	insercion = new ParametroLayoutDto();
	                    	insercion.setPlNoEmpresa(piNoEmpresa);
	                    	insercion.setPiBanco(BANORTE);
	                    	insercion.setPsIdChequera(psChequera);
	                    	insercion.setPsSecuencia(plFolio);
	                    	insercion.setPsFecValor(psFechaValor);
	                    	insercion.setPsSucursal(psSucursal);
	                    	insercion.setPsFolioBanco(psMovimiento);
	                    	insercion.setPsReferencia(psReferencia);
							insercion.setPsCargoAbono(psCargoAbono);
							insercion.setPdImporte(pdImporte);
							insercion.setPsBSalvoBuenCobro("");
							insercion.setPsConcepto(psDescripcion);
							insercion.setPsUsuarioAlta(idUsuario);
							insercion.setPsDescripcion(psDescripcionLarga);
							insercion.setPsArchivos(psArchivo);
							insercion.setPsFechaHoy(funciones.ponerFechaSola(obtenerFechaHoy()));
							insercion.setPsObservacion("");
							insercion.setPsCveConcepto(psCodigoTrans);
							insercion.setPdSaldoBancario(Double.parseDouble(psSaldo.equals("") ? "0" : psSaldo));
	                    	insercion.setPsEstatus("");
							insercion.setPlNoLineaArchivo(0);
							psInserts = psInserts + administradorArchivosDao.insertarMovtoBancaEStrNet(insercion); 
                            plRegImportados++;
                            piEncontrados++;
                    	}
                    }
				}
				bHeader = false;
			}
            if(!psInserts.equals(""))
                administradorArchivosDao.ejecutaSentencia(psInserts);
            
            psInserts = "";
			dto.setPsArchivos(psArchivos);
			dto.setPlRegLeidos(plRegLeidos);
			dto.setPlRegSinChequera(iRegSinChequera);
			dto.setPsChequerasInexistentes(psChequerasInexistentes);
			dto.setPlRegImportados(plRegImportados);
			archivo.close();
			archivo = null;
			bHeader = true;
			
			if(piEncontrados == plRegLeidos)
				busquedaArchivos.cambiarExtensionSoloUno(nombresArch[z], ".tmp", ".old");
			z++;
		} //while(z < nombresArch.length)
		mensajes.add("\nRegistros Leidos:  " + dto.getPlRegLeidos());
		mensajes.add("\nRegistros sin chequera: " + dto.getPlRegSinChequera());
		mensajes.add(dto.getPsChequerasInexistentes());
		mensajes.add("\nRegistros Importados:  " + dto.getPlRegImportados());
		logger.info("leeB: " + leeBanorte);
		
		return leeBanorte;
        
		/* aqui despues de leer hay que detectar y marcar los rechazados para que no se ocupen al confirmar en semiautomatico
	       esto en egresos confirmacion de transferencias 
		Call gobjSQL.FunSQLUpdateRechazados(BANORTE)
		ProcesaRechazados BANORTE */
	}
	
	@SuppressWarnings("unused")
	public boolean leeInverlat(ParametroLayoutDto dto)
	{
		boolean leeInverlat = false;
		boolean existeChequera = false;
		int i = 0;
		int cuentaLinea = 0;
		int noEmpresa = 0;		
		int piRegistrosSinChequera = 0;
		int insertado = 0;
		int plRegImportados = 0;
		int posicion = 0;
		int posicionCadena = 0;
		String psDirectorio = "";
		String psArchivo = "";
		String psRegistro = "";
		String psFechaValor = "";		
		String psChequerasInexistentes = "";
		String psSBCobro = "";
		String psFolioBanco = "";
		String psSucursal = "";
		String psCargoAbono = "";
		String psReferencia = "";
		String psConcepto = "";
		String psDescripcion = "";
		String cadena = "";
		String campoString = "";
		double pdImporte = 0;
		double pdSaldo = 0;
		List<MovtoBancaEDto> recibeDatos = new ArrayList<MovtoBancaEDto>();
		funciones = new Funciones();
		busquedaArchivos = new BusquedaArchivos();
		rutaActual = null;
		SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
		
		try
		{
			psDirectorio = administradorArchivosDao.consultarConfiguraSet(201) + "\\INVERLAT\\";
			mensajes.add("\n Inverlat");
			
			if (psDirectorio.equals(""))
			{				
				leeInverlat = false;
			}
			else
			{				
				if (!psDirectorio.equals(rutaActual))				
					System.setProperty("user.dir", psDirectorio);
								
				File [] nombres = busquedaArchivos.obtenerNombreArchivos(".txt");
																				
				if (nombres != null)
				{
					while (i < nombres.length)
					{
						cuentaLinea = 0;
						try
						{
							nombres = busquedaArchivos.cambiarExtension(nombres, ".txt", ".tmp");
							archivo = new Scanner(nombres[i]);
						}
						catch(FileNotFoundException e)
						{
							e.printStackTrace();
						}
						
						psArchivo = nombres[i].getName();
						
						if (!psArchivo.equals(""))
						{							
							siguienteLinea:
							while (archivo.hasNext()) //Lee el archivo hasta la ultima lines
							{
								cuentaLinea = cuentaLinea + 1;							
								psRegistro = archivo.nextLine();								
																									
								if (psRegistro.length() > 0 && !psRegistro.equals(""))									
								{
									System.out.println(psRegistro);
									psRegistro.replace("'", "");
									
									//Fecha valor del archivo
									posicion = psRegistro.indexOf("\t");
									System.out.println(posicion);
									psChequera = psRegistro.substring(0, posicion);
									
									cadena = psRegistro.substring(posicion + 1);
									
									psChequeraNueva = psChequera + funciones.ajustarLongitudCampo(cadena.substring(0, cadena.indexOf("\t")), 8, "D", "", "0");
											
									if (!psChequeraNueva.equals(psChequera))
									{
										psChequera = psChequeraNueva;
										recibeDatos = administradorArchivosDao.buscaBancoChequera(psChequera);
										
										if (recibeDatos.size() != 0)
										{
											noEmpresa = recibeDatos.get(0).getNoEmpresa();
											existeChequera = true;												
										}
										else
										{
											existeChequera = false;
											piRegistrosSinChequera ++;
											psChequerasInexistentes += (!psChequerasInexistentes.equals("") ? ", " : "") + psChequeraNueva;
											continue siguienteLinea;
										}											
									}
									
									//Fecha valor
									cadena = cadena.substring(cadena.indexOf("\t") + 1);
									
									psFechaValor = cadena.substring(0, cadena.indexOf("\t"));
									psFechaValor = psFechaValor.replace("/", "-");																	
										
									//Referencia / folioBanco
									cadena = cadena.substring(cadena.indexOf("\t") + 1);									
									psReferencia = cadena.substring(0, cadena.indexOf("\t"));
									
									psFolioBanco = psReferencia;
									
									//Importe
									cadena = cadena.substring(cadena.indexOf("\t") + 1);
									campoString = cadena.substring(0, cadena.indexOf("\t"));
									campoString = campoString.replace(",", "");

									pdImporte = Double.parseDouble(campoString);
										
									//Cargo - Abono									
									cadena = cadena.substring(cadena.indexOf("\t") + 1);
									campoString = cadena.substring(0, cadena.indexOf("\t"));

									if (campoString.equals("ABONO"))
									{
										psCargoAbono = "I";
										
										cadena = cadena.substring(cadena.indexOf("\t") + 1);
										psDescripcion = cadena.substring(0, cadena.indexOf("\t"));
										if (psDescripcion.equals("DEPOSITO SALVO BUEN COBRO"))											
											psSBCobro = "N";											
										else
											psSBCobro = "S";
										
										
									}
									else
									{
										
										cadena = cadena.substring(cadena.indexOf("\t") + 1);
										psDescripcion = cadena.substring(0, cadena.indexOf("\t"));
										psCargoAbono = "E";
										psSBCobro = "";
									}	
									
									psSucursal = "";
									psConcepto = psDescripcion;
																			
									//Saldo															
									cadena = cadena.substring(cadena.indexOf("\t") + 1);									
									campoString = cadena.replace(",", "");
									
									pdSaldo = Double.parseDouble(campoString);
																		
									parametro = new ParametroLayoutDto();
		                            parametro.setPsFolioBanco(psFolioBanco);
		                            parametro.setPsConcepto(psConcepto);
				                    parametro.setPsReferencia(psReferencia);
				                    parametro.setPlNoEmpresa(noEmpresa);
				                    parametro.setPsIdChequera(psChequera);
				                    parametro.setPsFecValor(psFechaValor);
				                    parametro.setPdImporte(pdImporte);
				                    parametro.setPdSaldo(0);
				                    parametro.setPlNoLineaArchivo(cuentaLinea);
				                    parametro.setPsCargoAbono(psCargoAbono);
				                    parametro.setPiBanco(INVERLAT);
									recibeDatos = administradorArchivosDao.seleccionarMovtoBancaE(parametro);
									
									if (recibeDatos.size() <= 0)
									{
										plFolio = seleccionarFolioReal("secuencia");
				                    	
				                    	leeInverlat = true;
				                    						                    				                    
				                    	insercion = new ParametroLayoutDto();
										insercion.setPlNoEmpresa(noEmpresa);
										insercion.setPiBanco(INVERLAT);
										insercion.setPsIdChequera(psChequera);
										insercion.setPsSecuencia(plFolio);
										insercion.setPsFecValor(psFechaValor);
										insercion.setPsSucursal(psSucursal);
										insercion.setPsFolioBanco(psFolioBanco);
										insercion.setPsReferencia(psReferencia);
										insercion.setPsCargoAbono(psCargoAbono);
										insercion.setPdImporte(pdImporte);
										insercion.setPsBSalvoBuenCobro(psSBCobro);
										insercion.setPsConcepto(psConcepto);
										insercion.setPsEstatus("");
										insercion.setPsFecAlta(funciones.ponerFechaSola(obtenerFechaHoy()));											
										insercion.setPsUsuarioAlta(idUsuario);
										insercion.setPsDescripcion(psDescripcion);
										insercion.setPsArchivo(psArchivo);
										insercion.setPsObservacion("");
										insercion.setPsCveConcepto("");
										insercion.setPdSaldoBancario(pdSaldo);
										insercion.setPlNoLineaArchivo(cuentaLinea);
																					
										int resultado = 0;											
										resultado = administradorArchivosDao.insertarMovtoBancaE(insercion);
																					
										dto.setPlRegImportados((dto.getPlRegImportados() + 1));
		                                piEncontrados++;
									}
									//}//temina else de un renglon
									
								}//Termina if que valida si trae info el renglon
								
							}//Termina while que incrementa las lineas en el archivo
						}//Termina el if que valida que el vector de archivos no este vacio
						
						administradorArchivosDao.actualizarFechaBanca(psArchivo, INVERLAT);								
						
						archivo.close();
						archivo = null;						
						nombres = busquedaArchivos.cambiarExtension(nombres, ".tmp", ".old");
						i++;
						
						//mensajes.add("Registros Leidos:  " + dto.getPlRegLeidos());
						mensajes.add("Registros Leidos:  " + cuentaLinea);
						mensajes.add("Registros sin chequera: " + piRegistrosSinChequera);
						//mensajes.add(dto.getPsChequerasInexistentes());
						mensajes.add("Registros Importados:  " + dto.getPlRegImportados());
						logger.info("leeB: "+ leeInverlat);
											
						return leeInverlat;
					} //Termina while de vector de nombres
					
				}
			}
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectronica, M:AdministradorArchivosBusiness, M:leeInverlat");
			return false;
		}
		return leeInverlat;
	}
	
	public int confirmacionAutomatica(String fecHoy) {
		List<MovtoBancaEDto> recibeDatos = new ArrayList<MovtoBancaEDto>();
		int resultado = 0;
		
		try{
			//Confirmacion de movimientos enviados por H2H
			recibeDatos = administradorArchivosDao.selectMovtoBanca(administradorArchivosDao.obtenerFechaHoyBase());
			
			if (recibeDatos.size() != 0) {
				//Confirmacion automatica de cheques
				resultado = administradorArchivosDao.confirmacionAutomaticaCheques(recibeDatos, recibeDatos.size());
				
				//Confirmacion automatica de transfer
				resultado = administradorArchivosDao.confirmacionAutomatica(recibeDatos, recibeDatos.size());
			}
			
//			buscaIngresosMail();
			
		}
		catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, M: AdministradorArchivosBusiness, M: conrmacionAutomatica");
		}return resultado;
	}
	
	public void buscaIngresosMail() {
		List<MovtoBancaEDto> recibeDatos = new ArrayList<MovtoBancaEDto>();
		
		try{
			recibeDatos = administradorArchivosDao.selectMovtoBancaIngresos(administradorArchivosDao.obtenerFechaHoyBase());
			
			if (recibeDatos.size() != 0) 
				enviaMailIngresos(recibeDatos);
		}
		catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, M: AdministradorArchivosBusiness, M: buscaIngresosMail");
		}
	}
	
	public void enviaMailIngresos(List<MovtoBancaEDto> datos) {
		List<MovtoBancaEDto> listCuerpoMail = new ArrayList<MovtoBancaEDto>();
		List<MovtoBancaEDto> listReceptor = new ArrayList<MovtoBancaEDto>();
		List<MovtoBancaEDto> listUsrCopia = new ArrayList<MovtoBancaEDto>();
		List<MovtoBancaEDto> listCopia = new ArrayList<MovtoBancaEDto>();
		
		String mailReceptor;
		String mailCopia;
		String asunto;
		String cuerpo = "";
		
		funciones = new Funciones();
		
		try{
			listCuerpoMail = administradorArchivosDao.cuerpoMail();
			
			listReceptor = administradorArchivosDao.mailsReceptores(0);
			
			listUsrCopia = administradorArchivosDao.usuarioMailsCopia();
			
			listCopia = administradorArchivosDao.mailsCopia(listUsrCopia.get(0).getCorreosCopia());
			
			if(listReceptor.size() <= 0){
				mailReceptor = listCopia.get(0).getCorreosCopia();
				mailCopia = "";
			}else{
				mailReceptor = listReceptor.get(0).getCorreoElectronico();
				mailCopia = listCopia.get(0).getCorreosCopia();
			}
			asunto = listCuerpoMail.get(0).getAvisoMail();
			
			for(int i=0; i<datos.size(); i++) {
				cuerpo = armaMailsIngresos(listReceptor, listCuerpoMail, datos, i);
				funciones.enviaMail(mailReceptor, mailCopia, asunto, cuerpo);
			}
		}
		catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, M: AdministradorArchivosBusiness, M: enviaMailIngresos");
		}
	}
	
	public String armaMailsIngresos(List<MovtoBancaEDto> listReceptor, List<MovtoBancaEDto> listMail, 
			List<MovtoBancaEDto> datos, int i) {
		
		String cuerpo = "";
		
		try {
			cuerpo = listMail.get(0).getIntroMail() + "\n\n";
			cuerpo += "EMPRESA BENEFICIARIA:" + "  " + datos.get(i).getNomEmpresa() + "\n\n";
			cuerpo += listMail.get(0).getBancoMail() + "  " + datos.get(i).getDescBanco() + "\n";
			cuerpo += listMail.get(0).getCuentaMail() + "  " + datos.get(i).getIdChequera() + "\n";
			cuerpo += listMail.get(0).getConceptoMail() + " "+ datos.get(i).getConcepto() + "\n";
			cuerpo += listMail.get(0).getImporteMail() + " " + datos.get(i).getImporte() + "\n";
			cuerpo += listMail.get(0).getDescFormaPago() + "  " + datos.get(i).getDescFormaPago() + "\n";
			cuerpo += listMail.get(0).getReferenciaMail() + "  " + datos.get(i).getReferencia() + "\n";
			cuerpo += listMail.get(0).getDivisaMail() + " " + datos.get(i).getIdDivisa() + "\n";
			cuerpo += listMail.get(0).getCaracterMail() + "\n\n";
			cuerpo += listMail.get(0).getDespedidaMail();
			
			//cuerpo += listMail.get(0).getBeneficMaill() + " " + datos.get(0).getBeneficiario() + "\n";
			//cuerpo += listMail.get(0).getNoFacturaMail() + " " + datos.get(0).getNoFactura() + "\n";
			//cuerpo += listMail.get(0).getTipoCambioMail() + " " + datos.get(0).getTipoCambio() + "\n\n";
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectronica, C:AdministradorArchivosDao, M: armaMails");
			return "";
		}
		return cuerpo;
	}

	
	@SuppressWarnings("unused")
	public String eliminaArchivos(){
		String mensaje = "";		
		File lastModifiedFile = null;
		String psDirectorio = "";
		int contadorDias = 0;
		
		busquedaArchivos = new BusquedaArchivos();
		
		try{
			
			//Este ciclo se coloco para que tome las diferentes rutas de los bancos
			for (int banco = 1; banco <= 5; banco++){
				
				switch (banco)
				{
					case 1: //Bancomer
						psDirectorio = administradorArchivosDao.consultarConfiguraSet(201) + "\\bancomer\\";
						break;
					case 2: //Banamex
						psDirectorio = administradorArchivosDao.consultarConfiguraSet(201) + "\\banamex\\";
						break;
					case 3: //Santander
						psDirectorio = administradorArchivosDao.consultarConfiguraSet(201) + "\\santander\\";
						break;
					case 4: //City
						psDirectorio = administradorArchivosDao.consultarConfiguraSet(201) + "\\citibank_dls\\";
						break;
					case 5: //Inverlat
						psDirectorio = administradorArchivosDao.consultarConfiguraSet(201) + "\\inverlat\\";
						break;
				}	
							
				System.setProperty("user.dir", psDirectorio);			
				File [] nombres = busquedaArchivos.obtenerNombreArchivos(".old");
							
				if (nombres != null)
				{
					int i = 0;
					while (i < nombres.length)
					{							
						lastModifiedFile = nombres[i];						
						long fechaModif = nombres[i].lastModified();
						
						Date d = new Date(fechaModif);
						Calendar c = new GregorianCalendar();
						c.setTime(d);
									
						Date fecHoy = new Date();
						Calendar ch = new GregorianCalendar();
						ch.setTime(fecHoy);
																	
						if (!nombres[i].equals("")){				
							contadorDias = Integer.parseInt(administradorArchivosDao.consultarConfiguraSet(400));													
						}
						
						//Las fechas se convierten en milisegundos
						long mili1 = c.getTimeInMillis();
						long mili2 = ch.getTimeInMillis();
						
						//Se restan los milisegundos
						long difMilisegundos = mili2 - mili1;
												
						//Calcula la diferencia en dias
						long difDias = difMilisegundos / (24 * 60 * 60 * 1000);
												
						if (difDias >= contadorDias){						
							nombres[i].delete();							
							mensaje = "Archivos eliminados";
						}										
						i++;					
					}
				}
				else
					mensaje = "No existen archivos";
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: AdministradorArchivosBusiness, M: eliminaArchivos");
		}return mensaje;
	}
	
	public static void main(String []args){
		System.out.println(System.getProperty("user.dir"));
		XmlBeanFactory beanFactory = new XmlBeanFactory(new FileSystemResource("C:\\proyectos\\webset\\workspace\\SET\\WebRoot\\WEB-INF\\applicationContext.xml"));
		AdministradorArchivosBusiness obj = (AdministradorArchivosBusiness) beanFactory.getBean("administradorArchivosBusiness");
		ParametroLayoutDto dto = new ParametroLayoutDto();
		int importados = 0;
		int total=0;
		String mensajes="";
		String mensajesUsuario="";
		obj.setLbReferenciaPorChequera(true);
		obj.setLbReferenciaNumerica(false);
		obj.setLiLongitudReferencia(16);
		obj.setIdUsuario(2);
		dto.setPsArchivos("");
		dto.setPlRegLeidos(0);
		dto.setPlRegSinChequera(0);
		dto.setPlRegImportados(0);
		dto.setPsChequerasInexistentes("");
		//obj.leerBancomer(dto);
		if(obj.leerBancomer(dto)){
			if (!dto.getPsArchivos().equals(""))
				obj.actualizarFechaBanca(dto.getPsArchivos(), obj.BANCOMER);
			else
				obj.actualizarFechaBanca("", obj.BANCOMER);
		}
		importados += dto.getPlRegImportados();
	    total+=importados;
		importados=0;
		for (int p = 0; p < obj.mensajes.size(); p++)
			if (obj.mensajes.get(p)!=null&&!obj.mensajes.get(p).equals(""))
				mensajes += obj.mensajes.get(p) + "\n";
		mensajes += "\nTotal Importados: " + total;
		if(obj.mensajesUsuario.size()>1){
			mensajesUsuario += "\n\nMesanjes de Usuario \n";
			for (int i = 0; i < obj.mensajesUsuario.size(); i++)
				mensajesUsuario += obj.mensajesUsuario.get(i) + "\n";
		}
		logger.info("//////////////////////////////////");
		logger.info(mensajes.toString());
		logger.info(mensajesUsuario.toString());
	}
	
	public void leeEdosCtaAuto() {
		int z = 0;
		@SuppressWarnings("unused")
		int total = 0;
		int longitud = 9;
		int importados = 0;
		int vec[]= new int[1];
		
		boolean referenciaC = false;
		boolean referenciaN = false;
		
		try {
			AdministradorArchivosBusiness obj = new AdministradorArchivosBusiness();
			ParametroLayoutDto dto = new ParametroLayoutDto();
			
			globalSingleton = GlobalSingleton.getInstancia();
			
			obj.setLbReferenciaPorChequera(referenciaC);
			obj.setLbReferenciaNumerica(referenciaN);
			obj.setLiLongitudReferencia(longitud);
			obj.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
			
			vec[z] = 12;
			
			do{
				dto.setPsArchivos("");
				dto.setPlRegLeidos(0);
				dto.setPlRegSinChequera(0);
				dto.setPlRegImportados(0);
				dto.setPsChequerasInexistentes("");
				
				switch(vec[z]){
					case ConstantesSet.BANCOMER:
						/* Esta lectura es la de CashWindows
						 * if(obj.leerBancomer(dto)){
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.BANCOMER);
							else
								obj.actualizarFechaBanca("", obj.BANCOMER);
						}*/
						
						if(leeNetCashC43(dto)){
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.BANCOMER);
							else
								obj.actualizarFechaBanca("", obj.BANCOMER);
						}
						importados += dto.getPlRegImportados();
					    total += importados;
						importados=0;
						break;
					case ConstantesSet.SANTANDER:
						if(obj.leerSantander(dto)){
					    	if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.SANTANDER);
							else
								obj.actualizarFechaBanca("", obj.SANTANDER);
					    }
					    importados += dto.getPlRegImportados();
					    
					    dto.setPsArchivos("");
						dto.setPlRegLeidos(0);
						dto.setPlRegSinChequera(0);
						dto.setPlRegImportados(0);
						dto.setPsChequerasInexistentes("");
					    
						if(obj.leerSantanderBuzon(dto)){
					    	if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.SANTANDER);
							else
								obj.actualizarFechaBanca("", obj.SANTANDER);
					    }
						importados += dto.getPlRegImportados();
						
						dto.setPsArchivos("");
						dto.setPlRegLeidos(0);
						dto.setPlRegSinChequera(0);
						dto.setPlRegImportados(0);
						dto.setPsChequerasInexistentes("");
					    
						if(obj.leerSantanderTodasChequeras(dto)){
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.SANTANDER);
							else
								obj.actualizarFechaBanca("", obj.SANTANDER);
					    }
						importados += dto.getPlRegImportados();
						
						dto.setPsArchivos("");
						dto.setPlRegLeidos(0);
						dto.setPlRegSinChequera(0);
						dto.setPlRegImportados(0);
						dto.setPsChequerasInexistentes("");
						importados = 0;
						break;
				}
				z++;
			}while(z<vec.length);
			
			confirmacionAutomatica(funciones.ponerFechaSola(administradorArchivosDao.obtenerFechaHoy()));
			
		}catch(Exception e){ e.printStackTrace(); }
	}
	
	public boolean leerCitibankDlsProcedimiento(ParametroLayoutDto dto) throws IOException{
		boolean sigueLeyendo = false;
		boolean siguienteChequera=false;
		int piBanco;
		int secuencia = 0;
		int plRegImportados = 0;
		String psCitibank = "";
		String psFolioBanco = "";
		String psIdentificador = "";
		String psAuxiliar = "";
		String psDato = "";
		String psDatoFecha = "";
		String psSigno = "";
		int rutaComp;
		int linea;
		String result = "";
		String psrutauno = "";
		String psrutados = "";
		inicializar();
		SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yy");

		//psrutauno = administradorArchivosDao.consultarRutaUno(1);
		//psrutados = administradorArchivosDao.consultarRutaDos(1);
		
		//File origen = new File(psrutauno);
		//File destino = new File(psrutados);
		
		/*File []archivos = origen.listFiles();
		for(File path:archivos){
			FileUtils.copyDirectory(origen,destino);		
		}*/
		
		
		z=0;
		psCitibank = administradorArchivosDao.consultarConfiguraSet(201)+"\\citibank_mn\\";
		if(!psCitibank.equals(rutaActual))
			System.setProperty("user.dir", psCitibank);
		File[] nombres = busquedaArchivos.obtenerNombreArchivos(".txt");
		if(nombres!=null){
			//while (i<nombres.length)
			//{
			mensajes.add("\n CITIBANK DLS");
			CatCtaBancoDto catCtaBancoDto = new CatCtaBancoDto();
			MovtoBancaEDto movtoBancaEDto = new MovtoBancaEDto();
			catCtaBancoDto.setIdBanco(2);
			movtoBancaEDto.setFecAlta(administradorArchivosDao.obtenerFechaHoyBase());
			movtoBancaEDto.setUsuarioAlta(idUsuario);
			
			while(z<nombres.length)
			{
				rutaComp = administradorArchivosDao.insertRutaCompleta(nombres[z]);//Se agreega esta linea para que se inserte la ruta en la base de datos
				nombres = busquedaArchivos.cambiarExtension(nombres, ".txt", ".tmp");
				try {
					archivo = new Scanner(nombres[z]);
				}
				catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				psArchivo=nombres[z].getName().replace(".tmp", ".txt");
				if (psArchivos.equals(""))
					psArchivos = psArchivo;
				else
					psArchivos += "', '" + psArchivo;
				movtoBancaEDto.setArchivo(psArchivo);
				piEncontrados = 0;
				do{
					if(sigueLeyendo)
						sigueLeyendo=false;
					while(archivo.hasNext()){
						while(archivo.hasNext()){
	                        psRegistro=archivo.nextLine();
	                        linea = administradorArchivosDao.insertLinea(psRegistro);
						}
						if(!archivo.hasNext())
							break;
					}
				}while(sigueLeyendo);
				archivo.close();
				archivo=null;
				busquedaArchivos.cambiarExtensionSoloUno(nombres[z], ".tmp",".old");
				
				
			int res = administradorArchivosDao.selecionarMovtoBancaEProcedimiento();
				
				if(res==1)
					result = "Procedimiento ejecutado correctamente";
				else if(res==-1)
					result = "Ocurrio un error al ejecutar el procedimiento";
				
				
				z++;
				administradorArchivosDao.limpiarTabla();
			}
			
			int cons = administradorArchivosDao.obtenerResultado();
			System.out.println(cons);
			dto.setPlRegSinChequera(cons);
			
			int guarda = administradorArchivosDao.obtenerGuardados();
			System.out.println(guarda);
			dto.setPlRegImportados(guarda);
			dto.setPlRegLeidos(guarda);
			
			mensajes.add("Registros Leidos:  " + dto.getPlRegLeidos());
			mensajes.add("Registros sin chequera: " + dto.getPlRegSinChequera());
			//mensajes.add("Chequeras que no existen: \n"+dto.getPsChequerasInexistentes()+"\n");
			//mensajes.add("Chequeras que no existen: \n"+dto.getPsChequerasInexistentes2()+"\t");
			mensajes.add("Registros Importados:  " + dto.getPlRegImportados());
			
			List<String> chequera = administradorArchivosDao.obtenerChequeras();
			String salida="";
			System.out.println(chequera);
			 mensajes.add("Chequeras que no existen: \n");
			for (String s:chequera){
				//s.split("  ");
				 salida += s; 
				 //System.out.println("soy "+s);
				 dto.setPsChequerasInexistentes(s);
			    mensajes.add(dto.getPsChequerasInexistentes());
				 
				
			}
			
		 
			//dto.setPlRegImportados(guarda);
			//dto.setPlRegLeidos(guarda);
			
			
		}
		
		return true;
		
		
	}

	public void leerCitibankDlsProcedimientoBanamex(){
		boolean sigueLeyendo = false;
		boolean siguienteChequera=false;
		int piBanco;
		int secuencia = 0;
		int plRegImportados = 0;
		String psCitibank = "";
		String psFolioBanco = "";
		String psIdentificador = "";
		String psAuxiliar = "";
		String psDato = "";
		String psDatoFecha = "";
		String psSigno = "";
		int rutaComp;
		int linea;
		String result = "";
		String psrutauno = "";
		String psrutados = "";
		inicializar();
		SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yy");
		
		z=0;
		psCitibank = administradorArchivosDao.consultarConfiguraSet(201)+"\\citibank_mn\\";
		if(!psCitibank.equals(rutaActual))
			System.setProperty("user.dir", psCitibank);
		File[] nombres = busquedaArchivos.obtenerNombreArchivos(".txt");
		if(nombres!=null){
			mensajes.add("\n CITIBANK DLS");
			CatCtaBancoDto catCtaBancoDto = new CatCtaBancoDto();
			MovtoBancaEDto movtoBancaEDto = new MovtoBancaEDto();
			catCtaBancoDto.setIdBanco(2);
			movtoBancaEDto.setFecAlta(administradorArchivosDao.obtenerFechaHoyBase());
			movtoBancaEDto.setUsuarioAlta(idUsuario);
			
			while(z<nombres.length)
			{
				rutaComp = administradorArchivosDao.insertRutaCompleta(nombres[z]);//Se agreega esta linea para que se inserte la ruta en la base de datos
				nombres = busquedaArchivos.cambiarExtension(nombres, ".txt", ".tmp");
				try {
					archivo = new Scanner(nombres[z]);
				}
				catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				psArchivo=nombres[z].getName().replace(".tmp", ".txt");
				if (psArchivos.equals(""))
					psArchivos = psArchivo;
				else
					psArchivos += "', '" + psArchivo;
				movtoBancaEDto.setArchivo(psArchivo);
				piEncontrados = 0;
				do{
					if(sigueLeyendo)
						sigueLeyendo=false;
					while(archivo.hasNext()){
						while(archivo.hasNext()){
	                        psRegistro=archivo.nextLine();
	                        linea = administradorArchivosDao.insertLinea(psRegistro);
						}
						if(!archivo.hasNext())
							break;
					}
				}while(sigueLeyendo);
				archivo.close();
				archivo=null;
				busquedaArchivos.cambiarExtensionSoloUno(nombres[z], ".tmp",".old");
				
				
				int res = administradorArchivosDao.selecionarMovtoBancaEProcedimiento();
				
				if(res==1)
					result = "Procedimiento ejecutado correctamente";
				else if(res==-1)
					result = "Ocurrio un error al ejecutar el procedimiento";
				
				
				z++;
				administradorArchivosDao.limpiarTabla();
			}
			
		}
		//return true;
		administradorArchivosDao.limpiarTablaBanamex();
	}
	
	public void consultarConfiguraSetdatosMantenimientoMovimiento() {
		
		String retorno = null;
		
		
		try {
			retorno= administradorArchivosDao.consultarConfiguraSet(1);
			
			if(retorno.equals("DALTON") ){
				datosMantenimientoMovimiento();
				
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
	
	
	
	public void datosMantenimientoMovimiento(){
		int res = administradorArchivosDao.DatosMantenimientoMovimiento();
		String result = "";
		
		System.out.println("hola");
		
		if(res==1)
			result = "Procedimiento ejecutado correctamente";
		else if(res==-1)
			result = "Ocurrio un error al ejecutar el procedimiento";
		
		
	}
	
	
	
	public <s> boolean leerbancomerProcedimiento(ParametroLayoutDto dto) throws IOException{
		boolean sigueLeyendo = false;
		boolean siguienteChequera=false;
		int piBanco;
		int secuencia = 0;
		int plRegImportados = 0;
		String psCitibank = "";
		String psFolioBanco = "";
		String psIdentificador = "";
		String psAuxiliar = "";
		String psDato = "";
		String psDatoFecha = "";
		String psSigno = "";
		int rutaComp;
		int linea;
		String result = "";
		String psrutauno = "";
		String psrutados = "";
		inicializar();
		SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yy");

		//psrutauno = administradorArchivosDao.consultarRutaUno(1);
		//psrutados = administradorArchivosDao.consultarRutaDos(1);
		
		//File origen = new File(psrutauno);
		//File destino = new File(psrutados);
		
		/*File []archivos = origen.listFiles();
		for(File path:archivos){
			FileUtils.copyDirectory(origen,destino);		
		}*/
		
		
		z=0;
		psCitibank = administradorArchivosDao.consultarConfiguraSet(201)+"\\bancomer\\netCashC43\\";
		if(!psCitibank.equals(rutaActual))
			System.setProperty("user.dir", psCitibank);
		File[] nombres = busquedaArchivos.obtenerNombreArchivos(".txt");
		if(nombres!=null){
			//while (i<nombres.length)
			//{
			mensajes.add("\n BANCOMER");
			CatCtaBancoDto catCtaBancoDto = new CatCtaBancoDto();
			MovtoBancaEDto movtoBancaEDto = new MovtoBancaEDto();
			catCtaBancoDto.setIdBanco(2);
			movtoBancaEDto.setFecAlta(administradorArchivosDao.obtenerFechaHoyBase());
			movtoBancaEDto.setUsuarioAlta(idUsuario);
			
			while(z<nombres.length)
			{
				rutaComp = administradorArchivosDao.insertRutaCompletaBancomer(nombres[z]);//Se agreega esta linea para que se inserte la ruta en la base de datos
				nombres = busquedaArchivos.cambiarExtension(nombres, ".txt", ".tmp");
				try {
					archivo = new Scanner(nombres[z]);
				}
				catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				psArchivo=nombres[z].getName().replace(".tmp", ".txt");
				if (psArchivos.equals(""))
					psArchivos = psArchivo;
				else
					psArchivos += "', '" + psArchivo;
				movtoBancaEDto.setArchivo(psArchivo);
				piEncontrados = 0;
				do{
					if(sigueLeyendo)
						sigueLeyendo=false;
					while(archivo.hasNext()){
						while(archivo.hasNext()){
	                        psRegistro=archivo.nextLine();
	                        linea = administradorArchivosDao.insertLineaBancomer(psRegistro);
						}
						if(!archivo.hasNext())
							break;
					}
				}while(sigueLeyendo);
				archivo.close();
				archivo=null;
				busquedaArchivos.cambiarExtensionSoloUno(nombres[z], ".tmp",".old");
				
				
				int res = administradorArchivosDao.selecionarMovtoBancaEProcedimientoBancomer();
				
				if(res==1)
					result = "Procedimiento ejecutado correctamente";
				else if(res==-1)
					result = "Ocurrio un error al ejecutar el procedimiento";
				
				
				z++;
				
				administradorArchivosDao.limpiarTablaBancomer();
			}
			
			int cons = administradorArchivosDao.obtenerResultado();
			System.out.println("Sin chequera");
			System.out.println(cons);
			dto.setPlRegSinChequera(cons);
			
			int guarda = administradorArchivosDao.obtenerGuardados();
			System.out.println("Con chequera");
			System.out.println(guarda);
			dto.setPlRegImportados(guarda);
			dto.setPlRegLeidos(guarda);
			
			mensajes.add("Registros Leidos:  " + dto.getPlRegLeidos());
			mensajes.add("Registros sin chequera: " + dto.getPlRegSinChequera());
			//mensajes.add("Chequeras que no existen: \n"+dto.getPsChequerasInexistentes()+"\n");
			//mensajes.add("Chequeras que no existen: \n"+dto.getPsChequerasInexistentes2()+"\t");
			mensajes.add("Registros Importados:  " + dto.getPlRegImportados());
			
			List<String> chequera = administradorArchivosDao.obtenerChequeras();
			String salida="";
			System.out.println(chequera);
			 mensajes.add("Chequeras que no existen: \n");
			for (String s:chequera){
				//s.split("  ");
				 salida += s; 
				 //System.out.println("soy "+s);
				 dto.setPsChequerasInexistentes(s);
			    mensajes.add(dto.getPsChequerasInexistentes());
				 
				
			}
			int rs=0;
			administradorArchivosDao.limpiarTablasBancomer();
		
			//dto.setPlRegImportados(guarda);
			//dto.setPlRegLeidos(guarda);
			
			
		}
		
		return true;
		
		
	}

	
	

	// getters and setters
	public AdministradorArchivosDao getAdministradorArchivosDao() {
		return administradorArchivosDao;
	}
	

	public void setAdministradorArchivosDao(
			AdministradorArchivosDao administradorArchivosDao) {
		this.administradorArchivosDao = administradorArchivosDao;
	}	
	// getters and setters de Variables Globales
	public boolean isLbReferenciaPorChequera() {
		return lbReferenciaPorChequera;
	}

	public void setLbReferenciaPorChequera(boolean lbReferenciaPorChequera) {
		this.lbReferenciaPorChequera = lbReferenciaPorChequera;
	}

	public int getLiLongitudReferencia() {
		return liLongitudReferencia;
	}

	public void setLiLongitudReferencia(int liLongitudReferencia) {
		this.liLongitudReferencia = liLongitudReferencia;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public boolean isLbReferenciaNumerica() {
		return lbReferenciaNumerica;
	}

	public void setLbReferenciaNumerica(boolean lbReferenciaNumerica) {
		this.lbReferenciaNumerica = lbReferenciaNumerica;
	}
}
