package com.webset.set.utilerias.dto;
/**
 * @autor Miguel Silva 
 *  Basada en la clase original de Funciones.java, solo que ahora con las fechas en sql.date
 * @since 31/05/2017
 */

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
//import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Date;
import java.util.GregorianCalendar;
//import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.codec.binary.Hex;

import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.ConvertirNumeroEnLetra;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public class FuncionesSql {

	Bitacora bitacora = new Bitacora();

	final String miCorreo = "pruebas@gmail.com";
	final String miContrasena = "contraseña";
	final String servidorSMTP = "smtp.gmail.com";
	final String puertoEnvio = "465";

	/**
	 * 
	 * @param fecha
	 * @param hms
	 * @return boolean
	 * 
	 *         Verifica que sea una fecha valida ademas de que si la bandera hms
	 *         es igual a false no contiene horas minutos y segundos y si true
	 *         viceversa
	 * 
	 */
	//MS: la idea es recibir un date y regresar un string en el formato de la base de datos necesario
	public static String ponFechaDMY(Date fecha, boolean hms) {
		String sFecha;
		SimpleDateFormat formatoDeFecha = null;
		
		if(ConstantesSet.gsDBM.equals("DB2")){
			if (hms)
				formatoDeFecha = new SimpleDateFormat("yyyy-MM-dd-HH:mm:00");
			else
				formatoDeFecha = new SimpleDateFormat("yyyy-MM-dd");
			formatoDeFecha.setLenient(false);
		} else {// if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE") ||
				// || ConstantesSet.gsDBM.equals("ORACLE")) || ConstantesSet.gsDBM.equals("POSTGRESQL")
			if (hms)
				formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			else
				formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
			formatoDeFecha.setLenient(false);
		}
		try {
			sFecha = formatoDeFecha.format(fecha);
			return sFecha;
		} catch (Exception e) {
			/*bitacora.insertarRegistro(new Date().toString()
					+ " "
					+ Bitacora.getStackTrace(e)
					+ "P:utilerias, C:Funciones, M:isDate");*/
			return "";
		}
	}
	
	
	

	public static String ponFechaDMY(String fecha, boolean hms) {
		String sFecha;
		SimpleDateFormat formatoDeFecha = null;
		
		if(ConstantesSet.gsDBM.equals("DB2")){
			if (hms)
				formatoDeFecha = new SimpleDateFormat("yyyy-MM-dd-HH:mm:00");
			else
				formatoDeFecha = new SimpleDateFormat("yyyy-MM-dd");
			formatoDeFecha.setLenient(false);
		} else {// if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE") ||
				// || ConstantesSet.gsDBM.equals("ORACLE")) || ConstantesSet.gsDBM.equals("POSTGRESQL")
			if (hms)
				formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			else
				formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
			formatoDeFecha.setLenient(false);
		}
		try {
			sFecha = formatoDeFecha.format(fecha);
			return sFecha;
		} catch (Exception e) {
			/*bitacora.insertarRegistro(new Date().toString()
					+ " "
					+ Bitacora.getStackTrace(e)
					+ "P:utilerias, C:Funciones, M:isDate");*/
			return "";
		}
	} 
	
	// MS: esta funcion valida que tenga un formato que se pueda evaluar como fecha e indica si tiene hora o no
	/*
	public boolean isDate(String fecha, boolean hms) {
		SimpleDateFormat formatoDeFecha = null;
		if (hms)
			formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		else
			formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		formatoDeFecha.setLenient(false);
		try {
			fecha = formatoDeFecha.format(formatoDeFecha.parse(fecha));
			return true;
		} catch (ParseException e) {
			bitacora.insertarRegistro(new Date().toString()
					+ " "
					+ Bitacora.getStackTrace(e)
					+ "P:utilerias, C:Funciones, M:isDate");
			return false;
		}
	}
*/
	//Método que cambia de formato de String DD/MM/yyyy a yyyy/MM/DD
	// MS: solo se usa en modificacion por bloque
	public String cambiarOrdenFecha(String fecha) throws ParseException {
		
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date date = (Date) formatter.parse(fecha);
		SimpleDateFormat newFormat = new SimpleDateFormat("yyyy/MM/dd");
		String finalString = newFormat.format(date);
		return finalString;
	}

	/**
	 * 
	 * @param fecha
	 *            String
	 * @return String
	 * 
	 * Cambia un string a formato fecha
	 * 
	 */
	public String ponerFecha(String fecha) {
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat(
				"dd/MM/yyyy HH:mm:ss");
		formatoDeFecha.setLenient(false);
		try {
			return formatoDeFecha.format(formatoDeFecha.parse(fecha));
		} catch (ParseException e) {
			System.out.println(e.toString());
			return null;
		}
	}
	/**
	 * @param fechaString
	 * @return Este metodo retorna un Date con formato dd/MM/yyyy
	 * MS: Revisar su uso en la mayoria convierten un variable a string y esta la regresa a date
	 * me parece que no es necesario tanto cambio si la variable es date 
	 */
	
	public static Date ponerFechaDate(String fechaString)
	{
		SimpleDateFormat dateFecha=new SimpleDateFormat("dd/MM/yyyy");
		try{
			Date fechaRet;
			java.util.Date date = dateFecha.parse(fechaString);
			fechaRet = new Date(date.getTime());
			return fechaRet; 
		}catch (ParseException e) {
			System.out.println(e.toString());
		return null;
		}
	}
	/**
	 * @param fechaString
	 * @return Este metodo retorna un Date con formato dd-MMM-yyyy H:mm:ss
	 */
	/*
	 //MS: la comente no se usa
	@SuppressWarnings("unused")
	public Date ponerFechaDate2(String fechaString)
	{
		SimpleDateFormat dateFecha=new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
		try{
			Date fechaRet;
			return fechaRet = dateFecha.parse(fechaString);
		}catch (ParseException e) {
			System.out.println(e.toString());
		return null;
		}
	}
*/
	/**
	 * @param fechaString
	 * @return Este metodo retorna un Date con formato dd-MMM-yyyy H:mm:ss
	 */
	// MS: solo se usa en inversiones y conciliaciones
/*
	@SuppressWarnings("unused")
	public Date ponerFechaDate3(String fechaString)
	{
		System.out.println("La fecha para formatear es "+ fechaString);
		SimpleDateFormat dateFecha=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		try{
			Date fechaRet;
			return fechaRet = dateFecha.parse(fechaString);
		}catch (ParseException e) {
			System.out.println(e.toString());
		return null;
		}
	}
*/
	/**
	 * 
	 * @param fecha
	 *            Date
	 * @return String para consultas
	 * 
	 */
	// MS: mismo que que las de arrbia recibe unn date le da formato y lo regresa en un string para meterlo a un date
	public String ponerFecha(Date fecha) {
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat(
				"dd/MM/yyyy HH:mm:ss");
		formatoDeFecha.setLenient(false);
		return formatoDeFecha.format(fecha);
	}

	/**
	 * 
	 * @param fecha
	 * @return String
	 * 
	 * Retorna la fecha string formato dd/mm/yyyy
	 */
	// MS: se usa en los dao para darle formato a un string con formato fecha
	// es mejor recibir un date y darle formato
	public String ponerFechaSola(String fecha) {
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		formatoDeFecha.setLenient(false);
		try {
			return formatoDeFecha.format(formatoDeFecha.parse(fecha));
		} catch (ParseException e) {
			System.out.println(e.toString());
			return null;
		}
	}
/*
 	// MS: esta funcion no se usa la comente
	public Date ConvertDate(Date date) {

		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		String s = df.format(date);
		String result = s;
		try {
			date = df.parse(result);
		} catch (ParseException e) {

			e.printStackTrace();
		}
		return date;
	}
*/
	/**
	 * 
	 * @param fecha
	 * @return String
	 * 
	 * Retorna la fecha string formato dd/mm/yyyy
	 */
	// MS: esta funcion esta sobre cargada hay que revisar
	public String ponerFechaSola(Date fecha) {
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		formatoDeFecha.setLenient(false);
		return formatoDeFecha.format(fecha);
	}

	public String ponerFormatoDate(Date fecha) {
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		return formatoDeFecha.format(fecha);
	}

	/**
	 * 
	 * @param fecha
	 * @return String 
	 * 
	 * Cambia la fecha de formato yyyy-mm-dd 00:00:00 a dd-mm-yyyy 00:00:00
	 */
	// MS: esta se debe revisar para ver por que maneja este formato
	public String cambiarFecha(String fecha){
		return fecha.substring(8,10)+"/"+fecha.substring(5,7)+"/"+fecha.substring(0,4)+fecha.substring(10);
	}
	
	/**
	 * 
	 * @param fecha
	 * @param ban
	 * @return String 
	 * 
	 * Cambia la fecha de formato yyyy-mm-dd 00:00:00 a dd-mm-yyyy
	 */
	public String cambiarFecha(String fecha, boolean ban){
		if(ban)
			return fecha.substring(8,10)+"/"+fecha.substring(5,7)+"/"+fecha.substring(0,4);
		else
			return fecha.substring(8,10)+"/"+fecha.substring(5,7)+"/"+fecha.substring(0,4)+fecha.substring(10);
	}
	/**
	 * @param cadena
	 * @param cant
	 * @return String pone ceros a la izquierda recibiendo la cadena y el tamano
	 *         de cadena que se quiere tener
	 */
	public String ponerCeros(String cadena, int cant) {
		String regla = "";
		for (int i = 0; i < cant; i++)
			regla += "0";
		if (regla.length() > cadena.length())
			cadena = regla.substring(0, regla.length() - cadena.length())
					.concat(cadena);
		return cadena;
	}
	/**
	 * Metodo para verificar si una cadena es de solo numeros
	 * @param cadena
	 * @return true si es numerica de lo contrario false
	 */
	public boolean isNumeric(String cadena){
		return (cadena.matches("[0-9]+"));
	}
/**
 * compara una cadena de la A a la Z maynsculas
 * @param cadena
 * @return true si encontro mayusculas
 */
	public boolean comparaMayusculas(String cadena)
	{
		return(cadena.matches("[A-Z]+"));		
	}
	/**
	*
	* @param cad
	* @param cant
	* @return String
	*
	* Retorna los caracteres a la derecha
	*/
	public String cadenaRight(String cad,int cant){
		String newCad="";
		if(cad.trim().length()>=cant)
		newCad=cad.substring(cad.trim().length()-cant);
		return newCad;
	}
	
/**
 * Calcula el digito verificador de la referencia en Base 10 "b10"
 * Public Function calculaDigito(psReferencia As String) As Byte
 * @param psReferencia
 * @return digito verificador
 */
	public char calcularDigito(String psReferencia)
	{
		int i=0;
	    int lSuma=0;
	    int iResiduo=0;
	    int calcu=0;
	    char calculaDigito=0;
	    String sSuma="";

	    for(i=0;i==psReferencia.trim().length();i++)
	    {
	        if(i%2==0)
	            lSuma =Integer.parseInt(psReferencia.substring(i,i+1))*1;
	        else
	            lSuma =Integer.parseInt(psReferencia.substring(i,i+1))*2;

            sSuma +=""+lSuma;
	    }

	    lSuma = 0;
	    for(i=0;i==sSuma.trim().length();i++)
	        lSuma = lSuma + Integer.parseInt(sSuma.substring(i,i+1));

	    iResiduo = lSuma % 10;
	    if (iResiduo != 0)
	        calcu =10 - iResiduo;
	    else
	        calcu= 0;
	    calculaDigito=(char)calcu;
	    return calculaDigito;
	}
/*
	public int[] retornarNumeros(String claves, String separador){
		int vec[]=null;
		try{
			String aux=claves;
			int j=0;
			while(claves.indexOf(separador)>-1){
				claves = claves.substring(claves.indexOf(separador)+1);
				j++;
			}
			vec = new int[j];
			j=0;
			while(aux.indexOf(separador)>-1){
				claves = aux.substring(0,aux.indexOf(separador));
				vec[j]=Integer.parseInt(claves);
				aux=aux.substring(aux.indexOf(separador)+1);
				j++;
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Utilerias, C:FuncionesDao, M:retornarNumeros");
		}
		return vec;
	}
*/
	/**
	 * Este metodo recibe un valor double para formatearlo con 
	 * ceros a la izquierda y dos decimales, utilizado normalmente 
	 * para cadenas establecidas en layouts
	 * @param valor cantidad de importe, iva, etc.
	 * @param cant
	 * @return retorna formato con la cantidad de ceros en el valor cant, mas
	 * el punto y los dos ceros decimales
	 */
/*
	public String ponerFormatoCeros(double valor, int cant) {
		try{
			String regla="";
			for (int i = 0; i < cant; i++)
				regla += "0";
			
			//NumberFormat formatter = new DecimalFormat("#0.00"); 
			NumberFormat formato = new DecimalFormat(regla+".00"); 
			
			return formato.format(valor);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Utilerias, C:FuncionesDao, M:ponerFormatoCeros");
			return"";
		}
	}
*/
	/**
	 * Este metodo recibe un valor double para formatearlo con 
	 * ceros a la izquierda y dos decimales, utilizado normalmente 
	 * para cadenas establecidas en layouts
	 * @param valor cantidad de importe, iva, etc.
	 * @param cant
	 * @return retorna formato con la cantidad de ceros en el valor cant, sin
	 * el punto y los dos ceros decimales
	 */
/*
	public String ponerFormatoCerosSindecimales(double valor, int cant) {
		try{
			String regla="";
			for (int i = 0; i < cant; i++)
				regla += "0";
			 
			NumberFormat formato = new DecimalFormat(regla); 
			
			return formato.format(valor);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Utilerias, C:FuncionesDao, M:ponerFormatoCeros");
			return"";
		}
	}
*/
	/**
	 * funcion que quita las n y vocales con acento
	 * @param cadena
	 * @param enes
	 * @return psResultado
	 */
/*
	public String quitarCaracteresRaros(String cadena, boolean enes){
	    int i=0;
		String psResultado = "";
	    try
	    {
	    	if(enes)
	    	{
//		        'Cambiar n por N y vocales con acento por vocales sin acento
	    		cadena = cadena.toUpperCase();
	    		cadena = cadena.replaceAll("n", "N");
		        cadena = cadena.replaceAll("n", "A");
		        cadena = cadena.replaceAll("n", "E");
		        cadena = cadena.replaceAll("n", "I");
		        cadena = cadena.replaceAll("n", "O");
		        cadena = cadena.replaceAll("n", "U");
	    	}
		
	    	if(!cadena.equals(""))
	    	{
	    		for(i = 0; i < cadena.length(); i++)
	    		{
	    			if(comparaMayusculas(cadena.toUpperCase().substring(i, i+1)) || isNumeric(cadena.substring(i, i+1)))
		                psResultado = psResultado + cadena.substring(i, i+1);
		            else
		                psResultado = psResultado + " ";
	    		}
	    	}
		    return psResultado;
	    }catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Utilerias, C:FuncionesDao, M:quitarCaracteresRaros");
			return"";
		}
	}
*/
	/**
	 * 
	 * @param campo
	 * @param longMax
	 * @param alineacion
	 * @param separador
	 * @param complemento
	 * @return ajustarLongCampo
	 */
/*
	public String ajustarLongitudCampo(String campo, int longMax, String alineacion, String separador, String complemento){
//	'Esta funcion es para Ajustar el campo a la posicion que especifica el Layout
		try{
			String ajustarLongCampo = "";
			String espacios="";
		    campo = campo.trim();
		    
		    if(longMax == 0)
		    {
		    	ajustarLongCampo = campo.trim();
		    }
		    else
		    {
		    	if(alineacion.equals("R")) //'Toma la parte derecha de la cadena con la longitud indicada y lo alinea a la derecha
		    	{
		    		if(campo.length() > longMax)
		    		{
		                campo = cadenaRight(campo,longMax);
		    		}
		            alineacion = "D";
		    	}
		    	if(campo.length() > longMax)
		    	{
		    		if(alineacion.equals("D"))
		    		{
		                ajustarLongCampo = campo.substring((campo.length()-longMax), ((campo.length()-longMax))+longMax);
		    		}
	                else
		                ajustarLongCampo = campo.substring(0, longMax);
		    	}
		        else
		            ajustarLongCampo = campo;
		    	
		    	if(longMax - ajustarLongCampo.length() <= 0)
		    		espacios="";
		    	else
		    		espacios=""+(longMax - ajustarLongCampo.length());
		    		
		    	if(alineacion.equals("I"))
		    	{
		    		if(complemento.equals("") || complemento.equals(" ")) //rellenar con espacios
		    		{
		    			if(!espacios.equals(""))
		    				ajustarLongCampo = ajustarLongCampo+String.format("%1$-"+espacios+"s","");
		    		}
		    		else if(complemento.equals("0"))//rellenar con ceros
		    		{
		    			ajustarLongCampo = ajustarLongCampo + ponerCeros("",longMax - ajustarLongCampo.length());
		    		}
		    	}
		    	else if(alineacion.equals("D"))
		    	{
		    		if(complemento.equals("") || complemento.equals(" ")) //rellenar con espacios
		    		{
		    			ajustarLongCampo = String.format("%1$"+ espacios+"s",ajustarLongCampo);
		    		}
		    		if(complemento.equals("0"))
		    		{
		    			ajustarLongCampo = ponerCeros("",longMax - ajustarLongCampo.length()) + ajustarLongCampo;
		    		}
		            
		    	}
		    }
		    
		    if(separador.equals(","))
		    	ajustarLongCampo = ajustarLongCampo + ",";
	    	else if(separador.equals("T"))
	    		ajustarLongCampo = ajustarLongCampo + "\t";	//tabulador 
    		else
    			ajustarLongCampo = ajustarLongCampo + separador.trim();
    			
		    return ajustarLongCampo;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Utilerias, C:Funciones, M:ajustarLongitudCampo");
			e.printStackTrace();
			return"";
		}
	}
*/
	/**
	 * 
	 * @param campo
	 * @return campo
	 */
	public String ocultarCoinversora(String campo){
		return campo = campo.replace("COINVERSORA", "");
	}
	
	/**
	 * funcion RTrim
	 * @param cadena
	 * @return
	 */
	public String elmiminarEspaciosAlaDerecha(String cadena){ 
		String nuevaCadena = ""; 
		for( int i = cadena.length() - 1; i >= 0; i-- ){ 
			if ( cadena.charAt(i) != ' ' ){ 
				nuevaCadena = cadena.substring(0,i + 1); 
			break; 
			} 
		} 
	return nuevaCadena; 
	} 
	
	/**
	 * funcion LTrim
	 * @param cadena
	 * @return
	 */
	public String elmiminarEspaciosAlaIzquierda(String cadena){ 
		String nuevaCadena = ""; 
		for( int i = 0; i < cadena.length(); i++ ){ 
			if ( cadena.charAt(i) != ' ' ){ 
				nuevaCadena = cadena.substring(i); 
			break; 
			} 
		} 
	return nuevaCadena; 
	} 
	/**
	 * Este metodo convierte una cadena a double 
	 * si la cadena es nula o vacia retorna un cero
	 * @param cadena
	 * @return
	 */
	public double convertirCadenaDouble(String cadena){
		try{
			if(cadena != null && !cadena.trim().equals(""))
				return Double.parseDouble(cadena);
			else
				return 0;
		}catch(Exception e){
			return 0;
		}
	}
	/**
	 * Este metodo convierte una cadena a entero 
	 * si la cadena es nula o vacia retorna un cero
	 * @param cadena
	 * @return
	 */
	public int convertirCadenaInteger(String cadena){
		try{
			if(cadena != null && !cadena.trim().equals(""))
				return Integer.parseInt(cadena);
			else
				return 0;
		}catch(Exception e){
			return 0;
		}
	}
	/**
	 * Este metodo valida si una cadena no es null
	 * para poder seterar un valor valido en un dto
	 * @param cadena
	 * @return
	 */
	public String validarCadena(String cadena){
		if(cadena != null && !cadena.trim().equals(""))
			return cadena;
		else
			return "";
	}
	
	/**
	 * Convierte una cadena en un valor booleano
	 * @param cadena
	 * @return
	 */
	public boolean convertirCadenaBoolean(String cadena){
		if(cadena != null && cadena.trim().equals("true"))
			return true;
		else
			return false;
	}
			
	/**
	 * Este metodo valida que un valor entero
	 * sea mayor a cero de lo contrario retorna cero
	 * @param valor
	 * @return
	 */
	public int validarEntero(int valor){
		if(valor > 0)
			return valor;
		else
			return 0;
	}
	
	/**
	 * Esta funcion pone el formato moneda en pesos
	 * a un valor double
	 * @param valorCantidad
	 * @return
	 */
	public String obtenerFormatoPesos(double valorCantidad)
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
		
		DecimalFormat formatPesos = (DecimalFormat) nf;
		
		String dato = formatPesos.format(valorCantidad);
		
		return dato; 
	}
	
	/**
	 * Esta funcion pone el formato moneda en euros
	 * a un valor double
	 * @param valorCantidad
	 * @return
	 */
	public String obtenerFormatoEuros(double valorCantidad)
	{
		String patron = "n###,###,###,###.##";
		DecimalFormat formatPesos = new DecimalFormat(patron);
		return formatPesos.format(valorCantidad); 
	}
	
	/**
	 * Esta funcion pone el formato moneda en dolares
	 * a un valor double
	 * @param valorCantidad
	 * @return
	 */
	public String obtenerFormatoDolares(double valorCantidad)
	{
		String patron = "$###,###,###,###.##";
		DecimalFormat formatPesos = new DecimalFormat(patron);
		return formatPesos.format(valorCantidad); 
	}
	
	public String obtenerFormatoAsteriscos(double valorCantidad, int cant, String caracter)
	{
		String patron = "###,###,###,###.00";
		DecimalFormat decFormat = new DecimalFormat(patron);
		String regla = "";
		String cadena = "" + decFormat.format(valorCantidad);
		for (int i = 0; i < cant; i++)
			regla += caracter;
		if (regla.length() > cadena.length())
			cadena = regla.substring(0, regla.length() - cadena.length())
			.concat(cadena);

		if(caracter.equals(""))
			return "$"+cadena;
		else
			return cadena;
	}

	/**
	 * Convierte el numero de mes a su equivalente en letra
	 * @param numeroMes
	 * @return nombreMes
	 */
	public String nombreMes(int numeroMes){
    	String nombreMes = "";
    	switch (numeroMes)
    	{
	        case 1: nombreMes = "Enero"; break;
	        case 2: nombreMes = "Febrero"; break;
	        case 3: nombreMes = "Marzo"; break;
	        case 4: nombreMes = "Abril"; break;
	        case 5: nombreMes = "Mayo"; break;
	        case 6: nombreMes = "Junio"; break;
	        case 7: nombreMes = "Julio"; break;
	        case 8: nombreMes = "Agosto"; break;
	        case 9: nombreMes = "Septiembre"; break;
	        case 10: nombreMes = "Octubre"; break;
	        case 11: nombreMes = "Noviembre"; break;
	        case 12: nombreMes = "Diciembre"; break;
    	}
    	if(numeroMes < 1)
    		nombreMes = "Enero";
    	if(numeroMes > 12)
    		nombreMes = "Diciembre";
    	return nombreMes;
    }
    
	/**
	 * Convierte el numero de mes a su equivalente en letra abreviado 
	 * @param numeroMes
	 * @return nombreMes
	 */
    public String nombreMesCorto(int numeroMes){
    	String nombreMes = "";
    	switch (numeroMes)
    	{
	        case 1: nombreMes = "Ene"; break;
	        case 2: nombreMes = "Feb"; break;
	        case 3: nombreMes = "Mar"; break;
	        case 4: nombreMes = "Abr"; break;
	        case 5: nombreMes = "May"; break;
	        case 6: nombreMes = "Jun"; break;
	        case 7: nombreMes = "Jul"; break;
	        case 8: nombreMes = "Ago"; break;
	        case 9: nombreMes = "Sep"; break;
	        case 10: nombreMes = "Oct"; break;
	        case 11: nombreMes = "Nov"; break;
	        case 12: nombreMes = "Dic"; break;
    	}
    	if(numeroMes < 1)
    		nombreMes = "Ene";
    	if(numeroMes > 12)
    		nombreMes = "Dic";
    	return nombreMes;
    }
    
    public String convertirNumeroEnLetra(double numero){
    	return ConvertirNumeroEnLetra.convertNumberToLetter(numero);
    }
    
    /**
     * rellena la cadena con el caracter especificado a la izquierda
     * @param cadena
     * @param cant
     * @param caracter
     * @return
     */
    public String rellenarCadena(String cadena, int cant, String caracter) {
		String regla = "";
		for (int i = 0; i < cant; i++)
			regla += caracter;
		if (regla.length() > cadena.length())
			cadena = regla.substring(0, regla.length() - cadena.length())
					.concat(cadena);
		return cadena;
	}
    
    /**
     * rellena la cadena con el caracter especificado a la derecha
     * @param cadena
     * @param cant
     * @param caracter
     * @return
     */
    public String rellenarCadenaDerecha(String cadena, int cant, String caracter) {
		String regla = "";
		for (int i = 0; i < cant; i++)
			regla += caracter;
		if (regla.length() > cadena.length())
			cadena = cadena.concat(regla.substring(0, regla.length() - cadena.length()));
		return cadena;
	}
    
    /**
     * devuelve una nueva fecha dependiendo del tiempo que se le desea sumar o restar 
     * a la fecha indicada.
     * @param interval: permite 'd' para el dia, 'm' para el mes, o 'y' para el ano
     * @param tiempo: numero entero que se le quiere sumar o restar a la fecha, en caso de restar,
     * el valor debe ser negativo
     * @param fecha: fecha a modificar
     * @return Date fechaNueva
     */
