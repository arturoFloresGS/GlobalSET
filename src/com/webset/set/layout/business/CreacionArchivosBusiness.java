/**
 *@author Cristian Garcia Garcia
 *@since 01/April/2011
 */
package com.webset.set.layout.business;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.webset.set.bancaelectronica.dao.EnvioTransferenciasDao;
import com.webset.set.layout.dao.LayoutsDao;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.Funciones;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

public class CreacionArchivosBusiness {
	//Atributos de la clase
	Funciones funciones = new Funciones();
	private SmbFile creaArchivo = null;
	Bitacora bitacora = new Bitacora();
	private LayoutsDao layoutsDao;
	private CreacionArchivosBusinessLocal creacionArchivosBusinessLocal = new CreacionArchivosBusinessLocal();
	private EnvioTransferenciasDao envioTransferenciasDao;
	private Logger logger = Logger.getLogger(CreacionArchivosBusiness.class);
	
	/**
	 * crearDirectorioLayOut
	 * @param dirPrincipal
	 * @param subCarpetas
	 * @return
	 */
	public Map<String, Object> crearDirectorioLayOut(String dirPrincipal,String subCarpetas){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("resExito", false);
		result.put("msgUsuario", "Error desconocido.");
		try {
			if(subCarpetas.equals(""))
				creaArchivo = new SmbFile(dirPrincipal);
			else
				creaArchivo = new SmbFile(dirPrincipal+subCarpetas);
			if(creaArchivo.isDirectory())
				result.put("resExito", true);
			else{
				creaArchivo.mkdirs();
				result.put("resExito", true);
			}
			
		} catch (MalformedURLException e) {
			result.put("msgUsuario", "Error en la direccion destino.");
			result.put("resExito", false);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:com.webset.set.business, C:CreacionArchivosBusiness, M:crearDirectorioLayOut" +"Error en la URL destino." );
		}catch (SmbException e) {
			result.put("resExito", false);
			result.put("msgUsuario", "Error al tratar de crear el archivo.");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:com.webset.set.business, C:CreacionArchivosBusiness, M:crearDirectorioLayOut" +"No se creo el Directorio de la Bitacora " );
		}
		return result;
	}
	//revisado A.A.G 3
	public SmbFile crearArchivoLayOut(String dirPrincipal,String subCarpetas,String nombreArchivo){
		try {
		if(subCarpetas.equals(""))
				creaArchivo = new SmbFile(dirPrincipal + nombreArchivo);
			else
				creaArchivo = new SmbFile(dirPrincipal +subCarpetas+ nombreArchivo);
			
			if(creaArchivo.isFile())
				return creaArchivo;
			else{ 
				creaArchivo.createNewFile();
				return creaArchivo;
			}
		} catch (MalformedURLException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:com.webset.set.business, C:CreacionArchivosBusiness, M:crearArchivoLayOut" +"No se creo el Directorio de la Bitacora " );
			return null;
		}catch (SmbException e) {
			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:com.webset.set.business, C:CreacionArchivosBusiness, M:crearArchivoLayOut" +"No se creo el Directorio de la Bitacora " );
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	//revisado A.A.G 4
	
	public Map<String, Object> insertarRegistro(String dirPrincipal,String subCarpetas,String nomArch, String arg){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("resExito", false);
		result.put("msgUsuario", "Error desconocido.");
		result= crearDirectorioLayOut(dirPrincipal,subCarpetas); 
		if((Boolean)result.get("resExito")){
		//if(crearDirectorioLayOut(dirPrincipal,subCarpetas)){
			SmbFile fichero = crearArchivoLayOut(dirPrincipal,subCarpetas,nomArch);
			if(fichero!=null){
				try {
					SmbFileOutputStream out = new SmbFileOutputStream(fichero, true);
					out.write((arg+"\r\n").getBytes());
					out.close();
				}catch (FileNotFoundException e) {
					result.put("msgUsuario", "Error al tratar de encontrar el archivo.");
					bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:com.webset.set.business, C:CreacionArchivosBusiness, M:insertarRegistro" +"No se encuentra el Archivo");
				}catch (IOException e) {
					result.put("msgUsuario", "Error al tratar al insertar registros.");
					bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:com.webset.set.business, C:CreacionArchivosBusiness, M:insertarRegistro" +"Error al escribir en la Bitacora");
				}
			}
			else{
				result.put("msgUsuario", "No se pudo crear el Archivo.");
				bitacora.insertarRegistro(new Date().toString() + " "
				+"P:com.webset.set.business, C:CreacionArchivosBusiness, M:insertarRegistro" +"No se pudo crear el Archivo");
			}
		}
		else{
			bitacora.insertarRegistro(new Date().toString() + " " 
			+"P:com.webset.set.business, C:CreacionArchivosBusiness, M:insertarRegistro" +"Error al obtener el directorio");
		}
		return result;
	}
	//revisado A.A.G 1
	public Map<String, Object> escribirArchivoLayout(String dirPrincipal,String subCarpetas, String archivo, String datos){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("resExito", false);
		result.put("msgUsuario", "Error desconocido000.");
		try{
		System.out.println("escribirArchivo "+archivo);
			result.put("resExito", true);//quitar para cuando se habilite lo de cliente smb.
			
				boolean b = creacionArchivosBusinessLocal.crearDirectorioLayOut(dirPrincipal, subCarpetas);
				
					if (b) {
					System.out.println("crear archivoLayout "+datos);
						File archivoLocal = creacionArchivosBusinessLocal.crearArchivoLayOut(dirPrincipal,subCarpetas, archivo);
						if (archivoLocal != null) {
							creacionArchivosBusinessLocal.insertarRegistro(dirPrincipal, subCarpetas, archivo, datos);
							result.put("resExitoLocal", true);
							result.put("resRutaLocal", dirPrincipal+subCarpetas);
							result.put("resNombreArchivoLocal", archivo);
						} else {
							result.put("resExitoLocal", false);
						}
//					
//				}
			}else{
				return result;
			}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " 
			+"P:com.webset.set.business, C:CreacionArchivosBusiness, M:crearArchivoLayout" );
			result.put("msgUsuario", "Error al al escribir en el archivo.");
		}
		return result;
	}
	public boolean eliminarArchivoLayout(String dirPrincipal,String subCarpetas, String archivo){
		try{
			dirPrincipal+=subCarpetas;
			File borrar = new File(dirPrincipal+archivo);
			borrar.delete();
			return true;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " 
			+"P:com.webset.set.bu"
			+ "siness, C:CreacionArchivosBusiness, M:eliminarArchivoLayout" );
			return false;
		}
	}
	
	
	public void eliminarArchivo(String dirPrincipal,String subCarpetas, String archivo){
		try{
			dirPrincipal+=subCarpetas;
			File borrar = new File(dirPrincipal+archivo);
			borrar.delete();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " 
			+"P:com.webset.set.bu"
			+ "siness, C:CreacionArchivosBusiness, M:eliminarArchivoLayout" );
		}
	}
	
	//revisado A.A.G
	public Map<String,Object> generarNombreArchivo(String psBanco, String nomFolioBanco, String psIndiceBanco, /*String psArchivo,*/
												boolean optMismo, boolean optInternacional, boolean chkH2H, int noEmpresa, int h2hBBVA){
		Map<String,Object> mapRet = new HashMap<String,Object>();
		
		String contrato = "bc2";
		String folioCadena = "";
		System.out.println("Estamos adentro del metodo generaNomArch que nombre se genera angel");
		
		
		
		System.out.println(psBanco); 
				System.out.println(nomFolioBanco);
						System.out.println(psIndiceBanco);
								System.out.println(optMismo);
										System.out.println(optInternacional);
								//				System.out.println("chk2h generra nombreArc"+chkH2H );
														System.out.println(noEmpresa);
														System.out.println("///fin de los datos GenerarNomArch Angel");
														
		
		
		try {
			int folioBanco = 0;
			String psNomArchivo = "";
			//aqui agregar un folio real esto es de prueba para la version Web AgelIsrael
			if (psBanco.toUpperCase().equals("BANCOMER") && h2hBBVA==1){
				nomFolioBanco = "arch_bancomer_H2H";
			}
			
			layoutsDao.actualizarFolioReal(nomFolioBanco);
			folioBanco = layoutsDao.seleccionarFolioReal(nomFolioBanco);
	        
			if(folioBanco < 0) {
	        	mapRet.put("msgUsuario","No se encontro el folio para " + psBanco);
	        	return mapRet;
	        }
			if (psBanco != null && psBanco.toUpperCase().equals("BANCOMER") && h2hBBVA==1)
			{	        	
		        	folioCadena = funciones.ajustarLongitudCampo(Integer.toString(folioBanco), 7, "D", "", "0");	        	
		        	//Por el momento esta fijo el contrato esto lo define el Banco
		        	//psNomArchivo = noEmpresa + contrato + "." + folioCadena + ".txt"; //ESTABA ANTES
		        	psNomArchivo =  contrato + folioCadena + ".txt";//CORREGIDO ISRAEL
		            //psBanco= "\\" + psBanco + "\\";
		    }else if(psBanco.trim().indexOf("MassPayment") > 0) {
		    	psNomArchivo = psIndiceBanco + funciones.ajustarLongitudCampo("" + folioBanco, 8 - psIndiceBanco.length(), "D", "", "0") + ".sha";
		    }else if(psBanco!=null && psBanco.toUpperCase().equals("NAFIN"))
	        {
	            psNomArchivo = psIndiceBanco + "3" + funciones.ponerCeros(""+folioBanco,5)+ ".txt";
	            //psArchivo = psArchivo + "\\" + psBanco + "\\" + psNomArchivo;
	            //psBanco= "/" + psBanco + "/";
	            psBanco= System.getProperty("file.separator") + psBanco + System.getProperty("file.separator");
	        
	        }else if(psBanco != null && psBanco.toUpperCase().equals("SANTANDER_H2H") && nomFolioBanco.equals("arch_santander_h2h")) { //CASO DE H2H SANTANDER
		        psNomArchivo = psIndiceBanco + funciones.cambiarFecha(layoutsDao.fechaHoy(), true).replaceAll("[-|/]", "") + funciones.ponerCeros("" + (envioTransferenciasDao.consecutivoContrato(14, 9999)), 2) + "98_" + envioTransferenciasDao.numeroContrato(14, 9999) + ".in";
		        //psBanco= "/" + psBanco + "/";
		        psBanco= System.getProperty("file.separator") + psBanco + System.getProperty("file.separator");
	        } else {
	        	if(optMismo)
	        	{
	                psNomArchivo = psIndiceBanco + "1" + funciones.ponerCeros(""+folioBanco,5) + ".txt";
	                //psArchivo = psArchivo + "\\" + psBanco + "\\" + psNomArchivo;
	                //psBanco= "/" + psBanco + "/";
	                psBanco= System.getProperty("file.separator") + psBanco + System.getProperty("file.separator");
	        	}
	            else if(optInternacional)
	            {
	                psNomArchivo = psIndiceBanco + "3" +funciones.ponerCeros(""+folioBanco,5) + ".txt";
	                //psArchivo = psArchivo + "\\" + psBanco + "\\" + psNomArchivo;
	               // psBanco= "/" + psBanco + "/";
	                psBanco= System.getProperty("file.separator") + psBanco + System.getProperty("file.separator");
	            }
	            else
	            {
	            	psNomArchivo = psIndiceBanco + "2" + funciones.ponerCeros(""+folioBanco,5) + ".txt";
	               // psArchivo = psArchivo + "\\" + psBanco + "\\" + psNomArchivo;
	            	//psBanco= "/" + psBanco + "/";  System.getProperty("file.separator");
	            	psBanco= System.getProperty("file.separator") + psBanco + System.getProperty("file.separator");
	            }
	        }
		//	System.out.println("nombre archivo generado "+psNomArchivo);
	        mapRet.put("nomArchivo",psNomArchivo);
	        mapRet.put("psBanco",psBanco);
		}
		catch(Exception e){
			e.printStackTrace();
			logger.error(e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:com.webset.set.utilerias.bancaelectronica, C:FuncionesBe, M:generarNombreArchivo");
		}
		 return mapRet;
	}
	
	public Map<String,Object> generarNombreArcivoBanco(int idBanco, boolean bIsMismoBanco, 
			boolean bisInternacional, boolean bH2H, int optEnvioBNMX, int h2hAfrd){
		Map<String,Object> result = new HashMap<String, Object>();
		result.put("estatus", true);
		String sDescBanco = "";
		String sNomFoliador = "";
		String sIndiceArch = "";
		
		try {
			switch(idBanco){
				case ConstantesSet.BAJIO:
					sDescBanco = "bajio";
		        	sNomFoliador = "arch_bajio";
		        	sIndiceArch = "bj";
	        	break;
	        	
		        case ConstantesSet.AZTECA:
		        	sDescBanco = layoutsDao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_AZTECA);
		        	sNomFoliador = "arch_azteca";
		        	sIndiceArch = "az";
		        break;
	        
		        case ConstantesSet.BITAL:
					if(bIsMismoBanco) {
						sDescBanco = "BitalH2HMB";
						sNomFoliador = "arch_bital_h2h";
			        	sIndiceArch = "bh";
					}else{
						sDescBanco = "BitalH2HBI";
						sNomFoliador = "arch_bital_h2h";
			        	sIndiceArch = "bh";
					}               	
				break;
				
		        case ConstantesSet.SANTANDER:     
		        	if(bH2H) {
		        		sDescBanco = "santander_H2H";
						sNomFoliador = "arch_santander_h2h";
			        	sIndiceArch = "tran";
		        	}else {
		        		sDescBanco = "santander";
						sNomFoliador = "arch_santander";
			        	sIndiceArch = "st";
		        	}  
		    	break;
		    	
		        case ConstantesSet.BANAMEX:
					if((optEnvioBNMX==5)&&(h2hAfrd==1)){
						sDescBanco = layoutsDao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_BANAMEX_PL);
						sNomFoliador = "arch_citi_paylink";
						sIndiceArch = "PA";
					}
					else if(optEnvioBNMX == 4) {		//Layout - Flat File Citibank (Solo DLS)
						sDescBanco = layoutsDao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_BANAMEX_FLT);
						sNomFoliador = "arch_citibank_dls";
						sIndiceArch = "cd";
					}
					else if((optEnvioBNMX == 5)&&(h2hAfrd==2)) {		//Layout - Pay Link Citibank (Solo MN)
						sDescBanco = layoutsDao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_BANAMEX_PL);
						sNomFoliador = "arch_citi_paylink";
						sIndiceArch = "cp";
					}
					else {												//Layouts Digitem = dtoCriBus.getOptEnvioBNX() == 0
						sDescBanco = layoutsDao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_BANAMEX);
						sNomFoliador = "arch_banamex";
						sIndiceArch = "bn";
					}
		        break;
		        
		        case ConstantesSet.BANCOMER:
		            if(bIsMismoBanco){
		            	sDescBanco = "bancomer";
						sNomFoliador = "arch_bancomer";
			        	sIndiceArch = "bc";
		            }else 
		            	if(bisInternacional){
			            	sDescBanco = "bancomer_internacional";
							sNomFoliador = "arch_bancomer";
				        	sIndiceArch = "bc";
		            	}else{
			            	sDescBanco = "bancomer";
							sNomFoliador = "arch_bancomer";
				        	sIndiceArch = "bc";
		            }
		    	break;
	        
		        case ConstantesSet.CITIBANK_DLS:
					if(optEnvioBNMX == 2) {		
						sDescBanco = layoutsDao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_CITIBANCK)+
								System.getProperty("file.separator") +"WorldLink";
		   				sNomFoliador = "arch_citibank_wl";
		   				sIndiceArch = "wl";
					}else {
						sDescBanco = layoutsDao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_CITIBANCK);
		   				sNomFoliador = "arch_citibank_dls";
		   				sIndiceArch = "cd";
					}
					
		    	break;
		    	
		        case ConstantesSet.INVERLAT:
		        	sDescBanco = layoutsDao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_SCOTIABANK_INVERLAT);
					sNomFoliador = "arch_inverlat";
					sIndiceArch = "in";
		    	break;
		    	
		        case ConstantesSet.BANORTE:
		        	sDescBanco = layoutsDao.consultarConfiguraSet(ConstantesSet.NOMBRE_CARPETA_BANORTE);
		        	sNomFoliador = "arch_banorte";
					sIndiceArch = "bn";
		    	break;
			}
			
			
			result= generarNombreArchivo(sDescBanco, sNomFoliador, sIndiceArch,
        			bIsMismoBanco,bisInternacional, false, 0,1);
		   
			if(result.isEmpty()){
				result.put("estatus", false);
				if(result.get("msgUsuario")!=null){
					result.put("msgUsuario",result.get("msgUsuario"));
					return result;
				}
				else{
					result.put("msgUsuario","Este banco no tiene Layout de Banca Electronica");
					return result;
				}
			}
			else{
				result.put("estatus", true);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			result.put("estatus", false);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Interfaz, C:CreacionNombreArchivo, M:generarNombreArcivoBanco");
		}
		return result;
	}


	public LayoutsDao getLayoutsDao() {
		return layoutsDao;
	}

	public void setLayoutsDao(LayoutsDao layoutsDao) {
		this.layoutsDao = layoutsDao;
	}
	
	
	public StringBuffer creaArchivoFtp(String nomArchivo){	
		eliminarArchivo("C:\\apicifrado", "\\cifradoSalida\\", "sftp" + nomArchivo.replace("tran", "").replace("98_39957304.in", "") + ".dat");
		StringBuffer cadenaArchivo=new StringBuffer();
		//cadenaArchivo.append("@echo off \r\n");
		cadenaArchivo.append(" cd Inbound \r\n");
		cadenaArchivo.append(" put "+nomArchivo+" \r\n");
		cadenaArchivo.append(" quit \r\n");
		//cadenaArchivo.append("sftp -b sftpcmd.dat 089000001199@192.240.110.98 \r\n");
		//cadenaArchivo.append("del sftpcmd.dat \r\n");
		return cadenaArchivo;
	}

	//Proceso H2H para Santander
	public String enviaH2H(String dirPrincipal, String nomArch) {
		String copiaCifrado = "cmd /c copy " + dirPrincipal + nomArch + " C:\\apicifrado\\cifradoEntrada\\" + nomArch;
		if (ejecutaComando(copiaCifrado)) {
			String creaBackup = "cmd /c rename " + dirPrincipal + nomArch + " *.bak";
			if (ejecutaComando(creaBackup)) {
				String cifraArchivo = envioTransferenciasDao.consultarConfiguraSet(500) + 
					" C:\\apicifrado\\cifradoEntrada\\" + nomArch + 
						" C:\\apicifrado\\cifradoSalida\\" + nomArch;
				if (ejecutaComando(cifraArchivo)) {
					return "Exito";
				}  else return "No se pudo encriptar el archivo";
			} else return "No se pudo crear respaldo";
		} else return "No se pudo copiar";
	}

	public boolean ejecutaComando(String comando) {
		try { 
		    System.out.println(comando);
		    Process p = Runtime.getRuntime().exec(comando); 
		    BufferedReader input = new BufferedReader (new InputStreamReader (p.getInputStream())); 
			while (input.readLine() != null) { 
			    System.out.println(input.readLine()); 
			} 
			input.close(); 
			return true;
		}catch (Exception err) { 
		    err.printStackTrace(); 
		    return false;
		}
	}
	
	public EnvioTransferenciasDao getEnvioTransferenciasDao() {
		return envioTransferenciasDao;
	}
	public void setEnvioTransferenciasDao(EnvioTransferenciasDao envioTransferenciasDao) {
		this.envioTransferenciasDao = envioTransferenciasDao;
	}
}
