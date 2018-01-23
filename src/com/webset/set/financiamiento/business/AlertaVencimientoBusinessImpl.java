package com.webset.set.financiamiento.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.collections.functors.ForClosure;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.financiamiento.dao.AlertaVencimientoDao;
import com.webset.set.financiamiento.dto.AmortizacionCreditoDto;
import com.webset.set.financiamiento.dto.ControlPagosPasivos;
import com.webset.set.financiamiento.service.AlertaVencimientoService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
public class AlertaVencimientoBusinessImpl implements AlertaVencimientoService{
	
	AlertaVencimientoDao alertaVencimientoDao;
	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora=new Bitacora();
	ConsultasGenerales consultasGenerales;
	Funciones funciones= new Funciones();
	GlobalSingleton globalSingleton;
	
	

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	
	
	public void setDataSource(DataSource dataSource)
	{
		try
		{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Personas, C: ConsultaPersonasDaoImpl, M:setDataSource");
		}
	}


	public GlobalSingleton getGlobalSingleton() {
		return globalSingleton;
	}


	public void setGlobalSingleton(GlobalSingleton globalSingleton) {
		this.globalSingleton = globalSingleton;
	}


	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	

	public AlertaVencimientoDao getAlertaVencimientoDao() {
		return alertaVencimientoDao;
	}

	public void setAlertaVencimientoDao(AlertaVencimientoDao alertaVencimientoDao) {
		this.alertaVencimientoDao = alertaVencimientoDao;
	}



	@Override
	public List<LlenaComboGralDto> consultarBanco(int usuario) {
List<LlenaComboGralDto> listLineas= new ArrayList<LlenaComboGralDto>();
		
		try{
			listLineas = alertaVencimientoDao.consultarBanco(usuario);
		}catch(Exception e){ 
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: Financiamiento C: alertaVenciemientoImpl M: consultarBanco");
		}
		return listLineas;
	}



	@Override
	public List<LlenaComboGralDto> consultarLinea(int banco) {

List<LlenaComboGralDto> listLineas= new ArrayList<LlenaComboGralDto>();
		
		try{
			listLineas = alertaVencimientoDao.consultarLinea(banco);
		}catch(Exception e){ 
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: Financiamiento C: alertaVenciemientoImpl M: consultarBanco");
		}
		return listLineas;
	
	}



	@Override
	public List<LlenaComboGralDto> consultarCredito(int banco) {

List<LlenaComboGralDto> listLineas= new ArrayList<LlenaComboGralDto>();
		
		try{
			listLineas = alertaVencimientoDao.consultarCredito(banco);
		}catch(Exception e){ 
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: Financiamiento C: alertaVenciemientoImpl M: consultarBanco");
		}
		return listLineas;
	}



	@Override
	public List<ControlPagosPasivos> llenarGrid(int banco, String linea, int credito, String fecha, int conso) {
		 List<ControlPagosPasivos> listDisp=new ArrayList<ControlPagosPasivos>();
		try{
			listDisp = alertaVencimientoDao.consultarDisp(banco, linea, credito, fecha, conso);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: Financiamiento C:AlertaVencimientoCImpl M: consultarDisp");
				}
		return listDisp;
	}







	@Override
	public String exportaExcel(String json) {
		
		System.out.println("");
		String respuesta = "";
	    Gson gson = new Gson();
		List<Map<String, String>> parameters = gson.fromJson(json, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		String[] titulos=null,registros = null;
		String encabezado="Pagos de";
	    try {	    	
	    	
	    		titulos= new String[]{"fecha","empresa","institucion","financiamiento" ,"tipoFin","montoCred","saldoActual","pagoCap","interes","iva","pagoTotal"};
			    registros=new String[]{"fecha","empresa","institucion","financiamiento" ,"tipoFin","montoCred","saldoActual","pagoCap","interes","iva","pagoTotal"};
				encabezado+=" pasivos. ";
	    	HSSFWorkbook wb = Utilerias.generarExcel(titulos, parameters, registros,encabezado);			
            respuesta = ConstantesSet.RUTA_EXCEL + "pagos" + Utilerias.indicadorFecha() +".xls";
            File outputFile = new File(respuesta);
          
			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
        } catch (IOException e) {
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosBusinessImpl, M:exportaExcel");
        	respuesta = "";
        } catch(Exception e){
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosBusinessImpl, M:exportaExcel");
            respuesta = "";
        }
        return respuesta;
	}










	@Override
	public JRDataSource reportePDF2(int banco, String linea, int credito, String fecha, int conso) {
		return this.alertaVencimientoDao.obtenerReportePDF2(banco, linea, credito, fecha, conso);

	}



	@Override
	public JRDataSource reportePDF2(Map map) {

		JRMapArrayDataSource  jrDataSource = null; 
	    List<Map<String, Object>> resMap = null;
		try{
			//resMap = conciliacionDao.consultarReporteMovsDuplicados(funciones.convertirCadenaInteger(parameters.get("empresa").toString()));
			resMap = alertaVencimientoDao.obtenerReportePDF2(map);;
			jrDataSource = new JRMapArrayDataSource(resMap.toArray());

		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:obtenerReporteMovsDuplicados");
		}
		return jrDataSource;
		
		
	}

	

}