/*
    public Date modificarFecha(String interval, int tiempo, Date fecha){
    	Date fechaNueva = new Date();
    	Calendar calendar = Calendar.getInstance();
    	try{
            calendar.setTime(fecha);
            if(interval.equals("d"))
            	calendar.add(Calendar.DAY_OF_MONTH, tiempo);
            else if(interval.equals("m"))
            	calendar.add(Calendar.MONTH, tiempo);
            else if(interval.equals("y"))
            	calendar.add(Calendar.YEAR, tiempo);
            else
            	return fecha;
            
            fechaNueva = calendar.getTime();
            
    	}catch(Exception e){
    		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Utilerias, C:Funciones, M:modificarFecha");
			e.printStackTrace();
    	}
    	
    	return fechaNueva;
    }
*/  
    /**
     * Este metodo genera un codigo de session
     * @param iIdUsuario
     * @return
     */
    public String generarNumSession(int iIdUsuario){
    	Calendar calendario = new GregorianCalendar();
    	double uNumAleatorio = 0; 
    	String sNumSession = "";
    	int horas = 0;
    	int minutos = 0;
    	int segundos = 0;
    	
	    	uNumAleatorio = Math.random();
	    	horas = calendario.get(Calendar.HOUR_OF_DAY);
	    	minutos = calendario.get(Calendar.MINUTE);
	    	segundos = calendario.get(Calendar.SECOND);
	    	sNumSession = "~" + horas + ":" + minutos + ":" + segundos + uNumAleatorio + iIdUsuario + "~";
	    	System.out.println(sNumSession);
    	return sNumSession;
    }
    
    /**
     * Este mntodo genera una cadena aleatoria, 
     * nos puede servir para dar valores aleatorios a variables
     * @return
     */
    public String generarCadenaClave(){
    	Calendar calendario = new GregorianCalendar();
    	double uNumAleatorio = 0; 
    	String sNumSession = "";
    	int horas = 0;
    	int minutos = 0;
    	int segundos = 0;
    	
	    	uNumAleatorio = Math.random();
	    	horas = calendario.get(Calendar.HOUR_OF_DAY);
	    	minutos = calendario.get(Calendar.MINUTE);
	    	segundos = calendario.get(Calendar.SECOND);
	    	sNumSession = horas + "_" + minutos + "_" + segundos + uNumAleatorio;
	    	System.out.println(sNumSession);
    	return sNumSession;
    }
    
    /**
     * Este metodo obtiene la hora actual en formato de 12 horas
     * con horas:minutos:segundos am/pm donde el ultimo digito diferencia am(0)
     * y pm(1) si el parametro bFormat12 = true, si es false retorna la hora 
     * en formato de 24 horas.
     * @param bFormat12 determina como se obtendra la hora, si es true
     * sera formato de 12 horas am/pm, de lo contrario 24 hrs.
     * @return
     */
    public String obtenerHoraActual(boolean bFormat12){
    	Calendar calendario = new GregorianCalendar();
    	int horas = 0;
    	int minutos = 0;
    	int segundos = 0;
    	String sHora = "";
    	
    	minutos = calendario.get(Calendar.MINUTE);
    	segundos = calendario.get(Calendar.SECOND);
    	
    	String sHoras;
    	String sMinutos;
    	String sSegundos;
    	
    	if(minutos < 10){
    		sMinutos = "0" + minutos;
    	}else
    		sMinutos = String.valueOf(minutos);
    	
    	if(segundos < 10){
    		sSegundos = "0" + segundos;
    	}else
    		sSegundos = String.valueOf(segundos);

    	if(bFormat12){
    		horas = calendario.get(Calendar.HOUR);
        	
    		if(horas < 10){
        		sHoras = "0" + horas;
        	}else
        		sHoras = String.valueOf(horas);
    		
    		sHora = sHoras+":"+sMinutos+":"+sSegundos+" "+calendario.get(Calendar.AM_PM);
    		
    	}else{
    		horas = calendario.get(Calendar.HOUR_OF_DAY);
    		
    		if(horas < 10){
        		sHoras = "0" + horas;
        	}else
        		sHoras = String.valueOf(horas);

    		sHora = sHoras+":"+sMinutos+":"+sSegundos;
    	}
    	return sHora;
    }
	
    /**
     * Este metodo retorna una cadena de un importe o cantidad double
     * con formato de comas y punto con dos decimales redondeado
     * @param valor
     * @return
     */
