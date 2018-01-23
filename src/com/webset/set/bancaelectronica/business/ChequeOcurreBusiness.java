package com.webset.set.bancaelectronica.business;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.webset.set.bancaelectronica.dao.ChequeOcurreDao;
import com.webset.set.bancaelectronica.dao.EnvioTransferenciasDao;
import com.webset.set.bancaelectronica.dto.ArchTransferDto;
import com.webset.set.bancaelectronica.dto.CriterioBusquedaDto;
import com.webset.set.bancaelectronica.dto.DetArchTransferDto;
import com.webset.set.bancaelectronica.service.ChequeOcurreService;
import com.webset.set.layout.business.CreacionArchivosBusiness;
import com.webset.set.layout.business.LayoutBancomerBusiness;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;

public class ChequeOcurreBusiness implements ChequeOcurreService {
	ChequeOcurreDao chequeOcurreDao;
	Funciones funciones = new Funciones();
	private LayoutBancomerBusiness layoutBancomerBusiness;
	private EnvioTransferenciasDao envioTransferenciasDao;
	private CreacionArchivosBusiness creacionArchivosBusiness;
	private static Logger logger = Logger.getLogger(ChequeOcurreBusiness.class);

	@Override
	public List<LlenaComboEmpresasDto> obtenerEmpresas(int idUsuario) {
		return chequeOcurreDao.obtenerEmpresas(idUsuario);
	}
	
	@Override
	public List<LlenaComboGralDto> llenaComboBanco(int noEmpresa){
		return chequeOcurreDao.llenaComboBanco(noEmpresa);
	}

