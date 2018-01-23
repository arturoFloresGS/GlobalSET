package com.webset.set.flujos.business;

import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;

import com.webset.set.flujos.dao.CashFlowNDao;
import com.webset.set.flujos.dto.PosicionBancariaDto;
import com.webset.set.flujos.service.CashFlowNService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;

@SuppressWarnings("unchecked")
public class CashFlowNBusiness implements CashFlowNService {
	Bitacora bitacora = new Bitacora();
	Funciones funciones = new Funciones();
	CashFlowNDao cashFlowNDao;
	
	List<Map<String, String>> paramDatos;
	NumberFormat formato = NumberFormat.getCurrencyInstance(Locale.US);
	DecimalFormat unFormat = new DecimalFormat("######.##");
	
	List<String> list = new ArrayList<String>();
	Map<String, Double> sumTotales = new HashMap();
	Map<String, Double> sumTotales2 = new HashMap();
	Map<String, Double> sumTotCob = new HashMap();
	Map<String, Double> sumTotPag = new HashMap();
	Map<String, Double> sumTotCobI = new HashMap();
	Map<String, Double> sumTotMes = new HashMap();
	Map<String, Double> sumTotPagI = new HashMap();
	Map<String, Double> sumIva = new HashMap();
	String IE;
	int iGlob = 0;
	int diaSem = 0;
	int diaMes = 0;
	GregorianCalendar calFecIni = new GregorianCalendar();
	Date fecIni;
	Date fecFina;
	int fecInicial;
	int fecFinal;
	int semanaDelAnio;
	String fecFin;
	int finMes = 0;
	int cs = 0;
	int rango = 0;
	double sumaSemana = 0;
	double sumaMes = 0;
	
	Map<String, Double> totCobros = new HashMap();
	Map<String, Double> totPagos = new HashMap();
	List<String> listCobros = new ArrayList<String>();
	List<String> listPagos = new ArrayList<String>();
	List<String> listIVA = new ArrayList<String>();
	Map<String, Double> totIVA = new HashMap();
	Map<String, Double> totSinIVA = new HashMap();
	
	String conceptoGlob = "";
	
	public PosicionBancariaDto cashFlowDatos(List<Map<String, String>> parametros) {
		PosicionBancariaDto listResult;
		paramDatos = parametros;
		
		if(Integer.parseInt(paramDatos.get(0).get("tipoReporte")) == 0 || Integer.parseInt(paramDatos.get(0).get("tipoReporte")) == 1)
			listResult = new PosicionBancariaDto(cashFlowDiario());
		else
			listResult = new PosicionBancariaDto(cashFlowMensual());
		
		return listResult;
	}
	
	public List<HashMap> cashFlowMensual() {
		List<HashMap> bodyRow = new ArrayList<HashMap>();
		
		try {
			HashMap cols = new HashMap();
			HashMap cols1 = new HashMap();
			
			datosIngresos(cols1, cols, bodyRow, "I", "COBROS");
			cols = new HashMap();
			datosIngresos(cols1, cols, bodyRow, "E", "PAGOS");
			
			//Coloca los totales del pie de pagina, Saldo Inicial, Total Ingresos, Total Egresos, Saldo Final
			IE = "I";
			cols = new HashMap();
			totalesIniFin(cols1, cols, bodyRow, "SALDO INICIAL",2);
			cols = new HashMap();
			totalIngEgr(cols1, cols, bodyRow, "TOTAL INGRESOS",2);
			
			IE = "E";
			cols = new HashMap();
			totalIngEgr(cols1, cols, bodyRow, "TOTAL EGRESOS",2);
			cols = new HashMap();
			totalesIniFin(cols1, cols, bodyRow, "SALDO FINAL",2);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:CashFlowNBusiness, M:cashFlowMensual");
		}
		return bodyRow;
	}
	
	public List<HashMap> cashFlowDiario() {
		List<HashMap> bodyRow = new ArrayList<HashMap>();
		
		try {
			HashMap cols = new HashMap();
			HashMap cols1 = new HashMap();
			
			datosIngresosDiarios(cols1, cols, bodyRow, "I", "COBROS");
			cols = new HashMap();
			datosIngresosDiarios(cols1, cols, bodyRow, "E", "PAGOS");
			
			//Coloca los totales del pie de pagina, Saldo Inicial, Total Ingresos, Total Egresos, Saldo Final
			IE = "I";
			cols = new HashMap();
			totalesIniFin(cols1, cols, bodyRow, "SALDO INICIAL", 0);
			cols = new HashMap();
			totalIngEgr(cols1, cols, bodyRow, "TOTAL INGRESOS",0);
			
			IE = "E";
			cols = new HashMap();
			totalIngEgr(cols1, cols, bodyRow, "TOTAL EGRESOS",0);
			cols = new HashMap();
			totalesIniFin(cols1, cols, bodyRow, "SALDO FINAL",0);
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:CashFlowNBusiness, M:cashFlowMensual");
		}
		return bodyRow;
	}
	
