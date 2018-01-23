package com.webset.set.bancaelectronica.business;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webset.set.bancaelectronica.dao.ControlArchivosTransferidosADao;
import com.webset.set.bancaelectronica.dto.ArchTransferDto;
import com.webset.set.bancaelectronica.service.ControlArchivosTransferidosAService;
import com.webset.set.layout.business.CreacionArchivosBusiness;
import com.webset.set.layout.business.LayoutAztecaBusiness;
import com.webset.set.layout.business.LayoutBancomerBusiness;
import com.webset.set.layout.business.LayoutBanorteBusiness;
import com.webset.set.layout.business.LayoutScotiabankInverlatBusiness;
import com.webset.set.layout.dto.ParametrosLayoutDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.BusquedaArchivos;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.RevividorDto;
//com.webset.set.utilerias.dto.MovimientoDto
public class ControlArchivosTransferidosABusiness implements ControlArchivosTransferidosAService {
	Bitacora bitacora;
	private ControlArchivosTransferidosADao controlArchivosTransferidosADao;
	private LayoutAztecaBusiness layoutAztecaBusiness;
	private LayoutBancomerBusiness layoutBancomerBusiness;
	private LayoutScotiabankInverlatBusiness layoutScotiabankInverlatBusiness;
	private LayoutBanorteBusiness layoutBanorteBusiness;
	private CreacionArchivosBusiness creacionArchivosBusiness;
	ArchTransferDto objetoDto = new ArchTransferDto();
	Funciones funciones = new Funciones();
	
	
	BusquedaArchivos busquedaArchivos;
	//LayoutAztecaBusiness layoutAztecaBusiness// = new LayoutAztecaBusiness();
	File[] nombres;
	

	public String configuraSet(int indice){
		return controlArchivosTransferidosADao.configuraSet(indice);
	}
	
