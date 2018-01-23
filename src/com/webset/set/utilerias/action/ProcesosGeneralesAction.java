package com.webset.set.utilerias.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.service.ProcesosGeneralesService;
import com.webset.set.utileriasmod.dto.ProcesosGeneralesDto;
import com.webset.utils.tools.Utilerias;

public class ProcesosGeneralesAction {
	Contexto contexto = new Contexto();
	Bitacora bitacora = new Bitacora();
	ProcesosGeneralesService objProcesosGeneralesService;
	
	@DirectMethod
	public List<ProcesosGeneralesDto> llenaGrid(){
		List<ProcesosGeneralesDto> listaResultado = new ArrayList<ProcesosGeneralesDto>();
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objProcesosGeneralesService = (ProcesosGeneralesService)contexto.obtenerBean("objProcesosGeneralesBusinessImpl");
			listaResultado = objProcesosGeneralesService.llenaGrid();
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: ProcesosGeneralesAction, M: llenaGrid");
		}return listaResultado;	
	}
	
	@DirectMethod
	public String obtieneFecha(){
		String mensaje = "";
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objProcesosGeneralesService = (ProcesosGeneralesService)contexto.obtenerBean("objProcesosGeneralesBusinessImpl");
			mensaje = objProcesosGeneralesService.obtieneFecha();
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: ProcesosGeneralesAction, M: obtieneFecha");
		}return mensaje;
	}
	
	@DirectMethod
	public int validaEstatus(){
		int resultado = 0;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objProcesosGeneralesService = (ProcesosGeneralesService)contexto.obtenerBean("objProcesosGeneralesBusinessImpl");
			resultado = objProcesosGeneralesService.validaEstatus();
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: ProcesosGeneralesAction, M: validaEstatus");
		}return resultado;
	}
	
	@DirectMethod
	public int actualizaEstatusSist2(){
		int resultado = 0;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objProcesosGeneralesService = (ProcesosGeneralesService)contexto.obtenerBean("objProcesosGeneralesBusinessImpl");
			resultado = objProcesosGeneralesService.actualizaEstatusSist2();
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: ProcesosGeneralesAction, M: validaEstatusSist2");
		}return resultado;
	}
	
	@DirectMethod
	public int validaUsuariosConectados(){
		int resultado = 0;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objProcesosGeneralesService = (ProcesosGeneralesService)contexto.obtenerBean("objProcesosGeneralesBusinessImpl");
			resultado = objProcesosGeneralesService.validaUsuariosConectados();
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: ProcesosGeneralesAction, M: validaUsuariosConectados");			
		}return resultado;
	}
	
	@DirectMethod
	public String correCierre (int noUsuario, String fecHoy){
		String mensaje = "";
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objProcesosGeneralesService = (ProcesosGeneralesService)contexto.obtenerBean("objProcesosGeneralesBusinessImpl");
			mensaje = objProcesosGeneralesService.correCierre(noUsuario, fecHoy);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: ProcesosGeneralesAction, M: respaldoBD");
		}return mensaje;
	}
	
	@DirectMethod
	public String respaldoBD(int indice){
		String mensaje = "";
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objProcesosGeneralesService = (ProcesosGeneralesService)contexto.obtenerBean("objProcesosGeneralesBusinessImpl");
			mensaje = objProcesosGeneralesService.respaldoBD(indice);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: ProcesosGeneralesAction, M: configuraSet");
		}return mensaje;
	}
	
	@DirectMethod
	public int generaRespaldoBD(String fecHoy){
		int respuesta = 0;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objProcesosGeneralesService = (ProcesosGeneralesService)contexto.obtenerBean("objProcesosGeneralesBusinessImpl");
			respuesta = objProcesosGeneralesService.generaRespaldoBD(fecHoy);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: ProcesosGeneralesAction, M: generaRespaldoBD");
		}return respuesta;
	}
}
