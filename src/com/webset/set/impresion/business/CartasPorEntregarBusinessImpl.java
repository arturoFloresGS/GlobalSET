package com.webset.set.impresion.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.displaytag.model.Column;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.webset.set.impresion.dao.CartasPorEntregarDao;
import com.webset.set.impresion.dto.CartasPorEntregarDto;
import com.webset.set.impresion.service.CartasPorEntregarService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utileriasmod.dto.MantenimientoSolicitantesFirmantesDto;
import com.webset.utils.tools.Utilerias;
			 
public class CartasPorEntregarBusinessImpl implements CartasPorEntregarService{

	private CartasPorEntregarDao cartasPorEntregarDao;
	private Funciones funciones = new Funciones();

	public CartasPorEntregarDao getCartasPorEntregarDao() {
		return cartasPorEntregarDao;
	}


	public void setCartasPorEntregarDao(CartasPorEntregarDao cartasPorEntregarDao) {
		this.cartasPorEntregarDao = cartasPorEntregarDao;
	}


	Bitacora bitacora = new Bitacora();
	
	public List<CartasPorEntregarDto> obtenerCartasE(String folio, String idBanco, String tipo, String estatus, String fechaIni, String fechaFin){
		System.out.println("entro bussinnes obtenerCartas");
		List<CartasPorEntregarDto> listaCartas = new ArrayList<CartasPorEntregarDto>();
		Gson gson = new Gson();
		
		try {
			listaCartas = cartasPorEntregarDao.obtenerCartasE(folio, idBanco, tipo,estatus, fechaIni, fechaFin);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:CartasPorEntregarBusinessImpl, M:obtenerCartasE");
		}
		
		return listaCartas;
	}
	
