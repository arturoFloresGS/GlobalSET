package com.webset.set.ingresos.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.conciliacionbancoset.dto.ConciliaBancoDto;
import com.webset.set.conciliacionbancoset.dto.CriteriosBusquedaDto;
import com.webset.set.ingresos.dto.CuentaContableDto;
import com.webset.set.ingresos.service.IdentificacionIngresosService;
import com.webset.set.seguridad.dto.PersonaDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.utils.tools.Utilerias;

public class IdentificacionIngresosAction {

	Bitacora bitacora = new Bitacora();
	Contexto contexto=new  Contexto();
	Funciones funciones = new Funciones();
	IdentificacionIngresosService identificacionIngresosService;
	
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboBancos(int noEmpresa){

		List<LlenaComboGralDto> listDatos = new ArrayList<LlenaComboGralDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 222)){
			return listDatos;
		}
		
		try {
			identificacionIngresosService = (IdentificacionIngresosService)contexto.obtenerBean("identificacionIngresosBusinessImpl"); 
			listDatos = identificacionIngresosService.llenarComboBancos(noEmpresa);
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Ingresos, C:IdentificaccionIngresosAction, M:llenarComboBancos");
		}
		return listDatos;
	}
	
	@DirectMethod
	public List<CuentaContableDto> getCuentaContable(int noEmpresa, String idGrupo, String idRubro ){
		
		List<CuentaContableDto> listDatos = new ArrayList<CuentaContableDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 222)){
			return listDatos;
		}
		
		try{
			identificacionIngresosService = (IdentificacionIngresosService)contexto.obtenerBean("identificacionIngresosBusinessImpl"); 
			listDatos = identificacionIngresosService.getCuentaContable(noEmpresa, idGrupo,idRubro);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Ingresos, C:IdentificaccionIngresosAction, M:getCuentaContable");
		}
		return listDatos;
	}
	
	@DirectMethod
	public List<LlenaComboChequeraDto> llenarCmbChequeras(int iBanco, int iEmpresa, int iOpc){
		
		List<LlenaComboChequeraDto> list = new ArrayList<LlenaComboChequeraDto> ();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 222)){
			return list;
		}
		
		try{
			identificacionIngresosService = (IdentificacionIngresosService)contexto.obtenerBean("identificacionIngresosBusinessImpl"); 
			list = identificacionIngresosService.llenarCmbChequeras(iBanco, iEmpresa ,iOpc);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Ingresos, C:IdentificaccionIngresosAction, M:llenarCmbChequeras");
		}
		return list;
	}
	
	@DirectMethod
	public List<ConciliaBancoDto> llenarMovsEmpresa(int iIdBanco, String sIdChequera, String sFecIni, String sFecFin, 
													String uMontoIni, String uMontoFin, int iNoEmpresa,
													String idDivisa){
		List<ConciliaBancoDto> movsBanco = new ArrayList<ConciliaBancoDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 222)){
			return movsBanco;
		}
		
		CriteriosBusquedaDto dto = new CriteriosBusquedaDto();
		
		try{
			identificacionIngresosService = (IdentificacionIngresosService)contexto.obtenerBean("identificacionIngresosBusinessImpl");
			dto.setIdBanco(iIdBanco);
			dto.setIdChequera(funciones.validarCadena(sIdChequera));
			if(!sFecIni.equals(""))
				dto.setFechaIni(funciones.ponerFechaDate(sFecIni));
			if(!sFecFin.equals(""))
				dto.setFechaFin(funciones.ponerFechaDate(sFecFin));
			dto.setMontoIni(funciones.convertirCadenaDouble(uMontoIni.trim()));
			dto.setMontoFin(funciones.convertirCadenaDouble(uMontoFin.trim()));
			dto.setNoEmpresa(iNoEmpresa);
			dto.setIdDivisa(idDivisa);
			movsBanco = identificacionIngresosService.llenarMovsEmpresa(dto);
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Ingresos, C:IdentificaccionIngresosAction, M:llenarMovsEmpresa");
		}
		return movsBanco;
	}

	@DirectMethod
	public List<MovimientoDto> llenarMovsBancos(int iIdBanco, String sIdChequera, String sFecIni, String sFecFin,
												 String uMontoIni, String uMontoFin, int iNoEmpresa,
												 String idDivisa){
		
		List<MovimientoDto> movsEmpresa = new ArrayList<MovimientoDto>();
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 222)){
			return movsEmpresa;
		}
		
		CriteriosBusquedaDto dto = new CriteriosBusquedaDto();
		
		try{
			identificacionIngresosService = (IdentificacionIngresosService)contexto.obtenerBean("identificacionIngresosBusinessImpl");
			dto.setIdBanco(iIdBanco);
			dto.setIdChequera(funciones.validarCadena(sIdChequera));
			if(!sFecIni.equals(""))
				dto.setFechaIni(funciones.ponerFechaDate(sFecIni));
			if(!sFecFin.equals(""))
				dto.setFechaFin(funciones.ponerFechaDate(sFecFin));
			
			dto.setMontoIni(funciones.convertirCadenaDouble(uMontoIni.trim()));
			dto.setMontoFin(funciones.convertirCadenaDouble(uMontoFin.trim()));
			dto.setNoEmpresa(iNoEmpresa);
			dto.setIdDivisa(idDivisa);
			
			movsEmpresa = identificacionIngresosService.llenarMovsBancos(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Ingresos, C:IdentificaccionIngresosAction, M:llenarMovsBancos");
		}
		return movsEmpresa;
	}
	
	@DirectMethod
	public List<MovimientoDto> llenarMovsBancos2(int iIdBanco, String sIdChequera, String sFecIni, String sFecFin,
												 String uMontoIni, String uMontoFin, int iNoEmpresa,
												 String idDivisa){
		System.out.println("Tipos de cambio Action----------->");
		List<MovimientoDto> movsEmpresa = new ArrayList<MovimientoDto>();
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 222)){
			return movsEmpresa;
		}
		
		CriteriosBusquedaDto dto = new CriteriosBusquedaDto();
		
		try{
			identificacionIngresosService = (IdentificacionIngresosService)contexto.obtenerBean("identificacionIngresosBusinessImpl");
			dto.setIdBanco(iIdBanco);
			dto.setIdChequera(funciones.validarCadena(sIdChequera));
			if(!sFecIni.equals(""))
				dto.setFechaIni(funciones.ponerFechaDate(sFecIni));
			if(!sFecFin.equals(""))
				dto.setFechaFin(funciones.ponerFechaDate(sFecFin));
			
			dto.setMontoIni(funciones.convertirCadenaDouble(uMontoIni.trim()));
			dto.setMontoFin(funciones.convertirCadenaDouble(uMontoFin.trim()));
			dto.setNoEmpresa(iNoEmpresa);
			dto.setIdDivisa(idDivisa);
			
			movsEmpresa = identificacionIngresosService.llenarMovsBancos2(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Ingresos, C:IdentificaccionIngresosAction, M:llenarMovsBancos2");
		}
		return movsEmpresa;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboDivisa() {
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 222)){
			return list;
		}
		
		try {
			identificacionIngresosService = (IdentificacionIngresosService)contexto.obtenerBean("identificacionIngresosBusinessImpl");
			list = identificacionIngresosService.llenarComboDivisa();
			
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Ingresos, C:IdentificacionIngresosAction, M:llenarComboDivisa");	
		}
		return list;
	}
	
	@DirectMethod
	public List<MovimientoDto>llenaComboCuentaContable() {
		List<MovimientoDto> list= new ArrayList<MovimientoDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 222)){
			return list;
		}
		
		try {
			identificacionIngresosService = (IdentificacionIngresosService)contexto.obtenerBean("identificacionIngresosBusinessImpl");
			list = identificacionIngresosService.llenaComboCuentaContable();
			
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Ingresos, C:IdentificacionIngresosAction, M:llenaComboCuentaContable");	
		}
		return list;
	}
	
	@DirectMethod
	public List<ConciliaBancoDto>llenaComboCuentaContable2() {
		List<ConciliaBancoDto> list= new ArrayList<ConciliaBancoDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 222)){
			return list;
		}
		
		try {
			identificacionIngresosService = (IdentificacionIngresosService)contexto.obtenerBean("identificacionIngresosBusinessImpl");
			list = identificacionIngresosService.llenaComboCuentaContable2();
			
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Ingresos, C:IdentificacionIngresosAction, M:llenaComboCuentaContable2");	
		}
		return list;
	}
	
	@DirectMethod
	public List<ConciliaBancoDto>llenaComboOrigen() {
		List<ConciliaBancoDto> list= new ArrayList<ConciliaBancoDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 222)){
			return list;
		}
		
		try {
			identificacionIngresosService = (IdentificacionIngresosService)contexto.obtenerBean("identificacionIngresosBusinessImpl");
			list = identificacionIngresosService.llenaComboOrigen();
			
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Ingresos, C:IdentificacionIngresosAction, M:llenaComboOrigen");	
		}
		return list;
	}
	
	@DirectMethod
	public Map<String, String> conciliaBancosEmpresa(String jsonBancos, String jsonCobranza) {
		
		Map<String, String> result = new HashMap<>();
		result.put("error", "");
		result.put("mensaje", "");
		
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 222)){
			
			result.put("error", "No tiene permiso para operar la pantalla");
			
			return result;
		}
		
		try {
			
			Gson gson = new Gson();
			List<Map<String, String>> objCobranza = gson.fromJson(jsonBancos,new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			List<ConciliaBancoDto> listCobranza = new ArrayList<>();
			
			List<Map<String, String>> objBancos = gson.fromJson(jsonCobranza,new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
            List<MovimientoDto> listBanco = new ArrayList<>();


			
			for(int t=0; t<objCobranza.size(); t++) {
				ConciliaBancoDto con = new ConciliaBancoDto();
				con.setImporte(objCobranza.get(t).get("importe") != null? Double.parseDouble(objCobranza.get(t).get("importe")):0);
				con.setIdDivisa(objCobranza.get(t).get("idDivisa") != null? objCobranza.get(t).get("idDivisa"):"");
				con.setReferencia(objCobranza.get(t).get("referencia") != null? objCobranza.get(t).get("referencia"):"");
				con.setNoEmpresa(objCobranza.get(t).get("noEmpresa") != null ? Integer.parseInt(objCobranza.get(t).get("noEmpresa")):0);
				con.setNomEmpresa(objCobranza.get(t).get("nomEmpresa") != null? objCobranza.get(t).get("nomEmpresa"):"");
				con.setNoDocto2(objCobranza.get(t).get("noDocto2") != null? objCobranza.get(t).get("noDocto2"):"");
				con.setNoFactura2(objCobranza.get(t).get("noFactura2") != null? objCobranza.get(t).get("noFactura2"):"");
				con.setFecFactura(funciones.ponerFechaDate(objCobranza.get(t).get("fecFactura")));
				con.setFecValor(funciones.ponerFechaDate(objCobranza.get(t).get("fecValor")));
				con.setNoClienteS(objCobranza.get(t).get("noCliente") != null? objCobranza.get(t).get("noCliente"):"");
				con.setConcepto(objCobranza.get(t).get("concepto") != null? objCobranza.get(t).get("concepto"):"");
				con.setFolioCob(objCobranza.get(t).get("folioCob") != null ? Integer.parseInt(objCobranza.get(t).get("folioCob")):0);
				con.setSecuencia(objCobranza.get(t).get("secuencia") != null ? Integer.parseInt(objCobranza.get(t).get("secuencia")):0);
				con.setRazonSoc(objCobranza.get(t).get("razonSoc") != null? objCobranza.get(t).get("razonSoc"):"");
				con.setEstatus(objCobranza.get(t).get("estatus") != null? objCobranza.get(t).get("estatus"):"");
				con.setFolioDetMov(objCobranza.get(t).get("folioDetMov") != null? objCobranza.get(t).get("folioDetMov"):"");
				con.setImporteSol(objCobranza.get(t).get("importeSol") != null? Double.parseDouble(objCobranza.get(t).get("importeSol")):0);
				
				listCobranza.add(con);
			}
			
			for(int t=0; t<objBancos.size(); t++) {
				
				MovimientoDto mov = new MovimientoDto();
				
				mov.setNomEmpresa(objBancos.get(t).get("nomEmpresa") != null? objBancos.get(t).get("nomEmpresa"):"");
				mov.setNoEmpresa(objBancos.get(t).get("noEmpresa") != null ? Integer.parseInt(objBancos.get(t).get("noEmpresa")):0);
				mov.setIdBanco(objBancos.get(t).get("idBanco") != null ? Integer.parseInt(objBancos.get(t).get("idBanco")):0);
				mov.setDescBanco(objBancos.get(t).get("descBanco") != null? objBancos.get(t).get("descBanco"):"");
				mov.setNoCuenta(objBancos.get(t).get("noCuenta") != null ? Integer.parseInt(objBancos.get(t).get("noCuenta")):0);
				mov.setImporte(objBancos.get(t).get("importe") != null? Double.parseDouble(objBancos.get(t).get("importe")):0);
				mov.setFecOperacion(funciones.ponerFechaDate(objBancos.get(t).get("fecOperacion")));
				mov.setFecValorStr(objBancos.get(t).get("fecValor") != null? funciones.ponerFechaSola(objBancos.get(t).get("fecValor")):"");
				mov.setIdDivisa(objBancos.get(t).get("idDivisa") != null? objBancos.get(t).get("idDivisa"):"");
				mov.setReferencia(objBancos.get(t).get("referencia") != null? objBancos.get(t).get("referencia"):"");
				mov.setConcepto(objBancos.get(t).get("concepto") != null? objBancos.get(t).get("concepto"):"");
				mov.setObservacion(objBancos.get(t).get("observacion") != null? objBancos.get(t).get("observacion"):"");
				mov.setIdTipoOperacion(objBancos.get(t).get("idTipoOperacion") != null ? Integer.parseInt(objBancos.get(t).get("idTipoOperacion")):0);
				mov.setNoFolioDet(objBancos.get(t).get("noFolioDet") != null ? Integer.parseInt(objBancos.get(t).get("noFolioDet")):0);
				mov.setNoCliente(objBancos.get(t).get("noCliente") != null? objBancos.get(t).get("noCliente"):"");
				mov.setNoDocto(objBancos.get(t).get("noDocto") != null ? objBancos.get(t).get("noDocto"):"");
				mov.setIdChequera(objBancos.get(t).get("idChequera") != null? objBancos.get(t).get("idChequera"):"");
				mov.setIdEstatusMov(objBancos.get(t).get("idEstatusMov") != null? objBancos.get(t).get("idEstatusMov"):"");
				mov.setDivision(objBancos.get(t).get("division") != null? objBancos.get(t).get("division"):"");
				mov.setDescripcion(objBancos.get(t).get("descripcion") != null? objBancos.get(t).get("descripcion"):"");
				mov.setNoFolioMov(objBancos.get(t).get("noFolioMov") != null ? Integer.parseInt(objBancos.get(t).get("noFolioMov")):0);
				mov.setIdFormaPago(objBancos.get(t).get("idFormaPago") != null ? Integer.parseInt(objBancos.get(t).get("idFormaPago")):0); 
				mov.setIdCaja(objBancos.get(t).get("idCaja") != null ? Integer.parseInt(objBancos.get(t).get("idCaja")):0);
				mov.setIdBancoBenef(objBancos.get(t).get("idBancoBenef") != null ? Integer.parseInt(objBancos.get(t).get("idBancoBenef")):0);
				mov.setIdRubro(objBancos.get(t).get("idRubro") != null ? Integer.parseInt(objBancos.get(t).get("idRubro")):0); 
				
				
				listBanco.add(mov);
			}
			
			identificacionIngresosService = (IdentificacionIngresosService)contexto.obtenerBean("identificacionIngresosBusinessImpl");
			result = identificacionIngresosService.conciliaBancosEmpresa(listCobranza, listBanco);
			
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Ingresos, C:IdentificacionIngresosAction, M:conciliaBancosEmpresa");	
		}
		return result;
	}


	@DirectMethod
	public Map<String, String> conciliaVirtualBanco(String jsonBancos, String jsonCobranza, String causa, String origen, String cuenta) {
		
		Map<String, String> result = new HashMap<>();
		result.put("error", "");
		result.put("mensaje", "");
		
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 222)){
			
			result.put("error", "No tiene permiso para operar la pantalla");
			
			return result;
		}
		
		try {
			
			Gson gson = new Gson();
			List<Map<String, String>> objCobranza = gson.fromJson(jsonBancos,new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			List<ConciliaBancoDto> listCobranza = new ArrayList<>();
			
			List<Map<String, String>> objBancos = gson.fromJson(jsonCobranza,new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
            List<MovimientoDto> listBanco = new ArrayList<>();


			
			for(int t=0; t<objCobranza.size(); t++) {
				ConciliaBancoDto con = new ConciliaBancoDto();
				con.setImporte(objCobranza.get(t).get("importe") != null? Double.parseDouble(objCobranza.get(t).get("importe")):0);
				con.setIdDivisa(objCobranza.get(t).get("idDivisa") != null? objCobranza.get(t).get("idDivisa"):"");
				con.setReferencia(objCobranza.get(t).get("referencia") != null? objCobranza.get(t).get("referencia"):"");
				con.setNoEmpresa(objCobranza.get(t).get("noEmpresa") != null ? Integer.parseInt(objCobranza.get(t).get("noEmpresa")):0);
				con.setNomEmpresa(objCobranza.get(t).get("nomEmpresa") != null? objCobranza.get(t).get("nomEmpresa"):"");
				con.setNoDocto2(objCobranza.get(t).get("noDocto2") != null? objCobranza.get(t).get("noDocto2"):"");
				con.setNoFactura2(objCobranza.get(t).get("noFactura2") != null? objCobranza.get(t).get("noFactura2"):"");
				con.setFecFactura(funciones.ponerFechaDate(objCobranza.get(t).get("fecFactura")));
				con.setFecValor(funciones.ponerFechaDate(objCobranza.get(t).get("fecValor")));
				con.setNoClienteS(objCobranza.get(t).get("noCliente") != null? objCobranza.get(t).get("noCliente"):"");
				con.setConcepto(objCobranza.get(t).get("concepto") != null? objCobranza.get(t).get("concepto"):"");
				con.setFolioCob(objCobranza.get(t).get("folioCob") != null ? Integer.parseInt(objCobranza.get(t).get("folioCob")):0);
				con.setSecuencia(objCobranza.get(t).get("secuencia") != null ? Integer.parseInt(objCobranza.get(t).get("secuencia")):0);
				con.setRazonSoc(objCobranza.get(t).get("razonSoc") != null? objCobranza.get(t).get("razonSoc"):"");
				con.setEstatus(objCobranza.get(t).get("estatus") != null? objCobranza.get(t).get("estatus"):"");
				con.setFolioDetMov(objCobranza.get(t).get("folioDetMov") != null? objCobranza.get(t).get("folioDetMov"):"");
				con.setImporteSol(objCobranza.get(t).get("importeSol") != null? Double.parseDouble(objCobranza.get(t).get("importeSol")):0);
				
				
				listCobranza.add(con);
			}
			
				for(int t=0; t<objBancos.size(); t++) {
				
				MovimientoDto mov = new MovimientoDto();
				mov.setNomEmpresa(objBancos.get(t).get("nomEmpresa") != null? objBancos.get(t).get("nomEmpresa"):"");
				mov.setNoEmpresa(objBancos.get(t).get("noEmpresa") != null ? Integer.parseInt(objBancos.get(t).get("noEmpresa")):0);
				mov.setIdBanco(objBancos.get(t).get("idBanco") != null ? Integer.parseInt(objBancos.get(t).get("idBanco")):0);
				mov.setDescBanco(objBancos.get(t).get("descBanco") != null? objBancos.get(t).get("descBanco"):"");
				mov.setNoCuenta(objBancos.get(t).get("noCuenta") != null ? Integer.parseInt(objBancos.get(t).get("noCuenta")):0);
				mov.setImporte(objBancos.get(t).get("importe") != null? Double.parseDouble(objBancos.get(t).get("importe")):0);
				mov.setFecOperacion(funciones.ponerFechaDate(objBancos.get(t).get("fecOperacion")));
				mov.setFecValorStr(objBancos.get(t).get("fecValor") != null? funciones.ponerFechaSola(objBancos.get(t).get("fecValor")):"");
				mov.setIdDivisa(objBancos.get(t).get("idDivisa") != null? objBancos.get(t).get("idDivisa"):"");
				mov.setReferencia(objBancos.get(t).get("referencia") != null? objBancos.get(t).get("referencia"):"");
				mov.setConcepto(objBancos.get(t).get("concepto") != null? objBancos.get(t).get("concepto"):"");
				mov.setObservacion(objBancos.get(t).get("observacion") != null? objBancos.get(t).get("observacion"):"");
				mov.setIdTipoOperacion(objBancos.get(t).get("idTipoOperacion") != null ? Integer.parseInt(objBancos.get(t).get("idTipoOperacion")):0);
				mov.setNoFolioDet(objBancos.get(t).get("noFolioDet") != null ? Integer.parseInt(objBancos.get(t).get("noFolioDet")):0);
				mov.setNoCliente(objBancos.get(t).get("noCliente") != null? objBancos.get(t).get("noCliente"):"");
				mov.setNoDocto(objBancos.get(t).get("noDocto") != null ? objBancos.get(t).get("noDocto"):"");
				mov.setIdChequera(objBancos.get(t).get("idChequera") != null? objBancos.get(t).get("idChequera"):"");
				mov.setIdEstatusMov(objBancos.get(t).get("idEstatusMov") != null? objBancos.get(t).get("idEstatusMov"):"");
				mov.setDivision(objBancos.get(t).get("division") != null? objBancos.get(t).get("division"):"");
				mov.setDescripcion(objBancos.get(t).get("descripcion") != null? objBancos.get(t).get("descripcion"):"");
				mov.setNoFolioMov(objBancos.get(t).get("noFolioMov") != null ? Integer.parseInt(objBancos.get(t).get("noFolioMov")):0);
				mov.setIdFormaPago(objBancos.get(t).get("idFormaPago") != null ? Integer.parseInt(objBancos.get(t).get("idFormaPago")):0); 
				mov.setIdCaja(objBancos.get(t).get("idCaja") != null ? Integer.parseInt(objBancos.get(t).get("idCaja")):0);
				mov.setIdBancoBenef(objBancos.get(t).get("idBancoBenef") != null ? Integer.parseInt(objBancos.get(t).get("idBancoBenef")):0);
				mov.setIdRubro(objBancos.get(t).get("idRubro") != null ? Integer.parseInt(objBancos.get(t).get("idRubro")):0); 
				
				listBanco.add(mov);
			}
				String causa2 =(causa);
				String origen2=(origen);
				String cuenta2=(cuenta);
			identificacionIngresosService = (IdentificacionIngresosService)contexto.obtenerBean("identificacionIngresosBusinessImpl");
			result = identificacionIngresosService.conciliaVirtualBanco(listCobranza, listBanco, causa2, origen2,cuenta2);
			
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Ingresos, C:IdentificacionIngresosAction, M:conciliaVirtualBanco");	
			result.put("error", "Error al convertir datos Action.");
		}
		return result;
	}

	
	@DirectMethod
	public Map<String, String> conciliaVirtualCobranza(String jsonBancos, String jsonCobranza, String causa, String origen, String cuenta) {
		
		Map<String, String> result = new HashMap<>();
		result.put("error", "");
		result.put("mensaje", "");
		
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 222)){
			
			result.put("error", "No tiene permiso para operar la pantalla");
			
			return result;
		}
		
		try {
			
			Gson gson = new Gson();
			List<Map<String, String>> objCobranza = gson.fromJson(jsonBancos,new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			List<ConciliaBancoDto> listCobranza = new ArrayList<>();
			
			List<Map<String, String>> objBancos = gson.fromJson(jsonCobranza,new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
            List<MovimientoDto> listBanco = new ArrayList<>();


			
			for(int t=0; t<objCobranza.size(); t++) {
				ConciliaBancoDto con = new ConciliaBancoDto();
				
				con.setImporte(objCobranza.get(t).get("importe") != null? Double.parseDouble(objCobranza.get(t).get("importe")):0);
				con.setIdDivisa(objCobranza.get(t).get("idDivisa") != null? objCobranza.get(t).get("idDivisa"):"");
				con.setReferencia(objCobranza.get(t).get("referencia") != null? objCobranza.get(t).get("referencia"):"");
				con.setNoEmpresa(objCobranza.get(t).get("noEmpresa") != null ? Integer.parseInt(objCobranza.get(t).get("noEmpresa")):0);
				con.setNomEmpresa(objCobranza.get(t).get("nomEmpresa") != null? objCobranza.get(t).get("nomEmpresa"):"");
				con.setNoDocto2(objCobranza.get(t).get("noDocto2") != null? objCobranza.get(t).get("noDocto2"):"");
				con.setNoFactura2(objCobranza.get(t).get("noFactura2") != null? objCobranza.get(t).get("noFactura2"):"");
				con.setFecFactura(funciones.ponerFechaDate(objCobranza.get(t).get("fecFactura")));
				con.setFecValor(funciones.ponerFechaDate(objCobranza.get(t).get("fecValor")));
				con.setNoClienteS(objCobranza.get(t).get("noCliente") != null? objCobranza.get(t).get("noCliente"):"");
				con.setConcepto(objCobranza.get(t).get("concepto") != null? objCobranza.get(t).get("concepto"):"");
				con.setFolioCob(objCobranza.get(t).get("folioCob") != null ? Integer.parseInt(objCobranza.get(t).get("folioCob")):0);
				con.setSecuencia(objCobranza.get(t).get("secuencia") != null ? Integer.parseInt(objCobranza.get(t).get("secuencia")):0);
				con.setRazonSoc(objCobranza.get(t).get("razonSoc") != null? objCobranza.get(t).get("razonSoc"):"");
				con.setEstatus(objCobranza.get(t).get("estatus") != null? objCobranza.get(t).get("estatus"):"");
				con.setFolioDetMov(objCobranza.get(t).get("folioDetMov") != null? objCobranza.get(t).get("folioDetMov"):"");
				con.setImporteSol(objCobranza.get(t).get("importeSol") != null? Double.parseDouble(objCobranza.get(t).get("importeSol")):0);
				
				
				listCobranza.add(con);
			}
			
				for(int t=0; t<objBancos.size(); t++) {
				
				MovimientoDto mov = new MovimientoDto();
				
				mov.setNomEmpresa(objBancos.get(t).get("nomEmpresa") != null? objBancos.get(t).get("nomEmpresa"):"");
				mov.setNoEmpresa(objBancos.get(t).get("noEmpresa") != null ? Integer.parseInt(objBancos.get(t).get("noEmpresa")):0);
				mov.setIdBanco(objBancos.get(t).get("idBanco") != null ? Integer.parseInt(objBancos.get(t).get("idBanco")):0);
				mov.setDescBanco(objBancos.get(t).get("descBanco") != null? objBancos.get(t).get("descBanco"):"");
				mov.setNoCuenta(objBancos.get(t).get("noCuenta") != null ? Integer.parseInt(objBancos.get(t).get("noCuenta")):0);
				mov.setImporte(objBancos.get(t).get("importe") != null? Double.parseDouble(objBancos.get(t).get("importe")):0);
				mov.setFecOperacion(funciones.ponerFechaDate(objBancos.get(t).get("fecOperacion")));
				mov.setFecValorStr(objBancos.get(t).get("fecValor") != null? funciones.ponerFechaSola(objBancos.get(t).get("fecValor")):"");
				mov.setIdDivisa(objBancos.get(t).get("idDivisa") != null? objBancos.get(t).get("idDivisa"):"");
				mov.setReferencia(objBancos.get(t).get("referencia") != null? objBancos.get(t).get("referencia"):"");
				mov.setConcepto(objBancos.get(t).get("concepto") != null? objBancos.get(t).get("concepto"):"");
				mov.setObservacion(objBancos.get(t).get("observacion") != null? objBancos.get(t).get("observacion"):"");
				mov.setIdTipoOperacion(objBancos.get(t).get("idTipoOperacion") != null ? Integer.parseInt(objBancos.get(t).get("idTipoOperacion")):0);
				mov.setNoFolioDet(objBancos.get(t).get("noFolioDet") != null ? Integer.parseInt(objBancos.get(t).get("noFolioDet")):0);
				mov.setNoCliente(objBancos.get(t).get("noCliente") != null? objBancos.get(t).get("noCliente"):"");
				mov.setNoDocto(objBancos.get(t).get("noDocto") != null ? objBancos.get(t).get("noDocto"):"");
				mov.setIdChequera(objBancos.get(t).get("idChequera") != null? objBancos.get(t).get("idChequera"):"");
				mov.setIdEstatusMov(objBancos.get(t).get("idEstatusMov") != null? objBancos.get(t).get("idEstatusMov"):"");
				mov.setDivision(objBancos.get(t).get("division") != null? objBancos.get(t).get("division"):"");
				mov.setDescripcion(objBancos.get(t).get("descripcion") != null? objBancos.get(t).get("descripcion"):"");
				mov.setNoFolioMov(objBancos.get(t).get("noFolioMov") != null ? Integer.parseInt(objBancos.get(t).get("noFolioMov")):0);
				mov.setIdFormaPago(objBancos.get(t).get("idFormaPago") != null ? Integer.parseInt(objBancos.get(t).get("idFormaPago")):0); 
				mov.setIdCaja(objBancos.get(t).get("idCaja") != null ? Integer.parseInt(objBancos.get(t).get("idCaja")):0);
				mov.setIdBancoBenef(objBancos.get(t).get("idBancoBenef") != null ? Integer.parseInt(objBancos.get(t).get("idBancoBenef")):0);
				mov.setIdRubro(objBancos.get(t).get("idRubro") != null ? Integer.parseInt(objBancos.get(t).get("idRubro")):0); 
				
				listBanco.add(mov);
			}
				String causa2 =(causa);
				String origen2=(origen);
				String cuenta2=(cuenta);
			identificacionIngresosService = (IdentificacionIngresosService)contexto.obtenerBean("identificacionIngresosBusinessImpl");
			result = identificacionIngresosService.conciliaVirtualCobranza(listCobranza, listBanco, causa2, origen2,cuenta2);
			
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Ingresos, C:IdentificacionIngresosAction, M:conciliaVirtualCobranza");	
			result.put("error", "Error al convertir datos Action.");
		}
		return result;
	}

	
	@DirectMethod
	public List<ConciliaBancoDto> datosExcel(int empresa, String fechaIni, String fechaFin) {

		List<ConciliaBancoDto> datosExcel = new ArrayList<ConciliaBancoDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 222)){
			
			return datosExcel;
		}
		
		try {
			identificacionIngresosService = (IdentificacionIngresosService)contexto.obtenerBean("identificacionIngresosBusinessImpl");
			datosExcel = identificacionIngresosService.datosExcel(empresa, fechaIni, fechaFin);
			
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Ingresos, C:IdentificacionIngresosAction, M:Obtener Datos Excel");
		}
		return datosExcel;
	}
	
	@DirectMethod
	public Map<String, String> parcializarBancos(String jsonBancos, String diferencia) {
		
		Map<String, String> result = new HashMap<>();
		result.put("error", "");
		result.put("mensaje", "");
		
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 222)){
			
			result.put("error", "No tiene permiso para operar la pantalla");
			
			return result;
		}
		
		try {
			
			Gson gson = new Gson();
			List<Map<String, String>> objCobranza = gson.fromJson(jsonBancos, 
					new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			
			List<ConciliaBancoDto> listCobranza = new ArrayList<>();
			
			for(int t=0; t<objCobranza.size(); t++) {
				ConciliaBancoDto con = new ConciliaBancoDto();
				
				con.setImporte(objCobranza.get(t).get("importe") != null? Double.parseDouble(objCobranza.get(t).get("importe")):0);
				con.setIdDivisa(objCobranza.get(t).get("idDivisa") != null? objCobranza.get(t).get("idDivisa"):"");
				con.setReferencia(objCobranza.get(t).get("referencia") != null? objCobranza.get(t).get("referencia"):"");
				con.setNoEmpresa(objCobranza.get(t).get("noEmpresa") != null ? Integer.parseInt(objCobranza.get(t).get("noEmpresa")):0);
				con.setNomEmpresa(objCobranza.get(t).get("nomEmpresa") != null? objCobranza.get(t).get("nomEmpresa"):"");
				con.setNoDocto2(objCobranza.get(t).get("noDocto2") != null? objCobranza.get(t).get("noDocto2"):"");
				con.setNoFactura2(objCobranza.get(t).get("noFactura2") != null? objCobranza.get(t).get("noFactura2"):"");
				con.setFecFactura(funciones.ponerFechaDate(objCobranza.get(t).get("fecFactura")));
				con.setFecValor(funciones.ponerFechaDate(objCobranza.get(t).get("fecValor")));
				con.setNoClienteS(objCobranza.get(t).get("noCliente") != null? objCobranza.get(t).get("noCliente"):"");
				con.setConcepto(objCobranza.get(t).get("concepto") != null? objCobranza.get(t).get("concepto"):"");
				con.setFolioCob(objCobranza.get(t).get("folioCob") != null ? Integer.parseInt(objCobranza.get(t).get("folioCob")):0);
				con.setSecuencia(objCobranza.get(t).get("secuencia") != null ? Integer.parseInt(objCobranza.get(t).get("secuencia")):0);
				con.setRazonSoc(objCobranza.get(t).get("razonSoc") != null? objCobranza.get(t).get("razonSoc"):"");
				con.setEstatus(objCobranza.get(t).get("estatus") != null? objCobranza.get(t).get("estatus"):"");
				con.setFolioDetMov(objCobranza.get(t).get("folioDetMov") != null? objCobranza.get(t).get("folioDetMov"):"");
				con.setImporteSol(objCobranza.get(t).get("importeSol") != null? Double.parseDouble(objCobranza.get(t).get("importeSol")):0);
				
				
				listCobranza.add(con);
			}
			
			
			
			double difAux = Double.parseDouble(diferencia);
			
			identificacionIngresosService = (IdentificacionIngresosService)contexto.obtenerBean("identificacionIngresosBusinessImpl");
			result = identificacionIngresosService.parcializarBancos(listCobranza, difAux);
			
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Ingresos, C:IdentificacionIngresosAction, M:parcializarBancosEmpresa");
			result.put("error", "Error al convertir datos Action.");
		}
		return result;
	}
	
	@DirectMethod
	public Map<String, String> parcializarBancos2( String jsonCobranza, String diferencia) {

		
		Map<String, String> result2 = new HashMap<>();
		result2.put("error", "");
		result2.put("mensaje", "");
		
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 222)){
			
			result2.put("error", "No tiene permiso para operar la pantalla");
			
			return result2;
		}
		
		try {
			
			Gson gson = new Gson();
			List<Map<String, String>> objBancos = gson.fromJson(jsonCobranza, 
												  		new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			
			
			List<MovimientoDto> listBanco = new ArrayList<>();
			
			
			for(int t=0; t<objBancos.size(); t++) {
				
				MovimientoDto mov = new MovimientoDto();
				
				mov.setNomEmpresa(objBancos.get(t).get("nomEmpresa") != null? objBancos.get(t).get("nomEmpresa"):"");
				mov.setNoEmpresa(objBancos.get(t).get("noEmpresa") != null ? Integer.parseInt(objBancos.get(t).get("noEmpresa")):0);
				mov.setIdBanco(objBancos.get(t).get("idBanco") != null ? Integer.parseInt(objBancos.get(t).get("idBanco")):0);
				mov.setDescBanco(objBancos.get(t).get("descBanco") != null? objBancos.get(t).get("descBanco"):"");
				mov.setNoCuenta(objBancos.get(t).get("noCuenta") != null ? Integer.parseInt(objBancos.get(t).get("noCuenta")):0);
				mov.setImporte(objBancos.get(t).get("importe") != null? Double.parseDouble(objBancos.get(t).get("importe")):0);
				mov.setFecOperacion(funciones.ponerFechaDate(objBancos.get(t).get("fecOperacion")));
				mov.setFecValorStr(objBancos.get(t).get("fecValor") != null? funciones.ponerFechaSola(objBancos.get(t).get("fecValor")):"");
				mov.setIdDivisa(objBancos.get(t).get("idDivisa") != null? objBancos.get(t).get("idDivisa"):"");
				mov.setReferencia(objBancos.get(t).get("referencia") != null? objBancos.get(t).get("referencia"):"");
				mov.setConcepto(objBancos.get(t).get("concepto") != null? objBancos.get(t).get("concepto"):"");
				mov.setObservacion(objBancos.get(t).get("observacion") != null? objBancos.get(t).get("observacion"):"");
				mov.setIdTipoOperacion(objBancos.get(t).get("idTipoOperacion") != null ? Integer.parseInt(objBancos.get(t).get("idTipoOperacion")):0);
				mov.setNoFolioDet(objBancos.get(t).get("noFolioDet") != null ? Integer.parseInt(objBancos.get(t).get("noFolioDet")):0);
				mov.setNoCliente(objBancos.get(t).get("noCliente") != null? objBancos.get(t).get("noCliente"):"");
				mov.setNoDocto(objBancos.get(t).get("noDocto") != null ? objBancos.get(t).get("noDocto"):"");
				mov.setIdChequera(objBancos.get(t).get("idChequera") != null? objBancos.get(t).get("idChequera"):"");
				mov.setIdEstatusMov(objBancos.get(t).get("idEstatusMov") != null? objBancos.get(t).get("idEstatusMov"):"");
				mov.setDivision(objBancos.get(t).get("division") != null? objBancos.get(t).get("division"):"");
				mov.setDescripcion(objBancos.get(t).get("descripcion") != null? objBancos.get(t).get("descripcion"):"");
				mov.setNoFolioMov(objBancos.get(t).get("noFolioMov") != null ? Integer.parseInt(objBancos.get(t).get("noFolioMov")):0);
				mov.setIdFormaPago(objBancos.get(t).get("idFormaPago") != null ? Integer.parseInt(objBancos.get(t).get("idFormaPago")):0); 
				mov.setIdCaja(objBancos.get(t).get("idCaja") != null ? Integer.parseInt(objBancos.get(t).get("idCaja")):0);
				mov.setIdBancoBenef(objBancos.get(t).get("idBancoBenef") != null ? Integer.parseInt(objBancos.get(t).get("idBancoBenef")):0);
				mov.setIdRubro(objBancos.get(t).get("idRubro") != null ? Integer.parseInt(objBancos.get(t).get("idRubro")):0); 
				
				listBanco.add(mov);
			}
			
			
			
			double difAux = Double.parseDouble(diferencia);
			
			identificacionIngresosService = (IdentificacionIngresosService)contexto.obtenerBean("identificacionIngresosBusinessImpl");
			result2 = identificacionIngresosService.parcializarBancos2(listBanco, difAux);
			
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			
				+ "P:Ingresos, C:IdentificacionIngresosAction, M:parcializarBancosEmpresa2");
			result2.put("error", "Error al convertir datos.");
		}
		return result2;
	
	}
	

	@DirectMethod
	public Map<String, String> concilVirSBanco( String jsonCobranza, String causa, String cuenta) {

		
		Map<String, String> result2 = new HashMap<>();
		result2.put("error", "");
		result2.put("mensaje", "");
		
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 222)){
			
			result2.put("error", "No tiene permiso para operar la pantalla");
			
			return result2;
		}
		
		try {
			
			Gson gson = new Gson();
			List<Map<String, String>> objBancos = gson.fromJson(jsonCobranza, 
												  		new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			
			
			List<MovimientoDto> listBanco = new ArrayList<>();
			
			
			for(int t=0; t<objBancos.size(); t++) {
				
				MovimientoDto mov = new MovimientoDto();
				
				mov.setNomEmpresa(objBancos.get(t).get("nomEmpresa") != null? objBancos.get(t).get("nomEmpresa"):"");
				mov.setNoEmpresa(objBancos.get(t).get("noEmpresa") != null ? Integer.parseInt(objBancos.get(t).get("noEmpresa")):0);
				mov.setIdBanco(objBancos.get(t).get("idBanco") != null ? Integer.parseInt(objBancos.get(t).get("idBanco")):0);
				mov.setDescBanco(objBancos.get(t).get("descBanco") != null? objBancos.get(t).get("descBanco"):"");
				mov.setNoCuenta(objBancos.get(t).get("noCuenta") != null ? Integer.parseInt(objBancos.get(t).get("noCuenta")):0);
				mov.setImporte(objBancos.get(t).get("importe") != null? Double.parseDouble(objBancos.get(t).get("importe")):0);
				mov.setFecOperacion(funciones.ponerFechaDate(objBancos.get(t).get("fecOperacion")));
				mov.setFecValorStr(objBancos.get(t).get("fecValor") != null? funciones.ponerFechaSola(objBancos.get(t).get("fecValor")):"");
				mov.setIdDivisa(objBancos.get(t).get("idDivisa") != null? objBancos.get(t).get("idDivisa"):"");
				mov.setReferencia(objBancos.get(t).get("referencia") != null? objBancos.get(t).get("referencia"):"");
				mov.setConcepto(objBancos.get(t).get("concepto") != null? objBancos.get(t).get("concepto"):"");
				mov.setObservacion(objBancos.get(t).get("observacion") != null? objBancos.get(t).get("observacion"):"");
				mov.setIdTipoOperacion(objBancos.get(t).get("idTipoOperacion") != null ? Integer.parseInt(objBancos.get(t).get("idTipoOperacion")):0);
				mov.setNoFolioDet(objBancos.get(t).get("noFolioDet") != null ? Integer.parseInt(objBancos.get(t).get("noFolioDet")):0);
				mov.setNoCliente(objBancos.get(t).get("noCliente") != null? objBancos.get(t).get("noCliente"):"");
				mov.setNoDocto(objBancos.get(t).get("noDocto") != null ? objBancos.get(t).get("noDocto"):"");
				mov.setIdChequera(objBancos.get(t).get("idChequera") != null? objBancos.get(t).get("idChequera"):"");
				mov.setIdEstatusMov(objBancos.get(t).get("idEstatusMov") != null? objBancos.get(t).get("idEstatusMov"):"");
				mov.setDivision(objBancos.get(t).get("division") != null? objBancos.get(t).get("division"):"");
				mov.setDescripcion(objBancos.get(t).get("descripcion") != null? objBancos.get(t).get("descripcion"):"");
				mov.setNoFolioMov(objBancos.get(t).get("noFolioMov") != null ? Integer.parseInt(objBancos.get(t).get("noFolioMov")):0);
				mov.setIdFormaPago(objBancos.get(t).get("idFormaPago") != null ? Integer.parseInt(objBancos.get(t).get("idFormaPago")):0); 
				mov.setIdCaja(objBancos.get(t).get("idCaja") != null ? Integer.parseInt(objBancos.get(t).get("idCaja")):0);
				mov.setIdBancoBenef(objBancos.get(t).get("idBancoBenef") != null ? Integer.parseInt(objBancos.get(t).get("idBancoBenef")):0);
				mov.setIdRubro(objBancos.get(t).get("idRubro") != null ? Integer.parseInt(objBancos.get(t).get("idRubro")):0); 
				
				listBanco.add(mov);
			}
			
			
			
			;
			String causa2 =(causa);
			String cuenta2 =(cuenta);
			identificacionIngresosService = (IdentificacionIngresosService)contexto.obtenerBean("identificacionIngresosBusinessImpl");
			result2 = identificacionIngresosService.concilVirSBanco(listBanco, causa2, cuenta2);
			
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			
				+ "P:Ingresos, C:IdentificacionIngresosAction, M:concilVirSBanco");
			result2.put("error", "Error al convertir datos.");
		}
		return result2;
	
	}
		
	

	@DirectMethod
	public Map<String, String> concilVirSCobranza(String jsonBancos,String causa, String cuenta) {
		
		Map<String, String> result = new HashMap<>();
		result.put("error", "");
		result.put("mensaje", "");
		
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 222)){
			
			result.put("error", "No tiene permiso para operar la pantalla");
			
			return result;
		}
		
		try {
			
			Gson gson = new Gson();
			List<Map<String, String>> objCobranza = gson.fromJson(jsonBancos, 
					new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			
			List<ConciliaBancoDto> listCobranza = new ArrayList<>();
			
			for(int t=0; t<objCobranza.size(); t++) {
				ConciliaBancoDto con = new ConciliaBancoDto();
				
				con.setImporte(objCobranza.get(t).get("importe") != null? Double.parseDouble(objCobranza.get(t).get("importe")):0);
				con.setIdDivisa(objCobranza.get(t).get("idDivisa") != null? objCobranza.get(t).get("idDivisa"):"");
				con.setReferencia(objCobranza.get(t).get("referencia") != null? objCobranza.get(t).get("referencia"):"");
				con.setNoEmpresa(objCobranza.get(t).get("noEmpresa") != null ? Integer.parseInt(objCobranza.get(t).get("noEmpresa")):0);
				con.setNomEmpresa(objCobranza.get(t).get("nomEmpresa") != null? objCobranza.get(t).get("nomEmpresa"):"");
				con.setNoDocto2(objCobranza.get(t).get("noDocto2") != null? objCobranza.get(t).get("noDocto2"):"");
				con.setNoFactura2(objCobranza.get(t).get("noFactura2") != null? objCobranza.get(t).get("noFactura2"):"");
				con.setFecFactura(funciones.ponerFechaDate(objCobranza.get(t).get("fecFactura")));
				con.setFecValor(funciones.ponerFechaDate(objCobranza.get(t).get("fecValor")));
				con.setNoClienteS(objCobranza.get(t).get("noCliente") != null? objCobranza.get(t).get("noCliente"):"");
				con.setConcepto(objCobranza.get(t).get("concepto") != null? objCobranza.get(t).get("concepto"):"");
				con.setFolioCob(objCobranza.get(t).get("folioCob") != null ? Integer.parseInt(objCobranza.get(t).get("folioCob")):0);
				con.setSecuencia(objCobranza.get(t).get("secuencia") != null ? Integer.parseInt(objCobranza.get(t).get("secuencia")):0);
				con.setRazonSoc(objCobranza.get(t).get("razonSoc") != null? objCobranza.get(t).get("razonSoc"):"");
				con.setEstatus(objCobranza.get(t).get("estatus") != null? objCobranza.get(t).get("estatus"):"");
				con.setFolioDetMov(objCobranza.get(t).get("folioDetMov") != null? objCobranza.get(t).get("folioDetMov"):"");
				con.setImporteSol(objCobranza.get(t).get("importeSol") != null? Double.parseDouble(objCobranza.get(t).get("importeSol")):0);
				
				listCobranza.add(con);
			}
			
			
			

			String causa2 =(causa);
			String cuenta2 =(cuenta);
			
			identificacionIngresosService = (IdentificacionIngresosService)contexto.obtenerBean("identificacionIngresosBusinessImpl");
			result = identificacionIngresosService.concilVirSCobranza(listCobranza,causa2, cuenta2);
			
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Ingresos, C:IdentificacionIngresosAction, M:concilVirSCobranza");
			result.put("error", "Error al convertir datos Action.");
		}
		return result;
	}
	
	@DirectMethod
	public List<PersonaDto> obtenerClientes(String cliente) {
		List<PersonaDto> personas = null;
		if(!Utilerias.haveSession(WebContextManager.get()))
			return personas;
		try {
			identificacionIngresosService = (IdentificacionIngresosService)contexto.obtenerBean("identificacionIngresosBusinessImpl");
			personas = identificacionIngresosService.obtenerClientes(cliente);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Ingresos, C:IdentificacionIngresosAction, M:obtenerClientes");
		}
		return personas;
	}
	
	@DirectMethod
    public String exportaExcel(int empresa, int caso, String fecIni, String fecFin) {
		String resultado = "";
		if(!Utilerias.haveSession(WebContextManager.get()))
			return resultado;
		try {
			identificacionIngresosService = (IdentificacionIngresosService)contexto.obtenerBean("identificacionIngresosBusinessImpl");
			resultado = identificacionIngresosService.exportaExcel(empresa, caso, fecIni, fecFin);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C:MovimientosBEAction , M: exportaExcel");
		}
		return resultado;
	}
	
	@DirectMethod	
    public HSSFWorkbook obtenerExcel(String nombre) {
		FileInputStream file = null;
		HSSFWorkbook workbook = null;
		File arch = new File(nombre);
		
		try {
			file = new FileInputStream(arch);
			workbook = new HSSFWorkbook(file);
			file.close();
		} catch (FileNotFoundException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:obtenerExcel");
		} catch (IOException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:obtenerExcel");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:obtenerExcel");
		}
		arch.delete();
		return workbook;
	}
	
	@DirectMethod	
    public String enviarAnticipo(int folioDet, String cliente, double importe, int noEmpresa, String divisa) {
		String res = "";
		if(!Utilerias.haveSession(WebContextManager.get())) {
			res = "No tiene sesi�n activa";
			return res;
		}
		try {
			identificacionIngresosService = (IdentificacionIngresosService)contexto.obtenerBean("identificacionIngresosBusinessImpl");
			res = identificacionIngresosService.enviarAnticipo(folioDet, cliente, importe, noEmpresa, divisa);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Ingresos, C:IdentificacionIngresos ,M:enviarAnticipo");
		}
		return res;
	}
	
}
