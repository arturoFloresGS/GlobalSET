package com.webset.set.bancaelectronica.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.bancaelectronica.dao.MovimientosBEDao;
import com.webset.set.bancaelectronica.dao.impl.MovimientosBEDaoImpl;
import com.webset.set.bancaelectronica.dto.MovimientosBEDto;
import com.webset.set.bancaelectronica.service.MovimientosBEService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.dto.ComunDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;
/**
 * @author Jessica Arelly Cruz Cruz
 * @since 01/03/2011
 */

/**
* Modificado por YEC
* Se agregan funciones para generar excel
* 21 de diciembre del 2015
*/

public class MovimientosBEBusiness implements MovimientosBEService {
	private Bitacora bitacora= new Bitacora();
	private MovimientosBEDao objMovimientosBEDao;
	private static Logger logger = Logger.getLogger(MovimientosBEBusiness.class);
	
	public List<LlenaComboGralDto> llenarComboBancos(int noEmpresa){
		return objMovimientosBEDao.llenarComboBancos(noEmpresa);
	}

	public MovimientosBEDao getMovimientosBancaEDao() {
		return objMovimientosBEDao;
	}
	
	public List<LlenaComboGralDto> llenarComboChequeras(int noBanco,int noEmpresa){
		return objMovimientosBEDao.llenarComboChequeras(noBanco, noEmpresa);
	}
	
	public List<ComunDto>obtenerConceptos(boolean lbGenerico, int noBanco){
		List<ComunDto> conceptos = new ArrayList<ComunDto>();
		try {
			String generico = objMovimientosBEDao.seleccionarBancaElect(noBanco);
			if (generico.trim().equals("I") || generico.trim().equals("A") || generico.trim().equals("I,A"))
		        lbGenerico = true;
		    else //conceptos genericos
		        lbGenerico = false;
			conceptos = objMovimientosBEDao.obtenerConceptos(lbGenerico, noBanco);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Bancaelectronica, C:MovimientosBEBusiness, M:llenarComboChequeras");
		}return conceptos;	
	}

	/*********Exporta excel************/
	
	public String exportaExcel(String datos) {
		String respuesta = "";
	    Gson gson = new Gson();
		List<Map<String, String>> parameters = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
	    try {
	    	int i=Integer.parseInt(parameters.get(0).get("encabezados"));
	    	String encabezados[]=new String[i];
	    	String nombreParametros[]=new String[i];
	    	encabezados[0]="No.Empresa";
	    	encabezados[1]="Empresa";
	    	encabezados[2]="Id Banco";
	    	encabezados[3]="Banco";
	    	encabezados[4]="Chequera";
	    	encabezados[5]="Cargo abono";
	    	
	    	nombreParametros[0]="noEmpresa";
	    	nombreParametros[1]="nomEmpresa";
	    	nombreParametros[2]="idBanco";
	    	nombreParametros[3]="descBanco";
	    	nombreParametros[4]="idChequera";
	    	nombreParametros[5]="cargoAbono";
	    	
	    	if(parameters.get(0).get("opcion").equals("rConceptoConDetalle")|| parameters.get(0).get("opcion").equals("rBE")){
	    		encabezados[6]="Fecha Alta";
	    		encabezados[7]="Fecha Banco";
	    		encabezados[8]="Folio detalle";
	    		encabezados[9]="Secuencia";
	    		encabezados[10]="Divisa";
	    		encabezados[11]="Importe";
	    		encabezados[12]="Importe Egreso";
	    		encabezados[13]="Sucursal";
	    		encabezados[14]="Referencia";
	    		encabezados[15]="Traspaso Banco";
	    		encabezados[16]="Ejecutado";
	    		encabezados[17]= "Concepto Set";
	    		encabezados[18]="Observacion";
				encabezados[19]="Descripcion";
				encabezados[20]="Traspaso Contador";
				
				nombreParametros[6]="fecAlta";
				nombreParametros[7]="fecValor";
				nombreParametros[8]="folioDet";
				nombreParametros[9]="secuencia";
				nombreParametros[10]="idDivisa";
				nombreParametros[11]="importe";
				nombreParametros[12]="importeCa";
				nombreParametros[13]="sucursal";
				nombreParametros[14]="referencia";
				nombreParametros[15]="bTraspBanco";
				nombreParametros[16]="ejecutado";
				nombreParametros[17]="conceptoSet";
				nombreParametros[18]="observacion";
				nombreParametros[19]="descObservacion";
				nombreParametros[20]="bTraspConta";
				
				if(parameters.get(0).get("opcion").equals("rConceptoConDetalle")){
					
				}else{
					encabezados[21]="Folio banco";
					nombreParametros[21]="folioBanco";
				}
				
	    	}
	    	
	    	if(parameters.get(0).get("opcion").equals("rConceptoSinDetalle")){
	    		encabezados[6]="Concepto";
				nombreParametros[6]="concepto";
	    	}
	    	
			//Se crea el libro Excel
			HSSFWorkbook wb = Utilerias.generarExcel(encabezados, parameters, nombreParametros);			
            
            respuesta = ConstantesSet.RUTA_EXCELA + "propuestas " + Utilerias.indicadorFecha() +".xls";
            File outputFile = new File(respuesta.replace('/', File.separatorChar).replace('\\', File.separatorChar));
          
			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
        } catch (IOException e) {
        	logger.error(e);
        	e.printStackTrace();
        	respuesta = "";
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Bancaelectronica, C:MovimientosBEBusiness, M:exportaExcel");
    		
        } catch(Exception e){
        	logger.error(e);
        	e.printStackTrace();
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Bancaelectronica, C:MovimientosBEBusiness, M:exportaExcel");
    		
            respuesta = "";
        }
        return respuesta;
	}

	public List<MovimientosBEDto> consultaExcel(String noEmpresa, String idBanco, String chequera, String fechaIni,String fechaFin, String tipoMov,String concepto,String detalle,String origenMovimiento) {
		List<MovimientosBEDto> objResultado =  new ArrayList<MovimientosBEDto>();
		Map<String, String> datos= new HashMap<>();
		try {
			datos.put("piBancoSup", idBanco);
			datos.put("piBancoInf", idBanco);
			datos.put("psChequera", chequera);
			if(!noEmpresa.equals("")){
				datos.put("piEmpresaSup", noEmpresa);
				datos.put("piEmpresaInf", noEmpresa);
			}else{
				datos.put("piEmpresaSup", "0");
				datos.put("piEmpresaInf", "3000");
			}
			
			if(fechaIni.equals("")&&fechaFin.equals("")){
				datos.put("pdFechaSup","01-01-1900");
				datos.put("pdFechaInf","01-01-2900");
			}else{
				datos.put("pdFechaSup",fechaFin);
				datos.put("pdFechaInf",fechaIni);
			}
			
			if(tipoMov.equals("true")){
				datos.put("pbdetalle", detalle);
				objResultado=objMovimientosBEDao.ejecutarReporteConceptoBE(datos);
			}else{
				datos.put("tipoMov",tipoMov);
				datos.put("origenMovimiento", origenMovimiento);
				datos.put("concepto", concepto);
				objResultado=objMovimientosBEDao.ejecutarReporteBE(datos);
			}
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Bancaelectronica, C:MovimientosBEBusiness, M:consultaExcel");
		}return objResultado;
	}

	//*********************************************************************
			public MovimientosBEDao getMovimientosBEDao() {
				return objMovimientosBEDao;
			}

			public void setObjMovimientosBEDao(MovimientosBEDao objMovimientosBEDao) {
				this.objMovimientosBEDao = objMovimientosBEDao;
			}			
}
