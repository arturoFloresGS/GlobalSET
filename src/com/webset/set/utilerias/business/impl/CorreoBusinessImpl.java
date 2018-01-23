package com.webset.set.utilerias.business.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dao.CorreoDao;


/***********
 * @author YOSELINE E.C
 * Fecha: 04 de febrero del 2015
 *****/

public class CorreoBusinessImpl {
	private Bitacora bitacora = new Bitacora();
	private CorreoDao objCorreoDao;	
	
	@DirectMethod
	public void enviarCorreo(){
		try {
			bitacora.registrar("Inicia el proceso de envio de correos electronicos");
			bitacora.registrar("Se obtienen configuraciones");
			
			System.out.println("Inicia el proceso de envio de correos electronicos");
			System.out.println("Se obtienen configuraciones");
			
			InetAddress localHost = InetAddress.getLocalHost();
			
			String miIP = localHost.getHostAddress();
			String ipConf = objCorreoDao.consultarConfiguraSet(4848);
			String ejecutar = objCorreoDao.consultarConfiguraSet(5003);

			bitacora.insertarRegistro("IP local: " + miIP);
			bitacora.insertarRegistro("IP Configurada: " + ipConf);
			bitacora.insertarRegistro("Bandera de configuracion: " + ejecutar);
			bitacora.insertarRegistro("Inincia proceso de envío de correos");
			
			System.out.println("IP local: " + miIP);
			System.out.println("IP Configurada: " + ipConf);
			System.out.println("Bandera de configuracion: " + ejecutar);
			System.out.println("Inincia proceso de envío de correos");
			
			if (miIP.equals(ipConf) && ejecutar.toUpperCase().equals("SI")) {				
				bitacora.insertarRegistro("Inincia proceso de envío de correos 1");				
				//Constantes 
				/*String correoSalida="pruebascorreo2016@gmail.com";
			    String contrasenia="pruebasCorreo_2016";
				String puerto="587";
				String host="smtp.gmail.com";
				String correoSalidaDefecto="yoscardenas@gmail.com";*/				
				String correoSalida = this.objCorreoDao.consultarConfiguraSet(4950);
			    String contrasenia = this.objCorreoDao.consultarConfiguraSet(4951);
				String puerto = this.objCorreoDao.consultarConfiguraSet(4952);
				String host = this.objCorreoDao.consultarConfiguraSet(4953);
				String correoSalidaDefecto = this.objCorreoDao.consultarConfiguraSet(4954);
				
				Boolean destinatarioPorNoCliente = new Boolean(this.objCorreoDao.consultarConfiguraSet(4955));
				
				String enableStarttls = this.objCorreoDao.consultarConfiguraSet(4956);
				
			    Map<String,String> cuerposCorreo = new HashMap<String,String>();
				String psMail = "";
				String asunto = "Notificación de Pago";
				List<Map<String, String>> rstDatos = new ArrayList<Map<String,String>>();
				
	            // Propiedades de la conexión
	            Properties props = new Properties();
	            props.setProperty("mail.smtp.host", "smtp.office365.com");
	            props.setProperty("mail.smtp.starttls.enable", "true");
	            props.setProperty("mail.smtp.port", "587");
	            props.setProperty("mail.smtp.user", correoSalida);
	            props.setProperty("mail.smtp.auth", "false");
	            //props.setProperty("mail.smtp.ssl.trust", "true");
	            props.setProperty("mail.smtp.ssl.trust", "smtp.office365.com");
	            /*SmtpServer.Host = "smtp.office365.com";//"pod51012.outlook.com";
	            SmtpServer.Port = 587;
	            SmtpServer.UseDefaultCredentials = false;
	            SmtpServer.EnableSsl = false;*/

	            Enumeration<?> en = props.propertyNames();
	            
	            while(en.hasMoreElements()){
	            	String s = en.nextElement().toString();
	            	bitacora.insertarRegistro("["+ s +"] : ["+ props.getProperty(s) +"]");
	            }
	            
	            
	            // Preparamos la sesion           
	            rstDatos = objCorreoDao.enviarCorreo();
				bitacora.insertarRegistro("Registros para envío " + rstDatos.size());
	            
				if(rstDatos.isEmpty())
	            	return;
				
	            cuerposCorreo= armarHTML(rstDatos);
	            // Construimos el mensaje
	           
	          //for (int i = 0; i <= rstDatos.size(); i++) {
	            	
	            	for (Map<String, String> dato : rstDatos) {
	            		try{
		            		psMail = dato.get("email");
		            		
		            		if(psMail==null||psMail.equals("")){
		            			psMail =correoSalidaDefecto;
		            		}
			            	Session session = Session.getDefaultInstance(props);
				            MimeMessage message = new MimeMessage(session);
				            message.setFrom(new InternetAddress(correoSalida));
				            message.setSubject(asunto);
				            message.setText("pruebas");
				            message.setContent(cuerposCorreo.get(dato.get("no_folio_det")), "text/html");
				            
				            if(!destinatarioPorNoCliente){
					            message.addRecipient(
						                Message.RecipientType.TO,
						                new InternetAddress(correoSalidaDefecto));
				            }else{
				            	if(psMail==correoSalidaDefecto){
				            		message.addRecipient(Message.RecipientType.TO, new InternetAddress(psMail));
				            	}else{
				            		message.addRecipient(Message.RecipientType.TO, new InternetAddress(psMail));
				            		message.addRecipient(Message.RecipientType.CC, new InternetAddress(correoSalidaDefecto));
				            	}
				            
				            }
				            	/*
				            	String correoDestinatario =
				            			this.objCorreoDao.consultarCorreoPorNoCliente(new Integer(dato.get("no_cliente")));
					            message.addRecipient(
						                Message.RecipientType.TO,
						                new InternetAddress(correoDestinatario));
					            
					            List<String> correos = 
					            		this.objCorreoDao.correosAdicionalesPorNoCliente(new Integer(dato.get("no_cliente")));
					            
					            if(correos != null && !correos.isEmpty()){
						            Address[] cc = new Address[correos.size()];
						            
						            int i = 0;
						            
						            for(String s:correos){
						            	cc[i] = new InternetAddress (s);
						            	
						            	i++;
						            }
						            
						            message.addRecipients(Message.RecipientType.CC, cc);
					            }
					            */
				            	
				          
				            
				            // Lo enviamos.
				         
				            Transport t = session.getTransport("smtp");
				            t.connect(correoSalida, contrasenia);
				            t.sendMessage(message, message.getAllRecipients());
				            t.close();
				             
				            objCorreoDao.funSQLActEmailGenerado(dato.get("no_folio_det"), dato.get("importe"), dato.get("no_docto"), dato.get("no_Empresa"), dato.get("no_cliente"), psMail);
	

	            		}catch (AddressException e) {
	        	        	System.out.println("error");
	        				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
	        						"P: Utilerias, C: CorreoBusinessImpl, M: enviarCorreo");
	        	        } catch (Exception e){
	        	        	System.out.println("error 2");
	        	        	System.out.println( e);
	        				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
	        						"P: Utilerias, C: CorreoBusinessImpl, M: enviarCorreo");
	        	        } 
	            	}
	            //}
	            // Cierre.
	            
			} else {
				bitacora.registrar("El nodo no esta configurado para este proceso.");
			}
			
		} catch (UnknownHostException e1) {
			bitacora.registrar("El proceso no termino de forma correcta.");
			bitacora.insertarRegistro(new Date().toString() + " " + e1.toString() + 
					"P: Utilerias, C: CorreoBusinessImpl, M: enviarCorreo");
		} catch (Exception e) {
			bitacora.registrar("El proceso no termino de forma correcta.");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Utilerias, C: CorreoBusinessImpl, M: enviarCorreo");
		}
	}
	
	
	public Map<String,String> armarHTML(List<Map<String, String>> rstDatos) {
		List<Map<String, String>> listRstDatosCorreo;
		Map<String, String> rstDatosCorreo;
		StringBuffer sb= new StringBuffer();
		List<Map<String, String>> rstDatosPago;
		Map<String,String> resultado = new HashMap<String,String>();
		if ( rstDatos.size() > 0 ) {
			for (Map<String, String> dato : rstDatos) {
				sb.delete(0, sb.length());
				
					listRstDatosCorreo = objCorreoDao.funSQLDatosCorreo("");
					rstDatosCorreo = listRstDatosCorreo.get(0);
					//******************************************************
					//aqui llena los datos del pago
					sb.append("<HTML>");
					
					//Encabezado pagina
					sb.append("<HEAD>");
					sb.append("<TITLE>Aviso de Pago</TITLE>");
					sb.append("</HEAD>");
					
					//Cuerpo pagina
					//Encabezado
					sb.append("<BODY LINK='BLUE'VLINK='BLUE'>");
					sb.append("<H3 ALIGN='CENTER'>");
					sb.append(dato.get("nom_empresa") + "<BR>");
					
					sb.append("<BR>" + rstDatosCorreo.get("parrafo_1"));
					sb.append("<BR>" + rstDatosCorreo.get("parrafo_2"));

					sb.append("<H5 ALIGN='Left'><H5><TABLE>");
					sb.append("<TR><TD WIDTH=13%><STRONG>" + rstDatosCorreo.get("parrafo_3") + "</STRONG></TD>");
					sb.append("<TD WIDTH=17%><STRONG>" + dato.get("fec_valor") + "</STRONG></TD>");
					sb.append("<TD WIDTH=7%><STRONG>" + rstDatosCorreo.get("parrafo_4") + "</STRONG></TD>");
					sb.append("<TD WIDTH=30%><STRONG> " + dato.get("desc_banco") + "</STRONG></TD>");
					sb.append("<TD WIDTH=19%><STRONG> " + rstDatosCorreo.get("parrafo_5") + "</STRONG></TD>");


					sb.append("<TD WIDTH=14%> <STRONG>" + (dato.get("referencia") == null ? "" : dato.get("referencia")) + " </STRONG></TD></TR>");
					sb.append("<TR></TR><TR></TR><TR><TD><STRONG>" + rstDatosCorreo.get("parrafo_6") + "</STRONG></TD>");
					sb.append("<TD><STRONG>" + dato.get("id_chequera") + "</STRONG></TD>");
					sb.append("<TD><STRONG> " + rstDatosCorreo.get("parrafo_7") + " </STRONG></TD>");
					sb.append("<TD><STRONG>" + dato.get("id_divisa") + "<STRONG></TD>");
					sb.append("<TD><STRONG> " + rstDatosCorreo.get("parrafo_8") + " </STRONG></TD>");
					sb.append("<TD><STRONG>" + dato.get("tipo_cambio") + "</STRONG></TD></TR>");
					sb.append("<TR></TR><TR><TD><STRONG>" + rstDatosCorreo.get("parrafo_9") + "      " + dato.get("beneficiario") + "</STRONG> </TD><TD></TD>");
					
					if ( Integer.parseInt(dato.get("id_forma_pago")) == 3 ) {
						sb.append("<TD><STRONG> " + rstDatosCorreo.get("parrafo_23") + "</STRONG></TD>");
						sb.append("<TD><STRONG>" + dato.get("desc_banco_benef") + "</STRONG></TD>");
						sb.append("<TD><STRONG>" + rstDatosCorreo.get("parrafo_24") + "</STRONG></TD>");
						sb.append("<TD><STRONG>" + dato.get("id_chequera_benef") + "</STRONG></TD></TR>");
					} else {
						sb.append("</TR>");
					}

					sb.append("</TABLE ><BR><BR><BR>");
					//sb.append( "<TABLE WIDTH=100 BORDER=1><TR>",porciento);

					//********************************************************


					String pdGrupoPago = dato.get("grupo_pago") == null ? "0" : dato.get("grupo_pago");

					rstDatosPago = null;
					if ( !pdGrupoPago.equals("0") ) {
						rstDatosPago = objCorreoDao.funSQLDetallePagoAgrupado(dato.get("no_docto"), pdGrupoPago);

						sb.append("** Pago del Dia    " + dato.get("fec_valor") + " ***  Con Importe Abonado en Bancos : " + /*; Format(*/dato.get("importe")/*, "###,###.00")*/ + "<p class=MsoNormal> </p>");

						sb.append("Detalle De Facturas/Notas de Credito/Notas de Cargo Liquidadas " + "<p class=MsoNormal> </p>");

						sb.append("<table  BORDER=3><tr><td>   Contrarecibo    </td><td> No empresa Interna </td><td> No Benef Interno </td><td>   Importe     </td><td>Estatus</td><td>No Factura</td></tr>");

						for (Map<String, String> datoPago : rstDatosPago) {
							sb.append("<tr><td>" + datoPago.get("contra_rec") + "</td><td>" + datoPago.get("no_empresa") + "</td><td>  " + datoPago.get("no_benef") + "</td><td>" + /*Format(*/(datoPago.get("imp_solic")/*, "###,###.00")*/ + "               ").substring(0, 15) + "</td><td>" + "PAGADO" + "</td><td>" + datoPago.get("no_factura") + "</td><tr>");
						}
					} else {
						rstDatosPago = objCorreoDao.funSQLDetalleNoPagoAgrupado(dato.get("no_docto"), pdGrupoPago);
						sb.append("** Pago del Dia       " + dato.get("fec_valor") + " ***  Con Importe Abonado en Bancos : " + /*Format(*/dato.get("importe")/*, "###,###.00")*/ + "<p class=MsoNormal> </p>");

						sb.append("Detalle De Facturas/Notas de Credito/Notas de Cargo Liquidadas " + "<p class=MsoNormal> </p>");

						sb.append("<table  BORDER=3><tr><td>   Contrarecibo    </td><td> No empresa Interna </td><td> No Benef Interno </td><td>   Importe     </td><td>Estatus</td><td>No Factura</td></tr>");

						for (Map<String, String> datoPago : rstDatosPago) {
							sb.append("<tr><td>" + datoPago.get("contra_rec") + "</td><td>" + datoPago.get("no_empresa") + "</td><td>  " + datoPago.get("no_benef") + "</td><td>" + (/*Format(*/datoPago.get("imp_solic")/*, "###,###.00")*/ + "               ").substring(0, 15) + "</td><td>" + "PAGADO" + "</td><td>" + datoPago.get("no_factura") + "</td><tr>");
						}
					}
					sb.append("</table>");

					//Pone pie de Pagina del archivo html
					//********************************************
					sb.append("</H3>");

					sb.append("<BR><H5 ALIGN='CENTER'>" + rstDatosCorreo.get("parrafo_19"));

					//numArchivo.append(rstDatosCorreo.get("parrafo_2")0 + "<BR><BR><BR>",
					sb.append(rstDatosCorreo.get("parrafo_21") + "</H5>");


					sb.append("<BR><H5 ALIGN='LEFT'>" + rstDatosCorreo.get("parrafo_22") + "<BR>" + dato.get("nom_empresa") + "</H5> ");
					sb.append("<BR><H3 ALIGN='RIGHT'> <a href='http://www.webset.com.mx'> http://www.webset.com.mx </A> </H3></BODY></HTML>"); 
					//<BR><H3 ALIGN="RIGHT">                   <a href="http://www.webset.com.mx"> http://www.webset.com.mx </A> </H3></BODY></HTML>

					//**************************************************************************************************************

					System.out.println("Cerro Archivo de Correo ");

					//aqui se marca el campo en movimiento para marcar que se mando el correo electronico

					//plAfectados2 = funSQLActEmailGenerado(dato.get("no_folio_det"), dato.get("importe"), dato.get("no_docto"), dato.get("no_Empresa"), dato.get("no_cliente"), psMail);
					resultado.put(dato.get("no_folio_det"),sb.toString());
					
			}
		} else {
			System.out.println("No Encotro Registros Consulta funSQLSelectPagosEnviarCorreo ");
		}
		return resultado;
	}
	
	//*********************************************************************
	public CorreoDao getCorreoDao() {
		return objCorreoDao;
	}

	public void setObjCorreoDao(CorreoDao objCorreoDao) {
		this.objCorreoDao = objCorreoDao;
	}

}