	@Override
	public List<MovimientoDto> consultarCheques(int noEmpresa, int idBanco) {
		return chequeOcurreDao.consultarCheques(noEmpresa, idBanco);
	}
	
//	public  Map<String, Object> ejecutar(List<MovimientoDto> movimientos, int optSantanderDirecto, int idBanco, boolean chkSantanderH2H) throws Exception {
//		Map<String,Object> respuesta=new HashMap<String, Object>();
//	 	String ps_folios = "";
//	 	//String ls_ruta_envio = "";
//	    String ps_nom_archivo = "";
//	   // String lsRegistro = "";
//	    String ps_directorio = "";
//	    String psCarpeta_principal = ConstantesSet.CARPETA_RAIZ_CHEQUE_OCURRE;
//	    psCarpeta_principal=psCarpeta_principal.substring(0,psCarpeta_principal.length()-1 );
//	    String psRuta = "";
//		boolean pbEscribi = false;
//    	//String psFolios = "";
//    	//String folioSantander="";
//    	int piRegistros = 0;
//    	double pdImporte = 0;
//    	//String sDescBanco="";
//	   // boolean pb_escribi = false;
//		int insDetArchTrans = 0;
//	    String pd_folio_santander = "";
//	    
//		//if (chequeOcurreDao.consultarConfiguraSet(1).equals("CIE")) ls_ruta_envio = "\\fileset.cie\\transferencias";
//		   // pi_parche = 64;
//			//psRuta = ls_ruta_envio + "\\chqOcurre";
//		    //if (movimientos.size() > 0) {
//		        switch(idBanco) {
//		           /* case ConstantesSet.BANCOMER:
//		                psRuta = psRuta + "\\bancomer\\";
//		                lFolio_bancomer = chequeOcurreDao.obtenFolio("arch_bancomer_ocurr");
//		                ps_nom_archivo = "bnc" + fmt.format("%05d",lFolio_bancomer) + ".txt";
//		                break;
//		            case ConstantesSet.BANAMEX:
//		                if (optBanamexMasterCheck) psRuta = psRuta + "\\BanamexMC\\";
//		                else psRuta = psRuta + "\\banamex\\";
//	
//		                pd_folio_bital = chequeOcurreDao.obtenFolio("arch_banamex_ocurre");
//	
//		                if (pd_folio_bital < 0) return "No se encontro el folio para Banamex.";
//		               
//		                if (optBanamexMasterCheck) ps_nom_archivo = "bMC" + fmt.format("%05d",pd_folio_bital) + ".txt";
//		                else ps_nom_archivo = "bac" + fmt.format("%05d",pd_folio_bital) + ".txt";
//	
//		                break;                
//		                /* case ConstantesSet.BITAL:
//		                psRuta = psRuta + "\\bital\\";
//		                pd_folio_bital = chequeOcurreDao.obtenFolio("arch_bital_ocurre");
//		                
//						if (pd_folio_bital < 0) return "No se encontró el folio para cheque ocurre BITAL.";
//		                  
//		                ps_nom_archivo = "bic" + fmt.format("%05d",pd_folio_bital) + ".txt";
//		                break;
//		           case ConstantesSet.BITAL_DLS:
//		                psRuta = psRuta + "\\bitalDLS\\";
//		                pd_folio_bital = chequeOcurreDao.obtenFolio("arch_bitaldls_ocurre");
//		                ps_nom_archivo = "bdc" + fmt.format("%05d",pd_folio_bital) + ".txt";
//		                break;*/
//		            case ConstantesSet.SANTANDER:
//		                if ((optSantanderDirecto== 0||optSantanderDirecto==1) &&!chkSantanderH2H) {
//		                	if(optSantanderDirecto==0){
//		                		psRuta =  "arch_santander";
//			                    pd_folio_santander="santander";
//			                    ps_nom_archivo = "std";
//		                	}
//		                	else{
//		                		psRuta = "arch_santander";
//			                    pd_folio_santander="santander";
//			                    ps_nom_archivo = "stc";
//		                	}
//		                }
//		                else if(chkSantanderH2H){
//		                	ps_nom_archivo="tran";
//		                	pd_folio_santander = "SANTANDER_H2H";
//		                	psRuta="arch_santander_h2h";
//		                }
//		                break;
//		            default:
//		            	respuesta.put("error", "Este banco no tiene chequera");
//		                return respuesta;
//		        } 
//		        String nomArchivo = "";
//		        ps_directorio = psRuta;
//		        String psBanco="";
//		        Map<String, Object> mapCreaArch = new HashMap<String, Object>();
//		        if(!pd_folio_santander.equals("") && !psRuta.equals("") && !ps_nom_archivo.equals("")) {
//		        	pd_folio_santander = pd_folio_santander.replace('/', File.separatorChar).replace('\\', File.separatorChar);
//		    		mapCreaArch = creacionArchivosBusiness.generarNombreArchivo(pd_folio_santander, ps_directorio, ps_nom_archivo, false, false, false, 0);
//	   				if(!mapCreaArch.isEmpty()) {
//						if(mapCreaArch.get("msgUsuario") != null) {
//							respuesta.put("msgUsuario",mapCreaArch.get("msgUsuario").toString());
//							return respuesta;
//						}
//					}
//	   				
//	   				nomArchivo = mapCreaArch.get("nomArchivo").toString();
//	   				psBanco=mapCreaArch.get("psBanco").toString();
//	   				respuesta.put("archivo", nomArchivo);
//	    		}
//		       // if (creacionArchivosBusiness.generarNombreArchivo(psRuta, pd_folio_santander, ps_nom_archivo, false, false, false, 0).equals("")) return "No se pudo crear el archivo.";
//		   // } else return "Debe seleccionar al menos un movimiento.";
//		    /*****************************************************************/
//		   
//		    ps_folios = "";
//		    ///pb_escribi = false;
//		    double impTotDet = 0;
//		    boolean continuar = false;
//		    String psRegistro = "";
//		    String lsYaEjecutados = "";
//		    for (int i = 0; movimientos.size() > i; i++) {
//	            ps_folios = ps_folios + movimientos.get(i).getNoFolioDet() + ",";
//	            continuar = true;
//		    	if(envioTransferenciasDao.consultarYaEjecutado(movimientos.get(i).getNoFolioDet())) {
//		    		lsYaEjecutados += movimientos.get(i).getNoDocto()+ ",";
//		    		respuesta.put("msgUsuario", "Los siguientes documentos ya fueron ejecutados: "+lsYaEjecutados);
//		    		continuar = false;
//		    	}
//		    	if (continuar) {
//		            switch(idBanco){
//		            /* case BANAMEX
//			                if (!optBanamexMasterCheck) {
//			                    if arma_banamex_cheque_ocurre() = True { //funcion de armado de cadena
//			                        if genera_archivo(ps_Archivo, lb_abierto_banamex, lsRegistro) = False {
//			                            Screen.MousePointer = 0
//			                            return "Error al generar el archivo Banamex", vbCritical, "SET"
//			                            Exit Sub
//			                        else
//			                            lb_abierto_banamex = True
//			                        }
//			                    }
//			                }
//			                pb_escribi = True
//			            case BANCOMER
//			                if arma_BANCOMER_ocurre() = True {  //funcion de armado de cadena
//			                    if genera_archivo(ps_Archivo, lb_abierto_bancomer, lsRegistro) = False {
//			                        Screen.MousePointer = 0
//			                        return "Error al generar el archivo BANCOMER", vbCritical, "SET"
//			                        Exit Sub
//			                    else
//			                        lb_abierto_bancomer = True
//			                    }
//			                }
//			                pb_escribi = True
//			            case BITAL
//			                if arma_bital_cheque_ocurre() = True {  //funcion de armado de cadena
//			                    if genera_archivo(ps_Archivo, lb_abierto_bital, lsRegistro) = False {
//			                        Screen.MousePointer = 0
//			                        return "Error al generar el archivo BITAL", vbCritical, "SET"
//			                        Exit Sub
//			                    else
//			                        lb_abierto_bital = True
//			                    }
//			                }
//			                pb_escribi = True
//			            case 1229      //BITAL DLS
//			                if arma_bital_cheque_ocurre() = True {  //funcion de armado de cadena
//			                    if genera_archivo(ps_Archivo, lb_abierto_bital_DLS, lsRegistro) = False {
//			                        Screen.MousePointer = 0
//			                        return "Error al generar el archivo BITAL DLS", vbCritical, "SET"
//			                        //Call mensaje("ER029", "al generar archivo BITAL DLS")
//			                        Exit Sub
//			                    else
//			                        lb_abierto_bital_DLS = True
//			                    }
//			                }
//			                pb_escribi = True*/
//				        case ConstantesSet.SANTANDER:
//				        	if(chkSantanderH2H) {			        		
//	               				impTotDet += movimientos.get(i).getImporte();
//				        		psRegistro = layoutBancomerBusiness.beArmaH2HSantanderChequeOcurre(movimientos.get(i), i, impTotDet, movimientos.size());//Arma la cadena
//				        		//sDescBanco = envioTransferenciasDao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_SANTANDER_2H2);
//				        		respuesta.put("directo", "no");
//	           					if(psRegistro == null || psRegistro.equals("")){
//	           						respuesta.put("msgUsuario", "Error al crear el layout\n Codigo de error: [1]");
//	                    			return respuesta;
//	           					}
//	               			}else {
//	               				psRegistro = layoutBancomerBusiness.beArmaSantanderChequeOcurre(movimientos.get(i), optSantanderDirecto);
//	               				//sDescBanco = envioTransferenciasDao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_SANTANDER);
//	               				//respuesta.put("resRutaLocal",psCarpeta_principal+"\\"+psRuta);
//	               				//respuesta.put("resNombreArchivoLocal",nomArchivo);
//	               				respuesta.put("directo", "si");
//	               				if(psRegistro == null || psRegistro.equals("")){ 
//	           						respuesta.put("msgUsuario", "Error al crear el layout\n Codigo de error: [1]");
//	               					return respuesta; 
//	               				}
//	               			}
//							break;
//					} 
//		    	}
//	           // if (!beArmaNuevos) {
//	            Map<String, Object> mapRet = new HashMap<String, Object>();
//	    		if(!psRegistro.equals("")) {
//	    			mapRet = creacionArchivosBusiness.escribirArchivoLayout(psCarpeta_principal, psBanco, nomArchivo, psRegistro);
//	    			respuesta.put("resRutaLocal", mapRet.get("resRutaLocal"));
//		    		respuesta.put("resNombreArchivoLocal", mapRet.get("resNombreArchivoLocal"));
//	    			if(!(Boolean)mapRet.get("resExito")) {
//        				mapRet.put("msgUsuario", "Error al Generar Archivo " + idBanco);
//        				respuesta.put("msgUsuario","Error al Generar Archivo " + idBanco);
//						return respuesta;
//					}	    				
//				}else {
//					
//					//mapRet.put("msgUsuario", mapRet.get("msgUsuario"));
//					//respuesta.put("msgUsuario",respuesta.get("msgUsuario"));
//					//return respuesta;
//				}
//	    		
//				//}
//	    		DetArchTransferDto dtoInsert = new DetArchTransferDto();
//	    		dtoInsert.setNomArch(nomArchivo);
//	    		dtoInsert.setNoFolioDet(movimientos.get(i).getNoFolioDet());
//	    		dtoInsert.setFecValorDate(movimientos.get(i).getFecValor());
//	    		dtoInsert.setIdBanco(movimientos.get(i).getIdBanco());
//	    		dtoInsert.setIdEstatusArch("T");
//	    		dtoInsert.setIdChequeraBenef(movimientos.get(i).getIdChequeraBenef());
//	    		dtoInsert.setImporte(movimientos.get(i).getImporte());
//	    		dtoInsert.setBeneficiario(movimientos.get(i).getBeneficiario());
//	    		dtoInsert.setSucursal(Integer.parseInt(movimientos.get(i).getSucDestino()));
//	    		dtoInsert.setPlaza(Integer.parseInt(movimientos.get(i).getPlazaBenef()));
//	    		dtoInsert.setNoDocto(movimientos.get(i).getNoDocto());
//	    		dtoInsert.setIdChequera(movimientos.get(i).getIdChequera());
//	    		dtoInsert.setIdBancoBenef(movimientos.get(i).getIdBancoBenef());
//	    		dtoInsert.setPrefijoBenef(movimientos.get(i).getInstFinan() != null ? movimientos.get(i).getInstFinan():"");
//	    		dtoInsert.setConcepto(movimientos.get(i).getConcepto());
//	    		insDetArchTrans = envioTransferenciasDao.insertarDetArchTransfer(dtoInsert);
//	    		if(insDetArchTrans > 0) {
//	    			pbEscribi = true;
//                	//psFolios += movimientos.get(i).getNoFolioDet()+",";
//                	piRegistros++;
//                	pdImporte = pdImporte + movimientos.get(i).getImporte();
//                	chequeOcurreDao.actualizarMovimiento(movimientos.get(i).getNoFolioDet());
//                	
//                }
//		    }//termina for
//		    
//		    
//		   
//		    
//		    
//		    String psTipoEnvio = "C";
//		    ArchTransferDto insArchTrans = new ArchTransferDto(); 
//			insArchTrans.setNomArch(nomArchivo);
//			insArchTrans.setIdBanco(idBanco);
//			insArchTrans.setImporte(pdImporte);
//			insArchTrans.setFecTrans(envioTransferenciasDao.obtenerFechaHoy());
//			insArchTrans.setRegistros(piRegistros);
//			insArchTrans.setNoUsuarioAlta(GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario());
//			insArchTrans.setTipoEnvioLayout(psTipoEnvio);
//			   
//			envioTransferenciasDao.insertarArchTransfer(insArchTrans);
//			int piBanco = idBanco;
//			//dirPrincipal = ConstantesSet.CARPETA_RAIZ_RESPALDO;
//			String psRutaArchivos = psCarpeta_principal;
//			String psSoloArchivos = nomArchivo;
//			 //String dirPrincipal="";
//			 //dirPrincipal =ConstantesSet.CARPETA_RAIZ_CHEQUE_OCURRE;
//			 //dirPrincipal+=ps_directorio;
//			 psCarpeta_principal+=psBanco;
//			 //psRutaArchivos = dirPrincipal;
//			 psSoloArchivos = nomArchivo;
//			 String salida = "";	  
//			 if(piBanco==14 && chkSantanderH2H){
///////////////////////////////daniel////////////////////////////////////////
//					 psRutaArchivos = psCarpeta_principal;
//					// psRutaArchivos = dirPrincipal;
//					psSoloArchivos = nomArchivo;
//					 //StringBuffer mapFtp = creacionArchivosBusiness.creaArchivoFtp(psSoloArchivos);  
//			 		 //creacionArchivosBusiness.escribirArchivoLayout("C:\\apicifrado\\cifradoSalida\\", "", "ftp" +  GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()  + ".bat", mapFtp.toString());
//					 salida = creacionArchivosBusiness.enviaH2H(psRutaArchivos, psSoloArchivos);	 
//	   			
//			}
//			 if(pbEscribi) { //'si no escribió ningun registro se sale
//				 if(!lsYaEjecutados.equals("")&&!salida.equals("")){
//					 for (MovimientoDto movimientoDto : movimientos) {
//						 chequeOcurreDao.regresaEstatusMovimiento(movimientoDto.getNoFolioDet());
//					 }
//					 chequeOcurreDao.eliminaArchivosErroneos(nomArchivo);	   
//				 }   
//			 			   //respuesta.put("msgUsuario", "Los siguientes documentos ya fueron procesados:" + lsYaEjecutados.substring(0,lsYaEjecutados.length()-1));
//		                   //return respuesta;
//			 }
//			 else{ 
//				 respuesta.put("msgUsuario", "No se realizó ninguna tarea");
//			 }
//				   
//			 respuesta.put("msgUsuario", "Los Registros han sido Guardados en el archivo: "  + nomArchivo);		
//			 return respuesta; 
//	}
	
	
	
