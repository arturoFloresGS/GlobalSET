package com.webset.set.consultas.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
//Util
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.axis.AxisFault;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

//App
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.servlet.ssm.WebContext;
import com.webset.set.consultas.dao.ConsultasDao;
import com.webset.set.consultas.dto.ChequeraGridDto;
import com.webset.set.consultas.dto.DatosChequeraDto;
import com.webset.set.consultas.dto.MovimientoDto;
import com.webset.set.consultas.dto.ParamBusSolicitudDto;
import com.webset.set.consultas.dto.ParamBusquedaMovimientoDto;
import com.webset.set.consultas.dto.ParamZimpFactDto;
import com.webset.set.consultas.dto.ParametroGridChequeraDto;
import com.webset.set.consultas.dto.ReportesChequeraDto;
import com.webset.set.consultas.dto.SaldoChequeraDto;
import com.webset.set.consultas.dto.SaldoDto;
import com.webset.set.global.business.GlobalBusiness;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.BitacoraChequesDto;
import com.webset.set.utilerias.dto.ComunDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.RetornoUnicoDto;
import com.webset.set.utilerias.dto.RevividorDto;
import com.webset.utils.tools.Utilerias;

import jxl.Sheet;
import jxl.Workbook;
import mx.com.gruposalinas.CancelacionCompensacion.DT_CancelacionCompensacion_OBCancelaciones;
import mx.com.gruposalinas.CancelacionCompensacion.DT_CancelacionCompensacion_ResponseCancelaciones;
import mx.com.gruposalinas.CancelacionCompensacion.SOS_CancelacionCompensacionBindingStub;
import mx.com.gruposalinas.CancelacionCompensacion.SOS_CancelacionCompensacionServiceLocator;
//import java.text.SimpleDateFormat;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

/**
 * @author 
 */
public class ConsultasBusiness {
	private ConsultasDao consultasDao;
	private Bitacora bitacora = new Bitacora();
	private GlobalSingleton globalSingleton =  new GlobalSingleton();
	private GlobalBusiness globalBusiness;
	//private ExportarDatos exportarDatos = new ExportarDatos();
	private Funciones funciones = new Funciones();
	private Logger logger = Logger.getLogger(ConsultasBusiness.class);
	
	/**
	 *	Hace llamada  las tablas cat_banco y cat_cta_banco y obtine el id y la descripcion del banco
	 *  Public Function FunSQLCombo336(ByVal pvvValor1 As Variant) As ADODB.Recordset
	 *  
	 * @param idEmpresa
	 * @return List<ComunDto> 
	 */
	public List<LlenaComboGralDto> llenarComboCatCtaBanco (int iIdEmpresa){
		List<LlenaComboGralDto> listBanco = new ArrayList<LlenaComboGralDto>();
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			
			listBanco = consultasDao.llenarComboCatCtaBanco(iIdEmpresa);
			/*listBanco = consultasDao.llenarComboCatCtaBanco(iIdEmpresa <= 0 
					? globalSingleton.getUsuarioLoginDto().getNumeroEmpresa()
					: iIdEmpresa);*/
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Consultas, C:ConsultasBusiness, M:llenarComboCatCtaBanco");
		}

