package com.webset.set.inversiones.business;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

import org.apache.axis.AxisFault;
import org.apache.log4j.Logger;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.webset.set.egresos.dto.ConfirmacionCargoCtaDto;
import com.webset.set.financiamiento.dto.ControlPagosPasivos;
import com.webset.set.inversiones.dao.InversionesDao;
import com.webset.set.inversiones.dao.RepInversionesDao;
import com.webset.set.inversiones.dto.BancoCheContratoDto;
import com.webset.set.inversiones.dto.ComunInversionesDto;
import com.webset.set.inversiones.dto.ConsultaOrdenInversionDto;
import com.webset.set.inversiones.dto.CtasContratoDto;
import com.webset.set.inversiones.dto.CuentaPropiaDto;
import com.webset.set.inversiones.dto.LiquidaInversionesDto;
import com.webset.set.inversiones.dto.MovimientoDto;
import com.webset.set.inversiones.dto.OrdenInversionDto;
import com.webset.set.inversiones.dto.ParamReportesDto;
import com.webset.set.inversiones.dto.ParametroDto;
import com.webset.set.inversiones.dto.RetencionDto;
import com.webset.set.inversiones.dto.RubroDto;
import com.webset.set.inversiones.dto.TraspasosDto;
import com.webset.set.inversiones.middleware.service.InversionesService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.CatCtaBancoDto;
import com.webset.set.utilerias.dto.CatPapelDto;
import com.webset.set.utilerias.dto.CatTipoValorDto;
import com.webset.set.utilerias.dto.CuentaDto;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.RevividorDto;

import mx.com.gruposalinas.Poliza.DT_Polizas_OBPolizas;
import mx.com.gruposalinas.Poliza.DT_Polizas_ResponseResponse;
import mx.com.gruposalinas.Poliza.SOS_PolizasBindingStub;
import mx.com.gruposalinas.Poliza.SOS_PolizasServiceLocator;

public class InversionesBusinessImpl implements InversionesService{
	private InversionesDao inversionesDao;
	private RepInversionesDao repInversionesDao;
	private Bitacora bitacora = new Bitacora();
	private GlobalSingleton globalSingleton;
	private Funciones funciones = new Funciones();
	//Declaraciï¿½n de constantes
	private static final String FOLIOCUENTA = "no_cuenta";
	private static final String NUMPRODUCT = "1";
	private static final String ID_STATUS_CUENTA = "V";
	private static final String ID_TIPO_CUENTA = "C";
	private static final int I_APLICA = 1;
	private static final int I_FORMA_PAGO = 3;
	private static final String IS_ORIGEN_MOV = "INV";
	private static final String IS_SALVO_BCOBRO = "S";
	private static final int I_TIPO_DOCTO = 61;
	private static final String IS_STATUS_REG = "P";
	private static final String FOLIO_ORDEN = "no_orden";
	private static Logger logger = Logger.getLogger(InversionesBusinessImpl.class);

