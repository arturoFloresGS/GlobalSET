package com.webset.set.egresos.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.egresos.dto.PagosPropuestosDto;
import com.webset.set.egresos.service.ConsultaPropuestasService;
import com.webset.set.egresos.service.PagoPropuestasService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.ComunEgresosDto;
import com.webset.set.utilerias.dto.GrupoEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.SaldosChequerasDto;
import com.webset.set.utilerias.dto.SeleccionAutomaticaGrupoDto;
import com.webset.utils.tools.Utilerias;

import net.sf.jasperreports.engine.JRDataSource;
/**
 * @author	Cristian Garcia Garcia
 * @version	1.0
 * @since	25/Abril/2011	
 */

public class PagoPropuestasAction {
	 Bitacora bitacora = new Bitacora();
	private Contexto contexto = new  Contexto();
	private Funciones funciones = new Funciones();
	ConsultaPropuestasService consultaPropuestasService;
	/*Revisado Alberto A.G.*/
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboGrupoEmpresas(int idUsuario){
		GrupoEmpresasDto dtoIn= new GrupoEmpresasDto();
		List<LlenaComboGralDto> listRet= new ArrayList<LlenaComboGralDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),52)){
				PagoPropuestasService pagoPropuestasService = (PagoPropuestasService)contexto.obtenerBean("pagoPropuestasBusinessImpl");
				dtoIn.setIdUsuario("" + idUsuario);
				listRet=pagoPropuestasService.llenarComboGrupoEmpresa(dtoIn);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Egresos, C:PagoPropuestasAction, M:llenarComboGrupoEmpresas");	
		}
		return listRet;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboDivXEmp(int idUsuario){
		List<LlenaComboGralDto> listRet= new ArrayList<LlenaComboGralDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),52)){
				PagoPropuestasService pagoPropuestasService =(PagoPropuestasService)contexto.obtenerBean("pagoPropuestasBusinessImpl");
				listRet=pagoPropuestasService.llenarComboDivXEmp(idUsuario);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Egresos, C:PagoPropuestasAction, M:llenarComboDivXEmp");	
		}
		return listRet;
	}
	/*Revisado Alberto A.G.*/
	@DirectMethod
	public SaldosChequerasDto obtenerSaldosChequeras(int idGrupoEmpresa, 
			int idDivision, int idUsuario){
		SaldosChequerasDto dto= new SaldosChequerasDto();	
		
		try{
			System.out.println("saldosCheq action "+idGrupoEmpresa);
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),52)){
				PagoPropuestasService pagoPropuestasService=(PagoPropuestasService)contexto.obtenerBean("pagoPropuestasBusinessImpl");
				dto.setIdGrupo(idGrupoEmpresa>0?idGrupoEmpresa:0);
				dto.setPsDivision(idDivision>0?""+idDivision:"");
				dto.setIdUsuario(idUsuario>0?idUsuario:0);
				dto=pagoPropuestasService.obtenerSaldosChequeras(dto);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Egresos, C:PagoPropuestasAction, M:obtenerSaldosChequeras");	
		}
		return dto;
	}
	/*Revisado Alberto A.G.*/
	@DirectMethod
	public List<MovimientoDto> consultarPropuestas(int idGrupoEmpresa, 
			int idDivision,String fecIni,String fecFin,String chkLocal,int idUsuario){
		List<MovimientoDto> listCons = new ArrayList<MovimientoDto>();
		PagosPropuestosDto dtoIn = new PagosPropuestosDto();
		
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),52)){
				PagoPropuestasService pagoPropuestasService=(PagoPropuestasService)contexto.obtenerBean("pagoPropuestasBusinessImpl");
				dtoIn.setIdGrupoEmpresa(idGrupoEmpresa);
				dtoIn.setIdDivision(idDivision>0?idDivision:0);
				
				if(fecIni!=null && !fecIni.equals(""))
					dtoIn.setFechaIni(funciones.ponerFechaDate(fecIni));
				if(fecFin!=null && !fecFin.equals(""))
					dtoIn.setFechaFin(funciones.ponerFechaDate(fecFin));
				if(chkLocal!=null && chkLocal.equals("true"))
					dtoIn.setBLocal(true);
				else
					dtoIn.setBLocal(false);
				
				dtoIn.setIdUsuario(idUsuario>0?idUsuario:0);
				
				listCons = pagoPropuestasService.consultarPropuestas(dtoIn);
				/*
				if(listCons!=null && listCons.size()>0)
				{
					//Nombre de la grafica.
					String sName=idGrupoEmpresa+""+idDivision+""+chkLocal;
					
					//Se crean las graficas con los datos obtenidos en la consulta.
					Map<String, Double> datos = new HashMap<String, Double>();
					
					if(idGrupoEmpresa==0)
					{
						for(int i=0; i<listCons.size(); i++){
							MovimientoDto movtoCupos = (MovimientoDto)listCons.get(i);
							
							if(datos.containsKey(movtoCupos.getDescGrupoFlujo())){
								double dImporte = datos.get(movtoCupos.getDescGrupoFlujo());
								datos.remove(movtoCupos.getDescGrupoFlujo());
								datos.put(movtoCupos.getDescGrupoFlujo(), (movtoCupos.getImporteMn()+dImporte));
							}
							else {
								datos.put(movtoCupos.getDescGrupoFlujo(), movtoCupos.getImporteMn());
							}
						}
					}
					else
					{
						for(int i=0; i<listCons.size(); i++){
							MovimientoDto movtoCupos = (MovimientoDto)listCons.get(i);
							
							if(datos.containsKey(movtoCupos.getFecPropuesta().toString())){
								double dImporte = datos.get(movtoCupos.getFecPropuesta().toString());
								datos.remove(movtoCupos.getFecPropuesta().toString());
								datos.put(movtoCupos.getFecPropuesta().toString(), (movtoCupos.getImporteMn()+dImporte));
							}
							else {
								datos.put(movtoCupos.getFecPropuesta().toString(), movtoCupos.getImporteMn());
							}
						}
					}
					
					CreacionGrafica cg = new CreacionGrafica();
					cg.crearGraficaPie   (idUsuario+"", "PagoPropuestas"+sName, "Pago Propuestas", datos);
					cg.crearGraficaBarras(idUsuario+"", "PagoPropuestas"+sName, "Pago Propuestas", "Importe", datos, true);
					cg.crearGraficaLineas(idUsuario+"", "PagoPropuestas"+sName, "Pago Propuestas", "Importe", datos, true);
					//Fin graficas
				}*/
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Egresos, C:PagoPropuestasAction, M:consultarPropuestas");	
		}
		return listCons;
	}
	
	@DirectMethod
	public List<ComunEgresosDto> validarCpaVtaTransfer(String datos){

		Gson gson = new Gson();
		List<Map<String, String>> objParams = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<ComunEgresosDto> listRetorno = new ArrayList<ComunEgresosDto>();
		List<ComunEgresosDto>listGrid = new ArrayList<ComunEgresosDto>();
		
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),52)){
				PagoPropuestasService pagoPropuestasService = (PagoPropuestasService)contexto.obtenerBean("pagoPropuestasBusinessImpl");
				
				for(int i=0; i<objParams.size();i++)
				{
					ComunEgresosDto dtoGrid= new ComunEgresosDto(); 
					dtoGrid.setAgrupaEmpChe(objParams.get(i).get("agrupaCheEmp")!=null?Integer.parseInt(objParams.get(i).get("agrupaCheEmp")):0);
					dtoGrid.setCveControl(objParams.get(i).get("cveControl"));
					dtoGrid.setFechaPago(funciones.ponerFechaDate(objParams.get(i).get("fecPropuesta")));
					dtoGrid.setIdGrupoEmpresas(objParams.get(i).get("idGrupoEmpresa")!=null?Integer.parseInt(objParams.get(i).get("idGrupoEmpresa")):0);
					dtoGrid.setIdGrupoRubros(objParams.get(i).get("idGrupoRubro")!=null?Integer.parseInt(objParams.get(i).get("idGrupoRubro")):0);
					listGrid.add(dtoGrid);
				}
				listRetorno = pagoPropuestasService.validarCpaVtaTransfer(listGrid);
			}
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Egresos, C:PagoPropuestasAction, M:validarCpaVtaTransfer");
		}
		return listRetorno;  
	}
	
	@DirectMethod
	public Map<String, Object> insetarZexpFact (String datos, String fecHoy, int idUsuario, boolean agrupa){
		Map<String, Object>mapRetorno = new HashMap<String, Object>();
		mapRetorno.put("msgError", "Error desconocido");
		mapRetorno.put("estatus", false);
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),52)){
				PagoPropuestasService pagoPropuestasService = (PagoPropuestasService)
						contexto.obtenerBean("pagoPropuestasBusinessImpl");
				System.out.println("insertarZexpFact "+datos);
				mapRetorno= pagoPropuestasService.insertarFoliosZexpFact(datos, fecHoy, idUsuario, agrupa);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:PagoPropuestasAction, M:insetarZexpFact");
		}
		return mapRetorno;
	}
	
	@DirectMethod
	public List<MovimientoDto>consultarDetalle(int idGrupoEmpresa, int idGrupo, String cveControl, int usr1, int usr2, int usr3){
		SeleccionAutomaticaGrupoDto dtoIn= new SeleccionAutomaticaGrupoDto();
		List<MovimientoDto>listDetalle= new ArrayList<MovimientoDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get()) && !Utilerias.tienePermiso(WebContextManager.get(),52)){
			return listDetalle;
		}
		
		try{
			//consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			PagoPropuestasService pagoPropuestasService = (PagoPropuestasService)contexto.obtenerBean("pagoPropuestasBusinessImpl");
			dtoIn.setIdGrupoFlujo(idGrupoEmpresa);
			dtoIn.setIdGrupo(idGrupo);
			dtoIn.setCveControl(cveControl);
			dtoIn.setUsuarioUno(usr1);
			dtoIn.setUsuarioDos(usr2);
			dtoIn.setUsuarioTres(usr3);
			listDetalle = pagoPropuestasService.consultarDetalle(dtoIn);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:consultarDetalle");	
		}
		return listDetalle;
	}
	@DirectMethod
	public List<MovimientoDto>consultarDetallePropuestasNoPagadas(int idGrupoEmpresa, int idGrupo, String cveControl, int usr1, int usr2, int usr3){
		SeleccionAutomaticaGrupoDto dtoIn= new SeleccionAutomaticaGrupoDto();
		List<MovimientoDto>listDetalle= new ArrayList<MovimientoDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get()) && !Utilerias.tienePermiso(WebContextManager.get(),52)){
			
			return listDetalle;
		}
		
		try{
			
			PagoPropuestasService pagoPropuestasService = (PagoPropuestasService)contexto.obtenerBean("pagoPropuestasBusinessImpl");
			dtoIn.setIdGrupoFlujo(idGrupoEmpresa);
			dtoIn.setIdGrupo(idGrupo);
			dtoIn.setCveControl(cveControl);
			dtoIn.setUsuarioUno(usr1);
			dtoIn.setUsuarioDos(usr2);
			dtoIn.setUsuarioTres(usr3);
			listDetalle = pagoPropuestasService.consultarDetallePropuestasNoPagadas(dtoIn);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:consultarDetalle");	
		}
		return listDetalle;
	}
	
	
	
	@DirectMethod
	public Map<String,Object> ejecutarPropuestas(String datos, String fecHoy){
		Gson gson = new Gson();
		List<Map<String, String>> objParams = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		Map<String,Object>mapRetorno= new HashMap<String,Object>();
		List<ComunEgresosDto>listGrid= new ArrayList<ComunEgresosDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),52)){
				PagoPropuestasService pagoPropuestasService = (PagoPropuestasService)contexto.obtenerBean("pagoPropuestasBusinessImpl");
				for(int i=0; i<objParams.size();i++)
				{
					ComunEgresosDto dtoGrid= new ComunEgresosDto(); 
					dtoGrid.setAgrupaEmpChe(objParams.get(i).get("agrupaCheEmp")!=null ? Integer.parseInt(objParams.get(i).get("agrupaCheEmp")) : 0);
					dtoGrid.setCveControl(objParams.get(i).get("cveControl"));
					dtoGrid.setFechaPago(funciones.ponerFechaDate(objParams.get(i).get("fecPropuesta")));
					dtoGrid.setIdGrupoEmpresas(objParams.get(i).get("idGrupoEmpresa")!=null ? Integer.parseInt(objParams.get(i).get("idGrupoEmpresa")) : 0);
					dtoGrid.setIdGrupoRubros(objParams.get(i).get("idGrupoRubro")!=null ? Integer.parseInt(objParams.get(i).get("idGrupoRubro")) : 0);				
					dtoGrid.setPsAgrupaCheques(objParams.get(i).get("agrupaCheques"));
					dtoGrid.setPsAgrupaTransfers(objParams.get(i).get("agrupaTransfers"));				
					
					listGrid.add(dtoGrid);
				}
				mapRetorno=pagoPropuestasService.ejecutarPropuestas(listGrid, fecHoy);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Egresos, C:PagoPropuestasAction, M:ejecutarPropuestas");
		}
		return mapRetorno;
	}
	
	@DirectMethod
	public HSSFWorkbook consultaPagosExcel(String clave, ServletContext context){
		HSSFWorkbook wb=null;
		System.out.println("clabe"+clave);
		try {
			
			PagoPropuestasService pagoPropuestasService = (PagoPropuestasService)contexto.obtenerBean("pagoPropuestasBusinessImpl",context);
			//objMantenimientoChequerasService = (MantenimientoChequerasService)contexto.obtenerBean("objMantenimientoChequerasBusinessImpl",context);
			
			wb=pagoPropuestasService.consultaPagosExcel(clave);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: reportePersonas");
		}return wb;
	}
	
	
	
	
	
	/**
	 * Este m�todo obtiene realiza el reporte de Pago de Propuestas
	 * @param datos
	 * @return
	 */
	@DirectMethod
	public JRDataSource obtenerReportePagoPropuestas(Map<String, Object> datos, ServletContext context) {
		JRDataSource jrDataSource = null;
		PagosPropuestosDto dtoIn = new PagosPropuestosDto();
		System.out.println("action reporte");
		try {
			
			PagoPropuestasService pagoPropuestasService = (PagoPropuestasService)contexto.obtenerBean("pagoPropuestasBusinessImpl", context);
			
			dtoIn.setIdGrupoEmpresa(Integer.parseInt(datos.get("idGrupoEmpresa").toString()));
			dtoIn.setIdDivision(Integer.parseInt(datos.get("idDivision").toString()) > 0 ? Integer.parseInt(datos.get("idDivision").toString()) : 0);
			
			if(datos.get("fecIni") != null && !datos.get("fecIni").toString().equals(""))
				dtoIn.setFechaIni(funciones.ponerFechaDate(datos.get("fecIni").toString()));
			if(datos.get("fecFin") != null && !datos.get("fecFin").toString().equals(""))
				dtoIn.setFechaFin(funciones.ponerFechaDate(datos.get("fecFin").toString()));
			if(datos.get("chkLocales") != null && datos.get("chkLocales").toString().equals("true"))
				dtoIn.setBLocal(true);
			else
				dtoIn.setBLocal(false);
			dtoIn.setIdUsuario(Integer.parseInt(datos.get("idUsuario").toString()) > 0 ? Integer.parseInt(datos.get("idUsuario").toString()) : 0);
			dtoIn.setCveControl(datos.get("cveControl") != null ? datos.get("cveControl").toString() : "");
			dtoIn.setTipoReporte(datos.get("tipoReporte") != null ? Integer.parseInt(datos.get("tipoReporte").toString()) : 0);
			
			jrDataSource = pagoPropuestasService.reportePagoPropuestas(dtoIn);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PagoPropuestasAction, M:obtenerReportePagoPropuestas");	
		}
		return jrDataSource;
	}
	
	@DirectMethod
	public Map<String, String>validaCvePropuesta(String cveControl){
		Map<String, String> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		try{
			if(!Utilerias.haveSession(WebContextManager.get()) && !Utilerias.tienePermiso(WebContextManager.get(),52)){
				return resultado;
			}
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			resultado = consultaPropuestasService.validaCvePropuesta(cveControl);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:validaCvePropuesta");	
		}
		return resultado;
	}
	//Agregado EMS 10/11/2015
			@DirectMethod
			public Map<String, String>insertaSubPropuesta(double montoMaximo, String nvaCveControl, String oldCveControl, 
														  int idUsuario, String fecha){
				Map<String, String> resultado = new HashMap<>();
				resultado.put("error", "");
				resultado.put("mensaje", "");
				
				
				try{
					if(!Utilerias.haveSession(WebContextManager.get()) && !Utilerias.tienePermiso(WebContextManager.get(),52)){
						return resultado;
					}
					consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
					resultado = consultaPropuestasService.insertaSubPropuesta(montoMaximo, nvaCveControl,oldCveControl,idUsuario, fecha);
				}catch(Exception e){
					bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Egresos, C:ConsultaPropuestasAction, M:insertaSubPropuesta");	
				}
				return resultado;
			}
			
			//Agregado EMS 10/11/2015
			@DirectMethod
			public Map<String, String>actualizaMontoPropuesta(double montoMaximo, String cveControl, String stencero){
				
				Map<String, String> resultado = new HashMap<>();
				resultado.put("error", "");
				resultado.put("mensaje", "");
				
				
				try{
					if(!Utilerias.haveSession(WebContextManager.get()) && !Utilerias.tienePermiso(WebContextManager.get(),52)){
						return resultado;
					}
					consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
					resultado = consultaPropuestasService.actualizaMontoPropuesta(montoMaximo, cveControl, stencero);
				}catch(Exception e){
					bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Egresos, C:ConsultaPropuestasAction, M:insertaSubPropuesta");	
				}
				return resultado;
			}
			
			//Agregado EMS 10/11/2015
			@DirectMethod
			public Map<String, String>actualizaPropuesta(String nvaCveControl, String noDoctos, String oldCveControl, String fecha){
				
				Map<String, String> resultado = new HashMap<>();
				resultado.put("error", "");
				resultado.put("mensaje", "");
				
				
				try{
					if(!Utilerias.haveSession(WebContextManager.get()) && !Utilerias.tienePermiso(WebContextManager.get(),52)){
						return resultado;
					}
					consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
					resultado = consultaPropuestasService.actualizaPropuesta(nvaCveControl, noDoctos, oldCveControl, fecha);
				}catch(Exception e){
					bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Egresos, C:ConsultaPropuestasAction, M:insertaSubPropuesta");	
				}
				return resultado;
			}
			
	
	
}