/*
	public String ponerFormatoImporte(double valor) {
		try{
			NumberFormat formato = new DecimalFormat("#, ##0.00"); 
			return formato.format(valor);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Utilerias, C:FuncionesDao, M:ponerFormatoCeros");
			return"";
		}
	}
*/ 
	/**
	 * Este  mntodo evalua si un ano es bisiesto
	 * @param dFechaHoy parametro de entrada de tipo Date
	 * @return true si es bisiesto de lo contratio false
	 */
/*
	@SuppressWarnings("deprecation")
	public boolean obtenerAnioBisiesto(Date dFechaHoy){
		boolean bBisiesto = false;
		
		try {
			GregorianCalendar calendario = new GregorianCalendar();
			calendario.setTime(dFechaHoy);
			
			if(calendario.isLeapYear(dFechaHoy.getYear()))
				bBisiesto = true;
			else
				bBisiesto = false;
				
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Utilerias, C:FuncionesDao, M:obtenerAnioBisiesto");
		}
		return bBisiesto;
	}
*/
	/**
	 * Este mntodo redondea un valor double
	 * @param uImporte importe a redondear
	 * @param iNumDecimales numero de decimales a los que se desea redondear
	 * @return la cantidad redondeada a los decimales indicados
	 */
/*
	public double redondearCantidades(double uImporte, int iNumDecimales){
		double uImporteRedondeado = 0;
		BigDecimal bdImporte;
		try{
			bdImporte = new BigDecimal(uImporte).setScale(iNumDecimales, BigDecimal.ROUND_UP);
			uImporteRedondeado = bdImporte.doubleValue();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Utilerias, C:FuncionesDao, M:redondearCantidades");
		}
		return uImporteRedondeado;
	}
*/
	public String sacarDatoEntreComas(String sCadena, int iPosicionDato){
		int iContadorComas;
		int iPosicionComa = 0;
		boolean bComillas = false;
		String sDato;
		String sCadenaAuxiliar = "";
		
		sCadenaAuxiliar = sCadena + ",";
	    
		iContadorComas = 0;
	    sDato = "";
	    if(iPosicionDato == 1)
	    {
	    	if(sCadenaAuxiliar.indexOf("\"") == 0)
	    		iPosicionComa = sCadenaAuxiliar.indexOf("\"", (sCadenaAuxiliar.indexOf("\"")) + 1);
	        else
	            iPosicionComa = sCadenaAuxiliar.indexOf(",") ;
	        
	    	if(iPosicionComa == 0)
	    		sDato = sCadena.substring(0);
	        else
	        	sDato = sCadena.substring(0, iPosicionComa);
	    }
	    else
	    {
	    	do
	    	{
	    		
	    		if(sCadenaAuxiliar.trim().indexOf("\"") == 0)
	    		{
	    			iPosicionComa = sCadenaAuxiliar.indexOf("\"", (sCadenaAuxiliar.indexOf("\"")) + 1);
	                bComillas = true;
	    		}
	            else
	            {
	            	iPosicionComa = sCadenaAuxiliar.indexOf(",");
	            	bComillas = false;
	            }
	    		//System.out.println("comillas "+bComillas);
	    		if(iPosicionComa > 0)
	    		{
	    			iContadorComas = iContadorComas + 1;
	    			if(iContadorComas == iPosicionDato - 1)
	    			{
	    				if(bComillas)
	    					sCadenaAuxiliar = sCadenaAuxiliar.substring(iPosicionComa + 2);
	                    else
	                    	sCadenaAuxiliar = sCadenaAuxiliar.substring(iPosicionComa + 1);
	                    
	    				if(sCadenaAuxiliar.trim().indexOf("\"") == 0)
	    					iPosicionComa = sCadenaAuxiliar.indexOf("\"", (sCadenaAuxiliar.indexOf("\"")) + 1);
	                    else
	                    	iPosicionComa = sCadenaAuxiliar.indexOf(",");
	                  
	    				if(iPosicionComa == 0)
	    					sDato = sCadenaAuxiliar.substring(0);
	                    else
	                    	sDato = sCadenaAuxiliar.substring(0, iPosicionComa);
	                    
	                    break;
	    			}
	    			else
	    			{
	    				 //Tiene comillas por lo que puede incluir comas, considerar el campo hasta la siguiente comilla doble
	    				if(sCadenaAuxiliar.trim().indexOf("\"") == 0)
	                        sCadenaAuxiliar = sCadenaAuxiliar.substring(iPosicionComa + 2);
	                    else
	                    	sCadenaAuxiliar = sCadenaAuxiliar.substring(iPosicionComa + 1);
	    			}
	    		}
	    	}while(sCadenaAuxiliar.indexOf(",") > 0);
	    }
	    sDato = sDato.replace("\"", "");
	    return sDato.trim();
	}
	
	/**
	 * Metodo que regresa la fecha y hora de la ultima
	 * modificacion del archivo
	 * @param sRutaArchivo
	 * @return
	 */
