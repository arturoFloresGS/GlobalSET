package com.webset.set.impresion.action;

import java.util.Date;
import java.util.List;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.impresion.dto.MantenimientosDto;
import com.webset.set.impresion.service.ImpresorasService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.dto.CajaUsuarioDto;
import com.webset.utils.tools.Utilerias;

public class ImpresorasAction {
	Contexto contexto = new Contexto();
	Bitacora bitacora;
	ImpresorasService impresorasService = (ImpresorasService)contexto.obtenerBean("impresorasBusinessImpl");
	
	@DirectMethod
	public List<CajaUsuarioDto> llenaComboCajas() {
		if(!Utilerias.haveSession(WebContextManager.get()))
			return null;
		return impresorasService.llenaComboCajas(); 
	}
	
	@DirectMethod
	public List<MantenimientosDto> buscarImpresoras() {
		if(!Utilerias.haveSession(WebContextManager.get()))
			return null;
		return impresorasService.buscarImpresoras();
	}
	
	@DirectMethod
	public String eliminarImpre(int noImpresora) { 
		if(!Utilerias.haveSession(WebContextManager.get()))
			return null;
		return impresorasService.eliminarImpre(noImpresora); 
	}
	
	@DirectMethod
	public String insertarImpre(String noImpresora, String noCaja, String noCharola) { 
		String resp = "";
		if(!Utilerias.haveSession(WebContextManager.get()))
			return resp;
		
		try {
			if(noImpresora.equals(""))
				return "Falta capturar el numero de impresora";
			else if(noCaja.equals(""))
				return "Falta capturar el numero de caja";
			else if(noCharola.equals(""))
				return "Falta capturar el numero de charolas";
			
			resp = impresorasService.insertarImpre(Integer.parseInt(noImpresora), Integer.parseInt(noCaja), Integer.parseInt(noCharola));
			
		}catch(Exception e) {bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:ImpresorasAction, M:insertarImpre"); }
		return resp;
	}
}