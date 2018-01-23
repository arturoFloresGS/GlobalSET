package com.webset.set.interfaz.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;

import com.google.gson.Gson;
import com.webset.set.interfaz.dao.CargaPagosDao;
import com.webset.set.interfaz.dto.CargaPagosDto;
import com.webset.set.interfaz.service.CargaPagosService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.BusquedaArchivos;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.utils.tools.Utilerias;

@SuppressWarnings("unchecked")
public class CargaPagosBusinessImpl implements CargaPagosService
{
	String psRuta = "";
	File[] archivos;
	JdbcTemplate jdbcTemplate;
	BusquedaArchivos buscaArchivo;
	ConsultasGenerales consultasGenerales;
	Scanner archivo = null;
	Funciones funciones;
	int i=0;
	String psArchivo = "";
	String psRegistro = "";
	String rutaActual;
	int recibeDatosEnteros = 0;
		
	private CargaPagosDao objCargaPagosDao;
	private CargaPagosDto objCargaPagosDto;
	//CargaPagosBusinessImpl objCargaPagosBusiness;
	CargaPagosDto objCargaPagosRegresaCamposDto;
	GeneraLayoutH2H objGeneraLayout;
	Bitacora bitacora;
	
	GlobalSingleton globalSingleton;
	
	//Logger
	private Logger logger = Logger.getLogger(CargaPagosBusinessImpl.class);
	
	//Metodo para llenar el combo de las empresas
	public List<CargaPagosDto> llenaComboEmpresas(int noUsuario)
	{
		return objCargaPagosDao.llenaComboEmpresas(noUsuario);
	}
	
