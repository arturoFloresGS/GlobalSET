package com.webset.set.impresion.business;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.PrintJob;
import java.awt.Toolkit;
import java.awt.print.PrinterJob;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.apache.axis.AxisFault;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.bancaelectronica.dao.EnvioTransferenciasDao;
import com.webset.set.bancaelectronica.dto.CatBancoDto;
import com.webset.set.bancaelectronica.dto.DetArchTransferDto;
import com.webset.set.impresion.dao.ReimpresionDao;
import com.webset.set.impresion.dto.CatFirmasDto;
import com.webset.set.impresion.dto.ChequeImpresionDto;
import com.webset.set.impresion.dto.ConfiguracionChequeDto;
import com.webset.set.impresion.dto.ConsultaChequesDto;
import com.webset.set.impresion.dto.ControlPapelDto;
import com.webset.set.impresion.dto.LayoutProteccionDto;
import com.webset.set.impresion.service.ReimpresionService;
import com.webset.set.ingresos.dto.CatCtaBancoDto;
import com.webset.set.interfaz.dto.GuiaContableDto;
import com.webset.set.layout.business.CreacionArchivosBusiness;
import com.webset.set.layout.business.LayoutProteccionHSBCBusiness;
import com.webset.set.traspasos.dto.MovimientoDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.LlenaComboImpresora;

import mx.com.gruposalinas.ImpresionCheques.DT_ImpresionCheques_OBCheques;
import mx.com.gruposalinas.ImpresionCheques.SOS_ImpresionChequesBindingStub;
import mx.com.gruposalinas.ImpresionCheques.SOS_ImpresionChequesServiceLocator;

public class ReimpresionBusinessImpl implements ReimpresionService{
	
	private ReimpresionDao reimpresionDao;
	private Bitacora bitacora =  new Bitacora();
	private GlobalSingleton globalSingleton;
	private Funciones funciones = new Funciones();
	private LayoutProteccionHSBCBusiness layoutProteccionHSBCBusiness;
	private CreacionArchivosBusiness creacionArchivosBusiness;
	private EnvioTransferenciasDao envioTransferenciasDao;
	public EnvioTransferenciasDao getEnvioTransferenciasDao() {
		return envioTransferenciasDao;
	}

	public void setEnvioTransferenciasDao(EnvioTransferenciasDao envioTransferenciasDao) {
		this.envioTransferenciasDao = envioTransferenciasDao;
	}

	private File creaArchivo = null;
	
	private static Logger logger = Logger.getLogger(ImpresionBusinessImpl.class);
	Map<String, Object> mapCreaArch = new HashMap<String,Object>();
	
	public Map<String, Object>consultarChequesPendientes(ConsultaChequesDto dtoCheques){
		Map<String,Object> retorno = new HashMap<String, Object>();
		List<MovimientoDto> listaCheques = new ArrayList<MovimientoDto>();
		List<CatFirmasDto> listaFirmas = new ArrayList<CatFirmasDto>();
		ArrayList<String> mensajes = new ArrayList<String>();
		String cuenta = "";
		int banco = 0;
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			dtoCheques.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
			dtoCheques.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());
			listaCheques = reimpresionDao.consultarChequesPendientes2(dtoCheques);
			