	public  Map<String, Object> ejecutarCheques(List<MovimientoDto> movimientos, CriterioBusquedaDto dtoCriBus, int h2hAfrd) {
		Map<String,Object> respuesta=new HashMap<String, Object>();
		Map<String, Object> mapRet = new HashMap<String, Object>();
		Map<String, Object> mapCreaArch = new HashMap<String, Object>();
	 	String ps_folios = "";
	 	//String ls_ruta_envio = "";
	    String ps_nom_archivo = "";
	   // String lsRegistro = "";
	    String ps_directorio = "";
	    String dirPrincipal = ConstantesSet.CARPETA_RAIZ_CHEQUE_OCURRE;
	    dirPrincipal=dirPrincipal.substring(0,dirPrincipal.length()-1 );
	    System.out.println("directorioPrincipal "+dirPrincipal);
	    String psRuta = "";
		boolean pbEscribi = false;
    	//String psFolios = "";
    	//String folioSantander="";
    	int piRegistros = 0;
    	double pdImporte = 0;
    	//String sDescBanco="";
	   // boolean pb_escribi = false;
		int insDetArchTrans = 0;
	    String pd_folio_santander = "";
	  
	    String sDescBanco = "";
	    String sNomFoliador = "";
	    String sIndiceArch = "";
		int piTipoTrasferencia=0;
		int piBanco=0;
		boolean beArmaNuevos=true;
		//if (chequeOcurreDao.consultarConfiguraSet(1).equals("CIE")) ls_ruta_envio = "\\fileset.cie\\transferencias";
		   // pi_parche = 64;
			//psRuta = ls_ruta_envio + "\\chqOcurre";
		    //if (movimientos.size() > 0) {
		        switch(dtoCriBus.getIdBanco()) {
		           /* case ConstantesSet.BANCOMER:
		                psRuta = psRuta + "\\bancomer\\";
		                lFolio_bancomer = chequeOcurreDao.obtenFolio("arch_bancomer_ocurr");
		                ps_nom_archivo = "bnc" + fmt.format("%05d",lFolio_bancomer) + ".txt";
		                break;*/
		            case ConstantesSet.BANAMEX:
		            	if ((dtoCriBus.getOptEnvioBNMX() == 5)&&(h2hAfrd==1)) {
		            		System.out.println("entro a PA");
			   				sDescBanco = envioTransferenciasDao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_BANAMEX_PL);
			   				sNomFoliador = "arch_citi_paylink";
			   				sIndiceArch = "PA";
						}else{
							if(dtoCriBus.getOptEnvioBNMX() == 1) {				//Layout - TEF (Transferencia Electronica de Fondos)
				   				sDescBanco = envioTransferenciasDao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_BANAMEX_TEF);
				   				System.out.println("nombre carpeta raiz "+sDescBanco);
				   			//	mapRet = layoutBancomerBusiness.beArmaBanamexTEF(pbMismoBanco == false ? true : false, listBus, false, "", i, dirPrincipal, sDescBanco);
								return mapRet;
				   			}else if(dtoCriBus.getOptEnvioBNMX() == 3) {		//Layout - Mass Payment Citibank
				   				//mapRet = layoutBancomerBusiness.beArmaBanamexMassPay(pbMismoBanco == false ? true : false, listBus, false, "", i, dirPrincipal);
								return mapRet;
				   			}else if(dtoCriBus.getOptEnvioBNMX() == 4) {		//Layout - Flat File Citibank (Solo DLS)
				   				sDescBanco = envioTransferenciasDao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_BANAMEX_FLT);
				   				sNomFoliador = "arch_citibank_dls";
				   				sIndiceArch = "cd";
				   			}else if((dtoCriBus.getOptEnvioBNMX() == 5)&&(h2hAfrd==2)) {		//Layout - Pay Link Citibank (Solo MN)
				   				sDescBanco = envioTransferenciasDao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_BANAMEX_PL);
				   				sNomFoliador = "arch_citi_paylink";
				   				sIndiceArch = "cp";
				   			}else {												//Layoutï¿½s Digitem = dtoCriBus.getOptEnvioBNX() == 0
				   				sDescBanco = envioTransferenciasDao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_BANAMEX);
				   				sNomFoliador = "arch_banamex";
				   				sIndiceArch = "bn";
				   			}
						}
