package com.webset.set.barridosfondeos.integration;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletContext;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.barridosfondeos.dto.FondeoAutomaticoDto;
import com.webset.set.barridosfondeos.service.BarridosFondeosRepService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;

/**
 * Interface para el envio de correos en el modulo de Barridos y Fondeos
 *
 */
public class BarridosFondeosMail {
	Bitacora bitacora = new Bitacora();
	Contexto contexto = new  Contexto();
	Funciones funciones = new Funciones();
	
	public String enviarFondeoAutomatico (String datosGrid, int idEmpresaRaiz, int idUsuario, char tipoEvento, String nomEmpresa, ServletContext context){
		Gson gson = new Gson();
		//Map<String, Object> mapRetFondeo = new HashMap<String, Object>();
		
		List<FondeoAutomaticoDto> listGridFondeo = new ArrayList<FondeoAutomaticoDto>();
		List<Map<String, Object>> listaFinal = null;
		JRMapArrayDataSource jrDataSource = null;
		JasperPrint jasperPrint = null;
		
		try{
			
			BarridosFondeosRepService barridosFondeosRepService = (BarridosFondeosRepService)contexto.obtenerBean("barridosFondeosRepBusinessImpl", context);
			List<Map<String, String>> objParams = gson.fromJson(datosGrid, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			
			for(int i = 0; i < objParams.size(); i++)
			{
				FondeoAutomaticoDto dto = new FondeoAutomaticoDto();
				dto.setOrden(funciones.convertirCadenaInteger(objParams.get(i).get("orden")));
				dto.setPrestamos(funciones.convertirCadenaDouble(objParams.get(i).get("prestamos")));
		        dto.setNoEmpresaOrigen(funciones.convertirCadenaInteger(objParams.get(i).get("noEmpresaOrigen")));
		     	dto.setNomEmpresaOrigen(funciones.validarCadena(objParams.get(i).get("nomEmpresaOrigen").toString()));
		     	dto.setNoEmpresaDestino(funciones.convertirCadenaInteger(objParams.get(i).get("noEmpresaDestino")));
		     	dto.setNomEmpresaDestino(funciones.validarCadena(objParams.get(i).get("nomEmpresaDestino").toString()));
		     	dto.setDescBanco(funciones.validarCadena(objParams.get(i).get("descBanco").toString()));
		     	dto.setIdBanco(funciones.convertirCadenaInteger(objParams.get(i).get("idBanco")));
		     	dto.setIdChequeraPadre(funciones.validarCadena( objParams.get(i).get("idChequeraPadre").toString()));
		     	dto.setIdChequera(funciones.validarCadena( objParams.get(i).get("idChequera").toString()));
		     	dto.setSaldoChequera(funciones.convertirCadenaDouble(objParams.get(i).get("saldoChequera")));
		     	dto.setSaldoMinimoChequera(funciones.convertirCadenaDouble(objParams.get(i).get("saldoMinimoChequera")));
		     	dto.setImporteF(funciones.convertirCadenaDouble(objParams.get(i).get("importeF")));
		     	dto.setTipoCambio(funciones.convertirCadenaDouble(objParams.get(i).get("tipoCambio")));
		     	dto.setIdDivisa(funciones.validarCadena(objParams.get(i).get("idDivisa")));
		     	dto.setPm(funciones.convertirCadenaDouble(objParams.get(i).get("pm")));
		     	dto.setPmCheques(funciones.convertirCadenaDouble(objParams.get(i).get("pmCheques")));
		     	dto.setConcepto(funciones.validarCadena(objParams.get(i).get("concepto")));
		     	if (tipoEvento == 'F'){
		     		dto.setImporteTraspaso(funciones.convertirCadenaDouble(objParams.get(i).get("importeTraspaso")));
		     		dto.setImporteBarrido(new Double(0));
		     	}else{
		     		dto.setImporteTraspaso(new Double(0));
		     		dto.setImporteBarrido(funciones.convertirCadenaDouble(objParams.get(i).get("importeBarrido")));
		     	}
		     	dto.setSaldoCoinversion(funciones.convertirCadenaDouble(objParams.get(i).get("saldoCoinversion")));
		     	listGridFondeo.add(dto);
			}
			
			listaFinal = barridosFondeosRepService.obtenerReporteFondeoBarridoAut(listGridFondeo);
			
			jrDataSource = new JRMapArrayDataSource(listaFinal.toArray());
			
			String rutaServidor  = context.getRealPath("/WEB-INF/reportes/"); 
		    String reportFile = rutaServidor + File.separator + "ReporteFondeoBarrido.jrxml";
		    String pdfFile = rutaServidor + File.separator + "ReporteFondeoBarrido" + idUsuario + ".pdf";
		    
		    Map<String, Object> parametros = new HashMap<String, Object>();
		    JasperReport jasperReport = JasperCompileManager.compileReport(reportFile);
		    parametros.put("REPORT_DATA_SOURCE", jrDataSource);
			parametros.put("TITULO", "REPORTE");
			parametros.put("EMPRESA", nomEmpresa);
			Locale locale = new Locale("es", "MX");
			parametros.put(JRParameter.REPORT_LOCALE, locale);
			jasperPrint = JasperFillManager.fillReport(jasperReport, parametros);
			JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFile);
			
			String para = "mrodriguez@coinside.mx";
			String de = "mrodriguez@coinside.mx";
			String host = "mail.coinside.mx";
			Properties propiedades = System.getProperties();
			propiedades.setProperty("mail.smtp.host", host);
			Session sesion = Session.getDefaultInstance(propiedades);
			MimeMessage mensaje = new MimeMessage(sesion);
			mensaje.setFrom(new InternetAddress(de));
			mensaje.addRecipient(Message.RecipientType.TO, new InternetAddress(para));
			BodyPart body = new MimeBodyPart();
			if (tipoEvento == 'F'){
				mensaje.setSubject("Fondeos automaticos");
				body.setText("Se ejecutaron los Fondeos Automaticos que se indican en el documento adjunto");
			}else{
				mensaje.setSubject("Barridos automaticos");
				body.setText("Se ejecutaron los Barridos Automaticos que se indican en el documento adjunto");
			}
			
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(body);
			
			body = new MimeBodyPart();
			DataSource fuente = new FileDataSource(pdfFile);
			body.setDataHandler(new DataHandler(fuente));
			body.setFileName(pdfFile);
			multipart.addBodyPart(body);
			
			mensaje.setContent(multipart);
			Transport.send(mensaje);
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:BarridosFondeos, C:BarridosFondeosMail, M:enviarFondeoAutomatico");
		}
		return null;
	}
}
