package com.webset.set.utilerias.business.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;


public class MandarMail {
	//private Boolean HabilitarConfig;  // Para habilitar el uso de los .INI
	//private Boolean HabilitarRegistro;  // Para habilitar el uso de los .LOG

	private String RutaIni="C:\\WEBSETSVN\\trunk\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\SET\\WEB-INF\\";  // Ruta del archivo .Ini a utilizar
	private String RutaLog;  // Ruta del archivo .log a utilizar
	private String RutaGen ="C:\\WEBSETSVN\\trunk\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\SET\\WEB-INF\\";   //Ruta Del archivo general.ini a utilzar
	private String RutaLogAviso = "C:\\appset";  // Ruta del archivo .log del aviso a utilizar
	private String remitente_mail; 
	private String aviso_mail; 
	private String Dominio; 
	private String SMTP; 
	//private String cadena_entrada;  //Contiene los parametros que se mandan para la operacion
	private String Ruta_Snd_Mail; 
	private Boolean IncluirFecha;  // Para incluir la Fecha en el archivo log
	private Boolean IncluirHora;  // Para incluir la Hora en el archivo log
	//private Date GT_FECHA_HOY; 

	
	//private String DataControlConfiguracion;


	//Nuevos
	//***************
	//private String usuario    ; 
	//private String password   ; 
	//private String Server     ; 
	//private String DataBase   ; 
	//private String PATH1      ; 
	//private String PATH2      ; 
	//private String PATH3      ; 
	//*****************

	//private EstaConectado; Boolean
	//private CCommand; ADODB.Command
	//private CConexion; Connection
	//private strconexion; String

	//private usuario    ; String
	//private password   ; String
	//private Server     ; String
	//private DataBase   ; String
	private String RProv      ; 
	//private String RCheq      ; 
	//private String RFact      ; 
	//private String NafinEnvio     ; 
	//private String RNafinR     ; 
	//private String RNafinE     ; 
	//private String EXAS400      ; 
	//private String FELECT; 
	//private String FREFEL; 
	private String RRech;

	//private Integer iPosicion_pipe; 
	//private String Scuantos_acomodado; 
	/*private String cadena; 
	private Long result; 
	private String sMensaje; 
	private String psRutaArchPagos; 
	private String ps_Archivo; 
	private String psRutacompleta; 
	private Long plResultado; 
	private String psArchivos; 
	private Boolean pb_ArchivOk; 
	private Long plAfectados; 
	private Boolean pb_DatosOk; 
	private Long iContador; 
	private Long plReg_leidos ; 
	private String ps_registro; 
	private String psTmp; 
	private Integer plError; 
	private Integer psCountpipes; 
	private Integer i; 
	private String ps_registro_1; 
	private Long psBorra;*/
	private String psMail;
	private String RutaArch;
	private String psRutarRespaldoHtml = "C:\\WEBSETSVN\\trunk\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\SET\\WEB-INF\\banca_electronica_res\\psRutaResp\\general.ini";
	private boolean pBandera;
	
	/*Dias agregado por YEC*/
	String dias[]={"Domingo","Lunes","Martes","Miercoles","Jueves","Viernes","Sabado"};
	String psRutaResp="";
	
	private java.sql.Connection con=null;
	private java.sql.Statement stmt;
	public ResultSet rs;
	public JdbcTemplate jdbcTemplate=null;
	

     //MandarMail
	public static void main(String[] args) {
		MandarMail mandarMail = new MandarMail();
		mandarMail.correlos();
	}
	
	//Agregado por YEC
	
