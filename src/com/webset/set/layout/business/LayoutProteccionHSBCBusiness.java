package com.webset.set.layout.business;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.poi.ss.formula.functions.Replace;

import com.webset.set.impresion.dao.ImpresionDao;
import com.webset.set.impresion.dto.LayoutProteccionDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;
import com.webset.utils.tools.Utilerias;

/**
 * escribe archivo de proteccion de cheques de HSBC del modulo Impresion
 * @author Jessica Arelly Cruz Cruz
 * @since 30/06/2011
 *
 */
public class LayoutProteccionHSBCBusiness {
	Bitacora bitacora = new Bitacora();
	Funciones funciones = new Funciones();
	private ImpresionDao impresionDao;
	List<LayoutProteccionDto> recibeResultado = new ArrayList<LayoutProteccionDto>();
	SimpleDateFormat formatoFechaYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat formatoFechaddMMyy = new SimpleDateFormat("ddMMyy");
	SimpleDateFormat formatoFechaddMMyyyy = new SimpleDateFormat("ddMMyyyy");
	SimpleDateFormat formatoDelTexto = new SimpleDateFormat("yyyy/MM/dd");
	SimpleDateFormat formatoSantanderSeguridad = new SimpleDateFormat("dd/MM/yyyy");
	
	public String armaArchivoHSBC(LayoutProteccionDto dto){
		
		StringBuffer sRegistro = new StringBuffer() ;
		String psBeneficiario ="";
		String psCampo = "";
		
		try{
			psBeneficiario = funciones.quitarCaracteresRaros(dto.getBeneficiario(), true);
			funciones.ponerFechaSola(funciones.modificarFecha("m", 6, dto.getFecImprime()));
			sRegistro.append(funciones.ponerCeros(dto.getIdChequera(), 10)); 	//no de cuenta 10
			sRegistro.append(funciones.ponerCeros(""+dto.getNoCheque(), 8));	//no ceheque 8
			sRegistro.append(funciones.ponerFormatoCeros(dto.getImporte(), 10));	//importe 13 (10 dec. 2 decimales y punto decimal)
		    psCampo = psBeneficiario.trim(); 
		    if(psCampo.length() > 40)
		    	psCampo = psCampo.substring(0, 40);
		    else
		    	psCampo = funciones.rellenarCadenaDerecha(psCampo, 40, " ");
		    sRegistro.append(psCampo);	//Nombre del Beneficiario 40
		    psCampo = dto.getConcepto().trim();
		    if(psCampo.length() != 0)
		    {
		    	if(psCampo.length() > 200)
		    		psCampo = psCampo.substring(0, 200);
		    	else
		    		psCampo = funciones.rellenarCadenaDerecha(psCampo, 200, " ");
		    	sRegistro.append(psCampo);
		    }
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:layout, C:LayoutProteccionHSBCBusiness, M:armaArchivoHSBC");
			e.printStackTrace();
		}
		return sRegistro.toString();
	}
	//Armar layout de Cheque protegido Azteca Nuevo A.A.G
	public String armaAztecaChequesProtec(LayoutProteccionDto dto, int pid,  String rfc, String noEmpresa){
		StringBuffer psRegistro = new StringBuffer();
		psRegistro.append("");
		String psCampoe = "";
		String psCampo = "";
		try {
			psRegistro.append(funciones.ajustarLongitudCampo(""+(pid+1), 7, "D", "", "0"));
			recibeResultado = impresionDao.obtieneFechaHoy();
		    if (recibeResultado.size() != 0){
		    	psCampo = formatoFechaYYYYMMDD.format(recibeResultado.get(0).getFecHoy());  //ddMMyy						
				psRegistro.append(psCampo);						
			}
		    String psIdChequera =  "0127"+dto.getIdChequera().substring(0, 4)+
					"00"+dto.getIdChequera().substring(dto.getIdChequera().length()-10, dto.getIdChequera().length());
    		
		    psRegistro.append("03");
		    psRegistro.append("01");
		    psRegistro.append(funciones.ajustarLongitudCampo(psIdChequera, 20, "D", "", "0"));
		    psRegistro.append(funciones.ajustarLongitudCampo(funciones.quitarCaracteresRaros(noEmpresa, true), 40, "I", "", " "));
		    //primeras 7 posisicones 
		    psCampo = dto.getNoCheque()+"";
		    //psRegistro.append(funciones.ajustarLongitudCampo(rfc, 18, "I", "", " "));
		    psRegistro.append(funciones.ajustarLongitudCampo(psCampo, 7, "D", "", "0"));
		    psCampo = " ";
		    psRegistro.append(funciones.ajustarLongitudCampo(psCampo, 11, "I", "", " "));
		    psRegistro.append(funciones.ajustarLongitudCampo(" ", 30, "D", "", " "));
			psCampo = "";					
			psCampo = funciones.ponerFormatoCeros(dto.getImporte(), 15);
			psCampo = psCampo.replace(".", "");
			psCampo = funciones.ajustarLongitudCampo(psCampo, 15, "D", "", "0");					
			psRegistro.append(psCampo);
			psRegistro.append("MXP");
			psRegistro.append("000");
			psRegistro.append("00");
			psCampo = "";
			psRegistro.append(funciones.ajustarLongitudCampo(psCampo, 20, "I", "", "0"));
			psCampo = "";
			psRegistro.append(funciones.ajustarLongitudCampo(psCampo, 40, "I", "", " "));
			psRegistro.append(funciones.ajustarLongitudCampo(psCampo, 30, "D", "", " "));
			psCampo = "   ";
			psRegistro.append(funciones.ajustarLongitudCampo(psCampo, 18, "D", "", " "));
			psRegistro.append("00");
			psCampo = "";
			psRegistro.append(funciones.ajustarLongitudCampo(psCampo, 40, "I", "", "0"));
			psRegistro.append(funciones.ajustarLongitudCampo(psCampo, 12, "D", "", " "));
			psRegistro.append("06");
			psRegistro.append(funciones.ajustarLongitudCampo(psCampoe, 40, "I", "", "\u0020"));
			System.out.println("reg "+psRegistro);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + "" + toString() + "P:Impresion, C:LayoutProteccionHSBCBusiness, M:armaAztecaChequesProtec");			
		}
		return psRegistro.toString();
	}

	public String armaAztecaChequesProtecReapaldo(LayoutProteccionDto dto, int pid,  String rfc, String noEmpresa){
		StringBuffer psRegistro = new StringBuffer();
		psRegistro.append("");
		String psCampoe = "";
		String psCampo = "";
		try {
			psRegistro.append(funciones.ajustarLongitudCampo(""+(pid+1), 7, "D", "", "0"));
			recibeResultado = impresionDao.obtieneFechaHoy();
		    if (recibeResultado.size() != 0){
		    	psCampo = formatoFechaYYYYMMDD.format(recibeResultado.get(0).getFecHoy());  //ddMMyy						
				psRegistro.append(psCampo);						
			}
		    psRegistro.append("03");
		    psRegistro.append("01");
		    psRegistro.append(funciones.ajustarLongitudCampo(dto.getIdChequera(), 20, "D", "", "0"));
		    psRegistro.append(funciones.ajustarLongitudCampo(noEmpresa, 40, "I", "", " "));
		    psRegistro.append(funciones.ajustarLongitudCampo(rfc, 18, "I", "", " "));
		    psRegistro.append(funciones.ajustarLongitudCampo(" ", 30, "D", "", " "));
			psCampo = "";					
			psCampo = funciones.ponerFormatoCeros(dto.getImporte(), 15);
			psCampo = psCampo.replace(".", "");
			psCampo = funciones.ajustarLongitudCampo(psCampo, 15, "D", "", "0");					
			psRegistro.append(psCampo);
			psRegistro.append("MXP");
			psRegistro.append("000");
			psRegistro.append("00");
			psCampo = "";
			psRegistro.append(funciones.ajustarLongitudCampo(psCampo, 20, "I", "", "0"));
			psCampo = "";
			psRegistro.append(funciones.ajustarLongitudCampo(funciones.quitarCaracteresRaros(dto.getBeneficiario(),true), 40, "I", "", " "));
			psRegistro.append(funciones.ajustarLongitudCampo(psCampo, 30, "D", "", " "));
			psCampo = "   ";
			psRegistro.append(funciones.ajustarLongitudCampo(psCampo, 18, "D", "", " "));
			psRegistro.append("00");
			psCampo = "";
			psRegistro.append(funciones.ajustarLongitudCampo(psCampo, 40, "I", "", "0"));
			psRegistro.append(funciones.ajustarLongitudCampo(psCampo, 12, "D", "", " "));
			psRegistro.append("06");
			psRegistro.append(funciones.ajustarLongitudCampo(psCampoe, 40, "I", "", "\u0020"));
			System.out.println("reg "+psRegistro);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + "" + toString() + "P:Impresion, C:LayoutProteccionHSBCBusiness, M:armaAztecaChequesProtec");			
		}
		return psRegistro.toString();
	}

	public String armaSantanderChequesProtec(LayoutProteccionDto dto){
		StringBuffer psRegistro = new StringBuffer();
		psRegistro.append("");
		String psCampo;
		//655029129780002362ARTURO RUIZ SANCHEZGABRIELA SILVA ALVAREZ   00000001691750.02015-09-16
		try {
			psRegistro.append(funciones.ajustarLongitudCampo(dto.getIdChequera(), 16, "I", "", " ")); //CUENTA DE CHEQUES
			psRegistro.append(funciones.ajustarLongitudCampo(String.valueOf(dto.getNoCheque()), 7, "D", "", "0"));//NO DE CHEQUE
			psRegistro.append(funciones.ajustarLongitudCampo("", 13, "I", "", " ")); //CUENTA DE CHEQUES
			psCampo = (funciones.quitarCaracteresRaros(dto.getBeneficiario(), true));
			psRegistro.append(funciones.ajustarLongitudCampo(psCampo, 60, "I", "", " "));//BENEFICIARIO
			double dato = (double)dto.getImporte()*100;
			//double dato=720.0*100;
			NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
			DecimalFormat formatPesos = (DecimalFormat) nf;
			
			String in = formatPesos.format(dato);
			String convert=String.valueOf(in);
			int tam=convert.length();
			String correc=convert.substring(0,tam-2);
			correc=correc.replace(".", "");
			correc=correc.replace(",", "");
			correc=correc.replace("$", "");
			psCampo=correc;
			//psCampo = (in + "").replace(".0", "");
			psRegistro.append(funciones.ajustarLongitudCampo(psCampo, 16, "D", "", "0"));//IMPORTE
			recibeResultado = impresionDao.obtieneFechaHoy();
		    if (recibeResultado.size() != 0){
					psCampo = formatoSantanderSeguridad.format(recibeResultado.get(0).getFecHoy());  //ddMMyy						
					psRegistro.append(psCampo);	
					Date fecVencimiento = impresionDao.obtieneFechaVencimiento();
					psRegistro.append(formatoSantanderSeguridad.format(fecVencimiento));
					
					System.out.println(psRegistro);
			}
			
		    System.out.println(psRegistro);
		    
			return psRegistro.toString();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + "" + toString() + "P:Impresion, C:LayoutProteccionHSBCBusiness, M:armaBanamexChequesProtec");
			return "";
		}
	}
	//Armar layout de Cheque protegido BANAMEX Nuevo A.A.G
	public String armaBanamexChequesProtec(LayoutProteccionDto dto, int tipoRegistro, int totalRegistros, double totalImporteArchivo, 
			  String nomArchivo, boolean pbPendientes){
		StringBuffer psRegistro = new StringBuffer();
		psRegistro.append("");
		String psCampo = "";
		
		try {
			psRegistro.append("08");
			psRegistro.append( "01");
			psCampo = dto.getIdChequera().substring(0, 3);
			psRegistro.append(psCampo);
			psCampo = dto.getIdChequera().substring(dto.getIdChequera().length()-7, dto.getIdChequera().length());
			psRegistro.append( psCampo);
			psCampo = "00";  
			psRegistro.append(psCampo);
			psCampo = "0000";
			psRegistro.append( psCampo);
			psRegistro.append(funciones.ajustarLongitudCampo("0", 20, "D", "", "0"));
			psCampo = funciones.ponerFormatoCeros(dto.getImporte(), 15);
			psCampo = psCampo.replaceAll(".", "");
			psCampo = funciones.ajustarLongitudCampo(psCampo, 15, "D", "", "0");
			psRegistro.append(psCampo);
			psCampo = "001";
			psRegistro.append(psCampo);
		    psCampo = "06";               
		    psRegistro.append( psCampo);
		    psRegistro.append(funciones.ajustarLongitudCampo(""+dto.getNoCheque(), 8, "I", "", "0"));
		    psRegistro.append( funciones.ajustarLongitudCampo(""+dto.getNoCheque(), 8, "I", "", "0"));
		    psRegistro.append(funciones.ajustarLongitudCampo("0", 6, "D", "", "0"));
		    psRegistro.append(funciones.ajustarLongitudCampo("0", 6, "D", "", "0"));
		    recibeResultado = impresionDao.obtieneFechaHoy();
		    if (recibeResultado.size() != 0)
			{
				psCampo = formatoFechaddMMyy.format(recibeResultado.get(0).getFecHoy());  //ddMMyy						
				psRegistro.append(psCampo);						
			}
		    psRegistro.append(Utilerias.obtenerHoraMinuto());
		    return psRegistro.toString();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + "" + toString() + "P:Impresion, C:LayoutProteccionHSBCBusiness, M:armaBanamexChequesProtec");
			return "";
		}
	}
	
	public String archivoSeguridadNetCash(LayoutProteccionDto dto, int tipoRegistro, int totalRegistros, double totalImporteArchivo, 
			  String nomArchivo, boolean pbPendientes){
		StringBuffer psRegistro = new StringBuffer();
		//String psCampo = "";
		
		try {
			//psCampo = formatoFechaddMMyyyy.format(recibeResultado.get(0).getFecHoy());  //ddMMyy
			//String fechaVencimiento = formatoFechaddMMyyyy.format(Utilerias.sumarFecha(psCampo, 0, 6, 0));
			psRegistro.append(funciones.ajustarLongitudCampo(String.valueOf(dto.getNoCheque()), 7, "D", "", "0"));
			
			String val = dto.getImporte()+"";
			System.out.println("este es el importe-"+val+"-Este es el importe");
			int a = val.indexOf(".");
			System.out.println("Estre es a"+a);
			String cadena = val.substring(0, a);
			String cadena1 = val.substring(a+1, val.length());
			System.out.println("cadena uno "+cadena);
			System.out.println("cadena dos "+cadena1);
			if( a == -1){
				System.out.println("aa");
				val = val.substring(0, a+3);
				psRegistro.append(funciones.ajustarLongitudCampo(val, 16, "D", "", "0"));
			}else{
				System.out.println("bb");
				
				psRegistro.append(funciones.ajustarLongitudCampo(cadena, 13, "D", "", "0"));
				psRegistro.append(".");
				psRegistro.append(funciones.ajustarLongitudCampo(cadena1, 2, "I", "", "0"));
			}
			//psRegistro.append(funciones.ajustarLongitudCampo(val, 16, "D", "", "0"));
			psRegistro.append(funciones.ajustarLongitudCampo(dto.getIdChequera(), 18, "D", "", "0"));
			psRegistro.append("P");
			
		} catch (Exception e) {
			System.out.println("ee");
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + "" + toString() + "P:Impresion, C:LayoutProteccionHSBCBusiness, M:archivoSeguridadNetCash");
		}
		
		
		return psRegistro.toString();
	}
	
	
	//revisado9
	public String armaBancomerH2H(LayoutProteccionDto dto, int tipoRegistro, int totalRegistros, double totalImporteArchivo, 
			  String nomArchivo, boolean pbPendientes)
	{
		String campoStr = "";
		String campoVacio = "";
		String psRegistro = "";
		int encuentraCaracter = 0;
		try{
		switch (tipoRegistro){
			case 1: //Encabezado de proteccion del cheque
				//Identificador (valor fijo 1)
				psRegistro = "1";
				
				//Numero de registros OK
				campoStr = Integer.toString(totalRegistros);
				campoStr = funciones.ajustarLongitudCampo(campoStr, 7, "D", "", "0");
				psRegistro = psRegistro + campoStr;
				
				//Importe total OK					
				campoStr = "";					
				campoStr = funciones.ponerFormatoCeros(totalImporteArchivo, 15);
				campoStr = campoStr.replace(".", "");
				campoStr = funciones.ajustarLongitudCampo(campoStr, 15, "D", "", "0");					
				psRegistro = psRegistro + campoStr;
				
				//Numero de registros no OK (esto siempre se manda en blanco)
				campoVacio = "";
				campoVacio = funciones.ajustarLongitudCampo(campoVacio, 7, "D", "", "0");
				psRegistro = psRegistro + campoVacio;
				
				//Importe total no OK
				campoVacio = "";
				campoVacio = funciones.ajustarLongitudCampo(campoVacio, 15, "D", "", "0");
				psRegistro = psRegistro + campoVacio;
									
				//Tipo de cuenta
				encuentraCaracter = dto.getIdChequera().indexOf("910");
				if ((dto.getIdBanco() == 12) &&
						(encuentraCaracter != -1) &&
						(dto.getIdChequera().length() < 18))
				{
					psRegistro = psRegistro + "99";
				}else{
					psRegistro = psRegistro + "40";
				}
				
				//Cuenta de cheques
				if (dto.getIdChequera().length() > 10){
					campoStr = dto.getIdChequera().substring((dto.getIdChequera().length()-10) + 1);
				}
				campoStr = funciones.ajustarLongitudCampo(campoStr, 18, "D", "", "0");
				psRegistro = psRegistro + campoStr;
				
				//Fecha de envio
				recibeResultado = impresionDao.obtieneFechaHoy();
				
				if (recibeResultado.size() != 0){
					campoStr = formatoFechaYYYYMMDD.format(recibeResultado.get(0).getFecHoy());						
					psRegistro = psRegistro + campoStr;						
				}
				//bc0000001.txt
				//Numero de lote
				encuentraCaracter = nomArchivo.indexOf(".");
				campoStr = encuentraCaracter != -1 ? nomArchivo.substring(0, encuentraCaracter) : "1";
				
				campoStr = funciones.ajustarLongitudCampo(campoStr, 7, "D", "", "0");
				psRegistro = psRegistro + campoStr;
				
				//Codigo de respuesta del lote(al envio se va en blancos)
				campoVacio = "";
				campoVacio = funciones.ajustarLongitudCampo(campoVacio, 7, "D", "", "");
				psRegistro = psRegistro + campoVacio;
				
				//Descripcion del codigo de respuesta del lote(al envio se manda en blanco)
				campoVacio = "";
				campoVacio = funciones.ajustarLongitudCampo(campoVacio, 40, "D", "", "");
				psRegistro = psRegistro + campoVacio;
				
				//Filler (para uso futuro)
				campoVacio = "";
				campoVacio = funciones.ajustarLongitudCampo(campoVacio, 38, "D", "", "");
				psRegistro = psRegistro + campoVacio;
				
			break;
		
			case 2: //Detalle de proteccion del cheque
			//Identificador de detalle(valor fijo 3)
				psRegistro = "3";
				//Numero de cheque
				campoStr = Integer.toString(dto.getNoCheque());
				campoStr = funciones.ajustarLongitudCampo(campoStr, 7, "D", "", "0");
				psRegistro = psRegistro + campoStr;
				
				//Tipo de operacion
				if (pbPendientes){
					psRegistro = psRegistro + "A"; //Proteccion de cheques
				}else{
					psRegistro = psRegistro + "B"; //Desproteccion de cheques
				}
				
				//Importe				
				campoStr = funciones.ponerFormatoCeros(dto.getImporte(), 15);
				campoStr = campoStr.replace(".", "");
				campoStr = funciones.ajustarLongitudCampo(campoStr, 15, "D", "", "0");					
				psRegistro = psRegistro + campoStr;
				
				//Beneficiario
				campoStr = dto.getBeneficiario();
				campoStr = funciones.ajustarLongitudCampo(campoStr, 40, "I", "", "");
				psRegistro = psRegistro + campoStr;
				
				//No de cheque inicial (para saber estatus del cheque, en este caso no aplica consultas)
				campoVacio = "";
				campoVacio = funciones.ajustarLongitudCampo(campoVacio, 7, "D", "", "0");
				psRegistro = psRegistro + campoVacio;
				
				//No de cheque final (para saber estatus del cheque, en este caso no aplica consultas)
				campoVacio = "";
				campoVacio = funciones.ajustarLongitudCampo(campoVacio, 7, "D", "", "0");
				psRegistro = psRegistro + campoVacio;
				
				//Estatus del cheque (se manda vacio)
				campoVacio = "";
				campoVacio = funciones.ajustarLongitudCampo(campoVacio, 7, "I", "", "");
				psRegistro = psRegistro + campoVacio;
				
				//Descripcion del estatus
				campoVacio = "";
				campoVacio = funciones.ajustarLongitudCampo(campoVacio, 40, "I", "", "");
				psRegistro = psRegistro + campoVacio;
				
				//Fecha de operacion
				recibeResultado = impresionDao.obtieneFechaHoy();
				if (recibeResultado.size() != 0){
					campoStr = formatoFechaYYYYMMDD.format(recibeResultado.get(0).getFecHoy());						
					psRegistro = psRegistro + campoStr;						
				}
				
				//Hora de operacion (se manda vacio BBVA lo llena)
				campoVacio = "";
				campoVacio = funciones.ajustarLongitudCampo(campoVacio, 6, "D", "", "");
				psRegistro = psRegistro + campoVacio;
				
				//Filler (para uso futuro)
				campoVacio = "";
				campoVacio = funciones.ajustarLongitudCampo(campoVacio, 26, "D", "", "");
				psRegistro = psRegistro + campoVacio;
				
			break;
		}						
	}
	catch(Exception e){
		e.printStackTrace();
		bitacora.insertarRegistro(new Date().toString() + "" + toString() + "P:Impresion, C:LayoutProteccionHSBCBusiness, M:armaBancomerH2H");			
	}
	return psRegistro;
}
	
	public String armaBancomerH2HA(LayoutProteccionDto dto, int tipoRegistro, int totalRegistros, double totalImporteArchivo, 
			  String nomArchivo, boolean pbPendientes)
	{
		String campoStr = "";
		String campoVacio = "";
		String psRegistro = "";
		int encuentraCaracter = 0;
		try{

				//Numero de cheque
				campoStr = Integer.toString(dto.getNoCheque());
				campoStr = funciones.ajustarLongitudCampo(campoStr, 7, "D", "", "0");
				psRegistro = psRegistro + campoStr;
							
				campoStr = funciones.ponerFormatoCeros(dto.getImporte(), 16);
				campoStr = funciones.ajustarLongitudCampo(campoStr, 16, "D", "", "0");					
				psRegistro = psRegistro + campoStr;
				
				psRegistro = psRegistro + funciones.ajustarLongitudCampo( dto.getIdChequera(), 18, "D", "", "0");	
				
				campoVacio = "P";
				psRegistro = psRegistro + campoVacio;
				psRegistro +="\n";
				System.out.println("layout de bancomer "+psRegistro);
				
	}
	catch(Exception e){
		e.printStackTrace();
		bitacora.insertarRegistro(new Date().toString() + "" + toString() + "P:Impresion, C:LayoutProteccionHSBCBusiness, M:armaBancomerH2HA");			
	}
	return psRegistro;
}

	
	
	
	
	
	
	public ImpresionDao getImpresionDao() {
		return impresionDao;
	}

	public void setImpresionDao(ImpresionDao impresionDao) {
		this.impresionDao = impresionDao;
	}
}