	public List<CargaPagosDto> llenaGrid(int iTipoPago, String fecHoy, int usuarioAlta, String noEmpresaParam, String estatus, String fecIni, String fecFin) {
		List<CargaPagosDto> result = new ArrayList<CargaPagosDto>();
		
		try {
			switch (iTipoPago) {
				case 1:
					result = llenaGridSAP(iTipoPago, fecHoy, usuarioAlta, noEmpresaParam);
					break;
				case 2:
					result = llenaGridSAE(iTipoPago, fecHoy, usuarioAlta, noEmpresaParam, estatus, fecIni, fecFin);
					break;
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosBusinessImpl, M:llenaGrid");
		}
		return result;
	}
	
	//Metodo para la lectura de los ERP desde un archivo .txt
	public List<CargaPagosDto> llenaGridSAP(int iTipoPago, String fecHoy, int usuarioAlta, String noEmpresaParam)
	{
		List<CargaPagosDto> recibeDatos = new ArrayList<CargaPagosDto>();
		int noEmpresa = 0;
		String noDocto = "";
		String secuencia = "";
		String fecValor = "";
		String noBenef = "";
		double importe = 0;
		String divisa = "";
		String concepto = "";
		String idFormaPago = "";		
		String origen = "";
		String grupoRubro = "";
		String rubro = "";
		String mandante = "";
		String profitCenter = "";
		String centroCostos = "";
		String rfc = "";
		String curp = "";
		int idCaja = 0;
		String clabe = "";
		String descPropuesta = "";
		String chequeraBenef = "";
		int bancoBenef = 0;
		String nombreBancoBenef = "";
		int sucursalBenef = 0;
		String paisBancoBenef = "";
		String cdBancoBenef = "";
		String swiftBenef = "";
		String iban = "";
		String forFurtherCredit = "";
		String nombreBancoIntermediario = "";
		String aba = "";
		String paisBancoIntermediario = "";
		String cdBancoIntermediario = "";
		String chequeraPagadora = "";
		int bancoPagador = 0;
		String apellidoPaterno = "";
		String apellidoMaterno = "";
		String nombre = "";
		String razonSocial = "";
		String email = "";
		String direccionBenef = "";
		String cdBenef = "";
		String regionBenef = "";
		String cpBenef = "";	
		String paisBenef = "";
		String localizacion = "";
		int noCheque = 0;
		String nomArchivo = "";
		String causaRechazo = "";
		String validaCadena = "";
		String sTipoPersona = "";		
		String houseBank = "";
		String descripcionPropuesta = "";
		String tipoDocumento = "";
		String conceptoRechazo = "";
		int nuevoBanco = 0;
		int totalRegistros = 0;
		int totalOK = 0;
		int totalError = 0;
		double totalImporteMN = 0;
		double totalImporteDLS = 0;
		boolean esNumero = false;
		String fecHoyString = "";
		int valorNumerico = 0;
				
		rutaActual = null;
		
		rutaActual = System.getProperty("user.dir");
		List<CargaPagosDto> registrosLeidos = new ArrayList<CargaPagosDto>();
		
		buscaArchivo = new BusquedaArchivos();
		funciones = new Funciones();
				
		//archivos = new File[4];
		
		if (noEmpresaParam.equals(""))
			noEmpresa = 0;
		else
			noEmpresa = Integer.parseInt(noEmpresaParam);
		
		switch (iTipoPago)
		{			
			case 1://Archivos tipo SAP		
				//Aqui falta la validacion por si es diferente la ruta dl archivo dependiendo el noEmpresa
				
				
				psRuta = objCargaPagosDao.consultarConfiguraSet(386) + "\\SAP\\ACCOUNTS_PAYABLES\\INBOUND\\";
								
				if (!psRuta.equals(rutaActual))
					System.setProperty("user.dir", psRuta);					
				
				File[] archivos = buscaArchivo.obtenerNombreArchivos(".txt");				
							
				if (archivos != null)
				{
					while (i < archivos.length)
					{							
						try
						{								
							archivos = buscaArchivo.cambiarExtension(archivos, ".txt", ".tmp");
							archivo = new Scanner(archivos[i]);
						}						
						catch(FileNotFoundException e)
						{						
							e.printStackTrace();
						}
						
						psArchivo = archivos[i].getName();
						
						if (!psArchivo.equals("")) 
						{
							while (archivo.hasNext()) //Se usa la variable de tipo Scanner y Lee el archivo hasta la ultima linea
							{									
								psRegistro = archivo.nextLine();	
								
								//Entra cuando es un registro de pago 
								if ((psRegistro.substring(0,1)).equals("D"))
								{	
									//Valida la Empresa(4)
									if (funciones.isNumeric(psRegistro.substring(1,5)))										
									{										
										noEmpresa = Integer.parseInt(psRegistro.substring(1,5));
									}
									else
									{										
										noEmpresa = 0;
									}									
									
									//Origen de propuesta														
									origen = "SAP";										
									
									//Numero de documento del registro (10)
									noDocto = psRegistro.substring(5,15);
									
									//secuencia de archivo (8)
									validaCadena = "";
									validaCadena = psRegistro.substring(518,526);										
									secuencia = validaCadena.substring(0, 4) + validaCadena.substring(4,8);
									
									//concepto de propuestas (50)
									concepto = psRegistro.substring(31,81);										
									
									//importe(7)									
									importe = Double.parseDouble(psRegistro.substring(83,99)) / 100;
									
									//Divisa(3)
									divisa = psRegistro.substring(99,102);
									
									//Fecha valor(8)
									validaCadena = psRegistro.substring(518,526);
									fecValor = validaCadena.substring(6,8) + "/" + validaCadena.substring(4,6) + "/" + validaCadena.substring(0,4);
									
									//Id Forma de Pago(1)
									idFormaPago = psRegistro.substring(30,31);																	
									
									//No Beneficiario(16)
									noBenef = funciones.elmiminarEspaciosAlaDerecha(psRegistro.substring(104,114));
									
									//Nombre Benef cuando es Razon Social(80)									
									nombre = funciones.elmiminarEspaciosAlaDerecha(psRegistro.substring(114,194));
									if (nombre.length() > 30)
									{
										nombre = nombre.substring(0, 29);
									}
									
									//Razon Social
									razonSocial = funciones.elmiminarEspaciosAlaDerecha(psRegistro.substring(114,194));
																		
									//RFC (16)
									rfc =funciones.elmiminarEspaciosAlaDerecha(psRegistro.substring(194,210));
									
									if (rfc != "")
									{
										if (rfc.length() >= 4)
										{
											if (funciones.isNumeric(rfc.substring(3,3)))
											{
												sTipoPersona = "M";
												nombre = "";
											}
											else
											{
												if (nombre.indexOf(" ") > -1)
												{	
													//Nombre de Benef
													validaCadena = nombre;
													validaCadena = validaCadena.substring(1, validaCadena.indexOf(" "));
													apellidoPaterno = validaCadena;
													
													validaCadena = nombre.substring(nombre.indexOf(" ") + 1, nombre.length());
													validaCadena = validaCadena.substring(1, validaCadena.indexOf(" "));														
													apellidoMaterno = validaCadena;
													nombre = validaCadena.substring(validaCadena.indexOf(" ") + 1, validaCadena.length());																			
												}
												if (nombre.length() > 30)
												{
													nombre = nombre.substring(0, 29);
												}
												sTipoPersona = "F";
											}
										}
									}								
									
									//CURP (18)
									curp = psRegistro.substring(210,228);
									
									//Numero de cheque (5)
									noCheque = Integer.parseInt(psRegistro.substring(23,28));
									
									//Chequera Benef (18)
									chequeraBenef = psRegistro.substring(228, 246);
									
									//Clabe (18)
									clabe = psRegistro.substring(228, 246);
									
									//Descripcion de la propuesta
									validaCadena = psRegistro.substring(28, 30);
									descPropuesta = validaCadena;
									validaCadena = psRegistro.substring(526, 532);
									descPropuesta = descPropuesta + validaCadena;
									
									
									//Mid(ps_cadena, 29, 2) + Mid(ps_cadena, 527, 6)
									
									//Banco Benef (15)
									bancoBenef = Integer.parseInt(psRegistro.substring(286, 301));
									
									//Valida si la chequera es mismo banco o interbancaria
									if (bancoBenef == 12 && Integer.parseInt(chequeraBenef.substring(0,7)) == 0)
									{
										chequeraBenef = "91" + chequeraBenef.substring(6, 18);
									}
									
									//Sucursal Benef (4)
									sucursalBenef = Integer.parseInt(psRegistro.substring(282, 286));
									
									//Nombre del banco benef (60)
									nombreBancoBenef = funciones.quitarCaracteresRaros(psRegistro.substring(301, 361), true);
									
									//Pais del banco benef (3)
									paisBancoBenef = psRegistro.substring(361, 364);
									
									//Ciudad del banco benef (3)
									cdBancoBenef = psRegistro.substring(364, 367);
									
									//Swift del banco benef (11)
									swiftBenef = psRegistro.substring(367, 378);
									
									//IBAN del banco benef (34)
									iban = psRegistro.substring(378, 412);
									
									//forFurtherCredit (20)
									forFurtherCredit = psRegistro.substring(412, 432);
									
									//Banco intermediario (60)
									nombreBancoIntermediario = psRegistro.substring(432, 492);
									
									//ABA (15)
									aba = psRegistro.substring(492, 507);
									
									//Pais de banco intermediario (3)
									paisBancoIntermediario = psRegistro.substring(507, 510);
									
									//Ciudad de banco intermediario (3)
									cdBancoIntermediario = psRegistro.substring(510, 513);
									
									//No de caja (2)
									idCaja = Integer.parseInt(psRegistro.substring(81, 83));
									
									//Grupo de tesoreria
									//grupoRubro = psRegistro.substring(29, 2);
									grupoRubro = "6000";
									rubro = "6001";
									
									//HouseBank (5)
									houseBank = psRegistro.substring(513, 518);
									
									//mail (50)
									email = psRegistro.substring(532, 582);
									
									//Direccion del beneficiario (35)
									direccionBenef = funciones.quitarCaracteresRaros(psRegistro.substring(582, 617), true);
									
									//Ciudad del beneficiario (35)
									cdBenef = psRegistro.substring(617, 652);
									if (cdBenef.length() > 30)
									{
										cdBenef = cdBenef.substring(0, 29);
									}
									
									//Region del beneficiario (3)
									regionBenef = psRegistro.substring(652, 655);
									
									//Codigo postal beneficiario (10)
									cpBenef = psRegistro.substring(655, 665);
									
									if (cpBenef.length() > 5)
									{
										for(int i=0; i < 5; i++)
										{
											if (cpBenef.substring(0,1).equals("0"))
											{
												cpBenef = cpBenef.substring(1, cpBenef.length());
											}
										}
									}
																		
									//Pais del Benef (3)
									paisBenef = psRegistro.substring(665, 668);
									if (funciones.isNumeric(paisBenef.substring(0, 1)))
									{
										paisBenef = paisBenef.substring(1, 3);
									}
									else
									{
										paisBenef = paisBenef;
									}
									
									//Mandante
									mandante = "0";
									
									//Descripcion de la propuesta de pago (8)
									descripcionPropuesta = psRegistro.substring(28, 30) + psRegistro.substring(526, 532);
									
									//Tipo de documento (3)
									tipoDocumento = psRegistro.substring(28, 31);							
									
									//Busca el no_empresa dependiendo el HouseBank en cat_cta_banco
									recibeDatos = objCargaPagosDao.sacaHouseBank(houseBank);
									
									if (recibeDatos.size() != 0)
									{																		
										noEmpresa = recibeDatos.get(0).getNoEmpresa();
										bancoPagador = recibeDatos.get(0).getIdBanco();
									}
									else
									{
										noEmpresa = 0;
									}								
									
									//Valida que exista la empresa									
									recibeDatos = objCargaPagosDao.obtieneNombreEmpresa(noEmpresa);									
									if (recibeDatos.size() == 0)
									{
										conceptoRechazo = "NO EXISTE LA EMPRESA";										
									}									
									
									//Valida la forma de pago
									if (idFormaPago.equals(""))
										idFormaPago = "0";
									
									if((!chequeraBenef.equals("") && idFormaPago.equals("M")) || (idFormaPago.equals("S")))									
									{										
										if (idFormaPago.equals("M"))
										{
											chequeraBenef = "CONV" + chequeraBenef.substring(chequeraBenef.length()-7, chequeraBenef.length());
											idFormaPago = "3";
										}
										else
										{
											concepto = "CARTA_SEG";
											idFormaPago = "5";
										}																				
									}
									else
									{										
										if (funciones.isNumeric(idFormaPago))
											esNumero = true;
										else
											esNumero = false;
										
										recibeDatos = objCargaPagosDao.obtieneFormaPago(idFormaPago, esNumero);
										if (recibeDatos.size() != 0)
										{
											idFormaPago = recibeDatos.get(0).getIdFormaPago();											
										}
										else
										{
											idFormaPago = "0";
											if (conceptoRechazo.equals(""))
												conceptoRechazo = "NO EXISTE LA FORMA DE PAGO";
											else
												conceptoRechazo = conceptoRechazo + "NO EXISTE LA FORMA DE PAGO";
										}	
									}
																	
									//Valida Banco
									if (idFormaPago != "1")
									{
										recibeDatos = objCargaPagosDao.obtieneBancoBenef(bancoBenef);
										//Si no existe el banco se inserta en el catalogo de bancos
										 if (recibeDatos.size() == 0)
										 {
											 recibeDatosEnteros = objCargaPagosDao.obtieneMaximoNumeroBanco();
											 if (recibeDatosEnteros != 0)
												 nuevoBanco = recibeDatosEnteros + 1;
											 else
												 nuevoBanco = 1;
											 
											 objCargaPagosDto = new CargaPagosDto();
											 objCargaPagosDto.setIdBanco(bancoBenef);
											 objCargaPagosDto.setDescBanco(nombreBancoBenef);
											 objCargaPagosDto.setNuevoBanco(nuevoBanco);
											 
											 if (objCargaPagosDao.insertaBanco(objCargaPagosDto) <= 0)
												 objCargaPagosDto = null;							 
												
										 }						
									}
									else
									{
										bancoBenef = 0;
									}									
									
									//Valida la divisa
									recibeDatos = objCargaPagosDao.obtieneEquivaleDivisa(origen, divisa, "");									
									if (recibeDatos.size() != 0)
										divisa = recibeDatos.get(0).getCodDivisaSET();
									else
									{
										if (conceptoRechazo.equals(""))
										{
											conceptoRechazo = "TIPO DE DIVISA NO EXISTE";
										}
										else
										{
											conceptoRechazo = conceptoRechazo + "TIPO DE DIVISA NO EXISTE";
										}	
									}
									/*
									//Valida rubro del proveedor
									recibeDatos = objCargaPagosDao.obtieneRubroCorrecto(noBenef);
									if (recibeDatos.size() != 0)
									{
										validaCadena = recibeDatos.get(0).getIdRubro();
										if (!validaCadena.equals("0"))
										{
											rubro = validaCadena;
										}
										else
										{
											rubro = "0";
										}
									}
									
									//Valida que exista la caja
									recibeDatosEnteros = objCargaPagosDao.obtieneNumeroCaja(idCaja);
									if (recibeDatosEnteros != 0)
									{
										idCaja = recibeDatosEnteros;
									}
									else
									{	
										if (conceptoRechazo.equals(""))
											conceptoRechazo = "NO SE ESPECIFICA LUGAR DE PAGO";
										else
											conceptoRechazo = conceptoRechazo + "NO SE ESPECIFICA LUGAR DE PAGO";
									}
									
									//Valida si es transfer que contenga RFC
									if (idFormaPago.equals("3") && divisa.equals("MN"))
									{
										if (rfc.equals(""))
										{
											if (conceptoRechazo.equals(""))
												conceptoRechazo = "FALTA EL RFC";
											else
												conceptoRechazo = conceptoRechazo + "FALTA EL RFC";
										}
									}
																		
									//Valida ABA, SWIFT para DLS
									if (cdBancoBenef.indexOf("US") > 0)
									{
										if (aba.equals("") && swiftBenef.equals(""))
										{
											if (conceptoRechazo.equals(""))
												conceptoRechazo = "EL ABA O EL SWIFT ES REQUERIDO";
											else
												conceptoRechazo = conceptoRechazo + "EL ABA O EL SWIFT ES REQUERIDO";
										}
									}
									else
									{
										if (swiftBenef.equals(""))
										{
											if (conceptoRechazo.equals(""))
												conceptoRechazo = "EL SWIFT ES REQUERIDO";
											else
												conceptoRechazo = conceptoRechazo + "EL SWIFT ES REQUERIDO";
										}
										
									}
									*/
									
									//Valida Banco y chequera pagadora																		
									objCargaPagosDto = new CargaPagosDto();
									objCargaPagosDto.setNoEmpresa(noEmpresa);
									objCargaPagosDto.setOrigen(origen);
									objCargaPagosDto.setIdBancoBenef(bancoBenef);
									objCargaPagosDto.setIdFormaPago(idFormaPago);
									objCargaPagosDto.setIdDivisa(divisa);
									objCargaPagosDto.setIdCaja(idCaja);
									objCargaPagosDto.setHouseBank(houseBank);
									objCargaPagosDto.setBancoPagador(bancoPagador);
																											
									recibeDatos = objCargaPagosDao.obtenerBancoYChequera(objCargaPagosDto);
																		
									if (recibeDatos.size() != 0)
									{
										bancoPagador = recibeDatos.get(0).getBancoPagador();
										chequeraPagadora = recibeDatos.get(0).getChequeraPagadora();										
									}
									else
									{									
										if (conceptoRechazo.equals (""))
											conceptoRechazo = "SIN CHEQUERA PAGADORA";
										else
											conceptoRechazo = conceptoRechazo + "SIN CHEQUERA PAGADORA";
									}
									/*
									//Valida que contenga numero de documento
									if (noDocto.equals(""))
									{
										if (conceptoRechazo.equals(""))
											conceptoRechazo = "FALTA EL N�MERO DE DOCUMENTO";
										else
											conceptoRechazo = conceptoRechazo + "FALTA EL N�MERO DE DOCUMENTO";
									}
									
									//Si existe rechazo le asigna un folio
									if (!conceptoRechazo.equals(""))										
										secuencia = Integer.toString(objCargaPagosDao.seleccionaFolio("rechazo_doc"));
									*/
									
									//Saca el total de registros leidos
									totalRegistros = totalRegistros + 1;									
									
									//Saca el total de registros con error y correctos
									if (causaRechazo.equals(""))
										totalOK = totalOK + 1;
									else
										totalError = totalError + 1;
									
									//Saca el total del importe en MN
									if (divisa.equals("MN") || divisa.equals("MXN"))
										totalImporteMN = totalImporteMN + importe; 
									else
										if(divisa.equals("DLS") || divisa.equals("DLLS"))
											totalImporteDLS = totalImporteDLS + importe;
									
									//Cambia formato a fecha alta
									fecHoyString = fecHoy;
									valorNumerico = fecHoy.indexOf("/");									
									if (valorNumerico <= 0)
									{										
										fecHoy = fecHoy.substring(8, 10) + "/" + fecHoy.substring(5, 7) + "/" + fecHoy.substring(0, 4);
									}
									
									//Se genera un objeto para mandar los campos a insertar en el grid de consulta en el .js
									objCargaPagosRegresaCamposDto = new CargaPagosDto();
									objCargaPagosRegresaCamposDto.setNoEmpresa(noEmpresa);
									objCargaPagosRegresaCamposDto.setNoDocto(noDocto);
									objCargaPagosRegresaCamposDto.setSecuencia(secuencia);												
									objCargaPagosRegresaCamposDto.setFecValor(fecValor);
									objCargaPagosRegresaCamposDto.setFechaAlta(fecHoy);
									objCargaPagosRegresaCamposDto.setUsuarioAlta(usuarioAlta);
									objCargaPagosRegresaCamposDto.setNoBenef(noBenef);
									objCargaPagosRegresaCamposDto.setImporte(importe);									
									objCargaPagosRegresaCamposDto.setIdDivisa(divisa);								
									objCargaPagosRegresaCamposDto.setConcepto(concepto);									
									objCargaPagosRegresaCamposDto.setIdFormaPago(idFormaPago);									
									objCargaPagosRegresaCamposDto.setOrigen(origen);
									objCargaPagosRegresaCamposDto.setIdGrupo(grupoRubro);
									objCargaPagosRegresaCamposDto.setIdRubro(rubro);
									objCargaPagosRegresaCamposDto.setMandante(mandante);
									objCargaPagosRegresaCamposDto.setProfitCenter(profitCenter);
									objCargaPagosRegresaCamposDto.setCentroCostos(centroCostos);
									objCargaPagosRegresaCamposDto.setRfc(rfc);
									objCargaPagosRegresaCamposDto.setCurp(curp);
									objCargaPagosRegresaCamposDto.setIdCaja(idCaja);
									objCargaPagosRegresaCamposDto.setClabe(clabe);									
									objCargaPagosRegresaCamposDto.setDescPropuesta(descPropuesta);
									objCargaPagosRegresaCamposDto.setChequeraBenef(chequeraBenef);
									objCargaPagosRegresaCamposDto.setIdBancoBenef(bancoBenef);
									objCargaPagosRegresaCamposDto.setSucursalBenef(sucursalBenef);
									objCargaPagosRegresaCamposDto.setNombreBancoBenef(nombreBancoBenef);
									objCargaPagosRegresaCamposDto.setPaisBancoBenef(paisBancoBenef);
									objCargaPagosRegresaCamposDto.setCdBancoBenef(cdBancoBenef);
									objCargaPagosRegresaCamposDto.setSwiftBenef(swiftBenef);
									objCargaPagosRegresaCamposDto.setIban(iban);
									objCargaPagosRegresaCamposDto.setForFurtherCredit(forFurtherCredit);
									objCargaPagosRegresaCamposDto.setNombreBancoIntermediario(nombreBancoIntermediario);
									objCargaPagosRegresaCamposDto.setAba(aba);
									objCargaPagosRegresaCamposDto.setPaisBancoIntermediario(paisBancoIntermediario);
									objCargaPagosRegresaCamposDto.setCdBancoIntermediario(cdBancoIntermediario);
									objCargaPagosRegresaCamposDto.setChequeraPagadora(chequeraPagadora);
									objCargaPagosRegresaCamposDto.setBancoPagador(bancoPagador);
									objCargaPagosRegresaCamposDto.setApellidoPaterno(apellidoPaterno);
									objCargaPagosRegresaCamposDto.setApellidoMaterno(apellidoMaterno);
									objCargaPagosRegresaCamposDto.setNombre(nombre);
									objCargaPagosRegresaCamposDto.setRazonSocial(razonSocial);
									objCargaPagosRegresaCamposDto.setMail(email);
									objCargaPagosRegresaCamposDto.setDireccionBenef(direccionBenef);
									objCargaPagosRegresaCamposDto.setCdBenef(cdBenef);
									objCargaPagosRegresaCamposDto.setRegionBenef(regionBenef);
									objCargaPagosRegresaCamposDto.setCpBenef(cpBenef);
									objCargaPagosRegresaCamposDto.setPaisBenef(paisBenef);
									objCargaPagosRegresaCamposDto.setLocalizacion(localizacion);
									objCargaPagosRegresaCamposDto.setNoCheque(noCheque);
									objCargaPagosRegresaCamposDto.setTipoDocumento(tipoDocumento);
									objCargaPagosRegresaCamposDto.setConceptoRechazo(conceptoRechazo);
									objCargaPagosRegresaCamposDto.setTipoPersona(sTipoPersona);
									objCargaPagosRegresaCamposDto.setTotalRegistros(totalRegistros);
									objCargaPagosRegresaCamposDto.setTotalOK(totalOK);
									objCargaPagosRegresaCamposDto.setTotalError(totalError);
									objCargaPagosRegresaCamposDto.setTotalImporteMN(totalImporteMN);
									objCargaPagosRegresaCamposDto.setTotalImporteDLS(totalImporteDLS);									
									registrosLeidos.add(objCargaPagosRegresaCamposDto);																		
									causaRechazo = "";			
									
								}
								else
								{										
									//Lee total de archivo 1
								}
							}
						}							
		
						i++;
					}
					archivo.close();
					archivo = null;					
				}
				else
				{
					
				}					
								
			break;			
		}		
		return registrosLeidos;	
	}
	
	
	public String insertaRegistros(List<Map<String, String>> objCamposGrid)
	{
		String estatus = "";
		String idRubro = "";
		String estatusChequera = "";
		String estatusPropuesta = "";
		boolean existePersona = false;
		boolean existe = false;
		int respuestaNumerica = 0;
		String concepto = "";
		String referencia = "";
		String cadenaRespuesta = "";
		
		
		Gson gson = new Gson();
		
		List <CargaPagosDto> recibeDatos = new ArrayList<CargaPagosDto>();
		Map<String, Object> objMapBusiness = new HashMap<String, Object>();
		Map mapaNormal = new HashMap();			
		
		try
		{
			for (int i=0; i < objCamposGrid.size(); i++)
			{				
				recibeDatos = objCargaPagosDao.buscaRubroPersona(objCamposGrid.get(i).get("noBenef"));				
				if (recibeDatos.size() > 0)
				{
					idRubro = recibeDatos.get(0).getIdRubro();
					existePersona = true;				
				}
				else
				{					
					idRubro = "0";
					existePersona = false;
					
					//Se limpia la zinterprov y se inserta el registro del nuevo proveedor
					//objCargaPagosDao.limpiaZinterprov(Integer.parseInt(objCamposGrid.get(i).get("noEmpresa")), objCamposGrid.get(i).get("noBenef"));					
				}
				
				//Inserta y actualiza en la zinterprov
				recibeDatos = objCargaPagosDao.existePersonaZinterprov(Integer.parseInt(objCamposGrid.get(i).get("noEmpresa")), objCamposGrid.get(i).get("noBenef"));
				if (recibeDatos.size() > 0)
				{					
					objCargaPagosDao.actualizaDatosPersona(objCamposGrid, i, Integer.parseInt(idRubro));					
				}
				else
				{	
					objCargaPagosDao.insertaDatosPersona(objCamposGrid, i, Integer.parseInt(idRubro));					
				}
				
				//Limpia e inserta en la tabla zformapago por cada uno de los beneficiarios
				objCargaPagosDao.borraFormaPago(objCamposGrid.get(i).get("noBenef"), Integer.parseInt(objCamposGrid.get(i).get("noEmpresa")), 
												objCamposGrid.get(i).get("mandante"));
				objCargaPagosDao.insertaFormaPago(objCamposGrid.get(i).get("noBenef"), Integer.parseInt(objCamposGrid.get(i).get("noEmpresa")), 
												  objCamposGrid.get(i).get("mandante"), 1); //cheque
				objCargaPagosDao.insertaFormaPago(objCamposGrid.get(i).get("noBenef"), Integer.parseInt(objCamposGrid.get(i).get("noEmpresa")), 
												  objCamposGrid.get(i).get("mandante"), 3); //transferencia
				objCargaPagosDao.insertaFormaPago(objCamposGrid.get(i).get("noBenef"), Integer.parseInt(objCamposGrid.get(i).get("noEmpresa")), 
												  objCamposGrid.get(i).get("mandante"), 9); //cheque ocurre			
							
				//Valida si existe la chequera en la zimpcheqprov			
				if (objCamposGrid.get(i).get("idFormaPago").equals("3") || objCamposGrid.get(i).get("idFormaPago").equals("9"))
				{					
					existe = objCargaPagosDao.buscaChequeraCtasBanco(objCamposGrid, i);
					if (existe)
					{
						estatusChequera = "M";						
					}
					else
					{
						estatusChequera = "P";
					}
					
					respuestaNumerica = objCargaPagosDao.buscaChequeraZimpcheqprov(objCamposGrid, i);
					
					if (respuestaNumerica > 0)
					{						
						objCargaPagosDao.actualizaZimpcheqprov(objCamposGrid, i, estatusChequera);						
					}
					else
					{
						objCargaPagosDao.insertaZimpcheqprov(objCamposGrid, i, estatusChequera);
					}				
				}
								
				//Busca datos en persona si es Convenio CIE				
				if (objCamposGrid.get(i).get("origen").equals("SAP"))
				{					
					recibeDatos = objCargaPagosDao.buscaRubroPersona(objCamposGrid.get(i).get("noBenef"));
					
					if (recibeDatos.size() > 0)
					{
						if (objCamposGrid.get(i).get("chequeraBenef").indexOf("CONV") > -1)
						{
							concepto = recibeDatos.get(0).getConceptoCie() == null ? "" : recibeDatos.get(0).getConceptoCie();							
							referencia = recibeDatos.get(0).getReferenciaCie() == null ? "" : recibeDatos.get(0).getReferenciaCie();
						}
						else
							concepto = objCamposGrid.get(i).get("concepto");
					}
				}
				
				//Validacion para saber si existe la propuesta en la Zimp_fact
				respuestaNumerica = objCargaPagosDao.buscaRegistroZimpFact(objCamposGrid, i);
				
				if (respuestaNumerica > 0)
				{
					//Borra los registros que tengan las mismas caracteristicas pero que no se an importado
					objCargaPagosDao.eliminaRegistroZimpFact(objCamposGrid, i);
					objCargaPagosDao.actualizaRegistroZimpFact(objCamposGrid, i, concepto, referencia);
					cadenaRespuesta = "Se actualizo con exito la zimp_fact";
				}
				else
				{
					objCargaPagosDao.eliminaRegistroZimpFact(objCamposGrid, i);
					estatusPropuesta = objCamposGrid.get(i).get("conceptoRechazo").equals("")?"P":"R";
					respuestaNumerica = objCargaPagosDao.insertaRegistroZimpFact(objCamposGrid, i, concepto, referencia, estatusPropuesta);
					
					if (respuestaNumerica > 0)
					{						
						//Se empiezan las validaciones para generar las propuestas en movimiento
						cadenaRespuesta = "Se inserto con exito en la zimp_fact";
						
						//Se realiza la llamada al Stored para importar personas y los registros a movimiento						
						mapaNormal = objCargaPagosDao.importaProveedor(Integer.parseInt(objCamposGrid.get(i).get("usuarioAlta")), 
								Integer.parseInt(objCamposGrid.get(i).get("noEmpresa")));
						
						if (mapaNormal.size() > 0)							
							cadenaRespuesta = "Se insertaron proveedores con exito";
							mapaNormal = objCargaPagosDao.importaMovimientos(Integer.parseInt(objCamposGrid.get(i).get("usuarioAlta")), 
																			 objCamposGrid.get(i).get("noEmpresa"), 1);
							
							if (mapaNormal.size() > 0)
							{								
								cadenaRespuesta = "Importaci�n realizada con exito";						
							}
							else
								cadenaRespuesta = "Ocurrion un error al insertar a movimiento";												
					}
				}					
			}		
			
			//Funcion para generar el Layout de Altas de Proveedores para el Host to Host de Bancomer			
			if (cadenaRespuesta.equals("Importaci�n realizada con exito"))
			{
				if (layoutAltaProveedores(objCamposGrid))
					cadenaRespuesta = "Se genero Layout de Proveedores";
				else
					cadenaRespuesta = "Error al generar Layout de Proveedores";
			}
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosBusinessImpl, M:insertaRegistros");			
		}
		return cadenaRespuesta;		
	}
	
	public boolean layoutAltaProveedores(List<Map<String, String>> objGrid)
	{		
		String psRegistro = "";	
		boolean respuesta = false;
		String psRuta = "";		
		String psArchivo = "";
		String psDirectorio = "";
		String caracterFijo = "ALTA"; //Esto solo se pone como prueba
		String psNomArchivo = "";
		int folioConsecutivo = 0;
		List<CargaPagosDto> recibeDatos = new ArrayList<CargaPagosDto>();
		List<CargaPagosDto> resultado = new ArrayList<CargaPagosDto>();
		String resultadoString = "";
		int resultadoEntero = 0;
		int totalCambios = 0;
		int totalAltas = 0;
		String noBenef = "";
		int bancoBenef = 0;
		String chequeraBenef = "";
		boolean actualizaCambio = false;
		boolean actualizaAlta = false;
		boolean hayRegistros = false;
		
		try
		{				
			psArchivo = objCargaPagosDao.consultarConfiguraSet(2401);
			psDirectorio = "SITPRE\\MAESTROTER\\ENVIO\\";
									
			if (objGrid.size() > 0)
			{				
				//Se valida si aplican los registros como altas o cambios
				for (int i=0; i < objGrid.size(); i++) 
				{
					if (objGrid.get(i).get("idFormaPago").equals("3") || objGrid.get(i).get("idFormaPago").equals("5"))
					{
						if (objGrid.get(i).get("idDivisa").equals("MN")) //Por el momento el H2H solo aplica para MN
						{
							//Se valida que no sea nomina, ya que solo se mandan altas para los pagos a terceros y no en nomina
							if ((!objGrid.get(i).get("origen").equals("ADM")) || 
								(objGrid.get(i).get("idBancoBenef") != objGrid.get(i).get("bancoPagador") && objGrid.get(i).get("origen").equals("ADM") && !objGrid.get(i).get("idBancoBenef").equals("0")))								
							{
								if (objGrid.get(i).get("conceptoRechazo").equals(""))
								{
									recibeDatos = objCargaPagosDao.buscaCtasBanco(objGrid.get(i).get("noBenef"));
									
									if (recibeDatos.size() > 1) //Si existe mas de un registro de la misma persona
									{
										for (int cam = 0; cam < recibeDatos.size(); cam ++)
										{										
											if (recibeDatos.get(cam).getEstatusAlta().equals("A"))
												actualizaCambio = true;
											else if (recibeDatos.get(cam).getEstatusAlta().equals("C"))
												actualizaCambio =true;
											else
												actualizaAlta = true;										
										}									
									}
									else //Es el unico registro de la persona
									{
										if (recibeDatos.get(0).getEstatusAlta().equals("A"))
										{
											actualizaCambio = true;
										}
										else if (recibeDatos.get(0).getEstatusAlta().equals("C"))
										{
											actualizaCambio = true;
										}
										else
											actualizaAlta = true;									
									}
									
									//Actualiza estatus en la tabla ctas_banco para las altas de personas H2H
									recibeDatos = objCargaPagosDao.sacaFechaModifMax(objGrid.get(i).get("noBenef"));
																	
									if ((actualizaCambio == true && actualizaAlta == true) || actualizaCambio == true )
									{									
										resultadoEntero = objCargaPagosDao.actualizaEstatusAlta(objGrid.get(i).get("noBenef"), "C", "");
									    								    
									    if (resultadoEntero > 0)
									    {								    	
									    	resultadoEntero = objCargaPagosDao.actualizaEstatusAlta(objGrid.get(i).get("noBenef"), "C", recibeDatos.get(0).getFechaAlta().substring(0, 10));
									    }
									    
									    totalCambios = totalCambios + 1;
									}
									else
									{									
										objCargaPagosDao.actualizaEstatusAlta(objGrid.get(i).get("noBenef"), "A", "");
										objCargaPagosDao.actualizaEstatusAlta(objGrid.get(i).get("noBenef"), "A", recibeDatos.get(0).getFechaAlta().substring(0, 10));
										totalAltas = totalAltas + 1;
									}									
								}
							}
						}
					}
				} //Termina validacion de estatus para la tabla ctas_banco *************************************************
				
				if (totalCambios != 0 || totalAltas != 0)
				{					
					psNomArchivo = generaNombreArchivo(psArchivo, psDirectorio, "altas_H2H", caracterFijo, folioConsecutivo);
									
					if (!psNomArchivo.equals(""))
					{
						respuesta = true;
						
						if (abreArchivo(psArchivo + psDirectorio, psNomArchivo) != null)
						{							
							//Genera encabezado del layout
							psRegistro = armaLayout(objGrid, 0, 1, totalAltas, totalCambios, psNomArchivo, objGrid.get(0).get("fechaAlta")); 
							
							if (!psRegistro.equals(""))
							{
								insertaRegistro(psArchivo + psDirectorio, psNomArchivo, psRegistro);
								
								//Genera encabezado del archivo
								psRegistro = armaLayout(objGrid, 0, 2, totalAltas, totalCambios, psNomArchivo, objGrid.get(0).get("fechaAlta"));
								
								if (!psRegistro.equals(""))
								{
									insertaRegistro(psArchivo + psDirectorio, psNomArchivo, psRegistro);
									//Genera Detallado
									for(int i = 0; i < objGrid.size(); i++)
									{
										if (objGrid.get(i).get("conceptoRechazo").equals(""))
										{
											psRegistro = armaLayout(objGrid, i, 3, totalAltas, totalCambios, psNomArchivo, objGrid.get(i).get("fechaAlta"));
											
											if (!psRegistro.equals(""))
											{
												System.out.println(objGrid.get(i).get("noDocto"));
												System.out.println(psRegistro);
												insertaRegistro(psArchivo + psDirectorio, psNomArchivo, psRegistro);
												hayRegistros = true;
											}
										}
										else
											psRegistro = "";
									}									
									
									if (hayRegistros == true)
									{										
										//Genera Triler
										psRegistro = armaLayout(objGrid, 0, 4, totalAltas, totalCambios, psNomArchivo, objGrid.get(0).get("fechaAlta"));
										insertaRegistro(psArchivo + psDirectorio, psNomArchivo, psRegistro);
									}
									else
									{
										respuesta = false;
									}
										
								}
								else
								{
									respuesta = false;
								}
								
							}
							else
							{
								respuesta = false;
							}
						}	
						else
						{
							respuesta = false;
						}

					}
					else
					{
						respuesta = false;
					}
		
				}				
			}
			else
				respuesta = false;
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:interfaz, C:GeneraLayoutH2H, M:layoutAltaProveedores");
			respuesta = false;
		}
		return respuesta;
	}
	
	public String armaLayout(List<Map<String, String>> objGrid, int posicion, int tipoRegistro, int totalAltas, int totalCambios, String nombreArchivo, String fechaAlta)
	{
		String psRegistro = "";
		String cadena = "";
		String campoVacio = "";
		String divisa2 = "";
		int posicionCampo = 0;
		String contrato = "1";
		String convenio = "1";
		int totalRegistros = 0;
		int valorNumerico = 0;
		funciones = new Funciones();
		List<CargaPagosDto> recibeDatos = new ArrayList<CargaPagosDto>();
				
		try
		{
			psRegistro = "";
			switch (tipoRegistro)
			{
				case 1: //Header H2H
					psRegistro = "HHEADER H2H";
					
					//Total de registros por archivo
					totalRegistros = totalAltas + totalCambios;					
					cadena = Integer.toString(totalRegistros);					
					cadena = funciones.ajustarLongitudCampo(cadena, 7, "D", "", "0");					
					psRegistro = psRegistro + cadena;
					
					//Importe total para las altas no aplica, se rellena de 0
					cadena = "";
					cadena = funciones.ajustarLongitudCampo(cadena, 15, "I", "", "0");
					psRegistro = psRegistro + cadena;
					
					//Contrato H2H, este es asignado por el Banco 
					cadena = "";					
					cadena = funciones.ajustarLongitudCampo(contrato, 20, "I", "", "");
					psRegistro = psRegistro + cadena;
										
					//Subservicio para el H2H, asignado por el Banco
					psRegistro = psRegistro + "TG-0015 ";
					
					//Tipo de servicio asignado por el Banco
					psRegistro = psRegistro + "08";
					
					//Consecutivo o folio, debe de ser el mismo que va a traer en el nombre del archivo
					cadena = "";					
					valorNumerico = nombreArchivo.indexOf(".");					
					cadena = nombreArchivo.substring(valorNumerico + 1, (valorNumerico + 1) + 7);										
					psRegistro = psRegistro + cadena;
					
					//Fecha de envio					
					cadena = objGrid.get(posicion).get("fechaAlta").substring(0, 4) + "-" + objGrid.get(posicion).get("fechaAlta").substring(5, 7) + "-" + objGrid.get(posicion).get("fechaAlta").substring(8, 10);
					psRegistro = psRegistro + cadena;					
					
					//Campos en blanco
					cadena = "";
					cadena = funciones.ajustarLongitudCampo(cadena, 170, "I", " ", "");
					psRegistro = psRegistro + cadena;

					break;
				case 2: //Header de archivo
					psRegistro = "";
					cadena = "";
										
					//Este valor es fijo para el cabecero
					psRegistro = "H";
					
					//Convenio este dato lo asigna BBVA, se coloca fijo para pruebas
					cadena = funciones.ajustarLongitudCampo(convenio, 9, "D", "", "0");
					psRegistro = psRegistro + cadena;
					
					//Fecha de envio
					cadena = "";
					cadena = objGrid.get(posicion).get("fechaAlta").substring(0, 4) + "-" + objGrid.get(posicion).get("fechaAlta").substring(5, 7) + "-" + objGrid.get(posicion).get("fechaAlta").substring(8, 10);
					psRegistro = psRegistro + cadena;
					
					//Grupo 
					cadena = "";
					cadena = objGrid.get(posicion).get("idGrupo");
					cadena = funciones.ajustarLongitudCampo(cadena, 10, "I", "", "");
					psRegistro = psRegistro + cadena;
					
					//Clave de archivo, se coloca el mismo consecutivo del HeaderH2H
					cadena = "";					
					valorNumerico = nombreArchivo.indexOf(".");					
					cadena = nombreArchivo.substring(valorNumerico + 1, (valorNumerico + 1) + 7);
					cadena = funciones.ajustarLongitudCampo(cadena, 20, "D", "", "0");
					psRegistro = psRegistro + cadena;
					
					//Codigo de respuesta, siempre al enviar lleva 00
					cadena = "";
					cadena = "00";
					psRegistro = psRegistro + cadena;
					
					//Campos vacios para uso futuro
					cadena = "";
					cadena = funciones.ajustarLongitudCampo(cadena, 584, "I", "", "");
					psRegistro = psRegistro + cadena;			
			
					break;
					
				case 3: //Detallado
					cadena = "";
					psRegistro = "";
					
					//Este valor es fijo para el datallado
					psRegistro = "D";
					
					recibeDatos = objCargaPagosDao.buscaCtasBanco(objGrid.get(posicion).get("noBenef"));
					
					if (recibeDatos.size() > 0)
					{
						for (int estatus = 0; estatus < recibeDatos.size(); estatus++)
						{
							if (!recibeDatos.get(estatus).getEstatusAlta().equals(null));
							{
								cadena = recibeDatos.get(estatus).getEstatusAlta();
								if (cadena.equals("A"))
									psRegistro = psRegistro + "A";
								else if(cadena.equals("B"))
									psRegistro = psRegistro + "B";
								else if (cadena.equals("C"))
									psRegistro = psRegistro + "C";								
							}
						}
					}
					
					//Clave del tercero o no persona con el que se va a dar de alta en el banco 
					cadena = "";
					cadena = objGrid.get(posicion).get("noBenef");
					campoVacio = funciones.ajustarLongitudCampo(cadena, 30, "I", "", "");
					psRegistro = psRegistro + campoVacio;					
					
					//Tipo de cliente o persona (Existen P = Preferente, N = Normal, O = otros) se esta colocando por el momento Normal
					cadena = "N";
					campoVacio = funciones.ajustarLongitudCampo(cadena, 5, "I", "", "");
					psRegistro = psRegistro + campoVacio;
					
					//Divisa (el formato es MXP = MN)
					cadena = "";
					recibeDatos = objCargaPagosDao.obtieneEquivaleDivisa(objGrid.get(posicion).get("noEmpresa"), "", objGrid.get(posicion).get("idDivisa"));
					
					if (recibeDatos.size() > 0)
					{					
						divisa2 = recibeDatos.get(0).getCodDivisaERP();						
					}
					psRegistro = psRegistro + divisa2;
					
					//Tipo de cuenta
					cadena = "";
					cadena = objGrid.get(posicion).get("chequeraBenef");
					
					//Se valida si es chequera de convenio CIE
					if (cadena.substring(0, 4).indexOf("CONV") > 0)
					{
						cadena = objGrid.get(posicion).get("clabe");
					}
					
					//Esto es para cuando aplican como ordenes de pago (pago en ventanilla), ya que no tiene chequera benef
					if ((objGrid.get(posicion).get("idFormaPago").equals("8") || objGrid.get(posicion).get("idFormaPago").equals("5")) && objGrid.get(posicion).get("idBancoBenef").equals("12"))
					{							
						psRegistro = psRegistro + "99";
						campoVacio = "";
						campoVacio = funciones.ajustarLongitudCampo(campoVacio, 35, "D", "", "0");
						psRegistro = psRegistro + campoVacio;
					}
					else if (objGrid.get(posicion).get("idBancoBenef").equals("12"))
					{						
						cadena = objGrid.get(posicion).get("chequeraBenef");
						if(cadena.length() > 10)
						{
							posicionCampo = cadena.indexOf("910");
							if (posicionCampo > 0)
							{
								//Se ajusta la chequera de Bancomer a 10 posiciones
								psRegistro = psRegistro + "99";
								cadena = cadena.substring(posicionCampo + 3, cadena.length());
								cadena = funciones.ajustarLongitudCampo(cadena, 35, "D", "", "0");
								psRegistro = psRegistro + cadena;
							}
							else if (cadena.substring(0, 4).equals("CONV"))
							{
								//Si es convenio CIE
								psRegistro = psRegistro + "50";
								posicionCampo = cadena.indexOf("CONV");
								
								if (posicionCampo > 0)
								{
									cadena = cadena.substring(posicionCampo + 4, cadena.length());
									cadena = funciones.ajustarLongitudCampo(cadena, 8, "D", "", "0");
									cadena = "02" + cadena;
									cadena = funciones.ajustarLongitudCampo(cadena, 35, "D", "", "0");									
								}
								else
								{
									cadena = objGrid.get(posicion).get("clabe");
									cadena = funciones.ajustarLongitudCampo(cadena, 35, "D", "", "0");
								}								
							}
							else
							{
								//si tiene clabe a 18 posiciones
								psRegistro = psRegistro + "40";
								cadena = objGrid.get(posicion).get("clabe");
								cadena = funciones.ajustarLongitudCampo(cadena, 35, "D", "", "0");
								psRegistro = psRegistro + cadena;								
							}
						}
					}
					else if (!objGrid.get(posicion).get("idBancoBenef").equals("12") && objGrid.get(posicion).get("idDivisa").equals("MN")) 
					{
						psRegistro = psRegistro + "  ";
						campoVacio = "";
						campoVacio = funciones.ajustarLongitudCampo(campoVacio, 35, "D", "", "");
						psRegistro = psRegistro + campoVacio;					
					}					
					psRegistro = psRegistro + divisa2;			
					
					//Agrega chequera si es un interbancario
					if (!objGrid.get(posicion).get("idBancoBenef").equals("12") && objGrid.get(posicion).get("idDivisa").equals("MN"))
					{
						psRegistro = psRegistro + "40";
						cadena = objGrid.get(posicion).get("clabe");
						
						if (cadena.length() == 18)
						{
							cadena = funciones.ajustarLongitudCampo(cadena, 35, "D", "", "0");
							psRegistro = psRegistro + cadena;
						}
						else
						{
							cadena = cadena.substring(3, cadena.length());
							cadena = funciones.ajustarLongitudCampo(cadena, 35, "D", "", "0");
							psRegistro = psRegistro + cadena;
						}
						
						//clabe interbancaria
						cadena = objGrid.get(posicion).get("clabe").substring(1, 3);
						cadena = funciones.ajustarLongitudCampo(cadena, 4, "D", "", "0");
						psRegistro = psRegistro + cadena;						
					}
					else
					{
						psRegistro = psRegistro + "  ";
						campoVacio = "";
						campoVacio = funciones.ajustarLongitudCampo(campoVacio, 35, "D", "", "");
						psRegistro = psRegistro + campoVacio;
						
						campoVacio = "";
						campoVacio = funciones.ajustarLongitudCampo(campoVacio, 4, "D", "", "");
						psRegistro = psRegistro + campoVacio;
					}
					
					//El BIC Y ABA por el momento no aplica, el Host to Host solo es para MN. Pero se debe respetar la estructura
					//BIC
					campoVacio = "";
					campoVacio = funciones.ajustarLongitudCampo(campoVacio, 11, "D", "", "");
					psRegistro = psRegistro + campoVacio;
					
					//ABA
					campoVacio = "";
					campoVacio = funciones.ajustarLongitudCampo(campoVacio, 9, "D", "", "");
					psRegistro = psRegistro + campoVacio;
					
					//Contrato (se manda relleno de espacios blancos, es un campo adicional)
					campoVacio = "";
					campoVacio = funciones.ajustarLongitudCampo(campoVacio, 20, "D", "", "");
					psRegistro = psRegistro + campoVacio;
					
					//Telefono (adicional)
					campoVacio = "";
					campoVacio = funciones.ajustarLongitudCampo(campoVacio, 15, "D", "", "");
					psRegistro = psRegistro + campoVacio;
					
					//Canal(para uso futuro)
					campoVacio = "";
					campoVacio = funciones.ajustarLongitudCampo(campoVacio, 10, "D", "", "");
					psRegistro = psRegistro + campoVacio;
					
					//Fecha de alta del proveedor
					cadena = "";
					cadena = objGrid.get(posicion).get("fechaAlta").substring(0, 4) + "-" + objGrid.get(posicion).get("fechaAlta").substring(5, 7) + "-" + objGrid.get(posicion).get("fechaAlta").substring(8, 10);
					psRegistro = psRegistro + cadena;
					
					//Tipo de persona
					psRegistro = psRegistro + objGrid.get(posicion).get("tipoPersona");
					
					//Razon social o empresa para personas Morales
					if (objGrid.get(posicion).get("tipoPersona").equals("M"))
					{
						cadena = objGrid.get(posicion).get("razonSocial");
						cadena = funciones.ajustarLongitudCampo(cadena, 50, "I", "", "");
						psRegistro = psRegistro + cadena;
					}
					else
					{
						campoVacio = "";
						campoVacio = funciones.ajustarLongitudCampo(campoVacio, 50, "I", "", "");
						psRegistro = psRegistro + campoVacio;
					}
					
					//Fecha de registro de la empresa (opcional)
					campoVacio = "";
					campoVacio = funciones.ajustarLongitudCampo(campoVacio, 10, "I", "", "");
					psRegistro = psRegistro + campoVacio;
					
					//RFC de la empresa cuando es M
					if (!objGrid.get(posicion).get("rfc").equals("") && objGrid.get(posicion).get("tipoPersona").equals("M"))
					{
						cadena = objGrid.get(posicion).get("rfc");
						cadena = funciones.ajustarLongitudCampo(cadena, 10, "I", "", "");
						psRegistro = psRegistro + cadena;
					}
					else
					{
						campoVacio = "";
						campoVacio = funciones.ajustarLongitudCampo(campoVacio, 10, "I", "", "");
						psRegistro = psRegistro + cadena;
					}
					
					//Nombre completo si es persona fisica
					if (objGrid.get(posicion).get("tipoPersona").equals("F"))
					{
						cadena = objGrid.get(posicion).get("nombre");
						cadena = funciones.ajustarLongitudCampo(cadena, 20, "I", "", "");
						campoVacio = objGrid.get(posicion).get("apellidoPaterno");
						campoVacio = funciones.ajustarLongitudCampo(campoVacio, 20, "I", "", "");
						cadena = cadena + campoVacio;
						campoVacio = objGrid.get(posicion).get("apellidoMaterno");
						campoVacio = funciones.ajustarLongitudCampo(campoVacio, 20, "I", "", "");
						cadena = cadena + campoVacio;
						
						psRegistro = psRegistro + cadena;
					}
					else
					{
						campoVacio = "";
						campoVacio = funciones.ajustarLongitudCampo(campoVacio, 60, "I", "", "");
						psRegistro = psRegistro + campoVacio;
					}
					
					//Fecha de nacimiento (opcional)
					campoVacio = "";
					campoVacio = funciones.ajustarLongitudCampo(campoVacio, 10, "I", "", "");
					psRegistro = psRegistro + campoVacio;
					
					//RFC de persona F (opcional)
					if (!objGrid.get(posicion).get("rfc").equals("") && objGrid.get(posicion).get("tipoPersona").equals("F"))
					{
						cadena = objGrid.get(posicion).get("rfc");
						cadena = funciones.ajustarLongitudCampo(cadena, 14, "I", "", "");
						psRegistro = psRegistro + cadena;
					}
					else
					{
						campoVacio = "";
						campoVacio = funciones.ajustarLongitudCampo(campoVacio, 14, "I", "", "");
						psRegistro = psRegistro + campoVacio;
					}
					
					//Calle, numero, colonia, delegacion y entidad federativa (opcionales todos)
					campoVacio = "";
					campoVacio = funciones.ajustarLongitudCampo(campoVacio, 148, "I", "", "");
					psRegistro = psRegistro + campoVacio;
					
					//Codigo postal (opcional)
					campoVacio = "";
					campoVacio = funciones.ajustarLongitudCampo(campoVacio, 5, "I", "", "0");
					psRegistro = psRegistro + campoVacio;
					
					//Observaciones es un texto libre (opcional)
					campoVacio = "";
					campoVacio = funciones.ajustarLongitudCampo(campoVacio, 40, "I", "", "");
					psRegistro = psRegistro + campoVacio;
					
					//Tipo de confirmacion (ahorita esta aplicando sin confirmacion = 00)
					psRegistro = psRegistro + "00";
					
					//Email o celular (solo aplican si existe confirmacion en este caso no existe)
					campoVacio = "";
					campoVacio = funciones.ajustarLongitudCampo(campoVacio, 50, "I", "", "");
					psRegistro = psRegistro + campoVacio;
					
					//Codigo de respuesta (2), descripcion de codigo(30) y numero de cliente BBVA(8) se mandan en blanco son campos de respuesta
					campoVacio = "";
					campoVacio = funciones.ajustarLongitudCampo(campoVacio, 40, "I", "", "");
					psRegistro = psRegistro + campoVacio;
					System.out.println(psRegistro + "En el llenado");
					System.out.println(posicion);
					break;
				case 4: //Triler
					
					psRegistro = "";
					cadena = "";
					
					//Este valor es fijo
					psRegistro = "T";
					
					//Total de altas
					cadena = Integer.toString(totalAltas);
					cadena = funciones.ajustarLongitudCampo(cadena, 10, "D", "", "0");
					psRegistro = psRegistro + cadena;
					
					//Total de bajas
					cadena = "0"; //por el momento se van en 0 por que no se sabe cuando aplicaria una baja
					cadena = funciones.ajustarLongitudCampo(cadena, 10, "D", "", "0");
					psRegistro = psRegistro + cadena;
					
					//Total de cambios
					cadena = Integer.toString(totalCambios);
					cadena = funciones.ajustarLongitudCampo(cadena, 10, "D", "", "0");
					psRegistro = psRegistro + cadena;
					
					//Espacio reservado para los rechazos que genere BBVA de altas, bajas y cambios
					cadena = "";
					cadena = funciones.ajustarLongitudCampo(cadena, 30, "D", "", "0");
					psRegistro = psRegistro + cadena;
					
					//Espacio en blanco para uso futuro
					cadena = "";
					cadena = funciones.ajustarLongitudCampo(cadena, 575, "D", "", "");
					psRegistro = psRegistro + cadena;
									
					break;
			}
			
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:GeneraLayoutH2H, M:armaLayout");
		} return psRegistro;
	}
	
	public String generaNombreArchivo(String psDirectorio, String psRutaArchivo, String psTipoFolio, 
									  String psPrefijoNombre, int folioConsecutivo)	
	{
		String nombreArchivo = "";
		String psRuta = "";
		String folioString = "";
		boolean creaDirectorio = false;		
		funciones = new Funciones();
		
		try
		{			
			psRuta = psDirectorio + psRutaArchivo;
			folioConsecutivo = objCargaPagosDao.seleccionaFolio(psTipoFolio);
			objCargaPagosDao.actualizaFolio(psTipoFolio);
			
			folioString = Integer.toString(folioConsecutivo);
			folioString = funciones.ajustarLongitudCampo(folioString, 7, "D", "", "0");
						
			if (folioConsecutivo < 0)
				nombreArchivo = "";
			else
				nombreArchivo = psPrefijoNombre + "." + folioString + ".txt";
			
			//Se valida que exista el directorio para la colocacion del archivo
			creaDirectorio = generaDirectorio(psRuta);
						
			if (creaDirectorio == false)
				nombreArchivo = "";
			
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:interfaz, C:GeneraLayoutH2H, M:generaNombreArchivo");
			nombreArchivo = "";
		}
		return nombreArchivo;
	}
		
	public boolean generaDirectorio (String psRuta)
	{
		File archivo = null;
		
		try
		{
			if (!psRuta.equals(""))
			{
				archivo = new File(psRuta);				
			
				if (archivo.isDirectory())
					return true;
				else
					return archivo.mkdirs();
			}
			else
				return false;
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:GeneraLayoutH2H, M:generaDirectorio");
			return false;
		}
	}
	
	public File abreArchivo(String psRuta, String nombreArchivo)
	{		
		File creaArchivo = null;
		try
		{
			creaArchivo = new File(psRuta, nombreArchivo);
			if (creaArchivo.isFile())
			{
				return creaArchivo;				
			}
			else
			{
				if (creaArchivo.createNewFile())
				{					
					return creaArchivo;
				}
				else
				{				
					return null;
				}
			}		
		}
		catch(Exception e)
		{	
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:GeneraLayoutH2H, M:abreArchivo");
			return null;
		}
	}
	
	public void insertaRegistro(String psRuta, String nombreArchivo, String registro)
	{
		Scanner archivo = null;
		String sb = "";
		File fichero = abreArchivo(psRuta, nombreArchivo);
		if (fichero != null)
		{
			try
			{
				archivo = new Scanner(fichero);
				while(archivo.hasNext())
				{
					sb += archivo.nextLine() + "\r\n";
				}
				archivo.close();
				FileWriter altaH2H = new FileWriter(fichero);
				sb += registro;
				altaH2H.write(sb);
				altaH2H.close();	
			}
			catch (FileNotFoundException e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:GeneraLayoutH2H, M:insertaRegistro" + "No se encuentra el Archivo");
			}
			catch (IOException e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:GeneraLayoutH2H, M:insertaRegistro" +"Error al escribir en la Bitacora");
			}
		}
		else
		{
			bitacora.insertarRegistro(new Date().toString() + " " 
					+"P:com.webset.set.business, C:CreacionArchivosBusiness, M:insertarRegistro" +"Error al obtener el directorio");
		}
	}
	
	public List<CargaPagosDto> llenaGridSAE(int iTipoPago, String fecHoy, int usuarioAlta, String noEmpresaParam, String estatus, String fecIni, String fecFin) {
		List<CargaPagosDto> result = new ArrayList<CargaPagosDto>();
		
		try {
			//Se realiza la llamada al Stored para importar personas y los registros a movimiento						
			//result = objCargaPagosDao.buscaCheqPag();
			/*
			if(result.size() > 0) {
				for(int i=0; i<result.size(); i++) {
					objCargaPagosDao.actualizaDatosBcoCheq(result, i);
				}
			}
			*/
			//objCargaPagosDao.importaProveedor(usuarioAlta, Integer.parseInt(noEmpresaParam));
			
			result = objCargaPagosDao.selectImportaCP(iTipoPago, fecHoy, usuarioAlta, noEmpresaParam, estatus, fecIni, fecFin);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosBusinessImpl, M:llenaGridSAE");
		}
		return result;
	}
	
	public String insertaRegistrosCXP(String noEmpresa) {
		String result = "";
		Map mapaNormal = new HashMap();
		globalSingleton = GlobalSingleton.getInstancia();
		
		try {
				//Importa los proveedores que bienen del ERP a la tabla de personas
				importaProveedores();
				
				//Importa los movimientos de la zimp_fact a la tabla de movimiento
				mapaNormal = objCargaPagosDao.importaMovimientos(globalSingleton.getUsuarioLoginDto().getIdUsuario(), noEmpresa.equals("0") ? "*" : noEmpresa, 0);
				
				if (mapaNormal.size() != 0)
					result = "Importaci�n realizada con exito";						
				else
					result = "Ocurrio un error en la importaci�n";
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosBusinessImpl, M:insertaRegistrosCXP");
		}
		return result;
	}
	
	public void importaProveedores() {
		try {
			logger.debug("Entra a importaProveedores");
			Map m = objCargaPagosDao.importaProveedor(2, 0);
			logger.debug("Sale de importaProveedores ");
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosBusinessImpl, M:importaProveedores");
			logger.error("Ocurrio un error: " + e.toString());
		}
	}
	
	/**
	 * Metodo que ejecuta la importacion de CXP automatica,
	 * desde el cron configurado en spring.
	 * Se ejecuta con el usuario 2, empresa 0, valor 0 porque
	 * el Store importa todo lo que tenga estaus pendiente.
	 */
	public void insertaRegistrosCXPAuto() {
		try {
			logger.debug("Entra a insertaRegistrosCXPAuto");
			Map m = objCargaPagosDao.importaMovimientos(2, "0", 0);
			logger.debug("Sale de insertaRegistrosCXPAuto ");
		}
		catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosBusinessImpl, M:insertaRegistrosCXPAuto");
			logger.error("Ocurrio un error: " + e.toString());
		}
	}
	