			for(int i = 0; i < listaCheques.size(); i ++)
			{
				//si es impresion laser...
				if(dtoCheques.getPsbImpreContinua().equals("N"))
				{
					cuenta = listaCheques.get(i).getCtaAlias();
					banco = listaCheques.get(i).getBcoCve();
					listaFirmas = reimpresionDao.consultarFirmasChequera(cuenta, banco);
					if(listaFirmas.size() <= 0)
					{
						listaCheques.remove(i);
						mensajes.add("No hay firmas para la cuenta " + cuenta + " del banco " + banco);
						retorno.put("mensaje", mensajes);
					}
				}
				retorno.put("listaCheques", listaCheques);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ReimpresionBusinessImpl, M:consultarChequesPendientes");
			e.printStackTrace();
		}
		return retorno;
	}
	
	/**
	 * Devuelve la lista de cheques pendientes de imprimir.
	 * 
	 * @args ConsultaChequesDto dtoCheques. Criterios de busqueda.
	 * @return List<MovimientoDto>.
	 */
	//Revisado EMS 16/12/2015
	public List<MovimientoDto>consultarChequesPendientes1(ConsultaChequesDto dtoCheques)
	{
		List<MovimientoDto> listaCheques = new ArrayList<MovimientoDto>();
		
		try{
			listaCheques = reimpresionDao.consultarChequesPendientes2(dtoCheques);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:ReimpresionBusinessImpl, M:consultarChequesPendientes1");
			e.printStackTrace();
		}
		return listaCheques;
	}
	
	/**
	 * proceso de impresión de cheques laser o continuo
	 */
	public Map<String, Object> ejecutarImpresionCheques(
		List<MovimientoDto> listCheques, 
		ConsultaChequesDto dtoBusCheques,
		String idConfiguracion) {
		Map<String,Object> retorno = new HashMap<String, Object>();
		//List<String> lMensajesRet = new ArrayList<String>();
		
		retorno.put("error", "");
		retorno.put("datos", "");
		
		try {
			
			/* EMS 13/01/2016 - Ciclo se agrega para imprimir más de un cheques, anteriormente solo se permitia seleccionar un cheque en el JS */
			//for(int inc = 0; inc < listCheques.size(); inc++){	//Todavía no.
			
		    boolean bOptImpresionContinua = dtoBusCheques.getPsbImpreContinua().equals("S");	        
		    int iBanco = listCheques.get(0).getIdBanco();        
		    
	    	/* EMS: 13/01/2016 - Se comenta condición de firmados ya que no se toma en cuenta los firmados para GSalinas. 
	    	if(reimpresionDao.seleccionarBHabilita().equals("S") && 
	    			!bOptImpresionContinua &&
	    			!dtoBusCheques.isOptFirmados()) {
	    			retorno.put("msgUsuario", 
	    					"Para imprimir los cheques es " +
	    					"necesario seleccionar la opción " +
	    					"de cheques firmados!");
	    			return retorno;	    		
	    	}
	    	*/
	    	
		    /* EMS: 13/01/2016 - Se comenta condición de equivalencia ya que no se toma en cuenta para GSalinas.*/
			/*List<CatEquivaleCuentaContableDto> lEquivalencia = 
				reimpresionDao.consultarEquivalenciaCtaContable(
							listCheques.get(0).getNoEmpresa(),
							listCheques.get(0).getIdChequera());
			
			if (lEquivalencia.size() <= 0) {
				retorno.put("msgUsuario", 
						"Error, No se encontro cuenta " +
						"contable equivalente a la chequera: " + 
						listCheques.get(0).getIdChequera() + 
						" y empresa:" + listCheques.get(0).getNoEmpresa());
				return retorno;
			}*/
			
		  /*
		   * Se valida la existencia de folios activos en control papel.
		   * Se valida la existencia de folios para imprimir en controlPapel
		   * 26 ene 2016
		   */
		    boolean existenHojas=false;
		    int totalHojas =0;
		    if (!bOptImpresionContinua){
		    	existenHojas= reimpresionDao.existeControlPapel(iBanco, "P", "", "", "A");
		    }else{
		    	existenHojas = reimpresionDao.existeControlPapel(iBanco, "C",listCheques.get(0).getIdChequera()+"" , listCheques.get(0).getNoEmpresa()+"", "A");
		    }
		    if(!existenHojas){
		    	retorno.put("error", "Error: No existen hojas foleadas para la impresion. ");
		    	return retorno;
		    }
		    /*Fin de validacion de controlpapel para la impresion  26 ene 2016*/ 
		    
		    
		    if(existenHojas){ //26-01-2016 Si existe registro en control papel con estatus "A" continua
		     	
		     	
				if (!bOptImpresionContinua) { 
					totalHojas= reimpresionDao.totalHojasControlPapel(iBanco, "", "", false); //26-01-2016 Se obtiene el número total de hojas en control papel 
			     	if(totalHojas==0){ //26-01-2016 si no encuentra hojas termina el proceso.
			     		reimpresionDao.actualizarEstatusControlPapel(reimpresionDao.datosControlPapel(iBanco, "P", "", "", "A"), "T"); //Actualiza el estatus de la chequera en A.
			     		retorno.put("error", "Error: No existen hojas foleadas para la impresion. ");
				    	return retorno;
			     	}//Si no continua el proceso normal. 
					//impresion laser ---------------------------------------------------
						int iNoImpresora = dtoBusCheques.getNumImpresora();
						int iCaja = dtoBusCheques.getIdCaja();
						/*List<LlenaComboImpresora> listCharolas = 
			    			reimpresionDao.consultarCharolasImpresora(iNoImpresora, iCaja);
			    		*/
						//Ingreso 1 ya que noImpresora ya no se maneja.
							/*List<LlenaComboImpresora> listCharolas = reimpresionDao.consultarCharolasImpresora(1, iCaja); 
							
				    		if (listCharolas.size() <= 0) {
				    			retorno.put("error", "La impresora no tiene charolas registradas");
					        	return retorno;
							}
				    		 */
						
				    		/**solo para impresión laser confirmación de papel en charolas
				    		CHECA INCONSISTENCIA implementado para una sola charola
			                checa que el papel sea suficiente*/
				    		
				    		int iL_NumSol = 0;
							for (int n = 0; n < listCheques.size(); n ++) {
								if (listCheques.get(n).getIdBanco() == dtoBusCheques.getBancoCharola())
									iL_NumSol = iL_NumSol + 1;
							}
							int iL_Papel =  dtoBusCheques.getFolioFinal() - 
									(dtoBusCheques.getFolioInicial() - 1);
										
							List<ControlPapelDto> sigChequera = new ArrayList<>();
							
							if (iL_NumSol > iL_Papel) {
								int totalHoja = reimpresionDao.totalHojasControlPapel(iBanco, "", "", false);
								//Realiza consulta y ve si hay otro lote de papeles de control papel con estatus I 
								//Para ver si puede continuar imprimiendo si los folios_papel actuales no alcanzan.
								if(iL_NumSol > totalHoja){
									retorno.put("error", "El papel no es suficiente para la impresión");
									return retorno;
								}
								//sigChequera = reimpresionDao.consultarSigChequera(iBanco,"P");
							
								//if(sigChequera.size() <= 0){
									//retorno.put("error", "El papel no es suficiente para la impresión");
									//return retorno;
								//}
								
							}
				    		//FIN CHECA INCONSISTENCIA
							
				    		//CHECA FOLIOS
//				    		'checa que los folios caigan en el rango del inventario
//				    	    'y que sean consecutivos
				    		
				    		List<ControlPapelDto> foliosPapel = reimpresionDao.consultarFoliosPapel(iBanco, iCaja);
				    		
				    		boolean checaFolios = true;
				    		boolean iniCheca = false;
				    		boolean finCheca = false;
				    		int folioIni = 0;
				    		int folioFin = 0;
				    		String sNomBanco = listCheques.get(0).getDescBanco();
				    		if (foliosPapel.size() <= 0) {
				    			retorno.put("error", "No hay papel en el inventario para el banco " + sNomBanco);
				    			return retorno;
				    		} else {
				    			
				    			iniCheca = false;
				    			finCheca = false;
				    	        //Comente el ciclo ya q solo debe mandar 1 registro por banco
				    			//for (int k = 0; k < foliosPapel.size(); k ++) {
				    			
				    				folioIni = foliosPapel.get(0).getFolioInvIni();
				    				folioFin = foliosPapel.get(0).getFolioInvFin();
				    				int iG_FolioLaser = dtoBusCheques.getFolioInicial(); //texto 1
				    	            
				    	            iniCheca = (iG_FolioLaser >= folioIni && iG_FolioLaser <= folioFin);
				    	            
				    	            finCheca = (dtoBusCheques.getFolioFinal() >= folioIni && dtoBusCheques.getFolioFinal() <= folioFin);
				    	            
				    	                //checa que el folio del papel sea el siguiente
				    	          //MS: quito este mensaje para que en la presentacion no truene hay que revisar
				    	          /*  if (iG_FolioLaser != (foliosPapel.get(k).getFolioUltImpreso() + 1)) {//if Val(texto1) <> rst_folios!folio_ult_impreso + 1 Then
				    	            	retorno.put("msgUsuario", 
				    	            			"El folio inicial de la impresion de " + sNomBanco +
				    	            			" no coincide con el consecutivo de impresion " +
				    	            			"siguiente: " + (dtoBusCheques.getFolioFinal() -1));
				    	            	return retorno;
				    	               
				    	            }
				    	            */
				    			//}
				    	        
				    			if (!iniCheca || !finCheca) {
				    				
				    				if(sigChequera.size() <= 0){
				    					//Si no hay mas folios papel
				    					retorno.put("error", 
					    						"Los folios capturados para el banco " + 
					    						sNomBanco + " no checan con el inventario " +
					    						"de papel: " + folioIni + " al " + folioFin);
					    	            checaFolios = false;
					    	            
					    	            return retorno;
					    	            
									}else{
										//Si hay mas folios papel verifica que alcancen con los nuevos folios del otro lote.
										
						    			finCheca = false;
						    			
					    				folioIni = sigChequera.get(0).getFolioInvIni();
					    				folioFin = sigChequera.get(0).getFolioInvFin();
					    				int iG_FolioLaser2 = dtoBusCheques.getFolioInicial(); //texto 1
					    	            
					    	            /*iniCheca = (iG_FolioLaser >= folioIni && 
					    	            		iG_FolioLaser <= folioFin);
					    	            */
					    				//Solo reviso el final del siguiente lote
					    	            finCheca = (dtoBusCheques.getFolioFinal() >= folioIni && 
					    	            		dtoBusCheques.getFolioFinal() <= folioFin);
					    	            
					    	            if (!iniCheca || !finCheca) {
						    				
					    					retorno.put("error", 
						    						"Los folios capturados para el banco " + 
						    						sNomBanco + " no checan con el inventario " +
						    						"de papel: " + folioIni + " al " + folioFin +
						    						" del siguiente lote de papel.");
						    	            checaFolios = false;
						    	            
						    	            return retorno;
					    	            }
									} //fin else
				    				
				    			}
				    	        foliosPapel.clear();
				    	        sigChequera.clear();
				    	        
				    		}// fin checa folios!!!
				    		
				    		/*EMS 13/01/16: Comento la actualizacion de charolas ya que no se usan en GSalinas y toma por default siempre la imrpesora 1 y la charola 1.
				    		boolean bG_Sali = true;
				    		int iBancoCharola = dtoBusCheques.getBancoCharola();
				    		if (checaFolios) {
					    		//actualiza las charolas para la próxima impresión		    		
					    		bG_Sali = (reimpresionDao.actualizarCharolas(iBancoCharola, iNoImpresora) < 0);
				    		}
				    		
				    		if (bG_Sali) {
				    			//MS: lo comento ya que en ocasiones marca que se debe salir como si no actualizara las charolas
				    			//retorno.put("msgUsuario", "Error en la impresion");
				            	//return retorno;
				    		}*/
				    		
				    	//Termina impresion laser ----------------------------------------------
						
					} else { 
					//impresion continua ---------------------------------------------------
						//checa si la chequera puede imprimir cheque continuo
						int iEmpresa = dtoBusCheques.getNoEmpresa();
						String sChequera = listCheques.get(0).getIdChequera();
						String sMoneda = listCheques.get(0).getMoneda();
						int totalhojas=reimpresionDao.totalHojasControlPapel(iBanco, sChequera, iEmpresa+"", true);
						//26/01/2016 se valida el que el total de hojas sea suficiente para la impresión.
						if(totalhojas==0 || listCheques.size() > totalhojas){
							if(totalhojas==0) //Si no se encontraron hojas se actualiza el estado de control papel.
								reimpresionDao.actualizarEstatusControlPapel(reimpresionDao.datosControlPapel(iBanco, "C", sChequera,iEmpresa+ "", "A"), "T");
							retorno.put("error", "Advertencia: El número de cheques en inventario no es suficiente para la impresión");
							return retorno;
						}
				        /*List<CatCtaBancoDto> listContinuo = reimpresionDao.consultarImpresionContinua(iBanco, sChequera);
					        if (listContinuo.size() > 0) {
					        	String impreContinua = listContinuo.get(0).getBImpreContinua() == null ? "N" : listContinuo.get(0).getBImpreContinua();
					        	if (impreContinua.equals("N")) {
					        		retorno.put("error", 
					        				"La chequera seleccionada no esta " +
					        				"habilitada para impresión continua");
					        		return retorno;
					        	}
					        } else {
					        	retorno.put("error", 
					        			"Error al buscar la chequera para validar");
					        	return retorno;
					        }
					      */  
					        //checa si los bancos seleccionados tienen cheques configurados
					        List<ConfiguracionChequeDto> listConfigurados = reimpresionDao.consultarConfiguracionCheques(iBanco, sMoneda, iEmpresa, idConfiguracion);
			 
					        if (listConfigurados.size() > 0)
					        	listConfigurados.clear();
					        else {
					        	retorno.put("error", 
					        			"Debe seleccionar una configuración para la impresión del cheque");
					        	listConfigurados.clear();
					        	return retorno;
					        }
				    }
					// Termina Continua--------------------------------------------------------------
			}
		    
			
			/*lMensajesRet.add(
	    			imprimeCheques(
	    					listCheques, dtoBusCheques));
			*/
			
			retorno = imprimeCheques(listCheques, dtoBusCheques, idConfiguracion);
			
			
			//26-01-2016 Se valida el stock , en caso que los folios se terminen cambia el estatus de control papel si se encuentra alguno en I
			ControlPapelDto cp= new ControlPapelDto();
			if(bOptImpresionContinua){ 
				cp=  reimpresionDao.datosControlPapel(iBanco, "C", listCheques.get(0).getIdChequera()+"", listCheques.get(0).getNoEmpresa()+"", "A");
			}else{
				cp= reimpresionDao.datosControlPapel(iBanco, "P", "", "", "A");
			}
			
			int hojas=cp.getFolioInvFin()-cp.getFolioUltImpreso();
			
			if(hojas<cp.getStock() && hojas >0)
					retorno.put("msgUsuario", "Solo quedan "+ hojas + " hojas" );
			
			if(hojas==0){
				reimpresionDao.actualizarEstatusControlPapel(cp, "T");
				reimpresionDao.actualizarEstatusControlPapel(cp, "A");
				boolean existe=false;
				if(bOptImpresionContinua)
					existe= reimpresionDao.existeControlPapel(cp.getIdBanco(), "C", listCheques.get(0).getIdChequera()+"", listCheques.get(0).getNoEmpresa()+"", "A");
				else
					existe=reimpresionDao.existeControlPapel(iBanco, "P", "", "", "A");
				if(!existe)
					retorno.put("msgUsuario", "Advertencia: Ya no quedan hojas para la siguiente impresión" );
			}
			//26-01-2016 Se termina la validación del stock...	
			//retorno.put("msgUsuario", lMensajesRet);
	    	
        	return retorno;
		} catch (Exception e) {
			retorno.put("msgUsuario", "Cheques no impresos correctamente");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ReimpresionBusinessImpl, M:ejecutarImpresionCheques");
			e.printStackTrace();
		}
		return retorno;
	}
	
	/**
	 * imprime_cheques()
	 * @param listCheques
	 * @param dtoBusCheques
	 * @param idConfiguracion
	 * @return
	 */
	//Agregado parametro idConfiguración - EMS 29/01/16
	public Map<String, Object> imprimeCheques(List<MovimientoDto> listCheques, 	ConsultaChequesDto dtoBusCheques, String idConfiguracion){
		Map<String, Object> result = new HashMap<>();    
	    boolean bOptImpresionLaser = dtoBusCheques.getPsbImpreContinua().equals("N");
	    int iRenglon = 0;	    
	    String sIdBanco = "";
	    result.put("error", "");
		result.put("datos", "");
	    
		try {
			String arrCheques[][] = new String[listCheques.size()][3];
			
			for (int i = 0; i < listCheques.size(); i++) {
				String sCuenta = listCheques.get(i).getIdChequera(); //ctaAlias
				sIdBanco = String.valueOf(listCheques.get(i).getIdBanco()); //bcoCve
	            
				boolean bEncontrado = false;
				for (int j = 0; j < iRenglon; j++) {
					if (arrCheques[j][1].equals(sIdBanco) 
							&& arrCheques[j][0].equals(sCuenta)) {
						bEncontrado = true;
						break;
					}
				}
				if (!bEncontrado) {
					arrCheques[iRenglon][0] = listCheques.get(i).getIdChequera();
					arrCheques[iRenglon][1] = String.valueOf(listCheques.get(i).getIdBanco());
					arrCheques[iRenglon][2] = listCheques.get(i).getDescBanco();
					iRenglon++;
				}
			}
			
			/* 
			//listCheques.get(0).getNoFolioDet();
			
			//Las firmas no se contemplan en este proyecto (por el momento): EMS 11/01/2016
			if (bOptImpresionLaser) {
				//solo para impresion laser
		        //verifica las firmas predeterminadas para
		        //cada una de las cuentas de los bancos
		        for(int li = 0; li < iRenglon; li++) {
		        	int iBancoArreglo = Integer.parseInt(arrCheques[li][1]);
		        	String sCuentaArreglo = arrCheques[li][0];
		        	List<CatFirmasDto> firmas = new ArrayList<CatFirmasDto>();
		        	firmas = reimpresionDao.consultarContarFirmas(iBancoArreglo, sCuentaArreglo);
		            
		        	if ((firmas.size() > 0 && firmas.get(0).getNumFirmas() < 2) ||
		        			(firmas.size() <= 0)){
		        		result = "No se han determinado las firmas para" +
		        				"la cuenta No. " + sCuentaArreglo +
		        				" del banco " + arrCheques[li][2];
		                firmas.clear();
		                return result;
					}
		        	
		        	firmas.clear();
		        		
		        }
		        //variables globales del modChe  !!!!
			}
			*/
			
			//26-01-2016 se obtiene de movimiento la firma 1 y 2 para seguir con la impresion.
			
			/*	COMENTADO 31/03/2016 FALTA REVISION DE LAS FIRMAS.
			 * if (bOptImpresionLaser) {
				 for(int li = 0; li < iRenglon; li++) {
					 int iBancoArreglo = Integer.parseInt(arrCheques[li][1]);
					 int firma1=0;
					 int firma2=0;
					String sCuentaArreglo = arrCheques[li][0];
			       	firma1=reimpresionDao.obtenerFirmantes(iBancoArreglo, sCuentaArreglo, listCheques.get(li).getNoSolicitud()+"", 1);
			       	firma2=reimpresionDao.obtenerFirmantes(iBancoArreglo, sCuentaArreglo, listCheques.get(li).getNoSolicitud()+"", 2);   	
			        if(firma1==0 || firma2==0){
			        	result.put("error", "No se han determinado las firmas para" +"la cuenta No. " + sCuentaArreglo +" del banco " + arrCheques[li][2]);
			            return result;
				 	} else{
				 		listCheques.get(li).setFirmante1(firma1+"");
				 		listCheques.get(li).setFirmante2(firma2+"");
				 	}
				 }
			}
			*/
			
			//llamada al proceso de impresion
			//Agregado parametro configuracion 29/01/16
			//mandarNumerosDeChequeASap(convertirADtoSap(listCheques));
			result = progresoImpresion(listCheques, dtoBusCheques, idConfiguracion);

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ReimpresionBusinessImpl, M:imprimeCheques");
			e.printStackTrace();
		}
		return result;
	}
	
	
	private void mandarNumerosDeChequeASap(DT_ImpresionCheques_OBCheques[] convertirADtoSap) {
		try {
			SOS_ImpresionChequesServiceLocator service=
					new SOS_ImpresionChequesServiceLocator();
			SOS_ImpresionChequesBindingStub stub=
					new SOS_ImpresionChequesBindingStub(
							new URL(service.getHTTP_PortAddress()), service);
			stub.setUsername(
					reimpresionDao.consultarConfiguraSet(
							ConstantesSet.USERNAME_WS_IMPRESION));
			stub.setPassword(
					reimpresionDao.consultarConfiguraSet(
							ConstantesSet.PASSWORD_WS_IMPRESION));
			//DT_ImpresionCheques_ResponseCheques[] resp=
					stub.SOS_ImpresionCheques(convertirADtoSap);
			
			
			
		} catch ( MalformedURLException e1) {
			System.out.println("No se pudo conectar a SAP.");
			e1.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + e1.toString() 
			+ "P:Consultas, C:ConsultasBusiness, M:mandarContabilidadASap");
		} catch (AxisFault e1) {
			System.out.println("SAP ha tenido un problema al procesar los datos.");
			e1.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + e1.toString() 
			+ "P:Consultas, C:ConsultasBusiness, M:mandarContabilidadASap");
		} catch (Exception e) {
			System.out.println("Error al procesar el registro");
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Consultas, C:ConsultasBusiness, M:mandarContabilidadASap");
		}	
	}

	private DT_ImpresionCheques_OBCheques[] convertirADtoSap(List<MovimientoDto> listCheques) {
		DT_ImpresionCheques_OBCheques[] arreglo = new DT_ImpresionCheques_OBCheques[listCheques.size()];
		try {
			int i = 0;
			for (MovimientoDto mov : listCheques) {
				arreglo[i] = new DT_ImpresionCheques_OBCheques(
						funciones.ajustarLongitudCampo(
								String.valueOf(mov.getNoEmpresa()), 4, "D", "", "0"), mov.getPoHeaders(), String.valueOf(mov.getFecValor().getYear()+1900),
						String.valueOf(mov.getNoCheque()), mov.getObservacion(), "002");
						//String.valueOf(mov.getNoCheque()), mov.getObservacion(), mov.getInvoiceType());
				
				i++;
			}
			
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ReimpresionBusinessImpl, M:convertirADtoSap");
			e.printStackTrace();
		}return arreglo;
	}

	public int fCambiaImpresora(String sDriver, String sImpresoraAnt, boolean bCambiaPred){
		int iCambiaImpresora = 0;
		try
		{
			/*Public Function fCambiaImpresora(ByVal sNombre As String, ByRef sImpresoraAnterior As String, _
                    ByVal bCambiaPred As Boolean) As Integer
                    
			' Esta Función cambia la impresora default del sistema en caso de encontrarse
			'   una con el nombre que se pide. Regresa en sImpresoraAnterior
			'   un string (KEY) con el que se puede cambiar la impresora default por llave.*/
			
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ReimpresionBusinessImpl, M:fCambiaImpresora");
			e.printStackTrace();
		}
		return iCambiaImpresora;
	}
	
	
	public Map<String, Object> progresoImpresion(List<MovimientoDto> listCheques, ConsultaChequesDto dtoBusCheques, String idConfiguracion){
		
		Map<String, Object> result = new HashMap<>();
		StringBuffer cadResult = new StringBuffer();
		
		result.put("error", "");
		result.put("datos", "");
		
		boolean tieneCuentaContable = false;
		boolean tieneImporte = false;
		boolean tieneImporte2 = false;
		boolean tieneImporte3 = false;
		boolean tieneImporte4 = false;
		boolean tieneImporteLetra = false;
		boolean tieneNoAcreedor = false;
		boolean tieneNombreBanco = false;
		boolean tieneIdChequeraPag = false;
		boolean tieneIdChequeraBenef = false;
		boolean tieneNoCheque = false;
		boolean tieneNoCheque2 = false;
		boolean tieneFechaCheque = false;
		boolean tieneFechaCheque2 = false;
		boolean tieneDivisa = false;
		boolean tieneDocCompensacion = false;
		boolean tieneConcepto = false;
		boolean tieneConcepto2 = false;
		boolean tieneBeneficiario = false;
		boolean tieneBeneficiario2 = false;
		boolean tieneLeyenda = false;
		boolean tieneEmpresaPag = false;
		boolean tieneNombreEmpresa = false;
		boolean tieneNoDocto = false;
		boolean tieneNoDocto2 = false;
		boolean tieneFactura = false;
	
		try {
			globalSingleton = GlobalSingleton.getInstancia();			
			String gsTipoImpresora = "";			
			boolean bOptImpresionLaser = dtoBusCheques.getPsbImpreContinua().equals("N");
    		
			List<ConfiguracionChequeDto> listConfigurados;
			List<ChequeImpresionDto> lstChequeImp = new ArrayList<>();
			
			Map<String, String> mapConf = new HashMap<>();
			
		    if (bOptImpresionLaser) {
		    	int iNoImpresora = dtoBusCheques.getNumImpresora();
		    	int iCaja = dtoBusCheques.getIdCaja();
		    	
		    	List<LlenaComboImpresora> listCharolas = reimpresionDao.consultarCharolasImpresora(iNoImpresora, iCaja);
		        
	    		if (listCharolas.size() > 0) {
	    			gsTipoImpresora =listCharolas.get(0).getTipoImpresora() == null ? "" : listCharolas.get(0).getTipoImpresora();
	    		}
		    }else{ //Impresion continua, saca configuración de cheques.
			        listConfigurados = reimpresionDao.consultarConfiguracionCheques(
			        			listCheques.get(0).getIdBanco(), 
			        			listCheques.get(0).getMoneda(),
			        			listCheques.get(0).getNoEmpresa(),
			        			idConfiguracion);
			        
			        String nombreConf = reimpresionDao.obtieneDescripcionCatCheque(idConfiguracion);
			        
			        /** SE DECLARAN LOS CAMPO A CARGAR EN EL JS CON VALORES VACIOS, POR SI NO SE 
			         * CARGA NINGUN DATO NO SE MUESTRE COMO UNDEFINED AL IMPRIMIR EL CHEQUE	*/
        			mapConf.put("ctaContable","");
        			mapConf.put("importe","");
        			mapConf.put("importe2","");
        			mapConf.put("importe3","");
        			mapConf.put("importe4","");
        			mapConf.put("importeLetra","");
        			mapConf.put("beneficiario", "");
        			mapConf.put("beneficiario2", "");
        			mapConf.put("noAcreedor", "");		
        			mapConf.put("bancoPagador", "");
        			mapConf.put("chequeraBenef", "");
        			mapConf.put("chequeraPag", "");
        			mapConf.put("noCheque", "");
        			mapConf.put("noCheque2", "");
        			mapConf.put("fechaCheque", "");
        			mapConf.put("fechaCheque2", "");
        			mapConf.put("divO", "");
        			mapConf.put("docComp", "");
        			mapConf.put("concepto", "");
        			mapConf.put("concepto2", "");
        			mapConf.put("leyenda", "");
        			mapConf.put("empresaPag", "");
        			mapConf.put("nombreEmpresa", "");
        			mapConf.put("noDocto", "");
        			mapConf.put("noDocto2", "");
        			mapConf.put("factura", "");
        			
        			result.put("tamanoHoja", nombreConf);
        			
        			
			        if (listConfigurados.size() > 0){
			        	
			        	for(int k = 0; k < listConfigurados.size(); k++){
			        		if(listConfigurados.get(k).getCampo().equals("CUENTA CONTABLE")){
			        			tieneCuentaContable = true;
			        			mapConf.put("ctaContable", (listConfigurados.get(k).getFormato()!=null?listConfigurados.get(k).getFormato():"") );
			        			continue;
			        		}else if(listConfigurados.get(k).getCampo().equals("IMPORTE")){
			        			tieneImporte = true;
			        			mapConf.put("importe", (listConfigurados.get(k).getFormato()!=null?listConfigurados.get(k).getFormato():""));
			        			continue;
			        		}else if(listConfigurados.get(k).getCampo().equals("IMPORTE 2")){
			        			tieneImporte2 = true;
			        			mapConf.put("importe2", (listConfigurados.get(k).getFormato()!=null?listConfigurados.get(k).getFormato():""));
			        			continue;
			        		}else if(listConfigurados.get(k).getCampo().equals("IMPORTE 3")){
			        			tieneImporte3 = true;
			        			mapConf.put("importe3", (listConfigurados.get(k).getFormato()!=null?listConfigurados.get(k).getFormato():""));
			        			continue;
			        		}else if(listConfigurados.get(k).getCampo().equals("IMPORTE 4")){
			        			tieneImporte4 = true;
			        			mapConf.put("importe4", (listConfigurados.get(k).getFormato()!=null?listConfigurados.get(k).getFormato():""));
			        			continue;
			        		}else if(listConfigurados.get(k).getCampo().equals("IMPORTE EN LETRA")){
			        			tieneImporteLetra = true;
			        			mapConf.put("importeLetra", (listConfigurados.get(k).getFormato()!=null?listConfigurados.get(k).getFormato():""));
			        			continue;
			        		}else if(listConfigurados.get(k).getCampo().equals("BENEFICIARIO")){
			        			tieneBeneficiario = true;
			        			mapConf.put("beneficiario", (listConfigurados.get(k).getFormato()!=null?listConfigurados.get(k).getFormato():""));
			        			continue;
			        		}else if(listConfigurados.get(k).getCampo().equals("BENEFICIARIO 2")){
			        			tieneBeneficiario2 = true;
			        			mapConf.put("beneficiario2", (listConfigurados.get(k).getFormato()!=null?listConfigurados.get(k).getFormato():""));
			        			continue;	
			        		}else if(listConfigurados.get(k).getCampo().equals("NUMERO ACREEDOR")){
			        			tieneNoAcreedor = true;
			        			mapConf.put("noAcreedor", (listConfigurados.get(k).getFormato()!=null?listConfigurados.get(k).getFormato():""));
			        			continue;
			        		}else if(listConfigurados.get(k).getCampo().equals("BANCO PAGADOR")){
			        			tieneNombreBanco = true;
			        			mapConf.put("bancoPagador", (listConfigurados.get(k).getFormato()!=null?listConfigurados.get(k).getFormato():""));
			        			continue;
			        		}else if(listConfigurados.get(k).getCampo().equals("CHEQUERA BENEFICIARIA")){
			        			tieneIdChequeraBenef= true;
			        			mapConf.put("chequeraBenef", (listConfigurados.get(k).getFormato()!=null?listConfigurados.get(k).getFormato():""));
			        			continue;
			        		}else if(listConfigurados.get(k).getCampo().equals("CHEQUERA PAGADORA")){
			        			tieneIdChequeraPag = true;
			        			mapConf.put("chequeraPag", (listConfigurados.get(k).getFormato()!=null?listConfigurados.get(k).getFormato():""));
			        			continue;
			        		}else if(listConfigurados.get(k).getCampo().equals("NUMERO DE CHEQUE")){
			        			tieneNoCheque = true;
			        			mapConf.put("noCheque", (listConfigurados.get(k).getFormato()!=null?listConfigurados.get(k).getFormato():""));
			        			continue;
			        		}else if(listConfigurados.get(k).getCampo().equals("NUMERO DE CHEQUE 2")){
			        			tieneNoCheque2 = true;
			        			mapConf.put("noCheque2", (listConfigurados.get(k).getFormato()!=null?listConfigurados.get(k).getFormato():""));
			        			continue;
			        		}else if(listConfigurados.get(k).getCampo().equals("FECHA DEL CHEQUE")){
			        			tieneFechaCheque = true;
			        			mapConf.put("fechaCheque",(listConfigurados.get(k).getFormato()!=null?listConfigurados.get(k).getFormato():""));
			        			continue;
			        		}else if(listConfigurados.get(k).getCampo().equals("FECHA DEL CHEQUE 2")){
			        			tieneFechaCheque2 = true;
			        			mapConf.put("fechaCheque2", (listConfigurados.get(k).getFormato()!=null?listConfigurados.get(k).getFormato():""));
			        			continue;
			        		}else if(listConfigurados.get(k).getCampo().equals("DIVISA ORIGINAL")){
			        			tieneDivisa = true;
			        			mapConf.put("divO", (listConfigurados.get(k).getFormato()!=null?listConfigurados.get(k).getFormato():""));
			        			continue;
			        		}else if(listConfigurados.get(k).getCampo().equals("DOCUMENTO COMPENSACION")){
			        			tieneDocCompensacion = true;
			        			mapConf.put("docComp", (listConfigurados.get(k).getFormato()!=null?listConfigurados.get(k).getFormato():""));
			        			continue;
			        		}else if(listConfigurados.get(k).getCampo().equals("CONCEPTO")){
			        			tieneConcepto = true;
			        			mapConf.put("concepto", (listConfigurados.get(k).getFormato()!=null?listConfigurados.get(k).getFormato():""));
			        			continue;
			        		}else if(listConfigurados.get(k).getCampo().equals("CONCEPTO 2")){
			        			tieneConcepto2 = true;
			        			mapConf.put("concepto2", (listConfigurados.get(k).getFormato()!=null?listConfigurados.get(k).getFormato():""));
			        			continue;
			        		}else if(listConfigurados.get(k).getCampo().equals("LEYENDA")){
			        			tieneLeyenda = true;
			        			mapConf.put("leyenda", (listConfigurados.get(k).getFormato()!=null?listConfigurados.get(k).getFormato():""));
			        			continue;
			        		}else if(listConfigurados.get(k).getCampo().equals("EMPRESA PAGADORA")){
			        			tieneEmpresaPag = true;
			        			mapConf.put("empresaPag", (listConfigurados.get(k).getFormato()!=null?listConfigurados.get(k).getFormato():""));
			        			continue;
			        		}else if(listConfigurados.get(k).getCampo().equals("NOMBRE EMPRESA")){
			        			tieneNombreEmpresa = true;
			        			mapConf.put("nombreEmpresa", (listConfigurados.get(k).getFormato()!=null?listConfigurados.get(k).getFormato():""));
			        			continue;
			        		}else if(listConfigurados.get(k).getCampo().equals("NUMERO DE DOCUMENTO")){
			        			tieneNoDocto = true;
			        			mapConf.put("noDocto", (listConfigurados.get(k).getFormato()!=null?listConfigurados.get(k).getFormato():""));
			        			continue;
			        		}else if(listConfigurados.get(k).getCampo().equals("NUMERO DE DOCUMENTO 2")){
			        			tieneNoDocto2 = true;
			        			mapConf.put("noDocto2", (listConfigurados.get(k).getFormato()!=null?listConfigurados.get(k).getFormato():""));
			        			continue;
			        		}else if(listConfigurados.get(k).getCampo().equals("FACTURA")){
			        			tieneFactura = true;
			        			mapConf.put("factura", (listConfigurados.get(k).getFormato()!=null?listConfigurados.get(k).getFormato():""));
			        			continue;
			        		}
			        		
			        	}
			        	
			        	result.put("conf", listConfigurados);
			        	result.put("confLeyendas", mapConf);
			        }else {
			        	result.put("error", 
			        			"Debe seleccionar una configuración para la impresión del cheque");
			        	listConfigurados.clear();
			        	return result;
			        }
		    }
		    
		    for(int i = 0; i < listCheques.size(); i++) {
		    	String iGSolicitud = listCheques.get(i).getNoSolicitud()+"";
		    	int iNumCheque = listCheques.get(i).getNoCheque();
		    	//numero de banco, de cuenta y de cheque
	            int iBanco = listCheques.get(i).getIdBanco();
	            int iNoCheque = listCheques.get(i).getNoCheque();
	            Date fechaHoy = globalSingleton.getFechaHoy();
            	int usuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
            	Date fechaChequeCapturada = dtoBusCheques.getFechaCheque();
            	Date fechaCheque = listCheques.get(i).getFecCheque();
            	int caja = globalSingleton.getUsuarioLoginDto().getIdCaja();
	            String observacion = listCheques.get(i).getObservacion();
            	
            	int iAfectados = 0;
            	
            	int auxNumChe = bOptImpresionLaser ? iNoCheque : iNumCheque;
            	
            	/*iAfectados = reimpresionDao.actualizarMovimiento(fechaHoy, usuario, 
            													fechaChequeCapturada, fechaCheque, 
            													iGSolicitud, auxNumChe, observacion);
            	*/
            	
            	int noEmpresaTmp = listCheques.get(i).getNoEmpresa();
            	String fechaPropuestaTmp = funciones.ponerFechaSola(listCheques.get(i).getFecValor());
            	int cPeriodo = listCheques.get(i).getCPeriodo();
            	String noCliente = listCheques.get(i).getNoCliente();
            	int noFolioMov = listCheques.get(i).getNoFolioMov();
            	
            	iAfectados = reimpresionDao.actualizarMovimiento(fechaHoy, usuario, fechaChequeCapturada, 
            													fechaCheque, auxNumChe,observacion,
            													noFolioMov);
            	
	            if (iAfectados == 0) {
	            	result.put("error", "Error en validación movimiento");
	            	return result;
	            }
            
	            //Actualiza la caja

	            iAfectados = reimpresionDao.actualizarCaja(caja,noFolioMov);
	            
	            if (iAfectados == 0) {
	            	result.put("error", "Error en validación caja");
	            	return result;
	            }
            
	            if (bOptImpresionLaser){
	            //solo para impresion laser
	            		            	
	            	//26-01-2016
	            	ControlPapelDto cp = reimpresionDao.datosControlPapel(iBanco, "P", "", "", "A");
	            	int hojasRestantes= cp.getFolioInvFin()-cp.getFolioUltImpreso();
	            	if(hojasRestantes==0){
	            		reimpresionDao.actualizarEstatusControlPapel(cp, "T");
	            		reimpresionDao.actualizarEstatusControlPapel(cp, "A");
	            		cp=reimpresionDao.datosControlPapel(iBanco, "P", "", "", "A");
	            	}
	            	//FIN CONTROL PAPEL
	            	
	            	String resultCheque = "";
	            	
	                if (gsTipoImpresora.equals("E330") &&
	                		dtoBusCheques.isChkNuevoFormato()) {
	                	/*result = rutinaChequeLaserE352DN(
	                			i,listCheques, true, 
	                			dtoBusCheques.isChkNegociable(), 
	                			dtoBusCheques.isChkAbono(), gsTipoImpresora);
	                			*/
	                	 
	                	//EMS 14/01/16: Agregado el += para ver si imprime más de un cheque!!!
	                	//result = rutinaChequeLaserE652DN(
	                	resultCheque = rutinaChequeLaserE652DN(
	                			i,listCheques, true,
	                			dtoBusCheques.isChkNegociable(),
	                			dtoBusCheques.isChkAbono(), gsTipoImpresora);
	                	
	                	//EMS 15/01/2016: Concateno HTML para poder imprimir varios cheques a la vez en hojas diferentes cada uno.
	                	if(!resultCheque.equals("")){
	                		if(i==0){
	                			cadResult.append(resultCheque);
	                		}else{
	                			cadResult.append("<div class=\"page-break\"> " + resultCheque + " </div>");
	                		}
	                	}
	                	
	                }
	                
	                iAfectados = reimpresionDao.actualizaControlPapel(cp.getIdControlCheque());
	    			
	    			if(iAfectados <= 0){
	            		result.put("error", "Error al actualizar control papel");
		            	return result;
		            }
	    			
	    			
	            }else{
	            	//Impresión continua, obtengo y actualizo los datos (cat_ctas_banco y control_papel)
	            	//Retorno los datos como un objeto para armar el cheque en la página html.
	            	//Asigno el importe en letra en campo temporal comentario1 para que se visualice en
	            	
	            	//iAfectados = reimpresionDao.actualizaControlPapel(iBanco, caja);
	            	
	            	/*******Actualizar datos en control papel*****/
	            	ControlPapelDto cp = reimpresionDao.datosControlPapel(iBanco, "C", listCheques.get(i).getIdChequera()+"", listCheques.get(i).getNoEmpresa()+"", "A");
	            	int hojasRestantes= cp.getFolioInvFin()-cp.getFolioUltImpreso();
	            	if(hojasRestantes==0){
	            		reimpresionDao.actualizarEstatusControlPapel(cp, "T");
	            		reimpresionDao.actualizarEstatusControlPapel(cp, "A");
	            		cp = reimpresionDao.datosControlPapel(iBanco, "C", listCheques.get(i).getIdChequera()+"", listCheques.get(i).getNoEmpresa()+"", "A");
	            	}
	            	
	            	
	            	String importeLetra = "";
	    			importeLetra = funciones.convertirNumeroEnLetra(
	    										listCheques.get(i).getImporte());
	    			//importeLetra = importeLetra.replaceAll(" ", "&#255;");
	    			
	    			if(listCheques.get(i).getMoneda().equals("DLS")){
	    				importeLetra = importeLetra.replace("PESOS", "DOLARES");
	    				importeLetra = importeLetra.replace("M.N.", "USD");
	    			}
	    			
	            	listCheques.get(i).setComentario1(importeLetra);
	            	
	            	//Fecha con formato para impresion de cheque
	            	//String sMesActual = funciones.ponerFormatoDate(listCheques.get(i).getFecCheque()); 
	            	//String sMes = funciones.nombreMes(Integer.parseInt(sMesActual.substring(3, 5)));
	    			//String sMesCorto = funciones.nombreMesCorto(Integer.parseInt(sMesActual.substring(3, 5)));

	            	String sMesActual = funciones.ponerFormatoDate(dtoBusCheques.getFechaCheque()); 
	    			String sMes = funciones.nombreMes(Integer.parseInt(sMesActual.substring(3, 5)));
	    			String sMesCorto = funciones.nombreMesCorto(Integer.parseInt(sMesActual.substring(3, 5)));
	    			
	    			String fechaChequeFormato, fechaCorta = "";
	    			
	    			if(Integer.parseInt(sMesActual.substring(6, 10)) > 1999){
	    				fechaChequeFormato = sMesActual.substring(0, 2) + " DE " + sMes.toUpperCase() + " DEL " + sMesActual.substring(6, 10);
	    				fechaCorta = sMesActual.substring(0, 2) + " DE " + sMesCorto.toUpperCase() + " DEL " + sMesActual.substring(6, 10);
	    			}else{
	    				fechaChequeFormato = sMesActual.substring(0, 2) + " DE " + sMes.toUpperCase() + " DEL " + sMesActual.substring(6, 10);
	    				fechaCorta = sMesActual.substring(0, 2) + " DE " + sMesCorto.toUpperCase() + " DEL " + sMesActual.substring(6, 10);
	    			}
	    			//Inserto la fecha  temporalmente en comentario2
	    			listCheques.get(i).setComentario2(fechaChequeFormato);
	    			
	    			/*****	actualizo el folio papel y la chequera	*****/
	    			iAfectados = reimpresionDao.actualizaControlPapel(cp.getIdControlCheque());
	    			
	    			if(iAfectados <= 0){
	            		result.put("error", "Error al actualizar control papel");
		            	return result;
		            }
	    			
	    			iAfectados = reimpresionDao.actualizaCheque(listCheques.get(i).getNoCheque(), listCheques.get(i).getIdChequera());
	    			
	    			if(iAfectados <= 0){
	            		result.put("error", "Error al actualizar control papel");
		            	return result;
		            }
	    			/*********************	********************/
	    			
	    			//AGREGO LA LISTA DE CHEQUES (MOVIMIENTO) A LISTA DE CHEQUES IMPRESION, PARA LLEVAR CONTROL DE DATOS.
	    			ChequeImpresionDto cheque = new ChequeImpresionDto();
	    			
	    			/**********		VALIDO LA CONFIGURACIÓN DE CHEQUES, SI NO HAY ALGUNA CONFIGURACION SUSTITUYO EL VALOR A VACIO,
	    			 * 	03/03/2016 - CASO CONTRARIO ASIGNA EL VALOR EN CHEQUEIMPRESION PARA RETORNAR LA NUEVA LISTA.  **********/
	    			
	    			if(tieneCuentaContable){
	    				//Buscamos su cuenta contable y se la insertamos en direccionDivisas
	    				/*listCheques.get(i).setDireccionDivisas(
	    								   		reimpresionDao.obtieneCuentaContable(
	    								   				listCheques.get(i).getIdChequera(),
	    								   				listCheques.get(i).getIdBanco(),
	    								   				listCheques.get(i).getNoEmpresa(),
	    								   				listCheques.get(i).getImporte()
	    								   	));*/
	    				cheque.setCuentaContable(reimpresionDao.obtieneCuentaContable(
											   				listCheques.get(i).getIdChequera(),
											   				listCheques.get(i).getIdBanco(),
											   				listCheques.get(i).getNoEmpresa(),
											   				listCheques.get(i).getImporte()
											   	));
	    			}else{
	    				cheque.setCuentaContable("");
	    			}
	    			
	    			//De aqui para abajo las validacion de "tiene" estaban negadas, pero se asignaban vacio a la lista de Cheque-Movimiento
	    			if(tieneImporte && listCheques.get(i).getImporte() != 0){
	    				//cheque.setImporte(""+funciones.ponerFormatoImporte(listCheques.get(i).getImporte()));
	    				String r= parseaAsteriscosImporte(listCheques.get(i).getImporte(), (String)mapConf.get("importe"));
	    				if(!r.equals("")){
	    					cheque.setImporte(r);
	    				}else{
	    					//cheque.setImporte(""+listCheques.get(i).getImporte());
	    					cheque.setImporte(""+funciones.ponerFormatoImporte(listCheques.get(i).getImporte()));
	    				}
	    				
	    			}else{
	        			cheque.setImporte("");
	    			}
	    			
	    			if(tieneImporte2 && listCheques.get(i).getImporte() != 0){
	        			//cheque.setImporte2(""+listCheques.get(i).getImporte());
	    				//cheque.setImporte2(""+funciones.ponerFormatoImporte(listCheques.get(i).getImporte()));
	    				
	    				String r= parseaAsteriscosImporte(listCheques.get(i).getImporte(), (String)mapConf.get("importe2"));
	    				if(!r.equals("")){
	    					cheque.setImporte2(r);
	    				}else{
	    					//cheque.setImporte2(""+listCheques.get(i).getImporte());
	    					cheque.setImporte2(""+funciones.ponerFormatoImporte(listCheques.get(i).getImporte()));
	    				}
	    				
	    			}else{
	    				cheque.setImporte2("");
	    			}
	    				
	    			if(tieneImporte3 && listCheques.get(i).getImporte() != 0){
	    				//cheque.setImporte3(""+listCheques.get(i).getImporte());
	    				String r= parseaAsteriscosImporte(listCheques.get(i).getImporte(), (String)mapConf.get("importe3"));
	    				if(!r.equals("")){
	    					cheque.setImporte3(r);
	    				}else{
	    					//cheque.setImporte3(""+listCheques.get(i).getImporte());
	    					cheque.setImporte3(""+funciones.ponerFormatoImporte(listCheques.get(i).getImporte()));
	    				}
	    			}else{	
	    				cheque.setImporte3("");
	    			}
	    			
	    			if(tieneImporte4 && listCheques.get(i).getImporte() != 0){
	    				//qwer
	    				String r= parseaAsteriscosImporte(listCheques.get(i).getImporte(), (String)mapConf.get("importe4"));
	    				if(!r.equals("")){
	    					cheque.setImporte4(r);
	    					//cheque.setImporte4(""+funciones.ponerFormatoImporte(listCheques.get(i).getImporte()));
	    				}else{
	    					cheque.setImporte4(""+funciones.ponerFormatoImporte(listCheques.get(i).getImporte()));
	    					//cheque.setImporte4(""+listCheques.get(i).getImporte());
	    				}
	        		}else{
	        			cheque.setImporte4("");
	        		}
	    			
	    			if(tieneImporteLetra){
	    				cheque.setImporteLetra("("+importeLetra+")");
	        		}else{
	        			cheque.setImporteLetra("");
	        		}
	    			
	    			if(tieneBeneficiario && listCheques.get(i).getBeneficiario() != null){
	    				cheque.setBeneficiario(listCheques.get(i).getBeneficiario());
	        		}else{
	        			cheque.setBeneficiario("");
	        		}
	    			
	    			if(tieneBeneficiario2 && listCheques.get(i).getBeneficiario() != null){
	    				cheque.setBeneficiario2(listCheques.get(i).getBeneficiario());
	        		}else{
	        			cheque.setBeneficiario2("");
	        		}
	    			
	    			if(tieneNoAcreedor && listCheques.get(i).getEquivaleBenef() != null){
	    				cheque.setNoAcreedor(listCheques.get(i).getEquivaleBenef());
	        		}else{
	        			cheque.setNoAcreedor("");
	        		}
	    			
	    			if(tieneNombreBanco && listCheques.get(i).getDescBanco() != null){
	    				cheque.setBancoPagador(listCheques.get(i).getDescBanco());
	        		}else{
	        			cheque.setBancoPagador("");
	        		}
	    			
	    			if(tieneIdChequeraPag && listCheques.get(i).getIdChequera() != null){
	    				cheque.setChequeraPagadora(listCheques.get(i).getIdChequera());
	        		}else{
	        			cheque.setChequeraPagadora("");
	        		}
	    			
	    			if(tieneIdChequeraBenef && listCheques.get(i).getIdChequeraBenef() != null){
	    				cheque.setChequeraBeneficiaria(listCheques.get(i).getIdChequeraBenef());
	        		}else{
	        			cheque.setChequeraBeneficiaria("");
	        		}
	    			
	    			if(tieneNoCheque && listCheques.get(i).getNoCheque() != 0){
	    				cheque.setNoCheque(""+listCheques.get(i).getNoCheque());
	        		}else{
	        			cheque.setNoCheque("");
	        		}
	    			
	    			if(tieneNoCheque2 && listCheques.get(i).getNoCheque() != 0){
	    				cheque.setNoCheque2(""+listCheques.get(i).getNoCheque());
	        		}else{
	        			cheque.setNoCheque2("");
	        		}
	    			
	    			if(tieneFechaCheque && fechaChequeFormato != null){
	    				cheque.setFechaCheque(fechaChequeFormato);
	        		}else{
	        			cheque.setFechaCheque("");
	        		}
	    			
	    			if(tieneFechaCheque2 && fechaChequeFormato != null){
	    				cheque.setFechaCheque2(fechaChequeFormato);
	        		}else{
	        			cheque.setFechaCheque2("");
	        		}
	    			
	    			if(tieneDivisa && listCheques.get(i).getIdDivisaOriginal() != null){
	    				cheque.setDivisaOriginal(listCheques.get(i).getIdDivisaOriginal());
	        		}else{
	        			cheque.setDivisaOriginal("");
	        		}
	    			
	    			if(tieneDocCompensacion && listCheques.get(i).getPoHeaders() != null){
	    				cheque.setDocumentoCompensacion(listCheques.get(i).getPoHeaders());
	        		}else{
	        			cheque.setDocumentoCompensacion("");
	        		}
	    			
	    			if(tieneConcepto && listCheques.get(i).getConcepto() != null){
	    				cheque.setConcepto(listCheques.get(i).getConcepto());
	        		}else{
	        			cheque.setConcepto("");
	        		}
	    			
	    			if(tieneConcepto2 && listCheques.get(i).getConcepto() != null){
	    				cheque.setConcepto2(listCheques.get(i).getConcepto());
	        		}else{
	        			cheque.setConcepto2("");
	        		}
	    			
	    			/*if(!tieneLeyenda){
	    				
	        		}*/
	    			
	    			if(tieneEmpresaPag && listCheques.get(i).getNoEmpresa() != 0){
	    				cheque.setEmpresaPagadora(""+listCheques.get(i).getNoEmpresa());
	        		}else{
	        			cheque.setEmpresaPagadora("");
	        		}
	    			
	    			if(tieneNombreEmpresa){
	    				cheque.setNombreEmpresa(reimpresionDao.obtenerNombreEmpresa(
													listCheques.get(i).getNoEmpresa()));
	        		}else{
	        			cheque.setNombreEmpresa("");
	        		}
	    			
	    			if(tieneNoDocto && listCheques.get(i).getNoDocto() != null){
	    				cheque.setNoDocto(listCheques.get(i).getNoDocto());
	        		}else{
	        			cheque.setNoDocto("");
	        		}
	    			
	    			if(tieneNoDocto2 && listCheques.get(i).getNoDocto() != null){
	    				cheque.setNoDocto2(listCheques.get(i).getNoDocto());
	        		}else{
	        			cheque.setNoDocto2("");
	        		}
	    			
	    			if(tieneFactura && listCheques.get(i).getNoFactura() != null){
	    				cheque.setFactura(listCheques.get(i).getNoFactura());
	        		}else{
	        			cheque.setFactura("");
	        		}
	    			
	    			lstChequeImp.add(cheque);
	    			
	            }
	            
		    }	
		    
        	 if (bOptImpresionLaser){//laser
        		 result.put("datos", cadResult.toString());
        		 
        	 }else{ //continua
        		 result.put("datos", lstChequeImp);
        	 }
        	 
		} catch(Exception e ) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ReimpresionBusinessImpl, M:progresoImpresion");
			e.printStackTrace();
		}
		return result;
	}
	
	/** ----------------RUTINA DE IMPRESION DE CHEQUE DESDE NAVEGADOR --------------- **/
	public String rutinaChequeLaserE652DN(int renglon, List<MovimientoDto> listCheques,
			boolean imprime, boolean negociable, boolean abono, String tipoImpresora)
	{
		int iGFolioLaser = 0;
		int iSumaHorizontal = 0;
		int iConpeso = 0;
		int iPeso = 0;
		int iCalPeso = 0;
		long iDigitoPremarcado = 0;
		double importe = 0;
		String sAux = "";
		String sBancoTrunc = "";
		String importeLetra;
		String sFechaCheque = "";
		String sFechaCorta = "";
		String sLlaveIntercambio = "";
		String sDigitoIntercambio = "";
		String sTransito = "";
		String sDVCH = "";
		String sCerseg = "";
		String chequeLaser = "";
		
		// Se recupera la instancia del Singleton.
		globalSingleton = GlobalSingleton.getInstancia();
		
		try{
			//por el momento se deja asi la condicion ! para q no haga la busqueda
			if(!globalSingleton.obtenerValorConfiguraSet(1001).equals("")){
				
				PrintService[] impresoras =PrintServiceLookup.lookupPrintServices(null, null);
				PrintService impDefault = PrintServiceLookup.lookupDefaultPrintService();
				
				for(int i = 0; i < impresoras.length; i++) {
					
					if(impresoras[i].getName().toUpperCase().equals(globalSingleton.obtenerValorConfiguraSet(1001))){
						logger.info("Establecer esta impresora como predeterminada :" + impresoras[i].getName());
						impDefault = impresoras[i];
						break;
					}
				}
				logger.info("default : " + impDefault);
				
				if(!impDefault.equals(globalSingleton.obtenerValorConfiguraSet(1001))){
					chequeLaser = "No esta registrada la impresora para impresion de cheques";
					return chequeLaser;
				}
			}
			
			// Se obtiene la clave del banco y el nœmero de cuenta.
			int iBcoCve = listCheques.get(renglon).getIdBanco(); //renglon, 12
			String sNoCta = listCheques.get(renglon).getCtaNo(); //renglon, 19
			
			/* COMENTADO POR JRC 03/Dic/2014 MS: hay que descomentar esto y revisar por que se comento
			// Si es reimpresion no genera archivo.
			if(imprime)
			{
				switch (iBcoCve)
				{
					case 2: //'BANAMEX
						if(listCheques.get(renglon).getBProtegido().equals("S"))
							G_Reg_Banamex = G_Reg_Banamex + 1;
						
					case 161: //'BANCRECER
						if(listCheques.get(renglon).getBProtegido().equals("S"))
							G_Reg_Bancrecer = G_Reg_Bancrecer + 1;
						
					case 14: //'SANTANDER
						if(listCheques.get(renglon).getBProtegido().equals("S"))
							G_Reg_Santander = G_Reg_Santander + 1; //FALTA ARCHIVO PROTEGIDO
						
					case 12: //'BANCOMER
						if(listCheques.get(renglon).getBProtegido().equals("S"))
							G_Reg_Bancomer = G_Reg_Bancomer + 1; //FALTA ARCHIVO PROTEGIDO
				}
			}
			*/
			
			// Se obtiene la descripci—n del Banco
			if(listCheques.get(renglon).getDescBanco().length() >= 34)
				sBancoTrunc = listCheques.get(renglon).getDescBanco().substring(0, 34); //renglon, 6
			else
				sBancoTrunc = listCheques.get(renglon).getDescBanco(); //renglon, 6
			//Se sustituyen los espacios en blanco por &#255;
			//sBancoTrunc = sBancoTrunc.replaceAll(".", "&#255;");
			sBancoTrunc = sBancoTrunc.replaceAll(" ", "&#255;");
			sBancoTrunc = sBancoTrunc.replaceAll(",", "&#255;");
			sBancoTrunc = sBancoTrunc.replaceAll("„", "N");
			sBancoTrunc = sBancoTrunc.replaceAll("–", "n");
			
			// Se obtiene el importe.
			importe = listCheques.get(renglon).getImporte(); //renglon, 4
			importeLetra = funciones.convertirNumeroEnLetra(importe);
			importeLetra = importeLetra.replaceAll(" ", "&#255;");
			
			if(listCheques.get(renglon).getMoneda().equals("DLS")){
				importeLetra = importeLetra.replace("PESOS", "DOLARES");
				importeLetra = importeLetra.replace("M.N.", "USD");
			}
			//Se eliminan los puntos
			//importeLetra = importeLetra.replaceAll(".", "");
			importeLetra = importeLetra.replaceAll("/", "&#255;");
			
			//Se procesa el importe FALTA CREAR METODO PARA DARLE FORMATO
			sAux = importe+"";
			sAux = sAux.replaceAll(",", "&#255;");  
			//(sAux = sAux.replaceAll(".", "&#255;");
			
			// Se obtiene la fecha del cheque
			String sMesActual = funciones.ponerFormatoDate(listCheques.get(renglon).getFecCheque()); //renglon, 32
			String sMes = funciones.nombreMes(Integer.parseInt(sMesActual.substring(3, 5)));
			String sMesCorto = funciones.nombreMesCorto(Integer.parseInt(sMesActual.substring(3, 5)));
			
			if(Integer.parseInt(sMesActual.substring(6, 10)) > 1999){
				sFechaCheque = sMesActual.substring(0, 2) + "&#255;DE&#255;" + sMes.toUpperCase() + "&#255;DEL&#255;" + sMesActual.substring(6, 10);
				sFechaCorta = sMesActual.substring(0, 2) + "&#255;DE&#255;" + sMesCorto.toUpperCase() + "&#255;DEL&#255;" + sMesActual.substring(6, 10);
			}else{
				sFechaCheque = sMesActual.substring(0, 2) + "&#255;DE&#255;" + sMes.toUpperCase() + "&#255;DEL&#255;" + sMesActual.substring(6, 10);
				sFechaCorta = sMesActual.substring(0, 2) + "&#255;DE&#255;" + sMesCorto.toUpperCase() + "&#255;DEL&#255;" + sMesActual.substring(6, 10);
			}
			
			//Se arma el string del cheque
			StringBuilder sContCheque = new StringBuilder();
			sContCheque.append(ConstantesSet.FORMA_CHEQUE_WEB);
			
			sContCheque.append(">(s1p1h9v0s+0b4T ");
			
			//Numero de cheque
			sContCheque.append(">*p1900x95Y" + funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7) + " ");
			
			//Numero de Proveedor
			sContCheque.append(">*p45x275Y" + listCheques.get(renglon).getEquivaleBenef() + " ");
			
			//Nombre del Beneficiario
			String sNomBenef = (listCheques.get(renglon).getBeneficiario().trim()).replaceAll(" ", "&#255;");
			sNomBenef = sNomBenef.replaceAll("„", "N");  sNomBenef = sNomBenef.replaceAll("–", "n");
			sContCheque.append(">*p470x275Y" + sNomBenef + " ");
			
			//Importe
			//sContCheque.append(">*p1990x275Y" + sAux + " ");
			//sContCheque.append(">*p1990x275Y " + funciones.obtenerFormatoAsteriscos(listCheques.get(renglon).getImporte(), 12, "") + "\r\n");
			sContCheque.append(formatoImporte(1990,275,funciones.obtenerFormatoAsteriscos(listCheques.get(renglon).getImporte(), 12, ""), 'N'));
			
			//Pago concepto de
			String sConceptoPago = (listCheques.get(renglon).getConcepto().trim()).replaceAll(" ", "&#255;");
			sConceptoPago = sConceptoPago.replaceAll("„", "N");  sConceptoPago = sConceptoPago.replaceAll("–", "n");
			sContCheque.append(">*p45x575Y" + sConceptoPago + " ");
			//sContCheque.append(">*p  45x575Y " + listCheques.get(renglon).getConcepto().trim()+ "\r\n");
			
			//RFC
			sContCheque.append(">*p45x1170Y" +listCheques.get(renglon).getRfc() + " ");
			
			//Moneda de pago y moneda original
			sContCheque.append(">*p470x1170Y" + listCheques.get(renglon).getMoneda() + " >*p810x1170Y" + listCheques.get(renglon).getIdDivisaOriginal() + " ");
			
			//Tipo de cambio
			sAux = listCheques.get(renglon).getTipoCambio()+"";
			sContCheque.append(">*p1150x1170Y"+sAux.substring(0, sAux.indexOf("."))+". >*p1184x1170Y"+ sAux.substring(sAux.indexOf(".")+1, sAux.length()) +" ");
			
			//No. Sociedad
			sContCheque.append(">*p1570x1170Y" + listCheques.get(renglon).getNoEmpresa() + " ");
			
			//Importe de pago
			sAux = listCheques.get(renglon).getImporte()+"";
			sAux = sAux.replaceAll(",", "&#255;");  
			//sAux = sAux.replaceAll(".", "&#255;");
			//sContCheque.append(">*p1990x1170Y" + sAux + " ");
			//sContCheque.append(">*p1990x1170Y " + funciones.obtenerFormatoAsteriscos(listCheques.get(renglon).getImporte(), 12, "") + "\r\n");
			sContCheque.append(formatoImporte(1990,1170, funciones.obtenerFormatoAsteriscos(listCheques.get(renglon).getImporte(), 12, ""), 'N'));
			
			//Fecha corta
			sContCheque.append(">*p45x1390Y" + sFechaCorta + " ");
			
			//Banco
			sContCheque.append(">*p470x1390Y" + sBancoTrunc + " ");
			
			//No Cuenta
			sContCheque.append(">*p1150x1390Y" + sNoCta + " ");
			
			//Sociedad
			String sSoc = (listCheques.get(renglon).getCiaNmbr()).replaceAll(" ", "&#255;");
			sSoc = sSoc.replaceAll("„", "N");   sSoc = sSoc.replaceAll("–", "n");  
			//sSoc = sSoc.replaceAll(".", ""); 
			sContCheque.append(">*p1570x1390Y" + sSoc + " ");
			
			//Tipo de Egreso
			sContCheque.append(">*p45x1590Y" + listCheques.get(renglon).getIdRubroStr() + " ");
			
			//No Cheque
			sContCheque.append(">*p690x1590Y" + funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7) + " ");
			
			//Caja de Pago
			sContCheque.append(">*p1150x1590Y" + (listCheques.get(renglon).getDescCaja()).replaceAll(" ", "&#255;") + " ");
			
			//Contrarecibo
			sContCheque.append(">*p1570x1590Y" + listCheques.get(renglon).getNoContrarecibo() + " ");
			
			//Folio Tesoreria SET:
			sContCheque.append(">*p1990x1590Y" + listCheques.get(renglon).getNoSolicitud() + " ");
			
			//Datos de la compa–’a
			sContCheque.append(">*p1990x1590Y4 >(s1p1h10v0s+0b4T ");
			//Nombre compa–’a
			String sNomComp = (listCheques.get(renglon).getCiaNmbr()).replaceAll(" ", "&#255;");
			sNomComp = sNomComp.replaceAll("„", "N");   sNomComp = sNomComp.replaceAll("–", "n"); 
			//sNomComp = sNomComp.replaceAll(".", "");
			sContCheque.append(">*p510x2245Y" + sNomComp);
			
			//Direcci—n de la compa–’a
			String sDir1 = (listCheques.get(renglon).getCiaDir()).replaceAll(" ", "&#255;");
			sDir1.replaceAll("„", "N");   sDir1.replaceAll("–", "n");
			String sDir2 = (listCheques.get(renglon).getCiaDir2()).replaceAll(" ", "&#255;");
			sDir2.replaceAll("„", "N");   sDir2.replaceAll("–", "n");
			
			sContCheque.append(">(s1p1h6v0s+0b4T ");
			sContCheque.append(">*p510x2320Y" + sDir1 + " ");
			sContCheque.append(">*p510x2350Y" + sDir2 + "&#255;CP&#255;" + listCheques.get(renglon).getIdCp() + " ");
			sContCheque.append(">*p510x2380Y" + listCheques.get(renglon).getCiaRfc() + " ");
			
			//Se comenta lo de negociable y abono en cuenta.
			//La funcionalidad es que si:
			//	- leyenda=0,no lleva leyendas. leyenda=1, No negociable, leyenda=2, No negociable y abono en cuenta
			String leyenda = listCheques.get(renglon).getLeyendaProte().trim();
			if(leyenda.equals("1"))
				sContCheque.append(">(s1p1h14v0s+0b4T >*p1250x2550YNO&#255;NEGOCIABLE ");
			else if(leyenda.equals("2")){
				sContCheque.append(">(s1p1h14v0s+0b4T >*p1250x2550YNO&#255;NEGOCIABLE ");
				sContCheque.append(">(s1p1h9v0s+0b4T >*p200x2470YPARA&#255;ABONO&#255;EN&#255;CUENTA&#255;DEL&#255;BENEFICIARIO ");
			}
			/*if(negociable)
				sContCheque.append(">(s1p1h14v0s+0b4T >*p1250x2550YNO&#255;NEGOCIABLE ");
			if(abono)
				sContCheque.append(">(s1p1h9v0s+0b4T >*p200x2470YPARA&#255;ABONO&#255;EN&#255;CUENTA&#255;DEL&#255;BENEFICIARIO ");*/
			//Fin Leyenda
			
			//No Cheque
			sContCheque.append(">(s1p1h10v0s+0b4T >*p2180x2460Y" + funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7));
			
			//Fecha
			sContCheque.append(">*p1250x2460Y" + sFechaCheque + " ");
			
			//Beneficiario
			//(listCheques.get(renglon).getBeneficiario()).replaceAll(" ", "&#255;")
			sContCheque.append(">(s1p1h10v0s+0b4T >*p0x2610Y" + sNomBenef + " ");
			
			//Cantidad en letra
			//sContCheque.append(">(s1p1h6v0s+0b4T >*p70x2670Y" + importeLetra + " ");
			sContCheque.append(">(s1p1h6v0s+0b4T >*p70x2670Y" + importeLetra + " ");
			//sContCheque.append(">(s1p1h6v0s+0b4T >*p70x2670Y"+ importeLetra.substring(0, importeLetra.indexOf("/")) + "/");
			//+"/ >*p" + (1184 + (((importeLetra.length() - 7 ) * 17))) + "x1170Y"+ "100 MN");
			
			//sContCheque.append(">*p1150x1170Y"+sAux.substring(0, sAux.indexOf("."))+". >*p1184x1170Y"+ sAux.substring(sAux.indexOf(".")+1, sAux.length()) +" ");
			
			//Nombre del Banco
			sContCheque.append(">*p300x2900Y" + sBancoTrunc + " ");
			
			//No Cuenta
			sContCheque.append(">(s1p1h6v0s+0b4T ");
			sContCheque.append(">*p300x2980Y" + sNoCta + " ");
			
			//No Sucursal y plaza
			sContCheque.append(">*p620x2980Y" + listCheques.get(renglon).getSucursal() + "&#255;" + listCheques.get(renglon).getPlaza() + " ");
			
			//Se pone el importe con asteriscos
			sContCheque.append(">(s1p1h12v0s+0b4T ");
			sAux = funciones.obtenerFormatoAsteriscos(listCheques.get(renglon).getImporte(), 12, "*");
			sAux = sAux.replaceAll(",", "&#255;"); 
			//sAux = sAux.replaceAll(".", "&#255;");
			//sContCheque.append(">*p1945x2620Y" + sAux);
			sContCheque.append(formatoImporte(1945,2620, sAux , 'G'));
			
			//Firmas: 623 y 621
			//Se debe ir a buscar las firmas de los firmantes 1 y 2
			
			/* EMS - Se quitan las firmas para GSalinas.
			
			String firma1="", firma2="";
			//Firmante 1
			List<MantenimientosDto> lMant = mantenimientosDao.buscaFirmantes(Integer.parseInt(listCheques.get(renglon).getFirmante1()));
			if(lMant!=null && lMant.size()>0)
				firma1 = lMant.get(0).getPathFirma();
			
			//Firmante 2
			lMant = mantenimientosDao.buscaFirmantes(Integer.parseInt(listCheques.get(renglon).getFirmante2()));
			if(lMant!=null && lMant.size()>0)
				firma2 = lMant.get(0).getPathFirma();
			
			//Se agregan las firmas al cheque
			sContCheque.append(firma1 + " " + firma2 + " >(3@ >(12500X ");
			//sContCheque.append(">*p1130x2930Y>(623X! >*p1780x2930Y>(621X! >(3@ >(12500X ");
			*/
			
			// NUEVA BANDA MAGNETICA CECOBAN
			sLlaveIntercambio = funciones.ponerCeros(""+iBcoCve, 3) + funciones.ponerCeros(listCheques.get(renglon).getNoCuentaS().trim(), 11);
			iSumaHorizontal = 0;
			iConpeso = 1;
			iPeso = 0;
			
			for(iCalPeso=0; iCalPeso < sLlaveIntercambio.length(); iCalPeso++) {
				switch (iConpeso) {
					case 1: iPeso = 3; break;
					case 2: iPeso = 7; break;
					case 3: iPeso = 1; break;
				}
				iSumaHorizontal = iSumaHorizontal + Integer.parseInt(funciones.cadenaRight(""+(Integer.parseInt(sLlaveIntercambio.substring(iCalPeso, iCalPeso + 1)) * iPeso), 1));
				
				if(iConpeso == 3)
					iConpeso = 1;
				else
					iConpeso = iConpeso +1;
			}
			
			sDigitoIntercambio = funciones.cadenaRight(""+(10 - Integer.parseInt(funciones.cadenaRight(""+iSumaHorizontal, 1))), 1);
			
			if(listCheques.get(renglon).getMoneda().equals("DLS"))
				sTransito = "70" + funciones.ponerCeros(listCheques.get(renglon).getCvePlaza(), 3) + funciones.ponerCeros(""+listCheques.get(renglon).getBcoCve(), 3) + sDigitoIntercambio.trim();
			else
				sTransito = "51" + funciones.ponerCeros(listCheques.get(renglon).getCvePlaza(), 3) + funciones.ponerCeros(""+listCheques.get(renglon).getBcoCve(), 3) + sDigitoIntercambio.trim();
			
			// DIGITO VERIFICADOR DEL NUMERO DE CHEQUE PARA BANCRECER 161
			if(listCheques.get(renglon).getBcoCve() == 161){
				sDVCH = funciones.ponerCeros(""+(listCheques.get(renglon).getNoCheque() - ((listCheques.get(renglon).getNoCheque() / 7) * 7)), 1);
				sCerseg = funciones.ponerCeros(funciones.ponerCeros(listCheques.get(renglon).getCodigoSeguridad().substring(1, 3), 2) + sDVCH, 3);
			}else
				sCerseg = funciones.ponerCeros(listCheques.get(renglon).getCodigoSeguridad(), 3);
			
			iDigitoPremarcado = Integer.parseInt(sCerseg) + Integer.parseInt(sTransito) + Long.parseLong(listCheques.get(renglon).getNoCuentaS()) + listCheques.get(renglon).getNoCheque();
			iDigitoPremarcado = 9 - (iDigitoPremarcado - ((iDigitoPremarcado / 9) * 9));
			
			if(iBcoCve == 21){
				sContCheque.append("<p>&nbsp;</p>");
				sContCheque.append("<p>");
				sContCheque.append(">*p575x3100Y>(5X" + sCerseg + iDigitoPremarcado + "t");
				sContCheque.append("</p>");
				sContCheque.append("<p>");
				sContCheque.append(">*p765x3100Y>(5X" + sTransito + "t");
				sContCheque.append("</p>");
				sContCheque.append("<p>");
				sContCheque.append(">*p1145x3100Y>(5X" + funciones.ponerCeros(listCheques.get(renglon).getNoCuentaS(), 11) + "o");
				sContCheque.append("</p>");
				sContCheque.append("<p>");
				sContCheque.append(">*p1601x3100Y>(5X" + funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7) + "$ ");
				sContCheque.append("</p>");
				//sContCheque.append(">*p575x3143Y>(5X " + sCerseg + iDigitoPremarcado + "t" + sTransito + "t" + funciones.ponerCeros(listCheques.get(renglon).getNoCuentaS(), 11) + "o" + funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7) + "$ \r\n");
				
				/*if(tipoImpresora.equals("E330"))
					gEscribe.drawString(sCerseg + iDigitoPremarcado + "t" + sTransito + "t" + funciones.ponerCeros(listCheques.get(renglon).getNoCuentaS(), 11) + "o" + funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7) + "$", 587, 3123);
				else
					gEscribe.drawString(sCerseg + iDigitoPremarcado + "t" + sTransito + "t" + funciones.ponerCeros(listCheques.get(renglon).getNoCuentaS(), 11) + "o" + funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7) + "$", 454, 3143);*/
			}else{
				sContCheque.append("<p>&nbsp;</p>");
				sContCheque.append("<p>");
				sContCheque.append(">*p575x3100Y>(5X" + sCerseg + iDigitoPremarcado + "t");
				sContCheque.append("</p>");
				sContCheque.append("<p>");
				sContCheque.append(">*p765x3100Y>(5X" + sTransito + "t");
				sContCheque.append("</p>");
				sContCheque.append("<p>");
				sContCheque.append(">*p1145x3100Y>(5X" + funciones.ponerCeros(listCheques.get(renglon).getNoCuentaS(), 11) + "o");
				sContCheque.append("</p>");
				sContCheque.append("<p>");
				sContCheque.append(">*p1601x3100Y>(5X" + funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7) + "$ ");
				sContCheque.append("</p>");
				//sContCheque.append(">*p575x3125Y>(5X " + sCerseg + iDigitoPremarcado + "t" + sTransito + "t" + funciones.ponerCeros(listCheques.get(renglon).getNoCuentaS(), 11) + "o" + funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7) + "$ \r\n");
				
				/*if(tipoImpresora.equals("E330"))
					gEscribe.drawString(sCerseg + iDigitoPremarcado + "t" + sTransito + "t" + funciones.ponerCeros(listCheques.get(renglon).getNoCuentaS(), 11) + "o" + funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7) + "$", 587, 3123);
				else
					gEscribe.drawString(sCerseg + iDigitoPremarcado + "t" + sTransito + "t" + funciones.ponerCeros(listCheques.get(renglon).getNoCuentaS(), 11) + "o" + funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7) + "$", 452, 3125);*/
			}
			//sContCheque.append(">*p10x3080Y>(5X 111111222223333444440003t519990142777777777777778888888899999990000000000000");
			
			//Fin del Cheque
			sContCheque.append(">E >%-12345X ");
			
			//Se crea el archivo fisico del cheque.
			FileOutputStream fileOutput = new FileOutputStream (ConstantesSet.RUTA_CHEQUES + listCheques.get(renglon).getNoCheque() + ".txt");
			BufferedOutputStream bufferedOutput = new BufferedOutputStream(fileOutput);
			bufferedOutput.write(sContCheque.toString().getBytes(), 0, sContCheque.length());
			bufferedOutput.close();
			Thread.sleep(1000);
			System.out.println("Cheque creado");
			
			//Se crea el archivo f’sico del proceso.
			//FileOutputStream fosProceso = new FileOutputStream(ConstantesSet.RUTA_CHEQUES + listCheques.get(renglon).getNoCheque() + ".bat");
			//BufferedOutputStream bosProceso = new BufferedOutputStream(fosProceso);
			
			//String sProcesoLexmark = "more " + ConstantesSet.RUTA_CHEQUES + listCheques.get(renglon).getNoCheque() + ".txt | lp -d Lexmark_E352dn -o raw";
			//String sProcesoLexmark = "type " + ConstantesSet.RUTA_CHEQUES + listCheques.get(renglon).getNoCheque() + ".txt > lpt1: ";
			//bosProceso.write(sProcesoLexmark.getBytes(), 0, sProcesoLexmark.length());
			//String sProceso = "type " + ConstantesSet.RUTA_CHEQUES + listCheques.get(renglon).getNoCheque() + ".txt > lpt1:";
			//bosProceso.write(sProceso.getBytes(), 0, sProceso.length());
			
			//bosProceso.close();
			//Thread.sleep(1000);
			
			chequeLaser = sContCheque.toString();
			logger.info("Cheque creado y Shell creado");
			System.out.println("Shell creado");
			
			//Se ejecuta el proceso creado.
			/*
			Runtime.getRuntime().exec("chmod 777 " + ConstantesSet.RUTA_CHEQUES + listCheques.get(renglon).getNoCheque() + ".sh");
			Thread.sleep(1000);
			Runtime.getRuntime().exec(ConstantesSet.RUTA_CHEQUES + listCheques.get(renglon).getNoCheque() + ".sh");
			Thread.sleep(4000);
			System.out.println("Cheque impreso");
			*/
			
			//chequeLaser = "Impresion de cheques finalizada correctamente.";
			iGFolioLaser = iGFolioLaser + 1;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			chequeLaser = "Impresion suspendida Error en impresora.";
			logger.error(e);
			return chequeLaser;
		}
		return chequeLaser;
	}
	/** ----------------FIN DE RUTINA DE IMPRESION DE CHEQUE DESDE NAVEGADOR --------------- **/

	
	/********* NUEVA RUTINA DE IMPRESIîN DE CHEQUE JRC **********/
	public String rutinaChequeLaserE352DN(int renglon, List<MovimientoDto> listCheques,
			boolean imprime, boolean negociable, boolean abono, String tipoImpresora)
	{
		String chequeLaser = "";
		
		int iGFolioLaser = 0;
		int iSumaHorizontal = 0;
		int iConpeso = 0;
		int iPeso = 0;
		int iCalPeso = 0;
		long iDigitoPremarcado = 0;
		double importe = 0;
		String sBancoTrunc = "";
		String importeLetra;
		String sFechaCheque = "";
		String sFechaCorta = "";
		String sLlaveIntercambio = "";
		String sDigitoIntercambio = "";
		String sTransito = "";
		String sDVCH = "";
		String sCerseg = "";
		
		// Se recupera la instancia del Singleton.
		globalSingleton = GlobalSingleton.getInstancia();
		
		try
		{
			//por el momento se deja asi la condicion ! para q no haga la busqueda
			if(globalSingleton.obtenerValorConfiguraSet(1001).equals(""))
			{
				PrintService[] impresoras =PrintServiceLookup.lookupPrintServices(null, null);
				PrintService impDefault =PrintServiceLookup.lookupDefaultPrintService();
				
				for(int i = 0; i < impresoras.length; i++) {
					if(impresoras[i].getName().toUpperCase().equals(globalSingleton.obtenerValorConfiguraSet(1001)))
					{
						logger.info("Establecer esta impresora como predeterminada :" + impresoras[i].getName());
						impDefault = impresoras[i];
						break;
					}
				}
				logger.info("default : " + impDefault);
				if(!impDefault.equals(globalSingleton.obtenerValorConfiguraSet(1001)))
				{
					chequeLaser = "No esta registrada la impresora para impresion de cheques";
					return chequeLaser;
				}
			}
			
			// Se obtiene la clave del banco y el nœmero de cuenta.
			int iBcoCve = listCheques.get(renglon).getIdBanco(); //renglon, 12
			String sNoCta = listCheques.get(renglon).getCtaNo(); //renglon, 19
			
			/* COMENTADO POR JRC 03/Dic/2014
			// Si es reimpresion no genera archivo.
			if(imprime)
			{
				switch (iBcoCve)
				{
					case 2: //'BANAMEX
						if(listCheques.get(renglon).getBProtegido().equals("S"))
							G_Reg_Banamex = G_Reg_Banamex + 1;
						
					case 161: //'BANCRECER
						if(listCheques.get(renglon).getBProtegido().equals("S"))
							G_Reg_Bancrecer = G_Reg_Bancrecer + 1;
						
					case 14: //'SANTANDER
						if(listCheques.get(renglon).getBProtegido().equals("S"))
							G_Reg_Santander = G_Reg_Santander + 1; //FALTA ARCHIVO PROTEGIDO
						
					case 12: //'BANCOMER
						if(listCheques.get(renglon).getBProtegido().equals("S"))
							G_Reg_Bancomer = G_Reg_Bancomer + 1; //FALTA ARCHIVO PROTEGIDO
				}
			}
			*/
			
			// Se obtiene la descripci—n del Banco
			if(listCheques.get(renglon).getDescBanco().length() >= 34)
				sBancoTrunc = listCheques.get(renglon).getDescBanco().substring(0, 34); //renglon, 6
			else
				sBancoTrunc = listCheques.get(renglon).getDescBanco(); //renglon, 6
			
			// Se obtiene el importe.
			importe = listCheques.get(renglon).getImporte(); //renglon, 4
			importeLetra = funciones.convertirNumeroEnLetra(importe);
			
			if(listCheques.get(renglon).getMoneda().equals("DLS"))
			{
				importeLetra = importeLetra.replace("PESOS", "DOLARES");
				importeLetra = importeLetra.replace("M.N.", "USD");
			}
			
			// Se obtiene la fecha del cheque
			String sMesActual = funciones.ponerFormatoDate(listCheques.get(renglon).getFecCheque()); //renglon, 32
			String sMes = funciones.nombreMes(Integer.parseInt(sMesActual.substring(3, 5)));
			String sMesCorto = funciones.nombreMesCorto(Integer.parseInt(sMesActual.substring(3, 5)));
			
			if(Integer.parseInt(sMesActual.substring(6, 10)) > 1999)
			{
				sFechaCheque = sMesActual.substring(0, 2) + " de " + sMes + " del " + sMesActual.substring(6, 10);
				sFechaCorta = sMesActual.substring(0, 2) + " de " + sMesCorto + " del " + sMesActual.substring(6, 10);
			}
			else
			{
				sFechaCheque = sMesActual.substring(0, 2) + " de " + sMes + " de " + sMesActual.substring(6, 10);
				sFechaCorta = sMesActual.substring(0, 2) + " de " + sMesCorto + " de " + sMesActual.substring(6, 10);
			}
			
			StringBuilder sContCheque = new StringBuilder();
			sContCheque.append(ConstantesSet.FORMA_POLIZA_CHEQUE);
			sContCheque.append(">(s1p1h9v0s+0b4T");
			sContCheque.append(">*p  45x220Y " + listCheques.get(renglon).getNoCliente());	// Num Proveedor;
			sContCheque.append(">*p 470x220Y " + listCheques.get(renglon).getBeneficiario().trim());
			sContCheque.append(">*p  45x360Y " + listCheques.get(renglon).getConcepto().trim());
			sContCheque.append(">*p  45x540Y " +listCheques.get(renglon).getRfc());
			sContCheque.append(">*p 470x540Y " + listCheques.get(renglon).getMoneda());
			sContCheque.append(">*p 810x540Y " + listCheques.get(renglon).getIdDivisaOriginal());
			sContCheque.append(">*p1150x540Y " + listCheques.get(renglon).getTipoCambio());
			sContCheque.append(">*p1570x540Y " + listCheques.get(renglon).getNoEmpresa()); //NO SOCIEDAD
			sContCheque.append(">*p1990x540Y " + funciones.obtenerFormatoAsteriscos(listCheques.get(renglon).getImporte(), 12, ""));
			sContCheque.append(">*p  45x770Y " + sFechaCorta);
			sContCheque.append(">*p 470x770Y " + sBancoTrunc);
			sContCheque.append(">*p1150x770Y " + sNoCta);
			sContCheque.append(">*p1570x770Y SOCIEDAD DESCRIPCION S.A. DE C.V."); // + listCheques.get(renglon).getSociedad();
			sContCheque.append(">*p  45x970Y " + listCheques.get(renglon).getIdRubroStr()); //TIPO DE EGRESO (RUBRO)
			sContCheque.append(">*p 690x970Y " + funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7));
			sContCheque.append(">*p1150x970Y " + listCheques.get(renglon).getSucursal());
			sContCheque.append(">*p1570x970Y " + listCheques.get(renglon).getNoContrarecibo());
			sContCheque.append(">*p1990x970Y " + listCheques.get(renglon).getNoFolioDet());	// Folio Tesoreria SET
			sContCheque.append(">*p 270x1120Y " + listCheques.get(renglon).getNoCliente());	// Num Proveedor
			sContCheque.append(">*p 1250x1120Y " + listCheques.get(renglon).getBeneficiario());
			sContCheque.append(">*p  45x1250Y " + listCheques.get(renglon).getRfc());
			sContCheque.append(">*p 470x1250Y " + listCheques.get(renglon).getMoneda());
			sContCheque.append(">*p 810x1250Y " + listCheques.get(renglon).getIdDivisaOriginal());
			sContCheque.append(">*p1150x1250Y " + listCheques.get(renglon).getTipoCambio());
			sContCheque.append(">*p1570x1250Y " + listCheques.get(renglon).getNoEmpresa()); //NO SOCIEDAD;
			sContCheque.append(">*p1990x1250Y " + funciones.obtenerFormatoAsteriscos(listCheques.get(renglon).getImporte(), 12, ""));
			sContCheque.append(">*p  45x1480Y " + sFechaCorta);
			sContCheque.append(">*p 470x1480Y " + sBancoTrunc);
			sContCheque.append(">*p1150x1480Y " + sNoCta);
			sContCheque.append(">*p1570x1480Y SOCIEDAD DESCRIPCION S.A. DE C.V."); // + listCheques.get(renglon).getSociedad();
			sContCheque.append(">*p  45x1620Y " + listCheques.get(renglon).getIdRubroStr()); //TIPO DE EGRESO (RUBRO)
			sContCheque.append(">*p 690x1620Y " + funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7));
			sContCheque.append(">*p1150x1620Y " + listCheques.get(renglon).getSucursal());
			sContCheque.append(">*p1570x1620Y " + listCheques.get(renglon).getNoContrarecibo());
			sContCheque.append(">*p1990x1620Y " + listCheques.get(renglon).getNoFolioDet());	// Folio Tesoreria SET;
			if(negociable)
				sContCheque.append(">(s1p1h14v0s+0b4T>*p530x2440Y NO NEGOCIABLE ");
			if(abono)
				sContCheque.append(">(s1p1h9v0s+0b4T>*p530x2370Y PARA ABONO EN CUENTA DEL BENEFICIARIO ");
			sContCheque.append(">(s1p1h10v0s+0b4T>*p1780x2345Y " + sFechaCheque + " ");
			sContCheque.append(">(s1p1h9v0s+0b4T>*p90x2545Y " + listCheques.get(renglon).getBeneficiario());
			sContCheque.append(">(s0p16.67h8.50v0s0b0T>*p70x2670Y  " + importeLetra);
			sContCheque.append(">*p970x2670Y >*p1870x2670Y >(s1p1h9v0s+0b4T");
			sContCheque.append(">*p 460x2180Y " + listCheques.get(renglon).getBeneficiario());	//
			sContCheque.append(">(s1p1h6v0s+0b4T>*p 460x2210Y AV. PASEO DE LA REFORMA NO. 215   2O. PISO  LOMAS DE CHAPULTEPEC");
			sContCheque.append(">*p 460x2240Y MIGUEL HIDALGO CIUDAD DE MEXICO");
			sContCheque.append(">*p 460x2270Y RFC XIN9811266F5");
			sContCheque.append(">(s1p1h6v0s+0b4T");
			sContCheque.append(">*p 730x2850Y 65501917412");
			sContCheque.append(">*p 1010x2850Y 5814  001");
			sContCheque.append(">(s1p1h12v0s+0b4T");
			sContCheque.append(">*p 1315x2540Y >(12500X " + funciones.obtenerFormatoAsteriscos(listCheques.get(renglon).getImporte(), 12, ""));
			sContCheque.append(">*p1450x2900Y>(621X!");
			sContCheque.append(">*p1880x2900Y>(622X!");
			sContCheque.append(">*p1420x2580Y>(12500X");
			sContCheque.append(">*p500x2950Y>(s1p08v0s0b25093T 0003t519990142");
			
			// NUEVA BANDA MAGNETICA CECOBAN
			sLlaveIntercambio = funciones.ponerCeros(""+iBcoCve, 3) + funciones.ponerCeros(listCheques.get(renglon).getNoCuentaS().trim(), 11);
			iSumaHorizontal = 0;
			iConpeso = 1;
			iPeso = 0;
			
			for(iCalPeso=0; iCalPeso < sLlaveIntercambio.length(); iCalPeso++) {
				switch (iConpeso) {
					case 1: iPeso = 3; break;
					case 2: iPeso = 7; break;
					case 3: iPeso = 1; break;
				}
				iSumaHorizontal = iSumaHorizontal + Integer.parseInt(funciones.cadenaRight(""+(Integer.parseInt(sLlaveIntercambio.substring(iCalPeso, iCalPeso + 1)) * iPeso), 1));
				
				if(iConpeso == 3)
					iConpeso = 1;
				else
					iConpeso = iConpeso +1;
			}
			
			sDigitoIntercambio = funciones.cadenaRight(""+(10 - Integer.parseInt(funciones.cadenaRight(""+iSumaHorizontal, 1))), 1);
			
			if(listCheques.get(renglon).getMoneda().equals("DLS"))
				sTransito = "70" + funciones.ponerCeros(listCheques.get(renglon).getCvePlaza(), 3) + funciones.ponerCeros(""+listCheques.get(renglon).getBcoCve(), 3) + sDigitoIntercambio.trim();
			else
				sTransito = "51" + funciones.ponerCeros(listCheques.get(renglon).getCvePlaza(), 3) + funciones.ponerCeros(""+listCheques.get(renglon).getBcoCve(), 3) + sDigitoIntercambio.trim();
			
			// DIGITO VERIFICADOR DEL NUMERO DE CHEQUE PARA BANCRECER 161
			if(listCheques.get(renglon).getBcoCve() == 161)
			{
				sDVCH = funciones.ponerCeros(""+(listCheques.get(renglon).getNoCheque() - ((listCheques.get(renglon).getNoCheque() / 7) * 7)), 1);
				sCerseg = funciones.ponerCeros(funciones.ponerCeros(listCheques.get(renglon).getCodigoSeguridad().substring(1, 3), 2) + sDVCH, 3);
			}
			else
				sCerseg = funciones.ponerCeros(listCheques.get(renglon).getCodigoSeguridad(), 3);
			
			iDigitoPremarcado = Integer.parseInt(sCerseg) + Integer.parseInt(sTransito) + Long.parseLong(listCheques.get(renglon).getNoCuentaS()) + listCheques.get(renglon).getNoCheque();
			iDigitoPremarcado = 9 - (iDigitoPremarcado - ((iDigitoPremarcado / 9) * 9));
			
			if(iBcoCve == 21)
			{
				sContCheque.append(">*p200x3080Y>(5X " + sCerseg + iDigitoPremarcado + "t" + sTransito + "t" + funciones.ponerCeros(listCheques.get(renglon).getNoCuentaS(), 11) + "o" + funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7) + "$");
				/*if(tipoImpresora.equals("E330"))
					gEscribe.drawString(sCerseg + iDigitoPremarcado + "t" + sTransito + "t" + funciones.ponerCeros(listCheques.get(renglon).getNoCuentaS(), 11) + "o" + funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7) + "$", 587, 3123);
				else
					gEscribe.drawString(sCerseg + iDigitoPremarcado + "t" + sTransito + "t" + funciones.ponerCeros(listCheques.get(renglon).getNoCuentaS(), 11) + "o" + funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7) + "$", 454, 3143);*/
			}
			else
			{
				sContCheque.append(">*p200x3080Y>(5X " + sCerseg + iDigitoPremarcado + "t" + sTransito + "t" + funciones.ponerCeros(listCheques.get(renglon).getNoCuentaS(), 11) + "o" + funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7) + "$");
				/*if(tipoImpresora.equals("E330"))
					gEscribe.drawString(sCerseg + iDigitoPremarcado + "t" + sTransito + "t" + funciones.ponerCeros(listCheques.get(renglon).getNoCuentaS(), 11) + "o" + funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7) + "$", 587, 3123);
				else
					gEscribe.drawString(sCerseg + iDigitoPremarcado + "t" + sTransito + "t" + funciones.ponerCeros(listCheques.get(renglon).getNoCuentaS(), 11) + "o" + funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7) + "$", 452, 3125);*/
			}
			//sContCheque.append(">*p10x3080Y>(5X 111111222223333444440003t519990142777777777777778888888899999990000000000000");
			sContCheque.append(">(3@");//^L
			
			//Se crea el archivo fisico del cheque.
			FileOutputStream fileOutput = new FileOutputStream (ConstantesSet.RUTA_CHEQUES + listCheques.get(renglon).getNoCheque() + ".txt");
			BufferedOutputStream bufferedOutput = new BufferedOutputStream(fileOutput);
			bufferedOutput.write(sContCheque.toString().getBytes(), 0, sContCheque.length());
			bufferedOutput.close();
			Thread.sleep(1000);
			System.out.println("Cheque creado");
			
			//Se crea el archivo f’sico del proceso.
			FileOutputStream fosProceso = new FileOutputStream(ConstantesSet.RUTA_CHEQUES + listCheques.get(renglon).getNoCheque() + ".bat");
			BufferedOutputStream bosProceso = new BufferedOutputStream(fosProceso);
			String sProceso = "type " + ConstantesSet.RUTA_CHEQUES + listCheques.get(renglon).getNoCheque() + ".txt > lpt1:";
			bosProceso.write(sProceso.getBytes(), 0, sProceso.length());
			bosProceso.close();
			Thread.sleep(1000);
			System.out.println("Shell creado");
			
			//Se ejecuta el proceso creado.
			//Runtime.getRuntime().exec("chmod 777 " + ConstantesSet.RUTA_CHEQUES + listCheques.get(renglon).getNoCheque() + ".sh");
			//Thread.sleep(1000);
			Runtime.getRuntime().exec(ConstantesSet.RUTA_CHEQUES + listCheques.get(renglon).getNoCheque() + ".bat");
			Thread.sleep(4000);
			System.out.println("Cheque impreso");
			
			
			/*Graphics gEscribe = null;
			PrintJob pj;	
			pj = Toolkit.getDefaultToolkit().getPrintJob(new Frame(), "SET", null);
			Font fuente;
			PrinterJob printJob = PrinterJob.getPrinterJob ();*/
			
			/*
			// PRIMERA PARTE DE LA HOJA
			gEscribe = pj.getGraphics();
			
			fuente = new Font("Arial", Font.PLAIN, 8);
			gEscribe.setFont(fuente);
			
			//gEscribe.setColor(Color.black);
			gEscribe.drawString(listCheques.get(renglon).getEquivaleBenef().trim(), 45, 220); 			//renglon, 36 equivale_benef
			gEscribe.drawString(listCheques.get(renglon).getBeneficiario().trim(), 470, 220); 			//renglon, 5 beneficiario
			gEscribe.drawString(listCheques.get(renglon).getConcepto().trim(), 45, 360); 				//renglon, 9 concepto
			gEscribe.drawString(listCheques.get(renglon).getRfc().trim(), 45, 540); 					//renglon, 16 rfc
			gEscribe.drawString(listCheques.get(renglon).getMoneda().trim(), 470, 540); 				//renglon, 17 moneda
			gEscribe.drawString(listCheques.get(renglon).getIdDivisaOriginal().trim(), 810, 540); 		//renglon, 40 divisa_original
			gEscribe.drawString(""+listCheques.get(renglon).getTipoCambio(), 1150, 540); 										//renglon, 41 tipo_cambio
			gEscribe.drawString(""+listCheques.get(renglon).getEquivaleEmpresa(), 1570, 540); 									//renglon, 39 equivale_empresa
			gEscribe.drawString(funciones.obtenerFormatoAsteriscos(listCheques.get(renglon).getImporte(), 12, ""), 1990, 540); 											//renglon, 4 importe
			
			if(listCheques.get(renglon).getSucursal() != 0)
			{
				gEscribe.drawString("SUC.:", 1650, 590);
				gEscribe.drawString(""+listCheques.get(renglon).getSucursal(), 1770, 590); 		//renglon, 47 suc empresa
			}

			gEscribe.drawString(sFechaCorta.trim(), 45, 770);
			gEscribe.drawString(sBancoTrunc.trim(), 470, 770); //'desc_banco
			gEscribe.drawString(listCheques.get(renglon).getNoCuentaS().trim(), 1150, 770); 			//renglon, 24 no_cuenta
			gEscribe.drawString(listCheques.get(renglon).getCiaNmbr().trim(), 1570, 770); 				//renglon, 10 cia_nmbr
			gEscribe.drawString(""+listCheques.get(renglon).getIdRubroStr(), 45, 970); 					//renglon, 37 id_rubro
			gEscribe.drawString(funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7), 690, 970); //renglon, 33 no_cheque
			gEscribe.drawString(listCheques.get(renglon).getDescCaja().trim(), 1150, 970); 				//renglon, 8 desc_caja
			gEscribe.drawString(""+listCheques.get(renglon).getNoContrarecibo(), 1570, 970);			//renglon, 15 no_contrarecibo
			gEscribe.drawString(""+listCheques.get(renglon).getNoFolioDet(), 1990, 970);				//no_folio_det
		
			// SEGUNDA PARTE DE LA HOJA
			gEscribe.drawString(listCheques.get(renglon).getEquivaleBenef().trim(), 270, 1120);			//renglon, 36 equivale_benef
			gEscribe.drawString(listCheques.get(renglon).getBeneficiario().trim(), 850, 1120);			//renglon, 5 beneficiario
			gEscribe.drawString(listCheques.get(renglon).getRfc().trim(), 45, 1250); 					//renglon, 16 rfc
			gEscribe.drawString(listCheques.get(renglon).getMoneda().trim(), 470, 1250); 				//renglon, 17 moneda
			gEscribe.drawString(listCheques.get(renglon).getIdDivisaOriginal().trim(), 810, 1250); 		//renglon, 40 divisa_original
			gEscribe.drawString(""+listCheques.get(renglon).getTipoCambio(), 1150, 1250); 				//renglon, 41 tipo_cambio
			gEscribe.drawString(""+listCheques.get(renglon).getEquivaleEmpresa(), 1570, 1250); 			//renglon, 39 equivale_empresa
			gEscribe.drawString(funciones.obtenerFormatoAsteriscos(listCheques.get(renglon).getImporte(), 12, ""), 1990, 1250); 			//renglon, 4 importe
			
			if(listCheques.get(renglon).getSucursal() != 0)
			{
				gEscribe.drawString("SUC.:", 1650, 1290);
				gEscribe.drawString(""+listCheques.get(renglon).getSucursal(), 1770, 1290); 		//renglon, 47 suc empresa
			}
			
			gEscribe.drawString(sFechaCorta.trim(), 45, 1480);
			gEscribe.drawString(sBancoTrunc.trim(), 470, 1480); //'desc_banco
			gEscribe.drawString(listCheques.get(renglon).getNoCuentaS().trim(), 1150, 1480); 			//renglon, 24 no_cuenta
			gEscribe.drawString(listCheques.get(renglon).getCiaNmbr().trim(), 1570, 1480); 			//renglon, 10 cia_nmbr
			gEscribe.drawString(""+listCheques.get(renglon).getIdRubroStr(), 45, 1620); 		//renglon, 37 id_rubro
			gEscribe.drawString(funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7), 690, 1620); //renglon, 33 no_cheque
			gEscribe.drawString(listCheques.get(renglon).getDescCaja().trim(), 1150, 1620); 			//renglon, 8 desc_caja
			gEscribe.drawString(""+listCheques.get(renglon).getNoContrarecibo(), 1570, 1620);	//renglon, 15 no_contrarecibo
			gEscribe.drawString(""+listCheques.get(renglon).getNoFolioDet(), 1990, 1620);		//no_folio_det

			//TERCERA PARTE DE LA HOJA
			
			if(negociable)
			{
				// Printer.Print CarConf & "(s1p1h14v0s+0b4T"   'LETRA MAS GRANDE
				gEscribe.drawString(" NO NEGOCIABLE ", 530, 2440);
			}
			
			if(abono)
			{
				// Printer.Print CarConf & "(s1p1h9v0s+0b4T"
				gEscribe.drawString(" PARA ABONO EN CUENTA DEL BENEFICIARIO ", 530, 2370);
			}

			// NUM DE CHEQUE Y FECHA
			// Printer.Print CarConf & "(s1p1h10v0s+0b4T"
			gEscribe.drawString(sFechaCheque.trim(), 1780, 2345);
			// PAGUESE POR ESTE CHEQUE A: Y LA CANTIDAD DE:
			//Printer.Print CarConf & "(s1p1h9v0s+0b4T"
			gEscribe.drawString(listCheques.get(renglon).getBeneficiario().trim().toUpperCase().replace("Ñ", "N"), 90, 2545);
			
			// AHORA CON LETRA BITMAP
			// Printer.Print CarConf & "(s0p16.67h8.50v0s0b0T"
			//
			// Printer.Print CarConf & "*p70x2670Y " & Mid$(ImpLetra, 1, 50)
			// Printer.Print CarConf & "*p970x2670Y " & Mid$(ImpLetra, 51, 50)
			// Printer.Print CarConf & "*p1870x2670Y " & Mid$(ImpLetra, 101)
			//
			// DATOS DE LA COMPAÑIA
			// Printer.Print CarConf & "(s1p1h9v0s+0b4T"
			gEscribe.drawString(listCheques.get(renglon).getCiaNmbr().toUpperCase().replace("Ñ", "N"), 460, 2180);
			// Printer.Print CarConf & "(s1p1h6v0s+0b4T"
			gEscribe.drawString(listCheques.get(renglon).getCiaDir().toUpperCase().replace("Ñ", "N"), 460, 2210);
			gEscribe.drawString(listCheques.get(renglon).getCiaDir2().toUpperCase().replace("Ñ", "N"), 460, 2240);
			gEscribe.drawString("RFC "+listCheques.get(renglon).getCiaRfc(), 460, 2270);
			
			// Printer.Print CarConf & "(s1p1h6v0s+0b4T"
			gEscribe.drawString(listCheques.get(renglon).getNoCuentaS().trim(), 830, 2850);
			gEscribe.drawString(listCheques.get(renglon).getSucursal() + "  " + listCheques.get(renglon).getPlaza(), 1100, 2850);
			
			// AT ponemos el importe en numero
			// Printer.Print CarConf & "(s1p1h12v0s+0b4T"
			gEscribe.drawString(funciones.obtenerFormatoAsteriscos(listCheques.get(renglon).getImporte(), 15, "*"), 1915, 2540);

			// primero checa si es necesario ir por las firmas
			if(iGBcoFirma != listCheques.get(renglon).getBcoCve() || sGCtaFirma != listCheques.get(renglon).getCtaNo())
			{
				iGBcoFirma = listCheques.get(renglon).getBcoCve();
				sGCtaFirma = listCheques.get(renglon).getCtaNo();
				
				List<CatFirmasDto> catalogoFirmas = new ArrayList<CatFirmasDto>();
				catalogoFirmas = reimpresionDao.consultarCatalogoFirmas(iGBcoFirma, sGCtaFirma);
				
				if(catalogoFirmas.get(0).getPathFirma().substring(0, 2).equals("@@"))
				{
					if(catalogoFirmas.get(0).getTipoFirma().equals("A"))
					{
						catalogoFirmas.get(0).getNombre();
						iLPos = catalogoFirmas.get(0).getPathFirma().indexOf("*p");
						catalogoFirmas.get(0).getPathFirma().substring(0, iLPos + 1);
						catalogoFirmas.get(0).getPathFirma().substring(iLPos);// + 12);
						
						catalogoFirmas.get(1).getNombre();
						iLPos = catalogoFirmas.get(1).getPathFirma().indexOf("*p");
						catalogoFirmas.get(1).getPathFirma().substring(0, iLPos + 1);
						catalogoFirmas.get(1).getPathFirma().substring(iLPos);// + 12);
					}
					else
					{
						catalogoFirmas.get(0).getNombre();
						iLPos = catalogoFirmas.get(0).getPathFirma().indexOf("*p");
						catalogoFirmas.get(0).getPathFirma().substring(0, iLPos + 1);
						catalogoFirmas.get(0).getPathFirma().substring(iLPos);// + 12);
						
						catalogoFirmas.get(1).getNombre();
						iLPos = catalogoFirmas.get(1).getPathFirma().indexOf("*p");
						catalogoFirmas.get(1).getPathFirma().substring(0, iLPos + 1);
						catalogoFirmas.get(1).getPathFirma().substring(iLPos);// + 12);
					}
				}
				else
				{
					if(catalogoFirmas.get(0).getTipoFirma().equals("A"))
					{
						catalogoFirmas.get(0).getNombre();
						iLPos = catalogoFirmas.get(0).getPathFirma().indexOf("*p");
						catalogoFirmas.get(0).getPathFirma().substring(0, iLPos + 1);
						catalogoFirmas.get(0).getPathFirma().substring(iLPos);// + 12);
						catalogoFirmas.get(1).getNombre();
						iLPos = catalogoFirmas.get(1).getPathFirma().indexOf("*p");
						catalogoFirmas.get(1).getPathFirma().substring(0, iLPos + 1);
						catalogoFirmas.get(1).getPathFirma().substring(iLPos);// + 12);
					}
					else
					{
						catalogoFirmas.get(0).getNombre();
						iLPos = catalogoFirmas.get(0).getPathFirma().indexOf("*p");
						catalogoFirmas.get(0).getPathFirma().substring(0, iLPos + 1);
						catalogoFirmas.get(0).getPathFirma().substring(iLPos);// + 12);
						catalogoFirmas.get(1).getNombre();
						iLPos = catalogoFirmas.get(1).getPathFirma().indexOf("*p");
						catalogoFirmas.get(1).getPathFirma().substring(0, iLPos + 1);
						catalogoFirmas.get(1).getPathFirma().substring(iLPos);// + 12);
					}
				}
				catalogoFirmas.clear();
			}
			// Printer.Print G_Firma1
			// Printer.Print G_Firma2
			
			// NUEVA BANDA MAGNETICA CECOBAN
			sLlaveIntercambio = funciones.ponerCeros(""+iBcoCve, 3) + funciones.ponerCeros(listCheques.get(renglon).getNoCuentaS().trim(), 11);
			iSumaHorizontal = 0;
			iConpeso = 1;
			iPeso = 0;
			for(iCalPeso = 0; iCalPeso < sLlaveIntercambio.length(); iCalPeso++)
			{
				switch (iConpeso)
				{
					case 1: iPeso = 3; break;
					case 2: iPeso = 7; break;
					case 3: iPeso = 1; break;
				}
				iSumaHorizontal = iSumaHorizontal + Integer.parseInt(funciones.cadenaRight(""+(Integer.parseInt(sLlaveIntercambio.substring(iCalPeso, iCalPeso + 1)) * iPeso), 1));
				
				if(iConpeso == 3)
					iConpeso = 1;
				else
					iConpeso = iConpeso +1;
			}
			
			sDigitoIntercambio = funciones.cadenaRight(""+(10 - Integer.parseInt(funciones.cadenaRight(""+iSumaHorizontal, 1))), 1);
			
			if(listCheques.get(renglon).getMoneda().equals("DLS"))
				sTransito = "70" + funciones.ponerCeros(listCheques.get(renglon).getCvePlaza(), 3) + funciones.ponerCeros(""+listCheques.get(renglon).getBcoCve(), 3) + sDigitoIntercambio.trim();
			else
				sTransito = "51" + funciones.ponerCeros(listCheques.get(renglon).getCvePlaza(), 3) + funciones.ponerCeros(""+listCheques.get(renglon).getBcoCve(), 3) + sDigitoIntercambio.trim();
			
			// DIGITO VERIFICADOR DEL NUMERO DE CHEQUE PARA BANCRECER 161
			if(listCheques.get(renglon).getBcoCve() == 161)
			{
				sDVCH = funciones.ponerCeros(""+(listCheques.get(renglon).getNoCheque() - ((listCheques.get(renglon).getNoCheque() / 7) * 7)), 1);
				sCerseg = funciones.ponerCeros(funciones.ponerCeros(listCheques.get(renglon).getCodigoSeguridad().substring(1, 3), 2) + sDVCH, 3);
			}
			else
				sCerseg = funciones.ponerCeros(listCheques.get(renglon).getCodigoSeguridad(), 3);
			
			iDigitoPremarcado = Integer.parseInt(sCerseg) + Integer.parseInt(sTransito) + Long.parseLong(listCheques.get(renglon).getNoCuentaS()) + listCheques.get(renglon).getNoCheque();
			iDigitoPremarcado = 9 - (iDigitoPremarcado - ((iDigitoPremarcado / 9) * 9));
			
			if(iBcoCve == 21)
			{
				if(tipoImpresora.equals("E330"))
					gEscribe.drawString(sCerseg + iDigitoPremarcado + "t" + sTransito + "t" + funciones.ponerCeros(listCheques.get(renglon).getNoCuentaS(), 11) + "o" + funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7) + "$", 587, 3123);
				else
					gEscribe.drawString(sCerseg + iDigitoPremarcado + "t" + sTransito + "t" + funciones.ponerCeros(listCheques.get(renglon).getNoCuentaS(), 11) + "o" + funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7) + "$", 454, 3143);
			}
			else
			{
				if(tipoImpresora.equals("E330"))
					gEscribe.drawString(sCerseg + iDigitoPremarcado + "t" + sTransito + "t" + funciones.ponerCeros(listCheques.get(renglon).getNoCuentaS(), 11) + "o" + funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7) + "$", 587, 3123);
				else
					gEscribe.drawString(sCerseg + iDigitoPremarcado + "t" + sTransito + "t" + funciones.ponerCeros(listCheques.get(renglon).getNoCuentaS(), 11) + "o" + funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7) + "$", 452, 3125);
			}
		
			if(printJob.isCancelled())
			{
				chequeLaser = "La impresion fue cancelada";
				return chequeLaser;
			}
			//FIN DE LA IMPRESION
			gEscribe.dispose();
			pj.end();
			*/
			chequeLaser = "Impresion de cheques finalizada correctamente.";
			iGFolioLaser = iGFolioLaser + 1;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			chequeLaser = "Impresion suspendida Error en impresora.";
			logger.error(e);
			return chequeLaser;
		}
		return chequeLaser;
	}
	/********* FIN DE NUEVA RUTINA DE IMPRESIîN DE CHEQUE ********/
	
	public String rutinaChequeLaserDescE330NF(int renglon, List<MovimientoDto> listCheques,
			boolean imprime, boolean negociable, boolean abono, String tipoImpresora){
		//Map<String, Object> chequeLaser = new HashMap<String, Object>();
		String chequeLaser = "";
		
		int iBcoCve = 0;
		int iGFolioLaser = 0;
		
		int iGBcoFirma = 0;
		int iSumaHorizontal = 0;
		int iConpeso = 0;
		int iPeso = 0;
		int iCalPeso = 0;
		long iDigitoPremarcado = 0;
		int G_Reg_Banamex = 0;
		int G_Reg_Bancrecer = 0;
		int G_Reg_Santander = 0;
		int G_Reg_Bancomer = 0;
		double importe = 0;
		String sBancoTrunc = "";
		String importeLetra;
		String sMes = "";
		String sMesActual = "";
		String sMesCorto = "";
		String sFechaCheque = "";
		String sFechaCorta = "";
		
		
		String sGCtaFirma = "";
		String sLlaveIntercambio = "";
		String sDigitoIntercambio = "";
		String sTransito = "";
		String sDVCH = "";
		String sCerseg = "";
		StringBuffer sCarConf = new StringBuffer();
		Graphics gEscribe = null;
		PrintJob pj;	
		pj = Toolkit.getDefaultToolkit().getPrintJob(new Frame(), "SET", null);
		Font fuente;
		globalSingleton = GlobalSingleton.getInstancia();
		PrinterJob printJob = PrinterJob.getPrinterJob ();
		try
		{
			//por el momento se deja asi la condicion ! para q no haga la busqueda
			if(globalSingleton.obtenerValorConfiguraSet(1001).equals(""))
			{
				
				PrintService[] impresoras =PrintServiceLookup.lookupPrintServices(null, null);
				PrintService impDefault =PrintServiceLookup.lookupDefaultPrintService();
				for(int i = 0;i < impresoras.length; i++) {
					if(impresoras[i].getName().toUpperCase().equals(globalSingleton.obtenerValorConfiguraSet(1001)))
					{
						logger.info("Establecer esta impresora como predeterminada :" + impresoras[i].getName());
						impDefault = impresoras[i];
						break;
					}
				}
				logger.info("default : " + impDefault);
				if(!impDefault.equals(globalSingleton.obtenerValorConfiguraSet(1001)))
				{
					chequeLaser = "No esta registrada la impresora para impresion de cheques";
					return chequeLaser;
				}
			}
			
			iBcoCve = listCheques.get(renglon).getIdBanco(); //renglon, 12
			
			if(imprime){
			//'si es reimpresion no genera archivo
				switch (iBcoCve)
				{
					case 2: //'BANAMEX
						if(listCheques.get(renglon).getBProtegido().equals("S"))
							G_Reg_Banamex = G_Reg_Banamex + 1;
						
					case 161: //'BANCRECER
						if(listCheques.get(renglon).getBProtegido().equals("S"))
							G_Reg_Bancrecer = G_Reg_Bancrecer + 1;
						
					case 14: //'SANTANDER
						if(listCheques.get(renglon).getBProtegido().equals("S"))
							G_Reg_Santander = G_Reg_Santander + 1; //FALTA ARCHIVO PROTEGIDO
						
					case 12: //'BANCOMER
						if(listCheques.get(renglon).getBProtegido().equals("S"))
							G_Reg_Bancomer = G_Reg_Bancomer + 1; //FALTA ARCHIVO PROTEGIDO
				}
			}
			
			if(listCheques.get(renglon).getDescBanco().length() >= 34)
				sBancoTrunc = listCheques.get(renglon).getDescBanco().substring(0, 34); //renglon, 6
			else
				sBancoTrunc = listCheques.get(renglon).getDescBanco(); //renglon, 6
			
			importe = listCheques.get(renglon).getImporte(); //renglon, 4
			importeLetra = funciones.convertirNumeroEnLetra(importe);
			if(listCheques.get(renglon).getMoneda().equals("DLS"))
			{
				importeLetra = importeLetra.replace("PESOS", "DOLARES");
				importeLetra = importeLetra.replace("M.N.", "USD");
			}
			
			sMesActual = funciones.ponerFormatoDate(listCheques.get(renglon).getFecCheque()); //renglon, 32
			sMes = funciones.nombreMes(Integer.parseInt(sMesActual.substring(3, 5)));
			sMesCorto = funciones.nombreMesCorto(Integer.parseInt(sMesActual.substring(3, 5)));
			
			if(Integer.parseInt(sMesActual.substring(6, 10)) > 1999)
			{
				sFechaCheque = sMesActual.substring(0, 2) + " de " + sMes + " del " + sMesActual.substring(6, 10);
				sFechaCorta = sMesActual.substring(0, 2) + " de " + sMesCorto + " del " + sMesActual.substring(6, 10);
			}
			else
			{
				sFechaCheque = sMesActual.substring(0, 2) + " de " + sMes + " de " + sMesActual.substring(6, 10);
				sFechaCorta = sMesActual.substring(0, 2) + " de " + sMesCorto + " de " + sMesActual.substring(6, 10);
			}
			
			
			sCarConf.append(">&|1H&|1G");
//			Printer.Print CarConf & "E"
//			Printer.Print CarConf & "&l12d132p132f0l4E"
//			Printer.Print CarConf & "&f3700Y " & CarConf & "&f2X"
//			Printer.Print CarConf & "(s1p1h9v0s+0b4T"
//			
//			'PRIMERA PARTE DE LA HOJA
			gEscribe = pj.getGraphics();
			
			fuente = new Font("Arial", Font.PLAIN, 8);
			gEscribe.setFont(fuente);
			
//			gEscribe.setColor(Color.black);
			gEscribe.drawString(listCheques.get(renglon).getEquivaleBenef().trim(), 45, 220); 			//renglon, 36 equivale_benef
			gEscribe.drawString(listCheques.get(renglon).getBeneficiario().trim(), 470, 220); 			//renglon, 5 beneficiario
			gEscribe.drawString(listCheques.get(renglon).getConcepto().trim(), 45, 360); 				//renglon, 9 concepto
			gEscribe.drawString(listCheques.get(renglon).getRfc().trim(), 45, 540); 					//renglon, 16 rfc
			gEscribe.drawString(listCheques.get(renglon).getMoneda().trim(), 470, 540); 				//renglon, 17 moneda
			gEscribe.drawString(listCheques.get(renglon).getIdDivisaOriginal().trim(), 810, 540); 		//renglon, 40 divisa_original
			gEscribe.drawString(""+listCheques.get(renglon).getTipoCambio(), 1150, 540); 										//renglon, 41 tipo_cambio
			gEscribe.drawString(""+listCheques.get(renglon).getEquivaleEmpresa(), 1570, 540); 									//renglon, 39 equivale_empresa
			gEscribe.drawString(funciones.obtenerFormatoAsteriscos(listCheques.get(renglon).getImporte(), 12, ""), 1990, 540); 											//renglon, 4 importe
			
			//if(listCheques.get(renglon).getSucursal() != 0)
			//{
				gEscribe.drawString("SUC.:", 1650, 590);
				gEscribe.drawString(""+listCheques.get(renglon).getSucursal(), 1770, 590); 		//renglon, 47 suc empresa
			//}

			gEscribe.drawString(sFechaCorta.trim(), 45, 770);
			gEscribe.drawString(sBancoTrunc.trim(), 470, 770); //'desc_banco
			gEscribe.drawString(listCheques.get(renglon).getNoCuentaS().trim(), 1150, 770); 			//renglon, 24 no_cuenta
			gEscribe.drawString(listCheques.get(renglon).getCiaNmbr().trim(), 1570, 770); 				//renglon, 10 cia_nmbr
			gEscribe.drawString(""+listCheques.get(renglon).getIdRubroStr(), 45, 970); 					//renglon, 37 id_rubro
			gEscribe.drawString(funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7), 690, 970); //renglon, 33 no_cheque
			gEscribe.drawString(listCheques.get(renglon).getDescCaja().trim(), 1150, 970); 				//renglon, 8 desc_caja
			gEscribe.drawString(""+listCheques.get(renglon).getNoContrarecibo(), 1570, 970);			//renglon, 15 no_contrarecibo
			gEscribe.drawString(""+listCheques.get(renglon).getNoFolioDet(), 1990, 970);				//no_folio_det
		
//			'SEGUNDA PARTE DE LA HOJA
			gEscribe.drawString(listCheques.get(renglon).getEquivaleBenef().trim(), 270, 1120);			//renglon, 36 equivale_benef
			gEscribe.drawString(listCheques.get(renglon).getBeneficiario().trim(), 850, 1120);			//renglon, 5 beneficiario
			gEscribe.drawString(listCheques.get(renglon).getRfc().trim(), 45, 1250); 					//renglon, 16 rfc
			gEscribe.drawString(listCheques.get(renglon).getMoneda().trim(), 470, 1250); 				//renglon, 17 moneda
			gEscribe.drawString(listCheques.get(renglon).getIdDivisaOriginal().trim(), 810, 1250); 		//renglon, 40 divisa_original
			gEscribe.drawString(""+listCheques.get(renglon).getTipoCambio(), 1150, 1250); 				//renglon, 41 tipo_cambio
			gEscribe.drawString(""+listCheques.get(renglon).getEquivaleEmpresa(), 1570, 1250); 			//renglon, 39 equivale_empresa
			gEscribe.drawString(funciones.obtenerFormatoAsteriscos(listCheques.get(renglon).getImporte(), 12, ""), 1990, 1250); 			//renglon, 4 importe
			
			//if(listCheques.get(renglon).getSucursal() != 0)
			//{
				gEscribe.drawString("SUC.:", 1650, 1290);
				gEscribe.drawString(""+listCheques.get(renglon).getSucursal(), 1770, 1290); 		//renglon, 47 suc empresa
			//}
			
			gEscribe.drawString(sFechaCorta.trim(), 45, 1480);
			gEscribe.drawString(sBancoTrunc.trim(), 470, 1480); //'desc_banco
			gEscribe.drawString(listCheques.get(renglon).getNoCuentaS().trim(), 1150, 1480); 			//renglon, 24 no_cuenta
			gEscribe.drawString(listCheques.get(renglon).getCiaNmbr().trim(), 1570, 1480); 			//renglon, 10 cia_nmbr
			gEscribe.drawString(""+listCheques.get(renglon).getIdRubroStr(), 45, 1620); 		//renglon, 37 id_rubro
			gEscribe.drawString(funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7), 690, 1620); //renglon, 33 no_cheque
			gEscribe.drawString(listCheques.get(renglon).getDescCaja().trim(), 1150, 1620); 			//renglon, 8 desc_caja
			gEscribe.drawString(""+listCheques.get(renglon).getNoContrarecibo(), 1570, 1620);	//renglon, 15 no_contrarecibo
			gEscribe.drawString(""+listCheques.get(renglon).getNoFolioDet(), 1990, 1620);		//no_folio_det

