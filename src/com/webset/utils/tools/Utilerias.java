package com.webset.utils.tools;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.servlet.ssm.WebContext;
import com.webset.set.utilerias.Funciones;


public class Utilerias {
	public static HSSFWorkbook generarExcel(String sheetName, 
											String[] headers,
											List<List<Object>> data) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(sheetName);

		int rowIdx = 0;
		int cellIdx = 0;

		// Header
		HSSFRow hssfHeader = sheet.createRow(rowIdx);
		HSSFFont font = wb.createFont();
		HSSFCellStyle cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(HSSFCellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.LIGHT_BLUE().getIndex());
		
		for (String string : headers) {
			HSSFCell hssfCell = hssfHeader.createCell(cellIdx++);
			hssfCell.setCellStyle(cellStyle);
			hssfCell.setCellValue(new HSSFRichTextString(string));
		}
		
		//Data
		rowIdx = 1;
		for (Iterator<List<Object>> rows = data.iterator(); rows.hasNext();) {
			List<Object> row = rows.next();
			HSSFRow hssfRow = sheet.createRow(rowIdx++);
			cellIdx = 0;
			
			for (Iterator<Object> cells = row.iterator(); cells.hasNext();) {
				HSSFCell hssfCell = hssfRow.createCell(cellIdx++);
				hssfCell.setCellValue(new HSSFRichTextString((String)cells.next()));
			}
		}
		
