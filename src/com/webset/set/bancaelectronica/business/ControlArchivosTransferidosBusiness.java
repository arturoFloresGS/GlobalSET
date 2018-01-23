package com.webset.set.bancaelectronica.business;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.internal.compiler.batch.Main.Logger;

import com.webset.set.bancaelectronica.dao.ControlArchivosTransferidosDao;
import com.webset.set.bancaelectronica.dao.impl.ControlArchivosTransferidosDaoImpl;
import com.webset.set.bancaelectronica.dto.ArchTransferDto;
import com.webset.set.bancaelectronica.service.ControlArchivosTransferidosService;
import com.webset.set.layout.business.CreacionArchivosBusiness;
import com.webset.set.layout.business.LayoutAztecaBusiness;
import com.webset.set.layout.business.LayoutBancomerBusiness;
import com.webset.set.layout.dto.ParametrosLayoutDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.BusquedaArchivos;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.RevividorDto;

public class ControlArchivosTransferidosBusiness implements ControlArchivosTransferidosService{
	Bitacora bitacora;
	ControlArchivosTransferidosDao controlArchivosTransferidosDao;
	ArchTransferDto objetoDto = new ArchTransferDto();
	Funciones funciones = new Funciones();
	CreacionArchivosBusiness creacionArchivosBusiness;
	LayoutBancomerBusiness layoutBancomerBusiness;
	BusquedaArchivos busquedaArchivos;
	private LayoutAztecaBusiness layoutAztecaBusiness;
	//LayoutAztecaBusiness layoutAztecaBusiness;
	File[] nombres;
	boolean beArmaNuevos = false;

	public String configuraSet(int indice){
		return controlArchivosTransferidosDao.configuraSet(indice);
	}

	public List<ArchTransferDto> llenaComboArchivos (String fecValor, String bChequeOcurre){
		return controlArchivosTransferidosDao.llenaComboArchivos(fecValor, bChequeOcurre);
	}