//			'TERCERA PARTE DE LA HOJA
		
			if(negociable)
			{
//				Printer.Print CarConf & "(s1p1h14v0s+0b4T"   'LETRA MAS GRANDE
				gEscribe.drawString(" NO NEGOCIABLE ", 530, 2440);
			}
			
			if(abono)
			{
//				Printer.Print CarConf & "(s1p1h9v0s+0b4T"
				gEscribe.drawString(" PARA ABONO EN CUENTA DEL BENEFICIARIO ", 530, 2370);
			}

//			'NUM DE CHEQUE Y FECHA
//			Printer.Print CarConf & "(s1p1h10v0s+0b4T"
			gEscribe.drawString(sFechaCheque.trim(), 1780, 2345);
//			'PAGUESE POR ESTE CHEQUE A: Y LA CANTIDAD DE:
//			Printer.Print CarConf & "(s1p1h9v0s+0b4T"
			gEscribe.drawString(listCheques.get(renglon).getBeneficiario().trim().toUpperCase().replace("Ñ", "N"), 90, 2545);
		
//			'AHORA CON LETRA BITMAP
//			Printer.Print CarConf & "(s0p16.67h8.50v0s0b0T"
//			
//			Printer.Print CarConf & "*p70x2670Y " & Mid$(ImpLetra, 1, 50)
//			Printer.Print CarConf & "*p970x2670Y " & Mid$(ImpLetra, 51, 50)
//			Printer.Print CarConf & "*p1870x2670Y " & Mid$(ImpLetra, 101)
//			
//			'DATOS DE LA COMPAÑIA
//			Printer.Print CarConf & "(s1p1h9v0s+0b4T"
			gEscribe.drawString(listCheques.get(renglon).getCiaNmbr().toUpperCase().replace("Ñ", "N"), 460, 2180);
