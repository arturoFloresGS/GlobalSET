package com.webset.set.egresos.action;



import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.egresos.dto.PagosPendientesDto;
import com.webset.set.egresos.dto.PlantillaDto;
import com.webset.set.egresos.service.PagosPedientesService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;

/*
 * Clase Creada por Luis Alfredo Serrato Montes de Oca
 * 11092015
 */


public class ConsultaPagosPendientesAction {
	Bitacora bitacora = new Bitacora();
	Contexto contexto=new  Contexto();
	Funciones funciones = new Funciones();
	PagosPedientesService pendientesService;
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboGrupoFlujo(){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return list;
		try{
			pendientesService = (PagosPedientesService)contexto.obtenerBean("pagosPedientesBusinessImpl");
			list = pendientesService.llenarComboGrupoFlujo();
		}catch(Exception e){
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Egresos, C:ConsultarPagosPendientesAction, M:llenarComboGrupoFlujo");
		}return list;
	}
	
	@DirectMethod
	public List<PagosPendientesDto> retornarLisPagosPendientes(int reglaNegocio, String fechaConIni, String fechaConFin ,
			String fechaPropIni, String fechaPropFin, String divisa, String indicadores, String numDoc, String tipoDoc,
			String numDocA, String numDocB,String equivalePersonaA,String equivalePersonaB , String grupoEmpresa,
			String jsonDocumentos ,String jsonProveedores, boolean proveedores, boolean documentos,String tipoBusqueda,
			String concepto,String montoUno,String montoDos,String factura){
		//System.out.println("1");
		System.out.println("datos "+concepto+' '+montoUno+' '+montoDos+' '+factura);
		List<Map<String, String>> rangosDocumentos = new ArrayList<Map<String, String>>();
		List<Map<String, String>> rangosProveedores = new ArrayList<Map<String, String>>();
		Gson gson = new Gson();
		List<PagosPendientesDto> pagosPendientes = new ArrayList<PagosPendientesDto>();
		PlantillaDto plantilla = new PlantillaDto();
		plantilla.setIdReglaNegocio(reglaNegocio);
		plantilla.setFechaContableInicio(fechaConIni);
		plantilla.setFechaContableFin(fechaConFin);
		plantilla.setFechaPropuestaPagoInicio(fechaPropIni);
		plantilla.setFechaPropuestaPagoFin(fechaPropFin);
		plantilla.setDivisa(divisa);
		plantilla.setIndicadores(indicadores);
		plantilla.setTipoDocumento(tipoDoc);
		
		plantilla.setEquivalePersonaA(equivalePersonaA);
		plantilla.setEquivalePersonaB(equivalePersonaB);
		plantilla.setNumDocA(numDocA);
		plantilla.setNumDocB(numDocB);
		plantilla.setGrupoEmpresas(grupoEmpresa);
		plantilla.setTipoBusqueda(tipoBusqueda);
		
		plantilla.setConcepto(concepto);
		
		if(montoUno.equals("") && montoDos.equals("")){
			plantilla.setMontoUno(0);
			plantilla.setMontoDos(0);
		}else{
			if(montoUno.equals("") && !montoDos.equals("")){
				plantilla.setMontoUno(0);
				plantilla.setMontoDos(Float.parseFloat(montoDos));
			}else{
				if(!montoUno.equals("") && montoDos.equals("")){
					plantilla.setMontoUno(Float.parseFloat(montoUno));
					plantilla.setMontoDos(0);
				}else{
					plantilla.setMontoUno(Float.parseFloat(montoUno));
					plantilla.setMontoDos(Float.parseFloat(montoDos));
				}
			}
		}
		
		plantilla.setFactura(factura);
		
		if(jsonDocumentos!=null && !jsonDocumentos.trim().equals("")){
			 rangosDocumentos = gson.fromJson(jsonDocumentos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		}
		
		if(jsonProveedores!=null && !jsonProveedores.trim().equals("")){
			rangosProveedores = gson.fromJson(jsonProveedores, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		}
		
		try {
			if(Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),180)){
				pendientesService = (PagosPedientesService)contexto.obtenerBean("pagosPedientesBusinessImpl");
				pagosPendientes = pendientesService.llamarObtenerPagosPendientes(plantilla,rangosDocumentos,rangosProveedores, proveedores, documentos);
			}
			
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPagosPendientesAction, M:retornarLisPagosPendientes");
		}
		return pagosPendientes;
	}
	
