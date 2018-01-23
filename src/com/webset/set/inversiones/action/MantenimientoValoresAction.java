package com.webset.set.inversiones.action;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.inversiones.dto.MantenimientoValoresDto;
import com.webset.set.inversiones.dto.LlenaComboValoresDto;
import com.webset.set.inversiones.middleware.service.InversionesValoresService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.utils.tools.Utilerias;


public class MantenimientoValoresAction {
	private Bitacora bitacora = new Bitacora();
	private Contexto contexto = new Contexto();
	private Funciones  funciones = new Funciones();
	InversionesValoresService inversionesValoresService ;
	
	@DirectMethod
	public List<MantenimientoValoresDto> consultarValores(){
		List<MantenimientoValoresDto> listConsCon = new ArrayList<MantenimientoValoresDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listConsCon;
		try{
		 inversionesValoresService  = (InversionesValoresService) contexto.obtenerBean("inversionesValoresBusinessImpl");
		    listConsCon = inversionesValoresService.consultarValores();
		}catch(Exception e){
		    bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Inversiones, C:MantenimientoValoresAction, M:consultarValores");
		}
		return listConsCon;
	}
	
	@DirectMethod
	public List<LlenaComboValoresDto> consultarDivisa(){
		List<LlenaComboValoresDto> listConsVal = new ArrayList<LlenaComboValoresDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listConsVal;
		try{
		    inversionesValoresService = (InversionesValoresService) contexto.obtenerBean("inversionesValoresBusinessImpl");
		    listConsVal = inversionesValoresService.consultarDivisa();
		}catch(Exception e){
		    bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Inversiones, C:MantenimientoValoresAction, M:consultarDivisa");
		}
		return listConsVal;
	}
	
	@DirectMethod
	public Map<String, Object> insertarModificarValores(boolean bNuevo, boolean bModifi, String datVal){
		
		Map<String, Object> mapRet = new HashMap<String, Object>();
		
		Gson gson = new Gson();
		
		if(!Utilerias.haveSession(WebContextManager.get()))
			return mapRet;
		
		try{
			
			inversionesValoresService  = (InversionesValoresService) contexto.obtenerBean("inversionesValoresBusinessImpl");
			List<Map<String, String>> gListVres = gson.fromJson(datVal, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			List< MantenimientoValoresDto> listVal = new ArrayList< MantenimientoValoresDto>();
			
			MantenimientoValoresDto dtoValores = new  MantenimientoValoresDto();
				
		    dtoValores.setIdValor     	  ( funciones.validarCadena( gListVres.get( 0 ).get( "sIdValor"    ) ) );
		    dtoValores.setIsr             ( funciones.validarCadena( gListVres.get( 0 ).get( "sIsr"        ) ) );
		    dtoValores.setDescripcion     ( funciones.validarCadena( gListVres.get( 0 ).get( "sDescripcion") ) );
		    dtoValores.setIdDivisa        ( funciones.validarCadena( gListVres.get( 0 ).get( "sIdDivisa"   ) ) );
				    
		    dtoValores.setIdGrupoOrden    ( funciones.convertirCadenaDouble( gListVres.get( 0 ).get( "idGrupoOrden"     ) ) );
		    dtoValores.setIdRubroOrden    ( funciones.convertirCadenaDouble( gListVres.get( 0 ).get( "idRubroOrden"     ) ) );
		    dtoValores.setIdGrupoRegreso  ( funciones.convertirCadenaDouble( gListVres.get( 0 ).get( "idGrupoRegreso"   ) ) );
		    dtoValores.setIdRubroRegreso  ( funciones.convertirCadenaDouble( gListVres.get( 0 ).get( "idRubroRegreso"   ) ) );
		    dtoValores.setIdGrupoIntereses( funciones.convertirCadenaDouble( gListVres.get( 0 ).get( "idGrupoIntereses" ) ) );
		    dtoValores.setIdRubroIntereses( funciones.convertirCadenaDouble( gListVres.get( 0 ).get( "idRubroIntereses" ) ) );
		    dtoValores.setIdGrupoISR      ( funciones.convertirCadenaDouble( gListVres.get( 0 ).get( "idGrupoISR"       ) ) );
		    dtoValores.setIdRubroISR      ( funciones.convertirCadenaDouble( gListVres.get( 0 ).get( "idRubroISR"       ) ) );
		    
				    
		    listVal.add(dtoValores);
		    
			if(listVal.get(0).getIdValor().equals("")) {
			    mapRet.put("msgUsuario", "Falta llenar el Id Del Valor");
				return mapRet;
			}
			
			if(listVal.get(0).getDescripcion().equals("")) {
			    mapRet.put("msgUsuario", "Falta llenar la descripción");
				return mapRet;
			}
			
			if(listVal.get(0).getIsr().equals("")) {
			    mapRet.put("msgUsuario", "Falta llenar el Id Del Valor");
				return mapRet;
			}
			
			if(listVal.get(0).getIdDivisa().equals("")) {
			    mapRet.put("msgUsuario", "Falta llenar la Divisa");
				return mapRet;
			}
			
			if( listVal.get(0).getIdGrupoOrden() == 0){
				mapRet.put("msgUsuario", "No ha seleccionado el Grupo de la Orden.");
				return mapRet;
			}
			
			if( listVal.get(0).getIdRubroOrden() == 0){
				mapRet.put("msgUsuario", "No ha seleccionado el Rubro de la Orden.");
				return mapRet;
			}
			
			if( listVal.get(0).getIdGrupoRegreso() == 0){
				mapRet.put("msgUsuario", "No ha seleccionado el Grupo del Regreso.");
				return mapRet;
			}
			
			if( listVal.get(0).getIdRubroRegreso() == 0){
				mapRet.put("msgUsuario", "No ha seleccionado el Rubro del Regreso.");
				return mapRet;
			}
			
			if( listVal.get(0).getIdGrupoIntereses() == 0){
				mapRet.put("msgUsuario", "No ha seleccionado el Grupo de los Intereses.");
				return mapRet;
			}
			
			if( listVal.get(0).getIdRubroIntereses() == 0){
				mapRet.put("msgUsuario", "No ha seleccionado el Rubro de los Intereses.");
				return mapRet;
			}
			
			if( listVal.get(0).getIdGrupoISR() == 0){
				mapRet.put("msgUsuario", "No ha seleccionado el Grupo del ISR.");
				return mapRet;
			}
			
			if( listVal.get(0).getIdRubroISR() == 0){
				mapRet.put("msgUsuario", "No ha seleccionado el Rubro del ISR.");
				return mapRet;
			}
					 
			mapRet = inversionesValoresService.insertarModificarValores(bNuevo,bModifi,listVal);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Inversiones, C:MantenimientoValoresAction, M:insertarModificarValores");
			e.printStackTrace();
		}
		return mapRet;
	}
	
	@DirectMethod
	public Map<String, Object> eliminarValores(String sIdValor){
		Map<String, Object> mapRet = new HashMap<String, Object>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return mapRet;
		try{
		    inversionesValoresService  = (InversionesValoresService) contexto.obtenerBean("inversionesValoresBusinessImpl");
			mapRet = inversionesValoresService .eliminarValores(sIdValor);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Inversiones, C:MantenimientoValoresAction, M:eliminarValores");
		}
		return mapRet;
	}

}


