package com.webset.set.financiamiento.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.financiamiento.dao.impl.AlertaVencimientoDaoImpl;
import com.webset.set.financiamiento.dto.ControlPagosPasivos;
import com.webset.set.financiamiento.service.AlertaVencimientoService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;

import net.sf.jasperreports.engine.JRDataSource;
public class AlertaVencimientoAction {
	
	private static final int Map = 0;
	Bitacora bitacora = new Bitacora();
	Contexto contexto=new  Contexto();
	Funciones funciones = new Funciones();
 
	private static Logger logger = Logger.getLogger(AlertaVencimientoDaoImpl.class);
	@DirectMethod 
	public List<LlenaComboGralDto> llenarCmbBanco(int usuario){
		System.out.println("angel");
		List<LlenaComboGralDto> listLista = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listLista ;
		try{																										   
			AlertaVencimientoService alertaVencimientoService =(AlertaVencimientoService)contexto.obtenerBean("alertaVencimientoBusinessImpl");
			listLista  = alertaVencimientoService.consultarBanco(usuario);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: AlertaVencimiento C: AlertaVencimientoAction M: llenarCmbbanco");
		}
		return listLista ;
	}
	
	
	
	@DirectMethod 
	public List<LlenaComboGralDto> llenarCmbLinea(int banco){
		System.out.println("angel");
		List<LlenaComboGralDto> listLista = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listLista ;
		try{																										   
			AlertaVencimientoService alertaVencimientoService =(AlertaVencimientoService)contexto.obtenerBean("alertaVencimientoBusinessImpl");
			listLista  = alertaVencimientoService.consultarLinea(banco);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: AlertaVencimiento C: AlertaVencimientoAction M: llenarCmbbanco");
		}
		return listLista ;
	}
	
	
	
	@DirectMethod 
	public List<LlenaComboGralDto> llenarCmbCredito(int banco){
		System.out.println("angel");
		List<LlenaComboGralDto> listLista = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listLista ;
		try{																										   
			AlertaVencimientoService alertaVencimientoService =(AlertaVencimientoService)contexto.obtenerBean("alertaVencimientoBusinessImpl");
			listLista  = alertaVencimientoService.consultarCredito(banco);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: AlertaVencimiento C: AlertaVencimientoAction M: llenarCmbbanco");
		}
		return listLista ;
	}
	
	
	

	
	@DirectMethod 
	public List<ControlPagosPasivos> llenarGrid(int banco,String linea,int credito, String fecha, int conso){
		
		List<ControlPagosPasivos> listDis = new ArrayList<ControlPagosPasivos>();

		if(!Utilerias.haveSession(WebContextManager.get()))
			return listDis;
		try{																										   
			AlertaVencimientoService alertaVencimientoService =(AlertaVencimientoService)contexto.obtenerBean("alertaVencimientoBusinessImpl");
			listDis  = alertaVencimientoService.llenarGrid(banco,linea,credito, fecha,conso);
			
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: FinanciamientoModificacionC C: FinanciamientoModificacionCAction M: llenarGrid");
		}
 
		
		return listDis;
	}	
	

	public HSSFWorkbook reportePagosP(String nombre){
		System.out.println("nombre --------> " + nombre);
		FileInputStream file = null;
		HSSFWorkbook workbook = null;
		File arch = new File(nombre);
		try {
			file = new FileInputStream(arch);
			workbook = new HSSFWorkbook(file);
			file.close();
		} catch (FileNotFoundException e) {
			logger.error(e.toString());
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: AlertaVencimiento C: AlertaVencimientoAction M: llenarCmbbanco");
		} catch (IOException e) {
			logger.error(e.toString());
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: AlertaVencimiento C: AlertaVencimientoAction M: llenarCmbbanco");
		} catch (Exception e) {
			logger.error(e.toString());
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: AlertaVencimiento C: AlertaVencimientoAction M: llenarCmbbanco");
		}
		arch.delete();
		return workbook;
	}

	
	
	@DirectMethod
	public JRDataSource reportePDF2(Map map, ServletContext context){
		JRDataSource jrDataSource = null;
		System.out.println("entra al action pdf");
		try {

			AlertaVencimientoService alertaVencimientoService =(AlertaVencimientoService)contexto.obtenerBean("alertaVencimientoBusinessImpl",context);
			jrDataSource = alertaVencimientoService.reportePDF2(map);
			System.out.println(jrDataSource +"..::rrr");
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesAction, M:reportesCanasta");
		}
		return jrDataSource;
	}
	
	

	
	
	
	@DirectMethod
	public String exportaExcelPagos(String json) {
		
		System.out.println("datos::.."+json);
		String resultado = "";
			
		if(!Utilerias.haveSession(WebContextManager.get()) || !(Utilerias.tienePermiso(WebContextManager.get(), 109)||Utilerias.tienePermiso(WebContextManager.get(), 110)))
			return resultado;
		try {

			
			AlertaVencimientoService alertaVencimientoService =(AlertaVencimientoService)contexto.obtenerBean("alertaVencimientoBusinessImpl");
			resultado = alertaVencimientoService.exportaExcel(json);
		System.out.println(resultado+"::..Resultado");
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosAction, M: exportaExcel");
		}
		
		return resultado;
	}
	
	
	
	
	
	
	
	public List<Map<String, Object>> ReportePP(String pagos, ServletContext context) {
		
		System.out.println(pagos+".::GGG");
		List<Map<String, Object>> lista =new ArrayList<Map<String, Object>>();
		Gson gson = new Gson();
		DecimalFormat formato= new DecimalFormat("###,###.##");
		List<Map<String, String>> param= gson.fromJson(pagos,new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		ArrayList<Map> c = new ArrayList<Map>(); 
		
		
		System.out.println(param.size()+"..Tam");

		if (contexto == null) {
			bitacora.insertarRegistro(
					"P:Financiamiento, C:VencimientoFinanciamientoCAction, M:obtenerReporteVencimientos" + " contexto nulo");
			return null;
		}
		try {

		    
		    for (Map<String, String> map : param) {
				c.add(map);
				
			}
	
			for (Map map : c) {
				System.out.println(map);
			}


 


			for (int i=0; i <= param.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();		
					
					map.put("fecha",param.get(i).get("fecha"));
					map.put("empresa",param.get(i).get("empresa"));
					map.put("institucion",param.get(i).get("institucion"));
					map.put("financiamiento",param.get(i).get("financiamiento"));
					map.put("tipoFin",param.get(i).get("tipoFin"));
					map.put("montoCred",Double.parseDouble(param.get(i).get("montoCred")));
					map.put("saldoActual",Double.parseDouble(param.get(i).get("saldoActual")));
					map.put("pagoCap",Double.parseDouble(param.get(i).get("pagoCap")));
					map.put("interes",Double.parseDouble(param.get(i).get("interes")));
					map.put("iva",Double.parseDouble(param.get(i).get("iva")));
					map.put("pagoTotal",Double.parseDouble(param.get(i).get("pagoTotal")));
					lista.add(map);	
			}
			
			
			
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCAction, M:obtenerReporteVencimientos");
		}
		System.out.println(lista+"..::TT");
		return lista;
	}
	
	
	
	
	
	
	
	

}
