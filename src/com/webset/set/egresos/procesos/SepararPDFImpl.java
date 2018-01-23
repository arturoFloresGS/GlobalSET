package com.webset.set.egresos.procesos;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.webset.set.egresos.dao.SepararPDFDao;
import com.webset.set.egresos.service.SepararPDF;

import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.PDFTextStripperByArea;

/**
 * Proceso:
 *  a) Crea un PDF en la ruta temp, por cada pagina de los PDF existentes en la ruta origen
 *  b) Lee cada PDF creado para obtener: NumFolio_Beneficiario_FechaValor_Importe_Chequera
 *     b.1) Renombra cada pagina PDF con los datos obtenidos y los deja en ruta Destino
 *  c) Mueve los PDF procesados de la ruta Origen a la ruta Proc
 * @author javierriveracasanova
 */
public class SepararPDFImpl implements SepararPDF{
	//Atributos de la clase
	private String rutaRaiz;
	private String rutaPdfOrigen;
	private String rutaPdfDestino;
	private String rutaPdfTemp;
	private String rutaPdfProc;
	private String rutaPdfNoIden;
	private Logger logger = Logger.getLogger(SepararPDFImpl.class);
	SepararPDFDao separarPDFDao;
	
	public void procesoSepararPdf(){
		SepararPDFImpl separarPdf = new SepararPDFImpl();
		
		//Se inicializan las rutas
		//separarPdf.setRutaRaiz("/Users/javierriveracasanova/Documents/proyectos/formula/lecturaPDF/");
		separarPdf.setRutaPdfOrigen(separarPDFDao.consultaConfiguraSet(3035).getValor());
		separarPdf.setRutaPdfDestino(separarPDFDao.consultaConfiguraSet(3036).getValor());
		separarPdf.setRutaPdfTemp(separarPDFDao.consultaConfiguraSet(3037).getValor());
		separarPdf.setRutaPdfProc(separarPDFDao.consultaConfiguraSet(3038).getValor());
		separarPdf.setRutaPdfNoIden(separarPDFDao.consultaConfiguraSet(3039).getValor());
		/*separarPdf.setRutaPdfOrigen("/Users/javierriveracasanova/Documents/proyectos/formula/lecturaPDF/pdf/");
		separarPdf.setRutaPdfDestino("/Users/javierriveracasanova/Documents/proyectos/formula/lecturaPDF/pdf_dest/");
		separarPdf.setRutaPdfTemp("/Users/javierriveracasanova/Documents/proyectos/formula/lecturaPDF/pdf_temp/");
		separarPdf.setRutaPdfProc("/Users/javierriveracasanova/Documents/proyectos/formula/lecturaPDF/pdf_proc/");
		separarPdf.setRutaPdfNoIden("/Users/javierriveracasanova/Documents/proyectos/formula/lecturaPDF/no_iden/");*/
		
		try{
			File dOrigen = new File(separarPdf.getRutaPdfOrigen());
			File[] archivos = dOrigen.listFiles();
			
			if(archivos==null || archivos.length==0){
				System.err.println("No hay archivos a procesar en la ruta: ["+separarPdf.getRutaPdfOrigen()+"]");
				return;
			}
			else{
				for(int i=0; i<archivos.length; i++){
					if(archivos[i].isFile()){
						//Solo se procesan los archivos PDF
						//if(archivos[i].getName().indexOf(".")>0 &&
						//   archivos[i].getName().substring(archivos[i].getName().lastIndexOf(".")+1, archivos[i].getName().length()).equals("pdf"))
						//{
						// EN PRODUCCION LOS ARCHIVOS NO TIENEN EXTENSION, SE PROCESAN TODOS LOS ARCHIVOS
						if(archivos[i].getName().indexOf(".")!=0)
						{
							String ruta = new String();
							ruta = separarPdf.getRutaPdfOrigen() + archivos[i].getName();
							
							PdfReader reader = new PdfReader(ruta);
							int n = reader.getNumberOfPages();
							reader.close();
							
							String path;
							PdfStamper stamper;
							
							for (int j=1; j<n; j++){
								reader = new PdfReader(ruta);
								reader.selectPages(String.valueOf(j));
								path = String.format(separarPdf.getRutaPdfTemp() + "p-%s.pdf", j);
								stamper = new PdfStamper(reader, new FileOutputStream(path));
								stamper.close();
								reader.close();
		                        
								//Se lee cada pagina para obtener el nombre definitivo de cada PDF
								PDDocument document = null;
								document = PDDocument.load(new File(path));
								document.getClass();
								
								if(!document.isEncrypted()){
									PDFTextStripperByArea stripper = new PDFTextStripperByArea();
									stripper.setSortByPosition(true);
									PDFTextStripper Tstripper = new PDFTextStripper();
									String st = Tstripper.getText(document);
									//System.out.println("Text: " + st);
									String[] partes = st.split("\n");
									
									//Se obtiene chequera, importe, benef, folio y fec valor
									String sChequera="", sImporte="", sBenef="", sNoFolio="", sFecValor="";
									for(int k=0; k<partes.length; k++){
										System.out.println(partes[k]);
										if(partes[k].indexOf("Currency)")==0){
											
										}
										else if(partes[k].indexOf("Currency")==0){
											sChequera = partes[k+1];
											sChequera = sChequera.substring(0, 11);
										}
										else if(partes[k].indexOf("Crédito")==0){
											sChequera = partes[k+1];
											sChequera = sChequera.substring(0, 11);
										}
										else if(partes[k].indexOf("Payment Currency/Payment Amount")>=0 || partes[k].indexOf("Moneda/Monto del Pago")>=0){
											sImporte = partes[k];
											sImporte = sImporte.substring(sImporte.lastIndexOf("/")+2, sImporte.length());
											//sImporte = sImporte.replaceAll("\\.", "");
											sImporte = sImporte.replaceAll(",", "");
											sImporte = sImporte.replaceAll(" ", "");
											sImporte = sImporte.replaceAll("\r", "");
											sImporte = sImporte.replaceAll("\n", "");
										}
										else if(partes[k].indexOf("Original Input User Name")>=0){
											sBenef = partes[k];
											sBenef = sBenef.substring(sBenef.indexOf("Name")+5, sBenef.length());
											sBenef = sBenef.replaceAll(" ", "");
											sBenef = sBenef.replaceAll("\r", "");
											sBenef = sBenef.replaceAll("\n", "");
										}
										else if(partes[k].indexOf("Nombre de Beneficiario o Parte Deudora")>=0){
											sBenef = partes[k];
											sBenef = sBenef.substring(39, sBenef.length());
											sBenef = sBenef.replaceAll(" ", "");
											sBenef = sBenef.replaceAll("\r", "");
											sBenef = sBenef.replaceAll("\n", "");
										}
										else if(partes[k].indexOf("Transaction Reference Number")>=0 || partes[k].indexOf("mero de Referencia de Transacci")>=0){
											sNoFolio = partes[k];
											//sNoFolio = sNoFolio.substring(sNoFolio.indexOf("Number")+7, sNoFolio.length());
											//sNoFolio = sNoFolio.replaceAll(" ", "");
											sNoFolio = sNoFolio.substring(sNoFolio.length()-7, sNoFolio.length());
											if(sNoFolio.charAt(0) == '0')
												sNoFolio = sNoFolio.substring(1, sNoFolio.length());
											sNoFolio = sNoFolio.replaceAll("\r", "");
											sNoFolio = sNoFolio.replaceAll("\n", "");
										}
										else if(partes[k].indexOf("Value Date")>=0){
											sFecValor = partes[k];
											sFecValor = sFecValor.substring(sFecValor.indexOf("Date")+5, sFecValor.indexOf("Date")+15);
											//sFecValor = sFecValor.replaceAll("\\/", "");
											sFecValor = sFecValor.replaceAll("\r", "");
											sFecValor = sFecValor.replaceAll("\n", "");
											sFecValor = sFecValor.substring(2, 4) + sFecValor.substring(0, 2) + sFecValor.substring(4, sFecValor.length());
										}
										else if(partes[k].indexOf("Fecha de Valor")>=0){
											sFecValor = partes[k];
											sFecValor = sFecValor.substring(15, 25); //sFecValor.length());
											//sFecValor = sFecValor.replaceAll("\\/", "");
											sFecValor = sFecValor.replaceAll("\r", "");
											sFecValor = sFecValor.replaceAll("\n", "");
										}
									}
									
									//Se consulta el equivalente del pago en movimiento e hist_movimiento
									if(!sFecValor.equals("")){
										String sFecValorFormato = sFecValor.substring(3,5)+"/"+sFecValor.substring(0,2)+"/"+sFecValor.substring(6);
										int iExiste = separarPDFDao.consultaActualizaMovimiento(sNoFolio, sBenef, sFecValorFormato, sImporte, sChequera);
										//Si existe se pone en ruta Destino
										//Si no existe se pone en ruta de no identificados
										
										//Nuevo archivo
										String sNomArchivo = sNoFolio + "_" + sBenef + "_" + sFecValorFormato.replaceAll("\\/", "") + "_" + sImporte.replaceAll("\\.", "") + "_" + sChequera + ".pdf";
										
										logger.info("Archivo: ["+sNomArchivo+"] - Existe: ["+iExiste+"]");
										//System.out.println("Archivo: ["+sNomArchivo+"] - Existe: ["+iExiste+"]");
										
										File fichero1 = new File(path);
										File fichero2 = null;
										if(iExiste>=1){
											fichero2 = new File(separarPdf.getRutaPdfDestino() + sFecValor.substring(6, sFecValor.length()) + "/" + sFecValor.substring(0, 2) + "/" + sNomArchivo);
										}
										else
											fichero2 = new File(separarPdf.getRutaPdfNoIden() + sNomArchivo);
										
										logger.info("Archivo origen: " + fichero1.getAbsolutePath());
										logger.info("Archivo destino: " + fichero2.getAbsolutePath());
										//System.out.println("Archivo destino: " + fichero2.getAbsolutePath());
										logger.info("Renombra la pagina del PDF: " + fichero1.renameTo(fichero2));
									}
								}
								else{
									logger.info(path + " -> encriptado");
									//System.out.println( path + " -> encriptado");
								}
								
								document.close();
							}
							//Se mueve el pdf procesado a .proc para que no se vuelva a procesar
							File f = new File(separarPdf.getRutaPdfProc() + archivos[i].getName());
	                        //System.out.println("Se renombra a ["+f+"]");
	                        logger.info("Renombra el PDF " + archivos[i].getAbsolutePath() + " a " + f.getAbsolutePath() + " = " + archivos[i].renameTo(f));
						}//Si es PDF
					}//Si es archivo
				}//Por cada archivo en la ruta origen
			}
		}
		catch(IOException ioe){
			ioe.printStackTrace();
			logger.error(ioe);
		}
		catch(DocumentException de){
			de.printStackTrace();
			logger.error(de);
		}
	}
	
