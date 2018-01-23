package com.webset.set.prestamosinterempresas.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.prestamosinterempresas.dto.ParamComunDto;
import com.webset.set.prestamosinterempresas.dto.ParamInteresPresNoDoc;
import com.webset.set.prestamosinterempresas.dto.RetInteresPresNoDoc;
import com.webset.set.prestamosinterempresas.dto.SolTraspasoCreditoDto;
import com.webset.set.prestamosinterempresas.service.PrestamosInterempresasService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.ParametroDto;
import com.webset.utils.tools.Utilerias;

import net.sf.jasperreports.engine.JRDataSource;

public class PrestamosInterempresasAction {
	Contexto contexto = new Contexto();
	Bitacora bitacora = new Bitacora();
	Funciones funciones = new Funciones();
	
	@DirectMethod 
	public List<LlenaComboGralDto> llenarCmbEmpresaRaiz(boolean bExistentes){
		List<LlenaComboGralDto> listEmpRaiz = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listEmpRaiz;
		try{																										   
			PrestamosInterempresasService prestamosService = (PrestamosInterempresasService) contexto.obtenerBean("prestamosInterempresasBusinessImpl");
			listEmpRaiz = prestamosService.obtenerEmpresasRaiz(bExistentes);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasAction M: llenarCmbEmpresaRaiz");
		}
		return listEmpRaiz;
	}
	
	@DirectMethod 
	public List<LlenaComboGralDto> llenarCmbEmpresasHijo(int iEmpRaiz){
		List<LlenaComboGralDto> listEmpHijo = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listEmpHijo;
		try{																										   
			PrestamosInterempresasService prestamosService = (PrestamosInterempresasService) contexto.obtenerBean("prestamosInterempresasBusinessImpl");
			listEmpHijo = prestamosService.obtenerEmpresasHijo(iEmpRaiz);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasAction M: llenarCmbEmpresasHijo");
		}return listEmpHijo;
	}
	
	@DirectMethod 
	public String obtenerArbolEmpresa(int iEmpresaRaiz){
		String struc = "";
		if(!Utilerias.haveSession(WebContextManager.get()))
			return struc;
		try{																										   
			PrestamosInterempresasService prestamosService = (PrestamosInterempresasService) contexto.obtenerBean("prestamosInterempresasBusinessImpl");
			struc = prestamosService.obtenerArbolEmpresa(iEmpresaRaiz);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasAction M: obtenerArbolEmpresa");
		}return struc;
	}
	
	@DirectMethod
	public JRDataSource obtenerReporteArbolEmpresa(ServletContext context){
		JRDataSource jrDataSource = null;
		try{
			PrestamosInterempresasService prestamosService = (PrestamosInterempresasService) contexto.obtenerBean("prestamosInterempresasBusinessImpl", context);
			jrDataSource = prestamosService.obtenerReporteArbolEmpresa();
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasAction M: obtenerReporteArbolEmpresa");
		}return jrDataSource;
	}
	
	/**
	 * M�todo para agregar nuevos nodos a un arbol,
	 * o para crear un nuevo arbol, dependiendo de la bandera bNvoHijo, 
	 * si es verdadera, se agregar� un nodo, de lo contrario un nuevo arbol.
	 * @param bNvoHijo
	 * @param sRuta
	 * @param iIdEmpresa
	 * @param uMonto
	 * @return
	 */
	@DirectMethod
	public String agregarNodosArbol(boolean bNvoHijo, String sRuta, int iIdEmpresaRaiz, int iIdEmpresa, double uMonto){
		String sMsgUsuario = "";
		if(!Utilerias.haveSession(WebContextManager.get()))
			return sMsgUsuario;
		try{
			PrestamosInterempresasService prestamosService = (PrestamosInterempresasService) contexto.obtenerBean("prestamosInterempresasBusinessImpl");
			sMsgUsuario = prestamosService.agregarNodosArbol(bNvoHijo, sRuta, iIdEmpresaRaiz, iIdEmpresa, uMonto); 
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasAction M: agregarNodosArbol");
		}return sMsgUsuario;
	}
	