	public String armaPropuesta(List<Map<String, String>> gridDatos) {
		String result = "";
		int res = 0;
		
		try {
			for(int i=0; i<gridDatos.size(); i++) {
				
				if(gridDatos.get(i).get("noDocto").equals("") || gridDatos.get(i).get("noDocto").equals("0"))
					return "El registro no puede ser importado, debido a que no contiene n�mero de factura!!";
					
				res = objCargaPagosDao.buscaDatosBenef(gridDatos, i);
				
				if(res <= 0) return "No se ha dado de alta el banco y chequera para el proveedor " + gridDatos.get(i).get("razonSocial");
			}
			
			for(int i=0; i<gridDatos.size(); i++) {
				res = objCargaPagosDao.actDatosPropuesta(gridDatos, i);
			}
			if(res != 0)
				result = insertaRegistrosCXP("*");
			else
				result = "Error al tratar de agrupar los registros";
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosBusinessImpl, M:armaPropuesta");
		}
		return result;
	}
	
	public int validaFacultad(int facultad) {
		return objCargaPagosDao.validaFacultad(facultad);
	}
	
	public List<CargaPagosDto> llenaGridCXC(int noEmpresa, String estatus) {
		return objCargaPagosDao.llenaGridCXC(noEmpresa, estatus);
	}
	