	@SuppressWarnings("unused")
	public void datosIngresos(HashMap cols1,HashMap cols, List<HashMap> bodyRow, String tipoMovto, String titulo) {
		List<PosicionBancariaDto> listI = new ArrayList<PosicionBancariaDto>();
		int idTipoC;
		int Concepto;
		int idTipoR;
		double total=0;
		double totalDG=0;
		double totalAG=0;
		double aux = 0;
		double auxIva = 0;
		double neto = 0;
		int i;
		String cobro = "";
		int year = Integer.parseInt(paramDatos.get(0).get("fecIni").substring(0, 4));
		IE = tipoMovto;
		
		try {
			listI = cashFlowNDao.cashFlowDatos(paramDatos, IE);
			
			if(listI.size() != 0) {
				idTipoC = listI.get(0).getIdTipoConcepto();
				Concepto = listI.get(0).getIdTipoConcepto();
				idTipoR = Integer.parseInt(listI.get(0).getIdRubro());
				
				cols.put("descripcion", listI.get(0).getDescripcion());
//				colocaMesesGrid(cols);
				bodyRow.add(cols);
				cols = new HashMap();
				
				for(i=0; i<listI.size(); i++) {
					if(idTipoC != listI.get(i).getIdTipoConcepto()) {
						idTipoC = listI.get(i).getIdTipoConcepto();
						idTipoR = Integer.parseInt(listI.get(i).getIdRubro());
						cols.put("totales", formato.format(total));
						bodyRow.add(cols);
						
						cols = new HashMap();
						cols.put("descripcion", "Total " + listI.get(i-1).getDescripcion().substring(3, listI.get(i-1).getDescripcion().length()));
						cols.put("totales", formato.format(aux));
						colocaTotalesGrid("", 0.0, true, cols,"N");
						
						totalAG += aux;
						totalDG = 0;
						total = 0;
						
						cobro = listI.get(i-1).getDescripcion().substring(3, listI.get(i-1).getDescripcion().length());
						bodyRow.add(cols);
						cols1 = new HashMap();
						if(cobro.equals("Cobros Operativos"))
							cols1.put("descripcion", "Iva a Cobrar");
						else
							cols1.put("descripcion", "Iva a Pagar");
						cols1.put("totales", formato.format(auxIva));
						
						colocaTotalesGrid("", 0.0, true, cols1,"iva");
						
						cols = new HashMap();
						bodyRow.add(cols);

						aux = 0;
						cols = new HashMap();
						cols.put("idRubro", listI.get(i).getIdRubro());
						cols.put("descripcion", listI.get(i).getDescripcion());
//						colocaMesesGrid(cols);
						bodyRow.add(cols);
						cols = new HashMap();
						cols.put("idRubro", listI.get(i).getIdRubro());
						cols.put("descripcion", listI.get(i).getDescRubro());
						cols.put("col" + listI.get(i).getMes() + "" + listI.get(i).getAnio(), formato.format(listI.get(i).getImporte()));
						colocaTotalesGrid("col" + listI.get(i).getMes() + "" + listI.get(i).getAnio(), listI.get(i).getImporte(), false, cols,"");
						total += listI.get(i).getImporte();
						totalDG = 0;
						totalDG += total;
						aux += listI.get(i).getImporte();
					}else if(idTipoR != Integer.parseInt(listI.get(i).getIdRubro())) {
						if(Concepto == idTipoC){
							idTipoR = Integer.parseInt(listI.get(i).getIdRubro());
							cols.put("totales", formato.format(total));
							total = 0;
							neto = 0;
							bodyRow.add(cols);
							cols = new HashMap();
							neto = (listI.get(i).getImporte())/(1.16);
							cols.put("idRubro", listI.get(i).getIdRubro());
							cols.put("descripcion", listI.get(i).getDescRubro());
							cols.put("col" + listI.get(i).getMes() + "" + listI.get(i).getAnio(), formato.format(neto));
							if(idTipoC == Concepto)
								colocaTotalesGrid("col" + listI.get(i).getMes() + "" + listI.get(i).getAnio(), neto, false, cols,"c");
							else
								colocaTotalesGrid("col" + listI.get(i).getMes() + "" + listI.get(i).getAnio(), neto, false, cols,"");
							total += neto;
							totalDG = 0;
							totalDG += total;
							aux += neto;
							auxIva += neto * 0.16;
							
						}else{
							idTipoR = Integer.parseInt(listI.get(i).getIdRubro());
							cols.put("totales", formato.format(total));
							total = 0;
							bodyRow.add(cols);
							cols = new HashMap();
							cols.put("idRubro", listI.get(i).getIdRubro());
							cols.put("descripcion", listI.get(i).getDescRubro());
							cols.put("col" + listI.get(i).getMes() + "" + listI.get(i).getAnio(), formato.format(listI.get(i).getImporte()));
							colocaTotalesGrid("col" + listI.get(i).getMes() + "" + listI.get(i).getAnio(), listI.get(i).getImporte(), false, cols,"");
							total += listI.get(i).getImporte();
							totalDG = 0;
							totalDG += total;
							aux += listI.get(i).getImporte();
						}
					}else {
						if(Concepto == idTipoC){
							neto = (listI.get(i).getImporte())/(1.16);
							cols.put("idRubro", listI.get(i).getIdRubro());
							cols.put("descripcion", listI.get(i).getDescRubro());
							cols.put("col" + listI.get(i).getMes() + "" + listI.get(i).getAnio(), formato.format(neto));
							if(idTipoC == Concepto)
								colocaTotalesGrid("col" + listI.get(i).getMes() + "" + listI.get(i).getAnio(), neto, false, cols,"c");
							else
								colocaTotalesGrid("col" + listI.get(i).getMes() + "" + listI.get(i).getAnio(), neto, false, cols,"");
							total += neto;
							totalDG = 0;
							totalDG += total;
							aux += neto;
							auxIva += neto * 0.16;
						}else{
							cols.put("idRubro", listI.get(i).getIdRubro());
							cols.put("descripcion", listI.get(i).getDescRubro());
							cols.put("col" + listI.get(i).getMes() + "" + listI.get(i).getAnio(), formato.format(listI.get(i).getImporte()));
							colocaTotalesGrid("col" + listI.get(i).getMes() + "" + listI.get(i).getAnio(), listI.get(i).getImporte(), false, cols,"");
							total += listI.get(i).getImporte();
							totalDG = 0;
							totalDG += total;
							aux += listI.get(i).getImporte();
						}
					}
				}
				cols.put("totales", formato.format(total));
				cols.put("totales", formato.format(totalDG));
				totalDG = 0;
				totalDG += total;
				totalAG += aux + auxIva;
				bodyRow.add(cols);			
				bodyRow.add(cols1);
				
				cols = new HashMap();
				cols.put("descripcion", "Total " + listI.get(i-1).getDescripcion().substring(3, listI.get(i-1).getDescripcion().length()));
				cols.put("totales",  formato.format(aux + auxIva));
				
				colocaTotalesGrid("", 0.0, true, cols,"");
				
				bodyRow.add(cols);
				
				cols = new HashMap();
				bodyRow.add(cols);
				cols = new HashMap();
				cols.put("descripcion", "TOTAL " + titulo);
				cols.put("totales", formato.format(totalAG));
				
				colocaTotGlobalGrid(i, true, cols,"T");
				bodyRow.add(cols);
				cols = new HashMap();
				bodyRow.add(cols);
				auxIva = 0;
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:CashFlowNBusiness, M:datosIngresos");
		}
	}
	
	public void colocaTotalesGrid(String nomCol, double importe, boolean bTot, HashMap cols,String iva) {
		if(iva == "iva")
		{
			for(int i=0; i<list.size(); i++) {
				if(sumIva.containsKey(list.get(i))) {
					cols.put(list.get(i), formato.format(sumIva.get(list.get(i))));
					colocaTotGlobalGrid(i, false, cols, iva);
				}
			}
		}else{
			if(bTot) {
				if(sumTotales.size() >= sumIva.size()){
					for(int i=0; i<list.size(); i++) {
						if(sumTotales.containsKey(list.get(i))) {
							if(iva == "N"){
								cols.put(list.get(i), formato.format(sumTotales.get(list.get(i))));
								colocaTotGlobalGrid(i, false, cols,"");
							}else{
								if(sumTotales.containsKey(list.get(i)) == sumIva.containsKey(list.get(i))){
									sumTotales2.put(list.get(i), sumTotales.get(list.get(i)) + sumIva.get(list.get(i)));
									cols.put(list.get(i), formato.format(sumTotales.get(list.get(i)) + sumIva.get(list.get(i))));
									colocaTotGlobalGrid(i, false, cols,"");
								}else{
									if(sumIva.containsKey(list.get(i)))
										sumTotales2.put(list.get(i), sumIva.get(list.get(i)));
									else
										sumTotales2.put(list.get(i), sumTotales.get(list.get(i)));
									
									cols.put(list.get(i), formato.format(sumTotales.get(list.get(i))));
									colocaTotGlobalGrid(i, false, cols,"");
								}
							}
						}
					}
				}else{
					for(int i=0; i<list.size(); i++) {
						if(sumIva.containsKey(list.get(i)) && sumTotales.containsKey(list.get(i))) {
							if(iva == "N"){
								cols.put(list.get(i), formato.format(sumTotales.get(list.get(i))));
								colocaTotGlobalGrid(i, false, cols,"");
							}else{
								if(sumTotales.containsKey(list.get(i)) == sumIva.containsKey(list.get(i))){
									sumTotales2.put(list.get(i), sumTotales.get(list.get(i)) + sumIva.get(list.get(i)));
									cols.put(list.get(i), formato.format(sumTotales.get(list.get(i)) + sumIva.get(list.get(i))));
									colocaTotGlobalGrid(i, false, cols,"");
								}else{
									if(sumIva.containsKey(list.get(i)))
										sumTotales2.put(list.get(i), sumIva.get(list.get(i)));
									else
										sumTotales2.put(list.get(i), sumTotales.get(list.get(i)));
									
									cols.put(list.get(i), formato.format(sumIva.get(list.get(i))));
									colocaTotGlobalGrid(i, false, cols,"O");
								}
							}
						}
					}
				}
				sumTotales = new HashMap(); 
			}else {
				if(!sumTotales.containsKey(nomCol)) {
					if(!list.contains(nomCol))
						list.add(list.size(), nomCol);
					sumTotales.put(nomCol, importe);
					if (iva == "c")
						sumIva.put(nomCol,(sumTotales.get(nomCol)) * (0.16));
				}else{
					sumTotales.put(nomCol, importe + sumTotales.get(nomCol));
					if (iva == "c")
						sumIva.put(nomCol,(sumTotales.get(nomCol)) * (0.16));
					
					}
			}
		}
	}
//totalessssss	
	public void colocaMesesGrid(HashMap cols) {
		int iMesIni = Integer.parseInt(paramDatos.get(0).get("fecIni"));
		int iMesFin = Integer.parseInt(paramDatos.get(0).get("fecFin"));
		String mesGrid;
		
		for(int i=0; i<=(iMesFin - iMesIni); i++) {
			mesGrid = funciones.nombreMes(iMesIni + i).toUpperCase();
			cols.put("col" + (iMesIni + i), mesGrid.substring(0, 3));
		}
		cols.put("totales", "TOTAL");
	}
	
	public void totalesIniFin(HashMap cols1, HashMap cols, List<HashMap> bodyRow, String tit1,int tipoRep) {
		List<PosicionBancariaDto> listSdoChequera = new ArrayList<PosicionBancariaDto>();
		Date fecIniDate = funciones.ponerFechaDate(funciones.retornaFecha(funciones.cambiarFecha(paramDatos.get(0).get("fecIni"), true)));
		String fecIni = funciones.cambiarFecha(paramDatos.get(0).get("fecIni")).substring(3, 10);
		String fecFin = funciones.cambiarFecha(paramDatos.get(0).get("fecFin")).substring(3, 10);
		boolean unicaVez = false;
		int i=0;
		
		GregorianCalendar calFecIni = new GregorianCalendar();
		GregorianCalendar calFecha = new GregorianCalendar();
		
		try {
			cols.put("descripcion", tit1);
			
			if(IE.equals("E")) {
				calFecIni.setTime(fecIniDate);
				finMes = calFecIni.getActualMaximum(Calendar.DAY_OF_MONTH);
				fecIniDate = funciones.ponerFechaDate(finMes + funciones.cambiarFecha(paramDatos.get(0).get("fecIni"), true).substring(2, 10));
			}
			
			while (!unicaVez) {
				while(listSdoChequera.size() == 0) {
					//Buscamos el saldo inicial de las chequeras
					listSdoChequera = cashFlowNDao.totalSdoChequera(paramDatos, fecIniDate,tipoRep);
					
					if(listSdoChequera.size() == 0) {
						if(IE.equals("I"))
							fecIniDate = funciones.modificarFecha("d", 1, fecIniDate);
						else
							fecIniDate = funciones.modificarFecha("d", -1, fecIniDate);
					}
				}
				for(i=0; i<listSdoChequera.size(); i++) {
					if(IE.equals("I"))
						cols.put(listSdoChequera.get(i).getNomCol(), formato.format(listSdoChequera.get(i).getSdoIni()).replace("(", "").replace(")", ""));
					else
						cols.put(listSdoChequera.get(i).getNomCol(), formato.format(listSdoChequera.get(i).getSdoFin()).replace("(", "").replace(")", ""));
				}
				listSdoChequera = new ArrayList<PosicionBancariaDto>();
				if(fecIni.equals(fecFin)) unicaVez = true;
				
				calFecha.setTime(fecIniDate);
				
				if(calFecha.MONTH == 12)
					fecIniDate = funciones.modificarFecha("y", 1, fecIniDate);
				
				fecIniDate = funciones.modificarFecha("m", 1, fecIniDate);
				
				if(IE.equals("I")) {
					fecIniDate = funciones.ponerFechaDate(funciones.retornaFecha(funciones.ponerFechaSola(fecIniDate)));
				}else {
					calFecIni.setTime(fecIniDate);
					finMes = calFecIni.getActualMaximum(Calendar.DAY_OF_MONTH);
					fecIniDate = funciones.ponerFechaDate(finMes + funciones.ponerFechaSola(fecIniDate).substring(2, 10));
				}
				fecIni = funciones.ponerFechaSola(fecIniDate).substring(3, 10);
			}
			bodyRow.add(cols);
		}catch(Exception e) {bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:CashFlowNBusiness, M:totalesIniFin");}
	}
	
	public void totalIngEgr(HashMap cols1, HashMap cols, List<HashMap> bodyRow, String tit1,int tipoRep) {
		List<PosicionBancariaDto> listSdoIE = new ArrayList<PosicionBancariaDto>();
		int i=0;
		
		try {
			cols.put("descripcion", tit1);
			
			//Total de Ingresos y Egresos
			listSdoIE = cashFlowNDao.totalIngresosEgresos(paramDatos, IE,tipoRep);
			
			if(listSdoIE.size() != 0) {
				for(i=0; i<listSdoIE.size(); i++)
					cols.put(listSdoIE.get(i).getNomCol().toString(), formato.format(listSdoIE.get(i).getImporte()));
				bodyRow.add(cols);
			}
		}catch(Exception e) {bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:CashFlowNBusiness, M:totalIngEgr");}
	}
	
	public String generaExcel(List<Map<String, String>> parametros) {
		List<HashMap> body = new ArrayList<HashMap>();
		String nomCampo = "descripcion";
		String nomArch;
		int y;
		int a = 5;
		int iMeses = 0;
		int yearIni = 0;
		int mesIni = 0;
		String cadena = "";
		String subcadena = "";
		String subletra = "";
		String ivaacobrar = "";
		boolean inicia = false;
		boolean bpagos = false;
		String mesGrid = "";
		
		try {
			paramDatos = parametros;
			
			int iMesIni = Integer.parseInt(paramDatos.get(0).get("fecIni").substring(5, 7));
			int iMesFin = Integer.parseInt(paramDatos.get(0).get("fecFin").substring(5, 7));
			int year = 0;
			
			body = cashFlowMensual();
			
			//Se crea el libro Excel
			HSSFWorkbook wb = new HSSFWorkbook();
	        //Se crea una nueva hoja dentro del libro
	        HSSFSheet sheet = wb.createSheet("CashFlow");
	        //Se crea una fila dentro de la hoja
	        HSSFPatriarch patr = sheet.createDrawingPatriarch();

	        HSSFFont fuente = wb.createFont();
	        fuente.setFontHeightInPoints((short)10);
	        fuente.setFontName("Calibri");

	        HSSFFont fuente_t = wb.createFont();
	        fuente_t.setFontHeightInPoints((short)16);
	        fuente_t.setFontName("Calibri");
	        fuente_t.setColor((short)1);
	        fuente_t.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

	        //Negro = 0
	        //Blanco = 1
	        //Rojo = 2
	        //Verde = 3
	        //Azul = 4
	        //Amarillo = 5
	        //Magenta = 6
	        //Cyan = 7
	        
	        HSSFFont fuente_r = wb.createFont();
	        fuente_r.setFontHeightInPoints((short)12);
	        fuente_r.setFontName("Calibri");
	        fuente_r.setColor((short)1);
	        fuente_r.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	        
	        HSSFFont fuente_l = wb.createFont();
	        fuente_l.setFontHeightInPoints((short)10);
	        fuente_l.setFontName("Calibri");
	        fuente_l.setColor((short)1);
	        fuente_l.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	        
	        HSSFFont fuente_n = wb.createFont();
	        fuente_n.setFontHeightInPoints((short)10);
	        fuente_n.setFontName("Calibri");
	        fuente_n.setColor((short)0);
	     //   fuente_n.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);


	        HSSFFont fuente_cyan = wb.createFont();
	        fuente_cyan.setFontHeightInPoints((short)10);
	        fuente_cyan.setFontName("Calibri");
	        fuente_cyan.setColor((short)7);
	        fuente_cyan.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	        
	        HSSFFont fuente_rojo = wb.createFont();
	        fuente_rojo.setFontHeightInPoints((short)10);
	        fuente_rojo.setFontName("Calibri");
	        fuente_rojo.setColor((short)2);
	        fuente_rojo.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

	        //fuente.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	        HSSFDataFormat format = wb.createDataFormat();
	        HSSFCellStyle cellStyle = wb.createCellStyle();
	        cellStyle.setDataFormat(format.getFormat("#,##0.00"));
	        cellStyle.setFont(fuente);
	        
	        HSSFDataFormat format1 = wb.createDataFormat();
	        HSSFCellStyle cellVerde = wb.createCellStyle();
	        cellVerde.setDataFormat(format1.getFormat("#,##0.00"));
	        cellVerde.setFillForegroundColor(IndexedColors.GREEN.getIndex());
	        cellVerde.setFillPattern(CellStyle.SOLID_FOREGROUND);
	        cellVerde.setFont(fuente_l);
	        
	        HSSFDataFormat format2 = wb.createDataFormat();
	        HSSFCellStyle cellAmarillo = wb.createCellStyle();
	        cellAmarillo.setDataFormat(format2.getFormat("#,##0.00"));
	        cellAmarillo.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	        cellAmarillo.setFillPattern(CellStyle.SOLID_FOREGROUND);
	        cellAmarillo.setFont(fuente_rojo);

	        HSSFDataFormat format3 = wb.createDataFormat();
	        HSSFCellStyle cellCyan = wb.createCellStyle();
	        cellCyan.setDataFormat(format3.getFormat("#,##0.00"));
	        cellCyan.setFillForegroundColor(IndexedColors.AQUA.getIndex());
	        cellCyan.setFillPattern(CellStyle.SOLID_FOREGROUND);
	        cellCyan.setFont(fuente_n);

	        HSSFDataFormat format4 = wb.createDataFormat();
	        HSSFCellStyle cellAzul = wb.createCellStyle();
	        cellAzul.setDataFormat(format3.getFormat("#,##0.00"));
	        cellAzul.setFillForegroundColor(IndexedColors.BLUE.getIndex());
	        cellAzul.setFillPattern(CellStyle.SOLID_FOREGROUND);
	        cellAzul.setFont(fuente_l);
	        
	        // Luego creamos el objeto que se encargar� de aplicar el estilo a la celda
	        HSSFCellStyle estiloCelda = wb.createCellStyle();
	        estiloCelda.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
	        estiloCelda.setFont(fuente);

	        HSSFCellStyle estiloCeldaDesc = wb.createCellStyle();
	        estiloCeldaDesc.setAlignment(HSSFCellStyle.ALIGN_LEFT);
	        estiloCeldaDesc.setFont(fuente);
	        estiloCeldaDesc.setFillForegroundColor(IndexedColors.GREEN.getIndex());
	        estiloCeldaDesc.setFillPattern(CellStyle.SOLID_FOREGROUND);
	        
	        HSSFCellStyle estiloCeldaDesc_T = wb.createCellStyle();
	        estiloCeldaDesc_T.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	        estiloCeldaDesc_T.setFont(fuente_t);
	        estiloCeldaDesc_T.setFillForegroundColor(IndexedColors.GREEN.getIndex());
	        estiloCeldaDesc_T.setFillPattern(CellStyle.SOLID_FOREGROUND);

	        HSSFCellStyle estiloCeldaDesc_R = wb.createCellStyle();
	        estiloCeldaDesc_R.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	        estiloCeldaDesc_R.setFont(fuente_r);
	        estiloCeldaDesc_R.setFillForegroundColor(IndexedColors.GREEN.getIndex());
	        estiloCeldaDesc_R.setFillPattern(CellStyle.SOLID_FOREGROUND);

	        HSSFCellStyle estiloCeldaDesc_l = wb.createCellStyle();
	        estiloCeldaDesc_l.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	        estiloCeldaDesc_l.setFont(fuente_l);
	        estiloCeldaDesc_l.setFillForegroundColor(IndexedColors.GREEN.getIndex());
	        estiloCeldaDesc_l.setFillPattern(CellStyle.SOLID_FOREGROUND);

	        HSSFCellStyle estiloCeldaDesc_d = wb.createCellStyle();
	        estiloCeldaDesc_d.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
	        estiloCeldaDesc_d.setFont(fuente_l);
	        estiloCeldaDesc_d.setFillForegroundColor(IndexedColors.GREEN.getIndex());
	        estiloCeldaDesc_d.setFillPattern(CellStyle.SOLID_FOREGROUND);
	        
	        HSSFCellStyle estiloCeldaRojo = wb.createCellStyle();
	        estiloCeldaRojo.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
	        estiloCeldaRojo.setFont(fuente_rojo);

	        HSSFCellStyle estiloVerde = wb.createCellStyle();
	        estiloVerde.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
	        estiloVerde.setFont(fuente_l);
	        estiloVerde.setFillForegroundColor(IndexedColors.GREEN.getIndex());
	        estiloVerde.setFillPattern(CellStyle.SOLID_FOREGROUND);

	        HSSFCellStyle estiloPagos = wb.createCellStyle();
	        estiloPagos.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	        estiloPagos.setFont(fuente_l);
	        estiloPagos.setFillForegroundColor(IndexedColors.BLUE.getIndex());
	        estiloPagos.setFillPattern(CellStyle.SOLID_FOREGROUND);
	                    
	        for(int i=0; i<body.size(); i++) 
	        	{ 
		        	inicia = false;
		        	iMeses = 0;
		        	iMesIni = Integer.parseInt(paramDatos.get(0).get("fecIni").substring(5, 7));
		        	year = Integer.parseInt(paramDatos.get(0).get("fecIni").substring(0, 4));
		        	
		        	if(Integer.parseInt(paramDatos.get(0).get("fecIni").substring(0, 4)) == Integer.parseInt(paramDatos.get(0).get("fecFin").substring(0, 4)))
						 iMeses = (iMesFin - iMesIni);
					else {
						yearIni = Integer.parseInt(paramDatos.get(0).get("fecIni").substring(0, 4));
						mesIni = iMesIni;
						
						while (yearIni < Integer.parseInt(paramDatos.get(0).get("fecFin").substring(0, 4)) || (mesIni != iMesFin)){
							if(mesIni == 12) {
								mesIni = 1;
								iMeses++;
								yearIni++;
							}else {
								mesIni++;
								iMeses++;
							}
						}
					}

		        	HSSFRow row2 = sheet.createRow((short)a++);
		        	HSSFCell celda = row2.createCell((short)0);

		        	if(body.get(i).get("descripcion") != null) {
		                cadena = body.get(i).get("descripcion").toString();

		                if(cadena.length() == 12) {		            
		                	ivaacobrar = cadena.substring(0,12).toUpperCase();
		                }

		                
		                if(cadena.length() == 0) {		            
		                	subletra = " ";
		                }
		                
		                if(cadena.length() > 5) {		            
		                	subcadena = cadena.substring(0,5).toUpperCase();
		                	subletra = cadena.substring(0,1);

		                }
		                else {
		                	subcadena = "";
		                	subletra = "";
		                }

		                if(!bpagos && cadena.length() >= 8) {		  
		                	if(cadena.substring(3,8).toUpperCase().equals("PAGOS")) {
		                		bpagos = true;
		                	}
		                }
		                	
		        		if(i==0 || subcadena.equals("TOTAL") || subletra.equals("1") || subletra.equals("2") || subletra.equals("3") || subletra.equals("4")) { 
		        			if(bpagos == false) {
		        			   celda.setCellStyle(estiloCeldaDesc_l);     
		        			   }
		        			else if(bpagos == true) { 
		        				celda.setCellStyle(estiloPagos);///pagos estiloPagos
		        				} 
		        			}
		        		else 
		        			{	
		        			celda.setCellStyle(estiloCeldaRojo); 
		        			}

		        		celda.setCellValue(cadena); 
		        		
     		        	y = 0;

     		        	for(int x=0; x<=(iMeses); x++) {
			    			y++;
			    			
			    			if(x == 0 && i == 0) {
			    				//inicio	        
			    		        HSSFRow row = sheet.createRow((short)a-5);
			    	        	HSSFCell cell = row.createCell((short)0);
			    	            cell.setCellValue("GRUPO CAABSA");
			    	            cell.setCellStyle(estiloCeldaDesc_T);
			    	            sheet.addMergedRegion(new Region(a-5,(short)0,a-5,(short)(iMeses + 2)));
			    	            
			    		        HSSFRow row3 = sheet.createRow((short)a-4);
			    	        	HSSFCell cell3 = row3.createCell((short)0);
			    	            cell3.setCellValue("FLUJO DE EFECTIVO");
			    	            cell3.setCellStyle(estiloCeldaDesc_T);
			    	            sheet.addMergedRegion(new Region(a-4,(short)0,a-4,(short)(iMeses + 2)));
			    	            
			    		        HSSFRow row4 = sheet.createRow((short)a-3);
			    	        	HSSFCell cell4 = row4.createCell((short)0);
			    	            cell4.setCellValue("Costo");
			    	            cell4.setCellStyle(estiloCeldaDesc_R);
			    	            sheet.addMergedRegion(new Region(a-3,(short)0,a-3,(short)(iMeses + 2)));
			    			}
			    			
			    			
			    			
							if(!inicia) {
								nomCampo = "col" + (iMesIni + x) + year;
								mesGrid = funciones.nombreMes(iMesIni + x) + year;
							}else {
								iMesIni++;
								nomCampo = "col" + (iMesIni) + year;
								mesGrid = funciones.nombreMes(iMesIni) + year;
							}
			    			if(body.get(i).get(nomCampo) != null) {
			    				celda = row2.createCell((short)y);
			    				if(subcadena.equals("TOTAL")) { 
				    				if(bpagos == false) {
					        			celda.setCellStyle(cellVerde);     
					        		} else if(bpagos == true) { 
					        			celda.setCellStyle(cellAzul);
					        		} 
			    				} else if(ivaacobrar.equals("IVA A COBRAR")) { 
					    			celda.setCellStyle(cellAmarillo); 
			    				} else {
					    			celda.setCellStyle(cellStyle); 
				    			}
			    				celda.setCellValue(Double.parseDouble(body.get(i).get(nomCampo).toString().replace("$", "").replace(",", "")));
			    			}else if(body.get(i).get(nomCampo) == null && subcadena.equals("TOTAL")) { 
			    				celda = row2.createCell((short)y);
			    				if(bpagos == false) {
				        			celda.setCellStyle(cellVerde);     
				        		} else if(bpagos == true) { 
				        			celda.setCellStyle(cellAzul);
				        		} 
				        		celda.setCellValue("  ");
			    			}else if(body.get(i).get(nomCampo) == null && ivaacobrar.equals("IVA A COBRAR")) {  
			    				celda = row2.createCell((short)y);
				    			celda.setCellStyle(cellAmarillo); 
				        		celda.setCellValue("  ");
			    			}else if(i == 0) {
			    				celda = row2.createCell((short)y);
			    				celda.setCellStyle(estiloCeldaDesc_l); 
				        		celda.setCellValue(mesGrid);
			    			}else if(subletra.equals("1") || subletra.equals("2") || subletra.equals("3") || subletra.equals("4")) {
			    				celda = row2.createCell((short)y);
			    				celda.setCellStyle(estiloCeldaDesc_l);     
		    					if(bpagos == true) { 
			    					celda.setCellStyle(estiloPagos);
			    				} 
				        		celda.setCellValue(" ");
		        			}	
			    			
			    			if((iMesIni + x) == 12) {
								year++;
								iMesIni = 0;
								inicia = true;
							}
				        }
			    		y++;

			    		if(body.get(i).get("totales") != null) {
			    			celda = row2.createCell((short)y);
			    			if(subcadena.equals("TOTAL")) { 
			    				if(bpagos == false) {
				        			celda.setCellStyle(cellVerde);     
				        		} else if(bpagos == true) { 
				        			celda.setCellStyle(cellAzul);
				        		} 
			    			} else {
			    			celda.setCellStyle(cellCyan); 
			    			}
			    			celda.setCellValue(Double.parseDouble(body.get(i).get("totales").toString().replace("$", "").replace(",", "")));
			    			celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			    		}
			    		
			    		if(i==0) {  
			    			celda = row2.createCell((short)y);
			        		celda.setCellStyle(estiloCeldaDesc_l);
			        		celda.setCellValue("TOTALES");

			    		} else if(subletra.equals("1") || subletra.equals("2") || subletra.equals("3") || subletra.equals("4")) {
			    			celda = row2.createCell((short)y);
	    					celda.setCellStyle(estiloCeldaDesc_l);     
	    					if(bpagos == true) { 
		    					celda.setCellStyle(estiloPagos);
		    				} 
		       				celda.setCellValue(" ");
			    		}	
		        	}
		        	else {  
     		        	for(int x=0; x<=(iMeses+ 2) ; x++) {
			    			celda = row2.createCell((short)x);
				    		celda.setCellStyle(cellCyan); 
				        	celda.setCellValue(" ");
     		        	}
		        	}
	        }

	        //SE FIJA RL TAMA�O 
            for(int x=0; x<=(iMeses+2); x++) {
                sheet.autoSizeColumn((short)x);
            	}
            
	        nomArch = cashFlowNDao.configuraSET(3008) + "CashFlow" + ".xls";
	        
	        //Escribimos los resultados a un fichero Excel
	        FileOutputStream fileOut = new FileOutputStream(nomArch);
	        wb.write(fileOut);
	        fileOut.close();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:CashFlowNBusiness, M:generaExcel");
		}
        return "Cash flow exportados en excel";
	}
	
	public List<PosicionBancariaDto> consultarDetalleFlujo(List<Map<String, String>> parametros, String idRubro) {
		return cashFlowNDao.consultarDetalleFlujo(parametros, idRubro);
	}
	
	public void datosIngresosDiarios(HashMap cols1,HashMap cols, List<HashMap> bodyRow, String tipoMovto, String titulo) {
		List<PosicionBancariaDto> listI = new ArrayList<PosicionBancariaDto>();
		int idTipoC;
		int idTipoR;
		int i = 0;
		IE = tipoMovto;
		String fecValor = "";
		String titTotal;
		fecIni = funciones.ponerFechaDate(funciones.cambiarFecha(paramDatos.get(0).get("fecIni")));
		calFecIni.setTime(fecIni);
		fecInicial = calFecIni.get(Calendar.WEEK_OF_YEAR);
		fecFina = funciones.ponerFechaDate(funciones.cambiarFecha(paramDatos.get(0).get("fecFin")));
		calFecIni.setTime(fecFina);
		fecFinal = calFecIni.get(Calendar.WEEK_OF_YEAR);
		
		try {
			fecFin = funciones.cambiarFecha(paramDatos.get(0).get("fecFin")).substring(0,10);
			listI = cashFlowNDao.cashFlowDatosDiarios(paramDatos, IE);
			
			if(listI.size() != 0) {
				if(IE.equals("E")) {
					totSinIVA = new HashMap();
					totIVA = new HashMap();
					listIVA = new ArrayList<String>();
				}
				idTipoC = listI.get(0).getIdTipoConcepto();
				idTipoR = Integer.parseInt(listI.get(0).getIdRubro());
				
				cols.put("descripcion", listI.get(0).getDescripcion());
				bodyRow.add(cols);
				cols = new HashMap();
				
				for(i=0; i<listI.size(); i++) {
					fecValor = funciones.cambiarFecha(listI.get(i).getFecValor()).substring(0, 10);
					if(!conceptoGlob.equals(listI.get(i).getDescripcion())) 
						conceptoGlob = listI.get(i).getDescripcion();
					sumTotalCobPag("col" + fecValor, listI.get(i).getImporte());
					
					if(idTipoC != listI.get(i).getIdTipoConcepto()) {
						idTipoC = listI.get(i).getIdTipoConcepto();
						idTipoR = Integer.parseInt(listI.get(i).getIdRubro());
						titTotal = listI.get(i-1).getDescripcion().substring(3, listI.get(i-1).getDescripcion().length());

						bodyRow.add(cols);
						if(titTotal.equals("Otros cobros")){
							colocaTotalSinIVA(cols, "IVA a cobrar", bodyRow, titTotal);
						}else if(titTotal.equals("Otros pagos")){
							colocaTotalSinIVA(cols, "IVA a pagar", bodyRow, titTotal);
						}else{
							colocaTituloTotalGrupo(cols, titulo, bodyRow, titTotal);
						}
						cols = new HashMap();
						bodyRow.add(cols);
						cols = new HashMap();
						//cols.put("idRubro", listI.get(i).getIdRubro());
						cols.put("descripcion", listI.get(i).getDescripcion());
						bodyRow.add(cols);
						cols = new HashMap();
					}else if(idTipoR != Integer.parseInt(listI.get(i).getIdRubro())) {
							idTipoR = Integer.parseInt(listI.get(i).getIdRubro());
							bodyRow.add(cols);
							cols = new HashMap();
					}

						colocaImporte(cols, i, listI, "col" + fecValor);
					
				}
				bodyRow.add(cols);			
				titTotal = listI.get(i-1).getDescripcion().substring(3, listI.get(i-1).getDescripcion().length());
		//aqui		
				if(titTotal.equals("Otros cobros"))
					colocaTotalSinIVA(cols, "IVA a cobrar", bodyRow, titTotal);
				else if(titTotal.equals("Otros pagos"))
					colocaTotalSinIVA(cols, "IVA a pagar", bodyRow, titTotal);
				
				cols = new HashMap();
				bodyRow.add(cols);
				cols = new HashMap();
				cols.put("descripcion", "TOTAL " + titulo);
				colocaTotalCobPag(cols);
				bodyRow.add(cols);
				cols = new HashMap();
				bodyRow.add(cols);
			}
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:CashFlowNBusiness, M:datosIngresos");
		}
	}
	
	public void colocaImporte(HashMap cols, int i, List<PosicionBancariaDto> listI, String nomCol) {
		double importe;
		cols.put("idRubro", listI.get(i).getIdRubro());
		cols.put("descripcion", listI.get(i).getDescRubro());
		
		if(conceptoGlob.equals("1- Cobros Operativos") || conceptoGlob.equals("1- Pagos Operativos")) {
			importe = (listI.get(i).getImporte() / 1.16);
			
			if(!totIVA.containsKey(nomCol)) {
				totIVA.put(nomCol, importe * .16);
				listIVA.add(nomCol);
				
				if(!totSinIVA.containsKey(nomCol)) {
					totSinIVA.put(nomCol, importe);
				}else
					totSinIVA.put(nomCol, totSinIVA.get(nomCol) + importe);
			}else {
				totIVA.put(nomCol, (totIVA.get(nomCol) + (importe * .16)));
				if(!totSinIVA.containsKey(nomCol)) {
					totSinIVA.put(nomCol, importe);
				}else
					totSinIVA.put(nomCol, totSinIVA.get(nomCol) + importe);
			}
			
		}else {
			importe = listI.get(i).getImporte();
			if(!totSinIVA.containsKey(nomCol)) {
				totSinIVA.put(nomCol, importe);
				listIVA.add(nomCol);
			}else
				totSinIVA.put(nomCol, totSinIVA.get(nomCol) + importe);
		}
		if(!cols.containsKey("totales"))
			cols.put("totales", formato.format(importe));
		else 
			cols.put("totales", formato.format(Double.parseDouble(cols.get("totales").toString().replace(",", "").replace("$", "")) + importe));
		
		if(!cols.containsKey(nomCol)){
			cols.put(nomCol, formato.format(importe));
			colocaTotalSemana(cols,nomCol , importe);
		}else{ 
			cols.put(nomCol, formato.format(Double.parseDouble(cols.get(nomCol).toString().replace(",", "").replace("$", "")) + importe));
			colocaTotalSemana(cols,nomCol , importe);
			}
		}
	
	public void sumTotalCobPag(String nomCol, double importe) {
		if(IE.equals("I")) {
			if(!totCobros.containsKey(nomCol)) {
				totCobros.put(nomCol, importe);
				listCobros.add(nomCol);
			}else
				totCobros.put(nomCol, (totCobros.get(nomCol) + importe));
		}else {
			if(!totPagos.containsKey(nomCol)) {
				totPagos.put(nomCol, importe);
				listPagos.add(nomCol);
			}else
				totPagos.put(nomCol, (totPagos.get(nomCol) + importe));
		}
	}
	
	public void colocaTotalSemana(HashMap cols,String nomC, double importe){
		fecIni = funciones.ponerFechaDate(nomC.substring(3)+" 00:00:00");
		calFecIni.setTime(fecIni);
		semanaDelAnio = calFecIni.get(Calendar.WEEK_OF_YEAR);
		finMes = calFecIni.getActualMaximum(Calendar.DAY_OF_MONTH);
		diaMes = Integer.parseInt(nomC.substring(3, 5));
		
		String nomCol = "colTotSem" + semanaDelAnio;
		if(!cols.containsKey(nomCol)){
			if(conceptoGlob.equals("1- Cobros Operativos") || conceptoGlob.equals("1- Pagos Operativos")) {
				sumTotalCobPag(nomCol,importe + (importe *.16));
			}else{
				sumTotalCobPag(nomCol,importe);
			}
			cols.put(nomCol, formato.format(importe));
		}else{
			if(conceptoGlob.equals("1- Cobros Operativos") || conceptoGlob.equals("1- Pagos Operativos")) {
				sumTotalCobPag(nomCol,importe + (importe *.16));
			}else{
				sumTotalCobPag(nomCol,importe);
			}
			cols.put(nomCol, formato.format(Double.parseDouble(cols.get(nomCol).toString().replace(",", "").replace("$", "")) + importe));
		}
		
		if(conceptoGlob.equals("1- Cobros Operativos") || conceptoGlob.equals("1- Pagos Operativos")) {
			
			if(!totIVA.containsKey(nomCol)) {
				totIVA.put(nomCol, importe * .16);
				listIVA.add(nomCol);
				
				if(!totSinIVA.containsKey(nomCol)) {
					totSinIVA.put(nomCol, importe);
				}else
					totSinIVA.put(nomCol, totSinIVA.get(nomCol) + importe);
			}else {
				totIVA.put(nomCol, (totIVA.get(nomCol) + (importe * .16)));
				if(!totSinIVA.containsKey(nomCol)) {
					totSinIVA.put(nomCol, importe);
				}else
					totSinIVA.put(nomCol, totSinIVA.get(nomCol) + importe);
			}
			
		}else {
			if(!totSinIVA.containsKey(nomCol)) {
				totSinIVA.put(nomCol, importe);
				listIVA.add(nomCol);
			}else
				totSinIVA.put(nomCol, totSinIVA.get(nomCol) + importe);
		}
		
		colocaTotalMes(cols,importe, nomC);
	}
	
	public void colocaTotalMes(HashMap cols,double importe, String nomC){
		String nomCol = "colTotMes" + nomC.substring(6,8) + nomC.substring(9,13);
		if(!cols.containsKey(nomCol)){
			if(conceptoGlob.equals("1- Cobros Operativos") || conceptoGlob.equals("1- Pagos Operativos")) {
				sumTotalCobPag(nomCol,importe + (importe *.16));
			}else{
				sumTotalCobPag(nomCol,importe);
			}
			cols.put(nomCol,  formato.format(importe));
		}else{
			if(conceptoGlob.equals("1- Cobros Operativos") || conceptoGlob.equals("1- Pagos Operativos")) {
				sumTotalCobPag(nomCol,importe + (importe *.16));
			}else{
				sumTotalCobPag(nomCol,importe);
			}
			cols.put(nomCol, formato.format(Double.parseDouble(cols.get(nomCol).toString().replace(",", "").replace("$", "")) + importe));
		}
		
		if(conceptoGlob.equals("1- Cobros Operativos") || conceptoGlob.equals("1- Pagos Operativos")) {
			
			if(!totIVA.containsKey(nomCol)) {
				totIVA.put(nomCol, importe * .16);
				listIVA.add(nomCol);
				
				if(!totSinIVA.containsKey(nomCol)) {
					totSinIVA.put(nomCol, importe);
				}else
					totSinIVA.put(nomCol, totSinIVA.get(nomCol) + importe);
			}else {
				totIVA.put(nomCol, (totIVA.get(nomCol) + (importe * .16)));
				if(!totSinIVA.containsKey(nomCol)) {
					totSinIVA.put(nomCol, importe);
				}else
					totSinIVA.put(nomCol, totSinIVA.get(nomCol) + importe);
			}
			
		}else {
			if(!totSinIVA.containsKey(nomCol)) {
				totSinIVA.put(nomCol, importe);
				listIVA.add(nomCol);
			}else
				totSinIVA.put(nomCol, totSinIVA.get(nomCol) + importe);
		}
	}
	
	public void colocaTotalCobPag(HashMap cols) {
		cols.put("totales", 0);
		
		if(IE.equals("I")) {
			for(int i=0; i<listCobros.size(); i++) {
				cols.put(listCobros.get(i), formato.format(totCobros.get(listCobros.get(i))));
				cols.put("totales", formato.format(Double.parseDouble(cols.get("totales").toString().replace(",", "").replace("$", "")) + totCobros.get(listCobros.get(i))));
			}
		}else {
			for(int i=0; i<listPagos.size(); i++) {
				cols.put(listPagos.get(i), formato.format(totPagos.get(listPagos.get(i))));
				cols.put("totales", formato.format(Double.parseDouble(cols.get("totales").toString().replace(",", "").replace("$", "")) + totPagos.get(listPagos.get(i))));
			}
		}
	}
	
	public void colocaTotalSinIVA(HashMap cols, String titulo, List<HashMap> bodyRow, String titTotal) {
		String confir = "";
		cols = new HashMap();
		cols.put("descripcion", titulo);
		cols.put("totales", 0);
		for(int i=0; i<listIVA.size(); i++) {
			confir = listIVA.get(i);
			confir = confir.substring(0,6);
			if(!cols.containsKey(listIVA.get(i)) && totIVA.containsKey(listIVA.get(i))) {
				cols.put(listIVA.get(i), formato.format(totIVA.get(listIVA.get(i))));
				if(!confir.equals("colTot")){
					cols.put("totales", formato.format(Double.parseDouble(cols.get("totales").toString().replace(",", "").replace("$", "")) + totIVA.get(listIVA.get(i))));
				}
			}
		}
		bodyRow.add(cols);		
		colocaTituloTotalGrupo(cols, titulo, bodyRow, titTotal);
	}
	
	public void colocaTituloTotalGrupo(HashMap cols, String titulo, List<HashMap> bodyRow, String titTotal) {
		String confir = "";
		cols = new HashMap();
		cols.put("descripcion", "Total " + titTotal);
		cols.put("totales", 0);
		
		for(int i=0; i<listIVA.size(); i++) {
			confir = listIVA.get(i);
			confir = confir.substring(0,6);
			if(!cols.containsKey(listIVA.get(i))) {
				if(!titTotal.equals("Otros cobros") && !titTotal.equals("Otros pagos")) {
					cols.put(listIVA.get(i), formato.format(totSinIVA.get(listIVA.get(i))));
					if(!confir.equals("colTot"))
						cols.put("totales", formato.format(Double.parseDouble(cols.get("totales").toString().replace(",", "").replace("$", "")) + totSinIVA.get(listIVA.get(i))));
					}else {
					if(totIVA.containsKey(listIVA.get(i))) {
						if(totSinIVA.containsKey(listIVA.get(i))) {
							cols.put(listIVA.get(i), formato.format(totSinIVA.get(listIVA.get(i)) + totIVA.get(listIVA.get(i))));
							if(!confir.equals("colTot"))
								cols.put("totales", formato.format(Double.parseDouble(cols.get("totales").toString().replace(",", "").replace("$", "")) + totSinIVA.get(listIVA.get(i)) + totIVA.get(listIVA.get(i))));
						}else {
							cols.put(listIVA.get(i), formato.format(totIVA.get(listIVA.get(i))));
							if(!confir.equals("colTot"))
								cols.put("totales", formato.format(Double.parseDouble(cols.get("totales").toString().replace(",", "").replace("$", "")) + totIVA.get(listIVA.get(i))));
						}
					}else {
						cols.put(listIVA.get(i), formato.format(totSinIVA.get(listIVA.get(i))));
						if(!confir.equals("colTot"))
							cols.put("totales", formato.format(Double.parseDouble(cols.get("totales").toString().replace(",", "").replace("$", "")) + totSinIVA.get(listIVA.get(i))));
					}
				}
			}
		}
		bodyRow.add(cols);
		totSinIVA = new HashMap();
	}
	
	
	
	public void colocaTotGlobalGrid(int i, boolean bGrid, HashMap cols, String iva) {
		if(iva == "iva"){
			if(IE.equals("I")) {
				if(bGrid){
					for(int x=0; x<list.size(); x++)
						cols.put(list.get(x), formato.format(sumTotCobI.get(list.get(x))));
				}
			}else {
				if(bGrid) {
					for(int x=0; x<list.size(); x++)
						cols.put(list.get(x), formato.format(sumTotPagI.get(list.get(x))));
				}
			}
		}else if(iva == "T"){
			if(IE.equals("I")){
				if(bGrid){
					for(int x=0; x<list.size(); x++){
						if(sumTotCob.containsKey(list.get(x)) == sumIva.containsKey(list.get(x))){
							if(Double.compare(sumTotales2.get(list.get(x)),sumIva.get(list.get(x))) == 0){
								cols.put(list.get(x), formato.format(sumTotCob.get(list.get(x))));
							}else{
								cols.put(list.get(x), formato.format(sumTotCob.get(list.get(x)) + sumIva.get(list.get(x))));
							}
							
						}else{
							cols.put(list.get(x), formato.format(sumTotCob.get(list.get(x))));
						}
					}
				}
			}else {
				if(bGrid) {
					for(int x=0; x<list.size(); x++) {
						if(sumTotPag.containsKey(list.get(x)) == sumIva.containsKey(list.get(x))) {
							if(sumTotPag.containsKey(list.get(x)) && sumIva.containsKey(list.get(x))) {
								if(sumTotales2.containsKey(list.get(x))) {
									if(Double.compare(sumTotales2.get(list.get(x)), sumIva.get(list.get(x))) == 0) {
										cols.put(list.get(x), formato.format(sumTotPag.get(list.get(x))));
									}else{
										cols.put(list.get(x), formato.format(sumTotPag.get(list.get(x)) + sumIva.get(list.get(x))));
									}
								}else {
									cols.put(list.get(x), formato.format(sumTotPag.get(list.get(x)) + sumIva.get(list.get(x))));
								}
							}else if(!sumTotPag.containsKey(list.get(x)) && !sumIva.containsKey(list.get(x)))
								cols.put(list.get(x), formato.format(sumTotales2.get(list.get(x))));
							else if(!sumTotPag.containsKey(list.get(x)) && sumIva.containsKey(list.get(x)))
								cols.put(list.get(x), formato.format(sumTotales2.get(list.get(x)) + sumIva.get(list.get(x))));
							else if(!sumTotPag.containsKey(list.get(x)) && sumIva.containsKey(list.get(x)))
								cols.put(list.get(x), formato.format(sumTotales2.get(list.get(x)) + sumTotPag.get(list.get(x))));
						}else{
							cols.put(list.get(x), formato.format(sumTotPag.get(list.get(x))));
						}
					}
				}
			}
		}else{
			if(IE.equals("I")){
				if(bGrid){
					for(int x=0; x<list.size(); x++)
						cols.put(list.get(x), formato.format(sumTotCob.get(list.get(x))));
				}else {
					if(iva == "O"){
						if(!sumTotCob.containsKey(list.get(i)))
							sumTotCob.put(list.get(i), sumIva.get(list.get(i)));
						else
							sumTotCob.put(list.get(i), sumIva.get(list.get(i)) + sumTotCob.get(list.get(i)));
					}else{
						if(!sumTotCob.containsKey(list.get(i)))
							sumTotCob.put(list.get(i), sumTotales.get(list.get(i)));
						else
							sumTotCob.put(list.get(i), sumTotales.get(list.get(i)) + sumTotCob.get(list.get(i)));
					}
				}
			}else {
				if(bGrid) {
					for(int x=0; x<list.size(); x++)
						cols.put(list.get(x), formato.format(sumTotPag.get(list.get(x))));
				}else {
					if(iva == "O"){
						if(!sumTotPag.containsKey(list.get(i)))
							sumTotPag.put(list.get(i), sumIva.get(list.get(i)));
						else
							sumTotPag.put(list.get(i), sumIva.get(list.get(i)) + sumTotPag.get(list.get(i)));
					}else{
						if(!sumTotPag.containsKey(list.get(i)))
							sumTotPag.put(list.get(i), sumTotales.get(list.get(i)));
						else
							sumTotPag.put(list.get(i), sumTotales.get(list.get(i)) + sumTotPag.get(list.get(i)));
					}
				}
			}
		}	
	}
	
	public JRDataSource reporteDetalleFlujo(PosicionBancariaDto dtoIn) {

		JRMapArrayDataSource jrDataSource = null;
		List<Map<String, Object>> listReport = new ArrayList<Map<String, Object>>();
		
		try {
			listReport = cashFlowNDao.reporteDetalleFlujo(dtoIn);
	        jrDataSource = new JRMapArrayDataSource(listReport.toArray());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Egresos, C:PagoPropuestasBusiness, M:reportePagoPropuestas");
		}
		return jrDataSource;
	}
	
	public List<PosicionBancariaDto> comboGrupoRubro(String idTipoMovto) {
		return cashFlowNDao.comboGrupoRubro(idTipoMovto);
	}
	
	public List<PosicionBancariaDto> comboRubro(int idGrupo){
		return cashFlowNDao.comboRubro(idGrupo);
	}
	
	public String reclasificaMovtos(int noFolioDet, int idGrupo, int idRubro, int interes,String concepto,String fecValor) {
		String respuesta = "";
		int res = 0;
		int resInt = 0;
		
		try {
			
			if(idRubro == 72201){
				resInt = cashFlowNDao.insertaInteres(noFolioDet,interes,concepto,idGrupo,idRubro,fecValor);
			}
			
			res = cashFlowNDao.reclasificaMovtos(noFolioDet, idGrupo, idRubro);
			
			if(res != 0)
				respuesta = "Registro reclasificado con exito!!";
			else
				respuesta = "Error registro no reclasificado!!";
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Egresos, C:PagoPropuestasBusiness, M:reclasificaMovtos");
		}
		return respuesta;
	}
	
	public CashFlowNDao getCashFlowNDao() {
		return cashFlowNDao;
	}
	
	public void setCashFlowNDao(CashFlowNDao cashFlowNDao) {
		this.cashFlowNDao = cashFlowNDao;
	}
	
}