//		                if (optBanamexMasterCheck) sDescBanco = sDescBanco + "\\BanamexMC\\";
//		                else sDescBanco = sDescBanco + "\\banamex\\";
//	
//		               // pd_folio_bital = chequeOcurreDao.obtenFolio("arch_banamex_ocurre");
//		                sNomFoliador="arch_citi_paylink";
//		            //    if (pd_folio_bital < 0) return "No se encontro el folio para Banamex.";
//		               
//		                if (optBanamexMasterCheck){
//		                	// sIndiceArch= "bMC" + fmt.format("%05d",pd_folio) + ".txt";
//		                }
//		               else{
//		            	   //sIndiceArch = "bac" + fmt.format("%05d",pd_folio) + ".txt";
//		               }
	
		                break;                
		                /* case ConstantesSet.BITAL:
		                psRuta = psRuta + "\\bital\\";
		                pd_folio_bital = chequeOcurreDao.obtenFolio("arch_bital_ocurre");
		                
						if (pd_folio_bital < 0) return "No se encontró el folio para cheque ocurre BITAL.";
		                  
		                ps_nom_archivo = "bic" + fmt.format("%05d",pd_folio_bital) + ".txt";
		                break;
		           case ConstantesSet.BITAL_DLS:
		                psRuta = psRuta + "\\bitalDLS\\";
		                pd_folio_bital = chequeOcurreDao.obtenFolio("arch_bitaldls_ocurre");
		                ps_nom_archivo = "bdc" + fmt.format("%05d",pd_folio_bital) + ".txt";
		                break;
		            case ConstantesSet.SANTANDER:
		                if ((optSantanderDirecto== 0||optSantanderDirecto==1) &&!chkSantanderH2H) {
		                	if(optSantanderDirecto==0){
		                		psRuta =  "arch_santander";
			                    pd_folio_santander="santander";
			                    ps_nom_archivo = "std";
		                	}
		                	else{
		                		psRuta = "arch_santander";
			                    pd_folio_santander="santander";
			                    ps_nom_archivo = "stc";
		                	}
		                }
		                else if(chkSantanderH2H){
		                	ps_nom_archivo="tran";
		                	pd_folio_santander = "SANTANDER_H2H";
		                	psRuta="arch_santander_h2h";
		                }
		                break;*/
		            default:
		            	respuesta.put("error", "Este banco no tiene chequera");
		                return respuesta;
		        } 
		        String nomArchivo = "";
		        ps_directorio = psRuta;
		        String psBanco="";
		       
		        
		        if(!sDescBanco.equals("") && !sNomFoliador.equals("") && !sIndiceArch.equals("")) {
	    			sDescBanco = sDescBanco.replace('/', File.separatorChar).replace('\\', File.separatorChar);
		    		mapCreaArch = creacionArchivosBusiness.generarNombreArchivo(sDescBanco, sNomFoliador, sIndiceArch, true, false, dtoCriBus.isChkH2H(), 0,1);
	   				
	   				if(!mapCreaArch.isEmpty()) {
						if(mapCreaArch.get("msgUsuario") != null) {
							respuesta.put("msgUsuario", mapCreaArch.get("msgUsuario"));
							return respuesta;
						}
					}
	   				nomArchivo = mapCreaArch.get("nomArchivo").toString();
	   				System.out.println("nombreArchivo "+nomArchivo);
	   				psBanco=mapCreaArch.get("psBanco").toString();
	   				respuesta.put("nomArchivo", mapCreaArch.get("nomArchivo").toString());
	    		}
		       
		    	//	mapCreaArch = creacionArchivosBusiness.generarNombreArchivo(pd_folio_bital, ps_directorio, ps_nom_archivo, false, false, false, 0);
	   				
		       // if (creacionArchivosBusiness.generarNombreArchivo(psRuta, pd_folio_santander, ps_nom_archivo, false, false, false, 0).equals("")) return "No se pudo crear el archivo.";
		   // } else return "Debe seleccionar al menos un movimiento.";
		    /*****************************************************************/
		   
		    ps_folios = "";
		    ///pb_escribi = false;
		    double impTotDet = 0;
		    boolean continuar = false;
		    String psRegistro = "";
		    String lsYaEjecutados = "";
		    for (int i = 0; movimientos.size() > i; i++) {
	            ps_folios = ps_folios + movimientos.get(i).getNoFolioDet() + ",";
	            continuar = true;
		    	if(envioTransferenciasDao.consultarYaEjecutado(movimientos.get(i).getNoFolioDet())) {
		    		lsYaEjecutados += movimientos.get(i).getNoDocto()+ ",";
		    		respuesta.put("msgUsuario", "Los siguientes documentos ya fueron ejecutados: "+lsYaEjecutados);
		    		continuar = false;
		    	}
		    	if (continuar) {
		    		if(dtoCriBus.getOptEnvioBNMX() == 5)
		    			piBanco = ConstantesSet.CITIBANK_DLS;
		    		else
		    			piBanco = dtoCriBus.getIdBanco();
		    		
		    		System.out.println("\n piBanco "+piBanco+"\n");
		            switch(piBanco){
		            case ConstantesSet.BANAMEX:
		            	break;
		             case ConstantesSet.CITIBANK_DLS:
		             if ((dtoCriBus.getOptEnvioBNMX() == 5)&&(h2hAfrd==1)) {
            				psRegistro = layoutBancomerBusiness.beArmaCitibankPaylinkMNChequeOcurre(movimientos, i, "");
             			System.out.println("pay "+psRegistro);
            				if(psRegistro != null && !psRegistro.equals("")){
            					respuesta.put("msgRegistro", psRegistro);
            					System.out.println("msgRegistroa antes de crear archivo----"+respuesta.get("msgRegistro").toString());
            					respuesta = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal, mapCreaArch.get("psBanco").toString(),mapCreaArch.get("nomArchivo").toString(),respuesta.get("msgRegistro").toString());		
             				//return mapRet;
            					respuesta.put("msgRegistro", psRegistro);
             				System.out.println("resExito----"+respuesta.get("resExito").toString());
             				beArmaNuevos=true;

            				}else{
            					respuesta.put("msgUsuario", "No se encontró el folio <folio_paylink>!!");
            					
            				}
            			} 

						                   			
            			if((dtoCriBus.getOptEnvioBNMX() == 5)&&(h2hAfrd==2)) {			//Opcion de Citibank PayLink MN
            				psRegistro = layoutBancomerBusiness.beArmaCitibankPaylinkMN485(movimientos, i, "");
             			System.out.println("pay "+psRegistro);
            				if(psRegistro != null && !psRegistro.equals("")){
            					respuesta.put("msgRegistro", psRegistro);
            					System.out.println("msgRegistroa antes de crear archivo----"+respuesta.get("msgRegistro").toString());
            					respuesta = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal, mapCreaArch.get("psBanco").toString(),respuesta.get("nomArchivo").toString(),respuesta.get("msgRegistro").toString());		
             				//return mapRet;
            					respuesta.put("msgRegistro", psRegistro);
             				System.out.println("resExito----"+respuesta.get("resExito").toString());
             				//beArmaNuevos=true;
            				}else{
            					respuesta.put("msgUsuario", "No se encontró el folio <folio_paylink>!!");
            					
            				}
            					
            			}else {
//            				if(dtoCriBus.getOptEnvioBNMX() == 2)			//Opcion de Citibank WorldLink
//            					piTipoTrasferencia = 5;
//            				else if(dtoCriBus.getOptEnvioBNMX() == 6)	//Opcion de Citibank ACH
//            					piTipoTrasferencia = 4;
//            				else if(piTipoTrasferencia == 1) {			//Opcion de Interbancaria
//            					if(listBus.get(i).getTipoEnvioLayout() != 0)
//            						piTipoTrasferencia = listBus.get(i).getTipoEnvioLayout();
//            					else if(!listBus.get(i).getNomBancoIntermediario().trim().equals(""))
//            						piTipoTrasferencia = 3;
//            					else
//            						piTipoTrasferencia = 1;
//            				}
//            				System.out.println("entro a parte falsa de citybank dls" );
//            				respuesta = layoutBancomerBusiness.beExportacion(movimientos, i, piTipoTrasferencia,
//									mapCreaArch.get("nomArchivo").toString(), dirPrincipal, idBanco, "TRANSFERENCIA");
            			}
            			
            			break;

			              
			         		/*       case BANCOMER
			                if arma_BANCOMER_ocurre() = True {  //funcion de armado de cadena
			                    if genera_archivo(ps_Archivo, lb_abierto_bancomer, lsRegistro) = False {
			                        Screen.MousePointer = 0
			                        return "Error al generar el archivo BANCOMER", vbCritical, "SET"
			                        Exit Sub
			                    else
			                        lb_abierto_bancomer = True
			                    }
			                }
			                pb_escribi = True
			            case BITAL
			                if arma_bital_cheque_ocurre() = True {  //funcion de armado de cadena
			                    if genera_archivo(ps_Archivo, lb_abierto_bital, lsRegistro) = False {
			                        Screen.MousePointer = 0
			                        return "Error al generar el archivo BITAL", vbCritical, "SET"
			                        Exit Sub
			                    else
			                        lb_abierto_bital = True
			                    }
			                }
			                pb_escribi = True
			            case 1229      //BITAL DLS
			                if arma_bital_cheque_ocurre() = True {  //funcion de armado de cadena
			                    if genera_archivo(ps_Archivo, lb_abierto_bital_DLS, lsRegistro) = False {
			                        Screen.MousePointer = 0
			                        return "Error al generar el archivo BITAL DLS", vbCritical, "SET"
			                        //Call mensaje("ER029", "al generar archivo BITAL DLS")
			                        Exit Sub
			                    else
			                        lb_abierto_bital_DLS = True
			                    }
			                }
			                pb_escribi = True
				        case ConstantesSet.SANTANDER:
				        	if(chkSantanderH2H) {			        		
	               				impTotDet += movimientos.get(i).getImporte();
				        		psRegistro = layoutBancomerBusiness.beArmaH2HSantanderChequeOcurre(movimientos.get(i), i, impTotDet, movimientos.size());//Arma la cadena
				        		//sDescBanco = envioTransferenciasDao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_SANTANDER_2H2);
				        		respuesta.put("directo", "no");
	           					if(psRegistro == null || psRegistro.equals("")){
	           						respuesta.put("msgUsuario", "Error al crear el layout\n Codigo de error: [1]");
	                    			return respuesta;
	           					}
	               			}else {
	               				psRegistro = layoutBancomerBusiness.beArmaSantanderChequeOcurre(movimientos.get(i), optSantanderDirecto);
	               				//sDescBanco = envioTransferenciasDao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_SANTANDER);
	               				//respuesta.put("resRutaLocal",psCarpeta_principal+"\\"+psRuta);
	               				//respuesta.put("resNombreArchivoLocal",nomArchivo);
	               				respuesta.put("directo", "si");
	               				if(psRegistro == null || psRegistro.equals("")){ 
	           						respuesta.put("msgUsuario", "Error al crear el layout\n Codigo de error: [1]");
	               					return respuesta; 
	               				}
	               			}
							break;*/
					} 
		    	}
	            if (!beArmaNuevos) {
	           // Map<String, Object> mapRet = new HashMap<String, Object>();
	    		if(!psRegistro.equals("")) {
	    			mapRet = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal, psBanco, nomArchivo, psRegistro);
	    			respuesta.put("resRutaLocal", mapRet.get("resRutaLocal"));
		    		respuesta.put("resNombreArchivoLocal", mapRet.get("resNombreArchivoLocal"));
	    			if(!(Boolean)mapRet.get("resExito")) {
        				mapRet.put("msgUsuario", "Error al Generar Archivo " + piBanco);
        				respuesta.put("msgUsuario","Error al Generar Archivo " + piBanco);
						return respuesta;
					}	    				
				}else {
					
					//mapRet.put("msgUsuario", mapRet.get("msgUsuario"));
					//respuesta.put("msgUsuario",respuesta.get("msgUsuario"));
					//return respuesta;
				}
	    		
				}
	    		DetArchTransferDto dtoInsert = new DetArchTransferDto();
	    		dtoInsert.setNomArch(nomArchivo);
	    		dtoInsert.setNoFolioDet(movimientos.get(i).getNoFolioDet());
	    		dtoInsert.setFecValorDate(movimientos.get(i).getFecValor());
	    		dtoInsert.setIdBanco(movimientos.get(i).getIdBanco());
	    		dtoInsert.setIdEstatusArch("T");
	    		dtoInsert.setIdChequeraBenef("0");
	    		dtoInsert.setImporte(movimientos.get(i).getImporte());
	    		dtoInsert.setBeneficiario(movimientos.get(i).getBeneficiario());
	    		dtoInsert.setSucursal(0);
	    		dtoInsert.setPlaza(0);
	    		dtoInsert.setNoDocto(movimientos.get(i).getNoDocto());
	    		dtoInsert.setIdChequera(movimientos.get(i).getIdChequera());
	    		dtoInsert.setIdBancoBenef(0);
	    		dtoInsert.setPrefijoBenef(movimientos.get(i).getInstFinan() != null ? movimientos.get(i).getInstFinan():"");
	    		dtoInsert.setConcepto(movimientos.get(i).getConcepto());
	    		insDetArchTrans = envioTransferenciasDao.insertarDetArchTransfer(dtoInsert);
	    		if(insDetArchTrans > 0) {
	    			pbEscribi = true;
                	//psFolios += movimientos.get(i).getNoFolioDet()+",";
                	piRegistros++;
                	pdImporte = pdImporte + movimientos.get(i).getImporte();
                	chequeOcurreDao.actualizarMovimiento(movimientos.get(i).getNoFolioDet());
                	
                }
		    }//termina for
		    
		  ///crear trailer
    		if (dtoCriBus.getOptEnvioBNMX() == 5 && dtoCriBus.getIdBanco()==2) {
    			//System.out.println("mapRetE "+mapRet.get("msgRegistro").toString());
    			
    				System.out.println("totalRegistros "+piRegistros+" totalImporte "+pdImporte);
    				System.out.println("salto a el triller :...");
            		psRegistro = layoutBancomerBusiness.Trailer_citibank_paylink_MN(piRegistros,pdImporte,false); 		
            		System.out.println("datos del TRL..::"+psRegistro);
            		if(psRegistro != null && !psRegistro.equals("")){ 
            			respuesta.put("msgRegistro", psRegistro);
            			respuesta = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal, mapCreaArch.get("psBanco").toString(),
									   mapCreaArch.get("nomArchivo").toString(),respuesta.get("msgRegistro").toString());
  
        					String ruta=dirPrincipal+mapCreaArch.get("psBanco").toString()+mapCreaArch.get("nomArchivo").toString();
        					System.out.println("...::TODA LA RUTA PARA crar el file::.. "+ruta);
							if(h2hAfrd==1){
				        		
				        			try {
				        	String cadenaR=mapCreaArch.get("nomArchivo").toString();
				        	String cadena2=null;
				        	cadena2=cadenaR.replace("PA", "PAGOSDIARIOS");
				        			String ruta2 =dirPrincipal+mapCreaArch.get("psBanco").toString()+cadena2;
				        					File archivo = new File(ruta);
					        		        archivo.renameTo(new File(ruta2));
					        		        ruta="";
				        			} catch (Exception e) {
				        			
				        			}
				        	}
            		}else{
       					respuesta.put("msgUsuario", "No se armo el triler!!");
       					return respuesta;
        			}
    				
    				if(!(Boolean)respuesta.get("resExito")) {
    					respuesta.put("msgUsuario", "Error al Generar Archivo " + mapCreaArch.get("psBanco").toString());
						return respuesta;
					}
			}

		   
		    
		    
		    String psTipoEnvio = "C";
		    ArchTransferDto insArchTrans = new ArchTransferDto(); 
			insArchTrans.setNomArch(nomArchivo);
			insArchTrans.setIdBanco(piBanco);
			insArchTrans.setImporte(pdImporte);
			insArchTrans.setFecTrans(envioTransferenciasDao.obtenerFechaHoy());
			insArchTrans.setRegistros(piRegistros);
			insArchTrans.setNoUsuarioAlta(GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario());
			insArchTrans.setTipoEnvioLayout(psTipoEnvio);
			   
			envioTransferenciasDao.insertarArchTransfer(insArchTrans);
			int piBanco2 = piBanco;
			//dirPrincipal = ConstantesSet.CARPETA_RAIZ_RESPALDO;
			String psRutaArchivos = dirPrincipal;
			String psSoloArchivos = nomArchivo;
			 //String dirPrincipal="";
			 //dirPrincipal =ConstantesSet.CARPETA_RAIZ_CHEQUE_OCURRE;
			 //dirPrincipal+=ps_directorio;
			 dirPrincipal+=psBanco;
			 //psRutaArchivos = dirPrincipal;
			 psSoloArchivos = nomArchivo;
			 String salida = "";	  