	public String armaCadenaConeccion(){
		List<String> listDatosCadena = new ArrayList<String>();
		String result = "";
		try {
			/*listDatosCadena = controlArchivosTransferidosADao.armaCadenaConexion();
	    	if (listDatosCadena.size()!=0 ) {
	    		result = "smb://"+listDatosCadena.get(0)+":"+listDatosCadena.get(1)+
		    			"@"+listDatosCadena.get(2)+"/"+listDatosCadena.get(3);
	    	
			} */
	    	result = ConstantesSet.CARPETA_RAIZ_RESPALDO;
	    	
	    	System.out.println(result);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +
					"P: BancaElectronica, C: ControlArchivosTransferidosBusinessA, M: armaCadenaConeccion");
		}
		return result;
		
	}

	public List<ArchTransferDto> llenaComboArchivos (String fecValor, String bChequeOcurre){
		return controlArchivosTransferidosADao.llenaComboArchivos(fecValor, bChequeOcurre);
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
			lista = controlArchivosTransferidosADao.llenaGridArchivos(objetoDto);
			System.out.println("2");
			System.out.println("lista res.size"+lista.size());
		}catch(Exception e){
			System.out.println("Error business llenaGridArchivos");
		}return lista;
	}


	public List<MovimientoDto> llenaGridDetalle(String nomArchivo, int idBanco, boolean enviadasHoy, String bChequeOcurre){
		String recibeResultado = "";
		List<MovimientoDto> listaResultado = new ArrayList<MovimientoDto>();

		try{
			if (bChequeOcurre.equals("")){
				//Busca si es Traspaso o Transferencia
				recibeResultado = controlArchivosTransferidosADao.obtieneTipoDeMovto(nomArchivo);

				if (!recibeResultado.equals("")){
					//Obtiene el detallado dependiendo el tipo de operacion
					listaResultado =  controlArchivosTransferidosADao.llenaGridDetalle(nomArchivo, idBanco, enviadasHoy, recibeResultado);
				}
			}
			//else
				//listaResultado = controlArchivosTransferidosADao.llenaGridDetalleCheque(nomArchivo);

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
		int bArmaBanamexCity = 0;
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
		boolean beArmaNuevos = false;
		Map<String, Object> mapCreaArch = new HashMap<String,Object>();
		Map<String, Object> mapRet = new HashMap<String, Object>();
		Map<String, Object> mapRegistro = new HashMap<String, Object>();
		List<RevividorDto> listRevividor = new ArrayList<RevividorDto>();
		String psFolios = "";
		int recibeRespuesta = 0;
		String psRegistro = "";
		BusquedaArchivos busquedaArchivos = new BusquedaArchivos();
		String rutaBanco="";
		String rutaDinamica="";

		try {
			if(grid.size() != 0){
				System.out.println("grid con datos");
				psArchivo = grid.get(0).get("nomArchivo");
				//rutaEnvio = grid.get(0).get("directorio");
				if (controlArchivosTransferidosADao.obtieneTipoDeMovto(psArchivo.substring(0, psArchivo.length()-4)).equals("TRASPASOS")) 
					bTraspaso=true;


				
				rutaEnvio = ConstantesSet.CARPETA_RAIZ_RESPALDO; //ruta dinamica 
				rutaDinamica =  ConstantesSet.CARPETA_RAIZ_RESPALDO; //ruta dinamica
				rutaEnvioMassPayment = controlArchivosTransferidosADao.configuraSet(196);
				rutaEnvioNafi = controlArchivosTransferidosADao.configuraSet(198);

				idBanco = Integer.parseInt(grid.get(0).get("idBanco"));

				if (grid.get(0).get("nomArchivo").substring(2, 3).equals("1")){
					mismoBanco = true;
					if (bTraspaso && idBanco == ConstantesSet.BANAMEX)
						if (grid.get(0).get("bBanamexInversion").equals("true"))
							tipoTransferencia = 2;
						else
							tipoTransferencia = 3;
					else
						tipoTransferencia = 2;

				}
				else if(grid.get(0).get("nomArchivo").substring(3, 4).equals("3")){
					tipoTransferencia = 3;
					mismoBanco = false;
					bInternacional = true;
				}
				else {
					tipoTransferencia = 1;
					mismoBanco = false;
					bInterbancario = true;
				}

				//Si se selecciono el radioButton de cheques
				if (grid.get(0).get("optCheque").equals("1")){
					psIndicador = grid.get(0).get("nomArchivo").substring(1, 3);
					if (psIndicador.equals("std"))
						bChequeOcurreSantanderDirecto = true;
				}else{
					psPrefijoMassPayment = controlArchivosTransferidosADao.configuraSet(218);

					if (!psPrefijoMassPayment.equals("")){
						if (grid.get(0).get("nomArchivo").indexOf(psPrefijoMassPayment) > 0){
							bBanamexMassPayment = true;
							psArchivo = grid.get(0).get("nomArchivo").replace(".txt", ".sha");
						}
					}

					bBitalH2H = false;
					psIndicador = grid.get(0).get("nomArchivo").substring(0, 2);

					if (psIndicador.equals("bs")){
						bBitalSpeua = true;
						tipoTransferencia = 3;
					}else if (psIndicador.toUpperCase().equals("BH")){
						bBitalH2H = true;
					}else if (psIndicador.toUpperCase().equals("BT")){
						bBanamexTef = true;
					}else if (psIndicador.toUpperCase().equals("BM")){
						bBanamexMassPayment = true;
					}else if (psIndicador.toUpperCase().equals("CP")){
						bCitybankPaylinkMN = true;
						bArmaBanamexCity = 5;
					}else if (psIndicador.toUpperCase().equals("CD") && grid.get(0).get("idBanco").equals("2")){
						bCitybankFlatFile = true;
						idBanco = ConstantesSet.CITIBANK_DLS;
					}else if (psIndicador.toUpperCase().equals("AZ")){
						idBanco = ConstantesSet.AZTECA;
					}else if (psIndicador.toUpperCase().equals("BN")){
						idBanco = ConstantesSet.BANAMEX;
					}else if (psIndicador.toUpperCase().equals("xx")){
						idBanco = ConstantesSet.BANORTE;
					}
				}
				switch(idBanco) {
			    	case ConstantesSet.BANORTE:
			    		rutaBanco = "Banorte";
	    		   		break;
			   		case ConstantesSet.AZTECA:
			   			rutaBanco = controlArchivosTransferidosADao.configuraSet(ConstantesSet.NOMBRE_CARPETA_AZTECA);		   
	    				break;
			   		case ConstantesSet.SANTANDER:
			   			rutaBanco = controlArchivosTransferidosADao.configuraSet(ConstantesSet.NOMBRE_CARPETA_SANTANDER);
			   			break;
			   		case ConstantesSet.BANAMEX:
			   			if( bBanamexTef) {				//Layout - TEF (Transferencia Electronica de Fondos)
			   				rutaBanco = controlArchivosTransferidosADao.configuraSet(
			   						ConstantesSet.NOMBRE_CARPETA_BANAMEX_TEF).replace('/', File.separatorChar).replace('\\', File.separatorChar);;
			   			}else if(bCitybankFlatFile) {		//Layout - Flat File Citibank (Solo DLS)
			   				rutaBanco = controlArchivosTransferidosADao.configuraSet(ConstantesSet.NOMBRE_CARPETA_BANAMEX_FLT);
			  
			   			}else if(bCitybankPaylinkMN) {		//Layout - Pay Link Citibank (Solo MN)
			   				rutaBanco = controlArchivosTransferidosADao.configuraSet(ConstantesSet.NOMBRE_CARPETA_BANAMEX_PL);
			   			}else {												//Layoutï¿½s Digitem = dtoCriBus.getOptEnvioBNX() == 0
			   				rutaBanco = controlArchivosTransferidosADao.configuraSet(ConstantesSet.NOMBRE_CARPETA_BANAMEX);
			   				;
			   			}
			   			break;
	    			case ConstantesSet.BANCOMER:  
	    				if(grid.get(0).get("idServicioBe") != null && grid.get(0).get("idServicioBe").equals("CIE")) {   
	    					rutaBanco = controlArchivosTransferidosADao.configuraSet(ConstantesSet.NOMBRE_CARPETA_BANCOMER_CIE);
			   
	    				}else if(bInternacional) {
	    					rutaBanco = controlArchivosTransferidosADao.configuraSet(ConstantesSet.NOMBRE_CARPETA_BANCOMER_INTERNACIONAL);
			   
	    				}else {
	    					rutaBanco = controlArchivosTransferidosADao.configuraSet(ConstantesSet.NOMBRE_CARPETA_BANCOMER);
			   
	    				}
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
	    						rutaBanco = controlArchivosTransferidosADao.configuraSet(ConstantesSet.NOMBRE_CARPETA_CITIBANCK);
	    					}
	    				}
	    				break;
	    			
	    			case ConstantesSet.BITAL:
	    			
	    				break;
	    			case ConstantesSet.INVERLAT:
	    				rutaBanco = "Inverlat";
	    				break;
	    		}//Termina switch
				
				if(rutaBanco.equals("")){
					mapRet.put("msgUsuario", "No se encontro la ruta del archivo.");
					return mapRet;
				}
				String rutaDirectorios = rutaEnvio + rutaBanco+File.separatorChar +File.separatorChar;
				rutaEnvio = rutaEnvio + rutaBanco+File.separatorChar +File.separatorChar+ psArchivo;
				
				System.out.println("ruta directorioas"+rutaDirectorios);
				System.out.println("ruta envio"+rutaEnvio);
				
				for (int ii = 0; ii < registroCancelado.size(); ii++)
					importeCancelado += Double.parseDouble(registroCancelado.get(ii).get("importe"));
				
				System.out.println("Ruta envio:"+rutaEnvio);
				System.out.println("importe cancelado: "+importeCancelado);
				//Actualiza arch_transfer con el nuevo importe y numero de registros activos
				controlArchivosTransferidosADao.actualizaArchTransfer(importeCancelado, registroTotalParam, grid.get(0).get("fecHoy"), 
						 Integer.parseInt(grid.get(0).get("usuario")), psArchivo.substring(0, psArchivo.length() - 4));


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
							//Se manda llamar el revividor
							mapRet = controlArchivosTransferidosADao.ejecutarRevividor(grid.get(i),true);
							System.out.println("resultado del revividor:"+mapRet);
							if (mapRet.size() != 0)
								mensaje = "Error en el revividor";
							else
								mensaje = "Registro cancelado con exito";
							System.out.println("mensaje"+mensaje);

						}
						else{ //Si no es traspaso esto aplica para las transfer
							psEstatusMov = "N";
							if (bBanamexMassPayment){
								if (registroCancelado.get(i).get("idTipoOperacion").equals("3000"))
									psEstatusMov = "M";
								else if(registroCancelado.get(i).get("idTipoOperacion").equals("3000") && 
										registroCancelado.get(i).get("idBancoBenef").equals("0") && idBanco == ConstantesSet.NAFIN){
									psEstatusMov = "N";
								}
							}
							recibeRespuesta = controlArchivosTransferidosADao.actualizaEstatusAgrupapdo(registroCancelado.get(i).get("poHeaders"),
											Integer.parseInt(registroCancelado.get(i).get("idBanco")),
											registroCancelado.get(i).get("idChequera"),
											Integer.parseInt(registroCancelado.get(i).get("noEmpresa")),
											registroCancelado.get(i).get("fecOperacion"),psEstatusMov);
						}

						controlArchivosTransferidosADao.actualizaDetArchTransferAgrupado(psArchivo.substring(0, psArchivo.length() - 4), 
								registroCancelado.get(i).get("poHeaders"),
								Integer.parseInt(registroCancelado.get(i).get("idBanco")),
								registroCancelado.get(i).get("idChequera"),
								Integer.parseInt(registroCancelado.get(i).get("noEmpresa")),
								registroCancelado.get(i).get("fecOperacion"));

						//psFolios = psFolios + registroCancelado.get(i).get("noFolioDet") + ",";
					}

					if (recibeRespuesta > 0)
						mapRet.put("msgUsuario", "Se cancelaron los registros con exito");
				}
				
				// se toman solo los movimiento que no estan saleccionados
				for(int m = 0; m < grid.size(); m++){
					for(int j=0; j <registroCancelado.size();j++){
						if(grid.get(m).get("poHeaders").equals(registroCancelado.get(j).get("poHeaders")) &&
								grid.get(m).get("idBanco").equals(registroCancelado.get(j).get("idBanco")) &&
								grid.get(m).get("idChequera").equals(registroCancelado.get(j).get("idChequera")) &&
								grid.get(m).get("noEmpresa").equals(registroCancelado.get(j).get("noEmpresa"))
								){
							grid.remove(m);
						}
					}
				}
				// fin  de - se toman solo los movimiento que no estan saleccionados
				List<MovimientoDto> lista = new ArrayList<MovimientoDto>();
				if (grid != null && grid.size() != 0){ 
					
					lista = generarNuevosPagos(grid);
												
					for(int i = 0; i< lista.size(); i ++){	
							
						switch (idBanco){
							
							case ConstantesSet.AZTECA:	//ok
		                   			psRegistro = layoutAztecaBusiness.beArmaAztecaAgrupado(lista.get(i), i, mismoBanco, bInternacional);
		                   			if(psRegistro!=null && !psRegistro.equals("")){
		                   				mapRet = creacionArchivosBusiness.escribirArchivoLayout(rutaDinamica,rutaBanco+File.separatorChar,
		                   						psArchivo,psRegistro);
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
                   			
							case ConstantesSet.BANCOMER:
                    			if(lista.get(i).getIdServicioBe().equals("CIE") && mismoBanco){
                    				psRegistro = layoutBancomerBusiness.beArmaConvenioCIE(
                    						obtenerParametrosLayout(lista, i, mismoBanco));//Arma la cadena
                					
                       				if(psRegistro != null && !psRegistro.equals("")) 
                       					mapRet.put("msgRegistro", psRegistro);
                				}else{
                					if(bInternacional && lista.get(i).getIdDivisa().equals("DLS")){
            							psRegistro = layoutBancomerBusiness.beArmaBancomerInternacionalAgrupado
            									(obtenerParametrosLayout(lista, i, mismoBanco),lista.get(i));//Arma la cadena
                    					
                           				if(psRegistro != null && !psRegistro.equals("")) 
                           					mapRet.put("msgRegistro", psRegistro);
                					}else{
                    					psRegistro = layoutBancomerBusiness.beArmaBancomerC43Agrupado(obtenerParametrosLayout(lista, i, mismoBanco));
                    					
                           				if(psRegistro != null && !psRegistro.equals("")) 
                           					mapRet.put("msgRegistro", psRegistro);
                					}
                				}
                    			if(psRegistro!=null && !psRegistro.equals("")){
	                   				mapRet = creacionArchivosBusiness.escribirArchivoLayout(rutaDinamica,rutaBanco+File.separatorChar,
	                   						psArchivo,psRegistro);
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
                    		
							case ConstantesSet.BANORTE:
	                   			psRegistro = layoutBanorteBusiness.beArmaBanorteAgrupados(lista.get(i), i, mismoBanco, bInternacional);
	                   			if(psRegistro!=null && !psRegistro.equals("")){
	                   				mapRet = creacionArchivosBusiness.escribirArchivoLayout(rutaDinamica,rutaBanco+File.separatorChar,
	        								psArchivo,psRegistro);
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
                   			
							case ConstantesSet.BANAMEX:
								System.out.println("valor de la bandera de tefff"+bBanamexTef);
								if(bBanamexTef){
									rutaBanco = controlArchivosTransferidosADao.configuraSet(ConstantesSet.NOMBRE_CARPETA_BANAMEX_TEF)+
											File.separatorChar;
									rutaBanco = rutaBanco.replace('/', File.separatorChar).replace('\\', File.separatorChar);
									
					   				mapRet = layoutBancomerBusiness.beArmaBanamexTEFAgrupado(mismoBanco == false ? true : false, lista,
					   						true, "", i, rutaDinamica, rutaBanco,psArchivo);
									return mapRet;
								}
	                    		if(mismoBanco) {
	                    			mapRet = layoutBancomerBusiness.beExportacionAgrupado(lista, i, tipoTransferencia,
	                    								psArchivo, rutaDinamica, 
	                    								idBanco, "TRANSFERENCIA");
	                    			
	                    		}else {
	                    			psRegistro = layoutBancomerBusiness.beArmaBanamexInter(lista, i);//dijitem
	                    			
	                    			if(psRegistro != null && !psRegistro.equals("")) 
	                    				mapRet.put("msgRegistro", psRegistro);
	                    			else
	                    				mapRet.put("msgUsuario", "Error al armar Banamex Interbancario");
	                    		}
	                    		
                    		break;
	                    		
							case ConstantesSet.CITIBANK_DLS:
	                   			if(bArmaBanamexCity == 5 && bCitybankPaylinkMN) {			//Opcion de Citibank PayLink MN
	                   				psRegistro = layoutBancomerBusiness.beArmaCitibankPaylinkMNAgrupado(lista, i, "");
	                    			
	                   				if(psRegistro != null && !psRegistro.equals("")) 
	                    				mapRet.put("msgRegistro", psRegistro);
	                    			else
	                    				mapRet.put("msgUsuario", "No se encontró el folio <folio_paylink>!!");
	                   			}else {
	                   				if(bArmaBanamexCity == 2)			//Opcion de Citibank WorldLink
	                   					tipoTransferencia = 5;
	                   				else if(bArmaBanamexCity == 6)	//Opcion de Citibank ACH
	                   					tipoTransferencia = 4;
	                   				else if(tipoTransferencia == 1) {			//Opcion de Interbancaria
	                   					/*if(listBus.get(i).getTipoEnvioLayout() != 0)
	                   						piTipoTrasferencia = listBus.get(i).getTipoEnvioLayout();
	                   					else if(!listBus.get(i).getNomBancoIntermediario().trim().equals(""))
	                   						piTipoTrasferencia = 3;
	                   					else*/
	                   						tipoTransferencia = 1;
	                   				}
	                   				mapRet = layoutBancomerBusiness.beExportacion(lista, i, tipoTransferencia,
	                   									psArchivo, rutaDinamica, idBanco, "TRANSFERENCIA");
	                   			}
	                   		break;
	                   			
	                   		case ConstantesSet.SANTANDER:
	                   			
	                   			mapRet = layoutBancomerBusiness.beExportacionAgrupado(lista, i, tipoTransferencia,
        								psArchivo, rutaDinamica, 
        								idBanco, "TRANSFERENCIA");
							break;
	                    		
	                    		
                    	
						}//switch
						
						if (!beArmaNuevos) {
							if(mapRet.get("msgRegistro") != null && !mapRet.get("msgRegistro").equals("")) {
			    				mapRet = creacionArchivosBusiness.escribirArchivoLayout(rutaDinamica,rutaBanco+File.separatorChar,
			    						psArchivo, mapRet.get("msgRegistro").toString());
			    				System.out.println("fin del for escribir archivo");
			    				if(!(Boolean)mapRet.get("resExito")) {
		        					mapRet.put("msgUsuario", "Error al Generar Archivo " + mapCreaArch.get("psBanco").toString());
									return mapRet;
								}
			    				
							}else {
								mapRet.put("msgUsuario", mapRet.get("msgUsuario"));
								return mapRet;
							}
						}
						System.out.println("fin del for");
					}// for
				}else{
					psArchivo = registroCancelado.get(0).get("nomArchivo");
					for(int i = 0; i < registroCancelado.size(); i++){
						//Actualiza arch_transfer con el nuevo importe y numero de registros activos
						importeCancelado = Double.parseDouble(registroCancelado.get(i).get("importe"));
						controlArchivosTransferidosADao.actualizaArchCancelado(importeCancelado, 1, psArchivo.substring(0, psArchivo.length() - 4));
						//areglar en un momenot
						controlArchivosTransferidosADao.actualizaDetArchTransferAgrupado(psArchivo.substring(0, psArchivo.length() - 4), 
								registroCancelado.get(i).get("poHeaders"),
								Integer.parseInt(registroCancelado.get(i).get("idBanco")),
								registroCancelado.get(i).get("idChequera"),
								Integer.parseInt(registroCancelado.get(i).get("noEmpresa")),
								registroCancelado.get(i).get("fecOperacion"));

						controlArchivosTransferidosADao.actualizaEstatusAgrupapdo(registroCancelado.get(i).get("poHeaders"),
								Integer.parseInt(registroCancelado.get(i).get("idBanco")),
								registroCancelado.get(i).get("idChequera"),
								Integer.parseInt(registroCancelado.get(i).get("noEmpresa")),
								registroCancelado.get(i).get("fecOperacion"),
								"N");
						
					}
					mapRet.put("msgUsuario", "Registros procesados con exito ");
					mapRet.put("estatusAv", false);
		            System.out.println("fin de los registros cancelados.");
		            return mapRet;
				}
					
					
			}else{
				psArchivo = registroCancelado.get(0).get("nomArchivo");
				for(int i = 0; i < registroCancelado.size(); i++){
					//Actualiza arch_transfer con el nuevo importe y numero de registros activos
					importeCancelado = Double.parseDouble(registroCancelado.get(i).get("importe"));
					controlArchivosTransferidosADao.actualizaArchCancelado(importeCancelado, 1, psArchivo.substring(0, psArchivo.length() - 4));
					//areglar en un momenot
					controlArchivosTransferidosADao.actualizaDetArchTransferAgrupado(psArchivo.substring(0, psArchivo.length() - 4), 
							registroCancelado.get(i).get("poHeaders"),
							Integer.parseInt(registroCancelado.get(i).get("idBanco")),
							registroCancelado.get(i).get("idChequera"),
							Integer.parseInt(registroCancelado.get(i).get("noEmpresa")),
							registroCancelado.get(i).get("fecOperacion"));

					controlArchivosTransferidosADao.actualizaEstatusAgrupapdo(registroCancelado.get(i).get("poHeaders"),
							Integer.parseInt(registroCancelado.get(i).get("idBanco")),
							registroCancelado.get(i).get("idChequera"),
							Integer.parseInt(registroCancelado.get(i).get("noEmpresa")),
							registroCancelado.get(i).get("fecOperacion"),
							"N");
					
				}
			}

			mapRet.put("msgUsuario", "Registros procesados con exito " + psArchivo);
			mapRet.put("estatusAv", true);
            mapRet.put("nombreArchivo", psArchivo);
            System.out.println("fin del ciclo ");
            return mapRet;

		}catch(Exception e){
			System.out.println("error en el business regenerar");
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosBusiness, M: regenerar");
		}
		return mapRet; 
	}


	private List<MovimientoDto> generarNuevosPagos(List<Map<String, String>> objParams) {
		List<MovimientoDto> lista = new ArrayList<MovimientoDto>();
		try {
			int idBanco = objParams.get(0).get("idBanco")!=null?Integer.parseInt(objParams.get(0).get("idBanco")):0;
			for(int i = 0; i < objParams.size(); i++){
				
				MovimientoDto dto = new MovimientoDto();
				dto.setNoDocto(objParams.get(i).get("noDocto")!=null?objParams.get(i).get("noDocto"):"");
				if(objParams.get(i).get("fecOperacion")!=null)
					dto.setFecOperacion(funciones.ponerFechaDate(objParams.get(i).get("fecOperacion")));
				dto.setBeneficiario(objParams.get(i).get("beneficiario"));
				dto.setImporte(objParams.get(i).get("importe")!=null?Double.parseDouble(objParams.get(i).get("importe")):0);
				dto.setImporteStr(objParams.get(i).get("importe")!=null?objParams.get(i).get("importe"):"0");
				dto.setIdDivisa(objParams.get(i).get("idDivisa"));
				dto.setNombreBancoBenef(objParams.get(i).get("nombreBancoBenef"));
				dto.setIdChequeraBenef(objParams.get(i).get("idChequeraBenef"));
				dto.setIdChequeraBenefReal(objParams.get(i).get("idChequeraBenefReal"));
				dto.setConcepto(objParams.get(i).get("concepto"));
				//dto.setNoFolioDet(objParams.get(i).get("noFolioDet")!=null?Integer.parseInt(objParams.get(i).get("noFolioDet")):0);	 
				dto.setNoEmpresa(objParams.get(i).get("noEmpresa")!=null?Integer.parseInt(objParams.get(i).get("noEmpresa")):0);
				dto.setOrigenMov(objParams.get(i).get("origenMov"));
				dto.setPoHeaders(objParams.get(i).get("poHeaders") != null ?  objParams.get(i).get("poHeaders"):"");
				dto.setDivision(objParams.get(i).get("division"));
				dto.setNoCliente(objParams.get(i).get("noCliente"));
				dto.setEspeciales(objParams.get(i).get("especiales"));	 
				dto.setComplemento(objParams.get(i).get("complemento"));
				dto.setClave(objParams.get(i).get("clave"));
				dto.setNomEmpresa(objParams.get(i).get("nomEmpresa"));
				dto.setIdBancoBenef(objParams.get(i).get("idBancoBenef")!=null?Integer.parseInt(objParams.get(i).get("idBancoBenef")):0);
				dto.setPlazaBenef(objParams.get(i).get("plazaBenef"));
				dto.setPlaza(objParams.get(i).get("plaza")!=null?Integer.parseInt(objParams.get(i).get("plaza")):0);
				dto.setSucDestino(objParams.get(i).get("sucDestino") != null ?  objParams.get(i).get("sucDestino"):"");
				dto.setClabeBenef(objParams.get(i).get("clabeBenef") != null ?objParams.get(i).get("clabeBenef"):"");
				dto.setBLayoutComerica(objParams.get(i).get("bLayoutComerica") != null ? objParams.get(i).get("bLayoutComerica") : "");
				dto.setIdChequera(objParams.get(i).get("idChequera"));
				//dto.setConcepto(objParams.get(i).get("concepto") != null ? objParams.get(i).get("sucDestino"):"");
				dto.setConcepto(objParams.get(i).get("concepto") != null ? objParams.get(i).get("concepto"):"");
				dto.setFecValor(funciones.ponerFechaDate(objParams.get(i).get("fecValor").toString()));
				dto.setIdBanco(objParams.get(i).get("idBanco")!=null?Integer.parseInt(objParams.get(i).get("idBanco")):0);
				dto.setInstFinan(objParams.get(i).get("instFinan"));
				dto.setSucOrigen(objParams.get(i).get("sucOrigen"));
				dto.setRfcBenef(objParams.get(i).get("rfcBenef") != null ? objParams.get(i).get("rfcBenef"):"");
				dto.setRfc(objParams.get(i).get("rfc") != null ?objParams.get(i).get("rfc"):"");
				dto.setHoraRecibo(objParams.get(i).get("horaRecibo"));
				dto.setIdContratoMass(objParams.get(i).get("idContratoMass") != null ? Integer.parseInt(objParams.get(i).get("idContratoMass")) : 0);
				dto.setTipoEnvioLayout(objParams.get(i).get("tipoEnvioLayout") != null ? Integer.parseInt(objParams.get(i).get("tipoEnvioLayout")) : 0);
				dto.setNomBancoIntermediario(objParams.get(i).get("nomBancoIntermediario") != null ? objParams.get(i).get("nomBancoIntermediario") : "");
				dto.setDescBancoBenef(objParams.get(i).get("descBancoBenef") != null ? objParams.get(i).get("descBancoBenef") : "");
				dto.setDireccionBenef(objParams.get(i).get("direccionBenef") != null ? objParams.get(i).get("direccionBenef") : "");
				
				if (idBanco == 12) {
					dto.setAba(objParams.get(i).get("aba") != null && !objParams.get(i).get("aba").equals("") ?
							objParams.get(i).get("aba").trim():"");
					dto.setSwiftCode(objParams.get(i).get("swiftCode") != null && !objParams.get(i).get("swiftCode").equals("") ?
							objParams.get(i).get("swiftCode").trim():"");
				} else {
					if(objParams.get(i).get("aba") != null && !objParams.get(i).get("aba").equals("") ) {
						dto.setAba("FW");
						dto.setSwiftCode(objParams.get(i).get("aba").trim());
					}else if( objParams.get(i).get("swiftCode") != null && !objParams.get(i).get("swiftCode").equals("")) {
						dto.setAba("IS");
						dto.setSwiftCode(objParams.get(i).get("swiftCode").trim());
					}
				}
					
				if(objParams.get(i).get("abaIntermediario") != null && !objParams.get(i).get("abaIntermediario").equals("")) {
					dto.setAbaIntermediario("FW");
					dto.setSwiftIntermediario(objParams.get(i).get("abaIntermediario").trim());
				}else if( objParams.get(i).get("swiftIntermediario") != null && !objParams.get(i).get("swiftIntermediario").equals("") ) {
					dto.setAbaIntermediario("IS");
					dto.setSwiftIntermediario(objParams.get(i).get("swiftIntermediario").trim());
				}
				if( objParams.get(i).get("abaCorresponsal") != null && !objParams.get(i).get("abaCorresponsal").equals("") ) {
					dto.setAbaIntermediario("//FW=" + objParams.get(i).get("abaCorresponsal").trim());
					dto.setSwiftIntermediario("");
				}else if(objParams.get(i).get("swiftCorresponsal") != null && !objParams.get(i).get("swiftCorresponsal").equals("")) {
					dto.setAbaIntermediario("IS" + objParams.get(i).get("swiftCorresponsal").trim());
					dto.setSwiftIntermediario("");
				}
				dto.setNomBancoCorresponsal(objParams.get(i).get("nomBancoCorresponsal") != null ? objParams.get(i).get("nomBancoCorresponsal") : "");
				dto.setDescUsuarioBital(objParams.get(i).get("descUsuarioBital") != null ? objParams.get(i).get("descUsuarioBital") : "");
				dto.setDescServicioBital(objParams.get(i).get("descServicioBital") != null ? objParams.get(i).get("descServicioBital") : "");
				dto.setIdServicioBe(objParams.get(i).get("swiftIntermediario") != null ? objParams.get(i).get("swiftIntermediario"):"");
				lista.add(dto);
			}

		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:BancaElectronica, C:EnvioTransferenciasBusiness, M:generarNuevosPagos");
		}
		return lista;
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
			paramLayDto.setPoHeaders(list.get(i).getPoHeaders());
			paramLayDto.setNoEmpresa(list.get(i).getNoEmpresa());

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
	public LayoutAztecaBusiness getLayoutAztecaBusiness() {
		return layoutAztecaBusiness;
	}

	public void setLayoutAztecaBusiness(LayoutAztecaBusiness layoutAztecaBusiness) {
		this.layoutAztecaBusiness = layoutAztecaBusiness;
	}
	public LayoutBancomerBusiness getLayoutBancomerBusiness() {
		return layoutBancomerBusiness;
	}

	public void setLayoutBancomerBusiness(LayoutBancomerBusiness layoutBancomerBusiness) {
		this.layoutBancomerBusiness = layoutBancomerBusiness;
	}
	
	public ControlArchivosTransferidosADao getControlArchivosTransferidosADao() {
		return controlArchivosTransferidosADao;
	}

	public void setControlArchivosTransferidosADao(ControlArchivosTransferidosADao controlArchivosTransferidosADao) {
		this.controlArchivosTransferidosADao = controlArchivosTransferidosADao;
	}

	public CreacionArchivosBusiness getCreacionArchivosBusiness() {
		return creacionArchivosBusiness;
	}

	public void setCreacionArchivosBusiness(CreacionArchivosBusiness creacionArchivosBusiness) {
		this.creacionArchivosBusiness = creacionArchivosBusiness;
	}

	public LayoutScotiabankInverlatBusiness getLayoutScotiabankInverlatBusiness() {
		return layoutScotiabankInverlatBusiness;
	}

	public void setLayoutScotiabankInverlatBusiness(LayoutScotiabankInverlatBusiness layoutScotiabankInverlatBusiness) {
		this.layoutScotiabankInverlatBusiness = layoutScotiabankInverlatBusiness;
	}

	public LayoutBanorteBusiness getLayoutBanorteBusiness() {
		return layoutBanorteBusiness;
	}

	public void setLayoutBanorteBusiness(LayoutBanorteBusiness layoutBanorteBusiness) {
		this.layoutBanorteBusiness = layoutBanorteBusiness;
	}
	
}