	@Override
	public HSSFWorkbook reporteCartasEmitidas(String folio, String idBanco, 
			String tipo, String estatus, String fechaIni, String fechaFin) {
		
		HSSFWorkbook wb = new HSSFWorkbook();
		
		String[] headers = new String[]{
			"Folio",
			"No. Banco",
			"Banco",
			"Solicitante",
			"Fecha Impresion",
			"Tipo",
			"Estatus",
			"Motivo de Cancelaci�n",
			"Fecha de Entrega",
			"Usuario Entrega"
		};
		
		String[] keys = new String[]{
			"idEmision", 
		 	"idBanco", 
		 	"descBanco", 
		 	"nomSolicitante",
			"fechaI",
			"tipo",
		 	"estatus",
			"motCancelacion",
			"fechaE",
			"usuEntrega"
		};
		
		String[] headersD = new String[]{
			"ClaveP", 
			"Cuenta", 
			"Folio Set", 
			"Nom. Empresa",
			"No. Proveedor",
			"Nombre. Proveedor", 
			"Nombre Banco",
			"Id. Chequera", 
			"No. Cheque", 
			"Importe", 
			"Divisa",
			"Id. Emision",
			//"Beneficiario",
			"Tipo Carta"
		};
		
		String[] keysD = new String[]{
				"ClaveP", 
				"Cuenta", 
				"FolioSet", 
				"NomEmpresa",
				"NoProveedor",
				"NombreProveedor", 
				"NombreBanco",
				"IdChequera", 
				"NoCheque", 
				"Importe", 
				"Divisa",
				"IdEmision",
				//"Beneficiario",
				"TipoC"
			};
		
		try {
			
			List<Map<String, String>> data = cartasPorEntregarDao.obtenerCartasEmitidas(
					folio, idBanco, tipo, estatus, fechaIni, fechaFin);
			
			HSSFSheet sheet = wb.createSheet();

			int rowIdx = 0;
			int cellIdx = 0;

			
			for (Iterator<Map<String, String>> rows = data.iterator(); rows.hasNext();) {
				//Carta----------------------------------------------------------------
				cellIdx=0;
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
				celdaTitulo.setCellValue(new HSSFRichTextString("Carta"));
				
				sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, headers.length - 1));
				
				rowIdx++;
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
				rowIdx++;
				
				Map<String, String> row = rows.next();
				HSSFRow hssfRow = sheet.createRow(rowIdx++);
				cellIdx = 0;
				
				for (String string : keys) {
					HSSFCell hssfCell = hssfRow.createCell(cellIdx++);
					hssfCell.setCellValue(new HSSFRichTextString(row.get(string)));
				}
				//---------------------------------------------------------------------
				
				//Detalle----------------------------------------------------------------
				List<Map<String, String>> detalle = cartasPorEntregarDao.obtenerCartasDetalle(
						row.get("idEmision"), row.get("estatus"), row.get("tipo"));
				cellIdx = 0;
				
				HSSFRow hssfHeaderD = sheet.createRow(rowIdx);
				HSSFFont fontD = wb.createFont();
				HSSFCellStyle cellStyleD = wb.createCellStyle();
				fontD.setColor(new HSSFColor.WHITE().getIndex());
				//font.setFontHeight(new Short("18"));
				cellStyleD.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				cellStyleD.setFillPattern(HSSFCellStyle.FINE_DOTS );
				cellStyleD.setFont(fontD);
				cellStyleD.setFillForegroundColor(new HSSFColor.DARK_GREEN().getIndex());
				
				
				HSSFCell celdaTituloD = hssfHeaderD.createCell(cellIdx);
				celdaTituloD.setCellStyle(cellStyleD);
				celdaTituloD.setCellValue(new HSSFRichTextString("Detalle"));
				
				sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, headersD.length - 1));
				
				rowIdx++;
				// Header
				hssfHeaderD = sheet.createRow(rowIdx);
				fontD = wb.createFont();
				cellStyleD = wb.createCellStyle();
				fontD.setColor(new HSSFColor.WHITE().getIndex());
				cellStyleD.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				cellStyleD.setFillPattern(HSSFCellStyle.FINE_DOTS );
				cellStyleD.setFont(fontD);
				cellStyleD.setFillForegroundColor(new HSSFColor.LIGHT_BLUE().getIndex());
				
				for (String stringD : headersD) {
					HSSFCell hssfCellD = hssfHeaderD.createCell(cellIdx++);
					hssfCellD.setCellStyle(cellStyleD);
					hssfCellD.setCellValue(new HSSFRichTextString(stringD));
				}
				
				//Data
				rowIdx++;
				for (Iterator<Map<String, String>> rowsD = detalle.iterator(); rowsD.hasNext();) {
					Map<String, String> rowD = rowsD.next();
					HSSFRow hssfRowD = sheet.createRow(rowIdx++);
					cellIdx = 0;
					
					for (String stringD : keysD) {
						HSSFCell hssfCellD = hssfRowD.createCell(cellIdx++);
						hssfCellD.setCellValue(new HSSFRichTextString(rowD.get(stringD)));
					}
				}
				rowIdx+=5;
				//-----------------------------------------------------------------------
				
				
			}
			
			wb.setSheetName(0, "Hoja 1");
			
			for (int i = 0; i < headersD.length; i++) {
				sheet.autoSizeColumn((short)i);
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(
					new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Impresion, C: CartasPorEntregarBusinessImpl, "
					+ "M: reporteCartasEmitidas");
		} return wb;
	}
	
	@Override
	public List<CartasPorEntregarDto> obtieneDatos(String idEmision, String idBanco){
			List<CartasPorEntregarDto> resultado = new ArrayList<CartasPorEntregarDto>();
			try {
				
				resultado = cartasPorEntregarDao.obtieneDatos(idEmision, idBanco);
			} catch (Exception e) {
				e.printStackTrace();
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasPorEntregarBusinessImpl, M: obtieneDatos");
			}return resultado;
	}
	
	
	public List<CartasPorEntregarDto> obtenerCartas(String folio, String estatus, String tipo){
		System.out.println("entro bussinnes obtenerCartas");
		List<CartasPorEntregarDto> listaCartas = new ArrayList<CartasPorEntregarDto>();
		Gson gson = new Gson();
		try {
			listaCartas = cartasPorEntregarDao.obtenerCartas(folio, estatus, tipo);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:CartasPorEntregarBusinessImpl, M:obtenerCartas");
		}
		
		return listaCartas;
	}
	
	
	public List<MantenimientoSolicitantesFirmantesDto> obtenerSolicitantes(String tipoSol){
		List<MantenimientoSolicitantesFirmantesDto> listaSolcitantes = new ArrayList<MantenimientoSolicitantesFirmantesDto>();
		try {
			listaSolcitantes = cartasPorEntregarDao.obtenerSolicitantes(tipoSol);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:CartasPorEntregarBusinessImpl, M:obtenerSolicitantes");
		}
		
		return listaSolcitantes;
	}
	
	
	public String cambiarEstatus(String jsonDatos){
		System.out.println("businnes cambiar");
		String mensaje = "";
		Gson gson = new Gson();
		try {
			List<Map<String, String>> datos = gson.fromJson(jsonDatos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			mensaje = cartasPorEntregarDao.actualizarEstatus(datos);
			
		} catch (Exception e) {
			mensaje = "Error en la convercion de datos";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:CartasPorEntregarBusinessImpl, M:cambiarEstatus");
		}
		
		return mensaje;
	}
	

	public String exportarExcel(String json){
		String mensaje = "";
		Gson gson = new Gson();
		List<Map<String, String>> parameters = gson.fromJson(json, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());

		try {
			
			HSSFWorkbook wb = Utilerias.generarExcel(new String[]{
					"EMPRESA",
		            "NUMERO PROVEEDOR",
		            "NOMBRE PROVEEDOR",
		            "BANCO",
		            "CHEQUERA",
		            "NUMERO CHEQUE",
		            "IMPORTE",
		            "DIVISA"
				}, 
				parameters, 
				new String[]{
						"nomEmpresa", 
						"noProveedor",
						"nombreProveedor",
						"nombreBanco",
						"idChequera",
						"noCheque",														
						"importe", 
						"divisa" 
				});		
			
			mensaje = ConstantesSet.RUTA_EXCEL + "cheques_entregar " + Utilerias.indicadorFecha() +".xls";
			File outputFile = new File(mensaje);
			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
			
		} catch (IOException e) {
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Egresos, C:ConsultaPropuestasDao, M:exportarExcel");
        	mensaje = "";
        } catch(Exception e){
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Egresos, C:ConsultaPropuestasDao, M:exportarExcel");
        	mensaje = "";
        
		}
		
		return mensaje;
	}
	
	/******************************************/
	/********GETTERS AND SETTERS***************/
	/******************************************/
	
	/*@Override
	public String generaPDF(List<Map<String, String>> valor, List<Map<String, String>> beneficiarios, String dif, String tipo, String fechaImp) {
		String nomArch="";
		try {
			
			Document pdf = new Document(PageSize.A4, 30, 30, 30, 30);
			pdf.addTitle("Cartas de Cheques");
			System.out.println(ConstantesSet.CARPETA_PDF);
			nomArch = ConstantesSet.CARPETA_PDF + "carta"+Utilerias.indicadorFecha()+".pdf";
			PdfWriter editor = PdfWriter.getInstance(pdf, new FileOutputStream(nomArch));
			editor.setInitialLeading(16);
			Rectangle rct = new Rectangle(36, 54, 559, 788);
			editor.setBoxSize("art", rct);
			pdf.open();
			
			Paragraph parrafo = new Paragraph(valor.get(0).get("empresa"), FontFactory.getFont(FontFactory.COURIER_BOLD, 18, BaseColor.BLACK));
			parrafo.setAlignment(Element.ALIGN_CENTER);
			parrafo.setSpacingBefore (2);
			Paragraph parrafo1 = new Paragraph(valor.get(0).get("calle")+", Col. "+valor.get(0).get("colonia")+", C.P. "
					+valor.get(0).get("cp")+", "+valor.get(0).get("ciudad")+", "+valor.get(0).get("estado"),FontFactory.getFont(FontFactory.COURIER, 7));
			parrafo1.setAlignment(Element.ALIGN_CENTER);
			
			String vec[]=fechaImp.split("/");
			int dia=Integer.parseInt(vec[0]);
			int mes=Integer.parseInt(vec[1]);
			int anio=Integer.parseInt(vec[2]);
			Paragraph parrafo2 = new Paragraph(valor.get(0).get("lugarFecha")+", A "
			+DateFormat.getDateInstance(DateFormat.LONG).format(new GregorianCalendar(anio ,mes-1,dia).getTime())+".",
			FontFactory.getFont(FontFactory.COURIER, 10));
			parrafo2.setAlignment(Element.ALIGN_RIGHT);
			
			
			Paragraph parrafo3 = new Paragraph(valor.get(0).get("b1"), FontFactory.getFont(FontFactory.COURIER_BOLD, 10, BaseColor.BLACK));
			parrafo3.setAlignment(Element.ALIGN_LEFT);
			Paragraph parrafo4 = new Paragraph(valor.get(0).get("b2"), FontFactory.getFont(FontFactory.COURIER_BOLD, 10, BaseColor.BLACK));
			parrafo4.setAlignment(Element.ALIGN_LEFT);
			Paragraph parrafo5 = new Paragraph(valor.get(0).get("b3"), FontFactory.getFont(FontFactory.COURIER_BOLD, 10, BaseColor.BLACK));
			parrafo5.setAlignment(Element.ALIGN_LEFT);
			
			
				Chunk l1 = new Chunk("				"+valor.get(0).get("c1")+" ", FontFactory.getFont(FontFactory.COURIER,10,BaseColor.BLACK));
				Chunk l2 = new Chunk(valor.get(0).get("solicitante")+" " , FontFactory.getFont(FontFactory.COURIER_BOLD,10,BaseColor.BLACK));
				Chunk l3 = new Chunk(valor.get(0).get("c2"), FontFactory.getFont(FontFactory.COURIER,10,BaseColor.BLACK));
				Chunk l4 = new Chunk(valor.get(0).get("identificacion")+" ", FontFactory.getFont(FontFactory.COURIER,10,BaseColor.BLACK));
				Chunk l5 = new Chunk(valor.get(0).get("c3")+".", FontFactory.getFont(FontFactory.COURIER,10,BaseColor.BLACK));
			

			
			

			
			Chunk l6 = new Chunk("				"+valor.get(0).get("c4")+" "+valor.get(0).get("cuenta")+" ", FontFactory.getFont(FontFactory.COURIER,10,BaseColor.BLACK));
			Chunk l7 = new Chunk(valor.get(0).get("c5")+" " , FontFactory.getFont(FontFactory.COURIER,10,BaseColor.BLACK));
			Chunk l8 = new Chunk(valor.get(0).get("empresa")+".", FontFactory.getFont(FontFactory.COURIER_BOLD,10,BaseColor.BLACK));
			Chunk l9 = new Chunk("				"+valor.get(0).get("c6")+".", FontFactory.getFont(FontFactory.COURIER,10,BaseColor.BLACK));


			
			Paragraph parrafo7 = new Paragraph("Solicitante", FontFactory.getFont(FontFactory.COURIER_BOLD, 10, BaseColor.BLACK));
			parrafo7.setAlignment(Element.ALIGN_CENTER);
			
			Paragraph parrafo8 = new Paragraph(valor.get(0).get("solicitante"), FontFactory.getFont(FontFactory.COURIER_BOLD, 10, BaseColor.BLACK));
			parrafo8.setAlignment(Element.ALIGN_CENTER);
			
			Paragraph parrafo9 = new Paragraph("A T E N T A M E N T E", FontFactory.getFont(FontFactory.COURIER_BOLD, 10, BaseColor.BLACK));
			parrafo9.setAlignment(Element.ALIGN_CENTER);
			
			Paragraph parrafo10 = new Paragraph("Folio: "+valor.get(0).get("emision"), FontFactory.getFont(FontFactory.COURIER, 7, BaseColor.BLACK));
			parrafo10.setAlignment(Element.ALIGN_RIGHT);
			
			PdfPTable table = new PdfPTable(2);
			PdfPCell cell = new PdfPCell();
			table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
		    table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
			Paragraph parrafot = new Paragraph(valor.get(0).get("autorizacion1"), FontFactory.getFont(FontFactory.COURIER_BOLD, 10, BaseColor.BLACK));
			parrafot.setAlignment(Element.ALIGN_CENTER);
			table.addCell(parrafot);
			Paragraph parrafot2 = new Paragraph(valor.get(0).get("autorizacion2"), FontFactory.getFont(FontFactory.COURIER_BOLD, 10, BaseColor.BLACK));
			parrafot2.setAlignment(Element.ALIGN_CENTER);
			table.addCell(parrafot2);
			Paragraph parrafot3 = new Paragraph("AUTORIZO", FontFactory.getFont(FontFactory.COURIER_BOLD, 10, BaseColor.BLACK));
			parrafot3.setAlignment(Element.ALIGN_CENTER);
			table.addCell(parrafot3);
			Paragraph parrafot4 = new Paragraph("AUTORIZO", FontFactory.getFont(FontFactory.COURIER_BOLD, 10, BaseColor.BLACK));
			parrafot4.setAlignment(Element.ALIGN_CENTER);
			table.addCell(parrafot4);
			table.addCell(cell);
			table.getHorizontalAlignment();
			
			
			PdfPTable table2 = new PdfPTable(2);
			PdfPCell cell2 = new PdfPCell();
			table2.setWidthPercentage(100);
			table2.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		    table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
		    table2.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
			Paragraph parrafoa2 = new Paragraph("BENEFICIARIO", FontFactory.getFont(FontFactory.COURIER_BOLD, 9, BaseColor.BLACK));
			parrafoa2.setAlignment(Element.ALIGN_JUSTIFIED);
			table2.addCell(parrafoa2);
			Paragraph parrafoa3 = new Paragraph("MONTO", FontFactory.getFont(FontFactory.COURIER_BOLD, 9, BaseColor.BLACK));
			parrafoa3.setAlignment(Element.ALIGN_JUSTIFIED);
			table2.addCell(parrafoa3);
			for (Map<String, String> beneficiario: beneficiarios) {
					Paragraph parrafoa4 = new Paragraph(beneficiario.get("beneficiario"), FontFactory.getFont(FontFactory.COURIER, 9, BaseColor.BLACK));
					parrafoa4.setAlignment(Element.ALIGN_JUSTIFIED);
					table2.addCell(parrafoa4);
					Paragraph parrafoa5 = new Paragraph("$ "+funciones.ponerFormatoImporte(Double.parseDouble(beneficiario.get("importe"))), FontFactory.getFont(FontFactory.COURIER, 9, BaseColor.BLACK));
					parrafoa5.setAlignment(Element.ALIGN_RIGHT);
					table2.addCell(parrafoa5);	
			}
			table2.addCell(cell2);
			table2.getHorizontalAlignment();
			
			PdfPTable tableCert = new PdfPTable(3);
			PdfPCell cell6 = new PdfPCell();
			tableCert.setWidthPercentage(100);
			tableCert.getDefaultCell().setBorder(Rectangle.NO_BORDER);
			tableCert.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			tableCert.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
			Paragraph parrafocert = new Paragraph("CHEQUE", FontFactory.getFont(FontFactory.COURIER_BOLD, 9, BaseColor.BLACK));
			parrafocert.setAlignment(Element.ALIGN_JUSTIFIED);
			tableCert.addCell(parrafocert);
			Paragraph parrafocert2 = new Paragraph("BENEFICIARIO", FontFactory.getFont(FontFactory.COURIER_BOLD, 9, BaseColor.BLACK));
			parrafocert2.setAlignment(Element.ALIGN_JUSTIFIED);
			tableCert.addCell(parrafocert2);
			Paragraph parrafocert3 = new Paragraph("MONTO", FontFactory.getFont(FontFactory.COURIER_BOLD, 9, BaseColor.BLACK));
			parrafocert3.setAlignment(Element.ALIGN_JUSTIFIED);
			tableCert.addCell(parrafocert3);
//			tableC.addCell("IMPORTE EN LETRA");
			for (Map<String, String> beneficiario: beneficiarios) {
				Paragraph parrafocert4 = new Paragraph(beneficiario.get("noCheque"), FontFactory.getFont(FontFactory.COURIER, 9, BaseColor.BLACK));
				parrafocert4.setAlignment(Element.ALIGN_JUSTIFIED);
				tableCert.addCell(parrafocert4);
				Paragraph parrafocert5 = new Paragraph(beneficiario.get("beneficiario"), FontFactory.getFont(FontFactory.COURIER, 9, BaseColor.BLACK));
				parrafocert5.setAlignment(Element.ALIGN_JUSTIFIED);
				tableCert.addCell(parrafocert5);
	
				Paragraph parrafocert6 = new Paragraph("$ "+funciones.ponerFormatoImporte(Double.parseDouble(beneficiario.get("importe"))), FontFactory.getFont(FontFactory.COURIER, 9, BaseColor.BLACK));
				parrafocert6.setAlignment(Element.ALIGN_RIGHT);
				tableCert.addCell(parrafocert6);
//				tableC.addCell(beneficiario.get(""));
			}
			tableCert.addCell(cell6);
			tableCert.getHorizontalAlignment();
			
			PdfPTable tableC = new PdfPTable(3);
			PdfPCell cell4 = new PdfPCell();
			tableC.setWidthPercentage(100);
			tableC.getDefaultCell().setBorder(Rectangle.NO_BORDER);
			tableC.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			tableC.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
			Paragraph parrafoc2 = new Paragraph("BENEFICIARIO", FontFactory.getFont(FontFactory.COURIER_BOLD, 9, BaseColor.BLACK));
			parrafoc2.setAlignment(Element.ALIGN_JUSTIFIED);
			tableC.addCell(parrafoc2);
			Paragraph parrafoc3 = new Paragraph("MONTO", FontFactory.getFont(FontFactory.COURIER_BOLD, 9, BaseColor.BLACK));
			parrafoc3.setAlignment(Element.ALIGN_JUSTIFIED);
			tableC.addCell(parrafoc3);
			Paragraph parrafoc = new Paragraph("IMPORTE EN LETRA", FontFactory.getFont(FontFactory.COURIER_BOLD, 9, BaseColor.BLACK));
			parrafoc.setAlignment(Element.ALIGN_JUSTIFIED);
			tableC.addCell(parrafoc);
//			tableC.addCell("IMPORTE EN LETRA");
			for (Map<String, String> beneficiario: beneficiarios) {
				Paragraph parrafoc5 = new Paragraph(beneficiario.get("beneficiario"), FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK));
				parrafoc5.setAlignment(Element.ALIGN_JUSTIFIED);
				tableC.addCell(parrafoc5);
	
				Paragraph parrafoc6 = new Paragraph("$ "+funciones.ponerFormatoImporte(Double.parseDouble(beneficiario.get("importe"))), FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK));
				parrafoc6.setAlignment(Element.ALIGN_RIGHT);
				tableC.addCell(parrafoc6);
				
				Paragraph parrafoc4 = new Paragraph(funciones.convertirNumeroEnLetra(Double.parseDouble(beneficiario.get("importe"))), FontFactory.getFont(FontFactory.COURIER, 7, BaseColor.BLACK));
				parrafoc4.setAlignment(Element.ALIGN_JUSTIFIED);
				tableC.addCell(parrafoc4);
//				tableC.addCell(beneficiario.get(""));
			}
			tableC.addCell(cell4);
			tableC.getHorizontalAlignment();
			
			PdfPTable tableCCert = new PdfPTable(4);
			PdfPCell cell8 = new PdfPCell();
			tableCCert.setWidthPercentage(100);
			tableCCert.getDefaultCell().setBorder(Rectangle.NO_BORDER);
			tableCCert.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			tableCCert.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
			Paragraph parrafoccert = new Paragraph("CHEQUE", FontFactory.getFont(FontFactory.COURIER_BOLD, 9, BaseColor.BLACK));
			parrafoccert.setAlignment(Element.ALIGN_JUSTIFIED);
			tableCCert.addCell(parrafoccert);
			Paragraph parrafoccert2 = new Paragraph("BENEFICIARIO", FontFactory.getFont(FontFactory.COURIER_BOLD, 9, BaseColor.BLACK));
			parrafoccert2.setAlignment(Element.ALIGN_JUSTIFIED);
			tableCCert.addCell(parrafoccert2);
			Paragraph parrafoccert3 = new Paragraph("MONTO", FontFactory.getFont(FontFactory.COURIER_BOLD, 9, BaseColor.BLACK));
			parrafoccert3.setAlignment(Element.ALIGN_JUSTIFIED);
			tableCCert.addCell(parrafoccert3);
			Paragraph parrafoccert4 = new Paragraph("IMPORTE EN LETRA", FontFactory.getFont(FontFactory.COURIER_BOLD, 9, BaseColor.BLACK));
			parrafoccert4.setAlignment(Element.ALIGN_JUSTIFIED);
			tableCCert.addCell(parrafoccert4);
//			tableC.addCell("IMPORTE EN LETRA");
			for (Map<String, String> beneficiario: beneficiarios) {
				Paragraph parrafoccert5 = new Paragraph(beneficiario.get("noCheque"), FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK));
				parrafoccert5.setAlignment(Element.ALIGN_JUSTIFIED);
				tableCCert.addCell(parrafoccert5);
				Paragraph parrafoccert6 = new Paragraph(beneficiario.get("beneficiario"), FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK));
				parrafoccert6.setAlignment(Element.ALIGN_JUSTIFIED);
				tableCCert.addCell(parrafoccert6);
	
				Paragraph parrafoccert7 = new Paragraph("$ "+funciones.ponerFormatoImporte(Double.parseDouble(beneficiario.get("importe"))), FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK));
				parrafoccert7.setAlignment(Element.ALIGN_RIGHT);
				tableCCert.addCell(parrafoccert7);
				
				Paragraph parrafoccert8 = new Paragraph(funciones.convertirNumeroEnLetra(Double.parseDouble(beneficiario.get("importe"))), FontFactory.getFont(FontFactory.COURIER, 7, BaseColor.BLACK));
				parrafoccert8.setAlignment(Element.ALIGN_RIGHT);
				tableCCert.addCell(parrafoccert8);
//				tableC.addCell(beneficiario.get(""));
			}
			tableCCert.addCell(cell8);
			tableCCert.getHorizontalAlignment();
			
			System.out.println("*****************");
			System.out.println(valor.get(0).get("tipoCar"));
			System.out.println("*****************");
			String mensaje = "";
			if (valor.get(0).get("tipoCar").equals("ACERT")||valor.get(0).get("tipoCar").equals("ACHEQ")) {
				pdf.add(parrafo);
				pdf.add(parrafo1);
				pdf.add(Chunk.NEWLINE);
				pdf.add(parrafo2);
//				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
				pdf.add(parrafo3);
				pdf.add(parrafo4);
				pdf.add(parrafo5);
				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
//				pdf.add(Chunk.NEWLINE);
				pdf.add(l1);
				pdf.add(l2);
				pdf.add(l3);
				pdf.add(l4);
				pdf.add(l5);
				pdf.add(Chunk.NEWLINE);
				//pdf.add(Chunk.NEWLINE);
				if(tipo.equals("ACERT")){
					pdf.add(tableCert);
				}else{
					pdf.add(table2);			
				}			
				if(beneficiarios.size()<20){
					int diferiencia=20-beneficiarios.size();
					for (int i = 0; i < diferiencia; i+=2) {
						pdf.add(Chunk.NEWLINE);
					}
				}
				pdf.add(Chunk.NEWLINE);
//				pdf.add(Chunk.NEWLINE);
				pdf.add(l6);
				pdf.add(l7);
				pdf.add(l8);
				pdf.add(Chunk.NEWLINE);
				//pdf.add(Chunk.NEWLINE);
				pdf.add(l9);
				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
				//pdf.add(Chunk.NEWLINE);
				pdf.add(parrafo7);
				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
				pdf.add(parrafo8);
				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
				pdf.add(parrafo9);
				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
				pdf.add(table);
				pdf.add(parrafo10);
				pdf.close();
				
				mensaje=cartasPorEntregarDao.actualizarFecha(valor.get(0), fechaImp);
			}else{
				pdf.add(parrafo);
				pdf.add(parrafo1);
				pdf.add(Chunk.NEWLINE);
				pdf.add(parrafo2);
//				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
				pdf.add(parrafo3);
				pdf.add(parrafo4);
				pdf.add(parrafo5);
				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
//				pdf.add(Chunk.NEWLINE);
				pdf.add(l1);
				pdf.add(l3);
				//pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
				if(tipo.equals("CCERT")){
					pdf.add(tableCCert);
				}else{
					pdf.add(tableC);
				}
				if(beneficiarios.size()<12){
					int diferiencia=12-beneficiarios.size();
					for (int i = 0; i < diferiencia; i+=2) {
						pdf.add(Chunk.NEWLINE);
					}
				}
				pdf.add(Chunk.NEWLINE);
				//pdf.add(Chunk.NEWLINE);
//				pdf.add(Chunk.NEWLINE);
				pdf.add(l6);
				pdf.add(l7);
				pdf.add(l8);
				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
				pdf.add(l9);
				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
				//pdf.add(Chunk.NEWLINE);
				pdf.add(parrafo7);
				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
				pdf.add(parrafo8);
				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
				pdf.add(parrafo9);
				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
				pdf.add(table);
				pdf.add(parrafo10);
				pdf.close();
				mensaje=cartasPorEntregarDao.actualizarFecha(valor.get(0), fechaImp);
			}
				
			
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasPorentregarBusinessImpl, M: generaPDF");
			nomArch="";
		}return nomArch;
	}*/
	
	public String generaPDF(List<Map<String, String>> valor, List<Map<String, String>> beneficiarios, 
			String dif,String tipo, String fechaImp) {
		String nomArch="";
		String mensaje="";
		try {
			
			Document pdf = new Document(PageSize.A4, 40, 40, 40, 40);
			pdf.addTitle("Cartas de Cheques");
			System.out.println(ConstantesSet.CARPETA_PDF);
			nomArch = ConstantesSet.CARPETA_PDF + "carta"+Utilerias.indicadorFecha()+".pdf";
			PdfWriter editor = PdfWriter.getInstance(pdf, new FileOutputStream(nomArch));
			editor.setInitialLeading(16);
			Rectangle rct = new Rectangle(36, 54, 559, 788);
			editor.setBoxSize("art", rct);
			pdf.open();
			int d=0;
			if(tipo.equals("CCERT")||tipo.equals("CCHEQ")){
				d=12;
			}else if(tipo.equals("ACERT")||tipo.equals("ACHEQ")){
				d=20;
			}
			int iteraciones=(beneficiarios.size()/d);
			iteraciones+=(beneficiarios.size()%d==0?0:1);
			for (int i = 0; i < iteraciones; i++) {
				int fin=(i+1)*d;
				fin=(fin>beneficiarios.size()?beneficiarios.size():fin);
				generaHoja(valor, beneficiarios, dif, tipo, fechaImp, i*d, fin, pdf);
				pdf.newPage();
			}
			pdf.close();
			mensaje=cartasPorEntregarDao.actualizarFecha(valor.get(0), fechaImp);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesBusinessImpl, M: generaPDF");
			nomArch="";
		}
		return nomArch;
	}
	
	public void generaHoja(List<Map<String, String>> valor, List<Map<String, String>> beneficiarios, String dif,String tipo, 
			String fechaImp, int inicio, int fin, Document pdf) {
		try {
			
			Paragraph parrafo = new Paragraph(valor.get(0).get("empresa"), FontFactory.getFont(FontFactory.COURIER_BOLD, 18, BaseColor.BLACK));
			parrafo.setAlignment(Element.ALIGN_CENTER);
			parrafo.setSpacingBefore (2);
			Paragraph parrafo1 = new Paragraph(valor.get(0).get("calle")+", Col. "+valor.get(0).get("colonia")+", C.P. "
					+valor.get(0).get("cp")+", "+valor.get(0).get("ciudad")+", "+valor.get(0).get("estado"),FontFactory.getFont(FontFactory.COURIER, 7));
			parrafo1.setAlignment(Element.ALIGN_CENTER);
			
			String vec[]=fechaImp.split("/");
			int dia=Integer.parseInt(vec[0]);
			int mes=Integer.parseInt(vec[1]);
			int anio=Integer.parseInt(vec[2]);
			Paragraph parrafo2 = new Paragraph(valor.get(0).get("lugarFecha")+", A "
			+DateFormat.getDateInstance(DateFormat.LONG).format(new GregorianCalendar(anio ,mes-1,dia).getTime())+".",
			FontFactory.getFont(FontFactory.COURIER, 10));
			parrafo2.setAlignment(Element.ALIGN_RIGHT);
			
			
			Paragraph parrafo3 = new Paragraph(valor.get(0).get("b1"), FontFactory.getFont(FontFactory.COURIER_BOLD, 10, BaseColor.BLACK));
			parrafo3.setAlignment(Element.ALIGN_LEFT);
			Paragraph parrafo4 = new Paragraph(valor.get(0).get("b2"), FontFactory.getFont(FontFactory.COURIER_BOLD, 10, BaseColor.BLACK));
			parrafo4.setAlignment(Element.ALIGN_LEFT);
			Paragraph parrafo5 = new Paragraph(valor.get(0).get("b3"), FontFactory.getFont(FontFactory.COURIER_BOLD, 10, BaseColor.BLACK));
			parrafo5.setAlignment(Element.ALIGN_LEFT);
			
			
				Chunk l1 = new Chunk("				"+valor.get(0).get("c1")+" ", FontFactory.getFont(FontFactory.COURIER,10,BaseColor.BLACK));
				Chunk l2 = new Chunk(valor.get(0).get("solicitante")+" " , FontFactory.getFont(FontFactory.COURIER_BOLD,10,BaseColor.BLACK));
				Chunk l3 = new Chunk(valor.get(0).get("c2"), FontFactory.getFont(FontFactory.COURIER,10,BaseColor.BLACK));
				Chunk l4 = new Chunk(valor.get(0).get("identificacion")+" ", FontFactory.getFont(FontFactory.COURIER,10,BaseColor.BLACK));
				Chunk l5 = new Chunk(valor.get(0).get("c3")+".", FontFactory.getFont(FontFactory.COURIER,10,BaseColor.BLACK));
			

			
			

			
			Chunk l6 = new Chunk("				"+valor.get(0).get("c4")+" "+valor.get(0).get("cuenta")+" ", FontFactory.getFont(FontFactory.COURIER,10,BaseColor.BLACK));
			Chunk l7 = new Chunk(valor.get(0).get("c5")+" " , FontFactory.getFont(FontFactory.COURIER,10,BaseColor.BLACK));
			Chunk l8 = new Chunk(valor.get(0).get("empresa")+".", FontFactory.getFont(FontFactory.COURIER_BOLD,10,BaseColor.BLACK));
			Chunk l9 = new Chunk("				"+valor.get(0).get("c6")+".", FontFactory.getFont(FontFactory.COURIER,10,BaseColor.BLACK));


			
			Paragraph parrafo7 = new Paragraph("Solicitante", FontFactory.getFont(FontFactory.COURIER_BOLD, 10, BaseColor.BLACK));
			parrafo7.setAlignment(Element.ALIGN_CENTER);
			
			Paragraph parrafo8 = new Paragraph(valor.get(0).get("solicitante"), FontFactory.getFont(FontFactory.COURIER_BOLD, 10, BaseColor.BLACK));
			parrafo8.setAlignment(Element.ALIGN_CENTER);
			
			Paragraph parrafo9 = new Paragraph("A T E N T A M E N T E", FontFactory.getFont(FontFactory.COURIER_BOLD, 10, BaseColor.BLACK));
			parrafo9.setAlignment(Element.ALIGN_CENTER);
			
			Paragraph parrafo10 = new Paragraph("Folio: "+valor.get(0).get("emision"), FontFactory.getFont(FontFactory.COURIER, 7, BaseColor.BLACK));
			parrafo10.setAlignment(Element.ALIGN_RIGHT);
				
			
			//*************************************Tablas*****************************************//
			
			PdfPTable table = new PdfPTable(2);
			PdfPCell cell = new PdfPCell();
			table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
		    table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
			Paragraph parrafot = new Paragraph(valor.get(0).get("autorizacion1"), FontFactory.getFont(FontFactory.COURIER_BOLD, 10, BaseColor.BLACK));
			parrafot.setAlignment(Element.ALIGN_CENTER);
			table.addCell(parrafot);
			Paragraph parrafot2 = new Paragraph(valor.get(0).get("autorizacion2"), FontFactory.getFont(FontFactory.COURIER_BOLD, 10, BaseColor.BLACK));
			parrafot2.setAlignment(Element.ALIGN_CENTER);
			table.addCell(parrafot2);
			Paragraph parrafot3 = new Paragraph("AUTORIZO", FontFactory.getFont(FontFactory.COURIER_BOLD, 10, BaseColor.BLACK));
			parrafot3.setAlignment(Element.ALIGN_CENTER);
			table.addCell(parrafot3);
			Paragraph parrafot4 = new Paragraph("AUTORIZO", FontFactory.getFont(FontFactory.COURIER_BOLD, 10, BaseColor.BLACK));
			parrafot4.setAlignment(Element.ALIGN_CENTER);
			table.addCell(parrafot4);
			table.addCell(cell);
			table.getHorizontalAlignment();
			
			
			PdfPTable table2 = new PdfPTable(2);
			PdfPCell cell2 = new PdfPCell();
			table2.setWidthPercentage(100);
			table2.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		    table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
		    table2.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
			Paragraph parrafoa2 = new Paragraph("BENEFICIARIO", FontFactory.getFont(FontFactory.COURIER_BOLD, 9, BaseColor.BLACK));
			parrafoa2.setAlignment(Element.ALIGN_JUSTIFIED);
			table2.addCell(parrafoa2);
			Paragraph parrafoa3 = new Paragraph("MONTO", FontFactory.getFont(FontFactory.COURIER_BOLD, 9, BaseColor.BLACK));
			parrafoa3.setAlignment(Element.ALIGN_JUSTIFIED);
			table2.addCell(parrafoa3);
			for (int i = inicio; i < fin; i++) {
				Map<String, String> beneficiario = beneficiarios.get(i);
				Paragraph parrafoa4 = new Paragraph(beneficiario.get("beneficiario"), FontFactory.getFont(FontFactory.COURIER, 9, BaseColor.BLACK));
				parrafoa4.setAlignment(Element.ALIGN_JUSTIFIED);
				table2.addCell(parrafoa4);
				Paragraph parrafoa5 = new Paragraph("$ "+funciones.ponerFormatoImporte(Double.parseDouble(beneficiario.get("importe"))), FontFactory.getFont(FontFactory.COURIER, 9, BaseColor.BLACK));
				parrafoa5.setAlignment(Element.ALIGN_RIGHT);
				table2.addCell(parrafoa5);
			}
			table2.addCell(cell2);
			table2.getHorizontalAlignment();
			
			PdfPTable tableCert = new PdfPTable(3);
			PdfPCell cell6 = new PdfPCell();
			tableCert.setWidthPercentage(100);
			tableCert.getDefaultCell().setBorder(Rectangle.NO_BORDER);
			tableCert.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			tableCert.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
			Paragraph parrafocert = new Paragraph("CHEQUE", FontFactory.getFont(FontFactory.COURIER_BOLD, 9, BaseColor.BLACK));
			parrafocert.setAlignment(Element.ALIGN_JUSTIFIED);
			tableCert.addCell(parrafocert);
			Paragraph parrafocert2 = new Paragraph("BENEFICIARIO", FontFactory.getFont(FontFactory.COURIER_BOLD, 9, BaseColor.BLACK));
			parrafocert2.setAlignment(Element.ALIGN_JUSTIFIED);
			tableCert.addCell(parrafocert2);
			Paragraph parrafocert3 = new Paragraph("MONTO", FontFactory.getFont(FontFactory.COURIER_BOLD, 9, BaseColor.BLACK));
			parrafocert3.setAlignment(Element.ALIGN_JUSTIFIED);
			tableCert.addCell(parrafocert3);
//			tableC.addCell("IMPORTE EN LETRA");
			for (int i = inicio; i < fin; i++) {
				Map<String, String> beneficiario = beneficiarios.get(i);
				Paragraph parrafocert4 = new Paragraph(beneficiario.get("noCheque"), FontFactory.getFont(FontFactory.COURIER, 9, BaseColor.BLACK));
				parrafocert4.setAlignment(Element.ALIGN_JUSTIFIED);
				tableCert.addCell(parrafocert4);
				Paragraph parrafocert5 = new Paragraph(beneficiario.get("beneficiario"), FontFactory.getFont(FontFactory.COURIER, 9, BaseColor.BLACK));
				parrafocert5.setAlignment(Element.ALIGN_JUSTIFIED);
				tableCert.addCell(parrafocert5);
	
				Paragraph parrafocert6 = new Paragraph("$ "+funciones.ponerFormatoImporte(Double.parseDouble(beneficiario.get("importe"))), FontFactory.getFont(FontFactory.COURIER, 9, BaseColor.BLACK));
				parrafocert6.setAlignment(Element.ALIGN_RIGHT);
				tableCert.addCell(parrafocert6);
//				tableC.addCell(beneficiario.get(""));
			}
			tableCert.addCell(cell6);
			tableCert.getHorizontalAlignment();
			
			PdfPTable tableC = new PdfPTable(3);
			PdfPCell cell4 = new PdfPCell();
			tableC.setWidthPercentage(100);
			tableC.getDefaultCell().setBorder(Rectangle.NO_BORDER);
			tableC.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			tableC.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
			Paragraph parrafoc2 = new Paragraph("BENEFICIARIO", FontFactory.getFont(FontFactory.COURIER_BOLD, 9, BaseColor.BLACK));
			parrafoc2.setAlignment(Element.ALIGN_JUSTIFIED);
			tableC.addCell(parrafoc2);
			Paragraph parrafoc3 = new Paragraph("MONTO", FontFactory.getFont(FontFactory.COURIER_BOLD, 9, BaseColor.BLACK));
			parrafoc3.setAlignment(Element.ALIGN_JUSTIFIED);
			tableC.addCell(parrafoc3);
			Paragraph parrafoc = new Paragraph("IMPORTE EN LETRA", FontFactory.getFont(FontFactory.COURIER_BOLD, 9, BaseColor.BLACK));
			parrafoc.setAlignment(Element.ALIGN_JUSTIFIED);
			tableC.addCell(parrafoc);
//			tableC.addCell("IMPORTE EN LETRA");
			for (int i = inicio; i < fin; i++) {
				Map<String, String> beneficiario = beneficiarios.get(i);
				Paragraph parrafoc5 = new Paragraph(beneficiario.get("beneficiario"), FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK));
				parrafoc5.setAlignment(Element.ALIGN_JUSTIFIED);
				tableC.addCell(parrafoc5);
	
				Paragraph parrafoc6 = new Paragraph("$ "+funciones.ponerFormatoImporte(Double.parseDouble(beneficiario.get("importe"))), FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK));
				parrafoc6.setAlignment(Element.ALIGN_RIGHT);
				tableC.addCell(parrafoc6);
				
				Paragraph parrafoc4 = new Paragraph(funciones.convertirNumeroEnLetra(Double.parseDouble(beneficiario.get("importe"))), FontFactory.getFont(FontFactory.COURIER, 7, BaseColor.BLACK));
				parrafoc4.setAlignment(Element.ALIGN_JUSTIFIED);
				tableC.addCell(parrafoc4);
//				tableC.addCell(beneficiario.get(""));
			}
			tableC.addCell(cell4);
			tableC.getHorizontalAlignment();
			
			PdfPTable tableCCert = new PdfPTable(4);
			PdfPCell cell8 = new PdfPCell();
			tableCCert.setWidthPercentage(100);
			tableCCert.getDefaultCell().setBorder(Rectangle.NO_BORDER);
			tableCCert.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			tableCCert.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
			Paragraph parrafoccert = new Paragraph("CHEQUE", FontFactory.getFont(FontFactory.COURIER_BOLD, 9, BaseColor.BLACK));
			parrafoccert.setAlignment(Element.ALIGN_JUSTIFIED);
			tableCCert.addCell(parrafoccert);
			Paragraph parrafoccert2 = new Paragraph("BENEFICIARIO", FontFactory.getFont(FontFactory.COURIER_BOLD, 9, BaseColor.BLACK));
			parrafoccert2.setAlignment(Element.ALIGN_JUSTIFIED);
			tableCCert.addCell(parrafoccert2);
			Paragraph parrafoccert3 = new Paragraph("MONTO", FontFactory.getFont(FontFactory.COURIER_BOLD, 9, BaseColor.BLACK));
			parrafoccert3.setAlignment(Element.ALIGN_JUSTIFIED);
			tableCCert.addCell(parrafoccert3);
			Paragraph parrafoccert4 = new Paragraph("IMPORTE EN LETRA", FontFactory.getFont(FontFactory.COURIER_BOLD, 9, BaseColor.BLACK));
			parrafoccert4.setAlignment(Element.ALIGN_JUSTIFIED);
			tableCCert.addCell(parrafoccert4);
//			tableC.addCell("IMPORTE EN LETRA");
			for (int i = inicio; i < fin; i++) {
				Map<String, String> beneficiario = beneficiarios.get(i);
				Paragraph parrafoccert5 = new Paragraph(beneficiario.get("noCheque"), FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK));
				parrafoccert5.setAlignment(Element.ALIGN_JUSTIFIED);
				tableCCert.addCell(parrafoccert5);
				Paragraph parrafoccert6 = new Paragraph(beneficiario.get("beneficiario"), FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK));
				parrafoccert6.setAlignment(Element.ALIGN_JUSTIFIED);
				tableCCert.addCell(parrafoccert6);
	
				Paragraph parrafoccert7 = new Paragraph("$ "+funciones.ponerFormatoImporte(Double.parseDouble(beneficiario.get("importe"))), FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK));
				parrafoccert7.setAlignment(Element.ALIGN_RIGHT);
				tableCCert.addCell(parrafoccert7);
				
				Paragraph parrafoccert8 = new Paragraph(funciones.convertirNumeroEnLetra(Double.parseDouble(beneficiario.get("importe"))), FontFactory.getFont(FontFactory.COURIER, 7, BaseColor.BLACK));
				parrafoccert8.setAlignment(Element.ALIGN_RIGHT);
				tableCCert.addCell(parrafoccert8);
