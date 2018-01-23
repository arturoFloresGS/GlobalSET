package com.webset.set.utilerias;

import java.io.File;

import javax.servlet.ServletContext;

import com.softwarementors.extjs.djn.servlet.ssm.WebContext;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;

public final class ConstantesSet {
	
	//BANCOS NACIONALES
	public static final int BANAMEX = 2;
	public static final int BANCOMER = 12;
	public static final int SANTANDER = 14;
	public static final int BITAL = 21;
	public static final int INBURSA = 36;
	public static final int BAJIO = 30;
	public static final int BITAL_DLS = 1229;
	public static final int AMRO_BANK = 102;
	public static final int INVERLAT = 44; 
	public static final int AZTECA = 127;
	public static final int BANORTE = 72;
	public static final int BANCRECER = 161;
	public static final int CITIBANK_MN = 7;
	
	//BANCOS Extranjeros
	public static final int BANK_BOSTON = 107;
	public static final int COMERICA = 1260;
	public static final int CITIBANK_DLS = 1026;
	public static final int FUBI = 1234; //'FIRST UNION NATIONAL BANK
	public static final int AMEX = 1980; //'AMERICAN EXPRESS
	public static final int NAFIN = 135; //'NACIONAL FINANCIERA
	public static final int BOFA = 1027; //'BANK OF AMERICA
	public static final int CHASE_MANHATTAN = 1028;

	public static final int CONF_GUARDAR_DCOM = 222;  //'indice de configura_set para guardar archivos
	
	public static final String gsDBM = "SQL SERVER";
	
	public static final String PRIMERA_MONEDA = "MN";
	public static final String SEGUNDA_MONEDA = "DLS";
	
	public static final String UBICACION = "CCM";

	//Ruta para graficas. Se debe actualizar dependiendo a la ruta de instalacion de Glassfish.
	public static final String RUTA = RUTA_APP("/graficaSet/");
	
	//Ruta que contendra los cheques y el sh que mandara a imprimir el cheque.
	public static final String RUTA_CHEQUES = "C:\\webset\\cheques\\";
	
	//Ruta para guardar los archivos Excel
	public static final String RUTA_EXCELA = RUTA_APP("/WEB-INF/archivos_excel/");// + 
				//"\\WEB-INF\\archivos_excel\\";
	public static final String RUTA_EXCEL = "c:\\archivos_excel\\";
	
