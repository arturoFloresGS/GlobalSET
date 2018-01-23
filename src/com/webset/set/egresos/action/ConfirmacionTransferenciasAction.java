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
import com.webset.set.egresos.service.ConfirmacionTransferenciasService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.utils.tools.Utilerias;

public class ConfirmacionTransferenciasAction {
	Bitacora bitacora = new Bitacora();
	Contexto contexto = new  Contexto();
	Funciones funciones = new Funciones();
	
	@DirectMethod
	public List<LlenaComboEmpresasDto> obtenerEmpresas(int idUsuario) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<LlenaComboEmpresasDto> list = new ArrayList<LlenaComboEmpresasDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionTransferenciasService confirmacionTransferenciasService = (ConfirmacionTransferenciasService)contexto.obtenerBean("confirmacionTransferenciasBusinessImpl");
			list = confirmacionTransferenciasService.obtenerEmpresas(idUsuario);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionTransferenciasAction, M:obtenerEmpresas");
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> llenaComboBanco(int noEmpresa) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionTransferenciasService confirmacionTransferenciasService = (ConfirmacionTransferenciasService)contexto.obtenerBean("confirmacionTransferenciasBusinessImpl");
			list = confirmacionTransferenciasService.llenaComboBanco(noEmpresa);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionTransferenciasAction, M:llenaComboBanco");
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboChequeraDto> llenaComboChequera(int idBanco, int noEmpresa, String idDivisa) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<LlenaComboChequeraDto> list = new ArrayList<LlenaComboChequeraDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionTransferenciasService confirmacionTransferenciasService = (ConfirmacionTransferenciasService)contexto.obtenerBean("confirmacionTransferenciasBusinessImpl");
			list = confirmacionTransferenciasService.llenaComboChequera(idBanco, noEmpresa, idDivisa);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionTransferenciasAction, M:llenaComboChequera");
		}
		return list;
	}
	
	@DirectMethod
	public List<MovimientoDto> consultarMovimiento(int noEmpresa, int idBanco, String idChequera, int hayBanca, int idUsuario, String idDivisa) {
		List<MovimientoDto> list = new ArrayList<MovimientoDto>();
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionTransferenciasService confirmacionTransferenciasService = (ConfirmacionTransferenciasService)contexto.obtenerBean("confirmacionTransferenciasBusinessImpl");
			list = confirmacionTransferenciasService.consultarMovimiento(noEmpresa, idBanco, idChequera, hayBanca, idUsuario, idDivisa);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionTransferenciasAction, M:consultarMovimiento");	
		}
		return list;
	}
	
	@DirectMethod
	public Map<String, Object> ejecutarConfirmacion(String datos, boolean bAutomatico, boolean chkAutomatico, int iTieneBanca) {
		Gson gson = new Gson();
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<Map<String, String>> objParams = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		Map<String,Object> mapRetorno = new HashMap<String,Object>();
		List<MovimientoDto> list = new ArrayList<MovimientoDto>();
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			ConfirmacionTransferenciasService confirmacionTransferenciasService = (ConfirmacionTransferenciasService)contexto.obtenerBean("confirmacionTransferenciasBusinessImpl");
			
			for(int i=0; i<objParams.size(); i++) {
				MovimientoDto dtoGrid= new MovimientoDto();
				dtoGrid.setFolioRef(objParams.get(i).get("folioRef") != null ? Integer.parseInt(objParams.get(i).get("folioRef")) : 0);
				dtoGrid.setFecValor(funciones.ponerFechaDate(objParams.get(i).get("fecValor")));
				dtoGrid.setNoEmpresa(objParams.get(i).get("noEmpresa") != null ? Integer.parseInt(objParams.get(i).get("noEmpresa")) : 0);
				dtoGrid.setIdBanco(objParams.get(i).get("idBanco") != null ? Integer.parseInt(objParams.get(i).get("idBanco")) : 0);
				dtoGrid.setIdChequera(objParams.get(i).get("idChequera"));
				dtoGrid.setImporte(objParams.get(i).get("importe") != null ? Double.parseDouble(objParams.get(i).get("importe")) : 0);
				dtoGrid.setNoFolioDet(objParams.get(i).get("noFolioDet") != null ? Integer.parseInt(objParams.get(i).get("noFolioDet")) : 0);
				dtoGrid.setReferencia(objParams.get(i).get("referencia"));
				
				list.add(dtoGrid);
			}
			mapRetorno = confirmacionTransferenciasService.ejecutarConfirmacion(list, bAutomatico, chkAutomatico, iTieneBanca);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionTransferenciasAction, M:ejecutarConfirmacion");	
		}
		return mapRetorno;
	}
}
