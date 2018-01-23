package com.webset.set.consultas.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import net.sf.jasperreports.engine.JRDataSource;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContext;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.consultas.business.ConsultasBusiness;
import com.webset.set.consultas.dto.ChequeraGridDto;
import com.webset.set.consultas.dto.DatosChequeraDto;
import com.webset.set.consultas.dto.ParamBusSolicitudDto;
import com.webset.set.consultas.dto.MovimientoDto;
import com.webset.set.consultas.dto.ParamBusquedaMovimientoDto;
import com.webset.set.consultas.dto.ParamZimpFactDto;
import com.webset.set.consultas.dto.ParametroGridChequeraDto;
import com.webset.set.consultas.dto.ReportesChequeraDto;
import com.webset.set.consultas.dto.SaldoChequeraDto;
import com.webset.set.graficas.CreacionGrafica;
import com.webset.set.impresion.service.ImpresionService;
import com.webset.set.utilerias.dto.BitacoraChequesDto;
import com.webset.set.utilerias.dto.ComunDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.Retorno;
import com.webset.set.utilerias.dto.RetornoUnicoDto;
import com.webset.set.utilerias.dto.RevividorDto;
import com.webset.utils.tools.Utilerias;

/**
 * @author 
 */
public class ConsultasAction {
	private Contexto contexto = new Contexto();
	private ConsultasBusiness consultasBusiness;
	private Bitacora bitacora = new Bitacora();
	private Funciones funciones = new Funciones();
	private static Logger logger = Logger.getLogger(ConsultasAction.class);
	GlobalSingleton globalSingleton;
	/**
	 * @param idEmpresa
	 * @return List<ComunDto> 
	 *
	 *	Hace llamada  las tablas cat_banco y cat_cta_banco y obtine el id y la descripcion del banco
	 *  Public Function FunSQLCombo336(ByVal pvvValor1 As Variant) As ADODB.Recordset,FunSQLCombo337
	 */
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboBanco(int iIdEmpresa){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<LlenaComboGralDto> datos = new ArrayList<LlenaComboGralDto>();
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			datos = consultasBusiness.llenarComboCatCtaBanco(iIdEmpresa);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modconsulta, C:ConsultasAction, M:llenarComboCatCtaBanco");
		}
		return datos;
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
	@DirectMethod
	public List<ComunDto> llenarComboChequera(int idBanco, int idEmpresa){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<ComunDto> datos=null;
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");	
			if(idEmpresa > 0 && idBanco>0)
				datos = consultasBusiness.llenarComboChequera(idBanco, idEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modconsulta, C:ConsultasAction, M:llenarComboChequera");
		}
		return datos;
	}
	