//			 if(piBanco==14 && chkSantanderH2H){
///////////////////////////////daniel////////////////////////////////////////
//					 psRutaArchivos = dirPrincipal;
//					// psRutaArchivos = dirPrincipal;
//					psSoloArchivos = nomArchivo;
//					 //StringBuffer mapFtp = creacionArchivosBusiness.creaArchivoFtp(psSoloArchivos);  
//			 		 //creacionArchivosBusiness.escribirArchivoLayout("C:\\apicifrado\\cifradoSalida\\", "", "ftp" +  GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()  + ".bat", mapFtp.toString());
//					 salida = creacionArchivosBusiness.enviaH2H(psRutaArchivos, psSoloArchivos);	 
//	   			
//			}
			 if(pbEscribi) { //'si no escribió ningun registro se sale
				 if(!lsYaEjecutados.equals("")&&!salida.equals("")){
					 for (MovimientoDto movimientoDto : movimientos) {
						 chequeOcurreDao.regresaEstatusMovimiento(movimientoDto.getNoFolioDet());
					 }
					 chequeOcurreDao.eliminaArchivosErroneos(nomArchivo);	   
				 }   
			 			   //respuesta.put("msgUsuario", "Los siguientes documentos ya fueron procesados:" + lsYaEjecutados.substring(0,lsYaEjecutados.length()-1));
		                   //return respuesta;
			 }
			 else{ 
				 respuesta.put("msgUsuario", "No se realizó ninguna tarea");
			 }
				   
			 respuesta.put("msgUsuario", "Los Registros han sido Guardados en el archivo: "  + nomArchivo);		
			 System.out.println("respuesta de business "+respuesta.toString());
			 return respuesta; 
	}
	//get y set
	public ChequeOcurreDao getChequeOcurreDao() {
		return chequeOcurreDao;
	}
	
	public void setChequeOcurreDao(ChequeOcurreDao chequeOcurreDao) {
		this.chequeOcurreDao = chequeOcurreDao;
	}
	
	public CreacionArchivosBusiness getCreacionArchivosBusiness() {
		return creacionArchivosBusiness;
	}
	
	public void setCreacionArchivosBusiness(
			CreacionArchivosBusiness creacionArchivosBusiness) {
		this.creacionArchivosBusiness = creacionArchivosBusiness;
	}
	public LayoutBancomerBusiness getLayoutBancomerBusiness() {
		return layoutBancomerBusiness;
	}
	public void setLayoutBancomerBusiness(
			LayoutBancomerBusiness layoutBancomerBusiness) {
		this.layoutBancomerBusiness = layoutBancomerBusiness;
	}
	
	public EnvioTransferenciasDao getEnvioTransferenciasDao() {
		return envioTransferenciasDao;
	}
	public void setEnvioTransferenciasDao(EnvioTransferenciasDao envioTransferenciasDao) {
		this.envioTransferenciasDao = envioTransferenciasDao;
	}
	public String comandosH2H(String psRutaArchivos,String psSoloArchivos) throws IOException{
		 String salida ="";	  
		 String comando = "cmd /c move " + " "+  psRutaArchivos+psSoloArchivos +" " + " C:\\apicifrado\\CifradoEntrada\\" ;
		 String cmd = envioTransferenciasDao.obtenerComando();
		 String comand = "cmd /c " + cmd + " C:\\apicifrado\\CifradoEntrada\\" +psSoloArchivos +" " + " C:\\apicifrado\\CifradoSalida" ;
		 String comandr ="cmd /c rename C:\\apicifrado\\CifradoEntrada\\" +psSoloArchivos +" "+ psSoloArchivos.substring(0, psSoloArchivos.length()-3) +".bak" ;
		 String comandoCopiaArch = "cmd /c copy C:\\apicifrado\\CifradoEntrada\\" + psSoloArchivos.substring(0, psSoloArchivos.length()-3) +".bak" +" " + " C:\\apicifrado\\old" ;
		 String comandCambiaExt ="cmd /c rename C:\\apicifrado\\old\\" +psSoloArchivos.substring(0, psSoloArchivos.length()-3) +".bak" +" "+ psSoloArchivos.substring(0, psSoloArchivos.length()-3) +".old" ;
		 String comandoEliminar ="cmd /c del /f /q  C:\\apicifrado\\CifradoEntrada\\"+psSoloArchivos.substring(0, psSoloArchivos.length()-3) +".occ";
		 String comandoFtp="cmd /c C:\\apicifrado\\cifradoSalida\\ftp.bat";
		 if (comando != null){
			 // Ejecucion Basica del Comando
		     Process proceso = Runtime.getRuntime().exec(comando);
		     InputStreamReader entrada = new InputStreamReader(proceso.getInputStream());
		     BufferedReader stdInput = new BufferedReader(entrada);
		     //Si el comando tiene una salida la mostramos
		     if((salida=stdInput.readLine()) != null){
		    	 System.out.println("Movimiento correctamente");
			     while ((salida=stdInput.readLine()) != null){
			     	System.out.println(salida);
			     }
			 }else{
				 salida="Error al Generar Archivo";
			     comando= null;	
			 }
		 }
		 if (comand != null && comando != null ){
			 // Ejecucion Basica del Comando
			 Process proceso = Runtime.getRuntime().exec(comand);
			 InputStreamReader entrada = new InputStreamReader(proceso.getInputStream());
			 BufferedReader stdInput = new BufferedReader(entrada); 
			 //Si el comando tiene una salida la mostramos
			 if((salida=stdInput.readLine()) != null){
				 System.out.println("Comando ejecutado Correctamente para cifrar");
				 while ((salida=stdInput.readLine()) != null){
					 System.out.println(salida);
				 }
			 }else{
				 salida="Error al Generar Archivo";
				 comand= null;	
			 }
		 } 
		 else{
			 comand= null;
		 }
		 if (comando != null && comand != null && comandr != null){
			 // Ejecucion Basica del Comando
			 Process proceso = Runtime.getRuntime().exec(comandr);
			 InputStreamReader entrada = new InputStreamReader(proceso.getInputStream());
			 BufferedReader stdInput = new BufferedReader(entrada);
			 //Si el comando tiene una salida la mostramos
			 if((salida=stdInput.readLine()) == null){
				 System.out.println("Comando ejecutado Correctamente para el bak");
				 while ((salida=stdInput.readLine()) != null){
					 System.out.println(salida);
				 }
			 }else{
				 salida="Error al Generar Archivo";
				 comandr= null;	
			 }
			 //////////////COMANDOS NUEVOS DANIEL//////////////////////////
			 // Ejecucion Basica del Comando
			 Process procesoFtp = Runtime.getRuntime().exec(comandoCopiaArch);
			 InputStreamReader entradaFtp = new InputStreamReader(procesoFtp.getInputStream());
			 BufferedReader stdInputFtp = new BufferedReader(entradaFtp);
			 if((salida=stdInput.readLine()) == null){
				 System.out.println("Copia de archivos a la carpeta OLD Exitosa");
				 while ((salida=stdInput.readLine()) != null){
					 System.out.println(salida);
				 }
			 }else{
				 salida="Error al Generar Archivo";
				 comando= null;	
			 }            
			 procesoFtp = Runtime.getRuntime().exec(comandCambiaExt);
			 entradaFtp = new InputStreamReader(procesoFtp.getInputStream());
			 stdInputFtp = new BufferedReader(entradaFtp);
			 if((salida=stdInput.readLine()) == null){
				 System.out.println("Rename del archivo OLD exitoso");
				 salida="";
				 while ((salida=stdInput.readLine()) != null){
					 System.out.println(salida);
				 }
			 }else{
				 salida="Error al Generar Archivo";
				 comando= null;	
			 }    
			 procesoFtp = Runtime.getRuntime().exec(comandoFtp);
			 entradaFtp = new InputStreamReader(procesoFtp.getInputStream());
			 stdInputFtp = new BufferedReader(entradaFtp);
			 //Si el comando tiene una salida la mostramos
			 if(stdInputFtp.readLine()!= null){
				 System.out.println("Ejecucion del comando BAT exitosa");
			 }else{
				 salida="Error al Generar Archivo";
				 comando= null;	
			 }
			 procesoFtp = Runtime.getRuntime().exec(comandoEliminar);
			 entradaFtp = new InputStreamReader(procesoFtp.getInputStream());
			 stdInputFtp = new BufferedReader(entradaFtp);
			 if((salida=stdInput.readLine()) == null){
				 System.out.println("ELIMINACION DE ARCHIVOS DE CIFRADO ENTRADA");
				 salida="";
				 while ((salida=stdInput.readLine()) != null){
					 System.out.println(salida);
					 salida="";
				 }
			 }else{
				 salida="Error al Generar Archivo";
				 comando= null;	
			 }
		                ///////////////COMANDOS NUEVOS DANIEL////////////////////////
		 }
		 else{
			 comandr= null;
		 }
		 creacionArchivosBusiness.eliminarArchivo("C:\\apicifrado\\", "cifradoSalida\\","ftp.bat");
		 return salida;
	}
	
	
	public List<MovimientoDto> consultaCheques(CriterioBusquedaDto dtoBus){
		List<MovimientoDto> list= new ArrayList<MovimientoDto>();
		try{
			if(dtoBus.getIdBanco()==ConstantesSet.NAFIN)
				list=envioTransferenciasDao.consultarPagosNafin(dtoBus);
			else if(dtoBus.isChkMassPayment())
			{
				if(envioTransferenciasDao.consultarConfiguraSet(273)!= null && 
						envioTransferenciasDao.consultarConfiguraSet(273).equals("SI"))
				{
					dtoBus.setChkMassPayment(true);
					list=envioTransferenciasDao.consultarPagosMassPayment(dtoBus);
				}
				else
				{
					dtoBus.setChkMassPayment(false);
					list=envioTransferenciasDao.consultarPagosMassPayment(dtoBus);
				}
				
			}else{
				list=chequeOcurreDao.consultaCheques(dtoBus);
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



}