	@DirectMethod
	public List<PagosPendientesDto> retornarLisPagosPendientesNivDos(int numeroEmpresa, int reglaNegocio, String fechaConIni, String fechaConFin ,
			String fechaPropIni, String fechaPropFin, String divisa, String indicadores, String numDoc, String tipoDoc,
			String numDocA, String numDocB,String equivalePersonaA,String equivalePersonaB , String grupoEmpresa,
			String jsonDocumentos ,String jsonProveedores, boolean proveedores, boolean documentos, String tipoBusqueda){
		List<PagosPendientesDto> pagosPendientes = new ArrayList<PagosPendientesDto>();
		List<Map<String, String>> rangosDocumentos = new ArrayList<Map<String, String>>();
		List<Map<String, String>> rangosProveedores = new ArrayList<Map<String, String>>();
		Gson gson = new Gson();
		PlantillaDto plantilla = new PlantillaDto();
		
		plantilla.setIdReglaNegocio(reglaNegocio);
		plantilla.setFechaContableInicio(fechaConIni);
		plantilla.setFechaContableFin(fechaConFin);
		plantilla.setFechaPropuestaPagoInicio(fechaPropIni);
		plantilla.setFechaPropuestaPagoFin(fechaPropFin);
		plantilla.setDivisa(divisa);
		plantilla.setIndicadores(indicadores);
		plantilla.setTipoDocumento(tipoDoc);
		
		plantilla.setEquivalePersonaA(equivalePersonaA);
		plantilla.setEquivalePersonaB(equivalePersonaB);
		plantilla.setNumDocA(numDocA);
		plantilla.setNumDocB(numDocB);
		plantilla.setTipoBusqueda(tipoBusqueda);
		
		if(jsonDocumentos!=null && !jsonDocumentos.trim().equals("")){
			 rangosDocumentos = gson.fromJson(jsonDocumentos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		}
		
		if(jsonProveedores!=null && !jsonProveedores.trim().equals("")){
			rangosProveedores = gson.fromJson(jsonProveedores, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		}
		
		try {
			//if(Utilerias.haveSession(WebContextManager.get())
			//		&& Utilerias.tienePermiso(WebContextManager.get(),180)){
				pendientesService = (PagosPedientesService)contexto.obtenerBean("pagosPedientesBusinessImpl");
			
			pagosPendientes = pendientesService.llamarObtenerPagosPendientesNivDos(numeroEmpresa, plantilla ,rangosDocumentos,rangosProveedores, proveedores, documentos);
			//}
			
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPagosPendientesAction, M:retornarLisPagosPendientesNivDos");
		}
		return pagosPendientes;

	}
	
	@DirectMethod
	public List<PagosPendientesDto> retornarLisPagosPendientesNivTres(int numeroEmpresa, String acreedor, int reglaNegocio, String fechaConIni, String fechaConFin ,
			String fechaPropIni, String fechaPropFin, String divisa, String indicadores, String numDoc, String tipoDoc,
			String numDocA, String numDocB,String equivalePersonaA,String equivalePersonaB , String grupoEmpresa,
			String jsonDocumentos ,String jsonProveedores, boolean documentos, String beneficiario, String tipoBusqueda){
		System.out.println("Nivel 3");
		
		List<PagosPendientesDto> pagosPendientes = new ArrayList<PagosPendientesDto>();
		List<Map<String, String>> rangosDocumentos = new ArrayList<Map<String, String>>();
		List<Map<String, String>> rangosProveedores = new ArrayList<Map<String, String>>();
		Gson gson = new Gson();
		PlantillaDto plantilla = new PlantillaDto();
		plantilla.setIdReglaNegocio(reglaNegocio);
		plantilla.setFechaContableInicio(fechaConIni);
		plantilla.setFechaContableFin(fechaConFin);
		plantilla.setFechaPropuestaPagoInicio(fechaPropIni);
		plantilla.setFechaPropuestaPagoFin(fechaPropFin);
		plantilla.setDivisa(divisa);
		plantilla.setIndicadores(indicadores);
		plantilla.setTipoDocumento(tipoDoc);
		
		plantilla.setEquivalePersonaA(equivalePersonaA);
		plantilla.setEquivalePersonaB(equivalePersonaB);
		plantilla.setNumDocA(numDocA);
		plantilla.setNumDocB(numDocB);
		plantilla.setTipoBusqueda(tipoBusqueda);
		
		if(jsonDocumentos!=null && !jsonDocumentos.trim().equals("")){
			 rangosDocumentos = gson.fromJson(jsonDocumentos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		}
		
		if(jsonProveedores!=null && !jsonProveedores.trim().equals("")){
			rangosProveedores = gson.fromJson(jsonProveedores, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		}
		
		try {
			if(Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),180)){
				pendientesService = (PagosPedientesService)contexto.obtenerBean("pagosPedientesBusinessImpl");
				pagosPendientes = pendientesService.llamarObtenerPagosPendientesNivTres(numeroEmpresa, acreedor, plantilla,rangosDocumentos,rangosProveedores, documentos, beneficiario);
			}
			
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPagosPendientesAction, M:llamarObtenerPagosPendientesNivTres");
		}
		return pagosPendientes;
	}
	
	@DirectMethod
	public String retornarJsonPendientesNivTres(int numeroEmpresa, String acreedor, int reglaNegocio, String fechaConIni, String fechaConFin ,
			String fechaPropIni, String fechaPropFin, String divisa, String indicadores, String numDoc, String tipoDoc,
			String numDocA, String numDocB,String equivalePersonaA,String equivalePersonaB , String grupoEmpresa,
			String jsonDocumentos ,String jsonProveedores, int marca, boolean documentos, String tipoBusqueda){
		System.out.println("Nivel 3");
		
		String pagosPendientes = null;
		List<Map<String, String>> rangosDocumentos = new ArrayList<Map<String, String>>();
		List<Map<String, String>> rangosProveedores = new ArrayList<Map<String, String>>();
		Gson gson = new Gson();
		PlantillaDto plantilla = new PlantillaDto();
		plantilla.setIdReglaNegocio(reglaNegocio);
		plantilla.setFechaContableInicio(fechaConIni);
		plantilla.setFechaContableFin(fechaConFin);
		plantilla.setFechaPropuestaPagoInicio(fechaPropIni);
		plantilla.setFechaPropuestaPagoFin(fechaPropFin);
		plantilla.setDivisa(divisa);
		plantilla.setIndicadores(indicadores);
		plantilla.setTipoDocumento(tipoDoc);
		
		plantilla.setEquivalePersonaA(equivalePersonaA);
		plantilla.setEquivalePersonaB(equivalePersonaB);
		plantilla.setNumDocA(numDocA);
		plantilla.setNumDocB(numDocB);
		plantilla.setTipoBusqueda(tipoBusqueda);
		
		if(jsonDocumentos!=null && !jsonDocumentos.trim().equals("")){
			 rangosDocumentos = gson.fromJson(jsonDocumentos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		}
		
		if(jsonProveedores!=null && !jsonProveedores.trim().equals("")){
			rangosProveedores = gson.fromJson(jsonProveedores, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		}
		
		try {
			if(Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),180)){
				pendientesService = (PagosPedientesService)contexto.obtenerBean("pagosPedientesBusinessImpl");
				pagosPendientes = pendientesService.retornarJsonPendientesNivTres(
						numeroEmpresa, acreedor, plantilla,rangosDocumentos,
						rangosProveedores, marca, documentos);
			}
			
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPagosPendientesAction, M:llamarObtenerPagosPendientesNivTres");
		}
		return pagosPendientes;
	}
	
	@DirectMethod
	public String crearPropuesta(String matrizDatosPagos, int sociedad, double importeSeleccionado, String fechaPago, String divisa, String tipoBusqueda){
		String mensaje = "";
		if(matrizDatosPagos.length() > 0){
			try{
				System.out.println("creando propuesta "+matrizDatosPagos+sociedad+importeSeleccionado+fechaPago+divisa+tipoBusqueda);
				if(Utilerias.haveSession(WebContextManager.get())
						&& Utilerias.tienePermiso(WebContextManager.get(),180)){
					pendientesService = (PagosPedientesService)contexto.obtenerBean("pagosPedientesBusinessImpl");
					mensaje = pendientesService.crearPropuestaPago(matrizDatosPagos, sociedad ,importeSeleccionado, fechaPago, divisa, tipoBusqueda);
					if(mensaje.equals("crearNueva")){
						mensaje = pendientesService.crearPropuestaPagoSimple(matrizDatosPagos, sociedad ,importeSeleccionado, fechaPago, divisa, tipoBusqueda);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:Egresos, C:ConsultaPagosPendientesAction, M:llamarObtenerPagosPendientesNivTres");
				
			}
			
		}
		
		return mensaje;
		
	}
	
	@DirectMethod
	public String crearPropuestaSimple(String matrizDatosPagos, int sociedad, double importeSeleccionado, String fechaPago, String divisa, String tipoBusqueda){
		String mensaje = "";
		if(matrizDatosPagos.length() > 0){
			try{
				if(Utilerias.haveSession(WebContextManager.get())
						&& Utilerias.tienePermiso(WebContextManager.get(),180)){
					pendientesService = (PagosPedientesService)contexto.obtenerBean("pagosPedientesBusinessImpl");
					mensaje = pendientesService.crearPropuestaPagoSimple(matrizDatosPagos, sociedad ,importeSeleccionado, fechaPago, divisa, tipoBusqueda);
				}
			}catch(Exception e){
				e.getStackTrace();
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:Egresos, C:ConsultaPagosPendientesAction, M:llamarObtenerPagosPendientesNivTres");
			}
		}
		return mensaje;
		
	}
	
	
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboBanco(String proveedor, String tipoBusqueda){
		List<LlenaComboGralDto> listBancos = new ArrayList<LlenaComboGralDto>();
		System.out.println(proveedor);
		try {
			if(Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),180)){
				pendientesService = (PagosPedientesService)contexto.obtenerBean("pagosPedientesBusinessImpl");
				listBancos = pendientesService.obtenerListaBancos(proveedor, tipoBusqueda);
			}
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPagosPendientesAction, M:llenarComboBanco");
		}
		
		return listBancos;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboChequera(String proveedor, int idBanco, String tipoBusqueda){
		List<LlenaComboGralDto> listChequeras = new ArrayList<LlenaComboGralDto>();
		
		try {
			if(Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),180)){
				pendientesService = (PagosPedientesService)contexto.obtenerBean("pagosPedientesBusinessImpl");
				listChequeras = pendientesService.obtenerListaChequeras(proveedor, idBanco, tipoBusqueda);
			}
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPagosPendientesAction, M:llenarComboChequera");
		}
		
		return listChequeras;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboBancosPagador(int noEmpresa, String divisa){
		List<LlenaComboGralDto> listComboBancosPagador = new ArrayList<LlenaComboGralDto>();
		
		try {
			if(Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),180)){
				pendientesService = (PagosPedientesService)contexto.obtenerBean("pagosPedientesBusinessImpl");
				listComboBancosPagador = pendientesService.obtenerListaBancosPagador(noEmpresa, divisa);
			}
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPagosPendientesAction, M:llenarComboChequera");
		}
		
		return listComboBancosPagador;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboChequerasPagadoras(int noEmpresa, String divisa, int idBanco, String beneficiario, String acreedor){
		List<LlenaComboGralDto> listComboChequerasPagadoras = new ArrayList<LlenaComboGralDto>();
		
		try {
			if(Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),180)){
				pendientesService = (PagosPedientesService)contexto.obtenerBean("pagosPedientesBusinessImpl");
				listComboChequerasPagadoras = pendientesService.obtenerListaChequerasPagadoras(noEmpresa, divisa, idBanco, beneficiario, acreedor);
			}
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPagosPendientesAction, M:llenarComboChequerasPagadoras");
		}
	
