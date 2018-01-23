package com.webset.set.impresion.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

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
import com.webset.set.impresion.dao.CartasChequesDao;
import com.webset.set.impresion.dto.CartasChequesDto;
import com.webset.set.impresion.service.CartasChequesService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;

public class CartasChequesBusinessImpl <objCartasChequesDao> implements CartasChequesService {
	
	private CartasChequesDao objCartasChequesDao;
	
	public CartasChequesDao getObjCartasChequesDao() {
		return objCartasChequesDao;
	}


	public void setObjCartasChequesDao(CartasChequesDao objCartasChequesDao) {
		this.objCartasChequesDao = objCartasChequesDao;
	}
	
	Bitacora bitacora;
	Funciones funciones = new Funciones();
	CartasChequesDto objCartasChequesDto = new CartasChequesDto();
	List<CartasChequesDto> recibeDatos = new ArrayList<CartasChequesDto>();
	int recibeResultadoEntero = 0;
	
	@Override
	public List<CartasChequesDto> llenaEmpresa(String idClave){
		List<CartasChequesDto> resultado = new ArrayList<CartasChequesDto>();
		try {
			resultado = objCartasChequesDao.llenaEmpresa(idClave);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesBusinessImpl, M: llenaEmpresa");
		}return resultado;
	}


	@Override
	public List<CartasChequesDto> llenaProveedor() {
		List<CartasChequesDto> resultado = new ArrayList<CartasChequesDto>();
		try {
			resultado = objCartasChequesDao.llenaProveedor();
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesBusinessImpl, M: llenaProveedor");
		}return resultado;
	}


	@Override
	public List<CartasChequesDto> llenaSolicitante() {
		List<CartasChequesDto> resultado = new ArrayList<CartasChequesDto>();
		try {
			resultado = objCartasChequesDao.llenaSolicitante();
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesBusinessImpl, M: llenaSolicitante");
		}return resultado;
		
	}


	@Override
	public List<CartasChequesDto> llenaAutorizaciones() {
		List<CartasChequesDto> resultado = new ArrayList<CartasChequesDto>();
		try {
			resultado = objCartasChequesDao.llenaAutorizaciones();
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesBusinessImpl, M: llenaAutorizaciones");
		}return resultado;
		
	}


	@Override
	public List<CartasChequesDto> llenaAutorizaciones2() {
		List<CartasChequesDto> resultado = new ArrayList<CartasChequesDto>();
		try {
			resultado = objCartasChequesDao.llenaAutorizaciones2();
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesBusinessImpl, M: llenaAutorizaciones2");
		}return resultado;
	}


	@Override
	public List<CartasChequesDto> llenaClave(String fechaIni,String fechaFin) {
		List<CartasChequesDto> resultado = new ArrayList<CartasChequesDto>();
		try {
			resultado = objCartasChequesDao.llenaClave(fechaIni,fechaFin);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesBusinessImpl, M: llenaClave");
		}return resultado;
		
	}


	@Override
	public List<CartasChequesDto> llenaGrid(String idEmpresa, String idClave, 
			 String tipo, String idBanco, String tipoC, String op, String idChequera) {
		
		List<CartasChequesDto> resultado = new ArrayList<CartasChequesDto>();
		try {
			resultado = objCartasChequesDao.llenaGrid( idEmpresa, idClave, tipo, idBanco, tipoC, op, idChequera);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesBusinessImpl, M: llenaGrid");
		}return resultado;
		
	}

	@Override
	public List<LlenaComboGralDto> llenarComboBeneficiario(LlenaComboGralDto dto) {


		List<LlenaComboGralDto> resultado = new ArrayList<LlenaComboGralDto>();
		try {
			resultado = objCartasChequesDao.llenarComboBeneficiario(dto);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesBusinessImpl, M: llenarComboBeneficiario");
		}return resultado;
	}


	@Override
	public List<CartasChequesDto> obtieneDatos(String idEmpresa, String banco, String tipo){
			
			List<CartasChequesDto> resultado = new ArrayList<CartasChequesDto>();
			try {
				resultado = objCartasChequesDao.obtieneDatos(idEmpresa, banco, tipo);
			} catch (Exception e) {
				e.printStackTrace();
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesBusinessImpl, M: obtieneDatos");
			}return resultado;
	}


