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
import com.webset.set.utilerias.service.GrupoEmpresasService;
import com.webset.set.utileriasmod.dto.GrupoEmpresasDto;
import com.webset.utils.tools.Utilerias;

public class GrupoEmpresasAction {
	Contexto contexto = new Contexto();
	Bitacora bitacora = new Bitacora();
	GrupoEmpresasService objGrupoEmpresasService;
	
	@DirectMethod
	public List<GrupoEmpresasDto> llenaComboGrupo(){
		List<GrupoEmpresasDto> listaResultado = new ArrayList<GrupoEmpresasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objGrupoEmpresasService = (GrupoEmpresasService)contexto.obtenerBean("objGrupoEmpresasBusinessImpl");
			listaResultado = objGrupoEmpresasService.llenaComboGrupo();
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: GrupoEmpresasAction, M: llenaComboGrupo");
		}return listaResultado;
	}
	
	@DirectMethod
	public HSSFWorkbook reporteGrupoEmpresas(ServletContext context){
		HSSFWorkbook wb=null;
		try {
			
			objGrupoEmpresasService = (GrupoEmpresasService)contexto.obtenerBean("objGrupoEmpresasBusinessImpl",context);
			wb=objGrupoEmpresasService.reporteGrupoEmpresas();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: GrupoEmpresasAction, M: reporteGrupoEmpresas");
		}return wb;
	}
	
	@DirectMethod
	public String configuraSet(int indice){
		String resultadoCadena = "";
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objGrupoEmpresasService = (GrupoEmpresasService)contexto.obtenerBean("objGrupoEmpresasBusinessImpl");
			resultadoCadena = objGrupoEmpresasService.configuraSet(indice);
			}
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: GrupoEmpresasAction, M: configuraSet");
		}return resultadoCadena;
	}
	
	@DirectMethod
	public List<GrupoEmpresasDto> llenaComboEmpresa (){
		List<GrupoEmpresasDto> listaResultado = new ArrayList<GrupoEmpresasDto>();
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objGrupoEmpresasService = (GrupoEmpresasService)contexto.obtenerBean("objGrupoEmpresasBusinessImpl");
			listaResultado = objGrupoEmpresasService.llenaComboEmpresa();
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: GrupoEmpresasAction, M: llenaComboEmpresa");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<GrupoEmpresasDto> llenaGrid(int nomEmpresa, int descGrupo, int todo){
		List<GrupoEmpresasDto> listaResultado = new ArrayList<GrupoEmpresasDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			System.out.println("Llega al action");
			objGrupoEmpresasService = (GrupoEmpresasService)contexto.obtenerBean("objGrupoEmpresasBusinessImpl");
			listaResultado = objGrupoEmpresasService.llenaGrid(nomEmpresa, descGrupo, todo);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: GrupoEmpresasAction, M: llenaGrid");
		}return listaResultado;
	}
	
	@DirectMethod
	public String insertaRegistro(String registro){
		String mensaje = "";
		Gson gson = new Gson();
		List<Map<String, String>> objRegistro = gson.fromJson(registro, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			System.out.println("Llega al action del insert");
			objGrupoEmpresasService = (GrupoEmpresasService)contexto.obtenerBean("objGrupoEmpresasBusinessImpl");
			mensaje = objGrupoEmpresasService.insertaRegistro(objRegistro);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: GrupoEmpresasAction, M: insertaRegistro");
		}return mensaje;
	}
	
	@DirectMethod
	public int eliminaRegistro(int idGrupo, int noEmpresa){
		int resultado = 0;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objGrupoEmpresasService = (GrupoEmpresasService)contexto.obtenerBean("objGrupoEmpresasBusinessImpl");
			resultado = objGrupoEmpresasService.eliminaRegistro(idGrupo, noEmpresa);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: GrupoEmpresasAction, M: eliminaRegistro");
		}return resultado;
	}
	
	@DirectMethod
	public GrupoEmpresasDto obtieneCorreo(int idGrupo){
		GrupoEmpresasDto listaResultado = new GrupoEmpresasDto();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objGrupoEmpresasService = (GrupoEmpresasService)contexto.obtenerBean("objGrupoEmpresasBusinessImpl");
			listaResultado = objGrupoEmpresasService.obtieneCorreo(idGrupo);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: GrupoEmpresasAction, M: obtieneCorreo");
		}return listaResultado;
	}
	
	@DirectMethod
	public String cambiaNivel(int idGrupo, int nivel){
		String mensaje = "";
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objGrupoEmpresasService = (GrupoEmpresasService)contexto.obtenerBean("objGrupoEmpresasBusinessImpl");
			mensaje = objGrupoEmpresasService.cambiaNivel(idGrupo, nivel);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: GrupoEmpresasAction, M: cambiaNivel");
		}return mensaje;		
	}
	
}