		wb.setSheetName(0, sheetName);
		
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn((short)i);
		}	
		
		return wb;
	}
	
	public static HSSFWorkbook generarExcel(String sheetName, 
											String[] headers,
											List<List<Object>> data,
											String[] formato) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(sheetName);
		
		int rowIdx = 0;
		int cellIdx = 0;
		
		// Header
		HSSFRow hssfHeader = sheet.createRow(rowIdx);
		HSSFFont font = wb.createFont();
		HSSFCellStyle cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(HSSFCellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.LIGHT_BLUE().getIndex());
		
		for (String string : headers) {
			HSSFCell hssfCell = hssfHeader.createCell(cellIdx++);
			hssfCell.setCellStyle(cellStyle);
			hssfCell.setCellValue(new HSSFRichTextString(string));
		}
		
		//Data
		rowIdx = 1;
		for (Iterator<List<Object>> rows = data.iterator(); rows.hasNext();) {
			List<Object> row = rows.next();
			HSSFRow hssfRow = sheet.createRow(rowIdx++);
			cellIdx = 0;
			
			for (Iterator<Object> cells = row.iterator(); cells.hasNext(); cellIdx++) {
				HSSFCell hssfCell = hssfRow.createCell(cellIdx);
				hssfCell.setCellValue(new HSSFRichTextString((String)cells.next()));
				if (formato[cellIdx] != null) {
					HSSFDataFormat cf = wb.createDataFormat();
					cellStyle = wb.createCellStyle();
					cellStyle.setDataFormat(cf.getFormat(formato[cellIdx]));
					hssfCell.setCellStyle(cellStyle);
				}
			}
		}
		
		wb.setSheetName(0, sheetName);
		
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn((short)i);
		}		
		
		return wb;
	}

	public static HSSFWorkbook generarExcel(String[] headers,
											List<Map<String, String>> data,
											String[] keys) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();

		int rowIdx = 0;
		int cellIdx = 0;

		// Header
		HSSFRow hssfHeader = sheet.createRow(rowIdx);
		HSSFFont font = wb.createFont();
		HSSFCellStyle cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(HSSFCellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.LIGHT_BLUE().getIndex());
		
		for (String string : headers) {
			HSSFCell hssfCell = hssfHeader.createCell(cellIdx++);
			hssfCell.setCellStyle(cellStyle);
			hssfCell.setCellValue(new HSSFRichTextString(string));
		}
		
		//Data
		rowIdx = 1;
		for (Iterator<Map<String, String>> rows = data.iterator(); rows.hasNext();) {
			Map<String, String> row = rows.next();
			HSSFRow hssfRow = sheet.createRow(rowIdx++);
			cellIdx = 0;
			
			for (String string : keys) {
				HSSFCell hssfCell = hssfRow.createCell(cellIdx++);
				hssfCell.setCellValue(new HSSFRichTextString(row.get(string)));
			}
		}
		
		wb.setSheetName(0, "Hoja 1");
		
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn((short)i);
		}	
		
		return wb;
	}
	
	public static HSSFWorkbook generarExcel(String sheetName, 
											String[] headers,
											List<Map<String, String>> data,
											String[] keys,
											String[] formato) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(sheetName);
		
		int rowIdx = 0;
		int cellIdx = 0;
		
		// Header
		HSSFRow hssfHeader = sheet.createRow(rowIdx);
		HSSFFont font = wb.createFont();
		HSSFCellStyle cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(HSSFCellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.LIGHT_BLUE().getIndex());
		
		for (String string : headers) {
			HSSFCell hssfCell = hssfHeader.createCell(cellIdx++);
			hssfCell.setCellStyle(cellStyle);
			hssfCell.setCellValue(new HSSFRichTextString(string));
		}
		
		//Data
		rowIdx = 1;
		for (Iterator<Map<String, String>> rows = data.iterator(); rows.hasNext();) {
			Map<String, String> row = rows.next();
			HSSFRow hssfRow = sheet.createRow(rowIdx++);
			cellIdx = 0;
			
			for (String string : keys) {
				HSSFCell hssfCell = hssfRow.createCell(cellIdx);
				hssfCell.setCellValue(new HSSFRichTextString(row.get(string)));
				if (formato[cellIdx] != null) {
					HSSFDataFormat cf = wb.createDataFormat();
					cellStyle = wb.createCellStyle();
					cellStyle.setDataFormat(cf.getFormat(formato[cellIdx]));
					hssfCell.setCellStyle(cellStyle);
				}
				cellIdx++;
			}
		}
		
		wb.setSheetName(0, sheetName);
		
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn((short)i);
		}		
		
		return wb;
	}

	public static HSSFWorkbook generarExcel(String titulo,
											String sheetName, 
											String[] headers,
											List<List<Object>> data) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(sheetName);

		int rowIdx = 0;
		int cellIdx = 0;
		
		HSSFRow hssfHeader = sheet.createRow(rowIdx);
		HSSFFont font = wb.createFont();
		HSSFCellStyle cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		//font.setFontHeight(new Short("18"));
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(HSSFCellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.DARK_GREEN().getIndex());
		
		
		HSSFCell celdaTitulo = hssfHeader.createCell(cellIdx);
		celdaTitulo.setCellStyle(cellStyle);
		celdaTitulo.setCellValue(new HSSFRichTextString(titulo));
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));
		
		rowIdx = 1;
		// Header
		hssfHeader = sheet.createRow(rowIdx);
		font = wb.createFont();
		cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(HSSFCellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.LIGHT_BLUE().getIndex());
		
		for (String string : headers) {
			HSSFCell hssfCell = hssfHeader.createCell(cellIdx++);
			hssfCell.setCellStyle(cellStyle);
			hssfCell.setCellValue(new HSSFRichTextString(string));
		}
		
		//Data
		rowIdx = 2;
		for (Iterator<List<Object>> rows = data.iterator(); rows.hasNext();) {
			List<Object> row = rows.next();
			HSSFRow hssfRow = sheet.createRow(rowIdx++);
			cellIdx = 0;
			
			for (Iterator<Object> cells = row.iterator(); cells.hasNext();) {
				HSSFCell hssfCell = hssfRow.createCell(cellIdx++);
				hssfCell.setCellValue(new HSSFRichTextString((String)cells.next()));
			}
		}
		
		wb.setSheetName(0, sheetName);
		
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn((short)i);
		}	
		
		return wb;
	}
	
	public static HSSFWorkbook generarExcel(String titulo,
											String sheetName, 
											String[] headers,
											List<List<Object>> data,
											String[] formato) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(sheetName);
		
		int rowIdx = 0;
		int cellIdx = 0;
		
		HSSFRow hssfHeader = sheet.createRow(rowIdx);
		HSSFFont font = wb.createFont();
		HSSFCellStyle cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		//font.setFontHeight(new Short("18"));
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(HSSFCellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.DARK_GREEN().getIndex());
		
		
		HSSFCell celdaTitulo = hssfHeader.createCell(cellIdx);
		celdaTitulo.setCellStyle(cellStyle);
		celdaTitulo.setCellValue(new HSSFRichTextString(titulo));
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));
		
		rowIdx = 1;
		// Header
		hssfHeader = sheet.createRow(rowIdx);
		font = wb.createFont();
		cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(HSSFCellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.LIGHT_BLUE().getIndex());
		
		for (String string : headers) {
			HSSFCell hssfCell = hssfHeader.createCell(cellIdx++);
			hssfCell.setCellStyle(cellStyle);
			hssfCell.setCellValue(new HSSFRichTextString(string));
		}
		
		//Data
		rowIdx = 2;
		for (Iterator<List<Object>> rows = data.iterator(); rows.hasNext();) {
			List<Object> row = rows.next();
			HSSFRow hssfRow = sheet.createRow(rowIdx++);
			cellIdx = 0;
			
			for (Iterator<Object> cells = row.iterator(); cells.hasNext(); cellIdx++) {
				HSSFCell hssfCell = hssfRow.createCell(cellIdx);
				hssfCell.setCellValue(new HSSFRichTextString((String)cells.next()));
				if (formato[cellIdx] != null) {
					HSSFDataFormat cf = wb.createDataFormat();
					cellStyle = wb.createCellStyle();
					cellStyle.setDataFormat(cf.getFormat(formato[cellIdx]));
					hssfCell.setCellStyle(cellStyle);
				}
			}
		}
		
		wb.setSheetName(0, sheetName);
		
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn((short)i);
		}		
		
		return wb;
	}

	public static HSSFWorkbook generarExcel(String[] headers,
											List<Map<String, String>> data,
											String[] keys, String titulo) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();

		int rowIdx = 0;
		int cellIdx = 0;

		HSSFRow hssfHeader = sheet.createRow(rowIdx);
		HSSFFont font = wb.createFont();
		HSSFCellStyle cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		//font.setFontHeight(new Short("18"));
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(HSSFCellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.DARK_GREEN().getIndex());
		
		
		HSSFCell celdaTitulo = hssfHeader.createCell(cellIdx);
		celdaTitulo.setCellStyle(cellStyle);
		celdaTitulo.setCellValue(new HSSFRichTextString(titulo));
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));
		
		rowIdx = 1;
		// Header
		hssfHeader = sheet.createRow(rowIdx);
		font = wb.createFont();
		cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(HSSFCellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.LIGHT_BLUE().getIndex());
		
		for (String string : headers) {
			HSSFCell hssfCell = hssfHeader.createCell(cellIdx++);
			hssfCell.setCellStyle(cellStyle);
			hssfCell.setCellValue(new HSSFRichTextString(string));
		}
		
		//Data
		rowIdx = 2;
		for (Iterator<Map<String, String>> rows = data.iterator(); rows.hasNext();) {
			Map<String, String> row = rows.next();
			HSSFRow hssfRow = sheet.createRow(rowIdx++);
			cellIdx = 0;
			
			for (String string : keys) {
				HSSFCell hssfCell = hssfRow.createCell(cellIdx++);
				hssfCell.setCellValue(new HSSFRichTextString(row.get(string)));
			}
		}
		
		wb.setSheetName(0, "Hoja 1");
		
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn((short)i);
		}	
		
		return wb;
	}
	
	public static HSSFWorkbook generarExcel(String titulo,
											String sheetName, 
											String[] headers,
											List<Map<String, String>> data,
											String[] keys,
											String[] formato) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(sheetName);
		
		int rowIdx = 0;
		int cellIdx = 0;
		
		HSSFRow hssfHeader = sheet.createRow(rowIdx);
		HSSFFont font = wb.createFont();
		HSSFCellStyle cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		//font.setFontHeight(new Short("18"));
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(HSSFCellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.DARK_GREEN().getIndex());
		
		
		HSSFCell celdaTitulo = hssfHeader.createCell(cellIdx);
		celdaTitulo.setCellStyle(cellStyle);
		celdaTitulo.setCellValue(new HSSFRichTextString(titulo));
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));
		
		rowIdx = 1;
		// Header
		hssfHeader = sheet.createRow(rowIdx);
		font = wb.createFont();
		cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(HSSFCellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.LIGHT_BLUE().getIndex());
		
		for (String string : headers) {
			HSSFCell hssfCell = hssfHeader.createCell(cellIdx++);
			hssfCell.setCellStyle(cellStyle);
			hssfCell.setCellValue(new HSSFRichTextString(string));
		}
		
		//Data
		rowIdx = 2;
		for (Iterator<Map<String, String>> rows = data.iterator(); rows.hasNext();) {
			Map<String, String> row = rows.next();
			HSSFRow hssfRow = sheet.createRow(rowIdx++);
			cellIdx = 0;
			
			for (String string : keys) {
				HSSFCell hssfCell = hssfRow.createCell(cellIdx);
				hssfCell.setCellValue(new HSSFRichTextString(row.get(string)));
				if (formato[cellIdx] != null) {
					HSSFDataFormat cf = wb.createDataFormat();
					cellStyle = wb.createCellStyle();
					cellStyle.setDataFormat(cf.getFormat(formato[cellIdx]));
					hssfCell.setCellStyle(cellStyle);
				}
				cellIdx++;
			}
		}
		
		wb.setSheetName(0, sheetName);
		
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn((short)i);
		}		
		
		return wb;
	}
	
	public static XSSFWorkbook generarExcelX(String sheetName, 
											String[] headers,
											List<List<Object>> data) {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet(sheetName);

		int rowIdx = 0;
		int cellIdx = 0;

		// Header
		XSSFRow hssfHeader = sheet.createRow(rowIdx);
		XSSFFont font = wb.createFont();
		XSSFCellStyle cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(XSSFCellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.LIGHT_BLUE().getIndex());
		
		for (String string : headers) {
			XSSFCell hssfCell = hssfHeader.createCell(cellIdx++);
			hssfCell.setCellStyle(cellStyle);
			hssfCell.setCellValue(new XSSFRichTextString(string));
		}
		
		//Data
		rowIdx = 1;
		for (Iterator<List<Object>> rows = data.iterator(); rows.hasNext();) {
			List<Object> row = rows.next();
			XSSFRow hssfRow = sheet.createRow(rowIdx++);
			cellIdx = 0;
			
			for (Iterator<Object> cells = row.iterator(); cells.hasNext();) {
				XSSFCell hssfCell = hssfRow.createCell(cellIdx++);
				hssfCell.setCellValue(new XSSFRichTextString((String)cells.next()));
			}
		}
		
		wb.setSheetName(0, sheetName);
		
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn((short)i);
		}	
		
		return wb;
	}
	
	public static XSSFWorkbook generarExcelX(String sheetName, 
											String[] headers,
											List<List<Object>> data,
											String[] formato) {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet(sheetName);
		
		int rowIdx = 0;
		int cellIdx = 0;
		
		// Header
		XSSFRow hssfHeader = sheet.createRow(rowIdx);
		XSSFFont font = wb.createFont();
		XSSFCellStyle cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(XSSFCellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.LIGHT_BLUE().getIndex());
		
		for (String string : headers) {
			XSSFCell hssfCell = hssfHeader.createCell(cellIdx++);
			hssfCell.setCellStyle(cellStyle);
			hssfCell.setCellValue(new XSSFRichTextString(string));
		}
		
		//Data
		rowIdx = 1;
		for (Iterator<List<Object>> rows = data.iterator(); rows.hasNext();) {
			List<Object> row = rows.next();
			XSSFRow hssfRow = sheet.createRow(rowIdx++);
			cellIdx = 0;
			
			for (Iterator<Object> cells = row.iterator(); cells.hasNext(); cellIdx++) {
				XSSFCell hssfCell = hssfRow.createCell(cellIdx);
				hssfCell.setCellValue(new XSSFRichTextString((String)cells.next()));
				if (formato[cellIdx] != null) {
					XSSFDataFormat cf = wb.createDataFormat();
					cellStyle = wb.createCellStyle();
					cellStyle.setDataFormat(cf.getFormat(formato[cellIdx]));
					hssfCell.setCellStyle(cellStyle);
				}
			}
		}
		
		wb.setSheetName(0, sheetName);
		
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn((short)i);
		}		
		
		return wb;
	}

	public static XSSFWorkbook generarExcelX(String[] headers,
											List<Map<String, String>> data,
											String[] keys) {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet();

		int rowIdx = 0;
		int cellIdx = 0;

		// Header
		XSSFRow hssfHeader = sheet.createRow(rowIdx);
		XSSFFont font = wb.createFont();
		XSSFCellStyle cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(XSSFCellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.LIGHT_BLUE().getIndex());
		
		for (String string : headers) {
			XSSFCell hssfCell = hssfHeader.createCell(cellIdx++);
			hssfCell.setCellStyle(cellStyle);
			hssfCell.setCellValue(new XSSFRichTextString(string));
		}
		
		//Data
		rowIdx = 1;
		for (Iterator<Map<String, String>> rows = data.iterator(); rows.hasNext();) {
			Map<String, String> row = rows.next();
			XSSFRow hssfRow = sheet.createRow(rowIdx++);
			cellIdx = 0;
			
			for (String string : keys) {
				XSSFCell hssfCell = hssfRow.createCell(cellIdx++);
				hssfCell.setCellValue(new XSSFRichTextString(row.get(string)));
			}
		}
		
		wb.setSheetName(0, "Hoja 1");
		
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn((short)i);
		}	
		
		return wb;
	}
	
	public static XSSFWorkbook generarExcelX(String sheetName, 
											String[] headers,
											List<Map<String, String>> data,
											String[] keys,
											String[] formato) {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet(sheetName);
		
		int rowIdx = 0;
		int cellIdx = 0;
		
		// Header
		XSSFRow hssfHeader = sheet.createRow(rowIdx);
		XSSFFont font = wb.createFont();
		XSSFCellStyle cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(XSSFCellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.LIGHT_BLUE().getIndex());
		
		for (String string : headers) {
			XSSFCell hssfCell = hssfHeader.createCell(cellIdx++);
			hssfCell.setCellStyle(cellStyle);
			hssfCell.setCellValue(new XSSFRichTextString(string));
		}
		
		//Data
		rowIdx = 1;
		for (Iterator<Map<String, String>> rows = data.iterator(); rows.hasNext();) {
			Map<String, String> row = rows.next();
			XSSFRow hssfRow = sheet.createRow(rowIdx++);
			cellIdx = 0;
			
			for (String string : keys) {
				XSSFCell hssfCell = hssfRow.createCell(cellIdx);
				hssfCell.setCellValue(new XSSFRichTextString(row.get(string)));
				if (formato[cellIdx] != null) {
					XSSFDataFormat cf = wb.createDataFormat();
					cellStyle = wb.createCellStyle();
					cellStyle.setDataFormat(cf.getFormat(formato[cellIdx]));
					hssfCell.setCellStyle(cellStyle);
				}
				cellIdx++;
			}
		}
		
		wb.setSheetName(0, sheetName);
		
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn((short)i);
		}		
		
		return wb;
	}

	public static XSSFWorkbook generarExcelX(String titulo,
											String sheetName, 
											String[] headers,
											List<List<Object>> data) {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet(sheetName);

		int rowIdx = 0;
		int cellIdx = 0;
		
		XSSFRow hssfHeader = sheet.createRow(rowIdx);
		XSSFFont font = wb.createFont();
		XSSFCellStyle cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		//font.setFontHeight(new Short("18"));
		cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(XSSFCellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.DARK_GREEN().getIndex());
		
		
		XSSFCell celdaTitulo = hssfHeader.createCell(cellIdx);
		celdaTitulo.setCellStyle(cellStyle);
		celdaTitulo.setCellValue(new XSSFRichTextString(titulo));
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));
		
		rowIdx = 1;
		// Header
		hssfHeader = sheet.createRow(rowIdx);
		font = wb.createFont();
		cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(XSSFCellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.LIGHT_BLUE().getIndex());
		
		for (String string : headers) {
			XSSFCell hssfCell = hssfHeader.createCell(cellIdx++);
			hssfCell.setCellStyle(cellStyle);
			hssfCell.setCellValue(new XSSFRichTextString(string));
		}
		
		//Data
		rowIdx = 2;
		for (Iterator<List<Object>> rows = data.iterator(); rows.hasNext();) {
			List<Object> row = rows.next();
			XSSFRow hssfRow = sheet.createRow(rowIdx++);
			cellIdx = 0;
			
			for (Iterator<Object> cells = row.iterator(); cells.hasNext();) {
				XSSFCell hssfCell = hssfRow.createCell(cellIdx++);
				hssfCell.setCellValue(new XSSFRichTextString((String)cells.next()));
			}
		}
		
		wb.setSheetName(0, sheetName);
		
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn((short)i);
		}	
		
		return wb;
	}
	
	public static XSSFWorkbook generarExcelX(String titulo,
											String sheetName, 
											String[] headers,
											List<List<Object>> data,
											String[] formato) {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet(sheetName);
		
		int rowIdx = 0;
		int cellIdx = 0;
		
		XSSFRow hssfHeader = sheet.createRow(rowIdx);
		XSSFFont font = wb.createFont();
		XSSFCellStyle cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		//font.setFontHeight(new Short("18"));
		cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(XSSFCellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.DARK_GREEN().getIndex());
		
		
		XSSFCell celdaTitulo = hssfHeader.createCell(cellIdx);
		celdaTitulo.setCellStyle(cellStyle);
		celdaTitulo.setCellValue(new XSSFRichTextString(titulo));
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));
		
		rowIdx = 1;
		// Header
		hssfHeader = sheet.createRow(rowIdx);
		font = wb.createFont();
		cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(XSSFCellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.LIGHT_BLUE().getIndex());
		
		for (String string : headers) {
			XSSFCell hssfCell = hssfHeader.createCell(cellIdx++);
			hssfCell.setCellStyle(cellStyle);
			hssfCell.setCellValue(new XSSFRichTextString(string));
		}
		
		//Data
		rowIdx = 2;
		for (Iterator<List<Object>> rows = data.iterator(); rows.hasNext();) {
			List<Object> row = rows.next();
			XSSFRow hssfRow = sheet.createRow(rowIdx++);
			cellIdx = 0;
			
			for (Iterator<Object> cells = row.iterator(); cells.hasNext(); cellIdx++) {
				XSSFCell hssfCell = hssfRow.createCell(cellIdx);
				hssfCell.setCellValue(new XSSFRichTextString((String)cells.next()));
				if (formato[cellIdx] != null) {
					XSSFDataFormat cf = wb.createDataFormat();
					cellStyle = wb.createCellStyle();
					cellStyle.setDataFormat(cf.getFormat(formato[cellIdx]));
					hssfCell.setCellStyle(cellStyle);
				}
			}
		}
		
		wb.setSheetName(0, sheetName);
		
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn((short)i);
		}		
		
		return wb;
	}

	public static XSSFWorkbook generarExcelX(String[] headers,
											List<Map<String, String>> data,
											String[] keys, String titulo) {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet();

		int rowIdx = 0;
		int cellIdx = 0;

		XSSFRow hssfHeader = sheet.createRow(rowIdx);
		XSSFFont font = wb.createFont();
		XSSFCellStyle cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		//font.setFontHeight(new Short("18"));
		cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(XSSFCellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.DARK_GREEN().getIndex());
		
		
		XSSFCell celdaTitulo = hssfHeader.createCell(cellIdx);
		celdaTitulo.setCellStyle(cellStyle);
		celdaTitulo.setCellValue(new XSSFRichTextString(titulo));
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));
		
		rowIdx = 1;
		// Header
		hssfHeader = sheet.createRow(rowIdx);
		font = wb.createFont();
		cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(XSSFCellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.LIGHT_BLUE().getIndex());
		
		for (String string : headers) {
			XSSFCell hssfCell = hssfHeader.createCell(cellIdx++);
			hssfCell.setCellStyle(cellStyle);
			hssfCell.setCellValue(new XSSFRichTextString(string));
		}
		
		//Data
		rowIdx = 2;
		for (Iterator<Map<String, String>> rows = data.iterator(); rows.hasNext();) {
			Map<String, String> row = rows.next();
			XSSFRow hssfRow = sheet.createRow(rowIdx++);
			cellIdx = 0;
			
			for (String string : keys) {
				XSSFCell hssfCell = hssfRow.createCell(cellIdx++);
				hssfCell.setCellValue(new XSSFRichTextString(row.get(string)));
			}
		}
		
		wb.setSheetName(0, "Hoja 1");
		
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn((short)i);
		}	
		
		return wb;
	}
	
	public static XSSFWorkbook generarExcelX(String titulo,
											String sheetName, 
											String[] headers,
											List<Map<String, String>> data,
											String[] keys,
											String[] formato) {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet(sheetName);
		
		int rowIdx = 0;
		int cellIdx = 0;
		
		XSSFRow hssfHeader = sheet.createRow(rowIdx);
		XSSFFont font = wb.createFont();
		XSSFCellStyle cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		//font.setFontHeight(new Short("18"));
		cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(XSSFCellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.DARK_GREEN().getIndex());
		
		
		XSSFCell celdaTitulo = hssfHeader.createCell(cellIdx);
		celdaTitulo.setCellStyle(cellStyle);
		celdaTitulo.setCellValue(new XSSFRichTextString(titulo));
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));
		
		rowIdx = 1;
		// Header
		hssfHeader = sheet.createRow(rowIdx);
		font = wb.createFont();
		cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(XSSFCellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.LIGHT_BLUE().getIndex());
		
		for (String string : headers) {
			XSSFCell hssfCell = hssfHeader.createCell(cellIdx++);
			hssfCell.setCellStyle(cellStyle);
			hssfCell.setCellValue(new XSSFRichTextString(string));
		}
		
		//Data
		rowIdx = 2;
		for (Iterator<Map<String, String>> rows = data.iterator(); rows.hasNext();) {
			Map<String, String> row = rows.next();
			XSSFRow hssfRow = sheet.createRow(rowIdx++);
			cellIdx = 0;
			
			for (String string : keys) {
				XSSFCell hssfCell = hssfRow.createCell(cellIdx);
				hssfCell.setCellValue(new XSSFRichTextString(row.get(string)));
				if (formato[cellIdx] != null) {
					XSSFDataFormat cf = wb.createDataFormat();
					cellStyle = wb.createCellStyle();
					cellStyle.setDataFormat(cf.getFormat(formato[cellIdx]));
					hssfCell.setCellStyle(cellStyle);
				}
				cellIdx++;
			}
		}
		
		wb.setSheetName(0, sheetName);
		
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn((short)i);
		}		
		
		return wb;
	}
	
	public static String indicadorFecha() {
		Calendar calendario = Calendar.getInstance();
		int hora = calendario.get(Calendar.HOUR_OF_DAY);
		int min = calendario.get(Calendar.MINUTE);
		int seg = calendario.get(Calendar.SECOND);
		
		int anio = calendario.get(Calendar.YEAR);
		int mes = calendario.get(Calendar.MONTH) + 1;
		int dia = calendario.get(Calendar.DATE);
		
		return anio + "-" + mes + "-" + dia + " " + 
				hora + "_" + min + "_" + seg;
	}
	
	
	//PARA LECTURA DE ARCHIVOS XLS
			public static List<Map<String, String>> leerExcel(HSSFWorkbook workbook, String[] keys) {
				List<Map<String, String>> hojaDatos = new ArrayList<Map<String, String>>();
				
				HSSFSheet sheet = workbook.getSheetAt(0);
				Iterator<?> rows = sheet.rowIterator();
				rows.next();
				while (rows.hasNext()) {
					HSSFRow row = (HSSFRow) rows.next();
					
					Map<String, String> datosExcel = new HashMap<String, String>();
					
					DataFormatter formatter = new DataFormatter(); //creating formatter using the default locale
					
					for (int i = 0; i < keys.length; i++) {
						HSSFCell cell = row.getCell(i);
						System.out.println(cell);
						if(cell != null && cell.getCellType() == Cell.CELL_TYPE_NUMERIC && 
								HSSFDateUtil.isCellDateFormatted(cell)){
							Funciones funciones = new Funciones();
							datosExcel.put(keys[i],funciones.ponerFechaSola(cell.getDateCellValue()));
						}else{
							datosExcel.put(keys[i],formatter.formatCellValue(cell));
						}
						System.out.println(formatter.formatCellValue(cell));
						/*if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
							datosExcel.put(keys[i], obtenerNumero(cell.getNumericCellValue()));	
						}else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
							datosExcel.put(keys[i], cell.getRichStringCellValue().toString());
						}*/
					}
					
					hojaDatos.add(datosExcel);
				}
				return hojaDatos;
			}

			//AGREGADO EMS 26/10/2015: PARA LA LECTURA DE ARCHIVOS XLSX
			public static List<Map<String, String>> leerExcel(XSSFWorkbook workbook, String[] keys) {
				List<Map<String, String>> hojaDatos = new ArrayList<Map<String, String>>();
				
				XSSFSheet sheet = workbook.getSheetAt(0);
				Iterator<?> rows = sheet.rowIterator();
				rows.next();
				while (rows.hasNext()) {
					XSSFRow row = (XSSFRow) rows.next();
					
					Map<String, String> datosExcel = new HashMap<String, String>();
					DataFormatter formatter = new DataFormatter(); //creating formatter using the default locale
					 //Cell cell = sheet.getRow(i).getCell(0);
					//String j_username = formatter.formatCellValue(cell); //Returns the formatted value of a cell as a String regardless of the cell type.
					for (int i = 0; i < keys.length; i++) {
						XSSFCell cell = row.getCell(i);
						//datosExcel.put(keys[i],formatter.formatCellValue(cell));
						
						if(cell != null && cell.getCellType() == Cell.CELL_TYPE_NUMERIC && 
								HSSFDateUtil.isCellDateFormatted(cell)){
							Funciones funciones = new Funciones();
							datosExcel.put(keys[i],funciones.ponerFechaSola(cell.getDateCellValue()));
						}else{
							datosExcel.put(keys[i],formatter.formatCellValue(cell));
						}
						
						/*if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
							datosExcel.put(keys[i], obtenerNumero(cell.getNumericCellValue()));	
						}else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
							datosExcel.put(keys[i], cell.getRichStringCellValue().toString());
						}*/
					}
					
					hojaDatos.add(datosExcel);
				}
				return hojaDatos;
			}

	
	
	
