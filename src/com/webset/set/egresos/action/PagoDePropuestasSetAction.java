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
import com.webset.set.egresos.service.PagoDePropuestasSetService;
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
 * @author	Alberto Antonio G.
 * @version	1.0
 * @since	25/Febrero/2016	
 */

public class PagoDePropuestasSetAction {
	private Bitacora bitacora = new Bitacora();
	private Contexto contexto = new  Contexto();
	private Funciones funciones = new Funciones();
	ConsultaPropuestasService consultaPropuestasService;
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboGrupoEmpresas(int idUsuario){
		GrupoEmpresasDto dtoIn= new GrupoEmpresasDto();
		List<LlenaComboGralDto> listRet= new ArrayList<LlenaComboGralDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),205)){
				PagoDePropuestasSetService PagoDePropuestasSetService = (PagoDePropuestasSetService)contexto.obtenerBean("pagoDePropuestasSetBusinessImpl");
				dtoIn.setIdUsuario("" + idUsuario);
				listRet=PagoDePropuestasSetService.llenarComboGrupoEmpresa(dtoIn);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Egresos, C:PagoDePropuestasSetAction, M:llenarComboGrupoEmpresas");	
		}
		return listRet;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboDivXEmp(int idUsuario){
		List<LlenaComboGralDto> listRet= new ArrayList<LlenaComboGralDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),205)){
				PagoDePropuestasSetService PagoDePropuestasSetService =(PagoDePropuestasSetService)contexto.obtenerBean("pagoDePropuestasSetBusinessImpl");
				listRet=PagoDePropuestasSetService.llenarComboDivXEmp(idUsuario);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Egresos, C:PagoDePropuestasSetAction, M:llenarComboDivXEmp");	
		}
		return listRet;
	}
	/*Revisado Alberto A.G.*/
	@DirectMethod
	public SaldosChequerasDto obtenerSaldosChequeras(int idGrupoEmpresa, 
			int idDivision, int idUsuario){
		SaldosChequerasDto dto= new SaldosChequerasDto();		
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),205)){
				PagoDePropuestasSetService PagoDePropuestasSetService=(PagoDePropuestasSetService)contexto.obtenerBean("pagoDePropuestasSetBusinessImpl");
				dto.setIdGrupo(idGrupoEmpresa>0?idGrupoEmpresa:0);
				dto.setPsDivision(idDivision>0?""+idDivision:"");
				dto.setIdUsuario(idUsuario>0?idUsuario:0);
				dto=PagoDePropuestasSetService.obtenerSaldosChequeras(dto);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Egresos, C:PagoDePropuestasSetAction, M:obtenerSaldosChequeras");	
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
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),205)){
				PagoDePropuestasSetService PagoDePropuestasSetService=(PagoDePropuestasSetService)contexto.obtenerBean("pagoDePropuestasSetBusinessImpl");
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
				
				listCons = PagoDePropuestasSetService.consultarPropuestas(dtoIn);
				
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Egresos, C:PagoDePropuestasSetAction, M:consultarPropuestas");	
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
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),205)){
				PagoDePropuestasSetService PagoDePropuestasSetService = (PagoDePropuestasSetService)contexto.obtenerBean("pagoDePropuestasSetBusinessImpl");
				
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
				listRetorno = PagoDePropuestasSetService.validarCpaVtaTransfer(listGrid);
			}
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Egresos, C:PagoDePropuestasSetAction, M:validarCpaVtaTransfer");
		}
		return listRetorno;  
	}
	
	@DirectMethod
	public Map<String, Object> insetarZexpFact (String datos, String fecHoy, 
			int idUsuario){
		Map<String, Object>mapRetorno = new HashMap<String, Object>();
		mapRetorno.put("msgError", "Error desconocido");
		mapRetorno.put("estatus", false);
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),205)){
				PagoDePropuestasSetService PagoDePropuestasSetService = (PagoDePropuestasSetService)
						contexto.obtenerBean("pagoDePropuestasSetBusinessImpl");
				mapRetorno= PagoDePropuestasSetService.insertarFoliosZexpFact(datos, fecHoy, idUsuario);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:PagoDePropuestasSetAction, M:insetarZexpFact");
		}
		return mapRetorno;
	}
	
	@DirectMethod
	public List<MovimientoDto>consultarDetalle(int idGrupoEmpresa, int idGrupo, String cveControl, int usr1, int usr2, int usr3){
		SeleccionAutomaticaGrupoDto dtoIn= new SeleccionAutomaticaGrupoDto();
		List<MovimientoDto>listDetalle= new ArrayList<MovimientoDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get()) && !Utilerias.tienePermiso(WebContextManager.get(),205)){
			return listDetalle;
		}
		
		try{
			//consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			PagoDePropuestasSetService PagoDePropuestasSetService = (PagoDePropuestasSetService)contexto.obtenerBean("pagoDePropuestasSetBusinessImpl");
			dtoIn.setIdGrupoFlujo(idGrupoEmpresa);
			dtoIn.setIdGrupo(idGrupo);
			dtoIn.setCveControl(cveControl);
			dtoIn.setUsuarioUno(usr1);
			dtoIn.setUsuarioDos(usr2);
			dtoIn.setUsuarioTres(usr3);
			listDetalle = PagoDePropuestasSetService.consultarDetalle(dtoIn);
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
		
		if(!Utilerias.haveSession(WebContextManager.get()) && !Utilerias.tienePermiso(WebContextManager.get(),205)){
			
			return listDetalle;
		}
		
		try{
			
			PagoDePropuestasSetService PagoDePropuestasSetService = (PagoDePropuestasSetService)contexto.obtenerBean("pagoDePropuestasSetBusinessImpl");
			dtoIn.setIdGrupoFlujo(idGrupoEmpresa);
			dtoIn.setIdGrupo(idGrupo);
			dtoIn.setCveControl(cveControl);
			dtoIn.setUsuarioUno(usr1);
			dtoIn.setUsuarioDos(usr2);
			dtoIn.setUsuarioTres(usr3);
			listDetalle = PagoDePropuestasSetService.consultarDetallePropuestasNoPagadas(dtoIn);
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
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),205)){
				PagoDePropuestasSetService PagoDePropuestasSetService = (PagoDePropuestasSetService)contexto.obtenerBean("pagoDePropuestasSetBusinessImpl");
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
				mapRetorno=PagoDePropuestasSetService.ejecutarPropuestas(listGrid, fecHoy);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Egresos, C:PagoDePropuestasSetAction, M:ejecutarPropuestas");
		}
		return mapRetorno;
	}
	
	@DirectMethod
	public HSSFWorkbook consultaPagosExcel(String clave, ServletContext context){
		HSSFWorkbook wb=null;
		System.out.println("clabe"+clave);
		try {
			
			PagoDePropuestasSetService PagoDePropuestasSetService = (PagoDePropuestasSetService)contexto.obtenerBean("pagoDePropuestasSetBusinessImpl",context);
			//objMantenimientoChequerasService = (MantenimientoChequerasService)contexto.obtenerBean("objMantenimientoChequerasBusinessImpl",context);
			
			wb=PagoDePropuestasSetService.consultaPagosExcel(clave);
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
	public JRDataSource obtenerReportePagoPropuestas(Map datos, ServletContext context) {
		JRDataSource jrDataSource = null;
		PagosPropuestosDto dtoIn = new PagosPropuestosDto();
		System.out.println("action reporte");
		try {
			
			PagoDePropuestasSetService PagoDePropuestasSetService = (PagoDePropuestasSetService)contexto.obtenerBean("pagoDePropuestasSetBusinessImpl", context);
			
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
			
			jrDataSource = PagoDePropuestasSetService.reportePagoPropuestas(dtoIn);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PagoDePropuestasSetAction, M:obtenerReportePagoPropuestas");	
		}
		return jrDataSource;
	}
	
	@DirectMethod
	public Map<String, String>validaCvePropuesta(String cveControl){
		Map<String, String> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		try{
			if(!Utilerias.haveSession(WebContextManager.get()) && !Utilerias.tienePermiso(WebContextManager.get(),205)){
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
					if(!Utilerias.haveSession(WebContextManager.get()) && !Utilerias.tienePermiso(WebContextManager.get(),205)){
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
					if(!Utilerias.haveSession(WebContextManager.get()) && !Utilerias.tienePermiso(WebContextManager.get(),205)){
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
					if(!Utilerias.haveSession(WebContextManager.get()) && !Utilerias.tienePermiso(WebContextManager.get(),205)){
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

