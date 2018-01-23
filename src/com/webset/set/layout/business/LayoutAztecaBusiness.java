package com.webset.set.layout.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.webset.set.bancaelectronica.dao.EnvioTransferenciasDao;
import com.webset.set.bancaelectronica.dto.CriterioBusquedaDto;
import com.webset.set.layout.dao.LayoutsDao;
import com.webset.set.traspasos.dto.BuscarSolicitudesTraspasosDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.MovimientoDto;
//Autor A.A.G
public class LayoutAztecaBusiness {
	private EnvioTransferenciasDao envioTransferenciasDao;
	private LayoutsDao layoutsDao;
	List<CriterioBusquedaDto> structBE = new ArrayList<CriterioBusquedaDto>();
    List<CriterioBusquedaDto> structEquivaleCampo = new ArrayList<CriterioBusquedaDto>();
    List<CriterioBusquedaDto> structEquivaleBE = new ArrayList<CriterioBusquedaDto>();
    CreacionArchivosBusiness creacionArchivosBusiness = new CreacionArchivosBusiness();
    Bitacora bitacora = new Bitacora();
	Funciones funciones = new Funciones();
	ConsultasGenerales consultasGrales;
   
    
    public String beArmaAzteca(MovimientoDto movimientoDto, int i, boolean pbMismoBanco, boolean pbInternacional){
    	String psRegistro = "";
    	//String psReferencia = "";
    	String psDivisaLayout = "";
    	String lsTipoCtaBenef = "";
    	String lsTipoPago = "";
    	//String lsIdChequeraOrig = "";
    	String psNombreEmpresaP = "";
    	String cdChequeraBenef = "";
    	String psBeneficiario = "";
    	String psBeneficiarioP = "";
    	//String psEquivalePersona = "";
    	String psCampoe = "";
    	String psRfc = "";
    	String psNombreEmpresa = "";
    	String importeNuevo = ""; 
    	//Map<String, Object> valorPagRef = new HashMap<String, Object>();
    	try {
    		if(movimientoDto.getIdDivisa()!= null && movimientoDto.getIdDivisa().trim().equals("MN"))
 	        	psDivisaLayout = "MXP";
 		    else if(movimientoDto.getIdDivisa()!= null && movimientoDto.getIdDivisa().trim().equals("DLS"))
 		    	psDivisaLayout = "USD";
 		    
    		/*String psIdChequera = "0127"+funciones.ajustarLongitudCampo("" + movimientoDto.getIdChequera(), 4, "I", "", "") +
    				"00"+funciones.ajustarLongitudCampo("" + movimientoDto.getIdChequera(), 10, "I", "", "");*/
    		String psIdChequera =  "0127"+movimientoDto.getIdChequera().substring(0, 4)+
					"00"+movimientoDto.getIdChequera().substring(movimientoDto.getIdChequera().length()-10, movimientoDto.getIdChequera().length());
    		
    		String nomEmpresa = funciones.quitarCaracteresRaros(movimientoDto.getNomEmpresa(), true);
 		   //	String rfcRea = funciones.quitarCaracteresRaros(movimientoDto.getRfc(), true);
 		   	if (!pbMismoBanco) {
				if (movimientoDto.getClabeBenef().trim().length() == 18) {
					lsTipoCtaBenef = "40";
					cdChequeraBenef = "00" + movimientoDto.getClabeBenef();
				}else if (movimientoDto.getIdChequeraBenef().trim().length() == 16 ) {
					lsTipoCtaBenef = "03";
					cdChequeraBenef = "0000" + movimientoDto.getIdChequeraBenef();
				}else{
					return ""; //termina
				}
				if(!pbInternacional){
						lsTipoPago = "01";
				}else{
					lsTipoPago = "05";
				}
			} else {
				
				if (movimientoDto.getIdChequeraBenef().trim().length() == 14) {
					lsTipoCtaBenef = "01";
					cdChequeraBenef = "0127"+movimientoDto.getIdChequeraBenef().substring(0, 4)+
							"00"+movimientoDto.getIdChequeraBenef().substring(movimientoDto.getIdChequeraBenef().length()-10, movimientoDto.getIdChequeraBenef().length());
					lsTipoPago = "03";
				} else if (movimientoDto.getIdChequeraBenef().trim().length() == 16) {
					lsTipoCtaBenef = "03";
					//cdChequeraBenef = "0000"+movimientoDto.getIdChequeraBenef().substring(0, 4)+
					//	"00"+movimientoDto.getIdChequeraBenef().substring(movimientoDto.getIdChequeraBenef().length()-10, movimientoDto.getIdChequeraBenef().length());
					cdChequeraBenef = "0000"+movimientoDto.getIdChequeraBenef();
					lsTipoPago = "04";
				}

			}
 		   	
 			String rfcBd = layoutsDao.obtieneRfcEmpresa("" + movimientoDto.getNoEmpresa());
 		   	String poHeaders = layoutsDao.obtieneDatosMovComplementarios(String.valueOf(movimientoDto.getNoFolioDet()));
 		   	//String valorReferenciado = poHeaders;
		   	String val = movimientoDto.getImporteStr();
		   	System.out.println("val  "+val);
			int a = val.indexOf(".");
			val = a==-1? val+"00":val.substring(0,a)+funciones.ajustarLongitudCampo(val.substring(a+1), 2, "I", "", "0");
			importeNuevo = funciones.ajustarLongitudCampo(val, 15, "D", "", "0");
			
			rfcBd = funciones.quitarCaracteresRaros(rfcBd, true);
 		   	//psReferencia= valorPagRef.get("valorPagRef").toString();
 		   	psBeneficiario = funciones.quitarCaracteresRaros(movimientoDto.getBeneficiario(), true);
 		   	//psEquivalePersona = funciones.quitarCaracteresRaros(movimientoDto.getEquivalePersona(), true);
 		   	//psEquivalePersona = funciones.quitarCaracteresRaros(movimientoDto.getNoCliente(), true);
 		   	String psBeneficiarioE = psBeneficiario;
 		   	psRfc = funciones.quitarCaracteresRaros(movimientoDto.getRfcBenef().toUpperCase(), true);
 		   	psNombreEmpresa = nomEmpresa.trim();
 		   	psNombreEmpresaP = poHeaders;//+" "+psNombreEmpresa;
 		   	psBeneficiarioP = poHeaders;//+psBeneficiario;
 		   	String noEmpresa = movimientoDto.getNoEmpresa()+"";
 		   	
 		   	psRegistro = "";
 		   	psRegistro += funciones.ajustarLongitudCampo( i+1+"", 7, "D", "", "0");
 		   	psRegistro += layoutsDao.fechaHoy().replace("-", "");
 		   	psRegistro += "02";
 		   	psRegistro += "01";
 		   	psRegistro += psIdChequera;
 		   	psRegistro += funciones.ajustarLongitudCampo("" + funciones.quitarCaracteresRaros(psNombreEmpresa,true), 40, "I", "", " "); 
 		   	psRegistro += funciones.ajustarLongitudCampo("" + funciones.quitarCaracteresRaros(rfcBd,true), 18, "I", "", " ");
 		   	psRegistro += funciones.ajustarLongitudCampo("" + funciones.quitarCaracteresRaros(psBeneficiarioP, true), 30, "I", "", " ");
 		   	psRegistro += funciones.ajustarLongitudCampo("" + importeNuevo, 15, "D", "", "0");
 		   	psRegistro += psDivisaLayout;
 		   	psRegistro += funciones.ajustarLongitudCampo("" + movimientoDto.getIdBancoBenef(), 3, "D", "", "0");
 		   	psRegistro += lsTipoCtaBenef;
 		   	psRegistro += cdChequeraBenef;
 		   	psRegistro += funciones.ajustarLongitudCampo("" + funciones.quitarCaracteresRaros(psBeneficiarioE, true), 40, "I", "", " ");
 		   	psRegistro += funciones.ajustarLongitudCampo("" + funciones.quitarCaracteresRaros(psNombreEmpresaP, true), 30, "I", "", " ");
 		   	psRegistro += funciones.ajustarLongitudCampo("" + funciones.quitarCaracteresRaros(psRfc, true), 18, "I", "", " ");
 		   	psRegistro += "00"; 
 		   	psRegistro += funciones.ajustarLongitudCampo("" + funciones.quitarCaracteresRaros(poHeaders, true), 40, "I", "", " ");
 		   	//psRegistro += funciones.ajustarLongitudCampo("" + movimientoDto.getNoFolioDet(), 45, "D", "", "0");
 		   	psRegistro += funciones.ajustarLongitudCampo("" + movimientoDto.getNoFolioDet(), 6, "D", "", "0");
 		   	//psRegistro += " ";
 		   	psRegistro += funciones.ajustarLongitudCampo("" + funciones.quitarCaracteresRaros(noEmpresa,true), 4, "D", "", "0");
 		   	psRegistro += "  ";
 		   	psRegistro += lsTipoPago;
 		   	psRegistro += funciones.ajustarLongitudCampo(psCampoe, 40, "I", "", "\u0020");
 		   	
 		   	
 		  
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutAztecaBusiness, M:beArmaAzteca");
			return "";
		}
    	return psRegistro;
    }
    
