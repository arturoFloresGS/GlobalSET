package com.webset.set.layout.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webset.set.bancaelectronica.dao.EnvioTransferenciasDao;
import com.webset.set.bancaelectronica.dto.CriterioBusquedaDto;
import com.webset.set.layout.dao.LayoutsDao;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.MovimientoDto;

public class LayoutScotiabankInverlatBusiness {
	Bitacora bitacora = new Bitacora();
	Funciones funciones = new Funciones();
	ConsultasGenerales consultasGrales;
	private EnvioTransferenciasDao envioTransferenciasDao;
	

	private LayoutsDao layoutsDao;
	List<CriterioBusquedaDto> structBE = new ArrayList<CriterioBusquedaDto>();
    List<CriterioBusquedaDto> structEquivaleCampo = new ArrayList<CriterioBusquedaDto>();
    List<CriterioBusquedaDto> structEquivaleBE = new ArrayList<CriterioBusquedaDto>();
    
    CreacionArchivosBusiness creacionArchivosBusiness = new CreacionArchivosBusiness();
    
    Map<String, Object> mapResp = new HashMap<String, Object>();
	int iBanco = 0;
    int liTipoTransfer = 0;
    
    
    public String beArmaScotiaBankInverlat(MovimientoDto movimientoDto, int i, boolean mismo, boolean pbInternacional){
    	StringBuffer psRegistro = new StringBuffer();
    	String psCampo = "";
    	String psTipoCtaCargo = "CHQ";
    	String psTipoCtaAbono = "CHQ";
    	String psMonedaCargo = "";
    	String psCvePoblacion = "";
    	String psCveEdo = "";
    	String psBeneficiario = "";
    	String psTelBeneficiario = "";
    	String psTipoPersona = "";
    	String pdImporteIVA = "";
    	String psTipoEnvio = "";
    	String psInstruccionPago = "";
    	String psSucursal = "";
    	String psConcepto = "";
    	psRegistro.append("");

		try {
			if(movimientoDto.getIdDivisa()!= null && movimientoDto.getIdDivisa().trim().equals("MN"))
				psMonedaCargo = "MXN";
 		    else 
 		    	psMonedaCargo = "DLS";
			
			psCveEdo = "00"; //'Deben de ir sólo Ceros.
	        psCvePoblacion = "000"; //'Deben de ir sólo Ceros.
	        psTelBeneficiario =funciones.ajustarLongitudCampo(psCampo, 10, "D", "", "0");
	        psTipoPersona = " ";
	        pdImporteIVA = "0000000000000.00";
	        psTipoEnvio = " ";
	        psInstruccionPago = "3"; 
	        psSucursal = movimientoDto.getSucOrigen().trim();
	        psBeneficiario = funciones.quitarCaracteresRaros(movimientoDto.getBeneficiario(), true); 
	        psConcepto =  movimientoDto.getNoFolioDet() != 0 ? movimientoDto.getNoFolioDet()+"":"";
	        String rfcBeneficiario = funciones.quitarCaracteresRaros(movimientoDto.getRfcBenef()+"", true);
	        if (!mismo) {
	        	psRegistro.append(funciones.ajustarLongitudCampo(movimientoDto.getNomEmpresa()+"", 30, "D", "", " "));
	        	psCampo = "";
	        	psRegistro.append(funciones.ajustarLongitudCampo(psTelBeneficiario, 10, "D", "", " "));
	        	//psRegistro.append(funciones.ajustarLongitudCampo(psCampo, 10, "D", "", " "));
	        	psRegistro.append(psTipoCtaCargo);
	        	psRegistro.append(psMonedaCargo);
	        	psRegistro.append(funciones.ajustarLongitudCampo(movimientoDto.getPlaza()+"", 3, "D", "", "0"));
	        	psRegistro.append(funciones.ajustarLongitudCampo("" + movimientoDto.getIdChequera(), 17, "D", "", "0"));
	        	
				psRegistro.append(funciones.ponerFormatoCeros(movimientoDto.getImporte(), 17));
				psRegistro.append(psCveEdo);
				psRegistro.append(psCvePoblacion);
				psCampo = layoutsDao.obtenerInstiticionFinanciera(movimientoDto.getNoFolioDet()+"");
				psRegistro.append(funciones.ajustarLongitudCampo(psCampo, 3, "D", "", " "));
				psRegistro.append(funciones.ajustarLongitudCampo(psBeneficiario, 35, "I", "", " "));
				psRegistro.append(psTelBeneficiario);
				psRegistro.append(funciones.ajustarLongitudCampo(movimientoDto.getIdChequeraBenef(), 20, "D", "", "0"));
				psRegistro.append(funciones.ajustarLongitudCampo(psConcepto, 20, "I", "", " "));
				psRegistro.append(psInstruccionPago);
				psRegistro.append(funciones.ajustarLongitudCampo(psSucursal, 5, "I", "", " "));
				psRegistro.append(psTipoPersona);
				psRegistro.append(funciones.ajustarLongitudCampo(rfcBeneficiario, 13, "I", "", " "));
				psRegistro.append(pdImporteIVA);
				psRegistro.append(psTipoEnvio);
				//psRegistro.append(layoutsDao.fechaHoy().replace("-", "/"));
				String cadenan = "";
				cadenan = layoutsDao.fechaHoy();
				psRegistro.append(cadenan.replace("-", ""));
				psRegistro.append(funciones.ajustarLongitudCampo(movimientoDto.getNoFolioDet()+"", 7, "D", "", "0"));
			} else {
				//'registro transaccion al mismo banco
				
				psRegistro.append(psTipoCtaCargo);
				psRegistro.append(funciones.ajustarLongitudCampo(movimientoDto.getPlaza()+"", 3, "D", "", "0"));
				psRegistro.append(funciones.ajustarLongitudCampo("" + movimientoDto.getIdChequera(), 17, "D", "", "0"));
				psRegistro.append(psTipoCtaAbono);
				psRegistro.append(funciones.ajustarLongitudCampo(movimientoDto.getPlazaBenef()+"", 3, "D", "", "0"));
				psRegistro.append(funciones.ajustarLongitudCampo(movimientoDto.getIdChequeraBenef(), 17, "D", "", "0"));
				psRegistro.append(psMonedaCargo);
				//////////////////////
	        	/*String val = movimientoDto.getImporte()+"";
				//System.out.println("este es el importe-"+val+"-Este es el importe");
				int a = val.indexOf(".");
			//	System.out.println("Estre es a"+a);
				String cadena = val.substring(0, a);
				String cadena1 = val.substring(a+1, val.length());
				//System.out.println("cadena uno "+cadena);
				//System.out.println("cadena dos "+cadena1);
				if( a == -1){
					System.out.println("aa");
					val = val.substring(0, a+3);
					psRegistro.append(funciones.ajustarLongitudCampo(val, 17, "D", "", "0"));
				}else{
					System.out.println("bb");
					
					psRegistro.append(funciones.ajustarLongitudCampo(cadena, 14, "D", "", "0"));
					psRegistro.append(".");
					psRegistro.append(funciones.ajustarLongitudCampo(cadena1, 2, "I", "", "0"));
				}*/
				/////////////////////////psRegistro.append(funciones.ponerFormatoCeros(dto.getImporte(), 13));	
				psRegistro.append(funciones.ponerFormatoCeros(movimientoDto.getImporte(), 17));
				psRegistro.append(funciones.ajustarLongitudCampo(movimientoDto.getNoFolioDet()+"", 25, "I", "", " "));
				psRegistro.append(funciones.ajustarLongitudCampo(movimientoDto.getNoFolioDet()+"", 10, "D", "", "0"));
				psRegistro.append(psTipoPersona);
				psRegistro.append(funciones.ajustarLongitudCampo(rfcBeneficiario, 13, "I", "", " "));
				psRegistro.append(pdImporteIVA);
				String cadenan = "";
				cadenan = layoutsDao.fechaHoy();
				psRegistro.append(cadenan.replace("-", ""));

			}
	
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " +
			e.toString() + "P:com.webset.set.layout, C:LayoutScotiabankInverlat, M:beArmaScotiaBankInverlat");
		}
		System.out.println("cadena de arma banorte lauout ----"+psRegistro.toString());
    	return psRegistro.toString();
    }
    
    public String beArmaScotiaBankInverlatAgrupado(MovimientoDto movimientoDto, int i, boolean mismo, boolean pbInternacional){
    	StringBuffer psRegistro = new StringBuffer();
    	String psCampo = "";
    	String psTipoCtaCargo = "CHQ";
    	String psTipoCtaAbono = "CHQ";
    	String psMonedaCargo = "";
    	String psCvePoblacion = "";
    	String psCveEdo = "";
    	String psBeneficiario = "";
    	String psTelBeneficiario = "";
    	String psTipoPersona = "";
    	String pdImporteIVA = "";
    	String psTipoEnvio = "";
    	String psInstruccionPago = "";
    	String psSucursal = "";
    	String psConcepto = "";
    	psRegistro.append("");

		try {
			if(movimientoDto.getIdDivisa()!= null && movimientoDto.getIdDivisa().trim().equals("MN"))
				psMonedaCargo = "MXN";
 		    else 
 		    	psMonedaCargo = "DLS";
			
			psCveEdo = "00"; //'Deben de ir sólo Ceros.
	        psCvePoblacion = "000"; //'Deben de ir sólo Ceros.
	        psTelBeneficiario =funciones.ajustarLongitudCampo(psCampo, 10, "D", "", "0");
	        psTipoPersona = " ";
	        pdImporteIVA = "0000000000000.00";
	        psTipoEnvio = " ";
	        psInstruccionPago = "3"; 
	        psSucursal = movimientoDto.getSucOrigen().trim();
	        psBeneficiario = funciones.quitarCaracteresRaros(movimientoDto.getBeneficiario(), true); 
	        
	        String rfcBeneficiario = funciones.quitarCaracteresRaros(movimientoDto.getRfcBenef()+"", true);
	        if (!mismo) {
	        	psConcepto =  movimientoDto.getPoHeaders() != null ? movimientoDto.getPoHeaders()+"":"";
	        	psRegistro.append(funciones.ajustarLongitudCampo(movimientoDto.getNomEmpresa()+"", 30, "D", "", " "));
	        	psCampo = "";
	        	psRegistro.append(funciones.ajustarLongitudCampo(psTelBeneficiario, 10, "D", "", " "));
	        	//psRegistro.append(funciones.ajustarLongitudCampo(psCampo, 10, "D", "", " "));
	        	psRegistro.append(psTipoCtaCargo);
	        	psRegistro.append(psMonedaCargo);
	        	psRegistro.append(funciones.ajustarLongitudCampo(movimientoDto.getPlaza()+"", 3, "D", "", "0"));
	        	psRegistro.append(funciones.ajustarLongitudCampo("" + movimientoDto.getIdChequera(), 17, "D", "", "0"));
	        	
				psRegistro.append(funciones.ponerFormatoCeros(movimientoDto.getImporte(), 17));
				psRegistro.append(psCveEdo);
				psRegistro.append(psCvePoblacion);
				psCampo = movimientoDto.getInstFinan() != null ? movimientoDto.getInstFinan() : "";
				psRegistro.append(funciones.ajustarLongitudCampo(psCampo, 3, "D", "", " "));
				psRegistro.append(funciones.ajustarLongitudCampo(psBeneficiario, 35, "I", "", " "));
				psRegistro.append(psTelBeneficiario);
				psRegistro.append(funciones.ajustarLongitudCampo(movimientoDto.getIdChequeraBenef(), 20, "D", "", "0"));
				psRegistro.append(funciones.ajustarLongitudCampo(psConcepto, 20, "I", "", " "));
				psRegistro.append(psInstruccionPago);
				psRegistro.append(funciones.ajustarLongitudCampo(psSucursal, 5, "I", "", " "));
				psRegistro.append(psTipoPersona);
				psRegistro.append(funciones.ajustarLongitudCampo(rfcBeneficiario, 13, "I", "", " "));
				psRegistro.append(pdImporteIVA);
				psRegistro.append(psTipoEnvio);
				//psRegistro.append(layoutsDao.fechaHoy().replace("-", "/"));
				String cadenan = "";
				cadenan = layoutsDao.fechaHoy();
				psRegistro.append(cadenan.replace("-", ""));
				psRegistro.append(funciones.ajustarLongitudCampo(movimientoDto.getPoHeaders()+"", 7, "D", "", "0"));
			} else {
				//'registro transaccion al mismo banco
				
				psRegistro.append(psTipoCtaCargo);
				psRegistro.append(funciones.ajustarLongitudCampo(movimientoDto.getPlaza()+"", 3, "D", "", "0"));
				psRegistro.append(funciones.ajustarLongitudCampo("" + movimientoDto.getIdChequera(), 17, "D", "", "0"));
				psRegistro.append(psTipoCtaAbono);
				psRegistro.append(funciones.ajustarLongitudCampo(movimientoDto.getPlazaBenef()+"", 3, "D", "", "0"));
				psRegistro.append(funciones.ajustarLongitudCampo(movimientoDto.getIdChequeraBenef(), 17, "D", "", "0"));
				psRegistro.append(psMonedaCargo);
				
				psRegistro.append(funciones.ponerFormatoCeros(movimientoDto.getImporte(), 17));
				psRegistro.append(funciones.ajustarLongitudCampo(movimientoDto.getPoHeaders()+"", 25, "I", "", " "));
				psRegistro.append(funciones.ajustarLongitudCampo(movimientoDto.getPoHeaders()+"", 10, "D", "", "0"));
				psRegistro.append(psTipoPersona);
				psRegistro.append(funciones.ajustarLongitudCampo(rfcBeneficiario, 13, "I", "", " "));
				psRegistro.append(pdImporteIVA);
				String cadenan = "";
				cadenan = layoutsDao.fechaHoy();
				psRegistro.append(cadenan.replace("-", ""));

			}
	
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " +
			e.toString() + "P:com.webset.set.layout, C:LayoutScotiabankInverlat, M:beArmaScotiaBankInverlat");
		}
		System.out.println("cadena de arma banorte lauout ----"+psRegistro.toString());
    	return psRegistro.toString();
    }

	public EnvioTransferenciasDao getEnvioTransferenciasDao() {
		return envioTransferenciasDao;
	}
	public void setEnvioTransferenciasDao(EnvioTransferenciasDao envioTransferenciasDao) {
		this.envioTransferenciasDao = envioTransferenciasDao;
	}
	 public LayoutsDao getLayoutsDao() {
		return layoutsDao;
	}
	public void setLayoutsDao(LayoutsDao layoutsDao) {
		this.layoutsDao = layoutsDao;
	}
    
}
