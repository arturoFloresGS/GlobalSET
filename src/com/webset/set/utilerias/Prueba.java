package com.webset.set.utilerias;

public class Prueba {
	public static void main(String[] args){
//		Funciones dao = new Funciones();
//		String aux="Parabola";
//		System.out.println(dao.cadenaRight(aux, 3));
//		try{
//			Document pdf = new Document();
//			FileOutputStream ficheroPdf = new FileOutputStream("C:\\WEBSET\\miPrimerPdf.pdf");
//			PdfWriter.getInstance(pdf, ficheroPdf).setInitialLeading(20);
//			pdf.open();
//			
//			StringBuffer primerLinea = new StringBuffer();
//			primerLinea.append("Encabezado Uno");
//			primerLinea.append("                                                                        ");
//			primerLinea.append("Encabezado Dos");
//			pdf.add(new Paragraph(pri0merLinea.toString()));
//			pdf.add(new Paragraph("Este es el segundo y tiene una fuente rara \n \n",
//	                FontFactory.getFont("arial",
//	                        22,                            
//	                        Font.ITALIC,                   
//	                        BaseColor.MAGENTA)));
//			
//			PdfPTable tabla = new PdfPTable(3);
//			for (int i = 0; i < 9; i++)
//			{
//			    tabla.addCell("celda " + i);
//			}
//
//			pdf.add(tabla);
//			
//			pdf.close();
//			
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//
//		try {
//			
//			Document pdf = new Document(PageSize.A4, 50, 50, 50, 50);
//			pdf.addAuthor("Luis Alfredo Serrato");
//			pdf.addTitle("MiPrimerPdf");
//			PdfWriter editor = PdfWriter.getInstance(pdf, new FileOutputStream("C:\\WEBSET\\miPrimerPdf.pdf"));
//			editor.setInitialLeading(16);
//			Rectangle rct = new Rectangle(36, 54, 559, 788);
//			editor.setBoxSize("art", rct);
//			//HeaderFooter event = new HeaderFooter();
//			//editor.setPageEvent(event);
//			pdf.open();
//			
//			Chunk titulo = new Chunk("CHUNK", FontFactory.getFont(FontFactory.COURIER, 20, Font.UNDERLINE, BaseColor.BLACK));
//			Paragraph parrafo = new Paragraph(titulo);
//			parrafo.setAlignment(Element.ALIGN_CENTER);
//			pdf.add(titulo);
//
//			
//			Chunk chunkSeparador =  new Chunk("———————————————————————————————————————-");
//			Chunk chunkNormal = new Chunk("Este es una parte del texto que puede ir en este pdf");
//			pdf.add(chunkNormal);
//			pdf.add(Chunk.NEWLINE);
//			pdf.add(Chunk.NEWLINE);
//			Chunk chunkTunning = new Chunk("Este es otro ejemplo de chunk o linea",FontFactory.getFont(FontFactory.COURIER, 20, Font.ITALIC, BaseColor.GREEN));
//			pdf.add(chunkTunning);
//			
//			
//			pdf.close();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
//		String cadena = "0127017201000002522016-01-072016-05-100254000463710+00000000979670.08549-00000000000250.003227247                            +00000000979420.08000247690000001";
//		
//		System.out.println(cadena.substring(36,38) +"/"+ cadena.substring(33,35) + "/" + cadena.substring(28,32));
//		System.out.println(cadena.substring(26,28) + "/" + cadena.substring(23, 25) + "/" + cadena.substring(18, 22));
//		System.out.println(cadena.substring(4, 8));
//		System.out.println(cadena.substring(8, 18));
//		System.out.println(cadena.substring(18, 22));
//		System.out.println(cadena.substring(22, 31).trim());
//		System.out.println(cadena.substring(31, 46));
//		System.out.println(cadena.substring(46, 47));
//		System.out.println(cadena.substring(47, 65).trim());
//		System.out.println(cadena.substring(65, 66).trim());
//		System.out.println(cadena.substring(66, 101).trim());
//		System.out.println(cadena.substring(109,111) + "/" + cadena.substring(106, 108) + "/" + cadena.substring(101, 105));
//		System.out.println(cadena.substring(119, 121) + "/" + cadena.substring(116, 118) + "/" + cadena.substring(111, 115));
//		System.out.println(cadena.substring(121, 124));
//		System.out.println(cadena.substring(124, cadena.length()));
		
		System.out.println("uno"+"\u0020\u0020\u0020\u0020\u0020\u0020"+"dos");
		
	}
}