    public String beArmaAztecaT(BuscarSolicitudesTraspasosDto movimientoDto, int i, boolean pbMismoBanco, boolean pbInternacional){
    	String psRegistro = "";
    	//String psReferencia = "";
    	String psDivisaLayout = "";
    	String lsTipoCtaBenef = "";
    	String lsTipoPago = "";
    	//String lsIdChequeraOrig = "";
    	String psNombreEmpresaP = "";
    	String cdChequeraBenef = "";
    	String psBeneficiario = "";
    	String psBeneficiarioP = "";
    	//String psEquivalePersona = "";
    	String psCampoe = "";
    	String psRfc = "";
    	String psNombreEmpresa = "";
    	String importeNuevo = ""; 
    	//Map<String, Object> valorPagRef = new HashMap<String, Object>();
    	try {
    		System.out.println("traspasos azteca");
//    		if(movimientoDto.getIdDivisa()!= null && movimientoDto.getIdDivisa().trim().equals("MN"))
// 	        	psDivisaLayout = "MXP";
// 		    else if(movimientoDto.getIdDivisa()!= null && movimientoDto.getIdDivisa().trim().equals("DLS"))
// 		    	psDivisaLayout = "USD";
// 		    
//    		/*String psIdChequera = "0127"+funciones.ajustarLongitudCampo("" + movimientoDto.getIdChequera(), 4, "I", "", "") +
//    				"00"+funciones.ajustarLongitudCampo("" + movimientoDto.getIdChequera(), 10, "I", "", "");*/
//    		String psIdChequera =  "0127"+movimientoDto.getIdChequera().substring(0, 4)+
//					"00"+movimientoDto.getIdChequera().substring(movimientoDto.getIdChequera().length()-10, movimientoDto.getIdChequera().length());
//    		
//    		String nomEmpresa = funciones.quitarCaracteresRaros(movimientoDto.getNomEmpresa(), true);
// 		   //	String rfcRea = funciones.quitarCaracteresRaros(movimientoDto.getRfc(), true);
// 		   	if (!pbMismoBanco) {
//				if (movimientoDto.getClabeBenef().trim().length() == 18) {
//					lsTipoCtaBenef = "40";
//					cdChequeraBenef = "00" + movimientoDto.getClabeBenef();
//				}else if (movimientoDto.getIdChequeraBenef().trim().length() == 16 ) {
//					lsTipoCtaBenef = "03";
//					cdChequeraBenef = "0000" + movimientoDto.getIdChequeraBenef();
//				}else{
//					return ""; //termina
//				}
//				if(!pbInternacional){
//						lsTipoPago = "01";
//				}else{
//					lsTipoPago = "05";
//				}
//			} else {
//				
//				if (movimientoDto.getIdChequeraBenef().trim().length() == 14) {
//					lsTipoCtaBenef = "01";
//					cdChequeraBenef = "0127"+movimientoDto.getIdChequeraBenef().substring(0, 4)+
//							"00"+movimientoDto.getIdChequeraBenef().substring(movimientoDto.getIdChequeraBenef().length()-10, movimientoDto.getIdChequeraBenef().length());
//					lsTipoPago = "03";
//				} else if (movimientoDto.getIdChequeraBenef().trim().length() == 16) {
//					lsTipoCtaBenef = "03";
//					//cdChequeraBenef = "0000"+movimientoDto.getIdChequeraBenef().substring(0, 4)+
//					//	"00"+movimientoDto.getIdChequeraBenef().substring(movimientoDto.getIdChequeraBenef().length()-10, movimientoDto.getIdChequeraBenef().length());
//					cdChequeraBenef = "0000"+movimientoDto.getIdChequeraBenef();
//					lsTipoPago = "04";
//				}
//
//			}
    		if(movimientoDto.getIdDivisa()!= null && movimientoDto.getIdDivisa().trim().equals("MN"))
 	        	psDivisaLayout = "MXP";
 		    else if(movimientoDto.getIdDivisa()!= null && movimientoDto.getIdDivisa().trim().equals("DLS"))
 		    	psDivisaLayout = "USD";
 		    
    		/*String psIdChequera = "0127"+funciones.ajustarLongitudCampo("" + movimientoDto.getIdChequera(), 4, "I", "", "") +
    				"00"+funciones.ajustarLongitudCampo("" + movimientoDto.getIdChequera(), 10, "I", "", "");*/
    		String psIdChequera =  "0127"+movimientoDto.getIdChequera().substring(0, 4)+
					"00"+movimientoDto.getIdChequera().substring(movimientoDto.getIdChequera().length()-10, movimientoDto.getIdChequera().length());
    		
    		String nomEmpresa = funciones.quitarCaracteresRaros(movimientoDto.getNomEmpresa(), true);
 		   //	String rfcRea = funciones.quitarCaracteresRaros(movimientoDto.getRfc(), true);
 		   	if (!pbMismoBanco) {
				if (movimientoDto.getClabeBenef().trim().length() == 18) {
					lsTipoCtaBenef = "40";
					cdChequeraBenef = "00" + movimientoDto.getClabeBenef();
				}else if (movimientoDto.getIdChequeraBenef().trim().length() == 16 ) {
					lsTipoCtaBenef = "03";
					cdChequeraBenef = "0000" + movimientoDto.getIdChequeraBenef();
				}else{
					return ""; //termina
				}
				if(!pbInternacional){
						lsTipoPago = "01";
				}else{
					lsTipoPago = "05";
				}
			} else {
				
				if (movimientoDto.getIdChequeraBenef().trim().length() == 14) {
					lsTipoCtaBenef = "01";
					cdChequeraBenef = "0127"+movimientoDto.getIdChequeraBenef().substring(0, 4)+
							"00"+movimientoDto.getIdChequeraBenef().substring(movimientoDto.getIdChequeraBenef().length()-10, movimientoDto.getIdChequeraBenef().length());
					lsTipoPago = "03";
				} else if (movimientoDto.getIdChequeraBenef().trim().length() == 16) {
					lsTipoCtaBenef = "03";
					//cdChequeraBenef = "0000"+movimientoDto.getIdChequeraBenef().substring(0, 4)+
					//	"00"+movimientoDto.getIdChequeraBenef().substring(movimientoDto.getIdChequeraBenef().length()-10, movimientoDto.getIdChequeraBenef().length());
					cdChequeraBenef = "0000"+movimientoDto.getIdChequeraBenef();
					lsTipoPago = "04";
				}

			}
 		   	
 			
 		   	
 			String rfcBd = layoutsDao.obtieneRfcEmpresa("" + movimientoDto.getNoEmpresa());
 		   	String poHeaders = layoutsDao.obtieneDatosMovComplementarios(String.valueOf(movimientoDto.getNoFolioDet()));
 		   	//String valorReferenciado = poHeaders;
		   	
 		   	importeNuevo = funciones.ponerFormatoCeros(movimientoDto.getImporte(), 16);
 		   	
			rfcBd = funciones.quitarCaracteresRaros(rfcBd, true);
 		   	//psReferencia= valorPagRef.get("valorPagRef").toString();
 		   	psBeneficiario = funciones.quitarCaracteresRaros(movimientoDto.getBeneficiario(), true);
 		   	//psEquivalePersona = funciones.quitarCaracteresRaros(movimientoDto.getEquivalePersona(), true);
 		   	//psEquivalePersona = funciones.quitarCaracteresRaros(movimientoDto.getNoCliente(), true);
 		   	String psBeneficiarioE = psBeneficiario;
 		   	psRfc = funciones.quitarCaracteresRaros(movimientoDto.getRfcBenef().toUpperCase(), true);
 		   	psNombreEmpresa = nomEmpresa.trim();
 		   	psNombreEmpresaP = poHeaders;//+" "+psNombreEmpresa;
 		   	psBeneficiarioP = poHeaders;//+psBeneficiario;
 		   	String noEmpresa = movimientoDto.getNoEmpresa()+"";
 		   System.out.println("traspasos azteca");
 		   	psRegistro = "";
 		   	psRegistro += funciones.ajustarLongitudCampo( i+1+"", 7, "D", "", "0");
 		   	psRegistro += layoutsDao.fechaHoy().replace("-", "");
 		   	psRegistro += "02";
 		   	psRegistro += "01";
 		   	psRegistro += psIdChequera;
 		   	psRegistro += funciones.ajustarLongitudCampo("" + funciones.quitarCaracteresRaros(psNombreEmpresa, true), 40, "I", "", " "); 
 		   	psRegistro += funciones.ajustarLongitudCampo("" + rfcBd, 18, "I", "", " ");
 		   	psRegistro += funciones.ajustarLongitudCampo("" + funciones.quitarCaracteresRaros(psBeneficiarioP,true), 30, "I", "", " ");
 		   	psRegistro += funciones.ajustarLongitudCampo("" + importeNuevo.replace(".", ""), 15, "D", "", "0");
 		   	psRegistro += psDivisaLayout;
 		   	psRegistro += funciones.ajustarLongitudCampo("" + movimientoDto.getIdBancoBenef(), 3, "D", "", "0");
 		   	psRegistro += lsTipoCtaBenef;
 		   	psRegistro += cdChequeraBenef;
 		   	psRegistro += funciones.ajustarLongitudCampo("" + psBeneficiarioE, 40, "I", "", " ");
 		   	psRegistro += funciones.ajustarLongitudCampo("" + funciones.quitarCaracteresRaros(psNombreEmpresaP, true), 30, "I", "", " ");
 		   	psRegistro += funciones.ajustarLongitudCampo("" + psRfc, 18, "I", "", " ");
 		   	psRegistro += "00"; 
 		   	psRegistro += funciones.ajustarLongitudCampo("" + funciones.quitarCaracteresRaros(poHeaders,true), 40, "I", "", " ");
 		   	//psRegistro += funciones.ajustarLongitudCampo("" + movimientoDto.getNoFolioDet(), 45, "D", "", "0");
 		   	psRegistro += funciones.ajustarLongitudCampo("" + movimientoDto.getNoFolioDet(), 6, "D", "", "0");
 		   	//psRegistro += " ";
 		   	psRegistro += funciones.ajustarLongitudCampo("" + funciones.quitarCaracteresRaros(noEmpresa, true), 4, "D", "", "0");
 		   	psRegistro += "  ";
 		   	psRegistro += lsTipoPago;
 		   	psRegistro += funciones.ajustarLongitudCampo(psCampoe, 40, "I", "", "\u0020");
 		   	
 		   System.out.println("traspasos azteca fin");
 		  
		} catch (Exception e) {
			System.out.println("traspasos exception");
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutAztecaBusiness, M:beArmaAzteca");
			return "";
		}
  
    	return psRegistro;
    }