//			Printer.Print CarConf & "(s1p1h6v0s+0b4T"
			gEscribe.drawString(listCheques.get(renglon).getCiaDir().toUpperCase().replace("Ñ", "N"), 460, 2210);
			gEscribe.drawString(listCheques.get(renglon).getCiaDir2().toUpperCase().replace("Ñ", "N"), 460, 2240);
			gEscribe.drawString("RFC "+listCheques.get(renglon).getCiaRfc(), 460, 2270);
			
//			Printer.Print CarConf & "(s1p1h6v0s+0b4T"
			gEscribe.drawString(listCheques.get(renglon).getNoCuentaS().trim(), 830, 2850);
			gEscribe.drawString(listCheques.get(renglon).getSucursal() + "  " + listCheques.get(renglon).getPlaza(), 1100, 2850);
		
//			'AT ponemos el importe en numero
//			Printer.Print CarConf & "(s1p1h12v0s+0b4T"
			
			gEscribe.drawString(funciones.obtenerFormatoAsteriscos(listCheques.get(renglon).getImporte(), 15, "*"), 1915, 2540);

//			'primero checa si es necesario ir por las firmas
			if(iGBcoFirma != listCheques.get(renglon).getBcoCve() || sGCtaFirma != listCheques.get(renglon).getCtaNo())
			{
				iGBcoFirma = listCheques.get(renglon).getBcoCve();
				sGCtaFirma = listCheques.get(renglon).getCtaNo();
//				
				List<CatFirmasDto> catalogoFirmas = new ArrayList<CatFirmasDto>();
				catalogoFirmas = reimpresionDao.consultarCatalogoFirmas(iGBcoFirma, sGCtaFirma);
//				
				//if(catalogoFirmas.get(0).getPathFirma().substring(0, 2).equals("@@"))
				//{
					//if(catalogoFirmas.get(0).getTipoFirma().equals("A"))
					//{
						//sGNombre1 = catalogoFirmas.get(0).getNombre();
						//iLPos = catalogoFirmas.get(0).getPathFirma().indexOf("*p");
						//iLIni = catalogoFirmas.get(0).getPathFirma().substring(0, iLPos + 1);
						//iLFin = catalogoFirmas.get(0).getPathFirma().substring(iLPos);// + 12);
//						'AT SANTANDER
//						G_Firma1 = L_Ini & "1350x2940Y" & L_Fin
//						L_Firma.MoveNext
						//sGNombre2 = catalogoFirmas.get(1).getNombre();
						//iLPos = catalogoFirmas.get(1).getPathFirma().indexOf("*p");
						//iLIni = catalogoFirmas.get(1).getPathFirma().substring(0, iLPos + 1);
						//iLFin = catalogoFirmas.get(1).getPathFirma().substring(iLPos);// + 12);
//						'AT SANTANDER
//						G_Firma2 = L_Ini & "1980x2940Y" & L_Fin
					//}
					//else
					//{
						//sGNombre1 = catalogoFirmas.get(0).getNombre();
						//iLPos = catalogoFirmas.get(0).getPathFirma().indexOf("*p");
						//iLIni = catalogoFirmas.get(0).getPathFirma().substring(0, iLPos + 1);
						//iLFin = catalogoFirmas.get(0).getPathFirma().substring(iLPos);// + 12);
//						'AT SANTANDER
//						G_Firma1 = L_Ini & "1980x2940Y" & L_Fin
//						L_Firma.MoveNext
						//sGNombre2 = catalogoFirmas.get(1).getNombre();
						//iLPos = catalogoFirmas.get(1).getPathFirma().indexOf("*p");
						//iLIni = catalogoFirmas.get(1).getPathFirma().substring(0, iLPos + 1);
						//iLFin = catalogoFirmas.get(1).getPathFirma().substring(iLPos);// + 12);
//						'AT SANTANDER
//						G_Firma2 = L_Ini & "1350x2940Y" & L_Fin
					//}
				//}
				//else
				//{
					//if(catalogoFirmas.get(0).getTipoFirma().equals("A"))
					//{
						//sGNombre1 = catalogoFirmas.get(0).getNombre();
						//iLPos = catalogoFirmas.get(0).getPathFirma().indexOf("*p");
						//iLIni = catalogoFirmas.get(0).getPathFirma().substring(0, iLPos + 1);
						//iLFin = catalogoFirmas.get(0).getPathFirma().substring(iLPos);// + 12);
//						'AT SANTANDER
//						G_Firma1 = CarConf & "*p1350x2940Y" & CarConf & L_Fin
//						L_Firma.MoveNext
						//sGNombre2 = catalogoFirmas.get(1).getNombre();
						//iLPos = catalogoFirmas.get(1).getPathFirma().indexOf("*p");
						//iLIni = catalogoFirmas.get(1).getPathFirma().substring(0, iLPos + 1);
						//iLFin = catalogoFirmas.get(1).getPathFirma().substring(iLPos);// + 12);
//						'AT SANTANDER
//						G_Firma2 = CarConf & "*p1980x2940Y" & CarConf & L_Fin
					//}
					//else
					//{
						//sGNombre1 = catalogoFirmas.get(0).getNombre();
						//iLPos = catalogoFirmas.get(0).getPathFirma().indexOf("*p");
						//iLIni = catalogoFirmas.get(0).getPathFirma().substring(0, iLPos + 1);
						//iLFin = catalogoFirmas.get(0).getPathFirma().substring(iLPos);// + 12);
//						'AT SANTANDER
//						G_Firma1 = CarConf & "*p1980x2940Y" & CarConf & L_Fin
//						L_Firma.MoveNext
						//sGNombre2 = catalogoFirmas.get(1).getNombre();
						//iLPos = catalogoFirmas.get(1).getPathFirma().indexOf("*p");
						//iLIni = catalogoFirmas.get(1).getPathFirma().substring(0, iLPos + 1);
						//iLFin = catalogoFirmas.get(1).getPathFirma().substring(iLPos);// + 12);
//						'AT SANTANDER
//						G_Firma2 = CarConf & "*p1350x2940Y" & CarConf & L_Fin
					//}
				//}
				catalogoFirmas.clear();
			}