	private void correlos(){
		
		try {
			String driver = "oracle.jdbc.driver.OracleDriver";
			String ulrjdbc = "jdbc:oracle:thin:C##PRUEBASET/PRUEBA_SET@localhost:1521:orcl";
		   Class.forName(driver).newInstance();
		   con = DriverManager.getConnection(ulrjdbc);
		   stmt = (Statement) con.createStatement();
		} catch (Exception e) {
			System.out.println("Error de conexion");
			return;
		}
		
		   
		System.out.println("en correlos");
		//String nombre_dia;
		int num_proceso;
		String nombre_proceso;
		Object proceso_Set;
		int result;
		String regreso;
		//int cuantos;
		String errorDb;
		//int piIntentos ;
		boolean error ;
		//rsDatos aADOBD
		//String psRutaArchNafin;
		//String ls_registro;
		//String psNomFile;
		//int psFolio;
		//boolean psCreoFile;
		//int psNumRegistros;
		//Double psImporteTotal;
		//Dim rstDatosRevisa As ADODB.Recordset
		//Dim rstRevisaDetArch As ADODB.Recordset
		//Dim rstDatosRevCtas As ADODB.Recordset
		//int plLongAc;
		//Dim rsDatoControlImportador As ADODB.Recordset
	    //Dim rstDatosContra As ADODB.Recordset
		//int psResult;
		pBandera=false;
		
		try{
			//nombre_dia="Martes";
			//nombre_dia=dias[(GlobalSingleton.getInstancia().getFechaHoy().getDay())]; // nombre_dia = dias(Weekday(Now) - 1)
			System.out.println("Inicio de procesos");
			num_proceso=0;
			String rutaPrueba="C:\\WEBSETSVN\\trunk\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\SET\\WEB-INF\\banca_electronica_res\\psRutaResp\\general.ini";
			boolean prueba=true;
			//while (!LeeIni("Proceso"+num_proceso,"nombre",rutaPrueba).equals("")) { //While ConfigX.LeeIni("Proceso" & num_proceso, "nombre", RutaIni) <> ""
			while(prueba){
				result=-1;
				regreso="";
				errorDb="";
				nombre_proceso=LeeIni("Proceso"+num_proceso,"nombre",RutaIni);// nombre_proceso = ConfigX.LeeIni("Proceso" & num_proceso, "nombre", RutaIni)
				System.out.println("Ejecucion de proceso Proceso: " + num_proceso +" nombre: " +RutaIni);//Debug.Print "Ejecucion de proceso" & ConfigX.LeeIni("Proceso" & num_proceso, "nombre", RutaIni)
				try{
					System.out.println("Proceso"+num_proceso + " Ejecutado "+" SI "); //ConfigX.GuardaIni "Proceso" & num_proceso, "Ejecutado", "SI"
				}catch(Exception e){//On Error GoTo ErroresEjecucionProcesos
					return;
				}
				// MainForm.Caption = "SET Scheduler - Ejecutando " & nombre_proceso & " Hora: " & Format(Time, "HH:MM")
				result=0;
				switch (nombre_proceso.toUpperCase())  {
				case "ENVIA_CORREO_PAGOS":
					/*********************************************************************************
	                *aqui va a generar los rechazos en el archivo de compensacion
	                ***************************************************************************/
					Lee_ini_impProv(RutaGen.replace("\\general.ini" , "\\ImpProv.ini"));
					psRutarRespaldoHtml=psRutaResp.trim();
					armaHtmlPagos();
					result = 0; 
					
					break;
				}
				
				if(result==0){
					System.out.println ("Proceso: Envio Correo , resultado: " + result);
					System.out.println(result);
				}else
					error=true;
					System.out.println("Proceso: Envio Correo , resultado: "+result+",regreso" + regreso+ ", errorDb"+errorDb);
					//ConfigX.GuardaLog "Proceso: Envio Correo , resultado: " & result & ", regreso: " & regreso & ", errorDb: " & errorDb
				num_proceso=num_proceso+1;
				proceso_Set=null;	
				prueba=false;
			}
			con.close();
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error");
		}
	}
	
