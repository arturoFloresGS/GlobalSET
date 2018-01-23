package com.webset.set.inversiones.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.ServletContext;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectFormPostMethod;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
//import com.google.gson.Gss.djn.config.annotations.DirectFormPostMethod;
import com.webset.set.egresos.dto.ConfirmacionCargoCtaDto;
import com.webset.set.graficas.CreacionGrafica;
import com.webset.set.inversiones.dto.BancoCheContratoDto;
import com.webset.set.inversiones.dto.ComunInversionesDto;
import com.webset.set.inversiones.dto.ConsultaOrdenInversionDto;
import com.webset.set.inversiones.dto.CtasContratoDto;
import com.webset.set.inversiones.dto.LiquidaInversionesDto;
import com.webset.set.inversiones.dto.MovimientoDto;
import com.webset.set.inversiones.dto.OrdenInversionDto;
import com.webset.set.inversiones.middleware.service.InversionesService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.CatCtaBancoDto;
import com.webset.set.utilerias.dto.CatPapelDto;
import com.webset.set.utilerias.dto.CatTipoValorDto;
import com.webset.set.utilerias.dto.CuentaDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.DatosExcel;
import com.webset.utils.tools.Utilerias;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
/**
 * Clase Action para el modulo de inversiones
 * @author Cristian Garcia Garcia
 *
 */
public class InversionesAction {
	private Bitacora bitacora = new Bitacora();
	private Contexto contexto = new Contexto();
	private GlobalSingleton globalSingleton;
	private Funciones funciones = new Funciones();
	private static Logger logger = Logger.getLogger(InversionesAction.class);

	
	/**
	 * Este metodo llena el combo de banco en
	 * el formulario mantenimiento de contratos
	 * @param iInstitucion
	 * @return
	 */
	@DirectMethod
	public List<LlenaComboGralDto> llenarCombosBancosDep(int iInstitucion, String sIdDivisa, int noEmpresa){
		List<LlenaComboGralDto> listBanc = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listBanc;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			listBanc = inversionesService.llenarCombosBancosDep(iInstitucion, sIdDivisa, noEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:llenarCombosBancosDep");
		}
		return listBanc;
	}
	