	public String insertaRegistrosCXC(List<Map<String, String>> objCamposGrid) {
		int respuestaNumerica = 0;
		String cadenaRespuesta = "";
		
		try {
			for(int i=0; i<objCamposGrid.size(); i++) {
				respuestaNumerica = objCargaPagosDao.existeCXC(objCamposGrid, i);
				
				if(respuestaNumerica == 0) {
					respuestaNumerica = 0;
					respuestaNumerica = objCargaPagosDao.insertaRegistrosCXC(objCamposGrid, i);
					
					if(respuestaNumerica != 0) respuestaNumerica = objCargaPagosDao.modificaRegistrosCXC(objCamposGrid, i, "I", "");
				}else {
					respuestaNumerica = objCargaPagosDao.modificaRegistrosCXC(objCamposGrid, i, "R", "Ya fue procesado anteriormente");
				}
				
			}
			
			if (respuestaNumerica > 0)
				cadenaRespuesta = "Importaci�n de CXC realizada!!";
			else
				cadenaRespuesta = "Ocurrion un error al realizar la impotaci�n";
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosBusinessImpl, M:insertaRegistrosCXC");			
		}
		return cadenaRespuesta;
	}
	
	//Getters and Setters
	public CargaPagosDao getObjCargaPagosDao() 
	{
		return objCargaPagosDao;
	}

