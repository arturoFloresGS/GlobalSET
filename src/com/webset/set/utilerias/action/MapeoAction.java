package com.webset.set.utilerias.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.dto.MapeoDto;
import com.webset.set.utilerias.service.MapeoService;
import com.webset.utils.tools.Utilerias;

public class MapeoAction {
	
	Contexto contexto = new Contexto();
	Bitacora bitacora = new Bitacora();
	MapeoService objMapeoService;
	
	//['poliza','grupoRubro', 'rubro', 'banco', 'referencia',  'concepto', 'descripcion', 'observacion', 'tipo', 'especial', 'activo']
	@DirectMethod
	public List<MapeoDto> llenaGrid(){
		System.out.println("entra grid");
		List<MapeoDto> listaResultado = new ArrayList<MapeoDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),193)) {
				objMapeoService = (MapeoService)contexto.obtenerBean("objMapeoBusinessImpl");
				listaResultado = objMapeoService.llenaGrid( );
			}
			
		} 
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoAction, M: llenaGrid");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<MapeoDto> llenaPoliza(){
		List<MapeoDto> listaResultado = new ArrayList<MapeoDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),193)) {
			objMapeoService = (MapeoService)contexto.obtenerBean("objMapeoBusinessImpl");
			listaResultado = objMapeoService.llenaPoliza();
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoAction, M: llenaPoliza");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<MapeoDto> llenaGrupo(String poliza){
		List<MapeoDto> listaResultado = new ArrayList<MapeoDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),193)) {
			objMapeoService = (MapeoService)contexto.obtenerBean("objMapeoBusinessImpl");
			listaResultado = objMapeoService.llenaGrupo(poliza);
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoAction, M: llenaGrupo");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<MapeoDto> llenaRubro(String poliza){
		System.out.println("llego a action");
		List<MapeoDto> listaResultado = new ArrayList<MapeoDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),193)) {
			objMapeoService = (MapeoService)contexto.obtenerBean("objMapeoBusinessImpl");
			listaResultado = objMapeoService.llenaRubro(poliza);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoAction, M: llenaRubro");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<MapeoDto> llenaBanco(){
		List<MapeoDto> listaResultado = new ArrayList<MapeoDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),193)) {
			objMapeoService = (MapeoService)contexto.obtenerBean("objMapeoBusinessImpl");
			listaResultado = objMapeoService.llenaBanco();
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoAction, M: llenaBanco");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<MapeoDto> obtenerChequerasM(String idBanco, String secuencia){
		List<MapeoDto> chequeras= new ArrayList<MapeoDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),193)) {
			objMapeoService = (MapeoService)contexto.obtenerBean("objMapeoBusinessImpl");
			chequeras = objMapeoService.obtenerChequerasM(idBanco, secuencia);
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoAction, M: obtenerChequerasM");

		}
		
		return chequeras;
	}
	
	@DirectMethod
	public void agregarTodasM(String idBanco, String secuencia){
		System.out.println("Entro a action agregartodas");
		try {
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),193)) {
				objMapeoService = (MapeoService)contexto.obtenerBean("objMapeoBusinessImpl");
				objMapeoService.agregarTodasM(idBanco, secuencia);
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoAction, M: agregarTodasM");

		}
		
		
	}
	
	@DirectMethod
	public void quitarTodasM(String secuencia){
		System.out.println("Entro a action agregartodas");
		try {
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),193)) {
			objMapeoService = (MapeoService)contexto.obtenerBean("objMapeoBusinessImpl");
			objMapeoService.quitarTodasM(secuencia);
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoAction, M: quitarTodasM");

		}
		
		
	}
	
	
	
	@DirectMethod
	public List<MapeoDto> obtenerChequeras(String idBanco, int secuencia){
		System.out.println("Entrando a obtenerChequeras mapeo Action");
		List<MapeoDto> chequeras= new ArrayList<MapeoDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),193)) {
			objMapeoService = (MapeoService)contexto.obtenerBean("objMapeoBusinessImpl");
			chequeras = objMapeoService.obtenerChequeras(idBanco, secuencia);
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoAction, M: obtenerChequeras");

		}
		
		return chequeras;
	}
	
	@DirectMethod
	public List<MapeoDto> obtenerChequerasAV(String idBanco, int secuencia){
		System.out.println("action");
		List<MapeoDto> chequeras= new ArrayList<MapeoDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),193)) {
			objMapeoService = (MapeoService)contexto.obtenerBean("objMapeoBusinessImpl");
			chequeras = objMapeoService.obtenerChequerasAV(idBanco, secuencia);
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoAction, M: obtenerChequerasAV");

		}
		
		return chequeras;
	}
	
	
	@DirectMethod
	public HSSFWorkbook reporteMapeo(ServletContext context){
		HSSFWorkbook wb=null;
		try {
			
			objMapeoService = (MapeoService)contexto.obtenerBean("objMapeoBusinessImpl",context);
			wb=objMapeoService.reporteMapeo();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoAction, M: reporteMapeo");
		}return wb;
	}
	
	@DirectMethod
	public String retornaIdMapeo(String poliza,String grupo, String rubro,String banco){
		String retorno="";
		try {
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),193)) {
				objMapeoService = (MapeoService)contexto.obtenerBean("objMapeoBusinessImpl");
				MapeoDto objMapeoDto=new MapeoDto();
				objMapeoDto.setIdPoliza((poliza));
				objMapeoDto.setIdGrupo((grupo));
				objMapeoDto.setIdRubro((rubro));
				objMapeoDto.setIdBanco((banco));
				//retorno = objMapeoService.obtenerChequeras();
				
				retorno = objMapeoService.retornaIdMapeo(objMapeoDto);
			}
			
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoAction, M: retornaIdMapeo");
		}return retorno;
	}
	
	
	
	
	
	@DirectMethod
	public boolean asignarChequera(String claves)
	{
		try {
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),193)) {
			int ind=0, idBanco=0, idChequera=0, chequeras=0;
			boolean todos=false;
			
			objMapeoService = (MapeoService)contexto.obtenerBean("objMapeoBusinessImpl");
		ind=claves.indexOf(",");
		if(ind>0){
			idBanco=Integer.parseInt(claves.substring(0,ind));
			idChequera=Integer.parseInt(claves.substring(ind+1));
			todos=false;
			}
		else{
			idBanco=Integer.parseInt(claves.substring(0));
			todos=true;
		}
		
		chequeras = objMapeoService.asignarChequeras(idBanco, idChequera, todos);
		
		return chequeras>0;
			}else{
				return false;
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:MapeoAction, M:asignarChequera");
			return false;
		}
	}
	
	@DirectMethod
	public List<MapeoDto> obtieneDatos (String idPoliza, String idGrupo, String idRubro,String idBanco){
		System.out.println("entro");
		List<MapeoDto> listaResultado = new ArrayList<MapeoDto>();		
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),193)) {
			System.out.println("Llega al action de obtiene");
			objMapeoService = (MapeoService)contexto.obtenerBean("objMapeoBusinessImpl");
			listaResultado = objMapeoService.obtieneDatos(idPoliza, idGrupo, idRubro,idBanco);
			System.out.println(listaResultado.size() + " tama\u00f1o obtiene");
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoAction, M: obtieneDatos");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<MapeoDto> verificaRegistro(int secuencia){
		List<MapeoDto> listaResultado = new ArrayList<MapeoDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),193)) {
			objMapeoService = (MapeoService)contexto.obtenerBean("objMapeoBusinessImpl");
			listaResultado = objMapeoService.verificaRegistro(secuencia);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoAction, M: verificaRegistro");
		}return listaResultado;
	}
	
	@DirectMethod
	public String inhabilitaMapeo(int secuencia){
		String mensaje = "";
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),193)) {
			objMapeoService = (MapeoService)contexto.obtenerBean("objMapeoBusinessImpl");
			mensaje = objMapeoService.inhabilitaMapeo(secuencia);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoAction,  M: inhabilitaMapeo");
		}return mensaje;
	}
	
	@DirectMethod
	public String eliminaChequera(int secuencia){
		String mensaje = "";
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),193)) {
			objMapeoService = (MapeoService)contexto.obtenerBean("objMapeoBusinessImpl");
			mensaje = objMapeoService.eliminaChequera(secuencia);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoAction,  M: eliminaChequera");
		}return mensaje;
	}
	
	@DirectMethod
	public String actualizaMapeo (String registro, String idSecuencia){
		System.out.println("Entra a actualiza Mapeo");
		String mensaje = "";
		Gson gson = new Gson();
		List<Map<String, String>> registroGson = gson.fromJson(registro, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
//		List<Map<String, String>> chequerasGson = gson.fromJson(chequeras, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),193)) {
			objMapeoService = (MapeoService)contexto.obtenerBean("objMapeoBusinessImpl");
			mensaje = objMapeoService.actualizaMapeo(registroGson, idSecuencia);
			}
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoAction, M: actualizaMapeo");
		}
		return mensaje;
	}
	
	@DirectMethod
	public String insertaMapeo (String registro, String chequeras, String idSecuencia){
		System.out.println("Entra a insertaMapeo action");
		String mensaje = "";
		Gson gson = new Gson();
		List<Map<String, String>> registroGson = gson.fromJson(registro, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		List<Map<String, String>> chequerasGson = gson.fromJson(chequeras, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),193)) {
			objMapeoService = (MapeoService)contexto.obtenerBean("objMapeoBusinessImpl");
			mensaje = objMapeoService.insertaMapeo(registroGson, chequerasGson, idSecuencia);
			}
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoAction, M: insertaMapeo");
		}
		return mensaje;
	}
	
	@DirectMethod
	public void agregarChequerasAV (int secuencia, String chequeras, String banco){
		
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),193)) {
			objMapeoService = (MapeoService)contexto.obtenerBean("objMapeoBusinessImpl");
			objMapeoService.agregarChequerasAV(secuencia, chequeras, banco);
			}
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoAction, M: agregarChequerasAV");
		}
		
	}
	
	@DirectMethod
	public void eliminarChequerasAV (int secuencia, String chequeras, String banco){
		
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),193)) {
			objMapeoService = (MapeoService)contexto.obtenerBean("objMapeoBusinessImpl");
			objMapeoService.eliminarChequerasAV(secuencia, chequeras, banco);
			}
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoAction, M: eliminarChequerasAV");
		}
		
	}
	
	
	
}