	public void GuardaLogInicia(String mensaje){
		FileWriter numArchivo=null;
		try {
			numArchivo = new FileWriter(RutaLog);
			if (numArchivo != null) {
				if(!IncluirFecha && !IncluirHora)
					numArchivo.write(mensaje);//Print #1, Mensaje
				if(IncluirFecha && IncluirHora)
					numArchivo.write( "02-02-2016" + "12:28"+mensaje);//Print #1, Date & Chr$(9) & Time & Chr$(9) & Mensaje
				if(IncluirFecha && !IncluirHora)
					numArchivo.write("02-02-2016"+mensaje); //Print #1, Date & Chr$(9) & Mensaje
				if(!IncluirFecha && IncluirHora)
					numArchivo.write("12:28"+mensaje); //Print #1, Time & Chr$(9) & Mensaje
			}
			numArchivo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String LeeIni(String bloque,String variable,String rutaLectura){
		/*String cadena;
		String cadenaG;
		try{
			BufferedReader a = new BufferedReader (new FileReader (rutaLectura));
			//a= GetPrivateProfileString (bloque,variable,"",cadena,cadena.length(),rutaLectura);
			if(a!=null)
				return "";
		}catch (Exception e) {
		     System.out.println(e);
	    }*/return "ENVIA_CORREO_PAGOS";
	}
	
	private void Lee_ini_impProv(String ruta){
		/*String cadena;
		int x;
		try {
			BufferedReader a=new BufferedReader(new FileReader(ruta));
			while ((cadena = a.readLine()) != null)   {
				//cadena = a.readLine();
				if(cadena.matches("PPROV"))
					RProv= cadena.trim(); // RProv = Trim(Right(cadena, Len(cadena) - InStr(1, cadena, "=")))
				if(cadena.matches("PRECH"))
					RRech= cadena.trim(); // RRech = Trim(Right(cadena, Len(cadena) - InStr(1, cadena, "=")))
				if(cadena.matches("RUTPAG"))
					psRutaResp= cadena.trim(); //psRutaResp = Trim(Right(cadena, Len(cadena) - InStr(1, cadena, "=")))
			}
			a.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		RProv="banca_electronica_res\\RProv";
		RRech="banca_electronica_res\\RRech";
		psRutaResp="C:\\WEBSETSVN\\trunk\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\SET\\WEB-INF\\banca_electronica_res\\psRutaResp\\";
	}
	
	//fin agregado por YEc
	public void armaHtmlPagos() {
		//String psMensaje;
		//String newname;
		List<Map<String, String>> rstDatos;
		List<Map<String, String>> rstDatosPago;
		FileWriter numArchivo;
		List<Map<String, String>> listRstDatosCorreo;
		Map<String, String> rstDatosCorreo;
		String pdGrupoPago;
		int plAfectados2;
		Boolean pbDefaul;
		String psBenef;
		String psDesc;


	//FUNCION QUE VA ARMAR EL EXCEL DE TODOS LOS PAGADOS HOY

	rstDatos = null;
	
	
	try{
		//rstDatos = funSQLSelectPagosEnviarCorreo(GlobalSingleton.getInstancia().getFechaHoy());
		rstDatos = funSQLSelectPagosEnviarCorreo();
		System.out.println("Ejecuto Consulta funSQLSelectPagosEnviarCorreo ");
		

		if ( rstDatos.size() > 0 ) {
			for (Map<String, String> dato : rstDatos) {
				numArchivo = null;
				pbDefaul = false;
				psMail = "";
				psMail = dato.get("email");
				if(pbDefaul || psMail==null)
					psMail="correo@prueba.com";
					
				pbDefaul = psMail.equals("");
				psMail = pbDefaul ? 
							consultarConfiguraSet(/*default_mail*/ 3209) : 
							psMail;

				//Vamos a poner este correo para probar
				//psMail = "cgarcia@webset.com.mx"
				psBenef = quita_caracteres_raros(dato.get("beneficiario"));

				RutaArch = psRutarRespaldoHtml + "\\" + 
							dato.get("no_Empresa") + "-" + 
							dato.get("equivale_persona") + "-" + 
							dato.get("no_folio_det") + "-" + psBenef + ".txt";

				System.out.println("Antes de Crear Archivo Open RutaArch For Output As #numArchivo ");

				File f = new File(RutaArch);						

				numArchivo = null;

				try {
					numArchivo = new FileWriter(f, true);
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (numArchivo != null) {
					listRstDatosCorreo = funSQLDatosCorreo("");
					rstDatosCorreo = listRstDatosCorreo.get(0);
					//******************************************************
					//aqui llena los datos del pago
					numArchivo.append("<HTML>");
					numArchivo.append("<HEAD>");
					numArchivo.append("<TITLE>Aviso de Pago</TITLE>");
					numArchivo.append("</HEAD>");
					numArchivo.append("<BODY LINK='BLUE'VLINK='BLUE'>");
					numArchivo.append("<H3 ALIGN='CENTER'>");
					numArchivo.append(dato.get("nom_empresa") + "<BR>");

					numArchivo.append("<HTML><HEAD><TITLE ALIGN='CENTER' >Aviso de Pago</TITLE>");
					numArchivo.append("</HEAD><BODY LINK='BLUE'  VLINK='BLUE'><H3 ALIGN='CENTER'>");
					//rint #numArchivo, "<BR> <BR> <BR>" + ""
					numArchivo.append("<BR>" + rstDatosCorreo.get("parrafo_1"));
					numArchivo.append("<BR>" + rstDatosCorreo.get("parrafo_2"));

					numArchivo.append("<H5 ALIGN='Left'><H5><TABLE>");
					numArchivo.append("<TR><TD WIDTH=13%><STRONG>" + rstDatosCorreo.get("parrafo_3") + "</STRONG></TD>");
					numArchivo.append("<TD WIDTH=17%><STRONG>" + dato.get("fec_valor") + "</STRONG></TD>");
					numArchivo.append("<TD WIDTH=7%><STRONG>" + rstDatosCorreo.get("parrafo_4") + "</STRONG></TD>");
					numArchivo.append("<TD WIDTH=30%><STRONG> " + dato.get("desc_banco") + "</STRONG></TD>");
					numArchivo.append("<TD WIDTH=19%><STRONG> " + rstDatosCorreo.get("parrafo_5") + "</STRONG></TD>");


					numArchivo.append("<TD WIDTH=14%> <STRONG>" + (dato.get("referencia") == null ? "" : dato.get("referencia")) + " </STRONG></TD></TR>");
					numArchivo.append("<TR></TR><TR></TR><TR><TD><STRONG>" + rstDatosCorreo.get("parrafo_6") + "</STRONG></TD>");
					numArchivo.append("<TD><STRONG>" + dato.get("id_chequera") + "</STRONG></TD>");
					numArchivo.append("<TD><STRONG> " + rstDatosCorreo.get("parrafo_7") + " </STRONG></TD>");
					numArchivo.append("<TD><STRONG>" + dato.get("id_divisa") + "<STRONG></TD>");
					numArchivo.append("<TD><STRONG> " + rstDatosCorreo.get("parrafo_8") + " </STRONG></TD>");
					numArchivo.append("<TD><STRONG>" + dato.get("tipo_cambio") + "</STRONG></TD></TR>");
					numArchivo.append("<TR></TR><TR><TD><STRONG>" + rstDatosCorreo.get("parrafo_9") + "      " + dato.get("beneficiario") + "</STRONG> </TD><TD></TD>");
					
					if ( Integer.parseInt(dato.get("id_forma_pago")) == 3 ) {
						numArchivo.append("<TD><STRONG> " + rstDatosCorreo.get("parrafo_23") + "</STRONG></TD>");
						numArchivo.append("<TD><STRONG>" + dato.get("desc_banco_benef") + "</STRONG></TD>");
						numArchivo.append("<TD><STRONG>" + rstDatosCorreo.get("parrafo_24") + "</STRONG></TD>");
						numArchivo.append("<TD><STRONG>" + dato.get("id_chequera_benef") + "</STRONG></TD></TR>");
					} else {
						numArchivo.append("</TR>");
					}

					numArchivo.append("</TABLE ><BR><BR><BR>");
					//numArchivo.append( "<TABLE WIDTH=100 BORDER=1><TR>",porciento);

					//********************************************************


					pdGrupoPago = dato.get("grupo_pago") == null ? "0" : dato.get("grupo_pago");

					rstDatosPago = null;
					if ( pdGrupoPago.equals("0") ) {
						rstDatosPago = funSQLDetallePagoAgrupado(dato.get("no_docto"), pdGrupoPago);

						numArchivo.append("** Pago del Dia    " + dato.get("fec_valor") + " ***  Con Importe Abonado en Bancos : " + /*; Format(*/dato.get("importe")/*, "###,###.00")*/ + "<p class=MsoNormal> </p>");

						numArchivo.append("Detalle De Facturas/Notas de Credito/Notas de Cargo Liquidadas " + "<p class=MsoNormal> </p>");

						numArchivo.append("<table  BORDER=3><tr><td>   Contrarecibo    </td><td> No empresa Interna </td><td> No Benef Interno </td><td>   Importe     </td><td>Estatus</td><td>No Factura</td></tr>");

						for (Map<String, String> datoPago : rstDatosPago) {
							numArchivo.append("<tr><td>" + datoPago.get("contra_rec") + "</td><td>" + datoPago.get("no_Empresa") + "</td><td>  " + datoPago.get("no_benef") + "</td><td>" + /*Format(*/(datoPago.get("imp_solic")/*, "###,###.00")*/ + "               ").substring(0, 15) + "</td><td>" + "PAGADO" + "</td><td>" + datoPago.get("no_factura") + "</td><tr>");
						}
					} else {
						rstDatosPago = funSQLDetallePagoAgrupado(dato.get("no_docto"), pdGrupoPago);

						numArchivo.append("** Pago del Dia       " + dato.get("fec_valor") + " ***  Con Importe Abonado en Bancos : " + /*Format(*/dato.get("importe")/*, "###,###.00")*/ + "<p class=MsoNormal> </p>");

						numArchivo.append("Detalle De Facturas/Notas de Credito/Notas de Cargo Liquidadas " + "<p class=MsoNormal> </p>");

						numArchivo.append("<table  BORDER=3><tr><td>   Contrarecibo    </td><td> No empresa Interna </td><td> No Benef Interno </td><td>   Importe     </td><td>Estatus</td><td>No Factura</td></tr>");

						for (Map<String, String> datoPago : rstDatosPago) {
							numArchivo.append("<tr><td>" + datoPago.get("contra_rec") + "</td><td>" + datoPago.get("no_Empresa") + "</td><td>  " + datoPago.get("no_benef") + "</td><td>" + (/*Format(*/datoPago.get("imp_solic")/*, "###,###.00")*/ + "               ").substring(0, 15) + "</td><td>" + "PAGADO" + "</td><td>" + datoPago.get("no_factura") + "</td><tr>");
						}
					}

					numArchivo.append("</table>");

					//Pone pie de Pagina del archivo html
					//********************************************
					numArchivo.append("</H3>");

					numArchivo.append("<BR><H5 ALIGN='CENTER'>" + rstDatosCorreo.get("parrafo_19"));

					//numArchivo.append(rstDatosCorreo.get("parrafo_2")0 + "<BR><BR><BR>",
					numArchivo.append(rstDatosCorreo.get("parrafo_21") + "</H5>");


					numArchivo.append("<BR><H5 ALIGN='LEFT'>" + rstDatosCorreo.get("parrafo_22") + "<BR>" + dato.get("nom_empresa") + "</H5> ");
					numArchivo.append("<BR><H3 ALIGN='RIGHT'> <a href='http://www.webset.com.mx'> http://www.webset.com.mx </A> </H3>"); 
					//<BR><H3 ALIGN="RIGHT">                   <a href="http://www.webset.com.mx"> http://www.webset.com.mx </A> </H3></BODY></HTML>

					//**************************************************************************************************************

					try {
						numArchivo.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

					System.out.println("Cerro Archivo de Correo ");

					//aqui se marca el campo en movimiento para marcar que se mando el correo electronico

					plAfectados2 = funSQLActEmailGenerado(dato.get("no_folio_det"), dato.get("importe"), dato.get("no_docto"), dato.get("no_Empresa"), dato.get("no_cliente"), psMail);

					if ( plAfectados2 != 0 ) {
						System.out.println("Va a preparar archivo de Correo No folio_det " + dato.get("no_folio_det") + "  No Docto  : " + dato.get("no_docto"));

						pBandera = true;

						psDesc = "";
						if ( pbDefaul = true ) {
							psDesc = "Prov. Sin Email. Confirmacion de Pagos a Detalle";
						} else {
							psDesc = "Confirmacion de Pagos a Detalle";
						}

						envia_mail_error("Confirmacion de Pagos a Detalle", 0, true, false, psDesc);

						try {Thread.sleep(2000);} catch (InterruptedException e) {}

					} else {
						System.out.println("No actulizo ningun registro con correo ");
					}
				} else {
					System.out.println("error al crear el archivo.");
				}
			}
		} else {
			System.out.println("No Encotro Registros Consulta funSQLSelectPagosEnviarCorreo ");
		}
	}catch(Exception e) {
		e.printStackTrace();
		System.out.println("Ocurrio un error en la funcion ArmaHtmlPagos");
		System.out.println(" Ruta Configurada " + RutaArch);
	}
}
	