	/*@Override
	public String generaPDF(List<Map<String, String>> valor, List<Map<String, String>> beneficiarios, String dif,String tipo, String fechaImp) {
		System.out.println("========");
		System.out.println("entra businnes");
		System.out.println("========");
		String nomArch="";
		try {
			if (dif.equals("can")|| dif.equals("ace")) {
				String idEmision = objCartasChequesDao.obtenerIdEmision();
				valor.get(0).put("idEmision", idEmision);
			}
			
			
			System.out.println("entra TRY");
			
			System.out.println(valor.get(0).get("proveedor"));
			
			Document pdf = new Document(PageSize.A4, 30, 30, 30, 30);
//			pdf.addAuthor("");
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
			
			//new GregorianCalendar(year, month, dayOfMonth)
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
			
			Paragraph parrafo10 = new Paragraph("Folio: "+valor.get(0).get("idEmision"), FontFactory.getFont(FontFactory.COURIER, 7, BaseColor.BLACK));
			parrafo10.setAlignment(Element.ALIGN_RIGHT);
			
			Paragraph parrafo11 = new Paragraph("Folio: "+valor.get(0).get("emision"), FontFactory.getFont(FontFactory.COURIER,7, BaseColor.BLACK));
			parrafo11.setAlignment(Element.ALIGN_RIGHT);
				
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
			
			if (dif.equals("can")) {
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
				
				
				int r = objCartasChequesDao.guardarCartaEmitida(valor.get(0), dif, fechaImp);
				if (r>0) {
					boolean b=true;
					for (Map<String, String> beneficiario: beneficiarios) {
						int resultado = objCartasChequesDao.eliminaEmision(beneficiario.get("folio"));
						
						if (resultado<=0) {
							b=false;
						}
					}
					
					if (!b) {
						nomArch="";
					}else{
						 b=true;
						for (Map<String, String> beneficiario: beneficiarios) {
							int resultado = objCartasChequesDao.guardarDetalle(beneficiario,valor);
							
							if (resultado<=0) {
								b=false;
							}
						}
						if (!b) {
							nomArch="";
						}
					}
				} else {
					nomArch="";
				}

				
			} else {
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
				if (dif.equals("ace")) {
					pdf.add(parrafo10);
				} else {
					pdf.add(parrafo11);
				}
				pdf.close();
				
				if (dif.equals("ace")) {
					int r = objCartasChequesDao.guardarCartaEmitida(valor.get(0),dif, fechaImp);
					System.out.println("********************");
					System.out.println(r);
					System.out.println("********************");
					if (r>0) {
						boolean b=true;
						for (Map<String, String> beneficiario: beneficiarios) {
							int resultado = objCartasChequesDao.guardarDetalle(beneficiario,valor);
							if (resultado<=0) {
								b=false;
							}
						}
						if (!b) {
							nomArch="";
						} 
					} else {
						nomArch="";
					}
				}
				}	
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesBusinessImpl, M: generaPDF");
			nomArch="";
		}return nomArch;
	}*/
	public String generaPDF(List<Map<String, String>> valor, List<Map<String, String>> beneficiarios, 
			String dif,String tipo, String fechaImp) {
		String nomArch="";
		try {
			if (dif.equals("can")|| dif.equals("ace")) {
				String idEmision = objCartasChequesDao.obtenerIdEmision();
				valor.get(0).put("idEmision", idEmision);
			}
			
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
			if(dif.equals("can")){
				d=12;
			}else if(dif.equals("ace")){
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
			if (dif.equals("can")) {
				int r = objCartasChequesDao.guardarCartaEmitida(valor.get(0), dif, fechaImp);
				if (r>0) {
					boolean b=true;
					for (Map<String, String> beneficiario: beneficiarios) {
						int resultado = objCartasChequesDao.eliminaEmision(beneficiario.get("folio"));
						
						if (resultado<=0) {
							b=false;
						}
					}
					
					if (!b) {
						nomArch="";
					}else{
						 b=true;
						for (Map<String, String> beneficiario: beneficiarios) {
							int resultado = objCartasChequesDao.guardarDetalle(beneficiario,valor);
							
							if (resultado<=0) {
								b=false;
							}
						}
						if (!b) {
							nomArch="";
						}
					}
				} else {
					nomArch="";
				}

				
			} else {
				if (dif.equals("ace")) {
					int r = objCartasChequesDao.guardarCartaEmitida(valor.get(0),dif, fechaImp);
					if (r>0) {
						boolean b=true;
						for (Map<String, String> beneficiario: beneficiarios) {
							int resultado = objCartasChequesDao.guardarDetalle(beneficiario,valor);
							if (resultado<=0) {
								b=false;
							}
						}
						if (!b) {
							nomArch="";
						} 
					} else {
						nomArch="";
					}
				}
				}	
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
			
			Paragraph parrafo10 = new Paragraph("Folio: "+valor.get(0).get("idEmision"), FontFactory.getFont(FontFactory.COURIER, 7, BaseColor.BLACK));
			parrafo10.setAlignment(Element.ALIGN_RIGHT);
			
			Paragraph parrafo11 = new Paragraph("Folio: "+valor.get(0).get("emision"), FontFactory.getFont(FontFactory.COURIER,7, BaseColor.BLACK));
			parrafo11.setAlignment(Element.ALIGN_RIGHT);
				
			
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
			
			//******************************************************************************//
			if (dif.equals("can")) {
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
				pdf.add(l6);
				pdf.add(l7);
				pdf.add(l8);
				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
				pdf.add(l9);
				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
				pdf.add(parrafo7);
				//pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
				pdf.add(parrafo8);
				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
				pdf.add(parrafo9);
				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
				pdf.add(table);
				pdf.add(parrafo10);
							
			} else {
				pdf.add(parrafo);
				pdf.add(parrafo1);
				pdf.add(Chunk.NEWLINE);
				pdf.add(parrafo2);
				pdf.add(Chunk.NEWLINE);
				pdf.add(parrafo3);
				pdf.add(parrafo4);
				pdf.add(parrafo5);
				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
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
				pdf.add(l6);
				pdf.add(l7);
				pdf.add(l8);
				pdf.add(Chunk.NEWLINE);
				//pdf.add(Chunk.NEWLINE);
				pdf.add(l9);
				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
				pdf.add(parrafo7);
				pdf.add(Chunk.NEWLINE);
				pdf.add(parrafo8);
				pdf.add(Chunk.NEWLINE);
				pdf.add(parrafo9);
				pdf.add(Chunk.NEWLINE);
				pdf.add(Chunk.NEWLINE);
				pdf.add(table);
				if (dif.equals("ace")) {
					pdf.add(parrafo10);
				} else {
					pdf.add(parrafo11);
				}
				}
				
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesBusinessImpl, M: generaPDF");
			
		}
	}


	@Override
	public List<CartasChequesDto> llenaBanco(String idEmpresa) {
		List<CartasChequesDto> resultado = new ArrayList<CartasChequesDto>();
		try {
			resultado = objCartasChequesDao.llenaBanco(idEmpresa);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesBusinessImpl, M: llenaBanco");
		}return resultado;
	}


	@Override
	public List<CartasChequesDto> llenaChequera(String idEmpresa, String idBanco) {
		List<CartasChequesDto> resultado = new ArrayList<CartasChequesDto>();
		try {
			resultado = objCartasChequesDao.llenaChequera(idEmpresa, idBanco);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesBusinessImpl, M: llenaChequera");
		}return resultado;
	}


	@Override
	public String certificarCheque(List<String> listFolios) {
		StringBuffer mensaje = new StringBuffer();
		try {

			StringBuffer cadenaFolios = new StringBuffer();
			for (int i = 0; i < listFolios.size(); i++) {
				if(!listFolios.get(i).equals("")){
					cadenaFolios.append("'"+Utilerias.validarCadenaSQL(listFolios.get(i))+"',");
				}
			}
			if(cadenaFolios.length()>0){
				cadenaFolios.delete(cadenaFolios.length()-1, cadenaFolios.length());
			}
			System.out.println("Llegaron los Folios bussiness despues del for"+cadenaFolios.toString());
			int resultado = objCartasChequesDao.certificarCheque(cadenaFolios.toString());
			if(resultado>0){
				mensaje.append("Se certifico con exito");
			}else{
				mensaje.append("No se pudo certificar");
			}
					

		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P: Impresion, C: CartasChequesBusinessImpl, M:certificarCheque");
		}
		
		return mensaje.toString();
	}



	@Override
	public String exportaExcel(String datos) {
		String respuesta = "";
	    Gson gson = new Gson();
		List<Map<String, String>> parameters = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
	    try {	    	
			//Se crea el libro Excel
			HSSFWorkbook wb = Utilerias.generarExcel(new String[]{
														"Id. Empresa",
														"Empresa",
														"No. Proveedor",
														"Proveedor",
														"Fecha de Pago",
														"Documento",
														"Divisa",
														"Concepto",
														"Clave de Propuesta",
														"Importe",
														"No. Cheque",
														"Id. Emisi�n"	
												}, 
												parameters, 
												new String[]{
														"idEmpresa",
														"descEmpresa",
														"idProveedor",
														"descProveedor",
														"fecha",
														"documento",
														"divisa",
														"concepto",
														"claveP",
														"importe",
														"noCheque",
														"emision"
												});			
            
            respuesta = ConstantesSet.RUTA_EXCEL + "propuestas " + Utilerias.indicadorFecha() +".xls";
            File outputFile = new File(respuesta);
          
			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
        } catch (IOException e) {
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P: Impresion, C: CartasChequesBusinessImpl, M:exportaExcel");
        	respuesta = "";
        } catch(Exception e){
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P: Impresion, C: CartasChequesBusinessImpl, M:exportaExcel");
            respuesta = "";
        }
        return respuesta;
	}


	@Override
	public List<CartasChequesDto> llenaBancoSP() {
		List<CartasChequesDto> resultado = new ArrayList<CartasChequesDto>();
		try {
			resultado = objCartasChequesDao.llenaBancoSP();
		} catch (Exception e) {
			e.printStackTrace();
		}return resultado;
	}


}
