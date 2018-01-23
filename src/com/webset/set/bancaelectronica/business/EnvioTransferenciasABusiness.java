package com.webset.set.bancaelectronica.business;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.webset.set.bancaelectronica.dao.EnvioTransferenciasADao;
import com.webset.set.bancaelectronica.dto.ArchTransferDto;
import com.webset.set.bancaelectronica.dto.CriterioBusquedaDto;
import com.webset.set.bancaelectronica.dto.DetArchTranferAgrup;
import com.webset.set.layout.business.CreacionArchivosBusiness;
import com.webset.set.layout.business.LayoutAztecaBusiness;
import com.webset.set.layout.business.LayoutBancomerBusiness;
import com.webset.set.layout.business.LayoutBanorteBusiness;
import com.webset.set.layout.business.LayoutScotiabankInverlatBusiness;
import com.webset.set.layout.dao.LayoutsDao;
import com.webset.set.layout.dto.ParametrosLayoutDto;
import com.webset.set.seguridad.business.SegUsuarioPerfilBusiness;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

public class EnvioTransferenciasABusiness {
	private EnvioTransferenciasADao envioTransferenciasADao;
	private SegUsuarioPerfilBusiness segUsuarioPerfilBusiness; 
	private LayoutBancomerBusiness layoutBancomerBusiness;
	private LayoutAztecaBusiness layoutAztecaBusiness;
	private CreacionArchivosBusiness creacionArchivosBusiness;
	private LayoutScotiabankInverlatBusiness layoutScotiabankInverlatBusiness;
	private LayoutBanorteBusiness layoutBanorteBusiness;
	Bitacora bitacora = new Bitacora();
	LayoutsDao layoutsDao = new LayoutsDao();
	GlobalSingleton globalSingleton;
	Funciones funciones = new Funciones();
	
	boolean lbEnvioBitalH2H = false;
	private static Logger logger = Logger.getLogger(EnvioTransferenciasABusiness.class);
	