		return listBanco;
	}

	/**
	 * 
	 * @param idBanco
	 * @param idEmpresa
	 * @return List<ComunDto>
	 * 
	 * Retorna la lista de chequeras que correspondan al idBanco e idEmpresa ademas de que el tipo chequera 
	 * sea diferente U
	 * Public Function FunSQLCombo335(ByVal pvvValor1 As Variant, ByVal pvvValor2 As Variant) As ADODB.Recordset 
	 */
	public List<ComunDto> llenarComboChequera(int idBanco, int idEmpresa){
		return consultasDao.llenarComboChequera(idBanco, idEmpresa);
	}

	/**
	 * 
	 * @param dto
	 * @return List<SaldoChequeraDto>
	 * 
	 * Public Function FunSQLSelect802(ByVal pvvValor1 As Variant, ByVal pvvValor2 As Variant, ByVal pvvValor3 As Variant, ByVal pvvValor4 As Variant, ByVal pvvValor5 As Variant) As ADODB.Recordset
	 * 
	 * Seleciona los datos para llenar la pantalla de Saldo Historico de Chequeras
	 */
	public List<SaldoChequeraDto> seleccionarDatosChequeraHistorico(DatosChequeraDto dto){
		return consultasDao.seleccionarDatosChequeraHistorico(dto);
	}

	/**
	 * 
	 * @param dto
	 * @return
	 * Public Function SELECTSaldoCheq(ByVal pvsDescbanco As String, ByVal pvsChequera As String, 
	 * 		ByVal pviEmpresa As Integer, ByVal pviBanco As Integer) As ADODB.Recordset
	 * 
	 * Seleciona los datos para llenar la pantalla de Saldo de Chequeras
	 */
	public List<SaldoChequeraDto> seleccionarDatosChequera(DatosChequeraDto dto){
		return consultasDao.seleccionarDatosChequera(dto);
	}

	/**
	 * 
	 * @param dto
	 * @return List<RetornoUnicoDto>
	 * 
	 * Public Function FunSQLSelect793(ByVal pvvValor1 As Variant, ByVal pvvValor2 
	 * 	As Variant, ByVal pvvValor3 As Variant, Optional psFECHA As String) As ADODB.Recordset
	 * 
	 * Hace la suma de los importes que tengan que ver los los parametros 
	 * idChequera, noEmpresa, idBanco, fecValor
	 */
	public List<RetornoUnicoDto> seleccionarImporteTotalMovimiento(DatosChequeraDto dto){
		return consultasDao.seleccionarImporteTotalMovimiento(dto);
	}

	/**
	 * 
	 * @param dto
	 * @return List<ChequeraGridDto>
	 * 
	 * para seleccionar abonos
	 * Public Function FunSQLSelect803(ByVal pvvValor1 As Variant, ByVal pvvValor2 As Variant, _
                                ByVal pvvValor3 As Variant, ByVal pvvValor4 As Variant, _
                                ByVal pvvValor5 As Variant, ByVal pvvValor6 As Variant, _
                                ByVal pvvValor7 As Variant) As ADODB.Recordset
	 */
	public List<ChequeraGridDto> seleccionarDatosGridAbonos(ParametroGridChequeraDto dto){
		return consultasDao.seleccionarDatosGridAbonos(dto);
	}

	/**
	 * 
	 * @return String 
	 * 
	 * Obtiene la fecha de hoy apartir de la base de datos de la tabla fechas
	 */
	public String obtenerFechaHoy() {
		return consultasDao.obtenerFechaHoy();
	}
	/**
	 * 
	 * @return String 
	 * 
	 * Obtiene la fecha de hoy apartir de la base de datos de la tabla fechas
	 */
	public String obtenerFechaAyer() {
		return consultasDao.obtenerFechaAyer();
	}
	/**
	 * 
	 * @param dto
	 * @return List<ChequeraGridDto>
	 * 
	 * Public Function FunSQLSelect804(ByVal pvvValor1 As Variant, ByVal pvvValor2 As Variant, ByVal pvvValor3 As Variant, ByVal pvvValor4 As Variant, ByVal pvvValor5 As Variant, ByVal pvvValor6 As Variant, ByVal pvvValor7 As Variant) As ADODB.Recordset
	 *  
	 * Retorna los datos de los cargos
	 */
	public List<ChequeraGridDto> seleccionarDatosGridCargos(ParametroGridChequeraDto dto){
		return consultasDao.seleccionarDatosGridCargos(dto);
	}

	/**
	 * 
	 * @param dto
	 * @return List<ChequeraGridDto>
	 * 
	 * Public Function SELECTMvtosAbonosCheq(ByVal pvsFecha As String, ByVal pviEmpresa As Integer, ByVal pviBanco As Integer, ByVal pvsChequera As String) As ADODB.Recordset
	 * 
	 * Selecciona los datos para el grid de saldo chequera abonos
	 */
	public List<ChequeraGridDto> seleccionarMvtosAbonosChequera(ParametroGridChequeraDto dto){
		return consultasDao.seleccionarMvtosAbonosChequera(dto);
	}

	/**
	 * 
	 * @param dto
	 * @return List<ChequeraGridDto>
	 * 
	 *Public Function SELECTMvtosCargosCheq(ByVal pvsFecha As String, ByVal pviEmpresa As Integer, ByVal pviBanco As Integer, ByVal pvsChequera As String) As ADODB.Recordset 
	 * 
	 * Selecciona los datos para el grid de saldo chequera cargos
	 */
	public List<ChequeraGridDto> seleccionarMvtosCargosChequera(ParametroGridChequeraDto dto){
		return consultasDao.seleccionarMvtosCargosChequera(dto);
	}

	/**
	 * 
	 * @param idUsuario
	 * @return List<ComunDto>
	 * 
	 * llenar el combo de saldo de chequeras
	 */
	public List<ComunDto> llenarComboEmpresa(int idUsuario){
		System.out.println("llenarcomboempresa");
		return consultasDao.llenarComboEmpresa(idUsuario);
	}

	/**
	 * 
	 * @param dto
	 * @return List<RetornoUnicoDto>
	 * 
	 * Public Function FunSQLSelect805(ByVal pvvValor1 As Variant, 
	 * 		ByVal pvvValor2 As Variant, ByVal pvvValor3 As Variant) As ADODB.Recordset
	 * 
	 * Selecciona la ultima fecha de actualizacion  de la chequera
	 */
	public List<RetornoUnicoDto> seleccionarFechaActualizacion(ParametroGridChequeraDto dto){
		return consultasDao.seleccionarFechaActualizacion(dto);
	}

	/**
	 * 
	 * @param dto
	 * @return List<RetornoUnicoDto>
	 * 
	 * Public Function FunSQLSelect798(ByVal pvvValor1 As Variant, ByVal pvvValor2 As Variant, ByVal pvvValor3 As Variant) As ADODB.Recordset
	 * 
	 * Selecciona el valor de chequeras por entregar para  SaldoChequera
	 */
	public List<ChequeraGridDto> seleccionarPorEnt(ParametroGridChequeraDto dto){
		return consultasDao.seleccionarPorEnt(dto);
	}

	/**
	 * 
	 * @param dto
	 * @return List<RetornoUnicoDto>
	 * 
	 * Public Function FunSQLSelect799(ByVal pvvValor1 As Variant, ByVal pvvValor2 As Variant, ByVal pvvValor3 As Variant) As ADODB.Recordset
	 * 
	 * Selecciona el valor de chequeras por imprimir para  SaldoChequera
	 */
	public List<RetornoUnicoDto>  seleccionarPorImp(ParametroGridChequeraDto dto){
		return consultasDao.seleccionarPorImp(dto);
	}

	/**
	 * 
	 * @param dto
	 * @return List<RetornoUnicoDto>
	 * Public Function FunSQLSelect800(ByVal pvvValor1 As Variant, 
	 * 			ByVal pvvValor2 As Variant, ByVal pvvValor3 As Variant) As ADODB.Recordset
	 * 
	 * Selecciona el valor de chequeras por transferir para  SaldoChequera
	 */
	public List<RetornoUnicoDto>  seleccionarPorTrans(ParametroGridChequeraDto dto){
		return consultasDao.seleccionarPorTrans(dto);
	}

	/**
	 * 
	 * @param dto
	 * @return List<ChequeraGridDto>
	 * 
	 * Public Function FunSQLSelect795(ByVal pvvValor1 As Variant, ByVal pvvValor2 As Variant, ByVal pvvValor3 As Variant) As ADODB.Recordset
	 * 
	 * selcciona los datos de grid para AbonoSBC de Saldo Chequera
	 */
	public List<ChequeraGridDto> seleccionarAbonoSBC(ParametroGridChequeraDto dto){
		return consultasDao.seleccionarAbonoSBC(dto);
	}

	/**
	 * 
	 * @param dto
	 * @return List<RetornoUnicoDto>
	 * 
	 * Public Function FunSQLSelect796(ByVal pvvValor1 As Variant, ByVal pvvValor2 As Variant, ByVal pvvValor3 As Variant, ByVal pvvValor4 As Variant) As ADODB.Recordset
	 * 
	 * Selecciona la suma del abonoSBC en saldoChequera
	 */
	public List<RetornoUnicoDto>  seleccionarSumaAbonosSBC(ParametroGridChequeraDto dto){
		return consultasDao.seleccionarSumaAbonosSBC(dto);
	}

	public List<LlenaComboGralDto> obtenerCajas(){
		List<LlenaComboGralDto> listCajas = new ArrayList<LlenaComboGralDto>();
		int iIdUsuario = 0;
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			iIdUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			listCajas = consultasDao.consultarCajas(iIdUsuario);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Consultas, C:ConsultasBusiness, M:obtenerCajas");
		}
		return listCajas;
	}

	public List<LlenaComboGralDto> obtenerChequeras(int iIdBanco, int iIdEmpresa)
	{
		List<LlenaComboGralDto> listCheq = new ArrayList<LlenaComboGralDto>();
		//int iIdEmpresa = 0;
		try{

			globalSingleton = GlobalSingleton.getInstancia();
			//iIdEmpresa = globalSingleton.getUsuarioLoginDto().getNumeroEmpresa();
			listCheq = consultasDao.consultarChequeras(iIdBanco, iIdEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Consultas, C:ConsultasBusiness, M:obtenerChequeras");
		}
		return listCheq;
	}

	public List<LlenaComboGralDto> obtenerDivisiones(boolean bActual)
	{
		List<LlenaComboGralDto> listDiv = new ArrayList<LlenaComboGralDto>();
		int iIdEmpresa = 0;
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			if(bActual)
				iIdEmpresa = globalSingleton.getUsuarioLoginDto().getNumeroEmpresa();

			listDiv = consultasDao.consultarDivisiones(iIdEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Consultas, C:ConsultasBusiness, M:obtenerDivisiones");
		}
		return listDiv;
	}

	public List<LlenaComboGralDto> obtenerEstatus()
	{
		List<LlenaComboGralDto> listEstatus = new ArrayList<LlenaComboGralDto>();
		try{
			listEstatus = consultasDao.consultarEstatus();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Consultas, C:ConsultasBusiness, M:obtenerEstatus");
		}
		return listEstatus;
	}

	public List<LlenaComboGralDto> obtenerOrigen()
	{
		List<LlenaComboGralDto> listOrigen = new ArrayList<LlenaComboGralDto>();
		try{
			listOrigen = consultasDao.consultarOrigen();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Consultas, C:ConsultasBusiness, M:obtenerOrigen");
		}
		return listOrigen;
	}


	public List<LlenaComboGralDto> obtenerTiposOperacion()
	{
		List<LlenaComboGralDto> listTipoOperacion = new ArrayList<LlenaComboGralDto>();
		try{
			listTipoOperacion = consultasDao.consultarTiposOperacion();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Consultas, C:ConsultasBusiness, M:obtenerTiposOperacion");
		}
		return listTipoOperacion;
	}

	/**
	 * Este mï¿½todo es utilizado en ConsultaDeMovimientos
	 * @param bActual
	 * @return
	 */
	public List<LlenaComboGralDto> obtenerValorCustodia(boolean bActual)
	{
		List<LlenaComboGralDto> listValorCustodia = new ArrayList<LlenaComboGralDto>();
		int iIdCaja = 0;
		int iIdEmpresa = 0;
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			iIdCaja = globalSingleton.getUsuarioLoginDto().getIdCaja();
			if(bActual)
				iIdEmpresa = globalSingleton.getUsuarioLoginDto().getNumeroEmpresa();

			listValorCustodia = consultasDao.consultarValorCustodia(iIdCaja, iIdEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Consultas, C:ConsultasBusiness, M:obtenerValorCustodia");
		}
		return listValorCustodia;
	}
	@SuppressWarnings("unused")
	public List<Map<String, Object>> consultarMovimientos(ParamBusquedaMovimientoDto dto)
	{
		StringBuffer armaConsulta = new StringBuffer();
		List<Map<String, Object>> listConsMovi  = new ArrayList<Map<String, Object>>();

		int iIdUsuario = 0;
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			iIdUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();

			armaConsulta = consultasDao.armarConsultaMovimientos(dto);
			listConsMovi = consultasDao.consultarMovimientos(armaConsulta, dto.isBReporte());
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Consultas, C:ConsultasBusiness, M:consultarMovimientos");
			e.printStackTrace();
		}
		return listConsMovi;
	}

	@SuppressWarnings("unused")
	public JRDataSource obtenerReporteMovimientos(ParamBusquedaMovimientoDto dto, WebContext context)
	{
		StringBuffer armaConsulta = new StringBuffer();
		JRMapArrayDataSource jrDataSource = null;

		int iIdUsuario = 0;
		try{
			System.out.println("llega al business de reporte");
			System.out.println(dto.isOptEmp() + " valor del boolean");
			System.out.println("" + dto.getNoEmpresa() + " noEmpresa en el bussines");


			//globalSingleton = GlobalSingleton.getInstancia();
			//iIdUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();//Cambiar estos valores fijos, revisar el contextoWeb en globalBusiness

			if(dto.isOptEmp()){
				System.out.println(" entra a la opcion de true");
				dto.setSEmpresas("" + dto.getNoEmpresa());
			}
			else
				//dto.setSEmpresas(globalBusiness.obtenerCadenaEmpresasUsuario(iIdUsuario));
				dto.setSEmpresas("" + dto.getNoEmpresa());

			dto.setSEmpresas("" + dto.getNoEmpresa());
			dto.setNoEmpresa(dto.getNoEmpresa());

			dto.setBReporte(true);
			armaConsulta = consultasDao.armarConsReporteMovto(dto);
			jrDataSource = new JRMapArrayDataSource(consultasDao.consultarMovimientos(armaConsulta, dto.isBReporte()).toArray());

		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Consultas, C:ConsultasBusiness, M:obtenerReporteMovimientos");
			e.printStackTrace();
		}
		return jrDataSource;
	}


	public String exportarDatosExcel(String sDatos, int iNoColumn, String sForma){
		String respuesta = "";
		Gson gson = new Gson();
		List<Map<String, String>> parameters = gson.fromJson(sDatos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());

		try {	    	
			//Se crea el libro Excel
			HSSFWorkbook wb = Utilerias.generarExcel("Cheques", new String[]{
					"No. Factura",
					"Importe",
					"Divisa",
					"Estatus",
					"Fecha",
					"Fecha Contabilizacion",
					"Beneficiario",
					"Concepto",
					"Cve. Operacion",
					"Chequera",	
					"Desc. Banco Benef",
					"Chequera Benef",
					"Forma Pago",
					"Folio",
					"Referencia",
					"No. Cheque",
					"Fecha Confirmacion",
					"Origen Movto.",
					"Empresa",
					"Importe Original",
					"Cve. Control"
			}, 
					parameters, 
					new String[]{
							"C30",
							"C1",
							"C2",
							"C4",
							"C5",
							"C42",
							"C8",
							"C9",
							"C10",
							"C12",
							"C13",
							"C14",
							"C15",
							"C17",
							"C20",
							"C21",
							"C22",
							"C26",
							"C28",
							"C33",
							"C35"
			},
					new String[]{
							null,
							"$#,##0.00",
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							"$#,##0.00",
							null
			});			

			respuesta = ConstantesSet.RUTA_EXCEL + "consultaMovtos " + Utilerias.indicadorFecha() +".xls";
			File outputFile = new File(respuesta);

			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
		} catch (IOException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:ConsultaPropuestasDao, M:exportaExcel");
			respuesta = "";
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:ConsultaPropuestasDao, M:exportaExcel");
			respuesta = "";
		}
		return respuesta;
	}

	public String ejecutarRevividor(List<RevividorDto> listRevividor, List<BitacoraChequesDto> listBitChe){
		//String resultado="Resultado de los registros procesados ";
		String resultado="";
		String update="";
		String resp1 ="";
		int  verDetArchTransfer=0;
		boolean banderaUpdate=false;
		boolean bandera3200= false;
		try {
			for (int i = 0; i < listRevividor.size(); i++) {
				RevividorDto revividorDto = listRevividor.get(i);
				banderaUpdate=false;
				bandera3200=false;
				verDetArchTransfer=consultasDao.verDetalleArch(revividorDto.getNoFolioDet());
				if (verDetArchTransfer>0) {
					return resultado +="<br> El registro no se puede regresar debido a que <br> se encuentra dentro de un archivo enviado a la banca electrónica";
				}else{
				System.out.println(revividorDto.getNoFolioDet());
				//consultasDao.insertarBitacoraRentas(revividorDto,listBitChe.get(i));
				//Agregado por YEC 29.01.2016 para los movimientos 3000 con estatus N o C
				if(revividorDto.getIdTipoOperacion()==3000&& (revividorDto.getIdEstatusMov().equals("N")||revividorDto.getIdEstatusMov().equals("C"))
						&& (revividorDto.getPoHeaders() == null || revividorDto.getPoHeaders().equals("") )){
					System.out.println("movimiento 1");
					update= consultasDao.updateMovimiento(revividorDto.getNoFolioDet()+"" , "" ,"","");
					resultado+="<br>"+ revividorDto.getNoDocumento() +"-"+ update;
				}else{ 

					if(revividorDto.getIdTipoOperacion()==3000&& revividorDto.getIdEstatusMov().equals("N") && (revividorDto.getPoHeaders() != null || !revividorDto.getPoHeaders().equals("") )){
						banderaUpdate=true;
						revividorDto.setEjercicio(Integer.parseInt(revividorDto.getPeridoDescompensar()));
					}

					if(revividorDto.getIdTipoOperacion()==3200 && revividorDto.getIdEstatusMov().equals("T") && revividorDto.getIdFormaPago()==3){
						revividorDto.setIdEstatusMov("P");
						bandera3200=true;
					}else if(revividorDto.getIdTipoOperacion()==3200 && revividorDto.getIdEstatusMov().equals("T") ){
						revividorDto.setIdEstatusMov("P");
						revividorDto.setIdFormaPago(3);
					}

					if(revividorDto.getIdTipoOperacion()==3101 && revividorDto.getIdEstatusMov().equals("A")){
						if(revividorDto.getPsOrigenMov().equals("SOI"))
							revividorDto.setPsOrigenMov("SET");
					}

					if(revividorDto.getIdTipoOperacion()==1018 && revividorDto.getIdEstatusMov().equals("A")){
						if(revividorDto.getPsOrigenMov().equals("SOI"))
							revividorDto.setPsOrigenMov("SET");
					}


					/*DT_CancelacionCompensacion_OBCancelaciones cancelacionDto = new DT_CancelacionCompensacion_OBCancelaciones(
									funciones.ajustarLongitudCampo(String.valueOf(revividorDto.getNoEmpresa()), 4, "D", "", "0"),
									revividorDto.getPoHeaders(),
									String.valueOf(revividorDto.getEjercicio()));
					System.out.println("------->" + revividorDto.getPsOrigenMov());
					 */
					//if (revividorDto.getPsOrigenMov().equals("SAP")|| banderaUpdate) {
					//Map<String, Object> respuestaCancelacion = descompensar(cancelacionDto);

					//if ((Boolean)respuestaCancelacion.get("estatus")) {
					//DT_CancelacionCompensacion_ResponseCancelaciones[] respuestaCancelacionArreglo =
					//(DT_CancelacionCompensacion_ResponseCancelaciones[])
					//respuestaCancelacion.get("respuestaWS");
					//if (respuestaCancelacionArreglo != null && respuestaCancelacionArreglo.length !=0) {
					//DT_CancelacionCompensacion_ResponseCancelaciones respuesta=
					//respuestaCancelacionArreglo[0];
					//if (respuesta != null &&
					//(cancelacionDto.getNO_DOC_SAP().equals(
					//respuesta.getNO_DOC_SAP()))) {
					//Verificar la condicion para comprobar si el documento se cancelo correctamente
					//if ((respuesta.getDOC_POLIZA_SAP()!=null &&!respuesta.getDOC_POLIZA_SAP().equals("")) ||
					//(respuesta.getMSG_ERROR().indexOf("Se descompenso el documento pero no se anulï¿½ por")!=-1)) {
					if(banderaUpdate)
						resp1 = consultasDao.updateMovimiento(revividorDto.getNoFolioDet()+"" , ""+revividorDto.getPoHeaders(), ""+revividorDto.getNoEmpresa() , ""+revividorDto.getFecPropuestaStr());
					else
						resp1 = llamarRevividor(revividorDto,listBitChe.get(i));
						System.out.println("respuesta sp "+resp1);
					if(resp1.equals("Registro procesado con exito") && bandera3200)
					{
						resp1 = consultasDao.update3200(revividorDto);
					System.out.println("movimiento 2");
					resultado+="<br> "+revividorDto.getNoDocumento()+"-"+ resp1 ;
					//} else {
					//resp1 = respuesta.getMSG_ERROR() != null && 
					//		!respuesta.getMSG_ERROR().equals("") ? 
					//				respuesta.getMSG_ERROR() : 
					//				"No se pudo descompensar el documento"; 
					//resultado+="<br> "+revividorDto.getNoDocto()+"-"+ resp1 ;
					//}
					//} else {
					//resultado+="<br>"+ revividorDto.getNoDocto() +"- La respuesta no coincide con el documento enviado " ;
					//}
					//} else {
					//resultado+="<br>"+revividorDto.getNoDocto() +"- No se obtuvo respuesta de SAP";
					//}

					//} else {
					//resultado+="<br>"+ revividorDto.getNoDocto() + "-"+respuestaCancelacion.get("mensaje").toString();
					//}
					} 
					else { 
						if(resp1.equals("Registro procesado con exito")){
							resultado+="<br> "+revividorDto.getNoDocumento()+"-"+ resp1 ;
						}
						if (update.equals("")){
							System.out.println("update vacio");
							//resp1 = llamarRevividor(revividorDto,listBitChe.get(i)); se comenta porque afecta doble vez el saldo
							System.out.println("movimiento 3");
							//resultado+="<br> "+revividorDto.getNoDocumento()+"-"+ resp1 ;
						}
					}	
				}
				
			}



			}
		} catch (Exception e) {
			resultado +="<br> Ocurrio un error durante el proceso , por el cual fue detenida la operaciï¿½n";
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Consultas, C:ConsultasBusiness, M:ejecutarRevividor");
		}return resultado;
	}

	private String llamarRevividor(RevividorDto revividorDto, BitacoraChequesDto bitacoraDto) {
		String mensaje = "Error en el proceso.";
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			revividorDto.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
			revividorDto.setPsOrigenMov("SOI");
			Map<String, Object> mapRev = consultasDao.ejecutarRevividor(revividorDto);	
			if (mapRev.size() == 0)
				mensaje = "Error al regresar el documento";
			else  {
				mensaje = "Registro procesado con exito";
				try{
					consultasDao.insertarBitacoraRentas(revividorDto,bitacoraDto);
				}catch(Exception e){

				}
			}

			int iRegAfec = this.insertarBitacoraCheques(bitacoraDto);
			if (iRegAfec==0) {
				//mensaje+="\n No se guardo en bitacora"; 
			} 
		} catch (Exception e) {
			mensaje="Ocurrio un error durante el proceso";
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Consultas, C:ConsultasBusiness, M:llamarRevividor");
		}return mensaje;
	}

	private Map<String, Object> descompensar(
			DT_CancelacionCompensacion_OBCancelaciones cancelacionDto) {
		Map<String, Object> resultado = new HashMap<String, Object>();
		resultado.put("estatus", false);
		resultado.put("mensaje", "Error al procesar el registro");
		try {
			SOS_CancelacionCompensacionServiceLocator service=
					new SOS_CancelacionCompensacionServiceLocator();
			SOS_CancelacionCompensacionBindingStub stub=
					new SOS_CancelacionCompensacionBindingStub(
							new URL(service.getHTTP_PortAddress()), service);
			stub.setUsername(
					consultasDao.configuraSet(
							ConstantesSet.USERNAME_WS_CANCELACION));
			stub.setPassword(
					consultasDao.configuraSet(
							ConstantesSet.PASSWORD_WS_CANCELACION));
			DT_CancelacionCompensacion_ResponseCancelaciones[] resp=
					stub.SOS_CancelacionCompensacion(
							new DT_CancelacionCompensacion_OBCancelaciones[]{
									cancelacionDto
							} );

			resultado.put("estatus", true);
			resultado.put("respuestaWS", resp);

		} catch ( MalformedURLException e1) {
			resultado.put("mensaje","No se pudo conectar a SAP.");
			e1.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + e1.toString() 
			+ "P:Consultas, C:ConsultasBusiness, M:descompensar");
		} catch (AxisFault e1) {
			resultado.put("mensaje","SAP ha tenido un problema al procesar los datos.");
			e1.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + e1.toString() 
			+ "P:Consultas, C:ConsultasBusiness, M:descompensar");
		} catch (Exception e) {
			resultado.put("mensaje","Error al procesar el registro");
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Consultas, C:ConsultasBusiness, M:descompensar");
		} return resultado;
	}

	public Map<String, Object> cancelarMovimientos(List<RevividorDto> listRevividor, List<BitacoraChequesDto> listBitChe)
	{
		Map<String, Object> mapRet = new HashMap<String, Object>();
		Map<String, Object> mapRev = new HashMap<String, Object>();
		int iIdUsuario = 0;
		int iRegAfec = 0;
		int iRegDetArch = 0;
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			iIdUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			for(int i = 0; i < listRevividor.size(); i++)
			{
				//Validaciones antes de la ejecuciï¿½n del revividor
				iRegDetArch = consultasDao.buscarRegDetArchTransfer(listRevividor.get(i).getNoFolioRef(), listRevividor.get(i).getIdBanco());
				/*    	conhist = "CONHIST", sMensaje)(12)
					    consolicitud = "CONSOLI"(13)
					    mfCancelaIng = "CANING" (14) ' Cancela Ingresos
					    mfCancelaEgr = "CANEGR" (15)   ' Cancela Egresos
					    mfRegresaEgr = "REGEGR" (16)  ' Regresa Egresos
					    mfCancelaTrasp = "CANTRP"(17)  ' Cancela Traspasos*/

				/*
				 * 
				//COMENTO PARA VALIDAR SOLO PROCESO. HAY QUE ANALIZAR FACULTADES DE USUARIOS...
				//COMENTO ORLANDO RMZ 19-11-13				 *
				 * 
				 *  
				if(!globalSingleton.obtenerPerfilUsuario(15))
				{
					if(listRevividor.get(i).getIdTipoOperacion() == 3000 
							&& (listRevividor.get(i).getIdEstatusMov().equals("C") 
									|| listRevividor.get(i).getIdEstatusMov().equals("N")))
					{
						if(!globalSingleton.obtenerPerfilUsuario(13))
						{
							mapRet.put("msgUsuario", "No tiene la facultad para poder cancelar las solicitudes");
							return mapRet;
						}
					}
					else
					{
						mapRet.put("msgUsuario", "Solo tiene facultad para poder cancelar las solicitudes");
						return mapRet;
					}
				}
				 */

				if(listRevividor.get(i).getPsOrigenMov() != null && listRevividor.get(i).getPsOrigenMov().trim().equals("H"))
				{
					if(!globalSingleton.obtenerPerfilUsuario(12))
					{
						mapRet.put("msgUsuario", "No tiene la facultad para eliminar movimientos historicos");
						return mapRet;
					}
				}

				if((listRevividor.get(i).getPsOrigenMov() != null && listRevividor.get(i).getPsOrigenMov().trim().equals("M"))
						&& iRegDetArch > 0)
				{
					if(iRegDetArch > 0)
					{
						mapRet.put("msgUsuario", "ï¿½Este movimiento se cancela en control de archivos (Banca Electrï¿½nica) !");
						return mapRet;
					}
				}
				else
				{
					if(iRegDetArch == 0)
					{
						iRegDetArch = consultasDao.buscarRegDetArchTransfer(listRevividor.get(i).getNoFolioDet(), listRevividor.get(i).getIdBanco());
						if((listRevividor.get(i).getPsOrigenMov() != null && listRevividor.get(i).getPsOrigenMov().trim().equals("M"))
								&& iRegDetArch > 0)
						{
							mapRet.put("msgUsuario", "ï¿½Este movimiento se cancela en control de archivos (Banca Electrï¿½nica) !");
							return mapRet;
						}
					}
				}

				if(!globalSingleton.obtenerValorConfiguraSet(357).equals("SI"))
				{
					if((listRevividor.get(i).getIdTipoOperacion() >= 3702 && listRevividor.get(i).getIdTipoOperacion() <= 3709)
							|| (listRevividor.get(i).getIdTipoOperacion() >= 3800 && listRevividor.get(i).getIdTipoOperacion() <= 3818)
							&& (listRevividor.get(i).getIdTipoMovto() != null && listRevividor.get(i).getIdTipoMovto().equals("I")))
					{
						mapRet.put("msgUsuario", "No se puede regresar este tipo de traspaso");
						return mapRet;
					}
				}
				//Terminan validaciones

				listRevividor.get(i).setIdUsuario(iIdUsuario);
				mapRev = consultasDao.ejecutarRevividor(listRevividor.get(i));	

				if(!mapRev.get("result").toString().trim().equals("0"))
				{
					if(mapRev.get("result").toString().trim().equals("2000"))
						mapRet.put("msgUsuario", "ï¿½Este Movimiento no se puede cancelar!");
					else
						mapRet.put("msgUsuario", "ï¿½Este en revividor #" + mapRev.get("result").toString());

					return mapRet;
				}

				if(iRegDetArch > 0)
					consultasDao.modificarMovimientoArchivo(listRevividor.get(i).getNoFolioDet());

				iRegAfec = this.insertarBitacoraCheques(listBitChe.get(i));

				if(iRegAfec > 0)
					mapRet.put("msgUsuario", "Regreso(s) procesado(s) corectamente");
				else
					mapRet.put("msgUsuario", "Ocurrio una error en bitacora cheques");
			}

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Consultas, C:ConsultasBusiness, M:cancelarMovimientos");
		}
		return mapRet;
	}

	/**
	 * Mï¿½todo para insertar en bitacora_cheques
	 * @param dtoChe
	 * @return
	 */
	public int insertarBitacoraCheques(BitacoraChequesDto dtoChe){
		int iRegAfec = 0;
		try{
			iRegAfec = consultasDao.insertarBitacoraCheques(dtoChe);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Consultas, C:ConsultasBusiness, M:insertarBitacoraCheques");
		}
		return iRegAfec;
	}

	/**
	 * Mï¿½todo para obtener los proveedores para el llenado del
	 * combo de proveedor, utilizado en BuscarMovimiento.js, ConsultaDeMovimientos.js
	 * @param prefijoProv
	 * @return
	 */
	public List<LlenaComboGralDto> obtenerProveedores(String prefijoProv, int iNoProveedor, int noEmpresa)
	{
		List<LlenaComboGralDto> listProv = new ArrayList<LlenaComboGralDto>();
		try
		{
			listProv = consultasDao.consultarProveedores(prefijoProv, iNoProveedor, noEmpresa);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Consultas, C:ConsultasBusiness, M:consultarProveedores");
		}
		return listProv;
	}

	/**
	 * Este mï¿½todo consulta como primera opcion la tabla de zimp_fact,
	 * si no tiene datos la realiza sobre movimiento e hist_solicitud.
	 * Se retorna una lista de tipo map para retornar en un mismo mï¿½todo,
	 * diferentes tipos de consulta y realizar el llenado del grid.
	 * @param dto
	 * @return
	 */

	public List<Map<String, Object>> consultarMovto(ParamZimpFactDto dto)
	{
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listZimp = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listMovto = new ArrayList<Map<String, Object>>();
		try
		{
			listZimp = consultasDao.consultarZimpFact(dto);

			if(listZimp.size() > 0)
				listMap = listZimp;
			else{
				ParamBusSolicitudDto dtoSol = new ParamBusSolicitudDto();
				dtoSol.setIdTipoOperacion(3000);
				dtoSol.setNomTabla("hist_solicitud");
				dtoSol.setNoEmpresa(dto.getNoEmpresa());
				dtoSol.setNoDocto(dto.getNoDocto());
				dtoSol.setPeriodo(dto.getPeriodo());
				dtoSol.setSecuencia("");
				dtoSol.setFechaOperacion("");
				dtoSol.setIdProveedor(dto.getNoProveedor());
				listMovto = consultasDao.consultarSolicitudMovto(dtoSol);
				listMap = listMovto;
			}
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Consultas, C:ConsultasBusiness, M:consultarMovto");
		}
		return listMap;
	}

	public StringBuffer realizarSeguimientoMovto(ParamBusSolicitudDto dto, boolean bZimpFact)
	{
		StringBuffer sbSeg = new StringBuffer();
		String noCliente = "";
		try
		{
			if(dto.getFormaPago()==6){
				noCliente = consultasDao.obtenerNoClienteInterempresas(dto.getIdProveedor());
			}else if(dto.getIdProveedor().equals("") || bZimpFact==true){
				noCliente = consultasDao.obtenerNoCliente(dto.getIdProveedor());
			}else{
				noCliente = dto.getIdProveedor();
			}
			int cliente = Integer.parseInt(noCliente);
			dto.setNoBenef(cliente);
			if(bZimpFact)
				sbSeg.append(this.realizarSegZimpfact(dto));
			else
				sbSeg.append(this.realizarSegSol(dto));
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Consultas, C:ConsultasBusiness, M:consultarMovto");
		}
		return sbSeg;
	}

	/**
	 * Mï¿½todo para armar la redacciï¿½n del seguimiento del movimiento
	 * con las solicitudes, en tablas como movimiento, hist_solicitud, hist_movimiento,
	 * utilizado en BuscarMovimiento.js
	 * @param dto
	 * @return
	 */
	public StringBuffer realizarSegSol(ParamBusSolicitudDto dto)
	{
		List<MovimientoDto> lConsMovto = new ArrayList<MovimientoDto>();
		List<MovimientoDto> lBusPadre = new ArrayList<MovimientoDto>();
		StringBuffer sbSeg = new StringBuffer();
		int iGrupo = 0;
		String sEstatus = "";
		try
		{
			lConsMovto = consultasDao.consultarSeguimientoMovtos(dto);
			for(int i = 0; i < lConsMovto.size(); i ++)
			{
				iGrupo = lConsMovto.get(i).getGrupoPago();
				if(lConsMovto.get(i).getDescEstatus() != null && lConsMovto.get(i).getDescEstatus().trim().equals("HIJO")
						&& iGrupo > 0)
				{
					lBusPadre = consultasDao.consultarRegistroPadre(iGrupo, lConsMovto.get(i).getNoDocto());
					if(lBusPadre.size() > 0)
					{
						//SE CAMBIO LEYENDA YA QUE AQUI SOLO ENTRAN PAGOS AGRUPADOS
						sbSeg.append("EL MOVIMIENTO ES AGRUPADO DE FORMA PAGO " + lBusPadre.get(0).getDescFormaPago());
						sbSeg.append(" ,ESTA ");
						// SE AGREGO CASE PARA TOMAR EN CUENTA LOS PENDIENTES
						if(lBusPadre.get(0).getDescEstatus() != null && lBusPadre.get(0).getDescEstatus().trim().equals("APLICADO"))
						{
							sbSeg.append("PAGADO ");
							sEstatus = "PAGADO";
						}//CUANDO ESTATUS SEA PENDIENTE IDENTIFICA SI NO SE A IMPRESO O ENVIADO A LA BANCA
						else if(lBusPadre.get(0).getDescEstatus() != null && lBusPadre.get(0).getDescEstatus().trim().equals("PENDIENTE"))
						{
							if(lBusPadre.get(0).getDescFormaPago() != null && lBusPadre.get(0).getDescFormaPago().trim().equals("CHEQUE"))
								sbSeg.append("PENDIENTE POR IMPRIMIR ");
							else if(lBusPadre.get(0).getDescFormaPago() != null && lBusPadre.get(0).getDescFormaPago().trim().equals("TRANSFERENCIA"))
								sbSeg.append("PENDIENTE POR ENVIAR A LA BANCA ELECTRï¿½NICA ");
							else if(lBusPadre.get(0).getDescFormaPago() != null && !lBusPadre.get(0).getDescFormaPago().trim().equals("HIJO"))
							{
								sbSeg.append(funciones.validarCadena(lBusPadre.get(0).getDescEstatus()));
							}
						}
						sbSeg.append(" EL DIA ");
						sbSeg.append(lBusPadre.get(0).getFecValor() != null && lBusPadre.get(0).getFecValor().equals("") ? funciones.ponerFechaSola(lBusPadre.get(0).getFecValor()) : "");
						//sbSeg.append(" EL DIA ");
						//sbSeg.append(lBusPadre.get(0).getFecValor() != null && lBusPadre.get(0).getFecValor().equals("") ? funciones.ponerFechaSola(lBusPadre.get(0).getFecValor()) : "");

						//SE QUITARON LOS DATOS DE LA CAJA YA QUE YA ESTAN MOSTRANDOSE EN EL PAGO 3200 
						//pdfecha_mov = Str(rstpapa!fec_valor)
						if(lBusPadre.get(0).getCveControl() != null && !lBusPadre.get(0).getCveControl().equals("")
								&& lBusPadre.get(0).getIdTipoOperacion() == 3000)
							sbSeg.append(" Y FUE INSERTADA EN  LA PROPUESTA " + lBusPadre.get(i).getCveControl());

						sbSeg.append(" Y ESTA AGRUPADO EN EL GRUPO_PAGO " + iGrupo);
						//SE AGREGO EL DOCUMENTO PADRE DEL GRUPO 
						sbSeg.append(" DOCUMENTO " + lBusPadre.get(0).getNoDocto());
						sbSeg.append(" CON UN IMPORTE DE GRUPO DE $" + funciones.ponerFormatoImporte(lBusPadre.get(0).getImporte()) + " " + lBusPadre.get(0).getDescDivisa());

						sEstatus = funciones.validarCadena(lBusPadre.get(0).getDescEstatus());

						if(funciones.validarCadena(lBusPadre.get(0).getDescEstatus()).equals("CANCELADO")){
							sbSeg.append(", FUE CANCELADO POR EL USUARIO ");
							sbSeg.append(lConsMovto.get(i).getDescUsuario()!= null && !lConsMovto.get(i).getDescUsuario().equals("")? lConsMovto.get(i).getDescUsuario().equals("") : "DESCONOCIDO");
						}
					}
				}
				else
				{
					sbSeg.append("EL MOVIMIENTO SE ENCONTRO COMO " + lConsMovto.get(i).getDescTipoOperacion());
					//CONDICION PARA LA FORMA DE PAGO COMPRAVENTA DE TRANSFERENCIAS 
					if(lConsMovto.get(i).getOrigenMov() != null && lConsMovto.get(i).getOrigenMov().trim().equals("CVT")
							&& lConsMovto.get(i).getIdTipoOperacion() != 3000)
						sbSeg.append(" DE FORMA PAGO, COMPRA VENTA DE TRANSFERENCIA ");
					else
						sbSeg.append(" DE FORMA PAGO " + lConsMovto.get(i).getDescFormaPago());

					//CONDICION PARA QUE SOLO A LOS PAGOS Y NO A LAS SOLICITUDES LES AGREGUE DATOS DE PAGO
					if((lConsMovto.get(i).getIdTipoOperacion() == 3200 || lConsMovto.get(i).getIdTipoOperacion() == 3701)
							&& (funciones.validarCadena(lConsMovto.get(i).getDescFormaPago()).equals("TRANSFERENCIA") ||
									funciones.validarCadena(lConsMovto.get(i).getDescFormaPago()).equals("TRASPASO")
									|| funciones.validarCadena(lConsMovto.get(i).getDescFormaPago()).equals("CHEQUE")
									|| funciones.validarCadena(lConsMovto.get(i).getDescFormaPago()).equals("CARGO EN CUENTA"))
							&& (!funciones.validarCadena(lConsMovto.get(i).getDescEstatus()).equals("CANCELADO")))
					{
						sbSeg.append(" ,ESTA ");

						if(funciones.validarCadena(lConsMovto.get(i).getDescEstatus()).trim().equals("APLICADO"))
						{
							sbSeg.append("PAGADO");
							sEstatus = "PAGADO";
						}
						else if(funciones.validarCadena(lConsMovto.get(i).getDescEstatus()).equals("PENDIENTE"))
						{
							if(funciones.validarCadena(lConsMovto.get(i).getDescFormaPago()).trim().equals("CHEQUE"))
								sbSeg.append(" PENDIENTE POR IMPRIMIR ");
							else if(funciones.validarCadena(lConsMovto.get(i).getDescFormaPago()).trim().equals("TRANSFERENCIA"))
								sbSeg.append("PENDIENTE POR ENVIAR A LA BANCA ELECTRï¿½NICA");

							sEstatus = lConsMovto.get(i).getDescEstatus();
						}
						else if(!funciones.validarCadena(lConsMovto.get(i).getDescEstatus()).equals("HIJO"))
						{
							sbSeg.append(lConsMovto.get(i).getDescEstatus());
							sEstatus = lConsMovto.get(i).getDescEstatus();
						}
						sbSeg.append(" EL DIA " + lConsMovto.get(0).getFecValor() != null && lConsMovto.get(0).getFecValor().equals("") 
								? funciones.ponerFechaSola(lConsMovto.get(0).getFecValor()) : "");

						sbSeg.append(", EN LA CAJA " + funciones.validarCadena(lConsMovto.get(i).getDescCaja()));
					}

					if((!funciones.validarCadena(lConsMovto.get(i).getCveControl()).equals(""))
							&& lConsMovto.get(i).getIdTipoOperacion() == 3000)
						sbSeg.append(" Y FUE INSERTADA EN LA PROPUESTA " + lConsMovto.get(i).getCveControl());

					//CONDICIï¿½N PARA QUE A LA SOLICITUD LE ASIGNE DIVISA E IMPORTE ORIGINALES
					if(lConsMovto.get(i).getIdTipoOperacion() == 3000)
					{
						sbSeg.append(" CON UN IMPORTE DE $ " + funciones.ponerFormatoImporte(lConsMovto.get(i).getImporteOriginal()) + " " + lConsMovto.get(i).getIdDivisaOriginal());
						sbSeg.append(!funciones.validarCadena(lConsMovto.get(i).getDescUsuario()).equals("") ? " FUE DADO DE ALTA POR " + lConsMovto.get(i).getDescUsuario() : " ");
					}
					else
						sbSeg.append(" CON UN IMPORTE DE $ " + funciones.ponerFormatoImporte(lConsMovto.get(i).getImporte()) + " " + lConsMovto.get(i).getDescDivisa());

					//CONDICION PARA QUE SOLO A LOS PAGOS Y NO A LAS SOLICITUDES LES AGREGUE DATOS DE PAGO
					if((lConsMovto.get(i).getIdTipoOperacion() == 3200 || lConsMovto.get(i).getIdTipoOperacion() == 3701)
							&& (funciones.validarCadena(lConsMovto.get(i).getDescFormaPago()).equals("TRANSFERENCIA") ||
									funciones.validarCadena(lConsMovto.get(i).getDescFormaPago()).equals("TRASPASO")
									|| funciones.validarCadena(lConsMovto.get(i).getDescFormaPago()).equals("CHEQUE")
									|| funciones.validarCadena(lConsMovto.get(i).getDescFormaPago()).equals("CARGO EN CUENTA"))
							&& (!funciones.validarCadena(lConsMovto.get(i).getDescEstatus()).equals("CANCELADO")))
					{
						if(!funciones.validarCadena(lConsMovto.get(i).getDescUsuario()).equals("") && (lConsMovto.get(i).getIdTipoOperacion() == 3200 || lConsMovto.get(i).getIdTipoOperacion() == 3201))
							sbSeg.append(" , FUE PAGADO POR EL USUARIO " + lConsMovto.get(i).getDescUsuario());
						else
							sbSeg.append(" , ESTA PENDIENTE DE PAGO Y VA ");
						if(!funciones.validarCadena(lConsMovto.get(i).getDescBancoBenef()).equals(""))
							sbSeg.append(" A LA CUENTA (" + lConsMovto.get(i).getDescBancoBenef() + " " + lConsMovto.get(i).getIdChequeraBenef() + ") ");
						if(!funciones.validarCadena(lConsMovto.get(i).getDescBanco()).equals(""))
							sbSeg.append(" DE LA CUENTA (" + lConsMovto.get(i).getDescBanco() + " " + lConsMovto.get(i).getIdChequera() + ") " );
						if(!funciones.validarCadena(lConsMovto.get(i).getDescUsuarioMod()).equals(""))
							sbSeg.append(", FUE MODIFICADO POR EL USUARIO " + lConsMovto.get(i).getDescUsuarioMod());

						sEstatus = lConsMovto.get(i).getDescEstatus();
					}
					if(funciones.validarCadena(lConsMovto.get(i).getDescEstatus()).equals("CANCELADO")){
						sbSeg.append(", FUE CANCELADO POR EL USUARIO ");
						sbSeg.append(lConsMovto.get(i).getDescUsuario()!= null && !lConsMovto.get(i).getDescUsuario().equals("")? lConsMovto.get(i).getDescUsuario() : "DESCONOCIDO");
						sEstatus = "CANCELADO";
					}
				}
				sbSeg.append("\r\n\r\n").toString();
			}
			//Se setea el valor del estatus para obtenerlo en el js y 
			//poder realizar el sequimiento de las pantallas
			//en la pantalla de BuscarMovimiento.js
			sbSeg.append(!sEstatus.equals("") ? "@" + sEstatus : ""); 
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Consultas, C:ConsultasBusiness, M:realizarSegSol");
		}
		return sbSeg;
	}

	/**
	 * Mï¿½todo para realizar el seguimiento 
	 * @param dto
	 * @return
	 */
	public StringBuffer  realizarSegZimpfact(ParamBusSolicitudDto dto)
	{
		StringBuffer sbSeg = new StringBuffer();
		String sEstatus = "";
		String sCausa = "";
		try
		{
			sEstatus = dto.getEstatus() != null && !dto.getEstatus().equals("") ? dto.getEstatus() : ""; 
			if(sEstatus.trim().equals("I"))
			{
				sbSeg.append("EL MOVIMIENTO FUE IMPORTADO EL DIA " + dto.getFechaOperacion() + "\n");
				sEstatus = "IMPORTADO";
			}
			else if(sEstatus.trim().equals("R"))
			{
				sbSeg.append("EL MOVIMIENTO FUE RECHAZADO EN IMPORTACIï¿½N PORQUE " + dto.getSCausa());
				sbSeg.append(!funciones.validarCadena(dto.getFechaOperacion()).equals("") ? " EL DIA " + dto.getFechaOperacion() + "\n": "\n");
				sEstatus = "RECHAZADO EN IMPORTACION";
			}
			else if(sEstatus.trim().equals("F"))
			{
				sEstatus = "RECHAZADO EN IMPORTACION";
				if(dto.getFormaPago() != 6)
				{
					sCausa = consultasDao.consultarCausaChequera(dto.getNoBenef(), dto.getNoEmpresa());
					if(sCausa != null && !sCausa.equals(""))
						sbSeg.append("EL MOVIMIENTO FUE RECHAZADO EN IMPORTACIï¿½N PORQUE " + sCausa + "\n");
					else
						sbSeg.append("EL MOVIMIENTO FUE RECHAZADO EN IMPORTACIï¿½N PORQUE " + dto.getSCausa() + "\n");
					sbSeg.append(!funciones.validarCadena(dto.getFechaOperacion()).equals("") ? " EL DIA " + dto.getFechaOperacion() + "\n": "\n");
				}
				else
				{
					sbSeg.append("EL MOVIMIENTO FUE RECHAZADO EN IMPORTACIï¿½N PORQUE " + dto.getSCausa() + "\n");
					sbSeg.append(!funciones.validarCadena(dto.getFechaOperacion()).equals("") ? " EL DIA " + dto.getFechaOperacion() + "\n": "\n");
				}
			}
			else if(sEstatus.trim().equals("P"))
			{
				sEstatus = "PENDIENTE EN IMPORTACION";
				sbSeg.append("EL MOVIMIENTO SE ENCUENTRA PENDIENTE EN IMPORTACIï¿½N " + dto.getSCausa() + "\n");
			}
			else if(sEstatus.trim().equals("C"))
			{
				sEstatus = "POR CANCELAR EN IMPORTACION";
				sbSeg.append("EL MOVIMIENTO SE ENCUENTRA POR CANCELAR EN IMPORTACIï¿½N " + dto.getSCausa() + "\n");
				sbSeg.append(!funciones.validarCadena(dto.getFechaOperacion()).equals("") ? " CON LA SIGUIENTE FECHA " + dto.getFechaOperacion() + "\n": "\n");
			}
			if(!funciones.validarCadena(dto.getEstatusCompensa()).equals("") && dto.getEstatusCompensa().trim().equals("C"))
				sbSeg.append(" Y YA HA SIDO CONTABILIZADO \n");

			sbSeg.append("\r\n");
			sbSeg.append(this.realizarSegSol(dto));
			if(sbSeg.toString().indexOf("@") <= 0 && !sEstatus.equals(""))
				sbSeg.append("@" + sEstatus);

		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Consultas, C:ConsultasBusiness, M:realizarSegZimpfact");
		}
		return sbSeg;
	}

	/**
	 * Este mï¿½todo retorna una cadena con la informaciï¿½n de 
	 * las pantallas donde se podria encontrar el movimiento.
	 * @param sEstatus
	 * @return
	 */
	public String seguimientoPantallas(String sEstatus)
	{
		StringBuffer sbPantallas = new StringBuffer();
		StringBuffer sbSeg = new StringBuffer();
		try
		{
			if(sEstatus.trim().equals("IMPORTADO"))
				sbPantallas.append("Interfaz/Importacion de Egresos");
			else if(sEstatus.trim().equals("RECHAZADO EN IMPORTACION")
					|| sEstatus.trim().equals("PENDIENTE EN IMPORTACION")
					|| sEstatus.trim().equals("POR CANCELAR EN IMPORTACION")
					|| sEstatus.trim().equals("CANCELADO EN IMPORTACION"))
				sbPantallas.append("No se encuentra en el set");
			else if(sEstatus.trim().equals("PAGADO"))
				sbPantallas.append("Consultas/Consulta de movimientos");
			else if(sEstatus.trim().equals("CAPTURADO"))
			{
				sbPantallas.append("En propuesta: Egresos/Consulta de Propuestas,Pago propuestas \n");
				sbPantallas.append("Fuera de propuesta:Egresos/Pago Manual,Pago Automatico");
			}
			else if(sEstatus.trim().equals("IMPRESO"))
				sbPantallas.append("Caja/Cheques por entregar,Consultas/Consulta de movimientos,Impresion/Reimpresion de Cheques");
			else if(sEstatus.trim().equals("REIMPRESO"))
				sbPantallas.append("Consultas/Consulta de movimientos,Impresion/Reimpresion de Cheques");
			else if(sEstatus.trim().equals("PAGO EFECTIVO"))
				sbPantallas.append("Consultas/Consulta de movimientos,Caja/Arqueo");
			else if(sEstatus.trim().equals("PENDIENTE CHQ.EXT."))
				sbPantallas.append("Egresos/Cheques Pendientes de elaborar,Consultas/Consulta de movimientos");
			else if(sEstatus.trim().equals("CONF. TRANSFERIDO"))
				sbPantallas.append("Banca E/Control de Archivos,Consultas/Consulta de movimientos");
			else if(sEstatus.trim().equals("PENDIENTE"))
				sbPantallas.append("Banca E/Envio de transferencia,Envio de Cheque Ocurre,Impresion/Impresion Cheques");
			else if(sEstatus.trim().equals("TRANSFERIDO"))
				sbPantallas.append("Banca E/Control de Archivos");
			else if(sEstatus.trim().equals("CANCELADO"))
				sbPantallas.append("Consultas/Consulta de Movimientos");
			else if(sEstatus.trim().equals("RECHAZADO"))
				sbPantallas.append("Consultas/Consulta de movimientos,Banca E/Pagos Rechazados");

			if(!sbPantallas.toString().equals(""))
				sbSeg.append("EL MOVIMIENTO SE ENCUENTRA EN ESTATUS " + sEstatus 
						+ " Y LO PUEDES ENCONTRAR EN: \n" + sbPantallas.toString());
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Consultas, C:ConsultasBusiness, M:seguimientoPantallas");
		}
		return sbSeg.toString();
	}

	public List<LlenaComboChequeraDto> llenaComboChequera(int idBanco, int noEmpresa, String idDivisa) {
		return consultasDao.llenaComboChequera(idBanco, noEmpresa, idDivisa);
	}

	public List<SaldoChequeraDto> consultarSaldosCuentas(int noEmpresa, int idBanco, String idChequera, int idUsuario, String idDivisa) {
		return consultasDao.consultarSaldosCuentas(noEmpresa, idBanco, idChequera, idUsuario, idDivisa);
	}

	public JRDataSource reportePosicionChequeras(SaldoChequeraDto dtoIn, int noUsuario){
		JRMapArrayDataSource jrDataSource = null;
		List<Map<String, Object>> listReport = new ArrayList<Map<String, Object>>();

		try {
			listReport = consultasDao.reportePosicionChequeras(dtoIn, noUsuario);
			jrDataSource = new JRMapArrayDataSource(listReport.toArray());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasBusiness, M:reportePosicionChequeras");
		}
		return jrDataSource;
	}

	//getters and setters
	public ConsultasDao getConsultasDao() {
		return consultasDao;
	}

	public void setConsultasDao(ConsultasDao consultasDao){
		this.consultasDao = consultasDao;
	}

	public GlobalBusiness getGlobalBusiness() {
		return globalBusiness;
	}

	public void setGlobalBusiness(GlobalBusiness globalBusiness) {
		this.globalBusiness = globalBusiness;
	}

	public String reconstruyeChequera(int ntEmpresa, int ntBanco, String strChequera) {

		String strRespuesta = "";
		int ntAfect = 0;

		Double dblAbonos;
		Double dblCargos;

		dblCargos = 0.0;
		dblAbonos = 0.0;

		List<SaldoDto> saldosDto = null;

		try
		{
			saldosDto = consultasDao.getTotalesIngresosEgresos(ntEmpresa, ntBanco, strChequera);

			if ( saldosDto.size() == 0 )
				return (strRespuesta = "No se encontraron movimientos en la chequera." );

			for( SaldoDto saldo: saldosDto ){

				if( saldo.getIdTipoMovto().equals( "E" ) ){
					dblCargos+= saldo.getImporte();
				}

				if( saldo.getIdTipoMovto().equals( "I" ) ){
					dblAbonos+= saldo.getImporte();
				}

			}//End for

			ntAfect = consultasDao.updateSaldo( ntEmpresa, ntBanco, strChequera, dblCargos, dblAbonos);

			if ( ntAfect > 0 )
				return (strRespuesta = "La chequera fue fue actualizada.");


		}catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasBusiness, M:reconstruyeChequera");
		}

		return strRespuesta;

	}

	public String reconstruyeChequeraHistotica(int ntEmpresa, int ntBanco,String strChequera, String strFecha) throws ParseException {

		String strRespuesta = "";
		List<SaldoDto> saldosDto = null;
		double dblSaldoInicial = 0.0;
		double dblIngresos = 0.0;
		double dblEgresos = 0.0;
		double dblSaldoFinal = 0.0;
		//int ntAfect = 0;
		int contador = 0;

		strFecha = funciones.cambiarFecha(strFecha, true);

		if( obtenerFechaHoy().equals( strFecha ) )
			return (strRespuesta = "La fecha debe ser anterior al dia de hoy." ); 

		saldosDto = consultasDao.getSaldosHistoricos(ntEmpresa, ntBanco, strChequera, strFecha);

		if( saldosDto == null)
			return (strRespuesta = "No existen saldos historicos de esta chequera." );

		if( saldosDto.size() == 0 )
			return (strRespuesta = "No existen saldos historicos de esta chequera." );

		for( SaldoDto saldo: saldosDto ){

			if( contador == 0 )
				dblSaldoInicial = saldo.getSaldoInicial();

			strFecha = saldo.getFecValor();
			strFecha = funciones.cambiarFecha(strFecha, true);

			dblIngresos = consultasDao.getTotalesIngresosEgresos(ntEmpresa, ntBanco, strChequera, strFecha, "I");
			dblEgresos = consultasDao.getTotalesIngresosEgresos(ntEmpresa, ntBanco, strChequera, strFecha, "E");

			dblSaldoFinal = (dblSaldoInicial + dblIngresos - dblEgresos);

			consultasDao.updateSaldo( ntEmpresa, ntBanco, strChequera, dblSaldoInicial, dblEgresos, dblIngresos,dblSaldoFinal, strFecha);

			dblSaldoInicial = dblSaldoFinal;

			contador ++;

		}//End for

		consultasDao.updateSaldoInicialHoy( ntEmpresa, ntBanco, strChequera, dblSaldoInicial);

		strRespuesta = reconstruyeChequera(ntEmpresa, ntBanco, strChequera);

		return strRespuesta;

	}//End function reconstruyeChequeraHistotica

	/**
	 * Para graficar el total de Ingresos/Egresos por empresa y fecha.
	 * @param noEmpresa
	 * @param sFecIni
	 * @param sFecFin
	 * @return
	 */
	public List<Map<String, Object>> graficarTotalMovimientos (int noEmpresa, String sFecIni, String sFecFin)
	{
		List<Map<String, Object>> lMovs = new ArrayList<Map<String, Object>>();

		lMovs = consultasDao.graficarTotalMovimientos(noEmpresa, sFecIni, sFecFin);

		return lMovs;
	}

	public String leerArchivoExcel() {
		int result = 0;
		String resp = "";
		String chequeras = "";

		try {
			String rutaArchivo = consultasDao.configuraSet(3009) + "saldos.xls";

			Workbook archivoExcel = Workbook.getWorkbook(new File(rutaArchivo));

			for (int sheetNo = 0; sheetNo < archivoExcel.getNumberOfSheets(); sheetNo++) {
				if(sheetNo == 0) {
					Sheet hoja = archivoExcel.getSheet(sheetNo);
					int numFilas = hoja.getRows();
					System.out.println("Nombre de la Hoja\t" + archivoExcel.getSheet(sheetNo).getName());

					for (int fila = 1; fila < numFilas-1; fila++) { // Recorre cada fila de la hoja
						if(hoja.getCell(9, fila).getContents().toString().toUpperCase().equals("S") &&
								!hoja.getCell(5, fila).getContents().toString().equals("") && 
								!hoja.getCell(6, fila).getContents().toString().equals("") && 
								!hoja.getCell(7, fila).getContents().toString().equals("") && 
								!hoja.getCell(8, fila).getContents().toString().equals("")) {
							result = consultasDao.buscaChequera(Integer.parseInt(hoja.getCell(0, fila).getContents()), hoja.getCell(3, fila).getContents().toString());

							if(result != 0) {
								result = consultasDao.updateSaldos(Integer.parseInt(hoja.getCell(0, fila).getContents()), 
										hoja.getCell(3, fila).getContents().toString(), Double.parseDouble(hoja.getCell(5, fila).getContents()), 
										Double.parseDouble(hoja.getCell(6, fila).getContents()), Double.parseDouble(hoja.getCell(7, fila).getContents()),
										Double.parseDouble(hoja.getCell(8, fila).getContents()));
							}else
								chequeras += hoja.getCell(3, fila).getContents().toString() + ", ";
						}
					}
					if(!chequeras.equals("") || result == 0)
						resp = "Error al actualizar los saldos, chequeras no encontradas: " + chequeras;
					else
						resp = "Actualizaciï¿½n correcta";
				}
			}
		} catch (Exception ioe) { ioe.printStackTrace(); }
		return resp;
	}

	public int validaFacultad(int idFacultad) {
		return consultasDao.validaFacultad(idFacultad);
	}

	public List<SaldoChequeraDto> obtenerUsuarios() {
		return consultasDao.obtenerUsuarios();
	}

	public List<SaldoChequeraDto> obtenerBancos(int idUsuario, String idDivisa) {
		return consultasDao.obtenerBancos(idUsuario, idDivisa);
	}

	public List<SaldoChequeraDto> obternerChequeras(int idBanco, int idUsuario, String idDivisa) {
		return consultasDao.obternerChequeras(idBanco, idUsuario, idDivisa);
	}
	
	public List<SaldoChequeraDto> obtenerNvoBancos() {
		return consultasDao.obtenerNvoBancos();
	}
	
	public String insertaNuevo(String datos) {
		String resp = "";
		Gson gson = new Gson();
		List<Map<String, String>> dato = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		String band = dato.get(0).get("bandera");
		
		try {
			if(band.equals("N")){
				if(consultasDao.insertaNuevo(datos) == 1)
					resp = "Registro exitoso!!";
				else
					resp = "Error al registrar!!";
			}else if(band.equals("M")){
				if(consultasDao.modificarContrato(datos) == 1)
					resp = "Registro modificado!";
				else
					resp = "Error al modificar!";
			}
		}catch (Exception ioe) { ioe.printStackTrace(); }
		return resp;
	}
	
	public String insertaNuevoCP(String datos) {
		String resp = "";
		Gson gson = new Gson();
		List<Map<String, String>> dato = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		String band = dato.get(0).get("bandera");
		
		try {
			if(band.equals("N")){
				if(consultasDao.insertaNuevoCP(datos) == 1)
					resp = "Registro exitoso!!";
				else
					resp = "Error al registrar!!";
			}else if(band.equals("M")){
				if(consultasDao.modificarContratoCP(datos) == 1)
					resp = "Registro modificado!";
				else
					resp = "Error al modificar!";
			}
		}catch (Exception ioe) { ioe.printStackTrace(); }
		return resp;
	}
	
	public List<SaldoChequeraDto> obtenerRegistros(String params) {
		return consultasDao.obtenerRegistros(params);
	}
	
	public List<SaldoChequeraDto> obtenerRegistrosCP(String params) {
		return consultasDao.obtenerRegistrosCP(params);
	}
	
	public JRDataSource reporteCuentasPersonales(SaldoChequeraDto dtoIn) {
		JRMapArrayDataSource jrDataSource = null;
		List<Map<String, Object>> listReport = new ArrayList<Map<String, Object>>();
		
		try {
			listReport = consultasDao.reporteCuentasPersonales(dtoIn);
	        jrDataSource = new JRMapArrayDataSource(listReport.toArray());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasBusiness, M:reporteCuentasPersonales");
		}
		return jrDataSource;
	}
	
	public JRDataSource reporteContratosProveedores(SaldoChequeraDto dtoIn) {
		JRMapArrayDataSource jrDataSource = null;
		List<Map<String, Object>> listReport = new ArrayList<Map<String, Object>>();
		try {
			listReport = consultasDao.reporteContratosProveedores(dtoIn);
	        jrDataSource = new JRMapArrayDataSource(listReport.toArray());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasBusiness, M:reporteCuentasPersonales");
		}
		return jrDataSource;
	}
	
	public List<LlenaComboGralDto>consultarProveedores(String texto){
		return consultasDao.consultarProveedores(texto);
	}
	
	public List<LlenaComboGralDto>llenarComboBeneficiario(LlenaComboGralDto dto){
		return consultasDao.llenarComboBeneficiario(dto);	
	}
	
	public List<SaldoChequeraDto> obtenerProveedor(int noEmpresa) {
		return consultasDao.obtenerProveedor(noEmpresa);
	}
	
	public List<SaldoChequeraDto> obtenerContratos(int idUsuario) {
		return consultasDao.obtenerContratos(idUsuario);
	}
	
	public int eliminarContrato(int registro) {
		return consultasDao.eliminarContrato(registro);
	}
	
	public int eliminarCP(int banco, String chequera) {
		return consultasDao.eliminarCP(banco, chequera);
	}
	
	//Agregado el 10 de febrero del 2016
	
	public HSSFWorkbook reporteFondeo(String fInicio, String fFin, String usuario) {
		HSSFWorkbook hb=null;
		try {
			hb=Utilerias.generarExcel(new String[]{
					"Id grupo",
					"Descripcion",
					"Empresa",
					"Banco ",
					"Chequera ",
					"Cheque",				
					"Transferencia",
					"Cargo cuenta",
					"Importe total",
			}, 
					armarReporteFondeo(fInicio, fFin,usuario), 
					new String[]{
							"columna",
							"columna0",
							"columna8",
							"columna2",
							"columna3",
							"columna4",				
							"columna5",
							"columna6",
							"columna7",
					},"Reporte de Fondeo"
					);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Consultas, C: ConsultaMovimiento, M: reporteFondeo");
		}return hb;
		
	}
	
	public HSSFWorkbook reporteCXP(String noEmpresa, String fecIni, String fecFin, String estatus) {
		String noEmpresaR="";
		if(noEmpresa!=null && !noEmpresa.equals("0")){
			 noEmpresaR = consultasDao.obtenEmpresaSet006(noEmpresa);
		}else{
			 noEmpresaR = "0";
		}
		HSSFWorkbook hb=null;
		try {
			hb=generarExcel(new String[]{
					"No. Empresa",
					"No. Documento",
					"Secuencia",
					"No. Factura",
					"Fecha Factura ",
					"Fecha Valor",				
					"No. Beneficiario",
					"Id. Divisa",
					"Concepto",
					"Forma de Pago",
					"Clave Leyenda",
					"Beneficiario Alternativo",
					"Banco Alta",
					"Chequera Alta",
					"Id. Rubro",
					"Grupo de Tesorerï¿½a",
					"Codigo de Bloqueo",
					"Mandante",
					"Indicador de Mayor Especial",
					"Divisiï¿½n",
					"Indicador de IVA",
					"RFC",
					"Tipo de Cambio",
					"Id. Caja",
					"Fecha Importaciï¿½n",
					"Fecha Exportaciï¿½n",
					"Estatus",
					"Causa Rechazo",
					"Origen",
					"Estatus Compensa",
					"Estatus Proceso",
					"Email Alta",
					"Clabe",
					"Descipciï¿½n de Propuesta",
					"Banco Pagador",
					"Chequera Pagadora",
					"Fecha Propuesta",
					"Indice de Retenciï¿½n",
					"Clase Docto",
					"Importe Original",
					"Importe Base",
					"Id. Area",
					"Descripciï¿½n Area",
					"Importe",
					"Importe Solicitud",
					"Centro de Costos",
					"Forma de Pago original",
					"Chequera Beneficiaria",
					"Banco Beneficiario",
					"No. Cheque",
					"Origen Mov",
					"Posiciï¿½n",
					"No. Orden",
					"Clasificaciï¿½n Factura"
			}, 
					consultasDao.armarReporteCXP(noEmpresaR, fecIni, fecFin, estatus), 
					new String[]{
							"NO_EMPRESA",
							"NO_DOC_SAP",
							"SECUENCIA",
							"NO_FACTURA",
							"FEC_FACT",
							"FEC_VALOR",				
							"NO_BENEF",
							"ID_DIVISA",
							"CONCEPTO",
							"FORMA_PAGO",
							"CVE_LEYEN",
							"BENEF_ALT",
							"ID_BCO_ALT",
							"ID_CHQ_ALT",
							"ID_RUBRO",
							"GPO_TESOR",
							"COD_BLOQ",
							"MANDANTE",
							"IND_MAY_ES",
							"DIVISION",
							"IND_IVA",
							"RFC",
							"TIPO_CAMB",
							"ID_CAJA",
							"FECHA_IMP",
							"FECHA_EXP",
							"ESTATUS",
							"CAUSA_RECH",
							"ORIGEN",
							"ESTATUS_COMPENSA",
							"ESTATUS_PROCESO",
							"EMAIL_ALT",
							"CLABE",
							"DESC_PROPUESTA",
							"BANCO_PAGADOR",
							"CHEQUERA_PAGADORA",
							"FEC_PROPUESTA",
							"IND_RETENCION",
							"CLASE_DOCTO",
							"IMPORTE_ORIGINAL",
							"IMPORTE_BASE",
							"ID_AREA",
							"DESC_AREA",
							"IMPORTE",
							"IMP_SOLIC",
							"centro_cto",
							"forma_pago_original",
							"chequera_benef",
							"banco_benef",
							"no_cheque",
							"origen_mov",
							"posicion",
							"no_orden",
							"clasif_factura",
					},"Reporte de Cuentas Por Pagar"
					);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Consultas, C: ConsultaMovimiento, M: reporteCXP");
		}return hb;
		
	}
	
	public HSSFWorkbook reporteDatosbancarios(String noEmpresa, String fecIni, String fecFin, String estatus) {
		String noEmpresaR="";
		if(noEmpresa!=null && !noEmpresa.equals("0")){
			 noEmpresaR = consultasDao.obtenEmpresaSet006(noEmpresa);
		}else{
			 noEmpresaR = "0";
		}
		HSSFWorkbook hb=null;
		try {
			hb=generarExcel(new String[]{
					"No. Empresa",
					"Equivale Persona",
					"Tipo de Persona",
					"Id. Divisa",
					"Id. Banco ",
					"Id. Chequera",				
					"Plaza",
					"Sucursal",
					"Swift Code",
					"Aba",
					"Especiales",
					"Id. Bank true",
					"Id. Bank Corresponding",
					"Id. Chequera true",
					"Id. Clabe",
					"Id. Banco Anterior",
					"Id. Chequera Anterior",
					"Tipo Envio Layout",
					"Fecha Modificaciï¿½n",
					"Estatus",
					"Causa Rechazo",
					"Fecha Importaciï¿½n",
					"Fecha Exportaciï¿½n",
					"Descripciï¿½n Chequera",
					"Usuario Modif",
					"Aba intermediario",
					"Swift Intermediario",
					"Swift Corresponsal",
					"Aba Corresponsal",
					"Referencia",
					"Id. Paï¿½s",
					"Regiï¿½n",
					"Banco Corresponsal",
					"Id. Paï¿½s Crresponsal",
					"Id. Regiï¿½n Corresponsal",
					"IBAN",
					"Forfurthercred",
					"CIE Referencia",
					"CIE Concepto",


			}, 
					consultasDao.armarReporteDatosBancarios(noEmpresaR, fecIni, fecFin, estatus), 
					new String[]{
							"no_empresa",
							"equiv_per",
							"id_tipo_persona",
							"id_divisa",
							"id_banco",
							"id_chequera",				
							"plaza",
							"sucursal",
							"swift_code",
							"aba",
							"especiales",
							"id_bank_true",
							"id_bank_corresponding",
							"id_chequera_true",
							"id_clabe",
							"id_banco_anterior",
							"id_chequera_anterior",
							"tipo_envio_layout",
							"fec_modif",
							"ESTATUS",
							"causa_rech",
							"fec_imp",
							"fec_exp",
							"desc_chequera",
							"usuario_modif",
							"aba_intermediario",
							"swift_intermediario",
							"swift_corresponsal",
							"aba_corresponsal",
							"referencia",
							"id_pais",
							"id_region",
							"banco_corresponsal",
							"id_pais_corresponsal",
							"id_region_corresponsal",
							"IBAN",
							"forfurthercred",
							"CIE_Referencia",
							"CIE_Concepto",


			},"Reporte de Datos Bancarios"
					);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Consultas, C: ConsultaMovimiento, M: reporteDatosbancarios");
		}return hb;
	}

	public HSSFWorkbook reporteCXC(String noEmpresa, String fecIni, String fecFin, String estatus) {
		String noEmpresaR="";
		if(noEmpresa!=null && !noEmpresa.equals("0")){
			 noEmpresaR = consultasDao.obtenEmpresaSet006(noEmpresa);
		}else{
			 noEmpresaR = "0";
		}
		HSSFWorkbook hb=null;
		try {
			hb=generarExcel(new String[]{
					"No. Empresa",
					"No. Documento",
					"Secuencia",
					"No. Factura",
					"Fecha Factura ",
					"Fecha Valor",				
					"No. Beneficiario",
					"Importe",
					"Importe Solicitud",
					"Id. Divisa",
					"Concepto",
					"Forma de Pago",
					"Id. Rubro",
					"Grupo de Tesorerï¿½a",
					"Id. Caja",
					"Fecha Importaciï¿½n",
					"Fecha Exportaciï¿½n",
					"Estatus",
					"Causa Rechazo",
					"Origen",
					"Referencia",
					"Estatus Compensa"
			}, 
					consultasDao.armarReporteCXC(noEmpresaR, fecIni, fecFin, estatus), 
					new String[]{
							"NO_EMPRESA",
							"NO_DOC_SAP",
							"SECUENCIA",
							"NO_FACTURA",
							"FEC_FACT",
							"FEC_VALOR",				
							"NO_BENEF",
							"IMPORTE",
							"IMP_SOLIC",
							"ID_DIVISA",
							"CONCEPTO",
							"FORMA_PAGO",
							"ID_RUBRO",
							"GPO_TESOR",
							"ID_CAJA",
							"FECHA_IMP",
							"FECHA_EXP",
							"ESTATUS",
							"CAUSA_RECH",
							"ORIGEN",
							"REFERENCIA",
							"ESTATUS_COMPENSA",
			},"Reporte de Cuentas Por Cobrar"
					);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Consultas, C: ConsultaMovimiento, M: reporteCXC");
		}return hb;
	}

	public HSSFWorkbook obtenerDatosExcelReporteSaldosS(String idEmpresa, String nomEmpresa , String idBancoInf, String idBancoSup, String idChequera, String tipoChequera, String estatusTipoEmpresa, String usuario, String empresas) {
		HSSFWorkbook hb=null;
		try {  
			hb=Utilerias.generarExcel(new String[]{
					"No. Empresa",
					"Nombre Empresa",
					"Id. Banco",
					"Desc. Banco",
					"Id. Chequera",
					"Saldo inicial",
					"Cargo",
					//"Estatus",
					//"Fecha hoy",
					"Abono",
					"Saldo final",
					"Saldo Inicial Banco",
					"Saldo Final Banco",
					"Abono SBC",
					"Ult. Cheque Impreso",
					"Id. Divisa",
					"Desc. Tipo Chequera",
					"Disponible en Chequera",
					"Fecha Banca"
			},
					consultasDao.obtenerDatosExcelSaldosS(idEmpresa, nomEmpresa , idBancoInf, idBancoSup, idChequera, tipoChequera, estatusTipoEmpresa, usuario, empresas),
					new String[]{
							"no_empresa",
							"nom_empresa",
							"id_banco",
							"desc_banco",
							"id_chequera",
							"saldo_inicial",
							"cargo",				
							//"estatus",
							//"FECHAHOY",
							"abono",
							"saldo_final",
							"saldo_inicial_banco",
							"saldo_final_banco",
							"abono_sbc",
							"ult_cheq_impreso",
							"id_divisa",
							"desc_tipo_chequera",
							"disp_en_chequera",
							"fecha_banca"
			},"Reporte de Saldos de Chequeras"
					);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Consultas, C: ConsultaMovimiento, M: obtenerDatosExcelReporteSaldosS");
		}return hb;
	}

	public List<Map<String,String>> armarReporteFondeo(String fInicio,String fFin, String usuario){
		List<Map<String, String>> resultado = new ArrayList<Map<String,String>>();
		List<Map<String, String>> auxiliar = new ArrayList<Map<String,String>>();
		List<Map<String, String>> vacio = new ArrayList<Map<String,String>>();
		String idGrupo="";
		String descGrupo="";
		String banco="";
		String empresa="";
		String chequera="";
		String tipo="";
		String contador="";
		double importe=0;
		double importeTotal=0;
		double importeTotalDivisa=0;
		String divisa="";
		int c=0;
		int t=0;
		int cc=0;
		double importeCheque=0;
		double importeTransfer=0;
		double importeCargoCuenta=0;
		double sumaCheque=0;
		double sumaTransfer=0;
		double sumaCargoCuenta=0;
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		symbols.setGroupingSeparator(',');
		String pattern = "###,##0.00";
		DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
		try{
			
			Map<String, String> map = new HashMap<String, String>();
			map.put("columna", ""); //divissa
			map.put("columna0",""); //descBanco
			map.put("columna1", ""); //divissa
			map.put("columna2",""); //descBanco
			map.put("columna3", ""); //idBanco
			map.put("columna4",""); //idChequera
			map.put("columna5", ""); //Cheque
			map.put("columna6",""); //Transferencia
			map.put("columna7", ""); // cargo cuenta
			map.put("columna8", ""); //tipo
			map.put("columna9", ""); //contador de tipo
			
			//resultado =consultasDao.reporteFondeo(fInicio, fFin);
			//resultado.add(map);
			vacio.add(map);
			//if(resultado!=null && resultado.size()!=1)
				auxiliar=consultasDao.reporteFondeoSubReporte(fInicio,fFin,usuario);
			//else
			//return vacio;

			if(auxiliar==null || auxiliar.size()==0)
				return vacio;
			
			resultado.add(map);
			
			for (int i = 0; i < auxiliar.size(); i++) {
				Map<String, String> mapNuevoR = new HashMap<String, String>();
				Map<String, String> mapDivisa = new HashMap<String, String>();
			//Map<String, String> mapSubEncabezados = new HashMap<String, String>();
				Map<String, String> mapTotalDivisa = new HashMap<String, String>();
				boolean bdivisa=false;
				//String format="";
				if(i==0){
					
					divisa=auxiliar.get(i).get("columna1");
					if (divisa.equals("MN")) {
						mapDivisa.put("columna", "PESOS"); //Vacio
					}
					
					if (divisa.equals("DLS")) {
						mapDivisa.put("columna", "DOLARES"); //Vacio
					}
					
					if (divisa.equals("EUR")) {
						mapDivisa.put("columna", "EUROS"); //Vacio
					}
					mapDivisa.put("columna0", " "); //Vacio
					mapDivisa.put("columna2", " "); //Vacio
					mapDivisa.put("columna3", " "); //Vacio
					mapDivisa.put("columna4", " "); //Vacio
					mapDivisa.put("columna5", " "); //Vacio
					mapDivisa.put("columna6", " "); //Vacio
					mapDivisa.put("columna7", " "); //Vacio
					mapDivisa.put("columna8", " "); //Vacio
					mapDivisa.put("columna9", " "); //Vacio
					
					resultado.add(mapDivisa);
					
					/*mapSubEncabezados.put("columna1", " "); //Vacio
					mapSubEncabezados.put("columna2", "Banco"); //Vacio
					mapSubEncabezados.put("columna3", "Chequera"); //Vacio
					mapSubEncabezados.put("columna4", "Cheque"); //Vacio
					mapSubEncabezados.put("columna5", "Transferencia"); //Vacio
					mapSubEncabezados.put("columna6", "Cargo cuenta"); //Vacio
					mapSubEncabezados.put("columna7", "Importe"); //Vacio
					mapSubEncabezados.put("columna8", " "); //Vacio
					mapSubEncabezados.put("columna9", " "); //Vacio
					resultado.add(mapSubEncabezados);
					*/
					
					if(!divisa.equals("")){
						banco=auxiliar.get(i).get("columna2");
						empresa=auxiliar.get(i).get("columna10");
						idGrupo=auxiliar.get(i).get("columna");
						descGrupo=auxiliar.get(i).get("columna0");
						chequera=auxiliar.get(i).get("columna3");
						tipo=auxiliar.get(i).get("columna8");
						contador=auxiliar.get(i).get("columna9");
						importe=Double.parseDouble(auxiliar.get(i).get("columna7"));
						//importeTotal=Double.parseDouble(auxiliar.get(i).get("columna7"));
						System.out.println("llego aqui 1");
						//importeTotalDivisa=importeTotal;
					}else{
						return resultado;
					}
				}else{
					String auxDivisa=auxiliar.get(i).get("columna1");
					boolean iguales=true;
					if(divisa.equals(auxDivisa)){
						//importeTotalDivisa=importeTotalDivisa+importeTotal;
						
						String auxBanco=auxiliar.get(i).get("columna2");
						String auxChequera=auxiliar.get(i).get("columna3");
						if(banco.equals(auxBanco)){
							if(chequera.equals(auxChequera)){
								iguales=true;
							}else{
								iguales=false;
							}
						}else{
							iguales=false;
							//cambio el banco
						}
					}else{
						iguales=false;
						divisa=auxDivisa;
						bdivisa=true;
						
					}
					
					if(iguales){
						String AuxTipo=auxiliar.get(i).get("columna8");
						String AuxContador=auxiliar.get(i).get("columna9");
						double AuxImporte=Double.parseDouble(auxiliar.get(i).get("columna7"));
						//importeTotal=importeTotal+AuxImporte;
						if(AuxTipo.equals("cheque")&&c==0){
							c=Integer.parseInt(AuxContador);
							importeCheque=AuxImporte;
						}
						
						if(AuxTipo.equals("transferencia")&&t==0){
							t=Integer.parseInt(AuxContador);
							importeTransfer=AuxImporte;
						}
						
						if(AuxTipo.equals("cargo cuenta")&&cc==0){
							cc=Integer.parseInt(AuxContador);
							importeCargoCuenta=AuxImporte;
						}
						
					}else{
						mapNuevoR.put("columna", idGrupo); //Vacio
						mapNuevoR.put("columna0", descGrupo); //Vacio
						mapNuevoR.put("columna1", ""); //Vacio
						mapNuevoR.put("columna2", banco); //Vacio
						mapNuevoR.put("columna3", chequera); //Vacio
						if(tipo.equals("cheque")&&c==0){
							importeCheque=importe;
							mapNuevoR.put("columna4", "$"+decimalFormat.format(importeCheque)+""); //Vacio
							c=Integer.parseInt(contador);
						}else if(c!=0){
							mapNuevoR.put("columna4", "$"+decimalFormat.format(importeCheque)+"");
						}else{
							mapNuevoR.put("columna4","$0.00");
						}
						
						
						if(tipo.equals("transferencia")&&t==0){
							importeTransfer=importe;
							mapNuevoR.put("columna5", "$"+decimalFormat.format(importeTransfer)+""); //Vacio
							t=Integer.parseInt(contador);
							
						}else if(t!=0){
							mapNuevoR.put("columna5", "$"+decimalFormat.format(importeTransfer)+"");
						}else{
							mapNuevoR.put("columna5","$0.00");
						}
						
						if(tipo.equals("cargo cuenta")&&cc==0){
							importeCargoCuenta=importe;
							mapNuevoR.put("columna6", "$"+decimalFormat.format(importeCargoCuenta)+""); //Vacio
							cc=Integer.parseInt(contador);
							
						}else if(cc!=0){
							mapNuevoR.put("columna6", "$"+decimalFormat.format(importeCargoCuenta)+"");
						}else{
							mapNuevoR.put("columna6","$0.00");
						}
						
						importeTotal=importeCheque+importeTransfer+importeCargoCuenta;
						sumaCheque=sumaCheque+importeCheque;
						sumaTransfer=sumaTransfer+importeTransfer;
						sumaCargoCuenta=sumaCargoCuenta+importeCargoCuenta;
						importeTotalDivisa=importeTotalDivisa+importeTotal;
						mapNuevoR.put("columna7", "$"+decimalFormat.format(importeTotal)+""); //Vacio
						mapNuevoR.put("columna8", empresa); //Vacio
						mapNuevoR.put("columna9", ""); //Vacio
						resultado.add(mapNuevoR);
						
						
						if(bdivisa){
							//importeTotalDivisa=importeTotalDivisa+importeTotal;
							mapTotalDivisa.put("columna", ""); //Vacio
							mapTotalDivisa.put("columna0", ""); //Vacio
							mapTotalDivisa.put("columna1", ""); //Vacio
							mapTotalDivisa.put("columna2", ""); //Vacio
							mapTotalDivisa.put("columna2", ""); //Vacio
							mapTotalDivisa.put("columna3", "Total: "); //Vacio
							mapTotalDivisa.put("columna4", "$"+decimalFormat.format(sumaCheque)+""); //Vacio
							mapTotalDivisa.put("columna5", "$"+decimalFormat.format(sumaTransfer)+""); //Vacio
							mapTotalDivisa.put("columna6", "$"+decimalFormat.format(sumaCargoCuenta)+""); //Vacio
							mapTotalDivisa.put("columna7", "$"+decimalFormat.format(importeTotalDivisa)+""); //Vacio
							mapTotalDivisa.put("columna8", ""); //Vacio
							mapTotalDivisa.put("columna9", ""); //Vacio

							resultado.add(mapTotalDivisa);
							resultado.add(map);
							importeTotalDivisa=0;
							sumaCargoCuenta=0;
							sumaTransfer=0;
							sumaCheque=0;
							mapDivisa.put("columna1", ""); //Vacio
							if (auxDivisa.equals("MN")) {
								mapDivisa.put("columna", "PESOS"); //Vacio
							}

							if (auxDivisa.equals("DLS")) {
								mapDivisa.put("columna", "DOLARES"); //Vacio
							}

							if (auxDivisa.equals("EUR")) {
								mapDivisa.put("columna", "EUROS"); //Vacio
							}
							mapDivisa.put("columna0", " "); //Vacio
							mapDivisa.put("columna2", " "); //Vacio
							mapDivisa.put("columna3", " "); //Vacio
							mapDivisa.put("columna4", " "); //Vacio
							mapDivisa.put("columna5", " "); //Vacio
							mapDivisa.put("columna6", " "); //Vacio
							mapDivisa.put("columna7", " "); //Vacio
							mapDivisa.put("columna8", " "); //Vacio
							mapDivisa.put("columna9", " "); //Vacio

							resultado.add(mapDivisa);



							/*mapSubEncabezados.put("columna1", ""); //Vacio
							mapSubEncabezados.put("columna2", "Banco"); //Vacio
							mapSubEncabezados.put("columna3", "Chequera"); //Vacio
							mapSubEncabezados.put("columna4", "Cheque"); //Vacio
							mapSubEncabezados.put("columna5", "Transferencia"); //Vacio
							mapSubEncabezados.put("columna6", "Cargo cuenta"); //Vacio
							mapSubEncabezados.put("columna7", "Importe"); //Vacio
							mapSubEncabezados.put("columna8", ""); //Vacio
							mapSubEncabezados.put("columna9", ""); //Vacio
							resultado.add(mapSubEncabezados);*/
						}
						c=0;
						t=0;
						cc=0;
						importeCheque=0;
						importeTransfer=0;
						importeCargoCuenta=0;
						tipo=auxiliar.get(i).get("columna8");
						contador=auxiliar.get(i).get("columna9");
						importe=Double.parseDouble(auxiliar.get(i).get("columna7"));
						//importeTotal=importe;
						importeTotal=0;
						banco=auxiliar.get(i).get("columna2");
						chequera=auxiliar.get(i).get("columna3");
						idGrupo=auxiliar.get(i).get("columna");
						descGrupo=auxiliar.get(i).get("columna0");
						empresa=auxiliar.get(i).get("columna10");
					}
				}

				/*
				 * Auxiliar
				 * 1 divisa
				 * 2 descBanco
				 * 3 idChequera
				 * 4 cheque
				 * 5 transfer
				 * 6 cargoCuenta
				 * 7 importe
				 * 8 tipo
				 * 9 contador
				 */
			}
			Map<String, String> mapNuevoR1 = new HashMap<String, String>();
			mapNuevoR1.put("columna", idGrupo); //Vacio
			mapNuevoR1.put("columna0", descGrupo); //Vacio
			mapNuevoR1.put("columna1", ""); //Vacio
			mapNuevoR1.put("columna2", banco); //Vacio
			mapNuevoR1.put("columna3", chequera); //Vacio

			if(tipo.equals("cheque")&&c==0){
				importeCheque=importe;
				mapNuevoR1.put("columna4", "$"+decimalFormat.format(importeCheque)+""); //Vacio
			}else if(c!=0){
				mapNuevoR1.put("columna4", "$"+decimalFormat.format(importeCheque)+"");
			}else{
				mapNuevoR1.put("columna4","$0.00");
			}


			if(tipo.equals("transferencia")&&t==0){
				importeTransfer=importe;
				mapNuevoR1.put("columna5", "$"+decimalFormat.format(importeTransfer)+""); //Vacio
			}else if(t!=0){
				mapNuevoR1.put("columna5", "$"+decimalFormat.format(importeTransfer)+"");
			}else{
				mapNuevoR1.put("columna5","$0.00");
			}

			if(tipo.equals("cargo cuenta")&&cc==0){
				importeCargoCuenta=importe;
				mapNuevoR1.put("columna6", "$"+decimalFormat.format(importeCargoCuenta)+""); //Vacio
			}else if(cc!=0){
				mapNuevoR1.put("columna6", "$"+decimalFormat.format(importeCargoCuenta)+"");
			}else{
				mapNuevoR1.put("columna6","$0.00");
			}
			importeTotal=importeCheque+importeTransfer+importeCargoCuenta;
			mapNuevoR1.put("columna7", "$"+decimalFormat.format(importeTotal)+""); //Vacio
			mapNuevoR1.put("columna8", empresa); //Vacio
			mapNuevoR1.put("columna9", ""); //Vacio
			resultado.add(mapNuevoR1);

			importeTotalDivisa=importeTotalDivisa+importeTotal;
			//String sTotal= funciones.ponerFormatoCeros(importeTotalDivisa, 15);
			sumaCheque=sumaCheque+importeCheque;
			sumaTransfer=sumaTransfer+importeTransfer;
			sumaCargoCuenta=sumaCargoCuenta+importeCargoCuenta;


			Map<String, String> mapNuevoR2 = new HashMap<String, String>();





			//String format="";
			//System.out.println(format);
			mapNuevoR2.put("columna", ""); //Vacio
			mapNuevoR2.put("columna0", ""); //Vacio
			mapNuevoR2.put("columna1", ""); //Vacio
			mapNuevoR2.put("columna2", ""); //Vacio
			mapNuevoR2.put("columna2", ""); //Vacio
			mapNuevoR2.put("columna3", "Total: "); //Vacio
			//format = decimalFormat.format(sumaCheque);
			mapNuevoR2.put("columna4", "$"+decimalFormat.format(sumaCheque)); //Vacio
			//format = decimalFormat.format(sumaTransfer);
			mapNuevoR2.put("columna5", "$"+decimalFormat.format(sumaTransfer)); //Vacio
			mapNuevoR2.put("columna6", "$"+decimalFormat.format(sumaCargoCuenta)+""); //Vacio
			//format = decimalFormat.format(sumaTransfer);
			mapNuevoR2.put("columna7", "$"+decimalFormat.format(importeTotalDivisa)+""); //Vacio
			//format = decimalFormat.format(sumaTransfer);
			mapNuevoR2.put("columna7", "$"+decimalFormat.format(importeTotalDivisa)+""); //Vacio
			mapNuevoR2.put("columna8", ""); //Vacio
			mapNuevoR2.put("columna9", ""); //Vacio
			resultado.add(mapNuevoR2);

		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Consultas, C: ConsultaMovimiento, M:armarReporteFondeo");
		}return resultado;
	}



	//Reporte completo de fondeo
	/*
	 * public HSSFWorkbook reporteFondeo(String fInicio, String fFin) {
		HSSFWorkbook hb=null;
		try {
			hb=Utilerias.generarExcel(new String[]{
					"No. Empresa",
					"Empresa",
					"Id banco",
					"Chequera",				
					"Fecha",
					"Grupo",
					"Descripcion",
					"Estatus",
					"importe"
			}, 
					armarReporteFondeo(fInicio, fFin), 
					new String[]{
							"columna1",
							"columna2",
							"columna3",
							"columna4",				
							"columna5",
							"columna6",
							"columna7",
							"columna8",
							"columna9"
					});
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Consultas, C: ConsultaMovimiento, M: reporteFondeo");
		}return hb;
		
	}
	
	public List<Map<String,String>> armarReporteFondeo(String fInicio,String fFin){
		List<Map<String, String>> resultado = new ArrayList<Map<String,String>>();
		List<Map<String, String>> auxiliar = new ArrayList<Map<String,String>>();
		List<Map<String, String>> vacio = new ArrayList<Map<String,String>>();
		String banco="";
		String chequera="";
		String tipo="";
		String contador="";
		double importe=0;
		double importeTotal=0;
		double importeTotalDivisa=0;
		String divisa="";
		int c=0;
		int t=0;
		int cc=0;
		double importeCheque=0;
		double importeTransfer=0;
		double importeCargoCuenta=0;
		try{
			
			Map<String, String> map = new HashMap<String, String>();
			map.put("columna1", ""); //divissa
			map.put("columna2",""); //descBanco
			map.put("columna3", ""); //idBanco
			map.put("columna4",""); //idChequera
			map.put("columna5", ""); //Cheque
			map.put("columna6",""); //Transferencia
			map.put("columna7", ""); // cargo cuenta
			map.put("columna8", ""); //tipo
			map.put("columna9", ""); //contador de tipo
			
			resultado =consultasDao.reporteFondeo(fInicio, fFin);
			resultado.add(map);
			vacio.add(map);
			if(resultado!=null && resultado.size()!=1)
				auxiliar=consultasDao.reporteFondeoSubReporte(fInicio,fFin);
			else
				return vacio;
						
			if(auxiliar==null || auxiliar.size()==0)
				return vacio;
			
			resultado.add(map);
			
			for (int i = 0; i < auxiliar.size(); i++) {
				Map<String, String> mapNuevoR = new HashMap<String, String>();
				Map<String, String> mapDivisa = new HashMap<String, String>();
				Map<String, String> mapSubEncabezados = new HashMap<String, String>();
				Map<String, String> mapTotalDivisa = new HashMap<String, String>();
				boolean bdivisa=false;
				if(i==0){
					
					divisa=auxiliar.get(i).get("columna1");
					
					if (divisa.equals("MN")) {
						mapDivisa.put("columna2", "PESOS"); //Vacio
					}
					
					if (divisa.equals("DLS")) {
						mapDivisa.put("columna2", "DOLARES"); //Vacio
					}
					
					if (divisa.equals("EUR")) {
						mapDivisa.put("columna2", "EUROS"); //Vacio
					}
					
					mapDivisa.put("columna3", " "); //Vacio
					mapDivisa.put("columna4", " "); //Vacio
					mapDivisa.put("columna5", " "); //Vacio
					mapDivisa.put("columna6", " "); //Vacio
					mapDivisa.put("columna7", " "); //Vacio
					mapDivisa.put("columna8", " "); //Vacio
					mapDivisa.put("columna9", " "); //Vacio
					
					resultado.add(mapDivisa);
					
					mapSubEncabezados.put("columna1", " "); //Vacio
					mapSubEncabezados.put("columna2", "Banco"); //Vacio
					mapSubEncabezados.put("columna3", "Chequera"); //Vacio
					mapSubEncabezados.put("columna4", "Cheque"); //Vacio
					mapSubEncabezados.put("columna5", "Transferencia"); //Vacio
					mapSubEncabezados.put("columna6", "Cargo cuenta"); //Vacio
					mapSubEncabezados.put("columna7", "Importe"); //Vacio
					mapSubEncabezados.put("columna8", " "); //Vacio
					mapSubEncabezados.put("columna9", " "); //Vacio
					resultado.add(mapSubEncabezados);
					mapNuevoR.clear();
					
					if(!divisa.equals("")){
						banco=auxiliar.get(i).get("columna2");
						chequera=auxiliar.get(i).get("columna3");
						tipo=auxiliar.get(i).get("columna8");
						contador=auxiliar.get(i).get("columna9");
						importe=Double.parseDouble(auxiliar.get(i).get("columna7"));
						//importeTotal=Double.parseDouble(auxiliar.get(i).get("columna7"));
						System.out.println("llego aqui 1");
						//importeTotalDivisa=importeTotal;
					}else{
						return resultado;
					}
				}else{
					String auxDivisa=auxiliar.get(i).get("columna1");
					boolean iguales=true;
					if(divisa.equals(auxDivisa)){
						//importeTotalDivisa=importeTotalDivisa+importeTotal;
						
						String auxBanco=auxiliar.get(i).get("columna2");;
						String auxChequera=auxiliar.get(i).get("columna3");
						if(banco.equals(auxBanco)){
							if(chequera.equals(auxChequera)){
								iguales=true;
							}else{
								iguales=false;
							}
						}else{
							iguales=false;
							//cambio el banco
						}
					}else{
						iguales=false;
						divisa=auxDivisa;
						bdivisa=true;
						
					}
					
					if(iguales){
						String AuxTipo=auxiliar.get(i).get("columna8");
						String AuxContador=auxiliar.get(i).get("columna9");
						double AuxImporte=Double.parseDouble(auxiliar.get(i).get("columna7"));
						//importeTotal=importeTotal+AuxImporte;
						if(AuxTipo.equals("cheque")&&c==0){
							c=Integer.parseInt(AuxContador);
							importeCheque=AuxImporte;
						}
						
						if(AuxTipo.equals("transferencia")&&t==0){
							t=Integer.parseInt(AuxContador);
							importeTransfer=AuxImporte;
						}
						
						if(AuxTipo.equals("cargo cuenta")&&cc==0){
							cc=Integer.parseInt(AuxContador);
							importeCargoCuenta=AuxImporte;
						}
						
					}else{
						
						mapNuevoR.put("columna1", ""); //Vacio
						mapNuevoR.put("columna2", banco); //Vacio
						mapNuevoR.put("columna3", chequera); //Vacio
						if(tipo.equals("cheque")&&c==0){
							importeCheque=importe;
							mapNuevoR.put("columna4", importeCheque+""); //Vacio
							c=Integer.parseInt(contador);
						}else if(c!=0){
							mapNuevoR.put("columna4", importeCheque+"");
						}else{
							mapNuevoR.put("columna4","0");
						}
						
						
						if(tipo.equals("transferencia")&&t==0){
							importeTransfer=importe;
							mapNuevoR.put("columna5", importeTransfer+""); //Vacio
							t=Integer.parseInt(contador);
							
						}else if(t!=0){
							mapNuevoR.put("columna5", importeTransfer+"");
						}else{
							mapNuevoR.put("columna5","0");
						}
						
						if(tipo.equals("cargo cuenta")&&cc==0){
							importeCargoCuenta=importe;
							mapNuevoR.put("columna6", importeCargoCuenta+""); //Vacio
							cc=Integer.parseInt(contador);
							
						}else if(cc!=0){
							mapNuevoR.put("columna6", importeCargoCuenta+"");
						}else{
							mapNuevoR.put("columna6","0");
						}
						
						importeTotal=importeCheque+importeTransfer+importeCargoCuenta;
						importeTotalDivisa=importeTotalDivisa+importeTotal;
						mapNuevoR.put("columna7", importeTotal+""); //Vacio
						mapNuevoR.put("columna8", ""); //Vacio
						mapNuevoR.put("columna9", ""); //Vacio
						resultado.add(mapNuevoR);
						
						
						if(bdivisa){
							//importeTotalDivisa=importeTotalDivisa+importeTotal;
							mapTotalDivisa.put("columna1", ""); //Vacio
							mapTotalDivisa.put("columna2", ""); //Vacio
							mapTotalDivisa.put("columna2", ""); //Vacio
							mapTotalDivisa.put("columna3", ""); //Vacio
							mapTotalDivisa.put("columna4", ""); //Vacio
							mapTotalDivisa.put("columna5", ""); //Vacio
							mapTotalDivisa.put("columna6", "Total: "); //Vacio
							mapTotalDivisa.put("columna7", importeTotalDivisa+""); //Vacio
							mapTotalDivisa.put("columna8", ""); //Vacio
							mapTotalDivisa.put("columna9", ""); //Vacio
							
							resultado.add(mapTotalDivisa);
							resultado.add(map);
							importeTotalDivisa=0;
							mapDivisa.put("columna1", ""); //Vacio
							if (auxDivisa.equals("MN")) {
								mapDivisa.put("columna2", "PESOS"); //Vacio
							}
							
							if (auxDivisa.equals("DLS")) {
								mapDivisa.put("columna2", "DOLARES"); //Vacio
							}
							
							if (auxDivisa.equals("EUR")) {
								mapDivisa.put("columna2", "EUROS"); //Vacio
							}
							mapDivisa.put("columna3", " "); //Vacio
							mapDivisa.put("columna4", " "); //Vacio
							mapDivisa.put("columna5", " "); //Vacio
							mapDivisa.put("columna6", " "); //Vacio
							mapDivisa.put("columna7", " "); //Vacio
							mapDivisa.put("columna8", " "); //Vacio
							mapDivisa.put("columna9", " "); //Vacio
							
							resultado.add(mapDivisa);
						
							
							
							mapSubEncabezados.put("columna1", ""); //Vacio
							mapSubEncabezados.put("columna2", "Banco"); //Vacio
							mapSubEncabezados.put("columna3", "Chequera"); //Vacio
							mapSubEncabezados.put("columna4", "Cheque"); //Vacio
							mapSubEncabezados.put("columna5", "Transferencia"); //Vacio
							mapSubEncabezados.put("columna6", "Cargo cuenta"); //Vacio
							mapSubEncabezados.put("columna7", "Importe"); //Vacio
							mapSubEncabezados.put("columna8", ""); //Vacio
							mapSubEncabezados.put("columna9", ""); //Vacio
							resultado.add(mapSubEncabezados);
						}
						c=0;
						t=0;
						cc=0;
						importeCheque=0;
						importeTransfer=0;
						importeCargoCuenta=0;
						tipo=auxiliar.get(i).get("columna8");
						contador=auxiliar.get(i).get("columna9");
						importe=Double.parseDouble(auxiliar.get(i).get("columna7"));
						//importeTotal=importe;
						importeTotal=0;
						banco=auxiliar.get(i).get("columna2");
						chequera=auxiliar.get(i).get("columna3");
					}
				}
				
				
				 // Auxiliar
				 //1 divisa
				 //2 descBanco
				 // 3 idChequera
				 // 4 cheque
				 // 5 transfer
				 // 6 cargoCuenta
				 // 7 importe
				 // 8 tipo
				 // 9 contador
			}
			Map<String, String> mapNuevoR1 = new HashMap<String, String>();
			mapNuevoR1.put("columna1", ""); //Vacio
			mapNuevoR1.put("columna2", banco); //Vacio
			mapNuevoR1.put("columna3", chequera); //Vacio
			
			if(tipo.equals("cheque")&&c==0){
				importeCheque=importe;
				mapNuevoR1.put("columna4", importeCheque+""); //Vacio
			}else if(c!=0){
				mapNuevoR1.put("columna4", importeCheque+"");
			}else{
				mapNuevoR1.put("columna4","0");
			}
			
			
			if(tipo.equals("transferencia")&&t==0){
				importeTransfer=importe;
				mapNuevoR1.put("columna5", importeTransfer+""); //Vacio
			}else if(t!=0){
				mapNuevoR1.put("columna5", importeTransfer+"");
			}else{
				mapNuevoR1.put("columna5","0");
			}
			
			if(tipo.equals("cargo cuenta")&&cc==0){
				importeCargoCuenta=importe;
				mapNuevoR1.put("columna6", importeCargoCuenta+""); //Vacio
			}else if(cc!=0){
				mapNuevoR1.put("columna6", importeCargoCuenta+"");
			}else{
				mapNuevoR1.put("columna6","0");
			}
			importeTotal=importeCheque+importeTransfer+importeCargoCuenta;
			mapNuevoR1.put("columna7", importeTotal+""); //Vacio
			mapNuevoR1.put("columna8", ""); //Vacio
			mapNuevoR1.put("columna9", ""); //Vacio
			resultado.add(mapNuevoR1);
			
			importeTotalDivisa=importeTotalDivisa+importeTotal;
			//String sTotal= funciones.ponerFormatoCeros(importeTotalDivisa, 15);
		
			Map<String, String> mapNuevoR2 = new HashMap<String, String>();
			mapNuevoR2.put("columna1", ""); //Vacio
			mapNuevoR2.put("columna2", ""); //Vacio
			mapNuevoR2.put("columna2", ""); //Vacio
			mapNuevoR2.put("columna3", ""); //Vacio
			mapNuevoR2.put("columna4", ""); //Vacio
			mapNuevoR2.put("columna5", ""); //Vacio
			mapNuevoR2.put("columna6", "Total: "); //Vacio
			mapNuevoR2.put("columna7", importeTotalDivisa+""); //Vacio
			mapNuevoR2.put("columna8", ""); //Vacio
			mapNuevoR2.put("columna9", ""); //Vacio
			resultado.add(mapNuevoR2);
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Consultas, C: ConsultaMovimiento, M:armarReporteFondeo");
		}return resultado;
	}*/
	
	public HSSFWorkbook reporteFondeoDetalle(String fInicio, String fFin, String usuario) {
		HSSFWorkbook hb=null;
		try {
			hb=Utilerias.generarExcel(new String[]{
					"Id propuesta",
					"Fecha",
					"Grupo empresa ",
					"Empresa ",
					"Area centro costos",				
					"Banco",
					"Chequera",
					"Id rubro",
					"Rubro",
					"Forma pago",
					"Importe",
					"Estatus ",
					"No cliente",				
					"Razon social",
					"Cuenta mayor",
					"Id divisa",
			}, 
					consultasDao.reporteFondeo(fInicio, fFin,usuario), 
					new String[]{
							"columna1",
							"columna2",
							"columna3",
							"columna4",				
							"columna5",
							"columna6",
							"columna7",
							"columna8",
							"columna9",
							"columna10",
							"columna11",
							"columna12",
							"columna13",
							"columna14",				
							"columna15",
							"columna16",
					},"Reporte de Fondeo detallado"
					);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Consultas, C: ConsultaMovimiento, M: reporteFondeo");
		}return hb;
		
	}
	
	public JRDataSource obtenerDatosReporteSaldosS(Map<String, Object> parametros) {
		JRMapArrayDataSource  jrDataSource = null; 
	    List<Map<String, Object>> resMap = null;
	    //String sCadEmpresas = "";
	    //int iUsuario = funciones.convertirCadenaInteger(parametros.get("usuario").toString());
		try{

			//List<ComunDto> listEmpresas = new ArrayList<ComunDto>();
			//validando empresas
			/*if(parametros.get("empresas").equals("")){
				listEmpresas = consultasDao.llenarComboEmpresa(iUsuario);
				if(listEmpresas.size() > 0){
					for(int i = 0; i < listEmpresas.size(); i++)
					{
						sCadEmpresas = sCadEmpresas + listEmpresas.get(i).getIdEmpresa() + ",";
						
					}
				}
				parametros.put("empresas", sCadEmpresas.substring(0, sCadEmpresas.length() - 1));
				System.out.println(sCadEmpresas);
			}*/
			
			
			resMap = consultasDao.obtenerDatosReporteSaldosS(parametros);
		
			if(resMap.size()==0)
				return null;

			jrDataSource = new JRMapArrayDataSource(resMap.toArray());

		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Consultas, C:ConsultasBusiness, M:obtenerDatosReporteSaldosS");
		}
		return jrDataSource;
	}

	public List<Map<String, Object>> reporteChequeraDetalleMov(ReportesChequeraDto datos){
		List<Map<String, Object>> lista = this.consultasDao.reporteChequeraDetalleMov(datos);
		
		Double saldo = 0D;
		Double saldoI = 0D;
		boolean primeraIteracion = true;
		
		Double saldoAnterior = 0D;
		Double netoMovimiento = 0D;
		
		saldo = this.consultasDao.consultarSaldoFinal(datos);
		saldoI = saldo;
		for(int i = 0; i <= lista.size()-1; i++){
			Map<String, Object> mapa = lista.get(i);
			
			/*if(primeraIteracion){
//				saldo = ((BigDecimal)mapa.get("SALDO_FINAL")).doubleValue();
				primeraIteracion = false;
			}else{*/
			Map<String, Object> mapa1 = lista.get(i);
			saldo += ((BigDecimal) mapa1.get("CARGO")).doubleValue()
					- ((BigDecimal) mapa1.get("ABONO")).doubleValue(); 
			//}
			
			mapa.put("SALDO", saldo);
			mapa.put("saldoI", saldoI);
			lista.set(i, mapa);
		}
		
		return lista;
	}
	
	
	public List<Map<String, Object>> reporteChequeraMovimientos(ReportesChequeraDto datos){
		List<Map<String, Object>> lista = this.consultasDao.reporteChequeraMovimientos(datos);
		
		return lista;
	}
	
	public String validar3200(String movimiento){
		String resultado= "";
		try {
			String movimientos[] = movimiento.split("-");
			for (int i = 0; i < movimientos.length; i++) {
				String auxiliar[] = movimientos[i].split(",");
				resultado = resultado + consultasDao.validar3200(auxiliar) +"<br>";
			}
		} catch (Exception e) {

		}return resultado;
	}

	public List<SaldoChequeraDto> obtenerSaldoInicialBanco(DatosChequeraDto dto){
		return consultasDao.obtenerSaldoInicialBanco(dto);
	}
	///creacion de excel
	public static HSSFWorkbook generarExcel(String[] headers,
			List<Map<String, String>> data,
			String[] keys, String titulo) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();

		int rowIdx = 0;
		int cellIdx = 0;

		HSSFRow hssfHeader = sheet.createRow(rowIdx);
		HSSFFont font = wb.createFont();
		HSSFCellStyle cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		//font.setFontHeight(new Short("18"));
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(HSSFCellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.DARK_GREEN().getIndex());


		HSSFCell celdaTitulo = hssfHeader.createCell(cellIdx);
		celdaTitulo.setCellStyle(cellStyle);
		celdaTitulo.setCellValue(new HSSFRichTextString(titulo));

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));

		rowIdx = 1;
		// Header
		hssfHeader = sheet.createRow(rowIdx);
		font = wb.createFont();
		cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(HSSFCellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.LIGHT_BLUE().getIndex());

		for (String string : headers) {
			HSSFCell hssfCell = hssfHeader.createCell(cellIdx++);
			hssfCell.setCellStyle(cellStyle);
			hssfCell.setCellValue(new HSSFRichTextString(string));

		}

		//Data
		rowIdx = 2;
		int rech=0;
		int imp=0;
		int proc=0;
		System.out.println("datos "+data.size());
		for (Iterator<Map<String, String>> rows = data.iterator(); rows.hasNext();) {
			Map<String, String> row = rows.next();
			HSSFRow hssfRow = sheet.createRow(rowIdx++);
			cellIdx = 0;


			for (String string : keys) {
				HSSFCell hssfCell = hssfRow.createCell(cellIdx++);
				hssfCell.setCellValue(new HSSFRichTextString(row.get(string)));
			}
			System.out.println("estados "+row.get("estatus"));
			if(row.get("ESTATUS").equals("R")){
				rech++;
			}else{
				if(row.get("ESTATUS").equals("I")){
					imp++;
				}else{
					if(row.get("ESTATUS").equals("P")){
						proc++;
					}
				}
			}


		}
		System.out.println("RECHAZADOS"+rech);
		System.out.println("importados"+imp);
		System.out.println("en proceso"+proc);
		
		HSSFRow hssfHeader2 = sheet.createRow(rowIdx+2);
		HSSFCell hssfCell = hssfHeader2.createCell(0);
		hssfCell.setCellStyle(cellStyle);
		hssfCell.setCellValue(new HSSFRichTextString("Total Registros Rechazados"));
		hssfCell = hssfHeader2.createCell(1);
		hssfCell.setCellStyle(cellStyle);
		hssfCell.setCellValue(new HSSFRichTextString("Total Registros Exitosos"));
		hssfCell = hssfHeader2.createCell(2);
		hssfCell.setCellStyle(cellStyle);
		hssfCell.setCellValue(new HSSFRichTextString("Total Registros en Proceso"));
	

		HSSFRow hssfRow = sheet.createRow(rowIdx+3);
		HSSFCell hssfCell2 = hssfRow.createCell(0);
		hssfCell2.setCellValue(new HSSFRichTextString(String.valueOf(rech)));
		hssfCell2 = hssfRow.createCell(1);
		hssfCell2.setCellValue(new HSSFRichTextString(String.valueOf(imp)));
		hssfCell2 = hssfRow.createCell(2);
		hssfCell2.setCellValue(new HSSFRichTextString(String.valueOf(proc)));

		HSSFRow hssfHeader3 = sheet.createRow(rowIdx+5);
		HSSFCell hssfCell3 = hssfHeader3.createCell(3);
		hssfCell3.setCellStyle(cellStyle);
		hssfCell3.setCellValue(new HSSFRichTextString("Total Registros "));
		HSSFRow hssfRow1 = sheet.createRow(rowIdx+6);
		HSSFCell hssfCell1 = hssfRow1.createCell(3);
		hssfCell1.setCellValue(new HSSFRichTextString(String.valueOf(rech+imp+proc)));
		
		wb.setSheetName(0, "Hoja 1");

		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn((short)i);
		}	

		return wb;
	}
	
	public String leerRuta(int folio) {
		String ruta1= "";
		String ruta2 = "";
		String rutaTotal = "";
		
		try {	    	
			ruta1 = consultasDao.leerRutaConfSet(3040);
			ruta2 = consultasDao.leerRutaComentario2(folio);
			//Se indica la ruta
			rutaTotal=ruta1+ruta2;
			
			if(ruta1==null || ruta1.equals("")) {
				rutaTotal = "Error: Especifique la ruta destino del PDF en CONFIGURA_SET 3040";
			}
			else if(ruta2==null || ruta2.equals("")){
				rutaTotal = "Error: El movimiento no cuenta con comprobante bancario";
			}
		}
		catch(Exception e){
			e.printStackTrace();
			logger.error(e);
			//bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:bisness, C:Consultabusines, M:leerruta");
			rutaTotal = "Ocurrio un error: " + e.getMessage();
		}
		return rutaTotal;
	}

}
