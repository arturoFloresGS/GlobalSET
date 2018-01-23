package com.webset.set.utilerias.action;


import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.service.MantenimientoValorDivisaService;
import com.webset.set.utileriasmod.dto.MantenimientoValorDivisaDto;
import com.webset.utils.tools.Utilerias;

public class MantenimientoValorDivisaAction {
	Contexto contexto = new Contexto();
	Bitacora bitacora = new Bitacora();
	MantenimientoValorDivisaService objMantenimientoValorDivisaService;
	
	@DirectMethod
	public List<MantenimientoValorDivisaDto> llenaComboTasas(){
		List<MantenimientoValorDivisaDto> listaResultado = new ArrayList<MantenimientoValorDivisaDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 126))
			return listaResultado;
		try{			
			objMantenimientoValorDivisaService = (MantenimientoValorDivisaService)contexto.obtenerBean("objMantenimientoValorDivisaBusinessImpl");
			listaResultado = objMantenimientoValorDivisaService.llenaComboTasas();		
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoValorDivisaAction, M: llenaComboTasas");
		}	return listaResultado;
	}	
	
	@DirectMethod
	public List<MantenimientoValorDivisaDto> llenaComboDivisas(){
		List<MantenimientoValorDivisaDto> listaResultado = new ArrayList<MantenimientoValorDivisaDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 126))
			return listaResultado;
		try{
			objMantenimientoValorDivisaService = (MantenimientoValorDivisaService)contexto.obtenerBean("objMantenimientoValorDivisaBusinessImpl");
			listaResultado = objMantenimientoValorDivisaService.llenaComboDivisas();
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoValorDivisaAction, M:llenaComboDivisas");
		} return listaResultado;
	}	
	
	@DirectMethod
	public List<MantenimientoValorDivisaDto> llenaGridTasas(String fecha){
		List<MantenimientoValorDivisaDto> listaResultado = new ArrayList<MantenimientoValorDivisaDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 126))
			return listaResultado;
		try{
			objMantenimientoValorDivisaService = (MantenimientoValorDivisaService)contexto.obtenerBean("objMantenimientoValorDivisaBusinessImpl");
			listaResultado = objMantenimientoValorDivisaService.llenaGridTasas(fecha);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoValorDivisaAction, M: llenaGridTasas");
		} return listaResultado;
	}
	
	@DirectMethod
	public List<MantenimientoValorDivisaDto> llenaGridDivisas(String fecha){
		List<MantenimientoValorDivisaDto> listaResultado = new ArrayList<MantenimientoValorDivisaDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 126))
			return listaResultado;
		try{
			objMantenimientoValorDivisaService = (MantenimientoValorDivisaService)contexto.obtenerBean("objMantenimientoValorDivisaBusinessImpl");
			listaResultado = objMantenimientoValorDivisaService.llenaGridDivisas(fecha);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoValorDivisaAction, M: llenaGridDivisas");
		} return listaResultado;
	}
	
	@DirectMethod
	public String insertaActualiza(String gridTasas, String gridDivisas, String fecha){
		String cadenaRespuesta = "";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 126))
			return cadenaRespuesta;
		Gson gson = new Gson();
		List<Map<String, String>> objCamposTasas = gson.fromJson(gridTasas, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		List<Map<String, String>> objCamposDivisas = gson.fromJson(gridDivisas, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		try{
			objMantenimientoValorDivisaService = (MantenimientoValorDivisaService)contexto.obtenerBean("objMantenimientoValorDivisaBusinessImpl");
			cadenaRespuesta = objMantenimientoValorDivisaService.insertaActualiza(objCamposTasas, objCamposDivisas, fecha);
			System.out.println(cadenaRespuesta + "respuesta");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C:MantenimientoValorDivisaAction, M: insertaActualiza");
		}return cadenaRespuesta;
	}		
}