/*
	public String obtenerFechaArchivo(String sRutaArchivo){
		Date fecha = new java.util.Date(new java.io.File(sRutaArchivo).lastModified());
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return formatoDeFecha.format(fecha);
	}
*/
	/**
	 * Funcion que borra los ceros a la izquierda de una cadena
	 * @param cadena
	 * @return
	 */
	public String eliminarCerosAlaIzquierda(String cadena){ 
		String nuevaCadena = ""; 
		for( int i = 0; i < cadena.length(); i++ ){ 
			if ( cadena.charAt(i) != '0' ){ 
				nuevaCadena = cadena.substring(i); 
			break; 
			} 
		} 
	return nuevaCadena; 
	} 
	
	/**
	 * Metodo que calcula la diferencia de dias entre dos fechas
	 * @param fechaInicial
	 * @param fechaFinal
	 * @return
	 */
	public int diasEntreFechas(Date fechaInicial, Date fechaFinal) {
        int dias = 0;
        long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000;
        Calendar calendarFecha1 = Calendar.getInstance();
		Calendar calendarFecha2 = Calendar.getInstance();
    	calendarFecha1.setTime(fechaInicial);
		calendarFecha2.setTime(fechaFinal);
		
    	double fecIni = calendarFecha1.getTimeInMillis();
		double fecFin = calendarFecha2.getTimeInMillis();			 
		double fecResult = Math.ceil( ( fecFin - fecIni ) / MILLSECS_PER_DAY );	
		
		dias = (int)fecResult;
    		
		return dias;
	}
	
	/**
	 * Metodo que devuelve el nombre del mes segun la fecha indicada
	 * el parametro 'completo' indica si se va a retornar el nombre del mes
	 * completo si es true, o abreviado si es false
	 * @param fecha
	 * @param completo
	 * @return
	 */
	public String nombreMes(Date fecha, boolean completo) {
		String sMes = "";
		SimpleDateFormat formateador = null;
		if(completo)
			formateador = new SimpleDateFormat("MMMM", new Locale("ES"));
		else
			formateador = new SimpleDateFormat("MMM", new Locale("ES"));
		
		sMes = formateador.format(fecha);
	   return sMes.toUpperCase();
	}
	
	public String fechaString(Date fecha) {
		String sAnio = "";
		SimpleDateFormat formateador = new SimpleDateFormat("yyyy", new Locale("ES"));
		sAnio = formateador.format(fecha);
		String sMes = "";
		formateador = new SimpleDateFormat("MM", new Locale("ES"));
		sMes = formateador.format(fecha);
		String dia = "";
		formateador = new SimpleDateFormat("dd", new Locale("ES"));
		dia = formateador.format(fecha);
	   return sAnio + "-" + sMes + "-" + dia;
	}
	
	/**
	 * Metodo que devuelve el ano completo de la fecha indicada
	 * @param fecha
	 * @return
	 */
	public int obtenerAnio(Date fecha) {
		String sAnio = "";
		SimpleDateFormat formateador = new SimpleDateFormat("yyyy", new Locale("ES"));
		sAnio = formateador.format(fecha);
	   return Integer.parseInt(sAnio);
	}
	
	/**
	 * Metodo que devuelve el numero del mes de la fecha indicada
	 * @param fecha
	 * @return
	 */
	public int obtenerMes(Date fecha) {
		String sMes = "";
		SimpleDateFormat formateador = new SimpleDateFormat("MM", new Locale("ES"));
		sMes = formateador.format(fecha);
	   return Integer.parseInt(sMes);
	}
	
	/**
	 * Metodo que devuelve el numero del dia de la fecha indicada
	 * @param fecha
	 * @return
	 */
	public int obtenerDia(Date fecha) {
		String dia;
		SimpleDateFormat formateador = new SimpleDateFormat("dd", new Locale("ES"));
		dia = formateador.format(fecha);
	   return Integer.parseInt(dia);
	}
	
	/**
	 * Metodo que devuelve la fecha en letra 
	 * la bandera 'dia' indica que incluye el nombre del dia si es true
	 * @param fecha
	 * @param dia
	 * @return
	 */
	public String ponerFechaLetra(Date fecha, boolean dia) {
		String sFecha = "";
		SimpleDateFormat formateador = null;
		if (dia)
			formateador = new SimpleDateFormat("EEEE ',' dd 'de' MMMM 'de' yyyy", new Locale("ES"));
		else
			formateador = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("ES"));
		
		sFecha = formateador.format(fecha);
	   return sFecha;
	}
	
	/**
	 * Metodo que devuelve el password encriptado
	 * @param password
	 * @return password encriptado
	 */