	public String validaCriterios(int optTodas, int optCriterios, String fecArchivo, String fecInicial, String fecFinal, String nomArchivo, String noDocto){
		String mensaje = "";
		try{
			switch (optCriterios){
			case 0: //Por fechas
			{
				if (fecInicial.equals(""))
					mensaje = "Falta seleccionar la Fecha Inicial";

				if (fecFinal.equals(""))
					mensaje = "Falta seleccionar la Fecha Final";

				/*if (fecInicial.compareTo(fecFinal) > 0)
					mensaje = "La fecha inicial no puede ser mayor que la fecha final";*/

				break;
			}
			case 1: //Por archivo
				if (nomArchivo.equals(""))
					mensaje = "Se debe de seleccionar un nombre de archivo"; 
				break;
			case 2: //Por No docto
				if(noDocto.equals(""))
					mensaje = "Falta escribir el número de documento";
				break;
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosBusiness, M: validaCriterios");
		} return mensaje;
	}


	public List<ArchTransferDto> llenaGridArchivos(String bChequeOcurre, int optCriterios, String fecArchivo, String fecInicial, 
												  String fecFinal, String nomArchivo, String noDocto){
		List<ArchTransferDto> lista= new ArrayList<ArchTransferDto>();
		try{

			objetoDto.setBChequeOcurre(bChequeOcurre + "");
			objetoDto.setTipoCriterio(optCriterios);
			objetoDto.setFecArchivo(fecArchivo + "");
			objetoDto.setFecInicial(fecInicial + "");
			objetoDto.setFecFinal(fecFinal + "");
			objetoDto.setNomArch(nomArchivo + "");
			objetoDto.setNoDocto(noDocto + "");
			lista = controlArchivosTransferidosDao.llenaGridArchivos(objetoDto);
			System.out.println("2");
			System.out.println("lista res.size"+lista.size());
		}catch(Exception e){
			System.out.println("Error business llenaGridArchivos");
		}return lista;
	}

	//Funcion para obtener la Referencia, en caso que exista en ctas_contrato 
	/*public String obtieneReferencia(String idChequera, int idBanco, String idChequeraBenef, int idBancoBenef, String referencia, int noEmpresa){
		String cadena = "";
		String recibeDato = "";
		try{
			recibeDato = controlArchivosTransferidosDao.tipoChequera(idChequera);

			if (!recibeDato.equals("")){
				if (recibeDato.equals("I")){
					recibeDato = controlArchivosTransferidosDao.seleccionaReferencia(noEmpresa, idChequera, idBanco);
					if(!recibeDato.equals(""))
						cadena = recibeDato;
					else
						cadena = referencia;
				}
				else{
					recibeDato = controlArchivosTransferidosDao.tipoChequera(idChequeraBenef);
					if (!recibeDato.equals("")){
						if (recibeDato.equals("I")){
							recibeDato = controlArchivosTransferidosDao.seleccionaReferencia(noEmpresa, idChequeraBenef, idBancoBenef);
							if(!recibeDato.equals(""))
								cadena = recibeDato;
							else
								cadena = referencia;
						}
					}
				}
			}

			if (cadena.equals(""))
				cadena = referencia;
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosBusiness, M: obtieneReferencia");
		}return cadena;
	}

	public String obtieneChequeraBenefOriginal(int idBancoBenef, String sucursal, String plaza, String idChequeraBenef, int idBanco){
		String chequeraOriginal = "";
		String psSucursal = "";
		String psPlaza = "";
		try{
			psSucursal = sucursal.trim();
			psPlaza = plaza.trim();

			if (psPlaza.length() > 3)
				psPlaza = psPlaza.substring(0, 3);

			System.out.println(psPlaza + " plaza");

			if (psSucursal.length() > 4)
				psPlaza = psPlaza.substring(0, 4);
			System.out.println(psPlaza + " plaza");

			if (idBancoBenef == 2) {
				if (idBanco == 2){
					chequeraOriginal = funciones.ponerCeros(idChequeraBenef.substring(5, idChequeraBenef.length()), 11);
					System.out.println(chequeraOriginal + " chequera banamex");
				}
				else
					chequeraOriginal = idChequeraBenef;
			}
			else if (idBancoBenef == 12){
				if (idChequeraBenef.length() == 11){
					if (Integer.parseInt(idChequeraBenef.substring(0, 3)) == 0){
						chequeraOriginal = funciones.ajustarLongitudCampo(psPlaza, 3, "D", "", "0") + idChequeraBenef.substring(0, 4);
					}
					else
						chequeraOriginal = idChequeraBenef;
				}
				else
					chequeraOriginal = idChequeraBenef;
			}
			else
				chequeraOriginal = idChequeraBenef;
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosBusiness, M: obtieneChequeraBenefOriginal");
		}return chequeraOriginal;
	} 
	*/
	public List<ArchTransferDto> llenaGridDetalle(String nomArchivo, int idBanco, boolean enviadasHoy, String bChequeOcurre){
		String recibeResultado = "";
		List<ArchTransferDto> listaResultado = new ArrayList<ArchTransferDto>();

		try{
			if (bChequeOcurre.equals("")){
				//Busca si es Traspaso o Transferencia
				recibeResultado = controlArchivosTransferidosDao.obtieneTipoDeMovto(nomArchivo);

				if (!recibeResultado.equals("")){
					System.out.println("entro detalle traspaso o transferencia");
					//Obtiene el detallado dependiendo el tipo de operacion
					listaResultado =  controlArchivosTransferidosDao.llenaGridDetalle(nomArchivo, idBanco, enviadasHoy, recibeResultado);
				}
			}
			else{
				//System.out.println("entro a llenaGriDetalleCheque");
				listaResultado = controlArchivosTransferidosDao.llenaGridDetalleCheque(nomArchivo);
			}
				

		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, " + "C: ControlArchivosTransferidosBusiness, M: llenaGridDetalle");
		}return listaResultado;
	}

	public Map<String,Object> regenerar(List<Map<String, String>> grid, List<Map<String, String>> registroCancelado, int registroTotalParam){
		String mensaje = "";
		String psIndicador = "";
		String psPrefijoMassPayment = "";
		String psArchivo = "";
		String rutaEnvio = "";
		String rutaEnvioMassPayment = "";
		String rutaEnvioNafi = "";
		String psEstatusMov = "";
		boolean mismoBanco = false;
		int tipoTransferencia = 0;
		boolean bTraspaso = false;
		boolean bInternacional = false;
		boolean bInterbancario = false;
		boolean bBitalSpeua = false;
		boolean bBanamexTef = false;
		boolean bBanamexMassPayment = false;
		boolean bChequeOcurreSantanderDirecto = false;
		boolean bBitalH2H = false;
		boolean bCitybankPaylinkMN = false;
		boolean bCitybankFlatFile = false;
		boolean convenioCie = false;
		int idBanco = 0;
		double importeCancelado = 0;
		boolean payLink = false;
		boolean h2hAfrdBanamex=false;
		Map<String, Object> mapCreaArch = new HashMap<String,Object>();
		Map<String,Object> result= new HashMap<String,Object>();
		
		Map<String, Object> mapRet = new HashMap<String, Object>();
		Map<String, Object> mapRegistro = new HashMap<String, Object>();
		List<RevividorDto> listRevividor = new ArrayList<RevividorDto>();
		String psFolios = "";
		int recibeRespuesta = 0;
		String psRegistro = "";
		BusquedaArchivos busquedaArchivos = new BusquedaArchivos();
		String rutaBanco="";
		double im=0;
		try {
			if(grid.size() != 0){
			System.out.println("entro a grid");
				psArchivo = grid.get(0).get("nomArchivo");
				//rutaEnvio = grid.get(0).get("directorio");
				if (controlArchivosTransferidosDao.obtieneTipoDeMovto(psArchivo.substring(0, psArchivo.length()-4)).equals("TRASPASOS")) 
					bTraspaso=true;


				if (rutaEnvio.equals(""))
					rutaEnvio = ConstantesSet.CARPETA_RAIZ_RESPALDO; //ruta dinamica 

				rutaEnvioMassPayment = controlArchivosTransferidosDao.configuraSet(196);
				rutaEnvioNafi = controlArchivosTransferidosDao.configuraSet(198);

				idBanco = Integer.parseInt(grid.get(0).get("idBanco"));

				if (grid.get(0).get("nomArchivo").substring(2, 3).equals("1")){
					mismoBanco = true;
					if (bTraspaso && idBanco == ConstantesSet.BANAMEX){
						if (grid.get(0).get("bBanamexInversion").equals("true")){
							tipoTransferencia = 2;  //lo maneja como transferencia ya que la cuenta no pertenece al grupo
			
						}else{
							tipoTransferencia = 3; //mismo banco para Traspaos SOLO PARA BANAMEX
							bTraspaso=true;//no debe ir este
							
						}
					}else{
						System.out.println("tipoTransf 2");
						tipoTransferencia = 2; //mismo banco
					}
					bInterbancario=false;//agregue
				}
				else if(grid.get(0).get("nomArchivo").substring(2, 3).equals("3")){
					tipoTransferencia = 3;
					mismoBanco = false;
					bInternacional = true;
					bTraspaso=true;
				
				}
				else {
					tipoTransferencia = 1; //interbancario
					mismoBanco = false;
					bInterbancario = true;
					                                  //entro aqui para cancelar
				}

				//Si se selecciono el radioButton de cheques
				if (grid.get(0).get("optCheque").equals("1")){
					psIndicador = grid.get(0).get("nomArchivo").substring(1, 3);
					//System.out.println("psIndicador "+psIndicador);
					if (psIndicador.equals("std")){ //Cheque ocurre Santander pago directo
						bChequeOcurreSantanderDirecto = true;
					
					}
				}else{
					psPrefijoMassPayment = controlArchivosTransferidosDao.configuraSet(218);//entro aqui para canclar optcheque es 0

					if (!psPrefijoMassPayment.equals("")){
						if (grid.get(0).get("nomArchivo").indexOf(psPrefijoMassPayment) > 0){
							bBanamexMassPayment = true;
							psArchivo = grid.get(0).get("nomArchivo").replace(".txt", ".sha");
					
						}
					}

					bBitalH2H = false;
					psIndicador = grid.get(0).get("nomArchivo").substring(0, 2);
				//el psindicador tiene tr
					if (psIndicador.equals("bs")){
						bBitalSpeua = true;
						tipoTransferencia = 3;
					}
					else if (psIndicador.toUpperCase().equals("BH"))
						bBitalH2H = true;
					else if (psIndicador.toUpperCase().equals("PA")){//ver que arroja con tr para cancelar va bn
						h2hAfrdBanamex=true;
					   bCitybankPaylinkMN = true;
	     			   if(bCitybankPaylinkMN){
					      payLink=true;
		     		    }
					}
					else if (psIndicador.toUpperCase().equals("BT"))
						bBanamexTef = true;
					else if (psIndicador.toUpperCase().equals("BM"))
						bBanamexMassPayment = true;
					else if (psIndicador.toUpperCase().equals("CP"))
						bCitybankPaylinkMN = true;
		     			if(bCitybankPaylinkMN){ //de mas
						payLink=true;
			     		}
					else if (psIndicador.toUpperCase().equals("CD") && grid.get(0).get("idBanco").equals("2")){
						bCitybankFlatFile = true;
						idBanco = ConstantesSet.CITIBANK_DLS;
					}
					else if (psIndicador.toUpperCase().equals("AZ"))
						idBanco = ConstantesSet.AZTECA;
					else if (psIndicador.toUpperCase().equals("BN"))
						idBanco = ConstantesSet.BANAMEX;
					else if (psIndicador.toUpperCase().equals("xx"))
						idBanco = ConstantesSet.BANORTE;

				}
				
				
				
				//Aqui se comienza con la creacion del archivo por banco

				/*
				switch (idBanco){
					case ConstantesSet.CITIBANK_DLS:
						if (bBanamexMassPayment){
							if (grid.get(0).get("fecTransferencia").equals(grid.get(0).get("fecHoy"))){
								rutaEnvio = rutaEnvioMassPayment + "\\" + psArchivo; 
							}
						}
						else if (bCitybankFlatFile && !grid.get(0).get("idServicioBE").equals("ACH"))
							rutaEnvio = rutaEnvio + "\\citibank_FlatFile\\" + psArchivo;
						else if (psIndicador.equals("WL"))
							rutaEnvio = rutaEnvio + "\\citibank_WL\\" + psArchivo;
						else
							rutaEnvio = rutaEnvio + "\\citibank_dls\\" + psArchivo;
						break;
					case ConstantesSet.INBURSA:
						rutaEnvio = rutaEnvio + "\\inbursa\\" + psArchivo;
						break;
					case ConstantesSet.INVERLAT:
						rutaEnvio = rutaEnvio + "\\inverlat\\" + psArchivo;
						break;
					case ConstantesSet.CHASE_MANHATTAN:
						rutaEnvio = rutaEnvio + "\\chase\\" + psArchivo;
						break;
					case ConstantesSet.SANTANDER:
						if (grid.get(0).get("optCheque").equals("1"))
							if (bChequeOcurreSantanderDirecto) {
								rutaEnvio = rutaEnvio + "\\santander_directo\\" + psArchivo;
							}
							else{
								rutaEnvio = rutaEnvio + "\\santander\\" + psArchivo;
							}
						else
							rutaEnvio = rutaEnvio + "\\santander\\" + psArchivo;*/
						/*
	            Case SANTANDER
	                If optcheque.Value = True Then    'Para cheque ocurre
	                    If pbChequeOcurreSantander_Directo = True Then
	                        ps_ruta_Archivo = ls_ruta_envio & "\santander_directo\" & ps_Archivo
	                        If Dir(ls_ruta_envio & "\santander_directo", vbDirectory) = "" Then
	                            MsgBox "No existe el directorio " & ls_ruta_envio & "\santander_directo", vbCritical, "SET"
	                            Screen.MousePointer = 0
	                            Exit Sub
	                        End If
	                    Else
	                        ps_ruta_Archivo = ls_ruta_envio & "\santander\" & ps_Archivo
	                        If Dir(ls_ruta_envio & "\santander", vbDirectory) = "" Then
	                                MsgBox "No existe el directorio " & ls_ruta_envio & "\santander", vbCritical, "SET"
	                                Screen.MousePointer = 0
	                                Exit Sub
	                        End If
	                    End If
	                Else
	                    ps_ruta_Archivo = ls_ruta_envio & "\santander\" & ps_Archivo
	                End If
						 */
					/*	break;
					case ConstantesSet.BANAMEX:
						if (bBanamexTef)
							rutaEnvio = rutaEnvio + "\\banamex_tef\\" + psArchivo;
						else if (bCitybankPaylinkMN)
							rutaEnvio = rutaEnvio + "\\citibank_paylink\\" + psArchivo;
						else if (bBanamexMassPayment){
							if (grid.get(0).get("fecHoy").equals(grid.get(0).get("fecTransferencia")))
								rutaEnvio = rutaEnvioMassPayment + "\\" + psArchivo;
						}
						else
							rutaEnvio = rutaEnvio + "\\banamex\\" + psArchivo;
						break;
					case ConstantesSet.BANCRECER:
						rutaEnvio = rutaEnvio + "\\bancrecer\\" + psArchivo;
						break;
					case ConstantesSet.BANCOMER:
						if (grid.get(0).get("idServicioBE").equals("CIE")){
							convenioCie = true;
							rutaEnvio = rutaEnvio + "bancomer_cie\\" + psArchivo;
							//sDescBanco = envioTransferenciasDao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_BANCOMER_CIE);
						}
						else if (psArchivo.substring(0, 3).equals("bc3"))
							rutaEnvio = rutaEnvio + "bancomer_internacional\\" + psArchivo;
						else
							rutaEnvio = rutaEnvio + "bancomer\\"; //+ psArchivo;

						break;
					case ConstantesSet.BITAL:
						if (grid.get(0).get("optCheque").equals("1"))
							rutaEnvio = rutaEnvio + "\\bital\\"; //+ psArchivo;
						else if (bBitalH2H){
							if (bInterbancario)
								rutaEnvio = rutaEnvio + "\\bital_H2H_IB\\"; // + psArchivo;
							else
								rutaEnvio = rutaEnvio + "\\bital_h2h\\"; // + psArchivo;
						}
						else if (bBitalSpeua)
							rutaEnvio = rutaEnvio + "\\HSBC\\" ; //+ psArchivo;
						else{ //Para el formato SILK de BITAL no se necesita archivo plano
							if (bInterbancario)
								rutaEnvio = rutaEnvio + "\\HSBC\\"; // + psArchivo;
							else if (!controlArchivosTransferidosDao.configuraSet(203).equals("SILK"))
								rutaEnvio = rutaEnvio + "\\HSBC\\"; // + psArchivo;

							if (controlArchivosTransferidosDao.configuraSet(222).equals("SI"))
								rutaEnvio = rutaEnvio + "\\HSBC\\"; // + psArchivo;							 
						}
						//if (creacionArchivosBusiness.abrirArchivo(rutaEnvio))
//							mensaje = "Se sobreescribio el archivo";
						break;
					case ConstantesSet.BITAL_DLS:
						if (grid.get(0).get("optCheque").equals("1"))
							rutaEnvio = rutaEnvio + "\\dap\\bitalDLS\\envio\\"; // + psArchivo;
						break;
					case ConstantesSet.BOFA:
						rutaEnvio = rutaEnvio + "\\bofa\\" + psArchivo;
						break;
					case ConstantesSet.FUBI:
						rutaEnvio = rutaEnvio + "\\fubi\\" + psArchivo;
						break;
					case ConstantesSet.AMEX:
						rutaEnvio = rutaEnvio + "\\amex\\" + psArchivo;
						break;
					case ConstantesSet.COMERICA:
						rutaEnvio = rutaEnvio + "\\comerica\\" + psArchivo;
						break;
					case ConstantesSet.BANK_BOSTON:
						rutaEnvio = rutaEnvio + "\\bank_boston\\" + psArchivo;
						break;
					case ConstantesSet.NAFIN:
						rutaEnvio = rutaEnvioNafi + "\\nafin\\" + psArchivo;
						break;
					case ConstantesSet.AZTECA:
						rutaEnvio = rutaEnvio + "\\azteca\\" + psArchivo;
						break;
					case ConstantesSet.BANORTE:
						rutaEnvio = rutaEnvio + "\\banorte\\" + psArchivo;
						break;
				}*/
				switch(idBanco) {
		    	case ConstantesSet.BANORTE:
		    		rutaBanco = "Banorte";
    		   		break;
		   		case ConstantesSet.AZTECA:
		   			rutaBanco = controlArchivosTransferidosDao.configuraSet(ConstantesSet.NOMBRE_CARPETA_AZTECA);		   
    				break;
		   		case ConstantesSet.INBURSA:
		   			rutaBanco = "Inbursa";
    				break;
		   		case ConstantesSet.SANTANDER:
		   			rutaBanco = controlArchivosTransferidosDao.configuraSet(ConstantesSet.NOMBRE_CARPETA_SANTANDER);
		   			break;
		   		case ConstantesSet.BANAMEX:
		   			if( bBanamexTef) {	
		   				
		   				//Layout - TEF (Transferencia Electronica de Fondos)
		   				rutaBanco = controlArchivosTransferidosDao.configuraSet(ConstantesSet.NOMBRE_CARPETA_BANAMEX_TEF);
		   			}else if(bCitybankFlatFile) {		//Layout - Flat File Citibank (Solo DLS)
		   				rutaBanco = controlArchivosTransferidosDao.configuraSet(ConstantesSet.NOMBRE_CARPETA_BANAMEX_FLT);
		  
		   			}else if(bCitybankPaylinkMN) {
		   				//Layout - Pay Link Citibank (Solo MN)
		   				//siquieren que se guarde en carpeta diferente el h2hAFRDBanamex quedigan en dondepara modificar el if
		   				rutaBanco = controlArchivosTransferidosDao.configuraSet(ConstantesSet.NOMBRE_CARPETA_BANAMEX_PL);
		   			}else {												//Layoutï¿½s Digitem = dtoCriBus.getOptEnvioBNX() == 0
		   				rutaBanco = controlArchivosTransferidosDao.configuraSet(ConstantesSet.NOMBRE_CARPETA_BANAMEX);
		   				;
		   			}
		   			break;
    			case ConstantesSet.BANCOMER:  
    				if( mismoBanco ) {   
    					rutaBanco = controlArchivosTransferidosDao.configuraSet(ConstantesSet.NOMBRE_CARPETA_BANCOMER_CIE);

    				}else if(bInternacional) {
    					rutaBanco = controlArchivosTransferidosDao.configuraSet(ConstantesSet.NOMBRE_CARPETA_BANCOMER_INTERNACIONAL);

    				}else {
    					rutaBanco = controlArchivosTransferidosDao.configuraSet(ConstantesSet.NOMBRE_CARPETA_BANCOMER);

    				}
    				break;
    			case ConstantesSet.BANCRECER:
    				rutaBanco = "Bancrecer";
	   
	   				break;
    			case ConstantesSet.CITIBANK_MN:
    				rutaBanco = "CitiBank"+System.getProperty("file.separator")+"MN";
	   
    				break;
    			case ConstantesSet.CITIBANK_DLS:
    				if(bBanamexMassPayment) {			//Layout - Mass Payment Citibank
		   				//mapRet = layoutBancomerBusiness.beArmaBanamexMassPay(mismoBanco == false ? true : false, lista, false, "", i, dirPrincipal);
						return mapRet;
    				}else {
    					if(bCitybankFlatFile) {		//Layout - World Link Citibank
    						rutaBanco = "CitiBank"+ System.getProperty("file.separator") +"WorldLink";
    					}else {
    						rutaBanco = controlArchivosTransferidosDao.configuraSet(ConstantesSet.NOMBRE_CARPETA_CITIBANCK);
    					}
    				}
    				break;
    			case ConstantesSet.CHASE_MANHATTAN:
    				rutaBanco = "Chase";
    				break;
    			case ConstantesSet.BITAL:
    				/*if(lbEnvioBitalH2H && dtoCriBus.getOptEnvioHSBC() == 1) {
    					if(pbMismoBanco) {
    						sDescBanco = "Bital"+System.getProperty("file.separator")+"H2H MB";
    		   
    					}
    				}else if(dtoCriBus.getOptTipoEnvio().equals("SPEUA")) {
    					sDescBanco = "Bital"+System.getProperty("file.separator")+"Speua";
		   
    				}*/
    
    				break;
    			case ConstantesSet.INVERLAT:
    				rutaBanco = "Inverlat";
    				break;
    			case ConstantesSet.BOFA:
    				rutaBanco = "Bofa";
	   
    				break;
    			case ConstantesSet.FUBI:
    				rutaBanco = "Fubi";
	   
    				break;
    			case ConstantesSet.AMEX:
    				rutaBanco = "Amex";
	   
    				break;
    			case ConstantesSet.COMERICA:
    				rutaBanco = "Comerica";
	   
    				break;
    			case ConstantesSet.BANK_BOSTON:
    				rutaBanco = "Bank Boston";
	   
    				break;
    			case ConstantesSet.AMRO_BANK:
    				rutaBanco = "Amro Bank";
	   
    				break;
    			case ConstantesSet.NAFIN:
    				rutaBanco = "Nafin";
	   
    				break;
    		}//Termina switch
				String rutaDirectorios = rutaEnvio + rutaBanco+"\\";
				rutaEnvio = rutaEnvio + rutaBanco+"\\" + psArchivo;

				for (int ii = 0; ii < registroCancelado.size(); ii++)
					importeCancelado += Double.parseDouble(registroCancelado.get(ii).get("importe"));
				//System.out.println("Ruta envio:"+rutaEnvio);
				//System.out.println("Ruta directorios:"+rutaDirectorios);
				//Actualiza arch_transfer con el nuevo importe y numero de registros activos
				System.out.println("importe a descontar "+importeCancelado);
			//	if(!psIndicador.toUpperCase().equals("PA")){
					controlArchivosTransferidosDao.actualizaArchTransfer(importeCancelado, registroTotalParam, grid.get(0).get("fecHoy"), 
							 Integer.parseInt(grid.get(0).get("usuario")), psArchivo.substring(0, psArchivo.length() - 4));

//				}
				

				//Aqui se va a eliminar el archivo existente para generar el nuevo

				System.setProperty("user.dir", rutaDirectorios);
				nombres = busquedaArchivos.obtenerNombreArchivos(".txt");

				if (nombres != null){
					if (nombres.length > 0) {
						for(int arch = 0; arch < nombres.length; arch++){
							System.out.println(nombres[arch].getName());
							if (nombres[arch].getName().equals(psArchivo))
								nombres[arch].delete();

						}
					}
				}
			
				if (registroCancelado.size() > 0){
					for (int i = 0; i < registroCancelado.size(); i++){
						importeCancelado = importeCancelado + Double.parseDouble(registroCancelado.get(i).get("importe"));


						
						if (bTraspaso) {
						System.out.println("entro a traspaso "+bTraspaso);
							//Se manda llamar el revividor
							mapRet = controlArchivosTransferidosDao.ejecutarRevividor(grid.get(i),true);
							System.out.println("resultado del revividor:"+mapRet);

							if (!mapRet.get("result").toString().equals("0"))
								mensaje = "Error en el revividor";
							else
								mensaje = "Registro cancelado con exito";
							System.out.println("mensaje "+mensaje);

						}
						else{ //Si no es traspaso esto aplica para las transfer
							psEstatusMov = "P";
							System.out.println("bBanamexMassPayment "+bBanamexMassPayment);
							if (bBanamexMassPayment){
								if (registroCancelado.get(i).get("idTipoOperacion").equals("3000"))
									psEstatusMov = "M";
								else if(registroCancelado.get(i).get("idTipoOperacion").equals("3000") && 
										registroCancelado.get(i).get("idBancoBenef").equals("0") && idBanco == ConstantesSet.NAFIN){
									psEstatusMov = "N";
								}
							}
							
								recibeRespuesta = controlArchivosTransferidosDao.actualizaEstatus(Integer.parseInt(registroCancelado.get(i).get("noFolioDet")), psEstatusMov);
						
						}
						
						//if(!psIndicador.toUpperCase().equals("PA")){
							controlArchivosTransferidosDao.actualizaDetArchTransfer(psArchivo.substring(0, psArchivo.length() - 4), 
									Integer.parseInt(registroCancelado.get(i).get("noFolioDet")));

						//}
						
						psFolios = psFolios + registroCancelado.get(i).get("noFolioDet") + ",";
					}

					if (recibeRespuesta > 0)
						mapRet.put("msgUsuario", "Se cancelaron los registros con exito");
				}
				//else{ //Aqui se va a regenerar el archivo
					//Se genero un objeto de MovimientoDto por que en las funciones del armado del layout es como se recibe
					for(int m = 0; m < grid.size(); m++){
						for(int j=0; j <registroCancelado.size();j++){
							if(grid.get(m).get("noFolioDet").equals(registroCancelado.get(j).get("noFolioDet"))){
								grid.remove(m);
							}
						}
					}
					
					List<MovimientoDto> lista = new ArrayList<MovimientoDto>();
					
					for(int i = 0; i < grid.size(); i++){//llena  dto con el grid
						MovimientoDto dto = new MovimientoDto();

						dto.setNoFolioDet(Integer.parseInt(grid.get(i).get("noFolioDet")));
						//dto.setIdTipoOperacion(Integer.parseInt(!grid.get(i).get("idTipoOperacion").equals("null") ? grid.get(i).get("idTipoOperacion"):""));
						dto.setIdBancoBenef(Integer.parseInt(grid.get(i).get("idBancoBenef")!=null ? grid.get(i).get("idBancoBenef"):""));
						dto.setImporte(Double.parseDouble(grid.get(i).get("importe")));
						dto.setImporteStr(grid.get(i).get("importe"));
						dto.setIdBanco(Integer.parseInt(grid.get(i).get("idBanco")));
						dto.setNoDocto(grid.get(i).get("noDocto")!=null ? grid.get(i).get("noDocto"):"");
						dto.setFecValor(funciones.ponerFechaDate(grid.get(i).get("fecValor"))); 
						//dto.setFecValor(null);
						dto.setBeneficiario(grid.get(i).get("beneficiario")!=null ? grid.get(i).get("beneficiario"):"");
						dto.setIdDivisa(grid.get(i).get("idDivisa"));
						dto.setDescBancoBenef(grid.get(i).get("nombreBancoBenef")!=null ? grid.get(i).get("nombreBancoBenef"):"");
						dto.setIdChequeraBenef(grid.get(i).get("idChequeraBenef")!=null ? grid.get(i).get("idChequeraBenef"):"");
						dto.setIdChequeraBenefReal(grid.get(i).get("idChequeraBenefReal")!=null ? grid.get(i).get("idChequeraBenefReal"):"");
						dto.setConcepto(grid.get(i).get("concepto"));
						dto.setNoEmpresa(Integer.parseInt(grid.get(i).get("noEmpresa")));
						dto.setOrigenMov(grid.get(i).get("origenMov"));
						dto.setNoCliente(grid.get(i).get("noCliente"));
						dto.setEspeciales(grid.get(i).get("especiales"));
						dto.setComplemento(grid.get(i).get("complemento"));
						dto.setPlazaBenef(grid.get(i).get("plazaBenef"));
						dto.setSucDestino(grid.get(i).get("sucDestino"));
						dto.setClabeBenef(grid.get(i).get("clabeBenef"));
						dto.setBLayoutComerica(grid.get(i).get("bLayoutComerica"));
						dto.setIdChequera(grid.get(i).get("idChequera"));
						dto.setInstFinan(grid.get(i).get("instFinan"));
						dto.setSucOrigen(grid.get(i).get("sucOrigen")!=null ? grid.get(i).get("sucOrigen"):"");
						dto.setRfcBenef(grid.get(i).get("rfcBenef"));
						dto.setHoraRecibo(grid.get(i).get("horaRecibo"));
						dto.setNomBancoIntermediario(grid.get(i).get("nomBancoIntermediario"));
						dto.setDescBancoBenef(grid.get(i).get("descBancoBenef"));
						dto.setAba(grid.get(i).get("aba")!=null ? grid.get(i).get("aba"):"");
						dto.setSwiftCode(grid.get(i).get("swiftCode")!=null ? grid.get(i).get("swiftCode"):"");
						dto.setAbaIntermediario(grid.get(i).get("abaIntermediario")!=null ? grid.get(i).get("abaIntermediario"):"");
						dto.setSwiftIntermediario(grid.get(i).get("swiftIntermediario")!=null ? grid.get(i).get("swiftIntermediario"):"");
						dto.setAbaCorresponsal(grid.get(i).get("abaCorresponsal")!=null ? grid.get(i).get("abaCorresponsal"):"");
						dto.setSwiftCorresponsal(grid.get(i).get("swiftCorresponsal")!=null ? grid.get(i).get("swiftCorresponsal"):"");
						dto.setNomBancoCorresponsal(grid.get(i).get("nomBancoCorresponsal")!=null ? grid.get(i).get("nomBancoCorresponsal"):"");
						dto.setDescUsuarioBital (grid.get(i).get("descUsuarioBital")!=null ? grid.get(i).get("descUsuarioBital"):"");
						dto.setDescServicioBital(grid.get(i).get("descServicioBital")!=null ? grid.get(i).get("descServicioBital"):"");
						dto.setFecAlta(funciones.ponerFechaDate(grid.get(i).get("fecHoy")));
						//System.out.println("cheque banamex lista "+grid.get(i).get("idChequeraBanamex"));
						dto.setIdChequeraBanamex(grid.get(i).get("idChequeraBanamex")!=null ? grid.get(i).get("idChequeraBanamex"):"");
						dto.setHoraRecibo(grid.get(i).get("horaRecibo")!=null ? grid.get(i).get("horaRecibo"):"");
						if(grid.get(i).get("plaza") == null || grid.get(i).get("plaza").equals("") )
							dto.setPlazaStr("0");//dto.setPlaza(0),
							
						else
							dto.setPlazaStr(grid.get(i).get("plaza"));

						if(grid.get(i).get("tipoEnvioLayout") == null ||grid.get(i).get("tipoEnvioLayout").equals("")  )
							dto.setTipoEnvioLayout(0);
						else
							dto.setTipoEnvioLayout(Integer.parseInt(grid.get(i).get("tipoEnvioLayout")));
						//dto.setPlaza(!grid.get(i).get("plaza").equals("") && grid.get(i).get("plaza")!=null ?  Integer.parseInt(grid.get(i).get("plaza")):0);
						//dto.setTipoEnvioLayout(grid.get(i).get("tipoEnvioLayout").trim().equals("") ? 0 : Integer.parseInt(grid.get(i).get("tipoEnvioLayout")));

						lista.add(dto);
					if (!grid.get(i).get("descEstatus").equals("CANCELADO")){
					//	System.out.println("banco" +idBanco);
							switch (idBanco){
							case ConstantesSet.CITIBANK_DLS:
								if(bBanamexMassPayment) {			//Opcion de Citibank PayLink MN
	                   				psRegistro = layoutBancomerBusiness.beArmaCitibankPaylinkMN485(lista, i, "");
	                    
	                   				if(psRegistro != null && !psRegistro.equals("")) 
	                    				mapRet.put("msgRegistro", psRegistro);
	                    			else
	                    				mapRet.put("msgUsuario", "No se encontró el folio <folio_paylink>!!");
	                   			}else {
	                   				if(psIndicador.equals("WL")) 			//Opcion de Citibank WorldLink
	                   					tipoTransferencia = 5;
	                   				else if(bCitybankFlatFile && !grid.get(0).get("idServicioBE").equals("ACH"))	//Opcion de Citibank ACH
	                   					tipoTransferencia = 4;
	                   				else if(tipoTransferencia == 1) {			//Opcion de Interbancaria
	                   					if(lista.get(i).getTipoEnvioLayout() != 0)
	                   						tipoTransferencia = lista.get(i).getTipoEnvioLayout();
	                   					else if(!lista.get(i).getNomBancoIntermediario().trim().equals(""))
	                   						tipoTransferencia = 3;
	                   					else
	                   						tipoTransferencia = 1;
	                   				}
	                   				mapRet = layoutBancomerBusiness.beExportacion(lista, i, tipoTransferencia,
	                   						grid.get(0).get("nomArchivo"), rutaEnvio, idBanco, "TRANSFERENCIA");
	                   
	                   			}
								/*if (bBanamexMassPayment){
									if (grid.get(0).get("fecTransferencia").equals(grid.get(0).get("fecHoy"))){
										rutaEnvio = rutaEnvioMassPayment + "\\" + psArchivo; 
									}
								}
								else if (bCitybankFlatFile && !grid.get(0).get("idServicioBE").equals("ACH"))
									rutaEnvio = rutaEnvio + "\\citibank_FlatFile\\" + psArchivo;
								else if (psIndicador.equals("WL"))
									rutaEnvio = rutaEnvio + "\\citibank_WL\\" + psArchivo;
								else
									rutaEnvio = rutaEnvio + "\\citibank_dls\\" + psArchivo;		*/
								break;
							case ConstantesSet.INBURSA:
								//rutaEnvio = rutaEnvio + "\\inbursa\\" + psArchivo;
								break;
							case ConstantesSet.INVERLAT:
								//rutaEnvio = rutaEnvio + "\\inverlat\\" + psArchivo;
								break;
							case ConstantesSet.CHASE_MANHATTAN:
								//rutaEnvio = rutaEnvio + "\\chase\\" + psArchivo;
								break;
							case ConstantesSet.SANTANDER:
								if(bChequeOcurreSantanderDirecto) { 
	                   				psRegistro = layoutBancomerBusiness.beArmaH2HSantander(obtenerParametrosLayout(lista, i, mismoBanco), i, importeCancelado, lista.size(), false, false);//Arma la cadena
	            					if(psRegistro != null && !psRegistro.equals("")) 
	                    				mapRet.put("msgRegistro", psRegistro);
	                   			}else {
	                   				if(!psArchivo.substring(0,4).equals("tran")){
		                   			mapRet = layoutBancomerBusiness.beExportacion(lista, i, tipoTransferencia,
		                   					grid.get(0).get("nomArchivo").toString(), rutaEnvio, idBanco, "TRANSFERENCIA");
	                   				}
	                   			}

								/*if (grid.get(0).get("optCheque").equals("1"))
									if (bChequeOcurreSantanderDirecto) {
										rutaEnvio = rutaEnvio + "\\santander_directo\\" + psArchivo;
									}
									else{
										rutaEnvio = rutaEnvio + "\\santander\\" + psArchivo;
									}
								else
									rutaEnvio = rutaEnvio + "\\santander\\" + psArchivo;*/
								/*
			            Case SANTANDER
			                If optcheque.Value = True Then    'Para cheque ocurre
			                    If pbChequeOcurreSantander_Directo = True Then
			                        ps_ruta_Archivo = ls_ruta_envio & "\santander_directo\" & ps_Archivo
			                        If Dir(ls_ruta_envio & "\santander_directo", vbDirectory) = "" Then
			                            MsgBox "No existe el directorio " & ls_ruta_envio & "\santander_directo", vbCritical, "SET"
			                            Screen.MousePointer = 0
			                            Exit Sub
			                        End If
			                    Else
			                        ps_ruta_Archivo = ls_ruta_envio & "\santander\" & ps_Archivo
			                        If Dir(ls_ruta_envio & "\santander", vbDirectory) = "" Then
			                                MsgBox "No existe el directorio " & ls_ruta_envio & "\santander", vbCritical, "SET"
			                                Screen.MousePointer = 0
			                                Exit Sub
			                        End If
			                    End If
			                Else
			                    ps_ruta_Archivo = ls_ruta_envio & "\santander\" & ps_Archivo
			                End If
								 */
								break;
								case ConstantesSet.BANAMEX:
									im=im+lista.get(i).getImporte();
//									String cyti=lista.get(i).getIdBancoCityStr();
//                    				String bancoB=lista.get(i).getDescBancoBenef();
									rutaEnvio = ConstantesSet.CARPETA_RAIZ_RESPALDO;
                    				rutaBanco+="\\";
                    			
                    				if(mismoBanco && !psIndicador.toUpperCase().equals("PA")) {
                    			//	System.out.println("mismo banco");
//	                    			mapRet = layoutBancomerBusiness.beExportacion(lista, i, tipoTransferencia,mapCreaArch.get("nomArchivo").toString(), rutaEnvio, idBanco, "TRANSFERENCIA");
//                    				mapRet = layoutBancomerBusiness.beExportacion(lista, i, tipoTransferencia,psArchivo, rutaEnvio, idBanco, "TRANSFERENCIA");
	                    			psRegistro = layoutBancomerBusiness.beArmaCitibankPaylinkMN485(lista, i, "070005");
                    				if(psRegistro != null && !psRegistro.equals("")){ 
                        				result.put("msgRegistro", psRegistro);
        	                   			mapRet = creacionArchivosBusiness.escribirArchivoLayout(rutaEnvio,rutaBanco+"//",psArchivo,psRegistro);
        	                   			
        	        						if ((Boolean)mapRet.get("resExito")) {
        	        							mapRet.put("msgRegistro", psRegistro);
        	    								beArmaNuevos = true;
        									} else {
        	        							mapRet.put("msgUsuario",mapRet.get("msgUsuario").toString());
        	        							return mapRet;
        									}
        	       
                    				}else{
                    					result.put("msgUsuario", "No se encontro el folio <folio_paylink>!!");
                       					return result;
                        			}
			                    			
	                    		}else {
//	                    			if((payLink)&&(h2hAfrdBanamex)){
//	                    				
//	                    			System.out.println("entro a paylink y h2hafrdBanamex=true");
////	                    				psRegistro = layoutBancomerBusiness.beArmaCitibankPaylinkMNRevive(lista, i, "070005",cyti,bancoB);
//	                    				psRegistro = layoutBancomerBusiness.beArmaCitibankPaylinkMN485(lista, i, "070005");
//	                    				if(psRegistro != null && !psRegistro.equals("")){ 
//                            				result.put("msgRegistro", psRegistro);
//                            			   // psRegistro += layoutBancomerBusiness.Trailer_citibank_paylink_MN(lista.size(),im,true);
//                            			   // im=0;
//                            				cyti="";
//    	                    				bancoB="";
//                            			  //  im=0;
//	                    				}else{
//                            				result.put("msgUsuario", "No se encontro el folio <folio_paylink>!!");
//                           					return result;
//                            			}
//	                    				
//	                    				
//	                    			}
	                    			
	                    			if ((payLink)&&(h2hAfrdBanamex==false)) {
	                    				rutaEnvio = ConstantesSet.CARPETA_RAIZ_RESPALDO;
	                    				rutaBanco+="\\";
	                    			//	System.out.println("entro a paylink y h2hafrdBanamex=false");
//	                    				psRegistro = layoutBancomerBusiness.beArmaCitibankPaylinkMNRevive(lista, i, "070005",cyti,bancoB);
	                    				psRegistro=layoutBancomerBusiness.beArmaCitibankPaylinkMN485(lista, i, "070005");
	                    				if(psRegistro != null && !psRegistro.equals("")){ 
                            				result.put("msgRegistro", psRegistro);   
            	                   				mapRet = creacionArchivosBusiness.escribirArchivoLayout(rutaEnvio,rutaBanco,psArchivo,psRegistro);
            	                  
            	        						if ((Boolean)mapRet.get("resExito")) {
            	        							mapRet.put("msgRegistro", psRegistro);
            	    								beArmaNuevos = true;
            									} else {
            	        							mapRet.put("msgUsuario",mapRet.get("msgUsuario").toString());
            	        							return mapRet;
            									}
            	        
	                    				}else{
                            				result.put("msgUsuario", "No se encontro el folio <folio_paylink>!!");
                           					return result;
                            			}
	                    				
									}else{
										if(!psIndicador.toUpperCase().equals("PA")){
									//		System.out.println("entro parte falsa para generra BANAMEX INTER");
											psRegistro = layoutBancomerBusiness.beArmaBanamexInter(lista, i);
		                    				if(psRegistro != null && !psRegistro.equals("")){ 
		                    				mapRet.put("msgRegistro", psRegistro);
		                    				psRegistro += layoutBancomerBusiness.Trailer_citibank_paylink_MN(lista.size(),im,true);
	                        			    im=0;
	                        				
		                    				}else{
		                    				mapRet.put("msgUsuario", "Error al armar Banamex Interbancario");
		                    				}
										}		
									}	    
	                    		}

								
								if (bBanamexTef)
									rutaEnvio = rutaEnvio + "\\banamex_tef\\" + psArchivo;
								else if (bCitybankPaylinkMN)
									rutaEnvio = rutaEnvio + "\\citibank_paylink\\" + psArchivo;
								
								else if (bBanamexMassPayment){
									if (grid.get(0).get("fecHoy").equals(grid.get(0).get("fecTransferencia")))
										rutaEnvio = rutaEnvioMassPayment + "\\" + psArchivo;
								}
								else
									rutaEnvio = rutaEnvio + "\\banamex\\" + psArchivo;
								break;
							case ConstantesSet.BANCRECER:
								//rutaEnvio = rutaEnvio + "\\bancrecer\\" + psArchivo;
								break;
							case ConstantesSet.BANCOMER:
								/*if (grid.get(0).get("idServicioBE").equals("CIE")){
									convenioCie = true;
									rutaEnvio = rutaEnvio + "\\bancomer_cie\\" + psArchivo;
								}
								else if (psArchivo.substring(0, 3).equals("bc3"))
									rutaEnvio = rutaEnvio + "\\bancomer_internacional\\" + psArchivo;
								else
									rutaEnvio = rutaEnvio + "\\bancomer\\";// + psArchivo;
									*/




								//psRegistro = layoutBancomerBusiness.beArmaBancomerC43(obtenerParametrosLayout(lista, i, mismoBanco));//Arma la cadena

								if(grid.get(0).get("idServicioBE").equals("CIE")){
                    				psRegistro = layoutBancomerBusiness.beArmaConvenioCIE(
                    						obtenerParametrosLayout(lista, i, mismoBanco));//Arma la cadena
                
                       				if(psRegistro != null && !psRegistro.equals("")) 
                       					mapRet.put("msgRegistro", psRegistro);
                				}else{
                					psRegistro = layoutBancomerBusiness.beArmaBancomerC43(obtenerParametrosLayout(lista, i, mismoBanco));//Arma la cadena
    
                				}
								break;
							case ConstantesSet.AZTECA:	//ok
	                   			psRegistro = layoutAztecaBusiness.beArmaAzteca(lista.get(i), i, mismoBanco, false );
	                   
	                   			if(psRegistro!=null && !psRegistro.equals(""))
	        					{
	                   
	                   				mapRet = creacionArchivosBusiness.escribirArchivoLayout(rutaEnvio,rutaBanco+"//",psArchivo,psRegistro);
	                   						//rutaEnvio,idBanco+"",
	        								//mapCreaArch.get("nomArchivo").toString(),psRegistro);
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
	                   
	                   			break;
							case ConstantesSet.BITAL:
								mapRet = layoutBancomerBusiness.beArmaHSBCH2HIB(lista, 0, tipoTransferencia,
										 psArchivo, rutaEnvio, ConstantesSet.BITAL, "CONTROL ARCHIVOS");
								if(bBitalH2H && grid.get(0).get("optCheque").equals("1")) {
			    					if(mismoBanco) {
			    						mapRet = layoutBancomerBusiness.beArmaHSBCH2HMB(lista, i, tipoTransferencia,
		        								mapCreaArch.get("nomArchivo").toString(), rutaEnvio, ConstantesSet.BITAL, "TRANSFERENCIA");  //ok
			    					}else {
			    						mapRet = layoutBancomerBusiness.beArmaHSBCH2HIB(lista, i, tipoTransferencia,
		        								mapCreaArch.get("nomArchivo").toString(), rutaEnvio, ConstantesSet.BITAL, "TRANSFERENCIA");//ok
			    						return mapRet;
			    					}
			    				}else if(bBitalSpeua) {
			    					mapRet = layoutBancomerBusiness.beExportacion(lista, i, tipoTransferencia,
	        								mapCreaArch.get("nomArchivo").toString(), rutaEnvio, ConstantesSet.BITAL, "TRANSFERENCIA");  //ok p
			    				}

								mapRegistro = layoutBancomerBusiness.beArmaHSBCH2HMB(lista, i, tipoTransferencia,
										psArchivo, rutaEnvio, ConstantesSet.BITAL, "CONTROL ARCHIVOS");


								/*mapRet = layoutBancomerBusiness.beArmaHSBCH2HIB(lista, 0, tipoTransferencia,
										 psArchivo, rutaEnvio, ConstantesSet.BITAL, "CONTROL ARCHIVOS");*/
								/*if (grid.get(0).get("optCheque").equals("1"))
									//rutaEnvio = rutaEnvio + "\\bital\\" + psArchivo;
									mapRet = layoutBancomerBusiness.beArmaHSBCH2HIB(lista, 0, tipoTransferencia,
											psArchivo, rutaEnvio, ConstantesSet.BITAL, "CONTROL ARCHIVOS");
								else if (bBitalH2H){
									if (bInterbancario){
										//rutaEnvio = rutaEnvio + "\\bital_H2H_IB\\" + psArchivo;
										mapRet = layoutBancomerBusiness.beArmaHSBCH2HIB(lista, 0, tipoTransferencia,
										psArchivo, rutaEnvio, ConstantesSet.BITAL, "CONTROL ARCHIVOS");

										return mapRet;
									}
									else
										rutaEnvio = rutaEnvio + "\\bital_h2h\\" + psArchivo;
								}
								else if (bBitalSpeua)
									rutaEnvio = rutaEnvio + "\\HSBC\\" + psArchivo;
								else{ //Para el formato SILK de BITAL no se necesita archivo plano
									if (bInterbancario)
										rutaEnvio = rutaEnvio + "\\HSBC\\" + psArchivo;
									else if (!controlArchivosTransferidosDao.configuraSet(203).equals("SILK"))
										rutaEnvio = rutaEnvio + "\\HSBC\\" + psArchivo;

									if (controlArchivosTransferidosDao.configuraSet(222).equals("SI"))
										rutaEnvio = rutaEnvio + "\\HSBC\\" + psArchivo;							 
								}
								*/

								/*
								 * if(lbEnvioBitalH2H && dtoCriBus.getOptEnvioHSBC() == 1) {
			    					if(pbMismoBanco) {
			    						mapRespExportaReg = layoutBancomerBusiness.beArmaHSBCH2HMB(listBus, i, piTipoTrasferencia,
		        								mapCreaArch.get("nomArchivo").toString(), dirPrincipal, piBanco, "TRANSFERENCIA");
			    					}else {
			    						mapRet = layoutBancomerBusiness.beArmaHSBCH2HIB(listBus, i, piTipoTrasferencia,
		        								mapCreaArch.get("nomArchivo").toString(), dirPrincipal, piBanco, "TRANSFERENCIA");
			    						return mapRet;
			    					}
			    				}else if(dtoCriBus.getOptTipoEnvio().equals("SPEUA")) {
			    					mapRespExportaReg = layoutBancomerBusiness.beExportacion(listBus, i, piTipoTrasferencia,
	        								mapCreaArch.get("nomArchivo").toString(), dirPrincipal, piBanco, "TRANSFERENCIA");
			    				}
								 */
								//if (creacionArchivosBusiness.abrirArchivo(rutaEnvio))
//									mensaje = "Se sobreescribio el archivo";
								break;
							case ConstantesSet.BITAL_DLS:
								if (grid.get(0).get("optCheque").equals("1"))
								//	rutaEnvio = rutaEnvio + "\\dap\\bitalDLS\\envio\\" + psArchivo;
								break;
							case ConstantesSet.BOFA:
								//rutaEnvio = rutaEnvio + "\\bofa\\" + psArchivo;
								break;
							case ConstantesSet.FUBI:
								//rutaEnvio = rutaEnvio + "\\fubi\\" + psArchivo;
								break;
							case ConstantesSet.AMEX:
								//rutaEnvio = rutaEnvio + "\\amex\\" + psArchivo;
								break;
							case ConstantesSet.COMERICA:
								//rutaEnvio = rutaEnvio + "\\comerica\\" + psArchivo;
								break;
							case ConstantesSet.BANK_BOSTON:
								//rutaEnvio = rutaEnvio + "\\bank_boston\\" + psArchivo;
								break;
							case ConstantesSet.NAFIN:
								//rutaEnvio = rutaEnvioNafi + "\\nafin\\" + psArchivo;
								break;

							case ConstantesSet.BANORTE:
								//rutaEnvio = rutaEnvio + "\\banorte\\" + psArchivo;
								break;
						}
						}

						if (!grid.get(i).get("descEstatus").equals("CANCELADO")){
							if (!beArmaNuevos) {
				    			//if(!mapRet.get("msgRegistro").equals("")) {
								rutaEnvio = ConstantesSet.CARPETA_RAIZ_RESPALDO;
								System.out.println("Ruta de envio 2: "+rutaEnvio);
								System.out.println("Ruta de envio 2: "+rutaBanco +"//");
								rutaBanco+="\\";
								if(!psArchivo.substring(0,4).equals("tran")){
									if(psArchivo.substring(0,2).equals("st")){
										psRegistro=mapRet.get("msgRegistro").toString();
										
									}
									if(!psIndicador.toUpperCase().equals("PA")){
//										mapRet = creacionArchivosBusiness.escribirArchivoLayout(rutaEnvio,rutaBanco,psArchivo,mapRet.get("msgRegistro").toString());				    			
										mapRet = creacionArchivosBusiness.escribirArchivoLayout(rutaEnvio,rutaBanco,psArchivo,psRegistro);
										if(!(Boolean)mapRet.get("resExito")) {
				        					mapRet.put("msgUsuario", "Error al Generar Archivo " + mapCreaArch.get("psBanco").toString());
											return mapRet;
										}
									}
				    				
								}
//								}else {
//									mapRet.put("msgUsuario", mapRet.get("msgUsuario"));
//									return mapRet;
//								}
							}
						}
						else if (!mapRegistro.get("msgRegistro").equals("") && !mapRegistro.get("msgRegistro").equals(null)){
//								System.out.println("entro a msgRegistro");
//								if(!mapRet.get("msgRegistro").equals("")) {
//				    				mapRet = creacionArchivosBusiness.escribirArchivoLayout(rutaEnvio,rutaBanco,
//											   psArchivo, mapRet.get("msgRegistro").toString());
//				    				if(!(Boolean)mapRet.get("resExito")) {
//			        					mapRet.put("msgUsuario", "Error al Generar Archivo " + mapCreaArch.get("psBanco").toString());
//										return mapRet;
//									}
//				    
//								}else {
//									mapRet.put("msgUsuario", mapRet.get("msgUsuario"));
//									return mapRet;
//								}
//							}

							controlArchivosTransferidosDao.actualizaArchivoRegenerado(psArchivo.substring(0, psArchivo.length() - 4), 
																				Integer.parseInt(grid.get(i).get("noFolioDet")));

						}
													
					}//fin del for
					if(!psIndicador.toUpperCase().equals("PA")){
						rutaEnvio = ConstantesSet.CARPETA_RAIZ_RESPALDO;
						psRegistro = layoutBancomerBusiness.Trailer_citibank_paylink_MN(lista.size(),im,true); 		
	            		System.out.println("datos del TRL..::"+psRegistro);
            			System.out.println("rutaEnvio "+rutaEnvio+" rutaBanco "+rutaBanco+" psAr "+psArchivo);
	            		if(psRegistro != null && !psRegistro.equals("")){ 
	            			 mapRet.put("msgRegistro", psRegistro);
	            				mapRet = creacionArchivosBusiness.escribirArchivoLayout(rutaEnvio,rutaBanco,psArchivo,psRegistro);
	            				if(!(Boolean)mapRet.get("resExito")) {
		        					mapRet.put("msgUsuario", "Error al Generar Archivo " + mapCreaArch.get("psBanco").toString());
									return mapRet;
								}
	            		}else{
	       					mapRet.put("msgUsuario", "No se armo el triler!!");
	       					return mapRet;
	        			}
					}
								
			}else{
				psArchivo = registroCancelado.get(0).get("nomArchivo");
				for(int i = 0; i < registroCancelado.size(); i++){
					//Actualiza arch_transfer con el nuevo importe y numero de registros activos
					importeCancelado = Double.parseDouble(registroCancelado.get(i).get("importe"));
					controlArchivosTransferidosDao.actualizaArchCancelado(importeCancelado, 1, psArchivo.substring(0, psArchivo.length() - 4));

					controlArchivosTransferidosDao.actualizaDetArchTransfer(psArchivo.substring(0, psArchivo.length() - 4), Integer.parseInt(registroCancelado.get(i).get("noFolioDet")));

					controlArchivosTransferidosDao.actualizaEstatus(Integer.parseInt(registroCancelado.get(i).get("noFolioDet")), "P");
				}
			}

				mapRet.put("msgUsuario", "Registros procesados con exito " + psArchivo);
			//}

			/*importeTotal = Double.parseDouble(grid.get(0).get("importeTotal")) - importeCancelado;
			registroTotal = Integer.parseInt(grid.get(0).get("registroTotal")) - registroCancelado.size();

			//Actualiza registro en arch_transfer

				*/

		}
		catch(Exception e){
			System.out.println("error en el business regenerar");
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosBusiness, M: regenerar");
		}return mapRet; 
	}


	public ParametrosLayoutDto obtenerParametrosLayout(List<MovimientoDto> list, int i, boolean mismoBanco)
	{
		try{

			ParametrosLayoutDto paramLayDto = new ParametrosLayoutDto();

			paramLayDto.setMismoBanco(mismoBanco);
			paramLayDto.setIdChequeraBenef(list.get(i).getIdChequeraBenef());
			paramLayDto.setPiPlaza(list.get(i).getPlaza());
			//paramLayDto.setPiPlazaBenef(list.get(i).getPlazaBenef()!=null?Integer.parseInt(list.get(i).getPlazaBenef()):0);
			paramLayDto.setIdChequera(list.get(i).getIdChequera());
			paramLayDto.setImporte(list.get(i).getImporte());
			paramLayDto.setBeneficiario(list.get(i).getBeneficiario());
			paramLayDto.setIdBancoBenef(list.get(i).getIdBancoBenef() > 0 ? "" + list.get(i).getIdBancoBenef():"");
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
			paramLayDto.setPsDivisa(list.get(i).getIdDivisa());

			return paramLayDto;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:BancaElectronica, C:EnvioTransferenciasBusiness, M:parametrosLayout");
			return null;
		}

	}


	public Date ponerFechaDate(String fechaString){
		SimpleDateFormat dateFecha=new SimpleDateFormat("dd-MM-yyyy");
		try{
			Date fechaRet;
			return fechaRet = dateFecha.parse(fechaString);
		}catch (ParseException e) {
			System.out.println(e.toString());
		return null;
		}
	}

	//****************************************************************************************************************************************
	public ControlArchivosTransferidosDao getControlArchivosTransferidosDao() 
	{
		return controlArchivosTransferidosDao;
	}

	public void setControlArchivosTransferidosDao(ControlArchivosTransferidosDao controlArchivosTransferidosDao) {
		this.controlArchivosTransferidosDao = controlArchivosTransferidosDao;
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

	public LayoutAztecaBusiness getLayoutAztecaBusiness() {
		return layoutAztecaBusiness;
	}

	public void setLayoutAztecaBusiness(LayoutAztecaBusiness layoutAztecaBusiness) {
		this.layoutAztecaBusiness = layoutAztecaBusiness;
	}


}