//			private static String obtenerNumero(double numero) {
//				String num = String.valueOf(numero);
//				int i = num.indexOf(".");
//				if (i != -1) {
//					if (Integer.parseInt(num.substring(i + 1)) == 0) {
//						return num.substring(0, i);
//					}
//				}
//				
//				return num;
//			}
	public static String obtenerHoraMinuto(){
		Calendar calendario = Calendar.getInstance();
		int hora = calendario.get(Calendar.HOUR_OF_DAY);
		int min = calendario.get(Calendar.MINUTE);
		return
		(hora < 10 ? "0" + hora : String.valueOf(hora)) +
		(min < 10 ? "0" + min : String.valueOf(min));		
	}

	public static Date sumarFecha(String fecha, int dia, int mes, int ano){
		SimpleDateFormat formatoFechaddMMyy = new SimpleDateFormat("ddMMyy");
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(formatoFechaddMMyy.parse(fecha)); 	
			calendar.add(Calendar.DATE, dia);
			calendar.add(Calendar.MONTH, mes);
			calendar.add(Calendar.YEAR, ano);
			return calendar.getTime();
		} catch (Exception e) {
			return calendar.getTime();
		}
	}
	
	public static String validarCadenaSQL(String arg0) {
		return arg0.replace("\\", "\\\\").replace("'", "");
	}
	
	public static String validarCadenaSQL(boolean arg0) {
		return String.valueOf(arg0).replace("\\", "\\\\").replace("'", "");
	}
	
	public static String validarCadenaSQL(int arg0) {
		return String.valueOf(arg0).replace("\\", "\\\\").replace("'", "");
	}
	
	public static String validarCadenaSQL(float arg0) {
		return String.valueOf(arg0).replace("\\", "\\\\").replace("'", "");
	}
	
	public static String validarCadenaSQL(double arg0) {
		return String.valueOf(arg0).replace("\\", "\\\\").replace("'", "");
	}
	
	public static String validarCadenaSQL(char arg0) {
		return String.valueOf(arg0).replace("\\", "\\\\").replace("'", "");
	}
	
	public static String validarCadenaSQL(long arg0) {
		return String.valueOf(arg0).replace("\\", "\\\\").replace("'", "");
	}
	
	public static String validarCadenaSQL(short arg0) {
		return String.valueOf(arg0).replace("\\", "\\\\").replace("'", "");
	}
	
	public static String validarCadenaSQL(byte arg0) {
		return String.valueOf(arg0).replace("\\", "\\\\").replace("'", "");
	}

	public static boolean haveSession(WebContext webContext) {
		return webContext.getSession().getAttribute("codSession") != null;
	}

	public static boolean tienePermiso(WebContext webContext, int i) {
		return webContext.getSession().getAttribute("facultades") != null &&
				((Map<Integer, Boolean>)webContext.getSession().getAttribute("facultades")).get(i) != null;
	}
	
	public static boolean parseaJsonExcel(String json){
		boolean result = true;
		try{
			Gson gson = new Gson();
			gson.fromJson(json, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		} catch (Exception e) {
			//bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			//+ "P:Egresos, C:PagoPropuestasBusiness, M:parceaJsonExcel");
			e.printStackTrace();
			result = false;
		}	
		return result;
		
	}

	public static HSSFWorkbook generarExcel(List<DatosExcel> d, String string) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