//			Printer.Print G_Firma1
//			Printer.Print G_Firma2
			
			//JRC 13/11/2014
			//G_Firma1 = L_Ini & "1350x2940Y" & L_Fin
			//gEscribe.drawString(iLIni + "1350x2940Y" + iLFin, 45, 100);
			
			
//			' NUEVA BANDA MAGNETICA CECOBAN                     'no_cuenta
			sLlaveIntercambio = funciones.ponerCeros(""+iBcoCve, 3) + funciones.ponerCeros(listCheques.get(renglon).getNoCuentaS().trim(), 11);
			iSumaHorizontal = 0;
			iConpeso = 1;
			iPeso = 0;
			for(iCalPeso = 0; iCalPeso < sLlaveIntercambio.length(); iCalPeso++)
			{
				switch (iConpeso)
				{
					case 1: iPeso = 3; break;
					case 2: iPeso = 7; break;
					case 3: iPeso = 1; break;
				}
				iSumaHorizontal = iSumaHorizontal + Integer.parseInt(funciones.cadenaRight(""+(Integer.parseInt(sLlaveIntercambio.substring(iCalPeso, iCalPeso + 1)) * iPeso), 1));
				
				if(iConpeso == 3)
					iConpeso = 1;
				else
					iConpeso = iConpeso +1;
			}
			
			//JRC 13/11/2014. Banca Magnetica y Firmas.
			gEscribe.drawString(">*p1880x2940Y>(621X!", 45, 50);
			gEscribe.drawString(">*p1420x2580Y>(12500X", 45, 100);
			gEscribe.drawString(">*p01x20Y>(5X 111111222223333444440003t519990142777777777777778888888899999990000000000000", 45, 150);
			
		
			sDigitoIntercambio = funciones.cadenaRight(""+(10 - Integer.parseInt(funciones.cadenaRight(""+iSumaHorizontal, 1))), 1);
			
			if(listCheques.get(renglon).getMoneda().equals("DLS"))
				sTransito = "70" + funciones.ponerCeros(listCheques.get(renglon).getCvePlaza(), 3) + funciones.ponerCeros(""+listCheques.get(renglon).getBcoCve(), 3) + sDigitoIntercambio.trim();
			else
				sTransito = "51" + funciones.ponerCeros(listCheques.get(renglon).getCvePlaza(), 3) + funciones.ponerCeros(""+listCheques.get(renglon).getBcoCve(), 3) + sDigitoIntercambio.trim();
			
