package com.webset.set.utilerias.business.impl;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EnviarCorreoE {
	
	public static void main(String[] args) {
		String correoSalida="pruebascorreo2016@gmail.com";
	    String contrasenia="pruebasCorreo_2016";
		String puerto="587";
		String host="smtp.gmail.com";
		String correoReceptor="yoscardenas@gmail.com";
		String asunto="Pruebas SET";
		String cuerpoCorreo="<HTML><HEAD><TITLE>Aviso de Pago</TITLE></HEAD><BODY LINK='BLUE'VLINK='BLUE'><H3 ALIGN='CENTER'>PROMOTORA RIO BAKER SA DE CV<BR><HTML><HEAD><TITLE ALIGN='CENTER' >Aviso de Pago</TITLE></HEAD><BODY LINK='BLUE'  VLINK='BLUE'><H3 ALIGN='CENTER'><BR>AVISO DE PAGOS<BR>Por este medio le informamos que hemos realizado un pago a su favor, de acuerdo al siguiente detalle:<H5 ALIGN='Left'><H5><TABLE><TR><TD WIDTH=13%><STRONG>FECHA DE PAGO:</STRONG></TD><TD WIDTH=17%><STRONG>2015-01-15 00:00:00.0</STRONG></TD><TD WIDTH=7%><STRONG>BANCO:</STRONG></TD><TD WIDTH=30%><STRONG> SANTANDER</STRONG></TD><TD WIDTH=19%><STRONG> NO. REFERENCIA:</STRONG></TD><TD WIDTH=14%> <STRONG> </STRONG></TD></TR><TR></TR><TR></TR><TR><TD><STRONG>NO. DE CUENTA:</STRONG></TD><TD><STRONG>65502912978</STRONG></TD><TD><STRONG> DIVISA: </STRONG></TD><TD><STRONG>MN<STRONG></TD><TD><STRONG> TIPO DE CAMBIO: </STRONG></TD><TD><STRONG>1</STRONG></TD></TR><TR></TR><TR><TD><STRONG>BENEFICIARIO:      PARALIFT, S.A. DE C.V.   </STRONG> </TD><TD></TD></TR></TABLE ><BR><BR><BR>** Pago del Dia    2015-01-15 00:00:00.0 ***  Con Importe Abonado en Bancos : 46975<p class=MsoNormal> </p>Detalle De Facturas/Notas de Credito/Notas de Cargo Liquidadas <p class=MsoNormal> </p><table  BORDER=3><tr><td>   Contrarecibo    </td><td> No empresa Interna </td><td> No Benef Interno </td><td>   Importe     </td><td>Estatus</td><td>No Factura</td></tr></table></H3><BR><H5 ALIGN='CENTER'>Este correo es de caracter informativo.EMPRESA PAGADORA:</H5><BR><H5 ALIGN='LEFT'>Atentamente.<BR>PROMOTORA RIO BAKER SA DE CV</H5> <BR><H3 ALIGN='RIGHT'> <a href='http://www.webset.com.mx'> http://www.webset.com.mx </A> </H3>";
		/*Funciones fn= new Funciones();
		fn.enviaMail("yespino@webset.com.mx", "emedina@webset.com.mx;jflores@webset.com.mx", "Pruebas webset", "Envio de correo prueba");*/
	        try{
	        	System.out.println("inicio");
	            // Propiedades de la conexión
	            Properties props = new Properties();
	            props.setProperty("mail.smtp.host", host);
	            props.setProperty("mail.smtp.starttls.enable", "true");
	            props.setProperty("mail.smtp.port", puerto);
	            props.setProperty("mail.smtp.user", correoSalida);
	            props.setProperty("mail.smtp.auth", "true");
	            System.out.println("antes de crear la sesion");
	            // Preparamos la sesion
	            Session session = Session.getDefaultInstance(props);

	            // Construimos el mensaje
	            MimeMessage message = new MimeMessage(session);
	            message.setFrom(new InternetAddress(correoSalida));
	            message.addRecipient(
	                Message.RecipientType.TO,
	                new InternetAddress(correoReceptor));
	            message.setSubject(asunto);
	            message.setText("pruebas");
	            message.setContent(cuerpoCorreo, "text/html");
	            System.out.println("antes de enviarlo");
	            // Lo enviamos.
	            Transport t = session.getTransport("smtp");
	            t.connect(correoSalida, contrasenia);
	            t.sendMessage(message, message.getAllRecipients());
	            

	            // Cierre.
	            t.close();
	            System.out.println("correo enviado");
	        }catch (AddressException e) {
	        	System.out.println("error");
                e.printStackTrace();
	        } catch (Exception e){
	        	System.out.println("error 2");
	            e.printStackTrace();
	        }
	}
}