    public String beArmaAztecaAgrupado(MovimientoDto movimientoDto, int i, boolean pbMismoBanco, boolean pbInternacional){
    	String psRegistro = "";
    	//String psReferencia = "";
    	String psDivisaLayout = "";
    	String lsTipoCtaBenef = "";
    	String lsTipoPago = "";
    	//String lsIdChequeraOrig = "";
    	String psNombreEmpresaP = "";
    	String cdChequeraBenef = "";
    	String psBeneficiario = "";
    	String psBeneficiarioP = "";
    	//String psEquivalePersona = "";
    	String psCampoe = "";
    	String psRfc = "";
    	String psNombreEmpresa = "";
    	String importeNuevo = ""; 
    	//Map<String, Object> valorPagRef = new HashMap<String, Object>();
    	try {
    		if(movimientoDto.getIdDivisa()!= null && movimientoDto.getIdDivisa().trim().equals("MN"))
 	        	psDivisaLayout = "MXP";
 		    else if(movimientoDto.getIdDivisa()!= null && movimientoDto.getIdDivisa().trim().equals("DLS"))
 		    	psDivisaLayout = "USD";
 		    
    		/*String psIdChequera = "0127"+funciones.ajustarLongitudCampo("" + movimientoDto.getIdChequera(), 4, "I", "", "") +
    				"00"+funciones.ajustarLongitudCampo("" + movimientoDto.getIdChequera(), 10, "I", "", "");*/
    		String psIdChequera =  "0127"+movimientoDto.getIdChequera().substring(0, 4)+
					"00"+movimientoDto.getIdChequera().substring(movimientoDto.getIdChequera().length()-10, movimientoDto.getIdChequera().length());
    		
    		String nomEmpresa = funciones.quitarCaracteresRaros(movimientoDto.getNomEmpresa(), true);
 		   //	String rfcRea = funciones.quitarCaracteresRaros(movimientoDto.getRfc(), true);
 		   	if (!pbMismoBanco) {
				if (movimientoDto.getClabeBenef().trim().length() == 18) {
					lsTipoCtaBenef = "40";
					cdChequeraBenef = "00" + movimientoDto.getClabeBenef();
				}else if (movimientoDto.getIdChequeraBenef().trim().length() == 16 ) {
					lsTipoCtaBenef = "03";
					cdChequeraBenef = "0000" + movimientoDto.getIdChequeraBenef();
				}else{
					return ""; //termina
				}
				if(!pbInternacional){
						lsTipoPago = "01";
				}else{
					lsTipoPago = "05";
				}
			} else {
				
				if (movimientoDto.getIdChequeraBenef().trim().length() == 14) {
					lsTipoCtaBenef = "01";
					cdChequeraBenef = "0127"+movimientoDto.getIdChequeraBenef().substring(0, 4)+
							"00"+movimientoDto.getIdChequeraBenef().substring(movimientoDto.getIdChequeraBenef().length()-10, movimientoDto.getIdChequeraBenef().length());
					lsTipoPago = "03";
				} else if (movimientoDto.getIdChequeraBenef().trim().length() == 16) {
					lsTipoCtaBenef = "03";
					//cdChequeraBenef = "0000"+movimientoDto.getIdChequeraBenef().substring(0, 4)+
					//	"00"+movimientoDto.getIdChequeraBenef().substring(movimientoDto.getIdChequeraBenef().length()-10, movimientoDto.getIdChequeraBenef().length());
					cdChequeraBenef = "0000"+movimientoDto.getIdChequeraBenef();
					lsTipoPago = "04";
				}

			}
 		   	
 			String rfcBd =  movimientoDto.getRfc();
 		   	String poHeaders = layoutsDao.obtenerReferenciaAgrupada(movimientoDto.getNoEmpresa(),
 		   			movimientoDto.getIdBanco(), movimientoDto.getIdChequera(), movimientoDto.getPoHeaders());

		   	String val = movimientoDto.getImporteStr();
		   	System.out.println("val  "+val);
			int a = val.indexOf(".");
			val = a==-1? val+"00":val.substring(0,a)+funciones.ajustarLongitudCampo(val.substring(a+1), 2, "I", "", "0");
			importeNuevo = funciones.ajustarLongitudCampo(val, 15, "D", "", "0");
			
			rfcBd = funciones.quitarCaracteresRaros(rfcBd, true);
 		   	//psReferencia= valorPagRef.get("valorPagRef").toString();
 		   	psBeneficiario = funciones.quitarCaracteresRaros(movimientoDto.getBeneficiario(), true);
 		   	//psEquivalePersona = funciones.quitarCaracteresRaros(movimientoDto.getEquivalePersona(), true);
 		   	//psEquivalePersona = funciones.quitarCaracteresRaros(movimientoDto.getNoCliente(), true);
 		   	String psBeneficiarioE = psBeneficiario;
 		   	psRfc = funciones.quitarCaracteresRaros(movimientoDto.getRfcBenef().toUpperCase(), true);
 		   	psNombreEmpresa = nomEmpresa.trim();
 		   	psNombreEmpresaP = poHeaders;//+" "+psNombreEmpresa;
 		   	psBeneficiarioP = poHeaders;//+psBeneficiario;
 		   	String noEmpresa = movimientoDto.getNoEmpresa()+"";
 		   	
 		   	psRegistro = "";
 		   	psRegistro += funciones.ajustarLongitudCampo( i+1+"", 7, "D", "", "0");
 		   	psRegistro += layoutsDao.fechaHoy().replace("-", "");
 		   	psRegistro += "02";
 		   	psRegistro += "01";
 		   	psRegistro += psIdChequera;
 		   	psRegistro += funciones.ajustarLongitudCampo("" + funciones.quitarCaracteresRaros(psNombreEmpresa,true), 40, "I", "", " "); 
 		   	psRegistro += funciones.ajustarLongitudCampo("" + funciones.quitarCaracteresRaros(rfcBd,true), 18, "I", "", " ");
 		   	psRegistro += funciones.ajustarLongitudCampo("" + funciones.quitarCaracteresRaros(psBeneficiarioP, true), 30, "I", "", " ");
 		   	psRegistro += funciones.ajustarLongitudCampo("" + importeNuevo, 15, "D", "", "0");
 		   	psRegistro += psDivisaLayout;
 		   	psRegistro += funciones.ajustarLongitudCampo("" + movimientoDto.getIdBancoBenef(), 3, "D", "", "0");
 		   	psRegistro += lsTipoCtaBenef;
 		   	psRegistro += cdChequeraBenef;
 		   	psRegistro += funciones.ajustarLongitudCampo("" + funciones.quitarCaracteresRaros(psBeneficiarioE, true), 40, "I", "", " ");
 		   	psRegistro += funciones.ajustarLongitudCampo("" + funciones.quitarCaracteresRaros(psNombreEmpresaP, true), 30, "I", "", " ");
 		   	psRegistro += funciones.ajustarLongitudCampo("" + funciones.quitarCaracteresRaros(psRfc, true), 18, "I", "", " ");
 		   	psRegistro += "00"; 
 		   	psRegistro += funciones.ajustarLongitudCampo("" + funciones.quitarCaracteresRaros(poHeaders, true), 40, "I", "", " ");
 		   	//psRegistro += funciones.ajustarLongitudCampo("" + movimientoDto.getNoFolioDet(), 45, "D", "", "0");
 		   	psRegistro += funciones.ajustarLongitudCampo("" + movimientoDto.getNoFolioDet(), 6, "D", "", "0");
 		   	//psRegistro += " ";
 		   	psRegistro += funciones.ajustarLongitudCampo("" + funciones.quitarCaracteresRaros(noEmpresa,true), 4, "D", "", "0");
 		   	psRegistro += "  ";
 		   	psRegistro += lsTipoPago;
 		   	psRegistro += funciones.ajustarLongitudCampo(psCampoe, 40, "I", "", "\u0020");
 		   	
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutAztecaBusiness, M:beArmaAzteca");
			return "";
		}
  
    	return psRegistro;
    }

