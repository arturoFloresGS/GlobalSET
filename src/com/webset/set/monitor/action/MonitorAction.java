package com.webset.set.monitor.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.webset.set.monitor.service.MonitorService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;

public class MonitorAction {
	Contexto contexto = new Contexto();
	Bitacora bitacora = new Bitacora();
	MonitorService objMonitorService ;
	
	/********
	 * JSON con informacion para Arboles.
	 * @param divisa
	 * @param opcion : tipo de arbol
	 * @return String
	 */
	
	@DirectMethod
	public String obtenerArbol (String divisa , String opcion){
		String resultado = "{}";
		//if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 189))
		//return resultado;
		try{
			objMonitorService = (MonitorService)contexto.obtenerBean("objMonitorService");
			resultado = objMonitorService.obtenerArbol(divisa, opcion);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Monitor, C:MonitorAction, M:obtenerArbolConciliaciones");
		} 
		return resultado;
	}
	
	/***************************
	 * GRAFICAS
	 */

	@DirectMethod
	public List<Map<String, String>> consultaGrafica (String tipoGrafica, String divisa){
		List<Map<String, String>> resultado= new ArrayList<Map<String, String>>();
		//if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 189))
		//return resultado;
		try{
			objMonitorService = (MonitorService)contexto.obtenerBean("objMonitorService");
			resultado = objMonitorService.datosGrafica(tipoGrafica, divisa);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Monitor, C:MonitorAction, M:graficaPoscionCaja");
		} 
		return resultado;
	}
	
	/****
	 * Diversos
	 * @param divisa
	 * @return 
	 */
	
	@DirectMethod
	public List<Map<String, String>> consultaGridDiversos (String divisa){
		List<Map<String, String>> resultado= new ArrayList<Map<String, String>>();
		//if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 189))
		//return resultado;
		try{
			objMonitorService = (MonitorService)contexto.obtenerBean("objMonitorService");
			resultado = objMonitorService.consultaGridDiversos( divisa);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Monitor, C:MonitorAction, M:consultaGridDiversos");
		} 
		return resultado;
	}
	
	@DirectMethod
	public String  consultaIndicadoresBancarios (){
		String resultado= "";
		//if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 189))
		//return resultado;
		try{
			objMonitorService = (MonitorService)contexto.obtenerBean("objMonitorService");
			resultado = objMonitorService.consultaIndicadoresBancarios();
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Monitor, C:MonitorAction, M:consultaIndicadoresBancarios");
		} 
		System.out.println(resultado);
		return resultado;
	}
}