	public void setObjCargaPagosDao(CargaPagosDao objCargaPagosDao) 
	{
		this.objCargaPagosDao = objCargaPagosDao;
	}

	public GeneraLayoutH2H getObjGeneraLayout() {
		return objGeneraLayout;
	}

	public void setObjGeneraLayout(GeneraLayoutH2H objGeneraLayout) {
		this.objGeneraLayout = objGeneraLayout;
	}

	@Override
	public HSSFWorkbook reporteInterfaces(String tipoValor, String fecHoy, String usuarioAlta, String idEmpresa,
			String estatus, String fecIni, String fecFin) {
		HSSFWorkbook hb=null;
		try {
			hb=Utilerias.generarExcel(new String[]{
					"Secuencia",
					"NoDocto",
					"NoEmpresa", 
					"NoFactura",
					"FecFact", 
					"FecValor", 
					"NoBenef",
					"RazonSocial",
					"Nombre",
					"ApellidoPaterno",
					"ApellidoMaterno",
					"ImpSolic",
					"Importe", 
					"IdDivisa",
					"TipoCamb", 
					"Concepto",
					"IdFormaPago",
					"CveLeyen",
					"BenefAlt",
					"IdBcoAlt",
					"IdChqAlt", 
					"IdRubro", 
					"CentroCostos", 
					"GpoTesor",
					"CodBloq",
					"IndMayEs",
					"EstatusPropuesta", 
					"ConceptoRechazo",
					"FechaImp"
			}, 
					objCargaPagosDao.reporteInterfaces(tipoValor, fecHoy, usuarioAlta, idEmpresa, estatus, fecIni, fecFin), new String[]{
					"Secuencia",
					"NoDocto",
					"NoEmpresa", 
					"NoFactura",
					"FecFact", 
					"FecValor", 
					"NoBenef",
					"RazonSocial",
					"Nombre",
					"ApellidoPaterno",
					"ApellidoMaterno",
					"ImpSolic",
					"Importe", 
					"IdDivisa",
					"TipoCamb", 
					"Concepto",
					"IdFormaPago",
					"CveLeyen",
					"BenefAlt",
					"IdBcoAlt",
					"IdChqAlt", 
					"IdRubro", 
					"CentroCostos", 
					"GpoTesor",
					"CodBloq",
					"IndMayEs",
					"EstatusPropuesta", 
					"ConceptoRechazo",
					"FechaImp"
								
					});
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: reportePersonas");
		}return hb;
	}
}
