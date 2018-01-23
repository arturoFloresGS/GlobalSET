/**
 * @author Jessica Arelly Cruz Cruz
 * @since 05/01/2011
 */
package com.webset.set.traspasos.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.bancaelectronica.dto.PersonasDto;
import com.webset.set.egresos.service.ConsultaPropuestasService;
import com.webset.set.graficas.CreacionGrafica;
import com.webset.set.ingresos.dto.CatBancoDto;
import com.webset.set.ingresos.dto.CatCtaBancoDto;
import com.webset.set.seguridad.dto.EmpresaDto;
import com.webset.set.traspasos.business.TraspasosBusiness;
import com.webset.set.traspasos.dto.BuscarSolicitudesTraspasosDto;
import com.webset.set.traspasos.dto.CatConceptoTraspDto;
import com.webset.set.traspasos.dto.GrupoDTO;
import com.webset.set.traspasos.dto.ParametroTraspasosDto;
import com.webset.set.traspasos.dto.RubroDTO;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;

import net.sf.jasperreports.engine.JRDataSource;


public class TraspasosAction {
	private Contexto contexto = new Contexto();
	private Bitacora bitacora = new Bitacora();
	private TraspasosBusiness traspasosBusiness;
	private Funciones funciones;
	ConsultaPropuestasService consultaPropuestasService;
	private static Logger logger = Logger.getLogger(TraspasosAction.class);
	
	@DirectMethod
	public int validaFacultadt(int idFacultad) {
		int res = 0;
		
		if(!Utilerias.haveSession(WebContextManager.get()) || (!Utilerias.tienePermiso(WebContextManager.get(),55))&& !Utilerias.tienePermiso(WebContextManager.get(),45)){
			return res;
		}
		
		try {
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			res = consultaPropuestasService.validaFacultad(idFacultad);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Traspasos, C: TraspasosAction, M: validaFacultad");
		}
		return res;
	}
	
	@DirectMethod
	public int validaFacultad(int idFacultad) {
		int res = 0;
		
		try {
			if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
			traspasosBusiness = (TraspasosBusiness)contexto.obtenerBean("traspasosBusiness");
			res = traspasosBusiness.validaFacultad(idFacultad);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Traspasos, C: TraspasosAction, M: validaFacultad");
		}
		return res;
	}
	
	
	/**
	 * settings set method
	 * */
	@DirectMethod
	public boolean configuraSET(int indice) {
		String res = "";
		boolean result = false;
		traspasosBusiness = (TraspasosBusiness) contexto.obtenerBean("traspasosBusiness");
		try {
			if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
			res = traspasosBusiness.consultarSET(indice);
			logger.info("res: "+res);
			if(res.equals("SI"))
				result = true;
			else
				result = false;
			}
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosAction, M:configuraSET");
		}