	//Secuencia de escape que da forma a la Poliza de Cheque.
	public static final String FORMA_POLIZA_CHEQUE = ">E>&l12d132p132f0l4E>&f3700Y "
			+ ">&f2X>&f0X>&l0O>(0U>(s1p    1h 12v0s+0b 4T>"
			+ "*p900x80YPOLIZA CHEQUE>*p5x   100Y>*c 2400 a "
			+ "2 b0P>*p5x   270Y>*c 2400 a 2 b0P>*p5x   430Y>*c "
			+ "2400 a 2 b0P>*p5x   620Y>*c 2400 a 2 b0P>*p5x   "
			+ "800Y>*c 2400 a 2 b0P>*p430x430Y>*c 2 a 370 b0P>*"
			+ "p770x430Y>*c 2 a 190 b0P>*p1110x430Y>*c 2 a 570 b0P>"
			+ "*p1530x430Y>*c 2 a 570 b0P>*p1950x430Y>*c 2 a 190 b0P>"
			+ "*p1950x800Y>*c 2 a 205 b0P>*p650x800Y>*c 2 a 205 b0P>*"
			+ "p5x   100Y>*c 2 a 1550 b0P>*p 430x100Y>*c 2 a 170 b0P>*"
			+ "p2396x 100Y>*c 2 a 1695 b0P>*p5x   1000Y>*c 2400 a 2 b0P>*"
			+ "p5x   1070Y>*c 2400 a 2 b0P>*p5x   1140Y>*c 2400 a 2 b0P>*"
			+ "p5x   1330Y>*c 2400 a 2 b0P>*p5x   1500Y>*c 2400 a 2 b0P>*"
			+ "p430x1140Y>*c 2 a 360 b0P>*p770x1070Y>*c 2 a 260 b0P>*"
			+ "p1110x1140Y>*c 2 a 510 b0P>*p1530x1140Y>*c 2 a 510 b0P>*"
			+ "p1950x1140Y>*c 2 a 190 b0P>*p1950x1500Y>*c 2 a 150 b0P>*"
			+ "p650x1500Y>*c 2 a 150 b0P>&l0O>(0U>(s1p    1h 8v0s+0b 4T>*"
			+ "p1000x1050YACUSE DE RECIBO>*p5x   1650Y>*c 2400 a 2 b0P>*"
			+ "p700x 1800Y>*c 480 a 2 b0P>*p700x 1800Y>*c 480 a 2 b0P>*"
			+ "p1300x1800Y>*c 480 a 2 b0P>*p5x   1850Y>*c 1860 a 2 b0P>*"
			+ "p5x   1900Y>*c 1860 a 2 b0P>*p5x   2050Y>*c 2400 a 2 b0P>*"
			+ "p5x   1650Y>*c 2 a 400 b0P>*p2396x1650Y>*c 2 a 400 b0P>*"
			+ "p1865x1650Y>*c 2 a 400 b0P>*p530x1850Y>*c 2 a 200 b0P>*"
			+ "p1020x1900Y>*c 2 a 150 b0P>*p1500x1900Y>*c 2 a 150 b0P>&"
			+ "l0O>(0U>(s1p    1h  6v0s+0b 4T>*p25x130YNO. PROVEEDOR:>*"
			+ "p450x130YBENEFICIARIO:>*p25x300YPAGO CONCEPTO DE:>*"
			+ "p25x470YR.F.C.:>*p450x470YMONEDA PAGO:>*p790x470YMONEDA "
			+ "ORIGINAL:>*p1130x470YTIPO DE CAMBIO:>*p1550x470YNO. "
			+ "SOCIEDAD:>*p1970x470YIMPORTE PAGO:>*p25x660YFECHA CHEQUE:>"
			+ "*p450x660YBANCO:>*p1130x660YNO. CUENTA:>*p1550x660YSOCIEDAD"
			+ ":>*p25x840YTIPO DE EGRESO (RUBRO):>*p670x840YNO. CHEQUE:>*"
			+ "p1130x840YSUCURSAL:>*p1550x840YCONTRARECIBO:>*p1970x840YFOLIO "
			+ "TESORERIA SET:>*p25x1120YNO. PROVEEDOR:>*p790x1120YBENEFICIARIO"
			+ ":>*p25x1180YR.F.C.:>*p450x1180YMONEDA PAGO:>*p790x1180YMONEDA "
			+ "ORIGINAL:>*p1130x1180YTIPO DE CAMBIO:>*p1550x1180YNO. SOCIEDAD:"
			+ ">*p1970x1180YIMPORTE PAGO:>*p25x1380YFECHA CHEQUE:>*p450x1380YBANCO:"
			+ ">*p1130x1380YNO. CUENTA:>*p1550x1380YSOCIEDAD:>*p25x1560YTIPO DE "
			+ "EGRESO (RUBRO):>*p670x1560YNO. CHEQUE:>*p1130x1560YSUCURSAL:>*"
			+ "p1550x1560YCONTRARECIBO:>*p1970x1560YFOLIO TESORERIA SET:>*"
			+ "p25x1690YRECIBE CHEQUE:>*p1880x1690YFIRMA DE RECIBIDO:>*"
			+ "p200x1830YNOMBRE>*p800x1830YAPELLIDO PATERNO>*p1400x1830YAPELLIDO "
			+ "MATERNO>*p130x1880YFECHA DE ENTREGA:>*p1100x1880YIDENTIFICACION "
			+ "PRESENTADA:>*p550x1940YCREDENCIAL IFE:>*p1040x1940YLICENCIA:>*"
			+ "p1520x1940YPASAPORTE:>*p80x2030YDIA>*p240x2030YMES>*p400x2030YANIO>"
			+ "&l0O>(0U>(s1p    1h  8v0s+0b 4T>*p1550x2300YNumero de Cheque:>"
			+ "&l0O>(0U>(s1p    1h  6v0s+0b 4T>*p600x  2850Y CUENTA:>*p930x2850Y "
			+ "SUC:>&f1X>&f10X  ";
	
	
	public static final String FORMA_CHEQUE_WEB = ">%-12345X >E >*p1860x5Y>*c530a2b0P >*"
			+"p5x180Y>*c2400a2b0P >*p5x480Y>*c2400a2b0P >*"
			+"p5x180Y>*c2a1520b0P >*p430x180Y>*c2a300b0P >*"
			+"p1860x5Y>*c2a479b0P >*p2335x5Y>*c2a1695b0P >*"
			+"p5x980Y>*c2400a2b0P >*p5x1200Y>*c2400a2b0P >*"
			+"p5x1400Y>*c2400a2b0P >*p5x1600Y>*c2400a2b0P >*"
			+"p430x980Y>*c2a420b0P >*p770x980Y>*c2a220b0P >*"
			+"p1110x980Y>*c2a620b0P >*p1530x980Y>*c2a620b0P >*"
			+"p1950x980Y>*c2a220b0P >*p1950x1400Y>*c2a205b0P >*"
			+"p650x1400Y>*c2a205b0P >*p100x1800Y>*c480a2b0P >*"
			+"p700x1800Y>*c480a2b0P >*p1300x1800Y>*c480a2b0P >*"
			+"p5x1850Y>*c1860a2b0P >*p5x1900Y>*c1860a2b0P >*"
			+"p5x2050Y>*c2400a2b0P >*p5x1650Y>*c2a400b0P >*"
			+"p2335x1650Y>*c2a400b0P >*p1865x1650Y>*c2a400b0P >*"
			+"p530x1850Y>*c2a200b0P >*p1020x1900Y>*c2a150b0P >*"
			+"p1500x1900Y>*c2a150b0P >*p5x1650Y>*"
			+"c2400a2b0P >(s1p1h12v0s+0b4T >*p900x80YCOMPROBANTE >*"
			+"p1320x80YCONTABLE >(s1p1h8v0s+0b4T >*p1000x1640YACUSE >*"
			+"p1120x1640YDE >*p1180x1640YRECIBO >(s1p1h6v0s+0b4T >*p25x220YNO&#255;PROVEEDOR >*"
			+"p450x220YBENEFICIARIO: >*p450x350YBENEFICIARIO&#255;ALTERNO: >*"
			+"p1880x45YNO&#255;CHEQUE: >*p1880x225YIMPORTE&#255;PAGO: >*"
			+"p25x525YPAGO&#255;CONCEPTO&#255;DE: >*p25x1020YRFC: >*"
			+"p450x1020YMONEDA&#255;PAGO: >*p790x1020YMONEDA&#255;ORIGINAL: >*"
			+"p1130x1020YTIPO&#255;DE&#255;CAMBIO: >*p1550x1020YNO&#255;SOCIEDAD: >*"
			+"p1970x1020YIMPORTE&#255;PAGO: >*p25x1240YFECHA&#255;CHEQUE: >*p450x1240YBANCO: >*"
			+"p1130x1240YNO&#255;CUENTA: >*p1550x1240YSOCIEDAD: >*"
			+"p25x1440YTIPO&#255;DE&#255;EGRESO(RUBRO): >*p670x1440YNO&#255;CHEQUE: >*"
			+"p1130x1440YCAJA&#255;DE&#255;PAGO: >*p1550x1440YCONTRARECIBO: >*"
			+"p1970x1440YFOLIO&#255;TESORERIA&#255;SET: >*p25x1690YRECIBE&#255;CHEQUE: >*"
			+"p1880x1690YFIRMA&#255;DE&#255;RECIBIDO: >*p200x1830YNOMBRE >*"
			+"p800x1830YAPELLIDO&#255;PATERNO >*p1400x1830YAPELLIDO&#255;MATERNO >*"
			+"p130x1880YFECHA&#255;DE&#255;ENTREGA: >*p1100x1880YIDENTIFICACION&#255;PRESENTADA: >*"
			+"p550x1940YCREDENCIAL&#255;IFE: >*p1040x1940YLICENCIA: >*p1520x1940YPASAPORTE: >*"
			+"p80x2030YDIA >*p240x2030YMES >*p400x2030YANIO >*p181x2980YCUENTA: >*p550x2980YSUC:";
	