	public List<LlenaComboGralDto> llenarCombosBancosDep(int iInstitucion, String sIdDivisa, int noEmpresa){
		List<LlenaComboGralDto> listBanc = new ArrayList<LlenaComboGralDto>();
		
		try{
			listBanc = inversionesDao.consultarBancosDep(noEmpresa, iInstitucion, sIdDivisa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:llenarCombosBancosDep");
		}
		return listBanc;
	}
	
	public List<ConfirmacionCargoCtaDto> llenarComboInstitucion(){
		List<ConfirmacionCargoCtaDto> listInst = new ArrayList<ConfirmacionCargoCtaDto>();
		try{
			listInst = inversionesDao.consultarInstitucion();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:llenarComboInstitucion");
		}
		return listInst;
	}
	
	public List<CuentaDto> consultarContratos(int noEmpresa){
		List<CuentaDto> listConsCon = new ArrayList<CuentaDto>();
		
		try{
			listConsCon = inversionesDao.consultarContratos(noEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:consultarContratos");
		}
		return listConsCon;
	}
	
	/**
	 * Este mï¿½todo crea la lista de contactos asignadas
	 * a un contrato
	 * @param iContacto1
	 * @param iContacto2
	 * @param iContacto3
	 * @return
	 */
	public List<LlenaComboGralDto> obtenerNombresContactos(int iContacto1, int iContacto2, int iContacto3){
		String desc1 = "";
		String desc2 = "";
		String desc3 = "";
		List<LlenaComboGralDto> listContac = new ArrayList<LlenaComboGralDto>();
		try{
			LlenaComboGralDto dto = new LlenaComboGralDto();
			if(iContacto1 != 0)
			{
				desc1 = inversionesDao.consultarContactos(iContacto1);
				dto = new LlenaComboGralDto();
				dto.setId(iContacto1);
				dto.setDescripcion(desc1);
				listContac.add(dto);
			}
			if(iContacto2 != 0)
			{
				desc2 = inversionesDao.consultarContactos(iContacto2);
				dto = new LlenaComboGralDto();
				dto.setId(iContacto2);
				dto.setDescripcion(desc2);
				listContac.add(dto);
			}
				
			if(iContacto3 != 0)
			{
				desc3 = inversionesDao.consultarContactos(iContacto3);
				dto = new LlenaComboGralDto();
				dto.setId(iContacto3);
				dto.setDescripcion(desc3);
				listContac.add(dto);
			}
			
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:obtenerNombresContactos");
		}
		return listContac;
	}
	
	/**
	 * Este mï¿½todo es utilizado para llenar el grid de bancos-chequeras
	 * asignadas en los contratos en mantenimiento de contratos
	 * @param iNoCuenta
	 * @return
	 */
	public List<BancoCheContratoDto> llenarGridBancosContrato(int iNoCuenta, int noEmpresa)
	{
		List<BancoCheContratoDto> listBanChe = new ArrayList<BancoCheContratoDto>();
		int iNoLinea = 0;
		
		try{
			iNoLinea = inversionesDao.consultarLineaEmpresa(noEmpresa);
			listBanChe = inversionesDao.consultarBancosContrato(noEmpresa, iNoLinea, iNoCuenta);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:llenarGridBancosContrato");
		}
		return listBanChe;
	}
	
	public List<LlenaComboGralDto> consultarContactosInstitucion(int iInstitucion){
		List<LlenaComboGralDto> listContac = new ArrayList<LlenaComboGralDto>();
		try{
			listContac = inversionesDao.consultarContactosInstitucion(iInstitucion);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:consultarContactosInstitucion");
		}
		return listContac;
	}
	
	/**
	 * Este mï¿½todo obtiene las chequeras asiganadas a un banco y a una
	 * empresa, es utilizado para llenar combo chequeras, es utilizado en
	 * MantenimientoDeContratos
	 * @param iIdBanco
	 * @param iIns
	 * @return
	 */
	public List<LlenaComboChequeraDto> obtenerChequeras(int iIdBanco, int iIns, String sIdDivisa, int noEmpresa){
		List<LlenaComboChequeraDto> listCheq = new ArrayList<LlenaComboChequeraDto>();
		
		try{
			listCheq = inversionesDao.consultarChequeras(iIdBanco, noEmpresa, iIns, sIdDivisa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:consultarChequeras");
		}
		return listCheq;
	}
	
	@SuppressWarnings("unchecked")
	public Map insertarModificarContratos(boolean bNuevo, boolean bModifi, CuentaDto dtoCuenta, List<CtasContratoDto> listCtasCon, int noEmpresa){
		Map<String, Object> mapRet = new HashMap<String, Object>();
		int iInsert = 0;
		int iModifi = 0;
		int iModifiCtas = 0;
		int iLineaEmpresas = 0;
		int iNoEmpresa = 0;
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			iNoEmpresa = noEmpresa;//globalSingleton.getUsuarioLoginDto().getNumeroEmpresa();
			iLineaEmpresas = inversionesDao.consultarLineaEmpresa(iNoEmpresa);
			if(bNuevo && !bModifi)
			{
				iInsert = this.insertarContratos(dtoCuenta, listCtasCon, iNoEmpresa);
				if(iInsert > 0)
					mapRet.put("msgUsuario", "Datos Registrados, La Clave del contrato es: " + iInsert);
			}
			else if(!bNuevo && bModifi)
			{
				iModifi = this.modificarContratos(dtoCuenta, iNoEmpresa);
				for(int i = 0; i < listCtasCon.size(); i ++)
				{	
					CtasContratoDto dtoCtas = new CtasContratoDto();
						dtoCtas.setIdBanco(listCtasCon.get(i).getIdBanco());
						dtoCtas.setIdChequera(listCtasCon.get(i).getIdChequera());
						dtoCtas.setNoCuenta(dtoCuenta.getNoCuenta());
						dtoCtas.setNoEmpresa(iNoEmpresa);
						dtoCtas.setNoLinea(iLineaEmpresas);
					if(listCtasCon.get(i).getM() != null && listCtasCon.get(i).getM().trim().equals("N"))
					{
						logger.info("bancoChe  " + listCtasCon.get(i).getM() + "banc " + listCtasCon.get(i).getIdBanco());
						iModifiCtas = inversionesDao.insertarCuentas(dtoCtas);
					}
					else if(listCtasCon.get(i).getM() != null && listCtasCon.get(i).getM().trim().equals("E"))
					{
						logger.info("bancoChe  " + listCtasCon.get(i).getM() + "banc " + listCtasCon.get(i).getIdBanco());
						iModifiCtas = inversionesDao.eliminarCuentasContrato(dtoCtas);
					}
				}
				if(iModifi > 0 || iModifiCtas > 0)
					mapRet.put("msgUsuario", "Datos modificados correctamente");
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:insertarModificarContratos");
			mapRet.put("msgUsuario", "Ocurrio un error durante la operaciï¿½n");
		}
		return mapRet;
	}
	
	public int insertarContratos(CuentaDto dtoCuenta, List<CtasContratoDto> listCtasCon, int iNoEmpresa){
		int iFolioCuenta = 0;
		int iLineaEmpresas = 0;
		int iAfec1 = 0;
		int iAfec2 = 0;
		
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			iFolioCuenta = this.obtenerFolioReal(FOLIOCUENTA);
			iLineaEmpresas = inversionesDao.consultarLineaEmpresa(iNoEmpresa);
			//Se agregan valores al dto que viene desde la vista 
			dtoCuenta.setNoEmpresa(iNoEmpresa);
			dtoCuenta.setNoProducto(Integer.parseInt(NUMPRODUCT));
			dtoCuenta.setUsuarioAlta(globalSingleton.getUsuarioLoginDto().getIdUsuario());
			dtoCuenta.setNoLinea(iLineaEmpresas);
			dtoCuenta.setNoCuenta(iFolioCuenta);
			dtoCuenta.setIdEstatusCta(ID_STATUS_CUENTA);
			dtoCuenta.setIdTipoCuenta(ID_TIPO_CUENTA);
             
			iAfec1 = inversionesDao.insertarContratos(dtoCuenta);
			
			for(int i = 0; i < listCtasCon.size(); i++)
			{
				if(listCtasCon.get(i).getM().trim().equals("N"))
				{
					listCtasCon.get(i).setNoEmpresa(iNoEmpresa);
					listCtasCon.get(i).setNoLinea(iLineaEmpresas);
					listCtasCon.get(i).setNoCuenta(iFolioCuenta);
					iAfec2 = inversionesDao.insertarCuentas(listCtasCon.get(i));
				}
			}
			
			if(iAfec1 > 0 && iAfec2 >0)
				return iFolioCuenta;
			else
				return -1;
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:insertarContratos");
			return -1;
		}
	}
	
	public int obtenerFolioReal(String sTipo){
		int iFolio = 0;
		
		try {
			inversionesDao.actualizarFolioReal(sTipo);
			iFolio = inversionesDao.seleccionarFolioReal(sTipo);
			return iFolio;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:obtenerFolioReal");
			return -1;
		}
	}
	
	@SuppressWarnings("unchecked")
	public Map eliminarContratos(int iNoCuenta, int noEmpresa){
		Map<String, Object> mapRet = new HashMap<String, Object>();
		List<OrdenInversionDto> listOrdenInv =  new ArrayList<OrdenInversionDto>();
		
		try{
			int iAfecCon = 0;
			int iAfecCue = 0;
			
			listOrdenInv = inversionesDao.consultarOrdenInversion(iNoCuenta, noEmpresa);
			if(listOrdenInv.size() > 0)
			{
				mapRet.put("msgUsuario","Este contrato no puede ser eliminado ya que tiene ordenes pendientes");
				return mapRet;
			}
			
			iAfecCon = inversionesDao.eliminarContratos(iNoCuenta, noEmpresa);
			iAfecCue = inversionesDao.eliminarCuentasContrato(iNoCuenta, noEmpresa);
			
			if(iAfecCon > 0 && iAfecCue > 0)
				mapRet.put("msgUsuario", "Datos eliminados correctamente");
			else
				mapRet.put("msgUsuario", "Ocurrio un error al eliminar");
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:eliminarContratos");
			mapRet.put("msgUsuario", "Ocurrio una excepciï¿½n al eliminar");
		}
		return mapRet;
	}
	
	/**
	 * Este mï¿½todo es se utiliza para realizar modificaciones en contratos
	 * se utiliza en MantenimientoDeContratos.js
	 * @param dtoCuenta
	 * @return
	 */
	public int modificarContratos(CuentaDto dtoCuenta, int iNoEmpresa){
		int iAfec= 0;
		
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			dtoCuenta.setUsuarioModif(globalSingleton.getUsuarioLoginDto().getIdUsuario());
			dtoCuenta.setNoEmpresa(iNoEmpresa);
			iAfec = inversionesDao.modificarContratos(dtoCuenta);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:modificarContratos");
			return -1;
		}
		return iAfec;
	}
	
	/**
	 * Este metodo obtiene los contratos en OrdenDeInversion
	 * @param bInternas
	 * @return
	 */
	public List<CuentaDto> obtenerNumeroContratos(boolean bInternas, int noEmpresa)
	{
		List<CuentaDto> listConsContratos = new ArrayList<CuentaDto>();
		
		try{
			listConsContratos = inversionesDao.consultarNumeroContratos(noEmpresa, bInternas);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:obtenerNumeroContratos");
		}
		return listConsContratos;
	}
	
	/**
	 * Este mï¿½todo obtiene el tipo de valor y es utilizado
	 * en OrdenDeInversion.js
	 * @param sIdDivisa
	 * @return
	 */
	public List<CatTipoValorDto> obtenerTipoValor(String sIdDivisa)
	{
		List<CatTipoValorDto> listTipoVal = new ArrayList<CatTipoValorDto>();
		try{
			listTipoVal = inversionesDao.consultarTipoValor(sIdDivisa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:consultarTipoValor");
		}
		return listTipoVal;
	}
	
	/**
	 * Este mï¿½todo obtiene el nï¿½mero y nombre del contacto
	 * de la tabla persona, es utilizado en OrdenDeInversion.js
	 * @param iContacto1
	 * @param iContacto2
	 * @param iContacto3
	 * @return
	 */
	public List<LlenaComboGralDto> llenarComboContactos(int iContacto1, int iContacto2, int iContacto3)
	{
		List<LlenaComboGralDto> listCont = new ArrayList<LlenaComboGralDto>();
		try{
			listCont = inversionesDao.consultarIdNomContactos(iContacto1, iContacto2, iContacto3);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:llenarComboContactos");
		}
		return listCont;
	}

	/**
	 * Este mï¿½todo obtiene el id y tipo valor del papel con respecto al tipo de valor,
	 * es utilizado en OrdenDeInversion
	 * @param sIdTipoValor
	 * @return
	 */
	public List<CatPapelDto> obtenerPapel(String sIdTipoValor)
	{
		List<CatPapelDto> listPapel = new ArrayList<CatPapelDto>();
		try{
			listPapel = inversionesDao.consultarPapel(sIdTipoValor);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:consultarPapel");
		}
		return listPapel;
	}
	
	/**
	 * Este mï¿½todo obtiene la empresa concentradora, es utilizado en
	 * OrdenDeInversion
	 * @return
	 */
	public List<LlenaComboGralDto> obtenerEmpresaConcentradora(){
		List<LlenaComboGralDto> listLlenaCombo = new ArrayList<LlenaComboGralDto>();
		try{
			listLlenaCombo = inversionesDao.consultarEmpresaConcentradora();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:consultarEmpresaConcentradora");
		}
		return listLlenaCombo;
	}
	
	public List<LlenaComboGralDto> obtenerBancosMonitor(String sIdDivisa, String sBancos, int noEmpresa){
		List<LlenaComboGralDto> listBancos = new ArrayList<LlenaComboGralDto>();
		
		try{
			listBancos = inversionesDao.consultarBancosMonitor(noEmpresa, sIdDivisa, sBancos);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:consultarBancosMonitor");
		}
		return listBancos;
	}
	
	/**
	 * Este metodo es utilizado para obtener las chequeras I ï¿½ M
	 * es utilizado en OrdendeInversion, (frminversiones)
	 * @param iIdEmpresa
	 * @param sIdDivisa
	 * @param iIdBanco
	 * @return
	 */
	public List<LlenaComboGralDto> obtenerChequerasMonitor(int iIdEmpresa, String sIdDivisa, int iIdBanco){
		List<LlenaComboGralDto> listChe = new ArrayList<LlenaComboGralDto>();
		try{
			listChe = inversionesDao.consultarChequerasMonitor(iIdEmpresa, sIdDivisa, iIdBanco);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:consultarBancosMonitor");
		}
		return listChe;
	}
	
	/**
	 * Este mï¿½todo obtiene el saldo inicial,
	 * es utilizado en OrdenDeInversion
	 * @param dto
	 * @return
	 */
	public double obtenerSaldoInicial(ComunInversionesDto dto){
		double uSaldoInicial = 0;
		List<CatCtaBancoDto> listSaldo = new ArrayList<CatCtaBancoDto>();
		try{
			listSaldo = inversionesDao.consultarSaldoInicialMonitor(dto);
			uSaldoInicial = listSaldo.size() > 0 ? listSaldo.get(0).getSaldoInicial() : 0;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Inversiones, C:InversionesBusinessImpl, M:obtenerSaldoInicial");
		}
		return uSaldoInicial;
	}
	
	/**
	 * Este mï¿½todo sirve para llenar el grid Barrido/Fondeo, aqui ae hacen una modificadiones
	 * para ser presentadas en la vista
	 * es utilizado en OrdenDeInversion
	 * llenarBarrido
	 * @param dto
	 * @return
	 */
	public List<MovimientoDto> obtenerBarrido(ComunInversionesDto dto){
		List<MovimientoDto> listBarrido = new ArrayList<MovimientoDto>();
		try{
			listBarrido = inversionesDao.consultarBarridoMonitor(dto);
			for(int i = 0; i < listBarrido.size(); i ++)
			{
				if(listBarrido.get(i).getImporte() != 0)
				{
					if(listBarrido.get(i).getIdTipoOperacion() == 3705)
						listBarrido.get(i).setConcepto("Barrido");
					else if(listBarrido.get(i).getIdTipoOperacion() == 3709)
						listBarrido.get(i).setConcepto("Pago de prestamo");
					
					listBarrido.get(i).setSBandera("BARRIDO");
				}
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:obtenerBarrido");
		}
		return listBarrido;
	}
	
	/**
	 * Este mï¿½todo obtiene el fondeo, para realizar el llenado del grid Barrido/fondeo
	 * que es utilizado en OrdenDeInversion
	 * @param dto
	 * @return
	 */
	public List<MovimientoDto> obtenerFondeo(ComunInversionesDto dto)
	{
		List<MovimientoDto> listFondeo =  new ArrayList<MovimientoDto>();
		try{
			listFondeo = inversionesDao.consultarFondeoMonitor(dto);
			for(int i = 0; i < listFondeo.size(); i ++)
			{
				if(listFondeo.get(i).getImporte() != 0)
				{
					if(listFondeo.get(i).getIdTipoOperacion() == 3706)
						listFondeo.get(i).setConcepto("Fondeo");
					else if(listFondeo.get(i).getIdTipoOperacion() == 3708)
						listFondeo.get(i).setConcepto("Prestamo");
					
					listFondeo.get(i).setSBandera("FONDEO"); //se agrega esta bandera ya que son parte de un mismo grid
															//y se requiere identificarlos posteriormente
				}
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:obtenerFondeo");
		}
		return listFondeo;
	}
	
	/**
	 * Este metodo obtiene el importe de orden_inversion
	 * sumandole el interes y restandole el impuesto
	 * @param dto
	 * @return
	 */
	public List<OrdenInversionDto> obtenerVencimientos(ComunInversionesDto dto){
		List<OrdenInversionDto> listVenc = new ArrayList<OrdenInversionDto>();
		try{
			listVenc = inversionesDao.consultarVencimientoMonitor(dto);
			for(int i = 0; i < listVenc.size(); i ++)
				listVenc.get(i).setImporte(listVenc.get(i).getImporte() + listVenc.get(i).getInteres() - listVenc.get(i).getIsr());
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:obtenerBarrido");
		}
		return listVenc;
	}
	
	/**
	 * Este mï¿½todo consulta a hist_cat_cta_banco y cat_cat_banco
	 * es utilizado en OrdenDeInversion
	 * @param dto
	 * @return
	 */
	public List<CatCtaBancoDto> obtenerPorInvertir(ComunInversionesDto dto){
		List<CatCtaBancoDto> listPorInv = new ArrayList<CatCtaBancoDto>();
		try{
			listPorInv = inversionesDao.consultarPorInvertirMonitor(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:obtenerPorInvertir");
		}
		return listPorInv;
	}
	
	/**
	 * Este metodo obtiene las inversiones de la tabla Movimiento, 
	 * es utilizado en OrdenDeInversion
	 * @param dto
	 * @return
	 */
	public List<MovimientoDto> obtenerInversiones(ComunInversionesDto dto){
		List<MovimientoDto> listInv = new ArrayList<MovimientoDto>();
		try{
			listInv = inversionesDao.consultarInversionesMonitor(dto);
			
			for(int i = 0; i < listInv.size(); i ++)
			{
				if(listInv.get(i).getIdEstatusMov().trim().equals("C"))
					listInv.get(i).setIdEstatusMov("CAPTURADA");
				else if(listInv.get(i).getIdEstatusMov().trim().equals("L"))
					listInv.get(i).setIdEstatusMov("LIQUIDADA");

			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:obtenerPorInvertir");
		}
		return listInv;
	}
	
	public boolean verificarFechaInhabil(String sFecha){
		boolean bInhabil = false;
		try{
			bInhabil = inversionesDao.consultarFechaInhabil(sFecha);
			
			Date fecha = funciones.ponerFechaDate(sFecha);
			
			Calendar c = Calendar.getInstance();
			
			c.setTime(fecha);
			
			int dia = c.get(Calendar.DAY_OF_WEEK);
			
			if(!bInhabil && (dia == Calendar.SATURDAY || dia == Calendar.SUNDAY))
				bInhabil = true;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:verificarFechaInhabil");
		}
		return bInhabil;
	}
	
	@SuppressWarnings("unchecked")
	public Map insertarOrdenInversion(OrdenInversionDto dto){
		Map<String,Object> mapRet = new HashMap<String, Object>();
		Map<String,Object> mapGen = new HashMap<String, Object>();
		int iRegOrAfec = 0;
		int iRegPaAfec = 0;
		int iFolioOrden = 0;
		int iFolioInv = 0;
		double uCurva = 0;
		double uPrimera = 0;
		double uConst = 0;
		double uPotencia = 0;
		double uTipoCambio = 0;
		BigDecimal bdCurva;
		List<OrdenInversionDto> listConsOrdenInv = new ArrayList<OrdenInversionDto>();
		//int iNoEmpresa = 0;
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			
			uPotencia = 28 / dto.getPlazo();
			uConst = 36000;
		    uPrimera = ((Math.pow((((dto.getTasa() / uConst) * dto.getPlazo()) + 1 ), uPotencia) - 1) * uConst / 28);
		    bdCurva = new BigDecimal(uPrimera);
		    uCurva = bdCurva.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
		    
		    uTipoCambio = inversionesDao.consultarTipoCambio(dto.getIdDivisa());
			listConsOrdenInv = inversionesDao.consultarOrdenInversionDos(dto.getNoAvisoRef(),dto.getNoEmpresa(),
					dto.getNoOrden(), dto.getGrupoInversion(), globalSingleton.getFechaHoy());
			
			if(listConsOrdenInv.size() <= 0)
			{   
				iFolioOrden = this.obtenerFolioReal(FOLIO_ORDEN);
				//Se agregan datos faltantes al dto para el insert
				dto.setNoOrden("" + iFolioOrden);
				dto.setGrupoInversion(iFolioOrden);
				//dto.setNoEmpresa(iNoEmpresa);
				dto.setFecAlta(globalSingleton.getFechaHoy());
				dto.setUsuarioAlta(globalSingleton.getUsuarioLoginDto().getIdUsuario());
				dto.setTasaCurva28(uCurva);
				dto.setTipoOrden("E");
				dto.setNuevoInversion(globalSingleton.obtenerValorConfiguraSet(268).toString().equals("SI"));
				iRegOrAfec = inversionesDao.insertarOrdenInversion(dto);
			}
			else
			{
				mapRet.put("msgUsuario", "Ya existe la orden ");
				return mapRet;
			}
			
			Integer formaPago = 
					this.inversionesDao.consultarFormaPagoContrato(dto.getNoCuenta(), dto.getNoEmpresa());
			
			iFolioInv = this.obtenerFolioReal("no_folio_param");
			ParametroDto dtoInsParam = new ParametroDto();
			dtoInsParam.setNoFolioParam(iFolioInv);
			dtoInsParam.setNoEmpresa(dto.getNoEmpresa());
			dtoInsParam.setIdTipoOperacion(4000);
			dtoInsParam.setIdFormaPago(formaPago);
			dtoInsParam.setNoCuenta(dto.getNoCuenta());
			dtoInsParam.setImporte(dto.getImporte());
			dtoInsParam.setImporteOriginal(dto.getImporte());
			dtoInsParam.setFecValor(dto.getFecVenc());
			dtoInsParam.setFecValorOriginal(dto.getFecVenc());
			dtoInsParam.setFecAlta(globalSingleton.getFechaHoy());
			dtoInsParam.setFecOperacion(globalSingleton.getFechaHoy());
			dtoInsParam.setGrupo(iFolioInv);
			dtoInsParam.setNoDocto("" + iFolioOrden);//checar
			dtoInsParam.setNoFolioMov(0);
			dtoInsParam.setFolioRef(0);
			dtoInsParam.setIdEstatusMov("O");
			dtoInsParam.setSecuencia(0);
			dtoInsParam.setConcepto("ORDEN INVERSION");
			dtoInsParam.setAplica(1);
			dtoInsParam.setDiasInv(dto.getPlazo());
			dtoInsParam.setUsuarioAlta(globalSingleton.getUsuarioLoginDto().getIdUsuario());
			dtoInsParam.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
			dtoInsParam.setIdEstatusReg(IS_STATUS_REG);
			dtoInsParam.setIdDivisa(dto.getIdDivisa());
			dtoInsParam.setValorTasa(dto.getTasa());
			dtoInsParam.setOrigenMov("INV");
			dtoInsParam.setBSalvoBuenCobro(IS_SALVO_BCOBRO);
			dtoInsParam.setTipoCambio(uTipoCambio);
			
			iRegPaAfec = inversionesDao.insertarParametro(dtoInsParam);
			/*    
			 *   FunSQLInsert189(sFolio, plIdEmpresa, sTipoOperacion, _
                                              IS_FORMA_PAGO, sDiasInv, GI_USUARIO, _
                                              IS_TIPO_DOCTO, sNumOrden, sGrupo, _
                                              sFolioDet, sFolioRef, sFecha, sNumCta, _
                                              Format(GT_FECHA_HOY, "dd/mm/yyyy"), _
                                              Format(sImporte, "#0.#0"), txtTasa.Text, _
                                              GI_ID_CAJA, Trim(txtDivisa.Text), _
                                              psAplica, sEstatusMov, IS_SALVO_BCOBRO, _
                                              IS_STATUS_REG, sBcoBeneficiario, _
                                              sCtaBeneficiario, sSecuencia, sConcepto, _
                                              sOrigenMov, pdTipoCambio)
			 *   
			 *    INSERT INTO parametro ("
    sSQL = sSQL & "     no_folio_param, no_empresa, id_tipo_operacion, id_forma_pago, "
    sSQL = sSQL & "     dias_inv, usuario_alta, id_tipo_docto, no_docto, grupo, "
    sSQL = sSQL & "     no_folio_mov, folio_ref, fec_valor, fec_valor_original, "     'Fecha
    sSQL = sSQL & "     no_cuenta, fec_operacion, fec_alta, importe, valor_tasa, "
    sSQL = sSQL & "     importe_original, tipo_cambio, id_caja, id_divisa, aplica, "
    sSQL = sSQL & "     id_estatus_mov, b_salvo_buen_cobro, id_estatus_reg, "
			 *   
			 *   QueryParametros(CStr(lFolioInv), "4000", _
                                   mtaContrato(1, cmbContrato.ListIndex + 1), _
                                   Format(txtImporte.Text, "#.0#"), _
                                   Format(tFechaValor, "dd/mm/yyyy"), sGrupoParametro, plError, , , _
                                   CStr(lFolioOrden), , , "O", , "ORDEN INVERSION", "1", _
                                   Trim(txtPlazo.Text), txtTasa.Text, "INV", _
                                   pdTipoCambioActual)
			 * 
			 * sSQL = sSQL & "
    
    ByVal sFolio As String, ByVal sTipoOperacion, _
                                 ByVal sNumCta As String, ByVal sImporte As String, _
                                 ByVal sFecha As String, ByVal sGrupo As String, _
                                 lerror As Long, Optional sBcoBeneficiario As String = "", _
                                 Optional sCtaBeneficiario As String = "", _
                                 Optional sNumOrden As String = "", _
                                 Optional sFolioDet As String = "0", _
                                 Optional sFolioRef As String = "0", _
                                 Optional sEstatusMov As String = "D", _
                                 Optional sSecuencia As String = "", _
                                 Optional sConcepto As String = "", _
                                 Optional psAplica As String = "1", _
                                 Optional sDiasInv As String = "", _
                                 Optional sValorTasa As String = "", _
                                 Optional sOrigenMov As String, _
                                 Optional pdTipoCambio As Double = 1#, _
                                 Optional plIdEmpresa As Long = 0#) As Long*/
			GeneradorDto dtoGen = new GeneradorDto();
				dtoGen.setEmpresa(dto.getNoEmpresa());
				dtoGen.setFolParam(iFolioInv);
				dtoGen.setFolMovi(iFolioInv);
				dtoGen.setFolDeta(0);
				dtoGen.setResult(0);
				dtoGen.setMensajeResp("inicia generador");
				dtoGen.setNomForma("OrdenDeInversion.js");
				dtoGen.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
			mapGen = inversionesDao.ejecutarGenerador(dtoGen);
			
			if(Integer.parseInt(mapGen.get("result").toString()) != 0)
			{
				mapRet.put("msgUsuario", "Error en generador: " + mapGen.get("result").toString() + " operacion 4000");
				return mapRet;
			}
			if(iRegOrAfec > 0 && iRegPaAfec > 0)
				mapRet.put("msgUsuario", "Datos registrados con el folio: " + iFolioOrden);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:insertarOrdenInversion");
		}
		return mapRet;
	}
	
	@SuppressWarnings("unchecked")
	public Map calcularInteresImpuesto(double uCapital, double uTasaImpuesto, int iPlazo, int iDiasAnual, 
			int iCuenta, int iNoInstitucion, String sContrato, String cisr)
	{
		Map<String, Object> mapInIm = new HashMap<String, Object>();
		double uInteres = 0;
		double uImpuesto = 0;
		double uImpuestoEsperado = 0;
		double uNeto = 0;
		String bIsr ="";
		try{
			
			bIsr = this.consultarbIsr(cisr);
			
			if(bIsr.trim().equals("S"))
			{
				uImpuesto = this.calcularIsr(iPlazo, inversionesDao.verificarManejaAnioBisiesto(iNoInstitucion), sContrato, iCuenta);
				uImpuestoEsperado = this.calcularInteres(uCapital, uImpuesto, iPlazo, iDiasAnual);
			}
			uInteres = this.calcularInteres(uCapital, uTasaImpuesto, iPlazo, iDiasAnual);
			uNeto = uCapital + uInteres;
			uNeto = uNeto - uImpuestoEsperado;
			uNeto = Math.rint(uNeto*100)/100;
			
			mapInIm.put("interes", uInteres);
			mapInIm.put("impuesto", uImpuesto);
			mapInIm.put("impuestoEsperado", uImpuestoEsperado);
			mapInIm.put("neto", uNeto);
			
			//System.out.println("El neto es "+ mapInIm.get("neto"));
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:calcularInteresImpuesto");
		}
		return mapInIm;
	}
	
	/**
	 * Este mï¿½todo retorna el interes, es utilizado en OrdenDeInversion.js
	 */
	public double calcularInteres(double uCapital, double uTasaImpuesto, int iPlazo, int iDiasAnual){
		double uInteres = 0;
		try{
			//System.out.println("borrar interes : " + (uCapital * ((uTasaImpuesto / 100) / iDiasAnual) * iPlazo));
			
			uInteres = (uCapital * ((uTasaImpuesto / 100) / iDiasAnual) * iPlazo );
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:calcularInteres");
			uInteres = -1;
		}
		return uInteres;
	}
	
	public double calcularIsr(int iPlazo, boolean bAplicaBisiesto, String sContrato, int iCuenta){
		List<RetencionDto> listImp = new ArrayList<RetencionDto>();
		globalSingleton = GlobalSingleton.getInstancia();
		double uIsr = 0;
		
		try {
			listImp = inversionesDao.consultarIsrRetencion(iPlazo, sContrato, iCuenta);
			
			if(listImp.size() > 0) {
				if(listImp.get(0).getCondicionAlt().trim().equals("SI")) {
					uIsr = listImp.get(0).getTasaIsr();
				}else {
		        	if(iPlazo == 365) {
		        		uIsr = listImp.get(0).getTasaIsr();
		        	}else {
		        		if(funciones.obtenerAnioBisiesto(globalSingleton.getFechaHoy()) && bAplicaBisiesto)
		            		uIsr = funciones.redondearCantidades(((listImp.get(0).getTasaIsr() / 366) * 360), 6);
		            	else
		            		uIsr = funciones.redondearCantidades(((listImp.get(0).getTasaIsr() / 365) * 360), 6);
		            }
		        }
			}else {
				uIsr = 0;
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:calcularIsr");
		}
		return uIsr;
	}
	
	//Inicia metodos para ConsultaOrdenesInversion, sin embargo se pueden reutilizar metodos de arriba
	
	/**
	 * Este mï¿½todo obtiene datos de la tabla orden_inversion,
	 * es utilizado en ConsultaOrdenesInversion
	 */
	public List<ConsultaOrdenInversionDto> obtenerOrdenesInversion(int iIdEmpresa){
		List<ConsultaOrdenInversionDto> listConsOrd = new ArrayList<ConsultaOrdenInversionDto>();
		List<CuentaPropiaDto> listCuentaPropia = new ArrayList<CuentaPropiaDto>(); 
		
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			//iIdEmpresa = globalSingleton.getUsuarioLoginDto().getNumeroEmpresa();
			listConsOrd = inversionesDao.consultarOrdenesInversion(iIdEmpresa);
			for(int i = 0; i < listConsOrd.size(); i ++)
			{
				if(listConsOrd.get(i).getBAutoriza().trim().equals("S"))
					listConsOrd.get(i).setBAutoriza("SI");
				else if(listConsOrd.get(i).getBAutoriza().trim().equals("N"))
					listConsOrd.get(i).setBAutoriza("NO");
				
				listCuentaPropia = inversionesDao.consultarCuentaPropia(listConsOrd.get(i).getNoCuenta(), iIdEmpresa);
				
				if(listCuentaPropia.size() > 0)
				{
					listConsOrd.get(i).setIdChequera(listCuentaPropia.get(0).getIdChequera() != null 
							 && !listCuentaPropia.get(0).getIdChequera().equals("") 
							 ? listCuentaPropia.get(0).getIdChequera() : "");
					listConsOrd.get(i).setIdBanco(listCuentaPropia.get(0).getIdBanco() > 0 ? listCuentaPropia.get(0).getIdBanco() : 0);
					/*listConsOrd.get(i).setIdChequeraReg(listCuentaPropia.get(i).getIdChequeraDos() != null 
								 && !listCuentaPropia.get(i).getIdChequeraDos().equals("") 
								 ? listCuentaPropia.get(i).getIdChequeraDos() : "");*/
				}
			}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:obtenerOrdenesInversion");
		}
		return listConsOrd;
	}
	
	
	public int actualizarAutorizaOrdenInversion(String sAutoriza, int iNoOrden){
		int iRegAfec = 0;
		try{
			iRegAfec = inversionesDao.actualizarAutorizaOrdenInversion(sAutoriza, iNoOrden);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:actualizarAutorizaOrdenInversion");
		}
		return iRegAfec;
	}
	
	public List<CuentaPropiaDto> obtenerCuentaPropia(int iNoCuenta, int iIdEmpresa)
	{
		List<CuentaPropiaDto> listCuenta = new ArrayList<CuentaPropiaDto>();
		try{
			listCuenta = inversionesDao.consultarCuentaPropia(iNoCuenta, iIdEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:consultarCuentaPropia");
		}
		return listCuenta;
	}
	
	@SuppressWarnings("unchecked")
	public Map ejecutarRevividor(List<ConsultaOrdenInversionDto> listOrdIn, int noEmpresa){
		Map<String, Object> mapRev = new HashMap<String, Object>();
		Map<String, Object> mapRet = new HashMap<String, Object>();
		String sRevividor = "";
        String psTipoCancelacion = "";
        String sEstatus = "";
        String sOrigen = "";
        String sEstatusMov = "";
        int iSecuencia = 1;
        int iIdUsuario = 0;
        
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			iIdUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			
			for(int i = 0; i < listOrdIn.size(); i++) {
				sRevividor = "N";
		        psTipoCancelacion = "C";
		        sEstatus = listOrdIn.get(i).getIdEstatusMov();
		        sOrigen = listOrdIn.get(i).getOrigenMov();
		        sEstatusMov = "P";
				
		        RevividorDto dtoRev = new RevividorDto();
		        	dtoRev.setNomForma("ConsultaOrdenesInversion");
					dtoRev.setPsRevividor(sRevividor);
					dtoRev.setNoFolioDet(listOrdIn.get(i).getNoFolioDet());
					dtoRev.setIdTipoOperacion(listOrdIn.get(i).getIdTipoOperacion());
					dtoRev.setPsTipoCancelacion(psTipoCancelacion);
					dtoRev.setIdEstatusMov(listOrdIn.get(i).getIdEstatusMov());
					dtoRev.setPsOrigenMov(listOrdIn.get(i).getOrigenMov().equals("CXP") ? "SOI" : listOrdIn.get(i).getOrigenMov());
					dtoRev.setIdFormaPago(listOrdIn.get(i).getIdFormaPago());
					dtoRev.setBEntregado(listOrdIn.get(i).getBEntregado());
					dtoRev.setIdTipoMovto(listOrdIn.get(i).getIdTipoMovto());
					dtoRev.setImporte(listOrdIn.get(i).getImporte());
					dtoRev.setNoEmpresa(noEmpresa);
					dtoRev.setNoCuenta(listOrdIn.get(i).getNoCuenta());
					dtoRev.setIdChequera(listOrdIn.get(i).getIdChequera());
					dtoRev.setIdBanco(listOrdIn.get(i).getIdBanco());
					dtoRev.setIdUsuario(iIdUsuario);
					dtoRev.setNoDocto(Integer.parseInt(listOrdIn.get(i).getNoOrden().trim()));
					dtoRev.setLote(listOrdIn.get(i).getLoteEntrada());
					dtoRev.setBSalvoBuenCobro(listOrdIn.get(i).getBSalvoBuenCobro());
					dtoRev.setFecConfTrans("");
					dtoRev.setIdDivisa(listOrdIn.get(i).getIdDivisa());
				mapRev = inversionesDao.ejecutarRevividor(dtoRev);	
				
				if(!mapRev.get("result").toString().trim().equals("0"))
				{
					if(mapRev.get("result").toString().trim().equals("2000"))
						mapRet.put("msgUsuario", "ï¿½Este Movimiento no se puede cancelar!");
					else
						mapRet.put("msgUsuario", "ï¿½Este en revividor #" + mapRev.get("result").toString() );
					
					return mapRet;
				}
					
			}
			mapRet.put("msgUsuario", "La Orden de Inversiï¿½n ha sido cancelada...");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:ejecutarRevividor");
		}
		return mapRet;
	}
	
	//Inician mï¿½todos de LiquidacionDeInversiones, aunque se pueden reutilizar mï¿½todos anteriores.
	/**
	 * 
	 * @param iAvisoInversion
	 * @return
	 */
	public List<ConsultaOrdenInversionDto> consultarLiquidaOrdenInversion(Integer iAvisoInversion, Integer iIdEmpresa, Integer isUsuario){
		List<ConsultaOrdenInversionDto> listLiqOrd = new ArrayList<ConsultaOrdenInversionDto>();
		List<CuentaPropiaDto> listCtaProp = new ArrayList<CuentaPropiaDto>();
		
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			//iIdEmpresa = globalSingleton.getUsuarioLoginDto().getNumeroEmpresa();
			System.out.println(".:::Entar al metodo consultar liquidacionordenInversion");
			listLiqOrd = inversionesDao.consultarLiquidaOrdenInversion(iIdEmpresa, iAvisoInversion, isUsuario);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:consultarLiquidaOrdenInversion");
		}
		return listLiqOrd;
	}
	
	
	public List<LlenaComboGralDto> obtenerBancosRegresoInversion(int iIdEmpresa, String sIdDivisa)
	{
		List<LlenaComboGralDto> listBancos = new ArrayList<LlenaComboGralDto>();
		int iEmpresa = 0;
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			iEmpresa = globalSingleton.getUsuarioLoginDto().getNumeroEmpresa();
			listBancos = inversionesDao.consultarBancosRegresoInversion(iIdEmpresa > 0 ? iIdEmpresa : iEmpresa, sIdDivisa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:obtenerBancosRegresoInversion");
		}
		return listBancos;
	}
	
	public List<CuentaDto> obtenerContratosLiq(int iIdEmpresa, int iNumCta)
	{
		List<CuentaDto> listContLiq = new ArrayList<CuentaDto>();
		int iEmpresa = 0;
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			iEmpresa = globalSingleton.getUsuarioLoginDto().getNumeroEmpresa();
			listContLiq = inversionesDao.consultarContratosLiq(iIdEmpresa > 0 ? iIdEmpresa : iEmpresa, iNumCta);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:consultarContratosLiq");
		}
		return listContLiq;
	}
	
	public List<LlenaComboChequeraDto> obtenerChequerasLiq(int iIdBancoLiq, int iNoCuenta, boolean bInterna){
		List<LlenaComboChequeraDto> listChe = new ArrayList<LlenaComboChequeraDto>();
		try{
			listChe = inversionesDao.consultarChequerasLiq(iIdBancoLiq, iNoCuenta, bInterna);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:consultarChequerasLiq");
		}
		return listChe;
	}
	
	public List<LlenaComboGralDto> obtenerBancosCargo(int iIdEmpresa, String sIdDivisa){
		List<LlenaComboGralDto> listBancos = new ArrayList<LlenaComboGralDto>();
		int iNoEmpresa = 0;
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			iNoEmpresa = globalSingleton.getUsuarioLoginDto().getNumeroEmpresa();
			listBancos = inversionesDao.consultarBancosCargo(iIdEmpresa > 0 ? iIdEmpresa : iNoEmpresa, sIdDivisa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:consultarBancosCargo");
		}
		return listBancos;
	}
	
	public List<CatCtaBancoDto> obtenerChequerasCargo(int iIdEmpresa, int iIdBanco, String sIdDivisa){
		int iNoEmpresa = 0;
		List<CatCtaBancoDto> listChe = new ArrayList<CatCtaBancoDto>();
		int noBanco = 0;
		
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			iNoEmpresa = globalSingleton.getUsuarioLoginDto().getNumeroEmpresa();
			listChe = inversionesDao.consultarChequerasCargo(iIdEmpresa > 0 ? iIdEmpresa : iNoEmpresa, iIdBanco, sIdDivisa);
			
			if(listChe.size() > 0) {
				for(int i=0; i<listChe.size(); i++) {
					if(listChe.get(i).getTipoChequera().equals("C")) {
						noBanco = listChe.get(i).getIdBanco();
						break;
					}
				}
				if(noBanco == 0)
					noBanco = listChe.get(0).getIdBanco();
			}
			listChe = inversionesDao.consultarChequerasCargo(iIdEmpresa > 0 ? iIdEmpresa : iNoEmpresa, noBanco, sIdDivisa);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:obtenerChequerasCargo");
		}
		return listChe;
	}
	
	
	/**
	 * Mï¿½todo para realizar la liquidaciï¿½n de inversiones,
	 * por ahora la funcionalidad de inversion interna se queda
	 * pendiente cuando se requiera en alguna instalaciï¿½n
	 * @param bInterna
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> liquidarInversiones(LiquidaInversionesDto dtoLiqInv, 
												   List<ConsultaOrdenInversionDto> listOrdenInv, 
												   List<BancoCheContratoDto> listBancoCargo){
		Map<String, Object> mapRet = new HashMap<String, Object>();
		Map<String, Object> mapGen = new HashMap<String, Object>();
		int iFolio = 0;
		int iFolio2 = 0;
		int iFolio3 = 0;
		int iNumCta = 0;
		int iFolioGenerador = 0;
		double uTipoCambio = 0;
		boolean bTraspasos = false;
		String sOrigenMov = "";
		String sGrupo = "";
		
		int iAplica = 0;
		int iFolioGrupoGlobal;
		int iRegAfectados = 0;
		int iFolioRef = 0;
		int iIdBancoCargo = 0;
		int iEmpresa = 0;
		int iEmpresaLiq = 0;
		int iPlazo = 0;
		double uTasa = 0;
		String sIdChequeraCargo = "";
		
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			//Se asigna la empresa en la que esta parado el usuario en caso de la funcionalidad de empresas internas sea false,
			//ya que desde la vista viene como '0'
			iEmpresa = dtoLiqInv.getIdEmpresa() > 0 ? dtoLiqInv.getIdEmpresa() : globalSingleton.getUsuarioLoginDto().getNumeroEmpresa(); 
			iEmpresaLiq = iEmpresa;
			if(!dtoLiqInv.getIdDivisa().equals(globalSingleton.obtenerValorConfiguraSet(771)))
			{
				uTipoCambio = inversionesDao.consultarTipoCambio(dtoLiqInv.getIdDivisa());
				if(uTipoCambio <= 0)
				{
					mapRet.put("msgUsuario", "No existe el Tipo de Cambio para la Divisa del contrato, por favor verifique e intente nuevamente...");
					return mapRet;
				}
			}
			else
				uTipoCambio = 1;
			
			if(globalSingleton.obtenerValorConfiguraSet(268).equals("SI"))
			{
				if(dtoLiqInv.isBInterna())
				{
					/*If pbInversionInterna Then
			            Set rstDatos = gobjSQL.FunSQLSelectDatosEmp(txtIdBacoLiq.Text, Combo2.Text)
			            If Not rstDatos.EOF Then
			                plEmpresaLiq = rstDatos("no_empresa")
			                psEmpresaLiq = rstDatos("nom_empresa")
			            End If
			            Set rstDatos = Nothing
			            
			            psOrigenMov = "IVT"
			        Else
			            psOrigenMov = "INV"
			        End If*/
				}
				else
				{
					sOrigenMov = "INV";
				}
			}
			else
			{
				sOrigenMov = "INV";
			}
			
