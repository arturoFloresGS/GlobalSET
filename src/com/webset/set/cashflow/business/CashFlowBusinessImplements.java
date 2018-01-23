package com.webset.set.cashflow.business;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.cashflow.dao.CashFlowDaoInterface;
import com.webset.set.cashflow.dto.AColReporteDto;
import com.webset.set.cashflow.dto.ARowReporteDto;
import com.webset.set.cashflow.dto.ConceptoFlujo;
import com.webset.set.cashflow.dto.EmpresaDto;
import com.webset.set.cashflow.dto.FechaInabilDto;
import com.webset.set.cashflow.dto.FechasDto;
import com.webset.set.cashflow.dto.GrupoEmpresasDto;
import com.webset.set.cashflow.dto.ParamSpFlujoDto;
import com.webset.set.cashflow.dto.ReporteFlujoDto;
import com.webset.set.cashflow.dto.Saldo;
import com.webset.set.cashflow.dto.TipoConcepto;
import com.webset.set.cashflow.dto.TipoMovto;
import com.webset.set.cashflow.dto.TotalConcepto;
import com.webset.set.cashflow.dto.TotalDiario;
import com.webset.set.cashflow.dto.TotalIngresoEgreso;
import com.webset.set.cashflow.dto.TotalSemanaDto;
import com.webset.set.cashflow.dto.TotalTipoConcepto;
import com.webset.set.global.business.GlobalBusiness;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

/****************************************************************************
 * INIT* AUTOR : JUAN RAMIRO BARRERA MARTINEZ NAME : CashFlowBusinessImplements
 * IMPLEMENTA CashFlowBusinnesInterface() TYPE : CLASS RESPONSABILITY :
 * IMPLEMENTACION DE LA INTERFAZ DEFINIDA PARA LA CAPA DE NEGOCIO INIT
 */
public class CashFlowBusinessImplements implements CashFlowBusinnesInterface {

	// ************************************************************************
	// DECLARACION DE CAMPOS DE LA CLASE
	// ************************************************************************
	private CashFlowDaoInterface cashFlowDao;
	private static Logger logger = Logger.getLogger(CashFlowBusinessImplements.class);
	private Funciones funciones = new Funciones();

	public CashFlowDaoInterface getCashFlowDao() {
		return cashFlowDao;
	}

	public void setCashFlowDao(CashFlowDaoInterface cashFlowDao) {
		this.cashFlowDao = cashFlowDao;
	}

	private int IdGrupo;
	private String DescGrupo;
	private int NoEmpresa;
	private String DescEmpresa;
	private String FechaInicial;
	private String FechaHoy;
	private String FechaAyer;
	private String FechaManana;
	private String FechaFinal;
	private int IdReporte;
	private String DescReporte;
	private String IdDivisa;
	private int NoUsuario;
	private int lastCol;

	GlobalBusiness global = new GlobalBusiness();

	// ------------------------------------------------------------------------
	// DECLARACION DE CAMPOS DE LA CLASE
	// ------------------------------------------------------------------------

	// ************************************************************************
	// DECLARACION DE METODOS PARA EL FORMULARIO PRINCIPAL EN LA
	// CAPA DE PRESENTACION
	// ************************************************************************
	public List<GrupoEmpresasDto> getGrupoEmpresasBusiness() {

		return cashFlowDao.getGrupoEmpresasDao();

	}

	public List<ReporteFlujoDto> getReportesFlujoBusiness() {

		return cashFlowDao.getReportesFlujoDao();

	}

	public List<EmpresaDto> getEmpresasBusiness(int idGrupo, int noUsuario) {

		return cashFlowDao.getEmpresasDao(idGrupo, noUsuario);

	}

	public List<EmpresaDto> getEmpresaBusiness(int noEmpresa) {

		return cashFlowDao.getEmpresaDao(noEmpresa);

	}

	public List<GrupoEmpresasDto> getGrupoEmpresaBusiness(int noGrupo) {

		return cashFlowDao.getGrupoEmpresaDao(noGrupo);

	}

	public List<ReporteFlujoDto> getReporteFlujoBusiness(int idReporte) {

		return cashFlowDao.getReporteFlujoDao(idReporte);

	}

	// ------------------------------------------------------------------------
	// DECLARACION DE METODOS PARA EL FORMULARIO PRINCIPAL EN LA
	// CAPA DE PRESENTACION
	// ------------------------------------------------------------------------

	/*
	 * 
	 * 
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * FUNCIONES UTILITARIAS DE SOPORTE PARA LA CONSTRUCCION DE
	 * CABECERAS++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * 
	 * 
	 */

	/*
	 * AUTOR : JUAN RAMIRO BARRERA MARTINEZ NAME : addCols PARAMS :
	 * columnasReporte-> LISTA DE COLUMNAS : paramNumCols -> NUMERO DE COLUMNAS
	 * RETURN : NOTHING RESPONSABILITY : AGREGAR UN NUMERO DE COLUMNAS PASADO A
	 * UNA LISTA DE COLUMNAS ------->R-UTIL-HEADER
	 */
	// *****************************************************************************INIT
	public void addCols(List<AColReporteDto> columnasReporte, int paramNumCols, String strBandOrigen) {

		Integer numColsOfLista;

		// numColsOfLista = columnasReporte.size();
		numColsOfLista = lastCol;
		//System.out.println("una "+paramNumCols);
		for (int i = 0; i < paramNumCols; i++) {
			numColsOfLista += 1;
			columnasReporte.add(new AColReporteDto("col" + numColsOfLista.toString()));
			System.out.println("col" + numColsOfLista.toString());
			lastCol += 1;

		}

	}

	public void addColsTOTAL(List<AColReporteDto> columnasReporte, int paramNumCols, String strBandOrigen) {

		Integer numColsOfLista;

		// numColsOfLista = columnasReporte.size();
		numColsOfLista = lastCol;
		// System.out.println("una "+paramNumCols);
		for (int i = 0; i < paramNumCols; i++) {
			numColsOfLista += 1;
			columnasReporte.add(new AColReporteDto("col" + numColsOfLista.toString()));
			// System.out.println("col" + numColsOfLista.toString());
			lastCol += 1;

		}

	}
	// *****************************************************************************END

	/*
	 * AUTOR : JUAN RAMIRO BARRERA MARTINEZ NAME : getMonthsBetweenTwoDates
	 * PARAMS : paramFechaInicial-> FECHA INICIAL : paramagFechaFinal-> FECHA
	 * FINAL RETURN : NUMERO DE MESES RESPONSABILITY : DETERMINAR EL NUMERO DE
	 * MESES QUE HAY ENTRE DOS FECHAS ------->R-UTIL-HEADER
	 */
	// *****************************************************************************INIT
	public long getMonthsBetweenTwoDates(String paramFechaInicial, String paramFechaFinal) {

		Calendar fechaI = Calendar.getInstance();
		Calendar fechaF = Calendar.getInstance();

		fechaI.setTime(getTypeDateFromStringFec(paramFechaInicial));
		getFecIni(fechaI);
		fechaF.setTime(getTypeDateFromStringFec(paramFechaFinal));
		getFecFin(fechaF);

		int mesInicial = fechaI.get(Calendar.MONTH);
		int mesFinal = fechaF.get(Calendar.MONTH);
		int mesResult = (mesFinal - mesInicial) + 1;
		int anioInicial = fechaI.get(Calendar.YEAR);
		int anioFinal = fechaF.get(Calendar.YEAR);
		int anioResult = (anioFinal - anioInicial) * 12;

		int diferencia = mesResult + anioResult;

		return diferencia;

	}

	public int getMonthsBetweenTwoDatess(String paramFechaInicial, String paramFechaFinal) {

		Calendar fechaI = Calendar.getInstance();
		Calendar fechaF = Calendar.getInstance();

		fechaI.setTime(getTypeDateFromStringFec(paramFechaInicial));
		getFecIni(fechaI);
		fechaF.setTime(getTypeDateFromStringFec(paramFechaFinal));
		getFecFin(fechaF);

		int mesInicial = fechaI.get(Calendar.MONTH);
		int mesFinal = fechaF.get(Calendar.MONTH);
		int mesResult = (mesFinal - mesInicial) + 1;
		int anioInicial = fechaI.get(Calendar.YEAR);
		int anioFinal = fechaF.get(Calendar.YEAR);
		int anioResult = (anioFinal - anioInicial) * 12;

		int diferencia = mesResult + anioResult;

		return diferencia;

	}
	// *****************************************************************************END

	/*
	 * AUTOR : JUAN RAMIRO BARRERA MARTINEZ NAME : getWeeksBetweenTwoDates
	 * PARAMS : paramFechaInicial-> FECHA INICIAL : paramagFechaFinal-> FECHA
	 * FINAL RETURN : NUMERO DE SEMANAS RESPONSABILITY : DETERMINAR EL NUMERO DE
	 * SEMANAS QUE HAY ENTRE DOS FECHAS ------->R-UTIL-HEADER
	 */
	// *****************************************************************************INIT
	public int getWeeksBetweenTwoDates(String paramfecha1, String paramfecha2) throws Exception {

		java.util.Date fecha1 = getTypeDateFromStringFec(paramfecha1);
		java.util.Date fecha2 = getTypeDateFromStringFec(paramfecha2);

		if (fecha1.compareTo(fecha2) >= 0)
			throw new Exception("La fecha final debe ser mayor a la fecha inicial");

		Calendar calFecha = Calendar.getInstance();

		int resultado = 0;

		calFecha.setTime(fecha1);
		calFecha.setFirstDayOfWeek(Calendar.SUNDAY);
		int semana1 = calFecha.get(Calendar.WEEK_OF_YEAR);
		int anio1 = calFecha.get(Calendar.YEAR);

		calFecha.setTime(fecha2);
		calFecha.setFirstDayOfWeek(Calendar.SUNDAY);
		int semana2 = calFecha.get(Calendar.WEEK_OF_YEAR);
		int anio2 = calFecha.get(Calendar.YEAR);

		if (anio1 < anio2 && semana1 > 0)
			semana1 -= 52 * (anio2 - anio1);

		semana1 = (semana1 >= 52) ? 0 : semana1;
		semana2 = (semana2 >= 54) ? 0 : semana2;

		resultado = semana2 - semana1 + 1;

		return resultado;
	}
	
	
	
	public int getWeeksBetweenTwoDates1(String paramfecha1, String paramfecha2) {

		java.util.Date fecha1 = getTypeDateFromStringFec(paramfecha1);
		java.util.Date fecha2 = getTypeDateFromStringFec(paramfecha2);

		 
		Calendar calFecha = Calendar.getInstance();

		int resultado = 0;

		calFecha.setTime(fecha1);
		calFecha.setFirstDayOfWeek(Calendar.SUNDAY);
		int semana1 = calFecha.get(Calendar.WEEK_OF_YEAR);
		int anio1 = calFecha.get(Calendar.YEAR);

		calFecha.setTime(fecha2);
		calFecha.setFirstDayOfWeek(Calendar.SUNDAY);
		int semana2 = calFecha.get(Calendar.WEEK_OF_YEAR);
		int anio2 = calFecha.get(Calendar.YEAR);

		if (anio1 < anio2 && semana1 > 0)
			semana1 -= 52 * (anio2 - anio1);

		semana1 = (semana1 >= 52) ? 0 : semana1;
		semana2 = (semana2 >= 54) ? 0 : semana2;

		resultado = semana2 - semana1 + 1;

		return resultado;
	}
	
	// *****************************************************************************END

	/*
	 * AUTOR : JUAN RAMIRO BARRERA MARTINEZ NAME : getDaysBetweenTwoDates PARAMS
	 * : paramFechaInicial-> FECHA INICIAL : paramagFechaFinal-> FECHA FINAL
	 * RETURN : NUMERO DE DIAS RESPONSABILITY : DETERMINA EL NUMERO DE DIAS
	 * ENTRE DOS FECHAS ------->R-UTIL-HEADER
	 */
	// *****************************************************************************INIT
	public long getDaysBetweenTwoDates(Calendar paramfecha1, Calendar paramfecha2) throws Exception {

		double fecIni = paramfecha1.getTimeInMillis();
		double fecFin = paramfecha2.getTimeInMillis();
		long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000;
		double fecResult = Math.ceil((fecFin - fecIni) / MILLSECS_PER_DAY);

		long diferencia = (long) fecResult;
		long resultado = 0;

		resultado = diferencia + 1;

		return resultado;
	}
	// *****************************************************************************END

	/*
	 * AUTOR : JUAN RAMIRO BARRERA MARTINEZ NAME : getDatesOfMonth PARAMS :
	 * fecInicial: fecha inicial : fecFinal : fecha final : opt : opcion de
	 * seleccion de fecha RETURN : String con la fecha seleccionada
	 * RESPONSABILITY : DETERMINAR UNA CADENA QUE CONTIENE UNA FECHA SEGUN LA
	 * OPCION SELECCIONADA. ES UNA FUNCION PARA LA SELECCION DE FECHAS A PARTIR
	 * DE PERIODOS MENSUALES. *------->R-UTIL-HEADER
	 */
	// *****************************************************************************INIT

	public String getDatesOfMonth(String fecInicial, String fecFinal, int pOpcion) {

		String sFecResult = "";

		Calendar calfecIni = Calendar.getInstance();
		calfecIni.setTime(getTypeDateFromStringFec(fecInicial));

		Integer ntDiaI = calfecIni.getMinimum(Calendar.DAY_OF_MONTH);
		Integer ntMesI = calfecIni.get(Calendar.MONTH) + 1;
		Integer ntAnioI = calfecIni.get(Calendar.YEAR);

		String strDiaI = (ntDiaI < 10) ? "0" + ntDiaI.toString() : ntDiaI.toString();
		String strMesI = (ntMesI < 10) ? "0" + ntMesI.toString() : ntMesI.toString();
		;
		// logger.info("1 "+ strMesI);
		String strAnioI = ntAnioI.toString();

		Calendar calfecFin = new GregorianCalendar(calfecIni.get(Calendar.YEAR), calfecIni.get(Calendar.MONTH),
				calfecIni.getActualMaximum(Calendar.DAY_OF_MONTH));

		Integer ntDiaF = calfecIni.getActualMaximum(Calendar.DAY_OF_MONTH);
		Integer ntMesF = calfecIni.get(Calendar.MONTH) + 1;
		Integer ntAnioF = calfecIni.get(Calendar.YEAR);
		// logger.info("2 "+ ntMesF);
		String strDiaF = (ntDiaF < 10) ? "0" + ntDiaF.toString() : ntDiaF.toString();
		String strMesF = (ntMesF < 10) ? "0" + ntMesF.toString() : ntMesF.toString();
		;
		String strAnioF = ntAnioF.toString();

		switch (pOpcion) {

		case 1:
			fecInicial = strDiaI + "/" + strMesI + "/" + strAnioI;
			sFecResult = fecInicial;
			break;

		case 2:
			fecFinal = strDiaF + "/" + strMesF + "/" + strAnioF;
			sFecResult = fecFinal;
			break;

		case 3:
			calfecIni.add(Calendar.MONTH, 1);
			ntDiaI = calfecIni.getMinimum(Calendar.DAY_OF_MONTH);
			ntMesI = calfecIni.get(Calendar.MONTH) + 1;
			// logger.info("339 "+ntMesI);
			ntAnioI = calfecIni.get(Calendar.YEAR);
			strDiaI = (ntDiaI < 10) ? "0" + ntDiaI.toString() : ntDiaI.toString();
			strMesI = (ntMesI < 10) ? "0" + ntMesI.toString() : ntMesI.toString();
			;
			strAnioI = ntAnioI.toString();
			fecInicial = strDiaI + "/" + strMesI + "/" + strAnioI;
			// logger.info("no se "+fecInicial);
			sFecResult = fecInicial;
			break;

		case 4:
			calfecFin.add(Calendar.MONTH, 1);
			ntDiaF = calfecIni.getActualMaximum(Calendar.DAY_OF_MONTH);
			ntMesF = calfecIni.get(Calendar.MONTH) + 1;
			ntAnioF = calfecIni.get(Calendar.YEAR);
			strDiaF = (ntDiaF < 10) ? "0" + ntDiaF.toString() : ntDiaF.toString();
			strMesF = (ntMesF < 10) ? "0" + ntMesF.toString() : ntMesF.toString();
			;
			strAnioF = ntAnioF.toString();
			fecFinal = strDiaF + "/" + strMesF + "/" + strAnioF;
			sFecResult = fecFinal;
			break;

		}

		return sFecResult;
	}

	// *****************************************************************************END

	/*
	 * AUTOR : JUAN RAMIRO BARRERA MARTINEZ NAME : getDateInitOfWeekFromDatePass
	 * PARAMS : paramFecha-> FECHA DE LA CUAL SE DETERMINARA EL DATO RETURN :
	 * FECHA INICIAL DE LA SEMANA DE LA FECHA PASADA RESPONSABILITY : DETERMINA
	 * EL PRIMER DIA DE LA SEMANA UBICADA EN LA SEMANA EN LA FECHA PASADA COMO
	 * PARAMETRO ------->R-UTIL-HEADER
	 */
	// *****************************************************************************INIT
	public Calendar getDateInitOfWeekFromDatePass(Calendar paramfecha) throws Exception {

		java.util.Date dtfecha;
		Calendar calFecha;
		Calendar calFechaIni;

		int dia;
		int mes;
		int anio;
		int dayInIWeek;

		calFecha = Calendar.getInstance();
		calFecha.setFirstDayOfWeek(Calendar.SUNDAY);
		calFecha = paramfecha;

		dia = calFecha.get(Calendar.DAY_OF_MONTH);
		mes = calFecha.get(Calendar.MONTH);
		anio = calFecha.get(Calendar.YEAR);

		calFechaIni = new GregorianCalendar(anio, mes, dia);
		dayInIWeek = 0;

		if (dia > 1) {
			dayInIWeek = calFecha.get(Calendar.DAY_OF_WEEK) - 1;
			calFechaIni.add(Calendar.DAY_OF_MONTH, -dayInIWeek);
		}

		return calFechaIni;
	}
	// *****************************************************************************END

	/*
	 * AUTOR : JUAN RAMIRO BARRERA MARTINEZ NAME : getDateEndOfWeekFromDatePass
	 * PARAMS : paramFecha-> FECHA DE LA CUAL SE DETERMINARA EL DATO RETURN :
	 * FECHA FINAL DE LA SEMANA DE LA FECHA PASADA RESPONSABILITY : DETERMINA EL
	 * ULTIMO DIA DE LA SEMANA UBICADA EN LA SEMANA EN LA FECHA PASADA COMO
	 * PARAMETRO ------->R-UTIL-HEADER
	 */
	// *****************************************************************************INIT
	public Calendar getDateEndOfWeekFromDatePass(Calendar paramfecha) throws Exception {

		java.util.Date dtfecha;
		Calendar calFecha;
		Calendar calFechaFin;

		int dia;
		int mes;
		int anio;
		int dayFinWeek;

		calFecha = Calendar.getInstance();
		calFecha.setFirstDayOfWeek(Calendar.SUNDAY);
		calFecha = paramfecha;

		dia = calFecha.get(Calendar.DAY_OF_MONTH);
		mes = calFecha.get(Calendar.MONTH);
		anio = calFecha.get(Calendar.YEAR);

		calFechaFin = new GregorianCalendar(anio, mes, dia);
		dayFinWeek = 0;

		if (dia >= 1) {

			dayFinWeek = 7 - calFecha.get(Calendar.DAY_OF_WEEK);

			calFechaFin.add(Calendar.DAY_OF_MONTH, dayFinWeek);

			if (calFecha.get(Calendar.MONTH) != calFechaFin.get(Calendar.MONTH)) {

				while (calFecha.get(Calendar.MONTH) != calFechaFin.get(Calendar.MONTH)) {

					calFechaFin.add(Calendar.DAY_OF_MONTH, -1);

				}

			}

		}

		return calFechaFin;

	}
	// *****************************************************************************END

	/*
	 * AUTOR : JUAN RAMIRO BARRERA MARTINEZ NAME : getTypeDateFromStringFec
	 * PARAMS : paramFecha-> Fecha a castear RETURN : Valor en formato Date
	 * RESPONSABILITY : CASTEA UN STRING CON UN VALOR DE FECHA A DATE
	 * ------->R-UTIL-HEADER
	 */
	// *****************************************************************************INIT
	public java.util.Date getTypeDateFromStringFec(String paramFecha) {
		java.util.Date d = new Date(0);
		// if (paramFecha.contains("-"))
		// paramFecha = (paramFecha.split("-")[2]).split("
		// ")[0]+"/"+paramFecha.split("-")[1]+"/"+paramFecha.split("-")[0];
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			// System.out.println("fecha antes del parseo " + paramFecha);
			if (paramFecha.length() > 10)

				paramFecha = funciones.cambiarFecha(paramFecha, true);
			d = sdf.parse(paramFecha);

			// System.out.println("fecha despues del parseo " + d);
		} catch (ParseException e) {
			// logger.error(e.getLocalizedMessage(),e);
			e.printStackTrace();
		}
		return d;
	}

	// *****************************************************************************END

	/*
	 * AUTOR : JUAN RAMIRO BARRERA MARTINEZ NAME : getFecIni PARAMS : paramFecha
	 * -> FECHA RETURN : FECHA FINAL DEL MES RESPONSABILITY : DETERMINAR Y
	 * DEVOLVER LA FECHA INICIAL DEL MES DE UNA FECHA PASADA
	 * ------->R-UTIL-HEADER
	 */
	// *****************************************************************************INIT
	public void getFecIni(Calendar paramFecha) {

		paramFecha = new GregorianCalendar(paramFecha.get(Calendar.YEAR), paramFecha.get(Calendar.MONTH),
				paramFecha.getMinimum(Calendar.DAY_OF_MONTH));

	}
	// *****************************************************************************END

	/*
	 * AUTOR : JUAN RAMIRO BARRERA MARTINEZ NAME : getFecFin PARAMS : paramFecha
	 * -> FECHA RETURN : FECHA FINAL DEL MES RESPONSABILITY : DETERMINAR Y
	 * DEVOLVER LA FECHA FINAL DEL MES DE UNA FECHA PASADA ------->R-UTIL-HEADER
	 */

	// *****************************************************************************INIT
	public void getFecFin(Calendar paramFecha) {

		paramFecha = new GregorianCalendar(paramFecha.get(Calendar.YEAR), paramFecha.get(Calendar.MONTH),
				paramFecha.getActualMaximum(Calendar.DAY_OF_MONTH));

	}
	// *****************************************************************************END

	/*
	 * AUTOR : JUAN RAMIRO BARRERA MARTINEZ NAME : setColMonths PARAMS :
	 * columnasReporte -> LISTA DE COLUMNAS : paramFechaInicial-> FECHA INICIAL
	 * DE EMISION DEL REPORTE : paramagFechaFinal-> FECHA FINAL DE EMISION DEL
	 * REPORTE RETURN : LISTA DE COLUMNAS RESPONSABILITY : ESTABLECER COLUMNAS
	 * SEGUN EL NUMERO DE MESES QUE ABARCA EL RANGO
	 */
	// *****************************************************************************INIT
	private void setColMonths(List<AColReporteDto> columnasReporte, String paramFechaInicial, String paramFechaFinal)
			throws Exception {

		long ntNumMonths = 0;

		String fecInicial = paramFechaInicial;
		String fecFinal = paramFechaFinal;

		ntNumMonths = getMonthsBetweenTwoDates(fecInicial, fecFinal);

		fecInicial = getDatesOfMonth(fecInicial, fecFinal, 1);
		fecFinal = getDatesOfMonth(fecInicial, fecFinal, 2);

		for (int i = 1; i <= ntNumMonths; i++) {
			setColWeeks(columnasReporte, fecInicial, fecFinal);
			addCols(columnasReporte, 1, "MONTH");
			fecInicial = getDatesOfMonth(fecInicial, fecFinal, 3);
			fecFinal = getDatesOfMonth(fecInicial, fecFinal, 4);
		}
		addCols(columnasReporte, 1, "TOTAL DIFERENCIA ");

	}
	
	private void setColMonthsprueba(List<AColReporteDto> columnasReporte, String paramFechaInicial, String paramFechaFinal)
			throws Exception {

		long ntNumMonths = 0;

		String fecInicial = paramFechaInicial;
		String fecFinal = paramFechaFinal;

		ntNumMonths = getMonthsBetweenTwoDates(fecInicial, fecFinal);

		fecInicial = getDatesOfMonth(fecInicial, fecFinal, 1);
		fecFinal = getDatesOfMonth(fecInicial, fecFinal, 2);

		for (int i = 1; i <= ntNumMonths; i++) {
			setColWeeks(columnasReporte, fecInicial, fecFinal);
			addCols(columnasReporte, 1, "MONTH");
			fecInicial = getDatesOfMonth(fecInicial, fecFinal, 3);
			fecFinal = getDatesOfMonth(fecInicial, fecFinal, 4);
		}

	}
	// *****************************************************************************END

	/*
	 * AUTOR : JUAN RAMIRO BARRERA MARTINEZ NAME : setColWeeks PARAMS :
	 * columnasReporte -> LISTA DE COLUMNAS : paramFechaInicial-> FECHA INICIAL
	 * DE EMISION DEL REPORTE : paramagFechaFinal-> FECHA FINAL DE EMISION DEL
	 * REPORTE RETURN : LISTA DE COLUMNAS RESPONSABILITY : ESTABLECER COLUMNAS
	 * SEGUN EL NUMERO DE SEMANAS QUE ABARCA EL RANGO DE MESES
	 */
	// *****************************************************************************INIT
	private void setColWeeks(List<AColReporteDto> columnasReporte, String paramFechaInicial, String paramFechaFinal)
			throws Exception {

		int ntWeeks = 0;

		Calendar calFecNext = Calendar.getInstance();
		calFecNext.setTime(getTypeDateFromStringFec(paramFechaInicial));

		ntWeeks = getWeeksBetweenTwoDates(paramFechaInicial, paramFechaFinal);

		for (int i = 1; i <= ntWeeks; i++) {
			calFecNext = setColDays(columnasReporte, calFecNext);
			addCols(columnasReporte, 1, "WEEK");

		}
	}

	 
	// *****************************************************************************END

	/*
	 * AUTOR : JUAN RAMIRO BARRERA MARTINEZ NAME : setColDays PARAMS :
	 * columnasReporte -> LISTA DE COLUMNAS : paramFechaInicial-> FECHA INICIAL
	 * DE EMISION DEL REPORTE : paramagFechaFinal-> FECHA FINAL DE EMISION DEL
	 * REPORTE RETURN : LISTA DE COLUMNAS RESPONSABILITY : ESTABLECER COLUMNAS
	 * SEGUN EL NUMERO DE SEMANAS QUE ABARCA EL RANGO DE MESES
	 */
	// *****************************************************************************INIT
	private Calendar setColDays(List<AColReporteDto> columnasReporte, Calendar paramFecha) throws Exception {

		Calendar calFecIni;
		Calendar calFecFin;
		Calendar calFecNext;

		calFecIni = getDateInitOfWeekFromDatePass(paramFecha);
		calFecFin = getDateEndOfWeekFromDatePass(paramFecha);

		long ntDays = 0;

		ntDays = getDaysBetweenTwoDates(calFecIni, calFecFin);

		for (int i = 1; i <= ntDays; i++) {
			addCols(columnasReporte, 1, "DAY");
		}

		calFecFin.add(Calendar.DAY_OF_MONTH, 1);
		calFecNext = calFecFin;

		return calFecNext;

	}
	
	
	
	
	// *****************************************************************************END

	/*
	 * 
	 * 
	 * -------------------------------------------------------------------------
	 * ------------------------------------------------------------------------
	 * -------------------------------------------------------------------------
	 * ------------------------------------------------------------------------
	 * FUNCIONES UTILITARIAS DE SOPORTE PARA LA CONSTRUCCION DE CABECERAS
	 * -------------------------------------------------------------------------
	 * ------------------------------------------------------------------------
	 * -------------------------------------------------------------------------
	 * ------------------------------------------------------------------------
	 * 
	 * 
	 */

	/*****************************************************************************
	 * INIT AUTOR : JUAN RAMIRO BARRERA MARTINEZ NAME :
	 * getStructureReportBusiness PARAMS : paramIdGrupo -> GRUPO DE FLUJO :
	 * paramNoEmpresa -> EMPRESA DEL GRUPO O TODAS : paramFechaInicial-> FECHA
	 * INICIAL DE EMISION DEL REPORTE : paramagFechaFinal-> FECHA FINAL DE
	 * EMISION DEL REPORTE : paramIdReporte -> TIPO DE REPORTE SELECCIONADO
	 * RETURN : COLUMNAS PARA LA CABECERA RESPONSABILITY : PUNTO DE CONTROL PARA
	 * DETERMINAR Y RETORNAR UNA ESTRUCTURA QUE DEFINE LAS COLUMNAS A ESTABLECER
	 * EN LA CABECERA DEL REPORTE EN BASE A LA SELECCION DE UN REPORTE DADO INIT
	 */
	public List<AColReporteDto> getStructureReportBusiness(int paramIdGrupo, int paramNoEmpresa,
			String paramFechaInicial, String paramaFechaFinal, int paramIdReporte) throws Exception {

		List<AColReporteDto> columnasReporte = new ArrayList<AColReporteDto>();

		IdReporte = paramIdReporte;

		switch (paramIdReporte) {
		case 1: /* DIARIO REAL */
			/*
			 * setHeaderDiarioReal(columnasReporte,paramFechaInicial,
			 * paramaFechaFinal);
			 */
			setHeaderMensualAjustado(columnasReporte, paramFechaInicial, paramaFechaFinal);
			break;
		case 2: /* MENSUAL AJUSTADO */
			setHeaderMensualAjustado(columnasReporte, paramFechaInicial, paramaFechaFinal);
			break;
		case 3: /* COMPARATIVO MENSUAL ORIGINAL/AJUSTADO */
			setHeaderComparativoMensualOriginalAjustado(columnasReporte, paramFechaInicial, paramaFechaFinal);
			break;
		case 4: /* MENSUAL ORIGINAL AJUSTADO */
			setHeaderMensualOriginalAjustado(columnasReporte, paramFechaInicial, paramaFechaFinal);
			break;
		case 5: /* COMPARATIVO MENSUAL REAL AJUSTADO */
			setHeaderComparativoMensualRealAjustado(columnasReporte, paramFechaInicial, paramaFechaFinal);
			break;
		case 6: /* ORIGINAL AJUSTADO Y REAL */
			setHeaderMensualOriginalAjustadoReal(columnasReporte, paramFechaInicial, paramaFechaFinal);
			break;
		case 7: /* DIARIO REAL */
			setHeaderSemanalRealAjustado(columnasReporte, paramFechaInicial, paramaFechaFinal);
			break;
		case 8: /* DIARIO REAL */
			prueba(columnasReporte, paramFechaInicial, paramaFechaFinal);
			break;

		}
		Gson obj = new Gson();
		// logger.info("columas: " + obj.toJson(columnasReporte) );

		return columnasReporte;
	}

	// *****************************************************************************END
	// getStructureReportBusiness

	/*
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++
	 ***************************************************************************************************************************************************** 
	 *****************************************************************************************************************************************************
	 **************************************************** CONSTRUCCION DE LAS COLUMNAS DIARIO
	 * REAL*********************************************************
	 *****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++
	 */

	/*
	 * AUTOR : JUAN RAMIRO BARRERA MARTINEZ NAME : getHeaderDiarioReal PARAMS :
	 * columnasReporte -> LISTA DE COLUMNAS : paramFechaInicial-> FECHA INICIAL
	 * DE EMISION DEL REPORTE : paramagFechaFinal-> FECHA FINAL DE EMISION DEL
	 * REPORTE RETURN : COLUMNAS PARA LA CABECERA RESPONSABILITY : DETERMINAR
	 * LAS COLUMNAS PARA EL REPORTE FLUJO DIARIO REAL
	 */
	// *****************************************************************************INIT
	public void setHeaderDiarioReal(List<AColReporteDto> columnasReporte, String paramFechaInicial,
			String paramaFechaFinal) throws Exception {

		addCols(columnasReporte, 3, "MAIN");
		setColMonths(columnasReporte, paramFechaInicial, paramaFechaFinal);

	}
	// *****************************************************************************END

	/*----------------------------------------------------------------------------------------------------------------------------------------------------
	 ***************************************************************************************************************************************************** 
	 *****************************************************************************************************************************************************
	 ****************************************************CONSTRUCCION DE LAS COLUMNAS DIARIO REAL*********************************************************
	 *****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************
	 *----------------------------------------------------------------------------------------------------------------------------------------------------*/

	/*
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++
	 ***************************************************************************************************************************************************** 
	 *****************************************************************************************************************************************************
	 **************************************************** CONSTRUCCION DE LAS COLUMNAS MENSUAL
	 * AJUSTADO****************************************************
	 *****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++
	 */

	private void setHeaderMensualAjustado(List<AColReporteDto> columnasReporte, String paramFechaInicial,
			String paramaFechaFinal) throws Exception {

		addCols(columnasReporte, 3, "MAIN");
		// logger.info("MENSUAL AJUSTADO: COLS BASE: 3");
		setColMonths(columnasReporte, paramFechaInicial, paramaFechaFinal);

	}

	/*----------------------------------------------------------------------------------------------------------------------------------------------------
	 ***************************************************************************************************************************************************** 
	 *****************************************************************************************************************************************************
	 ****************************************************CONSTRUCCION DE LAS COLUMNAS MENSUAL AJUSTADO****************************************************
	 *****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************
	 *----------------------------------------------------------------------------------------------------------------------------------------------------*/

	/*
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++
	 ***************************************************************************************************************************************************** 
	 *****************************************************************************************************************************************************
	 **************************************************** CONSTRUCCION DE LAS COLUMNAS MENSUAL COMPARATIVO
	 * ORIGINAL/AJUSTADO*******************************************************
	 *****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++
	 */
	private void setHeaderComparativoMensualOriginalAjustado(List<AColReporteDto> columnasReporte,
			String paramFechaInicial, String paramaFechaFinal) throws Exception {

		addCols(columnasReporte, 3, "MAIN");
		// logger.info("MENSUAL ORIGINAL AJUSTADO: COLS BASE: 3");
		// PARA ORIGINAL
		setColMonths(columnasReporte, paramFechaInicial, paramaFechaFinal);
		// PARA AJUSTADO
		setColMonths(columnasReporte, paramFechaInicial, paramaFechaFinal);

	}
	
	private void prueba(List<AColReporteDto> columnasReporte,
			String paramFechaInicial, String paramaFechaFinal) throws Exception { 
			
		addCols(columnasReporte, 3, "MAIN"); 
		
		setColMonths(columnasReporte, paramFechaInicial, paramaFechaFinal);
		 
		
		setColMonths(columnasReporte, paramFechaInicial, paramaFechaFinal);
		 

	}
	
	/*----------------------------------------------------------------------------------------------------------------------------------------------------
	 ***************************************************************************************************************************************************** 
	 *****************************************************************************************************************************************************
	 ****************************************************CONSTRUCCION DE LAS COLUMNAS MENSUAL COMPARATIVO ORIGINAL/AJUSTADO*******************************
	 *****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************
	 *----------------------------------------------------------------------------------------------------------------------------------------------------*/

	/*
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++
	 ***************************************************************************************************************************************************** 
	 *****************************************************************************************************************************************************
	 **************************************************** CONSTRUCCION DE LAS COLUMNAS MENSUAL
	 * ORIGINAL/AJUSTADO*******************************************
	 *****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++
	 */
	private void setHeaderMensualOriginalAjustado(List<AColReporteDto> columnasReporte, String paramFechaInicial,
			String paramaFechaFinal) throws Exception {

		addCols(columnasReporte, 3, "MAIN");
		setColMonths(columnasReporte, paramFechaInicial, paramaFechaFinal);
	}
	/*----------------------------------------------------------------------------------------------------------------------------------------------------
	 ***************************************************************************************************************************************************** 
	 *****************************************************************************************************************************************************
	 ****************************************************CONSTRUCCION DE LAS COLUMNAS MENSUAL ORIGINAL/AJUSTADO*******************************************
	 *****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************
	 *----------------------------------------------------------------------------------------------------------------------------------------------------*/

	/*
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++
	 ***************************************************************************************************************************************************** 
	 *****************************************************************************************************************************************************
	 **************************************************** CONSTRUCCION DE LAS COLUMNAS MENSUAL COMPARATIVO
	 * ORIGINAL/AJUSTADO*******************************************************
	 *****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++
	 */
	private void setHeaderComparativoMensualRealAjustado(List<AColReporteDto> columnasReporte, String paramFechaInicial,
			String paramaFechaFinal) throws Exception {

		addCols(columnasReporte, 3, "MAIN");

		// logger.info("MENSUAL COMPARATIVO REAL AJUSTADO: COLS BASE: 3");
		// PARA REAL
		setColMonths(columnasReporte, paramFechaInicial, paramaFechaFinal);
		 
		// PARA AJUSTADO
		setColMonths(columnasReporte, paramFechaInicial, paramaFechaFinal);

	}
	/*----------------------------------------------------------------------------------------------------------------------------------------------------
	 ***************************************************************************************************************************************************** 
	 *****************************************************************************************************************************************************
	 ****************************************************CONSTRUCCION DE LAS COLUMNAS MENSUAL COMPARATIVO ORIGINAL/AJUSTADO*******************************
	 *****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************
	 *----------------------------------------------------------------------------------------------------------------------------------------------------*/

	/*
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++
	 ***************************************************************************************************************************************************** 
	 *****************************************************************************************************************************************************
	 **************************************************** CONSTRUCCION DE LAS COLUMNAS
	 * ORIGINAL/AJUSTADO/REAL***************************************************
	 * ****
	 *****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++
	 */
	private void setHeaderMensualOriginalAjustadoReal(List<AColReporteDto> columnasReporte, String paramFechaInicial,
			String paramaFechaFinal) throws Exception {

		addCols(columnasReporte, 3, "MAIN");
		// logger.info("MENSUAL ORIGINAL AJUSTADO: COLS BASE: 3");
		// PARA ORIGINAL
		setColMonths(columnasReporte, paramFechaInicial, paramaFechaFinal);
		// PARA AJUSTADO
		setColMonths(columnasReporte, paramFechaInicial, paramaFechaFinal);
		// PARA REAL
		setColMonths(columnasReporte, paramFechaInicial, paramaFechaFinal);

	}
	/*----------------------------------------------------------------------------------------------------------------------------------------------------
	 ***************************************************************************************************************************************************** 
	 *****************************************************************************************************************************************************
	 *****************************************************CONSTRUCCION DE LAS COLUMNAS ORIGINAL/AJUSTADO/REAL*********************************************
	 *****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************
	 *----------------------------------------------------------------------------------------------------------------------------------------------------*/

	/*
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++
	 ***************************************************************************************************************************************************** 
	 *****************************************************************************************************************************************************
	 **************************************************** CONSTRUCCION DE LAS SEMANAL REAL
	 * AJUSTADO*******************************************************
	 *****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++
	 */
	private void setHeaderSemanalRealAjustado(List<AColReporteDto> columnasReporte, String paramFechaInicial,
			String paramaFechaFinal) throws Exception {

		addCols(columnasReporte, 3, "MAIN");
		
		setColMonths(columnasReporte, paramFechaInicial, paramaFechaFinal); 

	}
	/*----------------------------------------------------------------------------------------------------------------------------------------------------
	 ***************************************************************************************************************************************************** 
	 *****************************************************************************************************************************************************
	 ****************************************************CONSTRUCCION DE LAS SEMANAL REAL AJUSTADO*******************************************************
	 *****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************
	 *----------------------------------------------------------------------------------------------------------------------------------------------------*/

	/*
	 * 
	 * 
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * FUNCIONES UTILITARIAS DE SOPORTE PARA LA CONSTRUCCION DEL BODY
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * 
	 * 
	 */

	/*
	 * AUTOR : JUAN RAMIRO BARRERA MARTINEZ NAME : initFields PARAMS : int
	 * paramIdGrupo, int paramNoEmpresa, String paramFechaInicial, String
	 * paramFechaFinal, int paramIdReporte RETURN : NOTHING RESPONSABILITY :
	 * INICIALIZAR LOS CAMPOS DE LA CLASE CON LOS PARAMETROS ENVIADOS
	 * ------->R-UTIL-BODY
	 */
	// *****************************************************************************INIT
	public void initFields(int paramIdGrupo, int paramNoEmpresa, String paramFechaInicial, String paramFechaFinal,
			int paramIdReporte) {

		List<EmpresaDto> empresas;
		EmpresaDto empresa;
		List<GrupoEmpresasDto> grupos;
		GrupoEmpresasDto grupo;
		List<ReporteFlujoDto> reportes;
		ReporteFlujoDto reporte;
		Integer nTempDia;
		Integer nTempMes;
		Integer nTempAnio;

		String sTempDia;
		String sTempMes;
		String sTempAnio;

		IdGrupo = paramIdGrupo;
		NoEmpresa = paramNoEmpresa;

		Calendar fechaI = Calendar.getInstance();
		Calendar fechaH = Calendar.getInstance();
		Calendar fechaM = Calendar.getInstance();
		Calendar fechaF = Calendar.getInstance();

		fechaI.setTime(getTypeDateFromStringFec(paramFechaInicial));
		getFecIni(fechaI);
		nTempDia = fechaI.get(Calendar.DAY_OF_MONTH);
		nTempMes = fechaI.get(Calendar.MONTH) + 1;
		nTempAnio = fechaI.get(Calendar.YEAR);
		sTempDia = nTempDia < 10 ? "0" + nTempDia.toString() : nTempDia.toString();
		sTempMes = nTempMes < 10 ? "0" + nTempMes.toString() : nTempMes.toString();
		sTempAnio = nTempAnio.toString();
		FechaInicial = sTempDia + "/" + sTempMes + "/" + sTempAnio;

		fechaF.setTime(getTypeDateFromStringFec(paramFechaFinal));
		getFecFin(fechaF);
		nTempDia = fechaF.get(Calendar.DAY_OF_MONTH);
		nTempMes = fechaF.get(Calendar.MONTH) + 1;
		nTempAnio = fechaF.get(Calendar.YEAR);
		sTempDia = nTempDia < 10 ? "0" + nTempDia.toString() : nTempDia.toString();
		sTempMes = nTempMes < 10 ? "0" + nTempMes.toString() : nTempMes.toString();
		sTempAnio = nTempAnio.toString();
		FechaFinal = sTempDia + "/" + sTempMes + "/" + sTempAnio;

		FechaHoy = getFechasSistemaBusiness("HOY");
		FechaAyer = getFechasSistemaBusiness("AYER");
		FechaManana = "";

		IdReporte = paramIdReporte;
		grupos = getGrupoEmpresaBusiness(paramIdGrupo);
		grupo = grupos.get(0);
		DescGrupo = grupo.getDesc_super_grupo();

		if (paramNoEmpresa != 0) {
			empresas = getEmpresaBusiness(paramNoEmpresa);
			empresa = empresas.get(0);
			DescEmpresa = empresa.getNomEmpresa();
		}

		reportes = getReporteFlujoBusiness(paramIdReporte);
		reporte = reportes.get(0);
		DescReporte = reporte.getDescripcion();

		IdDivisa = "MN";
		NoUsuario = 2;

	}
	// *****************************************************************************END

	/*
	 * AUTOR : JUAN RAMIRO BARRERA MARTINEZ NAME : subPrintHeaderOfReport PARAMS
	 * : List<HashMap> bodyRow RETURN : NOTHING RESPONSABILITY : 'RESPONSABILITY
	 * : IMPRIMIR LAS CLAVES Y LOS ENCABEZADOS DE LA COLUMNA CERO Y UNO 'NOTES :
	 * SE ESTABLECIERON LAS SIGUIENTES CLAVES Y LEYENDAS PARA ESTA SECCION
	 * 
	 * ' G-E ------ GRUPO EMPRESA ' T-R ------ TITULO DEL REPORTE ' FEC ------
	 * FECHA ' FEC_NO_DIA ------ NO DIA ' FEC_DIA ------ DIA ' FEC_NO_MES ------
	 * FEC MES ' FEC_ANIO ------ ANIO ' FEC_HABIL ------ HABIL ' IS_FS ------
	 * INICIO FIN DE UNA SEMANA ' IM_FM ------ INICIO FIN DE UN MES '
	 * FEC_INI_FIN ------ INICIO-FIN DE PERIODO COMPLETOS ' BAN_INI_HOY_FIN
	 * ------ BANDA INI HOY FIN ' TIPO_INFO ------ TIPO INFO ' R-C ------ TITULO
	 * DE CONCEPTOS ' R-S ------ TITULO DE SALDO INICIAL 'CONSIDERACIONES
	 * IMPORTANTES ' G-E ------ GRUPO O EMPRESA ' T-R ------ TITULO DEL REPORTE
	 * ' FEC ------ ESTABLECIDO PARA LA FILA DE FECHAS NORMALES EJ: '08/04/2011'
	 * ' FEC_NO_DIA ------ ESTABLECIDO PARA LA FILA DE DIAS DE LA FECHA EJ:
	 * '08'-- 8 ' FEC_DIA ------ ESTABLECIDO PARA LA FILA DE DIAS DE LA FECHA EN
	 * LETRA EJ: 'VIERNES' ' FEC_NO_MES ------ ESTABLECIDO PARA LA FILA DE MES
	 * DE LA FECHA EN NUMER EJ: '04' - 4 ' FEC_ANIO ------ ESTABLECIDO PARA LA
	 * FILA DE ANIO DE LA FECHA EJ: 2011 ' FEC_HABIL ------ PARA DIAS : S PARA
	 * VISIBLE N PARA NO VISIBLE ' ------ PARA SEMA : O PARA VISIBLE V PARA NO
	 * VISIBLE ' ------ PARA MES : A PARA VISIBLE ' ------ PARA PERIODO : --- '
	 * IS_FS ------ BANDERAS ESTABLECIDAS PARA EL INICIO O FIN DE UNA SEMANA '
	 * ------ EJ: IS--1-M-1 INICIO DE LA SEMANA UNO DEL MES UNO ' ------ EJ:
	 * IS--3-M-2 INICIO DE LA SEMANA TRES DEL MES DOS ' ------ EJ: FS--1-M-1 FIN
	 * DE LA SEMANA UNO DEL MES UNO ' ------ EJ: FS--3-M-2 FIN DE LA SEMANA TRES
	 * DEL MES DOS ' IM_FM ------ BANDERAS ESTABLECIDAS PARA EL INICIO Y FIN DE
	 * MES ' ------ EJ: IM--1 INICIO DEL MES 1 DEL PERIODO SELECCIONADO ' ------
	 * EJ: FM--1 FIN DEL MES 1 DEL PERIODO SELECCIONADO ' FEC_INI_FIN ------
	 * INICIO-FIN DE PERIODO COMPLETOS Y TIENE LAS SIGUIENTES BANDERAS
	 * ESTABLECIDAS ' ------ EJ: I INICIAL, H FECHA HOY, F FINAL, I_H CUANDO I Y
	 * HOY ES IGUAL, H_F CUANDO HOY Y FINAL SON IGUALES ' BAN_INI_HOY_FIN ------
	 * INICIO-FIN DE PERIODO COMPLETOS HABILES,ES DECIR SI I ES DOMINGO Y F ES
	 * DOMIGO LOS PONE EN DIAS HABILES 'Y TIENE LAS SIGUIENTES BANDERAS
	 * ESTABLECIDAS ' ------ EJ: I INICIAL, H FECHA HOY, F FINAL, I_H CUANDO I Y
	 * HOY ES IGUAL, H_F CUANDO HOY Y FINAL SON IGUALES ' TIPO_INFO ------ TIPO
	 * INFO REAL O AJUSTADO ' R-C ------ TITULO DE CONCEPTOS ' R-S ------ TITULO
	 * DE SALDO INICIAL '*------->R-UTIL-BODY '
	 ********************************************************************************************************************/

	public void subPrintHeaderOfReport(List<HashMap> bodyRow) {

		int ntLastRow;

		// GRUPO - EMPRESA
		addRows(bodyRow, 1);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "R-E");
		subSetValToRow(bodyRow, ntLastRow, funGetTitleGroupOrEmpHeader());

		// CLAVE Y TITULO DEL REPORTE
		addRows(bodyRow, 1);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "T-R");
		subSetValToRow(bodyRow, ntLastRow, DescReporte);

		// CLAVE Y TITULO DE FECHAS
		addRows(bodyRow, 1);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "FEC");
		subSetValToRow(bodyRow, ntLastRow, funGetTitleGenerals(1));

		// CLAVE Y TITULO DE SOPORTES PARA DIAS DE LA FECHA EN NUMERO
		addRows(bodyRow, 1);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "FEC_NO_DIA");
		subSetValToRow(bodyRow, ntLastRow, "NO DIA");

		// CLAVE Y TITULO DE SOPORTES PARA DIAS DE LA FECHA EN LETRA
		addRows(bodyRow, 1);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "FEC_DIA");
		subSetValToRow(bodyRow, ntLastRow, "DIA");

		// CLAVE Y TITULO DE SOPORTES PARA MES EN NUMERO
		addRows(bodyRow, 1);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "FEC_NO_MES");
		subSetValToRow(bodyRow, ntLastRow, "NO MES");

		/*
		 * addRows( bodyRow, 1); ntLastRow = funGetLastRowOfList( bodyRow ) - 1
		 * ; subSetIdToRow(bodyRow, ntLastRow , "TOTAL - DIFERENCIA" );
		 * subSetValToRow(bodyRow, ntLastRow , "DIFERENCIA" );
		 */

		// CLAVE Y TITULO DE SOPORTES PARA MES EN LETRA
		addRows(bodyRow, 1);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "FEC_MES");
		subSetValToRow(bodyRow, ntLastRow, "MES");

		// CLAVE Y TITULO DE SOPORTES PARA EL AÑO
		addRows(bodyRow, 1);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "FEC_ANIO");
		subSetValToRow(bodyRow, ntLastRow, "ANIO");

		// CLAVE Y TITULO DE SOPORTES DETERMINAR SI ES HABIL
		addRows(bodyRow, 1);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "FEC_HABIL");
		subSetValToRow(bodyRow, ntLastRow, "HABIL");

		// CLAVE Y TITULO DE SOPORTES PARA ESTABLECER EL INICIO Y FIN DE UNA
		// SEMANA DE UN MES
		addRows(bodyRow, 1);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "IS_FS");
		subSetValToRow(bodyRow, ntLastRow, "INICIO FIN DE UNA SEMANA");

		// CLAVE Y TITULO DE SOPORTES PARA ESTABLECER EL INICIO Y FIN DE UM MES
		addRows(bodyRow, 1);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "IM_FM");
		subSetValToRow(bodyRow, ntLastRow, "INICIO FIN DE UN MES");

		// CLAVE Y TITULO DE SOPORTES PARA ESTABLECER EL INICIO Y FIN DEL
		// PERIODO SELECCIONADO
		addRows(bodyRow, 1);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "FEC_INI_FIN");
		subSetValToRow(bodyRow, ntLastRow, "INICIO-FIN DE PERIODO COMPLETOS");

		// CLAVE Y TITULO DE SOPORTES PARA ESTABLECER EL INICIO Y FIN DEL
		// PERIODO SELECCIONADO INICIO Y FIN HABILES
		addRows(bodyRow, 1);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "BAN_INI_HOY_FIN");
		subSetValToRow(bodyRow, ntLastRow, "BANDA INI HOY FIN");

		// CLAVE Y TITULO DE SOPORTES PARA ESTABLECER EL TIPO DE INFORMACION
		// REAL O AJUSTADO
		addRows(bodyRow, 1);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "TIPO_INFO");
		subSetValToRow(bodyRow, ntLastRow, "TIPO DE INFORMACION");

		// CLAVE Y TITULO DE SOPORTES PARA CONCEPTOS
		addRows(bodyRow, 1);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "R-C");
		subSetValToRow(bodyRow, ntLastRow, funGetTitleGenerals(2));

		// CLAVE Y TITULO DE SOPORTES PARA EL SALDO INICIAL
		addRows(bodyRow, 1);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "R-S");
		subSetValToRow(bodyRow, ntLastRow, funGetTitleGenerals(3));

	}

	// *****************************************************************************END

	/*
	 * AUTOR : JUAN RAMIRO BARRERA MARTINEZ NAME : subPrintDatesOfColPeriodo
	 * PARAMS : List<HashMap> bodyRow RETURN : NOTHING RESPONSABILITY : PINTA
	 * LOS VALORES DE LAS BANDERAS DE CONTROL DE LA COLUMNA PERIODO
	 * ------->R-UTIL-BODY
	 */
	// *****************************************************************************INIT
	public void subPrintDatesOfColPeriodo(List<HashMap> bodyRow) {

		//
		HashMap rTemp = new HashMap<String, String>();

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC"));
		rTemp.put("col" + lastCol, "TOTAL PERIODO");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA"));
		rTemp.put("col" + lastCol, "10000");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_DIA"));
		rTemp.put("col" + lastCol, "-");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_NO_MES"));
		rTemp.put("col" + lastCol, "-");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_MES"));
		rTemp.put("col" + lastCol, "-");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_ANIO"));
		rTemp.put("col" + lastCol, "-");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_INI_FIN"));
		rTemp.put("col" + lastCol, "-");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_HABIL"));
		rTemp.put("col" + lastCol, "-");

	}
	// *****************************************************************************END

	// *****************************************************************************END

	/*
	 * AUTOR : JUAN RAMIRO BARRERA MARTINEZ NAME : subPrintDatesOfColPeriodo
	 * PARAMS : List<HashMap> bodyRow RETURN : NOTHING RESPONSABILITY : PINTA
	 * LOS VALORES DE LAS BANDERAS DE CONTROL DE LA COLUMNA PERIODO
	 * ------->R-UTIL-BODY
	 */
	// *****************************************************************************INIT
	public void subPrintDatesOfColPeriodoComparativo(List<HashMap> bodyRow) {

		//
		HashMap rTemp = new HashMap<String, String>();

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC"));
		rTemp.put("col" + lastCol, "TOTAL");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA"));
		rTemp.put("col" + lastCol, "10000");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_DIA"));
		rTemp.put("col" + lastCol, "-");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_NO_MES"));
		rTemp.put("col" + lastCol, "-");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_MES"));
		rTemp.put("col" + lastCol, "-");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_ANIO"));
		rTemp.put("col" + lastCol, "-");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_INI_FIN"));
		rTemp.put("col" + lastCol, "-");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_HABIL"));
		rTemp.put("col" + lastCol, "-");

	}
	// *****************************************************************************END

	public void subPrintDatesOfColPeriodoComparativo2(List<HashMap> bodyRow) {

		//
		HashMap rTemp = new HashMap<String, String>();

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC"));
		rTemp.put("col" + lastCol, "TOTAL DIFERENCIA");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA"));
		rTemp.put("col" + lastCol, "10000000");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_DIA"));
		rTemp.put("col" + lastCol, "-");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_NO_MES"));
		rTemp.put("col" + lastCol, "-");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_MES"));
		rTemp.put("col" + lastCol, "-");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_ANIO"));
		rTemp.put("col" + lastCol, "-");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_INI_FIN"));
		rTemp.put("col" + lastCol, "-");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_HABIL"));
		rTemp.put("col" + lastCol, "-");

	}

	/*
	 * 'AUTOR : JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subPrintDatesOfColMonths 'PARAMS : fecha y posicion de la columna donde
	 * se van a imprimir los texts 'RETURN : NONE 'RESPONSABILITY : PINTA LOS
	 * DATOS DE ENCABEZADOS DE LAS COLUMNAS DE MESES '*------->R-UTIL-BODY
	 */
	public void subPrintDatesOfColMonths1(List<HashMap> bodyRow, String pFecha, int iMes, String strTipoInfo) {

		//
		HashMap rTemp = new HashMap<String, String>();

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC"));
		rTemp.put("col" + lastCol, subGetMonthOfDateInLetter(pFecha) + " " + strTipoInfo);

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA"));
		rTemp.put("col" + lastCol, "1000");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_DIA"));
		rTemp.put("col" + lastCol, "M--" + iMes);

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_NO_MES"));
		rTemp.put("col" + lastCol, subGetMonthOfDateInNumber(pFecha));

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_MES"));
		rTemp.put("col" + lastCol, subGetMonthOfDateInLetter(pFecha).toUpperCase());

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_ANIO"));
		rTemp.put("col" + lastCol, subGetYearOfDate(pFecha));

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_INI_FIN"));
		rTemp.put("col" + lastCol, "-");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_HABIL"));
		rTemp.put("col" + lastCol, "A");

	}

	public void subPrintDatesOfColMonthDIFERENCIA(List<HashMap> bodyRow, String pFecha, String strTipoInfo) {

		//
		HashMap rTemp = new HashMap<String, String>();

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC"));
		rTemp.put("col" + lastCol, "TOTAL diferencia");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA"));
		rTemp.put("col" + lastCol, "1000000");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_DIA"));
		rTemp.put("col" + lastCol, "M--" );

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_NO_MES"));
		rTemp.put("col" + lastCol, subGetMonthOfDateInNumber(pFecha));

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_MES"));
		rTemp.put("col" + lastCol, subGetMonthOfDateInLetter(pFecha).toUpperCase());

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_ANIO"));
		rTemp.put("col" + lastCol, subGetYearOfDate(pFecha));

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_INI_FIN"));
		rTemp.put("col" + lastCol, "-");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_HABIL"));
		rTemp.put("col" + lastCol, "A");

	}
	// *****************************************************************************END

	/*
	 * 'AUTOR : JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subPrintDatesOfColMonths 'PARAMS : fecha y posicion de la columna donde
	 * se van a imprimir los texts 'RETURN : NONE 'RESPONSABILITY : PINTA LOS
	 * DATOS DE ENCABEZADOS DE LAS COLUMNAS DE MESES '*------->R-UTIL-BODY
	 */
	public void subPrintDatesOfColMonths(List<HashMap> bodyRow, String pFecha, int iMes, String strTipoInfo) {

		//
		HashMap rTemp = new HashMap<String, String>();

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC"));
		// rTemp.put( "col" + lastCol, subGetMonthOfDateInLetter( pFecha ));
		rTemp.put("col" + lastCol, subGetMonthOfDateInLetter(pFecha));

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA"));
		rTemp.put("col" + lastCol, "1000");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_DIA"));
		rTemp.put("col" + lastCol, "M--" + iMes);
		// logger.info("mes "+iMes);
		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_NO_MES"));
		rTemp.put("col" + lastCol, subGetMonthOfDateInNumber(pFecha));

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_MES"));
		rTemp.put("col" + lastCol, subGetMonthOfDateInLetter(pFecha).toUpperCase());

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_ANIO"));
		rTemp.put("col" + lastCol, subGetYearOfDate(pFecha));

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_INI_FIN"));
		rTemp.put("col" + lastCol, "-");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_HABIL"));
		rTemp.put("col" + lastCol, "A");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "TIPO_INFO"));
		rTemp.put("col" + lastCol, strTipoInfo);

	}
	// *****************************************************************************END

	/*
	 * 'AUTOR : JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subPrintDatesOfColWeeks 'PARAMS : fecha y posicion de la columna donde se
	 * van a imprimir los texts 'RETURN : NONE 'RESPONSABILITY : PINTA LOS DATOS
	 * DE ENCABEZADOS DE LAS COLUMNAS DE MESES '*------->R-UTIL-BODY
	 */

	public void subPrintDatesOfColWeeks(List<HashMap> bodyRow, String pFecha, int iMes, int iWeek, String strBandWeek) {

		//
		HashMap rTemp = new HashMap<String, String>();

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC"));
		rTemp.put("col" + lastCol, "SEMANA " + iWeek);

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA"));
		rTemp.put("col" + lastCol, "100");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_DIA"));
		rTemp.put("col" + lastCol, "W--" + iWeek + "-M--" + iMes);

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_NO_MES"));
		rTemp.put("col" + lastCol, subGetMonthOfDateInNumber(pFecha));

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_MES"));
		rTemp.put("col" + lastCol, subGetMonthOfDateInLetter(pFecha).toUpperCase());

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_ANIO"));
		rTemp.put("col" + lastCol, subGetYearOfDate(pFecha));

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_INI_FIN"));
		rTemp.put("col" + lastCol, "-");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_HABIL"));
		rTemp.put("col" + lastCol, subWeekIsInhabil(pFecha, strBandWeek));

	}
	
	public void subPrintDatesOfColWeeksPrueba(List<HashMap> bodyRow, String pFecha, int iMes, int iWeek, String strBandWeek,String tipo) {

		//
		HashMap rTemp = new HashMap<String, String>();

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC"));
		rTemp.put("col" + lastCol, "SEMANA " + iWeek + tipo);

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA"));
		rTemp.put("col" + lastCol, "100");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_DIA"));
		rTemp.put("col" + lastCol, "W--" + iWeek + "-M--" + iMes);

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_NO_MES"));
		rTemp.put("col" + lastCol, subGetMonthOfDateInNumber(pFecha));

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_MES"));
		rTemp.put("col" + lastCol, subGetMonthOfDateInLetter(pFecha).toUpperCase());

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_ANIO"));
		rTemp.put("col" + lastCol, subGetYearOfDate(pFecha));

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_INI_FIN"));
		rTemp.put("col" + lastCol, "-");

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_HABIL"));
		rTemp.put("col" + lastCol, subWeekIsInhabil(pFecha, strBandWeek));

	}
	// *****************************************************************************END

	/*
	 * 'AUTOR : JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subPrintDatesOfColDays 'PARAMS : fecha y posicion de la columna donde se
	 * van a imprimir los texts 'RETURN : NONE 'RESPONSABILITY : PINTA LOS DATOS
	 * DE ENCABEZADOS DE LAS COLUMNAS DE MESES '*------->R-UTIL-BODY
	 */

	public void subPrintDatesOfColDays(List<HashMap> bodyRow, String pFecha, int iMes, int iWeek, int iDay,
			long lngNoDays, String strBandWeek) {

		//
		HashMap rTemp = new HashMap<String, String>();
		String isDayInhabil;

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC"));
		rTemp.put("col" + lastCol, pFecha);

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA"));
		rTemp.put("col" + lastCol, subGetDayOfDate(pFecha));

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_DIA"));
		rTemp.put("col" + lastCol, subGetDayOfDateFormatLetter(pFecha));

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_NO_MES"));
		rTemp.put("col" + lastCol, subGetMonthOfDateInNumber(pFecha));

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_MES"));
		rTemp.put("col" + lastCol, subGetMonthOfDateInLetter(pFecha).toUpperCase());

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_ANIO"));
		rTemp.put("col" + lastCol, subGetYearOfDate(pFecha));

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_INI_FIN"));
		rTemp.put("col" + lastCol, funIsIniOrTodayOrFin(pFecha));

		isDayInhabil = getFecInhabilBusiness(pFecha) ? "N" : "S";

		rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_HABIL"));
		rTemp.put("col" + lastCol, isDayInhabil);

		// ESTABLECEMOS EN LA FILA IM_FM EL INICIO Y FINAL DE LA SEMANA
		if (iDay == 1) {
			rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "IS_FS"));
			rTemp.put("col" + lastCol, "IS--" + iWeek + "-M--" + iMes);

		}

		// ESTABLECEMOS EN LA FILA IM_FM EL INICIO Y FINAL DE LA SEMANA
		if (iDay == lngNoDays) {
			rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "IS_FS"));
			rTemp.put("col" + lastCol, "FS--" + iWeek + "-M--" + iMes);

		}

		// ESTABLECEMOS EN LA FILA IM_FM EL INICIO Y FINAL DEL MES

		if (strBandWeek.equals("FIRST_WEEK") == true) {

			if (iDay == 1) {
				rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "IM_FM"));
				rTemp.put("col" + lastCol, "IM--" + iMes);
			}

		}

		if (strBandWeek.equals("LAST_WEEK") == true) {

			if (iDay == lngNoDays) {
				rTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "IM_FM"));
				rTemp.put("col" + lastCol, "FM--" + iMes);

			}

		}

	}
	// *****************************************************************************END

	/*
	 * 'AUTOR : JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subGetMonthOfDateInLetter 'PARAMS : String: fecha 'RETURN : String con el
	 * mes en letra 'RESPONSABILITY : OBTENER EL MES DE UNA FECHA EN FORMATO DE
	 * LETRA '*------->R-UTIL-BODY
	 */
	public String subGetMonthOfDateInLetter(String pFecha) {

		String result = "";

		Calendar cFecha = GregorianCalendar.getInstance();

		cFecha.setTime(getTypeDateFromStringFec(pFecha));

		SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM");

		result = dateFormat.format(cFecha.getTime());

		return result;

	}
	// *****************************************************************************END

	/*
	 * 'AUTOR : JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subGetMonthOfDateInNumber 'PARAMS : String: fecha 'RETURN : String con el
	 * mes en numero 'RESPONSABILITY : OBTENER EL MES DE UNA FECHA EN FORMATO DE
	 * Número '*------->R-UTIL-BODY
	 */
	public String subGetMonthOfDateInNumber(String pFecha) {

		String result = "";

		Calendar cFecha = GregorianCalendar.getInstance();

		cFecha.setTime(getTypeDateFromStringFec(pFecha));

		SimpleDateFormat dateFormat = new SimpleDateFormat("MM");

		result = dateFormat.format(cFecha.getTime());

		return result;

	}

	/*
	 * 'AUTOR : JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subGetYearOfDate 'PARAMS : String: fecha 'RETURN : String con el año en
	 * numero 'RESPONSABILITY : OBTENER EL año DE UNA FECHA EN FORMATO DE Número
	 * '*------->R-UTIL-BODY
	 */
	public String subGetYearOfDate(String pFecha) {

		String result = "";

		Calendar cFecha = GregorianCalendar.getInstance();

		cFecha.setTime(getTypeDateFromStringFec(pFecha));

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");

		result = dateFormat.format(cFecha.getTime());

		return result;

	}
	// *****************************************************************************END

	/*
	 * 'AUTOR : JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : FUNCTION 'NAME :
	 * getRowOfIdHeaderDates 'PARAMS : paramIdHeadFec, identificador del
	 * elemento del encabezado en la seccion de fecha que se quiere recuperar
	 * 'RETURN : long, numero de fila donde se encuentra el identificador
	 * 'RESPONSABILITY : RETORNA EL NUMERO DE FILA DONDE SE ENCUENTRA EL
	 * IDENTIFICADOR DEL SEGMENTO ENCABEZADO DE FECHAS 'NOTA SE DEFINEN LAS
	 * SIGUIENTES COSTANTES QUE SE RECIBEN COMO PARAMETROS ' FEC 'FECHA '
	 * FEC_NO_DIA 'NO DIA ' FEC_DIA 'DIA ' FEC_NO_MES 'MES ' FEC_ANIO 'ANIO '
	 * FEC_INI_FIN 'INICIO-FIN DE MES ' FEC_HABIL 'HABIL '*------->R-UTIL-BODY
	 */

	public int getRowOfIdHeaderDates(List<HashMap> bodyRow, String sIdforSearch) {

		int cont = 0;
		int nResult = 0;

		Iterator listIterador = bodyRow.listIterator();

		int x = 0;

		while (listIterador.hasNext()) {

			//// logger.info("Iterador: " + (x += 1) );
			x += 1;

			HashMap rtemp = (HashMap) listIterador.next();

			Set set = rtemp.entrySet();

			Iterator mapIterator = set.iterator();

			while (mapIterator.hasNext()) {

				Map.Entry entry = (Map.Entry) mapIterator.next();

				// logger.info("informacion "+entry.getKey() + "--" +
				// entry.getValue());
				// System.out.println("informacion "+entry.getKey() + " -- " +
				// entry.getValue());
				// if( entry.getValue().toString() == sIdforSearch )
				if (entry.getValue().toString().equals(sIdforSearch)) {

					nResult = x;

				}

				if (entry.getKey() == "col1") {
					cont++;

				}

			}

		}
		/// logger.info( "MENSUAL AJUSTADO: " + bodyRow ) ;

		return nResult - 1;

	}

	// *****************************************************************************END

	/*
	 * 'AUTOR : JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subGetDayOfDate 'PARAMS : String: fecha 'RETURN : String con el año en
	 * numero 'RESPONSABILITY : OBTENER EL dia DE UNA FECHA EN FORMATO DE Número
	 * '*------->R-UTIL-BODY
	 */
	public String subGetDayOfDate(String pFecha) {

		String result = "";

		Calendar cFecha = GregorianCalendar.getInstance();

		cFecha.setTime(getTypeDateFromStringFec(pFecha));

		SimpleDateFormat dateFormat = new SimpleDateFormat("d");

		result = dateFormat.format(cFecha.getTime());

		return result;

	}
	// *****************************************************************************END

	/*
	 * 'AUTOR : JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subGetDayOfDateFormatLetter 'PARAMS : String: fecha 'RETURN : String con
	 * el año en numero 'RESPONSABILITY : OBTENER EL dia DE UNA FECHA EN FORMATO
	 * DE Número '*------->R-UTIL-BODY
	 */
	public String subGetDayOfDateFormatLetter(String pFecha) {

		String result = "";

		Calendar cFecha = GregorianCalendar.getInstance();

		cFecha.setTime(getTypeDateFromStringFec(pFecha));

		SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");

		result = dateFormat.format(cFecha.getTime());

		return result;

	}
	// *****************************************************************************END

	/*
	 * 'AUTOR : JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subWeekIsInhabil 'PARAMS : noSemana, numero de semana del que se pintaran
	 * los dias, fecha inicial y fecha final del mese que se van a pintar los
	 * dias ' indica el tipo de semana que es la inicial del mes dado, la final,
	 * o alguna de enmedio 'FIRST_WEEK 'MIDDLE WEEK 'LAST_WEEK 'RETURN : NONE
	 * 'RESPONSABILITY : DETERMINA UN ESTATUS QUE ESTABLECE SI UNA SEMANA ES
	 * VISIBLE U OCULTABLE '*------->R-UTIL-BODY
	 */
	public String subWeekIsInhabil(String fecInicial, String strBandWeek) {

		String strFecIniWeek;
		String strFecFinWeek;
		String isDayInhabil = "";
		String resp = "O";

		long lngNoDays = 0;

		Calendar calIni = Calendar.getInstance();
		Calendar calFin = Calendar.getInstance();

		strFecIniWeek = fecInicial;
		strFecFinWeek = subGetRangeOfWeek(strFecIniWeek, strBandWeek);

		calIni.setTime(getTypeDateFromStringFec(strFecIniWeek));
		calFin.setTime(getTypeDateFromStringFec(strFecFinWeek));

		try {
			lngNoDays = getDaysBetweenTwoDates(calIni, calFin);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int iDay = 1; iDay <= lngNoDays; iDay++) {

			isDayInhabil = getFecInhabilBusiness(strFecIniWeek) ? "N" : "S";

			if (isDayInhabil.equals("S")) {
				resp = "V";
				break;
			}

			calIni.add(Calendar.DATE, 1);
			strFecIniWeek = subFecFormatoString(calIni);

		}

		return resp;

	}
	// *****************************************************************************END

	/*
	 * 'AUTOR : JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subGetRangeOfWeek 'PARAMS : 'RETURN : FECHA 'RESPONSABILITY : OBTIENE UNA
	 * FECHA DE UN RANGO DE UNA SEMANA DADA '*------->R-UTIL-BODY
	 */
	public String subGetRangeOfWeek(String fecInicial, String strBandWeek) {

		Calendar c = Calendar.getInstance();
		Calendar cAux = Calendar.getInstance();
		Calendar cReturn = Calendar.getInstance();

		Integer nTempDia;
		Integer nTempMes;
		Integer nTempAnio;

		String sTempDia;
		String sTempMes;
		String sTempAnio;

		String fecReturn = "";

		//// logger.info( "NO MAMES " + fecInicial );

		c.setTime(getTypeDateFromStringFec(fecInicial));
		c.setFirstDayOfWeek(Calendar.SUNDAY);
		c.set(Calendar.DAY_OF_WEEK, c.get(c.DAY_OF_WEEK));

		if (strBandWeek == "FIRST_WEEK") {
			DateFormat df = new SimpleDateFormat("EEE dd/MM/yyyy");

			for (int i = 0; i < 7; i++) {

				cReturn = c;

				if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {

					break;
				}

				c.add(Calendar.DATE, 1);

			}
		}

		if (strBandWeek == "MIDDLE_WEEK") {
			DateFormat df = new SimpleDateFormat("EEE dd/MM/yyyy");

			for (int i = 0; i < 7; i++) {

				if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
					break;
				}

				c.add(Calendar.DATE, 1);
				cReturn = c;

			}
		}

		if (strBandWeek == "LAST_WEEK") {

			int iMonth = c.get(Calendar.MONTH);

			for (int i = 0; i < 7; i++) {

				c.add(Calendar.DATE, 1);

				if (c.get(Calendar.MONTH) != iMonth) {
					cReturn.add(Calendar.DATE, -1);
					break;
				}

				cReturn = c;

			}

		}

		nTempDia = cReturn.get(Calendar.DAY_OF_MONTH);
		nTempMes = cReturn.get(Calendar.MONTH) + 1;
		nTempAnio = cReturn.get(Calendar.YEAR);

		sTempDia = nTempDia < 10 ? "0" + nTempDia.toString() : nTempDia.toString();
		sTempMes = nTempMes < 10 ? "0" + nTempMes.toString() : nTempMes.toString();
		sTempAnio = nTempAnio.toString();

		fecReturn = sTempDia + "/" + sTempMes + "/" + sTempAnio;

		return fecReturn;

	}
	// *****************************************************************************END

	/*
	 * 'AUTOR : JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subFecFormatoString 'PARAMS : Calendar con la fecha 'RETURN : string
	 * FECHA 'RESPONSABILITY : OBTIENE UNA FECHA EN FORMATO DE FECHA
	 * '*------->R-UTIL-BODY
	 */

	public String subFecFormatoString(Calendar cal) {

		Integer nTempDia;
		Integer nTempMes;
		Integer nTempAnio;

		String sTempDia;
		String sTempMes;
		String sTempAnio;

		String fecReturn = "";

		nTempDia = cal.get(Calendar.DAY_OF_MONTH);
		nTempMes = cal.get(Calendar.MONTH) + 1;
		nTempAnio = cal.get(Calendar.YEAR);

		sTempDia = nTempDia < 10 ? "0" + nTempDia.toString() : nTempDia.toString();
		sTempMes = nTempMes < 10 ? "0" + nTempMes.toString() : nTempMes.toString();
		sTempAnio = nTempAnio.toString();

		fecReturn = sTempDia + "/" + sTempMes + "/" + sTempAnio;

		return fecReturn;

	}
	// *****************************************************************************END

	/*
	 * 'AUTOR : JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : Funcion 'NAME :
	 * funGetTitleGenerals 'PARAMS : NONE 'RETURN : Retorna una cadena con un
	 * titulo 'RESPONSABILITY : Retorna titulos generales para el reporte
	 * '*------->R-UTIL-BODY
	 */
	public String funGetTitleGenerals(int nTitle) {

		String strRespCad;

		strRespCad = "";

		switch (nTitle) {
		case 1: // "COL_FECHA"
			strRespCad = "                          FECHA";
			break;

		case 2: // "COL_CONCEPTOS"

			strRespCad = "                   CONCEPTOS";
			break;

		case 3: // "COL_SAL_INI"
			strRespCad = "     SALDO INICIAL";
			break;

		}

		return strRespCad;

	}
	// *****************************************************************************END

	/*
	 * 'AUTOR : JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : Funcion 'NAME :
	 * funGetTitleGroupOrEmpHeader 'PARAMS : NONE 'RETURN : Retorna una cadena
	 * 'RESPONSABILITY : Obtiene un titulo para el reporte con la asociacion
	 * grupo o empresa '*------->R-UTIL-BODY
	 */
	public String funGetTitleGroupOrEmpHeader() {

		String strRespCad;

		strRespCad = "";

		if (NoEmpresa != 0) {

			strRespCad = "EMPRESA: " + DescEmpresa;
		} else {

			strRespCad = "GRUPO: " + DescGrupo;
		}

		return strRespCad;

	}
	// *****************************************************************************END

	/*
	 * AUTOR : JUAN RAMIRO BARRERA MARTINEZ TYPE OBJECT : FUNCION NAME :
	 * funGetGrupo PARAMS : NONE RETURN : LONG, LA ULTIMA FILA DEL GRID
	 * RESPONSABILITY : OBTENER LA ULTIMA FILA AGREGADA AL GRID
	 * '*------->R-UTIL-BODY
	 */
	public int funGetLastRowOfList(List<HashMap> bodyRow) {

		int ntLastRow = 0;

		ntLastRow = bodyRow.size();

		return ntLastRow;

	}
	// *****************************************************************************END

	/*
	 * ' AUTOR :JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subSetIdToRow 'PARAMS : ByVal nRow As Long - NUMERO DE LA FILA DONDE SE
	 * ESTABLECERA EL ID ' ByVal sId As String - ID CON CON EL QUE SE
	 * IDENTIFICARA LA FILA 'RETURN : NONE 'RESPONSABILITY : IMPRIMIR EL
	 * ENCABEZADO DEL REPORTE 'NOTES : 1 G-E SIGNIFICA GRUPO-EMPRESA, IDENTIFICA
	 * LA FILA DONDE SE ESTABLECE EL TITULO DE LA EMPRESA ' col1 ES EL
	 * IDENTIFICADOR DE LA COLUMNAS DONDE SE ESTABLECERAN LOS IDs
	 * '*------->R-UTIL-BODY
	 */
	public void subSetIdToRow(List<HashMap> bodyRow, Integer nRow, String sId) {

		HashMap rTemp = new HashMap<String, String>();
		rTemp = bodyRow.get(nRow);
		rTemp.put("col1", sId);

	}
	// *****************************************************************************END

	/*
	 * ' AUTOR :JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subSetValToRowConcepts 'PARAMS : ByVal nRow As Long - NUMERO DE LA FILA
	 * DONDE SE ESTABLECERA EL ID ' ByVal sVal As String - VALOR QUE SE LE DARA
	 * A LA FILA DE CONCEPTOS 'RETURN : NONE 'RESPONSABILITY : IMPRIMIR EL
	 * ENCABEZADO DEL REPORTE 'NOTES : 1 G-E SIGNIFICA GRUPO-EMPRESA, IDENTIFICA
	 * LA FILA DONDE SE ESTABLECE EL TITULO DE LA EMPRESA ' col2 ES EL
	 * IDENTIFICADOR DE LA COLUMNAS DONDE SE ESTABLECERAN LOS TITULOS DE
	 * CONCEPTOS '*------->R-UTIL-BODY
	 */
	public void subSetValToRow(List<HashMap> bodyRow, Integer nRow, String sVal) {

		HashMap rTemp = new HashMap<String, String>();
		rTemp = bodyRow.get(nRow);
		rTemp.put("col2", sVal);

	}
	// *****************************************************************************END

	/*
	 * AUTOR : JUAN RAMIRO BARRERA MARTINEZ NAME : addRows PARAMS :
	 * columnasReporte-> LISTA DE FILAS : paramNumCols -> NUMERO DE FILAS RETURN
	 * : NOTHING RESPONSABILITY : AGREGAR UN NUMERO DE FILAS A UNA LISTA DE
	 * FILAS '*------->R-UTIL-BODY
	 */
	// *****************************************************************************INIT
	public void addRows(List<HashMap> bodyRow, int paramNumRows) {

		Integer numRowsOfLista;

		numRowsOfLista = bodyRow.size();

		for (int i = 0; i < paramNumRows; i++) {

			numRowsOfLista += 1;
			HashMap temp = new HashMap();
			temp.put("col" + numRowsOfLista.toString(), "");
			//System.out.println("addRows" + "col" + numRowsOfLista.toString());
			bodyRow.add(temp);

		}

	}
	// *****************************************************************************END

	/*
	 * AUTOR : JUAN RAMIRO BARRERA MARTINEZ NAME : getNumCols PARAMS : String
	 * paramFechaFinal, String paramFechaInicial RETURN : NOTHING RESPONSABILITY
	 * : DETERMINA Y DEVUELVE EL NUMERO DE COLUMNAS GENERADAS, SEGUN EL PERIODO
	 * DEFINIDO POR LAS FECHAS DE LOS PARAMETROS '*------->R-UTIL-BODY
	 */
	// *****************************************************************************INIT
	public int getNumCols(String paramFechaFinal, String paramFechaInicial) {

		int numCols = 0;

		List<AColReporteDto> columnasReporte = new ArrayList<AColReporteDto>();

		try {

			setHeaderDiarioReal(columnasReporte, paramFechaInicial, paramFechaFinal);
			numCols = columnasReporte.size();

		} catch (Exception e) {

			e.printStackTrace();
		}

		return numCols;
	}
	// *****************************************************************************END

	/*
	 * AUTOR : JUAN RAMIRO BARRERA MARTINEZ NAME : getFecInhabilBusiness PARAMS
	 * : String fecha RETURN : TRUE O FALSE RESPONSABILITY : DETERMINAR SI UNA
	 * FECHA PASADA COMO PARAMETRO ES INHABIL '*------->R-UTIL-BODY
	 */
	public boolean getFecInhabilBusiness(String fecha) {

		boolean resp = false;
		List<FechaInabilDto> fechas = cashFlowDao.getFecInhabilDao(fecha);
		/*
		 * if(fechas == null){ FechaInabilDto fechaDummy = new FechaInabilDto();
		 * String dummy = "01/01/2000"; fechaDummy.setFecha(dummy); fechas = new
		 * ArrayList<FechaInabilDto>(); fechas.add(fechaDummy); }
		 */
		if (fechas == null && fechas.size() > 0) {
			resp = true;
		}

		if (subGetDayOfDateFormatLetter(fecha).equals("sábado")) {
			resp = true;
		}

		if (subGetDayOfDateFormatLetter(fecha).equals("domingo")) {
			resp = true;
		}

		return resp;

	}
	// *****************************************************************************END

	/*
	 * AUTOR : JUAN RAMIRO BARRERA MARTINEZ NAME : getFechasSistemaBusiness
	 * PARAMS : String getFechasSistemaBusiness RETURN : String con la fecha de
	 * sistema pedida RESPONSABILITY : Obtener la fechas del sistema
	 * '*------->R-UTIL-BODY
	 */
	public String getFechasSistemaBusiness(String sTipoFecha) {

		String resp = "";

		List<FechasDto> fechas = cashFlowDao.getFechasSistemaDao();
		if (fechas == null) {
			FechasDto fechaDummy = new FechasDto();
			String dummy = "01/01/2000";
			fechaDummy.setFec_ayer(dummy);
			fechaDummy.setFec_hoy(dummy);
			fechaDummy.setFec_manana(dummy);
			fechas = new ArrayList<FechasDto>();
			fechas.add(fechaDummy);
		}

		Iterator iterador = fechas.iterator();

		FechasDto fecha = null;

		while (iterador.hasNext()) {
			fecha = (FechasDto) iterador.next();
		}

		if (sTipoFecha.equals("AYER"))
			resp = fecha.getFec_ayer();

		if (sTipoFecha.equals("HOY"))
			resp = fecha.getFec_hoy();

		if (sTipoFecha.equals("MANANA"))
			resp = fecha.getFec_manana();

		// logger.info( "?????????????????" + resp );
		// resp = sf.format( fecResult );

		return resp;

	}
	// *****************************************************************************END

	/*
	 * 'AUTOR : JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : fucnion 'NAME :
	 * funIsIniOrTodayOrFin 'PARAMS : FECHA A ANALIZAR 'RETURN : VARCHAR, I SI
	 * ES INICIAL H SI ES HOY F SI ES FINAL O N SI NINGUNA 'RESPONSABILITY :
	 * FUNCION QUE DETERMINA SI EL TIPO DE FECHA ENVIADA CORRESPONDE CON LA DE
	 * INICIO FIN U HOY '*------->R-UTIL-BODY
	 *******************************************************************************************/
	public String funIsIniOrTodayOrFin(String paramFecha) {

		String strResp = "-";

		if (FechaInicial.equals(paramFecha))
			strResp = "I";

		if (FechaHoy.equals(paramFecha))
			strResp = "H";

		if (FechaFinal.equals(paramFecha))
			strResp = "F";

		if (FechaInicial.equals(paramFecha) && FechaHoy.equals(paramFecha))
			strResp = "I_H";

		if (FechaHoy.equals(paramFecha) && FechaFinal.equals(paramFecha))
			strResp = "H_F";

		return strResp;
	}
	// *****************************************************************************END

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 6 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subPrintTitulosTiposConceptosDiario 'PARAMS : NONE 'RETURN : NONE
	 * 'RESPONSABILITY : PREPARA Y LLENA LOS DATOS CON LOS TITULOS, TIPOS Y
	 * CONCEPTOS DE FLUJO EN LA COLUMNA UNO '*------->R-UTIL-BODY
	 * '************************************************************************
	 * *******************************************
	 */
	private void subPrintTitulosTiposConceptos(List<HashMap> bodyRow) {

		subPrintTituloIngresoEgreso(bodyRow);

	}
	
	private void subPrintTitulosTiposConceptosOAR(List<HashMap> bodyRow) {

		subPrintTituloIngresoEgresoOAR(bodyRow);

	}

	private void subPrintTitulosTiposConceptosOriginalAjustado(List<HashMap> bodyRow) {

		subPrintTituloIngresoEgresoOriginalAjustado(bodyRow);

	}

	private void subPrintTitulosTiposConceptosOriginal(List<HashMap> bodyRow) {

		subPrintTituloIngresoEgresoOriginal(bodyRow);

	}

	private void subPrintTitulosTiposConceptosAjustado(List<HashMap> bodyRow) {

		subPrintTituloIngresoEgresoAjustado(bodyRow);

	}

	/* HECTOR INICIO */
	private void subPrintTitulosTiposConceptosMensual(List<HashMap> bodyRow) {

		subPrintTituloIngresoEgresoMensual(bodyRow);

	}
	
	private void subPrintTitulosTiposConceptosMensualAR(List<HashMap> bodyRow) {

		subPrintTituloIngresoEgresoMensual(bodyRow);

	}
	
	/* HECTOR FIN */

	private void subPrintTitulosTiposConceptosComparativoAjustadoReal(List<HashMap> bodyRow) {

		subPrintTituloIngresoEgresoComparativoMensualAjustadoReal(bodyRow);

	}

	// *****************************************************************************END

	/*
	 * 'AUTOR : JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subPrintTituloIngresoEgreso 'PARAMS : NONE 'RETURN : NONE 'RESPONSABILITY
	 * : PINTA INGRESO O EGRESO Y LLAMA PARA PINTAR LOS TIPOS DE CONCEPTOS
	 * '*------->R-UTIL-BODY
	 */
	private void subPrintTituloIngresoEgreso(List<HashMap> bodyRow) {

		List<TipoMovto> tiposMovto = cashFlowDao.FunSQLGetIngresoEgreso();
		int ntLastRow;
		int resp = 0;

		if (tiposMovto.size() == 0)
			return;

		Iterator it = tiposMovto.listIterator();

		while (it.hasNext()) {

			TipoMovto tipoMovto = (TipoMovto) it.next();

			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetValToRow(bodyRow, ntLastRow, tipoMovto.getDescTipoMovto());

			subPrintTiposConceptos(bodyRow, tipoMovto.getIdTipoMovto());

			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetIdToRow(bodyRow, ntLastRow, "T" + tipoMovto.getIdTipoMovto());
			subSetValToRow(bodyRow, ntLastRow, "TOTAL" + tipoMovto.getDescTipoMovto());
			resp = cashFlowDao.funSQLUpdateRowForTotalIngresoEgreso(NoUsuario, tipoMovto.getIdTipoMovto(), ntLastRow);
			addRows(bodyRow, 1);
		}

		subPrintSaldos(bodyRow);

	}

	private void subPrintTituloIngresoEgresoOAR(List<HashMap> bodyRow) {

		List<TipoMovto> tiposMovto = cashFlowDao.FunSQLGetIngresoEgreso();
		int ntLastRow;
		int resp = 0;

		if (tiposMovto.size() == 0)
			return;

		Iterator it = tiposMovto.listIterator();

		while (it.hasNext()) {

			TipoMovto tipoMovto = (TipoMovto) it.next();

			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetValToRow(bodyRow, ntLastRow, tipoMovto.getDescTipoMovto());

			subPrintTiposConceptosOAR(bodyRow, tipoMovto.getIdTipoMovto());

			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetIdToRow(bodyRow, ntLastRow, "T" + tipoMovto.getIdTipoMovto());
			subSetValToRow(bodyRow, ntLastRow, "TOTAL" + tipoMovto.getDescTipoMovto());
			resp = cashFlowDao.funSQLUpdateRowForTotalIngresoEgresoOAR(NoUsuario, tipoMovto.getIdTipoMovto(), ntLastRow);
			addRows(bodyRow, 1);
		}

		subPrintSaldosOAR(bodyRow);

	}
	private void subPrintTituloIngresoEgresoOriginalAjustado(List<HashMap> bodyRow) {

		List<TipoMovto> tiposMovto = cashFlowDao.FunSQLGetIngresoEgreso();
		int ntLastRow;
		int resp = 0;

		if (tiposMovto.size() == 0)
			return;

		Iterator it = tiposMovto.listIterator();

		while (it.hasNext()) {

			TipoMovto tipoMovto = (TipoMovto) it.next();

			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetValToRow(bodyRow, ntLastRow, tipoMovto.getDescTipoMovto());

			subPrintTiposConceptosOriginalAjustado(bodyRow, tipoMovto.getIdTipoMovto());

			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetIdToRow(bodyRow, ntLastRow, "T" + tipoMovto.getIdTipoMovto());
			subSetValToRow(bodyRow, ntLastRow, "TOTAL " + tipoMovto.getDescTipoMovto());
			resp = cashFlowDao.funSQLUpdateRowForTotalIngresoEgresoOriginalAjustado(NoUsuario,
					tipoMovto.getIdTipoMovto(), ntLastRow);
			addRows(bodyRow, 1);
		}

		subPrintSaldosOriginalAjustado(bodyRow);

	}

	private void subPrintTituloIngresoEgresoOriginal(List<HashMap> bodyRow) {

		List<TipoMovto> tiposMovto = cashFlowDao.FunSQLGetIngresoEgreso();
		int ntLastRow;
		int resp = 0;

		if (tiposMovto.size() == 0)
			return;

		Iterator it = tiposMovto.listIterator();

		while (it.hasNext()) {

			TipoMovto tipoMovto = (TipoMovto) it.next();

			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetValToRow(bodyRow, ntLastRow, tipoMovto.getDescTipoMovto());

			subPrintTiposConceptosDiario(bodyRow, tipoMovto.getIdTipoMovto());

			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetIdToRow(bodyRow, ntLastRow, "T" + tipoMovto.getIdTipoMovto());
			subSetValToRow(bodyRow, ntLastRow, "TOTAL " + tipoMovto.getDescTipoMovto());
			resp = cashFlowDao.funSQLUpdateRowForTotalIngresoEgresoOriginal(NoUsuario, tipoMovto.getIdTipoMovto(),
					ntLastRow);
			addRows(bodyRow, 1);
		}

		subPrintSaldosOriginal(bodyRow);

	}

	private void subPrintTituloIngresoEgresoAjustado(List<HashMap> bodyRow) {

		List<TipoMovto> tiposMovto = cashFlowDao.FunSQLGetIngresoEgreso();
		int ntLastRow;
		int resp = 0;

		if (tiposMovto.size() == 0)
			return;

		Iterator it = tiposMovto.listIterator();

		while (it.hasNext()) {

			TipoMovto tipoMovto = (TipoMovto) it.next();

			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetValToRow(bodyRow, ntLastRow, tipoMovto.getDescTipoMovto());

			subPrintTiposConceptosAjustado(bodyRow, tipoMovto.getIdTipoMovto());

			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetIdToRow(bodyRow, ntLastRow, "T" + tipoMovto.getIdTipoMovto());
			subSetValToRow(bodyRow, ntLastRow, "TOTAL " + tipoMovto.getDescTipoMovto());
			resp = cashFlowDao.funSQLUpdateRowForTotalIngresoEgresoAjustado(NoUsuario, tipoMovto.getIdTipoMovto(),
					ntLastRow);
			addRows(bodyRow, 1);
		}

		subPrintSaldosAjustado(bodyRow);

	}

	// *****************************************************************************END
	/* inicio HECTOR */
	private void subPrintTituloIngresoEgresoMensual(List<HashMap> bodyRow) {

		List<TipoMovto> tiposMovto = cashFlowDao.FunSQLGetIngresoEgreso();
		int ntLastRow;
		int resp = 0;

		if (tiposMovto.size() == 0)
			return;

		Iterator it = tiposMovto.listIterator();

		while (it.hasNext()) {

			TipoMovto tipoMovto = (TipoMovto) it.next();

			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetValToRow(bodyRow, ntLastRow, tipoMovto.getDescTipoMovto());

			subPrintTiposConceptosMensual(bodyRow, tipoMovto.getIdTipoMovto());/* hector */

			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetIdToRow(bodyRow, ntLastRow, "T" + tipoMovto.getIdTipoMovto());
			subSetValToRow(bodyRow, ntLastRow, "TOTAL " + tipoMovto.getDescTipoMovto());
			resp = cashFlowDao.funSQLUpdateRowForTotalIngresoEgresoMensual(NoUsuario, tipoMovto.getIdTipoMovto(),
					ntLastRow);
			addRows(bodyRow, 1);
		}

		subPrintSaldosMensual(bodyRow);

	}

	/* FIN HECTOR */
	private void subPrintTituloIngresoEgresoComparativoMensualAjustadoReal(List<HashMap> bodyRow) {

		List<TipoMovto> tiposMovto = cashFlowDao.FunSQLGetIngresoEgreso();
		int ntLastRow;
		int resp = 0;

		if (tiposMovto.size() == 0)
			return;

		Iterator it = tiposMovto.listIterator();

		while (it.hasNext()) {

			TipoMovto tipoMovto = (TipoMovto) it.next();

			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetValToRow(bodyRow, ntLastRow, tipoMovto.getDescTipoMovto());

			subPrintTiposConceptoscomparativoRealajustado(bodyRow, tipoMovto.getIdTipoMovto());

			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetIdToRow(bodyRow, ntLastRow, "T" + tipoMovto.getIdTipoMovto());
			subSetValToRow(bodyRow, ntLastRow, "TOTAL" + tipoMovto.getDescTipoMovto());
			resp = cashFlowDao.funSQLUpdateRowForTotalIngresoEgresoComparativorealajustado(NoUsuario,
					tipoMovto.getIdTipoMovto(), ntLastRow);
			addRows(bodyRow, 1);
		}

		subPrintSaldosRealAjustado(bodyRow);

	}

	/*
	 * 'AUTOR : JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subPrintSaldos 'PARAMS : NONE 'RETURN : NONE 'RESPONSABILITY : PINTA LAS
	 * FILAS QUE CORRESPONDERAN A LOS SALDOS '*------->R-UTIL-BODY
	 */
	private void subPrintSaldos(List<HashMap> bodyRow) {

		int ntLastRow;
		int resp;

		addRows(bodyRow, 1);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "SOA");
		subSetValToRow(bodyRow, ntLastRow, "SALDO OPERATIVO ACUMULADO");
		resp = cashFlowDao.funSQLUpdateRowSOA(NoUsuario, ntLastRow);

		addRows(bodyRow, 2);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "SD");
		subSetValToRow(bodyRow, ntLastRow, "SALDO DISPONIBLE");
		resp = cashFlowDao.funSQLUpdateRowSD(NoUsuario, ntLastRow);

	}
	
	private void subPrintSaldosOAR(List<HashMap> bodyRow) {

		int ntLastRow;
		int resp;

		addRows(bodyRow, 1);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "SOA");
		subSetValToRow(bodyRow, ntLastRow, "SALDO OPERATIVO ACUMULADO");
		resp = cashFlowDao.funSQLUpdateRowSOAOAR(NoUsuario, ntLastRow);

		addRows(bodyRow, 2);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "SD");
		subSetValToRow(bodyRow, ntLastRow, "SALDO DISPONIBLE");
		resp = cashFlowDao.funSQLUpdateRowSDOAR(NoUsuario, ntLastRow);

	}

	private void subPrintSaldosRealAjustado(List<HashMap> bodyRow) {

		int ntLastRow;
		int resp;

		addRows(bodyRow, 1);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "SOA");
		subSetValToRow(bodyRow, ntLastRow, "SALDO OPERATIVO ACUMULADO");
		resp = cashFlowDao.funSQLUpdateRowSOARealAjustado(NoUsuario, ntLastRow);

		addRows(bodyRow, 2);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "SD");
		subSetValToRow(bodyRow, ntLastRow, "SALDO DISPONIBLE");
		resp = cashFlowDao.funSQLUpdateRowSDRealAjustado(NoUsuario, ntLastRow);

	}

	private void subPrintSaldosOriginalAjustado(List<HashMap> bodyRow) {

		int ntLastRow;
		int resp;

		addRows(bodyRow, 1);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "SOA");
		subSetValToRow(bodyRow, ntLastRow, "SALDO OPERATIVO ACUMULADO");
		resp = cashFlowDao.funSQLUpdateRowSOAOriginalAjustado(NoUsuario, ntLastRow);

		addRows(bodyRow, 2);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "SD");
		subSetValToRow(bodyRow, ntLastRow, "SALDO DISPONIBLE");
		resp = cashFlowDao.funSQLUpdateRowSDOriginalAjustado(NoUsuario, ntLastRow);

	}

	private void subPrintSaldosOriginal(List<HashMap> bodyRow) {

		int ntLastRow;
		int resp;

		addRows(bodyRow, 1);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "SOA");
		subSetValToRow(bodyRow, ntLastRow, "SALDO OPERATIVO ACUMULADO");
		resp = cashFlowDao.funSQLUpdateRowSOAOriginal(NoUsuario, ntLastRow);

		addRows(bodyRow, 2);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "SD");
		subSetValToRow(bodyRow, ntLastRow, "SALDO DISPONIBLE");
		resp = cashFlowDao.funSQLUpdateRowSDOriginal(NoUsuario, ntLastRow);

	}

	private void subPrintSaldosAjustado(List<HashMap> bodyRow) {

		int ntLastRow;
		int resp;

		addRows(bodyRow, 1);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "SOA");
		subSetValToRow(bodyRow, ntLastRow, "SALDO OPERATIVO ACUMULADO");
		resp = cashFlowDao.funSQLUpdateRowSOAAjustado(NoUsuario, ntLastRow);

		addRows(bodyRow, 2);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "SD");
		subSetValToRow(bodyRow, ntLastRow, "SALDO DISPONIBLE");
		resp = cashFlowDao.funSQLUpdateRowSDAjustado(NoUsuario, ntLastRow);

	}

	private void subPrintSaldosMensual(List<HashMap> bodyRow) {

		int ntLastRow;
		int resp;

		addRows(bodyRow, 1);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "SOA");
		subSetValToRow(bodyRow, ntLastRow, "SALDO OPERATIVO ACUMULADO");
		resp = cashFlowDao.funSQLUpdateRowSOAMENSUAL(NoUsuario, ntLastRow);

		addRows(bodyRow, 2);
		ntLastRow = funGetLastRowOfList(bodyRow) - 1;
		subSetIdToRow(bodyRow, ntLastRow, "SD");
		subSetValToRow(bodyRow, ntLastRow, "SALDO DISPONIBLE");
		resp = cashFlowDao.funSQLUpdateRowSDMENSUAL(NoUsuario, ntLastRow);

	}

	// *****************************************************************************END

	/*
	 * 'AUTOR : JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subPrintTiposConceptos 'PARAMS : paramIngresoEgreso, parametro de que
	 * naturaleza seran los tipos de conceptos 'RETURN : NONE 'RESPONSABILITY :
	 * PINTA LOS TIPOS DE CONCEPTOS '*------->R-UTIL-BODY
	 */
	private void subPrintTiposConceptos(List<HashMap> bodyRow, String idTipoMovto) {

		List<TipoConcepto> tiposConcepto = cashFlowDao.FunSQLGetTiposConceptos(NoUsuario, idTipoMovto);
		int ntLastRow;
		int resp;

		if (tiposConcepto.size() == 0)
			return;

		Iterator it = tiposConcepto.listIterator();

		while (it.hasNext()) {

			TipoConcepto tipoConcepto = (TipoConcepto) it.next();

			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetIdToRow(bodyRow, ntLastRow, "TC_" + tipoConcepto.getIdTipoConcepto());
			subSetValToRow(bodyRow, ntLastRow, tipoConcepto.getDescripcion());

			subPrintConceptos(bodyRow, tipoConcepto.getIdTipoConcepto(), IdReporte, NoEmpresa, IdGrupo, FechaInicial,
					FechaFinal, FechaHoy, idTipoMovto, IdDivisa);

			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetIdToRow(bodyRow, ntLastRow, "SUB" + tipoConcepto.getIdTipoConcepto());
			subSetValToRow(bodyRow, ntLastRow, "SUBTOTAL " + tipoConcepto.getDescripcion());
			resp = cashFlowDao.funSQLUpdateRowForTipoConcepto(NoUsuario, tipoConcepto.getIdTipoConcepto(), ntLastRow);

			addRows(bodyRow, 1);

		}

	}

	private void subPrintTiposConceptosOAR(List<HashMap> bodyRow, String idTipoMovto) {

		List<TipoConcepto> tiposConcepto = cashFlowDao.FunSQLGetTiposConceptos(NoUsuario, idTipoMovto);
		int ntLastRow;
		int resp;

		if (tiposConcepto.size() == 0)
			return;

		Iterator it = tiposConcepto.listIterator();

		while (it.hasNext()) {

			TipoConcepto tipoConcepto = (TipoConcepto) it.next();

			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetIdToRow(bodyRow, ntLastRow, "TC_" + tipoConcepto.getIdTipoConcepto());
			subSetValToRow(bodyRow, ntLastRow, tipoConcepto.getDescripcion());

			subPrintConceptosOAR(bodyRow, tipoConcepto.getIdTipoConcepto(), IdReporte, NoEmpresa, IdGrupo, FechaInicial,
					FechaFinal, FechaHoy, idTipoMovto, IdDivisa);

			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetIdToRow(bodyRow, ntLastRow, "SUB" + tipoConcepto.getIdTipoConcepto());
			subSetValToRow(bodyRow, ntLastRow, "SUBTOTAL " + tipoConcepto.getDescripcion());
			resp = cashFlowDao.funSQLUpdateRowForTipoConceptoOAR(NoUsuario, tipoConcepto.getIdTipoConcepto(), ntLastRow);

			addRows(bodyRow, 1);

		}

	}
	private void subPrintTiposConceptosOriginalAjustado(List<HashMap> bodyRow, String idTipoMovto) {

		List<TipoConcepto> tiposConcepto = cashFlowDao.FunSQLGetTiposConceptos(NoUsuario, idTipoMovto);
		int ntLastRow;
		int resp;

		if (tiposConcepto.size() == 0)
			return;

		Iterator it = tiposConcepto.listIterator();

		while (it.hasNext()) {

			TipoConcepto tipoConcepto = (TipoConcepto) it.next();

			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetIdToRow(bodyRow, ntLastRow, "TC_" + tipoConcepto.getIdTipoConcepto());
			subSetValToRow(bodyRow, ntLastRow, tipoConcepto.getDescripcion());

			subPrintConceptosOriginalAjustado(bodyRow, tipoConcepto.getIdTipoConcepto(), IdReporte, NoEmpresa, IdGrupo,
					FechaInicial, FechaFinal, FechaHoy, idTipoMovto, IdDivisa);

			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetIdToRow(bodyRow, ntLastRow, "SUB" + tipoConcepto.getIdTipoConcepto());
			subSetValToRow(bodyRow, ntLastRow, "SUBTOTAL " + tipoConcepto.getDescripcion());
			resp = cashFlowDao.funSQLUpdateRowForTipoConceptoOriginalAjustado(NoUsuario,
					tipoConcepto.getIdTipoConcepto(), ntLastRow);

			addRows(bodyRow, 1);

		}

	}

	private void subPrintTiposConceptosDiario(List<HashMap> bodyRow, String idTipoMovto) {

		List<TipoConcepto> tiposConcepto = cashFlowDao.FunSQLGetTiposConceptos(NoUsuario, idTipoMovto);
		int ntLastRow;
		int resp;

		if (tiposConcepto.size() == 0)
			return;

		Iterator it = tiposConcepto.listIterator();

		while (it.hasNext()) {

			TipoConcepto tipoConcepto = (TipoConcepto) it.next();

			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetIdToRow(bodyRow, ntLastRow, "TC_" + tipoConcepto.getIdTipoConcepto());
			subSetValToRow(bodyRow, ntLastRow, tipoConcepto.getDescripcion());

			subPrintConceptosDiario(bodyRow, tipoConcepto.getIdTipoConcepto(), IdReporte, NoEmpresa, IdGrupo,
					FechaInicial, FechaFinal, FechaHoy, idTipoMovto, IdDivisa);

			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetIdToRow(bodyRow, ntLastRow, "SUB" + tipoConcepto.getIdTipoConcepto());
			subSetValToRow(bodyRow, ntLastRow, "SUBTOTAL " + tipoConcepto.getDescripcion());
			resp = cashFlowDao.funSQLUpdateRowForTipoConceptoOriginal(NoUsuario, tipoConcepto.getIdTipoConcepto(),
					ntLastRow);

			addRows(bodyRow, 1);

		}

	}

	// *****************************************************************************END
	private void subPrintTiposConceptosAjustado(List<HashMap> bodyRow, String idTipoMovto) {

		List<TipoConcepto> tiposConcepto = cashFlowDao.FunSQLGetTiposConceptos(NoUsuario, idTipoMovto);
		int ntLastRow;
		int resp;

		if (tiposConcepto.size() == 0)
			return;

		Iterator it = tiposConcepto.listIterator();

		while (it.hasNext()) {

			TipoConcepto tipoConcepto = (TipoConcepto) it.next();

			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetIdToRow(bodyRow, ntLastRow, "TC_" + tipoConcepto.getIdTipoConcepto());
			subSetValToRow(bodyRow, ntLastRow, tipoConcepto.getDescripcion());

			subPrintConceptosAjustado(bodyRow, tipoConcepto.getIdTipoConcepto(), IdReporte, NoEmpresa, IdGrupo,
					FechaInicial, FechaFinal, FechaHoy, idTipoMovto, IdDivisa);

			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetIdToRow(bodyRow, ntLastRow, "SUB" + tipoConcepto.getIdTipoConcepto());
			subSetValToRow(bodyRow, ntLastRow, "SUBTOTAL " + tipoConcepto.getDescripcion());
			resp = cashFlowDao.funSQLUpdateRowForTipoConceptoAjustado(NoUsuario, tipoConcepto.getIdTipoConcepto(),
					ntLastRow);

			addRows(bodyRow, 1);

		}

	}
	/* inicio hector */

	private void subPrintTiposConceptosMensual(List<HashMap> bodyRow, String idTipoMovto) {

		List<TipoConcepto> tiposConcepto = cashFlowDao.FunSQLGetTiposConceptos(NoUsuario, idTipoMovto);
		int ntLastRow;
		int resp;

		if (tiposConcepto.size() == 0)
			return;

		Iterator it = tiposConcepto.listIterator();

		while (it.hasNext()) {

			TipoConcepto tipoConcepto = (TipoConcepto) it.next();

			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetIdToRow(bodyRow, ntLastRow, "TC_" + tipoConcepto.getIdTipoConcepto());
			subSetValToRow(bodyRow, ntLastRow, tipoConcepto.getDescripcion());

			subPrintConceptosMensual(bodyRow, tipoConcepto.getIdTipoConcepto(), IdReporte, NoEmpresa, IdGrupo,
					FechaInicial, FechaFinal, FechaHoy, idTipoMovto, IdDivisa);

			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetIdToRow(bodyRow, ntLastRow, "SUB" + tipoConcepto.getIdTipoConcepto());
			subSetValToRow(bodyRow, ntLastRow, "SUBTOTAL " + tipoConcepto.getDescripcion());
			resp = cashFlowDao.funSQLUpdateRowForTipoConceptoMesual(NoUsuario, tipoConcepto.getIdTipoConcepto(),
					ntLastRow);

			addRows(bodyRow, 1);

		}

	}

	/* Fin hector */
	private void subPrintTiposConceptoscomparativoRealajustado(List<HashMap> bodyRow, String idTipoMovto) {

		List<TipoConcepto> tiposConcepto = cashFlowDao.FunSQLGetTiposConceptos(NoUsuario, idTipoMovto);
		int ntLastRow;
		int resp;

		if (tiposConcepto.size() == 0)
			return;

		Iterator it = tiposConcepto.listIterator();

		while (it.hasNext()) {

			TipoConcepto tipoConcepto = (TipoConcepto) it.next();

			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetIdToRow(bodyRow, ntLastRow, "TC_" + tipoConcepto.getIdTipoConcepto());
			subSetValToRow(bodyRow, ntLastRow, tipoConcepto.getDescripcion());

			subPrintConceptoscomparativoRealajustado(bodyRow, tipoConcepto.getIdTipoConcepto(), IdReporte, NoEmpresa,
					IdGrupo, FechaInicial, FechaFinal, FechaHoy, idTipoMovto, IdDivisa);

			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetIdToRow(bodyRow, ntLastRow, "SUB" + tipoConcepto.getIdTipoConcepto());
			subSetValToRow(bodyRow, ntLastRow, "SUBTOTAL " + tipoConcepto.getDescripcion());
			resp = cashFlowDao.funSQLUpdateRowForTipoConceptocomparativoRealajustado(NoUsuario,
					tipoConcepto.getIdTipoConcepto(), ntLastRow);

			addRows(bodyRow, 1);

		}

	}

	/*
	 * 'AUTOR : JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subPrintConceptos 'PARAMS : 'RETURN : NONE 'RESPONSABILITY : PINTA LOS
	 * CONCEPTOS '*------->R-UTIL-BODY
	 */
	private void subPrintConceptos(List<HashMap> bodyRow, int idTipoConcepto, int reporte, int empresa, int grupo,
			String fechaini, String fechfin, String fechahoy, String idTipoMovto, String divisa) {
 
		List<ConceptoFlujo> conceptos = cashFlowDao.FunSQLGetConceptosFlujo(NoUsuario, idTipoConcepto, reporte, empresa,
				grupo, fechaini, fechfin, fechahoy, idTipoMovto, IdDivisa);
		int ntLastRow;
		int resp = 0;
		if (conceptos.size() == 0)
			return;
		Iterator it = conceptos.listIterator();

		while (it.hasNext()) {

			ConceptoFlujo concepto = (ConceptoFlujo) it.next();
			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetIdToRow(bodyRow, ntLastRow, concepto.getCveConcepto().toString());
			subSetValToRow(bodyRow, ntLastRow, concepto.getDescLarga());
			resp = cashFlowDao.funSQLUpdateRowForConcepto(NoUsuario, concepto.getCveConcepto(), ntLastRow);
			System.out.println("conceptos " + concepto.getDescLarga());
		}

	}
	
	private void subPrintConceptosOAR(List<HashMap> bodyRow, int idTipoConcepto, int reporte, int empresa, int grupo,
			String fechaini, String fechfin, String fechahoy, String idTipoMovto, String divisa) {
 
		List<ConceptoFlujo> conceptos = cashFlowDao.FunSQLGetConceptosFlujo(NoUsuario, idTipoConcepto, reporte, empresa,
				grupo, fechaini, fechfin, fechahoy, idTipoMovto, IdDivisa);
		int ntLastRow;
		int resp = 0;
		if (conceptos.size() == 0)
			return;
		Iterator it = conceptos.listIterator();

		while (it.hasNext()) {

			ConceptoFlujo concepto = (ConceptoFlujo) it.next();
			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetIdToRow(bodyRow, ntLastRow, concepto.getCveConcepto().toString());
			subSetValToRow(bodyRow, ntLastRow, concepto.getDescLarga());
			resp = cashFlowDao.funSQLUpdateRowForConceptoOAR(NoUsuario, concepto.getCveConcepto(), ntLastRow);
			System.out.println("conceptos " + concepto.getDescLarga());
		}

	}


	private void subPrintConceptosOriginalAjustado(List<HashMap> bodyRow, int idTipoConcepto, int reporte, int empresa,
			int grupo, String fechaini, String fechfin, String fechahoy, String idTipoMovto, String divisa) {
 
		List<ConceptoFlujo> conceptos = cashFlowDao.FunSQLGetConceptosFlujo(NoUsuario, idTipoConcepto, reporte, empresa,
				grupo, fechaini, fechfin, fechahoy, idTipoMovto, IdDivisa);
		int ntLastRow;
		int resp = 0;
		if (conceptos.size() == 0)
			return;
		Iterator it = conceptos.listIterator();

		while (it.hasNext()) {

			ConceptoFlujo concepto = (ConceptoFlujo) it.next();
			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetIdToRow(bodyRow, ntLastRow, concepto.getCveConcepto().toString());
			subSetValToRow(bodyRow, ntLastRow, concepto.getDescLarga());
			resp = cashFlowDao.funSQLUpdateRowForConceptoOriginalAjustado(NoUsuario, concepto.getCveConcepto(),
					ntLastRow);
			System.out.println("conceptos " + concepto.getDescLarga());
		}

	}

	private void subPrintConceptosDiario(List<HashMap> bodyRow, int idTipoConcepto, int reporte, int empresa, int grupo,
			String fechaini, String fechfin, String fechahoy, String idTipoMovto, String divisa) {

		/*
		 * System.out.println("reporte "+reporte); System.out.println("empresa "
		 * +empresa); System.out.println("grupo "+grupo); System.out.println(
		 * "fecha ini "+fechaini); System.out.println("fecha fin "+fechfin);
		 * System.out.println("fecha hoy "+fechahoy); System.out.println(
		 * "movimiento "+idTipoMovto); System.out.println("divisa"+ divisa);
		 */
		List<ConceptoFlujo> conceptos = cashFlowDao.FunSQLGetConceptosFlujo(NoUsuario, idTipoConcepto, reporte, empresa,
				grupo, fechaini, fechfin, fechahoy, idTipoMovto, IdDivisa);
		int ntLastRow;
		int resp = 0;
		if (conceptos.size() == 0)
			return;
		Iterator it = conceptos.listIterator();

		while (it.hasNext()) {

			ConceptoFlujo concepto = (ConceptoFlujo) it.next();
			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetIdToRow(bodyRow, ntLastRow, concepto.getCveConcepto().toString());
			subSetValToRow(bodyRow, ntLastRow, concepto.getDescLarga());
			resp = cashFlowDao.funSQLUpdateRowForConceptoOriginal(NoUsuario, concepto.getCveConcepto(), ntLastRow);
			System.out.println("conceptos " + concepto.getDescLarga());
		}

	}

	// *****************************************************************************END
	/*
	 * 'AUTOR : JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subPrintConceptos 'PARAMS : 'RETURN : NONE 'RESPONSABILITY : PINTA LOS
	 * CONCEPTOS '*------->R-UTIL-BODY
	 */
	private void subPrintConceptosAjustado(List<HashMap> bodyRow, int idTipoConcepto, int reporte, int empresa,
			int grupo, String fechaini, String fechfin, String fechahoy, String idTipoMovto, String divisa) {

		/*
		 * System.out.println("reporte "+reporte); System.out.println("empresa "
		 * +empresa); System.out.println("grupo "+grupo); System.out.println(
		 * "fecha ini "+fechaini); System.out.println("fecha fin "+fechfin);
		 * System.out.println("fecha hoy "+fechahoy); System.out.println(
		 * "movimiento "+idTipoMovto); System.out.println("divisa"+ divisa);
		 */
		List<ConceptoFlujo> conceptos = cashFlowDao.FunSQLGetConceptosFlujo(NoUsuario, idTipoConcepto, reporte, empresa,
				grupo, fechaini, fechfin, fechahoy, idTipoMovto, IdDivisa);
		int ntLastRow;
		int resp = 0;
		if (conceptos.size() == 0)
			return;
		Iterator it = conceptos.listIterator();

		while (it.hasNext()) {

			ConceptoFlujo concepto = (ConceptoFlujo) it.next();
			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetIdToRow(bodyRow, ntLastRow, concepto.getCveConcepto().toString());
			subSetValToRow(bodyRow, ntLastRow, concepto.getDescLarga());
			resp = cashFlowDao.funSQLUpdateRowForConceptoAjustado(NoUsuario, concepto.getCveConcepto(), ntLastRow);
			System.out.println("conceptos " + concepto.getDescLarga());
		}

	}

	/*
	 * Hector 'AUTOR : JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA
	 * 'NAME : subPrintConceptos 'PARAMS : 'RETURN : NONE 'RESPONSABILITY :
	 * PINTA LOS CONCEPTOS '*------->R-UTIL-BODY
	 */
	private void subPrintConceptosMensual(List<HashMap> bodyRow, int idTipoConcepto, int reporte, int empresa,
			int grupo, String fechaini, String fechfin, String fechahoy, String idTipoMovto, String divisa) {

		/*
		 * System.out.println("reporte "+reporte); System.out.println("empresa "
		 * +empresa); System.out.println("grupo "+grupo); System.out.println(
		 * "fecha ini "+fechaini); System.out.println("fecha fin "+fechfin);
		 * System.out.println("fecha hoy "+fechahoy); System.out.println(
		 * "movimiento "+idTipoMovto); System.out.println("divisa"+ divisa);
		 */
		List<ConceptoFlujo> conceptos = cashFlowDao.FunSQLGetConceptosFlujo(NoUsuario, idTipoConcepto, reporte, empresa,
				grupo, fechaini, fechfin, fechahoy, idTipoMovto, IdDivisa);
		int ntLastRow;
		int resp = 0;
		if (conceptos.size() == 0)
			return;
		Iterator it = conceptos.listIterator();

		while (it.hasNext()) {

			ConceptoFlujo concepto = (ConceptoFlujo) it.next();
			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetIdToRow(bodyRow, ntLastRow, concepto.getCveConcepto().toString());
			subSetValToRow(bodyRow, ntLastRow, concepto.getDescLarga());
			resp = cashFlowDao.funSQLUpdateRowForConceptoMensual(NoUsuario, concepto.getCveConcepto(), ntLastRow);
			System.out.println("conceptos " + concepto.getDescLarga());
		}

	}
	// *hector****************************************************************************END

	private void subPrintConceptoscomparativoRealajustado(List<HashMap> bodyRow, int idTipoConcepto, int reporte,
			int empresa, int grupo, String fechaini, String fechfin, String fechahoy, String idTipoMovto,
			String divisa) {

		/*
		 * System.out.println("reporte "+reporte); System.out.println("empresa "
		 * +empresa); System.out.println("grupo "+grupo); System.out.println(
		 * "fecha ini "+fechaini); System.out.println("fecha fin "+fechfin);
		 * System.out.println("fecha hoy "+fechahoy); System.out.println(
		 * "movimiento "+idTipoMovto); System.out.println("divisa"+ divisa);
		 */
		List<ConceptoFlujo> conceptos = cashFlowDao.FunSQLGetConceptosFlujo(NoUsuario, idTipoConcepto, reporte, empresa,
				grupo, fechaini, fechfin, fechahoy, idTipoMovto, IdDivisa);
		int ntLastRow;
		int resp = 0;
		if (conceptos.size() == 0)
			return;
		Iterator it = conceptos.listIterator();

		while (it.hasNext()) {

			ConceptoFlujo concepto = (ConceptoFlujo) it.next();
			addRows(bodyRow, 1);
			ntLastRow = funGetLastRowOfList(bodyRow) - 1;
			subSetIdToRow(bodyRow, ntLastRow, concepto.getCveConcepto().toString());
			subSetValToRow(bodyRow, ntLastRow, concepto.getDescLarga());
			resp = cashFlowDao.funSQLUpdateRowForConceptoMensualcomparativorealajustado(NoUsuario,
					concepto.getCveConcepto(), ntLastRow);
			System.out.println("conceptos " + concepto.getDescLarga());
		}

	}
	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 7 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subRutina 'NAME :
	 * subPrintCerosinGrid 'PARAMS : NONE 'RETURN : NONE 'RESPONSABILITY :
	 * INICIALIZAR O IMPRIMIR LAS CELDAS DEL GRID VAYAN A CONTENER INFORMACION
	 * DE IMPORTES '*------->R-UTIL-BODY
	 * '************************************************************************
	 * *******************************************
	 */

	private void subPrintCerosinGrid(List<HashMap> bodyRow) {

		int iRowIni;
		int iRowFin;

		int iColIni;
		int iColFin;

		String strValColCero;

		HashMap hsmpTemp = new HashMap<String, String>();

		iRowIni = getRowOfIdHeaderDates(bodyRow, "R-S");
		iRowFin = funGetLastRowOfList(bodyRow);

		iColIni = 3;
		iColFin = lastCol;

		Gson g = new Gson();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		for (int r = iRowIni; r < iRowFin; r++) {

			hsmpTemp = bodyRow.get(r);

			for (int c = iColIni; c < iColFin; c++) {

				if (hsmpTemp.get("col" + c) == null)
					hsmpTemp.put("col" + c, "");

				if (hsmpTemp.get("col1") == null)
					hsmpTemp.put("col1", "");

				strValColCero = hsmpTemp.get("col1").toString();

				if (!strValColCero.equals("") && !strValColCero.contains("TC_")) {
					hsmpTemp.put("col" + c, formateador.format(0));

				}
			} // for

		} // for r

	}

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 8 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subSetSaldoInicialDiarioSemanalMensualPeriodo 'PARAMS : NONE 'RETURN :
	 * NONE 'RESPONSABILITY : CONTROLA LA OBTENCION Y LA IMPRESION DEL SALDO
	 * INICIAL PARA DIA SEMANA MES PERIODO ' NOTA: SE OBTIENE Y SE IMPRIME
	 * INDEPENDIENTEMENTE
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subSetSaldoInicialDiarioSemanalMensualPeriodo(List<HashMap> bodyRow) {

		// 'PARA ENCONTRAR I, EL INICIO HABIL DEL PERIODO
		int iRowOfIniDia;
		int iColOfIniDia;
		int iColOfIniDiaTmp;

		int iRowOfIniSemana;
		int iColOfIniSemana;

		int iRowOfIniMes;
		int iColOfIniMes;

		int iRowOfIniPeriodo;
		int iColOfIniPeriodo;

		int iRowSaldoDayWeekMonthPeriodic;

		double dblSaldoInicial;

		HashMap hsmpTemp = new HashMap<String, String>();

		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		HashMap hsmpSaldos = new HashMap<String, String>();

		dblSaldoInicial = funGetSaldoInicial();

		hsmpSaldos = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "R-S"));

		iRowSaldoDayWeekMonthPeriodic = getRowOfIdHeaderDates(bodyRow, "R-S");
		iRowOfIniDia = getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN");

		iColOfIniDiaTmp = funGetColOfFec(bodyRow, iRowOfIniDia, "I");
		iColOfIniDia = (iColOfIniDiaTmp == 0) ? funGetColOfFec(bodyRow, iRowOfIniDia, "I_H") : iColOfIniDiaTmp;

		iRowOfIniSemana = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
		iColOfIniSemana = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--1-M--1");

		hsmpTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_HABIL"));
		// logger.info("error hsmpTemp"+hsmpTemp);
		if (hsmpTemp.get("col" + iColOfIniSemana).equals("O"))
			iColOfIniSemana = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--2-M--1");

		if (hsmpTemp.get("col" + iColOfIniSemana).equals("O"))
			iColOfIniSemana = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--3-M--1");

		if (hsmpTemp.get("col" + iColOfIniSemana).equals("O"))
			iColOfIniSemana = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--4-M--1");

		if (hsmpTemp.get("col" + iColOfIniSemana).equals("O"))
			iColOfIniSemana = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--5-M--1");

		if (hsmpTemp.get("col" + iColOfIniSemana).equals("O"))
			iColOfIniSemana = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--6-M--1");

		iRowOfIniMes = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
		iColOfIniMes = funGetColOfFec(bodyRow, iRowOfIniMes, "M--1");

		iRowOfIniPeriodo = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
		iColOfIniPeriodo = funGetColOfFec(bodyRow, iRowOfIniPeriodo, "10000");

		hsmpSaldos.put("col" + iColOfIniDia, formateador.format(dblSaldoInicial));
		hsmpSaldos.put("col" + iColOfIniSemana, formateador.format(dblSaldoInicial));
		hsmpSaldos.put("col" + iColOfIniMes, formateador.format(dblSaldoInicial));
		hsmpSaldos.put("col" + iColOfIniPeriodo, formateador.format(dblSaldoInicial));

	}
	
	
	private void subSetSaldoInicialDiarioSemanalMensualPeriodoPrueba(List<HashMap> bodyRow,long prueba) {

		// 'PARA ENCONTRAR I, EL INICIO HABIL DEL PERIODO
		int iRowOfIniDia;
		int iColOfIniDia;
		int iColOfIniDiaTmp;

		int iRowOfIniSemana;
		int iColOfIniSemana;

		int iRowOfIniMes;
		int iColOfIniMes;

		int iRowOfIniPeriodo;
		int iColOfIniPeriodo;

		int iRowSaldoDayWeekMonthPeriodic;

		double dblSaldoInicial;

		HashMap hsmpTemp = new HashMap<String, String>();

		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		HashMap hsmpSaldos = new HashMap<String, String>();

		dblSaldoInicial = funGetSaldoInicial();

		hsmpSaldos = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "R-S"));

		iRowSaldoDayWeekMonthPeriodic = getRowOfIdHeaderDates(bodyRow, "R-S");
		iRowOfIniDia = getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN");

		iColOfIniDiaTmp = funGetColOfFec(bodyRow, iRowOfIniDia, "I");
		iColOfIniDia = (iColOfIniDiaTmp == 0) ? funGetColOfFec(bodyRow, iRowOfIniDia, "I_H") : iColOfIniDiaTmp;
		
		for(int i = 1; i <= prueba; i++){
		
		iRowOfIniSemana = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
		iColOfIniSemana = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--1-M--"+i+"");
 

		iRowOfIniMes = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
		iColOfIniMes = funGetColOfFec(bodyRow, iRowOfIniMes, "M--"+i+"");

		iRowOfIniPeriodo = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
		iColOfIniPeriodo = funGetColOfFec(bodyRow, iRowOfIniPeriodo, "10000");

		hsmpSaldos.put("col" + iColOfIniDia, formateador.format(dblSaldoInicial));
		hsmpSaldos.put("col" + iColOfIniSemana, formateador.format(dblSaldoInicial));
		hsmpSaldos.put("col" + iColOfIniMes, formateador.format(dblSaldoInicial));
		hsmpSaldos.put("col" + iColOfIniPeriodo, formateador.format(dblSaldoInicial));
		 
		}
		
		
	}
	
	
	// *****************************************************************************END

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 7 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subRutina 'NAME :
	 * funGetSaldoInicial 'PARAMS : NONE 'RETURN : double 'RESPONSABILITY :
	 * Obtiene el saldo inicial '*------->R-UTIL-BODY
	 * '************************************************************************
	 * *******************************************
	 */
	private double funGetSaldoInicial() {

		Calendar calFecIni = Calendar.getInstance();
		Calendar calFecHoy = Calendar.getInstance();
		Calendar calFecManana = Calendar.getInstance();
		Calendar calFecFin = Calendar.getInstance();

		calFecIni.setTime(getTypeDateFromStringFec(FechaInicial));
		calFecHoy.setTime(getTypeDateFromStringFec(FechaHoy));
		calFecManana.setTime(getTypeDateFromStringFec(FechaManana));
		calFecFin.setTime(getTypeDateFromStringFec(FechaFinal));

		List<Saldo> saldos = null;
		Saldo saldo = new Saldo();
		;

		/*
		 * 'I-H-F: 'I<H<F: 'SI LA FECHA HOY ES MAYOR A FECHA INICIAL Y MENOR A
		 * FECHA FINAL
		 */
		if (calFecHoy.compareTo(calFecIni) > 0 && calFecHoy.compareTo(calFecFin) < 0) {
			saldos = cashFlowDao.funSqlGetSaldosForFlujo(NoUsuario, FechaInicial, "I-H", IdDivisa, IdGrupo, NoEmpresa);
		}

		/*
		 * 'H-I-F: 'H<I<F: 'SI LA FECHA HOY ES MENOR A FECHA INICIAL Y FECHA
		 * FINAL
		 */
		if (calFecHoy.compareTo(calFecIni) < 0 && calFecHoy.compareTo(calFecFin) < 0) {
			// saldos = cashFlowDao.funSqlGetSaldosForFlujo( NoUsuario,
			// FechaHoy,"F-C", IdDivisa, IdGrupo,NoEmpresa);
			saldos = cashFlowDao.funSqlGetSaldosForFlujo(NoUsuario, FechaInicial, "I-H", IdDivisa, IdGrupo, NoEmpresa);
		}

		/*
		 * 'I-F-H 'SI LA FECHA HOY ES MAYOR A FECHA INICIAL Y MAYOR A FECHA
		 * FINAL I<F<H
		 */

		if (calFecHoy.compareTo(calFecIni) > 0 && calFecHoy.compareTo(calFecFin) > 0) {
			saldos = cashFlowDao.funSqlGetSaldosForFlujo(NoUsuario, FechaInicial, "I-H", IdDivisa, IdGrupo, NoEmpresa);
		}

		/*
		 * 'I_H-F 'I=H<F 'SI LA FECHA HOY ES IGUAL FECHA INICIAL Y MENOR A FECHA
		 * FINAL
		 */

		if (calFecHoy.compareTo(calFecIni) == 0 && calFecHoy.compareTo(calFecFin) < 0) {
			saldos = cashFlowDao.funSqlGetSaldosForFlujo(NoUsuario, FechaInicial, "F-H", IdDivisa, IdGrupo, NoEmpresa);
		}

		/*
		 * 'I-H_F 'SI LA FECHA HOY ES MAYOR A FECHA INICIAL E IGUAL A FECHA
		 * FINAL
		 */
		if (calFecHoy.compareTo(calFecIni) > 0 && calFecHoy.compareTo(calFecFin) == 0) {
			saldos = cashFlowDao.funSqlGetSaldosForFlujo(NoUsuario, FechaInicial, "I-H", IdDivisa, IdGrupo, NoEmpresa);

		}

		saldo = saldos.get(0);
		// saldos= cashFlowDao.FunSQLSaldo_Inversion( NoUsuario, FechaInicial,
		// "I-H", IdDivisa,IdGrupo, NoEmpresa);
		/*
		 * int x=saldos.size(); int i=1; /*do{ saldos.get(i);
		 * 
		 * i++; }while(i == x);
		 */

		// System.out.println(saldo);

		return saldo.getSaldo();

	}

	// *****************************************************************************END

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subControlTotalesDiario 'PARAMS : none 'RETURN : none 'RESPONSABILITY :
	 * PUNTO DE CONTROL PARA LA IMPRESION DE TOTALES EN LAS COLUMNAS DE DIAS
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subControlTotalesDiario(List<HashMap> bodyRow) {

		subTotalDiario(bodyRow);
		subTotalDiarioForIngresoOrEgreso(bodyRow);
		subTotalDiarioForTypeConcepto(bodyRow);
		subTotalDiarioForConcepto(bodyRow);

	}
	// *****************************************************************************END

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subTotalDiario 'PARAMS : none 'RETURN : none 'RESPONSABILITY : FUNCION
	 * QUE CONTROLA LA EJECUCION DE LA IMPRESION DE DATOS DIARIO
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subTotalDiario(List<HashMap> bodyRow) {

		List<TotalDiario> totalesDiario = cashFlowDao.funSqlGetTotalesDiario(IdDivisa, NoUsuario);

		if (totalesDiario.size() == 0)
			return;

		Iterator it = totalesDiario.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		while (it.hasNext()) {

			TotalDiario totalDiario = (TotalDiario) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, totalDiario.getFec_valor());

			hsmpTemp = bodyRow.get(totalDiario.getRow_total_sd());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalDiario.getTotal()));
			hsmpTemp = bodyRow.get(totalDiario.getRow_total_soa());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalDiario.getTotal()));

		}

	}

	// *****************************************************************************END

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subTotalDiarioForIngresoOrEgreso 'PARAMS : none 'RETURN : none
	 * 'RESPONSABILITY : IMPRIME TOTALES RELACIONADAS CON INGRESOS Y EGRESOS
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */

	private void subTotalDiarioForIngresoOrEgreso(List<HashMap> bodyRow) {

		List<TotalIngresoEgreso> totalesIngresoEgreso = cashFlowDao.funSqlGetTotalesIngresoEgreso(IdDivisa, NoUsuario);

		if (totalesIngresoEgreso.size() == 0)
			return;

		Iterator it = totalesIngresoEgreso.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		while (it.hasNext()) {

			TotalIngresoEgreso totalIngresoEgreso = (TotalIngresoEgreso) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, totalIngresoEgreso.getFec_valor());

			hsmpTemp = bodyRow.get(totalIngresoEgreso.getRow_ingresoegreso());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalIngresoEgreso.getTotal()));

		}

	}
	// *****************************************************************************END

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subTotalDiarioForTypeConcepto 'PARAMS : none 'RETURN : none
	 * 'RESPONSABILITY : IMPRIME TOTALES RELACIONADAS CON LOS TIPOS DE CONCEPTOS
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subTotalDiarioForTypeConcepto(List<HashMap> bodyRow) {

		List<TotalTipoConcepto> totalesTipoConcepto = cashFlowDao.funSqlGetTotalesForTypeConcepto(IdDivisa, NoUsuario);

		if (totalesTipoConcepto.size() == 0)
			return;

		Iterator it = totalesTipoConcepto.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalTipoConcepto totalTipoConcepto = (TotalTipoConcepto) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, totalTipoConcepto.getFec_valor());

			hsmpTemp = bodyRow.get(totalTipoConcepto.getRow_tipo_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalTipoConcepto.getTotal()));

		}

	}
	// *****************************************************************************END

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subTotalDiarioForConcepto 'PARAMS : none 'RETURN : none 'RESPONSABILITY :
	 * IMPRIME TOTALES RELACIONADAS CON CONCEPTOS
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subTotalDiarioForConcepto(List<HashMap> bodyRow) {

		List<TotalConcepto> totalesConcepto = cashFlowDao.funSqlGetTotalesForConcepto(IdDivisa, NoUsuario);

		if (totalesConcepto.size() == 0)
			return;

		Iterator it = totalesConcepto.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalConcepto totalConcepto = (TotalConcepto) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, totalConcepto.getFec_valor());

			hsmpTemp = bodyRow.get(totalConcepto.getRow_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalConcepto.getTotal()));

		}

	}
	// *****************************************************************************END

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subPrintTotalesXWeeks 'PARAMS : none 'RETURN : none 'RESPONSABILITY :
	 * PUNTO DE CONTROL PARA IMPRESION DE TOTALES EN LAS COLUMNAS DE SEMANAS
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subPrintTotalesXWeeks(List<HashMap> bodyRow) {

		String strFecIni;
		String strFecFin;

		String strFecIniMes;
		String strFecFinMes;

		long lngNoMonths;
		int iMeses;

		int iRowIni;
		int iRowFin;

		int iColIni;
		int iColFin;

		int iColIM;
		int iColFM;

		int lngNoWeek;
		int iWeek;

		int iColIS;
		int iColFS;

		String strFecIniSem;
		String strFecFinSem;

		iColIni = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "I");
		if (iColIni == 0)
			iColIni = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "I_H");

		iColFin = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "F");

		if (iColFin == 0)
			iColFin = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "H_F");

		iRowIni = getRowOfIdHeaderDates(bodyRow, "FEC");
		iRowFin = getRowOfIdHeaderDates(bodyRow, "FEC");

		HashMap hsmpTemp = new HashMap<String, String>();
		hsmpTemp = bodyRow.get(iRowIni);

		strFecIni = hsmpTemp.get("col" + iColIni).toString();
		strFecFin = hsmpTemp.get("col" + iColFin).toString();

		lngNoMonths = getMonthsBetweenTwoDates(strFecIni, strFecFin);

		for (iMeses = 1; iMeses <= lngNoMonths; iMeses++) {

			iColIM = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "IM_FM"), "IM--" + iMeses);
			iColFM = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "IM_FM"), "FM--" + iMeses);

			strFecIniMes = hsmpTemp.get("col" + iColIM).toString();
			strFecFinMes = hsmpTemp.get("col" + iColFM).toString();
			lngNoWeek = 0;

			try {
				lngNoWeek = getWeeksBetweenTwoDates(strFecIniMes, strFecFinMes);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// logger.info("++++++++++++++++++++++++++++++++");
			// logger.info("MES INICIAL....." + strFecIniMes);

			for (iWeek = 1; iWeek <= lngNoWeek; iWeek++) {

				iColIS = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "IS_FS"),
						"IS--" + iWeek + "-M--" + iMeses);
				iColFS = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "IS_FS"),
						"FS--" + iWeek + "-M--" + iMeses);

				iColIS = iColIS == 0 ? iColFS : iColIS;
				iColFS = iColFS == 0 ? iColIS : iColFS;

				strFecIniSem = hsmpTemp.get("col" + iColIS).toString();
				strFecFinSem = hsmpTemp.get("col" + iColFS).toString();

				// logger.info("SEMANA INICIAL....." + strFecIniSem);
				// logger.info("SEMANA FINAL......." + strFecFinSem);

				subPrintTotalXSemanas(bodyRow, strFecIniSem, strFecFinSem, iMeses, iWeek);
				subTotalSemanaForIngresoOrEgreso(bodyRow, strFecIniSem, strFecFinSem, iMeses, iWeek);
				subTotalXSemanaForTypeConcepto(bodyRow, strFecIniSem, strFecFinSem, iMeses, iWeek);
				subTotalXSemanaForConcepto(bodyRow, strFecIniSem, strFecFinSem, iMeses, iWeek);

			}

			// logger.info("MES FINAL....." + strFecFinMes);
			// logger.info("------------------------------");

		}

	}
	// *****************************************************************************END

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subPrintTotalXSemanas 'PARAMS : none 'RETURN : none 'RESPONSABILITY :
	 * PUNTO DE CONTROL PARA IMPRESION DE TOTALES EN LAS COLUMNAS DE SEMANAS
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subPrintTotalXSemanas(List<HashMap> bodyRow, String strFecIni, String strFecFinSem, int meses,
			int week) {

		List<TotalDiario> totalesWeek = cashFlowDao.funSqlGetTotalesXWeek(IdDivisa, NoUsuario, strFecIni, strFecFinSem);

		if (totalesWeek.size() == 0)
			return;

		Iterator it = totalesWeek.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();

		// logger.info( "Total de registros Semanales " + totalesWeek.size() );
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalDiario totalWeek = (TotalDiario) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "W--" + week + "-M--" + meses);

			hsmpTemp = bodyRow.get(totalWeek.getRow_total_sd());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalWeek.getTotal()));
			hsmpTemp = bodyRow.get(totalWeek.getRow_total_soa());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalWeek.getTotal()));

		}

	}
	// *****************************************************************************END

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subTotalSemanaForIngresoOrEgreso 'PARAMS : none 'RETURN : none
	 * 'RESPONSABILITY : IMPRESION DE TOTALES A NIVEL SEMANA DE INGRESO Y EGRESO
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */

	private void subTotalSemanaForIngresoOrEgreso(List<HashMap> bodyRow, String strFecIniSem, String strFecFinSem,
			int meses, int week) {

		List<TotalIngresoEgreso> totalesIngresoEgreso = cashFlowDao.funSqlGetTotalesXSemanaIngresoEgreso(IdDivisa,
				NoUsuario, strFecIniSem, strFecFinSem);

		if (totalesIngresoEgreso.size() == 0)
			return;

		Iterator it = totalesIngresoEgreso.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();

		while (it.hasNext()) {

			TotalIngresoEgreso totalIngresoEgreso = (TotalIngresoEgreso) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "W--" + week + "-M--" + meses);

			hsmpTemp = bodyRow.get(totalIngresoEgreso.getRow_ingresoegreso());
			hsmpTemp.put("col" + iColFecha, totalIngresoEgreso.getTotal());
		}

	}
	// *****************************************************************************END

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subTotalXSemanaForTypeConcepto 'PARAMS : none 'RETURN : none
	 * 'RESPONSABILITY : IMPRESION DE TOTALES A NIVEL SEMANA DE TIPOS DE
	 * CONCEPTOS
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subTotalXSemanaForTypeConcepto(List<HashMap> bodyRow, String strFecIniSem, String strFecFinSem,
			int meses, int week) {

		List<TotalTipoConcepto> totalesTipoConcepto = cashFlowDao.funSqlGetTotalesXSemanaForTypeConcepto(IdDivisa,
				NoUsuario, strFecIniSem, strFecFinSem,week);

		if (totalesTipoConcepto.size() == 0)
			return;

		Iterator it = totalesTipoConcepto.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalTipoConcepto totalTipoConcepto = (TotalTipoConcepto) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "W--" + week + "-M--" + meses);

			hsmpTemp = bodyRow.get(totalTipoConcepto.getRow_tipo_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalTipoConcepto.getTotal()));

		}

	}
	// *****************************************************************************END

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subTotalXSemanaForConcepto 'PARAMS : none 'RETURN : none 'RESPONSABILITY
	 * : IMPRESION DE TOTALES A NIVEL SEMANA DE CONCEPTOS
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subTotalXSemanaForConcepto(List<HashMap> bodyRow, String strFecIniSem, String strFecFinSem, int meses,
			int week) {

		List<TotalConcepto> totalesConcepto = cashFlowDao.funSqlGetTotalesXWeekForConcepto(IdDivisa, NoUsuario,
				strFecIniSem, strFecFinSem);

		if (totalesConcepto.size() == 0)
			return;

		Iterator it = totalesConcepto.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalConcepto totalConcepto = (TotalConcepto) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "W--" + week + "-M--" + meses);

			hsmpTemp = bodyRow.get(totalConcepto.getRow_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalConcepto.getTotal()));

		}

	}

	/**
	 * graficarFlujoEfectivo Business
	 * 
	 * @param noEmpresa
	 * @param sFecIni
	 * @param sFecFin
	 * @param idReporte
	 * @return
	 */
	public List<TotalConcepto> graficarFlujoEfectivo(int noEmpresa, String sFecIni, String sFecFin) {
		List<TotalConcepto> lResult = cashFlowDao.graficarFlujoEfectivo(noEmpresa, sFecIni, sFecFin);
		return lResult;
	}

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subPrintTotalesXMonths 'PARAMS : none 'RETURN : none 'RESPONSABILITY :
	 * CONTROL DE IMPRESION DE TOTALES EN LAS COLUMNAS DE MESES
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subPrintTotalesXMonths(List<HashMap> bodyRow) {

		String strFecIni;
		String strFecFin;

		String strFecIniMes;
		String strFecFinMes;

		long lngNoMonths;
		int iMeses;

		int iRowIni;
		int iRowFin;

		int iColIni;
		int iColFin;

		int iColIM;
		int iColFM;

		iColIni = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "I");
		if (iColIni == 0)
			iColIni = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "I_H");

		iColFin = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "F");

		if (iColFin == 0)
			iColFin = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "H_F");

		iRowIni = getRowOfIdHeaderDates(bodyRow, "FEC");
		iRowFin = getRowOfIdHeaderDates(bodyRow, "FEC");

		HashMap hsmpTemp = new HashMap<String, String>();
		hsmpTemp = bodyRow.get(iRowIni);

		strFecIni = hsmpTemp.get("col" + iColIni).toString();
		strFecFin = hsmpTemp.get("col" + iColFin).toString();

		lngNoMonths = getMonthsBetweenTwoDates(strFecIni, strFecFin);

		for (iMeses = 1; iMeses <= lngNoMonths; iMeses++) {

			iColIM = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "IM_FM"), "IM--" + iMeses);
			iColFM = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "IM_FM"), "FM--" + iMeses);

			strFecIniMes = hsmpTemp.get("col" + iColIM).toString();
			strFecFinMes = hsmpTemp.get("col" + iColFM).toString();

			subPrintTotalXMes(bodyRow, strFecIniMes, strFecFinMes, iMeses);
			subTotalMesForIngresoOrEgreso(bodyRow, strFecIniMes, strFecFinMes, iMeses);
			subTotalXMesForTypeConcepto(bodyRow, strFecIniMes, strFecFinMes, iMeses);
			subTotalXMesForConcepto(bodyRow, strFecIniMes, strFecFinMes, iMeses);

		}

	}

	private void subPrintTotalesXMonthsAjustado(List<HashMap> bodyRow) {

		String strFecIni;
		String strFecFin;

		String strFecIniMes;
		String strFecFinMes;

		long lngNoMonths;
		int iMeses;

		int iRowIni;
		int iRowFin;

		int iColIni;
		int iColFin;

		int iColIM;
		int iColFM;

		iColIni = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "I");
		if (iColIni == 0)
			iColIni = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "I_H");

		iColFin = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "F");

		if (iColFin == 0)
			iColFin = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "H_F");

		iRowIni = getRowOfIdHeaderDates(bodyRow, "FEC");
		iRowFin = getRowOfIdHeaderDates(bodyRow, "FEC");

		HashMap hsmpTemp = new HashMap<String, String>();
		hsmpTemp = bodyRow.get(iRowIni);

		strFecIni = hsmpTemp.get("col" + iColIni).toString();
		strFecFin = hsmpTemp.get("col" + iColFin).toString();

		lngNoMonths = getMonthsBetweenTwoDates(strFecIni, strFecFin);

		for (iMeses = 1; iMeses <= lngNoMonths; iMeses++) {

			iColIM = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "IM_FM"), "IM--" + iMeses);
			iColFM = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "IM_FM"), "FM--" + iMeses);

			strFecIniMes = hsmpTemp.get("col" + iColIM).toString();
			strFecFinMes = hsmpTemp.get("col" + iColFM).toString();

			subPrintTotalXMesAjustado(bodyRow, strFecIniMes, strFecFinMes, iMeses);
			subTotalMesForIngresoOrEgresoAjustado(bodyRow, strFecIniMes, strFecFinMes, iMeses);
			subTotalXMesForTypeConceptoAjustado(bodyRow, strFecIniMes, strFecFinMes, iMeses);
			subTotalXMesForConceptoAjustado(bodyRow, strFecIniMes, strFecFinMes, iMeses);

		}

	}

	private void subPrintTotalesXMonthsOriginal(List<HashMap> bodyRow) {
		String strFecIni;
		String strFecFin;

		String strFecIniMes;
		String strFecFinMes;

		long lngNoMonths;
		int iMeses;

		int iRowIni;
		int iRowFin;

		int iColIni;
		int iColFin;

		int iColIM;
		int iColFM;

		iColIni = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "I");
		if (iColIni == 0)
			iColIni = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "I_H");

		iColFin = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "F");

		if (iColFin == 0)
			iColFin = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "H_F");

		iRowIni = getRowOfIdHeaderDates(bodyRow, "FEC");
		iRowFin = getRowOfIdHeaderDates(bodyRow, "FEC");

		HashMap hsmpTemp = new HashMap<String, String>();
		hsmpTemp = bodyRow.get(iRowIni);

		strFecIni = hsmpTemp.get("col" + iColIni).toString();
		strFecFin = hsmpTemp.get("col" + iColFin).toString();

		lngNoMonths = getMonthsBetweenTwoDates(strFecIni, strFecFin);

		for (iMeses = 1; iMeses <= lngNoMonths; iMeses++) {

			iColIM = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "IM_FM"), "IM--" + iMeses);
			iColFM = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "IM_FM"), "FM--" + iMeses);

			strFecIniMes = hsmpTemp.get("col" + iColIM).toString();
			strFecFinMes = hsmpTemp.get("col" + iColFM).toString();

			subPrintTotalXMesOriginal(bodyRow, strFecIniMes, strFecFinMes, iMeses);
			subTotalMesForIngresoOrEgresoOriginal(bodyRow, strFecIniMes, strFecFinMes, iMeses);
			subTotalXMesForTypeConceptoOriginal(bodyRow, strFecIniMes, strFecFinMes, iMeses);
			subTotalXMesForConceptoOriginal(bodyRow, strFecIniMes, strFecFinMes, iMeses);
		}
	}

	// *****************************************************************************END

	/* hector mensual */

	private void subPrintTotalesXMonthsMensual(List<HashMap> bodyRow) {

		String strFecIni;
		String strFecFin;

		String strFecIniMes;
		String strFecFinMes;

		long lngNoMonths;
		int iMeses;

		int iRowIni;
		int iRowFin;

		int iColIni;
		int iColFin;

		int iColIM;
		int iColFM;

		iColIni = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "I");
		if (iColIni == 0)
			iColIni = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "I_H");

		iColFin = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "F");

		if (iColFin == 0)
			iColFin = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "H_F");

		iRowIni = getRowOfIdHeaderDates(bodyRow, "FEC");
		iRowFin = getRowOfIdHeaderDates(bodyRow, "FEC");

		HashMap hsmpTemp = new HashMap<String, String>();
		hsmpTemp = bodyRow.get(iRowIni);

		strFecIni = hsmpTemp.get("col" + iColIni).toString();
		strFecFin = hsmpTemp.get("col" + iColFin).toString();

		lngNoMonths = getMonthsBetweenTwoDates(strFecIni, strFecFin);

		for (iMeses = 1; iMeses <= lngNoMonths; iMeses++) {

			iColIM = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "IM_FM"), "IM--" + iMeses);
			iColFM = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "IM_FM"), "FM--" + iMeses);

			strFecIniMes = hsmpTemp.get("col" + iColIM).toString();
			strFecFinMes = hsmpTemp.get("col" + iColFM).toString();

			subPrintTotalXMesMesual(bodyRow, strFecIniMes, strFecFinMes, iMeses);
			subTotalMesForIngresoOrEgresoMensual(bodyRow, strFecIniMes, strFecFinMes, iMeses);
			subTotalXMesForTypeConceptoMensual(bodyRow, strFecIniMes, strFecFinMes, iMeses);
			subTotalXMesForConceptoMensual(bodyRow, strFecIniMes, strFecFinMes, iMeses);

		}

	}

	/* Fin mensual */

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subPrintTotalesXMonths 'PARAMS : none 'RETURN : none 'RESPONSABILITY :
	 * CONTROL DE IMPRESION DE TOTALES EN LAS COLUMNAS DE MESES
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subPrintTotalesXMonthsComparativos(List<HashMap> bodyRow) {

		String strFecIni;
		String strFecFin;

		String strFecIniMes;
		String strFecFinMes;

		long lngNoMonths;
		int iMeses;

		int iRowIni;
		int iRowFin;

		int iColIni;
		int iColFin;

		int iColIM;
		int iColFM;

		iColIni = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "I");
		if (iColIni == 0)
			iColIni = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "I_H");

		iColFin = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "F");

		if (iColFin == 0)
			iColFin = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "H_F");

		iRowIni = getRowOfIdHeaderDates(bodyRow, "FEC");
		iRowFin = getRowOfIdHeaderDates(bodyRow, "FEC");

		HashMap hsmpTemp = new HashMap<String, String>();
		hsmpTemp = bodyRow.get(iRowIni);

		strFecIni = hsmpTemp.get("col" + iColIni).toString();
		strFecFin = hsmpTemp.get("col" + iColFin).toString();

		lngNoMonths = getMonthsBetweenTwoDates(strFecIni, strFecFin);

		for (iMeses = 1; iMeses <= lngNoMonths; iMeses++) {

			iColIM = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "IM_FM"), "IM--" + iMeses);
			iColFM = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "IM_FM"), "FM--" + iMeses);

			strFecIniMes = hsmpTemp.get("col" + iColIM).toString();
			strFecFinMes = hsmpTemp.get("col" + iColFM).toString();

			subPrintTotalXMesComparativo(bodyRow, strFecIniMes, strFecFinMes, iMeses);
			subTotalMesForIngresoOrEgresoComparativo(bodyRow, strFecIniMes, strFecFinMes, iMeses);
			subTotalXMesForTypeConceptoComparativo(bodyRow, strFecIniMes, strFecFinMes, iMeses);
			subTotalXMesForConceptoComparativo(bodyRow, strFecIniMes, strFecFinMes, iMeses);

		}

	}

	// *****************************************************************************END
	private void subPrintTotalesXMonthsComparativosRealAjustado(List<HashMap> bodyRow) {

		String strFecIni;
		String strFecFin;

		String strFecIniMes;
		String strFecFinMes;

		long lngNoMonths;
		int iMeses;

		int iRowIni;
		int iRowFin;

		int iColIni;
		int iColFin;

		int iColIM;
		int iColFM;

		iColIni = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "I");
		if (iColIni == 0)
			iColIni = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "I_H");

		iColFin = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "F");

		if (iColFin == 0)
			iColFin = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "H_F");

		iRowIni = getRowOfIdHeaderDates(bodyRow, "FEC");
		iRowFin = getRowOfIdHeaderDates(bodyRow, "FEC");

		HashMap hsmpTemp = new HashMap<String, String>();
		hsmpTemp = bodyRow.get(iRowIni);

		strFecIni = hsmpTemp.get("col" + iColIni).toString();
		strFecFin = hsmpTemp.get("col" + iColFin).toString();

		lngNoMonths = getMonthsBetweenTwoDates(strFecIni, strFecFin);

		for (iMeses = 1; iMeses <= lngNoMonths; iMeses++) {

			iColIM = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "IM_FM"), "IM--" + iMeses);
			iColFM = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "IM_FM"), "FM--" + iMeses);

			strFecIniMes = hsmpTemp.get("col" + iColIM).toString();
			strFecFinMes = hsmpTemp.get("col" + iColFM).toString();

			subPrintTotalXMesComparativoRealAjustado(bodyRow, strFecIniMes, strFecFinMes, iMeses);
			subTotalMesForIngresoOrEgresoComparativoRealAjustado(bodyRow, strFecIniMes, strFecFinMes, iMeses);
			subTotalXMesForTypeConceptoComparativoRealAjustado(bodyRow, strFecIniMes, strFecFinMes, iMeses);
			subTotalXMesForConceptoComparativoRealAjustado(bodyRow, strFecIniMes, strFecFinMes, iMeses);

		}

	}

	private void subPrintTotalesXMonthsComparativosOriginalAjustado(List<HashMap> bodyRow) {

		String strFecIni;
		String strFecFin;

		String strFecIniMes;
		String strFecFinMes;

		long lngNoMonths;
		int iMeses;

		int iRowIni;
		int iRowFin;

		int iColIni;
		int iColFin;

		int iColIM;
		int iColFM;

		iColIni = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "I");
		if (iColIni == 0)
			iColIni = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "I_H");

		iColFin = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "F");

		if (iColFin == 0)
			iColFin = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "H_F");

		iRowIni = getRowOfIdHeaderDates(bodyRow, "FEC");
		iRowFin = getRowOfIdHeaderDates(bodyRow, "FEC");

		HashMap hsmpTemp = new HashMap<String, String>();
		hsmpTemp = bodyRow.get(iRowIni);

		strFecIni = hsmpTemp.get("col" + iColIni).toString();
		strFecFin = hsmpTemp.get("col" + iColFin).toString();

		lngNoMonths = getMonthsBetweenTwoDates(strFecIni, strFecFin);

		for (iMeses = 1; iMeses <= lngNoMonths; iMeses++) {

			iColIM = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "IM_FM"), "IM--" + iMeses);
			iColFM = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "IM_FM"), "FM--" + iMeses);

			strFecIniMes = hsmpTemp.get("col" + iColIM).toString();
			strFecFinMes = hsmpTemp.get("col" + iColFM).toString();

			subPrintTotalXMesComparativoOriginalAjustado(bodyRow, strFecIniMes, strFecFinMes, iMeses);
			subTotalMesForIngresoOrEgresoComparativoOriginalAjustado(bodyRow, strFecIniMes, strFecFinMes, iMeses);
			subTotalXMesForTypeConceptoComparativoOriginalAjustado(bodyRow, strFecIniMes, strFecFinMes, iMeses);
			subTotalXMesForConceptoComparativoOriginalAjustado(bodyRow, strFecIniMes, strFecFinMes, iMeses);

		}

	}

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subPrintTotalesXMonths 'PARAMS : none 'RETURN : none 'RESPONSABILITY :
	 * CONTROL DE IMPRESION DE TOTALES EN LAS COLUMNAS DE MESES
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subPrintTotalesXMonthsOrigAjusReal(List<HashMap> bodyRow) {

		String strFecIni;
		String strFecFin;

		String strFecIniMes;
		String strFecFinMes;

		long lngNoMonths;
		int iMeses;

		int iRowIni;
		int iRowFin;

		int iColIni;
		int iColFin;

		int iColIM;
		int iColFM;

		iColIni = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "I");
		if (iColIni == 0)
			iColIni = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "I_H");

		iColFin = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "F");

		if (iColFin == 0)

			iColFin = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"), "H_F");

		iRowIni = getRowOfIdHeaderDates(bodyRow, "FEC");
		iRowFin = getRowOfIdHeaderDates(bodyRow, "FEC");

		HashMap hsmpTemp = new HashMap<String, String>();
		hsmpTemp = bodyRow.get(iRowIni);

		strFecIni = hsmpTemp.get("col" + iColIni).toString();
		strFecFin = hsmpTemp.get("col" + iColFin).toString();

		lngNoMonths = getMonthsBetweenTwoDates(strFecIni, strFecFin);

		for (iMeses = 1; iMeses <= lngNoMonths; iMeses++) {

			iColIM = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "IM_FM"), "IM--" + iMeses);
			iColFM = funGetColOfFec(bodyRow, getRowOfIdHeaderDates(bodyRow, "IM_FM"), "FM--" + iMeses);

			strFecIniMes = hsmpTemp.get("col" + iColIM).toString();
			strFecFinMes = hsmpTemp.get("col" + iColFM).toString();

			subPrintTotalXMesOrigAjusReal(bodyRow, strFecIniMes, strFecFinMes, iMeses);
			subTotalMesForIngresoOrEgresoOrigAjusReal(bodyRow, strFecIniMes, strFecFinMes, iMeses);
			subTotalXMesForTypeConceptoOrigAjusReal(bodyRow, strFecIniMes, strFecFinMes, iMeses);
			subTotalXMesForConceptoOrigAjusReal(bodyRow, strFecIniMes, strFecFinMes, iMeses);

		}

	}
	// *****************************************************************************END

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subPrintTotalXMes 'PARAMS : none 'RETURN : none 'RESPONSABILITY : CONTROL
	 * DE IMPRESION DE TOTALES EN LAS FILAS DE TOTALES
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subPrintTotalXMes(List<HashMap> bodyRow, String strFecIniMes, String strFecFinMes, int meses) {

		List<TotalDiario> totalesWeek = cashFlowDao.funSqlGetTotalesXMes(IdDivisa, NoUsuario, strFecIniMes,
				strFecFinMes);

		if (totalesWeek.size() == 0)
			return;

		Iterator it = totalesWeek.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();

		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalDiario totalWeek = (TotalDiario) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

			hsmpTemp = bodyRow.get(totalWeek.getRow_total_sd());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalWeek.getTotal()));
			hsmpTemp = bodyRow.get(totalWeek.getRow_total_soa());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalWeek.getTotal()));

		}

	}

	private void subPrintTotalXMesAjustado(List<HashMap> bodyRow, String strFecIniMes, String strFecFinMes, int meses) {

		List<TotalDiario> totalesWeek = cashFlowDao.funSqlGetTotalesXMesAjustado(IdDivisa, NoUsuario, strFecIniMes,
				strFecFinMes);

		if (totalesWeek.size() == 0)
			return;

		Iterator it = totalesWeek.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();

		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalDiario totalWeek = (TotalDiario) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

			hsmpTemp = bodyRow.get(totalWeek.getRow_total_sd());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalWeek.getTotal()));
			hsmpTemp = bodyRow.get(totalWeek.getRow_total_soa());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalWeek.getTotal()));

		}

	}

	private void subPrintTotalXMesOriginal(List<HashMap> bodyRow, String strFecIniMes, String strFecFinMes, int meses) {

		List<TotalDiario> totalesWeek = cashFlowDao.funSqlGetTotalesXMesOriginal(IdDivisa, NoUsuario, strFecIniMes,
				strFecFinMes);

		if (totalesWeek.size() == 0)
			return;

		Iterator it = totalesWeek.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();

		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalDiario totalWeek = (TotalDiario) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

			hsmpTemp = bodyRow.get(totalWeek.getRow_total_sd());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalWeek.getTotal()));
			hsmpTemp = bodyRow.get(totalWeek.getRow_total_soa());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalWeek.getTotal()));

		}

	}

	// *****************************************************************************END
	/* hector */
	private void subPrintTotalXMesMesual(List<HashMap> bodyRow, String strFecIniMes, String strFecFinMes, int meses) {

		List<TotalDiario> totalesWeek = cashFlowDao.funSqlGetTotalesXMesMensual(IdDivisa, NoUsuario, strFecIniMes,
				strFecFinMes);

		if (totalesWeek.size() == 0)
			return;

		Iterator it = totalesWeek.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();

		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalDiario totalWeek = (TotalDiario) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

			hsmpTemp = bodyRow.get(totalWeek.getRow_total_sd());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalWeek.getTotal()));
			hsmpTemp = bodyRow.get(totalWeek.getRow_total_soa());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalWeek.getTotal()));

		}

	}/* hector */
	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subPrintTotalXMes 'PARAMS : none 'RETURN : none 'RESPONSABILITY : CONTROL
	 * DE IMPRESION DE TOTALES EN LAS FILAS DE TOTALES
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */

	private void subPrintTotalXMesComparativo(List<HashMap> bodyRow, String strFecIniMes, String strFecFinMes,
			int meses) {

		String tipoInfo = "";

		if (IdReporte == 3)
			tipoInfo = "Original";

		if (IdReporte == 5)
			tipoInfo = "Real";

		List<TotalDiario> totalesWeek = cashFlowDao.funSqlGetTotalesXMes(IdDivisa, NoUsuario, strFecIniMes,
				strFecFinMes, tipoInfo);

		Iterator it = totalesWeek.iterator();

		int iColFecha;
		int iColFecha2;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();

		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
		iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

		while (it.hasNext()) {

			TotalDiario totalWeek = (TotalDiario) it.next();

			hsmpTemp = bodyRow.get(totalWeek.getRow_total_sd());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalWeek.getTotal()));
			hsmpTemp = bodyRow.get(totalWeek.getRow_total_soa());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalWeek.getTotal()));

		}

		totalesWeek = cashFlowDao.funSqlGetTotalesXMes(IdDivisa, NoUsuario, strFecIniMes, strFecFinMes, "Ajustado");

		if (totalesWeek.size() == 0)
			return;

		it = totalesWeek.iterator();

		iColFecha2 = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses, iColFecha + 1);

		while (it.hasNext()) {

			TotalDiario totalWeek = (TotalDiario) it.next();

			hsmpTemp = bodyRow.get(totalWeek.getRow_total_sd());
			hsmpTemp.put("col" + iColFecha2, formateador.format(totalWeek.getTotal()));
			hsmpTemp = bodyRow.get(totalWeek.getRow_total_soa());
			hsmpTemp.put("col" + iColFecha2, formateador.format(totalWeek.getTotal()));

		}

	}
	// *****************************************************************************END

	private void subPrintTotalXMesComparativoRealAjustado(List<HashMap> bodyRow, String strFecIniMes,
			String strFecFinMes, int meses) {

		String tipoInfo = "";

		if (IdReporte == 3)
			tipoInfo = "Original";

		if (IdReporte == 5)
			tipoInfo = "Real";

		List<TotalDiario> totalesWeek = cashFlowDao.funSqlGetTotalesXMesRealAjustado(IdDivisa, NoUsuario, strFecIniMes,
				strFecFinMes, "Real");

		Iterator it = totalesWeek.iterator();

		int iColFecha;
		int iColFecha2;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();

		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
		iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

		while (it.hasNext()) {

			TotalDiario totalWeek = (TotalDiario) it.next();

			hsmpTemp = bodyRow.get(totalWeek.getRow_total_sd());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalWeek.getTotal()));
			hsmpTemp = bodyRow.get(totalWeek.getRow_total_soa());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalWeek.getTotal()));

		}

		totalesWeek = cashFlowDao.funSqlGetTotalesXMesRealAjustado2(IdDivisa, NoUsuario, strFecIniMes, strFecFinMes,
				"Ajustado");

		if (totalesWeek.size() == 0)
			return;

		it = totalesWeek.iterator();

		iColFecha2 = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses, iColFecha + 1);

		while (it.hasNext()) {

			TotalDiario totalWeek = (TotalDiario) it.next();

			hsmpTemp = bodyRow.get(totalWeek.getRow_total_sd());
			hsmpTemp.put("col" + iColFecha2, formateador.format(totalWeek.getTotal()));
			hsmpTemp = bodyRow.get(totalWeek.getRow_total_soa());
			hsmpTemp.put("col" + iColFecha2, formateador.format(totalWeek.getTotal()));

		}

	}

	private void subPrintTotalXMesComparativoOriginalAjustado(List<HashMap> bodyRow, String strFecIniMes,
			String strFecFinMes, int meses) {

		String tipoInfo = "";

		if (IdReporte == 3)
			tipoInfo = "Original";

		if (IdReporte == 5)
			tipoInfo = "Real";

		List<TotalDiario> totalesWeek = cashFlowDao.funSqlGetTotalesXMesOriginalAjustado(IdDivisa, NoUsuario,
				strFecIniMes, strFecFinMes, "Original");

		Iterator it = totalesWeek.iterator();

		int iColFecha;
		int iColFecha2;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();

		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
		iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

		while (it.hasNext()) {

			TotalDiario totalWeek = (TotalDiario) it.next();

			hsmpTemp = bodyRow.get(totalWeek.getRow_total_sd());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalWeek.getTotal()));
			hsmpTemp = bodyRow.get(totalWeek.getRow_total_soa());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalWeek.getTotal()));

		}

		totalesWeek = cashFlowDao.funSqlGetTotalesXMesOriginalAjustado2(IdDivisa, NoUsuario, strFecIniMes, strFecFinMes,
				"Ajustado");

		if (totalesWeek.size() == 0)
			return;

		it = totalesWeek.iterator();

		iColFecha2 = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses, iColFecha + 1);

		while (it.hasNext()) {

			TotalDiario totalWeek = (TotalDiario) it.next();

			hsmpTemp = bodyRow.get(totalWeek.getRow_total_sd());
			hsmpTemp.put("col" + iColFecha2, formateador.format(totalWeek.getTotal()));
			hsmpTemp = bodyRow.get(totalWeek.getRow_total_soa());
			hsmpTemp.put("col" + iColFecha2, formateador.format(totalWeek.getTotal()));

		}

	}

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subPrintTotalXMes 'PARAMS : none 'RETURN : none 'RESPONSABILITY : CONTROL
	 * DE IMPRESION DE TOTALES EN LAS FILAS DE TOTALES
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subPrintTotalXMesOrigAjusReal(List<HashMap> bodyRow, String strFecIniMes, String strFecFinMes,
			int meses) {

		int iColFecha;
		int iColFecha2;
		int iColFecha3;
		int iRowFecha;

		List<TotalDiario> totalesWeek = cashFlowDao.funSqlGetTotalesXMes(IdDivisa, NoUsuario, strFecIniMes, strFecFinMes, "Original");

		Iterator it = totalesWeek.iterator();

		HashMap hsmpTemp = new HashMap<String, String>();

		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
		iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

		while (it.hasNext()) {

			TotalDiario totalWeek = (TotalDiario) it.next();

			hsmpTemp = bodyRow.get(totalWeek.getRow_total_sd());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalWeek.getTotal()));
			hsmpTemp = bodyRow.get(totalWeek.getRow_total_soa());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalWeek.getTotal()));

		}

		totalesWeek = cashFlowDao.funSqlGetTotalesXMes(IdDivisa, NoUsuario, strFecIniMes, strFecFinMes, "Ajustado");

		if (totalesWeek.size() == 0)
			return;

		it = totalesWeek.iterator();

		iColFecha2 = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses, iColFecha + 1);

		while (it.hasNext()) {

			TotalDiario totalWeek = (TotalDiario) it.next();

			hsmpTemp = bodyRow.get(totalWeek.getRow_total_sd());
			hsmpTemp.put("col" + iColFecha2, formateador.format(totalWeek.getTotal()));
			hsmpTemp = bodyRow.get(totalWeek.getRow_total_soa());
			hsmpTemp.put("col" + iColFecha2, formateador.format(totalWeek.getTotal()));

		}

		totalesWeek = cashFlowDao.funSqlGetTotalesXMes(IdDivisa, NoUsuario, strFecIniMes, strFecFinMes, "Real");

		it = totalesWeek.iterator();

		iColFecha3 = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses, iColFecha2 + 1);

		while (it.hasNext()) {

			TotalDiario totalWeek = (TotalDiario) it.next();

			hsmpTemp = bodyRow.get(totalWeek.getRow_total_sd());
			hsmpTemp.put("col" + iColFecha3, formateador.format(totalWeek.getTotal()));
			hsmpTemp = bodyRow.get(totalWeek.getRow_total_soa());
			hsmpTemp.put("col" + iColFecha3, formateador.format(totalWeek.getTotal()));

		}

	}
	// *****************************************************************************END

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subTotalMesForIngresoOrEgreso 'PARAMS : none 'RETURN : none
	 * 'RESPONSABILITY : IMPRESION DE TOTALES DE MESES A DE INGRESOS Y EGRESOS
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subTotalMesForIngresoOrEgreso(List<HashMap> bodyRow, String strFecIniMes, String strFecFinMes,
			int meses) {

		List<TotalIngresoEgreso> totalesIngresoEgreso = cashFlowDao.funSqlGetTotalesXMesIngresoEgreso(IdDivisa,
				NoUsuario, strFecIniMes, strFecFinMes);

		if (totalesIngresoEgreso.size() == 0)
			return;

		Iterator it = totalesIngresoEgreso.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalIngresoEgreso totalIngresoEgreso = (TotalIngresoEgreso) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

			hsmpTemp = bodyRow.get(totalIngresoEgreso.getRow_ingresoegreso());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalIngresoEgreso.getTotal()));
		}

	}

	private void subTotalMesForIngresoOrEgresoAjustado(List<HashMap> bodyRow, String strFecIniMes, String strFecFinMes,
			int meses) {

		List<TotalIngresoEgreso> totalesIngresoEgreso = cashFlowDao.funSqlGetTotalesXMesIngresoEgresoAjustado(IdDivisa,
				NoUsuario, strFecIniMes, strFecFinMes);

		if (totalesIngresoEgreso.size() == 0)
			return;

		Iterator it = totalesIngresoEgreso.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalIngresoEgreso totalIngresoEgreso = (TotalIngresoEgreso) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

			hsmpTemp = bodyRow.get(totalIngresoEgreso.getRow_ingresoegreso());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalIngresoEgreso.getTotal()));
		}

	}
	// *****************************************************************************END

	private void subTotalMesForIngresoOrEgresoOriginal(List<HashMap> bodyRow, String strFecIniMes, String strFecFinMes,
			int meses) {

		List<TotalIngresoEgreso> totalesIngresoEgreso = cashFlowDao.funSqlGetTotalesXMesIngresoEgresoOriginal(IdDivisa,
				NoUsuario, strFecIniMes, strFecFinMes);

		if (totalesIngresoEgreso.size() == 0)
			return;

		Iterator it = totalesIngresoEgreso.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalIngresoEgreso totalIngresoEgreso = (TotalIngresoEgreso) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

			hsmpTemp = bodyRow.get(totalIngresoEgreso.getRow_ingresoegreso());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalIngresoEgreso.getTotal()));
		}

	}

	/* HECTOR */
	private void subTotalMesForIngresoOrEgresoMensual(List<HashMap> bodyRow, String strFecIniMes, String strFecFinMes,
			int meses) {

		List<TotalIngresoEgreso> totalesIngresoEgreso = cashFlowDao.funSqlGetTotalesXMesIngresoEgresoMensual(IdDivisa,
				NoUsuario, strFecIniMes, strFecFinMes);

		if (totalesIngresoEgreso.size() == 0)
			return;

		Iterator it = totalesIngresoEgreso.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalIngresoEgreso totalIngresoEgreso = (TotalIngresoEgreso) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

			hsmpTemp = bodyRow.get(totalIngresoEgreso.getRow_ingresoegreso());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalIngresoEgreso.getTotal()));
		}

	}

	/* HECTOR */

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subTotalMesForIngresoOrEgreso 'PARAMS : none 'RETURN : none
	 * 'RESPONSABILITY : IMPRESION DE TOTALES DE MESES A DE INGRESOS Y EGRESOS
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subTotalMesForIngresoOrEgresoComparativo(List<HashMap> bodyRow, String strFecIniMes,
			String strFecFinMes, int meses) {

		String tipoInfo = "";

		if (IdReporte == 3)
			tipoInfo = "Original";

		if (IdReporte == 5)
			tipoInfo = "Real";

		List<TotalIngresoEgreso> totalesIngresoEgreso = cashFlowDao.funSqlGetTotalesXMesIngresoEgreso(IdDivisa,
				NoUsuario, strFecIniMes, strFecFinMes, tipoInfo);

		Iterator it = totalesIngresoEgreso.iterator();

		int iColFecha;
		int iColFecha2;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
		iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

		while (it.hasNext()) {

			TotalIngresoEgreso totalIngresoEgreso = (TotalIngresoEgreso) it.next();

			hsmpTemp = bodyRow.get(totalIngresoEgreso.getRow_ingresoegreso());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalIngresoEgreso.getTotal()));
		}

		totalesIngresoEgreso = cashFlowDao.funSqlGetTotalesXMesIngresoEgreso(IdDivisa, NoUsuario, strFecIniMes,
				strFecFinMes, "Ajustado");

		if (totalesIngresoEgreso.size() == 0)
			return;

		it = totalesIngresoEgreso.iterator();

		iColFecha2 = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses, iColFecha + 1);

		while (it.hasNext()) {

			TotalIngresoEgreso totalIngresoEgreso = (TotalIngresoEgreso) it.next();

			hsmpTemp = bodyRow.get(totalIngresoEgreso.getRow_ingresoegreso());
			hsmpTemp.put("col" + iColFecha2, formateador.format(totalIngresoEgreso.getTotal()));
		}

	}
	// *****************************************************************************END

	private void subTotalMesForIngresoOrEgresoComparativoOriginalAjustado(List<HashMap> bodyRow, String strFecIniMes,
			String strFecFinMes, int meses) {

		String tipoInfo = "";

		if (IdReporte == 3)
			tipoInfo = "Original";

		if (IdReporte == 5)
			tipoInfo = "Real";

		List<TotalIngresoEgreso> totalesIngresoEgreso = cashFlowDao.funSqlGetTotalesXMesIngresoEgresoOriginalAjustado(
				IdDivisa, NoUsuario, strFecIniMes, strFecFinMes, "Original");

		Iterator it = totalesIngresoEgreso.iterator();

		int iColFecha;
		int iColFecha2;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
		iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

		while (it.hasNext()) {

			TotalIngresoEgreso totalIngresoEgreso = (TotalIngresoEgreso) it.next();

			hsmpTemp = bodyRow.get(totalIngresoEgreso.getRow_ingresoegreso());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalIngresoEgreso.getTotal()));
		}

		totalesIngresoEgreso = cashFlowDao.funSqlGetTotalesXMesIngresoEgresoOriginalAjustado2(IdDivisa, NoUsuario,
				strFecIniMes, strFecFinMes, "Ajustado");

		if (totalesIngresoEgreso.size() == 0)
			return;

		it = totalesIngresoEgreso.iterator();

		iColFecha2 = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses, iColFecha + 1);

		while (it.hasNext()) {

			TotalIngresoEgreso totalIngresoEgreso = (TotalIngresoEgreso) it.next();

			hsmpTemp = bodyRow.get(totalIngresoEgreso.getRow_ingresoegreso());
			hsmpTemp.put("col" + iColFecha2, formateador.format(totalIngresoEgreso.getTotal()));
		}

	}

	private void subTotalMesForIngresoOrEgresoComparativoRealAjustado(List<HashMap> bodyRow, String strFecIniMes,
			String strFecFinMes, int meses) {

		String tipoInfo = "";

		if (IdReporte == 3)
			tipoInfo = "Original";

		if (IdReporte == 5)
			tipoInfo = "Real";

		List<TotalIngresoEgreso> totalesIngresoEgreso = cashFlowDao
				.funSqlGetTotalesXMesIngresoEgresoRealAjustado(IdDivisa, NoUsuario, strFecIniMes, strFecFinMes, "Real");

		Iterator it = totalesIngresoEgreso.iterator();

		int iColFecha;
		int iColFecha2;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
		iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

		while (it.hasNext()) {

			TotalIngresoEgreso totalIngresoEgreso = (TotalIngresoEgreso) it.next();

			hsmpTemp = bodyRow.get(totalIngresoEgreso.getRow_ingresoegreso());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalIngresoEgreso.getTotal()));
		}

		totalesIngresoEgreso = cashFlowDao.funSqlGetTotalesXMesIngresoEgresoRealAjustado2(IdDivisa, NoUsuario,
				strFecIniMes, strFecFinMes, "Ajustado");

		if (totalesIngresoEgreso.size() == 0)
			return;

		it = totalesIngresoEgreso.iterator();

		iColFecha2 = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses, iColFecha + 1);

		while (it.hasNext()) {

			TotalIngresoEgreso totalIngresoEgreso = (TotalIngresoEgreso) it.next();

			hsmpTemp = bodyRow.get(totalIngresoEgreso.getRow_ingresoegreso());
			hsmpTemp.put("col" + iColFecha2, formateador.format(totalIngresoEgreso.getTotal()));
		}

	}

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subTotalMesForIngresoOrEgreso 'PARAMS : none 'RETURN : none
	 * 'RESPONSABILITY : IMPRESION DE TOTALES DE MESES A DE INGRESOS Y EGRESOS
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subTotalMesForIngresoOrEgresoOrigAjusReal(List<HashMap> bodyRow, String strFecIniMes,
			String strFecFinMes, int meses) {

		String tipoInfo = "";

		int iColFecha;
		int iColFecha2;
		int iColFecha3;
		int iRowFecha;

		List<TotalIngresoEgreso> totalesIngresoEgreso = cashFlowDao.funSqlGetTotalesXMesIngresoEgreso(IdDivisa,
				NoUsuario, strFecIniMes, strFecFinMes, "Original");

		Iterator it = totalesIngresoEgreso.iterator();

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
		iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

		while (it.hasNext()) {

			TotalIngresoEgreso totalIngresoEgreso = (TotalIngresoEgreso) it.next();

			hsmpTemp = bodyRow.get(totalIngresoEgreso.getRow_ingresoegreso());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalIngresoEgreso.getTotal()));
		}

		totalesIngresoEgreso = cashFlowDao.funSqlGetTotalesXMesIngresoEgreso(IdDivisa, NoUsuario, strFecIniMes,
				strFecFinMes, "Ajustado");

		it = totalesIngresoEgreso.iterator();

		iColFecha2 = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses, iColFecha + 1);

		while (it.hasNext()) {

			TotalIngresoEgreso totalIngresoEgreso = (TotalIngresoEgreso) it.next();

			hsmpTemp = bodyRow.get(totalIngresoEgreso.getRow_ingresoegreso());
			hsmpTemp.put("col" + iColFecha2, formateador.format(totalIngresoEgreso.getTotal()));
		}

		totalesIngresoEgreso = cashFlowDao.funSqlGetTotalesXMesIngresoEgreso(IdDivisa, NoUsuario, strFecIniMes,
				strFecFinMes, "Real");

		it = totalesIngresoEgreso.iterator();

		iColFecha3 = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses, iColFecha2 + 1);

		while (it.hasNext()) {

			TotalIngresoEgreso totalIngresoEgreso = (TotalIngresoEgreso) it.next();

			hsmpTemp = bodyRow.get(totalIngresoEgreso.getRow_ingresoegreso());
			hsmpTemp.put("col" + iColFecha3, formateador.format(totalIngresoEgreso.getTotal()));

		}

	}
	// *****************************************************************************END

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subTotalXMesForTypeConcepto 'PARAMS : none 'RETURN : none 'RESPONSABILITY
	 * : IMPRESION DE TOTALES DE MESES A DE TIPOS DE CONCEPTOS
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subTotalXMesForTypeConcepto(List<HashMap> bodyRow, String strFecIniMes, String strFecFinMes,
			int meses) {

		List<TotalTipoConcepto> totalesTipoConcepto = cashFlowDao.funSqlGetTotalesXMESForTypeConcepto(IdDivisa,
				NoUsuario, strFecIniMes, strFecFinMes);

		if (totalesTipoConcepto.size() == 0)
			return;

		Iterator it = totalesTipoConcepto.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalTipoConcepto totalTipoConcepto = (TotalTipoConcepto) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

			hsmpTemp = bodyRow.get(totalTipoConcepto.getRow_tipo_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalTipoConcepto.getTotal()));

		}

	}

	private void subTotalXMesForTypeConceptoAjustado(List<HashMap> bodyRow, String strFecIniMes, String strFecFinMes,
			int meses) {

		List<TotalTipoConcepto> totalesTipoConcepto = cashFlowDao.funSqlGetTotalesXMESForTypeConceptoAjustado(IdDivisa,
				NoUsuario, strFecIniMes, strFecFinMes);

		if (totalesTipoConcepto.size() == 0)
			return;

		Iterator it = totalesTipoConcepto.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalTipoConcepto totalTipoConcepto = (TotalTipoConcepto) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

			hsmpTemp = bodyRow.get(totalTipoConcepto.getRow_tipo_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalTipoConcepto.getTotal()));

		}

	}
	// *****************************************************************************END

	private void subTotalXMesForTypeConceptoOriginal(List<HashMap> bodyRow, String strFecIniMes, String strFecFinMes,
			int meses) {

		List<TotalTipoConcepto> totalesTipoConcepto = cashFlowDao.funSqlGetTotalesXMESForTypeConceptoOriginal(IdDivisa,
				NoUsuario, strFecIniMes, strFecFinMes);

		if (totalesTipoConcepto.size() == 0)
			return;

		Iterator it = totalesTipoConcepto.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalTipoConcepto totalTipoConcepto = (TotalTipoConcepto) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

			hsmpTemp = bodyRow.get(totalTipoConcepto.getRow_tipo_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalTipoConcepto.getTotal()));

		}

	}

	private void subTotalXMesForTypeConceptoMensual(List<HashMap> bodyRow, String strFecIniMes, String strFecFinMes,
			int meses) {

		List<TotalTipoConcepto> totalesTipoConcepto = cashFlowDao.funSqlGetTotalesXMESForTypeConceptoMensual(IdDivisa,
				NoUsuario, strFecIniMes, strFecFinMes);

		if (totalesTipoConcepto.size() == 0)
			return;

		Iterator it = totalesTipoConcepto.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalTipoConcepto totalTipoConcepto = (TotalTipoConcepto) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

			hsmpTemp = bodyRow.get(totalTipoConcepto.getRow_tipo_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalTipoConcepto.getTotal()));

		}

	}

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subTotalXMesForTypeConcepto 'PARAMS : none 'RETURN : none 'RESPONSABILITY
	 * : IMPRESION DE TOTALES DE MESES A DE TIPOS DE CONCEPTOS
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subTotalXMesForTypeConceptoComparativo(List<HashMap> bodyRow, String strFecIniMes, String strFecFinMes,
			int meses) {

		String tipoInfo = "";

		if (IdReporte == 3)
			tipoInfo = "Original";

		if (IdReporte == 5)
			tipoInfo = "Real";

		List<TotalTipoConcepto> totalesTipoConcepto = cashFlowDao.funSqlGetTotalesXMESForTypeConcepto(IdDivisa,
				NoUsuario, strFecIniMes, strFecFinMes, tipoInfo);

		Iterator it = totalesTipoConcepto.iterator();

		int iColFecha;
		int iColFecha2;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
		iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

		while (it.hasNext()) {

			TotalTipoConcepto totalTipoConcepto = (TotalTipoConcepto) it.next();

			hsmpTemp = bodyRow.get(totalTipoConcepto.getRow_tipo_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalTipoConcepto.getTotal()));

		}

		totalesTipoConcepto = cashFlowDao.funSqlGetTotalesXMESForTypeConcepto(IdDivisa, NoUsuario, strFecIniMes,
				strFecFinMes, "Ajustado");

		if (totalesTipoConcepto.size() == 0)
			return;

		it = totalesTipoConcepto.iterator();

		iColFecha2 = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses, iColFecha + 1);

		while (it.hasNext()) {

			TotalTipoConcepto totalTipoConcepto = (TotalTipoConcepto) it.next();

			hsmpTemp = bodyRow.get(totalTipoConcepto.getRow_tipo_concepto());
			hsmpTemp.put("col" + iColFecha2, formateador.format(totalTipoConcepto.getTotal()));

		}

	}
	// *****************************************************************************END

	private void subTotalXMesForTypeConceptoComparativoOriginalAjustado(List<HashMap> bodyRow, String strFecIniMes,
			String strFecFinMes, int meses) {

		String tipoInfo = "";

		if (IdReporte == 3)
			tipoInfo = "Original";

		if (IdReporte == 5)
			tipoInfo = "Real";

		List<TotalTipoConcepto> totalesTipoConcepto = cashFlowDao.funSqlGetTotalesXMESForTypeConceptoOriginalAjustado(
				IdDivisa, NoUsuario, strFecIniMes, strFecFinMes, "Original");

		Iterator it = totalesTipoConcepto.iterator();

		int iColFecha;
		int iColFecha2;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
		iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

		while (it.hasNext()) {

			TotalTipoConcepto totalTipoConcepto = (TotalTipoConcepto) it.next();

			hsmpTemp = bodyRow.get(totalTipoConcepto.getRow_tipo_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalTipoConcepto.getTotal()));

		}

		totalesTipoConcepto = cashFlowDao.funSqlGetTotalesXMESForTypeConceptoOriginalAjustado2(IdDivisa, NoUsuario,
				strFecIniMes, strFecFinMes, "Ajustado");

		if (totalesTipoConcepto.size() == 0)
			return;

		it = totalesTipoConcepto.iterator();

		iColFecha2 = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses, iColFecha + 1);

		while (it.hasNext()) {

			TotalTipoConcepto totalTipoConcepto = (TotalTipoConcepto) it.next();

			hsmpTemp = bodyRow.get(totalTipoConcepto.getRow_tipo_concepto());
			hsmpTemp.put("col" + iColFecha2, formateador.format(totalTipoConcepto.getTotal()));

		}

	}

	private void subTotalXMesForTypeConceptoComparativoRealAjustado(List<HashMap> bodyRow, String strFecIniMes,
			String strFecFinMes, int meses) {

		String tipoInfo = "";

		if (IdReporte == 3)
			tipoInfo = "Original";

		if (IdReporte == 5)
			tipoInfo = "Real";

		List<TotalTipoConcepto> totalesTipoConcepto = cashFlowDao.funSqlGetTotalesXMESForTypeConceptoRealAjustado(
				IdDivisa, NoUsuario, strFecIniMes, strFecFinMes, "Real");

		Iterator it = totalesTipoConcepto.iterator();

		int iColFecha;
		int iColFecha2;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
		iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

		while (it.hasNext()) {

			TotalTipoConcepto totalTipoConcepto = (TotalTipoConcepto) it.next();

			hsmpTemp = bodyRow.get(totalTipoConcepto.getRow_tipo_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalTipoConcepto.getTotal()));

		}

		totalesTipoConcepto = cashFlowDao.funSqlGetTotalesXMESForTypeConceptoRealAjustado2(IdDivisa, NoUsuario,
				strFecIniMes, strFecFinMes, "Ajustado");

		if (totalesTipoConcepto.size() == 0)
			return;

		it = totalesTipoConcepto.iterator();

		iColFecha2 = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses, iColFecha + 1);

		while (it.hasNext()) {

			TotalTipoConcepto totalTipoConcepto = (TotalTipoConcepto) it.next();

			hsmpTemp = bodyRow.get(totalTipoConcepto.getRow_tipo_concepto());
			hsmpTemp.put("col" + iColFecha2, formateador.format(totalTipoConcepto.getTotal()));

		}

	}

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subTotalXMesForTypeConcepto 'PARAMS : none 'RETURN : none 'RESPONSABILITY
	 * : IMPRESION DE TOTALES DE MESES A DE TIPOS DE CONCEPTOS
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subTotalXMesForTypeConceptoOrigAjusReal(List<HashMap> bodyRow, String strFecIniMes,
			String strFecFinMes, int meses) {

		int iColFecha;
		int iColFecha2;
		int iColFecha3;
		int iRowFecha;

		List<TotalTipoConcepto> totalesTipoConcepto = cashFlowDao.funSqlGetTotalesXMESForTypeConcepto(IdDivisa,
				NoUsuario, strFecIniMes, strFecFinMes, "Original");

		Iterator it = totalesTipoConcepto.iterator();

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
		iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

		while (it.hasNext()) {

			TotalTipoConcepto totalTipoConcepto = (TotalTipoConcepto) it.next();

			hsmpTemp = bodyRow.get(totalTipoConcepto.getRow_tipo_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalTipoConcepto.getTotal()));

		}

		totalesTipoConcepto = cashFlowDao.funSqlGetTotalesXMESForTypeConcepto(IdDivisa, NoUsuario, strFecIniMes,
				strFecFinMes, "Ajustado");

		it = totalesTipoConcepto.iterator();

		iColFecha2 = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses, iColFecha + 1);

		while (it.hasNext()) {

			TotalTipoConcepto totalTipoConcepto = (TotalTipoConcepto) it.next();

			hsmpTemp = bodyRow.get(totalTipoConcepto.getRow_tipo_concepto());
			hsmpTemp.put("col" + iColFecha2, formateador.format(totalTipoConcepto.getTotal()));

		}

		totalesTipoConcepto = cashFlowDao.funSqlGetTotalesXMESForTypeConcepto(IdDivisa, NoUsuario, strFecIniMes,
				strFecFinMes, "Real");

		it = totalesTipoConcepto.iterator();

		iColFecha3 = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses, iColFecha2 + 1);

		while (it.hasNext()) {

			TotalTipoConcepto totalTipoConcepto = (TotalTipoConcepto) it.next();

			hsmpTemp = bodyRow.get(totalTipoConcepto.getRow_tipo_concepto());
			hsmpTemp.put("col" + iColFecha3, formateador.format(totalTipoConcepto.getTotal()));

		}

	}
	// *****************************************************************************END

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subTotalXMesForConcepto 'PARAMS : none 'RETURN : none 'RESPONSABILITY :
	 * IMPRESION DE TOTALES DE MESES A DE CONCEPTOS
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subTotalXMesForConcepto(List<HashMap> bodyRow, String strFecIniMes, String strFecFinMes, int meses) {

		List<TotalConcepto> totalesConcepto = cashFlowDao.funSqlGetTotalesXMesForConcepto(IdDivisa, NoUsuario,
				strFecIniMes, strFecFinMes);

		if (totalesConcepto.size() == 0)
			return;

		Iterator it = totalesConcepto.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalConcepto totalConcepto = (TotalConcepto) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

			hsmpTemp = bodyRow.get(totalConcepto.getRow_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalConcepto.getTotal()));

		}

	}

	private void subTotalXMesForConceptoAjustado(List<HashMap> bodyRow, String strFecIniMes, String strFecFinMes,
			int meses) {

		List<TotalConcepto> totalesConcepto = cashFlowDao.funSqlGetTotalesXMesForConceptoAjustado(IdDivisa, NoUsuario,
				strFecIniMes, strFecFinMes);

		if (totalesConcepto.size() == 0)
			return;

		Iterator it = totalesConcepto.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalConcepto totalConcepto = (TotalConcepto) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

			hsmpTemp = bodyRow.get(totalConcepto.getRow_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalConcepto.getTotal()));

		}

	}
	// *****************************************************************************END

	private void subTotalXMesForConceptoOriginal(List<HashMap> bodyRow, String strFecIniMes, String strFecFinMes,
			int meses) {
		List<TotalConcepto> totalesConcepto = cashFlowDao.funSqlGetTotalesXMesForConceptoOriginal(IdDivisa, NoUsuario,
				strFecIniMes, strFecFinMes);

		if (totalesConcepto.size() == 0)
			return;

		Iterator it = totalesConcepto.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {
			TotalConcepto totalConcepto = (TotalConcepto) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

			hsmpTemp = bodyRow.get(totalConcepto.getRow_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalConcepto.getTotal()));
		}
	}

	/* hector */
	private void subTotalXMesForConceptoMensual(List<HashMap> bodyRow, String strFecIniMes, String strFecFinMes,
			int meses) {

		List<TotalConcepto> totalesConcepto = cashFlowDao.funSqlGetTotalesXMesForConceptoMensual(IdDivisa, NoUsuario,
				strFecIniMes, strFecFinMes);

		if (totalesConcepto.size() == 0)
			return;

		Iterator it = totalesConcepto.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalConcepto totalConcepto = (TotalConcepto) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

			hsmpTemp = bodyRow.get(totalConcepto.getRow_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalConcepto.getTotal()));

		}

	}

	/* hector */
	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subTotalXMesForConcepto 'PARAMS : none 'RETURN : none 'RESPONSABILITY :
	 * IMPRESION DE TOTALES DE MESES A DE CONCEPTOS
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subTotalXMesForConceptoComparativo(List<HashMap> bodyRow, String strFecIniMes, String strFecFinMes,
			int meses) {

		String tipoInfo = "";

		if (IdReporte == 3)
			tipoInfo = "Original";

		if (IdReporte == 5)
			tipoInfo = "Real";

		List<TotalConcepto> totalesConcepto = cashFlowDao.funSqlGetTotalesXMesForConcepto(IdDivisa, NoUsuario,
				strFecIniMes, strFecFinMes, tipoInfo);

		Iterator it = totalesConcepto.iterator();

		int iColFecha;
		int iColFecha2;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
		iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

		while (it.hasNext()) {

			TotalConcepto totalConcepto = (TotalConcepto) it.next();

			hsmpTemp = bodyRow.get(totalConcepto.getRow_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalConcepto.getTotal()));

		}

		totalesConcepto = cashFlowDao.funSqlGetTotalesXMesForConcepto(IdDivisa, NoUsuario, strFecIniMes, strFecFinMes,
				"Ajustado");

		if (totalesConcepto.size() == 0)
			return;

		it = totalesConcepto.iterator();

		iColFecha2 = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses, iColFecha + 1);

		while (it.hasNext()) {

			TotalConcepto totalConcepto = (TotalConcepto) it.next();

			hsmpTemp = bodyRow.get(totalConcepto.getRow_concepto());
			hsmpTemp.put("col" + iColFecha2, formateador.format(totalConcepto.getTotal()));

		}

	}
	// *****************************************************************************END

	private void subTotalXMesForConceptoComparativoOriginalAjustado(List<HashMap> bodyRow, String strFecIniMes,
			String strFecFinMes, int meses) {

		String tipoInfo = "";

		if (IdReporte == 3)
			tipoInfo = "Original";

		if (IdReporte == 5)
			tipoInfo = "Real";

		List<TotalConcepto> totalesConcepto = cashFlowDao.funSqlGetTotalesXMesForConceptoOriginalAjustado(IdDivisa,
				NoUsuario, strFecIniMes, strFecFinMes, "Original");

		Iterator it = totalesConcepto.iterator();

		int iColFecha;
		int iColFecha2;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
		iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

		while (it.hasNext()) {

			TotalConcepto totalConcepto = (TotalConcepto) it.next();

			hsmpTemp = bodyRow.get(totalConcepto.getRow_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalConcepto.getTotal()));

		}

		totalesConcepto = cashFlowDao.funSqlGetTotalesXMesForConceptoOriginalAjustado2(IdDivisa, NoUsuario,
				strFecIniMes, strFecFinMes, "Ajustado");

		if (totalesConcepto.size() == 0)
			return;

		it = totalesConcepto.iterator();

		iColFecha2 = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses, iColFecha + 1);

		while (it.hasNext()) {

			TotalConcepto totalConcepto = (TotalConcepto) it.next();

			hsmpTemp = bodyRow.get(totalConcepto.getRow_concepto());
			hsmpTemp.put("col" + iColFecha2, formateador.format(totalConcepto.getTotal()));

		}

	}

	private void subTotalXMesForConceptoComparativoRealAjustado(List<HashMap> bodyRow, String strFecIniMes,
			String strFecFinMes, int meses) {

		String tipoInfo = "";

		if (IdReporte == 3)
			tipoInfo = "Original";

		if (IdReporte == 5)
			tipoInfo = "Real";

		List<TotalConcepto> totalesConcepto = cashFlowDao.funSqlGetTotalesXMesForConceptoRealAjustado(IdDivisa,
				NoUsuario, strFecIniMes, strFecFinMes, "Real");

		Iterator it = totalesConcepto.iterator();

		int iColFecha;
		int iColFecha2;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
		iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

		while (it.hasNext()) {

			TotalConcepto totalConcepto = (TotalConcepto) it.next();

			hsmpTemp = bodyRow.get(totalConcepto.getRow_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalConcepto.getTotal()));

		}

		totalesConcepto = cashFlowDao.funSqlGetTotalesXMesForConceptoRealAjustado2(IdDivisa, NoUsuario, strFecIniMes,
				strFecFinMes, "Ajustado");

		if (totalesConcepto.size() == 0)
			return;

		it = totalesConcepto.iterator();

		iColFecha2 = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses, iColFecha + 1);

		while (it.hasNext()) {

			TotalConcepto totalConcepto = (TotalConcepto) it.next();

			hsmpTemp = bodyRow.get(totalConcepto.getRow_concepto());
			hsmpTemp.put("col" + iColFecha2, formateador.format(totalConcepto.getTotal()));

		}

	}

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subTotalXMesForConcepto 'PARAMS : none 'RETURN : none 'RESPONSABILITY :
	 * IMPRESION DE TOTALES DE MESES A DE CONCEPTOS
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subTotalXMesForConceptoOrigAjusReal(List<HashMap> bodyRow, String strFecIniMes, String strFecFinMes,
			int meses) {

		int iColFecha;
		int iColFecha2;
		int iColFecha3;
		int iRowFecha;

		List<TotalConcepto> totalesConcepto = cashFlowDao.funSqlGetTotalesXMesForConcepto(IdDivisa, NoUsuario,
				strFecIniMes, strFecFinMes, "Original");

		Iterator it = totalesConcepto.iterator();

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
		iColFecha = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses);

		while (it.hasNext()) {

			TotalConcepto totalConcepto = (TotalConcepto) it.next();

			hsmpTemp = bodyRow.get(totalConcepto.getRow_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalConcepto.getTotal()));

		}

		totalesConcepto = cashFlowDao.funSqlGetTotalesXMesForConcepto(IdDivisa, NoUsuario, strFecIniMes, strFecFinMes,
				"Ajustado");

		it = totalesConcepto.iterator();

		iColFecha2 = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses, iColFecha + 1);

		while (it.hasNext()) {

			TotalConcepto totalConcepto = (TotalConcepto) it.next();

			hsmpTemp = bodyRow.get(totalConcepto.getRow_concepto());
			hsmpTemp.put("col" + iColFecha2, formateador.format(totalConcepto.getTotal()));

		}

		totalesConcepto = cashFlowDao.funSqlGetTotalesXMesForConcepto(IdDivisa, NoUsuario, strFecIniMes, strFecFinMes,
				"Real");

		it = totalesConcepto.iterator();

		iColFecha3 = funGetColOfFec(bodyRow, iRowFecha, "M--" + meses, iColFecha2 + 1);

		while (it.hasNext()) {

			TotalConcepto totalConcepto = (TotalConcepto) it.next();

			hsmpTemp = bodyRow.get(totalConcepto.getRow_concepto());
			hsmpTemp.put("col" + iColFecha3, formateador.format(totalConcepto.getTotal()));

		}

	}
	// *****************************************************************************END

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subPrintControlTotalesPeriodo 'PARAMS : none 'RETURN : none
	 * 'RESPONSABILITY : CONTROL DE IMPRESION DE TOTALES POR PERIODO
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subPrintControlTotalesPeriodo(List<HashMap> bodyRow) {
		subPrintTotalPeriodo(bodyRow);
		subTotalPeriodoForIngresoOrEgreso(bodyRow);
		subTotalXPeriodoForTypeConcepto(bodyRow);
		subTotalXPeriodoForConcepto(bodyRow);

	}

	private void subPrintControlTotalesPeriodoMesual(List<HashMap> bodyRow) {
		subPrintTotalPeriodoMensual(bodyRow);
		subTotalPeriodoForIngresoOrEgresoMensual(bodyRow);
		subTotalXPeriodoForTypeConceptoMensual(bodyRow);
		subTotalXPeriodoForConceptoMensual(bodyRow);

	}

	private void subPrintControlTotalesPeriodoSemanal(List<HashMap> bodyRow) {

		subPrintTotalPeriodoSemanal(bodyRow);
		subTotalPeriodoForIngresoOrEgresoSemanal(bodyRow);
		subTotalXPeriodoForTypeConceptoSemanal(bodyRow);
		subTotalXPeriodoForConceptoSemanal(bodyRow);

	}

	// *****************************************************************************END
	private void subPrintControlTotalesPeriodoAjustado(List<HashMap> bodyRow) {
		subPrintTotalPeriodoAjustado(bodyRow);
		subTotalPeriodoForIngresoOrEgresoAjustado(bodyRow);
		subTotalXPeriodoForTypeConceptoAjustado(bodyRow);
		subTotalXPeriodoForConceptoAjustado(bodyRow);

	}

	private void subPrintControlTotalesPeriodoOriginal(List<HashMap> bodyRow) {
		subPrintTotalPeriodoOriginal(bodyRow);
		subTotalPeriodoForIngresoOrEgresoOriginal(bodyRow);
		subTotalXPeriodoForTypeConceptoOriginal(bodyRow);
		subTotalXPeriodoForConceptoOriginal(bodyRow);

	}

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subPrintControlTotalesPeriodo 'PARAMS : none 'RETURN : none
	 * 'RESPONSABILITY : CONTROL DE IMPRESION DE TOTALES POR PERIODO
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subPrintControlTotalesPeriodoComparativo(List<HashMap> bodyRow) {
		subPrintTotalPeriodoComparativo(bodyRow);
		subTotalPeriodoForIngresoOrEgresoComparativo(bodyRow);
		subTotalXPeriodoForTypeConceptoComparativo(bodyRow);
		subTotalXPeriodoForConceptoComparativo(bodyRow);

	}

	private void subPrintControlTotalesPeriodoComparativoOriginalAjustado(List<HashMap> bodyRow) {
		subPrintTotalPeriodoComparativoOriginalAjustado(bodyRow);
		subTotalPeriodoForIngresoOrEgresoComparativoOriginalAjustado(bodyRow);
		subTotalXPeriodoForTypeConceptoComparativoOriginalAjustado(bodyRow);
		subTotalXPeriodoForConceptoComparativoOriginalAjustado(bodyRow);

	}
	// *****************************************************************************END

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subPrintTotalPeriodo 'PARAMS : none 'RETURN : none 'RESPONSABILITY :
	 * IMPRESION EN LA COLUMNA DE PERIODO DE TOTALES
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subPrintTotalPeriodo(List<HashMap> bodyRow) {

		List<TotalDiario> totalesDiario = cashFlowDao.funSqlGetTotalesXPeriodo(IdDivisa, NoUsuario);

		if (totalesDiario.size() == 0)
			return;

		Iterator it = totalesDiario.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalDiario totalDiario = (TotalDiario) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "10000");

			hsmpTemp = bodyRow.get(totalDiario.getRow_total_sd());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalDiario.getTotal()));
			hsmpTemp = bodyRow.get(totalDiario.getRow_total_soa());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalDiario.getTotal()));

		}

	}

	private void subPrintTotalPeriodoMensual(List<HashMap> bodyRow) {

		List<TotalDiario> totalesDiario = cashFlowDao.funSqlGetTotalesXPeriodoMesual(IdDivisa, NoUsuario, FechaInicial,
				FechaFinal);

		if (totalesDiario.size() == 0)
			return;

		Iterator it = totalesDiario.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalDiario totalDiario = (TotalDiario) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "10000");

			hsmpTemp = bodyRow.get(totalDiario.getRow_total_sd());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalDiario.getTotal()));
			hsmpTemp = bodyRow.get(totalDiario.getRow_total_soa());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalDiario.getTotal()));

		}

	}

	private void subPrintTotalPeriodoSemanal(List<HashMap> bodyRow) {

		List<TotalDiario> totalesDiario = cashFlowDao.funSqlGetTotalesXPeriodoSemanal(IdDivisa, NoUsuario, FechaInicial,
				FechaFinal);

		if (totalesDiario.size() == 0)
			return;

		Iterator it = totalesDiario.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalDiario totalDiario = (TotalDiario) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "10000");

			hsmpTemp = bodyRow.get(totalDiario.getRow_total_sd());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalDiario.getTotal()));
			hsmpTemp = bodyRow.get(totalDiario.getRow_total_soa());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalDiario.getTotal()));

		}

	}

	// *****************************************************************************END
	private void subPrintTotalPeriodoAjustado(List<HashMap> bodyRow) {

		List<TotalDiario> totalesDiario = cashFlowDao.funSqlGetTotalesXPeriodoAjustado(IdDivisa, NoUsuario);

		if (totalesDiario.size() == 0)
			return;

		Iterator it = totalesDiario.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalDiario totalDiario = (TotalDiario) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "10000");

			hsmpTemp = bodyRow.get(totalDiario.getRow_total_sd());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalDiario.getTotal()));
			hsmpTemp = bodyRow.get(totalDiario.getRow_total_soa());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalDiario.getTotal()));

		}

	}

	private void subPrintTotalPeriodoOriginal(List<HashMap> bodyRow) {

		List<TotalDiario> totalesDiario = cashFlowDao.funSqlGetTotalesXPeriodoOriginal(IdDivisa, NoUsuario);

		if (totalesDiario.size() == 0)
			return;

		Iterator it = totalesDiario.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalDiario totalDiario = (TotalDiario) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "10000");

			hsmpTemp = bodyRow.get(totalDiario.getRow_total_sd());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalDiario.getTotal()));
			hsmpTemp = bodyRow.get(totalDiario.getRow_total_soa());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalDiario.getTotal()));

		}

	}

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subPrintTotalPeriodo 'PARAMS : none 'RETURN : none 'RESPONSABILITY :
	 * IMPRESION EN LA COLUMNA DE PERIODO DE INGRESOS Y EGRESOS
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subTotalPeriodoForIngresoOrEgreso(List<HashMap> bodyRow) {

		List<TotalIngresoEgreso> totalesIngresoEgreso = cashFlowDao.funSqlGetTotalesXPeriodoIngresoEgreso(IdDivisa,
				NoUsuario);

		if (totalesIngresoEgreso.size() == 0)
			return;

		Iterator it = totalesIngresoEgreso.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalIngresoEgreso totalIngresoEgreso = (TotalIngresoEgreso) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "10000");

			hsmpTemp = bodyRow.get(totalIngresoEgreso.getRow_ingresoegreso());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalIngresoEgreso.getTotal()));

		}

	}

	private void subTotalPeriodoForIngresoOrEgresoMensual(List<HashMap> bodyRow) {

		List<TotalIngresoEgreso> totalesIngresoEgreso = cashFlowDao
				.funSqlGetTotalesXPeriodoIngresoEgresoMensual(IdDivisa, NoUsuario, FechaInicial, FechaFinal);

		if (totalesIngresoEgreso.size() == 0)
			return;

		Iterator it = totalesIngresoEgreso.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalIngresoEgreso totalIngresoEgreso = (TotalIngresoEgreso) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "10000");

			hsmpTemp = bodyRow.get(totalIngresoEgreso.getRow_ingresoegreso());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalIngresoEgreso.getTotal()));

		}

	}

	private void subTotalPeriodoForIngresoOrEgresoSemanal(List<HashMap> bodyRow) {

		List<TotalIngresoEgreso> totalesIngresoEgreso = cashFlowDao
				.funSqlGetTotalesXPeriodoIngresoEgresoSemanal(IdDivisa, NoUsuario, FechaInicial, FechaFinal);

		if (totalesIngresoEgreso.size() == 0)
			return;

		Iterator it = totalesIngresoEgreso.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalIngresoEgreso totalIngresoEgreso = (TotalIngresoEgreso) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "10000");

			hsmpTemp = bodyRow.get(totalIngresoEgreso.getRow_ingresoegreso());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalIngresoEgreso.getTotal()));

		}

	}
	// *****************************************************************************END

	private void subTotalPeriodoForIngresoOrEgresoAjustado(List<HashMap> bodyRow) {

		List<TotalIngresoEgreso> totalesIngresoEgreso = cashFlowDao
				.funSqlGetTotalesXPeriodoIngresoEgresoAjustado(IdDivisa, NoUsuario);

		if (totalesIngresoEgreso.size() == 0)
			return;

		Iterator it = totalesIngresoEgreso.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalIngresoEgreso totalIngresoEgreso = (TotalIngresoEgreso) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "10000");

			hsmpTemp = bodyRow.get(totalIngresoEgreso.getRow_ingresoegreso());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalIngresoEgreso.getTotal()));

		}

	}

	private void subTotalPeriodoForIngresoOrEgresoOriginal(List<HashMap> bodyRow) {

		List<TotalIngresoEgreso> totalesIngresoEgreso = cashFlowDao
				.funSqlGetTotalesXPeriodoIngresoEgresoOriginal(IdDivisa, NoUsuario);

		if (totalesIngresoEgreso.size() == 0)
			return;

		Iterator it = totalesIngresoEgreso.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalIngresoEgreso totalIngresoEgreso = (TotalIngresoEgreso) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "10000");

			hsmpTemp = bodyRow.get(totalIngresoEgreso.getRow_ingresoegreso());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalIngresoEgreso.getTotal()));

		}

	}

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subTotalXPeriodoForTypeConcepto 'PARAMS : none 'RETURN : none
	 * 'RESPONSABILITY : IMPRESION EN LA COLUMNA DE PERIODO DE TIPOS DE
	 * CONCEPTOS
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subTotalXPeriodoForTypeConcepto(List<HashMap> bodyRow) {

		List<TotalTipoConcepto> totalesTipoConcepto = cashFlowDao.funSqlGetTotalesXPeriodoForTypeConcepto(IdDivisa,
				NoUsuario);

		if (totalesTipoConcepto.size() == 0)
			return;

		Iterator it = totalesTipoConcepto.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalTipoConcepto totalTipoConcepto = (TotalTipoConcepto) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "10000");

			hsmpTemp = bodyRow.get(totalTipoConcepto.getRow_tipo_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalTipoConcepto.getTotal()));

		}

	}

	// *****************************************************************************END
	private void subTotalXPeriodoForTypeConceptoMensual(List<HashMap> bodyRow) {

		List<TotalTipoConcepto> totalesTipoConcepto = cashFlowDao
				.funSqlGetTotalesXPeriodoForTypeConceptoMensual(IdDivisa, NoUsuario, FechaInicial, FechaFinal);

		if (totalesTipoConcepto.size() == 0)
			return;

		Iterator it = totalesTipoConcepto.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalTipoConcepto totalTipoConcepto = (TotalTipoConcepto) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "10000");

			hsmpTemp = bodyRow.get(totalTipoConcepto.getRow_tipo_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalTipoConcepto.getTotal()));

		}

	}

	private void subTotalXPeriodoForTypeConceptoSemanal(List<HashMap> bodyRow) {

		List<TotalTipoConcepto> totalesTipoConcepto = cashFlowDao
				.funSqlGetTotalesXPeriodoForTypeConceptoSemanal(IdDivisa, NoUsuario, FechaInicial, FechaFinal);

		if (totalesTipoConcepto.size() == 0)
			return;

		Iterator it = totalesTipoConcepto.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalTipoConcepto totalTipoConcepto = (TotalTipoConcepto) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "10000");

			hsmpTemp = bodyRow.get(totalTipoConcepto.getRow_tipo_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalTipoConcepto.getTotal()));

		}

	}

	private void subTotalXPeriodoForTypeConceptoAjustado(List<HashMap> bodyRow) {

		List<TotalTipoConcepto> totalesTipoConcepto = cashFlowDao
				.funSqlGetTotalesXPeriodoForTypeConceptoAjustado(IdDivisa, NoUsuario);

		if (totalesTipoConcepto.size() == 0)
			return;

		Iterator it = totalesTipoConcepto.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalTipoConcepto totalTipoConcepto = (TotalTipoConcepto) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "10000");

			hsmpTemp = bodyRow.get(totalTipoConcepto.getRow_tipo_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalTipoConcepto.getTotal()));

		}

	}

	private void subTotalXPeriodoForTypeConceptoOriginal(List<HashMap> bodyRow) {

		List<TotalTipoConcepto> totalesTipoConcepto = cashFlowDao
				.funSqlGetTotalesXPeriodoForTypeConceptoOriginal(IdDivisa, NoUsuario);

		if (totalesTipoConcepto.size() == 0)
			return;

		Iterator it = totalesTipoConcepto.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalTipoConcepto totalTipoConcepto = (TotalTipoConcepto) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "10000");

			hsmpTemp = bodyRow.get(totalTipoConcepto.getRow_tipo_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalTipoConcepto.getTotal()));

		}

	}

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subTotalXPeriodoForConcepto 'PARAMS : none 'RETURN : none 'RESPONSABILITY
	 * : IMPRESION EN LA COLUMNA DE PERIODO DE CONCEPTOS
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subTotalXPeriodoForConcepto(List<HashMap> bodyRow) {

		List<TotalConcepto> totalesConcepto = cashFlowDao.funSqlGetTotalesXPeriodoForConcepto(IdDivisa, NoUsuario);

		if (totalesConcepto.size() == 0)
			return;

		Iterator it = totalesConcepto.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		while (it.hasNext()) {

			TotalConcepto totalConcepto = (TotalConcepto) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "10000");

			hsmpTemp = bodyRow.get(totalConcepto.getRow_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalConcepto.getTotal()));

		}

	}

	private void subTotalXPeriodoForConceptoMensual(List<HashMap> bodyRow) {

		List<TotalConcepto> totalesConcepto = cashFlowDao.funSqlGetTotalesXPeriodoForConceptoMensual(IdDivisa,
				NoUsuario, FechaInicial, FechaFinal);

		if (totalesConcepto.size() == 0)
			return;

		Iterator it = totalesConcepto.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		while (it.hasNext()) {

			TotalConcepto totalConcepto = (TotalConcepto) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "10000");

			hsmpTemp = bodyRow.get(totalConcepto.getRow_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalConcepto.getTotal()));

		}

	}

	private void subTotalXPeriodoForConceptoSemanal(List<HashMap> bodyRow) {

		List<TotalConcepto> totalesConcepto = cashFlowDao.funSqlGetTotalesXPeriodoForConceptosemanal(IdDivisa,
				NoUsuario, FechaInicial, FechaFinal);

		if (totalesConcepto.size() == 0)
			return;

		Iterator it = totalesConcepto.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		while (it.hasNext()) {

			TotalConcepto totalConcepto = (TotalConcepto) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "10000");

			hsmpTemp = bodyRow.get(totalConcepto.getRow_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalConcepto.getTotal()));

		}

	}

	// *****************************************************************************END
	private void subTotalXPeriodoForConceptoAjustado(List<HashMap> bodyRow) {

		List<TotalConcepto> totalesConcepto = cashFlowDao.funSqlGetTotalesXPeriodoForConceptoAjustado(IdDivisa,
				NoUsuario);

		if (totalesConcepto.size() == 0)
			return;

		Iterator it = totalesConcepto.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		while (it.hasNext()) {

			TotalConcepto totalConcepto = (TotalConcepto) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "10000");

			hsmpTemp = bodyRow.get(totalConcepto.getRow_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalConcepto.getTotal()));

		}

	}

	private void subTotalXPeriodoForConceptoOriginal(List<HashMap> bodyRow) {

		List<TotalConcepto> totalesConcepto = cashFlowDao.funSqlGetTotalesXPeriodoForConceptoOriginal(IdDivisa,
				NoUsuario);

		if (totalesConcepto.size() == 0)
			return;

		Iterator it = totalesConcepto.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		while (it.hasNext()) {

			TotalConcepto totalConcepto = (TotalConcepto) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "10000");

			hsmpTemp = bodyRow.get(totalConcepto.getRow_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalConcepto.getTotal()));

		}

	}

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subPrintTotalPeriodo 'PARAMS : none 'RETURN : none 'RESPONSABILITY :
	 * IMPRESION EN LA COLUMNA DE PERIODO DE TOTALES
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subPrintTotalPeriodoComparativo(List<HashMap> bodyRow) {

		int tipoInfo;
		tipoInfo = IdReporte;

		List<TotalDiario> totalesDiario = cashFlowDao.funSqlGetTotalesXPeriodo(IdDivisa, NoUsuario, tipoInfo);

		if (totalesDiario.size() == 0)
			return;

		Iterator it = totalesDiario.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalDiario totalDiario = (TotalDiario) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "10000");

			hsmpTemp = bodyRow.get(totalDiario.getRow_total_sd());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalDiario.getTotal()));
			hsmpTemp = bodyRow.get(totalDiario.getRow_total_soa());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalDiario.getTotal()));

		}

	}

	// *****************************************************************************END
	private void subPrintTotalPeriodoComparativoOriginalAjustado(List<HashMap> bodyRow) {

		int tipoInfo;
		tipoInfo = IdReporte;

		List<TotalDiario> totalesDiario = cashFlowDao.funSqlGetTotalesXPeriodoOriginalAjustado(IdDivisa, NoUsuario,
				tipoInfo);

		if (totalesDiario.size() == 0)
			return;

		Iterator it = totalesDiario.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalDiario totalDiario = (TotalDiario) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "10000");

			hsmpTemp = bodyRow.get(totalDiario.getRow_total_sd());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalDiario.getTotal()));
			hsmpTemp = bodyRow.get(totalDiario.getRow_total_soa());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalDiario.getTotal()));

		}

	}

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subPrintTotalPeriodo 'PARAMS : none 'RETURN : none 'RESPONSABILITY :
	 * IMPRESION EN LA COLUMNA DE PERIODO DE INGRESOS Y EGRESOS
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subTotalPeriodoForIngresoOrEgresoComparativo(List<HashMap> bodyRow) {

		int tipoInfo;
		tipoInfo = IdReporte;

		List<TotalIngresoEgreso> totalesIngresoEgreso = cashFlowDao.funSqlGetTotalesXPeriodoIngresoEgreso(IdDivisa,
				NoUsuario, tipoInfo);

		if (totalesIngresoEgreso.size() == 0)
			return;

		Iterator it = totalesIngresoEgreso.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalIngresoEgreso totalIngresoEgreso = (TotalIngresoEgreso) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "10000");

			hsmpTemp = bodyRow.get(totalIngresoEgreso.getRow_ingresoegreso());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalIngresoEgreso.getTotal()));

		}

	}
	// *****************************************************************************END

	private void subTotalPeriodoForIngresoOrEgresoComparativoOriginalAjustado(List<HashMap> bodyRow) {

		int tipoInfo;
		tipoInfo = IdReporte;

		List<TotalIngresoEgreso> totalesIngresoEgreso = cashFlowDao
				.funSqlGetTotalesXPeriodoIngresoEgresoOriginalAjustado(IdDivisa, NoUsuario, tipoInfo);

		if (totalesIngresoEgreso.size() == 0)
			return;

		Iterator it = totalesIngresoEgreso.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalIngresoEgreso totalIngresoEgreso = (TotalIngresoEgreso) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "10000");

			hsmpTemp = bodyRow.get(totalIngresoEgreso.getRow_ingresoegreso());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalIngresoEgreso.getTotal()));

		}

	}

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subTotalXPeriodoForTypeConcepto 'PARAMS : none 'RETURN : none
	 * 'RESPONSABILITY : IMPRESION EN LA COLUMNA DE PERIODO DE TIPOS DE
	 * CONCEPTOS
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subTotalXPeriodoForTypeConceptoComparativo(List<HashMap> bodyRow) {

		int tipoInfo;
		tipoInfo = IdReporte;

		List<TotalTipoConcepto> totalesTipoConcepto = cashFlowDao.funSqlGetTotalesXPeriodoForTypeConcepto(IdDivisa,
				NoUsuario, tipoInfo);

		if (totalesTipoConcepto.size() == 0)
			return;

		Iterator it = totalesTipoConcepto.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalTipoConcepto totalTipoConcepto = (TotalTipoConcepto) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "10000");

			hsmpTemp = bodyRow.get(totalTipoConcepto.getRow_tipo_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalTipoConcepto.getTotal()));

		}

	}
	// *****************************************************************************END

	private void subTotalXPeriodoForTypeConceptoComparativoOriginalAjustado(List<HashMap> bodyRow) {

		int tipoInfo;
		tipoInfo = IdReporte;

		List<TotalTipoConcepto> totalesTipoConcepto = cashFlowDao
				.funSqlGetTotalesXPeriodoForTypeConceptoOriginalAjustado(IdDivisa, NoUsuario, tipoInfo);

		if (totalesTipoConcepto.size() == 0)
			return;

		Iterator it = totalesTipoConcepto.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		while (it.hasNext()) {

			TotalTipoConcepto totalTipoConcepto = (TotalTipoConcepto) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "10000");

			hsmpTemp = bodyRow.get(totalTipoConcepto.getRow_tipo_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalTipoConcepto.getTotal()));

		}

	}

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subTotalXPeriodoForConcepto 'PARAMS : none 'RETURN : none 'RESPONSABILITY
	 * : IMPRESION EN LA COLUMNA DE PERIODO DE CONCEPTOS
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subTotalXPeriodoForConceptoComparativo(List<HashMap> bodyRow) {

		int tipoInfo;
		tipoInfo = IdReporte;

		List<TotalConcepto> totalesConcepto = cashFlowDao.funSqlGetTotalesXPeriodoForConcepto(IdDivisa, NoUsuario,
				tipoInfo);

		if (totalesConcepto.size() == 0)
			return;

		Iterator it = totalesConcepto.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		while (it.hasNext()) {

			TotalConcepto totalConcepto = (TotalConcepto) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "10000");

			hsmpTemp = bodyRow.get(totalConcepto.getRow_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalConcepto.getTotal()));

		}

	}

	// *****************************************************************************END
	private void subTotalXPeriodoForConceptoComparativoOriginalAjustado(List<HashMap> bodyRow) {

		int tipoInfo;
		tipoInfo = IdReporte;

		List<TotalConcepto> totalesConcepto = cashFlowDao.funSqlGetTotalesXPeriodoForConceptoOriginalAjustado(IdDivisa,
				NoUsuario, tipoInfo);

		if (totalesConcepto.size() == 0)
			return;

		Iterator it = totalesConcepto.iterator();

		int iColFecha;
		int iRowFecha;

		HashMap hsmpTemp = new HashMap<String, String>();
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		while (it.hasNext()) {

			TotalConcepto totalConcepto = (TotalConcepto) it.next();

			iRowFecha = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
			iColFecha = funGetColOfFec(bodyRow, iRowFecha, "10000");

			hsmpTemp = bodyRow.get(totalConcepto.getRow_concepto());
			hsmpTemp.put("col" + iColFecha, formateador.format(totalConcepto.getTotal()));

		}

	}

	private int funGetColOfFec(List<HashMap> bodyRow, int rowOfIniDia, String string) {

		HashMap hsmpTemp = new HashMap<String, String>();
		hsmpTemp = bodyRow.get(rowOfIniDia);

		Gson g = new Gson();

		int c;
		String strValCol;
		boolean band = false;

		for (c = 2; c < lastCol; c++) {

			if (hsmpTemp.get("col" + c) == null)
				hsmpTemp.put("col" + c, "");

			strValCol = hsmpTemp.get("col" + c).toString();

			if (strValCol.equals(string)) {
				band = true;
				break;
			}

		} // for

		if (!band) {
			c = 0;
		}

		return c;
	}
	// *****************************************************************************END

	private int funGetColOfFec(List<HashMap> bodyRow, int rowOfIniDia, String string, int start) {

		HashMap hsmpTemp = new HashMap<String, String>();
		hsmpTemp = bodyRow.get(rowOfIniDia);

		Gson g = new Gson();

		int c;
		String strValCol;
		boolean band = false;

		for (c = start; c < lastCol; c++) {

			if (hsmpTemp.get("col" + c) == null)
				hsmpTemp.put("col" + c, "");

			strValCol = hsmpTemp.get("col" + c).toString();

			if (strValCol.equals(string)) {
				band = true;
				break;
			}

		} // for

		if (!band) {
			c = 0;
		}

		return c;
	}
	// *****************************************************************************END

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subSaldoInicialFinalDiarioNext 'PARAMS : none 'RETURN : none
	 * 'RESPONSABILITY : CICLO DEL SALDO INICIAL DIARIO
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subSaldoInicialFinalDiarioNext(List<HashMap> bodyRow) {

		int iRowOfIniDia;
		int iColSaldoInicial;
		String esDia;
		String esHabil;
		String strSaldoInicial;
		double dblSaldoInicial;
		double dblSaldoInicialNext;
		double dblSaldoFinal1;
		double dblSaldoFinal2;
		double dblSaldoFinal3;

		int iCol;

		int irowTI;
		int irowTE;

		HashMap hsmpSaldoInicial = new HashMap<String, String>();
		hsmpSaldoInicial = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "R-S"));

		HashMap hsmpFecNoDia = new HashMap<String, String>();
		hsmpFecNoDia = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA"));

		HashMap hsmpFecHabil = new HashMap<String, String>();
		hsmpFecHabil = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_HABIL"));

		iRowOfIniDia = 0;
		iRowOfIniDia = getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN");
		iColSaldoInicial = funGetColOfFec(bodyRow, iRowOfIniDia, "I");
		iColSaldoInicial = (iColSaldoInicial == 0) ? funGetColOfFec(bodyRow, iRowOfIniDia, "I_H") : iColSaldoInicial;

		strSaldoInicial = hsmpSaldoInicial.get("col" + iColSaldoInicial).toString();
		strSaldoInicial = strSaldoInicial.replace(",", "");
		dblSaldoInicial = Double.parseDouble(strSaldoInicial);
		dblSaldoInicialNext = dblSaldoInicial;

		irowTI = getRowOfIdHeaderDates(bodyRow, "TI");
		HashMap hsmpTotalIngresos = new HashMap<String, String>();
		hsmpTotalIngresos = bodyRow.get(irowTI);

		irowTE = getRowOfIdHeaderDates(bodyRow, "TE");
		HashMap hsmpTotalEgresos = new HashMap<String, String>();
		hsmpTotalEgresos = bodyRow.get(irowTE);

		HashMap hsmpSOA = new HashMap<String, String>();
		hsmpSOA = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "SOA"));

		HashMap hsmpSD = new HashMap<String, String>();
		hsmpSD = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "SD"));

		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		for (iCol = iColSaldoInicial; iCol < lastCol; iCol++) {

			esDia = hsmpFecNoDia.get("col" + iCol).toString();
			esHabil = hsmpFecHabil.get("col" + iCol).toString();

			if (!(esDia.equals("100") && esDia.equals("1000") && esDia.equals("10000"))) {

				if (esHabil.equals("S")) {

					if (!(iCol == iColSaldoInicial)) {
						hsmpSaldoInicial.put("col" + iCol, formateador.format(dblSaldoInicialNext));
					}

					dblSaldoFinal1 = Double.parseDouble(hsmpTotalIngresos.get("col" + iCol).toString().replace(",", ""))- Double.parseDouble(hsmpTotalEgresos.get("col" + iCol).toString().replace(",", ""));
					dblSaldoFinal2 = Double.parseDouble(hsmpTotalIngresos.get("col" + iCol).toString().replace(",", ""))- Double.parseDouble(hsmpTotalEgresos.get("col" + iCol).toString().replace(",", ""));

					dblSaldoFinal1 = dblSaldoFinal1;
					dblSaldoFinal2 = dblSaldoInicialNext + dblSaldoFinal2;

					hsmpSOA.put("col" + iCol, formateador.format(dblSaldoFinal1));
					hsmpSD.put("col" + iCol, formateador.format(dblSaldoFinal2));

					dblSaldoInicialNext = dblSaldoFinal2;

				}

			}

		}

	}
	// *****************************************************************************END

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subSaldoInicialFinalSemanalNext 'PARAMS : none 'RETURN : none
	 * 'RESPONSABILITY : CICLO DEL SALDO INICIAL SEMANAL
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subSaldoInicialFinalSemanalNext(List<HashMap> bodyRow) {

		int iRowOfIniDia;
		int iColSaldoInicial;
		String esDia;
		String esHabil;
		String strSaldoInicial;
		double dblSaldoInicial;
		double dblSaldoInicialNext;
		double dblSaldoFinal1;
		double dblSaldoFinal2;
		double dblSaldoFinal3;

		int iCol;

		int irowTI;
		int irowTE;

		HashMap hsmpSaldoInicial = new HashMap<String, String>();
		hsmpSaldoInicial = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "R-S"));

		HashMap hsmpFecNoDia = new HashMap<String, String>();
		hsmpFecNoDia = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA"));

		HashMap hsmpFecHabil = new HashMap<String, String>();
		hsmpFecHabil = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_HABIL"));

		iRowOfIniDia = 0;
		iRowOfIniDia = getRowOfIdHeaderDates(bodyRow, "FEC_HABIL");
		iColSaldoInicial = funGetColOfFec(bodyRow, iRowOfIniDia, "V");

		strSaldoInicial = hsmpSaldoInicial.get("col" + iColSaldoInicial).toString();
		strSaldoInicial = strSaldoInicial.replace(",", "");
		dblSaldoInicial = Double.parseDouble(strSaldoInicial);
		dblSaldoInicialNext = dblSaldoInicial;

		irowTI = getRowOfIdHeaderDates(bodyRow, "TI");
		HashMap hsmpTotalIngresos = new HashMap<String, String>();
		hsmpTotalIngresos = bodyRow.get(irowTI);

		irowTE = getRowOfIdHeaderDates(bodyRow, "TE");
		HashMap hsmpTotalEgresos = new HashMap<String, String>();
		hsmpTotalEgresos = bodyRow.get(irowTE);

		HashMap hsmpSOA = new HashMap<String, String>();
		hsmpSOA = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "SOA"));

		HashMap hsmpSD = new HashMap<String, String>();
		hsmpSD = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "SD"));

		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		for (iCol = iColSaldoInicial; iCol < lastCol; iCol++) {

			esDia = hsmpFecNoDia.get("col" + iCol).toString();
			esHabil = hsmpFecHabil.get("col" + iCol).toString();

			if (esDia.equals("100")) {

				if (esHabil.equals("V")) {

					if (!(iCol == iColSaldoInicial)) {
						hsmpSaldoInicial.put("col" + iCol, formateador.format(dblSaldoInicialNext));
					}

					dblSaldoFinal1 = Double.parseDouble(hsmpTotalIngresos.get("col" + iCol).toString().replace(",", ""))- Double.parseDouble(hsmpTotalEgresos.get("col" + iCol).toString().replace(",", ""));
					dblSaldoFinal2 = Double.parseDouble(hsmpTotalIngresos.get("col" + iCol).toString().replace(",", ""))- Double.parseDouble(hsmpTotalEgresos.get("col" + iCol).toString().replace(",", ""));

					dblSaldoFinal1 = dblSaldoFinal1;
					dblSaldoFinal2 = dblSaldoInicialNext + dblSaldoFinal2;

					hsmpSOA.put("col" + iCol, formateador.format(dblSaldoFinal1));
					hsmpSD.put("col" + iCol, formateador.format(dblSaldoFinal2));

					dblSaldoInicialNext = dblSaldoFinal2;

				}

			}

		}

	}
	// *****************************************************************************END

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subSaldoInicialFinalMensualNext 'PARAMS : none 'RETURN : none
	 * 'RESPONSABILITY : CICLO DEL SALDO INICIAL MENSUAL
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subSaldoInicialFinalMensualNext(List<HashMap> bodyRow) {

		int iRowOfIniDia;
		int iColSaldoInicial;
		String esDia;
		String esHabil;
		String strSaldoInicial;
		double dblSaldoInicial;
		double dblSaldoInicialNext;
		double dblSaldoFinal1;
		double dblSaldoFinal2;
		double dblSaldoFinal3;

		int iCol;

		int irowTI;
		int irowTE;

		HashMap hsmpSaldoInicial = new HashMap<String, String>();
		hsmpSaldoInicial = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "R-S"));

		HashMap hsmpFecNoDia = new HashMap<String, String>();
		hsmpFecNoDia = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA"));

		HashMap hsmpFecHabil = new HashMap<String, String>();
		hsmpFecHabil = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_HABIL"));

		iRowOfIniDia = 0;
		iRowOfIniDia = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
		iColSaldoInicial = funGetColOfFec(bodyRow, iRowOfIniDia, "1000");

		strSaldoInicial = hsmpSaldoInicial.get("col" + iColSaldoInicial).toString();
		strSaldoInicial = strSaldoInicial.replace(",", "");
		dblSaldoInicial = Double.parseDouble(strSaldoInicial);
		dblSaldoInicialNext = dblSaldoInicial;

		irowTI = getRowOfIdHeaderDates(bodyRow, "TI");
		HashMap hsmpTotalIngresos = new HashMap<String, String>();
		hsmpTotalIngresos = bodyRow.get(irowTI);

		irowTE = getRowOfIdHeaderDates(bodyRow, "TE");
		HashMap hsmpTotalEgresos = new HashMap<String, String>();
		hsmpTotalEgresos = bodyRow.get(irowTE);

		HashMap hsmpSOA = new HashMap<String, String>();
		hsmpSOA = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "SOA"));

		HashMap hsmpSD = new HashMap<String, String>();
		hsmpSD = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "SD"));

		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		for (iCol = iColSaldoInicial; iCol < lastCol; iCol++) {

			esDia = hsmpFecNoDia.get("col" + iCol).toString();
			esHabil = hsmpFecHabil.get("col" + iCol).toString();

			if (esDia.equals("1000")) {

				if (esHabil.equals("A")) {

					if (!(iCol == iColSaldoInicial)) {
						hsmpSaldoInicial.put("col" + iCol, dblSaldoInicialNext);
					}

					dblSaldoFinal1 = Double.parseDouble(hsmpTotalIngresos.get("col" + iCol).toString().replace(",", ""))- Double.parseDouble(hsmpTotalEgresos.get("col" + iCol).toString().replace(",", ""));

					dblSaldoFinal2 = Double.parseDouble(hsmpTotalIngresos.get("col" + iCol).toString().replace(",", ""))- Double.parseDouble(hsmpTotalEgresos.get("col" + iCol).toString().replace(",", ""));

					dblSaldoFinal1 = dblSaldoFinal1;
					dblSaldoFinal2 = dblSaldoInicialNext + dblSaldoFinal2;

					hsmpSOA.put("col" + iCol, formateador.format(dblSaldoFinal1));
					hsmpSD.put("col" + iCol, formateador.format(dblSaldoFinal2));

					dblSaldoInicialNext = dblSaldoFinal2;

				}

			}

		}

	}
	// *****************************************************************************END

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subSaldoInicialFinalMensualNext 'PARAMS : none 'RETURN : none
	 * 'RESPONSABILITY : CICLO DEL SALDO INICIAL MENSUAL
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subSaldoInicialFinalMensualNextComparativo(List<HashMap> bodyRow) {

		int iRowOfIniDia;
		int iColSaldoInicial;
		int iColSaldoInicial2;
		String esDia;
		String esHabil;
		String strSaldoInicial;
		String strSaldoInicial2;
		double dblSaldoInicial;
		double dblSaldoInicial2;
		double dblSaldoInicialNext;
		double dblSaldoInicialNext2;
		double dblSaldoFinal1;
		double dblSaldoFinal12;
		double dblSaldoFinal2;
		double dblSaldoFinal22;
		double dblSaldoFinal3;
		double dblSaldoFinal32;

		int iCol;

		int irowTI;
		int irowTE;

		HashMap hsmpSaldoInicial = new HashMap<String, String>();
		hsmpSaldoInicial = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "R-S"));

		HashMap hsmpFecNoDia = new HashMap<String, String>();
		hsmpFecNoDia = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA"));

		HashMap hsmpFecHabil = new HashMap<String, String>();
		hsmpFecHabil = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_HABIL"));

		iRowOfIniDia = 0;
		iRowOfIniDia = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
		iColSaldoInicial = funGetColOfFec(bodyRow, iRowOfIniDia, "1000");

		strSaldoInicial = hsmpSaldoInicial.get("col" + iColSaldoInicial).toString();
		strSaldoInicial = strSaldoInicial.replace(",", "");
		dblSaldoInicial = Double.parseDouble(strSaldoInicial);
		dblSaldoInicialNext = dblSaldoInicial;

		iColSaldoInicial2 = funGetColOfFec(bodyRow, iRowOfIniDia, "1000", iColSaldoInicial + 1);
		strSaldoInicial2 = hsmpSaldoInicial.get("col" + iColSaldoInicial2).toString();
		strSaldoInicial2 = strSaldoInicial2.replace(",", "");
		dblSaldoInicial2 = Double.parseDouble(strSaldoInicial2);
		dblSaldoInicialNext2 = dblSaldoInicial2;

		irowTI = getRowOfIdHeaderDates(bodyRow, "TI");
		HashMap hsmpTotalIngresos = new HashMap<String, String>();
		hsmpTotalIngresos = bodyRow.get(irowTI);

		irowTE = getRowOfIdHeaderDates(bodyRow, "TE");
		HashMap hsmpTotalEgresos = new HashMap<String, String>();
		hsmpTotalEgresos = bodyRow.get(irowTE);

		HashMap hsmpSOA = new HashMap<String, String>();
		hsmpSOA = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "SOA"));

		HashMap hsmpSD = new HashMap<String, String>();
		hsmpSD = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "SD"));

		HashMap hsmpMeses = new HashMap<String, String>();
		hsmpMeses = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "TIPO_INFO"));

		String strTipoInfo = "";

		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		String tipoInfo = "";

		if (IdReporte == 3)
			tipoInfo = "Original";

		if (IdReporte == 5)
			tipoInfo = "Real";

		for (iCol = iColSaldoInicial; iCol < lastCol; iCol++) {

			esDia = hsmpFecNoDia.get("col" + iCol).toString();
			esHabil = hsmpFecHabil.get("col" + iCol).toString();
			if (hsmpMeses.get("col" + iCol) != null)
				strTipoInfo = hsmpMeses.get("col" + iCol).toString();
			else
				strTipoInfo = "";

			if (esDia.equals("1000")) {

				if (esHabil.equals("A") && strTipoInfo.equals(tipoInfo)) {

					if (!(iCol == iColSaldoInicial)) {
						hsmpSaldoInicial.put("col" + iCol, dblSaldoInicialNext);
					}

					dblSaldoFinal1 = Double.parseDouble(hsmpTotalIngresos.get("col" + iCol).toString().replace(",", ""))- Double.parseDouble(hsmpTotalEgresos.get("col" + iCol).toString().replace(",", ""));
					dblSaldoFinal2 = Double.parseDouble(hsmpTotalIngresos.get("col" + iCol).toString().replace(",", ""))- Double.parseDouble(hsmpTotalEgresos.get("col" + iCol).toString().replace(",", ""));

					dblSaldoFinal1 = dblSaldoFinal1;
					dblSaldoFinal2 = dblSaldoInicialNext + dblSaldoFinal2;

					hsmpSOA.put("col" + iCol, formateador.format(dblSaldoFinal1));
					hsmpSD.put("col" + iCol, formateador.format(dblSaldoFinal2));

					dblSaldoInicialNext = dblSaldoFinal2;

				}

			}

		}

		for (iCol = iColSaldoInicial2; iCol < lastCol; iCol++) {

			esDia = hsmpFecNoDia.get("col" + iCol).toString();
			esHabil = hsmpFecHabil.get("col" + iCol).toString();
			if (hsmpMeses.get("col" + iCol) != null)
				strTipoInfo = hsmpMeses.get("col" + iCol).toString();
			else
				strTipoInfo = "";

			if (esDia.equals("1000")) {

				if (esHabil.equals("A") && strTipoInfo.equals("Ajustado")) {

					if (!(iCol == iColSaldoInicial2)) {
						hsmpSaldoInicial.put("col" + iCol, dblSaldoInicialNext2);
					}

					dblSaldoFinal12 = Double.parseDouble(hsmpTotalIngresos.get("col" + iCol).toString().replace(",", ""))- Double.parseDouble(hsmpTotalEgresos.get("col" + iCol).toString().replace(",", ""));
					dblSaldoFinal22 = Double.parseDouble(hsmpTotalIngresos.get("col" + iCol).toString().replace(",", ""))- Double.parseDouble(hsmpTotalEgresos.get("col" + iCol).toString().replace(",", ""));

					dblSaldoFinal12 = dblSaldoFinal12;
					dblSaldoFinal22 = dblSaldoInicialNext2 + dblSaldoFinal22;

					hsmpSOA.put("col" + iCol, formateador.format(dblSaldoFinal12));
					hsmpSD.put("col" + iCol, formateador.format(dblSaldoFinal22));

					dblSaldoInicialNext2 = dblSaldoFinal22;

				}

			}

		}

	}
	// *****************************************************************************END

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subSaldoInicialFinalMensualNext 'PARAMS : none 'RETURN : none
	 * 'RESPONSABILITY : CICLO DEL SALDO INICIAL MENSUAL
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subSaldoInicialFinalMensualNextOrigAjusReal(List<HashMap> bodyRow) {

		String esDia;
		String esHabil;

		int iRowOfIniDia;

		int iColSaldoInicial;
		int iColSaldoInicial2;
		int iColSaldoInicial3;

		String strSaldoInicial;
		String strSaldoInicial2;
		String strSaldoInicial3;

		double dblSaldoInicial;
		double dblSaldoInicial2;
		double dblSaldoInicial3;

		double dblSaldoInicialNext;
		double dblSaldoInicialNext2;
		double dblSaldoInicialNext3;

		double dblSaldoFinal1;
		double dblSaldoFinal12;
		double dblSaldoFinal123;

		double dblSaldoFinal2;
		double dblSaldoFinal22;
		double dblSaldoFinal223;

		double dblSaldoFinal3;
		double dblSaldoFinal32;
		double dblSaldoFinal323;

		int iCol;

		int irowTI;
		int irowTE;

		HashMap hsmpSaldoInicial = new HashMap<String, String>();
		hsmpSaldoInicial = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "R-S"));

		HashMap hsmpFecNoDia = new HashMap<String, String>();
		hsmpFecNoDia = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA"));

		HashMap hsmpFecHabil = new HashMap<String, String>();
		hsmpFecHabil = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_HABIL"));

		iRowOfIniDia = 0;
		iRowOfIniDia = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
		iColSaldoInicial = funGetColOfFec(bodyRow, iRowOfIniDia, "1000");

		strSaldoInicial = hsmpSaldoInicial.get("col" + iColSaldoInicial).toString();
		strSaldoInicial = strSaldoInicial.replace(",", "");
		dblSaldoInicial = Double.parseDouble(strSaldoInicial);
		dblSaldoInicialNext = dblSaldoInicial;

		iColSaldoInicial2 = funGetColOfFec(bodyRow, iRowOfIniDia, "1000", iColSaldoInicial + 1);
		strSaldoInicial2 = hsmpSaldoInicial.get("col" + iColSaldoInicial2).toString();
		strSaldoInicial2 = strSaldoInicial2.replace(",", "");
		dblSaldoInicial2 = Double.parseDouble(strSaldoInicial2);
		dblSaldoInicialNext2 = dblSaldoInicial2;

		iColSaldoInicial3 = funGetColOfFec(bodyRow, iRowOfIniDia, "1000", iColSaldoInicial2 + 1);
		strSaldoInicial3 = hsmpSaldoInicial.get("col" + iColSaldoInicial3).toString();
		strSaldoInicial3 = strSaldoInicial2.replace(",", "");
		dblSaldoInicial3 = Double.parseDouble(strSaldoInicial3);
		dblSaldoInicialNext3 = dblSaldoInicial3;

		irowTI = getRowOfIdHeaderDates(bodyRow, "TI");
		HashMap hsmpTotalIngresos = new HashMap<String, String>();
		hsmpTotalIngresos = bodyRow.get(irowTI);

		irowTE = getRowOfIdHeaderDates(bodyRow, "TE");
		HashMap hsmpTotalEgresos = new HashMap<String, String>();
		hsmpTotalEgresos = bodyRow.get(irowTE);

		HashMap hsmpSOA = new HashMap<String, String>();
		hsmpSOA = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "SOA"));

		HashMap hsmpSD = new HashMap<String, String>();
		hsmpSD = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "SD"));

		HashMap hsmpMeses = new HashMap<String, String>();
		hsmpMeses = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "TIPO_INFO"));

		String strTipoInfo = "";

		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		for (iCol = iColSaldoInicial; iCol < lastCol; iCol++) {

			esDia = hsmpFecNoDia.get("col" + iCol).toString();
			esHabil = hsmpFecHabil.get("col" + iCol).toString();
			if (hsmpMeses.get("col" + iCol) != null)
				strTipoInfo = hsmpMeses.get("col" + iCol).toString();
			else
				strTipoInfo = "";

			if (esDia.equals("1000")) {

				if (esHabil.equals("A") && strTipoInfo.equals("Original")) {

					if (!(iCol == iColSaldoInicial)) {
						hsmpSaldoInicial.put("col" + iCol, dblSaldoInicialNext);
					}

					dblSaldoFinal1 = Double.parseDouble(hsmpTotalIngresos.get("col" + iCol).toString().replace(",", ""))- Double.parseDouble(hsmpTotalEgresos.get("col" + iCol).toString().replace(",", ""));
					dblSaldoFinal2 = Double.parseDouble(hsmpTotalIngresos.get("col" + iCol).toString().replace(",", ""))- Double.parseDouble(hsmpTotalEgresos.get("col" + iCol).toString().replace(",", ""));

					dblSaldoFinal1 = dblSaldoFinal1;
					dblSaldoFinal2 = dblSaldoInicialNext + dblSaldoFinal2;

					hsmpSOA.put("col" + iCol, formateador.format(dblSaldoFinal1));
					hsmpSD.put("col" + iCol, formateador.format(dblSaldoFinal2));

					dblSaldoInicialNext = dblSaldoFinal2;

				}

			}

		}

		for (iCol = iColSaldoInicial2; iCol < lastCol; iCol++) {

			esDia = hsmpFecNoDia.get("col" + iCol).toString();
			esHabil = hsmpFecHabil.get("col" + iCol).toString();
			if (hsmpMeses.get("col" + iCol) != null)
				strTipoInfo = hsmpMeses.get("col" + iCol).toString();
			else
				strTipoInfo = "";

			if (esDia.equals("1000")) {

				if (esHabil.equals("A") && strTipoInfo.equals("Ajustado")) {

					if (!(iCol == iColSaldoInicial2)) {
						hsmpSaldoInicial.put("col" + iCol, dblSaldoInicialNext2);
					}

					dblSaldoFinal12 = Double.parseDouble(hsmpTotalIngresos.get("col" + iCol).toString().replace(",", ""))- Double.parseDouble(hsmpTotalEgresos.get("col" + iCol).toString().replace(",", ""));
					dblSaldoFinal22 = Double.parseDouble(hsmpTotalIngresos.get("col" + iCol).toString().replace(",", ""))- Double.parseDouble(hsmpTotalEgresos.get("col" + iCol).toString().replace(",", ""));

					dblSaldoFinal12 = dblSaldoFinal12;
					dblSaldoFinal22 = dblSaldoInicialNext2 + dblSaldoFinal22;

					hsmpSOA.put("col" + iCol, formateador.format(dblSaldoFinal12));
					hsmpSD.put("col" + iCol, formateador.format(dblSaldoFinal22));

					dblSaldoInicialNext2 = dblSaldoFinal22;

				}

			}

		}

		for (iCol = iColSaldoInicial3; iCol < lastCol; iCol++) {

			esDia = hsmpFecNoDia.get("col" + iCol).toString();
			esHabil = hsmpFecHabil.get("col" + iCol).toString();
			if (hsmpMeses.get("col" + iCol) != null)
				strTipoInfo = hsmpMeses.get("col" + iCol).toString();
			else
				strTipoInfo = "";

			if (esDia.equals("1000")) {

				if (esHabil.equals("A") && strTipoInfo.equals("Real")) {

					if (!(iCol == iColSaldoInicial3)) {
						hsmpSaldoInicial.put("col" + iCol, dblSaldoInicialNext3);
					}

					dblSaldoFinal123 = Double.parseDouble(hsmpTotalIngresos.get("col" + iCol).toString().replace(",", ""))- Double.parseDouble(hsmpTotalEgresos.get("col" + iCol).toString().replace(",", ""));
					dblSaldoFinal223 = Double.parseDouble(hsmpTotalIngresos.get("col" + iCol).toString().replace(",", ""))- Double.parseDouble(hsmpTotalEgresos.get("col" + iCol).toString().replace(",", ""));

					dblSaldoFinal123 = dblSaldoFinal123;
					dblSaldoFinal223 = dblSaldoInicialNext3 + dblSaldoFinal223;

					hsmpSOA.put("col" + iCol, formateador.format(dblSaldoFinal123));
					hsmpSD.put("col" + iCol, formateador.format(dblSaldoFinal223));

					dblSaldoInicialNext3 = dblSaldoFinal223;

				}

			}

		}

	}
	// *****************************************************************************END

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 9 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : subrutina 'NAME :
	 * subSaldoInicialFinalMensualNext 'PARAMS : none 'RETURN : none
	 * 'RESPONSABILITY : CICLO DEL SALDO INICIAL PERIODO
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subSaldoInicialFinalPeriodoNext(List<HashMap> bodyRow) {

		int iRowOfIniDia;
		int iColSaldoInicial;
		String esDia;
		String esHabil;
		String strSaldoInicial;
		double dblSaldoInicial;
		double dblSaldoInicialNext;
		double dblSaldoFinal1;
		double dblSaldoFinal2;
		double dblSaldoFinal3;

		int iCol;

		int irowTI;
		int irowTE;

		HashMap hsmpSaldoInicial = new HashMap<String, String>();
		hsmpSaldoInicial = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "R-S"));

		HashMap hsmpFecNoDia = new HashMap<String, String>();
		hsmpFecNoDia = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA"));

		HashMap hsmpFecHabil = new HashMap<String, String>();
		hsmpFecHabil = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_HABIL"));

		iRowOfIniDia = 0;
		iRowOfIniDia = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
		iColSaldoInicial = funGetColOfFec(bodyRow, iRowOfIniDia, "10000");

		strSaldoInicial = hsmpSaldoInicial.get("col" + iColSaldoInicial).toString();
		strSaldoInicial = strSaldoInicial.replace(",", "");
		dblSaldoInicial = Double.parseDouble(strSaldoInicial);
		dblSaldoInicialNext = dblSaldoInicial;

		irowTI = getRowOfIdHeaderDates(bodyRow, "TI");
		HashMap hsmpTotalIngresos = new HashMap<String, String>();
		hsmpTotalIngresos = bodyRow.get(irowTI);

		irowTE = getRowOfIdHeaderDates(bodyRow, "TE");
		HashMap hsmpTotalEgresos = new HashMap<String, String>();
		hsmpTotalEgresos = bodyRow.get(irowTE);

		HashMap hsmpSOA = new HashMap<String, String>();
		hsmpSOA = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "SOA"));

		HashMap hsmpSD = new HashMap<String, String>();
		hsmpSD = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "SD"));
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		for (iCol = iColSaldoInicial; iCol < lastCol; iCol++) {

			esDia = hsmpFecNoDia.get("col" + iCol).toString();
			esHabil = hsmpFecHabil.get("col" + iCol).toString();

			if (esDia.equals("10000")) {

				if (esHabil.equals("-")) {

					if (!(iCol == iColSaldoInicial)) {
						hsmpSaldoInicial.put("col" + iCol, dblSaldoInicialNext);
					}

					dblSaldoFinal1 = Double.parseDouble(hsmpTotalIngresos.get("col" + iCol).toString().replace(",", ""))- Double.parseDouble(hsmpTotalEgresos.get("col" + iCol).toString().replace(",", ""));
					dblSaldoFinal2 = Double.parseDouble(hsmpTotalIngresos.get("col" + iCol).toString().replace(",", ""))- Double.parseDouble(hsmpTotalEgresos.get("col" + iCol).toString().replace(",", ""));

					dblSaldoFinal1 = dblSaldoFinal1;
					dblSaldoFinal2 = dblSaldoInicialNext + dblSaldoFinal2;

					hsmpSOA.put("col" + iCol, formateador.format(dblSaldoFinal1));
					hsmpSD.put("col" + iCol, formateador.format(dblSaldoFinal2));

					dblSaldoInicialNext = dblSaldoFinal2;

				}

			}

		}

	}
	// *****************************************************************************END

	/*
	 * 'AUTOR : JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subPrintColsOfGrid 'PARAMS : NONE 'RETURN : NONE 'RESPONSABILITY : AGREGA
	 * LA COLUMNA TOTAL DEL PERIODO Y LAS DESENCADENA EL LLAMANDO A LA FUNCION
	 * AGREGADO DE COLUMNAS DE MESES
	 */
	public void subPrintColsPeriodoOfGrid(List<HashMap> bodyRow) {

		subPrintColMonths(bodyRow);

		subPrintDatesOfColPeriodo(bodyRow);

		lastCol += 1;
		

	}

	// *****************************************************************************END

	/*
	 * 'AUTOR : JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subPrintColMonths 'PARAMS : NONE 'RETURN : NONE 'RESPONSABILITY : PINTA
	 * LOS MESES EN EL GRID
	 */
	public void subPrintColMonths(List<HashMap> bodyRow) {

		long ntNumMonths = 0;
		String fecInicial;
		String fecFinal;
		String tipoInfo = "";
		ntNumMonths = getMonthsBetweenTwoDates(FechaInicial, FechaFinal);
		lastCol = 3;

		fecInicial = getDatesOfMonth(FechaInicial, FechaFinal, 1);
		fecFinal = getDatesOfMonth(fecInicial, FechaFinal, 2);
		if (IdReporte == 1) {
			tipoInfo = "Original";
		}

		if (IdReporte == 2) {
			tipoInfo = "Ajustado";
		}

		for (int iMes = 1; iMes <= ntNumMonths; iMes++) {

			subPrintColWeek(bodyRow, fecInicial, fecFinal, iMes);
			subPrintDatesOfColMonths1(bodyRow, fecInicial, iMes, tipoInfo);
			fecInicial = getDatesOfMonth(fecInicial, fecFinal, 3);
			fecFinal = getDatesOfMonth(fecInicial, fecFinal, 4);
			lastCol += 1;

		} 
		

	}
	// *****************************************************************************END

	/*
	 * 'AUTOR : JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subPrintColWeekend 'PARAMS : NONE 'RETURN : NONE 'RESPONSABILITY : PINTA
	 * LAS SEMANAS DEL MES
	 */
	public void subPrintColWeek(List<HashMap> bodyRow, String fecInicial, String fecFinal, int iMes) {

		int ntWeeks = 0;
		String strBandWeek = "";

		try {
			ntWeeks = getWeeksBetweenTwoDates(fecInicial, fecFinal);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Calendar calIni = Calendar.getInstance();
		String strFecFinWeek = fecInicial;

		// logger.info("weeeekkk" + ntWeeks);

		for (int iWeek = 1; iWeek <= ntWeeks; iWeek++) {

			if (iWeek == 1)
				strBandWeek = "FIRST_WEEK";
			if (iWeek == ntWeeks)
				strBandWeek = "LAST_WEEK";
			if (iWeek > 1 && iWeek < ntWeeks)
				strBandWeek = "MIDDLE_WEEK";

			try {

				subPrintColDays(bodyRow, strFecFinWeek, iMes, iWeek, strBandWeek);

			} catch (Exception e) {
				e.printStackTrace();
			}
			subPrintDatesOfColWeeks(bodyRow, fecInicial, iMes, iWeek, strBandWeek);
			lastCol += 1;

			strFecFinWeek = subGetRangeOfWeek(strFecFinWeek, strBandWeek);

			calIni.setTime(getTypeDateFromStringFec(strFecFinWeek));
			calIni.add(Calendar.DATE, 1);
			strFecFinWeek = subFecFormatoString(calIni);

			// logger.info("week" + iWeek);

		}

	}
	
	public void subPrintColWeekPrueba(List<HashMap> bodyRow, String fecInicial, String fecFinal, int iMes,String tipo) {

		int ntWeeks = 0;
		String strBandWeek = "";

		try {
			ntWeeks = getWeeksBetweenTwoDates(fecInicial, fecFinal);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Calendar calIni = Calendar.getInstance();
		String strFecFinWeek = fecInicial;

		// logger.info("weeeekkk" + ntWeeks);

		for (int iWeek = 1; iWeek <= ntWeeks; iWeek++) {

			if (iWeek == 1)
				strBandWeek = "FIRST_WEEK";
			if (iWeek == ntWeeks)
				strBandWeek = "LAST_WEEK";
			if (iWeek > 1 && iWeek < ntWeeks)
				strBandWeek = "MIDDLE_WEEK";

			try {

				subPrintColDays(bodyRow, strFecFinWeek, iMes, iWeek, strBandWeek);

			} catch (Exception e) {
				e.printStackTrace();
			}
			subPrintDatesOfColWeeksPrueba(bodyRow, fecInicial, iMes, iWeek, strBandWeek,tipo);
			lastCol += 1;

			strFecFinWeek = subGetRangeOfWeek(strFecFinWeek, strBandWeek);

			calIni.setTime(getTypeDateFromStringFec(strFecFinWeek));
			calIni.add(Calendar.DATE, 1);
			strFecFinWeek = subFecFormatoString(calIni);

			// logger.info("week" + iWeek);

		}

	}
	
	// *****************************************************************************END

	/*
	 * 'AUTOR : JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subPrintColDays 'PARAMS : noSemana, numero de semana del que se pintaran
	 * los dias, fecha inicial y fecha final del mese que se van a pintar los
	 * dias ' indica el tipo de semana que es la inicial del mes dado, la final,
	 * o alguna de enmedio 'FIRST_WEEK 'MIDDLE WEEK 'LAST_WEEK 'RETURN : NONE
	 * 'RESPONSABILITY : PINTA LOS DIAS DEL MES
	 */
	public void subPrintColDays(List<HashMap> bodyRow, String fecInicial, int iMes, int iWeek, String strBandWeek)
			throws Exception {

		String strFecIniWeek;
		String strFecFinWeek;

		long lngNoDays;

		Calendar calIni = Calendar.getInstance();
		Calendar calFin = Calendar.getInstance();

		strFecIniWeek = fecInicial;
		strFecFinWeek = subGetRangeOfWeek(strFecIniWeek, strBandWeek);

		calIni.setTime(getTypeDateFromStringFec(strFecIniWeek));
		calFin.setTime(getTypeDateFromStringFec(strFecFinWeek));

		lngNoDays = getDaysBetweenTwoDates(calIni, calFin);

		for (int iDay = 1; iDay <= lngNoDays; iDay++) {

			subPrintDatesOfColDays(bodyRow, strFecIniWeek, iMes, iWeek, iDay, lngNoDays, strBandWeek);

			calIni.add(Calendar.DATE, 1);
			strFecIniWeek = subFecFormatoString(calIni);
			lastCol += 1;

		}

	}
	// *****************************************************************************END

	/*
	 * 'AUTOR : JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subSetColControlDatesHeader 'PARAMS : NONE 'RETURN : NONE 'RESPONSABILITY
	 * : ESTABLECE LAS BANDERAS DE CONTROL EN LOS DIAS INHABILES EN LA FILA
	 * DEFINICA POR LA CONSTANTE BAN_INI_HOY_FIN
	 */

	public void subSetColControlDatesHeader(List<HashMap> bodyRow) {
		int iCol;
		int iCol2;

		String strValInCell;
		String strHabilInhabil;
		String strValInCell2;
		String strHabilInhabil2;

		HashMap hsmpFecIniFin = new HashMap<String, String>();
		HashMap hsmpFecHabil = new HashMap<String, String>();
		HashMap hsmpBanIniHoyFin = new HashMap<String, String>();

		hsmpFecIniFin = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_INI_FIN"));
		hsmpFecHabil = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_HABIL"));
		hsmpBanIniHoyFin = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"));

		Gson g = new Gson();

		// logger.info( g.toJson(hsmpFecIniFin) );
		// logger.info( g.toJson(hsmpFecHabil) );
		// logger.info( g.toJson(hsmpBanIniHoyFin) );

		for (iCol = 1; iCol < lastCol; iCol++) {

			strValInCell = hsmpFecIniFin.get("col" + iCol).toString();
			strHabilInhabil = hsmpFecHabil.get("col" + iCol).toString();

			if (strValInCell.equals("I") && strHabilInhabil.equals("N")) {

				for (iCol2 = iCol; iCol2 < lastCol; iCol2++) {
					strHabilInhabil = hsmpFecHabil.get("col" + iCol2).toString();

					if (strHabilInhabil.equals("S")) {

						if (hsmpFecIniFin.get("col" + iCol2).toString().equals("H")) {

							hsmpBanIniHoyFin.put("col" + iCol2, strValInCell + "_H");

						} else if (hsmpFecIniFin.get("col" + iCol2).toString().equals("-")) {

							hsmpBanIniHoyFin.put("col" + iCol2, strValInCell);

						}

						break;

					}

				}

			} // if( strValInCell.equals("I") && strValInCell.equals("n") )
			else if ((strValInCell.equals("I") || strValInCell.equals("H")) && strHabilInhabil.equals("S")) {

				// logger.info("ENTRA: " + strValInCell);

				if (hsmpBanIniHoyFin.get("col" + iCol) == null) {

					hsmpBanIniHoyFin.put("col" + iCol, strValInCell);

				} else {

					if (hsmpBanIniHoyFin.get("col" + iCol).toString().equals("")) {

						hsmpBanIniHoyFin.put("col" + iCol, strValInCell);
					}
				}

			} else if (strValInCell.equals("I_H") && strHabilInhabil.equals("S")) {

				hsmpBanIniHoyFin.put("col" + iCol, strValInCell);

			}

		} // for iCol < lastCol

		for (iCol = lastCol - 1; iCol > 1; iCol--) {
			strValInCell = hsmpFecIniFin.get("col" + iCol).toString();
			strHabilInhabil = hsmpFecHabil.get("col" + iCol).toString();

			if (strValInCell.equals("F") && strHabilInhabil.equals("N")) {

				for (iCol2 = iCol; iCol2 > 1; iCol2--) {
					strHabilInhabil = hsmpFecHabil.get("col" + iCol2).toString();

					if (strHabilInhabil.equals("S")) {

						if (hsmpFecIniFin.get("col" + iCol2).toString().equals("H")) {

							hsmpBanIniHoyFin.put("col" + iCol2, "H_" + strValInCell);

						} else if (hsmpFecIniFin.get("col" + iCol2).toString().equals("-")) {

							hsmpBanIniHoyFin.put("col" + iCol2, strValInCell);

						}

						break;

					}

				}

			} // if( strValInCell.equals("I") && strValInCell.equals("n") )
			else if ((strValInCell.equals("F") || strValInCell.equals("H")) && strHabilInhabil.equals("S")) {

				// logger.info("ENTRA: " + strValInCell);

				if (hsmpBanIniHoyFin.get("col" + iCol) == null) {

					hsmpBanIniHoyFin.put("col" + iCol, strValInCell);

				} else {

					if (hsmpBanIniHoyFin.get("col" + iCol).toString().equals("")) {

						hsmpBanIniHoyFin.put("col" + iCol, strValInCell);
					}
				}

			} else if (strValInCell.equals("H_F") && strHabilInhabil.equals("S")) {

				hsmpBanIniHoyFin.put("col" + iCol, strValInCell);
			}
		}

	}
	// *****************************************************************************END

	public void subAnalizeAndSetRowTypeInfo(List<HashMap> bodyRow) {

		Gson g = new Gson();
		HashMap hsmpBanIniHoyFin = new HashMap<String, String>();
		HashMap hsmpFechas = new HashMap<String, String>();

		String strValor;
		String strKey;
		int ntColumna;

		int iColIni = 0;
		int iColHoy = 0;
		int iColFin = 0;

		int iColIni2 = 0;
		int iColHoy2 = 0;
		int iColFin2 = 0;

		String sFecIni = "";
		String sFecHoy = "";
		String sFecFin = "";

		hsmpBanIniHoyFin = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"));
		hsmpFechas = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC"));

		Iterator it = hsmpBanIniHoyFin.entrySet().iterator();

		while (it.hasNext()) {

			Map.Entry e = (Map.Entry) it.next();
			strValor = e.getValue().toString();
			strKey = e.getKey().toString();
			ntColumna = Integer.parseInt(strKey.substring(3, strKey.length()));

			if (strValor.equals("")) {
				continue;
			}

			if (strValor.equals("I")) {
				if (iColIni == 0) {
					iColIni = ntColumna;
				} else {
					iColIni2 = ntColumna;

				}
				sFecIni = hsmpFechas.get("col" + ntColumna).toString();

			}

			if (strValor.equals("H")) {
				if (iColHoy == 0) {
					iColHoy = ntColumna;
				} else {
					iColHoy2 = ntColumna;

				}
				sFecHoy = hsmpFechas.get("col" + ntColumna).toString();

			}

			if (strValor.equals("F")) {

				if (iColFin == 0) {
					iColFin = ntColumna;
				} else {
					iColFin2 = ntColumna;

				}
				sFecFin = hsmpFechas.get("col" + ntColumna).toString();
			}

			if (strValor.equals("I_H")) {

				if (iColIni == 0 && iColHoy == 0) {
					iColIni = ntColumna;
					iColHoy = ntColumna;

				} else {
					iColIni2 = ntColumna;
					iColHoy2 = ntColumna;

				}

				sFecIni = hsmpFechas.get("col" + ntColumna).toString();
				sFecHoy = hsmpFechas.get("col" + ntColumna).toString();

			}

			if (strValor.equals("H_F")) {

				if (iColHoy == 0 && iColFin == 0) {
					iColHoy = ntColumna;
					iColFin = ntColumna;

				} else {
					iColHoy2 = ntColumna;
					iColFin2 = ntColumna;

				}

				sFecHoy = hsmpFechas.get("col" + ntColumna).toString();
				sFecFin = hsmpFechas.get("col" + ntColumna).toString();
			}

		} // while( it.hasNext() )

		// logger.info("1:" + sFecIni );
		// logger.info("2:" + sFecHoy );
		// logger.info("3:" + sFecFin );

		if (sFecHoy.equals(""))
			sFecHoy = FechaHoy;

		Calendar calFecIni = Calendar.getInstance();
		Calendar calFecHoy = Calendar.getInstance();
		Calendar calFecManana = Calendar.getInstance();
		Calendar calFecFin = Calendar.getInstance();

		calFecIni.setTime(getTypeDateFromStringFec(sFecIni));
		calFecHoy.setTime(getTypeDateFromStringFec(sFecHoy));
		calFecManana = calFecHoy;
		calFecManana.add(Calendar.DAY_OF_MONTH, 1);
		calFecFin.setTime(getTypeDateFromStringFec(sFecFin));

		HashMap hsmpStatusDay = new HashMap<String, String>();
		HashMap hsmpTypeInfo = new HashMap<String, String>();

		hsmpStatusDay = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_HABIL"));
		hsmpTypeInfo = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "TIPO_INFO"));

		int i = iColIni;
		String strStatus = "";
		/**********************************************************************************************************
		 * 
		 * 7 FLUJO DIARIO REAL
		 * 
		 **********************************************************************************************************/

		if (IdReporte == 1)// FLUJO DIARIO REAL
		{
			/*
			 * 'H-I-F: 'SI LA FECHA HOY ES MENOR A FECHA INICIAL Y FECHA FINAL
			 */
			if (calFecHoy.compareTo(calFecIni) < 0 && calFecHoy.compareTo(calFecFin) < 0) {

				for (i = iColIni; i <= iColFin; i++) {
					strStatus = hsmpStatusDay.get("col" + i).toString();

					if (strStatus.equals("S")) {
						hsmpTypeInfo.put("col" + i, "ajustado");

					}

				}

			}

			/*
			 * 'I-H-F: 'SI LA FECHA HOY ES MAYOR A FECHA INICIAL Y MENOR A FECHA
			 * FINAL
			 */
			if (calFecHoy.compareTo(calFecIni) > 0 && calFecHoy.compareTo(calFecFin) < 0) {

				for (i = iColIni; i <= iColHoy; i++) {
					strStatus = hsmpStatusDay.get("col" + i).toString();

					if (strStatus.equals("S")) {
						hsmpTypeInfo.put("col" + i, "real");

					}

				}

				for (i = iColHoy + 1; i <= iColFin; i++) {
					strStatus = hsmpStatusDay.get("col" + i).toString();

					if (strStatus.equals("S")) {
						hsmpTypeInfo.put("col" + i, "ajustado");

					}

				}

			}

			/*
			 * 'I-F-H 'SI LA FECHA HOY ES MAYOR A FECHA INICIAL Y MAYOR A FECHA
			 * FINAL
			 */

			if (calFecHoy.compareTo(calFecIni) > 0 && calFecHoy.compareTo(calFecFin) > 0) {

				for (i = iColIni; i <= iColFin; i++) {
					strStatus = hsmpStatusDay.get("col" + i).toString();

					if (strStatus.equals("S")) {
						hsmpTypeInfo.put("col" + i, "real");

					}

				}
			}

			/*
			 * 'I_H-F 'SI LA FECHA HOY ES IGUAL FECHA INICIAL Y MENOR A FECHA
			 * FINAL
			 */

			if (calFecHoy.compareTo(calFecIni) == 0 && calFecHoy.compareTo(calFecFin) < 0) {

				for (i = iColIni; i <= iColHoy; i++) {
					strStatus = hsmpStatusDay.get("col" + i).toString();

					if (strStatus.equals("S")) {
						hsmpTypeInfo.put("col" + i, "real");

					}

				}

				for (i = iColHoy + 1; i <= iColFin; i++) {
					strStatus = hsmpStatusDay.get("col" + i).toString();

					if (strStatus.equals("S")) {
						hsmpTypeInfo.put("col" + i, "ajustado");

					}

				}

			}

			/*
			 * 'I-H_F 'SI LA FECHA HOY ES MAYOR A FECHA INICIAL E IGUAL A FECHA
			 * FINAL
			 */
			if (calFecHoy.compareTo(calFecIni) > 0 && calFecHoy.compareTo(calFecFin) == 0) {

				for (i = iColIni; i <= iColFin; i++) {
					strStatus = hsmpStatusDay.get("col" + i).toString();

					if (strStatus.equals("S")) {
						hsmpTypeInfo.put("col" + i, "real");

					}

				}
			}

		}
		/**********************************************************************************************************
		 * 
		 * FLUJO DIARIO REAL
		 * 
		 **********************************************************************************************************/

		/**********************************************************************************************************
		 * 
		 * MENSUAL AJUSTADO
		 * 
		 *********************************************************************************************************/
		if (IdReporte == 2)// MENSUAL AJUSTADO
		{

			for (i = iColIni; i <= iColFin; i++) {
				strStatus = hsmpStatusDay.get("col" + i).toString();

				if (strStatus.equals("S")) {
					hsmpTypeInfo.put("col" + i, "ajustado");

				}

			}

		}
		/**********************************************************************************************************
		 * 
		 * MENSUAL AJUSTADO
		 * 
		 *********************************************************************************************************/

		/**********************************************************************************************************
		 * 
		 * COMPARATIVO MENSUAL ORIGINAL AJUSTADO
		 * 
		 *********************************************************************************************************/
		if (IdReporte == 3)// COMPARATIVO MENSUAL ORIGINAL AJUSTADO
		{
			int iColIniAux;
			int iColFinAux;

			if (iColIni > 0 && iColIni2 > 0 && iColFin > 0 && iColFin2 > 0) {
				if (iColFin2 < iColFin) {

					iColFinAux = iColFin2;
					iColFin2 = iColFin;
					iColFin = iColFinAux;

				}

				if (iColIni2 < iColIni) {

					iColIniAux = iColIni2;
					iColIni2 = iColIni;
					iColIni = iColIniAux;

				}

				for (i = iColIni; i <= iColFin; i++) {
					strStatus = hsmpStatusDay.get("col" + i).toString();

					if (strStatus.equals("S")) {
						hsmpTypeInfo.put("col" + i, "original");

					}

				}

				for (i = iColIni2; i <= iColFin2; i++) {
					strStatus = hsmpStatusDay.get("col" + i).toString();

					if (strStatus.equals("S")) {
						hsmpTypeInfo.put("col" + i, "ajustado");

					}

				}

			} // End if

		}
		/**********************************************************************************************************
		 * 
		 * COMPARATIVO MENSUAL ORIGINAL AJUSTADO
		 * 
		 *********************************************************************************************************/

		/**********************************************************************************************************
		 * 
		 * COMPARATIVO MENSUAL REAL AJUSTADO
		 * 
		 *********************************************************************************************************/
		if (IdReporte == 5)// COMPARATIVO MENSUAL ORIGINAL AJUSTADO
		{
			int iColIniAux;
			int iColFinAux;

			if (iColIni > 0 && iColIni2 > 0 && iColFin > 0 && iColFin2 > 0) {
				if (iColFin2 < iColFin) {

					iColFinAux = iColFin2;
					iColFin2 = iColFin;
					iColFin = iColFinAux;

				}

				if (iColIni2 < iColIni) {

					iColIniAux = iColIni2;
					iColIni2 = iColIni;
					iColIni = iColIniAux;

				}

				for (i = iColIni; i <= iColFin; i++) {
					strStatus = hsmpStatusDay.get("col" + i).toString();

					if (strStatus.equals("S")) {
						hsmpTypeInfo.put("col" + i, "real");

					}

				}

				for (i = iColIni2; i <= iColFin2; i++) {
					strStatus = hsmpStatusDay.get("col" + i).toString();

					if (strStatus.equals("S")) {
						hsmpTypeInfo.put("col" + i, "ajustado");

					}

				}

			} // End if

		}
		/**********************************************************************************************************
		 * 
		 * COMPARATIVO MENSUAL ORIGINAL AJUSTADO
		 * 
		 **********************************************************************************************************/

		FechaInicial = sFecIni;
		FechaFinal = sFecFin;

		Integer nTempDia;
		Integer nTempMes;
		Integer nTempAnio;

		String sTempDia;
		String sTempMes;
		String sTempAnio;

		nTempDia = calFecManana.get(Calendar.DAY_OF_MONTH);
		nTempMes = calFecManana.get(Calendar.MONTH) + 1;
		nTempAnio = calFecManana.get(Calendar.YEAR);
		sTempDia = nTempDia < 10 ? "0" + nTempDia.toString() : nTempDia.toString();
		sTempMes = nTempMes < 10 ? "0" + nTempMes.toString() : nTempMes.toString();
		sTempAnio = nTempAnio.toString();
		FechaManana = sTempDia + "/" + sTempMes + "/" + sTempAnio;

	}

	// *****************************************************************************END

	/*
	 * 
	 * 
	 * -------------------------------------------------------------------------
	 * -----------------------------------------------------------------------
	 * -------------------------------------------------------------------------
	 * -----------------------------------------------------------------------
	 * FUNCIONES UTILITARIAS DE SOPORTE PARA LA CONSTRUCCION DEL BODY
	 * -------------------------------------------------------------------------
	 * -----
	 * -------------------------------------------------------------------------
	 * -----------------------------------------------------------------------
	 * -------------------------------------------------------------------------
	 * -----------------------------------------------------------------------
	 * 
	 * 
	 */

	/*****************************************************************************
	 * INIT AUTOR : JUAN RAMIRO BARRERA MARTINEZ NAME : getBodyReportBusiness
	 * PARAMS : paramIdGrupo -> GRUPO DE FLUJO : paramNoEmpresa -> EMPRESA DEL
	 * GRUPO O TODAS : paramFechaInicial-> FECHA INICIAL DE EMISION DEL REPORTE
	 * : paramagFechaFinal-> FECHA FINAL DE EMISION DEL REPORTE : paramIdReporte
	 * -> TIPO DE REPORTE SELECCIONADO RETURN : COLUMNAS PARA LA CABECERA
	 * RESPONSABILITY : PUNTO DE CONTROL PARA DETERMINAR Y RETORNAR UNA
	 * ESTRUCTURA QUE DEFINE LAS FILAS A ESTABLECER EN EL CUERPO DEL REPORTE EN
	 * BASE A LA SELECCION DE UN REPORTE DADO INIT
	 */

	public ARowReporteDto getBodyReportBusiness(int paramIdGrupo, int paramNoEmpresa, String paramFechaInicial,
			String paramFechaFinal, int paramIdReporte) {
		long ntNumMonths;
		ntNumMonths = getMonthsBetweenTwoDates(paramFechaInicial, paramFechaFinal);
		ARowReporteDto body;

		initFields(paramIdGrupo, paramNoEmpresa, paramFechaInicial, paramFechaFinal, paramIdReporte);

		switch (paramIdReporte) {
		case 1: /* DIARIO REAL */ 
			body = new ARowReporteDto(setBodyMensualOriginal());
			return body;
		case 2: /* MENSUAL AJUSTADO */
			body = new ARowReporteDto(setBodyMensualAjustado());
			return body;
		case 3: /* MENSUAL COMPARATIVO ORIGINAL AJUSTADO */
			body = new ARowReporteDto(setBodyMensualComparativoOriginalAjustado());
			return body;
		case 4: /* MENSUAL REAL AJUSTADO */
			body = new ARowReporteDto(setBodyMensualOriginalAjustado());
			return body;
		case 5: /* MENSUAL COMPARATIVO REAL AJUSTADO */
			body = new ARowReporteDto(setBodyMensualComparativoRealAjustado());
			return body;
		case 6: /* MENSUAL COMPARATIVO REAL AJUSTADO */
			body = new ARowReporteDto(setBodyMensualOriginalAjustadoReal());
			return body;
		case 7: /* SEMANAL REAL AJUSTADO */
			body = new ARowReporteDto(setBodySemanalRealAjustado());
			return body; 
		case 8: /* SEMANAL REAL AJUSTADO */
			body = new ARowReporteDto(prueba(ntNumMonths));
			return body;

		}

		return null;
	}

	/*
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++
	 ***************************************************************************************************************************************************** 
	 *****************************************************************************************************************************************************
	 **************************************************** CONSTRUCCION DE DIARIO REAL
	 * AJUSTADO************************************************************
	 *****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++
	 */

	/*
	 * AUTOR : JUAN RAMIRO BARRERA MARTINEZ NAME : setBodyDiarioReal PARAMS :
	 * NONE RETURN : LISTA QUE CONFORMA EL CUERPO DEL REPORTE RESPONSABILITY :
	 * DETERMINAR EL CUERPO DEL REPORTE es para el real diario por dia
	 */
	// *****************************************************************************INIT
	public List<HashMap> setBodyDiarioReal() {

		List<HashMap> bodyRow = new ArrayList<HashMap>();

		// '1.-INICIALIZAMOS EL GRID DONDE SE PRESENTA EL REPORTE
		subPrintHeaderOfReport(bodyRow);

		// '2.-CREACION DEL CUERPO E IMPRESION DE LOS ENCABEZADOS DE
		// PRESENTACION Y DE CONTROL
		subPrintColsPeriodoOfGrid(bodyRow);

		// '3.-ESTABLECE LAS FECHAS INICIAL Y FINAL DEL PERIODO EN EL GRID
		subSetColControlDatesHeader(bodyRow);

		// '4.-DETERMINA Y ESTABLECE SI LA INFORMACION ES REAL O AJUSTADA U
		// ORIGINAL ===D MODIFICAR
		subAnalizeAndSetRowTypeInfo(bodyRow);

		// '5.-EJECUCION DEL STORE PARA PREPARACION DE LA INFORMACION C=== CREAR
		subPrepareInformationforFlujoRealAjustado();

		// '6.-PUNTO DE INICIO PARA IMPRIMIR LA INFORMACION DE LA COLUMNA DE
		// CONCEPTOS
		subPrintTitulosTiposConceptos(bodyRow);

		// 7.-IMPRIME CEROS EN EL GRID DEL REPORTE
		subPrintCerosinGrid(bodyRow);

		// 8.-ESTABLECEMOS EL SALDO INICIAL PARA DIA SEMANA MES PERIODO
		subSetSaldoInicialDiarioSemanalMensualPeriodo(bodyRow);

		// 9.- PUNTO DE CONTROL PARA IMPRESION DE INFORMACION DIARIA
		subControlTotalesDiario(bodyRow);

		// 10.- CICLO PARA DETERMINAR SALDO INICIAL Y SALDO FINAL EN LA
		// INFORMACION DIARIA
		// subSaldoInicialFinalDiarioNext(bodyRow);

		// 11.- PUNTO DE CONTROL PARA IMPRESION DE INFORMACION SEMANAL
		// subPrintTotalesXWeeks(bodyRow);

		// 12.- CICLO PARA DETERMINAR SALDO INICIAL Y SALDO FINAL EN LA
		// INFORMACION SEMANAL
		// subSaldoInicialFinalSemanalNext(bodyRow);

		// '13.- PUNTO DE CONTROL PARA IMPRESION DE INFORMACION MENSUAL
		// subPrintTotalesXMonths(bodyRow);

		// 14.- CICLO PARA DETERMINAR SALDO INICIAL Y SALDO FINAL EN LA
		// INFORMACION MENSUAL
		// subSaldoInicialFinalMensualNext(bodyRow);

		// 15.- PUNTO DE CONTROL PARA IMPRESION DE INFORMACION POR PERIODO
		// subPrintControlTotalesPeriodo(bodyRow);

		// 16.- CICLO PARA DETERMINAR SALDO INICIAL Y SALDO FINAL EN LA
		// INFORMACION POR PERIODO
		// subSaldoInicialFinalPeriodoNext(bodyRow);

		// 17.- OCULTAMOS LAS COLUMNAS Y FILAS DE CONTROL
		subOcultaColsAndRowsDiarioRealAjustado(bodyRow);

		Gson g = new Gson();
		// logger.info( "CUERPO DIARIO REAL AJUSTADO: " + g.toJson(bodyRow) ) ;

		return bodyRow;

	}
	// *****************************************************************************END

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 5 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subPrepareInformationforFlujo 'PARAMS : NONE 'RETURN : NONE
	 * 'RESPONSABILITY : PUNTO DE LLAMADA PARA EL STORE QUE PREPARA LA
	 * INFORMACION EN UNA TABLA TEMPORAL POR USUARIO
	 * '************************************************************************
	 * *******************************************
	 */
	public void subPrepareInformationforFlujoRealAjustado() {

		ParamSpFlujoDto obj = new ParamSpFlujoDto(IdGrupo, NoEmpresa, FechaInicial, FechaHoy, FechaManana, FechaFinal,
				IdReporte, IdDivisa, NoUsuario);

		Calendar calFecIni = Calendar.getInstance();
		Calendar calFecHoy = Calendar.getInstance();
		Calendar calFecFin = Calendar.getInstance();

		calFecIni.setTime(getTypeDateFromStringFec(obj.getFechaInicial()));
		calFecHoy.setTime(getTypeDateFromStringFec(obj.getFechaHoy()));
		calFecFin.setTime(getTypeDateFromStringFec(obj.getFechaFinal()));

		/*
		 * 'I-H-F: 'SI LA FECHA HOY ES MAYOR A FECHA INICIAL Y MENOR A FECHA
		 * FINAL
		 */
		/*
		 * if( calFecHoy.compareTo( calFecIni ) > 0 && calFecHoy.compareTo(
		 * calFecFin ) < 0 ) { obj.setOpcion( 1 ); }
		 */
		/*
		 * 'H-I-F: 'SI LA FECHA HOY ES MENOR A FECHA INICIAL Y FECHA FINAL
		 *//*
			 * if( calFecHoy.compareTo( calFecIni ) < 0 && calFecHoy.compareTo(
			 * calFecFin ) < 0 ) { obj.setOpcion( 2 ); }
			 */
		/*
		 * 'I-F-H 'SI LA FECHA HOY ES MAYOR A FECHA INICIAL Y MAYOR A FECHA
		 * FINAL
		 */
		/*
		 * if( calFecHoy.compareTo( calFecIni ) > 0 && calFecHoy.compareTo(
		 * calFecFin ) > 0 ) { obj.setOpcion( 3 ); }
		 */
		/*
		 * 'I_H-F 'SI LA FECHA HOY ES IGUAL FECHA INICIAL Y MENOR A FECHA FINAL
		 */
		/*
		 * if( calFecHoy.compareTo( calFecIni ) == 0 && calFecHoy.compareTo(
		 * calFecFin ) < 0 ) { obj.setOpcion( 4 ); }
		 */

		/*
		 * 'I-H_F 'SI LA FECHA HOY ES MAYOR A FECHA INICIAL E IGUAL A FECHA
		 * FINAL
		 */
		/*
		 * if( calFecHoy.compareTo( calFecIni ) > 0 && calFecHoy.compareTo(
		 * calFecFin ) == 0 ) { obj.setOpcion( 5 ); }
		 */

		int result = cashFlowDao.spFlujoDiarioRealAjustadoDao(obj);

		obj.setOpcion(0);

		// logger.info( "RESULTADO " + result );

	}

	// *****************************************************************************END
	public void subPrepareInformationforFlujoDiario() {

		ParamSpFlujoDto obj = new ParamSpFlujoDto(IdGrupo, NoEmpresa, FechaInicial, FechaHoy, FechaManana, FechaFinal,
				IdReporte, IdDivisa, NoUsuario);

		Calendar calFecIni = Calendar.getInstance();
		Calendar calFecHoy = Calendar.getInstance();
		Calendar calFecFin = Calendar.getInstance();

		calFecIni.setTime(getTypeDateFromStringFec(obj.getFechaInicial()));
		calFecHoy.setTime(getTypeDateFromStringFec(obj.getFechaHoy()));
		calFecFin.setTime(getTypeDateFromStringFec(obj.getFechaFinal()));

		/*
		 * 'I-H-F: 'SI LA FECHA HOY ES MAYOR A FECHA INICIAL Y MENOR A FECHA
		 * FINAL
		 */
		/*
		 * if( calFecHoy.compareTo( calFecIni ) > 0 && calFecHoy.compareTo(
		 * calFecFin ) < 0 ) { obj.setOpcion( 1 ); }
		 */
		/*
		 * 'H-I-F: 'SI LA FECHA HOY ES MENOR A FECHA INICIAL Y FECHA FINAL
		 *//*
			 * if( calFecHoy.compareTo( calFecIni ) < 0 && calFecHoy.compareTo(
			 * calFecFin ) < 0 ) { obj.setOpcion( 2 ); }
			 */
		/*
		 * 'I-F-H 'SI LA FECHA HOY ES MAYOR A FECHA INICIAL Y MAYOR A FECHA
		 * FINAL
		 */
		/*
		 * if( calFecHoy.compareTo( calFecIni ) > 0 && calFecHoy.compareTo(
		 * calFecFin ) > 0 ) { obj.setOpcion( 3 ); }
		 */
		/*
		 * 'I_H-F 'SI LA FECHA HOY ES IGUAL FECHA INICIAL Y MENOR A FECHA FINAL
		 */
		/*
		 * if( calFecHoy.compareTo( calFecIni ) == 0 && calFecHoy.compareTo(
		 * calFecFin ) < 0 ) { obj.setOpcion( 4 ); }
		 */

		/*
		 * 'I-H_F 'SI LA FECHA HOY ES MAYOR A FECHA INICIAL E IGUAL A FECHA
		 * FINAL
		 */
		/*
		 * if( calFecHoy.compareTo( calFecIni ) > 0 && calFecHoy.compareTo(
		 * calFecFin ) == 0 ) { obj.setOpcion( 5 ); }
		 */

		int result = cashFlowDao.spFlujoDiarioRealDiarioDao(obj);

		obj.setOpcion(0);

		// logger.info( "RESULTADO " + result );

	}

	/* Hector */
	public void subPrepareInformationforFlujoMesualRealAjustado() {

		ParamSpFlujoDto obj = new ParamSpFlujoDto(IdGrupo, NoEmpresa, FechaInicial, FechaHoy, FechaManana, FechaFinal,
				IdReporte, IdDivisa, NoUsuario);

		Calendar calFecIni = Calendar.getInstance();
		Calendar calFecHoy = Calendar.getInstance();
		Calendar calFecFin = Calendar.getInstance();

		calFecIni.setTime(getTypeDateFromStringFec(obj.getFechaInicial()));
		calFecHoy.setTime(getTypeDateFromStringFec(obj.getFechaHoy()));
		calFecFin.setTime(getTypeDateFromStringFec(obj.getFechaFinal()));

		/*
		 * 'I-H-F: 'SI LA FECHA HOY ES MAYOR A FECHA INICIAL Y MENOR A FECHA
		 * FINAL
		 */

		if (calFecHoy.compareTo(calFecIni) > 0 && calFecHoy.compareTo(calFecFin) < 0) {
			obj.setOpcion(1);
		}

		/*
		 * 'H-I-F: 'SI LA FECHA HOY ES MENOR A FECHA INICIAL Y FECHA FINAL
		 */
		if (calFecHoy.compareTo(calFecIni) < 0 && calFecHoy.compareTo(calFecFin) < 0) {
			obj.setOpcion(2);
		}

		/*
		 * 'I-F-H 'SI LA FECHA HOY ES MAYOR A FECHA INICIAL Y MAYOR A FECHA
		 * FINAL
		 */

		if (calFecHoy.compareTo(calFecIni) > 0 && calFecHoy.compareTo(calFecFin) > 0) {
			obj.setOpcion(3);
		}

		/*
		 * 'I_H-F 'SI LA FECHA HOY ES IGUAL FECHA INICIAL Y MENOR A FECHA FINAL
		 */

		if (calFecHoy.compareTo(calFecIni) == 0 && calFecHoy.compareTo(calFecFin) < 0) {
			obj.setOpcion(4);
		}

		/*
		 * 'I-H_F 'SI LA FECHA HOY ES MAYOR A FECHA INICIAL E IGUAL A FECHA
		 * FINAL
		 */

		if (calFecHoy.compareTo(calFecIni) > 0 && calFecHoy.compareTo(calFecFin) == 0) {
			obj.setOpcion(5);
		}

		int result = cashFlowDao.spFlujoMesualRealAjustadoDao(obj);

		obj.setOpcion(0);

		// logger.info( "RESULTADO " + result );

	}

	private void subOcultaColsAndRowsDiarioRealAjustado(List<HashMap> bodyRow) {

		String esHabil;
		HashMap hsmpFecHabil = new HashMap<String, String>();
		int iRowFecHabil = getRowOfIdHeaderDates(bodyRow, "FEC_HABIL");
		hsmpFecHabil = bodyRow.get(iRowFecHabil);

		HashMap hsmpTemp = new HashMap<String, String>();

		for (int iRow = 1; iRow < bodyRow.size(); iRow++) {

			if (iRow == iRowFecHabil)
				continue;

			hsmpTemp = bodyRow.get(iRow);

			for (int iCol = 1; iCol < lastCol; iCol++) {

				esHabil = hsmpFecHabil.get("col" + iCol).toString();

				if (esHabil.equals("N") || esHabil.equals("O")) {

					hsmpTemp.remove("col" + iCol);

				}

			}

		}

		bodyRow.remove(getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA"));
		bodyRow.remove(getRowOfIdHeaderDates(bodyRow, "FEC_DIA"));
		bodyRow.remove(getRowOfIdHeaderDates(bodyRow, "FEC_MES"));
		bodyRow.remove(getRowOfIdHeaderDates(bodyRow, "FEC_NO_MES"));
		bodyRow.remove(getRowOfIdHeaderDates(bodyRow, "FEC_ANIO"));
		bodyRow.remove(getRowOfIdHeaderDates(bodyRow, "IS_FS"));
		bodyRow.remove(getRowOfIdHeaderDates(bodyRow, "IM_FM"));
		bodyRow.remove(getRowOfIdHeaderDates(bodyRow, "FEC_INI_FIN"));
		bodyRow.remove(getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"));
		// bodyRow.remove( getRowOfIdHeaderDates( bodyRow, "FEC_HABIL") );

	}

	/*----------------------------------------------------------------------------------------------------------------------------------------------------
	 ***************************************************************************************************************************************************** 
	 *****************************************************************************************************************************************************
	 ****************************************************CONSTRUCCION DE DIARIO REAL AJUSTADO************************************************************
	 *****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************
	 *----------------------------------------------------------------------------------------------------------------------------------------------------*/

	/*
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++
	 ***************************************************************************************************************************************************** 
	 *****************************************************************************************************************************************************
	 **************************************************** CONSTRUCCION DE MENSUAL
	 * AJUSTADO************************************************************
	 *****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++
	 */
	private List<HashMap> setBodyMensualAjustado() {

		List<HashMap> bodyRow = new ArrayList<HashMap>();

		// '1.-INICIALIZAMOS EL GRID DONDE SE PRESENTA EL REPORTE
		subPrintHeaderOfReport(bodyRow);

		// '2.-CREACION DEL CUERPO E IMPRESION DE LOS ENCABEZADOS DE
		// PRESENTACION Y DE CONTROL
		subPrintColsPeriodoOfGrid(bodyRow);

		// '3.-ESTABLECE LAS FECHAS INICIAL Y FINAL DEL PERIODO EN EL GRID
		subSetColControlDatesHeader(bodyRow);

		// '4.-DETERMINA Y ESTABLECE SI LA INFORMACION ES REAL AJUSTADA ORIGINAL
		// ===D MODIFICAR
		subAnalizeAndSetRowTypeInfo(bodyRow);

		// '6.-EJECUCION DEL STORE PARA PREPARACION DE LA INFORMACION C=== CREAR
		subPrepareInformationforFlujoMensualAjustado();

		// '7.-PUNTO DE INICIO PARA IMPRIMIR LA INFORMACION DE LA COLUMNA DE
		// CONCEPTOS
		subPrintTitulosTiposConceptosAjustado(bodyRow);

		// 8.-IMPRIME CEROS EN EL GRID DEL REPORTE
		subPrintCerosinGrid(bodyRow);

		// 9.-ESTABLECEMOS EL SALDO INICIAL PARA DIA SEMANA MES PERIODO ===D
		// MODIFICAR
		subSetSaldoInicialDiarioSemanalMensualPeriodo(bodyRow);

		// '10.- PUNTO DE CONTROL PARA IMPRESION DE INFORMACION MENSUAL
		subPrintTotalesXMonthsAjustado(bodyRow);

		// 11.- CICLO PARA DETERMINAR SALDO INICIAL Y SALDO FINAL EN LA
		// INFORMACION MENSUAL
		subSaldoInicialFinalMensualNext(bodyRow);

		// 12.- PUNTO DE CONTROL PARA IMPRESION DE INFORMACION POR PERIODO
		subPrintControlTotalesPeriodoAjustado(bodyRow);

		// 13.- CICLO PARA DETERMINAR SALDO INICIAL Y SALDO FINAL EN LA
		// INFORMACION POR PERIODO
		subSaldoInicialFinalPeriodoNext(bodyRow);

		// 14.- OCULTAMOS LAS COLUMNAS Y FILAS DE CONTROL
		subOcultaColsAndRowsMensualAjustado(bodyRow);

		Gson g = new Gson();
		// logger.info( "MENSUAL AJUSTADO: " + g.toJson(bodyRow) ) ;
		return bodyRow;

	}

	// *****************************************************************************END
	private List<HashMap> setBodyMensualOriginal() {

		List<HashMap> bodyRow = new ArrayList<HashMap>();

		// '1.-INICIALIZAMOS EL GRID DONDE SE PRESENTA EL REPORTE
		subPrintHeaderOfReport(bodyRow);

		// '2.-CREACION DEL CUERPO E IMPRESION DE LOS ENCABEZADOS DE
		// PRESENTACION Y DE CONTROL
		subPrintColsPeriodoOfGrid(bodyRow);

		// '3.-ESTABLECE LAS FECHAS INICIAL Y FINAL DEL PERIODO EN EL GRID
		subSetColControlDatesHeader(bodyRow);

		// '4.-DETERMINA Y ESTABLECE SI LA INFORMACION ES REAL AJUSTADA ORIGINAL
		// ===D MODIFICAR
		subAnalizeAndSetRowTypeInfo(bodyRow);

		// '6.-EJECUCION DEL STORE PARA PREPARACION DE LA INFORMACION C=== CREAR
		subPrepareInformationforFlujoMensualOriginal();

		// '7.-PUNTO DE INICIO PARA IMPRIMIR LA INFORMACION DE LA COLUMNA DE
		// CONCEPTOS
		subPrintTitulosTiposConceptosOriginal(bodyRow);

		// 8.-IMPRIME CEROS EN EL GRID DEL REPORTE
		subPrintCerosinGrid(bodyRow);

		// 9.-ESTABLECEMOS EL SALDO INICIAL PARA DIA SEMANA MES PERIODO ===D
		// MODIFICAR
		subSetSaldoInicialDiarioSemanalMensualPeriodo(bodyRow);

		// '10.- PUNTO DE CONTROL PARA IMPRESION DE INFORMACION MENSUAL
		subPrintTotalesXMonthsOriginal(bodyRow);

		// 11.- CICLO PARA DETERMINAR SALDO INICIAL Y SALDO FINAL EN LA
		// INFORMACION MENSUAL
		subSaldoInicialFinalMensualNext(bodyRow);

		// 12.- PUNTO DE CONTROL PARA IMPRESION DE INFORMACION POR PERIODO
		subPrintControlTotalesPeriodoOriginal(bodyRow);

		// 13.- CICLO PARA DETERMINAR SALDO INICIAL Y SALDO FINAL EN LA
		// INFORMACION POR PERIODO
		subSaldoInicialFinalPeriodoNext(bodyRow);

		// 14.- OCULTAMOS LAS COLUMNAS Y FILAS DE CONTROL
		subOcultaColsAndRowsMensualAjustado(bodyRow);

		Gson g = new Gson();
		// logger.info( "MENSUAL AJUSTADO: " + g.toJson(bodyRow) ) ;
		return bodyRow;

	}
	// *****************************************************************************END

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 5 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subPrepareInformationforFlujo 'PARAMS : NONE 'RETURN : NONE
	 * 'RESPONSABILITY : PUNTO DE LLAMADA PARA EL STORE QUE PREPARA LA
	 * INFORMACION EN UNA TABLA TEMPORAL POR USUARIO
	 * '************************************************************************
	 * *******************************************
	 */
	public void subPrepareInformationforFlujoMensualAjustado() {

		ParamSpFlujoDto obj = new ParamSpFlujoDto(IdGrupo, NoEmpresa, FechaInicial, FechaHoy, FechaManana, FechaFinal,
				IdReporte, IdDivisa, NoUsuario);

		int result = cashFlowDao.spFlujoMensualAjustadoDao(obj);

		obj.setOpcion(0);

		// logger.info( "RESULTADO " + result );

	}

	public void subPrepareInformationforFlujoMensualOriginal() {

		ParamSpFlujoDto obj = new ParamSpFlujoDto(IdGrupo, NoEmpresa, FechaInicial, FechaHoy, FechaManana, FechaFinal,
				IdReporte, IdDivisa, NoUsuario);

		int result = cashFlowDao.spFlujoMensualOriginalDao(obj);

		obj.setOpcion(0);

		// logger.info( "RESULTADO " + result );

	}
	// *****************************************************************************END

	private void subOcultaColsAndRowsMensualAjustado(List<HashMap> bodyRow) {

		String esHabil;
		HashMap hsmpFecHabil = new HashMap<String, String>();
		int iRowFecHabil = getRowOfIdHeaderDates(bodyRow, "FEC_HABIL");
		hsmpFecHabil = bodyRow.get(iRowFecHabil);

		HashMap hsmpTemp = new HashMap<String, String>();

		for (int iRow = 1; iRow < bodyRow.size(); iRow++) {

			if (iRow == iRowFecHabil)
				continue;

			hsmpTemp = bodyRow.get(iRow);

			for (int iCol = 1; iCol < lastCol; iCol++) {

				esHabil = hsmpFecHabil.get("col" + iCol).toString();

				if (esHabil.equals("N") || esHabil.equals("O")) {

					hsmpTemp.remove("col" + iCol);

				}

			}

		}

		// bodyRow.remove( getRowOfIdHeaderDates( bodyRow, "FEC_NO_DIA") );
		bodyRow.remove(getRowOfIdHeaderDates(bodyRow, "FEC_DIA"));
		bodyRow.remove(getRowOfIdHeaderDates(bodyRow, "FEC_MES"));
		bodyRow.remove(getRowOfIdHeaderDates(bodyRow, "FEC_NO_MES"));
		bodyRow.remove(getRowOfIdHeaderDates(bodyRow, "FEC_ANIO"));
		bodyRow.remove(getRowOfIdHeaderDates(bodyRow, "IS_FS"));
		bodyRow.remove(getRowOfIdHeaderDates(bodyRow, "IM_FM"));
		bodyRow.remove(getRowOfIdHeaderDates(bodyRow, "FEC_INI_FIN"));
		bodyRow.remove(getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN"));
		bodyRow.remove(getRowOfIdHeaderDates(bodyRow, "FEC_HABIL"));
		bodyRow.remove(getRowOfIdHeaderDates(bodyRow, "TIPO_INFO"));

	}

	/*----------------------------------------------------------------------------------------------------------------------------------------------------
	 ***************************************************************************************************************************************************** 
	 *****************************************************************************************************************************************************
	 ****************************************************CONSTRUCCION DE MENSUAL AJUSTADO************************************************************
	 *****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************
	 *----------------------------------------------------------------------------------------------------------------------------------------------------*/

	/*
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++
	 ***************************************************************************************************************************************************** 
	 *****************************************************************************************************************************************************
	 **************************************************** CONSTRUCCION DE MENSUAL COMPARATIVO ORIGINAL /
	 * AJUSTADO************************************************************
	 *****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++
	 */
	private List<HashMap> setBodyMensualComparativoOriginalAjustado() {

		List<HashMap> bodyRow = new ArrayList<HashMap>();

		// '1.-INICIALIZAMOS EL GRID DONDE SE PRESENTA EL REPORTE
		subPrintHeaderOfReport(bodyRow);

		// '2.-CREACION DEL CUERPO E IMPRESION DE LOS ENCABEZADOS DE
		// PRESENTACION Y DE CONTROL
		subPrintColsPeriodoForComparativos(bodyRow, 2);

		// '3.-ESTABLECE LAS FECHAS INICIAL Y FINAL DEL PERIODO EN EL GRID
		subSetColControlDatesHeader(bodyRow);

		// '4.-DETERMINA Y ESTABLECE SI LA INFORMACION ES REAL AJUSTADA ORIGINAL
		// ===D MODIFICAR
		subAnalizeAndSetRowTypeInfo(bodyRow);

		// '6.-EJECUCION DEL STORE PARA PREPARACION DE LA INFORMACION C=== CREAR
		subPrepareInformationforFlujoComparativoMensualOriginalAjustado();

		// '7.-PUNTO DE INICIO PARA IMPRIMIR LA INFORMACION DE LA COLUMNA DE
		// CONCEPTOS
		subPrintTitulosTiposConceptosOriginalAjustado(bodyRow);

		// 8.-IMPRIME CEROS EN EL GRID DEL REPORTE
		subPrintCerosinGrid(bodyRow);

		// 9.-ESTABLECEMOS EL SALDO INICIAL PARA DIA SEMANA MES PERIODO ===D
		// MODIFICAR
		subSetSaldoInicialDiarioSemanalMensualPeriodoComparativos(bodyRow);

		// '10.- PUNTO DE CONTROL PARA IMPRESION DE INFORMACION MENSUAL
		subPrintTotalesXMonthsComparativosOriginalAjustado(bodyRow);

		// 11.- CICLO PARA DETERMINAR SALDO INICIAL Y SALDO FINAL EN LA
		// INFORMACION MENSUAL
		subSaldoInicialFinalMensualNextComparativo(bodyRow);

		// 12.- PUNTO DE CONTROL PARA IMPRESION DE INFORMACION POR PERIODO
		subPrintControlTotalesPeriodoComparativoOriginalAjustado(bodyRow);

		// 13.- CICLO PARA DETERMINAR SALDO INICIAL Y SALDO FINAL EN LA
		// INFORMACION POR PERIODO
		subSaldoInicialFinalPeriodoNext(bodyRow);

		// 14.- OCULTAMOS LAS COLUMNAS Y FILAS DE CONTROL
		subOcultaColsAndRowsMensualAjustado(bodyRow);

		Gson g = new Gson();
		// logger.info( "MENSUAL AJUSTADO: " + g.toJson(bodyRow) ) ;
		return bodyRow;

	}
	
	
	private List<HashMap> prueba( long meses) {

		List<HashMap> bodyRow = new ArrayList<HashMap>();

		// '1.-INICIALIZAMOS EL GRID DONDE SE PRESENTA EL REPORTE
				subPrintHeaderOfReport(bodyRow);

				// '2.-CREACION DEL CUERPO E IMPRESION DE LOS ENCABEZADOS DE
				// PRESENTACION Y DE CONTROL 
				subPrintColsPeriodoForComparativosPrueba(bodyRow, 2);
				
				// '3.-ESTABLECE LAS FECHAS INICIAL Y FINAL DEL PERIODO EN EL GRID
				subSetColControlDatesHeader(bodyRow);

				// '4.-DETERMINA Y ESTABLECE SI LA INFORMACION ES REAL O AJUSTADA U
				// ORIGINAL ===D MODIFICAR
				subAnalizeAndSetRowTypeInfo(bodyRow);

				// '5.-EJECUCION DEL STORE PARA PREPARACION DE LA INFORMACION C=== CREAR
				subPrepareInformationforFlujoRealAjustado();

				// '6.-PUNTO DE INICIO PARA IMPRIMIR LA INFORMACION DE LA COLUMNA DE
				// CONCEPTOS
				subPrintTitulosTiposConceptos(bodyRow);

				// 7.-IMPRIME CEROS EN EL GRID DEL REPORTE
				subPrintCerosinGrid(bodyRow);

				subSetSaldoInicialDiarioSemanalMensualPeriodoPrueba(bodyRow,meses);

				// 9.- PUNTO DE CONTROL PARA IMPRESION DE INFORMACION DIARIA
//				subControlTotalesDiario(bodyRow);

				// 10.- CICLO PARA DETERMINAR SALDO INICIAL Y SALDO FINAL EN LA
				// INFORMACION DIARIA
//				subSaldoInicialFinalDiarioNext(bodyRow);

				// 11.- PUNTO DE CONTROL PARA IMPRESION DE INFORMACION SEMANAL
//				subPrintTotalesXWeeks(bodyRow);

				// 12.- CICLO PARA DETERMINAR SALDO INICIAL Y SALDO FINAL EN LA
				// INFORMACION SEMANAL
//				subSaldoInicialFinalSemanalNext(bodyRow);

				// '13.- PUNTO DE CONTROL PARA IMPRESION DE INFORMACION MENSUAL
//				subPrintTotalesXMonths(bodyRow);

				// 14.- CICLO PARA DETERMINAR SALDO INICIAL Y SALDO FINAL EN LA
				// INFORMACION MENSUAL
//				subSaldoInicialFinalMensualNext(bodyRow);

				// 15.- PUNTO DE CONTROL PARA IMPRESION DE INFORMACION POR PERIODO
//				subPrintControlTotalesPeriodoSemanal(bodyRow);

				// //16.- CICLO PARA DETERMINAR SALDO INICIAL Y SALDO FINAL EN LA
				// INFORMACION POR PERIODO
//				subSaldoInicialFinalPeriodoNext(bodyRow);

				// 14.- OCULTAMOS LAS COLUMNAS Y FILAS DE CONTROL
			//	subOcultaColsAndRowsMensualAjustado(bodyRow);

		Gson g = new Gson();
		// logger.info( "MENSUAL AJUSTADO: " + g.toJson(bodyRow) ) ;
		return bodyRow;

	}
	

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 5 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subPrepareInformationforFlujoComparativoMensualOriginalAjustado 'PARAMS :
	 * NONE 'RETURN : NONE 'RESPONSABILITY : PUNTO DE LLAMADA PARA EL STORE QUE
	 * PREPARA LA INFORMACION EN UNA TABLA TEMPORAL POR USUARIO
	 * '************************************************************************
	 * *******************************************
	 */
	private void subPrepareInformationforFlujoComparativoMensualOriginalAjustado() {

		ParamSpFlujoDto obj = new ParamSpFlujoDto(IdGrupo, NoEmpresa, FechaInicial, FechaHoy, FechaManana, FechaFinal,
				IdReporte, IdDivisa, NoUsuario);

		int result = cashFlowDao.spFlujoMensualComparativoOriginalAjustadoDao(obj);

		obj.setOpcion(0);

		// logger.info( "RESULTADO " + result );

	}

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 8 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subSetSaldoInicialDiarioSemanalMensualPeriodo 'PARAMS : NONE 'RETURN :
	 * NONE 'RESPONSABILITY : CONTROLA LA OBTENCION Y LA IMPRESION DEL SALDO
	 * INICIAL PARA DIA SEMANA MES PERIODO ' NOTA: SE OBTIENE Y SE IMPRIME
	 * INDEPENDIENTEMENTE
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subSetSaldoInicialDiarioSemanalMensualPeriodoComparativos(List<HashMap> bodyRow) {

		// 'PARA ENCONTRAR I, EL INICIO HABIL DEL PERIODO
		int iRowOfIniDia;
		int iColOfIniDia;
		int iColOfIniDia2;
		int iColOfIniDiaTmp;
		int iColOfIniDiaTmp2;

		int iRowOfIniSemana;
		int iColOfIniSemana;
		int iRowOfIniSemana2;
		int iColOfIniSemana2;

		int iRowOfIniMes;
		int iColOfIniMes;
		int iColOfIniMes2;

		int iRowOfIniPeriodo;
		int iColOfIniPeriodo;

		int iRowSaldoDayWeekMonthPeriodic;

		double dblSaldoInicial;

		HashMap hsmpTemp = new HashMap<String, String>();

		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		HashMap hsmpSaldos = new HashMap<String, String>();

		dblSaldoInicial = funGetSaldoInicial();

		hsmpSaldos = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "R-S"));

		iRowSaldoDayWeekMonthPeriodic = getRowOfIdHeaderDates(bodyRow, "R-S");
		iRowOfIniDia = getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN");

		iColOfIniDiaTmp = funGetColOfFec(bodyRow, iRowOfIniDia, "I");
		iColOfIniDia = (iColOfIniDiaTmp == 0) ? funGetColOfFec(bodyRow, iRowOfIniDia, "I_H") : iColOfIniDiaTmp;

		iColOfIniDiaTmp2 = funGetColOfFec(bodyRow, iRowOfIniDia, "I", iColOfIniDiaTmp + 1);
		iColOfIniDia2 = (iColOfIniDiaTmp2 == 0) ? funGetColOfFec(bodyRow, iRowOfIniDia, "I_H", iColOfIniDia + 1) : iColOfIniDiaTmp2;

		iRowOfIniSemana = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
		iColOfIniSemana = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--1-M--1");
		iColOfIniSemana2 = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--1-M--1", iColOfIniSemana + 1);

		hsmpTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_HABIL"));

		if (hsmpTemp.get("col" + iColOfIniSemana).equals("O"))
			iColOfIniSemana = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--2-M--1");

		if (hsmpTemp.get("col" + iColOfIniSemana).equals("O"))
			iColOfIniSemana = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--3-M--1");

		if (hsmpTemp.get("col" + iColOfIniSemana).equals("O"))
			iColOfIniSemana = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--4-M--1");

		if (hsmpTemp.get("col" + iColOfIniSemana).equals("O"))
			iColOfIniSemana = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--5-M--1");

		if (hsmpTemp.get("col" + iColOfIniSemana).equals("O"))
			iColOfIniSemana = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--6-M--1");

		if (hsmpTemp.get("col" + iColOfIniSemana2).equals("O"))
			iColOfIniSemana2 = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--2-M--1", iColOfIniSemana + 1);

		if (hsmpTemp.get("col" + iColOfIniSemana).equals("O"))
			iColOfIniSemana2 = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--3-M--1", iColOfIniSemana + 1);

		if (hsmpTemp.get("col" + iColOfIniSemana).equals("O"))
			iColOfIniSemana2 = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--4-M--1", iColOfIniSemana + 1);

		if (hsmpTemp.get("col" + iColOfIniSemana).equals("O"))
			iColOfIniSemana2 = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--5-M--1", iColOfIniSemana + 1);

		if (hsmpTemp.get("col" + iColOfIniSemana).equals("O"))
			iColOfIniSemana2 = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--6-M--1", iColOfIniSemana + 1);

		iRowOfIniMes = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
		iColOfIniMes = funGetColOfFec(bodyRow, iRowOfIniMes, "M--1");
		iColOfIniMes2 = funGetColOfFec(bodyRow, iRowOfIniMes, "M--1", iColOfIniMes + 1);

		iRowOfIniPeriodo = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
		iColOfIniPeriodo = funGetColOfFec(bodyRow, iRowOfIniPeriodo, "10000");

		hsmpSaldos.put("col" + iColOfIniDia, formateador.format(dblSaldoInicial));
		hsmpSaldos.put("col" + iColOfIniDia2, formateador.format(dblSaldoInicial));
		hsmpSaldos.put("col" + iColOfIniSemana, formateador.format(dblSaldoInicial));
		hsmpSaldos.put("col" + iColOfIniSemana2, formateador.format(dblSaldoInicial));
		hsmpSaldos.put("col" + iColOfIniMes, formateador.format(dblSaldoInicial));
		hsmpSaldos.put("col" + iColOfIniMes2, formateador.format(dblSaldoInicial));
		hsmpSaldos.put("col" + iColOfIniPeriodo, formateador.format(dblSaldoInicial));

	}
	// *****************************************************************************END

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 8 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subSetSaldoInicialDiarioSemanalMensualPeriodo 'PARAMS : NONE 'RETURN :
	 * NONE 'RESPONSABILITY : CONTROLA LA OBTENCION Y LA IMPRESION DEL SALDO
	 * INICIAL PARA DIA SEMANA MES PERIODO ' NOTA: SE OBTIENE Y SE IMPRIME
	 * INDEPENDIENTEMENTE
	 * '************************************************************************
	 * ******************************************* '*------->R-UTIL-BODY
	 */
	private void subSetSaldoInicialDiarioSemanalMensualPeriodoOrigAjusReal(List<HashMap> bodyRow) {

		// 'PARA ENCONTRAR I, EL INICIO HABIL DEL PERIODO
		int iRowOfIniDia;
		int iColOfIniDia;
		int iColOfIniDia2;
		int iColOfIniDia3;
		int iColOfIniDiaTmp;
		int iColOfIniDiaTmp2;
		int iColOfIniDiaTmp3;

		int iRowOfIniSemana;
		int iColOfIniSemana;
		int iRowOfIniSemana2;
		int iColOfIniSemana2;
		int iRowOfIniSemana3;
		int iColOfIniSemana3;

		int iRowOfIniMes;
		int iColOfIniMes;
		int iColOfIniMes2;
		int iColOfIniMes3;

		int iRowOfIniPeriodo;
		int iColOfIniPeriodo;

		int iRowSaldoDayWeekMonthPeriodic;

		double dblSaldoInicial;

		HashMap hsmpTemp = new HashMap<String, String>();

		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");

		HashMap hsmpSaldos = new HashMap<String, String>();

		dblSaldoInicial = funGetSaldoInicial();

		hsmpSaldos = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "R-S"));

		iRowSaldoDayWeekMonthPeriodic = getRowOfIdHeaderDates(bodyRow, "R-S");
		iRowOfIniDia = getRowOfIdHeaderDates(bodyRow, "BAN_INI_HOY_FIN");

		iColOfIniDiaTmp = funGetColOfFec(bodyRow, iRowOfIniDia, "I");
		iColOfIniDia = (iColOfIniDiaTmp == 0) ? funGetColOfFec(bodyRow, iRowOfIniDia, "I_H") : iColOfIniDiaTmp;

		iColOfIniDiaTmp2 = funGetColOfFec(bodyRow, iRowOfIniDia, "I", iColOfIniDiaTmp + 1);
		iColOfIniDia2 = (iColOfIniDiaTmp2 == 0) ? funGetColOfFec(bodyRow, iRowOfIniDia, "I_H", iColOfIniDia + 1)
				: iColOfIniDiaTmp2;

		iColOfIniDiaTmp3 = funGetColOfFec(bodyRow, iRowOfIniDia, "I", iColOfIniDiaTmp2 + 1);
		iColOfIniDia3 = (iColOfIniDiaTmp3 == 0) ? funGetColOfFec(bodyRow, iRowOfIniDia, "I_H", iColOfIniDia2 + 1)
				: iColOfIniDiaTmp3;

		iRowOfIniSemana = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
		iColOfIniSemana = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--1-M--1");
		iColOfIniSemana2 = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--1-M--1", iColOfIniSemana + 1);
		iColOfIniSemana3 = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--1-M--1", iColOfIniSemana2 + 1);

		hsmpTemp = bodyRow.get(getRowOfIdHeaderDates(bodyRow, "FEC_HABIL"));

		if (hsmpTemp.get("col" + iColOfIniSemana).equals("O"))
			iColOfIniSemana = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--2-M--1");

		if (hsmpTemp.get("col" + iColOfIniSemana).equals("O"))
			iColOfIniSemana = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--3-M--1");

		if (hsmpTemp.get("col" + iColOfIniSemana).equals("O"))
			iColOfIniSemana = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--4-M--1");

		if (hsmpTemp.get("col" + iColOfIniSemana).equals("O"))
			iColOfIniSemana = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--5-M--1");

		if (hsmpTemp.get("col" + iColOfIniSemana).equals("O"))
			iColOfIniSemana = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--6-M--1");

		if (hsmpTemp.get("col" + iColOfIniSemana2).equals("O"))
			iColOfIniSemana2 = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--2-M--1", iColOfIniSemana + 1);

		if (hsmpTemp.get("col" + iColOfIniSemana2).equals("O"))
			iColOfIniSemana2 = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--3-M--1", iColOfIniSemana + 1);

		if (hsmpTemp.get("col" + iColOfIniSemana2).equals("O"))
			iColOfIniSemana2 = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--4-M--1", iColOfIniSemana + 1);

		if (hsmpTemp.get("col" + iColOfIniSemana2).equals("O"))
			iColOfIniSemana2 = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--5-M--1", iColOfIniSemana + 1);

		if (hsmpTemp.get("col" + iColOfIniSemana2).equals("O"))
			iColOfIniSemana2 = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--6-M--1", iColOfIniSemana + 1);

		if (hsmpTemp.get("col" + iColOfIniSemana3).equals("O"))
			iColOfIniSemana3 = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--2-M--1", iColOfIniSemana2 + 1);

		if (hsmpTemp.get("col" + iColOfIniSemana3).equals("O"))
			iColOfIniSemana3 = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--3-M--1", iColOfIniSemana2 + 1);

		if (hsmpTemp.get("col" + iColOfIniSemana3).equals("O"))
			iColOfIniSemana3 = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--4-M--1", iColOfIniSemana2 + 1);

		if (hsmpTemp.get("col" + iColOfIniSemana3).equals("O"))
			iColOfIniSemana3 = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--5-M--1", iColOfIniSemana2 + 1);

		if (hsmpTemp.get("col" + iColOfIniSemana3).equals("O"))
			iColOfIniSemana3 = funGetColOfFec(bodyRow, iRowOfIniSemana, "W--6-M--1", iColOfIniSemana2 + 1);

		iRowOfIniMes = getRowOfIdHeaderDates(bodyRow, "FEC_DIA");
		iColOfIniMes = funGetColOfFec(bodyRow, iRowOfIniMes, "M--1");
		iColOfIniMes2 = funGetColOfFec(bodyRow, iRowOfIniMes, "M--1", iColOfIniMes + 1);
		iColOfIniMes3 = funGetColOfFec(bodyRow, iRowOfIniMes, "M--1", iColOfIniMes2 + 1);

		iRowOfIniPeriodo = getRowOfIdHeaderDates(bodyRow, "FEC_NO_DIA");
		iColOfIniPeriodo = funGetColOfFec(bodyRow, iRowOfIniPeriodo, "10000");

		hsmpSaldos.put("col" + iColOfIniDia, formateador.format(dblSaldoInicial));
		hsmpSaldos.put("col" + iColOfIniDia2, formateador.format(dblSaldoInicial));
		hsmpSaldos.put("col" + iColOfIniDia3, formateador.format(dblSaldoInicial));
		hsmpSaldos.put("col" + iColOfIniSemana, formateador.format(dblSaldoInicial));
		hsmpSaldos.put("col" + iColOfIniSemana2, formateador.format(dblSaldoInicial));
		hsmpSaldos.put("col" + iColOfIniSemana3, formateador.format(dblSaldoInicial));
		hsmpSaldos.put("col" + iColOfIniMes, formateador.format(dblSaldoInicial));
		hsmpSaldos.put("col" + iColOfIniMes2, formateador.format(dblSaldoInicial));
		hsmpSaldos.put("col" + iColOfIniMes3, formateador.format(dblSaldoInicial));
		hsmpSaldos.put("col" + iColOfIniPeriodo, formateador.format(dblSaldoInicial));

	}
	// *****************************************************************************END

	/*
	 * 'AUTOR : JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subPrintColsPeriodoForComparativos 'PARAMS : NONE 'RETURN : NONE
	 * 'RESPONSABILITY : AGREGA LA COLUMNA TOTAL DEL PERIODO Y LAS DESENCADENA
	 * EL LLAMANDO A LA FUNCION AGREGADO DE COLUMNAS DE MESES
	 */
	public void subPrintColsPeriodoForComparativos(List<HashMap> bodyRow, int iClicles) {

		subPrintColMonthsForComparativos(bodyRow, iClicles);  
		
		subPrintDatesOfColPeriodoComparativo(bodyRow); 
		lastCol += 1;

		// logger.info("body "+bodyRow);
	}
	
	public void subPrintColsPeriodoForComparativosPrueba(List<HashMap> bodyRow, int iClicles) {

		subPrintColMonthsForComparativosPrueba(bodyRow, iClicles);  
		
		subPrintDatesOfColPeriodoComparativo(bodyRow); 
		lastCol += 1;
		 
		// logger.info("body "+bodyRow);
	}

	/*
	 * 'AUTOR : JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subPrintColMonths 'PARAMS : NONE 'RETURN : NONE 'RESPONSABILITY : PINTA
	 * LOS MESES EN EL GRID
	 */
	public void subPrintColMonthsForComparativos(List<HashMap> bodyRow, int ntCiclos) {

		long ntNumMonths = 0;

		String fecInicialAux;
		String fecFinalAux;

		String fecInicial;
		String fecFinal;

		ntNumMonths = getMonthsBetweenTwoDates(FechaInicial, FechaFinal);
		// logger.info("uno "+ntNumMonths);
		lastCol = 3;

		fecInicial = getDatesOfMonth(FechaInicial, FechaFinal, 1);
		fecFinal = getDatesOfMonth(fecInicial, FechaFinal, 2);

		String tipoInfo = "";
		for (int iMes = 1; iMes <= ntNumMonths; iMes++) {
			fecInicialAux = fecInicial;
			fecFinalAux = fecFinal;
			for (int iCiclos = 1; iCiclos <= ntCiclos; iCiclos++) {
				fecInicial = fecInicialAux;
				fecFinal = fecFinalAux;
				if (IdReporte == 3) {
					if (iCiclos == 1)
						tipoInfo = "Original"; 
					if (iCiclos == 2)
						tipoInfo = "Ajustado";
				}

				if (IdReporte == 5) {
					if (iCiclos == 1)
						tipoInfo = "Real";

					if (iCiclos == 2)
						tipoInfo = "Ajustado";
				}

				if (IdReporte == 6) {
					if (iCiclos == 1)
						tipoInfo = "Original";

					if (iCiclos == 2)
						tipoInfo = "Ajustado";

					if (iCiclos == 3)
						tipoInfo = "Real"; 
				}
				subPrintColWeek(bodyRow, fecInicial, fecFinal, iMes);
				subPrintDatesOfColMonths1(bodyRow, fecInicial, iMes, tipoInfo);
				fecInicial = getDatesOfMonth(fecInicial, fecFinal, 3);
				fecFinal = getDatesOfMonth(fecInicial, fecFinal, 4);
				lastCol += 1;
			}
			
			 
			
		}  

	}

	
	public void subPrintColMonthsForComparativosPrueba(List<HashMap> bodyRow, int ntCiclos) {

		long ntNumMonths = 0;

		String fecInicialAux;
		String fecFinalAux;

		String fecInicial;
		String fecFinal;

		ntNumMonths = getMonthsBetweenTwoDates(FechaInicial, FechaFinal);
		// logger.info("uno "+ntNumMonths);
		lastCol = 3;

		fecInicial = getDatesOfMonth(FechaInicial, FechaFinal, 1);
		fecFinal = getDatesOfMonth(fecInicial, FechaFinal, 2);

		String tipoInfo = "";
		for (int iMes = 1; iMes <= ntNumMonths; iMes++) {
			fecInicialAux = fecInicial;
			fecFinalAux = fecFinal;
			for (int iCiclos = 1; iCiclos <= ntCiclos; iCiclos++) {
				fecInicial = fecInicialAux;
				fecFinal = fecFinalAux;
				if (IdReporte == 3) {
					if (iCiclos == 1)
						tipoInfo = "Original"; 
					if (iCiclos == 2)
						tipoInfo = "Ajustado";
				}

				if (IdReporte == 5 || IdReporte == 8) {
					if (iCiclos == 1)
						tipoInfo = "Real";

					if (iCiclos == 2)
						tipoInfo = "Ajustado";
				}

				if (IdReporte == 6) {
					if (iCiclos == 1)
						tipoInfo = "Original";

					if (iCiclos == 2)
						tipoInfo = "Ajustado";

					if (iCiclos == 3)
						tipoInfo = "Real"; 
				}
				subPrintColWeekPrueba( bodyRow, fecInicial, fecFinal , iMes,tipoInfo);	
				subPrintDatesOfColMonths1(bodyRow, fecInicial, iMes, tipoInfo);
				fecInicial = getDatesOfMonth(fecInicial, fecFinal, 3);
				fecFinal = getDatesOfMonth(fecInicial, fecFinal, 4);
				lastCol += 1;
			} 
			subPrintDatesOfColPeriodoComparativo2(bodyRow);
			lastCol += 1;
		}   
		
		
		
	}

	// *****************************************************************************END

	/*----------------------------------------------------------------------------------------------------------------------------------------------------
	 ***************************************************************************************************************************************************** 
	 *****************************************************************************************************************************************************
	 ****************************************************CONSTRUCCION DE COMPARATIVO MENSUAL ORIGINAL / AJUSTADO************************************************************
	 *****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************
	 *----------------------------------------------------------------------------------------------------------------------------------------------------*/

	/*
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++
	 ***************************************************************************************************************************************************** 
	 *****************************************************************************************************************************************************
	 **************************************************** CONSTRUCCION DE MENSUAL ORIGINAL /
	 * AJUSTADO************************************************************
	 *****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++
	 */
	private List<HashMap> setBodyMensualOriginalAjustado() {

		List<HashMap> bodyRow = new ArrayList<HashMap>();

		// '1.-INICIALIZAMOS EL GRID DONDE SE PRESENTA EL REPORTE
		subPrintHeaderOfReport(bodyRow);

		// '2.-CREACION DEL CUERPO E IMPRESION DE LOS ENCABEZADOS DE
		// PRESENTACION Y DE CONTROL
		subPrintColsPeriodoOfGrid(bodyRow);

		// '3.-ESTABLECE LAS FECHAS INICIAL Y FINAL DEL PERIODO EN EL GRID
		subSetColControlDatesHeader(bodyRow);

		// '4.-DETERMINA Y ESTABLECE SI LA INFORMACION ES REAL O AJUSTADA U
		// ORIGINAL ===D MODIFICAR
		subAnalizeAndSetRowTypeInfo(bodyRow);

		// '5.-EJECUCION DEL STORE PARA PREPARACION DE LA INFORMACION C=== CREAR
		subPrepareInformationforFlujoMesualRealAjustado(); /* Hector */

		// '6.-PUNTO DE INICIO PARA IMPRIMIR LA INFORMACION DE LA COLUMNA DE
		// CONCEPTOS
		subPrintTitulosTiposConceptosMensual(bodyRow); /* HECTOR */

		// 7.-IMPRIME CEROS EN EL GRID DEL REPORTE
		subPrintCerosinGrid(bodyRow);

		// 8.-ESTABLECEMOS EL SALDO INICIAL PARA DIA SEMANA MES PERIODO
		subSetSaldoInicialDiarioSemanalMensualPeriodo(bodyRow);

		// 9.- PUNTO DE CONTROL PARA IMPRESION DE INFORMACION DIARIA
		//subControlTotalesDiario(bodyRow);

		// 10.- CICLO PARA DETERMINAR SALDO INICIAL Y SALDO FINAL EN LA
		// INFORMACION DIARIA
		//subSaldoInicialFinalDiarioNext(bodyRow);

		// 11.- PUNTO DE CONTROL PARA IMPRESION DE INFORMACION SEMANAL
		//subPrintTotalesXWeeks(bodyRow);

		// 12.- CICLO PARA DETERMINAR SALDO INICIAL Y SALDO FINAL EN LA
		// INFORMACION SEMANAL
		//subSaldoInicialFinalSemanalNext(bodyRow);

		// //'13.- PUNTO DE CONTROL PARA IMPRESION DE INFORMACION MENSUAL
		subPrintTotalesXMonthsMensual(bodyRow);

		// 14.- CICLO PARA DETERMINAR SALDO INICIAL Y SALDO FINAL EN LA
		// INFORMACION MENSUAL
		subSaldoInicialFinalMensualNext(bodyRow);

		// 15.- PUNTO DE CONTROL PARA IMPRESION DE INFORMACION POR PERIODO
		subPrintControlTotalesPeriodoMesual(bodyRow);

		// 16.- CICLO PARA DETERMINAR SALDO INICIAL Y SALDO FINAL EN LA
		// INFORMACION POR PERIODO
		subSaldoInicialFinalPeriodoNext(bodyRow);

		// 14.- OCULTAMOS LAS COLUMNAS Y FILAS DE CONTROL
		subOcultaColsAndRowsMensualAjustado(bodyRow);

		Gson g = new Gson();
		// logger.info( "CUERPO MENSUAL REAL AJUSTADO: " + g.toJson(bodyRow) ) ;

		return bodyRow;

	}
	// *****************************************************************************END

	/*----------------------------------------------------------------------------------------------------------------------------------------------------
	 ***************************************************************************************************************************************************** 
	 *****************************************************************************************************************************************************
	 ****************************************************CONSTRUCCION DE MENSUAL ORIGINAL / AJUSTADO******************************************************
	 *****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************
	 *----------------------------------------------------------------------------------------------------------------------------------------------------*/

	/*
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++
	 ***************************************************************************************************************************************************** 
	 *****************************************************************************************************************************************************
	 **************************************************** CONSTRUCCION DE MENSUAL COMPARATIVO REAL /
	 * AJUSTADO******************************************
	 *****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++
	 */



	public HSSFWorkbook reporteFlujo2(String grupo, String Noempresa, String empresa, String fechaIni, String fechaFin,
			String reporte, String noGrupo) {
		HSSFWorkbook hb = null;
		String mov = "I";
		String mov2 = "E";
		IdDivisa = "MN";
		NoUsuario = 2;
		try { 
			if(reporte.equals("FLUJO MENSUAL ORIGINAL")){
					hb = generarExcelS2(cashFlowDao.saldoInicial(grupo, Noempresa, empresa, fechaIni, fechaFin, reporte,
					noGrupo, NoUsuario, IdDivisa), Noempresa, empresa, reporte, grupo, NoUsuario, fechaIni, fechaFin, noGrupo, IdDivisa);
			}else if(reporte.equals("FLUJO MENSUAL AJUSTADO")){
					hb = generarExcelA(cashFlowDao.saldoInicial(grupo, Noempresa, empresa, fechaIni, fechaFin, reporte,
					noGrupo, NoUsuario, IdDivisa), Noempresa, empresa, reporte, grupo, NoUsuario, fechaIni, fechaFin, noGrupo, IdDivisa);
			}else if(reporte.equals("FLUJO COMPARATIVO ORIGINAL/AJUSTADO")){
				hb = generarExcel(cashFlowDao.saldoInicial(grupo, Noempresa, empresa, fechaIni, fechaFin, reporte,
				noGrupo, NoUsuario, IdDivisa), Noempresa, empresa, reporte, grupo, NoUsuario, fechaIni, fechaFin, noGrupo, IdDivisa);
			}else if(reporte.equals("FLUJO SEMANAL REAL/AJUSTADO")){
				hb = generarExcelSemanal(cashFlowDao.saldoInicial(grupo, Noempresa, empresa, fechaIni, fechaFin, reporte,
				noGrupo, NoUsuario, IdDivisa), Noempresa, empresa, reporte, grupo, NoUsuario, fechaIni, fechaFin, noGrupo, IdDivisa);
			}else if(reporte.equals("FLUJO COMPARATIVO REAL/AJUSTADO")){
				hb = generarExcelRA(cashFlowDao.saldoInicial(grupo, Noempresa, empresa, fechaIni, fechaFin, reporte,
				noGrupo, NoUsuario, IdDivisa), Noempresa, empresa, reporte, grupo, NoUsuario, fechaIni, fechaFin, noGrupo, IdDivisa);
			}else if(reporte.equals("FLUJO MENSUAL ORIGINAL/AJUSTADO/REAL")){
				hb = generarExcelOAR(cashFlowDao.saldoInicial(grupo, Noempresa, empresa, fechaIni, fechaFin, reporte,
				noGrupo, NoUsuario, IdDivisa), Noempresa, empresa, reporte, grupo, NoUsuario, fechaIni, fechaFin, noGrupo, IdDivisa);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			// bitacora.insertarRegistro(new Date().toString() + " " +
			// Bitacora.getStackTrace(e) + "P: Personas, C:
			// ConsultaPersonasBusinessImpl, M: reportePersonas");
		}
		return hb;
	}

	private List<HashMap> setBodyMensualComparativoRealAjustado() {

		List<HashMap> bodyRow = new ArrayList<HashMap>();

		// '1.-INICIALIZAMOS EL GRID DONDE SE PRESENTA EL REPORTE
		subPrintHeaderOfReport(bodyRow);

		// '2.-CREACION DEL CUERPO E IMPRESION DE LOS ENCABEZADOS DE
		// PRESENTACION Y DE CONTROL
		subPrintColsPeriodoForComparativos(bodyRow, 2);

		// '3.-ESTABLECE LAS FECHAS INICIAL Y FINAL DEL PERIODO EN EL GRID
		 subSetColControlDatesHeader( bodyRow );

		// '4.-DETERMINA Y ESTABLECE SI LA INFORMACION ES REAL AJUSTADA ORIGINAL
		// ===D MODIFICAR
		 subAnalizeAndSetRowTypeInfo( bodyRow );

		// '6.-EJECUCION DEL STORE PARA PREPARACION DE LA INFORMACION C=== CREAR
		 subPrepareInformationforFlujoComparativoMensualRealAjustado();

		// '7.-PUNTO DE INICIO PARA IMPRIMIR LA INFORMACION DE LA COLUMNA DE
		// CONCEPTOS
		 subPrintTitulosTiposConceptosComparativoAjustadoReal( bodyRow );

		// 8.-IMPRIME CEROS EN EL GRID DEL REPORTE
		 subPrintCerosinGrid(bodyRow);

		// 9.-ESTABLECEMOS EL SALDO INICIAL PARA DIA SEMANA MES PERIODO ===D
		// MODIFICAR
		 subSetSaldoInicialDiarioSemanalMensualPeriodoComparativos(bodyRow);

		// '10.- PUNTO DE CONTROL PARA IMPRESION DE INFORMACION MENSUAL
		 subPrintTotalesXMonthsComparativosRealAjustado(bodyRow);

		// 11.- CICLO PARA DETERMINAR SALDO INICIAL Y SALDO FINAL EN LA
		// INFORMACION MENSUAL
		 subSaldoInicialFinalMensualNextComparativo(bodyRow);

		// 12.- PUNTO DE CONTROL PARA IMPRESION DE INFORMACION POR PERIODO
		subPrintControlTotalesPeriodoComparativo(bodyRow);

		// 13.- CICLO PARA DETERMINAR SALDO INICIAL Y SALDO FINAL EN LA
		// INFORMACION POR PERIODO
		 subSaldoInicialFinalPeriodoNext(bodyRow);

		// 14.- OCULTAMOS LAS COLUMNAS Y FILAS DE CONTROL
		 subOcultaColsAndRowsMensualAjustado(bodyRow);

		Gson g = new Gson();
		// logger.info( "MENSUAL COMPARATIVO REAL AJUSTADO: " +
		// g.toJson(bodyRow) ) ;
		return bodyRow;

	}

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 5 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subPrepareInformationforFlujoComparativoMensualRealAjustado 'PARAMS :
	 * NONE 'RETURN : NONE 'RESPONSABILITY : PUNTO DE LLAMADA PARA EL STORE QUE
	 * PREPARA LA INFORMACION EN UNA TABLA TEMPORAL POR USUARIO
	 * '************************************************************************
	 * *******************************************
	 */
	private void subPrepareInformationforFlujoComparativoMensualRealAjustado() {

		ParamSpFlujoDto obj = new ParamSpFlujoDto(IdGrupo, NoEmpresa, FechaInicial, FechaHoy, FechaManana, FechaFinal,
				IdReporte, IdDivisa, NoUsuario);

		int result = cashFlowDao.spFlujoMensualComparativoRealAjustadoDao(obj);

		obj.setOpcion(0);

		// logger.info( "RESULTADO " + result );

	}

	/*----------------------------------------------------------------------------------------------------------------------------------------------------
	 ***************************************************************************************************************************************************** 
	 *****************************************************************************************************************************************************
	 ****************************************************CONSTRUCCION DE MENSUAL COMPARATIVO REAL / AJUSTADO******************************************
	 *****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************
	 *----------------------------------------------------------------------------------------------------------------------------------------------------*/

	/*
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++
	 ***************************************************************************************************************************************************** 
	 *****************************************************************************************************************************************************
	 **************************************************** CONSTRUCCION DE MENSUAL ORIGINAL / AJUSTADO / REAL
	 * **********************************************
	 *****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++
	 */
	private List<HashMap> setBodyMensualOriginalAjustadoReal() {

		List<HashMap> bodyRow = new ArrayList<HashMap>();

		// '1.-INICIALIZAMOS EL GRID DONDE SE PRESENTA EL REPORTE
		subPrintHeaderOfReport(bodyRow);

		// '2.-CREACION DEL CUERPO E IMPRESION DE LOS ENCABEZADOS DE
		// PRESENTACION Y DE CONTROL
		subPrintColsPeriodoForComparativos(bodyRow, 3);

		// '3.-ESTABLECE LAS FECHAS INICIAL Y FINAL DEL PERIODO EN EL GRID
		 subSetColControlDatesHeader( bodyRow );

		// '4.-DETERMINA Y ESTABLECE SI LA INFORMACION ES REAL AJUSTADA ORIGINAL
		// ===D MODIFICAR
		 subAnalizeAndSetRowTypeInfo( bodyRow );

		// '6.-EJECUCION DEL STORE PARA PREPARACION DE LA INFORMACION C=== CREAR
		 subPrepareInformationforFlujoMensualOriginalAjustadoReal();

		// '7.-PUNTO DE INICIO PARA IMPRIMIR LA INFORMACION DE LA COLUMNA DE
		// CONCEPTOS
		 subPrintTitulosTiposConceptosOAR( bodyRow );

		// 8.-IMPRIME CEROS EN EL GRID DEL REPORTE
		 subPrintCerosinGrid(bodyRow);

		// 9.-ESTABLECEMOS EL SALDO INICIAL PARA DIA SEMANA MES PERIODO ===D
		// MODIFICAR
		 subSetSaldoInicialDiarioSemanalMensualPeriodoOrigAjusReal(bodyRow);

		// '10.- PUNTO DE CONTROL PARA IMPRESION DE INFORMACION MENSUAL
		 subPrintTotalesXMonthsOrigAjusReal(bodyRow);

		// 11.- CICLO PARA DETERMINAR SALDO INICIAL Y SALDO FINAL EN LA
		// INFORMACION MENSUAL
		 subSaldoInicialFinalMensualNextOrigAjusReal(bodyRow);

		// 12.- PUNTO DE CONTROL PARA IMPRESION DE INFORMACION POR PERIODO
		 subPrintControlTotalesPeriodoComparativo(bodyRow);

		// 13.- CICLO PARA DETERMINAR SALDO INICIAL Y SALDO FINAL EN LA
		// INFORMACION POR PERIODO
		 subSaldoInicialFinalPeriodoNext(bodyRow);

		// 14.- OCULTAMOS LAS COLUMNAS Y FILAS DE CONTROL
		 subOcultaColsAndRowsMensualAjustado(bodyRow);

		Gson g = new Gson();
		// logger.info( "MENSUAL ORIGINAL AJUSTADO REAL: " + g.toJson(bodyRow) )
		// ;
		return bodyRow;

	}

	/*
	 * '************************************************************************
	 * ******************************************* 'ID : 5 'NIVEL : N1 'AUTOR :
	 * JUAN RAMIRO BARRERA MARTINEZ 'TYPE OBJECT : SUBRUTINA 'NAME :
	 * subPrepareInformationforFlujoComparativoMensualOriginalAjustado 'PARAMS :
	 * NONE 'RETURN : NONE 'RESPONSABILITY : PUNTO DE LLAMADA PARA EL STORE QUE
	 * PREPARA LA INFORMACION EN UNA TABLA TEMPORAL POR USUARIO
	 * '************************************************************************
	 * *******************************************
	 */
	private void subPrepareInformationforFlujoMensualOriginalAjustadoReal() {

		ParamSpFlujoDto obj = new ParamSpFlujoDto(IdGrupo, NoEmpresa, FechaInicial, FechaHoy, FechaManana, FechaFinal,
				IdReporte, IdDivisa, NoUsuario);

		int result = cashFlowDao.spFlujoMensuaOriginalAjustadoRealDao(obj);

		obj.setOpcion(0);

		// logger.info( "RESULTADO " + result );

	}

	/*----------------------------------------------------------------------------------------------------------------------------------------------------
	 ***************************************************************************************************************************************************** 
	 *****************************************************************************************************************************************************
	 ***************************************************CONSTRUCCION DE MENSUAL ORIGINAL / AJUSTADO / REAL **********************************************
	 *****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************
	 *----------------------------------------------------------------------------------------------------------------------------------------------------*/

	/*
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++
	 ***************************************************************************************************************************************************** 
	 *****************************************************************************************************************************************************
	 **************************************************** CONSTRUCCION DE SEMANAL REAL
	 * AJUSTADO************************************************************
	 *****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++
	 */

	/*
	 * AUTOR : JUAN RAMIRO BARRERA MARTINEZ NAME : setBodyDiarioReal PARAMS :
	 * NONE RETURN : LISTA QUE CONFORMA EL CUERPO DEL REPORTE RESPONSABILITY :
	 * DETERMINAR EL CUERPO DEL REPORTE
	 */
	// *****************************************************************************INIT
	public List<HashMap> setBodySemanalRealAjustado() {

		List<HashMap> bodyRow = new ArrayList<HashMap>();

		// '1.-INICIALIZAMOS EL GRID DONDE SE PRESENTA EL REPORTE
		subPrintHeaderOfReport(bodyRow);

		// '2.-CREACION DEL CUERPO E IMPRESION DE LOS ENCABEZADOS DE
		// PRESENTACION Y DE CONTROL
		subPrintColsPeriodoOfGrid(bodyRow);

		// '3.-ESTABLECE LAS FECHAS INICIAL Y FINAL DEL PERIODO EN EL GRID
		subSetColControlDatesHeader(bodyRow);

		// '4.-DETERMINA Y ESTABLECE SI LA INFORMACION ES REAL O AJUSTADA U
		// ORIGINAL ===D MODIFICAR
		subAnalizeAndSetRowTypeInfo(bodyRow);

		// '5.-EJECUCION DEL STORE PARA PREPARACION DE LA INFORMACION C=== CREAR
		subPrepareInformationforFlujoRealAjustado();

		// '6.-PUNTO DE INICIO PARA IMPRIMIR LA INFORMACION DE LA COLUMNA DE
		// CONCEPTOS
		subPrintTitulosTiposConceptos(bodyRow);

		// 7.-IMPRIME CEROS EN EL GRID DEL REPORTE
		subPrintCerosinGrid(bodyRow);

		// 8.-ESTABLECEMOS EL SALDO INICIAL PARA DIA SEMANA MES PERIODO
		subSetSaldoInicialDiarioSemanalMensualPeriodo(bodyRow);

		// 9.- PUNTO DE CONTROL PARA IMPRESION DE INFORMACION DIARIA
		subControlTotalesDiario(bodyRow);

		// 10.- CICLO PARA DETERMINAR SALDO INICIAL Y SALDO FINAL EN LA
		// INFORMACION DIARIA
		subSaldoInicialFinalDiarioNext(bodyRow);

		// 11.- PUNTO DE CONTROL PARA IMPRESION DE INFORMACION SEMANAL
		subPrintTotalesXWeeks(bodyRow);

		// 12.- CICLO PARA DETERMINAR SALDO INICIAL Y SALDO FINAL EN LA
		// INFORMACION SEMANAL
		subSaldoInicialFinalSemanalNext(bodyRow);

		// '13.- PUNTO DE CONTROL PARA IMPRESION DE INFORMACION MENSUAL
		subPrintTotalesXMonths(bodyRow);

		// 14.- CICLO PARA DETERMINAR SALDO INICIAL Y SALDO FINAL EN LA
		// INFORMACION MENSUAL
		subSaldoInicialFinalMensualNext(bodyRow);

		// 15.- PUNTO DE CONTROL PARA IMPRESION DE INFORMACION POR PERIODO
		subPrintControlTotalesPeriodoSemanal(bodyRow);

		// //16.- CICLO PARA DETERMINAR SALDO INICIAL Y SALDO FINAL EN LA
		// INFORMACION POR PERIODO
		subSaldoInicialFinalPeriodoNext(bodyRow);

		// 14.- OCULTAMOS LAS COLUMNAS Y FILAS DE CONTROL
		subOcultaColsAndRowsMensualAjustado(bodyRow);

		Gson g = new Gson();
		// logger.info( "CUERPO SEMANAL REAL AJUSTADO: " + g.toJson(bodyRow) ) ;

		return bodyRow;

	}
	// *****************************************************************************END

	/*----------------------------------------------------------------------------------------------------------------------------------------------------
	 ***************************************************************************************************************************************************** 
	 *****************************************************************************************************************************************************
	 ****************************************************CONSTRUCCION DE SEMANAL REAL  AJUSTADO***********************************************************
	 *****************************************************************************************************************************************************
	 *****************************************************************************************************************************************************
	 *----------------------------------------------------------------------------------------------------------------------------------------------------*/
	 

	public HSSFWorkbook generarExcelS2(float x, String noEmpresa, String empresa, String reporte, String grupo,
			int usuario, String fechaIni, String fechaFin, String noGrupo, String IdDivisa) {
 		FechaHoy = getFechasSistemaBusiness("HOY");
		String mesPrimero = "";
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		HSSFWorkbook wb = new HSSFWorkbook();// genera archivo
		HSSFSheet sheet = wb.createSheet();// genera hojas
		
		HSSFCellStyle reporteEstilo = wb.createCellStyle();
		reporteEstilo.setAlignment(HSSFCellStyle.ALIGN_FILL); 
		
		HSSFCellStyle styleGroup2 = wb.createCellStyle();
		styleGroup2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleGroup2.setFillForegroundColor(HSSFColor.YELLOW.index);
		
		HSSFCellStyle cellStyleI = wb.createCellStyle();	 
		cellStyleI.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		
		cellStyleI.setFillForegroundColor(new HSSFColor.BLUE().getIndex());
		
		HSSFCellStyle styleGroup3 = wb.createCellStyle();
		styleGroup3.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleGroup3.setFillForegroundColor(HSSFColor.RED.index);
		
		HSSFCellStyle styleGroup1 = wb.createCellStyle();
		styleGroup1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleGroup1.setBorderRight((short) 1);
		styleGroup1.setFillForegroundColor(HSSFColor.GREEN.index);
		
		HSSFFont font = wb.createFont();
		font.setColor(HSSFColor.RED.index);
		HSSFCellStyle colorLetra = wb.createCellStyle();
		colorLetra.setFont(font);
		 
		HSSFCellStyle alignText = wb.createCellStyle();
		alignText.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		alignText.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);
		 
		int rowIdx = 0;// renglones
		int cellIdx = 0;// filas 
		
		HSSFRow hssfHeader = sheet.createRow(rowIdx);// encabezado
		HSSFCell celdaLinea = hssfHeader.createCell(cellIdx);
		
		HSSFRow hssfHeader1 = sheet.createRow(rowIdx);// encabezado
		HSSFCell celdaLinea1 = hssfHeader.createCell(cellIdx);

		double contadorFinalPeriodo = 0.00;
		double subtotalPeriodo=0.00; 
		double subtotalPeriodoIngresoEgresos=0.00;
		int posicionSaldo;
		String columnaMes = "";
		String fecInicial = fechaIni, fecFinal = fechaFin, mes;
		int ntNumMonths = 0;

		int noEmp = Integer.parseInt(noEmpresa); 
		celdaLinea.setCellValue(new HSSFRichTextString("Grupo Empresa: " + grupo));
		sheet.autoSizeColumn(cellIdx,true);
		 
		if (noEmp != 0) { 
			rowIdx = rowIdx + 1;
			hssfHeader = sheet.createRow(rowIdx);
			celdaLinea = hssfHeader.createCell(cellIdx); 
			celdaLinea.setCellValue(new HSSFRichTextString("Empresa: " + empresa));
			sheet.autoSizeColumn(cellIdx,true);
		}

		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx); 
		celdaLinea = hssfHeader.createCell(cellIdx); 
		celdaLinea.setCellStyle(reporteEstilo);
		celdaLinea.setCellValue(new HSSFRichTextString("Reporte: " + reporte));
		sheet.autoSizeColumn(cellIdx,true);
		
		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx);
		celdaLinea = hssfHeader.createCell(cellIdx); 
		celdaLinea.setCellValue(new HSSFRichTextString("Fecha"));
		
		ntNumMonths = getMonthsBetweenTwoDatess(fecInicial, fecFinal);
		fecInicial = getDatesOfMonth(fecInicial, fecFinal, 1);
		fecFinal = getDatesOfMonth(fecInicial, fecFinal, 2);
		mes = subGetMonthOfDateInLetter(fecFinal);

		cellIdx = cellIdx + 1;
		celdaLinea = hssfHeader.createCell(cellIdx); 
		celdaLinea.setCellValue(new HSSFRichTextString(mes.toUpperCase()));
		sheet.autoSizeColumn(cellIdx,true);
		mesPrimero = mes.toUpperCase();

		int primero = cellIdx;
		Map<String, Integer> posicionMes = new HashMap<String, Integer>();
		posicionMes.put(mesPrimero, 1); 
		

		for (int i = 1; i <= ntNumMonths - 1; i++) {
			fecInicial = getDatesOfMonth(fecInicial, fecFinal, 3);
			fecFinal = getDatesOfMonth(fecInicial, fecFinal, 4);
			mes = subGetMonthOfDateInLetter(fecFinal);
			mes = mes.toUpperCase();
			cellIdx =cellIdx + 1;
			celdaLinea = hssfHeader.createCell(cellIdx); 
			celdaLinea.setCellValue(new HSSFRichTextString(mes));
			sheet.autoSizeColumn(cellIdx,true);
			posicionMes.put(mes, cellIdx);
		}
		
		boolean pasa=false;
		double[] subtotal = new double[ntNumMonths+1];
		double[] subtotalHor = new double[ntNumMonths+1];
		double[] totalIngresosEgresos= new double[ntNumMonths+1];
		double[] totalIngresosEgresos2= new double[ntNumMonths+1];
		double[] saldosAcomulado=new double[ntNumMonths+1];
		int[] posicionCelda=new int[ntNumMonths+1];
		double[] cantSaldo=new double[ntNumMonths+1];
		
		for (int i = 0; i == ntNumMonths; i++) {
			subtotal[i] = 0;
			subtotalHor[i] = 0;
			totalIngresosEgresos[i]=0;
			totalIngresosEgresos2[i]=0;
			saldosAcomulado[i]=0;
			cantSaldo[i]=0;
		}

		cellIdx = cellIdx + 1;
		posicionMes.put("TOTAL", cellIdx); 
		celdaLinea = hssfHeader.createCell(cellIdx); 
		celdaLinea.setCellValue(new HSSFRichTextString("Total periodo")); 
		sheet.autoSizeColumn(cellIdx,true);
		cellIdx = 0;
		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx); 
		celdaLinea = hssfHeader.createCell(cellIdx); 
		celdaLinea.setCellValue(new HSSFRichTextString("Conceptos"));
		sheet.autoSizeColumn(cellIdx,true);
		//inmoviliza columna,fila
		 sheet.createFreezePane (1, 5);
		rowIdx = rowIdx + 1; 
		
		int filaSaldo=rowIdx;//obtiene fila 
		int celdaSaldo=cellIdx;//obtiene la celda
		
		cantSaldo[0]=x;
		rowIdx=filaSaldo;
		cellIdx = celdaSaldo;
		
		rowIdx =rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx); 
		celdaLinea = hssfHeader.createCell(cellIdx); 
		celdaLinea.setCellStyle(cellStyleI);
		celdaLinea.setCellValue(new HSSFRichTextString("INGRESOS"));
		sheet.autoSizeColumn(cellIdx,true);

		List<ConceptoFlujo> conceptos;
		String idTipoMovto = "I";
		int empresaM = Integer.parseInt(noEmpresa);
		int NogrupoM = Integer.parseInt(noGrupo);
		List<TotalConcepto> totalesConcepto = cashFlowDao.funSqlGetTotalesXMesForConceptoOriginalExcel(IdDivisa,
				NoUsuario, mes, mes);

		Iterator it = totalesConcepto.iterator();

		String encabezadoAnterior = " ", descLargaAnterior = " ", tipoMovimiento = "";
		boolean totalMovimiento = true;
		int iPos = 0;

		while (it.hasNext()) {
			TotalConcepto totalConcepto = (TotalConcepto) it.next();
			tipoMovimiento = totalConcepto.getIngresoegreso();
			String mess = totalConcepto.getMes();

			if (tipoMovimiento.equals("I")) {
				if (encabezadoAnterior.equals(" ")) {
					cellIdx = 0;
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellStyle(styleGroup2);
					celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDescripcion().toString()));
					sheet.autoSizeColumn(cellIdx,true);
					
					// Se crea la descripcion larga
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx); 
					celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString()));
					sheet.autoSizeColumn(cellIdx,true);
					
					for (int i = 1; i <= subtotal.length-1; i++) {
						celdaLinea = hssfHeader.createCell(i);
						celdaLinea.setCellValue(new HSSFRichTextString(0.00+""));  
						celdaLinea.setCellStyle(alignText);
						}
					
					// Se crea el primer mes
					cellIdx = posicionMes.get(totalConcepto.getMes().toUpperCase()); 
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal()))); 
					celdaLinea.setCellStyle(alignText);
					sheet.autoSizeColumn(cellIdx,true);
					// Se va sumando el subtotal
					subtotal[cellIdx] = Double.parseDouble(totalConcepto.getTotal() + "");
					subtotalHor[cellIdx] = Double.parseDouble(totalConcepto.getTotal() + ""); 
					descLargaAnterior = totalConcepto.getDesc_larga().toString();
					
				} else if (encabezadoAnterior.equals(totalConcepto.getDescripcion().toString())) {
					if (descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())) {
						// Se crea el primer mes
						cellIdx = posicionMes.get(totalConcepto.getMes().toUpperCase());
						celdaLinea = hssfHeader.createCell(cellIdx);
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
						celdaLinea.setCellStyle(alignText);
						sheet.autoSizeColumn(cellIdx,true);
						descLargaAnterior = totalConcepto.getDesc_larga().toString();

						// Se va sumando el subtotal
						subtotal[cellIdx] += Double.parseDouble(totalConcepto.getTotal() + "");
						subtotalHor[cellIdx] += Double.parseDouble(totalConcepto.getTotal() + "");

					} else {
						// Se calcula y se pinta el total de la descripcion
						// larga anterior
						cellIdx=posicionMes.get("TOTAL");
						celdaLinea = hssfHeader.createCell(cellIdx);
						for (int i = 1; i <= subtotalHor.length-1; i++) {
							contadorFinalPeriodo += subtotalHor[i];
							subtotalHor[i] = 0;
						}
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(contadorFinalPeriodo)));
						celdaLinea.setCellStyle(alignText);
						sheet.autoSizeColumn(cellIdx,true);
						contadorFinalPeriodo = 0.00;

						// Se pinta el nuevo concepto 
						cellIdx = 0;
						rowIdx = rowIdx + 1;
						hssfHeader = sheet.createRow(rowIdx);
						celdaLinea = hssfHeader.createCell(cellIdx); 
						celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString()));
						sheet.autoSizeColumn(cellIdx,true);
						System.out.println(totalConcepto.getDesc_larga().toString());
						
						for (int i = 1; i <= subtotal.length-1; i++) {
							celdaLinea = hssfHeader.createCell(i);
							celdaLinea.setCellValue(new HSSFRichTextString(0.00+"")); 
							celdaLinea.setCellStyle(alignText); 
							}
						
						cellIdx = posicionMes.get(totalConcepto.getMes().toUpperCase());
						celdaLinea = hssfHeader.createCell(cellIdx);
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
						celdaLinea.setCellStyle(alignText);
						sheet.autoSizeColumn(cellIdx,true);
						System.out.println(formateador.format(totalConcepto.getTotal()));
						 

						// Se va sumando el subtotal
						subtotal[cellIdx] += Double.parseDouble(totalConcepto.getTotal() + "");
						subtotalHor[cellIdx] += Double.parseDouble(totalConcepto.getTotal() + "");
						descLargaAnterior = totalConcepto.getDesc_larga().toString();
					}
				} else {
					
					if(pasa==true){
					cellIdx = 0;
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellStyle(styleGroup2);
					celdaLinea.setCellValue(new HSSFRichTextString("SUBTOTAL- " + encabezadoAnterior));
					sheet.autoSizeColumn(cellIdx,true);

					for (int i = 1; i <= subtotal.length-1; i++) {
						celdaLinea = hssfHeader.createCell(i);
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotal[i])));
						celdaLinea.setCellStyle(alignText);
						sheet.autoSizeColumn(i,true);
						subtotalPeriodo+=subtotal[i];  
						totalIngresosEgresos2[i]+=subtotal[i]; 
						subtotal[i] = 0;
					}
					cellIdx=posicionMes.get("TOTAL");
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalPeriodo)));
					celdaLinea.setCellStyle(alignText);
					sheet.autoSizeColumn(cellIdx,true);
					subtotalPeriodo=0.00;
					contadorFinalPeriodo = 0.00;
					}
					pasa=true;
					cellIdx = 0;
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellStyle(styleGroup2);
					celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDescripcion().toString()));
					sheet.autoSizeColumn(cellIdx,true);

					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx); 
					celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString()));
					sheet.autoSizeColumn(cellIdx,true);

					for (int i = 1; i <= subtotal.length-1; i++) {
						celdaLinea = hssfHeader.createCell(i);
						celdaLinea.setCellValue(new HSSFRichTextString(0.00+""));  
						}
					
					descLargaAnterior = totalConcepto.getDesc_larga().toString();
					cellIdx = posicionMes.get(totalConcepto.getMes().toUpperCase());
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
					celdaLinea.setCellStyle(alignText);
					sheet.autoSizeColumn(cellIdx,true);
					// Sumar el subtotal
					subtotal[cellIdx] = Double.parseDouble(totalConcepto.getTotal() + "");
					subtotalHor[cellIdx] += Double.parseDouble(totalConcepto.getTotal() + "");
				}
				
				encabezadoAnterior = totalConcepto.getDescripcion().toString();
				
			}
			//empieza la condicion del egreso
			else{
			  if(totalMovimiento){
				  
				  cellIdx=posicionMes.get("TOTAL");
					celdaLinea = hssfHeader.createCell(cellIdx); 
					for (int i = 1; i <= subtotalHor.length-1; i++) { 
						contadorFinalPeriodo += subtotalHor[i];
						subtotalHor[i] = 0;
					}
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(contadorFinalPeriodo)));
					celdaLinea.setCellStyle(alignText);
					sheet.autoSizeColumn(cellIdx,true);
					contadorFinalPeriodo = 0.00;
				  
				  cellIdx = 0;
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellStyle(styleGroup2);
					celdaLinea.setCellValue(new HSSFRichTextString("SUBTOTAL- " + encabezadoAnterior));
					sheet.autoSizeColumn(cellIdx,true);
					
					for (int i = 1; i <= subtotal.length-1; i++) {
						celdaLinea = hssfHeader.createCell(i);
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotal[i])));
						celdaLinea.setCellStyle(alignText);
						sheet.autoSizeColumn(i,true);
						//suma total de periodo
						subtotalPeriodo+=subtotal[i];
						//accede a subtotal de saldo ingresos
						totalIngresosEgresos[i]+=subtotal[i];
						subtotal[i] = 0;
					}
					
					cellIdx=posicionMes.get("TOTAL");
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalPeriodo)));
					celdaLinea.setCellStyle(alignText);
					sheet.autoSizeColumn(cellIdx,true);
					subtotalPeriodo=0.00;

				  rowIdx=rowIdx+1; 
				  cellIdx=0;
				  rowIdx=rowIdx+1;
				  hssfHeader = sheet.createRow(rowIdx);
				  celdaLinea = hssfHeader.createCell(cellIdx); 
				  celdaLinea.setCellStyle(styleGroup3);
				  celdaLinea.setCellValue(new HSSFRichTextString("TOTAL INGRESOS"));
				  sheet.autoSizeColumn(cellIdx,true);
				  
				  
				  for (int i = 1; i <= totalIngresosEgresos.length-1; i++) {
						celdaLinea = hssfHeader.createCell(i);
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalIngresosEgresos[i])));
						celdaLinea.setCellStyle(alignText);
						sheet.autoSizeColumn(cellIdx,true);
						subtotalPeriodoIngresoEgresos+=totalIngresosEgresos[i];
					}
				  
				  cellIdx=posicionMes.get("TOTAL");
				  celdaLinea = hssfHeader.createCell(cellIdx);
				  celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalPeriodoIngresoEgresos)));
					celdaLinea.setCellStyle(alignText);
				  sheet.autoSizeColumn(cellIdx,true);
				  subtotalPeriodoIngresoEgresos=0.00;
				  
				  rowIdx=rowIdx+1;
				  cellIdx=0; 
				  rowIdx=rowIdx+1; 
				  hssfHeader = sheet.createRow(rowIdx);   
				  celdaLinea = hssfHeader.createCell(cellIdx);
				  celdaLinea.setCellStyle(cellStyleI);
				  celdaLinea.setCellValue(new HSSFRichTextString("EGRESOS"));   
				  sheet.autoSizeColumn(cellIdx,true);
				  totalMovimiento=false; 
			 }
				if (encabezadoAnterior.equals(totalConcepto.getDescripcion().toString())) {
					if (descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())) {
						// Se crea el primer mes
						cellIdx = posicionMes.get(totalConcepto.getMes().toUpperCase());
						celdaLinea = hssfHeader.createCell(cellIdx);
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
						celdaLinea.setCellStyle(alignText);
						sheet.autoSizeColumn(cellIdx,true);
						descLargaAnterior = totalConcepto.getDesc_larga().toString();

						// Se va sumando el subtotal
						subtotal[cellIdx] += Double.parseDouble(totalConcepto.getTotal() + "");
						subtotalHor[cellIdx] += Double.parseDouble(totalConcepto.getTotal() + "");
 
					} else {
						// Se calcula y se pinta el total de la descripcion
						// larga anterior
						cellIdx=posicionMes.get("TOTAL");
						celdaLinea = hssfHeader.createCell(cellIdx);
						for (int i = 1; i <= subtotalHor.length-1; i++) {
							contadorFinalPeriodo += subtotalHor[i];
							subtotalHor[i] = 0;
						}
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(contadorFinalPeriodo)));
						celdaLinea.setCellStyle(alignText);
						sheet.autoSizeColumn(cellIdx,true);
						contadorFinalPeriodo = 0.00;

						// Se pinta el nuevo concepto 
						cellIdx = 0;
						rowIdx = rowIdx + 1;
						hssfHeader = sheet.createRow(rowIdx);
						celdaLinea = hssfHeader.createCell(cellIdx); 
						celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString()));
						sheet.autoSizeColumn(cellIdx,true);
						System.out.println(totalConcepto.getDesc_larga().toString());
						
						for (int i = 1; i <= subtotal.length-1; i++) {
							celdaLinea = hssfHeader.createCell(i);
							celdaLinea.setCellValue(new HSSFRichTextString(0.00+"")); 
							celdaLinea.setCellStyle(alignText); 
							}
						
						cellIdx = posicionMes.get(totalConcepto.getMes().toUpperCase());
						celdaLinea = hssfHeader.createCell(cellIdx);
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
						celdaLinea.setCellStyle(alignText);
						sheet.autoSizeColumn(cellIdx,true);
						System.out.println(formateador.format(totalConcepto.getTotal()));
						 

						// Se va sumando el subtotal
						subtotal[cellIdx] += Double.parseDouble(totalConcepto.getTotal() + "");
						subtotalHor[cellIdx] += Double.parseDouble(totalConcepto.getTotal() + "");
						descLargaAnterior = totalConcepto.getDesc_larga().toString();
					}
				} else {
					
					if(pasa==true){
					cellIdx = 0;
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellStyle(styleGroup2);
					celdaLinea.setCellValue(new HSSFRichTextString("SUBTOTAL- " + encabezadoAnterior));
					sheet.autoSizeColumn(cellIdx,true);

					for (int i = 1; i <= subtotal.length-1; i++) {
						celdaLinea = hssfHeader.createCell(i);
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotal[i])));
						celdaLinea.setCellStyle(alignText);
						sheet.autoSizeColumn(i,true);
						subtotalPeriodo+=subtotal[i];  
						totalIngresosEgresos2[i]+=subtotal[i]; 
						subtotal[i] = 0;
					}
					cellIdx=posicionMes.get("TOTAL");
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalPeriodo)));
					celdaLinea.setCellStyle(alignText);
					sheet.autoSizeColumn(cellIdx,true);
					subtotalPeriodo=0.00;
					contadorFinalPeriodo = 0.00;
					}
					pasa=true;
					cellIdx = 0;
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellStyle(styleGroup2);
					celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDescripcion().toString()));
					sheet.autoSizeColumn(cellIdx,true);

					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx); 
					celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString()));
					sheet.autoSizeColumn(cellIdx,true);

					for (int i = 1; i <= subtotal.length-1; i++) {
						celdaLinea = hssfHeader.createCell(i);
						celdaLinea.setCellValue(new HSSFRichTextString(0.00+""));  
						}
					
					descLargaAnterior = totalConcepto.getDesc_larga().toString();
					cellIdx = posicionMes.get(totalConcepto.getMes().toUpperCase());
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
					celdaLinea.setCellStyle(alignText);
					sheet.autoSizeColumn(cellIdx,true);
					// Sumar el subtotal
					subtotal[cellIdx] = Double.parseDouble(totalConcepto.getTotal() + "");
					subtotalHor[cellIdx] += Double.parseDouble(totalConcepto.getTotal() + "");
				}
			
			 
			cellIdx=posicionMes.get("TOTAL");
			celdaLinea = hssfHeader.createCell(cellIdx);
			sheet.autoSizeColumn(cellIdx,true);
			for (int i = 1; i <= subtotalHor.length-1; i++) { 
				contadorFinalPeriodo += subtotalHor[i];
				subtotalHor[i] = 0;
			}
			celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(contadorFinalPeriodo)));
			celdaLinea.setCellStyle(alignText);
			sheet.autoSizeColumn(cellIdx,true);
			encabezadoAnterior = totalConcepto.getDescripcion().toString();
			}
		}
		cellIdx = 0;
		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx);
		celdaLinea = hssfHeader.createCell(cellIdx); 
		celdaLinea.setCellStyle(styleGroup2);
		celdaLinea.setCellValue(new HSSFRichTextString("SUBTOTAL" + encabezadoAnterior));
		sheet.autoSizeColumn(cellIdx,true);
		 
		for (int i = 1; i <= subtotal.length-1; i++) {
			celdaLinea = hssfHeader.createCell(i);
			celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotal[i])));
			celdaLinea.setCellStyle(alignText);
			sheet.autoSizeColumn(i,true);
			subtotalPeriodo+=subtotal[i]; 
			//accede a subtotal de saldo ingresos
			totalIngresosEgresos2[i]+=subtotal[i];
			subtotal[i] = 0;
		}
		cellIdx=posicionMes.get("TOTAL");
		celdaLinea = hssfHeader.createCell(cellIdx); 
		celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalPeriodo)));
		celdaLinea.setCellStyle(alignText);
		sheet.autoSizeColumn(cellIdx,true);
		subtotalPeriodo=0.00;
		
		cellIdx=posicionMes.get("TOTAL");
		celdaLinea = hssfHeader.createCell(cellIdx);
		for (int i = 1; i <= subtotalHor.length-1; i++) { 
			contadorFinalPeriodo += subtotalHor[i];
			subtotalHor[i] = 0;
		}
		celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(contadorFinalPeriodo)));
		celdaLinea.setCellStyle(alignText);
		sheet.autoSizeColumn(cellIdx,true);
		
		rowIdx = rowIdx + 1;
		cellIdx = 0;
		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx);
		celdaLinea = hssfHeader.createCell(cellIdx);  
		celdaLinea.setCellStyle(styleGroup3);
		celdaLinea.setCellValue(new HSSFRichTextString("TOTAL EGRESOS"));
		sheet.autoSizeColumn(cellIdx,true);
		
		for (int i = 1; i <= totalIngresosEgresos2.length-1; i++) {
			celdaLinea = hssfHeader.createCell(i);
			
			celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalIngresosEgresos2[i])));
			celdaLinea.setCellStyle(alignText);
			sheet.autoSizeColumn(i,true);
			subtotalPeriodoIngresoEgresos+=totalIngresosEgresos2[i];
		}
		
		cellIdx=posicionMes.get("TOTAL");
		celdaLinea = hssfHeader.createCell(cellIdx);
		celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalPeriodoIngresoEgresos)));
		celdaLinea.setCellStyle(alignText);
		sheet.autoSizeColumn(cellIdx,true);
		subtotalPeriodoIngresoEgresos=0.00;

		rowIdx = rowIdx + 1;
		cellIdx = 0;
		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx);
		celdaLinea = hssfHeader.createCell(cellIdx);
		celdaLinea.setCellStyle(styleGroup3);
		celdaLinea.setCellValue(new HSSFRichTextString("SALDO OPERATIVO ACOMULADO"));
		sheet.autoSizeColumn(cellIdx,true);
		double acomulado=0.00;
		double acomuladoFinal=0.00;
		
		for (int i = 1; i <= totalIngresosEgresos.length-1; ) { 		
			for (int j = 1; j <= totalIngresosEgresos2.length-1; j++) {
				acomulado=totalIngresosEgresos[i]-totalIngresosEgresos2[j];
				celdaLinea = hssfHeader.createCell(i);
				 
				celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(acomulado))); 
				celdaLinea.setCellStyle(alignText);
				sheet.autoSizeColumn(i,true);
				if(acomulado < -0){ 
					celdaLinea.setCellStyle(colorLetra);
				} 
				saldosAcomulado[i]=	acomulado;	
				acomuladoFinal+=acomulado;
				acomulado=0.00;
				i++;
			}
		}
		cellIdx=posicionMes.get("TOTAL");
		celdaLinea = hssfHeader.createCell(cellIdx);
		celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(acomuladoFinal)));
		celdaLinea.setCellStyle(alignText);
		sheet.autoSizeColumn(cellIdx,true);
		acomuladoFinal=0.00;
		
		rowIdx = rowIdx + 1;

		cellIdx = 0;
		rowIdx = rowIdx + 1;
		hssfHeader1 = sheet.createRow(rowIdx);
		celdaLinea1 = hssfHeader1.createCell(cellIdx); 
		celdaLinea1.setCellStyle(styleGroup3);
		celdaLinea1.setCellValue(new HSSFRichTextString("SALDO DISPONIBLE"));
		sheet.autoSizeColumn(cellIdx,true);
		boolean primeraVez=true;
		double y=0.00;
		int ultima=0;
		for (int i = 1; i <= saldosAcomulado.length-1; i++) { 	
			if(primeraVez){
				y=x+saldosAcomulado[i];
				celdaLinea1 = hssfHeader1.createCell(i);
				sheet.autoSizeColumn(cellIdx,true);
				celdaLinea1.setCellValue(new HSSFRichTextString(formateador.format(y)));
				celdaLinea.setCellStyle(alignText);
				sheet.autoSizeColumn(i,true);
				primeraVez=false;
				cantSaldo[i]=y; 
			}else{  
				
				y=y+saldosAcomulado[i];
				celdaLinea1 = hssfHeader1.createCell(i);
				celdaLinea1.setCellValue(new HSSFRichTextString(formateador.format(y)));
				celdaLinea.setCellStyle(alignText);	
				sheet.autoSizeColumn(i,true);
				cantSaldo[i]=y; 
				ultima=i;
			}
		} 
		cantSaldo[ultima]=x; 
		
		
		 
		cellIdx=posicionMes.get("TOTAL");
		celdaLinea1 = hssfHeader1.createCell(cellIdx);
		celdaLinea1.setCellValue(new HSSFRichTextString(formateador.format(y))); 
		celdaLinea.setCellStyle(alignText);
		sheet.autoSizeColumn(cellIdx,true);
		
		hssfHeader = sheet.createRow(filaSaldo); 
		celdaLinea = hssfHeader.createCell(celdaSaldo); 
		celdaLinea.setCellStyle(styleGroup1);
		celdaLinea.setCellValue(new HSSFRichTextString("SALDO INICIAL"));
		
		for (int i = 0; i <= cantSaldo.length-1; i++) { 	
			celdaSaldo += 1;
			celdaLinea = hssfHeader.createCell(celdaSaldo); 
			celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(cantSaldo[i])));	 
			celdaLinea.setCellStyle(alignText);
			sheet.autoSizeColumn(i,true);
			
		}
		 
		return wb;
	}
	
	 
	
	public HSSFWorkbook generarExcelA(float x, String noEmpresa, String empresa, String reporte, String grupo,
			int usuario, String fechaIni, String fechaFin, String noGrupo, String IdDivisa) {
 		FechaHoy = getFechasSistemaBusiness("HOY");
		String mesPrimero = "";
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		HSSFWorkbook wb = new HSSFWorkbook();// genera archivo
		HSSFSheet sheet = wb.createSheet();// genera hojas
		
		HSSFCellStyle reporteEstilo = wb.createCellStyle();
		reporteEstilo.setAlignment(HSSFCellStyle.ALIGN_FILL); 
		
		HSSFCellStyle styleGroup2 = wb.createCellStyle();
		styleGroup2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleGroup2.setFillForegroundColor(HSSFColor.YELLOW.index);
		
		HSSFCellStyle cellStyleI = wb.createCellStyle();	 
		cellStyleI.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		
		cellStyleI.setFillForegroundColor(new HSSFColor.BLUE().getIndex());
		
		HSSFCellStyle styleGroup3 = wb.createCellStyle();
		styleGroup3.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleGroup3.setFillForegroundColor(HSSFColor.RED.index);
		
		HSSFCellStyle styleGroup1 = wb.createCellStyle();
		styleGroup1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleGroup1.setBorderRight((short) 1);
		styleGroup1.setFillForegroundColor(HSSFColor.GREEN.index);
		 
		HSSFCellStyle alignText = wb.createCellStyle();
		alignText.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		alignText.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);
		
		HSSFFont font = wb.createFont();
		font.setColor(HSSFColor.RED.index);
		HSSFCellStyle colorLetra = wb.createCellStyle();
		colorLetra.setFont(font);
		
		int rowIdx = 0;// renglones
		int cellIdx = 0;// filas 
		
		HSSFRow hssfHeader = sheet.createRow(rowIdx);// encabezado
		
		HSSFCell celdaLinea = hssfHeader.createCell(cellIdx); 
		HSSFRow hssfHeader1 = sheet.createRow(rowIdx);// encabezado
		HSSFCell celdaLinea1 = hssfHeader.createCell(cellIdx);

		double contadorFinalPeriodo = 0.00;
		double subtotalPeriodo=0.00; 
		double subtotalPeriodoIngresoEgresos=0.00;
		int posicionSaldo;
		String columnaMes = "";
		String fecInicial = fechaIni, fecFinal = fechaFin, mes;
		int ntNumMonths = 0;

		int noEmp = Integer.parseInt(noEmpresa); 
		celdaLinea.setCellValue(new HSSFRichTextString("Grupo Empresa: " + grupo));
		sheet.autoSizeColumn(cellIdx,true);
		 
		if (noEmp != 0) { 
			rowIdx = rowIdx + 1;
			hssfHeader = sheet.createRow(rowIdx);
			celdaLinea = hssfHeader.createCell(cellIdx); 
			celdaLinea.setCellValue(new HSSFRichTextString("Empresa: " + empresa));
			sheet.autoSizeColumn(cellIdx,true);
		}

		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx);
		celdaLinea = hssfHeader.createCell(cellIdx);  
		celdaLinea.setCellStyle(reporteEstilo); 
		celdaLinea.setCellValue(new HSSFRichTextString("Reporte: " + reporte));
		sheet.autoSizeColumn(cellIdx,true);

		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx);
		celdaLinea = hssfHeader.createCell(cellIdx); 
		celdaLinea.setCellValue(new HSSFRichTextString("Fecha"));
		sheet.autoSizeColumn(cellIdx,true);
		ntNumMonths = getMonthsBetweenTwoDatess(fecInicial, fecFinal);
		fecInicial = getDatesOfMonth(fecInicial, fecFinal, 1);
		fecFinal = getDatesOfMonth(fecInicial, fecFinal, 2);
		mes = subGetMonthOfDateInLetter(fecFinal);

		cellIdx = cellIdx + 1;
		celdaLinea = hssfHeader.createCell(cellIdx); 
		celdaLinea.setCellValue(new HSSFRichTextString(mes.toUpperCase()+" - Ajustado"));
		sheet.autoSizeColumn(cellIdx,true);
		mesPrimero = mes.toUpperCase();

		int primero = cellIdx;
		Map<String, Integer> posicionMes = new HashMap<String, Integer>();
		posicionMes.put(mesPrimero, 1); 
		

		for (int i = 1; i <= ntNumMonths - 1; i++) {
			fecInicial = getDatesOfMonth(fecInicial, fecFinal, 3);
			fecFinal = getDatesOfMonth(fecInicial, fecFinal, 4);
			mes = subGetMonthOfDateInLetter(fecFinal);
			mes = mes.toUpperCase();
			cellIdx = cellIdx + 1;
			celdaLinea = hssfHeader.createCell(cellIdx); 
			celdaLinea.setCellValue(new HSSFRichTextString(mes+" - Ajustado"));
			sheet.autoSizeColumn(cellIdx,true);
			posicionMes.put(mes, cellIdx);
		}
		
		boolean pasa=false;
		double[] subtotal = new double[ntNumMonths+1];
		double[] subtotalHor = new double[ntNumMonths+1];
		double[] totalIngresosEgresos= new double[ntNumMonths+1];
		double[] totalIngresosEgresos2= new double[ntNumMonths+1];
		double[] saldosAcomulado=new double[ntNumMonths+1];
		int[] posicionCelda=new int[ntNumMonths+1];
		double[] cantSaldo=new double[ntNumMonths+1];
		for (int i = 0; i == ntNumMonths; i++) {
			subtotal[i] = 0;
			subtotalHor[i] = 0;
			totalIngresosEgresos[i]=0;
			totalIngresosEgresos2[i]=0;
			saldosAcomulado[i]=0;
			cantSaldo[i]=0;
		}

		cellIdx = cellIdx + 1;
		posicionMes.put("TOTAL", cellIdx); 
		celdaLinea = hssfHeader.createCell(cellIdx); 
		celdaLinea.setCellValue(new HSSFRichTextString("Total periodo"));
		sheet.autoSizeColumn(cellIdx,true);

		cellIdx = 0;
		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx); 
		celdaLinea = hssfHeader.createCell(cellIdx); 
		celdaLinea.setCellValue(new HSSFRichTextString("Conceptos"));
		sheet.autoSizeColumn(cellIdx,true);
		
		rowIdx = rowIdx + 1;
		//inmoviliza columna,fila
		 sheet.createFreezePane (1, 5);
		int filaSaldo=rowIdx;
		int celdaSaldo=cellIdx;
		
		cantSaldo[0]=x;
		rowIdx=filaSaldo;
		cellIdx=celdaSaldo;
		
		cellIdx = 0;
		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx); 
		celdaLinea = hssfHeader.createCell(cellIdx); 
		celdaLinea.setCellStyle(cellStyleI);
		celdaLinea.setCellValue(new HSSFRichTextString("Ingresos"));
		sheet.autoSizeColumn(cellIdx,true);

		List<ConceptoFlujo> conceptos;
		String idTipoMovto = "I";
		int empresaM = Integer.parseInt(noEmpresa);
		int NogrupoM = Integer.parseInt(noGrupo);
		List<TotalConcepto> totalesConcepto = cashFlowDao.funSqlGetTotalesXMesForConceptoAjustadoExcel(IdDivisa,
				NoUsuario, mes, mes);

		Iterator it = totalesConcepto.iterator();

		String encabezadoAnterior = " ", descLargaAnterior = " ", tipoMovimiento = "";
		boolean totalMovimiento = true;
		int iPos = 0;

		while (it.hasNext()) {
			TotalConcepto totalConcepto = (TotalConcepto) it.next();
			tipoMovimiento = totalConcepto.getIngresoegreso();
			String mess = totalConcepto.getMes();

			if (tipoMovimiento.equals("I")) {
				if (encabezadoAnterior.equals(" ")) {
					cellIdx = 0;
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellStyle(styleGroup2);
					celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDescripcion().toString()));
					sheet.autoSizeColumn(cellIdx,true);
					System.out.println(totalConcepto.getDescripcion().toString());

					// Se crea la descripcion larga
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx); 
					celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString()));
					sheet.autoSizeColumn(cellIdx,true);
					
					for (int i = 1; i <= subtotal.length-1; i++) {
						celdaLinea = hssfHeader.createCell(i);
						celdaLinea.setCellValue(new HSSFRichTextString(0.00+""));  
						celdaLinea.setCellStyle(alignText);
						}

					// Se crea el primer mes
					cellIdx = posicionMes.get(totalConcepto.getMes().toUpperCase()); 
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
					sheet.autoSizeColumn(cellIdx,true);
					celdaLinea.setCellStyle(alignText);
					
					// Se va sumando el subtotal
					subtotal[cellIdx] = Double.parseDouble(totalConcepto.getTotal() + "");
					subtotalHor[cellIdx] = Double.parseDouble(totalConcepto.getTotal() + ""); 
					descLargaAnterior = totalConcepto.getDesc_larga().toString();
					
				} else if (encabezadoAnterior.equals(totalConcepto.getDescripcion().toString())) {
					if (descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())) {
						// Se crea el primer mes
						cellIdx = posicionMes.get(totalConcepto.getMes().toUpperCase());
						celdaLinea = hssfHeader.createCell(cellIdx);
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
						celdaLinea.setCellStyle(alignText);
						sheet.autoSizeColumn(cellIdx,true);
						descLargaAnterior = totalConcepto.getDesc_larga().toString();

						// Se va sumando el subtotal
						subtotal[cellIdx] += Double.parseDouble(totalConcepto.getTotal() + "");
						subtotalHor[cellIdx] += Double.parseDouble(totalConcepto.getTotal() + "");

					} else {
						// Se calcula y se pinta el total de la descripcion
						// larga anterior
						cellIdx=posicionMes.get("TOTAL");
						celdaLinea = hssfHeader.createCell(cellIdx);
						for (int i = 1; i <= subtotalHor.length-1; i++) {
							contadorFinalPeriodo += subtotalHor[i];
							subtotalHor[i] = 0;
						}
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(contadorFinalPeriodo)));
						celdaLinea.setCellStyle(alignText);
						sheet.autoSizeColumn(cellIdx,true);
						contadorFinalPeriodo = 0.00;

						// Se pinta el nuevo concepto 
						cellIdx = 0;
						rowIdx = rowIdx + 1;
						hssfHeader = sheet.createRow(rowIdx);
						celdaLinea = hssfHeader.createCell(cellIdx); 
						celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString()));
						sheet.autoSizeColumn(cellIdx,true);
						System.out.println(totalConcepto.getDesc_larga().toString());
						
						for (int i = 1; i <= subtotal.length-1; i++) {
							celdaLinea = hssfHeader.createCell(i);
							celdaLinea.setCellValue(new HSSFRichTextString(0.00+"")); 
							celdaLinea.setCellStyle(alignText); 
							}
						
						cellIdx = posicionMes.get(totalConcepto.getMes().toUpperCase());
						celdaLinea = hssfHeader.createCell(cellIdx);
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
						celdaLinea.setCellStyle(alignText);
						sheet.autoSizeColumn(cellIdx,true);
						System.out.println(formateador.format(totalConcepto.getTotal()));
						 

						// Se va sumando el subtotal
						subtotal[cellIdx] += Double.parseDouble(totalConcepto.getTotal() + "");
						subtotalHor[cellIdx] += Double.parseDouble(totalConcepto.getTotal() + "");
						descLargaAnterior = totalConcepto.getDesc_larga().toString();
					}
				} else {
					
					if(pasa==true){
					cellIdx = 0;
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellStyle(styleGroup2);
					celdaLinea.setCellValue(new HSSFRichTextString("SUBTOTAL- " + encabezadoAnterior));
					sheet.autoSizeColumn(cellIdx,true);

					for (int i = 1; i <= subtotal.length-1; i++) {
						celdaLinea = hssfHeader.createCell(i);
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotal[i])));
						celdaLinea.setCellStyle(alignText);
						sheet.autoSizeColumn(i,true);
						subtotalPeriodo+=subtotal[i];  
						totalIngresosEgresos2[i]+=subtotal[i]; 
						subtotal[i] = 0;
					}
					cellIdx=posicionMes.get("TOTAL");
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalPeriodo)));
					celdaLinea.setCellStyle(alignText);
					sheet.autoSizeColumn(cellIdx,true);
					subtotalPeriodo=0.00;
					contadorFinalPeriodo = 0.00;
					}
					pasa=true;
					cellIdx = 0;
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellStyle(styleGroup2);
					celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDescripcion().toString()));
					sheet.autoSizeColumn(cellIdx,true);

					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx); 
					celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString()));
					sheet.autoSizeColumn(cellIdx,true);

					for (int i = 1; i <= subtotal.length-1; i++) {
						celdaLinea = hssfHeader.createCell(i);
						celdaLinea.setCellValue(new HSSFRichTextString(0.00+""));  
						}
					
					descLargaAnterior = totalConcepto.getDesc_larga().toString();
					cellIdx = posicionMes.get(totalConcepto.getMes().toUpperCase());
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
					celdaLinea.setCellStyle(alignText);
					sheet.autoSizeColumn(cellIdx,true);
					// Sumar el subtotal
					subtotal[cellIdx] = Double.parseDouble(totalConcepto.getTotal() + "");
					subtotalHor[cellIdx] += Double.parseDouble(totalConcepto.getTotal() + "");
				}
				 
				encabezadoAnterior = totalConcepto.getDescripcion().toString();
				
			}
			//empieza la condicion del egreso
			else{
			  if(totalMovimiento){
				  
				  cellIdx=posicionMes.get("TOTAL");
					celdaLinea = hssfHeader.createCell(cellIdx); 
					for (int i = 1; i <= subtotalHor.length-1; i++) { 
						contadorFinalPeriodo += subtotalHor[i];
						subtotalHor[i] = 0;
					}
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(contadorFinalPeriodo)));
					celdaLinea.setCellStyle(alignText);
					sheet.autoSizeColumn(cellIdx,true);
					contadorFinalPeriodo = 0.00;
				  
				  
				  
				  cellIdx = 0;
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellStyle(styleGroup2);
					celdaLinea.setCellValue(new HSSFRichTextString("SUBTOTAL- " + encabezadoAnterior));
					sheet.autoSizeColumn(cellIdx,true);
					
					for (int i = 1; i <= subtotal.length-1; i++) {
						celdaLinea = hssfHeader.createCell(i);
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotal[i])));
						sheet.autoSizeColumn(i,true);;
						celdaLinea.setCellStyle(alignText);
						//suma total de periodo
						subtotalPeriodo+=subtotal[i];
						//accede a subtotal de saldo ingresos
						totalIngresosEgresos[i]+=subtotal[i];
						subtotal[i] = 0;
					}
					
					cellIdx=posicionMes.get("TOTAL");
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalPeriodo)));
					sheet.autoSizeColumn(cellIdx,true);
					celdaLinea.setCellStyle(alignText);
					subtotalPeriodo=0.00;

				  rowIdx=rowIdx+1; 
				  cellIdx=0;
				  rowIdx=rowIdx+1;
				  hssfHeader = sheet.createRow(rowIdx);
				  celdaLinea = hssfHeader.createCell(cellIdx); 
				  celdaLinea.setCellStyle(styleGroup3);
				  celdaLinea.setCellValue(new HSSFRichTextString("TOTAL INGRESOS"));
				  sheet.autoSizeColumn(cellIdx,true);
				  
				  
				  for (int i = 1; i <= totalIngresosEgresos.length-1; i++) {
						celdaLinea = hssfHeader.createCell(i);
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalIngresosEgresos[i])));
						celdaLinea.setCellStyle(alignText);
						sheet.autoSizeColumn(i,true);
						subtotalPeriodoIngresoEgresos+=totalIngresosEgresos[i];
					}
				  
				  cellIdx=posicionMes.get("TOTAL");
				  celdaLinea = hssfHeader.createCell(cellIdx);
				  celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalPeriodoIngresoEgresos)));
				  celdaLinea.setCellStyle(alignText);
				  sheet.autoSizeColumn(cellIdx,true);
				  subtotalPeriodoIngresoEgresos=0.00;
				  
				  rowIdx=rowIdx+1;
				  cellIdx=0; 
				  rowIdx=rowIdx+1; 
				  hssfHeader = sheet.createRow(rowIdx);   
				  celdaLinea = hssfHeader.createCell(cellIdx);  
				  celdaLinea.setCellStyle(cellStyleI);
				  sheet.autoSizeColumn(cellIdx,true);
				  celdaLinea.setCellValue(new HSSFRichTextString("EGRESOS")); 
				  totalMovimiento=false; 
			 }
			  if (encabezadoAnterior.equals(totalConcepto.getDescripcion().toString())) {
					if (descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())) {
						// Se crea el primer mes
						cellIdx = posicionMes.get(totalConcepto.getMes().toUpperCase());
						celdaLinea = hssfHeader.createCell(cellIdx);
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
						celdaLinea.setCellStyle(alignText);
						sheet.autoSizeColumn(cellIdx,true);
						descLargaAnterior = totalConcepto.getDesc_larga().toString();

						// Se va sumando el subtotal
						subtotal[cellIdx] += Double.parseDouble(totalConcepto.getTotal() + "");
						subtotalHor[cellIdx] += Double.parseDouble(totalConcepto.getTotal() + "");

					} else {
						// Se calcula y se pinta el total de la descripcion
						// larga anterior
						cellIdx=posicionMes.get("TOTAL");
						celdaLinea = hssfHeader.createCell(cellIdx);
						for (int i = 1; i <= subtotalHor.length-1; i++) {
							contadorFinalPeriodo += subtotalHor[i];
							subtotalHor[i] = 0;
						}
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(contadorFinalPeriodo)));
						celdaLinea.setCellStyle(alignText);
						sheet.autoSizeColumn(cellIdx,true);
						contadorFinalPeriodo = 0.00;

						// Se pinta el nuevo concepto 
						cellIdx = 0;
						rowIdx = rowIdx + 1;
						hssfHeader = sheet.createRow(rowIdx);
						celdaLinea = hssfHeader.createCell(cellIdx); 
						celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString()));
						sheet.autoSizeColumn(cellIdx,true);
						System.out.println(totalConcepto.getDesc_larga().toString());
						
						for (int i = 1; i <= subtotal.length-1; i++) {
							celdaLinea = hssfHeader.createCell(i);
							celdaLinea.setCellValue(new HSSFRichTextString(0.00+"")); 
							celdaLinea.setCellStyle(alignText); 
							}
						
						cellIdx = posicionMes.get(totalConcepto.getMes().toUpperCase());
						celdaLinea = hssfHeader.createCell(cellIdx);
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
						celdaLinea.setCellStyle(alignText);
						sheet.autoSizeColumn(cellIdx,true);
						System.out.println(formateador.format(totalConcepto.getTotal()));
						 

						// Se va sumando el subtotal
						subtotal[cellIdx] += Double.parseDouble(totalConcepto.getTotal() + "");
						subtotalHor[cellIdx] += Double.parseDouble(totalConcepto.getTotal() + "");
						descLargaAnterior = totalConcepto.getDesc_larga().toString();
					}
				} else {
					
					if(pasa==true){
					cellIdx = 0;
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellStyle(styleGroup2);
					celdaLinea.setCellValue(new HSSFRichTextString("SUBTOTAL- " + encabezadoAnterior));
					sheet.autoSizeColumn(cellIdx,true);

					for (int i = 1; i <= subtotal.length-1; i++) {
						celdaLinea = hssfHeader.createCell(i);
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotal[i])));
						celdaLinea.setCellStyle(alignText);
						sheet.autoSizeColumn(i,true);
						subtotalPeriodo+=subtotal[i];  
						totalIngresosEgresos2[i]+=subtotal[i]; 
						subtotal[i] = 0;
					}
					cellIdx=posicionMes.get("TOTAL");
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalPeriodo)));
					celdaLinea.setCellStyle(alignText);
					sheet.autoSizeColumn(cellIdx,true);
					subtotalPeriodo=0.00;
					contadorFinalPeriodo = 0.00;
					}
					pasa=true;
					cellIdx = 0;
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellStyle(styleGroup2);
					celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDescripcion().toString()));
					sheet.autoSizeColumn(cellIdx,true);

					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx); 
					celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString()));
					sheet.autoSizeColumn(cellIdx,true);

					for (int i = 1; i <= subtotal.length-1; i++) {
						celdaLinea = hssfHeader.createCell(i);
						celdaLinea.setCellValue(new HSSFRichTextString(0.00+""));  
						}
					
					descLargaAnterior = totalConcepto.getDesc_larga().toString();
					cellIdx = posicionMes.get(totalConcepto.getMes().toUpperCase());
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
					celdaLinea.setCellStyle(alignText);
					sheet.autoSizeColumn(cellIdx,true);
					// Sumar el subtotal
					subtotal[cellIdx] = Double.parseDouble(totalConcepto.getTotal() + "");
					subtotalHor[cellIdx] += Double.parseDouble(totalConcepto.getTotal() + "");
				}
			
			 
			cellIdx=posicionMes.get("TOTAL");
			celdaLinea = hssfHeader.createCell(cellIdx);
			sheet.autoSizeColumn(cellIdx,true);
			for (int i = 1; i <= subtotalHor.length-1; i++) { 
				contadorFinalPeriodo += subtotalHor[i];
				subtotalHor[i] = 0;
			}
			celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(contadorFinalPeriodo)));
			celdaLinea.setCellStyle(alignText);
			sheet.autoSizeColumn(cellIdx,true);
			encabezadoAnterior = totalConcepto.getDescripcion().toString();
			}
		}
		cellIdx = 0;
		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx);
		celdaLinea = hssfHeader.createCell(cellIdx);
		celdaLinea.setCellStyle(styleGroup2);
		celdaLinea.setCellValue(new HSSFRichTextString("SUBTOTAL" + encabezadoAnterior));
		sheet.autoSizeColumn(cellIdx,true);
		 
		for (int i = 1; i <= subtotal.length-1; i++) {
			celdaLinea = hssfHeader.createCell(i);
			celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotal[i])));
			celdaLinea.setCellStyle(alignText);
			sheet.autoSizeColumn(i,true);
			subtotalPeriodo+=subtotal[i]; 
			//accede a subtotal de saldo ingresos
			totalIngresosEgresos2[i]+=subtotal[i];
			subtotal[i] = 0;
		}
		cellIdx=posicionMes.get("TOTAL");
		celdaLinea = hssfHeader.createCell(cellIdx);
		celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalPeriodo)));
		sheet.autoSizeColumn(cellIdx,true);
		celdaLinea.setCellStyle(alignText);
		subtotalPeriodo=0.00;
		
		cellIdx=posicionMes.get("TOTAL");
		celdaLinea = hssfHeader.createCell(cellIdx);
		for (int i = 1; i <= subtotalHor.length-1; i++) { 
			contadorFinalPeriodo += subtotalHor[i];
			subtotalHor[i] = 0;
		}
		celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(contadorFinalPeriodo)));
		sheet.autoSizeColumn(cellIdx,true);
		celdaLinea.setCellStyle(alignText);
		
		rowIdx = rowIdx + 1;
		cellIdx = 0;
		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx);
		celdaLinea = hssfHeader.createCell(cellIdx); 
		celdaLinea.setCellStyle(styleGroup3);
		celdaLinea.setCellValue(new HSSFRichTextString("TOTAL EGRESOS"));
		sheet.autoSizeColumn(cellIdx,true);
		
		for (int i = 1; i <= totalIngresosEgresos2.length-1; i++) {
			celdaLinea = hssfHeader.createCell(i);
			celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalIngresosEgresos2[i])));
			sheet.autoSizeColumn(i,true);
			celdaLinea.setCellStyle(alignText);
			subtotalPeriodoIngresoEgresos+=totalIngresosEgresos2[i];
		}
		
		cellIdx=posicionMes.get("TOTAL");
		celdaLinea = hssfHeader.createCell(cellIdx);
		celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalPeriodoIngresoEgresos)));
		celdaLinea.setCellStyle(alignText);
		sheet.autoSizeColumn(cellIdx,true);
		subtotalPeriodoIngresoEgresos=0.00;

		rowIdx = rowIdx + 1;
		cellIdx = 0;
		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx);
		celdaLinea = hssfHeader.createCell(cellIdx);
		celdaLinea.setCellStyle(styleGroup3);
		celdaLinea.setCellValue(new HSSFRichTextString("SALDO OPERATIVO ACOMULADO"));
		sheet.autoSizeColumn(cellIdx,true);
		double acomulado=0.00;
		double acomuladoFinal=0.00;
		
		for (int i = 1; i <= totalIngresosEgresos.length-1; ) { 		
			for (int j = 1; j <= totalIngresosEgresos2.length-1; j++) {
				acomulado=totalIngresosEgresos[i]-totalIngresosEgresos2[j];
				celdaLinea = hssfHeader.createCell(i);
				celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(acomulado))); 
				sheet.autoSizeColumn(i,true);
				celdaLinea.setCellStyle(alignText);
				if(acomulado < -0){ 
					celdaLinea.setCellStyle(colorLetra);
				} 
				saldosAcomulado[i]=	acomulado;	
				acomuladoFinal+=acomulado;
				acomulado=0.00;
				i++;
			}
		}
		cellIdx=posicionMes.get("TOTAL");
		celdaLinea = hssfHeader.createCell(cellIdx);
		celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(acomuladoFinal)));
		celdaLinea.setCellStyle(alignText);
		sheet.autoSizeColumn(cellIdx,true);
		acomuladoFinal=0.00;
		
		rowIdx = rowIdx + 1;

		cellIdx = 0;
		rowIdx = rowIdx + 1;
		hssfHeader1 = sheet.createRow(rowIdx);
		celdaLinea1 = hssfHeader1.createCell(cellIdx); 
		celdaLinea1.setCellStyle(styleGroup3);
		celdaLinea1.setCellValue(new HSSFRichTextString("SALDO DISPONIBLE"));
		sheet.autoSizeColumn(cellIdx,true);
		boolean primeraVez=true;
		double y=0.00;
		int ultima=0;
		for (int i = 1; i <= saldosAcomulado.length-1; i++) { 	
			if(primeraVez){
				y=x+saldosAcomulado[i];
				celdaLinea1 = hssfHeader1.createCell(i);
				celdaLinea1.setCellValue(new HSSFRichTextString(formateador.format(y)));	
				sheet.autoSizeColumn(i,true);
				celdaLinea.setCellStyle(alignText);
				primeraVez=false;
				cantSaldo[i]=y;
			
			}else{  
				
				y=y+saldosAcomulado[i];
				celdaLinea1 = hssfHeader1.createCell(i);
				celdaLinea1.setCellValue(new HSSFRichTextString(formateador.format(y)));
				sheet.autoSizeColumn(i,true);
				celdaLinea.setCellStyle(alignText);
				cantSaldo[i]=y;
				ultima=i;
			}
		}
		cantSaldo[ultima]=x;
		
		
		cellIdx=posicionMes.get("TOTAL");
		celdaLinea1 = hssfHeader1.createCell(cellIdx);
		celdaLinea1.setCellValue(new HSSFRichTextString(formateador.format(y)));
		celdaLinea.setCellStyle(alignText);	
		sheet.autoSizeColumn(cellIdx,true);
		
		hssfHeader = sheet.createRow(filaSaldo); 
		celdaLinea = hssfHeader.createCell(celdaSaldo); 
		celdaLinea.setCellStyle(styleGroup1);
		celdaLinea.setCellValue(new HSSFRichTextString("SALDO INICIAL"));
		sheet.autoSizeColumn(cellIdx,true);
		
		for (int i = 0; i <= cantSaldo.length-1; i++) { 	
			celdaSaldo += 1;
			celdaLinea = hssfHeader.createCell(celdaSaldo); 
			celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(cantSaldo[i])));	
			sheet.autoSizeColumn(i,true);
			celdaLinea.setCellStyle(alignText);
		}
	
		 
		return wb;
	}
	
	public HSSFWorkbook generarExcelRA(float x, String noEmpresa, String empresa, String reporte, String grupo,
			int usuario, String fechaIni, String fechaFin, String noGrupo, String IdDivisa) {
 		FechaHoy = getFechasSistemaBusiness("HOY");
		String mesPrimero = "";
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		HSSFWorkbook wb = new HSSFWorkbook();// genera archivo
		HSSFSheet sheet = wb.createSheet();// genera hojas
		
		HSSFCellStyle reporteEstilo = wb.createCellStyle();
		reporteEstilo.setAlignment(HSSFCellStyle.ALIGN_FILL); 
		
		HSSFFont font = wb.createFont();
		font.setColor(HSSFColor.RED.index);
		HSSFCellStyle colorLetra = wb.createCellStyle();
		colorLetra.setFont(font);
		
		HSSFCellStyle styleGroup2 = wb.createCellStyle();
		styleGroup2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleGroup2.setFillForegroundColor(HSSFColor.YELLOW.index);
		
		HSSFCellStyle cellStyleI = wb.createCellStyle();	 
		cellStyleI.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		
		cellStyleI.setFillForegroundColor(new HSSFColor.BLUE().getIndex());
		
		HSSFCellStyle styleGroup3 = wb.createCellStyle();
		styleGroup3.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleGroup3.setFillForegroundColor(HSSFColor.RED.index);
		
		HSSFCellStyle styleGroup1 = wb.createCellStyle();
		styleGroup1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleGroup1.setBorderRight((short) 1);
		styleGroup1.setFillForegroundColor(HSSFColor.GREEN.index);
		 
		HSSFCellStyle alignText = wb.createCellStyle();
		alignText.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		alignText.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);
		
		int rowIdx = 0;// renglones
		int cellIdx = 0;// filas 
		
		HSSFRow hssfHeader = sheet.createRow(rowIdx);// encabezado
		HSSFCell celdaLinea = hssfHeader.createCell(cellIdx);
		
		HSSFRow hssfHeader1 = sheet.createRow(rowIdx);// encabezado
		HSSFCell celdaLinea1 = hssfHeader.createCell(cellIdx);
		Map<String, Integer> posicionMesTipo = new HashMap<String, Integer>();
		double contadorFinalPeriodo = 0.00;
		double subtotalPeriodo=0.00; 
		double subtotalPeriodoIngresoEgresos=0.00;
		int posicionSaldo;
		String columnaMes = "";
		String fecInicial = fechaIni, fecFinal = fechaFin, mes;
		String fecInicialAux="",fecFinalAux="",tipoInfo="";
		int ntNumMonths = 0;
		int ntCiclos=2;
		
		int noEmp = Integer.parseInt(noEmpresa); 
		celdaLinea.setCellValue(new HSSFRichTextString("Grupo Empresa: " + grupo));
		 
		if (noEmp != 0) { 
			rowIdx = rowIdx + 1;
			hssfHeader = sheet.createRow(rowIdx);
			celdaLinea = hssfHeader.createCell(cellIdx); 
			celdaLinea.setCellValue(new HSSFRichTextString("Empresa: " + empresa));
		}

		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx);
		celdaLinea = hssfHeader.createCell(cellIdx);  
		celdaLinea.setCellStyle(reporteEstilo);
		celdaLinea.setCellValue(new HSSFRichTextString("Reporte: " + reporte));
		sheet.autoSizeColumn(cellIdx,true);

		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx);
		celdaLinea = hssfHeader.createCell(cellIdx); 
		celdaLinea.setCellValue(new HSSFRichTextString("Fecha"));
		ntNumMonths = getMonthsBetweenTwoDatess(fecInicial, fecFinal);
		fecInicial = getDatesOfMonth(fecInicial, fecFinal, 1);
		fecFinal = getDatesOfMonth(fecInicial, fecFinal, 2);
		mes = subGetMonthOfDateInLetter(fecFinal);
		String mess = mes.toUpperCase();
		Map<String, Integer> posicionMes = new HashMap<String, Integer>();
		Map<String, Integer> tipoMes = new HashMap<String, Integer>();
		mes.toUpperCase(); 
		
		cellIdx = cellIdx + 1;
		celdaLinea = hssfHeader.createCell(cellIdx);
		celdaLinea.setCellValue(new HSSFRichTextString(mes.toUpperCase()+" - Real")); 
		posicionMes.put(mes.toUpperCase() +" Real", cellIdx);   
		sheet.autoSizeColumn(cellIdx,true);
		cellIdx = cellIdx + 1;
		celdaLinea = hssfHeader.createCell(cellIdx); 
		sheet.autoSizeColumn(cellIdx,true);
		celdaLinea.setCellValue(new HSSFRichTextString(mes.toUpperCase()+" - Ajustado"));
		posicionMes.put(mes.toUpperCase() + " Ajustado", cellIdx);  
		sheet.autoSizeColumn(cellIdx,true);
		cellIdx = cellIdx +  1;
		celdaLinea = hssfHeader.createCell(cellIdx); 
		sheet.autoSizeColumn(cellIdx,true);
		celdaLinea.setCellValue(new HSSFRichTextString(mes.toUpperCase()+" - Diferencia"));
		posicionMes.put(mes.toUpperCase()+"diferencia", cellIdx);  
		sheet.autoSizeColumn(cellIdx,true);
		
		mesPrimero = mes.toUpperCase();  
		for (int i = 1; i <= ntNumMonths - 1; i++) {
			fecInicial = getDatesOfMonth(fecInicial, fecFinal, 3);
			fecFinal = getDatesOfMonth(fecInicial, fecFinal, 4);
			mes = subGetMonthOfDateInLetter(fecFinal);
			mes = mes.toUpperCase();
			cellIdx = cellIdx + 1;
			celdaLinea = hssfHeader.createCell(cellIdx); 
			sheet.autoSizeColumn(cellIdx,true);
			celdaLinea.setCellValue(new HSSFRichTextString(mes+" - Real"));
			posicionMes.put(mes.toUpperCase()+" Real", cellIdx);  
			sheet.autoSizeColumn(cellIdx,true); 
			cellIdx = cellIdx + 1;
			celdaLinea = hssfHeader.createCell(cellIdx);
			sheet.autoSizeColumn(cellIdx,true);
			celdaLinea.setCellValue(new HSSFRichTextString(mes+" - Ajustado"));
			posicionMes.put(mes.toUpperCase() +" Ajustado", cellIdx);  
			sheet.autoSizeColumn(cellIdx,true); 
			cellIdx = cellIdx + 1;
			celdaLinea = hssfHeader.createCell(cellIdx); 
			sheet.autoSizeColumn(cellIdx,true);
			celdaLinea.setCellValue(new HSSFRichTextString(mes+" - Diferencia"));
			posicionMes.put(mes+"diferencia", cellIdx);  
			sheet.autoSizeColumn(cellIdx,true);
		}
		
		cellIdx = cellIdx + 1; 
		celdaLinea = hssfHeader.createCell(cellIdx);  
		celdaLinea.setCellValue(new HSSFRichTextString("Total Real"));
		sheet.autoSizeColumn(cellIdx,true); 
		posicionMes.put("TOTALReal", cellIdx); 
		
		cellIdx = cellIdx + 1;
		celdaLinea = hssfHeader.createCell(cellIdx);  
		celdaLinea.setCellValue(new HSSFRichTextString("Total Ajustado"));
		sheet.autoSizeColumn(cellIdx,true); 
		posicionMes.put("TOTALAjustado", cellIdx); 
		
		cellIdx = cellIdx + 1;
		
		celdaLinea = hssfHeader.createCell(cellIdx);  
		celdaLinea.setCellValue(new HSSFRichTextString("Total Diferencia"));
		sheet.autoSizeColumn(cellIdx,true); 
		posicionMes.put("TOTALDiferencia", cellIdx); 
		//inmoviliza columna,fila
		sheet.createFreezePane (1, 5);
		
		double[] subtotal = new double[posicionMes.size()+1];
		double[] subtotalHor = new double[posicionMes.size()+1];
		double[] subtotalHorAux = new double[posicionMes.size()+1];
		double[] totalIngresos = new double[posicionMes.size()+1];
		double[] totalEgresos = new double[posicionMes.size()+1];
		double[] saldosAcomulado=new double[posicionMes.size()+1];
		int[] posicionCelda=new int[ntNumMonths+1];
		for (int i = 0; i == ntNumMonths; i++) {
			subtotal[i] = 0;
			subtotalHor[i] = 0;
			totalIngresos[i] = 0;
			subtotalHorAux[i] = 0;
			totalIngresos[i] = 0;
			saldosAcomulado[i] = 0;
		}

		
		cellIdx = 0;
		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx); 
		celdaLinea = hssfHeader.createCell(cellIdx);  
		celdaLinea.setCellValue(new HSSFRichTextString("Conceptos"));
		cellIdx = 0;
		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx); 
		celdaLinea = hssfHeader.createCell(cellIdx);  
		celdaLinea.setCellValue(new HSSFRichTextString("INGRESOS"));
		
		
		String tipoMovimiento="",encabezadoAnterior=" ",descLargaAnterior="",mesAnterior="";
		double diferencia=0.00, acomulado = 0.00;
		int posicion=0;
		boolean totalMovimiento = true, pasa = false, pasa1 = false;
		
		List<TotalConcepto> totalesConcepto = cashFlowDao.funSqlGetTotalesXMesForConceptoRealAjustadoExcel(IdDivisa,NoUsuario, mes, mes);
		Iterator it = totalesConcepto.iterator();
		while (it.hasNext()) {
			TotalConcepto totalConcepto = (TotalConcepto) it.next(); 
			tipoMovimiento = totalConcepto.getIngresoegreso();
			if(tipoMovimiento.equals("I")){
			if (encabezadoAnterior.equals(" ")) {
				cellIdx = 0;
				rowIdx = rowIdx + 1;
				hssfHeader = sheet.createRow(rowIdx);
				celdaLinea = hssfHeader.createCell(cellIdx);
				celdaLinea.setCellStyle(styleGroup2);
				celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDescripcion().toString())); 
				
				// Se crea la descripcion larga
				rowIdx = rowIdx + 1;
				hssfHeader = sheet.createRow(rowIdx);
				celdaLinea = hssfHeader.createCell(cellIdx); 
				celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString()));
				
				cellIdx = posicionMes.get((totalConcepto.getMes().toString().toUpperCase()+" "+totalConcepto.getIdentificador().toString()));
				celdaLinea = hssfHeader.createCell(cellIdx); 
				celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
				celdaLinea.setCellStyle(alignText);
				posicion = posicionMes.get("TOTAL"+totalConcepto.getIdentificador()) ;
				
				subtotalHorAux[posicion] = totalConcepto.getTotal(); 
				subtotalHor[cellIdx] = totalConcepto.getTotal();
				
				subtotal[cellIdx] += totalConcepto.getTotal();
				subtotal[posicion] += totalConcepto.getTotal();
				
				
				mesAnterior=totalConcepto.getMes().toUpperCase();
			 	descLargaAnterior = totalConcepto.getDesc_larga().toString();
				
			}  else if (encabezadoAnterior.equals(totalConcepto.getDescripcion().toString())) {
				if (descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())) { 						
					if(!mesAnterior.equals(totalConcepto.getMes().toUpperCase()) || !descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())){
						
						for(int i=1;i<=subtotalHor.length-1;i++){
							diferencia =  subtotalHor[i] - diferencia;
							subtotalHor[i]=0.00;
						} 
						cellIdx=posicionMes.get(mesAnterior+"diferencia");
						celdaLinea = hssfHeader.createCell(cellIdx); 
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(diferencia)));
						celdaLinea.setCellStyle(alignText);
						sheet.autoSizeColumn(cellIdx,true);  
						
						posicion = posicionMes.get("TOTALDiferencia") ;
						subtotalHorAux[posicion] += diferencia;
						subtotal[posicion] += diferencia;
						diferencia = 0.00;						
					 } 
					cellIdx=posicionMes.get((totalConcepto.getMes().toString().toUpperCase()+" "+totalConcepto.getIdentificador().toString()));
					celdaLinea = hssfHeader.createCell(cellIdx); 
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal()))); 
					celdaLinea.setCellStyle(alignText);
					posicion = posicionMes.get("TOTAL"+totalConcepto.getIdentificador()) ;
					
					subtotalHorAux[posicion] += totalConcepto.getTotal(); 
					subtotalHor[cellIdx]= totalConcepto.getTotal();
					
					subtotal[cellIdx] += totalConcepto.getTotal();
					subtotal[posicion] += totalConcepto.getTotal();
					
					mesAnterior=totalConcepto.getMes().toUpperCase();
					
				} else {

					if(!mesAnterior.equals(totalConcepto.getMes().toUpperCase()) || !descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())){
						
						for(int i=1;i<=subtotalHor.length-1;i++){
							diferencia =  subtotalHor[i] - diferencia;
							subtotalHor[i]=0.00;
						} 
						cellIdx=posicionMes.get(mesAnterior+"diferencia");
						celdaLinea = hssfHeader.createCell(cellIdx); 
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(diferencia)));
						celdaLinea.setCellStyle(alignText);
						sheet.autoSizeColumn(cellIdx,true);  
						 
						posicion = posicionMes.get("TOTALDiferencia") ; 
						subtotalHorAux[posicion] += diferencia; 
						subtotal[posicion] += diferencia;
						diferencia = 0.00;
						posicion = posicionMes.get("TOTALReal");
						
						for(int i = posicion; i <= subtotalHorAux.length-1; i++ ){
							cellIdx = i;
							celdaLinea = hssfHeader.createCell(cellIdx);  
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHorAux[i])));
							celdaLinea.setCellStyle(alignText);
							subtotalHorAux[i] = 0.00;
						}
						
					 } 
					
					cellIdx = 0;
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx); 
					celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString()));
					
					cellIdx=posicionMes.get((totalConcepto.getMes().toString().toUpperCase()+" "+totalConcepto.getIdentificador().toString()));
					celdaLinea = hssfHeader.createCell(cellIdx); 
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
					celdaLinea.setCellStyle(alignText);
					posicion = posicionMes.get("TOTAL"+totalConcepto.getIdentificador()) ;
					
					subtotalHorAux[posicion] += totalConcepto.getTotal(); 
					subtotalHor[cellIdx] = totalConcepto.getTotal();
					
					subtotal[cellIdx] += totalConcepto.getTotal();
					subtotal[posicion] += totalConcepto.getTotal();
				 
					mesAnterior=totalConcepto.getMes().toUpperCase();
					descLargaAnterior = totalConcepto.getDesc_larga().toString();
				}
			} else {			 
				
				if(!mesAnterior.equals(totalConcepto.getMes().toUpperCase()) || !descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())){
					
					for(int i=1;i<=subtotalHor.length-1;i++){
						diferencia =  subtotalHor[i] - diferencia;
						subtotalHor[i]=0.00;
					} 
					cellIdx=posicionMes.get(mesAnterior+"diferencia");
					celdaLinea = hssfHeader.createCell(cellIdx); 
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(diferencia)));
					celdaLinea.setCellStyle(alignText);
					sheet.autoSizeColumn(cellIdx,true);  
					 
					posicion = posicionMes.get("TOTALDiferencia") ;
					subtotalHorAux[posicion] += diferencia;
					
					subtotal[posicion] += diferencia;
					
					posicion = posicionMes.get("TOTALReal");
					diferencia = 0.00;
					for(int i = posicion; i <= subtotalHorAux.length-1; i++ ){
						cellIdx = i;
						celdaLinea = hssfHeader.createCell(cellIdx);  
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHorAux[i]))); 
						celdaLinea.setCellStyle(alignText);
						subtotalHorAux[i] = 0.00;
					}
					
				 }
				
				
				if(pasa1){
				cellIdx = 0;
				rowIdx = rowIdx + 1;
				hssfHeader = sheet.createRow(rowIdx);
				celdaLinea = hssfHeader.createCell(cellIdx);
				celdaLinea.setCellStyle(styleGroup2);
				celdaLinea.setCellValue(new HSSFRichTextString("SUBTOTAL- " + encabezadoAnterior));


				for (int i = 1; i <= subtotal.length-1; i++) {
					celdaLinea = hssfHeader.createCell(i);
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotal[i])));
					celdaLinea.setCellStyle(alignText);
					sheet.autoSizeColumn(i,true);
					totalIngresos[i] += subtotal[i];
					subtotal[i] = 0;
					
				}
				}
				pasa1=true;
				
				 
				
				// espacio entre los conceptos
				rowIdx = rowIdx + 1;

				cellIdx = 0;
				rowIdx = rowIdx + 1;
				hssfHeader = sheet.createRow(rowIdx);
				celdaLinea = hssfHeader.createCell(cellIdx);
				celdaLinea.setCellStyle(styleGroup2);
				celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDescripcion().toString()));
				

				rowIdx = rowIdx + 1;
				hssfHeader = sheet.createRow(rowIdx);
				celdaLinea = hssfHeader.createCell(cellIdx); 
				celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString()));
				
				cellIdx=posicionMes.get((totalConcepto.getMes().toString().toUpperCase()+" "+totalConcepto.getIdentificador().toString()));
				celdaLinea = hssfHeader.createCell(cellIdx); 
				celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
				celdaLinea.setCellStyle(alignText);
				
				posicion = posicionMes.get("TOTAL"+totalConcepto.getIdentificador()) ;
				
				subtotalHorAux[posicion] += totalConcepto.getTotal(); 
				subtotalHor[cellIdx]= totalConcepto.getTotal();
				
				subtotal[cellIdx] += totalConcepto.getTotal();
				subtotal[posicion] += totalConcepto.getTotal();
				
				mesAnterior=totalConcepto.getMes().toUpperCase();
				descLargaAnterior = totalConcepto.getDesc_larga().toString();

			}
			
			encabezadoAnterior = totalConcepto.getDescripcion().toString(); 
			}else{
				if(totalMovimiento){
					if(!mesAnterior.equals(totalConcepto.getMes().toUpperCase()) || !descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())){
																
						for(int i=1;i<=subtotalHor.length-1;i++){
							diferencia =  subtotalHor[i] - diferencia;
							subtotalHor[i]=0.00;
						} 
						cellIdx=posicionMes.get(mesAnterior+"diferencia");
						celdaLinea = hssfHeader.createCell(cellIdx); 
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(diferencia)));
						celdaLinea.setCellStyle(alignText);
						sheet.autoSizeColumn(cellIdx,true);  
						 
						posicion = posicionMes.get("TOTALDiferencia") ;
						subtotalHorAux[posicion] += diferencia; 
						subtotal[posicion] += diferencia; 
						posicion = posicionMes.get("TOTALReal");
						diferencia = 0.00;
						for(int i = posicion; i <= subtotalHorAux.length-1; i++ ){
							cellIdx = i;
							celdaLinea = hssfHeader.createCell(cellIdx);  
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHorAux[i]))); 
							celdaLinea.setCellStyle(alignText);
							subtotalHorAux[i] = 0.00;
						}
						
					} 
					
					cellIdx = 0;
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellStyle(styleGroup2);
					celdaLinea.setCellValue(new HSSFRichTextString("SUBTOTAL- " + encabezadoAnterior)); 
					
					
					for (int i = 1; i <= subtotal.length-1; i++) {
						celdaLinea = hssfHeader.createCell(i);
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotal[i])));
						celdaLinea.setCellStyle(alignText);
						sheet.autoSizeColumn(i,true);
						totalIngresos[i] += subtotal[i];
						subtotal[i] = 0;
						
					}
					
					rowIdx=rowIdx+1; 
					 cellIdx=0;
					 rowIdx=rowIdx+1;
					 hssfHeader = sheet.createRow(rowIdx);
					 celdaLinea = hssfHeader.createCell(cellIdx); 
					 celdaLinea.setCellStyle(styleGroup3);
					 celdaLinea.setCellValue(new HSSFRichTextString("TOTAL INGRESOS"));
					  
					  for (int i = 1; i <= totalIngresos.length-1; i++) {
							celdaLinea = hssfHeader.createCell(i);
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalIngresos[i])));
							celdaLinea.setCellStyle(alignText);
						}
					  
					  rowIdx=rowIdx+1;
					  
					  cellIdx=0; 
					  rowIdx=rowIdx+1; 
					  hssfHeader = sheet.createRow(rowIdx);   
					  celdaLinea = hssfHeader.createCell(cellIdx);  
					  celdaLinea.setCellStyle(cellStyleI);
					  celdaLinea.setCellValue(new HSSFRichTextString("EGRESOS"));
					
					  mesAnterior=totalConcepto.getMes().toUpperCase();
					  descLargaAnterior = totalConcepto.getDesc_larga().toString();
					
					totalMovimiento = false;
				}
					if (encabezadoAnterior.equals(totalConcepto.getDescripcion().toString())) {
						if (descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())) { 						
							if(!mesAnterior.equals(totalConcepto.getMes().toUpperCase()) || !descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())){
								
								for(int i=1;i<=subtotalHor.length-1;i++){
									diferencia =  subtotalHor[i] - diferencia;
									subtotalHor[i]=0.00;
								} 
								cellIdx=posicionMes.get(mesAnterior+"diferencia");
								celdaLinea = hssfHeader.createCell(cellIdx); 
								celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(diferencia)));
								celdaLinea.setCellStyle(alignText);
								sheet.autoSizeColumn(cellIdx,true);  
								
								posicion = posicionMes.get("TOTALDiferencia") ;
								subtotalHorAux[posicion] += diferencia;
								subtotal[posicion] += diferencia;
								
								
							 } 
							cellIdx=posicionMes.get((totalConcepto.getMes().toString().toUpperCase()+" "+totalConcepto.getIdentificador().toString()));
							celdaLinea = hssfHeader.createCell(cellIdx); 
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal()))); 
							celdaLinea.setCellStyle(alignText);
							
							posicion = posicionMes.get("TOTAL"+totalConcepto.getIdentificador()) ;
							
							subtotalHorAux[posicion] += totalConcepto.getTotal(); 
							subtotalHor[cellIdx]= totalConcepto.getTotal();
							
							subtotal[cellIdx] += totalConcepto.getTotal();
							subtotal[posicion] += totalConcepto.getTotal();
							
							mesAnterior=totalConcepto.getMes().toUpperCase();
							
						} else {

							if(!mesAnterior.equals(totalConcepto.getMes().toUpperCase()) || !descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())){
								
								for(int i=1;i<=subtotalHor.length-1;i++){
									diferencia =  subtotalHor[i] - diferencia;
									subtotalHor[i]=0.00;
								} 
								cellIdx=posicionMes.get(mesAnterior+"diferencia");
								celdaLinea = hssfHeader.createCell(cellIdx); 
								celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(diferencia)));
								celdaLinea.setCellStyle(alignText);
								sheet.autoSizeColumn(cellIdx,true);  
								 
								posicion = posicionMes.get("TOTALDiferencia") ;
								subtotalHorAux[posicion] += diferencia; 
								subtotal[posicion] += diferencia; 
								posicion = posicionMes.get("TOTALReal");
								diferencia = 0.00;
								for(int i = posicion; i <= subtotalHorAux.length-1; i++ ){
									cellIdx = i;
									celdaLinea = hssfHeader.createCell(cellIdx);  
									celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHorAux[i]))); 
									celdaLinea.setCellStyle(alignText);
									subtotalHorAux[i] = 0.00;
								}
								
							 } 
							
							cellIdx = 0;
							rowIdx = rowIdx + 1;
							hssfHeader = sheet.createRow(rowIdx);
							celdaLinea = hssfHeader.createCell(cellIdx); 
							celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString()));
							
							cellIdx=posicionMes.get((totalConcepto.getMes().toString().toUpperCase()+" "+totalConcepto.getIdentificador().toString()));
							celdaLinea = hssfHeader.createCell(cellIdx); 
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
							celdaLinea.setCellStyle(alignText);
							
							posicion = posicionMes.get("TOTAL"+totalConcepto.getIdentificador()) ;
							
							subtotalHorAux[posicion] += totalConcepto.getTotal(); 
							subtotalHor[cellIdx] = totalConcepto.getTotal();
							
							subtotal[cellIdx] += totalConcepto.getTotal();
							subtotal[posicion] += totalConcepto.getTotal();
						 
							mesAnterior=totalConcepto.getMes().toUpperCase();
							descLargaAnterior = totalConcepto.getDesc_larga().toString();
						}
					} else {			 
						
						if(!mesAnterior.equals(totalConcepto.getMes().toUpperCase()) || !descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())){
							
							for(int i=1;i<=subtotalHor.length-1;i++){
								diferencia =  subtotalHor[i] - diferencia;
								subtotalHor[i]=0.00;
							} 
							cellIdx=posicionMes.get(mesAnterior+"diferencia");
							celdaLinea = hssfHeader.createCell(cellIdx); 
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(diferencia)));
							celdaLinea.setCellStyle(alignText);
							sheet.autoSizeColumn(cellIdx,true);  
							 
							posicion = posicionMes.get("TOTALDiferencia") ;
							subtotalHorAux[posicion] += diferencia;
							
							subtotal[posicion] += diferencia;
							
							posicion = posicionMes.get("TOTALReal");
							diferencia = 0.00;
							for(int i = posicion; i <= subtotalHorAux.length-1; i++ ){
								cellIdx = i;
								celdaLinea = hssfHeader.createCell(cellIdx);  
								celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHorAux[i])));
								celdaLinea.setCellStyle(alignText);
								subtotalHorAux[i] = 0.00;
							}
							
						 }
						
						
						if(pasa1){
						cellIdx = 0;
						rowIdx = rowIdx + 1;
						hssfHeader = sheet.createRow(rowIdx);
						celdaLinea = hssfHeader.createCell(cellIdx);
						celdaLinea.setCellStyle(styleGroup2);
						celdaLinea.setCellValue(new HSSFRichTextString("SUBTOTAL- " + encabezadoAnterior));


						for (int i = 1; i <= subtotal.length-1; i++) {
							celdaLinea = hssfHeader.createCell(i);
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotal[i])));
							celdaLinea.setCellStyle(alignText);
							sheet.autoSizeColumn(i,true);
							totalEgresos[i] += subtotal[i];
							subtotal[i] = 0;
							
						}
						}
						pasa1=true;
						
						 
						
						// espacio entre los conceptos
						rowIdx = rowIdx + 1;

						cellIdx = 0;
						rowIdx = rowIdx + 1;
						hssfHeader = sheet.createRow(rowIdx);
						celdaLinea = hssfHeader.createCell(cellIdx);
						celdaLinea.setCellStyle(styleGroup2);
						celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDescripcion().toString()));
						

						rowIdx = rowIdx + 1;
						hssfHeader = sheet.createRow(rowIdx);
						celdaLinea = hssfHeader.createCell(cellIdx); 
						celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString()));
						
						cellIdx=posicionMes.get((totalConcepto.getMes().toString().toUpperCase()+" "+totalConcepto.getIdentificador().toString()));
						celdaLinea = hssfHeader.createCell(cellIdx); 
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
						celdaLinea.setCellStyle(alignText);
						
						posicion = posicionMes.get("TOTAL"+totalConcepto.getIdentificador()) ;
						
						subtotalHorAux[posicion] += totalConcepto.getTotal(); 
						subtotalHor[cellIdx]= totalConcepto.getTotal();
						
						subtotal[cellIdx] += totalConcepto.getTotal();
						subtotal[posicion] += totalConcepto.getTotal();
						
						mesAnterior=totalConcepto.getMes().toUpperCase();
						descLargaAnterior = totalConcepto.getDesc_larga().toString();

					}
					
					encabezadoAnterior = totalConcepto.getDescripcion().toString(); 
				}
			}
				for(int i=1;i<=subtotalHor.length-1;i++){
					diferencia =  subtotalHor[i] - diferencia;
					subtotalHor[i]=0.00;
				} 
				cellIdx=posicionMes.get(mesAnterior+"diferencia");
				celdaLinea = hssfHeader.createCell(cellIdx); 
				celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(diferencia)));
				celdaLinea.setCellStyle(alignText);
				sheet.autoSizeColumn(cellIdx,true);  
				 
				posicion = posicionMes.get("TOTALDiferencia") ;
				subtotalHorAux[posicion] += diferencia;
				
				subtotal[posicion] += diferencia;
				
				posicion = posicionMes.get("TOTALReal");
				diferencia = 0.00;
				for(int i = posicion; i <= subtotalHorAux.length-1; i++ ){
					cellIdx = i;
					celdaLinea = hssfHeader.createCell(cellIdx);  
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHorAux[i]))); 
					celdaLinea.setCellStyle(alignText);
					subtotalHorAux[i] = 0.00;
				}
				
				cellIdx = 0;
				rowIdx = rowIdx + 1;
				hssfHeader = sheet.createRow(rowIdx);
				celdaLinea = hssfHeader.createCell(cellIdx);
				celdaLinea.setCellStyle(styleGroup2);
				celdaLinea.setCellValue(new HSSFRichTextString("SUBTOTAL- " + encabezadoAnterior)); 
			 
				for (int i = 1; i <= subtotal.length-1; i++) {
					celdaLinea = hssfHeader.createCell(i);
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotal[i])));
					celdaLinea.setCellStyle(alignText);
					sheet.autoSizeColumn(i,true);
					totalEgresos[i] += subtotal[i];
					subtotal[i] = 0; 
				}
				  rowIdx=rowIdx+1; 
				  cellIdx=0;
				  rowIdx=rowIdx+1;
				  hssfHeader = sheet.createRow(rowIdx);
				  celdaLinea = hssfHeader.createCell(cellIdx); 
				  celdaLinea.setCellStyle(styleGroup3);
				  celdaLinea.setCellValue(new HSSFRichTextString("TOTAL EGRESOS"));
				  
				  for (int i = 1; i <= totalEgresos.length-1; i++) {
						celdaLinea = hssfHeader.createCell(i);
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalEgresos[i])));
						celdaLinea.setCellStyle(alignText);
					}
				  
				  	rowIdx = rowIdx + 1;
				  	cellIdx = 0;
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellStyle(styleGroup3);
					celdaLinea.setCellValue(new HSSFRichTextString("SALDO OPERATIVO ACOMULADO"));
					
					for (int i = 1; i <= totalIngresos.length-1; ) { 		
						for (int j = 1; j <= totalEgresos.length-1; j++) {
							acomulado=totalIngresos[i]-totalEgresos[j]; 
							celdaLinea = hssfHeader.createCell(i);
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(acomulado))); 
							celdaLinea.setCellStyle(alignText);
							if(acomulado < -0){ 
								celdaLinea.setCellStyle(colorLetra);
							} 
							sheet.autoSizeColumn(i,true); 
							saldosAcomulado[i] = acomulado;
							acomulado=0.00;
							i++;
						}
					}
					
					rowIdx = rowIdx + 1;
				  	cellIdx = 0;
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellStyle(styleGroup3);
					celdaLinea.setCellValue(new HSSFRichTextString("SALDO  DISPONIBLE"));
					
					for(int i = 1; i <= saldosAcomulado.length-1; i++){
						
						
					}
		 
		return wb;
	}
	
	public HSSFWorkbook generarExcelOAR(float x, String noEmpresa, String empresa, String reporte, String grupo,
			int usuario, String fechaIni, String fechaFin, String noGrupo, String IdDivisa) { 
		
		FechaHoy = getFechasSistemaBusiness("HOY");
		String mesPrimero = "";
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		HSSFWorkbook wb = new HSSFWorkbook();// genera archivo
		HSSFSheet sheet = wb.createSheet();// genera hojas
		
		HSSFCellStyle reporteEstilo = wb.createCellStyle();
		reporteEstilo.setAlignment(HSSFCellStyle.ALIGN_FILL); 
		
		HSSFFont font = wb.createFont();
		font.setColor(HSSFColor.RED.index);
		HSSFCellStyle colorLetra = wb.createCellStyle();
		colorLetra.setFont(font);
		
		HSSFCellStyle styleGroup2 = wb.createCellStyle();
		styleGroup2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleGroup2.setFillForegroundColor(HSSFColor.YELLOW.index);
		
		HSSFCellStyle cellStyleI = wb.createCellStyle();	 
		cellStyleI.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		cellStyleI.setFillForegroundColor(new HSSFColor.BLUE().getIndex());
		
		HSSFCellStyle styleGroup3 = wb.createCellStyle();
		styleGroup3.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleGroup3.setFillForegroundColor(HSSFColor.RED.index);
		
		HSSFCellStyle styleGroup1 = wb.createCellStyle();
		styleGroup1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleGroup1.setBorderRight((short) 1);
		styleGroup1.setFillForegroundColor(HSSFColor.GREEN.index);
		
		HSSFCellStyle alingText = wb.createCellStyle();
		alingText.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		alingText.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);
		
		
		int rowIdx = 0;// renglones
		int cellIdx = 0;// filas 
		
		HSSFRow hssfHeader = sheet.createRow(rowIdx);// encabezado
		HSSFCell celdaLinea = hssfHeader.createCell(cellIdx);
		
		HSSFRow hssfHeader1 = sheet.createRow(rowIdx);// encabezado
		HSSFCell celdaLinea1 = hssfHeader.createCell(cellIdx);
		Map<String, Integer> posicionMesTipo = new HashMap<String, Integer>();
		double contadorFinalPeriodo = 0.00;
		double subtotalPeriodo=0.00; 
		double subtotalPeriodoIngresoEgresos=0.00;
		int posicionSaldo;
		String columnaMes = "";
		String fecInicial = fechaIni, fecFinal = fechaFin, mes;
		String fecInicialAux="",fecFinalAux="",tipoInfo="";
		int ntNumMonths = 0;
		int ntCiclos=2;
		
		int noEmp = Integer.parseInt(noEmpresa); 
		celdaLinea.setCellValue(new HSSFRichTextString("Grupo Empresa: " + grupo));
		 
		if (noEmp != 0) { 
			rowIdx = rowIdx + 1;
			hssfHeader = sheet.createRow(rowIdx);
			celdaLinea = hssfHeader.createCell(cellIdx); 
			celdaLinea.setCellValue(new HSSFRichTextString("Empresa: " + empresa));
		}

		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx);
		celdaLinea = hssfHeader.createCell(cellIdx);  
		celdaLinea.setCellStyle(reporteEstilo);
		celdaLinea.setCellValue(new HSSFRichTextString("Reporte: " + reporte));
		sheet.autoSizeColumn(cellIdx,true);

		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx);
		celdaLinea = hssfHeader.createCell(cellIdx); 
		celdaLinea.setCellValue(new HSSFRichTextString("Fecha"));
		ntNumMonths = getMonthsBetweenTwoDatess(fecInicial, fecFinal);
		fecInicial = getDatesOfMonth(fecInicial, fecFinal, 1);
		fecFinal = getDatesOfMonth(fecInicial, fecFinal, 2);
		mes = subGetMonthOfDateInLetter(fecFinal);
		String mess = mes.toUpperCase();
		Map<String, Integer> posicionMes = new HashMap<String, Integer>();
		Map<String, Integer> tipoMes = new HashMap<String, Integer>();
		mes.toUpperCase(); 
		
		cellIdx = cellIdx + 1;
		celdaLinea = hssfHeader.createCell(cellIdx);
		celdaLinea.setCellValue(new HSSFRichTextString(mes.toUpperCase()+" - Original")); 
		posicionMes.put(mes.toUpperCase() +" Original", cellIdx);   
		sheet.autoSizeColumn(cellIdx,true);
		cellIdx = cellIdx + 1;
		celdaLinea = hssfHeader.createCell(cellIdx); 
		sheet.autoSizeColumn(cellIdx,true);
		celdaLinea.setCellValue(new HSSFRichTextString(mes.toUpperCase()+" - Ajustado"));
		posicionMes.put(mes.toUpperCase() + " Ajustado", cellIdx);  
		sheet.autoSizeColumn(cellIdx,true);
		cellIdx = cellIdx +  1;
		celdaLinea = hssfHeader.createCell(cellIdx); 
		sheet.autoSizeColumn(cellIdx,true);
		celdaLinea.setCellValue(new HSSFRichTextString(mes.toUpperCase()+"- Real"));
		posicionMes.put(mes.toUpperCase()+" Real", cellIdx);  
		sheet.autoSizeColumn(cellIdx,true);
		
		mesPrimero = mes.toUpperCase();  
		for (int i = 1; i <= ntNumMonths - 1; i++) {
			fecInicial = getDatesOfMonth(fecInicial, fecFinal, 3);
			fecFinal = getDatesOfMonth(fecInicial, fecFinal, 4);
			mes = subGetMonthOfDateInLetter(fecFinal);
			mes = mes.toUpperCase();
			cellIdx = cellIdx + 1;
			celdaLinea = hssfHeader.createCell(cellIdx); 
			sheet.autoSizeColumn(cellIdx,true);
			celdaLinea.setCellValue(new HSSFRichTextString(mes+" - Original"));
			posicionMes.put(mes.toUpperCase()+" Original", cellIdx);  
			sheet.autoSizeColumn(cellIdx,true); 
			cellIdx = cellIdx + 1;
			celdaLinea = hssfHeader.createCell(cellIdx);
			sheet.autoSizeColumn(cellIdx,true);
			celdaLinea.setCellValue(new HSSFRichTextString(mes+" - Ajustado"));
			posicionMes.put(mes.toUpperCase() +" Ajustado", cellIdx);  
			sheet.autoSizeColumn(cellIdx,true); 
			cellIdx = cellIdx + 1;
			celdaLinea = hssfHeader.createCell(cellIdx); 
			sheet.autoSizeColumn(cellIdx,true);
			celdaLinea.setCellValue(new HSSFRichTextString(mes+" - Real"));
			posicionMes.put(mes+" Real", cellIdx);  
			sheet.autoSizeColumn(cellIdx,true);
		}
		
		cellIdx = cellIdx + 1; 
		celdaLinea = hssfHeader.createCell(cellIdx);  
		celdaLinea.setCellValue(new HSSFRichTextString("Total Original"));
		sheet.autoSizeColumn(cellIdx,true); 
		posicionMes.put("TOTALOriginal", cellIdx); 
		
		cellIdx = cellIdx + 1;
		celdaLinea = hssfHeader.createCell(cellIdx);  
		celdaLinea.setCellValue(new HSSFRichTextString("Total Ajustado"));
		sheet.autoSizeColumn(cellIdx,true); 
		posicionMes.put("TOTALAjustado", cellIdx); 
		
		cellIdx = cellIdx + 1;
		
		celdaLinea = hssfHeader.createCell(cellIdx);  
		celdaLinea.setCellValue(new HSSFRichTextString("Total Real"));
		sheet.autoSizeColumn(cellIdx,true); 
		posicionMes.put("TOTALReal", cellIdx); 
		//inmoviliza columna,fila
		sheet.createFreezePane (1, 5);
		
		double[] subtotal = new double[posicionMes.size()+1];
		double[] subtotalHor = new double[posicionMes.size()+1];
		double[] subtotalHorAux = new double[posicionMes.size()+1];
		double[] totalIngresos = new double[posicionMes.size()+1];
		double[] totalEgresos = new double[posicionMes.size()+1];
		double[] saldosAcomulado=new double[posicionMes.size()+1];
		double[] cantSaldo=new double[posicionMes.size()+1];
		double[] auxiliar=new double[posicionMes.size()+1];
		int[] posicionCelda=new int[ntNumMonths+1];
		for (int i = 0; i == ntNumMonths; i++) {
			subtotal[i] = 0;
			subtotalHor[i] = 0;
			totalIngresos[i] = 0;
			subtotalHorAux[i] = 0; 
			cantSaldo[i] = 0;
			totalIngresos[i] = 0;
			auxiliar[i] = 0;
			saldosAcomulado[i] = 0;
		}
		 
		
		cellIdx = 0;
		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx); 
		celdaLinea = hssfHeader.createCell(cellIdx);  
		celdaLinea.setCellValue(new HSSFRichTextString("Conceptos"));
		cellIdx = 0;
		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx); 
		celdaLinea = hssfHeader.createCell(cellIdx);  
		celdaLinea.setCellValue(new HSSFRichTextString("INGRESOS"));
		
		//funSqlGetTotalesXMesForConceptoOARExcel
		String tipoMovimiento="",encabezadoAnterior=" ",descLargaAnterior="",mesAnterior="";
		double diferencia=0.00, acomulado = 0.00;
		int posicion=0;
		boolean totalMovimiento = true, pasa = false, pasa1 = false;
		
		List<TotalConcepto> totalesConcepto = cashFlowDao.funSqlGetTotalesXMesForConceptoOARExcel(IdDivisa,NoUsuario, mes, mes);
		Iterator it = totalesConcepto.iterator();
	 
			while (it.hasNext()) {
				TotalConcepto totalConcepto = (TotalConcepto) it.next(); 
				tipoMovimiento = totalConcepto.getIngresoegreso();
				if(tipoMovimiento.equals("I")){
				if (encabezadoAnterior.equals(" ")) {
					cellIdx = 0;
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellStyle(styleGroup2);
					celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDescripcion().toString())); 
					
					// Se crea la descripcion larga
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx); 
					celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString()));
					
					cellIdx = posicionMes.get(totalConcepto.getMes().toString().toUpperCase()+" "+totalConcepto.getIdentificador().toString());
					celdaLinea = hssfHeader.createCell(cellIdx); 
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
					celdaLinea.setCellStyle(alingText);
					
					posicion = posicionMes.get("TOTAL"+totalConcepto.getIdentificador());
					subtotalHor[posicion] += totalConcepto.getTotal(); 
					
					subtotal[cellIdx] += totalConcepto.getTotal();
					subtotal[posicion] += totalConcepto.getTotal();
					
					mesAnterior=totalConcepto.getMes().toUpperCase();
				 	descLargaAnterior = totalConcepto.getDesc_larga().toString();
					
				}  else if (encabezadoAnterior.equals(totalConcepto.getDescripcion().toString())) {
					if (descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())) { 						
						 
						cellIdx=posicionMes.get(totalConcepto.getMes().toString().toUpperCase()+" "+totalConcepto.getIdentificador().toString());
						celdaLinea = hssfHeader.createCell(cellIdx); 
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal()))); 
						celdaLinea.setCellStyle(alingText);
						posicion = posicionMes.get("TOTAL"+totalConcepto.getIdentificador()) ;
						
						posicion = posicionMes.get("TOTAL"+totalConcepto.getIdentificador());
						subtotalHor[posicion] += totalConcepto.getTotal(); 
						
						subtotal[cellIdx] += totalConcepto.getTotal();
						subtotal[posicion] += totalConcepto.getTotal();
						
						mesAnterior=totalConcepto.getMes().toUpperCase();
						
					} else {

						posicion=posicionMes.get("TOTALOriginal");
						
						for (int i = posicion; i <= subtotalHor.length-1; i++) { 
							celdaLinea = hssfHeader.createCell(i); 
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHor[i])));
							sheet.autoSizeColumn(i,true);
							celdaLinea.setCellStyle(alingText); 
							subtotalHor[i] = 0.00;
							 
						}
						
						cellIdx = 0;
						rowIdx = rowIdx + 1;
						hssfHeader = sheet.createRow(rowIdx);
						celdaLinea = hssfHeader.createCell(cellIdx); 
						celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString()));
						
						cellIdx=posicionMes.get(totalConcepto.getMes().toString().toUpperCase()+" "+totalConcepto.getIdentificador().toString());
						celdaLinea = hssfHeader.createCell(cellIdx); 
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
						celdaLinea.setCellStyle(alingText);
						
						posicion = posicionMes.get("TOTAL"+totalConcepto.getIdentificador());
						subtotalHor[posicion] += totalConcepto.getTotal(); 
						
						subtotal[cellIdx] += totalConcepto.getTotal();
						subtotal[posicion] += totalConcepto.getTotal();
					 
						mesAnterior=totalConcepto.getMes().toUpperCase();
						descLargaAnterior = totalConcepto.getDesc_larga().toString();
					}
				} else {			 
				 
					 
					cellIdx = 0;
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellStyle(styleGroup2);
					celdaLinea.setCellValue(new HSSFRichTextString("SUBTOTAL- " + encabezadoAnterior));


					for (int i = 1; i <= subtotal.length-1; i++) {
						celdaLinea = hssfHeader.createCell(i);
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotal[i])));
						celdaLinea.setCellStyle(alingText);
						sheet.autoSizeColumn(i,true);
						totalIngresos[i] += subtotal[i];
						subtotal[i] = 0;
						
					}
				 
					
					 
					
					// espacio entre los conceptos
					rowIdx = rowIdx + 1;

					cellIdx = 0;
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellStyle(styleGroup2);
					celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDescripcion().toString()));
					celdaLinea.setCellStyle(alingText);

					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx); 
					celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString()));
					
					cellIdx=posicionMes.get(totalConcepto.getMes().toString().toUpperCase()+" "+totalConcepto.getIdentificador().toString());
					celdaLinea = hssfHeader.createCell(cellIdx); 
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
					celdaLinea.setCellStyle(alingText);
				 
					posicion = posicionMes.get("TOTAL"+totalConcepto.getIdentificador());
					subtotalHor[posicion] += totalConcepto.getTotal(); 
					
					subtotal[cellIdx] += totalConcepto.getTotal();
					subtotal[posicion] += totalConcepto.getTotal();
					
					mesAnterior=totalConcepto.getMes().toUpperCase();
					descLargaAnterior = totalConcepto.getDesc_larga().toString();

				}
				
				encabezadoAnterior = totalConcepto.getDescripcion().toString(); 
				}else{
					if(totalMovimiento){
						
						posicion=posicionMes.get("TOTALOriginal");
						
						for (int i = posicion; i <= subtotalHor.length-1; i++) { 
							celdaLinea = hssfHeader.createCell(i); 
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHor[i])));
							sheet.autoSizeColumn(i,true);
							celdaLinea.setCellStyle(alingText); 
							subtotalHor[i] = 0.00;
							 
						}
						
						cellIdx = 0;
						rowIdx = rowIdx + 1;
						hssfHeader = sheet.createRow(rowIdx);
						celdaLinea = hssfHeader.createCell(cellIdx);
						celdaLinea.setCellStyle(styleGroup2);
						celdaLinea.setCellValue(new HSSFRichTextString("SUBTOTAL- " + encabezadoAnterior));


						for (int i = 1; i <= subtotal.length-1; i++) {
							celdaLinea = hssfHeader.createCell(i);
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotal[i])));
							celdaLinea.setCellStyle(alingText);
							sheet.autoSizeColumn(i,true);
							totalIngresos[i] += subtotal[i];
							subtotal[i] = 0;
							
						}
						
						rowIdx=rowIdx+1; 
						 cellIdx=0;
						 rowIdx=rowIdx+1;
						 hssfHeader = sheet.createRow(rowIdx);
						 celdaLinea = hssfHeader.createCell(cellIdx); 
						 celdaLinea.setCellStyle(styleGroup3);
						 celdaLinea.setCellValue(new HSSFRichTextString("TOTAL INGRESOS"));
						  
						  for (int i = 1; i <= totalIngresos.length-1; i++) {
								celdaLinea = hssfHeader.createCell(i);
								celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalIngresos[i])));
								celdaLinea.setCellStyle(alingText);
							}
						  
						  rowIdx=rowIdx+1;
						  
						  cellIdx=0; 
						  rowIdx=rowIdx+1; 
						  hssfHeader = sheet.createRow(rowIdx);   
						  celdaLinea = hssfHeader.createCell(cellIdx);  
						  celdaLinea.setCellStyle(cellStyleI);
						  celdaLinea.setCellValue(new HSSFRichTextString("EGRESOS"));
						
						  mesAnterior=totalConcepto.getMes().toUpperCase();
						  descLargaAnterior = totalConcepto.getDesc_larga().toString();
						
						totalMovimiento = false;
					}
					
					if (encabezadoAnterior.equals(totalConcepto.getDescripcion().toString())) {
						if (descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())) { 						
							 
							cellIdx=posicionMes.get(totalConcepto.getMes().toString().toUpperCase()+" "+totalConcepto.getIdentificador().toString());
							celdaLinea = hssfHeader.createCell(cellIdx); 
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal()))); 
							celdaLinea.setCellStyle(alingText);
							posicion = posicionMes.get("TOTAL"+totalConcepto.getIdentificador()) ;
							
							posicion = posicionMes.get("TOTAL"+totalConcepto.getIdentificador());
							subtotalHor[posicion] += totalConcepto.getTotal(); 
							
							subtotal[cellIdx] += totalConcepto.getTotal();
							subtotal[posicion] += totalConcepto.getTotal();
							
							mesAnterior=totalConcepto.getMes().toUpperCase();
							
						} else {

							posicion=posicionMes.get("TOTALOriginal");
							
							for (int i = posicion; i <= subtotalHor.length-1; i++) { 
								celdaLinea = hssfHeader.createCell(i); 
								celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHor[i])));
								sheet.autoSizeColumn(i,true);
								celdaLinea.setCellStyle(alingText); 
								subtotalHor[i] = 0.00;
								 
							}
							
							cellIdx = 0;
							rowIdx = rowIdx + 1;
							hssfHeader = sheet.createRow(rowIdx);
							celdaLinea = hssfHeader.createCell(cellIdx); 
							celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString()));
							
							cellIdx=posicionMes.get(totalConcepto.getMes().toString().toUpperCase()+" "+totalConcepto.getIdentificador().toString());
							celdaLinea = hssfHeader.createCell(cellIdx); 
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
							celdaLinea.setCellStyle(alingText);
							
							posicion = posicionMes.get("TOTAL"+totalConcepto.getIdentificador());
							subtotalHor[posicion] += totalConcepto.getTotal(); 
							
							subtotal[cellIdx] += totalConcepto.getTotal();
							subtotal[posicion] += totalConcepto.getTotal();
						 
							mesAnterior=totalConcepto.getMes().toUpperCase();
							descLargaAnterior = totalConcepto.getDesc_larga().toString();
						}
					} else {			 
						posicion=posicionMes.get("TOTALOriginal");
						
						for (int i = posicion; i <= subtotalHor.length-1; i++) { 
							celdaLinea = hssfHeader.createCell(i); 
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHor[i])));
							sheet.autoSizeColumn(i,true);
							celdaLinea.setCellStyle(alingText); 
							subtotalHor[i] = 0.00;
							 
						}
						
						 
					 if(pasa){
						cellIdx = 0;
						rowIdx = rowIdx + 1;
						hssfHeader = sheet.createRow(rowIdx);
						celdaLinea = hssfHeader.createCell(cellIdx);
						celdaLinea.setCellStyle(styleGroup2);
						celdaLinea.setCellValue(new HSSFRichTextString("SUBTOTAL- " + encabezadoAnterior));


						for (int i = 1; i <= subtotal.length-1; i++) {
							celdaLinea = hssfHeader.createCell(i);
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotal[i])));
							celdaLinea.setCellStyle(alingText);
							sheet.autoSizeColumn(i,true);
							totalEgresos[i] += subtotal[i];
							subtotal[i] = 0;
							
						}
					 }
					 pasa=true;
						 
						
						 
						
						// espacio entre los conceptos
						rowIdx = rowIdx + 1;

						cellIdx = 0;
						rowIdx = rowIdx + 1;
						hssfHeader = sheet.createRow(rowIdx);
						celdaLinea = hssfHeader.createCell(cellIdx);
						celdaLinea.setCellStyle(styleGroup2);
						celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDescripcion().toString()));
					 

						rowIdx = rowIdx + 1;
						hssfHeader = sheet.createRow(rowIdx);
						celdaLinea = hssfHeader.createCell(cellIdx); 
						celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString()));
						
						cellIdx=posicionMes.get(totalConcepto.getMes().toString().toUpperCase()+" "+totalConcepto.getIdentificador().toString());
						celdaLinea = hssfHeader.createCell(cellIdx); 
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
						celdaLinea.setCellStyle(alingText);
					 
						posicion = posicionMes.get("TOTAL"+totalConcepto.getIdentificador());
						subtotalHor[posicion] += totalConcepto.getTotal();
						
						subtotal[cellIdx] += totalConcepto.getTotal();
						subtotal[posicion] += totalConcepto.getTotal();
						
						mesAnterior=totalConcepto.getMes().toUpperCase();
						descLargaAnterior = totalConcepto.getDesc_larga().toString();

					}
					
					 
					
					encabezadoAnterior = totalConcepto.getDescripcion().toString();
				}
				}
			
			posicion=posicionMes.get("TOTALOriginal");
			
			for (int i = posicion; i <= subtotalHor.length-1; i++) { 
				celdaLinea = hssfHeader.createCell(i); 
				celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHor[i])));
				sheet.autoSizeColumn(i,true);
				celdaLinea.setCellStyle(alingText); 
				subtotalHor[i] = 0.00;
				 
			}
			
			 
	 
			cellIdx = 0;
			rowIdx = rowIdx + 1;
			hssfHeader = sheet.createRow(rowIdx);
			celdaLinea = hssfHeader.createCell(cellIdx);
			celdaLinea.setCellStyle(styleGroup2);
			celdaLinea.setCellValue(new HSSFRichTextString("SUBTOTAL- " + encabezadoAnterior));


			for (int i = 1; i <= subtotal.length-1; i++) {
				celdaLinea = hssfHeader.createCell(i);
				celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotal[i])));
				celdaLinea.setCellStyle(alingText);
				sheet.autoSizeColumn(i,true);
				totalEgresos[i] += subtotal[i];
				subtotal[i] = 0;
				
			}
			
			 rowIdx=rowIdx+1; 
			  cellIdx=0;
			  rowIdx=rowIdx+1;
			  hssfHeader = sheet.createRow(rowIdx);
			  celdaLinea = hssfHeader.createCell(cellIdx); 
			  celdaLinea.setCellStyle(styleGroup3);
			  celdaLinea.setCellValue(new HSSFRichTextString("TOTAL EGRESOS"));
			  
			  for (int i = 1; i <= totalEgresos.length-1; i++) {
					celdaLinea = hssfHeader.createCell(i);
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalEgresos[i])));
					celdaLinea.setCellStyle(alingText);
				}
			  
			  	rowIdx = rowIdx + 1;
			  	cellIdx = 0;
				rowIdx = rowIdx + 1;
				hssfHeader = sheet.createRow(rowIdx);
				celdaLinea = hssfHeader.createCell(cellIdx);
				celdaLinea.setCellStyle(styleGroup3);
				celdaLinea.setCellValue(new HSSFRichTextString("SALDO OPERATIVO ACOMULADO"));
				
				for (int i = 1; i <= totalIngresos.length-1; ) { 		
					for (int j = 1; j <= totalEgresos.length-1; j++) {
						acomulado=totalIngresos[i]-totalEgresos[j]; 
						celdaLinea = hssfHeader.createCell(i);
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(acomulado))); 
						celdaLinea.setCellStyle(alingText);
						if(acomulado < -0){ 
							celdaLinea.setCellStyle(colorLetra);
						} 
						sheet.autoSizeColumn(i,true); 
						saldosAcomulado[i] = acomulado;
						acomulado=0.00;
						i++;
					}
				}
				
				
			 
			 	
					for(int j = 1 ; j <= saldosAcomulado.length-1;){
						if(j < 4){
							System.out.println("posicion de saldo Inicial" + x);
							double y = x + saldosAcomulado[j];
							System.out.println(y);
							auxiliar[j+3] = 0; 
							 j++;
						}else{
							posicion=posicionMes.get("TOTALOriginal");
							
							if(j >= posicion){
								double p = x + saldosAcomulado[j] ; 
								j++;
							}else{
								for(int i = j; i <= auxiliar.length-1 ;){ 
									 double m =  auxiliar[i] + saldosAcomulado[j];
									 System.out.println(m); 
									 
									 j++;
									 i++;
								}
								 
							}
							 
						}
						
				}
//				  
//				 for(int i = 1 ; i <= cantSaldo.length-1; i++){
//					 System.out.println(cantSaldo[i]);
//				 }
		return wb;
	}
	
	
	
	public HSSFWorkbook generarExcel(float x, String noEmpresa, String empresa, String reporte, String grupo,
			int usuario, String fechaIni, String fechaFin, String noGrupo, String IdDivisa) {
 		FechaHoy = getFechasSistemaBusiness("HOY");
		String mesPrimero = "";
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		HSSFWorkbook wb = new HSSFWorkbook();// genera archivo
		HSSFSheet sheet = wb.createSheet();// genera hojas
		
		HSSFCellStyle reporteEstilo = wb.createCellStyle();
		reporteEstilo.setAlignment(HSSFCellStyle.ALIGN_FILL); 
		
		HSSFFont font = wb.createFont();
		font.setColor(HSSFColor.RED.index);
		HSSFCellStyle colorLetra = wb.createCellStyle();
		colorLetra.setFont(font);
		
		HSSFCellStyle styleGroup2 = wb.createCellStyle();
		styleGroup2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleGroup2.setFillForegroundColor(HSSFColor.YELLOW.index);
		
		HSSFCellStyle cellStyleI = wb.createCellStyle();	 
		cellStyleI.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		cellStyleI.setFillForegroundColor(new HSSFColor.BLUE().getIndex());
		
		HSSFCellStyle styleGroup3 = wb.createCellStyle();
		styleGroup3.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleGroup3.setFillForegroundColor(HSSFColor.RED.index);
		
		HSSFCellStyle styleGroup1 = wb.createCellStyle();
		styleGroup1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleGroup1.setBorderRight((short) 1);
		styleGroup1.setFillForegroundColor(HSSFColor.GREEN.index);
		
		HSSFCellStyle alingText = wb.createCellStyle();
		alingText.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		alingText.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);
		
		
		int rowIdx = 0;// renglones
		int cellIdx = 0;// filas 
		
		HSSFRow hssfHeader = sheet.createRow(rowIdx);// encabezado
		HSSFCell celdaLinea = hssfHeader.createCell(cellIdx);
		
		HSSFRow hssfHeader1 = sheet.createRow(rowIdx);// encabezado
		HSSFCell celdaLinea1 = hssfHeader.createCell(cellIdx);
		Map<String, Integer> posicionMesTipo = new HashMap<String, Integer>();
		double contadorFinalPeriodo = 0.00;
		double subtotalPeriodo=0.00; 
		double subtotalPeriodoIngresoEgresos=0.00;
		int posicionSaldo;
		String columnaMes = "";
		String fecInicial = fechaIni, fecFinal = fechaFin, mes;
		String fecInicialAux="",fecFinalAux="",tipoInfo="";
		int ntNumMonths = 0;
		int ntCiclos=2;
		
		int noEmp = Integer.parseInt(noEmpresa); 
		celdaLinea.setCellValue(new HSSFRichTextString("Grupo Empresa: " + grupo));
		 
		if (noEmp != 0) { 
			rowIdx = rowIdx + 1;
			hssfHeader = sheet.createRow(rowIdx);
			celdaLinea = hssfHeader.createCell(cellIdx); 
			celdaLinea.setCellValue(new HSSFRichTextString("Empresa: " + empresa));
		}

		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx);
		celdaLinea = hssfHeader.createCell(cellIdx);  
		celdaLinea.setCellStyle(reporteEstilo);
		celdaLinea.setCellValue(new HSSFRichTextString("Reporte: " + reporte));
		sheet.autoSizeColumn(cellIdx,true);

		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx);
		celdaLinea = hssfHeader.createCell(cellIdx); 
		celdaLinea.setCellValue(new HSSFRichTextString("Fecha"));
		ntNumMonths = getMonthsBetweenTwoDatess(fecInicial, fecFinal);
		fecInicial = getDatesOfMonth(fecInicial, fecFinal, 1);
		fecFinal = getDatesOfMonth(fecInicial, fecFinal, 2);
		mes = subGetMonthOfDateInLetter(fecFinal);
		String mess = mes.toUpperCase();
		Map<String, Integer> posicionMes = new HashMap<String, Integer>();
		Map<String, Integer> tipoMes = new HashMap<String, Integer>();
		mes.toUpperCase(); 
		
		cellIdx = cellIdx + 1;
		celdaLinea = hssfHeader.createCell(cellIdx);
		celdaLinea.setCellValue(new HSSFRichTextString(mes.toUpperCase()+" - Original")); 
		posicionMes.put(mes.toUpperCase() +" Original", cellIdx);   
		sheet.autoSizeColumn(cellIdx,true);
		cellIdx = cellIdx + 1;
		celdaLinea = hssfHeader.createCell(cellIdx); 
		sheet.autoSizeColumn(cellIdx,true);
		celdaLinea.setCellValue(new HSSFRichTextString(mes.toUpperCase()+" - Ajustado"));
		posicionMes.put(mes.toUpperCase() + " Ajustado", cellIdx);  
		sheet.autoSizeColumn(cellIdx,true);
		cellIdx = cellIdx +  1;
		celdaLinea = hssfHeader.createCell(cellIdx); 
		sheet.autoSizeColumn(cellIdx,true);
		celdaLinea.setCellValue(new HSSFRichTextString(mes.toUpperCase()+" - Diferencia"));
		posicionMes.put(mes.toUpperCase()+"diferencia", cellIdx);  
		sheet.autoSizeColumn(cellIdx,true);
		
		mesPrimero = mes.toUpperCase();  
		for (int i = 1; i <= ntNumMonths - 1; i++) {
			fecInicial = getDatesOfMonth(fecInicial, fecFinal, 3);
			fecFinal = getDatesOfMonth(fecInicial, fecFinal, 4);
			mes = subGetMonthOfDateInLetter(fecFinal);
			mes = mes.toUpperCase();
			cellIdx = cellIdx + 1;
			celdaLinea = hssfHeader.createCell(cellIdx); 
			sheet.autoSizeColumn(cellIdx,true);
			celdaLinea.setCellValue(new HSSFRichTextString(mes+" - Original"));
			posicionMes.put(mes.toUpperCase()+" Original", cellIdx);  
			sheet.autoSizeColumn(cellIdx,true); 
			cellIdx = cellIdx + 1;
			celdaLinea = hssfHeader.createCell(cellIdx);
			sheet.autoSizeColumn(cellIdx,true);
			celdaLinea.setCellValue(new HSSFRichTextString(mes+" - Ajustado"));
			posicionMes.put(mes.toUpperCase() +" Ajustado", cellIdx);  
			sheet.autoSizeColumn(cellIdx,true); 
			cellIdx = cellIdx + 1;
			celdaLinea = hssfHeader.createCell(cellIdx); 
			sheet.autoSizeColumn(cellIdx,true);
			celdaLinea.setCellValue(new HSSFRichTextString(mes+" - Diferencia"));
			posicionMes.put(mes+"diferencia", cellIdx);  
			sheet.autoSizeColumn(cellIdx,true);
		}
		
		cellIdx = cellIdx + 1; 
		celdaLinea = hssfHeader.createCell(cellIdx);  
		celdaLinea.setCellValue(new HSSFRichTextString("Total Original"));
		sheet.autoSizeColumn(cellIdx,true); 
		posicionMes.put("TOTALOriginal", cellIdx); 
		
		cellIdx = cellIdx + 1;
		celdaLinea = hssfHeader.createCell(cellIdx);  
		celdaLinea.setCellValue(new HSSFRichTextString("Total Ajustado"));
		sheet.autoSizeColumn(cellIdx,true); 
		posicionMes.put("TOTALAjustado", cellIdx); 
		
		cellIdx = cellIdx + 1;
		
		celdaLinea = hssfHeader.createCell(cellIdx);  
		celdaLinea.setCellValue(new HSSFRichTextString("Total Diferencia"));
		sheet.autoSizeColumn(cellIdx,true); 
		posicionMes.put("TOTALDiferencia", cellIdx); 
		//inmoviliza columna,fila
		sheet.createFreezePane (1, 5);
		
		double[] subtotal = new double[posicionMes.size()+1];
		double[] subtotalHor = new double[posicionMes.size()+1];
		double[] subtotalHorAux = new double[posicionMes.size()+1];
		double[] totalIngresos = new double[posicionMes.size()+1];
		double[] totalEgresos = new double[posicionMes.size()+1];
		double[] saldosAcomulado=new double[posicionMes.size()+1];
		int[] posicionCelda=new int[ntNumMonths+1];
		for (int i = 0; i == ntNumMonths; i++) {
			subtotal[i] = 0;
			subtotalHor[i] = 0;
			totalIngresos[i] = 0;
			subtotalHorAux[i] = 0;
			totalIngresos[i] = 0;
			saldosAcomulado[i] = 0;
		}

		
		cellIdx = 0;
		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx); 
		celdaLinea = hssfHeader.createCell(cellIdx);  
		celdaLinea.setCellValue(new HSSFRichTextString("Conceptos"));
		cellIdx = 0;
		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx); 
		celdaLinea = hssfHeader.createCell(cellIdx);  
		celdaLinea.setCellValue(new HSSFRichTextString("INGRESOS"));
		
		
		String tipoMovimiento="",encabezadoAnterior=" ",descLargaAnterior="",mesAnterior="";
		double diferencia=0.00, acomulado = 0.00;
		int posicion=0;
		boolean totalMovimiento = true, pasa = false, pasa1 = false;
		
		List<TotalConcepto> totalesConcepto = cashFlowDao.funSqlGetTotalesXMesForConceptoOriginalAjustadoExcel(IdDivisa,NoUsuario, mes, mes);
		Iterator it = totalesConcepto.iterator();
		while (it.hasNext()) {
			TotalConcepto totalConcepto = (TotalConcepto) it.next(); 
			tipoMovimiento = totalConcepto.getIngresoegreso();
			if(tipoMovimiento.equals("I")){
			if (encabezadoAnterior.equals(" ")) {
				cellIdx = 0;
				rowIdx = rowIdx + 1;
				hssfHeader = sheet.createRow(rowIdx);
				celdaLinea = hssfHeader.createCell(cellIdx);
				celdaLinea.setCellStyle(styleGroup2);
				celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDescripcion().toString())); 
				
				// Se crea la descripcion larga
				rowIdx = rowIdx + 1;
				hssfHeader = sheet.createRow(rowIdx);
				celdaLinea = hssfHeader.createCell(cellIdx); 
				celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString()));
				
				cellIdx = posicionMes.get((totalConcepto.getMes().toString().toUpperCase()+" "+totalConcepto.getIdentificador().toString()));
				celdaLinea = hssfHeader.createCell(cellIdx); 
				celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
				celdaLinea.setCellStyle(alingText);
				
				posicion = posicionMes.get("TOTAL"+totalConcepto.getIdentificador()) ;
				
				subtotalHorAux[posicion] = totalConcepto.getTotal(); 
				subtotalHor[cellIdx] = totalConcepto.getTotal();
				
				subtotal[cellIdx] += totalConcepto.getTotal();
				subtotal[posicion] += totalConcepto.getTotal();
				
				
				mesAnterior=totalConcepto.getMes().toUpperCase();
			 	descLargaAnterior = totalConcepto.getDesc_larga().toString();
				
			}  else if (encabezadoAnterior.equals(totalConcepto.getDescripcion().toString())) {
				if (descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())) { 						
					if(!mesAnterior.equals(totalConcepto.getMes().toUpperCase()) || !descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())){
						
						for(int i=1;i<=subtotalHor.length-1;i++){
							diferencia =  subtotalHor[i] - diferencia;
							subtotalHor[i]=0.00;
						} 
						cellIdx=posicionMes.get(mesAnterior+"diferencia");
						celdaLinea = hssfHeader.createCell(cellIdx); 
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(diferencia)));
						celdaLinea.setCellStyle(alingText);
						sheet.autoSizeColumn(cellIdx,true);  
						
						posicion = posicionMes.get("TOTALDiferencia") ;
						subtotalHorAux[posicion] += diferencia;
						subtotal[posicion] += diferencia;
						diferencia = 0.00;
						
					 } 
					cellIdx=posicionMes.get((totalConcepto.getMes().toString().toUpperCase()+" "+totalConcepto.getIdentificador().toString()));
					celdaLinea = hssfHeader.createCell(cellIdx); 
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal()))); 
					celdaLinea.setCellStyle(alingText);
					posicion = posicionMes.get("TOTAL"+totalConcepto.getIdentificador()) ;
					
					subtotalHorAux[posicion] += totalConcepto.getTotal(); 
					subtotalHor[cellIdx]= totalConcepto.getTotal();
					
					subtotal[cellIdx] += totalConcepto.getTotal();
					subtotal[posicion] += totalConcepto.getTotal();
					
					mesAnterior=totalConcepto.getMes().toUpperCase();
					
				} else {

					if(!mesAnterior.equals(totalConcepto.getMes().toUpperCase()) || !descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())){
						
						for(int i=1;i<=subtotalHor.length-1;i++){
							diferencia =  subtotalHor[i] - diferencia;
							subtotalHor[i]=0.00;
						} 
						cellIdx=posicionMes.get(mesAnterior+"diferencia");
						celdaLinea = hssfHeader.createCell(cellIdx); 
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(diferencia)));
						celdaLinea.setCellStyle(alingText);
						sheet.autoSizeColumn(cellIdx,true);  
						 
						posicion = posicionMes.get("TOTALDiferencia") ;
						subtotalHorAux[posicion] += diferencia;
						
						subtotal[posicion] += diferencia;
						 
						posicion = posicionMes.get("TOTALOriginal");
						diferencia = 0.00;
						for(int i = posicion; i <= subtotalHorAux.length-1; i++ ){
							cellIdx = i;
							celdaLinea = hssfHeader.createCell(cellIdx);  
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHorAux[i])));
							celdaLinea.setCellStyle(alingText);
							subtotalHorAux[i] = 0.00;
						}
						
					 } 
					
					cellIdx = 0;
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx); 
					celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString()));
					
					cellIdx=posicionMes.get((totalConcepto.getMes().toString().toUpperCase()+" "+totalConcepto.getIdentificador().toString()));
					celdaLinea = hssfHeader.createCell(cellIdx); 
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
					celdaLinea.setCellStyle(alingText);
					posicion = posicionMes.get("TOTAL"+totalConcepto.getIdentificador()) ;
					
					subtotalHorAux[posicion] += totalConcepto.getTotal(); 
					subtotalHor[cellIdx] = totalConcepto.getTotal();
					
					subtotal[cellIdx] += totalConcepto.getTotal();
					subtotal[posicion] += totalConcepto.getTotal();
				 
					mesAnterior=totalConcepto.getMes().toUpperCase();
					descLargaAnterior = totalConcepto.getDesc_larga().toString();
				}
			} else {			 
				
				if(!mesAnterior.equals(totalConcepto.getMes().toUpperCase()) || !descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())){
					
					for(int i=1;i<=subtotalHor.length-1;i++){
						diferencia =  subtotalHor[i] - diferencia;
						subtotalHor[i]=0.00;
					} 
					cellIdx=posicionMes.get(mesAnterior+"diferencia");
					celdaLinea = hssfHeader.createCell(cellIdx); 
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(diferencia)));
					celdaLinea.setCellStyle(alingText);
					sheet.autoSizeColumn(cellIdx,true);  
					 
					posicion = posicionMes.get("TOTALDiferencia") ;
					subtotalHorAux[posicion] += diferencia;
					
					subtotal[posicion] += diferencia;
					
					posicion = posicionMes.get("TOTALOriginal");
					diferencia = 0.00;
					for(int i = posicion; i <= subtotalHorAux.length-1; i++ ){
						cellIdx = i;
						celdaLinea = hssfHeader.createCell(cellIdx);  
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHorAux[i]))); 
						celdaLinea.setCellStyle(alingText);
						subtotalHorAux[i] = 0.00;
					}
					
				 }
				
				
				if(pasa1){
				cellIdx = 0;
				rowIdx = rowIdx + 1;
				hssfHeader = sheet.createRow(rowIdx);
				celdaLinea = hssfHeader.createCell(cellIdx);
				celdaLinea.setCellStyle(styleGroup2);
				celdaLinea.setCellValue(new HSSFRichTextString("SUBTOTAL- " + encabezadoAnterior));


				for (int i = 1; i <= subtotal.length-1; i++) {
					celdaLinea = hssfHeader.createCell(i);
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotal[i])));
					celdaLinea.setCellStyle(alingText);
					sheet.autoSizeColumn(i,true);
					totalIngresos[i] += subtotal[i];
					subtotal[i] = 0;
					
				}
				}
				pasa1=true;
				
				 
				
				// espacio entre los conceptos
				rowIdx = rowIdx + 1;

				cellIdx = 0;
				rowIdx = rowIdx + 1;
				hssfHeader = sheet.createRow(rowIdx);
				celdaLinea = hssfHeader.createCell(cellIdx);
				celdaLinea.setCellStyle(styleGroup2);
				celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDescripcion().toString()));
				celdaLinea.setCellStyle(alingText);

				rowIdx = rowIdx + 1;
				hssfHeader = sheet.createRow(rowIdx);
				celdaLinea = hssfHeader.createCell(cellIdx); 
				celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString()));
				
				cellIdx=posicionMes.get((totalConcepto.getMes().toString().toUpperCase()+" "+totalConcepto.getIdentificador().toString()));
				celdaLinea = hssfHeader.createCell(cellIdx); 
				celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
				celdaLinea.setCellStyle(alingText);
				posicion = posicionMes.get("TOTAL"+totalConcepto.getIdentificador()) ;
				
				subtotalHorAux[posicion] += totalConcepto.getTotal(); 
				subtotalHor[cellIdx]= totalConcepto.getTotal();
				
				subtotal[cellIdx] += totalConcepto.getTotal();
				subtotal[posicion] += totalConcepto.getTotal();
				
				mesAnterior=totalConcepto.getMes().toUpperCase();
				descLargaAnterior = totalConcepto.getDesc_larga().toString();

			}
			
			encabezadoAnterior = totalConcepto.getDescripcion().toString(); 
			}else{
				if(totalMovimiento){
					if(!mesAnterior.equals(totalConcepto.getMes().toUpperCase()) || !descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())){
																
						for(int i=1;i<=subtotalHor.length-1;i++){
							diferencia =  subtotalHor[i] - diferencia;
							subtotalHor[i]=0.00;
						} 
						cellIdx=posicionMes.get(mesAnterior+"diferencia");
						celdaLinea = hssfHeader.createCell(cellIdx); 
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(diferencia)));
						celdaLinea.setCellStyle(alingText);
						sheet.autoSizeColumn(cellIdx,true);  
						 
						posicion = posicionMes.get("TOTALDiferencia") ;
						subtotalHorAux[posicion] += diferencia;
						
						subtotal[posicion] += diferencia;
						
						posicion = posicionMes.get("TOTALOriginal");
						diferencia = 0.00;
						for(int i = posicion; i <= subtotalHorAux.length-1; i++ ){
							cellIdx = i;
							celdaLinea = hssfHeader.createCell(cellIdx);  
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHorAux[i]))); 
							celdaLinea.setCellStyle(alingText);
							subtotalHorAux[i] = 0.00;
						}
						
					} 
					
					cellIdx = 0;
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellStyle(styleGroup2);
					celdaLinea.setCellValue(new HSSFRichTextString("SUBTOTAL- " + encabezadoAnterior)); 
					
					
					for (int i = 1; i <= subtotal.length-1; i++) {
						celdaLinea = hssfHeader.createCell(i);
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotal[i])));
						celdaLinea.setCellStyle(alingText);
						sheet.autoSizeColumn(i,true);
						totalIngresos[i] += subtotal[i];
						subtotal[i] = 0;
						
					}
					
					rowIdx=rowIdx+1; 
					 cellIdx=0;
					 rowIdx=rowIdx+1;
					 hssfHeader = sheet.createRow(rowIdx);
					 celdaLinea = hssfHeader.createCell(cellIdx); 
					 celdaLinea.setCellStyle(styleGroup3);
					 celdaLinea.setCellValue(new HSSFRichTextString("TOTAL INGRESOS"));
					  
					  for (int i = 1; i <= totalIngresos.length-1; i++) {
							celdaLinea = hssfHeader.createCell(i);
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalIngresos[i])));
							celdaLinea.setCellStyle(alingText);
						}
					  
					  rowIdx=rowIdx+1;
					  
					  cellIdx=0; 
					  rowIdx=rowIdx+1; 
					  hssfHeader = sheet.createRow(rowIdx);   
					  celdaLinea = hssfHeader.createCell(cellIdx);  
					  celdaLinea.setCellStyle(cellStyleI);
					  celdaLinea.setCellValue(new HSSFRichTextString("EGRESOS"));
					
					  mesAnterior=totalConcepto.getMes().toUpperCase();
					  descLargaAnterior = totalConcepto.getDesc_larga().toString();
					
					totalMovimiento = false;
				}
					if (encabezadoAnterior.equals(totalConcepto.getDescripcion().toString())) {
						if (descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())) { 						
							if(!mesAnterior.equals(totalConcepto.getMes().toUpperCase()) || !descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())){
								
								for(int i=1;i<=subtotalHor.length-1;i++){
									diferencia =  subtotalHor[i] - diferencia;
									subtotalHor[i]=0.00;
								} 
								cellIdx=posicionMes.get(mesAnterior+"diferencia");
								celdaLinea = hssfHeader.createCell(cellIdx); 
								celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(diferencia)));
								celdaLinea.setCellStyle(alingText);
								sheet.autoSizeColumn(cellIdx,true);  
								
								posicion = posicionMes.get("TOTALDiferencia") ;
								subtotalHorAux[posicion] += diferencia;
								subtotal[posicion] += diferencia;
								
								diferencia = 0.00;
							 } 
							cellIdx=posicionMes.get((totalConcepto.getMes().toString().toUpperCase()+" "+totalConcepto.getIdentificador().toString()));
							celdaLinea = hssfHeader.createCell(cellIdx); 
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal()))); 
							celdaLinea.setCellStyle(alingText);
							posicion = posicionMes.get("TOTAL"+totalConcepto.getIdentificador()) ;
							
							subtotalHorAux[posicion] += totalConcepto.getTotal(); 
							subtotalHor[cellIdx]= totalConcepto.getTotal();
							
							subtotal[cellIdx] += totalConcepto.getTotal();
							subtotal[posicion] += totalConcepto.getTotal();
							
							mesAnterior=totalConcepto.getMes().toUpperCase();
							
						} else {

							if(!mesAnterior.equals(totalConcepto.getMes().toUpperCase()) || !descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())){
								
								for(int i=1;i<=subtotalHor.length-1;i++){
									diferencia =  subtotalHor[i] - diferencia;
									subtotalHor[i]=0.00;
								} 
								cellIdx=posicionMes.get(mesAnterior+"diferencia");
								celdaLinea = hssfHeader.createCell(cellIdx); 
								celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(diferencia)));
								celdaLinea.setCellStyle(alingText);
								sheet.autoSizeColumn(cellIdx,true);  
								 
								posicion = posicionMes.get("TOTALDiferencia") ;
								subtotalHorAux[posicion] += diferencia;
								
								subtotal[posicion] += diferencia;
								
								posicion = posicionMes.get("TOTALOriginal");
								diferencia = 0.00;
								for(int i = posicion; i <= subtotalHorAux.length-1; i++ ){
									cellIdx = i;
									celdaLinea = hssfHeader.createCell(cellIdx);  
									celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHorAux[i]))); 
									celdaLinea.setCellStyle(alingText);
									subtotalHorAux[i] = 0.00;
								}
								
							 } 
							
							cellIdx = 0;
							rowIdx = rowIdx + 1;
							hssfHeader = sheet.createRow(rowIdx);
							celdaLinea = hssfHeader.createCell(cellIdx); 
							celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString()));
							
							cellIdx=posicionMes.get((totalConcepto.getMes().toString().toUpperCase()+" "+totalConcepto.getIdentificador().toString()));
							celdaLinea = hssfHeader.createCell(cellIdx); 
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
							celdaLinea.setCellStyle(alingText);
							posicion = posicionMes.get("TOTAL"+totalConcepto.getIdentificador()) ;
							
							subtotalHorAux[posicion] += totalConcepto.getTotal(); 
							subtotalHor[cellIdx] = totalConcepto.getTotal();
							
							subtotal[cellIdx] += totalConcepto.getTotal();
							subtotal[posicion] += totalConcepto.getTotal();
						 
							mesAnterior=totalConcepto.getMes().toUpperCase();
							descLargaAnterior = totalConcepto.getDesc_larga().toString();
						}
					} else {			 
						
						if(!mesAnterior.equals(totalConcepto.getMes().toUpperCase()) || !descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())){
							
							for(int i=1;i<=subtotalHor.length-1;i++){
								diferencia =  subtotalHor[i] - diferencia;
								subtotalHor[i]=0.00;
							} 
							cellIdx=posicionMes.get(mesAnterior+"diferencia");
							celdaLinea = hssfHeader.createCell(cellIdx); 
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(diferencia)));
							celdaLinea.setCellStyle(alingText);
							sheet.autoSizeColumn(cellIdx,true);  
							 
							posicion = posicionMes.get("TOTALDiferencia") ;
							subtotalHorAux[posicion] += diferencia;
							
							subtotal[posicion] += diferencia;
							
							posicion = posicionMes.get("TOTALOriginal");
							diferencia = 0.00;
							for(int i = posicion; i <= subtotalHorAux.length-1; i++ ){
								cellIdx = i;
								celdaLinea = hssfHeader.createCell(cellIdx);  
								celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHorAux[i]))); 
								celdaLinea.setCellStyle(alingText);
								subtotalHorAux[i] = 0.00;
							}
							
						 }
						
						
						if(pasa1){
						cellIdx = 0;
						rowIdx = rowIdx + 1;
						hssfHeader = sheet.createRow(rowIdx);
						celdaLinea = hssfHeader.createCell(cellIdx);
						celdaLinea.setCellStyle(styleGroup2);
						celdaLinea.setCellValue(new HSSFRichTextString("SUBTOTAL- " + encabezadoAnterior));


						for (int i = 1; i <= subtotal.length-1; i++) {
							celdaLinea = hssfHeader.createCell(i);
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotal[i])));
							celdaLinea.setCellStyle(alingText);
							sheet.autoSizeColumn(i,true);
							totalEgresos[i] += subtotal[i];
							subtotal[i] = 0;
							
						}
						}
						pasa1=true;
						
						 
						
						// espacio entre los conceptos
						rowIdx = rowIdx + 1;

						cellIdx = 0;
						rowIdx = rowIdx + 1;
						hssfHeader = sheet.createRow(rowIdx);
						celdaLinea = hssfHeader.createCell(cellIdx);
						celdaLinea.setCellStyle(styleGroup2);
						celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDescripcion().toString()));
						
						

						rowIdx = rowIdx + 1;
						hssfHeader = sheet.createRow(rowIdx);
						celdaLinea = hssfHeader.createCell(cellIdx); 
						celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString()));
						
						cellIdx=posicionMes.get((totalConcepto.getMes().toString().toUpperCase()+" "+totalConcepto.getIdentificador().toString()));
						celdaLinea = hssfHeader.createCell(cellIdx); 
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
						celdaLinea.setCellStyle(alingText);
						posicion = posicionMes.get("TOTAL"+totalConcepto.getIdentificador()) ;
						
						subtotalHorAux[posicion] += totalConcepto.getTotal(); 
						subtotalHor[cellIdx]= totalConcepto.getTotal();
						
						subtotal[cellIdx] += totalConcepto.getTotal();
						subtotal[posicion] += totalConcepto.getTotal();
						
						mesAnterior=totalConcepto.getMes().toUpperCase();
						descLargaAnterior = totalConcepto.getDesc_larga().toString();

					}
					
					encabezadoAnterior = totalConcepto.getDescripcion().toString(); 
				}
			}
				for(int i=1;i<=subtotalHor.length-1;i++){
					diferencia =  subtotalHor[i] - diferencia;
					subtotalHor[i]=0.00;
				} 
				cellIdx=posicionMes.get(mesAnterior+"diferencia");
				celdaLinea = hssfHeader.createCell(cellIdx); 
				celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(diferencia)));
				celdaLinea.setCellStyle(alingText);
				sheet.autoSizeColumn(cellIdx,true);  
				 
				posicion = posicionMes.get("TOTALDiferencia") ;
				subtotalHorAux[posicion] += diferencia;
				
				subtotal[posicion] += diferencia;
				
				posicion = posicionMes.get("TOTALOriginal");
				diferencia = 0.00;
				for(int i = posicion; i <= subtotalHorAux.length-1; i++ ){
					cellIdx = i;
					celdaLinea = hssfHeader.createCell(cellIdx);  
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHorAux[i]))); 
					celdaLinea.setCellStyle(alingText);
					subtotalHorAux[i] = 0.00;
				}
				
				cellIdx = 0;
				rowIdx = rowIdx + 1;
				hssfHeader = sheet.createRow(rowIdx);
				celdaLinea = hssfHeader.createCell(cellIdx);
				celdaLinea.setCellStyle(styleGroup2);
				celdaLinea.setCellValue(new HSSFRichTextString("SUBTOTAL- " + encabezadoAnterior)); 
			 
				for (int i = 1; i <= subtotal.length-1; i++) {
					celdaLinea = hssfHeader.createCell(i);
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotal[i])));
					celdaLinea.setCellStyle(alingText);
					sheet.autoSizeColumn(i,true);
					totalEgresos[i] += subtotal[i];
					subtotal[i] = 0; 
				}
				  rowIdx=rowIdx+1; 
				  cellIdx=0;
				  rowIdx=rowIdx+1;
				  hssfHeader = sheet.createRow(rowIdx);
				  celdaLinea = hssfHeader.createCell(cellIdx); 
				  celdaLinea.setCellStyle(styleGroup3);
				  celdaLinea.setCellValue(new HSSFRichTextString("TOTAL EGRESOS"));
				  
				  for (int i = 1; i <= totalEgresos.length-1; i++) {
						celdaLinea = hssfHeader.createCell(i);
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalEgresos[i])));
						celdaLinea.setCellStyle(alingText);
					}
				  
				  	rowIdx = rowIdx + 1;
				  	cellIdx = 0;
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellStyle(styleGroup3);
					celdaLinea.setCellValue(new HSSFRichTextString("SALDO OPERATIVO ACOMULADO"));
					
					for (int i = 1; i <= totalIngresos.length-1; ) { 		
						for (int j = 1; j <= totalEgresos.length-1; j++) {
							acomulado=totalIngresos[i]-totalEgresos[j]; 
							celdaLinea = hssfHeader.createCell(i);
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(acomulado))); 
							celdaLinea.setCellStyle(alingText);
							if(acomulado < -0){ 
								celdaLinea.setCellStyle(colorLetra);
							} 
							sheet.autoSizeColumn(i,true); 
							saldosAcomulado[i] = acomulado;
							acomulado=0.00;
							i++;
						}
					}
					
					rowIdx = rowIdx + 1;
				  	cellIdx = 0;
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellStyle(styleGroup3);
					celdaLinea.setCellValue(new HSSFRichTextString("SALDO  DISPONIBLE"));
					
					for(int i = 1; i <= saldosAcomulado.length-1; i++){
						
						
					}
				
		return wb;
	}
	
	public HSSFWorkbook generarExcelSemanal(double x, String noEmpresa, String empresa, String reporte, String grupo,
			int usuario, String fechaIni, String fechaFin, String noGrupo, String IdDivisa) { 

		FechaHoy = getFechasSistemaBusiness("HOY");
		String mesPrimero = "";
		DecimalFormat formateador = new DecimalFormat("###,###,###,##0.00");
		HSSFWorkbook wb = new HSSFWorkbook();// genera archivo
		HSSFSheet sheet = wb.createSheet();// genera hojas 
		
		HSSFCellStyle reporteEstilo = wb.createCellStyle();
		reporteEstilo.setAlignment(HSSFCellStyle.ALIGN_FILL); 
		
		HSSFFont font = wb.createFont();
		font.setColor(HSSFColor.RED.index);
		HSSFCellStyle colorLetra = wb.createCellStyle();
		colorLetra.setFont(font);
		
		HSSFCellStyle styleGroup2 = wb.createCellStyle();
		styleGroup2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleGroup2.setFillForegroundColor(HSSFColor.YELLOW.index);
		
		HSSFCellStyle cellStyleI = wb.createCellStyle();	 
		cellStyleI.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); 
		cellStyleI.setFillForegroundColor(new HSSFColor.BLUE().getIndex());
		
		HSSFCellStyle styleGroup3 = wb.createCellStyle();
		styleGroup3.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleGroup3.setFillForegroundColor(HSSFColor.RED.index);
		
		HSSFCellStyle styleGroup1 = wb.createCellStyle();
		styleGroup1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); 
		styleGroup1.setFillForegroundColor(HSSFColor.GREEN.index);
		  
		int rowIdx = 0;// renglones
		int cellIdx = 0;// filas 
		
		HSSFRow hssfHeader = sheet.createRow(rowIdx);// encabezado
		HSSFCell celdaLinea = hssfHeader.createCell(cellIdx);
		
		HSSFRow hssfHeader1 = sheet.createRow(rowIdx);// encabezado
		HSSFCell celdaLinea1 = hssfHeader1.createCell(cellIdx); 
		
		int posicionSaldo;
		String columnaMes = "";
		String fecInicial = fechaIni, fecFinal = fechaFin, mes=" ";
		int ntNumMonths = 0,ntWeeks=0;
		
		Map<String , Integer> posicionSemana = new HashMap<String, Integer>();
		Map<String , Integer> posicionMes = new HashMap<String, Integer>();
		
		int noEmp = Integer.parseInt(noEmpresa); 
		celdaLinea.setCellValue(new HSSFRichTextString("Grupo Empresa: " + grupo));
		 
		if (noEmp != 0) { 
			rowIdx = rowIdx + 1;
			hssfHeader = sheet.createRow(rowIdx);
			celdaLinea = hssfHeader.createCell(cellIdx); 
			celdaLinea.setCellValue(new HSSFRichTextString("Empresa: " + empresa));
		}

		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx); 
		celdaLinea = hssfHeader.createCell(cellIdx);  
		celdaLinea.setCellStyle(reporteEstilo);
		celdaLinea.setCellValue(new HSSFRichTextString("Reporte: " + reporte)); 
		sheet.autoSizeColumn(cellIdx,true);

		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx);
		celdaLinea = hssfHeader.createCell(cellIdx); 
		celdaLinea.setCellValue(new HSSFRichTextString("Fecha"));
		

		int ntCiclos=2;
		String descripcion; 
		
		String fecInicialAux="";
		String fecFinalAux="";

		String fecInicialr;
		String fecFinalr;
		
	 
		ntNumMonths = getMonthsBetweenTwoDatess(fecInicial, fecFinal);
		fecInicialAux = fecInicial;
		fecFinalAux = fecFinal;
		for (int iCiclos = 1; iCiclos <= ntCiclos; iCiclos ++ ) {
			
			fecInicial = fecInicialAux; 
			fecFinal = fecFinalAux;

			if(iCiclos == 1){
				 descripcion = "Real";
			}else{
				descripcion ="Ajustado"; 
			}
			 
			fecInicial = getDatesOfMonth(fecInicial, fecFinal, 1);
			fecFinal = getDatesOfMonth(fecInicial, fecFinal, 2);
			ntWeeks = getWeeksBetweenTwoDates1(fecInicial, fecFinal); 
			mes = subGetMonthOfDateInLetter(fecFinal);
			mes = mes.toUpperCase();
			
			for (int i = 1; i <= ntNumMonths;i++) {
				for (int j = 1; j <= ntWeeks; j++) {
					cellIdx = cellIdx + 1;
					celdaLinea = hssfHeader.createCell(cellIdx); 
					celdaLinea.setCellValue(new HSSFRichTextString("SEMANA "+j+"/"+descripcion));
					sheet.autoSizeColumn(cellIdx,true);
					posicionSemana.put(mes+j+descripcion, cellIdx); 
				}
			 
				cellIdx = cellIdx + 1;
				celdaLinea = hssfHeader.createCell(cellIdx); 
				celdaLinea.setCellValue(new HSSFRichTextString(mes+"/"+descripcion));
				sheet.autoSizeColumn(cellIdx,true);
				posicionSemana.put(mes+descripcion, cellIdx);
				
				fecInicial = getDatesOfMonth(fecInicial, fecFinal, 3);
				fecFinal = getDatesOfMonth(fecInicial, fecFinal, 4);
			 
				ntWeeks = getWeeksBetweenTwoDates1(fecInicial, fecFinal);
				
				mes = subGetMonthOfDateInLetter(fecFinal);
				mes = mes.toUpperCase();
			} 
			 
			cellIdx = cellIdx + 1;
			celdaLinea = hssfHeader.createCell(cellIdx); 
			celdaLinea.setCellValue(new HSSFRichTextString("TOTAL"+" "+descripcion));
			sheet.autoSizeColumn(cellIdx,true);
			posicionSemana.put("TOTAL"+descripcion,cellIdx);
					 
		}
		
		cellIdx = cellIdx + 1;
		celdaLinea = hssfHeader.createCell(cellIdx); 
		celdaLinea.setCellValue(new HSSFRichTextString("TOTAL PERIODO"));
		sheet.autoSizeColumn(cellIdx,true);
		posicionSemana.put("TOTAL PERIODO",cellIdx);
		
		 
		double[] subtotalHor = new double[posicionSemana.size()+1];
		double[] subtotal = new double[posicionSemana.size()+1];
		double[] totalIngresos = new double[posicionSemana.size()+1];
		double[] totalEgresos = new double[posicionSemana.size()+1];
		double[] saldoAcomulado = new double[posicionSemana.size()+1];
		double[] cantSaldo = new double[posicionSemana.size()+1];
		double[] subtotalHorFinal = new double[posicionSemana.size()+1];
		
		for (int i = 0; i == posicionSemana.size(); i++) { 
			subtotalHor[i] = 0; 
			subtotalHorFinal[i] = 0;
			subtotal[i] = 0;
			totalIngresos[i] = 0;
			totalEgresos[i] = 0;
			saldoAcomulado[i] = 0;
			cantSaldo[i] = 0;
		}
		
		 
		
		cellIdx = 0;
		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx); 
		celdaLinea = hssfHeader.createCell(cellIdx); 
		celdaLinea.setCellValue(new HSSFRichTextString("CONCEPTOS"));
		
		rowIdx = rowIdx + 1; 
	
		int filaSaldo=rowIdx;//obtiene fila 
		int celdaSaldo=cellIdx;//obtiene la celda

		
		rowIdx =rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx); 
		celdaLinea = hssfHeader.createCell(cellIdx); 
		celdaLinea.setCellStyle(cellStyleI);
		celdaLinea.setCellValue(new HSSFRichTextString("INGRESOS"));
		
		//inmoviliza columna,fila
		sheet.createFreezePane (1, 5);
		
		List<ConceptoFlujo> conceptos;
		String idTipoMovto = "I";
		int empresaM = Integer.parseInt(noEmpresa);
		int NogrupoM = Integer.parseInt(noGrupo);
		
		List<TotalSemanaDto> totalesConcepto = cashFlowDao.funSqlGetTotalesXMesForConceptoSemanalExcel(IdDivisa,
				NoUsuario, mes, mes);
		boolean totalMovimiento=true,pasa=false;
		Iterator it = totalesConcepto.iterator();
		String encabezadoAnterior=" ",descLargaAnterior=" ",mesAnterior=" ",descripcionAnterior=" ";
		double contadorFinalPeriodo = 0.00;
		int posicion,columna,total;
	while (it.hasNext()) {
			TotalSemanaDto totalConcepto = (TotalSemanaDto) it.next();
			String tipoMovimiento = totalConcepto.getIngreso_egreso();  
			 

			if (tipoMovimiento.equals("I")) {
				if (encabezadoAnterior.equals(" ")) {
					cellIdx = 0;
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellStyle(styleGroup2);
					celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDescripcion().toString())); 
					//ajusta ancho de celda segun el texto numCelda
					sheet.autoSizeColumn(cellIdx,true);
					// Se crea la descripcion larga
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx); 
					celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString()));
					sheet.autoSizeColumn(cellIdx,true);
//					for (int i = 1; i <= subtotal.length-1; i++) {
//						celdaLinea = hssfHeader.createCell(i);
//						celdaLinea.setCellValue(new HSSFRichTextString(0.00+""));  
//					}
					
					// Se crea el primer mes 
					
					cellIdx = posicionSemana.get(totalConcepto.getMes().toUpperCase()+totalConcepto.getColumna()+totalConcepto.getIdentificador()); 
					celdaLinea = hssfHeader.createCell(cellIdx); 
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
					
					sheet.autoSizeColumn(cellIdx,true);
					columna = posicionSemana.get(totalConcepto.getMes().toUpperCase()+totalConcepto.getIdentificador());
					subtotal[columna]+=Double.parseDouble(totalConcepto.getTotal() + "");
					
					subtotal[cellIdx]=Double.parseDouble(totalConcepto.getTotal() + "");
					
					total=posicionSemana.get("TOTAL"+totalConcepto.getIdentificador());
					subtotal[total]=Double.parseDouble(totalConcepto.getTotal() + "");
					
					posicion = posicionSemana.get(totalConcepto.getMes().toUpperCase()+totalConcepto.getIdentificador());
					subtotalHor[posicion] = Double.parseDouble(totalConcepto.getTotal() + "");  
					
					subtotalHorFinal[posicion] += Double.parseDouble(totalConcepto.getTotal() + ""); 
					
					mesAnterior=totalConcepto.getMes().toUpperCase()+totalConcepto.getIdentificador();
					descripcionAnterior=totalConcepto.getIdentificador();
					descLargaAnterior = totalConcepto.getDesc_larga().toString();
					
				} else if (encabezadoAnterior.equals(totalConcepto.getDescripcion().toString())) {
					if (descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())) {
					 
						// Se crea el primer mes
						cellIdx = posicionSemana.get(totalConcepto.getMes().toUpperCase()+totalConcepto.getColumna()+totalConcepto.getIdentificador()); 
						celdaLinea = hssfHeader.createCell(cellIdx);
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
						 
						sheet.autoSizeColumn(cellIdx,true);
						
						columna = posicionSemana.get(totalConcepto.getMes().toUpperCase()+totalConcepto.getIdentificador());
						subtotal[columna]+=Double.parseDouble(totalConcepto.getTotal() + "");
						
						subtotal[cellIdx]+=Double.parseDouble(totalConcepto.getTotal() + "");
						
						total=posicionSemana.get("TOTAL"+totalConcepto.getIdentificador());
						subtotal[total]+=Double.parseDouble(totalConcepto.getTotal() + "");
						
						posicion = posicionSemana.get(totalConcepto.getMes().toUpperCase()+totalConcepto.getIdentificador());
						subtotalHor[posicion] += Double.parseDouble(totalConcepto.getTotal() + ""); 
						subtotalHorFinal[posicion] += Double.parseDouble(totalConcepto.getTotal() + ""); 
						
						//pinta totales por mes
						if(!mesAnterior.equals(totalConcepto.getMes().toUpperCase()+totalConcepto.getIdentificador()) || !descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())){
								cellIdx=posicionSemana.get(mesAnterior);
								celdaLinea = hssfHeader.createCell(cellIdx);
								celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHor[cellIdx])));
								sheet.autoSizeColumn(cellIdx,true);
								
								total=posicionSemana.get("TOTAL"+descripcionAnterior);
								subtotalHor[total]+=subtotalHor[cellIdx]; 
								
								subtotalHor[cellIdx]=0.00;
								
							 }
						 
						mesAnterior=totalConcepto.getMes().toUpperCase()+totalConcepto.getIdentificador();
						descripcionAnterior=totalConcepto.getIdentificador();
						descLargaAnterior = totalConcepto.getDesc_larga().toString(); 
						
 
					} else {
						System.out.println(totalConcepto.getMes().toUpperCase()+totalConcepto.getIdentificador());
						 if(!mesAnterior.equals(totalConcepto.getMes().toUpperCase()+totalConcepto.getIdentificador()) || !descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())){
								cellIdx=posicionSemana.get(mesAnterior);
								celdaLinea = hssfHeader.createCell(cellIdx);
								celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHor[cellIdx])));
								sheet.autoSizeColumn(cellIdx,true); 
								
								total=posicionSemana.get("TOTAL"+descripcionAnterior);
								subtotalHor[total]+=subtotalHor[cellIdx];
								
								celdaLinea = hssfHeader.createCell(total);
								celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHor[total])));
								sheet.autoSizeColumn(cellIdx,true); 
								subtotalHor[total]=0.00;
								
								subtotalHor[cellIdx]=0.00;
								
							 }
						
						cellIdx = 0;
						rowIdx = rowIdx + 1;
						hssfHeader = sheet.createRow(rowIdx);
						celdaLinea = hssfHeader.createCell(cellIdx); 
						celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString())); 
						sheet.autoSizeColumn(cellIdx,true);
//						for (int i = 1; i <= subtotal.length-1; i++) {
//							celdaLinea = hssfHeader.createCell(i);
//							celdaLinea.setCellValue(new HSSFRichTextString(0.00+""));  
//						}
						
						cellIdx = posicionSemana.get(totalConcepto.getMes().toUpperCase()+totalConcepto.getColumna()+totalConcepto.getIdentificador()); 
						celdaLinea = hssfHeader.createCell(cellIdx);
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
						sheet.autoSizeColumn(cellIdx,true);
						columna = posicionSemana.get(totalConcepto.getMes().toUpperCase()+totalConcepto.getIdentificador());
						subtotal[columna]+=Double.parseDouble(totalConcepto.getTotal() + "");
						
						subtotal[cellIdx]+=Double.parseDouble(totalConcepto.getTotal() + "");
						
						total=posicionSemana.get("TOTAL"+totalConcepto.getIdentificador());
						subtotal[total]+=Double.parseDouble(totalConcepto.getTotal() + "");
						
						posicion = posicionSemana.get(totalConcepto.getMes().toUpperCase()+totalConcepto.getIdentificador());
						subtotalHor[posicion] += Double.parseDouble(totalConcepto.getTotal() + ""); 
						subtotalHorFinal[posicion] += Double.parseDouble(totalConcepto.getTotal() + ""); 
						
						mesAnterior=totalConcepto.getMes().toUpperCase()+totalConcepto.getIdentificador();
						descripcionAnterior=totalConcepto.getIdentificador();
						descLargaAnterior = totalConcepto.getDesc_larga().toString();
					}
				} else {				 
					 if(!mesAnterior.equals(totalConcepto.getMes().toUpperCase()+totalConcepto.getIdentificador()) || !descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())){
							cellIdx=posicionSemana.get(mesAnterior);
							celdaLinea = hssfHeader.createCell(cellIdx);
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHor[cellIdx]))); 
							sheet.autoSizeColumn(cellIdx,true);
							
							total=posicionSemana.get("TOTAL"+descripcionAnterior);
							subtotalHor[total]+=subtotalHor[cellIdx];

							subtotalHor[cellIdx]=0.00;
							
							
						 }
					
					cellIdx = 0;
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellStyle(styleGroup2);
					celdaLinea.setCellValue(new HSSFRichTextString("SUBTOTAL- " + encabezadoAnterior));
				 
					for (int i = 1; i <= subtotal.length-1; i++) {
						celdaLinea = hssfHeader.createCell(i);
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotal[i])));
						sheet.autoSizeColumn(i,true);
						totalIngresos[i] += subtotal[i];
						subtotal[i] = 0;
						
					}
		
					rowIdx = rowIdx + 1;

					cellIdx = 0;
					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellStyle(styleGroup2);
					celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDescripcion().toString()));
					sheet.autoSizeColumn(cellIdx,true);

					rowIdx = rowIdx + 1;
					hssfHeader = sheet.createRow(rowIdx);
					celdaLinea = hssfHeader.createCell(cellIdx); 
					celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString()));
					sheet.autoSizeColumn(cellIdx,true);
					
//					for (int i = 1; i <= subtotal.length-1; i++) {
//						celdaLinea = hssfHeader.createCell(i);
//						celdaLinea.setCellValue(new HSSFRichTextString(0.00+""));  
//					}
					
					descLargaAnterior = totalConcepto.getDesc_larga().toString();
					System.out.println(totalConcepto.getMes().toUpperCase()+totalConcepto.getColumna()+totalConcepto.getIdentificador());
					cellIdx = posicionSemana.get(totalConcepto.getMes().toUpperCase()+totalConcepto.getColumna()+totalConcepto.getIdentificador()); 
					celdaLinea = hssfHeader.createCell(cellIdx);
					celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
					sheet.autoSizeColumn(cellIdx,true);

				}	
				
				
				
				encabezadoAnterior = totalConcepto.getDescripcion().toString(); 
				
				
				
			}
		 
			//empieza egresos
			else{
				
				  if(totalMovimiento){
					  
					  if(!mesAnterior.equals(totalConcepto.getMes().toUpperCase()+totalConcepto.getIdentificador()) || !descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())){
							cellIdx=posicionSemana.get(mesAnterior);
							celdaLinea = hssfHeader.createCell(cellIdx);
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHor[cellIdx])));
							sheet.autoSizeColumn(cellIdx,true);
							
							total=posicionSemana.get("TOTAL"+descripcionAnterior);
							subtotalHor[total]+=subtotalHor[cellIdx];
							
							celdaLinea = hssfHeader.createCell(total);
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHor[total])));
							sheet.autoSizeColumn(cellIdx,true); 
							subtotalHor[total]=0.00;
							
							subtotalHor[cellIdx]=0.00;
						 }
					  
					  cellIdx = 0;
						rowIdx = rowIdx + 1;
						hssfHeader = sheet.createRow(rowIdx);
						celdaLinea = hssfHeader.createCell(cellIdx);
						celdaLinea.setCellStyle(styleGroup2);
						celdaLinea.setCellValue(new HSSFRichTextString("SUBTOTAL- " + encabezadoAnterior));
						 
						for (int i = 1; i <= subtotal.length-1; i++) {
							celdaLinea = hssfHeader.createCell(i);
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotal[i]))); 
							totalIngresos[i] += subtotal[i];
							sheet.autoSizeColumn(i,true);
							subtotal[i] = 0;
						}

					  rowIdx=rowIdx+1; 
					  cellIdx=0;
					  rowIdx=rowIdx+1;
					  hssfHeader = sheet.createRow(rowIdx);
					  celdaLinea = hssfHeader.createCell(cellIdx); 
					  celdaLinea.setCellStyle(styleGroup3);
					  celdaLinea.setCellValue(new HSSFRichTextString("TOTAL INGRESOS"));
					  
					  for (int i = 1; i <= totalIngresos.length-1; i++) {
							celdaLinea = hssfHeader.createCell(i);
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalIngresos[i])));
							
						}
					  
					  rowIdx=rowIdx+1;
					  cellIdx=0; 
					  rowIdx=rowIdx+1; 
					  hssfHeader = sheet.createRow(rowIdx);   
					  celdaLinea = hssfHeader.createCell(cellIdx);  
					  celdaLinea.setCellStyle(cellStyleI);
					  celdaLinea.setCellValue(new HSSFRichTextString("EGRESOS")); 
					  
					  mesAnterior=totalConcepto.getMes().toUpperCase()+totalConcepto.getIdentificador();
					 descLargaAnterior = totalConcepto.getDesc_larga().toString();
					  
					  totalMovimiento=false; 
				 }
				  
				  if (encabezadoAnterior.equals(totalConcepto.getDescripcion().toString())) {
						if (descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())) {
						 
							// Se crea el primer mes
							cellIdx = posicionSemana.get(totalConcepto.getMes().toUpperCase()+totalConcepto.getColumna()+totalConcepto.getIdentificador()); 
							celdaLinea = hssfHeader.createCell(cellIdx);
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
							
							columna = posicionSemana.get(totalConcepto.getMes().toUpperCase()+totalConcepto.getIdentificador());
							subtotal[columna]+=Double.parseDouble(totalConcepto.getTotal() + "");
							
							subtotal[cellIdx]+=Double.parseDouble(totalConcepto.getTotal() + "");
							
							total=posicionSemana.get("TOTAL"+totalConcepto.getIdentificador());
							subtotal[total]+=Double.parseDouble(totalConcepto.getTotal() + "");
							
							posicion = posicionSemana.get(totalConcepto.getMes().toUpperCase()+totalConcepto.getIdentificador());
							subtotalHor[posicion] += Double.parseDouble(totalConcepto.getTotal() + ""); 
							subtotalHorFinal[posicion] += Double.parseDouble(totalConcepto.getTotal() + ""); 
							
							if(!mesAnterior.equals(totalConcepto.getMes().toUpperCase()+totalConcepto.getIdentificador()) || !descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())){
									cellIdx=posicionSemana.get(mesAnterior);
									celdaLinea = hssfHeader.createCell(cellIdx);
									celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHor[cellIdx])));
									
									total=posicionSemana.get("TOTAL"+descripcionAnterior);
									subtotalHor[total]+=subtotalHor[cellIdx];
									
									subtotalHor[cellIdx]=0.00;
								 }
							 
							mesAnterior=totalConcepto.getMes().toUpperCase()+totalConcepto.getIdentificador();
							descripcionAnterior=totalConcepto.getIdentificador();
							descLargaAnterior = totalConcepto.getDesc_larga().toString(); 
							
	 
						} else {
							
							 if(!mesAnterior.equals(totalConcepto.getMes().toUpperCase()+totalConcepto.getIdentificador()) || !descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())){
									cellIdx=posicionSemana.get(mesAnterior);
									celdaLinea = hssfHeader.createCell(cellIdx);
									celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHor[cellIdx])));
									sheet.autoSizeColumn(cellIdx,true);
									
									
									total=posicionSemana.get("TOTAL"+descripcionAnterior);
									subtotalHor[total]+=subtotalHor[cellIdx];
									
									celdaLinea = hssfHeader.createCell(total);
									celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHor[total])));
									sheet.autoSizeColumn(cellIdx,true); 
									subtotalHor[total]=0.00;
									
									subtotalHor[cellIdx]=0.00;
								 }
							
							cellIdx = 0;
							rowIdx = rowIdx + 1;
							hssfHeader = sheet.createRow(rowIdx);
							celdaLinea = hssfHeader.createCell(cellIdx); 
							celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString())); 
							sheet.autoSizeColumn(cellIdx,true);
							
//							for (int i = 1; i <= subtotal.length-1; i++) {
//								celdaLinea = hssfHeader.createCell(i);
//								celdaLinea.setCellValue(new HSSFRichTextString(0.00+""));  
//							}
							
							cellIdx = posicionSemana.get(totalConcepto.getMes().toUpperCase()+totalConcepto.getColumna()+totalConcepto.getIdentificador()); 
							celdaLinea = hssfHeader.createCell(cellIdx);
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
							sheet.autoSizeColumn(cellIdx,true);
							
							columna = posicionSemana.get(totalConcepto.getMes().toUpperCase()+totalConcepto.getIdentificador());
							subtotal[columna]+=Double.parseDouble(totalConcepto.getTotal() + "");
							
							subtotal[cellIdx]+=Double.parseDouble(totalConcepto.getTotal() + "");
							
							total=posicionSemana.get("TOTAL"+totalConcepto.getIdentificador());
							subtotal[total]+=Double.parseDouble(totalConcepto.getTotal() + "");
							
							posicion = posicionSemana.get(totalConcepto.getMes().toUpperCase()+totalConcepto.getIdentificador());
							subtotalHor[posicion] += Double.parseDouble(totalConcepto.getTotal() + ""); 
							subtotalHorFinal[posicion] += Double.parseDouble(totalConcepto.getTotal() + ""); 
							
							mesAnterior=totalConcepto.getMes().toUpperCase()+totalConcepto.getIdentificador();
							descripcionAnterior=totalConcepto.getIdentificador();
							descLargaAnterior = totalConcepto.getDesc_larga().toString();
						}
					} else {				 
						
						if(!mesAnterior.equals(totalConcepto.getMes().toUpperCase()+totalConcepto.getIdentificador()) || !descLargaAnterior.equals(totalConcepto.getDesc_larga().toString())){
							cellIdx=posicionSemana.get(mesAnterior);
							celdaLinea = hssfHeader.createCell(cellIdx);
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHor[cellIdx])));
							sheet.autoSizeColumn(cellIdx,true);
							
							total=posicionSemana.get("TOTAL"+descripcionAnterior);
							subtotalHor[total]+=subtotalHor[cellIdx];
							
							celdaLinea = hssfHeader.createCell(total);
							celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHor[total])));
							sheet.autoSizeColumn(cellIdx,true); 
							subtotalHor[total]=0.00;
							
							subtotalHor[cellIdx]=0.00;
						 }
							if(pasa){	
								cellIdx = 0;
								rowIdx = rowIdx + 1;
								hssfHeader = sheet.createRow(rowIdx);
								celdaLinea = hssfHeader.createCell(cellIdx);
								celdaLinea.setCellStyle(styleGroup2);
								celdaLinea.setCellValue(new HSSFRichTextString("SUBTOTAL- " + encabezadoAnterior));
								
								for (int i = 1; i <= subtotal.length-1; i++) {
									celdaLinea = hssfHeader.createCell(i);
									celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotal[i]))); 
									sheet.autoSizeColumn(i,true);
									totalEgresos[i] += subtotal[i];
									
									subtotal[i] = 0;
								}
							}
							pasa=true;
						rowIdx = rowIdx + 1;

						cellIdx = 0;
						rowIdx = rowIdx + 1;
						hssfHeader = sheet.createRow(rowIdx);
						celdaLinea = hssfHeader.createCell(cellIdx);
						celdaLinea.setCellStyle(styleGroup2);
						celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDescripcion().toString()));
						sheet.autoSizeColumn(cellIdx,true);

						rowIdx = rowIdx + 1;
						hssfHeader = sheet.createRow(rowIdx);
						celdaLinea = hssfHeader.createCell(cellIdx); 
						celdaLinea.setCellValue(new HSSFRichTextString(totalConcepto.getDesc_larga().toString()));
						sheet.autoSizeColumn(cellIdx,true);
						
//						for (int i = 1; i <= subtotal.length-1; i++) {
//							celdaLinea = hssfHeader.createCell(i);
//							celdaLinea.setCellValue(new HSSFRichTextString(0.00+""));  
//						}
						
						mesAnterior=totalConcepto.getMes().toUpperCase()+totalConcepto.getIdentificador();
						descripcionAnterior=totalConcepto.getIdentificador();
						descLargaAnterior = totalConcepto.getDesc_larga().toString();
						
						cellIdx = posicionSemana.get(totalConcepto.getMes().toUpperCase()+totalConcepto.getColumna()+totalConcepto.getIdentificador()); 
						celdaLinea = hssfHeader.createCell(cellIdx);
						celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalConcepto.getTotal())));
						sheet.autoSizeColumn(cellIdx,true);
						
						columna = posicionSemana.get(totalConcepto.getMes().toUpperCase()+totalConcepto.getIdentificador());
						subtotal[columna]+=Double.parseDouble(totalConcepto.getTotal() + "");
						
						subtotal[cellIdx]+=Double.parseDouble(totalConcepto.getTotal() + "");

						total=posicionSemana.get("TOTAL"+totalConcepto.getIdentificador());
						subtotal[total]+=Double.parseDouble(totalConcepto.getTotal() + "");

						posicion = posicionSemana.get(totalConcepto.getMes().toUpperCase()+totalConcepto.getIdentificador());
						subtotalHor[posicion] += Double.parseDouble(totalConcepto.getTotal() + ""); 
						subtotalHorFinal[posicion] += Double.parseDouble(totalConcepto.getTotal() + ""); 

					}			 
					encabezadoAnterior = totalConcepto.getDescripcion().toString(); 
				  
				  
				
			}
		}
		
		cellIdx=posicionSemana.get(mesAnterior);
		celdaLinea = hssfHeader.createCell(cellIdx);
		celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHor[cellIdx])));
		sheet.autoSizeColumn(cellIdx,true);
		
		
		total=posicionSemana.get("TOTAL"+descripcionAnterior);
		subtotalHor[total]+=subtotalHor[cellIdx];
		
		celdaLinea = hssfHeader.createCell(total);
		celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotalHor[total])));
		sheet.autoSizeColumn(cellIdx,true); 
		subtotalHor[total]=0.00;
		
		subtotalHor[cellIdx]=0.00;
		
		cellIdx = 0;
		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx);
		celdaLinea = hssfHeader.createCell(cellIdx);
		celdaLinea.setCellStyle(styleGroup2);
		celdaLinea.setCellValue(new HSSFRichTextString("SUBTOTAL- " + encabezadoAnterior));
		
		for (int i = 1; i <= subtotal.length-1; i++) {
			celdaLinea = hssfHeader.createCell(i);
			celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(subtotal[i])));
			sheet.autoSizeColumn(i,true);
			totalEgresos[i] += subtotal[i];
			subtotal[i] = 0;
		}
		
		rowIdx = rowIdx + 1;
		cellIdx = 0;
		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx);
		celdaLinea = hssfHeader.createCell(cellIdx); 
		celdaLinea.setCellStyle(styleGroup3);
		celdaLinea.setCellValue(new HSSFRichTextString("TOTAL EGRESOS"));
		
		for (int i = 1; i <= totalEgresos.length-1; i++) {
			celdaLinea = hssfHeader.createCell(i);
			celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(totalEgresos[i]))); 
			sheet.autoSizeColumn(i,true);
		}
		
		rowIdx = rowIdx + 1;
		cellIdx = 0;
		rowIdx = rowIdx + 1;
		hssfHeader = sheet.createRow(rowIdx);
		celdaLinea = hssfHeader.createCell(cellIdx);
		celdaLinea.setCellStyle(styleGroup3);
		celdaLinea.setCellValue(new HSSFRichTextString("SALDO OPERATIVO ACOMULADO"));
		double acomulado=0.00;
		double acomuladoFinal=0.00;
		
		for (int i = 1; i <= totalIngresos.length-1; ) { 		
			for (int j = 1; j <= totalEgresos.length-1; j++) {
				acomulado=totalIngresos[i]-totalEgresos[j]; 
				celdaLinea = hssfHeader.createCell(i);
				celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(acomulado))); 
				if(acomulado < -0){ 
					celdaLinea.setCellStyle(colorLetra);
				} 
				sheet.autoSizeColumn(i,true);
				saldoAcomulado[i]=	acomulado;	 
				acomulado=0.00;
				i++;
			}
		}
		
		rowIdx = rowIdx + 1;

		cellIdx = 0;
		rowIdx = rowIdx + 1;
		hssfHeader1 = sheet.createRow(rowIdx);
		celdaLinea1 = hssfHeader1.createCell(cellIdx); 
		celdaLinea1.setCellStyle(styleGroup3);
		celdaLinea1.setCellValue(new HSSFRichTextString("SALDO DISPONIBLE"));
		boolean primeraVez=true,primeraVez2=true;
		double y=0.00; 
		int ultima=0;
		int numeroSemana = 6,numeroSemana2 = 5;
		cantSaldo[1]=x;
		double xx=0.0;
		double aux=0.00;
		for (int i = 1; i <= saldoAcomulado.length-1; i++) {  
				if(primeraVez){
					y=x+saldoAcomulado[i]; 
					celdaLinea1 = hssfHeader1.createCell(i);
					celdaLinea1.setCellValue(new HSSFRichTextString(formateador.format(y))); 
					sheet.autoSizeColumn(i,true); 
					cantSaldo[i+1]=y;
					xx = y;
					primeraVez = false;
				}else{ 
					if(cantSaldo.length > numeroSemana){
						if(numeroSemana == i){
							if(primeraVez2){
								y = x; 
								cantSaldo[i]= cantSaldo[i-5];
								primeraVez2 = false;
								numeroSemana += 6;
							}else{ 
								cantSaldo[i]= cantSaldo[i-5];
								y = xx;
							} 
						}
					}  
					if(saldoAcomulado.length-1 != i ){
					y=y+saldoAcomulado[i]; 
					celdaLinea1 = hssfHeader1.createCell(i);
					celdaLinea1.setCellValue(new HSSFRichTextString(formateador.format(y)));
					aux=y;
					y = y;
					sheet.autoSizeColumn(i,true);  
					if(total-1 == i){
						celdaLinea1 = hssfHeader1.createCell(i);
						celdaLinea1.setCellValue(new HSSFRichTextString(formateador.format(aux)));
						aux = aux;
					}
					 
						if(total ==  i){
							cantSaldo[i]=x;
							celdaLinea1 = hssfHeader1.createCell(i);
							celdaLinea1.setCellValue(new HSSFRichTextString(formateador.format(aux)));
						}else{
							cantSaldo[i+1]=y;
						}
						xx = y;
					
					}else{
						celdaLinea1 = hssfHeader1.createCell(i);
						celdaLinea1.setCellValue(new HSSFRichTextString(formateador.format(xx)));
						cantSaldo[i]= cantSaldo[i-5];//xx; 
					} 
				 
			}
		}
		 
		for (int i = 0; i <= cantSaldo.length-1; i++) { 	
			System.out.println(cantSaldo[i]);
			
		}
		
		
		hssfHeader = sheet.createRow(filaSaldo); 
		celdaLinea = hssfHeader.createCell(celdaSaldo); 
		celdaLinea.setCellStyle(styleGroup1);
		celdaLinea.setCellValue(new HSSFRichTextString("SALDO INICIAL"));
		
		for (int i = 1; i <= cantSaldo.length-1; i++) { 	
			celdaSaldo += 1;
			celdaLinea = hssfHeader.createCell(celdaSaldo); 
			celdaLinea.setCellValue(new HSSFRichTextString(formateador.format(cantSaldo[i])));	
			
		} 
		return wb;
		
	}

	


	public String exportaExcel(String datos) throws IOException {
		String respuesta = "";
		String sCadEmpresas = "";
		String sCadCajas = "";
		Gson gson = new Gson();
		List<Map<String, Object>> resMap = null;
		List<LlenaComboEmpresasDto> listEmpresas = new ArrayList<LlenaComboEmpresasDto>();
		List<LlenaComboGralDto> listCajas = new ArrayList<LlenaComboGralDto>();
		List<Map<String, String>> parameters = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {
		}.getType());

		Map params = new HashMap();
		// int iUsuario =
		// funciones.convertirCadenaInteger(parameters.get(0).get("usuario").toString());

		return respuesta;
	}

	public HSSFWorkbook reporteFlujo(String grupo, String noempresa2, String empresa, String fechaIni, String fechaFin,
			String reporte, String noGrupo) {
		// TODO Auto-generated method stub
		return null;
	}

}// FIN DE LA CLASE
