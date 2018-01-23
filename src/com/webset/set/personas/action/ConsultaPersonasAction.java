package com.webset.set.personas.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.personas.dto.ConsultaPersonasDto;
import com.webset.set.personas.service.ConsultaPersonasService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;

public class ConsultaPersonasAction {
	Contexto contexto = new Contexto();
	Bitacora bitacora = new Bitacora();
	ConsultaPersonasService objConsultaPersonasService;
	
	@DirectMethod
	public List<ConsultaPersonasDto> llenaComboEmpresas(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.llenaComboEmpresas();
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: llenaComboEmpresas");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> llenaComboTipoPersona(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.llenaComboTipoPersona();
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: llenaComboTipoPersona");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> llenaComboBancoCP(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.llenaComboTipoPersona();
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: llenaComboTipoPersona");
		}return listaResultado;
	}
	@DirectMethod
	public List<ConsultaPersonasDto> llenaComboDivisaCP(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.llenaComboDivisaCP();
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: llenaComboTipoPersona");
		}return listaResultado;
	}
	
	@DirectMethod
	public String validaDatos(String tipoPersona){
		String mensaje = "";
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			System.out.println("Aqui llega validar datos action");
			System.out.println(tipoPersona);
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			mensaje = objConsultaPersonasService.validaDatos(tipoPersona);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C:ConsultaPersonasAction, M: vaidaDatos");
		}
		System.out.println("Aqui llega al mensaje");
		return mensaje;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> llenaGrid(String tipoPersona, String equivalePersona, String razonSocial, String paterno, 
											   String materno, String nombre, boolean inactivas){
		
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.llenaGrid(tipoPersona, equivalePersona, razonSocial, paterno, materno, 
																  nombre, inactivas);
			System.out.println("Aqui llega el llena grid"+listaResultado.size());
			}
		}
		catch(Exception e){
			System.out.println("error en llena grid action");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: llenaGrid");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> llenaGridCP(String tipoPersona, String noPersona ){
		
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.llenaGridCP(tipoPersona, noPersona);
			System.out.println("Aqui llega el llena grid"+listaResultado.size());
			}
		}
		catch(Exception e){
			System.out.println("error en llena grid action");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: llenaGrid");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> llenaGridMedios(String equivale){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		try{
			System.out.println("ROOOOOOOOOOOOOOOOOOOOOOOOOO");
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			System.out.println("Aqui llega el llena grid");
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.llenaGridMedios(equivale);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: llenaGrid");
		}return listaResultado;
	}
	
	public HSSFWorkbook reportePersonas(String tipoPersona,ServletContext context){
		HSSFWorkbook wb=null;
		try {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl",context);
			wb=objConsultaPersonasService.reportePersonas(tipoPersona);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: reportePersonas");
		}return wb;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> comboEstadoCivil (){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.comboEstadoCivil();
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: comboEstadoCivil");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> llenaComboGrupo(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.llenaComboGrupo();	
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: llenaComboGrupo");
		}return listaResultado;		
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> llenaActividadEconomica(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();		
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.llenaActividadEconomica();
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: llenaActividadEconomica");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> llenaActividadGenerica (){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.llenaActividadGenerica();
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: llenaActividadGenerica");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> llenaEstadoCivil(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.llenaEstadoCivil();
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: llenaEstadoCivil");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> llenaPais(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.llenaPais();
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: llenaEstado");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> llenaEstado(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.llenaEstado();
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: llenaEstado");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> llenaDireccion(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.llenaDireccion();
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: llenaDireccion");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> llenaGiro(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.llenaGiro();
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: llenaGiro");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> llenaCaja(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		try{			
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.llenaCaja();	
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: llenaCaja");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> llenaRiesgo(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.llenaRiesgo();
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: llenaRiesgo");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> comboTamano(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		try{			
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.comboTamano();	
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: comboTama\u00f1o");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> comboCalidad(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.comboCalidad();
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: comboCalidad");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> comboTipoInmueble(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.comboTipoInmueble();
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: comboTipoInmueble");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> comboFormaPago (){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		System.out.println("Entra al action de comboFormaPagoProv");
		try{			
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.comboFormaPago();
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: comboFormaPagoProv");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> comboFormaPagoProv (int noPersona, int noEmpresa){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		System.out.println("Entra al action de comboFormaPagoProv");
		try{			
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.comboFormaPagoProv(noPersona, noEmpresa);	
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: comboFormaPagoProv");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> comboTEF (){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.comboTEF();
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: comboTef");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> comboPayment (){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.comboPayment();
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: comboPayment");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> obtienePersonaFisica (int noEmpresa, String noPersona, String tipoPersona){
		System.out.println("entro");
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();		
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			System.out.println("Llega al action de obtiene");
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.obtienePersonaFisica(noEmpresa, noPersona, tipoPersona);
			System.out.println(listaResultado.size() + " tama\u00f1o obtiene");
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: obtienePersonaFisica");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> obtieneDatos (int noEmpresa, String noPersona, String tipoPersona){
		System.out.println("entro");
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();		
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			System.out.println("Llega al action de obtiene");
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.obtieneDatos(noEmpresa, noPersona, tipoPersona);
			System.out.println(listaResultado.size() + " tama\u00f1o obtiene");
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: obtieneDatos");
		}return listaResultado;
	}
	
	@DirectMethod
	public String configuraSet(int indice){
		String mensaje = "";
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			mensaje = objConsultaPersonasService.configuraSet(indice);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: configuraSet");
		}return mensaje;
	}
	
	@DirectMethod
	public String proveedorBasico(int noEmpresa, int noPersona){
		String cadena = "";
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			System.out.println("llega action proveedor");			
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			cadena = objConsultaPersonasService.proveedorBasico(noEmpresa, noPersona);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: proveedorBasico");
		}return cadena;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> obtieneDireccion (int noEmpresa, String persona, String tipoPersona){
		System.out.println("entro");
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();		
		try{
		//	if (Utilerias.haveSession(WebContextManager.get())
				//	&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			System.out.println("Llega al action de obtiene");
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.obtieneDireccion(noEmpresa, persona, tipoPersona);
			System.out.println(listaResultado.size() + " tama\u00f1o obtiene");
		//	}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: obtieneDireccion");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> obtieneCaja(int noEmpresa){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			System.out.println(" action caja");
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.obtieneCaja(noEmpresa);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: obtieneCaja");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> obtenerDatosEmpresa(int noEmpresa){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			System.out.println("entra al action de obtener DatosEmpresa");
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.obtenerDatosEmpresa(noEmpresa);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: obtenerDatosEmpresa");
		}return listaResultado;
	}
		
	@DirectMethod
	public String validaDatosInsertUpdate (String registro){
		System.out.println(registro);
		String mensaje = "";
		Gson gson = new Gson();
		List<Map<String, String>> registroGson = gson.fromJson(registro, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			mensaje = objConsultaPersonasService.validaDatosInsertUpdate(registroGson);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: validaDatosInsertUpdate");
		}return mensaje;
	}
	
	@DirectMethod
	public String guardarRelacion (String registro){
		System.out.println("HOLAAaaa");
		String mensaje = "";
		Gson gson = new Gson();
		List<Map<String, String>> registroGson = gson.fromJson(registro, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			mensaje = objConsultaPersonasService.altaRelaciones(registroGson);
			}
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: aceptar");
		}return mensaje;
	}
	
	
	@DirectMethod
	public String guardarPM (String registro){
		String mensaje = "";
		Gson gson = new Gson();
		List<Map<String, String>> registroGson = gson.fromJson(registro, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			mensaje = objConsultaPersonasService.guardarPM(registroGson);
			}
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: aceptar");
		}return mensaje;
	}
	
	@DirectMethod
	public String guardarF (String registro){
		String mensaje = "";
		Gson gson = new Gson();
		List<Map<String, String>> registroGson = gson.fromJson(registro, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			mensaje = objConsultaPersonasService.guardarF(registroGson);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: guardarF");
		}return mensaje;
	}
	
	@DirectMethod
	public String guardarPF (String registro){
		String mensaje = "";
		Gson gson = new Gson();
		List<Map<String, String>> registroGson = gson.fromJson(registro, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			mensaje = objConsultaPersonasService.guardarPF(registroGson);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: guardarPF");
		}return mensaje;
	}
	
	@DirectMethod
	public String guardarDireccion (String registro){
		String mensaje = "";
		Gson gson = new Gson();
		List<Map<String, String>> registroGson = gson.fromJson(registro, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			mensaje = objConsultaPersonasService.guardarDireccion(registroGson);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: guardarDireccion");
		}return mensaje;
	}
	
	@DirectMethod
	public String guardarCasa (String registro){
		String mensaje = "";
		Gson gson = new Gson();
		List<Map<String, String>> registroGson = gson.fromJson(registro, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			mensaje = objConsultaPersonasService.guardarCasa(registroGson);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: guardarCasa");
		}return mensaje;
	}

	@DirectMethod
	public String guardaNuevoMedioContacto (String no_persona, String mail,int empre){
		String mensaje = "";
		Gson gson = new Gson();
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			mensaje = objConsultaPersonasService.guardaNuevoMedioContacto(no_persona, mail,empre);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: guardarCasa");
		}return mensaje;
	}
	@DirectMethod
	public String guardarIB (String registro){
		String mensaje = "";
		Gson gson = new Gson();
		List<Map<String, String>> registroGson = gson.fromJson(registro, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			mensaje = objConsultaPersonasService.guardarIB(registroGson);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: guardarIB");
		}return mensaje;
	}
	
	//funcion para obtener datos de empresa  darlos de alta
	@DirectMethod
	public String aceptar (String registro){
		String mensaje = "";
		Gson gson = new Gson();
		List<Map<String, String>> registroGson = gson.fromJson(registro, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
				objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
				mensaje = objConsultaPersonasService.aceptar(registroGson);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: aceptar");
		}return mensaje;
	}
	
	@DirectMethod
	public String verificaEmpresa (int noEmpresa){
		String mensaje = "";
		
		try{			
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			System.out.println("action verifica empresa");
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			mensaje = objConsultaPersonasService.verificaEmpresa(noEmpresa);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: verificaEmpresa");
		}return mensaje;
	}
	
	@DirectMethod
	public int obtieneFolio(String tipoFolio){
		int folio = 0;
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			folio = objConsultaPersonasService.obtieneFolio(tipoFolio);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: obtieneFolio");
		}return folio;
	}
	