	private String consultarConfiguraSet(int j) {
		switch (j) {
		case 3209:
			return "asdf_arturo@hotmail.com";//default_mail
		case 3232:
			return "c:\\temp\\SET_AVISO_DE_PAGO.txt";/*RutaLogAviso*/
		case 3233:
			return "webset@herdezdelfuerte.com";/*reimtente_mail*/
		case 3234:
			return "webset@herdezdelfuerte.com";/*aviso_mail*/
		case 3235:
			return "C:\\Netroset\\procesosC\\snd_mail\\";/*ruta_snd_mail*/
		case 3236:
			return "webset@herdezdelfuerte.com";/*error_mail*/
		case 3237:
			return "cgarcia@netroset.com;cperez@herdezdelfuerte.com";/*error_mail_cc*/
		case 3238:
			return "cgarcia@netroset.com;cperez@herdezdelfuerte.com";/*aviso_mail_cc*/
		default:
			return "";
		}
	}

	public String envia_mail_error(String nombre_proceso, int result, boolean adjunto,
            boolean Error, String asunto) {
            
	String archivo_log=null;
	FileWriter numArchivo;
	BufferedReader numArchivo1;
	String commandline;
	String ps_registro;
	
	try {
	
	if ( Error ) {
	    // se obtiene el nombre del archivo de log
	    if ( adjunto ) {
	        if (nombre_proceso.equals("ENVIA_CORREO_PAGOS")) {
	                archivo_log = "c:\\temp\\EnviaCorreoPagos.txt";
	        }
	    }
	} else {
	    // no hay error se solicita que se envie el archivo de log a los indicados en el ini
	    archivo_log = RutaArch;
	}
	
	// se verifica que exista el archivo , por si acaso no se creo y que tenga la fecha de hoy ademas de
    // hacer una copia de el pára no perder el log
	RutaLogAviso = consultarConfiguraSet(/*RutaLogAviso*/3232);
	remitente_mail = consultarConfiguraSet(/*reimtente_mail*/3233);
	aviso_mail = consultarConfiguraSet(/*aviso_mail*/3234);
	Dominio = Dominio;
	SMTP = SMTP;
	Ruta_Snd_Mail = consultarConfiguraSet(/*ruta_snd_mail*/3235);
			
			
	File f = new File(RutaLogAviso);						

	numArchivo = null;

	try {
		numArchivo = new FileWriter(f, true);
	} catch (IOException e) {
		e.printStackTrace();
	}

	if (numArchivo != null) {

		commandline = "";
		commandline = Ruta_Snd_Mail + "snd_mail " + SMTP + " " + remitente_mail;
	
	if ( Error ) {
	    numArchivo.append("To: " + consultarConfiguraSet(/*error_mail*/3236));
	    numArchivo.append("Cc: " + consultarConfiguraSet(/*error_mail_cc*/3237));
	    commandline = commandline + " \"" + consultarConfiguraSet(/*error_mail*/3236) + ";" + consultarConfiguraSet(/*error_mail_cc*/3237) + "\"";
	} else {
	    numArchivo.append("To: " + psMail );//consultarConfiguraSet(/*aviso_mail*/3234);
	    numArchivo.append("Cc: " + consultarConfiguraSet(/*aviso_mail_cc*/3238));
	    commandline = commandline + " \"" + psMail + ";" + consultarConfiguraSet(/*aviso_mail_cc*/3238) + "\"";
	}
	numArchivo.append("From: " + remitente_mail);
	numArchivo.append("Subject: " + asunto);
	
	numArchivo.append("Content-type: text/html; charset=\"US-ASCII\"" + (char)13);
	//numArchivo.append("Content-transfer-encoding: 7BIT" + Chr(13) & Chr(13)
	// abrir el archivo y volver a escribirlo con el encabezado mime, agregar los encabezados y el subjet
	
	//numArchivo.append("<html>"
	numArchivo.append("<body lang=ES style='tab-interval:35.4pt'>");
	numArchivo.append("<div class=Section1>");
	
	
	if ( !archivo_log.equals("") ) {
		 
	        FileReader fr = new FileReader(archivo_log);
	        numArchivo1 = new BufferedReader(fr);
	        while((ps_registro = numArchivo1.readLine())!=null) {
	        	numArchivo.append("<p class=MsoNormal>" + ps_registro + "</p>");
	        }
	        numArchivo1.close();
	}
	numArchivo.append("</div>");
	numArchivo.append("</body>");
	//numArchivo.append("</html>");
	try {
		numArchivo.close();
	} catch (IOException e) {
		e.printStackTrace();
	}
	
	commandline = commandline + " " + RutaLogAviso + " " + Dominio;
	
	if ( pBandera ) {
	    //Shell commandline // , vbHide
		System.out.println(commandline);
	}
	} else {
		System.out.println("Error al crear el archivo");
	}
	
	} catch (Exception e) {	
	System.out.println("****** ERROR EN PROCESO ENVIA MAIL PROCESOS DE CONFIRMACION DE PAGO SCHEDULER******");
	e.printStackTrace();
	}
		return null;
	}


