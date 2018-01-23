package com.webset.set.layout.business;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webset.set.bancaelectronica.business.EnvioTransferenciasBusiness;
import com.webset.set.bancaelectronica.dao.EnvioTransferenciasADao;
import com.webset.set.bancaelectronica.dao.EnvioTransferenciasDao;
import com.webset.set.bancaelectronica.dto.CriterioBusquedaDto;
import com.webset.set.bancaelectronica.dto.DetArchTranferAgrup;
import com.webset.set.bancaelectronica.dto.DetArchTransferDto;
import com.webset.set.layout.dao.LayoutsDao;
import com.webset.set.layout.dto.ParametrosLayoutDto;
import com.webset.set.traspasos.dto.BuscarSolicitudesTraspasosDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.MovimientoDto;

/*
 * @author: Victor H. Tello
 * @since: 02/Enero/2012
 */

public class LayoutBancomerBusiness {
	Bitacora bitacora = new Bitacora();
	Funciones funciones = new Funciones();
	ConsultasGenerales consultasGrales;
	EnvioTransferenciasDao envioTransferenciasDao;
	EnvioTransferenciasADao envioTransferenciasADao;
	
	public EnvioTransferenciasADao getEnvioTransferenciasADao() {
		return envioTransferenciasADao;
	}
	public void setEnvioTransferenciasADao(EnvioTransferenciasADao envioTransferenciasADao) {
		this.envioTransferenciasADao = envioTransferenciasADao;
	}
	private LayoutsDao layoutsDao;
	List<CriterioBusquedaDto> structBE = new ArrayList<CriterioBusquedaDto>();
    List<CriterioBusquedaDto> structEquivaleCampo = new ArrayList<CriterioBusquedaDto>();
    List<CriterioBusquedaDto> structEquivaleBE = new ArrayList<CriterioBusquedaDto>();
    
    CreacionArchivosBusiness creacionArchivosBusiness = new CreacionArchivosBusiness();
    
    
    
	Map<String, Object> mapResp = new HashMap<String, Object>();
	int iBanco = 0;
    int liTipoTransfer = 0;
    
	//Cash Windows
	public String beArmaBancomer(ParametrosLayoutDto dto){
		StringBuffer psRegistro = new StringBuffer() ;
		Map<String,Object> valorPagRef = new HashMap<String,Object>();
	    int plIdBancoBancomer=0;
	    String psPlazaLayout="";
	    String psDivisaLayout="";
	    int piDivisa=0;
	    String psReferencia="";
	    String psIdChequeraBenefCLABE="";
	    String psCampo="";
	    String psBeneficiario="";
	    String sConcepto="";
		try{
		    if(dto.getPsDivisa()!= null && dto.getPsDivisa().trim().equals("MN"))
		    {
	        	psDivisaLayout = "MXP";
	        	piDivisa = 1;
		    }
		    else if(dto.getPsDivisa()!= null && dto.getPsDivisa().trim().equals("DLS"))
		    {
		        psDivisaLayout = "UCY";
		        piDivisa = 2;
		    }
		    else
		    {
		        //psDivisaLayout = Space(3) 
		    	String.format("%1$3s", psDivisaLayout);
		        piDivisa = 0;
		    }
		    
		    if(dto.getPsReferenciaTraspaso()!=null && !dto.getPsReferenciaTraspaso().equals(""))
		        psReferencia = dto.getPsReferenciaTraspaso();
		    else
		        psReferencia = ""+dto.getNoFolioDet();
		    
		    if(!dto.isMismoBanco())
		    {
		    	psRegistro = new StringBuffer();//'SUMA 165 CARACTERES
	            if(layoutsDao.consultarConfiguraSet(209)!=null && layoutsDao.consultarConfiguraSet(209).equals("SI"))
	                psIdChequeraBenefCLABE = dto.getPsClabe();
	            else
	                psIdChequeraBenefCLABE = dto.getIdChequeraBenef();
	            
	            psRegistro.append(funciones.ponerCeros(psIdChequeraBenefCLABE, 18));//1.-No. De cuenta definida para el  cargo (Nu)18
	            if(dto.getPiPlaza()==0)
	                psPlazaLayout = funciones.ponerCeros(""+piDivisa, 3);
	            else
	                psPlazaLayout = funciones.ponerCeros(""+dto.getPiPlaza(), 3)+piDivisa;
	           
	            if(dto.getIdChequera()!=null && dto.getIdChequera().trim().length()==13)
	                psRegistro.append(funciones.ponerCeros(dto.getIdChequera(),18));       //2.-No. De cuenta definida para el abono (NU) 18
	            else
	                psRegistro .append(funciones.ponerCeros(psPlazaLayout+dto.getIdChequera().substring(3,11), 18));//ps_registro & Format(psPlaza_Layout & Mid(ps_id_chequera, 4, 8), String(18, "0")) '2.- Cuenta Origen (NU) 18
	            
	            psRegistro.append(psDivisaLayout);  //3.- Moneda (Nu) 3
	            psRegistro.append(funciones.ponerFormatoCeros(dto.getImporte(),13)); //4.- Monto (NU) 16
	            psBeneficiario=funciones.quitarCaracteresRaros(dto.getBeneficiario(), true);
	            psBeneficiario=funciones.ocultarCoinversora(psBeneficiario);
	            psBeneficiario=funciones.ajustarLongitudCampo(psBeneficiario,30,"I","","");
	            
	            psRegistro.append(psBeneficiario);           /*5.- Beneficiario (Nu) 30 La informacinn aqun plasmada
													            sern enviada al estado de
													            //cuenta del Ordenante y del
													            // Beneficiario*/
	            
	            if(layoutsDao.seleccionarbanco(dto.getIdBancoBenef()!=null?Integer.parseInt(dto.getIdBancoBenef()):0)>0)
	            	plIdBancoBancomer = layoutsDao.seleccionarbanco(Integer.parseInt(dto.getIdBancoBenef()));
	            
	            //'Tipo de cuenta
	            if(layoutsDao.consultarConfiguraSet(209)!=null && layoutsDao.consultarConfiguraSet(209).trim().equals("SI"))
	                psRegistro.append("40");   // 'Cuenta clabe                                        
	            else                                                                                //' ya es 03 para Tarjeta de debito y 40 cuenta clabe
	            	psRegistro.append("03");      //'Tarjeta de Debito                                    'Se agrego este nuevo dato para el tipo de cuenta
	            
	            psRegistro.append(funciones.ponerCeros(""+plIdBancoBancomer,3)); //'8.-Numero de banco del Beneficiario(NU) 3 a 3 posiciones
	            
	            sConcepto=funciones.ajustarLongitudCampo(dto.getConcepto(),30,"I","","");
	            
	            psRegistro.append(sConcepto);
	            
	            if(dto.getPsReferenciaTraspaso()!=null && dto.getPsReferenciaTraspaso().equals(""))
	            {
	            	valorPagRef=layoutsDao.obtenerValorPagoReferenciado(dto.getNoCliente(), dto.getNoFolioDet(),"");
	            	psReferencia=valorPagRef.get("valorPagRef").toString();
	            }
	            
	            psReferencia = obtenerReferencias(dto.getNoCliente(), dto.getNoFolioDet()).get("refN").toString();
	            psCampo =funciones.ajustarLongitudCampo(psReferencia, 7, "D", "", "");
	            psRegistro.append(psCampo);               //'10.- 'Referencia numerica

	            psRegistro.append("H");                     //'10.- disponibilidad H es hoy y M es manana
	            
	            psRegistro.append("0");                     //'11. Comprobante fiscal 0 no aplica y 1 si se requiere
	            
	            psRegistro.append(String.format("%1$18s", ""));               //'11. rfc del beneficiario 18 espacios en blanco
	            
	            psRegistro.append(funciones.ponerFormatoCeros(0.0, 15));

		    }else{
		    	//'REGISTRO DE TRANSACCION AL MISMO BANCO
			    psRegistro = new StringBuffer();
	            
	            //'Plaza origen
	            if(dto.getPiPlaza()==0)
	                psPlazaLayout = funciones.ponerCeros(""+piDivisa,3);
	            else
	                psPlazaLayout = funciones.ponerCeros(""+dto.getPiPlaza(),3)+piDivisa;
	            
	                        
	            if(dto.getIdChequera()!=null && dto.getIdChequera().length()==13)
	                psRegistro.append(funciones.ponerCeros(dto.getIdChequera(),18)); //'2.- Cuenta Origen (NU) 18
	            else
	                psRegistro.append(funciones.ponerCeros(psPlazaLayout+dto.getIdChequera().substring(3,11),18));//      '2.- Cuenta Origen (NU) 18
	            
	            
	            //'Plaza destino
	            //'Mandar la CLABE si es transferencia
	            if((dto.getPsReferenciaTraspaso()!=null && dto.getPsReferenciaTraspaso().equals("")) || (dto.getPsClabe()!=null && dto.getPsClabe().equals("")))
	            {
	            	if(dto.getPiPlazaBenef()==0)
	                    psPlazaLayout = funciones.ponerCeros(""+piDivisa,3);
	                else
	                    psPlazaLayout =funciones.ponerCeros(""+dto.getPiPlazaBenef(),3)+piDivisa;
	                
	                if(dto.getIdChequeraBenef()!=null && dto.getIdChequeraBenef().length()==13) 
	                    psRegistro.append(funciones.ponerCeros(dto.getIdChequeraBenef(),18));                 //'2.- Cuenta Origen (NU) 18
	                else
	                    psRegistro.append(funciones.ponerCeros(psPlazaLayout+dto.getIdChequeraBenef().substring(3,11),18));   //'2.- Cuenta Origen (NU) 18
	                
	            }else{
	            	psCampo = dto.getPsClabe();
	                psCampo = funciones.ajustarLongitudCampo(psCampo,18,"D","","0");
	                psRegistro.append(psCampo);                           //'2.- Cuenta Origen (NU) 18
	            }
	            
	            psRegistro.append(psDivisaLayout);  //' "MXP"             '3.- Moneda (AL) 3
	            psRegistro.append(funciones.ponerFormatoCeros(dto.getImporte(),13)); //'4.- Monto (NU) 16
	            
	            //'Motivo de pago
	            //'se agrego este nuevo campo para el armado
	       
	            sConcepto = funciones.ajustarLongitudCampo(dto.getConcepto(),30,"I","",""); 
	        
	            psRegistro.append(sConcepto);
	            
	            //'Comprobante fiscal (sin comprobante fisca)
	            psRegistro .append("0");
	            
	            //'RFC del beneficiario (solo si es con comprobante fiscal)
	            psRegistro.append(String.format("%1$18s", ""));
	            
	            //'IVA del monto (solo si es con comprobante fiscal)
	            psRegistro.append(funciones.ponerFormatoCeros(0,12));
		    }
            
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:com.webset.set.layout, C:LayoutBancomer, M:beArmaBancomer");
			e.printStackTrace();
		}
		
		return psRegistro.toString();
	}
	public String beArmaBancomerInternacional(ParametrosLayoutDto dto, MovimientoDto movimiento) {
		StringBuffer psRegistro = new StringBuffer();
		//String psIdChequeraBenefCLABE = "";
	    //String psCampo = "";
	    String psDivisaLayout = "";
	    String psChequera = "";
		try {
			 if(dto.getPsDivisa() != null && dto.getPsDivisa().trim().equals("MN")) {
		        	psDivisaLayout = "MXP";
		        	//piDivisa = 1;
			    } else if(dto.getPsDivisa() != null && dto.getPsDivisa().trim().equals("DLS")) {
			        psDivisaLayout = "USD";
			       // piDivisa = 2;
			    } else {
			        //psDivisaLayout = Space(3) 
			    	String.format("%1$3s", psDivisaLayout);
			       // piDivisa = 0;
			    }
		    
			String psCampoE = "";
			System.out.println("clabe internacional "+dto.getPsClabe()+" "+movimiento.getClabeBenef());
	    	if (dto.getPsClabe() != null && !dto.getPsClabe().equals("")) {
	    		psChequera = dto.getPsClabe();
			} else {
				psChequera = dto.getIdChequeraBenef();
			}
	    	//List<String> recibeDatos = new ArrayList<String>();
	    	//recibeDatos = layoutsDao.obtieneDatosBanco(movimiento.getIdBanco());
			List<String> recibeDatosMov = new ArrayList<String>();
	    	recibeDatosMov = layoutsDao.obtieneDatosBancoMovimientoFolioDet(movimiento.getNoFolioDet());
	    	psRegistro.append("OPI");

			if(dto.getIdChequera().length() == 13)
				psRegistro.append(funciones.ajustarLongitudCampo(dto.getIdChequera().substring(3,dto.getIdChequera().length()), 18, "D", "", "0"));	//3.- Cuenta Origen (NU) 18
			else
				psRegistro.append(funciones.ajustarLongitudCampo(dto.getIdChequera(), 18, "D", "", "0"));	//1.- Cuenta Destino (Nu)18
			
	    	//psRegistro.append(funciones.ponerCeros(dto.getIdChequera(), 18));											
	    	psRegistro.append(funciones.ajustarLongitudCampo(psChequera, 35, "I", "", " "));
	    	psRegistro.append(funciones.ponerFormatoCeros(dto.getImporte(), 13));											//4.- Monto (NU) 16
	    	psRegistro.append(psDivisaLayout);																				//3.- Moneda (AL) 3
	    	
	    	
	    	psRegistro.append(funciones.ajustarLongitudCampo(!recibeDatosMov.isEmpty() && recibeDatosMov.size() != 0 ?		//referencia
	    			funciones.quitarCaracteresRaros(recibeDatosMov.get(3), true):"", 50, "I", "", " "));
	    	
	    	psRegistro.append(funciones.ajustarLongitudCampo(!recibeDatosMov.isEmpty() && recibeDatosMov.size() != 0 ?		//aba
	    			funciones.quitarCaracteresRaros(recibeDatosMov.get(0), true):"", 15, "I", "", " "));
	    	
	    	psRegistro.append(funciones.ajustarLongitudCampo(!recibeDatosMov.isEmpty() && recibeDatosMov.size() != 0 ?	//descripcion del banco
	    			funciones.quitarCaracteresRaros(recibeDatosMov.get(1), true):"", 30, "I", "", " "));
	    	
	    	psRegistro.append(funciones.ajustarLongitudCampo(psCampoE, 30, "I", "", " "));							// pais del banco
	    	
	    	psRegistro.append(funciones.ajustarLongitudCampo(psCampoE, 40, "I", "", " "));							// Direccion del banco
	    	
	    	psRegistro.append(funciones.ajustarLongitudCampo(!recibeDatosMov.isEmpty() && recibeDatosMov.size() != 0 ?
	    			funciones.quitarCaracteresRaros(recibeDatosMov.get(4), true):"", 30, "I", "", " "));			// Beneficiario
	    	
	    	psRegistro.append(funciones.ajustarLongitudCampo(movimiento.getPaisBenef() != null ?					// pais del beneficiario
	    			funciones.quitarCaracteresRaros(movimiento.getPaisBenef(), true):"", 30, "I", "", " "));
	    	
	    	psRegistro.append(funciones.ajustarLongitudCampo(!recibeDatosMov.isEmpty() && recibeDatosMov.size() != 0 ?				// direccion del beneficiario
	    			funciones.quitarCaracteresRaros(recibeDatosMov.get(5), true):"", 40, "I", "", " "));
	    	
	    	psRegistro.append(funciones.ajustarLongitudCampo(!recibeDatosMov.isEmpty() && recibeDatosMov.size() != 0 ?
	    			
	    			funciones.quitarCaracteresRaros(recibeDatosMov.get(6), true):"", 12, "D", "", "0"));							// telefono
	    	
	    	
	    	/*
	    	obtieneDatosBanco.add(rs.getString("aba"));
					obtieneDatosBanco.add(rs.getString("descripcion_banco"));
					obtieneDatosBanco.add(rs.getString("Concepto"));
					obtieneDatosBanco.add(rs.getString("Referencia"));
					obtieneDatosBanco.add(rs.getString("beneficiario"));
					obtieneDatosBanco.add(rs.getString("Calle_No"));
					obtieneDatosBanco.add(rs.getString("numero"));
	    	*/
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:com.webset.set.layout, C:LayoutBancomer, M:beArmaBancomerInternacional");
			e.printStackTrace();
		}
		return psRegistro.toString();
	}
	
	
	public String beArmaBancomerInternacionalT(ParametrosLayoutDto dto, BuscarSolicitudesTraspasosDto movimiento) {
		StringBuffer psRegistro = new StringBuffer();
	    String psDivisaLayout = "";
	    String psChequera = "";
		try {
			 if(dto.getPsDivisa() != null && dto.getPsDivisa().trim().equals("MN")) {
		        	psDivisaLayout = "MXP";
		        	
			    } else if(dto.getPsDivisa() != null && dto.getPsDivisa().trim().equals("DLS")) {
			        psDivisaLayout = "USD";
			    } else { 
			    	String.format("%1$3s", psDivisaLayout);
			    }
			    
			String psCampoE = "";
	    	System.out.println("clabe internacional "+dto.getPsClabe()+" ");
			if (dto.getPsClabe() != null && !dto.getPsClabe().equals("")) {
	    		psChequera = dto.getPsClabe();
			} else {
				psChequera = dto.getIdChequeraBenef();
			}
			
	    	List<String> recibeDatosMov = new ArrayList<String>();
	    	recibeDatosMov = layoutsDao.obtieneDatosBancoMovimientoFolioDet2(movimiento.getNoFolioDet());
	    	System.out.println(" resulte de obtine dartos"+recibeDatosMov);
	    	psRegistro.append("OPI");

			if(dto.getIdChequera().length() == 13)
				psRegistro.append(funciones.ajustarLongitudCampo(dto.getIdChequera().substring(3,dto.getIdChequera().length()), 18, "D", "", "0"));	//3.- Cuenta Origen (NU) 18
			else
				psRegistro.append(funciones.ajustarLongitudCampo(dto.getIdChequera(), 18, "D", "", "0"));	//1.- Cuenta Destino (Nu)18
			
	    	//psRegistro.append(funciones.ponerCeros(dto.getIdChequera(), 18));											//1.- Cuenta Destino (Nu)18
	    	psRegistro.append(funciones.ajustarLongitudCampo(psChequera, 35, "I", "", " "));
	    	psRegistro.append(funciones.ponerFormatoCeros(dto.getImporte(), 13));											//4.- Monto (NU) 16
	    	psRegistro.append(psDivisaLayout);																				//3.- Moneda (AL) 3
	    	
	    	
	    	psRegistro.append(funciones.ajustarLongitudCampo(!recibeDatosMov.isEmpty() && recibeDatosMov.size() != 0 ?		//referencia
	    			funciones.quitarCaracteresRaros(recibeDatosMov.get(3), true):"", 50, "I", "", " "));
	    	
	    	psRegistro.append(funciones.ajustarLongitudCampo(!recibeDatosMov.isEmpty() && recibeDatosMov.size() != 0 ?		//aba
	    			funciones.quitarCaracteresRaros(recibeDatosMov.get(0), true):"", 15, "I", "", " "));
	    	
	    	psRegistro.append(funciones.ajustarLongitudCampo(!recibeDatosMov.isEmpty() && recibeDatosMov.size() != 0 ?	//descripcion del banco
	    			funciones.quitarCaracteresRaros(recibeDatosMov.get(1), true):"", 30, "I", "", " "));
	    	
	    	psRegistro.append(funciones.ajustarLongitudCampo(psCampoE, 30, "I", "", " "));							// pais del banco
	    	
	    	psRegistro.append(funciones.ajustarLongitudCampo(psCampoE, 40, "I", "", " "));							// Direccion del banco
	    	
	    	psRegistro.append(funciones.ajustarLongitudCampo(!recibeDatosMov.isEmpty() && recibeDatosMov.size() != 0 ?
	    			funciones.quitarCaracteresRaros(recibeDatosMov.get(2), true):"", 30, "I", "", " "));			// Beneficiario
	    	
	    	psRegistro.append(funciones.ajustarLongitudCampo(movimiento.getPaisBenef() != null ?					// pais del beneficiario
	    			funciones.quitarCaracteresRaros(movimiento.getPaisBenef(), true):"", 30, "I", "", " "));
	    	
	    	psRegistro.append(funciones.ajustarLongitudCampo(movimiento.getDireccionBenef() != null ?				// direccion del beneficiario
	    			funciones.quitarCaracteresRaros(movimiento.getDireccionBenef(),true):"", 40, "I", "", " "));
	    	
	    	psRegistro.append(funciones.ajustarLongitudCampo(psCampoE, 12, "D", "", "0"));							// telefono
	    	
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:com.webset.set.layout, C:LayoutBancomer, M:beArmaBancomerInternacionalT");
			e.printStackTrace();
		}
		return psRegistro.toString();
	}
	
	public String beArmaBancomerInternacionalAgrupado(ParametrosLayoutDto dto, MovimientoDto movimiento) {
		StringBuffer psRegistro = new StringBuffer();
	    String psDivisaLayout = "";
	    String psChequera = "";
		try {
			 if(dto.getPsDivisa() != null && dto.getPsDivisa().trim().equals("MN")) {
		        	psDivisaLayout = "MXP";
		        	//piDivisa = 1;
			    } else if(dto.getPsDivisa() != null && dto.getPsDivisa().trim().equals("DLS")) {
			        psDivisaLayout = "USD";
			       // piDivisa = 2;
			    } else {
			        //psDivisaLayout = Space(3) 
			    	String.format("%1$3s", psDivisaLayout);
			       // piDivisa = 0;
			    }
		    
			String psCampoE = "";
			System.out.println("clabe internacional "+dto.getPsClabe()+" "+movimiento.getClabeBenef());
	    	if (dto.getPsClabe() != null && !dto.getPsClabe().equals("")) {
	    		psChequera = dto.getPsClabe();
			} else {
				psChequera = dto.getIdChequeraBenef();
			}
	    	//List<String> recibeDatos = new ArrayList<String>();
	    	//recibeDatos = layoutsDao.obtieneDatosBanco(movimiento.getIdBanco());
			///List<String> recibeDatosMov = new ArrayList<String>();
	    	//recibeDatosMov = layoutsDao.obtieneDatosBancoMovimientoFolioDet(movimiento.getNoFolioDet());
	    	String referenciaAgrup = layoutsDao.obtenerReferenciaAgrupada(dto.getNoEmpresa(), 12, dto.getIdChequera(), dto.getPoHeaders());
	    	List<String> recibeDatosProv = new ArrayList<String>();
	    	recibeDatosProv = layoutsDao.obtineDatosProveedor(dto.getNoEmpresa(), 12, dto.getIdChequera(), dto.getPoHeaders());
	    	psRegistro.append("OPI");
	    	
			if(dto.getIdChequera().length() == 13)
				psRegistro.append(funciones.ajustarLongitudCampo(dto.getIdChequera().substring(3,dto.getIdChequera().length()), 18, "D", "", "0"));	//3.- Cuenta Origen (NU) 18
			else
				psRegistro.append(funciones.ajustarLongitudCampo(dto.getIdChequera(), 18, "D", "", "0"));	//1.- Cuenta Destino (Nu)18
			
	    	//psRegistro.append(funciones.ponerCeros(dto.getIdChequera(), 18));											//1.- Cuenta Destino (Nu)18
	    	psRegistro.append(funciones.ajustarLongitudCampo(psChequera, 35, "I", "", " "));
	    	psRegistro.append(funciones.ponerFormatoCeros(dto.getImporte(), 13));											//4.- Monto (NU) 16
	    	psRegistro.append(psDivisaLayout);																				//3.- Moneda (AL) 3
	    	
	    	
	    	psRegistro.append(funciones.ajustarLongitudCampo(referenciaAgrup != null && !referenciaAgrup.equals("") ?		//referencia
	    			funciones.quitarCaracteresRaros(referenciaAgrup, true):"", 50, "I", "", " "));
	    	
	    	psRegistro.append(funciones.ajustarLongitudCampo(movimiento.getAba() != null && !movimiento.getAba().equals("") ?		//aba
	    			funciones.quitarCaracteresRaros(movimiento.getAba(), true):movimiento.getSwiftCode()!= null && !movimiento.getSwiftCode().equals("")?
	    			movimiento.getSwiftCode():"", 15, "I", "", " "));
	    	
	    	psRegistro.append(funciones.ajustarLongitudCampo(movimiento.getDescBancoBenef() != null && !movimiento.getDescBancoBenef().equals("") ?	//descripcion del banco
	    			funciones.quitarCaracteresRaros(movimiento.getDescBancoBenef(), true):"", 30, "I", "", " "));
	    	
	    	psRegistro.append(funciones.ajustarLongitudCampo(psCampoE, 30, "I", "", " "));							// pais del banco
	    	
	    	psRegistro.append(funciones.ajustarLongitudCampo(psCampoE, 40, "I", "", " "));							// Direccion del banco
	    	
	    	psRegistro.append(funciones.ajustarLongitudCampo(dto.getBeneficiario() != null && !dto.getBeneficiario().equals("")?
	    			funciones.quitarCaracteresRaros(dto.getBeneficiario(), true):"", 30, "I", "", " "));			// Beneficiario
	    	
	    	psRegistro.append(funciones.ajustarLongitudCampo(!recibeDatosProv.isEmpty() && recibeDatosProv.size() != 0 ?	// pais del beneficiario
	    			funciones.quitarCaracteresRaros(recibeDatosProv.get(0), true):"", 30, "I", "", " "));
	    	
	    	psRegistro.append(funciones.ajustarLongitudCampo(movimiento.getDireccionBenef() != null && !movimiento.getDireccionBenef().equals("") ?				// direccion del beneficiario
	    			funciones.quitarCaracteresRaros(movimiento.getDireccionBenef(), true):"", 40, "I", "", " "));
	    	
	    	psRegistro.append(funciones.ajustarLongitudCampo(!recibeDatosProv.isEmpty() && recibeDatosProv.size() != 0 ?
	    			
	    			funciones.quitarCaracteresRaros(recibeDatosProv.get(1), true):"", 12, "D", "", "0"));							// telefono
	    	
	    
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:com.webset.set.layout, C:LayoutBancomer, M:beArmaBancomerInternacional");
			e.printStackTrace();
		}
		return psRegistro.toString();
	}
	
	
	//NetCash c43
	public String beArmaBancomerC43(ParametrosLayoutDto dto) {
		StringBuffer psRegistro = new StringBuffer() ;
		Map<String, Object> valorPagRef = new HashMap<String, Object>();
	    //String psPlazaLayout = "";
	    String psDivisaLayout = "";
	    //int piDivisa = 0;
	    String psReferencia = "";
	    String psIdChequeraBenefCLABE = "";
	    String psCampo = "";
	    String psBeneficiario = "";
	    String sConcepto = "";
	    String psRefAux = "";
	   // String psfolio = "";
	    
		try {
		    if(dto.getPsDivisa() != null && dto.getPsDivisa().trim().equals("MN")) {
	        	psDivisaLayout = "MXP";
	        	//piDivisa = 1;
		    } else if(dto.getPsDivisa() != null && dto.getPsDivisa().trim().equals("DLS")) {
		        psDivisaLayout = "USD";
		       // piDivisa = 2;
		    } else {
		        //psDivisaLayout = Space(3) 
		    	String.format("%1$3s", psDivisaLayout);
		       // piDivisa = 0;
		    }
		    
		    if(dto.getPsReferenciaTraspaso() != null && !dto.getPsReferenciaTraspaso().equals(""))
		        psReferencia = dto.getPsReferenciaTraspaso();
		    else
		        psReferencia = "" + dto.getNoFolioDet();
		    psRefAux = psReferencia;
		    
		    //MISMO BANCO SUMA 85 CARACTERES
		    if(dto.isMismoBanco()) {
		    	psRegistro = new StringBuffer();
		    	//String referenciaPoliza = layoutsDao.obtienePolizaConpensa(dto.getNoFolioDet()+"");
		    	String psChequera = "";
		    	//psRegistro.append("PTC");
	            if (dto.getPsClabe() != null && !dto.getPsClabe().equals("")) {
		    		psChequera = dto.getPsClabe();
		    		 psRegistro.append(funciones.ponerCeros(psChequera, 18));									//1.- Cuenta Destino (NU) 18
				} else {
					psChequera = dto.getIdChequeraBenef();
					psRegistro.append(funciones.ponerCeros(psChequera, 18));									//1.- Cuenta Destino (NU) 18
				}
	            
	            String chequeraOrigen =  layoutsDao.obtieneComplementosBancoOrigen(dto.getIdChequera(),12);
	            if (chequeraOrigen != null && !chequeraOrigen.equals("")) {
	        	   	psRegistro.append(funciones.ponerCeros(chequeraOrigen,18));											//2.- Cuenta Origen (NU) 18
				} else {
					psRegistro.append(funciones.ponerCeros(dto.getIdChequera(),18));											//2.- Cuenta Origen (NU) 18
				}
	            
	            psRegistro.append(psDivisaLayout);																				//3.- Moneda (AL) 3
	            psRegistro.append(funciones.ponerFormatoCeros(dto.getImporte(), 13));											//4.- Monto (NU) 16
		    	
	            if(dto.getPsReferenciaTraspaso() != null && dto.getPsReferenciaTraspaso().equals("")) {
	            	//Solo aplica para Transferencias, Pagos referenciados a Clientes
	            	valorPagRef = layoutsDao.obtenerValorPagoReferenciado(dto.getNoCliente(), dto.getNoFolioDet(), "");
	            	psReferencia = valorPagRef.get("valorPagRef").toString();
	                  }
	            //Se cambia la referecnia para meter espacios en blanco a la derecha para completar la lognitud del layout
	            psRegistro.append(funciones.ajustarLongitudCampo("" + funciones.quitarCaracteresRaros(psReferencia, true), 30, "I", "", " "));
	            psRegistro.append("\n");
		    }else {
		    	//INTERBANCARIA SUMA 165 CARACTERES
		    	psRegistro = new StringBuffer();
		    	String tipoCuenta = "";
		    	if((layoutsDao.consultarConfiguraSet(209) != null && layoutsDao.consultarConfiguraSet(209).equals("SI"))
		    			&& (dto.getPsClabe() != null && !dto.getPsClabe().equals(""))){
	                psIdChequeraBenefCLABE = dto.getPsClabe();
	                tipoCuenta = "40";
		    	}else{
	                psIdChequeraBenefCLABE = dto.getIdChequeraBenef();											//1.- Cuenta Destino (Nu)18
	                tipoCuenta = "03";
		    	}
		    	psRegistro.append(funciones.ponerCeros(psIdChequeraBenefCLABE, 18));											//1.- Cuenta Destino (Nu)18
                psRegistro.append(funciones.ponerCeros(dto.getIdChequera(), 18));											//2.- Cuenta Origen (NU) 18
		    	psRegistro.append(psDivisaLayout);																				//3.- Moneda (AL) 3
	            psRegistro.append(funciones.ponerFormatoCeros(dto.getImporte(), 13));											//4.- Monto (NU) 16
	            
			    if(dto.getPsClabe() != null && dto.getPsClabe().trim().length() == 13) {
			    	psRefAux = funciones.ajustarLongitudCampo(psRefAux, 7, "D", "", "0");
			    	psBeneficiario = psRefAux.trim() + psBeneficiario.trim();
			    	psBeneficiario = funciones.quitarCaracteresRaros(dto.getBeneficiario(), true);
		            psBeneficiario = funciones.ocultarCoinversora(psBeneficiario);
		            psBeneficiario = funciones.ajustarLongitudCampo(psBeneficiario, 30, "I", "", "");
			    }else {
			    	psBeneficiario = funciones.quitarCaracteresRaros(dto.getBeneficiario(), true);
		            psBeneficiario = funciones.ocultarCoinversora(psBeneficiario);
		            psBeneficiario = funciones.ajustarLongitudCampo(psBeneficiario, 30, "I", "", "");
			    }
			    psRegistro.append(psBeneficiario);																					//5.- Beneficiario (AL) 30
			    psRegistro.append(tipoCuenta);																						//6.- Tipo Cuenta Siempre 40
			    psRegistro.append(funciones.ponerCeros("" + dto.getIdBancoBenef(), 3));												//7.- Banco Beneficiario (NU) 8
			    sConcepto = layoutsDao.concepto(dto.getNoCliente(), dto.getNoFolioDet());
			    
			    if(!sConcepto.equals("concepto")) {
			    	if(dto.getPsReferenciaTraspaso() != null && dto.getPsReferenciaTraspaso().equals("")) {
			    		//Solo aplica para Transferencias, Pagos referenciados a Clientes
		            	valorPagRef = layoutsDao.obtenerValorPagoReferenciado(dto.getNoCliente(), dto.getNoFolioDet(), "");
		            	psReferencia = valorPagRef.get("valorPagRef").toString();
		            }

			    	psCampo = obtenerReferencias(dto.getNoCliente(), dto.getNoFolioDet()).get("refN").toString();
			    	psCampo = funciones.ajustarLongitudCampo(psCampo, 30, "I", "", "");
		            psRegistro.append(psCampo);																						//8.- Referencia (NU) 30
		            psCampo = funciones.ajustarLongitudCampo(dto.getNoFolioDet() + "", 7, "D", "", "0");
		            psRegistro.append(psCampo);																						//9.- Motivo del Pago (AL) 7 
			    }else {
			    	psCampo = obtenerReferencias(dto.getNoCliente(), dto.getNoFolioDet()).get("refN").toString();
			    	psCampo = funciones.ajustarLongitudCampo(psCampo, 30, "I", "", "");
		            psRegistro.append(psCampo);																						//8.- Motivo del Pago (AL) 30
		            if(dto.getPsReferenciaTraspaso() != null && dto.getPsReferenciaTraspaso().equals("")) {
			    		//Solo aplica para Transferencias, Pagos referenciados a Clientes
		            	valorPagRef = layoutsDao.obtenerValorPagoReferenciado(dto.getNoCliente(), dto.getNoFolioDet(), "");
		            	psReferencia = valorPagRef.get("valorPagRef").toString();
		            }
		            psCampo = funciones.ajustarLongitudCampo(dto.getNoFolioDet() + "", 7, "D", "", "0");
		            psRegistro.append(psCampo);																						//9.- Referencia (NU) 7
			    }
			    psRegistro.append("H");																								//10.- Disponibilidad H es Hoy y M es Manana
		    }
		              
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:com.webset.set.layout, C:LayoutBancomer, M:beArmaBancomerC43");
			e.printStackTrace();
		}
		
		return psRegistro.toString();
	}
	
	//NetCash c43
		public String beArmaBancomerC43Agrupado(ParametrosLayoutDto dto) {
			StringBuffer psRegistro = new StringBuffer() ;
			Map<String, Object> valorPagRef = new HashMap<String, Object>();
		    //String psPlazaLayout = "";
		    String psDivisaLayout = "";
		    //int piDivisa = 0;
		    String psReferencia = "";
		    String psIdChequeraBenefCLABE = "";
		    String psCampo = "";
		    String psBeneficiario = "";
		    String sConcepto = "";
		    String psRefAux = "";
		    String referenciaT = "";
		    
			try {
			    if(dto.getPsDivisa() != null && dto.getPsDivisa().trim().equals("MN")) {
		        	psDivisaLayout = "MXP";
		        	//piDivisa = 1;
			    } else if(dto.getPsDivisa() != null && dto.getPsDivisa().trim().equals("DLS")) {
			        psDivisaLayout = "USD";
			       // piDivisa = 2;
			    } else {
			        //psDivisaLayout = Space(3) 
			    	String.format("%1$3s", psDivisaLayout);
			       // piDivisa = 0;
			    }
			    
			    if(dto.getPsReferenciaTraspaso() != null && !dto.getPsReferenciaTraspaso().equals(""))
			        psReferencia = dto.getPsReferenciaTraspaso();
			    else
			        psReferencia = "" + dto.getNoFolioDet();
			    psRefAux = psReferencia;
			    
				/*String poHeaders = layoutsDao.obtenerReferenciaAgrupada(dto.getNoEmpresa(),
						dto.getIdBanco(), dto.getIdChequera(), dto.getPoHeaders());
*/
			    
			    
			    //MISMO BANCO SUMA 85 CARACTERES
			    if(dto.isMismoBanco()) {
			    	psRegistro = new StringBuffer();
			    	//String referenciaPoliza = dto.getPoHeaders();
			    	String psChequera = "";
			    	//psRegistro.append("PTC");
			    	
		            if (dto.getPsClabe() != null && !dto.getPsClabe().equals("")) {
			    		psChequera = dto.getPsClabe();
			    		 psRegistro.append(funciones.ponerCeros(psChequera, 18));									//1.- Cuenta Destino (NU) 18
					} else {
						psChequera = dto.getIdChequeraBenef();
						psRegistro.append(funciones.ponerCeros(psChequera, 18));									//1.- Cuenta Destino (NU) 18
					}
		            
		            String chequeraOrigen =  layoutsDao.obtieneComplementosBancoOrigen(dto.getIdChequera(),12);
		            if (chequeraOrigen != null && !chequeraOrigen.equals("")) {
		        	   	psRegistro.append(funciones.ponerCeros(chequeraOrigen,18));								    //2.- Cuenta Origen (NU) 18
					} else {
						psRegistro.append(funciones.ponerCeros(dto.getIdChequera(),18));							//2.- Cuenta Origen (NU) 18
					}
		            
		            psRegistro.append(psDivisaLayout);																				//3.- Moneda (AL) 3
		            psRegistro.append(funciones.ponerFormatoCeros(dto.getImporte(), 13));											//4.- Monto (NU) 16
			    	
		            if(dto.getPsReferenciaTraspaso() != null && dto.getPsReferenciaTraspaso().equals("")) {
		            	//Solo aplica para Transferencias, Pagos referenciados a Clientes
		            	valorPagRef = layoutsDao.obtenerValorPagoReferenciado(dto.getNoCliente(), dto.getNoFolioDet(), "");
		            	psReferencia = valorPagRef.get("valorPagRef").toString();
		            }
		            
		            psRegistro.append(funciones.ajustarLongitudCampo("" + funciones.quitarCaracteresRaros(psReferencia, true), 30, "I", "", " "));
		            psRegistro.append("\n");
		            
			    }else {
			    	//INTERBANCARIA SUMA 165 CARACTERES
			    	psRegistro = new StringBuffer();
			    	String tipoCuenta = "";
			    	if((layoutsDao.consultarConfiguraSet(209) != null && layoutsDao.consultarConfiguraSet(209).equals("SI"))
			    			&& (dto.getPsClabe() != null && !dto.getPsClabe().equals(""))){
		                psIdChequeraBenefCLABE = dto.getPsClabe();
		                tipoCuenta = "40";
			    	}else{
		                psIdChequeraBenefCLABE = dto.getIdChequeraBenef();											//1.- Cuenta Destino (Nu)18
		                tipoCuenta = "03";
			    	}
			    	psRegistro.append(funciones.ponerCeros(psIdChequeraBenefCLABE, 18));											//1.- Cuenta Destino (Nu)18
	                psRegistro.append(funciones.ponerCeros(dto.getIdChequera(), 18));											//2.- Cuenta Origen (NU) 18
			    	psRegistro.append(psDivisaLayout);																				//3.- Moneda (AL) 3
		            psRegistro.append(funciones.ponerFormatoCeros(dto.getImporte(), 13));											//4.- Monto (NU) 16
		            
				    if(dto.getPsClabe() != null && dto.getPsClabe().trim().length() == 13) {
				    	psRefAux = funciones.ajustarLongitudCampo(psRefAux, 7, "D", "", "0");
				    	psBeneficiario = psRefAux.trim() + psBeneficiario.trim();
				    	psBeneficiario = funciones.quitarCaracteresRaros(dto.getBeneficiario(), true);
			            psBeneficiario = funciones.ocultarCoinversora(psBeneficiario);
			            psBeneficiario = funciones.ajustarLongitudCampo(psBeneficiario, 30, "I", "", "");
				    }else {
				    	psBeneficiario = funciones.quitarCaracteresRaros(dto.getBeneficiario(), true);
			            psBeneficiario = funciones.ocultarCoinversora(psBeneficiario);
			            psBeneficiario = funciones.ajustarLongitudCampo(psBeneficiario, 30, "I", "", "");
				    }
				    psRegistro.append(psBeneficiario);																					//5.- Beneficiario (AL) 30
				    psRegistro.append(tipoCuenta);																							//6.- Tipo Cuenta Siempre 40
				    psRegistro.append(funciones.ponerCeros("" + dto.getIdBancoBenef(), 3));												//7.- Banco Beneficiario (NU) 8
				    sConcepto = layoutsDao.conceptoBancomer(dto.getNoCliente());
				    
				    if(!sConcepto.equals("concepto")) {
				    	if(dto.getPsReferenciaTraspaso() != null && dto.getPsReferenciaTraspaso().equals("")) {
				    		//Solo aplica para Transferencias, Pagos referenciados a Clientes
			            	referenciaT = layoutsDao.obtenerReferenciaAgrupada(dto.getNoEmpresa(), 12, dto.getIdChequera(), dto.getPoHeaders());
			            }
			            psCampo = funciones.ajustarLongitudCampo(referenciaT != null && !referenciaT.equals("")?
			            		referenciaT:dto.getPoHeaders(), 30, "I", "", "");
			            psRegistro.append( funciones.quitarCaracteresRaros(psCampo, true ));																						//8.- Referencia (NU) 30
			            psCampo = "0";
			            psRegistro.append(funciones.ajustarLongitudCampo(psCampo, 7, "D", "", "0"));									//9.- Motivo del Pago (AL) 7 
				    }else {
				    	psCampo = funciones.ajustarLongitudCampo(psReferencia, 30, "I", "", "");
			            psRegistro.append(psCampo);																						//8.- Motivo del Pago (AL) 30
			            psCampo = funciones.ajustarLongitudCampo("0", 7, "D", "", "0");
			            psRegistro.append(psCampo);																						//9.- Referencia (NU) 7
				    }
				    psRegistro.append("H");																								//10.- Disponibilidad H es Hoy y M es Manana
			    }
			              
			}catch(Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
						+"P:com.webset.set.layout, C:LayoutBancomer, M:beArmaBancomerC43Agrupado");
				e.printStackTrace();
			}
			
			return psRegistro.toString();
		}
		

		
		
		//h2hsit
		public String armaBancomerH2H (ParametrosLayoutDto dto,List<MovimientoDto> movimiento ,int tipoRegistro, int totalRegistros,double totalArchivo, boolean proveedor, String nomArchivo, boolean pbCie, int h2hBBVA){			

			StringBuffer psRegistro = new StringBuffer() ;
			Map<String, Object> valorPagRef = new HashMap<String, Object>();
		    //String psPlazaLayout = "";
		    String psDivisaLayout = "";
		    //int piDivisa = 0;
		    String psReferencia = "";
		    String psIdChequeraBenefCLABE = "";
		    String psCampo = "";
		    String psBeneficiario = "";
		    String sConcepto = "";
		    String psRefAux = "";
		    String referenciaT = "";

		    String psChequera = "";
			String aba=layoutsDao.consultarAba(dto.getIdChequera(), dto.getIdDivisa(), dto.getPsClabe());
			String swCode=layoutsDao.consultarSwCode(dto.getIdChequera(), dto.getIdDivisa(), dto.getPsClabe());
		    
		    try {
				 if(dto.getPsDivisa() != null && dto.getPsDivisa().trim().equals("MN")) {
			        	psDivisaLayout = "MXP";
			        	//piDivisa = 1;
				    } else if(dto.getPsDivisa() != null && dto.getPsDivisa().trim().equals("DLS")) {
				        psDivisaLayout = "USD";
				       // piDivisa = 2;
				    } else {
				        //psDivisaLayout = Space(3) 
				    	String.format("%1$3s", psDivisaLayout);
				       // piDivisa = 0;
				    }
			    
				 psRegistro = new StringBuffer();
			    	String tipoCuenta = "";
			    	if((layoutsDao.consultarConfiguraSet(209) != null && layoutsDao.consultarConfiguraSet(209).equals("SI"))
			    			&& (dto.getPsClabe() != null && !dto.getPsClabe().equals(""))){
		                psIdChequeraBenefCLABE = dto.getPsClabe();
		                tipoCuenta = "40";
			    	}else{
		                psIdChequeraBenefCLABE = dto.getIdChequeraBenef();											
		                tipoCuenta = "03";
			    	}
			    	psRegistro.append(funciones.ponerCeros(psIdChequeraBenefCLABE, 18));							
	                psRegistro.append(funciones.ponerCeros(dto.getIdChequera(), 18));								
			    	psRegistro.append(psDivisaLayout);																
		            psRegistro.append(funciones.ponerFormatoCeros(dto.getImporte(), 13));							
		            
				    if(dto.getPsClabe() != null && dto.getPsClabe().trim().length() == 13) {
				    	psRefAux = funciones.ajustarLongitudCampo(psRefAux, 7, "D", "", "0");
				    	psBeneficiario = psRefAux.trim() + psBeneficiario.trim();
				    	psBeneficiario = funciones.quitarCaracteresRaros(dto.getBeneficiario(), true);
			            psBeneficiario = funciones.ocultarCoinversora(psBeneficiario);
			            psBeneficiario = funciones.ajustarLongitudCampo(psBeneficiario, 30, "I", "", "");
				    }else {
				    	psBeneficiario = funciones.quitarCaracteresRaros(dto.getBeneficiario(), true);
			            psBeneficiario = funciones.ocultarCoinversora(psBeneficiario);
			            psBeneficiario = funciones.ajustarLongitudCampo(psBeneficiario, 30, "I", "", "");
				    }
				    psRegistro.append(psBeneficiario);																					
				    psRegistro.append(tipoCuenta);																						
				    psRegistro.append(funciones.ponerCeros("" + dto.getIdBancoBenef(), 3));												
				    sConcepto = layoutsDao.concepto(dto.getNoCliente(), dto.getNoFolioDet());
				    
				    if(!sConcepto.equals("concepto")) {
				    	if(dto.getPsReferenciaTraspaso() != null && dto.getPsReferenciaTraspaso().equals("")) {
				    		//Solo aplica para Transferencias, Pagos referenciados a Clientes
			            	valorPagRef = layoutsDao.obtenerValorPagoReferenciado(dto.getNoCliente(), dto.getNoFolioDet(), "");
			            	psReferencia = valorPagRef.get("valorPagRef").toString();
			            }

				    	psCampo = obtenerReferencias(dto.getNoCliente(), dto.getNoFolioDet()).get("refN").toString();
				    	psCampo = funciones.ajustarLongitudCampo(psCampo, 30, "I", "", "");
			            psRegistro.append(psCampo);																						
			            psCampo = funciones.ajustarLongitudCampo(dto.getNoFolioDet() + "", 7, "D", "", "0");
			            psRegistro.append(psCampo);																						 
				    }else {
				    	psCampo = obtenerReferencias(dto.getNoCliente(), dto.getNoFolioDet()).get("refN").toString();
				    	psCampo = funciones.ajustarLongitudCampo(psCampo, 30, "I", "", "");
			            psRegistro.append(psCampo);																						
			            if(dto.getPsReferenciaTraspaso() != null && dto.getPsReferenciaTraspaso().equals("")) {
				    		valorPagRef = layoutsDao.obtenerValorPagoReferenciado(dto.getNoCliente(), dto.getNoFolioDet(), "");
			            	psReferencia = valorPagRef.get("valorPagRef").toString();
			            }
			            psCampo = funciones.ajustarLongitudCampo(dto.getNoFolioDet() + "", 7, "D", "", "0");
			            psRegistro.append(psCampo);																						
				    }
				    psRegistro.append("H");	
		    	
		    
			} catch (Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:com.webset.set.layout, C:LayoutBancomer, M:beArmaBancomerInternacional");
				e.printStackTrace();
			}
			return psRegistro.toString();
			
			
			
	}
		
		
		
	//armaHostToHost (H2H)
	public String armaBancomerH2H2 (ParametrosLayoutDto dto, int tipoRegistro, int totalRegistros,
			double totalArchivo, boolean proveedor, String nomArchivo, boolean pbCie)
	{			
		String psRegistro = "";
		int caracter = 0;
		String sConcepto = "";
		String sClave = "";
		String sReferencia = "";
		String sNoPersona = "";
		String campoStr = "";
		String campoVacio = "";
		String sContrato = "00001234";
		String sConvenio = "000999999";
		String sDivisa2 = "";
		
		Date campoDate = null;
		SimpleDateFormat formatoFechaYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formatoFechaYYYYMMDDsin = new SimpleDateFormat("yyyyMMdd");
		
		List<ParametrosLayoutDto> recibeDatos = new ArrayList<ParametrosLayoutDto>();
		
		try
		{			
			//Se saca la forma de pago para saber el tipo de pago a realizar (transferencia u orden de pago)
			if (tipoRegistro != 4)
			{
			recibeDatos = layoutsDao.obtieneTipoDePago(dto.getNoFolioDet());
			
			if (recibeDatos.size() != 0)
			{
			/*if (recibeDatos.get(0).getIdFormaPago() == 8 || recibeDatos.get(0).getIdFormaPago() == 9)
			{
			sBeneficiario = recibeDatos.get(0).getBeneficiario();
			tipoPago = "PDV";
			}
			else
			{
			tipoPago = "PDA";
			}*/
			}
			
			if (!dto.getConcepto().equals(""))
			{
			sConcepto = dto.getConcepto();
			}
			else
			{
			sConcepto = dto.getDescripcion();
			}
			
			if (dto.getIdBancoBenef().equals("12"))
			{
			recibeDatos = layoutsDao.obtieneChequeraBenef(dto.getNoCliente());
			
			/*if (recibeDatos.size() != 0)
			{
			sChequeraBenef = recibeDatos.get(0).getIdChequeraBenef();
			}*/
			}
			else
			{
			recibeDatos = layoutsDao.obtieneChequeraBenef(dto.getNoCliente());
			
			if (recibeDatos.size() != 0)
			{
			sClave = recibeDatos.get(0).getIdChequeraBenef();
			}
			}
				
			sConcepto = funciones.quitarCaracteresRaros(sConcepto, true);
				
			recibeDatos = layoutsDao.obtieneReferencia(dto.getNoCliente());
			
			if (recibeDatos.size() != 0)
			{
			sReferencia = recibeDatos.get(0).getReferenciaCte();
			sNoPersona = recibeDatos.get(0).getEquivalePersona();
			
			if (sReferencia.equals(""))
			{
			sReferencia = sNoPersona;
			for(int c = 0; c < sReferencia.length(); c++)
			{
				if (sReferencia.length() > 7)
				{
					if (sReferencia.substring(0, 1).equals("0"))
					{
						sReferencia = sReferencia.substring(1, sReferencia.length());
					}
				}
			}
			}
			}
			else
			{
			//Esto se coloca ahorita por default la empresa elige que se coloca
			sReferencia = "12345678";
			}
			
			if (sReferencia.equals(""))
			{
			sReferencia = "12345678";
			}		
			}
			
			psRegistro = "";
			switch (tipoRegistro)
			{
			case 1: 
			//Estos datos son para el cabecero de Host to Host alguna de la informacion es fija
			//1.- Tipo de registro (H)
			//2.- Descripcion tipo de registro
			psRegistro = "HHEADER H2H";
			
			//3.- Total de registros enviados en el archivo
			campoStr = Integer.toString(totalRegistros);
			campoStr = funciones.ajustarLongitudCampo(campoStr, 7, "D", "", "0");
			psRegistro = psRegistro + campoStr;
			
			//4.- Total del importe del archivo
			campoStr = "";					
			campoStr = funciones.ponerFormatoCeros(totalArchivo, 15);
			campoStr = campoStr.replace(".", "");
			campoStr = funciones.ajustarLongitudCampo(campoStr, 15, "D", "", "0");					
			psRegistro = psRegistro + campoStr;
			
			//5.- Contrato H2H
			//el contrato lo asigna el banco ahorita se coloca fijo de prueba
			sContrato = funciones.ajustarLongitudCampo(sContrato, 20, "I", "", "");
			//proveedor = false, entonces es nomina
			if (proveedor)
			{
			//Sit
			psRegistro = psRegistro + sContrato;
			
			//Subservicion este dato lo asigna el banco
			psRegistro = psRegistro + "TG-0013 ";
			}
			else
			{
			//nomina
			psRegistro = psRegistro + sContrato;
			
			//Subservicion este dato lo asigna el banco
			psRegistro = psRegistro + "TG-0006 ";
			}
			
			//6.- Tipo de servicio (asignado por Bancomer)
			psRegistro = psRegistro + "08";
			
			//7.- Numero de lote (debe ser igual que el que contiene el nombre del archivo, es irrepetible)
			campoStr = "";
			campoStr = nomArchivo.substring(nomArchivo.indexOf(".") + 1, nomArchivo.length() - 4);
			campoStr = funciones.ajustarLongitudCampo(campoStr, 7, "D", "", "0");
			psRegistro = psRegistro + campoStr;
			
			//8.- Fecha de envio del archivo
			recibeDatos = layoutsDao.obtieneFechaHoy();
			
			if (recibeDatos.size() != 0)
			{						
			campoStr = formatoFechaYYYYMMDD.format(recibeDatos.get(0).getFecHoy());						
			psRegistro = psRegistro + campoStr;						
			}
			
			//9.- Campos vacios para uso futuro
			campoStr = "";
			campoStr = funciones.ajustarLongitudCampo(campoStr, 170, "I", "", "");
			psRegistro = psRegistro + campoStr;
			
			break;
			
			case 2: //Cabecero del SIT y Nomina
			if (proveedor)
			{
			psRegistro = "";
			//1.- Indicador de registro (Campo fijo H)
			psRegistro = "H"; 
			
			//2.- Convenio este dato lo da el Banco
			sConvenio = funciones.ajustarLongitudCampo(sConvenio, 9, "D", "", "0");
			psRegistro = psRegistro + sConvenio;
			
			//3.- Fecha de envio del archivo
			recibeDatos = layoutsDao.obtieneFechaHoy();
			
			if (recibeDatos.size() != 0)
			{	
				campoDate = funciones.modificarFecha("d", 1, recibeDatos.get(0).getFecHoy());
				campoStr = formatoFechaYYYYMMDD.format(campoDate);													
				psRegistro = psRegistro + campoStr;													
			}
			
			//4.- Tipo Validacion Tercero puede ser 00 = sin validacion tercero o 01 = que exista tercero, en este caso es 01
			psRegistro = psRegistro + "01";
			
			//5.- Clave de archivo(debe de ser el mismo que el encabezado H2H)
			campoStr = "";
			campoStr = nomArchivo.substring(nomArchivo.indexOf(".") + 1, nomArchivo.length() - 4);
			campoStr = funciones.ajustarLongitudCampo(campoStr, 30, "D", "", "0");
			psRegistro = psRegistro + campoStr;
			
			//6.- Codigo de respuesta al envio siempre va con 00
			psRegistro = psRegistro + "00";
			
			//7.- Descripcion codigo de respuesta, al enviar se va en blanco
			campoStr = "";
			campoStr = funciones.ajustarLongitudCampo(campoStr, 20, "I", "", "");
			psRegistro = psRegistro + campoStr;
			
			//8.- Canal siempre va una "H" indicando que es por Host to Host
			psRegistro = psRegistro + "H";
			
			//9.- Campos en blanco para uso futuro
			campoStr = "";
			campoStr = funciones.ajustarLongitudCampo(campoStr, 1291, "I", "", "");
			psRegistro = psRegistro + campoStr;						
			}
			else
			{
			psRegistro = "";
			
			//1.- Identificador de Header correcto
			psRegistro = "1";
			
			//2.- Numero de registros enviados
			campoStr = Integer.toString(totalRegistros);
			campoStr = funciones.ajustarLongitudCampo(campoStr, 7, "D", "", "0");
			psRegistro = psRegistro + campoStr;
			
			//3.- Importe total del archivo						
			campoStr = "";					
			campoStr = funciones.ponerFormatoCeros(totalArchivo, 15);
			campoStr = campoStr.replace(".", "");
			campoStr = funciones.ajustarLongitudCampo(campoStr, 15, "D", "", "0");					
			psRegistro = psRegistro + campoStr;
			
			
			//4.- Numero de registros no ok, siempre al envio se manda en 0
			campoStr = "";
			campoStr = funciones.ajustarLongitudCampo(campoStr, 7, "I", "", "0");
			psRegistro = psRegistro + campoStr;
			
			//5.- Importe total del archivo no ok, se manda en 0 al enviar
			campoStr = "";
			campoStr = funciones.ajustarLongitudCampo(campoStr, 15, "I", "", "0");
			psRegistro = psRegistro + campoStr;
			
			//6.- Contrato (Si se tiene se coloca el asignado, si no se manda en 0)
			campoStr = "";
			campoStr = funciones.ajustarLongitudCampo(sConvenio, 12, "D", "", "0");
			psRegistro = psRegistro + campoStr;
			
			//7.- Numero de empresa como se encuentra registrada en Bancomer, este dato lo asigna Bancomer						
			campoStr = "1234567890";
			campoStr = funciones.ajustarLongitudCampo(campoStr, 10, "D", "", "0");
			psRegistro = psRegistro + campoStr;
			
			//8.- Codigo de leyenda para las nominas
			campoStr = "";
			campoStr = dto.getNoDocto().substring(0, 3);
			
			if (campoStr.equals("NSRU"))
			{
				psRegistro = psRegistro + "R22"; //Pago de utilidades
				
			}
			else if (campoStr.equals("NSFA"))
			{
				psRegistro = psRegistro + "R21"; //Pago de fondo de ahorro
			}
			else if (campoStr.equals("NSAA"))
			{
				psRegistro = psRegistro + "R16"; //Pago de aguinaldo
			}
			else
			{
				campoStr = dto.getNoDocto().substring(0, 1);
				if (campoStr.equals("NS"))
				{
					psRegistro = psRegistro + "R05"; //Pago de nomina
				}
				else
				{
					psRegistro = psRegistro + "R17"; //Pago de pension
				}
			}
						
			//Tipo de servicio (por el momento aplica nomina)
			psRegistro = psRegistro + "101";
			
			//Clave de entrada valor fijo
			psRegistro = psRegistro + "H";
			
			//Fecha de envio
			recibeDatos = layoutsDao.obtieneFechaHoy();
			
			if (recibeDatos.size() != 0)
			{							
				campoDate = funciones.modificarFecha("d", 1, recibeDatos.get(0).getFecHoy());
				campoStr = formatoFechaYYYYMMDDsin.format(campoDate);													
				psRegistro = psRegistro + campoStr;						
			}
			
			//Fecha de pago
			//campoDate = dto.getFecOperacion();
			//campoDate = diaHabil(dto.getFecOperacion());
			
			campoStr = formatoFechaYYYYMMDDsin.format(dto.getFecOperacion());													
			psRegistro = psRegistro + campoStr;					
			
			//Filler
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 142, "I", "", "");
			psRegistro = psRegistro + campoVacio;
				 
			
			
			}					
			break;
			
			case 3:
			//Detallado del archivo
			if (!proveedor)
			{
			psRegistro = "";
			
			//1.- Este valor va fijo al inicio del detallado es el indicador del registro
			psRegistro = "D";
			
			//2.- Siempre al enviar se manda como un alta
			psRegistro = psRegistro + "A";
			
			//3.- Tipo de documento siempre aplica como por pagar "P"
			psRegistro = psRegistro + "P";
			
			//4.- Referencia del pago
			campoStr = funciones.ajustarLongitudCampo(sReferencia, 20, "I", "", "");
			campoStr = campoStr.toUpperCase();
			psRegistro = psRegistro + campoStr;
			
			//5.- Concepto del pago
			campoStr = "";
			campoStr = Integer.toString(dto.getNoCliente());
			campoStr = funciones.ajustarLongitudCampo(campoStr, 30, "I", "", "");
			psRegistro = psRegistro + campoStr;
			
			//6.- Tipo de pago
			psRegistro = psRegistro + "PDA";
			
			//7.- Codigo de operacion
			if (dto.isMismoBanco() && pbCie == false)
			{
				//Bancomer - Bancomer
				psRegistro = psRegistro + "2";
			}
			else if(pbCie)
			{
				//Para pagos CIE por H2H
				psRegistro = psRegistro + "3";
			}
			else
			{
				//Interbancario
				psRegistro = psRegistro + "6";							
			}
			
			//8.- Linea de credito no aplica se manda en 0
			campoStr = "";
			campoStr = funciones.ajustarLongitudCampo(campoStr, 20, "I", "", "0");
			psRegistro = psRegistro + campoStr;
			
			//9.- Leyenda corta (por el momento no aplica se manda en blanco)
			campoStr = "";
			campoStr = funciones.ajustarLongitudCampo(campoStr, 15, "I", "", "");
			psRegistro = psRegistro + campoStr;
			
			//10.-Texto si es interbacario se manda la referencia, de ser mismo banco puede ser otro texto
			campoStr = sReferencia;
			campoStr = funciones.ajustarLongitudCampo(campoStr, 25, "I", "", "");
			psRegistro = psRegistro + campoStr;
			
			//11.- Texto libre (por el momento se esta mandando la referencia o concepto)
			if (dto.isMismoBanco())
			{
				campoStr = sReferencia;
				campoStr = funciones.ajustarLongitudCampo(campoStr, 8, "I", "", "");
				campoVacio = campoStr + dto.getConcepto();
				campoVacio = funciones.ajustarLongitudCampo(campoVacio, 37, "I", "", "");
				campoVacio = funciones.quitarCaracteresRaros(campoVacio, false);
				psRegistro = psRegistro + campoVacio;
			}
			else
			{
				campoVacio = dto.getConcepto().replace(" ", "");
				campoVacio = funciones.ajustarLongitudCampo(campoVacio, 37, "I", "", "");
				campoVacio = funciones.quitarCaracteresRaros(campoVacio, false);
				psRegistro = psRegistro +  campoVacio; 
			}
			
			//12.- Leyenda cuenta de abono (se manda en blanco)
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 15, "I", "", "");
			psRegistro = psRegistro + campoVacio;
			
			//13.- Leyenda larga cuenta de abono (se manda en blanco)
			campoStr = sReferencia;
			campoStr = funciones.ajustarLongitudCampo(campoStr, 25, "I", "", "");
			psRegistro = psRegistro + campoStr;
			
			//14.- Leyenda de cuenta de abono segundo renglon
			if (dto.isMismoBanco())
			{
				campoStr = sReferencia;
				campoStr = funciones.ajustarLongitudCampo(campoStr, 8, "I", "", "");
				campoVacio = campoStr + dto.getConcepto();
				campoVacio = funciones.ajustarLongitudCampo(campoVacio, 37, "I", "", "");
				campoVacio = funciones.quitarCaracteresRaros(campoVacio, false);
				psRegistro = psRegistro + campoVacio;							
			}
			else
			{
				campoVacio = dto.getConcepto().replace(" ", "");
				campoVacio = funciones.ajustarLongitudCampo(campoVacio, 37, "I", "", "");
				campoVacio = funciones.quitarCaracteresRaros(campoVacio, false);
				psRegistro = psRegistro + campoVacio;							
			}
			
			//15.- Comprobante Fiscal (valor fijo) 
			psRegistro = psRegistro + "N";
			
			//16.- Numero de tercero cliente
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 8, "I", "", "");
			psRegistro = psRegistro + campoVacio;
								
			//17.- Tipo de cuenta
			//18.- Cuenta de cheques
			if (pbCie)
			{
				caracter = dto.getIdChequeraBenef().indexOf("CONV");
				if (caracter > 0)
				{
					psRegistro = psRegistro + "50";
					
					campoStr = dto.getIdChequeraBenef().substring(caracter + 4, dto.getIdChequeraBenef().length());
					campoStr = funciones.ajustarLongitudCampo(campoStr, 35, "D", "", "0");
					psRegistro = psRegistro + campoStr;
				}
			}
			else
			{
				if (dto.getIdChequeraBenef().length() > 10 && dto.getIdBancoBenef().equals("12"))
				{
					caracter = dto.getIdChequeraBenef().indexOf("910");
					if (caracter != -1)
					{
						psRegistro = psRegistro + "99";
						
						campoStr = dto.getIdChequeraBenef().substring(caracter + 3, dto.getIdChequeraBenef().length());
						campoStr = funciones.ajustarLongitudCampo(campoStr, 35, "D", "", "0");
					}
					else
					{
						psRegistro = psRegistro + "40";
						
						campoStr = dto.getIdChequeraBenef();
						campoStr = funciones.ajustarLongitudCampo(campoStr, 35, "D", "", "0");
					}
					psRegistro = psRegistro + campoStr;
				}
				else if (!dto.getIdBancoBenef().equals("12"))
				{
					campoStr = dto.getIdChequeraBenef();
					campoStr = funciones.ajustarLongitudCampo(campoStr, 35, "D", "", "0");
					psRegistro = psRegistro + campoStr;
				}
			}
			
			//19.- Beneficiario
			campoStr = dto.getBeneficiario();
			campoStr = funciones.ajustarLongitudCampo(campoStr, 40, "I", "", "");
			psRegistro = psRegistro + campoStr;
			
			//20.- Clave de identificacion tercero
			psRegistro = psRegistro + "7";
			
			//21.- Numero de identificacion del tercero (8)
			//22.- Tercero 2 
			//23.- Clave identificacion 2
			//24.- Numero de identificacion 2
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 101, "I", "", "");
			psRegistro = psRegistro + campoVacio;
			
			//25.- Divisa						
			recibeDatos = layoutsDao.obtieneDivisa(dto.getIdDivisa(), "BE");
			if (recibeDatos.size() != 0)
			{
				if (recibeDatos.get(0).getIdDivisa().equals("MXN"))
				{
					sDivisa2 = "MXP";
					psRegistro = psRegistro + sDivisa2;
				}
				else
				{
					sDivisa2 = recibeDatos.get(0).getIdDivisa();
					psRegistro = psRegistro + sDivisa2;
				}
			}
			
			//26.- Clave BIC
			//Por el momento no aplica para DLS solo para pesos, en BIC y ABA se mandan en blanco
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 11, "I", "", "");
			psRegistro = psRegistro + campoVacio;
			
			//27.- Clave ABA
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 9, "I", "", "");
			psRegistro = psRegistro + campoVacio;
			
			//28.- Tipo de Documento
			if (proveedor)
			{
				psRegistro = psRegistro + "FA";
			}
			else
			{
				psRegistro = psRegistro + "OT";
			}
			
			//29.- Importe						
			campoStr = "";					
			campoStr = funciones.ponerFormatoCeros(dto.getImporte(), 15);
			campoStr = campoStr.replace(".", "");
			campoStr = funciones.ajustarLongitudCampo(campoStr, 15, "D", "", "0");					
			psRegistro = psRegistro + campoStr;
			
			//30.- Iva del documento
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 15, "I", "", "0");
			psRegistro = psRegistro + campoVacio;
			
			//31.- Tipo de confirmacion (se puede enviar 00 o 01, en este caso se manda 00 para que no se mande mail)
			psRegistro = psRegistro + "00";
			
			//32.- Email, fax del tercero o celular (se manda en blanco por que no se manda confirmacion)
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 50, "I", "", "");
			psRegistro = psRegistro + campoVacio;
			
			//33.- Periodicidad (interes) por el momento no aplica
			psRegistro = psRegistro + "N";
			
			//34.- Tasa de interes
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 8, "I", "", "0");
			psRegistro = psRegistro + campoVacio;
			
			//35.- Tasa Iva de interes
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 4, "I", "", "0");
			psRegistro = psRegistro + campoVacio;
			
			//36.- Fecha de pago
			campoStr = "";
			System.out.println(dto.getFecOperacion());
			campoStr = formatoFechaYYYYMMDD.format(dto.getFecOperacion());
			System.out.println(campoStr);
			psRegistro = psRegistro + campoStr;	
									
			//37.- Fecha de vencimiento (solo aplica para pagos en ventanilla)
			psRegistro = psRegistro + "0001-01-01";
			
			//38.- Fecha de gracia (solo aplica para documentos por cobrar)
			psRegistro = psRegistro + "0001-01-01";
			
			//39.- Fecha de caducidad (debe ser igual a la fecha de vencimiento y solo aplica para pagos de ventanilla)
			psRegistro = psRegistro + "0001-01-01";
			
			//40.- Fecha del documento o de emision (siempre es igual a la fecha de pago)
			campoStr = "";						
			campoStr = formatoFechaYYYYMMDD.format(dto.getFecOperacion());												
			psRegistro = psRegistro + campoStr;
			
			//41.- Indicador pago recurrente (no aplica, lleva valor fijo)
			psRegistro = psRegistro + "N";
			
			//42.- Periodicidad pago recurrente
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 1, "I", "", "");
			psRegistro = psRegistro + campoVacio;
			
			//43.- Fecha fin pago recurrente
			psRegistro = psRegistro + "0001-01-01";
			
			//44.- Longitud (para el mesaje enviado por mail, por el momento no aplica)
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 3, "D", "", "0");
			psRegistro = psRegistro + campoVacio;
			
			//45.- Datos adicionales 1 (es un texto libre)
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 700, "I", "", "");
			psRegistro = psRegistro + campoVacio;
			
			//46.- Folios de abono (se mandan en blanco)
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 10, "I", "", "");
			psRegistro = psRegistro + campoVacio;
			
			//47.- Folios de cargo (se mandan en blanco)
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 10, "I", "", "");
			psRegistro = psRegistro + campoVacio;
			
			//48.- Codigo de estatus del documento (se manda en blanco)
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 2, "I", "", "");
			psRegistro = psRegistro + campoVacio;
			
			//49.- Descripcion del codigo de estatus
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 30, "I", "", "");
			psRegistro = psRegistro + campoVacio;
			
			//50.- Fecha del ultimo evento del documento
			psRegistro = psRegistro + "0001-01-01";					
			
			}	
			else //Si no es proveedor aplica para nomina
			{
			psRegistro = "";
			
			//1.- Identificador (valor fijo)
			psRegistro = "3";								
			
			//2.- Referencia numerica
			if (dto.isMismoBanco())
			{
				campoStr = Integer.toString(dto.getNoCliente());
				campoStr = funciones.ajustarLongitudCampo(campoStr, 7, "D", "", "0");
				psRegistro = psRegistro + campoStr;							
			}
			
			//3.- CURP - RFC
			recibeDatos = layoutsDao.obtieneDatosPersona(dto.getNoCliente());
			if (recibeDatos.size() != 0)
			{
				if (!recibeDatos.get(0).getRfc().equals(""))
				{
					campoStr = recibeDatos.get(0).getRfc();
					campoStr = funciones.ajustarLongitudCampo(campoStr, 18, "I", "", "");
					psRegistro = psRegistro + campoStr;
				}
				else
				{
					campoStr = recibeDatos.get(0).getCurp();
					campoStr = funciones.ajustarLongitudCampo(campoStr, 18, "I", "", "");
					psRegistro = psRegistro + campoStr;
				}
			}
			
			//4.- Tipo de cuenta
			if (dto.getIdChequeraBenef().length() == 16)
			{
				psRegistro = psRegistro + "03"; //Tarjeta de debito
			}
			else if(dto.isMismoBanco())
			{
				psRegistro = psRegistro + "01"; //Cuenta de cheques
			}
			else
			{
				psRegistro = psRegistro + "40"; //Cuenta clabe
			}
			
			//5.- Banco Destino
			if (dto.getIdBancoBenef().length() > 3)
			{
				if (!sClave.equals(""))
				{
					campoStr = sClave.substring(0, 2);
				}
				else
				{
					campoStr = dto.getIdBancoBenef();
				}
			}
			else
			{
				campoStr = dto.getIdBancoBenef();
			}
			
			campoStr = funciones.ajustarLongitudCampo(campoStr, 3, "D", "", "0");
			psRegistro = psRegistro + campoStr;
			
			//6.- Plaza Destino
			campoStr = Integer.toString(dto.getPiPlaza());
			campoStr = funciones.ajustarLongitudCampo(campoStr, 3, "D", "", "0");
			psRegistro = psRegistro + campoStr;
			
			//7.- Cuenta Abono
			if (dto.getIdChequeraBenef().length() > 10 && dto.getIdBancoBenef().equals("12"))
			{
				caracter = dto.getIdChequeraBenef().indexOf("910");
				campoStr = dto.getIdChequeraBenef().substring(caracter + 3);
			}
			else
			{
				campoStr = dto.getIdChequeraBenef();
			}
			campoStr = funciones.ajustarLongitudCampo(campoStr, 16, "D", "", "0");
			psRegistro = psRegistro + campoStr;
			
			//8.- Importe
			campoStr = "";					
			campoStr = funciones.ponerFormatoCeros(dto.getImporte(), 15);
			campoStr = campoStr.replace(".", "");
			campoStr = funciones.ajustarLongitudCampo(campoStr, 15, "D", "", "0");					
			psRegistro = psRegistro + campoStr;
			
			//9.- Estado de Pago
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 7, "D", "", "0");
			psRegistro = psRegistro + campoVacio;
			
			//10.- Descripcion del estado de pago
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 80, "I", "", "");
			psRegistro = psRegistro + campoVacio;
			
			//11.- Nombre titular
			campoStr = dto.getBeneficiario();
			campoStr = funciones.ajustarLongitudCampo(campoStr, 40, "I", "", "");
			psRegistro = psRegistro + campoStr;
			
			//12.- Motivo del pago (uso futuro)
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 40, "I", "", "");
			psRegistro = psRegistro + campoVacio;
			
			}
			
			System.out.println(psRegistro);
			break;
			
			case 4:
			//Esto es para el Trailer
			
			System.out.println("Entro al filler");
			psRegistro = "";
			
			//1.- Indicador de registro (valor fijo)
			psRegistro = "T";
			
			//2.- Numero de registros altas (siempre se van como altas todos los registros)
			campoStr = Integer.toString(totalRegistros);
			campoStr = funciones.ajustarLongitudCampo(campoStr, 10, "D", "", "0");
			psRegistro = psRegistro + campoStr;
			
			//3.- Importe total altas					
			campoStr = "";					
			campoStr = funciones.ponerFormatoCeros(totalArchivo, 15);
			campoStr = campoStr.replace(".", "");
			campoStr = funciones.ajustarLongitudCampo(campoStr, 15, "D", "", "0");					
			psRegistro = psRegistro + campoStr;
			
			//4.- Numero de registros bajas (se manda en blanco, es llenado por bancomer)
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 10, "I", "", "0");
			psRegistro = psRegistro + campoVacio;
			
			//5.- Importe total bajas
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 15, "I", "", "0");
			psRegistro = psRegistro + campoVacio;
			
			//6.- Numero de registros cambio (no aplica)
			//7.- Importe de registros de cambio (no aplica)
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 25, "I", "", "0");
			psRegistro = psRegistro + campoVacio;
			
			//8.- Numero de registros rechazados alta
			//9.- Importe de registros rechazados alta
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 25, "I", "", "0");
			psRegistro = psRegistro + campoVacio;
							
			//10.- Numero de registros rechazados baja
			//11.- Importe de registros rechazados baja
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 25, "I", "", "0");
			psRegistro = psRegistro + campoVacio;
			
			//12.- Numero de registros rechazados cambio
			//13.- Importe de registros rechazados cambio
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 25, "I", "", "0");
			psRegistro = psRegistro + campoVacio;
				
			//14.- Registros de cobro aceptados de aplicacion (10)
			//15.- Importe de registros de cobro aceptados de aplicacion (15)
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 25, "I", "", "0");
			psRegistro = psRegistro + campoVacio;
							
			//16.- Registros de pago aceptados de aplicacion (10)
			//17.- Importe de registros de pago aceptados de aplicacion (15)
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 25, "I", "", "0");
			psRegistro = psRegistro + campoVacio;
			
			//18.- Registros de cobro rechazados de aplicacion (10)
			//19.- Importe de registros de cobro rechazados de aplicacion (15)
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 25, "I", "", "0");
			psRegistro = psRegistro + campoVacio;
			
			//20.- Registros de pago rechazados de aplicacion (10)
			//21.- Importe de registros de pago rechazado de aplicacion (15)
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 25, "I", "", "0");
			psRegistro = psRegistro + campoVacio;
			
			//22.- Filler (para uso futuro)
			campoVacio = "";
			campoVacio = funciones.ajustarLongitudCampo(campoVacio, 1115, "I", "", "");
			psRegistro = psRegistro + campoVacio;
			
			System.out.println(psRegistro);				
			break;
			
			
			}
	}catch(Exception e) {
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:EnvioTransferencias, C:LayoutBancomerBusiness, M:armaBancomerH2H");
		e.printStackTrace();
	}return psRegistro;
}
	
	//Banamex Interbancario Digitem
	public String beArmaBanamexInter(List<MovimientoDto> listBus, int i) {
		System.out.println("entro a11 :.. metodo:beArmaBanamexInter en LayauBancomer.java");
		
		Map<String, Object> valorPagRef = new HashMap<String, Object>();
	    
		String psRegistro = "";
		String psDivisaLayout = "";
		String psCampo = "";
		String sReferenciaTraspaso = "";
	    
		try {
			if(listBus.get(i).getIdDivisa().equals("MN"))
		    	psDivisaLayout = "001";
		    else if(listBus.get(i).getIdDivisa().equals("DLS"))
		    	psDivisaLayout = "002";
		    else
		    	psDivisaLayout = "000";
		    
		    //Registro de transaccion interbancaria con CLABE
	    	psRegistro = psRegistro + "09";													//1 Tipo de Transaccion 9(02) --> 09	Constante
	    	psRegistro = psRegistro + "01";													//2 Tipo de cuenta origen 9(02) --> 01	Cheques
	    System.out.println("angel sucursalOrigen:..."+listBus.get(i).getSucOrigen());
	    	psCampo = funciones.ajustarLongitudCampo(listBus.get(i).getSucOrigen(), 4, "I", "", "0");
	    	//psCampo = funciones.ajustarLongitudCampo(listBus.get(i).getSucursalOrigen(), 4, "D", "", "0");
	    	psRegistro = psRegistro + psCampo;												//3 Sucursal Cuenta origen 9(04)
	    	psCampo=funciones.ajustarLongitudCampo(listBus.get(i).getIdChequera().substring(listBus.get(i).getIdChequera().length()-7),20,"D","", "0");
	    	psRegistro = psRegistro + psCampo;												//4 Cuenta Origen 9(20)
	    	//psRegistro.append(funciones.ponerFormatoCeros(dto.getImporte(), 13));										
	    	psCampo = "" + funciones.ponerFormatoCeros((listBus.get(i).getImporte()),14);
	    	psCampo = funciones.ajustarLongitudCampo(psCampo.replace(".", ""), 14, "D", "", "0");
	    	psRegistro = psRegistro + psCampo;												//5 Importe (12)v9(02)
	    	psCampo = psDivisaLayout;
	    	psRegistro = psRegistro + psCampo;												//6 Moneda 9(02) 001-->Pesos
	    	psRegistro = psRegistro + "40";													//7 Tipo Cuenta Destino 9(02)01--Chequers40--CLABE
	    	psCampo = listBus.get(i).getClabeBenef();
	    	psCampo = funciones.ajustarLongitudCampo(psCampo, 20, "D", "", "0");
	    	psRegistro = psRegistro + psCampo;												//8 Cuenta Destino 9(20)
	    	psCampo = funciones.quitarCaracteresRaros(listBus.get(i).getConcepto(), true);
	    	psCampo = funciones.ajustarLongitudCampo(psCampo, 40, "I", "", "");
	    	psRegistro = psRegistro + psCampo;												//9DescripcionX(40)Seenvialareferenciaalfanumerica
            if(sReferenciaTraspaso.equals("")) {
	    		//Solo aplica para Transferencias, Pagos referenciados a Clientes
	    		valorPagRef=layoutsDao.obtenerValorPagoReferenciado(Integer.parseInt(listBus.get(i).getNoCliente()),listBus.get(i).getNoFolioDet(), "");
            	psCampo = valorPagRef.get("valorPagRef").toString();
	    	}else
	    		psCampo = sReferenciaTraspaso;
	    	
	    	psCampo = funciones.ajustarLongitudCampo(psCampo, 7, "D", "", "0");
	    	psRegistro = psRegistro + psCampo;       										//10 Referencia 9(07)
			System.out.println("BeenficiarioAngel:.."+listBus.get(i).getBeneficiario());
	    	psCampo = funciones.quitarCaracteresRaros(listBus.get(i).getBeneficiario(), true);
			psCampo = funciones.ocultarCoinversora(psCampo);
			System.out.println("BeenficiarioAngel22:.."+listBus.get(i).getBeneficiario());
			System.out.println("campo aver:.."+psCampo);
			System.out.println("tam:.."+psCampo.length());
			
			if(psCampo.length() > 50) psCampo = psCampo.substring(50);
			psCampo = ajustaBeneficiarioMoralFisica(psCampo, listBus.get(i).getRfcBenef());
			psCampo = funciones.ajustarLongitudCampo(psCampo, 55, "I", "", "");
			psRegistro = psRegistro + psCampo;												//11 Beneficiario X(55)
		
			if(listBus.get(i).getImporte() >= 50000 || listBus.get(i).getIdBancoBenef() == 12)
				psCampo = "00";
			else
				psCampo = "24";

			psRegistro = psRegistro + psCampo;												//12 Plazo
			psCampo = "" + listBus.get(i).getIdBancoBenef();
			psCampo = funciones.ajustarLongitudCampo(psCampo, 4, "D", "", "0");
			psRegistro = psRegistro + psCampo;												//15 Banco Beneficiario 9(04)
			psRegistro = psRegistro + "000000";												//16 Fecha de aplicacion ddMMyy
			psRegistro = psRegistro + "0000";
			}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:beArmaBanamexInter");
			e.printStackTrace();
			return "";
		}
		return psRegistro;
	}
	
	
	public String beArmaBanamexInterT(List<BuscarSolicitudesTraspasosDto> listBus, int i) {
		Map<String, Object> valorPagRef = new HashMap<String, Object>();
	
		String psRegistro = "";
		String psDivisaLayout = "";
		String psCampo = "";
		String sReferenciaTraspaso = "";
	    
		try {
			if(listBus.get(i).getIdDivisa().equals("MN"))
		    	psDivisaLayout = "001";
		    else if(listBus.get(i).getIdDivisa().equals("DLS"))
		    	psDivisaLayout = "002";
		    else
		    	psDivisaLayout = "000";
		    
		    //Registro de transaccion interbancaria con CLABE
	    	psRegistro = psRegistro + "09";													//1 Tipo de Transaccion 9(02) --> 09	Constante
	    	psRegistro = psRegistro + "01";													//2 Tipo de cuenta origen 9(02) --> 01	Cheques
	    	//psCampo = funciones.ajustarLongitudCampo(listBus.get(i).getSucOrigen(), 4, "D", "", "0");
	    	System.out.println("sucursal blok::.."+listBus.get(i).getNoFolioDet());
	    	psCampo = funciones.ajustarLongitudCampo(String.valueOf(listBus.get(i).getNoFolioDet()), 4, "I", "", "0");
	    	psRegistro = psRegistro + psCampo;												//3 Sucursal Cuenta origen 9(04)
	    	psCampo = funciones.ajustarLongitudCampo(listBus.get(i).getIdChequera().substring(listBus.get(i).getIdChequera().length() - 7), 20, "D", "", "0");
	    	psRegistro = psRegistro + psCampo;												//4 Cuenta Origen 9(20)
	    	//psRegistro.append(funciones.ponerFormatoCeros(dto.getImporte(), 13));										
	    	psCampo = "" + funciones.ponerFormatoCeros((listBus.get(i).getImporte()),14);
	    	psCampo = funciones.ajustarLongitudCampo(psCampo.replace(".", ""), 14, "D", "", "0");
	    	psRegistro = psRegistro + psCampo;												//5 Importe (12)v9(02)
	    	psCampo = psDivisaLayout;
	    	psRegistro = psRegistro + psCampo;												//6 Moneda 9(02) 001-->Pesos
	    	psRegistro = psRegistro + "40";													//7 Tipo Cuenta Destino 9(02) 01-->Chequers 40-->CLABE
	    	psCampo = listBus.get(i).getClabeBenef();
	    	psCampo = funciones.ajustarLongitudCampo(psCampo, 20, "D", "", "0");
	    	psRegistro = psRegistro + psCampo;												//8 Cuenta Destino 9(20)
	    	psCampo = funciones.quitarCaracteresRaros(listBus.get(i).getConcepto(), true);
	    	psCampo = funciones.ajustarLongitudCampo(psCampo, 40, "I", "", "");
	    	psRegistro = psRegistro + psCampo;												//9 Descripcion X(40)  Se envia la referencia alfanumerica
	    	
	    	if(sReferenciaTraspaso.equals("")) {
	    		//Solo aplica para Transferencias, Pagos referenciados a Clientes
	    		valorPagRef = layoutsDao.obtenerValorPagoReferenciado(Integer.parseInt(listBus.get(i).getNoCliente()), listBus.get(i).getNoFolioDet(), "");
            	psCampo = valorPagRef.get("valorPagRef").toString();
	    	}else
	    		psCampo = sReferenciaTraspaso;
	    	
	    	psCampo = funciones.ajustarLongitudCampo(psCampo, 7, "D", "", "0");
	    	psRegistro = psRegistro + psCampo;       										//10 Referencia 9(07)
	    	System.out.println("BeenficiarioAngelblock:.."+listBus.get(i).getBeneficiario());
	    	psCampo = funciones.quitarCaracteresRaros(listBus.get(i).getBeneficiario(), true);
			psCampo = funciones.ocultarCoinversora(psCampo);
			psRegistro = psRegistro + psCampo;
			System.out.println("BeenficiarioAngel:.."+psCampo.length());
			if(psCampo.length() > 50) psCampo = psCampo.substring(50);
			psCampo = ajustaBeneficiarioMoralFisica(psCampo, listBus.get(i).getRfcBenef());
			psCampo = funciones.ajustarLongitudCampo(psCampo, 55, "I", "", "");
			psRegistro = psRegistro + psCampo;												//11 Beneficiario X(55)
			
			if(listBus.get(i).getImporte() >= 50000 || listBus.get(i).getIdBancoBenef() == 12)
				psCampo = "00";
			else
				psCampo = "24";

			psRegistro = psRegistro + psCampo;												//12 Plazo
			psCampo = "" + listBus.get(i).getIdBancoBenef();
			psCampo = funciones.ajustarLongitudCampo(psCampo, 4, "D", "", "0");
			psRegistro = psRegistro + psCampo;												//15 Banco Beneficiario 9(04)
			psRegistro = psRegistro + "000000";												//16 Fecha de aplicacion ddMMyy
			psRegistro = psRegistro + "0000";												//17 Hora de aplicacion HHmm
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:beArmaBanamexInter");
			e.printStackTrace();
			return "";
		}
		return psRegistro;
	}
	
	public String ajustaBeneficiarioMoralFisica(String sBeneficiario, String sRFCBenef) {
		boolean bMoral = true;
		String sCampo1 = "";
		
		try {
			sBeneficiario = funciones.quitarCaracteresRaros(sBeneficiario.trim().replace("   ", " ").replace("  ", " "), true);
			sRFCBenef = sRFCBenef.trim().replace(" ", "").replace("-", "").replace("/", "");
			
			if(!sRFCBenef.equals("") && sRFCBenef.length() >= 12) {//(12 Morales y 13 fisicas)
				sRFCBenef = funciones.quitarCaracteresRaros(sRFCBenef, true);
				//validacion en base a que las personas morales solo tienen 3 letras antes de la fecha
				// no sirve la siguinte linea se cae el programa StringIndexOutBounE
				if(!funciones.isNumeric(sRFCBenef.substring(3, 1))) bMoral = false;
			}
			//Formato Extrano para personas fisicas y morales
			if(bMoral) {
				/* Si analizamos el layout asi deberian ir las personas morales pero el de soporte
				 * de banamex dijo que lo pusieramos como fisica a ver que pex DESC-MS 11/02/2005
				 */
				sBeneficiario = "," + sBeneficiario + "/";
			}else {
				//Validacion para cuando solo traiga un apellido se le agrega una X como complemento del nombre
				if(sBeneficiario.indexOf(" ") == 0)
					sBeneficiario = sBeneficiario + " X X";
				else if(sBeneficiario.indexOf(" ", sBeneficiario.indexOf(" ") + 1) == 0)
					sBeneficiario = sBeneficiario + " X";
				
				sCampo1 = sBeneficiario.substring(sBeneficiario.indexOf(" ") + 1);
				sBeneficiario = sBeneficiario.substring(sBeneficiario.indexOf(" ") - 1) + "," +
								sCampo1.substring(sCampo1.indexOf(" ") - 1) + "/" +
								sCampo1.substring(sCampo1.indexOf(" ") + 1);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:ajustaBeneficiarioMoralFisica");
			return "";
		}
		return sBeneficiario;
	}
	
	//Banamex Mismo Banco Digitem, Flat File Citibank (Solo DLS) numero 1, santander
	//Revisado A.A.G 
	public Map<String, Object> beExportacion(List<MovimientoDto> listBus, int j, int tEnvio, String nomArchivo, String sRuta,
								 int idBanco, String pantalla) {
	    iBanco = idBanco;
	    liTipoTransfer = tEnvio;
	    
		try {
			System.out.println("idBanco "+idBanco+" tEnvio "+tEnvio);
			structBE = layoutsDao.envioBEParametrizado(idBanco, tEnvio); 
			System.out.println("structBE"+structBE.toString());
			System.out.println("listBus"+listBus.toString());
			if(structBE.size() <= 0) {
				mapResp.put("msgUsuario", "No se encontraron datos en envio_be del id_banco = " + idBanco + "");
				return mapResp;
			}
			//System.out.println("pantalla "+pantalla);
			if(pantalla.equals("TRANSFERENCIA")) {
				
				System.out.println("transfer::..Angel metodo:..beExportacion");
				if(layoutsDao.consultarConfiguraSet(227).equals("A") || idBanco != 107) {
					armaLayout(listBus, j, pantalla, false);
					//System.out.println("mapResp "+mapResp.toString());
					if(!mapResp.isEmpty() && (Boolean)mapResp.get("msgVF") == true)
						return mapResp;
					else {
						mapResp.put("msgVF", false);
						//mapResp.put("msgUsuario", "Error al crear al layout.");
						return mapResp;
					}
				}
			}else if(pantalla.equals("CONTROL ARCHIVO")) {
				armaLayout(listBus, j, "CONTROL_ARCHIVO", false);
				
				if(!mapResp.isEmpty())
					return mapResp;
				else {
					mapResp.put("msgVF", false);
					return mapResp;
				}
			}else if(pantalla.equals("TRASPASO")) {
				

				System.out.println("TRASPASO::..Angel metodo:..beExportacion");
				if(layoutsDao.consultarConfiguraSet(227).equals("A") || idBanco != 107) {//Checa si el envio es por Archivo
					
					System.out.println("----inicio parametros para armar el layaut----");
					System.out.println(listBus);
					System.out.println(j);
					System.out.println(pantalla);
					System.out.println(false);
					System.out.println("----Fin parametros para armar el layaut----");
					
					armaLayout(listBus, j, pantalla, false);
					if(!mapResp.isEmpty() && (Boolean)mapResp.get("msgVF") == true)
						return mapResp;
					else {
						mapResp.put("msgVF", false);
						//mapResp.put("msgUsuario", "Error al crear al layout.");
						return mapResp;
					}
				}
				
				
				
		       /*1   Funcion visual migrada  gobjVarGlobal.valor_configura_set(227) = "A" Or piNumBanco <> 107 Then 'Checa si el envio es por Archivo
		        
		            If arma_layout(MSGrid, posGrid, psRegistro) = True Then
		                If genera_archivo(psArchivo, lbAbierto, psRegistro) = False Then
		                    BEExportacion = False
		                    Screen.MousePointer = 0
		                    MsgBox "Error al generar archivo B.E", vbCritical, "SET"
		                    Exit Function
		                Else
		                    lbAbierto = True
		                End If
		            Else
		                BEExportacion = False
		                Screen.MousePointer = 0
		                Exit Function
		            End If
		            */
				
				//codigo para la pantalla de traspasos.
				System.out.println("----------codigo para pantalla de traspasos");
				
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:beExportacion");
			return mapResp;
		}
		return mapResp;
	}
	//Revisado A.A.G 
	public Map<String, Object>armaLayout(List<MovimientoDto> listGrid, int pos, String pantalla, boolean lbTraspasoControlArchivo) {
		String sRes = "";
		String sRegistro = "";
		String psCampo = "";
		String sFEspecial = "";
		String campoGrid = "";
		//pos = 0;
		mapResp.put("msgVF", true);
		
		System.out.println("----Dentro del metodo para armar el layaut parametros q llegan ----");
		System.out.println(listGrid);
		System.out.println(pos);
		System.out.println(pantalla);
		System.out.println(lbTraspasoControlArchivo);
		System.out.println("----Fin parametros para armar el layaut m2----");
		
		
		try {
		System.out.println("dentro del try metodo:..armaLayot ");
			//Se obtienen los datos del Layout para generar el Registro que sera insertado en el Archivo.txt
			for(int i=0; i<structBE.size(); i++) {
				System.out.println("tamaño de StructBE "+structBE.size());
				//for(int j=0;j<listGrid.size();j++){
					System.out.println("datos contadores "+"i:"+i+" pos:"+pos);
					System.out.println("campo a obtener "+structBE.get(i).getCampo().toString());
					campoGrid = obtenerCampo(structBE.get(i).getCampo().toString() != null ? structBE.get(i).getCampo().toString() : "" , i, listGrid, pos, pantalla, false);
					//System.out.println("armaLayout->campoGrid: "+campoGrid);
					psCampo = campoGrid;
					System.out.println("1 campo grid "+campoGrid);
					System.out.println("2 campo "+psCampo);
					//Si existe el campo en la tabla de equivalencias regresa una poscinn mayor a 0
					if(!campoGrid.equals("falso")) {
						//System.out.println("3");
						//Validacinn para pago referenciado de proveedores especificos
						if(structBE.get(i).getCampo().toString().equals("no_folio_det")) {
							//System.out.println("pantalla "+pantalla);
							if(pantalla.equals("TRANSFERENCIA")) {
								psCampo = layoutsDao.obtenValorPagoReferenciado(listGrid.get(pos).getNoCliente(), listGrid.get(pos).getNoFolioDet(), "");
								psCampo = psCampo.trim().replace("-", "").replace(" ", "");
								//System.out.println("psCampo "+psCampo);
							}else if(pantalla.equals("CONTROL_ARCHIVO")) {
								if(!lbTraspasoControlArchivo) {
									psCampo = layoutsDao.obtenValorPagoReferenciado(listGrid.get(pos).getNoCliente(), listGrid.get(pos).getNoFolioDet(), "");
									psCampo = psCampo.trim().replace("-", "").replace(" ", "");
								}
							}
							if(psCampo.equals("")) psCampo = campoGrid;
						}
						
						if(structBE.get(i).getCampo().toLowerCase().equals("beneficiario") || structBE.get(i).getCampo().toLowerCase().equals("concepto") ||
								structBE.get(i).getCampo().toLowerCase().equals("especiales") || structBE.get(i).getCampo().toLowerCase().equals("complemento")) {
							
							if(!structBE.get(i).getCampo().toLowerCase().equals("especiales") && !structBE.get(i).getCampo().toLowerCase().equals("complemento"))
								psCampo = funciones.quitarCaracteresRaros(psCampo, true);
							psCampo = funciones.ocultarCoinversora(psCampo);
							
							//Se agrega IF para revisar campo concepto
							if(structBE.get(i).getCampo().toLowerCase().equals("concepto") && pantalla.equals("TRANSFERENCIA") && 
									structBE.get(i).getIdBanco() == ConstantesSet.CITIBANK_DLS && structBE.get(i).getIdTipoEnvio() == 5) {
								//Si FURTHER CREDIT es parte del concepto este se envia en el registro de WL en el campo Bank Details
								if(psCampo.toUpperCase().indexOf("FURTHER") == -1 || psCampo.toUpperCase().indexOf("CREDIT") == -1) psCampo = "";
							}
						}
						psCampo = validaEquivalenciasBE(psCampo, structBE.get(i).getIdTipoEquivale());
						psCampo = validaFormato(psCampo, structBE.get(i).getFormato().trim());	//Valida el Formato del campo
						sRes = validaCampoRequerido(structBE.get(i).getBRequerido(), psCampo,pos, i);
						System.out.println("sRes "+sRes);
						if(!sRes.equals("")) {
							//System.out.println("sRES esta vacio");
							mapResp.put("msgVF", false);
							mapResp.put("msgUsuario", sRes);
							mapResp.put("msgRegistro", "");
							return mapResp;
						}
						sFEspecial = structBE.get(i).getFuncionEspecial() == null ? "" : structBE.get(i).getFuncionEspecial().trim();
						
						if(!sFEspecial.equals("")) {
							if(sFEspecial.substring(sFEspecial.length() - 3).equals("MID"))
								psCampo = sFEspecial.substring(Integer.parseInt(sFEspecial.substring(3, 6)), Integer.parseInt(sFEspecial.substring(6, 9)));
						}
						if(structBE.get(i).getCampo().equals("id_divisa") && psCampo.equals("YEN") && iBanco == ConstantesSet.CITIBANK_DLS && liTipoTransfer == 5) psCampo = "JPY";
						
						//Regresa el Registro que sera insertado en el Archivo
						sRegistro = sRegistro + funciones.ajustarLongitudCampo(psCampo, structBE.get(i).getLongitud(), structBE.get(i).getJustifica(), 
								structBE.get(i).getSeparador(), structBE.get(i).getComplemento());
						//System.out.println("esta es la posicion"+i);
					//	System.out.println("campo "+psCampo);
					//	System.out.println("sregistro "+sRegistro);
					}else {
						mapResp.put("msgVF", false);
						mapResp.put("msgUsuario", "No existe campo de la tabla equivalencias " + structBE.get(i).getCampo().toString());
						return mapResp;
					}
				//}
				
			}
			System.out.println("sregistro f "+sRegistro);
			//System.out.println("esta es i"+i);
			mapResp.put("msgRegistro", sRegistro);
		}catch(Exception e) {
			System.out.println("aaaaaaa-------aaaaaaaaaaaaa");
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:armaLayout");
			mapResp.put("msgVF", false);
			return mapResp;
		}
		return mapResp;
	}
	
	public String obtenerCampo(String sCampo, int posCampo, List<MovimientoDto> listGrid, int pos, String pantalla, boolean interBancario) {
		//Funcion para Guardar equivalencias con el Grid y el Layout, Guarda la posicinn que tiene el campo en el Grid
		String regCampo = "falso";
		System.out.println( " motodo obtenerCampo parametros sCampo "+sCampo +" posCampo "+posCampo+
				" pos "+pos);
		try {
			//for(int m=0;m<listGrid.size();m++){
			System.out.println("posCapmpo:"+posCampo);
				System.out.println("listGrid pos: cb "+listGrid.get(pos).getIdChequeraBanamex());
				System.out.println("listGrid pos: cp"+listGrid.get(pos).getIdChequeraPago());
//				System.out.println("listGrid m: "+listGrid.get(pos).getImporte());
				if(pantalla.equals("TRANSFERENCIA")) {
					if(iBanco == ConstantesSet.INBURSA && interBancario) {
						if(sCampo.equals("id_clabe")) 
							regCampo = listGrid.get(pos).getIdChequera();
					}else {
						if(sCampo.equals("id_chequera")) 
							regCampo = listGrid.get(pos).getIdChequera();
						System.out.println("TRANSFERENCIA-regCampoIdChequera "+regCampo);
					}
					//Para chequeras en dolares que tiene id_clabe pasan por aqui
					if(sCampo.equals("id_chequera_benef") && listGrid.get(pos).getIdDivisa().equals("DLS") &&
							(layoutsDao.consultarConfiguraSet(1).equals("CCM") || layoutsDao.consultarConfiguraSet(1).equals("CIE")))
						regCampo = listGrid.get(pos).getClabeBenef();
					else{
						if(sCampo.equals("id_chequera_benef")) regCampo = listGrid.get(pos).getIdChequeraBenef();
					}
					if(sCampo.equals("id_banco_benef")) regCampo = "" + listGrid.get(pos).getIdBancoBenef();
					if(sCampo.equals("plaza")) regCampo = "" + listGrid.get(pos).getPlazaBenef();
					if(sCampo.equals("plaza_origen")) regCampo = "" + listGrid.get(pos).getPlaza();
					if(sCampo.equals("no_cliente")) regCampo = listGrid.get(pos).getNoCliente();
					if(sCampo.equals("beneficiario")) regCampo = listGrid.get(pos).getBeneficiario();
					if(sCampo.equals("importe")) regCampo = "" + listGrid.get(pos).getImporte();
					//System.out.println("obtenerCamo-> importe: "+listGrid.get(pos).getImporte());
					if(sCampo.equals("concepto")) regCampo = listGrid.get(pos).getConcepto();
					if(sCampo.equals("id_divisa")) regCampo = listGrid.get(pos).getIdDivisa();
					if(sCampo.equals("no_folio_det")) regCampo = "" + listGrid.get(pos).getNoFolioDet();
					if(sCampo.equals("fec_valor")) regCampo = validaFormato(funciones.ponerFechaSola(listGrid.get(pos).getFecValor()), structBE.get(posCampo).getFormato());
					if(sCampo.equals("fecha_hoy")) regCampo = layoutsDao.fechaHoy();
					if(sCampo.equals("valor_default")) regCampo = structBE.get(posCampo).getValorDefault();
					if(sCampo.equals("inst_finan")) regCampo = listGrid.get(pos).getInstFinan();
					if(sCampo.equals("sucursal_origen")) regCampo = listGrid.get(pos).getSucOrigen();
					if(sCampo.equals("sucursal_destino")) regCampo = listGrid.get(pos).getSucDestino();
					if(sCampo.equals("hora")) regCampo = listGrid.get(pos).getHoraRecibo();
					if(sCampo.equals("observacion")) regCampo = listGrid.get(pos).getObservacion();
					if(sCampo.equals("desc_banco_inbursa")) regCampo = listGrid.get(pos).getDescBancoInbursa();
					if(sCampo.equals("id_clabe_benef")) regCampo = listGrid.get(pos).getClabeBenef();
					if(sCampo.equals("id_chequera_banamex")) regCampo = listGrid.get(pos).getIdChequeraBanamex();//pregunar
					
					if(sCampo.equals("desc_banco_benef")) regCampo = listGrid.get(pos).getDescBancoBenef();
					if(sCampo.equals("id_aba_swift")) regCampo = "" + listGrid.get(pos).getAba().toString();
					if(sCampo.equals("aba_swift")) regCampo = "" + listGrid.get(pos).getSwiftCode();
					if(sCampo.equals("id_AbaSwift_intermediario")) 
						regCampo = listGrid.get(pos).getAbaIntermediario() != null && !listGrid.get(pos).getAbaIntermediario().equals("") ?
								listGrid.get(pos).getAbaIntermediario():"";
					if(sCampo.equals("AbaSwift_intermediario")) 
						regCampo = listGrid.get(pos).getSwiftIntermediario() != null && !listGrid.get(pos).getSwiftIntermediario().equals("")?
								listGrid.get(pos).getSwiftIntermediario(): "";
					if(sCampo.equals("nom_banco_intermediario")) 
						regCampo = listGrid.get(pos).getNomBancoIntermediario() != null && !listGrid.get(pos).getNomBancoIntermediario().equals("") ?
								listGrid.get(pos).getNomBancoIntermediario():"";
					if(sCampo.equals("id_AbaSwift_corresponsal")) 
						regCampo = listGrid.get(pos).getAbaCorresponsal() != null && !listGrid.get(pos).getAbaCorresponsal().equals("") ?
								listGrid.get(pos).getAbaCorresponsal():"";
					if(sCampo.equals("AbaSwift_corresponsal")) 
						regCampo = listGrid.get(pos).getSwiftCorresponsal() != null && listGrid.get(pos).getSwiftCorresponsal().equals("") ?
								listGrid.get(pos).getSwiftCorresponsal():"";
					if(sCampo.equals("nom_banco_corresponsal")) 
						regCampo = listGrid.get(pos).getNomBancoCorresponsal() != null && listGrid.get(pos).getNomBancoCorresponsal().equals("") ?
								listGrid.get(pos).getNomBancoCorresponsal():"";
					if(sCampo.equals("rfc")) 
						regCampo = listGrid.get(pos).getRfc() != null && listGrid.get(pos).getRfc().equals("") ? 
								listGrid.get(pos).getRfc():"";
					if(sCampo.equals("nom_empresa")) 
						regCampo = listGrid.get(pos).getNomEmpresa() != null && listGrid.get(pos).getNomEmpresa().equals("") ?
								listGrid.get(pos).getNomEmpresa():"";
					if(sCampo.equals("id_contrato_wlink")) 
						regCampo = listGrid.get(pos).getIdContratoWlink() != null && listGrid.get(pos).getIdContratoWlink().equals("")?
								listGrid.get(pos).getIdContratoWlink():"";
					if(sCampo.equals("especiales")) regCampo = listGrid.get(pos).getEspeciales();
					if(sCampo.equals("complemento")) regCampo = listGrid.get(pos).getComplemento();
					//if(sCampo.equals("pais_banco_const")) regCampo = listGrid.get(pos).getPaisBancoConst();
					//if(sCampo.equals("direccion_banco_const")) regCampo = listGrid.get(pos).getDireccionBancoConst();
					if(sCampo.equals("pais_benef_const")) regCampo = listGrid.get(pos).getPaisBenef();
					if(sCampo.equals("direccion_benef_const")) regCampo = listGrid.get(pos).getDireccionBenef();
				}else if(pantalla.equals("CONTROL ARCHIVO")) {
					/*Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_ID_CHEQUERA, "id_chequera")
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_ID_CHEQUERA_BENEF_REAL, "id_chequera_benef")
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_ID_BANCO_BENEF, "id_banco_benef")
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_PLAZA_BENEF, "plaza")
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_PLAZA_ORIGEN, "plaza_origen")
				            
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_NO_CLIENTE, "no_cliente")
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_BENEFICIARIO, "beneficiario")
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_IMPORTE, "importe")
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_CONCEPTO, "concepto")
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_ID_DIVISA, "id_divisa")
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_FOLIO_DET_REFERENCIA, "no_folio_det")
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_FEC_VALOR, "fec_valor")
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_FEC_HOY, "fecha_hoy")
				            Call BE_llena_estructura_equivale_BE(iIndice, 999, "valor_default")
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_INST_FINAN, "inst_finan")
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_SUCURSAL_ORIGEN, "sucursal_origen")
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_SUCURSAL_DESTINO, "sucursal_destino")
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_HORA, "hora")
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_OBSERVACION, "observacion")
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_DESC_BANCO_INBURSA, "desc_banco_inbursa")
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_ID_CLABE_BENEF, "id_clabe_benef")
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_ID_CHEQUERA_BANAMEX, "id_chequera_banamex")
				            
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_DESC_BANCO_BENEF, "desc_banco_benef")
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_ID_ABA_SWIFT, "id_aba_swift")
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_ABA_SWIFT, "aba_swift")
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_ID_ABASWIFT_INTERMEDIARIO, "id_AbaSwift_intermediario")
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_ABASWIFT_INTERMEDIARIO, "AbaSwift_intermediario")
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_NOM_BANCO_INTERMEDIARIO, "nom_banco_intermediario")
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_ID_ABASWIFT_CORRESPONSAL, "id_AbaSwift_corresponsal")
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_ABASWIFT_CORRESPONSAL, "AbaSwift_corresponsal")
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_NOM_BANCO_CORRESPONSAL, "nom_banco_corresponsal")
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_CONTRATO_WLINK, "id_contrato_wlink") 'ms 28/03/2007
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_ESPECIALES, "especiales")  'dgs 9/11/06
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_COMPLEMENTO, "complemento")  'ms 28/03/2007
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_REG_NOM_EMPRESA, "nom_empresa") 'ms 28/03/2007
				            */
				}else if(pantalla.equals("TRASPASO")) {
					if(sCampo.equals("id_chequera")) regCampo = "" + listGrid.get(pos).getIdChequera();
					if(sCampo.equals("id_chequera_benef")) regCampo = "" + listGrid.get(pos).getIdChequeraBenef();
					if(sCampo.equals("id_banco_benef")) regCampo = "" + listGrid.get(pos).getIdBancoBenef();
					if(sCampo.equals("plaza")) regCampo = "" + listGrid.get(pos).getPlaza();
					if(sCampo.equals("plaza_origen")) regCampo = "" + listGrid.get(pos).getPlazaBenef();
					if(sCampo.equals("no_cliente")) regCampo = "" + listGrid.get(pos).getNoCliente();
					if(sCampo.equals("beneficiario")) regCampo = "" + listGrid.get(pos).getBeneficiario();
					if(sCampo.equals("importe")) regCampo = "" + listGrid.get(pos).getImporte();
					if(sCampo.equals("concepto")) regCampo = "" + listGrid.get(pos).getConcepto();
					if(sCampo.equals("id_divisa")) regCampo = "" + listGrid.get(pos).getIdDivisa();
					/*
					        Call BE_llena_estructura_equivale_BE(iIndice, LC_CHEQUERA_TR, "id_chequera");
				            Call BE_llena_estructura_equivale_BE(iIndice, LC_CHEQUERA_BENEF_REAL_TR, "id_chequera_benef");
				            Call BE_llena_estructura_equivale_BE(iIndice, LC_ID_BANCO_BENEF_TR, "id_banco_benef");
				            Call BE_llena_estructura_equivale_BE(iIndice, LC_PLAZA_BENEF_TR, "plaza");
				            Call BE_llena_estructura_equivale_BE(iIndice, LC_PLAZA_ORIGEN_TR, "plaza_origen");
				            Call BE_llena_estructura_equivale_BE(iIndice, LC_NO_CLIENTE_TR, "no_cliente");
				            Call BE_llena_estructura_equivale_BE(iIndice, LC_BENEFICIARIO_TR, "beneficiario");
				            Call BE_llena_estructura_equivale_BE(iIndice, LC_IMPORTE_TR, "importe");
				            Call BE_llena_estructura_equivale_BE(iIndice, LC_CONCEPTO_TR, "concepto");
				            Call BE_llena_estructura_equivale_BE(iIndice, LC_DIVISA_TR, "id_divisa");
				      */      
				            //'La referencia para envios de Traspasos no es el folio_det como en trasferencias
				    
					if(sCampo.equals("no_folio_det")) regCampo = "" + listGrid.get(pos).getNoFolioDet();
					if(sCampo.equals("fec_valor")) regCampo = "" + listGrid.get(pos).getFecValor();
					if(sCampo.equals("fecha_hoy")) regCampo = "" + listGrid.get(pos).getFecAlta();
					if(sCampo.equals("valor_default")) regCampo = "" + listGrid.get(pos).getValorTasa();
					if(sCampo.equals("inst_finan")) regCampo = "" + listGrid.get(pos).getInstFinan();
					if(sCampo.equals("sucursal_origen")) regCampo = "" + listGrid.get(pos).getSucOrigen();
					if(sCampo.equals("sucursal_destino")) regCampo = "" + listGrid.get(pos).getSucDestino();
					if(sCampo.equals("hora")) regCampo = "" + listGrid.get(pos).getHoraRecibo();
					if(sCampo.equals("desc_banco_inbursa")) regCampo = "" + listGrid.get(pos).getDescBancoInbursa();
					if(sCampo.equals("id_clabe_benef")) regCampo = "" + listGrid.get(pos).getIdClabe();
					if(sCampo.equals("id_chequera_banamex")) regCampo = "" + listGrid.get(pos).getIdChequera();
					
					       /*Call BE_llena_estructura_equivale_BE(iIndice, LC_REFERENCIA_TR, "no_folio_det");
				            Call BE_llena_estructura_equivale_BE(iIndice, LC_FEC_VALOR_TR, "fec_valor");
				            Call BE_llena_estructura_equivale_BE(iIndice, LC_FEC_HOY_TR, "fecha_hoy"):
				            Call BE_llena_estructura_equivale_BE(iIndice, 999, "valor_default");
				            Call BE_llena_estructura_equivale_BE(iIndice, LC_INST_FINAN_TR, "inst_finan");
				            Call BE_llena_estructura_equivale_BE(iIndice, LC_SUCURSAL_ORIGEN_TR, "sucursal_origen");
				            Call BE_llena_estructura_equivale_BE(iIndice, LC_SUCURSAL_DESTINO_TR, "sucursal_destino");
				            Call BE_llena_estructura_equivale_BE(iIndice, LC_HORA_TR, "hora");
				            Call BE_llena_estructura_equivale_BE(iIndice, LC_OBSERVACION_TR, "observacion");
				            Call BE_llena_estructura_equivale_BE(iIndice, LC_DESC_BANCO_INBURSA_TR, "desc_banco_inbursa");
				            Call BE_llena_estructura_equivale_BE(iIndice, LC_ID_CLABE_BENEF_TR, "id_clabe_benef");
				            Call BE_llena_estructura_equivale_BE(iIndice, LC_ID_CHEQUERA_BANAMEX_TR, "id_chequera_banamex");
				            */
				    
					
					if(sCampo.equals("desc_banco_benef")) regCampo = "" + listGrid.get(pos).getDescBancoBenef();
					if(sCampo.equals("id_aba_swift")) regCampo = "" + listGrid.get(pos).getSwift();
					if(sCampo.equals("aba_swift")) regCampo = "" + listGrid.get(pos).getAba();
					if(sCampo.equals("id_AbaSwift_intermediario")) regCampo = "" + listGrid.get(pos).getAbaIntermediario();
					if(sCampo.equals("AbaSwift_intermediario")) regCampo = "" + listGrid.get(pos).getSwiftIntermediario();
					if(sCampo.equals("nom_banco_intermediario")) regCampo = "" + listGrid.get(pos).getNomBancoIntermediario();
					if(sCampo.equals("id_AbaSwift_corresponsal")) regCampo = "" + listGrid.get(pos).getSwiftCorresponsal();
					if(sCampo.equals("AbaSwift_corresponsal")) regCampo = "" + listGrid.get(pos).getAbaCorresponsal();
					if(sCampo.equals("nom_banco_corresponsal")) regCampo = "" + listGrid.get(pos).getNomBancoCorresponsal();
					if(sCampo.equals("especiales")) regCampo = "" + listGrid.get(pos).getEspeciales();
					if(sCampo.equals("complemento")) regCampo = "" + listGrid.get(pos).getComplemento();			
					if(sCampo.equals("id_contrato_wlink")) regCampo = "" + listGrid.get(pos).getIdContratoWlink();
					if(sCampo.equals("rfc")) regCampo = "" + listGrid.get(pos).getRfc();
					if(sCampo.equals("nom_empresa")) regCampo = "" + listGrid.get(pos).getNomEmpresa();
				            // 'Columnas nuevas
				    
					 		/*Call be_llena_estructura_equivale_be(iIndice, LI_C_DES_BANCO_BENEF_TR, "desc_banco_benef");
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_ID_ABA_SWIFT_TR, "id_aba_swift");
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_ABA_SWIFT_TR, "aba_swift");
				            
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_ID_ABASWIFT_INTERMEDIARIO_TR, "id_AbaSwift_intermediario");
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_ABASWIFT_INTERMEDIARIO_TR, "AbaSwift_intermediario");
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_NOM_BANCO_INTERMEDIARIO_TR, "nom_banco_intermediario");
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_ID_ABASWIFT_CORRESPONSAL_TR, "id_AbaSwift_corresponsal");
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_ABASWIFT_CORRESPONSAL_TR, "AbaSwift_corresponsal");
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_NOM_BANCO_CORRESPONSAL_TR, "nom_banco_corresponsal");
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_ESPECIALES_TR, "especiales");
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_COMPLEMENTO_TR, "complemento");//  'dgs 9/11/06
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_CONTRATO_WLINK_TR, "id_contrato_wlink");// 'ms 28/03/2007
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_RFC_TR, "rfc");
				            Call BE_llena_estructura_equivale_BE(iIndice, LI_C_NOM_EMPRESA_TR, "nom_empresa");//'ms 28/03/2007
				            */
				}
			//}
	
			//Se valida si trae valor, de lo contrario se toma el valor default
			System.out.println("obtenerCampo->reg campo "+regCampo);
			
			if(regCampo == null || regCampo.equals("") || regCampo.equals("0"))
				regCampo = structBE.get(posCampo).getValorDefault()==null ? "" : structBE.get(posCampo).getValorDefault();
		}catch(Exception e) {
			System.out.println("aaaaaaa-------");
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:llenaEquivaleBE");
			return "";
		}
		return regCampo;
	}
	
	
	public String validaEquivalenciasBE(String psCampo, int tipoEquivale) {
		try {
			System.out.println("psCampo"+psCampo+" tipoEquivale"+tipoEquivale);
			if(tipoEquivale > 0) {
				for(int i=0; i<structEquivaleCampo.size(); i++) {
					if(structEquivaleCampo.get(i).getIdTipoEquivale() == tipoEquivale) {
						System.out.println("el tipoequivale es igual");
						if(structEquivaleCampo.get(i).getValorCampo().trim() == psCampo.trim()) {
							System.out.println("el valor del campo es igual");
							psCampo = structEquivaleCampo.get(i).getValorEquivale();
							System.out.println("validaEquivalenciasBE "+psCampo);
							break;
						}
					}
				}
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:validaEquivalenciasBE");
		}
		return psCampo;
	}
	
	//Esta funcinn recibe el campo y el formato que debe de ser y lo Regresa con el Formato especificado en el Layout
	public String validaFormato(String psCampo, String formato) {
		try {
			if(formato.equals("999")) psCampo = funciones.ponerFormatoCeros(Double.parseDouble(psCampo), 2).replace(".", "");	//le quita el punto de decimales
			else if(formato.equals("9.99")) psCampo = psCampo.indexOf(".") != -1 ? funciones.ponerFormatoCeros(Double.parseDouble(psCampo), 2) : psCampo + ".00";			//le coloca el punto con 2 decimales
			else if(formato.equals("dd/mm/yyyy")) psCampo = funciones.cambiarFecha(psCampo, true);
//Victor comento			else if(formato.equals("ddMMyyyy")) psCampo = funciones.cambiarFecha(psCampo, true).replace("/", "");
			else if(formato.equals("ddMMyyyy")) psCampo = psCampo.replace("/", "");
			else if(formato.equals("ddMMyy")) {
				String fecha = funciones.cambiarFecha(psCampo, true).replace("/", "");
				psCampo = fecha.substring(0, 4) + fecha.substring(6);
			}
			// revisar el formato yyMMdd no esta funcionando bien
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
	
	public String validaCampoRequerido(String psRequerido, String psCampo, int posGrid, int posCampo) {
		String msg = "";
		System.out.println("campo requerido"+psRequerido);
		try {
			if(psRequerido.trim().equals("S")) {
				if(psCampo.trim().equals("") || psCampo.trim().equals("0"))
					msg = "El Registro No. " + posGrid + " tiene el campo " + structBE.get(posCampo).getCampo().toUpperCase() + " " +
							"\n con valor " + psCampo + ", " + "y no es un valor valido para el Layout ya que el Campo es Requerido";
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:validaCampoRequerido");
			return msg;
		}
		return msg;
	}
	
	//Banamex TEF se esta trabajando numero uno
	public Map<String, Object> beArmaBanamexTEF(boolean pbInterbancario, List<MovimientoDto> listGrid, boolean pbControl_archivo, 
								   String psReferenciaTraspaso, int iGrid, String dirPrincipal, String sDescBanco) {
		Map<String, Object> mapCreaArch = new HashMap<String,Object>();
		Map<String, Object> mapResult = new HashMap<String,Object>();
		List<MovimientoDto> encabezado = new ArrayList<MovimientoDto>();
		List<MovimientoDto> detalle = new ArrayList<MovimientoDto>();
		boolean pbCLABE = false;
		String psNomEmpresa = "";
		String psReferencia = "";
		String sRegistro = "";
		String sCampo = "";
		String psFoliosIn = "";
		int resDetArchTrans = 0;
		int piRegistros = 0;
		String psArchGenerados = "";
		String lsYaEjecutados = "";
		String sFolioDet = "";
		System.out.println("dentro del metodo:beArmaBanamexTEF  :...Angel");
		
		try {
			for(int i=0; i<listGrid.size(); i++) {
				if(envioTransferenciasDao.consultarYaEjecutado(listGrid.get(i).getNoFolioDet()))
		    		lsYaEjecutados += listGrid.get(i).getNoDocto() + ",";
		    	else
		    		sFolioDet += listGrid.get(i).getNoFolioDet() + ",";
			}
			if(!lsYaEjecutados.equals("")) {
				mapResult.put("msgUsuario", "Los siguientes documentos ya fueron procesados: \n" + lsYaEjecutados);
				
				if(sFolioDet.equals("")) {
					mapResult.put("msgVF", false);
					return mapResult;
				}
			}
			//Para el Interbancario utilizar la version "C"
	        //Para el Mismo Banco utilizar la version "B"
	        
	        //Buscar los datos necesarios con los folios det, se genera un archivo por chequera pagadora
			sFolioDet = sFolioDet.substring(0, sFolioDet.length()-1);
			
			if(psReferenciaTraspaso.trim().equals(""))
				encabezado = layoutsDao.encabezadoBitalEnvio(sFolioDet, false, pbControl_archivo);
			else
				encabezado = layoutsDao.encabezadoBitalEnvio(sFolioDet, false, false);
			
			for(int i=0; i<encabezado.size(); i++) {
				if(!pbControl_archivo) {
					mapCreaArch = creacionArchivosBusiness.generarNombreArchivo(sDescBanco, "arch_banamex_tef", "BT", pbInterbancario == true ? false : true, false, false, 0,1);
	   				System.out.print("generarNombreArchivo "+mapCreaArch);
	   				if(!mapCreaArch.isEmpty()) {
						if(mapCreaArch.get("msgUsuario") != null) {
							mapResult.put("msgUsuario", mapCreaArch.get("msgUsuario"));
							mapResult.put("msgVF", false);
							return mapResult;
						}
					}
				}
				if(encabezado.get(i).getNoContrato() == 0) {
					mapResult.put("msgUsuario", "NO se puede Generar el envio TEF sin Contrato TEF para la Empresa, de la chequera " + encabezado.get(i).getIdChequera());
					mapResult.put("msgVF", false);
					return mapResult;
				}
				if(layoutsDao.consultarConfiguraSet(209).equals("SI") && pbInterbancario) pbCLABE = true;
				
				psNomEmpresa = encabezado.get(i).getNomEmpresa();
				psNomEmpresa = funciones.ocultarCoinversora(psNomEmpresa);
				
				// *** Insertar encabezados ***
				mapResult = beEncabezadoBanamexTEF(mapCreaArch, psNomEmpresa, encabezado, i, 
						encabezado.get(i).getIdChequera().substring(0, 3), pbCLABE, pbInterbancario, dirPrincipal);
				
				if(!mapResult.isEmpty()) {
					if(mapResult.get("msgVF") != null) 
						return mapResult;
				}
				
				//Insertar detalles
				//Buscar el detalle de la chequera encontrada en el encabezado
				if(psReferenciaTraspaso.trim().equals(""))
					detalle = layoutsDao.detalleBitalEnvio(sFolioDet, listGrid.get(i).getIdChequera(), 0, pbControl_archivo);
				else
					detalle = layoutsDao.detalleBitalEnvio(sFolioDet, listGrid.get(i).getIdChequera(), 0, false);
				
				for(int x=0; x<detalle.size(); x++) {
					if(!psReferenciaTraspaso.trim().equals(""))
						psReferencia = psReferenciaTraspaso.trim();
					else
						psReferencia = "" + detalle.get(x).getNoFolioDet();
					
					//Solo aplica para Transferencias, Pagos referenciados a Clientes					
					if(psReferenciaTraspaso.trim().equals(""))
						psReferencia = layoutsDao.obtenValorPagoReferenciado(detalle.get(x).getNoCliente(), detalle.get(x).getNoFolioDet(), "");
					
					//DETALLE
                    //Registro de Cargos y Abonos Individuales
					sRegistro = "3";			//3--> Registro de movtos individuales
					sRegistro += "0";			//Tipo de operacion, 0 = abono, 1 = cargo
					sRegistro += "001";			//Clave de la moneda 001 = pesos M.N.
					sRegistro += funciones.ajustarLongitudCampo(validaFormato("" + detalle.get(x).getImporte(), "999"), 18, "D", "", "0");		//Importe a abonar o cargar 9(16)V9(02)
					//DESC-MS
					if(!pbInterbancario) {
						if(detalle.get(x).getIdChequeraBenef().trim().length() == 16)
							sCampo = "03";
						else
							sCampo = "01";
					}else
						sCampo = "01";
					
					sRegistro += sCampo;		//Tipo de cuenta 9(02) 01=cheques  40=CLABE
					
					if(!pbInterbancario)
						sRegistro += funciones.ajustarLongitudCampo(detalle.get(x).getSucDestino(), 4, "D", "", "0");		//Tipo de cuenta 9(02) 01=cheques
					
					if(pbCLABE)
						sCampo = detalle.get(x).getClabeBenef();
					else {
						if(pbInterbancario)
							sCampo = detalle.get(x).getIdChequeraBenef();
						else {
							//DESC-MS aqui agregar la logitud de 16 para pago matico
							if(detalle.get(x).getIdChequeraBenef().trim().length() == 16)
								sCampo = detalle.get(x).getIdChequeraBenef();
							else
								sCampo = detalle.get(x).getIdChequeraBenef().substring(detalle.get(x).getIdChequeraBenef().length() - 7);
						}
					}
					sRegistro += funciones.ajustarLongitudCampo(sCampo, 20, "D", "", "0");		//Numero de cuenta 9(20)
					sRegistro += funciones.ajustarLongitudCampo(psReferencia, 40, "I", "", "");	//REFERENCIA DE la operacion TEF X(40)
					sCampo = detalle.get(x).getBeneficiario().toUpperCase();
					sRegistro += funciones.ocultarCoinversora(sCampo);
					
					if(sCampo.length() > 50) 
						sCampo = sCampo.substring(0, 50);
					sCampo = ajustaBeneficiarioMoralFisica(sCampo, detalle.get(x).getRfcBenef());
					sCampo = funciones.ajustarLongitudCampo(sCampo, 55, "I", "", "");
					sRegistro += sCampo;				//Beneficiario X(55)
					sRegistro += funciones.ajustarLongitudCampo("", 40, "D", "", " ");			//Instrucciones X(40)
					sCampo = funciones.quitarCaracteresRaros(detalle.get(x).getConcepto(), true);
					sRegistro += funciones.ajustarLongitudCampo(sCampo, 24, "I", "", "");		//Descripcion TEF X(20) X(24)
					
					if(pbInterbancario) {
						sRegistro += funciones.ajustarLongitudCampo("" + detalle.get(x).getIdBancoBenef(), 4, "D", "", "0");		//Clave de banco 9(04)
						sRegistro += funciones.ajustarLongitudCampo(psReferencia, 7, "D", "", "0");		//Referencia Bajo Valor 9(07)
						
						if((detalle.get(x).getImporte() > 50000) || (detalle.get(x).getIdBancoBenef() == ConstantesSet.BANCOMER))
							sCampo = "00";
						else
							sCampo = "24";
						sRegistro += sCampo;			//Plazo para pagos
					}else {
						sRegistro += "00";				//Clave de estado 9(02)
						sRegistro += "0000";			//Clave de ciudad
						sRegistro += "0000";			//Clave de Banco
					} //aqui se modifico
					mapResult = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal, mapCreaArch.get("psBanco").toString(),
							   mapCreaArch.get("nomArchivo").toString(), sRegistro);
					if(!(Boolean)mapResult.get("resExito")) {
						mapResult.put("msgVF", false);
						mapResult.put("msgUsuario", "Error al Escribir el Detalle en el Archivo Banamex TEF!!");
						return mapResult;
					}
					
					//aqui se modifico
					if(!pbControl_archivo) {
						DetArchTransferDto dtoInsert = new DetArchTransferDto();
    		    		
    		    		dtoInsert.setNomArch(mapCreaArch.get("nomArchivo").toString().substring(0, 8));
    		    		dtoInsert.setNoFolioDet(detalle.get(x).getNoFolioDet());
    		    		dtoInsert.setNoDocto(detalle.get(x).getNoDocto());
    		    		dtoInsert.setFecValorDate(detalle.get(x).getFecValor());
    		    		dtoInsert.setIdChequera(encabezado.get(i).getIdChequera());
    		    		dtoInsert.setIdBanco(encabezado.get(i).getIdBanco());
    		    		dtoInsert.setIdBancoBenef(detalle.get(x).getIdBancoBenef());
    		    		dtoInsert.setIdChequeraBenef(detalle.get(x).getIdChequeraBenef());
    		    		dtoInsert.setPrefijoBenef(detalle.get(x).getInstFinan());
    		    		dtoInsert.setImporte(detalle.get(x).getImporte());
    		    		dtoInsert.setBeneficiario(funciones.quitarCaracteresRaros(detalle.get(x).getBeneficiario(), true));
    		    		dtoInsert.setSucursal(Integer.parseInt(detalle.get(x).getSucDestino()));
    		    		dtoInsert.setPlaza(Integer.parseInt(detalle.get(x).getPlazaBenef()));
    		    		dtoInsert.setConcepto(detalle.get(x).getConcepto());
    		    		dtoInsert.setIdEstatusArch("T");
    		    		
    		    		resDetArchTrans = layoutsDao.insertDetArchTransfer(dtoInsert);
    		    		
    		    		if(resDetArchTrans > 0) {
    		    			psFoliosIn += detalle.get(x).getNoFolioDet() + ",";
	                    	piRegistros++;
	                    }
					}
				}
				mapResult = beTrailBanamexTEF(encabezado.get(i).getImporte(), piRegistros, dirPrincipal, mapCreaArch);
				
				if(!mapResult.isEmpty()) {
					if(mapResult.get("msgVF") != null) return mapResult;
				}
				psArchGenerados += mapCreaArch.get("nomArchivo").toString().substring(0, 8) + ",";
				
				if(!pbControl_archivo) {
					//Grabar los datos en arch_transfer
					resDetArchTrans = layoutsDao.insertArchTransfer(mapCreaArch.get("nomArchivo").toString().substring(0, 8), ConstantesSet.BANAMEX,
							funciones.cambiarFecha(layoutsDao.fechaHoy(), true), encabezado.get(i).getImporte(), piRegistros);
					
					if(resDetArchTrans <= 0) {
						mapResult.put("msgVF", false);
						mapResult.put("msgUsuario", "No inserto el registro en arch_transfer");
						return mapResult;
					}
				}
			}
			if(mapResult.get("msgVF") == null) {
				if(!psFoliosIn.equals("")) {
					if(envioTransferenciasDao.actualizarMovimientoTipoEnvio("TEF", psArchGenerados) <= 0) {
						mapResult.put("msgUsuario", "Error al Actualizar estatus de los movimientos de Transferencias es necesario volver a generar el archivo");
						EnvioTransferenciasBusiness envioTransferenciasBusiness = new EnvioTransferenciasBusiness();
						envioTransferenciasBusiness.eliminarArchivosBe(dirPrincipal, mapCreaArch.get("psBanco").toString(), psArchGenerados);
						mapResult.put("msgVF", false);
						return mapResult;
					}
					mapResult.put("msgUsuario", "Los Registros han sido Guardados en el archivo " + dirPrincipal +
							mapCreaArch.get("psBanco").toString() + mapCreaArch.get("nomArchivo").toString());
				}
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:beArmaBanamexTEF");
		}
		return mapResult;
	}
	//actualizado A.A.G numero dos ok
	public Map<String, Object> beEncabezadoBanamexTEF(Map<String, Object> mapCreaArch, String psNomEmpresa, List<MovimientoDto> encabezado,
										 int i, String psSucursal, boolean pbCLABE, boolean pbInterbancario, String dirPrincipal) {
		Map<String, Object> result = new HashMap<String, Object>();
		String sRegistro = "";
		String sCampo = "";
		
		try {
			//Version C de Banamex TEF solo para Interbancarios
			//Registro de Control
			sRegistro = "1";																//Registro de Control 9 (01)
			sCampo = "" + encabezado.get(i).getNoContrato();
			sRegistro += funciones.ponerCeros(sCampo.substring(0, sCampo.indexOf(".")), 12);	//No Cliente 9 (12)	//gobjVarGlobal.valor_Configura_set(208)), String(12, "0"))
			sRegistro += validaFormato(layoutsDao.fechaHoy(), "ddMMyy");					//Fecha de pago 9 (06)
			sCampo = "" + layoutsDao.folioRealTEF(encabezado.get(i).getIdContrato());		//gobjVarGlobal.Folio_Real("TEF_dia")  'String(4, "X")
			
			if(Integer.parseInt(sCampo) == -1) {
				result.put("msgVF", false);
				result.put("msgUsuario", "No se encontrn el folio <<TEF_dia>>");
				return result;
			}
			if(Integer.parseInt(sCampo) > 99) {
				result.put("msgVF", false);
				result.put("msgUsuario", "No puede haber mas de 99 archivos secuenciales Banamex TEF por dna");
				return result;
			}
			sRegistro += funciones.ajustarLongitudCampo(sCampo, 4, "D", "", "0");			//Secuencial del Archivo 9 (04)
			sRegistro += funciones.quitarCaracteresRaros(funciones.ajustarLongitudCampo(psNomEmpresa.trim(), 36, "I", "", ""), true);	//Nombre de la empresa X (36)
			sRegistro += funciones.ajustarLongitudCampo(sCampo, 20, "I", "", "");			//Descripcion del Archivo X (20)
			
			if(!pbInterbancario)
				sCampo = "06";												//Pago a Proveedores mismo banco
			else 
				if(pbCLABE) 
					sCampo = "12";													//Pago Interbancario
				else 
					sCampo = "07";																//Pago Interbancario
			
			sRegistro += sCampo;															//Naturaleza del Archivo 9 (02)
			sRegistro += funciones.ajustarLongitudCampo("", 40, "D", "", " ");				//Instrucciones para ordenes de pago X (40)
			
			if(pbInterbancario)
				sCampo = "C";
			else 
				sCampo = "B";
			
			sRegistro += sCampo;															//Version layout X (01) version C o B
			sRegistro += "0";																//Volumen 9 (01) primer diskette = 0
			sRegistro += "1";																//Caracteristicas del archivo 9 (01) 0 = Modificable, 1 = Solo lectura
			result = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal, mapCreaArch.get("psBanco").toString(),
					   mapCreaArch.get("nomArchivo").toString(), sRegistro);
			if(!(Boolean)result.get("resExito")) { //cambiar
				result.put("msgVF", false);
				result.put("msgUsuario", result.get("msgUsuario").toString());
				return result;
			}
			
			//Registro Global
			sRegistro = "2";																//Tipo registro 9(01) 2 = Registro global
			sRegistro += "1";																//Tipo operacion 0 = Abono, 1 = Cargo
			sRegistro += "001";																//Clave de la moneda 9 (03) 001 = pesos MN
			sRegistro += funciones.ajustarLongitudCampo(validaFormato("" + encabezado.get(i).getImporte(), "999"), 18, "D", "", "0");	//Importe a abonar o cargar 9(16)V9(02)
			sRegistro += "01";																//Tipo de cuenta 9(02) 01=cheques
			sRegistro += funciones.ajustarLongitudCampo(psSucursal, 4, "D", "", "0");		//Sucursal 9(04)
			sRegistro += funciones.ajustarLongitudCampo(encabezado.get(i).getIdChequera().substring(encabezado.get(i).getIdChequera().length() - 7), 20, "D", "", "0") ;	//Numero de Cuenta 9(20)
			sRegistro += funciones.ajustarLongitudCampo("", 20, "D", "", " ");				//Espacio en blanco X(20)
			result = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal, mapCreaArch.get("psBanco").toString(),
					   mapCreaArch.get("nomArchivo").toString(), sRegistro);
			if(!(Boolean)result.get("resExito")) {
				result.put("msgVF", false);
				result.put("msgUsuario", result.get("msgUsuario").toString());
				return result;
			}
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:beEncabezadoBanamexTEF");
		}
		return result;
	}
	//actualizado A.A.G
	public Map<String, Object> beTrailBanamexTEF(double dImporte, int piRegistros, String dirPrincipal, Map<String, Object> mapCreaArch) {
		Map<String, Object> result = new HashMap<String, Object>();
		String sRegistro = "";
		
		try {
			//Registro de Totales
			sRegistro = "4";									//Registro de Control 9(01) 4 = Registro de totales
			sRegistro += "001";									//Clave de la moneda 9(03)  001 = MN
			sRegistro += funciones.ajustarLongitudCampo("" + piRegistros, 6, "D", "", "0");			//Numero de Abonos 9(06)
			sRegistro += funciones.ajustarLongitudCampo(validaFormato("" + dImporte, "999"), 18, "D", "", "0");		//Importe total de Abonos 9(16)V9(02)
			sRegistro += funciones.ajustarLongitudCampo("1", 6, "D", "", "0");			//Numero de cargos 9(06)
			sRegistro += funciones.ajustarLongitudCampo(validaFormato("" + dImporte, "999"), 18, "D", "", "0");		//Importe total de Cargos 9(16)V9(02)
			result = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal, mapCreaArch.get("psBanco").toString(),
					   mapCreaArch.get("nomArchivo").toString(), sRegistro);
			if(!(Boolean)result.get("resExito")) {
				result.put("msgVF", false);
				result.put("msgUsuario", "Error al Escribir Registro de Totales en el Archivo Banamex TEF!!"); //aqui
				return result;
			}
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:beTrailBanamexTEF");
		}
		return result;
	}
	//Actualizado A.A.G actualizando 13 no se concluyo
	//Banamex MassPayment
	public Map<String, Object> beArmaBanamexMassPay(boolean pbInterbancario, List<MovimientoDto> listGrid, boolean pbControl_archivo, 
								   String psReferenciaTraspaso, int iGrid, String dirPrincipal,int h2hBBVA) {
		Map<String, Object> mapCreaArch = new HashMap<String,Object>();
		Map<String, Object> mapResult = new HashMap<String,Object>();
		Map<String, Object> valorPagRef = new HashMap<String, Object>();
		
		List<MovimientoDto> encabezado = new ArrayList<MovimientoDto>();
		List<MovimientoDto> detalle = new ArrayList<MovimientoDto>();
		
		String sRutaEnvioMASSPAYMENT = "";
		/*String sFolios3200 = "";
		String sFolios3000 = "";*/
		String lsYaEjecutados = "";
		String sFolioDet = "";
		String sPrefijoMassPay = "";
		String sRegistro = "";
		String sDivisa = "";
		String psReferencia = "";
		String sCampo = "";
		String psFoliosIn = "";
		String psArchGenerados = "";
		
		//boolean bBanamexMassPayment = false;
		boolean bInsertarFoliosTMP = false;
		
		int resDetArchTrans = 0;
		int piRegistros = 0;
		double importeTot = 0;
		
		try {
			if(layoutsDao.consultarConfiguraSet(1).equals("CIE")) {
				/*If Not gobjSeguridad.ValidaFacultad("ARCHMASSP", sMensaje) Then
		                MsgBox "No tiene facultades para realizar esta operacinn!", vbExclamation, "SET"
		                Screen.MousePointer = 0
		                Exit Sub
		          End If*/
			}
			sRutaEnvioMASSPAYMENT = layoutsDao.consultarConfiguraSet(196);
			
			if(layoutsDao.consultarConfiguraSet(212).equals("SI")) {
				if(sRutaEnvioMASSPAYMENT.equals("")) {
					mapResult.put("msgUsuario", "No se encontro la ruta para Masspayment, configura_set(196)");
					return mapResult;
				}//else
					//bBanamexMassPayment = true;
			}
			dirPrincipal = sRutaEnvioMASSPAYMENT;
			
			for(int i=0; i<listGrid.size(); i++) {
				if(listGrid.get(i).getIdContratoMass() == 0) {
					mapResult.put("msgUsuario", "La empresa " + listGrid.get(i).getNoEmpresa() + " No tiene asignado contrato para Mass Payment");
					return mapResult;
				}
				if(envioTransferenciasDao.consultarYaEjecutado(listGrid.get(i).getNoFolioDet()))
		    		lsYaEjecutados += listGrid.get(i).getNoDocto() + ",";
		    	else {
		    		sFolioDet += listGrid.get(i).getNoFolioDet() + ",";
		    		
		    		/*if(listGrid.get(i).getIdTipoOperacion() == 3200)
		    			sFolios3200 += listGrid.get(i).getNoFolioDet() + ",";
		    		else
		    			sFolios3000 += listGrid.get(i).getNoFolioDet() + ",";*/
		    	}
			}
			if(!lsYaEjecutados.equals("")) {
				mapResult.put("msgUsuario", "Los siguientes documentos ya fueron procesados: \n" + lsYaEjecutados);
				if(sFolioDet.equals("")) return mapResult;
			}
			sFolioDet = sFolioDet.substring(0, sFolioDet.length()-1);
			
			if(psReferenciaTraspaso.trim().equals(""))
				if(contandorFolios(sFolioDet) > 150) {
					bInsertarFoliosTMP = true;
					insertarFoliosTMP(sFolioDet);
					encabezado = layoutsDao.encabezadoBitalEnvio("TABLA_TMP", true, pbControl_archivo);
				}else
					encabezado = layoutsDao.encabezadoBitalEnvio(sFolioDet, true, pbControl_archivo);
			else
				encabezado = layoutsDao.encabezadoBitalEnvio(sFolioDet, true, false);
			
			for(int i=0; i<encabezado.size(); i++) {
				if(!pbControl_archivo) {
					if(layoutsDao.consultarConfiguraSet(1).equals("CIE")) {
						sPrefijoMassPay = encabezado.get(i).getIdDivisa();
						mapCreaArch = creacionArchivosBusiness.generarNombreArchivo("Banamex\\MassPayment", sPrefijoMassPay + "_arch_banamex_mass", sPrefijoMassPay, 
								pbInterbancario == true ? false : true, false, false, 0,h2hBBVA);
					}else {
						sPrefijoMassPay = layoutsDao.consultarConfiguraSet(218);
						
						if(sPrefijoMassPay.equals("")) {
							mapResult.put("msgUsuario", "No se encontrn el prefijo para el archivo de MassPayment");
							return mapResult;
						}
						mapCreaArch = creacionArchivosBusiness.generarNombreArchivo("Banamex\\MassPayment", "arch_banamex_mass", 
								sPrefijoMassPay, pbInterbancario == true ? false : true, false, false, 0,h2hBBVA);
					}
	   				if(!mapCreaArch.isEmpty()) {
						if(mapCreaArch.get("msgUsuario") != null) {
							mapResult.put("msgUsuario", mapCreaArch.get("msgUsuario"));
							return mapResult;
						}
					}
				}
				// Tipo Paylink
				// Llenar el encabezado del registro
				sRegistro = "00";																						//1. Record Type N(2) 00-->FILE HEADER
				sRegistro += layoutsDao.fechaHoy().replace("-", "");													//2. Creation Date Date(8) yyyyMMdd
				sRegistro += funciones.obtenerHoraActual(false).substring(0, 8).replace(":", "").replace(" ", "");		//3. Creation Time Time(6)
				sRegistro += funciones.ajustarLongitudCampo(encabezado.get(i).getNoContratoMass(), 10, "I", "", "");	//4. Company Identification A(10)
				sRegistro += funciones.ajustarLongitudCampo("", 74, "D", "", "");										//5. Reserved A(74)
				mapResult = creacionArchivosBusiness.escribirArchivoLayout(sRutaEnvioMASSPAYMENT, "", mapCreaArch.get("nomArchivo").toString(), sRegistro);
				if(!mapResult.get("msgUsuario").equals("")) {
					mapResult.put("msgUsuario", "Error al Escribir Registro de encabezado en el Archivo Banamex MassPayment!!");
					return mapResult;
				}
				
				// *** Insertar Detalles ***
				//Buscar el detalle con los folios det
				if(psReferenciaTraspaso.trim().equals("")) {
					if(bInsertarFoliosTMP)
						detalle = layoutsDao.detalleBitalEnvio("TABLA_TMP", "", encabezado.get(i).getIdContratoMass(), pbControl_archivo);
					else
						detalle = layoutsDao.detalleBitalEnvio(sFolioDet, "", encabezado.get(i).getIdContratoMass(), pbControl_archivo);
				}else
					detalle = layoutsDao.detalleBitalEnvio(sFolioDet, "", encabezado.get(i).getIdContratoMass(), false);
				
				for(int x=0; x<detalle.size(); x++) {
					if(psReferenciaTraspaso.trim().equals("")) {
						//Solo aplica para Transferencias, Pagos referenciados a Clientes
						valorPagRef = layoutsDao.obtenerValorPagoReferenciado(Integer.parseInt(detalle.get(x).getNoCliente()), detalle.get(x).getNoFolioDet(), "");
						psReferencia = valorPagRef.get("valorPagRef").toString();
					}else
						psReferencia = psReferenciaTraspaso.trim();
					
					if(detalle.get(x).getIdDivisa().trim().equals("MN"))
						sDivisa = "MXP";
					else if(detalle.get(x).getIdDivisa().trim().equals("DLS"))
						sDivisa = "USD";
					else
						sDivisa = "   ";
					
					sRegistro = "01";																	//1. Record Type N(2)
					
					if(layoutsDao.consultarConfiguraSet(1).equals("CIE")) {
						layoutsDao.actualizarFolioReal(sPrefijoMassPay + "_sec_maspayment");
						sCampo = "" + layoutsDao.seleccionarFolioReal(sPrefijoMassPay + "_sec_maspayment");
					}else {
						layoutsDao.actualizarFolioReal("sec_maspayment");
						sCampo = "" + layoutsDao.seleccionarFolioReal("sec_maspayment");
					}
					if(Integer.parseInt(sCampo) < 0) {
						mapResult.put("msgUsuario","No se encontrn el folio (sec_maspayment)");
			        	return mapResult;
					}
					sRegistro += funciones.ajustarLongitudCampo(sCampo, 8, "D", "", "0");				//2. Transaction Sequence number N(8)
					
					if(detalle.get(x).getIdDivisa().equals("MN"))										//3. Costumer Country Code A(2)
						sRegistro += "MX";
					else
						sRegistro += "US";
					sCampo = funciones.quitarCaracteresRaros(funciones.ocultarCoinversora(detalle.get(x).getNomEmpresa().trim()), true);
					sRegistro += funciones.ajustarLongitudCampo(sCampo, 35, "I", "", "");				//4. Costumer Company Name A(35)
					
					if(sDivisa.equals("MXP"))
						sCampo = "X";
					else
						sCampo = "";
					sRegistro += funciones.ajustarLongitudCampo(sCampo, 14, "I", "", "");				//5. Third Party tax Id Number A(14)
					sRegistro += funciones.ajustarLongitudCampo(psReferencia, 16, "I", "", "");			//6. Transaction reference A(16)
					sRegistro += "C";																	//7. Transaction type A(1) C=Payment, D=direct Debit
					
					if(sDivisa.equals("MXP"))
						sCampo = "071";
					else
						sCampo = "001";	//DLS
					sRegistro += sCampo;																//8. Transaction Code N(3) 071->paylink
					//AT DESC 23/02/06
                    //EL PLAZO PARA PAGO: 05 MISMO DIA, 15 DIA SIGUIENTE (acreditacion al cta benef)
					if(sDivisa.equals("MXP"))
						if(detalle.get(x).getIdBancoBenef() != ConstantesSet.BANAMEX && detalle.get(x).getIdBancoBenef() != ConstantesSet.BANCOMER && detalle.get(x).getImporte() < 50000)
							sCampo = "15";
						else
							sCampo = "05";
					else
						sCampo = "  ";
					sRegistro += sCampo;																//9. Local Transaction code N(2)
					sRegistro += sDivisa;																//10. Customer Account Currency Code A(3)
					sRegistro += funciones.ajustarLongitudCampo(validaFormato("" + detalle.get(x).getImporte(), "999"), 17, "D", "", "0");		//11. Transaction amount N(17)
					
					if(layoutsDao.consultarConfiguraSet(1).equals("CCM"))
						sCampo = "" + detalle.get(x).getFecValorOriginal();
					else
						sCampo = layoutsDao.fechaHoy().replace("-", "");
					sRegistro += sCampo;																//12. Transaction date Date(8) yyyyMMdd
					
					if(sDivisa.equals("MXP"))
						sCampo = detalle.get(x).getIdChequera().substring(detalle.get(x).getIdChequera().length() - 10);	//Tipo BANAMEX
					else
						sCampo = detalle.get(x).getIdChequera().substring(detalle.get(x).getIdChequera().length() - 8);		//Tipo Citibank
					sRegistro += funciones.ajustarLongitudCampo(sCampo, 34, "I", "", "");				//13. Costumer Account number A(34)
					
					if(sDivisa.equals("MXP"))
						sCampo = "01";
					else
						sCampo = "  ";
					sRegistro += sCampo;																//14. Costumer Account type N(2) 01-> Cheques, 05->CLABE
					sRegistro += sDivisa;																//15. Third Party Currency Code A(3)
					
					if(sDivisa.equals("MXP"))
						sCampo = detalle.get(x).getNoCliente().trim();
					else
						sCampo = "";
					sRegistro += funciones.ajustarLongitudCampo(sCampo, 20, "I", "", "");				//16. Third Party Code A(20)
					
					//Toma  la cta clabe si no toma chequera benef
					if(sDivisa.equals("MXP")) {
						if(detalle.get(x).getIdChequeraBenef().length() == 16)
							sCampo = detalle.get(x).getIdChequeraBenef();
						else
							sCampo = detalle.get(x).getClabe();
					}else {
						if(layoutsDao.consultarConfiguraSet(1).equals("CCM") || layoutsDao.consultarConfiguraSet(1).equals("CIE")) {
							if(!detalle.get(x).getClabe().trim().equals(""))
								sCampo = detalle.get(x).getClabe();
							else
								sCampo = detalle.get(x).getIdChequeraBenef();
						}else
							sCampo = detalle.get(x).getIdChequeraBenef();
					}
					sRegistro += funciones.ajustarLongitudCampo(sCampo, 34, "I", "", "");				//17. Third Party Account number A(34)
					
					if(sDivisa.equals("MXP")) {
						//AT PAGOMATICO
						if(detalle.get(x).getIdChequeraBenef().length() == 16)
							sCampo = "01";			//03-DEBITO  01-cheques
						else
							sCampo = "05";			//CLABE
					}else
						sCampo = "  ";
					sRegistro += sCampo;																//18. Third Party Account type A(2) 01-> Cheques, 05->CLABE
					
					if(sDivisa.equals("MXP"))
						sCampo = "00001";
					else
						sCampo = funciones.ajustarLongitudCampo("", 5, "D", "", "");
					sRegistro += sCampo;																//19. Transaction Delivery Method N(5)*/
					sCampo = detalle.get(x).getBeneficiario();
					sCampo = funciones.quitarCaracteresRaros(funciones.ocultarCoinversora(detalle.get(x).getBeneficiario().trim()), true);
					sRegistro += funciones.ajustarLongitudCampo(sCampo, 80, "I", "", "");				//20. Third party name A(80)
					sRegistro += funciones.ajustarLongitudCampo("X", 35, "I", "", "");					//21. Third party Adress 1 A(35)
					sRegistro += funciones.ajustarLongitudCampo("X", 35, "I", "", "");					//22. Third party Adress 2 A(35)
					sRegistro += funciones.ajustarLongitudCampo("X", 35, "I", "", "");					//23. Third party Adress 3 A(35)
					
					if(sDivisa.equals("MXP"))															//24. Third party State A(2)
						sRegistro += funciones.ajustarLongitudCampo("X", 2, "I", "", "");
					else
						sRegistro += funciones.ajustarLongitudCampo("", 2, "I", "", "");
					sRegistro += funciones.ajustarLongitudCampo("", 16, "I", "", "");					//25. Third party phone number N(16)
					
					if(sDivisa.equals("MXP")) {
						if(detalle.get(x).getIdChequeraBenef().length() == 16 && detalle.get(x).getIdBancoBenef() == 2)
							sCampo = "0040";
						else
							sCampo = funciones.ajustarLongitudCampo("" + detalle.get(x).getIdBancoCity(), 4, "D", "", "0");
					}else {
						sCampo = "";
						
						if(!detalle.get(x).getAba().equals("")) //aquie s aba_benef y swift_benef
							sCampo = detalle.get(x).getAba();
						else
							sCampo = detalle.get(x).getSwift();
						
						if(sCampo.equals(""))
							sCampo = "SWIFT/ABA ";	//por si no trae datos
					}
					sRegistro += funciones.ajustarLongitudCampo(sCampo, 11, "I", "", "");				//26. Third party Bank number A(11)
					
					if(sDivisa.equals("MXP")) {
						sRegistro += funciones.ajustarLongitudCampo("001", 8, "I", "", "");				//27. Third party Bank Agency A(8)
						sRegistro += funciones.ajustarLongitudCampo("", 30, "I", "", "");				//28. Third party Bank Adress A(30)
						sRegistro += "01";																//29. Third party Bank Entity N(2)
						sRegistro += "001";																//30. Third party Bank Place Number N(3)
						sRegistro += funciones.ajustarLongitudCampo("", 14, "I", "", "");				//31. Third party Bank Place Name A(14)
						sRegistro += funciones.ajustarLongitudCampo("1", 6, "D", "", "0");				//32. Third party Bank Branch Number N(6)
					}else {
						sRegistro += funciones.ajustarLongitudCampo("", 8, "I", "", "");				//27. Third party Bank Agency A(8)
						sRegistro += funciones.ajustarLongitudCampo("", 30, "I", "", "");				//28. Third party Bank Adress A(30)
						sRegistro += funciones.ajustarLongitudCampo("", 2, "I", "", "");				//29. Third party Bank Entity N(2)
						sRegistro += funciones.ajustarLongitudCampo("", 3, "I", "", "");				//30. Third party Bank Place Number N(3)
						sRegistro += funciones.ajustarLongitudCampo("", 14, "I", "", "");				//31. Third party Bank Place Name A(14)
						sRegistro += funciones.ajustarLongitudCampo("", 6, "D", "", "");				//32. Third party Bank Branch Number N(6)
					}
					sRegistro += funciones.ajustarLongitudCampo("", 19, "D", "", "");					//33. Third party Bank Branch Name A(19)
					sRegistro += funciones.ajustarLongitudCampo("", 16, "D", "", "");					//34. Third party fax number N(16)
					sRegistro += funciones.ajustarLongitudCampo("", 20, "D", "", "");					//35. Third party fax contact name A(20)
					sRegistro += funciones.ajustarLongitudCampo("", 15, "D", "", "");					//36. Third party fax Departament name A(15)
					sRegistro += funciones.ajustarLongitudCampo("", 50, "D", "", "");					//37. Collection title Id A(50)
					sRegistro += funciones.ajustarLongitudCampo("", 5, "D", "", "");					//38. Third Party Activity Code A(5)
					sRegistro += funciones.ajustarLongitudCampo("", 11, "D", "", "");					//39. Intermediary Bank SWIFT Code A(11)
					sRegistro += funciones.ajustarLongitudCampo("", 20, "D", "", "");					//40. Company Discretionary Data A(20)
					sRegistro += funciones.ajustarLongitudCampo("", 8, "D", "", "");					//41. Company Descriptive Date Date(8)
					sRegistro += funciones.ajustarLongitudCampo("", 8, "D", "", "");					//42. Maturity Date Date(8)
					
					if(sDivisa.equals("MXP"))
						sRegistro += " ";																//43. Charge code N(1) 1-->Cargo al Beneficiario, 2--> Cargo al Cliente
					else
						sRegistro += "2";																//43. Charge code N(1) 1-->Cargo al Beneficiario, 2--> Cargo al Cliente
					sCampo = funciones.quitarCaracteresRaros(detalle.get(x).getConcepto().trim(), true);
					sRegistro += funciones.ajustarLongitudCampo(sCampo, 35, "I", "", "");				//44. Transactions Detal Line 1 A(35)
					sRegistro += funciones.ajustarLongitudCampo("", 35, "I", "", "");					//45. Transactions Detal Line 2 A(35)
					sRegistro += funciones.ajustarLongitudCampo("", 3, "I", "", "");					//46. Standard Entry Class A(3) PPD--> ACH
					sRegistro += funciones.ajustarLongitudCampo("", 14, "I", "", "");					//47. Reserved N(14)
					sRegistro += funciones.ajustarLongitudCampo("", 11, "I", "", "");					//48. Costumer Bank Number N(11)
					sRegistro += funciones.ajustarLongitudCampo("", 10, "I", "", "");					//49. Company identification N(10)
					sRegistro += funciones.ajustarLongitudCampo("", 10, "I", "", "");					//50. Company Entry Description A(10)
					sCampo = funciones.ajustarLongitudCampo(layoutsDao.selectCorreo(detalle.get(x).getNoCliente()), 50, "I", "", "");
					sRegistro += sCampo;																//51. Third Party e-mail adress A(50)
					sRegistro += funciones.ajustarLongitudCampo("", 35, "I", "", "");					//52. Third Party adress IB A(35)
					sRegistro += funciones.ajustarLongitudCampo("", 35, "I", "", "");					//53. Transaction Details Line 3 A(35)
					sRegistro += funciones.ajustarLongitudCampo("", 35, "I", "", "");					//54. Transaction Details Line 4 A(35)
					sRegistro += funciones.ajustarLongitudCampo("", 90, "I", "", "");					//55. Reserved A(90)
					
					mapResult = creacionArchivosBusiness.escribirArchivoLayout(sRutaEnvioMASSPAYMENT, "", mapCreaArch.get("nomArchivo").toString(), sRegistro);
					if(!mapResult.get("msgUsuario").equals("")) {
						mapResult.put("msgUsuario", "Error al Escribir Registro de detalle en el Archivo Banamex MassPayment!!");
						return mapResult;
					}
					if(!pbControl_archivo) {
						DetArchTransferDto dtoInsert = new DetArchTransferDto();
    		    		
    		    		dtoInsert.setNomArch(mapCreaArch.get("nomArchivo").toString().substring(0, 8));
    		    		dtoInsert.setNoFolioDet(detalle.get(x).getNoFolioDet());
    		    		dtoInsert.setNoDocto(detalle.get(x).getNoDocto());
    		    		dtoInsert.setFecValorDate(detalle.get(x).getFecValor());
    		    		dtoInsert.setIdChequera(encabezado.get(i).getIdChequera());
    		    		dtoInsert.setIdBanco(encabezado.get(i).getIdBanco());
    		    		dtoInsert.setIdBancoBenef(detalle.get(x).getIdBancoBenef());
    		    		dtoInsert.setIdChequeraBenef(detalle.get(x).getIdChequeraBenef());
    		    		dtoInsert.setPrefijoBenef(detalle.get(x).getInstFinan());
    		    		dtoInsert.setImporte(detalle.get(x).getImporte());
    		    		dtoInsert.setBeneficiario(funciones.quitarCaracteresRaros(detalle.get(x).getBeneficiario(), true));
    		    		dtoInsert.setSucursal(Integer.parseInt(detalle.get(x).getSucDestino()));
    		    		dtoInsert.setPlaza(Integer.parseInt(detalle.get(x).getPlazaBenef()));
    		    		dtoInsert.setConcepto(detalle.get(x).getConcepto());
    		    		dtoInsert.setIdEstatusArch("T");
    		    		
    		    		resDetArchTrans = layoutsDao.insertDetArchTransfer(dtoInsert);
    		    		
    		    		if(resDetArchTrans > 0) {
    		    			importeTot += detalle.get(x).getImporte();
    		    			psFoliosIn += detalle.get(x).getNoFolioDet() + ",";
	                    	piRegistros++;
	                    }
					}
				}
				mapResult = beTrailBanamexMassPay(encabezado.get(i).getImporte(), piRegistros, sRutaEnvioMASSPAYMENT, mapCreaArch);
				
				if(!mapResult.isEmpty()) {
					if(mapResult.get("msgVF") != null) return mapResult;
				}
				psArchGenerados += mapCreaArch.get("nomArchivo").toString().substring(0, 8) + ",";
				
				if(!pbControl_archivo) {
					//Grabar los datos en arch_transfer
					resDetArchTrans = layoutsDao.insertArchTransfer(mapCreaArch.get("nomArchivo").toString().substring(0, 8), ConstantesSet.BANAMEX,
							funciones.cambiarFecha(layoutsDao.fechaHoy(), true), importeTot, piRegistros);
					
					if(resDetArchTrans <= 0) {
						mapResult.put("msgUsuario", "No inserto el registro en arch_transfer");
						return mapResult;
					}
				}
			}
			if(!psFoliosIn.equals("")) {
				if(envioTransferenciasDao.actualizarMovimientoTipoEnvio("TEF", psArchGenerados) <= 0) {
					mapResult.put("msgUsuario", "Error al Actualizar estatus de los movimientos de Transferencias es necesario volver a generar el archivo");
					EnvioTransferenciasBusiness envioTransferenciasBusiness = new EnvioTransferenciasBusiness();
					envioTransferenciasBusiness.eliminarArchivosBe(dirPrincipal, mapCreaArch.get("psBanco").toString(), psArchGenerados);
					return mapResult;
				}
				mapResult.put("msgUsuario", "Los Registros han sido Guardados en el archivo " + dirPrincipal +
						mapCreaArch.get("psBanco").toString() + mapCreaArch.get("nomArchivo").toString());
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:beArmaBanamexMassPay");
		}
		return mapResult;
	}
	
	public int contandorFolios(String sFolioDet) {
		int numFolios = 1;
		
		try {
			while(sFolioDet.indexOf(",") > 0) {
				numFolios++;
				sFolioDet = sFolioDet.substring(sFolioDet.indexOf(",") + 1);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:contandorFolios");
		}
		return numFolios;
	}
	
	public int insertarFoliosTMP(String sFolioDet) {
		try {
			//Eliminar folios_anteriores del usuario
			layoutsDao.deleteTMPFoliosBE();
			
			while(sFolioDet.indexOf(",") > 0) {
				layoutsDao.insertTMPFoliosBE(sFolioDet.substring(0, sFolioDet.indexOf(",")));
				sFolioDet = sFolioDet.substring(sFolioDet.indexOf(",") + 1);
			}
			layoutsDao.insertTMPFoliosBE(sFolioDet);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:contandorFolios");
		}
		return 1;
	}
	//Actualizado A.A.G
	public Map<String, Object> beTrailBanamexMassPay(double dImporte, int piRegistros, String dirPrincipal, Map<String, Object> mapCreaArch) {
		Map<String, Object> result = new HashMap<String, Object>();
		StringBuffer sRegistro = new StringBuffer();
		
		try {
			sRegistro.append("4");																						//1. Record Type N(2) 04-->FILE TRAILER
			sRegistro.append(funciones.ajustarLongitudCampo("" + (piRegistros + 2), 6, "D", "", "0"));					//2. Number of Records N(6) Sumatoria de todos los registros
			sRegistro.append(funciones.ajustarLongitudCampo("" + piRegistros, 6, "D", "", "0"));						//3. Number of transactions N(6)
			sRegistro.append(funciones.ajustarLongitudCampo(validaFormato("" + dImporte, "999"), 17, "D", "", "0"));	//4. Total Transaction Amount N(17)
			sRegistro.append(funciones.ajustarLongitudCampo("", 40, "D", "", ""));										//5. Hash Total A(40)
			sRegistro.append(funciones.ajustarLongitudCampo("", 29, "D", "", ""));										//6. Reserverd A(29)
			result = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal, "", mapCreaArch.get("nomArchivo").toString(), sRegistro.toString());
			if(!result.get("msgUsuario").equals("")) {
				result.put("msgVF", false);
				result.put("msgUsuario", "Error al Escribir Registro de Triller en el Archivo Banamex MassPayment!!");
				return result;
			}
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:beTrailBanamexMassPay");
		}
		return result;
	}
	
	/*
	 * 
	 Public Function Trailer_citibank_paylink_MN(ByVal piRegistros,ByVal pdTotalImporte As Double) As String
Dim psCampo As String
Dim ps_registro As String

On Error GoTo atrapa_error
            'TRAILER para citibank Paylink pagos MN
            
            psCampo = "TRL"
            ps_registro = ps_registro & psCampo       '1. RECORD TYPE A(3)
            psCampo = piRegistros
           
            psCampo = ajustar_longitud_campo(psCampo, 15, "D", "", "0")
            
            ps_registro = ps_registro & psCampo       '2. Number of transactions N(15)
            
            psCampo = Format(pdTotalImporte * 100, "####")
            
            psCampo = ajustar_longitud_campo(psCampo, 15, "D", "", "0")
            
            ps_registro = ps_registro & psCampo       '3. Total Transaction amount N(15)
            
            psCampo = "0"  'No aplica su uso por eso va en cero
            
            psCampo = ajustar_longitud_campo(psCampo, 15, "D", "", "0")
            ps_registro = ps_registro & psCampo       '4. Number of Beneficiary Records N(15)
            psCampo = piRegistros
            psCampo = ajustar_longitud_campo(psCampo, 15, "D", "", "0")
            ps_registro = ps_registro & psCampo       '5. Number of Records Sent N(15)
            psCampo = Space(37)
            ps_registro = ps_registro & psCampo       '6. 37 Blank Characteres
            
            Trailer_citibank_paylink_MN = ps_registro
            
Exit Function
atrapa_error:
    Screen.MousePointer = 0
    BitacoraError GI_USUARIO, "NetroBancaESite", "ModBE", "Trailer_citibank_paylink_MN", CStr(Err.Number), Err.Description, ""
    Err.Clear

End Function

	 */

	public String Trailer_citibank_paylink_MN(int piRegistro, double monto,boolean ban) {
		Map<String, Object> valorPagRef = new HashMap<String, Object>();
	    String psRegistro = "";
	    String psCampo = "";
	  
		System.out.println("entra a ver TRILER*/ .:: angel");

	    try {
	    	System.out.println("detrno del ttry TRL");
	    	psCampo = "TRL";
	    	psRegistro=psRegistro+psCampo;
	    	psCampo=String.valueOf(piRegistro);
	    	psCampo= funciones.ajustarLongitudCampo(psCampo, 15, "D","","0") ;  	
	    	psRegistro=psRegistro+psCampo;
	    	//psCampo=funciones.ponerFormatoImporte(monto * 100);
	    	//psCampo = funciones.ponerFormatoCerosSindecimales((listGrid.get(i).getImporte() * 100), 15);
	    	psCampo= funciones.ponerFormatoCerosSindecimales(monto * 100, 15);
	    	psCampo= funciones.ajustarLongitudCampo(psCampo, 15, "D","","0") ;  	
	    	psRegistro=psRegistro+psCampo;
	    	psCampo="0";
	    	psCampo= funciones.ajustarLongitudCampo(psCampo, 15, "D","","0") ; 
	    	psRegistro=psRegistro+psCampo;
	    	psCampo=String.valueOf(piRegistro);
	    	psCampo= funciones.ajustarLongitudCampo(psCampo, 15, "D","","0") ;  	
	    	psRegistro=psRegistro+psCampo;
	    	psCampo=funciones.ajustarLongitudCampo("", 37, "D",""," ") ;
	    	psRegistro=psRegistro+psCampo;
	    	
	    	System.out.println("fin  TRILER .:: angel");
		
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:beArmaCitibankPaylinkMN");
			e.printStackTrace();
			return "";
	
		}
		return psRegistro;
	
	
	}

	//Banamex CitibankPaylinkMN 484
	public String beArmaCitibankPaylinkMN484(List<MovimientoDto> listGrid, int i, String psReferenciaTraspaso) {
		Map<String, Object> valorPagRef = new HashMap<String, Object>();
	    String psRegistro = "";
	    String psCampo = "";
		try {
			System.out.println("entra a ver el folio .:: angel");
			//Citibank PayLink Plus 1024 para Pago Mismo dia SOLO MXP
			psRegistro = "PAY";														//1 Record Type (02)
			psRegistro += "484";													//2 Costumer Country Code (03) - 484 = Mexico
			psCampo = listGrid.get(i).getIdChequera().substring(listGrid.get(i).getIdChequera().length() - 10);
			psRegistro += psCampo;													//3 Customer Account Number (10)
			
			// "yyMMdd"
			if(layoutsDao.consultarConfiguraSet(1).equals("CCM"))
				psCampo = listGrid.get(i).getFecValorOriginal().toString();
			else
				psCampo = listGrid.get(i).getFecValor().toString();
			if(psCampo.substring(4, 5).equals("-"))
				psCampo = psCampo.substring(2, 4) + psCampo.substring(5, 7) + psCampo.substring(8, 10);
			else{
				psCampo =funciones.cambiarFechaGregoriana(psCampo);
				psCampo = "17" + psCampo.substring(0, 2) + psCampo.substring(3, 5);
			}
			psRegistro += psCampo;													//4 Transaction Date (6)
			psRegistro += "071";													//5 Transaction Code (3)
			
			if(psReferenciaTraspaso.equals("")) {
				//Solo aplica para Transferencias, Pagos referenciados a Clientes
				//int x = Integer.parseInt()
						System.out.println("parametro1----"+listGrid.get(i).getNoCliente());
						System.out.println("parametro2----"+listGrid.get(i).getNoFolioDet());
				valorPagRef = layoutsDao.obtenerValorPagoReferenciado(Integer.parseInt(listGrid.get(i).getNoCliente()), listGrid.get(i).getNoFolioDet(), "");
            	psCampo = valorPagRef.get("valorPagRef").toString();
			}else
				psCampo = psReferenciaTraspaso;
			psRegistro += funciones.ajustarLongitudCampo(psCampo, 15, "I", "", "");	//6 Transaction Reference A(15)
			
			layoutsDao.actualizarFolioReal("folio_paylink");
			psCampo = "" + layoutsDao.seleccionarFolioReal("folio_paylink");
			if(Integer.parseInt(psCampo) < 0) 
				return "";
			
			psRegistro += funciones.ajustarLongitudCampo(psCampo, 8, "D", "", "0");	//7 Transaction Sequence number  N(8)
			psRegistro += funciones.ajustarLongitudCampo("X", 20, "I", "", "");		//8 Beneficiary tax id number N(20)
			psRegistro += "MXN";													//9 Customer account Currency Code(3)
			psCampo = funciones.ajustarLongitudCampo(listGrid.get(i).getNoCliente().toString(), 20, "I", "", "");
			psRegistro += psCampo;													//10 Beneficiary Code A(20)
			psCampo = funciones.ponerFormatoCerosSindecimales((listGrid.get(i).getImporte() * 100), 15);
			//psCampo = funciones.ajustarLongitudCampo("" + (listGrid.get(i).getImporte() * 100), 15, "D", "", "0");
			psRegistro += psCampo;													//11 Transaction amount N(15)
			psRegistro += funciones.ajustarLongitudCampo("", 6, "D", "", "");		//12 MAturity Date DATE(6)
			psCampo = funciones.quitarCaracteresRaros(listGrid.get(i).getConcepto(), true);
			psRegistro += funciones.ajustarLongitudCampo(psCampo, 35, "I", "", "");	//13 Transaction Detail Line 1 A(35)
			psRegistro += funciones.ajustarLongitudCampo("", 35, "D", "", "");		//14 Transaction Detail Line 2 A(35)
			psRegistro += funciones.ajustarLongitudCampo("", 35, "D", "", "");		//15 Transaction Detail Line 3 A(35)
			psRegistro += funciones.ajustarLongitudCampo("", 35, "D", "", "");		//16 Transaction Detail Line 4 A(35)
			psRegistro += "05";														//17 Local Transaction Code N(2)
			psRegistro += "01";														//18 COstumer account type N(2)
			psCampo = funciones.quitarCaracteresRaros(listGrid.get(i).getBeneficiario(), true);
			psCampo = funciones.ocultarCoinversora(psCampo);
			psRegistro += funciones.ajustarLongitudCampo(psCampo, 80, "I", "", "");	//19 Beneficiary name A(80)
			psRegistro += funciones.ajustarLongitudCampo("X", 35, "I", "", "");		//20 Beneficiary Adress 1 A(35)
			//MS: se 
			psRegistro += funciones.ajustarLongitudCampo("", 35, "D", "", "");		//21 Beneficiary Adress 1 second line A(35)
			psRegistro += funciones.ajustarLongitudCampo("X", 15, "I", "", "");		//22 Beneficiary Adress 2 A(15) ciudad
			psRegistro += funciones.ajustarLongitudCampo("", 2, "D", "", "");		//23 Beneficiary state A(2)
			psRegistro += funciones.ajustarLongitudCampo("1", 12, "D", "", "0");	//24 Beneficiary adress 3 A(12) Cod. Postal
			psRegistro += funciones.ajustarLongitudCampo("", 16, "D", "", "");		//25 Beneficiary phone number N(16)
			//MS: falta obtener el banco citi
			psCampo = funciones.ajustarLongitudCampo(listGrid.get(i).getIdBancoCityStr(), 3, "D", "", "0");
			psRegistro += psCampo;													//26 Beneficiary Bank number N(3)
			psCampo = funciones.ajustarLongitudCampo("MEXICO", 8, "I", "", "");
			psRegistro += psCampo;													//27 Beneficiary Bank Agency A(8)
			psCampo = funciones.ajustarLongitudCampo(listGrid.get(i).getClabeBenef(), 35, "D", "", "0");
			psRegistro += psCampo;													//28 Beneficiary Account Number(35) SOLO INTERMEDIARIOS
			psRegistro += "05";														//29 Account type CR SOLO INTERBANCARIO N(2)
			psCampo = funciones.ajustarLongitudCampo("MEXICO", 30, "I", "", "");
			psRegistro += psCampo;													//30 Beneficiary Bank Adress A(30)
			psRegistro += "01";														//31 Beneficiary Bank Entity N(2)
			psRegistro += "001";													//32 Beneficiary Bank Place Number N(3)
			psCampo = funciones.ajustarLongitudCampo("MEXICO", 14, "I", "", "");
			psRegistro += psCampo;													//33 Beneficiary Bank Place Name  A(14)
			psRegistro += "001";													//34 Beneficiary Bank Branch Number N(3)
			//MS falta obtener el nombre dle banco benef
			psCampo = funciones.ajustarLongitudCampo(listGrid.get(i).getNombreBancoBenef(), 19, "I", "", "");
			psRegistro += psCampo;													//35 Beneficiary Bank Branch Name N(19)
			System.out.println("BancoBenef "+psRegistro);
			psRegistro += funciones.ajustarLongitudCampo("", 16, "I", "", " ");		//36 Beneficiary Fax Number N(16)
			psRegistro += funciones.ajustarLongitudCampo("", 20, "I", "", " ");		//37 Beneficiary Fax Contact Name A(20)
			psRegistro += funciones.ajustarLongitudCampo("", 15, "I", "", " ");		//38 Beneficiary Fax Department Name A(15)
			psRegistro += funciones.ajustarLongitudCampo("", 10, "D", "", "0");		//39 Beneficiary Account number N(10) SOLO MISMO BANCO
			psRegistro += funciones.ajustarLongitudCampo("", 2, "D", "", " ");		//40 Account Type CR N(2) SOLO MISMO BANCO
			psRegistro += "001";													//41 Transaction Deliver Method N(3) 001-->MEXICO
			psRegistro += funciones.ajustarLongitudCampo("", 50, "D", "", "");		//42 Collection Title ID A(50)
			psRegistro += funciones.ajustarLongitudCampo("", 5, "D", "", "");		//43 Beneficiary Activity code N(5)
			psRegistro += funciones.ajustarLongitudCampo("", 50, "D", "", "");		//44 Beneficiary e-mail adress A(50)
			psCampo = funciones.ajustarLongitudCampo("" + (listGrid.get(i).getImporte() * 100), 15, "D", "", "0");
			psCampo = funciones.ponerFormatoCerosSindecimales((listGrid.get(i).getImporte() * 100), 15);
			psRegistro += psCampo;													//45 Maximum payment amount N(15)
			psRegistro += funciones.ajustarLongitudCampo("", 1, "D", "", "");		//46 Update Type N(1)
			psRegistro += funciones.ajustarLongitudCampo("", 11, "D", "", "0");		//47 Check Number N(11)
			psRegistro += funciones.ajustarLongitudCampo("", 1, "D", "", "");		//48 Printed Check A(1)
			psRegistro += funciones.ajustarLongitudCampo("", 1, "D", "", "");		//49 Match Paid A(1)
			psRegistro += funciones.ajustarLongitudCampo("", 254, "D", "", "");		//50 Blank Characteres A(254)
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:beArmaCitibankPaylinkMN");
			e.printStackTrace();
			return "";
		}
		return psRegistro;
	}

	//Banamex CitibankPaylinkMN 485
		public String beArmaCitibankPaylinkMN485Original(List<MovimientoDto> listGrid, int i, String psReferenciaTraspaso) {
			Map<String, Object> valorPagRef = new HashMap<String, Object>();
		    String psRegistro = "";
		    String psCampo = "";
			try {
				System.out.println("tamaño de listGrid "+listGrid.size());
				System.out.println("parametro i:.. "+listGrid.size());
				//for(int i=0;i<listGrid.size();i++){
					System.out.println("entra a ver el folio .:: angel");
					//Citibank PayLink Plus 1024 para Pago Mismo dia SOLO MXP
					psRegistro = "PAY";														//1 Record Type (02)
					psRegistro += "485";													//2 Costumer Country Code (03) - 484 = Mexico
					
					//MS: psCampo = listGrid.get(i).getIdChequera().substring(listGrid.get(i).getIdChequera().length() - 10);
					//MS: psRegistro += psCampo;													//3 Customer Account Number (10)
					psRegistro += funciones.ajustarLongitudCampo("", 10, "D", "", "");		//MS se agregaron 10 espacios
					
					// "yyMMdd"
					if(layoutsDao.consultarConfiguraSet(1).equals("CCM"))
						psCampo = listGrid.get(i).getFecValorOriginal().toString();
					else
						psCampo = listGrid.get(i).getFecValor()+"";
						String f=funciones.ponerFechaSola(listGrid.get(i).getFecValor());
					
					if(psCampo.substring(4, 5).equals("-"))
						psCampo = psCampo.substring(2, 4) + psCampo.substring(5, 7) + psCampo.substring(8, 10);
					else{
						psCampo =funciones.cambiarFechaGregoriana(psCampo);
						//psCampo = "17" + psCampo.substring(0, 2) + psCampo.substring(3, 5);
						psCampo = "17" + psCampo.substring(3,5) + psCampo.substring(0,2);

					}
					
					psRegistro += psCampo;													//4 Transaction Date (6)
					
					//If pbChqOcurre = True Then 'ACM 21/12/12 - Se agrega código 073
				    //        psCampo = "073"
			        //ElseIf pbInterbancario = False Then
			        //    psCampo = "072"
			        //Else
			        //    psCampo = "001"
			        //End If
					
					//psRegistro += "071";													//5 Transaction Code (3)
					if (listGrid.get(i).getIdBanco() == listGrid.get(i).getIdBancoBenef())
						psCampo = "072";
					else
						psCampo = "001";
					psRegistro+=psCampo;
					
					
					if(psReferenciaTraspaso.equals("")) {
						//Solo aplica para Transferencias, Pagos referenciados a Clientes
						//int x = Integer.parseInt()
								System.out.println("parametro1----"+listGrid.get(i).getNoCliente());
								System.out.println("parametro2----"+listGrid.get(i).getNoFolioDet());
						valorPagRef = layoutsDao.obtenerValorPagoReferenciado(Integer.parseInt(listGrid.get(i).getNoCliente()), listGrid.get(i).getNoFolioDet(), "");
		            	System.out.println("psCampo"+valorPagRef.get("valorPagRef").toString());
						psCampo = valorPagRef.get("valorPagRef").toString();
		            	
					}else
						psCampo = psReferenciaTraspaso;
					psRegistro += funciones.ajustarLongitudCampo(psCampo, 15, "I", "", "");	//6 Transaction Reference A(15)
					
					layoutsDao.actualizarFolioReal("folio_paylink");
					psCampo = "" + layoutsDao.seleccionarFolioReal("folio_paylink");
					if(Integer.parseInt(psCampo) < 0){
						return "";
					}
						
					
					psRegistro += funciones.ajustarLongitudCampo(psCampo, 8, "D", "", "0");	//7 Transaction Sequence number  N(8)
					//System.out.println("rfc "+listGrid.get(i).getRfcBenef()+" tam "+listGrid.get(i).getRfcBenef().length());
					psRegistro += funciones.ajustarLongitudCampo(listGrid.get(i).getRfcBenef()!=null? listGrid.get(i).getRfcBenef() : "", 20, "I", "", "");		//8 Beneficiary tax id number N(20), en vb6 pega el rfc
					//					psRegistro += funciones.ajustarLongitudCampo("X", 20, "I", "", "");		//8 Beneficiary tax id number N(20), en vb6 pega el rfc
					psRegistro += "MXN";
					psRegistro +=funciones.ajustarLongitudCampo("0", 20, "I", "", "");//9 Customer account Currency Code(3), vb6 si toma dls
//					psCampo = funciones.ajustarLongitudCampo(listGrid.get(i).getNoCliente().toString(), 20, "I", "", "");
//					psRegistro += psCampo;													//10 Beneficiary Code A(20)
					psCampo = funciones.ponerFormatoCerosSindecimales((listGrid.get(i).getImporte() * 100), 15);
					System.out.println("importe485 "+listGrid.get(i).getImporte());
					//psCampo = funciones.ajustarLongitudCampo("" + (listGrid.get(i).getImporte() * 100), 15, "D", "", "0");
					psRegistro += psCampo;													//11 Transaction amount N(15)
				
					// "yyMMdd"
					if(layoutsDao.consultarConfiguraSet(1).equals("CCM"))
						psCampo = listGrid.get(i).getFecValorOriginal().toString();
					else
						psCampo = listGrid.get(i).getFecValor()+"";
					
					if(psCampo.substring(4, 5).equals("-"))
						psCampo = psCampo.substring(2, 4) + psCampo.substring(5, 7) + psCampo.substring(8, 10);
					else{
						psCampo =funciones.cambiarFechaGregoriana(psCampo);
						//psCampo = "17" + psCampo.substring(0, 2) + psCampo.substring(3, 5);
						psCampo = "17" + psCampo.substring(3,5) + psCampo.substring(0,2);
					}
					psRegistro += psCampo;//12 MAturity Date DATE(6)
					
					//	psRegistro += funciones.ajustarLongitudCampo("", 6, "D", "", "");		//12 MAturity Date DATE(6)
					psCampo = funciones.quitarCaracteresRaros(listGrid.get(i).getConcepto(), true);
					psRegistro += funciones.ajustarLongitudCampo(psCampo, 35, "I", "", "");	//13 Transaction Detail Line 1 A(35)
					psRegistro += funciones.ajustarLongitudCampo("", 35, "D", "", "");		//14 Transaction Detail Line 2 A(35), en vb6 pone aqui en nombre de empresa
					psRegistro += funciones.ajustarLongitudCampo("", 35, "D", "", "");		//15 Transaction Detail Line 3 A(35)
					psRegistro += funciones.ajustarLongitudCampo("", 35, "D", "", "");		//16 Transaction Detail Line 4 A(35)
					psRegistro += "05";														//17 Local Transaction Code N(2)
					psRegistro += "01";														//18 COstumer account type N(2)
					psCampo = funciones.quitarCaracteresRaros(listGrid.get(i).getBeneficiario(), true);
					psCampo = funciones.ocultarCoinversora(psCampo);
					psRegistro += funciones.ajustarLongitudCampo(psCampo, 80, "I", "", "");	//19 Beneficiary name A(80)
					psRegistro += funciones.ajustarLongitudCampo("XXX", 35, "I", "", "");		//20 Beneficiary Adress 1 A(35)
					//MS: se 
					psRegistro += funciones.ajustarLongitudCampo("XXX", 35, "I", "", "");		//21 Beneficiary Adress 1 second line A(35)
					psRegistro += funciones.ajustarLongitudCampo("", 15, "D", "", "");		//22 Beneficiary Adress 2 A(15) ciudad en vb6 aqui se pone el tema de cheque ocurre
					//					psRegistro += funciones.ajustarLongitudCampo("X", 35, "I", "", "");		//20 Beneficiary Adress 1 A(35)
//					//MS: se 
//					psRegistro += funciones.ajustarLongitudCampo("", 35, "D", "", "");		//21 Beneficiary Adress 1 second line A(35)
//					psRegistro += funciones.ajustarLongitudCampo("X", 15, "I", "", "");		//22 Beneficiary Adress 2 A(15) ciudad en vb6 aqui se pone el tema de cheque ocurre
					psRegistro += funciones.ajustarLongitudCampo("", 2, "D", "", "");		//23 Beneficiary state A(2)
					psRegistro += funciones.ajustarLongitudCampo("", 12, "D", "", "");	//24 Beneficiary adress 3 A(12) Cod. Postal
					//					psRegistro += funciones.ajustarLongitudCampo("1", 12, "D", "", "0");	//24 Beneficiary adress 3 A(12) Cod. Postal
					psRegistro += funciones.ajustarLongitudCampo("", 16, "D", "", "");		//25 Beneficiary phone number N(16)
					//MS: falta obtener el banco citi
//					psCampo = funciones.ajustarLongitudCampo(listGrid.get(i).getIdBancoCityStr(), 3, "D", "", "0");
					if (listGrid.get(i).getIdBanco() == listGrid.get(i).getIdBancoBenef()){
						psCampo ="000";
					}else{
						psCampo = funciones.ajustarLongitudCampo(""+listGrid.get(i).getIdBancoBenef(), 3, "D", "", "0");

					}
					psRegistro += psCampo;	
					psRegistro += funciones.ajustarLongitudCampo("", 8, "I", "", "");	//26 Beneficiary Bank number N(3)
//					psCampo = funciones.ajustarLongitudCampo("MEXICO", 8, "I", "", "");
//					psRegistro += psCampo;													//27 Beneficiary Bank Agency A(8)
//					psCampo = funciones.ajustarLongitudCampo(listGrid.get(i).getClabeBenef(), 35, "D", "", "0");
					psCampo = funciones.ajustarLongitudCampo(listGrid.get(i).getClabeBenef(), 35, "I", "", " ");
					psRegistro += psCampo;													//28 Beneficiary Account Number(35) SOLO INTERMEDIARIOS 
					psRegistro += "05";		
					//'30 Dirección del banco A(30)
					psRegistro+=funciones.ajustarLongitudCampo("", 30, "I", "", "");//29 Account type CR SOLO INTERBANCARIO N(2), en vb6 si revisa si hay clave o no
//					psCampo = funciones.ajustarLongitudCampo("MEXICO", 30, "I", "", "");	//'30 Dirección del banco A(30)
//					psRegistro+=psCampo;
					psRegistro += funciones.ajustarLongitudCampo("", 17, "D", "", "0");		//'31 Importe del impuesto N(17)
				//	psRegistro += funciones.ajustarLongitudCampo("", 17, "D", "", "");		//'31 Importe del impuesto N(17)
					psRegistro += funciones.ajustarLongitudCampo("", 1, "I", "", "");														// '32 Bandera de prioridad A(01) N= no se usa prioridad ATene16
					psRegistro +=  funciones.ajustarLongitudCampo("", 1, "I", "", "");	
					//psRegistro += "N";														// '32 Bandera de prioridad A(01) N= no se usa prioridad ATene16
//					psRegistro += "N";														// '33 Confidencial A(01) N= no se usa confidencial ATene16
					//psRegistro += psCampo;													//30 Beneficiary Bank Adress A(30)
					psRegistro += "01";														//'34 Fecha de acreditación al beneficiario N(02)
					
					psCampo = listGrid.get(i).getIdChequera();
					psRegistro += funciones.ajustarLongitudCampo(psCampo, 20, "I", "", "");//35 Número de cuenta de débito banamex N(20)
					//psRegistro += psCampo;//35 Número de cuenta de débito banamex N(20)
				//	System.out.println("numero de cuenta de debito banam tamaño "+psCampo.length());
					psRegistro += funciones.ajustarLongitudCampo("", 16, "D", "", " ");		//36 Beneficiary Fax Number N(16)
					psRegistro += funciones.ajustarLongitudCampo("", 20, "D", "", " ");		//37 Beneficiary Fax Contact Name A(20)
					psRegistro += funciones.ajustarLongitudCampo("", 15, "D", "", " ");		//38 Beneficiary Fax Department Name A(15)
					psRegistro += funciones.ajustarLongitudCampo("", 10, "D", "", "");		//39 Beneficiary Account number N(10) SOLO MISMO BANCO
			//		psRegistro += funciones.ajustarLongitudCampo("", 10, "D", "", "0");		//39 Beneficiary Account number N(10) SOLO MISMO BANCO
					psRegistro += funciones.ajustarLongitudCampo("", 2, "D", "", " ");		//40 Account Type CR N(2) SOLO MISMO BANCO
					psRegistro += "001";		//41 Método de entrega de transacciones N(03)
	//				psRegistro += funciones.ajustarLongitudCampo("", 3, "D", "", " ");		//41 Método de entrega de transacciones N(03)
					
					psRegistro += funciones.ajustarLongitudCampo("", 50, "D", "", " ");		//42 Collection Title ID A(50)
					psRegistro += funciones.ajustarLongitudCampo("", 5, "D", "", "0");		//43 Beneficiary Activity code N(5)
					psRegistro += funciones.ajustarLongitudCampo("", 50, "D", "", " ");		//44 Beneficiary e-mail adress A(50)
//					psCampo = funciones.ajustarLongitudCampo("" + (listGrid.get(i).getImporte() * 100), 15, "D", "", "0");
//					psCampo = funciones.ponerFormatoCerosSindecimales((listGrid.get(i).getImporte() * 100), 15);
					//psRegistro += psCampo;//45 Maximum payment amount N(15)
					psRegistro += "999999999999999";//45 Maximum payment amount N(15)
					psRegistro += funciones.ajustarLongitudCampo("", 1, "D", "", "");		//46 Update Type N(1)
					psRegistro += funciones.ajustarLongitudCampo("", 11, "I", "", " ");
//					psRegistro += funciones.ajustarLongitudCampo("NONE", 11, "I", "", " ");		//47 Check Number N(11)
					psRegistro += funciones.ajustarLongitudCampo("", 1, "D", "", "");		//48 Printed Check A(1)
					psRegistro += funciones.ajustarLongitudCampo("", 1, "D", "", "");		//49 Match Paid A(1)
					psRegistro += funciones.ajustarLongitudCampo("", 253, "D", "", "");		//50 Blank Characteres A(254)

				//}
				
								
			}catch(Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:beArmaCitibankPaylinkMN");
				e.printStackTrace();
				return "";
			}
			return psRegistro;
		}

	
	
	//laoutRevividor
	//Banamex CitibankPaylinkMN
	public String beArmaCitibankPaylinkMNRevive(List<MovimientoDto> listGrid, int i, String psReferenciaTraspaso, String cyti, String bancoB) {
		Map<String, Object> valorPagRef = new HashMap<String, Object>();
	    String psRegistro = "";
	    String psCampo = "";
		try {
			System.out.println("entra a ver el folio .:: angel");
			//Citibank PayLink Plus 1024 para Pago Mismo dia SOLO MXP
			psRegistro = "PAY";														//1 Record Type (02)
			psRegistro += "484";													//2 Costumer Country Code (03) - 484 = Mexico
			psCampo = listGrid.get(i).getIdChequera().substring(listGrid.get(i).getIdChequera().length() - 10);
			psRegistro += psCampo;													//3 Customer Account Number (10)
			
			// "yyMMdd"
			if(layoutsDao.consultarConfiguraSet(1).equals("CCM"))
				psCampo = listGrid.get(i).getFecValorOriginal().toString();
			else
				psCampo = listGrid.get(i).getFecValor().toString();
			psCampo = psCampo.substring(2, 4) + psCampo.substring(5, 7) + psCampo.substring(8, 10); 
			psRegistro += psCampo;													//4 Transaction Date (6)
			psRegistro += "071";													//5 Transaction Code (3)
			
			if(psReferenciaTraspaso.equals("")) {
				//Solo aplica para Transferencias, Pagos referenciados a Clientes
				//int x = Integer.parseInt()
						System.out.println("parametro1----"+listGrid.get(i).getNoCliente());
						System.out.println("parametro2----"+listGrid.get(i).getNoFolioDet());
				valorPagRef = layoutsDao.obtenerValorPagoReferenciado(Integer.parseInt(listGrid.get(i).getNoCliente()), listGrid.get(i).getNoFolioDet(), "");
            	psCampo = valorPagRef.get("valorPagRef").toString();
			}else
				psCampo = psReferenciaTraspaso;
			psRegistro += funciones.ajustarLongitudCampo(psCampo, 15, "I", "", "");	//6 Transaction Reference A(15)
			
			layoutsDao.actualizarFolioReal("folio_paylink");
			psCampo = "" + layoutsDao.seleccionarFolioReal("folio_paylink");
			if(Integer.parseInt(psCampo) < 0) 
				return "";
			
			psRegistro += funciones.ajustarLongitudCampo(psCampo, 8, "D", "", "0");	//7 Transaction Sequence number  N(8)
			psRegistro += funciones.ajustarLongitudCampo("X", 20, "I", "", "");		//8 Beneficiary tax id number N(20)
			psRegistro += "MXN";													//9 Customer account Currency Code(3)
			psCampo = funciones.ajustarLongitudCampo(listGrid.get(i).getNoCliente().toString(), 20, "I", "", "");
			psRegistro += psCampo;													//10 Beneficiary Code A(20)
			psCampo = funciones.ponerFormatoCerosSindecimales((listGrid.get(i).getImporte() * 100), 15);
			//psCampo = funciones.ajustarLongitudCampo("" + (listGrid.get(i).getImporte() * 100), 15, "D", "", "0");
			psRegistro += psCampo;													//11 Transaction amount N(15)
			psRegistro += funciones.ajustarLongitudCampo("", 6, "D", "", "");		//12 MAturity Date DATE(6)
			psCampo = funciones.quitarCaracteresRaros(listGrid.get(i).getConcepto(), true);
			psRegistro += funciones.ajustarLongitudCampo(psCampo, 35, "I", "", "");	//13 Transaction Detail Line 1 A(35)
			psRegistro += funciones.ajustarLongitudCampo("", 35, "D", "", "");		//14 Transaction Detail Line 2 A(35)
			psRegistro += funciones.ajustarLongitudCampo("", 35, "D", "", "");		//15 Transaction Detail Line 3 A(35)
			psRegistro += funciones.ajustarLongitudCampo("", 35, "D", "", "");		//16 Transaction Detail Line 4 A(35)
			psRegistro += "05";														//17 Local Transaction Code N(2)
			psRegistro += "01";														//18 COstumer account type N(2)
			psCampo = funciones.quitarCaracteresRaros(listGrid.get(i).getBeneficiario(), true);
			psCampo = funciones.ocultarCoinversora(psCampo);
			psRegistro += funciones.ajustarLongitudCampo(psCampo, 80, "I", "", "");	//19 Beneficiary name A(80)
			psRegistro += funciones.ajustarLongitudCampo("X", 35, "I", "", "");		//20 Beneficiary Adress 1 A(35)
			//MS: se 
			psRegistro += funciones.ajustarLongitudCampo("", 35, "D", "", "");		//21 Beneficiary Adress 1 second line A(35)
			psRegistro += funciones.ajustarLongitudCampo("X", 15, "I", "", "");		//22 Beneficiary Adress 2 A(15) ciudad
			psRegistro += funciones.ajustarLongitudCampo("", 2, "D", "", "");		//23 Beneficiary state A(2)
			psRegistro += funciones.ajustarLongitudCampo("1", 12, "D", "", "0");	//24 Beneficiary adress 3 A(12) Cod. Postal
			psRegistro += funciones.ajustarLongitudCampo("", 16, "D", "", "");		//25 Beneficiary phone number N(16)
			//MS: falta obtener el banco citi
			System.out.println("Bancocity_cadena::.."+listGrid.get(i).getIdBancoCityStr());
			
			psCampo = funciones.ajustarLongitudCampo(String.valueOf(listGrid.get(i).getIdBancoCity()), 3, "D", "", "0");
			psRegistro += psCampo;													//26 Beneficiary Bank number N(3)
			psCampo = funciones.ajustarLongitudCampo("MEXICO", 8, "I", "", "");
			psRegistro += psCampo;													//27 Beneficiary Bank Agency A(8)
			psCampo = funciones.ajustarLongitudCampo(listGrid.get(i).getClabeBenef(), 35, "D", "", "0");
			psRegistro += psCampo;													//28 Beneficiary Account Number(35) SOLO INTERMEDIARIOS
			psRegistro += "05";														//29 Account type CR SOLO INTERBANCARIO N(2)
			psCampo = funciones.ajustarLongitudCampo("MEXICO", 30, "I", "", "");
			psRegistro += psCampo;													//30 Beneficiary Bank Adress A(30)
			psRegistro += "01";														//31 Beneficiary Bank Entity N(2)
			psRegistro += "001";													//32 Beneficiary Bank Place Number N(3)
			psCampo = funciones.ajustarLongitudCampo("MEXICO", 14, "I", "", "");
			psRegistro += psCampo;													//33 Beneficiary Bank Place Name  A(14)
			psRegistro += "001";													//34 Beneficiary Bank Branch Number N(3)
			//MS falta obtener el nombre dle banco benef
			System.out.println("BancoBenef::.."+bancoB);
			psCampo = funciones.ajustarLongitudCampo(bancoB, 19, "I", "", "");
			psRegistro += psCampo;													//35 Beneficiary Bank Branch Name N(19)
			psRegistro += funciones.ajustarLongitudCampo("", 16, "I", "", " ");		//36 Beneficiary Fax Number N(16)
			psRegistro += funciones.ajustarLongitudCampo("", 20, "I", "", " ");		//37 Beneficiary Fax Contact Name A(20)
			psRegistro += funciones.ajustarLongitudCampo("", 15, "I", "", " ");		//38 Beneficiary Fax Department Name A(15)
			psRegistro += funciones.ajustarLongitudCampo("", 10, "I", "", "0");		//39 Beneficiary Account number N(10) SOLO MISMO BANCO
			psRegistro += funciones.ajustarLongitudCampo("", 2, "I", "", " ");		//40 Account Type CR N(2) SOLO MISMO BANCO
			psRegistro += "001";													//41 Transaction Deliver Method N(3) 001-->MEXICO
			psRegistro += funciones.ajustarLongitudCampo("", 50, "I", "", "");		//42 Collection Title ID A(50)
			psRegistro += funciones.ajustarLongitudCampo("", 5, "I", "", "");		//43 Beneficiary Activity code N(5)
			psRegistro += funciones.ajustarLongitudCampo("", 50, "I", "", "");		//44 Beneficiary e-mail adress A(50)
			psCampo = funciones.ajustarLongitudCampo("" + (listGrid.get(i).getImporte() * 100), 15, "D", "", "0");
			psCampo = funciones.ponerFormatoCerosSindecimales((listGrid.get(i).getImporte() * 100), 15);
			psRegistro += psCampo;													//45 Maximum payment amount N(15)
			psRegistro += funciones.ajustarLongitudCampo("", 1, "I", "", "");		//46 Update Type N(1)
			psRegistro += funciones.ajustarLongitudCampo("", 11, "I", "", "0");		//47 Check Number N(11)
			psRegistro += funciones.ajustarLongitudCampo("", 1, "I", "", "");		//48 Printed Check A(1)
			psRegistro += funciones.ajustarLongitudCampo("", 1, "I", "", "");		//49 Match Paid A(1)
			psRegistro += funciones.ajustarLongitudCampo("", 254, "I", "", "");
			psRegistro += funciones.ajustarLongitudCampo("", 100, "I", "", "");//100 caracteres para ajustar el TRL en la posicion sigiente
			System.out.println("Valor del revive::.."+psRegistro);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:beArmaCitibankPaylinkMN");
			e.printStackTrace();
			return "";
		}
		return psRegistro;
	}

	
	//Resvisando
	public Map<String, Object> beArmaHSBCH2HMB(List<MovimientoDto> listGrid, int i, int tEnvio, String nomArchivo, String sRuta,
			 									int idBanco, String pantalla) {
		Map<String, Object> result = new HashMap<String, Object>();
		int iDivisa = 0;
		String sCampo;
		String sReferencia;
		StringBuffer sRegistro = new StringBuffer();
		sRegistro.append("");
		//Layout Host To Host Bital Mismo Banco, Dispoersion Automatizada de Pagos, cada registro debe tener 175 caracteres 
		try {
			if(listGrid.get(i).getIdDivisa().equals("MN"))
				iDivisa = 1;
			else if(listGrid.get(i).getIdDivisa().equals("DLS"))
				iDivisa = 2;
			sReferencia = "" + listGrid.get(i).getNoFolioDet();
			sCampo = funciones.ajustarLongitudCampo(listGrid.get(i).getIdChequera(), 10, "R", "", "0");
			sRegistro.append(sCampo);									//1.- Cuenta N(10) 1-10
			sRegistro.append(iDivisa);									//2.- Moneda N(1)  11-11
			sRegistro.append("01");										//3.- Estatus N(2) 12-13 ; 1--> Nuevo
			sCampo = funciones.quitarCaracteresRaros(listGrid.get(i).getBeneficiario(), true);
			sCampo = funciones.ajustarLongitudCampo(sCampo, 40, "I", "", "");
			sRegistro.append(sCampo);									//4.- Beneficiario A(40) 14-53
			sCampo = funciones.ajustarLongitudCampo(validaFormato("" + listGrid.get(i).getImporte(), "999"), 15, "D", "", "0");
			sRegistro.append(sCampo);									//5.- Monto N(15) 54-68 ; sin punto decimal
			sCampo = funciones.ajustarLongitudCampo(sReferencia, 30, "I", "", "");
			sRegistro.append(sCampo);									//6.- Referencia A(30) 69-98
			sCampo = validaFormato(layoutsDao.fechaHoy(), "yyyyMMdd");
			sCampo = funciones.ajustarLongitudCampo(sCampo, 8, "D", "", "0");
			sRegistro.append(sCampo);									//7.- Fecha de Alta N(8) 99-106
			sRegistro.append(sCampo);									//8.- Fecha de envio N(8) 107-114
			sCampo = validaFormato("" + funciones.modificarFecha("m", 1, funciones.ponerFechaDate(layoutsDao.fechaHoy())), "yyyyMMdd");
			sCampo = funciones.ajustarLongitudCampo(sCampo, 8, "D", "", "0");
			sRegistro.append(sCampo);									//9.- Fecha de expiracion N(8) 115-122
			sCampo = funciones.ajustarLongitudCampo(listGrid.get(i).getDescUsuarioBital(), 8, "D", "", "0");
			sRegistro.append(sCampo);									//10.- Usuario A(8) 123-130 ; asignado por empresa
			sRegistro.append("08000");									//11.- Sucursal N(5) 131-135 ; 08000 constante
			sCampo = funciones.ajustarLongitudCampo("0", 14, "D", "", "0");
			sRegistro.append(sCampo);									//12.- Serial A(14) 136-149 ; rellenar con ceros
			sCampo = funciones.ajustarLongitudCampo("0", 8, "D", "", "0");
			sRegistro.append(sCampo);									//13.- Folio A(8) 150-157 ; rellenar con ceros
			sCampo = funciones.ajustarLongitudCampo(listGrid.get(i).getDescServicioBital(), 7, "D", "", "0");
			sRegistro.append(sCampo);//ps_id_Chequera_benef				//14.- Servicio A(7) 158-164 ; Asignado por empresa
			sCampo = funciones.ajustarLongitudCampo("0", 10, "I", "", "0");
			sRegistro.append(sCampo);									//15.- Cuenta de abono N(10) 165-174
			sRegistro.append("0");										//16.- Clave de abono A(1) 175-175 ; cero
			result.put("msgRegistro", sRegistro);
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:beArmaHSBCH2HMB");
			e.printStackTrace();
			result.put("msgUsuario", "Error al general el archivo H2H Bital Mismo Banco");
			return result;
		}
		return result;
	}
	
	public Map<String, Object> beArmaHSBCH2HMBAgrupado(List<MovimientoDto> listGrid, int i, int tEnvio, String nomArchivo, String sRuta,
				int idBanco, String pantalla) {
		Map<String, Object> result = new HashMap<String, Object>();
		int iDivisa = 0;
		String sCampo;
		String sReferencia;
		StringBuffer sRegistro = new StringBuffer();
		sRegistro.append("");
		//Layout Host To Host Bital Mismo Banco, Dispoersion Automatizada de Pagos, cada registro debe tener 175 caracteres 
		try {
			sReferencia = layoutsDao.obtenerReferenciaAgrupada(listGrid.get(i).getNoEmpresa(),
					listGrid.get(i).getIdBanco() , listGrid.get(i).getIdChequera(), listGrid.get(i).getPoHeaders());
			
			
			if(listGrid.get(i).getIdDivisa().equals("MN"))
				iDivisa = 1;
			else if(listGrid.get(i).getIdDivisa().equals("DLS"))
				iDivisa = 2;

			sCampo = funciones.ajustarLongitudCampo(listGrid.get(i).getIdChequera(), 10, "R", "", "0");
			sRegistro.append(sCampo);									//1.- Cuenta N(10) 1-10
			sRegistro.append(iDivisa);									//2.- Moneda N(1)  11-11
			sRegistro.append("01");										//3.- Estatus N(2) 12-13 ; 1--> Nuevo
			sCampo = funciones.quitarCaracteresRaros(listGrid.get(i).getBeneficiario(), true);
			sCampo = funciones.ajustarLongitudCampo(sCampo, 40, "I", "", "");
			sRegistro.append(sCampo);									//4.- Beneficiario A(40) 14-53
			sCampo = funciones.ajustarLongitudCampo(validaFormato("" + listGrid.get(i).getImporte(), "999"), 15, "D", "", "0");
			sRegistro.append(sCampo);									//5.- Monto N(15) 54-68 ; sin punto decimal
			sCampo = funciones.ajustarLongitudCampo(sReferencia, 30, "I", "", "");
			sRegistro.append(sCampo);									//6.- Referencia A(30) 69-98
			sCampo = validaFormato(layoutsDao.obtenerFechaHoyDMY(), "yyyyMMdd");
			sCampo = funciones.ajustarLongitudCampo(sCampo, 8, "D", "", "0");
			sRegistro.append(sCampo);									//7.- Fecha de Alta N(8) 99-106
			sRegistro.append(sCampo);									//8.- Fecha de envio N(8) 107-114
			sCampo = validaFormato(layoutsDao.obtenerFechaHoyDMYADD(), "yyyyMMdd");
			sCampo = funciones.ajustarLongitudCampo(sCampo, 8, "D", "", "0");
			sRegistro.append(sCampo);									//9.- Fecha de expiracion N(8) 115-122
			sCampo = funciones.ajustarLongitudCampo(listGrid.get(i).getDescUsuarioBital(), 8, "D", "", "0");
			sRegistro.append(sCampo);									//10.- Usuario A(8) 123-130 ; asignado por empresa
			sRegistro.append("08000");									//11.- Sucursal N(5) 131-135 ; 08000 constante
			sCampo = funciones.ajustarLongitudCampo("0", 14, "D", "", "0");
			sRegistro.append(sCampo);									//12.- Serial A(14) 136-149 ; rellenar con ceros
			sCampo = funciones.ajustarLongitudCampo("0", 8, "D", "", "0");
			sRegistro.append(sCampo);									//13.- Folio A(8) 150-157 ; rellenar con ceros
			sCampo = funciones.ajustarLongitudCampo(listGrid.get(i).getDescServicioBital(), 7, "D", "", "0");
			sRegistro.append(sCampo);//ps_id_Chequera_benef				//14.- Servicio A(7) 158-164 ; Asignado por empresa
			sCampo = funciones.ajustarLongitudCampo("0", 10, "I", "", "0");
			sRegistro.append(sCampo);									//15.- Cuenta de abono N(10) 165-174
			sRegistro.append("0");										//16.- Clave de abono A(1) 175-175 ; cero
			result.put("msgRegistro", sRegistro);
		
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:beArmaHSBCH2HMB");
			e.printStackTrace();
			result.put("msgUsuario", "Error al general el archivo H2H Bital Mismo Banco");
			return result;
		}
		return result;
	}
	
	public Map<String, Object> beArmaHSBCH2HIBAgrupado(List<MovimientoDto> listGrid, int i, int tEnvio, String nomArchivo, String sRuta,
			int idBanco, String pantalla) {
	Map<String, Object> result = new HashMap<String, Object>();
	List<MovimientoDto> resList = new ArrayList<MovimientoDto>();
	//Map<String, Object> map = new HashMap<String, Object>();
	//String res = "";
	String psFoliosIn = "";
	String pdSumatoria = "";
	String psReferenciaTraspaso = "";
	String psReferencia = "";
	String fechaManana = layoutsDao.obtenerFechaMananaDMY();
	
	int piSecuencia = 0;
	//int registros = 0;
	int piNoPagos = 1;
	int e = 0;
	//int resDetArchTrans = 0;
	int piRegistro = 1;
	
	try {
		StringBuffer sRegistro = new StringBuffer();
		//StringBuffer variable = new StringBuffer();
		StringBuffer  psArchGenerados = new StringBuffer(); 
		int plfolioLote = 0;
		String psfolioLote = "";
		String psCampo = "";
		sRegistro.append("01");   					 		// 1. Tipo de Registro N(2)  1-2
		psCampo = funciones.ajustarLongitudCampo("1", 7, "D", "", "0");
		sRegistro.append(psCampo); 						  	// 2. Numero de Secuencia N(7) 3-9
		sRegistro.append("60"); 						  	// 3. Codigo de operacinn N(2) 10-11  60 -->para envio, 0 --> para regreso
		sRegistro.append("021"); 						  	// 4. Banco participante N(3) 12-14 constante 021
		sRegistro.append("E"); 	   					  		// 5. Sentido A(1) 15-15 constante E  --> Entrada, S--> Salida
		sRegistro.append("2"); 	   					  		// 6. Servicio N(1) 16-16 constante 2  --> Transferencia Electronica de Fondos
		psCampo = validaFormato(layoutsDao.obtenerFechaHoyDMYADD(), "yyyyMMdd");
		layoutsDao.actualizarFolioReal("lote_bital");
		plfolioLote = layoutsDao.seleccionarFolioReal("lote_bital");
		
		if(plfolioLote < 0){
			result.put("msgVF", false);
			result.put("msgUsuario", "No se encontrn el folio <<lote_bital>>");
			return result;
		}
		psCampo = funciones.ajustarLongitudCampo(plfolioLote + "", 5, "D", "", "0");
		psfolioLote = psCampo;
		sRegistro.append(psCampo); 	   					  	// 7. Numero de Lote N(7) 17-23 DDNNNNNN DD--> Dia, NNNNN--> folio lote
		psCampo = validaFormato(layoutsDao.obtenerFechaHoyDMYADD(), "yyyyMMdd");
		sRegistro.append(psCampo); 	   					  	// 8. Fecha de presentacion N(8) 24-31 AAAAMMDD
		sRegistro.append("01"); 	   					  	// 9. Cndigo de divisa N(2) 32-33 Constante 01 --> MN, 05--> DLS
		sRegistro.append("00"); 	   					  	// 10. Causa de rechazo de lote N(2) 34-35 Constante 00
		sRegistro.append("2"); 	   					  		// 11. Modalidad N(1) 36-36 Constante 2 --> T + 1 , 1--> Today
		psCampo = funciones.ajustarLongitudCampo("", 40, "D", "", "");
		sRegistro.append(psCampo); 	   					  	// 12. Uso futuro CCE A(40) 37-76 espacios
		psCampo = funciones.ajustarLongitudCampo("", 406, "D", "", "");
		sRegistro.append(psCampo); 	   					  	// 13. Uso futuro Banca A(406) 77-482 espacios
		
		/*result = creacionArchivosBusiness.escribirArchivoLayout(sRuta, "", nomArchivo, sRegistro.toString());
		if(!result.get("msgUsuario").equals("")) {
			result.put("msgVF", false);
			result.put("msgUsuario", "Error al Escribir Registro de HSBC Host To Host Interbancario!!");
			return result;
		}
		*/
		result = creacionArchivosBusiness.escribirArchivoLayout(sRuta, "",
				nomArchivo, sRegistro.toString());
		if(!(Boolean)result.get("resExito")) {
			result.put("msgVF", false);
			result.put("msgUsuario", "Error al Escribir Registro de HSBC Host To Host Interbancario!!");
			return result;
		}
		
		//RENGLONES DE DETALLE
		//Buscar el detalle de la chequera encontrada en el encabezado
		if(psReferenciaTraspaso.equals("")){
			//resList = layoutsDao.detalleBitalEnvio(listGrid.get(i).getNoFolioDet() + "", "", 0, false);
			resList = layoutsDao.detalleBitalEnvioAgrupado(listGrid.get(i).getPoHeaders(), listGrid.get(i).getIdChequera(), 0, false);
		}else{
//			resList = layoutsDao.detalleBitalEnvio(listGrid.get(i).getNoFolioDet() + "", "", 0, false);
			resList = layoutsDao.detalleBitalEnvioAgrupado(listGrid.get(i).getPoHeaders(), listGrid.get(i).getIdChequera(), 0, false);
		}
		
		while(e < resList.size()) {
			sRegistro.delete(0, sRegistro.length());
			pdSumatoria = pdSumatoria + listGrid.get(i).getImporte();
			
			if (!psReferenciaTraspaso.equals("")){
				psReferencia = psReferenciaTraspaso;
			}else {
				//    Solo aplica para Transferencias, Pagos referenciados a Clientes
				//layoutsDao.obtenerValorPagoReferenciado(Integer.parseInt(listGrid.get(i).getNoCliente()),
				  //    listGrid.get(i).getNoFolioDet(),"");
				//String referencia = layoutsDao.obtenerReferenciaAgrupada(listGrid.get(i).getNoEmpresa(), 21,
					//	listGrid.get(i).getIdChequera(), listGrid.get(i).getPoHeaders());
			}
			sRegistro.append("02");          // 14. Tipo de registro N(2) 1-2 constante 02 -->Detalle
			piSecuencia++;
			psCampo = piSecuencia+"";
			psCampo = funciones.ajustarLongitudCampo(psCampo, 7, "D", "", "0");
			sRegistro.append(psCampo);          //15. Nnmero de secuencia N(7) 3-9
			psCampo = "60";
			sRegistro.append(psCampo);          //16. Codigo de operacion N(2) 10-11, constante 60 -->Instruccion abono
			psCampo = "01";
			sRegistro.append(psCampo);          //17. Codigo de divisas N(2) 12-13, constante 01 -->pesos
			psCampo = fechaManana;
			sRegistro.append(psCampo);		//   '18. Fecha de transferencia N(8) 14-21 AAAAMMDD
			psCampo = "021";
			sRegistro.append(psCampo);       //  '19. Banco Presentador N(3) 22-24 constante 021-->Bital
			psCampo = listGrid.get(i).getIdBancoBenef() + "";
			psCampo = funciones.ajustarLongitudCampo(psCampo, 3, "D", "", "0");
			sRegistro.append(psCampo);        //  '20. Banco Receptor N(3) 25-27
			psCampo = "" + (listGrid.get(i).getImporte() * 100);
			psCampo = funciones.ajustarLongitudCampo(psCampo, 15, "D", "", "0");
			sRegistro.append(psCampo);       //    '21. Importe de la operacion N(15) 28-42 Sin punto decimal
			psCampo = funciones.ajustarLongitudCampo(psCampo, 16, "", "", "");
			sRegistro.append(psCampo);         //  '22. Uso futuro CCE A(16) 43-58 espacios
			psCampo = "01";
			sRegistro.append(psCampo);        //   '23. Tipo de operacion N(2) 59-60 constante 01-->Transferencia de abono
			psCampo = fechaManana;
			sRegistro.append(psCampo);        //   '24. Fecha de aplicacion N(8) 61-68 AAAAMMDD
			//'psCampo = "40"  Esta comentado desde visual vasic
			psCampo = "01";
			sRegistro.append(psCampo);   //   '25. Tipo de cuenta originador N(2) 69-70 constante 40 --> CLABE, 01--> cuenta normal
			//psCampo = listGrid.get(i).getPsClabe() + "";  por default comentada esta linea
			psCampo = listGrid.get(i).getIdChequera() + "";
			psCampo = funciones.ajustarLongitudCampo(psCampo, 20, "D", "", "0");
			sRegistro.append(psCampo);       //   '26. Numero de cuenta del originador N(20)71-90
			psCampo = listGrid.get(i).getNomEmpresa() + "";
			psCampo = funciones.quitarCaracteresRaros(psCampo,true);
			psCampo = funciones.ajustarLongitudCampo(psCampo, 4, "I", "", "");
			sRegistro.append(psCampo);        //  '27. Nombre del originador A(40)91-130
			psCampo = listGrid.get(i).getRfc() + "";
			psCampo = funciones.ajustarLongitudCampo(psCampo, 18, "I", "", "");
			sRegistro.append(psCampo);       //   '28. RFC o CURP del originador A(18) 131-148
			psCampo = "40";
			sRegistro.append(psCampo);       //   '29. Tipo de cuenta del receptor N(2) 149-150 constante 40 --> CLABE, 01--> cheques
			psCampo = listGrid.get(i).getRfc() + "";
			psCampo = funciones.ajustarLongitudCampo(psCampo, 20, "D", "", "0");
			sRegistro.append(psCampo);       //   '30. Numero de cuenta del receptor N(20) 151-170
			psCampo = funciones.quitarCaracteresRaros(listGrid.get(i).getBeneficiario() + "", true).trim().replace(" ", "");
			psCampo = funciones.ajustarLongitudCampo(psCampo, 40, "I", "", "");
			sRegistro.append(psCampo);       //   '31. Nombre del receptor A(40) 171-210
			psCampo = funciones.ajustarLongitudCampo("", 18, "I", "", "");
			sRegistro.append(psCampo);       //   '32. RFC o CURP del receptor A(18) 211-228 Opcional
			psCampo = funciones.ajustarLongitudCampo(psCampo, 40, "", "", "");
			sRegistro.append(psCampo);       //   '33. Referencia del servicio con el emisor A(40) 229-268 Espacios
			psCampo = funciones.ajustarLongitudCampo(psCampo, 40, "", "", "");
			sRegistro.append(psCampo);       //   '34. Nombre del titular del Servicio A(40) 269-308 espacios
			sRegistro.append(funciones.ajustarLongitudCampo("", 15, "I", "", "0"));       //   '35. Importe del IVA de operacinn N(15) 309-323 opcional
			psCampo = psfolioLote;
			psCampo = funciones.ajustarLongitudCampo(psCampo, 7, "D", "", "0");
			sRegistro.append(psCampo);       //   '36. Referencia numerica del originador N(7) 324-330  se especifica el mismo que el campo 7
			psCampo = listGrid.get(i).getPoHeaders() + "";
			psCampo = funciones.quitarCaracteresRaros(psCampo,true);
			psCampo = funciones.ajustarLongitudCampo(psCampo, 25, "I", "", "");// + Space(15)
			sRegistro.append(psCampo);       //   '37. Referencia leyenda del originador A(40) 331-370
			psCampo = funciones.ajustarLongitudCampo(psCampo, 30, "", "", "");
			sRegistro.append(psCampo);       //   '38. Clave de rastreo A(30) 371-400   Espacios
			psCampo = "00";
			sRegistro.append(psCampo);       //   '39. Motivo de devolucinn A(2) 401-402   constante 00
			psCampo = fechaManana;
			sRegistro.append(psCampo);       //   '40. Fecha Presentacion Inicial N(8) 403-410 AAAAMMDD
			psCampo = funciones.ajustarLongitudCampo(psCampo, 12, "", "", "");
			sRegistro.append(psCampo);       //   '41. Uso futuro Banco A(12) 411-422 espacios
			psCampo = psReferencia+"";
			psCampo = funciones.ajustarLongitudCampo(psCampo, 30, "I", "", "");
			sRegistro.append(psCampo);       //   '42. Referencia cliente A(30) 423-452
			psCampo = funciones.quitarCaracteresRaros(listGrid.get(i).getObservacion()!= null 
					?listGrid.get(i).getObservacion(): "", true);
			psCampo = funciones.ajustarLongitudCampo(psCampo, 30, "I", "", "");
			sRegistro.append(psCampo);       //   '43. Descripcion referencia de pago A(30) 453-482
			
			creacionArchivosBusiness.escribirArchivoLayout(sRuta, "", nomArchivo, sRegistro.toString());
			
			
			DetArchTranferAgrup dtoInsertA = new DetArchTranferAgrup();
			
				 
    		dtoInsertA.setNomArch(nomArchivo.substring(0, 8));
    		dtoInsertA.setPoHeaders(listGrid.get(i).getPoHeaders());
    		dtoInsertA.setFecPropuesta(listGrid.get(i).getFecValor());
    		dtoInsertA.setIdBanco(listGrid.get(i).getIdBanco());
    		dtoInsertA.setIdEstatusArch("T");
    		dtoInsertA.setIdChequeraBenef(listGrid.get(i).getIdChequeraBenef());
    		dtoInsertA.setImporte(listGrid.get(i).getImporte());
    		dtoInsertA.setBeneficiario(listGrid.get(i).getBeneficiario());
    		dtoInsertA.setSucursal(Integer.parseInt(listGrid.get(i).getSucDestino()));
    		dtoInsertA.setPlaza(Integer.parseInt(listGrid.get(i).getPlazaBenef()));
    		dtoInsertA.setNoDocto(listGrid.get(i).getNoDocto());
    		dtoInsertA.setIdChequera(listGrid.get(i).getIdChequera());
    		dtoInsertA.setIdBancoBenef(listGrid.get(i).getIdBancoBenef());
    		dtoInsertA.setPrefijoBenef(listGrid.get(i).getInstFinan() != null ? listGrid.get(i).getInstFinan():"");
    		dtoInsertA.setConcepto(listGrid.get(i).getConcepto());
    		dtoInsertA.setNoEmpresa(listGrid.get(i).getNoEmpresa());
    		
    		int afec = envioTransferenciasADao.insertarDetArchTransferAgrup(dtoInsertA);
    		
    		if(afec > 0) 
    			psFoliosIn += listGrid.get(i).getPoHeaders() + ",";
            
			
    		e++;
		}
		StringBuffer pie = new StringBuffer();
		//sRegistro = "";
		psCampo = "09";
		pie.append(psCampo);          //'44. Tipo de Registro N(2) 1-2 constante 09-->Sumario por lote
		piSecuencia = piSecuencia++;
		psCampo = piSecuencia + "";
		psCampo = funciones.ajustarLongitudCampo(psCampo, 7, "D", "", "0");
		pie.append(psCampo);          //'45. Numero de Secuencia N(7) 3-9
		psCampo = "60";
		pie.append(psCampo);          //'46. Cndigo de operacinn  N(2) 10-11
		psCampo = psfolioLote;
		psCampo = funciones.ajustarLongitudCampo(psCampo, 7, "D", "", "0");
		pie.append(psCampo);          //'47. Numero de lote N(7) 12-18 el mismo que el header
		psCampo = piNoPagos + "";
		psCampo = funciones.ajustarLongitudCampo(psCampo, 7, "D", "", "0");
		pie.append(psCampo);          //'48. Numero de operaciones N(7) 19-25
		//psCampo = Format(pdSumatoria * 100, "");
		psCampo = funciones.ajustarLongitudCampo(psCampo, 18, "D", "", "0");
		pie.append(psCampo);          //'49. Importe total de operaciones N(18) 26-43
		psCampo = funciones.ajustarLongitudCampo("", 40, "", "", "");
		pie.append(psCampo);          //'50. Usos futuro CCE A(40) 44-83
		psCampo = funciones.ajustarLongitudCampo("", 399, "", "", "");
		pie.append(psCampo);          //'51. Uso futuro banca A(399) 84-482
		
		if (psArchGenerados.equals("")) {
			psArchGenerados.append(nomArchivo.substring(0, 8));
		} else {
			psArchGenerados.append("','"+nomArchivo.substring(0, 8)) ;
		}
		
		creacionArchivosBusiness.escribirArchivoLayout(sRuta, "", nomArchivo, pie.toString());
		if(result.get("msgVF") == null) {
			if(!psFoliosIn.equals("")) {
				layoutsDao.insertArchTransfer(nomArchivo.substring(0, 8), 21,funciones.cambiarFecha(layoutsDao.fechaHoy(), true),
						Double.parseDouble(pdSumatoria), piRegistro);	
				if(envioTransferenciasADao.actulizaMovimientosTefAgrupados( psArchGenerados.toString(), "HSB") <= 0) {
					result.put("msgUsuario", "Error al Actualizar estatus de los movimientos de Transferencias es necesario volver a generar el archivo");
					result.put("msgVF", false);
					return result;
				}
				result.put("msgUsuario", "Los Registros han sido Guardados en el archivo " + nomArchivo);
			}
			result.put("estatusAv", true);
			result.put("nombreArchivo", nomArchivo);
		}
		
		//psArchGenerados.append(nomArchivo+",");
		
		//layoutsDao.insertArchTransfer(nomArchivo.substring(0, 8), 21,funciones.cambiarFecha(layoutsDao.fechaHoy(), true),
			//					Double.parseDouble(pdSumatoria), piRegistro);	
	}catch(Exception er) {
		bitacora.insertarRegistro(new Date().toString() + " " + er.toString() + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:beArmaHSBCH2HIB");
		er.printStackTrace();
		result.put("msgUsuario", "Error al general el archivo H2H Bital InterBancario");
		return result;
	}
	return result;
}

	
	public Map<String, Object> beArmaHSBCH2HIB(List<MovimientoDto> listGrid, int i, int tEnvio, String nomArchivo, String sRuta,
				int idBanco, String pantalla) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<MovimientoDto> resList = new ArrayList<MovimientoDto>();
		//Map<String, Object> map = new HashMap<String, Object>();
		//String res = "";
		String pdSumatoria = "";
		String psReferenciaTraspaso = "";
		String psReferencia = "";
		//String fechaManana = funciones.cambiarFecha(consultasGrales.obtenerFechaManana().toString(), true);
		String fechaManana = layoutsDao.obtenerFechaMananaDMY();
		int piSecuencia = 0;
		//int registros = 0;
		int piNoPagos = 1;
		int e = 0;
		//int resDetArchTrans = 0;
		int piRegistro = 1;
		
		try {
			StringBuffer sRegistro = new StringBuffer();
			//StringBuffer variable = new StringBuffer();
			StringBuffer  psArchGenerados = new StringBuffer(); 
			int plfolioLote = 0;
			String psfolioLote = "";
			String psCampo = "";
			sRegistro.append("01");   					 		// 1. Tipo de Registro N(2)  1-2
			psCampo = funciones.ajustarLongitudCampo("1", 7, "D", "", "0");
			sRegistro.append(psCampo); 						  	// 2. Numero de Secuencia N(7) 3-9
			sRegistro.append("60"); 						  	// 3. Codigo de operacinn N(2) 10-11  60 -->para envio, 0 --> para regreso
			sRegistro.append("021"); 						  	// 4. Banco participante N(3) 12-14 constante 021
			sRegistro.append("E"); 	   					  		// 5. Sentido A(1) 15-15 constante E  --> Entrada, S--> Salida
			sRegistro.append("2"); 	   					  		// 6. Servicio N(1) 16-16 constante 2  --> Transferencia Electronica de Fondos
			psCampo = validaFormato(layoutsDao.fechaHoy(), "yyyyMMdd");
			layoutsDao.actualizarFolioReal("lote_bital");
			plfolioLote = layoutsDao.seleccionarFolioReal("lote_bital");
			
			if(plfolioLote < 0){
				result.put("msgVF", false);
				result.put("msgUsuario", "No se encontrn el folio <<lote_bital>>");
				return result;
			}
			psCampo = funciones.ajustarLongitudCampo(plfolioLote + "", 5, "D", "", "0");
			psfolioLote = psCampo;
			sRegistro.append(psCampo); 	   					  	// 7. Numero de Lote N(7) 17-23 DDNNNNNN DD--> Dia, NNNNN--> folio lote
			psCampo = validaFormato(layoutsDao.fechaHoy(), "yyyyMMdd");
			sRegistro.append(psCampo); 	   					  	// 8. Fecha de presentacion N(8) 24-31 AAAAMMDD
			sRegistro.append("01"); 	   					  	// 9. Cndigo de divisa N(2) 32-33 Constante 01 --> MN, 05--> DLS
			sRegistro.append("00"); 	   					  	// 10. Causa de rechazo de lote N(2) 34-35 Constante 00
			sRegistro.append("2"); 	   					  		// 11. Modalidad N(1) 36-36 Constante 2 --> T + 1 , 1--> Today
			psCampo = funciones.ajustarLongitudCampo("", 40, "D", "", "");
			sRegistro.append(psCampo); 	   					  	// 12. Uso futuro CCE A(40) 37-76 espacios
			psCampo = funciones.ajustarLongitudCampo("", 406, "D", "", "");
			sRegistro.append(psCampo); 	   					  	// 13. Uso futuro Banca A(406) 77-482 espacios
			
			result = creacionArchivosBusiness.escribirArchivoLayout(sRuta, "", nomArchivo, sRegistro.toString());
			if(!result.get("msgUsuario").equals("")) {
				result.put("msgVF", false);
				result.put("msgUsuario", "Error al Escribir Registro de HSBC Host To Host Interbancario!!");
				return result;
			}
			
			
			//RENGLONES DE DETALLE
			//Buscar el detalle de la chequera encontrada en el encabezado
			if(psReferenciaTraspaso.equals("")){
				resList = layoutsDao.detalleBitalEnvio(listGrid.get(i).getNoFolioDet() + "", "", 0, false);
			}else{
				resList = layoutsDao.detalleBitalEnvio(listGrid.get(i).getNoFolioDet() + "", "", 0, false);
			}
			
			while(e < resList.size()) {
				sRegistro.delete(0, sRegistro.length());
				pdSumatoria = pdSumatoria + listGrid.get(i).getImporte();
				
				if (!psReferenciaTraspaso.equals("")){
					psReferencia = psReferenciaTraspaso;
				}else {
					//    Solo aplica para Transferencias, Pagos referenciados a Clientes
					layoutsDao.obtenerValorPagoReferenciado(Integer.parseInt(listGrid.get(i).getNoCliente()),
					      listGrid.get(i).getNoFolioDet(),"");
				}
				sRegistro.append("02");          // 14. Tipo de registro N(2) 1-2 constante 02 -->Detalle
				piSecuencia++;
				psCampo = piSecuencia+"";
				psCampo = funciones.ajustarLongitudCampo(psCampo, 7, "D", "", "0");
				sRegistro.append(psCampo);          //15. Nnmero de secuencia N(7) 3-9
				psCampo = "60";
				sRegistro.append(psCampo);          //16. Codigo de operacion N(2) 10-11, constante 60 -->Instruccion abono
				psCampo = "01";
				sRegistro.append(psCampo);          //17. Codigo de divisas N(2) 12-13, constante 01 -->pesos
				psCampo = fechaManana;
				sRegistro.append(psCampo);		//   '18. Fecha de transferencia N(8) 14-21 AAAAMMDD
				psCampo = "021";
				sRegistro.append(psCampo);       //  '19. Banco Presentador N(3) 22-24 constante 021-->Bital
				psCampo = listGrid.get(i).getIdBancoBenef() + "";
				psCampo = funciones.ajustarLongitudCampo(psCampo, 3, "D", "", "0");
				sRegistro.append(psCampo);        //  '20. Banco Receptor N(3) 25-27
				psCampo = "" + (listGrid.get(i).getImporte() * 100);
				psCampo = funciones.ajustarLongitudCampo(psCampo, 15, "D", "", "0");
				sRegistro.append(psCampo);       //    '21. Importe de la operacion N(15) 28-42 Sin punto decimal
				psCampo = funciones.ajustarLongitudCampo(psCampo, 16, "", "", "");
				sRegistro.append(psCampo);         //  '22. Uso futuro CCE A(16) 43-58 espacios
				psCampo = "01";
				sRegistro.append(psCampo);        //   '23. Tipo de operacion N(2) 59-60 constante 01-->Transferencia de abono
				psCampo = fechaManana;
				sRegistro.append(psCampo);        //   '24. Fecha de aplicacion N(8) 61-68 AAAAMMDD
				//'psCampo = "40"  Esta comentado desde visual vasic
				psCampo = "01";
				sRegistro.append(psCampo);   //   '25. Tipo de cuenta originador N(2) 69-70 constante 40 --> CLABE, 01--> cuenta normal
				//psCampo = listGrid.get(i).getPsClabe() + "";  por default comentada esta linea
				psCampo = listGrid.get(i).getIdChequera() + "";
				psCampo = funciones.ajustarLongitudCampo(psCampo, 20, "D", "", "0");
				sRegistro.append(psCampo);       //   '26. Numero de cuenta del originador N(20)71-90
				psCampo = listGrid.get(i).getNomEmpresa() + "";
				psCampo = funciones.quitarCaracteresRaros(psCampo,true);
				psCampo = funciones.ajustarLongitudCampo(psCampo, 4, "I", "", "");
				sRegistro.append(psCampo);        //  '27. Nombre del originador A(40)91-130
				psCampo = listGrid.get(i).getRfc() + "";
				psCampo = funciones.ajustarLongitudCampo(psCampo, 18, "I", "", "");
				sRegistro.append(psCampo);       //   '28. RFC o CURP del originador A(18) 131-148
				psCampo = "40";
				sRegistro.append(psCampo);       //   '29. Tipo de cuenta del receptor N(2) 149-150 constante 40 --> CLABE, 01--> cheques
				psCampo = listGrid.get(i).getRfc() + "";
				psCampo = funciones.ajustarLongitudCampo(psCampo, 20, "D", "", "0");
				sRegistro.append(psCampo);       //   '30. Numero de cuenta del receptor N(20) 151-170
				psCampo = funciones.quitarCaracteresRaros(listGrid.get(i).getBeneficiario() + "", true).trim().replace(" ", "");
				psCampo = funciones.ajustarLongitudCampo(psCampo, 40, "I", "", "");
				sRegistro.append(psCampo);       //   '31. Nombre del receptor A(40) 171-210
				psCampo = funciones.ajustarLongitudCampo("", 18, "I", "", "");
				sRegistro.append(psCampo);       //   '32. RFC o CURP del receptor A(18) 211-228 Opcional
				psCampo = funciones.ajustarLongitudCampo(psCampo, 40, "", "", "");
				sRegistro.append(psCampo);       //   '33. Referencia del servicio con el emisor A(40) 229-268 Espacios
				psCampo = funciones.ajustarLongitudCampo(psCampo, 40, "", "", "");
				sRegistro.append(psCampo);       //   '34. Nombre del titular del Servicio A(40) 269-308 espacios
				sRegistro.append(funciones.ajustarLongitudCampo("", 15, "I", "", "0"));       //   '35. Importe del IVA de operacinn N(15) 309-323 opcional
				psCampo = psfolioLote;
				psCampo = funciones.ajustarLongitudCampo(psCampo, 7, "D", "", "0");
				sRegistro.append(psCampo);       //   '36. Referencia numerica del originador N(7) 324-330  se especifica el mismo que el campo 7
				psCampo = listGrid.get(i).getConcepto() + "";
				psCampo = funciones.quitarCaracteresRaros(psCampo,true);
				psCampo = funciones.ajustarLongitudCampo(psCampo, 25, "I", "", "");// + Space(15)
				sRegistro.append(psCampo);       //   '37. Referencia leyenda del originador A(40) 331-370
				psCampo = funciones.ajustarLongitudCampo(psCampo, 30, "", "", "");
				sRegistro.append(psCampo);       //   '38. Clave de rastreo A(30) 371-400   Espacios
				psCampo = "00";
				sRegistro.append(psCampo);       //   '39. Motivo de devolucinn A(2) 401-402   constante 00
				psCampo = fechaManana;
				sRegistro.append(psCampo);       //   '40. Fecha Presentacion Inicial N(8) 403-410 AAAAMMDD
				psCampo = funciones.ajustarLongitudCampo(psCampo, 12, "", "", "");
				sRegistro.append(psCampo);       //   '41. Uso futuro Banco A(12) 411-422 espacios
				psCampo = psReferencia+"";
				psCampo = funciones.ajustarLongitudCampo(psCampo, 30, "I", "", "");
				sRegistro.append(psCampo);       //   '42. Referencia cliente A(30) 423-452
				psCampo = funciones.quitarCaracteresRaros(listGrid.get(i).getObservacion() + "", true);
				psCampo = funciones.ajustarLongitudCampo(psCampo, 30, "I", "", "");
				sRegistro.append(psCampo);       //   '43. Descripcion referencia de pago A(30) 453-482
				
				creacionArchivosBusiness.escribirArchivoLayout(sRuta, "", nomArchivo, sRegistro.toString());
				
				
				
				DetArchTransferDto dtoInsert = new DetArchTransferDto();
				
				dtoInsert.setNomArch(nomArchivo.substring(0, 8));
				dtoInsert.setNoFolioDet(listGrid.get(i).getNoFolioDet());
				dtoInsert.setNoDocto(listGrid.get(i).getNoDocto());
				dtoInsert.setFecValorDate(listGrid.get(i).getFecValor());
				dtoInsert.setIdBanco(listGrid.get(i).getIdBanco());
				dtoInsert.setIdChequera(listGrid.get(i).getIdChequera());
				dtoInsert.setIdBancoBenef(listGrid.get(i).getIdBancoBenef());
				dtoInsert.setIdChequeraBenef(listGrid.get(i).getIdChequeraBenef());
				dtoInsert.setIdEstatusArch("T");
				dtoInsert.setImporte(listGrid.get(i).getImporte());
				dtoInsert.setBeneficiario(funciones.quitarCaracteresRaros(listGrid.get(i).getBeneficiario(), true));
				dtoInsert.setSucursal(Integer.parseInt(listGrid.get(i).getSucDestino()));
				dtoInsert.setPlaza(Integer.parseInt(listGrid.get(i).getPlazaBenef()));
				dtoInsert.setConcepto(listGrid.get(i).getConcepto());
				dtoInsert.setNoDocto(listGrid.get(i).getNoDocto());
	    		dtoInsert.setPrefijoBenef(listGrid.get(i).getInstFinan());
	    		
	    		layoutsDao.insertDetArchTransfer(dtoInsert);
	    		e++;
			}
			//sRegistro = "";
			psCampo = "09";
			sRegistro.append(psCampo);          //'44. Tipo de Registro N(2) 1-2 constante 09-->Sumario por lote
			piSecuencia = piSecuencia++;
			psCampo = piSecuencia + "";
			psCampo = funciones.ajustarLongitudCampo(psCampo, 7, "D", "", "0");
			sRegistro.append(psCampo);          //'45. Numero de Secuencia N(7) 3-9
			psCampo = "60";
			sRegistro.append(psCampo);          //'46. Cndigo de operacinn  N(2) 10-11
			psCampo = psfolioLote;
			psCampo = funciones.ajustarLongitudCampo(psCampo, 7, "D", "", "0");
			sRegistro.append(psCampo);          //'47. Numero de lote N(7) 12-18 el mismo que el header
			psCampo = piNoPagos + "";
			psCampo = funciones.ajustarLongitudCampo(psCampo, 7, "D", "", "0");
			sRegistro.append(psCampo);          //'48. Numero de operaciones N(7) 19-25
			//psCampo = Format(pdSumatoria * 100, "");
			psCampo = funciones.ajustarLongitudCampo(psCampo, 18, "D", "", "0");
			sRegistro.append(psCampo);          //'49. Importe total de operaciones N(18) 26-43
			psCampo = funciones.ajustarLongitudCampo("", 40, "", "", "");
			sRegistro.append(psCampo);          //'50. Usos futuro CCE A(40) 44-83
			psCampo = funciones.ajustarLongitudCampo("", 399, "", "", "");
			sRegistro.append(psCampo);          //'51. Uso futuro banca A(399) 84-482
			
			creacionArchivosBusiness.escribirArchivoLayout(sRuta, "", nomArchivo, sRegistro.toString());
			
			
			psArchGenerados.append(nomArchivo+",");
			
			layoutsDao.insertArchTransfer(nomArchivo.substring(0, 8), 21,funciones.cambiarFecha(layoutsDao.fechaHoy(), true),
									Double.parseDouble(pdSumatoria), piRegistro);	
		}catch(Exception er) {
			bitacora.insertarRegistro(new Date().toString() + " " + er.toString() + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:beArmaHSBCH2HIB");
			er.printStackTrace();
			result.put("msgUsuario", "Error al general el archivo H2H Bital InterBancario");
			return result;
		}
		return result;
	}
	
	public String beArmaConvenioCIE(ParametrosLayoutDto dto) {
		String sCampo;
		StringBuffer sRegistro = new StringBuffer();
		//Map<String, Object> valorPagRef = new HashMap<String, Object>();
		
		try {
			//CIE --> Concentracion Inmediata Empresarial
		    //Solo aplica para Transferencias mismo banco
			
			//REGISTRO DE TRANSACCION INTERBANCARIA
			//String referencia = layoutsDao.obtieneReferenciaMovimiento(dto.getNoFolioDet()+"");
			sCampo = funciones.quitarCaracteresRaros(dto.getConcepto(), true);
			sRegistro.append(funciones.ajustarLongitudCampo(sCampo, 30, "I", "", ""));					//1. Concepto CIE A(30)
			
			sCampo = dto.getIdChequeraBenef().substring(dto.getIdChequeraBenef().length() - 7);	//El convenio viene dentro de la chequera benef
			sRegistro.append(funciones.ajustarLongitudCampo(sCampo, 7, "D", "", "0"));					//2. No Convenio N(7)
			//Aqui determinar cuentas azules y verdes
			if(dto.getIdChequera().length() == 13)
				sRegistro.append(funciones.ajustarLongitudCampo(dto.getIdChequera().substring(3,dto.getIdChequera().length()), 18, "D", "", "0"));	//3.- Cuenta Origen (NU) 18
			else
				sRegistro.append(funciones.ajustarLongitudCampo(dto.getIdChequera(), 18, "D", "", "0"));	//3.- Cuenta Origen (NU) 18
			
			
			
			sCampo = funciones.ponerFormatoCeros(dto.getImporte(), 13);
			sRegistro.append(funciones.ajustarLongitudCampo(sCampo, 16, "D", "", "0"));					//4. Monto MO(16)
			
        	/*valorPagRef = layoutsDao.obtenerValorPagoReferenciado(dto.getNoCliente(), dto.getNoFolioDet(), "");
        	sCampo = valorPagRef.get("valorPagRef").toString();
			if(valorPagRef.get("psRef2").toString().equals("")) 
				sCampo = dto.getNoDocto();
        	sRegistro.append(funciones.ajustarLongitudCampo(sCampo, 30, "I", "", ""));					//5. Referencia A(30)
        	*/
			//if (referencia != "" && dto.getPsReferenciaTraspaso() != null) {
				//sRegistro.append(funciones.ajustarLongitudCampo(referencia.substring(20, referencia.length()), 30, "I", "", ""));					//5. Referencia A(30)

			sCampo = obtenerReferencias(dto.getNoCliente(), dto.getNoFolioDet()).get("refN").toString();
			sRegistro.append(funciones.ajustarLongitudCampo(sCampo, 30, "I", "", " "));	
			//}else{
			sCampo = obtenerReferencias(dto.getNoCliente(), dto.getNoFolioDet()).get("refA").toString();
			sRegistro.append(funciones.ajustarLongitudCampo(sCampo, 20, "I", "", " "));				//5. Referencia A(30)
			//}
        	/* Modificainn solicitada por Julian Becerra, se pone como referencia el numero de la clabe bancaria del beneficiario, del contrato CIE
        	 * if(!valorPagRef.get("psRef2").toString().equals(""))
        		sCampo = valorPagRef.get("psRef2").toString();
        	else
        		sCampo = valorPagRef.get("valorPagRef").toString();
        	*/
        	
        		//sRegistro.append(funciones.ajustarLongitudCampo(dto.getPsClabe(), 20, "D", "", " "));		//6. Referencia CIE A(20)
    		//sRegistro.append(funciones.ajustarLongitudCampo(referencia, 20, "I", "", "\u0020"));		//6. Referencia CIE A(20)
			//sRegistro.append(funciones.ajustarLongitudCampo(referencia, 20, "D", "", " "));		//6. Referencia CIE A(20)
        	
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:beArmaConvenioCIE");
			e.printStackTrace();
		}
		System.out.println(sRegistro.toString());
		return sRegistro.toString();
	}
	

	public String beArmaConvenioCIEAgrupado(ParametrosLayoutDto dto) {
		String sCampo;
		StringBuffer sRegistro = new StringBuffer();
		try {
			//CIE --> Concentracion Inmediata Empresarial
			String referencia = layoutsDao.obtenerReferenciaAgrupada(dto.getNoEmpresa(), 12, dto.getIdChequera(), dto.getPoHeaders());
			sCampo = funciones.quitarCaracteresRaros(dto.getConcepto(), true);
			sRegistro.append(funciones.ajustarLongitudCampo(sCampo, 30, "I", "", ""));					//1. Concepto CIE A(30)
			
			sCampo = dto.getIdChequeraBenef().substring(dto.getIdChequeraBenef().length() - 7);	//El convenio viene dentro de la chequera benef
			sRegistro.append(funciones.ajustarLongitudCampo(sCampo, 7, "D", "", "0"));					//2. No Convenio N(7)
			//Aqui determinar cuentas azules y verdes
			if(dto.getIdChequera().length() == 13)
				sRegistro.append(funciones.ajustarLongitudCampo(dto.getIdChequera().substring(3,dto.getIdChequera().length()), 18, "D", "", "0"));	//3.- Cuenta Origen (NU) 18
			else 
				sRegistro.append(funciones.ajustarLongitudCampo(dto.getIdChequera(), 18, "D", "", "0"));	//3.- Cuenta Origen (NU) 18
			
			sCampo = funciones.ponerFormatoCeros(dto.getImporte(), 13);
			sRegistro.append(funciones.ajustarLongitudCampo(sCampo, 16, "D", "", "0"));					//4. Monto MO(16)
		
			if (referencia.length()>20) {
				sRegistro.append(funciones.ajustarLongitudCampo(referencia.substring(20, referencia.length()), 30, "I", "", ""));					//5. Referencia A(30)
			}else{
				sCampo = "";
				sRegistro.append(funciones.ajustarLongitudCampo(sCampo, 30, "I", "", ""));					//5. Referencia A(30)
			}
        	
    		sRegistro.append(funciones.ajustarLongitudCampo(referencia, 20, "I", "", "\u0020"));		//6. Referencia CIE A(20)
        	
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:beArmaConvenioCIEAgrupado");
			e.printStackTrace();
		}
		System.out.println(sRegistro.toString());
		return sRegistro.toString();
	}
	
	//Armado de layoud de H2H Santander, aplica para los casos de Mismo Banco e Interbancaria
	public String beArmaH2HSantander(ParametrosLayoutDto dto, int i, double importe, int finList, boolean nomina, boolean convenioSantander) {
		String sCampo;
		StringBuffer sRegistro = new StringBuffer();
		List<ParametrosLayoutDto> lista = new ArrayList<ParametrosLayoutDto>();
		String fechaHoy = layoutsDao.fechaHoy().replace("-", "");
		
		try {
			if(i == 0) {
				//Registro de encabezado
				sRegistro.append("01");																				//1. Tipo de registro N(2)
				sRegistro.append("0000001");																		//2. Nnmero de registro N(7)
				sRegistro.append("60");																				//3. Tipo de operacinn N(2)
				sRegistro.append("014");																			//4. Banco pagador N(3)
				sRegistro.append("E");																				//5. Tipo de archivo Entrada al banco o Salida al cliente AN(1)
				sRegistro.append("2");																				//6. Tipo de servicio N(1)
				
				sCampo = fechaHoy.substring(fechaHoy.length()-2, fechaHoy.length());								//Aqun solo tomara los dias en nnmero
				sRegistro.append(sCampo + funciones.ajustarLongitudCampo(Integer.toString(1), 5, "D", "", "0"));	//7. Nnmero de bloque N(7)
				
				sRegistro.append(fechaHoy);																			//8. Fecha de creacinn AAAAMMDD N(8)
				
				sRegistro.append("01");																				//9. Tipo de divisa N(2)
				sRegistro.append("00");																				//10. Causa de rechazo N(2)
				sRegistro.append("1");																				//11. Programado de pago 1 = mismo dia, 2 = programado N(1)
				sRegistro.append(funciones.ajustarLongitudCampo("", 40, "D", "", " "));								//12. Uso futuro N(40)
				sRegistro.append(funciones.ajustarLongitudCampo("", 406, "D", "", " "));							//13. Uso futuro N(406)
				sRegistro.append("\r\n");																		//Salto de linea que no esta en el layout pero es para pintarlo de una sola pasada
			}
			
			//Registros del detalle
			sRegistro.append("02");																				//1. Tipo de registro detalle N(2)
			sRegistro.append(funciones.ajustarLongitudCampo(Integer.toString(i+2), 7, "D", "", "0"));			//2. Nnmero de registro con incremento N(7)
			sRegistro.append("60");																				//3. Tipo de operacinn N(2)
			sRegistro.append("01");																				//4. Tipo de divisa N(2)
			
			sRegistro.append(fechaHoy);																			//5. Fecha de transferencia AAAAMMDD N(8)
			
			sRegistro.append("014");																			//6. Banco presentador N(3)
			sRegistro.append(funciones.ajustarLongitudCampo(dto.getIdBancoBenef(), 3, "D", "", "0"));			//7. Banco receptor N(3)
			
			sCampo = funciones.ponerFormatoCeros(dto.getImporte(), 15).replace(".", "");
			sRegistro.append(funciones.ajustarLongitudCampo(sCampo, 15, "D", "", "0"));							//8. Importe N(15)
			
			sRegistro.append(funciones.ajustarLongitudCampo("", 16, "D", "", " "));								//9. Uso futuro N(16)
			
			if(Integer.parseInt(dto.getIdBancoBenef()) == 14) {
				if (nomina) sRegistro.append("99");
				else sRegistro.append("98");																			//10. Tipo de operacinn N(2)
			} else {
				/*if (nomina) sRegistro.append("02");
				else sRegistro.append("01");		*/																//10. Tipo de operacinn N(2)
				sRegistro.append("01");
			}
			sRegistro.append(fechaHoy);																			//11. Fecha de aplicacinn N(8)
			sRegistro.append("01");																				//12. Tipo cta. ordenante N(2)
			sRegistro.append(funciones.ajustarLongitudCampo(dto.getIdChequera(), 20, "D", "", "0"));			//13. Cta. ordenante N(20)
			
			lista = layoutsDao.buscaNomEmpresa(dto.getNoFolioDet());
			
			if(lista.size() > 0) {
				sRegistro.append(funciones.ajustarLongitudCampo(funciones.quitaCaracteresEspeciales(lista.get(0).getNomEmpresa()), 40, "I", "", " "));//14. Nombre del ordenante (40)
				sRegistro.append(funciones.ajustarLongitudCampo(lista.get(0).getRfc(), 18, "I", "", " "));		//15. RFC del ordenante (18)
			}
			
			if(Integer.parseInt(dto.getIdBancoBenef()) == 14) {
				if (convenioSantander) {
					sRegistro.append("04");																			//16. Tipo cta. receptor N(2)
					sRegistro.append(funciones.ajustarLongitudCampo(dto.getIdChequeraBenef().substring(7, dto.getIdChequeraBenef().length()), 20, "D", "", "0"));	//17. Cta. receptor N(20)			
				} else {
					sRegistro.append("01");																			//16. Tipo cta. receptor N(2)
					sRegistro.append(funciones.ajustarLongitudCampo(dto.getIdChequeraBenef(), 20, "D", "", "0"));	//17. Cta. receptor N(20)
				}
			}else {
				if (dto.getPsClabe() == null || dto.getPsClabe().equals("")) {
					return "";
				}
				sRegistro.append("40");																			//16. Tipo cta. receptor N(2)
				sRegistro.append(funciones.ajustarLongitudCampo(dto.getPsClabe(), 20, "D", "", "0"));			//17. Clabe bancarna receptor N(20)
			}
			sRegistro.append(funciones.ajustarLongitudCampo(funciones.quitaCaracteresEspeciales(dto.getBeneficiario()), 40, "I", "", " "));			//18. Beneficiario N(40)
			sRegistro.append(funciones.ajustarLongitudCampo(dto.getRfcBenef(), 18, "I", "", " "));					//19. RFC Beneficiario N(18)
			String referencia = dto.getNoFolioDet() + "";
			if (convenioSantander) {
				referencia = obtenerReferencias(dto.getNoCliente(), dto.getNoFolioDet()).get("refN").toString();			
			}
			sRegistro.append(funciones.ajustarLongitudCampo(referencia, 40, "I", "", " "));//20. Referencia Servicio Emisor. N(40)
			sRegistro.append(funciones.ajustarLongitudCampo("CONCRET INFRAESTRUCTURA SA DE CV", 40, "I", "", " "));			//21. Nombre Titular Servicio. N(40)
			sRegistro.append(funciones.ajustarLongitudCampo("0", 15, "D", "", "0"));							//22. Importe IVA de Operacinn N(15)
			sRegistro.append("0000001");																		//23. Referencia Numero de Ordenante. Valor asignado por Santander N(7)
			sRegistro.append(funciones.ajustarLongitudCampo(dto.getNoFolioDet() + "", 40, "I", "", " "));									//24. Referencia Leyenda del Ordenante N(40)
			sRegistro.append(funciones.ajustarLongitudCampo("", 30, "I", "", " "));											//25. Clave de Rastreo N(30)
			sRegistro.append("00");																							//26. Motivacion de Devolucion N(2)
			sRegistro.append(fechaHoy);																						//27. Fecha de Presentacion Inicial AAAAMMDD N(8)
			sRegistro.append(funciones.ajustarLongitudCampo("", 12, "I", "", " "));											//28. Uso Futuro N(12)
			sRegistro.append(funciones.ajustarLongitudCampo(dto.getNoFolioDet() + "", 30, "I", "", " "));		//29. Referencia Cliente. N(30)
			sRegistro.append(funciones.ajustarLongitudCampo(dto.getConcepto(), 30, "I", "", " "));							//30. Descripciï¿½n Referencia Pago N(30)
			if (convenioSantander) {
				sRegistro.append(funciones.ajustarLongitudCampo("", 500, "I", "", " "));							//31. Correo notificaciï¿½n beneficiario N(500)
				//if (dto.getConcepto().substring(0, 3).equals("lc:"))
					sRegistro.append(funciones.ajustarLongitudCampo(dto.getConcepto(), 100, "I", "", " "));							//32. Linea de captura N(100)
				//else
					//sRegistro.append(funciones.ajustarLongitudCampo("", 100, "I", "", " "));							//32. Linea de captura N(100)
			}
			sRegistro.append("\r\n");																						//Salto de linea que no esta en el layout pero es para pintarlo de una sola pasada
			
			if((i+1) == finList) {	
				//Registro de Sumario	
				sRegistro.append("");		
				sRegistro.append("09");																			//01. Tipo de Registro N(2)
				sRegistro.append(funciones.ajustarLongitudCampo(Integer.toString(i+3), 7, "D", "", "0"));		//02. Numeo de Secuencia N(7)
				sRegistro.append("60");																			//03. Codigo de Operacinn N(2)
				
				sCampo = fechaHoy.substring(fechaHoy.length()-2, fechaHoy.length());							//Aqun solo tomara los dias en nnmero
				sRegistro.append(sCampo + funciones.ajustarLongitudCampo(Integer.toString(1), 5, "D", "", "0"));//04. Nnmero de bloque N(7)
				sRegistro.append(funciones.ajustarLongitudCampo(Integer.toString(i+1), 7, "D", "", "0"));		//05. Numero de Operaciones N(7)
				
				sCampo = funciones.ponerFormatoCeros(importe, 18).replace(".", "");
				sRegistro.append(funciones.ajustarLongitudCampo(sCampo, 18, "D", "", "0"));						//06. Importe Total Operaciones N(18)
				sRegistro.append(funciones.ajustarLongitudCampo("", 40, "D", "", " "));							//07. Uso Futuro CCEN N(40)
				sRegistro.append(funciones.ajustarLongitudCampo("", 399, "D", "", " "));						//08. Uso Futuro Banco N(399)
			}
			return sRegistro.toString();
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:beArmaH2HSantander");
			e.printStackTrace();
			System.out.println("error entro al catch");
		}
		System.out.print(sRegistro.toString());
		return sRegistro.toString();
	}
	
	public Map<String, Object> beExportacionAgrupado(List<MovimientoDto> listBus, int i, int tEnvio, String nomArchivo, String sRuta,
			 int idBanco, String pantalla) {
		iBanco = idBanco;
		liTipoTransfer = tEnvio;
			
		try {
			structBE = layoutsDao.envioBEParametrizadoAgrupado(idBanco, tEnvio); 
			
			if(structBE.size() <= 0) {
				mapResp.put("msgUsuario", "No se encontraron datos en envio_be del id_banco = " + idBanco + "");
				return mapResp;
			}
			if(pantalla.equals("TRANSFERENCIA")) {				
				if(layoutsDao.consultarConfiguraSet(227).equals("A") || idBanco != 107) {
					armaLayoutAgrupado(listBus, i, pantalla, false,structBE);
					if(!mapResp.isEmpty() && (Boolean)mapResp.get("msgVF") == true)
						return mapResp;
					else {
						mapResp.put("msgVF", false);
						mapResp.put("msgUsuario", "Error al crear al layout.");
						return mapResp;
					}
				}
			}else if(pantalla.equals("CONTROL ARCHIVO")) {
				armaLayoutAgrupado(listBus, i, "CONTROL_ARCHIVO", false,structBE);
			
				if(!mapResp.isEmpty())
					return mapResp;
				else {
					mapResp.put("msgVF", false);
					return mapResp;
				}
			}else if(pantalla.equals("TRASPASO")) {
			//codigo para la pantalla de traspasos.
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:beExportacion");
			return mapResp;
		}
		return mapResp;
	}
	 
	public Map<String, Object>armaLayoutAgrupado(List<MovimientoDto> listGrid, int pos, String pantalla, boolean lbTraspasoControlArchivo,List<CriterioBusquedaDto> structBEA) {
		String sRes = "";
		String sRegistro = "";
		String psCampo = "";
		String sFEspecial = "";
		String campoGrid = "";
		mapResp.put("msgVF", true);
		
		try {
			//Se obtienen los datos del Layout para generar el Registro que sera insertado en el Archivo.txt
			for(int i=0; i<structBEA.size(); i++) {
		//		System.out.println("campo a obtener "+structBEA.get(i).getCampo().toString());
				campoGrid = obtenerCampo(structBEA.get(i).getCampo().toString() != null ? structBEA.get(i).getCampo().toString() : "" , i, listGrid, pos, pantalla, false);
				psCampo = campoGrid;
				System.out.println("1 campo grid "+campoGrid);
				System.out.println("2 campo "+psCampo);
				//Si existe el campo en la tabla de equivalencias regresa una poscinn mayor a 0
				if(!campoGrid.equals("falso")) {
				//System.out.println("3");
				//Validacinn para pago referenciado de proveedores especificos
					if(structBEA.get(i).getCampo().toString().equals("no_folio_det")) {
						System.out.println("obtine ref agrup1");
						if(pantalla.equals("TRANSFERENCIA")) {
							System.out.println("obtine ref agrup");
							psCampo = layoutsDao.obtenerReferenciaAgrupada(listGrid.get(pos).getNoEmpresa(),
									listGrid.get(pos).getIdBanco() , listGrid.get(pos).getIdChequera(), listGrid.get(pos).getPoHeaders());
							System.out.println("obtine ref agrup R"+psCampo);
							//psCampo = psCampo.trim().replace("-", "").replace(" ", "");
							psCampo = funciones.quitarCaracteresRaros(psCampo, true);
						}else if(pantalla.equals("CONTROL_ARCHIVO")) {
							if(!lbTraspasoControlArchivo) {
								psCampo = layoutsDao.obtenerReferenciaAgrupada(listGrid.get(i).getNoEmpresa(),listGrid.get(i).getIdBanco() , listGrid.get(i).getIdChequera(), listGrid.get(i).getPoHeaders());
								//psCampo = psCampo.trim().replace("-", "").replace(" ", "");
								psCampo = funciones.quitarCaracteresRaros(psCampo, true);
							}
						}
						if(psCampo.equals("")) psCampo = campoGrid;
					}
					
					if(structBEA.get(i).getCampo().toLowerCase().equals("beneficiario") || structBEA.get(i).getCampo().toLowerCase().equals("concepto") ||
							structBEA.get(i).getCampo().toLowerCase().equals("especiales") || structBEA.get(i).getCampo().toLowerCase().equals("complemento")) {
						
						if(!structBEA.get(i).getCampo().toLowerCase().equals("especiales") && !structBEA.get(i).getCampo().toLowerCase().equals("complemento"))
							psCampo = funciones.quitarCaracteresRaros(psCampo, true);
						psCampo = funciones.ocultarCoinversora(psCampo);
						
						//Se agrega IF para revisar campo concepto
						if(structBEA.get(i).getCampo().toLowerCase().equals("concepto") && pantalla.equals("TRANSFERENCIA") && 
								structBEA.get(i).getIdBanco() == ConstantesSet.CITIBANK_DLS && structBEA.get(i).getIdTipoEnvio() == 5) {
							//Si FURTHER CREDIT es parte del concepto este se envia en el registro de WL en el campo Bank Details
							if(psCampo.toUpperCase().indexOf("FURTHER") == -1 || psCampo.toUpperCase().indexOf("CREDIT") == -1) psCampo = "";
						}
					}
					psCampo = validaEquivalenciasBE(psCampo, structBEA.get(i).getIdTipoEquivale());
					psCampo = validaFormato(psCampo, structBEA.get(i).getFormato().trim());	//Valida el Formato del campo
					sRes = validaCampoRequerido(structBEA.get(i).getBRequerido(), psCampo, pos, i);
					
					if(!sRes.equals("")) {
						mapResp.put("msgVF", false);
						mapResp.put("msgUsuario", sRes);
						mapResp.put("msgRegistro", "");
						return mapResp;
					}
					sFEspecial = structBEA.get(i).getFuncionEspecial() == null ? "" : structBEA.get(i).getFuncionEspecial().trim();
					
					if(!sFEspecial.equals("")) {
						if(sFEspecial.substring(sFEspecial.length() - 3).equals("MID"))
							psCampo = sFEspecial.substring(Integer.parseInt(sFEspecial.substring(3, 6)), Integer.parseInt(sFEspecial.substring(6, 9)));
					}
					if(structBEA.get(i).getCampo().equals("id_divisa") && psCampo.equals("YEN") && iBanco == ConstantesSet.CITIBANK_DLS && liTipoTransfer == 5)
						psCampo = "JPY";
					
					//Regresa el Registro que sera insertado en el Archivo
					sRegistro = sRegistro + funciones.ajustarLongitudCampo(psCampo, structBEA.get(i).getLongitud(), structBEA.get(i).getJustifica(), 
							structBEA.get(i).getSeparador(), structBEA.get(i).getComplemento());
					System.out.println("esta es la posicion"+i);
					System.out.println("campo "+psCampo);
					System.out.println("sregistro "+sRegistro);
				}else {
					mapResp.put("msgVF", false);
					mapResp.put("msgUsuario", "No existe campo de la tabla equivalencias " + structBEA.get(i).getCampo().toString());
					return mapResp;
				}
			}
			//System.out.println("esta es i"+i);
			mapResp.put("msgRegistro", sRegistro);
		}catch(Exception e) {
			System.out.println("aaaaaaa-------aaaaaaaaaaaaa");
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:armaLayout");
			mapResp.put("msgVF", false);
			return mapResp;
		}
		return mapResp;
	}
			
	public String obtenerCampoAgrupado(String sCampo, int posCampo, List<MovimientoDto> listGrid, int pos, String pantalla,
			boolean interBancario,List<CriterioBusquedaDto> structBEA) {
			//Funcion para Guardar equivalencias con el Grid y el Layout, Guarda la posicinn que tiene el campo en el Grid
		String regCampo = "falso";
		System.out.println( " motodo obtenerCampo parametros sCampo "+sCampo +" posCampo "+posCampo+
		" pos "+pos);
		try {
			if(pantalla.equals("TRANSFERENCIA")) {
				if(iBanco == ConstantesSet.INBURSA && interBancario) {
					if(sCampo.equals("id_clabe")) 
						regCampo = listGrid.get(pos).getIdChequera();
				}else {
					if(sCampo.equals("id_chequera")) 
						regCampo = listGrid.get(pos).getIdChequera();
				}
				//Para chequeras en dolares que tiene id_clabe pasan por aqui
				if(sCampo.equals("id_chequera_benef") && listGrid.get(pos).getIdDivisa().equals("DLS") &&
						(layoutsDao.consultarConfiguraSet(1).equals("CCM") || layoutsDao.consultarConfiguraSet(1).equals("CIE")))
					regCampo = listGrid.get(pos).getClabeBenef();
				else{
					if(sCampo.equals("id_chequera_benef"))
						regCampo = listGrid.get(pos).getIdChequeraBenefReal();
				}
				if(sCampo.equals("id_banco_benef")) regCampo = "" + listGrid.get(pos).getIdBancoBenef();
				if(sCampo.equals("plaza")) regCampo = "" + listGrid.get(pos).getPlazaBenef();
				if(sCampo.equals("plaza_origen")) regCampo = "" + listGrid.get(pos).getPlaza();
				if(sCampo.equals("no_cliente")) regCampo = listGrid.get(pos).getNoCliente();
				if(sCampo.equals("beneficiario")) regCampo = listGrid.get(pos).getBeneficiario();
				if(sCampo.equals("importe")) regCampo = "" + listGrid.get(pos).getImporte();
				if(sCampo.equals("concepto")) regCampo = listGrid.get(pos).getConcepto();
				if(sCampo.equals("id_divisa")) regCampo = listGrid.get(pos).getIdDivisa();
				if(sCampo.equals("no_folio_det")) regCampo = "" + listGrid.get(pos).getNoFolioDet();
				if(sCampo.equals("fec_valor")) regCampo = validaFormato(funciones.ponerFechaSola(listGrid.get(pos).getFecValor()), structBEA.get(posCampo).getFormato());
				if(sCampo.equals("fecha_hoy")) regCampo = layoutsDao.fechaHoy();
				if(sCampo.equals("valor_default")) regCampo = structBEA.get(posCampo).getValorDefault();
				if(sCampo.equals("inst_finan")) regCampo = listGrid.get(pos).getInstFinan();
				if(sCampo.equals("sucursal_origen")) regCampo = listGrid.get(pos).getSucOrigen();
				if(sCampo.equals("sucursal_destino")) regCampo = listGrid.get(pos).getSucDestino();
				if(sCampo.equals("hora")) regCampo = listGrid.get(pos).getHoraRecibo();
				if(sCampo.equals("observacion")) regCampo = listGrid.get(pos).getObservacion();
				if(sCampo.equals("desc_banco_inbursa")) regCampo = listGrid.get(pos).getDescBancoInbursa();
				if(sCampo.equals("id_clabe_benef")) regCampo = listGrid.get(pos).getClabeBenef();
				//if(sCampo.equals("id_chequera_banamex")) regCampo = listGrid.get(pos).getIdChequeraBanamex();
				if(sCampo.equals("desc_banco_benef")) regCampo = listGrid.get(pos).getDescBancoBenef();
				if(sCampo.equals("id_aba_swift")) regCampo = "" + listGrid.get(pos).getAba().toString();
				if(sCampo.equals("aba_swift")) regCampo = "" + listGrid.get(pos).getSwiftCode();
				if(sCampo.equals("id_AbaSwift_intermediario")) 
				regCampo = listGrid.get(pos).getAbaIntermediario() != null && !listGrid.get(pos).getAbaIntermediario().equals("") ?
						listGrid.get(pos).getAbaIntermediario():"";
				if(sCampo.equals("AbaSwift_intermediario")) 
				regCampo = listGrid.get(pos).getSwiftIntermediario() != null && !listGrid.get(pos).getSwiftIntermediario().equals("")?
						listGrid.get(pos).getSwiftIntermediario(): "";
				if(sCampo.equals("nom_banco_intermediario")) 
				regCampo = listGrid.get(pos).getNomBancoIntermediario() != null && !listGrid.get(pos).getNomBancoIntermediario().equals("") ?
						listGrid.get(pos).getNomBancoIntermediario():"";
				if(sCampo.equals("id_AbaSwift_corresponsal")) 
				regCampo = listGrid.get(pos).getAbaCorresponsal() != null && !listGrid.get(pos).getAbaCorresponsal().equals("") ?
						listGrid.get(pos).getAbaCorresponsal():"";
				if(sCampo.equals("AbaSwift_corresponsal")) 
				regCampo = listGrid.get(pos).getSwiftCorresponsal() != null && listGrid.get(pos).getSwiftCorresponsal().equals("") ?
						listGrid.get(pos).getSwiftCorresponsal():"";
				if(sCampo.equals("nom_banco_corresponsal")) 
				regCampo = listGrid.get(pos).getNomBancoCorresponsal() != null && listGrid.get(pos).getNomBancoCorresponsal().equals("") ?
						listGrid.get(pos).getNomBancoCorresponsal():"";
				if(sCampo.equals("rfc")) 
				regCampo = listGrid.get(pos).getRfc() != null && listGrid.get(pos).getRfc().equals("") ? 
						listGrid.get(pos).getRfc():"";
				if(sCampo.equals("nom_empresa")) 
				regCampo = listGrid.get(pos).getNomEmpresa() != null && listGrid.get(pos).getNomEmpresa().equals("") ?
						listGrid.get(pos).getNomEmpresa():"";
				if(sCampo.equals("id_contrato_wlink")) 
				regCampo = listGrid.get(pos).getIdContratoWlink() != null && listGrid.get(pos).getIdContratoWlink().equals("")?
						listGrid.get(pos).getIdContratoWlink():"";
				if(sCampo.equals("especiales")) regCampo = listGrid.get(pos).getEspeciales();
				if(sCampo.equals("complemento")) regCampo = listGrid.get(pos).getComplemento();
				//if(sCampo.equals("pais_banco_const")) regCampo = listGrid.get(pos).getPaisBancoConst();
				//if(sCampo.equals("direccion_banco_const")) regCampo = listGrid.get(pos).getDireccionBancoConst();
				if(sCampo.equals("pais_benef_const")) regCampo = listGrid.get(pos).getPaisBenef();
				if(sCampo.equals("direccion_benef_const")) regCampo = listGrid.get(pos).getDireccionBenef();
				
			}
			//Se valida si trae valor, de lo contrario se toma el valor default
			
			
			if(regCampo == null || regCampo.equals("") || regCampo.equals("0"))
				regCampo = structBEA.get(posCampo).getValorDefault()==null ? "" : structBEA.get(posCampo).getValorDefault();
		}catch(Exception e) {
			System.out.println("aaaaaaa-------");
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:llenaEquivaleBE");
			return "";
		}
		return regCampo;
	}
	
	//Banamex CitibankPaylinkMN Agrupado
		public String beArmaCitibankPaylinkMNAgrupado(List<MovimientoDto> listGrid, int i, String psReferenciaTraspaso) {
			
		    String psRegistro = "";
		    String psCampo = "";
			try {
				//Citibank PayLink Plus 1024 para Pago Mismo dia SOLO MXP
				psRegistro = "PAY";														//1 Record Type (02)
				psRegistro += "484";													//2 Costumer Country Code (03) - 484 = Mexico
				psCampo = listGrid.get(i).getIdChequera().substring(listGrid.get(i).getIdChequera().length() - 10);
				psRegistro += psCampo;													//3 Customer Account Number (10)
				
				if(layoutsDao.consultarConfiguraSet(1).equals("CCM"))
					psCampo = validaFormato(listGrid.get(i).getFecValorOriginal().toString(), "yyMMdd");
				else
					psCampo = validaFormato(listGrid.get(i).getFecValor().toString(), "yyMMdd");
				psRegistro += psCampo;													//4 Transaction Date (6)
				psRegistro += "071";													//5 Transaction Code (3)
				
				if(psReferenciaTraspaso.equals("")) {
					//Solo aplica para Transferencias, Pagos referenciados a Clientes
					//valorPagRef = layoutsDao.obtenerValorPagoReferenciado(Integer.parseInt(listGrid.get(i).getNoCliente()), listGrid.get(i).getNoFolioDet(), "");
	            	//psCampo = valorPagRef.get("valorPagRef").toString();
					psCampo = layoutsDao.obtenerReferenciaAgrupada(listGrid.get(i).getNoEmpresa(),
							listGrid.get(i).getIdBanco() , listGrid.get(i).getIdChequera(), listGrid.get(i).getPoHeaders());
					
				}else
					psCampo = psReferenciaTraspaso;
				psRegistro += funciones.ajustarLongitudCampo(psCampo, 15, "I", "", "");	//6 Transaction Reference A(15)
				
				layoutsDao.actualizarFolioReal("folio_paylink");
				psCampo = "" + layoutsDao.seleccionarFolioReal("folio_paylink");
				if(Integer.parseInt(psCampo) < 0) 
					return "";
				
				psRegistro += funciones.ajustarLongitudCampo(psCampo, 8, "D", "", "0");	//7 Transaction Sequence number  N(8)
				psRegistro += funciones.ajustarLongitudCampo("X", 20, "I", "", "");		//8 Beneficiary tax id number N(20)
				psRegistro += "MXN";													//9 Customer account Currency Code(3)
				psCampo = funciones.ajustarLongitudCampo(listGrid.get(i).getNoCliente().toString(), 20, "I", "", "");
				psRegistro += psCampo;													//10 Beneficiary Code A(20)
				psCampo = funciones.ponerFormatoCeros((listGrid.get(i).getImporte() * 100), 15);
				//psCampo = funciones.ajustarLongitudCampo("" + (listGrid.get(i).getImporte() * 100), 15, "D", "", "0");
				psRegistro += psCampo;													//11 Transaction amount N(15)
				psRegistro += funciones.ajustarLongitudCampo("", 6, "D", "", "");		//12 MAturity Date DATE(6)
				psCampo = funciones.quitarCaracteresRaros(listGrid.get(i).getConcepto(), true);
				psRegistro += funciones.ajustarLongitudCampo(psCampo, 35, "I", "", "");	//13 Transaction Detail Line 1 A(35)
				psRegistro += funciones.ajustarLongitudCampo("", 35, "I", "", "");		//14 Transaction Detail Line 2 A(35)
				psRegistro += funciones.ajustarLongitudCampo("", 35, "I", "", "");		//15 Transaction Detail Line 3 A(35)
				psRegistro += funciones.ajustarLongitudCampo("", 35, "I", "", "");		//16 Transaction Detail Line 4 A(35)
				psRegistro += "05";														//17 Local Transaction Code N(2)
				psRegistro += "01";														//18 COstumer account type N(2)
				psCampo = funciones.quitarCaracteresRaros(listGrid.get(i).getBeneficiario(), true);
				psCampo = funciones.ocultarCoinversora(psCampo);
				psRegistro += funciones.ajustarLongitudCampo(psCampo, 80, "I", "", "");	//19 Beneficiary name A(80)
				psRegistro += funciones.ajustarLongitudCampo("X", 35, "I", "", "");		//20 Beneficiary Adress 1 A(35)
				psRegistro += funciones.ajustarLongitudCampo("", 35, "I", "", "");		//21 Beneficiary Adress 1 second line A(35)
				psRegistro += funciones.ajustarLongitudCampo("X", 15, "I", "", "");		//22 Beneficiary Adress 2 A(15) ciudad
				psRegistro += funciones.ajustarLongitudCampo("", 2, "I", "", "");		//23 Beneficiary state A(2)
				psRegistro += funciones.ajustarLongitudCampo("1", 12, "D", "", "0");	//24 Beneficiary adress 3 A(12) Cod. Postal
				psRegistro += funciones.ajustarLongitudCampo("", 16, "I", "", "");		//25 Beneficiary phone number N(16)
				psCampo = funciones.ajustarLongitudCampo(listGrid.get(i).getIdBancoCityStr(), 3, "D", "", "0");
				psRegistro += psCampo;													//26 Beneficiary Bank number N(3)
				psCampo = funciones.ajustarLongitudCampo("MEXICO", 8, "I", "", "");
				psRegistro += psCampo;													//27 Beneficiary Bank Agency A(8)
				psCampo = funciones.ajustarLongitudCampo(listGrid.get(i).getClabeBenef(), 35, "D", "", "0");
				psRegistro += psCampo;													//28 Beneficiary Account Number(35) SOLO INTERMEDIARIOS
				psRegistro += "05";														//29 Account type CR SOLO INTERBANCARIO N(2)
				psCampo = funciones.ajustarLongitudCampo("MEXICO", 30, "I", "", "");
				psRegistro += psCampo;													//30 Beneficiary Bank Adress A(30)
				psRegistro += "01";														//31 Beneficiary Bank Entity N(2)
				psRegistro += "001";													//32 Beneficiary Bank Place Number N(3)
				psCampo = funciones.ajustarLongitudCampo("MEXICO", 14, "I", "", "");
				psRegistro += psCampo;													//33 Beneficiary Bank Place Name  A(14)
				psRegistro += "001";													//34 Beneficiary Bank Branch Number N(3)
				psCampo = funciones.ajustarLongitudCampo(listGrid.get(i).getNombreBancoBenef(), 19, "I", "", "");
				psRegistro += psCampo;													//35 Beneficiary Bank Branch Name N(19)
				psRegistro += funciones.ajustarLongitudCampo("", 16, "I", "", "");		//36 Beneficiary Fax Number N(16)
				psRegistro += funciones.ajustarLongitudCampo("", 20, "I", "", "");		//37 Beneficiary Fax Contact Name A(20)
				psRegistro += funciones.ajustarLongitudCampo("", 15, "I", "", "");		//38 Beneficiary Fax Department Name A(15)
				psRegistro += funciones.ajustarLongitudCampo("", 10, "D", "", "0");		//39 Beneficiary Account number N(10) SOLO MISMO BANCO
				psRegistro += funciones.ajustarLongitudCampo("", 2, "D", "", "");		//40 Account Type CR N(2) SOLO MISMO BANCO
				psRegistro += "001";													//41 Transaction Deliver Method N(3) 001-->MEXICO
				psRegistro += funciones.ajustarLongitudCampo("", 50, "D", "", "");		//42 Collection Title ID A(50)
				psRegistro += funciones.ajustarLongitudCampo("", 5, "D", "", "");		//43 Beneficiary Activity code N(5)
				psRegistro += funciones.ajustarLongitudCampo("", 50, "D", "", "");		//44 Beneficiary e-mail adress A(50)
				//psCampo = funciones.ajustarLongitudCampo("" + (listGrid.get(i).getImporte() * 100), 15, "D", "", "0");
				psCampo = funciones.ponerFormatoCeros((listGrid.get(i).getImporte() * 100), 15);
				psRegistro += psCampo;													//45 Maximum payment amount N(15)
				psRegistro += funciones.ajustarLongitudCampo("", 1, "D", "", "");		//46 Update Type N(1)
				psRegistro += funciones.ajustarLongitudCampo("", 11, "D", "", "0");		//47 Check Number N(11)
				psRegistro += funciones.ajustarLongitudCampo("", 1, "D", "", "");		//48 Printed Check A(1)
				psRegistro += funciones.ajustarLongitudCampo("", 1, "D", "", "");		//49 Match Paid A(1)
				psRegistro += funciones.ajustarLongitudCampo("", 254, "D", "", "");		//50 Blank Characteres A(254)
			}catch(Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout,"
						+ " C:LayoutBancomerBusiness, M:beArmaCitibankPaylinkMN");
				e.printStackTrace();
				return "";
			}
			return psRegistro;
		}
		
		//Banamex TEF Agrupado
		public Map<String, Object> beArmaBanamexTEFAgrupado(boolean pbInterbancario, List<MovimientoDto> listGrid, boolean pbControl_archivo, 
									   String psReferenciaTraspaso, int iGrid, String dirPrincipal, String sDescBanco, String nomArch) {
			Map<String, Object> mapCreaArch = new HashMap<String,Object>();
			Map<String, Object> mapResult = new HashMap<String,Object>();
			List<MovimientoDto> encabezado = new ArrayList<MovimientoDto>();
			List<MovimientoDto> detalle = new ArrayList<MovimientoDto>();
			boolean pbCLABE = false;
			String psNomEmpresa = "";
			String psReferencia = "";
			String sRegistro = "";
			String sCampo = "";
			String psFoliosIn = "";
			int resDetArchTrans = 0;
			int piRegistros = 0;
			String psArchGenerados = "";
			String lsYaEjecutados = "";
			//String sFolioDet = "";
			String sPoHeaders = "";
			
			try {
				for(int i=0; i<listGrid.size(); i++) {
					 
						if(envioTransferenciasADao.consultarPagoEjecutado(listGrid.get(i).getPoHeaders(),
								listGrid.get(i).getNoEmpresa(), listGrid.get(i).getIdBanco(), listGrid.get(i).getIdChequera()) && !pbControl_archivo)
							lsYaEjecutados += listGrid.get(i).getNoDocto() + ",";
				    	else{
				    		if (sPoHeaders.equals("")) {
				    			sPoHeaders =  listGrid.get(i).getPoHeaders();
							} else {
								sPoHeaders +=  "','" +listGrid.get(i).getPoHeaders();
							}
				    	}
					
				}
				if(!lsYaEjecutados.equals("")) {
					mapResult.put("msgUsuario", "Los siguientes documentos ya fueron procesados: \n" + lsYaEjecutados);
					
					if(sPoHeaders.equals("")) {
						mapResult.put("msgVF", false);
						return mapResult;
					}
				}
				//Para el Interbancario utilizar la version "C"
		        //Para el Mismo Banco utilizar la version "B"
		        
		        //Buscar los datos necesarios con los folios det, se genera un archivo por chequera pagadora
				if(psReferenciaTraspaso.trim().equals(""))
					encabezado = layoutsDao.encabezadoBitalEnvioAgrupado(sPoHeaders, false, pbControl_archivo);
				else
					encabezado = layoutsDao.encabezadoBitalEnvioAgrupado(sPoHeaders, false, false);
				
				for(int i=0; i<encabezado.size(); i++) {
					if(!pbControl_archivo) {
						mapCreaArch = creacionArchivosBusiness.generarNombreArchivo(sDescBanco, "arch_banamex_tef", "BT", pbInterbancario == true ? false : true, false, false, 0,1);
		   				
		   				if(!mapCreaArch.isEmpty()) {
							if(mapCreaArch.get("msgUsuario") != null) {
								mapResult.put("msgUsuario", mapCreaArch.get("msgUsuario"));
								mapResult.put("msgVF", false);
								return mapResult;
							}
						}
					}else{
						mapCreaArch.put("nomArchivo",nomArch);
						mapCreaArch.put("psBanco",sDescBanco);

					}
					if(encabezado.get(i).getNoContrato() == 0) {
						mapResult.put("msgUsuario", "NO se puede Generar el envio TEF sin Contrato TEF para la Empresa, de la chequera " + encabezado.get(i).getIdChequera());
						mapResult.put("msgVF", false);
						return mapResult;
					}
					if(layoutsDao.consultarConfiguraSet(209).equals("SI") && pbInterbancario) 
						pbCLABE = true;
					
					psNomEmpresa = encabezado.get(i).getNomEmpresa();
					psNomEmpresa = funciones.ocultarCoinversora(psNomEmpresa);
					
					// *** Insertar encabezados ***
					mapResult = beEncabezadoBanamexTEFAgrupado(mapCreaArch, psNomEmpresa, encabezado, i, 
							encabezado.get(i).getIdChequera().substring(0, 3), pbCLABE, pbInterbancario, dirPrincipal);
					
					if(!mapResult.isEmpty()) {
						if(mapResult.get("msgVF") != null) 
							return mapResult;
					}
					
					//Insertar detalles
					//Buscar el detalle de la chequera encontrada en el encabezado
					if(psReferenciaTraspaso.trim().equals(""))
						detalle = layoutsDao.detalleBitalEnvioAgrupado(sPoHeaders, listGrid.get(i).getIdChequera(), 0, pbControl_archivo);
					else
						detalle = layoutsDao.detalleBitalEnvioAgrupado(sPoHeaders, listGrid.get(i).getIdChequera(), 0, false);
					
					for(int x=0; x<detalle.size(); x++) {
						if(!psReferenciaTraspaso.trim().equals(""))
							psReferencia = psReferenciaTraspaso.trim();
						else
							psReferencia = "" + detalle.get(x).getNoFolioDet();
						
						//Solo aplica para Transferencias, Pagos referenciados a Clientes					
						if(psReferenciaTraspaso.trim().equals(""))
							//psReferencia = layoutsDao.obtenValorPagoReferenciado(detalle.get(x).getNoCliente(), detalle.get(x).getNoFolioDet(), "");
							psReferencia = layoutsDao.obtenerReferenciaAgrupada(detalle.get(x).getNoEmpresa(),
									detalle.get(x).getIdBanco() , detalle.get(x).getIdChequera(), detalle.get(x).getPoHeaders());
						//DETALLE
	                    //Registro de Cargos y Abonos Individuales
						sRegistro = "3";			//3--> Registro de movtos individuales
						sRegistro += "0";			//Tipo de operacion, 0 = abono, 1 = cargo
						sRegistro += "001";			//Clave de la moneda 001 = pesos M.N.
						sRegistro += funciones.ajustarLongitudCampo(validaFormato("" + detalle.get(x).getImporte(), "999"), 18, "D", "", "0");		//Importe a abonar o cargar 9(16)V9(02)
						//DESC-MS
						if(!pbInterbancario) {
							if(detalle.get(x).getIdChequeraBenef().trim().length() == 16)
								sCampo = "03";
							else
								sCampo = "01";
						}else
							sCampo = "01";
						
						sRegistro += sCampo;		//Tipo de cuenta 9(02) 01=cheques  40=CLABE
						
						if(!pbInterbancario)
							sRegistro += funciones.ajustarLongitudCampo(detalle.get(x).getSucDestino(), 4, "D", "", "0");		//Tipo de cuenta 9(02) 01=cheques
						
						if(pbCLABE)
							sCampo = detalle.get(x).getClabeBenef();
						else {
							if(pbInterbancario)
								sCampo = detalle.get(x).getIdChequeraBenef();
							else {
								//DESC-MS aqui agregar la logitud de 16 para pago matico
								if(detalle.get(x).getIdChequeraBenef().trim().length() == 16)
									sCampo = detalle.get(x).getIdChequeraBenef();
								else
									sCampo = detalle.get(x).getIdChequeraBenef().substring(detalle.get(x).getIdChequeraBenef().length() - 7);
							}
						}
						sRegistro += funciones.ajustarLongitudCampo(sCampo, 20, "D", "", "0");		//Numero de cuenta 9(20)
						sRegistro += funciones.ajustarLongitudCampo(psReferencia, 40, "I", "", "");	//REFERENCIA DE la operacion TEF X(40)
						sCampo = detalle.get(x).getBeneficiario().toUpperCase();
						sRegistro += funciones.ocultarCoinversora(sCampo);
						
						if(sCampo.length() > 50) 
							sCampo = sCampo.substring(0, 50);
						sCampo = ajustaBeneficiarioMoralFisica(sCampo, detalle.get(x).getRfcBenef());
						sCampo = funciones.ajustarLongitudCampo(sCampo, 55, "I", "", "");
						sRegistro += sCampo;				//Beneficiario X(55)
						sRegistro += funciones.ajustarLongitudCampo("", 40, "D", "", " ");			//Instrucciones X(40)
						sCampo = funciones.quitarCaracteresRaros(detalle.get(x).getConcepto(), true);
						sRegistro += funciones.ajustarLongitudCampo(sCampo, 24, "I", "", "");		//Descripcion TEF X(20) X(24)
						
						if(pbInterbancario) {
							sRegistro += funciones.ajustarLongitudCampo("" + detalle.get(x).getIdBancoBenef(), 4, "D", "", "0");		//Clave de banco 9(04)
							sRegistro += funciones.ajustarLongitudCampo(psReferencia, 7, "D", "", "0");		//Referencia Bajo Valor 9(07)
							
							if((detalle.get(x).getImporte() > 50000) || (detalle.get(x).getIdBancoBenef() == ConstantesSet.BANCOMER))
								sCampo = "00";
							else
								sCampo = "24";
							sRegistro += sCampo;			//Plazo para pagos
						}else {
							sRegistro += "00";				//Clave de estado 9(02)
							sRegistro += "0000";			//Clave de ciudad
							sRegistro += "0000";			//Clave de Banco
						} //aqui se modifico
						mapResult = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal, mapCreaArch.get("psBanco").toString(),
								   mapCreaArch.get("nomArchivo").toString(), sRegistro);
						if(!(Boolean)mapResult.get("resExito")) {
							mapResult.put("msgVF", false);
							mapResult.put("msgUsuario", "Error al Escribir el Detalle en el Archivo Banamex TEF!!");
							return mapResult;
						}
						
						//aqui se modifico
						if(!pbControl_archivo) {
							DetArchTranferAgrup dtoInsertA = new DetArchTranferAgrup();
	    		    	
							dtoInsertA.setNomArch(mapCreaArch.get("nomArchivo").toString().substring(0, 8));
				    		dtoInsertA.setPoHeaders(detalle.get(x).getPoHeaders());
				    		dtoInsertA.setFecPropuesta(detalle.get(x).getFecValor());
				    		dtoInsertA.setIdBanco(detalle.get(x).getIdBanco());
				    		dtoInsertA.setIdEstatusArch("T");
				    		dtoInsertA.setIdChequeraBenef(detalle.get(x).getIdChequeraBenef());
				    		dtoInsertA.setImporte(detalle.get(x).getImporte());
				    		dtoInsertA.setBeneficiario(detalle.get(x).getBeneficiario());
				    		dtoInsertA.setSucursal(Integer.parseInt(detalle.get(x).getSucDestino()));
				    		dtoInsertA.setPlaza(Integer.parseInt(detalle.get(x).getPlazaBenef()));
				    		dtoInsertA.setNoDocto(detalle.get(x).getNoDocto());
				    		dtoInsertA.setIdChequera(detalle.get(x).getIdChequera());
				    		dtoInsertA.setIdBancoBenef(detalle.get(x).getIdBancoBenef());
				    		dtoInsertA.setPrefijoBenef(detalle.get(x).getInstFinan() != null ? detalle.get(x).getInstFinan():"");
				    		dtoInsertA.setConcepto(detalle.get(x).getConcepto());
				    		dtoInsertA.setNoEmpresa(encabezado.get(i).getNoEmpresa());
				    		resDetArchTrans = envioTransferenciasADao.insertarDetArchTransferAgrup(dtoInsertA);
	    		    		
	    		    		//resDetArchTrans = layoutsDao.insertDetArchTransfer(dtoInsert);
	    		    		
	    		    		if(resDetArchTrans > 0) {
	    		    			psFoliosIn += detalle.get(x).getPoHeaders() + ",";
	    		    			System.out.println("donde valida que los pagos se realizaron");
		                    	piRegistros++;
		                    }
						}
					}
					mapResult = beTrailBanamexTEFAgrupado(encabezado.get(i).getImporte(), piRegistros, dirPrincipal, mapCreaArch);
					
					if(!mapResult.isEmpty()) {
						if(mapResult.get("msgVF") != null) return mapResult;
					}
					if (psArchGenerados.equals("")) {
						psArchGenerados = mapCreaArch.get("nomArchivo").toString().substring(0, 8);
					} else {
						psArchGenerados +=  "','"+mapCreaArch.get("nomArchivo").toString().substring(0, 8) ;
					}
					
					
					if(!pbControl_archivo) {
						//Grabar los datos en arch_transfer
						resDetArchTrans = layoutsDao.insertArchTransfer(mapCreaArch.get("nomArchivo").toString().substring(0, 8), ConstantesSet.BANAMEX,
								funciones.cambiarFecha(layoutsDao.fechaHoy(), true), encabezado.get(i).getImporte(), piRegistros);
						
						if(resDetArchTrans <= 0) {
							mapResult.put("msgVF", false);
							mapResult.put("msgUsuario", "No inserto el registro en arch_transfer");
							return mapResult;
						}
					}
				}
				if(mapResult.get("msgVF") == null) {
					if(!psFoliosIn.equals("")) {
						if(envioTransferenciasADao.actulizaMovimientosTefAgrupados( psArchGenerados, "TEF") <= 0) {
							mapResult.put("msgUsuario", "Error al Actualizar estatus de los movimientos de Transferencias es necesario volver a generar el archivo");
							/*EnvioTransferenciasBusiness envioTransferenciasBusiness = new EnvioTransferenciasBusiness();
							envioTransferenciasBusiness.eliminarArchivosBe(dirPrincipal, mapCreaArch.get("psBanco").toString(), psArchGenerados);*/
							mapResult.put("msgVF", false);
							return mapResult;
						}
						mapResult.put("msgUsuario", "Los Registros han sido Guardados en el archivo " +
								mapCreaArch.get("psBanco").toString() + mapCreaArch.get("nomArchivo").toString());
					}
					mapResult.put("estatusAv", true);
					mapResult.put("nombreArchivo", mapCreaArch.get("nomArchivo").toString());
				}
			}catch(Exception e) {
				e.printStackTrace();
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:beArmaBanamexTEF");
			}
			return mapResult;
		}
		//actualizado A.A.G numero dos ok
		public Map<String, Object> beEncabezadoBanamexTEFAgrupado(Map<String, Object> mapCreaArch, String psNomEmpresa, List<MovimientoDto> encabezado,
											 int i, String psSucursal, boolean pbCLABE, boolean pbInterbancario, String dirPrincipal) {
			Map<String, Object> result = new HashMap<String, Object>();
			String sRegistro = "";
			String sCampo = "";
			
			try {
				//Version C de Banamex TEF solo para Interbancarios
				//Registro de Control
				sRegistro = "1";																//Registro de Control 9 (01)
				sCampo = "" + encabezado.get(i).getNoContrato();
				sRegistro += funciones.ponerCeros(sCampo.substring(0, sCampo.indexOf(".")), 12);	//No Cliente 9 (12)	//gobjVarGlobal.valor_Configura_set(208)), String(12, "0"))
				sRegistro += validaFormato(layoutsDao.fechaHoy(), "ddMMyy");					//Fecha de pago 9 (06)
				sCampo = "" + layoutsDao.folioRealTEF(encabezado.get(i).getIdContrato());		//gobjVarGlobal.Folio_Real("TEF_dia")  'String(4, "X")
				
				if(Integer.parseInt(sCampo) == -1) {
					result.put("msgVF", false);
					result.put("msgUsuario", "No se encontrn el folio <<TEF_dia>>");
					return result;
				}
				if(Integer.parseInt(sCampo) > 99) {
					result.put("msgVF", false);
					result.put("msgUsuario", "No puede haber mas de 99 archivos secuenciales Banamex TEF por dna");
					return result;
				}
				sRegistro += funciones.ajustarLongitudCampo(sCampo, 4, "D", "", "0");			//Secuencial del Archivo 9 (04)
				sRegistro += funciones.quitarCaracteresRaros(funciones.ajustarLongitudCampo(psNomEmpresa.trim(), 36, "I", "", ""), true);	//Nombre de la empresa X (36)
				sRegistro += funciones.ajustarLongitudCampo(sCampo, 20, "I", "", "");			//Descripcion del Archivo X (20)
				
				if(!pbInterbancario)
					sCampo = "06";												//Pago a Proveedores mismo banco
				else 
					if(pbCLABE) 
						sCampo = "12";													//Pago Interbancario
					else 
						sCampo = "07";																//Pago Interbancario
				
				sRegistro += sCampo;															//Naturaleza del Archivo 9 (02)
				sRegistro += funciones.ajustarLongitudCampo("", 40, "D", "", " ");				//Instrucciones para ordenes de pago X (40)
				
				if(pbInterbancario)
					sCampo = "C";
				else 
					sCampo = "B";
				
				sRegistro += sCampo;															//Version layout X (01) version C o B
				sRegistro += "0";																//Volumen 9 (01) primer diskette = 0
				sRegistro += "1";																//Caracteristicas del archivo 9 (01) 0 = Modificable, 1 = Solo lectura
				result = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal, mapCreaArch.get("psBanco").toString(),
						   mapCreaArch.get("nomArchivo").toString(), sRegistro);
				if(!(Boolean)result.get("resExito")) { //cambiar
					result.put("msgVF", false);
					result.put("msgUsuario", result.get("msgUsuario").toString());
					return result;
				}
				
				//Registro Global
				sRegistro = "2";																//Tipo registro 9(01) 2 = Registro global
				sRegistro += "1";																//Tipo operacion 0 = Abono, 1 = Cargo
				sRegistro += "001";																//Clave de la moneda 9 (03) 001 = pesos MN
				sRegistro += funciones.ajustarLongitudCampo(validaFormato("" + encabezado.get(i).getImporte(), "999"), 18, "D", "", "0");	//Importe a abonar o cargar 9(16)V9(02)
				sRegistro += "01";																//Tipo de cuenta 9(02) 01=cheques
				sRegistro += funciones.ajustarLongitudCampo(psSucursal, 4, "D", "", "0");		//Sucursal 9(04)
				sRegistro += funciones.ajustarLongitudCampo(encabezado.get(i).getIdChequera().substring(encabezado.get(i).getIdChequera().length() - 7), 20, "D", "", "0") ;	//Numero de Cuenta 9(20)
				sRegistro += funciones.ajustarLongitudCampo("", 20, "D", "", " ");				//Espacio en blanco X(20)
				result = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal, mapCreaArch.get("psBanco").toString(),
						   mapCreaArch.get("nomArchivo").toString(), sRegistro);
				if(!(Boolean)result.get("resExito")) {
					result.put("msgVF", false);
					result.put("msgUsuario", result.get("msgUsuario").toString());
					return result;
				}
				
			}catch(Exception e) {
				e.printStackTrace();
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:beEncabezadoBanamexTEF");
			}
			return result;
		}
		//actualizado A.A.G
		public Map<String, Object> beTrailBanamexTEFAgrupado(double dImporte, int piRegistros, String dirPrincipal, Map<String, Object> mapCreaArch) {
			Map<String, Object> result = new HashMap<String, Object>();
			String sRegistro = "";
			
			try {
				//Registro de Totales
				sRegistro = "4";									//Registro de Control 9(01) 4 = Registro de totales
				sRegistro += "001";									//Clave de la moneda 9(03)  001 = MN
				sRegistro += funciones.ajustarLongitudCampo("" + piRegistros, 6, "D", "", "0");			//Numero de Abonos 9(06)
				sRegistro += funciones.ajustarLongitudCampo(validaFormato("" + dImporte, "999"), 18, "D", "", "0");		//Importe total de Abonos 9(16)V9(02)
				sRegistro += funciones.ajustarLongitudCampo("1", 6, "D", "", "0");			//Numero de cargos 9(06)
				sRegistro += funciones.ajustarLongitudCampo(validaFormato("" + dImporte, "999"), 18, "D", "", "0");		//Importe total de Cargos 9(16)V9(02)
				result = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal, mapCreaArch.get("psBanco").toString(),
						   mapCreaArch.get("nomArchivo").toString(), sRegistro);
				if(!(Boolean)result.get("resExito")) {
					result.put("msgVF", false);
					result.put("msgUsuario", "Error al Escribir Registro de Totales en el Archivo Banamex TEF!!"); //aqui
					return result;
				}
				
			}catch(Exception e) {
				e.printStackTrace();
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:beTrailBanamexTEF");
			}
			return result;
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
	
	
	public CreacionArchivosBusiness getCreacionArchivosBusiness() {
		return creacionArchivosBusiness;
	}
	public void setCreacionArchivosBusiness(
			CreacionArchivosBusiness creacionArchivosBusiness) {
		this.creacionArchivosBusiness = creacionArchivosBusiness;
	}
	public Map<String, Object> beArmaBital(String sDescBanco, String sNomFoliador, String sIndiceArch, List<MovimientoDto> listBus) {
		Object mapRetorno = null;
		Map<String, Object> retorno = new HashMap<String, Object>();
		retorno.put("mapRetorno", mapRetorno);
		
		return retorno;
		
	}
	public Map<String, Object> beArchivoBital(ParametrosLayoutDto obtenerParametrosLayout, int i, String dirPrincipal) {
		// TODO Auto-generated method stub
		return null;
	}
	public String beArmaH2HSantanderChequeOcurre(MovimientoDto movimientoDto, int i, double impTotDet, int j) {
		String sCampo;
		StringBuffer sRegistro = new StringBuffer();
		List<ParametrosLayoutDto> lista = new ArrayList<ParametrosLayoutDto>();
		String fechaHoy = layoutsDao.fechaHoy().replace("-", "");
		
		try {
			if(i == 0) {
				//Registro de encabezado
				sRegistro.append("01");																				//1. Tipo de registro N(2)
				sRegistro.append("0000001");																		//2. Nnmero de registro N(7)
				sRegistro.append("60");																				//3. Tipo de operacinn N(2)
				sRegistro.append("014");																			//4. Banco pagador N(3)
				sRegistro.append("E");																				//5. Tipo de archivo Entrada al banco o Salida al cliente AN(1)
				sRegistro.append("2");																				//6. Tipo de servicio N(1)
				
				sCampo = fechaHoy.substring(fechaHoy.length()-2, fechaHoy.length());								//Aqun solo tomara los dias en nnmero
				sRegistro.append(sCampo + funciones.ajustarLongitudCampo(Integer.toString(1), 5, "D", "", "0"));	//7. Nnmero de bloque N(7)
				
				sRegistro.append(fechaHoy);																			//8. Fecha de creacinn AAAAMMDD N(8)
				
				sRegistro.append("01");																				//9. Tipo de divisa N(2)
				sRegistro.append("00");																				//10. Causa de rechazo N(2)
				sRegistro.append("1");																				//11. Programado de pago 1 = mismo dia, 2 = programado N(1)
				sRegistro.append(funciones.ajustarLongitudCampo("", 40, "D", "", " "));								//12. Uso futuro N(40)
				sRegistro.append(funciones.ajustarLongitudCampo("", 406, "D", "", " "));							//13. Uso futuro N(406)
				sRegistro.append("\r\n");			
				//sRegistro.append("\n\r\n\r");																		//Salto de linea que no esta en el layout pero es para pintarlo de una sola pasada
			}
			
			//Registros del detalle
			sRegistro.append("02");																				//1. Tipo de registro detalle N(2)
			sRegistro.append(funciones.ajustarLongitudCampo(Integer.toString(i+2), 7, "D", "", "0"));			//2. Nnmero de registro con incremento N(7)
			sRegistro.append("60");																				//3. Tipo de operacinn N(2)
			sRegistro.append("01");																				//4. Tipo de divisa N(2)
			
			sRegistro.append(fechaHoy);																			//5. Fecha de transferencia AAAAMMDD N(8)
			
			sRegistro.append("014");																			//6. Banco presentador N(3)
			sRegistro.append(funciones.ajustarLongitudCampo("000", 3, "D", "", "0"));			//7. Banco receptor N(3)
			
			sCampo = funciones.ponerFormatoCeros(movimientoDto.getImporte(), 15).replace(".", "");
			sRegistro.append(funciones.ajustarLongitudCampo(sCampo, 15, "D", "", "0"));							//8. Importe N(15)
			
			sRegistro.append(funciones.ajustarLongitudCampo("", 16, "D", "", " "));								//9. Uso futuro N(16)
			
			/*if(Integer.parseInt(dto.getIdBancoBenef()) == 14) {
				if (nomina) sRegistro.append("99");
				else sRegistro.append("98");																			//10. Tipo de operacinn N(2)
			} else {
				if (nomina) sRegistro.append("02");*/
				//else 
			sRegistro.append("80");																		//10. Tipo de operacinn N(2)
			//}
			sRegistro.append(fechaHoy);																			//11. Fecha de aplicacinn N(8)
			sRegistro.append("01");																				//12. Tipo cta. ordenante N(2)
			sRegistro.append(funciones.ajustarLongitudCampo(movimientoDto.getIdChequera(), 20, "D", "", "0"));			//13. Cta. ordenante N(20)
			
			lista = layoutsDao.buscaNomEmpresa(movimientoDto.getNoFolioDet());
			
			if(lista.size() > 0) {
				sRegistro.append(funciones.ajustarLongitudCampo(funciones.quitaCaracteresEspeciales(lista.get(0).getNomEmpresa()), 40, "I", "", " "));//14. Nombre del ordenante (40)
				sRegistro.append(funciones.ajustarLongitudCampo(lista.get(0).getRfc(), 18, "I", "", " "));		//15. RFC del ordenante (18)
			}
			
			//if(Integer.parseInt(dto.getIdBancoBenef()) == 14) {
				sRegistro.append("00");																			//16. Tipo cta. receptor N(2)
				sRegistro.append(funciones.ajustarLongitudCampo(0 + "", 20, "D", "", "0"));	//17. Cta. receptor N(20)
			/*}else {
				if (dto.getPsClabe() == null || movimientoDto.getPsClabe().equals("")) {
					return "";
				}
				sRegistro.append("40");																			//16. Tipo cta. receptor N(2)
				sRegistro.append(funciones.ajustarLongitudCampo(dto.getPsClabe(), 20, "D", "", "0"));			//17. Clabe bancarna receptor N(20)
			}*/
			sRegistro.append(funciones.ajustarLongitudCampo(funciones.quitaCaracteresEspeciales(movimientoDto.getBeneficiario()), 40, "I", "", " "));			//18. Beneficiario N(40)
			sRegistro.append(funciones.ajustarLongitudCampo(movimientoDto.getRfcBenef(), 18, "I", "", " "));					//19. RFC Beneficiario N(18)
			sRegistro.append(funciones.ajustarLongitudCampo("", 40, "I", "", " "));//20. Referencia Servicio Emisor. N(40)
			sRegistro.append(funciones.ajustarLongitudCampo("", 40, "I", "", " "));			//21. Nombre Titular Servicio. N(40)
			sRegistro.append(funciones.ajustarLongitudCampo("0", 15, "D", "", "0"));							//22. Importe IVA de Operacinn N(15)
			sRegistro.append("0000001");																		//23. Referencia Numero de Ordenante. Valor asignado por Santander N(7)
			sRegistro.append(funciones.ajustarLongitudCampo(movimientoDto.getConcepto(), 40, "I", "", " "));	//24. Referencia Leyenda del Ordenante N(40)
			sRegistro.append(funciones.ajustarLongitudCampo("06", 30, "I", "", " "));								//25. Clave de Rastreo N(30)
			sRegistro.append("00");																				//26. Motivacion de Devolucion N(2)
			sRegistro.append(fechaHoy);																			//27. Fecha de Presentacion Inicial AAAAMMDD N(8)
			sRegistro.append(funciones.ajustarLongitudCampo("C", 1, "I", "", " "));								//28. Uso Futuro N(12)
			sRegistro.append(funciones.ajustarLongitudCampo("9999", 4, "I", "", " "));							//29. Referencia Cliente. N(30)
			sRegistro.append(funciones.ajustarLongitudCampo("C", 1, "I", "", " "));				//30. Descripcinn Referencia Pago N(30)
			sRegistro.append(funciones.ajustarLongitudCampo("A", 1, "I", "", " "));				//31. Accion de la orden
			sRegistro.append(funciones.ajustarLongitudCampo("30", 3, "I", "", " "));				//32. Vigencia
			sRegistro.append(funciones.ajustarLongitudCampo("", 2, "I", "", " "));				//33. Uso futuro zona
			sRegistro.append(funciones.ajustarLongitudCampo(movimientoDto.getNoFolioDet() + "", 30, "I", "", " "));				//34. Referencia (Clave del beneficiario)
			sRegistro.append("\r\n");																				//Salto de linea que no esta en el layout pero es para pintarlo de una sola pasada
			
			if((i+1) == j) {	
				//Registro de Sumario	
				//sRegistro.append("");		
				sRegistro.append("09");																			//01. Tipo de Registro N(2)
				sRegistro.append(funciones.ajustarLongitudCampo(Integer.toString(i+3), 7, "D", "", "0"));		//02. Numeo de Secuencia N(7)
				sRegistro.append("60");																			//03. Codigo de Operacinn N(2)
				
				sCampo = fechaHoy.substring(fechaHoy.length()-2, fechaHoy.length());							//Aqun solo tomara los dias en nnmero
				sRegistro.append(sCampo + funciones.ajustarLongitudCampo(Integer.toString(1), 5, "D", "", "0"));//04. Nnmero de bloque N(7)
				sRegistro.append(funciones.ajustarLongitudCampo(Integer.toString(i+1), 7, "D", "", "0"));		//05. Numero de Operaciones N(7)
				
				sCampo = funciones.ponerFormatoCeros(impTotDet, 18).replace(".", "");
				sRegistro.append(funciones.ajustarLongitudCampo(sCampo, 18, "D", "", "0"));						//06. Importe Total Operaciones N(18)
				sRegistro.append(funciones.ajustarLongitudCampo("", 40, "D", "", " "));							//07. Uso Futuro CCEN N(40)
				sRegistro.append(funciones.ajustarLongitudCampo("", 399, "D", "", " "));						//08. Uso Futuro Banco N(399)
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:beArmaH2HSantander");
			e.printStackTrace();
		}
		System.out.print(sRegistro.toString());
		return sRegistro.toString();
	}

	public String beArmaSantanderTarjetaDebito(MovimientoDto movimientoDto) {
		StringBuffer result = new StringBuffer();
		String fechaHoy = layoutsDao.fechaHoy().replace("-", "");
		String sCampo = "";
		
		result.append(funciones.ajustarLongitudCampo(movimientoDto.getIdChequera(), 11, "I", "", " "));		//Cuenta de cargo(11)
		result.append(funciones.ajustarLongitudCampo("", 5, "I", "", " "));									//Espacio en blanco(5)
		result.append(funciones.ajustarLongitudCampo(movimientoDto.getIdChequeraBenef(), 16, "I", "", " "));//Tarjeta de abono(16)
		sCampo = funciones.ponerFormatoCeros(movimientoDto.getImporte(), 18).replace(".", "");
		result.append(funciones.ajustarLongitudCampo(sCampo, 13, "D", "", "0"));							//Importe(13)
		result.append(funciones.ajustarLongitudCampo(movimientoDto.getConcepto(), 40, "I", "", " "));		//Concepto(40)
		result.append(fechaHoy);																			//Fecha de aplicación(8)
		result.append("\r\n");																				//Salto de linea que no esta en el layout pero es para pintarlo de una sola pasada
		
		return result.toString();
	}
	public String beArmaSantanderChequeOcurre(MovimientoDto movimientoDto, int optSantanderDirecto) {
        String fechaHoy = layoutsDao.fechaHoy().replace("-", "/");
        String psRegistro;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MM-yyyy");
        String fechaSalida="";
        Calendar c ;
        Date date ;
		String psCampo;	
	    psCampo = "";
	    psRegistro = "";
	    try {
			date = formatter.parse(layoutsDao.fechaHoy());
			System.out.println(date);
		    System.out.println(formatter.format(date));
		    fechaHoy=formatter2.format(date);
		} catch (java.text.ParseException e1) {
			// TODO Auto-generated catch block
			e1.toString();
		}
       
	    //BE_arma_santander_Cheque_Ocurre_Pago_directo = ""
	    psCampo =movimientoDto.getIdChequera();
	    psCampo = funciones.ajustarLongitudCampo(psCampo, 16, "I", "", "");
	    psRegistro+= psCampo;//"1. Cuenta de Cargo N(16) 1-16";
	    psCampo =""+movimientoDto.getNoFolioDet();
	    psCampo = funciones.ajustarLongitudCampo(psCampo, 7,"D", "", "0");
	    psRegistro+= psCampo;//"2. Numero de orden N(7) 17-23";
	    psCampo = fechaHoy.replace("-","");
	    psCampo = funciones.ajustarLongitudCampo(psCampo,10,"I","","");
	    psRegistro+= psCampo;//"3. FEcha de libramiento FECHA(10) DD/MM/YYYY 24-33";
	    //ACA VALIDAR QUE A LA FECHA RESULTANTE SEA DIA HABIL, SI NO, BUSCAR EL DIA HABIL ANTERIOR INMEDIATO
         formatter = new SimpleDateFormat("yyyy-MM-dd");
         formatter2 = new SimpleDateFormat("dd-MM-yyyy");
         fechaSalida="";
        try {

            date = formatter.parse(layoutsDao.fechaHoy());
            System.out.println(date);
            System.out.println(formatter.format(date));
			c = Calendar.getInstance();
			c.setTime(date); // Now use today date.
			c.add(Calendar.DATE, 30); // Adding 5 days
			fechaSalida = formatter.format(c.getTime());
			System.out.println(fechaSalida);
        } catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
        	fechaSalida="";
			return "error";
		}
        c.get(Calendar.DAY_OF_WEEK);
        boolean feriado=layoutsDao.diaFeriado(fechaSalida);
        System.out.println(c.get(Calendar.DAY_OF_WEEK));
        if(c.get(Calendar.DAY_OF_WEEK)!=7&&c.get(Calendar.DAY_OF_WEEK)!=1&&!feriado){
        	System.out.println("el dia no es feriado"+fechaSalida);
        }
        else{
        	for(int i=1;i<5;i++){
        		try{
	                date = formatter.parse(fechaSalida);
	                System.out.println(date);
	                System.out.println(formatter.format(date));
	    			c = Calendar.getInstance();
	    			c.setTime(date); // Now use today date.
	    			c.add(Calendar.DATE,-i); // Adding 5 days
	    			fechaSalida = formatter.format(c.getTime());
	    			System.out.println(fechaSalida);
	            } catch (java.text.ParseException e) {
	    			// TODO Auto-generated catch block
	            	fechaSalida="";
	    			return "error";
	    		}
        		feriado=layoutsDao.diaFeriado(fechaSalida);
                if(c.get(Calendar.DAY_OF_WEEK)!=7&&c.get(Calendar.DAY_OF_WEEK)!=1&&!feriado){
                	i=5;
                	fechaSalida=formatter2.format(c.getTime());
                }
        	}
        }
        if(fechaSalida!=""){
        	psCampo=fechaSalida.replace("-", "");
        	psCampo=funciones.ajustarLongitudCampo(psCampo, 10, "I", "", "");
        	psRegistro+=psCampo;//'4. Fecha limite de pago FECHA(10) DD/MM/YYYY 34-43
        	psCampo+="             "+movimientoDto.getNoCliente();
        	psCampo=funciones.ajustarLongitudCampo(psCampo, 13,"I","","");
        	psRegistro+=psCampo;//'5. CLAVE del Beneficiario A(13) 44-56
        	psCampo=movimientoDto.getBeneficiario();
        	psCampo=funciones.ajustarLongitudCampo(psCampo, 60, "I", "","");
        	psRegistro+=psCampo;//'6. Nombre del Beneficiario A(60) 57-116
        	psRegistro+="S    ";//7. Clave de todas las sucursales A(1) "S" 117-117
        	psRegistro+="C";//9. Tipo de pago A(1) "C" Cheque de caja, "E" efectivo  122-122
        	psCampo=""+movimientoDto.getImporte()*100;
        	psCampo=funciones.ajustarLongitudCampo(psCampo, 16, "D", "","0");
        	psRegistro+=psCampo;//'10. Importe N(16) 123-138
        	psCampo=movimientoDto.getConcepto();
        	psCampo=funciones.ajustarLongitudCampo(psCampo, 60, "I","","");
        	psRegistro+=psCampo;//11. Concepto A(60)  139-198
        }
        else{
        	psRegistro="";
        }
	    return psRegistro;
	}
	
	public Map<String, Object> obtenerReferencias(int noPersona, int noFolioDet) {
		Map<String, Object> referencias = new HashMap<String, Object>();
		String campo = layoutsDao.obtenerCampoReferencia(noPersona);
		switch (campo) {
			case "":
				if (layoutsDao.verificaDefault(noPersona)) {
					Map<String, Object> referenciasDefault = layoutsDao.obtieneReferenciasDefault(noPersona);
					referencias.put("refA", referenciasDefault.get("refA"));
					referencias.put("refN", referenciasDefault.get("refN"));
				} else {
					referencias.put("refA", "");
					referencias.put("refN", noFolioDet);
				}
				break;
			case "concepto y no_factura":
				referencias.put("refA", layoutsDao.obtieneReferenciaMov(campo, noFolioDet).get("refA"));
				referencias.put("refN", layoutsDao.obtieneReferenciaMov(campo, noFolioDet).get("refN"));		
				break;
			case "concepto":
				referencias.put("refA", layoutsDao.obtieneReferenciaMov(campo, noFolioDet).get("refA"));
				referencias.put("refN", layoutsDao.obtieneReferenciaMov(campo, noFolioDet).get("refN"));
				break;
			case "no_factura":
				referencias.put("refA", layoutsDao.obtieneReferenciaMov(campo, noFolioDet).get("refA"));
				referencias.put("refN", layoutsDao.obtieneReferenciaMov(campo, noFolioDet).get("refN"));
				break;
			case "referencia":
				referencias.put("refA", layoutsDao.obtieneReferenciaMov(campo, noFolioDet).get("refA"));
				referencias.put("refN", layoutsDao.obtieneReferenciaMov(campo, noFolioDet).get("refN"));
				break;
			case "no_docto":
				referencias.put("refA", layoutsDao.obtieneReferenciaMov(campo, noFolioDet).get("refA"));
				referencias.put("refN", layoutsDao.obtieneReferenciaMov(campo, noFolioDet).get("refN"));
				break;
			default:
				referencias.put("refN", campo);
				referencias.put("refA", noFolioDet);
		}
		return referencias;
		
	}
	/*public double obtenerImporte(String string) {
		try {
		if (string!="") {
			
	   	}
	} catch (Exception e) {
			// TODO: handle exception
		}
		
		//sCampo  = layoutsDao
		return 0;
	}*/
	
	
	//Banamex CitibankPaylinkMN 485
			public String beArmaCitibankPaylinkMNChequeOcurre(List<MovimientoDto> listGrid, int i, String psReferenciaTraspaso) {
				Map<String, Object> valorPagRef = new HashMap<String, Object>();
			    String psRegistro = "";
			    String psCampo = "";
				try {
					
					System.out.println("entra a ver el folio .:: angel");
					//Citibank PayLink Plus 1024 para Pago Mismo dia SOLO MXP
					psRegistro = "PAY";														//1 Record Type (02)
					psRegistro += "485";													//2 Costumer Country Code (03) - 484 = Mexico
					
					//MS: psCampo = listGrid.get(i).getIdChequera().substring(listGrid.get(i).getIdChequera().length() - 10);
					//MS: psRegistro += psCampo;													//3 Customer Account Number (10)
					psRegistro += funciones.ajustarLongitudCampo("", 10, "D", "", "");		//MS se agregaron 10 espacios
					
					// "yyMMdd"
					if(layoutsDao.consultarConfiguraSet(1).equals("CCM"))
						psCampo = listGrid.get(i).getFecValorOriginal().toString();
					else
						psCampo = listGrid.get(i).getFecValor().toString();
					if(psCampo.substring(4, 5).equals("-"))
						psCampo = psCampo.substring(2, 4) + psCampo.substring(5, 7) + psCampo.substring(8, 10);
					else{
						psCampo =funciones.cambiarFechaGregoriana(psCampo);
						//psCampo = "17" + psCampo.substring(0, 2) + psCampo.substring(3, 5);
						psCampo = "17" + psCampo.substring(3,5) + psCampo.substring(0,2);

					}
					
					psRegistro += psCampo;													//4 Transaction Date (6)
					
					//If pbChqOcurre = True Then 'ACM 21/12/12 - Se agrega código 073
				    //        psCampo = "073"
			        //ElseIf pbInterbancario = False Then
			        //    psCampo = "072"
			        //Else
			        //    psCampo = "001"
			        //End If
					
				//	psRegistro += "071";													//5 Transaction Code (3)
//					if (listGrid.get(i).getIdBanco() == listGrid.get(i).getIdBancoBenef())
//						psCampo = "072";
//					else
//						psCampo = "001";
					
					psCampo="277";
					psRegistro+=psCampo;
					
					
					if(psReferenciaTraspaso.equals("")) {
						//Solo aplica para Transferencias, Pagos referenciados a Clientes
						//int x = Integer.parseInt()
								System.out.println("parametro1----"+listGrid.get(i).getNoCliente());
								System.out.println("parametro2----"+listGrid.get(i).getNoFolioDet());
						valorPagRef = layoutsDao.obtenerValorPagoReferenciado(Integer.parseInt(listGrid.get(i).getNoCliente()), listGrid.get(i).getNoFolioDet(), "");
		            	psCampo = valorPagRef.get("valorPagRef").toString();
					}else
						psCampo = psReferenciaTraspaso;
					psRegistro += funciones.ajustarLongitudCampo(psCampo, 15, "I", "", "");	//6 Transaction Reference A(15)
					
					layoutsDao.actualizarFolioReal("folio_paylink");
					psCampo = "" + layoutsDao.seleccionarFolioReal("folio_paylink");
					if(Integer.parseInt(psCampo) < 0){
						return "";
					}
						
					
					psRegistro += funciones.ajustarLongitudCampo(psCampo, 8, "D", "", "0");	//7 Transaction Sequence number  N(8)
					psRegistro += funciones.ajustarLongitudCampo("X", 20, "I", "", "");		//8 Beneficiary tax id number N(20), en vb6 pega el rfc
					psRegistro += "MXN";													//9 Customer account Currency Code(3), vb6 si toma dls
					psCampo = funciones.ajustarLongitudCampo(listGrid.get(i).getNoCliente().toString(), 20, "I", "", "");
					psRegistro += psCampo;													//10 Beneficiary Code A(20)
					psCampo = funciones.ponerFormatoCerosSindecimales((listGrid.get(i).getImporte() * 100), 15);
					//psCampo = funciones.ajustarLongitudCampo("" + (listGrid.get(i).getImporte() * 100), 15, "D", "", "0");
					psRegistro += psCampo;													//11 Transaction amount N(15)
					psRegistro += funciones.ajustarLongitudCampo("", 6, "D", "", "");		//12 MAturity Date DATE(6)
					psCampo = funciones.quitarCaracteresRaros(listGrid.get(i).getConcepto(), true);
					psRegistro += funciones.ajustarLongitudCampo(psCampo, 35, "I", "", "");	//13 Transaction Detail Line 1 A(35)
					psRegistro += funciones.ajustarLongitudCampo("", 35, "D", "", "");		//14 Transaction Detail Line 2 A(35), en vb6 pone aqui en nombre de empresa
					psRegistro += funciones.ajustarLongitudCampo("", 35, "D", "", "");		//15 Transaction Detail Line 3 A(35)
					psRegistro += funciones.ajustarLongitudCampo("", 35, "D", "", "");		//16 Transaction Detail Line 4 A(35)
					psRegistro += "05";														//17 Local Transaction Code N(2)
					psRegistro += "01";														//18 COstumer account type N(2)
					psCampo = funciones.quitarCaracteresRaros(listGrid.get(i).getBeneficiario(), true);//quitar
					psCampo = funciones.ocultarCoinversora(psCampo);
					psRegistro += funciones.ajustarLongitudCampo(psCampo, 80, "I", "", "");	//19 Beneficiary name A(80) 
					psRegistro += funciones.ajustarLongitudCampo("X", 35, "I", "", "");		//20 Beneficiary Adress 1 A(35)
					//MS: se 
					psRegistro += funciones.ajustarLongitudCampo("", 35, "D", "", "");		//21 Beneficiary Adress 1 second line A(35)
					psRegistro += funciones.ajustarLongitudCampo("X", 15, "I", "", "");		//22 Beneficiary Adress 2 A(15) ciudad en vb6 aqui se pone el tema de cheque ocurre
					psRegistro += funciones.ajustarLongitudCampo("", 2, "D", "", "");		//23 Beneficiary state A(2)
					psRegistro += funciones.ajustarLongitudCampo("1", 12, "D", "", "0");	//24 Beneficiary adress 3 A(12) Cod. Postal
					psRegistro += funciones.ajustarLongitudCampo("", 16, "D", "", "");		//25 Beneficiary phone number N(16)
					//MS: falta obtener el banco citi

//					if (listGrid.get(i).getIdBanco() == listGrid.get(i).getIdBancoBenef()){ //quitar
//						psCampo ="000";
//					}else{
//						psCampo = funciones.ajustarLongitudCampo(""+listGrid.get(i).getIdBancoBenef(), 3, "D", "", "0");
//
//					}
					psCampo="002";
					psRegistro += psCampo;													//26 Beneficiary Bank number N(3)
					psCampo = funciones.ajustarLongitudCampo("MEXICO", 8, "I", "", "");
					psRegistro += psCampo;													//27 Beneficiary Bank Agency A(8)

//					psCampo = funciones.ajustarLongitudCampo(listGrid.get(i).getClabeBenef(), 35, "I", "", " ");//quitar
					psCampo = funciones.ajustarLongitudCampo("", 35, "I", "", " ");//quitar
					psRegistro += psCampo;													//28 Beneficiary Account Number(35) SOLO INTERMEDIARIOS 
//					psRegistro += "05";	
					psCampo = funciones.ajustarLongitudCampo("", 2, "D", "", " ");//29 Account type CR SOLO INTERBANCARIO N(2), en vb6 si revisa si hay clave o no
					psRegistro += psCampo;
					psCampo = funciones.ajustarLongitudCampo("MEXICO", 30, "I", "", "");	//'30 Dirección del banco A(30)
					psRegistro+=psCampo;
					psRegistro += funciones.ajustarLongitudCampo("", 17, "D", "", "");		//'31 Importe del impuesto N(17)
					psRegistro += "N";														// '32 Bandera de prioridad A(01) N= no se usa prioridad ATene16
					psRegistro += "N";														// '33 Confidencial A(01) N= no se usa confidencial ATene16
					//psRegistro += psCampo;													//30 Beneficiary Bank Adress A(30)
				//	psRegistro += "01";														//'34 Fecha de acreditación al beneficiario N(02)
					psCampo = funciones.ajustarLongitudCampo("", 2, "D", "", " ");//'34 Fecha de acreditación al beneficiario N(02)
					psRegistro += psCampo;
					psCampo = listGrid.get(i).getIdChequera();
					psRegistro += funciones.ajustarLongitudCampo(psCampo, 20, "I", "", "");//35 Número de cuenta de débito banamex N(20)
					//psRegistro += psCampo;//35 Número de cuenta de débito banamex N(20)
					System.out.println("numero de cuenta de debito banam tamaño "+psCampo.length());
					psRegistro += funciones.ajustarLongitudCampo("", 16, "D", "", " ");		//36 Beneficiary Fax Number N(16)
					psRegistro += funciones.ajustarLongitudCampo("", 20, "D", "", " ");		//37 Beneficiary Fax Contact Name A(20)
					psRegistro += funciones.ajustarLongitudCampo("", 15, "D", "", " ");		//38 Beneficiary Fax Department Name A(15)
					psRegistro += funciones.ajustarLongitudCampo("", 10, "D", "", "0");		//39 Beneficiary Account number N(10) SOLO MISMO BANCO
					psRegistro += funciones.ajustarLongitudCampo("", 2, "D", "", " ");		//40 Account Type CR N(2) SOLO MISMO BANCO
		//			psRegistro += funciones.ajustarLongitudCampo("", 3, "D", "", " ");		//41 Método de entrega de transacciones N(03)
					psRegistro +="003";
					psRegistro += funciones.ajustarLongitudCampo("", 50, "D", "", " ");		//42 Collection Title ID A(50)
					psRegistro += funciones.ajustarLongitudCampo("", 5, "D", "", "0");		//43 Beneficiary Activity code N(5)
					psRegistro += funciones.ajustarLongitudCampo("", 50, "D", "", " ");		//44 Beneficiary e-mail adress A(50)
//					psCampo = funciones.ajustarLongitudCampo("" + (listGrid.get(i).getImporte() * 100), 15, "D", "", "0");
//					psCampo = funciones.ponerFormatoCerosSindecimales((listGrid.get(i).getImporte() * 100), 15);
					//psRegistro += psCampo;//45 Maximum payment amount N(15)
					psRegistro += "999999999999999";//45 Maximum payment amount N(15)
					psRegistro += funciones.ajustarLongitudCampo("", 1, "D", "", "");		//46 Update Type N(1)
					psRegistro += funciones.ajustarLongitudCampo("NONE", 11, "I", "", " ");		//47 Check Number N(11)
					psRegistro += funciones.ajustarLongitudCampo("", 1, "D", "", "");		//48 Printed Check A(1)
					psRegistro += funciones.ajustarLongitudCampo("", 1, "D", "", "");		//49 Match Paid A(1)
					psRegistro += funciones.ajustarLongitudCampo("", 253, "D", "", "");		//50 Blank Characteres A(254)
					
				}catch(Exception e) {
					bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:beArmaCitibankPaylinkMN");
					e.printStackTrace();
					return "";
				}
				return psRegistro;
			}
			
			////layout para HoNDA
	
			public String beArmaCitibankPaylinkMN485(List<MovimientoDto> listGrid, int i, String psReferenciaTraspaso) {
				Map<String, Object> valorPagRef = new HashMap<String, Object>();
			    String psRegistro = "";
			    String psCampo = "";
				try {
					System.out.println("tamaño de listGrid "+listGrid.size());
					System.out.println("parametro i:.. "+listGrid.size());
					//for(int i=0;i<listGrid.size();i++){
						System.out.println("entra a ver el folio .:: angel");
						//Citibank PayLink Plus 1024 para Pago Mismo dia SOLO MXP
						psRegistro = "PAY";														//1 Record Type (02)
						psRegistro += "485";													//2 Costumer Country Code (03) - 484 = Mexico
						
						//MS: psCampo = listGrid.get(i).getIdChequera().substring(listGrid.get(i).getIdChequera().length() - 10);
						//MS: psRegistro += psCampo;													//3 Customer Account Number (10)
						psRegistro += funciones.ajustarLongitudCampo("", 10, "D", "", "");		//MS se agregaron 10 espacios
						
						// "yyMMdd"
						if(layoutsDao.consultarConfiguraSet(1).equals("CCM"))
							psCampo = listGrid.get(i).getFecValorOriginal().toString();
						else
							psCampo = listGrid.get(i).getFecValor()+"";
							String f=funciones.ponerFechaSola(listGrid.get(i).getFecValor());
						
						if(psCampo.substring(4, 5).equals("-"))
							psCampo = psCampo.substring(2, 4) + psCampo.substring(5, 7) + psCampo.substring(8, 10);
						else{
							psCampo =funciones.cambiarFechaGregoriana(psCampo);
							//psCampo = "17" + psCampo.substring(0, 2) + psCampo.substring(3, 5);
							psCampo = "17" + psCampo.substring(3,5) + psCampo.substring(0,2);

						}
						
						psRegistro += psCampo;													//4 Transaction Date (6)
						
						//If pbChqOcurre = True Then 'ACM 21/12/12 - Se agrega código 073
					    //        psCampo = "073"
				        //ElseIf pbInterbancario = False Then
				        //    psCampo = "072"
				        //Else
				        //    psCampo = "001"
				        //End If
						
						//psRegistro += "071";													//5 Transaction Code (3)
						if (listGrid.get(i).getIdBanco() == listGrid.get(i).getIdBancoBenef())
							psCampo = "072";
						else
							psCampo = "001";
						psRegistro+=psCampo;
						
					//	System.out.println("PSrEFERENCIA "+psReferenciaTraspaso);
						if(psReferenciaTraspaso.equals("")) {
							//Solo aplica para Transferencias, Pagos referenciados a Clientes
							//int x = Integer.parseInt()
									System.out.println("parametro1----"+listGrid.get(i).getNoCliente());
									System.out.println("parametro2----"+listGrid.get(i).getNoFolioDet());
							valorPagRef = layoutsDao.obtenerReferenciaHonda(Integer.parseInt(listGrid.get(i).getNoCliente()), listGrid.get(i).getNoFolioDet(), listGrid.get(0).getConcepto());
			            	System.out.println("referencia1 "+valorPagRef.get("valorPagRef").toString());
			      
							psCampo=valorPagRef.get("valorPagRef").toString();
			            	
						}else{
							psCampo = psReferenciaTraspaso;
						}
							
						psRegistro += funciones.ajustarLongitudCampo(funciones.quitarCaracteresRaros(psCampo,true), 15, "I", "", "");	//6 Transaction Reference A(15)
						
						layoutsDao.actualizarFolioReal("folio_paylink");
						psCampo = "" + layoutsDao.seleccionarFolioReal("folio_paylink");
						if(Integer.parseInt(psCampo) < 0){
							return "";
						}
							
						
						psRegistro += funciones.ajustarLongitudCampo(psCampo, 8, "D", "", "0");	//7 Transaction Sequence number  N(8)
						//System.out.println("rfc "+listGrid.get(i).getRfcBenef()+" tam "+listGrid.get(i).getRfcBenef().length());
						psRegistro += funciones.ajustarLongitudCampo(listGrid.get(i).getRfcBenef()!=null? listGrid.get(i).getRfcBenef() : "", 20, "I", "", "");		//8 Beneficiary tax id number N(20), en vb6 pega el rfc
						//					psRegistro += funciones.ajustarLongitudCampo("X", 20, "I", "", "");		//8 Beneficiary tax id number N(20), en vb6 pega el rfc
						psRegistro += "MXN";
						psRegistro +=funciones.ajustarLongitudCampo("0", 20, "I", "", "");//9 Customer account Currency Code(3), vb6 si toma dls
				//		psCampo = funciones.ajustarLongitudCampo(listGrid.get(i).getNoCliente().toString(), 20, "I", "", "");
				//		psRegistro += psCampo;													//10 Beneficiary Code A(20)
						psCampo = funciones.ponerFormatoCerosSindecimales((listGrid.get(i).getImporte() * 100), 15);
						System.out.println("importe485 "+listGrid.get(i).getImporte());
						//psCampo = funciones.ajustarLongitudCampo("" + (listGrid.get(i).getImporte() * 100), 15, "D", "", "0");
						psRegistro += psCampo;													//11 Transaction amount N(15)
					
						// "yyMMdd"
						if(layoutsDao.consultarConfiguraSet(1).equals("CCM"))
							psCampo = listGrid.get(i).getFecValorOriginal().toString();
						else
							psCampo = listGrid.get(i).getFecValor()+"";
						
						if(psCampo.substring(4, 5).equals("-"))
							psCampo = psCampo.substring(2, 4) + psCampo.substring(5, 7) + psCampo.substring(8, 10);
						else{
							psCampo =funciones.cambiarFechaGregoriana(psCampo);
							//psCampo = "17" + psCampo.substring(0, 2) + psCampo.substring(3, 5);
							psCampo = "17" + psCampo.substring(3,5) + psCampo.substring(0,2);
						}
						psRegistro += psCampo;//12 MAturity Date DATE(6)
						if(psReferenciaTraspaso.equals("")) {
							//Solo aplica para Transferencias, Pagos referenciados a Clientes
						
							valorPagRef = layoutsDao.obtenerReferenciaHonda(Integer.parseInt(listGrid.get(i).getNoCliente()), listGrid.get(i).getNoFolioDet(),listGrid.get(i).getConcepto());
							psRegistro += funciones.ajustarLongitudCampo(funciones.quitarCaracteresRaros(valorPagRef.get("valorPagRef1").toString()!=null? valorPagRef.get("valorPagRef1").toString() : "",true), 35, "I", "", "");	//13 Transaction Detail Line 1 A(35)
							//System.out.println("valor ref 2 "+valorPagRef.get("valorPagRef2").toString());
							psRegistro += funciones.ajustarLongitudCampo(funciones.quitarCaracteresRaros(valorPagRef.get("valorPagRef2").toString()!=null? valorPagRef.get("valorPagRef2").toString() : "",true), 35, "I", "", "");		//14 Transaction Detail Line 2 A(35), en vb6 pone aqui en nombre de empresa
							psRegistro += funciones.ajustarLongitudCampo(funciones.quitarCaracteresRaros(valorPagRef.get("valorPagRef3").toString()!=null? valorPagRef.get("valorPagRef3").toString() : "",true), 35, "I", "", "");		//15 Transaction Detail Line 3 A(35)
			            	
						}else{
							psRegistro += funciones.ajustarLongitudCampo(funciones.quitarCaracteresRaros(listGrid.get(i).getConcepto(),true), 35, "I", "", "");	//13 Transaction Detail Line 1 A(35)
							psRegistro += funciones.ajustarLongitudCampo("", 35, "I", "", "");		//14 Transaction Detail Line 2 A(35), en vb6 pone aqui en nombre de empresa
							psRegistro += funciones.ajustarLongitudCampo("", 35, "I", "", "");		//15 Transaction Detail Line 3 A(35)
			            	
						}
//						valorPagRef = layoutsDao.obtenerReferenciaHonda(Integer.parseInt(listGrid.get(i).getNoCliente()), listGrid.get(i).getNoFolioDet(),listGrid.get(i).getConcepto());
//						psRegistro += funciones.ajustarLongitudCampo(valorPagRef.get("valorPagRef1").toString()!=null? valorPagRef.get("valorPagRef1").toString() : "", 35, "I", "", "");	//13 Transaction Detail Line 1 A(35)
//						//System.out.println("valor ref 2 "+valorPagRef.get("valorPagRef2").toString());
//						psRegistro += funciones.ajustarLongitudCampo(valorPagRef.get("valorPagRef2").toString()!=null? valorPagRef.get("valorPagRef2").toString() : "", 35, "I", "", "");		//14 Transaction Detail Line 2 A(35), en vb6 pone aqui en nombre de empresa
//						psRegistro += funciones.ajustarLongitudCampo(valorPagRef.get("valorPagRef3").toString()!=null? valorPagRef.get("valorPagRef3").toString() : "", 35, "I", "", "");		//15 Transaction Detail Line 3 A(35)
//						
						//psCampo = funciones.quitarCaracteresRaros(listGrid.get(i).getConcepto(), true);
//						psRegistro += funciones.ajustarLongitudCampo(psCampo, 35, "I", "", "");	//13 Transaction Detail Line 1 A(35)
//						psRegistro += funciones.ajustarLongitudCampo("", 35, "D", "", "");		//14 Transaction Detail Line 2 A(35), en vb6 pone aqui en nombre de empresa
//						psRegistro += funciones.ajustarLongitudCampo("", 35, "D", "", "");		//15 Transaction Detail Line 3 A(35)
		            	
						psRegistro += funciones.ajustarLongitudCampo("", 35, "I", "", "");		//16 Transaction Detail Line 4 A(35)
						psRegistro += "05";														//17 Local Transaction Code N(2)
						psRegistro += "01";														//18 COstumer account type N(2)
						psCampo = funciones.quitarCaracteresRaros(listGrid.get(i).getBeneficiario(), true);
						psCampo = funciones.ocultarCoinversora(psCampo);
						psRegistro += funciones.ajustarLongitudCampo(psCampo, 80, "I", "", "");	//19 Beneficiary name A(80)
						psRegistro += funciones.ajustarLongitudCampo("XXX", 35, "I", "", "");		//20 Beneficiary Adress 1 A(35)
						//MS: se 
						psRegistro += funciones.ajustarLongitudCampo("XXX", 35, "I", "", "");		//21 Beneficiary Adress 1 second line A(35)
						psRegistro += funciones.ajustarLongitudCampo("", 15, "D", "", "");		//22 Beneficiary Adress 2 A(15) ciudad en vb6 aqui se pone el tema de cheque ocurre
						//					psRegistro += funciones.ajustarLongitudCampo("X", 35, "I", "", "");		//20 Beneficiary Adress 1 A(35)
//						//MS: se 
//						psRegistro += funciones.ajustarLongitudCampo("", 35, "D", "", "");		//21 Beneficiary Adress 1 second line A(35)
//						psRegistro += funciones.ajustarLongitudCampo("X", 15, "I", "", "");		//22 Beneficiary Adress 2 A(15) ciudad en vb6 aqui se pone el tema de cheque ocurre
						psRegistro += funciones.ajustarLongitudCampo("", 2, "D", "", "");		//23 Beneficiary state A(2)
						psRegistro += funciones.ajustarLongitudCampo("", 12, "D", "", "");	//24 Beneficiary adress 3 A(12) Cod. Postal
						//					psRegistro += funciones.ajustarLongitudCampo("1", 12, "D", "", "0");	//24 Beneficiary adress 3 A(12) Cod. Postal
						psRegistro += funciones.ajustarLongitudCampo("", 16, "D", "", "");		//25 Beneficiary phone number N(16)
						//MS: falta obtener el banco citi
//						psCampo = funciones.ajustarLongitudCampo(listGrid.get(i).getIdBancoCityStr(), 3, "D", "", "0");
						if (listGrid.get(i).getIdBanco() == listGrid.get(i).getIdBancoBenef()){
							psCampo ="000";
						}else{
							psCampo = funciones.ajustarLongitudCampo(""+listGrid.get(i).getIdBancoBenef(), 3, "D", "", "0");

						}
						psRegistro += psCampo;	
						psRegistro += funciones.ajustarLongitudCampo("", 8, "I", "", "");	//26 Beneficiary Bank number N(3)
//						psCampo = funciones.ajustarLongitudCampo("MEXICO", 8, "I", "", "");
//						psRegistro += psCampo;													//27 Beneficiary Bank Agency A(8)
//						psCampo = funciones.ajustarLongitudCampo(listGrid.get(i).getClabeBenef(), 35, "D", "", "0");
						psCampo = funciones.ajustarLongitudCampo(listGrid.get(i).getClabeBenef(), 35, "I", "", " ");
						psRegistro += psCampo;													//28 Beneficiary Account Number(35) SOLO INTERMEDIARIOS 
						

							if(listGrid.get(i).getIdBanco()==listGrid.get(i).getIdBancoBenef()){
								psRegistro += "01";	
							//	System.out.println("mismo banco ");
							}else{
								psRegistro += "05";
							}

								
						//'30 Dirección del banco A(30)
						psRegistro+=funciones.ajustarLongitudCampo("", 30, "I", "", "");//29 Account type CR SOLO INTERBANCARIO N(2), en vb6 si revisa si hay clave o no
//						psCampo = funciones.ajustarLongitudCampo("MEXICO", 30, "I", "", "");	//'30 Dirección del banco A(30)
//						psRegistro+=psCampo;
						psRegistro += funciones.ajustarLongitudCampo("", 17, "D", "", "0");		//'31 Importe del impuesto N(17)
					//	psRegistro += funciones.ajustarLongitudCampo("", 17, "D", "", "");		//'31 Importe del impuesto N(17)
						psRegistro += funciones.ajustarLongitudCampo("", 1, "I", "", "");														// '32 Bandera de prioridad A(01) N= no se usa prioridad ATene16
						psRegistro +=  funciones.ajustarLongitudCampo("", 1, "I", "", "");	
						//psRegistro += "N";														// '32 Bandera de prioridad A(01) N= no se usa prioridad ATene16
//						psRegistro += "N";														// '33 Confidencial A(01) N= no se usa confidencial ATene16
						//psRegistro += psCampo;													//30 Beneficiary Bank Adress A(30)
						psRegistro += "01";														//'34 Fecha de acreditación al beneficiario N(02)
						
						psCampo = listGrid.get(i).getIdChequera();
						psRegistro += funciones.ajustarLongitudCampo(psCampo, 20, "I", "", "");//35 Número de cuenta de débito banamex N(20)
						//psRegistro += psCampo;//35 Número de cuenta de débito banamex N(20)
					//	System.out.println("numero de cuenta de debito banam tamaño "+psCampo.length());
						psRegistro += funciones.ajustarLongitudCampo("", 16, "D", "", " ");		//36 Beneficiary Fax Number N(16)
						psRegistro += funciones.ajustarLongitudCampo("", 20, "D", "", " ");		//37 Beneficiary Fax Contact Name A(20)
						psRegistro += funciones.ajustarLongitudCampo("", 15, "D", "", " ");		//38 Beneficiary Fax Department Name A(15)
						psRegistro += funciones.ajustarLongitudCampo("", 10, "D", "", "");		//39 Beneficiary Account number N(10) SOLO MISMO BANCO
				//		psRegistro += funciones.ajustarLongitudCampo("", 10, "D", "", "0");		//39 Beneficiary Account number N(10) SOLO MISMO BANCO
						psRegistro += funciones.ajustarLongitudCampo("", 2, "D", "", " ");		//40 Account Type CR N(2) SOLO MISMO BANCO
						psRegistro += "001";		//41 Método de entrega de transacciones N(03)
		//				psRegistro += funciones.ajustarLongitudCampo("", 3, "D", "", " ");		//41 Método de entrega de transacciones N(03)
						
						psRegistro += funciones.ajustarLongitudCampo("", 50, "D", "", " ");		//42 Collection Title ID A(50)
						psRegistro += funciones.ajustarLongitudCampo("", 5, "D", "", "0");		//43 Beneficiary Activity code N(5)
						psRegistro += funciones.ajustarLongitudCampo("", 50, "D", "", " ");		//44 Beneficiary e-mail adress A(50)
//						psCampo = funciones.ajustarLongitudCampo("" + (listGrid.get(i).getImporte() * 100), 15, "D", "", "0");
//						psCampo = funciones.ponerFormatoCerosSindecimales((listGrid.get(i).getImporte() * 100), 15);
						//psRegistro += psCampo;//45 Maximum payment amount N(15)
						psRegistro += "999999999999999";//45 Maximum payment amount N(15)
						psRegistro += funciones.ajustarLongitudCampo("", 1, "D", "", "");		//46 Update Type N(1)
						psRegistro += funciones.ajustarLongitudCampo("", 11, "I", "", " ");
//						psRegistro += funciones.ajustarLongitudCampo("NONE", 11, "I", "", " ");		//47 Check Number N(11)
						psRegistro += funciones.ajustarLongitudCampo("", 1, "D", "", "");		//48 Printed Check A(1)
						psRegistro += funciones.ajustarLongitudCampo("", 1, "D", "", "");		//49 Match Paid A(1)
						psRegistro += funciones.ajustarLongitudCampo("", 253, "D", "", "");		//50 Blank Characteres A(254)

					//}
					
									
				}catch(Exception e) {
					bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:com.webset.set.layout, C:LayoutBancomerBusiness, M:beArmaCitibankPaylinkMN");
					e.printStackTrace();
					return "";
				}
				return psRegistro;
			}


		

	
}
