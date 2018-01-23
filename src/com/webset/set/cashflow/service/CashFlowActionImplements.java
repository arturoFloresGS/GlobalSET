package com.webset.set.cashflow.service;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.cashflow.business.CashFlowBusinessImplements;
import com.webset.set.cashflow.dto.AColReporteDto;
import com.webset.set.cashflow.dto.ARowReporteDto;
import com.webset.set.cashflow.dto.EmpresaDto;
import com.webset.set.cashflow.dto.GrupoEmpresasDto;
import com.webset.set.cashflow.dto.ReporteFlujoDto;
import com.webset.set.cashflow.dto.TotalConcepto;
import com.webset.set.graficas.CreacionGrafica;
import com.webset.set.personas.service.ConsultaPersonasService;
import com.webset.set.reportes.service.ReportesService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.utils.tools.Utilerias;

import edu.emory.mathcs.backport.java.util.Arrays;
import net.sf.jasperreports.engine.util.JsonUtil;

/****************************************************************************INIT*
 * AUTOR            : JUAN RAMIRO BARRERA MARTINEZ
 * NAME             : CashFlowActionImplements()
 * TYPE             : CLASS				   
 * RESPONSABILITY   : CONTENER LOS METODOS QUE SE INVOCAN REMOTAMENTE DESDE LA
 *                    CAPA DE PRESENTACION, ASI COMO RETORNAR INFORMACION GENERADA 
 *                    EN LA CAPA DE NEGOCIO HACIA LA CAPA DE PRESENTACION.	 	
 ****************************************************************************INIT*/
public class CashFlowActionImplements {
		  