//				tableC.addCell(beneficiario.get(""));
			}
			tableCCert.addCell(cell8);
			tableCCert.getHorizontalAlignment();			
			if (valor.get(0).get("tipoCar").equals("ACERT")||valor.get(0).get("tipoCar").equals("ACHEQ")) {
				pdf.add(parrafo);
				pdf.add(parrafo1);
				pdf.add(Chunk.NEWLINE);
				pdf.add(parrafo2);
//				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
				pdf.add(parrafo3);
				pdf.add(parrafo4);
				pdf.add(parrafo5);
				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
//				pdf.add(Chunk.NEWLINE);
				pdf.add(l1);
				pdf.add(l2);
				pdf.add(l3);
				pdf.add(l4);
				pdf.add(l5);
				pdf.add(Chunk.NEWLINE);
				//pdf.add(Chunk.NEWLINE);
				if(tipo.equals("ACERT")){
					pdf.add(tableCert);
				}else{
					pdf.add(table2);			
				}			
				int tamanio=fin%20;
				if(tamanio>0){
					int diferiencia=20-tamanio;
					for (int i = 0; i < diferiencia; i+=2) {
						pdf.add(Chunk.NEWLINE);
					}
				}
				pdf.add(Chunk.NEWLINE);
//				pdf.add(Chunk.NEWLINE);
				pdf.add(l6);
				pdf.add(l7);
				pdf.add(l8);
				pdf.add(Chunk.NEWLINE);
				//pdf.add(Chunk.NEWLINE);
				pdf.add(l9);
				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
				//pdf.add(Chunk.NEWLINE);
				pdf.add(parrafo7);
				pdf.add(Chunk.NEWLINE);
				pdf.add(parrafo8);
				pdf.add(Chunk.NEWLINE);
				pdf.add(parrafo9);
				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
				pdf.add(table);
				pdf.add(parrafo10);
				
			}else{
				pdf.add(parrafo);
				pdf.add(parrafo1);
				pdf.add(Chunk.NEWLINE);
				pdf.add(parrafo2);
//				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
				pdf.add(parrafo3);
				pdf.add(parrafo4);
				pdf.add(parrafo5);
				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
//				pdf.add(Chunk.NEWLINE);
				pdf.add(l1);
				pdf.add(l3);
				//pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
				if(tipo.equals("CCERT")){
					pdf.add(tableCCert);
				}else{
					pdf.add(tableC);
				}
				int tamanio=fin%12;
				if(tamanio>0){
					int diferiencia=12-tamanio;
					for (int i = 0; i < diferiencia; i++) {
						pdf.add(Chunk.NEWLINE);
					}
				}
				pdf.add(Chunk.NEWLINE);
				//pdf.add(Chunk.NEWLINE);
//				pdf.add(Chunk.NEWLINE);
				pdf.add(l6);
				pdf.add(l7);
				pdf.add(l8);
				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
				pdf.add(l9);
				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
				pdf.add(parrafo7);
				pdf.add(Chunk.NEWLINE);
				pdf.add(parrafo8);
				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
				pdf.add(parrafo9);
				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
				pdf.add(table);
				pdf.add(parrafo10);
				//mensaje=cartasPorEntregarDao.actualizarFecha(valor.get(0), fechaImp);
				
				
			}
			
				
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesBusinessImpl, M: generaPDF");
			
		}
	}

	@Override
	public List<CartasPorEntregarDto> llenaFolio(String fechaIni, String fechaFin) {
		List<CartasPorEntregarDto> resultado = new ArrayList<CartasPorEntregarDto>();
		try {
			resultado = cartasPorEntregarDao.llenaFolio(fechaIni,fechaFin);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasPorEntregarBusinessImpl, M: llenaFolio");
		}return resultado;
	}



	

	
}