		return listComboChequerasPagadoras;
	}

	
	@DirectMethod
	public List<LlenaComboDivisaDto> obtenerDivisas(){
		List<LlenaComboDivisaDto>list=new ArrayList<LlenaComboDivisaDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),180)){
				pendientesService = (PagosPedientesService)contexto.obtenerBean("pagosPedientesBusinessImpl");
				list = pendientesService.obtenerDivisas();
			}
		}catch(Exception e){
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPagosPendientesAction, M:obtenerDivisas");			
		}
		return list;
	}
	
	@DirectMethod
	public PlantillaDto obtenerFechasIni(){
		PlantillaDto plantilla = new PlantillaDto();
		try{
			if(Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),180)){
				pendientesService = (PagosPedientesService)contexto.obtenerBean("pagosPedientesBusinessImpl");
				plantilla = pendientesService.obtenerFechasIni();
			}
			
		}catch(Exception e){
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPagosPendientesAction, M:obtenerFechasIni");	
		}
		
		return plantilla;
	}
	
	@DirectMethod
	public String guardarPlantilla(String datosPlantilla, String jsonDocumentos, String jsonProveedores){
		String mensaje = "";
		try{
			if(Utilerias.haveSession(WebContextManager.get())&& Utilerias.tienePermiso(WebContextManager.get(),180)){
				pendientesService = (PagosPedientesService)contexto.obtenerBean("pagosPedientesBusinessImpl");
				List<Map<String, String>> rangosDocumentos = new ArrayList<Map<String, String>>();
				List<Map<String, String>> rangosProveedores = new ArrayList<Map<String, String>>();
				Gson gson = new Gson();
				if(jsonDocumentos!=null && !jsonDocumentos.trim().equals("")){
					 rangosDocumentos = gson.fromJson(jsonDocumentos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				}
				
				if(jsonProveedores!=null && !jsonProveedores.trim().equals("")){
					rangosProveedores = gson.fromJson(jsonProveedores, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				}
				mensaje = pendientesService.guardarPlantilla(datosPlantilla,rangosDocumentos ,rangosProveedores);
			}
		}catch(Exception e){
			e.getStackTrace();
			mensaje = "Ocurrio un error al guardar la plantilla";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPagosPendientesAction, M:guardarPlantilla");	
		}
		
		
		return mensaje;
	}
	
	@DirectMethod
	public List<PlantillaDto> obtenerPlantillas(){
		List<PlantillaDto> listPlantillas = new ArrayList<PlantillaDto>();
		
		try {
			if(Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),180)){
				pendientesService = (PagosPedientesService)contexto.obtenerBean("pagosPedientesBusinessImpl");
				listPlantillas = pendientesService.obtenerListaPlantillas();
			}
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPagosPendientesAction, M:llenarComboChequerasPagadoras");
		}
	
		return listPlantillas;
	}
	
	@DirectMethod
	public Map<String, Object> obtenerPlantilla(int idPlantilla){
		Map<String, Object> resultado = new HashMap<String, Object>();
		try {
			if(Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),180)){
				pendientesService = (PagosPedientesService)contexto.obtenerBean("pagosPedientesBusinessImpl");
				resultado = pendientesService.obtenerPlantilla(idPlantilla);
			}
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPagosPendientesAction, M:obtenerPlantilla");
		}		
		
		return resultado;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboReglaNegocio(String tipoRegla){
		List<LlenaComboGralDto> listRet= new ArrayList<LlenaComboGralDto>();
		LlenaComboGralDto dtoIn = new LlenaComboGralDto();
		
		dtoIn.setTabla("REGLA_NEGOCIO");
		dtoIn.setCampoUno("id_regla");
		dtoIn.setCampoDos("regla_negocio");
		dtoIn.setOrden("regla_negocio");
		dtoIn.setCondicion(tipoRegla);
		
		try{
			if(Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),180)){
				pendientesService = (PagosPedientesService)contexto.obtenerBean("pagosPedientesBusinessImpl");
				listRet = pendientesService.llenarComboReglaNegocio(dtoIn);
			}
		}catch(Exception e){
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:llenarComboReglaNegocio");	
		}
		return listRet;
	}
	
	@DirectMethod
	public String controlFechas(String fechaHoy, String origen){
		System.out.println(fechaHoy);
		String mensaje = "";
		
		try {
			if(Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),180)){
				pendientesService = (PagosPedientesService)contexto.obtenerBean("pagosPedientesBusinessImpl");
				mensaje = pendientesService.controlFechas(fechaHoy, origen);
			}
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:controlFechas");
		}
		
		return mensaje;
	}
	
	
	@DirectMethod
	public String pagosProgramados(String fecha){
		System.out.println("Entro a pagosProgramados");
		String mensaje = "";
		try {
			if(Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),180)){
				pendientesService = (PagosPedientesService)contexto.obtenerBean("pagosPedientesBusinessImpl");
				mensaje = pendientesService.pagosProgramados(fecha);
			}
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:pagosProgramados");
		}
		
		return mensaje;
	}
	
	@DirectMethod
	public String ejecutarPagoParcial(String fechaPago, float importeTotal, float importePagoParcial, String noFolioDet){
		String mensaje = "";
		try {
			if(Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),180)){
				pendientesService = (PagosPedientesService)contexto.obtenerBean("pagosPedientesBusinessImpl");
				mensaje = pendientesService.ejecutarPagoParcial(fechaPago,importeTotal,importePagoParcial,noFolioDet);
			}
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:ejecutarPagoParcial");
		}
		
		return mensaje;
	}
	
	@DirectMethod
	public String ejecutarCambioDatosMovimiento(String fechaPago, String claves, int idBancoBenef, String idChequeraBenef, int idBancoPag, String idChequeraPag, String refBancaria, String tipoBusqueda){
		String mensaje = "";
		try {
			if(Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),180)){
				pendientesService = (PagosPedientesService)contexto.obtenerBean("pagosPedientesBusinessImpl");
				mensaje = pendientesService.ejecutarCambioDatosMovimiento(fechaPago, claves, idBancoBenef, idChequeraBenef, idBancoPag, idChequeraPag, refBancaria, tipoBusqueda);
			}
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:ejecutarCambioFechaPago");
		}
		
		return mensaje;
	}
	
	@DirectMethod
	public List<PagosPendientesDto> obtenerTodosPagosPendientesPorEmpresa(int numeroEmpresa, String acreedor, int reglaNegocio, String fechaConIni, String fechaConFin ,
			String fechaPropIni, String fechaPropFin, String divisa, String indicadores, String numDoc, String tipoDoc,
			String numDocA, String numDocB,String equivalePersonaA,String equivalePersonaB , String grupoEmpresa, String tipo){
		System.out.println("1");
		List<PagosPendientesDto> pagosPendientes = new ArrayList<PagosPendientesDto>();
		PlantillaDto plantilla = new PlantillaDto();
		plantilla.setIdReglaNegocio(reglaNegocio);
		plantilla.setFechaContableInicio(fechaConIni);
		plantilla.setFechaContableFin(fechaConFin);
		plantilla.setFechaPropuestaPagoInicio(fechaPropIni);
		plantilla.setFechaPropuestaPagoFin(fechaPropFin);
		plantilla.setDivisa(divisa);
		plantilla.setIndicadores(indicadores);
		plantilla.setTipoDocumento(tipoDoc);
		
		plantilla.setEquivalePersonaA(equivalePersonaA);
		plantilla.setEquivalePersonaB(equivalePersonaB);
		plantilla.setNumDocA(numDocA);
		plantilla.setNumDocB(numDocB);
		
		try {
			if(Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),180)){
				pendientesService = (PagosPedientesService)contexto.obtenerBean("pagosPedientesBusinessImpl");
				pagosPendientes = pendientesService.obtenerTodosPagosPendientesPorEmpresa(numeroEmpresa, acreedor, plantilla, tipo);
			}
			
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPagosPendientesAction, M:obtenerTodosPagosPendientesPorEmpresa");
		}
		return pagosPendientes;
	}
	
}