	@DirectMethod
	public List<ConfirmacionCargoCtaDto> llenarComboInstitucion(){
		List<ConfirmacionCargoCtaDto> listInst = new ArrayList<ConfirmacionCargoCtaDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listInst;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			listInst = inversionesService.llenarComboInstitucion();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:llenarComboInstitucion");
		}
		return listInst;
	}
	@DirectMethod
	public List<CuentaDto> consultarContratos(int noEmpresa){
		List<CuentaDto> listConsCon = new ArrayList<CuentaDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listConsCon;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			listConsCon = inversionesService.consultarContratos(noEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:consultarContratos");
		}
		return listConsCon;
	}
	
	/**
	 * Metodo para obtener valores del configura set
	 * con el objetivo que sea llamado solo del js
	 * para determinar si se agregan o no componentes
	 * @param iIndice
	 * @return
	 */
	@DirectMethod
	public String obtenerValorConfiguraSet(int iIndice){
		String resultado="";
		if(!Utilerias.haveSession(WebContextManager.get()))
			return resultado;
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			resultado= globalSingleton.obtenerValorConfiguraSet(iIndice);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:obtenerValorConfiguraSet");
		}return resultado;
	}
	
	/**
	 * Metodo utilizado para obtener el nombre de contactos,
	 * es utilizado en MantenimientoDeContratos
	 * @param iContacto1
	 * @param iContacto2
	 * @param iContacto3
	 * @return
	 */
	@DirectMethod
	public List<LlenaComboGralDto> obtenerContactos(int iContacto1, int iContacto2, int iContacto3){
		List<LlenaComboGralDto> listCont = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listCont;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			listCont = inversionesService.obtenerNombresContactos(iContacto1, iContacto2, iContacto3);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:obtenerContactos");
		}
		return listCont;
	}
	
	@DirectMethod
	public List<BancoCheContratoDto> llenarGridBancosContrato(int iNoCuenta, int noEmpresa){
		List<BancoCheContratoDto> listBanChe = new ArrayList<BancoCheContratoDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listBanChe;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			listBanChe = inversionesService.llenarGridBancosContrato(iNoCuenta, noEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:llenarGridBancosContrato");
		}
		return listBanChe;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> consultarContactosInstitucion(int iInstitucion){
		List<LlenaComboGralDto> listContac = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listContac;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			listContac = inversionesService.consultarContactosInstitucion(iInstitucion);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:consultarContactosInstitucion");
		}
		return listContac;
	}
	
	/**
	 * Este metodo obtiene las chequeras asignadas a un banco, empresa e institucion,
	 * es utilizado en MantenimientoDeContratos y LiquidacionDeInversiones
	 * @param iIdBanco
	 * @param iIns si se quieren chequeras relacionadas con la institucion este debe ser > 0
	 * @return
	 */
	@DirectMethod
	public List<LlenaComboChequeraDto> obtenerChequeras(int iIdBanco, int iIns, String sIdDivisa, int noEmpresa){
		List<LlenaComboChequeraDto> listCheq = new ArrayList<LlenaComboChequeraDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listCheq;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			listCheq = inversionesService.obtenerChequeras(iIdBanco, iIns,sIdDivisa, noEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:consultarChequeras");
		}
		return listCheq;
	}
	
	@DirectMethod
	public Map<String, Object> insertarModificarContratos(boolean bNuevo, boolean bModifi, String datCuenta, String datCtasCont, int noEmpresa)
	{
		Map<String, Object> mapRet = new HashMap<String, Object>();
		Gson gson = new Gson();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return mapRet;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			List<Map<String, String>> gListCuenta = gson.fromJson(datCuenta, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			List<Map<String, String>> gListCtas = gson.fromJson(datCtasCont, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			List<CtasContratoDto> listCtasCont = new ArrayList<CtasContratoDto>();
			CuentaDto dtoCuenta = new CuentaDto();
			
			if(gListCuenta.size() > 0)
			{
				for(int c = 0; c < gListCuenta.size(); c ++)
				{
					dtoCuenta = new CuentaDto();
					dtoCuenta.setIdDivisa(funciones.validarCadena(gListCuenta.get(c).get("idDivisa").toString()));
					dtoCuenta.setNoCuenta(funciones.convertirCadenaInteger(gListCuenta.get(c).get("noCuenta").toString()));
					dtoCuenta.setPlazoInv(funciones.convertirCadenaInteger(gListCuenta.get(c).get("plazoInv").toString()));
					dtoCuenta.setDescCuenta(funciones.validarCadena(gListCuenta.get(c).get("descCuenta").toString()));
					dtoCuenta.setNoInstitucion(funciones.convertirCadenaInteger(gListCuenta.get(c).get("noInstitucion").toString()));
					dtoCuenta.setNoContacto1(funciones.convertirCadenaInteger(gListCuenta.get(c).get("noContacto1").toString()));
					dtoCuenta.setNoContacto2(funciones.convertirCadenaInteger(gListCuenta.get(c).get("noContacto2").toString()));
					dtoCuenta.setNoContacto3(Long.parseLong(gListCuenta.get(c).get("noContacto3").toString()));
					//dtoCuenta.setSocGl(funciones.validarCadena(gListCuenta.get(c).get("socGl").toString()));
					dtoCuenta.setSocGl    ( funciones.validarCadena( gListCuenta.get(c).get("socGl"    ) ) );					
					dtoCuenta.setSubCuenta( funciones.validarCadena( gListCuenta.get(c).get("subCuenta") ) );dtoCuenta.setContratoInstitucion(funciones.validarCadena(gListCuenta.get(c).get("contratoInstitucion").toString()));
					dtoCuenta.setBIsrBisiesto(funciones.validarCadena(gListCuenta.get(c).get("bIsrBisiesto").toString()));
					dtoCuenta.setPersonaAutoriza(funciones.validarCadena(gListCuenta.get(c).get("personaAutoriza").toString()));
					dtoCuenta.setCondicionAlt(funciones.validarCadena(gListCuenta.get(c).get("condicionAlt").toString()));
					dtoCuenta.setValorSalida( funciones.convertirCadenaDouble( gListCuenta.get(c).get( "idFormaCargo" ).toString()));
					dtoCuenta.setAplicaISR(funciones.validarCadena(gListCuenta.get(c).get("aplicaISR").toString()));
					dtoCuenta.setIsrIgualInt(funciones.validarCadena(gListCuenta.get(c).get("isrIgualInt").toString()));
				
				}
			}
						
			for(int i = 0; i < gListCtas.size(); i ++)
			{
				CtasContratoDto dtoContratos = new CtasContratoDto();
					dtoContratos.setIdBanco(funciones.convertirCadenaInteger(gListCtas.get(i).get("idBanco").toString()));
					dtoContratos.setIdChequera(funciones.validarCadena(gListCtas.get(i).get("idChequera").toString()));
   				 	dtoContratos.setM(funciones.validarCadena(gListCtas.get(i).get("m").toString()));
				listCtasCont.add(dtoContratos);
			}
			
			mapRet = inversionesService.insertarModificarContratos(bNuevo, bModifi, dtoCuenta, listCtasCont, noEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:insertarModificarContratos");
			e.printStackTrace();
		}
		return mapRet;
	}
	
	@DirectMethod
	public Map<String, Object> eliminarContratos(int iNoCuenta, int noEmpresa){
		Map<String, Object> mapRet = new HashMap<String, Object>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return mapRet;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			mapRet = inversionesService.eliminarContratos(iNoCuenta, noEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:eliminarContratos");
		}
		return mapRet;
	}
	
	@DirectMethod
	public List<CuentaDto> obtenerNumeroContratos(boolean bInternas, int noEmpresa){
		List<CuentaDto> listContratos = new ArrayList<CuentaDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listContratos;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			listContratos = inversionesService.obtenerNumeroContratos(bInternas, noEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:obtenerNumeroContratos");
		}
		return listContratos;
	}
	
	@DirectMethod
	public List<CatTipoValorDto> obtenerTipoValor(String sIdDivisa){
		List<CatTipoValorDto> listTipoVal = new ArrayList<CatTipoValorDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listTipoVal;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			listTipoVal = inversionesService.obtenerTipoValor(sIdDivisa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:obtenerTipoValor");
		}
		return listTipoVal;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboContactos(int iContacto1, int iContacto2, int iContacto3){
		List<LlenaComboGralDto> listCont = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listCont;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			listCont = inversionesService.llenarComboContactos(iContacto1, iContacto2, iContacto3);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:obtenerContactos");
		}
		return listCont;
	}
	
	@DirectMethod
	public List<CatPapelDto> obtenerPapel(String sIdTipoValor){
		List<CatPapelDto> listPapel = new  ArrayList<CatPapelDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listPapel;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			listPapel = inversionesService.obtenerPapel(sIdTipoValor);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:obtenerPapel");
		}
		return listPapel;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> consultarEmpresaConcentradora(){
		List<LlenaComboGralDto> listEmpCon = new  ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listEmpCon;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			listEmpCon = inversionesService.obtenerEmpresaConcentradora();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:obtenerPapel");
		}
		return listEmpCon;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> obtenerBancosMonitor(String sIdDivisa, String sBancos, int noEmpresa){
		List<LlenaComboGralDto> listBancos = new  ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listBancos;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			listBancos = inversionesService.obtenerBancosMonitor(sIdDivisa, sBancos, noEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:obtenerPapel");
		}
		return listBancos;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> obtenerChequerasMonitor(int iIdEmpresa, String sIdDivisa, int iIdBanco){
		List<LlenaComboGralDto> listChe = new  ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listChe;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			listChe = inversionesService.obtenerChequerasMonitor(iIdEmpresa, sIdDivisa, iIdBanco);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:obtenerPapel");
		}
		return listChe;
	}
	
	//here
	@DirectMethod
	public double obtenerSaldoInicial(int iIdBanco, String sChequera, boolean bAnterior, String sFecValor){
		ComunInversionesDto dto = new ComunInversionesDto();
		double dSaldoInicial = 0;
		if(!Utilerias.haveSession(WebContextManager.get()))
			return dSaldoInicial;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			dto.setIdBanco(iIdBanco);
			dto.setIdChequera(funciones.validarCadena(sChequera));
			dto.setAnterior(bAnterior);
			dto.setFecValor(funciones.ponerFechaDate(sFecValor));
			dSaldoInicial = inversionesService.obtenerSaldoInicial(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:obtenerSaldoInicial");
		}
		return dSaldoInicial;
	}
	
	@DirectMethod
	public List<MovimientoDto> llenarGridBarridoFondeo(int iIdEmpresa, int iIdBanco, String sIdDivisa, String sChequera
			, boolean bAnterior, String sFecValor){
		List<MovimientoDto> listBarr = new ArrayList<MovimientoDto>();
		List<MovimientoDto> listFond = new ArrayList<MovimientoDto>();
		List<MovimientoDto> listRet = new ArrayList<MovimientoDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listRet;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			ComunInversionesDto dto = new ComunInversionesDto();
				dto.setIdEmpresa(funciones.validarEntero(iIdEmpresa));
				dto.setIdBanco(funciones.validarEntero(iIdBanco));
				dto.setIdDivisa(funciones.validarCadena(sIdDivisa));
				dto.setIdChequera(funciones.validarCadena(sChequera));
				dto.setAnterior(bAnterior);
				dto.setFecValor(funciones.ponerFechaDate(sFecValor));
			listBarr = inversionesService.obtenerBarrido(dto);
			listFond = inversionesService.obtenerFondeo(dto);
			listRet.addAll(listBarr);
			listRet.addAll(listFond);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:llenarGridBarrido");
		}
		return listRet;
	}
	
	@DirectMethod
	public List<OrdenInversionDto> llenarGridVencimientos(int iIdBanco, String sChequera, String sIdDivisa, String sFecValor){
		List<OrdenInversionDto> listVenc = new ArrayList<OrdenInversionDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listVenc;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			ComunInversionesDto dto = new ComunInversionesDto();
				dto.setIdBanco(funciones.validarEntero(iIdBanco));
				dto.setIdDivisa(funciones.validarCadena(sIdDivisa));
				dto.setIdChequera(funciones.validarCadena(sChequera));
				dto.setFecValor(funciones.ponerFechaDate(sFecValor));
			listVenc = inversionesService.obtenerVencimientos(dto);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:llenarGridVencimientos");
		}
		return listVenc;
	}
	
	@DirectMethod
	public List<CatCtaBancoDto> llenarGridPorInvertir(int iIdBanco, String sChequera, String sIdDivisa, String sFecValor
			,boolean bAnterior,int iIdEmp){
		List<CatCtaBancoDto> listVenc = new ArrayList<CatCtaBancoDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listVenc;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			ComunInversionesDto dto = new ComunInversionesDto();
				dto.setIdBanco(funciones.validarEntero(iIdBanco));
				dto.setIdDivisa(funciones.validarCadena(sIdDivisa));
				dto.setIdChequera(funciones.validarCadena(sChequera));
				dto.setFecValor(funciones.ponerFechaDate(sFecValor));
				dto.setAnterior(bAnterior);
				dto.setIdEmpresa(funciones.validarEntero(iIdEmp));
			listVenc = inversionesService.obtenerPorInvertir(dto);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:llenarGridPorInvertir");
		}
		return listVenc;
	}
	
	@DirectMethod
	public List<MovimientoDto> llenarGridInversiones(int iIdEmpresa, int iIdBanco, String sIdDivisa, String sChequera
			, boolean bAnterior, String sFecValor, int iNoContrato, int iNoInstitucion){
		List<MovimientoDto> listInv = new ArrayList<MovimientoDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listInv;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			ComunInversionesDto dto = new ComunInversionesDto();
				dto.setIdEmpresa(funciones.validarEntero(iIdEmpresa));
				dto.setIdBanco(funciones.validarEntero(iIdBanco));
				dto.setIdDivisa(funciones.validarCadena(sIdDivisa));
				dto.setIdChequera(funciones.validarCadena(sChequera));
				dto.setAnterior(bAnterior);
				dto.setContrato(funciones.validarEntero(iNoContrato));
				dto.setFecValor(funciones.ponerFechaDate(sFecValor));
			listInv = inversionesService.obtenerBarrido(dto);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:llenarGridInversiones");
		}
		return listInv;
	}
	
	@DirectMethod
	public Map<String, Object> insertarOrdenInversion(String sOrdenInv){
		Map<String, Object> mapRet = new HashMap<String, Object>();
		Gson gson = new Gson();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return mapRet;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			List<Map<String, String>> gListOrden = gson.fromJson(sOrdenInv, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			OrdenInversionDto dtoOrdenInv = new OrdenInversionDto();
			for(int i = 0; i < gListOrden.size(); i ++)
			{
				dtoOrdenInv.setNoEmpresa(Integer.parseInt(funciones.validarCadena(gListOrden.get(i).get("noEmpresa"))));
				dtoOrdenInv.setIdPapel(funciones.validarCadena(gListOrden.get(i).get("idPapel").toString().trim()));
				dtoOrdenInv.setIdTipoValor(funciones.validarCadena(gListOrden.get(i).get("idTipoValor").toString().trim()));
				dtoOrdenInv.setNoCuenta(Integer.parseInt(funciones.validarCadena(gListOrden.get(i).get("idCuenta").toString().trim())));
				dtoOrdenInv.setNoInstitucion(Integer.parseInt(funciones.validarCadena(gListOrden.get(i).get("idInstitucion").toString().trim())));
				if(gListOrden.get(i).get("idContacto").toString().trim().equals("")){
					dtoOrdenInv.setNoContacto(0);
				}else{
					dtoOrdenInv.setNoContacto(Integer.parseInt(gListOrden.get(i).get("idContacto").toString().trim()));
				}
				dtoOrdenInv.setHora(Integer.parseInt(gListOrden.get(i).get("hora").toString().trim()));
				
				dtoOrdenInv.setFecVenc(funciones.ponerFechaDate(gListOrden.get(i).get("fecVencimiento").toString().trim()));
				
				dtoOrdenInv.setMinuto(Integer.parseInt(gListOrden.get(i).get("minuto").toString().trim()));
				dtoOrdenInv.setImporte(funciones.convertirCadenaDouble(gListOrden.get(i).get("importe").toString().trim()));
				dtoOrdenInv.setInteres(funciones.convertirCadenaDouble(gListOrden.get(i).get("interes").toString().trim()));
				dtoOrdenInv.setPlazo(Integer.parseInt(gListOrden.get(i).get("plazo").toString().trim()));
				dtoOrdenInv.setTasa(funciones.convertirCadenaDouble(gListOrden.get(i).get("tasa").toString().trim()));
				dtoOrdenInv.setImporteTraspaso(funciones.convertirCadenaDouble(gListOrden.get(i).get("impuesto").toString().trim()));
				dtoOrdenInv.setIsr(funciones.convertirCadenaDouble(gListOrden.get(i).get("impuestoEsperado").toString().trim()));
				dtoOrdenInv.setDiasAnual(Integer.parseInt(gListOrden.get(i).get("plazoInv").toString().trim()));
				dtoOrdenInv.setBGarantia(gListOrden.get(i).get("chkGarantia").toString().equals("true") ? "S" : "N");
				dtoOrdenInv.setIdDivisa(funciones.validarCadena(gListOrden.get(i).get("idDivisa").toString().trim()));
				
				/*regOrdenInversion.idTipoValor = BFwrk.Util.scanFieldsStore(NS.storeTipoValor, 'descTipoValor',Ext.getCmp(PF + 'cmbTipoValor').getValue(),'idTipoValor');
								        regOrdenInversion.idCuenta = BFwrk.Util.scanFieldsStore(NS.storeContrato, 'descripcion',Ext.getCmp(PF + 'cmbNumContrato').getValue(),'noCuenta');
								        regOrdenInversion.idInstitucion = BFwrk.Util.scanFieldsStore(NS.storeContrato, 'descripcion',Ext.getCmp(PF + 'cmbNumContrato').getValue(),'noInstitucion');
								        regOrdenInversion.idContacto = BFwrk.Util.scanFieldsStore(NS.storeContactos, 'descripcion',Ext.getCmp(PF + 'cmbContactos').getValue(),'id');
								        regOrdenInversion.hora = Ext.getCmp(PF + 'cmbHora').getValue();
								        regOrdenInversion.fecVencimiento = BFwrk.Util.changeDateToString(Ext.getCmp(PF + 'txtFechaVencimiento').getValue());
								        regOrdenInversion.minuto = 7;
								        regOrdenInversion.importe = Ext.getCmp(PF + 'txtImporte').getValue();
								        regOrdenInversion.interes = Ext.getCmp(PF + 'txtInteres').getValue();
								        regOrdenInversion.plazo = Ext.getCmp(PF + 'txtPlazo').getValue();
								        regOrdenInversion.tasa = Ext.getCmp(PF + 'txtTasa').getValue();
								        regOrdenInversion.impuestoEsperado = Ext.getCmp(PF + 'txtImpuestoEsperado').getValue();
								        regOrdenInversion.numContrato = Ext.getCmp(PF + 'cmbNumContracto').getValue();
								        regOrdenInversion.chkGarantia = Ext.getCmp(PF + 'chkGarantia').getValue();*/
			}
			mapRet = inversionesService.insertarOrdenInversion(dtoOrdenInv);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:insertarOrdenInversion");
			e.printStackTrace();
		}
		return mapRet;
	}
	
	@DirectMethod
	public boolean verificarFechaInhabil(String sFecha){
		boolean bInhabil = false;
		if(!Utilerias.haveSession(WebContextManager.get()))
			return bInhabil;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			bInhabil = inversionesService.verificarFechaInhabil(sFecha);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:verificarFechaInhabil");
		}
		return bInhabil;
	}
	
	@SuppressWarnings("unchecked")
	@DirectMethod
	public Map calcularInteresImpuesto(double uCapital, double uTasaImpuesto, int iPlazo, int iDiasAnual, 
			int iCuenta, int iNoInstitucion, String sContrato, String cisr)
	{
		Map<String, Object> mapIntImp = new HashMap<String, Object>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return mapIntImp;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			mapIntImp = inversionesService.calcularInteresImpuesto(uCapital, uTasaImpuesto, iPlazo, iDiasAnual, 
																	iCuenta, iNoInstitucion, sContrato, cisr);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:calcularInteres");
		}
		return mapIntImp;
	}
	
	@DirectMethod
	public List<ConsultaOrdenInversionDto> obtenerOrdenesInversion(String noEmpresa, int iUserId)
	{
		List<ConsultaOrdenInversionDto> listConsOrd = new ArrayList<ConsultaOrdenInversionDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listConsOrd;
		try
		{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			listConsOrd = inversionesService.obtenerOrdenesInversion(noEmpresa.equals("") ? 0 : Integer.parseInt(noEmpresa));
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:obtenerOrdenesInversion");
		}
		return listConsOrd;
	}
	
	@DirectMethod
	public int actualizarAutorizaOrdenInversion(String sAutoriza, int iNoOrden){
		int iRegAfec = 0;
		if(!Utilerias.haveSession(WebContextManager.get()))
			return iRegAfec;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			iRegAfec = inversionesService.actualizarAutorizaOrdenInversion(sAutoriza,iNoOrden);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:actualizarAutorizaOrdenInversion");
		}
		return iRegAfec;
	}
	
	@DirectMethod
	public Map<String, Object> ejecutarRevividor(String datosOrdenInversion, int noEmpresa){
		Map<String, Object> mapRet = new HashMap<String, Object>();
		List<ConsultaOrdenInversionDto> listOrdenInv = new ArrayList<ConsultaOrdenInversionDto>();
		Gson gson = new Gson();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return mapRet;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			List<Map<String, String>> gListOrden = gson.fromJson(datosOrdenInversion, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			
			for(int i = 0; i < gListOrden.size(); i ++)
			{
				ConsultaOrdenInversionDto dto = new ConsultaOrdenInversionDto();
					dto.setNoFolioDet(funciones.validarEntero(Integer.parseInt((gListOrden.get(i).get("noFolioDet").toString()))));
					dto.setIdTipoOperacion(Integer.parseInt(gListOrden.get(i).get("idTipoOperacion").toString()));
					dto.setIdEstatusMov(gListOrden.get(i).get("idEstatusMov").toString());
					dto.setOrigenMov(gListOrden.get(i).get("origenMov").toString());
					dto.setIdFormaPago(Integer.parseInt(gListOrden.get(i).get("idFormaPago").toString()));
					dto.setBEntregado(gListOrden.get(i).get("bEntregado").toString());
					dto.setIdTipoMovto(gListOrden.get(i).get("idTipoMovto").toString());
					dto.setImporte(Double.parseDouble(gListOrden.get(i).get("importe").toString()));
					dto.setNoCuenta(Integer.parseInt(gListOrden.get(i).get("noCuenta").toString()));
					dto.setIdChequera(gListOrden.get(i).get("idChequera").toString());
					dto.setIdBanco(Integer.parseInt(gListOrden.get(i).get("idBanco").toString()));
					dto.setNoOrden(gListOrden.get(i).get("noOrden").toString());
					dto.setLoteEntrada(Integer.parseInt(gListOrden.get(i).get("loteEntrada").toString()));
					dto.setBSalvoBuenCobro(gListOrden.get(i).get("bSalvoBuenCobro").toString());
					dto.setIdDivisa(gListOrden.get(i).get("idDivisa").toString());
				listOrdenInv.add(dto);
			}
			mapRet = inversionesService.ejecutarRevividor(listOrdenInv, noEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:ejecutarRevividor");
			e.printStackTrace();
		}
		return mapRet;
	}
	
	//Inician metodos para liquidacion de inversiones
	@DirectMethod
	public List<ConsultaOrdenInversionDto> consultarLiquidaOrdenInversion(int iAvisoInversion, String noEmpresa, int iUserId){
		System.out.println(".::"+iAvisoInversion);
		System.out.println(".::"+noEmpresa);
		System.out.println(".::"+iUserId);
		List<ConsultaOrdenInversionDto> listLiqOrd = new ArrayList<ConsultaOrdenInversionDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listLiqOrd;
		try
		{
			System.out.println("metodo liquidacion de inversiones");
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			listLiqOrd = inversionesService.consultarLiquidaOrdenInversion(iAvisoInversion, noEmpresa.equals("") ? -1 : Integer.parseInt(noEmpresa), iUserId);
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:consultarLiquidaOrdenInversion");
		}	
		return listLiqOrd;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> obtenerBancosRegresoInversion(int iIdEmpresa, String sIdDivisa)
	{
		List<LlenaComboGralDto> listBancos = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listBancos;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			listBancos = inversionesService.obtenerBancosRegresoInversion(iIdEmpresa, sIdDivisa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:obtenerBancosRegresoInversion");
		}
		return listBancos;
	}
	
	@DirectMethod
	public List<CuentaDto> obtenerContratosLiq(int iIdEmpresa, int iNumCta){
		List<CuentaDto> listContLiq = new ArrayList<CuentaDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listContLiq;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			listContLiq = inversionesService.obtenerContratosLiq(iIdEmpresa, iNumCta);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:obtenerContratosLiq");
		}
		return listContLiq;
	}
	
	/**
	 * Este mï¿½todo obtiene las chequeras de ctas_banco,
	 * es utilizado en LiquidacionDeInversiones
	 * @param iIdBancoLiq
	 * @param iNoCuenta
	 * @param bInterna
	 * @return
	 */
	@DirectMethod
	public List<LlenaComboChequeraDto> obtenerChequerasLiq(int iIdBancoLiq, int iNoCuenta, boolean bInterna){
		List<LlenaComboChequeraDto> listChe = new ArrayList<LlenaComboChequeraDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listChe;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			listChe = inversionesService.obtenerChequerasLiq(iIdBancoLiq, iNoCuenta, bInterna);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:obtenerChequerasLiq");
		}
		return listChe;
	}
	
	/**
	 * Este mï¿½todo obtiene los bancosCargo que estan asignados 
	 * a la empresa y dependiendo la divisa, es utilizado en LiquidacionDeInversiones 
	 * @param iIdEmpresa
	 * @param sIdDivisa
	 * @return
	 */
	@DirectMethod
	public List<LlenaComboGralDto> obtenerBancosCargo(int iIdEmpresa, String sIdDivisa){
		List<LlenaComboGralDto> listBancos = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listBancos;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			listBancos = inversionesService.obtenerBancosCargo(iIdEmpresa, sIdDivisa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:obtenerBancosCargo");
		}
		return listBancos;
	}
	
	/**
	 * Este mï¿½todo obtiene las chequeras de cat_cta_banco,
	 * ademas del saldo final, es utilizado en LiquidacionDeInversiones
	 * @param iIdEmpresa
	 * @param iIdBanco
	 * @param sIdDivisa
	 * @return
	 */
	@DirectMethod
	public List<CatCtaBancoDto> obtenerChequerasCargo(int iIdEmpresa, int iIdBanco, String sIdDivisa){
		List<CatCtaBancoDto> listChe = new ArrayList<CatCtaBancoDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listChe;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			listChe = inversionesService.obtenerChequerasCargo(iIdEmpresa, iIdBanco, sIdDivisa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:obtenerChequerasCargo");
		}
		return listChe;
	}
	
	@DirectMethod
	public Map<String, Object> liquidarInversiones(String gridOrdenInv, String gridBancoCargo, String datosVentana){
	    List<ConsultaOrdenInversionDto> listOrdenInv = new ArrayList<ConsultaOrdenInversionDto>(); 
	    List<BancoCheContratoDto> listBancoCargo = new ArrayList<BancoCheContratoDto>();
		Map<String, Object> mapRet = new HashMap<String, Object>();
		Gson gson = new Gson();
		
		//System.out.println("DAtos gridBancoCargo "+ gridBancoCargo);
		
		if(!Utilerias.haveSession(WebContextManager.get()))
			return mapRet;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			List<Map<String, String>> gListOrdenInv = gson.fromJson(gridOrdenInv, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			List<Map<String, String>> gListBancoCargo = gson.fromJson(gridBancoCargo, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			List<Map<String, String>> gDatosVentana = gson.fromJson(datosVentana, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			
			for(int i = 0 ; i < gListOrdenInv.size(); i ++ )
			{
				ConsultaOrdenInversionDto dtoOrd = new ConsultaOrdenInversionDto();
					dtoOrd.setNoOrden(funciones.validarCadena(gListOrdenInv.get(i).get("noOrden").toString()));
					dtoOrd.setNombreCorto(funciones.validarCadena(gListOrdenInv.get(i).get("nombreCorto").toString()));
					dtoOrd.setPlazo(funciones.convertirCadenaInteger(gListOrdenInv.get(i).get("plazo").toString()));
					dtoOrd.setImporte(funciones.convertirCadenaDouble(gListOrdenInv.get(i).get("importe").toString()));
					dtoOrd.setTasa(funciones.convertirCadenaDouble(gListOrdenInv.get(i).get("tasa").toString()));
					dtoOrd.setInteres(funciones.convertirCadenaDouble(gListOrdenInv.get(i).get("interes").toString()));
					dtoOrd.setIsr(funciones.convertirCadenaDouble(gListOrdenInv.get(i).get("isr").toString()));
//					dtoOrd.setNoContacto(funciones.convertirCadenaInteger(gListOrdenInv.get(i).get("noContacto").toString()));
					dtoOrd.setNoContacto(0);
					dtoOrd.setFecAlta(funciones.ponerFechaDate(gListOrdenInv.get(i).get("fecAlta").toString()));
					dtoOrd.setFecVenc(funciones.ponerFechaDate(gListOrdenInv.get(i).get("fecVenc").toString()));
					dtoOrd.setNoCuenta(funciones.convertirCadenaInteger(gListOrdenInv.get(i).get("noCuenta").toString()));
					dtoOrd.setNoFolioDet(funciones.convertirCadenaInteger(gListOrdenInv.get(i).get("noFolioDet").toString()));
					dtoOrd.setIdTipoOperacion(funciones.convertirCadenaInteger(gListOrdenInv.get(i).get("idTipoOperacion").toString()));
					
					dtoOrd.setIdBanco(funciones.convertirCadenaInteger(gListOrdenInv.get(i).get("idBanco").toString()));
					dtoOrd.setIdChequera(gListOrdenInv.get(i).get("idChequera").toString());
					
				listOrdenInv.add(dtoOrd);
			}
			
			for(int c = 0; c < gListBancoCargo.size(); c ++)
			{
				BancoCheContratoDto dtoBanCar = new BancoCheContratoDto();
					dtoBanCar.setIdBanco(funciones.convertirCadenaInteger(gListBancoCargo.get(c).get("idBancoCargo").toString()));
					dtoBanCar.setIdChequera(funciones.validarCadena(gListBancoCargo.get(c).get("idChequeraCargo").toString()));
					dtoBanCar.setImporte(funciones.convertirCadenaDouble(gListBancoCargo.get(c).get("importe").toString()));
				listBancoCargo.add(dtoBanCar);
			}
			
			LiquidaInversionesDto dtoLiqInv = new LiquidaInversionesDto();
				dtoLiqInv.setIdEmpresa(funciones.convertirCadenaInteger(gDatosVentana.get(0).get("idEmpresa").toString()));
//				dtoLiqInv.setBInterna(funciones.convertirCadenaBoolean(gDatosVentana.get(0).get("bInterna").toString()));
				dtoLiqInv.setBInterna(false);
//				dtoLiqInv.setBCuentaPropia(funciones.convertirCadenaBoolean(gDatosVentana.get(0).get("bCuentaPropia").toString()));
				dtoLiqInv.setBCuentaPropia(true);
				dtoLiqInv.setIdBancoRegreso(funciones.convertirCadenaInteger(gDatosVentana.get(0).get("idBancoRegreso").toString()));
				dtoLiqInv.setIdChequeraRegreso(funciones.validarCadena(gDatosVentana.get(0).get("idChequeraRegreso").toString()));
				try{
					dtoLiqInv.setIdBancoLiquida(funciones.convertirCadenaInteger(gDatosVentana.get(0).get("idBancoInv").toString()));
					dtoLiqInv.setIdChequeraLiquida(funciones.validarCadena(gDatosVentana.get(0).get("idChequeraInv").toString()));
				}catch(NullPointerException npe){
					//System.out.println("Las chequeras están vacías");
				}
				dtoLiqInv.setIdDivisa(funciones.validarCadena(gDatosVentana.get(0).get("idDivisa").toString()));
				dtoLiqInv.setFechaAlta(funciones.ponerFechaDate(gDatosVentana.get(0).get("fechaAlta").toString()));
				dtoLiqInv.setImporte(funciones.convertirCadenaDouble(gDatosVentana.get(0).get("importe").toString()));
				dtoLiqInv.setNomEmpresa(funciones.validarCadena(gDatosVentana.get(0).get("nomEmpresa").toString()));
				
				Boolean mismaChequeraLiqYCargo = false;
				
				if(dtoLiqInv.getIdBancoLiquida() == listBancoCargo.get(0).getIdBanco()
						&& dtoLiqInv.getIdChequeraLiquida().equals(listBancoCargo.get(0).getIdChequera())){
					mismaChequeraLiqYCargo = true;
				}
				
//						dtoLiqInv.setChequerasDiferentesUno(funciones.validarCadena(gDatosVentana.get(0).get("chequerasDiferentesUno").toString()).trim().equals("true") ? true : false);
				dtoLiqInv.setChequerasDiferentesUno(mismaChequeraLiqYCargo);
//				dtoLiqInv.setChequerasDiferentesDos(funciones.validarCadena(gDatosVentana.get(0).get("chequerasDiferentesDos").toString()).trim().equals("true") ? true : false);
				dtoLiqInv.setChequerasDiferentesDos(false);
				dtoLiqInv.setTipoPago(funciones.convertirCadenaInteger(gDatosVentana.get(0).get("tipoPago").toString()));
			mapRet = inversionesService.liquidarInversiones(dtoLiqInv, listOrdenInv, listBancoCargo);
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:liquidarInversiones");
		}
		return mapRet;
   }
	
	
	@DirectMethod
	public Map<String, Object> liquidarInversionesMultiple(String datos){
	    List<ConsultaOrdenInversionDto> listOrdenInv = new ArrayList<ConsultaOrdenInversionDto>(); 
	    List<BancoCheContratoDto> listBancoCargo = new ArrayList<BancoCheContratoDto>();
		Map<String, Object> mapRet = new HashMap<String, Object>();
		Gson gson = new Gson();
		
		//System.out.println("Datos  "+ datos);
		
		if(!Utilerias.haveSession(WebContextManager.get()))
			return mapRet;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			List<Map<String, String>> gListDatos = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			
			//System.out.println("El tamaño de la lista es "+ gListDatos.size());
			
			for(int i = 0 ; i < gListDatos.size(); i ++ ){
				listOrdenInv = new ArrayList<ConsultaOrdenInversionDto>();
				listBancoCargo = new ArrayList<BancoCheContratoDto>();
				
				ConsultaOrdenInversionDto dtoOrd = new ConsultaOrdenInversionDto();
				dtoOrd.setNoOrden(funciones.validarCadena(gListDatos.get(i).get("noOrden").toString()));
				dtoOrd.setNombreCorto(funciones.validarCadena(gListDatos.get(i).get("nombreCorto").toString()));
				dtoOrd.setPlazo(funciones.convertirCadenaInteger(gListDatos.get(i).get("plazo").toString()));
				dtoOrd.setImporte(funciones.convertirCadenaDouble(gListDatos.get(i).get("importe").toString()));
				dtoOrd.setTasa(funciones.convertirCadenaDouble(gListDatos.get(i).get("tasa").toString()));
				dtoOrd.setInteres(funciones.convertirCadenaDouble(gListDatos.get(i).get("interes").toString()));
				dtoOrd.setIsr(funciones.convertirCadenaDouble(gListDatos.get(i).get("isr").toString()));
				dtoOrd.setNoContacto(0);
				dtoOrd.setFecAlta(funciones.ponerFechaDate(gListDatos.get(i).get("fecAlta").toString()));
				dtoOrd.setFecVenc(funciones.ponerFechaDate(gListDatos.get(i).get("fecVenc").toString()));
				dtoOrd.setNoCuenta(funciones.convertirCadenaInteger(gListDatos.get(i).get("noCuenta").toString()));
				dtoOrd.setNoFolioDet(funciones.convertirCadenaInteger(gListDatos.get(i).get("noFolioDet").toString()));
				dtoOrd.setIdTipoOperacion(funciones.convertirCadenaInteger(gListDatos.get(i).get("idTipoOperacion").toString()));
				
				dtoOrd.setIdBanco(funciones.convertirCadenaInteger(gListDatos.get(i).get("idBanco").toString()));
				dtoOrd.setIdChequera(gListDatos.get(i).get("idChequera").toString());
					
				listOrdenInv.add(dtoOrd);

				BancoCheContratoDto dtoBanCar = new BancoCheContratoDto();
				dtoBanCar.setIdBanco(funciones.convertirCadenaInteger(gListDatos.get(i).get("idBancoCargo").toString()));
				dtoBanCar.setIdChequera(funciones.validarCadena(gListDatos.get(i).get("idChequeraCargo").toString()));
				dtoBanCar.setImporte(funciones.convertirCadenaDouble(gListDatos.get(i).get("importe").toString()));
				listBancoCargo.add(dtoBanCar);
			
				LiquidaInversionesDto dtoLiqInv = new LiquidaInversionesDto();
				dtoLiqInv.setIdEmpresa(funciones.convertirCadenaInteger(gListDatos.get(i).get("idEmpresa").toString()));
				dtoLiqInv.setBInterna(false);
				dtoLiqInv.setBCuentaPropia(true);
				dtoLiqInv.setIdBancoRegreso(funciones.convertirCadenaInteger(gListDatos.get(i).get("idBancoRegreso").toString()));
				dtoLiqInv.setIdChequeraRegreso(funciones.validarCadena(gListDatos.get(i).get("idChequeraRegreso").toString()));
				try{
					dtoLiqInv.setIdBancoLiquida(funciones.convertirCadenaInteger(gListDatos.get(i).get("idBancoInv").toString()));
					dtoLiqInv.setIdChequeraLiquida(funciones.validarCadena(gListDatos.get(i).get("idChequeraInv").toString()));
				}catch(NullPointerException npe){
					//System.out.println("Las chequeras están vacías");
				}
				dtoLiqInv.setIdDivisa(funciones.validarCadena(gListDatos.get(i).get("idDivisa").toString()));
				dtoLiqInv.setFechaAlta(funciones.ponerFechaDate(gListDatos.get(i).get("fechaAlta").toString()));
				dtoLiqInv.setImporte(funciones.convertirCadenaDouble(gListDatos.get(i).get("importe").toString()));
				dtoLiqInv.setNomEmpresa(funciones.validarCadena(gListDatos.get(i).get("nomEmpresa").toString()));
				
				Boolean mismaChequeraLiqYCargo = false;
				
				if(dtoLiqInv.getIdBancoLiquida() == listBancoCargo.get(0).getIdBanco()
						&& dtoLiqInv.getIdChequeraLiquida().equals(listBancoCargo.get(0).getIdChequera())){
					mismaChequeraLiqYCargo = true;
				}
				
				dtoLiqInv.setChequerasDiferentesUno(mismaChequeraLiqYCargo);
				dtoLiqInv.setChequerasDiferentesDos(false);
				dtoLiqInv.setTipoPago(funciones.convertirCadenaInteger(gListDatos.get(i).get("tipoPago").toString()));
				mapRet = inversionesService.liquidarInversiones(dtoLiqInv, listOrdenInv, listBancoCargo);
			}
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:liquidarInversiones");
		}
		return mapRet;
   }

	
	/**
	 * Este metodo es utilizado para cancelar ordenes de inversion
	 * en LiquidacionDeInversiones.js
	 * @param iNoOrden
	 * @return retorna mayor a cero si se realizo exitosamente 
	 */
	@DirectMethod
	public int cancelarOrdenesInversion(int iNoOrden){
		int iRegAfec = 0;
		if(!Utilerias.haveSession(WebContextManager.get()))
			return iRegAfec;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			iRegAfec = inversionesService.cancelarOrdenesInversionSET(iNoOrden);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:cancelarOrdenesInversion");
		}
		return iRegAfec;
	}
	
	@DirectMethod
	public List<MovimientoDto> obtenerVencimientosInversion(String sFecha, boolean bInvInterna, String sSrefMov, int iNoOrden, int noEmpresa,Integer idUsuario){
		List<MovimientoDto> listVenc = new ArrayList<MovimientoDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listVenc;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			ComunInversionesDto dto = new ComunInversionesDto();
				if(sFecha != null && !sFecha.equals(""))
				dto.setFecValor(funciones.ponerFechaDate(sFecha));
				dto.setBInversionInterna(bInvInterna);
				dto.setSrefmov(sSrefMov);
				dto.setNoOrden(iNoOrden);
				dto.setIdEmpresa(noEmpresa);
				dto.setIdUsuario(idUsuario);
			listVenc = inversionesService.obtenerVencimientosInversion(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:obtenerVencimientosInversion");
		}
		return listVenc;
	}
	
	/**
	 * Este mï¿½todo obtiene el nombre del contacto de la orden de inversiï¿½n,
	 * es utilizado en VencimientoDeInversiones.js
	 * @param iNoOrden
	 * @return
	 */
	@DirectMethod
	public List<LlenaComboGralDto> obtenerContactoOrden(int iNoOrden){
		List<LlenaComboGralDto> listCon = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listCon;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			listCon = inversionesService.obtenerContactoOrden(iNoOrden);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:obtenerContactoOrden");
		}
		return listCon;
	}
	
	@DirectMethod
	public List<OrdenInversionDto> obtenerDiasAnual(String sFechaVen, int iBanco, String sChequera, 
			double uTasa, int iNoCuenta, int iNoDocto)
	{
		List<OrdenInversionDto> listOrden = new ArrayList<OrdenInversionDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listOrden;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			listOrden = inversionesService.obtenerDiasAnual(sFechaVen, iBanco, sChequera, uTasa, iNoCuenta, iNoDocto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:obtenerDiasAnual");
		}
		return listOrden;
	}
	
	/**
	 * Este mï¿½todo obtiene el interes, es utilizado en VencimientoDeInversion.js
	 * @param uCapital
	 * @param uTasa
	 * @param iPlazo
	 * @param iDiasAnual
	 * @return
	 */
	@DirectMethod
	public double calcularInteres(double uCapital, double uTasa, int iPlazo, int iDiasAnual){
		double uInteres = 0;
		if(!Utilerias.haveSession(WebContextManager.get()))
			return uInteres;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			uInteres = inversionesService.calcularInteres(uCapital, uTasa, iPlazo, iDiasAnual);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:calcularInteres");
		}
		return uInteres;
	}
	
	/**
	 * Este mï¿½todo obtiene el impuesto, es utilizado en 
	 * vencimientoDeInversiones
	 * @param uCapital
	 * @param uTasa
	 * @param iPlazo
	 * @param iDiasAnual
	 * @param iNoContrato
	 * @param iNoCuenta
	 * @param iNoInstitucion
	 * @return
	 */
	@DirectMethod
	public double calcularImpuesto(double uCapital, int iPlazo, int iDiasAnual, 
									int iNoContrato, int iNoCuenta, int iNoInstitucion){
		double uImpuesto = 0;
		if(!Utilerias.haveSession(WebContextManager.get()))
			return uImpuesto;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			uImpuesto = inversionesService.calcularImpuesto(uCapital, iPlazo, iDiasAnual, iNoContrato, iNoCuenta, iNoInstitucion);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:calcularInteres");
		}
		return uImpuesto;
	}
	
	/**
	 * Este mï¿½todo realiza la modificaciï¿½n de la tasa en orden_inversion,
	 * es utilizado en VencimientoDeInversion.js
	 * @param uTasa
	 * @param sNoOrden
	 * @param sFecha
	 * @return
	 */
	@DirectMethod
	public int modificarTasa(double uTasa, String sNoOrden, String sFecha, int noEmpresa){
		int iRegAfec = 0;
		if(!Utilerias.haveSession(WebContextManager.get()))
			return iRegAfec;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			iRegAfec = inversionesService.modificarTasa(uTasa, sNoOrden, sFecha, noEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:actualizarTasa");
			iRegAfec = -1;
		}
		return iRegAfec;
	}
	
	@SuppressWarnings("unchecked")
	@DirectMethod
	public Map<String, Object> ejecutarVencimientoInversion(String datosVencimiento, double uImporteTxt, double uInteresTxt, double uImpuestoTxt,
											boolean bInversionInterna, double uTasaReinversion)
	{
		Map<String, Object> mapRet = new HashMap<String, Object>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return mapRet;
		List<MovimientoDto> listVencimiento = new ArrayList<MovimientoDto>();
		Gson gson = new Gson();
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			List<Map<String, String>> gListVenc = gson.fromJson(datosVencimiento, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());

			for(int i = 0; i < gListVenc.size(); i ++){
				MovimientoDto dtoMovi = new MovimientoDto();
					dtoMovi.setFecValor(funciones.ponerFechaDate3(gListVenc.get(i).get("fecValor").toString()));
					dtoMovi.setIdBanco(funciones.convertirCadenaInteger(gListVenc.get(i).get("idBanco").toString()));
					dtoMovi.setDescBancoBenef(gListVenc.get(i).get("descBancoBenef").toString());
					dtoMovi.setIdChequera(gListVenc.get(i).get("idChequera").toString());
					dtoMovi.setValorTasa(funciones.convertirCadenaDouble(gListVenc.get(i).get("valorTasa").toString()));
					dtoMovi.setImporte(funciones.convertirCadenaDouble(gListVenc.get(i).get("importe").toString()));
					dtoMovi.setIdCveOperacion(funciones.convertirCadenaInteger(gListVenc.get(i).get("idCveOperacion").toString()));
					dtoMovi.setNoFolioDet(funciones.convertirCadenaInteger(gListVenc.get(i).get("noFolioDet").toString()));
					dtoMovi.setFolioRef(funciones.convertirCadenaInteger(gListVenc.get(i).get("folioRef").toString()));
					dtoMovi.setIdDivisa(gListVenc.get(i).get("idDivisa").toString());
					dtoMovi.setNoCuenta(funciones.convertirCadenaInteger(gListVenc.get(i).get("noCuenta").toString()));
					dtoMovi.setIdTipoOperacion(funciones.convertirCadenaInteger(gListVenc.get(i).get("idTipoOperacion").toString()));
					dtoMovi.setNoDocto(gListVenc.get(i).get("noDocto").toString());
					dtoMovi.setDiasInv(funciones.convertirCadenaInteger(gListVenc.get(i).get("noDocto").toString()));
					dtoMovi.setInteres(funciones.convertirCadenaDouble(gListVenc.get(i).get("interes").toString()));
					dtoMovi.setIsr(funciones.convertirCadenaDouble(gListVenc.get(i).get("isr").toString()));
					dtoMovi.setTotalImporte(funciones.convertirCadenaDouble(gListVenc.get(i).get("totalImporte").toString()));
					dtoMovi.setInstFinan(gListVenc.get(i).get("instFinan").toString());
//					dtoMovi.setNomContacto(gListVenc.get(i).get("nomContacto").toString());
					
					dtoMovi.setCveOperacionA(funciones.convertirCadenaInteger(gListVenc.get(i).get("idCveOperacion").toString()));
					dtoMovi.setCveOperacionB(funciones.convertirCadenaInteger(gListVenc.get(i).get("cveOperacionB").toString()));
					dtoMovi.setCveOperacionC(funciones.convertirCadenaInteger(gListVenc.get(i).get("cveOperacionC").toString()));
					dtoMovi.setFolioDetA(funciones.convertirCadenaInteger(gListVenc.get(i).get("noFolioDet").toString()));
					dtoMovi.setFolioDetB(funciones.convertirCadenaInteger(gListVenc.get(i).get("folioDetB").toString()));
					dtoMovi.setFolioDetC(funciones.convertirCadenaInteger(gListVenc.get(i).get("folioDetC").toString()));
					dtoMovi.setFolioRefA(funciones.convertirCadenaInteger(gListVenc.get(i).get("folioRef").toString()));
					dtoMovi.setFolioRefB(funciones.convertirCadenaInteger(gListVenc.get(i).get("folioRef").toString()));
					dtoMovi.setFolioRefC(funciones.convertirCadenaInteger(gListVenc.get(i).get("folioRef").toString()));
					dtoMovi.setInteresAnt(funciones.convertirCadenaDouble(gListVenc.get(i).get("interesAnt").toString()));
					dtoMovi.setImpuestoAnt(funciones.convertirCadenaDouble(gListVenc.get(i).get("impuestoAnt").toString()));
					
					dtoMovi.setBInversionInterna(bInversionInterna);
					dtoMovi.setFecValorOriginal(funciones.ponerFechaDate(gListVenc.get(i).get("fecValorOriginal").toString()));
					
					dtoMovi.setNoEmpresa(funciones.convertirCadenaInteger(gListVenc.get(i).get("noEmpresa").toString()));
					
				listVencimiento.add(dtoMovi);
				
			}
		
			mapRet = inversionesService.ejecutarVencimientoInversion(listVencimiento, uImporteTxt, uInteresTxt, uImpuestoTxt, uTasaReinversion);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:ejecutarVencimientoInversion");
			e.printStackTrace();
		}
		return mapRet;
	}
	
	@DirectMethod
	public List<LlenaComboEmpresasDto> llenarComboEmpresas(){
		List<LlenaComboEmpresasDto> listEmpCon = new  ArrayList<LlenaComboEmpresasDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listEmpCon;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			listEmpCon = inversionesService.llenarComboEmpresas();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Inversiones, C:InversionesAction, M:llenarComboEmpresas");
		}
		return listEmpCon;
	}
	
	/**
	 * Este mï¿½todo obtiene realiza el reporte de LiquidacionDeInversion
	 * en LiquidacionDeInversion
	 * @param datos
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@DirectMethod
	public JRDataSource obtenerReporteLiquidacionInv(Map datos, ServletContext context){
		JRDataSource jrDataSource = null;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl" , context);
			jrDataSource = inversionesService.obtenerReporteLiquidacionInv(datos);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:obtenerReporteLiquidacionInv");
		}
		return jrDataSource;
	}

	/**
	 * Este mï¿½todo obtiene realiza el reporte de LiquidacionDeInversion
	 * en LiquidacionDeInversion
	 * @param datos
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@DirectMethod
	public JRMapArrayDataSource obtenerReporteLiquidacionInversion(Map datos, ServletContext context){
		JRMapArrayDataSource jrDataSource = null;
		System.out.println(".Reporte.::"+datos);
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl" , context);
			jrDataSource = inversionesService.obtenerReporteLiquidacionInversion(datos);
			
			System.out.println("El tamaño en el action es "+ jrDataSource.getRecordCount());
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:obtenerReporteLiquidacionInv");
		}
		return jrDataSource;
	}

	
	@SuppressWarnings("unchecked")
	@DirectMethod
	public JRDataSource obtenerReportesInversion(Map parametros, ServletContext context)
	{
		JRDataSource jrDataSource = null;
		try
		{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl" , context);
			jrDataSource = inversionesService.obtenerReportesInversion(parametros);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:obtenerReportesInversion");
		}
		return jrDataSource;
	}
	
	/**
	 * Grafica el total de inversiones por fecha.
	 * @param iUserId
	 * @param sNoEmpresa
	 * @param sDivisa
	 * @param sFecIni
	 * @param sFecFin
	 */
	@DirectMethod
	public List<ConsultaOrdenInversionDto> obtenerMontoInvertidoXFecha (int iUserId, String sNoEmpresa, String sDivisa, String sFecIni, String sFecFin)
	{
		List<ConsultaOrdenInversionDto> listConsOrd = new ArrayList<ConsultaOrdenInversionDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listConsOrd;
		try
		{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			listConsOrd = inversionesService.obtenerMontoInvertidoXFecha(sNoEmpresa, sDivisa, sFecIni, sFecFin);
			
			if(listConsOrd!=null && listConsOrd.size()>0)
			{
				//Se crean las graficas con los datos obtenidos en la consulta.
				Map<String, Double> datos = new TreeMap<String, Double>();
				//Nombre de la grafica
				String sName = sNoEmpresa + sDivisa + sFecIni.replaceAll("/", "") + sFecFin.replaceAll("/", "");
				
				for(int i=0; i<listConsOrd.size(); i++){
					ConsultaOrdenInversionDto consOrdenInvDto = listConsOrd.get(i);
					
					if(datos.containsKey(consOrdenInvDto.getFecAlta().toString())) {
						double dImporte = datos.get(consOrdenInvDto.getFecAlta().toString());
						datos.remove(consOrdenInvDto.getFecAlta().toString());
						
						datos.put(consOrdenInvDto.getFecAlta().toString(), (consOrdenInvDto.getImporte()+dImporte));
					}
					else {
						datos.put(consOrdenInvDto.getFecAlta().toString(), consOrdenInvDto.getImporte());
					}
				}
				
				CreacionGrafica cg = new CreacionGrafica();
				cg.crearGraficaPie   (iUserId+"", "ReporteInversion"+sName, "Monto Invertido por Fecha", datos);
				cg.crearGraficaBarras(iUserId+"", "ReporteInversion"+sName, "Monto Invertido por Fecha", "Importe", datos, true);
				cg.crearGraficaLineas(iUserId+"", "ReporteInversion"+sName, "Monto Invertido por Fecha", "Importe", datos, true);
			}//Fin grafica
		}
		catch(Exception e){
			System.err.println(e);
			logger.error(e);
		}
		return listConsOrd;
	}
	
	@DirectMethod
	public String validaDatosLiqInv(String ctaPropia, String jSonString, String uImporte, String bancosCargo, String chequeraCargo) {
		Gson gson = new Gson();
		List<Map<String, String>> datosGrid = gson.fromJson(jSonString, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		double importe = Double.parseDouble(uImporte);
		double importeG = 0;
		if(!Utilerias.haveSession(WebContextManager.get()))
			return null;
		try {
			if(ctaPropia.equals("true")) {
				for(int i=0; i<datosGrid.size(); i++){
					importeG += Double.parseDouble(datosGrid.get(i).get("importeGrid"));
					
					if(datosGrid.get(i).get("clave").equals("") || datosGrid.get(i).get("chequera").equals(""))
						return "Datos incompletos, verifique por favor...";
				}
				if(importe != importeG)
					return "Los importes a cargar son diferentes al importe de la inversiï¿½n...";
			}else {
				if(bancosCargo.equals(""))
					return "Debe selecionar un banco de cargo...";
				if(chequeraCargo.equals(""))
					return "Debe selecionar una chequera de cargo...";
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Inversiones, C:InversionesAction, M:validaDatosLiqInv");
		}
		return "";
	}
	
	@DirectMethod
	public String modificarIntImpInv(String datosIntImp) {
		Gson gson = new Gson();
		List<Map<String, String>> intImpInv = gson.fromJson(datosIntImp, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		String resp = "";
		if(!Utilerias.haveSession(WebContextManager.get()))
			return resp;
		try {
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			resp = inversionesService.modificarIntImpInv(intImpInv);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Inversiones, C:InversionesAction, M:modificarIntImpInv");
		}
		return resp;
	}
	
	@DirectMethod
	public int validaFacultad(int facultad) {
		int resp = 0;
		if(!Utilerias.haveSession(WebContextManager.get()))
			return resp;
		try {
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			resp = inversionesService.validaFacultad(facultad);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Inversiones, C:InversionesAction, M:validaFacultad");
		}
		return resp;
	}
	
	/*Para inversiones*/
	@DirectMethod
	public List<LlenaComboGralDto> obtenerBancoOrdenInversion(String cveContrato, 
			String idEmpresa, String tipoChequera){
		List<LlenaComboGralDto> listBancos = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listBancos;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			listBancos = inversionesService.consultarBancoOrdenInversion(new Integer(cveContrato), new Integer(idEmpresa), tipoChequera);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, obtenerBancoOrdenInversion");
		}
		return listBancos;
	}

	@DirectMethod
	public List<LlenaComboGralDto> obtenerBancoOrdenIinstitucion(String idEmpresa, String cveContrato){
		List<LlenaComboGralDto> listBancos = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listBancos;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			listBancos = inversionesService.consultarBancoOrdenIinstitucion(new Integer(idEmpresa), new Integer(cveContrato));
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, obtenerBancoOrdenIinstitucion");
		}
		return listBancos;
	}

	@DirectMethod
	public List<LlenaComboGralDto> obtenerChequeraOrdenInversion(String idBanco, 
			String idEmpresa, String tipoChequera){
		List<LlenaComboGralDto> listBancos = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listBancos;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			listBancos = inversionesService.consultarChequeraOrdenInversion(new Integer(idBanco), new Integer(idEmpresa), tipoChequera);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, obtenerChequeraOrdenInversion");
		}
		return listBancos;
	}
	@DirectMethod
	public List<LlenaComboGralDto> obtenerChequeraOrdenInstitucion(String idBanco, 
			String idEmpresa, String cuenta){
		List<LlenaComboGralDto> listBancos = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listBancos;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			listBancos = inversionesService.consultarChequeraOrdenInstitucion(new Integer(idBanco), new Integer(idEmpresa), new Integer(cuenta));
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + e.getLocalizedMessage()
					+ "P:Inversiones, C:InversionesAction, obtenerChequeraOrdenInstitucion");
		}
		return listBancos;
	}

	@DirectFormPostMethod
	public Boolean insertarTmpInvSipo(Map<String, String> formParameters, Map<String, FileItem> fileFields){
		if(!Utilerias.haveSession(WebContextManager.get()))
			return null;
		try{
			String sufijo = formParameters.get( "txtPF");
			
			
			OrdenInversionDto ordenInversionDto = new OrdenInversionDto();
			String horaMinuto = formParameters.get(sufijo+"cmbHora");
			Integer hora = Integer.valueOf(horaMinuto.substring(0, horaMinuto.indexOf(":")));
			Integer minuto = Integer.valueOf(horaMinuto.substring(horaMinuto.indexOf(":")+1, horaMinuto.indexOf(" ")));
			
			ordenInversionDto.setFolio(0);
			ordenInversionDto.setFecAlta(funciones.ponerFechaDate(formParameters.get(sufijo+"txtFechaInversion")));
			ordenInversionDto.setHoraMinuto(formParameters.get(sufijo+"cmbHora"));
			ordenInversionDto.setHora(hora);
			ordenInversionDto.setTipoOrden("");
			ordenInversionDto.setContraparte("");
			ordenInversionDto.setImporte(Double.valueOf(formParameters.get(sufijo+"txtImporteInvertir").replaceAll(",", "")));
			ordenInversionDto.setInstrumento("");
			ordenInversionDto.setFecVenc(funciones.ponerFechaDate(formParameters.get(sufijo+"txtFechaVencimiento")));
			ordenInversionDto.setTasa(Double.valueOf(formParameters.get(sufijo+"txtTasa").replaceAll(",", "")));
			ordenInversionDto.setContrato(formParameters.get(sufijo+"cmbNumContrato").replaceAll(",", ""));
			ordenInversionDto.setPlazo(Integer.valueOf(formParameters.get(sufijo+"txtPlazo")));
			ordenInversionDto.setDiasAnual(Integer.valueOf(formParameters.get(sufijo+"hdPlazoInv")));
			
			if(formParameters.get(sufijo + "chkGarantia") != null && formParameters.get(sufijo + "chkGarantia").equals("on"))
				ordenInversionDto.setbGarantia("S");
			else
				ordenInversionDto.setbGarantia("N");
			
			ordenInversionDto.setIdPapel(formParameters.get(sufijo+"cmbPapel"));
			ordenInversionDto.setIdTipoValor(formParameters.get(sufijo+"txtTipoValor"));
			ordenInversionDto.setCveContrato(Integer.valueOf(formParameters.get(sufijo+"txtCveContrato")));
			ordenInversionDto.setNoContacto(0);
			ordenInversionDto.setMinuto(minuto);
			ordenInversionDto.setImpuestoEsperado(Double.valueOf(formParameters.get(sufijo+"txtImpuestoEsperado").replaceAll(",", "")));
			ordenInversionDto.setNoInstitucion(Integer.valueOf(formParameters.get(sufijo+"txtInstucion")));
			ordenInversionDto.setFactorImpuesto(Double.valueOf(formParameters.get(sufijo+"txtImpuesto").replaceAll(",", "")));
			ordenInversionDto.setInteres(Double.valueOf(formParameters.get(sufijo+"txtInteres").replaceAll(",", "")));
			ordenInversionDto.setMoneda(formParameters.get(sufijo+"txtInteres"));
			ordenInversionDto.setMoneda(formParameters.get(sufijo+"txtIdDivisa"));
			ordenInversionDto.setInstitucion(formParameters.get(sufijo+"txtNomInstitucion"));
			ordenInversionDto.setNoEmpresa(Integer.valueOf(formParameters.get(sufijo+"txtNoEmpresa")));
			
			ordenInversionDto.setIdBanco(Integer.valueOf(formParameters.get(sufijo+"txtBancoInv")));
			ordenInversionDto.setIdBancoReg(Integer.valueOf(formParameters.get(sufijo+"txtBancoReg")));
		
			if(Integer.valueOf(formParameters.get(sufijo+"hdFormaPago")) == 3 || ( Integer.valueOf(formParameters.get(sufijo+"hdFormaPago")) == 5)){
				ordenInversionDto.setIdChequeraInst(formParameters.get(sufijo+"cmbChequeraInst"));
			}else{
				
				ordenInversionDto.setIdBancoInst(Integer.valueOf(formParameters.get(sufijo+"txtBancoInstitucion")));
			}
			ordenInversionDto.setIdChequera(formParameters.get(sufijo+"cmbChequeraInv"));
			ordenInversionDto.setIdChequeraReg(formParameters.get(sufijo+"cmbChequeraReg"));
		
			
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			return inversionesService.insertarTmpInvSipo(ordenInversionDto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + e.getLocalizedMessage()
					+ "P:Inversiones, C:InversionesAction, obtenerChequeraOrdenInstitucion");
			e.printStackTrace();
			return false;
		}
	}
	
	@DirectMethod
	public List<OrdenInversionDto> consultarOrdenesInvPendientes(){
		List<OrdenInversionDto> lista = new ArrayList<OrdenInversionDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return lista;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			lista = inversionesService.consultarOrdenesInvPendientes();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, consultarOrdenesInvPendientes");
		}
		return lista;
	}
	
	@DirectMethod
	public Map<String, Object>insertarOrdenesInversion(String cadena){
		System.out.println("..::cadena"+cadena);
		Map<String, Object> mapRet = new HashMap<String, Object>();
		Map<String, Object> mapa = new HashMap<String, Object>();
		
		mapRet.put("msgUsuario", "");
		
		StringTokenizer st = new StringTokenizer(cadena, "|");
		
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			//mapRet = inversionesService.insertarOrdenInversion(dtoOrdenInv);
			String mensajeAcumulado = "";
			while(st.hasMoreTokens()){
				String sFolio = st.nextToken();
				System.out.println("..::va entrar a orden de invercion");
				System.out.println("..::"+sFolio);
				mapa = inversionesService.insertarOrdenInversion(new Integer(sFolio));
				
				mensajeAcumulado += mapa.get("msgUsuario");
				mensajeAcumulado += "\n";
			}
			
			mapRet.put("msgUsuario", mensajeAcumulado);
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:insertarOrdenInversion");
			e.printStackTrace();
		}
		return mapRet;

	}

	/**
	 * Este mï¿½todo obtiene realiza el reporte de LiquidacionDeInversion
	 * en LiquidacionDeInversion
	 * @param datos
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@DirectMethod
	public JRDataSource obtenerReporteVencimientoInversion(Map datos, ServletContext context){
		JRDataSource jrDataSource = null;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl" , context);
			jrDataSource = inversionesService.obtenerReporteVencimientonInversion(datos);
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:obtenerReporteLiquidacionInv");
		}
		return jrDataSource;
	}
	
	@DirectMethod
	public Map<String, Object> actualizarTmpInvSipo(String datos){
	    List<ConsultaOrdenInversionDto> listOrdenInv = new ArrayList<ConsultaOrdenInversionDto>(); 
	    List<BancoCheContratoDto> listBancoCargo = new ArrayList<BancoCheContratoDto>();
		Map<String, Object> mapRet = new HashMap<String, Object>();
		Gson gson = new Gson();
		
		//System.out.println("Datos "+ datos);
		
		if(!Utilerias.haveSession(WebContextManager.get()))
			return mapRet;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			List<Map<String, String>> gDatos = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			
			OrdenInversionDto dtoOrd = new OrdenInversionDto();
			dtoOrd.setFolioSeq(funciones.convertirCadenaInteger(gDatos.get(0).get("folioSeq").toString()));

			dtoOrd.setImporte(funciones.convertirCadenaDouble(gDatos.get(0).get("importe").toString().replaceAll(",", "")));
			dtoOrd.setTasa(funciones.convertirCadenaDouble(gDatos.get(0).get("tasa").toString()));
			dtoOrd.setFactorImpuesto(funciones.convertirCadenaDouble(gDatos.get(0).get("impuesto").toString().replaceAll(",", "")));
			dtoOrd.setInteres(funciones.convertirCadenaDouble(gDatos.get(0).get("interes").toString().replaceAll(",", "")));
			dtoOrd.setImpuestoEsperado(funciones.convertirCadenaDouble(gDatos.get(0).get("impuestoEsperado").toString().replaceAll(",", "")));
			dtoOrd.setFecVenc(funciones.ponerFechaDate(gDatos.get(0).get("fechaVence").toString()));
			dtoOrd.setFecAlta(funciones.ponerFechaDate(gDatos.get(0).get("fechaAlta").toString()));
			
			inversionesService.actualizarTmpInvSipo(dtoOrd);

			mapRet.put("msgUsuario", "Operación realizada con éxito.");
		}catch(Exception e){
			//System.out.println("Error en el action ");
			e.printStackTrace();
			mapRet.put("msgUsuario", e.getLocalizedMessage());
		}
		
		return mapRet;
	}
	
	@DirectMethod
	public Map<String, Object>eliminarTmpInvSipo(String cadena){
		Map<String, Object> mapRet = new HashMap<String, Object>();
		Map<String, Object> mapa = new HashMap<String, Object>();
		
		mapRet.put("msgUsuario", "");
		
		StringTokenizer st = new StringTokenizer(cadena, "|");
		
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			//mapRet = inversionesService.insertarOrdenInversion(dtoOrdenInv);
			String mensajeAcumulado = "";
			while(st.hasMoreTokens()){
				String sFolio = st.nextToken();
				if(inversionesService.eliminarTmpInvSipo(new Integer(sFolio)) == 1){
					mensajeAcumulado += "Se elminó de manera correcta la captura con folio "+ sFolio;
					mensajeAcumulado += "\n";
				}else{
					mensajeAcumulado += "No se pudo eliminar la captura con folio "+ sFolio;
					mensajeAcumulado += "\n";
				}
			}
			
			mapRet.put("msgUsuario", mensajeAcumulado);
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:insertarOrdenInversion");
			e.printStackTrace();
		}
		return mapRet;

	}

	@DirectMethod
	public boolean verificarFechaMayor(String sFecha, String sFechaReferencia){
		boolean bInhabil = false;
		if(!Utilerias.haveSession(WebContextManager.get()))
			return bInhabil;
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			bInhabil = inversionesService.verificarFechaMayor(sFecha, sFechaReferencia);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:verificarFechaInhabil");
		}
		return bInhabil;
	}

	@DirectMethod
	public List<Map<String, Object>> obtenerInversionesLiqVencidas(int usuario, int empresa, int institucion, String fechaInicial, String fechaFinal){
		Gson gson = new Gson();
		
		InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");

		
		return inversionesService.obtenerInversionesLiqVencidas(usuario, empresa, institucion, fechaInicial, fechaFinal);
	}
	
	@DirectMethod
	public String cancelarOrdenesInversionMultiple(String ordenes){
		int iRegAfec = 0;
		Gson gson = new Gson();
		StringBuffer mensaje = new StringBuffer();
		List<Map<String, String>> grid = gson.fromJson(ordenes, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		Map<String, Object> mapSAP = new HashMap<String, Object>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return "";
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
			
			mensaje.append("");
			
			for (int i = 0; i < grid.size(); i++) {
				mapSAP =inversionesService.cancelarOrdenesInversion(Integer.parseInt(grid.get(i).get("noOrden")),
						grid.get(i).get("estatus"),
						grid.get(i).get("poHeaders") != null && !grid.get(i).get("poHeaders").equals("") ?grid.get(i).get("poHeaders"):"");
				
				if (!(boolean)mapSAP.get("estatus")) {
					mensaje.append("<br>" + mapSAP.get("mensaje"));
				}
			}
			
			//}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:cancelarOrdenesInversion");
		}
		return mensaje.toString();
	}
	
	@DirectMethod
	public List<Map<String, Object>> obtenerInversionesVigentes(int usuario, int empresa){
		InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
		return inversionesService.obtenerInversionesVigentes(usuario, empresa);
	}
	
	public HSSFWorkbook obtenerExcelInversionesVigentes(int usuario, int empresa, ServletContext context){
		HSSFWorkbook wb = null;
		DatosExcel datosExcel = new DatosExcel();
		InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl", context);
		List <Map<String, Object>> listaResultado = inversionesService.obtenerInversionesVigentes(usuario, empresa);
		String [] headers = {"Empresa", "Chequera", "Orden", "Importe","Fecha Ini", "Fecha Vto.", "Plazo", "Tasa","Interés Bruto", "Impuesto Ret.", "Interés Neto", "Monto Vto."};
		String [] keys = {"RAZON_SOCIAL", "ID_CHEQUERA", "NO_ORDEN", "IMPORTE", "FEC_ALTA", "FEC_VENC", "PLAZO", "TASA", "INTERES", "ISR", "INTERES_NETO", "MONTO_NETO"};

		datosExcel.setHeaders(headers);
		datosExcel.setKeys(keys);
		datosExcel.setData(listaResultado);
		
		List<DatosExcel> d = new ArrayList<DatosExcel>();
		
		d.add(datosExcel);
		
		wb = Utilerias.generarExcel(d, "Inversiones Vigentes");
		
		return wb;
	}
	
	
	
	
	@DirectMethod
	public String consultaCajas() {
		
	
		String resultado = "";
			
		if(!Utilerias.haveSession(WebContextManager.get()) || !(Utilerias.tienePermiso(WebContextManager.get(), 109)||Utilerias.tienePermiso(WebContextManager.get(), 110)))
			return resultado;
		try {

			
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl");
					resultado = inversionesService.consultaCajas();
		System.out.println(resultado+"::..Resultado");
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:inversiones, C:InversionesAction, M: consultaCajas");
		}
		
		return resultado;
	}
	

	
	
	
	

}
