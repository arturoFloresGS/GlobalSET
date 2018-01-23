package com.webset.set.financiamiento.business;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.plaf.synth.SynthSpinnerUI;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.jdbc.core.JdbcTemplate;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.financiamiento.dao.ReporteAnalisisLineasCreditoDao;
import com.webset.set.financiamiento.dto.AnalisisLineasCreditoDto;
import com.webset.set.financiamiento.service.ReporteAnalisisLineasCService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public class ReporteAnalisisLineasCBusinessImpl implements ReporteAnalisisLineasCService{
	private Bitacora bitacora = new Bitacora();
	private ReporteAnalisisLineasCreditoDao reporteAnalisisLineasCreditoDao;
	private ConsultasGenerales consultasGenerales;
	private JdbcTemplate jdbcTemplate;

	public ReporteAnalisisLineasCreditoDao getReportePasivosFDao() {
		return reporteAnalisisLineasCreditoDao;
	}

	public void setReporteAnalisisLineasCreditoDao(ReporteAnalisisLineasCreditoDao reporteAnalisisLineasCreditoDao) {
		this.reporteAnalisisLineasCreditoDao = reporteAnalisisLineasCreditoDao;
	}

	@Override
	public List<LlenaComboGralDto> llenarCmbEmpresa(int piNoUsuario,boolean grupo, int noGrupo) {
		if(!grupo)
			return reporteAnalisisLineasCreditoDao.llenarCmbEmpresa(piNoUsuario);
		else
			return reporteAnalisisLineasCreditoDao.llenarCmbEmpresa(piNoUsuario,noGrupo);
	}
	@Override
	public List<LlenaComboGralDto> obtenerGruposEmpresa() {
		return reporteAnalisisLineasCreditoDao.obtenerGruposEmpresa();
	}

	@Override
	public List<LlenaComboGralDto> obtenerTipoFinanciamiento(String vsTipoMenu) {
		return reporteAnalisisLineasCreditoDao.obtenerTipoFinanciamiento(vsTipoMenu);
	}

	@Override
	public List<AnalisisLineasCreditoDto> obtenerAnalisisLineas(int empresa, int tipoFinanciamiento,
			boolean vbTipoCambio, String vsMenu, String fechaInicio, String fechaFin) {
		return reporteAnalisisLineasCreditoDao.obtenerAnalisisLineas(empresa,tipoFinanciamiento,vbTipoCambio,vsMenu,fechaInicio,fechaFin);
	}

	@Override
	public int obtenerValoresDivisa() {
		int valor=0;
		List<AnalisisLineasCreditoDto> lista=new ArrayList<AnalisisLineasCreditoDto>();
		lista=reporteAnalisisLineasCreditoDao.obtenerValoresDivisa();
		if(!lista.isEmpty()){
			for (AnalisisLineasCreditoDto dto : lista) {
				if(dto.getValor()==0){
					valor=1;
					return valor;
				}

			}

		}else{
			valor=1;
		}
		return valor;

	}
	@Override
	public HSSFWorkbook excelAnalisisLineasCreditoResumen(String analisis) {
		HSSFWorkbook hb=null;
		Gson gson = new Gson();
		try {
			List<Map<String, String>> paramAnalisis = gson.fromJson(analisis,
					new TypeToken<ArrayList<Map<String, String>>>() {
			}.getType());
			hb=generarExcel(new String[]{
					"Descripción",
					"Líneas Autorizadas",
					"Dispuestas",
					"Disponible",
			}, paramAnalisis, new String[]{
					"descripcion",
					"lineasAut",
					"dispuestas",
					"disponibles",
					//"color"
			});
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: reportePersonas");
		}return hb;
	}	
	public static HSSFWorkbook generarExcel(String[] headers,
			List<Map<String, String>> data,
			String[] keys) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		int rowIdx = 0;
		int cellIdx = 0;
		String[] renglon;
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		//Encabezado
		HSSFRow hssfHeader = sheet.createRow(rowIdx);
		HSSFFont font = wb.createFont();
		HSSFCellStyle cellStyle = wb.createCellStyle();
		font.setCharSet(Font.DEFAULT_CHARSET); 
		font.setBold(true);
		font.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(CellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.BLUE_GREY().getIndex());
		HSSFCellStyle cellStyle2 = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle2.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle2.setFillPattern(CellStyle.FINE_DOTS );
		cellStyle2.setFont(font);
		cellStyle2.setFillForegroundColor(new HSSFColor.CORNFLOWER_BLUE().getIndex());
		hssfHeader = sheet.createRow(rowIdx);
		HSSFCell celdaTitulo = hssfHeader.createCell(cellIdx);
		celdaTitulo.setCellStyle(cellStyle);
		celdaTitulo.setCellValue(new HSSFRichTextString("RESUMEN ANÁLISIS DE LÍNEAS DE CRÉDITO"));
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));
		//Renglón 3 
		rowIdx = 3;
		cellIdx = 0;

		hssfHeader = sheet.createRow(rowIdx);
		//Encabezado tabla
		cellIdx = 0;
		rowIdx = 3;
		hssfHeader = sheet.createRow(rowIdx);
		font = wb.createFont();
		cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(CellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.LIGHT_BLUE().getIndex());
		for (String string : headers) {
			HSSFCell hssfCell = hssfHeader.createCell(cellIdx++);
			hssfCell.setCellStyle(cellStyle);
			hssfCell.setCellValue(new HSSFRichTextString(string));
		}
		rowIdx = 4;
		for (Iterator<Map<String, String>> rows = data.iterator(); rows.hasNext();) {
			Map<String, String> row = rows.next();
			HSSFRow hssfRow = sheet.createRow(rowIdx++);
			cellIdx = 0;
			for (String string : keys) {	
				HSSFCell hssfCell = hssfRow.createCell(cellIdx++);
				hssfCell.setCellValue(new HSSFRichTextString(row.get(string)));
			}
		}
		font.setBold(true);
		wb.setSheetName(0, "Resumen Analisis de Lineas de Credito");
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn((short)i);
		}	
		return wb;
	}		

	@Override
	public HSSFWorkbook excelAnalisisLineasCredito(String analisis) {
		HSSFWorkbook hb=null;
		Gson gson = new Gson();
		try {
			List<Map<String, String>> paramAnalisis = gson.fromJson(analisis,
					new TypeToken<ArrayList<Map<String, String>>>() {
			}.getType());
			hb=generarExcel2(new String[]{
					"Descripción",
					"Tipo Crédito",
					"Banco / Arrendadora",
					"Línea Autorizada",
					"Crédito dispuesto",
					"Tasa",
					"Fec. Vencimiento",
					"Factoraje",
					"Anticipagos",
					"Tot. Línea Dispuesta",
					"Tot. Línea Disponible"
			}, paramAnalisis, new String[]{
					"nomEmpresa",
					"tipoCredito",
					"descBanco",
					"linea",
					"pasivo",
					"tasa",
					"fecVencimiento",
					"factoraje",
					"anticipagos",
					"totalLinea",
					"totalLineaDisp",
					//"color"
			});
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: reportePersonas");
		}return hb;
	}	
	public static HSSFWorkbook generarExcel2(String[] headers,
			List<Map<String, String>> data,
			String[] keys) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		int rowIdx = 0;
		int cellIdx = 0;
		String[] renglon;
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		//Encabezado
		HSSFRow hssfHeader = sheet.createRow(rowIdx);
		HSSFFont font = wb.createFont();
		HSSFCellStyle cellStyle = wb.createCellStyle();
		font.setCharSet(Font.DEFAULT_CHARSET); 
		font.setBold(true);
		font.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(CellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.BLUE_GREY().getIndex());
		HSSFCellStyle cellStyle2 = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle2.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle2.setFillPattern(CellStyle.FINE_DOTS );
		cellStyle2.setFont(font);
		cellStyle2.setFillForegroundColor(new HSSFColor.CORNFLOWER_BLUE().getIndex());
		hssfHeader = sheet.createRow(rowIdx);
		HSSFCell celdaTitulo = hssfHeader.createCell(cellIdx);
		celdaTitulo.setCellStyle(cellStyle);
		celdaTitulo.setCellValue(new HSSFRichTextString("ANÁLISIS DE LÍNEAS DE CRÉDITO"));
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));
		//Renglón 3 
		rowIdx = 3;
		cellIdx = 0;

		hssfHeader = sheet.createRow(rowIdx);
		//Encabezado tabla
		cellIdx = 0;
		rowIdx = 3;
		hssfHeader = sheet.createRow(rowIdx);
		font = wb.createFont();
		cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(CellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.LIGHT_BLUE().getIndex());
		for (String string : headers) {
			HSSFCell hssfCell = hssfHeader.createCell(cellIdx++);
			hssfCell.setCellStyle(cellStyle);
			hssfCell.setCellValue(new HSSFRichTextString(string));
		}
		rowIdx = 4;
		for (Iterator<Map<String, String>> rows = data.iterator(); rows.hasNext();) {
			Map<String, String> row = rows.next();
			HSSFRow hssfRow = sheet.createRow(rowIdx++);
			cellIdx = 0;
			for (String string : keys) {	
				HSSFCell hssfCell = hssfRow.createCell(cellIdx++);
				hssfCell.setCellValue(new HSSFRichTextString(row.get(string)));
			}
		}
		font.setBold(true);
		wb.setSheetName(0, "Analisis de Lineas de Credito");
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn((short)i);
		}	
		return wb;
	}		
	@Override
	public List<AnalisisLineasCreditoDto> obtenerResumen(int empresa, int tipoFinanciamiento, String vsMenu) {
		return reporteAnalisisLineasCreditoDao.obtenerResumen(empresa,tipoFinanciamiento,vsMenu);
	}


}
