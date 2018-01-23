package com.webset.set.bancaelectronica.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.bancaelectronica.business.MovimientosBancaEBusiness;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.ComunDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;
/**
 * @author Jessica Arelly Cruz Cruz
 * @since 01/03/2011
 */
public class MovimientosBancaEAction {
	Contexto contexto=new Contexto();
	Bitacora bitacora=new Bitacora();
	Funciones funciones = new Funciones();
	MovimientosBancaEBusiness movimientosBancaEBusiness = new MovimientosBancaEBusiness();
	
	@DirectMethod
	public String obtenerFechaHoy(){
		if (!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 36)) 
			return null;
		String result = "";
		try{
			if (Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 36)) {
			movimientosBancaEBusiness = (MovimientosBancaEBusiness)contexto.obtenerBean("movimientosBancaEBusiness");
			result = movimientosBancaEBusiness.obtenerFechaHoy();
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:MovimientosBancaEAction, M:obtenerFechaHoy");
		}
		return result;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboBancos(int noEmpresa){
		if (!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 36)) 
			return null;
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 36)) {
			movimientosBancaEBusiness = (MovimientosBancaEBusiness)contexto.obtenerBean("movimientosBancaEBusiness");
			list=movimientosBancaEBusiness.llenarComboBancos(noEmpresa);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:PropuestaPagoManualAction, M:llenarComboGrupo");
		}
		return list;
	}
	
	@DirectMethod
	public List<ComunDto>obtenerConceptos(int noBanco){
		if (!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 36)) 
			return null;
		List<ComunDto> list= new ArrayList<ComunDto>();
		boolean lbGenerico = false;
		try{
			if (Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 36)) {
			movimientosBancaEBusiness = (MovimientosBancaEBusiness)contexto.obtenerBean("movimientosBancaEBusiness");
			list=movimientosBancaEBusiness.obtenerConceptos(lbGenerico, noBanco);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:PropuestaPagoManualAction, M:obtenerConceptos");
		}
		return list;
	}

	@DirectMethod
	public List<LlenaComboGralDto>llenarComboChequeras(int noBanco, int noEmpresa){
		if (!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 36)) 
			return null;
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 36)) {
			movimientosBancaEBusiness = (MovimientosBancaEBusiness)contexto.obtenerBean("movimientosBancaEBusiness");
			list=movimientosBancaEBusiness.llenarComboChequeras(noBanco, noEmpresa);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:PropuestaPagoManualAction, M:llenarComboChequeras");
		}
		return list;
	}
	
}
