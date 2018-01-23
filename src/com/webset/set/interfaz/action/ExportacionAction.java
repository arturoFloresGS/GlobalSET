package com.webset.set.interfaz.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.conciliacionbancoset.dto.CriteriosBusquedaDto;
import com.webset.set.interfaz.dto.GuiaContableDto;
import com.webset.set.interfaz.dto.InterfazDto;
import com.webset.set.interfaz.service.ExportacionService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.utils.tools.Utilerias;

public class ExportacionAction {
	Contexto contexto = new Contexto();
	Bitacora bitacora = new Bitacora();
	ExportacionService objExportacionService;
	
	@DirectMethod	
	public List<InterfazDto> llenaComboEmpresa(){
		List<InterfazDto> listaResultado = new ArrayList<InterfazDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listaResultado;		
		try{
			objExportacionService = (ExportacionService)contexto.obtenerBean("objExportacionBusinessImpl");
			listaResultado = objExportacionService.llenaComboEmpresa();
			
			System.out.println(listaResultado.size());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Interfaz, C: ExportacionAction, M: llenaComboEmpresa");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<InterfazDto> llenaGrid(int noEmpresa, String fecHoy, int estatus){
		List<InterfazDto> listaResultado = new ArrayList<InterfazDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listaResultado;
		try{
			objExportacionService = (ExportacionService)contexto.obtenerBean("objExportacionBusinessImpl");
			listaResultado = objExportacionService.llenaGrid(noEmpresa, fecHoy, estatus);
		}
		catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Interfaz, C: ExportacionAction, M: llenaGrid");
		}return listaResultado;
	}
	
	@DirectMethod
	public String exportaRegistros(String folios, int noEmpresa){
		String mensaje = "";
		if(!Utilerias.haveSession(WebContextManager.get()))
			return mensaje;
		try{
			objExportacionService = (ExportacionService)contexto.obtenerBean("objExportacionBusinessImpl");
			mensaje = objExportacionService.exportaRegistros(folios, noEmpresa);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Interfaz, C: ExportacionAction, M: exportacionRegistros");
		}return mensaje;
	}
	
	@DirectMethod
	public List<GuiaContableDto> llenaComboCuentas( String noCuenta,String idTipoMovto ){
		List<GuiaContableDto> list = new ArrayList<GuiaContableDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return list;
		try {
			objExportacionService = (ExportacionService)contexto.obtenerBean("objExportacionBusinessImpl");
			list=objExportacionService.llenaComboCuentas(noCuenta,idTipoMovto);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Interfaz, C: ExportacionAction, M: llenaComboCuentas");
		}
		
		return list;

	}
	
	
	@DirectMethod
	public Map<String, Object> updateMovimientoSET(String sRegistros, String sCriterios){
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<GuiaContableDto> listMovs = new ArrayList<GuiaContableDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return mapResult;
		Gson gson = new Gson();
		
		System.out.println( sRegistros );
		
		List<Map<String, String>> paramsGrid = gson.fromJson(sRegistros, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<Map<String, String>> paramsCriterio = gson.fromJson(sCriterios, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
		try{
			objExportacionService = (ExportacionService)contexto.obtenerBean("objExportacionBusinessImpl"); 
			
			for(int i = 0; i < paramsGrid.size(); i++){
				
				GuiaContableDto dto = new GuiaContableDto();				
				dto.setNoFolioDet( paramsGrid.get(i).get("noFolioDet")  );				
				listMovs.add(dto);
				
			}
			
			CriteriosBusquedaDto dtoParams = new CriteriosBusquedaDto(); 
			
			dtoParams.setCuentaContable(paramsCriterio.get(0).get("idCuentaContable"));
			dtoParams.setIdGrupo(paramsCriterio.get(0).get("idGrupo"));
			dtoParams.setIdRubro(paramsCriterio.get(0).get("idRubro"));
			
			mapResult = objExportacionService.updateMovimientoSET(listMovs, dtoParams);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:ConciliacionBancoSet, C:ConciliacionBancoSetAction, M:crearMovimientoSET");
		}
		return mapResult;
	}
	
	@DirectMethod
	public List<InterfazDto> llenaGridCXC(int noEmpresa, String fecHoy, int estatus){
		List<InterfazDto> listaResultado = new ArrayList<InterfazDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listaResultado;
		try{
			objExportacionService = (ExportacionService)contexto.obtenerBean("objExportacionBusinessImpl");
			listaResultado = objExportacionService.llenaGridCXC(noEmpresa, fecHoy, estatus);
		}
		catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Interfaz, C: ExportacionAction, M: llenaGrid");
		}return listaResultado;
	}
	
	@DirectMethod
	public String exportaRegistrosCXC(String folios){
		String mensaje = "";
		if(!Utilerias.haveSession(WebContextManager.get()))
			return mensaje;
		try{
			objExportacionService = (ExportacionService)contexto.obtenerBean("objExportacionBusinessImpl");
			mensaje = objExportacionService.exportaRegistrosCXC(folios);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Interfaz, C: ExportacionAction, M: exportacionRegistros");
		}return mensaje;
	}
}
