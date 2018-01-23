package com.webset.set.bancaelectronica.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.bancaelectronica.dto.ArchTransferDto;
import com.webset.set.bancaelectronica.service.ControlArchivosTransferidosService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.utils.tools.Utilerias;

public class ControlArchivosTransferidosAction {
	Contexto contexto = new Contexto();
	Bitacora bitacora = new Bitacora();
	ControlArchivosTransferidosService controlArchivosTransferidosService;
	
	@DirectMethod
	public List<ArchTransferDto> llenaComboArchivos(String fecValor, String bChequeOcurre){
		List<ArchTransferDto> listaResultado = new ArrayList<ArchTransferDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 35))
			return listaResultado;
		try{
			if (Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 35)) {
			controlArchivosTransferidosService = (ControlArchivosTransferidosService)contexto.obtenerBean("controlArchivosTransferidosBusiness");
			listaResultado = controlArchivosTransferidosService.llenaComboArchivos(fecValor, bChequeOcurre);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosAction, M: llenaComboArchivos");
		}return listaResultado;
	}	
	
	@DirectMethod
	public String validaCriterios(int optTodas, int optCriterios, String fechaArchivo, String fecInicial, String fecFinal, String nomArchivo, String noDocto){
		String mensaje = "";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 35))
			return mensaje;
		try{
			if (Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 35)) {
			controlArchivosTransferidosService = (ControlArchivosTransferidosService)contexto.obtenerBean("controlArchivosTransferidosBusiness");
			mensaje = controlArchivosTransferidosService.validaCriterios(optTodas, optCriterios, fechaArchivo, fecInicial, fecFinal, nomArchivo, noDocto);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosAction, M: validaCriterios");
		}return mensaje;
	}
	
	@DirectMethod
	public List<ArchTransferDto> llenaGridArchivos(String bChequeOcurre, int optCriterios, String fecArchivo, String fecInicial, 
												   String fecFinal, String nomArchivo, String noDocto){
		System.out.println("Aqui");
		List<ArchTransferDto> listaResultado = new ArrayList<ArchTransferDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 35))
			return listaResultado;
		try{
			//if (Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 35)) {
			controlArchivosTransferidosService = (ControlArchivosTransferidosService)contexto.obtenerBean("controlArchivosTransferidosBusiness");
			System.out.println("Variables de busqueda : bChequeOcurre, optCriterios, fecArchivo, fecInicial, fecFinal,nomArchivo, noDocto :"+bChequeOcurre+"-->"+ optCriterios+"-->"+ fecArchivo+"-->"+ fecInicial+"-->"+ fecFinal+"-->"+ nomArchivo+"-->"+ noDocto);
			listaResultado = controlArchivosTransferidosService.llenaGridArchivos(bChequeOcurre, optCriterios, fecArchivo, fecInicial, fecFinal,nomArchivo, noDocto);			
		System.out.println("lista res.size"+listaResultado.size());
		//	}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosAction, M: llenaGridArchivos");
		}return listaResultado;
	}
		
	@DirectMethod
	public List<ArchTransferDto> llenaGridDetalle(String nomArchivo, int idBanco, boolean enviadasHoy, String bChequeOcurre){
		List<ArchTransferDto> listaResultado = new ArrayList<ArchTransferDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 35))
			return listaResultado;
		try{
			//if (Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 35)) {
			controlArchivosTransferidosService = (ControlArchivosTransferidosService)contexto.obtenerBean("controlArchivosTransferidosBusiness");
			listaResultado = controlArchivosTransferidosService.llenaGridDetalle(nomArchivo, idBanco, enviadasHoy, bChequeOcurre);
		//}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosAction, M: llenaGridDetalle");
		}return listaResultado;
	}
	
	@DirectMethod
	public Map<String, Object> regenerar(String registro, String registroCancelado, int registroTotal){
		Map<String, Object> mapRespuesta = new HashMap<String, Object>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 35))
			return mapRespuesta;
		Gson gson = new Gson();
		List<Map<String, String>> grid = gson.fromJson(registro, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<Map<String, String>> registroCancela = gson.fromJson(registroCancelado, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		try{
			System.out.println("archivo a regenerar action"+grid.get(0).get("nomArchivo"));
			controlArchivosTransferidosService = (ControlArchivosTransferidosService)contexto.obtenerBean("controlArchivosTransferidosBusiness");
			mapRespuesta = controlArchivosTransferidosService.regenerar(grid, registroCancela, registroTotal);
		
		}
		catch(Exception e){
			System.out.println("Error");
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosAction, M: ejecutar");
		}return mapRespuesta;
	}
	
	@DirectMethod
	public String configuraSet(int indice){
		String mensaje = "";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 35))
			return mensaje;
		try{
			if (Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 35)) {
			controlArchivosTransferidosService = (ControlArchivosTransferidosService)contexto.obtenerBean("controlArchivosTransferidosBusiness");
			mensaje = controlArchivosTransferidosService.configuraSet(indice);
		}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosAction, M: configuraSet");
		}return mensaje;
	}
}