	/**
	 * 
	 * @param descBanco
	 * @param idChequera
	 * @param noEmpresa
	 * @param idBanco
	 * @param fecValor
	 * @return SaldoChequeraDto
	 * 
	 * Public Function FunSQLSelect802(ByVal pvvValor1 As Variant, ByVal pvvValor2 As Variant, ByVal pvvValor3 As Variant, ByVal pvvValor4 As Variant, ByVal pvvValor5 As Variant) As ADODB.Recordset
	 * 
	 * Seleciona los datos para llenar la pantalla de Saldo Historico de Chequeras
	 */
	@DirectMethod
	public List<SaldoChequeraDto> seleccionarDatosChequeraHistorico(int noEmpresa, int idBanco, String descBanco, String idChequera, String fecValor){
		DatosChequeraDto datosChequeraDto = null;
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		funciones = new Funciones();
		List<SaldoChequeraDto> saldoChequeraDto = null;
		if( descBanco.equals("")  ||
			idChequera.equals("") ||
			noEmpresa < 1 || idBanco < 1 ||
			fecValor.equals("")){
			return saldoChequeraDto;
		}
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			datosChequeraDto = new DatosChequeraDto();
			datosChequeraDto.setDescBanco(descBanco);
			datosChequeraDto.setIdChequera(idChequera);
			datosChequeraDto.setNoEmpresa(noEmpresa);
			datosChequeraDto.setIdBanco(idBanco);
			datosChequeraDto.setFecValor(funciones.cambiarFecha(fecValor.replace("T", " ")));
			saldoChequeraDto = consultasBusiness.seleccionarDatosChequeraHistorico(datosChequeraDto);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modconsulta, C:ConsultasAction, M:seleccionarDatosChequeraHistorico");
		}
		return saldoChequeraDto;
	}
	
	/**
	 * 
	 * @param descBanco
	 * @param idChequera
	 * @param noEmpresa
	 * @param idBanco
	 * @param fecValor
	 * @return SaldoChequeraDto
	 * 
	 * Public Function FunSQLSelect802(ByVal pvvValor1 As Variant, ByVal pvvValor2 As Variant, ByVal pvvValor3 As Variant, ByVal pvvValor4 As Variant, ByVal pvvValor5 As Variant) As ADODB.Recordset
	 * 
	 * Seleciona los datos para llenar la pantalla de Saldo de Chequeras
	 */
	@DirectMethod
	public List<SaldoChequeraDto> seleccionarDatosChequera(int noEmpresa, int idBanco, String descBanco, String idChequera){
		DatosChequeraDto datosChequeraDto = null;
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<SaldoChequeraDto> saldoChequeraDto = null;
		if( idChequera.equals("") ||
			noEmpresa < 1 || idBanco < 1){
			return saldoChequeraDto;
		}
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			datosChequeraDto = new DatosChequeraDto();
			datosChequeraDto.setDescBanco(descBanco);
			datosChequeraDto.setIdChequera(idChequera);
			datosChequeraDto.setNoEmpresa(noEmpresa);
			datosChequeraDto.setIdBanco(idBanco);
			saldoChequeraDto = consultasBusiness.seleccionarDatosChequera(datosChequeraDto);
//			saldoChequeraDto.add((SaldoChequeraDto) obtenerSaldoInicialBanco(datosChequeraDto));
		//	System.out.println("saldos"+saldoChequeraDto.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modconsulta, C:ConsultasAction, M:seleccionarDatosChequera");
		}
		return saldoChequeraDto;
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
	@DirectMethod
	public List<RetornoUnicoDto> seleccionarImporteTotalMovimiento(int noEmpresa, int idBanco, String idChequera, String fecValor){
		List<RetornoUnicoDto> datos = null;
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		funciones = new Funciones();
		if( idChequera.equals("") ||
				noEmpresa < 1 || idBanco < 1 ||
				fecValor.equals("")){
			return datos;
		}
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			DatosChequeraDto datosChequeraDto = new DatosChequeraDto();
			datosChequeraDto.setIdChequera(idChequera);
			datosChequeraDto.setNoEmpresa(noEmpresa);
			datosChequeraDto.setIdBanco(idBanco);
			datosChequeraDto.setFecValor(funciones.cambiarFecha(fecValor.replace("T", " ")));
			datos = consultasBusiness.seleccionarImporteTotalMovimiento(datosChequeraDto);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modconsulta, C:ConsultasAction, M:seleccionarImporteTotalMovimiento");
		}
		return datos;
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
	@DirectMethod
	public List<ChequeraGridDto> seleccionarDatosGridAbonos(String fecha, int idEmpresa, int idBanco, String idChequera){
		funciones = new Funciones();
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		fecha = funciones.cambiarFecha(fecha, true);
		ParametroGridChequeraDto dto = new ParametroGridChequeraDto();
		List<ChequeraGridDto> datos = null;
		dto.setFecMod(fecha);
		dto.setFecValor(fecha);
		dto.setSubQuery1(" and m.fec_valor_original = '");
		dto.setSubQuery2(" then m.importe *-1 else m.importe end as importe, m.beneficiario");
		dto.setIdEmpresa(idEmpresa);
		dto.setIdBanco(idBanco);
		dto.setIdChequera(idChequera);
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			datos = consultasBusiness.seleccionarDatosGridAbonos(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modconsulta, C:ConsultasAction, M:seleccionarDatosGridAbonos");
		}
		return datos;
	}
	
	/**
	 * 
	 * @param dto
	 * @return List<ChequeraGridDto>
	 * 
	 * Public Function FunSQLSelect804( ByVal pvvValor1 As Variant, ByVal pvvValor2 As Variant, 
	 * 									ByVal pvvValor3 As Variant, ByVal pvvValor4 As Variant, 
	 * 									ByVal pvvValor5 As Variant, ByVal pvvValor6 As Variant, 
	 * 									ByVal pvvValor7 As Variant) As ADODB.Recordset
	 *  
	 * Retorna los datos de los cargos
	 */
	@DirectMethod
	public List<ChequeraGridDto> seleccionarDatosGridCargos(String fecha, int idEmpresa, int idBanco, String idChequera){
		funciones = new Funciones();
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		fecha = funciones.cambiarFecha(fecha, true);
		ParametroGridChequeraDto dto = new ParametroGridChequeraDto();
		List<ChequeraGridDto> datos = null;
		dto.setFecMod(fecha);
		dto.setFecValor(fecha);
		dto.setSubQuery1(" and m.fec_valor < '");
		dto.setSubQuery2(" then m.importe *-1 else m.importe end as importe, m.beneficiario");
		dto.setIdEmpresa(idEmpresa);
		dto.setIdBanco(idBanco);
		dto.setIdChequera(idChequera);
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			datos = consultasBusiness.seleccionarDatosGridCargos(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modconsulta, C:ConsultasAction, M:seleccionarDatosGridCargos");
		}
		return datos;
	}
	
	/**
	 * 
	 * @return Retorno
	 * 
	 * Saca la fecha de hoy segun la Base de datos 
	 */
	@DirectMethod
	public Retorno obtenerFechaHoy(){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		Retorno result = null;
		try{
			result = new Retorno();
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			result.setFechaHoy(consultasBusiness.obtenerFechaHoy());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modconsulta, C:ConsultasAction, M:obtenerFechaHoy");
		}
		return result;
	}
	/**
	 * 
	 * @return Retorno
	 * 
	 * Saca la fecha de hoy segun la Base de datos 
	 */
	@DirectMethod
	public Retorno obtenerFechaAyer(){
		if (!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 77)) 
			return null;
		Retorno result = null;
		try{
			result = new Retorno();
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			result.setFechaAyer(consultasBusiness.obtenerFechaAyer());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modconsulta, C:ConsultasAction, M:obtenerFechaAyer");
		}
		return result;
	}
	/**
	 * 
	 * @param fecha
	 * @param idEmpresa
	 * @param idBanco
	 * @param idChequera
	 * @return List<ChequeraGridDto>
	 * 
	 * Public Function SELECTMvtosAbonosCheq(ByVal pvsFecha As String, ByVal pviEmpresa As Integer, ByVal pviBanco As Integer, ByVal pvsChequera As String) As ADODB.Recordset
	 * 
	 * Selecciona los datos para el grid de saldo chequera abonos
	 */
	@DirectMethod
	public List<ChequeraGridDto> seleccionarMvtosAbonosChequera(String fecha, int idEmpresa, int idBanco, String idChequera){
		funciones = new Funciones();
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		fecha = funciones.cambiarFecha(fecha, true);
		ParametroGridChequeraDto dto = new ParametroGridChequeraDto();
		List<ChequeraGridDto> datos = null;
		dto.setFecValor(fecha);
		dto.setIdEmpresa(idEmpresa);
		dto.setIdBanco(idBanco);
		dto.setIdChequera(idChequera);
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			datos = consultasBusiness.seleccionarMvtosAbonosChequera(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modconsulta, C:ConsultasAction, M:seleccionarMvtosAbonosChequera");
		}
		return datos;
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
	@DirectMethod
	public List<ChequeraGridDto> seleccionarMvtosCargosChequera(String fecha, int idEmpresa, int idBanco, String idChequera){
		funciones = new Funciones();
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		fecha = funciones.cambiarFecha(fecha, true);
		ParametroGridChequeraDto dto = new ParametroGridChequeraDto();
		List<ChequeraGridDto> datos = null;
		dto.setFecValor(fecha);
		dto.setIdEmpresa(idEmpresa);
		dto.setIdBanco(idBanco);
		dto.setIdChequera(idChequera);
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			datos = consultasBusiness.seleccionarMvtosCargosChequera(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modconsulta, C:ConsultasAction, M:seleccionarMvtosCargosChequera");
		}
		return datos;
	}
	
	/**
	 * 
	 * @param idUsuario
	 * @return List<ComunDto>
	 * 
	 * llenar el combo de saldo de chequeras
	 */
	@DirectMethod
	public List<ComunDto> llenarComboEmpresa(){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		System.out.println("llenarcomboempresa");
		
		List<ComunDto> datos = null;
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			datos =  consultasBusiness.llenarComboEmpresa(GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario());
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modconsulta, C:ConsultasAction, M:llenarComboEmpresa");
		}
		return datos;
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
	@DirectMethod
	public List<RetornoUnicoDto> seleccionarFechaActualizacion(int idEmpresa, int idBanco, String idChequera){
		List<RetornoUnicoDto> datos = null;
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		ParametroGridChequeraDto dto = new ParametroGridChequeraDto();
		dto.setIdEmpresa(idEmpresa);
		dto.setIdBanco(idBanco);
		dto.setIdChequera(idChequera);
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			datos = consultasBusiness.seleccionarFechaActualizacion(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modconsulta, C:ConsultasAction, M:seleccionarFechaActualizacion");
		}
		return datos;
	}
	/**
	 * 
	 * @param idEmpresa
	 * @param idBanco
	 * @param idChequera
	 * @return List<RetornoUnicoDto>
	 * 
	 * Public Function FunSQLSelect798(ByVal pvvValor1 As Variant, ByVal pvvValor2 As Variant, ByVal pvvValor3 As Variant) As ADODB.Recordset
	 * 
	 * Selecciona el valor de chequeras por entregar para  SaldoChequera
	 */
	@DirectMethod
	public List<ChequeraGridDto> seleccionarPorEnt(String fecha, int idEmpresa, int idBanco, String idChequera){
		funciones = new Funciones();
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		fecha = funciones.cambiarFecha(fecha, true);
		ParametroGridChequeraDto dto = new ParametroGridChequeraDto();
		List<ChequeraGridDto> datos = null;
		dto.setFecValor(fecha);
		dto.setIdEmpresa(idEmpresa);
		dto.setIdBanco(idBanco);
		dto.setIdChequera(idChequera);
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			datos = consultasBusiness.seleccionarPorEnt(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modconsulta, C:ConsultasAction, M:seleccionarPorEnt");
		}
		return datos;
	}
/*inicio bloque MAFJ 12 MAR 2014	
	public List<RetornoUnicoDto>  seleccionarPorEnt(int idEmpresa, int idBanco, String idChequera){
		//logger.info("Ent->Empresa: "+idEmpresa +" Banco: "+idBanco +" Chequera: "+idChequera);
		List<RetornoUnicoDto> datos = null;
		ParametroGridChequeraDto dto = new ParametroGridChequeraDto();
		dto.setIdEmpresa(idEmpresa);
		dto.setIdBanco(idBanco);
		dto.setIdChequera(idChequera);
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			datos = consultasBusiness.seleccionarPorImp(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modconsulta, C:ConsultasAction, M:seleccionarPorImp");
		}
		return datos;
	}
	FIN DE BLOQUE */
	
	/**
	 * 
	 * @param idEmpresa
	 * @param idBanco
	 * @param idChequera
	 * @return List<RetornoUnicoDto>
	 * 
	 * Public Function FunSQLSelect799(ByVal pvvValor1 As Variant, ByVal pvvValor2 As Variant, ByVal pvvValor3 As Variant) As ADODB.Recordset
	 * 
	 * Selecciona el valor de chequeras por imprimir para  SaldoChequera
	 */
	@DirectMethod
	public List<RetornoUnicoDto>  seleccionarPorImp(int idEmpresa, int idBanco, String idChequera){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		//logger.info("Imp->Empresa: "+idEmpresa +" Banco: "+idBanco +" Chequera: "+idChequera);
		List<RetornoUnicoDto> datos = null;
		ParametroGridChequeraDto dto = new ParametroGridChequeraDto();
		dto.setIdEmpresa(idEmpresa);
		dto.setIdBanco(idBanco);
		dto.setIdChequera(idChequera);
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			datos = consultasBusiness.seleccionarPorImp(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modconsulta, C:ConsultasAction, M:seleccionarPorImp");
		}
		return datos;
	}
	
	/**
	 * 
	 * @param idEmpresa
	 * @param idBanco
	 * @param idChequera
	 * @return List<RetornoUnicoDto>
	 * 
	 * Public Function FunSQLSelect800(ByVal pvvValor1 As Variant, 
	 * 			ByVal pvvValor2 As Variant, ByVal pvvValor3 As Variant) As ADODB.Recordset
	 * 
	 * Selecciona el valor de chequeras por transferir para  SaldoChequera
	 */
	@DirectMethod
	public List<RetornoUnicoDto>  seleccionarPorTrans(int idEmpresa, int idBanco, String idChequera){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		//logger.info("Trans->Empresa: "+idEmpresa +" Banco: "+idBanco +" Chequera: "+idChequera);
		List<RetornoUnicoDto> datos = null;
		ParametroGridChequeraDto dto = new ParametroGridChequeraDto();
		dto.setIdEmpresa(idEmpresa);
		dto.setIdBanco(idBanco);
		dto.setIdChequera(idChequera);
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			datos = consultasBusiness.seleccionarPorTrans(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modconsulta, C:ConsultasAction, M:seleccionarPorTrans");
		}
		return datos;
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
	@DirectMethod
	public List<ChequeraGridDto> seleccionarAbonoSBC(int idEmpresa, int idBanco, String idChequera){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		ParametroGridChequeraDto dto = new ParametroGridChequeraDto();
		List<ChequeraGridDto> datos = null;
		dto.setIdEmpresa(idEmpresa);
		dto.setIdBanco(idBanco);
		dto.setIdChequera(idChequera);
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			datos = consultasBusiness.seleccionarAbonoSBC(dto);
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modconsulta, C:ConsultasAction, M:seleccionarAbonoSBC");
		}
		return datos;
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
	@DirectMethod
	public List<RetornoUnicoDto>  seleccionarSumaAbonosSBC(String fecha, int idEmpresa, int idBanco, String idChequera){
		funciones = new Funciones();
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		fecha = funciones.cambiarFecha(fecha, true);
		ParametroGridChequeraDto dto = new ParametroGridChequeraDto();
		List<RetornoUnicoDto> datos = null;
		dto.setFecValor(fecha);
		dto.setIdEmpresa(idEmpresa);
		dto.setIdBanco(idBanco);
		dto.setIdChequera(idChequera);
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			datos = consultasBusiness.seleccionarSumaAbonosSBC(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modconsulta, C:ConsultasAction, M:seleccionarSumaAbonosSBC");
		}
		return datos;
	}
	
	/**
	 * Este m\u00e9todo realiza el llenado del combo de cajas,
	 * utilizado en ConsultaDeMovimientos
	 * @return
	 */
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboCajas(){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<LlenaComboGralDto> listCajas = new ArrayList<LlenaComboGralDto>();
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			listCajas = consultasBusiness.obtenerCajas();
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasAction, M:llenarComboCajas");
		}
		return listCajas;
	}
	
	/**
	 * Este m\u00e9todo obtiene las chequeras asignadas a un banco,
	 * es utilizado en ConsultaDeMovimientos
	 * @param iIdBanco
	 * @return
	 */
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboChequeras(int iIdBanco, int iIdEmpresa){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<LlenaComboGralDto> listCheq = new ArrayList<LlenaComboGralDto>();
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			listCheq = consultasBusiness.obtenerChequeras(iIdBanco,iIdEmpresa);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasAction, M:llenarComboChequeras");
		}
		return listCheq;
	}
	
	/**
	 * M\u00e9todo utilizado en ConsultaDeMovimientos 
	 * para llenar el combo de divisiones.
	 * @param bActual
	 * @return
	 */
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboDivisiones(boolean bActual){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<LlenaComboGralDto> listDiv = new ArrayList<LlenaComboGralDto>();
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			listDiv = consultasBusiness.obtenerDivisiones(bActual);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasAction, M:llenarComboDivisiones");
		}
		return listDiv;
	}
	
	/**
	 * Este m\u00e9todo obtiene los tipos de estatus, es utilizado
	 * en ConsultaDeMovimientos
	 * @return
	 */
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboEstatus(){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<LlenaComboGralDto> listDiv = new ArrayList<LlenaComboGralDto>();
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			listDiv = consultasBusiness.obtenerEstatus();
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasAction, M:llenarComboEstatus");
		}
		return listDiv;
	}
	
	/**
	 * Este m\u00e9todo obtiene los origenes de movimiento de la tabla
	 * cat_origen_mov, es utilizado en ConsultaDeMovimientos
	 * @return
	 */
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboOrigen(){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<LlenaComboGralDto> listDiv = new ArrayList<LlenaComboGralDto>();
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			listDiv = consultasBusiness.obtenerOrigen();
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasAction, M:llenarComboOrigen");
		}
		return listDiv;
	}
	
	/**
	 * Este m\u00e9todo obtiene los tipos de operacion, es utilizado en 
	 * ConsultaDeMovimientos
	 * @return
	 */
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboTipoOperacion(){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<LlenaComboGralDto> listTipoOperacion = new ArrayList<LlenaComboGralDto>();
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			listTipoOperacion = consultasBusiness.obtenerTiposOperacion();
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasAction, M:llenarComboTipoOperacion");
		}
		return listTipoOperacion;
	}
	
	
	/**
	 * Este m\u00e9todo es utilizado en ConsultaDeMovimientos,
	 * para obtener los tipos de valor custodia
	 * @param bActual
	 * @return
	 */
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboValorCustodia(boolean bActual){
		if (!Utilerias.haveSession(WebContextManager.get()) ) 
			return null;
		List<LlenaComboGralDto> listValCus = new ArrayList<LlenaComboGralDto>();
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			listValCus = consultasBusiness.obtenerValorCustodia(bActual);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasAction, M:llenarComboValorCustodia");
		}
		return listValCus;
	}
	
	@DirectMethod
	public List<Map<String, Object>> consultarMovimientos(int iIdBanco,int iIdBeneficiario, String sBeneficiario,int iIdCaja,
			String sIdChequera, String sConcepto, String sIdDivisa,
			int iIdDivision, String sEstatus, int iFolMovi,
			int iIdFormaPago, String sCvePropuesta, double uMontoIni,
			double uMontoFin, int iNoCheque, String sNoDocto,
			int iNoFactura, String sOrigen, int iTipoOperacion,
			String sValorCustodia, String sFecIni, String sFecFin,
//			boolean bOptEmp, boolean bOptTipoMovto, boolean bSolCan)
			boolean bOptEmp, int noEmpresa, boolean bOptTipoMovto, boolean bSolCan , String pooHeaders)
	{
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<Map<String, Object>> listConsMovi  = new ArrayList<Map<String, Object>>();
		
		try
		{
			String iUserId= GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+"";
			if(iUserId.equals(""))
				return null;
			ParamBusquedaMovimientoDto dto = new ParamBusquedaMovimientoDto();
			dto.setIdBanco(iIdBanco);
			dto.setIdProveedor(iIdBeneficiario);
			dto.setBeneficiario(sBeneficiario);
			dto.setIdCaja(iIdCaja);
			dto.setIdChequera(funciones.validarCadena(sIdChequera));
			dto.setConcepto(funciones.validarCadena(sConcepto));
			dto.setIdDivisa(funciones.validarCadena(sIdDivisa));
			dto.setIdDivision(iIdDivision);
			dto.setEstatus(funciones.validarCadena(sEstatus));
			dto.setFolioMovimiento(iFolMovi);
			dto.setIdFormaPago(iIdFormaPago);
			dto.setCvePropuesta(funciones.validarCadena(sCvePropuesta));
			dto.setMontoInicial(uMontoIni);
			dto.setMontoFinal(uMontoFin);
			dto.setNoCheque(iNoCheque);
			dto.setNoDocto(funciones.validarCadena(sNoDocto));
			dto.setNoFactura(iNoFactura);
			dto.setOrigen(funciones.validarCadena(sOrigen));
			dto.setTipoOperacion(iTipoOperacion);
			dto.setValorCustodia(funciones.isNumeric(sValorCustodia) ? sValorCustodia.toString() : sValorCustodia);
			dto.setFechaInicial(funciones.validarCadena(sFecIni));
			dto.setFechaFinal(funciones.validarCadena(sFecFin));
			dto.setOptEmp(bOptEmp);
			dto.setNoEmpresa(noEmpresa);
			dto.setOptMovto(bOptTipoMovto);
			dto.setITalblaInicio(0);
			dto.setFolioDet(-1);
			dto.setIHist(0);
			dto.setBReporte(false);
			dto.setBEncReporte(false);
			dto.setPooHeaders(pooHeaders);
			System.out.println(dto.getPooHeaders());
			if(bSolCan)
				dto.setITablaFin(2);
			else
				dto.setITablaFin(1);
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			
			listConsMovi = consultasBusiness.consultarMovimientos(dto);
			
			/*if(listConsMovi!=null && listConsMovi.size()>0)
			{
				//Nombre de la grafica.
				String sName = noEmpresa+""+iIdBanco+sOrigen+sFecIni.replaceAll("/", "")+sFecFin.replaceAll("/", "");
				
				//Grafica: Empresas/NumMovs
				Map<String, Double> mEmpMovsPie = new HashMap<String, Double>();
				Map<String, Double> mEmpMovsBarra = new HashMap<String, Double>();
				//Grafica: Banco/Importe
				Map<String, Double> mBanImp = new HashMap<String, Double>();
				//Grafica: FormaPago/NumeroMovimientos
				Map<String, Double> mForPagoMovs = new HashMap<String, Double>();
				Map<String, Double> mForPagoImpo = new HashMap<String, Double>();
				//Grafica: Estatus/NumeroMovimientos
				Map<String, Double> mEstMovs = new HashMap<String, Double>();
				
				//Numero de movimientos
				double dNumMovs=0, dForPagoMovs=0, dEstMovs=0;
				//Clase de creacion de graficas
				CreacionGrafica cg = new CreacionGrafica();
				
				for(int i=0; i<listConsMovi.size(); i++){
					MovimientoDto movtoDto = (MovimientoDto)listConsMovi.get(i);
					
					//Si se selecciono TODAS empresas
					if(noEmpresa==0)
					{
						//Grafica: Empresas/NumMovs
						if(mEmpMovsPie.containsKey(movtoDto.getNomEmpresa())){
							dNumMovs = mEmpMovsPie.get(movtoDto.getNomEmpresa());
							double dImporte = mEmpMovsBarra.get(movtoDto.getNomEmpresa());
							mEmpMovsPie.remove(movtoDto.getNomEmpresa());
							mEmpMovsBarra.remove(movtoDto.getNomEmpresa());
							mEmpMovsPie.put(movtoDto.getNomEmpresa(), (dNumMovs+1));
							mEmpMovsBarra.put(movtoDto.getNomEmpresa(), (movtoDto.getImporte()+dImporte));
						}
						else {
							dNumMovs=1;
							mEmpMovsPie.put(movtoDto.getNomEmpresa(), dNumMovs);
							mEmpMovsBarra.put(movtoDto.getNomEmpresa(), movtoDto.getImporte());
						}
					}
					
					//Grafica: Bancos/Importe
					if(mBanImp.containsKey(movtoDto.getDescBanco())){
						double dImporte = mBanImp.get(movtoDto.getDescBanco());
						mBanImp.remove(movtoDto.getDescBanco());
						mBanImp.put(movtoDto.getDescBanco(), (movtoDto.getImporte()+dImporte));
					}
					else {
						if(movtoDto.getIdBanco()!=0)
							mBanImp.put(movtoDto.getDescBanco(), movtoDto.getImporte());
					}
					
					//Grafica: FormaPago/Movs
					if(mForPagoMovs.containsKey(movtoDto.getDescFormaPago())){
						dForPagoMovs = mForPagoMovs.get(movtoDto.getDescFormaPago());
						double dImporte = mForPagoImpo.get(movtoDto.getDescFormaPago());
						mForPagoMovs.remove(movtoDto.getDescFormaPago());
						mForPagoImpo.remove(movtoDto.getDescFormaPago());
						mForPagoMovs.put(movtoDto.getDescFormaPago(), (dForPagoMovs+1));
						mForPagoImpo.put(movtoDto.getDescFormaPago(), (movtoDto.getImporte() + dImporte));
					}
					else {
						if(movtoDto.getDescFormaPago()!=null) {
							dForPagoMovs=1;
							mForPagoMovs.put(movtoDto.getDescFormaPago(), dForPagoMovs);
							mForPagoImpo.put(movtoDto.getDescFormaPago(), movtoDto.getImporte());
						}
					}
					
					//Grafica: Estatus/Movs
					if(mEstMovs.containsKey(movtoDto.getDescEstatus())){
						dEstMovs = mEstMovs.get(movtoDto.getDescEstatus());
						mEstMovs.remove(movtoDto.getDescEstatus());
						mEstMovs.put(movtoDto.getDescEstatus(), (dEstMovs+1));
					}
					else {
						if(movtoDto.getDescEstatus()!=null) {
							dEstMovs=1;
							mEstMovs.put(movtoDto.getDescEstatus(), dEstMovs);
						}
					}
				}
				
				if(noEmpresa==0)
				{
					//Empresas/Numero Movimientos
					cg.crearGraficaPie   (iUserId+"", "EmpresaNumMovs"+sName, "Numero Movimientos por Empresa", mEmpMovsPie);
					cg.crearGraficaBarras(iUserId+"", "EmpresaNumMovs"+sName, "Importe Movimientos por Empresa", "Importe", mEmpMovsBarra, true);
					cg.crearGraficaLineas(iUserId+"", "EmpresaNumMovs"+sName, "Importe Movimientos por Empresa", "Importe", mEmpMovsBarra, true);
				}
				//Bancos/Importe
				cg.crearGraficaPie   (iUserId+"", "BancosImporte"+sName, "Importe Movimientos por Banco", mBanImp);
				cg.crearGraficaBarras(iUserId+"", "BancosImporte"+sName, "Importe Movimientos por Banco", "Importe", mBanImp, true);
				cg.crearGraficaLineas(iUserId+"", "BancosImporte"+sName, "Importe Movimientos por Banco", "Importe", mBanImp, true);
				
				//FormaPagos/NumMovimientos
				cg.crearGraficaPie   (iUserId+"", "FormaPagoNumMovs"+sName, "Numero Movimientos por Forma de Pago", mForPagoMovs);
				cg.crearGraficaBarras(iUserId+"", "FormaPagoNumMovs"+sName, "Importe por Forma de Pago", "Importe", mForPagoImpo, true);
				cg.crearGraficaLineas(iUserId+"", "FormaPagoNumMovs"+sName, "Importe por Forma de Pago", "Importe", mForPagoImpo, true);
				
				//Estatus/NumMovimientos
				cg.crearGraficaPie   (iUserId+"", "EstatusNumMovs"+sName, "Numero Movimientos por Estatus", mEstMovs);
				//cg.crearGraficaBarras(iUserId+"", "EstatusNumMovs", "Forma Pago Num Movs", "Num Movs", mForPagoMovs, true);
				//cg.crearGraficaLineas(iUserId+"", "EstatusNumMovs", "Forma Pago Num Movs", "Num Movs", mForPagoMovs, true);
				
			}//Fin graficas
			*/
		}
		catch(Exception e){
			logger.error(e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasAction, M:consultarMovimientos");
			e.printStackTrace();
		}
		return listConsMovi;
	}
	
	/**
	 * Consulta el total de movimientos para graficarlos por Ingresos/Egresos.
	 * A partir de la empresa y un rango de fechas.
	 * @param noEmpresa
	 * @param sFecIni
	 * @param sFecFin
	 * @param iUserId
	 * @return
	 */
/*	@DirectMethod
	public List<Map<String, Object>> graficarTotalMovimientos(int noEmpresa, String sFecIni, String sFecFin)
	{
		if (!Utilerias.haveSession(WebContextManager.get()) ) 
			return null;
		List<Map<String, Object>> listConsMovi  = new ArrayList<Map<String, Object>>();
		
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			listConsMovi = consultasBusiness.graficarTotalMovimientos(noEmpresa, sFecIni, sFecFin);
			String iUserId= GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+"";
			if(iUserId.equals(""))
				return null;
			if(listConsMovi!=null && listConsMovi.size()>0)
			{
				//Nombre de la grafica.
				String sName = noEmpresa+""+sFecIni.replaceAll("/", "")+sFecFin.replaceAll("/", "");
				//Datos de la grafica
				Map<String, Double> datos = new HashMap<String, Double>();
				//Clase de creacion de graficas
				CreacionGrafica cg = new CreacionGrafica();
				
				for(int i=0; i<listConsMovi.size(); i++){
					MovimientoDto movtoDto = (MovimientoDto)listConsMovi.get(i);
					
					if(datos.containsKey(movtoDto.getIdTipoMovto())){
						double dImporte = datos.get(movtoDto.getIdTipoMovto());
						datos.remove(movtoDto.getIdTipoMovto());
						datos.put(movtoDto.getIdTipoMovto(), (movtoDto.getImporte()+dImporte));
					}
					else {
						datos.put(movtoDto.getIdTipoMovto(), movtoDto.getImporte());
					}
				}
				cg.crearGraficaPie   (iUserId+"", "MovsTotales"+sName, "Tipo Movimiento / Total", datos);
				cg.crearGraficaBarras(iUserId+"", "MovsTotales"+sName, "Tipo Movimiento / Total", "Importe", datos, true);
				cg.crearGraficaLineas(iUserId+"", "MovsTotales"+sName, "Tipo Movimiento / Total", "Importe", datos, true);
			}//Fin graficas
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasAction, M:consultarMovimientos");
			e.printStackTrace();
		}
		return listConsMovi;
	}
*/	
	@SuppressWarnings("unchecked")
	@DirectMethod
	public JRDataSource obtenerReporteMovimientos(Map parametros, ServletContext contextoSer, WebContext context)
	{
		JRDataSource jrDataSource = null;
		try{
			ParamBusquedaMovimientoDto dto = new ParamBusquedaMovimientoDto();
			dto.setIdBanco(funciones.convertirCadenaInteger(parametros.get("iIdBanco").toString()));
			dto.setIdProveedor(funciones.convertirCadenaInteger(parametros.get("iIdBeneficiario").toString()));
			dto.setIdCaja(funciones.convertirCadenaInteger(parametros.get("iIdCaja").toString()));
			dto.setIdChequera(funciones.validarCadena(parametros.get("sIdChequera").toString()));
			dto.setConcepto(funciones.validarCadena(parametros.get("sConcepto").toString()));
			dto.setIdDivisa(funciones.validarCadena(parametros.get("sIdDivisa").toString()));
			dto.setIdDivision(funciones.convertirCadenaInteger(parametros.get("iIdDivision").toString()));
			dto.setEstatus(funciones.validarCadena(parametros.get("sEstatus").toString()));
			dto.setFolioMovimiento(funciones.convertirCadenaInteger(parametros.get("iFolMovi").toString()));
			dto.setIdFormaPago(funciones.convertirCadenaInteger(parametros.get("iIdFormaPago").toString()));
			dto.setCvePropuesta(funciones.validarCadena(parametros.get("sCvePropuesta").toString()));
			dto.setMontoInicial(funciones.convertirCadenaDouble(parametros.get("uMontoIni").toString()));
			dto.setMontoFinal(funciones.convertirCadenaDouble(parametros.get("uMontoFin").toString()));
			dto.setNoCheque(funciones.convertirCadenaInteger(parametros.get("iNoCheque").toString()));
			dto.setNoDocto(funciones.validarCadena(parametros.get("sNoDocto").toString()));
			dto.setNoFactura(funciones.convertirCadenaInteger(parametros.get("iNoFactura").toString()));
			dto.setOrigen(funciones.validarCadena(parametros.get("sOrigen").toString()));
			dto.setTipoOperacion(funciones.convertirCadenaInteger(parametros.get("iTipoOperacion").toString()));
			dto.setValorCustodia(funciones.validarCadena(parametros.get("sValorCustodia").toString()));
			dto.setFechaInicial(funciones.validarCadena(parametros.get("sFecIni").toString()));
			dto.setFechaFinal(funciones.validarCadena(parametros.get("sFecFin").toString()));
			dto.setOptEmp(funciones.convertirCadenaBoolean(parametros.get("bOptEmp").toString()));
			dto.setOptMovto(funciones.convertirCadenaBoolean(parametros.get("bOptTipoMovto").toString()));
			dto.setITalblaInicio(0);
			dto.setITablaFin(1);
			if(funciones.convertirCadenaBoolean(parametros.get("bSolCan").toString()))
				dto.setIHist(1);
			else
				dto.setIHist(0);
			dto.setNoEmpresa(Integer.parseInt(parametros.get("noEmpresa").toString()));
			
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness", contextoSer);
			jrDataSource = consultasBusiness.obtenerReporteMovimientos(dto, context);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasAction, M:obtenerReporteMovimientos");
		}
		return jrDataSource;
	}
	
	@DirectMethod
	public String exportarDatosExcel(String sDatos, int iNoColumn, String sForma){
		String sNomArch = "";
		
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			sNomArch = consultasBusiness.exportarDatosExcel(sDatos, iNoColumn, sForma);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasAction, M:exportarDatosExcel");
		}
		return sNomArch;
	}
	
	//@SuppressWarnings("unchecked")
	@DirectMethod
	public String ejecutarRevividor(String sDatGrid, String sDatBitaChe, String sNomForma){
		System.out.println("entra al action");
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		//Map<String, Object> mapRet = new HashMap<String, Object>();
		Gson gSon = new Gson();
		String mensaje = "Error no se llevo a cabo ningun cambio.";
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			List<Map<String, String>> gListDatos = gSon.fromJson(sDatGrid, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			List<Map<String, String>> gListBita = gSon.fromJson(sDatGrid, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			List<RevividorDto> listRev = new ArrayList<RevividorDto>();
			List<BitacoraChequesDto> listBitChe = new ArrayList<BitacoraChequesDto>();
			int iUserId= GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario();
			for(int i = 0; i < gListDatos.size(); i ++){
				RevividorDto dtoRev = new RevividorDto();
		        	dtoRev.setNomForma(sNomForma);
					dtoRev.setPsRevividor(funciones.validarCadena(gListDatos.get(i).get("psRevividor")));
					dtoRev.setNoFolioDet(funciones.convertirCadenaInteger(gListDatos.get(i).get("noFolioDet")));
					dtoRev.setIdTipoOperacion(funciones.convertirCadenaInteger(gListDatos.get(i).get("idTipoOperacion")));
					dtoRev.setPsTipoCancelacion(funciones.validarCadena(gListDatos.get(i).get("psTipoCancelacion")));
					dtoRev.setIdEstatusMov(funciones.validarCadena(gListDatos.get(i).get("idEstatusMov")));
					dtoRev.setPsOrigenMov(funciones.validarCadena(gListDatos.get(i).get("psOrigenMov")).equals("CXP") ? "SOI" : funciones.validarCadena(gListDatos.get(i).get("psOrigenMov")));
					dtoRev.setIdFormaPago(funciones.convertirCadenaInteger(gListDatos.get(i).get("idFormaPago")));
					dtoRev.setBEntregado(funciones.validarCadena(gListDatos.get(i).get("bEntregado")));	
					dtoRev.setIdTipoMovto(funciones.validarCadena(gListDatos.get(i).get("idTipoMovto")));
					dtoRev.setImporte(funciones.convertirCadenaDouble(gListDatos.get(i).get("importe")));
				//	System.out.println("importe a regresar "+funciones.convertirCadenaDouble(gListDatos.get(i).get("importe")));
					dtoRev.setNoEmpresa(Integer.parseInt(gListDatos.get(i).get("noEmpresa")));
					dtoRev.setNoCuenta(Integer.parseInt(gListDatos.get(i).get("noCuenta")));
					dtoRev.setIdChequera(funciones.validarCadena(gListDatos.get(i).get("idChequera")));
					dtoRev.setIdBanco(funciones.convertirCadenaInteger(gListDatos.get(i).get("idBanco")));
					dtoRev.setIdUsuario(iUserId);
					dtoRev.setObservaciones(funciones.validarCadena(gListDatos.get(i).get("observacion")));
					if(gListDatos.get(i).get("noDocto")!= null && gListDatos.get(i).get("noDocto").equals(""))
						dtoRev.setNoDocumento("0");
					else
					dtoRev.setNoDocumento(gListDatos.get(i).get("noDocto")!= null ? gListDatos.get(i).get("noDocto") : "0");
					dtoRev.setLote(funciones.convertirCadenaInteger(gListDatos.get(i).get("lote").toString()));
					dtoRev.setBSalvoBuenCobro(funciones.validarCadena(gListDatos.get(i).get("bSalvoBuenCobro").toString()));
					dtoRev.setFecConfTrans(funciones.validarCadena(gListDatos.get(i).get("fecConfTrans").toString()));
					dtoRev.setIdDivisa(funciones.validarCadena(gListDatos.get(i).get("idDivisa")));	
					dtoRev.setPoHeaders(gListDatos.get(i).get("poHeaders"));
					dtoRev.setFecPropuestaStr(gListDatos.get(i).get("fecPropuestaStr"));
					dtoRev.setPeridoDescompensar(gListDatos.get(i).get("peridoDescompensar"));
					dtoRev.setOrigen(gListDatos.get(i).get("origen"));
					int periodo=0;
					try{
						periodo=Integer.parseInt(gListDatos.get(i).get("periodo"));
					}catch(Exception e){
						periodo=0;
					}
					dtoRev.setEjercicio(periodo);
				    listRev.add(dtoRev);
				
			}
			
			for(int c = 0; c < gListBita.size(); c = c + 1){
				BitacoraChequesDto dtoBitChe = new BitacoraChequesDto();
				
				dtoBitChe.setNoFolioDet(funciones.convertirCadenaInteger(gListBita.get(c).get("noFolioDet")));
				dtoBitChe.setNoEmpresa(funciones.convertirCadenaInteger(gListBita.get(c).get("noEmpresa"))); 
				dtoBitChe.setIdBanco(funciones.convertirCadenaInteger(gListBita.get(c).get("idBanco")));
				dtoBitChe.setIdChequera(funciones.validarCadena(gListBita.get(c).get("idChequera")));
				dtoBitChe.setNoCheque(funciones.convertirCadenaInteger(gListBita.get(c).get("noCheque")));
				dtoBitChe.setIdEstatus(funciones.validarCadena(gListBita.get(c).get("idEstatus")));
				dtoBitChe.setCausa(funciones.convertirCadenaInteger(gListBita.get(c).get("causa")));
				dtoBitChe.setBeneficiario(funciones.validarCadena(gListBita.get(c).get("beneficiario")));
				listBitChe.add(dtoBitChe);
			}
			
			mensaje = consultasBusiness.ejecutarRevividor(listRev, listBitChe);
			
		}catch(Exception e){
			mensaje+="";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasAction, M:ejecutarRevividor");
		}
		return mensaje;
	}
	
	/**
	 * Este m\u00e9todo realiza la cancelacion de movimientos
	 * @param sDatGrid
	 * @param sDatBitaChe
	 * @param sNomForma
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@DirectMethod
	public Map cancelarMovimientos(String sDatGrid, String sDatBitaChe, String sNomForma){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		Map<String, Object> mapRet = new HashMap<String, Object>();
		Gson gSon = new Gson();
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			List<Map<String, String>> gListDatos = gSon.fromJson(sDatGrid, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			List<Map<String, String>> gListBita = gSon.fromJson(sDatBitaChe, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			List<RevividorDto> listRev = new ArrayList<RevividorDto>();
			List<BitacoraChequesDto> listBitChe = new ArrayList<BitacoraChequesDto>();
			
			for(int i = 0; i < gListDatos.size(); i ++)
			{
				RevividorDto dtoRev = new RevividorDto();
		        	dtoRev.setNomForma(sNomForma);
					dtoRev.setPsRevividor(funciones.validarCadena(gListDatos.get(i).get("psRevividor").toString()));
					dtoRev.setNoFolioDet(funciones.convertirCadenaInteger(gListDatos.get(i).get("noFolioDet").toString()));
					dtoRev.setIdTipoOperacion(funciones.convertirCadenaInteger(gListDatos.get(i).get("idTipoOperacion").toString()));
					dtoRev.setPsTipoCancelacion(funciones.validarCadena(gListDatos.get(i).get("psTipoCancelacion").toString()));
					dtoRev.setIdEstatusMov(funciones.validarCadena(gListDatos.get(i).get("idEstatusMov")));
					dtoRev.setPsOrigenMov(funciones.validarCadena(gListDatos.get(i).get("psOrigenMov")).equals("CXP") ? "SOI" : funciones.validarCadena(gListDatos.get(i).get("psOrigenMov")));
					dtoRev.setIdFormaPago(funciones.convertirCadenaInteger(gListDatos.get(i).get("idFormaPago").toString()));
					dtoRev.setBEntregado(funciones.validarCadena(gListDatos.get(i).get("bEntregado")));
					dtoRev.setIdTipoMovto(funciones.validarCadena(gListDatos.get(i).get("idTipoMovto")));
					dtoRev.setImporte(funciones.convertirCadenaDouble(gListDatos.get(i).get("importe")));
					dtoRev.setNoEmpresa(funciones.convertirCadenaInteger(gListDatos.get(i).get("noEmpresa").toString()));
					dtoRev.setNoCuenta(funciones.convertirCadenaInteger(gListDatos.get(i).get("noCuenta").toString()));
					dtoRev.setIdChequera(funciones.validarCadena(gListDatos.get(i).get("idChequera").toString()));
					dtoRev.setIdBanco(funciones.convertirCadenaInteger(gListDatos.get(i).get("idBanco").toString()));
					dtoRev.setNoDocumento(funciones.validarCadena(gListDatos.get(i).get("noDocto").toString()));
					dtoRev.setLote(funciones.convertirCadenaInteger(gListDatos.get(i).get("iLote").toString()));
					dtoRev.setBSalvoBuenCobro(funciones.validarCadena(gListDatos.get(i).get("bSalvoBuenCobro").toString()));
					dtoRev.setFecConfTrans(funciones.validarCadena(gListDatos.get(i).get("fecConfTrans").toString()));
					dtoRev.setIdDivisa(funciones.validarCadena(gListDatos.get(i).get("idDivisa")));
					dtoRev.setNoFolioRef(funciones.convertirCadenaInteger(gListDatos.get(i).get("noFolioRef").toString()));
					
				listRev.add(dtoRev);
			}
			
			for(int c = 0; c < gListBita.size(); c = c + 1)
			{
				BitacoraChequesDto dtoBitChe = new BitacoraChequesDto();
				
				dtoBitChe.setNoFolioDet(funciones.convertirCadenaInteger(gListBita.get(c).get("noFolioDet")));
				dtoBitChe.setNoEmpresa(funciones.convertirCadenaInteger(gListBita.get(c).get("noEmpresa"))); 
				dtoBitChe.setIdBanco(funciones.convertirCadenaInteger(gListBita.get(c).get("idBanco")));
				dtoBitChe.setIdChequera(funciones.validarCadena(gListBita.get(c).get("idChequera")));
				dtoBitChe.setNoCheque(funciones.convertirCadenaInteger(gListBita.get(c).get("noCheque")));
				dtoBitChe.setIdEstatus(funciones.validarCadena(gListBita.get(c).get("idEstatus")));
				dtoBitChe.setCausa(funciones.convertirCadenaInteger(gListBita.get(c).get("causa")));
				 
				listBitChe.add(dtoBitChe);
			}
			
			mapRet = consultasBusiness.cancelarMovimientos(listRev, listBitChe);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasAction, M:cancelarMovimientos");
			e.printStackTrace();
		}
		return mapRet;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboProveedores(String prefijoProv, int iNoProveedor, int noEmpresa)
	{
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<LlenaComboGralDto> listProv = new ArrayList<LlenaComboGralDto>();
		try
		{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			listProv = consultasBusiness.obtenerProveedores(prefijoProv, iNoProveedor, noEmpresa);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasAction, M:llenarComboProveedores");
		}
		return listProv;
	}
	
	
	@DirectMethod
	public List<Map<String, Object>> realizarBusquedaMovto(int iNoEmpresa, String sNoDocto, String sPeriodo,
															String sIdProveedor, String sNoFactura)
	{
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		try
		{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			
			ParamZimpFactDto dto = new ParamZimpFactDto();
				dto.setNoEmpresa(iNoEmpresa);
				dto.setNoDocto(funciones.validarCadena(sNoDocto));
				dto.setPeriodo(funciones.validarCadena(sPeriodo));
				dto.setNoProveedor(funciones.validarCadena(sIdProveedor));
				dto.setFactura(funciones.validarCadena(sNoFactura));
			listMap = consultasBusiness.consultarMovto(dto);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasAction, M:consultarMovto");
		}
		return listMap;
	}
	
	/**
	 * M\u00e9todo para seguimiento
	 * @param iNoEmpresa
	 * @param sNoDocto
	 * @param sPeriodo
	 * @param sIdProveedor
	 * @param sNoFactura
	 * @param sFechaOperacion
	 * @param bZimpFact
	 * @return
	 */
	@DirectMethod
	public String realizarSeguimientoMovto(int iNoEmpresa, String sNoDocto, String sPeriodo,
															String sIdProveedor, String sNoFactura, 
															String sFechaOperacion, String sEstatus,
															String sCausa, int iFormaPago,
															int iNoBenef, String sEstatusComp, boolean bZimpFact)
	{
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		StringBuffer sbSeg = new StringBuffer();
		try
		{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			
			ParamBusSolicitudDto dto = new ParamBusSolicitudDto();
				dto.setNoEmpresa(iNoEmpresa);
				dto.setNoDocto(funciones.validarCadena(sNoDocto));
				dto.setPeriodo(funciones.validarCadena(sPeriodo));
				dto.setIdProveedor(funciones.validarCadena(sIdProveedor));
				dto.setFactura(funciones.validarCadena(sNoFactura));
				dto.setFechaOperacion(funciones.ponerFechaSola(sFechaOperacion));
				dto.setEstatus(funciones.validarCadena(sEstatus));
				dto.setSCausa(funciones.validarCadena(sCausa));
				dto.setFormaPago(iFormaPago);
				dto.setNoBenef(0);
				dto.setEstatusCompensa(sEstatusComp);
			sbSeg = consultasBusiness.realizarSeguimientoMovto(dto, bZimpFact);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasAction, M:realizarSeguimientoMovto");
		}
		return sbSeg.toString();
	}
	
	/**
	 * Este m\u00e9todo es utilizado en BuscarMovimiento.js,
	 * realiza el enunciado de donde se puede encontrar el 
	 * movimiento consultado.
	 * @param sEstatus
	 * @return
	 */
	@DirectMethod
	public String seguimientoPantallas(String sEstatus)
	{
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		StringBuffer sbSeg = new StringBuffer();
		try
		{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			sbSeg.append(consultasBusiness.seguimientoPantallas(sEstatus));
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasAction, M:seguimientoPantallas");
		}
		return sbSeg.toString();
	}
	
	@DirectMethod
	public List<LlenaComboChequeraDto> llenaComboChequera(int idBanco, int noEmpresa, String idDivisa) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<LlenaComboChequeraDto> list = new ArrayList<LlenaComboChequeraDto>();
		
		try {
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			list = consultasBusiness.llenaComboChequera(idBanco, noEmpresa, idDivisa);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasAction, M:llenaComboChequera");
		}
		return list;
	}
	
	@DirectMethod
	public List<SaldoChequeraDto> consultarSaldosCuentas(int noEmpresa, int idBanco, String idChequera, int idUsuario, String idDivisa)
	{
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<SaldoChequeraDto> listResult = new ArrayList<SaldoChequeraDto>();
		
		try
		{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			listResult = consultasBusiness.consultarSaldosCuentas(noEmpresa, idBanco, idChequera, idUsuario, idDivisa);
			
			//Se crean las graficas con los datos obtenidos en la consulta.
			if(listResult!=null && listResult.size()>0)
			{
				//Nombre de la grafica.
				String sName = noEmpresa+""+idBanco+idChequera;
				
				//Grafica: Chequeras por empresa
				Map<String, Double> mCheEmp = new HashMap<String, Double>();
				//Grafica: Chequeras por banco
				Map<String, Double> mCheBan = new HashMap<String, Double>();
				
				
				for(int i=0; i<listResult.size(); i++){
					SaldoChequeraDto saldoChequeraDto = listResult.get(i);
					
					if(mCheEmp.containsKey(saldoChequeraDto.getNomEmpresa())){
						double dImporte = mCheEmp.get(saldoChequeraDto.getNomEmpresa());
						mCheEmp.remove(saldoChequeraDto.getNomEmpresa());
						mCheEmp.put(saldoChequeraDto.getNomEmpresa(), (saldoChequeraDto.getSaldoFinal()+dImporte));
					}
					else {
						mCheEmp.put(saldoChequeraDto.getNomEmpresa(), saldoChequeraDto.getSaldoFinal());
					}
					
					if(mCheBan.containsKey(saldoChequeraDto.getDescBanco())){
						double dImporte = mCheBan.get(saldoChequeraDto.getDescBanco());
						mCheBan.remove(saldoChequeraDto.getDescBanco());
						mCheBan.put(saldoChequeraDto.getDescBanco(), (saldoChequeraDto.getSaldoFinal()+dImporte));
					}
					else {
						mCheBan.put(saldoChequeraDto.getDescBanco(), saldoChequeraDto.getSaldoFinal());
					}
				}
				
				CreacionGrafica cg = new CreacionGrafica();
				//Saldo-Empresa
				cg.crearGraficaPie   (idUsuario+"", "PosicionChequerasSaldoEmpresa"+sName, "Saldo de chequeras por Empresa", mCheEmp);
				cg.crearGraficaBarras(idUsuario+"", "PosicionChequerasSaldoEmpresa"+sName, "Saldo de chequeras por Empresa", "Importe", mCheEmp, true);
				cg.crearGraficaLineas(idUsuario+"", "PosicionChequerasSaldoEmpresa"+sName, "Saldo de chequeras por Empresa", "Importe", mCheEmp, true);
				//Saldo-Banco
				cg.crearGraficaPie   (idUsuario+"", "PosicionChequerasSaldoBanco"+sName, "Saldo de chequeras por Banco", mCheBan);
				cg.crearGraficaBarras(idUsuario+"", "PosicionChequerasSaldoBanco"+sName, "Saldo de chequeras por Banco", "Importe", mCheBan, true);
				cg.crearGraficaLineas(idUsuario+"", "PosicionChequerasSaldoBanco"+sName, "Saldo de chequeras por Banco", "Importe", mCheBan, true);
				//Fin graficas
			}
		}
		catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasAction, M:consultarSaldosCuentas");
		}
		return listResult;
	}
	
	@DirectMethod
	public String reconstruyeChequeraAction(int ntEmpresa, int ntBanco, String strChequera ){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		
		String strResp = "";
		
		try {
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			strResp = consultasBusiness.reconstruyeChequera(ntEmpresa, ntBanco, strChequera);
		}catch(Exception e) {
			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasAction, M:reconstruyeChequeraAction");
			
		}
		
		return strResp;
		
	}//End function reconstruyeChequeraAction
	
	@DirectMethod
	public String reconstruyeChequeraHistoricaAction(int ntEmpresa, int ntBanco, String strChequera, String strFecha ){

		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		String strResp = "";
		
		try {
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			strResp = consultasBusiness.reconstruyeChequeraHistotica(ntEmpresa, ntBanco, strChequera,strFecha);
		}catch(Exception e) {
			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasAction, M:reconstruyeChequeraAction");
			
		}
		
		return strResp;
		
	}//End function reconstruyeChequeraAction
	

	
	
	/**
	 * Este m\u00e9todo obtiene realiza el reporte de Pago de Propuestas
	 * @param datos
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@DirectMethod
	public JRDataSource reportePosicionChequeras(Map datos, ServletContext context) {
		JRDataSource jrDataSource = null;
		SaldoChequeraDto dtoIn = new SaldoChequeraDto();
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())&& Utilerias.tienePermiso(WebContextManager.get(), 77)) {
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness", context);
			
			dtoIn.setNoEmpresa(Integer.parseInt(datos.get("noEmpresa").toString()));
			dtoIn.setIdBanco(Integer.parseInt(datos.get("idBanco").toString()));
			dtoIn.setIdChequera(datos.get("idChequera").toString());
			dtoIn.setIdDivisa(datos.get("idDivisa").toString());
			
			jrDataSource = consultasBusiness.reportePosicionChequeras(dtoIn, Integer.parseInt(datos.get("noUsuario").toString()));
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasAction, M:reportePosicionChequeras");	
		}
		return jrDataSource;
	}
	
	@DirectMethod
	public String importaSaldos(){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		String strResp = "";
		
		try {
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			strResp = consultasBusiness.leerArchivoExcel();
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasAction, M:importaSaldos");
		}
		
		return strResp;
		
	}
	
	@DirectMethod
	public int validaFacultad(int idFacultad) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return -99999;
		int res = 0;
		
		try {
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			res = consultasBusiness.validaFacultad(idFacultad);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Traspasos, C: TraspasosAction, M: validaFacultad");
		}
		return res;
	}
	
	@DirectMethod
	public List<SaldoChequeraDto> obtenerUsuarios(){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<SaldoChequeraDto> datos = new ArrayList<SaldoChequeraDto>();
		
		try	{	
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			datos = consultasBusiness.obtenerUsuarios();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + " P:modconsulta, C:ConsultasAction, M:obtenerUsuarios");
		}
		return datos;
	}
	
	@DirectMethod
	public List<SaldoChequeraDto> obtenerBancos(int idUsuario, String idDivisa){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<SaldoChequeraDto> datos = new ArrayList<SaldoChequeraDto>();
		
		try	{	
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			datos = consultasBusiness.obtenerBancos(idUsuario, idDivisa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + " P:modconsulta, C:ConsultasAction, M:obtenerBancos");
		}
		return datos;
	}
	
	@DirectMethod
	public List<SaldoChequeraDto> obternerChequeras(int idBanco, int idUsuario, String idDivisa){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<SaldoChequeraDto> datos = new ArrayList<SaldoChequeraDto>();
		
		try	{	
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			datos = consultasBusiness.obternerChequeras(idBanco, idUsuario, idDivisa);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + " P:modconsulta, C:ConsultasAction, M:obtenerBancos");
		}
		return datos;
	}
	
	@DirectMethod
	public List<SaldoChequeraDto> obtenerNvoBancos(){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<SaldoChequeraDto> datos = new ArrayList<SaldoChequeraDto>();
		
		try	{	
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			datos = consultasBusiness.obtenerNvoBancos();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + " P:modconsulta, C:ConsultasAction, M:obtenerNvoBancos");
		}
		return datos;
	}
	
	@DirectMethod
	public String insertaNuevo(String datos) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		String resp = "";
		
		try	{
			resp = validaDatos(datos);
			if(!resp.equals("")) return resp;
			
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			resp = consultasBusiness.insertaNuevo(datos);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + " P:modconsulta, C:ConsultasAction, M:insertaNuevo");
		}
		return resp;
	}
	
	public String validaDatos(String params) {
		Gson gson = new Gson();
		List<Map<String, String>> datos = gson.fromJson(params, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		String resp = "";
		
		try	{
			if (Utilerias.haveSession(WebContextManager.get())) {
			if(datos.get(0).get("persona").equals(""))
				return "Seleccione un proveedor!!";
			else if(datos.get(0).get("noContrato").equals(""))
				return "Introduzca no. de contrato!!";
			else if(datos.get(0).get("descripcion").equals(""))
				return "Introduzca descripci\u00f2n!!";
			else if(datos.get(0).get("noPagos").equals(""))
				return "Introduzca no. pagos!!";
			else if(datos.get(0).get("montoOrig").equals(""))
				return "Introduzca monto original!!";
			else if(datos.get(0).get("montoPag").equals(""))
				return "Introduzca monto Pagado!!";
			else if(datos.get(0).get("montoAde").equals(""))
				return "Introduzca monto adeudo!!";
			else if(datos.get(0).get("fecIni").equals(""))
				return "Introduzca una fecha de inicio valida!!";
			else if(datos.get(0).get("fecFin").equals(""))
				return "Introduzca una fecha final valida!!";
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + " P:modconsulta, C:ConsultasAction, M:validaDatos");
		}
		return resp;
	}
	
	
	@DirectMethod
	public String insertaNuevoCP(String datos) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		String resp = "";
		
		try	{
			resp = validaDatosCP(datos);
			if(!resp.equals("")) return resp;
			
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			resp = consultasBusiness.insertaNuevoCP(datos);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + " P:modconsulta, C:ConsultasAction, M:insertaNuevo");
		}
		return resp;
	}
	
	public String validaDatosCP(String params) {
		Gson gson = new Gson();
		List<Map<String, String>> datos = gson.fromJson(params, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		String resp = "";
		
		try	{
			if (Utilerias.haveSession(WebContextManager.get())) {
			if(datos.get(0).get("idUsuario").equals(""))
				return "Seleccione un usuario!!";
			else if(datos.get(0).get("idDivisa").equals(""))
				return "Introduzca Divisa!!";
			else if(datos.get(0).get("idBanco").equals(""))
				return "Introduzca un banco!!";
			else if(datos.get(0).get("idChequera").equals(""))
				return "Introduzca no. chequera!!";
			else if(datos.get(0).get("clabe").equals(""))
				return "Introduzca descripcion cuenta!!";
			else if(datos.get(0).get("saldoIni").equals(""))
				return "Introduzca saldo inicial!!";
			else if(datos.get(0).get("saldoIng").equals(""))
				return "Introduzca Ingreso!!";
			else if(datos.get(0).get("saldoEgr").equals(""))
				return "Introduzca egreso!!";
			else if(datos.get(0).get("saldoFin").equals(""))
				return "Introduzca saldo Final!!";
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + " P:modconsulta, C:ConsultasAction, M:validaDatos");
		}
		return resp;
	}
	
	@DirectMethod
	public List<SaldoChequeraDto> obtenerRegistros(String params){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<SaldoChequeraDto> datos = new ArrayList<SaldoChequeraDto>();
		
		try	{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			datos = consultasBusiness.obtenerRegistros(params);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + " P:modconsulta, C:ConsultasAction, M:obtenerRegistros");
		}
		return datos;
	}
	
	@DirectMethod
	public List<SaldoChequeraDto> obtenerRegistrosCP(String params){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<SaldoChequeraDto> datos = new ArrayList<SaldoChequeraDto>();
		
		try	{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			datos = consultasBusiness.obtenerRegistrosCP(params);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + " P:modconsulta, C:ConsultasAction, M:obtenerRegistros");
		}
		return datos;
	}
	
	@DirectMethod
	public int obtenerFacultad(int idUsuario){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return -99999;
		int facu = 0;
		int result = 0;
		
		try	{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			
			switch(idUsuario){
				case 5: 
					facu = 166;
				break;
				case 6:
					facu = 167;
				break;
				case 15:
					facu = 168;
				break;
			}
			
			result = consultasBusiness.validaFacultad(facu);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + " P:modconsulta, C:ConsultasAction, M:obtenerRegistros");
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@DirectMethod
	public JRDataSource reporteCuentasPersonales(Map datos, ServletContext context) {
		JRDataSource jrDataSource = null;
		SaldoChequeraDto dtoIn = new SaldoChequeraDto();
		int idBanco;
		
		try {
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness", context);
			
			if(datos.get("idBanco").toString().equals(""))
				idBanco = 0;
			else
				idBanco = Integer.parseInt(datos.get("idBanco").toString());
			
			dtoIn.setIdUsuario(Integer.parseInt(datos.get("idUsuario").toString()));
			dtoIn.setIdBanco(idBanco);
			dtoIn.setIdChequera(datos.get("idChequera").toString());
			dtoIn.setIdDivisa(datos.get("idDivisa").toString());
			
			jrDataSource = consultasBusiness.reporteCuentasPersonales(dtoIn);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasAction, M:reportePosicionChequeras");	
		}
		return jrDataSource;
	}
	
	@SuppressWarnings("unchecked")
	@DirectMethod
	public JRDataSource reporteContratosProveedores(Map datos, ServletContext context) {
		JRDataSource jrDataSource = null;
		SaldoChequeraDto dtoIn = new SaldoChequeraDto();
		
		try {
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness", context);
			
			dtoIn.setNoEmpresa(Integer.parseInt(datos.get("empresa").toString()));
			if(datos.get("idUsuario").toString().equals("")){
				dtoIn.setIdUsuario(Integer.parseInt("0"));
			}else{
				dtoIn.setIdUsuario(Integer.parseInt(datos.get("idUsuario").toString()));
			}
			
			if(datos.get("contrato").toString().equals("")){
				dtoIn.setIdContrato(Integer.parseInt("0"));
			}else{
				dtoIn.setIdContrato(Integer.parseInt(datos.get("contrato").toString()));
			}
			
			
			jrDataSource = consultasBusiness.reporteContratosProveedores(dtoIn);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasAction, M:reportePosicionChequeras");	
		}
		return jrDataSource;
	}
	@DirectMethod
	public List<LlenaComboGralDto>consultarProveedores(String texto){
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get()))
			return list;
		
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			list = consultasBusiness.consultarProveedores(texto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Cosultas, C:ConsultasAction, M:consultarProveedores");
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboBeneficiario(String campoUno, String campoDos, String tabla, String condicion, String orden, boolean regUnico){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get()))
			return list;
		
		LlenaComboGralDto dto= new LlenaComboGralDto();

		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			dto.setCampoUno(campoUno!=null && !campoUno.equals("")?campoUno:"");
			dto.setCampoDos(campoDos!=null && !campoDos.equals("")?campoDos:"");
			dto.setTabla(tabla!=null && !tabla.equals("")?tabla:"");
			dto.setCondicion(condicion!=null && !condicion.equals("")?condicion:"");
			dto.setOrden(orden!=null && !orden.equals("")?orden:"");
			dto.setRegistroUnico(regUnico);
			list = consultasBusiness.llenarComboBeneficiario(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Consultas, C:ConsultasAction, M:llenarComboBeneficiario");	
		}
		return list;
	}
	
	@DirectMethod
	public List<SaldoChequeraDto> obtenerProveedor(int noEmpresa){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<SaldoChequeraDto> datos = new ArrayList<SaldoChequeraDto>();
		
		try	{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			datos = consultasBusiness.obtenerProveedor(noEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + " P:modconsulta, C:ConsultasAction, M:obtenerProveedor");
		}
		return datos;
	}
	
	@DirectMethod
	public List<SaldoChequeraDto> obtenerContratos(int idUsuario){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<SaldoChequeraDto> datos = new ArrayList<SaldoChequeraDto>();
		
		try	{	
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			datos = consultasBusiness.obtenerContratos(idUsuario);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + " P:modconsulta, C:ConsultasAction, M:obtenerContratos");
		}
		return datos;
	}
	
	@DirectMethod
	public int eliminarContrato(int registro){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return -999999;
		int resp = registro;
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			resp = consultasBusiness.eliminarContrato(resp);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + " P:modconsulta, C:ConsultasAction, M:eliminarContrato");
		}
		return resp;
	}
	
	@DirectMethod
	public int eliminarCP(int banco,String chequera){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return -9999;
		int resp = 0;
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			resp = consultasBusiness.eliminarCP(banco, chequera);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + " P:modconsulta, C:ConsultasAction, M:eliminarContrato");
		}
		return resp;
	}

	//09 DE FEBRERO DEL 2016
	
	public HSSFWorkbook reporteFondeo(String fInicio,String fFin , String bandera, String usuario, ServletContext context){
		System.out.println(bandera);
		HSSFWorkbook wb=null;
		try {
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness",context);
			if(!bandera.equals("true"))
				wb=consultasBusiness.reporteFondeo(fInicio,fFin, usuario);
			else
				wb=consultasBusiness.reporteFondeoDetalle(fInicio, fFin, usuario);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + " P:modconsulta, C:ConsultasAction, M:reporteFondeo");
		}return wb;
	}

	
	public HSSFWorkbook reporteCXP(String noEmpresa, String fecIni , String fecFin, String estatus, String tipoReporte,ServletContext context){
		HSSFWorkbook wb=null;
		try {
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness",context);
			
			if(tipoReporte.equals("0")){
				wb=consultasBusiness.reporteCXP(noEmpresa, fecIni, fecFin, estatus);
			}else if(tipoReporte.equals("1")){
				wb=consultasBusiness.reporteDatosbancarios(noEmpresa, fecIni, fecFin, estatus);
			}else if(tipoReporte.equals("2")){
				wb=consultasBusiness.reporteCXC(noEmpresa, fecIni, fecFin, estatus);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + " P:modconsulta, C:ConsultasAction, M:reporteCXP");
		}return wb;
	}
	
	public HSSFWorkbook obtenerDatosExcelReporteSaldosS(String idEmpresa, String nomEmpresa , String idBancoInf, String idBancoSup, String idChequera, String tipoChequera, String estatusTipoEmpresa, String usuario, String empresas,ServletContext context){
		HSSFWorkbook wb=null;
		try {
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness",context);
			wb=consultasBusiness.obtenerDatosExcelReporteSaldosS(idEmpresa, nomEmpresa , idBancoInf, idBancoSup, idChequera, tipoChequera, estatusTipoEmpresa, usuario, empresas);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + " P:modconsulta, C:ConsultasAction, M:obtenerDatosExcelReporteSaldosS");
		}return wb;
	}
	//Agregado el 08.04.16
	//AA
	@DirectMethod
	public String retornaUsuario(){
		String resultado = "";
		try {
			if (!Utilerias.haveSession(WebContextManager.get()) ) 
				return null;
			globalSingleton = GlobalSingleton.getInstancia();
			resultado = globalSingleton.getUsuarioLoginDto().getIdUsuario()+"";
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ " P:modconsulta, C:ConsultasAction, M:retornaUsuario");
		}
		return resultado+""; 
	}
	
	/**********************
	 * Metodo para validar movimientos antes de descompensar
	 * con tipo de operacion 3200 y estatus T.
	 * 21.04.2016
	 */
	@DirectMethod
	public String validar3200(String movimiento){
		String resultado = "";
		try{
			if (!Utilerias.haveSession(WebContextManager.get())) 
				return "";
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			resultado=consultasBusiness.validar3200(movimiento);
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ " P:modconsulta, C:ConsultasAction, M:validar3200");
		}
		return resultado; 
	}
	
	@SuppressWarnings("unchecked")
	@DirectMethod
	public JRDataSource obtenerDatosReporteSaldosS(Map<String, Object> parametros, ServletContext context){
		JRDataSource jrDataSource = null;
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness", context);
			jrDataSource = consultasBusiness.obtenerDatosReporteSaldosS(parametros);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasAction, M:obtenerDatosReporteSaldosS");
		}
		return jrDataSource;
	}
	
	/**
	 * Este m\u00e9todo obtiene realiza el reporte de Pago de Propuestas
	 * @param datos
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> reporteChequeraDetalleMov(Map datos, ServletContext context) {
		ReportesChequeraDto dtoIn = new ReportesChequeraDto();
		List<Map<String, Object>> lista = null;
		try {
			if (contexto == null)
				bitacora.insertarRegistro("P:modconsulta, C:ConsultasAction, M:reporteChequeraDetalleMov" + " contexto nulo");
			
			dtoIn.setNoEmpresa(Integer.parseInt(datos.get("noEmpresa").toString()));
			dtoIn.setIdBanco(Integer.parseInt(datos.get("idBanco").toString()));
			dtoIn.setIdChequera(datos.get("idChequera").toString());
			dtoIn.setsFechaDesde(datos.get("txtFechaDesde").toString());
			dtoIn.setsFechaHasta(datos.get("txtFechaHasta").toString());
			
			System.out.println("Dto "+ dtoIn);
			
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness", context);
			lista = consultasBusiness.reporteChequeraDetalleMov(dtoIn);
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasAction, M:reportePosicionChequeras");	
		}
		return lista;
	}
	
	
	/**
	 * Este m\u00e9todo obtiene realiza el reporte de Pago de Propuestas
	 * @param datos
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> reporteChequeraMovimientos(Map datos, ServletContext context) {
		ReportesChequeraDto dtoIn = new ReportesChequeraDto();
		List<Map<String, Object>> lista = null;
		try {
			if (contexto == null)
				bitacora.insertarRegistro("P:modconsulta, C:ConsultasAction, M:reporteChequeraDetalleMov" + " contexto nulo");
			
			dtoIn.setNoEmpresa(Integer.parseInt(datos.get("noEmpresa").toString()));
			dtoIn.setIdBanco(Integer.parseInt(datos.get("idBanco").toString()));
			dtoIn.setIdChequera(datos.get("idChequera").toString());
			dtoIn.setsFechaDesde(datos.get("txtFechaDesde").toString());
			
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness", context);
			lista = consultasBusiness.reporteChequeraMovimientos(dtoIn);
		}
		catch(Exception e){
			logger.error(e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasAction, M:reportePosicionChequeras");	
		}
		return lista;
	}
	
	@DirectMethod
	public List<SaldoChequeraDto> obtenerSaldoInicialBanco(int noEmpresa, int idBanco, String descBanco, String idChequera){
		DatosChequeraDto datosChequeraDto = null;
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<SaldoChequeraDto> saldoChequeraDto = null;
		if( idChequera.equals("") ||
			noEmpresa < 1 || idBanco < 1){
			return saldoChequeraDto;
		}
		try{
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			datosChequeraDto = new DatosChequeraDto();
			datosChequeraDto.setDescBanco(descBanco);
			datosChequeraDto.setIdChequera(idChequera);
			datosChequeraDto.setNoEmpresa(noEmpresa);
			datosChequeraDto.setIdBanco(idBanco);
			saldoChequeraDto = consultasBusiness.obtenerSaldoInicialBanco(datosChequeraDto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modconsulta, C:ConsultasAction, M:seleccionarDatosChequera");
		}
		return saldoChequeraDto;
	}
	@DirectMethod
	public String leerRuta(int folio) {
		String resultado = "";
			
		//if(!Utilerias.haveSession(WebContextManager.get()) || !(Utilerias.tienePermiso(WebContextManager.get(), 109)||Utilerias.tienePermiso(WebContextManager.get(), 110)))
		//	return resultado;
		
		try {
			consultasBusiness = (ConsultasBusiness)contexto.obtenerBean("consultasBusiness");
			resultado=consultasBusiness.leerRuta(folio);
			System.out.println(resultado+"::..Resultado");
		}
		catch(Exception e) {
			e.printStackTrace();
			logger.error(e);
			//bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:ConsultasMovimiento, C:CosultasAction, M: LeerRuta");
		}
		return resultado;
	}
}