	@DirectMethod
	public String eliminarNodosArbol(int iIdEmpresaRaiz, int iIdEmpresaActual, int iIdEmpresaPadre){
		String sMsgUsuario = "";
		if(!Utilerias.haveSession(WebContextManager.get()))
			return sMsgUsuario;
		try{
			PrestamosInterempresasService prestamosService = (PrestamosInterempresasService) contexto.obtenerBean("prestamosInterempresasBusinessImpl");
			sMsgUsuario = prestamosService.eliminarNodosArbol(iIdEmpresaRaiz, iIdEmpresaActual, iIdEmpresaPadre);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasAction M: eliminarNodosArbol");
		}return sMsgUsuario;
	}
	
	/**
	 * Inician llamada de m�todos para la forma SolicitudDeTraspasosDeCredito 
	 * M�todo para obtener la empresa concentradora
	 * @return
	 */
	@DirectMethod 
	public List<LlenaComboGralDto> llenarCmbEmpCon(){
		List<LlenaComboGralDto> listEmpCon = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listEmpCon;
		try	{																										   
			PrestamosInterempresasService prestamosService = (PrestamosInterempresasService) contexto.obtenerBean("prestamosInterempresasBusinessImpl");
			listEmpCon = prestamosService.obtenerEmpresaConcentradora();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasAction M: llenarCmbEmpCon");
		}return listEmpCon;
	}
	
	@DirectMethod 
	public List<LlenaComboGralDto> llenarCmbBancosEmp(int iIdEmpresa, int iIdBanco, String sIdCheqD){
		List<LlenaComboGralDto> listBan = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listBan;
		try{																										   
			PrestamosInterempresasService prestamosService = (PrestamosInterempresasService) contexto.obtenerBean("prestamosInterempresasBusinessImpl");
			listBan = prestamosService.obtenerBancosEmpresa(iIdEmpresa, iIdBanco, sIdCheqD);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasAction M: llenarCmbBancosEmp");
		}return listBan;
	}
	