	//contantes para el manejo de carpetas de bancos
	
	//BANCOS NACIONALES
	
	public static final int USUARIO_SERVIDOR = 3100;
	public static final int PASSWORD_SERVIDOR = 3101;
	public static final int IP_CARPETA_DESTINO = 3102;
	public static final int CARPETA_RAIZ = 3103;
	public static final int NOMBRE_CARPETA_BANAMEX = 3109;
	public static final int NOMBRE_CARPETA_BANAMEX_TEF = 3108;
	public static final int NOMBRE_CARPETA_BANCOMER = 3105;
	public static final int NOMBRE_CARPETA_BANCOMER_INTERNACIONAL = 3106;
	public static final int NOMBRE_CARPETA_BANCOMER_CIE = 3107;
	public static final int NOMBRE_CARPETA_AZTECA = 3104;
	public static final int NOMBRE_CARPETA_BANAMEX_FLT = 3110;
	public static final int NOMBRE_CARPETA_CITIBANCK = 3111;
	public static final int NOMBRE_CARPETA_BANAMEX_PL = 3112;
	public static final int NOMBRE_CARPETA_SANTANDER = 3113;
	public static final int NOMBRE_CARPETA_SANTANDER_2H2 = 3117;

	public static final int NOMBRE_CARPETA_BANORTE = 3114;
	public static final int NOMBRE_CARPETA_SCOTIABANK_INVERLAT = 3115;