	private int funSQLActEmailGenerado(String psFoli_set, String pdImp_pago, String psNoDocto, String psNoempresa, String psBenef,String psMail) {
		StringBuffer sb= new StringBuffer();
		int resultado=1;
		try{
		sb.append("UPDATE movimiento");
		sb.append("SET e_mail='"+psMail+"'");
		sb.append("WHERE no_folio_det="+ psFoli_set);
		sb.append("AND importe ="+pdImp_pago);
		sb.append("and no_docto = '" + psNoDocto + "'");
		sb.append("and id_forma_pago <> 7 ");
		sb.append("and no_empresa = '" + psNoempresa + "'");
		sb.append("and no_cliente = '" + psBenef + "'");
		}catch(Exception e){
			e.printStackTrace();
		}return resultado;
	}

	private List<Map<String, String>> funSQLDetallePagoAgrupado(String psNoDocto, String pdGrupo) { //ByVal psNoDocto As String, ByVal pdGrupo As Double
		StringBuffer sql= new StringBuffer();
		List<Map<String, String>> listConsMovi  = new ArrayList<Map<String, String>>();
		try{
			if(pdGrupo.equals("0")){
					sql.append("SELECT d.no_doc_sap,d.no_empresa,d.no_benef,d.imp_solic,d.no_factura,'' as causa_rech,d.contra_rec ");
					sql.append("\n FROM zimp_fact z , detalle_pago_as400 d");
					sql.append("\n where z.no_doc_sap = d.no_doc_sap");
					sql.append("\n and z.no_doc_sap = '" + psNoDocto + "'");
			}else{
					sql.append("SELECT  d.no_doc_sap,d.no_empresa,d.no_benef,d.imp_solic,d.no_factura,'' as causa_rech,d.contra_rec ");
					sql.append("\n FROM movimiento m, zimp_fact z, detalle_pago_as400 d");
					sql.append("\n WHERE m.no_docto = z.no_doc_sap");
					sql.append("\n AND z.no_doc_sap = d.no_doc_sap");
					sql.append("\n AND m.grupo_pago = " + pdGrupo);
					sql.append("\n AND   m.id_estatus_mov ='H'");
					sql.append("\n AND   m.id_tipo_operacion = 3201");
			}
			
			rs = (ResultSet) stmt.executeQuery(sql.toString());
			while (rs.next()){
			//listConsMovi = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, String>>(){
				//public Map<String, String> mapRow(ResultSet rs, int idx)throws SQLException{
					Map<String, String> map = new HashMap<String, String>();
					map.put("no_doc_sap", rs.getString("no_doc_sap"));
					map.put("no_empresa",  rs.getString("no_empresa"));
					map.put("no_benef",  rs.getString("no_benef"));
					map.put("imp_solic",  rs.getString("imp_solic"));
					map.put("no_factura",  rs.getString("no_factura"));
					map.put("causa_rech", rs.getString("causa_rech"));
					map.put("contra_rec",  rs.getString("contra_rec"));
					listConsMovi.add(map);
					//return map;
				//}
			//});
			}
		}catch(Exception e){
			e.printStackTrace();
		}return listConsMovi;
		
	}