	private Bitacora bitacora = new Bitacora();	
	Contexto contexto=new  Contexto();
	private static Logger logger = Logger.getLogger(CashFlowActionImplements.class);
	Integer numCols; 
	ARowReporteDto filasReporte=new ARowReporteDto();
	List<HashMap> l = null;
	/****************************************************************************INIT*
	 * AUTOR            : JUAN RAMIRO BARRERA MARTINEZ
	 * NAME             : getGrupoEmpresasService()
	 * PARAMS           : NOTHING				   
	 * RETURN           : LISTA DE GRUPO DE EMPRESAS
	 * RESPONSABILITY   : DETERMINAR Y DEVOLVER UNA LISTA DE GRUPO DE EMPRESAS	 	
     ****************************************************************************INIT*/	
	@DirectMethod
	public List<GrupoEmpresasDto> getGrupoEmpresasService()
	{
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		
		List<GrupoEmpresasDto> listaGrupoEmpresas =new ArrayList<GrupoEmpresasDto>();	
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
				CashFlowBusinessImplements cashFlowBusines = (CashFlowBusinessImplements) contexto.obtenerBean("cashFlowBusiness");
				listaGrupoEmpresas= cashFlowBusines.getGrupoEmpresasBusiness();
			}
		}
		catch(Exception e){
			logger.error("Error: P:CASH FLOW, C:CashFlowActionImplements, M:getGrupoEmpresasService");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:CASH FLOW, C:CashFlowActionImplements, M:getGrupoEmpresasService");					
		}
		
		return listaGrupoEmpresas;
		
	}//END METHOD getGrupoEmpresasService()
	
	
	
	/****************************************************************************INIT*
	 * AUTOR            : JUAN RAMIRO BARRERA MARTINEZ
	 * NAME             : getReportesFlujoService()
	 * PARAMS           : NOTHING				   
	 * RETURN           : LISTA DE REPORTES DE FLUJO DE EFECTIVO
	 * RESPONSABILITY   : DETERMINAR Y DEVOLVER UNA LISTA DE UN GRUPO DE REPORTES CF	 	
     ****************************************************************************INIT*/
	@DirectMethod
	public List<ReporteFlujoDto> getReportesFlujoService()
	{
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		
		List<ReporteFlujoDto> listaReportesFlujo =new ArrayList<ReporteFlujoDto>();	
		
		try{		
			if (Utilerias.haveSession(WebContextManager.get())) {
			CashFlowBusinessImplements cashFlowBusines = (CashFlowBusinessImplements) contexto.obtenerBean("cashFlowBusiness");			
			listaReportesFlujo= cashFlowBusines.getReportesFlujoBusiness();		
			}
		}catch(Exception e){
			logger.error("Error: P:CASH FLOW, C:CashFlowActionImplements, M:getReportesFlujoService");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:fdsaf, C:adfasd, M:sdfasd");					
		}		

		return listaReportesFlujo;
		
	}//END METHOD getReportesFlujoService
	
	/****************************************************************************INIT*
	 * AUTOR            : JUAN RAMIRO BARRERA MARTINEZ
	 * NAME             : getEmpresasService()
	 * PARAMS           : int idGrupo   :IDENTIFICADOR DEL GRUPO DE EMPRESAWS
	 *                  : int noUsuario :IDENTIFICADOR DEL USUARIO				   
	 * RETURN           : LISTA DE EMPRESAS
	 * RESPONSABILITY   : DETERMINAR Y DEVOLVER UNA LISTA DE EMPRESAS PERTENECIENTES
	 *                    A UN GRUPO Y QUE PUEDE VISUALIZAR UN USUARIO	 	
     ****************************************************************************INIT*/
	@DirectMethod
	public List<EmpresaDto> getEmpresasService(int idGrupo, int noUsuario)
	{
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		
		List<EmpresaDto> listaEmpresas =new ArrayList<EmpresaDto>();	
		
		try{		
			if (Utilerias.haveSession(WebContextManager.get())) {
			CashFlowBusinessImplements cashFlowBusines = (CashFlowBusinessImplements) contexto.obtenerBean("cashFlowBusiness");			
			listaEmpresas= cashFlowBusines.getEmpresasBusiness(idGrupo, noUsuario);		
			}
		}catch(Exception e){
			logger.error("Error: P:CASH FLOW, C:CashFlowActionImplements, M:getEmpresasService");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:fdsaf, C:adfasd, M:sdfasd");					
		}		
		
		return listaEmpresas;
		
	}//END METHOD getEmpresasService

	
	/****************************************************************************INIT*
	 * AUTOR            : JUAN RAMIRO BARRERA MARTINEZ
	 * NAME             : getStructureReportService()
	 * PARAMS           : int paramIdGrupo        :GRUPO DE EMPRESA 
	 *		   	        : int paramNoEmpresa      :EMPRESA
	 *		   			: String paramFechaInicial:FECHA INICIAL DEL PERIODO SELECCIONADO
	 *		   			: String paramagFechaFinal:FECHA FINAL DEL PERIODO SELECCIONADO
  	 *		   			: int paramIdReporte	  :ID DEL REPORTE SELECCIONADO		   
     * RETURN           : String CON UNA RESPUESTA EN FORMATO JSON CORRESPONDIENTE A LA
     *                    ESTRUCTURA QUE DEFINIRA LA LONGITUD Y CAMPOS DEL REPORTE 
	 * RESPONSABILITY   : DETERMINAR EN BASE AL PERIODO SELECCIONADO UNA ESTRUCTURA QUE 
	 *                    CORRESPONDERA A LA DEFINICION DE LA CABECERA QUE FORMARA EL 
	 *                    GRID EN LA CAPA DE PRESENTACION
     ****************************************************************************INIT*/
	@DirectMethod
	public String getStructureReportService(int paramIdGrupo, 
			   						 	    int paramNoEmpresa,
			   							    String paramFechaInicial,
			   							    String paramagFechaFinal,
			   							    int paramIdReporte){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		
		Gson gson = new Gson();
		
		String json="";
		
		
		List<AColReporteDto> columnasReporte =new ArrayList<AColReporteDto>();
		
		try{		
			if (Utilerias.haveSession(WebContextManager.get())) {
			CashFlowBusinessImplements cashFlowBusines = (CashFlowBusinessImplements) contexto.obtenerBean("cashFlowBusiness");			
			columnasReporte = cashFlowBusines.getStructureReportBusiness(paramIdGrupo, paramNoEmpresa, paramFechaInicial, paramagFechaFinal, paramIdReporte);
			json = gson.toJson(columnasReporte);
			System.out.println("llego hasta aqui en flujo " + json);
			}
		}catch(Exception e){
			logger.error("Error: P:CASH FLOW, C:CashFlowActionImplements, M:getStructureReportService");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:fdsaf, C:adfasd, M:sdfasd");					
		}		
		
		return json;
		
	}//END METHOD getStructureReportService 
	
	
	/****************************************************************************INIT*
	 * AUTOR            : JUAN RAMIRO BARRERA MARTINEZ
	 * NAME             : getBodyReportService()
	 * PARAMS           : int paramIdGrupo        :GRUPO DE EMPRESA 
	 *		   	        : int paramNoEmpresa      :EMPRESA
	 *		   			: String paramFechaInicial:FECHA INICIAL DEL PERIODO SELECCIONADO
	 *		   			: String paramagFechaFinal:FECHA FINAL DEL PERIODO SELECCIONADO
  	 *		   			: int paramIdReporte	  :ID DEL REPORTE SELECCIONADO		   
     * RETURN           : String CON UNA RESPUESTA EN FORMATO JSON CORRESPONDIENTE A LA
     *                    ESTRUCTURA QUE DEFINIRA LA LONGITUD Y CUERPO DEL REPORTE 
	 * RESPONSABILITY   : DETERMINAR EN BASE AL PERIODO SELECCIONADO UNA ESTRUCTURA QUE 
	 *                    CORRESPONDERA A LA DEFINICION DEL CUERPO QUE FORMARA EL 
	 *                    GRID EN LA CAPA DE PRESENTACION
     ****************************************************************************INIT*/
	@DirectMethod
	public ARowReporteDto getBodyReportService(int paramIdGrupo,
												int paramNoEmpresa,
												String paramFechaInicial,
												String paramagFechaFinal,
												int paramIdReporte)
	{
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null; 
		try
		{
			if (Utilerias.haveSession(WebContextManager.get())) {
			CashFlowBusinessImplements cashFlowBusines = (CashFlowBusinessImplements) contexto.obtenerBean("cashFlowBusiness");
			filasReporte = cashFlowBusines.getBodyReportBusiness(paramIdGrupo, paramNoEmpresa, paramFechaInicial, paramagFechaFinal, paramIdReporte);
			
			//Graficar
			if(filasReporte!=null) {
				  l = filasReporte.getRow();
				  System.out.println("tamaño "+l.size());
				for(int i=0; i<l.size(); i++){
					System.out.println(l.get(i));
				}
			}
			
			//if(filasReporte!=null && filasReporte.get)
			//Fin Graficar
			}
		}
		catch(Exception e){
			logger.error("Error: P:CASH FLOW, C:CashFlowActionImplements, M:getBodyReportServicePAEE");
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:fdsaf, C:adfasd, M:sdfasd");					
		}
		return filasReporte;
	}//END METHOD getBodyReportService
	
	@DirectMethod
	public ARowReporteDto getBodyReportService2(int paramIdGrupo,
												int paramNoEmpresa,
												String paramFechaInicial,
												String paramagFechaFinal,
												int paramIdReporte)
	{
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		
		
		try
		{
			if (Utilerias.haveSession(WebContextManager.get())) {
			CashFlowBusinessImplements cashFlowBusines = (CashFlowBusinessImplements) contexto.obtenerBean("cashFlowBusiness");
			filasReporte = cashFlowBusines.getBodyReportBusiness(paramIdGrupo, paramNoEmpresa, paramFechaInicial, paramagFechaFinal, paramIdReporte);
			
			//Graficar
			if(filasReporte!=null) {
				  l = filasReporte.getRow(); 
				  System.out.println(l.size());
				for(int i=0; i<l.size(); i++){
				 	//System.out.println(l.get(i));
				}
			}
		//	auxiliar(filasReporte);
			//if(filasReporte!=null && filasReporte.get)
			//Fin Graficar
			}
		}
		catch(Exception e){
			logger.error("Error: P:CASH FLOW, C:CashFlowActionImplements, M:getBodyReportServicePAEE");
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:fdsaf, C:adfasd, M:sdfasd");					
		}
		return filasReporte;
	}
	
	@DirectMethod
	public ARowReporteDto auxiliar(ARowReporteDto filasReporte2){ 
		System.out.println("auxiliar1");
		ARowReporteDto wb=null; 
			if(filasReporte2!=null) {
				  l = filasReporte2.getRow(); 
				  System.out.println(l.size());
				for(int i=0; i<l.size(); i++){
				 	System.out.println(l.get(i));
				} 
	}
			return filasReporte2;
			}
	
	@DirectMethod
	public HSSFWorkbook reporteExcels(String grupo,String Noempresa,String empresa,String fechaIni,String fechaFin,String reporte,String noGrupo,ServletContext context ){ 
		HSSFWorkbook wb=null;
		
		try {
			CashFlowBusinessImplements cashFlowBusines = (CashFlowBusinessImplements) contexto.obtenerBean("cashFlowBusiness", context);
			wb = cashFlowBusines.reporteFlujo(grupo,Noempresa,empresa,fechaIni,fechaFin,reporte,noGrupo);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Flujo, C: reporteExcel, M: reporteExcel");
		}
		return wb;
	}
	@DirectMethod
	public HSSFWorkbook reporteExcels2(String grupo,String Noempresa,String empresa,String fechaIni,String fechaFin,String reporte,String noGrupo,ServletContext context ){ 
		HSSFWorkbook wb=null;
		
		try {
			CashFlowBusinessImplements cashFlowBusines = (CashFlowBusinessImplements) contexto.obtenerBean("cashFlowBusiness", context); 
			wb = cashFlowBusines.reporteFlujo2(grupo,Noempresa,empresa,fechaIni,fechaFin,reporte,noGrupo); 
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Flujo, C: reporteExcel, M: reporteExcel");
		}
		return wb;
	}
	@DirectMethod
	public String exportaExcel(String datos) {
		String resp = "Error";
//		if(!Utilerias.haveSession(WebContextManager.get()))
//			return resp;
		Gson gson = new Gson();
		List<Map<String, String>> paramsGrid = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		System.out.println("datos "+datos);
		String[] dato=null;
		String dato2=null;
		String delimitador1=",";
		int posicion=0;
		String fila="";

		try {
			System.out.println("tamaño original"+paramsGrid.size());
			
			for(int i = 0; i < 5; i++){
				//System.out.println("posicion i"+paramsGrid.get(i).toString());
				String var=paramsGrid.get(i).toString();
				posicion=var.indexOf(delimitador1);
				dato=var.split(","); 
				System.out.println(dato.length + "lista tamaño");
				for(int j=0;j<dato.length;j++){ 
					System.out.println(dato[j]);
					
				}
				
//				for (int j=1;j<10;j++){
//					System.out.println("keys "+paramsGrid.get(j).keySet());
//				}
				//System.out.println(paramsGrid.get(i).keySet().size());
				//				for(int j=0;j<paramsGrid.get(i).size();j++){
//					fila=paramsGrid.get(i).get(j);
//					System.out.println("fila for2 "+fila);
//				}
				
				
//				System.out.println("posicion i"+paramsGrid.get(i).toString());
//				posicion=fila.indexOf(delimitador1);
//				
//				dato=fila.substring(0,posicion);
//				System.out.println("dato "+dato);
//				//fila quedade cadena
//				fila=fila.substring(posicion+1);
//				System.out.println("fila "+fila);
			}
			
		
//			System.out.println("datos "+datos);
//			CashFlowBusinessImplements cashFlowBusines = new CashFlowBusinessImplements() ;
//			resp = cashFlowBusines.exportaExcel(datos);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Reportes, C:ReportesAction, M:reporteTransConfirmadas");
		}
		return resp;
	}
	
	/**
	 * Grafica el flujo de efectivo real. Diferencia entre Ingresos - Egresos.
	 * @param noEmpresa
	 * @param sFecIni
	 * @param sFecFin
	 * @param idReporte
	 * @param iUserId
	 * @return
	 */
	@DirectMethod
	public List<TotalConcepto> graficarFlujoEfectivo(int noEmpresa, String sFecIni, String sFecFin, int idReporte, int iUserId)
	{
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<TotalConcepto> lResult = new ArrayList<TotalConcepto>();
		System.out.println("En action: " + noEmpresa);
		try
		{
			if (Utilerias.haveSession(WebContextManager.get())) {
			CashFlowBusinessImplements cashFlowBusines = (CashFlowBusinessImplements) contexto.obtenerBean("cashFlowBusiness");
			lResult = cashFlowBusines.graficarFlujoEfectivo(noEmpresa, sFecIni, sFecFin);
			
			//Existen datos a graficar
			if(lResult!=null && lResult.size()>0)
			{
				Map<String, Double> mGrafica = new HashMap<String, Double>();
				//API de creacion de graficas jfreechart.
				CreacionGrafica cg = new CreacionGrafica();
				//Nombre de la grafica
				String sName = noEmpresa+sFecIni.replaceAll("/", "")+sFecFin.replaceAll("/", "")+idReporte;
				
				//Reporte Diario Real /*** CAMBIAR AQUI POR EL NUMERO CORRESPONDIENTE A LA OPCION DEL COMBO 1,2,3,4,5, ...***/
				if(idReporte == 1)
				{
					for(int i=0; i<lResult.size(); i++){
						TotalConcepto conceptoDto = (TotalConcepto)lResult.get(i);
						
						//Grafica: Fecha/Ingresos-Egresos
						if(mGrafica.containsKey(conceptoDto.getFec_valor())){
							double dImporte = mGrafica.get(conceptoDto.getFec_valor());
							mGrafica.remove(conceptoDto.getFec_valor());
							if(conceptoDto.getIngresoegreso().equals("E"))
								mGrafica.put(conceptoDto.getFec_valor(), (dImporte - conceptoDto.getTotal()));
							else
								mGrafica.put(conceptoDto.getFec_valor(), (dImporte + conceptoDto.getTotal()));
						}
						else {
							if(conceptoDto.getIngresoegreso().equals("E"))
								mGrafica.put(conceptoDto.getFec_valor(), (conceptoDto.getTotal()*-1));
							else
								mGrafica.put(conceptoDto.getFec_valor(), conceptoDto.getTotal());
						}
					}
					//cg.crearGraficaPie   (iUserId+"", "FlujoEfectivo"+sName, "F.E. Diario Real Ingresos-Egresos", mGrafica);
					cg.crearGraficaBarras(iUserId+"", "FlujoEfectivo"+sName, "F.E. Diario Real Ingresos-Egresos", "Importe", mGrafica, true);
					cg.crearGraficaLineas(iUserId+"", "FlujoEfectivo"+sName, "F.E. Diario Real Ingresos-Egresos", "Importe", mGrafica, true);
				}
				else if(idReporte == 2) //Reporte Semanal Real /*** CAMBIAR AQUI POR EL NUMERO CORRESPONDIENTE A LA OPCION DEL COMBO 1,2,3,4,5, ...***/
				{
					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
					
					for(int i=0; i<lResult.size(); i++){
						TotalConcepto conceptoDto =(TotalConcepto)lResult.get(i);
						
						//Grafica: Fecha x Semana/Igresos-Egresos
						Date d = formatter.parse(conceptoDto.getFec_valor());
						Calendar c = Calendar.getInstance();
						c.setTime(d);
						System.out.println(d+ "Dia semana: ["+c.get(Calendar.DAY_OF_WEEK)+"] Semana del Aï¿½o: ["+c.get(Calendar.WEEK_OF_YEAR)+"]");
						
						
						if(mGrafica.containsKey("Semana " + c.get(Calendar.WEEK_OF_YEAR))){
							double dImporte = mGrafica.get("Semana " + c.get(Calendar.WEEK_OF_YEAR));
							mGrafica.remove("Semana " + c.get(Calendar.WEEK_OF_YEAR));
							if(conceptoDto.getIngresoegreso().equals("E"))
								mGrafica.put("Semana " + c.get(Calendar.WEEK_OF_YEAR), (dImporte - conceptoDto.getTotal()));
							else
								mGrafica.put("Semana " + c.get(Calendar.WEEK_OF_YEAR), (dImporte + conceptoDto.getTotal()));
						}
						else {
							if(conceptoDto.getIngresoegreso().equals("E"))
								mGrafica.put("Semana " + c.get(Calendar.WEEK_OF_YEAR), (conceptoDto.getTotal()*-1));
							else
								mGrafica.put("Semana " + c.get(Calendar.WEEK_OF_YEAR), conceptoDto.getTotal());
						}
					}
					//cg.crearGraficaPie   (iUserId+"", "FlujoEfectivo"+sName, "F.E. Semanal Real Ingresos-Egresos", mGrafica);
					cg.crearGraficaBarras(iUserId+"", "FlujoEfectivo"+sName, "F.E. Semanal Real Ingresos-Egresos", "Importe", mGrafica, true);
					cg.crearGraficaLineas(iUserId+"", "FlujoEfectivo"+sName, "F.E. Semanal Real Ingresos-Egresos", "Importe", mGrafica, true);
				}
				else if(idReporte == 3) //Reporte Mensual Real /*** CAMBIAR AQUI POR EL NUMERO CORRESPONDIENTE A LA OPCION DEL COMBO 1,2,3,4,5, ...***/
				{
					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
					
					for(int i=0; i<lResult.size(); i++){
						TotalConcepto conceptoDto =(TotalConcepto)lResult.get(i);
						
						//Grafica: Fecha x Mes/Igresos-Egresos
						Date d = formatter.parse(conceptoDto.getFec_valor());
						Calendar c = Calendar.getInstance();
						c.setTime(d);
						String sMes = (c.get(Calendar.MONTH)==0) ? "Enero" : (c.get(Calendar.MONTH)==1) ? "Febrero" :
							(c.get(Calendar.MONTH)==2) ? "Marzo" : (c.get(Calendar.MONTH)==3) ? "Abril" : (c.get(Calendar.MONTH)==4) ? "Mayo" :
							(c.get(Calendar.MONTH)==5) ? "Junio" : (c.get(Calendar.MONTH)==6) ? "Julio" : (c.get(Calendar.MONTH)==7) ? "Agosto" :
							(c.get(Calendar.MONTH)==8) ? "Septiembre" : (c.get(Calendar.MONTH)==9) ? "Octubre" :
							(c.get(Calendar.MONTH)==10) ? "Noviembre" : (c.get(Calendar.MONTH)==11) ? "Diciembre" : "";
						
						if(mGrafica.containsKey(sMes)){
							double dImporte = mGrafica.get(sMes);
							mGrafica.remove(sMes);
							if(conceptoDto.getIngresoegreso().equals("E"))
								mGrafica.put(sMes, (dImporte - conceptoDto.getTotal()));
							else
								mGrafica.put(sMes, (dImporte + conceptoDto.getTotal()));
						}
						else {
							if(conceptoDto.getIngresoegreso().equals("E"))
								mGrafica.put(sMes, (conceptoDto.getTotal()*-1));
							else
								mGrafica.put(sMes, conceptoDto.getTotal());
						}
					}
					//cg.crearGraficaPie   (iUserId+"", "FlujoEfectivo"+sName, "F.E. Mensual Real Ingresos-Egresos", mGrafica);
					cg.crearGraficaBarras(iUserId+"", "FlujoEfectivo"+sName, "F.E. Mensual Real Ingresos-Egresos", "Importe", mGrafica, true);
					cg.crearGraficaLineas(iUserId+"", "FlujoEfectivo"+sName, "F.E. Mensual Real Ingresos-Egresos", "Importe", mGrafica, true);
				}
			}
			}
		}
		catch(Exception e){
			System.err.println(e);
			logger.error(e);
		}
		return lResult;
	}

	/**
	 * Grafica el flujo de efectivo real. Comparativo entre Ingresos - Egresos.
	 * @param noEmpresa
	 * @param sFecIni
	 * @param sFecFin
	 * @param idReporte
	 * @param iUserId
	 * @return
	 */
	@DirectMethod
	public List<TotalConcepto> graficarFEComparativoReal(int noEmpresa, String sFecIni, String sFecFin, int idReporte, int iUserId)
	{
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<TotalConcepto> lResult = new ArrayList<TotalConcepto>();
		try
		{
			if (Utilerias.haveSession(WebContextManager.get())) {
			CashFlowBusinessImplements cashFlowBusines = (CashFlowBusinessImplements) contexto.obtenerBean("cashFlowBusiness");
			lResult = cashFlowBusines.graficarFlujoEfectivo(noEmpresa, sFecIni, sFecFin);
			
			//Existen datos a graficar
			if(lResult!=null && lResult.size()>0)
			{
				Map<String, Double> mGrafica = new TreeMap<String, Double>();
				//Map<String, Double> mGraficaE = new TreeMap<String, Double>();
				//API de creacion de graficas jfreechart.
				CreacionGrafica cg = new CreacionGrafica();
				//Nombre de la grafica
				String sName = noEmpresa+sFecIni.replaceAll("/", "")+sFecFin.replaceAll("/", "")+idReporte;
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				String sAux = new String();
				
				//Reporte Diario Real /*** CAMBIAR AQUI POR EL NUMERO CORRESPONDIENTE A LA OPCION DEL COMBO 1,2,3,4,5, ...***/
				if(idReporte == 1)
				{
					for(int i=0; i<lResult.size(); i++){
						TotalConcepto conceptoDto = (TotalConcepto)lResult.get(i);
						
						sAux = conceptoDto.getFec_valor();
						sAux = sAux.substring(6)+"/"+sAux.substring(3,5)+"/"+sAux.substring(0,2);
						
						//Grafica: Tiempo (diario)/Importe
						if(conceptoDto.getIngresoegreso().equals("I"))
						{
							if(mGrafica.containsKey(sAux+"I")){
								double dImporte = mGrafica.get(sAux+"I");
								mGrafica.remove(sAux+"I");
								mGrafica.put(sAux+"I", (dImporte + conceptoDto.getTotal()));
							}
							else {
								mGrafica.put(sAux+"I", conceptoDto.getTotal());
							}
						}
						else if(conceptoDto.getIngresoegreso().equals("E"))
						{
							if(mGrafica.containsKey(sAux+"E")){
								double dImporte = mGrafica.get(sAux+"E");
								mGrafica.remove(sAux+"E");
								mGrafica.put(sAux+"E", (dImporte + conceptoDto.getTotal()));
							}
							else {
								mGrafica.put(sAux+"E", conceptoDto.getTotal());
							}
						}
					}
				}
				else if(idReporte == 2) //Reporte Semanal Real /*** CAMBIAR AQUI POR EL NUMERO CORRESPONDIENTE A LA OPCION DEL COMBO 1,2,3,4,5, ...***/
				{
					for(int i=0; i<lResult.size(); i++){
						TotalConcepto conceptoDto =(TotalConcepto)lResult.get(i);
						
						//Grafica: Tiempo (Semana)/Importe
						Date d = formatter.parse(conceptoDto.getFec_valor());
						Calendar c = Calendar.getInstance();
						c.setTime(d);
						
						if(c.get(Calendar.WEEK_OF_YEAR)>=10)
							sAux = c.get(Calendar.WEEK_OF_YEAR) + "Semana";
						else
							sAux = "0"+c.get(Calendar.WEEK_OF_YEAR) + "Semana";
						
						if(conceptoDto.getIngresoegreso().equals("I"))
						{
							if(mGrafica.containsKey(sAux+"I")){
								double dImporte = mGrafica.get(sAux+"I");
								mGrafica.remove(sAux+"I");
								mGrafica.put(sAux+"I", (dImporte + conceptoDto.getTotal()));
							}
							else {
								mGrafica.put(sAux+"I", conceptoDto.getTotal());
							}
						}
						else if(conceptoDto.getIngresoegreso().equals("E"))
						{
							if(mGrafica.containsKey(sAux+"E")){
								double dImporte = mGrafica.get(sAux+"E");
								mGrafica.remove(sAux+"E");
								mGrafica.put(sAux+"E", (dImporte + conceptoDto.getTotal()));
							}
							else {
								mGrafica.put(sAux+"E", conceptoDto.getTotal());
							}
						}
					}
				}
				else if(idReporte == 3) //Reporte Mensual Real /*** CAMBIAR AQUI POR EL NUMERO CORRESPONDIENTE A LA OPCION DEL COMBO 1,2,3,4,5, ...***/
				{
					for(int i=0; i<lResult.size(); i++){
						TotalConcepto conceptoDto =(TotalConcepto)lResult.get(i);
						
						//Grafica: Tiempo (Mes)/Importe
						Date d = formatter.parse(conceptoDto.getFec_valor());
						Calendar c = Calendar.getInstance();
						c.setTime(d);
						String sMes = (c.get(Calendar.MONTH)==0) ? "Enero" : (c.get(Calendar.MONTH)==1) ? "Febrero" :
							(c.get(Calendar.MONTH)==2) ? "Marzo" : (c.get(Calendar.MONTH)==3) ? "Abril" : (c.get(Calendar.MONTH)==4) ? "Mayo" :
							(c.get(Calendar.MONTH)==5) ? "Junio" : (c.get(Calendar.MONTH)==6) ? "Julio" : (c.get(Calendar.MONTH)==7) ? "Agosto" :
							(c.get(Calendar.MONTH)==8) ? "Septiembre" : (c.get(Calendar.MONTH)==9) ? "Octubre" :
							(c.get(Calendar.MONTH)==10) ? "Noviembre" : (c.get(Calendar.MONTH)==11) ? "Diciembre" : "";
						
						if(c.get(Calendar.MONTH)>=10)
							sAux = c.get(Calendar.MONTH) + sMes;
						else
							sAux = "0" + c.get(Calendar.MONTH) + sMes;
						
						if(conceptoDto.getIngresoegreso().equals("I"))
						{
							if(mGrafica.containsKey(sAux+"I")){
								double dImporte = mGrafica.get(sAux+"I");
								mGrafica.remove(sAux+"I");
								mGrafica.put(sAux+"I", (dImporte + conceptoDto.getTotal()));
							}
							else {
								mGrafica.put(sAux+"I", conceptoDto.getTotal());
							}
						}
						else if(conceptoDto.getIngresoegreso().equals("E"))
						{
							if(mGrafica.containsKey(sAux+"E")){
								double dImporte = mGrafica.get(sAux+"E");
								mGrafica.remove(sAux+"E");
								mGrafica.put(sAux+"E", (dImporte + conceptoDto.getTotal()));
							}
							else {
								mGrafica.put(sAux+"E", conceptoDto.getTotal());
							}
						}
					}
				}
				//Se llama al API de creacion grafica.
				cg.crearGraficaBarrasFlujo(iUserId+"", "FECompReal"+sName, "Egresos - Ingresos", "Importe", mGrafica, true, idReporte);
				cg.crearGraficaLineasFlujo(iUserId+"", "FECompReal"+sName, "Egresos - Ingresos", "Importe", mGrafica, true, idReporte);
			}
			}
		}
		catch(Exception e){
			System.err.println(e);
			logger.error(e);
		}
		return lResult;
	}

}//END CLASS CashFlowActionImplements
