package com.webset.set.impresion.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.impresion.service.AsignacionFirmasService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;

/**
* YEC
* 06 de enero del 2014
*/
public class AsignacionFirmasAction {
	Contexto contexto=new Contexto();
	Bitacora bitacora=new Bitacora();
	AsignacionFirmasService objAsignacionFirmasService;
	
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboBancos(){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return list;
		try{
			objAsignacionFirmasService = (AsignacionFirmasService)contexto.obtenerBean("objAsignacionFirmasBusiness");
			list=objAsignacionFirmasService.llenarComboBancos();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Impresion, C:AsignacionFirmasAction, M:llenarComboBancos");
		}return list;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboCuentas(String idBanco){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return list;
		try{
			objAsignacionFirmasService = (AsignacionFirmasService)contexto.obtenerBean("objAsignacionFirmasBusiness");
			list=objAsignacionFirmasService.llenarComboCuentas(idBanco=idBanco!=null ? idBanco:"");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Impresion, C:AsignacionFirmasAction, M:llenarComboCuentas");
		}return list;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboFirmantes(String tipo,String idBanco, String cuenta ){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return list;
		try{
			objAsignacionFirmasService = (AsignacionFirmasService)contexto.obtenerBean("objAsignacionFirmasBusiness");
			tipo=tipo!=null ? tipo:""; 
			idBanco=idBanco!=null ? idBanco:""; 
			cuenta=cuenta!=null ? cuenta:""; 
			list=objAsignacionFirmasService.llenarComboFirmantes(tipo,idBanco,cuenta);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Impresion, C:AsignacionFirmasAction, M:llenarComboFirmantes");
		}return list;
	}
	
	@DirectMethod
	public String updateFirmanteDeterminado(String idBanco, String cuenta,String idPersonaA, String idPersonaB){
		String resultado="";
		if(!Utilerias.haveSession(WebContextManager.get()))
			return resultado;
		try{
			objAsignacionFirmasService = (AsignacionFirmasService)contexto.obtenerBean("objAsignacionFirmasBusiness");
			idPersonaA=idPersonaA!=null ? idPersonaA:"";
			idPersonaB=idPersonaB!=null ? idPersonaB:"";
			idBanco=idBanco!=null ? idBanco:""; 
			cuenta=cuenta!=null ? cuenta:"";
			System.out.println(idPersonaA+"--"+idPersonaB);
			resultado = objAsignacionFirmasService.updateFirmanteDeterminado(idBanco,cuenta,idPersonaA,idPersonaB);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:AsignacionFirmasAction, M: updateFirmanteDeterminado");
		} return resultado;	
	}

}