		return result;
	}
	
	/**
	 * consulta el folioReal
	 * @param tipoFolio
	 * @return
	 */
	@DirectMethod
	public int seleccionarFolioReal(String tipoFolio){
		int folio = 0;
		traspasosBusiness = (TraspasosBusiness) contexto.obtenerBean("traspasosBusiness");
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
			folio = traspasosBusiness.seleccionarFolioReal(tipoFolio);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosAction, M:configuraSET");
		}
		return folio;
	}
	
	@DirectMethod
	public String obtenerFechaHoy(){
		String result = "";
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
			traspasosBusiness = (TraspasosBusiness)contexto.obtenerBean("traspasosBusiness");
			result = traspasosBusiness.consultarFechaHoyS().toString();
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosAction, M:obtenerFechaHoy");
		}
		return result;
	}
	
	/**
	 * metodo que llena el combo de conceptos
	 * @return lista concepto
	 */
	@DirectMethod
	public List<CatConceptoTraspDto> llenarComboConcepto(){
		List<CatConceptoTraspDto> concepto = null;
		traspasosBusiness = (TraspasosBusiness) contexto.obtenerBean("traspasosBusiness");
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
			concepto = traspasosBusiness.llenarComboConcepto();
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosAction, M:llenarComboConcepto");
		}
		return concepto;
	}
	
	
	/**
	 * metodo para llenar el combo Empresa
	 * */
	@DirectMethod
	public List<EmpresaDto> obtenerEmpresas(int usuario){
		List<EmpresaDto> empresa = null;
		traspasosBusiness = (TraspasosBusiness) contexto.obtenerBean("traspasosBusiness");
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
			empresa = traspasosBusiness.obtenerEmpresa(usuario);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosAction, M:obtenerEmpresas");
		}
		return empresa;
	}
	
	
	/**
	 * metodo para llenar el combo Banco
	 * @param empresa
	 * @param bandera
	 * @return banco
	 */
	
	@DirectMethod
	public List<CatBancoDto> obtenerBanco1(int empresa, boolean bandera){
		List<CatBancoDto> banco = null;
		traspasosBusiness = (TraspasosBusiness)contexto.obtenerBean("traspasosBusiness");
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
			banco = traspasosBusiness.obtenerBanco(empresa, bandera);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosAction, M:obtenerBanco1");
		}
		return banco;
	}
	
	/**
	 * obtener las chequeras
	 * @param empresa
	 * @param banco
	 * @param bandera
	 * @return cheque
	 */
	@DirectMethod
	public List<CatCtaBancoDto> obtenerChequera1(int empresa, int banco, boolean bandera){
		List<CatCtaBancoDto> cheque = null;
		traspasosBusiness = (TraspasosBusiness) contexto.obtenerBean("traspasosBusiness");
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
			cheque = traspasosBusiness.obtenerChequera(empresa, banco, bandera);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosAction, M:obtenerChequera1");
		}
		return cheque;
	}
	
	/**
	 * llamado a la funcion consultarSaldoFin del business
	 * FunSQLSelect901
	 * @param emp
	 * @param ban
	 * @param chequera
	 * @param bAnterior
	 * @param fecha
	 * @return saldo
	 */
	@DirectMethod
	public double obtenerSaldoFinal(String datos){
		double saldo = 0;
		int emp = 0;
		int ban = 0; 
		
		String chequera = ""; 
		String aux = "";
		String aux2 = "";
		String fecha = "";
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
				traspasosBusiness = (TraspasosBusiness) contexto.obtenerBean("traspasosBusiness");
				Gson gson = new Gson();
				List<Map<String, String>> objParams = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				emp = Integer.parseInt(objParams.get(0).get("empresa"));
				ban = Integer.parseInt(objParams.get(0).get("banco"));
				chequera = objParams.get(0).get("chequera");
				aux = objParams.get(0).get("bAnterior");
				fecha = objParams.get(0).get("fecha");
				aux2 = objParams.get(0).get("pbInversion");
				
				boolean bAnterior = aux.equals("true");
				boolean pbInversion = aux2.equals("true");
				
					saldo = traspasosBusiness.sumarImporte(emp, chequera, ban, pbInversion, bAnterior, fecha);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosAction, M:obtenerSaldoFinal");
		}
		return saldo;
	}
	
	/**
	 * funcion verificar_divisa
	 * @param banco
	 * @param chequera
	 * @param bandera
	 * @return divisa
	 */
	@DirectMethod
	public List<Map<String,String>> verificarDivisa(int banco, String chequera, boolean pbInversion){
		List<Map<String,String>> divisa = null;
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
				traspasosBusiness = (TraspasosBusiness) contexto.obtenerBean("traspasosBusiness");
				
					divisa = traspasosBusiness.verificaDivisa(banco, chequera, pbInversion);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosAction, M:verificarDivisa");
		}
		return divisa;
	}
	
	
	/**
	 * funcion correspondiente a cheques_por_entregar
	 * @param datos
	 * @return
	 */
	@DirectMethod
	public double sumarImporte(String datos){
		double suma = 0;
		int emp = 0;
		int banco = 0; 
		String chequera = ""; 
		String fecha = "";
		boolean pbInversion = false;
		boolean bAnterior = false;
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
				traspasosBusiness = (TraspasosBusiness) contexto.obtenerBean("traspasosBusiness");
				Gson gson = new Gson();
				List<Map<String, String>> objParams = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				emp = Integer.parseInt(objParams.get(0).get("empresa"));
				banco = Integer.parseInt(objParams.get(0).get("banco"));
				chequera = objParams.get(0).get("chequera");
				suma = traspasosBusiness.sumarImporte(emp, chequera, banco, pbInversion, bAnterior, fecha);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosAction, M:sumarImporte");
		}
		return suma;
	}
	
	/**
	 * consulta la referencia e institucion
	 * @param emp
	 * @param ban
	 * @param cheque
	 * @return
	 */
	@DirectMethod
	public List<Map<String,String>> obtenerDatosReferencia (int emp, int ban, String cheque){
		List<Map<String,String>> datos = null;
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
			traspasosBusiness = (TraspasosBusiness) contexto.obtenerBean("traspasosBusiness");
			datos = traspasosBusiness.obtenerDatosReferencia(emp, ban, cheque);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosAction, M:consultarDatosReferencia");
		}
		return datos;
	}
	
	
	@DirectMethod
	public List<CatBancoDto> obtenerBancoA(int empresa2, String divisa, boolean pbInversion, String tipoChequera){
		List<CatBancoDto> banco = null;
		traspasosBusiness = (TraspasosBusiness) contexto.obtenerBean("traspasosBusiness");
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
				banco = traspasosBusiness.obtenerBancoA(empresa2, divisa, pbInversion, tipoChequera);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosAction, M:obtenerBancoA");
		}
		return banco;
	}
	
	
	@DirectMethod
	public List<CatCtaBancoDto> obtenerChequeraA(int emp, int ban, boolean pbInversion, String cheqExcluye){
		List<CatCtaBancoDto> banco = null;
		traspasosBusiness = (TraspasosBusiness) contexto.obtenerBean("traspasosBusiness");
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
				banco = traspasosBusiness.obtenerChequeraA(emp, ban, pbInversion, cheqExcluye);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosAction, M:consultarChequeraA");
		}
		return banco;
	}
	
	/**
	 * llena el combo de interempresas
	 * @param usuario
	 * @return lista empresas
	 */
	@DirectMethod
	public List<PersonasDto> llenarComboInterEmpresas(){
		List<PersonasDto> empresa = null;
		traspasosBusiness = (TraspasosBusiness) contexto.obtenerBean("traspasosBusiness");
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
				empresa = traspasosBusiness.llenarComboInterEmpresas();
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosAction, M:llenarComboInterEmpresas");
		}
		return empresa;
	}
	
	/**
	 * llena el combo de bancos inter
	 * @param empresa
	 * @return lista de bancos
	 */
	@DirectMethod
	public List<CatBancoDto> llenarComboBancoInter(int empresa){
		List<CatBancoDto> banco = null;
		traspasosBusiness = (TraspasosBusiness) contexto.obtenerBean("traspasosBusiness");
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
				banco = traspasosBusiness.llenarComboBancoInter(empresa);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosAction, M:llenarComboBancoInter");
		}
		return banco;
	}
	
	/**
	 * llena el combo de interempresas hacia donde va el traspaso
	 * @param empresa
	 * @param usuario
	 * @return lista de empresas
	 */
	@DirectMethod
	public List<PersonasDto> llenarComboInterEmpresas2(int empresa){
		System.out.println("llenarComboInterEmpresas2");
		List<PersonasDto> empresas = null;
		traspasosBusiness = (TraspasosBusiness) contexto.obtenerBean("traspasosBusiness");
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
				empresas = traspasosBusiness.llenarComboInterEmpresas2(empresa);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosAction, M:llenarComboInterEmpresas2");
		}
		return empresas;
	}
	
	/**
	 * llena el combo de bancos hacia donde va el traspaso
	 * @param empresa2
	 * @param pbInversion
	 * @param bancoDe
	 * @param chequeraDe
	 * @return lista de bancos
	 */
	@DirectMethod
	public List<CatBancoDto> llenarComboBancoInter2(int empresa2, boolean pbInversion, int bancoDe, String chequeraDe){
		List<CatBancoDto> banco = null;
		traspasosBusiness = (TraspasosBusiness) contexto.obtenerBean("traspasosBusiness");
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45))
				banco = traspasosBusiness.llenarComboBancoInter2(empresa2, pbInversion, bancoDe, chequeraDe);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosAction, M:llenarComboBancoInter2");
		}
		return banco;
	}
	
	/**
	 * concultar campo b_concentradora
	 * @param empresa
	 * @return
	 */
	@DirectMethod
	public String consultarConcentradora(int empresa){
		String result="";
		traspasosBusiness = (TraspasosBusiness) contexto.obtenerBean("traspasosBusiness");
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
				result = traspasosBusiness.consultarConcentradora(empresa);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosAction, M:consultarConcentradora");
		}
		return result;
	}
	
	@DirectMethod
	public List<Map<String,String>> obtenerClabe (int empresa, int banco, String chequera){
		List<Map<String,String>> datos = null;
		try{	
			if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
				traspasosBusiness = (TraspasosBusiness) contexto.obtenerBean("traspasosBusiness");
				datos = traspasosBusiness.obtenerClabe(empresa, banco, chequera);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosAction, M:obtenerClabe");
		}
		return datos;
	}
	
	
	@DirectMethod
	public Map<String, Object> insertarSolicitudTraspaso(String data){
		
		Gson gson = new Gson();
		funciones = new Funciones();
		String ban1 = "", ban2 = "";
		
		List<Map<String, String>> objParams = gson.fromJson(data, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
		Map<String,Object> result= new HashMap<String,Object>();
		
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
				traspasosBusiness = (TraspasosBusiness) contexto.obtenerBean("traspasosBusiness");
			
			ban1 = objParams.get(0).get("regreso");
			ban2 = objParams.get(0).get("bandera");
			
			ParametroTraspasosDto parametro = new ParametroTraspasosDto();
			
			
			parametro.setNoEmpresa(Integer.parseInt(objParams.get(0).get("empresa")));
			parametro.setIdTipoOperacion(Integer.parseInt(objParams.get(0).get("operacion")));
			parametro.setIdChequera(objParams.get(0).get("chequera"));
			parametro.setIdBanco(Integer.parseInt(objParams.get(0).get("banco")));
			parametro.setIdBancoBenef(Integer.parseInt(objParams.get(0).get("bancoBenef")));
			parametro.setImporte(Double.parseDouble(objParams.get(0).get("importe")));
			parametro.setImporteOriginal(Double.parseDouble(objParams.get(0).get("importe")));
			parametro.setFecValor(funciones.ponerFechaDate(objParams.get(0).get("fecha")));
			parametro.setFecValorOriginal(funciones.ponerFechaDate(objParams.get(0).get("fecha")));
			parametro.setFecAlta(funciones.ponerFechaDate(objParams.get(0).get("fecha")));
			parametro.setFecOperacion(funciones.ponerFechaDate(objParams.get(0).get("fecha")));
			parametro.setUsuarioAlta(Integer.parseInt(objParams.get(0).get("usuario")));
			parametro.setIdChequeraBenef(objParams.get(0).get("chequeraBenef"));
			parametro.setIdCaja(Integer.parseInt(objParams.get(0).get("caja")));
			parametro.setBeneficiario(objParams.get(0).get("beneficiario"));
			parametro.setBeneficiarion(objParams.get(0).get("beneficiarion"));
			parametro.setNoCliente(objParams.get(0).get("cliente"));
			parametro.setConcepto2(objParams.get(0).get("concepto"));			
			parametro.setIdGrupoFlujoEgreso( Integer.parseInt(objParams.get(0).get("idGrupoEgreso") ) );
			parametro.setIdRubroFlujoEgreso( Integer.parseInt(objParams.get(0).get("idRubroEgreso") ) );
			parametro.setIdGrupoFlujoIngreso( Integer.parseInt(objParams.get(0).get("idGrupoIngreso") ) );
			parametro.setIdRubroFlujoIngreso( Integer.parseInt(objParams.get(0).get("idRubroIngreso") ) );
			parametro.setNoDocto(objParams.get(0).get("noDocto"));
			
			System.out.println( parametro.getIdGrupoFlujoEgreso());
			System.out.println( parametro.getIdGrupoFlujoIngreso());
			
			if(objParams.get(0).get("ubicacion").equals("DESC"))
			{
				parametro.setConcepto("Emp:"+objParams.get(0).get("empresa")+" "+
				objParams.get(0).get("concepto")+" a emp:"+objParams.get(0).get("cliente"));
			}
			else
				parametro.setConcepto(objParams.get(0).get("concepto"));
			if(ban1 != null && ban1.equals("true"))
				parametro.setBRegreso(true);
			else if(ban2 != null && ban2.equals("false"))
				parametro.setBRegreso(false);
			if(ban2 != null && ban2.equals("true"))
				parametro.setBandera(true);
			else if(ban2 != null && ban2.equals("false"))
				parametro.setBandera(false);
			if(objParams.get(0).get("referencia") != null)
				parametro.setPsReferencia(objParams.get(0).get("referencia"));
			else
				parametro.setPsReferencia("");
			if(objParams.get(0).get("fechaInversion") != null && !objParams.get(0).get("fechaInversion").equals(""))
			{
				parametro.setFecInversion(funciones.ponerFechaDate(objParams.get(0).get("fechaInversion")));
			}
			else
			{
				parametro.setFecInversion(funciones.ponerFechaDate(objParams.get(0).get("fecha")));
			}
			
			result = traspasosBusiness.insertarSolicitudTraspaso(parametro);
			}
		} catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosAction, M:insertarSolicitudTraspaso");
		}
		return result;
	}

	/** de aqui en edelante se agregan las llamadas a las concultas referenctes a ejecucion de traspasos*/
	
	/**
	 * llenar combo general
	 * @param campoUno
	 * @param campoDos
	 * @param tabla
	 * @param condicion
	 * @param orden
	 * @return
	 */
	@DirectMethod
	public List<LlenaComboGralDto>llenarCombo(String campoUno, String campoDos, String tabla, String condicion, String orden){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		LlenaComboGralDto dto= new LlenaComboGralDto();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
			traspasosBusiness = (TraspasosBusiness) contexto.obtenerBean("traspasosBusiness");
			dto.setCampoUno(campoUno!=null && !campoUno.equals("")?campoUno:"");
			dto.setCampoDos(campoDos!=null && !campoDos.equals("")?campoDos:"");
			dto.setTabla(tabla!=null && !tabla.equals("")?tabla:"");
			dto.setCondicion(condicion!=null && !condicion.equals("")?condicion:"");
			dto.setOrden(orden!=null && !orden.equals("")?orden:"");
			list=traspasosBusiness.llenarComboGral(dto);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Traspasos, C:TraspasosBusiness, M:llenarCombo");	
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboDivisaDto> llenarComboDivisa(){
		List<LlenaComboDivisaDto> divisa = null;
		traspasosBusiness = (TraspasosBusiness) contexto.obtenerBean("traspasosBusiness");
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
			divisa = traspasosBusiness.llenarComboDivisa();
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosAction, M:llenarComboDivisa");
		}
		return divisa;
	}
	
	@DirectMethod
	public List<BuscarSolicitudesTraspasosDto>consultarSolicitudesTraspaso(String data)
	{
		Gson gson = new Gson();
		List<Map<String, String>> objParams = gson.fromJson(data, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		BuscarSolicitudesTraspasosDto result= new BuscarSolicitudesTraspasosDto();
		List<BuscarSolicitudesTraspasosDto> resultado = new ArrayList<BuscarSolicitudesTraspasosDto>();
		boolean mismoBanco = false;
		boolean empresaActual = false;
		boolean speua = false;
		boolean internacional = false;
		boolean interbancaria = false;
		boolean inversion = false;
		funciones = new Funciones();
		
		try
		{
			//if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
			traspasosBusiness = (TraspasosBusiness) contexto.obtenerBean("traspasosBusiness");
			
			if(objParams.get(0).get("banMismoBanco").equals("true"))
				mismoBanco = true;
			else
				mismoBanco = false;
			if(objParams.get(0).get("banEmpresaActual").equals("true"))
				empresaActual = true;
			else
				empresaActual = false;
			if(objParams.get(0).get("banSPEUA").equals("true"))
				speua = true;
			else
				speua= false;
			if(objParams.get(0).get("banInternacional").equals("true"))
				internacional = true;
			else
				internacional = false;
			if(objParams.get(0).get("banInversion").equals("true"))
				inversion = true;
			else 
				inversion = false;
			if(objParams.get(0).get("optInter").equals("true"))
				interbancaria = true;
			else
				interbancaria = false;
			
			result.setDescCveOperacion(objParams.get(0).get("DescOperacion"));
			
			if(objParams.get(0).get("DescOperacion").equals("PAGO INTEREMPRESAS"))
				result.setIdTipoOperacion(3801);
			else
				result.setIdTipoOperacion(Integer.parseInt(objParams.get(0).get("TipoOperacion")));
			
			if(objParams.get(0).get("banInversion").equals("true") || objParams.get(0).get("chkTodos").equals("true"))  
				result.setIdBanco(0);//Integer.parseInt(objParams.get(0).get(""))
			else
				result.setIdBanco(Integer.parseInt(objParams.get(0).get("Valor")));
			
			result.setOpcionMismoBanco(mismoBanco);
			result.setOpcionEmpresaActual(empresaActual);
			result.setNoEmpresa(Integer.parseInt(objParams.get(0).get("Empresa")));
			result.setIdDivisa(objParams.get(0).get("Divisa"));
			result.setOpcionSpeua(speua); 
			result.setIdUsuario(Integer.parseInt(objParams.get(0).get("Usuario")));
			result.setFechaIni(funciones.ponerFechaDate(objParams.get(0).get("fechaIni")));
			result.setFechaFin(funciones.ponerFechaDate(objParams.get(0).get("fechaFin")));
			result.setOpcionInternacional(internacional);
//			result.setIdBanco(Integer.parseInt(objParams.get(0).get("banco")));
			result.setOpcionInversion(inversion);
			result.setOpcionInterbancaria(interbancaria);
			
			resultado = traspasosBusiness.consultarSolicitudesTraspasos(result);
//			System.out.println("consulta trasp tamaño "+resultado.size());
			if(resultado==null) resultado = new ArrayList<BuscarSolicitudesTraspasosDto>();
			
			//Creacion de graficas
			if(resultado!=null && resultado.size()>0)
			{
				//Nombre de la grafica.
				String sName = objParams.get(0).get("Empresa")+objParams.get(0).get("Valor")+objParams.get(0).get("Divisa");
				String sAux=objParams.get(0).get("fechaIni");
				String sAux1=objParams.get(0).get("fechaFin");
				if(sAux!=null && !sAux.equals("")) {
					sAux = sAux.trim();
					sAux1 = sAux1.trim();
					sName += (!sAux.equals("")) ? sAux.replaceAll("/", "") : "";
					sName += (!sAux1.equals("")) ? sAux1.replaceAll("/", "") : "";
				}
				
				
				//Grafica: FormaPago - NumeroMovimientosFormaPago.
				Map<String, Double> datosPie = new HashMap<String, Double>();
				//Grafica: Banco - Importe Total.
				Map<String, Double> datosBarras = new HashMap<String, Double>();
				
				for(int i=0; i<resultado.size(); i++){
					BuscarSolicitudesTraspasosDto dataTraspasos = (BuscarSolicitudesTraspasosDto)resultado.get(i);
					
					if(datosPie.containsKey(dataTraspasos.getConcepto())){
						double dImporte = datosPie.get(dataTraspasos.getConcepto());
						datosPie.remove(dataTraspasos.getConcepto());
						datosPie.put(dataTraspasos.getConcepto(), (dataTraspasos.getImporte()+dImporte));
					}
					else {
						datosPie.put(dataTraspasos.getConcepto(), dataTraspasos.getImporte());
					}
					
					if(datosBarras.containsKey(dataTraspasos.getNomEmpresa())){
						double dImporte = datosBarras.get(dataTraspasos.getNomEmpresa());
						datosBarras.remove(dataTraspasos.getNomEmpresa());
						datosBarras.put(dataTraspasos.getNomEmpresa(), (dataTraspasos.getImporte()+dImporte));
					}
					else {
						datosBarras.put(dataTraspasos.getNomEmpresa(), dataTraspasos.getImporte());
					}
				}
				
				CreacionGrafica cg = new CreacionGrafica();
				cg.crearGraficaPie   (objParams.get(0).get("Usuario")+"", "EjecucionTraspasos"+sName, "Importe total por Tipo de Operaciï¿½n", datosPie);
				cg.crearGraficaBarras(objParams.get(0).get("Usuario")+"", "EjecucionTraspasos"+sName, "Importe total por Empresa", "Importe", datosBarras, true);
				cg.crearGraficaLineas(objParams.get(0).get("Usuario")+"", "EjecucionTraspasos"+sName, "Importe total por Empresa", "Importe", datosBarras, true);
			} //Fin graficas
			//}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosAction, M:consultarSolicitudesTraspaso");
		}
		return resultado;
	}
	
	
	@DirectMethod
	public List<BuscarSolicitudesTraspasosDto>llenarComboClave(String data){
		Gson gson = new Gson();
		List<Map<String, String>> objParams = gson.fromJson(data, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		BuscarSolicitudesTraspasosDto result= new BuscarSolicitudesTraspasosDto();
		List<BuscarSolicitudesTraspasosDto> resultado = new ArrayList<BuscarSolicitudesTraspasosDto>();
		boolean mismoBanco = false;
		boolean empresaActual = false;
		boolean speua = false;
		boolean inversion = false;
		funciones = new Funciones();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
			traspasosBusiness = (TraspasosBusiness) contexto.obtenerBean("traspasosBusiness");
			
			if(objParams.get(0).get("banMismoBanco").equals("true"))
				mismoBanco = true;
			else
				mismoBanco = false;
			if(objParams.get(0).get("banEmpresaActual").equals("true"))
				empresaActual = true;
			else
				empresaActual = false;
			if(objParams.get(0).get("banSPEUA").equals("true"))
				speua = true;
			else
				speua= false;
			if(objParams.get(0).get("banInversion").equals("true"))
				inversion = true;
			else 
				inversion = false;
			
			result.setDescCveOperacion(objParams.get(0).get("DescOperacion"));
			if(objParams.get(0).get("DescOperacion").equals("PAGO INTEREMPRESAS"))
				result.setIdTipoOperacion(3801);
			else
				result.setIdTipoOperacion(Integer.parseInt(objParams.get(0).get("TipoOperacion")));
			if(objParams.get(0).get("banInversion").equals("true") || objParams.get(0).get("chkTodos").equals("true"))  
				result.setIdBanco(0); //Integer.parseInt(objParams.get(0).get(""))
			else
				result.setIdBanco(Integer.parseInt(objParams.get(0).get("Valor")));
			
			result.setOpcionMismoBanco(mismoBanco);
			result.setOpcionEmpresaActual(empresaActual);
			result.setNoEmpresa(Integer.parseInt(objParams.get(0).get("Empresa")));
			result.setIdDivisa(objParams.get(0).get("Divisa"));
			result.setOpcionSpeua(speua); 
			result.setIdUsuario(Integer.parseInt(objParams.get(0).get("Usuario")));
			result.setFechaIni(funciones.ponerFechaDate(objParams.get(0).get("fechaIni")));
			result.setFechaFin(funciones.ponerFechaDate(objParams.get(0).get("fechaFin")));
			result.setOpcionInversion(inversion);
			
			resultado = traspasosBusiness.llenarComboClave(result);
			}
		} catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosAction, M:llenarComboClave");
		}
		return resultado;
	}
	
	/**
	 * envia los datos para escribir el layout
	 * @param data
	 * @return
	 */
	@DirectMethod
	public Map<String, Object> ejecutarSolicitudes(String data, int opcTipoEnvioBanamex,int h2hAfrd){
		Gson gson = new Gson();
		funciones = new Funciones();
		List<BuscarSolicitudesTraspasosDto> listDto = new ArrayList<BuscarSolicitudesTraspasosDto>();
		List<Map<String, String>> objParams = gson.fromJson(data, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		Map<String,Object> result= new HashMap<String,Object>();
		boolean opcionMismoBanco = false;
		boolean opcionEmpresaActual = false;
		boolean opcionSpeua = false;
		boolean opcionInternacional = false;
		boolean opcionInversion = false;
		boolean opcionInterbancaria = false;
		boolean chkH2HSantander = false;
		boolean cambioDivisa = false;
		String chequera = "";
		String chequeraB = "";
		//double impTot = 0;
		//boolean ban;
		String noFolioDet = "";
		List<Map<String, String>> traspasosIguales = new ArrayList<Map<String, String>>();
		//Map<String, String> traspIgual = new HashMap<String, String>(); 
		
		try{
			//System.out.println("Entra al Actioncon la OpEnvio:.."+opcTipoEnvioBanamex);
		//	System.out.println("Entra al Actioncon la h2h:.."+h2hAfrd);
			//if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
			traspasosBusiness = (TraspasosBusiness) contexto.obtenerBean("traspasosBusiness");
			if(objParams.get(0).get("opcionMismoBanco").equals("true"))
				opcionMismoBanco = true; else opcionMismoBanco = false;
			if(objParams.get(0).get("opcionEmpresaActual").equals("true"))
				opcionEmpresaActual = true; else opcionEmpresaActual = false;
			if(objParams.get(0).get("opcionSpeua").equals("true"))
				opcionSpeua = true; else opcionSpeua = false;
			if(objParams.get(0).get("opcionInternacional").equals("true"))
				opcionInternacional = true; else opcionInternacional = false;
			if(objParams.get(0).get("opcionInversion").equals("true"))
				opcionInversion = true; else opcionInversion = false;
			if(objParams.get(0).get("opcionInterbancaria").equals("true"))
				opcionInterbancaria = true; else opcionInterbancaria = false;
			if(objParams.get(0).get("cambioDivisa").equals("true"))
				cambioDivisa = true; else cambioDivisa = false;
			if(objParams.get(0).get("chkH2HSantander").equals("true"))
				chkH2HSantander = true; else chkH2HSantander = false;
			
			
			for(int i = 0; i<objParams.size(); i++)
			{
				BuscarSolicitudesTraspasosDto parametroDto = new BuscarSolicitudesTraspasosDto();
				
				parametroDto.setCambioDivisa(cambioDivisa);
				parametroDto.setOpcionMismoBanco(opcionMismoBanco);
				parametroDto.setOpcionEmpresaActual(opcionEmpresaActual);
				parametroDto.setOpcionSpeua(opcionSpeua);
				parametroDto.setChkH2HSantander(chkH2HSantander);
				parametroDto.setOpcionInternacional(opcionInternacional);
				parametroDto.setOpcionInversion(opcionInversion);
				parametroDto.setOpcionInterbancaria(opcionInterbancaria);
				//opciones del frame tipos de envio HSBC
				parametroDto.setOpcionBitalH2H(false);
				parametroDto.setOpcionBitalNormal(false);
				//opciones del frame tipos de envio banamex
				parametroDto.setOpcionBanamexNormal(false);
				parametroDto.setOpcionBanamexTEF(false);
				parametroDto.setOpcionBanamexMassPayment(false);
				parametroDto.setOpcionCitiBankFlatFile(false);
				parametroDto.setOpcionCitiBankPaylinkMN(false);
				parametroDto.setOpcionCitiBankACH(false);
				parametroDto.setDescCveOperacion(objParams.get(0).get("DescOperacion"));
				if(objParams.get(0).get("DescOperacion").equals("PAGO INTEREMPRESAS"))
					parametroDto.setIdTipoOperacion(3801);
				else
					parametroDto.setIdTipoOperacion(objParams.get(0).get("idTipoOperacion") != null ? Integer.parseInt(objParams.get(i).get("idTipoOperacion")):0);
				parametroDto.setFecOperacion(funciones.ponerFechaDate(objParams.get(i).get("fechaOperacion")));
				parametroDto.setDescUsuarioBital(objParams.get(i).get("usuarioBital") != null ? objParams.get(i).get("usuarioBital") : "");
				parametroDto.setDescServicioBital(objParams.get(i).get("servicioBital") != null ? objParams.get(i).get("servicioBital") : "");
				parametroDto.setNoEmpresa(objParams.get(i).get("noEmpresa")!= null ? Integer.parseInt(objParams.get(i).get("noEmpresa")):0);
				parametroDto.setIdContratoMass(objParams.get(i).get("contratoMass")!= null? Integer.parseInt(objParams.get(i).get("contratoMass")):0);
				parametroDto.setBLayoutComerica(objParams.get(i).get("layoutComerica") != null ? objParams.get(i).get("layoutComerica") : "");
				parametroDto.setCmbBenef(objParams.get(i).get("cmbBenef") != null ? objParams.get(i).get("cmbBenef") : "");
				parametroDto.setIdBanco(objParams.get(0).get("banco")!= null ? Integer.parseInt(objParams.get(0).get("banco")) : 0);
				parametroDto.setNoEmpresaBenef(objParams.get(i).get("noEmBenf") != null ? Integer.parseInt(objParams.get(i).get("noEmBenf")) : 0);
				parametroDto.setLoteEntrada(objParams.get(i).get("loteE") != null ? Integer.parseInt(objParams.get(i).get("loteE")) : 0);
				parametroDto.setInstFinan(objParams.get(i).get("instFinan") != null ? objParams.get(i).get("instFinan") : "");
				parametroDto.setIdBancoBenef(objParams.get(i).get("idBanBenef") != null ? Integer.parseInt(objParams.get(i).get("idBanBenef")) : 0);
				parametroDto.setNoFolioDet(objParams.get(i).get("noFolioDet") != null ? Integer.parseInt(objParams.get(i).get("noFolioDet")) : 0);
				parametroDto.setImporte(objParams.get(i).get("importe") != null ? Double.parseDouble(objParams.get(i).get("importe")): 0);
				parametroDto.setNoDocto(objParams.get(i).get("documento") != null ? objParams.get(i).get("documento") : "");
				parametroDto.setPlazaBenef(objParams.get(i).get("plazaB") != null ? objParams.get(i).get("plazaB") : "");
				parametroDto.setIdChequeraBenef(objParams.get(i).get("chequeraB") != null ? objParams.get(i).get("chequeraB") : "");
				parametroDto.setIdChequera(objParams.get(i).get("idChequera") != null ?  objParams.get(i).get("idChequera") : "");				
				parametroDto.setBeneficiario(objParams.get(i).get("beneficiario") != null ? objParams.get(i).get("beneficiario") : "");
				parametroDto.setSucursalDestino(objParams.get(i).get("sucDestino") != null ? objParams.get(i).get("sucDestino") : "");
				parametroDto.setIdDivisa(objParams.get(i).get("idDivisa") != null ? objParams.get(i).get("idDivisa") : "");
				parametroDto.setNoCliente(objParams.get(i).get("cliente") != null ? objParams.get(i).get("cliente") : "");
				parametroDto.setClabeBenef(objParams.get(i).get("clabeBenef") == null || objParams.get(i).get("clabeBenef") == "" ? "" : objParams.get(i).get("clabeBenef"));
				parametroDto.setConcepto(objParams.get(i).get("concepto") != null ? objParams.get(i).get("concepto") : "");
		//		System.out.println("concepto traspasos "+objParams.get(i).get("concepto"));
				parametroDto.setTipoCambio(objParams.get(0).get("tipoCambio") != null ? Double.parseDouble(objParams.get(i).get("tipoCambio")) : 0);
				parametroDto.setIdUsuario(Integer.parseInt(objParams.get(0).get("usuario")));
				parametroDto.setRfcBenef(objParams.get(i).get("rfcBenef") != null ? objParams.get(i).get("rfcBenef") : "");
				parametroDto.setAba(objParams.get(i).get("aba") != null ? objParams.get(i).get("aba") : "");
				parametroDto.setSwiftCode(objParams.get(i).get("swift") != null ? objParams.get(i).get("swift") : "");
				parametroDto.setNomEmpresa(objParams.get(i).get("nomEmpresa") != null ? objParams.get(i).get("nomEmpresa") : "");
			
				/*parametro.aba= regSelec[i].get('abaBenef');
	       		parametro.swift
				*/
				chequera = objParams.get(i).get("idChequera") != null ?  objParams.get(i).get("idChequera") : "";
				chequeraB = objParams.get(i).get("chequeraB") != null ?  objParams.get(i).get("chequeraB") : "";
				//impTot += objParams.get(i).get("importe") != null ? Double.parseDouble(objParams.get(i).get("importe")): 0;
				//ban = false;
				
				for(int ii = i+1; ii<objParams.size(); ii++) {
					if(chequera.equals(objParams.get(ii).get("idChequera")) && chequeraB.equals(objParams.get(ii).get("chequeraB")) && 
							!noFolioDet.equals(objParams.get(ii).get("noFolioDet"))) {
						
						noFolioDet += objParams.get(ii).get("noFolioDet") + ","; 
						//impTot += Double.parseDouble(objParams.get(ii).get("importe"));
						//ban = true;
					}
				}
				/*if(ban) {
					traspIgual.put("chequera", chequera);
					traspIgual.put("chequeraB", chequeraB);
					traspIgual.put("importeTot", impTot + "");
					traspIgual.put("bIguales", "true");
					traspasosIguales.add(traspIgual);
				}*/
				
				/*if(noFolioDet.indexOf(objParams.get(i).get("noFolioDet")) != -1)
					parametroDto.setBSumImp(true);
				else
					parametroDto.setBSumImp(false);*/
				
				listDto.add(parametroDto);
			}
			//impTot = 0;
			/*for (BuscarSolicitudesTraspasosDto listDtoDetalle : listDto) {
				impTot += listDtoDetalle.getImporte();
			}*/
			//traspIgual.put("importeTot", impTot + "");
			result=traspasosBusiness.ejecutarSolicitudTraspaso(listDto, traspasosIguales,opcTipoEnvioBanamex,h2hAfrd);
			System.out.println("Salto al bisnnes");
			//}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Traspasos, C:TraspasosAction, M:ejecutarSolicitudes");	
			e.printStackTrace();
		}
		return result;
	}
	
	@DirectMethod
	public JRDataSource reporteDetArchTraspInv(Map<String, Object> datos, ServletContext context) {
		JRDataSource jrDataSource = null;
		System.out.println("reporte action");
		try {
			traspasosBusiness = (TraspasosBusiness) contexto.obtenerBean("traspasosBusiness", context);
			jrDataSource = traspasosBusiness.reporteDetArchTraspInv(datos.get("archivo") != null ? datos.get("archivo").toString() : "");
						
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:TraspasosAction, M:reporteDetArchTraspInv");	
		}
		return jrDataSource;
	}
	
	@DirectMethod
	public List<LlenaComboEmpresasDto> llenaComboEmpresas(int noUsuario){
		List<LlenaComboEmpresasDto> listaResultado = new ArrayList<LlenaComboEmpresasDto>();
		try{	
			if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
			traspasosBusiness = (TraspasosBusiness) contexto.obtenerBean("traspasosBusiness");
			listaResultado = traspasosBusiness.llenaComboEmpresas(noUsuario);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Traspasos, C: TraspasosAction, M: llenaComboEmpresas");
		}return listaResultado;
	}
	
	@DirectMethod
	public String cancelaMovimiento(String foliosCancelados){
		String mensaje = "";
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
			traspasosBusiness = (TraspasosBusiness)contexto.obtenerBean("traspasosBusiness");
			mensaje = traspasosBusiness.cancelaMovimiento(foliosCancelados);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Traspasos, C: TraspasosAction, M: cancelaMovimiento");
		}return mensaje;
	}
	
	
	
	@DirectMethod
	public String autoDesAuto(String datos, String autoDesAuto, String pass) {
		String res = "";
		Gson gson = new Gson();
		List<Map<String, String>> objParams = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
		try {
			if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
			traspasosBusiness = (TraspasosBusiness)contexto.obtenerBean("traspasosBusiness");
			res = traspasosBusiness.autoDesAuto(objParams, autoDesAuto, pass);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Traspasos, C: TraspasosAction, M: autoDesAuto");
		}
		return res;
	}
	
	
	@DirectMethod
	public List<GrupoDTO> llenarComboGrupo(String idTipoGrupo){
		
				
		List<GrupoDTO> grupo = null;
		
		traspasosBusiness = (TraspasosBusiness)contexto.obtenerBean("traspasosBusiness");
				
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
			grupo = traspasosBusiness.llenarComboGrupo(idTipoGrupo);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosAction, M:llenarComboGrupo");
		}
		return grupo;
	}
	
	@DirectMethod
	public List<GrupoDTO> llenarComboGrupoVX(String idTipoGrupo, int noEmpresa){
		
				
		List<GrupoDTO> grupo = null;
		
		traspasosBusiness = (TraspasosBusiness)contexto.obtenerBean("traspasosBusiness");
				
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && (Utilerias.tienePermiso(WebContextManager.get(),55))||Utilerias.tienePermiso(WebContextManager.get(),45)){
			grupo = traspasosBusiness.llenarComboGrupoVX(idTipoGrupo, noEmpresa);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosAction, M:llenarComboGrupo");
		}
		return grupo;
	}
	
	@DirectMethod
	public List<RubroDTO> llenarComboRubros(int idGrupo, int noEmpresa){
		
		System.out.println("Entra = " + idGrupo);
				
		List<RubroDTO> rubros = null;
		
		traspasosBusiness = (TraspasosBusiness)contexto.obtenerBean("traspasosBusiness");
				
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && 
					(Utilerias.tienePermiso(WebContextManager.get(),55))||
					Utilerias.tienePermiso(WebContextManager.get(),45)){
			rubros = traspasosBusiness.llenarComboRubro(idGrupo, noEmpresa);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosAction, M:llenarComboGrupo");
		}
		return rubros;
	}

	
	
}//End class