	//public static final String CARPETA_RAIZ_RESPALDO = RUTA_APP("/WEB-INF/banca_electronica_res/");//banca_electronica_res
	public static final String CARPETA_RAIZ_RESPALDO = "C:\\transferencias\\";
//	public static final String CARPETA_RAIZ_CHEQUE_OCURRE = RUTA_APP("/WEB-INF/cheque_ocurre/");//banca_electronica_res 
	public static final String CARPETA_RAIZ_CHEQUE_OCURRE ="C:\\cheque_ocurre\\";//banca_electronica_res 

	// constantes para el manejo de carpetas en el modulo de traspasos para Masspayment

	public static final int USUARIO_SERVIDOR_MASSPAYMENT = 3120;
	public static final int PASSWORD_SERVIDOR_MASSPAYMENT = 3121;
	public static final int IP_CARPETA_DESTINO_MASSPAYMENT = 3122;
	public static final int CARPETA_RAIZ_MASSPAYMENT = 3123;
	
	// CONSTANTES PARA LA AUTENTICACION DE LOS WEBSERVICE
	
	public static final int USERNAME_WS_POLIZAS = 3200;
	public static final int PASSWORD_WS_POLIZAS = 3201;
	
	public static final int USERNAME_WS_PAGOS = 3202;
	public static final int PASSWORD_WS_PAGOS = 3203;
	public static final int USERNAME_WS_CANCELACION = 3204;
	public static final int PASSWORD_WS_CANCELACION = 3205;
	public static final String CARPETA_PDF = RUTA_APP("/WEB-INF/archivosPDF/");
	public static final int USERNAME_WS_CONTABILIDAD = 3206;
	public static final int PASSWORD_WS_CONTABILIDAD = 3207;

	//Constantes para enviar correo
	
	public static final String CORREO_SALIDA="pruebascorreo2016@gmail.com";
	public static final String CONTRASENIA="pruebasCorreo_2016";
	public static final String PUERTO_CORREO="587";
	public static final String HOST_CORREO="smtp.gmail.com";
	public static final String CORREO_RECEPCION_DEFECTO="yoscardenas@gmail.com";
	public static final int PASSWORD_WS_IMPRESION = 3208;
	public static final int USERNAME_WS_IMPRESION = 3209;
	