//			'DIGITO VERIFICADOR DEL NUMERO DE CHEQUE PARA BANCRECER 161
			if(listCheques.get(renglon).getBcoCve() == 161)
			{
				sDVCH = funciones.ponerCeros(""+(listCheques.get(renglon).getNoCheque() - ((listCheques.get(renglon).getNoCheque() / 7) * 7)), 1);
				sCerseg = funciones.ponerCeros(funciones.ponerCeros(listCheques.get(renglon).getCodigoSeguridad().substring(1, 3), 2) + sDVCH, 3);
			}
			else
				sCerseg = funciones.ponerCeros(listCheques.get(renglon).getCodigoSeguridad(), 3);
			
			iDigitoPremarcado = Integer.parseInt(sCerseg) + Integer.parseInt(sTransito) + Long.parseLong(listCheques.get(renglon).getNoCuentaS()) + listCheques.get(renglon).getNoCheque();
			iDigitoPremarcado = 9 - (iDigitoPremarcado - ((iDigitoPremarcado / 9) * 9));
			
			if(iBcoCve == 21)
			{
				if(tipoImpresora.equals("E330"))
					gEscribe.drawString(sCerseg + iDigitoPremarcado + "t" + sTransito + "t" + funciones.ponerCeros(listCheques.get(renglon).getNoCuentaS(), 11) + "o" + funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7) + "$", 587, 3123);
				else
					gEscribe.drawString(sCerseg + iDigitoPremarcado + "t" + sTransito + "t" + funciones.ponerCeros(listCheques.get(renglon).getNoCuentaS(), 11) + "o" + funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7) + "$", 454, 3143);
			}
			else
			{
				if(tipoImpresora.equals("E330"))
					gEscribe.drawString(sCerseg + iDigitoPremarcado + "t" + sTransito + "t" + funciones.ponerCeros(listCheques.get(renglon).getNoCuentaS(), 11) + "o" + funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7) + "$", 587, 3123);
				else
					gEscribe.drawString(sCerseg + iDigitoPremarcado + "t" + sTransito + "t" + funciones.ponerCeros(listCheques.get(renglon).getNoCuentaS(), 11) + "o" + funciones.ponerCeros(""+listCheques.get(renglon).getNoCheque(), 7) + "$", 452, 3125);
			}
		
			if(printJob.isCancelled())
			{
				chequeLaser = "La impresion fue cancelada";
				return chequeLaser;
			}
			//FIN DE LA IMPRESION
			gEscribe.dispose();
			pj.end();
			chequeLaser = "Impresion de cheques finalizada correctamente.";
			iGFolioLaser = iGFolioLaser + 1;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			chequeLaser = "Impresión suspendida Error en impresora";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ReimpresionBusinessImpl, M:rutinaChequeLaserDescE330NF");
			return chequeLaser;
		}
		return chequeLaser;
	}
	
	/**
	 * Método queda inhabilitado para la impresión continua.
	 * @param renglon
	 * @param listCheques
	 * @return
	 */
	public String rutinaChequeContinuo(int renglon, List<MovimientoDto> listCheques){
		//Map<String, Object> chequeContinuo = new HashMap<String, Object>();
		String chequeContinuo = "";
		/*
	    Dim rstCampos As Recordset
	    Dim rstConf As Recordset
	    Dim i As Integer*/
		int iIdBanco = 0;
		int iNoEmpresa = 0;
		int iCurrentX = 0; //Cambiar a double
		int iCurrentY = 0;	//Cambiar a double
		String sImporte = "";
		String sIdDivisa = "";
		String sMesActual = "";
		String sImpLetra = "";
		String sMes = "";
		String sFechaCheque = "";
		Graphics gEscribeC = null;
		PrintJob pj;	
		pj = Toolkit.getDefaultToolkit().getPrintJob(new Frame(), "SET", null);
		try
		{
		String arrCampos[][] = new String[2][29];
	    	arrCampos[0][0] = "CAJA";
	        arrCampos[1][0] = "2";
	        
	        arrCampos[0][1] = "NUMERO DE SOLICITUD";
	        arrCampos[1][1] = "3";
	        
	        arrCampos[0][2] = "IMPORTE";
	        arrCampos[1][2] = "4";
	        
	        arrCampos[0][3] = "BENEFICIARIO";
	        arrCampos[1][3] = "5";
	        
	        arrCampos[0][4] = "BANCO";
	        arrCampos[1][4] = "6";

	        arrCampos[0][5] = "CHEQUERA";
	        arrCampos[1][5] = "7";
	        
	        arrCampos[0][6] = "CONCEPTO";
	        arrCampos[1][6] = "9";
	        
	        arrCampos[0][7] = "CENTRO DE COSTOS";
	        arrCampos[1][7] = "11";
	        
	        arrCampos[0][8] = "NUM. DE BANCO";
	        arrCampos[1][8] = "12";
	        
	        arrCampos[0][9] = "IMPORTE ORIGINAL";
	        arrCampos[1][9] = "14";
	        
	        arrCampos[0][10] = "IMPORTE EN LETRA";
	        arrCampos[1][10] = "13";
	        
	        arrCampos[0][11] = "NUM. CONTRARECIBO";
	        arrCampos[1][11] = "15";
	        
	        arrCampos[0][12] = "RFC BENEFICIARIO";
	        arrCampos[1][12] = "16";
	        
	        arrCampos[0][13] = "MONEDA";
	        arrCampos[1][13] = "17";
	        
	        arrCampos[0][14] = "LEYENDA PROTECCION";
	        arrCampos[1][14] = "20";
	        
	        arrCampos[0][15] = "PLAZA";
	        arrCampos[1][15] = "21";
	        
	        arrCampos[0][16] = "SUCURSAL";
	        arrCampos[1][16] = "22";
	        
	        arrCampos[0][17] = "DIRECCION EMPRESA";
	        arrCampos[1][17] = "29";
	        
	        arrCampos[0][18] = "2a DIRECCION EMPRESA";
	        arrCampos[1][18] = "28";
	        
	        arrCampos[0][19] = "RFC DE LA EMPRESA";
	        arrCampos[1][19] = "30";
	        
	        arrCampos[0][20] = "FECHA DEL CHEQUE";
	        arrCampos[1][20] = "32";
	        
	        arrCampos[0][21] = "NUMERO DE CHEQUE";
	        arrCampos[1][21] = "33";
	        
	        arrCampos[0][22] = "FIRMANTE 1";
	        arrCampos[1][22] = "34";
	        
	        arrCampos[0][23] = "FIRMANTE 2";
	        arrCampos[1][23] = "35";
	        
	        arrCampos[0][24] = "ID. RUBRO";
	        arrCampos[1][24] = "37";
	        
	        arrCampos[0][25] = "NUM. DE PEDIDO";
	        arrCampos[1][25] = "38";
	        
	        arrCampos[0][26] = "EMPRESA EQUIVALENTE";
	        arrCampos[1][26] = "39";
	        
	        arrCampos[0][27] = "DIVISA ORIGINAL";
	        arrCampos[1][27] = "40";
	        
	        arrCampos[0][28] = "TIPO DE CAMBIO";
	        arrCampos[1][28] = "41";
		
		
			iIdBanco  = listCheques.get(renglon).getBcoCve();
			sIdDivisa = listCheques.get(renglon).getMoneda();
			iNoEmpresa = listCheques.get(renglon).getNoEmpresa();
		    
			
		    
		    sImporte = funciones.ponerFormatoCeros(listCheques.get(renglon).getImporte(), 2);
		    sMesActual = funciones.ponerFechaSola(listCheques.get(renglon).getFecCheque());
		    
		    if(listCheques.get(renglon).getMoneda().equals("MN"))
		    {
		    	sImpLetra = funciones.convertirNumeroEnLetra(Double.parseDouble(sImporte));
		    	sMes = funciones.nombreMes(Integer.parseInt(sMesActual.substring(3, 5)));
		    	if(Integer.parseInt(sMesActual.substring(6, 10)) > 1999)
		    		sFechaCheque = sMesActual.substring(0, 2) + " de " + sMes + " del " + sMesActual.substring(6, 10);
		    	else
		    		sFechaCheque = sMesActual.substring(0, 2) + " de " + sMes + " de " + sMesActual.substring(6, 10);
		    }
		    
		    sImpLetra = "*** (" + sImpLetra + ") ***";
		    
//		    'selecciona todos los campos de conf_cheque
//		    'con sus coordenadas y caracteristicas para imprimirlo
		    List<ConfiguracionChequeDto> campos = new ArrayList<ConfiguracionChequeDto>();
		    //EMS: 29/01/2016 - Se agregó un campo adicional para buscar por configuración, esta funcion ya no tiene utilidad sin el id. 
		    campos = reimpresionDao.consultarConfiguracionCheques(iIdBanco, sIdDivisa, iNoEmpresa,"");
		    gEscribeC = pj.getGraphics();
		    Font fuente;
		    if(campos.size() > 0)
		    {
		    	for(int i = 0; i < campos.size(); i++)
		    	{
		    		fuente = new Font(campos.get(i).getTipoLetra(), Font.PLAIN, campos.get(i).getTamanoLetra());
					gEscribeC.setFont(fuente);
					//iCurrentX = campos.get(i).getCoordX()/20;
					//iCurrentY = campos.get(i).getCoordY()/15;
					
		    		for(int j = 0; j < arrCampos[0].length; j++)
		    		{
		    			if(arrCampos[0][j].equals(campos.get(i).getCampo()))
		    			{
		    				if(campos.get(i).getCampo().equals("IMPORTE EN LETRA"))
		    				{
		    					gEscribeC.drawString(sImpLetra, iCurrentX, iCurrentY);
	                            break;
		    				}
		    				else if(campos.get(i).getCampo().equals("FECHA DEL CHEQUE"))
		    				{
		    					gEscribeC.drawString(sFechaCheque, iCurrentX, iCurrentY);
	                            break;
		    				}
		    				else if(campos.get(i).getCampo().equals("BENEFICIARIO"))
		    				{
		    					gEscribeC.drawString(listCheques.get(renglon).getBeneficiario(), iCurrentX, iCurrentY);
	                            break;
		    				}
		    				else if(campos.get(i).getCampo().equals("CHEQUERA"))
		    				{
		    					gEscribeC.drawString(listCheques.get(renglon).getCtaNo(), iCurrentX, iCurrentY);
	                            break;
		    				}
		    				else if(campos.get(i).getCampo().equals("IMPORTE"))
		    				{
		    					gEscribeC.drawString(sImporte, iCurrentX, iCurrentY);
	                            break;
		    				}
		    				else if(campos.get(i).getCampo().equals("NUMERO DE CHEQUE"))
		    				{
		    					gEscribeC.drawString(""+listCheques.get(renglon).getNoCheque(), iCurrentX, iCurrentY);
	                            break;
		    				}
		    				else if(campos.get(i).getCampo().equals("NUMERO DE SOLICITUD"))
		    				{
		    					gEscribeC.drawString(""+listCheques.get(renglon).getNoSolicitud(), iCurrentX, iCurrentY);
	                            break;
		    				}
//		    				else
//		    				{
//		    					gEscribe.drawString(listCheques.get(renglon).getBeneficiario(), iCurrentX, iCurrentY);
////	                            Printer.Print grid.TextMatrix(renglon, CLng(arrCampos(1, i)))
//	                            break;
//		    				}
		    			}
		    		}
		    	}
                campos.clear();
		    }
		    campos.clear();
		    PrinterJob printJob = PrinterJob.getPrinterJob ();
		    if(printJob.isCancelled())
		    {
		    	chequeContinuo = "La impresion fue cancelada";
		    	return chequeContinuo;
		    }
		    
		    //FIN DE LA IMPRESION
		    gEscribeC.dispose();
			pj.end();
			chequeContinuo = "Cheques impresos";
		}
		catch(Exception e){
			e.printStackTrace();
			chequeContinuo = "Impresión suspendida Error en impresora";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ReimpresionBusinessImpl, M:rutinaChequeContinuo");
			return chequeContinuo;
		}
		return chequeContinuo;
	}
	
	//Revisado EMS 15/12/2015
	public List<LlenaComboEmpresasDto>llenarComboEmpresa(int usuario){
		return reimpresionDao.llenarComboEmpresa(usuario);
	}
	
	public List<LlenaComboImpresora>llenarComboImpresora(int caja){
		return reimpresionDao.llenarComboImpresora(caja);
	}

	public List<LlenaComboGralDto>llenarComboGral(LlenaComboGralDto dto){
		return reimpresionDao.llenarComboGral(dto);
	}
	
	public List<CatFirmasDto>consultarFirmasChequera(String cuenta, int banco){
		return reimpresionDao.consultarFirmasChequera(cuenta, banco);
	}
	
	public List<LlenaComboGralDto>consultarProveedores(String texto){
		return reimpresionDao.consultarProveedores(texto);
	}
	
	public List<LlenaComboImpresora>consultarBancoDeCharola(int impresora, int idBanco){
		return reimpresionDao.consultarBancoDeCharola(impresora, idBanco);
	}
	
	public List<ControlPapelDto>consultarFoliosPapel(int banco, int caja){
		List<ControlPapelDto> list = new ArrayList<ControlPapelDto>();
		list =  reimpresionDao.consultarFoliosPapel(banco, caja);
		return list;
	}
	
	public List<CatCtaBancoDto>consultarChequeras(int banco, int empresa){
		return reimpresionDao.consultarChequeras(banco, empresa);
	}
	
	public List<MovimientoDto>consultarDocumentosCheques(ConsultaChequesDto dtoCheques){
		return reimpresionDao.consultarDocumentosCheques(dtoCheques);
	}
	
	public List<LlenaComboEmpresasDto> consultarEmpresas(int usuario){
		return reimpresionDao.consultarEmpresas(usuario);
	}
	
	/**
	 * procedimiento para generar el layout de proteccion del cheque
	 * @param listCheques
	 * @return
	 * @since 27/06/2011
	 */
	public Map<String, Object>ejecutarProteccionCheque(List<MovimientoDto> listCheques, boolean chkH2H, boolean pbPendientes){
		Map<String,Object> result= new HashMap<String,Object>();
		globalSingleton = GlobalSingleton.getInstancia();
		Map<String, Object> mapRespRegMeto = new HashMap<String, Object>();
		mapRespRegMeto.put("msgUsuario","Error desconocido.");
		mapRespRegMeto.put("booRespuesta",false);
		int iNbanco = 0;
		int iUsuario = 0;
		int iNreg = 0;
		int piConsecutivo = 0;
		int iFolios2 = 0;
		boolean bBien = true;
		boolean inicio = false;
		double dImporte = 0;
		double totalImporteArchivo = 0;
		Date dFecha = globalSingleton.getFechaHoy();
		String sDirectorioDos = "";
		String sFolios = "";
		String sBenef = "";
		String sConcepto = "";
		String sNoDocto = "";
		String sChe2 = "";
		String sArchivo = "";
		String sArchivo2 = "";
		String sRegistro = "";
		String dirPrincipal = "";
		List<String> listDatosCadena = new ArrayList<>();
	    try{
	    	List<CatBancoDto> archProtegido = new ArrayList<CatBancoDto>(); //L_Proteg
	    	/*listDatosCadena = envioTransferenciasDao.armaCadenaConexion();
	    	if (listDatosCadena.size()!=0 ) {
	    		dirPrincipal = "smb://"+listDatosCadena.get(0)+":"+listDatosCadena.get(1)+
		    			"@"+listDatosCadena.get(2)+"/"+listDatosCadena.get(3);
	    		if (!dirPrincipal.equals("")) {
	    			archProtegido = reimpresionDao.consultarPropiedadesArchivo(listCheques.get(0).getIdBanco());
	    			sDirectorioDos = archProtegido.get(0).getPathProtegido();
					piConsecutivo = archProtegido.get(0).getNoProtegido() + 1;
			        reimpresionDao.actualizarCatBanco(listCheques.get(0).getIdBanco());
			        sArchivo = archProtegido.get(0).getArchProtegido() + funciones.ponerCeros(""+piConsecutivo, 6) + ".txt";
				} else {
					mapRespRegMeto.put("msgUsuario","La ruta no fue completada.");
		    		return mapRespRegMeto;
				}
			} else {
				mapRespRegMeto.put("msgUsuario","No se encontraron las variantes para generar la ruta");
	    		return mapRespRegMeto;
			}*/
	    	dirPrincipal = ConstantesSet.CARPETA_RAIZ_RESPALDO;
	    	if (!dirPrincipal.equals("")) {
    			archProtegido = reimpresionDao.consultarPropiedadesArchivo(listCheques.get(0).getIdBanco());
    			sDirectorioDos = archProtegido.get(0).getPathProtegido();
    			sDirectorioDos = sDirectorioDos.replace('/', File.separatorChar).replace('\\', File.separatorChar);
				piConsecutivo = archProtegido.get(0).getNoProtegido() + 1;
		        reimpresionDao.actualizarCatBanco(listCheques.get(0).getIdBanco());
		        sArchivo = archProtegido.get(0).getArchProtegido() + funciones.ponerCeros(""+piConsecutivo, 6) + ".txt";
			} else {
				mapRespRegMeto.put("msgUsuario","La ruta no fue completada.");
	    		return mapRespRegMeto;
			}
	    	
	    	iNreg = listCheques.size();
	        
	    	for(int i = 0; i < listCheques.size(); i++)
	    	{
	    		switch (listCheques.get(i).getIdBanco())
	    		{
	    		case ConstantesSet.SANTANDER:
    				//List<Map<String, Object>> listResultSantander= reimpresionDao.consultarDetalleChaqueAzteca(String.valueOf(listCheques.get(i).getNoFolioDet()));
    				sRegistro = layoutProteccionHSBCBusiness.armaSantanderChequesProtec(obtenerParamsLayout(listCheques, i));
    				if(sRegistro!=null ){
    					sFolios = sFolios + listCheques.get(i).getNoFolioDet() + ",";
    					result = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal,sDirectorioDos,sArchivo,sRegistro);
    					if(!(boolean)result.get("resExito")){
							result.put("msgUsuario",result.get("msgUsuario"));
							bBien = false;
							return result;
						}
					}
                break;
	    			case ConstantesSet.BITAL:
	    				
	    				sRegistro = layoutProteccionHSBCBusiness.armaArchivoHSBC(obtenerParamsLayout(listCheques, i));
	    				if(sRegistro!=null && !sRegistro.equals(""))
    					{
	    					sFolios = sFolios + listCheques.get(i).getNoFolioDet() + ",";
    					}
                    break;
	    			case ConstantesSet.BANCOMER:
	    				
	    					if (inicio == false)
	    					{
		    					//Arma encabezado de la proteccion de cheques
		    					sRegistro = layoutProteccionHSBCBusiness.armaBancomerH2H(obtenerParamsLayout(listCheques, i), 1, 
		    							listCheques.size(), totalImporteArchivo, sArchivo, pbPendientes);
		    							    					
		    					if(sRegistro!=null && !sRegistro.equals("")){
		    						result = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal,sDirectorioDos,sArchivo,sRegistro);
		    						if(!(boolean)result.get("resExito")){
		    							result.put("msgUsuario","Error al generar el archivo de proteccion de H2H Bancomer!");
		    							bBien = false;
		    							
		    							return result;
		    						}
		    						
		    						inicio = true;
		    					}
	    					}	    					
	    					if (inicio == true)
	    					{	    						
	    						//Arma detallado de la proteccion de cheques
		    					sRegistro = layoutProteccionHSBCBusiness.armaBanamexChequesProtec(obtenerParamsLayout(listCheques, i), 2, 
		    							listCheques.size(), totalImporteArchivo,sArchivo, pbPendientes);
		    							    					
		    					if(sRegistro!=null && !sRegistro.equals(""))
		    					{
			    					sFolios = sFolios + listCheques.get(i).getNoFolioDet() + ",";
			    					mapRespRegMeto = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal,sDirectorioDos,sArchivo,sRegistro);
			    					if(!(boolean)mapRespRegMeto.get("resExito"))
		    						{
		    							result.put("msgUsuario",mapRespRegMeto.get("msgUsuario"));
		    							bBien = false;
		    							return result;
		    						}			    					
		    					}
	    					}
	    				
	    			 break;
	    			case ConstantesSet.AZTECA:
	    				List<Map<String, Object>> listResult= reimpresionDao.consultarDetalleChaqueAzteca(""+listCheques.get(i).getNoFolioDet());
	    				sRegistro = layoutProteccionHSBCBusiness.armaAztecaChequesProtec(obtenerParamsLayout(listCheques, i), i, 
	    						listResult.get(0).get("rfc").toString(), listResult.get(0).get("noEmpresa").toString());
	    				if(sRegistro!=null && !sRegistro.equals("no_empresa"))
    					{
	    					sFolios = sFolios + listCheques.get(i).getNoFolioDet() + ",";
	    					result = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal,sDirectorioDos,sArchivo,sRegistro);
	    					if(!(boolean)result.get("resExito"))
    						{
    							result.put("msgUsuario",result.get("msgUsuario"));
    							bBien = false;
    							return result;
    						}
    					}
                    break;
	    			case ConstantesSet.BANAMEX:
	    				sRegistro = layoutProteccionHSBCBusiness.armaBanamexChequesProtec(obtenerParamsLayout(listCheques, i), 2, 
    							listCheques.size(), totalImporteArchivo,sArchivo, pbPendientes);
    						
	    				if(sRegistro!=null && !sRegistro.equals("")){
	    					sFolios = sFolios + listCheques.get(i).getNoFolioDet() + ",";
	    					result = creacionArchivosBusiness.escribirArchivoLayout(dirPrincipal,sDirectorioDos,sArchivo,sRegistro);
    						if(!(boolean)result.get("resExito"))
    						{
    							result.put("msgUsuario","Error al generar el archivo de proteccion de H2H Bancomer!");
    							bBien = false;
    							
    							return result;
    						}
    						inicio = true;
    					}
                    break;
	    		}
	            
	    		iFolios2 = listCheques.get(i).getNoFolioDet();
	    		dImporte = listCheques.get(i).getImporte();
	    		sBenef = listCheques.get(i).getBeneficiario();
	    		iNbanco = listCheques.get(i).getIdBanco();
	    		sConcepto = listCheques.get(i).getConcepto();
	    		sNoDocto = listCheques.get(i).getNoDocto();
	    		sChe2 = listCheques.get(i).getIdChequera();
	    		
	    		if (listCheques.get(i).getIdBanco() == 12 && chkH2H)
	    		{
	    			sArchivo2 = mapCreaArch.get("nomArchivo").toString().replace(".txt", "");
	    			sArchivo = sArchivo2;
	    		}
	    		else
	    		{
	    			sArchivo2 = sArchivo.substring(0, 8);
	    		}///sedescomenta
	    		
	    		List<DetArchTransferDto> tipoEnvio = new ArrayList<DetArchTransferDto>();
	    		tipoEnvio = reimpresionDao.consultarTipoEnvio(sNoDocto, dImporte, iFolios2);
	            
	    		if(tipoEnvio.size() != 0)
	    		{
	    			for(int j = 0; j < tipoEnvio.size(); j++)
	    			{
	    				if(tipoEnvio.get(j).getTipoEnvioLayout().equals("CHE"))
	    				{
	    					reimpresionDao.actualizarDetArchTransfer(tipoEnvio.get(j).getNoFolioDet()); //, lsArchivo2);
	    					reimpresionDao.insertarDetArchTransfer(sArchivo2, sBenef, dImporte, iFolios2, dFecha, sChe2, iNbanco, sConcepto, sNoDocto, 1);
	    				}
	                    else
	                    	reimpresionDao.insertarDetArchTransfer(sArchivo2, sBenef, dImporte, iFolios2, dFecha, sChe2, iNbanco, sConcepto, sNoDocto, 1);
	    			}
	    		}
	    		else
	    			reimpresionDao.insertarDetArchTransfer(sArchivo2, sBenef, dImporte, iFolios2, dFecha, sChe2, iNbanco, sConcepto, sNoDocto, 1);
	                
               tipoEnvio.clear();
               
	    		///sedescomenta
	    	}/**termina for**/
	    	
	    	if(bBien)
	    	{
	    		dFecha = globalSingleton.getFechaHoy();
	    		iUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
	    		
	    		reimpresionDao.insertarArchTransfer(sArchivo2, iNbanco, dFecha, dImporte, iUsuario, iNreg);
	    		
	    		if(!sFolios.equals(""))
	    		{
	    			sFolios = sFolios.substring(0, sFolios.length() - 1);
	    			reimpresionDao.actualizarMovimientoHistMovimiento(sArchivo, sFolios);
	    		}
	    		if (chkH2H)
	    		{
	    			result.put("msgUsuario", "Se genero el archivo " + dirPrincipal + sArchivo2 + ".");
	    		}
	    		else
	    		{
	    			System.out.println("\n dir"+ dirPrincipal);
	    			result.put("msgUsuario",  "Los registros han sido guardados en el archivo "  +sDirectorioDos +sArchivo);
	    			//listDatosCadena.get(2)+"/"+listDatosCadena.get(3)+"/"+sDirectorioDos +"/"+sArchivo);
	    		}
	    	}
	    	else
	    	{
	    		result.put("msgUsuario", "No se pudieron generar los registros");
	    	}
		}catch(Exception e)
		{
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ReimpresionBusinessImpl, M:ejecutarProteccionCheque");
		}
		return result;
	}
	
	public LayoutProteccionDto obtenerParamsLayout(List<MovimientoDto> listCheques, int i){
		LayoutProteccionDto dto = new LayoutProteccionDto();
		try{
			dto.setRegistro(i);
			dto.setIdBanco(listCheques.get(i).getIdBanco());
			dto.setIdChequera(listCheques.get(i).getIdChequera());
			dto.setNoCheque(listCheques.get(i).getNoCheque());
			dto.setBeneficiario(listCheques.get(i).getBeneficiario());
			dto.setImporte(listCheques.get(i).getImporte());
			dto.setFecImprime(listCheques.get(i).getFecImprime());
			dto.setConcepto(listCheques.get(i).getConcepto());
	
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ReimpresionBusinessImpl, M:obtenerParamsLayout");
		}
		return dto;
	}
	
	
	public List<MovimientoDto>consultarSolicitudesReimpresion(ConsultaChequesDto dtoCheques){
		List<MovimientoDto> list = new ArrayList<MovimientoDto>();
		list = reimpresionDao.consultarSolicitudesReimpresion(dtoCheques);
		return list;
	}
	
	/**
	 * 
	 * @param listCheques
	 * @param dtoBusCheques
	 * @return
	 */
	public Map<String, Object>ejecutarReimpresionCheques(
			List<MovimientoDto> listCheques, 
			ConsultaChequesDto dtoBusCheques) {
		Map<String,Object> retorno = new HashMap<String, Object>();
		List<String> lMensajesRet = new ArrayList<String>();
		globalSingleton = GlobalSingleton.getInstancia();
		String sMensajes = "";
		try{
		    boolean bOptImpresionContinua = 
		    	dtoBusCheques.getPsbImpreContinua().equals("S");
		    String gsTipoImpresora = "";
		    
			if (!bOptImpresionContinua) {
				int iNoImpresora = dtoBusCheques.getNumImpresora();
				int iCaja = dtoBusCheques.getIdCaja();
		        //solo si es impresion laser
	    		List<LlenaComboImpresora> listCharolas = 
	    			reimpresionDao.consultarCharolasImpresora(
	    					iNoImpresora, iCaja);
		        
	    		if (listCharolas.size() > 0) {
	    			gsTipoImpresora = 
	    				listCharolas.get(0).getTipoImpresora() == null ? 
	    						"" : listCharolas.get(0).getTipoImpresora();
	    		} else {
		        	retorno.put("msgUsuario", 
		        			"La impresora no tiene charolas registradas");
		        	return retorno;
		        }
		    
	    	} else {
		        //checa si la chequera puede imprimir cheque continuo
				int iBanco = listCheques.get(0).getIdBanco();
				String sChequera = listCheques.get(0).getCtaAlias();
		        List<CatCtaBancoDto> listContinuo = 
		        	reimpresionDao.consultarImpresionContinua(
		        			iBanco, sChequera);
		        
		        if(listContinuo.size() > 0) {
		        	String impreContinua = 
		        		listContinuo.get(0).getBImpreContinua() == null ? 
		        				"N" : listContinuo.get(0).getBImpreContinua();
		        	if (impreContinua.equals("N")) {
		        		retorno.put("msgUsuario", "La chequera seleccionada no esta habilitada para impresión continua");
		        		return retorno;
		        	}
		        }else {
		        	retorno.put("msgUsuario", "Error al buscar la chequera para validar");
		        	return retorno;
		        }
		        int iEmpresa = dtoBusCheques.getCriterioEmpresa();
		        String sMoneda = listCheques.get(0).getMoneda();
		        //checa si los bancos seleccionados tienen cheques configurados
		      
		        //EMS: 29/01/2016 - Se agregó un campo adicional para buscar por configuración, esta funcion ya no tiene utilidad sin el id.
		        List<ConfiguracionChequeDto> listConfigurados = 
		        	reimpresionDao.consultarConfiguracionCheques(iBanco, sMoneda, iEmpresa, "");
		        
		        if (listConfigurados.size() > 0)
		        	listConfigurados.clear();
		        else{
		        	retorno.put("msgUsuario", "Debe seleccionar una configuración para la impresión del cheque");
		        	listConfigurados.clear();
		        	return retorno;
		        }
	    	}//else if(optImpresionContinua)...
			
			//ACM 19/03/10 - Se agrega If para insert en bitacora_cheques
			 
	
		    for (int i = 0; i < listCheques.size(); i++) {
		    	List<MovimientoDto> entregaCheque = 
		    		reimpresionDao.consultarEntregaCheque(
		    				listCheques.get(i).getNoSolicitud()+"");
	    
	    		if (entregaCheque.size() > 0) {
	    			retorno.put("msgUsuario", 
	    					"El cheque con numero de solicitud " + 
	    					listCheques.get(i).getNoSolicitud()
	    					+ " no se puede imprimir porque ya fue entregado");
	    		} else {
	                //actualiza el grid con la nueva fecha
	            	if (dtoBusCheques.getFechaCheque() != null 
	            			&& !dtoBusCheques.getFechaCheque().equals("")) {
	            		listCheques.get(i).setFecCheque(
	            				dtoBusCheques.getFechaCheque());
	            	}
	                //actualiza movimiento
	            	sMensajes = 
	            		!bOptImpresionContinua ? 
	            				rutinaChequeLaserE352DN(
			            			i,listCheques, true, 
			            			dtoBusCheques.isChkNegociable(), 
			            			dtoBusCheques.isChkAbono(), gsTipoImpresora) 
			            		: 
	            				"Cheques Impresos";
	                
	            	if(i % 16 == 0)
	            		Thread.sleep(4000);
	            
	            }//else...
		    }//end for...
		    lMensajesRet.add(sMensajes);
			retorno.put("msgUsuario", lMensajesRet);
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ReimpresionBusinessImpl, M:ejecutarReimpresionCheques");
		}
		return retorno;
	}
	
	/**
	 * @autor Javier Construye el JRDataSource para la impresion del cheque.
	 * @param parameters
	 * @return
	 */
	
	public void obtenerReporteCheque(Map parameters){
		//JRDataSource jrDataSource = null;
		List<Map<String, Object>> resp = new ArrayList<Map<String, Object>>();
		Map<String, Object> results = new HashMap<String, Object>();
		double importe = Double.parseDouble(parameters.get("importe").toString());
		int noCheque = Integer.parseInt(parameters.get("noCheque").toString());
		//byte[] bAux=null;
		boolean bNegociable  = Boolean.parseBoolean((parameters.get("negociable").toString()));
		boolean bAbonoCuenta = Boolean.parseBoolean((parameters.get("abonoCuenta").toString()));
		//int respUpdate = 0;
		
		try
		{
			results.put("concepto", parameters.get("concepto").toString());
			results.put("importe", importe);
			results.put("noCheque", noCheque);
			results.put("lugar", parameters.get("lugar").toString());
			results.put("nomBeneficiario", parameters.get("nomBeneficiario").toString());
			String importeLetra = funciones.convertirNumeroEnLetra( Double.parseDouble(parameters.get("importe").toString()) );
			results.put("importeLetra", importeLetra);
			results.put("fecCheque", parameters.get("fecCheque"));
			
			
			String sCuenta = parameters.get("ctaAlias").toString();
			//String sNoDocto = parameters.get("noDocto").toString();
			int idGrupo = Integer.parseInt(parameters.get("idGrupo").toString());
			int idRubro = Integer.parseInt(parameters.get("idRubro").toString());
			int noEmpresa = Integer.parseInt(parameters.get("noEmpresa").toString());
			
			
			List<GuiaContableDto> lCtaContable = reimpresionDao.obtenerCtaContable(sCuenta);
			
			/*List<GuiaContableDto> lCtaContable = reimpresionDao.consultarCtaContableRubro(noEmpresa);
			if(lCtaContable.size() == 0)
			{
				throw new Exception("No se encontro cuenta contable asociada en GUIA_CONTABLE para el grupo: "+idGrupo + " y rubro:" +idRubro);
			}
			List<CatEquivaleCuentaContableDto> lEquivalencia = reimpresionDao.consultarEquivalenciaCtaContable(noEmpresa, sCuenta);
			if(lEquivalencia.size() == 0)
			{
				throw new Exception("No se encontro cuenta contable equivalente a la chequera: "+sCuenta + " y empresa:" +noEmpresa);
			}
			*/
			
			resp.add(results);
			GuiaContableDto guiaContable = lCtaContable.get(0);
			//CatEquivaleCuentaContableDto equivale = lEquivalencia.get(0);
			
			//******************JAVI EXCEL*********************
			POIFSFileSystem fs = new POIFSFileSystem(new URL("http://localhost:8080/SET/jsp/invoiceTemplate.xls").openStream());
			HSSFWorkbook wb = new  HSSFWorkbook(fs, true);
			//HSSFWorkbook wb = new  HSSFWorkbook();
			HSSFSheet hoja1 = wb.getSheet("invoice");
			
			
			HSSFDataFormat format = wb.createDataFormat();
			HSSFFont font = wb.createFont();
			font.setFontName(HSSFFont.FONT_ARIAL);
			font.setFontHeightInPoints((short) 10);
			
			HSSFCellStyle styleCurrencyCenter = wb.createCellStyle();
			styleCurrencyCenter.setDataFormat(format.getFormat("$#,##0.00"));
			//styleCurrencyCenter.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			styleCurrencyCenter.setFont(font);
			
			HSSFCellStyle styleCurrencyLeft = wb.createCellStyle();
			styleCurrencyLeft.setDataFormat(format.getFormat("$#,##0.00"));
			styleCurrencyLeft.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			styleCurrencyLeft.setFont(font);
			
			HSSFCellStyle styleNoChequeLeft = wb.createCellStyle();
			styleCurrencyLeft.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			styleCurrencyLeft.setFont(font);
			
			HSSFRow fila = hoja1.getRow(1);
	        HSSFCell celda = fila.getCell((short)0);
			celda.setCellValue(parameters.get("concepto").toString());
			
			fila = hoja1.getRow(6);
			celda = fila.getCell((short)0);
			celda.setCellValue(guiaContable.getCuentaContable());
			
			fila = hoja1.getRow(6);
			celda = fila.getCell((short)2);
			celda.setCellValue(guiaContable.getNoFolioDet()); //Concepto
			
			fila = hoja1.getRow(7);
			celda = fila.getCell((short)0);
			celda.setCellValue("Equivale - vacio");
			//celda.setCellValue(equivale.getLibroMayor()+"-"+equivale.getCuentaBancaria()+"-"+equivale.getDepto()+"-"+equivale.getIdCuenta());
			
			fila = hoja1.getRow(7);
			celda = fila.getCell((short)2);
			celda.setCellValue("CUENTA BANCARIA MN");
			
			fila = hoja1.getRow(6);
			celda = fila.getCell((short)7);
            celda.setCellValue(importe);
            celda.setCellStyle(styleCurrencyCenter);
			
            fila = hoja1.getRow(7);
			celda = fila.getCell((short)8);
			celda.setCellValue(importe);
			celda.setCellStyle(styleCurrencyCenter);
			
			fila = hoja1.getRow(33);
			celda = fila.getCell((short)0);
			celda.setCellValue(parameters.get("noCheque").toString());
			celda.setCellStyle(styleNoChequeLeft);
			
			fila = hoja1.getRow(32);
			celda = fila.getCell((short)7);
			celda.setCellValue("");
			celda.setCellStyle(styleCurrencyCenter);
			celda = fila.getCell((short)8);
			celda.setCellValue("");
			celda.setCellStyle(styleCurrencyCenter);
			
			fila = hoja1.getRow(27);
			celda = fila.getCell((short)7);
			celda.setCellValue(importe);
			celda.setCellStyle(styleCurrencyCenter);
			celda = fila.getCell((short)8);
			celda.setCellValue(importe);
			celda.setCellStyle(styleCurrencyCenter);
			
			fila = hoja1.getRow(42);
			celda = fila.getCell((short)8);
			celda.setCellValue("");
			
			fila = hoja1.getRow(40);
			celda = fila.getCell((short)8);
			celda.setCellValue(funciones.ponerFechaLetra(funciones.ponerFechaDate(parameters.get("fecCheque").toString()), false));
			//celda.setCellValue(funciones.ponerFechaSola(parameters.get("fecCheque").toString()));
			
			fila = hoja1.getRow(44);
			celda = fila.getCell((short)0);
			celda.setCellValue(parameters.get("nomBeneficiario").toString());
			celda = fila.getCell((short)8);
			celda.setCellValue(importe);
			celda.setCellStyle(styleCurrencyLeft);
			
			fila = hoja1.getRow(45);
			celda = fila.getCell((short)0);
			celda.setCellValue(importeLetra);
			
			if(bNegociable)
			{
				fila = hoja1.getRow(40);
				celda = fila.getCell((short)3);
				celda.setCellValue("No negociable");
			}
			if(bAbonoCuenta)
			{
				fila = hoja1.getRow(41);
				celda = fila.getCell((short)3);
				celda.setCellValue("Para abono en cuenta");
			}
			
			FileOutputStream fileOut = new FileOutputStream("C:\\set\\invoiceTemplate.xls");
			wb.getBytes();
			wb.write(fileOut);
			fileOut.close();
			//*****************JAVI EXCEL*********************
			
			reimpresionDao.actualizaCheque(noCheque, sCuenta);
			
			//jrDataSource = new JRMapArrayDataSource(resp.toArray());
		}
		catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:ImpresionCheques, C:ReimpresionBusinessImpl, M:obtenerReporteCheque");
			logger.error(Bitacora.getStackTrace(e));
		}
		//return jrDataSource;
	}
	
	public String escribirExcel(String datosGrid) {
		String respuesta;
		Gson gson = new Gson();
		List<Map<String, String>> parameters = 
			gson.fromJson(datosGrid, 
					new TypeToken<
					ArrayList<
					Map<String, String>>>() {}.getType());
		
		try {
			respuesta = 
				(Integer.parseInt(
						parameters.get(0).get("noEmpresa")) >= 414 && 
				Integer.parseInt(
						parameters.get(0).get("noEmpresa")) <= 418) ?
				escribirExcelCentralAbastos(datosGrid)
			:
				escribirExcelCoorporativo(datosGrid);
		}catch(Exception e) {
            return "Error al imprimir el cheque";	
		}
		return respuesta;
	}	
	
	
	@SuppressWarnings("deprecation")
	public String escribirExcelCoorporativo(String datosGrid) {
		Gson gson = new Gson();
		List<Map<String, String>> parameters = 
			gson.fromJson(datosGrid, 
					new TypeToken<
					ArrayList<
					Map<String, String>>>() {}.getType());
		
		double importe = 
			Double.parseDouble(
					parameters.get(0).get(
							"importe").toString());
		int noCheque = 
			Integer.parseInt(
					parameters.get(0).get(
							"noCheque").toString());
		int noChequeAnt = 
			Integer.parseInt(
					parameters.get(0).get(
							"noChequeAnt").toString());
		/* Se comentan ya que no se utilizan en GSalina, se quitaron desde el JS no existia ninguna referencia a estas variables.
		  boolean bNegociable  = 
			Boolean.parseBoolean(
					(parameters.get(0).get(
							"negociable").toString()));
		boolean bAbonoCuenta = 
			Boolean.parseBoolean(
					(parameters.get(0).get(
							"abonoCuenta").toString()));
		*/
		int respUpdate = 0;
		String importeLetra = 
			funciones.convertirNumeroEnLetra(importe);
		String respuesta = "";
		int pos1 = 0,
			pos2 = 6,
			pos3 = 7,
			pos4 = 30,
			pos5 = 37,
			pos6 = 38,
			pos7 = 39,
			pos8 = 41,
			pos9 = 43;
    	
		try {
			
			String idChequera = parameters.get(0).get("ctaAlias").toString();
			
			//cat_cta_contable_erp. - 3 registros con chequera, cargo abono = E, campo-> libro_mayor. 
			//BUSCAR POR id_chequera, Y CARGO ABONO = E 
			
			List<GuiaContableDto> lCtaContable = reimpresionDao.obtenerCtaContable(idChequera);
			
			if(lCtaContable.size() == 0)
				return "Error, No se encontro cuenta " +
						"contable asociada en CAT_CTAS_CONTABLES_ERP " +
						"con la chequera: " + idChequera;
			
			//Comentado EMS: No se utilizan en GSalinas.
			/*List<GuiaContableDto> lCtaContable =
				reimpresionDao.esAgrupado(noChequeAnt) ?
						reimpresionDao.
							consultarCtaContableRubroAgrupado(
								noChequeAnt)
						:
						reimpresionDao.consultarCtaContableRubro(
							Integer.parseInt(
								parameters.get(0).get(
									"noFolioDet").toString()))
						;
			 */
			
			/*if(lCtaContable.size() == 0)
				return "Error, No se encontro cuenta " +
						"contable asociada en CAT_CTAS_CONTABLES " +
						"para el grupo: " + 
						parameters.get(0).get("idGrupo").toString() + 
						" y rubro:" + parameters.get(0).get(
								"idRubro").toString();
			*/
			
			//Comentado EMS: No se utilizan equivalencias en Gsalinas.
			/*List<CatEquivaleCuentaContableDto> lEquivalencia = 
				reimpresionDao.consultarEquivalenciaCtaContable(
					Integer.parseInt(parameters.get(0).get(
							"noEmpresa").toString()),
					parameters.get(0).get("ctaAlias").toString());
			
			if(lEquivalencia.size() <= 0)
				return "Error, No se encontro cuenta " +
						"contable equivalente a la chequera: " + 
						parameters.get(0).get("ctaAlias").toString() + 
						" y empresa:" + 
						parameters.get(0).get("noEmpresa").toString();
			*/
			
			/*Tipos de celdas, para los formatos
			 * row1.createCell((short)0).setCellValue(1);
            row1.createCell((short)1).setCellValue(1.2);
            row1.createCell((short)2).setCellValue("ejemplo");
            row1.createCell((short)3).setCellValue(true);
            */
			
			//Se crea el libro Excel
			HSSFWorkbook wb = new HSSFWorkbook();
            //Se crea una nueva hoja dentro del libro
            HSSFSheet sheet = wb.createSheet("Cheque");
            
            HSSFDataFormat format = wb.createDataFormat();
            HSSFCellStyle cellStyle = wb.createCellStyle();
            cellStyle.setDataFormat(format.getFormat("$#,##0.00"));
            
            if(parameters.get(0).get("nuevoFormato").equals("true")) {
            	pos1 = 1;
        		pos2 = 7;
        		pos3 = 8;
        		pos4 = 31;
        		pos5 = 38;
        		pos6 = 39;
        		pos7 = 40;
        		pos8 = 42;
        		pos9 = 44;
            }
            
            HSSFRow row1 = sheet.createRow((short)pos1);
            row1.createCell((short)0).setCellValue(
            		parameters.get(0).get("concepto").toString());
            
            HSSFRow row27 = sheet.createRow((short)pos4);
            //row27.createCell((short)7).setCellValue(importe);
            //row27.createCell((short)8).setCellValue(importe);
            
            HSSFCell cell27 = row27.createCell((short)7);
            cell27.setCellValue(importe);
            cell27.setCellStyle(cellStyle);
            
            HSSFCell cell28 = row27.createCell((short)9);
            cell28.setCellValue(importe);
            cell28.setCellStyle(cellStyle);
            
            HSSFRow row41 = sheet.createRow((short)pos5);
            row41.createCell((short)8).setCellValue(
            		funciones.ponerFechaLetra(
            				funciones.ponerFechaDate(
            						parameters.get(0).get(
            								"fecCheque").toString()), false));
            
            //Comentado EMS: No se utiliza en GSalinas.
            /*if(bNegociable) {
            	HSSFRow row44 = sheet.createRow((short)pos6);
                row44.createCell((short)3).setCellValue(
                		"NO NEGOCIABLE");
			}
            if(bAbonoCuenta){
				HSSFRow row45 = sheet.createRow((short)pos7);
                row45.createCell((short)3).setCellValue(
                		"PARA ABONO EN CUENTA");
			}*/
            
            HSSFRow row46 = sheet.createRow((short)pos8);
            row46.createCell((short)0).setCellValue(
            		parameters.get(0).get(
            				"nomBeneficiario").toString());
            //row43.createCell((short)8).setCellValue(importe);
            
            HSSFCell cell46 = row46.createCell((short)9);
            cell46.setCellValue(importe);
            cell46.setCellStyle(cellStyle);
            
            HSSFRow row48 = sheet.createRow((short)pos9);
            row48.createCell((short)0).setCellValue(importeLetra);
            
            if (reimpresionDao.esAgrupado(noChequeAnt)) {
            	HSSFRow row5 = sheet.createRow((short)pos2);
	            /*row5.createCell((short)0).setCellValue(
	            		lEquivalencia.get(0).getLibroMayor()+
	            		"-"+lEquivalencia.get(0).getCuentaBancaria()+
	            		"-"+lEquivalencia.get(0).getDepto()+
	            		"-"+lEquivalencia.get(0).getIdCuenta());
	           */
            	row5.createCell((short)0).setCellValue("Equivalencia - Vacio 2");
	            row5.createCell((short)2).setCellValue("CUENTA BANCARIA MN");
	            
	            HSSFCell cell5 = row5.createCell((short)7);
	            cell5.setCellValue(importe);
	            cell5.setCellStyle(cellStyle);
	            
	            for (int i = 0, p = pos3; i < lCtaContable.size(); i++, p++) {
	            	HSSFRow row6 = sheet.createRow((short)p);
		            row6.createCell((short)0).setCellValue(
		            		lCtaContable.get(i).getCuentaContable().toString());
		            row6.createCell((short)2).setCellValue("No folio det de cuenta contable.");
		            //row6.createCell((short)2).setCellValue(lCtaContable.get(0).getNoFolioDet().toString().split("\n")[0]);
		            
		            //row7.createCell((short)8).setCellValue(importe);
		            
		            HSSFCell cell6 = row6.createCell((short)9);
		            //cell6.setCellValue(Double.parseDouble(lCtaContable.get(0).getNoFolioDet().toString().split("\n")[1]));
		            cell6.setCellStyle(cellStyle);
				}
	            
			} else {
				
	            
	            HSSFRow row5 = sheet.createRow((short)pos2);
	            row5.createCell((short)0).setCellValue(
	            		lCtaContable.get(0).getCuentaContable().toString());
	            /*row5.createCell((short)2).setCellValue(
	            		lCtaContable.get(0).getNoFolioDet().toString());
	            */
	            
	            HSSFCell cell5 = row5.createCell((short)7);
	            cell5.setCellValue(importe);
	            cell5.setCellStyle(cellStyle);
	            
	            HSSFRow row6 = sheet.createRow((short)pos3);
	            //row6.createCell((short)0).setCellValue(lEquivalencia.get(0).getLibroMayor()+"-"+lEquivalencia.get(0).getCuentaBancaria()+"-"+lEquivalencia.get(0).getDepto()+"-"+lEquivalencia.get(0).getIdCuenta());
	            row6.createCell((short)0).setCellValue("Equivalencia - vacio 3");
	            row6.createCell((short)2).setCellValue("CUENTA BANCARIA MN");
	            //row7.createCell((short)8).setCellValue(importe);
	            
	            HSSFCell cell6 = row6.createCell((short)9);
	            cell6.setCellValue(importe);
	            cell6.setCellStyle(cellStyle);
			}

            String ruta = creaRutaCheques(
            		Integer.parseInt(
            				parameters.get(0).get(
            						"noEmpresa").toString()));
            
            String nomArch = ruta + "cheque" + 
            	Integer.toString(noCheque) + ".xls";
//            String nomArch = "cheque" + Integer.toString(noCheque) + ".xls";
            System.out.print(nomArch);
            
            /*esto es una prueba para la central victor*/
            
/*            File archivoXLS = new File(nomArch);
            archivoXLS.createNewFile();
            FileOutputStream fileOut = new FileOutputStream(archivoXLS);
  */          
            /*aqui termina la prueba para lo de la central*/
            
            //Escribimos los resultados a un fichero Excel
            // Esto lo comento para fines de la prueba de la central
            FileOutputStream fileOut = new FileOutputStream(nomArch);
            wb.write(fileOut);
            fileOut.close();
            
            respUpdate = 
            	reimpresionDao.actualizaCheque(
            			noCheque, parameters.get(0).get(
            					"ctaAlias").toString());
            
            if (respUpdate != 0) respuesta = "Cheque Impreso";
        }catch(IOException e){
            System.out.println("Error al escribir el fichero.");
            return "Error al imprimir el cheque";
        }
        return respuesta;
    }
	
	
	public int obtieneUltimoCheqImp(String chequera) {
		return reimpresionDao.obtieneUltimoCheqImp(chequera);
	}
	
	public int getUltimoImpCtrlCheque(String noEmpresa, String noBanco, String noChequera) {
		
		int count = 0;  
		
		try{
			count = reimpresionDao.getUltimoImpCtrlCheque(noEmpresa, noBanco, noChequera);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ImpresionBusiness, M:getUltimoImpCtrlCheque");
		}
		
		return count;
	}
	
	public String creaRutaCheques(int noEmpresa) {
		String rutaCheque = "";
		
		try {
			
			List<LayoutProteccionDto> fechaHoy = 
				reimpresionDao.obtieneFechaHoy();
			
			String nomEmpresa = 
				reimpresionDao.obtieneNomCarpeta(noEmpresa);
			String mes = 
				funciones.nombreMes(
						funciones.obtenerMes(
								fechaHoy.get(0).getFecHoy()));
			String year = 
				Integer.toString(
						funciones.obtenerAnio(
								fechaHoy.get(0).getFecHoy()));
			
			rutaCheque = 
				reimpresionDao.consultarConfiguraSet(3007) + 
				nomEmpresa + "\\" + mes + year + "\\";
			
		    creaArchivo = new File(rutaCheque);
		    
		    if(!creaArchivo.isDirectory())
				creaArchivo.mkdirs();
		}catch(Exception e) {}
		return rutaCheque;
	}
    
	@SuppressWarnings("deprecation")
	public String escribirExcelCentralAbastos(String datosGrid) {
		Gson gson = new Gson();
		List<Map<String, String>> parameters = 
			gson.fromJson(datosGrid, 
					new TypeToken<
					ArrayList<
					Map<String, String>>>() {}.getType());
		
		double importe = 
			Double.parseDouble(
					parameters.get(0).get("importe").toString());
		int noCheque = 
			Integer.parseInt(
					parameters.get(0).get("noCheque").toString());
		/*boolean bNegociable  = 
			Boolean.parseBoolean(
					(parameters.get(0).get("negociable").toString()));
		boolean bAbonoCuenta = 
			Boolean.parseBoolean(
					(parameters.get(0).get("abonoCuenta").toString()));
		*/			
		int respUpdate = 0;
		String importeLetra = 
			funciones.convertirNumeroEnLetra(importe);
		String respuesta = "";
		int pos1 = 18,
			pos2 = 24,
			pos3 = 25,
			pos4 = 47,
			pos5 = 0,
			pos6 = 0,
			pos7 = 1,
			pos8 = 3,
			pos9 = 6;
    	
		try {
			
			String idChequera = parameters.get(0).get("ctaAlias").toString();
						
			List<GuiaContableDto> lCtaContable = reimpresionDao.obtenerCtaContable(idChequera);
			
			if(lCtaContable.size() == 0)
				return "Error, No se encontro cuenta " +
						"contable asociada en CAT_CTAS_CONTABLES_ERP " +
						"con la chequera: " + idChequera;
			
			/*List<GuiaContableDto> lCtaContable = 
				reimpresionDao.consultarCtaContableRubro(
						Integer.parseInt(
								parameters.get(0).get("noFolioDet").toString()));
			
			if (lCtaContable.size() <= 0)
				return "Error, No se encontro cuenta " +
						"contable asociada en CAT_CTAS_CONTABLES " +
						"para el grupo: " + 
						parameters.get(0).get("idGrupo").toString() + 
						" y rubro:" + parameters.get(0).get("idRubro").toString();
			*/
			
			/*List<CatEquivaleCuentaContableDto> lEquivalencia = 
				reimpresionDao.consultarEquivalenciaCtaContable(
					Integer.parseInt(parameters.get(0).get("noEmpresa").toString()),
					parameters.get(0).get("ctaAlias").toString());
			
			if(lEquivalencia.size() <= 0)
				return "Error, No se encontro cuenta contable equivalente a la chequera: "+ parameters.get(0).get("ctaAlias").toString() + " y empresa:" + parameters.get(0).get("noEmpresa").toString();
			*/
			
			//Se crea el libro Excel
			HSSFWorkbook wb = new HSSFWorkbook();
            //Se crea una nueva hoja dentro del libro
            HSSFSheet sheet = wb.createSheet("Cheque");
            
            HSSFDataFormat format = wb.createDataFormat();
            HSSFCellStyle cellStyle = wb.createCellStyle();
            cellStyle.setDataFormat(format.getFormat("$#,##0.00"));
            
            HSSFRow row1 = sheet.createRow((short)pos1);
            row1.createCell((short)0).setCellValue(
            		parameters.get(0).get("concepto").toString());
            
            HSSFRow row5 = sheet.createRow((short)pos2);
            row5.createCell((short)0).setCellValue(lCtaContable.get(0).getCuentaContable().toString());
            //row5.createCell((short)3).setCellValue(lCtaContable.get(0).getNoFolioDet().toString());
            
            HSSFCell cell5 = row5.createCell((short)7);
            cell5.setCellValue(importe);
            cell5.setCellStyle(cellStyle);
            
            HSSFRow row6 = sheet.createRow((short)pos3);
            row6.createCell((short)0).setCellValue("Equivalencia - vacio 4");
            /*row6.createCell((short)0).setCellValue(
            		lEquivalencia.get(0).getLibroMayor() + 
            		"-" + lEquivalencia.get(0).getCuentaBancaria() +
            		"-" + lEquivalencia.get(0).getDepto() +
            		"-" + lEquivalencia.get(0).getIdCuenta());
            */
            row6.createCell((short)3).setCellValue("CUENTA BANCARIA MN");
            //row7.createCell((short)8).setCellValue(importe);
            
            HSSFCell cell6 = row6.createCell((short)9);
            cell6.setCellValue(importe);
            cell6.setCellStyle(cellStyle);
            
            
            HSSFRow row27 = sheet.createRow((short)pos4);
            //row27.createCell((short)7).setCellValue(importe);
            //row27.createCell((short)8).setCellValue(importe);
            
            HSSFCell cell27 = row27.createCell((short)7);
            cell27.setCellValue(importe);
            cell27.setCellStyle(cellStyle);
            
            HSSFCell cell28 = row27.createCell((short)9);
            cell28.setCellValue(importe);
            cell28.setCellStyle(cellStyle);
            
            HSSFRow row41 = sheet.createRow((short)pos5);
            row41.createCell((short)9).setCellValue(
            		funciones.ponerFechaLetra(
            				funciones.ponerFechaDate(
            						parameters.get(0).get(
            								"fecCheque").toString()), false));
            
            /*if(bNegociable) {
            	HSSFRow row44 = sheet.createRow((short)pos6);
                row44.createCell((short)4).setCellValue(
                		"NO NEGOCIABLE");
			}
            if(bAbonoCuenta){
				HSSFRow row45 = sheet.createRow((short)pos7);
                row45.createCell((short)4).setCellValue(
                		"PARA ABONO EN CUENTA");
			}*/
            
            HSSFRow row46 = sheet.createRow((short)pos8);
            row46.createCell((short)0).setCellValue(
            		parameters.get(0).get(
            				"nomBeneficiario").toString());
            //row43.createCell((short)8).setCellValue(importe);
            
            HSSFCell cell46 = row46.createCell((short)9);
            cell46.setCellValue(importe);
            cell46.setCellStyle(cellStyle);
            
            HSSFRow row48 = sheet.createRow((short)pos9);
            row48.createCell((short)0).setCellValue(importeLetra);
            
            String ruta = creaRutaCheques(
            		Integer.parseInt(
            				parameters.get(0).get(
            						"noEmpresa").toString()));
            
            String nomArch = ruta + "cheque" + 
            	Integer.toString(noCheque) + ".xls";
                        
            //Escribimos los resultados a un fichero Excel
            FileOutputStream fileOut = new FileOutputStream(nomArch);
            wb.write(fileOut);
            fileOut.close();
            
            respUpdate = reimpresionDao.actualizaCheque(
            		noCheque, 
            		parameters.get(0).get("ctaAlias").toString());
            
            if (respUpdate != 0) respuesta = "Cheque Impreso";
            
        }catch(IOException e){
            return "Error al imprimir el cheque";
        }
        return respuesta;
    }
	
	public ReimpresionDao getReimpresionDao() {
		return reimpresionDao;
	}
	
	public void setReimpresionDao(ReimpresionDao reimpresionDao) {
		this.reimpresionDao = reimpresionDao;
	}
	
	public LayoutProteccionHSBCBusiness getLayoutProteccionHSBCBusiness() {
		return layoutProteccionHSBCBusiness;
	}
	
	public void setLayoutProteccionHSBCBusiness(
			LayoutProteccionHSBCBusiness layoutProteccionHSBCBusiness) {
		this.layoutProteccionHSBCBusiness = layoutProteccionHSBCBusiness;
	}
	
	public CreacionArchivosBusiness getCreacionArchivosBusiness() {
		return creacionArchivosBusiness;
	}
	
	public void setCreacionArchivosBusiness(
			CreacionArchivosBusiness creacionArchivosBusiness) {
		this.creacionArchivosBusiness = creacionArchivosBusiness;
	}
	
	//Agregado EMS 12/15/2015
	public List<LlenaComboGralDto>llenarComboBeneficiario(LlenaComboGralDto dto){
		return reimpresionDao.llenarComboBeneficiario(dto);	
	}

	@Override
	public List<LlenaComboGralDto> llenarComboLeyenda() {
		return reimpresionDao.llenarComboLeyenda();
	}
	
	public List<LlenaComboGralDto> llenarComboConfiguracion() { //YEC 28.01.16
		return reimpresionDao.llenarComboConfiguracion();
	}
	
	//Agregado 11/01/2016
	// runtina para generar la impresion del importe
	public String formatoImporte(int coordenadaX, int coordenadaY, String importe, char tamano)
	{
		String resultado;
		char caracter;
		resultado = ">*p" + coordenadaX + "x" + coordenadaY +"Y";
		for (int i = 0; i < importe.length(); i++) {
			caracter = importe.charAt(i);
			if (tamano == 'G') {
				coordenadaX = coordenadaX + 23;
			} else {
				coordenadaX = coordenadaX + 17;
			}
			if (caracter == '*' ){
				coordenadaX = coordenadaX - 5;
				resultado = resultado + importe.charAt(i); 
			}else if (caracter == ','){
				if (tamano == 'G') {
					coordenadaX = coordenadaX + 8;
				} else {
					coordenadaX = coordenadaX + 5;
				}
				resultado = resultado + importe.charAt(i);
				resultado = resultado + " >*p" + coordenadaX + "x" + coordenadaY +"Y";
			} else if (caracter == '.'){
				if (tamano == 'G') {
					coordenadaX = coordenadaX + 8;
				} else {
					coordenadaX = coordenadaX + 5;
				}
				resultado = resultado + importe.charAt(i);
				resultado = resultado + " >*p" + coordenadaX + "x" + coordenadaY +"Y";
			} else {
				resultado = resultado + importe.charAt(i);
			}
		}
		return resultado + " ";
	}

	@Override
	public List<LlenaComboGralDto> llenarComboBancos(int noEmpresa) {
		return reimpresionDao.llenarComboBancos(noEmpresa);
	}

	@Override
	public List<LlenaComboGralDto> llenarComboBancosCC(int idBanco) {
		return reimpresionDao.llenarComboBancosCC(idBanco);
	}
	
	@Override
	public List<LlenaComboGralDto> llenarComboChequera(int idBanco, int noEmpresa) {
		return reimpresionDao.llenarComboChequera(idBanco, noEmpresa);
	}
	
	private String parseaAsteriscosImporte(double inImporte, String formato){
		
		if(formato.equals("*")){
			String importe = funciones.ponerFormatoImporte(inImporte);
			String importeSinComas = ""+inImporte;
			
			importe = importe.trim();
			importeSinComas = importeSinComas.trim();
			
			if(importeSinComas.indexOf('.') > 0 ){
				String tmp = importeSinComas.substring(importeSinComas.indexOf('.')+1, importeSinComas.length());
				int lngCad = tmp.length();
				
				switch (lngCad) {
				case 1:
					importe += "**";
					break;
				case 2:
					importe += "*";
					break;
				case 0:
					importe += "***";
					break;	
				}
			}else{
				importe += importe + "***";
			}
			
			importe = "******************".substring(importe.length()) + importe;
			
			System.out.println("Importe Final: " + importe);
			
			return importe;
		}else{
			return "";
		}
	}
	
	@Override
	public List<LlenaComboGralDto>llenarComboMotivos(){
		return reimpresionDao.llenarComboMotivos();
	}
	
}