			if(dtoLiqInv.isBInterna() && globalSingleton.obtenerValorConfiguraSet(268).equals("SI"))
			{
				/*  'Busca las ordenes internas que tiene relacionadas
			        Set rstDatos = gobjSQL.FunSQLSelectOrdenesInternas(CLng(msfOrdenesInv.TextMatrix(1, 0)))
			
			        While Not rstDatos.EOF
			            'Valida el estatus de cada una, deben estar en estatus LIQUIDADA
			            If Not ValidaEstatusBd(rstDatos("no_aviso_ref"), rstDatos("no_empresa"), "L") Then
			                EstableceDatos = False
			                lerror = -999
			                Exit Function
			            End If
			
			            rstDatos.MoveNext
			        Wend
			            
			        Set rstDatos = Nothing*/
			}
			else if(!dtoLiqInv.isBInterna() && globalSingleton.obtenerValorConfiguraSet(268).equals("SI"))
			{
				/*
				 With frmMonitorInversion.msfDatos
		            If Not ValidaEstatusBd(.TextMatrix(.row, COL_NO_AVISO), .TextMatrix(.row, COL_EMPRESA), "I", "U") Then
		                EstableceDatos = False
		                lerror = -999
		                Exit Function
		            End If
		        End With*/
			}
			
			iNumCta = inversionesDao.consultarNoCuentaEmpresa(iEmpresa);
			iPlazo = listOrdenInv.get(0).getPlazo();
			uTasa = listOrdenInv.get(0).getTasa();
			/**/
			if(dtoLiqInv.isBCuentaPropia())
			{
				bTraspasos = false;
				for(int i = 0; i < listBancoCargo.size(); i ++)
				{
					iIdBancoCargo = listBancoCargo.get(i).getIdBanco();
					
					//System.out.println("iIdBancoCargo "+ iIdBancoCargo);
					
					sIdChequeraCargo = listBancoCargo.get(i).getIdChequera();

					//System.out.println("sIdChequeraCargo "+ sIdChequeraCargo);
					//Se valida si la chequera de liquida y cargo son diferentes
					//Valida si la chequera liquida y regreso son diferentes
					if(dtoLiqInv.isChequerasDiferentesUno()) //|| dtoLiqInv.isChequerasDiferentesDos())
					{
						if(dtoLiqInv.isBInterna())
						{
							/*
							 ' Valida si la inversion va a ser hacia la misma empresa
	                        ' Si es misma empresa no genera los traspasos de liquidacion
	                        If Not gobjSQL.FunSQLVerInversionMismaEmp( _
	                            frmMonitorInversion.msfDatos.TextMatrix(frmMonitorInversion.msfDatos.row, COL_NO_INSTITUCION), _
	                            frmMonitorInversion.msfDatos.TextMatrix(frmMonitorInversion.msfDatos.row, COL_EMPRESA)) Then
	                            pbInversionMismaEmp = False
	                            If plEmpresaLiq <> is_IdEmpresa Then
	                                'ESTE TRASPASO ES A LA MISMA CUENTA.. NO SE DEBERï¿½A HACER
	                                pl_folio = Traspasos(ps_NumCta, ps_Importe, ps_BcoCargo, ps_ChequeCargo, _
	                                                     ps_BcoReg, ps_ChequeReg, lerror, ps_Grupo, _
	                                                     pdTipoCambioActual, "IVT", CLng(is_IdEmpresa), psEmpresaLiq, _
	                                                     plEmpresaLiq)
	                            Else
	                                pl_folio = Traspasos(ps_NumCta, ps_Importe, ps_BcoCargo, ps_ChequeCargo, _
	                                                     ps_BcoReg, ps_ChequeReg, lerror, ps_Grupo, _
	                                                     pdTipoCambioActual, "IVT")
	                            End If
	                        Else
	                            'inversion Interna Misma Empresa
	                            'Para simular la inversion de cie como unidad de negocio
	                            pbInversionMismaEmp = True
	                            pl_folio = Traspasos(ps_NumCta, ps_Importe, ps_BcoCargo, ps_ChequeCargo, _
	                                                     ps_BcoReg, ps_ChequeReg, lerror, ps_Grupo, _
	                                                     pdTipoCambioActual, "IVT")
	                            
	                            
	                            
	                        End If 
							 */
						}
						else
						{
//							TraspasosDto dtoTras = new TraspasosDto();
//								dtoTras.setNumCuenta(iNumCta);
//								dtoTras.setImporte(dtoLiqInv.getImporte());
//								dtoTras.setIdBancoCargo(listBancoCargo.get(i).getIdBanco());
//								dtoTras.setIdChequeraCargo(listBancoCargo.get(i).getIdChequera());
//								dtoTras.setIdBancoRegreso(dtoLiqInv.getIdBancoRegreso());
//								dtoTras.setIdChequeraRegreso(dtoLiqInv.getIdChequeraRegreso());
//								dtoTras.setIdBancoBenefLiq(dtoLiqInv.getIdBancoLiquida());
//								dtoTras.setIdChequeBenefLiq(dtoLiqInv.getIdChequeraLiquida());
//								dtoTras.setGrupo(""+8000);
//
//								dtoTras.setTipoCambioActual(uTipoCambio);
//								dtoTras.setOrigenMovto(sOrigenMov);
//								dtoTras.setFecha(dtoLiqInv.getFechaAlta());
//								dtoTras.setIdEmpresa(iEmpresa);
//								dtoTras.setIdEmpresaBenef(iEmpresa);
//								dtoTras.setBeneficiario(dtoLiqInv.getNomEmpresa());
//								dtoTras.setConcepto("TRASPASO DE INVERSIONES");
//								dtoTras.setIdEstatusMov("L");
//								dtoTras.setAplica(1);
//								dtoTras.setBTraspasos(bTraspasos);
//								dtoTras.setNoOrden(listOrdenInv.get(0).getNoOrden());
//								dtoTras.setIdDivisa(dtoLiqInv.getIdDivisa());
//								dtoTras.setDiasInv(iPlazo);
//								dtoTras.setValorTasa(uTasa);
//							iFolio = 0;
							//this.realizarTraspasos(dtoTras, dtoLiqInv.isChequerasDiferentesUno(), dtoLiqInv.isChequerasDiferentesDos());
							 
//							if(iFolio <= 0)
//							{
//								mapRet.put("msgUsuario", "Ocurrio un error en traspasos" + iFolio);
//								return mapRet;
//							}
//							
//							if(iFolioGenerador == 0)
//							{
//								iFolioGenerador = iFolio;
//								sGrupo = "" + iFolioGenerador;
//							}
							bTraspasos = true;
						}
					}
				}
			}/**/
		//Inician Valores del grid ordenesInversion para realizar las 4 Mil
			//System.out.println("listOrdenInv.size() "+ listOrdenInv.size());
			for(int c = 0; c < listOrdenInv.size(); c ++)
			{
				iFolio = this.obtenerFolioReal("no_folio_param");
				uTasa = listOrdenInv.get(c).getTasa();
				iNumCta = listOrdenInv.get(c).getNoCuenta();
				if(dtoLiqInv.isBCuentaPropia())
				{
//					iIdBancoCargo = dtoLiqInv.getIdBancoLiquida();
//					sIdChequeraCargo = dtoLiqInv.getIdChequeraLiquida();
					iAplica = 1;
					if(iFolioGenerador <= 0)
					{
						iFolioGenerador = iFolio;
						sGrupo = "" + iFolioGenerador;
						iFolioGrupoGlobal = iFolio;
					}
					if(sGrupo.equals(""))
						iFolioGrupoGlobal = this.obtenerFolioReal("no_folio_param");
					else
						iFolioGrupoGlobal = Integer.parseInt(sGrupo);
					
					Integer noCliente = this.inversionesDao.consultarNoClinente(iNumCta);
					//System.out.println("dtoLiqInv.isBInterna() "+ dtoLiqInv.isBInterna());
					if(!dtoLiqInv.isBInterna()){
						
						Integer formaPago = 
								this.inversionesDao.consultarFormaPagoContrato(iNumCta, iEmpresaLiq);
						List<RubroDto> rubros = this.inversionesDao.consultarRubroOrdenInversion(Integer.valueOf(listOrdenInv.get(c).getNoOrden().trim()));
						
						//System.out.println("La forma de pago es "+ formaPago);
						
						if(formaPago != 3){
						
							//inicia inserciï¿½n 4001
							iRegAfectados = 0;
							iFolioRef = listOrdenInv.get(c).getNoFolioDet();
	
							ParametroDto insert4001 = new ParametroDto();
							if(!bTraspasos)
							{
								insert4001.setNoFolioParam(Integer.parseInt(sGrupo));
							}
							else
							{
								iFolio2 = this.obtenerFolioReal("no_folio_param");
								insert4001.setNoFolioParam(iFolio2);
							}
							
							insert4001.setIdTipoOperacion(4001);
							insert4001.setNoCuenta(iNumCta);
							insert4001.setImporte(listOrdenInv.get(c).getImporte());
							insert4001.setImporteOriginal(listOrdenInv.get(c).getImporte());
							insert4001.setIdBanco(iIdBancoCargo);
							insert4001.setIdChequera(sIdChequeraCargo);
							insert4001.setIdBancoReg(dtoLiqInv.getIdBancoRegreso());
							insert4001.setIdChequeraReg(dtoLiqInv.getIdChequeraRegreso());
							insert4001.setGrupo(iFolioGrupoGlobal);
							insert4001.setIdBancoBenef(dtoLiqInv.getIdBancoLiquida());
							insert4001.setIdChequeraBenef(dtoLiqInv.getIdChequeraLiquida());
							insert4001.setNoFolioMov(0);
							insert4001.setFolioRef(iFolioRef);
							insert4001.setIdEstatusMov("A");
							insert4001.setSecuencia(0);
							insert4001.setConcepto("INVERSION");
							insert4001.setAplica(iAplica);
							insert4001.setDiasInv(iPlazo);
							insert4001.setValorTasa(uTasa);
							insert4001.setOrigenMov(IS_ORIGEN_MOV);
							insert4001.setTipoCambio(uTipoCambio);
							insert4001.setNoEmpresa(iEmpresaLiq);
							insert4001.setBeneficiario(listOrdenInv.get(c).getNombreCorto());
							insert4001.setNoCliente("" + noCliente);
							//insert4001.setFecValor(dtoLiqInv.getFechaAlta());
							//insert4001.setFecValorOriginal(dtoLiqInv.getFechaAlta());
							//insert4001.setFecValor(listOrdenInv.get(c).getFecVenc());
							//insert4001.setFecValorOriginal(listOrdenInv.get(c).getFecVenc());
							insert4001.setFecValor(listOrdenInv.get(c).getFecAlta());
							insert4001.setFecValorOriginal(listOrdenInv.get(c).getFecAlta());
							insert4001.setFecOperacion(globalSingleton.getFechaHoy());
							insert4001.setFecAlta(globalSingleton.getFechaHoy());
							
	//						if(dtoLiqInv.getTipoPago() == 5)
	//							insert4001.setIdFormaPago(dtoLiqInv.getTipoPago());
	//						else
							insert4001.setIdFormaPago(formaPago);
							
							insert4001.setIdTipoDocto(I_TIPO_DOCTO);
							insert4001.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
							insert4001.setIdDivisa(dtoLiqInv.getIdDivisa());
							insert4001.setBSalvoBuenCobro(IS_SALVO_BCOBRO);
							insert4001.setIdEstatusReg(IS_STATUS_REG);
							insert4001.setNoDocto(listOrdenInv.get(c).getNoOrden().trim());
							
							
							insert4001.setIdGrupoInv(rubros.get(0).getIdGrupoInv());
							insert4001.setRubro(rubros.get(0).getIdRubroInv());
							
							insert4001.setUsuarioAlta(globalSingleton.getUsuarioLoginDto().getIdUsuario());
							
							iRegAfectados = inversionesDao.insertarParametroLiquidacion(insert4001);
							
							if(iRegAfectados <= 0)
							{
								mapRet.put("msgUsuario", "No se pudo insertar 4001");
								return mapRet;
							}
							//Termina inserciï¿½n 4001
						}else{
							//Se Inicia la inserción de la 3200 y 3201
							iRegAfectados = 0;
							iFolioRef = listOrdenInv.get(c).getNoFolioDet();
	
							ParametroDto insert3200 = new ParametroDto();
							if(!bTraspasos)
							{
								insert3200.setNoFolioParam(Integer.parseInt(sGrupo));
							}
							else
							{
								iFolio2 = this.obtenerFolioReal("no_folio_param");
								insert3200.setNoFolioParam(iFolio2);
							}
							
							insert3200.setIdTipoOperacion(3200);
							insert3200.setNoCuenta(iNumCta);
							insert3200.setImporte(listOrdenInv.get(c).getImporte());
							insert3200.setImporteOriginal(listOrdenInv.get(c).getImporte());
							insert3200.setIdBanco(iIdBancoCargo);
							insert3200.setIdChequera(sIdChequeraCargo);
							insert3200.setIdBancoReg(dtoLiqInv.getIdBancoRegreso());
							insert3200.setIdChequeraReg(dtoLiqInv.getIdChequeraRegreso());
							insert3200.setGrupo(iFolioGrupoGlobal);
							insert3200.setIdBancoBenef(dtoLiqInv.getIdBancoLiquida());
							insert3200.setIdChequeraBenef(dtoLiqInv.getIdChequeraLiquida());
							insert3200.setNoFolioMov(0);
							insert3200.setFolioRef(iFolioRef);
							insert3200.setIdEstatusMov("P");
							insert3200.setSecuencia(0);
							insert3200.setConcepto("INVERSION");
							insert3200.setAplica(iAplica);
							insert3200.setDiasInv(iPlazo);
							insert3200.setValorTasa(uTasa);
							insert3200.setOrigenMov(IS_ORIGEN_MOV);
							insert3200.setTipoCambio(uTipoCambio);
							insert3200.setNoEmpresa(iEmpresaLiq);
							insert3200.setBeneficiario(listOrdenInv.get(c).getNombreCorto());
							insert3200.setNoCliente("" + noCliente);
							insert3200.setFecValor(listOrdenInv.get(c).getFecAlta());
							insert3200.setFecValorOriginal(listOrdenInv.get(c).getFecAlta());
							insert3200.setFecOperacion(globalSingleton.getFechaHoy());
							insert3200.setFecAlta(globalSingleton.getFechaHoy());
							
							insert3200.setIdFormaPago(formaPago);
							
							insert3200.setIdTipoDocto(I_TIPO_DOCTO);
							insert3200.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
							insert3200.setIdDivisa(dtoLiqInv.getIdDivisa());
							insert3200.setBSalvoBuenCobro(IS_SALVO_BCOBRO);
							insert3200.setIdEstatusReg(IS_STATUS_REG);
							insert3200.setNoDocto(listOrdenInv.get(c).getNoOrden().trim());
							
							insert3200.setIdGrupoInv(rubros.get(0).getIdGrupoInv());
							insert3200.setRubro(rubros.get(0).getIdRubroInv());
							
							insert3200.setUsuarioAlta(globalSingleton.getUsuarioLoginDto().getIdUsuario());
							
							iRegAfectados = inversionesDao.insertarParametroLiquidacion(insert3200);
							
							if(iRegAfectados <= 0)
							{
								mapRet.put("msgUsuario", "No se pudo insertar 3200");
								return mapRet;
							}

							iFolio2 = this.obtenerFolioReal("no_folio_param");
							insert3200.setNoFolioParam(iFolio2);
							insert3200.setFolioRef(1);
							insert3200.setIdTipoOperacion(3201);
							insert3200.setIdEstatusMov("H");
							iRegAfectados = inversionesDao.insertarParametroLiquidacion(insert3200);
							
							if(iRegAfectados <= 0)
							{
								mapRet.put("msgUsuario", "No se pudo insertar 3201");
								return mapRet;
							}
							
							
							//Se Termina la inserción de la 3200 y 3201
						}
						
						//Inicia inserciï¿½n 4002
						ParametroDto insert4002 = new ParametroDto();
							insert4002.setNoFolioParam(this.obtenerFolioReal("no_folio_param"));
							insert4002.setIdTipoOperacion(4002);
							insert4002.setNoCuenta(iNumCta);
							insert4002.setImporte(listOrdenInv.get(c).getImporte());
							insert4002.setImporteOriginal(listOrdenInv.get(c).getImporte());
							insert4002.setIdBanco(iIdBancoCargo);
							insert4002.setIdChequera(sIdChequeraCargo);
							insert4002.setIdBancoReg(dtoLiqInv.getIdBancoRegreso());
							insert4002.setIdChequeraReg(dtoLiqInv.getIdChequeraRegreso());
							insert4002.setGrupo(iFolioGrupoGlobal);
							insert4002.setIdBancoBenef(0);
							insert4002.setIdChequeraBenef("");
							insert4002.setNoFolioMov(0);
							insert4002.setFolioRef(2);
							insert4002.setIdEstatusMov("D");
							insert4002.setSecuencia(0);
							insert4002.setConcepto("LIQUIDACION INV.CAPITAL");
							insert4002.setAplica(iAplica);
							insert4002.setDiasInv(iPlazo);
							insert4002.setValorTasa(uTasa);
							insert4002.setOrigenMov(IS_ORIGEN_MOV);
							insert4002.setTipoCambio(uTipoCambio);
							insert4002.setNoEmpresa(iEmpresaLiq);
							insert4002.setBeneficiario(listOrdenInv.get(c).getNombreCorto());
							insert4002.setNoCliente("" + iEmpresa);
							//insert4002.setFecValor(dtoLiqInv.getFechaAlta());
							//insert4002.setFecValorOriginal(dtoLiqInv.getFechaAlta());
							insert4002.setFecValor(listOrdenInv.get(c).getFecVenc());
							insert4002.setFecValorOriginal(listOrdenInv.get(c).getFecVenc());
							insert4002.setFecOperacion(globalSingleton.getFechaHoy());
							insert4002.setFecAlta(globalSingleton.getFechaHoy());
							
//							if(dtoLiqInv.getTipoPago() == 5)
//								insert4001.setIdFormaPago(dtoLiqInv.getTipoPago());
//							else
							insert4002.setIdFormaPago(formaPago);
							
							insert4002.setIdTipoDocto(I_TIPO_DOCTO);
							insert4002.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
							insert4002.setIdDivisa(dtoLiqInv.getIdDivisa());
							insert4002.setBSalvoBuenCobro(IS_SALVO_BCOBRO);
							insert4002.setIdEstatusReg(IS_STATUS_REG);
							insert4002.setNoDocto(listOrdenInv.get(c).getNoOrden().trim());
							insert4002.setUsuarioAlta(globalSingleton.getUsuarioLoginDto().getIdUsuario());
							
							insert4002.setIdGrupoInv(rubros.get(0).getIdGrupoReg());
							insert4002.setRubro(rubros.get(0).getIdRubroReg());
						
							iRegAfectados = inversionesDao.insertarParametroLiquidacion(insert4002);
							
							if(iRegAfectados <= 0)
							{
								mapRet.put("msgUsuario", "No se pudo insertar 4002");
								return mapRet;
							}
						//Termina inserciï¿½n 4002
							
						//Inicia inserciï¿½n 4003
						ParametroDto insert4003 = new ParametroDto();
							insert4003.setNoFolioParam(this.obtenerFolioReal("no_folio_param"));
							insert4003.setIdTipoOperacion(4003);
							insert4003.setNoCuenta(iNumCta);
							insert4003.setImporte(listOrdenInv.get(c).getInteres());
							insert4003.setImporteOriginal(listOrdenInv.get(c).getInteres());
							insert4003.setIdBanco(iIdBancoCargo);
							insert4003.setIdChequera(sIdChequeraCargo);
							insert4003.setIdBancoReg(dtoLiqInv.getIdBancoRegreso());
							insert4003.setIdChequeraReg(dtoLiqInv.getIdChequeraRegreso());
							insert4003.setGrupo(iFolioGrupoGlobal);
							insert4003.setIdBancoBenef(0);
							insert4003.setIdChequeraBenef("");
							insert4003.setNoFolioMov(1);
							insert4003.setFolioRef(2);
							insert4003.setIdEstatusMov("D");
							insert4003.setSecuencia(0);
							insert4003.setConcepto("LIQUIDACION INV. INTERES");
							insert4003.setAplica(iAplica);
							insert4003.setDiasInv(iPlazo);
							insert4003.setValorTasa(uTasa);
							insert4003.setOrigenMov(IS_ORIGEN_MOV);
							insert4003.setTipoCambio(uTipoCambio);
							insert4003.setNoEmpresa(iEmpresaLiq);
							insert4003.setBeneficiario(dtoLiqInv.getNomEmpresa());
							insert4003.setNoCliente("" + iEmpresa);
							//insert4003.setFecValor(dtoLiqInv.getFechaAlta());
							//insert4003.setFecValorOriginal(dtoLiqInv.getFechaAlta());
							insert4003.setFecValor(listOrdenInv.get(c).getFecVenc());
							insert4003.setFecValorOriginal(listOrdenInv.get(c).getFecVenc());
							insert4003.setFecOperacion(globalSingleton.getFechaHoy());
							insert4003.setFecAlta(globalSingleton.getFechaHoy());
							
//							if(dtoLiqInv.getTipoPago() == 5)
//								insert4001.setIdFormaPago(dtoLiqInv.getTipoPago());
//							else
							insert4003.setIdFormaPago(formaPago);
							
							insert4003.setIdTipoDocto(I_TIPO_DOCTO);
							insert4003.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
							insert4003.setIdDivisa(dtoLiqInv.getIdDivisa());
							insert4003.setBSalvoBuenCobro(IS_SALVO_BCOBRO);
							insert4003.setIdEstatusReg(IS_STATUS_REG);
							insert4003.setNoDocto(listOrdenInv.get(c).getNoOrden().trim());
							insert4003.setUsuarioAlta(globalSingleton.getUsuarioLoginDto().getIdUsuario());
							insert4003.setIdGrupoInv(rubros.get(0).getIdGrupoInt());
							insert4003.setRubro(rubros.get(0).getIdRubroInt());
							
						iRegAfectados = inversionesDao.insertarParametroLiquidacion(insert4003);
						
						if(iRegAfectados <= 0)
						{
							mapRet.put("msgUsuario", "No se pudo insertar 4003");
							return mapRet;
						}
						//Termina inserciï¿½n 4003 
						
						//Inicia inserciï¿½n 4004
						ParametroDto insert4004 = new ParametroDto();
							insert4004.setNoFolioParam(this.obtenerFolioReal("no_folio_param"));
							insert4004.setIdTipoOperacion(4004);
							insert4004.setNoCuenta(iNumCta);
							insert4004.setImporte(listOrdenInv.get(c).getIsr());
							insert4004.setImporteOriginal(listOrdenInv.get(c).getIsr());
							insert4004.setIdBanco(iIdBancoCargo);
							insert4004.setIdChequera(sIdChequeraCargo);
							insert4004.setIdBancoReg(dtoLiqInv.getIdBancoRegreso());
							insert4004.setIdChequeraReg(dtoLiqInv.getIdChequeraRegreso());
							insert4004.setGrupo(iFolioGrupoGlobal);
							insert4004.setIdBancoBenef(0);
							insert4004.setIdChequeraBenef("");
							insert4004.setNoFolioMov(1);
							insert4004.setFolioRef(2);
							insert4004.setIdEstatusMov("D");
							insert4004.setSecuencia(0);
							insert4004.setConcepto("LIQUIDACION INV. ISR");
							insert4004.setAplica(iAplica);
							insert4004.setDiasInv(iPlazo);
							insert4004.setValorTasa(uTasa);
							insert4004.setOrigenMov(IS_ORIGEN_MOV);
							insert4004.setTipoCambio(uTipoCambio);
							insert4004.setNoEmpresa(iEmpresaLiq);
							insert4004.setBeneficiario(dtoLiqInv.getNomEmpresa());
							insert4004.setNoCliente("" + iEmpresa);
							//insert4004.setFecValor(dtoLiqInv.getFechaAlta());
							//insert4004.setFecValorOriginal(dtoLiqInv.getFechaAlta());
							insert4004.setFecValor(listOrdenInv.get(c).getFecVenc());
							insert4004.setFecValorOriginal(listOrdenInv.get(c).getFecVenc());
							insert4004.setFecOperacion(globalSingleton.getFechaHoy());
							insert4004.setFecAlta(globalSingleton.getFechaHoy());
							
//							if(dtoLiqInv.getTipoPago() == 5)
//								insert4001.setIdFormaPago(dtoLiqInv.getTipoPago());
//							else
							insert4004.setIdFormaPago(formaPago);
							
							insert4004.setIdTipoDocto(I_TIPO_DOCTO);
							insert4004.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
							insert4004.setIdDivisa(dtoLiqInv.getIdDivisa());
							insert4004.setBSalvoBuenCobro(IS_SALVO_BCOBRO);
							insert4004.setIdEstatusReg(IS_STATUS_REG);
							insert4004.setNoDocto(listOrdenInv.get(c).getNoOrden().trim());
							insert4004.setIdGrupoInv(rubros.get(0).getIdGrupoISR());
							insert4004.setRubro(rubros.get(0).getIdRubroISR());
							insert4004.setUsuarioAlta(globalSingleton.getUsuarioLoginDto().getIdUsuario());
							
						iRegAfectados = inversionesDao.insertarParametroLiquidacion(insert4004);
						
						if(iRegAfectados <= 0)
						{
							mapRet.put("msgUsuario", "No se pudo insertar 4004");
							return mapRet;
						}
						//Termina inserciï¿½n 4004 
						
						//Inicia llamada al generador
						GeneradorDto dtoGen = new GeneradorDto();
						dtoGen.setEmpresa(iEmpresa);
						dtoGen.setFolParam(Integer.parseInt(sGrupo));
						dtoGen.setFolMovi(Integer.parseInt(sGrupo));
						dtoGen.setFolDeta(0);
						dtoGen.setResult(0);
						dtoGen.setMensajeResp("inicia generador");
						dtoGen.setNomForma("LiquidacionDeInversiones.js");
						dtoGen.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
						
						
						mapGen = inversionesDao.ejecutarGenerador(dtoGen);
						
						this.inversionesDao.actualizarEstatusOrdenInversion(
								Integer.valueOf(listOrdenInv.get(0).getNoOrden().trim()), "A");
						
						OrdenInversionDto dto = new OrdenInversionDto();
						
						dto.setIdBanco(iIdBancoCargo);
						dto.setIdChequera(sIdChequeraCargo);
						
						dto.setIdBancoReg(dtoLiqInv.getIdBancoRegreso());
						dto.setIdChequeraReg(dtoLiqInv.getIdChequeraRegreso());
						
						this.inversionesDao.actualizarOrdenInversion(
								Integer.valueOf(listOrdenInv.get(0).getNoOrden().trim()), dto );
						
						if(Integer.parseInt(mapGen.get("result").toString()) != 0)
						{
							mapRet.put("msgUsuario", "Error en generador " + mapGen.get("result").toString());
							return mapRet;
						}
					}
				}
			}
			mapRet.put("msgUsuario","Datos registrados correctamente");
			mapRet.put("codError","0");
		}catch(Exception e){
			mapRet.put("codError","-1");
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:liquidarInversiones");
		}
		return mapRet;
	}
	
	public int realizarTraspasos(TraspasosDto dto, boolean bCheDifUno, boolean bCheDifDos){
		int iFolio = 0;
		int iFolio2 = 0;
		int iFolio3 = 0;
		int iFolioRetorno = 0;
		int iIdTipoOperacion = 0;
		int iRegAfec = 0;
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			 if(dto.getFecha() == null)
				 dto.setFecha(globalSingleton.getFechaHoy());
			 
			 if(dto.isBInterna() && globalSingleton.obtenerValorConfiguraSet(268).equals("SI") && dto.isBInvMismaEmpresa())
			 {
				 iIdTipoOperacion = 3810;
			 }
			 else
			 {
				 if(dto.getIdEmpresa() == dto.getIdEmpresaBenef())
					 iIdTipoOperacion = 3800;
				 else
					 iIdTipoOperacion = 3801;
			 }
			 
			 //if(bCheDifUno)
			 //{
				 iFolio = this.obtenerFolioReal("no_folio_param");
				 iFolioRetorno = iFolio;
				 if(iFolio <= 0)
					 iFolioRetorno = -200;
				 
				 //Objeto para insertar en parametro
				 ParametroDto dtoInsertParam = new ParametroDto();
				/* if(!dto.isBTraspasos())
				 {
					 dtoInsertParam.setNoFolioParam(dto.getGrupo() != null && !dto.getGrupo().equals("") ? Integer.parseInt(dto.getGrupo()) : iFolio);
				 }
				 else
				 {*/
					 iFolio2 = this.obtenerFolioReal("no_folio_param");
					 if(iFolio2 <= 0)
						 iFolioRetorno = -200;
					 dtoInsertParam.setNoFolioParam(iFolio2);
				// }
				 
				 dtoInsertParam.setIdTipoOperacion(iIdTipoOperacion);
				 dtoInsertParam.setNoCuenta(dto.getNumCuenta());
				 dtoInsertParam.setImporte(dto.getImporte());
				 dtoInsertParam.setImporteOriginal(dto.getImporte());
				 dtoInsertParam.setFecValor(dto.getFecha());
				 dtoInsertParam.setFecValorOriginal(dto.getFecha());
				 dtoInsertParam.setFecOperacion(globalSingleton.getFechaHoy());
				 dtoInsertParam.setFecAlta(globalSingleton.getFechaHoy());
				 dtoInsertParam.setIdBanco(dto.getIdBancoCargo());
				 dtoInsertParam.setIdChequera(dto.getIdChequeraCargo());
				 dtoInsertParam.setIdBancoReg(dto.getIdBancoRegreso());
				 dtoInsertParam.setIdChequeraReg(dto.getIdChequeraRegreso());
				 dtoInsertParam.setGrupo(8000);
				 //Valores opcionales
				 dtoInsertParam.setIdBancoBenef(dto.getIdBancoBenefLiq());
				 //dtoInsertParam.setIdBancoBenef(dto.getIdBancoRegreso());
				 dtoInsertParam.setIdChequeraBenef(funciones.validarCadena(dto.getIdChequeraBenefLiq()));
				 //dtoInsertParam.setIdChequeraBenef(funciones.validarCadena(dto.getIdChequeraRegreso()));
				 dtoInsertParam.setNoFolioMov(dto.getNoFolioMov());
				 dtoInsertParam.setFolioRef(dto.getNoFolioRef());
				 dtoInsertParam.setIdEstatusMov(dto.getIdEstatusMov() != null && !dto.getIdEstatusMov().equals("") ? dto.getIdEstatusMov() : "D");
				 dtoInsertParam.setSecuencia(1);
				 dtoInsertParam.setConcepto(funciones.validarCadena(dto.getConcepto()));
				 dtoInsertParam.setAplica(dto.getAplica());
				 dtoInsertParam.setDiasInv(dto.getDiasInv());
				 dtoInsertParam.setValorTasa(dto.getValorTasa());
				 dtoInsertParam.setOrigenMov(funciones.validarCadena(dto.getOrigenMovto()));
				 dtoInsertParam.setTipoCambio(dto.getTipoCambioActual());
				 dtoInsertParam.setNoEmpresa(dto.getIdEmpresa());
				 dtoInsertParam.setBeneficiario(dto.getBeneficiario());
				 dtoInsertParam.setNoCliente(funciones.validarCadena(""+dto.getIdEmpresa()));
				 
				 dtoInsertParam.setRubro(new BigDecimal(8001));
				 
				 Integer formaPago = this.inversionesDao.consultarFormaPagoContrato(dto.getNumCuenta(),dto.getIdEmpresa());
				 
				 dtoInsertParam.setIdFormaPago(formaPago);
				 dtoInsertParam.setIdTipoDocto(I_TIPO_DOCTO);
				 dtoInsertParam.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
				 dtoInsertParam.setBSalvoBuenCobro(IS_SALVO_BCOBRO);
				 dtoInsertParam.setIdEstatusReg(IS_STATUS_REG);
				 dtoInsertParam.setNoDocto(dto.getNoOrden().trim());
				 dtoInsertParam.setIdDivisa(dto.getIdDivisa());
				 dtoInsertParam.setUsuarioAlta(globalSingleton.getUsuarioLoginDto().getIdUsuario());
		
				 iRegAfec = inversionesDao.insertarParametroLiquidacion(dtoInsertParam);
				 
				 if(iRegAfec <= 0)
					 iFolioRetorno = -500;
		
			 //}
			 		 
			 //if(bCheDifDos)
			 //{
				 iFolio3 =  this.obtenerFolioReal("no_folio_param");
				 if(iFolio3 <= 0)
					 iFolioRetorno = -200;
				 
				 iFolioRetorno = iFolio > 0 ? iFolio : iFolio3;
				 
				 ParametroDto dtoInsertParamDos = new ParametroDto();
				 dtoInsertParamDos.setNoFolioParam(iFolio3);
				 dtoInsertParamDos.setIdTipoOperacion(iIdTipoOperacion);
				 dtoInsertParamDos.setNoCuenta(dto.getNumCuenta());
				 dtoInsertParamDos.setImporte(dto.getImporte());
				 dtoInsertParamDos.setImporteOriginal(dto.getImporte());
				 dtoInsertParamDos.setFecValor(dto.getFecha());
				 dtoInsertParamDos.setFecValorOriginal(dto.getFecha());
				 dtoInsertParamDos.setFecOperacion(globalSingleton.getFechaHoy());
				 dtoInsertParamDos.setFecAlta(globalSingleton.getFechaHoy());
				 dtoInsertParamDos.setIdBanco(dto.getIdBancoBenefLiq());
				 dtoInsertParamDos.setIdChequera(dto.getIdChequeraBenefLiq());
				 dtoInsertParamDos.setIdBancoReg(dto.getIdBancoRegreso());
				 dtoInsertParamDos.setIdChequeraReg(dto.getIdChequeraRegreso());
				 dtoInsertParamDos.setGrupo(dto.getGrupo() != null && !dto.getGrupo().equals("") 
						 				 ? Integer.parseInt(dto.getGrupo()) : (iFolio > 0 ? iFolio : iFolio3));
				//Valores opcionales
				 //dtoInsertParamDos.setIdBancoBenef(dto.getIdBancoBenefLiq());
				 dtoInsertParamDos.setIdBancoBenef(dto.getIdBancoCargo());
				 //dtoInsertParamDos.setIdChequeraBenef(funciones.validarCadena(dto.getIdChequeraBenefLiq()));
				 dtoInsertParamDos.setIdChequeraBenef(funciones.validarCadena(dto.getIdChequeraCargo()));
				 dtoInsertParamDos.setNoFolioMov(1);
				 dtoInsertParamDos.setFolioRef(1);
				 dtoInsertParamDos.setIdEstatusMov(dto.getIdEstatusMov() != null && !dto.getIdEstatusMov().equals("") ? dto.getIdEstatusMov() : "D");
				 //dtoInsertParamDos.setSecuencia(iFolio > 0 ? 2 : 1);
				 dtoInsertParamDos.setSecuencia(2);
				 dtoInsertParamDos.setConcepto(funciones.validarCadena(dto.getConcepto()));
				 dtoInsertParamDos.setAplica(dto.getAplica());
				 dtoInsertParamDos.setDiasInv(dto.getDiasInv());
				 dtoInsertParamDos.setValorTasa(dto.getValorTasa());
				 dtoInsertParamDos.setOrigenMov(funciones.validarCadena(dto.getOrigenMovto()));
				 dtoInsertParamDos.setTipoCambio(dto.getTipoCambioActual());
				 dtoInsertParamDos.setNoEmpresa(dto.getIdEmpresa());
				 dtoInsertParamDos.setBeneficiario(dto.getBeneficiario());
				 dtoInsertParamDos.setNoCliente(funciones.validarCadena(""+dto.getIdEmpresa()));
				 
				 dtoInsertParamDos.setIdFormaPago(formaPago);
				 dtoInsertParamDos.setIdTipoDocto(I_TIPO_DOCTO);
				 dtoInsertParamDos.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
				 dtoInsertParamDos.setBSalvoBuenCobro(IS_SALVO_BCOBRO);
				 dtoInsertParamDos.setIdEstatusReg(IS_STATUS_REG);
				 dtoInsertParamDos.setNoDocto(dto.getNoOrden().trim());
				 dtoInsertParamDos.setIdDivisa(dto.getIdDivisa());
				 dtoInsertParamDos.setUsuarioAlta(globalSingleton.getUsuarioLoginDto().getIdUsuario());
				 
				 iRegAfec = inversionesDao.insertarParametroLiquidacion(dtoInsertParamDos);
				 
				 if(iRegAfec <= 0)
					 iFolioRetorno = -500;
			 //}
				 
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:realizarTraspasos");
		}
		return iFolioRetorno;
	}
	
	/**
	 * Cancelación de inversiones
	 * @param iNoOrden
	 * @return
	 */
	public  Map<String, Object> cancelarOrdenesInversion(int iNoOrden, String estatus, String poHeaders)
	{
		int iRegAfec = 0;
		String sRevividor;
		int noEmpresa = 0;
		Map<String, Object> mapRev = new HashMap<String, Object>();
		Map<String, Object> mapSAP = new HashMap<String, Object>();
		try{
			
			if (!poHeaders.trim().equals("")) {
				
				mapSAP =validaCancelacionSAP(iNoOrden, estatus, inversionesDao.consultarEmpresaInversion(iNoOrden));
				
			}
			else{
				mapSAP.put("estatus", true);
				mapSAP.put("mensaje", "");
			}
			
			
			
			if((boolean)mapSAP.get("estatus") || poHeaders.trim().equals("")){
				iRegAfec = inversionesDao.actualizarEstatusOrdenInversion(iNoOrden);
				noEmpresa = inversionesDao.consultarEmpresaInversion(iNoOrden);
				/*--- llama al revividor ---*/
				sRevividor = "N";
	
				RevividorDto dtoRev = new RevividorDto();
	        	dtoRev.setNomForma("ConsultaOrdenesInversion");
				dtoRev.setPsRevividor(sRevividor);
				dtoRev.setNoFolioDet(0);
				dtoRev.setIdTipoOperacion(3200);
				dtoRev.setPsTipoCancelacion("X");
				dtoRev.setIdEstatusMov("P");
				dtoRev.setPsOrigenMov("INV");
				dtoRev.setIdFormaPago(3);
				dtoRev.setBEntregado("N");
				dtoRev.setIdTipoMovto("E");
				dtoRev.setImporte(0);
				dtoRev.setNoEmpresa(noEmpresa);
				dtoRev.setNoCuenta(0);
				dtoRev.setIdChequera("");
				dtoRev.setIdBanco(0);
				dtoRev.setIdUsuario(0);
				dtoRev.setNoDocto(iNoOrden);
				dtoRev.setLote(0);
				dtoRev.setBSalvoBuenCobro("N");
				dtoRev.setFecConfTrans("");
				dtoRev.setIdDivisa("");
				mapRev = inversionesDao.ejecutarRevividor(dtoRev);	
				if (!mapRev.get("result").equals("0"))
					iRegAfec = 0;
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:cancelarOrdenesInversion");
		}
		return mapSAP;
	}
	
	public int cancelarOrdenesInversionSET(int iNoOrden)
	{
		int iRegAfec = 0;
		String sRevividor;
		int noEmpresa = 0;
		Map<String, Object> mapRev = new HashMap<String, Object>();
		try{
			iRegAfec = inversionesDao.actualizarEstatusOrdenInversion(iNoOrden);
			noEmpresa = inversionesDao.consultarEmpresaInversion(iNoOrden);
			/*--- llama al revividor ---*/
			sRevividor = "N";

			RevividorDto dtoRev = new RevividorDto();
        	dtoRev.setNomForma("ConsultaOrdenesInversion");
			dtoRev.setPsRevividor(sRevividor);
			dtoRev.setNoFolioDet(0);
			dtoRev.setIdTipoOperacion(3200);
			dtoRev.setPsTipoCancelacion("X");
			dtoRev.setIdEstatusMov("P");
			dtoRev.setPsOrigenMov("INV");
			dtoRev.setIdFormaPago(3);
			dtoRev.setBEntregado("N");
			dtoRev.setIdTipoMovto("E");
			dtoRev.setImporte(0);
			dtoRev.setNoEmpresa(noEmpresa);
			dtoRev.setNoCuenta(0);
			dtoRev.setIdChequera("");
			dtoRev.setIdBanco(0);
			dtoRev.setIdUsuario(0);
			dtoRev.setNoDocto(iNoOrden);
			dtoRev.setLote(0);
			dtoRev.setBSalvoBuenCobro("N");
			dtoRev.setFecConfTrans("");
			dtoRev.setIdDivisa("");
			mapRev = inversionesDao.ejecutarRevividor(dtoRev);	
			if (!mapRev.get("result").equals("0"))
				iRegAfec = 0;
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:cancelarOrdenesInversion");
		}
		return iRegAfec;
	}
	private Map<String, Object> validaCancelacionSAP(int iNoOrden, String estatus, int empresa) {
		Map<String, Object> result = new HashMap<>();
		List<DT_Polizas_OBPolizas> list_dt_Polizas_OBPolizas;
		try {
			list_dt_Polizas_OBPolizas = inversionesDao.obtieneListaCancelar(iNoOrden, estatus, empresa);
			
			if (list_dt_Polizas_OBPolizas != null && !list_dt_Polizas_OBPolizas.isEmpty()) {
				SOS_PolizasServiceLocator service = new SOS_PolizasServiceLocator();
				SOS_PolizasBindingStub sos_PolizasBindingStub = new SOS_PolizasBindingStub(new URL(service.getHTTP_PortAddress()), service);
				sos_PolizasBindingStub.setUsername(inversionesDao.configuraSet(ConstantesSet.USERNAME_WS_POLIZAS));
				sos_PolizasBindingStub.setPassword(inversionesDao.configuraSet(ConstantesSet.PASSWORD_WS_POLIZAS));
				DT_Polizas_ResponseResponse[] dt_Polizas_ResponseResponse  
						= sos_PolizasBindingStub.SOS_Polizas(list_dt_Polizas_OBPolizas.toArray(new DT_Polizas_OBPolizas[list_dt_Polizas_OBPolizas.size()]));
				
				if (dt_Polizas_ResponseResponse != null && dt_Polizas_ResponseResponse.length != 0) {
					
					result=inversionesDao.insertaBitacoraPoliza(dt_Polizas_ResponseResponse);
					
					if((boolean)result.get("estatus")){
						result.put("estatus", true);
						return result;
					}else{
						result.put("estatus", false);
						return result;
					}
				}
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			result.put("mensaje", "Error al conectarce al web service.");
			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e1)
			+"P:Interfaz, C:ExportacionPolizasBusiness, M:ejecutarExportacionPolizas");
			e1.printStackTrace();
		} catch (AxisFault e) {
			e.printStackTrace();
			result.put("mensaje", "Error al procesar los datos en el web service.");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Interfaz, C:ExportacionPolizasBusiness, M:ejecutarExportacionPolizas");
		
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Interfaz, C:ExportacionPolizasBusiness, M:ejecutarExportacionPolizas");
		}
		return result;
		
	}

	//Inician llamadas de VencimientoDeInversion.js
	
	/**
	 * Este mï¿½todo obtiene la inversiones para vencer,
	 * es utilizado en VencimientoDeInversion.js
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unused")
	public List<MovimientoDto> obtenerVencimientosInversion(ComunInversionesDto dto)
	{
		List<MovimientoDto> listVenc = new ArrayList<MovimientoDto>();
		List<Map<String, Object>> mapConIns = null;
		int iCveOperacion = 0;
		int iFolioRef = 0;
		int iFolioDet = 0;
		boolean bOp4002 = false;
		boolean bOp4003 = false;
		boolean bOp4004 = false;
		int iIndice = 0;
		
		try{
			listVenc = inversionesDao.consultarVencimientoInversion(dto);
			/*
			for(int i = 0; i < listVenc.size(); i ++)
			{
				iCveOperacion = listVenc.get(i).getIdTipoOperacion();
				if(iCveOperacion == 4002)
				{
					iIndice = i;
					iFolioRef = listVenc.get(i).getFolioRef();
					iFolioDet = listVenc.get(i).getNoFolioDet();
					listVenc.get(i).setCveOperacionA(listVenc.get(i).getIdCveOperacion());
					listVenc.get(i).setFolioDetA(listVenc.get(i).getFolioRef());
					bOp4002 = true;
					bOp4003 = false;
					bOp4004 = true;
				}
				//if(iCveOperacion == 4003 && bOp4002 && iFolioRef == listVenc.get(i).getFolioRef())
				if(iCveOperacion == 4003 && bOp4002 && iFolioDet == listVenc.get(i).getFolioRef())
				{
					//iFolioRef = listVenc.get(i).getFolioRef();
					iFolioDet = listVenc.get(i).getNoFolioDet();
					listVenc.get(iIndice).setInteres(listVenc.get(i).getImporte());
					listVenc.get(iIndice).setFolioRefA(listVenc.get(i).getFolioRef());
					listVenc.get(iIndice).setCveOperacionB(listVenc.get(i).getIdCveOperacion());
					listVenc.get(iIndice).setFolioDetB(listVenc.get(i).getNoFolioDet());
					listVenc.get(iIndice).setFolioRefB(listVenc.get(i).getFolioRef());
					bOp4003 = true;
					listVenc.remove(i);
					i--;
				}
				if(bOp4002 && bOp4003)
				{
					//if(iFolioRef == listVenc.get(i).getFolioRef())
					if(iFolioDet == listVenc.get(i).getFolioRef())
					{
						listVenc.get(iIndice).setIsr(listVenc.get(i).getImporte());
						bOp4002 = false;
						bOp4003 = false;
						listVenc.remove(i);
						i--;
					}
					else
						bOp4004 = false;
				}
				else
					bOp4004 = false;
				
				if(!bOp4002 && !bOp4003 && iCveOperacion == 4004 && bOp4004)
				{
					bOp4004 = false;
					listVenc.get(iIndice).setIsr(listVenc.get(i).getImporte());
					listVenc.get(iIndice).setCveOperacionC(listVenc.get(i).getIdCveOperacion());
					listVenc.get(iIndice).setFolioDetC(listVenc.get(i).getNoFolioDet());
					listVenc.get(iIndice).setFolioRefC(listVenc.get(i).getFolioRef());
					listVenc.remove(i);
					//i--;
				}
			}
			*/
			
			//Ciclo para obtener el nombre de la institucion y el contacto con quien se hiso el contrato
			for(int c = 0; c < listVenc.size(); c ++) {
				//listVenc.get(c).setTotalImporte(listVenc.get(c).getImporte() + listVenc.get(c).getInteres() - listVenc.get(c).getIsr());
				mapConIns = inversionesDao.consultarContactoInstitucion(Integer.parseInt(listVenc.get(c).getNoDocto().trim()));
				
				if(mapConIns.size() > 0) {
					listVenc.get(c).setInstFinan(mapConIns.get(0).get("institucion").toString());
					listVenc.get(c).setNomContacto(mapConIns.get(0).get("contacto").toString());
				}
				/*listVenc.get(c).setInteresAnt(listVenc.get(c).getInteres());
				listVenc.get(c).setImpuestoAnt(listVenc.get(c).getIsr());*/
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:obtenerVencimientosInversion");
		}
		return listVenc;
	}
	
	/**
	 * Este mï¿½todo obtiene el nombre del contacto de la orden de inversiï¿½n,
	 * es utilizado en VencimientoDeInversiones.js
	 * @param iNoOrden
	 * @return
	 */
	public List<LlenaComboGralDto> obtenerContactoOrden(int iNoOrden)
	{
		List<LlenaComboGralDto> listCont = new ArrayList<LlenaComboGralDto>();
		try{
			listCont = inversionesDao.consultarContactoOrden(iNoOrden);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:obtenerContactoOrden");
		}
		return listCont;
	}
	
	/**
	 * Este mï¿½todo obtiene los dias anual de la tabla orden_inversion,
	 * es utilizado en VencimientodeInversion.js
	 */
	public List<OrdenInversionDto> obtenerDiasAnual(String dFechaVen, int iBanco, String sChequera, 
													double uTasa, int iNoCuenta, int iNoDocto)
	{
		List<OrdenInversionDto> listOrden = new ArrayList<OrdenInversionDto>();
		try{
			listOrden = inversionesDao.consultarDiasAnual(dFechaVen, iBanco, sChequera, uTasa, iNoCuenta, iNoDocto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:obtenerDiasAnual");
		}
		return listOrden;
	}
	
	/**
	 * Este mï¿½todo obtiene el impuesto, es utilizado en VencimientoDeInversion
	 * @param uCapital
	 * @param uTasa
	 * @param iPlazo
	 * @param iDiasAnual
	 * @param iNoContrato
	 * @param iNoCuenta
	 * @param iNoInstitucion
	 * @return
	 */
	public double calcularImpuesto(double uCapital, int iPlazo, int iDiasAnual, 
			int iNoContrato, int iNoCuenta, int iNoInstitucion)
	{
		double uIsr = 0;
		double uImpuesto = 0;
		boolean bAplicaBisiesto = false;
		try{
			bAplicaBisiesto = inversionesDao.verificarManejaAnioBisiesto(iNoInstitucion);
			uIsr = this.calcularIsr(iPlazo, bAplicaBisiesto, "" + iNoContrato, iNoCuenta);
			uImpuesto = this.calcularInteres(uCapital, uIsr, iPlazo, iDiasAnual);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:calcularImpuesto");
		}
		return uImpuesto;
	}
	
	/**
	 * Este mï¿½todo es utilizado en VencimientoDeInversion, para actualizar
	 * el valor de la tasa de inversiï¿½n
	 * @param uTasa
	 * @param sNoOrden
	 * @param sFecha
	 * @return
	 */
	public int modificarTasa(double uTasa, String sNoOrden, String sFecha, int noEmpresa)
	{
		int iRegAfec = 0;
		
		try{
			iRegAfec = inversionesDao.actualizarTasa(noEmpresa, uTasa, sNoOrden, sFecha);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:actualizarTasa");
			iRegAfec = -1;
		}
		return iRegAfec;
	}
	
	@SuppressWarnings("unchecked")
	public Map ejecutarVencimientoInversion(List<MovimientoDto> listVencInv,double uImporteTxt, double uInteresTxt, 
											double uImpuestoTxt, double uTasaReinversion){
		Map<String, Object> mapRet = new HashMap<String, Object>();
		Map<String, Object> mapEstableceDat = new HashMap<String, Object>();
		double uTipoCambioActual = 0;
		int iFolioOrden = 0;
		
		int iPlazoReinversion = 0;
		boolean bFechaInvalida = false;
		Date dFechaVenc = null;
		Date dFechaValor = null;
		boolean bDiaInhabil = false;
		
		double uCurva = 0;
		double uPotencia = 0;
		double uPrimera = 0;
		double uConst = 0;
		int iRegAfec = 0;
		//int iEmpresaUbicado = 0;
		int iFolioInv = 0;
		double uCapital = 0;
		
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			//iEmpresaUbicado = globalSingleton.getUsuarioLoginDto().getNumeroEmpresa();
			
			
			for(int i = 0; i < listVencInv.size(); i ++)
			{
				if(!listVencInv.get(i).getIdDivisa().equals(globalSingleton.obtenerValorConfiguraSet(771)))
				{
					uTipoCambioActual = inversionesDao.consultarTipoCambio(listVencInv.get(i).getIdDivisa()); 
					//System.out.println("uTipoCambioActual "+ uTipoCambioActual);
					if(uTipoCambioActual <= 0)
					{
						mapRet.put("msgUsuario", "No existe el Tipo de Cambio para la Divisa del contrato, por favor verifique e intente nuevamente...");
						return mapRet;
					}
				}
				else
					uTipoCambioActual = 1;
				
				listVencInv.get(i).setTipoCambio(uTipoCambioActual);
				mapEstableceDat = this.establecerDatosVencimientoInversion(listVencInv.get(i),uImporteTxt, uInteresTxt, uImpuestoTxt);
				
				//System.out.println("mapEstableceDat.get(msgUsuario).toString() ["+ mapEstableceDat.get("msgUsuario").toString()+"]");
				
				if(mapEstableceDat.get("msgUsuario").toString().equals("do"))
				{
					/* If Trim(gobjVarGlobal.valor_Configura_set(268)) = "SI" Then
			            
			                Set prs_Datos = gobjSQL.FunSQLSelectOrdenesInternas(CLng(mfgVencInv.TextMatrix(mfgVencInv.RowSel, COL_NUM_ORDEN)))
			
			                While Not prs_Datos.EOF
			                    plResult = gobjSQL.FunSQLActVencimOrdenInv(prs_Datos("no_orden"))
			                    prs_Datos.MoveNext
			                Wend
			
			                Set prs_Datos = Nothing
			            
			            End If*/
					if(globalSingleton.obtenerValorConfiguraSet(272).trim().equals("SI"))
					{
						if(uTasaReinversion > 0)
						{
							iFolioOrden = this.obtenerFolioReal("no_orden");
							if(iFolioOrden <= 0)
							{
								mapRet.put("msgUsuario", "Error al obtener folio de orden reinversion");
								return mapRet;
							}
							
							iPlazoReinversion = 1;
							dFechaVenc = globalSingleton.getFechaHoy();
							do{
								dFechaVenc = funciones.modificarFecha("d", iPlazoReinversion, dFechaVenc);
								bFechaInvalida = inversionesDao.consultarFechaInhabil(funciones.ponerFecha(dFechaVenc));
								if(bFechaInvalida)
									iPlazoReinversion ++;
							}while(bFechaInvalida);
							
							dFechaValor = globalSingleton.getFechaHoy();
							
							uPotencia = 28 / iPlazoReinversion;
		                    uConst = 36000;
		                    uPrimera = ((Math.pow(((uTasaReinversion / uConst) * iPlazoReinversion) + 1, uPotencia)) - 1) * uConst / 28;
		                    uCurva = funciones.redondearCantidades(uPrimera, 4);
		                    
		                    
		                    OrdenInversionDto dtoInsReinv = new OrdenInversionDto();
			                    dtoInsReinv.setNoEmpresa(listVencInv.get(i).getNoEmpresa());
			                    dtoInsReinv.setNoOrden("" + listVencInv.get(i).getNoDocto().trim());
			                    dtoInsReinv.setUsuarioAlta(globalSingleton.getUsuarioLoginDto().getIdUsuario());
			                    dtoInsReinv.setNoOrdenNva("" + iFolioOrden);
			                    dtoInsReinv.setFecAlta(dFechaValor);
			                    dtoInsReinv.setFecVenc(dFechaVenc);
			                    dtoInsReinv.setTasa(uTasaReinversion);
			                    dtoInsReinv.setTasaCurva28(uCurva);
			                    dtoInsReinv.setPlazo(iPlazoReinversion);
		            
		                    iRegAfec = 0;
		                    iRegAfec = inversionesDao.insertarOrdenReinversion(dtoInsReinv);
		                    
		                    if(iRegAfec <= 0)
		                    {
		                    	mapRet.put("msgUsuario", "No se pudo realizar el movimiento de Reinversiï¿½n");
		                    	return mapRet;
		                    }
		                    
		                    iFolioInv = this.obtenerFolioReal("no_folio_param");
		                    if(iFolioInv <= 0)
							{
								mapRet.put("msgUsuario", "Error al obtener folio param de reinversion");
								return mapRet;
							}
		                    
		                    uCapital = listVencInv.get(i).getImporte() + listVencInv.get(i).getInteres() - listVencInv.get(i).getIsr();
//		                    Integer noCliente = this.inversionesDao.consultarNoClinente(iNumCta);
		                    //Inserta reinversion en parametro
		                    ParametroDto dtoInsRein = new ParametroDto();
			    				dtoInsRein.setNoFolioParam(iFolioInv);
			    				dtoInsRein.setIdTipoOperacion(4000);
			    				dtoInsRein.setNoCuenta(listVencInv.get(i).getNoCuenta());
			    				dtoInsRein.setImporte(uCapital);
			    				dtoInsRein.setFecValor(dFechaValor);
			    				dtoInsRein.setIdGrupo(0);
			    				//error
			    				dtoInsRein.setIdBancoBenef(0);
			    				dtoInsRein.setIdChequeraBenef("");
			    				dtoInsRein.setNoDocto("" + iFolioOrden);
			    				//foliodet
			    				dtoInsRein.setFolioRef(0);
			    				dtoInsRein.setIdEstatusMov("O");
			    				dtoInsRein.setSecuencia(0);
			    				dtoInsRein.setConcepto("ORDEN DE INVERSION");
			    				dtoInsRein.setAplica(1);
			    				dtoInsRein.setDiasInv(iPlazoReinversion);
			    				dtoInsRein.setValorTasa(uTasaReinversion);
			    				dtoInsRein.setOrigenMov("INV");
			    				dtoInsRein.setTipoCambio(uTipoCambioActual);
			    				dtoInsRein.setNoEmpresa(listVencInv.get(i).getNoEmpresa());
			    				
				   				Integer formaPago = 
				   						this.inversionesDao.consultarFormaPagoContrato(listVencInv.get(i).getNoCuenta(), listVencInv.get(i).getNoEmpresa());
			    				
			    				//Valores globales
			    				dtoInsRein.setIdFormaPago(formaPago);
			    				dtoInsRein.setUsuarioAlta(globalSingleton.getUsuarioLoginDto().getIdUsuario());
			    				dtoInsRein.setFecValorOriginal(dtoInsRein.getFecValor());
			    				dtoInsRein.setFecAlta(globalSingleton.getFechaHoy());
			    				dtoInsRein.setFecOperacion(globalSingleton.getFechaHoy());
			    				dtoInsRein.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
			    				dtoInsRein.setAplica(I_APLICA);
			    				dtoInsRein.setBSalvoBuenCobro(IS_SALVO_BCOBRO);
			    				dtoInsRein.setIdEstatusReg("P");
			    				dtoInsRein.setOrigenMov(IS_ORIGEN_MOV);
			    			iRegAfec = inversionesDao.insertarParametroReinversion(dtoInsRein);
			    			
			    			if(iRegAfec <= 0)
			    			{
			    				mapRet.put("msgUsuario", "No se pudo realizar el movimiento de reinversion en parametro");
			    				return mapRet;
			    			}
		                    
			    			Map<String, Object> mapGen = new HashMap<String, Object>();
			    			//Inicia llamada al generador
							GeneradorDto dtoGen = new GeneradorDto();
								dtoGen.setEmpresa(listVencInv.get(i).getNoEmpresa());
								dtoGen.setFolParam(iFolioInv);
								dtoGen.setFolMovi(iFolioInv);
								dtoGen.setFolDeta(0);
								dtoGen.setResult(0);
								dtoGen.setMensajeResp("inicia generador");
								dtoGen.setNomForma("VencimientoDeInversion.js");
								dtoGen.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
							mapGen = inversionesDao.ejecutarGenerador(dtoGen);
							
		                    this.inversionesDao.actualizarEstatusOrdenInversion(
		                    		Integer.valueOf(listVencInv.get(i).getNoDocto().trim()), "V");
		                    
							if(Integer.parseInt(mapGen.get("result").toString()) != 0)
							{
								mapRet.put("msgUsuario", "Error en generador " + mapGen.get("result").toString());
								return mapRet;
							}
						}
					}
					mapRet.put("msgUsuario", "Datos Registrados Correctamente");
				}
				else
				{
					mapRet.put("msgUsuario", mapEstableceDat.get("msgUsuario").toString());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:ejecutarVencimientoInversion");
		}
		return mapRet;
	}
	
	@SuppressWarnings("unchecked")
	public Map establecerDatosVencimientoInversion(MovimientoDto dtoVencInv, double uImporteTxt, double uInteresTxt, double uImpuestoTxt){
		Map<String, Object> mapRet = new HashMap<String, Object>();
		Map<String, Object> mapGen = new HashMap<String, Object>();
		boolean bRealizado = false;
		try{
			String sNoDocto = "";
			int iIdBanco = 0;
			String sIdChequera = "";
			int iIdBancoReg = 0;
			String sIdChequeraReg = "";
			double uCapital = 0;
			double uInteres = 0;
			double uImpuesto = 0;
			int iCveOperacion = 0;
			int iCveOperacionContraria = 0;
			int iFolioGenerador = 0;
			int iGrupo = 0;
			int iNoEmpresa = 0;
			int iNoUsuario = 0;
			int iIdCaja = 0;
			int iRegAfec = 0;
			int iSecuencia = 0;
			int iFolioParam = 0;
			int iEmpresaReg = 0;
			String sNomEmpresaReg = "";
			boolean bIntImpCambia = false;
			Date dFechaValorReg = null;
			
			double uImporteSol = 0;
			boolean validarOrden = false;
			int iEmpresaGen = 0;
			
			
			globalSingleton = GlobalSingleton.getInstancia();
			iNoEmpresa = dtoVencInv.getNoEmpresa(); //globalSingleton.getUsuarioLoginDto().getNumeroEmpresa();
			iIdCaja = globalSingleton.getUsuarioLoginDto().getIdCaja();
			iNoUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			sNoDocto = dtoVencInv.getNoDocto().trim();
			iIdBanco = dtoVencInv.getIdBanco();
			sIdChequera = dtoVencInv.getIdChequera();
			
			String noCliente = this.inversionesDao.obtenerNoCliente(sNoDocto);
			
			List<RubroDto> rubros = 
					this.inversionesDao.consultarRubroOrdenInversion(Integer.valueOf(sNoDocto));
			
			if(!dtoVencInv.isBInversionInterna())
			{
				//Datos tomados de las cajas de texto
				uCapital = uImporteTxt;
				uInteres = uInteresTxt;
				uImpuesto = uImpuestoTxt;
			}
			else
			{
				//Datos tomados del grid principal
				uCapital = dtoVencInv.getImporte();
				uInteres = dtoVencInv.getInteres();
				uImpuesto = dtoVencInv.getIsr();
			}
			
			//CAPITAL
			iCveOperacion = dtoVencInv.getCveOperacionA();
			iCveOperacionContraria = inversionesDao.consultarCveOperacionContraria(iCveOperacion);
			
			iFolioGenerador = this.obtenerFolioReal("no_folio_param");
			iGrupo = iFolioGenerador;
			dFechaValorReg = dtoVencInv.getFecValor();
			
			ParametroDto dtoInsCapital = new ParametroDto();
			dtoInsCapital.setNoFolioParam(iFolioGenerador);
			dtoInsCapital.setIdTipoOperacion(iCveOperacionContraria);
			dtoInsCapital.setIdGrupo(iGrupo);
			dtoInsCapital.setNoCuenta(dtoVencInv.getNoCuenta());
			dtoInsCapital.setImporte(uCapital);
			dtoInsCapital.setImporteOriginal(uCapital);
			if (dFechaValorReg.after(globalSingleton.getFechaHoy()))
				dtoInsCapital.setFecValor(globalSingleton.getFechaHoy());//sFecha tiene que venir del textFecha
			else
				dtoInsCapital.setFecValor(dFechaValorReg);
			dtoInsCapital.setValorTasa(dtoVencInv.getValorTasa());
			dtoInsCapital.setNoDocto(dtoVencInv.getNoDocto().trim());//sNumDocto
			dtoInsCapital.setIdBanco(dtoVencInv.getIdBanco());
			dtoInsCapital.setIdChequera(dtoVencInv.getIdChequera());
			dtoInsCapital.setIdBancoBenef(0);
			dtoInsCapital.setIdChequeraBenef("");
			dtoInsCapital.setConcepto("REGRESO DE INVERSION CAPITAL");
			//Error
			dtoInsCapital.setNoFolioMov(0);
			dtoInsCapital.setFolioRef(dtoVencInv.getFolioDetA());
			dtoInsCapital.setIdEstatusMov("A");
			dtoInsCapital.setSecuencia(0);
			dtoInsCapital.setTipoCambio(dtoVencInv.getTipoCambio());
			//pbInversionInterna
			dtoInsCapital.setNoEmpresa(dtoVencInv.getNoEmpresa());
			dtoInsCapital.setNoCliente(noCliente);
			dtoInsCapital.setBeneficiario("");
			
			Integer formaPago = this.inversionesDao.consultarFormaPagoContrato(dtoVencInv.getNoCuenta(), dtoVencInv.getNoEmpresa());

			//Valores globales
			dtoInsCapital.setIdFormaPago(formaPago);
			dtoInsCapital.setUsuarioAlta(iNoUsuario);
			dtoInsCapital.setFecValorOriginal(dtoInsCapital.getFecValor());
			dtoInsCapital.setFecAlta(globalSingleton.getFechaHoy());//verificar
			dtoInsCapital.setFecOperacion(globalSingleton.getFechaHoy());
			dtoInsCapital.setIdCaja(iIdCaja);
			dtoInsCapital.setIdDivisa(dtoVencInv.getIdDivisa());
			dtoInsCapital.setAplica(I_APLICA);
			dtoInsCapital.setBSalvoBuenCobro(IS_SALVO_BCOBRO);
			dtoInsCapital.setIdEstatusReg("P");
			dtoInsCapital.setOrigenMov(IS_ORIGEN_MOV);
			
			dtoInsCapital.setRubro(rubros.get(0).getIdRubroReg());
			dtoInsCapital.setIdGrupoInv(rubros.get(0).getIdGrupoReg());
				
			iRegAfec = 0;
			iRegAfec = inversionesDao.insertarParametroVencInv(dtoInsCapital); 
			
			if(iRegAfec <= 0)
			{
				mapRet.put("msgUsuario", "No se pudo generar el movimiento de capital");
				return mapRet;
			}
			
			//Inicia interes secuencia 1
			if(uInteres != dtoVencInv.getInteresAnt())
			{
				bIntImpCambia = true;
				iSecuencia = 1;
			}
			else
			{
				bIntImpCambia = false;
				iSecuencia = 0;
			}
			
			iCveOperacion = dtoVencInv.getCveOperacionB();
			iCveOperacionContraria = inversionesDao.consultarCveOperacionContraria(iCveOperacion);
			
			iFolioParam = this.obtenerFolioReal("no_folio_param");
			
			ParametroDto dtoIntSec1 = new ParametroDto();
			dtoIntSec1.setNoFolioParam(iFolioParam);
			dtoIntSec1.setIdTipoOperacion(iCveOperacionContraria);
			dtoIntSec1.setIdGrupo(iGrupo);
			dtoIntSec1.setNoCuenta(dtoVencInv.getNoCuenta());
			dtoIntSec1.setImporte(uInteres);
			dtoIntSec1.setImporteOriginal(uInteres);
			
			if (dFechaValorReg.after(globalSingleton.getFechaHoy()))
				dtoIntSec1.setFecValor(globalSingleton.getFechaHoy());//sFecha tiene que venir del textFecha
			else
				dtoIntSec1.setFecValor(dFechaValorReg);
			
			dtoIntSec1.setValorTasa(dtoVencInv.getValorTasa());
			dtoIntSec1.setNoDocto(dtoVencInv.getNoDocto().trim());//sNumDocto
			dtoIntSec1.setIdBanco(dtoVencInv.getIdBanco());
			dtoIntSec1.setIdChequera(dtoVencInv.getIdChequera());
			dtoIntSec1.setIdBancoBenef(0);
			dtoIntSec1.setIdChequeraBenef("");
			dtoIntSec1.setConcepto("REGRESO DE INVERSION INTERES");
			//Error
			dtoIntSec1.setNoFolioMov(1);
			dtoIntSec1.setFolioRef(1);
			dtoIntSec1.setIdEstatusMov("A");
			dtoIntSec1.setSecuencia(iSecuencia);
			dtoIntSec1.setTipoCambio(dtoVencInv.getTipoCambio());
			//pbInversionInterna
			dtoIntSec1.setNoEmpresa(dtoVencInv.getNoEmpresa());
			dtoIntSec1.setNoCliente(noCliente);
			dtoIntSec1.setBeneficiario("");
			//Valores globales
			dtoIntSec1.setIdFormaPago(formaPago);
			dtoIntSec1.setUsuarioAlta(iNoUsuario);
			dtoIntSec1.setFecValorOriginal(dtoIntSec1.getFecValor());
			dtoIntSec1.setFecAlta(globalSingleton.getFechaHoy());//verificar
			dtoIntSec1.setFecOperacion(globalSingleton.getFechaHoy());
			dtoIntSec1.setIdCaja(iIdCaja);
			dtoIntSec1.setIdDivisa(dtoVencInv.getIdDivisa());
			dtoIntSec1.setAplica(I_APLICA);
			dtoIntSec1.setBSalvoBuenCobro(IS_SALVO_BCOBRO);
			dtoIntSec1.setIdEstatusReg("P");
			dtoIntSec1.setOrigenMov(IS_ORIGEN_MOV);
			
			dtoIntSec1.setRubro(rubros.get(0).getIdRubroInt());
			dtoIntSec1.setIdGrupoInv(rubros.get(0).getIdGrupoInt());

			
			iRegAfec = 0;
			iRegAfec = inversionesDao.insertarParametroVencInv(dtoIntSec1); 
			
			if(iRegAfec <= 0)
			{
				mapRet.put("msgUsuario", "No se pudo generar el movimiento de interes secuencia 1");
				return mapRet;
			}
			//Termina interes secuencia 1
			
			//Inicia interes secuencia 2
			if(iSecuencia == 1)
			{
				iFolioParam = this.obtenerFolioReal("no_folio_param");
				
				ParametroDto dtoIntSec2 = new ParametroDto();
					dtoIntSec2.setNoFolioParam(iFolioParam);
					dtoIntSec2.setIdTipoOperacion(iCveOperacionContraria);
					dtoIntSec2.setIdGrupo(iGrupo);
					dtoIntSec2.setNoCuenta(dtoVencInv.getNoCuenta());
					dtoIntSec2.setImporte(dtoVencInv.getInteresAnt());//Verificar si se setea el valor anterior del importe
					dtoIntSec2.setImporteOriginal(dtoVencInv.getInteresAnt());
					if (dFechaValorReg.after(globalSingleton.getFechaHoy()))
						dtoIntSec2.setFecValor(globalSingleton.getFechaHoy());//sFecha tiene que venir del textFecha
					else
						dtoIntSec2.setFecValor(dFechaValorReg);
					//dtoIntSec2.setFecValor(globalSingleton.getFechaHoy());//sFecha tiene que venir del textFecha
					dtoIntSec2.setValorTasa(dtoVencInv.getValorTasa());
					dtoIntSec2.setNoDocto(dtoVencInv.getNoDocto().trim());
					dtoIntSec2.setIdBanco(dtoVencInv.getIdBanco());
					dtoIntSec2.setIdChequera(dtoVencInv.getIdChequera());
					dtoIntSec2.setIdBancoBenef(0);
					dtoIntSec2.setIdChequeraBenef("");
					dtoIntSec2.setConcepto("REGRESO DE INVERSION INTERES");
					//Error
					dtoIntSec2.setNoFolioMov(1);
					dtoIntSec2.setFolioRef(1);
					dtoIntSec2.setIdEstatusMov("A");
					dtoIntSec2.setSecuencia(2);
					dtoIntSec2.setTipoCambio(dtoVencInv.getTipoCambio());
					//pbInversionInterna
					dtoIntSec2.setNoEmpresa(dtoVencInv.getNoEmpresa());
					dtoIntSec2.setNoCliente(noCliente);
					dtoIntSec2.setBeneficiario("");
					//Valores globales
					dtoIntSec2.setIdFormaPago(formaPago);
					dtoIntSec2.setUsuarioAlta(iNoUsuario);
					dtoIntSec2.setFecValorOriginal(dtoIntSec2.getFecValor());
					dtoIntSec2.setFecAlta(globalSingleton.getFechaHoy());//verificar
					dtoIntSec2.setFecOperacion(globalSingleton.getFechaHoy());
					dtoIntSec2.setIdCaja(iIdCaja);
					dtoIntSec2.setIdDivisa(dtoVencInv.getIdDivisa());
					dtoIntSec2.setAplica(I_APLICA);
					dtoIntSec2.setBSalvoBuenCobro(IS_SALVO_BCOBRO);
					dtoIntSec2.setIdEstatusReg("P");
					dtoIntSec2.setOrigenMov(IS_ORIGEN_MOV);
					dtoIntSec2.setRubro(rubros.get(0).getIdRubroInt());
					dtoIntSec2.setIdGrupoInv(rubros.get(0).getIdGrupoInt());
				
				iRegAfec = 0;
				iRegAfec = inversionesDao.insertarParametroVencInv(dtoIntSec2); 
				
				if(iRegAfec <= 0)
				{
					mapRet.put("msgUsuario", "No se pudo generar el movimiento de interes secuencia 2");
					return mapRet;
				}

			}
			//Termina interes secuencia 2
			
			//Inicia impuesto secuencia 1
			if(uImpuesto != dtoVencInv.getImpuestoAnt())
			{
				bIntImpCambia = true;
				iSecuencia = 1;
			}
			else
			{
				bIntImpCambia = false;
				iSecuencia = 0;
			}
			
			iCveOperacion = dtoVencInv.getCveOperacionC();
			if(iCveOperacion > 0)
			{
				iCveOperacionContraria = inversionesDao.consultarCveOperacionContraria(iCveOperacion);
				iFolioParam = this.obtenerFolioReal("no_folio_param");
				
				ParametroDto dtoImpuestoSec1 = new ParametroDto();
					dtoImpuestoSec1.setNoFolioParam(iFolioParam);
					dtoImpuestoSec1.setIdTipoOperacion(iCveOperacionContraria);
					dtoImpuestoSec1.setIdGrupo(iGrupo);
					dtoImpuestoSec1.setNoCuenta(dtoVencInv.getNoCuenta());
					dtoImpuestoSec1.setImporte(uImpuesto);//Verificar si se setea el valor anterior del impuesto
					dtoImpuestoSec1.setImporteOriginal(uImpuesto);
					
					if (dFechaValorReg.after(globalSingleton.getFechaHoy()))
						dtoImpuestoSec1.setFecValor(globalSingleton.getFechaHoy());//sFecha tiene que venir del textFecha
					else
						dtoImpuestoSec1.setFecValor(dFechaValorReg);
					//dtoImpuestoSec1.setFecValor(globalSingleton.getFechaHoy());//sFecha tiene que venir del textFecha
					dtoImpuestoSec1.setValorTasa(dtoVencInv.getValorTasa());
					dtoImpuestoSec1.setNoDocto(dtoVencInv.getNoDocto().trim());
					dtoImpuestoSec1.setIdBanco(dtoVencInv.getIdBanco());
					dtoImpuestoSec1.setIdChequera(dtoVencInv.getIdChequera());
					dtoImpuestoSec1.setIdBancoBenef(0);
					dtoImpuestoSec1.setIdChequeraBenef("");
					dtoImpuestoSec1.setConcepto("REGRESO DE INVERSION ISR");
					//Error
					dtoImpuestoSec1.setNoFolioMov(1);
					dtoImpuestoSec1.setFolioRef(2);
					dtoImpuestoSec1.setIdEstatusMov("A");
					dtoImpuestoSec1.setSecuencia(iSecuencia);
					dtoImpuestoSec1.setTipoCambio(dtoVencInv.getTipoCambio());
					//pbInversionInterna
					dtoImpuestoSec1.setNoEmpresa(dtoVencInv.getNoEmpresa());
					dtoImpuestoSec1.setNoCliente(noCliente);
					dtoImpuestoSec1.setBeneficiario("");
					//Valores globales
					dtoImpuestoSec1.setIdFormaPago(formaPago);
					dtoImpuestoSec1.setUsuarioAlta(iNoUsuario);
					dtoImpuestoSec1.setFecValorOriginal(dtoImpuestoSec1.getFecValor());
					dtoImpuestoSec1.setFecAlta(globalSingleton.getFechaHoy());//verificar
					dtoImpuestoSec1.setFecOperacion(globalSingleton.getFechaHoy());
					dtoImpuestoSec1.setIdCaja(iIdCaja);
					dtoImpuestoSec1.setIdDivisa(dtoVencInv.getIdDivisa());
					dtoImpuestoSec1.setAplica(I_APLICA);
					dtoImpuestoSec1.setBSalvoBuenCobro(IS_SALVO_BCOBRO);
					dtoImpuestoSec1.setIdEstatusReg("P");
					dtoImpuestoSec1.setOrigenMov(IS_ORIGEN_MOV);
					dtoImpuestoSec1.setRubro(rubros.get(0).getIdRubroISR());
					dtoImpuestoSec1.setIdGrupoInv(rubros.get(0).getIdGrupoISR());
			
				iRegAfec = 0;
				iRegAfec = inversionesDao.insertarParametroVencInv(dtoImpuestoSec1); 
			
				if(iRegAfec <= 0)
				{
					mapRet.put("msgUsuario", "No se pudo generar el movimiento de impuesto secuencia 1");
					return mapRet;
				}
				//Termina impuesto secuencia1
				
				//Inicia impuesto secuencia 2
				if(iSecuencia == 1)
				{
					iFolioParam = this.obtenerFolioReal("no_folio_param");
					
					ParametroDto dtoImpuestoSec2 = new ParametroDto();
						dtoImpuestoSec2.setNoFolioParam(iFolioParam);
						dtoImpuestoSec2.setIdTipoOperacion(iCveOperacionContraria);
						dtoImpuestoSec2.setIdGrupo(iGrupo);
						dtoImpuestoSec2.setNoCuenta(dtoVencInv.getNoCuenta());
						dtoImpuestoSec2.setImporte(dtoVencInv.getImpuestoAnt());//Verificar si se setea el valor anterior del impuesto
						dtoImpuestoSec2.setImporteOriginal(dtoVencInv.getImpuestoAnt());
						if (dFechaValorReg.after(globalSingleton.getFechaHoy()))
							dtoImpuestoSec2.setFecValor(globalSingleton.getFechaHoy());//sFecha tiene que venir del textFecha
						else
							dtoImpuestoSec2.setFecValor(dFechaValorReg);
						//dtoImpuestoSec2.setFecValor(globalSingleton.getFechaHoy());//sFecha tiene que venir del textFecha
						dtoImpuestoSec2.setValorTasa(dtoVencInv.getValorTasa());
						dtoImpuestoSec2.setNoDocto(dtoVencInv.getNoDocto().trim());
						dtoImpuestoSec2.setIdBanco(dtoVencInv.getIdBanco());
						dtoImpuestoSec2.setIdChequera(dtoVencInv.getIdChequera());
						dtoImpuestoSec2.setIdBancoBenef(0);
						dtoImpuestoSec2.setIdChequeraBenef("");
						dtoImpuestoSec2.setConcepto("REGRESO DE INVERSION ISR");
						//Error
						dtoImpuestoSec2.setNoFolioMov(1);
						dtoImpuestoSec2.setFolioRef(2);
						dtoImpuestoSec2.setIdEstatusMov("A");
						dtoImpuestoSec2.setSecuencia(2);
						dtoImpuestoSec2.setTipoCambio(dtoVencInv.getTipoCambio());
						//pbInversionInterna
						dtoImpuestoSec2.setNoEmpresa(dtoVencInv.getNoEmpresa());
						dtoImpuestoSec2.setNoCliente(noCliente);
						dtoImpuestoSec2.setBeneficiario("");
						//Valores globales
						dtoImpuestoSec2.setIdFormaPago(formaPago);
						dtoImpuestoSec2.setUsuarioAlta(iNoUsuario);
						dtoImpuestoSec2.setFecValorOriginal(dtoImpuestoSec1.getFecValor());
						dtoImpuestoSec2.setFecAlta(globalSingleton.getFechaHoy());//verificar
						dtoImpuestoSec2.setFecOperacion(globalSingleton.getFechaHoy());
						dtoImpuestoSec2.setIdCaja(iIdCaja);
						dtoImpuestoSec2.setIdDivisa(dtoVencInv.getIdDivisa());
						dtoImpuestoSec2.setAplica(I_APLICA);
						dtoImpuestoSec2.setBSalvoBuenCobro(IS_SALVO_BCOBRO);
						dtoImpuestoSec2.setIdEstatusReg("P");
						dtoImpuestoSec2.setOrigenMov(IS_ORIGEN_MOV);
						dtoImpuestoSec2.setRubro(rubros.get(0).getIdRubroISR());
						dtoImpuestoSec2.setIdGrupoInv(rubros.get(0).getIdGrupoISR());

					iRegAfec = 0;
					iRegAfec = inversionesDao.insertarParametroVencInv(dtoImpuestoSec2); 
				
					if(iRegAfec <= 0)
					{
						mapRet.put("msgUsuario", "No se pudo generar el movimiento de impuesto secuencia 1");
						return mapRet;
					}
				}
				//Termina impuesto secuencia 2
			}
			
			//INICIA SOLICITUD SECUENCIA 1
			List<ComunInversionesDto> listBanChe = new ArrayList<ComunInversionesDto>();
			listBanChe = inversionesDao.consultarBancoCheqRegreso(Integer.parseInt(dtoVencInv.getNoDocto().trim()));
			if(listBanChe.size() > 0)
			{
				iIdBancoReg = listBanChe.get(0).getIdBanco();
				sIdChequeraReg = listBanChe.get(0).getIdChequera();
				
				iEmpresaReg = dtoVencInv.getNoEmpresa();
				sNomEmpresaReg = "" + dtoVencInv.getNoEmpresa(); 
				
				if(dtoVencInv.isBInversionInterna())
				{
					List<ComunInversionesDto> listEmp = new ArrayList<ComunInversionesDto>();
					listEmp = inversionesDao.consultarEmpresaInterna(iIdBancoReg, sIdChequeraReg);
					if(listEmp.size() > 0)
					{
						iEmpresaReg = listEmp.get(0).getIdEmpresa();
						sNomEmpresaReg = listEmp.get(0).getDescEmpresa();
					}
				}
				
				if(!dtoVencInv.getIdChequera().equals(sIdChequeraReg))
				{
					if(dtoVencInv.isBInversionInterna())
					{
						iCveOperacion = 3800;
					}
					else
					{
						if(iEmpresaReg != iNoEmpresa)
							iCveOperacion = 3801;
	                    else
	                    	iCveOperacion = 3800;
					}
					
					uImporteSol = uCapital + uInteres - uImpuesto;
					
					iFolioParam = this.obtenerFolioReal("no_folio_param");
					
					ParametroDto dtoSolTrasSec1 = new ParametroDto();
						dtoSolTrasSec1.setNoFolioParam(iFolioParam);
						dtoSolTrasSec1.setIdTipoOperacion(iCveOperacion);
						dtoSolTrasSec1.setIdGrupo(iGrupo);
						dtoSolTrasSec1.setNoCuenta(dtoVencInv.getNoCuenta());
						dtoSolTrasSec1.setImporte(uImporteSol);
						dtoSolTrasSec1.setImporteOriginal(uImporteSol);
						if (dFechaValorReg.after(globalSingleton.getFechaHoy()))
							dtoSolTrasSec1.setFecValor(globalSingleton.getFechaHoy());//sFecha tiene que venir del textFecha
						else
							dtoSolTrasSec1.setFecValor(dFechaValorReg);
						dtoSolTrasSec1.setValorTasa(dtoVencInv.getValorTasa());
						dtoSolTrasSec1.setNoDocto(dtoVencInv.getNoDocto().trim());
						dtoSolTrasSec1.setIdBanco(dtoVencInv.getIdBanco());
						dtoSolTrasSec1.setIdChequera(dtoVencInv.getIdChequera());
						dtoSolTrasSec1.setIdBancoBenef(iIdBancoReg);
						dtoSolTrasSec1.setIdChequeraBenef(sIdChequeraReg);
						dtoSolTrasSec1.setConcepto("SOLICITUD DE TRASPASO REGRESO DE INVERSION");
						//Error
						dtoSolTrasSec1.setNoFolioMov(0);
						dtoSolTrasSec1.setFolioRef(0);
						dtoSolTrasSec1.setIdEstatusMov("Q");
						dtoSolTrasSec1.setSecuencia(1);
						dtoSolTrasSec1.setTipoCambio(dtoVencInv.getTipoCambio());
						//pbInversionInterna
						dtoSolTrasSec1.setNoEmpresa(dtoVencInv.getNoEmpresa());
						if(dtoVencInv.isBInversionInterna())
						{
							dtoSolTrasSec1.setNoCliente("" + iEmpresaReg);
							dtoSolTrasSec1.setBeneficiario(sNomEmpresaReg);
						}
						else
						{
							dtoSolTrasSec1.setNoCliente(noCliente);
							dtoSolTrasSec1.setBeneficiario("");
						}
						//Valores globales
						dtoSolTrasSec1.setIdFormaPago(formaPago);
						dtoSolTrasSec1.setUsuarioAlta(iNoUsuario);
						dtoSolTrasSec1.setFecValorOriginal(dtoSolTrasSec1.getFecValor());
						dtoSolTrasSec1.setFecAlta(globalSingleton.getFechaHoy());//verificar
						dtoSolTrasSec1.setFecOperacion(globalSingleton.getFechaHoy());
						dtoSolTrasSec1.setIdCaja(iIdCaja);
						dtoSolTrasSec1.setIdDivisa(dtoVencInv.getIdDivisa());
						dtoSolTrasSec1.setAplica(I_APLICA);
						dtoSolTrasSec1.setBSalvoBuenCobro(IS_SALVO_BCOBRO);
						dtoSolTrasSec1.setIdEstatusReg("P");
						dtoSolTrasSec1.setOrigenMov(IS_ORIGEN_MOV);
						dtoSolTrasSec1.setRubro(rubros.get(0).getIdRubroReg());
						dtoSolTrasSec1.setIdGrupoInv(rubros.get(0).getIdGrupoReg());
				
					iRegAfec = 0;
					iRegAfec = inversionesDao.insertarParametroVencInv(dtoSolTrasSec1); 
				
					if(iRegAfec <= 0)
					{
						mapRet.put("msgUsuario", "No se pudo generar el movimiento de la solicitud secuencia 1");
						return mapRet;
					}
					//TERMINA SOLICITUD SECUENCIA 1
					
					//INICIA SOLICITUD SECUENCIA 2
					iFolioParam = this.obtenerFolioReal("no_folio_param");
					
					ParametroDto dtoSolTrasSec2 = new ParametroDto();
						dtoSolTrasSec2.setNoFolioParam(iFolioParam);
						dtoSolTrasSec2.setIdTipoOperacion(iCveOperacion);
						dtoSolTrasSec2.setIdGrupo(iGrupo);
						dtoSolTrasSec2.setNoCuenta(dtoVencInv.getNoCuenta());
						dtoSolTrasSec2.setImporte(uImporteSol);
						dtoSolTrasSec2.setImporteOriginal(uImporteSol);
						
						if (dFechaValorReg.after(globalSingleton.getFechaHoy()))
							dtoSolTrasSec2.setFecValor(globalSingleton.getFechaHoy());//sFecha tiene que venir del textFecha
						else
							dtoSolTrasSec2.setFecValor(dFechaValorReg);
						dtoSolTrasSec2.setValorTasa(dtoVencInv.getValorTasa());
						dtoSolTrasSec2.setNoDocto(dtoVencInv.getNoDocto().trim());
						dtoSolTrasSec2.setIdBanco(iIdBancoReg);
						dtoSolTrasSec2.setIdChequera(sIdChequeraReg);
						dtoSolTrasSec2.setIdBancoBenef(dtoVencInv.getIdBanco());
						dtoSolTrasSec2.setIdChequeraBenef(dtoVencInv.getIdChequera());
						dtoSolTrasSec2.setConcepto("SOLICITUD DE TRASPASO REGRESO DE INVERSION");
						//Error
						dtoSolTrasSec2.setNoFolioMov(1);
						dtoSolTrasSec2.setFolioRef(1);
						dtoSolTrasSec2.setIdEstatusMov("Q");
						dtoSolTrasSec2.setSecuencia(2);
						dtoSolTrasSec2.setTipoCambio(dtoVencInv.getTipoCambio());
						//pbInversionInterna
						if(dtoVencInv.isBInversionInterna())
						{
							dtoSolTrasSec2.setNoEmpresa(iEmpresaReg);
							dtoSolTrasSec2.setNoCliente("" + dtoVencInv.getNoEmpresa());
							dtoSolTrasSec2.setBeneficiario("");
						}
						else
						{
							dtoSolTrasSec2.setNoEmpresa(dtoVencInv.getNoEmpresa());
							dtoSolTrasSec2.setNoCliente(noCliente);
							dtoSolTrasSec2.setBeneficiario("");
						}
						//Valores globales
						dtoSolTrasSec2.setIdFormaPago(formaPago);
						dtoSolTrasSec2.setUsuarioAlta(iNoUsuario);
						dtoSolTrasSec2.setFecValorOriginal(dtoSolTrasSec2.getFecValor());
						dtoSolTrasSec2.setFecAlta(globalSingleton.getFechaHoy());//verificar
						dtoSolTrasSec2.setFecOperacion(globalSingleton.getFechaHoy());
						dtoSolTrasSec2.setIdCaja(iIdCaja);
						dtoSolTrasSec2.setIdDivisa(dtoVencInv.getIdDivisa());
						dtoSolTrasSec2.setAplica(I_APLICA);
						dtoSolTrasSec2.setBSalvoBuenCobro(IS_SALVO_BCOBRO);
						dtoSolTrasSec2.setIdEstatusReg("P");
						dtoSolTrasSec2.setOrigenMov(IS_ORIGEN_MOV);
						dtoSolTrasSec2.setRubro(rubros.get(0).getIdRubroReg());
						dtoSolTrasSec2.setIdGrupoInv(rubros.get(0).getIdGrupoReg());
				
					iRegAfec = 0;
					iRegAfec = inversionesDao.insertarParametroVencInv(dtoSolTrasSec2); 
				
					if(iRegAfec <= 0)
					{
						mapRet.put("msgUsuario", "No se pudo generar el movimiento de la solicitud secuencia 2");
						return mapRet;
					}
					//TERMINA SOLICITUD SECUENCIA 2
					
					//INICIA EJECUTA TRASPASO SECUENCIA 1
					iCveOperacion = 3700;
					uImporteSol = uCapital + uInteres - uImpuesto;
					
					iFolioParam = this.obtenerFolioReal("no_folio_param");
					
					ParametroDto dtoEjeTrasSec1 = new ParametroDto();
						dtoEjeTrasSec1.setNoFolioParam(iFolioParam);
						dtoEjeTrasSec1.setIdTipoOperacion(iCveOperacion);
						dtoEjeTrasSec1.setIdGrupo(iGrupo);
						dtoEjeTrasSec1.setNoCuenta(dtoVencInv.getNoCuenta());
						dtoEjeTrasSec1.setImporte(uImporteSol);
						dtoEjeTrasSec1.setImporteOriginal(uImporteSol);
						if (dFechaValorReg.after(globalSingleton.getFechaHoy()))
							dtoEjeTrasSec1.setFecValor(globalSingleton.getFechaHoy());//sFecha tiene que venir del textFecha
						else
							dtoEjeTrasSec1.setFecValor(dFechaValorReg);
						dtoEjeTrasSec1.setValorTasa(dtoVencInv.getValorTasa());
						dtoEjeTrasSec1.setNoDocto(dtoVencInv.getNoDocto().trim());
						dtoEjeTrasSec1.setIdBanco(dtoVencInv.getIdBanco());
						dtoEjeTrasSec1.setIdChequera(dtoVencInv.getIdChequera());
						dtoEjeTrasSec1.setIdBancoBenef(iIdBancoReg);
						dtoEjeTrasSec1.setIdChequeraBenef(sIdChequeraReg);
						dtoEjeTrasSec1.setConcepto("TRASPASO REGRESO DE INVERSION");
						//Error
						dtoEjeTrasSec1.setNoFolioMov(0);
						dtoEjeTrasSec1.setFolioRef(2);
						dtoEjeTrasSec1.setIdEstatusMov("A");
						dtoEjeTrasSec1.setSecuencia(1);
						dtoEjeTrasSec1.setTipoCambio(dtoVencInv.getTipoCambio());
						//pbInversionInterna
						if(dtoVencInv.isBInversionInterna())
						{
							dtoEjeTrasSec1.setNoEmpresa(dtoVencInv.getNoEmpresa());
							dtoEjeTrasSec1.setNoCliente("" + iEmpresaReg);
							dtoEjeTrasSec1.setBeneficiario("");
						}
						else
						{
							dtoEjeTrasSec1.setNoEmpresa(dtoVencInv.getNoEmpresa());
							dtoEjeTrasSec1.setNoCliente(noCliente);
							dtoEjeTrasSec1.setBeneficiario("");
						}
						//Valores globales
						dtoEjeTrasSec1.setIdFormaPago(formaPago);
						dtoEjeTrasSec1.setUsuarioAlta(iNoUsuario);
						dtoEjeTrasSec1.setFecValorOriginal(dtoEjeTrasSec1.getFecValor());
						dtoEjeTrasSec1.setFecAlta(globalSingleton.getFechaHoy());//verificar
						dtoEjeTrasSec1.setFecOperacion(globalSingleton.getFechaHoy());
						dtoEjeTrasSec1.setIdCaja(iIdCaja);
						dtoEjeTrasSec1.setIdDivisa(dtoVencInv.getIdDivisa());
						dtoEjeTrasSec1.setAplica(I_APLICA);
						dtoEjeTrasSec1.setBSalvoBuenCobro(IS_SALVO_BCOBRO);
						dtoEjeTrasSec1.setIdEstatusReg("P");
						dtoEjeTrasSec1.setOrigenMov(IS_ORIGEN_MOV);
						dtoEjeTrasSec1.setRubro(rubros.get(0).getIdRubroReg());
						dtoEjeTrasSec1.setIdGrupoInv(rubros.get(0).getIdGrupoReg());
				
					iRegAfec = 0;
					iRegAfec = inversionesDao.insertarParametroVencInv(dtoEjeTrasSec1); 
				
					if(iRegAfec <= 0)
					{
						mapRet.put("msgUsuario", "No se pudo generar el movimiento de traspaso secuencia 1");
						return mapRet;
					}
					//TERMINA EJECUTA TRASPASO SECUENCIA 1
					
					//INICIA EJECUTA TRASPASO SECUENCIA 2
					iFolioParam = this.obtenerFolioReal("no_folio_param");
					
					ParametroDto dtoEjeTrasSec2 = new ParametroDto();
						dtoEjeTrasSec2.setNoFolioParam(iFolioParam);
						dtoEjeTrasSec2.setIdTipoOperacion(iCveOperacion);
						dtoEjeTrasSec2.setIdGrupo(iGrupo);
						dtoEjeTrasSec2.setNoCuenta(dtoVencInv.getNoCuenta());
						dtoEjeTrasSec2.setImporte(uImporteSol);
						dtoEjeTrasSec2.setImporteOriginal(uImporteSol);
						if (dFechaValorReg.after(globalSingleton.getFechaHoy()))
							dtoEjeTrasSec2.setFecValor(globalSingleton.getFechaHoy());//sFecha tiene que venir del textFecha
						else
							dtoEjeTrasSec2.setFecValor(dFechaValorReg);
						dtoEjeTrasSec2.setValorTasa(dtoVencInv.getValorTasa());
						dtoEjeTrasSec2.setNoDocto(dtoVencInv.getNoDocto().trim());
						dtoEjeTrasSec2.setIdBanco(iIdBancoReg);
						dtoEjeTrasSec2.setIdChequera(sIdChequeraReg);
						dtoEjeTrasSec2.setIdBancoBenef(dtoVencInv.getIdBanco());
						dtoEjeTrasSec2.setIdChequeraBenef(dtoVencInv.getIdChequera());
						dtoEjeTrasSec2.setConcepto("TRASPASO REGRESO DE INVERSION");
						//Error
						dtoEjeTrasSec2.setNoFolioMov(1);
						dtoEjeTrasSec2.setFolioRef(1);
						dtoEjeTrasSec2.setIdEstatusMov("A");
						dtoEjeTrasSec2.setSecuencia(2);
						dtoEjeTrasSec2.setTipoCambio(dtoVencInv.getTipoCambio());
						//pbInversionInterna
						if(dtoVencInv.isBInversionInterna())
						{
							dtoEjeTrasSec2.setNoEmpresa(iEmpresaReg);
							dtoEjeTrasSec2.setNoCliente("" + dtoVencInv.getNoEmpresa());
							dtoEjeTrasSec2.setBeneficiario("");
						}
						else
						{
							dtoEjeTrasSec2.setNoEmpresa(dtoVencInv.getNoEmpresa());
							dtoEjeTrasSec2.setNoCliente(noCliente);
							dtoEjeTrasSec2.setBeneficiario("");
						}
						//Valores globales
						dtoEjeTrasSec2.setIdFormaPago(formaPago);
						dtoEjeTrasSec2.setUsuarioAlta(iNoUsuario);
						dtoEjeTrasSec2.setFecValorOriginal(dtoEjeTrasSec2.getFecValor());
						dtoEjeTrasSec2.setFecAlta(globalSingleton.getFechaHoy());//verificar
						dtoEjeTrasSec2.setFecOperacion(globalSingleton.getFechaHoy());
						dtoEjeTrasSec2.setIdCaja(iIdCaja);
						dtoEjeTrasSec2.setIdDivisa(dtoVencInv.getIdDivisa());
						dtoEjeTrasSec2.setAplica(I_APLICA);
						dtoEjeTrasSec2.setBSalvoBuenCobro(IS_SALVO_BCOBRO);
						dtoEjeTrasSec2.setIdEstatusReg("P");
						dtoEjeTrasSec2.setOrigenMov(IS_ORIGEN_MOV);
						dtoEjeTrasSec2.setRubro(rubros.get(0).getIdRubroReg());
						dtoSolTrasSec2.setIdGrupoInv(rubros.get(0).getIdGrupoReg());
				
					iRegAfec = 0;
					iRegAfec = inversionesDao.insertarParametroVencInv(dtoEjeTrasSec2); 
				
					if(iRegAfec <= 0)
					{
						mapRet.put("msgUsuario", "No se pudo generar el movimiento de traspaso secuencia 2");
						return mapRet;
					}
					//TERMINA EJECUTA TRASPASO SECUENCIA 2
				}
			}
			
			//Verificar el estatus de la orden de la inversion
			validarOrden = this.validarOrden(Integer.parseInt(dtoVencInv.getNoDocto().trim()));
			if(validarOrden)
			{
				if(dtoVencInv.isBInversionInterna())
					iEmpresaGen = iNoEmpresa;
				else
					iEmpresaGen = dtoVencInv.getNoEmpresa();
				
				GeneradorDto dtoGen = new GeneradorDto();
					dtoGen.setEmpresa(iNoEmpresa);
					dtoGen.setFolParam(iGrupo);
					dtoGen.setFolMovi(0);
					dtoGen.setFolDeta(0);
					dtoGen.setResult(1);
					dtoGen.setMensajeResp("inicia generador");
					dtoGen.setNomForma("VencimientoDeInversion.js");
					dtoGen.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
				mapGen = inversionesDao.ejecutarGenerador(dtoGen);
				
				if(Integer.parseInt(mapGen.get("result").toString()) != 0)
				{
					mapRet.put("msgUsuario", "Error en generador: " + mapGen.get("result").toString() + "Vencimiento de inversiï¿½n");
					return mapRet;
				}
				mapRet.put("msgUsuario", "do");
			}
			else
			{
				mapRet.put("msgUsuario", "Operación cancelada. Esta solicitud ya está vencida");
				return mapRet;
			}
			/*
			If pbInversionInterna Then
                pl_RegistrosAfectados = QueryParametros(CStr(pl_folio), ps_CveOperacion, ps_Grupo, _
                                            " ", pd_importe_sol, txtFecha.Text, "", _
                                            ps_NumDocto, ps_banco, ps_chequera, _
                                            msfGridDatos.TextMatrix(msfGridDatos.RowSel, COL_ID_BANCO), _
                                            msfGridDatos.TextMatrix(msfGridDatos.RowSel, COL_CHEQUERA), _
                                            "TRASPASO REGRESO DE INVERSION", lerror, _
                                            "1", "1", "A", "2", pdTipoCambioActual, _
                                            pbInversionInterna, plEmpresaReg, _
                                            msfGridDatos.TextMatrix(msfGridDatos.RowSel, COL_NO_EMPRESA), , _
                                            msfGridDatos)
            Else
                pl_RegistrosAfectados = QueryParametros(CStr(pl_folio), ps_CveOperacion, ps_Grupo, _
                                            " ", pd_importe_sol, txtFecha.Text, "", _
                                            ps_NumDocto, ps_banco, ps_chequera, _
                                            msfGridDatos.TextMatrix(msfGridDatos.RowSel, COL_ID_BANCO), _
                                            msfGridDatos.TextMatrix(msfGridDatos.RowSel, COL_CHEQUERA), _
                                            "TRASPASO REGRESO DE INVERSION", lerror, _
                                            "1", "1", "A", "2", pdTipoCambioActual, _
                                            pbInversionInterna, , , , msfGridDatos)
				 * 
				 *   Query parametros
				 ByVal sFolio As String, ByVal sTipoOperacion, _
                 ByVal sGrupo As String, ByVal sNumCta As String, _
                 ByVal sImporte As String, ByVal sFecha As String, _
                 ByVal sTasa As String, ByVal sNumDocto As String, _
                 sBanco As String, sChequera As String, _
                 sBanco_benef As String, sChequera_benef As String, _
                 sConcepto As String, lerror As Long, _
                 Optional sFolioMov As String = "0", _
                 Optional sFolioRef As String = "0", _
                 Optional sEstatusMov As String = "P", _
                 Optional sSecuencia As String = "", _
                 Optional pdTipoCambio As Double = 1#, _
                 Optional pbInversionInterna As Boolean = False, _
                 Optional plEmpresa As Long = 0, _
                 Optional plEmpresaBenef As Long = 0, _
                 Optional psBenef As String = "", _
                 Optional msfDatos As MSFlexGrid) As Long
				 * 
				 * Datos del insert
				 * sFolio, plEmpresaAct, sBanco, _
	              sChequera, sBanco_benef, _
	              sChequera_benef, sTipoOperacion, _
	              IS_FORMA_PAGO, GI_USUARIO, sGrupo, _
	              sFolioMov, sFolioRef, _
	              Format(sFecha, "dd/mm/yyyy"), _
	              pscuenta, _
	              Format(GT_FECHA_HOY, "dd/mm/yyyy"), _
	              Format(sImporte, "#0.#0"), _
	              .TextMatrix(.RowSel, COL_TASA), GI_ID_CAJA, _
	              .TextMatrix(.RowSel, COL_DIVISA), _
	              IS_APLICA, sConcepto, sNumDocto, _
	              sEstatusMov, IS_SALVO_BCOBRO, "P", _
	              IS_ORIGEN_MOV, sSecuencia, _
	              pdTipoCambio, pbInversionInterna, _
	              plEmpresaBenef, psBenef
	              
                 */
			//TERMINA CAPITAL
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:establecerDatosVencimientoInversion");
		}
		return mapRet;
	}
	
	public boolean validarOrden(int iNoOrden){
		String sIdEstatusOrd = "";
		try{
			sIdEstatusOrd = inversionesDao.consultarEstatusOrden(iNoOrden);
			if(sIdEstatusOrd.trim().equals("A"))
				return true;
			else
				return false;
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:validarOrden");
			return false;
		}
	}

	/**
	 * Este mï¿½todo sirve para realizar el reporte de LiquidacionDeInversiones
	 * en LiquidacionDeInversion
	 * @param datos
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteLiquidacionInv(Map parametros){
		JRMapArrayDataSource jrDataSource = null;
		List<Map<String, Object>> listTras = null;
		List<Map<String, Object>> listOrdenReport = null;
		List<Map<String, Object>> listSolPago = null;
		boolean bChkCtaPropia = false;
		try{
			bChkCtaPropia = parametros != null 
							&& !parametros.get("chkCuentaPropia").equals("") 
							&& parametros.get("chkCuentaPropia").equals("true") ? true : false;
			
			if(bChkCtaPropia)
			{
				listTras = inversionesDao.consultarTraspasos(3800, "E", parametros);
				if(listTras.size() > 0)
				{
					jrDataSource = new JRMapArrayDataSource(listTras.toArray());
				}	
				else
				{
					//Si no hay traspasos, obtenemos la 4001
					listOrdenReport = inversionesDao.consultarOrdenInversionReport(parametros);
					jrDataSource = new JRMapArrayDataSource(listOrdenReport.toArray());
				}
			}
			else
			{
				listSolPago = inversionesDao.consultarOrdenInversionReport(parametros);
				jrDataSource = new JRMapArrayDataSource(listSolPago.toArray());
			}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:obtenerTraspasos");
		}
		return jrDataSource;
	}
	
	
	/**
	 * Este mï¿½todo sirve para realizar el reporte de LiquidacionDeInversiones
	 * en LiquidacionDeInversion
	 * @param datos
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JRMapArrayDataSource obtenerReporteLiquidacionInversion(Map parametros){
		JRMapArrayDataSource jrDataSource = null;
		List<Map<String, Object>> listSolPago = null;
		try{
			parametros.put("usuario", "");
			System.out.println("asdd..::");
			listSolPago = inversionesDao.obtenerReporteLiquidacionInversion(parametros);
			
			jrDataSource = new JRMapArrayDataSource(listSolPago.toArray());
			
			System.out.println("El tamaño del datasource es "+ jrDataSource.getRecordCount());
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:obtenerTraspasos");
		}
		return jrDataSource;
	}

	
	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReportesInversion(Map parametros)
	{
		JRMapArrayDataSource jrDataSource = null;
		List<Map<String, Object>> listReport = new ArrayList<Map<String, Object>>();
		ParamReportesDto dtoParams = new ParamReportesDto();
		String sNomReporte = "";
		
		try {
			sNomReporte = parametros.get("nomReporte").toString();
			dtoParams.setFecInicial(funciones.ponerFechaDate(parametros.get("pFecIni").toString()));
			dtoParams.setFecFinal(funciones.ponerFechaDate(parametros.get("pFecFin").toString()));
			dtoParams.setIdDivisa(funciones.validarCadena(parametros.get("pIdDivisa").toString()));
			dtoParams.setBTodasDivisas(funciones.validarCadena(parametros.get("chkTodasDivisas").toString()).equals("true") ? true : false);
			dtoParams.setBActual(funciones.validarCadena(parametros.get("bActual").toString()).equals("true") ? true : false);
			dtoParams.setIdEmpresa(Integer.parseInt(parametros.get("noEmpresa").toString()));
			
			//Condicion para el reporte de Inversiones establecidas para el dï¿½a de hoy
			if(sNomReporte.trim().equals("InvEstablecidasHoy")) {
				listReport = inversionesDao.consultarInvEstHoy(dtoParams);
			
			}
			else if(sNomReporte.trim().equals("VencimientoDeInversiones"))
				listReport = inversionesDao.consultarVencimientoInversiones(dtoParams);
			else if(sNomReporte.trim().equals("VencimientoDeInversionesSal2Prom"))
				listReport = inversionesDao.consultarVencInvSal2Prom(dtoParams);
			else if(sNomReporte.trim().equals("InversionesDiarias"))
				listReport = inversionesDao.consultarInversionesDiarias(dtoParams);
			
			jrDataSource = new JRMapArrayDataSource(listReport.toArray());
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:obtenerReportesInversion");
		}
		return jrDataSource;
	}
	
	public List<LlenaComboEmpresasDto> llenarComboEmpresas() {
		List<LlenaComboEmpresasDto> listEmp = new ArrayList<LlenaComboEmpresasDto>();
		
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			listEmp = inversionesDao.llenarComboEmpresas(globalSingleton.getUsuarioLoginDto().getIdUsuario());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:llenarComboEmpresas");
		}
		return listEmp;
	}
	
	public String modificarIntImpInv(List<Map<String, String>> intImpInv) {
		int resp = 0;
		String msgUsuario = "";
		
		try {
			resp = inversionesDao.modificarIntImpInv(intImpInv);
			
			if(resp <= 0) return "Error al almacenar la informaciï¿½n de la nota";
			
			resp = inversionesDao.modificarMoviIntIsr(intImpInv);
			
			if(resp <= 0) msgUsuario = "Error al almacenar la informaciï¿½n de los ajustes";
			else msgUsuario = "Datos almacenados con exito";
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Inversiones, C:InversionesBusinessImpl, M:modificarIntImpInv");
		}
		return msgUsuario;
	}
	
	public int validaFacultad(int facultad) {
		return inversionesDao.validaFacultad(facultad);
	}
	
	public InversionesDao getInversionesDao() {
		return inversionesDao;
	}

	public void setInversionesDao(InversionesDao inversionesDao) {
		this.inversionesDao = inversionesDao;
	}
	
	/**
	 * @return the repInversionesDao
	 */
	public RepInversionesDao getRepInversionesDao() {
		return repInversionesDao;
	}

	/**
	 * @param repInversionesDao the repInversionesDao to set
	 */
	public void setRepInversionesDao(RepInversionesDao repInversionesDao) {
		this.repInversionesDao = repInversionesDao;
	}

	/**
	 * Obtiene el monto total invertido por fecha y divisa.
	 * Regresa un listado de ConsultaOrdenInversionDto
	 */
	public List<ConsultaOrdenInversionDto> obtenerMontoInvertidoXFecha(String sNoEmpresa, String sDivisa, String sFecIni, String sFecFin)
	{
		List<ConsultaOrdenInversionDto> lResult = new ArrayList<ConsultaOrdenInversionDto>();
		
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			lResult = inversionesDao.obtenerMontoInvertidoXFecha(sNoEmpresa, sDivisa, sFecIni, sFecFin);
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:obtenerOrdenesInversion");
		}
		return lResult;
	}
	
	public String consultarbIsr(String Isr){
		String coisr = "";
		try{
			coisr = inversionesDao.consultarbIsr(Isr);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:consultarbIsr");
		}
		return coisr;
	}
	
	/*PARA ORDEN INVERSION*/
	public List<LlenaComboGralDto> consultarBancoOrdenInversion(Integer cveContrato, 
			Integer idEmpresa, String tipoChequera){
		List<LlenaComboGralDto> listBanc = new ArrayList<LlenaComboGralDto>();
		
		try{
			listBanc = inversionesDao.consultarBancoOrdenInversion(cveContrato, idEmpresa, tipoChequera);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, consultarBancoOrdenInversion");
		}
		return listBanc;
	}
	
	public List<LlenaComboGralDto> consultarBancoOrdenIinstitucion(Integer idEmpresa, Integer cveContrato){
		List<LlenaComboGralDto> listBanc = new ArrayList<LlenaComboGralDto>();
		
		try{
			listBanc = inversionesDao.consultarBancoOrdenIinstitucion(idEmpresa, cveContrato);
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, consultarBancoOrdenIinstitucion");
		}
		return listBanc;
	}
	
	public List<LlenaComboGralDto> consultarChequeraOrdenInversion(Integer idBanco, 
			Integer idEmpresa, String tipoChequera){
		List<LlenaComboGralDto> listBanc = new ArrayList<LlenaComboGralDto>();
		
		try{
			listBanc = inversionesDao.consultarChequeraOrdenInversion(idBanco, idEmpresa, tipoChequera);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, consultarChequeraOrdenInversion");
		}
		return listBanc;

	}

	public List<LlenaComboGralDto> consultarChequeraOrdenInstitucion(Integer idBanco, 
			Integer idEmpresa, Integer cuenta){
		List<LlenaComboGralDto> listBanc = new ArrayList<LlenaComboGralDto>();
		
		try{
			listBanc = inversionesDao.consultarChequeraOrdenInstitucion(idBanco, idEmpresa, cuenta);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, consultarChequeraOrdenInstitucion");
		}
		return listBanc;

	}
	
	public Boolean insertarTmpInvSipo(OrdenInversionDto ordenInversionDto){
	
		Integer afectados = null;
		try{
			afectados = inversionesDao.insertarTmpInvSipo(ordenInversionDto);
			if(afectados > 0)
				return true;
			else
				return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public List<OrdenInversionDto> consultarOrdenesInvPendientes(){
		List<OrdenInversionDto> lista = new ArrayList<OrdenInversionDto>();
		
		try{
			lista = inversionesDao.consultarOrdenesInvPendientes();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, consultarOrdenesInvPendientes");
		}
		return lista;
	}
	
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> insertarOrdenInversion(Integer folioSeqTpInvSipo){
		Map<String, Object> mapRet = new HashMap<String, Object>();
		Map<String,Object>  mapGen = null;
		Integer afectados = null;
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			String fechaHoy = funciones.ponerFechaSola(globalSingleton.getFechaHoy());
			Integer iFolioCuenta = this.obtenerFolioReal(FOLIOCUENTA);
			System.out.println("..::va entrar a insertarOrdenInversion");
			afectados = inversionesDao.insertarOrdenInversion(folioSeqTpInvSipo, 
					globalSingleton.getUsuarioLoginDto().getIdUsuario(), 
					iFolioCuenta, fechaHoy);
			
			if(afectados < 0){
				mapRet.put("msgUsuario", "Error al generar la orden: " + folioSeqTpInvSipo + " operacion 4000");
				return mapRet;
			}
			
			/*Solo para reinversiÃ³n*/
//			afectados = inversionesDao.insertarEstadoCtaInv(folioSeqTpInvSipo, 
//					globalSingleton.getUsuarioLoginDto().getIdUsuario(), 
//					iFolioCuenta, fechaHoy);
//			
//			if(afectados < 0){
//				mapRet.put("msgUsuario", "Error al generar la orden: " + folioSeqTpInvSipo + " operacion 4000");
//				return mapRet;
//			}
			System.out.println("..::va entrar actualizarTmpInvSipo");
			afectados = inversionesDao.actualizarTmpInvSipo(folioSeqTpInvSipo);

			if(afectados < 0){
				mapRet.put("msgUsuario", "Error al actualizar estatus a la orden: " + folioSeqTpInvSipo);
				return mapRet;
			}

			/************************************************************/
			System.out.println("..::va entrar a consultarTmpOrdenInv");
			OrdenInversionDto dto = inversionesDao.consultarTmpOrdenInv(folioSeqTpInvSipo).get(0);
			Integer iFolioOrden = this.obtenerFolioReal("no_orden");
			System.out.println("..::va entrar a consultar tipo cambio");
			Double uTipoCambio = inversionesDao.consultarTipoCambio(dto.getMoneda());
			System.out.println("..::va entrar consultarformadecontrato");
			Integer formaPago = 
					this.inversionesDao.consultarFormaPagoContrato(dto.getNoCuenta(), dto.getNoEmpresa());
			System.out.println("..::va entrar a consultaNoCliente");
            Integer noCliente = this.inversionesDao.consultarNoClinente(dto.getCveContrato());

			
			Integer iFolioInv = this.obtenerFolioReal("no_folio_param");
			ParametroDto dtoInsParam = new ParametroDto();
			dtoInsParam.setNoFolioParam(iFolioInv);
			dtoInsParam.setNoEmpresa(dto.getNoEmpresa());
			dtoInsParam.setIdTipoOperacion(4000);
			dtoInsParam.setIdFormaPago(formaPago);
			dtoInsParam.setNoCuenta(dto.getNoCuenta());
			dtoInsParam.setImporte(dto.getImporte());
			dtoInsParam.setImporteOriginal(dto.getImporte());
			dtoInsParam.setFecValor(dto.getFecVenc());
			dtoInsParam.setFecValorOriginal(dto.getFecVenc());
			dtoInsParam.setFecAlta(globalSingleton.getFechaHoy());
			dtoInsParam.setFecOperacion(funciones.ponerFechaDate(dto.getsFechaValor()));
			dtoInsParam.setGrupo(iFolioInv);
			dtoInsParam.setNoDocto("" + iFolioCuenta);//checar
			dtoInsParam.setNoFolioMov(0);
			dtoInsParam.setFolioRef(0);
			dtoInsParam.setIdEstatusMov("O");
			dtoInsParam.setSecuencia(0);
			dtoInsParam.setConcepto("ORDEN INVERSION");
			dtoInsParam.setAplica(1);
			dtoInsParam.setDiasInv(dto.getPlazo());
			dtoInsParam.setUsuarioAlta(globalSingleton.getUsuarioLoginDto().getIdUsuario());
			dtoInsParam.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
			dtoInsParam.setIdEstatusReg(IS_STATUS_REG);
			dtoInsParam.setIdDivisa(dto.getMoneda());
			dtoInsParam.setValorTasa(dto.getTasa());
			dtoInsParam.setOrigenMov("INV");
			dtoInsParam.setBSalvoBuenCobro(IS_SALVO_BCOBRO);
			dtoInsParam.setTipoCambio(uTipoCambio);
			dtoInsParam.setRubro(dto.getRubro());
			dtoInsParam.setIdGrupoInv(dto.getGrupo());
			
			dtoInsParam.setIdBanco(dto.getIdBanco());
			dtoInsParam.setIdChequera(dto.getIdChequera());
			
			dtoInsParam.setIdBancoBenef(dto.getIdBancoInst());
			dtoInsParam.setIdChequeraBenef(dto.getIdChequeraInst());
			
			dtoInsParam.setNoCliente(noCliente.toString());
			System.out.println("..::va entrar a inserta parametro");
			Integer iRegPaAfec = inversionesDao.insertarParametro(dtoInsParam);
			GeneradorDto dtoGen = new GeneradorDto();
				dtoGen.setEmpresa(dto.getNoEmpresa());
				dtoGen.setFolParam(iFolioInv);
				dtoGen.setFolMovi(iFolioCuenta);
				dtoGen.setFolDeta(0);
				dtoGen.setResult(0);
				dtoGen.setMensajeResp("inicia generador");
				dtoGen.setNomForma("OrdenDeInversion.js");
				dtoGen.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
				System.out.println("..::va entrar ejecutar generador");
			mapGen = inversionesDao.ejecutarGenerador(dtoGen);
			
			if(Integer.parseInt(mapGen.get("result").toString()) != 0)
			{
				mapRet.put("msgUsuario", "Error en generador: " + mapGen.get("result").toString() + " operacion 4000");
				return mapRet;
			}
			if(afectados > 0)
				mapRet.put("msgUsuario", "Datos registrados con el folio: " + iFolioOrden);

			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return mapRet;
	}

	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.middleware.service.InversionesService#obtenerRepLiquidacion(java.lang.Integer, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> obtenerRepLiquidacion(Integer noEmpresa,
			String tipoInversion) {
		return this.getRepInversionesDao().consultarReporteLiqInv(noEmpresa, tipoInversion);
	}
	
	/**
	 * Este mï¿½todo sirve para realizar el reporte de LiquidacionDeInversiones
	 * en LiquidacionDeInversion
	 * @param datos
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteVencimientonInversion(Map parametros){
		JRMapArrayDataSource jrDataSource = null;
		List<Map<String, Object>> listSolPago = null;
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			parametros.put("fechaHoy", funciones.ponerFecha(globalSingleton.getFechaHoy()));
			
			listSolPago = inversionesDao.obtenerReporteVencimientoInversion(parametros);
			jrDataSource = new JRMapArrayDataSource(listSolPago.toArray());
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:obtenerTraspasos");
		}
		return jrDataSource;
	}
	
	@DirectMethod
	public Integer actualizarTmpInvSipo(OrdenInversionDto dto){
		return this.inversionesDao.actualizarTmpInvSipo(dto);
	}

	
	@DirectMethod
	public Integer eliminarTmpInvSipo(Integer folioSeqTpInvSipo){
		return this.inversionesDao.eliminarTmpInvSipo(folioSeqTpInvSipo);
	}
	
	@Override
	public boolean verificarFechaMayor(String sFecha, String sFechaReferencia){
		Date fecha = funciones.ponerFechaDate(sFecha);
		Date fechaRef = funciones.ponerFechaDate(sFechaReferencia);
		
		if(fecha.getTime() <= fechaRef.getTime())
			return false;
		else
			return true;
	}
	
	public List<Map<String, Object>> obtenerInversionesLiqVencidas(Integer usuario, Integer empresa, Integer institucion, String fechaInicial, String fechaFinal){
		return this.inversionesDao.obtenerInversionesLiqVencidas(usuario, empresa, institucion, fechaInicial, fechaFinal);
	}
	
	public List<Map<String, Object>> obtenerInversionesVigentes(Integer usuario, Integer empresa){
		List<Map<String, Object>> inversiones = this.inversionesDao.obtenerInversionesVigentes(usuario, empresa);
		List<Map<String, Object>> resultado = new ArrayList<Map<String, Object>>();
		
		inversiones = this.inversionesDao.obtenerInversionesVigentes(usuario, empresa);
		
		String noInstitucion = "0";
		BigDecimal sumInteres = new BigDecimal(0);
		BigDecimal sumImporte = new BigDecimal(0);
		BigDecimal sumIsr = new BigDecimal(0);
		BigDecimal sumInteresNeto = new BigDecimal(0);
		BigDecimal sumMontoNeto = new BigDecimal(0);
		String institucion = "";
		
		Map<String, Object> map = new HashMap<String,Object>();
		
		for(Map<String,Object> m : inversiones){
			if(!noInstitucion.equals((String)m.get("NO_INSTITUCION")) && 
					!noInstitucion.equals("0")){
				
				
				map = new HashMap<String,Object>();
				map.put("RAZON_SOCIAL","SUBTOTAL");
				map.put("ID_CHEQUERA", "");
				map.put("NO_ORDEN", "");
				map.put("IMPORTE", sumImporte);
				map.put("FEC_ALTA", "");
				map.put("FEC_VENC", "");
				map.put("PLAZO", "");
				map.put("TASA", "");
				map.put("INTERES", sumInteres);
				map.put("ISR", sumIsr);
				map.put("INTERES_NETO", sumInteresNeto);
				map.put("MONTO_NETO", sumMontoNeto);
				map.put("ID_ESTATUS_ORD", "");
				map.put("NO_EMPRESA", "");
				map.put("INSTITUCION", "");
				map.put("NO_INSTITUCION", noInstitucion);
			
				resultado.add(map);
				

				
				map = new HashMap<String,Object>();
				map.put("RAZON_SOCIAL",(String)m.get("INSTITUCION"));
				map.put("ID_CHEQUERA", "");
				map.put("NO_ORDEN", "");
				map.put("IMPORTE", null);
				map.put("FEC_ALTA", "");
				map.put("FEC_VENC", "");
				map.put("PLAZO", "");
				map.put("TASA", "");
				map.put("INTERES", null);
				map.put("ISR", null);
				map.put("INTERES_NETO", null);
				map.put("MONTO_NETO", null);
				map.put("ID_ESTATUS_ORD", "");
				map.put("NO_EMPRESA", "");
				map.put("INSTITUCION", "");
				map.put("NO_INSTITUCION", noInstitucion);
				resultado.add(map);
institucion		= (String)m.get("INSTITUCION");
				sumImporte		= (BigDecimal)m.get("IMPORTE");
				sumInteres		= (BigDecimal)m.get("INTERES");
				sumIsr			= (BigDecimal)m.get("ISR");
				sumInteresNeto	= (BigDecimal)m.get("INTERES_NETO");
				sumMontoNeto	= (BigDecimal)m.get("MONTO_NETO");
				noInstitucion	= (String)m.get("NO_INSTITUCION");
				
//				map = new HashMap<String,Object>();
//				map.put("RAZON_SOCIAL",institucion);
//				map.put("ID_CHEQUERA", "");
//				map.put("NO_ORDEN", "");
//				map.put("IMPORTE", sumImporte);
//				map.put("FEC_ALTA", "");
//				map.put("FEC_VENC", "");
//				map.put("PLAZO", "");
//				map.put("TASA", "");
//				map.put("INTERES", sumInteres);
//				map.put("ISR", sumIsr);
//				map.put("INTERES_NETO", sumInteresNeto);
//				map.put("MONTO_NETO", sumMontoNeto);
//				map.put("ID_ESTATUS_ORD", "");
//				map.put("NO_EMPRESA", "");
//				map.put("INSTITUCION", "");
//				map.put("NO_INSTITUCION", noInstitucion);
				
			}else if(noInstitucion.equals("0")){
				institucion		= (String)m.get("INSTITUCION");
				sumImporte		= (BigDecimal)m.get("IMPORTE");
				sumInteres		= (BigDecimal)m.get("INTERES");
				sumIsr			= (BigDecimal)m.get("ISR");
				sumInteresNeto	= (BigDecimal)m.get("INTERES_NETO");
				sumMontoNeto	= (BigDecimal)m.get("MONTO_NETO");
				noInstitucion	= (String)m.get("NO_INSTITUCION");
				
				map = new HashMap<String,Object>();
				map.put("RAZON_SOCIAL",institucion);
				map.put("ID_CHEQUERA", "");
				map.put("NO_ORDEN", "");
				map.put("IMPORTE", null);
				map.put("FEC_ALTA", "");
				map.put("FEC_VENC", "");
				map.put("PLAZO", "");
				map.put("TASA", "");
				map.put("INTERES", null);
				map.put("ISR", null);
				map.put("INTERES_NETO", null);
				map.put("MONTO_NETO", null);
				map.put("ID_ESTATUS_ORD", "");
				map.put("NO_EMPRESA", "");
				map.put("INSTITUCION", "");
				map.put("NO_INSTITUCION", noInstitucion);
				resultado.add(map);

			}else{
				//System.out.println("Antes "+ sumImporte + " sumando "+ (BigDecimal)m.get("IMPORTE"));
				
				sumImporte		= new BigDecimal(sumImporte.doubleValue() + ((BigDecimal)m.get("IMPORTE")).doubleValue());
				sumInteres		= new BigDecimal(sumInteres.doubleValue() + ((BigDecimal)m.get("INTERES")).doubleValue());//(BigDecimal)m.get("INTERES");
				sumIsr			= new BigDecimal(sumIsr.doubleValue() + ((BigDecimal)m.get("ISR")).doubleValue());//(BigDecimal)m.get("ISR");
				sumInteresNeto	= new BigDecimal(sumInteresNeto.doubleValue() + ((BigDecimal)m.get("INTERES_NETO")).doubleValue());//(BigDecimal)m.get("INTERES_NETO");
				sumMontoNeto	= new BigDecimal(sumMontoNeto.doubleValue() + ((BigDecimal)m.get("MONTO_NETO")).doubleValue());//(BigDecimal)m.get("MONTO_NETO");
				noInstitucion	= (String)m.get("NO_INSTITUCION");
				
//				sumImporte.add((BigDecimal)m.get("IMPORTE"));
//				sumInteres.add((BigDecimal)m.get("INTERES"));
//				sumIsr.add((BigDecimal)m.get("ISR"));
//				sumInteresNeto.add((BigDecimal)m.get("INTERES_NETO"));
//				sumMontoNeto.add((BigDecimal)m.get("MONTO_NETO"));
				//System.out.println("Después "+ sumImporte);
			}
			
			map = new HashMap<String,Object>();
			map.put("RAZON_SOCIAL","SUBTOTAL");
			map.put("ID_CHEQUERA", "");
			map.put("NO_ORDEN", "");
			map.put("IMPORTE", sumImporte);
			map.put("FEC_ALTA", "");
			map.put("FEC_VENC", "");
			map.put("PLAZO", "");
			map.put("TASA", "");
			map.put("INTERES", sumInteres);
			map.put("ISR", sumIsr);
			map.put("INTERES_NETO", sumInteresNeto);
			map.put("MONTO_NETO", sumMontoNeto);
			map.put("ID_ESTATUS_ORD", "");
			map.put("NO_EMPRESA", "");
			map.put("INSTITUCION", "");
			map.put("NO_INSTITUCION", noInstitucion);

			resultado.add(m);
		}
		resultado.add(map);
		
		return resultado;
	}

	@Override
	public String consultaCajas() {
		 String res="";
			try{
				res =inversionesDao.consultarCajas();
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P: Inversiones C:inversionesImpl M: consultarcajas");
					}
			return res;
	}

	
}
