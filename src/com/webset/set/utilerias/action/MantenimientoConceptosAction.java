package com.webset.set.utilerias.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import com.webset.set.reportes.business.ReportesBusiness;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.business.impl.MantenimientoConceptosBusinessImpl;
import com.webset.set.utilerias.service.MantenimientoConceptosService;
import com.webset.set.utileriasmod.dto.MantenimientoConceptosDto;
import com.webset.utils.tools.Utilerias;

import net.sf.jasperreports.engine.JRDataSource;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;

public class MantenimientoConceptosAction {

	Contexto contexto = new Contexto();
	Bitacora bitacora = new Bitacora();
	//agrego//
	MantenimientoConceptosBusinessImpl mantenimientoConceptosBussinesImpl= new MantenimientoConceptosBusinessImpl();
	//////
	MantenimientoConceptosService objMantenimientoConceptosService;
	
	/////agrego inicio 
	@DirectMethod
	public JRDataSource obtenerDatosReporteConcepto(String nomReporte, Map<String, Object> parameters, ServletContext context){
		
		JRDataSource jrDataSource = null;
		
		try{
			mantenimientoConceptosBussinesImpl = (MantenimientoConceptosBusinessImpl)contexto.obtenerBean("objMantenimientoConceptosBusinessImpl", context);
			jrDataSource = mantenimientoConceptosBussinesImpl.obtenerDatosConcepto(nomReporte, parameters);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:SET, C:ReportesAction, M:obtenerXMLReporte");
		}
		return jrDataSource;
	}
////fin 
	@DirectMethod
	public List<MantenimientoConceptosDto> llenaBancos(){
		List<MantenimientoConceptosDto> listaResultado = new ArrayList<MantenimientoConceptosDto>();
		
		try{			
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMantenimientoConceptosService = (MantenimientoConceptosService)contexto.obtenerBean("objMantenimientoConceptosBusinessImpl");
			listaResultado = objMantenimientoConceptosService.llenaBancos();
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoConceptosAction, M:llenaBancos");
		}return listaResultado;
	}	
	
	@DirectMethod
	public List<MantenimientoConceptosDto> llenaFormaPago (String ingresoEgreso){
		List<MantenimientoConceptosDto> listaResultado = new ArrayList<MantenimientoConceptosDto>();
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMantenimientoConceptosService = (MantenimientoConceptosService)contexto.obtenerBean("objMantenimientoConceptosBusinessImpl");
			listaResultado = objMantenimientoConceptosService.llenaFormaPago(ingresoEgreso);
			System.out.println(listaResultado.size() + "total registros");
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoConceptosAction, M: llenaFormaPago");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<MantenimientoConceptosDto> llenaGrid(int idBanco, String conceptoBanco, String formaPago, String ingresoEgreso){
		List<MantenimientoConceptosDto> listaResultado = new ArrayList<MantenimientoConceptosDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
				objMantenimientoConceptosService = (MantenimientoConceptosService)contexto.obtenerBean("objMantenimientoConceptosBusinessImpl");
				listaResultado = objMantenimientoConceptosService.llenaGrid(idBanco, conceptoBanco, formaPago, ingresoEgreso);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C: MantenimientoConceptosAction, M: llenaGrid");
		}return listaResultado;
	}
	
	@DirectMethod
	public String aceptar(String registro){
		Gson gson = new Gson();
		List<Map<String, String>> matRegistro = gson.fromJson(registro, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		String resultado = "";
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMantenimientoConceptosService = (MantenimientoConceptosService)contexto.obtenerBean("objMantenimientoConceptosBusinessImpl");
			resultado = objMantenimientoConceptosService.aceptar(matRegistro);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoConceptosAction, M: aceptar");
		}return resultado;	
	}
	
	@DirectMethod
	public int eliminaConcepto (int idBanco, String conceptoBanco, String ingresoEgreso){
		int resultado = 0;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMantenimientoConceptosService = (MantenimientoConceptosService)contexto.obtenerBean("objMantenimientoConceptosBusinessImpl");
			resultado = objMantenimientoConceptosService.eliminaConcepto(idBanco, conceptoBanco, ingresoEgreso);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoConceptosAction, M: eliminaConcepto");
		}return resultado;
	}
	
	@DirectMethod
	public List<MantenimientoConceptosDto> llenaTipoOperacion(){
		List<MantenimientoConceptosDto> listaResultado = new ArrayList<MantenimientoConceptosDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMantenimientoConceptosService = (MantenimientoConceptosService)contexto.obtenerBean("objMantenimientoConceptosBusinessImpl");
			listaResultado = objMantenimientoConceptosService.llenaTipoOperacion();
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoConceptosAction, M: llenaTipoOperacion");
		}return listaResultado;
	}
	
}