	public static final String FORMA_CHEQUE_WEB_HP = "@@%-12345X@@E@@*p5x250Y@@*c2400a2b0P@@*p5x550Y@@*c2400a2b0P@@*"
		+"p5x250Y@@*c2a1480b0P@@*p430x250Y@@*c2a300b0P@@*p1703x250Y@@*c2a305b0P@@*p2396x250Y@@*c2a1480b0P@@*"
		+"p5x980Y@@*c2400a2b0P@@*p5x1200Y@@*c2400a2b0P@@*p5x1400Y@@*c2400a2b0P@@*p5x1600Y@@*c2400a2b0P@@*"
		+"p430x980Y@@*c2a420b0P@@*p770x980Y@@*c2a220b0P@@*p1110x980Y@@*c2a620b0P@@*p1530x980Y@@*c2a620b0P@@*"
		+"p1950x980Y@@*c2a220b0P@@*p1950x1400Y@@*c2a205b0P@@*p650x1400Y@@*c2a205b0P@@*p100x1800Y@@*c480a2b0P@@*"
		+"p700x1800Y@@*c480a2b0P@@*p1300x1800Y@@*c480a2b0P@@*p5x1850Y@@*c1860a2b0P@@*p5x1900Y@@*c1860a2b0P@@*"
		+"p5x2030Y@@*c2400a2b0P@@*p5x1650Y@@*c2a380b0P@@*p2396x1650Y@@*c2a380b0P@@*p1865x1650Y@@*c2a380b0P@@*"
		+"p530x1850Y@@*c2a180b0P@@*p1020x1900Y@@*c2a130b0P@@*p1500x1900Y@@*c2a130b0P@@*p5x1650Y@@*c2400a2b0P@@"
		+"(s1p1h12v0s+0b4T@@(s1p1h8v0s+0b4T@@*p1000x1640YACUSE@@*p1120x1640YDE@@*p1180x1640YRECIBO@@(s1p1h6v0s+0b4T@@*"
		+"p25x295YNO PROVEEDOR@@*p450x295YBENEFICIARIO:@@*p450x425YBENEFICIARIO ALTERNO:@@*p1750x295YIMPORTE PAGO:@@*"
		+"p25x595YPAGO CONCEPTO DE:@@*p25x1020YRFC:@@*p450x1020YMONEDA PAGO:@@*p790x1020YMONEDA ORIGINAL:@@*"
		+"p1130x1020YTIPO DE CAMBIO:@@*p1550x1020YNO EMPRESA:@@*p1970x1020YIMPORTE PAGO:@@*p25x1240YFECHA CHEQUE:@@*"
		+"p450x1240YBANCO:@@*p1130x1240YNO CUENTA:@@*p1550x1240YEMPRESA:@@*p25x1440YTIPO DE EGRESO(RUBRO):@@*"
		+"p670x1440YNO CHEQUE:@@*p1130x1440YCAJA DE PAGO:@@*p1550x1440YDOCUMENTO:@@*p1970x1440YFOLIO TESORERIA SET:@@*"
		+"p25x1690YRECIBE CHEQUE:@@*p1880x1690YFIRMA DE RECIBIDO:@@*p200x1830YNOMBRE@@*p800x1830YAPELLIDO PATERNO@@*"
		+"p1400x1830YAPELLIDO MATERNO@@*p130x1880YFECHA DE ENTREGA:@@*p1100x1880YIDENTIFICACION PRESENTADA:@@*"
		+"p550x1940YCREDENCIAL IFE:@@*p1040x1940YLICENCIA:@@*p1520x1940YPASAPORTE:@@*p80x2020YDIA@@*p240x2020Y MES@@*p400x2020YANIO@@"
		+"(s1p1h10v0s+0b4T@@*p750x2840YCUENTA:@@*p750x2890YSUC:@@*p750x2940YCHEQUE:";
	
	public static String RUTA_APP(String ruta) {
		WebContext context = WebContextManager.get();
        ServletContext contexto = context.getServletContext();
        return contexto.getRealPath(ruta) + File.separator;
	}
}