/*
	public String encriptador(String password) {
		String sPassword = "";
		MessageDigest md = null;
		
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] mb = md.digest();
            sPassword = new String(Hex.encodeHex(mb));
            
        } catch (NoSuchAlgorithmException e) {
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:FuncionesDao, M:encriptador");
        }
        return sPassword;
	}
*/
/*
	@SuppressWarnings("deprecation")
	public static void main(String args[])
	{
		Funciones fun = new Funciones();
		Date f = new Date();
		Date fecha = new Date("02/01/" + fun.obtenerAnio(f));
		System.out.println(fecha);
	}
*/
	/**
	 * Metodo que devuelve la fecha con el dia en letra y concatenado la fecha en formato normal 
	 * la bandera 'dia' indica que incluye el nombre del dia si es true
	 * @param fecha (date)
	 * @param dia (true o false)
	 * @return lunes '01/01/2100'
	 */
	// MS: se usa en flujo y posicion
	public String ponerDiaLetraYFecha(Date fecha, boolean dia) {
		String sFecha = "";
		SimpleDateFormat formateador = null;
		
		if (dia)
			formateador = new SimpleDateFormat("EEEE ',' d'/'M'/'yyyy", new Locale("ES"));
		else
			formateador = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("ES"));
		
		sFecha = formateador.format(fecha);
	   return sFecha;
	}
	/*
	//MS: comentada por que no se usa
	public List<LlenaComboGralDto> mounthYear() {
		List<LlenaComboGralDto> meses = new ArrayList<LlenaComboGralDto>();
		LlenaComboGralDto obj = new LlenaComboGralDto();
		
		for(int i=1; i<13; i++) {
			obj = new LlenaComboGralDto();
			obj.setId(i);
			obj.setCampoDos(nombreMes(i));
			meses.set(i, obj);
		}
		return meses;
	}
	*/
	/*private void leerArchivoExcel(String archivoDestino) {
		try {
			Workbook archivoExcel = Workbook.getWorkbook(new File(archivoDestino));
			System.out.println("Nnmero de Hojas\t"+ archivoExcel.getNumberOfSheets());
			
			for (int sheetNo = 0; sheetNo < archivoExcel.getNumberOfSheets(); sheetNo++) {
				Sheet hoja = archivoExcel.getSheet(sheetNo);
				int numColumnas = hoja.getColumns();
			    int numFilas = hoja.getRows();
			    String data;
			    System.out.println("Nombre de la Hoja\t" + archivoExcel.getSheet(sheetNo).getName());
				
			    for (int fila = 0; fila < numFilas; fila++) { // Recorre cada fila de la hoja
					for (int columna = 0; columna < numColumnas; columna++) { // Recorre cada columna de la hoja                                                    
						data = hoja.getCell(columna, fila).getContents();
						System.out.print(data + " ");
					}
				    System.out.println("\n");
				}
			}
		} catch (Exception ioe) { ioe.printStackTrace(); }
	}*/
	
	public String nombreDia(int numeroDia){
    	String nombreDia = "";
    	
    	switch (numeroDia)
    	{
	        case 1: nombreDia = "Dom"; break;
	        case 2: nombreDia = "Lun"; break;
	        case 3: nombreDia = "Mar"; break;
	        case 4: nombreDia = "Mie"; break;
	        case 5: nombreDia = "Jue"; break;
	        case 6: nombreDia = "Vie"; break;
	        case 7: nombreDia = "Sab"; break;
    	}
    	return nombreDia;
    }
	
	public void enviaMail(String mailReceptor, String mailCopia, String asunto, String cuerpo) {
//        SecurityManager security = System.getSecurityManager();
        System.out.println("funciones");
        try {
        	Properties props = new Properties();
            props.put("mail.smtp.user", miCorreo);
            props.put("mail.smtp.host", servidorSMTP);
            props.put("mail.smtp.port", puertoEnvio);
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.socketFactory.port", puertoEnvio);
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");
            
            Authenticator auth = new autentificadorSMTP();
            Session session = Session.getInstance(props, auth);
            
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(miCorreo));
            
            if(mailCopia.equals("")){
            	String recep[] = mailReceptor.split(";");     
                Address []destinos = new Address[recep.length];//Aqui usamos el arreglo de destinatarios
                for(int i=0;i<recep.length;i++){
                    destinos[i]=new InternetAddress(recep[i]);
                }
                
                msg.addRecipients(Message.RecipientType.TO, destinos);	//Mail a quienes va dirigido el aviso de pago
            }else{
            	String recep[] = mailCopia.split(";");     
                Address []destinos = new Address[recep.length];//Aqui usamos el arreglo de destinatarios
                for(int i=0;i<recep.length;i++){
                    destinos[i]=new InternetAddress(recep[i]);
                }
                
                msg.addRecipient(Message.RecipientType.TO, new InternetAddress(mailReceptor));	//Mail a quienes va dirigido el aviso de pago
                msg.addRecipients(Message.RecipientType.CC, destinos);	//Mail a quienes va dirigido el aviso de pago 
            }
            
            
            //msg.addRecipient(Message.RecipientType.CC, new InternetAddress(mailCopia));	//Mail a quienes va copiado el aviso de pago
            msg.setSubject(asunto);	//Asunto del mail
            msg.setText(cuerpo);	//Cuerpo del mail
            
//            DataHandler dh = new DataHandler(msgBody,"text/plain"); // Aqun se espesifica el tipo de texto que se desea enviar
 //           msg.setDataHandler(dh);
            
            Transport.send(msg);
            System.out.println("mail enviado");
        } catch (Exception mex) { mex.printStackTrace(); }
	}
	
	private class autentificadorSMTP extends javax.mail.Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(miCorreo, miContrasena);
        }
    }
	//MS: funcion para flujos
	public String retornaFecha(String dataInd) {
		return 01 + "/" + dataInd.substring(3, 5) + "/" + dataInd.substring(dataInd.length() - 4);
	}
	
	public String quitaCaracteresEspeciales(String cadena) {
		return cadena.replaceAll("[.|-|_|,|;|!|$|%|&|/|(|)|=|?|n|n|#]", "");
	}
	
	//AGREGADO EMS: 26/10/2015 - Se pasa la ruta o nombre del archivo y retorna su extencinn.
	public String obtenerExtensionArchivo(String rutaArchivo) {
	      int index = rutaArchivo.lastIndexOf('.');
	      if (index == -1) {
	            return "";
	      } else {
	            return rutaArchivo.substring(index + 1).toLowerCase();
	      }
	}

	// MS: solo se usa en pay link pero hay que revisar para no tener que utilizarla
	public String cambiarFechaGregoriana(String fecha){
		String []mesArreglo = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", 
				"Sep", "Oct", "Nov", "Dec"};
		
		
		String mesDate = fecha.substring(4,7);
		String dia=fecha.substring(8,10);
		String anio=fecha.substring(11,15);
		String mesAux = "";
		int mes = 0;
		
		for(int i=0 ; i<12; i++)
		{
			if(mesArreglo[i].equals(mesDate)){
				mes = i+1;
				if(mes < 10)
					mesAux = '0' + "" + mes;
				else
					mesAux = mes + "";
			}
		}		
		String fechaString=""+dia+'/'+mesAux+'/'+anio;
		return fechaString;		
	} 
}