	@DirectMethod 
	public List<LlenaComboGralDto> llenarCmbEmpresaDestino(int iIdEmpresa){
		List<LlenaComboGralDto> listEmpD = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listEmpD;
		try{																										   
			PrestamosInterempresasService prestamosService = (PrestamosInterempresasService) contexto.obtenerBean("prestamosInterempresasBusinessImpl");
			listEmpD = prestamosService.obtenerEmpresasDestino(iIdEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasAction M: llenarCmbEmpresaDestino");
		}return listEmpD;
	}
	
	/**
	 * M�todo para llenar el combo de chequeras origen y destino.
	 * @param iIdEmpresa
	 * @param iIdBancoD
	 * @param sIdCheqD
	 * @return
	 */
	@DirectMethod 
	public List<LlenaComboGralDto> llenarCmbCheqOrigenDes(int iIdEmpresa, int iIdBancoD, String sIdCheqD, int iIdBancoA){
		List<LlenaComboGralDto> listCheq = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listCheq;
		try{																										   
			PrestamosInterempresasService prestamosService = (PrestamosInterempresasService) contexto.obtenerBean("prestamosInterempresasBusinessImpl");
			listCheq = prestamosService.obtenerChequeras(iIdEmpresa, iIdBancoD, sIdCheqD, iIdBancoA);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasAction M: llenarCmbCheqOrigenDes");
		}return listCheq;
	}
	
	@DirectMethod 
	public double obtenerSaldoFinal(int iIdEmpresa, int iIdBanco, String sIdChequera){
		double uSaldoFinal = 0;
		if(!Utilerias.haveSession(WebContextManager.get()))
			return uSaldoFinal;
		try{
			PrestamosInterempresasService prestamosService = (PrestamosInterempresasService) contexto.obtenerBean("prestamosInterempresasBusinessImpl");
			uSaldoFinal = prestamosService.obtenerSaldoFinal(iIdEmpresa, iIdBanco, sIdChequera);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasAction M: obtenerSaldoFinal");
		}return uSaldoFinal;
	}
	
	@DirectMethod
	public Map<String, Object> obtenerSaldoFinalYCoinversion(int iEmpresaOrigen, int iEmpresaDes,int iIdBancoA, String sIdChequeraA){
		Map<String, Object> mapSaldos = new HashMap<String, Object>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return mapSaldos;
		try{
			PrestamosInterempresasService prestamosService = (PrestamosInterempresasService) contexto.obtenerBean("prestamosInterempresasBusinessImpl");
			mapSaldos = prestamosService.obtenerSaldoFinalYCoinversion(iEmpresaOrigen, iEmpresaDes, iIdBancoA, sIdChequeraA);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasAction M: obtenerSaldoFinalYCoinversion");
		}
		return mapSaldos;
	}
	
	/**
	 * M�todo para realizar la solicitud de traspaso de credito,
	 * utilizado en SolicitudDeTraspasosDeCredito.js
	 * @param dto
	 * @return
	 */
	@DirectMethod
	public Map<String, Object> realizarSolTraspCred(int empresaOrigen,int empresaDestino,
													int bancoOrigen, int bancoDestino,
													String cheqOrigen, String cheqDestino,
													double monto, String concepto)
	{
		Map<String, Object> mapTrasp = new HashMap<String, Object>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return mapTrasp;
		try{
			PrestamosInterempresasService prestamosService = (PrestamosInterempresasService) contexto.obtenerBean("prestamosInterempresasBusinessImpl");
			SolTraspasoCreditoDto dto = new SolTraspasoCreditoDto(); 
				dto.setEmpresaOrigen(funciones.validarEntero(empresaOrigen));
				dto.setEmpresaDestino(funciones.validarEntero(empresaDestino));
				dto.setBancoOrigen(funciones.validarEntero(bancoOrigen));
				dto.setBancoDestino(funciones.validarEntero(bancoDestino));
				dto.setMonto(monto);
				dto.setCheqOrigen(funciones.validarCadena(cheqOrigen));
				dto.setCheqDestino(funciones.validarCadena(cheqDestino));
				dto.setConcepto(funciones.validarCadena(concepto));
			mapTrasp = prestamosService.realizarSolTraspCred(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasAction M: realizarSolTraspCred");
		}
		return mapTrasp;
	}
	
	@DirectMethod
	public Map<String, Object> realizarPagoTraspCred(int empresaOrigen,int empresaDestino,
													int bancoOrigen, int bancoDestino,
													String cheqOrigen, String cheqDestino,
													double monto, String concepto)
	{
		Map<String, Object> mapTrasp = new HashMap<String, Object>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return mapTrasp;
		try{
			PrestamosInterempresasService prestamosService = (PrestamosInterempresasService) contexto.obtenerBean("prestamosInterempresasBusinessImpl");
			SolTraspasoCreditoDto dto = new SolTraspasoCreditoDto(); 
				dto.setEmpresaOrigen(funciones.validarEntero(empresaOrigen));
				dto.setEmpresaDestino(funciones.validarEntero(empresaDestino));
				dto.setBancoOrigen(funciones.validarEntero(bancoOrigen));
				dto.setBancoDestino(funciones.validarEntero(bancoDestino));
				dto.setMonto(monto);
				dto.setCheqOrigen(funciones.validarCadena(cheqOrigen));
				dto.setCheqDestino(funciones.validarCadena(cheqDestino));
				dto.setConcepto(funciones.validarCadena(concepto));
			mapTrasp = prestamosService.realizarPagoTraspCred(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasAction M: realizarPagoTraspCred");
		}
		return mapTrasp;
	}
	
	@DirectMethod 
	public List<LlenaComboGralDto> llenarCmbEmpresasArbol(){
		List<LlenaComboGralDto> listEmp = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listEmp;
		try{																										   
			PrestamosInterempresasService prestamosService = (PrestamosInterempresasService) contexto.obtenerBean("prestamosInterempresasBusinessImpl");
			listEmp = prestamosService.obtenerEmpresasArbol();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasAction M: llenarCmbEmpresasArbol");
		}
		return listEmp;
	}
	
	/**
	 * Inician llamadas a m�todos que se utilizan en CalculoDeInteres
	 * @return
	 */
	@DirectMethod 
	public List<RetInteresPresNoDoc> obtenerConsultaInteresPresNoDoc(String sFecIni, String sFecFin, int iPlazo,
																		double uTasa, String sDivisa)
	{
		List<RetInteresPresNoDoc> listCons = new ArrayList<RetInteresPresNoDoc>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listCons;
		try{																										   
			PrestamosInterempresasService prestamosService = (PrestamosInterempresasService) contexto.obtenerBean("prestamosInterempresasBusinessImpl");
			
			ParamInteresPresNoDoc dtoParam = new ParamInteresPresNoDoc();
				dtoParam.setFecIni(funciones.ponerFechaDate(sFecIni));
				dtoParam.setFecFin(funciones.ponerFechaDate(sFecFin));
				dtoParam.setPlazo(funciones.validarEntero(iPlazo));
				dtoParam.setTasa(uTasa);
				dtoParam.setIdDivisa(funciones.validarCadena(sDivisa));
			listCons =  prestamosService.obtenerConsultaInteresPresNoDoc(dtoParam);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasAction M: obtenerConsultaInteresPresNoDoc");
		}
		return listCons;
	}
	
	@DirectMethod
	public ArrayList<String> ejecutarCalculoInteres(String sDatosGrid, String sFecIni, String sFecFin,double uTasa, String sIdDivisa){
		Gson gSon = new Gson();
		ArrayList<String> sEjecutarCalInt = new ArrayList<String>();
		List<ParametroDto> listParam = new ArrayList<ParametroDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return sEjecutarCalInt;
		try{
			PrestamosInterempresasService prestamosService = (PrestamosInterempresasService) contexto.obtenerBean("prestamosInterempresasBusinessImpl");
			
			List<Map<String, String>> gListGrid = gSon.fromJson(sDatosGrid, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			for(int i = 0; i < gListGrid.size(); i ++){
				ParametroDto dto = new ParametroDto();
					dto.setIva(funciones.convertirCadenaDouble(gListGrid.get(i).get("iva").toString()));
					dto.setNoEmpresa(funciones.convertirCadenaInteger(gListGrid.get(i).get("noEmpresa").toString()));
					dto.setNomEmpresa(funciones.validarCadena(gListGrid.get(i).get("nomEmpresa").toString()));
					dto.setNoCuenta(funciones.convertirCadenaInteger(gListGrid.get(i).get("noCuentaEmp").toString()));
					//arrayReg.noLineaEmp = regSelecGrid[i].get('noLineaEmp');
					//arrayReg.idDivisaSoin = regSelecGrid[i].get('idDivisaSoin');
					dto.setIdDivisa(funciones.validarCadena(gListGrid.get(i).get("idDivisa").toString()));
					dto.setSaldoPromedio(funciones.convertirCadenaDouble(gListGrid.get(i).get("saldoPromedio").toString()));
					dto.setImporte(funciones.convertirCadenaDouble(gListGrid.get(i).get("interesesPorPagar").toString()));
					dto.setNoCliente(funciones.validarCadena(gListGrid.get(i).get("noEmpBenef").toString()));
					dto.setNomEmpresaBenef(funciones.validarCadena(gListGrid.get(i).get("nomEmpBenef").toString()));
					dto.setIdChequeraBenef(funciones.validarCadena(gListGrid.get(i).get("cheqBenef").toString()));
					dto.setIdBancoBenef(funciones.convertirCadenaInteger(gListGrid.get(i).get("bancoBenef").toString()));
					//arrayReg.nomBancoBenef = regSelecGrid[i].get('nomBancoBenef');
					dto.setIdChequera(funciones.validarCadena(gListGrid.get(i).get("cheqOrigen").toString()));
					dto.setIdBanco(funciones.convertirCadenaInteger(gListGrid.get(i).get("bancoOrigen").toString()));
				listParam.add(dto);
			}
			
			ParamComunDto comunDto = new ParamComunDto();
				comunDto.setFecIni(funciones.ponerFechaDate(sFecIni));
				comunDto.setFecFin(funciones.ponerFechaDate(sFecFin));
				comunDto.setDivisa(funciones.validarCadena(sIdDivisa));
				comunDto.setTasa(uTasa);
			
			sEjecutarCalInt = prestamosService.ejecutarCalculoInteres(listParam, comunDto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasAction M: ejecutarCalculoInteres");
		}
		return sEjecutarCalInt;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> llenarCmbPeriodos(){
		List<LlenaComboGralDto> listPeri = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listPeri;
		try{
			PrestamosInterempresasService prestamosService = (PrestamosInterempresasService) contexto.obtenerBean("prestamosInterempresasBusinessImpl");
			listPeri = prestamosService.obtenerPeriodosPrestamos(); 
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasAction M: llenarCmbPeriodos");
		}
		return listPeri;
	}
	
	@DirectMethod
	public JRDataSource obtenerReportePrestamosNoDoc(Map<String,Object> parametros, ServletContext context)
	{
		JRDataSource jrDataSource = null;
		try
		{
			PrestamosInterempresasService prestamosService = (PrestamosInterempresasService) contexto.obtenerBean("prestamosInterempresasBusinessImpl", context);
			jrDataSource = prestamosService.obtenerReportePresNoDoc(parametros);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasAction M: obtenerReportePrestamosNoDoc");
		}
		return jrDataSource;
	}
	
	//Inician m�todos de ReporteDeInteresNeto
	@DirectMethod
	public List<LlenaComboGralDto> llenarCmbAnios()
	{
		List<LlenaComboGralDto> listAnios = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listAnios;
		try{
			PrestamosInterempresasService prestamosService = (PrestamosInterempresasService) contexto.obtenerBean("prestamosInterempresasBusinessImpl");
			listAnios = prestamosService.obtenerAnios();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasAction M: llenarCmbAnios");
		}
		return listAnios;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> llenarCmbMes(int iAnio){
		List<LlenaComboGralDto> listMes = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listMes;
		try{
			PrestamosInterempresasService prestamosService = (PrestamosInterempresasService) contexto.obtenerBean("prestamosInterempresasBusinessImpl");
			listMes = prestamosService.obtenerMes(iAnio);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasAction M: llenarCmbMes");
		}
		return listMes;
	}
	
	@DirectMethod
	public JRDataSource obtenerRepInteresNeto(Map<String,Object> params, ServletContext context){
		JRDataSource jrDataSource = null;
		try{
			PrestamosInterempresasService prestamosService = (PrestamosInterempresasService) contexto.obtenerBean("prestamosInterempresasBusinessImpl", context);
			jrDataSource = prestamosService.obtenerRepInteresNeto(params);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasAction M: obtenerRepInteresNeto");
		}
		return jrDataSource;
	}
	
	//Inician Llamadas a m�todos utilizados en FlujoNeto
	@DirectMethod
	public List<LlenaComboGralDto> llenarCmbSectores(){
		List<LlenaComboGralDto> listSec = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listSec;
		try{
			PrestamosInterempresasService prestamosService = (PrestamosInterempresasService) contexto.obtenerBean("prestamosInterempresasBusinessImpl");
			listSec = prestamosService.obtenerSectores();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasAction M: llenarCmbSectores");
		}
		return listSec;
	}
	
	@DirectMethod
	public List<Map<String, Object>> llenarGridFlujoNeto(int iIdEmpresa, String sIdDivisa, int iSector, 
													String sFecValor, boolean bCheckSector)
	{
		List<Map<String, Object>> listMapFlujo = new ArrayList<Map<String, Object>>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listMapFlujo;
		try{
			PrestamosInterempresasService prestamosService = (PrestamosInterempresasService) contexto.obtenerBean("prestamosInterempresasBusinessImpl");
			
			ParamComunDto dto = new ParamComunDto();
				dto.setIdEmpresa(iIdEmpresa);
				dto.setDivisa(funciones.validarCadena(sIdDivisa));
				dto.setSector(iSector);
				dto.setFecValor(funciones.ponerFechaDate(sFecValor));
				dto.setBSector(bCheckSector);
				
				listMapFlujo = prestamosService.obtenerFlujoNeto(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasAction M: llenarGridFlujoNeto");
		}
		return listMapFlujo;
	}
	
	@DirectMethod
	public JRDataSource obtenerReporteFlujoNeto(Map<String,Object> params, ServletContext context){
		JRDataSource jrDataSource = null;
		try
		{
			PrestamosInterempresasService prestamosService = (PrestamosInterempresasService) contexto.obtenerBean("prestamosInterempresasBusinessImpl", context);
	
			ParamComunDto dto = new ParamComunDto();
			dto.setIdEmpresa(funciones.convertirCadenaInteger(params.get("iIdEmpresa").toString()));
			dto.setDivisa(funciones.validarCadena(params.get("sIdDivisa").toString()));
			dto.setSector(funciones.convertirCadenaInteger(params.get("iSector").toString()));
			dto.setFecValor(funciones.ponerFechaDate(params.get("sFecValor").toString()));
			dto.setBSector(params.get("bCheckSector").toString().trim().equals("true"));
			
			jrDataSource = prestamosService.obtenerReporteFlujoNeto(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasAction M: obtenerReporteFlujoNeto");
		}
		return jrDataSource;
	}
	
	//Inician Llamadas a m�todos utilizados en EstadoDeCuentaCredito
	@DirectMethod
	public List<LlenaComboGralDto> llenarCmbArbolEmpresas(){
		List<LlenaComboGralDto> listEmp = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listEmp;
		try{
			PrestamosInterempresasService prestamosService = (PrestamosInterempresasService) contexto.obtenerBean("prestamosInterempresasBusinessImpl");
			listEmp = prestamosService.obtenerEmpresasArbolUsuario();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasAction M: llenarCmbArbolEmpresas");
		}
		return listEmp;
	}
	
	@DirectMethod
	public JRDataSource obtenerRepEstadoDeCuentaDeCredito(Map<String,Object> params, ServletContext context){
		JRDataSource jrDataSource = null;
		try{
			PrestamosInterempresasService prestamosService = (PrestamosInterempresasService) contexto.obtenerBean("prestamosInterempresasBusinessImpl", context);
			jrDataSource = prestamosService.obtenerReporteEstadoDeCuentDeCredito(params);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasAction M: obtenerRepEstadoDeCuentaDeCredito");
		}
		return jrDataSource;
	}
	
	//Inician llamadas de m�todos de ReporteDeSolicitudes
	@DirectMethod
	public JRDataSource obtenerReporteDeSolicitudes(Map<String,Object> params, ServletContext context){
		JRDataSource jrDataSource = null;
		try{
			PrestamosInterempresasService prestamosService = (PrestamosInterempresasService) contexto.obtenerBean("prestamosInterempresasBusinessImpl", context);
			jrDataSource = prestamosService.obtenerReporteSolicitudesDeCredito(params);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas C: PrestamosInterempresasAction M: obtenerReporteDeSolicitudes");
		}
		return jrDataSource;
	}
	
}