//	@DirectMethod
//	public int actualizaFolio(String tipoFolio){
//		int folio = 0;
//		try{
//			if (Utilerias.haveSession(WebContextManager.get())
//					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
//			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
//			folio = objConsultaPersonasService.actualizaFolio(tipoFolio);
//			}
//		}
//		catch(Exception e){
//			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: actualizaFolio");
//		}return folio;
//	}
	
	@DirectMethod
	public String cambiaTipoPersona (int noPersona){
		String mensaje = "";
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			mensaje = objConsultaPersonasService.cambiaTipoPersona(noPersona);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: cambiaTipoPersona");
		}return mensaje;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> comboBenef (int noEmpresa, String noPersona, String descPersona){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.comboBenef(noEmpresa, noPersona, descPersona);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: comboBenef");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> verificaRegistro(int noPersona, String tipoPersona){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.verificaRegistro(noPersona, tipoPersona);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: verificaRegistro");
		}return listaResultado;
	}
	
	@DirectMethod
	public String inhabilitaPersona(int noPersona, String tipoPersona){
		String mensaje = "";
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			mensaje = objConsultaPersonasService.inhabilitaPersona(noPersona, tipoPersona);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction,  M: inhabilitaPersona");
		}return mensaje;
	}
	
	//***********************************************MODIFICACION DE CUENTAS PROVEEDOR *************************************************************
	@DirectMethod
	public List<ConsultaPersonasDto> consultaCuentasProveedor(int noPersona, int noEmpresa, String tipoPersona){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		System.out.println("Inicio de metodo de consulta action");
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			System.out.println("Entra al action de consulta");
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.consultaCuentasProveedor(noPersona, noEmpresa, tipoPersona);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: consultaCuentasProveedor");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> comboDivisasCuentas(){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		try{			
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.comboDivisasCuentas();	
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: comboDivisasCuentas");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> comboBancoCuentas(String nacionalidad){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			System.out.println(nacionalidad + " nacionalidad action");
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.comboBancoCuentas(nacionalidad);
			System.out.println(listaResultado.size() + " lista bancos");
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: comboBancoCuentas");
		}return listaResultado;
	}
	
	@DirectMethod
	public String buscaDescripcionBanco(int noBanco){
		String nombre = "";
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			nombre = objConsultaPersonasService.buscaDescripcionBanco(noBanco);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: buscaDescripcionBanco");
		}return nombre;		
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> validaMovimientos(int noPersona, String chequera){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			System.out.println("entro al action");
			System.out.println(noPersona + " noPersona");
			System.out.println(chequera + " chequera");
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.validaMovimientos(noPersona, chequera);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: validaMovimientos");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> validaTransferencias(String registro){
		List<ConsultaPersonasDto> listaResultado = new ArrayList<ConsultaPersonasDto>();
		Gson gson = new Gson();
		List<Map<String, String>> registroGson = gson.fromJson(registro, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			listaResultado = objConsultaPersonasService.validaTransferencias(registroGson);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Persona, C: ConsultaPersonasAction, M: validaTransferencias");
		}return listaResultado;
	}
	
	@DirectMethod
	public String eliminaCuentaProveedor(String registro){
		String mensaje = "";
		Gson gson = new Gson();
		List<Map<String, String>> registroGson = gson.fromJson(registro, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			mensaje = objConsultaPersonasService.eliminaCuentaProveedor(registroGson);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: eliminaCuentaProveedor");
		}return mensaje;
	}
	
	@DirectMethod
	public String eliminaMedioContacto(String no_persona, String mail){
		String mensaje = "";
		Gson gson = new Gson();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			mensaje = objConsultaPersonasService.eliminaMedioContacto(no_persona,mail);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: eliminaCuentaProveedor");
		}return mensaje;
	}
	
	@DirectMethod
	public String insertaCuentaHistorico(String registro){
		String mensaje = "";
		Gson gson = new Gson();
		List<Map<String, String>> registroGson = gson.fromJson(registro, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			mensaje = objConsultaPersonasService.insertaCuentaHistorico(registroGson);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: insertaCuentaHistorico");
 		}return mensaje;
	}
	
	@DirectMethod
	public String validaDatosCuentas(String registro){
		String mensaje = "";
		Gson gson = new Gson();
		List<Map<String, String>> registroGson = gson.fromJson(registro, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			System.out.println("entro al action de valida");
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			mensaje = objConsultaPersonasService.validaDatosCuentas(registroGson);
			System.out.println(mensaje + " respuesta");
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: vaidaDatosCuentas");
		}return mensaje;
	}
	
	@DirectMethod
	public String insertaActualizaCuentasProveedor(String registro){		
		String mensaje = "";
		Gson gson = new Gson();
		List<Map<String, String>> registroGson = gson.fromJson(registro, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			mensaje = objConsultaPersonasService.insertaActualizaCuentasProveedor(registroGson);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: insertaActualizaCuentasProveedor");
		}return mensaje;
	}	
	
	@DirectMethod
	public List<ConsultaPersonasDto> obtenerPersonas(String condicion,boolean todos) {
		List<ConsultaPersonasDto> list = new ArrayList<ConsultaPersonasDto>();
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			list = objConsultaPersonasService.obtenerPersonas(condicion,todos);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: obtenerPersonas");
		}return list;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> obtenerRelaciones() {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		LlenaComboGralDto obj = new LlenaComboGralDto();
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			
			obj.setCampoUno("id_tipo_relacion");
			obj.setCampoDos("desc_tipo_relacion");
			obj.setTabla("cat_tipo_relacion");
			obj.setOrden("id_tipo_relacion");
			
			list = objConsultaPersonasService.obtenerRelaciones(obj);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: obtenerRelaciones");
		}return list;
	}
	
	@DirectMethod
	public String altaRelaciones(String cadJson) {
		
		String resp = "";
		Gson gson = new Gson();
		List<Map<String, String>> reg = gson.fromJson(cadJson, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		System.out.println("1");
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			resp = validaDatos(reg);
			if(!resp.equals("")) return resp;
			
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			resp = objConsultaPersonasService.altaRelaciones(reg);
			System.out.println("1");
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: altaRelaciones");
		}return resp;
	}
	
	public String validaDatos(List<Map<String, String>> datos) {
		String msg = "";
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			if(datos.get(0).get("noPersona").equals(""))
				msg = "Seleccione una persona a relacionar";
			else if(datos.get(0).get("tipoRelacion").equals(""))
				msg = "Seleccione un tipo de relaci\u00f2n";
//			else if(datos.get(0).get("fecha").equals(""))
//				msg = "Seleccione una fecha de relaci\u00f2n";
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasAction, M: validaDatos");
		}
		
		return msg;
	}
	
	@DirectMethod
	public List<ConsultaPersonasDto> consultarRelaciones(int noPersona, int noEmpresa) {
		List<ConsultaPersonasDto> list = new ArrayList<ConsultaPersonasDto>();
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			list = objConsultaPersonasService.consultarRelaciones(noPersona, noEmpresa);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: consultarRelaciones");
		}return list;
	}
	
	@DirectMethod
	public String eliminarRelaciones(String datosJson) {
		String resp = "";
		Gson gson = new Gson();
		List<Map<String, String>> reg = gson.fromJson(datosJson, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			resp = objConsultaPersonasService.eliminarRelaciones(reg);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: eliminarRelaciones");
		}return resp;
	}
	
	@DirectMethod
	public String modificarReferencia(String tipoReferencia, int noPersona, String tipoPersona) {
		String resp = "";
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),132)) {
			objConsultaPersonasService = (ConsultaPersonasService)contexto.obtenerBean("objConsultaPersonasBusinessImpl");
			resp = objConsultaPersonasService.modificarReferencia(tipoReferencia, noPersona, tipoPersona);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: modificarReferencia");
		}return resp;
	}
	//**********************************************************************************************************************************************
}