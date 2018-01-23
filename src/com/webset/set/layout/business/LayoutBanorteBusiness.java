package com.webset.set.layout.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webset.set.bancaelectronica.dao.EnvioTransferenciasDao;
import com.webset.set.bancaelectronica.dto.CriterioBusquedaDto;
import com.webset.set.layout.dao.LayoutsDao;
import com.webset.set.traspasos.dto.BuscarSolicitudesTraspasosDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.MovimientoDto;

public class LayoutBanorteBusiness {

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
    
    
    public String beArmaBanorte(MovimientoDto movimientoDto, int i, boolean mismo, boolean pbInternacional){
    	StringBuffer psRegistro = new StringBuffer();
    	String psCampo = "";
    	String psMonedaOrigen = "";
    	String psMonedaDestino = "";
    	String psTipo = "";
    	String psCchequera = "";
    	String psNombreEmpresa = "";
    	psRegistro.append("");
    	String psBeneficiario = "";
    	//Map<String, Object> valorPagRef = new HashMap<String, Object>();
		try {
			if (mismo) {
				psTipo = "01";
				psCchequera = movimientoDto.getIdChequeraBenef();
			} else {
				psTipo = "04";
				psCchequera = movimientoDto.getClabeBenef();
			}
			String nomEmpresa = funciones.quitarCaracteresRaros(movimientoDto.getNomEmpresa(), true);
			psNombreEmpresa = nomEmpresa.trim();
			//valorPagRef = layoutsDao.obtenerValorPagoReferenciado(Integer.parseInt(movimientoDto.getNoCliente()),movimientoDto.getNoFolioDet(), "");
			//psReferencia= valorPagRef.get("valorPagRef").toString();
    		if(movimientoDto.getIdDivisa()!= null && movimientoDto.getIdDivisa().trim().equals("MN")){
    			psMonedaOrigen = "1";
    	        psMonedaDestino = "1";
    		}else {
 		    	psMonedaOrigen = "2";
 		        psMonedaDestino = "2";
 		    }
    		String rfcRea = funciones.quitarCaracteresRaros(movimientoDto.getRfc(), true);
    		psBeneficiario = funciones.quitarCaracteresRaros(movimientoDto.getBeneficiario(), true);
    		
    		psRegistro.append("");
    		//psRegistro.append(movimientoDto.getDescCveOperacion());
    		psRegistro.append(psTipo);
    		psRegistro.append(funciones.ajustarLongitudCampo("" + psNombreEmpresa, 13, "D", "", "0"));
    		psRegistro.append(funciones.ajustarLongitudCampo("" + movimientoDto.getIdChequera(), 20, "D", "", "0"));
    		psRegistro.append(funciones.ajustarLongitudCampo("" + psCchequera, 20, "D", "", "0"));
    		psCampo = funciones.ponerFormatoCeros((movimientoDto.getImporte() * 100), 14);
    		psRegistro.append(funciones.ajustarLongitudCampo("" +psCampo , 14, "D", "", "0"));
    		psRegistro.append(funciones.ajustarLongitudCampo("" + movimientoDto.getNoFolioDet(), 10, "D", "", "0"));
    		psRegistro.append(funciones.ajustarLongitudCampo("" + funciones.quitarCaracteresRaros(movimientoDto.getConcepto(), true), 30, "I", "", " "));
    		psRegistro.append(psMonedaOrigen);
    		psRegistro.append(psMonedaDestino);
    		psRegistro.append(funciones.ajustarLongitudCampo("" + rfcRea, 13, "I", "", " "));
    		psRegistro.append(funciones.ajustarLongitudCampo(psCampo, 14, "D", "", "0"));
    		psCampo = "";
    		psRegistro.append(funciones.ajustarLongitudCampo(psCampo, 39, "D", "", " "));
    		System.out.println("fecha "+layoutsDao.fechaHoy());
    		psRegistro.append(layoutsDao.fechaHoy().replace("-", ""));//psRegistro += layoutsDao.fechaHoy().replace("-", "");
    		psRegistro.append(funciones.ajustarLongitudCampo("" + psBeneficiario, 70, "I", "", " "));
    		psRegistro.append("");
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " +
			e.toString() + "P:com.webset.set.layout, C:LayoutBanorteBusiness, M:beArmaBanorte");
		}
		System.out.println("cadena de arma banorte lauout ----"+psRegistro.toString());
    	return psRegistro.toString();
    }
    

    public String beArmaBanorteT(BuscarSolicitudesTraspasosDto movimientoDto, int i, boolean mismo, boolean pbInternacional){
    	StringBuffer psRegistro = new StringBuffer();
    	String psCampo = "";
    	String psMonedaOrigen = "";
    	String psMonedaDestino = "";
    	String psTipo = "";
    	String psCchequera = "";
    	String psNombreEmpresa = "";
    	psRegistro.append("");
    	String psBeneficiario = "";
    	//Map<String, Object> valorPagRef = new HashMap<String, Object>();
		try {
			if (mismo) {
				psTipo = "01";
				psCchequera = movimientoDto.getIdChequeraBenef();
			} else {
				psTipo = "04";
				psCchequera = movimientoDto.getClabeBenef();
			}
			String nomEmpresa = funciones.quitarCaracteresRaros(movimientoDto.getNomEmpresa(), true);
			psNombreEmpresa = nomEmpresa.trim();
			//valorPagRef = layoutsDao.obtenerValorPagoReferenciado(Integer.parseInt(movimientoDto.getNoCliente()),movimientoDto.getNoFolioDet(), "");
			//psReferencia= valorPagRef.get("valorPagRef").toString();
    		if(movimientoDto.getIdDivisa()!= null && movimientoDto.getIdDivisa().trim().equals("MN")){
    			psMonedaOrigen = "1";
    	        psMonedaDestino = "1";
    		}else {
 		    	psMonedaOrigen = "2";
 		        psMonedaDestino = "2";
 		    }
    		String rfcRea = funciones.quitarCaracteresRaros(movimientoDto.getRfc(), true);
    		psBeneficiario = funciones.quitarCaracteresRaros(movimientoDto.getBeneficiario(), true);
    		
    		psRegistro.append("");
    		//psRegistro.append(movimientoDto.getDescCveOperacion());
    		psRegistro.append(psTipo);
    		psRegistro.append(funciones.ajustarLongitudCampo("" + psNombreEmpresa, 13, "D", "", "0"));
    		psRegistro.append(funciones.ajustarLongitudCampo("" + movimientoDto.getIdChequera(), 20, "D", "", "0"));
    		psRegistro.append(funciones.ajustarLongitudCampo("" + psCchequera, 20, "D", "", "0"));
    		psCampo = funciones.ponerFormatoCeros((movimientoDto.getImporte() * 100), 14);
    		psRegistro.append(funciones.ajustarLongitudCampo("" +psCampo , 14, "D", "", "0"));
    		psRegistro.append(funciones.ajustarLongitudCampo("" + movimientoDto.getNoFolioDet(), 10, "D", "", "0"));
    		psRegistro.append(funciones.ajustarLongitudCampo("" + funciones.quitarCaracteresRaros(movimientoDto.getConcepto(), true), 30, "I", "", " "));
    		psRegistro.append(psMonedaOrigen);
    		psRegistro.append(psMonedaDestino);
    		psRegistro.append(funciones.ajustarLongitudCampo("" + rfcRea, 13, "I", "", " "));
    		psRegistro.append(funciones.ajustarLongitudCampo(psCampo, 14, "D", "", "0"));
    		psCampo = "";
    		psRegistro.append(funciones.ajustarLongitudCampo(psCampo, 39, "D", "", " "));
    		System.out.println("fecha "+layoutsDao.fechaHoy());
    		psRegistro.append(layoutsDao.fechaHoy().replace("-", ""));//psRegistro += layoutsDao.fechaHoy().replace("-", "");
    		psRegistro.append(funciones.ajustarLongitudCampo("" + psBeneficiario, 70, "I", "", " "));
    		psRegistro.append("");
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " +
			e.toString() + "P:com.webset.set.layout, C:LayoutBanorteBusiness, M:beArmaBanorte");
		}
		System.out.println("cadena de arma banorte lauout ----"+psRegistro.toString());
    	return psRegistro.toString();
    }
    
    public String beArmaBanorteAgrupados(MovimientoDto movimientoDto, int i, boolean mismo, boolean pbInternacional){
    	StringBuffer psRegistro = new StringBuffer();
    	String psCampo = "";
    	String psMonedaOrigen = "";
    	String psMonedaDestino = "";
    	String psTipo = "";
    	String psCchequera = "";
    	String psNombreEmpresa = "";
    	psRegistro.append("");
    	String psBeneficiario = "";
    	
		try {
			if (mismo) {
				psTipo = "01";
				psCchequera = movimientoDto.getIdChequeraBenef();
			} else {
				psTipo = "04";
				psCchequera = movimientoDto.getClabeBenef();
			}
			String nomEmpresa = funciones.quitarCaracteresRaros(movimientoDto.getNomEmpresa(), true);
			psNombreEmpresa = nomEmpresa.trim();
    		if(movimientoDto.getIdDivisa()!= null && movimientoDto.getIdDivisa().trim().equals("MN")){
    			psMonedaOrigen = "1";
    	        psMonedaDestino = "1";
    		}else {
 		    	psMonedaOrigen = "2";
 		        psMonedaDestino = "2";
 		    }
    		String rfcRea = funciones.quitarCaracteresRaros(movimientoDto.getRfc(), true);
    		psBeneficiario = funciones.quitarCaracteresRaros(movimientoDto.getBeneficiario(), true);
    		String poHeaders = layoutsDao.obtenerReferenciaAgrupada(movimientoDto.getNoEmpresa(),
 		   			movimientoDto.getIdBanco(), movimientoDto.getIdChequera(), movimientoDto.getPoHeaders());

    		psRegistro.append("");
    		//psRegistro.append(movimientoDto.getDescCveOperacion());
    		psRegistro.append(psTipo);
    		psRegistro.append(funciones.ajustarLongitudCampo("" + psNombreEmpresa, 13, "D", "", "0"));
    		psRegistro.append(funciones.ajustarLongitudCampo("" + movimientoDto.getIdChequera(), 20, "D", "", "0"));
    		psRegistro.append(funciones.ajustarLongitudCampo("" + psCchequera, 20, "D", "", "0"));
    		psCampo = funciones.ponerFormatoCeros((movimientoDto.getImporte() * 100), 14);
    		psRegistro.append(funciones.ajustarLongitudCampo("" +psCampo , 14, "D", "", "0"));
    		psRegistro.append(funciones.ajustarLongitudCampo("" + funciones.quitarCaracteresRaros(poHeaders, true), 10, "D", "", "0"));
    		psRegistro.append(funciones.ajustarLongitudCampo("" + funciones.quitarCaracteresRaros(movimientoDto.getConcepto(), true), 30, "I", "", " "));
    		psRegistro.append(psMonedaOrigen);
    		psRegistro.append(psMonedaDestino);
    		psRegistro.append(funciones.ajustarLongitudCampo("" + rfcRea, 13, "I", "", " "));
    		psRegistro.append(funciones.ajustarLongitudCampo(psCampo, 14, "D", "", "0"));
    		psCampo = "";
    		psRegistro.append(funciones.ajustarLongitudCampo(psCampo, 39, "D", "", " "));
    		System.out.println("fecha "+layoutsDao.fechaHoy());
    		psRegistro.append(layoutsDao.fechaHoy().replace("-", ""));
    		psRegistro.append(funciones.ajustarLongitudCampo("" + psBeneficiario, 70, "I", "", " "));
    		psRegistro.append("");
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " +
			e.toString() + "P:com.webset.set.layout, C:LayoutBanorteBusiness, M:beArmaBanorteAgrupados");
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