	public List<LlenaComboGralDto> llenarBancoEmisor(int idUsuario){
		List<LlenaComboGralDto> listRet = new ArrayList<LlenaComboGralDto>();
		try{
			if(segUsuarioPerfilBusiness.validarPerfilUsuario(idUsuario, 108))//BEYNAFIN
				listRet=envioTransferenciasADao.llenarBancoEmisor(true, true);
			else if(segUsuarioPerfilBusiness.validarPerfilUsuario(idUsuario, 109))//SOLONAFIN  'Solo Muestra envios para NAFIN
				listRet=envioTransferenciasADao.llenarBancoEmisor(false, true);
	        else//'Solo envios de BE sin incluir NAFIN
	        	listRet=envioTransferenciasADao.llenarBancoEmisor(true, false);
	        
		}catch(Exception e){
			logger.error(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:BancaElectronica, C:EnvioTransferenciasBusiness, M:llenarBancoEmisor");
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
		return listRet;
	}
	public List<LlenaComboGralDto>llenarComboGral(LlenaComboGralDto dto){
		return envioTransferenciasADao.llenarComboGral(dto);
	}
	
	public List<LlenaComboDivisaDto>llenarComboDivisa(){
		List<LlenaComboDivisaDto> listDatos = new ArrayList<LlenaComboDivisaDto>();
		try{
		 
			listDatos=envioTransferenciasADao.llenarComboDivisa();
		}catch(Exception e){
			logger.error(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BancaElectronica, C:EnvioTransferenciasBusiness, M:llenarComboDivisa");
			logger.error(e.getLocalizedMessage());
		}
		return listDatos;
	}
	
	
	public List<MovimientoDto> consultarPagos(CriterioBusquedaDto dtoBus){
		List<MovimientoDto> list= new ArrayList<MovimientoDto>();
		try{
			if(dtoBus.getIdBanco()==ConstantesSet.NAFIN)
				list=envioTransferenciasADao.consultarPagosNafin(dtoBus);
			else if(dtoBus.isChkMassPayment())
			{
				if(envioTransferenciasADao.consultarConfiguraSet(273)!= null && 
						envioTransferenciasADao.consultarConfiguraSet(273).equals("SI"))
				{
					dtoBus.setChkMassPayment(true);
					list=envioTransferenciasADao.consultarPagosMassPayment(dtoBus);
				}
				else
				{
					dtoBus.setChkMassPayment(false);
					list=envioTransferenciasADao.consultarPagosMassPayment(dtoBus);
				}
				
			}else{
				list=envioTransferenciasADao.consultarPagos(dtoBus);
			}
			
			if(list.size()>0)
			{
				for(int i= 0; i<list.size();i++)
				{
					//'*******************Para pagos en dolares de CCM se pone la cuenta clabe
		            if(list.get(i).getIdDivisa()!=null && list.get(i).getIdDivisa().trim().equals("DLS"))
		            {
		            	if(list.get(i).getClabeBenef()!=null && !list.get(i).getClabeBenef().equals(""))
		            		list.get(i).setClabeBenef(list.get(i).getClabeBenef()); 
		                else
		                	list.get(i).setClabeBenef(list.get(i).getIdChequeraBenef());
		            }
		         
	            	//'********************
				}
			}
			
			
		}catch(Exception e){
			logger.error(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BancaElectronica, C:EnvioTransferenciasBusiness, M:consultarPagos");
			logger.error(e.getLocalizedMessage());
		}
		return list;
	}
	
	public Map<String,Object> ejecutarEnvioTrans(List<MovimientoDto> listBus, CriterioBusquedaDto dtoCriBus){
		Map<String, Object> mapRet = new HashMap<String, Object>();
		try{
			if(listBus.size()>0)
			{
				lbEnvioBitalH2H = false;
		        if(dtoCriBus.getIdBanco()==ConstantesSet.BITAL && envioTransferenciasADao.consultarConfiguraSet(232)!= null 
		        		&& envioTransferenciasADao.consultarConfiguraSet(232).equals("")) 
		            lbEnvioBitalH2H = true;
		        System.out.println("Bandera de hsbc"+lbEnvioBitalH2H);
				mapRet=llamarAceptar(listBus,dtoCriBus,lbEnvioBitalH2H);
			}
		}catch(Exception e){
			logger.error(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BancaElectronica, C:EnvioTransferenciasBusiness, M:ejecutarEnvioTrans");
			logger.error(e.getLocalizedMessage());
		}
		return mapRet;
	}
	
	public Map<String, Object> llamarAceptar(List<MovimientoDto> listBus, CriterioBusquedaDto dtoCriBus, boolean lbEnvioBitalH2H) {
		Map<String, Object> mapRet = new HashMap<String, Object>();
		mapRet.put("msgUsuario", "Error desconocido.");
		mapRet.put("estatusAv", false);
		Map<String, Object> mapCreaArch = new HashMap<String,Object>();
		int i=0;
		int insDetArchTrans=0;
		int piRegistros=0;
		int piTipoTrasferencia=0;
		int piRespuesta=0;
		int piBanco=0;
	    int totalRegistros = 0;
	    int noEmpresa = 0;
	    int folioConfirma = 0;
	    double pdImporte=0;
	    double totalArchivo = 0;
		boolean pbMismoBanco = false;
		boolean pbEscribi=false;
		boolean pbLayoutComerica=false;
		boolean pbInternacional=false;
	    boolean continuar = false;
	    boolean proveedor = false;
	    boolean encabezado1 = false;
	    boolean encabezado2 = false;
	    boolean triler = false;
	  
	    String psFolios="";
	    String psPoliza="";
	    String psTipoEnvioComerica=""; 
	    String psTipoEnvio="";
	    String psRutaArchivos="";
	    String psSoloArchivos="";
	    String lsYaEjecutados="";
	    String psRegistro="";
	    String dirPrincipal="";
	    String dirPrincipalLocal="";
	    String psSubdirectorio = "";
	    String sDescBanco = "";
	    String sNomFoliador = "";
	    String sIndiceArch = "";
	    String nomArch = "";
	    List<String> listDatosCadena = new ArrayList<String>();
	    
	    List<String> psPolizas = new ArrayList<String>();   
	    List<String> psEmpresas = new ArrayList<String>();
	    List<String> psChequeras = new ArrayList<String>();
	    boolean beArmaNuevos = false;
	    boolean cifrado = false;
	    
	   // Map<String, Object> mapRespExportaReg = new HashMap<String, Object>();
	    
	    double impTotDet = 0;
	    
	    try {
	    	if(listBus.size() <= 0) {
	    		mapRet.put("msgUsuario", "No existen datos para procesar");
	    		return mapRet;
	    	}
	    	/*listDatosCadena = envioTransferenciasADao.armaCadenaConexion();
	    	if (listDatosCadena.size()!=0 ) {
	    		dirPrincipal = "smb://"+listDatosCadena.get(0)+":"+listDatosCadena.get(1)+
		    			"@"+listDatosCadena.get(2)+"/"+listDatosCadena.get(3);
	    	
			} else {
				mapRet.put("msgUsuario","No se encontraron las variantes para generar la ruta");
	    		return mapRet;
			}
	    	
	    	if(dirPrincipal.equals("")) {
	    		mapRet.put("msgUsuario","No se encontro la ruta para guardar los archivos");
	    		return mapRet;
	    	}*/
	    	
	    	dirPrincipal = ConstantesSet.CARPETA_RAIZ_RESPALDO;
	    	
	    	System.out.println(dirPrincipal);
	    	
	    	if(dtoCriBus.getOptTipoEnvio().equals("Mismo")) {
	    		pbMismoBanco = true;
	    		piTipoTrasferencia = 2;
	    	}else
	    		pbMismoBanco = false;
	    	
	    	if(dtoCriBus.getOptTipoEnvio().equals("Internacional")) {
	    		pbInternacional = true;
	    		piTipoTrasferencia = 3;
	    	}else
	    		pbInternacional = false;
	    	
	    	if(dtoCriBus.getOptTipoEnvio().equals("InterBancario"))
	        	piTipoTrasferencia = 1;
	        else if(dtoCriBus.getOptTipoEnvio().equals("SPEUA"))
	        	piTipoTrasferencia = 4;
	        
    		/*if(lbEnvioBitalH2H && pbMismoBanco && dtoCriBus.getOptEnvioHSBC() == 1) {
    			for(i=0; i<listBus.size(); i++) {
	    			if(listBus.get(i).getDescUsuarioBital().equals("")) {
	    				mapRet.put("msgUsuario", "La empresa " + listBus.get(i).getNoEmpresa() + " No tiene asignado el Usuario Bital Host to Host");
	    	    		return mapRet;
	    			}else if(listBus.get(i).getDescServicioBital().equals("")) {
	    				mapRet.put("msgUsuario", "La empresa " + listBus.get(i).getNoEmpresa() + " No tiene asignado el Servicio Bital Host to Host");
	    	    		return mapRet;
	    			}
    			}
    		}*/
    		//Validaciones en azteca, city y comerica
    		for(i=0; i<listBus.size(); i++) {
    			if(dtoCriBus.getIdBanco() == ConstantesSet.AZTECA) {
    				if(!pbMismoBanco) {
    					if(listBus.get(i).getClabeBenef().trim().length() != 18 && listBus.get(i).getIdChequeraBenef().trim().length() != 16) {
    						mapRet.put("msgUsuario", "La Chequera o Clabe del beneficiario " + listBus.get(i).getBeneficiario() + 
    								" Tiene una longitud incorrecta, Chequera = 16 y clabe = 18");
    	    	    		return mapRet;
    					}
    				}else {
    					if(listBus.get(i).getIdChequera().trim().length() != 14) {
    						mapRet.put("msgUsuario", "La Chequera " + listBus.get(i).getIdChequera() + " Del beneficiario " +
    								 listBus.get(i).getBeneficiario() + " Tiene una longitud incorrecta, Chequera = 14");
    	    	    		return mapRet;
    					}
    				}
    			}else if(dtoCriBus.getIdBanco() == ConstantesSet.COMERICA || dtoCriBus.getIdBanco() == ConstantesSet.CITIBANK_DLS) {
    				if(listBus.get(i).getAba().trim().equals("") || listBus.get(i).getSwiftCode().trim().equals("")) {
    					mapRet.put("msgUsuario", "La Chequera " + listBus.get(i).getIdChequeraBenef() + " Del beneficiario " +
								 listBus.get(i).getBeneficiario() + " No tiene ABA ni SWIFT , es necesario capturar alguno");
	    	    		return mapRet;
    				}
    			}
    			if((dtoCriBus.getIdDivisa().equals("DLS") || dtoCriBus.isChkMassPayment()) &&  dtoCriBus.getIdBanco() != ConstantesSet.BANCOMER) {
    				if(listBus.get(i).getAba().trim().equals("") || listBus.get(i).getSwiftCode().trim().equals("")) {
    					mapRet.put("msgUsuario", "La Chequera " + listBus.get(i).getIdChequeraBenef() + " Del beneficiario " +
								 listBus.get(i).getBeneficiario() + " No tiene ABA ni SWIFT , es necesario capturar alguno");
	    	    		return mapRet;
    				}
    			}
    			if(dtoCriBus.getIdDivisa().equals("DLS") && dtoCriBus.getIdBanco() == ConstantesSet.BANCOMER) {
    				if(listBus.get(i).getAba().trim().equals("") && listBus.get(i).getSwiftCode().trim().equals("")) {
    					mapRet.put("msgUsuario", "La Chequera " + listBus.get(i).getIdChequeraBenef() + " Del beneficiario " +
								 listBus.get(i).getBeneficiario() + " No tiene ABA ni SWIFT , es necesario capturar alguno");
	    	    		return mapRet;
    				}
    			}
    		}
    		if(listBus.get(0).getBLayoutComerica().equals("S")) 
    			pbLayoutComerica = true;
    		
    		if(dtoCriBus.getIdBanco()!= ConstantesSet.INVERLAT || dtoCriBus.getIdBanco()!= ConstantesSet.SANTANDER
    				|| dtoCriBus.getIdBanco()!= ConstantesSet.BANCOMER || dtoCriBus.getIdBanco()!= ConstantesSet.BANCRECER
    				|| dtoCriBus.getIdBanco()!= ConstantesSet.CITIBANK_MN || dtoCriBus.getIdBanco()!= ConstantesSet.BITAL
    				|| dtoCriBus.getIdBanco()!=ConstantesSet.BANAMEX || dtoCriBus.getIdBanco()!= ConstantesSet.INBURSA
    				|| dtoCriBus.getIdBanco()!=ConstantesSet.AMRO_BANK || dtoCriBus.getIdBanco()!= ConstantesSet.AZTECA
    				|| dtoCriBus.getIdBanco()!=ConstantesSet.BANORTE || dtoCriBus.getIdBanco()!= ConstantesSet.BOFA
    				|| dtoCriBus.getIdBanco()!= ConstantesSet.CHASE_MANHATTAN || dtoCriBus.getIdBanco()!= ConstantesSet.FUBI
    				|| dtoCriBus.getIdBanco()!= ConstantesSet.AMEX || dtoCriBus.getIdBanco()!= ConstantesSet.COMERICA
    				|| dtoCriBus.getIdBanco()!= ConstantesSet.CITIBANK_DLS || dtoCriBus.getIdBanco()!= ConstantesSet.BANK_BOSTON
    				|| dtoCriBus.getIdBanco()!= ConstantesSet.NAFIN) {
    			
    			if(pbLayoutComerica) {
    				mapRet.put("msgUsuario", "No hay Layout para el Banco");
    				return mapRet;
    			}
    		}
    		
    		switch(dtoCriBus.getIdBanco()) {
    			case ConstantesSet.BANORTE:
		    		sDescBanco = envioTransferenciasADao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_BANORTE);
	   				sNomFoliador = "arch_banorte";
	   				sIndiceArch = "xx";
			   		break;
		   		case ConstantesSet.AZTECA:
		   			sDescBanco = envioTransferenciasADao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_AZTECA);		   			
	   				sNomFoliador = "arch_azteca";
	   				sIndiceArch = "az";
    				break;
		   		case ConstantesSet.INBURSA:
		   			sDescBanco = "Inbursa";
	   				sNomFoliador = "arch_inbursa";
	   				sIndiceArch = "in";
    				break;
		   		case ConstantesSet.SANTANDER:
		   			sDescBanco = envioTransferenciasADao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_SANTANDER);
	   				sNomFoliador = "arch_santander";
	   				sIndiceArch = "st";
		   			break;
		   		case ConstantesSet.BANAMEX:
		   			if(dtoCriBus.getOptEnvioBNMX() == 1) {				//Layout - TEF (Transferencia Electronica de Fondos)
		   				sDescBanco = envioTransferenciasADao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_BANAMEX_TEF);
		   				mapRet = layoutBancomerBusiness.beArmaBanamexTEFAgrupado(pbMismoBanco == false ? true : false, listBus, false, "", i, dirPrincipal, sDescBanco,"");
						return mapRet;
		   			}else if(dtoCriBus.getOptEnvioBNMX() == 3) {		//Layout - Mass Payment Citibank
		   				mapRet = layoutBancomerBusiness.beArmaBanamexMassPay(pbMismoBanco == false ? true : false, listBus, false, "", i, dirPrincipal,1);
						return mapRet;
		   			}else if(dtoCriBus.getOptEnvioBNMX() == 4) {		//Layout - Flat File Citibank (Solo DLS)
		   				sDescBanco = envioTransferenciasADao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_BANAMEX_FLT);
		   				sNomFoliador = "arch_citibank_dls";
		   				sIndiceArch = "cd";
		   			}else if(dtoCriBus.getOptEnvioBNMX() == 5) {		//Layout - Pay Link Citibank (Solo MN)
		   				sDescBanco = envioTransferenciasADao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_BANAMEX_PL);
		   				sNomFoliador = "arch_citi_paylink";
		   				sIndiceArch = "cp";
		   			}else {												//Layoutï¿½s Digitem = dtoCriBus.getOptEnvioBNX() == 0
		   				sDescBanco = envioTransferenciasADao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_BANAMEX);
		   				sNomFoliador = "arch_banamex";
		   				sIndiceArch = "bn";
		   			}
		   			break;
    			case ConstantesSet.BANCOMER:  
    				if(dtoCriBus.isChkConvenioCie() && pbMismoBanco) {   
    					sDescBanco = envioTransferenciasADao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_BANCOMER_CIE);
		   				sNomFoliador = "arch_bancomer_cie";
		   				sIndiceArch = "be";
    				}else if(pbInternacional) {
    					sDescBanco = envioTransferenciasADao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_BANCOMER_INTERNACIONAL);
		   				sNomFoliador = "arch_bancomer";
		   				sIndiceArch = "bc";
    				}else {
    					sDescBanco = envioTransferenciasADao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_BANCOMER);
		   				sNomFoliador = "arch_bancomer";
		   				sIndiceArch = "bc";
    				}
    				break;
    			case ConstantesSet.BANCRECER:
    				sDescBanco = "Bancrecer";
	   				sNomFoliador = "arch_bancrecer";
	   				sIndiceArch = "br";
	   				break;
    			case ConstantesSet.CITIBANK_MN:
    				sDescBanco = "CitiBank"+System.getProperty("file.separator")+"MN";
	   				sNomFoliador = "arch_citibank";
	   				sIndiceArch = "ct";
    				break;
    			case ConstantesSet.CITIBANK_DLS:
    				if(dtoCriBus.getOptEnvioBNMX() == 3) {			//Layout - Mass Payment Citibank
		   				mapRet = layoutBancomerBusiness.beArmaBanamexMassPay(pbMismoBanco == false ? true : false, listBus, false, "", i, dirPrincipal,1);
						return mapRet;
    				}else {
    					if(dtoCriBus.getOptEnvioBNMX() == 2) {		//Layout - World Link Citibank
    						sDescBanco = "CitiBank"+ System.getProperty("file.separator") +"WorldLink";
    		   				sNomFoliador = "arch_citibank_wl";
    		   				sIndiceArch = "wl";
    					}else {
    						sDescBanco = envioTransferenciasADao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_CITIBANCK);
    		   				sNomFoliador = "arch_citibank_dls";
    		   				sIndiceArch = "cd";
    					}
    				}
    				break;
    			case ConstantesSet.CHASE_MANHATTAN:
    				sDescBanco = "Chase";
	   				sNomFoliador = "arch_chase";
	   				sIndiceArch = "ch";
    				break;
    			case ConstantesSet.BITAL:
    				if(lbEnvioBitalH2H) {
    					if(pbMismoBanco) {
    						sDescBanco = "Bital"+System.getProperty("file.separator")+"H2H MB";
    		   				sNomFoliador = "arch_bital_h2h";
    		   				sIndiceArch = "bh";
    					}else{
    						sDescBanco = "Bital"+System.getProperty("file.separator")+"H2H MBI";
    		   				sNomFoliador = "arch_bital_h2h";
    		   				sIndiceArch = "bh";
    					}
    				}else if(dtoCriBus.getOptTipoEnvio().equals("SPEUA")) {
    					sDescBanco = "Bital"+System.getProperty("file.separator")+"Speua";
		   				sNomFoliador = "arch_bital_speua";
		   				sIndiceArch = "bs";
    				}
    				break;
    			case ConstantesSet.INVERLAT:
    				sDescBanco = envioTransferenciasADao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_SCOTIABANK_INVERLAT);
    				//sDescBanco = "Inverlat";
	   				sNomFoliador = "arch_inverlat";
	   				sIndiceArch = "in";
    				break;
    			case ConstantesSet.BOFA:
    				sDescBanco = "Bofa";
	   				sNomFoliador = "arch_bofa";
	   				sIndiceArch = "ba";
    				break;
    			case ConstantesSet.FUBI:
    				sDescBanco = "Fubi";
	   				sNomFoliador = "arch_fubi";
	   				sIndiceArch = "fu";
    				break;
    			case ConstantesSet.AMEX:
    				sDescBanco = "Amex";
	   				sNomFoliador = "arch_amex";
	   				sIndiceArch = "ae";
    				break;
    			case ConstantesSet.COMERICA:
    				sDescBanco = "Comerica";
	   				sNomFoliador = "arch_comerica";
	   				sIndiceArch = "co";
    				break;
    			case ConstantesSet.BANK_BOSTON:
    				sDescBanco = "Bank Boston";
	   				sNomFoliador = "arch_bankboston";
	   				sIndiceArch = "bb";
    				break;
    			case ConstantesSet.AMRO_BANK:
    				sDescBanco = "Amro Bank";
	   				sNomFoliador = "arch_amro";
	   				sIndiceArch = "am";
    				break;
    			case ConstantesSet.NAFIN:
    				sDescBanco = "Nafin";
	   				sNomFoliador = "arch_nafin";
	   				sIndiceArch = "nf";
    				break;
    		}//Termina switch
	    	//se genera el nombre del archivo
    		
    		if(!sDescBanco.equals("") && !sNomFoliador.equals("") && !sIndiceArch.equals("")) {
    			sDescBanco = sDescBanco.replace('/', File.separatorChar).replace('\\', File.separatorChar);
	    		mapCreaArch = creacionArchivosBusiness.generarNombreArchivo(sDescBanco, sNomFoliador, sIndiceArch, pbMismoBanco, false, dtoCriBus.isChkH2H(), noEmpresa,1);
   				
   				if(!mapCreaArch.isEmpty()) {
					if(mapCreaArch.get("msgUsuario") != null) {
						mapRet.put("msgUsuario", mapCreaArch.get("msgUsuario"));
						return mapRet;
					}
				}
   				mapRet.put("nomArchivo", mapCreaArch.get("nomArchivo").toString());
    		}
    		//for para la creacion del layout
    		for(i=0; i<listBus.size(); i++) {
    			mapRet.put("msgRegistro", "");
    			continuar = true;
		    	
		    	/*if(envioTransferenciasADao.consultarYaEjecutado(listBus.get(i).getNoFolioDet())) {
		    		lsYaEjecutados += listBus.get(i).getNoDocto()+ ",";
		    		continuar = false;
		    	}*/
		    	if(envioTransferenciasADao.consultarPagoEjecutado(listBus.get(i).getPoHeaders(),
		    			listBus.get(i).getNoEmpresa(), listBus.get(i).getIdBanco(), listBus.get(i).getIdChequera())){
		    		lsYaEjecutados += listBus.get(i).getPoHeaders()+ ",";
		    		continuar = false;
		    	}
		    	
		    	if(continuar) {
		    		if(dtoCriBus.getOptEnvioBNMX() == 4 || dtoCriBus.getOptEnvioBNMX() == 5)
		    			piBanco = ConstantesSet.CITIBANK_DLS;
		    		else
		    			piBanco = dtoCriBus.getIdBanco();
		    		
		    		switch(piBanco) {
		    		
		    			case ConstantesSet.BITAL:
		    				if(lbEnvioBitalH2H) {
		    					if(pbMismoBanco) {
		    						mapRet = layoutBancomerBusiness.beArmaHSBCH2HMBAgrupado(listBus, i, piTipoTrasferencia,
	        								mapCreaArch.get("nomArchivo").toString(), dirPrincipal, piBanco, "TRANSFERENCIA");  //ok
		    					}else {
		    						mapRet = layoutBancomerBusiness.beArmaHSBCH2HIBAgrupado(listBus, i, piTipoTrasferencia,
	        								mapCreaArch.get("nomArchivo").toString(), dirPrincipal, piBanco, "TRANSFERENCIA");//ok
		    						return mapRet;
		    					}
		    				}else if(dtoCriBus.getOptTipoEnvio().equals("SPEUA")) {
		    					mapRet = layoutBancomerBusiness.beExportacion(listBus, i, piTipoTrasferencia,
        								mapCreaArch.get("nomArchivo").toString(), dirPrincipal, piBanco, "TRANSFERENCIA");  //ok p
		    				}
		    				break;
		    				
                    	case ConstantesSet.BANAMEX:
                    		if(pbMismoBanco) {
                    			mapRet = layoutBancomerBusiness.beExportacionAgrupado(listBus, i, piTipoTrasferencia,
                    								mapCreaArch.get("nomArchivo").toString(), dirPrincipal, piBanco, "TRANSFERENCIA");
                    			
                    		}else {
                    			psRegistro = layoutBancomerBusiness.beArmaBanamexInter(listBus, i);
                    			
                    			if(psRegistro != null && !psRegistro.equals("")) 
                    				mapRet.put("msgRegistro", psRegistro);
                    			else
                    				mapRet.put("msgUsuario", "Error al armar Banamex Interbancario");
                    		}
                    		break;
                    		
                   		case ConstantesSet.INBURSA:
                   			
                   			break;
                   			
                   		case ConstantesSet.SANTANDER:
                   			if(dtoCriBus.isChkH2HSantander()) {
                   				impTotDet += listBus.get(i).getImporte();

                   				boolean nomina = dtoCriBus.isNomina();
                   				psRegistro = layoutBancomerBusiness.beArmaH2HSantander(obtenerParametrosLayout(listBus, i, pbMismoBanco), i, impTotDet, listBus.size(), nomina, dtoCriBus.isChkConvenioSant());//Arma la cadena
            					
                   				if(psRegistro != null && !psRegistro.equals("")) 
                    				mapRet.put("msgRegistro", psRegistro);
                   				if(psRegistro.equals(""))
                   					mapRet.put("msgUsuario", "No existe la cuenta clabe.");
                   			}else {
	                   			mapRet = layoutBancomerBusiness.beExportacionAgrupado(listBus, i, piTipoTrasferencia,
	                   								mapCreaArch.get("nomArchivo").toString(), dirPrincipal, piBanco, "TRANSFERENCIA");
                   			}
							break;
							
                   		case ConstantesSet.INVERLAT:
                   			psRegistro = layoutScotiabankInverlatBusiness.beArmaScotiaBankInverlatAgrupado(listBus.get(i), folioConfirma, pbMismoBanco, pbInternacional);//(listBus.get(i), i, pbMismoBanco, pbInternacional);
                   			if(psRegistro!=null && !psRegistro.equals(""))
        					{
                   				mapRet = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal,mapCreaArch.get("psBanco").toString(),
        								mapCreaArch.get("nomArchivo").toString(),psRegistro);
        						if ((Boolean)mapRet.get("resExito")) {
        							mapRet.put("msgRegistro", psRegistro);
    								beArmaNuevos = true;
								} else {
        							mapRet.put("msgUsuario",mapRet.get("msgUsuario").toString());
        							return mapRet;
								}
        					}else{
        						mapRet.put("msgUsuario","Error al generar archivo de Banco Banorte.");
    							return mapRet;
        					}
                   			break;
                   			
                   		case ConstantesSet.CITIBANK_DLS:
                   			if(dtoCriBus.getOptEnvioBNMX() == 5) {			//Opcion de Citibank PayLink MN
                   				psRegistro = layoutBancomerBusiness.beArmaCitibankPaylinkMNAgrupado(listBus, i, "");
                    			
                   				if(psRegistro != null && !psRegistro.equals("")) 
                    				mapRet.put("msgRegistro", psRegistro);
                    			else
                    				mapRet.put("msgUsuario", "No se encontró el folio <folio_paylink>!!");
                   			}else {
                   				if(dtoCriBus.getOptEnvioBNMX() == 2)			//Opcion de Citibank WorldLink
                   					piTipoTrasferencia = 5;
                   				else if(dtoCriBus.getOptEnvioBNMX() == 6)	//Opcion de Citibank ACH
                   					piTipoTrasferencia = 4;
                   				else if(piTipoTrasferencia == 1) {			//Opcion de Interbancaria
                   					if(listBus.get(i).getTipoEnvioLayout() != 0)
                   						piTipoTrasferencia = listBus.get(i).getTipoEnvioLayout();
                   					else if(!listBus.get(i).getNomBancoIntermediario().trim().equals(""))
                   						piTipoTrasferencia = 3;
                   					else
                   						piTipoTrasferencia = 1;
                   				}
                   				mapRet = layoutBancomerBusiness.beExportacion(listBus, i, piTipoTrasferencia,
                   									mapCreaArch.get("nomArchivo").toString(), dirPrincipal, piBanco, "TRANSFERENCIA");
                   			}
                   			break;
                   			
                   		case ConstantesSet.AZTECA:	//ok
                   			/*
                   			
                   			if(envioTransferenciasADao.consultarConfiguraSet(4000).equals("true")){
                   				if(!cifrado){
                   					///obtenerCifrado
                   					
                   				}
                   				psRegistro = layoutAztecaBusiness.beArmaAztecaAgrupado(listBus.get(i), i, pbMismoBanco, pbInternacional);
	                   			if(psRegistro!=null && !psRegistro.equals("")){
	                   				mapRet = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal,mapCreaArch.get("psBanco").toString(),
	        								mapCreaArch.get("nomArchivo").toString(),psRegistro);
	        						if ((Boolean)mapRet.get("resExito")) {
	        							mapRet.put("msgRegistro", psRegistro);
	    								beArmaNuevos = true;
									} else {
	        							mapRet.put("msgUsuario",mapRet.get("msgUsuario").toString());
	        							return mapRet;
									}
	        						
	        					}else{
	        						mapRet.put("msgUsuario","Error al generar archivo de Banco Azteca.");
	    							return mapRet;
	        					}
                   			}else{*/
                   				psRegistro = layoutAztecaBusiness.beArmaAztecaAgrupado(listBus.get(i), i, pbMismoBanco, pbInternacional);
                   				if(psRegistro!=null && !psRegistro.equals("")){
	                   				mapRet = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal,mapCreaArch.get("psBanco").toString(),
	        								mapCreaArch.get("nomArchivo").toString(),psRegistro);
	        						if ((Boolean)mapRet.get("resExito")) {
	        							mapRet.put("msgRegistro", psRegistro);
	    								beArmaNuevos = true;
									} else {
	        							mapRet.put("msgUsuario",mapRet.get("msgUsuario").toString());
	        							return mapRet;
									}
	        						
	        					}else{
	        						mapRet.put("msgUsuario","Error al generar archivo de Banco Azteca.");
	    							return mapRet;
	        					}
                   			//}
                   			
                   			break;
                   		case ConstantesSet.BANORTE:
                   			psRegistro = layoutBanorteBusiness.beArmaBanorteAgrupados(listBus.get(i), i, pbMismoBanco, pbInternacional);
                   			//psRegistro = layoutAztecaBusiness.beArmaAzteca(listBus.get(i), i, pbMismoBanco, pbInternacional);
                   			if(psRegistro!=null && !psRegistro.equals("")){
                   				mapRet = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal,mapCreaArch.get("psBanco").toString(),
        								mapCreaArch.get("nomArchivo").toString(),psRegistro);
        						if ((Boolean)mapRet.get("resExito")) {
        							mapRet.put("msgRegistro", psRegistro);
    								beArmaNuevos = true;
								} else {
        							mapRet.put("msgUsuario",mapRet.get("msgUsuario").toString());
        							return mapRet;
								}
        					}else{
        						mapRet.put("msgUsuario","Error al generar archivo de Banorte.");
    							return mapRet;
        					}
                   			
                   			break;
                   		default:
                   			switch(piBanco) {
                   				case ConstantesSet.AMRO_BANK:

                   					break;
                            	case ConstantesSet.BANK_BOSTON:
                            		
                            		break;
                            	case ConstantesSet.CHASE_MANHATTAN:
                            			
                            		break;
                            	case ConstantesSet.BANCOMER:
                            			if(dtoCriBus.isChkConvenioCie() && pbMismoBanco)
                        				{
                            				psRegistro = layoutBancomerBusiness.beArmaConvenioCIE(
                            						obtenerParametrosLayout(listBus, i, pbMismoBanco));//Arma la cadena
                        					
                               				if(psRegistro != null && !psRegistro.equals("")) 
                               					mapRet.put("msgRegistro", psRegistro);
                        				}else if (dtoCriBus.isChkH2H()) 
                        				{	                            					
                        					dirPrincipal=envioTransferenciasADao.consultarConfiguraSet(2401);	                            					
                        					psRegistro = "";
                        					
                        					if (listBus.get(i).getOrigenMov().equals("SAP") || 
                        							listBus.get(i).getOrigenMov().equals("SET") || 
                        							(pbMismoBanco == false && listBus.get(i).getOrigenMov().equals("ADM")))
                        					{
                        						psSubdirectorio = "sitpre"+System.getProperty("file.separator")+"PAGOSTER"+
                        								System.getProperty("file.separator")+"ENVIO"+System.getProperty("file.separator");
                        						proveedor = true;
                        					}
                        					else
                        					{
                        						psSubdirectorio = "nominapre"+System.getProperty("file.separator")+"ENVIO"+System.getProperty("file.separator");
                        						proveedor = false;		
                        					}
                        					
                        					if (totalRegistros == 0 && totalArchivo == 0)
                        					{
                        						for(int var = 0; var < listBus.size(); var++)
                        						{
                        							totalArchivo = totalArchivo + listBus.get(var).getImporte();	                            							
                        						}
                        						totalRegistros = listBus.size();
                        					}                     					
                        					
                        					if (encabezado1 == false)
                        					{	    
                        						//Genera encabezado h2h
                        						psRegistro = "";
//                        						psRegistro=layoutBancomerBusiness.armaBancomerH2H(obtenerParametrosLayout(listBus, i, pbMismoBanco), 1, 
//                        																	  totalRegistros, totalArchivo, proveedor, 
//                        																	  mapCreaArch.get("nomArchivo").toString(), dtoCriBus.isConvenioCie());//Arma la cadena
//                        						
                        						encabezado1 = true;
                        						
                        						if(psRegistro!=null && !psRegistro.equals(""))
                            					{   
                        							mapRet = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal,psSubdirectorio,
                            								mapCreaArch.get("nomArchivo").toString(),psRegistro);
                        							
                        							if(!(Boolean)mapRet.get("resExito")){
    	                                				mapRet.put("msgUsuario","Error al generar archivo Bancomer");
                            							return mapRet;
                            						}
                            					}
                        					}
                        					
                        					if(encabezado1 == true && encabezado2 == false)
                        					{
                        						//Genera encabezado SIT
                        						psRegistro = "";	                            						
//                        						psRegistro=layoutBancomerBusiness.armaBancomerH2H(obtenerParametrosLayout(listBus, i, pbMismoBanco), 2, 
//														  totalRegistros, totalArchivo, proveedor, 
//														  mapCreaArch.get("nomArchivo").toString(), dtoCriBus.isConvenioCie());//Arma la cadena
//			
                        						encabezado2 = true;
                        						
                        						if(psRegistro!=null && !psRegistro.equals(""))
                            					{
                        							mapRet = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal,psSubdirectorio,
                            								mapCreaArch.get("nomArchivo").toString(),psRegistro);
                            						if(!(Boolean)mapRet.get("resExito")){
    	                                				mapRet.put("msgUsuario","Error al generar archivo Bancomer");
                            							return mapRet;
                            						}
                            					}
                        					}                         					
                        					
                        					if (encabezado1 == true && encabezado2 == true && triler == false)
                        					{
                        						//Genera detallado
                        						psRegistro = "";	                            						
//                        						psRegistro=layoutBancomerBusiness.armaBancomerH2H(obtenerParametrosLayout(listBus, i, pbMismoBanco), 3, 
//														  totalRegistros, totalArchivo, proveedor, 
//														  mapCreaArch.get("nomArchivo").toString(), dtoCriBus.isConvenioCie());//Arma la cadena
//                        						
                        						if(psRegistro!=null && !psRegistro.equals(""))
                            					{
                        							mapRet = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal,psSubdirectorio,
                            								mapCreaArch.get("nomArchivo").toString(),psRegistro);
                        							if(!(Boolean)mapRet.get("resExito")){
    	                                				mapRet.put("msgUsuario","Error al generar archivo Bancomer");
                            							return mapRet;
                            						}
                            					}
                        					}                            					
                        					
                        				}else{
                        					if(pbInternacional && listBus.get(i).getIdDivisa().equals("DLS")){
                    							psRegistro = layoutBancomerBusiness.beArmaBancomerInternacionalAgrupado(obtenerParametrosLayout(listBus, i, pbMismoBanco),listBus.get(i));//Arma la cadena
                            					
                                   				if(psRegistro != null && !psRegistro.equals("")) 
                                   					mapRet.put("msgRegistro", psRegistro);
                        					}else{
                            					psRegistro = layoutBancomerBusiness.beArmaBancomerC43Agrupado(obtenerParametrosLayout(listBus, i, pbMismoBanco));//Arma la cadena
                            					
                                   				if(psRegistro != null && !psRegistro.equals("")) 
                                   					mapRet.put("msgRegistro", psRegistro);
                        					}
                        					
                        				}
                            	break;
                            	case ConstantesSet.BANCRECER:
                            			
                            	break;
                            	case ConstantesSet.BOFA:
                            			
                            	break;
                            	case ConstantesSet.FUBI:
                            			
                            	break;
                            	case ConstantesSet.AMEX:
                            			
                            	break;
                            	case ConstantesSet.COMERICA:
                            			
                            	break;
                            	case ConstantesSet.NAFIN:
                            		
                        		break;
                            }
                   			break;
                   	}//
		    		if (!beArmaNuevos) {
		    			if(!mapRet.get("msgRegistro").equals("")) {
		    				mapRet = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal, mapCreaArch.get("psBanco").toString(),
									   mapCreaArch.get("nomArchivo").toString(), 
									   mapRet.get("msgRegistro").toString());
		    				if(!(Boolean)mapRet.get("resExito")) {
	        					mapRet.put("msgUsuario", "Error al Generar Archivo " + mapCreaArch.get("psBanco").toString());
								return mapRet;
							}
		    				
						}else {
							mapRet.put("msgUsuario", mapRet.get("msgUsuario"));
							return mapRet;
						}
					}
		  
		    		if(!dtoCriBus.isChkH2H() && !dtoCriBus.isChkH2HSantander())
		    			nomArch = mapCreaArch.get("nomArchivo").toString().substring(0, 8);
		    		else
		    			nomArch = mapCreaArch.get("nomArchivo").toString().replace(".in", "");
		    		
		    		
		    		insDetArchTrans = insertaDetArchTransfAgrup(listBus.get(i), nomArch);
		    		
		    		if(insDetArchTrans>0) {
		    			pbEscribi = true;
                    	psFolios += listBus.get(i).getNoFolioDet()+",";
                    	piRegistros++;
                    	pdImporte = pdImporte + listBus.get(i).getImporte();
                    	psPoliza += listBus.get(i).getPoHeaders()+",";
                    	psPolizas.add(listBus.get(i).getPoHeaders());
                    	psEmpresas.add(listBus.get(i).getNoEmpresa()+"");
                    	psChequeras.add(listBus.get(i).getIdChequera()+"");
                    	
                    }
		    	}//Termina if continua
		    }//Termina for
		    

		   if (proveedor == true && dtoCriBus.isChkH2H()){
			   psRegistro = "";	                    	          						
//			   psRegistro=layoutBancomerBusiness.armaBancomerH2H(obtenerParametrosLayout(listBus,0,pbMismoBanco), 4,
//						  totalRegistros, totalArchivo, proveedor, 
//						  mapCreaArch.get("nomArchivo").toString(), dtoCriBus.isConvenioCie());//Arma la cadena

				triler = true;						
				
				if(psRegistro!=null && !psRegistro.equals("")){   
					mapRet = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal,psSubdirectorio,
							mapCreaArch.get("nomArchivo").toString(),psRegistro);
					
					if(!(Boolean)mapRet.get("resExito")){
        				mapRet.put("msgUsuario","Error al generar archivo Bancomer");
						return mapRet;
					}
				}
			}
		   
		   ArchTransferDto insArchTrans = new ArchTransferDto(); 
		   insArchTrans.setNomArch(nomArch);
		   insArchTrans.setIdBanco(piBanco);
		   insArchTrans.setImporte(pdImporte);
		   insArchTrans.setFecTrans(envioTransferenciasADao.obtenerFechaHoy());
		   insArchTrans.setRegistros(piRegistros);
		   insArchTrans.setNoUsuarioAlta(dtoCriBus.getIdUsuario());
		   insArchTrans.setTipoEnvioLayout(psTipoEnvioComerica);
		   
		   envioTransferenciasADao.insertarArchTransfer(insArchTrans);
		   
		   
		   if(!pbEscribi) { //'si no escribiï¿½ ningun registro se sale
			   if(!lsYaEjecutados.equals(""))
                   mapRet.put("msgUsuario", "Los siguientes documentos ya fueron procesados: " + lsYaEjecutados.substring(0,lsYaEjecutados.length()-1));
			   else
				   mapRet.put("msgUsuario","No se realizó ninguna tarea");
               return mapRet;
		   }
		   
		  
		   
		   if(!psPoliza.equals("")){
			   if(dtoCriBus.getOptTipoEnvio().trim().equals("SPEUA") && piBanco==21)
				   psTipoEnvio = "SP";
               else if(piBanco == ConstantesSet.NAFIN)
                   psTipoEnvio = "NAF";
               else if(piBanco == ConstantesSet.BANCOMER && dtoCriBus.isChkConvenioCie())
                   psTipoEnvio = "CIE"; //'Convenio CIE
               else if(piBanco == ConstantesSet.CITIBANK_DLS && dtoCriBus.getOptComerica().equals("ACH"))
                   psTipoEnvio = "ACH"; //'Convenio CIE
               else if(dtoCriBus.getOptTipoEnvio().equals("Mismo"))
                   psTipoEnvio = "MB";
               else
                   psTipoEnvio = "IB";
               
			   if(piBanco!=ConstantesSet.NAFIN){
                   psRutaArchivos = dirPrincipal;
                   psSoloArchivos = mapCreaArch.get("nomArchivo").toString();
               }
                  
              if(llamarCuadrantesTransferenciasAgrupadas(psSoloArchivos, psTipoEnvio)<=0){
                   mapRet.put("msgUsuario","Error al Actualizar estatus de los movimientos de Transferencias es necesario volver a generar el archivo");
                   eliminarArchivosBe(psRutaArchivos,mapCreaArch.get("psBanco").toString(),psSoloArchivos);
                   return mapRet;
               }
              
              if (dtoCriBus.isChkH2H()){
            	  //Para actualizar el foliador cuando se manda por h2h
            	  folioConfirma = layoutsDao.seleccionarFolioReal("folioConfirma");
      			  //consultasGenerales.actualizarFolioReal("folioConfirma");
      			  	      			  
            	  piRespuesta = envioTransferenciasADao.updateMovimiento(psSoloArchivos, folioConfirma);
            	  
            	  if (piRespuesta <= 0)
            		  piRespuesta = envioTransferenciasADao.updateHistMovimiento(psSoloArchivos, folioConfirma);
            	  
    
                 // mapRet.put("msgUsuario", "Los Registros han sido Guardados en el archivo " + listDatosCadena.get(2)+"/"+listDatosCadena.get(3)+
	            	//	   psSubdirectorio + mapCreaArch.get("nomArchivo").toString());
                  mapRet.put("msgUsuario", "Los Registros han sido Guardados en el archivo "  + mapCreaArch.get("nomArchivo").toString());
                  mapRet.put("estatusAv", true);
                  mapRet.put("nombreArchivo", mapCreaArch.get("nomArchivo").toString());
		            if(!lsYaEjecutados.equals(""))
		            	mapRet.put("msgUsuarioDos","Los siguientes documentos ya fueron procesados:"+lsYaEjecutados.substring(0,lsYaEjecutados.length()-1));
              }
              else{
            	 /* folioConfirma = 0;
	  	          piRespuesta = envioTransferenciasADao.updateMovimiento(psSoloArchivos, folioConfirma);
	            	  
	              if (piRespuesta <= 0)
	              {
	            	  piRespuesta = envioTransferenciasADao.updateHistMovimiento(psSoloArchivos, folioConfirma);
	              }
	              */
	             //  mapRet.put("msgUsuario", "Los Registros han sido Guardados en el archivo " + listDatosCadena.get(2)+"/"+listDatosCadena.get(3)+
	            	//	   mapCreaArch.get("psBanco").toString() + mapCreaArch.get("nomArchivo").toString());
            	  String cadenaEjecutados =  !lsYaEjecutados.equals("")? "Los siguientes documentos ya fueron procesados:"+
            			  lsYaEjecutados.substring(0,lsYaEjecutados.length()-1):""; 
	              mapRet.put("msgUsuario", "Los Registros han sido Guardados en el archivo "  + mapCreaArch.get("nomArchivo").toString()+
	            		 "<BR >"+ cadenaEjecutados);
	            		  
	              mapRet.put("estatusAv", true);
	              mapRet.put("nombreArchivo", mapCreaArch.get("nomArchivo").toString());
		         
              }
		   }
   	}catch(Exception e){
			logger.error(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BancaElectronica, C:EnvioTransferenciasBusiness, M:llamarAceptar");
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
		return mapRet;
	}
	
	private  Map<String, Object> obtenerLlave(int noEmpresa){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("estatus", false);
		StringBuffer llave = new StringBuffer();
		int folioBEA;
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			folioBEA = obtenerFolioBEA();
			if (folioBEA != 0) {
				llave.append(funciones.ajustarLongitudCampo(noEmpresa+"", 40, "I", "", " "));
				//llave.append();
				llave.append(funciones.ajustarLongitudCampo(globalSingleton.getUsuarioLoginDto().getIdUsuario()
						+"", 20, "I", "", " "));
				llave.append(funciones.ajustarLongitudCampo(folioBEA+"", 8, "D", "", "0"));
			} else {
				result.put("msjUsuario", "Error al obtener folio de la BEA.");
			}
		} catch (Exception e) {
			logger.error(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BancaElectronica, C:EnvioTransferenciasBusiness, M:obtenerCifrado");
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	private int obtenerFolioBEA(){
		int resultado = 0;
		try {
			
		} catch (Exception e) {
			logger.error(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BancaElectronica, C:EnvioTransferenciasBusiness, M:obtenerCifrado");
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
		return resultado;
	}
	
	
	public int insertaDetArchTransfAgrup (MovimientoDto listBus, String nomArch){
		DetArchTranferAgrup dtoInsertA = new DetArchTranferAgrup();
		int result =0;
		try {
			 
    		dtoInsertA.setNomArch(nomArch);
    		dtoInsertA.setPoHeaders(listBus.getPoHeaders());
    		dtoInsertA.setFecPropuesta(listBus.getFecValor());
    		dtoInsertA.setIdBanco(listBus.getIdBanco());
    		dtoInsertA.setIdEstatusArch("T");
    		dtoInsertA.setIdChequeraBenef(listBus.getIdChequeraBenef());
    		dtoInsertA.setImporte(listBus.getImporte());
    		dtoInsertA.setBeneficiario(listBus.getBeneficiario());
    		dtoInsertA.setSucursal(Integer.parseInt(listBus.getSucDestino()));
    		dtoInsertA.setPlaza(Integer.parseInt(listBus.getPlazaBenef()));
    		dtoInsertA.setNoDocto(listBus.getNoDocto());
    		dtoInsertA.setIdChequera(listBus.getIdChequera());
    		dtoInsertA.setIdBancoBenef(listBus.getIdBancoBenef());
    		dtoInsertA.setPrefijoBenef(listBus.getInstFinan() != null ? listBus.getInstFinan():"");
    		dtoInsertA.setConcepto(listBus.getConcepto());
    		dtoInsertA.setNoEmpresa(listBus.getNoEmpresa());
    		result = envioTransferenciasADao.insertarDetArchTransferAgrup(dtoInsertA);
		} catch (Exception e) {
			logger.error(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BancaElectronica, C:EnvioTransferenciasBusiness, M:insertaDetArchTransfAgrup");
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
		return result;
	}
	

	
	public int llamarCuadrantesTransferenciasAgrupadas(String soloArchivos, String tipoEnvio){
		int result = 0;
		try {
			result = envioTransferenciasADao.llamarCuadrantesTransferenciasAgrupadas(soloArchivos, tipoEnvio);
		} catch (Exception e) {
			logger.error(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:EnvioTransferenciasBusiness, M:llamarCuadrantesTransferenciasAgrupadas");	
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
		return result;
	}
	public void eliminarArchivosBe(String dirPrincipal,String subCarpetas, String nomArchivos){
		int iPosicionComa=0;
		String sCadenaAuxiliar="";
		String psArchivo="";
		String psArchivosEliminar="";
		try{
			sCadenaAuxiliar = nomArchivos;
			psArchivosEliminar="'"+psArchivosEliminar.replace(".txt","").replace(",","','")+"'";
			envioTransferenciasADao.eliminarArchTransferAgrup(psArchivosEliminar);
			
			/*if(!sCadenaAuxiliar.equals(""))
			{
				while(sCadenaAuxiliar.indexOf(",")>0){
					iPosicionComa = sCadenaAuxiliar.indexOf(",");
					if(iPosicionComa>0)
					{
						psArchivo = sCadenaAuxiliar.substring(0,iPosicionComa);
						creacionArchivosBusiness.eliminarArchivoLayout(dirPrincipal, subCarpetas, psArchivo);
					}
					sCadenaAuxiliar = sCadenaAuxiliar.substring(iPosicionComa+1);
				}
				if(sCadenaAuxiliar.indexOf(",")==0)
					creacionArchivosBusiness.eliminarArchivoLayout(dirPrincipal, subCarpetas, sCadenaAuxiliar);
			}*/
		}catch(Exception e){
			logger.error(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BancaElectronica, C:EnvioTransferenciasBusiness, M:eliminarArchivosBe");	
			logger.error(e.getLocalizedMessage());
		}
	}
	
	public ParametrosLayoutDto obtenerParametrosLayout(List<MovimientoDto> list, int i, boolean mismoBanco)
	{
		try{
			ParametrosLayoutDto paramLayDto = new ParametrosLayoutDto();
			
			paramLayDto.setMismoBanco(mismoBanco);
			paramLayDto.setIdChequeraBenef(list.get(i).getIdChequeraBenef());
			paramLayDto.setPiPlaza(list.get(i).getPlaza());
			paramLayDto.setPiPlazaBenef(list.get(i).getPlazaBenef()!=null?Integer.parseInt(list.get(i).getPlazaBenef()):0);
			paramLayDto.setIdChequera(list.get(i).getIdChequera());
			paramLayDto.setImporte(list.get(i).getImporte());
			paramLayDto.setBeneficiario(list.get(i).getBeneficiario());
			paramLayDto.setIdBancoBenef(list.get(i).getIdBancoBenef()>0?""+list.get(i).getIdBancoBenef():"");
			paramLayDto.setNoFolioDet(list.get(i).getNoFolioDet());
			paramLayDto.setPsSucursal(list.get(i).getSucDestino());
			paramLayDto.setPsDivisa(list.get(i).getIdDivisa());
			paramLayDto.setPsReferenciaTraspaso("");
			paramLayDto.setNoCliente(list.get(i).getNoCliente()!=null?Integer.parseInt(list.get(i).getNoCliente()):0);
			paramLayDto.setPsClabe(list.get(i).getClabeBenef());
			paramLayDto.setConcepto(list.get(i).getConcepto());
			paramLayDto.setNoDocto(list.get(i).getNoDocto());
			paramLayDto.setFecOperacion(list.get(i).getFecOperacion());
			paramLayDto.setDescripcion(list.get(i).getDescripcion());
			paramLayDto.setIdDivisa(list.get(i).getIdDivisa());
			paramLayDto.setRfcBenef(list.get(i).getRfcBenef());
			paramLayDto.setNomEmpresa(list.get(i).getNomEmpresa());
			paramLayDto.setPoHeaders(list.get(i).getPoHeaders());
			paramLayDto.setNoEmpresa(list.get(i).getNoEmpresa());
			return paramLayDto;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:BancaElectronica, C:EnvioTransferenciasBusiness, M:parametrosLayout");
			return null;
		}
		
	}
	
	public JRDataSource reporteDetArchivoTransf(CriterioBusquedaDto dtoBus){
		JRMapArrayDataSource jrDataSource = null;
		List<Map<String, Object>> listReport = new ArrayList<Map<String, Object>>();
		
		try {
			listReport = envioTransferenciasADao.reporteDetArchivoTransf(dtoBus);
	        jrDataSource = new JRMapArrayDataSource(listReport.toArray());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:EnvioTransferenciasBusiness, M:reporteDetArchivoTransf");
		}
		return jrDataSource;
	}
	
	public int buscaDatosBenef(List<Map<String, String>> objParams, int i) {
		return envioTransferenciasADao.buscaDatosBenef(objParams, i);
	}
	
	public List<LlenaComboGralDto> obtenerBancosBenef(int noPersona) {
		return envioTransferenciasADao.obtenerBancosBenef(noPersona);
	}
	
	public List<LlenaComboGralDto> llenaComboCveControl() {
		List<LlenaComboGralDto> result = new ArrayList<LlenaComboGralDto>();
		try {
			result = envioTransferenciasADao.llenaComboCveControl();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +
					"P:Egresos, C:EnvioTransferenciasBusiness, M:actualizaMovtoCheqBenef");
		}
		return result;
	}
	
	public List<LlenaComboGralDto> obtenerChequerasBenef(int noPersona, int idBanco) {
		return envioTransferenciasADao.obtenerChequerasBenef(noPersona, idBanco);
	}
	
	public String actualizaMovtoCheqBenef(String noFactura, int noEmpresa, int noCliente, int noFolioMov, int idBanco, String chequera) {
		int resp = 0;
		String msg = "";
		
		try {
			resp = envioTransferenciasADao.actualizaMovtoCheqBenef(noFactura, noEmpresa, noCliente, noFolioMov, idBanco, chequera);
			
			if(resp == 0)
				msg = "No se encontraron registros para actualizar";
			else
				msg = "Registros actualizados correctamente";
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:EnvioTransferenciasBusiness, M:actualizaMovtoCheqBenef");
		}
		return msg;
	}
	public LayoutBanorteBusiness getLayoutBanorteBusiness() {
		return layoutBanorteBusiness;
	}
	public void setLayoutBanorteBusiness(LayoutBanorteBusiness layoutBanorteBusiness) {
		this.layoutBanorteBusiness = layoutBanorteBusiness;
	}
	
	public EnvioTransferenciasADao getenvioTransferenciasADao() {
		return envioTransferenciasADao;
	}

	public void setenvioTransferenciasADao(
			EnvioTransferenciasADao envioTransferenciasADao) {
		this.envioTransferenciasADao = envioTransferenciasADao;
	}
	public SegUsuarioPerfilBusiness getSegUsuarioPerfilBusiness() {
		return segUsuarioPerfilBusiness;
	}
	public void setSegUsuarioPerfilBusiness(
			SegUsuarioPerfilBusiness segUsuarioPerfilBusiness) {
		this.segUsuarioPerfilBusiness = segUsuarioPerfilBusiness;
	}
	public LayoutBancomerBusiness getLayoutBancomerBusiness() {
		return layoutBancomerBusiness;
	}
	public void setLayoutBancomerBusiness(
			LayoutBancomerBusiness layoutBancomerBusiness) {
		this.layoutBancomerBusiness = layoutBancomerBusiness;
	}
	public CreacionArchivosBusiness getCreacionArchivosBusiness() {
		return creacionArchivosBusiness;
	}
	public void setCreacionArchivosBusiness(
			CreacionArchivosBusiness creacionArchivosBusiness) {
		this.creacionArchivosBusiness = creacionArchivosBusiness;
	}
	public LayoutAztecaBusiness getLayoutAztecaBusiness() {
		return layoutAztecaBusiness;
	}
	public void setLayoutAztecaBusiness(LayoutAztecaBusiness layoutAztecaBusiness) {
		this.layoutAztecaBusiness = layoutAztecaBusiness;
	}
	public LayoutScotiabankInverlatBusiness getLayoutScotiabankInverlatBusiness() {
		return layoutScotiabankInverlatBusiness;
	}
	public void setLayoutScotiabankInverlatBusiness(LayoutScotiabankInverlatBusiness layoutScotiabankInverlatBusiness) {
		this.layoutScotiabankInverlatBusiness = layoutScotiabankInverlatBusiness;
	}

}