	private List<Map<String, String>> funSQLDatosCorreo(String string) {
		
		StringBuffer sql= new StringBuffer();
		List<Map<String, String>> listConsMovi  = new ArrayList<Map<String, String>>();
		rs=null;
		try{
			
			sql.append("\n SELECT parrafo_1,  parrafo_2,  parrafo_3,  parrafo_4,  parrafo_5,");
			sql.append("\n parrafo_6,  parrafo_7,  parrafo_8,  parrafo_9,  parrafo_10,");
			sql.append("\n parrafo_11, parrafo_12, parrafo_13, parrafo_14, parrafo_15,");
			sql.append("\n parrafo_16, parrafo_17, parrafo_18, parrafo_19, parrafo_20,");
			sql.append("\n parrafo_21, parrafo_22, parrafo_23, parrafo_24, parrafo_25,");
			sql.append("\n parrafo_26");
			sql.append("\n From configura_mail");
			sql.append("\n WHERE no_correo = 1");
			System.out.println(sql.toString());
			//listConsMovi = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, String>>(){
				//public Map<String, String> mapRow(ResultSet rs, int idx)throws SQLException{
			rs = (ResultSet) stmt.executeQuery(sql.toString());
			while (rs.next()){
					Map<String, String> map = new HashMap<String, String>();
					map.put("parrafo_1", rs.getString("parrafo_1"));
					map.put("parrafo_2", rs.getString("parrafo_2"));
					map.put("parrafo_3", rs.getString("parrafo_3"));
					map.put("parrafo_4", rs.getString("parrafo_4"));
					map.put("parrafo_5", rs.getString("parrafo_5"));
					map.put("parrafo_6", rs.getString("parrafo_6"));
					map.put("parrafo_7", rs.getString("parrafo_7"));
					map.put("parrafo_8", rs.getString("parrafo_8"));
					map.put("parrafo_9", rs.getString("parrafo_9"));
					map.put("parrafo_10", rs.getString("parrafo_10"));
					map.put("parrafo_11", rs.getString("parrafo_11"));
					map.put("parrafo_12", rs.getString("parrafo_12"));
					map.put("parrafo_13",rs.getString("parrafo_13"));
					map.put("parrafo_14", rs.getString("parrafo_14"));
					map.put("parrafo_15", rs.getString("parrafo_15"));
					map.put("parrafo_16", rs.getString("parrafo_16"));
					map.put("parrafo_17", rs.getString("parrafo_17"));
					map.put("parrafo_18", rs.getString("parrafo_18"));
					map.put("parrafo_19", rs.getString("parrafo_19"));
					map.put("parrafo_20", rs.getString("parrafo_20"));
					map.put("parrafo_21", rs.getString("parrafo_21"));
					map.put("parrafo_22", rs.getString("parrafo_22"));
					map.put("parrafo_23", rs.getString("parrafo_23"));
					map.put("parrafo_24", rs.getString("parrafo_24"));
					map.put("parrafo_25",rs.getString("parrafo_25"));
					map.put("parrafo_26", rs.getString("parrafo_26"));
					listConsMovi.add(map);
			}
					//return map;
				//}
			//});
		}catch(Exception e){
			e.printStackTrace();
		}return listConsMovi;
	}