	public String validaFormato(String psCampo, String formato) {
		try {
			if(formato.equals("999")) psCampo = funciones.ponerFormatoCeros(Double.parseDouble(psCampo), 2).replace(".", "");	//le quita el punto de decimales
			else if(formato.equals("9.99")) psCampo = psCampo.indexOf(".") != -1 ? funciones.ponerFormatoCeros(Double.parseDouble(psCampo), 2) : psCampo + ".00";			//le coloca el punto con 2 decimales
			else if(formato.equals("dd/mm/yyyy")) psCampo = funciones.cambiarFecha(psCampo, true);
				//	Victor comento else if(formato.equals("ddMMyyyy")) psCampo = funciones.cambiarFecha(psCampo, true).replace("/", "");
			else if(formato.equals("ddMMyyyy")) psCampo = psCampo.replace("/", "");
			else if(formato.equals("ddMMyy")) {
				String fecha = funciones.cambiarFecha(psCampo, true).replace("/", "");
				psCampo = fecha.substring(0, 4) + fecha.substring(6);
			}
			else if(formato.equals("yyyyMMdd")) psCampo = psCampo.substring(6, 10) + psCampo.substring(3,5) + psCampo.substring(0,2);
			else if(formato.equals("yyMMdd")) psCampo = psCampo.substring(8, 10) + psCampo.substring(3,5) + psCampo.substring(0,2);
			else if(formato.equals("MMddyyyy")) psCampo = psCampo.substring(3,5) + psCampo.substring(0,2) + psCampo.substring(6, 10);
			else if(formato.equals("hhmm")) psCampo = psCampo.replace(":", "");
			else if(formato.equals("00000")) psCampo = funciones.ponerCeros(psCampo, 5);
			//else if(!formato.equals("")) psCampo = psCampo;
			/*	If formato <> "" Then
	                campo = Format(campo, formato)
	            End If
	        */
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:validaFormato");
		}
		return psCampo;
	}
	public LayoutsDao getLayoutsDao() {
		return layoutsDao;
	}

	public void setLayoutsDao(LayoutsDao layoutsDao) {
		this.layoutsDao = layoutsDao;
	}

	public EnvioTransferenciasDao getEnvioTransferenciasDao() {
		return envioTransferenciasDao;
	}

	public void setEnvioTransferenciasDao(EnvioTransferenciasDao envioTransferenciasDao) {
		this.envioTransferenciasDao = envioTransferenciasDao;
	}
	

}