	//Getters and Setters
	public String getRutaPdfOrigen() {
		return rutaPdfOrigen;
	}

	public void setRutaPdfOrigen(String rutaPdfOrigen) {
		this.rutaPdfOrigen = rutaPdfOrigen;
	}

	public String getRutaPdfDestino() {
		return rutaPdfDestino;
	}

	public void setRutaPdfDestino(String rutaPdfDestino) {
		this.rutaPdfDestino = rutaPdfDestino;
	}
	public String getRutaRaiz() {
		return rutaRaiz;
	}

	public void setRutaRaiz(String rutaRaiz) {
		this.rutaRaiz = rutaRaiz;
	}
	public String getRutaPdfTemp() {
		return rutaPdfTemp;
	}

	public void setRutaPdfTemp(String rutaPdfTemp) {
		this.rutaPdfTemp = rutaPdfTemp;
	}
	public String getRutaPdfProc() {
		return rutaPdfProc;
	}

	public void setRutaPdfProc(String rutaPdfProc) {
		this.rutaPdfProc = rutaPdfProc;
	}
	public String getRutaPdfNoIden() {
		return rutaPdfNoIden;
	}

	public void setRutaPdfNoIden(String rutaPdfNoIden) {
		this.rutaPdfNoIden = rutaPdfNoIden;
	}
	
	public SepararPDFDao getSepararPDFDao() {
		return separarPDFDao;
	}

	public void setSepararPDFDao(SepararPDFDao separarPDFDao) {
		this.separarPDFDao = separarPDFDao;
	}

}