	private String quita_caracteres_raros(String cadena) {
		String salida = "";
		for (int i = 0; i < cadena.length(); i++) {
			salida += (cadena.toUpperCase().charAt(i) >= 'A' && cadena.toUpperCase().charAt(i) <= 'Z') ||
					Character.isDigit(cadena.charAt(i)) ? cadena.charAt(i) : " ";
		}
		return salida;
	}

	private List<Map<String, String>> funSQLSelectPagosEnviarCorreo() {
		StringBuffer sql= new StringBuffer();
		List<Map<String, String>> listConsMovi  = new ArrayList<Map<String, String>>();
		try{
			rs=null;
		   /*String driver = "oracle.jdbc.driver.OracleDriver";
		   String ulrjdbc = "jdbc:oracle:thin:C##PRUEBASET/PRUEBA_SET@localhost:1521:orcl";
		   Class.forName(driver).newInstance();
		   con = DriverManager.getConnection(ulrjdbc);
		   stmt = (Statement) con.createStatement();*/
		   
		   sql.append("SELECT m.no_docto, m.no_folio_det, m.no_cliente, m.no_empresa, m.importe, m.fec_valor, m.grupo_pago , m.id_chequera_benef");
			sql.append("\n  ,m.id_forma_pago,m.beneficiario,m.tipo_cambio,m.id_divisa,m.id_chequera, m.referencia ");
			sql.append("\n  ,(select desc_banco from cat_banco cb where cb.id_banco = m.id_banco) as desc_banco");
			sql.append("\n ,(select desc_banco from cat_banco cb2 where cb2.id_banco = m.id_banco_benef) as desc_banco_benef");
			sql.append("\n  ,(select nom_empresa from empresa e where e.no_empresa = m.no_empresa ) as nom_empresa");
			sql.append("\n  ,coalesce((select coalesce(contacto,'') from medios_contacto mc where mc.id_tipo_medio = 'MAIL' and mc.no_persona = m.no_cliente ) ,'') as email");
			sql.append("\n ,(select equivale_persona from persona p where p.no_persona = m.no_cliente and id_tipo_persona = 'P') AS equivale_persona ");
			sql.append("\n FROM movimiento m");
			sql.append("\n  where m.id_estatus_mov IN('K','T')");
			sql.append("\n AND m.id_tipo_operacion = 3200");
			sql.append("\n and coalesce(m.e_mail,' ') =' '");
			//sql.append("\n and m.origen_mov = 'AS4'");
			//sql.append("\n AND COALESCE(b_gen_conta,'')= 'S'");
			//sql.append("\n and m.fec_valor >=(select fec_hoy-10  from fechas) ");
			sql.append("\n and importe <> 0");
			System.out.println(sql.toString());
			//listConsMovi = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, String>>(){
				//public Map<String, String> mapRow(ResultSet rs, int idx)throws SQLException{
			rs = (ResultSet) stmt.executeQuery(sql.toString());
			while (rs.next()){
					Map<String, String> map = new HashMap<String, String>();
					map.put("email", rs.getString("email"));
					map.put("no_docto", rs.getString("no_docto"));
					map.put("no_folio_det", rs.getString("no_folio_det"));
					map.put("no_cliente", rs.getString("no_cliente"));
					map.put("no_Empresa", rs.getString("no_Empresa"));
					map.put("importe", rs.getString("importe"));
					map.put("nom_empresa", rs.getString("nom_empresa"));
					map.put("fec_valor", rs.getString("fec_valor"));
					map.put("grupo_pago", rs.getString("grupo_pago"));
					map.put("id_chequera_benef", rs.getString("id_chequera_benef"));
					map.put("desc_banco_benef", rs.getString("desc_banco_benef"));
					map.put("id_forma_pago", rs.getString("id_forma_pago"));
					map.put("beneficiario", rs.getString("beneficiario"));
					map.put("tipo_cambio", rs.getString("tipo_cambio"));
					map.put("id_divisa", rs.getString("id_divisa"));
					map.put("id_chequera", rs.getString("id_chequera"));
					map.put("referencia", rs.getString("referencia"));
					map.put("desc_banco", rs.getString("desc_banco"));
					map.put("equivale_persona", rs.getString("equivale_persona"));
					//return map;
					listConsMovi.add(map);
			//	}
			//});
			}
		}catch(Exception e){
			e.printStackTrace();
		}return listConsMovi;
	}

}