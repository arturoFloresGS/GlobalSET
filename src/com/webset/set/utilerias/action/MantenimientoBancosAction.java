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
import com.webset.set.personas.service.ConsultaPersonasService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.service.MantenimientoBancosService;
import com.webset.set.utileriasmod.dto.MantenimientoBancosDto;
import com.webset.utils.tools.Utilerias;

public class MantenimientoBancosAction {
	Contexto contexto = new Contexto();
	Bitacora bitacora = new Bitacora();
	
	MantenimientoBancosService objMantenimientoBancosService;
	
	public HSSFWorkbook reporteBancos(String tipoBanco,ServletContext context){
		HSSFWorkbook wb=null;
		try {
			objMantenimientoBancosService = (MantenimientoBancosService)contexto.obtenerBean("objMantenimientoBancosBusinessImpl",context);
			wb=objMantenimientoBancosService.reporteBancos(tipoBanco);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoBancosAction, M: reporteBancos");
		}return wb;
	}
		
	@DirectMethod
	public List<MantenimientoBancosDto> llenaComboBancos(int bancoNacional)
	{
		List<MantenimientoBancosDto> listaResultado = new ArrayList<MantenimientoBancosDto>();
				
		try
		{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMantenimientoBancosService = (MantenimientoBancosService)contexto.obtenerBean("objMantenimientoBancosBusinessImpl");
			listaResultado = objMantenimientoBancosService.llenaComboBancos(bancoNacional);
			}
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoBancosAction, M:llenaComboBancos");
		}
		return listaResultado;
	}	
	
	
	@DirectMethod
	public List<MantenimientoBancosDto> llenaGridBancos(String descBanco, String aba, int nacionalidad){
		List <MantenimientoBancosDto> listaResultado = new ArrayList<MantenimientoBancosDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMantenimientoBancosService = (MantenimientoBancosService)contexto.obtenerBean("objMantenimientoBancosBusinessImpl");
			listaResultado = objMantenimientoBancosService.llenaGridBancos(descBanco, aba, nacionalidad);
			System.out.println(listaResultado.size() + "regreso del action");
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoBancosAction, M:llenaGridBancos");
		}return listaResultado;
	}
	
	@DirectMethod
	public int obtieneIdBancoMax(){
		int resultado = 0;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMantenimientoBancosService = (MantenimientoBancosService)contexto.obtenerBean("objMantenimientoBancosBusinessImpl");
			resultado = objMantenimientoBancosService.obtieneIdBancoMax();	
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoBancosAction, M:obtieneIdBancoMax");
		}return resultado;
	};
	
	@DirectMethod
	public List<MantenimientoBancosDto> validaCuentasAsignadas (int idBanco){
		List<MantenimientoBancosDto> listaResultado = new ArrayList<MantenimientoBancosDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			System.out.println("Llega al action de la eliminacion");
			objMantenimientoBancosService = (MantenimientoBancosService)contexto.obtenerBean("objMantenimientoBancosBusinessImpl");
			listaResultado = objMantenimientoBancosService.validaChequerasAsignadas(idBanco);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoBancosAction, M:validaCuentasAsignadas");
		}return listaResultado;
	}
	
	@DirectMethod
	public int eliminaBanco(int idBanco){
		int resultado = 0;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			//System.out.println("Llega al 2 action de la eliminacion");
			objMantenimientoBancosService = (MantenimientoBancosService)contexto.obtenerBean("objMantenimientoBancosBusinessImpl");
			resultado = objMantenimientoBancosService.eliminaBanco(idBanco);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoBancosAction, M:eliminaBanco");			
		}return resultado;
	}
	
	@DirectMethod
	
	public String aceptar(String registro){
		Gson gson = new Gson();
		List<Map<String, String>> matRegistro = gson.fromJson(registro, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		String resultado = "";
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			//System.out.println("Entra al action del aceptar");
			objMantenimientoBancosService = (MantenimientoBancosService)contexto.obtenerBean("objMantenimientoBancosBusinessImpl");
			resultado = objMantenimientoBancosService.aceptar(matRegistro);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C: MantenimientoBancosAction, M:aceptar");
		}return resultado;	
	}
}
